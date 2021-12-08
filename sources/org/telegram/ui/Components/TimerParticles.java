package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;

public class TimerParticles {
    private ArrayList<Particle> freeParticles = new ArrayList<>();
    private long lastAnimationTime;
    private ArrayList<Particle> particles = new ArrayList<>();

    private static class Particle {
        float alpha;
        float currentTime;
        float lifeTime;
        float velocity;
        float vx;
        float vy;
        float x;
        float y;

        private Particle() {
        }
    }

    public TimerParticles() {
        for (int a = 0; a < 40; a++) {
            this.freeParticles.add(new Particle());
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
                particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(particle.currentTime / particle.lifeTime);
                particle.x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void draw(Canvas canvas, Paint particlePaint, RectF rect, float radProgress, float alpha) {
        Particle newParticle;
        Paint paint = particlePaint;
        int count = this.particles.size();
        for (int a = 0; a < count; a++) {
            Particle particle = this.particles.get(a);
            paint.setAlpha((int) (particle.alpha * 255.0f * alpha));
            canvas.drawPoint(particle.x, particle.y, paint);
        }
        Canvas canvas2 = canvas;
        double d = (double) (radProgress - 90.0f);
        Double.isNaN(d);
        double vx = Math.sin(d * 0.017453292519943295d);
        double d2 = (double) (radProgress - 90.0f);
        Double.isNaN(d2);
        double vy = -Math.cos(d2 * 0.017453292519943295d);
        float rad = rect.width() / 2.0f;
        double d3 = (double) rad;
        Double.isNaN(d3);
        double d4 = (-vy) * d3;
        double centerX = (double) rect.centerX();
        Double.isNaN(centerX);
        float cx = (float) (d4 + centerX);
        double d5 = (double) rad;
        Double.isNaN(d5);
        double centerY = (double) rect.centerY();
        Double.isNaN(centerY);
        float cy = (float) ((d5 * vx) + centerY);
        int a2 = 0;
        while (a2 < 1) {
            if (!this.freeParticles.isEmpty()) {
                newParticle = this.freeParticles.get(0);
                this.freeParticles.remove(0);
            } else {
                newParticle = new Particle();
            }
            newParticle.x = cx;
            newParticle.y = cy;
            double nextInt = (double) (Utilities.random.nextInt(140) - 70);
            Double.isNaN(nextInt);
            double angle = nextInt * 0.017453292519943295d;
            if (angle < 0.0d) {
                angle += 6.283185307179586d;
            }
            newParticle.vx = (float) ((Math.cos(angle) * vx) - (Math.sin(angle) * vy));
            newParticle.vy = (float) ((Math.sin(angle) * vx) + (Math.cos(angle) * vy));
            newParticle.alpha = 1.0f;
            newParticle.currentTime = 0.0f;
            newParticle.lifeTime = (float) (Utilities.random.nextInt(100) + 400);
            newParticle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
            this.particles.add(newParticle);
            a2++;
            Paint paint2 = particlePaint;
            count = count;
        }
        long newTime = SystemClock.elapsedRealtime();
        updateParticles(Math.min(20, newTime - this.lastAnimationTime));
        this.lastAnimationTime = newTime;
    }
}
