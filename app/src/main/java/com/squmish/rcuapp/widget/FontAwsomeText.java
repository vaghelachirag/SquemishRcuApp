package com.squmish.rcuapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class FontAwsomeText extends androidx.appcompat.widget.AppCompatTextView{


    public FontAwsomeText(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "FontAwesome.ttf");
        this.setTypeface(face);
    }

    public FontAwsomeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "FontAwesome.ttf");
        this.setTypeface(face);
    }

    public FontAwsomeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "FontAwesome.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }
}
