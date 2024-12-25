package com.mfuhrmann.benchmarks.calculation;

import com.mfuhrmann.benchmarks.data.OptionInputData;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

public class BsPriceCalculator {
    enum OptionType {
        CALL,
        PUT
    }
    public static void main(String[] args) {
        double marketPrice = 10.0;
        double S = 100.0;
        double K = 100.0;
        double T = 1.0;
        double r = 0.05;

        BrentSolver solver = new BrentSolver(1e-5, 1e-10);
        UnivariateFunction bsFunction = (sigma) -> blackScholesCall(S, K, T, r, sigma) - marketPrice;

        double iv = solver.solve(100, bsFunction, 0.01, 1.0); // Bounds: 0.01 to 1.0
        System.out.printf("Implied Volatility: %.5f\n", iv);
    }

    public static double blackScholesIv(double S, double K, double T, double r, double sigma,OptionType optionType) {
        switch (optionType) {
            case CALL:
                return blackScholesCall(S, K, T, r, sigma);
            case PUT:
                return blackScholesPut(S, K, T, r, sigma);
            default:
                throw new IllegalArgumentException("Invalid option type");
        }
    }

    public static double callPriceCalc(OptionInputData optionInputData) {
        return blackScholesCall(optionInputData.stockPrice(), optionInputData.strikePrice(), optionInputData.timeToMaturity(), optionInputData.riskFreeRate(), optionInputData.volatility());
    }

    public static double putPriceCalc(OptionInputData optionInputData) {
        return blackScholesCall(optionInputData.stockPrice(), optionInputData.strikePrice(), optionInputData.timeToMaturity(), optionInputData.riskFreeRate(), optionInputData.volatility());
    }

    public static double blackScholesCall(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        return S * cdf(d1) - K * Math.exp(-r * T) * cdf(d2);
    }

    public static double blackScholesPut(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        return K * Math.exp(-r * T) * cdf(-d2) - S * cdf(-d1);
    }

    private static double cdf(double x) {
        return 0.5 * (1.0 + org.apache.commons.math3.special.Erf.erf(x / Math.sqrt(2.0)));
    }



}
