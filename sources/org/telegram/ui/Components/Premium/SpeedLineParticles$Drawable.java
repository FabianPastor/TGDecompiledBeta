package org.telegram.ui.Components.Premium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class SpeedLineParticles$Drawable {
    public final int count;
    /* access modifiers changed from: private */
    public final float dt = (1000.0f / AndroidUtilities.screenRefreshRate);
    private int lastColor;
    /* access modifiers changed from: private */
    public float[] lines;
    public long minLifeTime = 2000;
    private Paint paint = new Paint();
    ArrayList<Particle> particles = new ArrayList<>();
    public boolean paused;
    long pausedTime;
    public RectF rect = new RectF();
    public RectF screenRect = new RectF();
    public float speedScale = 1.0f;

    public SpeedLineParticles$Drawable(int i) {
        this.count = i;
        this.lines = new float[(i * 4)];
    }

    public void init() {
        if (this.particles.isEmpty()) {
            for (int i = 0; i < this.count; i++) {
                this.particles.add(new Particle());
            }
        }
        updateColors();
    }

    public void updateColors() {
        int alphaComponent = ColorUtils.setAlphaComponent(Theme.getColor("premiumStartSmallStarsColor2"), 80);
        if (this.lastColor != alphaComponent) {
            this.lastColor = alphaComponent;
            this.paint.setColor(alphaComponent);
        }
    }

    public void resetPositions() {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < this.particles.size(); i++) {
            this.particles.get(i).genPosition(currentTimeMillis, true);
        }
    }

    public void onDraw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < this.particles.size(); i++) {
            Particle particle = this.particles.get(i);
            if (this.paused) {
                particle.draw(canvas, i, this.pausedTime);
            } else {
                particle.draw(canvas, i, currentTimeMillis);
            }
            if (currentTimeMillis > particle.lifeTime || !this.screenRect.contains(particle.x, particle.y)) {
                particle.genPosition(currentTimeMillis, false);
            }
        }
        canvas.drawLines(this.lines, this.paint);
    }

    private class Particle {
        float inProgress;
        /* access modifiers changed from: private */
        public long lifeTime;
        private float vecX;
        private float vecY;
        /* access modifiers changed from: private */
        public float x;
        /* access modifiers changed from: private */
        public float y;

        private Particle() {
        }

        public void draw(Canvas canvas, int i, long j) {
            int i2 = i * 4;
            SpeedLineParticles$Drawable.this.lines[i2] = this.x;
            SpeedLineParticles$Drawable.this.lines[i2 + 1] = this.y;
            SpeedLineParticles$Drawable.this.lines[i2 + 2] = this.x + (((float) AndroidUtilities.dp(30.0f)) * this.vecX);
            SpeedLineParticles$Drawable.this.lines[i2 + 3] = this.y + (((float) AndroidUtilities.dp(30.0f)) * this.vecY);
            if (!SpeedLineParticles$Drawable.this.paused) {
                float dp = ((float) AndroidUtilities.dp(4.0f)) * (SpeedLineParticles$Drawable.this.dt / 660.0f);
                SpeedLineParticles$Drawable speedLineParticles$Drawable = SpeedLineParticles$Drawable.this;
                float f = dp * speedLineParticles$Drawable.speedScale;
                this.x += this.vecX * f;
                this.y += this.vecY * f;
                float f2 = this.inProgress;
                if (f2 != 1.0f) {
                    float access$500 = f2 + (speedLineParticles$Drawable.dt / 200.0f);
                    this.inProgress = access$500;
                    if (access$500 > 1.0f) {
                        this.inProgress = 1.0f;
                    }
                }
            }
        }

        public void genPosition(long j, boolean z) {
            this.lifeTime = j + SpeedLineParticles$Drawable.this.minLifeTime + ((long) Utilities.fastRandom.nextInt(1000));
            SpeedLineParticles$Drawable speedLineParticles$Drawable = SpeedLineParticles$Drawable.this;
            RectF rectF = z ? speedLineParticles$Drawable.screenRect : speedLineParticles$Drawable.rect;
            float abs = rectF.left + Math.abs(((float) Utilities.fastRandom.nextInt()) % rectF.width());
            this.x = abs;
            this.y = rectF.top + Math.abs(((float) Utilities.fastRandom.nextInt()) % rectF.height());
            double atan2 = Math.atan2((double) (abs - SpeedLineParticles$Drawable.this.rect.centerX()), (double) (this.y - SpeedLineParticles$Drawable.this.rect.centerY()));
            this.vecX = (float) Math.sin(atan2);
            this.vecY = (float) Math.cos(atan2);
            Utilities.fastRandom.nextInt(50);
            this.inProgress = 0.0f;
        }
    }
}
