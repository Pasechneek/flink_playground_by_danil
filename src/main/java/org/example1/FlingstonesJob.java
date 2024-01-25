package org.example1;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;
import org.debug.print.DebugPrint;

public class FlingstonesJob {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ExecutionConfig executionConfig = env.getConfig();

        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);

        DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Agata", 1),
                new Person("Danil", 31),
                new Person("Alexey", 23),
                new Person("Yana", 25),
                new Person("Bob", 20),
                new Person("Alice", 20),
                new Person("Thomas", 44),
                new Person("Pebbles", 2)
        );

        DataStream<Person> data_stream_adults = flintstones.filter(new FilterFunction<Person>() {
            @Override
            public boolean filter(Person person) throws Exception {
                return person.age >= 18;
            }
        });

        data_stream_adults.print();

        var jobExecutionResult = env.execute();
        DebugPrint.deprint(jobExecutionResult.toString(), "job result");
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
