package com.mfuhrmann.benchmarks.data;

import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class SyntheticDataGenerator {

    // Black-Scholes call option pricing formula
    public static double blackScholesCall(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        return S * cdf(d1) - K * Math.exp(-r * T) * cdf(d2);
    }

    // Black-Scholes put option pricing formula
    public static double blackScholesPut(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        return K * Math.exp(-r * T) * cdf(-d2) - S * cdf(-d1);
    }

    // Cumulative distribution function for standard normal distribution
    private static double cdf(double x) {
        return 0.5 * (1.0 + erf(x / Math.sqrt(2.0)));
    }

    // Error function approximation
    private static double erf(double x) {
        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;

        double sign = (x < 0) ? -1 : 1;
        x = Math.abs(x);

        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

        return sign * y;
    }

    // Generate synthetic options data
    public static List<OptionInputData> generateSyntheticData(int numSamples, double minPrice, double maxPrice) {
        Random random = new Random();

        // Strike price (around stock price)
        // Time to maturity (0.1 to 2 years)
        // Risk-free rate (1% to 10%)
        // Volatility (10% to 50%)

        return random.doubles(numSamples, minPrice, maxPrice)
                .mapToObj(S -> {
                    double K = S + random.nextDouble() * 20 - 10; // Strike price (around stock price)
                    double T = 0.1 + random.nextDouble() * 2.0;   // Time to maturity (0.1 to 2 years)
                    double r = 0.01 + random.nextDouble() * 0.1;  // Risk-free rate (1% to 10%)
                    double sigma = 0.1 + random.nextDouble() * 0.4; // Volatility (10% to 50%)
                    return new OptionInputData(S, K, T, r, sigma);
                })
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        // Generate synthetic data
        int numSamples = 1_000_000; // Number of synthetic data points
        double minPrice = 50.0; // Minimum stock price
        double maxPrice = 250.0; // Maximum stock price

        List<OptionInputData> syntheticData = generateSyntheticData(numSamples, minPrice, maxPrice);

        Stopwatch stopwatch = Stopwatch.createStarted();
        // Calculate call and put option prices


        System.out.println(stopwatch.elapsed(MILLISECONDS));

        // Print the generated data
        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-15s %-15s\n",
                "Stock", "Strike", "Time", "Rate", "Volatility", "Call Price", "Put Price");

    }
}

