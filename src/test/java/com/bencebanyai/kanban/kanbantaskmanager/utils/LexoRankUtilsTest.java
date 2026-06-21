package com.bencebanyai.kanban.kanbantaskmanager.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class LexoRankUtilsTest {

    private LexoRankUtils lexoRankUtils;

    @BeforeEach
    void setUp() {
        lexoRankUtils = new LexoRankUtils();
    }

    @Test
    @DisplayName("Should return 'm' when list is completely empty")
    void getMidpoint_EmptyList() {
        String result = lexoRankUtils.getMidpoint(null, null);
        assertEquals("m", result, "First item should default to 'm'");
    }

    @Test
    @DisplayName("Should calculate correct midpoint when inserting at the top of the list")
    void getMidpoint_InsertAtTop() {
        // Middle of 'a' (padded) and 'm' is 'g'
        String result = lexoRankUtils.getMidpoint(null, "m");
        assertEquals("g", result, "Inserting above 'm' should yield 'g'");

        // Middle of 'a' and 'c' is 'b'
        String result2 = lexoRankUtils.getMidpoint(null, "c");
        assertEquals("b", result2, "Inserting above 'c' should yield 'b'");
    }

    @Test
    @DisplayName("Should calculate correct midpoint when inserting at the bottom of the list")
    void getMidpoint_InsertAtBottom() {
        // Middle of 'm' and 'z' (padded) is 's'
        String result = lexoRankUtils.getMidpoint("m", null);
        assertEquals("s", result, "Inserting below 'm' should yield 's'");

        // Middle of 'y' and 'z' (padded) is 'y' + 'm'
        String result2 = lexoRankUtils.getMidpoint("y", null);
        assertEquals("ym", result2, "Inserting below 'y' should yield 'ym'");
    }

    @Test
    @DisplayName("Should find exact midpoint when there is a gap > 1 between characters")
    void getMidpoint_WithGap() {
        String result = lexoRankUtils.getMidpoint("a", "e");
        assertEquals("c", result, "Midpoint of 'a' and 'e' should be 'c'");
    }

    @Test
    @DisplayName("Should append precision when items are sequentially adjacent (difference of 1)")
    void getMidpoint_AdjacentItems() {
        String result = lexoRankUtils.getMidpoint("a", "b");
        assertEquals("am", result, "Midpoint of 'a' and 'b' should be 'am'");

        String result2 = lexoRankUtils.getMidpoint("c", "d");
        assertEquals("cm", result2, "Midpoint of 'c' and 'd' should be 'cm'");
    }

    @Test
    @DisplayName("Should correctly handle strings of different lengths")
    void getMidpoint_DifferentLengths() {
        String result = lexoRankUtils.getMidpoint("a", "am");
        assertEquals("ag", result, "Midpoint of 'a' and 'am' should be 'ag'");

        String result2 = lexoRankUtils.getMidpoint("am", "b");
        assertEquals("at", result2, "Midpoint of 'am' and 'b' should be 'at'");
    }

    @Test
    @DisplayName("Should handle deep precision nesting correctly")
    void getMidpoint_DeepPrecision() {
        String result1 = lexoRankUtils.getMidpoint("a", "b");
        String result2 = lexoRankUtils.getMidpoint("a", result1);
        String result3 = lexoRankUtils.getMidpoint("a", result2);
        String result4 = lexoRankUtils.getMidpoint("a", result3);
        String result5 = lexoRankUtils.getMidpoint("a", result4);

        assertEquals("aam", result5, "Midpoint of 'a' and 'ab' should extend length to 'aam'");
    }

    @Test
    @DisplayName("Appending from empty list to end")
    void getMidpoint_DeepFromEmptyEnd() {
        List<String> result = new ArrayList<>();
        result.add(lexoRankUtils.getMidpoint("", ""));
        for (int i = 0; i < 100; i++) {
            result.add(lexoRankUtils.getMidpoint(result.getLast(), ""));
        }
        assertEquals("zzzzzzzzzzzzzzzzzzzzm", result.getLast());
    }

    @Test
    @DisplayName("Appending from empty list to middle")
    void getMidpoint_DeepFromEmptyMid() {
        List<String> result = new ArrayList<>();
        result.add(lexoRankUtils.getMidpoint("", ""));
        result.add(lexoRankUtils.getMidpoint(result.getFirst(), ""));
        for (int i = 0; i < 1000; i++) {
            int midIdx = result.size() / 2;
            result.add(midIdx, lexoRankUtils.getMidpoint(result.get(midIdx - 1), result.get(midIdx)));
        }
        assertEquals("n", result.get(1));
        assertEquals("p", result.get(result.size() - 2));
    }
}
