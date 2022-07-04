package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
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
    private AnimatedFloat appearFloat = new AnimatedFloat(125, 450, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);
    private float clearProgress = 1.0f;
    private SeekBar.SeekBarDelegate delegate;
    private float[] fromHeights;
    private int fromWidth;
    private int height;
    private float[] heights;
    private int innerColor;
    private boolean isUnread;
    private boolean loading;
    private AnimatedFloat loadingFloat = new AnimatedFloat(150, (TimeInterpolator) CubicBezierInterpolator.DEFAULT);
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

    public void setColors(int inner, int outer, int selected2) {
        this.innerColor = inner;
        this.outerColor = outer;
        this.selectedColor = selected2;
    }

    public void setWaveform(byte[] waveform) {
        this.waveformBytes = waveform;
        this.heights = calculateHeights((int) (((float) this.width) / AndroidUtilities.dpf2(3.0f)));
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setMessageObject(MessageObject object) {
        MessageObject messageObject2;
        if (!(this.animatedValues == null || (messageObject2 = this.messageObject) == null || object == null || messageObject2.getId() == object.getId())) {
            this.animatedValues.clear();
        }
        this.messageObject = object;
    }

    public void setParentView(View view) {
        this.parentView = view;
        this.loadingFloat.setParent(view);
        this.appearFloat.setParent(view);
    }

    public boolean isStartDraging() {
        return this.startDraging;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBar.SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            if (0.0f <= x && x <= ((float) this.width) && y >= 0.0f && y <= ((float) this.height)) {
                this.startX = x;
                this.pressed = true;
                this.thumbDX = (int) (x - ((float) this.thumbX));
                this.startDraging = false;
                return true;
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) this.width));
                }
                this.pressed = false;
                return true;
            }
        } else if (action == 2 && this.pressed) {
            if (this.startDraging) {
                int i = (int) (x - ((float) this.thumbDX));
                this.thumbX = i;
                if (i < 0) {
                    this.thumbX = 0;
                } else {
                    int i2 = this.width;
                    if (i > i2) {
                        this.thumbX = i2;
                    }
                }
                this.progress = ((float) this.thumbX) / ((float) this.width);
            }
            float f = this.startX;
            if (f != -1.0f && Math.abs(x - f) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
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

    public void setProgress(float progress2) {
        setProgress(progress2, false);
    }

    public void setProgress(float progress2, boolean animated) {
        boolean z = this.isUnread;
        this.progress = z ? 1.0f : progress2;
        int currentThumbX = z ? this.width : this.thumbX;
        if (animated && currentThumbX != 0 && progress2 == 0.0f) {
            this.clearProgress = 0.0f;
        } else if (!animated) {
            this.clearProgress = 1.0f;
        }
        int ceil = (int) Math.ceil((double) (((float) this.width) * progress2));
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width;
        if (ceil > i) {
            this.thumbX = i;
        }
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSize(int w, int h) {
        setSize(w, h, w, w);
    }

    public void setSize(int w, int h, int fromW, int toW) {
        this.width = w;
        this.height = h;
        float[] fArr = this.heights;
        if (fArr == null || fArr.length != ((int) (((float) w) / AndroidUtilities.dpf2(3.0f)))) {
            this.heights = calculateHeights((int) (((float) this.width) / AndroidUtilities.dpf2(3.0f)));
        }
        if (fromW != toW && (this.fromWidth != fromW || this.toWidth != toW)) {
            this.fromWidth = fromW;
            this.toWidth = toW;
            this.fromHeights = calculateHeights((int) (((float) fromW) / AndroidUtilities.dpf2(3.0f)));
            this.toHeights = calculateHeights((int) (((float) this.toWidth) / AndroidUtilities.dpf2(3.0f)));
        } else if (fromW == toW) {
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

    private float[] calculateHeights(int count) {
        int samplesCount;
        SeekBarWaveform seekBarWaveform = this;
        int i = count;
        byte[] bArr = seekBarWaveform.waveformBytes;
        if (bArr == null || i <= 0) {
            return null;
        }
        float[] heights2 = new float[i];
        int samplesCount2 = (bArr.length * 8) / 5;
        float samplesPerBar = ((float) samplesCount2) / ((float) i);
        float barCounter = 0.0f;
        int nextBarNum = 0;
        int barNum = 0;
        int a = 0;
        while (a < samplesCount2) {
            if (a != nextBarNum) {
                samplesCount = samplesCount2;
            } else {
                int drawBarCount = 0;
                int lastBarNum = nextBarNum;
                while (lastBarNum == nextBarNum) {
                    barCounter += samplesPerBar;
                    nextBarNum = (int) barCounter;
                    drawBarCount++;
                }
                int bitPointer = a * 5;
                int byteNum = bitPointer / 8;
                int byteBitOffset = bitPointer - (byteNum * 8);
                int currentByteCount = 8 - byteBitOffset;
                int nextByteRest = 5 - currentByteCount;
                byte value = (byte) ((seekBarWaveform.waveformBytes[byteNum] >> byteBitOffset) & ((2 << (Math.min(5, currentByteCount) - 1)) - 1));
                if (nextByteRest > 0) {
                    int i2 = byteNum + 1;
                    samplesCount = samplesCount2;
                    byte[] bArr2 = seekBarWaveform.waveformBytes;
                    if (i2 < bArr2.length) {
                        value = (byte) ((bArr2[byteNum + 1] & ((2 << (nextByteRest - 1)) - 1)) | ((byte) (value << nextByteRest)));
                    }
                } else {
                    samplesCount = samplesCount2;
                }
                int b = 0;
                while (b < drawBarCount) {
                    if (barNum >= heights2.length) {
                        return heights2;
                    }
                    heights2[barNum] = Math.max(0.0f, ((float) (value * 7)) / 31.0f);
                    b++;
                    barNum++;
                }
                continue;
            }
            a++;
            seekBarWaveform = this;
            int i3 = count;
            samplesCount2 = samplesCount;
        }
        return heights2;
    }

    public void draw(Canvas canvas, View parentView2) {
        int i;
        float[] fArr;
        Canvas canvas2 = canvas;
        if (this.waveformBytes != null && (i = this.width) != 0) {
            float totalBarsCount = ((float) i) / AndroidUtilities.dpf2(3.0f);
            if (totalBarsCount > 0.1f) {
                float f = this.clearProgress;
                if (f != 1.0f) {
                    float f2 = f + 0.10666667f;
                    this.clearProgress = f2;
                    if (f2 > 1.0f) {
                        this.clearProgress = 1.0f;
                    } else {
                        parentView2.invalidate();
                    }
                }
                float appearProgress = this.appearFloat.set(1.0f);
                Path path2 = this.path;
                if (path2 == null) {
                    this.path = new Path();
                } else {
                    path2.reset();
                }
                float alpha = 0.0f;
                Path path3 = this.alphaPath;
                if (path3 == null) {
                    this.alphaPath = new Path();
                } else {
                    path3.reset();
                }
                float[] fArr2 = this.fromHeights;
                if (fArr2 == null || (fArr = this.toHeights) == null) {
                    float appearProgress2 = appearProgress;
                    if (this.heights != null) {
                        int barNum = 0;
                        while (((float) barNum) < totalBarsCount && barNum < this.heights.length) {
                            addBar(this.path, ((float) barNum) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(this.heights[barNum]) * MathUtils.clamp((appearProgress2 * totalBarsCount) - ((float) barNum), 0.0f, 1.0f));
                            barNum++;
                        }
                    }
                } else {
                    int i2 = this.width;
                    int i3 = this.fromWidth;
                    float t = ((float) (i2 - i3)) / ((float) (this.toWidth - i3));
                    int maxlen = Math.max(fArr2.length, fArr.length);
                    int minlen = Math.min(this.fromHeights.length, this.toHeights.length);
                    float[] fArr3 = this.fromHeights;
                    int length = fArr3.length;
                    float[] fArr4 = this.toHeights;
                    float[] minarr = length < fArr4.length ? fArr3 : fArr4;
                    float[] maxarr = fArr3.length < fArr4.length ? fArr4 : fArr3;
                    float T = fArr3.length < fArr4.length ? t : 1.0f - t;
                    int k = -1;
                    int barNum2 = 0;
                    while (barNum2 < maxlen) {
                        float appearProgress3 = appearProgress;
                        int l = MathUtils.clamp((int) Math.floor((double) ((((float) barNum2) / ((float) maxlen)) * ((float) minlen))), 0, minlen - 1);
                        if (k < l) {
                            addBar(this.path, AndroidUtilities.lerp((float) l, (float) barNum2, T) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(AndroidUtilities.lerp(minarr[l], maxarr[barNum2], T)));
                            k = l;
                        } else {
                            addBar(this.alphaPath, AndroidUtilities.lerp((float) l, (float) barNum2, T) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(AndroidUtilities.lerp(minarr[l], maxarr[barNum2], T)));
                            alpha = T;
                        }
                        barNum2++;
                        appearProgress = appearProgress3;
                    }
                }
                if (alpha > 0.0f) {
                    canvas.save();
                    canvas2.clipPath(this.alphaPath);
                    drawFill(canvas2, alpha);
                    canvas.restore();
                }
                canvas.save();
                canvas2.clipPath(this.path);
                drawFill(canvas2, 1.0f);
                canvas.restore();
            }
        }
    }

    private void drawFill(Canvas canvas, float alpha) {
        float strokeWidth = AndroidUtilities.dpf2(2.0f);
        MessageObject messageObject2 = this.messageObject;
        boolean z = messageObject2 != null && messageObject2.isContentUnread() && !this.messageObject.isOut() && this.progress <= 0.0f;
        this.isUnread = z;
        paintInner.setColor(z ? this.outerColor : this.selected ? this.selectedColor : this.innerColor);
        paintOuter.setColor(this.outerColor);
        this.loadingFloat.setParent(this.parentView);
        float loadingT = this.loadingFloat.set((!this.loading || MediaController.getInstance().isPlayingMessage(this.messageObject)) ? 0.0f : 1.0f);
        Paint paint = paintInner;
        paint.setColor(ColorUtils.blendARGB(paint.getColor(), this.innerColor, loadingT));
        Paint paint2 = paintOuter;
        paint2.setAlpha((int) (((float) paint2.getAlpha()) * (1.0f - loadingT) * alpha));
        Paint paint3 = paintInner;
        paint3.setAlpha((int) (((float) paint3.getAlpha()) * alpha));
        canvas.drawRect(0.0f, 0.0f, ((float) this.width) + strokeWidth, (float) this.height, paintInner);
        if (loadingT < 1.0f) {
            canvas.drawRect(0.0f, 0.0f, this.progress * (((float) this.width) + strokeWidth) * (1.0f - loadingT), (float) this.height, paintOuter);
        }
        if (loadingT > 0.0f) {
            if (this.loadingPaint == null || Math.abs(this.loadingPaintWidth - ((float) this.width)) > ((float) AndroidUtilities.dp(8.0f)) || this.loadingPaintColor1 != this.innerColor || this.loadingPaintColor2 != this.outerColor) {
                if (this.loadingPaint == null) {
                    this.loadingPaint = new Paint(1);
                }
                this.loadingPaintColor1 = this.innerColor;
                this.loadingPaintColor2 = this.outerColor;
                Paint paint4 = this.loadingPaint;
                float f = (float) this.width;
                this.loadingPaintWidth = f;
                int i = this.loadingPaintColor1;
                LinearGradient linearGradient = r8;
                LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, f, 0.0f, new int[]{i, this.loadingPaintColor2, i}, new float[]{0.0f, 0.2f, 0.4f}, Shader.TileMode.CLAMP);
                paint4.setShader(linearGradient);
            }
            this.loadingPaint.setAlpha((int) (255.0f * loadingT * alpha));
            canvas.save();
            float dx = ((((float) Math.pow((double) (((float) (SystemClock.elapsedRealtime() - this.loadingStart)) / 270.0f), 0.75d)) % 1.6f) - 0.6f) * this.loadingPaintWidth;
            canvas.translate(dx, 0.0f);
            canvas.drawRect(-dx, 0.0f, ((float) (this.width + 5)) - dx, (float) this.height, this.loadingPaint);
            canvas.restore();
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
                return;
            }
            return;
        }
        Canvas canvas2 = canvas;
    }

    private void addBar(Path path2, float x, float h) {
        float strokeWidth = AndroidUtilities.dpf2(2.0f);
        int y = (this.height - AndroidUtilities.dp(14.0f)) / 2;
        float h2 = h * this.waveScaling;
        AndroidUtilities.rectTmp.set((AndroidUtilities.dpf2(1.0f) + x) - (strokeWidth / 2.0f), ((float) (AndroidUtilities.dp(7.0f) + y)) + ((-h2) - (strokeWidth / 2.0f)), AndroidUtilities.dpf2(1.0f) + x + (strokeWidth / 2.0f), ((float) (AndroidUtilities.dp(7.0f) + y)) + (strokeWidth / 2.0f) + h2);
        path2.addRoundRect(AndroidUtilities.rectTmp, strokeWidth, strokeWidth, Path.Direction.CW);
    }

    public void setWaveScaling(float waveScaling2) {
        this.waveScaling = waveScaling2;
    }

    public void setLoading(boolean loading2) {
        if (!this.loading && loading2 && this.loadingFloat.get() <= 0.0f) {
            this.loadingStart = SystemClock.elapsedRealtime();
        }
        this.loading = loading2;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }
}
