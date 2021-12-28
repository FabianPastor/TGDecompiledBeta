package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class SnowflakesEffect {
    /* access modifiers changed from: private */
    public Paint bitmapPaint = new Paint();
    private int color;
    private String colorKey = "actionBarDefaultTitle";
    private ArrayList<Particle> freeParticles = new ArrayList<>();
    private long lastAnimationTime;
    Bitmap particleBitmap;
    /* access modifiers changed from: private */
    public Paint particlePaint;
    /* access modifiers changed from: private */
    public Paint particleThinPaint;
    private ArrayList<Particle> particles = new ArrayList<>();
    private int viewType;

    private class Particle {
        float alpha;
        float currentTime;
        float lifeTime;
        float scale;
        int type;
        float velocity;
        float vx;
        float vy;
        float x;
        float y;

        private Particle() {
        }

        public void draw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (this.type != 0) {
                float f = -1.5707964f;
                SnowflakesEffect snowflakesEffect = SnowflakesEffect.this;
                if (snowflakesEffect.particleBitmap == null) {
                    snowflakesEffect.particleThinPaint.setAlpha(255);
                    SnowflakesEffect.this.particleBitmap = Bitmap.createBitmap(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas3 = new Canvas(SnowflakesEffect.this.particleBitmap);
                    float dpf2 = AndroidUtilities.dpf2(2.0f) * 2.0f;
                    float f2 = (-AndroidUtilities.dpf2(0.57f)) * 2.0f;
                    float dpvar_ = 2.0f * AndroidUtilities.dpf2(1.55f);
                    int i = 0;
                    while (i < 6) {
                        float dp = (float) AndroidUtilities.dp(8.0f);
                        float dp2 = (float) AndroidUtilities.dp(8.0f);
                        double d = (double) f;
                        float cos = ((float) Math.cos(d)) * dpf2;
                        float sin = ((float) Math.sin(d)) * dpf2;
                        float f3 = cos * 0.66f;
                        double d2 = d;
                        float f4 = dp2;
                        canvas3.drawLine(dp, dp2, dp + cos, dp2 + sin, SnowflakesEffect.this.particleThinPaint);
                        Double.isNaN(d2);
                        double d3 = (double) ((float) (d2 - 1.5707963267948966d));
                        double cos2 = Math.cos(d3);
                        double d4 = (double) f2;
                        Double.isNaN(d4);
                        double sin2 = Math.sin(d3);
                        Canvas canvas4 = canvas3;
                        double d5 = (double) dpvar_;
                        Double.isNaN(d5);
                        double sin3 = Math.sin(d3);
                        Double.isNaN(d4);
                        double cos3 = Math.cos(d3);
                        Double.isNaN(d5);
                        float f5 = dp + f3;
                        float f6 = f4 + (sin * 0.66f);
                        double d6 = d4;
                        double d7 = d3;
                        canvas4.drawLine(f5, f6, dp + ((float) ((cos2 * d4) - (sin2 * d5))), f4 + ((float) ((sin3 * d4) + (cos3 * d5))), SnowflakesEffect.this.particleThinPaint);
                        Double.isNaN(d6);
                        double sin4 = Math.sin(d7);
                        Double.isNaN(d5);
                        Double.isNaN(d6);
                        double cos4 = Math.cos(d7);
                        Double.isNaN(d5);
                        canvas4.drawLine(f5, f6, dp + ((float) (((-Math.cos(d7)) * d6) - (sin4 * d5))), f4 + ((float) (((-Math.sin(d7)) * d6) + (cos4 * d5))), SnowflakesEffect.this.particleThinPaint);
                        f += 1.0471976f;
                        i++;
                        canvas3 = canvas4;
                    }
                }
                SnowflakesEffect.this.bitmapPaint.setAlpha((int) (this.alpha * 255.0f));
                canvas.save();
                float f7 = this.scale;
                canvas2.scale(f7, f7, this.x, this.y);
                SnowflakesEffect snowflakesEffect2 = SnowflakesEffect.this;
                canvas2.drawBitmap(snowflakesEffect2.particleBitmap, this.x, this.y, snowflakesEffect2.bitmapPaint);
                canvas.restore();
                return;
            }
            SnowflakesEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
            canvas2.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
        }
    }

    public SnowflakesEffect(int i) {
        this.viewType = i;
        Paint paint = new Paint(1);
        this.particlePaint = paint;
        paint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        this.particlePaint.setStrokeCap(Paint.Cap.ROUND);
        this.particlePaint.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint(1);
        this.particleThinPaint = paint2;
        paint2.setStrokeWidth((float) AndroidUtilities.dp(0.5f));
        this.particleThinPaint.setStrokeCap(Paint.Cap.ROUND);
        this.particleThinPaint.setStyle(Paint.Style.STROKE);
        updateColors();
        for (int i2 = 0; i2 < 20; i2++) {
            this.freeParticles.add(new Particle());
        }
    }

    public void setColorKey(String str) {
        this.colorKey = str;
        updateColors();
    }

    public void updateColors() {
        int color2 = Theme.getColor(this.colorKey) & -1644826;
        if (this.color != color2) {
            this.color = color2;
            this.particlePaint.setColor(color2);
            this.particleThinPaint.setColor(color2);
        }
    }

    private void updateParticles(long j) {
        int size = this.particles.size();
        int i = 0;
        while (i < size) {
            Particle particle = this.particles.get(i);
            float f = particle.currentTime;
            float f2 = particle.lifeTime;
            if (f >= f2) {
                if (this.freeParticles.size() < 40) {
                    this.freeParticles.add(particle);
                }
                this.particles.remove(i);
                i--;
                size--;
            } else {
                if (this.viewType == 0) {
                    if (f < 200.0f) {
                        particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(f / 200.0f);
                    } else {
                        particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation((f - 200.0f) / (f2 - 200.0f));
                    }
                } else if (f < 200.0f) {
                    particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(f / 200.0f);
                } else if (f2 - f < 2000.0f) {
                    particle.alpha = AndroidUtilities.decelerateInterpolator.getInterpolation((f2 - f) / 2000.0f);
                }
                float f3 = particle.x;
                float f4 = particle.vx;
                float f5 = particle.velocity;
                float f6 = (float) j;
                particle.x = f3 + (((f4 * f5) * f6) / 500.0f);
                particle.y += ((particle.vy * f5) * f6) / 500.0f;
                particle.currentTime += f6;
            }
            i++;
        }
    }

    public void onDraw(View view, Canvas canvas) {
        Particle particle;
        if (view != null && canvas != null) {
            int size = this.particles.size();
            for (int i = 0; i < size; i++) {
                this.particles.get(i).draw(canvas);
            }
            int i2 = this.viewType;
            int i3 = i2 == 0 ? 100 : 300;
            int i4 = i2 == 0 ? 1 : 10;
            if (this.particles.size() < i3) {
                for (int i5 = 0; i5 < i4; i5++) {
                    if (this.particles.size() < i3 && Utilities.random.nextFloat() > 0.7f) {
                        int i6 = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                        float nextFloat = Utilities.random.nextFloat() * ((float) view.getMeasuredWidth());
                        float nextFloat2 = ((float) i6) + (Utilities.random.nextFloat() * ((float) ((view.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - i6)));
                        double nextInt = (double) ((Utilities.random.nextInt(40) - 20) + 90);
                        Double.isNaN(nextInt);
                        double d = nextInt * 0.017453292519943295d;
                        float cos = (float) Math.cos(d);
                        float sin = (float) Math.sin(d);
                        if (!this.freeParticles.isEmpty()) {
                            particle = this.freeParticles.get(0);
                            this.freeParticles.remove(0);
                        } else {
                            particle = new Particle();
                        }
                        particle.x = nextFloat;
                        particle.y = nextFloat2;
                        particle.vx = cos;
                        particle.vy = sin;
                        particle.alpha = 0.0f;
                        particle.currentTime = 0.0f;
                        particle.scale = Utilities.random.nextFloat() * 1.2f;
                        particle.type = Utilities.random.nextInt(2);
                        if (this.viewType == 0) {
                            particle.lifeTime = (float) (Utilities.random.nextInt(100) + 2000);
                        } else {
                            particle.lifeTime = (float) (Utilities.random.nextInt(2000) + 3000);
                        }
                        particle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                        this.particles.add(particle);
                    }
                }
            }
            long currentTimeMillis = System.currentTimeMillis();
            updateParticles(Math.min(17, currentTimeMillis - this.lastAnimationTime));
            this.lastAnimationTime = currentTimeMillis;
            view.invalidate();
        }
    }
}
