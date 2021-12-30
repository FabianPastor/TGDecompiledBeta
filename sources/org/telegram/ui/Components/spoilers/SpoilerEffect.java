package org.telegram.ui.Components.spoilers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.TextStyleSpan;

public class SpoilerEffect extends Drawable {
    public static final float[] ALPHAS = {0.3f, 0.6f, 1.0f};
    public static final int MAX_PARTICLES_PER_ENTITY = measureMaxParticlesCount();
    public static final int PARTICLES_PER_CHARACTER = measureParticlesPerCharacter();
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
    private RectF visibleRect;

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$new$0(float f) {
        return f;
    }

    public int getOpacity() {
        return -2;
    }

    private static int measureParticlesPerCharacter() {
        return SharedConfig.getDevicePerformanceClass() != 2 ? 10 : 30;
    }

    private static int measureMaxParticlesCount() {
        return SharedConfig.getDevicePerformanceClass() != 2 ? 100 : 150;
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

    public void setSuppressUpdates(boolean z) {
        invalidateSelf();
    }

    public void setInvalidateParent(boolean z) {
        this.invalidateParent = z;
    }

    public void updateMaxParticles() {
        int width = getBounds().width() / AndroidUtilities.dp(6.0f);
        int i = PARTICLES_PER_CHARACTER;
        setMaxParticlesCount(MathUtils.clamp(width * i, i, MAX_PARTICLES_PER_ENTITY));
    }

    public void setOnRippleEndCallback(Runnable runnable) {
        this.onRippleEndCallback = runnable;
    }

    public void startRipple(float f, float f2, float f3) {
        startRipple(f, f2, f3, false);
    }

    public void startRipple(float f, float f2, float f3, boolean z) {
        this.rippleX = f;
        this.rippleY = f2;
        this.rippleMaxRadius = f3;
        float f4 = 1.0f;
        this.rippleProgress = z ? 1.0f : 0.0f;
        this.reverseAnimator = z;
        ValueAnimator valueAnimator = this.rippleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int alpha = this.reverseAnimator ? 255 : this.particlePaints[ALPHAS.length - 1].getAlpha();
        float[] fArr = new float[2];
        fArr[0] = this.rippleProgress;
        if (z) {
            f4 = 0.0f;
        }
        fArr[1] = f4;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration((long) MathUtils.clamp(this.rippleMaxRadius * 0.3f, 250.0f, 550.0f));
        this.rippleAnimator = duration;
        duration.setInterpolator(this.rippleInterpolator);
        this.rippleAnimator.addUpdateListener(new SpoilerEffect$$ExternalSyntheticLambda1(this, alpha));
        this.rippleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Iterator it = SpoilerEffect.this.particles.iterator();
                while (it.hasNext()) {
                    Particle particle = (Particle) it.next();
                    if (SpoilerEffect.this.particlesPool.size() < SpoilerEffect.this.maxParticles) {
                        SpoilerEffect.this.particlesPool.push(particle);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startRipple$1(int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.rippleProgress = floatValue;
        setAlpha((int) (((float) i) * (1.0f - floatValue)));
        this.shouldInvalidateColor = true;
        invalidateSelf();
    }

    public void setRippleInterpolator(TimeInterpolator timeInterpolator) {
        this.rippleInterpolator = timeInterpolator;
    }

    public void setKeyPoints(List<Long> list) {
        this.keyPoints = list;
        invalidateSelf();
    }

    public void getRipplePath(Path path) {
        path.addCircle(this.rippleX, this.rippleY, this.rippleMaxRadius * MathUtils.clamp(this.rippleProgress, 0.0f, 1.0f), Path.Direction.CW);
    }

    public float getRippleProgress() {
        return this.rippleProgress;
    }

    public boolean shouldInvalidateColor() {
        boolean z = this.shouldInvalidateColor;
        this.shouldInvalidateColor = false;
        return z;
    }

    public void setRippleProgress(float f) {
        ValueAnimator valueAnimator;
        this.rippleProgress = f;
        if (f == -1.0f && (valueAnimator = this.rippleAnimator) != null) {
            valueAnimator.cancel();
        }
        this.shouldInvalidateColor = true;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        Iterator<Particle> it = this.particles.iterator();
        while (it.hasNext()) {
            Particle next = it.next();
            if (!getBounds().contains((int) next.x, (int) next.y)) {
                it.remove();
            }
            if (this.particlesPool.size() < this.maxParticles) {
                this.particlesPool.push(next);
            }
        }
    }

    public void draw(Canvas canvas) {
        Particle particle;
        int i;
        float f;
        if (this.drawPoints) {
            long currentTimeMillis = System.currentTimeMillis();
            int min = (int) Math.min(currentTimeMillis - this.lastDrawTime, 34);
            this.lastDrawTime = currentTimeMillis;
            int i2 = getBounds().left;
            int i3 = getBounds().top;
            int i4 = getBounds().right;
            int i5 = getBounds().bottom;
            for (int i6 = 0; i6 < ALPHAS.length; i6++) {
                this.renderCount[i6] = 0;
            }
            int i7 = 0;
            while (i7 < this.particles.size()) {
                Particle particle2 = this.particles.get(i7);
                float f2 = (float) min;
                float unused = particle2.currentTime = Math.min(particle2.currentTime + f2, particle2.lifeTime);
                if (particle2.currentTime < particle2.lifeTime) {
                    float f3 = f2;
                    if (!isOutOfBounds(i2, i3, i4, i5, particle2.x, particle2.y)) {
                        float access$900 = (particle2.velocity * f3) / 500.0f;
                        Particle.access$516(particle2, particle2.vecX * access$900);
                        Particle.access$616(particle2, particle2.vecY * access$900);
                        int access$1200 = particle2.alpha;
                        this.particlePoints[access$1200][this.renderCount[access$1200] * 2] = particle2.x;
                        this.particlePoints[access$1200][(this.renderCount[access$1200] * 2) + 1] = particle2.y;
                        int[] iArr = this.renderCount;
                        iArr[access$1200] = iArr[access$1200] + 1;
                        i7++;
                    }
                }
                if (this.particlesPool.size() < this.maxParticles) {
                    this.particlesPool.push(particle2);
                }
                this.particles.remove(i7);
                i7--;
                i7++;
            }
            int size = this.particles.size();
            int i8 = this.maxParticles;
            if (size < i8) {
                int size2 = i8 - this.particles.size();
                float f4 = -1.0f;
                Arrays.fill(this.particleRands, -1.0f);
                int i9 = 0;
                while (i9 < size2) {
                    float[] fArr = this.particleRands;
                    int i10 = i9 % 14;
                    float f5 = fArr[i10];
                    if (f5 == f4) {
                        f5 = Utilities.fastRandom.nextFloat();
                        fArr[i10] = f5;
                    }
                    float f6 = f5;
                    Particle pop = !this.particlesPool.isEmpty() ? this.particlesPool.pop() : new Particle();
                    int i11 = 0;
                    while (true) {
                        generateRandomLocation(pop, i9);
                        float access$500 = pop.x;
                        float access$600 = pop.y;
                        int i12 = i11 + 1;
                        particle = pop;
                        float f7 = access$500;
                        i = size2;
                        f = f6;
                        if (!isOutOfBounds(i2, i3, i4, i5, f7, access$600) || i12 >= 4) {
                            double d = (double) f;
                            Double.isNaN(d);
                            double d2 = ((d * 3.141592653589793d) * 2.0d) - 3.141592653589793d;
                            float unused2 = particle.vecX = (float) Math.cos(d2);
                            float unused3 = particle.vecY = (float) Math.sin(d2);
                            float unused4 = particle.currentTime = 0.0f;
                            float unused5 = particle.lifeTime = (float) (Math.abs(Utilities.fastRandom.nextInt(2000)) + 1000);
                            float unused6 = particle.velocity = (f * 6.0f) + 4.0f;
                            int unused7 = particle.alpha = Utilities.fastRandom.nextInt(ALPHAS.length);
                            this.particles.add(particle);
                            int access$12002 = particle.alpha;
                            this.particlePoints[access$12002][this.renderCount[access$12002] * 2] = particle.x;
                            this.particlePoints[access$12002][(this.renderCount[access$12002] * 2) + 1] = particle.y;
                            int[] iArr2 = this.renderCount;
                            iArr2[access$12002] = iArr2[access$12002] + 1;
                            i9++;
                            size2 = i;
                            f4 = -1.0f;
                        } else {
                            f6 = f;
                            pop = particle;
                            i11 = i12;
                            size2 = i;
                        }
                    }
                    double d3 = (double) f;
                    Double.isNaN(d3);
                    double d22 = ((d3 * 3.141592653589793d) * 2.0d) - 3.141592653589793d;
                    float unused8 = particle.vecX = (float) Math.cos(d22);
                    float unused9 = particle.vecY = (float) Math.sin(d22);
                    float unused10 = particle.currentTime = 0.0f;
                    float unused11 = particle.lifeTime = (float) (Math.abs(Utilities.fastRandom.nextInt(2000)) + 1000);
                    float unused12 = particle.velocity = (f * 6.0f) + 4.0f;
                    int unused13 = particle.alpha = Utilities.fastRandom.nextInt(ALPHAS.length);
                    this.particles.add(particle);
                    int access$120022 = particle.alpha;
                    this.particlePoints[access$120022][this.renderCount[access$120022] * 2] = particle.x;
                    this.particlePoints[access$120022][(this.renderCount[access$120022] * 2) + 1] = particle.y;
                    int[] iArr22 = this.renderCount;
                    iArr22[access$120022] = iArr22[access$120022] + 1;
                    i9++;
                    size2 = i;
                    f4 = -1.0f;
                }
            }
            for (int length = this.enableAlpha ? 0 : ALPHAS.length - 1; length < ALPHAS.length; length++) {
                int i13 = 0;
                int i14 = 0;
                for (int i15 = 0; i15 < this.particles.size(); i15++) {
                    Particle particle3 = this.particles.get(i15);
                    RectF rectF = this.visibleRect;
                    if ((rectF == null || rectF.contains(particle3.x, particle3.y)) && (particle3.alpha == length || !this.enableAlpha)) {
                        int i16 = (i15 - i14) * 2;
                        this.particlePoints[length][i16] = particle3.x;
                        this.particlePoints[length][i16 + 1] = particle3.y;
                        i13 += 2;
                    } else {
                        i14++;
                    }
                }
                canvas.drawPoints(this.particlePoints[length], 0, i13, this.particlePaints[length]);
            }
            return;
        }
        Canvas canvas2 = canvas;
        SpoilerEffectBitmapFactory.getInstance().getPaint().setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.SRC_IN));
        canvas.drawRect((float) getBounds().left, (float) getBounds().top, (float) getBounds().right, (float) getBounds().bottom, SpoilerEffectBitmapFactory.getInstance().getPaint());
        invalidateSelf();
        SpoilerEffectBitmapFactory.getInstance().checkUpdate();
    }

    public void setVisibleBounds(float f, float f2, float f3, float f4) {
        if (this.visibleRect == null) {
            this.visibleRect = new RectF();
        }
        RectF rectF = this.visibleRect;
        rectF.left = f;
        rectF.top = f2;
        rectF.right = f3;
        rectF.bottom = f4;
        invalidateSelf();
    }

    private boolean isOutOfBounds(int i, int i2, int i3, int i4, float f, float f2) {
        if (f < ((float) i) || f > ((float) i3) || f2 < ((float) (i2 + AndroidUtilities.dp(2.5f))) || f2 > ((float) (i4 - AndroidUtilities.dp(2.5f)))) {
            return true;
        }
        for (int i5 = 0; i5 < this.spaces.size(); i5++) {
            if (this.spaces.get(i5).contains(f, f2)) {
                return true;
            }
        }
        return false;
    }

    private void generateRandomLocation(Particle particle, int i) {
        List<Long> list = this.keyPoints;
        if (list == null || list.isEmpty()) {
            float unused = particle.x = ((float) getBounds().left) + (Utilities.fastRandom.nextFloat() * ((float) getBounds().width()));
            float unused2 = particle.y = ((float) getBounds().top) + (Utilities.fastRandom.nextFloat() * ((float) getBounds().height()));
            return;
        }
        float f = this.particleRands[i % 14];
        List<Long> list2 = this.keyPoints;
        long longValue = list2.get(Utilities.fastRandom.nextInt(list2.size())).longValue();
        float unused3 = particle.x = (((float) (((long) getBounds().left) + (longValue >> 16))) + (((float) AndroidUtilities.dp(5.0f)) * f)) - ((float) AndroidUtilities.dp(2.5f));
        float unused4 = particle.y = (((float) (((long) getBounds().top) + (longValue & 65535))) + (f * ((float) AndroidUtilities.dp(5.0f)))) - ((float) AndroidUtilities.dp(2.5f));
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        View view = this.mParent;
        if (view == null) {
            return;
        }
        if (view.getParent() == null || !this.invalidateParent) {
            view.invalidate();
        } else {
            ((View) view.getParent()).invalidate();
        }
    }

    public void setParentView(View view) {
        this.mParent = view;
    }

    public View getParentView() {
        return this.mParent;
    }

    public void setAlpha(int i) {
        this.mAlpha = i;
        int i2 = 0;
        while (true) {
            float[] fArr = ALPHAS;
            if (i2 < fArr.length) {
                this.particlePaints[i2].setAlpha((int) (fArr[i2] * ((float) i)));
                i2++;
            } else {
                return;
            }
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        for (Paint colorFilter2 : this.particlePaints) {
            colorFilter2.setColorFilter(colorFilter);
        }
    }

    public void setColor(int i) {
        if (this.lastColor != i) {
            int i2 = 0;
            while (true) {
                float[] fArr = ALPHAS;
                if (i2 < fArr.length) {
                    this.particlePaints[i2].setColor(ColorUtils.setAlphaComponent(i, (int) (((float) this.mAlpha) * fArr[i2])));
                    i2++;
                } else {
                    this.lastColor = i;
                    return;
                }
            }
        }
    }

    public static synchronized List<Long> measureKeyPoints(Layout layout) {
        synchronized (SpoilerEffect.class) {
            int width = layout.getWidth();
            int height = layout.getHeight();
            if (width != 0) {
                if (height != 0) {
                    Bitmap createBitmap = Bitmap.createBitmap(Math.round((float) width), Math.round((float) height), Bitmap.Config.ARGB_4444);
                    layout.draw(new Canvas(createBitmap));
                    int width2 = createBitmap.getWidth() * createBitmap.getHeight();
                    int[] iArr = new int[width2];
                    createBitmap.getPixels(iArr, 0, createBitmap.getWidth(), 0, 0, width, height);
                    ArrayList arrayList = new ArrayList(width2);
                    int i = -1;
                    for (int i2 = 0; i2 < width; i2++) {
                        for (int i3 = 0; i3 < height; i3++) {
                            if (Color.alpha(iArr[(createBitmap.getWidth() * i3) + i2]) >= 128) {
                                if (i == -1) {
                                    i = i2;
                                }
                                arrayList.add(Long.valueOf((((long) (i2 - i)) << 16) + ((long) i3)));
                            }
                        }
                    }
                    arrayList.trimToSize();
                    createBitmap.recycle();
                    return arrayList;
                }
            }
            List<Long> emptyList = Collections.emptyList();
            return emptyList;
        }
    }

    public void setMaxParticlesCount(int i) {
        this.maxParticles = i;
        while (this.particlesPool.size() + this.particles.size() < i) {
            this.particlesPool.push(new Particle());
        }
    }

    public static void addSpoilers(TextView textView, Stack<SpoilerEffect> stack, List<SpoilerEffect> list) {
        addSpoilers(textView, textView.getLayout(), (Spannable) textView.getText(), stack, list);
    }

    public static void addSpoilers(View view, Layout layout, Stack<SpoilerEffect> stack, List<SpoilerEffect> list) {
        if (layout.getText() instanceof Spannable) {
            addSpoilers(view, layout, (Spannable) layout.getText(), stack, list);
        }
    }

    public static void addSpoilers(View view, Layout layout, Spannable spannable, Stack<SpoilerEffect> stack, List<SpoilerEffect> list) {
        int i;
        int i2;
        int i3;
        TextStyleSpan[] textStyleSpanArr;
        int i4;
        int i5;
        float f;
        float f2;
        Layout layout2 = layout;
        Spannable spannable2 = spannable;
        int i6 = 0;
        while (i6 < layout.getLineCount()) {
            float lineLeft = layout2.getLineLeft(i6);
            float lineTop = (float) layout2.getLineTop(i6);
            float lineRight = layout2.getLineRight(i6);
            float lineBottom = (float) layout2.getLineBottom(i6);
            int lineStart = layout2.getLineStart(i6);
            int lineEnd = layout2.getLineEnd(i6);
            TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) spannable2.getSpans(lineStart, lineEnd, TextStyleSpan.class);
            int length = textStyleSpanArr2.length;
            int i7 = 0;
            while (i7 < length) {
                TextStyleSpan textStyleSpan = textStyleSpanArr2[i7];
                if (textStyleSpan.isSpoiler()) {
                    int spanStart = spannable2.getSpanStart(textStyleSpan);
                    int spanEnd = spannable2.getSpanEnd(textStyleSpan);
                    int max = Math.max(lineStart, spanStart);
                    int min = Math.min(lineEnd, spanEnd);
                    if (min - max != 0) {
                        i5 = i7;
                        i4 = length;
                        textStyleSpanArr = textStyleSpanArr2;
                        i3 = lineEnd;
                        float f3 = lineBottom;
                        i2 = lineStart;
                        int i8 = max;
                        f2 = lineBottom;
                        int i9 = min;
                        f = lineTop;
                        i = i6;
                        addSpoilersInternal(view, spannable, layout, lineStart, lineEnd, lineLeft, lineTop, lineRight, f3, i8, i9, stack, list);
                        i7 = i5 + 1;
                        lineBottom = f2;
                        lineTop = f;
                        length = i4;
                        textStyleSpanArr2 = textStyleSpanArr;
                        lineEnd = i3;
                        lineStart = i2;
                        i6 = i;
                    }
                }
                i5 = i7;
                i4 = length;
                textStyleSpanArr = textStyleSpanArr2;
                i3 = lineEnd;
                i2 = lineStart;
                f2 = lineBottom;
                f = lineTop;
                i = i6;
                i7 = i5 + 1;
                lineBottom = f2;
                lineTop = f;
                length = i4;
                textStyleSpanArr2 = textStyleSpanArr;
                lineEnd = i3;
                lineStart = i2;
                i6 = i;
            }
            i6++;
        }
        if ((view instanceof TextView) && stack != null) {
            stack.clear();
        }
    }

    @SuppressLint({"WrongConstant"})
    private static void addSpoilersInternal(View view, Spannable spannable, Layout layout, int i, int i2, float f, float f2, float f3, float f4, int i3, int i4, Stack<SpoilerEffect> stack, List<SpoilerEffect> list) {
        StaticLayout staticLayout;
        int i5;
        float f5;
        int i6;
        View view2 = view;
        Spannable spannable2 = spannable;
        Layout layout2 = layout;
        int i7 = i2;
        int i8 = i3;
        int i9 = i4;
        Stack<SpoilerEffect> stack2 = stack;
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(AndroidUtilities.replaceNewLines(new SpannableStringBuilder(spannable2, i8, i9)));
        for (TextStyleSpan removeSpan : (TextStyleSpan[]) valueOf.getSpans(0, valueOf.length(), TextStyleSpan.class)) {
            valueOf.removeSpan(removeSpan);
        }
        for (URLSpan removeSpan2 : (URLSpan[]) valueOf.getSpans(0, valueOf.length(), URLSpan.class)) {
            valueOf.removeSpan(removeSpan2);
        }
        if (valueOf.toString().trim().length() != 0) {
            int ellipsizedWidth = layout.getEllipsizedWidth() > 0 ? layout.getEllipsizedWidth() : layout.getWidth();
            TextPaint textPaint = new TextPaint(layout.getPaint());
            textPaint.setColor(-16777216);
            if (Build.VERSION.SDK_INT >= 24) {
                staticLayout = StaticLayout.Builder.obtain(valueOf, 0, valueOf.length(), textPaint, ellipsizedWidth).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(layout.getSpacingAdd(), layout.getSpacingMultiplier()).build();
                i5 = 0;
            } else {
                i5 = 0;
                staticLayout = new StaticLayout(valueOf, textPaint, ellipsizedWidth, Layout.Alignment.ALIGN_NORMAL, layout.getSpacingMultiplier(), layout.getSpacingAdd(), false);
            }
            boolean z = (LocaleController.isRTLCharacter(valueOf.charAt(i5)) || LocaleController.isRTLCharacter(valueOf.charAt(valueOf.length() + -1))) && !LocaleController.isRTL;
            SpoilerEffect spoilerEffect = (stack2 == null || stack.isEmpty()) ? new SpoilerEffect() : (SpoilerEffect) stack2.remove(i5);
            spoilerEffect.setRippleProgress(-1.0f);
            if (i8 == i) {
                f5 = f;
            } else {
                f5 = layout2.getPrimaryHorizontal(i8);
            }
            float primaryHorizontal = (i9 == i7 || (z && i9 == (i6 = i7 + -1) && spannable2.charAt(i6) == 8230)) ? f3 : layout2.getPrimaryHorizontal(i9);
            spoilerEffect.setBounds((int) Math.min(f5, primaryHorizontal), (int) f2, (int) Math.max(f5, primaryHorizontal), (int) f4);
            spoilerEffect.setColor(layout.getPaint().getColor());
            spoilerEffect.setRippleInterpolator(Easings.easeInQuad);
            if (!spoilerEffect.isLowDevice) {
                spoilerEffect.setKeyPoints(measureKeyPoints(staticLayout));
            }
            spoilerEffect.updateMaxParticles();
            View view3 = view;
            if (view3 != null) {
                spoilerEffect.setParentView(view3);
            }
            spoilerEffect.spaces.clear();
            for (int i10 = 0; i10 < valueOf.length(); i10++) {
                if (valueOf.charAt(i10) == ' ') {
                    RectF rectF = new RectF();
                    int i11 = i8 + i10;
                    int lineForOffset = layout2.getLineForOffset(i11);
                    rectF.top = (float) layout2.getLineTop(lineForOffset);
                    rectF.bottom = (float) layout2.getLineBottom(lineForOffset);
                    float primaryHorizontal2 = layout2.getPrimaryHorizontal(i11);
                    float primaryHorizontal3 = layout2.getPrimaryHorizontal(i11 + 1);
                    rectF.left = (float) ((int) Math.min(primaryHorizontal2, primaryHorizontal3));
                    rectF.right = (float) ((int) Math.max(primaryHorizontal2, primaryHorizontal3));
                    if (Math.abs(primaryHorizontal2 - primaryHorizontal3) <= ((float) AndroidUtilities.dp(20.0f))) {
                        spoilerEffect.spaces.add(rectF);
                    }
                }
            }
            list.add(spoilerEffect);
        }
    }

    public static void clipOutCanvas(Canvas canvas, List<SpoilerEffect> list) {
        tempPath.rewind();
        for (SpoilerEffect bounds : list) {
            Rect bounds2 = bounds.getBounds();
            tempPath.addRect((float) bounds2.left, (float) bounds2.top, (float) bounds2.right, (float) bounds2.bottom, Path.Direction.CW);
        }
        canvas.clipPath(tempPath, Region.Op.DIFFERENCE);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0044, code lost:
        if (r5 != r6) goto L_0x0046;
     */
    @android.annotation.SuppressLint({"WrongConstant"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void renderWithRipple(android.view.View r22, boolean r23, int r24, int r25, java.util.concurrent.atomic.AtomicReference<android.text.Layout> r26, android.text.Layout r27, java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r28, android.graphics.Canvas r29) {
        /*
            r0 = r22
            r1 = r24
            r2 = r27
            r3 = r28
            r11 = r29
            boolean r4 = r28.isEmpty()
            if (r4 == 0) goto L_0x0013
            r2.draw(r11)
        L_0x0013:
            java.lang.Object r4 = r26.get()
            android.text.Layout r4 = (android.text.Layout) r4
            r13 = 0
            if (r4 == 0) goto L_0x0046
            java.lang.CharSequence r5 = r27.getText()
            java.lang.String r5 = r5.toString()
            java.lang.CharSequence r6 = r4.getText()
            java.lang.String r6 = r6.toString()
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x0046
            int r5 = r27.getWidth()
            int r6 = r4.getWidth()
            if (r5 != r6) goto L_0x0046
            int r5 = r27.getHeight()
            int r6 = r4.getHeight()
            if (r5 == r6) goto L_0x0121
        L_0x0046:
            android.text.SpannableStringBuilder r15 = new android.text.SpannableStringBuilder
            java.lang.CharSequence r4 = r27.getText()
            r15.<init>(r4)
            java.lang.CharSequence r4 = r27.getText()
            android.text.Spannable r4 = (android.text.Spannable) r4
            int r5 = r4.length()
            java.lang.Class<org.telegram.ui.Components.TextStyleSpan> r6 = org.telegram.ui.Components.TextStyleSpan.class
            java.lang.Object[] r5 = r4.getSpans(r13, r5, r6)
            org.telegram.ui.Components.TextStyleSpan[] r5 = (org.telegram.ui.Components.TextStyleSpan[]) r5
            int r6 = r5.length
            r7 = 0
        L_0x0063:
            if (r7 >= r6) goto L_0x00c9
            r8 = r5[r7]
            boolean r9 = r8.isSpoiler()
            if (r9 == 0) goto L_0x00bd
            int r9 = r4.getSpanStart(r8)
            int r10 = r4.getSpanEnd(r8)
            java.lang.Class<org.telegram.messenger.Emoji$EmojiSpan> r14 = org.telegram.messenger.Emoji.EmojiSpan.class
            java.lang.Object[] r14 = r4.getSpans(r9, r10, r14)
            org.telegram.messenger.Emoji$EmojiSpan[] r14 = (org.telegram.messenger.Emoji.EmojiSpan[]) r14
            int r12 = r14.length
        L_0x007e:
            if (r13 >= r12) goto L_0x00a0
            r16 = r5
            r5 = r14[r13]
            r17 = r6
            org.telegram.ui.Components.spoilers.SpoilerEffect$2 r6 = new org.telegram.ui.Components.spoilers.SpoilerEffect$2
            r6.<init>()
            r18 = r12
            int r12 = r4.getSpanFlags(r8)
            r15.setSpan(r6, r9, r10, r12)
            r15.removeSpan(r5)
            int r13 = r13 + 1
            r5 = r16
            r6 = r17
            r12 = r18
            goto L_0x007e
        L_0x00a0:
            r16 = r5
            r17 = r6
            android.text.style.ForegroundColorSpan r5 = new android.text.style.ForegroundColorSpan
            r6 = 0
            r5.<init>(r6)
            int r6 = r4.getSpanStart(r8)
            int r9 = r4.getSpanEnd(r8)
            int r10 = r4.getSpanFlags(r8)
            r15.setSpan(r5, r6, r9, r10)
            r15.removeSpan(r8)
            goto L_0x00c1
        L_0x00bd:
            r16 = r5
            r17 = r6
        L_0x00c1:
            int r7 = r7 + 1
            r5 = r16
            r6 = r17
            r13 = 0
            goto L_0x0063
        L_0x00c9:
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 24
            if (r4 < r5) goto L_0x0100
            int r4 = r15.length()
            android.text.TextPaint r5 = r27.getPaint()
            int r6 = r27.getWidth()
            r7 = 0
            android.text.StaticLayout$Builder r4 = android.text.StaticLayout.Builder.obtain(r15, r7, r4, r5, r6)
            r5 = 1
            android.text.StaticLayout$Builder r4 = r4.setBreakStrategy(r5)
            android.text.StaticLayout$Builder r4 = r4.setHyphenationFrequency(r7)
            android.text.Layout$Alignment r5 = android.text.Layout.Alignment.ALIGN_NORMAL
            android.text.StaticLayout$Builder r4 = r4.setAlignment(r5)
            float r5 = r27.getSpacingAdd()
            float r6 = r27.getSpacingMultiplier()
            android.text.StaticLayout$Builder r4 = r4.setLineSpacing(r5, r6)
            android.text.StaticLayout r4 = r4.build()
            goto L_0x011c
        L_0x0100:
            android.text.StaticLayout r4 = new android.text.StaticLayout
            android.text.TextPaint r16 = r27.getPaint()
            int r17 = r27.getWidth()
            android.text.Layout$Alignment r18 = r27.getAlignment()
            float r19 = r27.getSpacingMultiplier()
            float r20 = r27.getSpacingAdd()
            r21 = 0
            r14 = r4
            r14.<init>(r15, r16, r17, r18, r19, r20, r21)
        L_0x011c:
            r5 = r26
            r5.set(r4)
        L_0x0121:
            boolean r5 = r28.isEmpty()
            r12 = 0
            if (r5 != 0) goto L_0x0138
            r29.save()
            r5 = r25
            float r5 = (float) r5
            r11.translate(r12, r5)
            r4.draw(r11)
            r29.restore()
            goto L_0x013b
        L_0x0138:
            r2.draw(r11)
        L_0x013b:
            boolean r4 = r28.isEmpty()
            if (r4 != 0) goto L_0x0268
            android.graphics.Path r4 = tempPath
            r4.rewind()
            java.util.Iterator r4 = r28.iterator()
        L_0x014a:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0172
            java.lang.Object r5 = r4.next()
            org.telegram.ui.Components.spoilers.SpoilerEffect r5 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r5
            android.graphics.Rect r5 = r5.getBounds()
            android.graphics.Path r13 = tempPath
            int r6 = r5.left
            float r14 = (float) r6
            int r6 = r5.top
            float r15 = (float) r6
            int r6 = r5.right
            float r6 = (float) r6
            int r5 = r5.bottom
            float r5 = (float) r5
            android.graphics.Path$Direction r18 = android.graphics.Path.Direction.CW
            r16 = r6
            r17 = r5
            r13.addRect(r14, r15, r16, r17, r18)
            goto L_0x014a
        L_0x0172:
            boolean r4 = r28.isEmpty()
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r4 != 0) goto L_0x01ba
            r4 = 0
            java.lang.Object r6 = r3.get(r4)
            org.telegram.ui.Components.spoilers.SpoilerEffect r6 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r6
            float r4 = r6.rippleProgress
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x01ba
            r29.save()
            android.graphics.Path r4 = tempPath
            r11.clipPath(r4)
            android.graphics.Path r4 = tempPath
            r4.rewind()
            boolean r4 = r28.isEmpty()
            if (r4 != 0) goto L_0x01a6
            r4 = 0
            java.lang.Object r6 = r3.get(r4)
            org.telegram.ui.Components.spoilers.SpoilerEffect r6 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r6
            android.graphics.Path r4 = tempPath
            r6.getRipplePath(r4)
        L_0x01a6:
            android.graphics.Path r4 = tempPath
            r11.clipPath(r4)
            int r4 = r22.getPaddingTop()
            int r4 = -r4
            float r4 = (float) r4
            r11.translate(r12, r4)
            r2.draw(r11)
            r29.restore()
        L_0x01ba:
            r2 = 0
            java.lang.Object r4 = r3.get(r2)
            org.telegram.ui.Components.spoilers.SpoilerEffect r4 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r4
            float r2 = r4.rippleProgress
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 == 0) goto L_0x01c9
            r2 = 1
            goto L_0x01ca
        L_0x01c9:
            r2 = 0
        L_0x01ca:
            if (r2 == 0) goto L_0x01e1
            r5 = 0
            r6 = 0
            int r4 = r22.getMeasuredWidth()
            float r7 = (float) r4
            int r4 = r22.getMeasuredHeight()
            float r8 = (float) r4
            r9 = 0
            r10 = 31
            r4 = r29
            r4.saveLayer(r5, r6, r7, r8, r9, r10)
            goto L_0x01e4
        L_0x01e1:
            r29.save()
        L_0x01e4:
            int r4 = r22.getPaddingTop()
            int r4 = -r4
            float r4 = (float) r4
            r11.translate(r12, r4)
            java.util.Iterator r4 = r28.iterator()
        L_0x01f1:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x022e
            java.lang.Object r5 = r4.next()
            org.telegram.ui.Components.spoilers.SpoilerEffect r5 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r5
            r6 = r23
            r5.setInvalidateParent(r6)
            android.view.View r7 = r5.getParentView()
            if (r7 == r0) goto L_0x020b
            r5.setParentView(r0)
        L_0x020b:
            boolean r7 = r5.shouldInvalidateColor()
            if (r7 == 0) goto L_0x0227
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            int r7 = r7.getColor()
            float r8 = r5.getRippleProgress()
            float r8 = java.lang.Math.max(r12, r8)
            int r7 = androidx.core.graphics.ColorUtils.blendARGB(r1, r7, r8)
            r5.setColor(r7)
            goto L_0x022a
        L_0x0227:
            r5.setColor(r1)
        L_0x022a:
            r5.draw(r11)
            goto L_0x01f1
        L_0x022e:
            if (r2 == 0) goto L_0x0265
            android.graphics.Path r0 = tempPath
            r0.rewind()
            r0 = 0
            java.lang.Object r0 = r3.get(r0)
            org.telegram.ui.Components.spoilers.SpoilerEffect r0 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r0
            android.graphics.Path r1 = tempPath
            r0.getRipplePath(r1)
            android.graphics.Paint r0 = xRefPaint
            if (r0 != 0) goto L_0x025e
            android.graphics.Paint r0 = new android.graphics.Paint
            r1 = 1
            r0.<init>(r1)
            xRefPaint = r0
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0.setColor(r1)
            android.graphics.Paint r0 = xRefPaint
            android.graphics.PorterDuffXfermode r1 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.CLEAR
            r1.<init>(r2)
            r0.setXfermode(r1)
        L_0x025e:
            android.graphics.Path r0 = tempPath
            android.graphics.Paint r1 = xRefPaint
            r11.drawPath(r0, r1)
        L_0x0265:
            r29.restore()
        L_0x0268:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.spoilers.SpoilerEffect.renderWithRipple(android.view.View, boolean, int, int, java.util.concurrent.atomic.AtomicReference, android.text.Layout, java.util.List, android.graphics.Canvas):void");
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

        static /* synthetic */ float access$516(Particle particle, float f) {
            float f2 = particle.x + f;
            particle.x = f2;
            return f2;
        }

        static /* synthetic */ float access$616(Particle particle, float f) {
            float f2 = particle.y + f;
            particle.y = f2;
            return f2;
        }
    }
}
