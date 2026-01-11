package com.ridango.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvTest {

    @Test
    void parsesSimpleCsvLine() {
        String[] cols = Csv.parseLine("a,b,c");
        assertArrayEquals(new String[]{"a","b","c"}, cols);
    }

    @Test
    void parsesQuotedCommas() {
        String[] cols = Csv.parseLine("1,\"this, is,a, test, \",3");
        assertArrayEquals(new String[]{"1","this, is,a, test, ","3"}, cols);
    }

    @Test
    void headerIndex_handlesUtf8Bom() {
        String header = "\uFEFFstop_id,stop_name";
        assertEquals(0, Csv.headerIndex(header, "stop_id"));
        assertEquals(1, Csv.headerIndex(header, "stop_name"));
    }
}
