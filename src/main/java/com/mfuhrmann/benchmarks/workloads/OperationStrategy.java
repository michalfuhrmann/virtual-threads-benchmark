package com.mfuhrmann.benchmarks.workloads;

import com.mfuhrmann.benchmarks.calculation.BsPriceCalculator;
import com.mfuhrmann.benchmarks.data.OptionInputData;
import com.mfuhrmann.benchmarks.data.OptionPriceData;
import com.sun.nio.file.ExtendedOpenOption;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;


public class OperationStrategy {

    public static final int SIMULATED_TIMEOUT = 10;

    public static OptionPriceData performOperation(OptionInputData inputData, OperationType operationType) {
        switch (operationType) {
            case CPU_HEAVY -> {
                return cpu(inputData);
            }
            case MIXED -> {
                return mixed(inputData);
            }
            case NETWORK -> {
                return network(inputData);
            }
            case SIMULATED_SLEEP -> {
                return simulatedSleep(inputData);
            }
            case DISK -> {
                return disk(inputData);
            }
            case NON_BLOCKING_DISK -> {
                return nonBlockingDisk(inputData);
            }
            default -> throw new IllegalArgumentException("Operation type not supported");
        }
    }


    private static OptionPriceData simulatedSleep(OptionInputData inputData) {

        try{
            Thread.sleep(SIMULATED_TIMEOUT);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new OptionPriceData(inputData.volatility(), inputData.volatility());
    }

    private static int sendHttpRequest() {
        try {
            String serverUrl = "http://localhost:8080/test";

            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            var responseCode = connection.getResponseCode();// Wait for the response
            connection.disconnect();
            return responseCode;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static OptionPriceData network(OptionInputData inputData) {
        var responseCode = sendHttpRequest();
        return new OptionPriceData(responseCode, inputData.volatility());
    }

    static OptionPriceData cpu(OptionInputData optionInputData) {
        var callPrice = BsPriceCalculator.callPriceCalc(optionInputData);
        var putPrice = BsPriceCalculator.putPriceCalc(optionInputData);
        return new OptionPriceData(callPrice, putPrice);
    }

    public static String readFileContent(Path filePath) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ, ExtendedOpenOption.DIRECT)) {
            ByteBuffer buffer = ByteBuffer.allocate(4096);

            Future<Integer> future = fileChannel.read(buffer, 0);

            future.get();

            buffer.flip();
            return StandardCharsets.UTF_8.decode(buffer).toString();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    static OptionPriceData nonBlockingDisk(OptionInputData optionInputData) {

        var path = Path.of("src\\main\\resources\\io-benchmark-13929518615495663013.txt");
        var data = readFileContent(path);
        return new OptionPriceData(optionInputData.volatility(), data.length());
    }


    static OptionPriceData disk(OptionInputData optionInputData) {

        try {
            // Simulate a 10 mb read operation
            var path = Path.of("src\\main\\resources\\io-benchmark-13929518615495663013.txt");
            var data = Files.readAllBytes(path);
            return new OptionPriceData(optionInputData.volatility(), data.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static OptionPriceData mixed(OptionInputData optionInputData) {

        var cpu = cpu(optionInputData);
        var disk = disk(optionInputData);
        var sleep = simulatedSleep(optionInputData);
        var nonBlockingDisk = nonBlockingDisk(optionInputData);
        return new OptionPriceData(cpu.callPrice() + sleep.putPrice(), disk.putPrice() + nonBlockingDisk.callPrice());
    }
}
