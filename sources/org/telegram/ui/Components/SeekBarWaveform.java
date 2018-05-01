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
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    if (i == 2 && this.pressed != 0) {
                        if (this.startDraging != 0) {
                            this.thumbX = (int) (f - ((float) this.thumbDX));
                            if (this.thumbX < 0) {
                                this.thumbX = 0;
                            } else if (this.thumbX > this.width) {
                                this.thumbX = this.width;
                            }
                        }
                        if (this.startX != -NUM && Math.abs(f - this.startX) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
                            if (!(this.parentView == 0 || this.parentView.getParent() == 0)) {
                                this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            this.startDraging = true;
                            this.startX = -1.0f;
                        }
                        return true;
                    }
                }
            }
            if (this.pressed != null) {
                if (i == 1 && this.delegate != 0) {
                    this.delegate.onSeekBarDrag(((float) this.thumbX) / ((float) this.width));
                }
                this.pressed = false;
                return true;
            }
        } else if (0.0f <= f && f <= ((float) this.width) && f2 >= 0.0f && f2 <= ((float) this.height)) {
            this.startX = f;
            this.pressed = true;
            this.thumbDX = (int) (f - ((float) this.thumbX));
            this.startDraging = false;
            return true;
        }
        return false;
    }

    public void setProgress(float f) {
        this.thumbX = (int) Math.ceil((double) (((float) this.width) * f));
        if (this.thumbX < null) {
            this.thumbX = 0;
        } else if (this.thumbX > this.width) {
            this.thumbX = this.width;
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
            if (r0.width != 0) {
                float f = 3.0f;
                float dp = (float) (r0.width / AndroidUtilities.dp(3.0f));
                if (dp > 0.1f) {
                    int i = 5;
                    int length = (r0.waveformBytes.length * 8) / 5;
                    float f2 = ((float) length) / dp;
                    Paint paint = paintInner;
                    int i2 = (r0.messageObject == null || r0.messageObject.isOutOwner() || !r0.messageObject.isContentUnread()) ? r0.selected ? r0.selectedColor : r0.innerColor : r0.outerColor;
                    paint.setColor(i2);
                    paintOuter.setColor(r0.outerColor);
                    float f3 = 14.0f;
                    int i3 = 2;
                    int dp2 = (r0.height - AndroidUtilities.dp(14.0f)) / 2;
                    float f4 = 0.0f;
                    int i4 = 0;
                    int i5 = 0;
                    int i6 = 0;
                    while (i4 < length) {
                        int i7;
                        float f5;
                        if (i4 != i5) {
                            i7 = length;
                            f5 = f3;
                        } else {
                            float f6 = f4;
                            int i8 = 0;
                            int i9 = i5;
                            while (i5 == i9) {
                                f6 += f2;
                                i9 = (int) f6;
                                i8++;
                            }
                            i5 = i4 * 5;
                            int i10 = i5 / 8;
                            i5 -= i10 * 8;
                            int i11 = 8 - i5;
                            int i12 = 5 - i11;
                            i2 = (byte) ((r0.waveformBytes[i10] >> i5) & ((i3 << (Math.min(i, i11) - 1)) - 1));
                            if (i12 > 0) {
                                i2 = (byte) (((byte) (i2 << i12)) | (r0.waveformBytes[i10 + 1] & ((i3 << (i12 - 1)) - 1)));
                            }
                            i11 = 0;
                            while (i11 < i8) {
                                i5 = AndroidUtilities.dp(f) * i6;
                                if (i5 >= r0.thumbX || AndroidUtilities.dp(2.0f) + i5 >= r0.thumbX) {
                                    float f7 = (float) i5;
                                    float f8 = (((float) i2) * 14.0f) / 31.0f;
                                    i7 = length;
                                    canvas.drawRect(f7, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, f8)) + dp2), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (dp2 + AndroidUtilities.dp(14.0f)), paintInner);
                                    if (i5 < r0.thumbX) {
                                        canvas.drawRect(f7, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, f8)) + dp2), (float) r0.thumbX, (float) (AndroidUtilities.dp(14.0f) + dp2), paintOuter);
                                        i6++;
                                        i11++;
                                        length = i7;
                                        f = 3.0f;
                                    }
                                } else {
                                    canvas.drawRect((float) i5, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) i2) * 14.0f) / 31.0f)) + dp2), (float) (i5 + AndroidUtilities.dp(2.0f)), (float) (AndroidUtilities.dp(14.0f) + dp2), paintOuter);
                                    i7 = length;
                                }
                                i6++;
                                i11++;
                                length = i7;
                                f = 3.0f;
                            }
                            i7 = length;
                            f5 = 14.0f;
                            i5 = i9;
                            f4 = f6;
                        }
                        i4++;
                        f3 = f5;
                        length = i7;
                        f = 3.0f;
                        i = 5;
                        i3 = 2;
                    }
                }
            }
        }
    }
}
