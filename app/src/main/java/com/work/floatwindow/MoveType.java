package com.work.floatwindow;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MoveType {
    static final int FIXED = 0;
    public static final int INACTIVE = 1;
    public static final int ACTIVE = 2;
    public static final int SLIDE = 3;
    public static final int BACK = 4;

    @IntDef({FIXED, INACTIVE, ACTIVE, SLIDE, BACK})
    @Retention(RetentionPolicy.SOURCE)
    @interface MOVE_TYPE {
    }
}