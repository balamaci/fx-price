package com.balamaci.fxprice;

import com.balamaci.fxprice.entity.Quote;
import com.balamaci.fxprice.generator.RandomQuoteGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import kafka.admin.AdminUtils;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static kafka.admin.RackAwareMode.Disabled$.MODULE$;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FxQuoteApplicationTests {

	private static final int EVENTS_TO_GENERATE = 20_000;

	private static final String BROKER_HOST = "127.0.0.1";
	private int brokerPort = 9092;

	@Value("${kafka_consumer.topic}")
	private String spotTopic;

	private ObjectWriter jsonWriter = new ObjectMapper().writer();

	private static final Logger log = LoggerFactory
			.getLogger(FxQuoteApplicationTests.class);

	private KafkaServerHolder createKafkaCluster() throws IOException {
		EmbeddedZookeeper zkServer = new EmbeddedZookeeper();
		String zkConnectUrl = "localhost:" + zkServer.port();
		ZkClient zkClient = new ZkClient(zkConnectUrl, 30000, 30000,
				ZKStringSerializer$.MODULE$);

		KafkaConfig config = new KafkaConfig(props(
				"zookeeper.connect", zkConnectUrl,
				"broker.id", "0",
				"log.dirs", Files.createTempDirectory("kafka-").toAbsolutePath().toString(),
				"offsets.topic.replication.factor", "1",
				"listeners", "PLAINTEXT://" + BROKER_HOST + ":" + brokerPort));
		Time time = new SystemTime();

		KafkaServer kafkaServer = TestUtils.createServer(config, time);
		ZkUtils zkUtils = ZkUtils.apply(zkClient, false);

		return new KafkaServerHolder(kafkaServer, zkUtils);
	}

	@Test
	public void generatePricesKafka() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		KafkaServerHolder kafkaServerHolder = createKafkaCluster();

		createTopic(kafkaServerHolder.zkUtils, spotTopic, 1);
		KafkaProducer<Integer, String> kafkaProducer = createProducer();

		generateAndPushRandomQuotes(kafkaProducer);

		latch.await();
	}

	private static Properties props(String... kvs) {
		final Properties props = new Properties();
		for (int i = 0; i < kvs.length;) {
			props.setProperty(kvs[i++], kvs[i++]);
		}
		return props;
	}


	private void createTopic(ZkUtils zkUtils, String topicId, int partitionCount) {
		AdminUtils.createTopic(zkUtils, topicId, partitionCount, 1, new Properties(), MODULE$);
	}

	private KafkaProducer<Integer, String> createProducer() {
		Properties producerProps = new Properties();
		producerProps.setProperty("bootstrap.servers", BROKER_HOST + ':' + brokerPort);
		producerProps.setProperty("key.serializer", IntegerSerializer.class.getCanonicalName());
		producerProps.setProperty("value.serializer", StringSerializer.class.getCanonicalName());
		return new KafkaProducer<>(producerProps);
	}

	private void generateAndPushRandomQuotes(KafkaProducer<Integer, String> kafkaProducer) {
		RandomQuoteGenerator randomQuoteGenerator = new RandomQuoteGenerator();
		log.info("Started generating and pushing events to Kafka...");

		try {
			for(int i=1; i <= EVENTS_TO_GENERATE; i++) {
				if(i % 1000 == 0) {
					log.info("Generated {} quotes", i);
				}

				Quote quote = randomQuoteGenerator.generate();

				Future<RecordMetadata> responseFuture = kafkaProducer.
						send(new ProducerRecord<>(spotTopic, i, jsonWriter.writeValueAsString(quote)));
				responseFuture.get();
			}
		} catch (Exception e) {
			log.error("Exception encountered", e);
		}
	}

	private class KafkaServerHolder {
		KafkaServer kafkaServer;
		ZkUtils zkUtils;

		KafkaServerHolder(KafkaServer kafkaServer, ZkUtils zkUtils) {
			this.kafkaServer = kafkaServer;
			this.zkUtils = zkUtils;
		}
	}
}
