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
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;

public class PasscodeView extends FrameLayout {
    private static final int id_fingerprint_imageview = 1001;
    private static final int id_fingerprint_textview = 1000;
    private static final int[] ids = new int[]{NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    private Drawable backgroundDrawable;
    private FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    private Runnable checkRunnable = new Runnable() {
        public void run() {
            PasscodeView.this.checkRetryTextView();
            AndroidUtilities.runOnUIThread(PasscodeView.this.checkRunnable, 100);
        }
    };
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    private AlertDialog fingerprintDialog;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private int keyboardHeight = 0;
    private int lastValue;
    private ArrayList<TextView> lettersTextViews;
    private ArrayList<FrameLayout> numberFrameLayouts;
    private ArrayList<TextView> numberTextViews;
    private FrameLayout numbersFrameLayout;
    private TextView passcodeTextView;
    private EditTextBoldCursor passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    private Rect rect = new Rect();
    private TextView retryTextView;
    private boolean selfCancelled;

    private class AnimatingTextView extends FrameLayout {
        private String DOT = "•";
        private ArrayList<TextView> characterTextViews = new ArrayList(4);
        private AnimatorSet currentAnimation;
        private Runnable dotRunnable;
        private ArrayList<TextView> dotTextViews = new ArrayList(4);
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

        private int getXForTextView(int i) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (i * AndroidUtilities.dp(30.0f))) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String str) {
            if (this.stringBuilder.length() != 4) {
                int i;
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                ArrayList arrayList = new ArrayList();
                final int length = this.stringBuilder.length();
                this.stringBuilder.append(str);
                TextView textView = (TextView) this.characterTextViews.get(length);
                textView.setText(str);
                textView.setTranslationX((float) getXForTextView(length));
                String str2 = "scaleX";
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f, 1.0f}));
                String str3 = "scaleY";
                arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f, 1.0f}));
                String str4 = "alpha";
                arrayList.add(ObjectAnimator.ofFloat(textView, str4, new float[]{0.0f, 1.0f}));
                String str5 = "translationY";
                arrayList.add(ObjectAnimator.ofFloat(textView, str5, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                textView = (TextView) this.dotTextViews.get(length);
                textView.setTranslationX((float) getXForTextView(length));
                textView.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, str5, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (i = length + 1; i < 4; i++) {
                    textView = (TextView) this.characterTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str4, new float[]{0.0f}));
                    }
                    textView = (TextView) this.dotTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str4, new float[]{0.0f}));
                    }
                }
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                this.dotRunnable = new Runnable() {
                    public void run() {
                        if (AnimatingTextView.this.dotRunnable == this) {
                            ArrayList arrayList = new ArrayList();
                            TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(length);
                            String str = "scaleX";
                            arrayList.add(ObjectAnimator.ofFloat(textView, str, new float[]{0.0f}));
                            String str2 = "scaleY";
                            arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                            String str3 = "alpha";
                            arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                            textView = (TextView) AnimatingTextView.this.dotTextViews.get(length);
                            arrayList.add(ObjectAnimator.ofFloat(textView, str, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{1.0f}));
                            AnimatingTextView.this.currentAnimation = new AnimatorSet();
                            AnimatingTextView.this.currentAnimation.setDuration(150);
                            AnimatingTextView.this.currentAnimation.playTogether(arrayList);
                            AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                                        AnimatingTextView.this.currentAnimation = null;
                                    }
                                }
                            });
                            AnimatingTextView.this.currentAnimation.start();
                        }
                    }
                };
                AndroidUtilities.runOnUIThread(this.dotRunnable, 1500);
                for (i = 0; i < length; i++) {
                    TextView textView2 = (TextView) this.characterTextViews.get(i);
                    String str6 = "translationX";
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str6, new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str2, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str3, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str4, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str5, new float[]{0.0f}));
                    textView2 = (TextView) this.dotTextViews.get(i);
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str6, new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str2, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str3, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str4, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView2, str5, new float[]{0.0f}));
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                            AnimatingTextView.this.currentAnimation = null;
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
                String str;
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                ArrayList arrayList = new ArrayList();
                int length = this.stringBuilder.length() - 1;
                if (length != 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                int i = length;
                while (true) {
                    str = "translationX";
                    if (i >= 4) {
                        break;
                    }
                    TextView textView = (TextView) this.characterTextViews.get(i);
                    String str2 = "translationY";
                    String str3 = "alpha";
                    String str4 = "scaleY";
                    String str5 = "scaleX";
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, str5, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str4, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str, new float[]{(float) getXForTextView(i)}));
                    }
                    textView = (TextView) this.dotTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, str5, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str4, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, str, new float[]{(float) getXForTextView(i)}));
                    }
                    i++;
                }
                if (length == 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (i = 0; i < length; i++) {
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.characterTextViews.get(i), str, new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.dotTextViews.get(i), str, new float[]{(float) getXForTextView(i)}));
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
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                            AnimatingTextView.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        }

        private void eraseAllCharacters(boolean z) {
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
                StringBuilder stringBuilder = this.stringBuilder;
                int i = 0;
                stringBuilder.delete(0, stringBuilder.length());
                if (z) {
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = 0; i2 < 4; i2++) {
                        TextView textView = (TextView) this.characterTextViews.get(i2);
                        String str = "alpha";
                        String str2 = "scaleY";
                        String str3 = "scaleX";
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, str, new float[]{0.0f}));
                        }
                        textView = (TextView) this.dotTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, str3, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, str2, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, str, new float[]{0.0f}));
                        }
                    }
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.playTogether(arrayList);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator)) {
                                AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    while (i < 4) {
                        ((TextView) this.characterTextViews.get(i)).setAlpha(0.0f);
                        ((TextView) this.dotTextViews.get(i)).setAlpha(0.0f);
                        i++;
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
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
                    TextView textView = (TextView) this.characterTextViews.get(i5);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(i5));
                    textView = (TextView) this.dotTextViews.get(i5);
                    textView.setAlpha(1.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(i5));
                } else {
                    ((TextView) this.characterTextViews.get(i5)).setAlpha(0.0f);
                    ((TextView) this.dotTextViews.get(i5)).setAlpha(0.0f);
                }
            }
            super.onLayout(z, i, i2, i3, i4);
        }
    }

    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    public PasscodeView(Context context) {
        Context context2 = context;
        super(context);
        int i = 0;
        setWillNotDraw(false);
        setVisibility(8);
        this.backgroundFrameLayout = new FrameLayout(context2);
        addView(this.backgroundFrameLayout);
        LayoutParams layoutParams = (LayoutParams) this.backgroundFrameLayout.getLayoutParams();
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
        imageView.setImageResource(NUM);
        this.passwordFrameLayout.addView(imageView);
        LayoutParams layoutParams2 = (LayoutParams) imageView.getLayoutParams();
        if (AndroidUtilities.density < 1.0f) {
            layoutParams2.width = AndroidUtilities.dp(30.0f);
            layoutParams2.height = AndroidUtilities.dp(30.0f);
        } else {
            layoutParams2.width = AndroidUtilities.dp(40.0f);
            layoutParams2.height = AndroidUtilities.dp(40.0f);
        }
        layoutParams2.gravity = 81;
        layoutParams2.bottomMargin = AndroidUtilities.dp(100.0f);
        imageView.setLayoutParams(layoutParams2);
        this.passcodeTextView = new TextView(context2);
        this.passcodeTextView.setTextColor(-1);
        this.passcodeTextView.setTextSize(1, 14.0f);
        this.passcodeTextView.setGravity(1);
        this.passwordFrameLayout.addView(this.passcodeTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 62.0f));
        this.retryTextView = new TextView(context2);
        this.retryTextView.setTextColor(-1);
        this.retryTextView.setTextSize(1, 15.0f);
        this.retryTextView.setGravity(1);
        this.retryTextView.setVisibility(4);
        addView(this.retryTextView, LayoutHelper.createFrame(-2, -2, 17));
        this.passwordEditText2 = new AnimatingTextView(context2);
        this.passwordFrameLayout.addView(this.passwordEditText2);
        layoutParams = (LayoutParams) this.passwordEditText2.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(6.0f);
        layoutParams.gravity = 81;
        this.passwordEditText2.setLayoutParams(layoutParams);
        this.passwordEditText = new EditTextBoldCursor(context2);
        this.passwordEditText.setTextSize(1, 36.0f);
        this.passwordEditText.setTextColor(-1);
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setImeOptions(6);
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setBackgroundDrawable(null);
        this.passwordEditText.setCursorColor(-1);
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(32.0f));
        this.passwordFrameLayout.addView(this.passwordEditText);
        layoutParams = (LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams.gravity = 81;
        this.passwordEditText.setLayoutParams(layoutParams);
        this.passwordEditText.setOnEditorActionListener(new -$$Lambda$PasscodeView$9N26YW5Tms2wrtUaIRATApaOSjo(this));
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
        this.passwordEditText.setCustomSelectionActionModeCallback(new Callback() {
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
        this.checkImage = new ImageView(context2);
        this.checkImage.setImageResource(NUM);
        this.checkImage.setScaleType(ScaleType.CENTER);
        this.checkImage.setBackgroundResource(NUM);
        this.passwordFrameLayout.addView(this.checkImage);
        layoutParams = (LayoutParams) this.checkImage.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(60.0f);
        layoutParams.height = AndroidUtilities.dp(60.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
        layoutParams.gravity = 85;
        this.checkImage.setLayoutParams(layoutParams);
        this.checkImage.setContentDescription(LocaleController.getString("Done", NUM));
        this.checkImage.setOnClickListener(new -$$Lambda$PasscodeView$EpKy6ofjnRtgAQLp4vVETcSEcUk(this));
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setBackgroundColor(NUM);
        this.passwordFrameLayout.addView(frameLayout);
        LayoutParams layoutParams3 = (LayoutParams) frameLayout.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = AndroidUtilities.dp(1.0f);
        layoutParams3.gravity = 83;
        layoutParams3.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(20.0f);
        frameLayout.setLayoutParams(layoutParams3);
        this.numbersFrameLayout = new FrameLayout(context2);
        addView(this.numbersFrameLayout);
        layoutParams = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.numbersFrameLayout.setLayoutParams(layoutParams);
        int i2 = 10;
        this.lettersTextViews = new ArrayList(10);
        this.numberTextViews = new ArrayList(10);
        this.numberFrameLayouts = new ArrayList(10);
        for (int i3 = 0; i3 < 10; i3++) {
            TextView textView = new TextView(context2);
            textView.setTextColor(-1);
            textView.setTextSize(1, 36.0f);
            textView.setGravity(17);
            textView.setText(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i3)}));
            this.numbersFrameLayout.addView(textView);
            LayoutParams layoutParams4 = (LayoutParams) textView.getLayoutParams();
            layoutParams4.width = AndroidUtilities.dp(50.0f);
            layoutParams4.height = AndroidUtilities.dp(50.0f);
            layoutParams4.gravity = 51;
            textView.setLayoutParams(layoutParams4);
            textView.setImportantForAccessibility(2);
            this.numberTextViews.add(textView);
            textView = new TextView(context2);
            textView.setTextSize(1, 12.0f);
            textView.setTextColor(Integer.MAX_VALUE);
            textView.setGravity(17);
            this.numbersFrameLayout.addView(textView);
            layoutParams4 = (LayoutParams) textView.getLayoutParams();
            layoutParams4.width = AndroidUtilities.dp(50.0f);
            layoutParams4.height = AndroidUtilities.dp(20.0f);
            layoutParams4.gravity = 51;
            textView.setLayoutParams(layoutParams4);
            textView.setImportantForAccessibility(2);
            if (i3 != 0) {
                switch (i3) {
                    case 2:
                        textView.setText("ABC");
                        break;
                    case 3:
                        textView.setText("DEF");
                        break;
                    case 4:
                        textView.setText("GHI");
                        break;
                    case 5:
                        textView.setText("JKL");
                        break;
                    case 6:
                        textView.setText("MNO");
                        break;
                    case 7:
                        textView.setText("PQRS");
                        break;
                    case 8:
                        textView.setText("TUV");
                        break;
                    case 9:
                        textView.setText("WXYZ");
                        break;
                    default:
                        break;
                }
            }
            textView.setText("+");
            this.lettersTextViews.add(textView);
        }
        this.eraseView = new ImageView(context2);
        this.eraseView.setScaleType(ScaleType.CENTER);
        this.eraseView.setImageResource(NUM);
        this.numbersFrameLayout.addView(this.eraseView);
        layoutParams = (LayoutParams) this.eraseView.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(50.0f);
        layoutParams.height = AndroidUtilities.dp(50.0f);
        layoutParams.gravity = 51;
        this.eraseView.setLayoutParams(layoutParams);
        while (i < 11) {
            AnonymousClass3 anonymousClass3 = new FrameLayout(context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    accessibilityNodeInfo.setClassName("android.widget.Button");
                }
            };
            anonymousClass3.setBackgroundResource(NUM);
            anonymousClass3.setTag(Integer.valueOf(i));
            if (i == 10) {
                anonymousClass3.setOnLongClickListener(new -$$Lambda$PasscodeView$dQyZ_TMnXOE3cSzmJWAd_Tm7_ow(this));
                anonymousClass3.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
                setNextFocus(anonymousClass3, NUM);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(i);
                stringBuilder.append("");
                anonymousClass3.setContentDescription(stringBuilder.toString());
                if (i == 0) {
                    setNextFocus(anonymousClass3, NUM);
                } else if (i == 9) {
                    setNextFocus(anonymousClass3, NUM);
                } else {
                    setNextFocus(anonymousClass3, ids[i + 1]);
                }
            }
            anonymousClass3.setId(ids[i]);
            anonymousClass3.setOnClickListener(new -$$Lambda$PasscodeView$9JJmImU9HuNDiFCYKoINiryr54k(this));
            this.numberFrameLayouts.add(anonymousClass3);
            i++;
        }
        while (i2 >= 0) {
            FrameLayout frameLayout2 = (FrameLayout) this.numberFrameLayouts.get(i2);
            this.numbersFrameLayout.addView(frameLayout2);
            LayoutParams layoutParams5 = (LayoutParams) frameLayout2.getLayoutParams();
            layoutParams5.width = AndroidUtilities.dp(100.0f);
            layoutParams5.height = AndroidUtilities.dp(100.0f);
            layoutParams5.gravity = 51;
            frameLayout2.setLayoutParams(layoutParams5);
            i2--;
        }
    }

    public /* synthetic */ boolean lambda$new$0$PasscodeView(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone(false);
        return true;
    }

    public /* synthetic */ void lambda$new$1$PasscodeView(View view) {
        processDone(false);
    }

    public /* synthetic */ boolean lambda$new$2$PasscodeView(View view) {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        return true;
    }

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
        if (VERSION.SDK_INT >= 22) {
            view.setAccessibilityTraversalBefore(i);
        }
    }

    public void setDelegate(PasscodeViewDelegate passcodeViewDelegate) {
        this.delegate = passcodeViewDelegate;
    }

    private void processDone(boolean z) {
        if (!z) {
            if (SharedConfig.passcodeRetryInMs <= 0) {
                int i = SharedConfig.passcodeType;
                String str = "";
                String string = i == 0 ? this.passwordEditText2.getString() : i == 1 ? this.passwordEditText.getText().toString() : str;
                if (string.length() == 0) {
                    onPasscodeError();
                    return;
                } else if (!SharedConfig.checkPasscode(string)) {
                    SharedConfig.increaseBadPasscodeTries();
                    if (SharedConfig.passcodeRetryInMs > 0) {
                        checkRetryTextView();
                    }
                    this.passwordEditText.setText(str);
                    this.passwordEditText2.eraseAllCharacters(true);
                    onPasscodeError();
                    return;
                }
            }
            return;
        }
        SharedConfig.badPasscodeTries = 0;
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        r2 = new Animator[2];
        r2[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)});
        r2[1] = ObjectAnimator.ofFloat(this, "alpha", new float[]{(float) AndroidUtilities.dp(0.0f)});
        animatorSet.playTogether(r2);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PasscodeView.this.setVisibility(8);
            }
        });
        animatorSet.start();
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener(null);
        PasscodeViewDelegate passcodeViewDelegate = this.delegate;
        if (passcodeViewDelegate != null) {
            passcodeViewDelegate.didAcceptedPassword();
        }
    }

    private void shakeTextView(final float f, final int i) {
        if (i != 6) {
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.passcodeTextView, "translationX", new float[]{(float) AndroidUtilities.dp(f)});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PasscodeView.this.shakeTextView(i == 5 ? 0.0f : -f, i + 1);
                }
            });
            animatorSet.start();
        }
    }

    private void checkRetryTextView() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime > SharedConfig.lastUptimeMillis) {
            SharedConfig.passcodeRetryInMs -= elapsedRealtime - SharedConfig.lastUptimeMillis;
            if (SharedConfig.passcodeRetryInMs < 0) {
                SharedConfig.passcodeRetryInMs = 0;
            }
        }
        SharedConfig.lastUptimeMillis = elapsedRealtime;
        SharedConfig.saveConfig();
        elapsedRealtime = SharedConfig.passcodeRetryInMs;
        int max;
        if (elapsedRealtime > 0) {
            double d = (double) elapsedRealtime;
            Double.isNaN(d);
            max = Math.max(1, (int) Math.ceil(d / 1000.0d));
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
            max = SharedConfig.passcodeType;
            if (max == 0) {
                this.numbersFrameLayout.setVisibility(0);
            } else if (max == 1) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$PasscodeView$NTxy4jfUAbP6Dz3o64FD_qu_vbw(this), 200);
            }
            checkFingerprint();
        }
    }

    public /* synthetic */ void lambda$onResume$4$PasscodeView() {
        if (this.retryTextView.getVisibility() != 0) {
            EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        }
    }

    public void onPause() {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        AlertDialog alertDialog = this.fingerprintDialog;
        if (alertDialog != null) {
            try {
                if (alertDialog.isShowing()) {
                    this.fingerprintDialog.dismiss();
                }
                this.fingerprintDialog = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            if (VERSION.SDK_INT >= 23 && this.cancellationSignal != null) {
                this.cancellationSignal.cancel();
                this.cancellationSignal = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    private void checkFingerprint() {
        String str = "dialogTextBlack";
        Activity activity = (Activity) getContext();
        if (VERSION.SDK_INT >= 23 && activity != null && SharedConfig.useFingerprint && !ApplicationLoader.mainInterfacePaused) {
            try {
                if (this.fingerprintDialog != null && this.fingerprintDialog.isShowing()) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                FingerprintManagerCompat from = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (from.isHardwareDetected() && from.hasEnrolledFingerprints()) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    TextView textView = new TextView(getContext());
                    textView.setId(1000);
                    textView.setTextAppearance(16974344);
                    textView.setTextColor(Theme.getColor(str));
                    textView.setText(LocaleController.getString("FingerprintInfo", NUM));
                    relativeLayout.addView(textView);
                    RelativeLayout.LayoutParams createRelative = LayoutHelper.createRelative(-2, -2);
                    createRelative.addRule(10);
                    createRelative.addRule(20);
                    textView.setLayoutParams(createRelative);
                    this.fingerprintImageView = new ImageView(getContext());
                    this.fingerprintImageView.setImageResource(NUM);
                    this.fingerprintImageView.setId(1001);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, 1000));
                    this.fingerprintStatusTextView = new TextView(getContext());
                    this.fingerprintStatusTextView.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", NUM));
                    this.fingerprintStatusTextView.setTextAppearance(16974320);
                    this.fingerprintStatusTextView.setTextColor(Theme.getColor(str) & NUM);
                    relativeLayout.addView(this.fingerprintStatusTextView);
                    RelativeLayout.LayoutParams createRelative2 = LayoutHelper.createRelative(-2, -2);
                    createRelative2.setMarginStart(AndroidUtilities.dp(16.0f));
                    createRelative2.addRule(8, 1001);
                    createRelative2.addRule(6, 1001);
                    createRelative2.addRule(17, 1001);
                    this.fingerprintStatusTextView.setLayoutParams(createRelative2);
                    Builder builder = new Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    builder.setOnDismissListener(new -$$Lambda$PasscodeView$TCcTv4oyYUzBTnrh4HFlzxWR-gM(this));
                    if (this.fingerprintDialog != null) {
                        if (this.fingerprintDialog.isShowing()) {
                            this.fingerprintDialog.dismiss();
                        }
                    }
                    this.fingerprintDialog = builder.show();
                    this.cancellationSignal = new CancellationSignal();
                    this.selfCancelled = false;
                    from.authenticate(null, 0, this.cancellationSignal, new AuthenticationCallback() {
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

                        public void onAuthenticationSucceeded(AuthenticationResult authenticationResult) {
                            try {
                                if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                    PasscodeView.this.fingerprintDialog.dismiss();
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            PasscodeView.this.fingerprintDialog = null;
                            PasscodeView.this.processDone(true);
                        }
                    }, null);
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$checkFingerprint$5$PasscodeView(DialogInterface dialogInterface) {
        CancellationSignal cancellationSignal = this.cancellationSignal;
        if (cancellationSignal != null) {
            this.selfCancelled = true;
            try {
                cancellationSignal.cancel();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.cancellationSignal = null;
        }
    }

    public void onShow() {
        checkRetryTextView();
        Activity activity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (this.retryTextView.getVisibility() != 0) {
                EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
                if (editTextBoldCursor != null) {
                    editTextBoldCursor.requestFocus();
                    AndroidUtilities.showKeyboard(this.passwordEditText);
                }
            }
        } else if (activity != null) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
            }
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
            } else if (Theme.getSelectedBackgroundId() == 1000001) {
                this.backgroundFrameLayout.setBackgroundColor(-11436898);
            } else {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                if (this.backgroundDrawable != null) {
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
            setOnTouchListener(-$$Lambda$PasscodeView$KE0jvuBZJ1oZX5aNavDxSwZtWh0.INSTANCE);
        }
    }

    private void showFingerprintError(CharSequence charSequence) {
        this.fingerprintImageView.setImageResource(NUM);
        this.fingerprintStatusTextView.setText(charSequence);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        int dp;
        int i3;
        LayoutParams layoutParams;
        int size = MeasureSpec.getSize(i);
        int i4 = 0;
        int i5 = AndroidUtilities.displaySize.y - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
        if (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) {
            if (AndroidUtilities.isTablet()) {
                if (size > AndroidUtilities.dp(498.0f)) {
                    dp = (size - AndroidUtilities.dp(498.0f)) / 2;
                    size = AndroidUtilities.dp(498.0f);
                } else {
                    dp = 0;
                }
                if (i5 > AndroidUtilities.dp(528.0f)) {
                    int i6 = dp;
                    dp = (i5 - AndroidUtilities.dp(528.0f)) / 2;
                    i5 = AndroidUtilities.dp(528.0f);
                    i3 = i6;
                } else {
                    i3 = dp;
                    dp = 0;
                }
            } else {
                dp = 0;
                i3 = 0;
            }
            LayoutParams layoutParams2 = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            int i7 = i5 / 3;
            layoutParams2.height = i7;
            layoutParams2.width = size;
            layoutParams2.topMargin = dp;
            layoutParams2.leftMargin = i3;
            this.passwordFrameLayout.setTag(Integer.valueOf(dp));
            this.passwordFrameLayout.setLayoutParams(layoutParams2);
            layoutParams2 = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
            layoutParams2.height = i7 * 2;
            layoutParams2.leftMargin = i3;
            layoutParams2.topMargin = (i5 - layoutParams2.height) + dp;
            layoutParams2.width = size;
            this.numbersFrameLayout.setLayoutParams(layoutParams2);
            layoutParams = layoutParams2;
        } else {
            layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.width = SharedConfig.passcodeType == 0 ? size / 2 : size;
            layoutParams.height = AndroidUtilities.dp(140.0f);
            layoutParams.topMargin = (i5 - AndroidUtilities.dp(140.0f)) / 2;
            this.passwordFrameLayout.setLayoutParams(layoutParams);
            layoutParams = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
            layoutParams.height = i5;
            size /= 2;
            layoutParams.leftMargin = size;
            layoutParams.topMargin = i5 - layoutParams.height;
            layoutParams.width = size;
            this.numbersFrameLayout.setLayoutParams(layoutParams);
        }
        size = (layoutParams.width - (AndroidUtilities.dp(50.0f) * 3)) / 4;
        dp = (layoutParams.height - (AndroidUtilities.dp(50.0f) * 4)) / 5;
        while (true) {
            int i8 = 11;
            if (i4 < 11) {
                LayoutParams layoutParams3;
                int dp2;
                if (i4 == 0) {
                    i8 = 10;
                } else if (i4 != 10) {
                    i8 = i4 - 1;
                }
                int i9 = i8 / 3;
                i8 %= 3;
                if (i4 < 10) {
                    TextView textView = (TextView) this.numberTextViews.get(i4);
                    TextView textView2 = (TextView) this.lettersTextViews.get(i4);
                    layoutParams3 = (LayoutParams) textView.getLayoutParams();
                    LayoutParams layoutParams4 = (LayoutParams) textView2.getLayoutParams();
                    dp2 = ((AndroidUtilities.dp(50.0f) + dp) * i9) + dp;
                    layoutParams3.topMargin = dp2;
                    layoutParams4.topMargin = dp2;
                    i9 = ((AndroidUtilities.dp(50.0f) + size) * i8) + size;
                    layoutParams3.leftMargin = i9;
                    layoutParams4.leftMargin = i9;
                    layoutParams4.topMargin += AndroidUtilities.dp(40.0f);
                    textView.setLayoutParams(layoutParams3);
                    textView2.setLayoutParams(layoutParams4);
                } else {
                    layoutParams3 = (LayoutParams) this.eraseView.getLayoutParams();
                    i3 = (((AndroidUtilities.dp(50.0f) + dp) * i9) + dp) + AndroidUtilities.dp(8.0f);
                    layoutParams3.topMargin = i3;
                    layoutParams3.leftMargin = ((AndroidUtilities.dp(50.0f) + size) * i8) + size;
                    dp2 = i3 - AndroidUtilities.dp(8.0f);
                    this.eraseView.setLayoutParams(layoutParams3);
                }
                FrameLayout frameLayout = (FrameLayout) this.numberFrameLayouts.get(i4);
                LayoutParams layoutParams5 = (LayoutParams) frameLayout.getLayoutParams();
                layoutParams5.topMargin = dp2 - AndroidUtilities.dp(17.0f);
                layoutParams5.leftMargin = layoutParams3.leftMargin - AndroidUtilities.dp(25.0f);
                frameLayout.setLayoutParams(layoutParams5);
                i4++;
            } else {
                super.onMeasure(i, i2);
                return;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View rootView = getRootView();
        int height = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        Rect rect = this.rect;
        this.keyboardHeight = height - (rect.bottom - rect.top);
        if (SharedConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            int i5 = 0;
            LayoutParams layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
            int intValue = ((this.passwordFrameLayout.getTag() != null ? ((Integer) this.passwordFrameLayout.getTag()).intValue() : 0) + layoutParams.height) - (this.keyboardHeight / 2);
            if (VERSION.SDK_INT >= 21) {
                i5 = AndroidUtilities.statusBarHeight;
            }
            layoutParams.topMargin = intValue - i5;
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            Drawable drawable = this.backgroundDrawable;
            if (drawable == null) {
                super.onDraw(canvas);
            } else if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
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
