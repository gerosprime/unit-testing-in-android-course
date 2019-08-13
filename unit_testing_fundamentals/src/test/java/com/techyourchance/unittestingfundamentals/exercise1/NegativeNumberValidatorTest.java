package com.techyourchance.unittestingfundamentals.exercise1;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NegativeNumberValidatorTest {

    NegativeNumberValidator SUT;

    @Before
    public void setUpSUT() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void testPositive() {
        boolean negative = SUT.isNegative(1);
        Assert.assertThat(negative, Is.is(false));
    }

    @Test
    public void testZero() {
        boolean negative = SUT.isNegative(0);
        Assert.assertThat(negative, Is.is(false));
    }

    @Test
    public void testNegative() {
        boolean negative = SUT.isNegative(-1);
        Assert.assertThat(negative, Is.is(true));
    }


}