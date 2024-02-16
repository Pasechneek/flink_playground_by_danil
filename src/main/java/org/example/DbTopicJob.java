package org.example;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.config.ServerIdRange;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.debug.print.DebugPrint;

import javax.management.timer.Timer;
import java.sql.Connection;
import java.sql.DriverManager;

public class DbTopicJob {
    public static void main(String[] args) throws Exception {

//        DebugPrint.deprint(
//                DriverManager.getConnection("jdbc:mysql://localhost/example?user=hello&password=hello").toString()
//                );

        MySqlSource<String> mySQLSource = MySqlSource.<String>builder()
                .hostname(System.getenv("EXAMPLE_HOST"))
                .port(3307)
                .databaseList(System.getenv("EXAMPLE_DB"))
                .tableList("example.Application")
                .username(System.getenv("EXAMPLE_USER"))
                .password(System.getenv("EXAMPLE_PASSWORD"))
                .serverId(String.valueOf(new ServerIdRange(5401, 5404)))
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

//        DebugPrint.deprint(System.getenv("EXAMPLE_HOST"), "EXAMPLE_HOST");
//        DebugPrint.deprint(System.getenv("EXAMPLE_DB"), "EXAMPLE_DB");
//        DebugPrint.deprint(System.getenv("EXAMPLE_USER"), "EXAMPLE_USER");
//        DebugPrint.deprint(System.getenv("EXAMPLE_PASSWORD"), "EXAMPLE_PASSWORD");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(Timer.ONE_MINUTE);
        env.setParallelism(1);

        final ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);

        DataStream<String> dataStream = env.fromSource(mySQLSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                .setParallelism(1) // mandatory
                .name("data stream from, MySQL");

        dataStream
                .print();

        String broker = System.getenv("KAFKA_HOST") + ":9092";

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
                .name("Kafka Sink")
                .setParallelism(1);

        DebugPrint.deprint(dataStream.print().toString());

        env.execute("Print MySQL Snapshot + Binlog");
    }
}
