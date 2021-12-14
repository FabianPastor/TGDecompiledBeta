package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class LanguageCell extends FrameLayout {
    private ImageView checkImage;
    private LocaleController.LocaleInfo currentLocale;
    private boolean isDialog;
    private boolean needDivider;
    private TextView textView;
    private TextView textView2;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LanguageCell(Context context, boolean z) {
        super(context);
        Context context2 = context;
        boolean z2 = z;
        if (Theme.dividerPaint == null) {
            Theme.createCommonResources(context);
        }
        setWillNotDraw(false);
        this.isDialog = z2;
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor(z2 ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView4 = this.textView;
        boolean z3 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-1, -1.0f, (z3 ? 5 : 3) | 48, z3 ? 71.0f : 23.0f, (float) (this.isDialog ? 4 : 7), z3 ? 23.0f : 71.0f, 0.0f));
        TextView textView5 = new TextView(context2);
        this.textView2 = textView5;
        textView5.setTextColor(Theme.getColor(z2 ? "dialogTextGray3" : "windowBackgroundWhiteGrayText3"));
        this.textView2.setTextSize(1, 13.0f);
        this.textView2.setLines(1);
        this.textView2.setMaxLines(1);
        this.textView2.setSingleLine(true);
        this.textView2.setEllipsize(TextUtils.TruncateAt.END);
        this.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView6 = this.textView2;
        boolean z4 = LocaleController.isRTL;
        addView(textView6, LayoutHelper.createFrame(-1, -1.0f, (z4 ? 5 : 3) | 48, z4 ? 71.0f : 23.0f, (float) (this.isDialog ? 25 : 29), z4 ? 23.0f : 71.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.checkImage = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(NUM);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 23.0f, 0.0f, 23.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.isDialog ? 50.0f : 54.0f) + (this.needDivider ? 1 : 0), NUM));
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
        this.checkImage.setVisibility(4);
        this.currentLocale = null;
        this.needDivider = false;
    }

    public LocaleController.LocaleInfo getCurrentLocale() {
        return this.currentLocale;
    }

    public void setLanguageSelected(boolean z) {
        this.checkImage.setVisibility(z ? 0 : 4);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
