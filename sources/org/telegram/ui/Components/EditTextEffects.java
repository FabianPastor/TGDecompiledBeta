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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.spoilers.SpoilersClickDetector;

public class EditTextEffects extends EditText {
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiDrawables;
    private SpoilersClickDetector clickDetector = new SpoilersClickDetector(this, this.spoilers, new EditTextEffects$$ExternalSyntheticLambda5(this));
    private boolean clipToPadding;
    private boolean isSpoilersRevealed;
    private Layout lastLayout = null;
    private float lastRippleX;
    private float lastRippleY;
    private int lastTextLength;
    private Path path = new Path();
    private boolean postedSpoilerTimeout;
    private Rect rect = new Rect();
    private int selEnd;
    private int selStart;
    private boolean shouldRevealSpoilersByTouch = true;
    private Runnable spoilerTimeout = new EditTextEffects$$ExternalSyntheticLambda4(this);
    private List<SpoilerEffect> spoilers = new ArrayList();
    private Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private boolean suppressOnTextChanged;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        this.postedSpoilerTimeout = false;
        this.isSpoilersRevealed = false;
        invalidateSpoilers();
        if (!this.spoilers.isEmpty()) {
            this.spoilers.get(0).setOnRippleEndCallback(new EditTextEffects$$ExternalSyntheticLambda0(this));
            float sqrt = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
            for (SpoilerEffect startRipple : this.spoilers) {
                startRipple.startRipple(this.lastRippleX, this.lastRippleY, sqrt, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        setSpoilersRevealed(false, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        post(new EditTextEffects$$ExternalSyntheticLambda1(this));
    }

    public EditTextEffects(Context context) {
        super(context);
    }

    /* access modifiers changed from: private */
    public void onSpoilerClicked(SpoilerEffect spoilerEffect, float f, float f2) {
        if (!this.isSpoilersRevealed) {
            this.lastRippleX = f;
            this.lastRippleY = f2;
            this.postedSpoilerTimeout = false;
            removeCallbacks(this.spoilerTimeout);
            setSpoilersRevealed(true, false);
            spoilerEffect.setOnRippleEndCallback(new EditTextEffects$$ExternalSyntheticLambda2(this));
            float sqrt = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
            for (SpoilerEffect startRipple : this.spoilers) {
                startRipple.startRipple(f, f2, sqrt);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSpoilerClicked$4() {
        post(new EditTextEffects$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSpoilerClicked$3() {
        invalidateSpoilers();
        checkSpoilerTimeout();
    }

    /* access modifiers changed from: protected */
    public void onSelectionChanged(int i, int i2) {
        super.onSelectionChanged(i, i2);
        if (!this.suppressOnTextChanged) {
            this.selStart = i;
            this.selEnd = i2;
            checkSpoilerTimeout();
        }
    }

    private void checkSpoilerTimeout() {
        int i;
        int i2;
        CharSequence text = getLayout() != null ? getLayout().getText() : null;
        boolean z = false;
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;
            TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) spannable.getSpans(0, spannable.length(), TextStyleSpan.class);
            int length = textStyleSpanArr.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    break;
                }
                TextStyleSpan textStyleSpan = textStyleSpanArr[i3];
                int spanStart = spannable.getSpanStart(textStyleSpan);
                int spanEnd = spannable.getSpanEnd(textStyleSpan);
                if (!textStyleSpan.isSpoiler() || ((spanStart <= (i = this.selStart) || spanEnd >= this.selEnd) && ((i <= spanStart || i >= spanEnd) && ((i2 = this.selEnd) <= spanStart || i2 >= spanEnd)))) {
                    i3++;
                }
            }
            removeCallbacks(this.spoilerTimeout);
            this.postedSpoilerTimeout = false;
            z = true;
        }
        if (this.isSpoilersRevealed && !z && !this.postedSpoilerTimeout) {
            this.postedSpoilerTimeout = true;
            postDelayed(this.spoilerTimeout, 10000);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.spoilerTimeout);
        AnimatedEmojiSpan.release((View) this, this.animatedEmojiDrawables);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateAnimatedEmoji();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateEffects();
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        if (!this.suppressOnTextChanged) {
            invalidateEffects();
            Layout layout = getLayout();
            if ((charSequence instanceof Spannable) && layout != null) {
                int lineForOffset = layout.getLineForOffset(i);
                int primaryHorizontal = (int) layout.getPrimaryHorizontal(i);
                int lineTop = (int) (((float) (layout.getLineTop(lineForOffset) + layout.getLineBottom(lineForOffset))) / 2.0f);
                Iterator<SpoilerEffect> it = this.spoilers.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    SpoilerEffect next = it.next();
                    if (next.getBounds().contains(primaryHorizontal, lineTop)) {
                        int i4 = i3 - i2;
                        this.selStart += i4;
                        this.selEnd += i4;
                        onSpoilerClicked(next, (float) primaryHorizontal, (float) lineTop);
                        break;
                    }
                }
            }
        }
        updateAnimatedEmoji();
        invalidate();
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        if (!this.suppressOnTextChanged) {
            this.isSpoilersRevealed = false;
            Stack<SpoilerEffect> stack = this.spoilersPool;
            if (stack != null) {
                stack.clear();
            }
        }
        super.setText(charSequence, bufferType);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateAnimatedEmoji();
    }

    public void setShouldRevealSpoilersByTouch(boolean z) {
        this.shouldRevealSpoilersByTouch = z;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (!this.shouldRevealSpoilersByTouch || !this.clickDetector.onTouchEvent(motionEvent)) {
            z = false;
        } else {
            if (motionEvent.getActionMasked() == 1) {
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                super.dispatchTouchEvent(obtain);
                obtain.recycle();
            }
            z = true;
        }
        if (super.dispatchTouchEvent(motionEvent) || z) {
            return true;
        }
        return false;
    }

    public void setSpoilersRevealed(boolean z, boolean z2) {
        this.isSpoilersRevealed = z;
        Editable text = getText();
        if (text != null) {
            for (TextStyleSpan textStyleSpan : (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class)) {
                if (textStyleSpan.isSpoiler()) {
                    textStyleSpan.setSpoilerRevealed(z);
                }
            }
        }
        this.suppressOnTextChanged = true;
        setText(text, TextView.BufferType.EDITABLE);
        setSelection(this.selStart, this.selEnd);
        this.suppressOnTextChanged = false;
        if (z2) {
            invalidateSpoilers();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        if (this.clipToPadding && getScrollY() != 0) {
            canvas.clipRect(0, getScrollY(), getMeasuredWidth(), getMeasuredHeight() + getScrollY());
        }
        this.path.rewind();
        for (SpoilerEffect bounds : this.spoilers) {
            Rect bounds2 = bounds.getBounds();
            this.path.addRect((float) bounds2.left, (float) bounds2.top, (float) bounds2.right, (float) bounds2.bottom, Path.Direction.CW);
        }
        canvas.clipPath(this.path, Region.Op.DIFFERENCE);
        int length = (getLayout() == null || getLayout().getText() == null) ? 0 : getLayout().getText().length();
        Layout layout = this.lastLayout;
        if (!(layout != null && layout == getLayout() && this.lastTextLength == length)) {
            updateAnimatedEmoji();
        }
        super.onDraw(canvas);
        if (this.animatedEmojiDrawables != null) {
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, getLayout(), this.animatedEmojiDrawables, 0.0f, this.spoilers, (float) (computeVerticalScrollOffset() - AndroidUtilities.dp(6.0f)), (float) (computeVerticalScrollOffset() + computeVerticalScrollExtent()), 0.0f, 1.0f);
        }
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
        for (SpoilerEffect next : this.spoilers) {
            Rect bounds3 = next.getBounds();
            Rect rect2 = this.rect;
            int i = rect2.top;
            int i2 = bounds3.bottom;
            if ((i <= i2 && rect2.bottom >= bounds3.top) || (bounds3.top <= rect2.bottom && i2 >= i)) {
                next.setColor(getPaint().getColor());
                next.draw(canvas);
            }
        }
        canvas.restore();
    }

    public void updateAnimatedEmoji() {
        int i = 0;
        this.animatedEmojiDrawables = AnimatedEmojiSpan.update(AnimatedEmojiDrawable.getCacheTypeForEnterView(), (View) this, this.animatedEmojiDrawables, getLayout());
        if (!(getLayout() == null || getLayout().getText() == null)) {
            i = getLayout().getText().length();
        }
        if (this.lastLayout != getLayout() || this.lastTextLength != i) {
            this.lastLayout = getLayout();
            this.lastTextLength = i;
        }
    }

    public void invalidateEffects() {
        Editable text = getText();
        if (text != null) {
            for (TextStyleSpan textStyleSpan : (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class)) {
                if (textStyleSpan.isSpoiler()) {
                    textStyleSpan.setSpoilerRevealed(this.isSpoilersRevealed);
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
                AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans = this.animatedEmojiDrawables;
                if (emojiGroupedSpans != null) {
                    emojiGroupedSpans.recordPositions(false);
                }
                SpoilerEffect.addSpoilers(this, this.spoilersPool, this.spoilers);
                AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans2 = this.animatedEmojiDrawables;
                if (emojiGroupedSpans2 != null) {
                    emojiGroupedSpans2.recordPositions(true);
                }
            }
            invalidate();
        }
    }

    public void setClipToPadding(boolean z) {
        this.clipToPadding = z;
    }
}
