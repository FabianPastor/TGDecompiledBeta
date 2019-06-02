package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import org.telegram.ui.Components.CheckBoxBase.ProgressDelegate;

public class CheckBox2 extends View {
    private CheckBoxBase checkBoxBase = new CheckBoxBase(this);

    public CheckBox2(Context context) {
        super(context);
    }

    public void setProgressDelegate(ProgressDelegate progressDelegate) {
        this.checkBoxBase.setProgressDelegate(progressDelegate);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBoxBase.setChecked(z, z2);
    }

    public boolean isChecked() {
        return this.checkBoxBase.isChecked();
    }

    public void setColor(String str, String str2, String str3) {
        this.checkBoxBase.setColor(str, str2, str3);
    }

    public void setSize(int i) {
        this.checkBoxBase.setSize(i);
    }

    public void setEnabled(boolean z) {
        this.checkBoxBase.setEnabled(z);
        super.setEnabled(z);
    }

    public void setDrawUnchecked(boolean z) {
        this.checkBoxBase.setDrawUnchecked(z);
    }

    public void setDrawBackgroundAsArc(int i) {
        this.checkBoxBase.setDrawBackgroundAsArc(i);
    }

    public float getProgress() {
        return this.checkBoxBase.getProgress();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkBoxBase.onAttachedToWindow();
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.checkBoxBase.onDetachedFromWindow();
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.checkBoxBase.setBounds(0, 0, i3 - i, i4 - i2);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        this.checkBoxBase.draw(canvas);
    }
}
