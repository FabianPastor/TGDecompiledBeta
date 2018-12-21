package org.telegram.p005ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.HeaderCell */
public class HeaderCell extends FrameLayout {
    private int height;
    private TextView textView;
    private SimpleTextView textView2;

    public HeaderCell(Context context) {
        this(context, false, 21, false);
    }

    public HeaderCell(Context context, int padding) {
        this(context, false, padding, false);
    }

    public HeaderCell(Context context, boolean dialog, int padding, boolean text2) {
        int i;
        int i2 = 3;
        super(context);
        this.height = 40;
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (dialog) {
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        } else {
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        }
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, (float) padding, 15.0f, (float) padding, 0.0f));
        if (text2) {
            int i3;
            this.textView2 = new SimpleTextView(getContext());
            this.textView2.setTextSize(13);
            SimpleTextView simpleTextView = this.textView2;
            if (LocaleController.isRTL) {
                i3 = 3;
            } else {
                i3 = 5;
            }
            simpleTextView.setGravity(i3 | 48);
            view = this.textView2;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, (float) padding, 21.0f, (float) padding, 0.0f));
        }
    }

    public void setHeight(int value) {
        this.height = value;
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        TextView textView;
        if (animators != null) {
            textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            return;
        }
        textView = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView.setAlpha(f);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp((float) this.height), NUM));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setText2(String text) {
        this.textView2.setText(text);
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }
}
