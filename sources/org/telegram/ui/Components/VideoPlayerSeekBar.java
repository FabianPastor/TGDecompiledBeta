package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;

public class VideoPlayerSeekBar {
    private static Paint paint;
    private static Paint strokePaint;
    private static int thumbWidth;
    private static Path tmpPath;
    private static float[] tmpRadii;
    private final float TIMESTAMP_GAP = 1.0f;
    private float animateFromBufferedProgress;
    private boolean animateResetBuffering;
    private AnimatedFloat animateThumbLoopBackProgress;
    private float animateThumbProgress = 1.0f;
    private float animatedThumbX = 0.0f;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedAnimationValue = 1.0f;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private float currentRadius;
    private int currentTimestamp = -1;
    private SeekBarDelegate delegate;
    private int draggingThumbX = 0;
    private int fromThumbX = 0;
    private int height;
    private int horizontalPadding;
    private CharSequence lastCaption;
    private int lastTimestamp = -1;
    private long lastTimestampUpdate;
    private long lastTimestampsAppearingUpdate;
    private long lastUpdateTime;
    private long lastVideoDuration;
    private float lastWidth = -1.0f;
    private int lineHeight = AndroidUtilities.dp(4.0f);
    private float loopBackWasThumbX;
    private View parentView;
    private boolean pressed = false;
    private boolean pressedDelayed = false;
    private float progress;
    private int progressColor;
    private RectF rect = new RectF();
    private boolean selected;
    private int smallLineColor;
    private int smallLineHeight = AndroidUtilities.dp(2.0f);
    private int thumbDX = 0;
    private int thumbX = 0;
    private int timestampChangeDirection;
    private float timestampChangeT = 1.0f;
    private StaticLayout[] timestampLabel;
    private TextPaint timestampLabelPaint;
    private ArrayList<Pair<Float, CharSequence>> timestamps;
    private float timestampsAppearing = 0.0f;
    private float transitionProgress;
    private int width;

    public interface SeekBarDelegate {
        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);

        /* renamed from: org.telegram.ui.Components.VideoPlayerSeekBar$SeekBarDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate _this, float progress) {
            }
        }
    }

    public VideoPlayerSeekBar(View parent) {
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            strokePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(-16777216);
            strokePaint.setStrokeWidth(1.0f);
        }
        this.parentView = parent;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
        this.animateThumbLoopBackProgress = new AnimatedFloat(0.0f, parent, 0, 300, CubicBezierInterpolator.EASE_OUT_QUINT);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            if (this.transitionProgress > 0.0f) {
                return false;
            }
            int i = this.height;
            int i2 = thumbWidth;
            int additionWidth = (i - i2) / 2;
            if (x >= ((float) (-additionWidth))) {
                int i3 = this.width;
                if (x <= ((float) (i3 + additionWidth)) && y >= 0.0f && y <= ((float) i)) {
                    int i4 = this.thumbX;
                    if (((float) (i4 - additionWidth)) > x || x > ((float) (i4 + i2 + additionWidth))) {
                        int i5 = ((int) x) - (i2 / 2);
                        this.thumbX = i5;
                        if (i5 < 0) {
                            this.thumbX = 0;
                        } else if (i5 > i3 - i2) {
                            this.thumbX = i2 - i3;
                        }
                        this.animatedThumbX = (float) this.thumbX;
                    }
                    this.pressedDelayed = true;
                    this.pressed = true;
                    int i6 = this.thumbX;
                    this.draggingThumbX = i6;
                    this.thumbDX = (int) (x - ((float) i6));
                    return true;
                }
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                int i7 = this.draggingThumbX;
                this.thumbX = i7;
                this.animatedThumbX = (float) i7;
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) i7) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                AndroidUtilities.runOnUIThread(new VideoPlayerSeekBar$$ExternalSyntheticLambda0(this), 50);
                return true;
            }
        } else if (action == 2 && this.pressed) {
            int i8 = (int) (x - ((float) this.thumbDX));
            this.draggingThumbX = i8;
            if (i8 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i9 = this.width;
                int i10 = thumbWidth;
                if (i8 > i9 - i10) {
                    this.draggingThumbX = i9 - i10;
                }
            }
            SeekBarDelegate seekBarDelegate2 = this.delegate;
            if (seekBarDelegate2 != null) {
                seekBarDelegate2.onSeekBarContinuousDrag(((float) this.draggingThumbX) / ((float) (this.width - thumbWidth)));
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$onTouch$0$org-telegram-ui-Components-VideoPlayerSeekBar  reason: not valid java name */
    public /* synthetic */ void m1551lambda$onTouch$0$orgtelegramuiComponentsVideoPlayerSeekBar() {
        this.pressedDelayed = false;
    }

