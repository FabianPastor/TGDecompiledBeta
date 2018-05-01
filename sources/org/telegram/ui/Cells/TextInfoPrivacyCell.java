package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextInfoPrivacyCell extends FrameLayout {
    private TextView textView;

    public TextInfoPrivacyCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 14.0f);
        int i = 3;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        context = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(-2, -2.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(CharSequence charSequence) {
        if (charSequence == null) {
            this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        } else {
            this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        }
        this.textView.setText(charSequence);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        float f = 0.5f;
        if (arrayList != null) {
            TextView textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
            return;
        }
        arrayList = this.textView;
        if (z) {
            f = 1.0f;
        }
        arrayList.setAlpha(f);
    }
}
