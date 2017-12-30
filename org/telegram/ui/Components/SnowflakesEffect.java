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
        float x;
        float y;

        private Particle() {
        }

        public void draw(Canvas canvas) {
            switch (this.type) {
                case 0:
                    SnowflakesEffect.this.particlePaint.setAlpha((int) (255.0f * this.alpha));
                    canvas.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
                    return;
                default:
                    SnowflakesEffect.this.particleThinPaint.setAlpha((int) (255.0f * this.alpha));
                    float angle = -1.5707964f;
                    float px = (AndroidUtilities.dpf2(2.0f) * 2.0f) * this.scale;
                    float px1 = ((-AndroidUtilities.dpf2(0.57f)) * 2.0f) * this.scale;
                    float py1 = (AndroidUtilities.dpf2(1.55f) * 2.0f) * this.scale;
                    for (int a = 0; a < 6; a++) {
                        float x1 = ((float) Math.cos((double) angle)) * px;
                        float y1 = ((float) Math.sin((double) angle)) * px;
                        float cx = x1 * 0.66f;
                        float cy = y1 * 0.66f;
                        canvas.drawLine(this.x, this.y, this.x + x1, this.y + y1, SnowflakesEffect.this.particleThinPaint);
                        float angle2 = (float) (((double) angle) - 1.5707963267948966d);
                        canvas.drawLine(this.x + cx, this.y + cy, this.x + ((float) ((Math.cos((double) angle2) * ((double) px1)) - (Math.sin((double) angle2) * ((double) py1)))), this.y + ((float) ((Math.sin((double) angle2) * ((double) px1)) + (Math.cos((double) angle2) * ((double) py1)))), SnowflakesEffect.this.particleThinPaint);
                        canvas.drawLine(this.x + cx, this.y + cy, this.x + ((float) (((-Math.cos((double) angle2)) * ((double) px1)) - (Math.sin((double) angle2) * ((double) py1)))), this.y + ((float) (((-Math.sin((double) angle2)) * ((double) px1)) + (Math.cos((double) angle2) * ((double) py1)))), SnowflakesEffect.this.particleThinPaint);
                        angle += 1.0471976f;
                    }
                    return;
            }
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
                particle.x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void onDraw(View parent, Canvas canvas) {
        if (parent != null && canvas != null) {
            int count = this.particles.size();
            for (int a = 0; a < count; a++) {
                ((Particle) this.particles.get(a)).draw(canvas);
            }
            if (Utilities.random.nextFloat() > 0.7f && this.particles.size() < 100) {
                Particle newParticle;
                int statusBarHeight = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                float cx = Utilities.random.nextFloat() * ((float) parent.getMeasuredWidth());
                float cy = ((float) statusBarHeight) + (Utilities.random.nextFloat() * ((float) ((parent.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - statusBarHeight)));
                int angle = (Utilities.random.nextInt(40) - 20) + 90;
                float vx = (float) Math.cos(0.017453292519943295d * ((double) angle));
                float vy = (float) Math.sin(0.017453292519943295d * ((double) angle));
                if (this.freeParticles.isEmpty()) {
                    newParticle = new Particle();
                } else {
                    newParticle = (Particle) this.freeParticles.get(0);
                    this.freeParticles.remove(0);
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
                newParticle.velocity = 20.0f + (Utilities.random.nextFloat() * 4.0f);
                this.particles.add(newParticle);
            }
            long newTime = System.currentTimeMillis();
            updateParticles(Math.min(17, newTime - this.lastAnimationTime));
            this.lastAnimationTime = newTime;
            parent.invalidate();
        }
    }
}
