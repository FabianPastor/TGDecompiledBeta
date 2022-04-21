package org.telegram.ui.Components.voip;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class FabBackgroundDrawable extends Drawable {
    private Paint bgPaint = new Paint(1);
    private Bitmap shadowBitmap;
    private Paint shadowPaint;

    public FabBackgroundDrawable() {
        Paint paint = new Paint();
        this.shadowPaint = paint;
        paint.setColor(NUM);
    }

    public void draw(Canvas canvas) {
        if (this.shadowBitmap == null) {
            onBoundsChange(getBounds());
        }
        int size = Math.min(getBounds().width(), getBounds().height());
        Bitmap bitmap = this.shadowBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, (float) (getBounds().centerX() - (this.shadowBitmap.getWidth() / 2)), (float) (getBounds().centerY() - (this.shadowBitmap.getHeight() / 2)), this.shadowPaint);
        }
        canvas.drawCircle((float) (size / 2), (float) (size / 2), (float) ((size / 2) - AndroidUtilities.dp(4.0f)), this.bgPaint);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -2;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        int size = Math.min(bounds.width(), bounds.height());
        if (size <= 0) {
            this.shadowBitmap = null;
            return;
        }
        this.shadowBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(this.shadowBitmap);
        Paint p = new Paint(1);
        p.setShadowLayer((float) AndroidUtilities.dp(3.33333f), 0.0f, (float) AndroidUtilities.dp(0.666f), -1);
        c.drawCircle((float) (size / 2), (float) (size / 2), (float) ((size / 2) - AndroidUtilities.dp(4.0f)), p);
    }

    public void setColor(int color) {
        this.bgPaint.setColor(color);
        invalidateSelf();
    }

    public boolean getPadding(Rect padding) {
        int pad = AndroidUtilities.dp(4.0f);
        padding.set(pad, pad, pad, pad);
        return true;
    }
}
