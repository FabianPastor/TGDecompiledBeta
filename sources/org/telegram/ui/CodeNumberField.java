package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.view.ActionMode;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;

public class CodeNumberField extends EditTextBoldCursor {
    ActionMode actionMode;
    float enterAnimation = 1.0f;
    ValueAnimator enterAnimator;
    float exitAnimation = 1.0f;
    ValueAnimator exitAnimator;
    Bitmap exitBitmap;
    Canvas exitCanvas;
    float focusedProgress;
    boolean pressed = false;
    boolean replaceAnimation;
    float startX = 0.0f;
    float startY = 0.0f;

    public CodeNumberField(Context context) {
        super(context);
        setBackground((Drawable) null);
        setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        setMovementMethod((MovementMethod) null);
        addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CodeNumberField.this.startEnterAnimation(charSequence.length() != 0);
                CodeNumberField.this.hideActionMode();
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void startExitAnimation() {
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0 && getLayout() != null) {
            Bitmap bitmap = this.exitBitmap;
            if (!(bitmap != null && bitmap.getHeight() == getMeasuredHeight() && this.exitBitmap.getWidth() == getMeasuredWidth())) {
                Bitmap bitmap2 = this.exitBitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.exitBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                this.exitCanvas = new Canvas(this.exitBitmap);
            }
            this.exitBitmap.eraseColor(0);
            StaticLayout staticLayout = new StaticLayout(getText(), getLayout().getPaint(), (int) Math.ceil((double) getLayout().getPaint().measureText(String.valueOf(getText()))), Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
            this.exitCanvas.save();
            this.exitCanvas.translate(((float) (getMeasuredWidth() - staticLayout.getWidth())) / 2.0f, ((float) (getMeasuredHeight() - staticLayout.getHeight())) / 2.0f);
            staticLayout.draw(this.exitCanvas);
            this.exitCanvas.restore();
            this.exitAnimation = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.exitAnimator = ofFloat;
            ofFloat.addUpdateListener(new CodeNumberField$$ExternalSyntheticLambda1(this));
            this.exitAnimator.setDuration(220);
            this.exitAnimator.start();
        }
    }

    /* renamed from: lambda$startExitAnimation$0$org-telegram-ui-CodeNumberField  reason: not valid java name */
    public /* synthetic */ void m1988lambda$startExitAnimation$0$orgtelegramuiCodeNumberField(ValueAnimator valueAnimator1) {
        this.exitAnimation = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public void startEnterAnimation(boolean replace) {
        this.replaceAnimation = replace;
        this.enterAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.enterAnimator = ofFloat;
        ofFloat.addUpdateListener(new CodeNumberField$$ExternalSyntheticLambda0(this));
        if (!this.replaceAnimation) {
            this.enterAnimator.setInterpolator(new OvershootInterpolator(1.5f));
            this.enterAnimator.setDuration(350);
        } else {
            this.enterAnimator.setDuration(220);
        }
        this.enterAnimator.start();
    }

    /* renamed from: lambda$startEnterAnimation$1$org-telegram-ui-CodeNumberField  reason: not valid java name */
    public /* synthetic */ void m1987lambda$startEnterAnimation$1$orgtelegramuiCodeNumberField(ValueAnimator valueAnimator1) {
        this.enterAnimation = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        ((ViewGroup) getParent()).invalidate();
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    /* JADX WARNING: type inference failed for: r2v7, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getAction()
            r1 = 1
            if (r0 != 0) goto L_0x0015
            r7.pressed = r1
            float r0 = r8.getX()
            r7.startX = r0
            float r0 = r8.getY()
            r7.startY = r0
        L_0x0015:
            int r0 = r8.getAction()
            if (r0 == r1) goto L_0x0022
            int r0 = r8.getAction()
            r2 = 3
            if (r0 != r2) goto L_0x008b
        L_0x0022:
            r0 = 0
            android.view.ViewParent r2 = r7.getParent()
            boolean r2 = r2 instanceof org.telegram.ui.CodeFieldContainer
            if (r2 == 0) goto L_0x0032
            android.view.ViewParent r2 = r7.getParent()
            r0 = r2
            org.telegram.ui.CodeFieldContainer r0 = (org.telegram.ui.CodeFieldContainer) r0
        L_0x0032:
            int r2 = r8.getAction()
            r3 = 0
            if (r2 != r1) goto L_0x0089
            boolean r1 = r7.pressed
            if (r1 == 0) goto L_0x0089
            boolean r1 = r7.isFocused()
            if (r1 == 0) goto L_0x0080
            if (r0 == 0) goto L_0x0080
            android.content.Context r1 = r7.getContext()
            java.lang.Class<android.content.ClipboardManager> r2 = android.content.ClipboardManager.class
            java.lang.Object r1 = androidx.core.content.ContextCompat.getSystemService(r1, r2)
            android.content.ClipboardManager r1 = (android.content.ClipboardManager) r1
            if (r1 != 0) goto L_0x0054
            return r3
        L_0x0054:
            android.content.ClipDescription r2 = r1.getPrimaryClipDescription()
            java.lang.String r4 = "text/plain"
            r2.hasMimeType(r4)
            android.content.ClipData r2 = r1.getPrimaryClip()
            android.content.ClipData$Item r2 = r2.getItemAt(r3)
            r4 = -1
            java.lang.CharSequence r5 = r2.getText()
            java.lang.String r5 = r5.toString()
            int r6 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x0074 }
            r4 = r6
            goto L_0x0075
        L_0x0074:
            r6 = move-exception
        L_0x0075:
            if (r4 <= 0) goto L_0x007f
            org.telegram.ui.CodeNumberField$2 r6 = new org.telegram.ui.CodeNumberField$2
            r6.<init>()
            r7.startActionMode(r6)
        L_0x007f:
            goto L_0x0083
        L_0x0080:
            r7.requestFocus()
        L_0x0083:
            r7.setSelection(r3)
            org.telegram.messenger.AndroidUtilities.showKeyboard(r7)
        L_0x0089:
            r7.pressed = r3
        L_0x008b:
            boolean r0 = r7.pressed
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CodeNumberField.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: type inference failed for: r1v5, types: [android.view.ViewParent] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void pasteFromClipboard() {
        /*
            r6 = this;
            r0 = 0
            android.view.ViewParent r1 = r6.getParent()
            boolean r1 = r1 instanceof org.telegram.ui.CodeFieldContainer
            if (r1 == 0) goto L_0x0010
            android.view.ViewParent r1 = r6.getParent()
            r0 = r1
            org.telegram.ui.CodeFieldContainer r0 = (org.telegram.ui.CodeFieldContainer) r0
        L_0x0010:
            if (r0 == 0) goto L_0x0049
            android.content.Context r1 = r6.getContext()
            java.lang.Class<android.content.ClipboardManager> r2 = android.content.ClipboardManager.class
            java.lang.Object r1 = androidx.core.content.ContextCompat.getSystemService(r1, r2)
            android.content.ClipboardManager r1 = (android.content.ClipboardManager) r1
            if (r1 != 0) goto L_0x0021
            return
        L_0x0021:
            android.content.ClipDescription r2 = r1.getPrimaryClipDescription()
            java.lang.String r3 = "text/plain"
            r2.hasMimeType(r3)
            android.content.ClipData r2 = r1.getPrimaryClip()
            r3 = 0
            android.content.ClipData$Item r2 = r2.getItemAt(r3)
            r3 = -1
            java.lang.CharSequence r4 = r2.getText()
            java.lang.String r4 = r4.toString()
            int r5 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0042 }
            r3 = r5
            goto L_0x0043
        L_0x0042:
            r5 = move-exception
        L_0x0043:
            if (r3 <= 0) goto L_0x0049
            r5 = 1
            r0.setText(r4, r5)
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CodeNumberField.pasteFromClipboard():void");
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!isFocused()) {
            hideActionMode();
        }
    }
}
