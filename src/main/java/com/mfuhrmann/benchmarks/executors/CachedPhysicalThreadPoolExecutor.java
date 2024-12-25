package com.mfuhrmann.benchmarks.executors;

import com.mfuhrmann.benchmarks.data.OptionInputData;
import com.mfuhrmann.benchmarks.data.OptionPriceData;
import com.mfuhrmann.benchmarks.workloads.OperationType;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedPhysicalThreadPoolExecutor implements CalculationExecutor {

    private final ExecutorBasedThreadsExecutor calculationExecutor;
    private final int tasks;

    public CachedPhysicalThreadPoolExecutor(int tasks) {
        this.tasks = tasks;
        ExecutorService executorService = Executors.newCachedThreadPool();
        this.calculationExecutor = new ExecutorBasedThreadsExecutor(tasks, executorService);
    }

    @Override
    public List<OptionPriceData> execute(List<OptionInputData> optionInputDataList, OperationType operationType) {
        return calculationExecutor.execute(optionInputDataList,operationType);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
                "tasks=" + tasks +
                '}';
    }
}
