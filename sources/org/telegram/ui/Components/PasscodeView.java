package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.os.CancellationSignal;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;

public class PasscodeView extends FrameLayout {
    private static final int id_fingerprint_imageview = 1001;
    private static final int id_fingerprint_textview = 1000;
    private Drawable backgroundDrawable;
    private FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    private AlertDialog fingerprintDialog;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private int keyboardHeight = 0;
    private ArrayList<TextView> lettersTextViews;
    private ArrayList<FrameLayout> numberFrameLayouts;
    private ArrayList<TextView> numberTextViews;
    private FrameLayout numbersFrameLayout;
    private TextView passcodeTextView;
    private EditTextBoldCursor passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    private Rect rect = new Rect();
    private boolean selfCancelled;

    /* renamed from: org.telegram.ui.Components.PasscodeView$1 */
    class C12181 implements OnEditorActionListener {
        C12181() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            PasscodeView.this.processDone(false);
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$2 */
    class C12192 implements TextWatcher {
        C12192() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (PasscodeView.this.passwordEditText.length() == 4 && SharedConfig.passcodeType == 0) {
                PasscodeView.this.processDone(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$3 */
    class C12203 implements Callback {
        C12203() {
        }

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
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$4 */
    class C12214 implements OnClickListener {
        C12214() {
        }

        public void onClick(View v) {
            PasscodeView.this.processDone(false);
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$5 */
    class C12225 implements OnLongClickListener {
        C12225() {
        }

        public boolean onLongClick(View v) {
            PasscodeView.this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            PasscodeView.this.passwordEditText2.eraseAllCharacters(true);
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$6 */
    class C12236 implements OnClickListener {
        C12236() {
        }

        public void onClick(View v) {
            switch (((Integer) v.getTag()).intValue()) {
                case 0:
                    PasscodeView.this.passwordEditText2.appendCharacter("0");
                    break;
                case 1:
                    PasscodeView.this.passwordEditText2.appendCharacter("1");
                    break;
                case 2:
                    PasscodeView.this.passwordEditText2.appendCharacter("2");
                    break;
                case 3:
                    PasscodeView.this.passwordEditText2.appendCharacter("3");
                    break;
                case 4:
                    PasscodeView.this.passwordEditText2.appendCharacter("4");
                    break;
                case 5:
                    PasscodeView.this.passwordEditText2.appendCharacter("5");
                    break;
                case 6:
                    PasscodeView.this.passwordEditText2.appendCharacter("6");
                    break;
                case 7:
                    PasscodeView.this.passwordEditText2.appendCharacter("7");
                    break;
                case 8:
                    PasscodeView.this.passwordEditText2.appendCharacter("8");
                    break;
                case 9:
                    PasscodeView.this.passwordEditText2.appendCharacter("9");
                    break;
                case 10:
                    PasscodeView.this.passwordEditText2.eraseLastCharacter();
                    break;
                default:
                    break;
            }
            if (PasscodeView.this.passwordEditText2.lenght() == 4) {
                PasscodeView.this.processDone(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$7 */
    class C12247 extends AnimatorListenerAdapter {
        C12247() {
        }

        public void onAnimationEnd(Animator animation) {
            PasscodeView.this.setVisibility(8);
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$9 */
    class C12269 implements Runnable {
        C12269() {
        }

        public void run() {
            if (PasscodeView.this.passwordEditText != null) {
                PasscodeView.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
            }
        }
    }

    private class AnimatingTextView extends FrameLayout {
        private String DOT = "\u2022";
        private ArrayList<TextView> characterTextViews = new ArrayList(4);
        private AnimatorSet currentAnimation;
        private Runnable dotRunnable;
        private ArrayList<TextView> dotTextViews = new ArrayList(4);
        private StringBuilder stringBuilder = new StringBuilder(4);

        /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$2 */
        class C12292 extends AnimatorListenerAdapter {
            C12292() {
            }

            public void onAnimationEnd(Animator animation) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$3 */
        class C12303 extends AnimatorListenerAdapter {
            C12303() {
            }

            public void onAnimationEnd(Animator animation) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$4 */
        class C12314 extends AnimatorListenerAdapter {
            C12314() {
            }

            public void onAnimationEnd(Animator animation) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

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
                addView(textView);
                LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                layoutParams.height = AndroidUtilities.dp(50.0f);
                layoutParams.gravity = 51;
                textView.setLayoutParams(layoutParams);
                this.characterTextViews.add(textView);
                textView = new TextView(context);
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setText(this.DOT);
                textView.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView);
                LayoutParams layoutParams2 = (LayoutParams) textView.getLayoutParams();
                layoutParams2.width = AndroidUtilities.dp(50.0f);
                layoutParams2.height = AndroidUtilities.dp(50.0f);
                layoutParams2.gravity = 51;
                textView.setLayoutParams(layoutParams2);
                this.dotTextViews.add(textView);
            }
        }

        private int getXForTextView(int pos) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (AndroidUtilities.dp(30.0f) * pos)) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String c) {
            if (this.stringBuilder.length() != 4) {
                try {
                    performHapticFeedback(3);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                ArrayList<Animator> animators = new ArrayList();
                final int newPos = this.stringBuilder.length();
                this.stringBuilder.append(c);
                TextView textView = (TextView) this.characterTextViews.get(newPos);
                textView.setText(c);
                textView.setTranslationX((float) getXForTextView(newPos));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                textView = (TextView) this.dotTextViews.get(newPos);
                textView.setTranslationX((float) getXForTextView(newPos));
                textView.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (int a = newPos + 1; a < 4; a++) {
                    textView = (TextView) this.characterTextViews.get(a);
                    if (textView.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    }
                    textView = (TextView) this.dotTextViews.get(a);
                    if (textView.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    }
                }
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                }
                this.dotRunnable = new Runnable() {

                    /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$1$1 */
                    class C12271 extends AnimatorListenerAdapter {
                        C12271() {
                        }

                        public void onAnimationEnd(Animator animation) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                                AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    }

                    public void run() {
                        if (AnimatingTextView.this.dotRunnable == this) {
                            ArrayList<Animator> animators = new ArrayList();
                            TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(newPos);
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                            textView = (TextView) AnimatingTextView.this.dotTextViews.get(newPos);
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{1.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{1.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{1.0f}));
                            AnimatingTextView.this.currentAnimation = new AnimatorSet();
                            AnimatingTextView.this.currentAnimation.setDuration(150);
                            AnimatingTextView.this.currentAnimation.playTogether(animators);
                            AnimatingTextView.this.currentAnimation.addListener(new C12271());
                            AnimatingTextView.this.currentAnimation.start();
                        }
                    }
                };
                AndroidUtilities.runOnUIThread(this.dotRunnable, 1500);
                for (int a2 = 0; a2 < newPos; a2++) {
                    textView = (TextView) this.characterTextViews.get(a2);
                    animators.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                    textView = (TextView) this.dotTextViews.get(a2);
                    animators.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.addListener(new C12292());
                this.currentAnimation.start();
            }
        }

        public String getString() {
            return this.stringBuilder.toString();
        }

        public int lenght() {
            return this.stringBuilder.length();
        }

        public void eraseLastCharacter() {
            if (this.stringBuilder.length() != 0) {
                int a;
                try {
                    performHapticFeedback(3);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                ArrayList<Animator> animators = new ArrayList();
                int deletingPos = this.stringBuilder.length() - 1;
                if (deletingPos != 0) {
                    this.stringBuilder.deleteCharAt(deletingPos);
                }
                for (a = deletingPos; a < 4; a++) {
                    TextView textView = (TextView) this.characterTextViews.get(a);
                    if (textView.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(a)}));
                    }
                    textView = (TextView) this.dotTextViews.get(a);
                    if (textView.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(a)}));
                    }
                }
                if (deletingPos == 0) {
                    this.stringBuilder.deleteCharAt(deletingPos);
                }
                for (a = 0; a < deletingPos; a++) {
                    animators.add(ObjectAnimator.ofFloat((TextView) this.characterTextViews.get(a), "translationX", new float[]{(float) getXForTextView(a)}));
                    animators.add(ObjectAnimator.ofFloat((TextView) this.dotTextViews.get(a), "translationX", new float[]{(float) getXForTextView(a)}));
                }
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                    this.dotRunnable = null;
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.addListener(new C12303());
                this.currentAnimation.start();
            }
        }

        private void eraseAllCharacters(boolean animated) {
            if (this.stringBuilder.length() != 0) {
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                    this.dotRunnable = null;
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                    this.currentAnimation = null;
                }
                int a = 0;
                this.stringBuilder.delete(0, this.stringBuilder.length());
                if (animated) {
                    ArrayList<Animator> animators = new ArrayList();
                    for (int a2 = 0; a2 < 4; a2++) {
                        TextView textView = (TextView) this.characterTextViews.get(a2);
                        if (textView.getAlpha() != 0.0f) {
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                        textView = (TextView) this.dotTextViews.get(a2);
                        if (textView.getAlpha() != 0.0f) {
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                    }
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.playTogether(animators);
                    this.currentAnimation.addListener(new C12314());
                    this.currentAnimation.start();
                } else {
                    while (a < 4) {
                        ((TextView) this.characterTextViews.get(a)).setAlpha(0.0f);
                        ((TextView) this.dotTextViews.get(a)).setAlpha(0.0f);
                        a++;
                    }
                }
            }
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (this.dotRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                this.dotRunnable = null;
            }
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
            for (int a = 0; a < 4; a++) {
                if (a < this.stringBuilder.length()) {
                    TextView textView = (TextView) this.characterTextViews.get(a);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(a));
                    textView = (TextView) this.dotTextViews.get(a);
                    textView.setAlpha(1.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(a));
                } else {
                    ((TextView) this.characterTextViews.get(a)).setAlpha(0.0f);
                    ((TextView) this.dotTextViews.get(a)).setAlpha(0.0f);
                }
            }
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    public PasscodeView(Context context) {
        Context context2 = context;
        super(context);
        int a = 0;
        setWillNotDraw(false);
        setVisibility(8);
        this.backgroundFrameLayout = new FrameLayout(context2);
        addView(this.backgroundFrameLayout);
        LayoutParams layoutParams = (LayoutParams) this.backgroundFrameLayout.getLayoutParams();
        int i = -1;
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.backgroundFrameLayout.setLayoutParams(layoutParams);
        this.passwordFrameLayout = new FrameLayout(context2);
        addView(this.passwordFrameLayout);
        layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.passwordFrameLayout.setLayoutParams(layoutParams);
        ImageView imageView = new ImageView(context2);
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.passcode_logo);
        this.passwordFrameLayout.addView(imageView);
        layoutParams = (LayoutParams) imageView.getLayoutParams();
        if (AndroidUtilities.density < 1.0f) {
            layoutParams.width = AndroidUtilities.dp(30.0f);
            layoutParams.height = AndroidUtilities.dp(30.0f);
        } else {
            layoutParams.width = AndroidUtilities.dp(40.0f);
            layoutParams.height = AndroidUtilities.dp(40.0f);
        }
        layoutParams.gravity = 81;
        layoutParams.bottomMargin = AndroidUtilities.dp(100.0f);
        imageView.setLayoutParams(layoutParams);
        r0.passcodeTextView = new TextView(context2);
        r0.passcodeTextView.setTextColor(-1);
        r0.passcodeTextView.setTextSize(1, 14.0f);
        r0.passcodeTextView.setGravity(1);
        r0.passwordFrameLayout.addView(r0.passcodeTextView);
        layoutParams = (LayoutParams) r0.passcodeTextView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.bottomMargin = AndroidUtilities.dp(62.0f);
        layoutParams.gravity = 81;
        r0.passcodeTextView.setLayoutParams(layoutParams);
        r0.passwordEditText2 = new AnimatingTextView(context2);
        r0.passwordFrameLayout.addView(r0.passwordEditText2);
        layoutParams = (LayoutParams) r0.passwordEditText2.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(6.0f);
        layoutParams.gravity = 81;
        r0.passwordEditText2.setLayoutParams(layoutParams);
        r0.passwordEditText = new EditTextBoldCursor(context2);
        float f = 36.0f;
        r0.passwordEditText.setTextSize(1, 36.0f);
        r0.passwordEditText.setTextColor(-1);
        r0.passwordEditText.setMaxLines(1);
        r0.passwordEditText.setLines(1);
        r0.passwordEditText.setGravity(1);
        r0.passwordEditText.setSingleLine(true);
        r0.passwordEditText.setImeOptions(6);
        r0.passwordEditText.setTypeface(Typeface.DEFAULT);
        r0.passwordEditText.setBackgroundDrawable(null);
        r0.passwordEditText.setCursorColor(-1);
        r0.passwordEditText.setCursorSize(AndroidUtilities.dp(32.0f));
        r0.passwordFrameLayout.addView(r0.passwordEditText);
        layoutParams = (LayoutParams) r0.passwordEditText.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams.gravity = 81;
        r0.passwordEditText.setLayoutParams(layoutParams);
        r0.passwordEditText.setOnEditorActionListener(new C12181());
        r0.passwordEditText.addTextChangedListener(new C12192());
        r0.passwordEditText.setCustomSelectionActionModeCallback(new C12203());
        r0.checkImage = new ImageView(context2);
        r0.checkImage.setImageResource(R.drawable.passcode_check);
        r0.checkImage.setScaleType(ScaleType.CENTER);
        r0.checkImage.setBackgroundResource(R.drawable.bar_selector_lock);
        r0.passwordFrameLayout.addView(r0.checkImage);
        layoutParams = (LayoutParams) r0.checkImage.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(60.0f);
        layoutParams.height = AndroidUtilities.dp(60.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
        layoutParams.gravity = 85;
        r0.checkImage.setLayoutParams(layoutParams);
        r0.checkImage.setOnClickListener(new C12214());
        FrameLayout lineFrameLayout = new FrameLayout(context2);
        lineFrameLayout.setBackgroundColor(654311423);
        r0.passwordFrameLayout.addView(lineFrameLayout);
        layoutParams = (LayoutParams) lineFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(1.0f);
        layoutParams.gravity = 83;
        layoutParams.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(20.0f);
        lineFrameLayout.setLayoutParams(layoutParams);
        r0.numbersFrameLayout = new FrameLayout(context2);
        addView(r0.numbersFrameLayout);
        layoutParams = (LayoutParams) r0.numbersFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        r0.numbersFrameLayout.setLayoutParams(layoutParams);
        int a2 = 10;
        r0.lettersTextViews = new ArrayList(10);
        r0.numberTextViews = new ArrayList(10);
        r0.numberFrameLayouts = new ArrayList(10);
        int a3 = 0;
        while (a3 < 10) {
            TextView textView = new TextView(context2);
            textView.setTextColor(i);
            textView.setTextSize(1, f);
            textView.setGravity(17);
            textView.setText(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(a3)}));
            r0.numbersFrameLayout.addView(textView);
            LayoutParams layoutParams2 = (LayoutParams) textView.getLayoutParams();
            layoutParams2.width = AndroidUtilities.dp(50.0f);
            layoutParams2.height = AndroidUtilities.dp(50.0f);
            layoutParams2.gravity = 51;
            textView.setLayoutParams(layoutParams2);
            r0.numberTextViews.add(textView);
            TextView textView2 = new TextView(context2);
            textView2.setTextSize(1, 12.0f);
            textView2.setTextColor(ConnectionsManager.DEFAULT_DATACENTER_ID);
            textView2.setGravity(17);
            r0.numbersFrameLayout.addView(textView2);
            LayoutParams layoutParams3 = (LayoutParams) textView2.getLayoutParams();
            layoutParams3.width = AndroidUtilities.dp(50.0f);
            layoutParams3.height = AndroidUtilities.dp(20.0f);
            layoutParams3.gravity = 51;
            textView2.setLayoutParams(layoutParams3);
            if (a3 != 0) {
                switch (a3) {
                    case 2:
                        textView2.setText("ABC");
                        break;
                    case 3:
                        textView2.setText("DEF");
                        break;
                    case 4:
                        textView2.setText("GHI");
                        break;
                    case 5:
                        textView2.setText("JKL");
                        break;
                    case 6:
                        textView2.setText("MNO");
                        break;
                    case 7:
                        textView2.setText("PQRS");
                        break;
                    case 8:
                        textView2.setText("TUV");
                        break;
                    case 9:
                        textView2.setText("WXYZ");
                        break;
                    default:
                        break;
                }
            }
            textView2.setText("+");
            r0.lettersTextViews.add(textView2);
            a3++;
            i = -1;
            f = 36.0f;
        }
        r0.eraseView = new ImageView(context2);
        r0.eraseView.setScaleType(ScaleType.CENTER);
        r0.eraseView.setImageResource(R.drawable.passcode_delete);
        r0.numbersFrameLayout.addView(r0.eraseView);
        layoutParams = (LayoutParams) r0.eraseView.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(50.0f);
        layoutParams.height = AndroidUtilities.dp(50.0f);
        layoutParams.gravity = 51;
        r0.eraseView.setLayoutParams(layoutParams);
        while (a < 11) {
            FrameLayout frameLayout = new FrameLayout(context2);
            frameLayout.setBackgroundResource(R.drawable.bar_selector_lock);
            frameLayout.setTag(Integer.valueOf(a));
            if (a == 10) {
                frameLayout.setOnLongClickListener(new C12225());
            }
            frameLayout.setOnClickListener(new C12236());
            r0.numberFrameLayouts.add(frameLayout);
            a++;
        }
        while (true) {
            a = a2;
            if (a >= 0) {
                frameLayout = (FrameLayout) r0.numberFrameLayouts.get(a);
                r0.numbersFrameLayout.addView(frameLayout);
                layoutParams = (LayoutParams) frameLayout.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(100.0f);
                layoutParams.height = AndroidUtilities.dp(100.0f);
                layoutParams.gravity = 51;
                frameLayout.setLayoutParams(layoutParams);
                a2 = a - 1;
            } else {
                return;
            }
        }
    }

    public void setDelegate(PasscodeViewDelegate delegate) {
        this.delegate = delegate;
    }

    private void processDone(boolean fingerprint) {
        if (!fingerprint) {
            String password = TtmlNode.ANONYMOUS_REGION_ID;
            if (SharedConfig.passcodeType == 0) {
                password = this.passwordEditText2.getString();
            } else if (SharedConfig.passcodeType == 1) {
                password = this.passwordEditText.getText().toString();
            }
            if (password.length() == 0) {
                onPasscodeError();
                return;
            } else if (!SharedConfig.checkPasscode(password)) {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.passwordEditText2.eraseAllCharacters(true);
                onPasscodeError();
                return;
            }
        }
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.setDuration(200);
        r2 = new Animator[2];
        r2[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)});
        r2[1] = ObjectAnimator.ofFloat(this, "alpha", new float[]{(float) AndroidUtilities.dp(0.0f)});
        AnimatorSet.playTogether(r2);
        AnimatorSet.addListener(new C12247());
        AnimatorSet.start();
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener(null);
        if (this.delegate != null) {
            this.delegate.didAcceptedPassword();
        }
    }

    private void shakeTextView(final float x, final int num) {
        if (num != 6) {
            AnimatorSet AnimatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.passcodeTextView, "translationX", new float[]{(float) AndroidUtilities.dp(x)});
            AnimatorSet.playTogether(animatorArr);
            AnimatorSet.setDuration(50);
            AnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PasscodeView.this.shakeTextView(num == 5 ? 0.0f : -x, num + 1);
                }
            });
            AnimatorSet.start();
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
        if (SharedConfig.passcodeType == 1) {
            if (this.passwordEditText != null) {
                this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
            AndroidUtilities.runOnUIThread(new C12269(), 200);
        }
        checkFingerprint();
    }

    public void onPause() {
        if (this.fingerprintDialog != null) {
            try {
                if (this.fingerprintDialog.isShowing()) {
                    this.fingerprintDialog.dismiss();
                }
                this.fingerprintDialog = null;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        try {
            if (VERSION.SDK_INT >= 23 && this.cancellationSignal != null) {
                this.cancellationSignal.cancel();
                this.cancellationSignal = null;
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    private void checkFingerprint() {
        PasscodeView passcodeView = this;
        Activity parentActivity = (Activity) getContext();
        if (VERSION.SDK_INT >= 23 && parentActivity != null && SharedConfig.useFingerprint && !ApplicationLoader.mainInterfacePaused) {
            try {
                if (passcodeView.fingerprintDialog == null || !passcodeView.fingerprintDialog.isShowing()) {
                    try {
                        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                        if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                            RelativeLayout relativeLayout = new RelativeLayout(getContext());
                            relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                            TextView fingerprintTextView = new TextView(getContext());
                            fingerprintTextView.setId(id_fingerprint_textview);
                            fingerprintTextView.setTextAppearance(16974344);
                            fingerprintTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            fingerprintTextView.setText(LocaleController.getString("FingerprintInfo", R.string.FingerprintInfo));
                            relativeLayout.addView(fingerprintTextView);
                            RelativeLayout.LayoutParams layoutParams = LayoutHelper.createRelative(-2, -2);
                            layoutParams.addRule(10);
                            layoutParams.addRule(20);
                            fingerprintTextView.setLayoutParams(layoutParams);
                            passcodeView.fingerprintImageView = new ImageView(getContext());
                            passcodeView.fingerprintImageView.setImageResource(R.drawable.ic_fp_40px);
                            passcodeView.fingerprintImageView.setId(id_fingerprint_imageview);
                            relativeLayout.addView(passcodeView.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, id_fingerprint_textview));
                            passcodeView.fingerprintStatusTextView = new TextView(getContext());
                            passcodeView.fingerprintStatusTextView.setGravity(16);
                            passcodeView.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", R.string.FingerprintHelp));
                            passcodeView.fingerprintStatusTextView.setTextAppearance(16974320);
                            passcodeView.fingerprintStatusTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack) & NUM);
                            relativeLayout.addView(passcodeView.fingerprintStatusTextView);
                            RelativeLayout.LayoutParams layoutParams2 = LayoutHelper.createRelative(-2, -2);
                            layoutParams2.setMarginStart(AndroidUtilities.dp(16.0f));
                            layoutParams2.addRule(8, id_fingerprint_imageview);
                            layoutParams2.addRule(6, id_fingerprint_imageview);
                            layoutParams2.addRule(17, id_fingerprint_imageview);
                            passcodeView.fingerprintStatusTextView.setLayoutParams(layoutParams2);
                            Builder builder = new Builder(getContext());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setView(relativeLayout);
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder.setOnDismissListener(new OnDismissListener() {
                                public void onDismiss(DialogInterface dialog) {
                                    if (PasscodeView.this.cancellationSignal != null) {
                                        PasscodeView.this.selfCancelled = true;
                                        PasscodeView.this.cancellationSignal.cancel();
                                        PasscodeView.this.cancellationSignal = null;
                                    }
                                }
                            });
                            if (passcodeView.fingerprintDialog != null) {
                                if (passcodeView.fingerprintDialog.isShowing()) {
                                    passcodeView.fingerprintDialog.dismiss();
                                }
                            }
                            passcodeView.fingerprintDialog = builder.show();
                            passcodeView.cancellationSignal = new CancellationSignal();
                            passcodeView.selfCancelled = false;
                            fingerprintManager.authenticate(null, 0, passcodeView.cancellationSignal, new AuthenticationCallback() {
                                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                                    if (!PasscodeView.this.selfCancelled) {
                                        PasscodeView.this.showFingerprintError(errString);
                                    }
                                }

                                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                                    PasscodeView.this.showFingerprintError(helpString);
                                }

                                public void onAuthenticationFailed() {
                                    PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", R.string.FingerprintNotRecognized));
                                }

                                public void onAuthenticationSucceeded(AuthenticationResult result) {
                                    try {
                                        if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                            PasscodeView.this.fingerprintDialog.dismiss();
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    PasscodeView.this.fingerprintDialog = null;
                                    PasscodeView.this.processDone(true);
                                }
                            }, null);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    } catch (Throwable th) {
                    }
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
    }

    public void onShow() {
        Activity parentActivity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (this.passwordEditText != null) {
                this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (parentActivity != null) {
            View currentFocus = parentActivity.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
            }
        }
        checkFingerprint();
        if (getVisibility() != 0) {
            setAlpha(1.0f);
            setTranslationY(0.0f);
            if (Theme.isCustomTheme()) {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                this.backgroundFrameLayout.setBackgroundColor(-NUM);
            } else if (MessagesController.getGlobalMainSettings().getInt("selectedBackground", 1000001) == 1000001) {
                this.backgroundFrameLayout.setBackgroundColor(-11436898);
            } else {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                if (this.backgroundDrawable != null) {
                    this.backgroundFrameLayout.setBackgroundColor(-NUM);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(-11436898);
                }
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", R.string.EnterYourPasscode));
            if (SharedConfig.passcodeType == 0) {
                this.numbersFrameLayout.setVisibility(0);
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
            } else if (SharedConfig.passcodeType == 1) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.numbersFrameLayout.setVisibility(8);
                this.passwordEditText.setFocusable(true);
                this.passwordEditText.setFocusableInTouchMode(true);
                this.passwordEditText.setVisibility(0);
                this.passwordEditText2.setVisibility(8);
                this.checkImage.setVisibility(0);
            }
            setVisibility(0);
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.passwordEditText2.eraseAllCharacters(false);
            setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    private void showFingerprintError(CharSequence error) {
        this.fingerprintImageView.setImageResource(R.drawable.ic_fingerprint_error);
        this.fingerprintStatusTextView.setText(error);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams layoutParams;
        PasscodeView passcodeView = this;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int a = 0;
        int height = AndroidUtilities.displaySize.y - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
        if (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) {
            int top = 0;
            int left = 0;
            if (AndroidUtilities.isTablet()) {
                if (width > AndroidUtilities.dp(498.0f)) {
                    int left2 = (width - AndroidUtilities.dp(498.0f)) / 2;
                    width = AndroidUtilities.dp(498.0f);
                    left = left2;
                }
                if (height > AndroidUtilities.dp(528.0f)) {
                    top = (height - AndroidUtilities.dp(528.0f)) / 2;
                    height = AndroidUtilities.dp(528.0f);
                }
            }
            LayoutParams layoutParams2 = (LayoutParams) passcodeView.passwordFrameLayout.getLayoutParams();
            layoutParams2.height = height / 3;
            layoutParams2.width = width;
            layoutParams2.topMargin = top;
            layoutParams2.leftMargin = left;
            passcodeView.passwordFrameLayout.setTag(Integer.valueOf(top));
            passcodeView.passwordFrameLayout.setLayoutParams(layoutParams2);
            layoutParams2 = (LayoutParams) passcodeView.numbersFrameLayout.getLayoutParams();
            layoutParams2.height = (height / 3) * 2;
            layoutParams2.leftMargin = left;
            layoutParams2.topMargin = (height - layoutParams2.height) + top;
            layoutParams2.width = width;
            passcodeView.numbersFrameLayout.setLayoutParams(layoutParams2);
            layoutParams = layoutParams2;
        } else {
            layoutParams = (LayoutParams) passcodeView.passwordFrameLayout.getLayoutParams();
            layoutParams.width = SharedConfig.passcodeType == 0 ? width / 2 : width;
            layoutParams.height = AndroidUtilities.dp(140.0f);
            layoutParams.topMargin = (height - AndroidUtilities.dp(140.0f)) / 2;
            passcodeView.passwordFrameLayout.setLayoutParams(layoutParams);
            layoutParams = (LayoutParams) passcodeView.numbersFrameLayout.getLayoutParams();
            layoutParams.height = height;
            layoutParams.leftMargin = width / 2;
            layoutParams.topMargin = height - layoutParams.height;
            layoutParams.width = width / 2;
            passcodeView.numbersFrameLayout.setLayoutParams(layoutParams);
        }
        float f = 50.0f;
        int sizeBetweenNumbersX = (layoutParams.width - (AndroidUtilities.dp(50.0f) * 3)) / 4;
        int sizeBetweenNumbersY = (layoutParams.height - (AndroidUtilities.dp(50.0f) * 4)) / 5;
        while (a < 11) {
            int num;
            int row;
            int col;
            int top2;
            FrameLayout frameLayout;
            LayoutParams layoutParams1;
            if (a == 0) {
                num = 10;
            } else if (a == 10) {
                num = 11;
            } else {
                num = a - 1;
                row = num / 3;
                col = num % 3;
                if (a >= 10) {
                    TextView textView = (TextView) passcodeView.numberTextViews.get(a);
                    TextView textView1 = (TextView) passcodeView.lettersTextViews.get(a);
                    layoutParams = (LayoutParams) textView.getLayoutParams();
                    LayoutParams layoutParams12 = (LayoutParams) textView1.getLayoutParams();
                    top2 = ((AndroidUtilities.dp(f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY;
                    layoutParams.topMargin = top2;
                    layoutParams12.topMargin = top2;
                    int dp = ((AndroidUtilities.dp(f) + sizeBetweenNumbersX) * col) + sizeBetweenNumbersX;
                    layoutParams.leftMargin = dp;
                    layoutParams12.leftMargin = dp;
                    layoutParams12.topMargin += AndroidUtilities.dp(40.0f);
                    textView.setLayoutParams(layoutParams);
                    textView1.setLayoutParams(layoutParams12);
                    f = 50.0f;
                } else {
                    layoutParams = (LayoutParams) passcodeView.eraseView.getLayoutParams();
                    f = 50.0f;
                    left2 = (((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY) + AndroidUtilities.dp(8.0f);
                    layoutParams.topMargin = left2;
                    layoutParams.leftMargin = ((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersX) * col) + sizeBetweenNumbersX;
                    top2 = left2 - AndroidUtilities.dp(8.0f);
                    passcodeView.eraseView.setLayoutParams(layoutParams);
                }
                frameLayout = (FrameLayout) passcodeView.numberFrameLayouts.get(a);
                layoutParams1 = (LayoutParams) frameLayout.getLayoutParams();
                layoutParams1.topMargin = top2 - AndroidUtilities.dp(17.0f);
                layoutParams1.leftMargin = layoutParams.leftMargin - AndroidUtilities.dp(25.0f);
                frameLayout.setLayoutParams(layoutParams1);
                a++;
            }
            row = num / 3;
            col = num % 3;
            if (a >= 10) {
                layoutParams = (LayoutParams) passcodeView.eraseView.getLayoutParams();
                f = 50.0f;
                left2 = (((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY) + AndroidUtilities.dp(8.0f);
                layoutParams.topMargin = left2;
                layoutParams.leftMargin = ((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersX) * col) + sizeBetweenNumbersX;
                top2 = left2 - AndroidUtilities.dp(8.0f);
                passcodeView.eraseView.setLayoutParams(layoutParams);
            } else {
                TextView textView2 = (TextView) passcodeView.numberTextViews.get(a);
                TextView textView12 = (TextView) passcodeView.lettersTextViews.get(a);
                layoutParams = (LayoutParams) textView2.getLayoutParams();
                LayoutParams layoutParams122 = (LayoutParams) textView12.getLayoutParams();
                top2 = ((AndroidUtilities.dp(f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY;
                layoutParams.topMargin = top2;
                layoutParams122.topMargin = top2;
                int dp2 = ((AndroidUtilities.dp(f) + sizeBetweenNumbersX) * col) + sizeBetweenNumbersX;
                layoutParams.leftMargin = dp2;
                layoutParams122.leftMargin = dp2;
                layoutParams122.topMargin += AndroidUtilities.dp(40.0f);
                textView2.setLayoutParams(layoutParams);
                textView12.setLayoutParams(layoutParams122);
                f = 50.0f;
            }
            frameLayout = (FrameLayout) passcodeView.numberFrameLayouts.get(a);
            layoutParams1 = (LayoutParams) frameLayout.getLayoutParams();
            layoutParams1.topMargin = top2 - AndroidUtilities.dp(17.0f);
            layoutParams1.leftMargin = layoutParams.leftMargin - AndroidUtilities.dp(25.0f);
            frameLayout.setLayoutParams(layoutParams1);
            a++;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View rootView = getRootView();
        int usableViewHeight = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        this.keyboardHeight = usableViewHeight - (this.rect.bottom - this.rect.top);
        if (SharedConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            int t = 0;
            if (this.passwordFrameLayout.getTag() != null) {
                t = ((Integer) this.passwordFrameLayout.getTag()).intValue();
            }
            LayoutParams layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.topMargin = ((layoutParams.height + t) - (this.keyboardHeight / 2)) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            if (this.backgroundDrawable == null) {
                super.onDraw(canvas);
            } else if (this.backgroundDrawable instanceof ColorDrawable) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
            } else {
                float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float scaleY = ((float) (getMeasuredHeight() + this.keyboardHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                float scale = scaleX < scaleY ? scaleY : scaleX;
                int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale));
                int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale));
                int x = (getMeasuredWidth() - width) / 2;
                int y = ((getMeasuredHeight() - height) + this.keyboardHeight) / 2;
                this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                this.backgroundDrawable.draw(canvas);
            }
        }
    }
}
