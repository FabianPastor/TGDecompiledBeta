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

public class FireworksEffect {
    final float angleDiff = 1.0471976f;
    private ArrayList<Particle> freeParticles = new ArrayList();
    private long lastAnimationTime;
    private Paint particlePaint = new Paint(1);
    private ArrayList<Particle> particles = new ArrayList();

    private class Particle {
        float alpha;
        int color;
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
                    FireworksEffect.this.particlePaint.setColor(this.color);
                    FireworksEffect.this.particlePaint.setStrokeWidth(((float) AndroidUtilities.dp(1.5f)) * this.scale);
                    FireworksEffect.this.particlePaint.setAlpha((int) (255.0f * this.alpha));
                    canvas.drawPoint(this.x, this.y, FireworksEffect.this.particlePaint);
                    return;
                default:
                    return;
            }
        }
    }

    public FireworksEffect() {
        this.particlePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        this.particlePaint.setColor(Theme.getColor(Theme.key_actionBarDefaultTitle) & -1644826);
        this.particlePaint.setStrokeCap(Cap.ROUND);
        this.particlePaint.setStyle(Style.STROKE);
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
                particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(particle.currentTime / particle.lifeTime);
                particle.x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.vy += ((float) dt) / 100.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void onDraw(View parent, Canvas canvas) {
        if (parent != null && canvas != null) {
            int a;
            int count = this.particles.size();
            for (a = 0; a < count; a++) {
                ((Particle) this.particles.get(a)).draw(canvas);
            }
            if (Utilities.random.nextBoolean() && this.particles.size() + 8 < 150) {
                int color;
                int statusBarHeight = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                float cx = Utilities.random.nextFloat() * ((float) parent.getMeasuredWidth());
                float cy = ((float) statusBarHeight) + (Utilities.random.nextFloat() * ((float) ((parent.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - statusBarHeight)));
                switch (Utilities.random.nextInt(4)) {
                    case 0:
                        color = -13357350;
                        break;
                    case 1:
                        color = -843755;
                        break;
                    case 2:
                        color = -207021;
                        break;
                    case 3:
                        color = -15088582;
                        break;
                    default:
                        color = -5752;
                        break;
                }
                for (a = 0; a < 8; a++) {
                    Particle newParticle;
                    int angle = Utilities.random.nextInt(270) - 225;
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
                    newParticle.vx = 1.5f * vx;
                    newParticle.vy = vy;
                    newParticle.color = color;
                    newParticle.alpha = 1.0f;
                    newParticle.currentTime = 0.0f;
                    newParticle.scale = Math.max(1.0f, Utilities.random.nextFloat() * 1.5f);
                    newParticle.type = 0;
                    newParticle.lifeTime = (float) (Utilities.random.nextInt(1000) + 1000);
                    newParticle.velocity = 20.0f + (Utilities.random.nextFloat() * 4.0f);
                    this.particles.add(newParticle);
                }
            }
            long newTime = System.currentTimeMillis();
            updateParticles(Math.min(17, newTime - this.lastAnimationTime));
            this.lastAnimationTime = newTime;
            parent.invalidate();
        }
    }
}
