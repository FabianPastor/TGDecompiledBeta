package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;

public class CodeNumberField extends EditTextBoldCursor {
    private static final FloatPropertyCompat<CodeNumberField> ERROR_PROGRESS = new SimpleFloatPropertyCompat("errorProgress", CodeNumberField$$ExternalSyntheticLambda3.INSTANCE, CodeNumberField$$ExternalSyntheticLambda7.INSTANCE).setMultiplier(100.0f);
    private static final FloatPropertyCompat<CodeNumberField> FOCUSED_PROGRESS = new SimpleFloatPropertyCompat("focusedProgress", CodeNumberField$$ExternalSyntheticLambda2.INSTANCE, CodeNumberField$$ExternalSyntheticLambda6.INSTANCE).setMultiplier(100.0f);
    private static final float SPRING_MULTIPLIER = 100.0f;
    private static final FloatPropertyCompat<CodeNumberField> SUCCESS_PROGRESS = new SimpleFloatPropertyCompat("successProgress", CodeNumberField$$ExternalSyntheticLambda4.INSTANCE, CodeNumberField$$ExternalSyntheticLambda8.INSTANCE).setMultiplier(100.0f);
    private static final FloatPropertyCompat<CodeNumberField> SUCCESS_SCALE_PROGRESS = new SimpleFloatPropertyCompat("successScaleProgress", CodeNumberField$$ExternalSyntheticLambda5.INSTANCE, CodeNumberField$$ExternalSyntheticLambda9.INSTANCE).setMultiplier(100.0f);
    ActionMode actionMode;
    float enterAnimation = 1.0f;
    ValueAnimator enterAnimator;
    /* access modifiers changed from: private */
    public float errorProgress;
    private SpringAnimation errorSpringAnimation = new SpringAnimation(this, ERROR_PROGRESS);
    float exitAnimation = 1.0f;
    ValueAnimator exitAnimator;
    Bitmap exitBitmap;
    Canvas exitCanvas;
    /* access modifiers changed from: private */
    public float focusedProgress;
    private SpringAnimation focusedSpringAnimation = new SpringAnimation(this, FOCUSED_PROGRESS);
    boolean pressed = false;
    boolean replaceAnimation;
    private boolean showSoftInputOnFocusInternal = true;
    float startX = 0.0f;
    float startY = 0.0f;
    /* access modifiers changed from: private */
    public float successProgress;
    /* access modifiers changed from: private */
    public float successScaleProgress = 1.0f;
    private SpringAnimation successScaleSpringAnimation = new SpringAnimation(this, SUCCESS_SCALE_PROGRESS);
    private SpringAnimation successSpringAnimation = new SpringAnimation(this, SUCCESS_PROGRESS);

