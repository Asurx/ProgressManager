package com.asurx.progressmanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class CalFun {
    public static int dip2px(Context context, float dipValue){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
