package com.progressoft.samples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Money {
    public static final Money Zero = new Money(0);
    public static final Money OnePiaster = new Money(0.01);
    public static final Money FivePiasters = new Money(0.05);
    public static final Money TenPiasters = new Money(0.10);
    public static final Money TwentyFivePiasters = new Money(0.25);
    public static final Money FiftyPiasters = new Money(0.50);
    public static final Money OneDinar = new Money(1.00);
    public static final Money FiveDinars = new Money(5.00);
    public static final Money TenDinars = new Money(10.00);
    public static final Money TwentyDinars = new Money(20.00);
    public static final Money FiftyDinars = new Money(50.00);

    private final List<Double> denominations;

    // Constructor for initializing the money value with a list of denominations
    public Money(List<Double> denominations) {
        this.denominations = new ArrayList<>(denominations);
        Collections.sort(this.denominations, Collections.reverseOrder());
    }

    // Constructor to initialize with a single value
    public Money(double amount) {
        this.denominations = new ArrayList<>();
        this.denominations.add(amount);
    }

    // Returns the total amount of money in Dinars
    public double amount() {
        return this.denominations.stream().mapToDouble(Double::doubleValue).sum();
    }

    // Multiplies the money value by a given count
    public Money times(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        List<Double> resultDenominations = new ArrayList<>();
        for (Double denomination : this.denominations) {
            for (int i = 0; i < count; i++) {
                resultDenominations.add(denomination);
            }
        }
        return new Money(resultDenominations);
    }

    // Adds two Money instances together
    public Money plus(Money other) {
        List<Double> combinedDenominations = new ArrayList<>(this.denominations);
        combinedDenominations.addAll(other.denominations);
        return new Money(combinedDenominations);
    }

    // Subtracts one Money instance from another, throws an exception if change cannot be made exactly
    public Money minus(Money other) {
        double amountToSubtract = other.amount();
        double amountAvailable = this.amount();

        if (amountAvailable < amountToSubtract) {
            throw new IllegalArgumentException("Cannot subtract more than available");
        }

        // Create a map to count the occurrences of each denomination
        Map<Double, Integer> denominationCount = new HashMap<>();
        for (Double denomination : this.denominations) {
            denominationCount.put(denomination, denominationCount.getOrDefault(denomination, 0) + 1);
        }

        List<Double> resultDenominations = new ArrayList<>(this.denominations);
        double remainingAmount = amountToSubtract;

        // Sort denominations in descending order
        Collections.sort(resultDenominations, Collections.reverseOrder());

        for (Double denomination : resultDenominations) {
            while (remainingAmount >= denomination && denominationCount.getOrDefault(denomination, 0) > 0) {
                remainingAmount -= denomination;
                denominationCount.put(denomination, denominationCount.get(denomination) - 1);
            }
        }

        if (remainingAmount > 0) {
            throw new IllegalArgumentException("Insufficient denominations to make exact change");
        }

        // Rebuild the result denominations list based on remaining counts
        List<Double> finalDenominations = new ArrayList<>();
        for (Map.Entry<Double, Integer> entry : denominationCount.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                finalDenominations.add(entry.getKey());
            }
        }

        return new Money(finalDenominations);
    }

    // Sums multiple Money instances
    public static Money sum(Money... items) {
        List<Double> totalDenominations = new ArrayList<>();
        for (Money item : items) {
            totalDenominations.addAll(item.denominations);
        }
        return new Money(totalDenominations);
    }

    @Override
    public String toString() {
        return String.format("%.2f", this.amount());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Money)) return false;
        Money other = (Money) obj;
        return Objects.equals(other.amount(), amount());
    }

}