    static /* synthetic */ void lambda$static$1(CodeNumberField obj, float value) {
        obj.focusedProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    static /* synthetic */ void lambda$static$3(CodeNumberField obj, float value) {
        obj.errorProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    static /* synthetic */ void lambda$static$5(CodeNumberField obj, float value) {
        obj.successProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    static /* synthetic */ void lambda$static$7(CodeNumberField obj, float value) {
        obj.successScaleProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

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

    public void setShowSoftInputOnFocusCompat(boolean showSoftInputOnFocus) {
        this.showSoftInputOnFocusInternal = showSoftInputOnFocus;
        if (Build.VERSION.SDK_INT >= 21) {
            setShowSoftInputOnFocus(showSoftInputOnFocus);
        }
    }

    public float getFocusedProgress() {
        return this.focusedProgress;
    }

    public void animateFocusedProgress(float newProgress) {
        animateSpring(this.focusedSpringAnimation, 100.0f * newProgress);
    }

    public float getErrorProgress() {
        return this.errorProgress;
    }

    public void animateErrorProgress(float newProgress) {
        animateSpring(this.errorSpringAnimation, 100.0f * newProgress);
    }

    public float getSuccessProgress() {
        return this.successProgress;
    }

    public float getSuccessScaleProgress() {
        return this.successScaleProgress;
    }

    public void animateSuccessProgress(float newProgress) {
        animateSpring(this.successSpringAnimation, newProgress * 100.0f);
        this.successScaleSpringAnimation.cancel();
        if (newProgress != 0.0f) {
            ((SpringAnimation) ((SpringAnimation) this.successScaleSpringAnimation.setSpring(new SpringForce(1.0f).setStiffness(500.0f).setDampingRatio(0.75f).setFinalPosition(100.0f)).setStartValue(100.0f)).setStartVelocity(4000.0f)).start();
        } else {
            this.successScaleProgress = 1.0f;
        }
    }

    private void animateSpring(SpringAnimation anim, float progress) {
        if (anim.getSpring() == null || progress != anim.getSpring().getFinalPosition()) {
            anim.cancel();
            anim.setSpring(new SpringForce(progress).setStiffness(400.0f).setDampingRatio(1.0f).setFinalPosition(progress)).start();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.focusedSpringAnimation.cancel();
        this.errorSpringAnimation.cancel();
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
            CharSequence transformed = getTransformationMethod().getTransformation(getText(), this);
            StaticLayout staticLayout = new StaticLayout(transformed, getLayout().getPaint(), (int) Math.ceil((double) getLayout().getPaint().measureText(transformed, 0, transformed.length())), Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
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

    /* renamed from: lambda$startExitAnimation$8$org-telegram-ui-CodeNumberField  reason: not valid java name */
    public /* synthetic */ void m2026lambda$startExitAnimation$8$orgtelegramuiCodeNumberField(ValueAnimator valueAnimator1) {
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

    /* renamed from: lambda$startEnterAnimation$9$org-telegram-ui-CodeNumberField  reason: not valid java name */
    public /* synthetic */ void m2025lambda$startEnterAnimation$9$orgtelegramuiCodeNumberField(ValueAnimator valueAnimator1) {
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

    /* JADX WARNING: type inference failed for: r2v8, types: [android.view.ViewParent] */
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
            if (r0 != r2) goto L_0x0096
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
            if (r2 != r1) goto L_0x0094
            boolean r1 = r7.pressed
            if (r1 == 0) goto L_0x0094
            boolean r1 = r7.isFocused()
            if (r1 == 0) goto L_0x0087
            if (r0 == 0) goto L_0x0087
            android.content.Context r1 = r7.getContext()
            java.lang.Class<android.content.ClipboardManager> r2 = android.content.ClipboardManager.class
            java.lang.Object r1 = androidx.core.content.ContextCompat.getSystemService(r1, r2)
            android.content.ClipboardManager r1 = (android.content.ClipboardManager) r1
            if (r1 == 0) goto L_0x0086
            android.content.ClipDescription r2 = r1.getPrimaryClipDescription()
            if (r2 != 0) goto L_0x005a
            goto L_0x0086
        L_0x005a:
            android.content.ClipDescription r2 = r1.getPrimaryClipDescription()
            java.lang.String r4 = "text/plain"
            r2.hasMimeType(r4)
            android.content.ClipData r2 = r1.getPrimaryClip()
            android.content.ClipData$Item r2 = r2.getItemAt(r3)
            r4 = -1
            java.lang.CharSequence r5 = r2.getText()
            java.lang.String r5 = r5.toString()
            int r6 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x007a }
            r4 = r6
            goto L_0x007b
        L_0x007a:
            r6 = move-exception
        L_0x007b:
            if (r4 <= 0) goto L_0x0085
            org.telegram.ui.CodeNumberField$2 r6 = new org.telegram.ui.CodeNumberField$2
            r6.<init>()
            r7.startActionMode(r6)
        L_0x0085:
            goto L_0x008a
        L_0x0086:
            return r3
        L_0x0087:
            r7.requestFocus()
        L_0x008a:
            r7.setSelection(r3)
            boolean r1 = r7.showSoftInputOnFocusInternal
            if (r1 == 0) goto L_0x0094
            org.telegram.messenger.AndroidUtilities.showKeyboard(r7)
        L_0x0094:
            r7.pressed = r3
        L_0x0096:
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
