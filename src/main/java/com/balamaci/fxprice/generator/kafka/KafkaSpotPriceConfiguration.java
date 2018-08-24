package com.balamaci.fxprice.generator.kafka;

import com.balamaci.fxprice.entity.Quote;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collections;
import java.util.List;

@Configuration
//@ConditionalOnProperty(value = "generator", havingValue = "kafka")
public class KafkaSpotPriceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KafkaSpotPriceConfiguration.class);

    @Autowired
    private KafkaConsumer<Integer, String> consumer;

    @Value("${kafka_consumer.topic}")
    private String kafkaTopic;

    private ObjectReader jsonReader = new ObjectMapper().reader();

    @Bean
    public Flux<Quote> generateSpotPrices() {
        return Flux.<Quote>push(this::receiveSpotPrices);
    }


    private void receiveSpotPrices(FluxSink<Quote> sink) {
        try {
            consumer.subscribe(Collections.singletonList(kafkaTopic));
            log.info("KafkaConsumer subscribed");

            while (true) {
                if(sink.isCancelled()) {
                    return;
                }

                log.debug("Consumer polling ...");
                ConsumerRecords<Integer, String> records = consumer.poll(Long.MAX_VALUE);
                log.debug("Received {} records", records.count());

                for (TopicPartition topicPartition : records.partitions()) {
                    List<ConsumerRecord<Integer, String>> topicRecords = records.records(topicPartition);

                    for (ConsumerRecord<Integer, String> record : topicRecords) {
                        log.debug("Consumer:Topic:{} => Partition={}, Offset={}, EventTime:[{}] Val={}",
                                topicPartition.topic(), record.partition(), record.offset(),
                                record.timestamp(), record.value());

                        Quote quote = jsonReader.forType(Quote.class).readValue(record.value());

                        sink.next(quote);
                    }

                    long lastPartitionOffset = topicRecords.get(topicRecords.size() - 1).offset();
                    consumer.commitSync(Collections.singletonMap(topicPartition,
                            new OffsetAndMetadata(lastPartitionOffset + 1)));
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } catch (Exception e) {
            log.error("KafkaConsumer encountered error", e);
        } finally {
            consumer.close();
        }
    }


}
