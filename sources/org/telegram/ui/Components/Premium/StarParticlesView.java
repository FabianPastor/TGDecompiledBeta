package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.GLIconSettingsView;

public class StarParticlesView extends View {
    public static final int TYPE_APP_ICON_REACT = 1001;
    public static final int TYPE_APP_ICON_STAR_PREMIUM = 1002;
    public Drawable drawable;
    int size;

    public StarParticlesView(Context context) {
        super(context);
        int particlesCount = 50;
        if (SharedConfig.getDevicePerformanceClass() == 2) {
            particlesCount = 200;
        } else if (SharedConfig.getDevicePerformanceClass() == 1) {
            particlesCount = 100;
        }
        Drawable drawable2 = new Drawable(particlesCount);
        this.drawable = drawable2;
        drawable2.type = 100;
        this.drawable.roundEffect = true;
        this.drawable.useRotate = true;
        this.drawable.useBlur = true;
        this.drawable.checkBounds = true;
        this.drawable.size1 = 4;
        Drawable drawable3 = this.drawable;
        drawable3.k3 = 0.98f;
        drawable3.k2 = 0.98f;
        drawable3.k1 = 0.98f;
        this.drawable.init();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeInternal = getMeasuredWidth() << (getMeasuredHeight() + 16);
        this.drawable.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(140.0f), (float) AndroidUtilities.dp(140.0f));
        this.drawable.rect.offset((((float) getMeasuredWidth()) - this.drawable.rect.width()) / 2.0f, (((float) getMeasuredHeight()) - this.drawable.rect.height()) / 2.0f);
        this.drawable.rect2.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        if (this.size != sizeInternal) {
            this.size = sizeInternal;
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

    public void flingParticles(float sum) {
        float maxSpeed = 15.0f;
        if (sum < 60.0f) {
            maxSpeed = 5.0f;
        } else if (sum < 180.0f) {
            maxSpeed = 9.0f;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator.AnimatorUpdateListener updateListener = new StarParticlesView$$ExternalSyntheticLambda0(this);
        ValueAnimator a1 = ValueAnimator.ofFloat(new float[]{1.0f, maxSpeed});
        a1.addUpdateListener(updateListener);
        a1.setDuration(600);
        ValueAnimator a2 = ValueAnimator.ofFloat(new float[]{maxSpeed, 1.0f});
        a2.addUpdateListener(updateListener);
        a2.setDuration(2000);
        animatorSet.playTogether(new Animator[]{a1, a2});
        animatorSet.start();
    }

    /* renamed from: lambda$flingParticles$0$org-telegram-ui-Components-Premium-StarParticlesView  reason: not valid java name */
    public /* synthetic */ void m1257x5530cCLASSNAME(ValueAnimator animation) {
        this.drawable.speedScale = ((Float) animation.getAnimatedValue()).floatValue();
    }

    public static class Drawable {
        public static final int TYPE_SETTINGS = 101;
        float a;
        float a1;
        float a2;
        public boolean checkBounds;
        public boolean checkTime;
        public String colorKey;
        public final int count;
        /* access modifiers changed from: private */
        public boolean distributionAlgorithm;
        /* access modifiers changed from: private */
        public final float dt = (1000.0f / AndroidUtilities.screenRefreshRate);
        public RectF excludeRect = new RectF();
        public boolean isCircle;
        public float k1 = 0.85f;
        public float k2 = 0.85f;
        public float k3 = 0.9f;
        private int lastColor;
        Matrix matrix = new Matrix();
        Matrix matrix2 = new Matrix();
        Matrix matrix3 = new Matrix();
        public long minLifeTime = 2000;
        /* access modifiers changed from: private */
        public Paint paint = new Paint();
        ArrayList<Particle> particles = new ArrayList<>();
        public boolean paused;
        long pausedTime;
        float[] points1;
        float[] points2;
        float[] points3;
        int pointsCount1;
        int pointsCount2;
        int pointsCount3;
        public int randLifeTime = 1000;
        public RectF rect = new RectF();
        public RectF rect2 = new RectF();
        public boolean roundEffect;
        public int size1 = 14;
        public int size2 = 12;
        public int size3 = 10;
        public float speedScale = 1.0f;
        /* access modifiers changed from: private */
        public final Bitmap[] stars = new Bitmap[3];
        public boolean startFromCenter;
        public boolean svg;
        public int type;
        public boolean useBlur;
        public boolean useGradient;
        public boolean useRotate;

        public Drawable(int count2) {
            boolean z = false;
            this.checkBounds = false;
            this.checkTime = true;
            this.isCircle = true;
            this.useBlur = false;
            this.roundEffect = true;
            this.type = -1;
            this.colorKey = "premiumStartSmallStarsColor";
            this.count = count2;
            this.distributionAlgorithm = count2 < 50 ? true : z;
        }

        public void init() {
            if (this.useRotate) {
                int i = this.count;
                this.points1 = new float[(i * 2)];
                this.points2 = new float[(i * 2)];
                this.points3 = new float[(i * 2)];
            }
            generateBitmaps();
            if (this.particles.isEmpty()) {
                for (int i2 = 0; i2 < this.count; i2++) {
                    this.particles.add(new Particle());
                }
            }
        }

        public void updateColors() {
            int c = Theme.getColor(this.colorKey);
            if (this.lastColor != c) {
                this.lastColor = c;
                generateBitmaps();
            }
        }

        private void generateBitmaps() {
            int size;
            int res;
            int res2;
            int res3;
            for (int i = 0; i < 3; i++) {
                float k = this.k1;
                if (i == 0) {
                    size = AndroidUtilities.dp((float) this.size1);
                } else if (i == 1) {
                    k = this.k2;
                    size = AndroidUtilities.dp((float) this.size2);
                } else {
                    k = this.k3;
                    size = AndroidUtilities.dp((float) this.size3);
                }
                int i2 = this.type;
                if (i2 == 9) {
                    if (i == 0) {
                        res3 = NUM;
                    } else if (i == 1) {
                        res3 = NUM;
                    } else {
                        res3 = NUM;
                    }
                    this.stars[i] = SvgHelper.getBitmap(res3, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 3) {
                    if (i == 0) {
                        res2 = NUM;
                    } else if (i == 1) {
                        res2 = NUM;
                    } else {
                        res2 = NUM;
                    }
                    this.stars[i] = SvgHelper.getBitmap(res2, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 7) {
                    if (i == 0) {
                        res = NUM;
                    } else if (i == 1) {
                        res = NUM;
                    } else {
                        res = NUM;
                    }
                    this.stars[i] = SvgHelper.getBitmap(res, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 1001) {
                    this.stars[i] = SvgHelper.getBitmap(NUM, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 1002) {
                    this.stars[i] = SvgHelper.getBitmap(NUM, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else {
                    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    this.stars[i] = bitmap;
                    Canvas canvas = new Canvas(bitmap);
                    if (this.type == 6 && (i == 1 || i == 2)) {
                        android.graphics.drawable.Drawable drawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, NUM);
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.colorKey), PorterDuff.Mode.MULTIPLY));
                        drawable.setBounds(0, 0, size, size);
                        drawable.draw(canvas);
                    } else {
                        Path path = new Path();
                        int sizeHalf = size >> 1;
                        int mid = (int) (((float) sizeHalf) * k);
                        path.moveTo(0.0f, (float) sizeHalf);
                        path.lineTo((float) mid, (float) mid);
                        path.lineTo((float) sizeHalf, 0.0f);
                        path.lineTo((float) (size - mid), (float) mid);
                        path.lineTo((float) size, (float) sizeHalf);
                        path.lineTo((float) (size - mid), (float) (size - mid));
                        path.lineTo((float) sizeHalf, (float) size);
                        path.lineTo((float) mid, (float) (size - mid));
                        path.lineTo(0.0f, (float) sizeHalf);
                        path.close();
                        Paint paint2 = new Paint();
                        if (this.useGradient) {
                            if (size >= AndroidUtilities.dp(10.0f)) {
                                Paint paint3 = paint2;
                                float f = (float) (size * -2);
                                int i3 = mid;
                                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, size, size, f, 0.0f);
                            } else {
                                int i4 = mid;
                                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, size, size, (float) (size * -4), 0.0f);
                            }
                            Paint paint1 = PremiumGradient.getInstance().getMainGradientPaint();
                            if (this.roundEffect) {
                                paint1.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(((float) this.size1) / 5.0f)));
                            }
                            if (this.useBlur) {
                                paint1.setAlpha(60);
                            } else {
                                paint1.setAlpha(120);
                            }
                            canvas.drawPath(path, paint1);
                            paint1.setPathEffect((PathEffect) null);
                            paint1.setAlpha(255);
                        } else {
                            Paint paint4 = paint2;
                            int i5 = mid;
                            if (this.type == 100) {
                                paint4.setColor(ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 200));
                            } else {
                                paint4.setColor(Theme.getColor(this.colorKey));
                            }
                            if (this.roundEffect) {
                                paint4.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(((float) this.size1) / 5.0f)));
                            }
                            canvas.drawPath(path, paint4);
                        }
                        if (this.useBlur) {
                            Utilities.stackBlurBitmap(bitmap, 2);
                        }
                    }
                }
            }
        }

        public void resetPositions() {
            long time = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                this.particles.get(i).genPosition(time);
            }
        }

        public void onDraw(Canvas canvas) {
            onDraw(canvas, 1.0f);
        }

        public void onDraw(Canvas canvas, float alpha) {
            Canvas canvas2 = canvas;
            float f = alpha;
            long time = System.currentTimeMillis();
            if (this.useRotate) {
                this.matrix.reset();
                float f2 = this.a + 0.144f;
                this.a = f2;
                this.a1 += 0.1152f;
                this.a2 += 0.096f;
                this.matrix.setRotate(f2, this.rect.centerX(), this.rect.centerY());
                this.matrix2.setRotate(this.a1, this.rect.centerX(), this.rect.centerY());
                this.matrix3.setRotate(this.a2, this.rect.centerX(), this.rect.centerY());
                this.pointsCount1 = 0;
                this.pointsCount2 = 0;
                this.pointsCount3 = 0;
                for (int i = 0; i < this.particles.size(); i++) {
                    this.particles.get(i).updatePoint();
                }
                Matrix matrix4 = this.matrix;
                float[] fArr = this.points1;
                matrix4.mapPoints(fArr, 0, fArr, 0, this.pointsCount1);
                Matrix matrix5 = this.matrix2;
                float[] fArr2 = this.points2;
                matrix5.mapPoints(fArr2, 0, fArr2, 0, this.pointsCount2);
                Matrix matrix6 = this.matrix3;
                float[] fArr3 = this.points3;
                matrix6.mapPoints(fArr3, 0, fArr3, 0, this.pointsCount3);
                this.pointsCount1 = 0;
                this.pointsCount2 = 0;
                this.pointsCount3 = 0;
            }
            for (int i2 = 0; i2 < this.particles.size(); i2++) {
                Particle particle = this.particles.get(i2);
                if (this.paused) {
                    particle.draw(canvas2, this.pausedTime, f);
                } else {
                    particle.draw(canvas2, time, f);
                }
                if (this.checkTime && time > particle.lifeTime) {
                    particle.genPosition(time);
                }
                if (this.checkBounds && !this.rect2.contains(particle.drawingX, particle.drawingY)) {
                    particle.genPosition(time);
                }
            }
        }

        private class Particle {
            private int alpha;
            /* access modifiers changed from: private */
            public float drawingX;
            /* access modifiers changed from: private */
            public float drawingY;
            float inProgress;
            /* access modifiers changed from: private */
            public long lifeTime;
            private float randomRotate;
            private int starIndex;
            private float vecX;
            private float vecY;
            private float x;
            private float x2;
            private float y;
            private float y2;

            private Particle() {
            }

            static /* synthetic */ long access$114(Particle x0, long x1) {
                long j = x0.lifeTime + x1;
                x0.lifeTime = j;
                return j;
            }

            public void updatePoint() {
                int i = this.starIndex;
                if (i == 0) {
                    Drawable.this.points1[Drawable.this.pointsCount1 * 2] = this.x;
                    Drawable.this.points1[(Drawable.this.pointsCount1 * 2) + 1] = this.y;
                    Drawable.this.pointsCount1++;
                } else if (i == 1) {
                    Drawable.this.points2[Drawable.this.pointsCount2 * 2] = this.x;
                    Drawable.this.points2[(Drawable.this.pointsCount2 * 2) + 1] = this.y;
                    Drawable.this.pointsCount2++;
                } else if (i == 2) {
                    Drawable.this.points3[Drawable.this.pointsCount3 * 2] = this.x;
                    Drawable.this.points3[(Drawable.this.pointsCount3 * 2) + 1] = this.y;
                    Drawable.this.pointsCount3++;
                }
            }

            public void draw(Canvas canvas, long time, float alpha2) {
                Canvas canvas2 = canvas;
                if (Drawable.this.useRotate) {
                    int i = this.starIndex;
                    if (i == 0) {
                        this.drawingX = Drawable.this.points1[Drawable.this.pointsCount1 * 2];
                        this.drawingY = Drawable.this.points1[(Drawable.this.pointsCount1 * 2) + 1];
                        Drawable.this.pointsCount1++;
                    } else if (i == 1) {
                        this.drawingX = Drawable.this.points2[Drawable.this.pointsCount2 * 2];
                        this.drawingY = Drawable.this.points2[(Drawable.this.pointsCount2 * 2) + 1];
                        Drawable.this.pointsCount2++;
                    } else if (i == 2) {
                        this.drawingX = Drawable.this.points3[Drawable.this.pointsCount3 * 2];
                        this.drawingY = Drawable.this.points3[(Drawable.this.pointsCount3 * 2) + 1];
                        Drawable.this.pointsCount3++;
                    }
                } else {
                    this.drawingX = this.x;
                    this.drawingY = this.y;
                }
                boolean skipDraw = false;
                if (!Drawable.this.excludeRect.isEmpty() && Drawable.this.excludeRect.contains(this.drawingX, this.drawingY)) {
                    skipDraw = true;
                }
                if (!skipDraw) {
                    canvas.save();
                    canvas.translate(this.drawingX, this.drawingY);
                    float f = this.randomRotate;
                    if (f != 0.0f) {
                        canvas.rotate(f, ((float) Drawable.this.stars[this.starIndex].getWidth()) / 2.0f, ((float) Drawable.this.stars[this.starIndex].getHeight()) / 2.0f);
                    }
                    if (this.inProgress < 1.0f || GLIconSettingsView.smallStarsSize != 1.0f) {
                        float s = AndroidUtilities.overshootInterpolator.getInterpolation(this.inProgress) * GLIconSettingsView.smallStarsSize;
                        canvas.scale(s, s, 0.0f, 0.0f);
                    }
                    float outProgress = 0.0f;
                    if (Drawable.this.checkTime) {
                        long j = this.lifeTime;
                        if (j - time < 200) {
                            outProgress = Utilities.clamp(1.0f - (((float) (j - time)) / 150.0f), 1.0f, 0.0f);
                        }
                    }
                    Drawable.this.paint.setAlpha((int) (((float) this.alpha) * (1.0f - outProgress) * alpha2));
                    canvas.drawBitmap(Drawable.this.stars[this.starIndex], (float) (-(Drawable.this.stars[this.starIndex].getWidth() >> 1)), (float) (-(Drawable.this.stars[this.starIndex].getHeight() >> 1)), Drawable.this.paint);
                    canvas.restore();
                }
                if (!Drawable.this.paused) {
                    float speed = ((float) AndroidUtilities.dp(4.0f)) * (Drawable.this.dt / 660.0f) * Drawable.this.speedScale;
                    this.x += this.vecX * speed;
                    this.y += this.vecY * speed;
                    float f2 = this.inProgress;
                    if (f2 != 1.0f) {
                        float access$600 = f2 + (Drawable.this.dt / 200.0f);
                        this.inProgress = access$600;
                        if (access$600 > 1.0f) {
                            this.inProgress = 1.0f;
                        }
                    }
                }
            }

            public void genPosition(long time) {
                int i;
                float ry;
                float rx;
                this.starIndex = Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.stars.length);
                this.lifeTime = Drawable.this.minLifeTime + time + ((long) Utilities.fastRandom.nextInt(Drawable.this.randLifeTime));
                this.randomRotate = 0.0f;
                if (Drawable.this.distributionAlgorithm) {
                    float bestDistance = 0.0f;
                    float bestX = Drawable.this.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                    float bestY = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                    for (int k = 0; k < 10; k++) {
                        float randX = Drawable.this.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                        float randY = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                        float minDistance = 2.14748365E9f;
                        for (int j = 0; j < Drawable.this.particles.size(); j++) {
                            if (Drawable.this.startFromCenter) {
                                rx = Drawable.this.particles.get(j).x2 - randX;
                                ry = Drawable.this.particles.get(j).y2 - randY;
                            } else {
                                rx = Drawable.this.particles.get(j).x - randX;
                                ry = Drawable.this.particles.get(j).y - randY;
                            }
                            float distance = (rx * rx) + (ry * ry);
                            if (distance < minDistance) {
                                minDistance = distance;
                            }
                        }
                        if (minDistance > bestDistance) {
                            bestDistance = minDistance;
                            bestX = randX;
                            bestY = randY;
                        }
                    }
                    this.x = bestX;
                    this.y = bestY;
                } else if (Drawable.this.isCircle) {
                    float r = (((float) Math.abs(Utilities.fastRandom.nextInt() % 1000)) / 1000.0f) * Drawable.this.rect.width();
                    float a = (float) Math.abs(Utilities.fastRandom.nextInt() % 360);
                    float centerX = Drawable.this.rect.centerX();
                    double d = (double) r;
                    double sin = Math.sin(Math.toRadians((double) a));
                    Double.isNaN(d);
                    this.x = centerX + ((float) (d * sin));
                    float centerY = Drawable.this.rect.centerY();
                    double d2 = (double) r;
                    double cos = Math.cos(Math.toRadians((double) a));
                    Double.isNaN(d2);
                    this.y = centerY + ((float) (d2 * cos));
                } else {
                    this.x = Drawable.this.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                    this.y = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                }
                double a2 = Math.atan2((double) (this.x - Drawable.this.rect.centerX()), (double) (this.y - Drawable.this.rect.centerY()));
                this.vecX = (float) Math.sin(a2);
                this.vecY = (float) Math.cos(a2);
                if (Drawable.this.svg) {
                    this.alpha = (int) ((((float) (Utilities.fastRandom.nextInt(50) + 50)) / 100.0f) * 120.0f);
                } else {
                    this.alpha = (int) ((((float) (Utilities.fastRandom.nextInt(50) + 50)) / 100.0f) * 255.0f);
                }
                if ((Drawable.this.type == 6 && ((i = this.starIndex) == 1 || i == 2)) || Drawable.this.type == 9 || Drawable.this.type == 3 || Drawable.this.type == 7) {
                    this.randomRotate = (float) ((int) ((((float) (Utilities.fastRandom.nextInt() % 100)) / 100.0f) * 45.0f));
                }
                if (Drawable.this.type != 101) {
                    this.inProgress = 0.0f;
                }
                if (Drawable.this.startFromCenter) {
                    this.x2 = this.x;
                    this.y2 = this.y;
                    this.x = Drawable.this.rect.centerX();
                    this.y = Drawable.this.rect.centerY();
                }
            }
        }
    }

    public void setPaused(boolean paused) {
        if (paused != this.drawable.paused) {
            this.drawable.paused = paused;
            if (paused) {
                this.drawable.pausedTime = System.currentTimeMillis();
                return;
            }
            for (int i = 0; i < this.drawable.particles.size(); i++) {
                Drawable.Particle.access$114(this.drawable.particles.get(i), System.currentTimeMillis() - this.drawable.pausedTime);
            }
            invalidate();
        }
    }
}
