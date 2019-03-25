package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextDetailCell extends FrameLayout {
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public TextDetailCell(Context context) {
        int i;
        int i2;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, 23.0f, 8.0f, 23.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        TextView textView2 = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView2.setGravity(i2);
        textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, 23.0f, 33.0f, 23.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(60.0f), NUM));
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!this.needDivider);
    }

    public void setTextWithEmojiAndValue(String text, CharSequence value, boolean divider) {
        boolean z = false;
        this.textView.setText(Emoji.replaceEmoji(text, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
        this.valueTextView.setText(value);
        this.needDivider = divider;
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void invalidate() {
        super.invalidate();
        this.textView.invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
