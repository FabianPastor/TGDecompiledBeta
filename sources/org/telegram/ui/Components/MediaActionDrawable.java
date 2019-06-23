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

    /* JADX WARNING: Removed duplicated region for block: B:381:0x0aed  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0ac9  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0b09  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0ba2  */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0b62  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0ba6  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0beb  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0c5a  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0ac9  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0aed  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0b09  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0b62  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0ba2  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0ba6  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0beb  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0c5a  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05f1  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0600  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x06d9  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06ed  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0721  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0739  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0736  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07f5  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0821  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0813  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0839 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x05b7  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05f1  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0600  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x06d9  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06ed  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0721  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0736  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0739  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x07cb  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07f5  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0813  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0821  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0839 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x053e  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x05b7  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05f1  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0600  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x06d9  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06ed  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0721  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0739  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0736  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x07cb  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07f5  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0821  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0813  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0839 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x048a  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0495  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0526  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x053e  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x05b7  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05f1  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0600  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x06d9  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06ed  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0721  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0736  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0739  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x07cb  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07f5  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0813  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0821  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0839 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x048a  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0495  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0526  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x053e  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x05b7  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05f1  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0600  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x06d9  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x06ed  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0721  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0739  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0736  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0760  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x07cb  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07f5  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0821  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0813  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0839 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0982  */
    /* JADX WARNING: Missing block: B:88:0x0313, code skipped:
            if (r1 != r13) goto L_0x0317;
     */
    /* JADX WARNING: Missing block: B:178:0x0542, code skipped:
            if (r1 != r13) goto L_0x05b1;
     */
    /* JADX WARNING: Missing block: B:190:0x057f, code skipped:
            if (r1 != r13) goto L_0x05a3;
     */
    /* JADX WARNING: Missing block: B:417:0x0CLASSNAME, code skipped:
            if (r1 != 13) goto L_0x0CLASSNAME;
     */
    public void draw(android.graphics.Canvas r35) {
        /*
        r34 = this;
        r0 = r34;
        r7 = r35;
        r8 = r34.getBounds();
        r9 = r8.centerX();
        r10 = r8.centerY();
        r1 = r0.nextIcon;
        r11 = 6;
        r12 = 4;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r1 != r12) goto L_0x0025;
    L_0x0018:
        r1 = r0.transitionProgress;
        r1 = r13 - r1;
        r35.save();
        r2 = (float) r9;
        r3 = (float) r10;
        r7.scale(r1, r1, r2, r3);
        goto L_0x0039;
    L_0x0025:
        if (r1 == r11) goto L_0x002b;
    L_0x0027:
        r2 = 10;
        if (r1 != r2) goto L_0x0039;
    L_0x002b:
        r1 = r0.currentIcon;
        if (r1 != r12) goto L_0x0039;
    L_0x002f:
        r35.save();
        r1 = r0.transitionProgress;
        r2 = (float) r9;
        r3 = (float) r10;
        r7.scale(r1, r1, r2, r3);
    L_0x0039:
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r16 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r5 = 3;
        r17 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r4 = 14;
        r18 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r3 = 2;
        if (r1 == r3) goto L_0x0056;
    L_0x004c:
        r1 = r0.nextIcon;
        if (r1 != r3) goto L_0x0051;
    L_0x0050:
        goto L_0x0056;
    L_0x0051:
        r13 = 3;
        r14 = 14;
        goto L_0x0305;
    L_0x0056:
        r1 = (float) r10;
        r19 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = (float) r2;
        r11 = r0.scale;
        r2 = r2 * r11;
        r2 = r1 - r2;
        r11 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r11 = r11 + r1;
        r12 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r14 = r0.scale;
        r12 = r12 * r14;
        r12 = r12 + r1;
        r14 = r0.currentIcon;
        if (r14 == r5) goto L_0x0082;
    L_0x0080:
        if (r14 != r4) goto L_0x00a8;
    L_0x0082:
        r14 = r0.nextIcon;
        if (r14 != r3) goto L_0x00a8;
    L_0x0086:
        r14 = r0.paint;
        r15 = r0.transitionProgress;
        r15 = r15 / r18;
        r15 = java.lang.Math.min(r13, r15);
        r15 = r15 * r17;
        r15 = (int) r15;
        r14.setAlpha(r15);
        r14 = r0.transitionProgress;
        r15 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r6 = r0.scale;
        r15 = r15 * r6;
        r15 = r15 + r1;
        r6 = r14;
        r14 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x00de;
    L_0x00a8:
        r6 = r0.nextIcon;
        if (r6 == r5) goto L_0x00cb;
    L_0x00ac:
        if (r6 == r4) goto L_0x00cb;
    L_0x00ae:
        if (r6 == r3) goto L_0x00cb;
    L_0x00b0:
        r6 = r0.paint;
        r14 = r0.savedTransitionProgress;
        r14 = r14 / r18;
        r14 = java.lang.Math.min(r13, r14);
        r14 = r14 * r17;
        r15 = r0.transitionProgress;
        r15 = r13 - r15;
        r14 = r14 * r15;
        r14 = (int) r14;
        r6.setAlpha(r14);
        r6 = r0.savedTransitionProgress;
        r14 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x00d4;
    L_0x00cb:
        r6 = r0.paint;
        r14 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r6.setAlpha(r14);
        r6 = r0.transitionProgress;
    L_0x00d4:
        r15 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r15 = (float) r15;
        r4 = r0.scale;
        r15 = r15 * r4;
        r15 = r15 + r1;
    L_0x00de:
        r4 = r0.animatingTransition;
        if (r4 == 0) goto L_0x02aa;
    L_0x00e2:
        r4 = r0.nextIcon;
        if (r4 == r3) goto L_0x0258;
    L_0x00e6:
        r4 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r4 > 0) goto L_0x00ec;
    L_0x00ea:
        goto L_0x0258;
    L_0x00ec:
        r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r4 = r0.scale;
        r2 = r2 * r4;
        r6 = r6 - r18;
        r4 = r6 / r18;
        r11 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r11 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r11 <= 0) goto L_0x010e;
    L_0x0102:
        r11 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r6 = r6 - r11;
        r11 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r6 = r6 / r11;
        r11 = r6;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0115;
    L_0x010e:
        r11 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r6 = r6 / r11;
        r24 = r6;
        r11 = 0;
    L_0x0115:
        r6 = r0.rect;
        r14 = (float) r9;
        r3 = r14 - r2;
        r22 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = r2 / r22;
        r5 = r12 - r2;
        r2 = r2 + r12;
        r6.set(r3, r5, r14, r2);
        r2 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r11 * r2;
        r2 = r0.rect;
        r5 = NUM; // 0x42d00000 float:104.0 double:5.5381189E-315;
        r4 = r4 * r5;
        r4 = r4 - r3;
        r5 = 0;
        r6 = r0.paint;
        r28 = r1;
        r1 = r35;
        r13 = 0;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r12 - r15;
        r1 = r1 * r24;
        r15 = r15 + r1;
        r1 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r1 <= 0) goto L_0x024e;
    L_0x0143:
        r1 = r0.nextIcon;
        r6 = 14;
        if (r1 != r6) goto L_0x014b;
    L_0x0149:
        r5 = 0;
        goto L_0x0154;
    L_0x014b:
        r1 = -NUM; // 0xffffffffCLASSNAME float:-45.0 double:NaN;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r2 - r11;
        r2 = r3 * r1;
        r5 = r2;
    L_0x0154:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = (float) r1;
        r1 = r1 * r11;
        r2 = r0.scale;
        r1 = r1 * r2;
        r11 = r11 * r17;
        r2 = (int) r11;
        r3 = r0.nextIcon;
        r11 = 3;
        if (r3 == r11) goto L_0x017c;
    L_0x0167:
        if (r3 == r6) goto L_0x017c;
    L_0x0169:
        r4 = 2;
        if (r3 == r4) goto L_0x017c;
    L_0x016c:
        r3 = r0.transitionProgress;
        r3 = r3 / r18;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = java.lang.Math.min(r4, r3);
        r3 = r4 - r3;
        r2 = (float) r2;
        r2 = r2 * r3;
        r2 = (int) r2;
    L_0x017c:
        r4 = r2;
        r2 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1));
        if (r2 == 0) goto L_0x018a;
    L_0x0181:
        r35.save();
        r2 = r28;
        r7.rotate(r5, r14, r2);
        goto L_0x018c;
    L_0x018a:
        r2 = r28;
    L_0x018c:
        if (r4 == 0) goto L_0x0241;
    L_0x018e:
        r3 = r0.paint;
        r3.setAlpha(r4);
        r3 = r0.nextIcon;
        if (r3 != r6) goto L_0x021e;
    L_0x0197:
        r1 = r0.paint3;
        r1.setAlpha(r4);
        r1 = r0.rect;
        r2 = NUM; // 0x40600000 float:3.5 double:5.3360734E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r9 - r3;
        r3 = (float) r3;
        r20 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r6 = r10 - r20;
        r6 = (float) r6;
        r20 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r11 = r9 + r20;
        r11 = (float) r11;
        r20 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r10 + r20;
        r2 = (float) r2;
        r1.set(r3, r6, r11, r2);
        r1 = r0.rect;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r6;
        r6 = r0.paint3;
        r7.drawRoundRect(r1, r3, r2, r6);
        r1 = r0.paint;
        r2 = (float) r4;
        r3 = NUM; // 0x3e19999a float:0.15 double:5.147497604E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x01e5;
    L_0x01e2:
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x01e7;
    L_0x01e5:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x01e7:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r0.rect;
        r3 = r8.left;
        r3 = r3 + r1;
        r3 = (float) r3;
        r6 = r8.top;
        r6 = r6 + r1;
        r6 = (float) r6;
        r11 = r8.right;
        r11 = r11 - r1;
        r11 = (float) r11;
        r13 = r8.bottom;
        r13 = r13 - r1;
        r1 = (float) r13;
        r2.set(r3, r6, r11, r1);
        r2 = r0.rect;
        r3 = 0;
        r6 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r11 = 0;
        r13 = r0.paint;
        r1 = r35;
        r30 = r4;
        r4 = r6;
        r23 = r5;
        r5 = r11;
        r11 = 14;
        r6 = r13;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r0.paint;
        r2 = r30;
        r1.setAlpha(r2);
        goto L_0x0245;
    L_0x021e:
        r23 = r5;
        r11 = 14;
        r13 = r14 - r1;
        r24 = r2 - r1;
        r25 = r14 + r1;
        r26 = r2 + r1;
        r6 = r0.paint;
        r1 = r35;
        r2 = r13;
        r3 = r24;
        r4 = r25;
        r5 = r26;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r25;
        r4 = r13;
        r1.drawLine(r2, r3, r4, r5, r6);
        goto L_0x0245;
    L_0x0241:
        r23 = r5;
        r11 = 14;
    L_0x0245:
        r1 = 0;
        r2 = (r23 > r1 ? 1 : (r23 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0250;
    L_0x024a:
        r35.restore();
        goto L_0x0250;
    L_0x024e:
        r11 = 14;
    L_0x0250:
        r3 = r12;
        r1 = r14;
        r2 = r1;
        r5 = 2;
        r13 = 3;
        r14 = 14;
        goto L_0x02a0;
    L_0x0258:
        r13 = 3;
        r14 = 14;
        r1 = r0.nextIcon;
        r5 = 2;
        if (r1 != r5) goto L_0x0265;
    L_0x0260:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r1 - r6;
        goto L_0x026b;
    L_0x0265:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r6 / r18;
        r6 = r1 - r3;
    L_0x026b:
        r15 = r15 - r2;
        r15 = r15 * r3;
        r15 = r15 + r2;
        r12 = r12 - r11;
        r12 = r12 * r3;
        r12 = r12 + r11;
        r1 = (float) r9;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r2 = r2 * r6;
        r3 = r0.scale;
        r2 = r2 * r3;
        r2 = r1 - r2;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r3 * r6;
        r4 = r0.scale;
        r3 = r3 * r4;
        r1 = r1 + r3;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r3 * r6;
        r4 = r0.scale;
        r3 = r3 * r4;
        r3 = r12 - r3;
    L_0x02a0:
        r23 = r1;
        r11 = r12;
        r12 = r2;
        r33 = r15;
        r15 = r3;
        r3 = r33;
        goto L_0x02da;
    L_0x02aa:
        r5 = 2;
        r13 = 3;
        r14 = 14;
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
        r6 = r0.scale;
        r4 = r4 * r6;
        r1 = r1 + r4;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r6 = r0.scale;
        r4 = r4 * r6;
        r4 = r11 - r4;
        r23 = r1;
        r12 = r3;
        r15 = r4;
        r3 = r2;
    L_0x02da:
        r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r1 == 0) goto L_0x02e8;
    L_0x02de:
        r4 = (float) r9;
        r6 = r0.paint;
        r1 = r35;
        r2 = r4;
        r5 = r11;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x02e8:
        r6 = (float) r9;
        r1 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x0305;
    L_0x02ed:
        r5 = r0.paint;
        r1 = r35;
        r2 = r12;
        r3 = r15;
        r4 = r6;
        r12 = r5;
        r5 = r11;
        r24 = r6;
        r6 = r12;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r23;
        r4 = r24;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0305:
        r1 = r0.currentIcon;
        r12 = 1;
        if (r1 == r13) goto L_0x0388;
    L_0x030a:
        if (r1 == r14) goto L_0x0388;
    L_0x030c:
        r2 = 4;
        if (r1 != r2) goto L_0x0317;
    L_0x030f:
        r1 = r0.nextIcon;
        if (r1 == r14) goto L_0x0388;
    L_0x0313:
        if (r1 != r13) goto L_0x0317;
    L_0x0315:
        goto L_0x0388;
    L_0x0317:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x032b;
    L_0x031d:
        r2 = r0.nextIcon;
        r3 = 10;
        if (r2 == r3) goto L_0x032b;
    L_0x0323:
        r2 = 13;
        if (r1 != r2) goto L_0x0328;
    L_0x0327:
        goto L_0x032b;
    L_0x0328:
        r15 = 2;
        goto L_0x05ba;
    L_0x032b:
        r1 = r0.nextIcon;
        r2 = 4;
        if (r1 == r2) goto L_0x0337;
    L_0x0330:
        r2 = 6;
        if (r1 != r2) goto L_0x0334;
    L_0x0333:
        goto L_0x0337;
    L_0x0334:
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0340;
    L_0x0337:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = r2 - r1;
        r1 = r1 * r17;
        r6 = (int) r1;
    L_0x0340:
        if (r6 == 0) goto L_0x0328;
    L_0x0342:
        r1 = r0.paint;
        r2 = (float) r6;
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
        if (r1 == 0) goto L_0x0360;
    L_0x035d:
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x0362;
    L_0x0360:
        r15 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x0362:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
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
        r15 = r8.bottom;
        r15 = r15 - r1;
        r1 = (float) r15;
        r2.set(r3, r5, r6, r1);
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r35;
        r1.drawArc(r2, r3, r4, r5, r6);
        goto L_0x0328;
    L_0x0388:
        r1 = r0.nextIcon;
        r15 = 2;
        if (r1 != r15) goto L_0x03b7;
    L_0x038d:
        r1 = r0.transitionProgress;
        r2 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r2 > 0) goto L_0x03a9;
    L_0x0393:
        r1 = r1 / r18;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = r2 - r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = (float) r2;
        r2 = r2 * r1;
        r3 = r0.scale;
        r2 = r2 * r3;
        r1 = r1 * r17;
        r1 = (int) r1;
        r6 = r1;
        goto L_0x03ab;
    L_0x03a9:
        r2 = 0;
        r6 = 0;
    L_0x03ab:
        r23 = r2;
        r3 = r6;
        r1 = 0;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x03b1:
        r4 = 0;
        r5 = 0;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0486;
    L_0x03b7:
        if (r1 == 0) goto L_0x044e;
    L_0x03b9:
        if (r1 == r12) goto L_0x044e;
    L_0x03bb:
        r2 = 5;
        if (r1 == r2) goto L_0x044e;
    L_0x03be:
        r2 = 8;
        if (r1 == r2) goto L_0x044e;
    L_0x03c2:
        r2 = 9;
        if (r1 == r2) goto L_0x044e;
    L_0x03c6:
        r2 = 7;
        if (r1 == r2) goto L_0x044e;
    L_0x03c9:
        r2 = 6;
        if (r1 != r2) goto L_0x03ce;
    L_0x03cc:
        goto L_0x044e;
    L_0x03ce:
        r2 = 4;
        if (r1 != r2) goto L_0x03fd;
    L_0x03d1:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r2 - r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = (float) r2;
        r4 = r0.scale;
        r2 = r2 * r4;
        r4 = r3 * r17;
        r6 = (int) r4;
        r4 = r0.currentIcon;
        if (r4 != r14) goto L_0x03e9;
    L_0x03e7:
        r1 = 0;
        goto L_0x03ef;
    L_0x03e9:
        r3 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r3;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x03ef:
        r4 = r1;
        r23 = r2;
        r1 = 0;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = 0;
    L_0x03f6:
        r33 = r6;
        r6 = r3;
        r3 = r33;
        goto L_0x0486;
    L_0x03fd:
        if (r1 == r14) goto L_0x0413;
    L_0x03ff:
        if (r1 != r13) goto L_0x0402;
    L_0x0401:
        goto L_0x0413;
    L_0x0402:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = (float) r1;
        r2 = r0.scale;
        r2 = r2 * r1;
        r23 = r2;
        r1 = 0;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x03b1;
    L_0x0413:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r2 - r1;
        r2 = r0.currentIcon;
        r4 = 4;
        if (r2 != r4) goto L_0x0421;
    L_0x041e:
        r3 = r1;
        r2 = 0;
        goto L_0x0428;
    L_0x0421:
        r2 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r3 = r3 * r2;
        r2 = r3;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0428:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = (float) r4;
        r5 = r0.scale;
        r4 = r4 * r5;
        r1 = r1 * r17;
        r6 = (int) r1;
        r1 = r0.nextIcon;
        if (r1 != r14) goto L_0x043e;
    L_0x0438:
        r1 = r8.left;
        r1 = (float) r1;
        r5 = r8.top;
        goto L_0x0447;
    L_0x043e:
        r1 = r8.centerX();
        r1 = (float) r1;
        r5 = r8.centerY();
    L_0x0447:
        r5 = (float) r5;
        r23 = r4;
        r4 = r2;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x03f6;
    L_0x044e:
        r1 = r0.nextIcon;
        r2 = 6;
        if (r1 != r2) goto L_0x045e;
    L_0x0453:
        r1 = r0.transitionProgress;
        r1 = r1 / r18;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = java.lang.Math.min(r2, r1);
        goto L_0x0462;
    L_0x045e:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = r0.transitionProgress;
    L_0x0462:
        r3 = r2 - r1;
        r4 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r1 = r1 * r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = (float) r4;
        r4 = r4 * r3;
        r5 = r0.scale;
        r4 = r4 * r5;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = r3 * r5;
        r3 = java.lang.Math.min(r2, r3);
        r3 = r3 * r17;
        r6 = (int) r3;
        r23 = r4;
        r3 = r6;
        r5 = 0;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r1;
        r1 = 0;
    L_0x0486:
        r24 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r24 == 0) goto L_0x0490;
    L_0x048a:
        r35.save();
        r7.scale(r6, r6, r1, r5);
    L_0x0490:
        r1 = 0;
        r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x049d;
    L_0x0495:
        r35.save();
        r1 = (float) r9;
        r2 = (float) r10;
        r7.rotate(r4, r1, r2);
    L_0x049d:
        if (r3 == 0) goto L_0x0526;
    L_0x049f:
        r1 = r0.paint;
        r2 = (float) r3;
        r5 = r0.overrideAlpha;
        r5 = r5 * r2;
        r5 = (int) r5;
        r1.setAlpha(r5);
        r1 = r0.currentIcon;
        if (r1 == r14) goto L_0x04e2;
    L_0x04ae:
        r1 = r0.nextIcon;
        if (r1 != r14) goto L_0x04b3;
    L_0x04b2:
        goto L_0x04e2;
    L_0x04b3:
        r1 = (float) r9;
        r21 = r1 - r23;
        r2 = (float) r10;
        r24 = r2 - r23;
        r25 = r1 + r23;
        r23 = r2 + r23;
        r5 = r0.paint;
        r1 = r35;
        r2 = r21;
        r11 = r3;
        r3 = r24;
        r27 = r4;
        r4 = r25;
        r28 = r5;
        r5 = r23;
        r29 = r6;
        r6 = r28;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r25;
        r4 = r21;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = 0;
        r22 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x052e;
    L_0x04e2:
        r11 = r3;
        r27 = r4;
        r29 = r6;
        r1 = r0.paint3;
        r3 = r0.overrideAlpha;
        r2 = r2 * r3;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = r0.rect;
        r2 = NUM; // 0x40600000 float:3.5 double:5.3360734E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r9 - r3;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r4 = r10 - r4;
        r4 = (float) r4;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r5 = r5 + r9;
        r5 = (float) r5;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r10;
        r2 = (float) r2;
        r1.set(r3, r4, r5, r2);
        r1 = r0.rect;
        r22 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = (float) r3;
        r4 = r0.paint3;
        r7.drawRoundRect(r1, r2, r3, r4);
        goto L_0x052d;
    L_0x0526:
        r11 = r3;
        r27 = r4;
        r29 = r6;
        r22 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
    L_0x052d:
        r1 = 0;
    L_0x052e:
        r2 = (r27 > r1 ? 1 : (r27 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x0535;
    L_0x0532:
        r35.restore();
    L_0x0535:
        r1 = r0.currentIcon;
        if (r1 == r13) goto L_0x0544;
    L_0x0539:
        if (r1 == r14) goto L_0x0544;
    L_0x053b:
        r2 = 4;
        if (r1 != r2) goto L_0x05b1;
    L_0x053e:
        r1 = r0.nextIcon;
        if (r1 == r14) goto L_0x0544;
    L_0x0542:
        if (r1 != r13) goto L_0x05b1;
    L_0x0544:
        if (r11 == 0) goto L_0x05b1;
    L_0x0546:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r3 = r0.animatedDownloadProgress;
        r3 = r3 * r2;
        r21 = java.lang.Math.max(r1, r3);
        r1 = r0.isMini;
        if (r1 == 0) goto L_0x0559;
    L_0x0556:
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x055b;
    L_0x0559:
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
    L_0x055b:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
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
        if (r1 == r14) goto L_0x0581;
    L_0x0578:
        r2 = 4;
        if (r1 != r2) goto L_0x05a3;
    L_0x057b:
        r1 = r0.nextIcon;
        if (r1 == r14) goto L_0x0581;
    L_0x057f:
        if (r1 != r13) goto L_0x05a3;
    L_0x0581:
        r1 = r0.paint;
        r2 = (float) r11;
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
        r1 = r35;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r0.paint;
        r1.setAlpha(r11);
    L_0x05a3:
        r2 = r0.rect;
        r3 = r0.downloadRadOffset;
        r5 = 0;
        r6 = r0.paint;
        r1 = r35;
        r4 = r21;
        r1.drawArc(r2, r3, r4, r5, r6);
    L_0x05b1:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r29 > r1 ? 1 : (r29 == r1 ? 0 : -1));
        if (r2 == 0) goto L_0x05ba;
    L_0x05b7:
        r35.restore();
    L_0x05ba:
        r1 = 0;
        r2 = 0;
        r3 = r0.currentIcon;
        r4 = r0.nextIcon;
        if (r3 != r4) goto L_0x05c7;
    L_0x05c2:
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x05e9;
    L_0x05c7:
        r4 = 4;
        if (r3 != r4) goto L_0x05d1;
    L_0x05ca:
        r3 = r0.transitionProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r4 - r3;
        goto L_0x05e6;
    L_0x05d1:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r0.transitionProgress;
        r3 = r3 / r18;
        r3 = java.lang.Math.min(r4, r3);
        r5 = r0.transitionProgress;
        r5 = r5 / r18;
        r5 = r4 - r5;
        r4 = 0;
        r5 = java.lang.Math.max(r4, r5);
    L_0x05e6:
        r11 = r3;
        r21 = r5;
    L_0x05e9:
        r3 = r0.nextIcon;
        r4 = 5;
        if (r3 != r4) goto L_0x05f1;
    L_0x05ee:
        r1 = org.telegram.ui.ActionBar.Theme.chat_fileIcon;
        goto L_0x05f8;
    L_0x05f1:
        r3 = r0.currentIcon;
        r4 = 5;
        if (r3 != r4) goto L_0x05f8;
    L_0x05f6:
        r2 = org.telegram.ui.ActionBar.Theme.chat_fileIcon;
    L_0x05f8:
        r3 = r0.nextIcon;
        r4 = 7;
        if (r3 != r4) goto L_0x0600;
    L_0x05fd:
        r1 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
        goto L_0x0607;
    L_0x0600:
        r3 = r0.currentIcon;
        r4 = 7;
        if (r3 != r4) goto L_0x0607;
    L_0x0605:
        r2 = org.telegram.ui.ActionBar.Theme.chat_flameIcon;
    L_0x0607:
        r3 = r0.nextIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x0610;
    L_0x060d:
        r1 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
        goto L_0x0618;
    L_0x0610:
        r3 = r0.currentIcon;
        r4 = 8;
        if (r3 != r4) goto L_0x0618;
    L_0x0616:
        r2 = org.telegram.ui.ActionBar.Theme.chat_gifIcon;
    L_0x0618:
        r6 = r1;
        r5 = r2;
        r1 = r0.currentIcon;
        r2 = 9;
        if (r1 == r2) goto L_0x062d;
    L_0x0620:
        r1 = r0.nextIcon;
        r2 = 9;
        if (r1 != r2) goto L_0x0627;
    L_0x0626:
        goto L_0x062d;
    L_0x0627:
        r31 = r5;
        r32 = r6;
        goto L_0x06ac;
    L_0x062d:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x0638;
    L_0x0635:
        r2 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x063d;
    L_0x0638:
        r2 = r0.transitionProgress;
        r2 = r2 * r17;
        r2 = (int) r2;
    L_0x063d:
        r1.setAlpha(r2);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = r10 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = r9 - r1;
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x065e;
    L_0x0654:
        r35.save();
        r1 = r0.transitionProgress;
        r2 = (float) r9;
        r14 = (float) r10;
        r7.scale(r1, r1, r2, r14);
    L_0x065e:
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r3 - r1;
        r2 = (float) r1;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r4 - r1;
        r14 = (float) r1;
        r1 = (float) r3;
        r13 = (float) r4;
        r12 = r0.paint;
        r23 = r1;
        r1 = r35;
        r24 = r3;
        r3 = r14;
        r14 = r4;
        r4 = r23;
        r31 = r5;
        r5 = r13;
        r32 = r6;
        r6 = r12;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = r24 + r1;
        r4 = (float) r3;
        r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r14 - r1;
        r5 = (float) r1;
        r6 = r0.paint;
        r1 = r35;
        r2 = r23;
        r3 = r13;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x06ac;
    L_0x06a9:
        r35.restore();
    L_0x06ac:
        r1 = r0.currentIcon;
        r2 = 12;
        if (r1 == r2) goto L_0x06b8;
    L_0x06b2:
        r1 = r0.nextIcon;
        r2 = 12;
        if (r1 != r2) goto L_0x0724;
    L_0x06b8:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x06c1;
    L_0x06be:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x06ce;
    L_0x06c1:
        r1 = 13;
        if (r2 != r1) goto L_0x06c8;
    L_0x06c5:
        r13 = r0.transitionProgress;
        goto L_0x06ce;
    L_0x06c8:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = r2 - r1;
    L_0x06ce:
        r1 = r0.paint;
        r2 = r0.currentIcon;
        r3 = r0.nextIcon;
        if (r2 != r3) goto L_0x06d9;
    L_0x06d6:
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x06dc;
    L_0x06d9:
        r2 = r13 * r17;
        r6 = (int) r2;
    L_0x06dc:
        r1.setAlpha(r6);
        org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x06f5;
    L_0x06ed:
        r35.save();
        r1 = (float) r9;
        r2 = (float) r10;
        r7.scale(r13, r13, r1, r2);
    L_0x06f5:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = (float) r1;
        r2 = r0.scale;
        r1 = r1 * r2;
        r2 = (float) r9;
        r12 = r2 - r1;
        r3 = (float) r10;
        r13 = r3 - r1;
        r14 = r2 + r1;
        r23 = r3 + r1;
        r6 = r0.paint;
        r1 = r35;
        r2 = r12;
        r3 = r13;
        r4 = r14;
        r5 = r23;
        r1.drawLine(r2, r3, r4, r5, r6);
        r6 = r0.paint;
        r2 = r14;
        r4 = r12;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 == r2) goto L_0x0724;
    L_0x0721:
        r35.restore();
    L_0x0724:
        r1 = r0.currentIcon;
        r2 = 13;
        if (r1 == r2) goto L_0x0730;
    L_0x072a:
        r1 = r0.nextIcon;
        r2 = 13;
        if (r1 != r2) goto L_0x07af;
    L_0x0730:
        r1 = r0.currentIcon;
        r2 = r0.nextIcon;
        if (r1 != r2) goto L_0x0739;
    L_0x0736:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0746;
    L_0x0739:
        r1 = 13;
        if (r2 != r1) goto L_0x0740;
    L_0x073d:
        r13 = r0.transitionProgress;
        goto L_0x0746;
    L_0x0740:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = r2 - r1;
    L_0x0746:
        r1 = r0.textPaint;
        r2 = r13 * r17;
        r2 = (int) r2;
        r1.setAlpha(r2);
        r1 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r1 + r10;
        r2 = r0.percentStringWidth;
        r2 = r2 / r15;
        r2 = r9 - r2;
        r3 = r0.currentIcon;
        r4 = r0.nextIcon;
        if (r3 == r4) goto L_0x0768;
    L_0x0760:
        r35.save();
        r3 = (float) r9;
        r4 = (float) r10;
        r7.scale(r13, r13, r3, r4);
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
        r35.restore();
    L_0x07af:
        r1 = r0.currentIcon;
        if (r1 == 0) goto L_0x07c6;
    L_0x07b3:
        r2 = 1;
        if (r1 == r2) goto L_0x07c7;
    L_0x07b6:
        r1 = r0.nextIcon;
        if (r1 == 0) goto L_0x07c7;
    L_0x07ba:
        if (r1 != r2) goto L_0x07bd;
    L_0x07bc:
        goto L_0x07c7;
    L_0x07bd:
        r27 = r9;
        r24 = r10;
        r8 = r11;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0ab7;
    L_0x07c6:
        r2 = 1;
    L_0x07c7:
        r1 = r0.currentIcon;
        if (r1 != 0) goto L_0x07cf;
    L_0x07cb:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x07d7;
    L_0x07cf:
        r1 = r0.currentIcon;
        if (r1 != r2) goto L_0x07e5;
    L_0x07d3:
        r1 = r0.nextIcon;
        if (r1 != 0) goto L_0x07e5;
    L_0x07d7:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x07e5;
    L_0x07db:
        r1 = r0.interpolator;
        r2 = r0.transitionProgress;
        r1 = r1.getInterpolation(r2);
        r2 = r1;
        goto L_0x07e6;
    L_0x07e5:
        r2 = 0;
    L_0x07e6:
        r1 = r0.path1;
        r1.reset();
        r1 = r0.path2;
        r1.reset();
        r1 = 0;
        r3 = r0.currentIcon;
        if (r3 == 0) goto L_0x0803;
    L_0x07f5:
        r4 = 1;
        if (r3 == r4) goto L_0x07fc;
    L_0x07f8:
        r3 = 0;
        r4 = r3;
        r5 = 0;
        goto L_0x080f;
    L_0x07fc:
        r3 = pausePath1;
        r4 = pausePath2;
        r5 = 90;
        goto L_0x080f;
    L_0x0803:
        r3 = playPath1;
        r1 = playPath2;
        r4 = playFinalPath;
        r5 = 0;
        r33 = r4;
        r4 = r1;
        r1 = r33;
    L_0x080f:
        r6 = r0.nextIcon;
        if (r6 == 0) goto L_0x0821;
    L_0x0813:
        r12 = 1;
        if (r6 == r12) goto L_0x081a;
    L_0x0816:
        r6 = 0;
        r12 = r6;
    L_0x0818:
        r13 = 0;
        goto L_0x0826;
    L_0x081a:
        r6 = pausePath1;
        r12 = pausePath2;
        r13 = 90;
        goto L_0x0826;
    L_0x0821:
        r6 = playPath1;
        r12 = playPath2;
        goto L_0x0818;
    L_0x0826:
        if (r3 != 0) goto L_0x0830;
    L_0x0828:
        r3 = 0;
        r4 = 0;
        r33 = r12;
        r12 = r4;
        r4 = r33;
        goto L_0x0835;
    L_0x0830:
        r33 = r6;
        r6 = r3;
        r3 = r33;
    L_0x0835:
        r14 = r0.animatingTransition;
        if (r14 != 0) goto L_0x08ce;
    L_0x0839:
        if (r1 == 0) goto L_0x08ce;
    L_0x083b:
        r3 = 0;
    L_0x083c:
        r4 = r1.length;
        r4 = r4 / r15;
        if (r3 >= r4) goto L_0x08c2;
    L_0x0840:
        if (r3 != 0) goto L_0x087f;
    L_0x0842:
        r4 = r0.path1;
        r6 = r3 * 2;
        r12 = r1[r6];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r14 = r0.scale;
        r12 = r12 * r14;
        r14 = r6 + 1;
        r23 = r1[r14];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r23);
        r15 = (float) r15;
        r23 = r11;
        r11 = r0.scale;
        r15 = r15 * r11;
        r4.moveTo(r12, r15);
        r4 = r0.path2;
        r6 = r1[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r11 = r0.scale;
        r6 = r6 * r11;
        r11 = r1[r14];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r4.moveTo(r6, r11);
        goto L_0x08bb;
    L_0x087f:
        r23 = r11;
        r4 = r0.path1;
        r6 = r3 * 2;
        r11 = r1[r6];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r12 = r6 + 1;
        r14 = r1[r12];
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r15 = r0.scale;
        r14 = r14 * r15;
        r4.lineTo(r11, r14);
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
    L_0x08bb:
        r3 = r3 + 1;
        r11 = r23;
        r15 = 2;
        goto L_0x083c;
    L_0x08c2:
        r23 = r11;
        r27 = r9;
        r24 = r10;
    L_0x08c8:
        r25 = r13;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0a5e;
    L_0x08ce:
        r23 = r11;
        if (r3 != 0) goto L_0x0982;
    L_0x08d2:
        r1 = 0;
    L_0x08d3:
        r3 = 5;
        if (r1 >= r3) goto L_0x0957;
    L_0x08d6:
        if (r1 != 0) goto L_0x0915;
    L_0x08d8:
        r3 = r0.path1;
        r11 = r1 * 2;
        r12 = r6[r11];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r14 = r0.scale;
        r12 = r12 * r14;
        r14 = r11 + 1;
        r15 = r6[r14];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r24 = r10;
        r10 = r0.scale;
        r15 = r15 * r10;
        r3.moveTo(r12, r15);
        r3 = r0.path2;
        r10 = r4[r11];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r11 = r4[r14];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r3.moveTo(r10, r11);
        goto L_0x0951;
    L_0x0915:
        r24 = r10;
        r3 = r0.path1;
        r10 = r1 * 2;
        r11 = r6[r10];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = r0.scale;
        r11 = r11 * r12;
        r12 = r10 + 1;
        r14 = r6[r12];
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r15 = r0.scale;
        r14 = r14 * r15;
        r3.lineTo(r11, r14);
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
    L_0x0951:
        r1 = r1 + 1;
        r10 = r24;
        goto L_0x08d3;
    L_0x0957:
        r24 = r10;
        r1 = r0.nextIcon;
        r3 = 4;
        if (r1 != r3) goto L_0x0970;
    L_0x095e:
        r1 = r0.paint2;
        r3 = r0.transitionProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r4 - r3;
        r3 = r3 * r17;
        r3 = (int) r3;
        r1.setAlpha(r3);
    L_0x096c:
        r27 = r9;
        goto L_0x08c8;
    L_0x0970:
        r3 = r0.paint2;
        r4 = r0.currentIcon;
        if (r4 != r1) goto L_0x0979;
    L_0x0976:
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x097e;
    L_0x0979:
        r1 = r0.transitionProgress;
        r1 = r1 * r17;
        r6 = (int) r1;
    L_0x097e:
        r3.setAlpha(r6);
        goto L_0x096c;
    L_0x0982:
        r24 = r10;
        r1 = 0;
    L_0x0985:
        r10 = 5;
        if (r1 >= r10) goto L_0x0a53;
    L_0x0988:
        if (r1 != 0) goto L_0x09ec;
    L_0x098a:
        r10 = r0.path1;
        r11 = r1 * 2;
        r14 = r6[r11];
        r15 = r3[r11];
        r25 = r6[r11];
        r15 = r15 - r25;
        r15 = r15 * r2;
        r14 = r14 + r15;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r15 = r0.scale;
        r14 = r14 * r15;
        r15 = r11 + 1;
        r25 = r6[r15];
        r27 = r3[r15];
        r28 = r6[r15];
        r27 = r27 - r28;
        r27 = r27 * r2;
        r25 = r25 + r27;
        r27 = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r25);
        r9 = (float) r9;
        r25 = r13;
        r13 = r0.scale;
        r9 = r9 * r13;
        r10.moveTo(r14, r9);
        r9 = r0.path2;
        r10 = r4[r11];
        r13 = r12[r11];
        r11 = r4[r11];
        r13 = r13 - r11;
        r13 = r13 * r2;
        r10 = r10 + r13;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r11 = r4[r15];
        r13 = r12[r15];
        r14 = r4[r15];
        r13 = r13 - r14;
        r13 = r13 * r2;
        r11 = r11 + r13;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r13 = r0.scale;
        r11 = r11 * r13;
        r9.moveTo(r10, r11);
        goto L_0x0a4b;
    L_0x09ec:
        r27 = r9;
        r25 = r13;
        r9 = r0.path1;
        r10 = r1 * 2;
        r11 = r6[r10];
        r13 = r3[r10];
        r14 = r6[r10];
        r13 = r13 - r14;
        r13 = r13 * r2;
        r11 = r11 + r13;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r13 = r0.scale;
        r11 = r11 * r13;
        r13 = r10 + 1;
        r14 = r6[r13];
        r15 = r3[r13];
        r28 = r6[r13];
        r15 = r15 - r28;
        r15 = r15 * r2;
        r14 = r14 + r15;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = (float) r14;
        r15 = r0.scale;
        r14 = r14 * r15;
        r9.lineTo(r11, r14);
        r9 = r0.path2;
        r11 = r4[r10];
        r14 = r12[r10];
        r10 = r4[r10];
        r14 = r14 - r10;
        r14 = r14 * r2;
        r11 = r11 + r14;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r11 = r4[r13];
        r14 = r12[r13];
        r13 = r4[r13];
        r14 = r14 - r13;
        r14 = r14 * r2;
        r11 = r11 + r14;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r13 = r0.scale;
        r11 = r11 * r13;
        r9.lineTo(r10, r11);
    L_0x0a4b:
        r1 = r1 + 1;
        r13 = r25;
        r9 = r27;
        goto L_0x0985;
    L_0x0a53:
        r27 = r9;
        r25 = r13;
        r1 = r0.paint2;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r1.setAlpha(r9);
    L_0x0a5e:
        r1 = r0.path1;
        r1.close();
        r1 = r0.path2;
        r1.close();
        r35.save();
        r1 = r8.left;
        r1 = (float) r1;
        r3 = r8.top;
        r3 = (float) r3;
        r7.translate(r1, r3);
        r1 = (float) r5;
        r13 = r25 - r5;
        r3 = (float) r13;
        r3 = r3 * r2;
        r1 = r1 + r3;
        r2 = r8.left;
        r2 = r27 - r2;
        r2 = (float) r2;
        r3 = r8.top;
        r10 = r24 - r3;
        r3 = (float) r10;
        r7.rotate(r1, r2, r3);
        r1 = r0.currentIcon;
        if (r1 == 0) goto L_0x0a8f;
    L_0x0a8c:
        r2 = 1;
        if (r1 != r2) goto L_0x0a94;
    L_0x0a8f:
        r1 = r0.currentIcon;
        r2 = 4;
        if (r1 != r2) goto L_0x0aa4;
    L_0x0a94:
        r1 = r8.left;
        r1 = r27 - r1;
        r1 = (float) r1;
        r2 = r8.top;
        r10 = r24 - r2;
        r2 = (float) r10;
        r8 = r23;
        r7.scale(r8, r8, r1, r2);
        goto L_0x0aa6;
    L_0x0aa4:
        r8 = r23;
    L_0x0aa6:
        r1 = r0.path1;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r1 = r0.path2;
        r2 = r0.paint2;
        r7.drawPath(r1, r2);
        r35.restore();
    L_0x0ab7:
        r1 = r0.currentIcon;
        r2 = 6;
        if (r1 == r2) goto L_0x0ac5;
    L_0x0abc:
        r1 = r0.nextIcon;
        if (r1 != r2) goto L_0x0ac1;
    L_0x0ac0:
        goto L_0x0ac5;
    L_0x0ac1:
        r2 = r31;
        goto L_0x0b60;
    L_0x0ac5:
        r1 = r0.currentIcon;
        if (r1 == r2) goto L_0x0aed;
    L_0x0ac9:
        r1 = r0.transitionProgress;
        r2 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r2 <= 0) goto L_0x0ae9;
    L_0x0acf:
        r1 = r1 - r18;
        r1 = r1 / r18;
        r2 = r1 / r18;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = java.lang.Math.min(r3, r2);
        r13 = r3 - r2;
        r2 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r2 <= 0) goto L_0x0ae6;
    L_0x0ae1:
        r1 = r1 - r18;
        r1 = r1 / r18;
        goto L_0x0ae7;
    L_0x0ae6:
        r1 = 0;
    L_0x0ae7:
        r10 = r1;
        goto L_0x0af0;
    L_0x0ae9:
        r10 = 0;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0af0;
    L_0x0aed:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
    L_0x0af0:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r11 = r24 + r1;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r12 = r27 - r1;
        r1 = r0.paint;
        r1.setAlpha(r9);
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1));
        if (r2 >= 0) goto L_0x0b3a;
    L_0x0b09:
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
        r4 = r4 * r13;
        r4 = r1 - r4;
        r1 = (float) r11;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r5 = r5 * r13;
        r5 = r1 - r5;
        r6 = r0.paint;
        r1 = r35;
        r1.drawLine(r2, r3, r4, r5, r6);
    L_0x0b3a:
        r1 = 0;
        r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r2 <= 0) goto L_0x0ac1;
    L_0x0b3f:
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
        r1 = r35;
        r1.drawLine(r2, r3, r4, r5, r6);
        goto L_0x0ac1;
    L_0x0b60:
        if (r2 == 0) goto L_0x0ba2;
    L_0x0b62:
        r1 = r32;
        if (r2 == r1) goto L_0x0ba4;
    L_0x0b66:
        r3 = r2.getIntrinsicWidth();
        r3 = (float) r3;
        r3 = r3 * r21;
        r3 = (int) r3;
        r4 = r2.getIntrinsicHeight();
        r4 = (float) r4;
        r4 = r4 * r21;
        r4 = (int) r4;
        r5 = r0.colorFilter;
        r2.setColorFilter(r5);
        r5 = r0.currentIcon;
        r6 = r0.nextIcon;
        if (r5 != r6) goto L_0x0b84;
    L_0x0b81:
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0b8d;
    L_0x0b84:
        r5 = r0.transitionProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = r6 - r5;
        r13 = r13 * r17;
        r6 = (int) r13;
    L_0x0b8d:
        r2.setAlpha(r6);
        r5 = 2;
        r3 = r3 / r5;
        r6 = r27 - r3;
        r4 = r4 / r5;
        r10 = r24 - r4;
        r3 = r27 + r3;
        r4 = r24 + r4;
        r2.setBounds(r6, r10, r3, r4);
        r2.draw(r7);
        goto L_0x0ba4;
    L_0x0ba2:
        r1 = r32;
    L_0x0ba4:
        if (r1 == 0) goto L_0x0bdd;
    L_0x0ba6:
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
        if (r4 != r5) goto L_0x0bc4;
    L_0x0bc1:
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0bc9;
    L_0x0bc4:
        r4 = r0.transitionProgress;
        r4 = r4 * r17;
        r6 = (int) r4;
    L_0x0bc9:
        r1.setAlpha(r6);
        r4 = 2;
        r2 = r2 / r4;
        r9 = r27 - r2;
        r3 = r3 / r4;
        r10 = r24 - r3;
        r2 = r27 + r2;
        r3 = r24 + r3;
        r1.setBounds(r9, r10, r2, r3);
        r1.draw(r7);
    L_0x0bdd:
        r1 = java.lang.System.currentTimeMillis();
        r3 = r0.lastAnimationTime;
        r3 = r1 - r3;
        r5 = 17;
        r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r8 <= 0) goto L_0x0bed;
    L_0x0beb:
        r3 = 17;
    L_0x0bed:
        r0.lastAnimationTime = r1;
        r1 = r0.currentIcon;
        r2 = 3;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0bf4:
        r2 = 14;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0bf8:
        r5 = 4;
        if (r1 != r5) goto L_0x0bff;
    L_0x0bfb:
        r1 = r0.nextIcon;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0bff:
        r1 = r0.currentIcon;
        r2 = 10;
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = 13;
        if (r1 != r2) goto L_0x0CLASSNAME;
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
        if (r1 == r2) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.downloadProgress;
        r2 = r0.downloadProgressAnimationStart;
        r5 = r1 - r2;
        r6 = 0;
        r8 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r8 <= 0) goto L_0x0CLASSNAME;
    L_0x0c2f:
        r6 = r0.downloadProgressTime;
        r8 = (float) r3;
        r6 = r6 + r8;
        r0.downloadProgressTime = r6;
        r6 = r0.downloadProgressTime;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x0CLASSNAME;
    L_0x0c3d:
        r0.animatedDownloadProgress = r1;
        r0.downloadProgressAnimationStart = r1;
        r1 = 0;
        r0.downloadProgressTime = r1;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.interpolator;
        r8 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r6 = r6 / r8;
        r1 = r1.getInterpolation(r6);
        r5 = r5 * r1;
        r2 = r2 + r5;
        r0.animatedDownloadProgress = r2;
    L_0x0CLASSNAME:
        r34.invalidateSelf();
    L_0x0CLASSNAME:
        r1 = r0.animatingTransition;
        if (r1 == 0) goto L_0x0c7b;
    L_0x0c5a:
        r1 = r0.transitionProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x0c7b;
    L_0x0CLASSNAME:
        r3 = (float) r3;
        r4 = r0.transitionAnimationTime;
        r3 = r3 / r4;
        r1 = r1 + r3;
        r0.transitionProgress = r1;
        r1 = r0.transitionProgress;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x0CLASSNAME;
    L_0x0c6f:
        r1 = r0.nextIcon;
        r0.currentIcon = r1;
        r0.transitionProgress = r2;
        r1 = 0;
        r0.animatingTransition = r1;
    L_0x0CLASSNAME:
        r34.invalidateSelf();
    L_0x0c7b:
        r1 = r0.nextIcon;
        r2 = 4;
        if (r1 == r2) goto L_0x0c8b;
    L_0x0CLASSNAME:
        r3 = 6;
        if (r1 == r3) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3 = 10;
        if (r1 != r3) goto L_0x0c8e;
    L_0x0CLASSNAME:
        r1 = r0.currentIcon;
        if (r1 != r2) goto L_0x0c8e;
    L_0x0c8b:
        r35.restore();
    L_0x0c8e:
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
