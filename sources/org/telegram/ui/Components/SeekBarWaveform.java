package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.SeekBar;

public class SeekBarWaveform {
    private static Paint paintInner;
    private static Paint paintOuter;
    private int clearFromX;
    private float clearProgress = 1.0f;
    private SeekBar.SeekBarDelegate delegate;
    private int height;
    private int innerColor;
    private boolean isUnread;
    private MessageObject messageObject;
    private int outerColor;
    private View parentView;
    private boolean pressed = false;
    private boolean selected;
    private int selectedColor;
    private boolean startDraging = false;
    private float startX;
    private int thumbDX = 0;
    private int thumbX = 0;
    private float waveScaling = 1.0f;
    private byte[] waveformBytes;
    private int width;

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint(1);
            paintOuter = new Paint(1);
            paintInner.setStyle(Paint.Style.STROKE);
            paintOuter.setStyle(Paint.Style.STROKE);
            paintInner.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
            paintOuter.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
            paintInner.setStrokeCap(Paint.Cap.ROUND);
            paintOuter.setStrokeCap(Paint.Cap.ROUND);
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
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setMessageObject(MessageObject object) {
        this.messageObject = object;
    }

    public void setParentView(View view) {
        this.parentView = view;
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

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setProgress(float progress, boolean animated) {
        int currentThumbX = this.isUnread ? this.width : this.thumbX;
        if (animated && currentThumbX != 0 && progress == 0.0f) {
            this.clearFromX = currentThumbX;
            this.clearProgress = 0.0f;
        } else if (!animated) {
            this.clearProgress = 1.0f;
        }
        int ceil = (int) Math.ceil((double) (((float) this.width) * progress));
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
        this.width = w;
        this.height = h;
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0222  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r36, android.view.View r37) {
        /*
            r35 = this;
            r6 = r35
            r7 = r36
            byte[] r0 = r6.waveformBytes
            if (r0 == 0) goto L_0x025f
            int r0 = r6.width
            if (r0 != 0) goto L_0x000e
            goto L_0x025f
        L_0x000e:
            r8 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 / r1
            float r9 = (float) r0
            r0 = 1036831949(0x3dcccccd, float:0.1)
            int r0 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x001e
            return
        L_0x001e:
            byte[] r0 = r6.waveformBytes
            int r0 = r0.length
            int r0 = r0 * 8
            r10 = 5
            int r11 = r0 / 5
            float r0 = (float) r11
            float r12 = r0 / r9
            r0 = 0
            r1 = 0
            org.telegram.messenger.MessageObject r2 = r6.messageObject
            r13 = 1
            if (r2 == 0) goto L_0x0044
            boolean r2 = r2.isOutOwner()
            if (r2 != 0) goto L_0x0044
            org.telegram.messenger.MessageObject r2 = r6.messageObject
            boolean r2 = r2.isContentUnread()
            if (r2 == 0) goto L_0x0044
            int r2 = r6.thumbX
            if (r2 != 0) goto L_0x0044
            r2 = 1
            goto L_0x0045
        L_0x0044:
            r2 = 0
        L_0x0045:
            r6.isUnread = r2
            android.graphics.Paint r3 = paintInner
            if (r2 == 0) goto L_0x004e
            int r2 = r6.outerColor
            goto L_0x0057
        L_0x004e:
            boolean r2 = r6.selected
            if (r2 == 0) goto L_0x0055
            int r2 = r6.selectedColor
            goto L_0x0057
        L_0x0055:
            int r2 = r6.innerColor
        L_0x0057:
            r3.setColor(r2)
            android.graphics.Paint r2 = paintOuter
            int r3 = r6.outerColor
            r2.setColor(r3)
            int r2 = r6.height
            r14 = 1096810496(0x41600000, float:14.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r2 = r2 - r3
            r15 = 2
            int r5 = r2 / 2
            r2 = 0
            float r3 = r6.clearProgress
            r4 = 1065353216(0x3var_, float:1.0)
            int r16 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r16 == 0) goto L_0x0087
            r16 = 1037726734(0x3dda740e, float:0.10666667)
            float r3 = r3 + r16
            r6.clearProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x0084
            r6.clearProgress = r4
            goto L_0x0087
        L_0x0084:
            r37.invalidate()
        L_0x0087:
            r3 = 0
        L_0x0088:
            if (r3 >= r11) goto L_0x025e
            if (r3 == r1) goto L_0x009a
            r31 = r3
            r33 = r5
            r18 = 1096810496(0x41600000, float:14.0)
            r23 = 1077936128(0x40400000, float:3.0)
            r25 = 1
            r32 = 1065353216(0x3var_, float:1.0)
            goto L_0x024f
        L_0x009a:
            r16 = 0
            r17 = r1
            r34 = r16
            r16 = r0
            r0 = r34
        L_0x00a4:
            r14 = r17
            if (r14 != r1) goto L_0x00b6
            float r4 = r16 + r12
            int r1 = (int) r4
            int r0 = r0 + 1
            r16 = r4
            r17 = r14
            r4 = 1065353216(0x3var_, float:1.0)
            r14 = 1096810496(0x41600000, float:14.0)
            goto L_0x00a4
        L_0x00b6:
            int r19 = r3 * 5
            int r20 = r19 / 8
            int r4 = r20 * 8
            int r21 = r19 - r4
            int r4 = 8 - r21
            int r22 = 5 - r4
            byte[] r8 = r6.waveformBytes
            byte r8 = r8[r20]
            int r8 = r8 >> r21
            int r24 = java.lang.Math.min(r10, r4)
            int r24 = r24 + -1
            int r24 = r15 << r24
            int r24 = r24 + -1
            r8 = r8 & r24
            byte r8 = (byte) r8
            if (r22 <= 0) goto L_0x00f3
            int r10 = r20 + 1
            byte[] r13 = r6.waveformBytes
            int r15 = r13.length
            if (r10 >= r15) goto L_0x00f2
            int r10 = r8 << r22
            byte r8 = (byte) r10
            int r10 = r20 + 1
            byte r10 = r13[r10]
            int r13 = r22 + -1
            r15 = 2
            int r13 = r15 << r13
            r25 = 1
            int r13 = r13 + -1
            r10 = r10 & r13
            r10 = r10 | r8
            byte r8 = (byte) r10
            goto L_0x00f5
        L_0x00f2:
            r15 = 2
        L_0x00f3:
            r25 = 1
        L_0x00f5:
            r10 = 0
            r13 = r10
            r10 = r2
        L_0x00f8:
            if (r13 >= r0) goto L_0x023a
            float r2 = (float) r10
            r23 = 1077936128(0x40400000, float:3.0)
            float r26 = org.telegram.messenger.AndroidUtilities.dpf2(r23)
            float r26 = r26 * r2
            r2 = 0
            int r15 = r8 * 7
            float r15 = (float) r15
            r27 = 1106771968(0x41var_, float:31.0)
            float r15 = r15 / r27
            float r2 = java.lang.Math.max(r2, r15)
            float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            int r2 = r6.thumbX
            float r2 = (float) r2
            r27 = 1073741824(0x40000000, float:2.0)
            int r2 = (r26 > r2 ? 1 : (r26 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0151
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r27)
            float r2 = (float) r2
            float r2 = r26 + r2
            r28 = r0
            int r0 = r6.thumbX
            float r0 = (float) r0
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x0146
            android.graphics.Paint r29 = paintOuter
            r0 = r35
            r30 = r1
            r1 = r36
            r2 = r26
            r31 = r3
            r3 = r5
            r17 = r4
            r32 = 1065353216(0x3var_, float:1.0)
            r4 = r15
            r33 = r5
            r5 = r29
            r0.drawLine(r1, r2, r3, r4, r5)
            goto L_0x019f
        L_0x0146:
            r30 = r1
            r31 = r3
            r17 = r4
            r33 = r5
            r32 = 1065353216(0x3var_, float:1.0)
            goto L_0x015d
        L_0x0151:
            r28 = r0
            r30 = r1
            r31 = r3
            r17 = r4
            r33 = r5
            r32 = 1065353216(0x3var_, float:1.0)
        L_0x015d:
            android.graphics.Paint r5 = paintInner
            r0 = r35
            r1 = r36
            r2 = r26
            r3 = r33
            r4 = r15
            r0.drawLine(r1, r2, r3, r4, r5)
            int r0 = r6.thumbX
            float r0 = (float) r0
            int r0 = (r26 > r0 ? 1 : (r26 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x019f
            r36.save()
            float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r32)
            float r0 = r26 - r0
            r5 = r33
            float r1 = (float) r5
            int r2 = r6.thumbX
            float r2 = (float) r2
            r3 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r5 + r4
            float r3 = (float) r3
            r7.clipRect(r0, r1, r2, r3)
            android.graphics.Paint r29 = paintOuter
            r0 = r35
            r1 = r36
            r2 = r26
            r3 = r5
            r4 = r15
            r5 = r29
            r0.drawLine(r1, r2, r3, r4, r5)
            r36.restore()
        L_0x019f:
            float r0 = r6.clearProgress
            int r0 = (r0 > r32 ? 1 : (r0 == r32 ? 0 : -1))
            if (r0 == 0) goto L_0x0222
            android.graphics.Paint r0 = paintOuter
            int r5 = r0.getAlpha()
            android.graphics.Paint r0 = paintOuter
            float r1 = (float) r5
            float r2 = r6.clearProgress
            float r4 = r32 - r2
            float r1 = r1 * r4
            int r1 = (int) r1
            r0.setAlpha(r1)
            int r0 = r6.clearFromX
            float r0 = (float) r0
            int r0 = (r26 > r0 ? 1 : (r26 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01e3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r27)
            float r0 = (float) r0
            float r0 = r26 + r0
            int r1 = r6.clearFromX
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x01e3
            android.graphics.Paint r27 = paintOuter
            r0 = r35
            r1 = r36
            r2 = r26
            r3 = r33
            r4 = r15
            r29 = r8
            r8 = r5
            r5 = r27
            r0.drawLine(r1, r2, r3, r4, r5)
            r18 = 1096810496(0x41600000, float:14.0)
            goto L_0x021c
        L_0x01e3:
            r29 = r8
            r8 = r5
            int r0 = r6.clearFromX
            float r0 = (float) r0
            int r0 = (r26 > r0 ? 1 : (r26 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x021a
            r36.save()
            float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r32)
            float r0 = r26 - r0
            r5 = r33
            float r1 = (float) r5
            int r2 = r6.clearFromX
            float r2 = (float) r2
            r18 = 1096810496(0x41600000, float:14.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r3 + r5
            float r3 = (float) r3
            r7.clipRect(r0, r1, r2, r3)
            android.graphics.Paint r27 = paintOuter
            r0 = r35
            r1 = r36
            r2 = r26
            r3 = r5
            r4 = r15
            r5 = r27
            r0.drawLine(r1, r2, r3, r4, r5)
            r36.restore()
            goto L_0x021c
        L_0x021a:
            r18 = 1096810496(0x41600000, float:14.0)
        L_0x021c:
            android.graphics.Paint r0 = paintOuter
            r0.setAlpha(r8)
            goto L_0x0226
        L_0x0222:
            r29 = r8
            r18 = 1096810496(0x41600000, float:14.0)
        L_0x0226:
            int r10 = r10 + 1
            int r13 = r13 + 1
            r4 = r17
            r0 = r28
            r8 = r29
            r1 = r30
            r3 = r31
            r5 = r33
            r15 = 2
            goto L_0x00f8
        L_0x023a:
            r28 = r0
            r30 = r1
            r31 = r3
            r17 = r4
            r33 = r5
            r29 = r8
            r18 = 1096810496(0x41600000, float:14.0)
            r23 = 1077936128(0x40400000, float:3.0)
            r32 = 1065353216(0x3var_, float:1.0)
            r2 = r10
            r0 = r16
        L_0x024f:
            int r3 = r31 + 1
            r5 = r33
            r4 = 1065353216(0x3var_, float:1.0)
            r8 = 1077936128(0x40400000, float:3.0)
            r10 = 5
            r13 = 1
            r14 = 1096810496(0x41600000, float:14.0)
            r15 = 2
            goto L_0x0088
        L_0x025e:
            return
        L_0x025f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBarWaveform.draw(android.graphics.Canvas, android.view.View):void");
    }

    private void drawLine(Canvas canvas, float x, int y, float h, Paint paint) {
        float h2 = h * this.waveScaling;
        if (h2 == 0.0f) {
            canvas.drawPoint(AndroidUtilities.dpf2(1.0f) + x, (float) (AndroidUtilities.dp(7.0f) + y), paint);
            return;
        }
        canvas.drawLine(x + AndroidUtilities.dpf2(1.0f), ((float) (AndroidUtilities.dp(7.0f) + y)) - h2, x + AndroidUtilities.dpf2(1.0f), ((float) (AndroidUtilities.dp(7.0f) + y)) + h2, paint);
    }

    public void setWaveScaling(float waveScaling2) {
        this.waveScaling = waveScaling2;
    }
}
