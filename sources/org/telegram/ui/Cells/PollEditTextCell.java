package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;

public class PollEditTextCell extends FrameLayout {
    private boolean alwaysShowText2;
    private CheckBox2 checkBox;
    private AnimatorSet checkBoxAnimation;
    private ImageView deleteImageView;
    private ImageView moveImageView;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    /* access modifiers changed from: protected */
    public boolean drawDivider() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isChecked(PollEditTextCell pollEditTextCell) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onActionModeStart(EditTextBoldCursor editTextBoldCursor, ActionMode actionMode) {
    }

    /* access modifiers changed from: protected */
    public void onEditTextDraw(EditTextBoldCursor editTextBoldCursor, Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowCheckBox() {
        return false;
    }

    public PollEditTextCell(Context context, View.OnClickListener onClickListener) {
        this(context, false, onClickListener);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PollEditTextCell(Context context, boolean z, View.OnClickListener onClickListener) {
        super(context);
        Context context2 = context;
        View.OnClickListener onClickListener2 = onClickListener;
        if (z) {
            AnonymousClass1 r4 = new EditTextCaption(context2) {
                public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                    InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                    if (PollEditTextCell.this.showNextButton) {
                        editorInfo.imeOptions &= -NUM;
                    }
                    return onCreateInputConnection;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    PollEditTextCell.this.onEditTextDraw(this, canvas);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!isEnabled()) {
                        return false;
                    }
                    if (motionEvent.getAction() == 1) {
                        PollEditTextCell.this.onFieldTouchUp(this);
                    }
                    return super.onTouchEvent(motionEvent);
                }

                public ActionMode startActionMode(ActionMode.Callback callback, int i) {
                    ActionMode startActionMode = super.startActionMode(callback, i);
                    PollEditTextCell.this.onActionModeStart(this, startActionMode);
                    return startActionMode;
                }

                public ActionMode startActionMode(ActionMode.Callback callback) {
                    ActionMode startActionMode = super.startActionMode(callback);
                    PollEditTextCell.this.onActionModeStart(this, startActionMode);
                    return startActionMode;
                }
            };
            this.textView = r4;
            r4.setAllowTextEntitiesIntersection(true);
        } else {
            this.textView = new EditTextBoldCursor(context2) {
                public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                    InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                    if (PollEditTextCell.this.showNextButton) {
                        editorInfo.imeOptions &= -NUM;
                    }
                    return onCreateInputConnection;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    PollEditTextCell.this.onEditTextDraw(this, canvas);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!isEnabled()) {
                        return false;
                    }
                    if (motionEvent.getAction() == 1) {
                        PollEditTextCell.this.onFieldTouchUp(this);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
        }
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.textView.setTextSize(1, 16.0f);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable((Drawable) null);
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | NUM);
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        this.textView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(11.0f));
        if (onClickListener2 != null) {
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 58.0f : 64.0f, 0.0f, !LocaleController.isRTL ? 58.0f : 64.0f, 0.0f));
            ImageView imageView = new ImageView(context2);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(NUM);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            ImageView imageView2 = new ImageView(context2);
            this.deleteImageView = imageView2;
            imageView2.setFocusable(false);
            this.deleteImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setOnClickListener(onClickListener2);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(48, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context2);
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(48, 24.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhiteGrayIcon", "checkboxCheck");
            this.checkBox.setDrawUnchecked(true);
            this.checkBox.setChecked(true, false);
            this.checkBox.setAlpha(0.0f);
            this.checkBox.setDrawBackgroundAsArc(8);
            addView(this.checkBox, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            this.checkBox.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PollEditTextCell.this.lambda$new$0$PollEditTextCell(view);
                }
            });
            return;
        }
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 16, 19.0f, 0.0f, 19.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$PollEditTextCell(View view) {
        if (this.checkBox.getTag() != null) {
            onCheckBoxClick(this, !this.checkBox.isChecked());
        }
    }

    public void createErrorTextView() {
        this.alwaysShowText2 = true;
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.textView2 = simpleTextView;
        simpleTextView.setTextSize(13);
        int i = 3;
        this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        SimpleTextView simpleTextView2 = this.textView2;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(48, 24.0f, i | 48, LocaleController.isRTL ? 20.0f : 0.0f, 17.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int size = View.MeasureSpec.getSize(i);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        ImageView imageView2 = this.moveImageView;
        if (imageView2 != null) {
            imageView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
        if (this.textView2 == null) {
            i3 = 42;
        } else {
            i3 = this.deleteImageView == null ? 70 : 122;
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((size - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp((float) i3), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        int measuredHeight = this.textView.getMeasuredHeight();
        setMeasuredDimension(size, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + (this.needDivider ? 1 : 0));
        SimpleTextView simpleTextView2 = this.textView2;
        if (simpleTextView2 != null && !this.alwaysShowText2) {
            simpleTextView2.setAlpha(measuredHeight >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.checkBox != null) {
            setShowCheckBox(shouldShowCheckBox(), false);
            this.checkBox.setChecked(isChecked(this), false);
        }
    }

    /* access modifiers changed from: protected */
    public void onCheckBoxClick(PollEditTextCell pollEditTextCell, boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public void callOnDelete() {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.callOnClick();
        }
    }

    public void setShowNextButton(boolean z) {
        this.showNextButton = z;
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public void addTextWatcher(TextWatcher textWatcher) {
        this.textView.addTextChangedListener(textWatcher);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setShowCheckBox(boolean z, boolean z2) {
        if (z != (this.checkBox.getTag() != null)) {
            AnimatorSet animatorSet = this.checkBoxAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.checkBoxAnimation = null;
            }
            CheckBox2 checkBox2 = this.checkBox;
            if (z) {
                i = 1;
            }
            checkBox2.setTag(i);
            float f = 1.0f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.checkBoxAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                CheckBox2 checkBox22 = this.checkBox;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(checkBox22, property, fArr);
                ImageView imageView = this.moveImageView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (z) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.checkBoxAnimation.setDuration(180);
                this.checkBoxAnimation.start();
                return;
            }
            this.checkBox.setAlpha(z ? 1.0f : 0.0f);
            ImageView imageView2 = this.moveImageView;
            if (z) {
                f = 0.0f;
            }
            imageView2.setAlpha(f);
        }
    }

    public void setTextAndHint(CharSequence charSequence, String str, boolean z) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag((Object) null);
        }
        this.textView.setText(charSequence);
        if (!TextUtils.isEmpty(charSequence)) {
            EditTextBoldCursor editTextBoldCursor = this.textView;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
        }
        this.textView.setHint(str);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setText2(String str) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(str);
        }
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        int i;
        if (this.needDivider && drawDivider()) {
            float f2 = 63.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = (float) AndroidUtilities.dp(this.moveImageView != null ? 63.0f : 20.0f);
            }
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.moveImageView == null) {
                    f2 = 20.0f;
                }
                i = AndroidUtilities.dp(f2);
            } else {
                i = 0;
            }
            canvas.drawLine(f, measuredHeight, (float) (measuredWidth - i), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
