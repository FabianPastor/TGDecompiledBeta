package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

public class FireworksOverlay extends View {
    private static int[] colors = {-13845272, -6421296, -79102, -187561, -14185218, -10897300};
    private static final int fallParticlesCount = (SharedConfig.getDevicePerfomanceClass() == 0 ? 20 : 30);
    /* access modifiers changed from: private */
    public static Paint[] paint = new Paint[colors.length];
    private static final int particlesCount = (SharedConfig.getDevicePerfomanceClass() == 0 ? 50 : 60);
    private int fallingDownCount;
    private long lastUpdateTime;
    private ArrayList<Particle> particles = new ArrayList<>(particlesCount + fallParticlesCount);
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public float speedCoef = 1.0f;
    private boolean started;
    private boolean startedFall;

    static /* synthetic */ int access$308(FireworksOverlay fireworksOverlay) {
        int i = fireworksOverlay.fallingDownCount;
        fireworksOverlay.fallingDownCount = i + 1;
        return i;
    }

    static {
        int i = 0;
        while (true) {
            Paint[] paintArr = paint;
            if (i < paintArr.length) {
                paintArr[i] = new Paint(1);
                paint[i].setColor(colors[i]);
                i++;
            } else {
                return;
            }
        }
    }

    private class Particle {
        byte colorType;
        byte finishedStart;
        float moveX;
        float moveY;
        short rotation;
        byte side;
        byte type;
        byte typeSize;
        float x;
        byte xFinished;
        float y;

        private Particle() {
        }

        /* access modifiers changed from: private */
        public void draw(Canvas canvas) {
            if (this.type == 0) {
                canvas.drawCircle(this.x, this.y, (float) AndroidUtilities.dp((float) this.typeSize), FireworksOverlay.paint[this.colorType]);
                return;
            }
            FireworksOverlay.this.rect.set(this.x - ((float) AndroidUtilities.dp((float) this.typeSize)), this.y - ((float) AndroidUtilities.dp(2.0f)), this.x + ((float) AndroidUtilities.dp((float) this.typeSize)), this.y + ((float) AndroidUtilities.dp(2.0f)));
            canvas.save();
            canvas.rotate((float) this.rotation, FireworksOverlay.this.rect.centerX(), FireworksOverlay.this.rect.centerY());
            canvas.drawRoundRect(FireworksOverlay.this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), FireworksOverlay.paint[this.colorType]);
            canvas.restore();
        }

