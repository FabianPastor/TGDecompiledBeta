package org.telegram.p005ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.TextInfoPrivacyCell */
public class TextInfoPrivacyCell extends FrameLayout {
    private String linkTextColorKey = Theme.key_windowBackgroundWhiteLinkText;
    private TextView textView;

    public TextInfoPrivacyCell(Context context) {
        int i;
        int i2 = 5;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 14.0f);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i);
        this.textView.setPadding(0, AndroidUtilities.m10dp(10.0f), 0, AndroidUtilities.m10dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i2 | 48, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setLinkTextColor(Theme.getColor(this.linkTextColorKey));
    }

    public void setLinkTextColorKey(String key) {
        this.linkTextColorKey = key;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(CharSequence text) {
        if (text == null) {
            this.textView.setPadding(0, AndroidUtilities.m10dp(2.0f), 0, 0);
        } else {
            this.textView.setPadding(0, AndroidUtilities.m10dp(10.0f), 0, AndroidUtilities.m10dp(17.0f));
        }
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public TextView getTextView() {
        return this.textView;
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
}
