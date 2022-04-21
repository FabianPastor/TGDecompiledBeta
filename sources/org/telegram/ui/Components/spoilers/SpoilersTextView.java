package org.telegram.ui.Components.spoilers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Spanned;
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

    /* renamed from: lambda$new$2$org-telegram-ui-Components-spoilers-SpoilersTextView  reason: not valid java name */
    public /* synthetic */ void m4543x69fa7571(SpoilerEffect eff, float x, float y) {
        if (!this.isSpoilersRevealed) {
            eff.setOnRippleEndCallback(new SpoilersTextView$$ExternalSyntheticLambda1(this));
            float rad = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
            for (SpoilerEffect ef : this.spoilers) {
                ef.startRipple(x, y, rad);
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-spoilers-SpoilersTextView  reason: not valid java name */
    public /* synthetic */ void m4542x4fdef6d2() {
        post(new SpoilersTextView$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-spoilers-SpoilersTextView  reason: not valid java name */
    public /* synthetic */ void m4541x35CLASSNAME() {
        this.isSpoilersRevealed = true;
        invalidateSpoilers();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.clickDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        this.isSpoilersRevealed = false;
        super.setText(text, type);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        invalidateSpoilers();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateSpoilers();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        canvas.save();
        this.path.rewind();
        for (SpoilerEffect eff : this.spoilers) {
            Rect bounds = eff.getBounds();
            this.path.addRect((float) (bounds.left + pl), (float) (bounds.top + pt), (float) (bounds.right + pl), (float) (bounds.bottom + pt), Path.Direction.CW);
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
            boolean useAlphaLayer = this.spoilers.get(0).getRippleProgress() != -1.0f;
            if (useAlphaLayer) {
                canvas.saveLayer(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (Paint) null, 31);
            } else {
                canvas.save();
            }
            canvas.translate((float) getPaddingLeft(), (float) (getPaddingTop() + AndroidUtilities.dp(2.0f)));
            for (SpoilerEffect eff2 : this.spoilers) {
                eff2.setColor(getPaint().getColor());
                eff2.draw(canvas);
            }
            if (useAlphaLayer) {
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
            if (getLayout() != null && (getText() instanceof Spanned)) {
                SpoilerEffect.addSpoilers(this, this.spoilersPool, this.spoilers);
            }
            invalidate();
        }
    }
}
