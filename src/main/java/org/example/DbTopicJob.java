package org.example;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.config.ServerIdRange;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

import javax.management.timer.Timer;


public class DbTopicJob {
    public static void main(String[] args) throws Exception {

        MySqlSource<String> mySQLSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db_example")
                .tableList("db_example.Application")
                .username("user")
                .password("password")
                .serverId(String.valueOf(new ServerIdRange(5401, 5404)))
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(Timer.ONE_MINUTE);

        final ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);

        DataStream<String> dataStream = env.fromSource(mySQLSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                .setParallelism(4)
                .name("data stream from, MySQL");

        dataStream.print();

        String broker = "localhost:9092";

        KafkaSink<String> sinkApplications = KafkaSink.<String>builder()
                .setBootstrapServers(broker)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("caught-applications")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                        )
//                .setTransactionalIdPrefix("my-record-producer")
//                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        dataStream
                .sinkTo(sinkApplications)
                .setParallelism(1);

        env.execute("Print MySQL Snapshot + Binlog");
    }
}
