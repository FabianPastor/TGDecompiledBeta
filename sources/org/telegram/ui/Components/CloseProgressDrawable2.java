package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public CloseProgressDrawable2() {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStyle(Style.STROKE);
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void draw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        int centerX;
        int centerY;
        RectF rectF;
        Canvas canvas2 = canvas;
        long currentTimeMillis = System.currentTimeMillis();
        float f6 = 0.0f;
        if (this.lastFrameTime != 0) {
            long j = currentTimeMillis - r0.lastFrameTime;
            if (r0.animating || r0.angle != 0.0f) {
                r0.angle += ((float) (360 * j)) / 500.0f;
                if (r0.animating || r0.angle < 720.0f) {
                    r0.angle -= (float) (((int) (r0.angle / 720.0f)) * 720);
                } else {
                    r0.angle = 0.0f;
                }
                invalidateSelf();
            }
        }
        canvas.save();
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas2.rotate(-45.0f);
        if (r0.angle >= 0.0f && r0.angle < 90.0f) {
            f = 1.0f - (r0.angle / 90.0f);
            f2 = 0.0f;
            f3 = 1.0f;
        } else if (r0.angle >= 90.0f && r0.angle < 180.0f) {
            f3 = 1.0f - ((r0.angle - 90.0f) / 90.0f);
            f = 0.0f;
            f2 = f;
            f4 = 1.0f;
            if (f == 0.0f) {
                f5 = 8.0f;
                canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f, r0.paint);
            } else {
                f5 = 8.0f;
            }
            if (f3 != 0.0f) {
                canvas2.drawLine(((float) (-AndroidUtilities.dp(f5))) * f3, 0.0f, 0.0f, 0.0f, r0.paint);
            }
            if (f4 != 0.0f) {
                canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f5))) * f4, 0.0f, 0.0f, r0.paint);
            }
            if (f2 != 1.0f) {
                canvas2.drawLine(((float) AndroidUtilities.dp(f5)) * f2, 0.0f, (float) AndroidUtilities.dp(f5), 0.0f, r0.paint);
            }
            canvas.restore();
            centerX = getBounds().centerX();
            centerY = getBounds().centerY();
            r0.rect.set((float) (centerX - AndroidUtilities.dp(f5)), (float) (centerY - AndroidUtilities.dp(f5)), (float) (centerX + AndroidUtilities.dp(f5)), (float) (centerY + AndroidUtilities.dp(f5)));
            rectF = r0.rect;
            if (r0.angle < 360.0f) {
                f6 = r0.angle - 360.0f;
            }
            if (r0.angle >= 360.0f) {
            }
            canvas2.drawArc(rectF, f6 - 45.0f, r0.angle >= 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
            r0.lastFrameTime = currentTimeMillis;
        } else if (r0.angle < 180.0f || r0.angle >= 270.0f) {
            if (r0.angle >= 270.0f && r0.angle < 360.0f) {
                f = (r0.angle - 270.0f) / 90.0f;
            } else if (r0.angle >= 360.0f && r0.angle < 450.0f) {
                f = 1.0f - ((r0.angle - 360.0f) / 90.0f);
            } else if (r0.angle >= 450.0f && r0.angle < 540.0f) {
                f = (r0.angle - 450.0f) / 90.0f;
                f3 = 0.0f;
                f4 = f3;
                f2 = f4;
                if (f == 0.0f) {
                    f5 = 8.0f;
                    canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f, r0.paint);
                } else {
                    f5 = 8.0f;
                }
                if (f3 != 0.0f) {
                    canvas2.drawLine(((float) (-AndroidUtilities.dp(f5))) * f3, 0.0f, 0.0f, 0.0f, r0.paint);
                }
                if (f4 != 0.0f) {
                    canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f5))) * f4, 0.0f, 0.0f, r0.paint);
                }
                if (f2 != 1.0f) {
                    canvas2.drawLine(((float) AndroidUtilities.dp(f5)) * f2, 0.0f, (float) AndroidUtilities.dp(f5), 0.0f, r0.paint);
                }
                canvas.restore();
                centerX = getBounds().centerX();
                centerY = getBounds().centerY();
                r0.rect.set((float) (centerX - AndroidUtilities.dp(f5)), (float) (centerY - AndroidUtilities.dp(f5)), (float) (centerX + AndroidUtilities.dp(f5)), (float) (centerY + AndroidUtilities.dp(f5)));
                rectF = r0.rect;
                if (r0.angle < 360.0f) {
                    f6 = r0.angle - 360.0f;
                }
                if (r0.angle >= 360.0f) {
                }
                canvas2.drawArc(rectF, f6 - 45.0f, r0.angle >= 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
                r0.lastFrameTime = currentTimeMillis;
            } else if (r0.angle >= 540.0f && r0.angle < 630.0f) {
                f3 = (r0.angle - 540.0f) / 90.0f;
                f4 = 0.0f;
                f2 = f4;
                f = 1.0f;
                if (f == 0.0f) {
                    f5 = 8.0f;
                } else {
                    f5 = 8.0f;
                    canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f, r0.paint);
                }
                if (f3 != 0.0f) {
                    canvas2.drawLine(((float) (-AndroidUtilities.dp(f5))) * f3, 0.0f, 0.0f, 0.0f, r0.paint);
                }
                if (f4 != 0.0f) {
                    canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f5))) * f4, 0.0f, 0.0f, r0.paint);
                }
                if (f2 != 1.0f) {
                    canvas2.drawLine(((float) AndroidUtilities.dp(f5)) * f2, 0.0f, (float) AndroidUtilities.dp(f5), 0.0f, r0.paint);
                }
                canvas.restore();
                centerX = getBounds().centerX();
                centerY = getBounds().centerY();
                r0.rect.set((float) (centerX - AndroidUtilities.dp(f5)), (float) (centerY - AndroidUtilities.dp(f5)), (float) (centerX + AndroidUtilities.dp(f5)), (float) (centerY + AndroidUtilities.dp(f5)));
                rectF = r0.rect;
                if (r0.angle < 360.0f) {
                    f6 = r0.angle - 360.0f;
                }
                if (r0.angle >= 360.0f) {
                }
                canvas2.drawArc(rectF, f6 - 45.0f, r0.angle >= 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
                r0.lastFrameTime = currentTimeMillis;
            } else if (r0.angle < 630.0f || r0.angle >= 720.0f) {
                f2 = 0.0f;
                f = 1.0f;
                f3 = f;
            } else {
                f4 = (r0.angle - 630.0f) / 90.0f;
                f2 = 0.0f;
                f = 1.0f;
                f3 = f;
                if (f == 0.0f) {
                    f5 = 8.0f;
                    canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f, r0.paint);
                } else {
                    f5 = 8.0f;
                }
                if (f3 != 0.0f) {
                    canvas2.drawLine(((float) (-AndroidUtilities.dp(f5))) * f3, 0.0f, 0.0f, 0.0f, r0.paint);
                }
                if (f4 != 0.0f) {
                    canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f5))) * f4, 0.0f, 0.0f, r0.paint);
                }
                if (f2 != 1.0f) {
                    canvas2.drawLine(((float) AndroidUtilities.dp(f5)) * f2, 0.0f, (float) AndroidUtilities.dp(f5), 0.0f, r0.paint);
                }
                canvas.restore();
                centerX = getBounds().centerX();
                centerY = getBounds().centerY();
                r0.rect.set((float) (centerX - AndroidUtilities.dp(f5)), (float) (centerY - AndroidUtilities.dp(f5)), (float) (centerX + AndroidUtilities.dp(f5)), (float) (centerY + AndroidUtilities.dp(f5)));
                rectF = r0.rect;
                if (r0.angle < 360.0f) {
                    f6 = r0.angle - 360.0f;
                }
                canvas2.drawArc(rectF, f6 - 45.0f, r0.angle >= 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
                r0.lastFrameTime = currentTimeMillis;
            }
            f2 = f;
            f = 0.0f;
            f3 = f;
        } else {
            f4 = 1.0f - ((r0.angle - 180.0f) / 90.0f);
            f = 0.0f;
            f3 = f;
            f2 = f3;
            if (f == 0.0f) {
                f5 = 8.0f;
            } else {
                f5 = 8.0f;
                canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f, r0.paint);
            }
            if (f3 != 0.0f) {
                canvas2.drawLine(((float) (-AndroidUtilities.dp(f5))) * f3, 0.0f, 0.0f, 0.0f, r0.paint);
            }
            if (f4 != 0.0f) {
                canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f5))) * f4, 0.0f, 0.0f, r0.paint);
            }
            if (f2 != 1.0f) {
                canvas2.drawLine(((float) AndroidUtilities.dp(f5)) * f2, 0.0f, (float) AndroidUtilities.dp(f5), 0.0f, r0.paint);
            }
            canvas.restore();
            centerX = getBounds().centerX();
            centerY = getBounds().centerY();
            r0.rect.set((float) (centerX - AndroidUtilities.dp(f5)), (float) (centerY - AndroidUtilities.dp(f5)), (float) (centerX + AndroidUtilities.dp(f5)), (float) (centerY + AndroidUtilities.dp(f5)));
            rectF = r0.rect;
            if (r0.angle < 360.0f) {
                f6 = r0.angle - 360.0f;
            }
            if (r0.angle >= 360.0f) {
            }
            canvas2.drawArc(rectF, f6 - 45.0f, r0.angle >= 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
            r0.lastFrameTime = currentTimeMillis;
        }
        f4 = f3;
        if (f == 0.0f) {
            f5 = 8.0f;
        } else {
            f5 = 8.0f;
            canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f, r0.paint);
        }
        if (f3 != 0.0f) {
            canvas2.drawLine(((float) (-AndroidUtilities.dp(f5))) * f3, 0.0f, 0.0f, 0.0f, r0.paint);
        }
        if (f4 != 0.0f) {
            canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f5))) * f4, 0.0f, 0.0f, r0.paint);
        }
        if (f2 != 1.0f) {
            canvas2.drawLine(((float) AndroidUtilities.dp(f5)) * f2, 0.0f, (float) AndroidUtilities.dp(f5), 0.0f, r0.paint);
        }
        canvas.restore();
        centerX = getBounds().centerX();
        centerY = getBounds().centerY();
        r0.rect.set((float) (centerX - AndroidUtilities.dp(f5)), (float) (centerY - AndroidUtilities.dp(f5)), (float) (centerX + AndroidUtilities.dp(f5)), (float) (centerY + AndroidUtilities.dp(f5)));
        rectF = r0.rect;
        if (r0.angle < 360.0f) {
            f6 = r0.angle - 360.0f;
        }
        if (r0.angle >= 360.0f) {
        }
        canvas2.drawArc(rectF, f6 - 45.0f, r0.angle >= 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
        r0.lastFrameTime = currentTimeMillis;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
