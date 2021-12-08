package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class SnowflakesEffect {
    final float angleDiff = 1.0471976f;
    private int color;
    private String colorKey = "actionBarDefaultTitle";
    private ArrayList<Particle> freeParticles = new ArrayList<>();
    private long lastAnimationTime;
    /* access modifiers changed from: private */
    public Paint particlePaint;
    /* access modifiers changed from: private */
    public Paint particleThinPaint;
    private ArrayList<Particle> particles = new ArrayList<>();

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
            switch (this.type) {
                case 0:
                    SnowflakesEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
                    canvas.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
                    return;
                default:
                    Canvas canvas2 = canvas;
                    SnowflakesEffect.this.particleThinPaint.setAlpha((int) (this.alpha * 255.0f));
                    float angle = -1.5707964f;
                    float px = AndroidUtilities.dpf2(2.0f) * 2.0f * this.scale;
                    float px1 = (-AndroidUtilities.dpf2(0.57f)) * 2.0f * this.scale;
                    float py1 = this.scale * AndroidUtilities.dpf2(1.55f) * 2.0f;
                    for (int a = 0; a < 6; a++) {
                        float x1 = ((float) Math.cos((double) angle)) * px;
                        float y1 = ((float) Math.sin((double) angle)) * px;
                        float cx = x1 * 0.66f;
                        float cy = y1 * 0.66f;
                        float f = this.x;
                        float f2 = this.y;
                        canvas.drawLine(f, f2, f + x1, f2 + y1, SnowflakesEffect.this.particleThinPaint);
                        double d = (double) angle;
                        Double.isNaN(d);
                        float angle2 = (float) (d - 1.5707963267948966d);
                        double cos = Math.cos((double) angle2);
                        double d2 = (double) px1;
                        Double.isNaN(d2);
                        double d3 = cos * d2;
                        double sin = Math.sin((double) angle2);
                        float f3 = x1;
                        float f4 = y1;
                        double d4 = (double) py1;
                        Double.isNaN(d4);
                        float x12 = (float) (d3 - (sin * d4));
                        double sin2 = Math.sin((double) angle2);
                        double d5 = (double) px1;
                        Double.isNaN(d5);
                        double d6 = sin2 * d5;
                        double cos2 = Math.cos((double) angle2);
                        float angle22 = angle2;
                        double d7 = (double) py1;
                        Double.isNaN(d7);
                        float y12 = (float) (d6 + (cos2 * d7));
                        float f5 = this.x;
                        float f6 = this.y;
                        float f7 = f6 + y12;
                        float f8 = f7;
                        float f9 = y12;
                        canvas.drawLine(f5 + cx, f6 + cy, f5 + x12, f8, SnowflakesEffect.this.particleThinPaint);
                        double d8 = (double) px1;
                        Double.isNaN(d8);
                        double d9 = (-Math.cos((double) angle22)) * d8;
                        double sin3 = Math.sin((double) angle22);
                        double d10 = (double) py1;
                        Double.isNaN(d10);
                        float x13 = (float) (d9 - (sin3 * d10));
                        double d11 = (double) px1;
                        Double.isNaN(d11);
                        double d12 = (-Math.sin((double) angle22)) * d11;
                        double cos3 = Math.cos((double) angle22);
                        double d13 = (double) py1;
                        Double.isNaN(d13);
                        float y13 = (float) (d12 + (cos3 * d13));
                        float var_ = this.x;
                        float var_ = this.y;
                        float var_ = var_ + y13;
                        float var_ = var_;
                        float var_ = y13;
                        canvas.drawLine(var_ + cx, var_ + cy, var_ + x13, var_, SnowflakesEffect.this.particleThinPaint);
                        angle += 1.0471976f;
                    }
                    return;
            }
        }
    }

    public SnowflakesEffect() {
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
        for (int a = 0; a < 20; a++) {
            this.freeParticles.add(new Particle());
        }
    }

    public void setColorKey(String key) {
        this.colorKey = key;
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

    private void updateParticles(long dt) {
        int count = this.particles.size();
        int a = 0;
        while (a < count) {
            Particle particle = this.particles.get(a);
            if (particle.currentTime >= particle.lifeTime) {
                if (this.freeParticles.size() < 40) {
                    this.freeParticles.add(particle);
                }
                this.particles.remove(a);
                a--;
                count--;
            } else {
                if (particle.currentTime < 200.0f) {
                    particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(particle.currentTime / 200.0f);
                } else {
                    particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation((particle.currentTime - 200.0f) / (particle.lifeTime - 200.0f));
                }
                particle.x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void onDraw(View parent, Canvas canvas) {
        Particle newParticle;
        if (parent != null && canvas != null) {
            int count = this.particles.size();
            for (int a = 0; a < count; a++) {
                this.particles.get(a).draw(canvas);
            }
            if (Utilities.random.nextFloat() > 0.7f && this.particles.size() < 100) {
                int statusBarHeight = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                float cx = Utilities.random.nextFloat() * ((float) parent.getMeasuredWidth());
                float cy = ((float) statusBarHeight) + (Utilities.random.nextFloat() * ((float) ((parent.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - statusBarHeight)));
                int angle = (Utilities.random.nextInt(40) - 20) + 90;
                double d = (double) angle;
                Double.isNaN(d);
                float vx = (float) Math.cos(d * 0.017453292519943295d);
                double d2 = (double) angle;
                Double.isNaN(d2);
                float vy = (float) Math.sin(d2 * 0.017453292519943295d);
                if (!this.freeParticles.isEmpty()) {
                    newParticle = this.freeParticles.get(0);
                    this.freeParticles.remove(0);
                } else {
                    newParticle = new Particle();
                }
                newParticle.x = cx;
                newParticle.y = cy;
                newParticle.vx = vx;
                newParticle.vy = vy;
                newParticle.alpha = 0.0f;
                newParticle.currentTime = 0.0f;
                newParticle.scale = Utilities.random.nextFloat() * 1.2f;
                newParticle.type = Utilities.random.nextInt(2);
                newParticle.lifeTime = (float) (Utilities.random.nextInt(100) + 2000);
                newParticle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                this.particles.add(newParticle);
            }
            long newTime = System.currentTimeMillis();
            updateParticles(Math.min(17, newTime - this.lastAnimationTime));
            this.lastAnimationTime = newTime;
            parent.invalidate();
        }
    }
}
