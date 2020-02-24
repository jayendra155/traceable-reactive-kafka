package com.kafka;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import brave.Tracer;
import brave.Tracing;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

/**
 * @author jayendravikramsingh
 *         <p>
 *         <p>
 *         20/02/20
 */
@RestController
public class ProducerController {
    
    @Autowired
    Tracer tracer;
    
    @Autowired
    Tracing tracing;
    
    @Autowired
    BeanFactory beanFactory;
    
    @Autowired
    KafkaSender<String, String> producer;
   static  Logger log = LoggerFactory.getLogger(ProducerController.class);
    @PostMapping("/send")
    public ResponseEntity<RecordMetadata> writeData(@RequestBody Record record)
    throws ExecutionException, InterruptedException {
        
        //        long l1 = tracer.currentSpan().context().traceId();
        //        long l = tracer.currentSpan().context().parentIdAsLong();
        //        Header trace = new RecordHeader("X-B3-TraceId", tracer.currentSpan().context().traceIdString()
        //        .getBytes());
        //        Header parent = new RecordHeader("X-B3-ParentId", tracer.currentSpan().context().traceIdString()
        //        .getBytes());
        
        producer.send(Flux.just(
                SenderRecord.create(record.topic, null, System.currentTimeMillis(), record.key, record.value, null)))
                .publishOn(Schedulers.fromExecutor(new TraceableExecutorService(beanFactory,
                                                                                Executors.newFixedThreadPool(1))))
                .doOnNext(c-> log.info("Record sent"))
                .doOnError(e -> log.info("Error {}", e.getMessage(), e))
                .publishOn(Schedulers.immediate())
                .subscribe();
        
        return ResponseEntity.ok(null);
    }
    
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    
    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
    
    public static class Record {
        public String value;
        public String key;
        public String topic;
        public String broker;
    }
}
