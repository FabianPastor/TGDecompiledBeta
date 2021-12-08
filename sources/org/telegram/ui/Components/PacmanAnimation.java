package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
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

    public PacmanAnimation(View parent) {
        this.edgePaint.setStyle(Paint.Style.STROKE);
        this.edgePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.parentView = parent;
    }

    public void setFinishRunnable(Runnable onAnimationFinished) {
        this.finishRunnable = onAnimationFinished;
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 17) {
            dt = 17;
        }
        if (this.progress >= 1.0f) {
            this.progress = 0.0f;
        }
        float f = this.progress + (((float) dt) / 400.0f);
        this.progress = f;
        if (f > 1.0f) {
            this.progress = 1.0f;
        }
        float f2 = this.translationProgress + (((float) dt) / 2000.0f);
        this.translationProgress = f2;
        if (f2 > 1.0f) {
            this.translationProgress = 1.0f;
        }
        float f3 = this.ghostProgress + (((float) dt) / 200.0f);
        this.ghostProgress = f3;
        if (f3 >= 1.0f) {
            this.ghostWalk = !this.ghostWalk;
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

    private void drawGhost(Canvas canvas, int num) {
        Canvas canvas2 = canvas;
        int i = num;
        Path path = this.ghostPath;
        if (path == null || this.ghostWalk != this.currentGhostWalk) {
            if (path == null) {
                this.ghostPath = new Path();
            }
            this.ghostPath.reset();
            boolean z = this.ghostWalk;
            this.currentGhostWalk = z;
            if (z) {
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
        if (i == 0) {
            this.paint.setColor(-90112);
        } else if (i == 1) {
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

    public void draw(Canvas canvas, int cy) {
        int rad;
        Canvas canvas2 = canvas;
        int size = AndroidUtilities.dp(110.0f);
        int height = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        int additionalSize = size + (AndroidUtilities.dp(62.0f) * 3);
        float translation = (((float) (this.parentView.getMeasuredWidth() + additionalSize)) * this.translationProgress) - ((float) additionalSize);
        int y = cy - (size / 2);
        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
        canvas.drawRect(0.0f, (float) (cy - (height / 2)), translation + ((float) (size / 2)), (float) (cy + (height / 2) + 1), this.paint);
        this.paint.setColor(-69120);
        this.rect.set(translation, (float) y, ((float) size) + translation, (float) (y + size));
        float f = this.progress;
        if (f < 0.5f) {
            rad = (int) ((1.0f - (f / 0.5f)) * 35.0f);
        } else {
            rad = (int) (((f - 0.5f) * 35.0f) / 0.5f);
        }
        int rad2 = rad;
        canvas.drawArc(this.rect, (float) rad, (float) (360 - (rad * 2)), true, this.edgePaint);
        canvas.drawArc(this.rect, (float) rad2, (float) (360 - (rad2 * 2)), true, this.paint);
        this.paint.setColor(-16777216);
        canvas2.drawCircle((((float) (size / 2)) + translation) - ((float) AndroidUtilities.dp(8.0f)), (float) ((size / 4) + y), (float) AndroidUtilities.dp(8.0f), this.paint);
        canvas.save();
        canvas2.translate(((float) size) + translation + ((float) AndroidUtilities.dp(20.0f)), (float) (cy - AndroidUtilities.dp(25.0f)));
        for (int a = 0; a < 3; a++) {
            drawGhost(canvas2, a);
            canvas2.translate((float) AndroidUtilities.dp(62.0f), 0.0f);
        }
        canvas.restore();
        if (this.translationProgress >= 1.0f) {
            this.finishRunnable.run();
        }
        update();
    }
}
