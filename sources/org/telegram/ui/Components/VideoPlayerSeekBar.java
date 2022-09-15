package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableString;
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
    private long lastTimestampUpdate;
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
    }

    public VideoPlayerSeekBar(View view) {
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            strokePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(-16777216);
            strokePaint.setStrokeWidth(1.0f);
        }
        this.parentView = view;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
        this.animateThumbLoopBackProgress = new AnimatedFloat(0.0f, view, 0, 300, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int i, float f, float f2) {
        SeekBarDelegate seekBarDelegate;
        if (i == 0) {
            if (this.transitionProgress > 0.0f) {
                return false;
            }
            int i2 = this.height;
            int i3 = thumbWidth;
            int i4 = (i2 - i3) / 2;
            if (f >= ((float) (-i4))) {
                int i5 = this.width;
                if (f <= ((float) (i5 + i4)) && f2 >= 0.0f && f2 <= ((float) i2)) {
                    int i6 = this.thumbX;
                    if (((float) (i6 - i4)) > f || f > ((float) (i6 + i3 + i4))) {
                        int i7 = ((int) f) - (i3 / 2);
                        this.thumbX = i7;
                        if (i7 < 0) {
                            this.thumbX = 0;
                        } else if (i7 > i5 - i3) {
                            this.thumbX = i3 - i5;
                        }
                        this.animatedThumbX = (float) this.thumbX;
                    }
                    this.pressedDelayed = true;
                    this.pressed = true;
                    int i8 = this.thumbX;
                    this.draggingThumbX = i8;
                    this.thumbDX = (int) (f - ((float) i8));
                    return true;
                }
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                int i9 = this.draggingThumbX;
                this.thumbX = i9;
                this.animatedThumbX = (float) i9;
                if (i == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) i9) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                AndroidUtilities.runOnUIThread(new VideoPlayerSeekBar$$ExternalSyntheticLambda0(this), 50);
                return true;
            }
        } else if (i == 2 && this.pressed) {
            int i10 = (int) (f - ((float) this.thumbDX));
            this.draggingThumbX = i10;
            if (i10 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i11 = this.width;
                int i12 = thumbWidth;
                if (i10 > i11 - i12) {
                    this.draggingThumbX = i11 - i12;
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTouch$0() {
        this.pressedDelayed = false;
    }

    public void setColors(int i, int i2, int i3, int i4, int i5, int i6) {
        this.backgroundColor = i;
        this.cacheColor = i2;
        this.circleColor = i4;
        this.progressColor = i3;
        this.backgroundSelectedColor = i5;
        this.smallLineColor = i6;
    }

    public void setProgress(float f, boolean z) {
        if (Math.abs(this.progress - 1.0f) < 0.04f && Math.abs(f) < 0.04f) {
            this.animateThumbLoopBackProgress.set(1.0f, true);
            this.loopBackWasThumbX = (float) this.thumbX;
        }
        this.progress = f;
        int ceil = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * f));
        if (z) {
            if (Math.abs(ceil - this.thumbX) > AndroidUtilities.dp(10.0f)) {
                float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                this.fromThumbX = (int) ((((float) this.thumbX) * interpolation) + (((float) this.fromThumbX) * (1.0f - interpolation)));
                this.animateThumbProgress = 0.0f;
            } else if (this.animateThumbProgress == 1.0f) {
                this.animateThumbProgress = 0.0f;
                this.fromThumbX = this.thumbX;
            }
        }
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
        } else {
            int i = this.width;
            int i2 = thumbWidth;
            if (ceil > i - i2) {
                this.thumbX = i - i2;
            }
        }
        if (Math.abs(this.animatedThumbX - ((float) this.thumbX)) > ((float) AndroidUtilities.dp(8.0f))) {
            this.animatedThumbX = (float) this.thumbX;
        }
    }

    public void setProgress(float f) {
        setProgress(f, false);
    }

    public void setBufferedProgress(float f) {
        float f2 = this.bufferedProgress;
        if (f != f2) {
            this.animateFromBufferedProgress = f2;
            this.animateResetBuffering = f < f2;
            this.bufferedProgress = f;
            this.bufferedAnimationValue = 0.0f;
        }
    }

    public float getProgress() {
        return ((float) this.thumbX) / ((float) (this.width - thumbWidth));
    }

    public int getThumbX() {
        return (this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2);
    }

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public void setTransitionProgress(float f) {
        if (this.transitionProgress != f) {
            this.transitionProgress = f;
            this.parentView.invalidate();
        }
    }

    public void setHorizontalPadding(int i) {
        this.horizontalPadding = i;
    }

    public void updateTimestamps(MessageObject messageObject, long j) {
        Integer parseInt;
        if (messageObject == null || j < 0) {
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
        CharSequence charSequence = messageObject.caption;
        if (messageObject.isYouTubeVideo()) {
            if (messageObject.youtubeDescription == null) {
                messageObject.youtubeDescription = SpannableString.valueOf(messageObject.messageOwner.media.webpage.description);
                MessageObject.addUrlsByPattern(messageObject.isOut(), messageObject.youtubeDescription, false, 3, (int) j, false);
            }
            charSequence = messageObject.youtubeDescription;
        }
        if (charSequence != this.lastCaption || this.lastVideoDuration != j) {
            this.lastCaption = charSequence;
            this.lastVideoDuration = j;
            if (!(charSequence instanceof Spanned)) {
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
            Spanned spanned = (Spanned) charSequence;
            try {
                URLSpanNoUnderline[] uRLSpanNoUnderlineArr = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
                this.timestamps = new ArrayList<>();
                this.timestampsAppearing = 0.0f;
                if (this.timestampLabelPaint == null) {
                    TextPaint textPaint = new TextPaint(1);
                    this.timestampLabelPaint = textPaint;
                    textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
                    this.timestampLabelPaint.setColor(-1);
                }
                for (URLSpanNoUnderline uRLSpanNoUnderline : uRLSpanNoUnderlineArr) {
                    if (uRLSpanNoUnderline != null && uRLSpanNoUnderline.getURL().startsWith("video?") && (parseInt = Utilities.parseInt((CharSequence) uRLSpanNoUnderline.getURL().substring(6))) != null && parseInt.intValue() >= 0) {
                        float intValue = ((float) (((long) parseInt.intValue()) * 1000)) / ((float) j);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(uRLSpanNoUnderline.label);
                        Emoji.replaceEmoji(spannableStringBuilder, this.timestampLabelPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false);
                        this.timestamps.add(new Pair(Float.valueOf(intValue), spannableStringBuilder));
                    }
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

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$updateTimestamps$1(Pair pair, Pair pair2) {
        if (((Float) pair.first).floatValue() > ((Float) pair2.first).floatValue()) {
            return 1;
        }
        return ((Float) pair2.first).floatValue() > ((Float) pair.first).floatValue() ? -1 : 0;
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
        float f = (float) this.thumbX;
        float min = Math.min(this.animatedThumbX, f);
        this.animatedThumbX = min;
        float lerp = AndroidUtilities.lerp(min, f, 0.5f);
        this.animatedThumbX = lerp;
        if (Math.abs(f - lerp) > 0.005f) {
            this.parentView.invalidate();
        }
        float f2 = this.animatedThumbX;
        float f3 = this.animateThumbProgress;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.07272727f;
            this.animateThumbProgress = f4;
            if (f4 >= 1.0f) {
                this.animateThumbProgress = 1.0f;
            } else {
                view.invalidate();
                float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                f2 = (f2 * interpolation) + (((float) this.fromThumbX) * (1.0f - interpolation));
            }
        }
        float f5 = this.animateThumbLoopBackProgress.set(0.0f);
        if (this.pressed) {
            f5 = 0.0f;
        }
        this.rect.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) this.width) - (((float) thumbWidth) / 2.0f), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
        setPaintColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor, 1.0f - this.transitionProgress);
        drawProgressBar(canvas2, this.rect, paint);
        float f6 = this.bufferedAnimationValue;
        if (f6 != 1.0f) {
            float f7 = f6 + 0.16f;
            this.bufferedAnimationValue = f7;
            if (f7 > 1.0f) {
                this.bufferedAnimationValue = 1.0f;
            } else {
                this.parentView.invalidate();
            }
        }
        if (this.animateResetBuffering) {
            float f8 = this.animateFromBufferedProgress;
            if (f8 > 0.0f) {
                RectF rectF3 = this.rect;
                int i3 = thumbWidth;
                rectF3.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i3) / 2.0f) + (f8 * ((float) (this.width - i3))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, (1.0f - this.transitionProgress) * (1.0f - this.bufferedAnimationValue));
                drawProgressBar(canvas2, this.rect, paint);
            }
            float f9 = this.bufferedProgress;
            if (f9 > 0.0f) {
                RectF rectF4 = this.rect;
                int i4 = thumbWidth;
                rectF4.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i4) / 2.0f) + (f9 * ((float) (this.width - i4))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                drawProgressBar(canvas2, this.rect, paint);
            }
        } else {
            float var_ = this.animateFromBufferedProgress;
            float var_ = this.bufferedAnimationValue;
            float var_ = (var_ * (1.0f - var_)) + (this.bufferedProgress * var_);
            if (var_ > 0.0f) {
                RectF rectF5 = this.rect;
                int i5 = thumbWidth;
                rectF5.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i5) / 2.0f) + (var_ * ((float) (this.width - i5))), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                drawProgressBar(canvas2, this.rect, paint);
            }
        }
        float dp = (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != dp) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            this.lastUpdateTime = elapsedRealtime;
            if (j > 18) {
                j = 16;
            }
            float var_ = this.currentRadius;
            if (var_ < dp) {
                float dp2 = var_ + (((float) AndroidUtilities.dp(1.0f)) * (((float) j) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 > dp) {
                    this.currentRadius = dp;
                }
            } else {
                float dp3 = var_ - (((float) AndroidUtilities.dp(1.0f)) * (((float) j) / 60.0f));
                this.currentRadius = dp3;
                if (dp3 < dp) {
                    this.currentRadius = dp;
                }
            }
            View view2 = this.parentView;
            if (view2 != null) {
                view2.invalidate();
            }
        }
        float lerp2 = AndroidUtilities.lerp(this.currentRadius, 0.0f, this.transitionProgress);
        if (f5 > 0.0f) {
            RectF rectF6 = this.rect;
            float var_ = rectF6.left;
            int i6 = thumbWidth;
            rectF6.right = ((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) i6) / 2.0f) + ((float) (this.width - i6)), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
            RectF rectF7 = this.rect;
            rectF7.left = AndroidUtilities.lerp(var_, rectF7.right, 1.0f - f5);
            if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
                strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
                drawProgressBar(canvas2, this.rect, strokePaint);
            }
            setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
            drawProgressBar(canvas2, this.rect, paint);
            this.rect.left = var_;
            setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
            canvas2.drawCircle(((float) this.horizontalPadding) + AndroidUtilities.lerp((((float) thumbWidth) / 2.0f) + this.loopBackWasThumbX, (((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f)) * (this.loopBackWasThumbX / ((float) (this.width - thumbWidth))), this.transitionProgress), this.rect.centerY(), lerp2 * f5, paint);
        }
        RectF rectF8 = this.rect;
        float var_ = (float) this.horizontalPadding;
        float var_ = ((float) thumbWidth) / 2.0f;
        if (this.pressed) {
            f2 = (float) this.draggingThumbX;
        }
        rectF8.right = var_ + AndroidUtilities.lerp(var_ + f2, (((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f)) * getProgress(), this.transitionProgress);
        if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
            strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
            drawProgressBar(canvas2, this.rect, strokePaint);
        }
        setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
        drawProgressBar(canvas2, this.rect, paint);
        setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
        RectF rectF9 = this.rect;
        canvas2.drawCircle(rectF9.right, rectF9.centerY(), lerp2 * (1.0f - f5), paint);
        drawTimestampLabel(canvas);
    }

    private void drawProgressBar(Canvas canvas, RectF rectF, Paint paint2) {
        int i;
        float f;
        float f2;
        float f3;
        Canvas canvas2 = canvas;
        RectF rectF2 = rectF;
        Paint paint3 = paint2;
        float dp = (float) AndroidUtilities.dp((float) AndroidUtilities.lerp(2, 1, this.transitionProgress));
        ArrayList<Pair<Float, CharSequence>> arrayList = this.timestamps;
        if (arrayList == null || arrayList.isEmpty()) {
            canvas2.drawRoundRect(rectF2, dp, dp, paint3);
            return;
        }
        float f4 = rectF2.bottom;
        float lerp = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) thumbWidth) / 2.0f, 0.0f, this.transitionProgress);
        float lerp2 = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) this.width) - (((float) thumbWidth) / 2.0f), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
        AndroidUtilities.rectTmp.set(rectF2);
        float dp2 = ((float) AndroidUtilities.dp(this.timestampsAppearing * 1.0f)) / 2.0f;
        if (tmpPath == null) {
            tmpPath = new Path();
        }
        tmpPath.reset();
        float dp3 = ((float) AndroidUtilities.dp(4.0f)) / (lerp2 - lerp);
        int i2 = 0;
        while (true) {
            i = -1;
            if (i2 >= this.timestamps.size()) {
                i2 = -1;
                break;
            } else if (((Float) this.timestamps.get(i2).first).floatValue() >= dp3) {
                break;
            } else {
                i2++;
            }
        }
        if (i2 < 0) {
            i2 = 0;
        }
        int size = this.timestamps.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            } else if (1.0f - ((Float) this.timestamps.get(size).first).floatValue() >= dp3) {
                i = size + 1;
                break;
            } else {
                size--;
            }
        }
        if (i < 0) {
            i = this.timestamps.size();
        }
        int i3 = i;
        int i4 = i2;
        while (i4 <= i3) {
            if (i4 == i2) {
                f = 0.0f;
            } else {
                f = ((Float) this.timestamps.get(i4 - 1).first).floatValue();
            }
            if (i4 == i3) {
                f2 = 1.0f;
            } else {
                f2 = ((Float) this.timestamps.get(i4).first).floatValue();
            }
            RectF rectF3 = AndroidUtilities.rectTmp;
            rectF3.left = AndroidUtilities.lerp(lerp, lerp2, f) + (i4 > 0 ? dp2 : 0.0f);
            float lerp3 = AndroidUtilities.lerp(lerp, lerp2, f2) - (i4 < i3 ? dp2 : 0.0f);
            rectF3.right = lerp3;
            float f5 = rectF2.right;
            boolean z = lerp3 > f5;
            if (z) {
                rectF3.right = f5;
            }
            float f6 = rectF3.right;
            float f7 = rectF2.left;
            if (f6 < f7) {
                f3 = lerp;
            } else {
                if (rectF3.left < f7) {
                    rectF3.left = f7;
                }
                if (tmpRadii == null) {
                    tmpRadii = new float[8];
                }
                if (i4 == i2 || (z && rectF3.left >= rectF2.left)) {
                    f3 = lerp;
                    float[] fArr = tmpRadii;
                    fArr[7] = dp;
                    fArr[6] = dp;
                    fArr[1] = dp;
                    fArr[0] = dp;
                    float f8 = 0.7f * dp * this.timestampsAppearing;
                    fArr[5] = f8;
                    fArr[4] = f8;
                    fArr[3] = f8;
                    fArr[2] = f8;
                } else if (i4 >= i3) {
                    float[] fArr2 = tmpRadii;
                    float f9 = 0.7f * dp * this.timestampsAppearing;
                    fArr2[7] = f9;
                    fArr2[6] = f9;
                    fArr2[1] = f9;
                    fArr2[0] = f9;
                    fArr2[5] = dp;
                    fArr2[4] = dp;
                    fArr2[3] = dp;
                    fArr2[2] = dp;
                    f3 = lerp;
                } else {
                    float[] fArr3 = tmpRadii;
                    f3 = lerp;
                    float var_ = 0.7f * dp * this.timestampsAppearing;
                    fArr3[5] = var_;
                    fArr3[4] = var_;
                    fArr3[3] = var_;
                    fArr3[2] = var_;
                    fArr3[7] = var_;
                    fArr3[6] = var_;
                    fArr3[1] = var_;
                    fArr3[0] = var_;
                }
                tmpPath.addRoundRect(rectF3, tmpRadii, Path.Direction.CW);
                if (z) {
                    break;
                }
            }
            i4++;
            lerp = f3;
        }
        canvas2.drawPath(tmpPath, paint3);
    }

    private void drawTimestampLabel(Canvas canvas) {
        float f;
        float f2;
        Canvas canvas2 = canvas;
        ArrayList<Pair<Float, CharSequence>> arrayList = this.timestamps;
        if (arrayList != null && !arrayList.isEmpty()) {
            float f3 = ((this.pressed || this.pressedDelayed) ? (float) this.draggingThumbX : this.animatedThumbX) / ((float) (this.width - thumbWidth));
            int size = this.timestamps.size() - 1;
            while (true) {
                if (size < 0) {
                    size = -1;
                    break;
                } else if (((Float) this.timestamps.get(size).first).floatValue() - 0.001f <= f3) {
                    break;
                } else {
                    size--;
                }
            }
            if (this.timestampLabel == null) {
                this.timestampLabel = new StaticLayout[2];
            }
            float lerp = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) thumbWidth) / 2.0f, 0.0f, this.transitionProgress);
            float lerp2 = ((float) this.horizontalPadding) + AndroidUtilities.lerp(((float) this.width) - (((float) thumbWidth) / 2.0f), ((float) this.parentView.getWidth()) - (((float) this.horizontalPadding) * 2.0f), this.transitionProgress);
            float f4 = ((float) this.horizontalPadding) + (((float) this.width) - (((float) thumbWidth) / 2.0f));
            float abs = Math.abs(lerp - f4) - ((float) AndroidUtilities.dp(16.0f));
            float f5 = this.lastWidth;
            if (f5 > 0.0f && Math.abs(f5 - abs) > 0.01f) {
                StaticLayout[] staticLayoutArr = this.timestampLabel;
                if (staticLayoutArr[0] != null) {
                    staticLayoutArr[0] = makeStaticLayout(staticLayoutArr[0].getText(), (int) abs);
                }
                StaticLayout[] staticLayoutArr2 = this.timestampLabel;
                if (staticLayoutArr2[1] != null) {
                    staticLayoutArr2[1] = makeStaticLayout(staticLayoutArr2[1].getText(), (int) abs);
                }
            }
            this.lastWidth = abs;
            if (size != this.currentTimestamp) {
                StaticLayout[] staticLayoutArr3 = this.timestampLabel;
                staticLayoutArr3[1] = staticLayoutArr3[0];
                if (this.pressed) {
                    try {
                        this.parentView.performHapticFeedback(9, 1);
                    } catch (Exception unused) {
                    }
                }
                if (size < 0 || size >= this.timestamps.size()) {
                    this.timestampLabel[0] = null;
                } else {
                    CharSequence charSequence = (CharSequence) this.timestamps.get(size).second;
                    if (charSequence == null) {
                        this.timestampLabel[0] = null;
                    } else {
                        this.timestampLabel[0] = makeStaticLayout(charSequence, (int) abs);
                    }
                }
                this.timestampChangeT = 0.0f;
                if (size == -1) {
                    this.timestampChangeDirection = -1;
                } else {
                    int i = this.currentTimestamp;
                    if (i == -1) {
                        this.timestampChangeDirection = 1;
                    } else if (size < i) {
                        this.timestampChangeDirection = -1;
                    } else if (size > i) {
                        this.timestampChangeDirection = 1;
                    }
                }
                this.currentTimestamp = size;
            }
            if (this.timestampChangeT < 1.0f) {
                this.timestampChangeT = Math.min(this.timestampChangeT + (((float) Math.min(17, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate))) / (this.timestamps.size() > 8 ? 160.0f : 220.0f)), 1.0f);
                this.parentView.invalidate();
                this.lastTimestampUpdate = SystemClock.elapsedRealtime();
            }
            if (this.timestampsAppearing < 1.0f) {
                this.timestampsAppearing = Math.min(this.timestampsAppearing + (((float) Math.min(17, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate))) / 200.0f), 1.0f);
                this.parentView.invalidate();
                SystemClock.elapsedRealtime();
            }
            float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.timestampChangeT);
            canvas.save();
            int i2 = this.height;
            canvas2.translate(lerp + ((lerp2 - f4) * this.transitionProgress), AndroidUtilities.lerp(((float) (this.lineHeight + i2)) / 2.0f, (float) (i2 - AndroidUtilities.dp(3.0f)), this.transitionProgress) + ((float) AndroidUtilities.dp(12.0f)));
            if (this.timestampLabel[1] != null) {
                canvas.save();
                if (this.timestampChangeDirection != 0) {
                    f2 = 0.0f;
                    canvas2.translate(((float) AndroidUtilities.dp(8.0f)) + (((float) (AndroidUtilities.dp(16.0f) * (-this.timestampChangeDirection))) * interpolation), 0.0f);
                } else {
                    f2 = 0.0f;
                }
                canvas2.translate(f2, ((float) (-this.timestampLabel[1].getHeight())) / 2.0f);
                this.timestampLabelPaint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f * (1.0f - interpolation) * this.timestampsAppearing));
                this.timestampLabel[1].draw(canvas2);
                canvas.restore();
            }
            if (this.timestampLabel[0] != null) {
                canvas.save();
                if (this.timestampChangeDirection != 0) {
                    f = 0.0f;
                    canvas2.translate(((float) AndroidUtilities.dp(8.0f)) + (((float) (AndroidUtilities.dp(16.0f) * this.timestampChangeDirection)) * (1.0f - interpolation)), 0.0f);
                } else {
                    f = 0.0f;
                }
                canvas2.translate(f, ((float) (-this.timestampLabel[0].getHeight())) / 2.0f);
                this.timestampLabelPaint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f * interpolation * this.timestampsAppearing));
                this.timestampLabel[0].draw(canvas2);
                canvas.restore();
            }
            canvas.restore();
        }
    }

    private StaticLayout makeStaticLayout(CharSequence charSequence, int i) {
        if (this.timestampLabelPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            this.timestampLabelPaint = textPaint;
            textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.timestampLabelPaint.setColor(-1);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            return StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.timestampLabelPaint, i).setMaxLines(1).setAlignment(Layout.Alignment.ALIGN_CENTER).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(Math.min(AndroidUtilities.dp(400.0f), i)).build();
        }
        int length = charSequence.length();
        return new StaticLayout(charSequence, 0, length, this.timestampLabelPaint, i, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, Math.min(AndroidUtilities.dp(400.0f), i));
    }

    private void setPaintColor(int i, float f) {
        if (f < 1.0f) {
            i = ColorUtils.setAlphaComponent(i, (int) (((float) Color.alpha(i)) * f));
        }
        paint.setColor(i);
    }
}
