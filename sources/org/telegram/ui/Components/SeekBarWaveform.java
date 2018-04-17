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

    public void setColors(int inner, int outer, int selected) {
        this.innerColor = inner;
        this.outerColor = outer;
        this.selectedColor = selected;
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
        if (action != 0) {
            if (action != 1) {
                if (action != 3) {
                    if (action == 2 && this.pressed) {
                        if (this.startDraging) {
                            this.thumbX = (int) (x - ((float) this.thumbDX));
                            if (this.thumbX < 0) {
                                this.thumbX = 0;
                            } else if (this.thumbX > this.width) {
                                this.thumbX = this.width;
                            }
                        }
                        if (this.startX != -1.0f && Math.abs(x - this.startX) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
                            if (!(this.parentView == null || this.parentView.getParent() == null)) {
                                this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            this.startDraging = true;
                            this.startX = -1.0f;
                        }
                        return true;
                    }
                }
            }
            if (this.pressed) {
                if (action == 1 && this.delegate != null) {
                    this.delegate.onSeekBarDrag(((float) this.thumbX) / ((float) this.width));
                }
                this.pressed = false;
                return true;
            }
        } else if (0.0f <= x && x <= ((float) this.width) && y >= 0.0f && y <= ((float) this.height)) {
            this.startX = x;
            this.pressed = true;
            this.thumbDX = (int) (x - ((float) this.thumbX));
            this.startDraging = false;
            return true;
        }
        return false;
    }

    public void setProgress(float progress) {
        this.thumbX = (int) Math.ceil((double) (((float) this.width) * progress));
        if (this.thumbX < 0) {
            this.thumbX = 0;
        } else if (this.thumbX > this.width) {
            this.thumbX = this.width;
        }
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void draw(Canvas canvas) {
        if (this.waveformBytes != null) {
            if (r0.width != 0) {
                float f = 3.0f;
                float totalBarsCount = (float) (r0.width / AndroidUtilities.dp(3.0f));
                if (totalBarsCount > 0.1f) {
                    int samplesCount;
                    float samplesPerBar;
                    int i = 5;
                    int samplesCount2 = (r0.waveformBytes.length * 8) / 5;
                    float samplesPerBar2 = ((float) samplesCount2) / totalBarsCount;
                    int nextBarNum = 0;
                    Paint paint = paintInner;
                    int i2 = (r0.messageObject == null || r0.messageObject.isOutOwner() || !r0.messageObject.isContentUnread()) ? r0.selected ? r0.selectedColor : r0.innerColor : r0.outerColor;
                    paint.setColor(i2);
                    paintOuter.setColor(r0.outerColor);
                    int i3 = 2;
                    int y = (r0.height - AndroidUtilities.dp(14.0f)) / 2;
                    int barNum = 0;
                    float barCounter = 0.0f;
                    int a = 0;
                    while (a < samplesCount2) {
                        float totalBarsCount2;
                        if (a != nextBarNum) {
                            totalBarsCount2 = totalBarsCount;
                            samplesCount = samplesCount2;
                            samplesPerBar = samplesPerBar2;
                        } else {
                            int drawBarCount = 0;
                            float barCounter2 = barCounter;
                            int nextBarNum2 = nextBarNum;
                            while (nextBarNum == nextBarNum2) {
                                barCounter2 += samplesPerBar2;
                                nextBarNum2 = (int) barCounter2;
                                drawBarCount++;
                            }
                            int bitPointer = a * 5;
                            int byteNum = bitPointer / 8;
                            int byteBitOffset = bitPointer - (byteNum * 8);
                            int currentByteCount = 8 - byteBitOffset;
                            int nextByteRest = 5 - currentByteCount;
                            byte value = (byte) ((r0.waveformBytes[byteNum] >> byteBitOffset) & ((i3 << (Math.min(i, currentByteCount) - 1)) - 1));
                            if (nextByteRest > 0) {
                                value = (byte) ((r0.waveformBytes[byteNum + 1] & ((i3 << (nextByteRest - 1)) - 1)) | ((byte) (value << nextByteRest)));
                            }
                            i = 0;
                            while (i < drawBarCount) {
                                int x = barNum * AndroidUtilities.dp(f);
                                totalBarsCount2 = totalBarsCount;
                                if (x >= r0.thumbX || AndroidUtilities.dp(2.0f) + x >= r0.thumbX) {
                                    samplesCount = samplesCount2;
                                    samplesPerBar = samplesPerBar2;
                                    canvas.drawRect((float) x, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) value) * 14.0f) / 31.0f)) + y), (float) (AndroidUtilities.dp(2.0f) + x), (float) (y + AndroidUtilities.dp(14.0f)), paintInner);
                                    if (x < r0.thumbX) {
                                        canvas.drawRect((float) x, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) value) * 14.0f) / 31.0f)) + y), (float) r0.thumbX, (float) (y + AndroidUtilities.dp(14.0f)), paintOuter);
                                    }
                                } else {
                                    samplesCount = samplesCount2;
                                    samplesPerBar = samplesPerBar2;
                                    canvas.drawRect((float) x, (float) (AndroidUtilities.dp(14.0f - Math.max(1.0f, (((float) value) * 14.0f) / 31.0f)) + y), (float) (AndroidUtilities.dp(2.0f) + x), (float) (y + AndroidUtilities.dp(14.0f)), paintOuter);
                                }
                                barNum++;
                                i++;
                                totalBarsCount = totalBarsCount2;
                                samplesCount2 = samplesCount;
                                samplesPerBar2 = samplesPerBar;
                                f = 3.0f;
                            }
                            totalBarsCount2 = totalBarsCount;
                            samplesCount = samplesCount2;
                            samplesPerBar = samplesPerBar2;
                            nextBarNum = nextBarNum2;
                            barCounter = barCounter2;
                        }
                        a++;
                        totalBarsCount = totalBarsCount2;
                        samplesCount2 = samplesCount;
                        samplesPerBar2 = samplesPerBar;
                        f = 3.0f;
                        i = 5;
                        i3 = 2;
                    }
                    samplesCount = samplesCount2;
                    samplesPerBar = samplesPerBar2;
                }
            }
        }
    }
}
