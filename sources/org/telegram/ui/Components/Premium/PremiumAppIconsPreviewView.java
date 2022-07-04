package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.LauncherIconController;

public class PremiumAppIconsPreviewView extends FrameLayout implements PagerHeaderView {
    private AdaptiveIconImageView bottomLeftIcon;
    private AdaptiveIconImageView bottomRightIcon;
    private List<LauncherIconController.LauncherIcon> icons = new ArrayList();
    boolean isEmpty;
    private AdaptiveIconImageView topIcon;

    public PremiumAppIconsPreviewView(Context context) {
        super(context);
        for (LauncherIconController.LauncherIcon launcherIcon : LauncherIconController.LauncherIcon.values()) {
            if (launcherIcon.premium) {
                this.icons.add(launcherIcon);
            }
            if (this.icons.size() == 3) {
                break;
            }
        }
        if (this.icons.size() < 3) {
            FileLog.e((Throwable) new IllegalArgumentException("There should be at least 3 premium icons!"));
            this.isEmpty = true;
            return;
        }
        this.topIcon = newIconView(context, 0);
        this.bottomLeftIcon = newIconView(context, 1);
        this.bottomRightIcon = newIconView(context, 2);
        setClipChildren(false);
    }

    private AdaptiveIconImageView newIconView(Context context, int i) {
        LauncherIconController.LauncherIcon launcherIcon = this.icons.get(i);
        AdaptiveIconImageView adaptiveIconImageView = new AdaptiveIconImageView(this, context, i);
        adaptiveIconImageView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 52.0f, 0.0f, 0.0f));
        adaptiveIconImageView.setForeground(launcherIcon.foreground);
        adaptiveIconImageView.setBackgroundResource(launcherIcon.background);
        adaptiveIconImageView.setPadding(AndroidUtilities.dp(8.0f));
        adaptiveIconImageView.setBackgroundOuterPadding(AndroidUtilities.dp(32.0f));
        addView(adaptiveIconImageView);
        return adaptiveIconImageView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (!this.isEmpty) {
            int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
            int dp = AndroidUtilities.dp(76.0f);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.topIcon.getLayoutParams();
            layoutParams.height = dp;
            layoutParams.width = dp;
            float f = (float) dp;
            layoutParams.bottomMargin = (int) ((((float) min) * 0.1f) + f);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.bottomLeftIcon.getLayoutParams();
            layoutParams2.height = dp;
            layoutParams2.width = dp;
            int i3 = (int) (f * 0.95f);
            layoutParams2.rightMargin = i3;
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.bottomRightIcon.getLayoutParams();
            layoutParams3.height = dp;
            layoutParams3.width = dp;
            layoutParams3.leftMargin = i3;
        }
    }

    public void setOffset(float f) {
        if (!this.isEmpty) {
            float measuredWidth = f / ((float) getMeasuredWidth());
            float interpolation = CubicBezierInterpolator.EASE_IN.getInterpolation(measuredWidth);
            this.bottomRightIcon.setTranslationX((((float) (getRight() - this.bottomRightIcon.getRight())) + (((float) this.bottomRightIcon.getWidth()) * 1.5f) + ((float) AndroidUtilities.dp(32.0f))) * interpolation);
            this.bottomRightIcon.setTranslationY(((float) AndroidUtilities.dp(16.0f)) * interpolation);
            float f2 = 1.0f;
            float clamp = Utilities.clamp(AndroidUtilities.lerp(1.0f, 1.5f, interpolation), 1.0f, 0.0f);
            this.bottomRightIcon.setScaleX(clamp);
            this.bottomRightIcon.setScaleY(clamp);
            this.topIcon.setTranslationY(((((float) (getTop() - this.topIcon.getTop())) - (((float) this.topIcon.getHeight()) * 1.8f)) - ((float) AndroidUtilities.dp(32.0f))) * measuredWidth);
            this.topIcon.setTranslationX(((float) AndroidUtilities.dp(16.0f)) * measuredWidth);
            float clamp2 = Utilities.clamp(AndroidUtilities.lerp(1.0f, 1.8f, measuredWidth), 1.0f, 0.0f);
            this.topIcon.setScaleX(clamp2);
            this.topIcon.setScaleY(clamp2);
            float interpolation2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(measuredWidth);
            this.bottomLeftIcon.setTranslationX(((((float) (getLeft() - this.bottomLeftIcon.getLeft())) - (((float) this.bottomLeftIcon.getWidth()) * 2.5f)) + ((float) AndroidUtilities.dp(32.0f))) * interpolation2);
            this.bottomLeftIcon.setTranslationY(interpolation2 * (((float) (getBottom() - this.bottomLeftIcon.getBottom())) + (((float) this.bottomLeftIcon.getHeight()) * 2.5f) + ((float) AndroidUtilities.dp(32.0f))));
            float clamp3 = Utilities.clamp(AndroidUtilities.lerp(1.0f, 2.5f, measuredWidth), 1.0f, 0.0f);
            this.bottomLeftIcon.setScaleX(clamp3);
            this.bottomLeftIcon.setScaleY(clamp3);
            if (measuredWidth < 0.4f) {
                f2 = measuredWidth / 0.4f;
            }
            this.bottomRightIcon.particlesScale = f2;
            this.topIcon.particlesScale = f2;
            this.bottomLeftIcon.particlesScale = f2;
        }
    }

    private class AdaptiveIconImageView extends AppIconsSelectorCell.AdaptiveIconImageView {
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(20);
        Paint paint = new Paint(1);
        float particlesScale;

        public AdaptiveIconImageView(PremiumAppIconsPreviewView premiumAppIconsPreviewView, Context context, int i) {
            super(context);
            StarParticlesView.Drawable drawable2 = this.drawable;
            drawable2.size1 = 12;
            drawable2.size2 = 8;
            drawable2.size3 = 6;
            if (i == 1) {
                drawable2.type = 1001;
            }
            if (i == 0) {
                drawable2.type = 1002;
            }
            drawable2.colorKey = "premiumStartSmallStarsColor2";
            drawable2.init();
            this.paint.setColor(-1);
        }

        public void draw(Canvas canvas) {
            int dp = AndroidUtilities.dp(10.0f);
            this.drawable.excludeRect.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(5.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(5.0f)));
            float f = (float) (-dp);
            this.drawable.rect.set(f, f, (float) (getWidth() + dp), (float) (getHeight() + dp));
            canvas.save();
            float f2 = this.particlesScale;
            canvas.scale(1.0f - f2, 1.0f - f2, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
            this.drawable.onDraw(canvas);
            canvas.restore();
            invalidate();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f), this.paint);
            super.draw(canvas);
        }
    }
}
