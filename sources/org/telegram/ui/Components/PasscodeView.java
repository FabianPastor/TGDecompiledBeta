package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.os.CancellationSignal;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class PasscodeView extends FrameLayout {
    private static final int[] ids = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    private Drawable backgroundDrawable;
    private FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    /* access modifiers changed from: private */
    public Runnable checkRunnable = new Runnable() {
        public void run() {
            PasscodeView.this.checkRetryTextView();
            AndroidUtilities.runOnUIThread(PasscodeView.this.checkRunnable, 100);
        }
    };
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    /* access modifiers changed from: private */
    public AlertDialog fingerprintDialog;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private int keyboardHeight = 0;
    private int lastValue;
    private ArrayList<TextView> lettersTextViews;
    private ArrayList<FrameLayout> numberFrameLayouts;
    private ArrayList<TextView> numberTextViews;
    private FrameLayout numbersFrameLayout;
    private TextView passcodeTextView;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    private Rect rect = new Rect();
    private TextView retryTextView;
    /* access modifiers changed from: private */
    public boolean selfCancelled;

    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    static /* synthetic */ boolean lambda$onShow$6(View view, MotionEvent motionEvent) {
        return true;
    }

    private class AnimatingTextView extends FrameLayout {
        /* access modifiers changed from: private */
        public ArrayList<TextView> characterTextViews = new ArrayList<>(4);
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        /* access modifiers changed from: private */
        public Runnable dotRunnable;
        /* access modifiers changed from: private */
        public ArrayList<TextView> dotTextViews = new ArrayList<>(4);
        private StringBuilder stringBuilder = new StringBuilder(4);

        public AnimatingTextView(PasscodeView passcodeView, Context context) {
            super(context);
            for (int i = 0; i < 4; i++) {
                TextView textView = new TextView(context);
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                layoutParams.height = AndroidUtilities.dp(50.0f);
                layoutParams.gravity = 51;
                textView.setLayoutParams(layoutParams);
                this.characterTextViews.add(textView);
                TextView textView2 = new TextView(context);
                textView2.setTextColor(-1);
                textView2.setTextSize(1, 36.0f);
                textView2.setGravity(17);
                textView2.setAlpha(0.0f);
                textView2.setText("•");
                textView2.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView2.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView2);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) textView2.getLayoutParams();
                layoutParams2.width = AndroidUtilities.dp(50.0f);
                layoutParams2.height = AndroidUtilities.dp(50.0f);
                layoutParams2.gravity = 51;
                textView2.setLayoutParams(layoutParams2);
                this.dotTextViews.add(textView2);
            }
        }

        private int getXForTextView(int i) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (i * AndroidUtilities.dp(30.0f))) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String str) {
            if (this.stringBuilder.length() != 4) {
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList arrayList = new ArrayList();
                final int length = this.stringBuilder.length();
                this.stringBuilder.append(str);
                TextView textView = this.characterTextViews.get(length);
                textView.setText(str);
                textView.setTranslationX((float) getXForTextView(length));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                TextView textView2 = this.dotTextViews.get(length);
                textView2.setTranslationX((float) getXForTextView(length));
                textView2.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (int i = length + 1; i < 4; i++) {
                    TextView textView3 = this.characterTextViews.get(i);
                    if (textView3.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.ALPHA, new float[]{0.0f}));
                    }
                    TextView textView4 = this.dotTextViews.get(i);
                    if (textView4.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView4, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView4, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView4, View.ALPHA, new float[]{0.0f}));
                    }
                }
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                AnonymousClass1 r11 = new Runnable() {
                    public void run() {
                        if (AnimatingTextView.this.dotRunnable == this) {
                            ArrayList arrayList = new ArrayList();
                            TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(length);
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                            TextView textView2 = (TextView) AnimatingTextView.this.dotTextViews.get(length);
                            arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{1.0f}));
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = new AnimatorSet();
                            AnimatingTextView.this.currentAnimation.setDuration(150);
                            AnimatingTextView.this.currentAnimation.playTogether(arrayList);
                            AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                                        AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                                    }
                                }
                            });
                            AnimatingTextView.this.currentAnimation.start();
                        }
                    }
                };
                this.dotRunnable = r11;
                AndroidUtilities.runOnUIThread(r11, 1500);
                for (int i2 = 0; i2 < length; i2++) {
                    TextView textView5 = this.characterTextViews.get(i2);
                    arrayList.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_X, new float[]{(float) getXForTextView(i2)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView5, View.SCALE_X, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView5, View.SCALE_Y, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView5, View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_Y, new float[]{0.0f}));
                    TextView textView6 = this.dotTextViews.get(i2);
                    arrayList.add(ObjectAnimator.ofFloat(textView6, View.TRANSLATION_X, new float[]{(float) getXForTextView(i2)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView6, View.SCALE_X, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView6, View.SCALE_Y, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView6, View.ALPHA, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView6, View.TRANSLATION_Y, new float[]{0.0f}));
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        }

        public String getString() {
            return this.stringBuilder.toString();
        }

        public int length() {
            return this.stringBuilder.length();
        }

        public void eraseLastCharacter() {
            if (this.stringBuilder.length() != 0) {
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList arrayList = new ArrayList();
                int length = this.stringBuilder.length() - 1;
                if (length != 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (int i = length; i < 4; i++) {
                    TextView textView = this.characterTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, new float[]{(float) getXForTextView(i)}));
                    }
                    TextView textView2 = this.dotTextViews.get(i);
                    if (textView2.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_X, new float[]{(float) getXForTextView(i)}));
                    }
                }
                if (length == 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (int i2 = 0; i2 < length; i2++) {
                    arrayList.add(ObjectAnimator.ofFloat(this.characterTextViews.get(i2), View.TRANSLATION_X, new float[]{(float) getXForTextView(i2)}));
                    arrayList.add(ObjectAnimator.ofFloat(this.dotTextViews.get(i2), View.TRANSLATION_X, new float[]{(float) getXForTextView(i2)}));
                }
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.dotRunnable = null;
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        }

        /* access modifiers changed from: private */
        public void eraseAllCharacters(boolean z) {
            if (this.stringBuilder.length() != 0) {
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.dotRunnable = null;
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.currentAnimation = null;
                }
                StringBuilder sb = this.stringBuilder;
                sb.delete(0, sb.length());
                if (z) {
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < 4; i++) {
                        TextView textView = this.characterTextViews.get(i);
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                        }
                        TextView textView2 = this.dotTextViews.get(i);
                        if (textView2.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{0.0f}));
                        }
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.setDuration(150);
                    this.currentAnimation.playTogether(arrayList);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                                AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                    return;
                }
                for (int i2 = 0; i2 < 4; i2++) {
                    this.characterTextViews.get(i2).setAlpha(0.0f);
                    this.dotTextViews.get(i2).setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            Runnable runnable = this.dotRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.dotRunnable = null;
            }
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            for (int i5 = 0; i5 < 4; i5++) {
                if (i5 < this.stringBuilder.length()) {
                    TextView textView = this.characterTextViews.get(i5);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(i5));
                    TextView textView2 = this.dotTextViews.get(i5);
                    textView2.setAlpha(1.0f);
                    textView2.setScaleX(1.0f);
                    textView2.setScaleY(1.0f);
                    textView2.setTranslationY(0.0f);
                    textView2.setTranslationX((float) getXForTextView(i5));
                } else {
                    this.characterTextViews.get(i5).setAlpha(0.0f);
                    this.dotTextViews.get(i5).setAlpha(0.0f);
                }
            }
            super.onLayout(z, i, i2, i3, i4);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PasscodeView(Context context) {
        super(context);
        Context context2 = context;
        char c = 0;
        setWillNotDraw(false);
        setVisibility(8);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.backgroundFrameLayout = frameLayout;
        addView(frameLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backgroundFrameLayout.getLayoutParams();
        int i = -1;
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.backgroundFrameLayout.setLayoutParams(layoutParams);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.passwordFrameLayout = frameLayout2;
        addView(frameLayout2);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.gravity = 51;
        this.passwordFrameLayout.setLayoutParams(layoutParams2);
        ImageView imageView = new ImageView(context2);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(NUM);
        this.passwordFrameLayout.addView(imageView);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (AndroidUtilities.density < 1.0f) {
            layoutParams3.width = AndroidUtilities.dp(30.0f);
            layoutParams3.height = AndroidUtilities.dp(30.0f);
        } else {
            layoutParams3.width = AndroidUtilities.dp(40.0f);
            layoutParams3.height = AndroidUtilities.dp(40.0f);
        }
        layoutParams3.gravity = 81;
        layoutParams3.bottomMargin = AndroidUtilities.dp(100.0f);
        imageView.setLayoutParams(layoutParams3);
        TextView textView = new TextView(context2);
        this.passcodeTextView = textView;
        textView.setTextColor(-1);
        this.passcodeTextView.setTextSize(1, 14.0f);
        this.passcodeTextView.setGravity(1);
        this.passwordFrameLayout.addView(this.passcodeTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 62.0f));
        TextView textView2 = new TextView(context2);
        this.retryTextView = textView2;
        textView2.setTextColor(-1);
        this.retryTextView.setTextSize(1, 15.0f);
        this.retryTextView.setGravity(1);
        this.retryTextView.setVisibility(4);
        addView(this.retryTextView, LayoutHelper.createFrame(-2, -2, 17));
        AnimatingTextView animatingTextView = new AnimatingTextView(this, context2);
        this.passwordEditText2 = animatingTextView;
        this.passwordFrameLayout.addView(animatingTextView);
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.passwordEditText2.getLayoutParams();
        layoutParams4.height = -2;
        layoutParams4.width = -1;
        layoutParams4.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams4.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams4.bottomMargin = AndroidUtilities.dp(6.0f);
        layoutParams4.gravity = 81;
        this.passwordEditText2.setLayoutParams(layoutParams4);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.passwordEditText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 36.0f);
        this.passwordEditText.setTextColor(-1);
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setImeOptions(6);
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setBackgroundDrawable((Drawable) null);
        this.passwordEditText.setCursorColor(-1);
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(32.0f));
        this.passwordFrameLayout.addView(this.passwordEditText);
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams5.height = -2;
        layoutParams5.width = -1;
        layoutParams5.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams5.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams5.gravity = 81;
        this.passwordEditText.setLayoutParams(layoutParams5);
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return PasscodeView.this.lambda$new$0$PasscodeView(textView, i, keyEvent);
            }
        });
        this.passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (PasscodeView.this.passwordEditText.length() == 4 && SharedConfig.passcodeType == 0) {
                    PasscodeView.this.processDone(false);
                }
            }
        });
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback(this) {
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        ImageView imageView2 = new ImageView(context2);
        this.checkImage = imageView2;
        imageView2.setImageResource(NUM);
        this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
        this.checkImage.setBackgroundResource(NUM);
        this.passwordFrameLayout.addView(this.checkImage);
        FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) this.checkImage.getLayoutParams();
        layoutParams6.width = AndroidUtilities.dp(60.0f);
        layoutParams6.height = AndroidUtilities.dp(60.0f);
        layoutParams6.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams6.rightMargin = AndroidUtilities.dp(10.0f);
        layoutParams6.gravity = 85;
        this.checkImage.setLayoutParams(layoutParams6);
        this.checkImage.setContentDescription(LocaleController.getString("Done", NUM));
        this.checkImage.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PasscodeView.this.lambda$new$1$PasscodeView(view);
            }
        });
        FrameLayout frameLayout3 = new FrameLayout(context2);
        frameLayout3.setBackgroundColor(NUM);
        this.passwordFrameLayout.addView(frameLayout3);
        FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) frameLayout3.getLayoutParams();
        layoutParams7.width = -1;
        layoutParams7.height = AndroidUtilities.dp(1.0f);
        layoutParams7.gravity = 83;
        layoutParams7.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams7.rightMargin = AndroidUtilities.dp(20.0f);
        frameLayout3.setLayoutParams(layoutParams7);
        FrameLayout frameLayout4 = new FrameLayout(context2);
        this.numbersFrameLayout = frameLayout4;
        addView(frameLayout4);
        FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) this.numbersFrameLayout.getLayoutParams();
        layoutParams8.width = -1;
        layoutParams8.height = -1;
        layoutParams8.gravity = 51;
        this.numbersFrameLayout.setLayoutParams(layoutParams8);
        this.lettersTextViews = new ArrayList<>(10);
        this.numberTextViews = new ArrayList<>(10);
        this.numberFrameLayouts = new ArrayList<>(10);
        int i2 = 0;
        while (i2 < 10) {
            TextView textView3 = new TextView(context2);
            textView3.setTextColor(i);
            textView3.setTextSize(1, 36.0f);
            textView3.setGravity(17);
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[c] = Integer.valueOf(i2);
            textView3.setText(String.format(locale, "%d", objArr));
            this.numbersFrameLayout.addView(textView3);
            FrameLayout.LayoutParams layoutParams9 = (FrameLayout.LayoutParams) textView3.getLayoutParams();
            layoutParams9.width = AndroidUtilities.dp(50.0f);
            layoutParams9.height = AndroidUtilities.dp(50.0f);
            layoutParams9.gravity = 51;
            textView3.setLayoutParams(layoutParams9);
            textView3.setImportantForAccessibility(2);
            this.numberTextViews.add(textView3);
            TextView textView4 = new TextView(context2);
            textView4.setTextSize(1, 12.0f);
            textView4.setTextColor(Integer.MAX_VALUE);
            textView4.setGravity(17);
            this.numbersFrameLayout.addView(textView4);
            FrameLayout.LayoutParams layoutParams10 = (FrameLayout.LayoutParams) textView4.getLayoutParams();
            layoutParams10.width = AndroidUtilities.dp(50.0f);
            layoutParams10.height = AndroidUtilities.dp(20.0f);
            layoutParams10.gravity = 51;
            textView4.setLayoutParams(layoutParams10);
            textView4.setImportantForAccessibility(2);
            if (i2 != 0) {
                switch (i2) {
                    case 2:
                        textView4.setText("ABC");
                        break;
                    case 3:
                        textView4.setText("DEF");
                        break;
                    case 4:
                        textView4.setText("GHI");
                        break;
                    case 5:
                        textView4.setText("JKL");
                        break;
                    case 6:
                        textView4.setText("MNO");
                        break;
                    case 7:
                        textView4.setText("PQRS");
                        break;
                    case 8:
                        textView4.setText("TUV");
                        break;
                    case 9:
                        textView4.setText("WXYZ");
                        break;
                }
            } else {
                textView4.setText("+");
            }
            this.lettersTextViews.add(textView4);
            i2++;
            c = 0;
            i = -1;
        }
        ImageView imageView3 = new ImageView(context2);
        this.eraseView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseView.setImageResource(NUM);
        this.numbersFrameLayout.addView(this.eraseView);
        FrameLayout.LayoutParams layoutParams11 = (FrameLayout.LayoutParams) this.eraseView.getLayoutParams();
        layoutParams11.width = AndroidUtilities.dp(50.0f);
        layoutParams11.height = AndroidUtilities.dp(50.0f);
        layoutParams11.gravity = 51;
        this.eraseView.setLayoutParams(layoutParams11);
        for (int i3 = 0; i3 < 11; i3++) {
            AnonymousClass3 r3 = new FrameLayout(this, context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    accessibilityNodeInfo.setClassName("android.widget.Button");
                }
            };
            r3.setBackgroundResource(NUM);
            r3.setTag(Integer.valueOf(i3));
            if (i3 == 10) {
                r3.setOnLongClickListener(new View.OnLongClickListener() {
                    public final boolean onLongClick(View view) {
                        return PasscodeView.this.lambda$new$2$PasscodeView(view);
                    }
                });
                r3.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
                setNextFocus(r3, NUM);
            } else {
                r3.setContentDescription(i3 + "");
                if (i3 == 0) {
                    setNextFocus(r3, NUM);
                } else if (i3 == 9) {
                    setNextFocus(r3, NUM);
                } else {
                    setNextFocus(r3, ids[i3 + 1]);
                }
            }
            r3.setId(ids[i3]);
            r3.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PasscodeView.this.lambda$new$3$PasscodeView(view);
                }
            });
            this.numberFrameLayouts.add(r3);
        }
        for (int i4 = 10; i4 >= 0; i4--) {
            FrameLayout frameLayout5 = this.numberFrameLayouts.get(i4);
            this.numbersFrameLayout.addView(frameLayout5);
            FrameLayout.LayoutParams layoutParams12 = (FrameLayout.LayoutParams) frameLayout5.getLayoutParams();
            layoutParams12.width = AndroidUtilities.dp(100.0f);
            layoutParams12.height = AndroidUtilities.dp(100.0f);
            layoutParams12.gravity = 51;
            frameLayout5.setLayoutParams(layoutParams12);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ boolean lambda$new$0$PasscodeView(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone(false);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$PasscodeView(View view) {
        processDone(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ boolean lambda$new$2$PasscodeView(View view) {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$PasscodeView(View view) {
        switch (((Integer) view.getTag()).intValue()) {
            case 0:
                this.passwordEditText2.appendCharacter("0");
                break;
            case 1:
                this.passwordEditText2.appendCharacter("1");
                break;
            case 2:
                this.passwordEditText2.appendCharacter("2");
                break;
            case 3:
                this.passwordEditText2.appendCharacter("3");
                break;
            case 4:
                this.passwordEditText2.appendCharacter("4");
                break;
            case 5:
                this.passwordEditText2.appendCharacter("5");
                break;
            case 6:
                this.passwordEditText2.appendCharacter("6");
                break;
            case 7:
                this.passwordEditText2.appendCharacter("7");
                break;
            case 8:
                this.passwordEditText2.appendCharacter("8");
                break;
            case 9:
                this.passwordEditText2.appendCharacter("9");
                break;
            case 10:
                this.passwordEditText2.eraseLastCharacter();
                break;
        }
        if (this.passwordEditText2.length() == 4) {
            processDone(false);
        }
    }

    private void setNextFocus(View view, int i) {
        view.setNextFocusForwardId(i);
        if (Build.VERSION.SDK_INT >= 22) {
            view.setAccessibilityTraversalBefore(i);
        }
    }

    public void setDelegate(PasscodeViewDelegate passcodeViewDelegate) {
        this.delegate = passcodeViewDelegate;
    }

    /* access modifiers changed from: private */
    public void processDone(boolean z) {
        String str;
        if (!z) {
            if (SharedConfig.passcodeRetryInMs <= 0) {
                int i = SharedConfig.passcodeType;
                if (i == 0) {
                    str = this.passwordEditText2.getString();
                } else if (i == 1) {
                    str = this.passwordEditText.getText().toString();
                } else {
                    str = "";
                }
                if (str.length() == 0) {
                    onPasscodeError();
                    return;
                } else if (!SharedConfig.checkPasscode(str)) {
                    SharedConfig.increaseBadPasscodeTries();
                    if (SharedConfig.passcodeRetryInMs > 0) {
                        checkRetryTextView();
                    }
                    this.passwordEditText.setText("");
                    this.passwordEditText2.eraseAllCharacters(true);
                    onPasscodeError();
                    return;
                }
            } else {
                return;
            }
        }
        SharedConfig.badPasscodeTries = 0;
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{(float) AndroidUtilities.dp(0.0f)})});
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PasscodeView.this.setVisibility(8);
            }
        });
        animatorSet.start();
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener((View.OnTouchListener) null);
        PasscodeViewDelegate passcodeViewDelegate = this.delegate;
        if (passcodeViewDelegate != null) {
            passcodeViewDelegate.didAcceptedPassword();
        }
    }

    /* access modifiers changed from: private */
    public void shakeTextView(final float f, final int i) {
        if (i != 6) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.passcodeTextView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(f)})});
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PasscodeView passcodeView = PasscodeView.this;
                    int i = i;
                    passcodeView.shakeTextView(i == 5 ? 0.0f : -f, i + 1);
                }
            });
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void checkRetryTextView() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime > SharedConfig.lastUptimeMillis) {
            long j = SharedConfig.passcodeRetryInMs - (elapsedRealtime - SharedConfig.lastUptimeMillis);
            SharedConfig.passcodeRetryInMs = j;
            if (j < 0) {
                SharedConfig.passcodeRetryInMs = 0;
            }
        }
        SharedConfig.lastUptimeMillis = elapsedRealtime;
        SharedConfig.saveConfig();
        long j2 = SharedConfig.passcodeRetryInMs;
        if (j2 > 0) {
            double d = (double) j2;
            Double.isNaN(d);
            int max = Math.max(1, (int) Math.ceil(d / 1000.0d));
            if (max != this.lastValue) {
                this.retryTextView.setText(LocaleController.formatString("TooManyTries", NUM, LocaleController.formatPluralString("Seconds", max)));
                this.lastValue = max;
            }
            if (this.retryTextView.getVisibility() != 0) {
                this.retryTextView.setVisibility(0);
                this.passwordFrameLayout.setVisibility(4);
                if (this.numbersFrameLayout.getVisibility() == 0) {
                    this.numbersFrameLayout.setVisibility(4);
                }
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
                AndroidUtilities.runOnUIThread(this.checkRunnable, 100);
                return;
            }
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        if (this.passwordFrameLayout.getVisibility() != 0) {
            this.retryTextView.setVisibility(4);
            this.passwordFrameLayout.setVisibility(0);
            int i = SharedConfig.passcodeType;
            if (i == 0) {
                this.numbersFrameLayout.setVisibility(0);
            } else if (i == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        }
    }

    private void onPasscodeError() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        shakeTextView(2.0f, 0);
    }

    public void onResume() {
        checkRetryTextView();
        if (this.retryTextView.getVisibility() != 0) {
            if (SharedConfig.passcodeType == 1) {
                EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
                if (editTextBoldCursor != null) {
                    editTextBoldCursor.requestFocus();
                    AndroidUtilities.showKeyboard(this.passwordEditText);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        PasscodeView.this.lambda$onResume$4$PasscodeView();
                    }
                }, 200);
            }
            checkFingerprint();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$4 */
    public /* synthetic */ void lambda$onResume$4$PasscodeView() {
        EditTextBoldCursor editTextBoldCursor;
        if (this.retryTextView.getVisibility() != 0 && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void onPause() {
        CancellationSignal cancellationSignal2;
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        AlertDialog alertDialog = this.fingerprintDialog;
        if (alertDialog != null) {
            try {
                if (alertDialog.isShowing()) {
                    this.fingerprintDialog.dismiss();
                }
                this.fingerprintDialog = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= 23 && (cancellationSignal2 = this.cancellationSignal) != null) {
                cancellationSignal2.cancel();
                this.cancellationSignal = null;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    private void checkFingerprint() {
        Activity activity = (Activity) getContext();
        if (Build.VERSION.SDK_INT >= 23 && activity != null && SharedConfig.useFingerprint && !ApplicationLoader.mainInterfacePaused) {
            try {
                AlertDialog alertDialog = this.fingerprintDialog;
                if (alertDialog != null && alertDialog.isShowing()) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                FingerprintManagerCompat from = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (from.isHardwareDetected() && from.hasEnrolledFingerprints()) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    TextView textView = new TextView(getContext());
                    textView.setId(1000);
                    textView.setTextAppearance(16974344);
                    textView.setTextColor(Theme.getColor("dialogTextBlack"));
                    textView.setText(LocaleController.getString("FingerprintInfo", NUM));
                    relativeLayout.addView(textView);
                    RelativeLayout.LayoutParams createRelative = LayoutHelper.createRelative(-2, -2);
                    createRelative.addRule(10);
                    createRelative.addRule(20);
                    textView.setLayoutParams(createRelative);
                    ImageView imageView = new ImageView(getContext());
                    this.fingerprintImageView = imageView;
                    imageView.setImageResource(NUM);
                    this.fingerprintImageView.setId(1001);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, 1000));
                    TextView textView2 = new TextView(getContext());
                    this.fingerprintStatusTextView = textView2;
                    textView2.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", NUM));
                    this.fingerprintStatusTextView.setTextAppearance(16974320);
                    this.fingerprintStatusTextView.setTextColor(Theme.getColor("dialogTextBlack") & NUM);
                    relativeLayout.addView(this.fingerprintStatusTextView);
                    RelativeLayout.LayoutParams createRelative2 = LayoutHelper.createRelative(-2, -2);
                    createRelative2.setMarginStart(AndroidUtilities.dp(16.0f));
                    createRelative2.addRule(8, 1001);
                    createRelative2.addRule(6, 1001);
                    createRelative2.addRule(17, 1001);
                    this.fingerprintStatusTextView.setLayoutParams(createRelative2);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public final void onDismiss(DialogInterface dialogInterface) {
                            PasscodeView.this.lambda$checkFingerprint$5$PasscodeView(dialogInterface);
                        }
                    });
                    AlertDialog alertDialog2 = this.fingerprintDialog;
                    if (alertDialog2 != null) {
                        if (alertDialog2.isShowing()) {
                            this.fingerprintDialog.dismiss();
                        }
                    }
                    this.fingerprintDialog = builder.show();
                    CancellationSignal cancellationSignal2 = new CancellationSignal();
                    this.cancellationSignal = cancellationSignal2;
                    this.selfCancelled = false;
                    from.authenticate((FingerprintManagerCompat.CryptoObject) null, 0, cancellationSignal2, new FingerprintManagerCompat.AuthenticationCallback() {
                        public void onAuthenticationError(int i, CharSequence charSequence) {
                            if (!PasscodeView.this.selfCancelled && i != 5) {
                                PasscodeView.this.showFingerprintError(charSequence);
                            }
                        }

                        public void onAuthenticationHelp(int i, CharSequence charSequence) {
                            PasscodeView.this.showFingerprintError(charSequence);
                        }

                        public void onAuthenticationFailed() {
                            PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", NUM));
                        }

                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult authenticationResult) {
                            try {
                                if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                    PasscodeView.this.fingerprintDialog.dismiss();
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                            AlertDialog unused = PasscodeView.this.fingerprintDialog = null;
                            PasscodeView.this.processDone(true);
                        }
                    }, (Handler) null);
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkFingerprint$5 */
    public /* synthetic */ void lambda$checkFingerprint$5$PasscodeView(DialogInterface dialogInterface) {
        CancellationSignal cancellationSignal2 = this.cancellationSignal;
        if (cancellationSignal2 != null) {
            this.selfCancelled = true;
            try {
                cancellationSignal2.cancel();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.cancellationSignal = null;
        }
    }

    public void onShow() {
        View currentFocus;
        EditTextBoldCursor editTextBoldCursor;
        checkRetryTextView();
        Activity activity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (!(this.retryTextView.getVisibility() == 0 || (editTextBoldCursor = this.passwordEditText) == null)) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (!(activity == null || (currentFocus = activity.getCurrentFocus()) == null)) {
            currentFocus.clearFocus();
            AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
        }
        if (this.retryTextView.getVisibility() != 0) {
            checkFingerprint();
        }
        if (getVisibility() != 0) {
            setAlpha(1.0f);
            setTranslationY(0.0f);
            if (Theme.isCustomTheme()) {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                this.backgroundFrameLayout.setBackgroundColor(-NUM);
            } else if ("d".equals(Theme.getSelectedBackgroundSlug())) {
                this.backgroundFrameLayout.setBackgroundColor(-11436898);
            } else {
                Drawable cachedWallpaper = Theme.getCachedWallpaper();
                this.backgroundDrawable = cachedWallpaper;
                if (cachedWallpaper != null) {
                    this.backgroundFrameLayout.setBackgroundColor(-NUM);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(-11436898);
                }
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", NUM));
            int i = SharedConfig.passcodeType;
            if (i == 0) {
                if (this.retryTextView.getVisibility() != 0) {
                    this.numbersFrameLayout.setVisibility(0);
                }
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
            } else if (i == 1) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setInputType(129);
                this.numbersFrameLayout.setVisibility(8);
                this.passwordEditText.setFocusable(true);
                this.passwordEditText.setFocusableInTouchMode(true);
                this.passwordEditText.setVisibility(0);
                this.passwordEditText2.setVisibility(8);
                this.checkImage.setVisibility(0);
            }
            setVisibility(0);
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setText("");
            this.passwordEditText2.eraseAllCharacters(false);
            setOnTouchListener($$Lambda$PasscodeView$toX_NNPd08dZTbLjuc_vei8QYI.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public void showFingerprintError(CharSequence charSequence) {
        this.fingerprintImageView.setImageResource(NUM);
        this.fingerprintStatusTextView.setText(charSequence);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    /* JADX WARNING: type inference failed for: r5v4, types: [android.view.ViewGroup$LayoutParams] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r13, int r14) {
        /*
            r12 = this;
            int r0 = android.view.View.MeasureSpec.getSize(r13)
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.y
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 0
            r4 = 21
            if (r2 < r4) goto L_0x0011
            r2 = 0
            goto L_0x0013
        L_0x0011:
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0013:
            int r1 = r1 - r2
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            r4 = 2
            if (r2 != 0) goto L_0x006c
            android.content.Context r2 = r12.getContext()
            android.content.res.Resources r2 = r2.getResources()
            android.content.res.Configuration r2 = r2.getConfiguration()
            int r2 = r2.orientation
            if (r2 != r4) goto L_0x006c
            android.widget.FrameLayout r2 = r12.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r5 = org.telegram.messenger.SharedConfig.passcodeType
            if (r5 != 0) goto L_0x003a
            int r5 = r0 / 2
            goto L_0x003b
        L_0x003a:
            r5 = r0
        L_0x003b:
            r2.width = r5
            r5 = 1124859904(0x430CLASSNAME, float:140.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.height = r6
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r1 - r5
            int r5 = r5 / r4
            r2.topMargin = r5
            android.widget.FrameLayout r5 = r12.passwordFrameLayout
            r5.setLayoutParams(r2)
            android.widget.FrameLayout r2 = r12.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.height = r1
            int r0 = r0 / r4
            r2.leftMargin = r0
            int r1 = r1 - r1
            r2.topMargin = r1
            r2.width = r0
            android.widget.FrameLayout r0 = r12.numbersFrameLayout
            r0.setLayoutParams(r2)
            goto L_0x00df
        L_0x006c:
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x00a3
            r2 = 1140391936(0x43var_, float:498.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r0 <= r5) goto L_0x0088
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r5
            int r0 = r0 / r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r11 = r2
            r2 = r0
            r0 = r11
            goto L_0x0089
        L_0x0088:
            r2 = 0
        L_0x0089:
            r5 = 1141112832(0x44040000, float:528.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            if (r1 <= r6) goto L_0x00a0
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r1 = r1 - r6
            int r1 = r1 / r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r11 = r2
            r2 = r1
            r1 = r5
            r5 = r11
            goto L_0x00a5
        L_0x00a0:
            r5 = r2
            r2 = 0
            goto L_0x00a5
        L_0x00a3:
            r2 = 0
            r5 = 0
        L_0x00a5:
            android.widget.FrameLayout r6 = r12.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            int r7 = r1 / 3
            r6.height = r7
            r6.width = r0
            r6.topMargin = r2
            r6.leftMargin = r5
            android.widget.FrameLayout r8 = r12.passwordFrameLayout
            java.lang.Integer r9 = java.lang.Integer.valueOf(r2)
            r8.setTag(r9)
            android.widget.FrameLayout r8 = r12.passwordFrameLayout
            r8.setLayoutParams(r6)
            android.widget.FrameLayout r6 = r12.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            int r7 = r7 * 2
            r6.height = r7
            r6.leftMargin = r5
            int r1 = r1 - r7
            int r1 = r1 + r2
            r6.topMargin = r1
            r6.width = r0
            android.widget.FrameLayout r0 = r12.numbersFrameLayout
            r0.setLayoutParams(r6)
            r2 = r6
        L_0x00df:
            int r0 = r2.width
            r1 = 1112014848(0x42480000, float:50.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = r4 * 3
            int r0 = r0 - r4
            int r0 = r0 / 4
            int r2 = r2.height
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = r4 * 4
            int r2 = r2 - r4
            int r2 = r2 / 5
        L_0x00f7:
            r4 = 11
            if (r3 >= r4) goto L_0x01ab
            r5 = 10
            if (r3 != 0) goto L_0x0102
            r4 = 10
            goto L_0x0107
        L_0x0102:
            if (r3 != r5) goto L_0x0105
            goto L_0x0107
        L_0x0105:
            int r4 = r3 + -1
        L_0x0107:
            int r6 = r4 / 3
            int r4 = r4 % 3
            if (r3 >= r5) goto L_0x0153
            java.util.ArrayList<android.widget.TextView> r5 = r12.numberTextViews
            java.lang.Object r5 = r5.get(r3)
            android.widget.TextView r5 = (android.widget.TextView) r5
            java.util.ArrayList<android.widget.TextView> r7 = r12.lettersTextViews
            java.lang.Object r7 = r7.get(r3)
            android.widget.TextView r7 = (android.widget.TextView) r7
            android.view.ViewGroup$LayoutParams r8 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            android.view.ViewGroup$LayoutParams r9 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r10 = r10 + r2
            int r10 = r10 * r6
            int r10 = r10 + r2
            r8.topMargin = r10
            r9.topMargin = r10
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r6 + r0
            int r6 = r6 * r4
            int r6 = r6 + r0
            r8.leftMargin = r6
            r9.leftMargin = r6
            int r4 = r9.topMargin
            r6 = 1109393408(0x42200000, float:40.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            r9.topMargin = r4
            r5.setLayoutParams(r8)
            r7.setLayoutParams(r9)
            goto L_0x0182
        L_0x0153:
            android.widget.ImageView r5 = r12.eraseView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            r8 = r5
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r5 + r2
            int r5 = r5 * r6
            int r5 = r5 + r2
            r6 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r7
            r8.topMargin = r5
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r7 = r7 + r0
            int r7 = r7 * r4
            int r7 = r7 + r0
            r8.leftMargin = r7
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r10 = r5 - r4
            android.widget.ImageView r4 = r12.eraseView
            r4.setLayoutParams(r8)
        L_0x0182:
            java.util.ArrayList<android.widget.FrameLayout> r4 = r12.numberFrameLayouts
            java.lang.Object r4 = r4.get(r3)
            android.widget.FrameLayout r4 = (android.widget.FrameLayout) r4
            android.view.ViewGroup$LayoutParams r5 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r10 = r10 - r6
            r5.topMargin = r10
            int r6 = r8.leftMargin
            r7 = 1103626240(0x41CLASSNAME, float:25.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            r5.leftMargin = r6
            r4.setLayoutParams(r5)
            int r3 = r3 + 1
            goto L_0x00f7
        L_0x01ab:
            super.onMeasure(r13, r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PasscodeView.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View rootView = getRootView();
        int height = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        Rect rect2 = this.rect;
        this.keyboardHeight = height - (rect2.bottom - rect2.top);
        if (SharedConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            int i5 = 0;
            int intValue = this.passwordFrameLayout.getTag() != null ? ((Integer) this.passwordFrameLayout.getTag()).intValue() : 0;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
            int i6 = (intValue + layoutParams.height) - (this.keyboardHeight / 2);
            if (Build.VERSION.SDK_INT >= 21) {
                i5 = AndroidUtilities.statusBarHeight;
            }
            layoutParams.topMargin = i6 - i5;
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            Drawable drawable = this.backgroundDrawable;
            if (drawable == null) {
                super.onDraw(canvas);
            } else if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
            } else {
                float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float measuredHeight = ((float) (getMeasuredHeight() + this.keyboardHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                if (measuredWidth < measuredHeight) {
                    measuredWidth = measuredHeight;
                }
                int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredWidth));
                int ceil2 = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * measuredWidth));
                int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                int measuredHeight2 = ((getMeasuredHeight() - ceil2) + this.keyboardHeight) / 2;
                this.backgroundDrawable.setBounds(measuredWidth2, measuredHeight2, ceil + measuredWidth2, ceil2 + measuredHeight2);
                this.backgroundDrawable.draw(canvas);
            }
        }
    }
}
