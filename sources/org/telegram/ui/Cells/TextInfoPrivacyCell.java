package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
/* loaded from: classes3.dex */
public class TextInfoPrivacyCell extends FrameLayout {
    private int bottomPadding;
    private int fixedSize;
    private String linkTextColorKey;
    private LinkSpanDrawable.LinkCollector links;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence text;
    private TextView textView;
    private int topPadding;

    protected void afterTextDraw() {
    }

    protected void onTextDraw() {
    }

    public TextInfoPrivacyCell(Context context) {
        this(context, 21, null);
    }

    public TextInfoPrivacyCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 21, resourcesProvider);
    }

    public TextInfoPrivacyCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.linkTextColorKey = "windowBackgroundWhiteLinkText";
        this.topPadding = 10;
        this.bottomPadding = 17;
        this.resourcesProvider = resourcesProvider;
        LinkSpanDrawable.LinkCollector linkCollector = new LinkSpanDrawable.LinkCollector(this);
        this.links = linkCollector;
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, linkCollector, resourcesProvider) { // from class: org.telegram.ui.Cells.TextInfoPrivacyCell.1
            @Override // org.telegram.ui.Components.LinkSpanDrawable.LinksTextView, android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                TextInfoPrivacyCell.this.onTextDraw();
                super.onDraw(canvas);
                TextInfoPrivacyCell.this.afterTextDraw();
            }
        };
        this.textView = linksTextView;
        linksTextView.setTextSize(1, 14.0f);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(getThemedColor(this.linkTextColorKey));
        this.textView.setImportantForAccessibility(2);
        float f = i;
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i2) | 48, f, 0.0f, f, 0.0f));
        setWillNotDraw(false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.links != null) {
            canvas.save();
            canvas.translate(this.textView.getLeft(), this.textView.getTop());
            if (this.links.draw(canvas)) {
                invalidate();
            }
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    public void setLinkTextColorKey(String str) {
        this.linkTextColorKey = str;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.fixedSize != 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.fixedSize), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    public void setTopPadding(int i) {
        this.topPadding = i;
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
            if (charSequence == null) {
                this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            } else {
                this.textView.setPadding(0, AndroidUtilities.dp(this.topPadding), 0, AndroidUtilities.dp(this.bottomPadding));
            }
            SpannableString spannableString = null;
            if (charSequence != null) {
                int length = charSequence.length();
                for (int i = 0; i < length - 1; i++) {
                    if (charSequence.charAt(i) == '\n') {
                        int i2 = i + 1;
                        if (charSequence.charAt(i2) == '\n') {
                            if (spannableString == null) {
                                spannableString = new SpannableString(charSequence);
                            }
                            spannableString.setSpan(new AbsoluteSizeSpan(10, true), i2, i + 2, 33);
                        }
                    }
                }
            }
            TextView textView = this.textView;
            if (spannableString != null) {
                charSequence = spannableString;
            }
            textView.setText(charSequence);
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextColor(String str) {
        this.textView.setTextColor(getThemedColor(str));
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
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView, property, fArr));
            return;
        }
        TextView textView2 = this.textView;
        if (!z) {
            f = 0.5f;
        }
        textView2.setAlpha(f);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TextView.class.getName());
        accessibilityNodeInfo.setText(this.text);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
