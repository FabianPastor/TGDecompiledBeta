package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class DialogRadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;

    public DialogRadioCell(Context context) {
        this(context, false);
    }

    public DialogRadioCell(Context context, boolean dialog) {
        int i;
        int i2;
        int i3 = 5;
        super(context);
        this.textView = new TextView(context);
        if (dialog) {
            this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        } else {
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, LocaleController.isRTL ? 23.0f : 61.0f, 0.0f, LocaleController.isRTL ? 61.0f : 23.0f, 0.0f));
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        if (dialog) {
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        } else {
            this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
        }
        View view2 = this.radioButton;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        addView(view2, LayoutHelper.createFrame(22, 22.0f, i3 | 48, 20.0f, 15.0f, 20.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + AndroidUtilities.dp(50.0f));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.radioButton.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(availableWidth, NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, boolean checked, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.radioButton.setChecked(checked, false);
        this.needDivider = divider;
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public boolean isChecked() {
        return this.radioButton.isChecked();
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        TextView textView;
        RadioButton radioButton;
        if (animators != null) {
            textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            radioButton = this.radioButton;
            String str2 = "alpha";
            float[] fArr2 = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr2[0] = f;
            animators.add(ObjectAnimator.ofFloat(radioButton, str2, fArr2));
            return;
        }
        float f2;
        textView = this.textView;
        if (value) {
            f2 = 1.0f;
        } else {
            f2 = 0.5f;
        }
        textView.setAlpha(f2);
        radioButton = this.radioButton;
        if (!value) {
            f = 0.5f;
        }
        radioButton.setAlpha(f);
    }

    protected void onDraw(Canvas canvas) {
        float f = 60.0f;
        if (this.needDivider) {
            float f2;
            if (LocaleController.isRTL) {
                f2 = 0.0f;
            } else {
                f2 = 60.0f;
            }
            float dp = (float) AndroidUtilities.dp(f2);
            float height = (float) (getHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (!LocaleController.isRTL) {
                f = 0.0f;
            }
            canvas.drawLine(dp, height, (float) (measuredWidth - AndroidUtilities.dp(f)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
