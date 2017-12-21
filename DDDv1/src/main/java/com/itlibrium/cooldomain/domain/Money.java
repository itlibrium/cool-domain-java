package com.itlibrium.cooldomain.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public static Money fromDecimal(BigDecimal value) {
        return new Money(value);
    }

    public static Money sum(Money money1, Money money2) {
        return new Money(money1._value.add(money2._value));
    }

    public static Money subtract(Money money1, Money money2) {
        return new Money(money1._value.subtract(money2._value));
    }

    public static Money multiply(Money money, double multiplicand) {
        return new Money(money._value.multiply(BigDecimal.valueOf(multiplicand)));
    }

    public static Money max(Money money1, Money money2) {
        return new Money(money1._value.max(money2._value));
    }

    public static Money min(Money money1, Money money2) {
        return new Money(money1._value.min(money2._value));
    }

    private BigDecimal _value;

    public Money(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == -1) throw new IllegalArgumentException();
        _value = value;
    }

    public boolean greaterThan(Money other) {
        return _value.compareTo(other._value) == 1;
    }

    public boolean lessThan(Money other) {
        return _value.compareTo(other._value) == -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return round(_value).equals(round(money._value));
    }

    private static BigDecimal round(BigDecimal in) {
        return in.setScale(2, RoundingMode.CEILING);
    }
    @Override
    public int hashCode() {

        return Objects.hash(_value);
    }

    @Override
    public String toString() {
        return "Money{" +
                "_value=" + _value +
                '}';
    }
}