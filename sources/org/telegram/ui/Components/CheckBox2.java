package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxBase;

public class CheckBox2 extends View {
    private CheckBoxBase checkBoxBase;

    public CheckBox2(Context context, int sz) {
        this(context, sz, (Theme.ResourcesProvider) null);
    }

    public CheckBox2(Context context, int sz, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.checkBoxBase = new CheckBoxBase(this, sz, resourcesProvider);
    }

    public void setProgressDelegate(CheckBoxBase.ProgressDelegate delegate) {
        this.checkBoxBase.setProgressDelegate(delegate);
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        this.checkBoxBase.setChecked(num, checked, animated);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBoxBase.setChecked(checked, animated);
    }

    public void setNum(int num) {
        this.checkBoxBase.setNum(num);
    }

    public boolean isChecked() {
        return this.checkBoxBase.isChecked();
    }

    public void setColor(String background, String background2, String check) {
        this.checkBoxBase.setColor(background, background2, check);
    }

    public void setEnabled(boolean enabled) {
        this.checkBoxBase.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public void setDrawUnchecked(boolean value) {
        this.checkBoxBase.setDrawUnchecked(value);
    }

    public void setDrawBackgroundAsArc(int type) {
        this.checkBoxBase.setBackgroundType(type);
    }

    public float getProgress() {
        return this.checkBoxBase.getProgress();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkBoxBase.onAttachedToWindow();
    }

    public void setDuration(long duration) {
        this.checkBoxBase.animationDuration = duration;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.checkBoxBase.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.checkBoxBase.setBounds(0, 0, right - left, bottom - top);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.checkBoxBase.draw(canvas);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CheckBox.class.getName());
        info.setChecked(isChecked());
        info.setCheckable(true);
    }
}
