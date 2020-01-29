package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextSettingsCell extends FrameLayout {
    private boolean canDisable;
    private boolean needDivider;
    private TextView textView;
    private ImageView valueImageView;
    private TextView valueTextView;

    public TextSettingsCell(Context context) {
        this(context, 21);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TextSettingsCell(Context context, int i) {
        super(context);
        Context context2 = context;
        this.textView = new TextView(context2);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i2 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        float f = (float) i;
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, f, 0.0f, f, 0.0f));
        this.valueTextView = new TextView(context2);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, f, 0.0f, f, 0.0f));
        this.valueImageView = new ImageView(context2);
        this.valueImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.valueImageView.setVisibility(4);
        this.valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.valueImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i2) | 16, f, 0.0f, f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int i3 = measuredWidth / 2;
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        }
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            measuredWidth = (measuredWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setCanDisable(boolean z) {
        this.canDisable = z;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextValueColor(int i) {
        this.valueTextView.setTextColor(i);
    }

    public void setText(String str, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        this.textView.setText(str);
        this.valueImageView.setVisibility(4);
        if (str2 != null) {
            this.valueTextView.setText(str2);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.needDivider = z;
        setWillNotDraw(!z);
        requestLayout();
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setVisibility(4);
        if (i != 0) {
            this.valueImageView.setVisibility(0);
            this.valueImageView.setImageResource(i);
        } else {
            this.valueImageView.setVisibility(4);
        }
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        setEnabled(z);
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            if (this.valueTextView.getVisibility() == 0) {
                TextView textView3 = this.valueTextView;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.5f;
                arrayList.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr2));
            }
            if (this.valueImageView.getVisibility() == 0) {
                ImageView imageView = this.valueImageView;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(imageView, "alpha", fArr3));
                return;
            }
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha(z ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            ImageView imageView2 = this.valueImageView;
            if (!z) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        float f = 0.5f;
        this.textView.setAlpha((z || !this.canDisable) ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha((z || !this.canDisable) ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            ImageView imageView = this.valueImageView;
            if (z || !this.canDisable) {
                f = 1.0f;
            }
            imageView.setAlpha(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(isEnabled());
    }
}