    public void setColors(int background, int cache, int progress2, int circle, int selected2, int smallLineColor2) {
        this.backgroundColor = background;
        this.cacheColor = cache;
        this.circleColor = circle;
        this.progressColor = progress2;
        this.backgroundSelectedColor = selected2;
        this.smallLineColor = smallLineColor2;
    }

    public void setProgress(float progress2, boolean animated) {
        if (Math.abs(this.progress - 1.0f) < 0.04f && Math.abs(progress2) < 0.04f) {
            this.animateThumbLoopBackProgress.set(1.0f, true);
            this.loopBackWasThumbX = (float) this.thumbX;
        }
        this.progress = progress2;
        int newThumb = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * progress2));
        if (animated) {
            if (Math.abs(newThumb - this.thumbX) > AndroidUtilities.dp(10.0f)) {
                float progressInterpolated = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                this.fromThumbX = (int) ((((float) this.thumbX) * progressInterpolated) + (((float) this.fromThumbX) * (1.0f - progressInterpolated)));
                this.animateThumbProgress = 0.0f;
            } else if (this.animateThumbProgress == 1.0f) {
                this.animateThumbProgress = 0.0f;
                this.fromThumbX = this.thumbX;
            }
        }
        this.thumbX = newThumb;
        if (newThumb < 0) {
            this.thumbX = 0;
        } else {
            int i = this.width;
            int i2 = thumbWidth;
            if (newThumb > i - i2) {
                this.thumbX = i - i2;
            }
        }
        if (Math.abs(this.animatedThumbX - ((float) this.thumbX)) > ((float) AndroidUtilities.dp(8.0f))) {
            this.animatedThumbX = (float) this.thumbX;
        }
    }

    public void setProgress(float progress2) {
        setProgress(progress2, false);
    }

    public void setBufferedProgress(float value) {
        float f = this.bufferedProgress;
        if (value != f) {
            this.animateFromBufferedProgress = f;
            this.animateResetBuffering = value < f;
            this.bufferedProgress = value;
            this.bufferedAnimationValue = 0.0f;
        }
    }

    public float getProgress() {
        return ((float) this.thumbX) / ((float) (this.width - thumbWidth));
    }

    public int getThumbX() {
        return (this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2);
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public float getTransitionProgress() {
        return this.transitionProgress;
    }

    public void setTransitionProgress(float transitionProgress2) {
        if (this.transitionProgress != transitionProgress2) {
            this.transitionProgress = transitionProgress2;
            this.parentView.invalidate();
        }
    }

    public int getHorizontalPadding() {
        return this.horizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding2) {
        this.horizontalPadding = horizontalPadding2;
    }

    public void updateTimestamps(MessageObject messageObject, long videoDuration) {
        Integer seconds;
        MessageObject messageObject2 = messageObject;
        long j = videoDuration;
        boolean z = false;
        if (messageObject2 == null || j < 0) {
            this.timestamps = null;
            this.currentTimestamp = -1;
            this.timestampsAppearing = 0.0f;
            StaticLayout[] staticLayoutArr = this.timestampLabel;
            if (staticLayoutArr != null) {
                staticLayoutArr[1] = null;
                staticLayoutArr[0] = null;
            }
            this.lastCaption = null;
            this.lastVideoDuration = -1;
            return;
        }
        CharSequence text = messageObject2.caption;
        if (text != this.lastCaption || this.lastVideoDuration != j) {
            this.lastCaption = text;
            this.lastVideoDuration = j;
            if (!(text instanceof Spanned)) {
                this.timestamps = null;
                this.currentTimestamp = -1;
                this.timestampsAppearing = 0.0f;
                StaticLayout[] staticLayoutArr2 = this.timestampLabel;
                if (staticLayoutArr2 != null) {
                    staticLayoutArr2[1] = null;
                    staticLayoutArr2[0] = null;
                    return;
                }
                return;
            }
            Spanned spanned = (Spanned) text;
            try {
                URLSpanNoUnderline[] links = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
                this.timestamps = new ArrayList<>();
                this.timestampsAppearing = 0.0f;
                if (this.timestampLabelPaint == null) {
                    TextPaint textPaint = new TextPaint(1);
                    this.timestampLabelPaint = textPaint;
                    textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
                    this.timestampLabelPaint.setColor(-1);
                }
                int i = 0;
                while (i < links.length) {
                    URLSpanNoUnderline link = links[i];
                    if (link != null && link.getURL().startsWith("video?") && (seconds = Utilities.parseInt((CharSequence) link.getURL().substring(6))) != null && seconds.intValue() >= 0) {
                        float position = ((float) (((long) seconds.intValue()) * 1000)) / ((float) j);
                        SpannableStringBuilder builder = new SpannableStringBuilder(link.label);
                        Emoji.replaceEmoji(builder, this.timestampLabelPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), z);
                        this.timestamps.add(new Pair(Float.valueOf(position), builder));
                    }
                    i++;
                    z = false;
                }
                Collections.sort(this.timestamps, VideoPlayerSeekBar$$ExternalSyntheticLambda1.INSTANCE);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                this.timestamps = null;
                this.currentTimestamp = -1;
                this.timestampsAppearing = 0.0f;
                StaticLayout[] staticLayoutArr3 = this.timestampLabel;
                if (staticLayoutArr3 != null) {
                    staticLayoutArr3[1] = null;
                    staticLayoutArr3[0] = null;
                }
            }
        }
    }

    static /* synthetic */ int lambda$updateTimestamps$1(Pair a, Pair b) {
        if (((Float) a.first).floatValue() > ((Float) b.first).floatValue()) {
            return 1;
        }
        if (((Float) b.first).floatValue() > ((Float) a.first).floatValue()) {
            return -1;
        }
        return 0;
    }

    public void draw(Canvas canvas, View view) {
        Canvas canvas2 = canvas;
        this.rect.left = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) thumbWidth) / 2.0f, 0.0f, this.transitionProgress);
        RectF rectF = this.rect;
        int i = this.height;
        rectF.top = AndroidUtilities.lerp(((float) (i - this.lineHeight)) / 2.0f, (float) ((i - AndroidUtilities.dp(3.0f)) - this.smallLineHeight), this.transitionProgress);
        RectF rectF2 = this.rect;
        int i2 = this.height;
        rectF2.bottom = AndroidUtilities.lerp(((float) (this.lineHeight + i2)) / 2.0f, (float) (i2 - AndroidUtilities.dp(3.0f)), this.transitionProgress);
        float thumbX2 = (float) this.thumbX;
        float min = Math.min(this.animatedThumbX, thumbX2);
        this.animatedThumbX = min;
        float lerp = AndroidUtilities.lerp(min, thumbX2, 0.5f);
        this.animatedThumbX = lerp;
        if (Math.abs(thumbX2 - lerp) > 0.005f) {
            this.parentView.invalidate();
        }
        float thumbX3 = this.animatedThumbX;
        float currentThumbX = thumbX3;
        float f = this.animateThumbProgress;
        if (f != 1.0f) {
            float f2 = f + 0.07272727f;
            this.animateThumbProgress = f2;
            if (f2 >= 1.0f) {
                this.animateThumbProgress = 1.0f;
            } else {
                view.invalidate();
                float progressInterpolated = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                currentThumbX = (((float) this.fromThumbX) * (1.0f - progressInterpolated)) + (thumbX3 * progressInterpolated);
            }
        }
        float loopBack = this.animateThumbLoopBackProgress.set(0.0f);
        if (this.pressed) {
            loopBack = 0.0f;
        }
        this.rect.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) this.width) - (((float) thumbWidth) / 2.0f), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
        setPaintColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor, 1.0f - this.transitionProgress);
        drawProgressBar(canvas2, this.rect, paint);
        float f3 = this.bufferedAnimationValue;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.16f;
            this.bufferedAnimationValue = f4;
            if (f4 > 1.0f) {
                this.bufferedAnimationValue = 1.0f;
            } else {
                this.parentView.invalidate();
            }
        }
        if (this.animateResetBuffering) {
            float f5 = this.animateFromBufferedProgress;
            if (f5 > 0.0f) {
                RectF rectF3 = this.rect;
                int i3 = thumbWidth;
                rectF3.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i3) / 2.0f) + (f5 * ((float) (this.width - i3))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, (1.0f - this.transitionProgress) * (1.0f - this.bufferedAnimationValue));
                drawProgressBar(canvas2, this.rect, paint);
            }
            float f6 = this.bufferedProgress;
            if (f6 > 0.0f) {
                RectF rectF4 = this.rect;
                int i4 = thumbWidth;
                rectF4.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i4) / 2.0f) + (f6 * ((float) (this.width - i4))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                drawProgressBar(canvas2, this.rect, paint);
            }
        } else {
            float f7 = this.animateFromBufferedProgress;
            float f8 = this.bufferedAnimationValue;
            float currentBufferedProgress = (f7 * (1.0f - f8)) + (this.bufferedProgress * f8);
            if (currentBufferedProgress > 0.0f) {
                RectF rectF5 = this.rect;
                int i5 = thumbWidth;
                rectF5.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i5) / 2.0f) + (((float) (this.width - i5)) * currentBufferedProgress), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                drawProgressBar(canvas2, this.rect, paint);
            }
        }
        int newRad = AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != ((float) newRad)) {
            long newUpdateTime = SystemClock.elapsedRealtime();
            long dt = newUpdateTime - this.lastUpdateTime;
            this.lastUpdateTime = newUpdateTime;
            if (dt > 18) {
                dt = 16;
            }
            float f9 = this.currentRadius;
            if (f9 < ((float) newRad)) {
                float dp = f9 + (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            } else {
                float dp2 = f9 - (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            }
            View view2 = this.parentView;
            if (view2 != null) {
                view2.invalidate();
            }
        }
        float circleRadius = AndroidUtilities.lerp(this.currentRadius, 0.0f, this.transitionProgress);
        if (loopBack > 0.0f) {
            float wasLeft = this.rect.left;
            RectF rectF6 = this.rect;
            int i6 = thumbWidth;
            rectF6.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i6) / 2.0f) + ((float) (this.width - i6)), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
            RectF rectF7 = this.rect;
            rectF7.left = AndroidUtilities.lerp(wasLeft, rectF7.right, 1.0f - loopBack);
            if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
                strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
                drawProgressBar(canvas2, this.rect, strokePaint);
            }
            setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
            drawProgressBar(canvas2, this.rect, paint);
            this.rect.left = wasLeft;
            setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
            canvas2.drawCircle(((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) thumbWidth) / 2.0f) + this.loopBackWasThumbX, (((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f)) * (this.loopBackWasThumbX / ((float) (this.width - thumbWidth))), this.transitionProgress), this.rect.centerY(), circleRadius * loopBack, paint);
        }
        this.rect.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) thumbWidth) / 2.0f) + (this.pressed ? (float) this.draggingThumbX : currentThumbX), (((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f)) * getProgress(), this.transitionProgress);
        if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
            strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
            drawProgressBar(canvas2, this.rect, strokePaint);
        }
        setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
        drawProgressBar(canvas2, this.rect, paint);
        setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
        canvas2.drawCircle(this.rect.right, this.rect.centerY(), (1.0f - loopBack) * circleRadius, paint);
        drawTimestampLabel(canvas);
    }

    /* JADX WARNING: Removed duplicated region for block: B:78:0x0218 A[EDGE_INSN: B:78:0x0218->B:72:0x0218 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0203 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void drawProgressBar(android.graphics.Canvas r27, android.graphics.RectF r28, android.graphics.Paint r29) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            float r4 = r0.transitionProgress
            r5 = 2
            r6 = 1
            int r4 = org.telegram.messenger.AndroidUtilities.lerp((int) r5, (int) r6, (float) r4)
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r7 = r0.timestamps
            if (r7 == 0) goto L_0x021e
            boolean r7 = r7.isEmpty()
            if (r7 == 0) goto L_0x0022
            goto L_0x021e
        L_0x0022:
            float r7 = r2.bottom
            float r8 = r2.top
            float r7 = r7 - r8
            int r8 = r0.horizontalPadding
            float r8 = (float) r8
            int r9 = thumbWidth
            float r9 = (float) r9
            r10 = 1073741824(0x40000000, float:2.0)
            float r9 = r9 / r10
            float r11 = r0.transitionProgress
            r12 = 0
            float r9 = org.telegram.messenger.AndroidUtilities.lerp((float) r9, (float) r12, (float) r11)
            float r8 = r8 + r9
            int r9 = r0.horizontalPadding
            float r9 = (float) r9
            int r11 = r0.width
            float r11 = (float) r11
            int r13 = thumbWidth
            float r13 = (float) r13
            float r13 = r13 / r10
            float r11 = r11 - r13
            android.view.View r13 = r0.parentView
            int r13 = r13.getWidth()
            float r13 = (float) r13
            int r14 = r0.horizontalPadding
            float r14 = (float) r14
            float r14 = r14 * r10
            float r13 = r13 - r14
            float r14 = r0.transitionProgress
            float r11 = org.telegram.messenger.AndroidUtilities.lerp((float) r11, (float) r13, (float) r14)
            float r9 = r9 + r11
            android.graphics.RectF r11 = org.telegram.messenger.AndroidUtilities.rectTmp
            r11.set(r2)
            float r11 = r0.timestampsAppearing
            r13 = 1065353216(0x3var_, float:1.0)
            float r11 = r11 * r13
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r11 = r11 / r10
            android.graphics.Path r10 = tmpPath
            if (r10 != 0) goto L_0x0073
            android.graphics.Path r10 = new android.graphics.Path
            r10.<init>()
            tmpPath = r10
        L_0x0073:
            android.graphics.Path r10 = tmpPath
            r10.reset()
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r14 = r9 - r8
            float r10 = r10 / r14
            r14 = -1
            r15 = -1
            r16 = 0
            r12 = r16
        L_0x0088:
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r5 = r0.timestamps
            int r5 = r5.size()
            if (r12 >= r5) goto L_0x00aa
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r5 = r0.timestamps
            java.lang.Object r5 = r5.get(r12)
            android.util.Pair r5 = (android.util.Pair) r5
            java.lang.Object r5 = r5.first
            java.lang.Float r5 = (java.lang.Float) r5
            float r5 = r5.floatValue()
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 < 0) goto L_0x00a6
            r14 = r12
            goto L_0x00aa
        L_0x00a6:
            int r12 = r12 + 1
            r5 = 2
            goto L_0x0088
        L_0x00aa:
            if (r14 >= 0) goto L_0x00ad
            r14 = 0
        L_0x00ad:
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r5 = r0.timestamps
            int r5 = r5.size()
            int r5 = r5 - r6
        L_0x00b4:
            if (r5 < 0) goto L_0x00d2
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r12 = r0.timestamps
            java.lang.Object r12 = r12.get(r5)
            android.util.Pair r12 = (android.util.Pair) r12
            java.lang.Object r12 = r12.first
            java.lang.Float r12 = (java.lang.Float) r12
            float r12 = r12.floatValue()
            float r12 = r13 - r12
            int r12 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r12 < 0) goto L_0x00cf
            int r15 = r5 + 1
            goto L_0x00d2
        L_0x00cf:
            int r5 = r5 + -1
            goto L_0x00b4
        L_0x00d2:
            if (r15 >= 0) goto L_0x00da
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r5 = r0.timestamps
            int r15 = r5.size()
        L_0x00da:
            r5 = 1
            r12 = r14
        L_0x00dc:
            if (r12 > r15) goto L_0x0212
            if (r12 != r14) goto L_0x00e2
            r6 = 0
            goto L_0x00f4
        L_0x00e2:
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r13 = r0.timestamps
            int r6 = r12 + -1
            java.lang.Object r6 = r13.get(r6)
            android.util.Pair r6 = (android.util.Pair) r6
            java.lang.Object r6 = r6.first
            java.lang.Float r6 = (java.lang.Float) r6
            float r6 = r6.floatValue()
        L_0x00f4:
            if (r12 != r15) goto L_0x00f9
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0109
        L_0x00f9:
            java.util.ArrayList<android.util.Pair<java.lang.Float, java.lang.CharSequence>> r13 = r0.timestamps
            java.lang.Object r13 = r13.get(r12)
            android.util.Pair r13 = (android.util.Pair) r13
            java.lang.Object r13 = r13.first
            java.lang.Float r13 = (java.lang.Float) r13
            float r13 = r13.floatValue()
        L_0x0109:
            r17 = r5
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r18 = org.telegram.messenger.AndroidUtilities.lerp((float) r8, (float) r9, (float) r6)
            if (r12 <= 0) goto L_0x0116
            r19 = r11
            goto L_0x0118
        L_0x0116:
            r19 = 0
        L_0x0118:
            r20 = r6
            float r6 = r18 + r19
            r5.left = r6
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r6 = org.telegram.messenger.AndroidUtilities.lerp((float) r8, (float) r9, (float) r13)
            if (r12 >= r15) goto L_0x0129
            r18 = r11
            goto L_0x012b
        L_0x0129:
            r18 = 0
        L_0x012b:
            float r6 = r6 - r18
            r5.right = r6
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r5 = r5.right
            float r6 = r2.right
            r18 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x013d
            r5 = 1
            goto L_0x013e
        L_0x013d:
            r5 = 0
        L_0x013e:
            r6 = r5
            if (r5 == 0) goto L_0x014a
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            r19 = r7
            float r7 = r2.right
            r5.right = r7
            goto L_0x014c
        L_0x014a:
            r19 = r7
        L_0x014c:
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r5 = r5.right
            float r7 = r2.left
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L_0x015a
            r25 = r8
            goto L_0x0203
        L_0x015a:
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r5 = r5.left
            float r7 = r2.left
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L_0x016a
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r7 = r2.left
            r5.left = r7
        L_0x016a:
            float[] r5 = tmpRadii
            if (r5 != 0) goto L_0x0174
            r5 = 8
            float[] r5 = new float[r5]
            tmpRadii = r5
        L_0x0174:
            r21 = 4
            r22 = 3
            r23 = 7
            r24 = 6
            if (r12 == r14) goto L_0x01d5
            if (r6 == 0) goto L_0x018e
            android.graphics.RectF r7 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r7 = r7.left
            float r5 = r2.left
            int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x018e
            r25 = r8
            r7 = 1
            goto L_0x01d8
        L_0x018e:
            if (r12 < r15) goto L_0x01b2
            float[] r5 = tmpRadii
            r7 = 1060320051(0x3var_, float:0.7)
            float r7 = r7 * r4
            r25 = r8
            float r8 = r0.timestampsAppearing
            float r7 = r7 * r8
            r5[r23] = r7
            r5[r24] = r7
            r8 = 1
            r5[r8] = r7
            r5[r18] = r7
            r7 = 5
            r5[r7] = r4
            r5[r21] = r4
            r5[r22] = r4
            r8 = 2
            r5[r8] = r4
            r7 = 2
            goto L_0x01f5
        L_0x01b2:
            r25 = r8
            r7 = 5
            r8 = 2
            float[] r5 = tmpRadii
            r16 = 1060320051(0x3var_, float:0.7)
            float r16 = r16 * r4
            float r8 = r0.timestampsAppearing
            float r8 = r8 * r16
            r5[r7] = r8
            r5[r21] = r8
            r5[r22] = r8
            r7 = 2
            r5[r7] = r8
            r5[r23] = r8
            r5[r24] = r8
            r7 = 1
            r5[r7] = r8
            r5[r18] = r8
            r7 = 2
            goto L_0x01f5
        L_0x01d5:
            r25 = r8
            r7 = 1
        L_0x01d8:
            float[] r5 = tmpRadii
            r5[r23] = r4
            r5[r24] = r4
            r5[r7] = r4
            r5[r18] = r4
            r8 = 1060320051(0x3var_, float:0.7)
            float r8 = r8 * r4
            float r7 = r0.timestampsAppearing
            float r8 = r8 * r7
            r7 = 5
            r5[r7] = r8
            r5[r21] = r8
            r5[r22] = r8
            r7 = 2
            r5[r7] = r8
        L_0x01f5:
            android.graphics.Path r5 = tmpPath
            android.graphics.RectF r8 = org.telegram.messenger.AndroidUtilities.rectTmp
            float[] r7 = tmpRadii
            android.graphics.Path$Direction r0 = android.graphics.Path.Direction.CW
            r5.addRoundRect(r8, r7, r0)
            if (r6 == 0) goto L_0x0203
            goto L_0x0218
        L_0x0203:
            int r12 = r12 + 1
            r0 = r26
            r5 = r17
            r7 = r19
            r8 = r25
            r6 = 1
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x00dc
        L_0x0212:
            r17 = r5
            r19 = r7
            r25 = r8
        L_0x0218:
            android.graphics.Path r0 = tmpPath
            r1.drawPath(r0, r3)
            goto L_0x0221
        L_0x021e:
            r1.drawRoundRect(r2, r4, r4, r3)
        L_0x0221:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayerSeekBar.drawProgressBar(android.graphics.Canvas, android.graphics.RectF, android.graphics.Paint):void");
    }

    private void drawTimestampLabel(Canvas canvas) {
        float right;
        float f;
        float f2;
        Canvas canvas2 = canvas;
        ArrayList<Pair<Float, CharSequence>> arrayList = this.timestamps;
        if (arrayList != null && !arrayList.isEmpty()) {
            float progress2 = ((this.pressed || this.pressedDelayed) ? (float) this.draggingThumbX : this.animatedThumbX) / ((float) (this.width - thumbWidth));
            int i = this.timestamps.size() - 1;
            while (true) {
                if (i < 0) {
                    i = -1;
                    break;
                } else if (((Float) this.timestamps.get(i).first).floatValue() - 0.001f <= progress2) {
                    int timestampIndex = i;
                    break;
                } else {
                    i--;
                }
            }
            if (this.timestampLabel == null) {
                this.timestampLabel = new StaticLayout[2];
            }
            float left = AndroidUtilities.lerp(((float) thumbWidth) / 2.0f, 0.0f, this.transitionProgress) + ((float) this.horizontalPadding);
            float right2 = AndroidUtilities.lerp(((float) this.width) - (((float) thumbWidth) / 2.0f), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress) + ((float) this.horizontalPadding);
            float rightPadded = (((float) this.width) - (((float) thumbWidth) / 2.0f)) + ((float) this.horizontalPadding);
            float width2 = Math.abs(left - rightPadded) - ((float) AndroidUtilities.dp(16.0f));
            float f3 = this.lastWidth;
            if (f3 > 0.0f && Math.abs(f3 - width2) > 0.01f) {
                StaticLayout[] staticLayoutArr = this.timestampLabel;
                if (staticLayoutArr[0] != null) {
                    staticLayoutArr[0] = makeStaticLayout(staticLayoutArr[0].getText(), (int) width2);
                }
                StaticLayout[] staticLayoutArr2 = this.timestampLabel;
                if (staticLayoutArr2[1] != null) {
                    staticLayoutArr2[1] = makeStaticLayout(staticLayoutArr2[1].getText(), (int) width2);
                }
            }
            this.lastWidth = width2;
            if (i != this.currentTimestamp) {
                StaticLayout[] staticLayoutArr3 = this.timestampLabel;
                staticLayoutArr3[1] = staticLayoutArr3[0];
                if (this.pressed) {
                    try {
                        this.parentView.performHapticFeedback(9, 1);
                    } catch (Exception e) {
                    }
                }
                if (i < 0 || i >= this.timestamps.size()) {
                    this.timestampLabel[0] = null;
                } else {
                    CharSequence label = (CharSequence) this.timestamps.get(i).second;
                    if (label == null) {
                        this.timestampLabel[0] = null;
                    } else {
                        this.timestampLabel[0] = makeStaticLayout(label, (int) width2);
                    }
                }
                this.timestampChangeT = 0.0f;
                if (i == -1) {
                    this.timestampChangeDirection = -1;
                } else {
                    int i2 = this.currentTimestamp;
                    if (i2 == -1) {
                        this.timestampChangeDirection = 1;
                    } else if (i < i2) {
                        this.timestampChangeDirection = -1;
                    } else if (i > i2) {
                        this.timestampChangeDirection = 1;
                    }
                }
                this.lastTimestamp = this.currentTimestamp;
                this.currentTimestamp = i;
            }
            if (this.timestampChangeT < 1.0f) {
                float f4 = width2;
                this.timestampChangeT = Math.min(this.timestampChangeT + (((float) Math.min(17, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate))) / (this.timestamps.size() > 8 ? 160.0f : 220.0f)), 1.0f);
                this.parentView.invalidate();
                right = right2;
                this.lastTimestampUpdate = SystemClock.elapsedRealtime();
            } else {
                right = right2;
                float f5 = width2;
            }
            if (this.timestampsAppearing < 1.0f) {
                this.timestampsAppearing = Math.min(this.timestampsAppearing + (((float) Math.min(17, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate))) / 200.0f), 1.0f);
                this.parentView.invalidate();
                this.lastTimestampsAppearingUpdate = SystemClock.elapsedRealtime();
            }
            float changeT = CubicBezierInterpolator.DEFAULT.getInterpolation(this.timestampChangeT);
            canvas.save();
            int i3 = this.height;
            canvas2.translate(((right - rightPadded) * this.transitionProgress) + left, ((float) AndroidUtilities.dp(12.0f)) + AndroidUtilities.lerp(((float) (this.lineHeight + i3)) / 2.0f, (float) (i3 - AndroidUtilities.dp(3.0f)), this.transitionProgress));
            if (this.timestampLabel[1] != null) {
                canvas.save();
                if (this.timestampChangeDirection != 0) {
                    f2 = 0.0f;
                    canvas2.translate(((float) AndroidUtilities.dp(8.0f)) + (((float) (AndroidUtilities.dp(16.0f) * (-this.timestampChangeDirection))) * changeT), 0.0f);
                } else {
                    f2 = 0.0f;
                }
                canvas2.translate(f2, ((float) (-this.timestampLabel[1].getHeight())) / 2.0f);
                this.timestampLabelPaint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f * (1.0f - changeT) * this.timestampsAppearing));
                this.timestampLabel[1].draw(canvas2);
                canvas.restore();
            }
            if (this.timestampLabel[0] != null) {
                canvas.save();
                if (this.timestampChangeDirection != 0) {
                    f = 0.0f;
                    canvas2.translate(((float) AndroidUtilities.dp(8.0f)) + (((float) (AndroidUtilities.dp(16.0f) * this.timestampChangeDirection)) * (1.0f - changeT)), 0.0f);
                } else {
                    f = 0.0f;
                }
                canvas2.translate(f, ((float) (-this.timestampLabel[0].getHeight())) / 2.0f);
                this.timestampLabelPaint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f * changeT * this.timestampsAppearing));
                this.timestampLabel[0].draw(canvas2);
                canvas.restore();
            }
            canvas.restore();
        }
    }

    private StaticLayout makeStaticLayout(CharSequence text, int width2) {
        if (this.timestampLabelPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            this.timestampLabelPaint = textPaint;
            textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.timestampLabelPaint.setColor(-1);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            return StaticLayout.Builder.obtain(text, 0, text.length(), this.timestampLabelPaint, width2).setMaxLines(1).setAlignment(Layout.Alignment.ALIGN_CENTER).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(Math.min(AndroidUtilities.dp(400.0f), width2)).build();
        }
        int length = text.length();
        return new StaticLayout(text, 0, length, this.timestampLabelPaint, width2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, Math.min(AndroidUtilities.dp(400.0f), width2));
    }

    private void setPaintColor(int color, float alpha) {
        if (alpha < 1.0f) {
            color = ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * alpha));
        }
        paint.setColor(color);
    }
}
