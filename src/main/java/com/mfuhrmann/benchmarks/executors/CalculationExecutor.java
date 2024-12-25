package com.mfuhrmann.benchmarks.executors;

import com.mfuhrmann.benchmarks.data.OptionInputData;
import com.mfuhrmann.benchmarks.data.OptionPriceData;
import com.mfuhrmann.benchmarks.workloads.OperationType;

import java.util.List;

public interface CalculationExecutor {

    List<OptionPriceData> execute(List<OptionInputData> optionInputDataList, OperationType operationType);
}
