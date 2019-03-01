package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class MediaActionDrawable extends Drawable {
    private static final float CANCEL_TO_CHECK_STAGE1 = 0.5f;
    private static final float CANCEL_TO_CHECK_STAGE2 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE1 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE2 = 0.2f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE3 = 0.3f;
    private static final float EPS = 0.001f;
    public static final int ICON_CANCEL = 3;
    public static final int ICON_CANCEL_FILL = 14;
    public static final int ICON_CANCEL_NOPROFRESS = 12;
    public static final int ICON_CANCEL_PERCENT = 13;
    public static final int ICON_CHECK = 6;
    public static final int ICON_DOWNLOAD = 2;
    public static final int ICON_EMPTY = 10;
    public static final int ICON_EMPTY_NOPROGRESS = 11;
    public static final int ICON_FILE = 5;
    public static final int ICON_FIRE = 7;
    public static final int ICON_GIF = 8;
    public static final int ICON_NONE = 4;
    public static final int ICON_PAUSE = 1;
    public static final int ICON_PLAY = 0;
    public static final int ICON_SECRETCHECK = 9;
    private static final float[] pausePath1 = new float[]{16.0f, 17.0f, 32.0f, 17.0f, 32.0f, 22.0f, 16.0f, 22.0f, 16.0f, 19.5f};
    private static final float[] pausePath2 = new float[]{16.0f, 31.0f, 32.0f, 31.0f, 32.0f, 26.0f, 16.0f, 26.0f, 16.0f, 28.5f};
    private static final int pauseRotation = 90;
    private static final float[] playFinalPath = new float[]{18.0f, 15.0f, 34.0f, 24.0f, 18.0f, 33.0f};
    private static final float[] playPath1 = new float[]{18.0f, 15.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final float[] playPath2 = new float[]{18.0f, 33.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final int playRotation = 0;
    private float animatedDownloadProgress;
    private boolean animatingTransition;
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isMini;
    private long lastAnimationTime;
    private int lastPercent = -1;
    private int nextIcon;
    private float overrideAlpha = 1.0f;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private Path path1 = new Path();
    private Path path2 = new Path();
    private String percentString;
    private int percentStringWidth;
    private RectF rect = new RectF();
    private float savedTransitionProgress;
    private float scale = 1.0f;
    private TextPaint textPaint = new TextPaint(1);
    private float transitionAnimationTime = 400.0f;
    private float transitionProgress = 1.0f;

    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStyle(Style.STROKE);
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.paint2.setColor(-1);
        this.paint2.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(2.0f)));
    }

    public void setAlpha(int alpha) {
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        this.paint2.setColorFilter(colorFilter);
        this.textPaint.setColorFilter(colorFilter);
    }

    public void setColor(int value) {
        this.paint.setColor(value | -16777216);
        this.paint2.setColor(value | -16777216);
        this.textPaint.setColor(value | -16777216);
        this.colorFilter = new PorterDuffColorFilter(value, Mode.MULTIPLY);
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean value) {
        this.isMini = value;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(this.isMini ? 2.0f : 3.0f));
    }

    public int getOpacity() {
        return -2;
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int icon, boolean animated) {
        if (animated && this.currentIcon == icon && this.nextIcon != icon) {
            this.currentIcon = this.nextIcon;
            this.transitionProgress = 1.0f;
        }
        if (animated) {
            if (this.currentIcon == icon || this.nextIcon == icon) {
                return false;
            }
            if (this.currentIcon == 2 && (icon == 3 || icon == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (this.currentIcon != 4 && icon == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((this.currentIcon == 4 && icon == 14) || (this.currentIcon == 14 && icon == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            this.animatingTransition = true;
            this.nextIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else if (this.currentIcon == icon) {
            return false;
        } else {
            this.animatingTransition = false;
            this.nextIcon = icon;
            this.currentIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (icon == 3 || icon == 14) {
            this.downloadRadOffset = 112.0f;
            this.animatedDownloadProgress = 0.0f;
            this.downloadProgressAnimationStart = 0.0f;
            this.downloadProgressTime = 0.0f;
        }
        invalidateSelf();
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public int getPreviousIcon() {
        return this.currentIcon;
    }

    public void setProgress(float value, boolean animated) {
        if (animated) {
            if (this.animatedDownloadProgress > value) {
                this.animatedDownloadProgress = value;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        } else {
            this.animatedDownloadProgress = value;
            this.downloadProgressAnimationStart = value;
        }
        this.downloadProgress = value;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    private float getCircleValue(float value) {
        while (value > 360.0f) {
            value -= 360.0f;
        }
        return value;
    }

    public float getProgressAlpha() {
        return 1.0f - this.transitionProgress;
    }

    public float getTransitionProgress() {
        return this.animatingTransition ? this.transitionProgress : 1.0f;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.scale = ((float) (right - left)) / ((float) getIntrinsicWidth());
        if (this.scale < 0.7f) {
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        if (this.delegate != null) {
            this.delegate.invalidate();
        }
    }

    public void draw(Canvas canvas) {
        float progress;
        float transition;
        float d;
        float rotation;
        int alpha;
        int diff;
        float previowsDrawableScale;
        float drawableScale;
        Paint paint;
        int i;
        int y;
        int x;
        int w;
        int h;
        Rect bounds = getBounds();
        int cx = bounds.centerX();
        int cy = bounds.centerY();
        if (this.nextIcon == 4) {
            progress = 1.0f - this.transitionProgress;
            canvas.save();
            canvas.scale(progress, progress, (float) cx, (float) cy);
        } else if ((this.nextIcon == 6 || this.nextIcon == 10) && this.currentIcon == 4) {
            canvas.save();
            canvas.scale(this.transitionProgress, this.transitionProgress, (float) cx, (float) cy);
        }
        int width = AndroidUtilities.dp(3.0f);
        if (this.currentIcon == 2 || this.nextIcon == 2) {
            float yStart2;
            float y1;
            float y2;
            float x1;
            float x2;
            float y3;
            float yStart = ((float) cy) - (((float) AndroidUtilities.dp(9.0f)) * this.scale);
            float yEnd = ((float) cy) + (((float) AndroidUtilities.dp(9.0f)) * this.scale);
            float yEnd2 = ((float) cy) + (((float) AndroidUtilities.dp(12.0f)) * this.scale);
            if ((this.currentIcon == 3 || this.currentIcon == 14) && this.nextIcon == 2) {
                this.paint.setAlpha((int) (255.0f * Math.min(1.0f, this.transitionProgress / 0.5f)));
                transition = this.transitionProgress;
                yStart2 = ((float) cy) + (((float) AndroidUtilities.dp(12.0f)) * this.scale);
            } else {
                if (this.nextIcon == 3 || this.nextIcon == 14 || this.nextIcon == 2) {
                    this.paint.setAlpha(255);
                    transition = this.transitionProgress;
                } else {
                    this.paint.setAlpha((int) ((255.0f * Math.min(1.0f, this.savedTransitionProgress / 0.5f)) * (1.0f - this.transitionProgress)));
                    transition = this.savedTransitionProgress;
                }
                yStart2 = ((float) cy) + (((float) AndroidUtilities.dp(1.0f)) * this.scale);
            }
            if (this.animatingTransition) {
                progress = transition;
                float currentProgress;
                if (this.nextIcon == 2 || progress <= 0.5f) {
                    float currentBackProgress;
                    if (this.nextIcon == 2) {
                        currentBackProgress = transition;
                        currentProgress = 1.0f - currentBackProgress;
                    } else {
                        currentProgress = transition / 0.5f;
                        currentBackProgress = 1.0f - currentProgress;
                    }
                    y1 = yStart + ((yStart2 - yStart) * currentProgress);
                    y2 = yEnd + ((yEnd2 - yEnd) * currentProgress);
                    x1 = ((float) cx) - ((((float) AndroidUtilities.dp(8.0f)) * currentBackProgress) * this.scale);
                    x2 = ((float) cx) + ((((float) AndroidUtilities.dp(8.0f)) * currentBackProgress) * this.scale);
                    y3 = y2 - ((((float) AndroidUtilities.dp(8.0f)) * currentBackProgress) * this.scale);
                } else {
                    float currentProgress2;
                    d = ((float) AndroidUtilities.dp(13.0f)) * this.scale;
                    progress -= 0.5f;
                    float currentProgress3 = progress / 0.5f;
                    if (progress > 0.2f) {
                        currentProgress = 1.0f;
                        currentProgress2 = (progress - 0.2f) / 0.3f;
                    } else {
                        currentProgress = progress / 0.2f;
                        currentProgress2 = 0.0f;
                    }
                    this.rect.set(((float) cx) - d, yEnd2 - (d / 2.0f), (float) cx, (d / 2.0f) + yEnd2);
                    float start = 100.0f * currentProgress2;
                    canvas.drawArc(this.rect, start, (104.0f * currentProgress3) - start, false, this.paint);
                    float y12 = yStart2 + ((yEnd2 - yStart2) * currentProgress);
                    y3 = yEnd2;
                    float y22 = yEnd2;
                    x2 = (float) cx;
                    x1 = x2;
                    if (currentProgress2 > 0.0f) {
                        rotation = -45.0f * (1.0f - currentProgress2);
                        d = (((float) AndroidUtilities.dp(7.0f)) * currentProgress2) * this.scale;
                        alpha = (int) (255.0f * currentProgress2);
                        if (!(this.nextIcon == 3 || this.nextIcon == 14 || this.nextIcon == 2)) {
                            alpha = (int) (((float) alpha) * (1.0f - Math.min(1.0f, this.transitionProgress / 0.5f)));
                        }
                        if (rotation != 0.0f) {
                            canvas.save();
                            canvas.rotate(rotation, (float) cx, (float) cy);
                        }
                        if (alpha != 0) {
                            this.paint.setAlpha(alpha);
                            canvas.drawLine(((float) cx) - d, ((float) cy) - d, ((float) cx) + d, ((float) cy) + d, this.paint);
                            canvas.drawLine(((float) cx) + d, ((float) cy) - d, ((float) cx) - d, ((float) cy) + d, this.paint);
                            if (this.nextIcon == 14) {
                                this.paint.setAlpha((int) (((float) alpha) * 0.15f));
                                diff = AndroidUtilities.dp(this.isMini ? 2.0f : 4.0f);
                                this.rect.set((float) (bounds.left + diff), (float) (bounds.top + diff), (float) (bounds.right - diff), (float) (bounds.bottom - diff));
                                canvas.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                                this.paint.setAlpha(alpha);
                            }
                        }
                        if (rotation != 0.0f) {
                            canvas.restore();
                        }
                    }
                    y2 = y22;
                    y1 = y12;
                }
            } else {
                y1 = yStart;
                y2 = yEnd;
                x1 = ((float) cx) - (((float) AndroidUtilities.dp(8.0f)) * this.scale);
                x2 = ((float) cx) + (((float) AndroidUtilities.dp(8.0f)) * this.scale);
                y3 = y2 - (((float) AndroidUtilities.dp(8.0f)) * this.scale);
            }
            if (y1 != y2) {
                canvas.drawLine((float) cx, y1, (float) cx, y2, this.paint);
            }
            if (x1 != ((float) cx)) {
                canvas.drawLine(x1, y3, (float) cx, y2, this.paint);
                canvas.drawLine(x2, y3, (float) cx, y2, this.paint);
            }
        }
        if (this.currentIcon == 3 || this.currentIcon == 14 || (this.currentIcon == 4 && this.nextIcon == 14)) {
            float iconScale = 1.0f;
            float backProgress;
            if (this.nextIcon == 2) {
                if (this.transitionProgress <= 0.5f) {
                    backProgress = 1.0f - (this.transitionProgress / 0.5f);
                    d = (((float) AndroidUtilities.dp(7.0f)) * backProgress) * this.scale;
                    alpha = (int) (255.0f * backProgress);
                } else {
                    d = 0.0f;
                    alpha = 0;
                }
                rotation = 0.0f;
            } else if (this.nextIcon == 0 || this.nextIcon == 1 || this.nextIcon == 5 || this.nextIcon == 8 || this.nextIcon == 9 || this.nextIcon == 7 || this.nextIcon == 6) {
                if (this.nextIcon == 6) {
                    progress = Math.min(1.0f, this.transitionProgress / 0.5f);
                    backProgress = 1.0f - progress;
                } else {
                    progress = this.transitionProgress;
                    backProgress = 1.0f - progress;
                }
                rotation = 45.0f * progress;
                d = (((float) AndroidUtilities.dp(7.0f)) * backProgress) * this.scale;
                alpha = (int) (255.0f * Math.min(1.0f, 2.0f * backProgress));
            } else if (this.nextIcon == 4) {
                progress = this.transitionProgress;
                backProgress = 1.0f - progress;
                d = ((float) AndroidUtilities.dp(7.0f)) * this.scale;
                alpha = (int) (255.0f * backProgress);
                if (this.currentIcon == 14) {
                    rotation = 0.0f;
                    iconScale = backProgress;
                } else {
                    rotation = 45.0f * progress;
                    iconScale = 1.0f;
                }
            } else if (this.nextIcon == 14) {
                progress = this.transitionProgress;
                backProgress = 1.0f - progress;
                if (this.currentIcon == 4) {
                    rotation = 0.0f;
                    iconScale = progress;
                } else {
                    rotation = 45.0f * backProgress;
                    iconScale = 1.0f;
                }
                d = ((float) AndroidUtilities.dp(7.0f)) * this.scale;
                alpha = (int) (255.0f * progress);
            } else {
                rotation = 0.0f;
                d = ((float) AndroidUtilities.dp(7.0f)) * this.scale;
                alpha = 255;
            }
            if (iconScale != 1.0f) {
                canvas.save();
                canvas.scale(iconScale, iconScale, (float) bounds.left, (float) bounds.top);
            }
            if (rotation != 0.0f) {
                canvas.save();
                canvas.rotate(rotation, (float) cx, (float) cy);
            }
            if (alpha != 0) {
                this.paint.setAlpha((int) (((float) alpha) * this.overrideAlpha));
                canvas.drawLine(((float) cx) - d, ((float) cy) - d, ((float) cx) + d, ((float) cy) + d, this.paint);
                canvas.drawLine(((float) cx) + d, ((float) cy) - d, ((float) cx) - d, ((float) cy) + d, this.paint);
            }
            if (rotation != 0.0f) {
                canvas.restore();
            }
            if ((this.currentIcon == 3 || this.currentIcon == 14 || (this.currentIcon == 4 && this.nextIcon == 14)) && alpha != 0) {
                float rad = Math.max(4.0f, 360.0f * this.animatedDownloadProgress);
                diff = AndroidUtilities.dp(this.isMini ? 2.0f : 4.0f);
                this.rect.set((float) (bounds.left + diff), (float) (bounds.top + diff), (float) (bounds.right - diff), (float) (bounds.bottom - diff));
                if (this.currentIcon == 14 || (this.currentIcon == 4 && this.nextIcon == 14)) {
                    this.paint.setAlpha((int) ((((float) alpha) * 0.15f) * this.overrideAlpha));
                    canvas.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                    this.paint.setAlpha(alpha);
                }
                canvas.drawArc(this.rect, this.downloadRadOffset, rad, false, this.paint);
            }
            if (iconScale != 1.0f) {
                canvas.restore();
            }
        } else if (this.currentIcon == 10 || this.nextIcon == 10 || this.currentIcon == 13) {
            if (this.nextIcon == 4 || this.nextIcon == 6) {
                alpha = (int) (255.0f * (1.0f - this.transitionProgress));
            } else {
                alpha = 255;
            }
            if (alpha != 0) {
                this.paint.setAlpha((int) (((float) alpha) * this.overrideAlpha));
                float rad2 = Math.max(4.0f, 360.0f * this.animatedDownloadProgress);
                diff = AndroidUtilities.dp(this.isMini ? 2.0f : 4.0f);
                this.rect.set((float) (bounds.left + diff), (float) (bounds.top + diff), (float) (bounds.right - diff), (float) (bounds.bottom - diff));
                canvas.drawArc(this.rect, this.downloadRadOffset, rad2, false, this.paint);
            }
        }
        Drawable nextDrawable = null;
        Drawable previousDrawable = null;
        if (this.currentIcon == this.nextIcon) {
            previowsDrawableScale = 1.0f;
            drawableScale = 1.0f;
        } else {
            drawableScale = Math.min(1.0f, this.transitionProgress / 0.5f);
            previowsDrawableScale = Math.max(0.0f, 1.0f - (this.transitionProgress / 0.5f));
        }
        if (this.nextIcon == 5) {
            nextDrawable = Theme.chat_fileIcon;
        } else if (this.currentIcon == 5) {
            previousDrawable = Theme.chat_fileIcon;
        }
        if (this.nextIcon == 7) {
            nextDrawable = Theme.chat_flameIcon;
        } else if (this.currentIcon == 7) {
            previousDrawable = Theme.chat_flameIcon;
        }
        if (this.nextIcon == 8) {
            nextDrawable = Theme.chat_gifIcon;
        } else if (this.currentIcon == 8) {
            previousDrawable = Theme.chat_gifIcon;
        }
        if (this.currentIcon == 9 || this.nextIcon == 9) {
            paint = this.paint;
            if (this.currentIcon == this.nextIcon) {
                i = 255;
            } else {
                i = (int) (this.transitionProgress * 255.0f);
            }
            paint.setAlpha(i);
            y = cy + AndroidUtilities.dp(7.0f);
            x = cx - AndroidUtilities.dp(3.0f);
            if (this.currentIcon != this.nextIcon) {
                canvas.save();
                canvas.scale(this.transitionProgress, this.transitionProgress, (float) cx, (float) cy);
            }
            canvas.drawLine((float) (x - AndroidUtilities.dp(6.0f)), (float) (y - AndroidUtilities.dp(6.0f)), (float) x, (float) y, this.paint);
            canvas.drawLine((float) x, (float) y, (float) (AndroidUtilities.dp(12.0f) + x), (float) (y - AndroidUtilities.dp(12.0f)), this.paint);
            if (this.currentIcon != this.nextIcon) {
                canvas.restore();
            }
        }
        if (this.currentIcon == 12 || this.nextIcon == 12) {
            if (this.currentIcon == this.nextIcon) {
                transition = 1.0f;
            } else if (this.nextIcon == 13) {
                transition = this.transitionProgress;
            } else {
                transition = 1.0f - this.transitionProgress;
            }
            paint = this.paint;
            if (this.currentIcon == this.nextIcon) {
                i = 255;
            } else {
                i = (int) (255.0f * transition);
            }
            paint.setAlpha(i);
            y = cy + AndroidUtilities.dp(7.0f);
            x = cx - AndroidUtilities.dp(3.0f);
            if (this.currentIcon != this.nextIcon) {
                canvas.save();
                canvas.scale(transition, transition, (float) cx, (float) cy);
            }
            d = ((float) AndroidUtilities.dp(7.0f)) * this.scale;
            canvas.drawLine(((float) cx) - d, ((float) cy) - d, ((float) cx) + d, ((float) cy) + d, this.paint);
            canvas.drawLine(((float) cx) + d, ((float) cy) - d, ((float) cx) - d, ((float) cy) + d, this.paint);
            if (this.currentIcon != this.nextIcon) {
                canvas.restore();
            }
        }
        if (this.currentIcon == 13 || this.nextIcon == 13) {
            if (this.currentIcon == this.nextIcon) {
                transition = 1.0f;
            } else if (this.nextIcon == 13) {
                transition = this.transitionProgress;
            } else {
                transition = 1.0f - this.transitionProgress;
            }
            this.textPaint.setAlpha((int) (255.0f * transition));
            y = cy + AndroidUtilities.dp(5.0f);
            x = cx - (this.percentStringWidth / 2);
            if (this.currentIcon != this.nextIcon) {
                canvas.save();
                canvas.scale(transition, transition, (float) cx, (float) cy);
            }
            int newPercent = (int) (this.animatedDownloadProgress * 100.0f);
            if (this.percentString == null || newPercent != this.lastPercent) {
                this.lastPercent = newPercent;
                this.percentString = String.format("%d%%", new Object[]{Integer.valueOf(this.lastPercent)});
                this.percentStringWidth = (int) Math.ceil((double) this.textPaint.measureText(this.percentString));
            }
            canvas.drawText(this.percentString, (float) x, (float) y, this.textPaint);
            if (this.currentIcon != this.nextIcon) {
                canvas.restore();
            }
        }
        if (this.currentIcon == 0 || this.currentIcon == 1 || this.nextIcon == 0 || this.nextIcon == 1) {
            float[] p1;
            float[] p2;
            int rotation1;
            float[] p3;
            float[] p4;
            int rotation2;
            float p = ((this.currentIcon == 0 && this.nextIcon == 1) || (this.currentIcon == 1 && this.nextIcon == 0)) ? this.animatingTransition ? this.interpolator.getInterpolation(this.transitionProgress) : 0.0f : 0.0f;
            this.path1.reset();
            this.path2.reset();
            float[] finalPath = null;
            switch (this.currentIcon) {
                case 0:
                    p1 = playPath1;
                    p2 = playPath2;
                    finalPath = playFinalPath;
                    rotation1 = 0;
                    break;
                case 1:
                    p1 = pausePath1;
                    p2 = pausePath2;
                    rotation1 = 90;
                    break;
                default:
                    rotation1 = 0;
                    p2 = null;
                    p1 = null;
                    break;
            }
            switch (this.nextIcon) {
                case 0:
                    p3 = playPath1;
                    p4 = playPath2;
                    rotation2 = 0;
                    break;
                case 1:
                    p3 = pausePath1;
                    p4 = pausePath2;
                    rotation2 = 90;
                    break;
                default:
                    rotation2 = 0;
                    p4 = null;
                    p3 = null;
                    break;
            }
            if (p1 == null) {
                p1 = p3;
                p2 = p4;
                p3 = null;
                p4 = null;
            }
            int a;
            if (!this.animatingTransition && finalPath != null) {
                for (a = 0; a < finalPath.length / 2; a++) {
                    if (a == 0) {
                        this.path1.moveTo(((float) AndroidUtilities.dp(finalPath[a * 2])) * this.scale, ((float) AndroidUtilities.dp(finalPath[(a * 2) + 1])) * this.scale);
                        this.path2.moveTo(((float) AndroidUtilities.dp(finalPath[a * 2])) * this.scale, ((float) AndroidUtilities.dp(finalPath[(a * 2) + 1])) * this.scale);
                    } else {
                        this.path1.lineTo(((float) AndroidUtilities.dp(finalPath[a * 2])) * this.scale, ((float) AndroidUtilities.dp(finalPath[(a * 2) + 1])) * this.scale);
                        this.path2.lineTo(((float) AndroidUtilities.dp(finalPath[a * 2])) * this.scale, ((float) AndroidUtilities.dp(finalPath[(a * 2) + 1])) * this.scale);
                    }
                }
            } else if (p3 == null) {
                for (a = 0; a < 5; a++) {
                    if (a == 0) {
                        this.path1.moveTo(((float) AndroidUtilities.dp(p1[a * 2])) * this.scale, ((float) AndroidUtilities.dp(p1[(a * 2) + 1])) * this.scale);
                        this.path2.moveTo(((float) AndroidUtilities.dp(p2[a * 2])) * this.scale, ((float) AndroidUtilities.dp(p2[(a * 2) + 1])) * this.scale);
                    } else {
                        this.path1.lineTo(((float) AndroidUtilities.dp(p1[a * 2])) * this.scale, ((float) AndroidUtilities.dp(p1[(a * 2) + 1])) * this.scale);
                        this.path2.lineTo(((float) AndroidUtilities.dp(p2[a * 2])) * this.scale, ((float) AndroidUtilities.dp(p2[(a * 2) + 1])) * this.scale);
                    }
                }
                paint = this.paint2;
                if (this.currentIcon == this.nextIcon) {
                    i = 255;
                } else {
                    i = (int) (this.transitionProgress * 255.0f);
                }
                paint.setAlpha(i);
            } else {
                for (a = 0; a < 5; a++) {
                    if (a == 0) {
                        this.path1.moveTo(((float) AndroidUtilities.dp(p1[a * 2] + ((p3[a * 2] - p1[a * 2]) * p))) * this.scale, ((float) AndroidUtilities.dp(p1[(a * 2) + 1] + ((p3[(a * 2) + 1] - p1[(a * 2) + 1]) * p))) * this.scale);
                        this.path2.moveTo(((float) AndroidUtilities.dp(p2[a * 2] + ((p4[a * 2] - p2[a * 2]) * p))) * this.scale, ((float) AndroidUtilities.dp(p2[(a * 2) + 1] + ((p4[(a * 2) + 1] - p2[(a * 2) + 1]) * p))) * this.scale);
                    } else {
                        this.path1.lineTo(((float) AndroidUtilities.dp(p1[a * 2] + ((p3[a * 2] - p1[a * 2]) * p))) * this.scale, ((float) AndroidUtilities.dp(p1[(a * 2) + 1] + ((p3[(a * 2) + 1] - p1[(a * 2) + 1]) * p))) * this.scale);
                        this.path2.lineTo(((float) AndroidUtilities.dp(p2[a * 2] + ((p4[a * 2] - p2[a * 2]) * p))) * this.scale, ((float) AndroidUtilities.dp(p2[(a * 2) + 1] + ((p4[(a * 2) + 1] - p2[(a * 2) + 1]) * p))) * this.scale);
                    }
                }
                this.paint2.setAlpha(255);
            }
            this.path1.close();
            this.path2.close();
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            canvas.rotate(((float) rotation1) + (((float) (rotation2 - rotation1)) * p), (float) (cx - bounds.left), (float) (cy - bounds.top));
            if (!(this.currentIcon == 0 || this.currentIcon == 1)) {
                canvas.scale(drawableScale, drawableScale, (float) (cx - bounds.left), (float) (cy - bounds.top));
            }
            canvas.drawPath(this.path1, this.paint2);
            canvas.drawPath(this.path2, this.paint2);
            canvas.restore();
        }
        if (this.currentIcon == 6 || this.nextIcon == 6) {
            float progress1;
            float progress2;
            if (this.currentIcon == 6) {
                progress1 = 0.0f;
                progress2 = 1.0f;
            } else if (this.transitionProgress > 0.5f) {
                progress = (this.transitionProgress - 0.5f) / 0.5f;
                progress1 = 1.0f - Math.min(1.0f, progress / 0.5f);
                progress2 = progress > 0.5f ? (progress - 0.5f) / 0.5f : 0.0f;
            } else {
                progress1 = 1.0f;
                progress2 = 0.0f;
            }
            y = cy + AndroidUtilities.dp(7.0f);
            x = cx - AndroidUtilities.dp(3.0f);
            this.paint.setAlpha(255);
            if (progress1 < 1.0f) {
                canvas.drawLine((float) (x - AndroidUtilities.dp(6.0f)), (float) (y - AndroidUtilities.dp(6.0f)), ((float) x) - (((float) AndroidUtilities.dp(6.0f)) * progress1), ((float) y) - (((float) AndroidUtilities.dp(6.0f)) * progress1), this.paint);
            }
            if (progress2 > 0.0f) {
                canvas.drawLine((float) x, (float) y, ((float) x) + (((float) AndroidUtilities.dp(12.0f)) * progress2), ((float) y) - (((float) AndroidUtilities.dp(12.0f)) * progress2), this.paint);
            }
        }
        if (!(previousDrawable == null || previousDrawable == nextDrawable)) {
            w = (int) (((float) previousDrawable.getIntrinsicWidth()) * previowsDrawableScale);
            h = (int) (((float) previousDrawable.getIntrinsicHeight()) * previowsDrawableScale);
            previousDrawable.setColorFilter(this.colorFilter);
            previousDrawable.setAlpha(this.currentIcon == this.nextIcon ? 255 : (int) ((1.0f - this.transitionProgress) * 255.0f));
            previousDrawable.setBounds(cx - (w / 2), cy - (h / 2), (w / 2) + cx, (h / 2) + cy);
            previousDrawable.draw(canvas);
        }
        if (nextDrawable != null) {
            w = (int) (((float) nextDrawable.getIntrinsicWidth()) * drawableScale);
            h = (int) (((float) nextDrawable.getIntrinsicHeight()) * drawableScale);
            nextDrawable.setColorFilter(this.colorFilter);
            if (this.currentIcon == this.nextIcon) {
                i = 255;
            } else {
                i = (int) (this.transitionProgress * 255.0f);
            }
            nextDrawable.setAlpha(i);
            nextDrawable.setBounds(cx - (w / 2), cy - (h / 2), (w / 2) + cx, (h / 2) + cy);
            nextDrawable.draw(canvas);
        }
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastAnimationTime;
        if (dt > 17) {
            dt = 17;
        }
        this.lastAnimationTime = newTime;
        if (this.currentIcon == 3 || this.currentIcon == 14 || ((this.currentIcon == 4 && this.nextIcon == 14) || this.currentIcon == 10 || this.currentIcon == 13)) {
            this.downloadRadOffset += ((float) (360 * dt)) / 2500.0f;
            this.downloadRadOffset = getCircleValue(this.downloadRadOffset);
            if (this.nextIcon != 2) {
                float progressDiff = this.downloadProgress - this.downloadProgressAnimationStart;
                if (progressDiff > 0.0f) {
                    this.downloadProgressTime += (float) dt;
                    if (this.downloadProgressTime >= 200.0f) {
                        this.animatedDownloadProgress = this.downloadProgress;
                        this.downloadProgressAnimationStart = this.downloadProgress;
                        this.downloadProgressTime = 0.0f;
                    } else {
                        this.animatedDownloadProgress = this.downloadProgressAnimationStart + (this.interpolator.getInterpolation(this.downloadProgressTime / 200.0f) * progressDiff);
                    }
                }
            }
            invalidateSelf();
        }
        if (this.animatingTransition && this.transitionProgress < 1.0f) {
            this.transitionProgress += ((float) dt) / this.transitionAnimationTime;
            if (this.transitionProgress >= 1.0f) {
                this.currentIcon = this.nextIcon;
                this.transitionProgress = 1.0f;
                this.animatingTransition = false;
            }
            invalidateSelf();
        }
        if (this.nextIcon == 4 || ((this.nextIcon == 6 || this.nextIcon == 10) && this.currentIcon == 4)) {
            canvas.restore();
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(48.0f);
    }
}
