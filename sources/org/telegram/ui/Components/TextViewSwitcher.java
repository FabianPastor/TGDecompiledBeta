package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class TextViewSwitcher extends ViewSwitcher {
    public TextViewSwitcher(Context context) {
        super(context);
    }

    public void setText(CharSequence text) {
        setText(text, true);
    }

    public void setText(CharSequence text, boolean animated) {
        if (TextUtils.equals(text, getCurrentView().getText())) {
            return;
        }
        if (animated) {
            getNextView().setText(text);
            showNext();
            return;
        }
        getCurrentView().setText(text);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof TextView) {
            super.addView(child, index, params);
            return;
        }
        throw new IllegalArgumentException();
    }

    public TextView getCurrentView() {
        return (TextView) super.getCurrentView();
    }

    public TextView getNextView() {
        return (TextView) super.getNextView();
    }

    public void invalidateViews() {
        getCurrentView().invalidate();
        getNextView().invalidate();
    }
}
