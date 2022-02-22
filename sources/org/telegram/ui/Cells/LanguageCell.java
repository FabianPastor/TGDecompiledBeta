package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class LanguageCell extends FrameLayout {
    private LocaleController.LocaleInfo currentLocale;
    private int marginEndDp = 23;
    private int marginStartDp = 62;
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView textView2;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LanguageCell(Context context) {
        super(context);
        Context context2 = context;
        if (Theme.dividerPaint == null) {
            Theme.createCommonResources(context);
        }
        int i = 0;
        setWillNotDraw(false);
        RadioButton radioButton2 = new RadioButton(context2);
        this.radioButton = radioButton2;
        radioButton2.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        RadioButton radioButton3 = this.radioButton;
        boolean z = LocaleController.isRTL;
        int i2 = 5;
        addView(radioButton3, LayoutHelper.createFrame(22, 22.0f, (z ? 5 : 3) | 16, (float) (z ? 0 : 20), 0.0f, (float) (z ? 20 : i), 0.0f));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        TextView textView4 = this.textView;
        boolean z2 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-1, -1.0f, (z2 ? 5 : 3) | 48, (float) (z2 ? this.marginEndDp : this.marginStartDp), 0.0f, (float) (z2 ? this.marginStartDp : this.marginEndDp), 17.0f));
        TextView textView5 = new TextView(context2);
        this.textView2 = textView5;
        textView5.setTextColor(Theme.getColor("dialogTextGray3"));
        this.textView2.setTextSize(1, 13.0f);
        this.textView2.setSingleLine(true);
        this.textView2.setEllipsize(TextUtils.TruncateAt.END);
        this.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        TextView textView6 = this.textView2;
        boolean z3 = LocaleController.isRTL;
        addView(textView6, LayoutHelper.createFrame(-1, -1.0f, (!z3 ? 3 : i2) | 48, (float) (z3 ? this.marginEndDp : this.marginStartDp), 20.0f, (float) (z3 ? this.marginStartDp : this.marginEndDp), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setLanguage(LocaleController.LocaleInfo localeInfo, String str, boolean z) {
        TextView textView3 = this.textView;
        if (str == null) {
            str = localeInfo.name;
        }
        textView3.setText(str);
        this.textView2.setText(localeInfo.nameEnglish);
        this.currentLocale = localeInfo;
        this.needDivider = z;
    }

    public void setValue(String str, String str2) {
        this.textView.setText(str);
        this.textView2.setText(str2);
        this.radioButton.setChecked(false, false);
        this.currentLocale = null;
        this.needDivider = false;
    }

    public LocaleController.LocaleInfo getCurrentLocale() {
        return this.currentLocale;
    }

    public void setLanguageSelected(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) (this.marginStartDp - 3)), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) (this.marginStartDp - 3)) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
