package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
/* loaded from: classes3.dex */
public class TextRadioCell extends FrameLayout {
    private int animatedColorBackground;
    private Paint animationPaint;
    private float animationProgress;
    private int height;
    private boolean isMultiline;
    private float lastTouchX;
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    static {
        new AnimationProperties.FloatProperty<TextRadioCell>("animationProgress") { // from class: org.telegram.ui.Cells.TextRadioCell.1
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(TextRadioCell textRadioCell, float f) {
                textRadioCell.setAnimationProgress(f);
                textRadioCell.invalidate();
            }

            @Override // android.util.Property
            public Float get(TextRadioCell textRadioCell) {
                return Float.valueOf(textRadioCell.animationProgress);
            }
        };
    }

    public TextRadioCell(Context context) {
        this(context, 21);
    }

    public TextRadioCell(Context context, int i) {
        this(context, i, false);
    }

    public TextRadioCell(Context context, int i, boolean z) {
        super(context);
        this.height = 50;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(z ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i2 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView2 = this.textView;
        boolean z2 = LocaleController.isRTL;
        addView(textView2, LayoutHelper.createFrame(-1, -1.0f, (z2 ? 5 : 3) | 48, z2 ? i : 64.0f, 0.0f, z2 ? 64.0f : i, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(z ? "dialogIcon" : "windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView4 = this.valueTextView;
        boolean z3 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 5 : 3) | 48, z3 ? i : 64.0f, 36.0f, z3 ? 64.0f : i, 0.0f));
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
        addView(this.radioButton, LayoutHelper.createFrame(20, 20.0f, (!LocaleController.isRTL ? 3 : i2) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
        setClipChildren(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.valueTextView.getVisibility() == 0 ? 64.0f : this.height) + (this.needDivider ? 1 : 0), NUM));
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.lastTouchX = motionEvent.getX();
        return super.onTouchEvent(motionEvent);
    }

    public void setTypeface(Typeface typeface) {
        this.textView.setTypeface(typeface);
    }

    public void setHeight(int i) {
        this.height = i;
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        super.setPressed(z);
    }

    public void setTextAndValueAndCheck(String str, String str2, boolean z, boolean z2, boolean z3) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.radioButton.setChecked(z, false);
        this.needDivider = z3;
        this.valueTextView.setVisibility(0);
        this.isMultiline = z2;
        if (z2) {
            this.valueTextView.setLines(0);
            this.valueTextView.setMaxLines(0);
            this.valueTextView.setSingleLine(false);
            this.valueTextView.setEllipsize(null);
            this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(11.0f));
        } else {
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.topMargin = AndroidUtilities.dp(10.0f);
        this.textView.setLayoutParams(layoutParams);
        setWillNotDraw(true ^ z3);
    }

    public void setChecked(boolean z) {
        this.radioButton.setChecked(z, true);
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        clearAnimation();
        this.animatedColorBackground = 0;
        super.setBackgroundColor(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAnimationProgress(float f) {
        this.animationProgress = f;
        Math.max(this.lastTouchX, getMeasuredWidth() - this.lastTouchX);
        AndroidUtilities.dp(40.0f);
        int measuredHeight = getMeasuredHeight() / 2;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.animatedColorBackground != 0) {
            canvas.drawCircle(this.lastTouchX, getMeasuredHeight() / 2, (Math.max(this.lastTouchX, getMeasuredWidth() - this.lastTouchX) + AndroidUtilities.dp(40.0f)) * this.animationProgress, this.animationPaint);
        }
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        int i;
        String str;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.radioButton.isChecked());
        if (this.radioButton.isChecked()) {
            i = R.string.NotificationsOn;
            str = "NotificationsOn";
        } else {
            i = R.string.NotificationsOff;
            str = "NotificationsOff";
        }
        accessibilityNodeInfo.setContentDescription(LocaleController.getString(str, i));
        StringBuilder sb = new StringBuilder();
        sb.append(this.textView.getText());
        if (!TextUtils.isEmpty(this.valueTextView.getText())) {
            sb.append("\n");
            sb.append(this.valueTextView.getText());
        }
        accessibilityNodeInfo.setContentDescription(sb);
        accessibilityNodeInfo.setClassName("android.widget.RadioButton");
    }
}
