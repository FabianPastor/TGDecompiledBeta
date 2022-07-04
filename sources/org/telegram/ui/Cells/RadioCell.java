package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
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
import org.telegram.ui.Components.RadioButton;

public class RadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public RadioCell(Context context) {
        this(context, false, 21);
    }

    public RadioCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, false, 21, resourcesProvider2);
    }

    public RadioCell(Context context, boolean dialog, int padding) {
        this(context, dialog, padding, (Theme.ResourcesProvider) null);
    }

    public RadioCell(Context context, boolean dialog, int padding, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        if (dialog) {
            textView2.setTextColor(Theme.getColor("dialogTextBlack", resourcesProvider2));
        } else {
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) padding, 0.0f, (float) padding, 0.0f));
        RadioButton radioButton2 = new RadioButton(context);
        this.radioButton = radioButton2;
        radioButton2.setSize(AndroidUtilities.dp(20.0f));
        if (dialog) {
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground", resourcesProvider2), Theme.getColor("dialogRadioBackgroundChecked", resourcesProvider2));
        } else {
            this.radioButton.setColor(Theme.getColor("radioBackground", resourcesProvider2), Theme.getColor("radioBackgroundChecked", resourcesProvider2));
        }
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? padding + 1 : 0), 14.0f, (float) (!LocaleController.isRTL ? padding + 1 : 0), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, boolean checked, boolean divider) {
        this.textView.setText(text);
        this.radioButton.setChecked(checked, false);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public boolean isChecked() {
        return this.radioButton.isChecked();
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        super.setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView2, property, fArr));
            RadioButton radioButton2 = this.radioButton;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr2[0] = f;
            animators.add(ObjectAnimator.ofFloat(radioButton2, property2, fArr2));
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        RadioButton radioButton3 = this.radioButton;
        if (!value) {
            f = 0.5f;
        }
        radioButton3.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.RadioButton");
        info.setCheckable(true);
        info.setChecked(isChecked());
    }
}
