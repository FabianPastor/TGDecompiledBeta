package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import org.telegram.ui.Components.CheckBoxBase;

public class CheckBox2 extends View {
    private CheckBoxBase checkBoxBase;

    public CheckBox2(Context context, int i) {
        super(context);
        this.checkBoxBase = new CheckBoxBase(this, i);
    }

    public void setProgressDelegate(CheckBoxBase.ProgressDelegate progressDelegate) {
        this.checkBoxBase.setProgressDelegate(progressDelegate);
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.checkBoxBase.setChecked(i, z, z2);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBoxBase.setChecked(z, z2);
    }

    public void setNum(int i) {
        this.checkBoxBase.setNum(i);
    }

    public boolean isChecked() {
        return this.checkBoxBase.isChecked();
    }

    public void setColor(String str, String str2, String str3) {
        this.checkBoxBase.setColor(str, str2, str3);
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkBoxBase.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.checkBoxBase.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.checkBoxBase.setBounds(0, 0, i3 - i, i4 - i2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.checkBoxBase.draw(canvas);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(CheckBox.class.getName());
        accessibilityNodeInfo.setChecked(isChecked());
        accessibilityNodeInfo.setCheckable(true);
    }
}
