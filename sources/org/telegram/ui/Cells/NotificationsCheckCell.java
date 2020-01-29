package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class NotificationsCheckCell extends FrameLayout {
    private Switch checkBox;
    private int currentHeight;
    private boolean drawLine;
    private boolean isMultiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public NotificationsCheckCell(Context context) {
        this(context, 21, 70);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NotificationsCheckCell(Context context, int i, int i2) {
        super(context);
        Context context2 = context;
        this.drawLine = true;
        setWillNotDraw(false);
        this.currentHeight = i2;
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i3 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 23.0f, (float) (((this.currentHeight - 70) / 2) + 13), LocaleController.isRTL ? 23.0f : 80.0f, 0.0f));
        this.valueTextView = new TextView(context2);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 23.0f, (float) (((this.currentHeight - 70) / 2) + 38), LocaleController.isRTL ? 23.0f : 80.0f, 0.0f));
        this.checkBox = new Switch(context2);
        this.checkBox.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
        addView(this.checkBox, LayoutHelper.createFrame(37, 40.0f, (LocaleController.isRTL ? 3 : i3) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        this.checkBox.setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.currentHeight), NUM));
        }
    }

    public void setTextAndValueAndCheck(String str, CharSequence charSequence, boolean z, boolean z2) {
        setTextAndValueAndCheck(str, charSequence, z, 0, false, z2);
    }

    public void setTextAndValueAndCheck(String str, CharSequence charSequence, boolean z, int i, boolean z2) {
        setTextAndValueAndCheck(str, charSequence, z, i, false, z2);
    }

    public void setTextAndValueAndCheck(String str, CharSequence charSequence, boolean z, int i, boolean z2, boolean z3) {
        this.textView.setText(str);
        this.valueTextView.setText(charSequence);
        this.checkBox.setChecked(z, i, false);
        this.valueTextView.setVisibility(0);
        this.needDivider = z3;
        this.isMultiline = z2;
        if (z2) {
            this.valueTextView.setLines(0);
            this.valueTextView.setMaxLines(0);
            this.valueTextView.setSingleLine(false);
            this.valueTextView.setEllipsize((TextUtils.TruncateAt) null);
            this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        } else {
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
        }
        this.checkBox.setContentDescription(str);
    }

    public void setDrawLine(boolean z) {
        this.drawLine = z;
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public void setChecked(boolean z, int i) {
        this.checkBox.setChecked(z, i, true);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        if (this.drawLine) {
            int dp = LocaleController.isRTL ? AndroidUtilities.dp(76.0f) : (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - 1;
            int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(22.0f)) / 2;
            canvas.drawRect((float) dp, (float) measuredHeight, (float) (dp + 2), (float) (measuredHeight + AndroidUtilities.dp(22.0f)), Theme.dividerPaint);
        }
    }
}
