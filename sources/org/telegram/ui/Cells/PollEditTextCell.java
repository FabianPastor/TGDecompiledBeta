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
import java.util.ArrayList;
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

    public PollEditTextCell(Context context, View.OnClickListener onDelete) {
        this(context, false, onDelete);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PollEditTextCell(Context context, boolean caption, View.OnClickListener onDelete) {
        super(context);
        Context context2 = context;
        View.OnClickListener onClickListener = onDelete;
        if (caption) {
            AnonymousClass1 r5 = new EditTextCaption(context2, (Theme.ResourcesProvider) null) {
                public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                    InputConnection conn = super.onCreateInputConnection(outAttrs);
                    if (PollEditTextCell.this.showNextButton) {
                        outAttrs.imeOptions &= -NUM;
                    }
                    return conn;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    PollEditTextCell.this.onEditTextDraw(this, canvas);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    if (!isEnabled()) {
                        return false;
                    }
                    if (event.getAction() == 1) {
                        PollEditTextCell.this.onFieldTouchUp(this);
                    }
                    return super.onTouchEvent(event);
                }

                public ActionMode startActionMode(ActionMode.Callback callback, int type) {
                    ActionMode actionMode = super.startActionMode(callback, type);
                    PollEditTextCell.this.onActionModeStart(this, actionMode);
                    return actionMode;
                }

                public ActionMode startActionMode(ActionMode.Callback callback) {
                    ActionMode actionMode = super.startActionMode(callback);
                    PollEditTextCell.this.onActionModeStart(this, actionMode);
                    return actionMode;
                }
            };
            this.textView = r5;
            r5.setAllowTextEntitiesIntersection(true);
        } else {
            this.textView = new EditTextBoldCursor(context2) {
                public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                    InputConnection conn = super.onCreateInputConnection(outAttrs);
                    if (PollEditTextCell.this.showNextButton) {
                        outAttrs.imeOptions &= -NUM;
                    }
                    return conn;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    PollEditTextCell.this.onEditTextDraw(this, canvas);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    if (!isEnabled()) {
                        return false;
                    }
                    if (event.getAction() == 1) {
                        PollEditTextCell.this.onFieldTouchUp(this);
                    }
                    return super.onTouchEvent(event);
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
        if (onClickListener != null) {
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
            this.deleteImageView.setOnClickListener(onClickListener);
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
            this.checkBox.setContentDescription(LocaleController.getString("AccDescrQuizCorrectAnswer", NUM));
            this.checkBox.setDrawUnchecked(true);
            this.checkBox.setChecked(true, false);
            this.checkBox.setAlpha(0.0f);
            this.checkBox.setDrawBackgroundAsArc(8);
            addView(this.checkBox, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            this.checkBox.setOnClickListener(new PollEditTextCell$$ExternalSyntheticLambda0(this));
            return;
        }
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 16, 19.0f, 0.0f, 19.0f, 0.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-PollEditTextCell  reason: not valid java name */
    public /* synthetic */ void m1548lambda$new$0$orgtelegramuiCellsPollEditTextCell(View v) {
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int right;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
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
            right = 42;
        } else if (this.deleteImageView == null) {
            right = 70;
        } else {
            right = 122;
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((width - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp((float) right), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        int h = this.textView.getMeasuredHeight();
        setMeasuredDimension(width, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + (this.needDivider ? 1 : 0));
        SimpleTextView simpleTextView2 = this.textView2;
        if (simpleTextView2 != null && !this.alwaysShowText2) {
            simpleTextView2.setAlpha(h >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
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
    public void onCheckBoxClick(PollEditTextCell editText, boolean checked) {
        this.checkBox.setChecked(checked, true);
    }

    /* access modifiers changed from: protected */
    public boolean isChecked(PollEditTextCell editText) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onActionModeStart(EditTextBoldCursor editText, ActionMode actionMode) {
    }

    public void callOnDelete() {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.callOnClick();
        }
    }

    public void setShowNextButton(boolean value) {
        this.showNextButton = value;
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.textView.addTextChangedListener(watcher);
    }

    /* access modifiers changed from: protected */
    public boolean drawDivider() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onEditTextDraw(EditTextBoldCursor editText, Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowCheckBox() {
        return false;
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public int length() {
        return this.textView.length();
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setShowCheckBox(boolean show, boolean animated) {
        if (show != (this.checkBox.getTag() != null)) {
            AnimatorSet animatorSet = this.checkBoxAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.checkBoxAnimation = null;
            }
            CheckBox2 checkBox2 = this.checkBox;
            if (show) {
                i = 1;
            }
            checkBox2.setTag(i);
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.checkBoxAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                CheckBox2 checkBox22 = this.checkBox;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(checkBox22, property, fArr);
                ImageView imageView = this.moveImageView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.checkBoxAnimation.setDuration(180);
                this.checkBoxAnimation.start();
                return;
            }
            this.checkBox.setAlpha(show ? 1.0f : 0.0f);
            ImageView imageView2 = this.moveImageView;
            if (show) {
                f = 0.0f;
            }
            imageView2.setAlpha(f);
        }
    }

    public void setText(CharSequence text, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndHint(CharSequence text, String hint, boolean divider) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag((Object) null);
        }
        this.textView.setText(text);
        if (!TextUtils.isEmpty(text)) {
            EditTextBoldCursor editTextBoldCursor = this.textView;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
        }
        this.textView.setHint(hint);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> arrayList) {
        setEnabled(value);
    }

    /* access modifiers changed from: protected */
    public void onFieldTouchUp(EditTextBoldCursor editText) {
    }

    public void setText2(String text) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(text);
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
