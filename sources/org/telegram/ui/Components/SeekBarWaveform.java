package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;

public class SeekBarWaveform {
    private static Paint paintInner;
    private static Paint paintOuter;
    private SeekBarDelegate delegate;
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
    private byte[] waveformBytes;
    private int width;

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint();
            paintOuter = new Paint();
        }
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
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

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
    }

    public void setParentView(View view) {
        this.parentView = view;
    }

    public boolean isStartDraging() {
        return this.startDraging;
    }

    public boolean onTouch(int i, float f, float f2) {
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
                if (i == 1) {
                    SeekBarDelegate seekBarDelegate = this.delegate;
                    if (seekBarDelegate != null) {
                        seekBarDelegate.onSeekBarDrag(((float) this.thumbX) / ((float) this.width));
                    }
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            if (this.startDraging) {
                this.thumbX = (int) (f - ((float) this.thumbDX));
                i = this.thumbX;
                if (i < 0) {
                    this.thumbX = 0;
                } else {
                    int i2 = this.width;
                    if (i > i2) {
                        this.thumbX = i2;
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

    public void setProgress(float f) {
        this.thumbX = (int) Math.ceil((double) (((float) this.width) * f));
        int i = this.thumbX;
        if (i < 0) {
            this.thumbX = 0;
            return;
        }
        int i2 = this.width;
        if (i > i2) {
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

    public void draw(Canvas canvas) {
        if (this.waveformBytes != null) {
            int i = this.width;
            if (i != 0) {
                float f = 3.0f;
                float dp = (float) (i / AndroidUtilities.dp(3.0f));
                if (dp > 0.1f) {
                    int i2 = 5;
                    int length = (this.waveformBytes.length * 8) / 5;
                    float f2 = ((float) length) / dp;
                    Paint paint = paintInner;
                    MessageObject messageObject = this.messageObject;
                    int i3 = (messageObject == null || messageObject.isOutOwner() || !this.messageObject.isContentUnread()) ? this.selected ? this.selectedColor : this.innerColor : this.outerColor;
                    paint.setColor(i3);
                    paintOuter.setColor(this.outerColor);
                    int i4 = 2;
                    int dp2 = (this.height - AndroidUtilities.dp(14.0f)) / 2;
                    i = 0;
                    int i5 = 0;
                    float f3 = 0.0f;
                    int i6 = 0;
                    while (i < length) {
                        int i7;
                        if (i != i5) {
                            i7 = length;
                        } else {
                            float f4 = f3;
                            int i8 = 0;
                            int i9 = i5;
                            while (i5 == i9) {
                                f4 += f2;
                                i9 = (int) f4;
                                i8++;
                            }
                            i5 = i * 5;
                            int i10 = i5 / 8;
                            i5 -= i10 * 8;
                            int i11 = 8 - i5;
                            int i12 = 5 - i11;
                            i3 = (byte) ((this.waveformBytes[i10] >> i5) & ((i4 << (Math.min(i2, i11) - 1)) - 1));
                            if (i12 > 0) {
                                i10++;
                                byte[] bArr = this.waveformBytes;
                                if (i10 < bArr.length) {
                                    i3 = (byte) (((byte) (i3 << i12)) | (bArr[i10] & ((i4 << (i12 - 1)) - 1)));
                                }
                            }
                            i11 = 0;
                            while (i11 < i8) {
                                i5 = AndroidUtilities.dp(f) * i6;
                                if (i5 >= this.thumbX || AndroidUtilities.dp(2.0f) + i5 >= this.thumbX) {
                                    float f5 = (float) i5;
                                    float f6 = (((float) i3) * 14.0f) / 31.0f;
                                    i7 = length;
                                    canvas.drawRect(f5, (float) (dp2 + AndroidUtilities.dp(14.0f - Math.max(1.0f, f6))), (float) (i5 + AndroidUtilities.dp(2.0f)), (float) (dp2 + AndroidUtilities.dp(14.0f)), paintInner);
                                    if (i5 < this.thumbX) {
                                        canvas.drawRect(f5, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, f6)) + dp2), (float) this.thumbX, (float) (AndroidUtilities.dp(14.0f) + dp2), paintOuter);
                                        i6++;
                                        i11++;
                                        length = i7;
                                        f = 3.0f;
                                    }
                                } else {
                                    canvas.drawRect((float) i5, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) i3) * 14.0f) / 31.0f)) + dp2), (float) (i5 + AndroidUtilities.dp(2.0f)), (float) (AndroidUtilities.dp(14.0f) + dp2), paintOuter);
                                    i7 = length;
                                }
                                i6++;
                                i11++;
                                length = i7;
                                f = 3.0f;
                            }
                            i7 = length;
                            i5 = i9;
                            f3 = f4;
                        }
                        i++;
                        length = i7;
                        f = 3.0f;
                        i2 = 5;
                        i4 = 2;
                    }
                }
            }
        }
    }
}
