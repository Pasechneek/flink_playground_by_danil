package org.example;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;
import org.example.DebugPrint.*;

public class Example {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);

        DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 35),
                new Person("Pebbles", 2));

//        List<Person> people = new ArrayList<Person>();
//        people.add(new Person("Fred", 35));
//        people.add(new Person("Wilma", 35));
//        people.add(new Person("Pebbles", 2));
//        DataStream<Person> flintstones = env.fromCollection(people);

        DataStream<Person> adults = flintstones.filter(new FilterFunction<Person>() {
            @Override
            public boolean filter(Person person) throws Exception {
                return person.age >= 18;
            }
        });

//        adults.print();
        DebugPrint.deprint(adults.toString(), "Adults");

        final JobClient jobClient = env.executeAsync();
        final JobExecutionResult jobExecutionResult = jobClient.getJobExecutionResult().get();

        DebugPrint.deprint(jobExecutionResult.toString(), "job result");
//        OR
//        env.execute();
    }

    public static class Person {
        public String name;
        public Integer age;
        public Person() {}

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
