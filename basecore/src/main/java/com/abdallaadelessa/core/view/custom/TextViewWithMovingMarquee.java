package com.abdallaadelessa.core.view.custom;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextViewWithMovingMarquee extends TextView
{
    public TextViewWithMovingMarquee(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewWithMovingMarquee(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TextViewWithMovingMarquee(Context context)
    {
        super(context);
        init();
    }

    public void init() {
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(10);
        setHorizontallyScrolling(true);
        setSelected(true);
    }
}