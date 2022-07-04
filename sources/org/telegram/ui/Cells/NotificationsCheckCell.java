package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class NotificationsCheckCell extends FrameLayout {
    private boolean animationsEnabled;
    private Switch checkBox;
    private int currentHeight;
    private boolean drawLine;
    private boolean isMultiline;
    private ImageView moveImageView;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView valueTextView;

    public NotificationsCheckCell(Context context) {
        this(context, 21, 70, false, (Theme.ResourcesProvider) null);
    }

    public NotificationsCheckCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, 21, 70, false, resourcesProvider2);
    }

    public NotificationsCheckCell(Context context, int padding, int height, boolean reorder) {
        this(context, padding, height, reorder, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NotificationsCheckCell(Context context, int padding, int height, boolean reorder, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        float f;
        float f2;
        float f3;
        float f4;
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.drawLine = true;
        this.resourcesProvider = resourcesProvider3;
        setWillNotDraw(false);
        this.currentHeight = height;
        int i = 5;
        if (reorder) {
            ImageView imageView = new ImageView(context2);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(NUM);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon", resourcesProvider3), PorterDuff.Mode.MULTIPLY));
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider3));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView3 = this.textView;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 80.0f;
        } else {
            f = (float) (reorder ? 64 : padding);
        }
        float f5 = (float) (((this.currentHeight - 70) / 2) + 13);
        if (LocaleController.isRTL) {
            f2 = (float) (reorder ? 64 : padding);
        } else {
            f2 = 80.0f;
        }
        addView(textView3, LayoutHelper.createFrame(-1, -2.0f, i2, f, f5, f2, 0.0f));
        TextView textView4 = new TextView(context2);
        this.valueTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider3));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView5 = this.valueTextView;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f3 = 80.0f;
        } else {
            f3 = (float) (reorder ? 64 : padding);
        }
        float f6 = (float) (((this.currentHeight - 70) / 2) + 38);
        if (LocaleController.isRTL) {
            f4 = (float) (reorder ? 64 : padding);
        } else {
            f4 = 80.0f;
        }
        addView(textView5, LayoutHelper.createFrame(-2, -2.0f, i3, f3, f6, f4, 0.0f));
        Switch switchR = new Switch(context2, resourcesProvider3);
        this.checkBox = switchR;
        switchR.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
        addView(this.checkBox, LayoutHelper.createFrame(37, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        this.checkBox.setFocusable(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.currentHeight), NUM));
        }
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, boolean divider) {
        setTextAndValueAndCheck(text, value, checked, 0, false, divider);
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, int iconType, boolean divider) {
        setTextAndValueAndCheck(text, value, checked, iconType, false, divider);
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, int iconType, boolean multiline, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.checkBox.setChecked(checked, iconType, this.animationsEnabled);
        this.valueTextView.setVisibility(0);
        this.needDivider = divider;
        this.isMultiline = multiline;
        if (multiline) {
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
        this.checkBox.setContentDescription(text);
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        if (this.drawLine) {
            int x = LocaleController.isRTL ? AndroidUtilities.dp(76.0f) : (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - 1;
            int y = (getMeasuredHeight() - AndroidUtilities.dp(22.0f)) / 2;
            canvas.drawRect((float) x, (float) y, (float) (x + 2), (float) (AndroidUtilities.dp(22.0f) + y), Theme.dividerPaint);
        }
    }

    public void setAnimationsEnabled(boolean animationsEnabled2) {
        this.animationsEnabled = animationsEnabled2;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Switch");
        StringBuilder sb = new StringBuilder();
        sb.append(this.textView.getText());
        TextView textView2 = this.valueTextView;
        if (textView2 != null && !TextUtils.isEmpty(textView2.getText())) {
            sb.append("\n");
            sb.append(this.valueTextView.getText());
        }
        info.setContentDescription(sb);
        info.setCheckable(true);
        info.setChecked(this.checkBox.isChecked());
    }
}
