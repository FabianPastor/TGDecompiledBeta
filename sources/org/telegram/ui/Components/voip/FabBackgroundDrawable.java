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
    private Paint shadowPaint = new Paint();

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public FabBackgroundDrawable() {
        this.shadowPaint.setColor(NUM);
    }

    public void draw(Canvas canvas) {
        if (this.shadowBitmap == null) {
            onBoundsChange(getBounds());
        }
        int min = Math.min(getBounds().width(), getBounds().height());
        Bitmap bitmap = this.shadowBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, (float) (getBounds().centerX() - (this.shadowBitmap.getWidth() / 2)), (float) (getBounds().centerY() - (this.shadowBitmap.getHeight() / 2)), this.shadowPaint);
        }
        int i = min / 2;
        float f = (float) i;
        canvas.drawCircle(f, f, (float) (i - AndroidUtilities.dp(4.0f)), this.bgPaint);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        int min = Math.min(rect.width(), rect.height());
        if (min <= 0) {
            this.shadowBitmap = null;
            return;
        }
        this.shadowBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(this.shadowBitmap);
        Paint paint = new Paint(1);
        paint.setShadowLayer((float) AndroidUtilities.dp(3.33333f), 0.0f, (float) AndroidUtilities.dp(0.666f), -1);
        int i = min / 2;
        float f = (float) i;
        canvas.drawCircle(f, f, (float) (i - AndroidUtilities.dp(4.0f)), paint);
    }

    public void setColor(int i) {
        this.bgPaint.setColor(i);
        invalidateSelf();
    }

    public boolean getPadding(Rect rect) {
        int dp = AndroidUtilities.dp(4.0f);
        rect.set(dp, dp, dp, dp);
        return true;
    }
}
