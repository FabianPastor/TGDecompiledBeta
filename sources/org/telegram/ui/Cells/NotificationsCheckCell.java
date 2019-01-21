package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class NotificationsCheckCell extends FrameLayout {
    private Switch checkBox;
    private boolean drawLine;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public NotificationsCheckCell(Context context) {
        this(context, 21);
    }

    public NotificationsCheckCell(Context context, int padding) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        this.drawLine = true;
        setWillNotDraw(false);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TruncateAt.END);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i | 48, LocaleController.isRTL ? 80.0f : 23.0f, 13.0f, LocaleController.isRTL ? 23.0f : 80.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TruncateAt.END);
        view = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, LocaleController.isRTL ? 80.0f : 23.0f, 38.0f, LocaleController.isRTL ? 23.0f : 80.0f, 0.0f));
        this.checkBox = new Switch(context);
        this.checkBox.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
        View view2 = this.checkBox;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(view2, LayoutHelper.createFrame(37, 40.0f, i3 | 16, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70.0f), NUM));
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, boolean divider) {
        setTextAndValueAndCheck(text, value, checked, 0, divider);
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, int iconType, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.checkBox.setChecked(checked, iconType, false);
        this.valueTextView.setVisibility(0);
        this.needDivider = divider;
    }

    public void setDrawLine(boolean value) {
        this.drawLine = value;
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked, true);
    }

    public void setChecked(boolean checked, int iconType) {
        this.checkBox.setChecked(checked, iconType, true);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        if (this.drawLine) {
            int x = LocaleController.isRTL ? AndroidUtilities.dp(76.0f) : (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - 1;
            canvas.drawRect((float) x, (float) AndroidUtilities.dp(24.0f), (float) (x + 2), (float) AndroidUtilities.dp(46.0f), Theme.dividerPaint);
        }
    }
}
