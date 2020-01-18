package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme.MessageDrawable;

public class MediaActionDrawable extends Drawable {
    private static final float CANCEL_TO_CHECK_STAGE1 = 0.5f;
    private static final float CANCEL_TO_CHECK_STAGE2 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE1 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE2 = 0.2f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE3 = 0.3f;
    private static final float EPS = 0.001f;
    public static final int ICON_CANCEL = 3;
    public static final int ICON_CANCEL_FILL = 14;
    public static final int ICON_CANCEL_NOPROFRESS = 12;
    public static final int ICON_CANCEL_PERCENT = 13;
    public static final int ICON_CHECK = 6;
    public static final int ICON_DOWNLOAD = 2;
    public static final int ICON_EMPTY = 10;
    public static final int ICON_EMPTY_NOPROGRESS = 11;
    public static final int ICON_FILE = 5;
    public static final int ICON_FIRE = 7;
    public static final int ICON_GIF = 8;
    public static final int ICON_NONE = 4;
    public static final int ICON_PAUSE = 1;
    public static final int ICON_PLAY = 0;
    public static final int ICON_SECRETCHECK = 9;
    private static final float[] pausePath1 = new float[]{16.0f, 17.0f, 32.0f, 17.0f, 32.0f, 22.0f, 16.0f, 22.0f, 16.0f, 19.5f};
    private static final float[] pausePath2 = new float[]{16.0f, 31.0f, 32.0f, 31.0f, 32.0f, 26.0f, 16.0f, 26.0f, 16.0f, 28.5f};
    private static final int pauseRotation = 90;
    private static final float[] playFinalPath = new float[]{18.0f, 15.0f, 34.0f, 24.0f, 18.0f, 33.0f};
    private static final float[] playPath1 = new float[]{18.0f, 15.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final float[] playPath2 = new float[]{18.0f, 33.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final int playRotation = 0;
    private float animatedDownloadProgress;
    private boolean animatingTransition;
    private Paint backPaint = new Paint(1);
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private boolean hasOverlayImage;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isMini;
    private long lastAnimationTime;
    private int lastPercent = -1;
    private MessageDrawable messageDrawable;
    private int nextIcon;
    private float overrideAlpha = 1.0f;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private Paint paint3 = new Paint(1);
    private Path path1 = new Path();
    private Path path2 = new Path();
    private String percentString;
    private int percentStringWidth;
    private RectF rect = new RectF();
    private float savedTransitionProgress;
    private float scale = 1.0f;
    private TextPaint textPaint = new TextPaint(1);
    private float transitionAnimationTime = 400.0f;
    private float transitionProgress = 1.0f;

    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    private float getCircleValue(float f) {
        while (f > 360.0f) {
            f -= 360.0f;
        }
        return f;
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStyle(Style.STROKE);
        this.paint3.setColor(-1);
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.paint2.setColor(-1);
        this.paint2.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(2.0f)));
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        this.paint2.setColorFilter(colorFilter);
        this.paint3.setColorFilter(colorFilter);
        this.textPaint.setColorFilter(colorFilter);
    }

    public void setColor(int i) {
        int i2 = -16777216 | i;
        this.paint.setColor(i2);
        this.paint2.setColor(i2);
        this.paint3.setColor(i2);
        this.textPaint.setColor(i2);
        this.colorFilter = new PorterDuffColorFilter(i, Mode.MULTIPLY);
    }

