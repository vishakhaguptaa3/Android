package com.tnc.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by a3logics on 16/3/18.
 */

public class TextViewWithRobotoBold extends AppCompatTextView {

    public TextViewWithRobotoBold(Context context, AttributeSet attrs,
                                          int defStyle) {
        super(context, attrs, defStyle);


        if (!isInEditMode()) {
            setCustomFont();
        }

    }

    public TextViewWithRobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);


        if (!isInEditMode()) {
            setCustomFont();
        }
    }

    public TextViewWithRobotoBold(Context context) {
        super(context);


        if (!isInEditMode()) {
            setCustomFont();
        }
    }

    private void setCustomFont() {

        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Bold_1.ttf");
        this.setTypeface(typeFace);
    }
}
