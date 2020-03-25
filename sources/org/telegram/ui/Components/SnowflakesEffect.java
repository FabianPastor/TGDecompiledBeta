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
            if (this.type != 0) {
                SnowflakesEffect.this.particleThinPaint.setAlpha((int) (this.alpha * 255.0f));
                float f = -1.5707964f;
                float dpf2 = AndroidUtilities.dpf2(2.0f) * 2.0f * this.scale;
                float f2 = (-AndroidUtilities.dpf2(0.57f)) * 2.0f * this.scale;
                float dpvar_ = AndroidUtilities.dpf2(1.55f) * 2.0f * this.scale;
                int i = 0;
                while (i < 6) {
                    double d = (double) f;
                    float cos = ((float) Math.cos(d)) * dpf2;
                    float sin = ((float) Math.sin(d)) * dpf2;
                    float f3 = cos * 0.66f;
                    float f4 = 0.66f * sin;
                    float f5 = this.x;
                    float f6 = this.y;
                    canvas.drawLine(f5, f6, f5 + cos, f6 + sin, SnowflakesEffect.this.particleThinPaint);
                    Double.isNaN(d);
                    double d2 = (double) ((float) (d - 1.5707963267948966d));
                    double cos2 = Math.cos(d2);
                    double d3 = (double) f2;
                    Double.isNaN(d3);
                    double sin2 = Math.sin(d2);
                    float f7 = dpf2;
                    float f8 = f2;
                    double d4 = (double) dpvar_;
                    Double.isNaN(d4);
                    double sin3 = Math.sin(d2);
                    Double.isNaN(d3);
                    double cos3 = Math.cos(d2);
                    Double.isNaN(d4);
                    float f9 = (float) ((sin3 * d3) + (cos3 * d4));
                    float var_ = this.x;
                    float var_ = this.y;
                    Canvas canvas2 = canvas;
                    canvas2.drawLine(var_ + f3, var_ + f4, var_ + ((float) ((cos2 * d3) - (sin2 * d4))), var_ + f9, SnowflakesEffect.this.particleThinPaint);
                    Double.isNaN(d3);
                    double sin4 = Math.sin(d2);
                    Double.isNaN(d4);
                    Double.isNaN(d3);
                    double cos4 = Math.cos(d2);
                    Double.isNaN(d4);
                    float var_ = this.x;
                    float var_ = this.y;
                    canvas2.drawLine(var_ + f3, var_ + f4, var_ + ((float) (((-Math.cos(d2)) * d3) - (sin4 * d4))), var_ + ((float) (((-Math.sin(d2)) * d3) + (cos4 * d4))), SnowflakesEffect.this.particleThinPaint);
                    f += 1.0471976f;
                    i++;
                    dpf2 = f7;
                    f2 = f8;
                }
                return;
            }
            SnowflakesEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
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
        for (int i = 0; i < 20; i++) {
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
                if (f < 200.0f) {
                    particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(f / 200.0f);
                } else {
                    particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation((f - 200.0f) / (f2 - 200.0f));
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
            if (Utilities.random.nextFloat() > 0.7f && this.particles.size() < 100) {
                int i2 = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                float nextFloat = Utilities.random.nextFloat() * ((float) view.getMeasuredWidth());
                float nextFloat2 = ((float) i2) + (Utilities.random.nextFloat() * ((float) ((view.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - i2)));
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
                particle.lifeTime = (float) (Utilities.random.nextInt(100) + 2000);
                particle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                this.particles.add(particle);
            }
            long currentTimeMillis = System.currentTimeMillis();
            updateParticles(Math.min(17, currentTimeMillis - this.lastAnimationTime));
            this.lastAnimationTime = currentTimeMillis;
            view.invalidate();
        }
    }
}
