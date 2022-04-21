package org.telegram.ui.Components.spoilers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.TextStyleSpan;

public class SpoilerEffect extends Drawable {
    public static final float[] ALPHAS = {0.3f, 0.6f, 1.0f};
    private static final int FPS = 30;
    private static final float KEYPOINT_DELTA = 5.0f;
    public static final int MAX_PARTICLES_PER_ENTITY = measureMaxParticlesCount();
    public static final int PARTICLES_PER_CHARACTER = measureParticlesPerCharacter();
    private static final int RAND_REPEAT = 14;
    private static final float VERTICAL_PADDING_DP = 2.5f;
    private static final int renderDelayMs = 34;
    private static Path tempPath = new Path();
    private static Paint xRefPaint;
    public boolean drawPoints;
    private boolean enableAlpha;
    private boolean invalidateParent;
    private boolean isLowDevice;
    private List<Long> keyPoints;
    private int lastColor;
    private long lastDrawTime;
    private int mAlpha;
    private View mParent;
    /* access modifiers changed from: private */
    public int maxParticles;
    /* access modifiers changed from: private */
    public Runnable onRippleEndCallback;
    private Paint[] particlePaints;
    float[][] particlePoints;
    private float[] particleRands;
    /* access modifiers changed from: private */
    public ArrayList<Particle> particles;
    /* access modifiers changed from: private */
    public Stack<Particle> particlesPool = new Stack<>();
    private int[] renderCount;
    private boolean reverseAnimator;
    /* access modifiers changed from: private */
    public ValueAnimator rippleAnimator;
    private TimeInterpolator rippleInterpolator;
    private float rippleMaxRadius;
    private float rippleProgress;
    private float rippleX;
    private float rippleY;
    private boolean shouldInvalidateColor;
    private List<RectF> spaces;
    private boolean suppressUpdates;
    private RectF visibleRect;

    static /* synthetic */ float lambda$new$0(float input) {
        return input;
    }

    private static int measureParticlesPerCharacter() {
        switch (SharedConfig.getDevicePerformanceClass()) {
            case 2:
                return 30;
            default:
                return 10;
        }
    }

    private static int measureMaxParticlesCount() {
        switch (SharedConfig.getDevicePerformanceClass()) {
            case 2:
                return 150;
            default:
                return 100;
        }
    }

    public SpoilerEffect() {
        float[] fArr = ALPHAS;
        this.particlePaints = new Paint[fArr.length];
        int length = fArr.length;
        int[] iArr = new int[2];
        iArr[1] = MAX_PARTICLES_PER_ENTITY * 2;
        iArr[0] = length;
        this.particlePoints = (float[][]) Array.newInstance(float.class, iArr);
        this.particleRands = new float[14];
        this.renderCount = new int[fArr.length];
        this.particles = new ArrayList<>();
        this.rippleProgress = -1.0f;
        this.spaces = new ArrayList();
        this.mAlpha = 255;
        this.rippleInterpolator = SpoilerEffect$$ExternalSyntheticLambda0.INSTANCE;
        for (int i = 0; i < ALPHAS.length; i++) {
            this.particlePaints[i] = new Paint();
            if (i == 0) {
                this.particlePaints[i].setStrokeWidth((float) AndroidUtilities.dp(1.4f));
                this.particlePaints[i].setStyle(Paint.Style.STROKE);
                this.particlePaints[i].setStrokeCap(Paint.Cap.ROUND);
            } else {
                this.particlePaints[i].setStrokeWidth((float) AndroidUtilities.dp(1.2f));
                this.particlePaints[i].setStyle(Paint.Style.STROKE);
                this.particlePaints[i].setStrokeCap(Paint.Cap.ROUND);
            }
        }
        this.isLowDevice = SharedConfig.getDevicePerformanceClass() == 0;
        this.enableAlpha = true;
        setColor(0);
    }

    public void setSuppressUpdates(boolean suppressUpdates2) {
        this.suppressUpdates = suppressUpdates2;
        invalidateSelf();
    }

    public void setInvalidateParent(boolean invalidateParent2) {
        this.invalidateParent = invalidateParent2;
    }

    public void updateMaxParticles() {
        int width = getBounds().width() / AndroidUtilities.dp(6.0f);
        int i = PARTICLES_PER_CHARACTER;
        setMaxParticlesCount(MathUtils.clamp(width * i, i, MAX_PARTICLES_PER_ENTITY));
    }

