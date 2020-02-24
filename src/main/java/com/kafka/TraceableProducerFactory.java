package com.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.ProducerFactory;

/**
 * @author jayendravikramsingh
 *         <p>
 *         <p>
 *         21/02/20
 */
public class TraceableProducerFactory extends ProducerFactory {
    
    private Tracing tracing;
    
    private KafkaTracing kafkaTracing;
    
    public static final TraceableProducerFactory INSTANCE = new TraceableProducerFactory();
    
    protected TraceableProducerFactory() {
        super();
    }
    
    public <K, V> Producer<K, V> createProducer(SenderOptions<K, V> senderOptions) {
        Producer<K, V> producer = super.createProducer(senderOptions);
        return this.kafkaTracing.producer(producer);
    }
    
    public void setTracing(Tracing tracing) {
        this.tracing = tracing;
    }
    
    public void setKafkaTracing(KafkaTracing kafkaTracing) {
        this.kafkaTracing = kafkaTracing;
        setTracing(kafkaTracing.messagingTracing().tracing());
    }
}
