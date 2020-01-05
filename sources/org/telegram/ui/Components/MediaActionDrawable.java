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
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isMini;
    private long lastAnimationTime;
    private int lastPercent = -1;
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

    /* JADX WARNING: Removed duplicated region for block: B:384:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0b6e  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0bf4  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:451:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0b6e  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0bf4  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:451:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0835  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x082b  */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0993  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x08e3  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05ed  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x05fc  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0636  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0633  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0652  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x06bb  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x06d3  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x06ea  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0738  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0b6e  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0bf4  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:451:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x05b7  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05ed  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x05fc  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x061f  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0633  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0636  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0652  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x06bb  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x06d3  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x06ea  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0738  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0b6e  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0bf4  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:451:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x02b7  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x02f9  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x031d  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05ed  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x05fc  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x061f  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0636  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0633  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0652  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x06bb  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x06d3  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x06ea  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0738  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0b6e  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0bf4  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:451:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x008f A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x02b7  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x02f9  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x031d  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05ed  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x05fc  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x061f  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0633  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0636  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0652  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x06bb  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x06d3  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x06ea  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0738  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0b6e  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0bf4  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:451:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing block: B:96:0x0321, code skipped:
            if (r1 != 3) goto L_0x0324;
     */
    /* JADX WARNING: Missing block: B:184:0x0542, code skipped:
            if (r1 != 3) goto L_0x05b1;
     */
    /* JADX WARNING: Missing block: B:196:0x0580, code skipped:
            if (r1 != 3) goto L_0x05a4;
     */
    /* JADX WARNING: Missing block: B:420:0x0CLASSNAME, code skipped:
            if (r1 != 13) goto L_0x0c5f;
     */
    public void draw(android.graphics.Canvas r33) {
        /*
        r32 = this;
        r0 = r32;
        r7 = r33;
        r8 = r32.getBounds();
        r9 = r8.centerX();
        r10 = r8.centerY();
        r1 = r0.nextIcon;
        r11 = 6;
        r12 = 3;
        r14 = 4;
        r15 = 14;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r1 != r14) goto L_0x002f;
    L_0x001b:
        r1 = r0.currentIcon;
        if (r1 == r12) goto L_0x0046;
    L_0x001f:
        if (r1 == r15) goto L_0x0046;
    L_0x0021:
        r1 = r33.save();
        r2 = r0.transitionProgress;
        r2 = r6 - r2;
        r3 = (float) r9;
        r4 = (float) r10;
        r7.scale(r2, r2, r3, r4);
        goto L_0x0044;
    L_0x002f:
        if (r1 == r11) goto L_0x0035;
    L_0x0031:
        r2 = 10;
        if (r1 != r2) goto L_0x0046;
    L_0x0035:
        r1 = r0.currentIcon;
        if (r1 != r14) goto L_0x0046;
    L_0x0039:
        r1 = r33.save();
        r2 = r0.transitionProgress;
        r3 = (float) r9;
        r4 = (float) r10;
        r7.scale(r2, r2, r3, r4);
    L_0x0044:
        r5 = r1;
        goto L_0x0047;
    L_0x0046:
        r5 = 0;
    L_0x0047:
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r16 = NUM; // 0x40600000 float:3.5 double:5.3360734E-315;
        r17 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r18 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r19 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r20 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r3 = 2;
        if (r1 == r3) goto L_0x0065;
    L_0x005b:
        r1 = r0.nextIcon;
        if (r1 != r3) goto L_0x0060;
    L_0x005f:
        goto L_0x0065;
    L_0x0060:
        r28 = r5;
        r14 = 2;
        goto L_0x0310;
    L_0x0065:
        r1 = (float) r10;
        r21 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = (float) r2;
        r13 = r0.scale;
        r2 = r2 * r13;
        r2 = r1 - r2;
        r13 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r11 = r0.scale;
        r13 = r13 * r11;
        r13 = r13 + r1;
        r11 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r14 = r0.scale;
        r11 = r11 * r14;
        r11 = r11 + r1;
        r14 = r0.currentIcon;
        if (r14 == r12) goto L_0x0091;
    L_0x008f:
        if (r14 != r15) goto L_0x00b7;
    L_0x0091:
        r14 = r0.nextIcon;
        if (r14 != r3) goto L_0x00b7;
    L_0x0095:
        r14 = r0.paint;
        r4 = r0.transitionProgress;
        r4 = r4 / r20;
        r4 = java.lang.Math.min(r6, r4);
        r4 = r4 * r19;
        r4 = (int) r4;
        r14.setAlpha(r4);
        r4 = r0.transitionProgress;
        r14 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r6 = r0.scale;
        r14 = r14 * r6;
        r14 = r14 + r1;
        r12 = r14;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x00f3;
    L_0x00b7:
        r4 = r0.nextIcon;
        if (r4 == r12) goto L_0x00dc;
    L_0x00bb:
        if (r4 == r15) goto L_0x00dc;
    L_0x00bd:
        if (r4 == r3) goto L_0x00dc;
    L_0x00bf:
        r4 = r0.paint;
        r6 = r0.savedTransitionProgress;
        r6 = r6 / r20;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = java.lang.Math.min(r14, r6);
        r6 = r6 * r19;
        r12 = r0.transitionProgress;
        r12 = r14 - r12;
        r6 = r6 * r12;
        r6 = (int) r6;
        r4.setAlpha(r6);
        r4 = r0.savedTransitionProgress;
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x00e5;
    L_0x00dc:
        r4 = r0.paint;
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r4.setAlpha(r6);
        r4 = r0.transitionProgress;
    L_0x00e5:
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r12 = (float) r12;
        r6 = r0.scale;
        r12 = r12 * r6;
        r6 = r1 + r12;
        r12 = r6;
    L_0x00f3:
        r6 = r0.animatingTransition;
        if (r6 == 0) goto L_0x02b7;
    L_0x00f7:
        r6 = r0.nextIcon;
        if (r6 == r3) goto L_0x026b;
    L_0x00fb:
        r6 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1));
        if (r6 > 0) goto L_0x0101;
    L_0x00ff:
        goto L_0x026b;
    L_0x0101:
        r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r6 = r0.scale;
        r2 = r2 * r6;
        r4 = r4 - r20;
        r6 = r4 / r20;
        r13 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r13 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1));
        if (r13 <= 0) goto L_0x0123;
    L_0x0117:
        r13 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r4 = r4 - r13;
        r13 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r4 = r4 / r13;
        r13 = r4;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x012a;
    L_0x0123:
        r13 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r4 = r4 / r13;
        r24 = r4;
        r13 = 0;
    L_0x012a:
        r4 = r0.rect;
        r14 = (float) r9;
        r3 = r14 - r2;
        r2 = r2 / r17;
        r15 = r11 - r2;
        r2 = r2 + r11;
        r4.set(r3, r15, r14, r2);
        r2 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r13 * r2;
        r2 = r0.rect;
        r4 = NUM; // 0x42d00000 float:104.0 double:5.5381189E-315;
        r6 = r6 * r4;
        r4 = r6 - r3;
        r6 = 0;
        r15 = r0.paint;
        r27 = r1;
        r1 = r33;
        r28 = r5;
        r5 = r6;
        r22 = r8;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r15;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r11 - r12;
        r1 = r1 * r24;
        r12 = r12 + r1;
        r15 = 0;
        r1 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1));
        if (r1 <= 0) goto L_0x0263;
    L_0x015f:
        r1 = r0.nextIcon;
        r2 = 14;
        if (r1 != r2) goto L_0x0167;
    L_0x0165:
        r6 = 0;
        goto L_0x016e;
    L_0x0167:
        r1 = -NUM; // 0xffffffffCLASSNAME float:-45.0 double:NaN;
        r6 = r8 - r13;
        r2 = r6 * r1;
        r6 = r2;
    L_0x016e:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r1 = r1 * r13;
        r2 = r0.scale;
        r1 = r1 * r2;
        r13 = r13 * r19;
        r2 = (int) r13;
        r3 = r0.nextIcon;
        r4 = 3;
        if (r3 == r4) goto L_0x0197;
    L_0x0181:
        r4 = 14;
        if (r3 == r4) goto L_0x0197;
    L_0x0185:
        r13 = 2;
        if (r3 == r13) goto L_0x0198;
    L_0x0188:
        r3 = r0.transitionProgress;
        r3 = r3 / r20;
        r3 = java.lang.Math.min(r8, r3);
        r3 = r8 - r3;
        r2 = (float) r2;
        r2 = r2 * r3;
        r2 = (int) r2;
        goto L_0x0198;
    L_0x0197:
        r13 = 2;
    L_0x0198:
        r5 = r2;
        r2 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1));
        if (r2 == 0) goto L_0x01a6;
    L_0x019d:
        r33.save();
        r2 = r27;
        r7.rotate(r6, r14, r2);
        goto L_0x01a8;
    L_0x01a6:
        r2 = r27;
    L_0x01a8:
        if (r5 == 0) goto L_0x0257;
    L_0x01aa:
        r3 = r0.paint;
        r3.setAlpha(r5);
        r3 = r0.nextIcon;
        r4 = 14;
        if (r3 != r4) goto L_0x0235;
    L_0x01b5:
        r1 = r0.paint3;
        r1.setAlpha(r5);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r9 - r2;
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r10 - r3;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = r4 + r9;
        r4 = (float) r4;
        r23 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r13 = r10 + r23;
        r13 = (float) r13;
        r1.set(r2, r3, r4, r13);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = (float) r3;
        r4 = r0.paint3;
        r7.drawRoundRect(r1, r2, r3, r4);
        r1 = r0.paint;
        r2 = (float) r5;
        r3 = NUM; // 0x3e19999a float:0.15 double:5.147497604E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x01fe;
    L_0x01fb:
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x0200;
    L_0x01fe:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x0200:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r0.rect;
        r13 = r22;
        r3 = r13.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r4 = r13.top;
        r4 = r4 + r1;
        r4 = (float) r4;
        r8 = r13.right;
        r8 = r8 - r1;
        r8 = (float) r8;
        r15 = r13.bottom;
        r15 = r15 - r1;
        r1 = (float) r15;
        r2.set(r3, r4, r8, r1);
        r2 = r0.rect;
        r3 = 0;
        r4 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r8 = 0;
        r15 = r0.paint;
        r1 = r33;
        r29 = r5;
        r5 = r8;
        r8 = r6;
        r6 = r15;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r0.paint;
        r2 = r29;
        r1.setAlpha(r2);
        goto L_0x025a;
    L_0x0235:
        r8 = r6;
        r13 = r22;
        r15 = r14 - r1;
        r22 = r2 - r1;
        r23 = r14 + r1;
        r25 = r2 + r1;
        r6 = r0.paint;
        r1 = r33;
        r2 = r15;
        r3 = r22;
        r4 = r23;
        r5 = r25;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r23;
        r4 = r15;
        r1.drawLine(r2, r3, r4, r5, r6);
        goto L_0x025a;
    L_0x0257:
        r8 = r6;
        r13 = r22;
    L_0x025a:
        r1 = 0;
        r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0265;
    L_0x025f:
        r33.restore();
        goto L_0x0265;
    L_0x0263:
        r13 = r22;
    L_0x0265:
        r3 = r11;
        r8 = r13;
        r1 = r14;
        r2 = r1;
        r14 = 2;
        goto L_0x02b2;
    L_0x026b:
        r28 = r5;
        r14 = 2;
        r1 = r0.nextIcon;
        if (r1 != r14) goto L_0x0277;
    L_0x0272:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r1 - r4;
        goto L_0x027d;
    L_0x0277:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r4 / r20;
        r4 = r1 - r6;
    L_0x027d:
        r12 = r12 - r2;
        r12 = r12 * r6;
        r12 = r12 + r2;
        r11 = r11 - r13;
        r11 = r11 * r6;
        r11 = r11 + r13;
        r1 = (float) r9;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r2 = r2 * r4;
        r3 = r0.scale;
        r2 = r2 * r3;
        r2 = r1 - r2;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r3 * r4;
        r5 = r0.scale;
        r3 = r3 * r5;
        r1 = r1 + r3;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r3 * r4;
        r4 = r0.scale;
        r3 = r3 * r4;
        r3 = r11 - r3;
    L_0x02b2:
        r15 = r1;
        r13 = r3;
        r3 = r12;
        r12 = r2;
        goto L_0x02e6;
    L_0x02b7:
        r28 = r5;
        r14 = 2;
        r1 = (float) r9;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r4 = r0.scale;
        r3 = r3 * r4;
        r3 = r1 - r3;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r5 = r0.scale;
        r4 = r4 * r5;
        r1 = r1 + r4;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r5 = r0.scale;
        r4 = r4 * r5;
        r4 = r13 - r4;
        r15 = r1;
        r12 = r3;
        r11 = r13;
        r3 = r2;
        r13 = r4;
    L_0x02e6:
        r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r1 == 0) goto L_0x02f4;
    L_0x02ea:
        r4 = (float) r9;
        r6 = r0.paint;
        r1 = r33;
        r2 = r4;
        r5 = r11;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x02f4:
        r6 = (float) r9;
        r1 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x0310;
    L_0x02f9:
        r5 = r0.paint;
        r1 = r33;
        r2 = r12;
        r3 = r13;
        r4 = r6;
        r12 = r5;
        r5 = r11;
        r22 = r6;
        r6 = r12;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r15;
        r4 = r22;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0310:
        r1 = r0.currentIcon;
        r11 = 1;
        r2 = 3;
        if (r1 == r2) goto L_0x0390;
    L_0x0316:
        r3 = 14;
        if (r1 == r3) goto L_0x0390;
    L_0x031a:
        r4 = 4;
        if (r1 != r4) goto L_0x0324;
    L_0x031d:
        r1 = r0.nextIcon;
        if (r1 == r3) goto L_0x0390;
    L_0x0321:
        if (r1 != r2) goto L_0x0324;
    L_0x0323:
        goto L_0x0390;
    L_0x0324:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0334;
    L_0x032a:
        r2 = r0.nextIcon;
        r3 = 10;
        if (r2 == r3) goto L_0x0334;
    L_0x0330:
        r2 = 13;
        if (r1 != r2) goto L_0x05ba;
    L_0x0334:
        r1 = r0.nextIcon;
        r2 = 4;
        if (r1 == r2) goto L_0x0340;
    L_0x0339:
        r2 = 6;
        if (r1 != r2) goto L_0x033d;
    L_0x033c:
        goto L_0x0340;
    L_0x033d:
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0349;
    L_0x0340:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r2 - r1;
        r6 = r6 * r19;
        r4 = (int) r6;
    L_0x0349:
        if (r4 == 0) goto L_0x05ba;
    L_0x034b:
        r1 = r0.paint;
        r2 = (float) r4;
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
        if (r1 == 0) goto L_0x0367;
    L_0x0366:
        goto L_0x0369;
    L_0x0367:
        r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x0369:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r0.rect;
        r3 = r8.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r5 = r8.top;
        r5 = r5 + r1;
        r5 = (float) r5;
        r6 = r8.right;
        r6 = r6 - r1;
        r6 = (float) r6;
        r12 = r8.bottom;
        r12 = r12 - r1;
        r1 = (float) r12;
        r2.set(r3, r5, r6, r1);
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r33;
        r1.drawArc(r2, r3, r4, r5, r6);
        goto L_0x05ba;
    L_0x0390:
        r1 = r0.nextIcon;
        if (r1 != r14) goto L_0x03bd;
    L_0x0394:
        r1 = r0.transitionProgress;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 > 0) goto L_0x03b0;
    L_0x039a:
        r1 = r1 / r20;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r2 - r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r1 = r1 * r6;
        r2 = r0.scale;
        r2 = r2 * r1;
        r6 = r6 * r19;
        r13 = (int) r6;
        r4 = r13;
        goto L_0x03b2;
    L_0x03b0:
        r2 = 0;
        r4 = 0;
    L_0x03b2:
        r3 = r2;
        r15 = r4;
        r1 = 0;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = 0;
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        goto L_0x049e;
    L_0x03bd:
        if (r1 == 0) goto L_0x046a;
    L_0x03bf:
        if (r1 == r11) goto L_0x046a;
    L_0x03c1:
        r2 = 5;
        if (r1 == r2) goto L_0x046a;
    L_0x03c4:
        r2 = 8;
        if (r1 == r2) goto L_0x046a;
    L_0x03c8:
        r2 = 9;
        if (r1 == r2) goto L_0x046a;
    L_0x03cc:
        r2 = 7;
        if (r1 == r2) goto L_0x046a;
    L_0x03cf:
        r2 = 6;
        if (r1 != r2) goto L_0x03d4;
    L_0x03d2:
        goto L_0x046a;
    L_0x03d4:
        r2 = 4;
        if (r1 != r2) goto L_0x0412;
    L_0x03d7:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r2 - r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = (float) r2;
        r3 = r0.scale;
        r2 = r2 * r3;
        r3 = r6 * r19;
        r4 = (int) r3;
        r3 = r0.currentIcon;
        r5 = 14;
        if (r3 != r5) goto L_0x03f9;
    L_0x03ef:
        r1 = r8.left;
        r1 = (float) r1;
        r3 = r8.top;
        r3 = (float) r3;
        r5 = r3;
        r3 = r1;
        r1 = 0;
        goto L_0x0409;
    L_0x03f9:
        r3 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r3;
        r3 = r8.centerX();
        r3 = (float) r3;
        r5 = r8.centerY();
        r5 = (float) r5;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0409:
        r13 = r1;
        r1 = r3;
        r15 = r4;
        r12 = r6;
        r3 = r2;
    L_0x040e:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x049e;
    L_0x0412:
        r2 = 14;
        if (r1 == r2) goto L_0x042f;
    L_0x0416:
        r2 = 3;
        if (r1 != r2) goto L_0x041a;
    L_0x0419:
        goto L_0x042f;
    L_0x041a:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r2 = r0.scale;
        r2 = r2 * r1;
        r3 = r2;
        r1 = 0;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = 0;
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        r15 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x049e;
    L_0x042f:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r2 - r1;
        r2 = r0.currentIcon;
        r3 = 4;
        if (r2 != r3) goto L_0x043d;
    L_0x043a:
        r6 = r1;
        r2 = 0;
        goto L_0x0444;
    L_0x043d:
        r2 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r6 = r6 * r2;
        r2 = r6;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0444:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r4 = r0.scale;
        r3 = r3 * r4;
        r1 = r1 * r19;
        r4 = (int) r1;
        r1 = r0.nextIcon;
        r5 = 14;
        if (r1 != r5) goto L_0x045c;
    L_0x0456:
        r1 = r8.left;
        r1 = (float) r1;
        r5 = r8.top;
        goto L_0x0465;
    L_0x045c:
        r1 = r8.centerX();
        r1 = (float) r1;
        r5 = r8.centerY();
    L_0x0465:
        r5 = (float) r5;
        r13 = r2;
        r15 = r4;
        r12 = r6;
        goto L_0x040e;
    L_0x046a:
        r1 = r0.nextIcon;
        r2 = 6;
        if (r1 != r2) goto L_0x047a;
    L_0x046f:
        r1 = r0.transitionProgress;
        r1 = r1 / r20;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = java.lang.Math.min(r2, r1);
        goto L_0x047e;
    L_0x047a:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = r0.transitionProgress;
    L_0x047e:
        r6 = r2 - r1;
        r3 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r3 = r3 * r6;
        r4 = r0.scale;
        r3 = r3 * r4;
        r6 = r6 * r17;
        r4 = java.lang.Math.min(r2, r6);
        r4 = r4 * r19;
        r4 = (int) r4;
        r13 = r1;
        r15 = r4;
        r1 = 0;
        r5 = 0;
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x049e:
        r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x04a8;
    L_0x04a2:
        r33.save();
        r7.scale(r12, r12, r1, r5);
    L_0x04a8:
        r1 = 0;
        r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x04b5;
    L_0x04ad:
        r33.save();
        r1 = (float) r9;
        r2 = (float) r10;
        r7.rotate(r13, r1, r2);
    L_0x04b5:
        if (r15 == 0) goto L_0x052a;
    L_0x04b7:
        r1 = r0.paint;
        r2 = (float) r15;
        r4 = r0.overrideAlpha;
        r4 = r4 * r2;
        r4 = (int) r4;
        r1.setAlpha(r4);
        r1 = r0.currentIcon;
        r4 = 14;
        if (r1 == r4) goto L_0x04f0;
    L_0x04c8:
        r1 = r0.nextIcon;
        if (r1 != r4) goto L_0x04cd;
    L_0x04cc:
        goto L_0x04f0;
    L_0x04cd:
        r1 = (float) r9;
        r16 = r1 - r3;
        r2 = (float) r10;
        r22 = r2 - r3;
        r23 = r1 + r3;
        r25 = r2 + r3;
        r6 = r0.paint;
        r1 = r33;
        r2 = r16;
        r3 = r22;
        r4 = r23;
        r5 = r25;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r23;
        r4 = r16;
        r1.drawLine(r2, r3, r4, r5, r6);
        goto L_0x052a;
    L_0x04f0:
        r1 = r0.paint3;
        r3 = r0.overrideAlpha;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r9 - r2;
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r10 - r3;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = r4 + r9;
        r4 = (float) r4;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r5 = r5 + r10;
        r5 = (float) r5;
        r1.set(r2, r3, r4, r5);
        r1 = r0.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = (float) r3;
        r4 = r0.paint3;
        r7.drawRoundRect(r1, r2, r3, r4);
    L_0x052a:
        r1 = 0;
        r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0532;
    L_0x052f:
        r33.restore();
    L_0x0532:
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x0544;
    L_0x0537:
        r3 = 14;
        if (r1 == r3) goto L_0x0544;
    L_0x053b:
        r4 = 4;
        if (r1 != r4) goto L_0x05b1;
    L_0x053e:
        r1 = r0.nextIcon;
        if (r1 == r3) goto L_0x0544;
    L_0x0542:
        if (r1 != r2) goto L_0x05b1;
    L_0x0544:
        if (r15 == 0) goto L_0x05b1;
    L_0x0546:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r3 = r0.animatedDownloadProgress;
        r3 = r3 * r2;
        r13 = java.lang.Math.max(r1, r3);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x0557;
    L_0x0556:
        goto L_0x0559;
    L_0x0557:
        r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x0559:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r0.rect;
        r3 = r8.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r4 = r8.top;
        r4 = r4 + r1;
        r4 = (float) r4;
        r5 = r8.right;
        r5 = r5 - r1;
        r5 = (float) r5;
        r6 = r8.bottom;
        r6 = r6 - r1;
        r1 = (float) r6;
        r2.set(r3, r4, r5, r1);
        r1 = r0.currentIcon;
        r2 = 14;
        if (r1 == r2) goto L_0x0582;
    L_0x0578:
        r3 = 4;
        if (r1 != r3) goto L_0x05a4;
    L_0x057b:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0582;
    L_0x057f:
        r2 = 3;
        if (r1 != r2) goto L_0x05a4;
    L_0x0582:
        r1 = r0.paint;
        r2 = (float) r15;
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
        r1.setAlpha(r15);
    L_0x05a4:
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r33;
        r4 = r13;
        r1.drawArc(r2, r3, r4, r5, r6);
    L_0x05b1:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x05ba;
    L_0x05b7:
        r33.restore();
    L_0x05ba:
        r1 = 0;
        r2 = 0;
        r3 = r0.currentIcon;
        r4 = r0.nextIcon;
        if (r3 != r4) goto L_0x05c7;
    L_0x05c2:
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x05e8;
    L_0x05c7:
        r4 = 4;
        if (r3 != r4) goto L_0x05d1;
    L_0x05ca:
        r6 = r0.transitionProgress;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r3 - r6;
        goto L_0x05e6;
    L_0x05d1:
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r0.transitionProgress;
        r4 = r4 / r20;
        r6 = java.lang.Math.min(r3, r4);
        r4 = r0.transitionProgress;
        r4 = r4 / r20;
        r4 = r3 - r4;
        r3 = 0;
        r4 = java.lang.Math.max(r3, r4);
    L_0x05e6:
        r13 = r4;
        r12 = r6;
    L_0x05e8:
        r3 = r0.nextIcon;
        r4 = 5;
        if (r3 != r4) goto L_0x05f0;
    L_0x05ed:
        r1 = org.telegram.ui.ActionBar.Theme.chat_fileIcon;
        goto L_0x05f7;
    L_0x05f0:
        r3 = r0.currentIcon;
        r4 = 5;
        if (r3 != r4) goto L_0x05f7;
    L_0x05f5:
        r2 = org.telegram.ui.ActionBar.Theme.chat_fileIcon;
    L_0x05f7:
        r3 = r0.nextIcon;
        r4 = 7;
        if (r3 != r4) goto L_0x05ff;
    L_0x05fc:
        r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
        goto L_0x0606;
    L_0x05ff:
        r3 = r0.currentIcon;
        r4 = 7;
        if (r3 != r4) goto L_0x0606;
    L_0x0604:
        r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
    L_0x0606:
        r3 = r0.nextIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x060f;
    L_0x060c:
        r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
        goto L_0x0617;
    L_0x060f:
        r3 = r0.currentIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x0617;
    L_0x0615:
        r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
    L_0x0617:
        r15 = r1;
        r6 = r2;
        r1 = r0.currentIcon;
        r2 = 9;
        if (r1 == r2) goto L_0x062b;
    L_0x061f:
        r1 = r0.nextIcon;
        r2 = 9;
        if (r1 != r2) goto L_0x0626;
    L_0x0625:
        goto L_0x062b;
    L_0x0626:
        r25 = r13;
        r13 = r6;
        goto L_0x06a9;
    L_0x062b:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x0636;
    L_0x0633:
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x063b;
    L_0x0636:
        r2 = r0.transitionProgress;
        r2 = r2 * r19;
        r4 = (int) r2;
    L_0x063b:
        r1.setAlpha(r4);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r5 = r10 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r4 = r9 - r1;
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x065c;
    L_0x0652:
        r33.save();
        r1 = r0.transitionProgress;
        r2 = (float) r9;
        r3 = (float) r10;
        r7.scale(r1, r1, r2, r3);
    L_0x065c:
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r4 - r1;
        r2 = (float) r1;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r5 - r1;
        r3 = (float) r1;
        r1 = (float) r4;
        r11 = (float) r5;
        r14 = r0.paint;
        r17 = r1;
        r1 = r33;
        r22 = r4;
        r4 = r17;
        r23 = r5;
        r5 = r11;
        r25 = r13;
        r13 = r6;
        r6 = r14;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r4 = r22 + r1;
        r4 = (float) r4;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r5 = r23 - r1;
        r5 = (float) r5;
        r6 = r0.paint;
        r1 = r33;
        r2 = r17;
        r3 = r11;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x06a9;
    L_0x06a6:
        r33.restore();
    L_0x06a9:
        r1 = r0.currentIcon;
        r2 = 12;
        if (r1 == r2) goto L_0x06b5;
    L_0x06af:
        r1 = r0.nextIcon;
        r2 = 12;
        if (r1 != r2) goto L_0x0723;
    L_0x06b5:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x06be;
    L_0x06bb:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x06cb;
    L_0x06be:
        r1 = 13;
        if (r2 != r1) goto L_0x06c5;
    L_0x06c2:
        r6 = r0.transitionProgress;
        goto L_0x06cb;
    L_0x06c5:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r2 - r1;
    L_0x06cb:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x06d6;
    L_0x06d3:
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x06d9;
    L_0x06d6:
        r2 = r6 * r19;
        r4 = (int) r2;
    L_0x06d9:
        r1.setAlpha(r4);
        org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x06f2;
    L_0x06ea:
        r33.save();
        r1 = (float) r9;
        r2 = (float) r10;
        r7.scale(r6, r6, r1, r2);
    L_0x06f2:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = (float) r1;
        r2 = r0.scale;
        r1 = r1 * r2;
        r2 = (float) r9;
        r11 = r2 - r1;
        r3 = (float) r10;
        r14 = r3 - r1;
        r17 = r2 + r1;
        r22 = r3 + r1;
        r6 = r0.paint;
        r1 = r33;
        r2 = r11;
        r3 = r14;
        r4 = r17;
        r5 = r22;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r17;
        r4 = r11;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x0723;
    L_0x0720:
        r33.restore();
    L_0x0723:
        r1 = r0.currentIcon;
        r2 = 13;
        if (r1 == r2) goto L_0x072f;
    L_0x0729:
        r1 = r0.nextIcon;
        r2 = 13;
        if (r1 != r2) goto L_0x07af;
    L_0x072f:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x0738;
    L_0x0735:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0745;
    L_0x0738:
        r1 = 13;
        if (r2 != r1) goto L_0x073f;
    L_0x073c:
        r6 = r0.transitionProgress;
        goto L_0x0745;
    L_0x073f:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r2 - r1;
    L_0x0745:
        r1 = r0.textPaint;
        r2 = r6 * r19;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r1 + r10;
        r2 = r0.percentStringWidth;
        r3 = 2;
        r2 = r2 / r3;
        r2 = r9 - r2;
        r3 = r0.currentIcon;
        r4 = r0.nextIcon;
        if (r3 == r4) goto L_0x0768;
    L_0x0760:
        r33.save();
        r3 = (float) r9;
        r4 = (float) r10;
        r7.scale(r6, r6, r3, r4);
    L_0x0768:
        r3 = r0.animatedDownloadProgress;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r3 * r4;
        r3 = (int) r3;
        r4 = r0.percentString;
        if (r4 == 0) goto L_0x0777;
    L_0x0773:
        r4 = r0.lastPercent;
        if (r3 == r4) goto L_0x079d;
    L_0x0777:
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
    L_0x079d:
        r3 = r0.percentString;
        r2 = (float) r2;
        r1 = (float) r1;
        r4 = r0.textPaint;
        r7.drawText(r3, r2, r1, r4);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x07af;
    L_0x07ac:
        r33.restore();
    L_0x07af:
        r1 = r0.currentIcon;
        r2 = 1;
        if (r1 == 0) goto L_0x07ca;
    L_0x07b4:
        if (r1 == r2) goto L_0x07ca;
    L_0x07b6:
        r1 = r0.nextIcon;
        if (r1 == 0) goto L_0x07ca;
    L_0x07ba:
        if (r1 != r2) goto L_0x07bd;
    L_0x07bc:
        goto L_0x07ca;
    L_0x07bd:
        r29 = r9;
        r26 = r10;
        r8 = r12;
        r23 = r13;
        r17 = r15;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0ac8;
    L_0x07ca:
        r1 = r0.currentIcon;
        if (r1 != 0) goto L_0x07d2;
    L_0x07ce:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x07da;
    L_0x07d2:
        r1 = r0.currentIcon;
        if (r1 != r2) goto L_0x07e8;
    L_0x07d6:
        r1 = r0.nextIcon;
        if (r1 != 0) goto L_0x07e8;
    L_0x07da:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x07e8;
    L_0x07de:
        r1 = r0.interpolator;
        r2 = r0.transitionProgress;
        r1 = r1.getInterpolation(r2);
        r2 = r1;
        goto L_0x07e9;
    L_0x07e8:
        r2 = 0;
    L_0x07e9:
        r1 = r0.path1;
        r1.reset();
        r1 = r0.path2;
        r1.reset();
        r1 = 0;
        r3 = r0.currentIcon;
        if (r3 == 0) goto L_0x0806;
    L_0x07f8:
        r4 = 1;
        if (r3 == r4) goto L_0x07ff;
    L_0x07fb:
        r3 = 0;
        r4 = r3;
        r5 = 0;
        goto L_0x0812;
    L_0x07ff:
        r3 = pausePath1;
        r4 = pausePath2;
        r5 = 90;
        goto L_0x0812;
    L_0x0806:
        r3 = playPath1;
        r1 = playPath2;
        r4 = playFinalPath;
        r5 = 0;
        r31 = r4;
        r4 = r1;
        r1 = r31;
    L_0x0812:
        r6 = r0.nextIcon;
        if (r6 == 0) goto L_0x0824;
    L_0x0816:
        r11 = 1;
        if (r6 == r11) goto L_0x081d;
    L_0x0819:
        r6 = 0;
        r11 = r6;
    L_0x081b:
        r14 = 0;
        goto L_0x0829;
    L_0x081d:
        r6 = pausePath1;
        r11 = pausePath2;
        r14 = 90;
        goto L_0x0829;
    L_0x0824:
        r6 = playPath1;
        r11 = playPath2;
        goto L_0x081b;
    L_0x0829:
        if (r3 != 0) goto L_0x0835;
    L_0x082b:
        r3 = 0;
        r4 = 0;
        r17 = r15;
        r31 = r11;
        r11 = r4;
        r4 = r31;
        goto L_0x083c;
    L_0x0835:
        r17 = r15;
        r31 = r6;
        r6 = r3;
        r3 = r31;
    L_0x083c:
        r15 = r0.animatingTransition;
        if (r15 != 0) goto L_0x08dd;
    L_0x0840:
        if (r1 == 0) goto L_0x08dd;
    L_0x0842:
        r3 = 0;
    L_0x0843:
        r4 = r1.length;
        r6 = 2;
        r4 = r4 / r6;
        if (r3 >= r4) goto L_0x08cf;
    L_0x0848:
        if (r3 != 0) goto L_0x0889;
    L_0x084a:
        r4 = r0.path1;
        r6 = r3 * 2;
        r11 = r1[r6];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r15 = r0.scale;
        r11 = r11 * r15;
        r15 = r6 + 1;
        r22 = r1[r15];
        r23 = r13;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r13 = (float) r13;
        r22 = r12;
        r12 = r0.scale;
        r13 = r13 * r12;
        r4.moveTo(r11, r13);
        r4 = r0.path2;
        r6 = r1[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r11 = r0.scale;
        r6 = r6 * r11;
        r11 = r1[r15];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r4.moveTo(r6, r11);
        goto L_0x08c7;
    L_0x0889:
        r22 = r12;
        r23 = r13;
        r4 = r0.path1;
        r6 = r3 * 2;
        r11 = r1[r6];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r12 = r6 + 1;
        r13 = r1[r12];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r15 = r0.scale;
        r13 = r13 * r15;
        r4.lineTo(r11, r13);
        r4 = r0.path2;
        r6 = r1[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r11 = r0.scale;
        r6 = r6 * r11;
        r11 = r1[r12];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r4.lineTo(r6, r11);
    L_0x08c7:
        r3 = r3 + 1;
        r12 = r22;
        r13 = r23;
        goto L_0x0843;
    L_0x08cf:
        r22 = r12;
        r23 = r13;
        r29 = r9;
        r26 = r10;
    L_0x08d7:
        r27 = r14;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0a6f;
    L_0x08dd:
        r22 = r12;
        r23 = r13;
        if (r3 != 0) goto L_0x0993;
    L_0x08e3:
        r1 = 0;
    L_0x08e4:
        r3 = 5;
        if (r1 >= r3) goto L_0x0968;
    L_0x08e7:
        if (r1 != 0) goto L_0x0926;
    L_0x08e9:
        r3 = r0.path1;
        r11 = r1 * 2;
        r12 = r6[r11];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r13 = r0.scale;
        r12 = r12 * r13;
        r13 = r11 + 1;
        r15 = r6[r13];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r26 = r10;
        r10 = r0.scale;
        r15 = r15 * r10;
        r3.moveTo(r12, r15);
        r3 = r0.path2;
        r10 = r4[r11];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r11 = r4[r13];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r3.moveTo(r10, r11);
        goto L_0x0962;
    L_0x0926:
        r26 = r10;
        r3 = r0.path1;
        r10 = r1 * 2;
        r11 = r6[r10];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r12 = r10 + 1;
        r13 = r6[r12];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r15 = r0.scale;
        r13 = r13 * r15;
        r3.lineTo(r11, r13);
        r3 = r0.path2;
        r10 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r11 = r4[r12];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r3.lineTo(r10, r11);
    L_0x0962:
        r1 = r1 + 1;
        r10 = r26;
        goto L_0x08e4;
    L_0x0968:
        r26 = r10;
        r1 = r0.nextIcon;
        r3 = 4;
        if (r1 != r3) goto L_0x0981;
    L_0x096f:
        r1 = r0.paint2;
        r3 = r0.transitionProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = r4 - r3;
        r6 = r6 * r19;
        r3 = (int) r6;
        r1.setAlpha(r3);
    L_0x097d:
        r29 = r9;
        goto L_0x08d7;
    L_0x0981:
        r3 = r0.paint2;
        r4 = r0.currentIcon;
        if (r4 != r1) goto L_0x098a;
    L_0x0987:
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x098f;
    L_0x098a:
        r1 = r0.transitionProgress;
        r1 = r1 * r19;
        r4 = (int) r1;
    L_0x098f:
        r3.setAlpha(r4);
        goto L_0x097d;
    L_0x0993:
        r26 = r10;
        r1 = 0;
    L_0x0996:
        r10 = 5;
        if (r1 >= r10) goto L_0x0a64;
    L_0x0999:
        if (r1 != 0) goto L_0x09fd;
    L_0x099b:
        r10 = r0.path1;
        r12 = r1 * 2;
        r13 = r6[r12];
        r15 = r3[r12];
        r27 = r6[r12];
        r15 = r15 - r27;
        r15 = r15 * r2;
        r13 = r13 + r15;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r15 = r0.scale;
        r13 = r13 * r15;
        r15 = r12 + 1;
        r27 = r6[r15];
        r29 = r3[r15];
        r30 = r6[r15];
        r29 = r29 - r30;
        r29 = r29 * r2;
        r27 = r27 + r29;
        r29 = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r9 = (float) r9;
        r27 = r14;
        r14 = r0.scale;
        r9 = r9 * r14;
        r10.moveTo(r13, r9);
        r9 = r0.path2;
        r10 = r4[r12];
        r13 = r11[r12];
        r12 = r4[r12];
        r13 = r13 - r12;
        r13 = r13 * r2;
        r10 = r10 + r13;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r12 = r0.scale;
        r10 = r10 * r12;
        r12 = r4[r15];
        r13 = r11[r15];
        r14 = r4[r15];
        r13 = r13 - r14;
        r13 = r13 * r2;
        r12 = r12 + r13;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r13 = r0.scale;
        r12 = r12 * r13;
        r9.moveTo(r10, r12);
        goto L_0x0a5c;
    L_0x09fd:
        r29 = r9;
        r27 = r14;
        r9 = r0.path1;
        r10 = r1 * 2;
        r12 = r6[r10];
        r13 = r3[r10];
        r14 = r6[r10];
        r13 = r13 - r14;
        r13 = r13 * r2;
        r12 = r12 + r13;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r13 = r0.scale;
        r12 = r12 * r13;
        r13 = r10 + 1;
        r14 = r6[r13];
        r15 = r3[r13];
        r30 = r6[r13];
        r15 = r15 - r30;
        r15 = r15 * r2;
        r14 = r14 + r15;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r15 = r0.scale;
        r14 = r14 * r15;
        r9.lineTo(r12, r14);
        r9 = r0.path2;
        r12 = r4[r10];
        r14 = r11[r10];
        r10 = r4[r10];
        r14 = r14 - r10;
        r14 = r14 * r2;
        r12 = r12 + r14;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r10 = (float) r10;
        r12 = r0.scale;
        r10 = r10 * r12;
        r12 = r4[r13];
        r14 = r11[r13];
        r13 = r4[r13];
        r14 = r14 - r13;
        r14 = r14 * r2;
        r12 = r12 + r14;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r13 = r0.scale;
        r12 = r12 * r13;
        r9.lineTo(r10, r12);
    L_0x0a5c:
        r1 = r1 + 1;
        r14 = r27;
        r9 = r29;
        goto L_0x0996;
    L_0x0a64:
        r29 = r9;
        r27 = r14;
        r1 = r0.paint2;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r1.setAlpha(r9);
    L_0x0a6f:
        r1 = r0.path1;
        r1.close();
        r1 = r0.path2;
        r1.close();
        r33.save();
        r1 = r8.left;
        r1 = (float) r1;
        r3 = r8.top;
        r3 = (float) r3;
        r7.translate(r1, r3);
        r1 = (float) r5;
        r14 = r27 - r5;
        r3 = (float) r14;
        r3 = r3 * r2;
        r1 = r1 + r3;
        r2 = r8.left;
        r2 = r29 - r2;
        r2 = (float) r2;
        r3 = r8.top;
        r10 = r26 - r3;
        r3 = (float) r10;
        r7.rotate(r1, r2, r3);
        r1 = r0.currentIcon;
        if (r1 == 0) goto L_0x0aa0;
    L_0x0a9d:
        r2 = 1;
        if (r1 != r2) goto L_0x0aa5;
    L_0x0aa0:
        r1 = r0.currentIcon;
        r2 = 4;
        if (r1 != r2) goto L_0x0ab5;
    L_0x0aa5:
        r1 = r8.left;
        r1 = r29 - r1;
        r1 = (float) r1;
        r2 = r8.top;
        r10 = r26 - r2;
        r2 = (float) r10;
        r8 = r22;
        r7.scale(r8, r8, r1, r2);
        goto L_0x0ab7;
    L_0x0ab5:
        r8 = r22;
    L_0x0ab7:
        r1 = r0.path1;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r1 = r0.path2;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r33.restore();
    L_0x0ac8:
        r1 = r0.currentIcon;
        r2 = 6;
        if (r1 == r2) goto L_0x0ad1;
    L_0x0acd:
        r1 = r0.nextIcon;
        if (r1 != r2) goto L_0x0b6a;
    L_0x0ad1:
        r1 = r0.currentIcon;
        if (r1 == r2) goto L_0x0af9;
    L_0x0ad5:
        r1 = r0.transitionProgress;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 <= 0) goto L_0x0af5;
    L_0x0adb:
        r1 = r1 - r20;
        r1 = r1 / r20;
        r2 = r1 / r20;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = java.lang.Math.min(r3, r2);
        r6 = r3 - r2;
        r2 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r2 <= 0) goto L_0x0af2;
    L_0x0aed:
        r1 = r1 - r20;
        r1 = r1 / r20;
        goto L_0x0af3;
    L_0x0af2:
        r1 = 0;
    L_0x0af3:
        r10 = r1;
        goto L_0x0afc;
    L_0x0af5:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 0;
        goto L_0x0afc;
    L_0x0af9:
        r6 = 0;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0afc:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r11 = r26 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r12 = r29 - r1;
        r1 = r0.paint;
        r1.setAlpha(r9);
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r2 >= 0) goto L_0x0b46;
    L_0x0b15:
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r12 - r1;
        r2 = (float) r1;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r11 - r1;
        r3 = (float) r1;
        r1 = (float) r12;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r4 = r4 * r6;
        r4 = r1 - r4;
        r1 = (float) r11;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r5 = r5 * r6;
        r5 = r1 - r5;
        r6 = r0.paint;
        r1 = r33;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0b46:
        r1 = 0;
        r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b6a;
    L_0x0b4b:
        r2 = (float) r12;
        r3 = (float) r11;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r1 * r10;
        r4 = r2 + r1;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r1 * r10;
        r5 = r3 - r1;
        r6 = r0.paint;
        r1 = r33;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0b6a:
        r1 = r17;
        if (r23 == 0) goto L_0x0bad;
    L_0x0b6e:
        r2 = r23;
        if (r2 == r1) goto L_0x0bad;
    L_0x0b72:
        r3 = r2.getIntrinsicWidth();
        r3 = (float) r3;
        r3 = r3 * r25;
        r3 = (int) r3;
        r4 = r2.getIntrinsicHeight();
        r4 = (float) r4;
        r4 = r4 * r25;
        r4 = (int) r4;
        r5 = r0.colorFilter;
        r2.setColorFilter(r5);
        r5 = r0.currentIcon;
        r6 = r0.nextIcon;
        if (r5 != r6) goto L_0x0b90;
    L_0x0b8d:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0b99;
    L_0x0b90:
        r5 = r0.transitionProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r6 - r5;
        r5 = r5 * r19;
        r5 = (int) r5;
    L_0x0b99:
        r2.setAlpha(r5);
        r5 = 2;
        r3 = r3 / r5;
        r6 = r29 - r3;
        r4 = r4 / r5;
        r10 = r26 - r4;
        r3 = r29 + r3;
        r4 = r26 + r4;
        r2.setBounds(r6, r10, r3, r4);
        r2.draw(r7);
    L_0x0bad:
        if (r1 == 0) goto L_0x0be6;
    L_0x0baf:
        r2 = r1.getIntrinsicWidth();
        r2 = (float) r2;
        r2 = r2 * r8;
        r2 = (int) r2;
        r3 = r1.getIntrinsicHeight();
        r3 = (float) r3;
        r3 = r3 * r8;
        r3 = (int) r3;
        r4 = r0.colorFilter;
        r1.setColorFilter(r4);
        r4 = r0.currentIcon;
        r5 = r0.nextIcon;
        if (r4 != r5) goto L_0x0bcd;
    L_0x0bca:
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0bd2;
    L_0x0bcd:
        r4 = r0.transitionProgress;
        r4 = r4 * r19;
        r4 = (int) r4;
    L_0x0bd2:
        r1.setAlpha(r4);
        r4 = 2;
        r2 = r2 / r4;
        r9 = r29 - r2;
        r3 = r3 / r4;
        r10 = r26 - r3;
        r2 = r29 + r2;
        r3 = r26 + r3;
        r1.setBounds(r9, r10, r2, r3);
        r1.draw(r7);
    L_0x0be6:
        r1 = java.lang.System.currentTimeMillis();
        r3 = r0.lastAnimationTime;
        r3 = r1 - r3;
        r5 = 17;
        r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r8 <= 0) goto L_0x0bf6;
    L_0x0bf4:
        r3 = 17;
    L_0x0bf6:
        r0.lastAnimationTime = r1;
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0bfd:
        r2 = 14;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = 4;
        if (r1 != r5) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0c0e:
        r2 = 13;
        if (r1 != r2) goto L_0x0c5f;
    L_0x0CLASSNAME:
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
        if (r1 == r2) goto L_0x0c5c;
    L_0x0c2d:
        r1 = r0.downloadProgress;
        r2 = r0.downloadProgressAnimationStart;
        r5 = r1 - r2;
        r6 = 0;
        r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r8 <= 0) goto L_0x0c5c;
    L_0x0CLASSNAME:
        r6 = r0.downloadProgressTime;
        r8 = (float) r3;
        r6 = r6 + r8;
        r0.downloadProgressTime = r6;
        r6 = r0.downloadProgressTime;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x0c4e;
    L_0x0CLASSNAME:
        r0.animatedDownloadProgress = r1;
        r0.downloadProgressAnimationStart = r1;
        r1 = 0;
        r0.downloadProgressTime = r1;
        goto L_0x0c5c;
    L_0x0c4e:
        r1 = r0.interpolator;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r6 = r6 / r8;
        r1 = r1.getInterpolation(r6);
        r5 = r5 * r1;
        r2 = r2 + r5;
        r0.animatedDownloadProgress = r2;
    L_0x0c5c:
        r32.invalidateSelf();
    L_0x0c5f:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x0CLASSNAME;
    L_0x0c6b:
        r3 = (float) r3;
        r4 = r0.transitionAnimationTime;
        r3 = r3 / r4;
        r1 = r1 + r3;
        r0.transitionProgress = r1;
        r1 = r0.transitionProgress;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.nextIcon;
        r0.currentIcon = r1;
        r0.transitionProgress = r2;
        r1 = 0;
        r0.animatingTransition = r1;
    L_0x0CLASSNAME:
        r32.invalidateSelf();
    L_0x0CLASSNAME:
        r1 = r28;
        r2 = 1;
        if (r1 < r2) goto L_0x0c8c;
    L_0x0CLASSNAME:
        r7.restoreToCount(r1);
    L_0x0c8c:
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
