package org.telegram.ui.Components;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class CanvasButton {
    private static final int[] pressedState = {16842910, 16842919};
    boolean buttonPressed;
    private Runnable delegate;
    Path drawingPath = new Path();
    ArrayList<RectF> drawingRects = new ArrayList<>();
    Paint paint;
    private final View parent;
    private boolean pathCreated;
    RippleDrawable selectorDrawable;
    int usingRectCount;

    public CanvasButton(View view) {
        Paint paint = new Paint(1);
        this.paint = paint;
        this.parent = view;
        paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(12.0f)));
        if (Build.VERSION.SDK_INT >= 21) {
            final Paint paint2 = new Paint(1);
            paint2.setFilterBitmap(true);
            paint2.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(12.0f)));
            paint2.setColor(-1);
            this.selectorDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor("listSelectorSDK21") & NUM}), null, new Drawable() { // from class: org.telegram.ui.Components.CanvasButton.1
                @Override // android.graphics.drawable.Drawable
                public int getOpacity() {
                    return -2;
                }

                @Override // android.graphics.drawable.Drawable
                public void setAlpha(int i) {
                }

                @Override // android.graphics.drawable.Drawable
                public void setColorFilter(ColorFilter colorFilter) {
                }

                @Override // android.graphics.drawable.Drawable
                public void draw(Canvas canvas) {
                    CanvasButton.this.drawInternal(canvas, paint2);
                }
            });
        }
    }

    public void draw(Canvas canvas) {
        drawInternal(canvas, this.paint);
        RippleDrawable rippleDrawable = this.selectorDrawable;
        if (rippleDrawable != null) {
            rippleDrawable.draw(canvas);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawInternal(Canvas canvas, Paint paint) {
        int i = this.usingRectCount;
        if (i <= 1) {
            if (i != 1) {
                return;
            }
            canvas.drawRoundRect(this.drawingRects.get(0), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), paint);
            return;
        }
        if (!this.pathCreated) {
            this.drawingPath.rewind();
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            for (int i6 = 0; i6 < this.usingRectCount; i6++) {
                if (i6 == 0 || this.drawingRects.get(i6).bottom > i2) {
                    i2 = (int) this.drawingRects.get(i6).bottom;
                }
                if (i6 == 0 || this.drawingRects.get(i6).right > i3) {
                    i3 = (int) this.drawingRects.get(i6).right;
                }
                if (i6 == 0 || this.drawingRects.get(i6).left < i4) {
                    i4 = (int) this.drawingRects.get(i6).left;
                }
                if (i6 == 0 || this.drawingRects.get(i6).top < i5) {
                    i5 = (int) this.drawingRects.get(i6).top;
                }
                this.drawingPath.addRect(this.drawingRects.get(i6), Path.Direction.CW);
                RippleDrawable rippleDrawable = this.selectorDrawable;
                if (rippleDrawable != null) {
                    rippleDrawable.setBounds(i4, i5, i3, i2);
                }
            }
            this.pathCreated = true;
        }
        canvas.drawPath(this.drawingPath, paint);
    }

    public boolean checkTouchEvent(MotionEvent motionEvent) {
        RippleDrawable rippleDrawable;
        Runnable runnable;
        RippleDrawable rippleDrawable2;
        RippleDrawable rippleDrawable3;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (contains(x, y)) {
                this.buttonPressed = true;
                if (Build.VERSION.SDK_INT >= 21 && (rippleDrawable3 = this.selectorDrawable) != null) {
                    rippleDrawable3.setHotspot(x, y);
                    this.selectorDrawable.setState(pressedState);
                }
                this.parent.invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.buttonPressed) {
                if (motionEvent.getAction() == 1 && (runnable = this.delegate) != null) {
                    runnable.run();
                }
                this.parent.playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21 && (rippleDrawable = this.selectorDrawable) != null) {
                    rippleDrawable.setState(StateSet.NOTHING);
                }
                this.buttonPressed = false;
                this.parent.invalidate();
            }
        } else if (motionEvent.getAction() == 2 && this.buttonPressed && Build.VERSION.SDK_INT >= 21 && (rippleDrawable2 = this.selectorDrawable) != null) {
            rippleDrawable2.setHotspot(x, y);
        }
        return this.buttonPressed;
    }

    private boolean contains(int i, int i2) {
        for (int i3 = 0; i3 < this.usingRectCount; i3++) {
            if (this.drawingRects.get(i3).contains(i, i2)) {
                return true;
            }
        }
        return false;
    }

    public void setColor(int i) {
        this.paint.setColor(i);
        RippleDrawable rippleDrawable = this.selectorDrawable;
        if (rippleDrawable == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        Theme.setSelectorDrawableColor(rippleDrawable, i, true);
    }

    public void setDelegate(Runnable runnable) {
        this.delegate = runnable;
    }

    public void rewind() {
        this.pathCreated = false;
        this.usingRectCount = 0;
    }

    public void addRect(RectF rectF) {
        int i = this.usingRectCount + 1;
        this.usingRectCount = i;
        if (i > this.drawingRects.size()) {
            this.drawingRects.add(new RectF());
        }
        this.drawingRects.get(this.usingRectCount - 1).set(rectF);
    }
}
