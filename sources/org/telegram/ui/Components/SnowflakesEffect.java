package org.telegram.ui.Components;

import android.graphics.Bitmap;
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
    /* access modifiers changed from: private */
    public Paint bitmapPaint = new Paint();
    private int color;
    private String colorKey = "actionBarDefaultTitle";
    private ArrayList<Particle> freeParticles = new ArrayList<>();
    private long lastAnimationTime;
    Bitmap particleBitmap;
    /* access modifiers changed from: private */
    public Paint particlePaint;
    /* access modifiers changed from: private */
    public Paint particleThinPaint;
    private ArrayList<Particle> particles = new ArrayList<>();
    private int viewType;

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
            Canvas canvas2 = canvas;
            switch (this.type) {
                case 0:
                    SnowflakesEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
                    canvas2.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
                    Canvas canvas3 = canvas2;
                    return;
                default:
                    float y1 = -1.5707964f;
                    if (SnowflakesEffect.this.particleBitmap == null) {
                        SnowflakesEffect.this.particleThinPaint.setAlpha(255);
                        SnowflakesEffect.this.particleBitmap = Bitmap.createBitmap(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Bitmap.Config.ARGB_8888);
                        Canvas bitmapCanvas = new Canvas(SnowflakesEffect.this.particleBitmap);
                        float px = AndroidUtilities.dpf2(2.0f) * 2.0f;
                        float px1 = (-AndroidUtilities.dpf2(0.57f)) * 2.0f;
                        float py1 = 2.0f * AndroidUtilities.dpf2(1.55f);
                        int a = 0;
                        while (a < 6) {
                            float x2 = (float) AndroidUtilities.dp(8.0f);
                            float y2 = (float) AndroidUtilities.dp(8.0f);
                            float x1 = ((float) Math.cos((double) y1)) * px;
                            float y12 = ((float) Math.sin((double) y1)) * px;
                            float cx = x1 * 0.66f;
                            float cy = y12 * 0.66f;
                            bitmapCanvas.drawLine(x2, y2, x2 + x1, y2 + y12, SnowflakesEffect.this.particleThinPaint);
                            double d = (double) y1;
                            Double.isNaN(d);
                            float angle2 = (float) (d - 1.5707963267948966d);
                            double cos = Math.cos((double) angle2);
                            double d2 = (double) px1;
                            Double.isNaN(d2);
                            double d3 = cos * d2;
                            double sin = Math.sin((double) angle2);
                            float angle = y1;
                            double d4 = (double) py1;
                            Double.isNaN(d4);
                            float x12 = (float) (d3 - (sin * d4));
                            double sin2 = Math.sin((double) angle2);
                            double d5 = (double) px1;
                            Double.isNaN(d5);
                            double d6 = sin2 * d5;
                            double cos2 = Math.cos((double) angle2);
                            double d7 = (double) py1;
                            Double.isNaN(d7);
                            float y13 = (float) (d6 + (cos2 * d7));
                            Canvas canvas4 = bitmapCanvas;
                            canvas4.drawLine(x2 + cx, y2 + cy, x2 + x12, y2 + y13, SnowflakesEffect.this.particleThinPaint);
                            double d8 = (double) px1;
                            Double.isNaN(d8);
                            double d9 = (-Math.cos((double) angle2)) * d8;
                            double sin3 = Math.sin((double) angle2);
                            float f = x12;
                            float f2 = y13;
                            double d10 = (double) py1;
                            Double.isNaN(d10);
                            float x13 = (float) (d9 - (sin3 * d10));
                            double d11 = (double) px1;
                            Double.isNaN(d11);
                            double d12 = (-Math.sin((double) angle2)) * d11;
                            double cos3 = Math.cos((double) angle2);
                            double d13 = (double) py1;
                            Double.isNaN(d13);
                            canvas4.drawLine(x2 + cx, y2 + cy, x2 + x13, y2 + ((float) (d12 + (cos3 * d13))), SnowflakesEffect.this.particleThinPaint);
                            y1 = angle + 1.0471976f;
                            a++;
                            Canvas canvas5 = canvas;
                        }
                        float f3 = y1;
                    }
                    SnowflakesEffect.this.bitmapPaint.setAlpha((int) (this.alpha * 255.0f));
                    canvas.save();
                    float f4 = this.scale;
                    Canvas canvas6 = canvas;
                    canvas6.scale(f4, f4, this.x, this.y);
                    canvas6.drawBitmap(SnowflakesEffect.this.particleBitmap, this.x, this.y, SnowflakesEffect.this.bitmapPaint);
                    canvas.restore();
                    return;
            }
        }
    }

    public SnowflakesEffect(int viewType2) {
        this.viewType = viewType2;
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
                if (this.viewType == 0) {
                    if (particle.currentTime < 200.0f) {
                        particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(particle.currentTime / 200.0f);
                    } else {
                        particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation((particle.currentTime - 200.0f) / (particle.lifeTime - 200.0f));
                    }
                } else if (particle.currentTime < 200.0f) {
                    particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(particle.currentTime / 200.0f);
                } else if (particle.lifeTime - particle.currentTime < 2000.0f) {
                    particle.alpha = AndroidUtilities.decelerateInterpolator.getInterpolation((particle.lifeTime - particle.currentTime) / 2000.0f);
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
        Canvas canvas2 = canvas;
        if (parent != null && canvas2 != null) {
            int count = this.particles.size();
            for (int a = 0; a < count; a++) {
                this.particles.get(a).draw(canvas2);
            }
            int a2 = this.viewType;
            int maxCount = a2 == 0 ? 100 : 300;
            int createPerFrame = a2 == 0 ? 1 : 10;
            if (this.particles.size() < maxCount) {
                for (int i = 0; i < createPerFrame; i++) {
                    if (this.particles.size() < maxCount && Utilities.random.nextFloat() > 0.7f) {
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
                        if (this.viewType == 0) {
                            newParticle.lifeTime = (float) (Utilities.random.nextInt(100) + 2000);
                        } else {
                            newParticle.lifeTime = (float) (Utilities.random.nextInt(2000) + 3000);
                        }
                        newParticle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                        this.particles.add(newParticle);
                    }
                }
            }
            long newTime = System.currentTimeMillis();
            updateParticles(Math.min(17, newTime - this.lastAnimationTime));
            this.lastAnimationTime = newTime;
            parent.invalidate();
        }
    }
}
