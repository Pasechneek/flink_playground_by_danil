package org.example;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.map.StringToPOJOMap;
import org.model.Application;

public class TopicTopicJob {
    public static void main(String[] args) throws Exception {
        String broker = "localhost:9092";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(broker)
                .setTopics("caught-applications")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> dataStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(1)
                .name("data stream from Topic");

        DataStream<Application> mappedDataStream = dataStream.map(new MapFunction<>() {
            @Override
            public Application map(String someString) throws Exception {
                StringToPOJOMap myMap = new StringToPOJOMap();
                return myMap.map(someString);
            }
        });

        KeyedStream<Application, Long> keyedStream = mappedDataStream
                .keyBy(Application::getUcdbId);

//
//        keyedStream
//                .sum(Application::getRequestedAmount)
//                .print();


        KafkaSink<String> someSink = KafkaSink.<String>builder()
                .setBootstrapServers(broker)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("processed-applications")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
//            .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
//            .setTransactionalIdPrefix("my-record-producer")
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        dataStream
                .sinkTo(someSink);

        env.execute("From topic to topic");
    }
}
