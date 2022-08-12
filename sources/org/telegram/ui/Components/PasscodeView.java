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
import android.graphics.Point;
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
import org.telegram.messenger.FingerprintController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class PasscodeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int[] ids = {R.id.passcode_btn_0, R.id.passcode_btn_1, R.id.passcode_btn_2, R.id.passcode_btn_3, R.id.passcode_btn_4, R.id.passcode_btn_5, R.id.passcode_btn_6, R.id.passcode_btn_7, R.id.passcode_btn_8, R.id.passcode_btn_9, R.id.passcode_btn_backspace, R.id.passcode_btn_fingerprint};
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onShow$8(View view, MotionEvent motionEvent) {
        return true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didGenerateFingerprintKeyPair) {
            checkFingerprintButton();
            if (objArr[0].booleanValue() && SharedConfig.appLocked) {
                checkFingerprint();
            }
        }
    }

    private static class AnimatingTextView extends FrameLayout {
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
            for (int i = 0; i < 4; i++) {
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

        public boolean eraseLastCharacter() {
            if (this.stringBuilder.length() == 0) {
                return false;
            }
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
            return true;
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
                    float max = Math.max(((float) getMeasuredWidth()) / ((float) PasscodeView.this.backgroundDrawable.getIntrinsicWidth()), ((float) (getMeasuredHeight() + PasscodeView.this.keyboardHeight)) / ((float) PasscodeView.this.backgroundDrawable.getIntrinsicHeight()));
                    int ceil = (int) Math.ceil((double) (((float) PasscodeView.this.backgroundDrawable.getIntrinsicWidth()) * max));
                    int ceil2 = (int) Math.ceil((double) (((float) PasscodeView.this.backgroundDrawable.getIntrinsicHeight()) * max));
                    int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                    int measuredHeight = ((getMeasuredHeight() - ceil2) + PasscodeView.this.keyboardHeight) / 2;
                    PasscodeView.this.backgroundDrawable.setBounds(measuredWidth, measuredHeight, ceil + measuredWidth, ceil2 + measuredHeight);
                    PasscodeView.this.backgroundDrawable.draw(canvas);
                }
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
            }

            public void setBackgroundColor(int i) {
                this.paint.setColor(i);
            }
        };
        this.backgroundFrameLayout = r5;
        r5.setWillNotDraw(false);
        int i = -1;
        addView(this.backgroundFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        rLottieImageView.setAnimation(R.raw.passcode_lock_close, 58, 58);
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
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!(PasscodeView.this.backgroundDrawable instanceof MotionBackgroundDrawable)) {
                    return;
                }
                if (i2 == 0 && i3 == 1) {
                    ((MotionBackgroundDrawable) PasscodeView.this.backgroundDrawable).switchToNextPosition(true);
                } else if (i2 == 1 && i3 == 0) {
                    ((MotionBackgroundDrawable) PasscodeView.this.backgroundDrawable).switchToPrevPosition(true);
                }
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
        imageView2.setImageResource(R.drawable.passcode_check);
        this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.checkImage;
        int i2 = R.drawable.bar_selector_lock;
        imageView3.setBackgroundResource(i2);
        this.passwordFrameLayout.addView(this.checkImage, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 10.0f, 4.0f));
        this.checkImage.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.checkImage.setOnClickListener(new PasscodeView$$ExternalSyntheticLambda3(this));
        ImageView imageView4 = new ImageView(context2);
        this.fingerprintImage = imageView4;
        imageView4.setImageResource(R.drawable.fingerprint);
        this.fingerprintImage.setScaleType(ImageView.ScaleType.CENTER);
        this.fingerprintImage.setBackgroundResource(i2);
        this.passwordFrameLayout.addView(this.fingerprintImage, LayoutHelper.createFrame(60, 60.0f, 83, 10.0f, 0.0f, 0.0f, 4.0f));
        this.fingerprintImage.setContentDescription(LocaleController.getString("AccDescrFingerprint", R.string.AccDescrFingerprint));
        this.fingerprintImage.setOnClickListener(new PasscodeView$$ExternalSyntheticLambda2(this));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        frameLayout2.setBackgroundColor(NUM);
        this.passwordFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 1.0f, 83, 20.0f, 0.0f, 20.0f, 0.0f));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.numbersFrameLayout = frameLayout3;
        this.backgroundFrameLayout.addView(frameLayout3, LayoutHelper.createFrame(-1, -1, 51));
        this.lettersTextViews = new ArrayList<>(10);
        this.numberTextViews = new ArrayList<>(10);
        this.numberFrameLayouts = new ArrayList<>(10);
        int i3 = 0;
        while (i3 < 10) {
            TextView textView3 = new TextView(context2);
            textView3.setTextColor(i);
            textView3.setTextSize(1, 36.0f);
            textView3.setGravity(17);
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[c] = Integer.valueOf(i3);
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
            if (i3 != 0) {
                switch (i3) {
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
            i3++;
            c = 0;
            i = -1;
        }
        ImageView imageView5 = new ImageView(context2);
        this.eraseView = imageView5;
        imageView5.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseView.setImageResource(R.drawable.passcode_delete);
        this.numbersFrameLayout.addView(this.eraseView, LayoutHelper.createFrame(50, 50, 51));
        ImageView imageView6 = new ImageView(context2);
        this.fingerprintView = imageView6;
        imageView6.setScaleType(ImageView.ScaleType.CENTER);
        this.fingerprintView.setImageResource(R.drawable.fingerprint);
        this.fingerprintView.setVisibility(8);
        this.numbersFrameLayout.addView(this.fingerprintView, LayoutHelper.createFrame(50, 50, 51));
        checkFingerprintButton();
        int i4 = 0;
        while (true) {
            if (i4 < 12) {
                AnonymousClass4 r3 = new FrameLayout(this, context2) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setClassName("android.widget.Button");
                    }
                };
                r3.setBackgroundResource(R.drawable.bar_selector_lock);
                r3.setTag(Integer.valueOf(i4));
                if (i4 == 11) {
                    r3.setContentDescription(LocaleController.getString("AccDescrFingerprint", R.string.AccDescrFingerprint));
                    setNextFocus(r3, R.id.passcode_btn_0);
                } else if (i4 == 10) {
                    r3.setOnLongClickListener(new PasscodeView$$ExternalSyntheticLambda4(this));
                    r3.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
                    setNextFocus(r3, R.id.passcode_btn_1);
                } else {
                    r3.setContentDescription(i4 + "");
                    if (i4 == 0) {
                        setNextFocus(r3, R.id.passcode_btn_backspace);
                    } else if (i4 != 9) {
                        setNextFocus(r3, ids[i4 + 1]);
                    } else if (this.fingerprintView.getVisibility() == 0) {
                        setNextFocus(r3, R.id.passcode_btn_fingerprint);
                    } else {
                        setNextFocus(r3, R.id.passcode_btn_0);
                    }
                }
                r3.setId(ids[i4]);
                r3.setOnClickListener(new PasscodeView$$ExternalSyntheticLambda1(this));
                this.numberFrameLayouts.add(r3);
                i4++;
            } else {
                for (int i5 = 11; i5 >= 0; i5--) {
                    this.numbersFrameLayout.addView(this.numberFrameLayouts.get(i5), LayoutHelper.createFrame(100, 100, 51));
                }
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone(false);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        processDone(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        checkFingerprint();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3(View view) {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        Drawable drawable = this.backgroundDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            ((MotionBackgroundDrawable) drawable).switchToPrevPosition(true);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        boolean z;
        int intValue = ((Integer) view.getTag()).intValue();
        switch (intValue) {
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
                z = this.passwordEditText2.eraseLastCharacter();
                break;
            case 11:
                checkFingerprint();
                break;
        }
        z = false;
        if (this.passwordEditText2.length() == 4) {
            processDone(false);
        }
        if (intValue != 11) {
            if (intValue != 10) {
                Drawable drawable = this.backgroundDrawable;
                if (drawable instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) drawable).switchToNextPosition(true);
                }
            } else if (z) {
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) drawable2).switchToPrevPosition(true);
                }
            }
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
        if (Build.VERSION.SDK_INT >= 23 && FingerprintController.isKeyReady() && FingerprintController.checkDeviceFingerprintsChanged()) {
            FingerprintController.deleteInvalidKey();
        }
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener((View.OnTouchListener) null);
        PasscodeViewDelegate passcodeViewDelegate = this.delegate;
        if (passcodeViewDelegate != null) {
            passcodeViewDelegate.didAcceptedPassword();
        }
        AndroidUtilities.runOnUIThread(new PasscodeView$$ExternalSyntheticLambda7(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$5() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{(float) AndroidUtilities.dp(0.0f)})});
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PasscodeView.this.setVisibility(8);
            }
        });
        animatorSet.start();
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
                this.retryTextView.setText(LocaleController.formatString("TooManyTries", R.string.TooManyTries, LocaleController.formatPluralString("Seconds", max, new Object[0])));
                this.lastValue = max;
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
                AndroidUtilities.runOnUIThread(new PasscodeView$$ExternalSyntheticLambda8(this), 200);
            }
            checkFingerprint();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$6() {
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didGenerateFingerprintKeyPair);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didGenerateFingerprintKeyPair);
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
                FingerprintManagerCompat from = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (from.isHardwareDetected() && from.hasEnrolledFingerprints() && FingerprintController.isKeyReady() && !FingerprintController.checkDeviceFingerprintsChanged()) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    TextView textView = new TextView(getContext());
                    textView.setId(1000);
                    textView.setTextAppearance(16974344);
                    textView.setTextColor(Theme.getColor("dialogTextBlack"));
                    textView.setText(LocaleController.getString("FingerprintInfo", R.string.FingerprintInfo));
                    relativeLayout.addView(textView);
                    RelativeLayout.LayoutParams createRelative = LayoutHelper.createRelative(-2, -2);
                    createRelative.addRule(10);
                    createRelative.addRule(20);
                    textView.setLayoutParams(createRelative);
                    ImageView imageView2 = new ImageView(getContext());
                    this.fingerprintImageView = imageView2;
                    imageView2.setImageResource(R.drawable.ic_fp_40px);
                    this.fingerprintImageView.setId(1001);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, 1000));
                    TextView textView2 = new TextView(getContext());
                    this.fingerprintStatusTextView = textView2;
                    textView2.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", R.string.FingerprintHelp));
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
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
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
                    from.authenticate((FingerprintManagerCompat.CryptoObject) null, 0, cancellationSignal2, new FingerprintManagerCompat.AuthenticationCallback() {
                        public void onAuthenticationError(int i, CharSequence charSequence) {
                            if (i == 10) {
                                try {
                                    if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                        PasscodeView.this.fingerprintDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                                AlertDialog unused = PasscodeView.this.fingerprintDialog = null;
                            } else if (!PasscodeView.this.selfCancelled && i != 5) {
                                PasscodeView.this.showFingerprintError(charSequence);
                            }
                        }

                        public void onAuthenticationHelp(int i, CharSequence charSequence) {
                            PasscodeView.this.showFingerprintError(charSequence);
                        }

                        public void onAuthenticationFailed() {
                            PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", R.string.FingerprintNotRecognized));
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
    public /* synthetic */ void lambda$checkFingerprint$7(DialogInterface dialogInterface) {
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

    public void onShow(boolean z, boolean z2) {
        onShow(z, z2, -1, -1, (Runnable) null, (Runnable) null);
    }

    private void checkFingerprintButton() {
        Activity activity = (Activity) getContext();
        if (Build.VERSION.SDK_INT < 23 || activity == null || !SharedConfig.useFingerprint) {
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
                FingerprintManagerCompat from = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (!from.isHardwareDetected() || !from.hasEnrolledFingerprints() || !FingerprintController.isKeyReady() || FingerprintController.checkDeviceFingerprintsChanged()) {
                    this.fingerprintView.setVisibility(8);
                } else {
                    this.fingerprintView.setVisibility(0);
                }
            } catch (Throwable th) {
                FileLog.e(th);
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

    public void onShow(boolean z, boolean z2, int i, int i2, Runnable runnable, Runnable runnable2) {
        View currentFocus;
        EditTextBoldCursor editTextBoldCursor;
        final Runnable runnable3 = runnable;
        checkFingerprintButton();
        checkRetryTextView();
        Activity activity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (!(z2 || this.retryTextView.getVisibility() == 0 || (editTextBoldCursor = this.passwordEditText) == null)) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (!(activity == null || (currentFocus = activity.getCurrentFocus()) == null)) {
            currentFocus.clearFocus();
            AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
        }
        if (z && this.retryTextView.getVisibility() != 0) {
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
                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                int[] colors = motionBackgroundDrawable.getColors();
                this.backgroundDrawable = new MotionBackgroundDrawable(colors[0], colors[1], colors[2], colors[3], false);
                if (!motionBackgroundDrawable.hasPattern() || motionBackgroundDrawable.getIntensity() >= 0) {
                    this.backgroundFrameLayout.setBackgroundColor(NUM);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(NUM);
                }
                ((MotionBackgroundDrawable) this.backgroundDrawable).setParentView(this.backgroundFrameLayout);
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourTelegramPasscode", R.string.EnterYourTelegramPasscode));
            int i3 = SharedConfig.passcodeType;
            if (i3 == 0) {
                if (this.retryTextView.getVisibility() != 0) {
                    this.numbersFrameLayout.setVisibility(0);
                }
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
                this.fingerprintImage.setVisibility(8);
            } else if (i3 == 1) {
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
            if (z2) {
                setAlpha(0.0f);
                final int i4 = i;
                final int i5 = i2;
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        int i;
                        float f;
                        View view;
                        int i2;
                        final AnimatorSet animatorSet;
                        char c;
                        float f2 = 1.0f;
                        PasscodeView.this.setAlpha(1.0f);
                        PasscodeView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        float f3 = 0.0f;
                        PasscodeView.this.imageView.setProgress(0.0f);
                        PasscodeView.this.imageView.playAnimation();
                        AndroidUtilities.runOnUIThread(new PasscodeView$9$$ExternalSyntheticLambda1(this), 350);
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        ArrayList arrayList = new ArrayList();
                        Point point = AndroidUtilities.displaySize;
                        int i3 = point.x;
                        int i4 = point.y;
                        int i5 = Build.VERSION.SDK_INT;
                        char c2 = 0;
                        int i6 = i4 + (i5 >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        if (i5 >= 21) {
                            int i7 = i4;
                            int i8 = (i3 - i7) * (i3 - i7);
                            int i9 = i5;
                            double sqrt = Math.sqrt((double) (i8 + ((i6 - i9) * (i6 - i9))));
                            int i10 = i4;
                            int i11 = i5;
                            double sqrt2 = Math.sqrt((double) ((i10 * i10) + ((i6 - i11) * (i6 - i11))));
                            int i12 = i4;
                            int i13 = i5;
                            double sqrt3 = Math.sqrt((double) ((i12 * i12) + (i13 * i13)));
                            int i14 = i4;
                            int i15 = (i3 - i14) * (i3 - i14);
                            int i16 = i5;
                            double max = Math.max(Math.max(Math.max(sqrt, sqrt2), sqrt3), Math.sqrt((double) (i15 + (i16 * i16))));
                            PasscodeView.this.innerAnimators.clear();
                            int childCount = PasscodeView.this.numbersFrameLayout.getChildCount();
                            int i17 = -1;
                            int i18 = -1;
                            while (i18 < childCount) {
                                if (i18 == i17) {
                                    view = PasscodeView.this.passcodeTextView;
                                } else {
                                    view = PasscodeView.this.numbersFrameLayout.getChildAt(i18);
                                }
                                if ((view instanceof TextView) || (view instanceof ImageView)) {
                                    view.setScaleX(0.7f);
                                    view.setScaleY(0.7f);
                                    view.setAlpha(f3);
                                    InnerAnimator innerAnimator = new InnerAnimator();
                                    view.getLocationInWindow(PasscodeView.this.pos);
                                    int measuredWidth = PasscodeView.this.pos[c2] + (view.getMeasuredWidth() / 2);
                                    int measuredHeight = PasscodeView.this.pos[1] + (view.getMeasuredHeight() / 2);
                                    int i19 = i4;
                                    int i20 = (i19 - measuredWidth) * (i19 - measuredWidth);
                                    int i21 = i5;
                                    float unused = innerAnimator.startRadius = ((float) Math.sqrt((double) (i20 + ((i21 - measuredHeight) * (i21 - measuredHeight))))) - ((float) AndroidUtilities.dp(40.0f));
                                    if (i18 != i17) {
                                        animatorSet = new AnimatorSet();
                                        Animator[] animatorArr = new Animator[2];
                                        Property property = View.SCALE_X;
                                        i2 = childCount;
                                        float[] fArr = new float[1];
                                        fArr[c2] = f2;
                                        animatorArr[c2] = ObjectAnimator.ofFloat(view, property, fArr);
                                        Property property2 = View.SCALE_Y;
                                        float[] fArr2 = new float[1];
                                        fArr2[c2] = f2;
                                        animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                                        animatorSet.playTogether(animatorArr);
                                        animatorSet.setDuration(140);
                                        animatorSet.setInterpolator(new DecelerateInterpolator());
                                    } else {
                                        i2 = childCount;
                                        animatorSet = null;
                                    }
                                    AnimatorSet unused2 = innerAnimator.animatorSet = new AnimatorSet();
                                    AnimatorSet access$2200 = innerAnimator.animatorSet;
                                    Animator[] animatorArr2 = new Animator[3];
                                    Property property3 = View.SCALE_X;
                                    float[] fArr3 = new float[2];
                                    float f4 = 0.6f;
                                    fArr3[c2] = i18 == -1 ? 0.9f : 0.6f;
                                    float f5 = 1.04f;
                                    fArr3[1] = i18 == -1 ? 1.0f : 1.04f;
                                    animatorArr2[c2] = ObjectAnimator.ofFloat(view, property3, fArr3);
                                    Property property4 = View.SCALE_Y;
                                    float[] fArr4 = new float[2];
                                    if (i18 == -1) {
                                        f4 = 0.9f;
                                    }
                                    fArr4[0] = f4;
                                    if (i18 == -1) {
                                        c = 1;
                                        f5 = 1.0f;
                                    } else {
                                        c = 1;
                                    }
                                    fArr4[c] = f5;
                                    animatorArr2[c] = ObjectAnimator.ofFloat(view, property4, fArr4);
                                    animatorArr2[2] = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f});
                                    access$2200.playTogether(animatorArr2);
                                    innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter(this) {
                                        public void onAnimationEnd(Animator animator) {
                                            AnimatorSet animatorSet = animatorSet;
                                            if (animatorSet != null) {
                                                animatorSet.start();
                                            }
                                        }
                                    });
                                    innerAnimator.animatorSet.setDuration(i18 == -1 ? 232 : 200);
                                    innerAnimator.animatorSet.setInterpolator(new DecelerateInterpolator());
                                    PasscodeView.this.innerAnimators.add(innerAnimator);
                                } else {
                                    i2 = childCount;
                                }
                                i18++;
                                childCount = i2;
                                f2 = 1.0f;
                                f3 = 0.0f;
                                i17 = -1;
                                c2 = 0;
                            }
                            arrayList.add(ViewAnimationUtils.createCircularReveal(PasscodeView.this.backgroundFrameLayout, i4, i5, 0.0f, (float) max));
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                            arrayList.add(ofFloat);
                            ofFloat.addUpdateListener(new PasscodeView$9$$ExternalSyntheticLambda0(this, max));
                            animatorSet2.setInterpolator(Easings.easeInOutQuad);
                            animatorSet2.setDuration(498);
                        } else {
                            arrayList.add(ObjectAnimator.ofFloat(PasscodeView.this.backgroundFrameLayout, View.ALPHA, new float[]{0.0f, 1.0f}));
                            animatorSet2.setDuration(350);
                        }
                        animatorSet2.playTogether(arrayList);
                        animatorSet2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                Runnable runnable = runnable3;
                                if (runnable != null) {
                                    runnable.run();
                                }
                                if (SharedConfig.passcodeType == 1 && PasscodeView.this.retryTextView.getVisibility() != 0 && PasscodeView.this.passwordEditText != null) {
                                    PasscodeView.this.passwordEditText.requestFocus();
                                    AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
                                }
                            }
                        });
                        animatorSet2.start();
                        AnimatorSet animatorSet3 = new AnimatorSet();
                        animatorSet3.setDuration(332);
                        if (AndroidUtilities.isTablet() || PasscodeView.this.getContext().getResources().getConfiguration().orientation != 2) {
                            f = ((float) i3) / 2.0f;
                            i = AndroidUtilities.dp(29.0f);
                        } else {
                            f = (SharedConfig.passcodeType == 0 ? ((float) i3) / 2.0f : (float) i3) / 2.0f;
                            i = AndroidUtilities.dp(30.0f);
                        }
                        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.TRANSLATION_X, new float[]{(float) (i4 - AndroidUtilities.dp(29.0f)), f - ((float) i)}), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.TRANSLATION_Y, new float[]{(float) (i5 - AndroidUtilities.dp(29.0f)), (float) PasscodeView.this.imageY}), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.SCALE_X, new float[]{0.5f, 1.0f}), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.SCALE_Y, new float[]{0.5f, 1.0f})});
                        animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                        animatorSet3.start();
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onGlobalLayout$0() {
                        PasscodeView.this.imageView.performHapticFeedback(3, 2);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onGlobalLayout$1(double d, ValueAnimator valueAnimator) {
                        double animatedFraction = (double) valueAnimator.getAnimatedFraction();
                        Double.isNaN(animatedFraction);
                        double d2 = d * animatedFraction;
                        int i = 0;
                        while (i < PasscodeView.this.innerAnimators.size()) {
                            InnerAnimator innerAnimator = (InnerAnimator) PasscodeView.this.innerAnimators.get(i);
                            if (((double) innerAnimator.startRadius) <= d2) {
                                innerAnimator.animatorSet.start();
                                PasscodeView.this.innerAnimators.remove(i);
                                i--;
                            }
                            i++;
                        }
                    }
                });
                requestLayout();
            } else {
                setAlpha(1.0f);
                this.imageView.setScaleX(1.0f);
                this.imageView.setScaleY(1.0f);
                this.imageView.stopAnimation();
                this.imageView.getAnimatedDrawable().setCurrentFrame(38, false);
                if (runnable3 != null) {
                    runnable.run();
                }
            }
            setOnTouchListener(PasscodeView$$ExternalSyntheticLambda5.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public void showFingerprintError(CharSequence charSequence) {
        this.fingerprintImageView.setImageResource(R.drawable.ic_fingerprint_error);
        this.fingerprintStatusTextView.setText(charSequence);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    /* JADX WARNING: type inference failed for: r5v7, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r5v14, types: [android.view.ViewGroup$LayoutParams] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r14, int r15) {
        /*
            r13 = this;
            int r0 = android.view.View.MeasureSpec.getSize(r14)
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.y
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            r4 = 0
            if (r2 < r3) goto L_0x0011
            r5 = 0
            goto L_0x0013
        L_0x0011:
            int r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0013:
            int r1 = r1 - r5
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            r6 = 1105723392(0x41e80000, float:29.0)
            r7 = 1073741824(0x40000000, float:2.0)
            r8 = 1109393408(0x42200000, float:40.0)
            r9 = 2
            if (r5 != 0) goto L_0x0098
            android.content.Context r5 = r13.getContext()
            android.content.res.Resources r5 = r5.getResources()
            android.content.res.Configuration r5 = r5.getConfiguration()
            int r5 = r5.orientation
            if (r5 != r9) goto L_0x0098
            org.telegram.ui.Components.RLottieImageView r5 = r13.imageView
            int r10 = org.telegram.messenger.SharedConfig.passcodeType
            if (r10 != 0) goto L_0x003a
            float r10 = (float) r0
            float r10 = r10 / r7
            goto L_0x003b
        L_0x003a:
            float r10 = (float) r0
        L_0x003b:
            float r10 = r10 / r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r10 = r10 - r6
            r5.setTranslationX(r10)
            android.widget.FrameLayout r5 = r13.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            int r6 = org.telegram.messenger.SharedConfig.passcodeType
            if (r6 != 0) goto L_0x0054
            int r6 = r0 / 2
            goto L_0x0055
        L_0x0054:
            r6 = r0
        L_0x0055:
            r5.width = r6
            r6 = 1124859904(0x430CLASSNAME, float:140.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.height = r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r1 - r6
            int r6 = r6 / r9
            int r7 = org.telegram.messenger.SharedConfig.passcodeType
            if (r7 != 0) goto L_0x006f
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0070
        L_0x006f:
            r7 = 0
        L_0x0070:
            int r6 = r6 + r7
            r5.topMargin = r6
            android.widget.FrameLayout r6 = r13.passwordFrameLayout
            r6.setLayoutParams(r5)
            android.widget.FrameLayout r5 = r13.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            r5.height = r1
            int r0 = r0 / r9
            r5.leftMargin = r0
            int r1 = r1 - r1
            if (r2 < r3) goto L_0x008b
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x008c
        L_0x008b:
            r2 = 0
        L_0x008c:
            int r1 = r1 + r2
            r5.topMargin = r1
            r5.width = r0
            android.widget.FrameLayout r0 = r13.numbersFrameLayout
            r0.setLayoutParams(r5)
            goto L_0x0143
        L_0x0098:
            org.telegram.ui.Components.RLottieImageView r2 = r13.imageView
            float r3 = (float) r0
            float r3 = r3 / r7
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r5 = (float) r5
            float r3 = r3 - r5
            r2.setTranslationX(r3)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x00dc
            r2 = 1140391936(0x43var_, float:498.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r0 <= r3) goto L_0x00c1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r3
            int r0 = r0 / r9
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12 = r2
            r2 = r0
            r0 = r12
            goto L_0x00c2
        L_0x00c1:
            r2 = 0
        L_0x00c2:
            r3 = 1141112832(0x44040000, float:528.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            if (r1 <= r5) goto L_0x00d9
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r5
            int r1 = r1 / r9
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r12 = r2
            r2 = r1
            r1 = r3
            r3 = r12
            goto L_0x00de
        L_0x00d9:
            r3 = r2
            r2 = 0
            goto L_0x00de
        L_0x00dc:
            r2 = 0
            r3 = 0
        L_0x00de:
            android.widget.FrameLayout r5 = r13.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            int r6 = r1 / 3
            int r7 = org.telegram.messenger.SharedConfig.passcodeType
            if (r7 != 0) goto L_0x00f1
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x00f2
        L_0x00f1:
            r7 = 0
        L_0x00f2:
            int r7 = r7 + r6
            r5.height = r7
            r5.width = r0
            r5.topMargin = r2
            r5.leftMargin = r3
            android.widget.FrameLayout r7 = r13.passwordFrameLayout
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            r7.setTag(r10)
            android.widget.FrameLayout r7 = r13.passwordFrameLayout
            r7.setLayoutParams(r5)
            android.widget.FrameLayout r5 = r13.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            int r6 = r6 * 2
            r5.height = r6
            r5.leftMargin = r3
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x012b
            int r3 = r5.height
            int r1 = r1 - r3
            int r1 = r1 + r2
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r5.topMargin = r1
            goto L_0x013c
        L_0x012b:
            int r3 = r5.height
            int r1 = r1 - r3
            int r1 = r1 + r2
            int r2 = org.telegram.messenger.SharedConfig.passcodeType
            if (r2 != 0) goto L_0x0138
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0139
        L_0x0138:
            r2 = 0
        L_0x0139:
            int r1 = r1 + r2
            r5.topMargin = r1
        L_0x013c:
            r5.width = r0
            android.widget.FrameLayout r0 = r13.numbersFrameLayout
            r0.setLayoutParams(r5)
        L_0x0143:
            int r0 = r5.width
            r1 = 1112014848(0x42480000, float:50.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r2 * 3
            int r0 = r0 - r2
            int r0 = r0 / 4
            int r2 = r5.height
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r3 = r3 * 4
            int r2 = r2 - r3
            int r2 = r2 / 5
        L_0x015b:
            r3 = 12
            if (r4 >= r3) goto L_0x0244
            r3 = 11
            r5 = 10
            if (r4 != 0) goto L_0x0168
            r3 = 10
            goto L_0x0172
        L_0x0168:
            if (r4 != r5) goto L_0x016b
            goto L_0x0172
        L_0x016b:
            if (r4 != r3) goto L_0x0170
            r3 = 9
            goto L_0x0172
        L_0x0170:
            int r3 = r4 + -1
        L_0x0172:
            int r6 = r3 / 3
            int r3 = r3 % 3
            if (r4 >= r5) goto L_0x01bc
            java.util.ArrayList<android.widget.TextView> r5 = r13.numberTextViews
            java.lang.Object r5 = r5.get(r4)
            android.widget.TextView r5 = (android.widget.TextView) r5
            java.util.ArrayList<android.widget.TextView> r7 = r13.lettersTextViews
            java.lang.Object r7 = r7.get(r4)
            android.widget.TextView r7 = (android.widget.TextView) r7
            android.view.ViewGroup$LayoutParams r9 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            android.view.ViewGroup$LayoutParams r10 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r10 = (android.widget.FrameLayout.LayoutParams) r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r11 = r11 + r2
            int r11 = r11 * r6
            int r11 = r11 + r2
            r9.topMargin = r11
            r10.topMargin = r11
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r6 + r0
            int r6 = r6 * r3
            int r6 = r6 + r0
            r9.leftMargin = r6
            r10.leftMargin = r6
            int r3 = r10.topMargin
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 + r6
            r10.topMargin = r3
            r5.setLayoutParams(r9)
            r7.setLayoutParams(r10)
            goto L_0x021b
        L_0x01bc:
            r7 = 1090519040(0x41000000, float:8.0)
            if (r4 != r5) goto L_0x01ee
            android.widget.ImageView r5 = r13.eraseView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            r9 = r5
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r5 + r2
            int r5 = r5 * r6
            int r5 = r5 + r2
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 + r6
            r9.topMargin = r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r6 + r0
            int r6 = r6 * r3
            int r6 = r6 + r0
            r9.leftMargin = r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = r5 - r3
            android.widget.ImageView r3 = r13.eraseView
            r3.setLayoutParams(r9)
            goto L_0x021b
        L_0x01ee:
            android.widget.ImageView r5 = r13.fingerprintView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            r9 = r5
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r5 + r2
            int r5 = r5 * r6
            int r5 = r5 + r2
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 + r6
            r9.topMargin = r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r6 + r0
            int r6 = r6 * r3
            int r6 = r6 + r0
            r9.leftMargin = r6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = r5 - r3
            android.widget.ImageView r3 = r13.fingerprintView
            r3.setLayoutParams(r9)
        L_0x021b:
            java.util.ArrayList<android.widget.FrameLayout> r3 = r13.numberFrameLayouts
            java.lang.Object r3 = r3.get(r4)
            android.widget.FrameLayout r3 = (android.widget.FrameLayout) r3
            android.view.ViewGroup$LayoutParams r5 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r11 = r11 - r6
            r5.topMargin = r11
            int r6 = r9.leftMargin
            r7 = 1103626240(0x41CLASSNAME, float:25.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            r5.leftMargin = r6
            r3.setLayoutParams(r5)
            int r4 = r4 + 1
            goto L_0x015b
        L_0x0244:
            super.onMeasure(r14, r15)
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
