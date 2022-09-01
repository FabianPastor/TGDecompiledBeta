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
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.GLIconSettingsView;

public class StarParticlesView extends View {
    public Drawable drawable;
    int size;

    public StarParticlesView(Context context) {
        super(context);
        int i;
        if (SharedConfig.getDevicePerformanceClass() == 2) {
            i = 200;
        } else {
            i = SharedConfig.getDevicePerformanceClass() == 1 ? 100 : 50;
        }
        Drawable drawable2 = new Drawable(i);
        this.drawable = drawable2;
        drawable2.type = 100;
        drawable2.roundEffect = true;
        drawable2.useRotate = true;
        drawable2.useBlur = true;
        drawable2.checkBounds = true;
        drawable2.size1 = 4;
        drawable2.k3 = 0.98f;
        drawable2.k2 = 0.98f;
        drawable2.k1 = 0.98f;
        drawable2.init();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth() << (getMeasuredHeight() + 16);
        this.drawable.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(140.0f), (float) AndroidUtilities.dp(140.0f));
        this.drawable.rect.offset((((float) getMeasuredWidth()) - this.drawable.rect.width()) / 2.0f, (((float) getMeasuredHeight()) - this.drawable.rect.height()) / 2.0f);
        this.drawable.rect2.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        if (this.size != measuredWidth) {
            this.size = measuredWidth;
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
        public boolean forceMaxAlpha;
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
        public ArrayList<Particle> particles = new ArrayList<>();
        public boolean paused;
        public long pausedTime;
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

        public Drawable(int i) {
            boolean z = false;
            this.checkBounds = false;
            this.checkTime = true;
            this.isCircle = true;
            this.useBlur = false;
            this.forceMaxAlpha = false;
            this.roundEffect = true;
            this.type = -1;
            this.colorKey = "premiumStartSmallStarsColor";
            this.count = i;
            this.distributionAlgorithm = i < 50 ? true : z;
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
            int color = Theme.getColor(this.colorKey);
            if (this.lastColor != color) {
                this.lastColor = color;
                generateBitmaps();
            }
        }

        private void generateBitmaps() {
            int dp;
            int i;
            int i2;
            int i3;
            int i4;
            for (int i5 = 0; i5 < 3; i5++) {
                float f = this.k1;
                if (i5 == 0) {
                    dp = AndroidUtilities.dp((float) this.size1);
                } else if (i5 == 1) {
                    f = this.k2;
                    dp = AndroidUtilities.dp((float) this.size2);
                } else {
                    f = this.k3;
                    dp = AndroidUtilities.dp((float) this.size3);
                }
                int i6 = dp;
                int i7 = this.type;
                if (i7 == 9) {
                    if (i5 == 0) {
                        i4 = R.raw.premium_object_folder;
                    } else if (i5 == 1) {
                        i4 = R.raw.premium_object_bubble;
                    } else {
                        i4 = R.raw.premium_object_settings;
                    }
                    this.stars[i5] = SvgHelper.getBitmap(i4, i6, i6, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i7 == 11) {
                    if (i5 == 0) {
                        i3 = R.raw.premium_object_smile1;
                    } else if (i5 == 1) {
                        i3 = R.raw.premium_object_smile2;
                    } else {
                        i3 = R.raw.premium_object_like;
                    }
                    this.stars[i5] = SvgHelper.getBitmap(i3, i6, i6, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i7 == 3) {
                    if (i5 == 0) {
                        i2 = R.raw.premium_object_adsbubble;
                    } else if (i5 == 1) {
                        i2 = R.raw.premium_object_like;
                    } else {
                        i2 = R.raw.premium_object_noads;
                    }
                    this.stars[i5] = SvgHelper.getBitmap(i2, i6, i6, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i7 == 7) {
                    if (i5 == 0) {
                        i = R.raw.premium_object_video2;
                    } else if (i5 == 1) {
                        i = R.raw.premium_object_video;
                    } else {
                        i = R.raw.premium_object_user;
                    }
                    this.stars[i5] = SvgHelper.getBitmap(i, i6, i6, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i7 == 1001) {
                    this.stars[i5] = SvgHelper.getBitmap(R.raw.premium_object_fire, i6, i6, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i7 == 1002) {
                    this.stars[i5] = SvgHelper.getBitmap(R.raw.premium_object_star2, i6, i6, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else {
                    Bitmap createBitmap = Bitmap.createBitmap(i6, i6, Bitmap.Config.ARGB_8888);
                    this.stars[i5] = createBitmap;
                    Canvas canvas = new Canvas(createBitmap);
                    if (this.type == 6 && (i5 == 1 || i5 == 2)) {
                        android.graphics.drawable.Drawable drawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_liststar);
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.colorKey), PorterDuff.Mode.MULTIPLY));
                        drawable.setBounds(0, 0, i6, i6);
                        drawable.draw(canvas);
                    } else {
                        Path path = new Path();
                        float f2 = (float) (i6 >> 1);
                        int i8 = (int) (f * f2);
                        path.moveTo(0.0f, f2);
                        float f3 = (float) i8;
                        path.lineTo(f3, f3);
                        path.lineTo(f2, 0.0f);
                        float f4 = (float) (i6 - i8);
                        path.lineTo(f4, f3);
                        float f5 = (float) i6;
                        path.lineTo(f5, f2);
                        path.lineTo(f4, f4);
                        path.lineTo(f2, f5);
                        path.lineTo(f3, f4);
                        path.lineTo(0.0f, f2);
                        path.close();
                        Paint paint2 = new Paint();
                        if (this.useGradient) {
                            if (i6 >= AndroidUtilities.dp(10.0f)) {
                                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, i6, i6, (float) (i6 * -2), 0.0f);
                            } else {
                                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, i6, i6, (float) (i6 * -4), 0.0f);
                            }
                            Paint mainGradientPaint = PremiumGradient.getInstance().getMainGradientPaint();
                            if (this.roundEffect) {
                                mainGradientPaint.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(((float) this.size1) / 5.0f)));
                            }
                            if (this.forceMaxAlpha) {
                                mainGradientPaint.setAlpha(255);
                            } else if (this.useBlur) {
                                mainGradientPaint.setAlpha(60);
                            } else {
                                mainGradientPaint.setAlpha(120);
                            }
                            canvas.drawPath(path, mainGradientPaint);
                            mainGradientPaint.setPathEffect((PathEffect) null);
                            mainGradientPaint.setAlpha(255);
                        } else {
                            if (this.type == 100) {
                                paint2.setColor(ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 200));
                            } else {
                                paint2.setColor(Theme.getColor(this.colorKey));
                            }
                            if (this.roundEffect) {
                                paint2.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(((float) this.size1) / 5.0f)));
                            }
                            canvas.drawPath(path, paint2);
                        }
                        if (this.useBlur) {
                            Utilities.stackBlurBitmap(createBitmap, 2);
                        }
                    }
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
            onDraw(canvas, 1.0f);
        }

        public void onDraw(Canvas canvas, float f) {
            Canvas canvas2 = canvas;
            float f2 = f;
            long currentTimeMillis = System.currentTimeMillis();
            if (this.useRotate) {
                this.matrix.reset();
                float f3 = this.a + 0.144f;
                this.a = f3;
                this.a1 += 0.1152f;
                this.a2 += 0.096f;
                this.matrix.setRotate(f3, this.rect.centerX(), this.rect.centerY());
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
                    particle.draw(canvas2, this.pausedTime, f2);
                } else {
                    particle.draw(canvas2, currentTimeMillis, f2);
                }
                if (this.checkTime && currentTimeMillis > particle.lifeTime) {
                    particle.genPosition(currentTimeMillis);
                }
                if (this.checkBounds && !this.rect2.contains(particle.drawingX, particle.drawingY)) {
                    particle.genPosition(currentTimeMillis);
                }
            }
        }

        public class Particle {
            private int alpha;
            /* access modifiers changed from: private */
            public float drawingX;
            /* access modifiers changed from: private */
            public float drawingY;
            float inProgress;
            public long lifeTime;
            private float randomRotate;
            private int starIndex;
            private float vecX;
            private float vecY;
            private float x;
            private float x2;
            private float y;
            private float y2;

            public Particle() {
            }

            public void updatePoint() {
                int i = this.starIndex;
                if (i == 0) {
                    Drawable drawable = Drawable.this;
                    float[] fArr = drawable.points1;
                    int i2 = drawable.pointsCount1;
                    fArr[i2 * 2] = this.x;
                    fArr[(i2 * 2) + 1] = this.y;
                    drawable.pointsCount1 = i2 + 1;
                } else if (i == 1) {
                    Drawable drawable2 = Drawable.this;
                    float[] fArr2 = drawable2.points2;
                    int i3 = drawable2.pointsCount2;
                    fArr2[i3 * 2] = this.x;
                    fArr2[(i3 * 2) + 1] = this.y;
                    drawable2.pointsCount2 = i3 + 1;
                } else if (i == 2) {
                    Drawable drawable3 = Drawable.this;
                    float[] fArr3 = drawable3.points3;
                    int i4 = drawable3.pointsCount3;
                    fArr3[i4 * 2] = this.x;
                    fArr3[(i4 * 2) + 1] = this.y;
                    drawable3.pointsCount3 = i4 + 1;
                }
            }

            public void draw(Canvas canvas, long j, float f) {
                Drawable drawable = Drawable.this;
                if (drawable.useRotate) {
                    int i = this.starIndex;
                    if (i == 0) {
                        float[] fArr = drawable.points1;
                        int i2 = drawable.pointsCount1;
                        this.drawingX = fArr[i2 * 2];
                        this.drawingY = fArr[(i2 * 2) + 1];
                        drawable.pointsCount1 = i2 + 1;
                    } else if (i == 1) {
                        float[] fArr2 = drawable.points2;
                        int i3 = drawable.pointsCount2;
                        this.drawingX = fArr2[i3 * 2];
                        this.drawingY = fArr2[(i3 * 2) + 1];
                        drawable.pointsCount2 = i3 + 1;
                    } else if (i == 2) {
                        float[] fArr3 = drawable.points3;
                        int i4 = drawable.pointsCount3;
                        this.drawingX = fArr3[i4 * 2];
                        this.drawingY = fArr3[(i4 * 2) + 1];
                        drawable.pointsCount3 = i4 + 1;
                    }
                } else {
                    this.drawingX = this.x;
                    this.drawingY = this.y;
                }
                boolean z = false;
                if (!drawable.excludeRect.isEmpty() && Drawable.this.excludeRect.contains(this.drawingX, this.drawingY)) {
                    z = true;
                }
                if (!z) {
                    canvas.save();
                    canvas.translate(this.drawingX, this.drawingY);
                    float f2 = this.randomRotate;
                    float f3 = 0.0f;
                    if (f2 != 0.0f) {
                        canvas.rotate(f2, ((float) Drawable.this.stars[this.starIndex].getWidth()) / 2.0f, ((float) Drawable.this.stars[this.starIndex].getHeight()) / 2.0f);
                    }
                    float f4 = this.inProgress;
                    if (f4 < 1.0f || GLIconSettingsView.smallStarsSize != 1.0f) {
                        float interpolation = AndroidUtilities.overshootInterpolator.getInterpolation(f4) * GLIconSettingsView.smallStarsSize;
                        canvas.scale(interpolation, interpolation, 0.0f, 0.0f);
                    }
                    if (Drawable.this.checkTime) {
                        long j2 = this.lifeTime;
                        if (j2 - j < 200) {
                            f3 = Utilities.clamp(1.0f - (((float) (j2 - j)) / 150.0f), 1.0f, 0.0f);
                        }
                    }
                    Drawable.this.paint.setAlpha((int) (((float) this.alpha) * (1.0f - f3) * f));
                    canvas.drawBitmap(Drawable.this.stars[this.starIndex], (float) (-(Drawable.this.stars[this.starIndex].getWidth() >> 1)), (float) (-(Drawable.this.stars[this.starIndex].getHeight() >> 1)), Drawable.this.paint);
                    canvas.restore();
                }
                if (!Drawable.this.paused) {
                    float dp = ((float) AndroidUtilities.dp(4.0f)) * (Drawable.this.dt / 660.0f);
                    Drawable drawable2 = Drawable.this;
                    float f5 = dp * drawable2.speedScale;
                    this.x += this.vecX * f5;
                    this.y += this.vecY * f5;
                    float f6 = this.inProgress;
                    if (f6 != 1.0f) {
                        float access$400 = f6 + (drawable2.dt / 200.0f);
                        this.inProgress = access$400;
                        if (access$400 > 1.0f) {
                            this.inProgress = 1.0f;
                        }
                    }
                }
            }

            public void genPosition(long j) {
                int i;
                float f;
                float f2;
                this.starIndex = Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.stars.length);
                Drawable drawable = Drawable.this;
                this.lifeTime = j + drawable.minLifeTime + ((long) Utilities.fastRandom.nextInt(drawable.randLifeTime));
                this.randomRotate = 0.0f;
                if (Drawable.this.distributionAlgorithm) {
                    float abs = Drawable.this.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                    float abs2 = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                    float f3 = 0.0f;
                    for (int i2 = 0; i2 < 10; i2++) {
                        float abs3 = Drawable.this.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                        float abs4 = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                        float f4 = 2.14748365E9f;
                        for (int i3 = 0; i3 < Drawable.this.particles.size(); i3++) {
                            Drawable drawable2 = Drawable.this;
                            if (drawable2.startFromCenter) {
                                f2 = drawable2.particles.get(i3).x2 - abs3;
                                f = Drawable.this.particles.get(i3).y2;
                            } else {
                                f2 = drawable2.particles.get(i3).x - abs3;
                                f = Drawable.this.particles.get(i3).y;
                            }
                            float f5 = f - abs4;
                            float f6 = (f2 * f2) + (f5 * f5);
                            if (f6 < f4) {
                                f4 = f6;
                            }
                        }
                        if (f4 > f3) {
                            abs = abs3;
                            abs2 = abs4;
                            f3 = f4;
                        }
                    }
                    this.x = abs;
                    this.y = abs2;
                } else {
                    Drawable drawable3 = Drawable.this;
                    if (drawable3.isCircle) {
                        float abs5 = (((float) Math.abs(Utilities.fastRandom.nextInt() % 1000)) / 1000.0f) * Drawable.this.rect.width();
                        float centerX = Drawable.this.rect.centerX();
                        double d = (double) abs5;
                        double abs6 = (double) ((float) Math.abs(Utilities.fastRandom.nextInt() % 360));
                        double sin = Math.sin(Math.toRadians(abs6));
                        Double.isNaN(d);
                        this.x = centerX + ((float) (sin * d));
                        float centerY = Drawable.this.rect.centerY();
                        double cos = Math.cos(Math.toRadians(abs6));
                        Double.isNaN(d);
                        this.y = centerY + ((float) (d * cos));
                    } else {
                        this.x = drawable3.rect.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.width());
                        this.y = Drawable.this.rect.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % Drawable.this.rect.height());
                    }
                }
                double atan2 = Math.atan2((double) (this.x - Drawable.this.rect.centerX()), (double) (this.y - Drawable.this.rect.centerY()));
                this.vecX = (float) Math.sin(atan2);
                this.vecY = (float) Math.cos(atan2);
                if (Drawable.this.svg) {
                    this.alpha = (int) ((((float) (Utilities.fastRandom.nextInt(50) + 50)) / 100.0f) * 120.0f);
                } else {
                    this.alpha = (int) ((((float) (Utilities.fastRandom.nextInt(50) + 50)) / 100.0f) * 255.0f);
                }
                int i4 = Drawable.this.type;
                if ((i4 == 6 && ((i = this.starIndex) == 1 || i == 2)) || i4 == 9 || i4 == 3 || i4 == 7 || i4 == 11) {
                    this.randomRotate = (float) ((int) ((((float) (Utilities.fastRandom.nextInt() % 100)) / 100.0f) * 45.0f));
                }
                Drawable drawable4 = Drawable.this;
                if (drawable4.type != 101) {
                    this.inProgress = 0.0f;
                }
                if (drawable4.startFromCenter) {
                    this.x2 = this.x;
                    this.y2 = this.y;
                    this.x = drawable4.rect.centerX();
                    this.y = Drawable.this.rect.centerY();
                }
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
                this.drawable.particles.get(i).lifeTime += System.currentTimeMillis() - this.drawable.pausedTime;
            }
            invalidate();
        }
    }
}
