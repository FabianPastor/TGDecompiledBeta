package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
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

    public DialogRadioCell(Context context, boolean z) {
        super(context);
        this.textView = new TextView(context);
        if (z) {
            this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        } else {
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 23.0f : 61.0f, 0.0f, LocaleController.isRTL ? 61.0f : 23.0f, 0.0f));
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        if (z) {
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        } else {
            this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
        }
        RadioButton radioButton = this.radioButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(radioButton, LayoutHelper.createFrame(22, 22.0f, i | 48, 20.0f, 15.0f, 20.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + this.needDivider);
        i = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.radioButton.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(String str, boolean z, boolean z2) {
        this.textView.setText(str);
        this.radioButton.setChecked(z, false);
        this.needDivider = z2;
        setWillNotDraw(z2 ^ 1);
    }

    public boolean isChecked() {
        return this.radioButton.isChecked();
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView = this.textView;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            String str = "alpha";
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
            RadioButton radioButton = this.radioButton;
            float[] fArr2 = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(radioButton, str, fArr2));
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        RadioButton radioButton2 = this.radioButton;
        if (!z) {
            f = 0.5f;
        }
        radioButton2.setAlpha(f);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            float f = 0.0f;
            float dp = (float) AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f);
            float height = (float) (getHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = 60.0f;
            }
            canvas.drawLine(dp, height, (float) (measuredWidth - AndroidUtilities.dp(f)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
