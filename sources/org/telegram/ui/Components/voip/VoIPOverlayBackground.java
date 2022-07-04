package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;

public class VoIPOverlayBackground extends ImageView {
    ValueAnimator animator;
    int blackoutColor = ColorUtils.setAlphaComponent(-16777216, 102);
    float blackoutProgress;
    boolean imageSet;
    boolean showBlackout;

    public VoIPOverlayBackground(Context context) {
        super(context);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = this.blackoutProgress;
        if (f == 1.0f) {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 102));
        } else if (f == 0.0f) {
            setImageAlpha(255);
            super.onDraw(canvas);
        } else {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (f * 102.0f)));
            setImageAlpha((int) ((1.0f - this.blackoutProgress) * 255.0f));
            super.onDraw(canvas);
        }
    }

    public void setBackground(ImageReceiver.BitmapHolder src) {
        new Thread(new VoIPOverlayBackground$$ExternalSyntheticLambda2(this, src)).start();
    }

    /* renamed from: lambda$setBackground$1$org-telegram-ui-Components-voip-VoIPOverlayBackground  reason: not valid java name */
    public /* synthetic */ void m1613xCLASSNAMEae(ImageReceiver.BitmapHolder src) {
        try {
            Bitmap blur1 = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blur1);
            canvas.drawBitmap(src.bitmap, (Rect) null, new Rect(0, 0, 150, 150), new Paint(2));
            Utilities.blurBitmap(blur1, 3, 0, blur1.getWidth(), blur1.getHeight(), blur1.getRowBytes());
            Palette palette = Palette.from(src.bitmap).generate();
            Paint paint = new Paint();
            paint.setColor((palette.getDarkMutedColor(-11242343) & 16777215) | NUM);
            canvas.drawColor(NUM);
            canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
            AndroidUtilities.runOnUIThread(new VoIPOverlayBackground$$ExternalSyntheticLambda1(this, blur1, src));
        } catch (Throwable th) {
        }
    }

    /* renamed from: lambda$setBackground$0$org-telegram-ui-Components-voip-VoIPOverlayBackground  reason: not valid java name */
    public /* synthetic */ void m1612x9c3bd26d(Bitmap blur1, ImageReceiver.BitmapHolder src) {
        setImageBitmap(blur1);
        this.imageSet = true;
        src.release();
    }

    public void setShowBlackout(boolean showBlackout2, boolean animated) {
        if (this.showBlackout != showBlackout2) {
            this.showBlackout = showBlackout2;
            float f = 1.0f;
            if (!animated) {
                if (!showBlackout2) {
                    f = 0.0f;
                }
                this.blackoutProgress = f;
                return;
            }
            float[] fArr = new float[2];
            fArr[0] = this.blackoutProgress;
            if (!showBlackout2) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator animator2 = ValueAnimator.ofFloat(fArr);
            animator2.addUpdateListener(new VoIPOverlayBackground$$ExternalSyntheticLambda0(this));
            animator2.setDuration(150).start();
        }
    }

    /* renamed from: lambda$setShowBlackout$2$org-telegram-ui-Components-voip-VoIPOverlayBackground  reason: not valid java name */
    public /* synthetic */ void m1614x593fed8d(ValueAnimator valueAnimator) {
        this.blackoutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }
}
