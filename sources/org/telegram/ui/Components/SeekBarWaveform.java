package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
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
                            addBar(this.path, AndroidUtilities.dpf2(3.0f) * f5, AndroidUtilities.dpf2(this.heights[i2]) * MathUtils.clamp((f4 * dpf2) - f5, 0.0f, 1.0f));
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
                        int clamp = MathUtils.clamp((int) Math.floor((double) ((f7 / ((float) max)) * ((float) min))), 0, min - 1);
                        if (i5 < clamp) {
                            addBar(this.path, AndroidUtilities.lerp((float) clamp, f7, f6) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(AndroidUtilities.lerp(fArr5[clamp], fArr6[i6], f6)));
                            i5 = clamp;
                        } else {
                            addBar(this.alphaPath, AndroidUtilities.lerp((float) clamp, f7, f6) * AndroidUtilities.dpf2(3.0f), AndroidUtilities.dpf2(AndroidUtilities.lerp(fArr5[clamp], fArr6[i6], f6)));
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

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00e8, code lost:
        if (r0.loadingPaintColor2 == r0.outerColor) goto L_0x0125;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void drawFill(android.graphics.Canvas r17, float r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = 1073741824(0x40000000, float:2.0)
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
            org.telegram.messenger.MessageObject r2 = r0.messageObject
            r3 = 0
            r4 = 1
            r5 = 0
            if (r2 == 0) goto L_0x0025
            boolean r2 = r2.isContentUnread()
            if (r2 == 0) goto L_0x0025
            org.telegram.messenger.MessageObject r2 = r0.messageObject
            boolean r2 = r2.isOut()
            if (r2 != 0) goto L_0x0025
            float r2 = r0.progress
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 > 0) goto L_0x0025
            r2 = 1
            goto L_0x0026
        L_0x0025:
            r2 = 0
        L_0x0026:
            r0.isUnread = r2
            android.graphics.Paint r6 = paintInner
            if (r2 == 0) goto L_0x002f
            int r2 = r0.outerColor
            goto L_0x0038
        L_0x002f:
            boolean r2 = r0.selected
            if (r2 == 0) goto L_0x0036
            int r2 = r0.selectedColor
            goto L_0x0038
        L_0x0036:
            int r2 = r0.innerColor
        L_0x0038:
            r6.setColor(r2)
            android.graphics.Paint r2 = paintOuter
            int r6 = r0.outerColor
            r2.setColor(r6)
            org.telegram.ui.Components.AnimatedFloat r2 = r0.loadingFloat
            android.view.View r6 = r0.parentView
            r2.setParent(r6)
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r6 = r0.messageObject
            boolean r2 = r2.isPlayingMessage(r6)
            org.telegram.ui.Components.AnimatedFloat r6 = r0.loadingFloat
            boolean r7 = r0.loading
            r8 = 1065353216(0x3var_, float:1.0)
            if (r7 == 0) goto L_0x0060
            if (r2 != 0) goto L_0x0060
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0061
        L_0x0060:
            r2 = 0
        L_0x0061:
            float r2 = r6.set(r2)
            android.graphics.Paint r6 = paintInner
            int r7 = r6.getColor()
            int r9 = r0.innerColor
            int r7 = androidx.core.graphics.ColorUtils.blendARGB(r7, r9, r2)
            r6.setColor(r7)
            android.graphics.Paint r6 = paintOuter
            int r7 = r6.getAlpha()
            float r7 = (float) r7
            float r9 = r8 - r2
            float r7 = r7 * r9
            float r7 = r7 * r18
            int r7 = (int) r7
            r6.setAlpha(r7)
            android.graphics.Paint r6 = paintInner
            int r7 = r6.getAlpha()
            float r7 = (float) r7
            float r7 = r7 * r18
            int r7 = (int) r7
            r6.setAlpha(r7)
            r11 = 0
            r12 = 0
            int r6 = r0.width
            float r6 = (float) r6
            float r13 = r6 + r1
            int r6 = r0.height
            float r14 = (float) r6
            android.graphics.Paint r15 = paintInner
            r10 = r17
            r10.drawRect(r11, r12, r13, r14, r15)
            int r6 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r6 >= 0) goto L_0x00bd
            r11 = 0
            r12 = 0
            float r6 = r0.progress
            int r7 = r0.width
            float r7 = (float) r7
            float r7 = r7 + r1
            float r6 = r6 * r7
            float r13 = r6 * r9
            int r1 = r0.height
            float r14 = (float) r1
            android.graphics.Paint r15 = paintOuter
            r10 = r17
            r10.drawRect(r11, r12, r13, r14, r15)
        L_0x00bd:
            int r1 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0175
            android.graphics.Paint r1 = r0.loadingPaint
            if (r1 == 0) goto L_0x00ea
            float r1 = r0.loadingPaintWidth
            int r6 = r0.width
            float r6 = (float) r6
            float r1 = r1 - r6
            float r1 = java.lang.Math.abs(r1)
            double r6 = (double) r1
            int r1 = r0.width
            double r8 = (double) r1
            r10 = 4602678819172646912(0x3feNUM, double:0.5)
            java.lang.Double.isNaN(r8)
            double r8 = r8 * r10
            int r1 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r1 > 0) goto L_0x00ea
            int r1 = r0.loadingPaintColor1
            int r6 = r0.innerColor
            if (r1 != r6) goto L_0x00ea
            int r1 = r0.loadingPaintColor2
            int r6 = r0.outerColor
            if (r1 == r6) goto L_0x0125
        L_0x00ea:
            android.graphics.Paint r1 = r0.loadingPaint
            if (r1 != 0) goto L_0x00f5
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r4)
            r0.loadingPaint = r1
        L_0x00f5:
            int r1 = r0.innerColor
            r0.loadingPaintColor1 = r1
            int r1 = r0.outerColor
            r0.loadingPaintColor2 = r1
            android.graphics.Paint r1 = r0.loadingPaint
            android.graphics.LinearGradient r14 = new android.graphics.LinearGradient
            r7 = 0
            r8 = 0
            int r6 = r0.width
            float r9 = (float) r6
            r0.loadingPaintWidth = r9
            r10 = 0
            r6 = 3
            int[] r11 = new int[r6]
            int r12 = r0.loadingPaintColor1
            r11[r3] = r12
            int r3 = r0.loadingPaintColor2
            r11[r4] = r3
            r3 = 2
            r11[r3] = r12
            float[] r12 = new float[r6]
            r12 = {0, NUM, NUM} // fill-array
            android.graphics.Shader$TileMode r13 = android.graphics.Shader.TileMode.CLAMP
            r6 = r14
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)
            r1.setShader(r14)
        L_0x0125:
            android.graphics.Paint r1 = r0.loadingPaint
            r3 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 * r3
            float r2 = r2 * r18
            int r2 = (int) r2
            r1.setAlpha(r2)
            r17.save()
            long r1 = android.os.SystemClock.elapsedRealtime()
            long r3 = r0.loadingStart
            long r1 = r1 - r3
            float r1 = (float) r1
            r2 = 1132920832(0x43870000, float:270.0)
            float r1 = r1 / r2
            double r1 = (double) r1
            r3 = 4604930618986332160(0x3feNUM, double:0.75)
            double r1 = java.lang.Math.pow(r1, r3)
            float r1 = (float) r1
            r2 = 1070386381(0x3fcccccd, float:1.6)
            float r1 = r1 % r2
            r2 = 1058642330(0x3var_a, float:0.6)
            float r1 = r1 - r2
            float r2 = r0.loadingPaintWidth
            float r1 = r1 * r2
            r2 = r17
            r2.translate(r1, r5)
            float r7 = -r1
            r8 = 0
            int r3 = r0.width
            int r3 = r3 + 5
            float r3 = (float) r3
            float r9 = r3 - r1
            int r1 = r0.height
            float r10 = (float) r1
            android.graphics.Paint r11 = r0.loadingPaint
            r6 = r17
            r6.drawRect(r7, r8, r9, r10, r11)
            r17.restore()
            android.view.View r1 = r0.parentView
            if (r1 == 0) goto L_0x0175
            r1.invalidate()
        L_0x0175:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBarWaveform.drawFill(android.graphics.Canvas, float):void");
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