    public void setBackColor(int i) {
        this.backPaint.setColor(i | -16777216);
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean z) {
        this.isMini = z;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(this.isMini ? 2.0f : 3.0f));
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int i, boolean z) {
        if (this.currentIcon == i) {
            int i2 = this.nextIcon;
            if (i2 != i) {
                this.currentIcon = i2;
                this.transitionProgress = 1.0f;
            }
        }
        if (z) {
            int i3 = this.currentIcon;
            if (i3 == i || this.nextIcon == i) {
                return false;
            }
            if (i3 == 2 && (i == 3 || i == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (this.currentIcon != 4 && i == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((this.currentIcon == 4 && i == 14) || (this.currentIcon == 14 && i == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = this.nextIcon;
            }
            this.animatingTransition = true;
            this.nextIcon = i;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else if (this.currentIcon == i) {
            return false;
        } else {
            this.animatingTransition = false;
            this.nextIcon = i;
            this.currentIcon = i;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (i == 3 || i == 14) {
            this.downloadRadOffset = 112.0f;
            this.animatedDownloadProgress = 0.0f;
            this.downloadProgressAnimationStart = 0.0f;
            this.downloadProgressTime = 0.0f;
        }
        invalidateSelf();
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public int getPreviousIcon() {
        return this.currentIcon;
    }

    public void setProgress(float f, boolean z) {
        if (z) {
            if (this.animatedDownloadProgress > f) {
                this.animatedDownloadProgress = f;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        } else {
            this.animatedDownloadProgress = f;
            this.downloadProgressAnimationStart = f;
        }
        this.downloadProgress = f;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    public float getProgressAlpha() {
        return 1.0f - this.transitionProgress;
    }

    public float getTransitionProgress() {
        return this.animatingTransition ? this.transitionProgress : 1.0f;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        if (drawable instanceof MessageDrawable) {
            this.messageDrawable = (MessageDrawable) drawable;
        }
    }

    public void setHasOverlayImage(boolean z) {
        this.hasOverlayImage = z;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.scale = ((float) (i3 - i)) / ((float) getIntrinsicWidth());
        if (this.scale < 0.7f) {
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        MediaActionDrawableDelegate mediaActionDrawableDelegate = this.delegate;
        if (mediaActionDrawableDelegate != null) {
            mediaActionDrawableDelegate.invalidate();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0899  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0892  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x09f4  */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x088b  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x087d  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0892  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0899  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x089f A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x09f4  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x04fc  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0507  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0585  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0511  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0598  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x04fc  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0507  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0511  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0585  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0598  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0355  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0377  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x03ab  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d7 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0355  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0377  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0399  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x03ab  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0699  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0728  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x07a5  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x07cd  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0b78  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0bae  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0cab  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d27  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d46  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0da2  */
    /* JADX WARNING: Removed duplicated region for block: B:487:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing block: B:107:0x037b, code skipped:
            if (r1 != 3) goto L_0x037f;
     */
    /* JADX WARNING: Missing block: B:195:0x059c, code skipped:
            if (r1 != 3) goto L_0x0609;
     */
    /* JADX WARNING: Missing block: B:207:0x05d8, code skipped:
            if (r1 != 3) goto L_0x05fc;
     */
    /* JADX WARNING: Missing block: B:456:0x0d29, code skipped:
            if (r1 != 13) goto L_0x0d78;
     */
    public void draw(android.graphics.Canvas r33) {
        /*
        r32 = this;
        r0 = r32;
        r7 = r33;
        r8 = r32.getBounds();
        r1 = r0.messageDrawable;
        r9 = 0;
        r10 = 0;
        if (r1 == 0) goto L_0x003d;
    L_0x000e:
        r1 = r1.hasGradient();
        if (r1 == 0) goto L_0x003d;
    L_0x0014:
        r1 = r0.hasOverlayImage;
        if (r1 != 0) goto L_0x003d;
    L_0x0018:
        r1 = r0.messageDrawable;
        r1 = r1.getGradientShader();
        r2 = r0.messageDrawable;
        r2 = r2.getMatrix();
        r3 = r8.top;
        r3 = (float) r3;
        r2.postTranslate(r10, r3);
        r1.setLocalMatrix(r2);
        r2 = r0.paint;
        r2.setShader(r1);
        r2 = r0.paint2;
        r2.setShader(r1);
        r2 = r0.paint3;
        r2.setShader(r1);
        goto L_0x004c;
    L_0x003d:
        r1 = r0.paint;
        r1.setShader(r9);
        r1 = r0.paint2;
        r1.setShader(r9);
        r1 = r0.paint3;
        r1.setShader(r9);
    L_0x004c:
        r11 = r8.centerX();
        r12 = r8.centerY();
        r1 = r0.nextIcon;
        r13 = 3;
        r14 = 4;
        r6 = 14;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r1 != r14) goto L_0x0072;
    L_0x005e:
        r1 = r0.currentIcon;
        if (r1 == r13) goto L_0x008a;
    L_0x0062:
        if (r1 == r6) goto L_0x008a;
    L_0x0064:
        r1 = r33.save();
        r2 = r0.transitionProgress;
        r2 = r5 - r2;
        r3 = (float) r11;
        r4 = (float) r12;
        r7.scale(r2, r2, r3, r4);
        goto L_0x0088;
    L_0x0072:
        r2 = 6;
        if (r1 == r2) goto L_0x0079;
    L_0x0075:
        r2 = 10;
        if (r1 != r2) goto L_0x008a;
    L_0x0079:
        r1 = r0.currentIcon;
        if (r1 != r14) goto L_0x008a;
    L_0x007d:
        r1 = r33.save();
        r2 = r0.transitionProgress;
        r3 = (float) r11;
        r4 = (float) r12;
        r7.scale(r2, r2, r3, r4);
    L_0x0088:
        r4 = r1;
        goto L_0x008b;
    L_0x008a:
        r4 = 0;
    L_0x008b:
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r16 = NUM; // 0x40600000 float:3.5 double:5.3360734E-315;
        r17 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r18 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r19 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r20 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r2 = 2;
        if (r1 == r2) goto L_0x00ad;
    L_0x009f:
        r1 = r0.nextIcon;
        if (r1 != r2) goto L_0x00a4;
    L_0x00a3:
        goto L_0x00ad;
    L_0x00a4:
        r27 = r4;
        r28 = r8;
        r8 = 14;
        r10 = 2;
        goto L_0x036c;
    L_0x00ad:
        r1 = (float) r12;
        r21 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r9 = (float) r9;
        r15 = r0.scale;
        r9 = r9 * r15;
        r9 = r1 - r9;
        r15 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14 = r0.scale;
        r15 = r15 * r14;
        r15 = r15 + r1;
        r14 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r10 = r0.scale;
        r14 = r14 * r10;
        r14 = r14 + r1;
        r10 = r0.currentIcon;
        if (r10 == r13) goto L_0x00d9;
    L_0x00d7:
        if (r10 != r6) goto L_0x0100;
    L_0x00d9:
        r10 = r0.nextIcon;
        if (r10 != r2) goto L_0x0100;
    L_0x00dd:
        r10 = r0.paint;
        r3 = r0.transitionProgress;
        r3 = r3 / r20;
        r3 = java.lang.Math.min(r5, r3);
        r3 = r3 * r19;
        r3 = (int) r3;
        r10.setAlpha(r3);
        r3 = r0.transitionProgress;
        r10 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r5 = r0.scale;
        r10 = r10 * r5;
        r10 = r10 + r1;
        r24 = r10;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x013d;
    L_0x0100:
        r3 = r0.nextIcon;
        if (r3 == r13) goto L_0x0125;
    L_0x0104:
        if (r3 == r6) goto L_0x0125;
    L_0x0106:
        if (r3 == r2) goto L_0x0125;
    L_0x0108:
        r3 = r0.paint;
        r5 = r0.savedTransitionProgress;
        r5 = r5 / r20;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = java.lang.Math.min(r10, r5);
        r5 = r5 * r19;
        r6 = r0.transitionProgress;
        r6 = r10 - r6;
        r5 = r5 * r6;
        r5 = (int) r5;
        r3.setAlpha(r5);
        r3 = r0.savedTransitionProgress;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x012e;
    L_0x0125:
        r3 = r0.paint;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r3.setAlpha(r5);
        r3 = r0.transitionProgress;
    L_0x012e:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r6 = (float) r6;
        r5 = r0.scale;
        r6 = r6 * r5;
        r5 = r1 + r6;
        r24 = r5;
    L_0x013d:
        r5 = r0.animatingTransition;
        if (r5 == 0) goto L_0x030f;
    L_0x0141:
        r5 = r0.nextIcon;
        if (r5 == r2) goto L_0x02bc;
    L_0x0145:
        r5 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1));
        if (r5 > 0) goto L_0x014b;
    L_0x0149:
        goto L_0x02bc;
    L_0x014b:
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r0.scale;
        r5 = r5 * r6;
        r5 = r5 * r6;
        r6 = r0.isMini;
        if (r6 == 0) goto L_0x0161;
    L_0x015c:
        r15 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x0162;
    L_0x0161:
        r15 = 0;
    L_0x0162:
        r6 = (float) r15;
        r5 = r5 + r6;
        r3 = r3 - r20;
        r6 = r3 / r20;
        r9 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r9 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));
        if (r9 <= 0) goto L_0x017b;
    L_0x016f:
        r9 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r3 = r3 - r9;
        r9 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r3 = r3 / r9;
        r9 = r3;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0181;
    L_0x017b:
        r9 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r3 = r3 / r9;
        r15 = r3;
        r9 = 0;
    L_0x0181:
        r3 = r0.rect;
        r10 = (float) r11;
        r2 = r10 - r5;
        r5 = r5 / r17;
        r13 = r14 - r5;
        r5 = r5 + r14;
        r3.set(r2, r13, r10, r5);
        r2 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r9 * r2;
        r2 = r0.rect;
        r5 = NUM; // 0x42d00000 float:104.0 double:5.5381189E-315;
        r6 = r6 * r5;
        r5 = r6 - r3;
        r6 = 0;
        r13 = r0.paint;
        r26 = r1;
        r1 = r33;
        r27 = r4;
        r4 = r5;
        r23 = r8;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r6;
        r8 = 14;
        r6 = r13;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r14 - r24;
        r1 = r1 * r15;
        r24 = r24 + r1;
        r1 = 0;
        r2 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1));
        if (r2 <= 0) goto L_0x02b5;
    L_0x01ba:
        r1 = r0.nextIcon;
        if (r1 != r8) goto L_0x01c0;
    L_0x01be:
        r13 = 0;
        goto L_0x01c9;
    L_0x01c0:
        r1 = -NUM; // 0xffffffffCLASSNAME float:-45.0 double:NaN;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r9;
        r1 = r1 * r5;
        r13 = r1;
    L_0x01c9:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r1 = r1 * r9;
        r2 = r0.scale;
        r1 = r1 * r2;
        r9 = r9 * r19;
        r2 = (int) r9;
        r3 = r0.nextIcon;
        r4 = 3;
        if (r3 == r4) goto L_0x01f2;
    L_0x01dc:
        if (r3 == r8) goto L_0x01f2;
    L_0x01de:
        r9 = 2;
        if (r3 == r9) goto L_0x01f3;
    L_0x01e1:
        r3 = r0.transitionProgress;
        r3 = r3 / r20;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = java.lang.Math.min(r4, r3);
        r5 = r4 - r3;
        r2 = (float) r2;
        r2 = r2 * r5;
        r2 = (int) r2;
        goto L_0x01f3;
    L_0x01f2:
        r9 = 2;
    L_0x01f3:
        r15 = r2;
        r2 = 0;
        r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x0202;
    L_0x01f9:
        r33.save();
        r2 = r26;
        r7.rotate(r13, r10, r2);
        goto L_0x0204;
    L_0x0202:
        r2 = r26;
    L_0x0204:
        if (r15 == 0) goto L_0x02aa;
    L_0x0206:
        r3 = r0.paint;
        r3.setAlpha(r15);
        r3 = r0.nextIcon;
        if (r3 != r8) goto L_0x028a;
    L_0x020f:
        r1 = r0.paint3;
        r1.setAlpha(r15);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r11 - r2;
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r12 - r3;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = r4 + r11;
        r4 = (float) r4;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r5 = r5 + r12;
        r5 = (float) r5;
        r1.set(r2, r3, r4, r5);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = (float) r3;
        r4 = r0.paint3;
        r7.drawRoundRect(r1, r2, r3, r4);
        r1 = r0.paint;
        r2 = (float) r15;
        r3 = NUM; // 0x3e19999a float:0.15 double:5.147497604E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x0257;
    L_0x0254:
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x0259;
    L_0x0257:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x0259:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r0.rect;
        r6 = r23;
        r3 = r6.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r4 = r6.top;
        r4 = r4 + r1;
        r4 = (float) r4;
        r5 = r6.right;
        r5 = r5 - r1;
        r5 = (float) r5;
        r9 = r6.bottom;
        r9 = r9 - r1;
        r1 = (float) r9;
        r2.set(r3, r4, r5, r1);
        r2 = r0.rect;
        r3 = 0;
        r4 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r5 = 0;
        r9 = r0.paint;
        r1 = r33;
        r28 = r6;
        r6 = r9;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r0.paint;
        r1.setAlpha(r15);
        goto L_0x02ac;
    L_0x028a:
        r28 = r23;
        r9 = r10 - r1;
        r15 = r2 - r1;
        r23 = r10 + r1;
        r25 = r2 + r1;
        r6 = r0.paint;
        r1 = r33;
        r2 = r9;
        r3 = r15;
        r4 = r23;
        r5 = r25;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r23;
        r4 = r9;
        r1.drawLine(r2, r3, r4, r5, r6);
        goto L_0x02ac;
    L_0x02aa:
        r28 = r23;
    L_0x02ac:
        r1 = 0;
        r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x02b7;
    L_0x02b1:
        r33.restore();
        goto L_0x02b7;
    L_0x02b5:
        r28 = r23;
    L_0x02b7:
        r1 = r10;
        r2 = r1;
        r3 = r14;
        r10 = 2;
        goto L_0x0309;
    L_0x02bc:
        r27 = r4;
        r28 = r8;
        r8 = 14;
        r10 = 2;
        r1 = r0.nextIcon;
        if (r1 != r10) goto L_0x02cc;
    L_0x02c7:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r1 - r3;
        goto L_0x02d2;
    L_0x02cc:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r3 / r20;
        r3 = r1 - r5;
    L_0x02d2:
        r24 = r24 - r9;
        r24 = r24 * r5;
        r24 = r9 + r24;
        r14 = r14 - r15;
        r14 = r14 * r5;
        r14 = r14 + r15;
        r1 = (float) r11;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r2 = r2 * r3;
        r4 = r0.scale;
        r2 = r2 * r4;
        r2 = r1 - r2;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r4 = r4 * r3;
        r5 = r0.scale;
        r4 = r4 * r5;
        r1 = r1 + r4;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r4 = r4 * r3;
        r3 = r0.scale;
        r4 = r4 * r3;
        r3 = r14 - r4;
    L_0x0309:
        r15 = r1;
        r9 = r2;
        r13 = r3;
        r3 = r24;
        goto L_0x0342;
    L_0x030f:
        r27 = r4;
        r28 = r8;
        r8 = 14;
        r10 = 2;
        r1 = (float) r11;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r3 = r0.scale;
        r2 = r2 * r3;
        r2 = r1 - r2;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r4 = r0.scale;
        r3 = r3 * r4;
        r1 = r1 + r3;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r4 = r0.scale;
        r3 = r3 * r4;
        r3 = r15 - r3;
        r13 = r3;
        r3 = r9;
        r14 = r15;
        r15 = r1;
        r9 = r2;
    L_0x0342:
        r1 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
        if (r1 == 0) goto L_0x0350;
    L_0x0346:
        r4 = (float) r11;
        r6 = r0.paint;
        r1 = r33;
        r2 = r4;
        r5 = r14;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0350:
        r6 = (float) r11;
        r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x036c;
    L_0x0355:
        r5 = r0.paint;
        r1 = r33;
        r2 = r9;
        r3 = r13;
        r4 = r6;
        r9 = r5;
        r5 = r14;
        r23 = r6;
        r6 = r9;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r15;
        r4 = r23;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x036c:
        r1 = r0.currentIcon;
        r9 = 1;
        r2 = 3;
        if (r1 == r2) goto L_0x03f2;
    L_0x0372:
        if (r1 == r8) goto L_0x03f2;
    L_0x0374:
        r3 = 4;
        if (r1 != r3) goto L_0x037f;
    L_0x0377:
        r1 = r0.nextIcon;
        if (r1 == r8) goto L_0x03f2;
    L_0x037b:
        if (r1 != r2) goto L_0x037f;
    L_0x037d:
        goto L_0x03f2;
    L_0x037f:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0394;
    L_0x0385:
        r2 = r0.nextIcon;
        r3 = 10;
        if (r2 == r3) goto L_0x0394;
    L_0x038b:
        r2 = 13;
        if (r1 != r2) goto L_0x0390;
    L_0x038f:
        goto L_0x0394;
    L_0x0390:
        r13 = r28;
        goto L_0x0612;
    L_0x0394:
        r1 = r0.nextIcon;
        r2 = 4;
        if (r1 == r2) goto L_0x03a0;
    L_0x0399:
        r2 = 6;
        if (r1 != r2) goto L_0x039d;
    L_0x039c:
        goto L_0x03a0;
    L_0x039d:
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x03a9;
    L_0x03a0:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
        r5 = r5 * r19;
        r3 = (int) r5;
    L_0x03a9:
        if (r3 == 0) goto L_0x0390;
    L_0x03ab:
        r1 = r0.paint;
        r2 = (float) r3;
        r3 = r0.overrideAlpha;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r3 = r0.animatedDownloadProgress;
        r3 = r3 * r2;
        r4 = java.lang.Math.max(r1, r3);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x03c7;
    L_0x03c6:
        goto L_0x03c9;
    L_0x03c7:
        r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x03c9:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r0.rect;
        r13 = r28;
        r3 = r13.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r5 = r13.top;
        r5 = r5 + r1;
        r5 = (float) r5;
        r6 = r13.right;
        r6 = r6 - r1;
        r6 = (float) r6;
        r14 = r13.bottom;
        r14 = r14 - r1;
        r1 = (float) r14;
        r2.set(r3, r5, r6, r1);
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r33;
        r1.drawArc(r2, r3, r4, r5, r6);
        goto L_0x0612;
    L_0x03f2:
        r13 = r28;
        r1 = r0.nextIcon;
        if (r1 != r10) goto L_0x0420;
    L_0x03f8:
        r1 = r0.transitionProgress;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 > 0) goto L_0x0414;
    L_0x03fe:
        r1 = r1 / r20;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r1 = r1 * r5;
        r2 = r0.scale;
        r1 = r1 * r2;
        r5 = r5 * r19;
        r15 = (int) r5;
        r3 = r15;
        goto L_0x0416;
    L_0x0414:
        r1 = 0;
        r3 = 0;
    L_0x0416:
        r5 = r3;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = 0;
    L_0x041a:
        r6 = 0;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = 0;
        goto L_0x04f8;
    L_0x0420:
        if (r1 == 0) goto L_0x04c3;
    L_0x0422:
        if (r1 == r9) goto L_0x04c3;
    L_0x0424:
        r2 = 5;
        if (r1 == r2) goto L_0x04c3;
    L_0x0427:
        r2 = 8;
        if (r1 == r2) goto L_0x04c3;
    L_0x042b:
        r2 = 9;
        if (r1 == r2) goto L_0x04c3;
    L_0x042f:
        r2 = 7;
        if (r1 == r2) goto L_0x04c3;
    L_0x0432:
        r2 = 6;
        if (r1 != r2) goto L_0x0437;
    L_0x0435:
        goto L_0x04c3;
    L_0x0437:
        r2 = 4;
        if (r1 != r2) goto L_0x0473;
    L_0x043a:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = (float) r2;
        r3 = r0.scale;
        r2 = r2 * r3;
        r3 = r5 * r19;
        r3 = (int) r3;
        r4 = r0.currentIcon;
        if (r4 != r8) goto L_0x045a;
    L_0x0450:
        r1 = r13.left;
        r1 = (float) r1;
        r4 = r13.top;
        r4 = (float) r4;
        r6 = r4;
        r4 = r1;
        r1 = 0;
        goto L_0x046b;
    L_0x045a:
        r4 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r4;
        r4 = r13.centerX();
        r4 = (float) r4;
        r5 = r13.centerY();
        r5 = (float) r5;
        r6 = r5;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x046b:
        r15 = r1;
        r1 = r2;
        r14 = r5;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r3;
        goto L_0x04f8;
    L_0x0473:
        if (r1 == r8) goto L_0x0488;
    L_0x0475:
        r2 = 3;
        if (r1 != r2) goto L_0x0479;
    L_0x0478:
        goto L_0x0488;
    L_0x0479:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r2 = r0.scale;
        r1 = r1 * r2;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = 0;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x041a;
    L_0x0488:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
        r2 = r0.currentIcon;
        r3 = 4;
        if (r2 != r3) goto L_0x0496;
    L_0x0493:
        r2 = r1;
        r5 = 0;
        goto L_0x049c;
    L_0x0496:
        r2 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r5 = r5 * r2;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x049c:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r4 = r0.scale;
        r3 = r3 * r4;
        r1 = r1 * r19;
        r1 = (int) r1;
        r4 = r0.nextIcon;
        if (r4 != r8) goto L_0x04b2;
    L_0x04ac:
        r4 = r13.left;
        r4 = (float) r4;
        r6 = r13.top;
        goto L_0x04bb;
    L_0x04b2:
        r4 = r13.centerX();
        r4 = (float) r4;
        r6 = r13.centerY();
    L_0x04bb:
        r6 = (float) r6;
        r14 = r2;
        r15 = r5;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r1;
        r1 = r3;
        goto L_0x04f8;
    L_0x04c3:
        r1 = r0.nextIcon;
        r2 = 6;
        if (r1 != r2) goto L_0x04d3;
    L_0x04c8:
        r1 = r0.transitionProgress;
        r1 = r1 / r20;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = java.lang.Math.min(r2, r1);
        goto L_0x04d7;
    L_0x04d3:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = r0.transitionProgress;
    L_0x04d7:
        r5 = r2 - r1;
        r3 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r3 = r3 * r5;
        r4 = r0.scale;
        r3 = r3 * r4;
        r5 = r5 * r17;
        r4 = java.lang.Math.min(r2, r5);
        r4 = r4 * r19;
        r4 = (int) r4;
        r15 = r1;
        r1 = r3;
        r5 = r4;
        r4 = 0;
        r6 = 0;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x04f8:
        r3 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x0502;
    L_0x04fc:
        r33.save();
        r7.scale(r14, r14, r4, r6);
    L_0x0502:
        r2 = 0;
        r3 = (r15 > r2 ? 1 : (r15 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x050f;
    L_0x0507:
        r33.save();
        r2 = (float) r11;
        r3 = (float) r12;
        r7.rotate(r15, r2, r3);
    L_0x050f:
        if (r5 == 0) goto L_0x0585;
    L_0x0511:
        r2 = r0.paint;
        r3 = (float) r5;
        r4 = r0.overrideAlpha;
        r4 = r4 * r3;
        r4 = (int) r4;
        r2.setAlpha(r4);
        r2 = r0.currentIcon;
        if (r2 == r8) goto L_0x0549;
    L_0x0520:
        r2 = r0.nextIcon;
        if (r2 != r8) goto L_0x0525;
    L_0x0524:
        goto L_0x0549;
    L_0x0525:
        r2 = (float) r11;
        r16 = r2 - r1;
        r3 = (float) r12;
        r23 = r3 - r1;
        r24 = r2 + r1;
        r25 = r3 + r1;
        r6 = r0.paint;
        r1 = r33;
        r2 = r16;
        r3 = r23;
        r4 = r24;
        r9 = r5;
        r5 = r25;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r24;
        r4 = r16;
        r1.drawLine(r2, r3, r4, r5, r6);
        goto L_0x0586;
    L_0x0549:
        r9 = r5;
        r1 = r0.paint3;
        r2 = r0.overrideAlpha;
        r3 = r3 * r2;
        r2 = (int) r3;
        r1.setAlpha(r2);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r11 - r2;
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r12 - r3;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = r4 + r11;
        r4 = (float) r4;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r5 = r5 + r12;
        r5 = (float) r5;
        r1.set(r2, r3, r4, r5);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = (float) r3;
        r4 = r0.paint3;
        r7.drawRoundRect(r1, r2, r3, r4);
        goto L_0x0586;
    L_0x0585:
        r9 = r5;
    L_0x0586:
        r1 = 0;
        r2 = (r15 > r1 ? 1 : (r15 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x058e;
    L_0x058b:
        r33.restore();
    L_0x058e:
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x059e;
    L_0x0593:
        if (r1 == r8) goto L_0x059e;
    L_0x0595:
        r3 = 4;
        if (r1 != r3) goto L_0x0609;
    L_0x0598:
        r1 = r0.nextIcon;
        if (r1 == r8) goto L_0x059e;
    L_0x059c:
        if (r1 != r2) goto L_0x0609;
    L_0x059e:
        if (r9 == 0) goto L_0x0609;
    L_0x05a0:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r3 = r0.animatedDownloadProgress;
        r3 = r3 * r2;
        r15 = java.lang.Math.max(r1, r3);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x05b1;
    L_0x05b0:
        goto L_0x05b3;
    L_0x05b1:
        r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x05b3:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r0.rect;
        r3 = r13.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r4 = r13.top;
        r4 = r4 + r1;
        r4 = (float) r4;
        r5 = r13.right;
        r5 = r5 - r1;
        r5 = (float) r5;
        r6 = r13.bottom;
        r6 = r6 - r1;
        r1 = (float) r6;
        r2.set(r3, r4, r5, r1);
        r1 = r0.currentIcon;
        if (r1 == r8) goto L_0x05da;
    L_0x05d0:
        r2 = 4;
        if (r1 != r2) goto L_0x05fc;
    L_0x05d3:
        r1 = r0.nextIcon;
        if (r1 == r8) goto L_0x05da;
    L_0x05d7:
        r2 = 3;
        if (r1 != r2) goto L_0x05fc;
    L_0x05da:
        r1 = r0.paint;
        r2 = (float) r9;
        r3 = NUM; // 0x3e19999a float:0.15 double:5.147497604E-315;
        r2 = r2 * r3;
        r3 = r0.overrideAlpha;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r2 = r0.rect;
        r3 = 0;
        r4 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r5 = 0;
        r6 = r0.paint;
        r1 = r33;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r0.paint;
        r1.setAlpha(r9);
    L_0x05fc:
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r33;
        r4 = r15;
        r1.drawArc(r2, r3, r4, r5, r6);
    L_0x0609:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0612;
    L_0x060f:
        r33.restore();
    L_0x0612:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x061d;
    L_0x0618:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x063e;
    L_0x061d:
        r2 = 4;
        if (r1 != r2) goto L_0x0627;
    L_0x0620:
        r5 = r0.transitionProgress;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = r1 - r5;
        goto L_0x063c;
    L_0x0627:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = r0.transitionProgress;
        r2 = r2 / r20;
        r5 = java.lang.Math.min(r1, r2);
        r2 = r0.transitionProgress;
        r2 = r2 / r20;
        r2 = r1 - r2;
        r1 = 0;
        r2 = java.lang.Math.max(r1, r2);
    L_0x063c:
        r14 = r2;
        r9 = r5;
    L_0x063e:
        r1 = r0.nextIcon;
        r2 = 5;
        if (r1 != r2) goto L_0x0648;
    L_0x0643:
        r1 = org.telegram.ui.ActionBar.Theme.chat_filePath;
        r15 = r1;
        r6 = 0;
        goto L_0x0653;
    L_0x0648:
        r1 = r0.currentIcon;
        r2 = 5;
        if (r1 != r2) goto L_0x0651;
    L_0x064d:
        r1 = org.telegram.ui.ActionBar.Theme.chat_filePath;
        r6 = r1;
        goto L_0x0652;
    L_0x0651:
        r6 = 0;
    L_0x0652:
        r15 = 0;
    L_0x0653:
        r1 = r0.nextIcon;
        r2 = 7;
        if (r1 != r2) goto L_0x065b;
    L_0x0658:
        r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
        goto L_0x0666;
    L_0x065b:
        r1 = r0.currentIcon;
        r2 = 7;
        if (r1 != r2) goto L_0x0665;
    L_0x0660:
        r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
        r2 = r1;
        r1 = 0;
        goto L_0x0667;
    L_0x0665:
        r1 = 0;
    L_0x0666:
        r2 = 0;
    L_0x0667:
        r3 = r0.nextIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x0670;
    L_0x066d:
        r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
        goto L_0x0678;
    L_0x0670:
        r3 = r0.currentIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x0678;
    L_0x0676:
        r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
    L_0x0678:
        r5 = r1;
        r4 = r2;
        r1 = r0.currentIcon;
        r2 = 9;
        if (r1 == r2) goto L_0x0691;
    L_0x0680:
        r1 = r0.nextIcon;
        r2 = 9;
        if (r1 != r2) goto L_0x0687;
    L_0x0686:
        goto L_0x0691;
    L_0x0687:
        r10 = r4;
        r29 = r6;
        r26 = r14;
        r16 = r15;
        r14 = r5;
        goto L_0x0716;
    L_0x0691:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x069c;
    L_0x0699:
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x06a1;
    L_0x069c:
        r2 = r0.transitionProgress;
        r2 = r2 * r19;
        r3 = (int) r2;
    L_0x06a1:
        r1.setAlpha(r3);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = r12 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r11 - r1;
        r1 = r0.currentIcon;
        r8 = r0.nextIcon;
        if (r1 == r8) goto L_0x06c2;
    L_0x06b8:
        r33.save();
        r1 = r0.transitionProgress;
        r8 = (float) r11;
        r10 = (float) r12;
        r7.scale(r1, r1, r8, r10);
    L_0x06c2:
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r2 - r1;
        r8 = (float) r1;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r3 - r1;
        r10 = (float) r1;
        r1 = (float) r2;
        r16 = r15;
        r15 = (float) r3;
        r17 = r6;
        r6 = r0.paint;
        r23 = r1;
        r1 = r33;
        r24 = r2;
        r2 = r8;
        r8 = r3;
        r3 = r10;
        r10 = r4;
        r4 = r23;
        r26 = r14;
        r14 = r5;
        r5 = r15;
        r29 = r17;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r24 + r1;
        r4 = (float) r2;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = r8 - r1;
        r5 = (float) r3;
        r6 = r0.paint;
        r1 = r33;
        r2 = r23;
        r3 = r15;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x0716;
    L_0x0713:
        r33.restore();
    L_0x0716:
        r1 = r0.currentIcon;
        r2 = 12;
        if (r1 == r2) goto L_0x0722;
    L_0x071c:
        r1 = r0.nextIcon;
        r2 = 12;
        if (r1 != r2) goto L_0x0790;
    L_0x0722:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x072b;
    L_0x0728:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0738;
    L_0x072b:
        r1 = 13;
        if (r2 != r1) goto L_0x0732;
    L_0x072f:
        r5 = r0.transitionProgress;
        goto L_0x0738;
    L_0x0732:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
    L_0x0738:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x0743;
    L_0x0740:
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0746;
    L_0x0743:
        r2 = r5 * r19;
        r3 = (int) r2;
    L_0x0746:
        r1.setAlpha(r3);
        org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x075f;
    L_0x0757:
        r33.save();
        r1 = (float) r11;
        r2 = (float) r12;
        r7.scale(r5, r5, r1, r2);
    L_0x075f:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r2 = r0.scale;
        r1 = r1 * r2;
        r2 = (float) r11;
        r8 = r2 - r1;
        r3 = (float) r12;
        r15 = r3 - r1;
        r17 = r2 + r1;
        r23 = r3 + r1;
        r6 = r0.paint;
        r1 = r33;
        r2 = r8;
        r3 = r15;
        r4 = r17;
        r5 = r23;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r17;
        r4 = r8;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x0790;
    L_0x078d:
        r33.restore();
    L_0x0790:
        r1 = r0.currentIcon;
        r2 = 13;
        if (r1 == r2) goto L_0x079c;
    L_0x0796:
        r1 = r0.nextIcon;
        r2 = 13;
        if (r1 != r2) goto L_0x081c;
    L_0x079c:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x07a5;
    L_0x07a2:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x07b2;
    L_0x07a5:
        r1 = 13;
        if (r2 != r1) goto L_0x07ac;
    L_0x07a9:
        r5 = r0.transitionProgress;
        goto L_0x07b2;
    L_0x07ac:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
    L_0x07b2:
        r1 = r0.textPaint;
        r2 = r5 * r19;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r1 + r12;
        r2 = r0.percentStringWidth;
        r3 = 2;
        r2 = r2 / r3;
        r2 = r11 - r2;
        r3 = r0.currentIcon;
        r4 = r0.nextIcon;
        if (r3 == r4) goto L_0x07d5;
    L_0x07cd:
        r33.save();
        r3 = (float) r11;
        r4 = (float) r12;
        r7.scale(r5, r5, r3, r4);
    L_0x07d5:
        r3 = r0.animatedDownloadProgress;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r3 * r4;
        r3 = (int) r3;
        r4 = r0.percentString;
        if (r4 == 0) goto L_0x07e4;
    L_0x07e0:
        r4 = r0.lastPercent;
        if (r3 == r4) goto L_0x080a;
    L_0x07e4:
        r0.lastPercent = r3;
        r3 = 1;
        r4 = new java.lang.Object[r3];
        r3 = r0.lastPercent;
        r3 = java.lang.Integer.valueOf(r3);
        r5 = 0;
        r4[r5] = r3;
        r3 = "%d%%";
        r3 = java.lang.String.format(r3, r4);
        r0.percentString = r3;
        r3 = r0.textPaint;
        r4 = r0.percentString;
        r3 = r3.measureText(r4);
        r3 = (double) r3;
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r0.percentStringWidth = r3;
    L_0x080a:
        r3 = r0.percentString;
        r2 = (float) r2;
        r1 = (float) r1;
        r4 = r0.textPaint;
        r7.drawText(r3, r2, r1, r4);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x081c;
    L_0x0819:
        r33.restore();
    L_0x081c:
        r1 = r0.currentIcon;
        r2 = 1;
        if (r1 == 0) goto L_0x0836;
    L_0x0821:
        if (r1 == r2) goto L_0x0836;
    L_0x0823:
        r1 = r0.nextIcon;
        if (r1 == 0) goto L_0x0836;
    L_0x0827:
        if (r1 != r2) goto L_0x082a;
    L_0x0829:
        goto L_0x0836;
    L_0x082a:
        r23 = r10;
        r30 = r11;
        r24 = r12;
        r17 = r14;
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0b29;
    L_0x0836:
        r1 = r0.currentIcon;
        if (r1 != 0) goto L_0x083e;
    L_0x083a:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0846;
    L_0x083e:
        r1 = r0.currentIcon;
        if (r1 != r2) goto L_0x0853;
    L_0x0842:
        r1 = r0.nextIcon;
        if (r1 != 0) goto L_0x0853;
    L_0x0846:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x0853;
    L_0x084a:
        r1 = r0.interpolator;
        r2 = r0.transitionProgress;
        r1 = r1.getInterpolation(r2);
        goto L_0x0854;
    L_0x0853:
        r1 = 0;
    L_0x0854:
        r2 = r0.path1;
        r2.reset();
        r2 = r0.path2;
        r2.reset();
        r2 = r0.currentIcon;
        if (r2 == 0) goto L_0x0872;
    L_0x0862:
        r3 = 1;
        if (r2 == r3) goto L_0x086a;
    L_0x0865:
        r2 = 0;
        r3 = 0;
        r4 = 0;
    L_0x0868:
        r15 = 0;
        goto L_0x0879;
    L_0x086a:
        r2 = pausePath1;
        r3 = pausePath2;
        r15 = 90;
        r4 = 0;
        goto L_0x0879;
    L_0x0872:
        r2 = playPath1;
        r3 = playPath2;
        r4 = playFinalPath;
        goto L_0x0868;
    L_0x0879:
        r5 = r0.nextIcon;
        if (r5 == 0) goto L_0x088b;
    L_0x087d:
        r6 = 1;
        if (r5 == r6) goto L_0x0884;
    L_0x0880:
        r5 = 0;
        r6 = 0;
    L_0x0882:
        r8 = 0;
        goto L_0x0890;
    L_0x0884:
        r5 = pausePath1;
        r6 = pausePath2;
        r8 = 90;
        goto L_0x0890;
    L_0x088b:
        r5 = playPath1;
        r6 = playPath2;
        goto L_0x0882;
    L_0x0890:
        if (r2 != 0) goto L_0x0899;
    L_0x0892:
        r2 = r5;
        r3 = r6;
        r17 = r14;
        r5 = 0;
        r6 = 0;
        goto L_0x089b;
    L_0x0899:
        r17 = r14;
    L_0x089b:
        r14 = r0.animatingTransition;
        if (r14 != 0) goto L_0x093c;
    L_0x089f:
        if (r4 == 0) goto L_0x093c;
    L_0x08a1:
        r2 = 0;
    L_0x08a2:
        r3 = r4.length;
        r5 = 2;
        r3 = r3 / r5;
        if (r2 >= r3) goto L_0x092e;
    L_0x08a7:
        if (r2 != 0) goto L_0x08e8;
    L_0x08a9:
        r3 = r0.path1;
        r5 = r2 * 2;
        r6 = r4[r5];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r14 = r0.scale;
        r6 = r6 * r14;
        r14 = r5 + 1;
        r22 = r4[r14];
        r23 = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r10 = (float) r10;
        r22 = r9;
        r9 = r0.scale;
        r10 = r10 * r9;
        r3.moveTo(r6, r10);
        r3 = r0.path2;
        r5 = r4[r5];
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r0.scale;
        r5 = r5 * r6;
        r6 = r4[r14];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r9 = r0.scale;
        r6 = r6 * r9;
        r3.moveTo(r5, r6);
        goto L_0x0926;
    L_0x08e8:
        r22 = r9;
        r23 = r10;
        r3 = r0.path1;
        r5 = r2 * 2;
        r6 = r4[r5];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r9 = r0.scale;
        r6 = r6 * r9;
        r9 = r5 + 1;
        r10 = r4[r9];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r14 = r0.scale;
        r10 = r10 * r14;
        r3.lineTo(r6, r10);
        r3 = r0.path2;
        r5 = r4[r5];
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r0.scale;
        r5 = r5 * r6;
        r6 = r4[r9];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r9 = r0.scale;
        r6 = r6 * r9;
        r3.lineTo(r5, r6);
    L_0x0926:
        r2 = r2 + 1;
        r9 = r22;
        r10 = r23;
        goto L_0x08a2;
    L_0x092e:
        r22 = r9;
        r23 = r10;
        r28 = r8;
        r30 = r11;
        r24 = r12;
    L_0x0938:
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0ad0;
    L_0x093c:
        r22 = r9;
        r23 = r10;
        if (r5 != 0) goto L_0x09f4;
    L_0x0942:
        r4 = 0;
    L_0x0943:
        r5 = 5;
        if (r4 >= r5) goto L_0x09c7;
    L_0x0946:
        if (r4 != 0) goto L_0x0985;
    L_0x0948:
        r5 = r0.path1;
        r6 = r4 * 2;
        r9 = r2[r6];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r10 = r0.scale;
        r9 = r9 * r10;
        r10 = r6 + 1;
        r14 = r2[r10];
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r24 = r12;
        r12 = r0.scale;
        r14 = r14 * r12;
        r5.moveTo(r9, r14);
        r5 = r0.path2;
        r6 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r9 = r0.scale;
        r6 = r6 * r9;
        r9 = r3[r10];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r10 = r0.scale;
        r9 = r9 * r10;
        r5.moveTo(r6, r9);
        goto L_0x09c1;
    L_0x0985:
        r24 = r12;
        r5 = r0.path1;
        r6 = r4 * 2;
        r9 = r2[r6];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r10 = r0.scale;
        r9 = r9 * r10;
        r10 = r6 + 1;
        r12 = r2[r10];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r14 = r0.scale;
        r12 = r12 * r14;
        r5.lineTo(r9, r12);
        r5 = r0.path2;
        r6 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r9 = r0.scale;
        r6 = r6 * r9;
        r9 = r3[r10];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r10 = r0.scale;
        r9 = r9 * r10;
        r5.lineTo(r6, r9);
    L_0x09c1:
        r4 = r4 + 1;
        r12 = r24;
        goto L_0x0943;
    L_0x09c7:
        r24 = r12;
        r2 = r0.nextIcon;
        r3 = 4;
        if (r2 != r3) goto L_0x09e2;
    L_0x09ce:
        r2 = r0.paint2;
        r3 = r0.transitionProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r4 - r3;
        r5 = r5 * r19;
        r3 = (int) r5;
        r2.setAlpha(r3);
    L_0x09dc:
        r28 = r8;
        r30 = r11;
        goto L_0x0938;
    L_0x09e2:
        r3 = r0.paint2;
        r4 = r0.currentIcon;
        if (r4 != r2) goto L_0x09eb;
    L_0x09e8:
        r2 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x09f0;
    L_0x09eb:
        r2 = r0.transitionProgress;
        r2 = r2 * r19;
        r2 = (int) r2;
    L_0x09f0:
        r3.setAlpha(r2);
        goto L_0x09dc;
    L_0x09f4:
        r24 = r12;
        r4 = 0;
    L_0x09f7:
        r9 = 5;
        if (r4 >= r9) goto L_0x0ac5;
    L_0x09fa:
        if (r4 != 0) goto L_0x0a5e;
    L_0x09fc:
        r9 = r0.path1;
        r10 = r4 * 2;
        r12 = r2[r10];
        r14 = r5[r10];
        r28 = r2[r10];
        r14 = r14 - r28;
        r14 = r14 * r1;
        r12 = r12 + r14;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r14 = r0.scale;
        r12 = r12 * r14;
        r14 = r10 + 1;
        r28 = r2[r14];
        r30 = r5[r14];
        r31 = r2[r14];
        r30 = r30 - r31;
        r30 = r30 * r1;
        r28 = r28 + r30;
        r30 = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r28);
        r11 = (float) r11;
        r28 = r8;
        r8 = r0.scale;
        r11 = r11 * r8;
        r9.moveTo(r12, r11);
        r8 = r0.path2;
        r9 = r3[r10];
        r11 = r6[r10];
        r10 = r3[r10];
        r11 = r11 - r10;
        r11 = r11 * r1;
        r9 = r9 + r11;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r10 = r0.scale;
        r9 = r9 * r10;
        r10 = r3[r14];
        r11 = r6[r14];
        r12 = r3[r14];
        r11 = r11 - r12;
        r11 = r11 * r1;
        r10 = r10 + r11;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r8.moveTo(r9, r10);
        goto L_0x0abd;
    L_0x0a5e:
        r28 = r8;
        r30 = r11;
        r8 = r0.path1;
        r9 = r4 * 2;
        r10 = r2[r9];
        r11 = r5[r9];
        r12 = r2[r9];
        r11 = r11 - r12;
        r11 = r11 * r1;
        r10 = r10 + r11;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r11 = r9 + 1;
        r12 = r2[r11];
        r14 = r5[r11];
        r31 = r2[r11];
        r14 = r14 - r31;
        r14 = r14 * r1;
        r12 = r12 + r14;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r14 = r0.scale;
        r12 = r12 * r14;
        r8.lineTo(r10, r12);
        r8 = r0.path2;
        r10 = r3[r9];
        r12 = r6[r9];
        r9 = r3[r9];
        r12 = r12 - r9;
        r12 = r12 * r1;
        r10 = r10 + r12;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = (float) r9;
        r10 = r0.scale;
        r9 = r9 * r10;
        r10 = r3[r11];
        r12 = r6[r11];
        r11 = r3[r11];
        r12 = r12 - r11;
        r12 = r12 * r1;
        r10 = r10 + r12;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r8.lineTo(r9, r10);
    L_0x0abd:
        r4 = r4 + 1;
        r8 = r28;
        r11 = r30;
        goto L_0x09f7;
    L_0x0ac5:
        r28 = r8;
        r30 = r11;
        r2 = r0.paint2;
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2.setAlpha(r8);
    L_0x0ad0:
        r2 = r0.path1;
        r2.close();
        r2 = r0.path2;
        r2.close();
        r33.save();
        r2 = r13.left;
        r2 = (float) r2;
        r3 = r13.top;
        r3 = (float) r3;
        r7.translate(r2, r3);
        r2 = (float) r15;
        r3 = r28 - r15;
        r3 = (float) r3;
        r3 = r3 * r1;
        r2 = r2 + r3;
        r1 = r13.left;
        r11 = r30 - r1;
        r1 = (float) r11;
        r3 = r13.top;
        r12 = r24 - r3;
        r3 = (float) r12;
        r7.rotate(r2, r1, r3);
        r1 = r0.currentIcon;
        if (r1 == 0) goto L_0x0b01;
    L_0x0afe:
        r2 = 1;
        if (r1 != r2) goto L_0x0b06;
    L_0x0b01:
        r1 = r0.currentIcon;
        r2 = 4;
        if (r1 != r2) goto L_0x0b16;
    L_0x0b06:
        r1 = r13.left;
        r11 = r30 - r1;
        r1 = (float) r11;
        r2 = r13.top;
        r12 = r24 - r2;
        r2 = (float) r12;
        r9 = r22;
        r7.scale(r9, r9, r1, r2);
        goto L_0x0b18;
    L_0x0b16:
        r9 = r22;
    L_0x0b18:
        r1 = r0.path1;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r1 = r0.path2;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r33.restore();
    L_0x0b29:
        r1 = r0.currentIcon;
        r2 = 6;
        if (r1 == r2) goto L_0x0b33;
    L_0x0b2e:
        r1 = r0.nextIcon;
        r2 = 6;
        if (r1 != r2) goto L_0x0bcd;
    L_0x0b33:
        r1 = r0.currentIcon;
        r2 = 6;
        if (r1 == r2) goto L_0x0b5c;
    L_0x0b38:
        r1 = r0.transitionProgress;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b58;
    L_0x0b3e:
        r1 = r1 - r20;
        r1 = r1 / r20;
        r2 = r1 / r20;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = java.lang.Math.min(r3, r2);
        r10 = r3 - r2;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b55;
    L_0x0b50:
        r1 = r1 - r20;
        r1 = r1 / r20;
        goto L_0x0b56;
    L_0x0b55:
        r1 = 0;
    L_0x0b56:
        r11 = r1;
        goto L_0x0b5f;
    L_0x0b58:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = 0;
        goto L_0x0b5f;
    L_0x0b5c:
        r10 = 0;
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0b5f:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r12 = r24 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r13 = r30 - r1;
        r1 = r0.paint;
        r1.setAlpha(r8);
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r2 >= 0) goto L_0x0ba9;
    L_0x0b78:
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r13 - r1;
        r2 = (float) r1;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r12 - r1;
        r3 = (float) r1;
        r1 = (float) r13;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r4 = r4 * r10;
        r4 = r1 - r4;
        r1 = (float) r12;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r5 = r5 * r10;
        r5 = r1 - r5;
        r6 = r0.paint;
        r1 = r33;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0ba9:
        r1 = 0;
        r2 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1));
        if (r2 <= 0) goto L_0x0bcd;
    L_0x0bae:
        r2 = (float) r13;
        r3 = (float) r12;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r1 * r11;
        r4 = r2 + r1;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r1 * r11;
        r5 = r3 - r1;
        r6 = r0.paint;
        r1 = r33;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0bcd:
        r1 = r17;
        if (r23 == 0) goto L_0x0CLASSNAME;
    L_0x0bd1:
        r2 = r23;
        if (r2 == r1) goto L_0x0CLASSNAME;
    L_0x0bd5:
        r3 = r2.getIntrinsicWidth();
        r3 = (float) r3;
        r3 = r3 * r26;
        r3 = (int) r3;
        r4 = r2.getIntrinsicHeight();
        r4 = (float) r4;
        r4 = r4 * r26;
        r4 = (int) r4;
        r5 = r0.colorFilter;
        r2.setColorFilter(r5);
        r5 = r0.currentIcon;
        r6 = r0.nextIcon;
        if (r5 != r6) goto L_0x0bf3;
    L_0x0bf0:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0bfc;
    L_0x0bf3:
        r5 = r0.transitionProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r6 - r5;
        r5 = r5 * r19;
        r5 = (int) r5;
    L_0x0bfc:
        r2.setAlpha(r5);
        r5 = 2;
        r3 = r3 / r5;
        r11 = r30 - r3;
        r4 = r4 / r5;
        r12 = r24 - r4;
        r3 = r30 + r3;
        r4 = r24 + r4;
        r2.setBounds(r11, r12, r3, r4);
        r2.draw(r7);
    L_0x0CLASSNAME:
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = r1.getIntrinsicWidth();
        r2 = (float) r2;
        r2 = r2 * r9;
        r2 = (int) r2;
        r3 = r1.getIntrinsicHeight();
        r3 = (float) r3;
        r3 = r3 * r9;
        r3 = (int) r3;
        r4 = r0.colorFilter;
        r1.setColorFilter(r4);
        r4 = r0.currentIcon;
        r5 = r0.nextIcon;
        if (r4 != r5) goto L_0x0CLASSNAME;
    L_0x0c2d:
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = r0.transitionProgress;
        r4 = r4 * r19;
        r4 = (int) r4;
    L_0x0CLASSNAME:
        r1.setAlpha(r4);
        r4 = 2;
        r2 = r2 / r4;
        r11 = r30 - r2;
        r3 = r3 / r4;
        r12 = r24 - r3;
        r2 = r30 + r2;
        r3 = r24 + r3;
        r1.setBounds(r11, r12, r2, r3);
        r1.draw(r7);
    L_0x0CLASSNAME:
        r1 = r29;
        r2 = r16;
        if (r1 == 0) goto L_0x0ca9;
    L_0x0c4f:
        if (r1 == r2) goto L_0x0ca9;
    L_0x0CLASSNAME:
        r3 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r3 * r26;
        r3 = (int) r3;
        r4 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r4 = r4 * r26;
        r4 = (int) r4;
        r5 = r0.paint2;
        r6 = android.graphics.Paint.Style.FILL_AND_STROKE;
        r5.setStyle(r6);
        r5 = r0.paint2;
        r6 = r0.currentIcon;
        r10 = r0.nextIcon;
        if (r6 != r10) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r0.transitionProgress;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r10 - r6;
        r6 = r6 * r19;
        r6 = (int) r6;
    L_0x0CLASSNAME:
        r5.setAlpha(r6);
        r33.save();
        r5 = 2;
        r3 = r3 / r5;
        r11 = r30 - r3;
        r3 = (float) r11;
        r4 = r4 / r5;
        r12 = r24 - r4;
        r4 = (float) r12;
        r7.translate(r3, r4);
        r3 = 0;
        r4 = r1[r3];
        r3 = r0.paint2;
        r7.drawPath(r4, r3);
        r3 = 1;
        r4 = r1[r3];
        if (r4 == 0) goto L_0x0ca6;
    L_0x0c9f:
        r1 = r1[r3];
        r3 = r0.backPaint;
        r7.drawPath(r1, r3);
    L_0x0ca6:
        r33.restore();
    L_0x0ca9:
        if (r2 == 0) goto L_0x0cff;
    L_0x0cab:
        r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r1 * r9;
        r1 = (int) r1;
        r3 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r3 * r9;
        r3 = (int) r3;
        r4 = r0.paint2;
        r5 = android.graphics.Paint.Style.FILL_AND_STROKE;
        r4.setStyle(r5);
        r4 = r0.paint2;
        r5 = r0.currentIcon;
        r6 = r0.nextIcon;
        if (r5 != r6) goto L_0x0cd1;
    L_0x0cce:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0cd6;
    L_0x0cd1:
        r5 = r0.transitionProgress;
        r5 = r5 * r19;
        r5 = (int) r5;
    L_0x0cd6:
        r4.setAlpha(r5);
        r33.save();
        r4 = 2;
        r1 = r1 / r4;
        r11 = r30 - r1;
        r1 = (float) r11;
        r3 = r3 / r4;
        r12 = r24 - r3;
        r3 = (float) r12;
        r7.translate(r1, r3);
        r1 = 0;
        r3 = r2[r1];
        r1 = r0.paint2;
        r7.drawPath(r3, r1);
        r1 = 1;
        r3 = r2[r1];
        if (r3 == 0) goto L_0x0cfc;
    L_0x0cf5:
        r2 = r2[r1];
        r1 = r0.backPaint;
        r7.drawPath(r2, r1);
    L_0x0cfc:
        r33.restore();
    L_0x0cff:
        r1 = java.lang.System.currentTimeMillis();
        r3 = r0.lastAnimationTime;
        r3 = r1 - r3;
        r5 = 17;
        r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r8 <= 0) goto L_0x0d0f;
    L_0x0d0d:
        r3 = 17;
    L_0x0d0f:
        r0.lastAnimationTime = r1;
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x0d2b;
    L_0x0d16:
        r2 = 14;
        if (r1 == r2) goto L_0x0d2b;
    L_0x0d1a:
        r5 = 4;
        if (r1 != r5) goto L_0x0d21;
    L_0x0d1d:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0d2b;
    L_0x0d21:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0d2b;
    L_0x0d27:
        r2 = 13;
        if (r1 != r2) goto L_0x0d78;
    L_0x0d2b:
        r1 = r0.downloadRadOffset;
        r5 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r5 = r5 * r3;
        r2 = (float) r5;
        r5 = NUM; // 0x451CLASSNAME float:2500.0 double:5.72858887E-315;
        r2 = r2 / r5;
        r1 = r1 + r2;
        r0.downloadRadOffset = r1;
        r1 = r0.downloadRadOffset;
        r1 = r0.getCircleValue(r1);
        r0.downloadRadOffset = r1;
        r1 = r0.nextIcon;
        r2 = 2;
        if (r1 == r2) goto L_0x0d75;
    L_0x0d46:
        r1 = r0.downloadProgress;
        r2 = r0.downloadProgressAnimationStart;
        r5 = r1 - r2;
        r6 = 0;
        r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r8 <= 0) goto L_0x0d75;
    L_0x0d51:
        r6 = r0.downloadProgressTime;
        r8 = (float) r3;
        r6 = r6 + r8;
        r0.downloadProgressTime = r6;
        r6 = r0.downloadProgressTime;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x0d67;
    L_0x0d5f:
        r0.animatedDownloadProgress = r1;
        r0.downloadProgressAnimationStart = r1;
        r1 = 0;
        r0.downloadProgressTime = r1;
        goto L_0x0d75;
    L_0x0d67:
        r1 = r0.interpolator;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r6 = r6 / r8;
        r1 = r1.getInterpolation(r6);
        r5 = r5 * r1;
        r2 = r2 + r5;
        r0.animatedDownloadProgress = r2;
    L_0x0d75:
        r32.invalidateSelf();
    L_0x0d78:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x0d9d;
    L_0x0d7c:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x0d9d;
    L_0x0d84:
        r3 = (float) r3;
        r4 = r0.transitionAnimationTime;
        r3 = r3 / r4;
        r1 = r1 + r3;
        r0.transitionProgress = r1;
        r1 = r0.transitionProgress;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x0d9a;
    L_0x0d91:
        r1 = r0.nextIcon;
        r0.currentIcon = r1;
        r0.transitionProgress = r2;
        r1 = 0;
        r0.animatingTransition = r1;
    L_0x0d9a:
        r32.invalidateSelf();
    L_0x0d9d:
        r1 = r27;
        r2 = 1;
        if (r1 < r2) goto L_0x0da5;
    L_0x0da2:
        r7.restoreToCount(r1);
    L_0x0da5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MediaActionDrawable.draw(android.graphics.Canvas):void");
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(48.0f);
    }
}
