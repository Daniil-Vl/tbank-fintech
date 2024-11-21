package kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 2)
@Measurement(iterations = 3)
public abstract class KafkaBenchmark {

    private static final String TOPIC_NAME = "test-topic";

    private List<KafkaProducer<String, String>> producers;
    private List<KafkaConsumer<String, String>> consumers;

    private final int producersNumber;
    private final int consumersNumber;

    public KafkaBenchmark(int producersNumber, int consumerNumber) {
        this.producersNumber = producersNumber;
        this.consumersNumber = consumerNumber;
    }

    private void setUpProducers() {
        producers = new ArrayList<>();
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        for (int i = 0; i < producersNumber; i++) {
            producers.add(new KafkaProducer<>(props));
        }
    }

    private void setUpConsumers() {
        consumers = new ArrayList<>();
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:29092");
        props.put("group.id", "test-group-id");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        for (int i = 0; i < consumersNumber; i++) {
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(List.of(TOPIC_NAME));
            consumers.add(consumer);
        }
    }

    @Setup(Level.Trial)
    public void setup() {
        setUpProducers();
        setUpConsumers();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        producers.forEach(KafkaProducer::close);
        consumers.forEach(KafkaConsumer::close);
    }

    @Benchmark
    public void test(Blackhole blackhole) {
        for (var producer : producers) {
            blackhole.consume(
                    producer.send(new ProducerRecord<>(TOPIC_NAME, "test key", "test message"))
            );
        }

        for (var consumer : consumers) {
            blackhole.consume(
                    consumer.poll(Duration.ofMillis(100))
            );
        }
    }

}
