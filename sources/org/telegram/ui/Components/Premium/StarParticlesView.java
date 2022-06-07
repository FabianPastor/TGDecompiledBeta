package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.GLIconSettingsView;

public class StarParticlesView extends View {
    public Drawable drawable;
    int size;

    public StarParticlesView(Context context) {
        super(context);
        Drawable drawable2 = new Drawable(20);
        this.drawable = drawable2;
        drawable2.init();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth() << (getMeasuredHeight() + 16);
        if (this.size != measuredWidth) {
            this.size = measuredWidth;
            this.drawable.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(140.0f), (float) AndroidUtilities.dp(140.0f));
            this.drawable.rect.offset((((float) getMeasuredWidth()) - this.drawable.rect.width()) / 2.0f, (((float) getMeasuredHeight()) - this.drawable.rect.height()) / 2.0f);
            this.drawable.resetPositions();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawable.onDraw(canvas);
        if (!this.drawable.paused) {
            invalidate();
        }
    }

    public void flingParticles(float f) {
        float f2 = f < 60.0f ? 5.0f : f < 180.0f ? 9.0f : 15.0f;
        AnimatorSet animatorSet = new AnimatorSet();
        StarParticlesView$$ExternalSyntheticLambda0 starParticlesView$$ExternalSyntheticLambda0 = new StarParticlesView$$ExternalSyntheticLambda0(this);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, f2});
        ofFloat.addUpdateListener(starParticlesView$$ExternalSyntheticLambda0);
        ofFloat.setDuration(600);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{f2, 1.0f});
        ofFloat2.addUpdateListener(starParticlesView$$ExternalSyntheticLambda0);
        ofFloat2.setDuration(2000);
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$flingParticles$0(ValueAnimator valueAnimator) {
        this.drawable.speedScale = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    public static class Drawable {
        public final int count;
        /* access modifiers changed from: private */
        public final float dt = (1000.0f / AndroidUtilities.screenRefreshRate);
        private int lastColor;
        public long minLifeTime = 2000;
        /* access modifiers changed from: private */
        public Paint paint = new Paint();
        ArrayList<Particle> particles = new ArrayList<>();
        public boolean paused;
        long pausedTime;
        public RectF rect = new RectF();
        public int size1 = 14;
        public int size2 = 12;
        public int size3 = 10;
        public float speedScale = 1.0f;
        /* access modifiers changed from: private */
        public final Bitmap[] stars = new Bitmap[3];
        public boolean useGradient;

        public Drawable(int i) {
            this.count = i;
        }

        public void init() {
            generateBitmaps();
            if (this.particles.isEmpty()) {
                for (int i = 0; i < this.count; i++) {
                    this.particles.add(new Particle());
                }
            }
        }

        public void updateColors() {
            int color = Theme.getColor("premiumStartSmallStarsColor");
            if (this.lastColor != color) {
                this.lastColor = color;
                generateBitmaps();
            }
        }

        private void generateBitmaps() {
            int dp;
            for (int i = 0; i < 3; i++) {
                float f = 0.85f;
                if (i == 0) {
                    dp = AndroidUtilities.dp((float) this.size1);
                } else if (i == 1) {
                    dp = AndroidUtilities.dp((float) this.size2);
                } else {
                    f = 0.9f;
                    dp = AndroidUtilities.dp((float) this.size3);
                }
                int i2 = dp;
                Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ARGB_8888);
                this.stars[i] = createBitmap;
                Canvas canvas = new Canvas(createBitmap);
                Path path = new Path();
                float f2 = (float) (i2 >> 1);
                int i3 = (int) (f * f2);
                path.moveTo(0.0f, f2);
                float f3 = (float) i3;
                path.lineTo(f3, f3);
                path.lineTo(f2, 0.0f);
                float f4 = (float) (i2 - i3);
                path.lineTo(f4, f3);
                float f5 = (float) i2;
                path.lineTo(f5, f2);
                path.lineTo(f4, f4);
                path.lineTo(f2, f5);
                path.lineTo(f3, f4);
                path.lineTo(0.0f, f2);
                path.close();
                Paint paint2 = new Paint();
                if (this.useGradient) {
                    if (i2 >= AndroidUtilities.dp(10.0f)) {
                        PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, i2, i2, (float) (i2 * -2), 0.0f);
                    } else {
                        PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, i2, i2, (float) (i2 * -4), 0.0f);
                    }
                    Paint mainGradientPaint = PremiumGradient.getInstance().getMainGradientPaint();
                    mainGradientPaint.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(((float) this.size1) / 5.0f)));
                    mainGradientPaint.setAlpha(120);
                    canvas.drawPath(path, mainGradientPaint);
                    mainGradientPaint.setPathEffect((PathEffect) null);
                    mainGradientPaint.setAlpha(255);
                } else {
                    paint2.setColor(Theme.getColor("premiumStartSmallStarsColor"));
                    paint2.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(((float) this.size1) / 5.0f)));
                    canvas.drawPath(path, paint2);
                }
            }
        }

        public void resetPositions() {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                this.particles.get(i).genPosition(currentTimeMillis);
            }
        }

        public void onDraw(Canvas canvas) {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                Particle particle = this.particles.get(i);
                if (this.paused) {
                    particle.draw(canvas, this.pausedTime);
                } else {
                    particle.draw(canvas, currentTimeMillis);
                }
                if (currentTimeMillis > particle.lifeTime) {
                    particle.genPosition(currentTimeMillis);
                }
            }
        }

        private class Particle {
            private int alpha;
            float inProgress;
            /* access modifiers changed from: private */
            public long lifeTime;
            private int starIndex;
            private float vecX;
            private float vecY;
            private float x;
            private float y;

            private Particle() {
            }

            static /* synthetic */ long access$114(Particle particle, long j) {
                long j2 = particle.lifeTime + j;
                particle.lifeTime = j2;
                return j2;
            }

            public void draw(Canvas canvas, long j) {
                canvas.save();
                canvas.translate(this.x, this.y);
                float f = this.inProgress;
                if (f < 1.0f || GLIconSettingsView.smallStarsSize != 1.0f) {
                    float interpolation = AndroidUtilities.overshootInterpolator.getInterpolation(f) * GLIconSettingsView.smallStarsSize;
                    canvas.scale(interpolation, interpolation, ((float) Drawable.this.stars[this.starIndex].getWidth()) / 2.0f, ((float) Drawable.this.stars[this.starIndex].getHeight()) / 2.0f);
                }
                long j2 = this.lifeTime;
                Drawable.this.paint.setAlpha((int) (((float) this.alpha) * (1.0f - (j2 - j < 200 ? Utilities.clamp(1.0f - (((float) (j2 - j)) / 150.0f), 1.0f, 0.0f) : 0.0f))));
                canvas.drawBitmap(Drawable.this.stars[this.starIndex], 0.0f, 0.0f, Drawable.this.paint);
                canvas.restore();
                if (!Drawable.this.paused) {
                    float dp = ((float) AndroidUtilities.dp(4.0f)) * (Drawable.this.dt / 660.0f);
                    Drawable drawable = Drawable.this;
                    float f2 = dp * drawable.speedScale;
                    this.x += this.vecX * f2;
                    this.y += this.vecY * f2;
                    float f3 = this.inProgress;
                    if (f3 != 1.0f) {
                        float access$400 = f3 + (drawable.dt / 200.0f);
                        this.inProgress = access$400;
                        if (access$400 > 1.0f) {
                            this.inProgress = 1.0f;
                        }
                    }
                }
            }

            public void genPosition(long j) {
                this.starIndex = Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.stars.length);
                this.lifeTime = j + Drawable.this.minLifeTime + ((long) Utilities.fastRandom.nextInt(1000));
                float f = 0.0f;
                float f2 = 0.0f;
                float f3 = 0.0f;
                for (int i = 0; i < 10; i++) {
                    float abs = Drawable.this.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                    float abs2 = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                    float f4 = 2.14748365E9f;
                    for (int i2 = 0; i2 < Drawable.this.particles.size(); i2++) {
                        float f5 = Drawable.this.particles.get(i2).x - abs;
                        float f6 = Drawable.this.particles.get(i2).y - abs2;
                        float f7 = (f5 * f5) + (f6 * f6);
                        if (f7 < f4) {
                            f4 = f7;
                        }
                    }
                    if (f4 > f3) {
                        f = abs;
                        f2 = abs2;
                        f3 = f4;
                    }
                }
                this.x = f;
                this.y = f2;
                double atan2 = Math.atan2((double) (f - Drawable.this.rect.centerX()), (double) (this.y - Drawable.this.rect.centerY()));
                this.vecX = (float) Math.sin(atan2);
                this.vecY = (float) Math.cos(atan2);
                this.alpha = (int) ((((float) (Utilities.fastRandom.nextInt(50) + 50)) / 100.0f) * 255.0f);
                this.inProgress = 0.0f;
            }
        }
    }

    public void setPaused(boolean z) {
        Drawable drawable2 = this.drawable;
        if (z != drawable2.paused) {
            drawable2.paused = z;
            if (z) {
                drawable2.pausedTime = System.currentTimeMillis();
                return;
            }
            for (int i = 0; i < this.drawable.particles.size(); i++) {
                Drawable.Particle.access$114(this.drawable.particles.get(i), System.currentTimeMillis() - this.drawable.pausedTime);
            }
            invalidate();
        }
    }
}
