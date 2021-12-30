package org.telegram.ui.Components.spoilers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;

public class SpoilersTextView extends TextView {
    private SpoilersClickDetector clickDetector = new SpoilersClickDetector(this, this.spoilers, new SpoilersTextView$$ExternalSyntheticLambda2(this));
    private boolean isSpoilersRevealed;
    private Path path = new Path();
    private List<SpoilerEffect> spoilers = new ArrayList();
    private Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private Paint xRefPaint;

    public SpoilersTextView(Context context) {
        super(context);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(SpoilerEffect spoilerEffect, float f, float f2) {
        if (!this.isSpoilersRevealed) {
            spoilerEffect.setOnRippleEndCallback(new SpoilersTextView$$ExternalSyntheticLambda0(this));
            float sqrt = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
            for (SpoilerEffect startRipple : this.spoilers) {
                startRipple.startRipple(f, f2, sqrt);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        post(new SpoilersTextView$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.isSpoilersRevealed = true;
        invalidateSpoilers();
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.clickDetector.onTouchEvent(motionEvent)) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        this.isSpoilersRevealed = false;
        super.setText(charSequence, bufferType);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        invalidateSpoilers();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateSpoilers();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        canvas.save();
        this.path.rewind();
        for (SpoilerEffect bounds : this.spoilers) {
            Rect bounds2 = bounds.getBounds();
            this.path.addRect((float) (bounds2.left + paddingLeft), (float) (bounds2.top + paddingTop), (float) (bounds2.right + paddingLeft), (float) (bounds2.bottom + paddingTop), Path.Direction.CW);
        }
        canvas.clipPath(this.path, Region.Op.DIFFERENCE);
        super.onDraw(canvas);
        canvas.restore();
        canvas.save();
        canvas.clipPath(this.path);
        this.path.rewind();
        if (!this.spoilers.isEmpty()) {
            this.spoilers.get(0).getRipplePath(this.path);
        }
        canvas.clipPath(this.path);
        super.onDraw(canvas);
        canvas.restore();
        if (!this.spoilers.isEmpty()) {
            boolean z = this.spoilers.get(0).getRippleProgress() != -1.0f;
            if (z) {
                canvas.saveLayer(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (Paint) null, 31);
            } else {
                canvas.save();
            }
            canvas.translate((float) getPaddingLeft(), (float) (getPaddingTop() + AndroidUtilities.dp(2.0f)));
            for (SpoilerEffect next : this.spoilers) {
                next.setColor(getPaint().getColor());
                next.draw(canvas);
            }
            if (z) {
                this.path.rewind();
                this.spoilers.get(0).getRipplePath(this.path);
                if (this.xRefPaint == null) {
                    Paint paint = new Paint(1);
                    this.xRefPaint = paint;
                    paint.setColor(-16777216);
                    this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                }
                canvas.drawPath(this.path, this.xRefPaint);
            }
            canvas.restore();
        }
    }

    private void invalidateSpoilers() {
        List<SpoilerEffect> list = this.spoilers;
        if (list != null) {
            this.spoilersPool.addAll(list);
            this.spoilers.clear();
            if (this.isSpoilersRevealed) {
                invalidate();
                return;
            }
            if (getLayout() != null && (getText() instanceof Spannable)) {
                SpoilerEffect.addSpoilers(this, this.spoilersPool, this.spoilers);
            }
            invalidate();
        }
    }
}
