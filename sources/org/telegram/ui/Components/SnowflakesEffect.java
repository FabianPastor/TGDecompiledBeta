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
                float angle = -1.5707964f;
                float px = (AndroidUtilities.dpf2(2.0f) * 2.0f) * r0.scale;
                float px1 = ((-AndroidUtilities.dpf2(0.57f)) * 2.0f) * r0.scale;
                float py1 = (AndroidUtilities.dpf2(1.55f) * 2.0f) * r0.scale;
                int a = 0;
                while (a < 6) {
                    float x1 = ((float) Math.cos((double) angle)) * px;
                    float y1 = ((float) Math.sin((double) angle)) * px;
                    float cx = x1 * 0.66f;
                    float cy = 0.66f * y1;
                    canvas.drawLine(r0.f28x, r0.f29y, r0.f28x + x1, r0.f29y + y1, SnowflakesEffect.this.particleThinPaint);
                    float angle2 = (float) (((double) angle) - 1.5707963267948966d);
                    int a2 = a;
                    float px2 = px;
                    float y12 = (float) ((Math.sin((double) angle2) * ((double) px1)) + (Math.cos((double) angle2) * ((double) py1)));
                    Canvas canvas2 = canvas;
                    canvas2.drawLine(r0.f28x + cx, r0.f29y + cy, r0.f28x + ((float) ((Math.cos((double) angle2) * ((double) px1)) - (Math.sin((double) angle2) * ((double) py1)))), r0.f29y + y12, SnowflakesEffect.this.particleThinPaint);
                    canvas2 = canvas;
                    canvas2.drawLine(r0.f28x + cx, r0.f29y + cy, r0.f28x + ((float) (((-Math.cos((double) angle2)) * ((double) px1)) - (Math.sin((double) angle2) * ((double) py1)))), r0.f29y + ((float) (((-Math.sin((double) angle2)) * ((double) px1)) + (Math.cos((double) angle2) * ((double) py1)))), SnowflakesEffect.this.particleThinPaint);
                    angle += 1.0471976f;
                    a = a2 + 1;
                    px = px2;
                }
                px1 = canvas;
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
        for (int a = 0; a < 20; a++) {
            this.freeParticles.add(new Particle());
        }
    }

    private void updateParticles(long dt) {
        int count = this.particles.size();
        int a = 0;
        while (a < count) {
            Particle particle = (Particle) this.particles.get(a);
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
                particle.f28x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.f29y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void onDraw(View parent, Canvas canvas) {
        if (parent != null) {
            if (canvas != null) {
                int a;
                int count = this.particles.size();
                for (a = 0; a < count; a++) {
                    ((Particle) this.particles.get(a)).draw(canvas);
                }
                if (Utilities.random.nextFloat() > 0.7f && this.particles.size() < 100) {
                    Particle newParticle;
                    a = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                    float cx = Utilities.random.nextFloat() * ((float) parent.getMeasuredWidth());
                    float cy = ((float) a) + (Utilities.random.nextFloat() * ((float) ((parent.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - a)));
                    int angle = (Utilities.random.nextInt(40) - 20) + 90;
                    float vx = (float) Math.cos(((double) angle) * 0.017453292519943295d);
                    float vy = (float) Math.sin(0.017453292519943295d * ((double) angle));
                    if (this.freeParticles.isEmpty()) {
                        newParticle = new Particle();
                    } else {
                        newParticle = (Particle) this.freeParticles.get(0);
                        this.freeParticles.remove(0);
                    }
                    Particle newParticle2 = newParticle;
                    newParticle2.f28x = cx;
                    newParticle2.f29y = cy;
                    newParticle2.vx = vx;
                    newParticle2.vy = vy;
                    newParticle2.alpha = 0.0f;
                    newParticle2.currentTime = 0.0f;
                    newParticle2.scale = Utilities.random.nextFloat() * 1.2f;
                    newParticle2.type = Utilities.random.nextInt(2);
                    newParticle2.lifeTime = (float) (2000 + Utilities.random.nextInt(100));
                    newParticle2.velocity = 20.0f + (Utilities.random.nextFloat() * 4.0f);
                    this.particles.add(newParticle2);
                }
                long newTime = System.currentTimeMillis();
                updateParticles(Math.min(17, newTime - this.lastAnimationTime));
                this.lastAnimationTime = newTime;
                parent.invalidate();
            }
        }
    }
}
