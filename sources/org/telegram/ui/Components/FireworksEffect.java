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
        /* renamed from: x */
        float f18x;
        /* renamed from: y */
        float f19y;

        private Particle() {
        }

        public void draw(Canvas canvas) {
            if (this.type == 0) {
                FireworksEffect.this.particlePaint.setColor(this.color);
                FireworksEffect.this.particlePaint.setStrokeWidth(((float) AndroidUtilities.dp(1.5f)) * this.scale);
                FireworksEffect.this.particlePaint.setAlpha((int) (255.0f * this.alpha));
                canvas.drawPoint(this.f18x, this.f19y, FireworksEffect.this.particlePaint);
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
                particle.f18x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.f19y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.vy += ((float) dt) / 100.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void onDraw(View parent, Canvas canvas) {
        FireworksEffect fireworksEffect = this;
        Canvas canvas2 = canvas;
        if (parent != null) {
            if (canvas2 != null) {
                int a;
                int count = fireworksEffect.particles.size();
                int i = 0;
                for (a = 0; a < count; a++) {
                    ((Particle) fireworksEffect.particles.get(a)).draw(canvas2);
                }
                if (Utilities.random.nextBoolean()) {
                    int i2 = 8;
                    if (fireworksEffect.particles.size() + 8 < 150) {
                        int color;
                        a = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                        float cx = Utilities.random.nextFloat() * ((float) parent.getMeasuredWidth());
                        float cy = ((float) a) + (Utilities.random.nextFloat() * ((float) ((parent.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - a)));
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
                        int a2 = 0;
                        while (a2 < i2) {
                            Particle newParticle;
                            int angle = Utilities.random.nextInt(270) - 225;
                            float vx = (float) Math.cos(((double) angle) * 0.017453292519943295d);
                            float cx2 = cx;
                            float vy = (float) Math.sin(((double) angle) * 0.017453292519943295d);
                            if (fireworksEffect.freeParticles.isEmpty()) {
                                newParticle = new Particle();
                            } else {
                                newParticle = (Particle) fireworksEffect.freeParticles.get(i);
                                fireworksEffect.freeParticles.remove(i);
                            }
                            float cx3 = cx2;
                            newParticle.f18x = cx3;
                            newParticle.f19y = cy;
                            newParticle.vx = vx * 1.5f;
                            newParticle.vy = vy;
                            newParticle.color = color;
                            newParticle.alpha = 1.0f;
                            newParticle.currentTime = 0.0f;
                            newParticle.scale = Math.max(1.0f, Utilities.random.nextFloat() * 1.5f);
                            i = 0;
                            newParticle.type = 0;
                            newParticle.lifeTime = (float) (1000 + Utilities.random.nextInt(1000));
                            newParticle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                            fireworksEffect.particles.add(newParticle);
                            a2++;
                            cx = cx3;
                            float f = 20.0f;
                            i2 = 8;
                        }
                    }
                }
                long newTime = System.currentTimeMillis();
                updateParticles(Math.min(17, newTime - fireworksEffect.lastAnimationTime));
                fireworksEffect.lastAnimationTime = newTime;
                parent.invalidate();
            }
        }
    }
}
