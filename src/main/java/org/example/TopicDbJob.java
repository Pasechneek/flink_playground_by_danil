package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.example.map.StringToObjectMap;
import org.model.Application;

//import static jdk.nashorn.internal.objects.NativeJava.type;

public class TopicDbJob {

    public static void main(String[] args) throws Exception {
        String broker = System.getenv("KAFKA_HOST") + ":9092";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(broker)
                .setTopics("caught-applications")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> dataStream = env
                .fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(1)
                .name("data stream from Topic");

        DataStream<Application> mappedDataStream = dataStream
                .map(new MapFunction<>() {
                    @Override
                    public Application map(String stringThatContainJson) throws Exception {
                        StringToObjectMap myMap = new StringToObjectMap();
                        return myMap.map(stringThatContainJson);
                    }
                });

        JdbcExactlyOnceOptions.builder()
                .withTransactionPerConnection(true)
                .build();


        String query = "INSERT into Application_2 (id, ucdb_id, requested_amount, product) VALUES (?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = ?, ucdb_id = ?, requested_amount = ?, product = ?;";


        var jdbcExecutionOptions = JdbcExecutionOptions.builder()
                .withBatchIntervalMs(200)
                .withBatchSize(1000)
                .withMaxRetries(5)
                .build();

        var jdbcConnectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl("jdbc:mariadb://" + System.getenv("EXAMPLE_HOST") + ":3307/" + System.getenv("EXAMPLE_DB"))
                .withDriverName("org.mariadb.jdbc.Driver")
                .withUsername(System.getenv("EXAMPLE_USER"))
                .withPassword(System.getenv("EXAMPLE_PASSWORD"))
                .build();

        mappedDataStream.addSink(
                JdbcSink
                        .sink(
                                query,
                                (preparedStatement, variable) -> {
                                    preparedStatement.setLong(1, variable.getId());
                                    preparedStatement.setLong(2, variable.getUcdbId());
                                    preparedStatement.setFloat(3, variable.getRequestedAmount());
                                    preparedStatement.setString(4, variable.getProduct());
                                    preparedStatement.setLong(5, variable.getId());
                                    preparedStatement.setLong(6, variable.getUcdbId());
                                    preparedStatement.setFloat(7, variable.getRequestedAmount());
                                    preparedStatement.setString(8, variable.getProduct());
                                },
                                jdbcExecutionOptions,
                                jdbcConnectionOptions
                        )
        );

        env.execute("From topic to db");
    }
}
