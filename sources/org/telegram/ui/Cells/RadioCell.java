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

public class RadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;

    public RadioCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
        context = this.radioButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        int i2 = i | 48;
        int i3 = 0;
        float f = (float) (LocaleController.isRTL ? 18 : 0);
        if (!LocaleController.isRTL) {
            i3 = 18;
        }
        addView(context, LayoutHelper.createFrame(22, 22.0f, i2, f, 13.0f, (float) i3, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM) + this.needDivider);
        i = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(NUM);
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
        float f = 0.5f;
        if (arrayList != null) {
            TextView textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
            RadioButton radioButton = this.radioButton;
            str = "alpha";
            float[] fArr2 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(radioButton, str, fArr2));
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        arrayList = this.radioButton;
        if (z) {
            f = 1.0f;
        }
        arrayList.setAlpha(f);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