    public void setOnRippleEndCallback(Runnable onRippleEndCallback2) {
        this.onRippleEndCallback = onRippleEndCallback2;
    }

    public void startRipple(float rX, float rY, float radMax) {
        startRipple(rX, rY, radMax, false);
    }

    public void startRipple(float rX, float rY, float radMax, boolean reverse) {
        this.rippleX = rX;
        this.rippleY = rY;
        this.rippleMaxRadius = radMax;
        float f = 1.0f;
        this.rippleProgress = reverse ? 1.0f : 0.0f;
        this.reverseAnimator = reverse;
        ValueAnimator valueAnimator = this.rippleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int startAlpha = this.reverseAnimator ? 255 : this.particlePaints[ALPHAS.length - 1].getAlpha();
        float[] fArr = new float[2];
        fArr[0] = this.rippleProgress;
        if (reverse) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration((long) MathUtils.clamp(this.rippleMaxRadius * 0.3f, 250.0f, 550.0f));
        this.rippleAnimator = duration;
        duration.setInterpolator(this.rippleInterpolator);
        this.rippleAnimator.addUpdateListener(new SpoilerEffect$$ExternalSyntheticLambda1(this, startAlpha));
        this.rippleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Iterator<Particle> it = SpoilerEffect.this.particles.iterator();
                while (it.hasNext()) {
                    Particle p = it.next();
                    if (SpoilerEffect.this.particlesPool.size() < SpoilerEffect.this.maxParticles) {
                        SpoilerEffect.this.particlesPool.push(p);
                    }
                    it.remove();
                }
                if (SpoilerEffect.this.onRippleEndCallback != null) {
                    SpoilerEffect.this.onRippleEndCallback.run();
                    Runnable unused = SpoilerEffect.this.onRippleEndCallback = null;
                }
                ValueAnimator unused2 = SpoilerEffect.this.rippleAnimator = null;
                SpoilerEffect.this.invalidateSelf();
            }
        });
        this.rippleAnimator.start();
        invalidateSelf();
    }

    /* renamed from: lambda$startRipple$1$org-telegram-ui-Components-spoilers-SpoilerEffect  reason: not valid java name */
    public /* synthetic */ void m4538x4218b496(int startAlpha, ValueAnimator animation) {
        float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
        this.rippleProgress = floatValue;
        setAlpha((int) (((float) startAlpha) * (1.0f - floatValue)));
        this.shouldInvalidateColor = true;
        invalidateSelf();
    }

    public void setRippleInterpolator(TimeInterpolator rippleInterpolator2) {
        this.rippleInterpolator = rippleInterpolator2;
    }

    public void setKeyPoints(List<Long> keyPoints2) {
        this.keyPoints = keyPoints2;
        invalidateSelf();
    }

    public void getRipplePath(Path path) {
        path.addCircle(this.rippleX, this.rippleY, this.rippleMaxRadius * MathUtils.clamp(this.rippleProgress, 0.0f, 1.0f), Path.Direction.CW);
    }

    public float getRippleProgress() {
        return this.rippleProgress;
    }

    public boolean shouldInvalidateColor() {
        boolean b = this.shouldInvalidateColor;
        this.shouldInvalidateColor = false;
        return b;
    }

    public void setRippleProgress(float rippleProgress2) {
        ValueAnimator valueAnimator;
        this.rippleProgress = rippleProgress2;
        if (rippleProgress2 == -1.0f && (valueAnimator = this.rippleAnimator) != null) {
            valueAnimator.cancel();
        }
        this.shouldInvalidateColor = true;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        Iterator<Particle> it = this.particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            if (!getBounds().contains((int) p.x, (int) p.y)) {
                it.remove();
            }
            if (this.particlesPool.size() < this.maxParticles) {
                this.particlesPool.push(p);
            }
        }
    }

    public void draw(Canvas canvas) {
        float rf;
        int np;
        Particle newParticle;
        int dt;
        float rf2;
        int i;
        long curTime;
        int i2;
        int i3;
        Particle particle;
        if (this.drawPoints) {
            long curTime2 = System.currentTimeMillis();
            int dt2 = (int) Math.min(curTime2 - this.lastDrawTime, 34);
            this.lastDrawTime = curTime2;
            int left = getBounds().left;
            int top = getBounds().top;
            int right = getBounds().right;
            int bottom = getBounds().bottom;
            for (int i4 = 0; i4 < ALPHAS.length; i4++) {
                this.renderCount[i4] = 0;
            }
            int i5 = 0;
            while (i5 < this.particles.size()) {
                Particle particle2 = this.particles.get(i5);
                float unused = particle2.currentTime = Math.min(particle2.currentTime + ((float) dt2), particle2.lifeTime);
                if (particle2.currentTime < particle2.lifeTime) {
                    Particle particle3 = particle2;
                    curTime = curTime2;
                    i3 = i5;
                    if (isOutOfBounds(left, top, right, bottom, particle2.x, particle2.y)) {
                        particle = particle3;
                    } else {
                        float hdt = (particle3.velocity * ((float) dt2)) / 500.0f;
                        Particle particle4 = particle3;
                        Particle.access$516(particle4, particle3.vecX * hdt);
                        Particle.access$616(particle4, particle4.vecY * hdt);
                        int alphaIndex = particle4.alpha;
                        this.particlePoints[alphaIndex][this.renderCount[alphaIndex] * 2] = particle4.x;
                        this.particlePoints[alphaIndex][(this.renderCount[alphaIndex] * 2) + 1] = particle4.y;
                        int[] iArr = this.renderCount;
                        iArr[alphaIndex] = iArr[alphaIndex] + 1;
                        i2 = i3;
                        i5 = i2 + 1;
                        curTime2 = curTime;
                    }
                } else {
                    particle = particle2;
                    curTime = curTime2;
                    i3 = i5;
                }
                if (this.particlesPool.size() < this.maxParticles) {
                    this.particlesPool.push(particle);
                }
                this.particles.remove(i3);
                i2 = i3 - 1;
                i5 = i2 + 1;
                curTime2 = curTime;
            }
            int i6 = i5;
            int size = this.particles.size();
            int i7 = this.maxParticles;
            if (size < i7) {
                int np2 = i7 - this.particles.size();
                float f = -1.0f;
                Arrays.fill(this.particleRands, -1.0f);
                int i8 = 0;
                while (i8 < np2) {
                    float[] fArr = this.particleRands;
                    float rf3 = fArr[i8 % 14];
                    if (rf3 == f) {
                        float rf4 = Utilities.fastRandom.nextFloat();
                        fArr[i8 % 14] = rf4;
                        rf = rf4;
                    } else {
                        rf = rf3;
                    }
                    Particle newParticle2 = !this.particlesPool.isEmpty() ? this.particlesPool.pop() : new Particle();
                    int attempts = 0;
                    while (true) {
                        generateRandomLocation(newParticle2, i8);
                        int attempts2 = attempts + 1;
                        np = np2;
                        newParticle = newParticle2;
                        dt = dt2;
                        rf2 = rf;
                        i = i8;
                        if (!isOutOfBounds(left, top, right, bottom, newParticle2.x, newParticle2.y) || attempts2 >= 4) {
                            double d = (double) rf2;
                            Double.isNaN(d);
                            double angleRad = ((d * 3.141592653589793d) * 2.0d) - 3.141592653589793d;
                            float unused2 = newParticle.vecX = (float) Math.cos(angleRad);
                            float unused3 = newParticle.vecY = (float) Math.sin(angleRad);
                            float unused4 = newParticle.currentTime = 0.0f;
                            float unused5 = newParticle.lifeTime = (float) (Math.abs(Utilities.fastRandom.nextInt(2000)) + 1000);
                            float unused6 = newParticle.velocity = (6.0f * rf2) + 4.0f;
                            int unused7 = newParticle.alpha = Utilities.fastRandom.nextInt(ALPHAS.length);
                            this.particles.add(newParticle);
                            int alphaIndex2 = newParticle.alpha;
                            this.particlePoints[alphaIndex2][this.renderCount[alphaIndex2] * 2] = newParticle.x;
                            this.particlePoints[alphaIndex2][(this.renderCount[alphaIndex2] * 2) + 1] = newParticle.y;
                            int[] iArr2 = this.renderCount;
                            iArr2[alphaIndex2] = iArr2[alphaIndex2] + 1;
                            i8 = i + 1;
                            np2 = np;
                            dt2 = dt;
                            f = -1.0f;
                        } else {
                            newParticle2 = newParticle;
                            attempts = attempts2;
                            rf = rf2;
                            np2 = np;
                            dt2 = dt;
                            i8 = i;
                        }
                    }
                    double d2 = (double) rf2;
                    Double.isNaN(d2);
                    double angleRad2 = ((d2 * 3.141592653589793d) * 2.0d) - 3.141592653589793d;
                    float unused8 = newParticle.vecX = (float) Math.cos(angleRad2);
                    float unused9 = newParticle.vecY = (float) Math.sin(angleRad2);
                    float unused10 = newParticle.currentTime = 0.0f;
                    float unused11 = newParticle.lifeTime = (float) (Math.abs(Utilities.fastRandom.nextInt(2000)) + 1000);
                    float unused12 = newParticle.velocity = (6.0f * rf2) + 4.0f;
                    int unused13 = newParticle.alpha = Utilities.fastRandom.nextInt(ALPHAS.length);
                    this.particles.add(newParticle);
                    int alphaIndex22 = newParticle.alpha;
                    this.particlePoints[alphaIndex22][this.renderCount[alphaIndex22] * 2] = newParticle.x;
                    this.particlePoints[alphaIndex22][(this.renderCount[alphaIndex22] * 2) + 1] = newParticle.y;
                    int[] iArr22 = this.renderCount;
                    iArr22[alphaIndex22] = iArr22[alphaIndex22] + 1;
                    i8 = i + 1;
                    np2 = np;
                    dt2 = dt;
                    f = -1.0f;
                }
                int i9 = i8;
                int i10 = np2;
                int i11 = dt2;
            }
            for (int a = this.enableAlpha ? 0 : ALPHAS.length - 1; a < ALPHAS.length; a++) {
                int renderCount2 = 0;
                int off = 0;
                for (int i12 = 0; i12 < this.particles.size(); i12++) {
                    Particle p = this.particles.get(i12);
                    RectF rectF = this.visibleRect;
                    if ((rectF == null || rectF.contains(p.x, p.y)) && (p.alpha == a || !this.enableAlpha)) {
                        this.particlePoints[a][(i12 - off) * 2] = p.x;
                        this.particlePoints[a][((i12 - off) * 2) + 1] = p.y;
                        renderCount2 += 2;
                    } else {
                        off++;
                    }
                }
                canvas.drawPoints(this.particlePoints[a], 0, renderCount2, this.particlePaints[a]);
            }
            Canvas canvas2 = canvas;
            return;
        }
        Canvas canvas3 = canvas;
        SpoilerEffectBitmapFactory.getInstance().getPaint().setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.SRC_IN));
        canvas.drawRect((float) getBounds().left, (float) getBounds().top, (float) getBounds().right, (float) getBounds().bottom, SpoilerEffectBitmapFactory.getInstance().getPaint());
        invalidateSelf();
        SpoilerEffectBitmapFactory.getInstance().checkUpdate();
    }

    public void setVisibleBounds(float left, float top, float right, float bottom) {
        if (this.visibleRect == null) {
            this.visibleRect = new RectF();
        }
        this.visibleRect.left = left;
        this.visibleRect.top = top;
        this.visibleRect.right = right;
        this.visibleRect.bottom = bottom;
        invalidateSelf();
    }

    private boolean isOutOfBounds(int left, int top, int right, int bottom, float x, float y) {
        if (x < ((float) left) || x > ((float) right) || y < ((float) (AndroidUtilities.dp(2.5f) + top)) || y > ((float) (bottom - AndroidUtilities.dp(2.5f)))) {
            return true;
        }
        for (int i = 0; i < this.spaces.size(); i++) {
            if (this.spaces.get(i).contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void generateRandomLocation(Particle newParticle, int i) {
        List<Long> list = this.keyPoints;
        if (list == null || list.isEmpty()) {
            float unused = newParticle.x = ((float) getBounds().left) + (Utilities.fastRandom.nextFloat() * ((float) getBounds().width()));
            float unused2 = newParticle.y = ((float) getBounds().top) + (Utilities.fastRandom.nextFloat() * ((float) getBounds().height()));
            return;
        }
        float rf = this.particleRands[i % 14];
        long kp = this.keyPoints.get(Utilities.fastRandom.nextInt(this.keyPoints.size())).longValue();
        float unused3 = newParticle.x = (((float) (((long) getBounds().left) + (kp >> 16))) + (((float) AndroidUtilities.dp(5.0f)) * rf)) - ((float) AndroidUtilities.dp(2.5f));
        float unused4 = newParticle.y = (((float) (((long) getBounds().top) + (65535 & kp))) + (((float) AndroidUtilities.dp(5.0f)) * rf)) - ((float) AndroidUtilities.dp(2.5f));
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        if (this.mParent != null) {
            View v = this.mParent;
            if (v.getParent() == null || !this.invalidateParent) {
                v.invalidate();
            } else {
                ((View) v.getParent()).invalidate();
            }
        }
    }

    public void setParentView(View parentView) {
        this.mParent = parentView;
    }

    public View getParentView() {
        return this.mParent;
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        int i = 0;
        while (true) {
            float[] fArr = ALPHAS;
            if (i < fArr.length) {
                this.particlePaints[i].setAlpha((int) (fArr[i] * ((float) alpha)));
                i++;
            } else {
                return;
            }
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        for (Paint p : this.particlePaints) {
            p.setColorFilter(colorFilter);
        }
    }

    public void setColor(int color) {
        if (this.lastColor != color) {
            int i = 0;
            while (true) {
                float[] fArr = ALPHAS;
                if (i < fArr.length) {
                    this.particlePaints[i].setColor(ColorUtils.setAlphaComponent(color, (int) (((float) this.mAlpha) * fArr[i])));
                    i++;
                } else {
                    this.lastColor = color;
                    return;
                }
            }
        }
    }

    public boolean hasColor() {
        return this.lastColor != 0;
    }

    public int getOpacity() {
        return -2;
    }

    public static synchronized List<Long> measureKeyPoints(Layout textLayout) {
        int h;
        synchronized (SpoilerEffect.class) {
            int w = textLayout.getWidth();
            int h2 = textLayout.getHeight();
            if (w == 0) {
                Layout layout = textLayout;
                int i = h2;
            } else if (h2 == 0) {
                Layout layout2 = textLayout;
                int i2 = h2;
            } else {
                Bitmap measureBitmap = Bitmap.createBitmap(Math.round((float) w), Math.round((float) h2), Bitmap.Config.ARGB_4444);
                textLayout.draw(new Canvas(measureBitmap));
                int[] pixels = new int[(measureBitmap.getWidth() * measureBitmap.getHeight())];
                measureBitmap.getPixels(pixels, 0, measureBitmap.getWidth(), 0, 0, w, h2);
                int sX = -1;
                ArrayList<Long> keyPoints2 = new ArrayList<>(pixels.length);
                for (int x = 0; x < w; x++) {
                    int y = 0;
                    while (y < h2) {
                        if (Color.alpha(pixels[(measureBitmap.getWidth() * y) + x]) >= 128) {
                            if (sX == -1) {
                                sX = x;
                            }
                            h = h2;
                            keyPoints2.add(Long.valueOf((((long) (x - sX)) << 16) + ((long) y)));
                        } else {
                            h = h2;
                        }
                        y++;
                        h2 = h;
                    }
                }
                keyPoints2.trimToSize();
                measureBitmap.recycle();
                return keyPoints2;
            }
            List<Long> emptyList = Collections.emptyList();
            return emptyList;
        }
    }

    public int getMaxParticlesCount() {
        return this.maxParticles;
    }

    public void setMaxParticlesCount(int maxParticles2) {
        this.maxParticles = maxParticles2;
        while (this.particlesPool.size() + this.particles.size() < maxParticles2) {
            this.particlesPool.push(new Particle());
        }
    }

    public static void addSpoilers(TextView tv, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        addSpoilers(tv, tv.getLayout(), (Spanned) tv.getText(), spoilersPool, spoilers);
    }

    public static void addSpoilers(View v, Layout textLayout, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        if (textLayout.getText() instanceof Spanned) {
            addSpoilers(v, textLayout, (Spanned) textLayout.getText(), spoilersPool, spoilers);
        }
    }

    public static void addSpoilers(View v, Layout textLayout, Spanned spannable, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        float t;
        float b;
        int start;
        int end;
        TextStyleSpan[] textStyleSpanArr;
        int i;
        int i2;
        Layout layout = textLayout;
        Spanned spanned = spannable;
        for (int line = 0; line < textLayout.getLineCount(); line++) {
            float l = layout.getLineLeft(line);
            float t2 = (float) layout.getLineTop(line);
            float r = layout.getLineRight(line);
            float b2 = (float) layout.getLineBottom(line);
            int start2 = layout.getLineStart(line);
            int end2 = layout.getLineEnd(line);
            TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) spanned.getSpans(start2, end2, TextStyleSpan.class);
            int length = textStyleSpanArr2.length;
            int i3 = 0;
            while (i3 < length) {
                TextStyleSpan span = textStyleSpanArr2[i3];
                if (span.isSpoiler()) {
                    int ss = spanned.getSpanStart(span);
                    int se = spanned.getSpanEnd(span);
                    int realStart = Math.max(start2, ss);
                    int realEnd = Math.min(end2, se);
                    if (realEnd - realStart == 0) {
                        i2 = i3;
                        i = length;
                        textStyleSpanArr = textStyleSpanArr2;
                        end = end2;
                        start = start2;
                        b = b2;
                        t = t2;
                    } else {
                        int i4 = se;
                        int i5 = ss;
                        TextStyleSpan textStyleSpan = span;
                        i2 = i3;
                        i = length;
                        textStyleSpanArr = textStyleSpanArr2;
                        end = end2;
                        start = start2;
                        b = b2;
                        t = t2;
                        addSpoilersInternal(v, spannable, textLayout, start2, end2, l, t2, r, b2, realStart, realEnd, spoilersPool, spoilers);
                    }
                } else {
                    i2 = i3;
                    i = length;
                    textStyleSpanArr = textStyleSpanArr2;
                    end = end2;
                    start = start2;
                    b = b2;
                    t = t2;
                }
                i3 = i2 + 1;
                length = i;
                textStyleSpanArr2 = textStyleSpanArr;
                end2 = end;
                start2 = start;
                b2 = b;
                t2 = t;
            }
            int i6 = start2;
            float f = b2;
            float f2 = t2;
        }
        if ((v instanceof TextView) && spoilersPool != null) {
            spoilersPool.clear();
        }
    }

    private static void addSpoilersInternal(View v, Spanned spannable, Layout textLayout, int lineStart, int lineEnd, float lineLeft, float lineTop, float lineRight, float lineBottom, int realStart, int realEnd, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        StaticLayout newLayout;
        int i;
        View view = v;
        Spanned spanned = spannable;
        Layout layout = textLayout;
        int i2 = lineEnd;
        int i3 = realStart;
        int i4 = realEnd;
        Stack<SpoilerEffect> stack = spoilersPool;
        SpannableStringBuilder vSpan = SpannableStringBuilder.valueOf(AndroidUtilities.replaceNewLines(new SpannableStringBuilder(spanned, i3, i4)));
        for (TextStyleSpan styleSpan : (TextStyleSpan[]) vSpan.getSpans(0, vSpan.length(), TextStyleSpan.class)) {
            vSpan.removeSpan(styleSpan);
        }
        for (URLSpan urlSpan : (URLSpan[]) vSpan.getSpans(0, vSpan.length(), URLSpan.class)) {
            vSpan.removeSpan(urlSpan);
        }
        if (vSpan.toString().trim().length() != 0) {
            int width = textLayout.getEllipsizedWidth() > 0 ? textLayout.getEllipsizedWidth() : textLayout.getWidth();
            TextPaint measurePaint = new TextPaint(textLayout.getPaint());
            measurePaint.setColor(-16777216);
            if (Build.VERSION.SDK_INT >= 24) {
                newLayout = StaticLayout.Builder.obtain(vSpan, 0, vSpan.length(), measurePaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(textLayout.getSpacingAdd(), textLayout.getSpacingMultiplier()).build();
                TextPaint textPaint = measurePaint;
                int i5 = width;
                i = 0;
            } else {
                TextPaint textPaint2 = measurePaint;
                int i6 = width;
                i = 0;
                newLayout = new StaticLayout(vSpan, measurePaint, width, Layout.Alignment.ALIGN_NORMAL, textLayout.getSpacingMultiplier(), textLayout.getSpacingAdd(), false);
            }
            boolean rtlInNonRTL = (LocaleController.isRTLCharacter(vSpan.charAt(i)) || LocaleController.isRTLCharacter(vSpan.charAt(vSpan.length() + -1))) && !LocaleController.isRTL;
            SpoilerEffect spoilerEffect = (stack == null || spoilersPool.isEmpty()) ? new SpoilerEffect() : (SpoilerEffect) stack.remove(i);
            spoilerEffect.setRippleProgress(-1.0f);
            float ps = i3 == lineStart ? lineLeft : layout.getPrimaryHorizontal(i3);
            float pe = (i4 == i2 || (rtlInNonRTL && i4 == i2 + -1 && spanned.charAt(i2 + -1) == 8230)) ? lineRight : layout.getPrimaryHorizontal(i4);
            spoilerEffect.setBounds((int) Math.min(ps, pe), (int) lineTop, (int) Math.max(ps, pe), (int) lineBottom);
            spoilerEffect.setColor(textLayout.getPaint().getColor());
            spoilerEffect.setRippleInterpolator(Easings.easeInQuad);
            if (!spoilerEffect.isLowDevice) {
                spoilerEffect.setKeyPoints(measureKeyPoints(newLayout));
            }
            spoilerEffect.updateMaxParticles();
            View view2 = v;
            if (view2 != null) {
                spoilerEffect.setParentView(view2);
            }
            spoilerEffect.spaces.clear();
            int i7 = 0;
            while (i7 < vSpan.length()) {
                if (vSpan.charAt(i7) == ' ') {
                    RectF r = new RectF();
                    int off = i3 + i7;
                    int line = layout.getLineForOffset(off);
                    r.top = (float) layout.getLineTop(line);
                    r.bottom = (float) layout.getLineBottom(line);
                    float lh = layout.getPrimaryHorizontal(off);
                    int i8 = line;
                    float rh = layout.getPrimaryHorizontal(off + 1);
                    r.left = (float) ((int) Math.min(lh, rh));
                    r.right = (float) ((int) Math.max(lh, rh));
                    float f = rh;
                    if (Math.abs(lh - rh) <= ((float) AndroidUtilities.dp(20.0f))) {
                        spoilerEffect.spaces.add(r);
                    }
                }
                i7++;
                View view3 = v;
                layout = textLayout;
                float f2 = lineBottom;
            }
            spoilers.add(spoilerEffect);
        }
    }

    public static void clipOutCanvas(Canvas canvas, List<SpoilerEffect> spoilers) {
        tempPath.rewind();
        for (SpoilerEffect eff : spoilers) {
            Rect b = eff.getBounds();
            tempPath.addRect((float) b.left, (float) b.top, (float) b.right, (float) b.bottom, Path.Direction.CW);
        }
        canvas.clipPath(tempPath, Region.Op.DIFFERENCE);
    }

    public static void renderWithRipple(View v, boolean invalidateSpoilersParent, int spoilersColor, int verticalOffset, AtomicReference<Layout> patchedLayoutRef, Layout textLayout, List<SpoilerEffect> spoilers, Canvas canvas) {
        Layout pl;
        StaticLayout staticLayout;
        Layout pl2;
        int i;
        TextStyleSpan[] textStyleSpanArr;
        View view = v;
        int i2 = spoilersColor;
        Layout layout = textLayout;
        List<SpoilerEffect> list = spoilers;
        Canvas canvas2 = canvas;
        if (spoilers.isEmpty()) {
            layout.draw(canvas2);
            return;
        }
        Layout pl3 = patchedLayoutRef.get();
        if (pl3 == null || !textLayout.getText().toString().equals(pl3.getText().toString()) || textLayout.getWidth() != pl3.getWidth() || textLayout.getHeight() != pl3.getHeight()) {
            SpannableStringBuilder sb = new SpannableStringBuilder(textLayout.getText());
            if (textLayout.getText() instanceof Spannable) {
                Spannable sp = (Spannable) textLayout.getText();
                TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) sp.getSpans(0, sp.length(), TextStyleSpan.class);
                int length = textStyleSpanArr2.length;
                int i3 = 0;
                while (i3 < length) {
                    TextStyleSpan ss = textStyleSpanArr2[i3];
                    if (ss.isSpoiler()) {
                        int start = sp.getSpanStart(ss);
                        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) sp.getSpans(start, sp.getSpanEnd(ss), Emoji.EmojiSpan.class);
                        int length2 = emojiSpanArr.length;
                        pl2 = pl3;
                        int i4 = 0;
                        while (i4 < length2) {
                            TextStyleSpan[] textStyleSpanArr3 = textStyleSpanArr2;
                            final Emoji.EmojiSpan e = emojiSpanArr[i4];
                            sb.setSpan(new ReplacementSpan() {
                                public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
                                    return Emoji.EmojiSpan.this.getSize(paint, text, start, end, fm);
                                }

                                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                                }
                            }, sp.getSpanStart(e), sp.getSpanEnd(e), sp.getSpanFlags(ss));
                            sb.removeSpan(e);
                            i4++;
                            textStyleSpanArr2 = textStyleSpanArr3;
                            length = length;
                            emojiSpanArr = emojiSpanArr;
                            length2 = length2;
                            start = start;
                        }
                        textStyleSpanArr = textStyleSpanArr2;
                        i = length;
                        int i5 = start;
                        sb.setSpan(new ForegroundColorSpan(0), sp.getSpanStart(ss), sp.getSpanEnd(ss), sp.getSpanFlags(ss));
                        sb.removeSpan(ss);
                    } else {
                        pl2 = pl3;
                        textStyleSpanArr = textStyleSpanArr2;
                        i = length;
                    }
                    i3++;
                    textStyleSpanArr2 = textStyleSpanArr;
                    length = i;
                    pl3 = pl2;
                }
            }
            if (Build.VERSION.SDK_INT >= 24) {
                staticLayout = StaticLayout.Builder.obtain(sb, 0, sb.length(), textLayout.getPaint(), textLayout.getWidth()).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(textLayout.getSpacingAdd(), textLayout.getSpacingMultiplier()).build();
            } else {
                staticLayout = new StaticLayout(sb, textLayout.getPaint(), textLayout.getWidth(), textLayout.getAlignment(), textLayout.getSpacingMultiplier(), textLayout.getSpacingAdd(), false);
            }
            patchedLayoutRef.set(staticLayout);
            pl = staticLayout;
        } else {
            AtomicReference<Layout> atomicReference = patchedLayoutRef;
            pl = pl3;
        }
        if (!spoilers.isEmpty()) {
            canvas.save();
            canvas2.translate(0.0f, (float) verticalOffset);
            pl.draw(canvas2);
            canvas.restore();
        } else {
            int i6 = verticalOffset;
            layout.draw(canvas2);
        }
        if (!spoilers.isEmpty()) {
            tempPath.rewind();
            for (SpoilerEffect eff : spoilers) {
                Rect b = eff.getBounds();
                tempPath.addRect((float) b.left, (float) b.top, (float) b.right, (float) b.bottom, Path.Direction.CW);
            }
            if (!spoilers.isEmpty() && list.get(0).rippleProgress != -1.0f) {
                canvas.save();
                canvas2.clipPath(tempPath);
                tempPath.rewind();
                if (!spoilers.isEmpty()) {
                    list.get(0).getRipplePath(tempPath);
                }
                canvas2.clipPath(tempPath);
                canvas2.translate(0.0f, (float) (-v.getPaddingTop()));
                layout.draw(canvas2);
                canvas.restore();
            }
            boolean useAlphaLayer = list.get(0).rippleProgress != -1.0f;
            if (useAlphaLayer) {
                canvas.saveLayer(0.0f, 0.0f, (float) v.getMeasuredWidth(), (float) v.getMeasuredHeight(), (Paint) null, 31);
            } else {
                canvas.save();
            }
            canvas2.translate(0.0f, (float) (-v.getPaddingTop()));
            for (SpoilerEffect eff2 : spoilers) {
                eff2.setInvalidateParent(invalidateSpoilersParent);
                if (eff2.getParentView() != view) {
                    eff2.setParentView(view);
                }
                if (eff2.shouldInvalidateColor()) {
                    eff2.setColor(ColorUtils.blendARGB(i2, Theme.chat_msgTextPaint.getColor(), Math.max(0.0f, eff2.getRippleProgress())));
                } else {
                    eff2.setColor(i2);
                }
                eff2.draw(canvas2);
            }
            boolean z = invalidateSpoilersParent;
            if (useAlphaLayer) {
                tempPath.rewind();
                list.get(0).getRipplePath(tempPath);
                if (xRefPaint == null) {
                    Paint paint = new Paint(1);
                    xRefPaint = paint;
                    paint.setColor(-16777216);
                    xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                }
                canvas2.drawPath(tempPath, xRefPaint);
            }
            canvas.restore();
            return;
        }
        boolean z2 = invalidateSpoilersParent;
    }

    private static class Particle {
        /* access modifiers changed from: private */
        public int alpha;
        /* access modifiers changed from: private */
        public float currentTime;
        /* access modifiers changed from: private */
        public float lifeTime;
        /* access modifiers changed from: private */
        public float vecX;
        /* access modifiers changed from: private */
        public float vecY;
        /* access modifiers changed from: private */
        public float velocity;
        /* access modifiers changed from: private */
        public float x;
        /* access modifiers changed from: private */
        public float y;

        private Particle() {
        }

        static /* synthetic */ float access$516(Particle x0, float x1) {
            float f = x0.x + x1;
            x0.x = f;
            return f;
        }

        static /* synthetic */ float access$616(Particle x0, float x1) {
            float f = x0.y + x1;
            x0.y = f;
            return f;
        }
    }
}
