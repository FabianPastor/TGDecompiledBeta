package org.telegram.ui.Cells;

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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextInfoPrivacyCell extends FrameLayout {
    private int bottomPadding;
    private String linkTextColorKey;
    private TextView textView;

    public TextInfoPrivacyCell(Context context) {
        this(context, 21);
    }

    public TextInfoPrivacyCell(Context context, int padding) {
        int i = 5;
        super(context);
        this.linkTextColorKey = "windowBackgroundWhiteLinkText";
        this.bottomPadding = 17;
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 14.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(Theme.getColor(this.linkTextColorKey));
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i | 48, (float) padding, 0.0f, (float) padding, 0.0f));
    }

    public void setLinkTextColorKey(String key) {
        this.linkTextColorKey = key;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setBottomPadding(int value) {
        this.bottomPadding = value;
    }

    public void setText(CharSequence text) {
        if (text == null) {
            this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        } else {
            this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp((float) this.bottomPadding));
        }
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextColor(String key) {
        this.textView.setTextColor(Theme.getColor(key));
        this.textView.setTag(key);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public int length() {
        return this.textView.length();
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
