package org.telegram.ui.Wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import drinkless.org.ton.TonApi.Error;
import drinkless.org.ton.TonApi.InputKey;
import java.util.ArrayList;
import java.util.Locale;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.TypefaceSpan;

public class WalletPasscodeActivity extends BaseFragment {
    public static final int TYPE_NO_PASSCODE_SEND = 3;
    public static final int TYPE_PASSCODE_CHANGE = 1;
    public static final int TYPE_PASSCODE_EXPORT = 2;
    public static final int TYPE_PASSCODE_SEND = 0;
    private static final int[] ids = new int[]{NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    private boolean allowEditing;
    private TextView continueButton;
    private int currentType;
    private TextView descriptionText;
    private boolean failedToOpenFinished;
    private String fromWallet;
    private boolean hasWalletInBack;
    private PasscodeView passcodeView;
    private long sendingAmount;
    private Cipher sendingCipher;
    private boolean sendingFinished;
    private String sendingMessage;
    private TextView titleTextView;
    private String toWallet;
    private UserConfig userConfig;

    private class AnimatingTextView extends FrameLayout {
        private boolean addedNew;
        private ArrayList<TextView> characterTextViews;
        private AnimatorSet currentAnimation;
        private Runnable dotRunnable;
        private ArrayList<TextView> dotTextViews;
        private int maxCharactrers;
        private Paint paint = new Paint(1);
        private RectF rect = new RectF();
        private StringBuilder stringBuilder;

        public AnimatingTextView(Context context, int i) {
            super(context);
            int i2 = 0;
            setWillNotDraw(false);
            this.maxCharactrers = i;
            this.characterTextViews = new ArrayList(4);
            this.dotTextViews = new ArrayList(4);
            this.stringBuilder = new StringBuilder(4);
            while (i2 < 4) {
                addTextView();
                i2++;
            }
        }

        private void addTextView() {
            TextView textView = new TextView(getContext());
            String str = "wallet_whiteText";
            textView.setTextColor(Theme.getColor(str));
            textView.setTextSize(1, 22.0f);
            textView.setGravity(17);
            textView.setAlpha(0.0f);
            addView(textView, LayoutHelper.createFrame(44, 44, 51));
            this.characterTextViews.add(textView);
            textView = new TextView(getContext());
            textView.setTextColor(Theme.getColor(str));
            textView.setTextSize(1, 22.0f);
            textView.setGravity(17);
            textView.setAlpha(0.0f);
            textView.setText("•");
            textView.setPivotX((float) AndroidUtilities.dp(25.0f));
            textView.setPivotY((float) AndroidUtilities.dp(25.0f));
            addView(textView, LayoutHelper.createFrame(44, 44, 51));
            this.dotTextViews.add(textView);
        }

        private int getXForTextView(int i) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(20.0f))) / 2) + (i * AndroidUtilities.dp(20.0f))) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String str) {
            if (this.stringBuilder.length() != this.maxCharactrers) {
                int i;
                if (this.stringBuilder.length() == this.characterTextViews.size()) {
                    addTextView();
                    this.addedNew = true;
                }
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
                arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                TextView textView2 = (TextView) this.dotTextViews.get(length);
                textView2.setTranslationX((float) getXForTextView(length));
                textView2.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                int size = this.characterTextViews.size();
                for (i = length + 1; i < size; i++) {
                    TextView textView3 = (TextView) this.characterTextViews.get(i);
                    if (textView3.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.ALPHA, new float[]{0.0f}));
                    }
                    textView3 = (TextView) this.dotTextViews.get(i);
                    if (textView3.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView3, View.ALPHA, new float[]{0.0f}));
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
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                            textView = (TextView) AnimatingTextView.this.dotTextViews.get(length);
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{1.0f}));
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
                    textView = (TextView) this.characterTextViews.get(i);
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{0.0f}));
                    textView = (TextView) this.dotTextViews.get(i);
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{0.0f}));
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
                int size = this.characterTextViews.size();
                for (int i = length; i < size; i++) {
                    TextView textView = (TextView) this.characterTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, new float[]{(float) getXForTextView(i)}));
                    }
                    textView = (TextView) this.dotTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, new float[]{(float) getXForTextView(i)}));
                    }
                }
                if (length == 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (size = 0; size < length; size++) {
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.characterTextViews.get(size), View.TRANSLATION_X, new float[]{(float) getXForTextView(size)}));
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.dotTextViews.get(size), View.TRANSLATION_X, new float[]{(float) getXForTextView(size)}));
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
                    int size = this.characterTextViews.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TextView textView = (TextView) this.characterTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
                        }
                        textView = (TextView) this.dotTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{0.0f}));
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
                    int size2 = this.characterTextViews.size();
                    while (i < size2) {
                        ((TextView) this.characterTextViews.get(i)).setAlpha(0.0f);
                        ((TextView) this.dotTextViews.get(i)).setAlpha(0.0f);
                        i++;
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5 = 0;
            if (this.addedNew) {
                this.addedNew = false;
            } else {
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
                int size = this.characterTextViews.size();
                while (i5 < size) {
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
                    i5++;
                }
            }
            super.onLayout(z, i, i2, i3, i4);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.paint.setColor(Theme.getColor("wallet_grayBackground"));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(22.0f), this.paint);
        }
    }

    private class PasscodeView extends FrameLayout {
        private ImageView checkImage;
        private Runnable checkRunnable = new Runnable() {
            public void run() {
                PasscodeView.this.checkRetryTextView();
                AndroidUtilities.runOnUIThread(PasscodeView.this.checkRunnable, 100);
            }
        };
        private ImageView eraseView;
        private int lastValue;
        private ArrayList<TextView> lettersTextViews;
        private RLottieImageView lottieImageView;
        private ArrayList<FrameLayout> numberFrameLayouts;
        private ArrayList<TextView> numberTextViews;
        private FrameLayout numbersFrameLayout;
        private TextView passcodeInfoTextView;
        private TextView passcodeTextView;
        private AnimatingTextView passwordEditText;
        private FrameLayout passwordFrameLayout;
        private Rect rect = new Rect();
        private TextView retryTextView;
        final /* synthetic */ WalletPasscodeActivity this$0;

        public PasscodeView(WalletPasscodeActivity walletPasscodeActivity, Context context, int i) {
            final WalletPasscodeActivity walletPasscodeActivity2 = walletPasscodeActivity;
            Context context2 = context;
            this.this$0 = walletPasscodeActivity2;
            super(context2);
            setBackgroundColor(Theme.getColor("wallet_blackBackground"));
            setOnTouchListener(-$$Lambda$WalletPasscodeActivity$PasscodeView$fWDR3yJgz6-FpZ8nTP2cp7FY0BU.INSTANCE);
            this.passwordFrameLayout = new FrameLayout(context2);
            addView(this.passwordFrameLayout, LayoutHelper.createFrame(-1, 250, 51));
            this.lottieImageView = new RLottieImageView(context2);
            this.lottieImageView.setAutoRepeat(true);
            this.lottieImageView.setScaleType(ScaleType.CENTER);
            this.lottieImageView.setAnimation(NUM, 120, 120);
            this.passwordFrameLayout.addView(this.lottieImageView, LayoutHelper.createFrame(120, 120.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
            this.lottieImageView.playAnimation();
            this.passcodeTextView = new TextView(context2);
            String str = "wallet_whiteText";
            this.passcodeTextView.setTextColor(Theme.getColor(str));
            this.passcodeTextView.setTextSize(1, 19.0f);
            this.passcodeTextView.setText(LocaleController.getString("WalletEnterPasscode", NUM));
            this.passcodeTextView.setGravity(1);
            this.passwordFrameLayout.addView(this.passcodeTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 120.0f, 0.0f, 0.0f));
            this.passcodeInfoTextView = new TextView(context2);
            String str2 = "wallet_grayText2";
            this.passcodeInfoTextView.setTextColor(Theme.getColor(str2));
            this.passcodeInfoTextView.setTextSize(1, 15.0f);
            this.passcodeInfoTextView.setGravity(1);
            String str3 = "Digits";
            String str4 = "WalletPasscodeLength";
            int i2 = 0;
            if (walletPasscodeActivity.userConfig.tonPasscodeType == 0) {
                this.passcodeInfoTextView.setText(LocaleController.formatString(str4, NUM, LocaleController.formatPluralString(str3, 4)));
            } else if (walletPasscodeActivity.userConfig.tonPasscodeType == 1) {
                this.passcodeInfoTextView.setText(LocaleController.formatString(str4, NUM, LocaleController.formatPluralString(str3, 6)));
            } else {
                this.passcodeInfoTextView.setVisibility(8);
            }
            this.passwordFrameLayout.addView(this.passcodeInfoTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 150.0f, 0.0f, 0.0f));
            this.retryTextView = new TextView(context2);
            this.retryTextView.setTextColor(Theme.getColor(str));
            this.retryTextView.setTextSize(1, 15.0f);
            this.retryTextView.setGravity(1);
            this.retryTextView.setVisibility(4);
            addView(this.retryTextView, LayoutHelper.createFrame(-2, -2, 17));
            this.passwordEditText = new AnimatingTextView(context2, i);
            this.passwordFrameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1, 44.0f, 49, 17.0f, 200.0f, 17.0f, 0.0f));
            this.numbersFrameLayout = new FrameLayout(context2);
            addView(this.numbersFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
            this.lettersTextViews = new ArrayList(10);
            this.numberTextViews = new ArrayList(10);
            this.numberFrameLayouts = new ArrayList(10);
            int i3 = 0;
            while (i3 < 10) {
                TextView textView = new TextView(context2);
                textView.setTextColor(Theme.getColor(str));
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setText(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i3)}));
                textView.setImportantForAccessibility(2);
                this.numbersFrameLayout.addView(textView, LayoutHelper.createFrame(50, 50, 51));
                this.numberTextViews.add(textView);
                textView = new TextView(context2);
                textView.setTextSize(1, 12.0f);
                textView.setTextColor(Theme.getColor(str2));
                textView.setGravity(17);
                this.numbersFrameLayout.addView(textView, LayoutHelper.createFrame(50, 20, 51));
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
                i3++;
                i2 = 0;
            }
            this.eraseView = new ImageView(context2);
            this.eraseView.setScaleType(ScaleType.CENTER);
            this.eraseView.setImageResource(NUM);
            this.eraseView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.numbersFrameLayout.addView(this.eraseView, LayoutHelper.createFrame(50, 50, 51));
            this.checkImage = new ImageView(context2);
            this.checkImage.setScaleType(ScaleType.CENTER);
            this.checkImage.setImageResource(NUM);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.checkImage.setVisibility(walletPasscodeActivity.userConfig.tonPasscodeType == 2 ? 0 : 8);
            this.numbersFrameLayout.addView(this.checkImage, LayoutHelper.createFrame(50, 50.0f, 51, 0.0f, 0.0f, 10.0f, 4.0f));
            for (i3 = 0; i3 < 12; i3++) {
                AnonymousClass1 anonymousClass1 = new FrameLayout(context2) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setClassName("android.widget.Button");
                    }
                };
                anonymousClass1.setBackgroundResource(NUM);
                anonymousClass1.setTag(Integer.valueOf(i3));
                if (i3 == 11) {
                    anonymousClass1.setContentDescription(LocaleController.getString("Done", NUM));
                    setNextFocus(anonymousClass1, NUM);
                } else if (i3 == 10) {
                    anonymousClass1.setOnLongClickListener(new -$$Lambda$WalletPasscodeActivity$PasscodeView$LaE677rtgitugeoJN-BL7oMHwqU(this));
                    anonymousClass1.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
                    setNextFocus(anonymousClass1, NUM);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(i3);
                    stringBuilder.append("");
                    anonymousClass1.setContentDescription(stringBuilder.toString());
                    if (i3 == 0) {
                        setNextFocus(anonymousClass1, NUM);
                    } else if (i3 == 9) {
                        setNextFocus(anonymousClass1, NUM);
                    } else {
                        setNextFocus(anonymousClass1, WalletPasscodeActivity.ids[i3 + 1]);
                    }
                }
                anonymousClass1.setId(WalletPasscodeActivity.ids[i3]);
                anonymousClass1.setOnClickListener(new -$$Lambda$WalletPasscodeActivity$PasscodeView$nbbLCMOC0l-8cjXePX2oSC6G1qk(this));
                this.numberFrameLayouts.add(anonymousClass1);
            }
            for (int i4 = 11; i4 >= 0; i4--) {
                this.numbersFrameLayout.addView((FrameLayout) this.numberFrameLayouts.get(i4), LayoutHelper.createFrame(64, 64, 51));
            }
        }

        public /* synthetic */ boolean lambda$new$1$WalletPasscodeActivity$PasscodeView(View view) {
            this.passwordEditText.eraseAllCharacters(true);
            return true;
        }

        public /* synthetic */ void lambda$new$2$WalletPasscodeActivity$PasscodeView(View view) {
            if (this.this$0.allowEditing) {
                switch (((Integer) view.getTag()).intValue()) {
                    case 0:
                        this.passwordEditText.appendCharacter("0");
                        break;
                    case 1:
                        this.passwordEditText.appendCharacter("1");
                        break;
                    case 2:
                        this.passwordEditText.appendCharacter("2");
                        break;
                    case 3:
                        this.passwordEditText.appendCharacter("3");
                        break;
                    case 4:
                        this.passwordEditText.appendCharacter("4");
                        break;
                    case 5:
                        this.passwordEditText.appendCharacter("5");
                        break;
                    case 6:
                        this.passwordEditText.appendCharacter("6");
                        break;
                    case 7:
                        this.passwordEditText.appendCharacter("7");
                        break;
                    case 8:
                        this.passwordEditText.appendCharacter("8");
                        break;
                    case 9:
                        this.passwordEditText.appendCharacter("9");
                        break;
                    case 10:
                        this.passwordEditText.eraseLastCharacter();
                        break;
                    case 11:
                        processDone();
                        break;
                }
                if (this.this$0.userConfig.tonPasscodeType != 2) {
                    if (this.passwordEditText.length() == (this.this$0.userConfig.tonPasscodeType == 0 ? 4 : 6)) {
                        processDone();
                    }
                }
            }
        }

        private void setNextFocus(View view, int i) {
            view.setNextFocusForwardId(i);
            if (VERSION.SDK_INT >= 22) {
                view.setAccessibilityTraversalBefore(i);
            }
        }

        public void onWrongPasscode() {
            increaseBadPasscodeTries();
            if (this.this$0.userConfig.tonPasscodeRetryInMs > 0) {
                checkRetryTextView();
            }
            this.passwordEditText.eraseAllCharacters(true);
            onPasscodeError();
        }

        public void onGoodPasscode(boolean z) {
            this.this$0.userConfig.tonBadPasscodeTries = 0;
            this.this$0.userConfig.saveConfig(false);
            if (z) {
                this.this$0.swipeBackEnabled = false;
                this.this$0.actionBar.setBackButtonDrawable(null);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(200);
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)});
                animatorArr[1] = ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{(float) AndroidUtilities.dp(0.0f)});
                animatorSet.playTogether(animatorArr);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PasscodeView.this.setVisibility(8);
                    }
                });
                animatorSet.start();
                setOnTouchListener(null);
            }
        }

        private void processDone() {
            if (this.this$0.userConfig.tonPasscodeRetryInMs <= 0) {
                String string = this.passwordEditText.getString();
                if (string.length() == 0) {
                    onPasscodeError();
                } else {
                    this.this$0.checkPasscode(string);
                }
            }
        }

        private void shakeTextView(final float f, final int i) {
            if (i != 6) {
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.passcodeTextView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(f)});
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
            if (elapsedRealtime > this.this$0.userConfig.tonLastUptimeMillis) {
                UserConfig access$400 = this.this$0.userConfig;
                access$400.tonPasscodeRetryInMs -= elapsedRealtime - this.this$0.userConfig.tonLastUptimeMillis;
                if (this.this$0.userConfig.tonPasscodeRetryInMs < 0) {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 0;
                }
            }
            this.this$0.userConfig.tonLastUptimeMillis = elapsedRealtime;
            this.this$0.userConfig.saveConfig(false);
            if (this.this$0.userConfig.tonPasscodeRetryInMs > 0) {
                double d = (double) this.this$0.userConfig.tonPasscodeRetryInMs;
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
                this.numbersFrameLayout.setVisibility(0);
            }
        }

        public void onResume() {
            checkRetryTextView();
        }

        public void onPause() {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        }

        public void onShow() {
            checkRetryTextView();
            if (this.retryTextView.getVisibility() != 0) {
                this.numbersFrameLayout.setVisibility(0);
            }
            this.passwordEditText.eraseAllCharacters(false);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int dp;
            LayoutParams layoutParams;
            int size = MeasureSpec.getSize(i);
            int i3 = 0;
            int i4 = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            int i5 = AndroidUtilities.displaySize.y - i4;
            if (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) {
                int dp2;
                if (AndroidUtilities.isTablet()) {
                    if (size > AndroidUtilities.dp(498.0f)) {
                        dp = (size - AndroidUtilities.dp(498.0f)) / 2;
                        size = AndroidUtilities.dp(498.0f);
                    } else {
                        dp = 0;
                    }
                    if (i5 > AndroidUtilities.dp(528.0f)) {
                        i5 = (i5 - AndroidUtilities.dp(528.0f)) / 2;
                        dp2 = AndroidUtilities.dp(528.0f);
                    } else {
                        dp2 = i5;
                        i5 = 0;
                    }
                } else {
                    dp2 = i5;
                    i5 = ActionBar.getCurrentActionBarHeight() + i4;
                    dp = 0;
                }
                LayoutParams layoutParams2 = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
                layoutParams2.width = size;
                layoutParams2.topMargin = i5;
                layoutParams2.leftMargin = dp;
                i5 = layoutParams2.height + layoutParams2.topMargin;
                layoutParams2 = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
                layoutParams2.height = dp2 - i5;
                layoutParams2.leftMargin = dp;
                layoutParams2.topMargin = (dp2 - layoutParams2.height) + i4;
                layoutParams2.width = size;
                layoutParams = layoutParams2;
            } else {
                layoutParams = (LayoutParams) this.passwordFrameLayout.getLayoutParams();
                size /= 2;
                layoutParams.width = size;
                layoutParams.topMargin = ((i5 - layoutParams.height) / 2) + i4;
                layoutParams = (LayoutParams) this.numbersFrameLayout.getLayoutParams();
                layoutParams.height = i5;
                layoutParams.leftMargin = size;
                layoutParams.topMargin = i4;
                layoutParams.width = size;
            }
            size = (layoutParams.width - (AndroidUtilities.dp(50.0f) * 3)) / 4;
            i5 = (layoutParams.height - (AndroidUtilities.dp(50.0f) * 4)) / 5;
            while (i3 < 12) {
                LayoutParams layoutParams3;
                int dp3;
                dp = 11;
                if (i3 == 0) {
                    dp = 10;
                } else if (i3 == 10) {
                    dp = 9;
                } else if (i3 != 11) {
                    dp = i3 - 1;
                }
                int i6 = dp / 3;
                dp %= 3;
                if (i3 < 10) {
                    layoutParams3 = (LayoutParams) ((TextView) this.numberTextViews.get(i3)).getLayoutParams();
                    LayoutParams layoutParams4 = (LayoutParams) ((TextView) this.lettersTextViews.get(i3)).getLayoutParams();
                    dp3 = ((AndroidUtilities.dp(50.0f) + i5) * i6) + i5;
                    layoutParams3.topMargin = dp3;
                    layoutParams4.topMargin = dp3;
                    i6 = ((AndroidUtilities.dp(50.0f) + size) * dp) + size;
                    layoutParams3.leftMargin = i6;
                    layoutParams4.leftMargin = i6;
                    layoutParams4.topMargin += AndroidUtilities.dp(40.0f);
                } else {
                    layoutParams3 = (LayoutParams) (i3 == 10 ? this.eraseView : this.checkImage).getLayoutParams();
                    dp3 = i5 + ((AndroidUtilities.dp(50.0f) + i5) * i6);
                    layoutParams3.topMargin = dp3;
                    layoutParams3.leftMargin = ((AndroidUtilities.dp(50.0f) + size) * dp) + size;
                }
                layoutParams = (LayoutParams) ((FrameLayout) this.numberFrameLayouts.get(i3)).getLayoutParams();
                layoutParams.topMargin = dp3 + AndroidUtilities.dp(1.0f);
                layoutParams.leftMargin = layoutParams3.leftMargin - AndroidUtilities.dp(7.0f);
                i3++;
            }
            super.onMeasure(i, i2);
        }

        private void onPasscodeError() {
            Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            shakeTextView(2.0f, 0);
        }

        private void increaseBadPasscodeTries() {
            UserConfig access$400 = this.this$0.userConfig;
            access$400.tonBadPasscodeTries++;
            if (this.this$0.userConfig.tonBadPasscodeTries >= 3) {
                int i = this.this$0.userConfig.tonBadPasscodeTries;
                if (i == 3) {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 5000;
                } else if (i == 4) {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 10000;
                } else if (i == 5) {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 15000;
                } else if (i == 6) {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 20000;
                } else if (i != 7) {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 30000;
                } else {
                    this.this$0.userConfig.tonPasscodeRetryInMs = 25000;
                }
                this.this$0.userConfig.tonLastUptimeMillis = SystemClock.elapsedRealtime();
            }
        }
    }

    public WalletPasscodeActivity(int i) {
        this.userConfig = getUserConfig();
        this.allowEditing = true;
        this.currentType = i;
    }

    public WalletPasscodeActivity(boolean z, Cipher cipher, String str, String str2, long j, String str3, boolean z2) {
        this(z ? 0 : 3);
        this.fromWallet = str;
        this.toWallet = str2;
        this.sendingAmount = j;
        this.sendingMessage = str3;
        this.hasWalletInBack = z2;
        this.sendingCipher = cipher;
        if (!z) {
            checkPasscode(null);
        }
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass1 anonymousClass1 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                if (WalletPasscodeActivity.this.continueButton != null && VERSION.SDK_INT >= 21) {
                    ((LayoutParams) WalletPasscodeActivity.this.continueButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(i, i2);
            }
        };
        anonymousClass1.setBackButtonImage(NUM);
        anonymousClass1.setBackgroundDrawable(null);
        String str = "wallet_whiteText";
        anonymousClass1.setTitleColor(Theme.getColor(str));
        anonymousClass1.setItemsColor(Theme.getColor(str), false);
        anonymousClass1.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        anonymousClass1.setAddToContainer(false);
        anonymousClass1.setOnTouchListener(null);
        anonymousClass1.setOnClickListener(null);
        anonymousClass1.setClickable(false);
        anonymousClass1.setFocusable(false);
        anonymousClass1.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1 && WalletPasscodeActivity.this.swipeBackEnabled) {
                    WalletPasscodeActivity.this.finishFragment();
                }
            }
        });
        return anonymousClass1;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture()) {
            AndroidUtilities.setFlagSecure(this, false);
        }
    }

    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.fragmentView = frameLayout;
        this.continueButton = new TextView(context);
        this.continueButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
        this.continueButton.setTextSize(1, 14.0f);
        this.continueButton.setText(LocaleController.getString("WalletClose", NUM));
        this.continueButton.setGravity(16);
        this.continueButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.continueButton.setAlpha(0.0f);
        this.continueButton.setVisibility(0);
        this.actionBar.addView(this.continueButton, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 22.0f, 0.0f));
        this.continueButton.setOnClickListener(new -$$Lambda$WalletPasscodeActivity$WKd3wEz2eE54L9DsjyQg15DQmeE(this));
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, -2, 17));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setScaleType(ScaleType.CENTER);
        rLottieImageView.setAutoRepeat(true);
        rLottieImageView.setAnimation(NUM, 130, 130);
        rLottieImageView.playAnimation();
        frameLayout2.addView(rLottieImageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        this.titleTextView.setText(LocaleController.getString("WalletSendingGrams", NUM));
        frameLayout2.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 130.0f, 0.0f, 0.0f));
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText.setText(LocaleController.getString("WalletSendingGramsInfo", NUM));
        frameLayout2.addView(this.descriptionText, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 200.0f, 0.0f, 0.0f));
        if (this.currentType != 3) {
            int i = this.userConfig.tonPasscodeType;
            i = i == 0 ? 4 : i == 1 ? 6 : 32;
            this.passcodeView = new PasscodeView(this, context, i);
            frameLayout.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0f));
            this.passcodeView.onShow();
        }
        frameLayout.addView(this.actionBar);
        if (VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture()) {
            AndroidUtilities.setFlagSecure(this, true);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$WalletPasscodeActivity(View view) {
        getTonController().cancelShortPoll();
        if (this.hasWalletInBack) {
            finishFragment();
        } else {
            presentFragment(new WalletActivity(), true);
        }
    }

    public void onResume() {
        super.onResume();
        PasscodeView passcodeView = this.passcodeView;
        if (passcodeView != null) {
            passcodeView.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        PasscodeView passcodeView = this.passcodeView;
        if (passcodeView != null) {
            passcodeView.onPause();
        }
    }

    public void checkPasscode(String str) {
        this.allowEditing = false;
        int i = this.currentType;
        AlertDialog alertDialog;
        if (i == 1) {
            if (getParentActivity() != null) {
                alertDialog = new AlertDialog(getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
                getTonController().prepareForPasscodeChange(str, new -$$Lambda$WalletPasscodeActivity$BI-fMXpnL5z8rR5QNvwUCcV_-FU(this, alertDialog), new -$$Lambda$WalletPasscodeActivity$ua4Q0UpSSX6xvhcB9pfaCss5klk(this, alertDialog));
            }
        } else if (i == 2) {
            if (getParentActivity() != null) {
                alertDialog = new AlertDialog(getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
                getTonController().getSecretWords(str, null, new -$$Lambda$WalletPasscodeActivity$6t39g59yWrccW6Nn-Y0ftETkiCA(this, alertDialog), new -$$Lambda$WalletPasscodeActivity$Uqb52jkhFxpMrtSlXrJGl13eFHU(this, alertDialog));
            }
        } else if (i == 0 || i == 3) {
            trySendGrams(str, null);
        }
    }

    public /* synthetic */ void lambda$checkPasscode$1$WalletPasscodeActivity(AlertDialog alertDialog) {
        alertDialog.dismiss();
        this.passcodeView.onGoodPasscode(false);
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(8);
        walletCreateActivity.setChangingPasscode();
        presentFragment(walletCreateActivity, true);
    }

    public /* synthetic */ void lambda$checkPasscode$2$WalletPasscodeActivity(AlertDialog alertDialog, String str, Error error) {
        this.allowEditing = true;
        alertDialog.dismiss();
        if ("PASSCODE_INVALID".equals(str)) {
            this.passcodeView.onWrongPasscode();
            return;
        }
        String string = LocaleController.getString("Wallet", NUM);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        if (error != null) {
            str = error.message;
        }
        stringBuilder.append(str);
        AlertsCreator.showSimpleAlert(this, string, stringBuilder.toString());
    }

    public /* synthetic */ void lambda$checkPasscode$3$WalletPasscodeActivity(AlertDialog alertDialog, String[] strArr) {
        this.passcodeView.onGoodPasscode(false);
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.setSecretWords(strArr);
        presentFragment(walletCreateActivity, true);
    }

    public /* synthetic */ void lambda$checkPasscode$4$WalletPasscodeActivity(AlertDialog alertDialog, String str, Error error) {
        this.allowEditing = true;
        alertDialog.dismiss();
        if ("PASSCODE_INVALID".equals(str)) {
            this.passcodeView.onWrongPasscode();
            return;
        }
        String string = LocaleController.getString("Wallet", NUM);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        if (error != null) {
            str = error.message;
        }
        stringBuilder.append(str);
        AlertsCreator.showSimpleAlert(this, string, stringBuilder.toString());
    }

    private void trySendGrams(String str, InputKey inputKey) {
        AlertDialog alertDialog;
        if (this.currentType == 0 && inputKey == null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        String str2 = str;
        getTonController().sendGrams(str2, this.sendingCipher, inputKey, this.fromWallet, this.toWallet, this.sendingAmount, this.sendingMessage, new -$$Lambda$WalletPasscodeActivity$9Epr4FB8wdpR5Km7TodjPK7RPXg(this, alertDialog), new -$$Lambda$WalletPasscodeActivity$9CbYONqZ1BeOIv-jwZAt1uAar9Y(this, alertDialog), new -$$Lambda$WalletPasscodeActivity$h2SaxgP530F9oGwRFQJtdbBSWaI(this), new -$$Lambda$WalletPasscodeActivity$hMQh41Vr1AYxXbRlz_G1Z21KynY(this, str2), new -$$Lambda$WalletPasscodeActivity$iCX9fEqianebHBHWQAhIHdcuKpo(this, alertDialog));
    }

    public /* synthetic */ void lambda$trySendGrams$5$WalletPasscodeActivity(AlertDialog alertDialog) {
        this.passcodeView.onGoodPasscode(true);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public /* synthetic */ void lambda$trySendGrams$6$WalletPasscodeActivity(AlertDialog alertDialog) {
        this.continueButton.setVisibility(0);
        this.continueButton.animate().alpha(1.0f).setDuration(180).start();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        getTonController().scheduleShortPoll();
    }

    public /* synthetic */ void lambda$trySendGrams$7$WalletPasscodeActivity() {
        this.sendingFinished = true;
        openFinishedFragment();
    }

    public /* synthetic */ void lambda$trySendGrams$10$WalletPasscodeActivity(String str, InputKey inputKey) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("WalletSendWarningTitle", NUM));
            builder.setMessage(LocaleController.getString("WalletSendWarningText", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new -$$Lambda$WalletPasscodeActivity$ySQo1RWSZx1qEdUCCdpWI-QVB5c(this));
            builder.setPositiveButton(LocaleController.getString("WalletSendWarningSendAnyway", NUM), new -$$Lambda$WalletPasscodeActivity$7shTJWRC9JKBj6AMCLzD5KySVGE(this, str, inputKey));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$8$WalletPasscodeActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public /* synthetic */ void lambda$null$9$WalletPasscodeActivity(String str, InputKey inputKey, DialogInterface dialogInterface, int i) {
        trySendGrams(str, inputKey);
    }

    public /* synthetic */ void lambda$trySendGrams$13$WalletPasscodeActivity(AlertDialog alertDialog, String str, Error error) {
        this.allowEditing = true;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if ("PASSCODE_INVALID".equals(str)) {
            this.passcodeView.onWrongPasscode();
        } else if (error == null || !error.message.startsWith("NOT_ENOUGH_FUNDS")) {
            Activity parentActivity = getParentActivity();
            String string = LocaleController.getString("Wallet", NUM);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
            stringBuilder.append("\n");
            if (error != null) {
                str = error.message;
            }
            stringBuilder.append(str);
            showDialog(AlertsCreator.createSimpleAlert(parentActivity, string, stringBuilder.toString()).create(), new -$$Lambda$WalletPasscodeActivity$MAKRb-BIqElPPJJSt-lQOPJHDr0(this));
        } else {
            showDialog(AlertsCreator.createSimpleAlert(getParentActivity(), LocaleController.getString("WalletInsufficientGramsTitle", NUM), LocaleController.getString("WalletInsufficientGramsText", NUM)).create(), new -$$Lambda$WalletPasscodeActivity$_N6qGMP92W0rMZhlhbtkmPe_KoQ(this));
        }
    }

    public /* synthetic */ void lambda$null$11$WalletPasscodeActivity(DialogInterface dialogInterface) {
        finishFragment();
    }

    public /* synthetic */ void lambda$null$12$WalletPasscodeActivity(DialogInterface dialogInterface) {
        finishFragment();
    }

    private void openFinishedFragment() {
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(9);
        String formatString = LocaleController.formatString("WalletSendDoneText", NUM, TonController.formatCurrency(this.sendingAmount));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
        spannableStringBuilder.append("\n\n");
        int length = formatString.length();
        String str = this.toWallet;
        SpannableStringBuilder append = spannableStringBuilder.append(str.substring(0, str.length() / 2)).append(10);
        str = this.toWallet;
        append.append(str.substring(str.length() / 2));
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmono.ttf")), length, spannableStringBuilder.length(), 33);
        walletCreateActivity.setSendText(spannableStringBuilder, this.hasWalletInBack);
        if (!presentFragment(walletCreateActivity, true)) {
            this.failedToOpenFinished = true;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (this.failedToOpenFinished && z && !z2) {
            int i = this.currentType;
            if ((i == 0 || i == 3) && this.sendingFinished) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$WalletPasscodeActivity$8Bx1Dn-e4mE3xmvkuc1oLgNdrRM(this));
            }
        }
    }

    public boolean onBackPressed() {
        return this.swipeBackEnabled;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6")};
    }
}
