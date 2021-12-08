package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import org.telegram.messenger.AndroidUtilities;

public class ChartPickerDelegate {
    private static final int CAPTURE_LEFT = 1;
    private static final int CAPTURE_MIDDLE = 4;
    private static final int CAPTURE_NONE = 0;
    private static final int CAPTURE_RIGHT = 2;
    CapturesData[] capturedStates = {null, null};
    public boolean disabled;
    public Rect leftPickerArea = new Rect();
    public Rect middlePickerArea = new Rect();
    public float minDistance = 0.1f;
    ValueAnimator moveToAnimator;
    public float moveToX;
    public float moveToY;
    public float pickerEnd = 1.0f;
    public float pickerStart = 0.7f;
    public float pickerWidth;
    public Rect rightPickerArea = new Rect();
    public long startTapTime;
    public boolean tryMoveTo;
    Listener view;

    interface Listener {
        void invalidate();

        void onPickerDataChanged();

        void onPickerJumpTo(float f, float f2, boolean z);
    }

    public ChartPickerDelegate(Listener view2) {
        this.view = view2;
    }

    public CapturesData getMiddleCaptured() {
        CapturesData[] capturesDataArr = this.capturedStates;
        if (capturesDataArr[0] != null && capturesDataArr[0].state == 4) {
            return this.capturedStates[0];
        }
        CapturesData[] capturesDataArr2 = this.capturedStates;
        if (capturesDataArr2[1] == null || capturesDataArr2[1].state != 4) {
            return null;
        }
        return this.capturedStates[1];
    }

    public CapturesData getLeftCaptured() {
        CapturesData[] capturesDataArr = this.capturedStates;
        if (capturesDataArr[0] != null && capturesDataArr[0].state == 1) {
            return this.capturedStates[0];
        }
        CapturesData[] capturesDataArr2 = this.capturedStates;
        if (capturesDataArr2[1] == null || capturesDataArr2[1].state != 1) {
            return null;
        }
        return this.capturedStates[1];
    }

    public CapturesData getRightCaptured() {
        CapturesData[] capturesDataArr = this.capturedStates;
        if (capturesDataArr[0] != null && capturesDataArr[0].state == 2) {
            return this.capturedStates[0];
        }
        CapturesData[] capturesDataArr2 = this.capturedStates;
        if (capturesDataArr2[1] == null || capturesDataArr2[1].state != 2) {
            return null;
        }
        return this.capturedStates[1];
    }

    class CapturesData {
        ValueAnimator a;
        public float aValue = 0.0f;
        public int capturedX;
        public float end;
        ValueAnimator jumpToAnimator;
        public int lastMovingX;
        public float start;
        public final int state;

        public CapturesData(int state2) {
            this.state = state2;
        }

        public void captured() {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.a = ofFloat;
            ofFloat.setDuration(600);
            this.a.setInterpolator(BaseChartView.INTERPOLATOR);
            this.a.addUpdateListener(new ChartPickerDelegate$CapturesData$$ExternalSyntheticLambda0(this));
            this.a.start();
        }

        /* renamed from: lambda$captured$0$org-telegram-ui-Charts-ChartPickerDelegate$CapturesData  reason: not valid java name */
        public /* synthetic */ void m1656x947CLASSNAMEcf(ValueAnimator animation) {
            this.aValue = ((Float) animation.getAnimatedValue()).floatValue();
            ChartPickerDelegate.this.view.invalidate();
        }

        public void uncapture() {
            ValueAnimator valueAnimator = this.a;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimator2 = this.jumpToAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
        }
    }

