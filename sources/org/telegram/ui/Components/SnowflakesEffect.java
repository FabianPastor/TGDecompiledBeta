package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class SnowflakesEffect {
    final float angleDiff = 1.0471976f;
    private ArrayList<Particle> freeParticles = new ArrayList();
    private long lastAnimationTime;
    private Paint particlePaint = new Paint(1);
    private Paint particleThinPaint;
    private ArrayList<Particle> particles = new ArrayList();

    private class Particle {
        float alpha;
        float currentTime;
        float lifeTime;
        float scale;
        int type;
        float velocity;
        float vx;
        float vy;
        /* renamed from: x */
        float f28x;
        /* renamed from: y */
        float f29y;

        private Particle() {
        }

        public void draw(Canvas canvas) {
            if (this.type != 0) {
                SnowflakesEffect.this.particleThinPaint.setAlpha((int) (255.0f * r0.alpha));
                float f = -1.5707964f;
                float dpf2 = (AndroidUtilities.dpf2(2.0f) * 2.0f) * r0.scale;
                float f2 = ((-AndroidUtilities.dpf2(0.57f)) * 2.0f) * r0.scale;
                float dpf22 = (AndroidUtilities.dpf2(1.55f) * 2.0f) * r0.scale;
                int i = 0;
                while (i < 6) {
                    double d = (double) f;
                    float cos = ((float) Math.cos(d)) * dpf2;
                    float sin = ((float) Math.sin(d)) * dpf2;
                    float f3 = cos * 0.66f;
                    float f4 = 0.66f * sin;
                    canvas.drawLine(r0.f28x, r0.f29y, r0.f28x + cos, r0.f29y + sin, SnowflakesEffect.this.particleThinPaint);
                    d = (double) ((float) (d - 1.5707963267948966d));
                    double d2 = (double) f2;
                    float f5 = dpf2;
                    float f6 = f2;
                    double d3 = (double) dpf22;
                    Canvas canvas2 = canvas;
                    canvas2.drawLine(r0.f28x + f3, r0.f29y + f4, r0.f28x + ((float) ((Math.cos(d) * d2) - (Math.sin(d) * d3))), r0.f29y + ((float) ((Math.sin(d) * d2) + (Math.cos(d) * d3))), SnowflakesEffect.this.particleThinPaint);
                    cos = (float) (((-Math.cos(d)) * d2) - (Math.sin(d) * d3));
                    dpf2 = (float) (((-Math.sin(d)) * d2) + (Math.cos(d) * d3));
                    canvas.drawLine(r0.f28x + f3, r0.f29y + f4, r0.f28x + cos, r0.f29y + dpf2, SnowflakesEffect.this.particleThinPaint);
                    f += 1.0471976f;
                    i++;
                    dpf2 = f5;
                    f2 = f6;
                }
                return;
            }
            SnowflakesEffect.this.particlePaint.setAlpha((int) (255.0f * r0.alpha));
            canvas.drawPoint(r0.f28x, r0.f29y, SnowflakesEffect.this.particlePaint);
        }
    }

    public SnowflakesEffect() {
        this.particlePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        this.particlePaint.setColor(Theme.getColor(Theme.key_actionBarDefaultTitle) & -1644826);
        this.particlePaint.setStrokeCap(Cap.ROUND);
        this.particlePaint.setStyle(Style.STROKE);
        this.particleThinPaint = new Paint(1);
        this.particleThinPaint.setStrokeWidth((float) AndroidUtilities.dp(0.5f));
        this.particleThinPaint.setColor(Theme.getColor(Theme.key_actionBarDefaultTitle) & -1644826);
        this.particleThinPaint.setStrokeCap(Cap.ROUND);
        this.particleThinPaint.setStyle(Style.STROKE);
        for (int i = 0; i < 20; i++) {
            this.freeParticles.add(new Particle());
        }
    }

    private void updateParticles(long j) {
        int size = this.particles.size();
        int i = 0;
        while (i < size) {
            Particle particle = (Particle) this.particles.get(i);
            if (particle.currentTime >= particle.lifeTime) {
                if (this.freeParticles.size() < 40) {
                    this.freeParticles.add(particle);
                }
                this.particles.remove(i);
                i--;
                size--;
            } else {
                if (particle.currentTime < 200.0f) {
                    particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(particle.currentTime / 200.0f);
                } else {
                    particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation((particle.currentTime - 200.0f) / (particle.lifeTime - 200.0f));
                }
                float f = (float) j;
                particle.f28x += ((particle.vx * particle.velocity) * f) / 500.0f;
                particle.f29y += ((particle.vy * particle.velocity) * f) / 500.0f;
                particle.currentTime += f;
            }
            i++;
        }
    }

    public void onDraw(View view, Canvas canvas) {
        if (view != null) {
            if (canvas != null) {
                int size = this.particles.size();
                for (int i = 0; i < size; i++) {
                    ((Particle) this.particles.get(i)).draw(canvas);
                }
                if (Utilities.random.nextFloat() > 0.7f && this.particles.size() < 100) {
                    Particle particle;
                    canvas = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : null;
                    float nextFloat = Utilities.random.nextFloat() * ((float) view.getMeasuredWidth());
                    float nextFloat2 = ((float) canvas) + (Utilities.random.nextFloat() * ((float) ((view.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - canvas)));
                    double nextInt = 0.017453292519943295d * ((double) ((Utilities.random.nextInt(40) - 20) + 90));
                    canvas = (float) Math.cos(nextInt);
                    float sin = (float) Math.sin(nextInt);
                    if (this.freeParticles.isEmpty()) {
                        particle = new Particle();
                    } else {
                        particle = (Particle) this.freeParticles.get(0);
                        this.freeParticles.remove(0);
                    }
                    particle.f28x = nextFloat;
                    particle.f29y = nextFloat2;
                    particle.vx = canvas;
                    particle.vy = sin;
                    particle.alpha = 0.0f;
                    particle.currentTime = 0.0f;
                    particle.scale = Utilities.random.nextFloat() * 1.2f;
                    particle.type = Utilities.random.nextInt(2);
                    particle.lifeTime = (float) (2000 + Utilities.random.nextInt(100));
                    particle.velocity = 20.0f + (Utilities.random.nextFloat() * 4.0f);
                    this.particles.add(particle);
                }
                long currentTimeMillis = System.currentTimeMillis();
                updateParticles(Math.min(17, currentTimeMillis - this.lastAnimationTime));
                this.lastAnimationTime = currentTimeMillis;
                view.invalidate();
            }
        }
    }
}
