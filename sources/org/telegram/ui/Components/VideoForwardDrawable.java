package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
/* loaded from: classes3.dex */
public class VideoForwardDrawable extends Drawable {
    private static final int[] playPath = {10, 7, 26, 16, 10, 25};
    private boolean animating;
    private float animationProgress;
    private Path clippingPath;
    private VideoForwardDrawableDelegate delegate;
    private float enterAnimationProgress;
    private boolean isOneShootAnimation;
    private boolean isRound;
    private long lastAnimationTime;
    private int lastClippingPath;
    private boolean leftSide;
    private boolean showing;
    private long time;
    private String timeStr;
    private Paint paint = new Paint(1);
    private TextPaint textPaint = new TextPaint(1);
    private Path path1 = new Path();
    private float playScaleFactor = 1.0f;

    /* loaded from: classes3.dex */
    public interface VideoForwardDrawableDelegate {
        void invalidate();

        void onAnimationEnd();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    public void setTime(long j) {
        this.time = j;
        if (j >= 1000) {
            this.timeStr = LocaleController.formatPluralString("Seconds", (int) (j / 1000), new Object[0]);
        } else {
            this.timeStr = null;
        }
    }

    public VideoForwardDrawable(boolean z) {
        this.isRound = z;
        this.paint.setColor(-1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.path1.reset();
        int i = 0;
        while (true) {
            int[] iArr = playPath;
            if (i >= iArr.length / 2) {
                this.path1.close();
                return;
            }
            if (i == 0) {
                int i2 = i * 2;
                this.path1.moveTo(AndroidUtilities.dp(iArr[i2]), AndroidUtilities.dp(iArr[i2 + 1]));
            } else {
                int i3 = i * 2;
                this.path1.lineTo(AndroidUtilities.dp(iArr[i3]), AndroidUtilities.dp(iArr[i3 + 1]));
            }
            i++;
        }
    }

    public void setPlayScaleFactor(float f) {
        this.playScaleFactor = f;
        invalidate();
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void startAnimation() {
        this.animating = true;
        this.animationProgress = 0.0f;
        invalidateSelf();
    }

    public void setOneShootAnimation(boolean z) {
        if (this.isOneShootAnimation != z) {
            this.isOneShootAnimation = z;
            this.timeStr = null;
            this.time = 0L;
            this.animationProgress = 0.0f;
        }
    }

    public void setLeftSide(boolean z) {
        boolean z2 = this.leftSide;
        if (z2 != z || this.animationProgress < 1.0f || !this.isOneShootAnimation) {
            if (z2 != z) {
                this.time = 0L;
                this.timeStr = null;
            }
            this.leftSide = z;
            startAnimation();
        }
    }

    public void setDelegate(VideoForwardDrawableDelegate videoForwardDrawableDelegate) {
        this.delegate = videoForwardDrawableDelegate;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        this.textPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x02a7  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x02aa  */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void draw(android.graphics.Canvas r12) {
        /*
            Method dump skipped, instructions count: 689
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoForwardDrawable.draw(android.graphics.Canvas):void");
    }

    public void setShowing(boolean z) {
        this.showing = z;
        invalidate();
    }

    private void invalidate() {
        VideoForwardDrawableDelegate videoForwardDrawableDelegate = this.delegate;
        if (videoForwardDrawableDelegate != null) {
            videoForwardDrawableDelegate.invalidate();
        } else {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    public void addTime(long j) {
        long j2 = this.time + j;
        this.time = j2;
        this.timeStr = LocaleController.formatPluralString("Seconds", (int) (j2 / 1000), new Object[0]);
    }
}
