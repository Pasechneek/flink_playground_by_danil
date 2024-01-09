package org.example;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;

public class FinalExample {
    public static void main(String[] args) throws Exception {

//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("localhost", 3306, "flink-connector-mysql-cdc");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);
//
//        DataStream<SourceObject> my_first_stream = env.addSource(new SourceObjectSource());
//
//        DataStream<EnrichedItem> enrichedNYCRides = my_first_stream
//                .keyBy(KeySelector<> obj -> obj.id)
//                .filter(new SomeObject.NYCFilter())
//                .map(new Enrichment());
//    }
//}
//
//public static class Enrichment implements MapFunction<TaxiRide, EnrichedItem> {
//    /*
//    A MapFunction is suitable only when performing a one-to-one transformation:
//    for each and every stream element coming in, map() will emit one transformed element.
//    Otherwise, you will want to use flatmap()
//     */
//
//    @Override
//    public EnrichedItem map(SourceObject sourceObject) throws Exception {
//        return new EnrichedItem(sourceObject);
    }
}
