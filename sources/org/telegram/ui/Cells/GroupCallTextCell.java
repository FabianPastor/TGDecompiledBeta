package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class GroupCallTextCell extends FrameLayout {
    private Paint dividerPaint;
    private int imageLeft;
    private ImageView imageView;
    private int leftPadding;
    private boolean needDivider;
    private int offsetFromImage;
    private SimpleTextView textView;
    private ImageView valueImageView;
    private SimpleTextView valueTextView;

    public GroupCallTextCell(Context context) {
        this(context, 23, false);
    }

    public GroupCallTextCell(Context context, int i, boolean z) {
        super(context);
        this.offsetFromImage = 67;
        this.imageLeft = 18;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.leftPadding = i;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(z ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(16);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setImportantForAccessibility(2);
        addView(this.textView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.valueTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(z ? "dialogTextBlue2" : "windowBackgroundWhiteValueText"));
        this.valueTextView.setTextSize(16);
        this.valueTextView.setGravity(LocaleController.isRTL ? 3 : i2);
        this.valueTextView.setImportantForAccessibility(2);
        addView(this.valueTextView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? "dialogIcon" : "windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
        ImageView imageView3 = new ImageView(context);
        this.valueImageView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.valueImageView);
        setFocusable(true);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    public ImageView getValueImageView() {
        return this.valueImageView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp((float) this.leftPadding), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec((size - AndroidUtilities.dp((float) (this.leftPadding + 71))) - this.valueTextView.getTextWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        }
        setMeasuredDimension(size, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6 = i4 - i2;
        int i7 = i3 - i;
        int textHeight = (i6 - this.valueTextView.getTextHeight()) / 2;
        int dp = LocaleController.isRTL ? AndroidUtilities.dp((float) this.leftPadding) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(dp, textHeight, simpleTextView.getMeasuredWidth() + dp, this.valueTextView.getMeasuredHeight() + textHeight);
        int textHeight2 = (i6 - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            i5 = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp((float) (this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding));
        } else {
            i5 = AndroidUtilities.dp((float) (this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding));
        }
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(i5, textHeight2, simpleTextView2.getMeasuredWidth() + i5, this.textView.getMeasuredHeight() + textHeight2);
        if (this.imageView.getVisibility() == 0) {
            int dp2 = AndroidUtilities.dp(5.0f);
            int dp3 = !LocaleController.isRTL ? AndroidUtilities.dp((float) this.imageLeft) : (i7 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp((float) this.imageLeft);
            ImageView imageView2 = this.imageView;
            imageView2.layout(dp3, dp2, imageView2.getMeasuredWidth() + dp3, this.imageView.getMeasuredHeight() + dp2);
        }
        if (this.valueImageView.getVisibility() == 0) {
            int measuredHeight = (i6 - this.valueImageView.getMeasuredHeight()) / 2;
            int dp4 = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (i7 - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(23.0f);
            ImageView imageView3 = this.valueImageView;
            imageView3.layout(dp4, measuredHeight, imageView3.getMeasuredWidth() + dp4, this.valueImageView.getMeasuredHeight() + measuredHeight);
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setColors(String str, String str2) {
        this.textView.setTextColor(Theme.getColor(str2));
        this.textView.setTag(str2);
        if (str != null) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
            this.imageView.setTag(str);
        }
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setImageResource(i);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setOffsetFromImage(int i) {
        this.offsetFromImage = i;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        int i;
        if (this.needDivider) {
            float f2 = 68.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = (float) AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 68.0f : 20.0f);
            }
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() != 0) {
                    f2 = 20.0f;
                }
                i = AndroidUtilities.dp(f2);
            } else {
                i = 0;
            }
            canvas.drawLine(f, measuredHeight, (float) (measuredWidth - i), (float) (getMeasuredHeight() - 1), this.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CharSequence text = this.textView.getText();
        if (!TextUtils.isEmpty(text)) {
            CharSequence text2 = this.valueTextView.getText();
            if (!TextUtils.isEmpty(text2)) {
                accessibilityNodeInfo.setText(text + ": " + text2);
                return;
            }
            accessibilityNodeInfo.setText(text);
        }
    }
}
