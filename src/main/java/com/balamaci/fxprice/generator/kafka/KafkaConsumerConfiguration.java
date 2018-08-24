package com.balamaci.fxprice.generator.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author sbalamaci
 */
@Configuration
@ConditionalOnProperty(value = "generator", havingValue = "kafka")
public class KafkaConsumerConfiguration {

    @Value("${kafka_consumer.bootstrap_servers}")
    private String kafkaServers;

    @Value("${kafka_consumer.consumer_id}")
    private String consumerId;

    @Bean
    public KafkaConsumer<Integer, String> createKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        //How many records to retrieve in a poll() invocation even if there are more available
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "8");

        //Heartbeat that the Kafka coordinator expects to be sent by the consumer
        //should not have calls between poll() method take longer than this
        //on the other hand it means it takes longer for the coordinator to declare the consumer dead
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "35000");

        return new KafkaConsumer<>(props);
    }

}
