package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.SeekBar;

public class SeekBarWaveform {
    private static Paint paintInner;
    private static Paint paintOuter;
    private Path alphaPath;
    private ArrayList<Float> animatedValues;
    private AnimatedFloat appearFloat = new AnimatedFloat(125, 600, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);
    private float clearProgress = 1.0f;
    private SeekBar.SeekBarDelegate delegate;
    private float[] fromHeights;
    private int fromWidth;
    private int height;
    private float[] heights;
    private int innerColor;
    private boolean isUnread;
    private boolean loading;
    private AnimatedFloat loadingFloat = new AnimatedFloat(150, CubicBezierInterpolator.DEFAULT);
    private Paint loadingPaint;
    private int loadingPaintColor1;
    private int loadingPaintColor2;
    private float loadingPaintWidth;
    private long loadingStart;
    private MessageObject messageObject;
    private int outerColor;
    private View parentView;
    private Path path;
    private boolean pressed = false;
    private float progress;
    private boolean selected;
    private int selectedColor;
    private boolean startDraging = false;
    private float startX;
    private int thumbDX = 0;
    private int thumbX = 0;
    private float[] toHeights;
    private int toWidth;
    private float waveScaling = 1.0f;
    private byte[] waveformBytes;
    private int width;

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint(1);
            paintOuter = new Paint(1);
            paintInner.setStyle(Paint.Style.FILL);
            paintOuter.setStyle(Paint.Style.FILL);
        }
    }

    public void setDelegate(SeekBar.SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setColors(int i, int i2, int i3) {
        this.innerColor = i;
        this.outerColor = i2;
        this.selectedColor = i3;
    }

    public void setWaveform(byte[] bArr) {
        this.waveformBytes = bArr;
        this.heights = calculateHeights((int) (((float) this.width) / AndroidUtilities.dpf2(3.0f)));
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setMessageObject(MessageObject messageObject2) {
        MessageObject messageObject3;
        if (!(this.animatedValues == null || (messageObject3 = this.messageObject) == null || messageObject2 == null || messageObject3.getId() == messageObject2.getId())) {
            this.animatedValues.clear();
        }
        this.messageObject = messageObject2;
    }

    public void setParentView(View view) {
        this.parentView = view;
        this.loadingFloat.setParent(view);
        this.appearFloat.setParent(view);
    }

    public boolean isStartDraging() {
        return this.startDraging;
    }

    public boolean onTouch(int i, float f, float f2) {
        SeekBar.SeekBarDelegate seekBarDelegate;
        if (i == 0) {
            if (0.0f <= f && f <= ((float) this.width) && f2 >= 0.0f && f2 <= ((float) this.height)) {
                this.startX = f;
                this.pressed = true;
                this.thumbDX = (int) (f - ((float) this.thumbX));
                this.startDraging = false;
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                if (i == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) this.width));
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            if (this.startDraging) {
                int i2 = (int) (f - ((float) this.thumbDX));
                this.thumbX = i2;
                if (i2 < 0) {
                    this.thumbX = 0;
                } else {
                    int i3 = this.width;
                    if (i2 > i3) {
                        this.thumbX = i3;
                    }
                }
                this.progress = ((float) this.thumbX) / ((float) this.width);
            }
            float f3 = this.startX;
            if (f3 != -1.0f && Math.abs(f - f3) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
                View view = this.parentView;
                if (!(view == null || view.getParent() == null)) {
                    this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.startDraging = true;
                this.startX = -1.0f;
            }
            return true;
        }
        return false;
    }

    public float getProgress() {
        return ((float) this.thumbX) / ((float) this.width);
    }

    public void setProgress(float f) {
        setProgress(f, false);
    }

    public void setProgress(float f, boolean z) {
        boolean z2 = this.isUnread;
        this.progress = z2 ? 1.0f : f;
        int i = z2 ? this.width : this.thumbX;
        if (z && i != 0 && f == 0.0f) {
            this.clearProgress = 0.0f;
        } else if (!z) {
            this.clearProgress = 1.0f;
        }
        int ceil = (int) Math.ceil((double) (((float) this.width) * f));
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i2 = this.width;
        if (ceil > i2) {
            this.thumbX = i2;
        }
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSize(int i, int i2) {
        setSize(i, i2, i, i);
    }

    public void setSize(int i, int i2, int i3, int i4) {
        this.width = i;
        this.height = i2;
        float[] fArr = this.heights;
        if (fArr == null || fArr.length != ((int) (((float) i) / AndroidUtilities.dpf2(3.0f)))) {
            this.heights = calculateHeights((int) (((float) this.width) / AndroidUtilities.dpf2(3.0f)));
        }
        if (i3 != i4 && (this.fromWidth != i3 || this.toWidth != i4)) {
            this.fromWidth = i3;
            this.toWidth = i4;
            this.fromHeights = calculateHeights((int) (((float) i3) / AndroidUtilities.dpf2(3.0f)));
            this.toHeights = calculateHeights((int) (((float) this.toWidth) / AndroidUtilities.dpf2(3.0f)));
        } else if (i3 == i4) {
            this.toHeights = null;
            this.fromHeights = null;
        }
    }

    public void setSent() {
        this.appearFloat.set(0.0f, true);
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    private float[] calculateHeights(int i) {
        int i2 = i;
        byte[] bArr = this.waveformBytes;
        if (bArr == null || i2 <= 0) {
            return null;
        }
        float[] fArr = new float[i2];
        int i3 = 5;
        int length = (bArr.length * 8) / 5;
        float f = ((float) length) / ((float) i2);
        int i4 = 0;
        int i5 = 0;
        float f2 = 0.0f;
        int i6 = 0;
        while (i4 < length) {
            if (i4 == i5) {
                int i7 = i5;
                int i8 = 0;
                while (i5 == i7) {
                    f2 += f;
                    i7 = (int) f2;
                    i8++;
                }
                int i9 = i4 * 5;
                int i10 = i9 / 8;
                int i11 = i9 - (i10 * 8);
                int i12 = 8 - i11;
                int i13 = 5 - i12;
                byte min = (byte) ((this.waveformBytes[i10] >> i11) & ((2 << (Math.min(i3, i12) - 1)) - 1));
                if (i13 > 0) {
                    int i14 = i10 + 1;
                    byte[] bArr2 = this.waveformBytes;
                    if (i14 < bArr2.length) {
                        min = (byte) (((byte) (min << i13)) | (bArr2[i14] & ((2 << (i13 - 1)) - 1)));
                    }
                }
                int i15 = 0;
                while (i15 < i8) {
                    if (i6 >= i2) {
                        return fArr;
                    }
                    fArr[i6] = Math.max(0.0f, ((float) (min * 7)) / 31.0f);
                    i15++;
                    i6++;
                }
                i5 = i7;
            }
            i4++;
            i3 = 5;
        }
        return fArr;
    }

    public void draw(Canvas canvas, View view) {
        int i;
        float f;
        float[] fArr;
        Canvas canvas2 = canvas;
        if (this.waveformBytes != null && (i = this.width) != 0) {
            float dpf2 = ((float) i) / AndroidUtilities.dpf2(3.0f);
            if (dpf2 > 0.1f) {
                float f2 = this.clearProgress;
                if (f2 != 1.0f) {
                    float f3 = f2 + 0.10666667f;
                    this.clearProgress = f3;
                    if (f3 > 1.0f) {
                        this.clearProgress = 1.0f;
                    } else {
                        view.invalidate();
                    }
                }
                float f4 = this.appearFloat.set(1.0f);
                Path path2 = this.path;
                if (path2 == null) {
                    this.path = new Path();
                } else {
                    path2.reset();
                }
                Path path3 = this.alphaPath;
                if (path3 == null) {
                    this.alphaPath = new Path();
                } else {
                    path3.reset();
                }
                float[] fArr2 = this.fromHeights;
                int i2 = 0;
                if (fArr2 == null || (fArr = this.toHeights) == null) {
                    if (this.heights != null) {
                        while (true) {
                            float f5 = (float) i2;
                            if (f5 >= dpf2 || i2 >= this.heights.length) {
                                break;
                            }
                            float clamp = MathUtils.clamp((f4 * dpf2) - f5, 0.0f, 1.0f);
                            addBar(this.path, AndroidUtilities.dpf2(3.0f) * f5, (AndroidUtilities.dpf2(this.heights[i2]) * clamp) - (AndroidUtilities.dpf2(1.0f) * (1.0f - clamp)));
                            i2++;
                        }
                    }
                    f = 0.0f;
                } else {
                    int i3 = this.width;
                    int i4 = this.fromWidth;
                    float f6 = ((float) (i3 - i4)) / ((float) (this.toWidth - i4));
                    int max = Math.max(fArr2.length, fArr.length);
                    int min = Math.min(this.fromHeights.length, this.toHeights.length);
                    float[] fArr3 = this.fromHeights;
                    int length = fArr3.length;
                    float[] fArr4 = this.toHeights;
                    float[] fArr5 = length < fArr4.length ? fArr3 : fArr4;
                    float[] fArr6 = fArr3.length < fArr4.length ? fArr4 : fArr3;
                    if (fArr3.length >= fArr4.length) {
                        f6 = 1.0f - f6;
                    }
                    int i5 = -1;
                    int i6 = 0;
                    f = 0.0f;
                    while (i6 < max) {
                        float f7 = (float) i6;
                        int i7 = max;
                        int clamp2 = MathUtils.clamp((int) Math.floor((double) ((f7 / ((float) max)) * ((float) min))), 0, min - 1);
                        if (i5 < clamp2) {
                            addBar(this.path, AndroidUtilities.lerp((float) clamp2, f7, f6) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(AndroidUtilities.lerp(fArr5[clamp2], fArr6[i6], f6)));
                            i5 = clamp2;
                        } else {
                            addBar(this.alphaPath, AndroidUtilities.lerp((float) clamp2, f7, f6) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(AndroidUtilities.lerp(fArr5[clamp2], fArr6[i6], f6)));
                            f = f6;
                        }
                        i6++;
                        max = i7;
                    }
                }
                if (f > 0.0f) {
                    canvas.save();
                    canvas2.clipPath(this.alphaPath);
                    drawFill(canvas2, f);
                    canvas.restore();
                }
                canvas.save();
                canvas2.clipPath(this.path);
                drawFill(canvas2, 1.0f);
                canvas.restore();
            }
        }
    }

    private void drawFill(Canvas canvas, float f) {
        float dpf2 = AndroidUtilities.dpf2(2.0f);
        MessageObject messageObject2 = this.messageObject;
        boolean z = messageObject2 != null && messageObject2.isContentUnread() && !this.messageObject.isOut() && this.progress <= 0.0f;
        this.isUnread = z;
        paintInner.setColor(z ? this.outerColor : this.selected ? this.selectedColor : this.innerColor);
        paintOuter.setColor(this.outerColor);
        this.loadingFloat.setParent(this.parentView);
        float f2 = this.loadingFloat.set((!this.loading || MediaController.getInstance().isPlayingMessage(this.messageObject)) ? 0.0f : 1.0f);
        Paint paint = paintInner;
        paint.setColor(ColorUtils.blendARGB(paint.getColor(), this.innerColor, f2));
        Paint paint2 = paintOuter;
        float f3 = 1.0f - f2;
        paint2.setAlpha((int) (((float) paint2.getAlpha()) * f3 * f));
        Paint paint3 = paintInner;
        paint3.setAlpha((int) (((float) paint3.getAlpha()) * f));
        canvas.drawRect(0.0f, 0.0f, ((float) this.width) + dpf2, (float) this.height, paintInner);
        if (f2 < 1.0f) {
            canvas.drawRect(0.0f, 0.0f, this.progress * (((float) this.width) + dpf2) * f3, (float) this.height, paintOuter);
        }
        if (f2 > 0.0f) {
            if (this.loadingPaint == null || Math.abs(this.loadingPaintWidth - ((float) this.width)) > ((float) AndroidUtilities.dp(8.0f)) || this.loadingPaintColor1 != this.innerColor || this.loadingPaintColor2 != this.outerColor) {
                if (this.loadingPaint == null) {
                    this.loadingPaint = new Paint(1);
                }
                this.loadingPaintColor1 = this.innerColor;
                this.loadingPaintColor2 = this.outerColor;
                Paint paint4 = this.loadingPaint;
                float f4 = (float) this.width;
                this.loadingPaintWidth = f4;
                int i = this.loadingPaintColor1;
                paint4.setShader(new LinearGradient(0.0f, 0.0f, f4, 0.0f, new int[]{i, this.loadingPaintColor2, i}, new float[]{0.0f, 0.2f, 0.4f}, Shader.TileMode.CLAMP));
            }
            this.loadingPaint.setAlpha((int) (f2 * 255.0f * f));
            canvas.save();
            float pow = ((((float) Math.pow((double) (((float) (SystemClock.elapsedRealtime() - this.loadingStart)) / 270.0f), 0.75d)) % 1.6f) - 0.6f) * this.loadingPaintWidth;
            canvas.translate(pow, 0.0f);
            canvas.drawRect(-pow, 0.0f, ((float) (this.width + 5)) - pow, (float) this.height, this.loadingPaint);
            canvas.restore();
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    private void addBar(Path path2, float f, float f2) {
        float dpf2 = AndroidUtilities.dpf2(2.0f);
        int dp = (this.height - AndroidUtilities.dp(14.0f)) / 2;
        float f3 = f2 * this.waveScaling;
        RectF rectF = AndroidUtilities.rectTmp;
        float f4 = dpf2 / 2.0f;
        rectF.set((AndroidUtilities.dpf2(1.0f) + f) - f4, ((float) (AndroidUtilities.dp(7.0f) + dp)) + ((-f3) - f4), f + AndroidUtilities.dpf2(1.0f) + f4, ((float) (dp + AndroidUtilities.dp(7.0f))) + f3 + f4);
        path2.addRoundRect(rectF, dpf2, dpf2, Path.Direction.CW);
    }

    public void setWaveScaling(float f) {
        this.waveScaling = f;
    }

    public void setLoading(boolean z) {
        if (!this.loading && z && this.loadingFloat.get() <= 0.0f) {
            this.loadingStart = SystemClock.elapsedRealtime();
        }
        this.loading = z;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }
}
