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

    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x088b  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x09e6  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0934  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x087d  */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x088b  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0891 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0934  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x09e6  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0601  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x04ee  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x04f9  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0503  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x057d  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x058a  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0601  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x04ee  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x04f9  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0503  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x057d  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x058a  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0601  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0301  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0347  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0369  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x039d  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d7 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0301  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0347  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0369  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x038b  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x039d  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0705  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0b2a  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0bc3  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0cff  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d38  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d6e  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d94  */
    /* JADX WARNING: Removed duplicated region for block: B:483:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing block: B:103:0x036d, code skipped:
            if (r1 != 3) goto L_0x0371;
     */
    /* JADX WARNING: Missing block: B:191:0x058e, code skipped:
            if (r1 != 3) goto L_0x05fb;
     */
    /* JADX WARNING: Missing block: B:203:0x05ca, code skipped:
            if (r1 != 3) goto L_0x05ee;
     */
    /* JADX WARNING: Missing block: B:452:0x0d1b, code skipped:
            if (r1 != 13) goto L_0x0d6a;
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
        goto L_0x035e;
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
        if (r5 == 0) goto L_0x0301;
    L_0x0141:
        r5 = r0.nextIcon;
        if (r5 == r2) goto L_0x02ae;
    L_0x0145:
        r5 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1));
        if (r5 > 0) goto L_0x014b;
    L_0x0149:
        goto L_0x02ae;
    L_0x014b:
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r0.scale;
        r5 = r5 * r6;
        r3 = r3 - r20;
        r6 = r3 / r20;
        r9 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r9 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));
        if (r9 <= 0) goto L_0x016d;
    L_0x0161:
        r9 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r3 = r3 - r9;
        r9 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r3 = r3 / r9;
        r9 = r3;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0173;
    L_0x016d:
        r9 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r3 = r3 / r9;
        r15 = r3;
        r9 = 0;
    L_0x0173:
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
        if (r2 <= 0) goto L_0x02a7;
    L_0x01ac:
        r1 = r0.nextIcon;
        if (r1 != r8) goto L_0x01b2;
    L_0x01b0:
        r13 = 0;
        goto L_0x01bb;
    L_0x01b2:
        r1 = -NUM; // 0xffffffffCLASSNAME float:-45.0 double:NaN;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r9;
        r1 = r1 * r5;
        r13 = r1;
    L_0x01bb:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r1 = r1 * r9;
        r2 = r0.scale;
        r1 = r1 * r2;
        r9 = r9 * r19;
        r2 = (int) r9;
        r3 = r0.nextIcon;
        r4 = 3;
        if (r3 == r4) goto L_0x01e4;
    L_0x01ce:
        if (r3 == r8) goto L_0x01e4;
    L_0x01d0:
        r9 = 2;
        if (r3 == r9) goto L_0x01e5;
    L_0x01d3:
        r3 = r0.transitionProgress;
        r3 = r3 / r20;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = java.lang.Math.min(r4, r3);
        r5 = r4 - r3;
        r2 = (float) r2;
        r2 = r2 * r5;
        r2 = (int) r2;
        goto L_0x01e5;
    L_0x01e4:
        r9 = 2;
    L_0x01e5:
        r15 = r2;
        r2 = 0;
        r3 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x01f4;
    L_0x01eb:
        r33.save();
        r2 = r26;
        r7.rotate(r13, r10, r2);
        goto L_0x01f6;
    L_0x01f4:
        r2 = r26;
    L_0x01f6:
        if (r15 == 0) goto L_0x029c;
    L_0x01f8:
        r3 = r0.paint;
        r3.setAlpha(r15);
        r3 = r0.nextIcon;
        if (r3 != r8) goto L_0x027c;
    L_0x0201:
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
        if (r1 == 0) goto L_0x0249;
    L_0x0246:
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x024b;
    L_0x0249:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x024b:
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
        goto L_0x029e;
    L_0x027c:
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
        goto L_0x029e;
    L_0x029c:
        r28 = r23;
    L_0x029e:
        r1 = 0;
        r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x02a9;
    L_0x02a3:
        r33.restore();
        goto L_0x02a9;
    L_0x02a7:
        r28 = r23;
    L_0x02a9:
        r1 = r10;
        r2 = r1;
        r3 = r14;
        r10 = 2;
        goto L_0x02fb;
    L_0x02ae:
        r27 = r4;
        r28 = r8;
        r8 = 14;
        r10 = 2;
        r1 = r0.nextIcon;
        if (r1 != r10) goto L_0x02be;
    L_0x02b9:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r1 - r3;
        goto L_0x02c4;
    L_0x02be:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r3 / r20;
        r3 = r1 - r5;
    L_0x02c4:
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
    L_0x02fb:
        r15 = r1;
        r9 = r2;
        r13 = r3;
        r3 = r24;
        goto L_0x0334;
    L_0x0301:
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
    L_0x0334:
        r1 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
        if (r1 == 0) goto L_0x0342;
    L_0x0338:
        r4 = (float) r11;
        r6 = r0.paint;
        r1 = r33;
        r2 = r4;
        r5 = r14;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0342:
        r6 = (float) r11;
        r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x035e;
    L_0x0347:
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
    L_0x035e:
        r1 = r0.currentIcon;
        r9 = 1;
        r2 = 3;
        if (r1 == r2) goto L_0x03e4;
    L_0x0364:
        if (r1 == r8) goto L_0x03e4;
    L_0x0366:
        r3 = 4;
        if (r1 != r3) goto L_0x0371;
    L_0x0369:
        r1 = r0.nextIcon;
        if (r1 == r8) goto L_0x03e4;
    L_0x036d:
        if (r1 != r2) goto L_0x0371;
    L_0x036f:
        goto L_0x03e4;
    L_0x0371:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0386;
    L_0x0377:
        r2 = r0.nextIcon;
        r3 = 10;
        if (r2 == r3) goto L_0x0386;
    L_0x037d:
        r2 = 13;
        if (r1 != r2) goto L_0x0382;
    L_0x0381:
        goto L_0x0386;
    L_0x0382:
        r13 = r28;
        goto L_0x0604;
    L_0x0386:
        r1 = r0.nextIcon;
        r2 = 4;
        if (r1 == r2) goto L_0x0392;
    L_0x038b:
        r2 = 6;
        if (r1 != r2) goto L_0x038f;
    L_0x038e:
        goto L_0x0392;
    L_0x038f:
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x039b;
    L_0x0392:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
        r5 = r5 * r19;
        r3 = (int) r5;
    L_0x039b:
        if (r3 == 0) goto L_0x0382;
    L_0x039d:
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
        if (r1 == 0) goto L_0x03b9;
    L_0x03b8:
        goto L_0x03bb;
    L_0x03b9:
        r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x03bb:
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
        goto L_0x0604;
    L_0x03e4:
        r13 = r28;
        r1 = r0.nextIcon;
        if (r1 != r10) goto L_0x0412;
    L_0x03ea:
        r1 = r0.transitionProgress;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 > 0) goto L_0x0406;
    L_0x03f0:
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
        goto L_0x0408;
    L_0x0406:
        r1 = 0;
        r3 = 0;
    L_0x0408:
        r5 = r3;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = 0;
    L_0x040c:
        r6 = 0;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = 0;
        goto L_0x04ea;
    L_0x0412:
        if (r1 == 0) goto L_0x04b5;
    L_0x0414:
        if (r1 == r9) goto L_0x04b5;
    L_0x0416:
        r2 = 5;
        if (r1 == r2) goto L_0x04b5;
    L_0x0419:
        r2 = 8;
        if (r1 == r2) goto L_0x04b5;
    L_0x041d:
        r2 = 9;
        if (r1 == r2) goto L_0x04b5;
    L_0x0421:
        r2 = 7;
        if (r1 == r2) goto L_0x04b5;
    L_0x0424:
        r2 = 6;
        if (r1 != r2) goto L_0x0429;
    L_0x0427:
        goto L_0x04b5;
    L_0x0429:
        r2 = 4;
        if (r1 != r2) goto L_0x0465;
    L_0x042c:
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
        if (r4 != r8) goto L_0x044c;
    L_0x0442:
        r1 = r13.left;
        r1 = (float) r1;
        r4 = r13.top;
        r4 = (float) r4;
        r6 = r4;
        r4 = r1;
        r1 = 0;
        goto L_0x045d;
    L_0x044c:
        r4 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r4;
        r4 = r13.centerX();
        r4 = (float) r4;
        r5 = r13.centerY();
        r5 = (float) r5;
        r6 = r5;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x045d:
        r15 = r1;
        r1 = r2;
        r14 = r5;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r3;
        goto L_0x04ea;
    L_0x0465:
        if (r1 == r8) goto L_0x047a;
    L_0x0467:
        r2 = 3;
        if (r1 != r2) goto L_0x046b;
    L_0x046a:
        goto L_0x047a;
    L_0x046b:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r2 = r0.scale;
        r1 = r1 * r2;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = 0;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x040c;
    L_0x047a:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
        r2 = r0.currentIcon;
        r3 = 4;
        if (r2 != r3) goto L_0x0488;
    L_0x0485:
        r2 = r1;
        r5 = 0;
        goto L_0x048e;
    L_0x0488:
        r2 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r5 = r5 * r2;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x048e:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r4 = r0.scale;
        r3 = r3 * r4;
        r1 = r1 * r19;
        r1 = (int) r1;
        r4 = r0.nextIcon;
        if (r4 != r8) goto L_0x04a4;
    L_0x049e:
        r4 = r13.left;
        r4 = (float) r4;
        r6 = r13.top;
        goto L_0x04ad;
    L_0x04a4:
        r4 = r13.centerX();
        r4 = (float) r4;
        r6 = r13.centerY();
    L_0x04ad:
        r6 = (float) r6;
        r14 = r2;
        r15 = r5;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r1;
        r1 = r3;
        goto L_0x04ea;
    L_0x04b5:
        r1 = r0.nextIcon;
        r2 = 6;
        if (r1 != r2) goto L_0x04c5;
    L_0x04ba:
        r1 = r0.transitionProgress;
        r1 = r1 / r20;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = java.lang.Math.min(r2, r1);
        goto L_0x04c9;
    L_0x04c5:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = r0.transitionProgress;
    L_0x04c9:
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
    L_0x04ea:
        r3 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x04f4;
    L_0x04ee:
        r33.save();
        r7.scale(r14, r14, r4, r6);
    L_0x04f4:
        r2 = 0;
        r3 = (r15 > r2 ? 1 : (r15 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x0501;
    L_0x04f9:
        r33.save();
        r2 = (float) r11;
        r3 = (float) r12;
        r7.rotate(r15, r2, r3);
    L_0x0501:
        if (r5 == 0) goto L_0x0577;
    L_0x0503:
        r2 = r0.paint;
        r3 = (float) r5;
        r4 = r0.overrideAlpha;
        r4 = r4 * r3;
        r4 = (int) r4;
        r2.setAlpha(r4);
        r2 = r0.currentIcon;
        if (r2 == r8) goto L_0x053b;
    L_0x0512:
        r2 = r0.nextIcon;
        if (r2 != r8) goto L_0x0517;
    L_0x0516:
        goto L_0x053b;
    L_0x0517:
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
        goto L_0x0578;
    L_0x053b:
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
        goto L_0x0578;
    L_0x0577:
        r9 = r5;
    L_0x0578:
        r1 = 0;
        r2 = (r15 > r1 ? 1 : (r15 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0580;
    L_0x057d:
        r33.restore();
    L_0x0580:
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x0590;
    L_0x0585:
        if (r1 == r8) goto L_0x0590;
    L_0x0587:
        r3 = 4;
        if (r1 != r3) goto L_0x05fb;
    L_0x058a:
        r1 = r0.nextIcon;
        if (r1 == r8) goto L_0x0590;
    L_0x058e:
        if (r1 != r2) goto L_0x05fb;
    L_0x0590:
        if (r9 == 0) goto L_0x05fb;
    L_0x0592:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r3 = r0.animatedDownloadProgress;
        r3 = r3 * r2;
        r15 = java.lang.Math.max(r1, r3);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x05a3;
    L_0x05a2:
        goto L_0x05a5;
    L_0x05a3:
        r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x05a5:
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
        if (r1 == r8) goto L_0x05cc;
    L_0x05c2:
        r2 = 4;
        if (r1 != r2) goto L_0x05ee;
    L_0x05c5:
        r1 = r0.nextIcon;
        if (r1 == r8) goto L_0x05cc;
    L_0x05c9:
        r2 = 3;
        if (r1 != r2) goto L_0x05ee;
    L_0x05cc:
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
    L_0x05ee:
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r33;
        r4 = r15;
        r1.drawArc(r2, r3, r4, r5, r6);
    L_0x05fb:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0604;
    L_0x0601:
        r33.restore();
    L_0x0604:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x060f;
    L_0x060a:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0630;
    L_0x060f:
        r2 = 4;
        if (r1 != r2) goto L_0x0619;
    L_0x0612:
        r5 = r0.transitionProgress;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = r1 - r5;
        goto L_0x062e;
    L_0x0619:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = r0.transitionProgress;
        r2 = r2 / r20;
        r5 = java.lang.Math.min(r1, r2);
        r2 = r0.transitionProgress;
        r2 = r2 / r20;
        r2 = r1 - r2;
        r1 = 0;
        r2 = java.lang.Math.max(r1, r2);
    L_0x062e:
        r14 = r2;
        r9 = r5;
    L_0x0630:
        r1 = r0.nextIcon;
        r2 = 5;
        if (r1 != r2) goto L_0x063a;
    L_0x0635:
        r1 = org.telegram.ui.ActionBar.Theme.chat_filePath;
        r15 = r1;
        r6 = 0;
        goto L_0x0645;
    L_0x063a:
        r1 = r0.currentIcon;
        r2 = 5;
        if (r1 != r2) goto L_0x0643;
    L_0x063f:
        r1 = org.telegram.ui.ActionBar.Theme.chat_filePath;
        r6 = r1;
        goto L_0x0644;
    L_0x0643:
        r6 = 0;
    L_0x0644:
        r15 = 0;
    L_0x0645:
        r1 = r0.nextIcon;
        r2 = 7;
        if (r1 != r2) goto L_0x064d;
    L_0x064a:
        r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
        goto L_0x0658;
    L_0x064d:
        r1 = r0.currentIcon;
        r2 = 7;
        if (r1 != r2) goto L_0x0657;
    L_0x0652:
        r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
        r2 = r1;
        r1 = 0;
        goto L_0x0659;
    L_0x0657:
        r1 = 0;
    L_0x0658:
        r2 = 0;
    L_0x0659:
        r3 = r0.nextIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x0662;
    L_0x065f:
        r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
        goto L_0x066a;
    L_0x0662:
        r3 = r0.currentIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x066a;
    L_0x0668:
        r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
    L_0x066a:
        r5 = r1;
        r4 = r2;
        r1 = r0.currentIcon;
        r2 = 9;
        if (r1 == r2) goto L_0x0683;
    L_0x0672:
        r1 = r0.nextIcon;
        r2 = 9;
        if (r1 != r2) goto L_0x0679;
    L_0x0678:
        goto L_0x0683;
    L_0x0679:
        r10 = r4;
        r29 = r6;
        r26 = r14;
        r16 = r15;
        r14 = r5;
        goto L_0x0708;
    L_0x0683:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x068e;
    L_0x068b:
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0693;
    L_0x068e:
        r2 = r0.transitionProgress;
        r2 = r2 * r19;
        r3 = (int) r2;
    L_0x0693:
        r1.setAlpha(r3);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = r12 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r11 - r1;
        r1 = r0.currentIcon;
        r8 = r0.nextIcon;
        if (r1 == r8) goto L_0x06b4;
    L_0x06aa:
        r33.save();
        r1 = r0.transitionProgress;
        r8 = (float) r11;
        r10 = (float) r12;
        r7.scale(r1, r1, r8, r10);
    L_0x06b4:
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
        if (r1 == r2) goto L_0x0708;
    L_0x0705:
        r33.restore();
    L_0x0708:
        r1 = r0.currentIcon;
        r2 = 12;
        if (r1 == r2) goto L_0x0714;
    L_0x070e:
        r1 = r0.nextIcon;
        r2 = 12;
        if (r1 != r2) goto L_0x0782;
    L_0x0714:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x071d;
    L_0x071a:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x072a;
    L_0x071d:
        r1 = 13;
        if (r2 != r1) goto L_0x0724;
    L_0x0721:
        r5 = r0.transitionProgress;
        goto L_0x072a;
    L_0x0724:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
    L_0x072a:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x0735;
    L_0x0732:
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0738;
    L_0x0735:
        r2 = r5 * r19;
        r3 = (int) r2;
    L_0x0738:
        r1.setAlpha(r3);
        org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x0751;
    L_0x0749:
        r33.save();
        r1 = (float) r11;
        r2 = (float) r12;
        r7.scale(r5, r5, r1, r2);
    L_0x0751:
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
        if (r1 == r2) goto L_0x0782;
    L_0x077f:
        r33.restore();
    L_0x0782:
        r1 = r0.currentIcon;
        r2 = 13;
        if (r1 == r2) goto L_0x078e;
    L_0x0788:
        r1 = r0.nextIcon;
        r2 = 13;
        if (r1 != r2) goto L_0x080e;
    L_0x078e:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x0797;
    L_0x0794:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x07a4;
    L_0x0797:
        r1 = 13;
        if (r2 != r1) goto L_0x079e;
    L_0x079b:
        r5 = r0.transitionProgress;
        goto L_0x07a4;
    L_0x079e:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r2 - r1;
    L_0x07a4:
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
        if (r3 == r4) goto L_0x07c7;
    L_0x07bf:
        r33.save();
        r3 = (float) r11;
        r4 = (float) r12;
        r7.scale(r5, r5, r3, r4);
    L_0x07c7:
        r3 = r0.animatedDownloadProgress;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r3 * r4;
        r3 = (int) r3;
        r4 = r0.percentString;
        if (r4 == 0) goto L_0x07d6;
    L_0x07d2:
        r4 = r0.lastPercent;
        if (r3 == r4) goto L_0x07fc;
    L_0x07d6:
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
    L_0x07fc:
        r3 = r0.percentString;
        r2 = (float) r2;
        r1 = (float) r1;
        r4 = r0.textPaint;
        r7.drawText(r3, r2, r1, r4);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x080e;
    L_0x080b:
        r33.restore();
    L_0x080e:
        r1 = r0.currentIcon;
        r2 = 1;
        if (r1 == 0) goto L_0x0828;
    L_0x0813:
        if (r1 == r2) goto L_0x0828;
    L_0x0815:
        r1 = r0.nextIcon;
        if (r1 == 0) goto L_0x0828;
    L_0x0819:
        if (r1 != r2) goto L_0x081c;
    L_0x081b:
        goto L_0x0828;
    L_0x081c:
        r23 = r10;
        r30 = r11;
        r24 = r12;
        r17 = r14;
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0b1b;
    L_0x0828:
        r1 = r0.currentIcon;
        if (r1 != 0) goto L_0x0830;
    L_0x082c:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0838;
    L_0x0830:
        r1 = r0.currentIcon;
        if (r1 != r2) goto L_0x0845;
    L_0x0834:
        r1 = r0.nextIcon;
        if (r1 != 0) goto L_0x0845;
    L_0x0838:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x0845;
    L_0x083c:
        r1 = r0.interpolator;
        r2 = r0.transitionProgress;
        r1 = r1.getInterpolation(r2);
        goto L_0x0846;
    L_0x0845:
        r1 = 0;
    L_0x0846:
        r2 = r0.path1;
        r2.reset();
        r2 = r0.path2;
        r2.reset();
        r2 = r0.currentIcon;
        if (r2 == 0) goto L_0x0864;
    L_0x0854:
        r3 = 1;
        if (r2 == r3) goto L_0x085c;
    L_0x0857:
        r2 = 0;
        r3 = 0;
        r4 = 0;
    L_0x085a:
        r15 = 0;
        goto L_0x086b;
    L_0x085c:
        r2 = pausePath1;
        r3 = pausePath2;
        r15 = 90;
        r4 = 0;
        goto L_0x086b;
    L_0x0864:
        r2 = playPath1;
        r3 = playPath2;
        r4 = playFinalPath;
        goto L_0x085a;
    L_0x086b:
        r5 = r0.nextIcon;
        if (r5 == 0) goto L_0x087d;
    L_0x086f:
        r6 = 1;
        if (r5 == r6) goto L_0x0876;
    L_0x0872:
        r5 = 0;
        r6 = 0;
    L_0x0874:
        r8 = 0;
        goto L_0x0882;
    L_0x0876:
        r5 = pausePath1;
        r6 = pausePath2;
        r8 = 90;
        goto L_0x0882;
    L_0x087d:
        r5 = playPath1;
        r6 = playPath2;
        goto L_0x0874;
    L_0x0882:
        if (r2 != 0) goto L_0x088b;
    L_0x0884:
        r2 = r5;
        r3 = r6;
        r17 = r14;
        r5 = 0;
        r6 = 0;
        goto L_0x088d;
    L_0x088b:
        r17 = r14;
    L_0x088d:
        r14 = r0.animatingTransition;
        if (r14 != 0) goto L_0x092e;
    L_0x0891:
        if (r4 == 0) goto L_0x092e;
    L_0x0893:
        r2 = 0;
    L_0x0894:
        r3 = r4.length;
        r5 = 2;
        r3 = r3 / r5;
        if (r2 >= r3) goto L_0x0920;
    L_0x0899:
        if (r2 != 0) goto L_0x08da;
    L_0x089b:
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
        goto L_0x0918;
    L_0x08da:
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
    L_0x0918:
        r2 = r2 + 1;
        r9 = r22;
        r10 = r23;
        goto L_0x0894;
    L_0x0920:
        r22 = r9;
        r23 = r10;
        r28 = r8;
        r30 = r11;
        r24 = r12;
    L_0x092a:
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0ac2;
    L_0x092e:
        r22 = r9;
        r23 = r10;
        if (r5 != 0) goto L_0x09e6;
    L_0x0934:
        r4 = 0;
    L_0x0935:
        r5 = 5;
        if (r4 >= r5) goto L_0x09b9;
    L_0x0938:
        if (r4 != 0) goto L_0x0977;
    L_0x093a:
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
        goto L_0x09b3;
    L_0x0977:
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
    L_0x09b3:
        r4 = r4 + 1;
        r12 = r24;
        goto L_0x0935;
    L_0x09b9:
        r24 = r12;
        r2 = r0.nextIcon;
        r3 = 4;
        if (r2 != r3) goto L_0x09d4;
    L_0x09c0:
        r2 = r0.paint2;
        r3 = r0.transitionProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r4 - r3;
        r5 = r5 * r19;
        r3 = (int) r5;
        r2.setAlpha(r3);
    L_0x09ce:
        r28 = r8;
        r30 = r11;
        goto L_0x092a;
    L_0x09d4:
        r3 = r0.paint2;
        r4 = r0.currentIcon;
        if (r4 != r2) goto L_0x09dd;
    L_0x09da:
        r2 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x09e2;
    L_0x09dd:
        r2 = r0.transitionProgress;
        r2 = r2 * r19;
        r2 = (int) r2;
    L_0x09e2:
        r3.setAlpha(r2);
        goto L_0x09ce;
    L_0x09e6:
        r24 = r12;
        r4 = 0;
    L_0x09e9:
        r9 = 5;
        if (r4 >= r9) goto L_0x0ab7;
    L_0x09ec:
        if (r4 != 0) goto L_0x0a50;
    L_0x09ee:
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
        goto L_0x0aaf;
    L_0x0a50:
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
    L_0x0aaf:
        r4 = r4 + 1;
        r8 = r28;
        r11 = r30;
        goto L_0x09e9;
    L_0x0ab7:
        r28 = r8;
        r30 = r11;
        r2 = r0.paint2;
        r8 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2.setAlpha(r8);
    L_0x0ac2:
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
        if (r1 == 0) goto L_0x0af3;
    L_0x0af0:
        r2 = 1;
        if (r1 != r2) goto L_0x0af8;
    L_0x0af3:
        r1 = r0.currentIcon;
        r2 = 4;
        if (r1 != r2) goto L_0x0b08;
    L_0x0af8:
        r1 = r13.left;
        r11 = r30 - r1;
        r1 = (float) r11;
        r2 = r13.top;
        r12 = r24 - r2;
        r2 = (float) r12;
        r9 = r22;
        r7.scale(r9, r9, r1, r2);
        goto L_0x0b0a;
    L_0x0b08:
        r9 = r22;
    L_0x0b0a:
        r1 = r0.path1;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r1 = r0.path2;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r33.restore();
    L_0x0b1b:
        r1 = r0.currentIcon;
        r2 = 6;
        if (r1 == r2) goto L_0x0b25;
    L_0x0b20:
        r1 = r0.nextIcon;
        r2 = 6;
        if (r1 != r2) goto L_0x0bbf;
    L_0x0b25:
        r1 = r0.currentIcon;
        r2 = 6;
        if (r1 == r2) goto L_0x0b4e;
    L_0x0b2a:
        r1 = r0.transitionProgress;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b4a;
    L_0x0b30:
        r1 = r1 - r20;
        r1 = r1 / r20;
        r2 = r1 / r20;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = java.lang.Math.min(r3, r2);
        r10 = r3 - r2;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b47;
    L_0x0b42:
        r1 = r1 - r20;
        r1 = r1 / r20;
        goto L_0x0b48;
    L_0x0b47:
        r1 = 0;
    L_0x0b48:
        r11 = r1;
        goto L_0x0b51;
    L_0x0b4a:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = 0;
        goto L_0x0b51;
    L_0x0b4e:
        r10 = 0;
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0b51:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r12 = r24 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r13 = r30 - r1;
        r1 = r0.paint;
        r1.setAlpha(r8);
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r2 >= 0) goto L_0x0b9b;
    L_0x0b6a:
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
    L_0x0b9b:
        r1 = 0;
        r2 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1));
        if (r2 <= 0) goto L_0x0bbf;
    L_0x0ba0:
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
    L_0x0bbf:
        r1 = r17;
        if (r23 == 0) goto L_0x0CLASSNAME;
    L_0x0bc3:
        r2 = r23;
        if (r2 == r1) goto L_0x0CLASSNAME;
    L_0x0bc7:
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
        if (r5 != r6) goto L_0x0be5;
    L_0x0be2:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0bee;
    L_0x0be5:
        r5 = r0.transitionProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r6 - r5;
        r5 = r5 * r19;
        r5 = (int) r5;
    L_0x0bee:
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
        if (r1 == 0) goto L_0x0c3b;
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
    L_0x0c1f:
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
    L_0x0c3b:
        r1 = r29;
        r2 = r16;
        if (r1 == 0) goto L_0x0c9b;
    L_0x0CLASSNAME:
        if (r1 == r2) goto L_0x0c9b;
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
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r1[r3];
        r3 = r0.backPaint;
        r7.drawPath(r1, r3);
    L_0x0CLASSNAME:
        r33.restore();
    L_0x0c9b:
        if (r2 == 0) goto L_0x0cf1;
    L_0x0c9d:
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
        if (r5 != r6) goto L_0x0cc3;
    L_0x0cc0:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0cc8;
    L_0x0cc3:
        r5 = r0.transitionProgress;
        r5 = r5 * r19;
        r5 = (int) r5;
    L_0x0cc8:
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
        if (r3 == 0) goto L_0x0cee;
    L_0x0ce7:
        r2 = r2[r1];
        r1 = r0.backPaint;
        r7.drawPath(r2, r1);
    L_0x0cee:
        r33.restore();
    L_0x0cf1:
        r1 = java.lang.System.currentTimeMillis();
        r3 = r0.lastAnimationTime;
        r3 = r1 - r3;
        r5 = 17;
        r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r8 <= 0) goto L_0x0d01;
    L_0x0cff:
        r3 = 17;
    L_0x0d01:
        r0.lastAnimationTime = r1;
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x0d1d;
    L_0x0d08:
        r2 = 14;
        if (r1 == r2) goto L_0x0d1d;
    L_0x0d0c:
        r5 = 4;
        if (r1 != r5) goto L_0x0d13;
    L_0x0d0f:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0d1d;
    L_0x0d13:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0d1d;
    L_0x0d19:
        r2 = 13;
        if (r1 != r2) goto L_0x0d6a;
    L_0x0d1d:
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
        if (r1 == r2) goto L_0x0d67;
    L_0x0d38:
        r1 = r0.downloadProgress;
        r2 = r0.downloadProgressAnimationStart;
        r5 = r1 - r2;
        r6 = 0;
        r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r8 <= 0) goto L_0x0d67;
    L_0x0d43:
        r6 = r0.downloadProgressTime;
        r8 = (float) r3;
        r6 = r6 + r8;
        r0.downloadProgressTime = r6;
        r6 = r0.downloadProgressTime;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x0d59;
    L_0x0d51:
        r0.animatedDownloadProgress = r1;
        r0.downloadProgressAnimationStart = r1;
        r1 = 0;
        r0.downloadProgressTime = r1;
        goto L_0x0d67;
    L_0x0d59:
        r1 = r0.interpolator;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r6 = r6 / r8;
        r1 = r1.getInterpolation(r6);
        r5 = r5 * r1;
        r2 = r2 + r5;
        r0.animatedDownloadProgress = r2;
    L_0x0d67:
        r32.invalidateSelf();
    L_0x0d6a:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x0d8f;
    L_0x0d6e:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x0d8f;
    L_0x0d76:
        r3 = (float) r3;
        r4 = r0.transitionAnimationTime;
        r3 = r3 / r4;
        r1 = r1 + r3;
        r0.transitionProgress = r1;
        r1 = r0.transitionProgress;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x0d8c;
    L_0x0d83:
        r1 = r0.nextIcon;
        r0.currentIcon = r1;
        r0.transitionProgress = r2;
        r1 = 0;
        r0.animatingTransition = r1;
    L_0x0d8c:
        r32.invalidateSelf();
    L_0x0d8f:
        r1 = r27;
        r2 = 1;
        if (r1 < r2) goto L_0x0d97;
    L_0x0d94:
        r7.restoreToCount(r1);
    L_0x0d97:
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
