package com.mfuhrmann.benchmarks;

import com.google.common.base.Stopwatch;
import com.mfuhrmann.benchmarks.data.OptionPriceData;
import com.mfuhrmann.benchmarks.data.SyntheticDataGenerator;
import com.mfuhrmann.benchmarks.executors.ThreadPerCoreExecutor;
import com.mfuhrmann.benchmarks.executors.CachedPhysicalThreadPoolExecutor;
import com.mfuhrmann.benchmarks.executors.CachedVirtualThreadPoolExecutor;
import com.mfuhrmann.benchmarks.executors.CalculationExecutor;
import com.mfuhrmann.benchmarks.executors.PhysicalThreadPerTaskExecutor;
import com.mfuhrmann.benchmarks.executors.VirtualThreadsPerTaskExecutor;
import com.mfuhrmann.benchmarks.workloads.OperationType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.mfuhrmann.benchmarks.workloads.OperationType.CPU_HEAVY;
import static com.mfuhrmann.benchmarks.workloads.OperationType.MIXED;
import static com.mfuhrmann.benchmarks.workloads.OperationType.NETWORK;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class JavaVolatilityCalculationBenchmarkMain {

    public static final double MAX_STOCK_PRICe = 250.0;
    public static final double MIN_STOCK_PRICE = 50.0;
    public static final int NUM_SAMPLES = 200_000;

    public static void main(String[] args) throws IOException {

        var optionInputData = SyntheticDataGenerator.generateSyntheticData(NUM_SAMPLES, MIN_STOCK_PRICE, MAX_STOCK_PRICe);

        Stopwatch stopwatch1 = Stopwatch.createStarted();
        Arrays.stream(OperationType.values())
//                .filter(operationType -> Set.of(CPU_HEAVY).contains(operationType))
                .forEach(operationType -> {
                    getExecutors(optionInputData.size()).forEach(executor -> {
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        var optionPriceResponse = executor.execute(optionInputData, operationType);
                        System.out.println("Operation: " + operationType + " - Executor - " + executor + " Time taken: " + stopwatch.elapsed(MILLISECONDS) + " ms" + "    consume output: " + optionPriceResponse.size());
//            printResultsSnapshot(optionPriceResponse);
                    });

                });
    }

    private static List<CalculationExecutor> getExecutors(int dataSize) {
        //
        //
        return List.of(
                new VirtualThreadsPerTaskExecutor(16),
                new VirtualThreadsPerTaskExecutor(64),
                new VirtualThreadsPerTaskExecutor(1024),
                new VirtualThreadsPerTaskExecutor(dataSize),
                //
                new PhysicalThreadPerTaskExecutor(16),
                new PhysicalThreadPerTaskExecutor(64),
                new PhysicalThreadPerTaskExecutor(1024),
                new PhysicalThreadPerTaskExecutor(dataSize),
                //
                new ThreadPerCoreExecutor(16),
                new ThreadPerCoreExecutor(64),
                new ThreadPerCoreExecutor(1024),
                new ThreadPerCoreExecutor(dataSize),
                //
                new CachedPhysicalThreadPoolExecutor(16),
                new CachedPhysicalThreadPoolExecutor(64),
                new CachedPhysicalThreadPoolExecutor(1024),
                new CachedPhysicalThreadPoolExecutor(dataSize),
                //
                new CachedVirtualThreadPoolExecutor(16),
                new CachedVirtualThreadPoolExecutor(64),
                new CachedVirtualThreadPoolExecutor(1024),
                new CachedVirtualThreadPoolExecutor(dataSize)
                //
//                new SingleThreadedExecutor()
                //
        );
    }

    private static void printResultsSnapshot(List<OptionPriceData> optionPriceResponse) {
        var fromIndex = ThreadLocalRandom.current().nextInt(optionPriceResponse.size());
        for (OptionPriceData data : optionPriceResponse.subList(fromIndex, fromIndex + 10)) {
            System.out.printf(" %-15.2f %-15.2f\n",
                    data.callPrice(), data.putPrice());
        }
    }


}
