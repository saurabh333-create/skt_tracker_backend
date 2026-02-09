package com.skt.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateRangeValidator {

    public static void validate(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("fromDate cannot be after toDate");
        }
    }
}
