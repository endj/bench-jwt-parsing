package bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


public class Test {

    @State(Scope.Thread)
    public static class TestState {
        private final String JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void naive(TestState testState, Blackhole blackhole) {
        blackhole.consume(JwtParser.naive(testState.JWT));
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void parseNameFromJWTErrorHandling(TestState testState, Blackhole blackhole) {
        blackhole.consume(JwtParser.parseNameFromJWTErrorHandling(testState.JWT));
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void nonThreadSafeBufferReuse(TestState testState, Blackhole blackhole) {
        blackhole.consume(JwtParser.nonThreadSafeBufferReuse(testState.JWT));
    }


    public static void main(String[] args) throws RunnerException {
        Options build = new OptionsBuilder()
                .include(Test.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(3))
                .measurementTime(TimeValue.seconds(10))
                .forks(1)
                .build();

        new Runner(build).run();
    }
}

