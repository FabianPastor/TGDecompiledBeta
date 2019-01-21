package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextPriceCell extends FrameLayout {
    private TextView textView;
    private TextView valueTextView;

    public TextPriceCell(Context context) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        setWillNotDraw(false);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -1.0f, i | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 3;
        } else {
            i2 = 5;
        }
        textView.setGravity(i2 | 16);
        view = this.valueTextView;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -1.0f, i3 | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(40.0f));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextValueColor(int color) {
        this.valueTextView.setTextColor(color);
    }

    public void setTextAndValue(String text, String value, boolean bold) {
        this.textView.setText(text);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        if (bold) {
            setTag("windowBackgroundWhiteBlackText");
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        } else {
            setTag("windowBackgroundWhiteGrayText2");
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.textView.setTypeface(Typeface.DEFAULT);
            this.valueTextView.setTypeface(Typeface.DEFAULT);
        }
        requestLayout();
    }
}