    public boolean capture(int x, int y, int pointerIndex) {
        if (this.disabled) {
            return false;
        }
        if (pointerIndex == 0) {
            if (this.leftPickerArea.contains(x, y)) {
                CapturesData[] capturesDataArr = this.capturedStates;
                if (capturesDataArr[0] != null) {
                    capturesDataArr[1] = capturesDataArr[0];
                }
                capturesDataArr[0] = new CapturesData(1);
                this.capturedStates[0].start = this.pickerStart;
                this.capturedStates[0].capturedX = x;
                this.capturedStates[0].lastMovingX = x;
                this.capturedStates[0].captured();
                ValueAnimator valueAnimator = this.moveToAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                return true;
            } else if (this.rightPickerArea.contains(x, y)) {
                CapturesData[] capturesDataArr2 = this.capturedStates;
                if (capturesDataArr2[0] != null) {
                    capturesDataArr2[1] = capturesDataArr2[0];
                }
                capturesDataArr2[0] = new CapturesData(2);
                this.capturedStates[0].end = this.pickerEnd;
                this.capturedStates[0].capturedX = x;
                this.capturedStates[0].lastMovingX = x;
                this.capturedStates[0].captured();
                ValueAnimator valueAnimator2 = this.moveToAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                return true;
            } else if (this.middlePickerArea.contains(x, y)) {
                this.capturedStates[0] = new CapturesData(4);
                this.capturedStates[0].end = this.pickerEnd;
                this.capturedStates[0].start = this.pickerStart;
                this.capturedStates[0].capturedX = x;
                this.capturedStates[0].lastMovingX = x;
                this.capturedStates[0].captured();
                ValueAnimator valueAnimator3 = this.moveToAnimator;
                if (valueAnimator3 != null) {
                    valueAnimator3.cancel();
                }
                return true;
            } else if (y < this.leftPickerArea.bottom && y > this.leftPickerArea.top) {
                this.tryMoveTo = true;
                this.moveToX = (float) x;
                this.moveToY = (float) y;
                this.startTapTime = System.currentTimeMillis();
                ValueAnimator valueAnimator4 = this.moveToAnimator;
                if (valueAnimator4 != null) {
                    if (valueAnimator4.isRunning()) {
                        this.view.onPickerJumpTo(this.pickerStart, this.pickerEnd, true);
                    }
                    this.moveToAnimator.cancel();
                }
                return true;
            }
        } else if (pointerIndex == 1) {
            CapturesData[] capturesDataArr3 = this.capturedStates;
            if (capturesDataArr3[0] == null || capturesDataArr3[0].state == 4) {
                return false;
            }
            if (this.leftPickerArea.contains(x, y) && this.capturedStates[0].state != 1) {
                this.capturedStates[1] = new CapturesData(1);
                this.capturedStates[1].start = this.pickerStart;
                this.capturedStates[1].capturedX = x;
                this.capturedStates[1].lastMovingX = x;
                this.capturedStates[1].captured();
                ValueAnimator valueAnimator5 = this.moveToAnimator;
                if (valueAnimator5 != null) {
                    valueAnimator5.cancel();
                }
                return true;
            } else if (!this.rightPickerArea.contains(x, y) || this.capturedStates[0].state == 2) {
                return false;
            } else {
                this.capturedStates[1] = new CapturesData(2);
                this.capturedStates[1].end = this.pickerEnd;
                this.capturedStates[1].capturedX = x;
                this.capturedStates[1].lastMovingX = x;
                this.capturedStates[1].captured();
                ValueAnimator valueAnimator6 = this.moveToAnimator;
                if (valueAnimator6 != null) {
                    valueAnimator6.cancel();
                }
                return true;
            }
        }
        return false;
    }

    public boolean captured() {
        return this.capturedStates[0] != null || this.tryMoveTo;
    }

    public boolean move(int x, int y, int pointer) {
        CapturesData d;
        if (this.tryMoveTo || (d = this.capturedStates[pointer]) == null) {
            return false;
        }
        int capturedState = d.state;
        float capturedStart = d.start;
        float capturedEnd = d.end;
        int capturedX = d.capturedX;
        d.lastMovingX = x;
        boolean notifyPicker = false;
        if (capturedState == 1) {
            float f = capturedStart - (((float) (capturedX - x)) / this.pickerWidth);
            this.pickerStart = f;
            if (f < 0.0f) {
                this.pickerStart = 0.0f;
            }
            float f2 = this.pickerEnd;
            float f3 = this.minDistance;
            if (f2 - this.pickerStart < f3) {
                this.pickerStart = f2 - f3;
            }
            notifyPicker = true;
        }
        if (capturedState == 2) {
            float f4 = capturedEnd - (((float) (capturedX - x)) / this.pickerWidth);
            this.pickerEnd = f4;
            if (f4 > 1.0f) {
                this.pickerEnd = 1.0f;
            }
            float f5 = this.pickerEnd;
            float f6 = this.pickerStart;
            float f7 = this.minDistance;
            if (f5 - f6 < f7) {
                this.pickerEnd = f6 + f7;
            }
            notifyPicker = true;
        }
        if (capturedState == 4) {
            float f8 = this.pickerWidth;
            float f9 = capturedStart - (((float) (capturedX - x)) / f8);
            this.pickerStart = f9;
            this.pickerEnd = capturedEnd - (((float) (capturedX - x)) / f8);
            if (f9 < 0.0f) {
                this.pickerStart = 0.0f;
                this.pickerEnd = capturedEnd - capturedStart;
            }
            if (this.pickerEnd > 1.0f) {
                this.pickerEnd = 1.0f;
                this.pickerStart = 1.0f - (capturedEnd - capturedStart);
            }
            notifyPicker = true;
        }
        if (notifyPicker) {
            this.view.onPickerDataChanged();
        }
        return true;
    }

