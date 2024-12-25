package com.mfuhrmann.benchmarks.data;

public record OptionInputData(
        double stockPrice,
        double strikePrice,
        double timeToMaturity,
        double riskFreeRate,
        double volatility
) {
}
