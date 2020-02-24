package com.kafka;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

/**
 * @author jayendravikramsingh
 *         <p>
 *         <p>
 *         20/02/20
 */
@SpringBootApplication
public class ProducerApplication {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProducerApplication.class);
        app.setBanner(new WorkerBanner());
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.run(ProducerApplication.class);
    }
    
    @Bean
    KafkaTracing kafkaTracing(Tracing tracing) {
        return KafkaTracing.create(tracing);
    }
    
    private Map props() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return producerProps;
    }
    
    @Bean
    public SenderOptions<String, String> senderOptions() {
        return SenderOptions.<String, String>create(props()).toImmutable();
    }
    
    @Bean
    KafkaSender<String, String> producer(KafkaTracing kafkaTracing, SenderOptions<String, String> senderOptions) {
        TraceableProducerFactory obj = TraceableProducerFactory.INSTANCE;
        obj.setKafkaTracing(kafkaTracing);
        return KafkaSender.create(obj, senderOptions);
        
    }
}
