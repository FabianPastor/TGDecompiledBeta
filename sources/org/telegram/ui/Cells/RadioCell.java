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
/* loaded from: classes3.dex */
public class RadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public RadioCell(Context context) {
        this(context, false, 21);
    }

    public RadioCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, false, 21, resourcesProvider);
    }

    public RadioCell(Context context, boolean z, int i) {
        this(context, z, i, null);
    }

    public RadioCell(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.textView = textView;
        if (z) {
            textView.setTextColor(Theme.getColor("dialogTextBlack", resourcesProvider));
        } else {
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i2 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        float f = i;
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, f, 0.0f, f, 0.0f));
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        if (z) {
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground", resourcesProvider), Theme.getColor("dialogRadioBackgroundChecked", resourcesProvider));
        } else {
            this.radioButton.setColor(Theme.getColor("radioBackground", resourcesProvider), Theme.getColor("radioBackgroundChecked", resourcesProvider));
        }
        RadioButton radioButton2 = this.radioButton;
        boolean z2 = LocaleController.isRTL;
        addView(radioButton2, LayoutHelper.createFrame(22, 22.0f, (z2 ? 3 : i2) | 48, z2 ? i + 1 : 0, 14.0f, !z2 ? i + 1 : 0, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(String str, boolean z, boolean z2) {
        this.textView.setText(str);
        this.radioButton.setChecked(z, false);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public boolean isChecked() {
        return this.radioButton.isChecked();
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        super.setEnabled(z);
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView = this.textView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(textView, property, fArr));
            RadioButton radioButton = this.radioButton;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(radioButton, property2, fArr2));
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        RadioButton radioButton2 = this.radioButton;
        if (!z) {
            f = 0.5f;
        }
        radioButton2.setAlpha(f);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.RadioButton");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(isChecked());
    }
}
