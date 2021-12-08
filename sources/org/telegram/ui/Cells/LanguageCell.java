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
    public LanguageCell(Context context, boolean dialog) {
        super(context);
        Context context2 = context;
        boolean z = dialog;
        if (Theme.dividerPaint == null) {
            Theme.createCommonResources(context);
        }
        setWillNotDraw(false);
        this.isDialog = z;
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor(z ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 23.0f, (float) (this.isDialog ? 4 : 7), LocaleController.isRTL ? 23.0f : 71.0f, 0.0f));
        TextView textView4 = new TextView(context2);
        this.textView2 = textView4;
        textView4.setTextColor(Theme.getColor(z ? "dialogTextGray3" : "windowBackgroundWhiteGrayText3"));
        this.textView2.setTextSize(1, 13.0f);
        this.textView2.setLines(1);
        this.textView2.setMaxLines(1);
        this.textView2.setSingleLine(true);
        this.textView2.setEllipsize(TextUtils.TruncateAt.END);
        this.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.textView2, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 23.0f, (float) (this.isDialog ? 25 : 29), LocaleController.isRTL ? 23.0f : 71.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.checkImage = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(NUM);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 23.0f, 0.0f, 23.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.isDialog ? 50.0f : 54.0f) + (this.needDivider ? 1 : 0), NUM));
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
        this.checkImage.setVisibility(4);
        this.currentLocale = null;
        this.needDivider = false;
    }

    public LocaleController.LocaleInfo getCurrentLocale() {
        return this.currentLocale;
    }

    public void setLanguageSelected(boolean value) {
        this.checkImage.setVisibility(value ? 0 : 4);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
