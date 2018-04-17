package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class LanguageCell extends FrameLayout {
    private ImageView checkImage;
    private LocaleInfo currentLocale;
    private boolean isDialog;
    private boolean needDivider;
    private TextView textView;
    private TextView textView2;

    public LanguageCell(Context context, boolean dialog) {
        float f;
        float f2;
        Context context2 = context;
        boolean z = dialog;
        super(context);
        setWillNotDraw(false);
        this.isDialog = z;
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        r0.textView.setTextSize(1, 16.0f);
        r0.textView.setLines(1);
        r0.textView.setMaxLines(1);
        r0.textView.setSingleLine(true);
        r0.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        r0.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = r0.textView;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i3 = 23;
        if (LocaleController.isRTL) {
            f = 71.0f;
        } else {
            f = (float) (z ? 23 : 16);
        }
        float f3 = (float) (r0.isDialog ? 4 : 6);
        if (LocaleController.isRTL) {
            f2 = (float) (z ? 23 : 16);
        } else {
            f2 = 71.0f;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2, f, f3, f2, 0.0f));
        r0.textView2 = new TextView(context2);
        r0.textView2.setTextColor(Theme.getColor(z ? Theme.key_dialogTextGray3 : Theme.key_windowBackgroundWhiteGrayText3));
        r0.textView2.setTextSize(1, 13.0f);
        r0.textView2.setLines(1);
        r0.textView2.setMaxLines(1);
        r0.textView2.setSingleLine(true);
        r0.textView2.setEllipsize(TruncateAt.END);
        r0.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        view = r0.textView2;
        i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 71.0f;
        } else {
            f = (float) (z ? 23 : 16);
        }
        f3 = (float) (r0.isDialog ? 25 : 28);
        if (LocaleController.isRTL) {
            if (!z) {
                i3 = 16;
            }
            f2 = (float) i3;
        } else {
            f2 = 71.0f;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2, f, f3, f2, 0.0f));
        r0.checkImage = new ImageView(context2);
        r0.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        r0.checkImage.setImageResource(R.drawable.sticker_added);
        view = r0.checkImage;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(19, 14.0f, i | 16, 23.0f, 0.0f, 23.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.isDialog ? 48.0f : 54.0f) + this.needDivider, NUM));
    }

    public void setLanguage(LocaleInfo language, String desc, boolean divider) {
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

    public LocaleInfo getCurrentLocale() {
        return this.currentLocale;
    }

    public void setLanguageSelected(boolean value) {
        this.checkImage.setVisibility(value ? 0 : 4);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
