package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
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
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence text;
    private TextView textView;
    private int topPadding;

    public TextInfoPrivacyCell(Context context) {
        this(context, 21, (Theme.ResourcesProvider) null);
    }

    public TextInfoPrivacyCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, 21, resourcesProvider2);
    }

    public TextInfoPrivacyCell(Context context, int padding) {
        this(context, padding, (Theme.ResourcesProvider) null);
    }

    public TextInfoPrivacyCell(Context context, int padding, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.linkTextColorKey = "windowBackgroundWhiteLinkText";
        this.topPadding = 10;
        this.bottomPadding = 17;
        this.resourcesProvider = resourcesProvider2;
        AnonymousClass1 r0 = new TextView(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                TextInfoPrivacyCell.this.onTextDraw();
                super.onDraw(canvas);
                TextInfoPrivacyCell.this.afterTextDraw();
            }
        };
        this.textView = r0;
        r0.setTextSize(1, 14.0f);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(getThemedColor(this.linkTextColorKey));
        this.textView.setImportantForAccessibility(2);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) padding, 0.0f, (float) padding, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onTextDraw() {
    }

    /* access modifiers changed from: protected */
    public void afterTextDraw() {
    }

    public void setLinkTextColorKey(String key) {
        this.linkTextColorKey = key;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.fixedSize != 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.fixedSize), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    public void setTopPadding(int topPadding2) {
        this.topPadding = topPadding2;
    }

    public void setBottomPadding(int value) {
        this.bottomPadding = value;
    }

    public void setFixedSize(int size) {
        this.fixedSize = size;
    }

    public void setText(CharSequence text2) {
        if (!TextUtils.equals(text2, this.text)) {
            this.text = text2;
            if (text2 == null) {
                this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            } else {
                this.textView.setPadding(0, AndroidUtilities.dp((float) this.topPadding), 0, AndroidUtilities.dp((float) this.bottomPadding));
            }
            SpannableString spannableString = null;
            if (text2 != null) {
                int len = text2.length();
                for (int i = 0; i < len - 1; i++) {
                    if (text2.charAt(i) == 10 && text2.charAt(i + 1) == 10) {
                        if (spannableString == null) {
                            spannableString = new SpannableString(text2);
                        }
                        spannableString.setSpan(new AbsoluteSizeSpan(10, true), i + 1, i + 2, 33);
                    }
                }
            }
            this.textView.setText(spannableString != null ? spannableString : text2);
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextColor(String key) {
        this.textView.setTextColor(getThemedColor(key));
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
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            return;
        }
        TextView textView3 = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView3.setAlpha(f);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextView.class.getName());
        info.setText(this.text);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
