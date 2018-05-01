package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextPriceCell extends FrameLayout {
    private int dotLength;
    private String dotstring;
    private TextView textView;
    private TextView valueTextView;

    protected void onDraw(Canvas canvas) {
    }

    public TextPriceCell(Context context) {
        super(context);
        this.dotstring = LocaleController.isRTL ? " ." : ". ";
        setWillNotDraw(false);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        context = this.valueTextView;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(-2, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM));
        i = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(NUM);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(i / 2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec((i - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(NUM), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.dotLength = (int) Math.ceil((double) this.textView.getPaint().measureText(this.dotstring));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextValueColor(int i) {
        this.valueTextView.setTextColor(i);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        this.textView.setText(str);
        if (str2 != null) {
            this.valueTextView.setText(str2);
            this.valueTextView.setVisibility(null);
        } else {
            this.valueTextView.setVisibility(4);
        }
        if (z) {
            setTag(Theme.key_windowBackgroundWhiteBlackText);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        } else {
            setTag(Theme.key_windowBackgroundWhiteGrayText2);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.textView.setTypeface(Typeface.DEFAULT);
            this.valueTextView.setTypeface(Typeface.DEFAULT);
        }
        requestLayout();
    }
}
