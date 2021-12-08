package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class TextCheckCell extends FrameLayout {
    public static final Property<TextCheckCell, Float> ANIMATION_PROGRESS = new AnimationProperties.FloatProperty<TextCheckCell>("animationProgress") {
        public void setValue(TextCheckCell object, float value) {
            object.setAnimationProgress(value);
            object.invalidate();
        }

        public Float get(TextCheckCell object) {
            return Float.valueOf(object.animationProgress);
        }
    };
    /* access modifiers changed from: private */
    public int animatedColorBackground;
    private Paint animationPaint;
    /* access modifiers changed from: private */
    public float animationProgress;
    private ObjectAnimator animator;
    private Switch checkBox;
    private boolean drawCheckRipple;
    private int height;
    private boolean isMultiline;
    private float lastTouchX;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public TextCheckCell(Context context) {
        this(context, 21);
    }

    public TextCheckCell(Context context, int padding) {
        this(context, padding, false);
    }

    public TextCheckCell(Context context, int padding, boolean dialog) {
        super(context);
        this.height = 50;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(dialog ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 70.0f : (float) padding, 0.0f, LocaleController.isRTL ? (float) padding : 70.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(dialog ? "dialogIcon" : "windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 64.0f : (float) padding, 36.0f, LocaleController.isRTL ? (float) padding : 64.0f, 0.0f));
        Switch switchR = new Switch(context);
        this.checkBox = switchR;
        switchR.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
        addView(this.checkBox, LayoutHelper.createFrame(37, 20.0f, (LocaleController.isRTL ? 3 : i) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
        setClipChildren(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.valueTextView.getVisibility() == 0 ? 64.0f : (float) this.height) + (this.needDivider ? 1 : 0), NUM));
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.lastTouchX = event.getX();
        return super.onTouchEvent(event);
    }

    public void setTextAndCheck(String text, boolean checked, boolean divider) {
        this.textView.setText(text);
        this.isMultiline = false;
        this.checkBox.setChecked(checked, false);
        this.needDivider = divider;
        this.valueTextView.setVisibility(8);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.topMargin = 0;
        this.textView.setLayoutParams(layoutParams);
        setWillNotDraw(!divider);
    }

    public void setColors(String key, String switchKey, String switchKeyChecked, String switchThumb, String switchThumbChecked) {
        this.textView.setTextColor(Theme.getColor(key));
        this.checkBox.setColors(switchKey, switchKeyChecked, switchThumb, switchThumbChecked);
        this.textView.setTag(key);
    }

    public void setTypeface(Typeface typeface) {
        this.textView.setTypeface(typeface);
    }

    public void setHeight(int value) {
        this.height = value;
    }

    public void setDrawCheckRipple(boolean value) {
        this.drawCheckRipple = value;
    }

    public void setPressed(boolean pressed) {
        if (this.drawCheckRipple) {
            this.checkBox.setDrawRipple(pressed);
        }
        super.setPressed(pressed);
    }

    public void setTextAndValueAndCheck(String text, String value, boolean checked, boolean multiline, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.checkBox.setChecked(checked, false);
        this.needDivider = divider;
        this.valueTextView.setVisibility(0);
        this.isMultiline = multiline;
        if (multiline) {
            this.valueTextView.setLines(0);
            this.valueTextView.setMaxLines(0);
            this.valueTextView.setSingleLine(false);
            this.valueTextView.setEllipsize((TextUtils.TruncateAt) null);
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
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        super.setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            Switch switchR = this.checkBox;
            float[] fArr2 = new float[1];
            fArr2[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(switchR, "alpha", fArr2));
            if (this.valueTextView.getVisibility() == 0) {
                TextView textView3 = this.valueTextView;
                float[] fArr3 = new float[1];
                if (!value) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                animators.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr3));
                return;
            }
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        this.checkBox.setAlpha(value ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            TextView textView4 = this.valueTextView;
            if (!value) {
                f = 0.5f;
            }
            textView4.setAlpha(f);
        }
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked, true);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void setBackgroundColor(int color) {
        clearAnimation();
        this.animatedColorBackground = 0;
        super.setBackgroundColor(color);
    }

    public void setBackgroundColorAnimated(boolean checked, int color) {
        ObjectAnimator objectAnimator = this.animator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.animator = null;
        }
        int i = this.animatedColorBackground;
        if (i != 0) {
            setBackgroundColor(i);
        }
        int i2 = 1;
        if (this.animationPaint == null) {
            this.animationPaint = new Paint(1);
        }
        Switch switchR = this.checkBox;
        if (!checked) {
            i2 = 2;
        }
        switchR.setOverrideColor(i2);
        this.animatedColorBackground = color;
        this.animationPaint.setColor(color);
        this.animationProgress = 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, ANIMATION_PROGRESS, new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                TextCheckCell textCheckCell = TextCheckCell.this;
                textCheckCell.setBackgroundColor(textCheckCell.animatedColorBackground);
                int unused = TextCheckCell.this.animatedColorBackground = 0;
                TextCheckCell.this.invalidate();
            }
        });
        this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.animator.setDuration(240).start();
    }

    /* access modifiers changed from: private */
    public void setAnimationProgress(float value) {
        this.animationProgress = value;
        float rad = Math.max(this.lastTouchX, ((float) getMeasuredWidth()) - this.lastTouchX) + ((float) AndroidUtilities.dp(40.0f));
        float cx = this.lastTouchX;
        this.checkBox.setOverrideColorProgress(cx, (float) (getMeasuredHeight() / 2), this.animationProgress * rad);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.animatedColorBackground != 0) {
            canvas.drawCircle(this.lastTouchX, (float) (getMeasuredHeight() / 2), this.animationProgress * (Math.max(this.lastTouchX, ((float) getMeasuredWidth()) - this.lastTouchX) + ((float) AndroidUtilities.dp(40.0f))), this.animationPaint);
        }
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Switch");
        info.setCheckable(true);
        info.setChecked(this.checkBox.isChecked());
        if (this.checkBox.isChecked()) {
            i = NUM;
            str = "NotificationsOn";
        } else {
            i = NUM;
            str = "NotificationsOff";
        }
        info.setContentDescription(LocaleController.getString(str, i));
    }
}
