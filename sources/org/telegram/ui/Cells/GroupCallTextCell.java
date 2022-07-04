package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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

    public GroupCallTextCell(Context context, int left, boolean dialog) {
        super(context);
        this.offsetFromImage = 67;
        this.imageLeft = 18;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.leftPadding = left;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(dialog ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(16);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setImportantForAccessibility(2);
        addView(this.textView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.valueTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(dialog ? "dialogTextBlue2" : "windowBackgroundWhiteValueText"));
        this.valueTextView.setTextSize(16);
        this.valueTextView.setGravity(LocaleController.isRTL ? 3 : i);
        this.valueTextView.setImportantForAccessibility(2);
        addView(this.valueTextView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(dialog ? "dialogIcon" : "windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp((float) this.leftPadding), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec((width - AndroidUtilities.dp((float) (this.leftPadding + 71))) - this.valueTextView.getTextWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        }
        setMeasuredDimension(width, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewLeft;
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.valueTextView.getTextHeight()) / 2;
        int viewLeft2 = LocaleController.isRTL ? AndroidUtilities.dp((float) this.leftPadding) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(viewLeft2, viewTop, simpleTextView.getMeasuredWidth() + viewLeft2, this.valueTextView.getMeasuredHeight() + viewTop);
        int viewTop2 = (height - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL != 0) {
            viewLeft = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp((float) (this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding));
        } else {
            viewLeft = AndroidUtilities.dp((float) (this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding));
        }
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(viewLeft, viewTop2, simpleTextView2.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop2);
        if (this.imageView.getVisibility() == 0) {
            int viewTop3 = AndroidUtilities.dp(5.0f);
            int viewLeft3 = !LocaleController.isRTL ? AndroidUtilities.dp((float) this.imageLeft) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp((float) this.imageLeft);
            ImageView imageView2 = this.imageView;
            imageView2.layout(viewLeft3, viewTop3, imageView2.getMeasuredWidth() + viewLeft3, this.imageView.getMeasuredHeight() + viewTop3);
        }
        if (this.valueImageView.getVisibility() == 0) {
            int viewTop4 = (height - this.valueImageView.getMeasuredHeight()) / 2;
            int viewLeft4 = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (width - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(23.0f);
            ImageView imageView3 = this.valueImageView;
            imageView3.layout(viewLeft4, viewTop4, imageView3.getMeasuredWidth() + viewLeft4, this.valueImageView.getMeasuredHeight() + viewTop4);
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setColors(String icon, String text) {
        this.textView.setTextColor(Theme.getColor(text));
        this.textView.setTag(text);
        if (icon != null) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(icon), PorterDuff.Mode.MULTIPLY));
            this.imageView.setTag(icon);
        }
    }

    public void setColors(int icon, int text) {
        this.textView.setTextColor(text);
        this.textView.setTag((Object) null);
        this.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
        this.imageView.setTag((Object) null);
    }

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setImageResource(resId);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndIcon(String text, Drawable drawable, boolean divider) {
        this.offsetFromImage = 68;
        this.imageLeft = 18;
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setColorFilter((ColorFilter) null);
        this.imageView.setImageDrawable(drawable);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(6.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setOffsetFromImage(int value) {
        this.offsetFromImage = value;
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(8);
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(resId);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueDrawable(String text, Drawable drawable, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(8);
        this.imageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CharSequence text = this.textView.getText();
        if (!TextUtils.isEmpty(text)) {
            CharSequence valueText = this.valueTextView.getText();
            if (!TextUtils.isEmpty(valueText)) {
                info.setText(text + ": " + valueText);
                return;
            }
            info.setText(text);
        }
    }
}
