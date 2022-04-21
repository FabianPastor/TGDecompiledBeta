package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.spoilers.SpoilersClickDetector;

public class EditTextEffects extends EditText {
    private static final int SPOILER_TIMEOUT = 10000;
    private SpoilersClickDetector clickDetector = new SpoilersClickDetector(this, this.spoilers, new EditTextEffects$$ExternalSyntheticLambda5(this));
    private boolean isSpoilersRevealed;
    private float lastRippleX;
    private float lastRippleY;
    private Path path = new Path();
    private boolean postedSpoilerTimeout;
    private Rect rect = new Rect();
    private int selEnd;
    private int selStart;
    private boolean shouldRevealSpoilersByTouch = true;
    private Runnable spoilerTimeout = new EditTextEffects$$ExternalSyntheticLambda2(this);
    private List<SpoilerEffect> spoilers = new ArrayList();
    private Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private boolean suppressOnTextChanged;

    /* renamed from: lambda$new$2$org-telegram-ui-Components-EditTextEffects  reason: not valid java name */
    public /* synthetic */ void m3948lambda$new$2$orgtelegramuiComponentsEditTextEffects() {
        this.postedSpoilerTimeout = false;
        this.isSpoilersRevealed = false;
        invalidateSpoilers();
        if (!this.spoilers.isEmpty()) {
            this.spoilers.get(0).setOnRippleEndCallback(new EditTextEffects$$ExternalSyntheticLambda1(this));
            float rad = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
            for (SpoilerEffect eff : this.spoilers) {
                eff.startRipple(this.lastRippleX, this.lastRippleY, rad, true);
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-EditTextEffects  reason: not valid java name */
    public /* synthetic */ void m3946lambda$new$0$orgtelegramuiComponentsEditTextEffects() {
        setSpoilersRevealed(false, true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-EditTextEffects  reason: not valid java name */
    public /* synthetic */ void m3947lambda$new$1$orgtelegramuiComponentsEditTextEffects() {
        post(new EditTextEffects$$ExternalSyntheticLambda0(this));
    }

    public EditTextEffects(Context context) {
        super(context);
    }

    /* access modifiers changed from: private */
    public void onSpoilerClicked(SpoilerEffect eff, float x, float y) {
        if (!this.isSpoilersRevealed) {
            this.lastRippleX = x;
            this.lastRippleY = y;
            this.postedSpoilerTimeout = false;
            removeCallbacks(this.spoilerTimeout);
            setSpoilersRevealed(true, false);
            eff.setOnRippleEndCallback(new EditTextEffects$$ExternalSyntheticLambda4(this));
            float rad = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
            for (SpoilerEffect ef : this.spoilers) {
                ef.startRipple(x, y, rad);
            }
        }
    }

    /* renamed from: lambda$onSpoilerClicked$4$org-telegram-ui-Components-EditTextEffects  reason: not valid java name */
    public /* synthetic */ void m3950xd5a1var_d() {
        post(new EditTextEffects$$ExternalSyntheticLambda3(this));
    }

    /* renamed from: lambda$onSpoilerClicked$3$org-telegram-ui-Components-EditTextEffects  reason: not valid java name */
    public /* synthetic */ void m3949x12b589de() {
        invalidateSpoilers();
        checkSpoilerTimeout();
    }

    /* access modifiers changed from: protected */
    public void onSelectionChanged(int selStart2, int selEnd2) {
        super.onSelectionChanged(selStart2, selEnd2);
        if (!this.suppressOnTextChanged) {
            this.selStart = selStart2;
            this.selEnd = selEnd2;
            checkSpoilerTimeout();
        }
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkSpoilerTimeout() {
        /*
            r12 = this;
            r0 = 0
            android.text.Layout r1 = r12.getLayout()
            if (r1 == 0) goto L_0x0010
            android.text.Layout r1 = r12.getLayout()
            java.lang.CharSequence r1 = r1.getText()
            goto L_0x0011
        L_0x0010:
            r1 = 0
        L_0x0011:
            boolean r2 = r1 instanceof android.text.Spannable
            if (r2 == 0) goto L_0x0057
            r2 = r1
            android.text.Spannable r2 = (android.text.Spannable) r2
            int r3 = r2.length()
            java.lang.Class<org.telegram.ui.Components.TextStyleSpan> r4 = org.telegram.ui.Components.TextStyleSpan.class
            r5 = 0
            java.lang.Object[] r3 = r2.getSpans(r5, r3, r4)
            org.telegram.ui.Components.TextStyleSpan[] r3 = (org.telegram.ui.Components.TextStyleSpan[]) r3
            int r4 = r3.length
            r6 = 0
        L_0x0027:
            if (r6 >= r4) goto L_0x0057
            r7 = r3[r6]
            int r8 = r2.getSpanStart(r7)
            int r9 = r2.getSpanEnd(r7)
            boolean r10 = r7.isSpoiler()
            if (r10 == 0) goto L_0x0054
            int r10 = r12.selStart
            if (r8 <= r10) goto L_0x0041
            int r11 = r12.selEnd
            if (r9 < r11) goto L_0x004b
        L_0x0041:
            if (r10 <= r8) goto L_0x0045
            if (r10 < r9) goto L_0x004b
        L_0x0045:
            int r10 = r12.selEnd
            if (r10 <= r8) goto L_0x0054
            if (r10 >= r9) goto L_0x0054
        L_0x004b:
            r0 = 1
            java.lang.Runnable r4 = r12.spoilerTimeout
            r12.removeCallbacks(r4)
            r12.postedSpoilerTimeout = r5
            goto L_0x0057
        L_0x0054:
            int r6 = r6 + 1
            goto L_0x0027
        L_0x0057:
            boolean r2 = r12.isSpoilersRevealed
            if (r2 == 0) goto L_0x006b
            if (r0 != 0) goto L_0x006b
            boolean r2 = r12.postedSpoilerTimeout
            if (r2 != 0) goto L_0x006b
            r2 = 1
            r12.postedSpoilerTimeout = r2
            java.lang.Runnable r2 = r12.spoilerTimeout
            r3 = 10000(0x2710, double:4.9407E-320)
            r12.postDelayed(r2, r3)
        L_0x006b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextEffects.checkSpoilerTimeout():void");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.spoilerTimeout);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateEffects();
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!this.suppressOnTextChanged) {
            invalidateEffects();
            Layout layout = getLayout();
            if ((text instanceof Spannable) && layout != null) {
                int line = layout.getLineForOffset(start);
                int x = (int) layout.getPrimaryHorizontal(start);
                int y = (int) (((float) (layout.getLineTop(line) + layout.getLineBottom(line))) / 2.0f);
                for (SpoilerEffect eff : this.spoilers) {
                    if (eff.getBounds().contains(x, y)) {
                        int selOffset = lengthAfter - lengthBefore;
                        this.selStart += selOffset;
                        this.selEnd += selOffset;
                        onSpoilerClicked(eff, (float) x, (float) y);
                        return;
                    }
                }
            }
        }
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        if (!this.suppressOnTextChanged) {
            this.isSpoilersRevealed = false;
            Stack<SpoilerEffect> stack = this.spoilersPool;
            if (stack != null) {
                stack.clear();
            }
        }
        super.setText(text, type);
    }

    public void setShouldRevealSpoilersByTouch(boolean shouldRevealSpoilersByTouch2) {
        this.shouldRevealSpoilersByTouch = shouldRevealSpoilersByTouch2;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean detector = false;
        if (this.shouldRevealSpoilersByTouch && this.clickDetector.onTouchEvent(event)) {
            if (event.getActionMasked() == 1) {
                MotionEvent c = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                super.dispatchTouchEvent(c);
                c.recycle();
            }
            detector = true;
        }
        if (super.dispatchTouchEvent(event) != 0 || detector) {
            return true;
        }
        return false;
    }

    public void setSpoilersRevealed(boolean spoilersRevealed, boolean notifyEffects) {
        this.isSpoilersRevealed = spoilersRevealed;
        Spannable text = getText();
        if (text != null) {
            for (TextStyleSpan span : (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class)) {
                if (span.isSpoiler()) {
                    span.setSpoilerRevealed(spoilersRevealed);
                }
            }
        }
        this.suppressOnTextChanged = true;
        setText(text, TextView.BufferType.EDITABLE);
        setSelection(this.selStart, this.selEnd);
        this.suppressOnTextChanged = false;
        if (notifyEffects) {
            invalidateSpoilers();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        this.path.rewind();
        for (SpoilerEffect eff : this.spoilers) {
            Rect bounds = eff.getBounds();
            this.path.addRect((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, Path.Direction.CW);
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
        canvas.translate(0.0f, (float) (-getPaddingTop()));
        super.onDraw(canvas);
        canvas.restore();
        this.rect.set(0, getScrollY(), getWidth(), (getScrollY() + getHeight()) - getPaddingBottom());
        canvas.save();
        canvas.clipRect(this.rect);
        for (SpoilerEffect eff2 : this.spoilers) {
            Rect b = eff2.getBounds();
            if ((this.rect.top <= b.bottom && this.rect.bottom >= b.top) || (b.top <= this.rect.bottom && b.bottom >= this.rect.top)) {
                eff2.setColor(getPaint().getColor());
                eff2.draw(canvas);
            }
        }
        canvas.restore();
    }

    public void invalidateEffects() {
        Editable text = getText();
        if (text != null) {
            for (TextStyleSpan span : (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class)) {
                if (span.isSpoiler()) {
                    span.setSpoilerRevealed(this.isSpoilersRevealed);
                }
            }
        }
        invalidateSpoilers();
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
            Layout layout = getLayout();
            if (layout != null && (layout.getText() instanceof Spannable)) {
                SpoilerEffect.addSpoilers(this, this.spoilersPool, this.spoilers);
            }
            invalidate();
        }
    }
}
