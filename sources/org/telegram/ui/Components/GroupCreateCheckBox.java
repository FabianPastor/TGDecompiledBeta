package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Keep;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateCheckBox extends View {
    private static Paint eraser = null;
    private static Paint eraser2 = null;
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private Paint backgroundInnerPaint;
    private String backgroundKey = Theme.key_groupcreate_checkboxCheck;
    private Paint backgroundPaint;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private String checkKey = Theme.key_groupcreate_checkboxCheck;
    private Paint checkPaint;
    private float checkScale = 1.0f;
    private Bitmap drawBitmap;
    private String innerKey = Theme.key_groupcreate_checkbox;
    private int innerRadDiff;
    private boolean isCheckAnimation = true;
    private boolean isChecked;
    private float progress;

    public GroupCreateCheckBox(Context context) {
        super(context);
        if (eraser == null) {
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            eraser2 = new Paint(1);
            eraser2.setColor(0);
            eraser2.setStyle(Style.STROKE);
            eraser2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        }
        this.backgroundPaint = new Paint(1);
        this.backgroundInnerPaint = new Paint(1);
        this.checkPaint = new Paint(1);
        this.checkPaint.setStyle(Style.STROKE);
        this.innerRadDiff = AndroidUtilities.dp(2.0f);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
        updateColors();
    }

    public void setColorKeysOverrides(String check, String inner, String back) {
        this.checkKey = check;
        this.innerKey = inner;
        this.backgroundKey = back;
        updateColors();
    }

    public void updateColors() {
        this.backgroundInnerPaint.setColor(Theme.getColor(this.innerKey));
        this.backgroundPaint.setColor(Theme.getColor(this.backgroundKey));
        this.checkPaint.setColor(Theme.getColor(this.checkKey));
        invalidate();
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

    public void setCheckScale(float value) {
        this.checkScale = value;
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        this.isCheckAnimation = newCheckedState;
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
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

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setInnerRadDiff(int value) {
        this.innerRadDiff = value;
    }

    protected void onDraw(Canvas canvas) {
        GroupCreateCheckBox groupCreateCheckBox = this;
        Canvas canvas2 = canvas;
        if (getVisibility() == 0 && groupCreateCheckBox.progress != 0.0f) {
            float radDiff;
            float radDiff2;
            float innerRad;
            float checkSide;
            float smallCheckSide;
            int x;
            int y;
            float side;
            int x2;
            float side2;
            int cx = getMeasuredWidth() / 2;
            int cy = getMeasuredHeight() / 2;
            eraser2.setStrokeWidth((float) AndroidUtilities.dp(30.0f));
            groupCreateCheckBox.drawBitmap.eraseColor(0);
            float roundProgress = groupCreateCheckBox.progress >= 0.5f ? 1.0f : groupCreateCheckBox.progress / 0.5f;
            float checkProgress = groupCreateCheckBox.progress < 0.5f ? 0.0f : (groupCreateCheckBox.progress - 0.5f) / 0.5f;
            float roundProgressCheckState = groupCreateCheckBox.isCheckAnimation ? groupCreateCheckBox.progress : 1.0f - groupCreateCheckBox.progress;
            if (roundProgressCheckState < progressBounceDiff) {
                radDiff = (((float) AndroidUtilities.dp(2.0f)) * roundProgressCheckState) / progressBounceDiff;
            } else if (roundProgressCheckState < 0.4f) {
                radDiff = ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (roundProgressCheckState - progressBounceDiff)) / progressBounceDiff);
            } else {
                radDiff = 0.0f;
                radDiff2 = radDiff;
                if (checkProgress != 0.0f) {
                    canvas2.drawCircle((float) cx, (float) cy, (((float) (cx - AndroidUtilities.dp(2.0f))) + (((float) AndroidUtilities.dp(2.0f)) * checkProgress)) - radDiff2, groupCreateCheckBox.backgroundPaint);
                }
                innerRad = ((float) (cx - groupCreateCheckBox.innerRadDiff)) - radDiff2;
                groupCreateCheckBox.bitmapCanvas.drawCircle((float) cx, (float) cy, innerRad, groupCreateCheckBox.backgroundInnerPaint);
                groupCreateCheckBox.bitmapCanvas.drawCircle((float) cx, (float) cy, (1.0f - roundProgress) * innerRad, eraser);
                canvas2.drawBitmap(groupCreateCheckBox.drawBitmap, 0.0f, 0.0f, null);
                checkSide = (((float) AndroidUtilities.dp(10.0f)) * checkProgress) * groupCreateCheckBox.checkScale;
                smallCheckSide = (((float) AndroidUtilities.dp(5.0f)) * checkProgress) * groupCreateCheckBox.checkScale;
                x = cx - AndroidUtilities.dp(1.0f);
                y = cy + AndroidUtilities.dp(4.0f);
                side = (float) Math.sqrt((double) ((smallCheckSide * smallCheckSide) / 2.0f));
                cx = y;
                x2 = x;
                canvas2.drawLine((float) x, (float) y, ((float) x) - side, ((float) y) - side, groupCreateCheckBox.checkPaint);
                side2 = (float) Math.sqrt((double) ((checkSide * checkSide) / 2.0f));
                y = x2 - AndroidUtilities.dp(1.2f);
                canvas2.drawLine((float) y, (float) cx, ((float) y) + side2, ((float) cx) - side2, groupCreateCheckBox.checkPaint);
            }
            radDiff2 = radDiff;
            if (checkProgress != 0.0f) {
                canvas2.drawCircle((float) cx, (float) cy, (((float) (cx - AndroidUtilities.dp(2.0f))) + (((float) AndroidUtilities.dp(2.0f)) * checkProgress)) - radDiff2, groupCreateCheckBox.backgroundPaint);
            }
            innerRad = ((float) (cx - groupCreateCheckBox.innerRadDiff)) - radDiff2;
            groupCreateCheckBox.bitmapCanvas.drawCircle((float) cx, (float) cy, innerRad, groupCreateCheckBox.backgroundInnerPaint);
            groupCreateCheckBox.bitmapCanvas.drawCircle((float) cx, (float) cy, (1.0f - roundProgress) * innerRad, eraser);
            canvas2.drawBitmap(groupCreateCheckBox.drawBitmap, 0.0f, 0.0f, null);
            checkSide = (((float) AndroidUtilities.dp(10.0f)) * checkProgress) * groupCreateCheckBox.checkScale;
            smallCheckSide = (((float) AndroidUtilities.dp(5.0f)) * checkProgress) * groupCreateCheckBox.checkScale;
            x = cx - AndroidUtilities.dp(1.0f);
            y = cy + AndroidUtilities.dp(4.0f);
            side = (float) Math.sqrt((double) ((smallCheckSide * smallCheckSide) / 2.0f));
            cx = y;
            x2 = x;
            canvas2.drawLine((float) x, (float) y, ((float) x) - side, ((float) y) - side, groupCreateCheckBox.checkPaint);
            side2 = (float) Math.sqrt((double) ((checkSide * checkSide) / 2.0f));
            y = x2 - AndroidUtilities.dp(1.2f);
            canvas2.drawLine((float) y, (float) cx, ((float) y) + side2, ((float) cx) - side2, groupCreateCheckBox.checkPaint);
        }
    }
}
