package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class PacmanAnimation {
    private boolean currentGhostWalk;
    private Paint edgePaint = new Paint(1);
    private Runnable finishRunnable;
    private Path ghostPath;
    private float ghostProgress;
    private boolean ghostWalk;
    private long lastUpdateTime = 0;
    private Paint paint = new Paint(1);
    private View parentView;
    private float progress;
    private RectF rect = new RectF();
    private float translationProgress;

    public PacmanAnimation(View view) {
        this.edgePaint.setStyle(Style.STROKE);
        this.edgePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.parentView = view;
    }

    public void setFinishRunnable(Runnable runnable) {
        this.finishRunnable = runnable;
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        currentTimeMillis = 17;
        if (j <= 17) {
            currentTimeMillis = j;
        }
        if (this.progress >= 1.0f) {
            this.progress = 0.0f;
        }
        float f = (float) currentTimeMillis;
        this.progress += f / 400.0f;
        if (this.progress > 1.0f) {
            this.progress = 1.0f;
        }
        this.translationProgress += f / 2000.0f;
        if (this.translationProgress > 1.0f) {
            this.translationProgress = 1.0f;
        }
        this.ghostProgress += f / 200.0f;
        if (this.ghostProgress >= 1.0f) {
            this.ghostWalk ^= 1;
            this.ghostProgress = 0.0f;
        }
        this.parentView.invalidate();
    }

    public void start() {
        this.translationProgress = 0.0f;
        this.progress = 0.0f;
        this.lastUpdateTime = System.currentTimeMillis();
        this.parentView.invalidate();
    }

    private void drawGhost(Canvas canvas, int i) {
        Canvas canvas2 = canvas;
        int i2 = i;
        if (this.ghostPath == null || this.ghostWalk != this.currentGhostWalk) {
            if (this.ghostPath == null) {
                this.ghostPath = new Path();
            }
            this.ghostPath.reset();
            this.currentGhostWalk = this.ghostWalk;
            if (this.currentGhostWalk) {
                this.ghostPath.moveTo(0.0f, (float) AndroidUtilities.dp(50.0f));
                this.ghostPath.lineTo(0.0f, (float) AndroidUtilities.dp(24.0f));
                this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(42.0f), (float) AndroidUtilities.dp(24.0f));
                this.ghostPath.arcTo(this.rect, 180.0f, 180.0f, false);
                this.ghostPath.lineTo((float) AndroidUtilities.dp(42.0f), (float) AndroidUtilities.dp(50.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(43.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(50.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(21.0f), (float) AndroidUtilities.dp(43.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(50.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(43.0f));
            } else {
                this.ghostPath.moveTo(0.0f, (float) AndroidUtilities.dp(43.0f));
                this.ghostPath.lineTo(0.0f, (float) AndroidUtilities.dp(24.0f));
                this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(42.0f), (float) AndroidUtilities.dp(24.0f));
                this.ghostPath.arcTo(this.rect, 180.0f, 180.0f, false);
                this.ghostPath.lineTo((float) AndroidUtilities.dp(42.0f), (float) AndroidUtilities.dp(43.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(50.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(43.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(21.0f), (float) AndroidUtilities.dp(50.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(43.0f));
                this.ghostPath.lineTo((float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(50.0f));
            }
            this.ghostPath.close();
        }
        canvas2.drawPath(this.ghostPath, this.edgePaint);
        if (i2 == 0) {
            this.paint.setColor(-90112);
        } else if (i2 == 1) {
            this.paint.setColor(-85326);
        } else {
            this.paint.setColor(-16720161);
        }
        canvas2.drawPath(this.ghostPath, this.paint);
        this.paint.setColor(-1);
        this.rect.set((float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(28.0f));
        canvas2.drawOval(this.rect, this.paint);
        this.rect.set((float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(36.0f), (float) AndroidUtilities.dp(28.0f));
        canvas2.drawOval(this.rect, this.paint);
        this.paint.setColor(-16777216);
        this.rect.set((float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(19.0f), (float) AndroidUtilities.dp(24.0f));
        canvas2.drawOval(this.rect, this.paint);
        this.rect.set((float) AndroidUtilities.dp(30.0f), (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(24.0f));
        canvas2.drawOval(this.rect, this.paint);
    }

    public void draw(Canvas canvas, int i) {
        Canvas canvas2 = canvas;
        int dp = AndroidUtilities.dp(110.0f);
        int dp2 = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        int dp3 = (AndroidUtilities.dp(62.0f) * 3) + dp;
        float measuredWidth = (((float) (this.parentView.getMeasuredWidth() + dp3)) * this.translationProgress) - ((float) dp3);
        dp3 = dp / 2;
        int i2 = i - dp3;
        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
        dp2 /= 2;
        float f = measuredWidth + ((float) dp3);
        canvas.drawRect(0.0f, (float) (i - dp2), f, (float) ((i + dp2) + 1), this.paint);
        this.paint.setColor(-69120);
        float f2 = measuredWidth + ((float) dp);
        this.rect.set(measuredWidth, (float) i2, f2, (float) (i2 + dp));
        float f3 = this.progress;
        dp2 = (int) (f3 < 0.5f ? (1.0f - (f3 / 0.5f)) * 35.0f : ((f3 - 0.5f) * 35.0f) / 0.5f);
        float f4 = (float) dp2;
        float f5 = (float) (360 - (dp2 * 2));
        Canvas canvas3 = canvas;
        float f6 = f4;
        float f7 = f5;
        canvas3.drawArc(this.rect, f6, f5, true, this.edgePaint);
        canvas3.drawArc(this.rect, f6, f7, true, this.paint);
        this.paint.setColor(-16777216);
        canvas2.drawCircle(f - ((float) AndroidUtilities.dp(8.0f)), (float) (i2 + (dp / 4)), (float) AndroidUtilities.dp(8.0f), this.paint);
        canvas.save();
        canvas2.translate(f2 + ((float) AndroidUtilities.dp(20.0f)), (float) (i - AndroidUtilities.dp(25.0f)));
        for (dp2 = 0; dp2 < 3; dp2++) {
            drawGhost(canvas2, dp2);
            canvas2.translate((float) AndroidUtilities.dp(62.0f), 0.0f);
        }
        canvas.restore();
        if (this.translationProgress >= 1.0f) {
            this.finishRunnable.run();
        }
        update();
    }
}
