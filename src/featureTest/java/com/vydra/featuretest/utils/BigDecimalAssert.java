package com.vydra.featuretest.utils;

import org.assertj.core.api.AbstractAssert;

import java.math.BigDecimal;

import static java.lang.String.format;

public class BigDecimalAssert extends AbstractAssert<BigDecimalAssert, BigDecimal> {

    public BigDecimalAssert(BigDecimal actual) {
        super(actual, BigDecimalAssert.class);
    }

    public BigDecimalAssert isEqualByComparison(final BigDecimal expected) {
        if (actual.compareTo(expected) != 0) {
            failWithMessage(format("Expected %s to be equal to %s", actual, expected));
        }
        return this;
    }
}