    public boolean uncapture(MotionEvent event, int pointerIndex) {
        float moveToLeft;
        float moveToRight;
        if (pointerIndex != 0) {
            CapturesData[] capturesDataArr = this.capturedStates;
            if (capturesDataArr[1] != null) {
                capturesDataArr[1].uncapture();
            }
            this.capturedStates[1] = null;
        } else if (this.tryMoveTo) {
            this.tryMoveTo = false;
            float dx = this.moveToX - event.getX();
            float dy = this.moveToY - event.getY();
            if (event.getAction() != 1 || System.currentTimeMillis() - this.startTapTime >= 300 || Math.sqrt((double) ((dx * dx) + (dy * dy))) >= ((double) AndroidUtilities.dp(10.0f))) {
                return true;
            }
            float moveToX2 = (this.moveToX - BaseChartView.HORIZONTAL_PADDING) / this.pickerWidth;
            float w = this.pickerEnd - this.pickerStart;
            float moveToLeft2 = moveToX2 - (w / 2.0f);
            float moveToRight2 = (w / 2.0f) + moveToX2;
            if (moveToLeft2 < 0.0f) {
                moveToRight = w;
                moveToLeft = 0.0f;
            } else if (moveToRight2 > 1.0f) {
                moveToRight = 1.0f;
                moveToLeft = 1.0f - w;
            } else {
                moveToRight = moveToRight2;
                moveToLeft = moveToLeft2;
            }
            float moveFromLeft = this.pickerStart;
            float moveFromRight = this.pickerEnd;
            this.moveToAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            float finalMoveToLeft = moveToLeft;
            float finalMoveToRight = moveToRight;
            this.view.onPickerJumpTo(finalMoveToLeft, finalMoveToRight, true);
            ChartPickerDelegate$$ExternalSyntheticLambda0 chartPickerDelegate$$ExternalSyntheticLambda0 = r0;
            float f = dx;
            ValueAnimator valueAnimator = this.moveToAnimator;
            float f2 = finalMoveToLeft;
            ChartPickerDelegate$$ExternalSyntheticLambda0 chartPickerDelegate$$ExternalSyntheticLambda02 = new ChartPickerDelegate$$ExternalSyntheticLambda0(this, moveFromLeft, finalMoveToLeft, moveFromRight, finalMoveToRight);
            valueAnimator.addUpdateListener(chartPickerDelegate$$ExternalSyntheticLambda0);
            this.moveToAnimator.setInterpolator(BaseChartView.INTERPOLATOR);
            this.moveToAnimator.start();
            return true;
        } else {
            CapturesData[] capturesDataArr2 = this.capturedStates;
            if (capturesDataArr2[0] != null) {
                capturesDataArr2[0].uncapture();
            }
            CapturesData[] capturesDataArr3 = this.capturedStates;
            capturesDataArr3[0] = null;
            if (capturesDataArr3[1] != null) {
                capturesDataArr3[0] = capturesDataArr3[1];
                capturesDataArr3[1] = null;
            }
        }
        return false;
    }

    /* renamed from: lambda$uncapture$0$org-telegram-ui-Charts-ChartPickerDelegate  reason: not valid java name */
    public /* synthetic */ void m1655lambda$uncapture$0$orgtelegramuiChartsChartPickerDelegate(float moveFromLeft, float finalMoveToLeft, float moveFromRight, float finalMoveToRight, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.pickerStart = ((finalMoveToLeft - moveFromLeft) * v) + moveFromLeft;
        this.pickerEnd = ((finalMoveToRight - moveFromRight) * v) + moveFromRight;
        this.view.onPickerJumpTo(finalMoveToLeft, finalMoveToRight, false);
    }

    public void uncapture() {
        CapturesData[] capturesDataArr = this.capturedStates;
        if (capturesDataArr[0] != null) {
            capturesDataArr[0].uncapture();
        }
        CapturesData[] capturesDataArr2 = this.capturedStates;
        if (capturesDataArr2[1] != null) {
            capturesDataArr2[1].uncapture();
        }
        CapturesData[] capturesDataArr3 = this.capturedStates;
        capturesDataArr3[0] = null;
        capturesDataArr3[1] = null;
    }
}
