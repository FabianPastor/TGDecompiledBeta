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
    private SeekBar.SeekBarDelegate delegate;
    private int height;
    private int innerColor;
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
        int ceil = (int) Math.ceil((double) (((float) this.width) * f));
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

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public void draw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        if (this.waveformBytes != null && (i = this.width) != 0) {
            float dp = (float) (i / AndroidUtilities.dp(3.0f));
            if (dp > 0.1f) {
                int length = (this.waveformBytes.length * 8) / 5;
                float f = ((float) length) / dp;
                Paint paint = paintInner;
                MessageObject messageObject2 = this.messageObject;
                paint.setColor((messageObject2 == null || messageObject2.isOutOwner() || !this.messageObject.isContentUnread() || this.thumbX != 0) ? this.selected ? this.selectedColor : this.innerColor : this.outerColor);
                paintOuter.setColor(this.outerColor);
                int dp2 = (this.height - AndroidUtilities.dp(14.0f)) / 2;
                int i7 = 0;
                float f2 = 0.0f;
                int i8 = 0;
                int i9 = 0;
                while (i9 < length) {
                    if (i9 != i7) {
                        i2 = i9;
                    } else {
                        int i10 = i7;
                        float f3 = f2;
                        int i11 = 0;
                        while (i7 == i10) {
                            float f4 = f3 + f;
                            i10 = (int) f4;
                            i11++;
                            f3 = f4;
                        }
                        int i12 = i9 * 5;
                        int i13 = i12 / 8;
                        int i14 = i12 - (i13 * 8);
                        int i15 = 8 - i14;
                        int i16 = 5 - i15;
                        byte min = (byte) ((this.waveformBytes[i13] >> i14) & ((2 << (Math.min(5, i15) - 1)) - 1));
                        if (i16 > 0) {
                            int i17 = i13 + 1;
                            byte[] bArr = this.waveformBytes;
                            if (i17 < bArr.length) {
                                min = (byte) (((byte) (min << i16)) | (bArr[i17] & ((2 << (i16 - 1)) - 1)));
                            }
                        }
                        byte b = min;
                        int i18 = i8;
                        int i19 = 0;
                        while (i19 < i11) {
                            float dpf2 = ((float) i18) * AndroidUtilities.dpf2(3.0f);
                            float dpvar_ = AndroidUtilities.dpf2(Math.max(0.0f, ((float) (b * 7)) / 31.0f));
                            if (dpf2 >= ((float) this.thumbX) || dpf2 + ((float) AndroidUtilities.dp(2.0f)) >= ((float) this.thumbX)) {
                                i6 = i19;
                                i5 = i11;
                                i4 = i10;
                                i3 = i9;
                                drawLine(canvas, dpf2, dp2, dpvar_, paintInner);
                                if (dpf2 < ((float) this.thumbX)) {
                                    canvas.save();
                                    canvas.clipRect(dpf2 - AndroidUtilities.dpf2(1.0f), (float) dp2, (float) this.thumbX, (float) (AndroidUtilities.dp(14.0f) + dp2));
                                    drawLine(canvas, dpf2, dp2, dpvar_, paintOuter);
                                    canvas.restore();
                                    i18++;
                                    i19 = i6 + 1;
                                    i11 = i5;
                                    i10 = i4;
                                    i9 = i3;
                                }
                            } else {
                                i6 = i19;
                                i5 = i11;
                                i4 = i10;
                                i3 = i9;
                                drawLine(canvas, dpf2, dp2, dpvar_, paintOuter);
                            }
                            i18++;
                            i19 = i6 + 1;
                            i11 = i5;
                            i10 = i4;
                            i9 = i3;
                        }
                        i2 = i9;
                        i8 = i18;
                        f2 = f3;
                        i7 = i10;
                    }
                    i9 = i2 + 1;
                }
            }
        }
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
