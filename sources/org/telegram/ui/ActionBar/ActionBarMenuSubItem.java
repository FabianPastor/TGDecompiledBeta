package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarMenuSubItem extends FrameLayout {
    private ImageView checkView;
    private int iconColor;
    private ImageView imageView;
    private int selectorColor;
    private int textColor;
    private TextView textView;

    public ActionBarMenuSubItem(Context context) {
        this(context, false);
    }

    public ActionBarMenuSubItem(Context context, boolean z) {
        super(context);
        this.textColor = Theme.getColor("actionBarDefaultSubmenuItem");
        this.iconColor = Theme.getColor("actionBarDefaultSubmenuItemIcon");
        int color = Theme.getColor("dialogButtonSelector");
        this.selectorColor = color;
        setBackground(Theme.createSelectorDrawable(color, 2));
        setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(this.iconColor, PorterDuff.Mode.MULTIPLY));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(3);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextColor(this.textColor);
        this.textView.setTextSize(1, 16.0f);
        addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
        if (z) {
            ImageView imageView3 = new ImageView(context);
            this.checkView = imageView3;
            imageView3.setImageResource(NUM);
            this.checkView.setScaleType(ImageView.ScaleType.CENTER);
            this.checkView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("radioBackgroundChecked"), PorterDuff.Mode.MULTIPLY));
            addView(this.checkView, LayoutHelper.createFrame(26, -1, 19));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    public void setChecked(boolean z) {
        ImageView imageView2 = this.checkView;
        if (imageView2 != null) {
            imageView2.setVisibility(z ? 0 : 4);
        }
    }

    public void setCheckColor(int i) {
        this.checkView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
    }

    public void setTextAndIcon(CharSequence charSequence, int i) {
        this.textView.setText(charSequence);
        if (i == 0 && this.checkView == null) {
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
            return;
        }
        this.imageView.setImageResource(i);
        this.imageView.setVisibility(0);
        this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
    }

    public void setColors(int i, int i2) {
        setTextColor(i);
        setIconColor(i2);
    }

    public void setTextColor(int i) {
        if (this.textColor != i) {
            TextView textView2 = this.textView;
            this.textColor = i;
            textView2.setTextColor(i);
        }
    }

    public void setIconColor(int i) {
        if (this.iconColor != i) {
            ImageView imageView2 = this.imageView;
            this.iconColor = i;
            imageView2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setIcon(int i) {
        this.imageView.setImageResource(i);
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setSelectorColor(int i) {
        if (this.selectorColor != i) {
            this.selectorColor = i;
            setBackground(Theme.createSelectorDrawable(i, 2));
        }
    }
}
