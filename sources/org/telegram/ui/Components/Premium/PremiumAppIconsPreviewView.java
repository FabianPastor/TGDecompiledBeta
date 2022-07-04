package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
        for (LauncherIconController.LauncherIcon icon : LauncherIconController.LauncherIcon.values()) {
            if (icon.premium) {
                this.icons.add(icon);
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

    private AdaptiveIconImageView newIconView(Context ctx, int i) {
        LauncherIconController.LauncherIcon icon = this.icons.get(i);
        AdaptiveIconImageView iconImageView = new AdaptiveIconImageView(ctx, i);
        iconImageView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 52.0f, 0.0f, 0.0f));
        iconImageView.setForeground(icon.foreground);
        iconImageView.setBackgroundResource(icon.background);
        iconImageView.setPadding(AndroidUtilities.dp(8.0f));
        iconImageView.setBackgroundOuterPadding(AndroidUtilities.dp(32.0f));
        addView(iconImageView);
        return iconImageView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.isEmpty) {
            int minSide = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
            int size = AndroidUtilities.dp(76.0f);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.topIcon.getLayoutParams();
            params.height = size;
            params.width = size;
            params.bottomMargin = (int) (((float) size) + (((float) minSide) * 0.1f));
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) this.bottomLeftIcon.getLayoutParams();
            params2.height = size;
            params2.width = size;
            params2.rightMargin = (int) (((float) size) * 0.95f);
            FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) this.bottomRightIcon.getLayoutParams();
            params3.height = size;
            params3.width = size;
            params3.leftMargin = (int) (((float) size) * 0.95f);
        }
    }

    public void setOffset(float translationX) {
        if (!this.isEmpty) {
            float progress = translationX / ((float) getMeasuredWidth());
            float rightProgress = CubicBezierInterpolator.EASE_IN.getInterpolation(progress);
            this.bottomRightIcon.setTranslationX((((float) (getRight() - this.bottomRightIcon.getRight())) + (((float) this.bottomRightIcon.getWidth()) * 1.5f) + ((float) AndroidUtilities.dp(32.0f))) * rightProgress);
            this.bottomRightIcon.setTranslationY(((float) AndroidUtilities.dp(16.0f)) * rightProgress);
            float p = 1.0f;
            float scale = Utilities.clamp(AndroidUtilities.lerp(1.0f, 1.5f, rightProgress), 1.0f, 0.0f);
            this.bottomRightIcon.setScaleX(scale);
            this.bottomRightIcon.setScaleY(scale);
            this.topIcon.setTranslationY(((((float) (getTop() - this.topIcon.getTop())) - (((float) this.topIcon.getHeight()) * 1.8f)) - ((float) AndroidUtilities.dp(32.0f))) * progress);
            this.topIcon.setTranslationX(((float) AndroidUtilities.dp(16.0f)) * progress);
            float scale2 = Utilities.clamp(AndroidUtilities.lerp(1.0f, 1.8f, progress), 1.0f, 0.0f);
            this.topIcon.setScaleX(scale2);
            this.topIcon.setScaleY(scale2);
            float leftProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(progress);
            this.bottomLeftIcon.setTranslationX(((((float) (getLeft() - this.bottomLeftIcon.getLeft())) - (((float) this.bottomLeftIcon.getWidth()) * 2.5f)) + ((float) AndroidUtilities.dp(32.0f))) * leftProgress);
            this.bottomLeftIcon.setTranslationY((((float) (getBottom() - this.bottomLeftIcon.getBottom())) + (((float) this.bottomLeftIcon.getHeight()) * 2.5f) + ((float) AndroidUtilities.dp(32.0f))) * leftProgress);
            float scale3 = Utilities.clamp(AndroidUtilities.lerp(1.0f, 2.5f, progress), 1.0f, 0.0f);
            this.bottomLeftIcon.setScaleX(scale3);
            this.bottomLeftIcon.setScaleY(scale3);
            if (progress < 0.4f) {
                p = progress / 0.4f;
            }
            this.bottomRightIcon.particlesScale = p;
            this.topIcon.particlesScale = p;
            this.bottomLeftIcon.particlesScale = p;
        }
    }

    private class AdaptiveIconImageView extends AppIconsSelectorCell.AdaptiveIconImageView {
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(20);
        Paint paint = new Paint(1);
        float particlesScale;

        public AdaptiveIconImageView(Context ctx, int i) {
            super(ctx);
            this.drawable.size1 = 12;
            this.drawable.size2 = 8;
            this.drawable.size3 = 6;
            if (i == 1) {
                this.drawable.type = 1001;
            }
            if (i == 0) {
                this.drawable.type = 1002;
            }
            this.drawable.colorKey = "premiumStartSmallStarsColor2";
            this.drawable.init();
            this.paint.setColor(-1);
        }

        public void draw(Canvas canvas) {
            int outBoundOffset = AndroidUtilities.dp(10.0f);
            this.drawable.excludeRect.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(5.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(5.0f)));
            this.drawable.rect.set((float) (-outBoundOffset), (float) (-outBoundOffset), (float) (getWidth() + outBoundOffset), (float) (getHeight() + outBoundOffset));
            canvas.save();
            float f = this.particlesScale;
            canvas.scale(1.0f - f, 1.0f - f, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
            this.drawable.onDraw(canvas);
            canvas.restore();
            invalidate();
            AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f), this.paint);
            super.draw(canvas);
        }
    }
}
