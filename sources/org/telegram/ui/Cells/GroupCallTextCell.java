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
/* loaded from: classes3.dex */
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
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? "dialogIcon" : "windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
        ImageView imageView2 = new ImageView(context);
        this.valueImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(this.leftPadding), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec((size - AndroidUtilities.dp(this.leftPadding + 71)) - this.valueTextView.getTextWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        }
        setMeasuredDimension(size, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int dp;
        int i5 = i4 - i2;
        int i6 = i3 - i;
        int textHeight = (i5 - this.valueTextView.getTextHeight()) / 2;
        int dp2 = LocaleController.isRTL ? AndroidUtilities.dp(this.leftPadding) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(dp2, textHeight, simpleTextView.getMeasuredWidth() + dp2, this.valueTextView.getMeasuredHeight() + textHeight);
        int textHeight2 = (i5 - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            dp = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding);
        } else {
            dp = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding);
        }
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(dp, textHeight2, simpleTextView2.getMeasuredWidth() + dp, this.textView.getMeasuredHeight() + textHeight2);
        if (this.imageView.getVisibility() == 0) {
            int dp3 = AndroidUtilities.dp(5.0f);
            int dp4 = !LocaleController.isRTL ? AndroidUtilities.dp(this.imageLeft) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageLeft);
            ImageView imageView = this.imageView;
            imageView.layout(dp4, dp3, imageView.getMeasuredWidth() + dp4, this.imageView.getMeasuredHeight() + dp3);
        }
        if (this.valueImageView.getVisibility() == 0) {
            int measuredHeight = (i5 - this.valueImageView.getMeasuredHeight()) / 2;
            int dp5 = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (i6 - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(23.0f);
            ImageView imageView2 = this.valueImageView;
            imageView2.layout(dp5, measuredHeight, imageView2.getMeasuredWidth() + dp5, this.valueImageView.getMeasuredHeight() + measuredHeight);
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setColors(int i, int i2) {
        this.textView.setTextColor(i2);
        this.textView.setTag(null);
        this.imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        this.imageView.setTag(null);
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
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

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float dp;
        int i;
        if (this.needDivider) {
            float f = 68.0f;
            if (LocaleController.isRTL) {
                dp = 0.0f;
            } else {
                dp = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 68.0f : 20.0f);
            }
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() != 0) {
                    f = 20.0f;
                }
                i = AndroidUtilities.dp(f);
            } else {
                i = 0;
            }
            canvas.drawLine(dp, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, this.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CharSequence text = this.textView.getText();
        if (!TextUtils.isEmpty(text)) {
            CharSequence text2 = this.valueTextView.getText();
            if (!TextUtils.isEmpty(text2)) {
                accessibilityNodeInfo.setText(((Object) text) + ": " + ((Object) text2));
                return;
            }
            accessibilityNodeInfo.setText(text);
        }
    }
}
