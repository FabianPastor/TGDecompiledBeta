package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioColorCell extends FrameLayout {
    private RadioButton radioButton;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public RadioColorCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public RadioColorCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        RadioButton radioButton2 = new RadioButton(context);
        this.radioButton = radioButton2;
        radioButton2.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(getThemedColor("dialogRadioBackground"), getThemedColor("dialogRadioBackgroundChecked"));
        int i = 5;
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 18), 14.0f, (float) (LocaleController.isRTL ? 18 : 0), 0.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(getThemedColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 21 : 51), 13.0f, (float) (LocaleController.isRTL ? 51 : 21), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
    }

    public void setCheckColor(int color1, int color2) {
        this.radioButton.setColor(color1, color2);
    }

    public void setTextAndValue(String text, boolean checked) {
        this.textView.setText(text);
        this.radioButton.setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.RadioButton");
        info.setCheckable(true);
        info.setChecked(this.radioButton.isChecked());
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
