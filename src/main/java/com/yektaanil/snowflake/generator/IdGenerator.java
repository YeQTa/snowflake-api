package com.yektaanil.snowflake.generator;

import java.time.Instant;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author : Yekta Anil AKSOY
 * @since : 4.12.2021
 **/
public class IdGenerator {
    private static volatile IdGenerator idGenerator;

    private static final long SNOWFLAKE_EPOCH = 1597600800000L; // Sunday, 16 August 2020 21:00:00 GMT+03.00
    private static final int DATACENTER_ID_BITS = 5;
    private static final int WORKER_ID_BITS = 5;
    private static final int SEQUENCE_BITS = 12;

    private static final int MAX_WORKER_ID = -1 ^ (-1 << WORKER_ID_BITS);
    private static final int MAX_DATACENTER_ID = -1 ^ (-1 << DATACENTER_ID_BITS);
    private static final int MAX_SEQUENCE = -1 ^ (-1 << SEQUENCE_BITS);

    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    @Value("${workerId}")
    private int workerId;

    @Value("${datacenterId}")
    private int datacenterId;
    private IdGenerator(){
        if(!Objects.isNull(idGenerator)){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        if(workerId < 0 || workerId > MAX_WORKER_ID){
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",MAX_WORKER_ID));
        }
        if(datacenterId < 0 || datacenterId > MAX_DATACENTER_ID){
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",MAX_DATACENTER_ID));
        }
    }
    public static IdGenerator getInstance(){
        //Double check locking pattern
        if(Objects.isNull(idGenerator)){
            synchronized (IdGenerator.class){
                if(Objects.isNull(idGenerator)){
                    idGenerator = new IdGenerator();
                }
            }
        }
        return idGenerator;
    }

    public synchronized long nextId(){
        long timestamp = timeGen();
        if(timestamp < lastTimestamp){
            throw new IllegalStateException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",lastTimestamp - timestamp));
        }

        if(lastTimestamp == timestamp){
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if(sequence == 0){
                timestamp = tillNextMillis(timestamp);
            }
        }
        else{
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - SNOWFLAKE_EPOCH) << TIMESTAMP_SHIFT |
                (datacenterId << DATACENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence);
    }

    private static long timeGen() {
        return Instant.now().toEpochMilli();
    }

    private long tillNextMillis(long timestamp){
        while(timestamp <= lastTimestamp){
            timestamp = timeGen();
        }
        return timestamp;
    }
}
