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
import org.telegram.ui.Components.Switch;

public class NotificationsCheckCell extends FrameLayout {
    private Switch checkBox;
    private int currentHeight;
    private boolean drawLine;
    private boolean isMultiline;
    private ImageView moveImageView;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public NotificationsCheckCell(Context context) {
        this(context, 21, 70, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NotificationsCheckCell(Context context, int i, int i2, boolean z) {
        super(context);
        float f;
        float f2;
        float f3;
        Context context2 = context;
        this.drawLine = true;
        setWillNotDraw(false);
        this.currentHeight = i2;
        int i3 = 5;
        if (z) {
            ImageView imageView = new ImageView(context2);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(NUM);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView3 = this.textView;
        boolean z2 = LocaleController.isRTL;
        int i4 = (z2 ? 5 : 3) | 48;
        int i5 = 64;
        float f4 = 80.0f;
        if (z2) {
            f = 80.0f;
        } else {
            f = (float) (z ? 64 : 23);
        }
        float f5 = (float) (((this.currentHeight - 70) / 2) + 13);
        if (z2) {
            f2 = (float) (z ? 64 : 23);
        } else {
            f2 = 80.0f;
        }
        addView(textView3, LayoutHelper.createFrame(-1, -2.0f, i4, f, f5, f2, 0.0f));
        TextView textView4 = new TextView(context2);
        this.valueTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView5 = this.valueTextView;
        boolean z3 = LocaleController.isRTL;
        int i6 = (z3 ? 5 : 3) | 48;
        if (z3) {
            f3 = 80.0f;
        } else {
            f3 = (float) (z ? 64 : 23);
        }
        float f6 = (float) (((this.currentHeight - 70) / 2) + 38);
        if (z3) {
            f4 = (float) (!z ? 23 : i5);
        }
        addView(textView5, LayoutHelper.createFrame(-2, -2.0f, i6, f3, f6, f4, 0.0f));
        Switch switchR = new Switch(context2);
        this.checkBox = switchR;
        switchR.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
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
