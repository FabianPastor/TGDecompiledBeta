package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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

    public Switch2(android.content.Context r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r6 = this;
        r6.<init>(r7);
        r7 = new android.graphics.RectF;
        r7.<init>();
        r6.rectF = r7;
        r7 = drawBitmap;
        r0 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = 1;
        r2 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        if (r7 == 0) goto L_0x001f;
    L_0x0013:
        r7 = drawBitmap;
        r7 = r7.getWidth();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        if (r7 == r3) goto L_0x0060;
    L_0x001f:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = android.graphics.Bitmap.Config.ARGB_8888;
        r7 = android.graphics.Bitmap.createBitmap(r7, r2, r3);
        drawBitmap = r7;
        r7 = new android.graphics.Canvas;
        r2 = drawBitmap;
        r7.<init>(r2);
        r2 = new android.graphics.Paint;
        r2.<init>(r1);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = (float) r3;
        r4 = NUM; // 0x7f000000 float:1.7014118E38 double:1.0527088494E-314;
        r5 = 0;
        r2.setShadowLayer(r3, r5, r5, r4);
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = (float) r4;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r5 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r7.drawCircle(r4, r3, r5, r2);
        r2 = 0;
        r7.setBitmap(r2);	 Catch:{ Exception -> 0x0060 }
    L_0x0060:
        r7 = new android.graphics.Paint;
        r7.<init>(r1);
        r6.paint = r7;
        r7 = new android.graphics.Paint;
        r7.<init>(r1);
        r6.paint2 = r7;
        r7 = r6.paint2;
        r1 = android.graphics.Paint.Style.STROKE;
        r7.setStyle(r1);
        r7 = r6.paint2;
        r1 = android.graphics.Paint.Cap.ROUND;
        r7.setStrokeCap(r1);
        r7 = r6.paint2;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = (float) r0;
        r7.setStrokeWidth(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Switch2.<init>(android.content.Context):void");
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
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

    private void animateToCheckedState(boolean z) {
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = z ? true : false;
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

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? true : false);
            }
        }
    }

    public void setDisabled(boolean z) {
        this.isDisabled = z;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            int dp = AndroidUtilities.dp(36.0f);
            AndroidUtilities.dp(20.0f);
            int measuredWidth = (getMeasuredWidth() - dp) / 2;
            int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2;
            int dp2 = (((int) (((float) (dp - AndroidUtilities.dp(14.0f))) * this.progress)) + measuredWidth) + AndroidUtilities.dp(7.0f);
            int measuredHeight2 = getMeasuredHeight() / 2;
            this.paint.setColor(((((((int) (255.0f + (-95.0f * this.progress))) & 255) << 16) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR) | ((((int) (176.0f + (38.0f * this.progress))) & 255) << 8)) | (((int) (173.0f + (77.0f * this.progress))) & 255));
            this.rectF.set((float) measuredWidth, (float) measuredHeight, (float) (measuredWidth + dp), (float) (measuredHeight + AndroidUtilities.dp(14.0f)));
            canvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(7.0f), this.paint);
            this.paint.setColor(((((((int) (219.0f + (-151.0f * this.progress))) & 255) << 16) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR) | ((((int) (88.0f + (80.0f * this.progress))) & 255) << 8)) | (((int) (92.0f + (142.0f * this.progress))) & 255));
            canvas.drawBitmap(drawBitmap, (float) (dp2 - AndroidUtilities.dp(12.0f)), (float) (measuredHeight2 - AndroidUtilities.dp(11.0f)), null);
            float f = (float) dp2;
            float f2 = (float) measuredHeight2;
            canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(10.0f), this.paint);
            this.paint2.setColor(-1);
            dp = (int) (f - (((float) AndroidUtilities.dp(10.8f)) - (((float) AndroidUtilities.dp(1.3f)) * this.progress)));
            measuredWidth = (int) (f2 - (((float) AndroidUtilities.dp(8.5f)) - (((float) AndroidUtilities.dp(0.5f)) * this.progress)));
            measuredHeight = ((int) AndroidUtilities.dpf2(4.6f)) + dp;
            int dpf2 = (int) (AndroidUtilities.dpf2(9.5f) + ((float) measuredWidth));
            measuredHeight2 = AndroidUtilities.dp(2.0f) + measuredHeight;
            dp2 = AndroidUtilities.dp(2.0f) + dpf2;
            int dpf22 = ((int) AndroidUtilities.dpf2(7.5f)) + dp;
            int dpf23 = ((int) AndroidUtilities.dpf2(5.4f)) + measuredWidth;
            int dp3 = AndroidUtilities.dp(7.0f) + dpf22;
            int dp4 = AndroidUtilities.dp(7.0f) + dpf23;
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) ((int) (((float) dpf22) + (((float) (measuredHeight - dpf22)) * this.progress))), (float) ((int) (((float) dpf23) + (((float) (dpf2 - dpf23)) * this.progress))), (float) ((int) (((float) dp3) + (((float) (measuredHeight2 - dp3)) * this.progress))), (float) ((int) (((float) dp4) + (((float) (dp2 - dp4)) * this.progress))), this.paint2);
            measuredHeight = ((int) AndroidUtilities.dpf2(7.5f)) + dp;
            dp = ((int) AndroidUtilities.dpf2(12.5f)) + measuredWidth;
            Canvas canvas3 = canvas;
            canvas3.drawLine((float) measuredHeight, (float) dp, (float) (AndroidUtilities.dp(7.0f) + measuredHeight), (float) (dp - AndroidUtilities.dp(7.0f)), this.paint2);
        }
    }
}
