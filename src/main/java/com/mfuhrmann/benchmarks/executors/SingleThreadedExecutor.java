package com.mfuhrmann.benchmarks.executors;

import com.mfuhrmann.benchmarks.data.OptionInputData;
import com.mfuhrmann.benchmarks.data.OptionPriceData;
import com.mfuhrmann.benchmarks.workloads.OperationStrategy;
import com.mfuhrmann.benchmarks.workloads.OperationType;

import java.util.List;

public class SingleThreadedExecutor implements CalculationExecutor{

    @Override
    public List<OptionPriceData> execute(List<OptionInputData> optionInputDataList, OperationType operationType) {

        return optionInputDataList.stream()
                .map(optionInputData ->
                        OperationStrategy.performOperation(optionInputData, operationType))
                .toList();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
