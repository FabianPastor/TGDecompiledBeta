package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
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
    private static final int id_fingerprint_imageview = 1001;
    private static final int id_fingerprint_textview = 1000;
    private static final int[] ids = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    /* access modifiers changed from: private */
    public Drawable backgroundDrawable;
    /* access modifiers changed from: private */
    public FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    /* access modifiers changed from: private */
    public Runnable checkRunnable = new Runnable() {
        public void run() {
            PasscodeView.this.checkRetryTextView();
            AndroidUtilities.runOnUIThread(PasscodeView.this.checkRunnable, 100);
        }
    };
    private FrameLayout container;
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    /* access modifiers changed from: private */
    public AlertDialog fingerprintDialog;
    private ImageView fingerprintImage;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private ImageView fingerprintView;
    /* access modifiers changed from: private */
    public RLottieImageView imageView;
    /* access modifiers changed from: private */
    public int imageY;
    /* access modifiers changed from: private */
    public ArrayList<InnerAnimator> innerAnimators = new ArrayList<>();
    /* access modifiers changed from: private */
    public int keyboardHeight = 0;
    private int lastValue;
    private ArrayList<TextView> lettersTextViews;
    private ArrayList<FrameLayout> numberFrameLayouts;
    private ArrayList<TextView> numberTextViews;
    /* access modifiers changed from: private */
    public FrameLayout numbersFrameLayout;
    /* access modifiers changed from: private */
    public TextView passcodeTextView;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    /* access modifiers changed from: private */
    public int[] pos = new int[2];
    private Rect rect = new Rect();
    /* access modifiers changed from: private */
    public TextView retryTextView;
    /* access modifiers changed from: private */
    public boolean selfCancelled;

    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    private static class AnimatingTextView extends FrameLayout {
        private static final String DOT = "•";
        /* access modifiers changed from: private */
        public ArrayList<TextView> characterTextViews = new ArrayList<>(4);
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        /* access modifiers changed from: private */
        public Runnable dotRunnable;
        /* access modifiers changed from: private */
        public ArrayList<TextView> dotTextViews = new ArrayList<>(4);
        private StringBuilder stringBuilder = new StringBuilder(4);

        public AnimatingTextView(Context context) {
            super(context);
            for (int a = 0; a < 4; a++) {
                TextView textView = new TextView(context);
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView, LayoutHelper.createFrame(50, 50, 51));
                this.characterTextViews.add(textView);
                TextView textView2 = new TextView(context);
                textView2.setTextColor(-1);
                textView2.setTextSize(1, 36.0f);
                textView2.setGravity(17);
                textView2.setAlpha(0.0f);
                textView2.setText("•");
                textView2.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView2.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView2, LayoutHelper.createFrame(50, 50, 51));
                this.dotTextViews.add(textView2);
            }
        }

        private int getXForTextView(int pos) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (AndroidUtilities.dp(30.0f) * pos)) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String c) {
            if (this.stringBuilder.length() != 4) {
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList<Animator> animators = new ArrayList<>();
                final int newPos = this.stringBuilder.length();
                this.stringBuilder.append(c);
                TextView textView = this.characterTextViews.get(newPos);
                textView.setText(c);
                textView.setTranslationX((float) getXForTextView(newPos));
                animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                TextView textView2 = this.dotTextViews.get(newPos);
                textView2.setTranslationX((float) getXForTextView(newPos));
                textView2.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (int a = newPos + 1; a < 4; a++) {
                    TextView textView3 = this.characterTextViews.get(a);
                    if (textView3.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView3, View.SCALE_X, new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView3, View.SCALE_Y, new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView3, View.ALPHA, new float[]{0.0f}));
                    }
                    TextView textView4 = this.dotTextViews.get(a);
                    if (textView4.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView4, View.SCALE_X, new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView4, View.SCALE_Y, new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView4, View.ALPHA, new float[]{0.0f}));
                    }
                }
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                AnonymousClass1 r1 = new Runnable() {
                    public void run() {
                        if (AnimatingTextView.this.dotRunnable == this) {
                            ArrayList<Animator> animators = new ArrayList<>();
                            TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(newPos);
                            animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                            TextView textView2 = (TextView) AnimatingTextView.this.dotTextViews.get(newPos);
                            animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{1.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{1.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{1.0f}));
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = new AnimatorSet();
                            AnimatingTextView.this.currentAnimation.setDuration(150);
                            AnimatingTextView.this.currentAnimation.playTogether(animators);
                            AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                                        AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                                    }
                                }
                            });
                            AnimatingTextView.this.currentAnimation.start();
                        }
                    }
                };
                this.dotRunnable = r1;
                AndroidUtilities.runOnUIThread(r1, 1500);
                for (int a2 = 0; a2 < newPos; a2++) {
                    TextView textView5 = this.characterTextViews.get(a2);
                    animators.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_X, new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(textView5, View.SCALE_X, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView5, View.SCALE_Y, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView5, View.ALPHA, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_Y, new float[]{0.0f}));
                    TextView textView6 = this.dotTextViews.get(a2);
                    animators.add(ObjectAnimator.ofFloat(textView6, View.TRANSLATION_X, new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(textView6, View.SCALE_X, new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView6, View.SCALE_Y, new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView6, View.ALPHA, new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView6, View.TRANSLATION_Y, new float[]{0.0f}));
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.setDuration(150);
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
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

        public boolean eraseLastCharacter() {
            if (this.stringBuilder.length() == 0) {
                return false;
            }
            try {
                performHapticFeedback(3);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ArrayList<Animator> animators = new ArrayList<>();
            int deletingPos = this.stringBuilder.length() - 1;
            if (deletingPos != 0) {
                this.stringBuilder.deleteCharAt(deletingPos);
            }
            for (int a = deletingPos; a < 4; a++) {
                TextView textView = this.characterTextViews.get(a);
                if (textView.getAlpha() != 0.0f) {
                    animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, new float[]{(float) getXForTextView(a)}));
                }
                TextView textView2 = this.dotTextViews.get(a);
                if (textView2.getAlpha() != 0.0f) {
                    animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_X, new float[]{(float) getXForTextView(a)}));
                }
            }
            if (deletingPos == 0) {
                this.stringBuilder.deleteCharAt(deletingPos);
            }
            for (int a2 = 0; a2 < deletingPos; a2++) {
                animators.add(ObjectAnimator.ofFloat(this.characterTextViews.get(a2), View.TRANSLATION_X, new float[]{(float) getXForTextView(a2)}));
                animators.add(ObjectAnimator.ofFloat(this.dotTextViews.get(a2), View.TRANSLATION_X, new float[]{(float) getXForTextView(a2)}));
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
            this.currentAnimation.playTogether(animators);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                        AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                    }
                }
            });
            this.currentAnimation.start();
            return true;
        }

        /* access modifiers changed from: private */
        public void eraseAllCharacters(boolean animated) {
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
                if (animated) {
                    ArrayList<Animator> animators = new ArrayList<>();
                    for (int a = 0; a < 4; a++) {
                        TextView textView = this.characterTextViews.get(a);
                        if (textView.getAlpha() != 0.0f) {
                            animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                        }
                        TextView textView2 = this.dotTextViews.get(a);
                        if (textView2.getAlpha() != 0.0f) {
                            animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{0.0f}));
                        }
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.setDuration(150);
                    this.currentAnimation.playTogether(animators);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                                AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                    return;
                }
                for (int a2 = 0; a2 < 4; a2++) {
                    this.characterTextViews.get(a2).setAlpha(0.0f);
                    this.dotTextViews.get(a2).setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
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
            for (int a = 0; a < 4; a++) {
                if (a < this.stringBuilder.length()) {
                    TextView textView = this.characterTextViews.get(a);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(a));
                    TextView textView2 = this.dotTextViews.get(a);
                    textView2.setAlpha(1.0f);
                    textView2.setScaleX(1.0f);
                    textView2.setScaleY(1.0f);
                    textView2.setTranslationY(0.0f);
                    textView2.setTranslationX((float) getXForTextView(a));
                } else {
                    this.characterTextViews.get(a).setAlpha(0.0f);
                    this.dotTextViews.get(a).setAlpha(0.0f);
                }
            }
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    private static class InnerAnimator {
        /* access modifiers changed from: private */
        public AnimatorSet animatorSet;
        /* access modifiers changed from: private */
        public float startRadius;

        private InnerAnimator() {
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PasscodeView(Context context) {
        super(context);
        Context context2 = context;
        char c = 0;
        setWillNotDraw(false);
        setVisibility(8);
        AnonymousClass1 r5 = new FrameLayout(context2) {
            private Paint paint = new Paint();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (PasscodeView.this.backgroundDrawable == null) {
                    super.onDraw(canvas);
                } else if ((PasscodeView.this.backgroundDrawable instanceof MotionBackgroundDrawable) || (PasscodeView.this.backgroundDrawable instanceof ColorDrawable) || (PasscodeView.this.backgroundDrawable instanceof GradientDrawable)) {
                    PasscodeView.this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    PasscodeView.this.backgroundDrawable.draw(canvas);
                } else {
                    float scale = Math.max(((float) getMeasuredWidth()) / ((float) PasscodeView.this.backgroundDrawable.getIntrinsicWidth()), ((float) (getMeasuredHeight() + PasscodeView.this.keyboardHeight)) / ((float) PasscodeView.this.backgroundDrawable.getIntrinsicHeight()));
                    int width = (int) Math.ceil((double) (((float) PasscodeView.this.backgroundDrawable.getIntrinsicWidth()) * scale));
                    int height = (int) Math.ceil((double) (((float) PasscodeView.this.backgroundDrawable.getIntrinsicHeight()) * scale));
                    int x = (getMeasuredWidth() - width) / 2;
                    int y = ((getMeasuredHeight() - height) + PasscodeView.this.keyboardHeight) / 2;
                    PasscodeView.this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                    PasscodeView.this.backgroundDrawable.draw(canvas);
                }
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
            }

            public void setBackgroundColor(int color) {
                this.paint.setColor(color);
            }
        };
        this.backgroundFrameLayout = r5;
        r5.setWillNotDraw(false);
        int i = -1;
        addView(this.backgroundFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        rLottieImageView.setAnimation(NUM, 58, 58);
        this.imageView.setAutoRepeat(false);
        addView(this.imageView, LayoutHelper.createFrame(58, 58, 51));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.passwordFrameLayout = frameLayout;
        this.backgroundFrameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
        TextView textView = new TextView(context2);
        this.passcodeTextView = textView;
        textView.setTextColor(-1);
        this.passcodeTextView.setTextSize(1, 14.0f);
        this.passcodeTextView.setGravity(1);
        this.passwordFrameLayout.addView(this.passcodeTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 74.0f));
        TextView textView2 = new TextView(context2);
        this.retryTextView = textView2;
        textView2.setTextColor(-1);
        this.retryTextView.setTextSize(1, 15.0f);
        this.retryTextView.setGravity(1);
        this.retryTextView.setVisibility(4);
        this.backgroundFrameLayout.addView(this.retryTextView, LayoutHelper.createFrame(-2, -2, 17));
        AnimatingTextView animatingTextView = new AnimatingTextView(context2);
        this.passwordEditText2 = animatingTextView;
        this.passwordFrameLayout.addView(animatingTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 70.0f, 0.0f, 70.0f, 6.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.passwordEditText = editTextBoldCursor;
        float f = 36.0f;
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
        this.passwordFrameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1, -2.0f, 81, 70.0f, 0.0f, 70.0f, 0.0f));
        this.passwordEditText.setOnEditorActionListener(new PasscodeView$$ExternalSyntheticLambda6(this));
        this.passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(PasscodeView.this.backgroundDrawable instanceof MotionBackgroundDrawable)) {
                    return;
                }
                if (count == 0 && after == 1) {
                    ((MotionBackgroundDrawable) PasscodeView.this.backgroundDrawable).switchToNextPosition(true);
                } else if (count == 1 && after == 0) {
                    ((MotionBackgroundDrawable) PasscodeView.this.backgroundDrawable).switchToPrevPosition(true);
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (PasscodeView.this.passwordEditText.length() == 4 && SharedConfig.passcodeType == 0) {
                    PasscodeView.this.processDone(false);
                }
            }
        });
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ImageView imageView2 = new ImageView(context2);
        this.checkImage = imageView2;
        imageView2.setImageResource(NUM);
        this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
        this.checkImage.setBackgroundResource(NUM);
        this.passwordFrameLayout.addView(this.checkImage, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 10.0f, 4.0f));
        this.checkImage.setContentDescription(LocaleController.getString("Done", NUM));
        this.checkImage.setOnClickListener(new PasscodeView$$ExternalSyntheticLambda1(this));
        ImageView imageView3 = new ImageView(context2);
        this.fingerprintImage = imageView3;
        imageView3.setImageResource(NUM);
        this.fingerprintImage.setScaleType(ImageView.ScaleType.CENTER);
        this.fingerprintImage.setBackgroundResource(NUM);
        this.passwordFrameLayout.addView(this.fingerprintImage, LayoutHelper.createFrame(60, 60.0f, 83, 10.0f, 0.0f, 0.0f, 4.0f));
        this.fingerprintImage.setContentDescription(LocaleController.getString("AccDescrFingerprint", NUM));
        this.fingerprintImage.setOnClickListener(new PasscodeView$$ExternalSyntheticLambda2(this));
        FrameLayout lineFrameLayout = new FrameLayout(context2);
        lineFrameLayout.setBackgroundColor(NUM);
        this.passwordFrameLayout.addView(lineFrameLayout, LayoutHelper.createFrame(-1, 1.0f, 83, 20.0f, 0.0f, 20.0f, 0.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.numbersFrameLayout = frameLayout2;
        this.backgroundFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1, 51));
        this.lettersTextViews = new ArrayList<>(10);
        this.numberTextViews = new ArrayList<>(10);
        this.numberFrameLayouts = new ArrayList<>(10);
        int a = 0;
        for (int i2 = 10; a < i2; i2 = 10) {
            TextView textView3 = new TextView(context2);
            textView3.setTextColor(i);
            textView3.setTextSize(1, f);
            textView3.setGravity(17);
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[c] = Integer.valueOf(a);
            textView3.setText(String.format(locale, "%d", objArr));
            this.numbersFrameLayout.addView(textView3, LayoutHelper.createFrame(50, 50, 51));
            textView3.setImportantForAccessibility(2);
            this.numberTextViews.add(textView3);
            TextView textView4 = new TextView(context2);
            textView4.setTextSize(1, 12.0f);
            textView4.setTextColor(Integer.MAX_VALUE);
            textView4.setGravity(17);
            this.numbersFrameLayout.addView(textView4, LayoutHelper.createFrame(50, 50, 51));
            textView4.setImportantForAccessibility(2);
            switch (a) {
                case 0:
                    textView4.setText("+");
                    break;
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
            this.lettersTextViews.add(textView4);
            a++;
            c = 0;
            i = -1;
            f = 36.0f;
        }
        ImageView imageView4 = new ImageView(context2);
        this.eraseView = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseView.setImageResource(NUM);
        this.numbersFrameLayout.addView(this.eraseView, LayoutHelper.createFrame(50, 50, 51));
        ImageView imageView5 = new ImageView(context2);
        this.fingerprintView = imageView5;
        imageView5.setScaleType(ImageView.ScaleType.CENTER);
        this.fingerprintView.setImageResource(NUM);
        this.fingerprintView.setVisibility(8);
        this.numbersFrameLayout.addView(this.fingerprintView, LayoutHelper.createFrame(50, 50, 51));
        checkFingerprintButton();
        for (int a2 = 0; a2 < 12; a2++) {
            FrameLayout frameLayout3 = new FrameLayout(context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setClassName("android.widget.Button");
                }
            };
            frameLayout3.setBackgroundResource(NUM);
            frameLayout3.setTag(Integer.valueOf(a2));
            if (a2 == 11) {
                frameLayout3.setContentDescription(LocaleController.getString("AccDescrFingerprint", NUM));
                setNextFocus(frameLayout3, NUM);
            } else if (a2 == 10) {
                frameLayout3.setOnLongClickListener(new PasscodeView$$ExternalSyntheticLambda4(this));
                frameLayout3.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
                setNextFocus(frameLayout3, NUM);
            } else {
                frameLayout3.setContentDescription(a2 + "");
                if (a2 == 0) {
                    setNextFocus(frameLayout3, NUM);
                } else if (a2 != 9) {
                    setNextFocus(frameLayout3, ids[a2 + 1]);
                } else if (this.fingerprintView.getVisibility() == 0) {
                    setNextFocus(frameLayout3, NUM);
                } else {
                    setNextFocus(frameLayout3, NUM);
                }
            }
            frameLayout3.setId(ids[a2]);
            frameLayout3.setOnClickListener(new PasscodeView$$ExternalSyntheticLambda3(this));
            this.numberFrameLayouts.add(frameLayout3);
        }
        for (int a3 = 11; a3 >= 0; a3--) {
            this.numbersFrameLayout.addView(this.numberFrameLayouts.get(a3), LayoutHelper.createFrame(100, 100, 51));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ boolean m2437lambda$new$0$orgtelegramuiComponentsPasscodeView(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone(false);
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ void m2438lambda$new$1$orgtelegramuiComponentsPasscodeView(View v) {
        processDone(false);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ void m2439lambda$new$2$orgtelegramuiComponentsPasscodeView(View v) {
        checkFingerprint();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ boolean m2440lambda$new$3$orgtelegramuiComponentsPasscodeView(View v) {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        Drawable drawable = this.backgroundDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            ((MotionBackgroundDrawable) drawable).switchToPrevPosition(true);
        }
        return true;
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ void m2441lambda$new$4$orgtelegramuiComponentsPasscodeView(View v) {
        int tag = ((Integer) v.getTag()).intValue();
        boolean erased = false;
        switch (tag) {
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
                erased = this.passwordEditText2.eraseLastCharacter();
                break;
            case 11:
                checkFingerprint();
                break;
        }
        if (this.passwordEditText2.length() == 4) {
            processDone(false);
        }
        if (tag != 11) {
            if (tag != 10) {
                Drawable drawable = this.backgroundDrawable;
                if (drawable instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) drawable).switchToNextPosition(true);
                }
            } else if (erased) {
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) drawable2).switchToPrevPosition(true);
                }
            }
        }
    }

    private void setNextFocus(View view, int nextId) {
        view.setNextFocusForwardId(nextId);
        if (Build.VERSION.SDK_INT >= 22) {
            view.setAccessibilityTraversalBefore(nextId);
        }
    }

    public void setDelegate(PasscodeViewDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void processDone(boolean fingerprint) {
        if (!fingerprint) {
            if (SharedConfig.passcodeRetryInMs <= 0) {
                String password = "";
                if (SharedConfig.passcodeType == 0) {
                    password = this.passwordEditText2.getString();
                } else if (SharedConfig.passcodeType == 1) {
                    password = this.passwordEditText.getText().toString();
                }
                if (password.length() == 0) {
                    onPasscodeError();
                    return;
                } else if (!SharedConfig.checkPasscode(password)) {
                    SharedConfig.increaseBadPasscodeTries();
                    if (SharedConfig.passcodeRetryInMs > 0) {
                        checkRetryTextView();
                    }
                    this.passwordEditText.setText("");
                    this.passwordEditText2.eraseAllCharacters(true);
                    onPasscodeError();
                    Drawable drawable = this.backgroundDrawable;
                    if (drawable instanceof MotionBackgroundDrawable) {
                        ((MotionBackgroundDrawable) drawable).rotatePreview(true);
                        return;
                    }
                    return;
                }
            } else {
                return;
            }
        }
        SharedConfig.badPasscodeTries = 0;
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener((View.OnTouchListener) null);
        PasscodeViewDelegate passcodeViewDelegate = this.delegate;
        if (passcodeViewDelegate != null) {
            passcodeViewDelegate.didAcceptedPassword();
        }
        AndroidUtilities.runOnUIThread(new PasscodeView$$ExternalSyntheticLambda8(this));
    }

    /* renamed from: lambda$processDone$5$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ void m2443lambda$processDone$5$orgtelegramuiComponentsPasscodeView() {
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.setDuration(200);
        AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{(float) AndroidUtilities.dp(0.0f)})});
        AnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                PasscodeView.this.setVisibility(8);
            }
        });
        AnimatorSet.start();
    }

    /* access modifiers changed from: private */
    public void shakeTextView(final float x, final int num) {
        if (num != 6) {
            AnimatorSet AnimatorSet = new AnimatorSet();
            AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.passcodeTextView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(x)})});
            AnimatorSet.setDuration(50);
            AnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PasscodeView passcodeView = PasscodeView.this;
                    int i = num;
                    passcodeView.shakeTextView(i == 5 ? 0.0f : -x, i + 1);
                }
            });
            AnimatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void checkRetryTextView() {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime > SharedConfig.lastUptimeMillis) {
            SharedConfig.passcodeRetryInMs -= currentTime - SharedConfig.lastUptimeMillis;
            if (SharedConfig.passcodeRetryInMs < 0) {
                SharedConfig.passcodeRetryInMs = 0;
            }
        }
        SharedConfig.lastUptimeMillis = currentTime;
        SharedConfig.saveConfig();
        if (SharedConfig.passcodeRetryInMs > 0) {
            double d = (double) SharedConfig.passcodeRetryInMs;
            Double.isNaN(d);
            int value = Math.max(1, (int) Math.ceil(d / 1000.0d));
            if (value != this.lastValue) {
                this.retryTextView.setText(LocaleController.formatString("TooManyTries", NUM, LocaleController.formatPluralString("Seconds", value)));
                this.lastValue = value;
            }
            if (this.retryTextView.getVisibility() != 0) {
                this.retryTextView.setVisibility(0);
                this.passwordFrameLayout.setVisibility(4);
                if (this.numbersFrameLayout.getVisibility() == 0) {
                    this.numbersFrameLayout.setVisibility(4);
                }
                AndroidUtilities.hideKeyboard(this.passwordEditText);
            }
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 100);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        if (this.passwordFrameLayout.getVisibility() != 0) {
            this.retryTextView.setVisibility(4);
            this.passwordFrameLayout.setVisibility(0);
            if (SharedConfig.passcodeType == 0) {
                this.numbersFrameLayout.setVisibility(0);
            } else if (SharedConfig.passcodeType == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        }
    }

    private void onPasscodeError() {
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
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
                AndroidUtilities.runOnUIThread(new PasscodeView$$ExternalSyntheticLambda7(this), 200);
            }
            checkFingerprint();
        }
    }

    /* renamed from: lambda$onResume$6$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ void m2442lambda$onResume$6$orgtelegramuiComponentsPasscodeView() {
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
        if (Build.VERSION.SDK_INT >= 23 && ((Activity) getContext()) != null && this.fingerprintView.getVisibility() == 0 && !ApplicationLoader.mainInterfacePaused) {
            try {
                AlertDialog alertDialog = this.fingerprintDialog;
                if (alertDialog != null && alertDialog.isShowing()) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    TextView fingerprintTextView = new TextView(getContext());
                    fingerprintTextView.setId(1000);
                    fingerprintTextView.setTextAppearance(16974344);
                    fingerprintTextView.setTextColor(Theme.getColor("dialogTextBlack"));
                    fingerprintTextView.setText(LocaleController.getString("FingerprintInfo", NUM));
                    relativeLayout.addView(fingerprintTextView);
                    RelativeLayout.LayoutParams layoutParams = LayoutHelper.createRelative(-2, -2);
                    layoutParams.addRule(10);
                    layoutParams.addRule(20);
                    fingerprintTextView.setLayoutParams(layoutParams);
                    ImageView imageView2 = new ImageView(getContext());
                    this.fingerprintImageView = imageView2;
                    imageView2.setImageResource(NUM);
                    this.fingerprintImageView.setId(1001);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, 1000));
                    TextView textView = new TextView(getContext());
                    this.fingerprintStatusTextView = textView;
                    textView.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", NUM));
                    this.fingerprintStatusTextView.setTextAppearance(16974320);
                    this.fingerprintStatusTextView.setTextColor(Theme.getColor("dialogTextBlack") & NUM);
                    relativeLayout.addView(this.fingerprintStatusTextView);
                    RelativeLayout.LayoutParams layoutParams2 = LayoutHelper.createRelative(-2, -2);
                    layoutParams2.setMarginStart(AndroidUtilities.dp(16.0f));
                    layoutParams2.addRule(8, 1001);
                    layoutParams2.addRule(6, 1001);
                    layoutParams2.addRule(17, 1001);
                    this.fingerprintStatusTextView.setLayoutParams(layoutParams2);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    builder.setOnDismissListener(new PasscodeView$$ExternalSyntheticLambda0(this));
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
                    fingerprintManager.authenticate((FingerprintManagerCompat.CryptoObject) null, 0, cancellationSignal2, new FingerprintManagerCompat.AuthenticationCallback() {
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            if (errMsgId == 10) {
                                try {
                                    if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                        PasscodeView.this.fingerprintDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                                AlertDialog unused = PasscodeView.this.fingerprintDialog = null;
                            } else if (!PasscodeView.this.selfCancelled && errMsgId != 5) {
                                PasscodeView.this.showFingerprintError(errString);
                            }
                        }

                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            PasscodeView.this.showFingerprintError(helpString);
                        }

                        public void onAuthenticationFailed() {
                            PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", NUM));
                        }

                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
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
            } catch (Throwable th) {
            }
        }
    }

    /* renamed from: lambda$checkFingerprint$7$org-telegram-ui-Components-PasscodeView  reason: not valid java name */
    public /* synthetic */ void m2436x228947ec(DialogInterface dialog) {
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

    public void onShow(boolean fingerprint, boolean animated) {
        onShow(fingerprint, animated, -1, -1, (Runnable) null, (Runnable) null);
    }

    private void checkFingerprintButton() {
        Activity parentActivity = (Activity) getContext();
        if (Build.VERSION.SDK_INT < 23 || parentActivity == null || !SharedConfig.useFingerprint) {
            this.fingerprintView.setVisibility(8);
        } else {
            try {
                AlertDialog alertDialog = this.fingerprintDialog;
                if (alertDialog != null && alertDialog.isShowing()) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (!fingerprintManager.isHardwareDetected() || !fingerprintManager.hasEnrolledFingerprints()) {
                    this.fingerprintView.setVisibility(8);
                } else {
                    this.fingerprintView.setVisibility(0);
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
                this.fingerprintView.setVisibility(8);
            }
        }
        if (SharedConfig.passcodeType == 1) {
            this.fingerprintImage.setVisibility(this.fingerprintView.getVisibility());
        }
        if (this.numberFrameLayouts.size() >= 11) {
            this.numberFrameLayouts.get(11).setVisibility(this.fingerprintView.getVisibility());
        }
    }

    public void onShow(boolean fingerprint, boolean animated, int x, int y, Runnable onShow, Runnable onStart) {
        View currentFocus;
        EditTextBoldCursor editTextBoldCursor;
        final Runnable runnable = onShow;
        checkFingerprintButton();
        checkRetryTextView();
        Activity parentActivity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (!(animated || this.retryTextView.getVisibility() == 0 || (editTextBoldCursor = this.passwordEditText) == null)) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (!(parentActivity == null || (currentFocus = parentActivity.getCurrentFocus()) == null)) {
            currentFocus.clearFocus();
            AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
        }
        if (fingerprint && this.retryTextView.getVisibility() != 0) {
            checkFingerprint();
        }
        if (getVisibility() != 0) {
            setTranslationY(0.0f);
            this.backgroundDrawable = null;
            if (Theme.getCachedWallpaper() instanceof MotionBackgroundDrawable) {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                this.backgroundFrameLayout.setBackgroundColor(-NUM);
            } else if (Theme.isCustomTheme() && !"CJz3BZ6YGEYBAAAABboWp6SAv04".equals(Theme.getSelectedBackgroundSlug()) && !"qeZWES8rGVIEAAAARfWlK1lnfiI".equals(Theme.getSelectedBackgroundSlug())) {
                BackgroundGradientDrawable currentGradientWallpaper = Theme.getCurrentGradientWallpaper();
                this.backgroundDrawable = currentGradientWallpaper;
                if (currentGradientWallpaper == null) {
                    this.backgroundDrawable = Theme.getCachedWallpaper();
                }
                if (this.backgroundDrawable instanceof BackgroundGradientDrawable) {
                    this.backgroundFrameLayout.setBackgroundColor(NUM);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(-NUM);
                }
            } else if ("d".equals(Theme.getSelectedBackgroundSlug()) || Theme.isPatternWallpaper()) {
                this.backgroundFrameLayout.setBackgroundColor(-11436898);
            } else {
                Drawable cachedWallpaper = Theme.getCachedWallpaper();
                this.backgroundDrawable = cachedWallpaper;
                if (cachedWallpaper instanceof BackgroundGradientDrawable) {
                    this.backgroundFrameLayout.setBackgroundColor(NUM);
                } else if (cachedWallpaper != null) {
                    this.backgroundFrameLayout.setBackgroundColor(-NUM);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(-11436898);
                }
            }
            Drawable drawable = this.backgroundDrawable;
            if (drawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable drawable2 = (MotionBackgroundDrawable) drawable;
                int[] colors = drawable2.getColors();
                this.backgroundDrawable = new MotionBackgroundDrawable(colors[0], colors[1], colors[2], colors[3], false);
                if (!drawable2.hasPattern() || drawable2.getIntensity() >= 0) {
                    this.backgroundFrameLayout.setBackgroundColor(NUM);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(NUM);
                }
                ((MotionBackgroundDrawable) this.backgroundDrawable).setParentView(this.backgroundFrameLayout);
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourTelegramPasscode", NUM));
            if (SharedConfig.passcodeType == 0) {
                if (this.retryTextView.getVisibility() != 0) {
                    this.numbersFrameLayout.setVisibility(0);
                }
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
                this.fingerprintImage.setVisibility(8);
            } else if (SharedConfig.passcodeType == 1) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setInputType(129);
                this.numbersFrameLayout.setVisibility(8);
                this.passwordEditText.setFocusable(true);
                this.passwordEditText.setFocusableInTouchMode(true);
                this.passwordEditText.setVisibility(0);
                this.passwordEditText2.setVisibility(8);
                this.checkImage.setVisibility(0);
                this.fingerprintImage.setVisibility(this.fingerprintView.getVisibility());
            }
            setVisibility(0);
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setText("");
            this.passwordEditText2.eraseAllCharacters(false);
            if (animated) {
                setAlpha(0.0f);
                final int i = x;
                final int i2 = y;
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        float ix;
                        View child;
                        double d3;
                        double d4;
                        double d2;
                        int h;
                        final AnimatorSet animatorSetInner;
                        PasscodeView.this.setAlpha(1.0f);
                        PasscodeView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        PasscodeView.this.imageView.setProgress(0.0f);
                        PasscodeView.this.imageView.playAnimation();
                        AndroidUtilities.runOnUIThread(new PasscodeView$9$$ExternalSyntheticLambda1(this), 350);
                        AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList<Animator> animators = new ArrayList<>();
                        int w = AndroidUtilities.displaySize.x;
                        int h2 = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        if (Build.VERSION.SDK_INT >= 21) {
                            int i = i;
                            int i2 = (w - i) * (w - i);
                            int i3 = i2;
                            double d1 = Math.sqrt((double) (i2 + ((h2 - i3) * (h2 - i3))));
                            int i4 = i;
                            int i5 = i2;
                            double d22 = Math.sqrt((double) ((i4 * i4) + ((h2 - i5) * (h2 - i5))));
                            int i6 = i;
                            int i7 = i2;
                            double d32 = Math.sqrt((double) ((i6 * i6) + (i7 * i7)));
                            int i8 = i;
                            int i9 = (w - i8) * (w - i8);
                            int i10 = i2;
                            double d42 = Math.sqrt((double) (i9 + (i10 * i10)));
                            double finalRadius = Math.max(Math.max(Math.max(d1, d22), d32), d42);
                            PasscodeView.this.innerAnimators.clear();
                            int a = -1;
                            double d = d1;
                            int N = PasscodeView.this.numbersFrameLayout.getChildCount();
                            while (a < N) {
                                if (a == -1) {
                                    child = PasscodeView.this.passcodeTextView;
                                } else {
                                    child = PasscodeView.this.numbersFrameLayout.getChildAt(a);
                                }
                                int N2 = N;
                                if ((child instanceof TextView) != 0 || (child instanceof ImageView)) {
                                    child.setScaleX(0.7f);
                                    child.setScaleY(0.7f);
                                    child.setAlpha(0.0f);
                                    h = h2;
                                    InnerAnimator innerAnimator = new InnerAnimator();
                                    child.getLocationInWindow(PasscodeView.this.pos);
                                    int buttonX = PasscodeView.this.pos[0] + (child.getMeasuredWidth() / 2);
                                    d2 = d22;
                                    double d43 = d42;
                                    int buttonY = PasscodeView.this.pos[1] + (child.getMeasuredHeight() / 2);
                                    int i11 = i;
                                    int i12 = (i11 - buttonX) * (i11 - buttonX);
                                    int i13 = i2;
                                    int i14 = i12 + ((i13 - buttonY) * (i13 - buttonY));
                                    int i15 = buttonX;
                                    int i16 = buttonY;
                                    float unused = innerAnimator.startRadius = ((float) Math.sqrt((double) i14)) - ((float) AndroidUtilities.dp(40.0f));
                                    if (a != -1) {
                                        animatorSetInner = new AnimatorSet();
                                        d4 = d43;
                                        animatorSetInner.playTogether(new Animator[]{ObjectAnimator.ofFloat(child, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(child, View.SCALE_Y, new float[]{1.0f})});
                                        animatorSetInner.setDuration(140);
                                        animatorSetInner.setInterpolator(new DecelerateInterpolator());
                                    } else {
                                        d4 = d43;
                                        animatorSetInner = null;
                                    }
                                    AnimatorSet unused2 = innerAnimator.animatorSet = new AnimatorSet();
                                    AnimatorSet access$2200 = innerAnimator.animatorSet;
                                    Animator[] animatorArr = new Animator[3];
                                    Property property = View.SCALE_X;
                                    d3 = d32;
                                    float[] fArr = new float[2];
                                    float f = 0.9f;
                                    fArr[0] = a == -1 ? 0.9f : 0.6f;
                                    float f2 = 1.04f;
                                    fArr[1] = a == -1 ? 1.0f : 1.04f;
                                    animatorArr[0] = ObjectAnimator.ofFloat(child, property, fArr);
                                    Property property2 = View.SCALE_Y;
                                    float[] fArr2 = new float[2];
                                    if (a != -1) {
                                        f = 0.6f;
                                    }
                                    fArr2[0] = f;
                                    if (a == -1) {
                                        f2 = 1.0f;
                                    }
                                    fArr2[1] = f2;
                                    animatorArr[1] = ObjectAnimator.ofFloat(child, property2, fArr2);
                                    animatorArr[2] = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                                    access$2200.playTogether(animatorArr);
                                    innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            AnimatorSet animatorSet = animatorSetInner;
                                            if (animatorSet != null) {
                                                animatorSet.start();
                                            }
                                        }
                                    });
                                    innerAnimator.animatorSet.setDuration(a == -1 ? 232 : 200);
                                    innerAnimator.animatorSet.setInterpolator(new DecelerateInterpolator());
                                    PasscodeView.this.innerAnimators.add(innerAnimator);
                                } else {
                                    h = h2;
                                    d2 = d22;
                                    d4 = d42;
                                    d3 = d32;
                                }
                                a++;
                                N = N2;
                                h2 = h;
                                d22 = d2;
                                d42 = d4;
                                d32 = d3;
                            }
                            int i17 = h2;
                            double d5 = d22;
                            double d6 = d42;
                            double d7 = d32;
                            animators.add(ViewAnimationUtils.createCircularReveal(PasscodeView.this.backgroundFrameLayout, i, i2, 0.0f, (float) finalRadius));
                            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                            animators.add(animator);
                            animator.addUpdateListener(new PasscodeView$9$$ExternalSyntheticLambda0(this, finalRadius));
                            animatorSet.setInterpolator(Easings.easeInOutQuad);
                            animatorSet.setDuration(498);
                        } else {
                            animators.add(ObjectAnimator.ofFloat(PasscodeView.this.backgroundFrameLayout, View.ALPHA, new float[]{0.0f, 1.0f}));
                            animatorSet.setDuration(350);
                        }
                        animatorSet.playTogether(animators);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (runnable != null) {
                                    runnable.run();
                                }
                                if (SharedConfig.passcodeType == 1 && PasscodeView.this.retryTextView.getVisibility() != 0 && PasscodeView.this.passwordEditText != null) {
                                    PasscodeView.this.passwordEditText.requestFocus();
                                    AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
                                }
                            }
                        });
                        animatorSet.start();
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorSet2.setDuration(332);
                        if (AndroidUtilities.isTablet() || PasscodeView.this.getContext().getResources().getConfiguration().orientation != 2) {
                            ix = (float) ((w / 2) - AndroidUtilities.dp(29.0f));
                        } else {
                            ix = (float) (((SharedConfig.passcodeType == 0 ? w / 2 : w) / 2) - AndroidUtilities.dp(30.0f));
                        }
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.TRANSLATION_X, new float[]{(float) (i - AndroidUtilities.dp(29.0f)), ix}), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.TRANSLATION_Y, new float[]{(float) (i2 - AndroidUtilities.dp(29.0f)), (float) PasscodeView.this.imageY}), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.SCALE_X, new float[]{0.5f, 1.0f}), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.SCALE_Y, new float[]{0.5f, 1.0f})});
                        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                        animatorSet2.start();
                    }

                    /* renamed from: lambda$onGlobalLayout$0$org-telegram-ui-Components-PasscodeView$9  reason: not valid java name */
                    public /* synthetic */ void m2444x50b696ea() {
                        PasscodeView.this.imageView.performHapticFeedback(3, 2);
                    }

                    /* renamed from: lambda$onGlobalLayout$1$org-telegram-ui-Components-PasscodeView$9  reason: not valid java name */
                    public /* synthetic */ void m2445x35var_ab(double finalRadius, ValueAnimator animation) {
                        double animatedFraction = (double) animation.getAnimatedFraction();
                        Double.isNaN(animatedFraction);
                        double rad = animatedFraction * finalRadius;
                        int a = 0;
                        while (a < PasscodeView.this.innerAnimators.size()) {
                            InnerAnimator innerAnimator = (InnerAnimator) PasscodeView.this.innerAnimators.get(a);
                            if (((double) innerAnimator.startRadius) <= rad) {
                                innerAnimator.animatorSet.start();
                                PasscodeView.this.innerAnimators.remove(a);
                                a--;
                            }
                            a++;
                        }
                    }
                });
                requestLayout();
            } else {
                int i3 = x;
                int i4 = y;
                setAlpha(1.0f);
                this.imageView.setScaleX(1.0f);
                this.imageView.setScaleY(1.0f);
                this.imageView.stopAnimation();
                this.imageView.getAnimatedDrawable().setCurrentFrame(38, false);
                if (runnable != null) {
                    onShow.run();
                }
            }
            setOnTouchListener(PasscodeView$$ExternalSyntheticLambda5.INSTANCE);
        }
    }

    static /* synthetic */ boolean lambda$onShow$8(View v, MotionEvent event) {
        return true;
    }

    /* access modifiers changed from: private */
    public void showFingerprintError(CharSequence error) {
        this.fingerprintImageView.setImageResource(NUM);
        this.fingerprintStatusTextView.setText(error);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    /* JADX WARNING: type inference failed for: r9v3, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r9v6, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r14v15, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r9v18, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r6v26, types: [android.view.ViewGroup$LayoutParams] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            int r1 = android.view.View.MeasureSpec.getSize(r18)
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r2.y
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            r5 = 0
            if (r3 < r4) goto L_0x0013
            r3 = 0
            goto L_0x0015
        L_0x0013:
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0015:
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            r6 = 1105723392(0x41e80000, float:29.0)
            r7 = 1109393408(0x42200000, float:40.0)
            r8 = 2
            if (r3 != 0) goto L_0x009f
            android.content.Context r3 = r17.getContext()
            android.content.res.Resources r3 = r3.getResources()
            android.content.res.Configuration r3 = r3.getConfiguration()
            int r3 = r3.orientation
            if (r3 != r8) goto L_0x009f
            org.telegram.ui.Components.RLottieImageView r3 = r0.imageView
            int r9 = org.telegram.messenger.SharedConfig.passcodeType
            if (r9 != 0) goto L_0x003a
            int r9 = r1 / 2
            goto L_0x003b
        L_0x003a:
            r9 = r1
        L_0x003b:
            int r9 = r9 / r8
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r9 = r9 - r6
            float r6 = (float) r9
            r3.setTranslationX(r6)
            android.widget.FrameLayout r3 = r0.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r6 = org.telegram.messenger.SharedConfig.passcodeType
            if (r6 != 0) goto L_0x0054
            int r6 = r1 / 2
            goto L_0x0055
        L_0x0054:
            r6 = r1
        L_0x0055:
            r3.width = r6
            r6 = 1124859904(0x430CLASSNAME, float:140.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.height = r9
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r2 - r6
            int r6 = r6 / r8
            int r8 = org.telegram.messenger.SharedConfig.passcodeType
            if (r8 != 0) goto L_0x006f
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x0070
        L_0x006f:
            r8 = 0
        L_0x0070:
            int r6 = r6 + r8
            r3.topMargin = r6
            android.widget.FrameLayout r6 = r0.passwordFrameLayout
            r6.setLayoutParams(r3)
            android.widget.FrameLayout r6 = r0.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            r3 = r6
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r3.height = r2
            int r6 = r1 / 2
            r3.leftMargin = r6
            int r6 = r3.height
            int r6 = r2 - r6
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r4) goto L_0x0091
            int r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0091:
            int r6 = r6 + r5
            r3.topMargin = r6
            int r4 = r1 / 2
            r3.width = r4
            android.widget.FrameLayout r4 = r0.numbersFrameLayout
            r4.setLayoutParams(r3)
            goto L_0x0145
        L_0x009f:
            org.telegram.ui.Components.RLottieImageView r3 = r0.imageView
            int r4 = r1 / 2
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            float r4 = (float) r4
            r3.setTranslationX(r4)
            r3 = 0
            r4 = 0
            boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r6 == 0) goto L_0x00dc
            r6 = 1140391936(0x43var_, float:498.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            if (r1 <= r9) goto L_0x00c8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r9 = r1 - r9
            int r4 = r9 / 2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x00c8:
            r6 = 1141112832(0x44040000, float:528.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            if (r2 <= r9) goto L_0x00dc
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r9 = r2 - r9
            int r3 = r9 / 2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x00dc:
            android.widget.FrameLayout r6 = r0.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            int r9 = r2 / 3
            int r10 = org.telegram.messenger.SharedConfig.passcodeType
            if (r10 != 0) goto L_0x00ef
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x00f0
        L_0x00ef:
            r10 = 0
        L_0x00f0:
            int r9 = r9 + r10
            r6.height = r9
            r6.width = r1
            r6.topMargin = r3
            r6.leftMargin = r4
            android.widget.FrameLayout r9 = r0.passwordFrameLayout
            java.lang.Integer r10 = java.lang.Integer.valueOf(r3)
            r9.setTag(r10)
            android.widget.FrameLayout r9 = r0.passwordFrameLayout
            r9.setLayoutParams(r6)
            android.widget.FrameLayout r9 = r0.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            r6 = r9
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            int r9 = r2 / 3
            int r9 = r9 * 2
            r6.height = r9
            r6.leftMargin = r4
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x012d
            int r5 = r6.height
            int r5 = r2 - r5
            int r5 = r5 + r3
            r8 = 1101004800(0x41a00000, float:20.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 + r8
            r6.topMargin = r5
            goto L_0x013d
        L_0x012d:
            int r8 = r6.height
            int r8 = r2 - r8
            int r8 = r8 + r3
            int r9 = org.telegram.messenger.SharedConfig.passcodeType
            if (r9 != 0) goto L_0x013a
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
        L_0x013a:
            int r8 = r8 + r5
            r6.topMargin = r8
        L_0x013d:
            r6.width = r1
            android.widget.FrameLayout r5 = r0.numbersFrameLayout
            r5.setLayoutParams(r6)
            r3 = r6
        L_0x0145:
            int r4 = r3.width
            r5 = 1112014848(0x42480000, float:50.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r6 = r6 * 3
            int r4 = r4 - r6
            int r4 = r4 / 4
            int r6 = r3.height
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r8 = r8 * 4
            int r6 = r6 - r8
            int r6 = r6 / 5
            r8 = 0
        L_0x015e:
            r9 = 12
            if (r8 >= r9) goto L_0x0259
            r9 = 10
            if (r8 != 0) goto L_0x0169
            r10 = 10
            goto L_0x0177
        L_0x0169:
            if (r8 != r9) goto L_0x016e
            r10 = 11
            goto L_0x0177
        L_0x016e:
            r10 = 11
            if (r8 != r10) goto L_0x0175
            r10 = 9
            goto L_0x0177
        L_0x0175:
            int r10 = r8 + -1
        L_0x0177:
            int r11 = r10 / 3
            int r12 = r10 % 3
            if (r8 >= r9) goto L_0x01c7
            java.util.ArrayList<android.widget.TextView> r9 = r0.numberTextViews
            java.lang.Object r9 = r9.get(r8)
            android.widget.TextView r9 = (android.widget.TextView) r9
            java.util.ArrayList<android.widget.TextView> r13 = r0.lettersTextViews
            java.lang.Object r13 = r13.get(r8)
            android.widget.TextView r13 = (android.widget.TextView) r13
            android.view.ViewGroup$LayoutParams r14 = r9.getLayoutParams()
            r3 = r14
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            android.view.ViewGroup$LayoutParams r14 = r13.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r15 = r15 + r6
            int r15 = r15 * r11
            int r15 = r15 + r6
            r3.topMargin = r15
            r14.topMargin = r15
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r16 = r4 + r16
            int r16 = r16 * r12
            int r5 = r4 + r16
            r3.leftMargin = r5
            r14.leftMargin = r5
            int r5 = r14.topMargin
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 + r16
            r14.topMargin = r5
            r9.setLayoutParams(r3)
            r13.setLayoutParams(r14)
            r9 = 1112014848(0x42480000, float:50.0)
            goto L_0x022c
        L_0x01c7:
            r5 = 1090519040(0x41000000, float:8.0)
            if (r8 != r9) goto L_0x01fd
            android.widget.ImageView r9 = r0.eraseView
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            r3 = r9
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r9 = 1112014848(0x42480000, float:50.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r13 = r13 + r6
            int r13 = r13 * r11
            int r13 = r13 + r6
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r13 = r13 + r14
            r3.topMargin = r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r4
            int r14 = r14 * r12
            int r14 = r14 + r4
            r3.leftMargin = r14
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r15 = r13 - r5
            android.widget.ImageView r5 = r0.eraseView
            r5.setLayoutParams(r3)
            r9 = 1112014848(0x42480000, float:50.0)
            goto L_0x022c
        L_0x01fd:
            android.widget.ImageView r9 = r0.fingerprintView
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            r3 = r9
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r9 = 1112014848(0x42480000, float:50.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r13 = r13 + r6
            int r13 = r13 * r11
            int r13 = r13 + r6
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r13 = r13 + r14
            r3.topMargin = r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r4
            int r14 = r14 * r12
            int r14 = r14 + r4
            r3.leftMargin = r14
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r15 = r13 - r5
            android.widget.ImageView r5 = r0.fingerprintView
            r5.setLayoutParams(r3)
        L_0x022c:
            java.util.ArrayList<android.widget.FrameLayout> r5 = r0.numberFrameLayouts
            java.lang.Object r5 = r5.get(r8)
            android.widget.FrameLayout r5 = (android.widget.FrameLayout) r5
            android.view.ViewGroup$LayoutParams r13 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            r14 = 1099431936(0x41880000, float:17.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r15 - r14
            r13.topMargin = r14
            int r14 = r3.leftMargin
            r16 = 1103626240(0x41CLASSNAME, float:25.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r14 = r14 - r16
            r13.leftMargin = r14
            r5.setLayoutParams(r13)
            int r8 = r8 + 1
            r5 = 1112014848(0x42480000, float:50.0)
            goto L_0x015e
        L_0x0259:
            super.onMeasure(r18, r19)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PasscodeView.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View rootView = getRootView();
        int usableViewHeight = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        this.keyboardHeight = usableViewHeight - (this.rect.bottom - this.rect.top);
        if (SharedConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            int t = 0;
            if (this.passwordFrameLayout.getTag() != null) {
                t = ((Integer) this.passwordFrameLayout.getTag()).intValue();
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.topMargin = ((layoutParams.height + t) - (this.keyboardHeight / 2)) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(changed, left, top, right, bottom);
        this.passcodeTextView.getLocationInWindow(this.pos);
        if (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) {
            RLottieImageView rLottieImageView = this.imageView;
            int dp = this.pos[1] - AndroidUtilities.dp(100.0f);
            this.imageY = dp;
            rLottieImageView.setTranslationY((float) dp);
            return;
        }
        RLottieImageView rLottieImageView2 = this.imageView;
        int dp2 = this.pos[1] - AndroidUtilities.dp(100.0f);
        this.imageY = dp2;
        rLottieImageView2.setTranslationY((float) dp2);
    }
}
