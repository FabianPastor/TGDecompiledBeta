package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.ClipboardManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;

public class CodeNumberField extends EditTextBoldCursor {
    float enterAnimation = 1.0f;
    ValueAnimator enterAnimator;
    float exitAnimation = 1.0f;
    ValueAnimator exitAnimator;
    Bitmap exitBitmap;
    Canvas exitCanvas;
    float focusedProgress;
    boolean pressed = false;
    boolean replaceAnimation;

    public CodeNumberField(Context context) {
        super(context);
        setBackground((Drawable) null);
        setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        setMovementMethod((MovementMethod) null);
        addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                CodeNumberField.this.startEnterAnimation(charSequence.length() != 0);
                CodeNumberField.this.hideActionMode();
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startExitAnimation$0(ValueAnimator valueAnimator) {
        this.exitAnimation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public void startEnterAnimation(boolean z) {
        this.replaceAnimation = z;
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startEnterAnimation$1(ValueAnimator valueAnimator) {
        this.enterAnimation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public boolean requestFocus(int i, Rect rect) {
        ((ViewGroup) getParent()).invalidate();
        return super.requestFocus(i, rect);
    }

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
                if (!isFocused() || codeFieldContainer == null) {
                    requestFocus();
                } else {
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
                        startActionMode(new ActionMode.Callback() {
                            public void onDestroyActionMode(ActionMode actionMode) {
                            }

                            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                return true;
                            }

                            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                menu.add(0, 16908322, 0, 17039371);
                                return true;
                            }

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
                }
                setSelection(0);
                AndroidUtilities.showKeyboard(this);
            }
            this.pressed = false;
        }
        return this.pressed;
    }

    /* access modifiers changed from: private */
    public void pasteFromClipboard() {
        ClipboardManager clipboardManager;
        CodeFieldContainer codeFieldContainer = getParent() instanceof CodeFieldContainer ? (CodeFieldContainer) getParent() : null;
        if (codeFieldContainer != null && (clipboardManager = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class)) != null) {
            clipboardManager.getPrimaryClipDescription().hasMimeType("text/plain");
            int i = -1;
            String charSequence = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
            try {
                i = Integer.parseInt(charSequence);
            } catch (Exception unused) {
            }
            if (i > 0) {
                codeFieldContainer.setText(charSequence, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (!isFocused()) {
            hideActionMode();
        }
    }
}
