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
import org.telegram.ui.LauncherIconController;

public class PremiumAppIconsPreviewView extends FrameLayout implements PagerHeaderView {
    private AppIconsSelectorCell.AdaptiveIconImageView bottomLeftIcon;
    private AppIconsSelectorCell.AdaptiveIconImageView bottomRightIcon;
    private List<LauncherIconController.LauncherIcon> icons = new ArrayList();
    boolean isEmpty;
    private AppIconsSelectorCell.AdaptiveIconImageView topIcon;

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
        this.topIcon = newIconView(context, this.icons.get(0));
        this.bottomLeftIcon = newIconView(context, this.icons.get(1));
        this.bottomRightIcon = newIconView(context, this.icons.get(2));
    }

    private AppIconsSelectorCell.AdaptiveIconImageView newIconView(Context context, LauncherIconController.LauncherIcon launcherIcon) {
        AnonymousClass1 r0 = new AppIconsSelectorCell.AdaptiveIconImageView(this, context) {
            private Paint paint;

            {
                Paint paint2 = new Paint(1);
                this.paint = paint2;
                paint2.setColor(-1);
            }

            public void draw(Canvas canvas) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f), this.paint);
                super.draw(canvas);
            }
        };
        r0.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 52.0f, 0.0f, 0.0f));
        r0.setForeground(launcherIcon.foreground);
        r0.setBackgroundResource(launcherIcon.background);
        r0.setPadding(AndroidUtilities.dp(8.0f));
        r0.setBackgroundOuterPadding(AndroidUtilities.dp(32.0f));
        addView(r0);
        return r0;
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
        }
    }
}
