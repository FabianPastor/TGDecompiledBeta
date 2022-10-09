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
/* loaded from: classes3.dex */
public class VoIPOverlayBackground extends ImageView {
    float blackoutProgress;
    boolean showBlackout;

    public VoIPOverlayBackground(Context context) {
        super(context);
        ColorUtils.setAlphaComponent(-16777216, 102);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
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

    public void setBackground(final ImageReceiver.BitmapHolder bitmapHolder) {
        new Thread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPOverlayBackground$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VoIPOverlayBackground.this.lambda$setBackground$1(bitmapHolder);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setBackground$1(final ImageReceiver.BitmapHolder bitmapHolder) {
        try {
            final Bitmap createBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawBitmap(bitmapHolder.bitmap, (Rect) null, new Rect(0, 0, 150, 150), new Paint(2));
            Utilities.blurBitmap(createBitmap, 3, 0, createBitmap.getWidth(), createBitmap.getHeight(), createBitmap.getRowBytes());
            Palette generate = Palette.from(bitmapHolder.bitmap).generate();
            Paint paint = new Paint();
            paint.setColor((generate.getDarkMutedColor(-11242343) & 16777215) | NUM);
            canvas.drawColor(NUM);
            canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), paint);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPOverlayBackground$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPOverlayBackground.this.lambda$setBackground$0(createBitmap, bitmapHolder);
                }
            });
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setBackground$0(Bitmap bitmap, ImageReceiver.BitmapHolder bitmapHolder) {
        setImageBitmap(bitmap);
        bitmapHolder.release();
    }

    public void setShowBlackout(boolean z, boolean z2) {
        if (this.showBlackout == z) {
            return;
        }
        this.showBlackout = z;
        float f = 1.0f;
        if (!z2) {
            if (!z) {
                f = 0.0f;
            }
            this.blackoutProgress = f;
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = this.blackoutProgress;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPOverlayBackground$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPOverlayBackground.this.lambda$setShowBlackout$2(valueAnimator);
            }
        });
        ofFloat.setDuration(150L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setShowBlackout$2(ValueAnimator valueAnimator) {
        this.blackoutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }
}
