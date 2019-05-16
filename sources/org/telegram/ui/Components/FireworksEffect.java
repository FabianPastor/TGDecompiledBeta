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
            if (this.type == 0) {
                FireworksEffect.this.particlePaint.setColor(this.color);
                FireworksEffect.this.particlePaint.setStrokeWidth(((float) AndroidUtilities.dp(1.5f)) * this.scale);
                FireworksEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
                canvas.drawPoint(this.x, this.y, FireworksEffect.this.particlePaint);
            }
        }
    }

    public FireworksEffect() {
        this.particlePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        this.particlePaint.setColor(Theme.getColor("actionBarDefaultTitle") & -1644826);
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
                particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(f / f2);
                f = particle.x;
                f2 = particle.vx;
                float f3 = particle.velocity;
                float f4 = (float) j;
                particle.x = f + (((f2 * f3) * f4) / 500.0f);
                f = particle.y;
                f2 = particle.vy;
                particle.y = f + (((f3 * f2) * f4) / 500.0f);
                particle.vy = f2 + (f4 / 100.0f);
                particle.currentTime += f4;
            }
            i++;
        }
    }

    public void onDraw(View view, Canvas canvas) {
        if (view != null && canvas != null) {
            int size = this.particles.size();
            for (int i = 0; i < size; i++) {
                ((Particle) this.particles.get(i)).draw(canvas);
            }
            if (Utilities.random.nextBoolean() && this.particles.size() + 8 < 150) {
                int i2 = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                float nextFloat = Utilities.random.nextFloat() * ((float) view.getMeasuredWidth());
                float nextFloat2 = ((float) i2) + (Utilities.random.nextFloat() * ((float) ((view.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - i2)));
                i2 = Utilities.random.nextInt(4);
                i2 = i2 != 0 ? i2 != 1 ? i2 != 2 ? i2 != 3 ? -5752 : -15088582 : -207021 : -843755 : -13357350;
                for (int i3 = 0; i3 < 8; i3++) {
                    Particle particle;
                    double nextInt = (double) (Utilities.random.nextInt(270) - 225);
                    Double.isNaN(nextInt);
                    nextInt *= 0.017453292519943295d;
                    float cos = (float) Math.cos(nextInt);
                    float sin = (float) Math.sin(nextInt);
                    if (this.freeParticles.isEmpty()) {
                        particle = new Particle();
                    } else {
                        particle = (Particle) this.freeParticles.get(0);
                        this.freeParticles.remove(0);
                    }
                    particle.x = nextFloat;
                    particle.y = nextFloat2;
                    particle.vx = cos * 1.5f;
                    particle.vy = sin;
                    particle.color = i2;
                    particle.alpha = 1.0f;
                    particle.currentTime = 0.0f;
                    particle.scale = Math.max(1.0f, Utilities.random.nextFloat() * 1.5f);
                    particle.type = 0;
                    particle.lifeTime = (float) (Utilities.random.nextInt(1000) + 1000);
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
