FROM openjdk:11

WORKDIR /flink-job
COPY . /flink-job

RUN ["chmod", "+x", "run_job.sh"]
CMD ["/flink-job/run_job.sh"]


# Step 1: Download Apache Maven
RUN wget https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz && tar xzvf apache-maven-3.8.8-bin.tar.gz apache-maven-3.8.8 && rm apache-maven-3.8.8-bin.tar.gz && mkdir /opt/maven

# Step 2: Move to the Flink job directory
WORKDIR /flink-job/apache-maven-3.8.8

# Step 3: Move all files and directories to the Maven directory
RUN mv ./* /opt/maven

# Step 4: Update the PATH environment variable
ENV PATH /opt/maven/bin:$PATH

WORKDIR /flink-job

RUN mvn clean && mvn compile