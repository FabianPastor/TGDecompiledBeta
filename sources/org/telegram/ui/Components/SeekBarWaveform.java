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
    private byte[] waveformBytes;
    private int width;

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint();
            paintOuter = new Paint();
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
                this.thumbX = (int) (f - ((float) this.thumbDX));
                int i2 = this.thumbX;
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
        int i;
        int i2;
        int i3;
        if (this.waveformBytes != null && (i = this.width) != 0) {
            float f = 3.0f;
            float dp = (float) (i / AndroidUtilities.dp(3.0f));
            if (dp > 0.1f) {
                int i4 = 5;
                int length = (this.waveformBytes.length * 8) / 5;
                float f2 = ((float) length) / dp;
                Paint paint = paintInner;
                MessageObject messageObject2 = this.messageObject;
                paint.setColor((messageObject2 == null || messageObject2.isOutOwner() || !this.messageObject.isContentUnread()) ? this.selected ? this.selectedColor : this.innerColor : this.outerColor);
                paintOuter.setColor(this.outerColor);
                int i5 = 2;
                int dp2 = (this.height - AndroidUtilities.dp(14.0f)) / 2;
                int i6 = 0;
                int i7 = 0;
                float f3 = 0.0f;
                int i8 = 0;
                while (i6 < length) {
                    if (i6 != i7) {
                        i2 = length;
                    } else {
                        float f4 = f3;
                        int i9 = 0;
                        int i10 = i7;
                        while (i7 == i10) {
                            f4 += f2;
                            i10 = (int) f4;
                            i9++;
                        }
                        int i11 = i6 * 5;
                        int i12 = i11 / 8;
                        int i13 = i11 - (i12 * 8);
                        int i14 = 8 - i13;
                        int i15 = 5 - i14;
                        byte min = (byte) ((this.waveformBytes[i12] >> i13) & ((i5 << (Math.min(i4, i14) - 1)) - 1));
                        if (i15 > 0) {
                            int i16 = i12 + 1;
                            byte[] bArr = this.waveformBytes;
                            if (i16 < bArr.length) {
                                min = (byte) (((byte) (min << i15)) | (bArr[i16] & ((i5 << (i15 - 1)) - 1)));
                            }
                        }
                        int i17 = 0;
                        while (i17 < i9) {
                            int dp3 = AndroidUtilities.dp(f) * i8;
                            if (dp3 >= this.thumbX || AndroidUtilities.dp(2.0f) + dp3 >= this.thumbX) {
                                float f5 = (float) dp3;
                                float f6 = (((float) min) * 14.0f) / 31.0f;
                                i3 = length;
                                canvas.drawRect(f5, (float) (dp2 + AndroidUtilities.dp(14.0f - Math.max(1.0f, f6))), (float) (dp3 + AndroidUtilities.dp(2.0f)), (float) (dp2 + AndroidUtilities.dp(14.0f)), paintInner);
                                if (dp3 < this.thumbX) {
                                    canvas.drawRect(f5, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, f6)) + dp2), (float) this.thumbX, (float) (AndroidUtilities.dp(14.0f) + dp2), paintOuter);
                                    i8++;
                                    i17++;
                                    length = i3;
                                    f = 3.0f;
                                }
                            } else {
                                canvas.drawRect((float) dp3, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) min) * 14.0f) / 31.0f)) + dp2), (float) (dp3 + AndroidUtilities.dp(2.0f)), (float) (AndroidUtilities.dp(14.0f) + dp2), paintOuter);
                                i3 = length;
                            }
                            i8++;
                            i17++;
                            length = i3;
                            f = 3.0f;
                        }
                        i2 = length;
                        i7 = i10;
                        f3 = f4;
                    }
                    i6++;
                    length = i2;
                    f = 3.0f;
                    i4 = 5;
                    i5 = 2;
                }
            }
        }
    }
}
