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
                particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(particle.currentTime / particle.lifeTime);
                float f = (float) j;
                particle.f18x += ((particle.vx * particle.velocity) * f) / 500.0f;
                particle.f19y += ((particle.vy * particle.velocity) * f) / 500.0f;
                particle.vy += f / 100.0f;
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
                if (Utilities.random.nextBoolean() != null && this.particles.size() + 8 < 150) {
                    canvas = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : null;
                    float nextFloat = Utilities.random.nextFloat() * ((float) view.getMeasuredWidth());
                    float nextFloat2 = ((float) canvas) + (Utilities.random.nextFloat() * ((float) ((view.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - canvas)));
                    switch (Utilities.random.nextInt(4)) {
                        case null:
                            canvas = -13357350;
                            break;
                        case 1:
                            canvas = -843755;
                            break;
                        case 2:
                            canvas = -207021;
                            break;
                        case 3:
                            canvas = -15088582;
                            break;
                        default:
                            canvas = -5752;
                            break;
                    }
                    for (int i2 = 0; i2 < 8; i2++) {
                        Particle particle;
                        double nextInt = 0.017453292519943295d * ((double) (Utilities.random.nextInt(270) - 225));
                        float cos = (float) Math.cos(nextInt);
                        float sin = (float) Math.sin(nextInt);
                        if (this.freeParticles.isEmpty()) {
                            particle = new Particle();
                        } else {
                            particle = (Particle) this.freeParticles.get(0);
                            this.freeParticles.remove(0);
                        }
                        particle.f18x = nextFloat;
                        particle.f19y = nextFloat2;
                        particle.vx = cos * 1.5f;
                        particle.vy = sin;
                        particle.color = canvas;
                        particle.alpha = 1.0f;
                        particle.currentTime = 0.0f;
                        particle.scale = Math.max(1.0f, Utilities.random.nextFloat() * 1.5f);
                        particle.type = 0;
                        particle.lifeTime = (float) (1000 + Utilities.random.nextInt(1000));
                        particle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                        this.particles.add(particle);
                    }
                }
                long currentTimeMillis = System.currentTimeMillis();
                updateParticles(Math.min(17, currentTimeMillis - this.lastAnimationTime));
                this.lastAnimationTime = currentTimeMillis;
                view.invalidate();
            }
        }
    }
}
