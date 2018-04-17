package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.annotation.Keep;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class Switch2 extends View {
    private static Bitmap drawBitmap;
    private boolean attachedToWindow;
    private ObjectAnimator checkAnimator;
    private boolean isChecked;
    private boolean isDisabled;
    private Paint paint;
    private Paint paint2;
    private float progress;
    private RectF rectF = new RectF();

    public Switch2(Context context) {
        super(context);
        if (drawBitmap == null || drawBitmap.getWidth() != AndroidUtilities.dp(24.0f)) {
            drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), Config.ARGB_8888);
            Canvas canvas = new Canvas(drawBitmap);
            Paint paint = new Paint(1);
            paint.setShadowLayer((float) AndroidUtilities.dp(2.0f), 0.0f, 0.0f, Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            canvas.drawCircle((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(9.0f), paint);
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        }
        this.paint = new Paint(1);
        this.paint2 = new Paint(1);
        this.paint2.setStyle(Style.STROKE);
        this.paint2.setStrokeCap(Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    @Keep
    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(250);
        this.checkAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (this.attachedToWindow && animated) {
                animateToCheckedState(checked);
            } else {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
            }
        }
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        Switch2 switch2 = this;
        Canvas canvas2 = canvas;
        if (getVisibility() == 0) {
            int width = AndroidUtilities.dp(36.0f);
            int thumb = AndroidUtilities.dp(20.0f);
            int x = (getMeasuredWidth() - width) / 2;
            int y = (getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2;
            int tx = (((int) (((float) (width - AndroidUtilities.dp(14.0f))) * switch2.progress)) + x) + AndroidUtilities.dp(7.0f);
            int ty = getMeasuredHeight() / 2;
            switch2.paint.setColor(((((((int) (NUM + (-95.0f * switch2.progress))) & 255) << 16) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR) | ((((int) (NUM + (38.0f * switch2.progress))) & 255) << 8)) | (((int) (NUM + (77.0f * switch2.progress))) & 255));
            switch2.rectF.set((float) x, (float) y, (float) (x + width), (float) (AndroidUtilities.dp(14.0f) + y));
            canvas2.drawRoundRect(switch2.rectF, (float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(7.0f), switch2.paint);
            switch2.paint.setColor(((((((int) (219.0f + (-151.0f * switch2.progress))) & 255) << 16) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR) | ((((int) (88.0f + (80.0f * switch2.progress))) & 255) << 8)) | (((int) (92.0f + (142.0f * switch2.progress))) & 255));
            canvas2.drawBitmap(drawBitmap, (float) (tx - AndroidUtilities.dp(12.0f)), (float) (ty - AndroidUtilities.dp(11.0f)), null);
            canvas2.drawCircle((float) tx, (float) ty, (float) AndroidUtilities.dp(10.0f), switch2.paint);
            switch2.paint2.setColor(-1);
            int tx2 = (int) (((float) tx) - (((float) AndroidUtilities.dp(10.8f)) - (((float) AndroidUtilities.dp(1.3f)) * switch2.progress)));
            int ty2 = (int) (((float) ty) - (((float) AndroidUtilities.dp(8.5f)) - (((float) AndroidUtilities.dp(0.5f)) * switch2.progress)));
            int startX2 = ((int) AndroidUtilities.dpf2(4.6f)) + tx2;
            int startY2 = (int) (AndroidUtilities.dpf2(9.5f) + ((float) ty2));
            tx = ((int) AndroidUtilities.dpf2(7.5f)) + tx2;
            int startY = ((int) AndroidUtilities.dpf2(5.4f)) + ty2;
            int endX = tx + AndroidUtilities.dp(7.0f);
            ty = startY + AndroidUtilities.dp(7.0f);
            int ty3 = ty2;
            width = (int) (((float) tx) + (((float) (startX2 - tx)) * switch2.progress));
            ty2 = (int) (((float) startY) + (((float) (startY2 - startY)) * switch2.progress));
            endX = (int) (((float) endX) + (((float) ((startX2 + AndroidUtilities.dp(2.0f)) - endX)) * switch2.progress));
            ty = (int) (((float) ty) + (((float) ((startY2 + AndroidUtilities.dp(2.0f)) - ty)) * switch2.progress));
            float f = (float) ty2;
            int startY22 = startY2;
            int startY3 = ty2;
            float f2 = f;
            Canvas canvas3 = canvas2;
            int i = startY3;
            canvas3.drawLine((float) width, f2, (float) endX, (float) ty, switch2.paint2);
            width = ((int) AndroidUtilities.dpf2(7.5f)) + tx2;
            thumb = ((int) AndroidUtilities.dpf2(12.5f)) + ty3;
            ty2 = width + AndroidUtilities.dp(7.0f);
            startY2 = thumb - AndroidUtilities.dp(7.0f);
            float f3 = (float) startY2;
            float f4 = f3;
            canvas2.drawLine((float) width, (float) thumb, (float) ty2, f4, switch2.paint2);
        }
    }
}