        /* access modifiers changed from: private */
        public boolean update(int i) {
            float f = ((float) i) / 16.0f;
            float f2 = this.x;
            float f3 = this.moveX;
            this.x = f2 + (f3 * f);
            this.y += this.moveY * f;
            if (this.xFinished != 0) {
                float dp = ((float) AndroidUtilities.dp(1.0f)) * 0.5f;
                if (this.xFinished == 1) {
                    this.moveX += dp * f * 0.05f;
                    if (this.moveX >= dp) {
                        this.xFinished = 2;
                    }
                } else {
                    this.moveX -= (dp * f) * 0.05f;
                    if (this.moveX <= (-dp)) {
                        this.xFinished = 1;
                    }
                }
            } else if (this.side == 0) {
                if (f3 > 0.0f) {
                    this.moveX = f3 - (0.05f * f);
                    if (this.moveX <= 0.0f) {
                        this.moveX = 0.0f;
                        this.xFinished = this.finishedStart;
                    }
                }
            } else if (f3 < 0.0f) {
                this.moveX = f3 + (0.05f * f);
                if (this.moveX >= 0.0f) {
                    this.moveX = 0.0f;
                    this.xFinished = this.finishedStart;
                }
            }
            float f4 = ((float) (-AndroidUtilities.dp(1.0f))) / 2.0f;
            boolean z = this.moveY < f4;
            float f5 = this.moveY;
            if (f5 > f4) {
                this.moveY = f5 + ((((float) AndroidUtilities.dp(1.0f)) / 3.0f) * f * FireworksOverlay.this.speedCoef);
            } else {
                this.moveY = f5 + ((((float) AndroidUtilities.dp(1.0f)) / 3.0f) * f);
            }
            if (z && this.moveY > f4) {
                FireworksOverlay.access$308(FireworksOverlay.this);
            }
            if (this.type == 1) {
                this.rotation = (short) ((int) (((float) this.rotation) + (f * 10.0f)));
                short s = this.rotation;
                if (s > 360) {
                    this.rotation = (short) (s - 360);
                }
            }
            if (this.y >= ((float) FireworksOverlay.this.getMeasuredHeight())) {
                return true;
            }
            return false;
        }
    }

    public FireworksOverlay(Context context) {
        super(context);
    }

    private Particle createParticle(boolean z) {
        Particle particle = new Particle();
        particle.colorType = (byte) Utilities.random.nextInt(paint.length);
        particle.type = (byte) Utilities.random.nextInt(2);
        particle.side = (byte) Utilities.random.nextInt(2);
        int i = 1;
        particle.finishedStart = (byte) (Utilities.random.nextInt(2) + 1);
        if (particle.type == 0) {
            particle.typeSize = (byte) ((int) ((Utilities.random.nextFloat() * 2.0f) + 4.0f));
        } else {
            particle.typeSize = (byte) ((int) ((Utilities.random.nextFloat() * 4.0f) + 4.0f));
        }
        if (z) {
            particle.y = (-Utilities.random.nextFloat()) * ((float) getMeasuredHeight()) * 1.2f;
            particle.x = (float) (AndroidUtilities.dp(5.0f) + Utilities.random.nextInt(getMeasuredWidth() - AndroidUtilities.dp(10.0f)));
            particle.xFinished = particle.finishedStart;
        } else {
            int dp = AndroidUtilities.dp((float) (Utilities.random.nextInt(10) + 4));
            int measuredHeight = getMeasuredHeight() / 4;
            if (particle.side == 0) {
                particle.x = (float) (-dp);
            } else {
                particle.x = (float) (getMeasuredWidth() + dp);
            }
            if (particle.side != 0) {
                i = -1;
            }
            particle.moveX = ((float) i) * (((float) AndroidUtilities.dp(1.2f)) + (Utilities.random.nextFloat() * ((float) AndroidUtilities.dp(4.0f))));
            particle.moveY = -(((float) AndroidUtilities.dp(4.0f)) + (Utilities.random.nextFloat() * ((float) AndroidUtilities.dp(4.0f))));
            particle.y = (float) ((measuredHeight / 2) + Utilities.random.nextInt(measuredHeight * 2));
        }
        return particle;
    }

    public void start() {
        this.particles.clear();
        if (Build.VERSION.SDK_INT >= 18) {
            setLayerType(2, (Paint) null);
        }
        this.started = true;
        this.startedFall = false;
        this.fallingDownCount = 0;
        this.speedCoef = 1.0f;
        for (int i = 0; i < particlesCount; i++) {
            this.particles.add(createParticle(false));
        }
        invalidate();
    }

    private void startFall() {
        if (!this.startedFall) {
            this.startedFall = true;
            for (int i = 0; i < fallParticlesCount; i++) {
                this.particles.add(createParticle(true));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int i = (int) (elapsedRealtime - this.lastUpdateTime);
        this.lastUpdateTime = elapsedRealtime;
        if (i > 18) {
            i = 16;
        }
        int size = this.particles.size();
        int i2 = 0;
        while (i2 < size) {
            Particle particle = this.particles.get(i2);
            particle.draw(canvas);
            if (particle.update(i)) {
                this.particles.remove(i2);
                i2--;
                size--;
            }
            i2++;
        }
        if (this.fallingDownCount >= particlesCount / 2 && this.speedCoef > 0.2f) {
            startFall();
            this.speedCoef -= (((float) i) / 16.0f) * 0.15f;
            if (this.speedCoef < 0.2f) {
                this.speedCoef = 0.2f;
            }
        }
        if (!this.particles.isEmpty()) {
            invalidate();
            return;
        }
        this.started = false;
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    FireworksOverlay.this.lambda$onDraw$0$FireworksOverlay();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onDraw$0$FireworksOverlay() {
        if (!this.started) {
            setLayerType(0, (Paint) null);
        }
    }
}
