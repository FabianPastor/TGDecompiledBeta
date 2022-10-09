package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
/* loaded from: classes3.dex */
public class CodeNumberField extends EditTextBoldCursor {
    float enterAnimation;
    ValueAnimator enterAnimator;
    private float errorProgress;
    private SpringAnimation errorSpringAnimation;
    float exitAnimation;
    ValueAnimator exitAnimator;
    Bitmap exitBitmap;
    Canvas exitCanvas;
    private float focusedProgress;
    private SpringAnimation focusedSpringAnimation;
    boolean pressed;
    boolean replaceAnimation;
    private boolean showSoftInputOnFocusInternal;
    private float successProgress;
    private float successScaleProgress;
    private SpringAnimation successScaleSpringAnimation;
    private SpringAnimation successSpringAnimation;
    private static final FloatPropertyCompat<CodeNumberField> FOCUSED_PROGRESS = new SimpleFloatPropertyCompat("focusedProgress", CodeNumberField$$ExternalSyntheticLambda4.INSTANCE, CodeNumberField$$ExternalSyntheticLambda6.INSTANCE).setMultiplier(100.0f);
    private static final FloatPropertyCompat<CodeNumberField> ERROR_PROGRESS = new SimpleFloatPropertyCompat("errorProgress", CodeNumberField$$ExternalSyntheticLambda3.INSTANCE, CodeNumberField$$ExternalSyntheticLambda9.INSTANCE).setMultiplier(100.0f);
    private static final FloatPropertyCompat<CodeNumberField> SUCCESS_PROGRESS = new SimpleFloatPropertyCompat("successProgress", CodeNumberField$$ExternalSyntheticLambda2.INSTANCE, CodeNumberField$$ExternalSyntheticLambda7.INSTANCE).setMultiplier(100.0f);
    private static final FloatPropertyCompat<CodeNumberField> SUCCESS_SCALE_PROGRESS = new SimpleFloatPropertyCompat("successScaleProgress", CodeNumberField$$ExternalSyntheticLambda5.INSTANCE, CodeNumberField$$ExternalSyntheticLambda8.INSTANCE).setMultiplier(100.0f);

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(CodeNumberField codeNumberField, float f) {
        codeNumberField.focusedProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$3(CodeNumberField codeNumberField, float f) {
        codeNumberField.errorProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$5(CodeNumberField codeNumberField, float f) {
        codeNumberField.successProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$7(CodeNumberField codeNumberField, float f) {
        codeNumberField.successScaleProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    public CodeNumberField(Context context) {
        super(context);
        this.successScaleProgress = 1.0f;
        this.focusedSpringAnimation = new SpringAnimation(this, FOCUSED_PROGRESS);
        this.errorSpringAnimation = new SpringAnimation(this, ERROR_PROGRESS);
        this.successSpringAnimation = new SpringAnimation(this, SUCCESS_PROGRESS);
        this.successScaleSpringAnimation = new SpringAnimation(this, SUCCESS_SCALE_PROGRESS);
        this.showSoftInputOnFocusInternal = true;
        this.enterAnimation = 1.0f;
        this.exitAnimation = 1.0f;
        this.pressed = false;
        setBackground(null);
        setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        setMovementMethod(null);
        addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.CodeNumberField.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                CodeNumberField.this.startEnterAnimation(charSequence.length() != 0);
                CodeNumberField.this.hideActionMode();
            }
        });
    }

    public void setShowSoftInputOnFocusCompat(boolean z) {
        this.showSoftInputOnFocusInternal = z;
        if (Build.VERSION.SDK_INT >= 21) {
            setShowSoftInputOnFocus(z);
        }
    }

    public float getFocusedProgress() {
        return this.focusedProgress;
    }

    public void animateFocusedProgress(float f) {
        animateSpring(this.focusedSpringAnimation, f * 100.0f);
    }

    public float getErrorProgress() {
        return this.errorProgress;
    }

    public void animateErrorProgress(float f) {
        animateSpring(this.errorSpringAnimation, f * 100.0f);
    }

    public float getSuccessProgress() {
        return this.successProgress;
    }

    public float getSuccessScaleProgress() {
        return this.successScaleProgress;
    }

    public void animateSuccessProgress(float f) {
        animateSpring(this.successSpringAnimation, f * 100.0f);
        this.successScaleSpringAnimation.cancel();
        if (f != 0.0f) {
            this.successScaleSpringAnimation.setSpring(new SpringForce(1.0f).setStiffness(500.0f).setDampingRatio(0.75f).setFinalPosition(100.0f)).setStartValue(100.0f).setStartVelocity(4000.0f).start();
        } else {
            this.successScaleProgress = 1.0f;
        }
    }

    private void animateSpring(SpringAnimation springAnimation, float f) {
        if (springAnimation.getSpring() == null || f != springAnimation.getSpring().getFinalPosition()) {
            springAnimation.cancel();
            springAnimation.setSpring(new SpringForce(f).setStiffness(400.0f).setDampingRatio(1.0f).setFinalPosition(f)).start();
        }
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.focusedSpringAnimation.cancel();
        this.errorSpringAnimation.cancel();
    }

    public void startExitAnimation() {
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0 || getLayout() == null) {
            return;
        }
        Bitmap bitmap = this.exitBitmap;
        if (bitmap == null || bitmap.getHeight() != getMeasuredHeight() || this.exitBitmap.getWidth() != getMeasuredWidth()) {
            Bitmap bitmap2 = this.exitBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            this.exitBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            this.exitCanvas = new Canvas(this.exitBitmap);
        }
        this.exitBitmap.eraseColor(0);
        CharSequence transformation = getTransformationMethod().getTransformation(getText(), this);
        StaticLayout staticLayout = new StaticLayout(transformation, getLayout().getPaint(), (int) Math.ceil(getLayout().getPaint().measureText(transformation, 0, transformation.length())), Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
        this.exitCanvas.save();
        this.exitCanvas.translate((getMeasuredWidth() - staticLayout.getWidth()) / 2.0f, (getMeasuredHeight() - staticLayout.getHeight()) / 2.0f);
        staticLayout.draw(this.exitCanvas);
        this.exitCanvas.restore();
        this.exitAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.exitAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CodeNumberField$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CodeNumberField.this.lambda$startExitAnimation$8(valueAnimator);
            }
        });
        this.exitAnimator.setDuration(220L);
        this.exitAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startExitAnimation$8(ValueAnimator valueAnimator) {
        this.exitAnimation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public void startEnterAnimation(boolean z) {
        this.replaceAnimation = z;
        this.enterAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.enterAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CodeNumberField$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CodeNumberField.this.lambda$startEnterAnimation$9(valueAnimator);
            }
        });
        if (!this.replaceAnimation) {
            this.enterAnimator.setInterpolator(new OvershootInterpolator(1.5f));
            this.enterAnimator.setDuration(350L);
        } else {
            this.enterAnimator.setDuration(220L);
        }
        this.enterAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startEnterAnimation$9(ValueAnimator valueAnimator) {
        this.enterAnimation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    @Override // android.view.View
    public boolean requestFocus(int i, Rect rect) {
        ((ViewGroup) getParent()).invalidate();
        return super.requestFocus(i, rect);
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.pressed = true;
            motionEvent.getX();
            motionEvent.getY();
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            CodeFieldContainer codeFieldContainer = null;
            if (getParent() instanceof CodeFieldContainer) {
                codeFieldContainer = (CodeFieldContainer) getParent();
            }
            if (motionEvent.getAction() == 1 && this.pressed) {
                if (isFocused() && codeFieldContainer != null) {
                    ClipboardManager clipboardManager = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class);
                    if (clipboardManager == null || clipboardManager.getPrimaryClipDescription() == null) {
                        return false;
                    }
                    clipboardManager.getPrimaryClipDescription().hasMimeType("text/plain");
                    int i = -1;
                    try {
                        i = Integer.parseInt(clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());
                    } catch (Exception unused) {
                    }
                    if (i > 0) {
                        startActionMode(new ActionMode.Callback() { // from class: org.telegram.ui.CodeNumberField.2
                            @Override // android.view.ActionMode.Callback
                            public void onDestroyActionMode(ActionMode actionMode) {
                            }

                            @Override // android.view.ActionMode.Callback
                            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                return true;
                            }

                            @Override // android.view.ActionMode.Callback
                            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                menu.add(0, 16908322, 0, 17039371);
                                return true;
                            }

                            @Override // android.view.ActionMode.Callback
                            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                if (menuItem.getItemId() != 16908322) {
                                    return true;
                                }
                                CodeNumberField.this.pasteFromClipboard();
                                CodeNumberField.this.hideActionMode();
                                return true;
                            }
                        });
                    }
                } else {
                    requestFocus();
                }
                setSelection(0);
                if (this.showSoftInputOnFocusInternal) {
                    AndroidUtilities.showKeyboard(this);
                }
            }
            this.pressed = false;
        }
        return this.pressed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pasteFromClipboard() {
        ClipboardManager clipboardManager;
        CodeFieldContainer codeFieldContainer = getParent() instanceof CodeFieldContainer ? (CodeFieldContainer) getParent() : null;
        if (codeFieldContainer == null || (clipboardManager = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class)) == null) {
            return;
        }
        clipboardManager.getPrimaryClipDescription().hasMimeType("text/plain");
        int i = -1;
        String charSequence = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        try {
            i = Integer.parseInt(charSequence);
        } catch (Exception unused) {
        }
        if (i <= 0) {
            return;
        }
        codeFieldContainer.setText(charSequence, true);
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (!isFocused()) {
            hideActionMode();
        }
    }
}
