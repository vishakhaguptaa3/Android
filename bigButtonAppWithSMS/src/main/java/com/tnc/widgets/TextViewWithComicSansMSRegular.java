package com.tnc.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by a3logics on 22/1/18.
 */

public class TextViewWithComicSansMSRegular extends AppCompatTextView {

    public TextViewWithComicSansMSRegular(Context context, AttributeSet attrs,
                                          int defStyle) {
        super(context, attrs, defStyle);


        if (!isInEditMode()) {
            setCustomFont();
        }

    }

    public TextViewWithComicSansMSRegular(Context context, AttributeSet attrs) {
        super(context, attrs);


        if (!isInEditMode()) {
            setCustomFont();
        }
    }

    public TextViewWithComicSansMSRegular(Context context) {
        super(context);


        if (!isInEditMode()) {
            setCustomFont();
        }
    }

    private void setCustomFont() {

        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/comic_sans_ms_regular.ttf");
        this.setTypeface(typeFace);
    }
}
