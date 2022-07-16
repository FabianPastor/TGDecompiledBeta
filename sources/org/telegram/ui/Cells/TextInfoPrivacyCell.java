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

public class TextInfoPrivacyCell extends FrameLayout {
    private int bottomPadding;
    private int fixedSize;
    private String linkTextColorKey;
    private LinkSpanDrawable.LinkCollector links;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence text;
    private TextView textView;
    private int topPadding;

    /* access modifiers changed from: protected */
    public void afterTextDraw() {
    }

    /* access modifiers changed from: protected */
    public void onTextDraw() {
    }

    public TextInfoPrivacyCell(Context context) {
        this(context, 21, (Theme.ResourcesProvider) null);
    }

    public TextInfoPrivacyCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, 21, resourcesProvider2);
    }

    public TextInfoPrivacyCell(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.linkTextColorKey = "windowBackgroundWhiteLinkText";
        this.topPadding = 10;
        this.bottomPadding = 17;
        this.resourcesProvider = resourcesProvider2;
        LinkSpanDrawable.LinkCollector linkCollector = new LinkSpanDrawable.LinkCollector(this);
        this.links = linkCollector;
        AnonymousClass1 r0 = new LinkSpanDrawable.LinksTextView(context, linkCollector, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                TextInfoPrivacyCell.this.onTextDraw();
                super.onDraw(canvas);
                TextInfoPrivacyCell.this.afterTextDraw();
            }
        };
        this.textView = r0;
        r0.setTextSize(1, 14.0f);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(getThemedColor(this.linkTextColorKey));
        this.textView.setImportantForAccessibility(2);
        float f = (float) i;
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i2) | 48, f, 0.0f, f, 0.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.links != null) {
            canvas.save();
            canvas.translate((float) this.textView.getLeft(), (float) this.textView.getTop());
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.fixedSize != 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.fixedSize), NUM));
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
                this.textView.setPadding(0, AndroidUtilities.dp((float) this.topPadding), 0, AndroidUtilities.dp((float) this.bottomPadding));
            }
            SpannableString spannableString = null;
            if (charSequence != null) {
                int length = charSequence.length();
                for (int i = 0; i < length - 1; i++) {
                    if (charSequence.charAt(i) == 10) {
                        int i2 = i + 1;
                        if (charSequence.charAt(i2) == 10) {
                            if (spannableString == null) {
                                spannableString = new SpannableString(charSequence);
                            }
                            spannableString.setSpan(new AbsoluteSizeSpan(10, true), i2, i + 2, 33);
                        }
                    }
                }
            }
            TextView textView2 = this.textView;
            if (spannableString != null) {
                charSequence = spannableString;
            }
            textView2.setText(charSequence);
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
            TextView textView2 = this.textView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView2, property, fArr));
            return;
        }
        TextView textView3 = this.textView;
        if (!z) {
            f = 0.5f;
        }
        textView3.setAlpha(f);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TextView.class.getName());
        accessibilityNodeInfo.setText(this.text);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
