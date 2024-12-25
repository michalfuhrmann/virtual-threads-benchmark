package com.mfuhrmann.benchmarks.executors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.mfuhrmann.benchmarks.data.OptionInputData;
import com.mfuhrmann.benchmarks.data.OptionPriceData;
import com.mfuhrmann.benchmarks.workloads.OperationStrategy;
import com.mfuhrmann.benchmarks.workloads.OperationType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ExecutorBasedThreadsExecutor implements CalculationExecutor {


    private final int tasks;
    private final ExecutorService executorService;

    public ExecutorBasedThreadsExecutor(int tasks, ExecutorService executorService) {
        this.tasks = tasks;
        this.executorService = executorService;
    }

    public List<OptionPriceData> execute(List<OptionInputData> optionInputDataList, OperationType operationType) {

        var collect = Lists.partition(optionInputDataList, optionInputDataList.size() / tasks).stream().
                map(batch -> executorService.submit(() -> {
//                            System.out.println("started batch of size " + batch.size());
                            return batch.stream()
                                    .map(optionInputData -> OperationStrategy.performOperation(optionInputData, operationType))
                                    .toList();
                        })
                ).toList();

        executorService.shutdown();
        return collect.stream()
                .flatMap(future -> unwrapFuture(future).stream())
                .toList();

    }

    private static List<OptionPriceData> unwrapFuture(Future<List<OptionPriceData>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
