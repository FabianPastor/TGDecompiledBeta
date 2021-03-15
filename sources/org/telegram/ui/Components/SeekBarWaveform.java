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
        this.messageObject = messageObject2;
    }

    public void setParentView(View view) {
        this.parentView = view;
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
        int i = this.isUnread ? this.width : this.thumbX;
        if (z && i != 0 && f == 0.0f) {
            this.clearFromX = i;
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
        this.width = i;
        this.height = i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01fa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r29, android.view.View r30) {
        /*
            r28 = this;
            r6 = r28
            r7 = r29
            byte[] r0 = r6.waveformBytes
            if (r0 == 0) goto L_0x0229
            int r0 = r6.width
            if (r0 != 0) goto L_0x000e
            goto L_0x0229
        L_0x000e:
            r8 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 / r1
            float r0 = (float) r0
            r1 = 1036831949(0x3dcccccd, float:0.1)
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x001e
            return
        L_0x001e:
            byte[] r1 = r6.waveformBytes
            int r1 = r1.length
            int r1 = r1 * 8
            r9 = 5
            int r10 = r1 / 5
            float r1 = (float) r10
            float r11 = r1 / r0
            org.telegram.messenger.MessageObject r0 = r6.messageObject
            r13 = 1
            if (r0 == 0) goto L_0x0042
            boolean r0 = r0.isOutOwner()
            if (r0 != 0) goto L_0x0042
            org.telegram.messenger.MessageObject r0 = r6.messageObject
            boolean r0 = r0.isContentUnread()
            if (r0 == 0) goto L_0x0042
            int r0 = r6.thumbX
            if (r0 != 0) goto L_0x0042
            r0 = 1
            goto L_0x0043
        L_0x0042:
            r0 = 0
        L_0x0043:
            r6.isUnread = r0
            android.graphics.Paint r1 = paintInner
            if (r0 == 0) goto L_0x004c
            int r0 = r6.outerColor
            goto L_0x0055
        L_0x004c:
            boolean r0 = r6.selected
            if (r0 == 0) goto L_0x0053
            int r0 = r6.selectedColor
            goto L_0x0055
        L_0x0053:
            int r0 = r6.innerColor
        L_0x0055:
            r1.setColor(r0)
            android.graphics.Paint r0 = paintOuter
            int r1 = r6.outerColor
            r0.setColor(r1)
            int r0 = r6.height
            r14 = 1096810496(0x41600000, float:14.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r1
            r15 = 2
            int r5 = r0 / 2
            float r0 = r6.clearProgress
            r4 = 1065353216(0x3var_, float:1.0)
            int r1 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x0083
            r1 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 + r1
            r6.clearProgress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0080
            r6.clearProgress = r4
            goto L_0x0083
        L_0x0080:
            r30.invalidate()
        L_0x0083:
            r0 = 0
            r1 = 0
            r2 = 0
            r16 = 0
        L_0x0088:
            if (r2 >= r10) goto L_0x0229
            if (r2 == r0) goto L_0x0097
            r24 = r2
            r8 = r5
            r19 = 1096810496(0x41600000, float:14.0)
            r26 = 0
            r27 = 1065353216(0x3var_, float:1.0)
            goto L_0x021d
        L_0x0097:
            r17 = r1
            r12 = 0
            r1 = r0
        L_0x009b:
            if (r0 != r1) goto L_0x00a8
            float r1 = r17 + r11
            int r4 = (int) r1
            int r12 = r12 + 1
            r17 = r1
            r1 = r4
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x009b
        L_0x00a8:
            int r0 = r2 * 5
            int r4 = r0 / 8
            int r19 = r4 * 8
            int r0 = r0 - r19
            int r14 = 8 - r0
            int r20 = 5 - r14
            byte[] r3 = r6.waveformBytes
            byte r3 = r3[r4]
            int r0 = r3 >> r0
            int r3 = java.lang.Math.min(r9, r14)
            int r3 = r3 - r13
            int r3 = r15 << r3
            int r3 = r3 - r13
            r0 = r0 & r3
            byte r0 = (byte) r0
            if (r20 <= 0) goto L_0x00da
            int r4 = r4 + 1
            byte[] r3 = r6.waveformBytes
            int r14 = r3.length
            if (r4 >= r14) goto L_0x00da
            int r0 = r0 << r20
            byte r0 = (byte) r0
            byte r3 = r3[r4]
            int r20 = r20 + -1
            int r4 = r15 << r20
            int r4 = r4 - r13
            r3 = r3 & r4
            r0 = r0 | r3
            byte r0 = (byte) r0
        L_0x00da:
            r14 = r0
            r4 = r16
            r3 = 0
        L_0x00de:
            if (r3 >= r12) goto L_0x020a
            float r0 = (float) r4
            float r16 = org.telegram.messenger.AndroidUtilities.dpf2(r8)
            float r16 = r16 * r0
            int r0 = r14 * 7
            float r0 = (float) r0
            r20 = 1106771968(0x41var_, float:31.0)
            float r0 = r0 / r20
            r8 = 0
            float r0 = java.lang.Math.max(r8, r0)
            float r21 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
            int r0 = r6.thumbX
            float r0 = (float) r0
            r22 = 1073741824(0x40000000, float:2.0)
            int r0 = (r16 > r0 ? 1 : (r16 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x012c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r0 = (float) r0
            float r0 = r16 + r0
            int r8 = r6.thumbX
            float r8 = (float) r8
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x012c
            android.graphics.Paint r8 = paintOuter
            r0 = r28
            r23 = r1
            r1 = r29
            r24 = r2
            r2 = r16
            r25 = r3
            r26 = 0
            r3 = r5
            r18 = r4
            r27 = 1065353216(0x3var_, float:1.0)
            r4 = r21
            r30 = r5
            r5 = r8
            r0.drawLine(r1, r2, r3, r4, r5)
            goto L_0x017d
        L_0x012c:
            r23 = r1
            r24 = r2
            r25 = r3
            r18 = r4
            r30 = r5
            r26 = 0
            r27 = 1065353216(0x3var_, float:1.0)
            android.graphics.Paint r5 = paintInner
            r0 = r28
            r1 = r29
            r2 = r16
            r3 = r30
            r4 = r21
            r0.drawLine(r1, r2, r3, r4, r5)
            int r0 = r6.thumbX
            float r0 = (float) r0
            int r0 = (r16 > r0 ? 1 : (r16 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x017d
            r29.save()
            float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r27)
            float r0 = r16 - r0
            r8 = r30
            float r1 = (float) r8
            int r2 = r6.thumbX
            float r2 = (float) r2
            r3 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = r8 + r4
            float r3 = (float) r5
            r7.clipRect(r0, r1, r2, r3)
            android.graphics.Paint r5 = paintOuter
            r0 = r28
            r1 = r29
            r2 = r16
            r3 = r8
            r4 = r21
            r0.drawLine(r1, r2, r3, r4, r5)
            r29.restore()
            goto L_0x017f
        L_0x017d:
            r8 = r30
        L_0x017f:
            float r0 = r6.clearProgress
            int r0 = (r0 > r27 ? 1 : (r0 == r27 ? 0 : -1))
            if (r0 == 0) goto L_0x01fa
            android.graphics.Paint r0 = paintOuter
            int r5 = r0.getAlpha()
            android.graphics.Paint r0 = paintOuter
            float r1 = (float) r5
            float r2 = r6.clearProgress
            float r4 = r27 - r2
            float r1 = r1 * r4
            int r1 = (int) r1
            r0.setAlpha(r1)
            int r0 = r6.clearFromX
            float r0 = (float) r0
            int r0 = (r16 > r0 ? 1 : (r16 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01bf
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r0 = (float) r0
            float r0 = r16 + r0
            int r1 = r6.clearFromX
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x01bf
            android.graphics.Paint r22 = paintOuter
            r0 = r28
            r1 = r29
            r2 = r16
            r3 = r8
            r4 = r21
            r9 = r5
            r5 = r22
            r0.drawLine(r1, r2, r3, r4, r5)
            goto L_0x01f2
        L_0x01bf:
            r9 = r5
            int r0 = r6.clearFromX
            float r0 = (float) r0
            int r0 = (r16 > r0 ? 1 : (r16 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01f2
            r29.save()
            float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r27)
            float r0 = r16 - r0
            float r1 = (float) r8
            int r2 = r6.clearFromX
            float r2 = (float) r2
            r19 = 1096810496(0x41600000, float:14.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r5 = r8 + r3
            float r3 = (float) r5
            r7.clipRect(r0, r1, r2, r3)
            android.graphics.Paint r5 = paintOuter
            r0 = r28
            r1 = r29
            r2 = r16
            r3 = r8
            r4 = r21
            r0.drawLine(r1, r2, r3, r4, r5)
            r29.restore()
            goto L_0x01f4
        L_0x01f2:
            r19 = 1096810496(0x41600000, float:14.0)
        L_0x01f4:
            android.graphics.Paint r0 = paintOuter
            r0.setAlpha(r9)
            goto L_0x01fc
        L_0x01fa:
            r19 = 1096810496(0x41600000, float:14.0)
        L_0x01fc:
            int r4 = r18 + 1
            int r3 = r25 + 1
            r5 = r8
            r1 = r23
            r2 = r24
            r8 = 1077936128(0x40400000, float:3.0)
            r9 = 5
            goto L_0x00de
        L_0x020a:
            r23 = r1
            r24 = r2
            r18 = r4
            r8 = r5
            r19 = 1096810496(0x41600000, float:14.0)
            r26 = 0
            r27 = 1065353216(0x3var_, float:1.0)
            r1 = r17
            r16 = r18
            r0 = r23
        L_0x021d:
            int r2 = r24 + 1
            r5 = r8
            r4 = 1065353216(0x3var_, float:1.0)
            r8 = 1077936128(0x40400000, float:3.0)
            r9 = 5
            r14 = 1096810496(0x41600000, float:14.0)
            goto L_0x0088
        L_0x0229:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBarWaveform.draw(android.graphics.Canvas, android.view.View):void");
    }

    private void drawLine(Canvas canvas, float f, int i, float f2, Paint paint) {
        float f3 = f2 * this.waveScaling;
        if (f3 == 0.0f) {
            canvas.drawPoint(f + AndroidUtilities.dpf2(1.0f), (float) (i + AndroidUtilities.dp(7.0f)), paint);
            return;
        }
        canvas.drawLine(f + AndroidUtilities.dpf2(1.0f), ((float) (AndroidUtilities.dp(7.0f) + i)) - f3, f + AndroidUtilities.dpf2(1.0f), ((float) (i + AndroidUtilities.dp(7.0f))) + f3, paint);
    }

    public void setWaveScaling(float f) {
        this.waveScaling = f;
    }
}
