package com.hospital.admin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateUtil {

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static LocalDate parseToLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return null;

        String normalized = dateStr.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (Exception ignored) {
            }
        }

        throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }

    public static String format(LocalDate date) {
        return date == null ? null : date.format(DISPLAY_FORMATTER);
    }

    public static String format(java.time.LocalDateTime date) {
        return date == null ? null : date.format(DISPLAY_FORMATTER);
    }

    public static LocalDateTime parseToLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception ignored) {}
        }

        throw new RuntimeException("Invalid date format: " + dateStr);
    }
}
