package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class SwipeGestureSettingsView extends FrameLayout {
    int currentIconIndex;
    Paint filledPaint = new Paint(1);
    boolean hasTabs;
    RLottieImageView[] iconViews;
    RLottieDrawable[] icons;
    Paint linePaint = new Paint(1);
    Paint outlinePaint = new Paint(1);
    private NumberPicker picker;
    Paint pickerDividersPaint = new Paint(1);
    float progressToSwipeFolders;
    RectF rect = new RectF();
    String[] strings;

    public SwipeGestureSettingsView(Context context, int i) {
        super(context);
        String[] strArr = new String[6];
        this.strings = strArr;
        this.icons = new RLottieDrawable[6];
        this.iconViews = new RLottieImageView[2];
        strArr[0] = LocaleController.getString("SwipeSettingsPin", NUM);
        this.strings[1] = LocaleController.getString("SwipeSettingsRead", NUM);
        this.strings[2] = LocaleController.getString("SwipeSettingsArchive", NUM);
        this.strings[3] = LocaleController.getString("SwipeSettingsMute", NUM);
        this.strings[4] = LocaleController.getString("SwipeSettingsDelete", NUM);
        this.strings[5] = LocaleController.getString("SwipeSettingsFolders", NUM);
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        float f = 1.0f;
        this.outlinePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(5.0f));
        this.pickerDividersPaint.setStyle(Paint.Style.STROKE);
        this.pickerDividersPaint.setStrokeCap(Paint.Cap.ROUND);
        this.pickerDividersPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        AnonymousClass1 r2 = new NumberPicker(context, 13) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float dp = (float) AndroidUtilities.dp(31.0f);
                SwipeGestureSettingsView.this.pickerDividersPaint.setColor(Theme.getColor("radioBackgroundChecked"));
                canvas.drawLine((float) AndroidUtilities.dp(2.0f), dp, (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), dp, SwipeGestureSettingsView.this.pickerDividersPaint);
                float measuredHeight = (float) (getMeasuredHeight() - AndroidUtilities.dp(31.0f));
                canvas.drawLine((float) AndroidUtilities.dp(2.0f), measuredHeight, (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), measuredHeight, SwipeGestureSettingsView.this.pickerDividersPaint);
            }
        };
        this.picker = r2;
        r2.setMinValue(0);
        this.picker.setDrawDividers(false);
        boolean z = !MessagesController.getInstance(i).dialogFilters.isEmpty();
        this.hasTabs = z;
        this.picker.setMaxValue(z ? this.strings.length - 1 : this.strings.length - 2);
        this.picker.setFormatter(new NumberPicker.Formatter() {
            public final String format(int i) {
                return SwipeGestureSettingsView.this.lambda$new$0$SwipeGestureSettingsView(i);
            }
        });
        this.picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                SwipeGestureSettingsView.this.lambda$new$1$SwipeGestureSettingsView(numberPicker, i, i2);
            }
        });
        this.picker.setValue(SharedConfig.getChatSwipeAction(i));
        addView(this.picker, LayoutHelper.createFrame(132, -1.0f, 5, 21.0f, 0.0f, 21.0f, 0.0f));
        setWillNotDraw(false);
        this.currentIconIndex = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            this.iconViews[i2] = new RLottieImageView(context);
            addView(this.iconViews[i2], LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 184.0f, 0.0f));
        }
        RLottieDrawable icon = getIcon(this.picker.getValue());
        if (icon != null) {
            this.iconViews[0].setImageDrawable(icon);
            icon.setCurrentFrame(icon.getFramesCount() - 1);
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[0], true, 0.5f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[1], false, 0.5f, false);
        this.progressToSwipeFolders = this.picker.getValue() != 5 ? 0.0f : f;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ String lambda$new$0$SwipeGestureSettingsView(int i) {
        return this.strings[i];
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$SwipeGestureSettingsView(NumberPicker numberPicker, int i, int i2) {
        int i3 = (this.currentIconIndex + 1) % 2;
        RLottieDrawable icon = getIcon(i2);
        if (icon != null) {
            if (this.iconViews[i3].getVisibility() != 0) {
                icon.setCurrentFrame(0, false);
            }
            this.iconViews[i3].setAnimation(icon);
            this.iconViews[i3].playAnimation();
        } else {
            this.iconViews[i3].clearAnimationDrawable();
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[this.currentIconIndex], false, 0.5f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[i3], true, 0.5f, true);
        this.currentIconIndex = i3;
        SharedConfig.updateChatListSwipeSetting(i2);
        invalidate();
        numberPicker.performHapticFeedback(3, 2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        RLottieImageView[] rLottieImageViewArr = this.iconViews;
        if (view != rLottieImageViewArr[0] && view != rLottieImageViewArr[1]) {
            return super.drawChild(canvas, view, j);
        }
        float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.progressToSwipeFolders);
        canvas.save();
        canvas.clipRect(((float) (getMeasuredWidth() - ((AndroidUtilities.dp(132.0f) + AndroidUtilities.dp(21.0f)) + AndroidUtilities.dp(16.0f)))) - (((float) AndroidUtilities.dp(58.0f)) * (1.0f - interpolation)), 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        boolean z = this.picker.getValue() == 5;
        if (z) {
            float f = this.progressToSwipeFolders;
            if (f != 1.0f) {
                float f2 = f + 0.053333335f;
                this.progressToSwipeFolders = f2;
                if (f2 > 1.0f) {
                    this.progressToSwipeFolders = 1.0f;
                } else {
                    this.iconViews[0].invalidate();
                    this.iconViews[1].invalidate();
                    invalidate();
                }
                float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.progressToSwipeFolders);
                this.outlinePaint.setColor(Theme.getColor("switchTrack"));
                this.linePaint.setColor(Theme.getColor("switchTrack"));
                int measuredWidth = getMeasuredWidth() - ((AndroidUtilities.dp(132.0f) + AndroidUtilities.dp(21.0f)) + AndroidUtilities.dp(16.0f));
                int dp = AndroidUtilities.dp(21.0f);
                int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(48.0f)) / 2;
                float f3 = (float) dp;
                float f4 = (float) measuredHeight;
                this.rect.set(f3, f4, (float) measuredWidth, (float) (getMeasuredHeight() - measuredHeight));
                this.filledPaint.setColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), Theme.getColor("switchTrack"), 0.16862746f));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.filledPaint);
                this.filledPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                this.filledPaint.setAlpha(255);
                float centerY = this.rect.centerY() - ((float) AndroidUtilities.dp(6.0f));
                this.linePaint.setAlpha((int) (interpolation * 57.0f));
                canvas.drawLine(((float) AndroidUtilities.dp(23.0f)) + this.rect.left + ((float) AndroidUtilities.dp(23.0f)), centerY, this.rect.right - ((float) AndroidUtilities.dp(50.0f)), centerY, this.linePaint);
                float centerY2 = this.rect.centerY() + ((float) AndroidUtilities.dp(6.0f));
                canvas.drawLine(((float) AndroidUtilities.dp(23.0f)) + this.rect.left + ((float) AndroidUtilities.dp(23.0f)), centerY2, this.rect.right - ((float) AndroidUtilities.dp(16.0f)), centerY2, this.linePaint);
                this.rect.set(f3, f4, (float) (measuredWidth - AndroidUtilities.dp(58.0f)), (float) (getMeasuredHeight() - measuredHeight));
                this.rect.inset((float) (-AndroidUtilities.dp(1.0f)), (float) (-AndroidUtilities.dp(1.0f)));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.filledPaint);
                this.outlinePaint.setAlpha(31);
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.outlinePaint);
                canvas.save();
                canvas2.clipRect(this.rect);
                this.filledPaint.setColor(Theme.getColor("switchTrack"));
                this.filledPaint.setAlpha(60);
                RectF rectF = this.rect;
                canvas2.drawCircle(rectF.left + 0.0f, rectF.centerY(), (float) AndroidUtilities.dp(15.0f), this.filledPaint);
                float centerY3 = this.rect.centerY() - ((float) AndroidUtilities.dp(6.0f));
                this.linePaint.setAlpha(57);
                canvas.drawLine(this.rect.left + ((float) AndroidUtilities.dp(23.0f)) + 0.0f, centerY3, this.rect.right - ((float) AndroidUtilities.dp(68.0f)), centerY3, this.linePaint);
                float centerY4 = this.rect.centerY() + ((float) AndroidUtilities.dp(6.0f));
                canvas.drawLine(this.rect.left + ((float) AndroidUtilities.dp(23.0f)) + 0.0f, centerY4, this.rect.right - ((float) AndroidUtilities.dp(23.0f)), centerY4, this.linePaint);
                canvas.restore();
            }
        }
        if (!z) {
            float f5 = this.progressToSwipeFolders;
            if (f5 != 0.0f) {
                float f6 = f5 - 0.053333335f;
                this.progressToSwipeFolders = f6;
                if (f6 < 0.0f) {
                    this.progressToSwipeFolders = 0.0f;
                } else {
                    this.iconViews[0].invalidate();
                    this.iconViews[1].invalidate();
                    invalidate();
                }
            }
        }
        float interpolation2 = CubicBezierInterpolator.DEFAULT.getInterpolation(this.progressToSwipeFolders);
        this.outlinePaint.setColor(Theme.getColor("switchTrack"));
        this.linePaint.setColor(Theme.getColor("switchTrack"));
        int measuredWidth2 = getMeasuredWidth() - ((AndroidUtilities.dp(132.0f) + AndroidUtilities.dp(21.0f)) + AndroidUtilities.dp(16.0f));
        int dp2 = AndroidUtilities.dp(21.0f);
        int measuredHeight2 = (getMeasuredHeight() - AndroidUtilities.dp(48.0f)) / 2;
        float var_ = (float) dp2;
        float var_ = (float) measuredHeight2;
        this.rect.set(var_, var_, (float) measuredWidth2, (float) (getMeasuredHeight() - measuredHeight2));
        this.filledPaint.setColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), Theme.getColor("switchTrack"), 0.16862746f));
        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.filledPaint);
        this.filledPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        this.filledPaint.setAlpha(255);
        float centerY5 = this.rect.centerY() - ((float) AndroidUtilities.dp(6.0f));
        this.linePaint.setAlpha((int) (interpolation2 * 57.0f));
        canvas.drawLine(((float) AndroidUtilities.dp(23.0f)) + this.rect.left + ((float) AndroidUtilities.dp(23.0f)), centerY5, this.rect.right - ((float) AndroidUtilities.dp(50.0f)), centerY5, this.linePaint);
        float centerY22 = this.rect.centerY() + ((float) AndroidUtilities.dp(6.0f));
        canvas.drawLine(((float) AndroidUtilities.dp(23.0f)) + this.rect.left + ((float) AndroidUtilities.dp(23.0f)), centerY22, this.rect.right - ((float) AndroidUtilities.dp(16.0f)), centerY22, this.linePaint);
        this.rect.set(var_, var_, (float) (measuredWidth2 - AndroidUtilities.dp(58.0f)), (float) (getMeasuredHeight() - measuredHeight2));
        this.rect.inset((float) (-AndroidUtilities.dp(1.0f)), (float) (-AndroidUtilities.dp(1.0f)));
        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.filledPaint);
        this.outlinePaint.setAlpha(31);
        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.outlinePaint);
        canvas.save();
        canvas2.clipRect(this.rect);
        this.filledPaint.setColor(Theme.getColor("switchTrack"));
        this.filledPaint.setAlpha(60);
        RectF rectF2 = this.rect;
        canvas2.drawCircle(rectF2.left + 0.0f, rectF2.centerY(), (float) AndroidUtilities.dp(15.0f), this.filledPaint);
        float centerY32 = this.rect.centerY() - ((float) AndroidUtilities.dp(6.0f));
        this.linePaint.setAlpha(57);
        canvas.drawLine(this.rect.left + ((float) AndroidUtilities.dp(23.0f)) + 0.0f, centerY32, this.rect.right - ((float) AndroidUtilities.dp(68.0f)), centerY32, this.linePaint);
        float centerY42 = this.rect.centerY() + ((float) AndroidUtilities.dp(6.0f));
        canvas.drawLine(this.rect.left + ((float) AndroidUtilities.dp(23.0f)) + 0.0f, centerY42, this.rect.right - ((float) AndroidUtilities.dp(23.0f)), centerY42, this.linePaint);
        canvas.restore();
    }

    public RLottieDrawable getIcon(int i) {
        if (i == 5) {
            return null;
        }
        RLottieDrawable[] rLottieDrawableArr = this.icons;
        if (rLottieDrawableArr[i] == null) {
            int i2 = i != 1 ? i != 2 ? i != 3 ? i != 4 ? NUM : NUM : NUM : NUM : NUM;
            rLottieDrawableArr[i] = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            updateIconColor(i);
        }
        return this.icons[i];
    }

    public void updateIconColor(int i) {
        if (this.icons[i] != null) {
            int blendARGB = ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), Theme.getColor("switchTrack"), 0.16862746f);
            int blendARGB2 = ColorUtils.blendARGB(Theme.getColor("switchTrack"), Theme.getColor("windowBackgroundWhite"), 0.2f);
            if (i == 2) {
                this.icons[i].setLayerColor("Arrow.**", blendARGB);
                this.icons[i].setLayerColor("Box2.**", blendARGB2);
                this.icons[i].setLayerColor("Box1.**", blendARGB2);
                return;
            }
            this.icons[i].setColorFilter(new PorterDuffColorFilter(blendARGB2, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void updateColors() {
        for (int i = 0; i < this.icons.length; i++) {
            updateIconColor(i);
        }
    }

    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        updateColors();
        this.picker.setTextColor(Theme.getColor("dialogTextBlack"));
        this.picker.invalidate();
    }
}
