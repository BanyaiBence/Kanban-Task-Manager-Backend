package com.bencebanyai.kanban.kanbantaskmanager.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LexoRankUtils {

    private static final char MIN_CHAR = 'a';
    private static final char MAX_CHAR = 'z';

    public String getMidpoint(String prev, String next) {
        // First item
        if (prev == null && next == null) {
            return "m";
        }

        // Top of the list
        if (prev == null) {
            prev = generatePadding(next.length(), MIN_CHAR);
            log.debug("Previous padding is {}", prev);
            if (prev.compareTo(next) >= 0) {
                return MIN_CHAR +
                        next;
            }
        }

        // Bottom of the list
        if (next == null) {
            next = generatePadding(prev.length(), MAX_CHAR);
            log.debug("Next padding is {}", next);
            if (prev.compareTo(next) >= 0) {
                return String.valueOf(prev + "m");
            }
        }

        // Between two items
        StringBuilder mid = new StringBuilder();
        int maxLength = Math.max(prev.length(), next.length());
        for (int i = 0; i < maxLength; i++) {
            int pChar = i < prev.length() ? prev.charAt(i) : MIN_CHAR;
            int nChar = i < next.length() ? next.charAt(i) : MAX_CHAR + 1;

            if (pChar == nChar) {
                mid.append((char) pChar);
            } else {
                int diff = nChar - pChar;
                if (diff > 1) {
                    mid.append((char) (pChar + diff / 2));
                    return mid.toString();
                } else {
                    mid.append((char) pChar);
                }
            }
        }
        mid.append('m');
        return mid.toString();
    }

    private String generatePadding(int length, char ch) {
        StringBuilder sb = new StringBuilder();
        sb.repeat(String.valueOf(ch), Math.max(0, length));
        return sb.toString();
    }
}
