package com.abdallaadelessa.core.view.custom;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithEllipsize extends TextView
{

    public TextViewWithEllipsize(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public TextViewWithEllipsize(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public TextViewWithEllipsize(Context context)
    {
        super(context);
        init(context);
    }

    public void init(Context con)
    {
        setLines(1);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.END);
    }
}