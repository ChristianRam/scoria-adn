package com.ceiba.library.model;

public enum Stars {
    ONE(1L),
    TWO(2L),
    THREE(3L),
    FOUR(4L),
    FIVE(5L);

    public final Long number;

    private Stars(Long number) {
        this.number = number;
    }

    public static Stars valueOfNumber(Long number) {
        for (Stars s : values()) {
            if (s.number.equals(number)) {
                return s;
            }
        }
        return null;
    }
}