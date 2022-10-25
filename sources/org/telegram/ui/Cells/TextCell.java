package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Switch;
/* loaded from: classes3.dex */
public class TextCell extends FrameLayout {
    private Switch checkBox;
    public int imageLeft;
    public final RLottieImageView imageView;
    private boolean inDialogs;
    private int leftPadding;
    private boolean needDivider;
    private int offsetFromImage;
    private boolean prioritizeTitleOverValue;
    private Theme.ResourcesProvider resourcesProvider;
    public final SimpleTextView textView;
    private ImageView valueImageView;
    public final AnimatedTextView valueTextView;

    public TextCell(Context context) {
        this(context, 23, false, false, null);
    }

    public TextCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 23, false, false, resourcesProvider);
    }

    public TextCell(Context context, int i, boolean z) {
        this(context, i, z, false, null);
    }

    public TextCell(Context context, int i, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.offsetFromImage = 71;
        this.imageLeft = 21;
        this.resourcesProvider = resourcesProvider;
        this.leftPadding = i;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(z ? "dialogTextBlack" : "windowBackgroundWhiteBlackText", resourcesProvider));
        simpleTextView.setTextSize(16);
        int i2 = 5;
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView.setImportantForAccessibility(2);
        addView(simpleTextView, LayoutHelper.createFrame(-2, -1.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context);
        this.valueTextView = animatedTextView;
        animatedTextView.setTextColor(Theme.getColor(z ? "dialogTextBlue2" : "windowBackgroundWhiteValueText", resourcesProvider));
        animatedTextView.setPadding(0, AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f));
        animatedTextView.setTextSize(AndroidUtilities.dp(16.0f));
        animatedTextView.setGravity(LocaleController.isRTL ? 3 : 5);
        animatedTextView.setImportantForAccessibility(2);
        addView(animatedTextView);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? "dialogIcon" : "windowBackgroundWhiteGrayIcon", resourcesProvider), PorterDuff.Mode.MULTIPLY));
        addView(rLottieImageView);
        ImageView imageView = new ImageView(context);
        this.valueImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.valueImageView);
        if (z2) {
            Switch r3 = new Switch(context, resourcesProvider);
            this.checkBox = r3;
            r3.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
            addView(this.checkBox, LayoutHelper.createFrame(37, 20.0f, (LocaleController.isRTL ? 3 : i2) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
        }
        setFocusable(true);
    }

    public Switch getCheckBox() {
        return this.checkBox;
    }

    public void setIsInDialogs() {
        this.inDialogs = true;
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public RLottieImageView getImageView() {
        return this.imageView;
    }

    public AnimatedTextView getValueTextView() {
        return this.valueTextView;
    }

    public ImageView getValueImageView() {
        return this.valueImageView;
    }

    public void setPrioritizeTitleOverValue(boolean z) {
        this.prioritizeTitleOverValue = z;
        requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(48.0f);
        if (this.prioritizeTitleOverValue) {
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(this.leftPadding + 71), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec((size - AndroidUtilities.dp(this.leftPadding + 103)) - this.textView.getTextWidth(), LocaleController.isRTL ? Integer.MIN_VALUE : NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        } else {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(this.leftPadding), LocaleController.isRTL ? Integer.MIN_VALUE : NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec((size - AndroidUtilities.dp(this.leftPadding + 71)) - this.valueTextView.width(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        }
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        }
        Switch r8 = this.checkBox;
        if (r8 != null) {
            r8.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(37.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        }
        setMeasuredDimension(size, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        Switch r0 = this.checkBox;
        if (r0 != null) {
            r0.setEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int dp;
        int i5 = i4 - i2;
        int i6 = i3 - i;
        int textHeight = (i5 - this.valueTextView.getTextHeight()) / 2;
        int dp2 = LocaleController.isRTL ? AndroidUtilities.dp(this.leftPadding) : 0;
        if (this.prioritizeTitleOverValue && !LocaleController.isRTL) {
            dp2 = (i6 - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(this.leftPadding);
        }
        AnimatedTextView animatedTextView = this.valueTextView;
        animatedTextView.layout(dp2, textHeight, animatedTextView.getMeasuredWidth() + dp2, this.valueTextView.getMeasuredHeight() + textHeight);
        int textHeight2 = (i5 - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            dp = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding);
        } else {
            dp = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding);
        }
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(dp, textHeight2, simpleTextView.getMeasuredWidth() + dp, this.textView.getMeasuredHeight() + textHeight2);
        if (this.imageView.getVisibility() == 0) {
            int dp3 = AndroidUtilities.dp(5.0f);
            int dp4 = !LocaleController.isRTL ? AndroidUtilities.dp(this.imageLeft) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageLeft);
            RLottieImageView rLottieImageView = this.imageView;
            rLottieImageView.layout(dp4, dp3, rLottieImageView.getMeasuredWidth() + dp4, this.imageView.getMeasuredHeight() + dp3);
        }
        if (this.valueImageView.getVisibility() == 0) {
            int measuredHeight = (i5 - this.valueImageView.getMeasuredHeight()) / 2;
            int dp5 = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (i6 - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(23.0f);
            ImageView imageView = this.valueImageView;
            imageView.layout(dp5, measuredHeight, imageView.getMeasuredWidth() + dp5, this.valueImageView.getMeasuredHeight() + measuredHeight);
        }
        Switch r3 = this.checkBox;
        if (r3 == null || r3.getVisibility() != 0) {
            return;
        }
        int measuredHeight2 = (i5 - this.checkBox.getMeasuredHeight()) / 2;
        int dp6 = LocaleController.isRTL ? AndroidUtilities.dp(22.0f) : (i6 - this.checkBox.getMeasuredWidth()) - AndroidUtilities.dp(22.0f);
        Switch r4 = this.checkBox;
        r4.layout(dp6, measuredHeight2, r4.getMeasuredWidth() + dp6, this.checkBox.getMeasuredHeight() + measuredHeight2);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setColors(String str, String str2) {
        this.textView.setTextColor(Theme.getColor(str2, this.resourcesProvider));
        this.textView.setTag(str2);
        if (str != null) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str, this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
            this.imageView.setTag(str);
        }
    }

    public void setText(String str, boolean z) {
        this.imageLeft = 21;
        this.textView.setText(str);
        this.valueTextView.setText(null, false);
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(str);
        this.valueTextView.setText(null, false);
        this.imageView.setImageResource(i);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextAndIcon(String str, Drawable drawable, boolean z) {
        this.offsetFromImage = 68;
        this.imageLeft = 18;
        this.textView.setText(str);
        this.valueTextView.setText(null, false);
        this.imageView.setColorFilter((ColorFilter) null);
        if (drawable instanceof RLottieDrawable) {
            this.imageView.setAnimation((RLottieDrawable) drawable);
        } else {
            this.imageView.setImageDrawable(drawable);
        }
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(6.0f), 0, 0);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setOffsetFromImage(int i) {
        this.offsetFromImage = i;
    }

    public void setImageLeft(int i) {
        this.imageLeft = i;
    }

    public void setTextAndValue(String str, String str2, boolean z, boolean z2) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(str);
        this.valueTextView.setText(str2, z);
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = z2;
        setWillNotDraw(!z2);
        Switch r2 = this.checkBox;
        if (r2 != null) {
            r2.setVisibility(8);
        }
    }

    public void setTextAndValueAndIcon(String str, String str2, int i, boolean z) {
        setTextAndValueAndIcon(str, str2, false, i, z);
    }

    public void setTextAndValueAndIcon(String str, String str2, boolean z, int i, boolean z2) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(str);
        this.valueTextView.setText(str2, z);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(8);
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(i);
        this.needDivider = z2;
        setWillNotDraw(!z2);
        Switch r2 = this.checkBox;
        if (r2 != null) {
            r2.setVisibility(8);
        }
    }

    public void setTextAndCheckAndIcon(String str, boolean z, int i, boolean z2) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(str);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        Switch r2 = this.checkBox;
        if (r2 != null) {
            r2.setVisibility(0);
            this.checkBox.setChecked(z, false);
        }
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(i);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public void setTextAndValueDrawable(String str, Drawable drawable, boolean z) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(str);
        this.valueTextView.setText(null, false);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(8);
        this.imageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = z;
        setWillNotDraw(!z);
        Switch r3 = this.checkBox;
        if (r3 != null) {
            r3.setVisibility(8);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f;
        float dp;
        int i;
        if (this.needDivider) {
            int i2 = 72;
            float f2 = 20.0f;
            if (LocaleController.isRTL) {
                dp = 0.0f;
            } else {
                if (this.imageView.getVisibility() == 0) {
                    f = this.inDialogs ? 72 : 68;
                } else {
                    f = 20.0f;
                }
                dp = AndroidUtilities.dp(f);
            }
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() == 0) {
                    if (!this.inDialogs) {
                        i2 = 68;
                    }
                    f2 = i2;
                }
                i = AndroidUtilities.dp(f2);
            } else {
                i = 0;
            }
            canvas.drawLine(dp, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, Theme.dividerPaint);
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
            } else {
                accessibilityNodeInfo.setText(text);
            }
        }
        accessibilityNodeInfo.addAction(16);
    }

    public void setNeedDivider(boolean z) {
        if (this.needDivider != z) {
            this.needDivider = z;
            setWillNotDraw(!z);
            invalidate();
        }
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z, true);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
