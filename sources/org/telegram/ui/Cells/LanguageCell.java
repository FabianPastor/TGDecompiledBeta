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
        int i2 = 5;
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 20), 0.0f, (float) (LocaleController.isRTL ? 20 : i), 0.0f));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? this.marginEndDp : this.marginStartDp), 0.0f, (float) (LocaleController.isRTL ? this.marginStartDp : this.marginEndDp), 17.0f));
        TextView textView4 = new TextView(context2);
        this.textView2 = textView4;
        textView4.setTextColor(Theme.getColor("dialogTextGray3"));
        this.textView2.setTextSize(1, 13.0f);
        this.textView2.setSingleLine(true);
        this.textView2.setEllipsize(TextUtils.TruncateAt.END);
        this.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView2, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i2) | 48, (float) (LocaleController.isRTL ? this.marginEndDp : this.marginStartDp), 20.0f, (float) (LocaleController.isRTL ? this.marginStartDp : this.marginEndDp), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setLanguage(LocaleController.LocaleInfo language, String desc, boolean divider) {
        this.textView.setText(desc != null ? desc : language.name);
        this.textView2.setText(language.nameEnglish);
        this.currentLocale = language;
        this.needDivider = divider;
    }

    public void setValue(String name, String nameEnglish) {
        this.textView.setText(name);
        this.textView2.setText(nameEnglish);
        this.radioButton.setChecked(false, false);
        this.currentLocale = null;
        this.needDivider = false;
    }

    public LocaleController.LocaleInfo getCurrentLocale() {
        return this.currentLocale;
    }

    public void setLanguageSelected(boolean value, boolean animated) {
        this.radioButton.setChecked(value, animated);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) (this.marginStartDp - 3)), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) (this.marginStartDp - 3)) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
