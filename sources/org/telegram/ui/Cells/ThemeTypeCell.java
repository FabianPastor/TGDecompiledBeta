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
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeTypeCell extends FrameLayout {
    private ImageView checkImage;
    private boolean needDivider;
    private TextView textView;

    public ThemeTypeCell(Context context) {
        super(context);
        setWillNotDraw(false);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = 17.0f;
        float f2 = LocaleController.isRTL ? 71.0f : 17.0f;
        if (!LocaleController.isRTL) {
            f = 23.0f;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2, f2, 0.0f, f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.checkImage.setImageResource(R.drawable.sticker_added);
        view = this.checkImage;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(19, 14.0f, i | 16, 23.0f, 0.0f, 23.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + this.needDivider, NUM));
    }

    public void setValue(String name, boolean checked, boolean divider) {
        this.textView.setText(name);
        this.checkImage.setVisibility(checked ? 0 : 4);
        this.needDivider = divider;
    }

    public void setTypeChecked(boolean value) {
        this.checkImage.setVisibility(value ? 0 : 4);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
