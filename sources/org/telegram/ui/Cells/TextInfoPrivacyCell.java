package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
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
    private int fixedSize;
    private String linkTextColorKey;
    private CharSequence text;
    private TextView textView;

    public TextInfoPrivacyCell(Context context) {
        this(context, 21);
    }

    public TextInfoPrivacyCell(Context context, int i) {
        super(context);
        this.linkTextColorKey = "windowBackgroundWhiteLinkText";
        this.bottomPadding = 17;
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 14.0f);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(Theme.getColor(this.linkTextColorKey));
        TextView textView = this.textView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        float f = (float) i;
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, i2 | 48, f, 0.0f, f, 0.0f));
    }

    public void setLinkTextColorKey(String str) {
        this.linkTextColorKey = str;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (this.fixedSize != 0) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.fixedSize), NUM));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    public void setBottomPadding(int i) {
        this.bottomPadding = i;
    }

    public void setFixedSize(int i) {
        this.fixedSize = i;
    }

    public void setText(CharSequence charSequence) {
        if (!TextUtils.equals(charSequence, this.text)) {
            this.text = charSequence;
            int i = 0;
            if (charSequence == null) {
                this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            } else {
                this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp((float) this.bottomPadding));
            }
            CharSequence charSequence2 = null;
            int length = charSequence.length();
            while (i < length - 1) {
                if (charSequence.charAt(i) == 10) {
                    int i2 = i + 1;
                    if (charSequence.charAt(i2) == 10) {
                        if (charSequence2 == null) {
                            charSequence2 = new SpannableString(charSequence);
                        }
                        charSequence2.setSpan(new AbsoluteSizeSpan(10, true), i2, i + 2, 33);
                    }
                }
                i++;
            }
            TextView textView = this.textView;
            if (charSequence2 != null) {
                charSequence = charSequence2;
            }
            textView.setText(charSequence);
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextColor(String str) {
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setTag(str);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public int length() {
        return this.textView.length();
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView = this.textView;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", fArr));
            return;
        }
        TextView textView2 = this.textView;
        if (!z) {
            f = 0.5f;
        }
        textView2.setAlpha(f);
    }
}
