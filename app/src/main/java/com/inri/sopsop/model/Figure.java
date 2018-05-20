package com.inri.sopsop.model;

import com.inri.sopsop.R;

import java.util.Random;

/**
 * Created by Railag on 17.03.2017.
 */

public enum Figure {
    OVAL,
    RECTANGLE,
    ROUND,
    SQUARE,
    STAR,
    TRAPEZIUM,
    TRIANGLE;

    public int getDrawableId() {
        switch (this) {
            case OVAL:
                return R.drawable.oval;
            case RECTANGLE:
                return R.drawable.rectangle;
            case ROUND:
                return R.drawable.round;
            case SQUARE:
                return R.drawable.rectangle2;
            case STAR:
                return R.drawable.star;
            case TRAPEZIUM:
                return R.drawable.trapezium;
            case TRIANGLE:
            default:
                return R.drawable.triangle;
        }
    }

    public static Figure random() {
        return randomEnum(Figure.class);
    }

    private static final Random random = new Random();

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}