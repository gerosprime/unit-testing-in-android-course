package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class StringDuplicatorTest {

    StringDuplicator stringDuplicator;

    @Before
    public void setUp() {
        stringDuplicator = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyCharacter_duplicatedEmptyCharacter() {
        String duplicated = stringDuplicator.duplicate("");
        assertThat(duplicated, is(""));
    }

    @Test
    public void duplicate_singleCharacter_duplicatedSingleCharacter() {
        String duplicated = stringDuplicator.duplicate("G");

        assertThat(duplicated, is("G"));
    }

    @Test
    public void duplicate_longCharacter_duplicatedLongCharacter() {
        String duplicated = stringDuplicator.duplicate("Glenn");

        assertThat(duplicated, is("Glenn"));
    }

}