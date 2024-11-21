package rabbit;

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
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 2)
@Measurement(iterations = 3)
public abstract class RabbitBenchmark {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "rmuser";
    private static final String PASSWORD = "rmpassword";

    private List<RabbitProducer> producers;
    private List<RabbitConsumer> consumers;

    private final int producersNumber;
    private final int consumersNumber;

    public RabbitBenchmark(int producersNumber, int consumerNumber) {
        this.producersNumber = producersNumber;
        this.consumersNumber = consumerNumber;
    }

    private void setUpProducers() throws IOException, TimeoutException {
        producers = new ArrayList<>();
        for (int i = 0; i < producersNumber; i++) {
            producers.add(new RabbitProducer(HOST, PORT, USERNAME, PASSWORD));
        }
    }

    private void setUpConsumers() throws IOException, TimeoutException {
        consumers = new ArrayList<>();

        for (int i = 0; i < consumersNumber; i++) {
            consumers.add(new RabbitConsumer(HOST, PORT, USERNAME, PASSWORD));
        }
    }

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        setUpProducers();
        setUpConsumers();
    }

    @Benchmark
    public void test(Blackhole blackhole) throws IOException {
        for (var producer : producers) {
            blackhole.consume(
                    producer.send("test message")
            );
        }

        for (var consumer : consumers) {
            blackhole.consume(
                    consumer.consume()
            );
        }
    }

}
