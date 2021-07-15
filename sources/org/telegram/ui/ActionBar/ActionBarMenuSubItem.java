package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarMenuSubItem extends FrameLayout {
    boolean bottom;
    private ImageView checkView;
    private int iconColor;
    private ImageView imageView;
    private int itemHeight;
    private int selectorColor;
    private TextView subtextView;
    private int textColor;
    private TextView textView;
    boolean top;

    public ActionBarMenuSubItem(Context context, boolean z, boolean z2) {
        this(context, false, z, z2);
    }

    public ActionBarMenuSubItem(Context context, boolean z, boolean z2, boolean z3) {
        super(context);
        this.textColor = Theme.getColor("actionBarDefaultSubmenuItem");
        this.iconColor = Theme.getColor("actionBarDefaultSubmenuItemIcon");
        this.selectorColor = Theme.getColor("dialogButtonSelector");
        this.itemHeight = 48;
        this.top = z2;
        this.bottom = z3;
        updateBackground();
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
        addView(this.textView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
        if (z) {
            ImageView imageView3 = new ImageView(context);
            this.checkView = imageView3;
            imageView3.setImageResource(NUM);
            this.checkView.setScaleType(ImageView.ScaleType.CENTER);
            this.checkView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("radioBackgroundChecked"), PorterDuff.Mode.MULTIPLY));
            addView(this.checkView, LayoutHelper.createFrame(26, -1, (!LocaleController.isRTL ? 3 : i) | 16));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.itemHeight), NUM));
    }

    public void setItemHeight(int i) {
        this.itemHeight = i;
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
        setTextAndIcon(charSequence, i, (Drawable) null);
    }

    public void setTextAndIcon(CharSequence charSequence, int i, Drawable drawable) {
        this.textView.setText(charSequence);
        if (i == 0 && drawable == null && this.checkView == null) {
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
            return;
        }
        if (drawable != null) {
            this.imageView.setImageDrawable(drawable);
        } else {
            this.imageView.setImageResource(i);
        }
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

    public void setSubtextColor(int i) {
        this.subtextView.setTextColor(i);
    }

    public void setSubtext(String str) {
        int i = 8;
        boolean z = true;
        int i2 = 0;
        if (this.subtextView == null) {
            TextView textView2 = new TextView(getContext());
            this.subtextView = textView2;
            textView2.setLines(1);
            this.subtextView.setSingleLine(true);
            int i3 = 3;
            this.subtextView.setGravity(3);
            this.subtextView.setEllipsize(TextUtils.TruncateAt.END);
            this.subtextView.setTextColor(-8617338);
            this.subtextView.setVisibility(8);
            this.subtextView.setTextSize(1, 13.0f);
            this.subtextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
            TextView textView3 = this.subtextView;
            if (LocaleController.isRTL) {
                i3 = 5;
            }
            addView(textView3, LayoutHelper.createFrame(-2, -2.0f, i3 | 16, 0.0f, 10.0f, 0.0f, 0.0f));
        }
        boolean z2 = !TextUtils.isEmpty(str);
        if (this.subtextView.getVisibility() != 0) {
            z = false;
        }
        if (z2 != z) {
            TextView textView4 = this.subtextView;
            if (z2) {
                i = 0;
            }
            textView4.setVisibility(i);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
            if (z2) {
                i2 = AndroidUtilities.dp(10.0f);
            }
            layoutParams.bottomMargin = i2;
            this.textView.setLayoutParams(layoutParams);
        }
        this.subtextView.setText(str);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public void setSelectorColor(int i) {
        if (this.selectorColor != i) {
            this.selectorColor = i;
            updateBackground();
        }
    }

    public void updateSelectorBackground(boolean z, boolean z2) {
        if (this.top != z || this.bottom != z2) {
            this.top = z;
            this.bottom = z2;
            updateBackground();
        }
    }

    private void updateBackground() {
        int i = 6;
        int i2 = this.top ? 6 : 0;
        if (!this.bottom) {
            i = 0;
        }
        setBackground(Theme.createRadSelectorDrawable(this.selectorColor, i2, i));
    }
}
