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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import drinkless.org.ton.TonApi;
import java.util.ArrayList;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
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
    /* access modifiers changed from: private */
    public static final int[] ids = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    /* access modifiers changed from: private */
    public boolean allowEditing;
    /* access modifiers changed from: private */
    public TextView continueButton;
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
    /* access modifiers changed from: private */
    public UserConfig userConfig;

    private class AnimatingTextView extends FrameLayout {
        private boolean addedNew;
        /* access modifiers changed from: private */
        public ArrayList<TextView> characterTextViews;
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        /* access modifiers changed from: private */
        public Runnable dotRunnable;
        /* access modifiers changed from: private */
        public ArrayList<TextView> dotTextViews;
        private int maxCharactrers;
        private Paint paint = new Paint(1);
        private RectF rect = new RectF();
        private StringBuilder stringBuilder;

        public AnimatingTextView(Context context, int i) {
            super(context);
            setWillNotDraw(false);
            this.maxCharactrers = i;
            this.characterTextViews = new ArrayList<>(4);
            this.dotTextViews = new ArrayList<>(4);
            this.stringBuilder = new StringBuilder(4);
            for (int i2 = 0; i2 < 4; i2++) {
                addTextView();
            }
        }

        private void addTextView() {
            TextView textView = new TextView(getContext());
            textView.setTextColor(Theme.getColor("wallet_whiteText"));
            textView.setTextSize(1, 22.0f);
            textView.setGravity(17);
            textView.setAlpha(0.0f);
            addView(textView, LayoutHelper.createFrame(44, 44, 51));
            this.characterTextViews.add(textView);
            TextView textView2 = new TextView(getContext());
            textView2.setTextColor(Theme.getColor("wallet_whiteText"));
            textView2.setTextSize(1, 22.0f);
            textView2.setGravity(17);
            textView2.setAlpha(0.0f);
            textView2.setText("•");
            textView2.setPivotX((float) AndroidUtilities.dp(25.0f));
            textView2.setPivotY((float) AndroidUtilities.dp(25.0f));
            addView(textView2, LayoutHelper.createFrame(44, 44, 51));
            this.dotTextViews.add(textView2);
        }

        private int getXForTextView(int i) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(20.0f))) / 2) + (i * AndroidUtilities.dp(20.0f))) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String str) {
            if (this.stringBuilder.length() != this.maxCharactrers) {
                if (this.stringBuilder.length() == this.characterTextViews.size()) {
                    addTextView();
                    this.addedNew = true;
                }
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
                int size = this.characterTextViews.size();
                for (int i = length + 1; i < size; i++) {
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
                this.dotRunnable = new Runnable() {
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
                AndroidUtilities.runOnUIThread(this.dotRunnable, 1500);
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
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
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
                int size = this.characterTextViews.size();
                for (int i = length; i < size; i++) {
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
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
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
                    int size = this.characterTextViews.size();
                    for (int i = 0; i < size; i++) {
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
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(150);
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
                int size2 = this.characterTextViews.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    this.characterTextViews.get(i2).setAlpha(0.0f);
                    this.dotTextViews.get(i2).setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (!this.addedNew) {
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
                for (int i5 = 0; i5 < size; i5++) {
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
            } else {
                this.addedNew = false;
            }
            super.onLayout(z, i, i2, i3, i4);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.paint.setColor(Theme.getColor("wallet_grayBackground"));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(22.0f), this.paint);
        }
    }

    private class PasscodeView extends FrameLayout {
        private ImageView checkImage;
        /* access modifiers changed from: private */
        public Runnable checkRunnable = new Runnable() {
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

        static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PasscodeView(org.telegram.ui.Wallet.WalletPasscodeActivity r22, android.content.Context r23, int r24) {
            /*
                r21 = this;
                r0 = r21
                r1 = r22
                r2 = r23
                r0.this$0 = r1
                r0.<init>(r2)
                android.graphics.Rect r3 = new android.graphics.Rect
                r3.<init>()
                r0.rect = r3
                org.telegram.ui.Wallet.WalletPasscodeActivity$PasscodeView$4 r3 = new org.telegram.ui.Wallet.WalletPasscodeActivity$PasscodeView$4
                r3.<init>()
                r0.checkRunnable = r3
                java.lang.String r3 = "wallet_blackBackground"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r3)
                org.telegram.ui.Wallet.-$$Lambda$WalletPasscodeActivity$PasscodeView$fWDR3yJgz6-FpZ8nTP2cp7FY0BU r3 = org.telegram.ui.Wallet.$$Lambda$WalletPasscodeActivity$PasscodeView$fWDR3yJgz6FpZ8nTP2cp7FY0BU.INSTANCE
                r0.setOnTouchListener(r3)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r2)
                r0.passwordFrameLayout = r3
                android.widget.FrameLayout r3 = r0.passwordFrameLayout
                r4 = -1
                r5 = 51
                r6 = 250(0xfa, float:3.5E-43)
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r5)
                r0.addView(r3, r6)
                org.telegram.ui.Components.RLottieImageView r3 = new org.telegram.ui.Components.RLottieImageView
                r3.<init>(r2)
                r0.lottieImageView = r3
                org.telegram.ui.Components.RLottieImageView r3 = r0.lottieImageView
                r6 = 1
                r3.setAutoRepeat(r6)
                org.telegram.ui.Components.RLottieImageView r3 = r0.lottieImageView
                android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r7)
                org.telegram.ui.Components.RLottieImageView r3 = r0.lottieImageView
                r7 = 120(0x78, float:1.68E-43)
                r8 = 2131558423(0x7f0d0017, float:1.8742161E38)
                r3.setAnimation(r8, r7, r7)
                android.widget.FrameLayout r3 = r0.passwordFrameLayout
                org.telegram.ui.Components.RLottieImageView r7 = r0.lottieImageView
                r8 = 120(0x78, float:1.68E-43)
                r9 = 1123024896(0x42var_, float:120.0)
                r10 = 49
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r3.addView(r7, r8)
                org.telegram.ui.Components.RLottieImageView r3 = r0.lottieImageView
                r3.playAnimation()
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.passcodeTextView = r3
                android.widget.TextView r3 = r0.passcodeTextView
                java.lang.String r7 = "wallet_whiteText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r3.setTextColor(r8)
                android.widget.TextView r3 = r0.passcodeTextView
                r8 = 1100480512(0x41980000, float:19.0)
                r3.setTextSize(r6, r8)
                android.widget.TextView r3 = r0.passcodeTextView
                java.lang.String r8 = "WalletEnterPasscode"
                r9 = 2131627030(0x7f0e0CLASSNAME, float:1.8881313E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)
                r3.setText(r8)
                android.widget.TextView r3 = r0.passcodeTextView
                r3.setGravity(r6)
                android.widget.FrameLayout r3 = r0.passwordFrameLayout
                android.widget.TextView r8 = r0.passcodeTextView
                r9 = -2
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 49
                r13 = 1123024896(0x42var_, float:120.0)
                r15 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r3.addView(r8, r9)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.passcodeInfoTextView = r3
                android.widget.TextView r3 = r0.passcodeInfoTextView
                java.lang.String r8 = "wallet_grayText2"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r3.setTextColor(r9)
                android.widget.TextView r3 = r0.passcodeInfoTextView
                r9 = 1097859072(0x41700000, float:15.0)
                r3.setTextSize(r6, r9)
                android.widget.TextView r3 = r0.passcodeInfoTextView
                r3.setGravity(r6)
                org.telegram.messenger.UserConfig r3 = r22.userConfig
                int r3 = r3.tonPasscodeType
                java.lang.String r10 = "Digits"
                r11 = 2131627046(0x7f0e0CLASSNAME, float:1.8881345E38)
                java.lang.String r12 = "WalletPasscodeLength"
                r13 = 0
                if (r3 != 0) goto L_0x00f7
                android.widget.TextView r3 = r0.passcodeInfoTextView
                java.lang.Object[] r14 = new java.lang.Object[r6]
                r15 = 4
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r15)
                r14[r13] = r10
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r12, r11, r14)
                r3.setText(r10)
                goto L_0x0119
            L_0x00f7:
                org.telegram.messenger.UserConfig r3 = r22.userConfig
                int r3 = r3.tonPasscodeType
                if (r3 != r6) goto L_0x0112
                android.widget.TextView r3 = r0.passcodeInfoTextView
                java.lang.Object[] r14 = new java.lang.Object[r6]
                r15 = 6
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r15)
                r14[r13] = r10
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r12, r11, r14)
                r3.setText(r10)
                goto L_0x0119
            L_0x0112:
                android.widget.TextView r3 = r0.passcodeInfoTextView
                r10 = 8
                r3.setVisibility(r10)
            L_0x0119:
                android.widget.FrameLayout r3 = r0.passwordFrameLayout
                android.widget.TextView r10 = r0.passcodeInfoTextView
                r14 = -2
                r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r16 = 49
                r17 = 0
                r18 = 1125515264(0x43160000, float:150.0)
                r19 = 0
                r20 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                r3.addView(r10, r11)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.retryTextView = r3
                android.widget.TextView r3 = r0.retryTextView
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r3.setTextColor(r10)
                android.widget.TextView r3 = r0.retryTextView
                r3.setTextSize(r6, r9)
                android.widget.TextView r3 = r0.retryTextView
                r3.setGravity(r6)
                android.widget.TextView r3 = r0.retryTextView
                r9 = 4
                r3.setVisibility(r9)
                android.widget.TextView r3 = r0.retryTextView
                r9 = -2
                r10 = -2
                r11 = 17
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r10, (int) r11)
                r0.addView(r3, r9)
                org.telegram.ui.Wallet.WalletPasscodeActivity$AnimatingTextView r3 = new org.telegram.ui.Wallet.WalletPasscodeActivity$AnimatingTextView
                r9 = r24
                r3.<init>(r2, r9)
                r0.passwordEditText = r3
                android.widget.FrameLayout r3 = r0.passwordFrameLayout
                org.telegram.ui.Wallet.WalletPasscodeActivity$AnimatingTextView r9 = r0.passwordEditText
                r14 = -1
                r15 = 1110441984(0x42300000, float:44.0)
                r17 = 1099431936(0x41880000, float:17.0)
                r18 = 1128792064(0x43480000, float:200.0)
                r19 = 1099431936(0x41880000, float:17.0)
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                r3.addView(r9, r10)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r2)
                r0.numbersFrameLayout = r3
                android.widget.FrameLayout r3 = r0.numbersFrameLayout
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r5)
                r0.addView(r3, r4)
                java.util.ArrayList r3 = new java.util.ArrayList
                r4 = 10
                r3.<init>(r4)
                r0.lettersTextViews = r3
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>(r4)
                r0.numberTextViews = r3
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>(r4)
                r0.numberFrameLayouts = r3
                r3 = 0
            L_0x01a3:
                r9 = 2
                r10 = 50
                if (r3 >= r4) goto L_0x0247
                android.widget.TextView r12 = new android.widget.TextView
                r12.<init>(r2)
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r12.setTextColor(r14)
                r14 = 1108344832(0x42100000, float:36.0)
                r12.setTextSize(r6, r14)
                r12.setGravity(r11)
                java.util.Locale r14 = java.util.Locale.US
                java.lang.Object[] r15 = new java.lang.Object[r6]
                java.lang.Integer r16 = java.lang.Integer.valueOf(r3)
                r15[r13] = r16
                java.lang.String r13 = "%d"
                java.lang.String r13 = java.lang.String.format(r14, r13, r15)
                r12.setText(r13)
                r12.setImportantForAccessibility(r9)
                android.widget.FrameLayout r13 = r0.numbersFrameLayout
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r5)
                r13.addView(r12, r14)
                java.util.ArrayList<android.widget.TextView> r13 = r0.numberTextViews
                r13.add(r12)
                android.widget.TextView r12 = new android.widget.TextView
                r12.<init>(r2)
                r13 = 1094713344(0x41400000, float:12.0)
                r12.setTextSize(r6, r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r12.setTextColor(r13)
                r12.setGravity(r11)
                android.widget.FrameLayout r13 = r0.numbersFrameLayout
                r14 = 20
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r14, (int) r5)
                r13.addView(r12, r10)
                r12.setImportantForAccessibility(r9)
                if (r3 == 0) goto L_0x0238
                switch(r3) {
                    case 2: goto L_0x0232;
                    case 3: goto L_0x022c;
                    case 4: goto L_0x0226;
                    case 5: goto L_0x0220;
                    case 6: goto L_0x021a;
                    case 7: goto L_0x0214;
                    case 8: goto L_0x020e;
                    case 9: goto L_0x0208;
                    default: goto L_0x0207;
                }
            L_0x0207:
                goto L_0x023d
            L_0x0208:
                java.lang.String r9 = "WXYZ"
                r12.setText(r9)
                goto L_0x023d
            L_0x020e:
                java.lang.String r9 = "TUV"
                r12.setText(r9)
                goto L_0x023d
            L_0x0214:
                java.lang.String r9 = "PQRS"
                r12.setText(r9)
                goto L_0x023d
            L_0x021a:
                java.lang.String r9 = "MNO"
                r12.setText(r9)
                goto L_0x023d
            L_0x0220:
                java.lang.String r9 = "JKL"
                r12.setText(r9)
                goto L_0x023d
            L_0x0226:
                java.lang.String r9 = "GHI"
                r12.setText(r9)
                goto L_0x023d
            L_0x022c:
                java.lang.String r9 = "DEF"
                r12.setText(r9)
                goto L_0x023d
            L_0x0232:
                java.lang.String r9 = "ABC"
                r12.setText(r9)
                goto L_0x023d
            L_0x0238:
                java.lang.String r9 = "+"
                r12.setText(r9)
            L_0x023d:
                java.util.ArrayList<android.widget.TextView> r9 = r0.lettersTextViews
                r9.add(r12)
                int r3 = r3 + 1
                r13 = 0
                goto L_0x01a3
            L_0x0247:
                android.widget.ImageView r3 = new android.widget.ImageView
                r3.<init>(r2)
                r0.eraseView = r3
                android.widget.ImageView r3 = r0.eraseView
                android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r6)
                android.widget.ImageView r3 = r0.eraseView
                r6 = 2131165940(0x7var_f4, float:1.7946111E38)
                r3.setImageResource(r6)
                android.widget.ImageView r3 = r0.eraseView
                android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
                r6.<init>(r8, r11)
                r3.setColorFilter(r6)
                android.widget.FrameLayout r3 = r0.numbersFrameLayout
                android.widget.ImageView r6 = r0.eraseView
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r5)
                r3.addView(r6, r8)
                android.widget.ImageView r3 = new android.widget.ImageView
                r3.<init>(r2)
                r0.checkImage = r3
                android.widget.ImageView r3 = r0.checkImage
                android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r6)
                android.widget.ImageView r3 = r0.checkImage
                r6 = 2131165756(0x7var_c, float:1.7945738E38)
                r3.setImageResource(r6)
                android.widget.ImageView r3 = r0.checkImage
                android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
                r6.<init>(r7, r8)
                r3.setColorFilter(r6)
                android.widget.ImageView r3 = r0.checkImage
                org.telegram.messenger.UserConfig r6 = r22.userConfig
                int r6 = r6.tonPasscodeType
                if (r6 != r9) goto L_0x02aa
                r13 = 0
                goto L_0x02ac
            L_0x02aa:
                r13 = 8
            L_0x02ac:
                r3.setVisibility(r13)
                android.widget.FrameLayout r3 = r0.numbersFrameLayout
                android.widget.ImageView r6 = r0.checkImage
                r7 = 50
                r8 = 1112014848(0x42480000, float:50.0)
                r9 = 51
                r10 = 0
                r11 = 0
                r12 = 1092616192(0x41200000, float:10.0)
                r13 = 1082130432(0x40800000, float:4.0)
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r3.addView(r6, r7)
                r3 = 0
            L_0x02c7:
                r6 = 12
                if (r3 >= r6) goto L_0x035e
                org.telegram.ui.Wallet.WalletPasscodeActivity$PasscodeView$1 r6 = new org.telegram.ui.Wallet.WalletPasscodeActivity$PasscodeView$1
                r6.<init>(r2, r1)
                r7 = 2131165278(0x7var_e, float:1.7944769E38)
                r6.setBackgroundResource(r7)
                java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
                r6.setTag(r7)
                r7 = 11
                if (r3 != r7) goto L_0x02f4
                r7 = 2131624929(0x7f0e03e1, float:1.8877052E38)
                java.lang.String r8 = "Done"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
                r6.setContentDescription(r7)
                r7 = 2131230858(0x7var_a, float:1.807778E38)
                r0.setNextFocus(r6, r7)
                goto L_0x0344
            L_0x02f4:
                if (r3 != r4) goto L_0x0311
                org.telegram.ui.Wallet.-$$Lambda$WalletPasscodeActivity$PasscodeView$LaE677rtgitugeoJN-BL7oMHwqU r7 = new org.telegram.ui.Wallet.-$$Lambda$WalletPasscodeActivity$PasscodeView$LaE677rtgitugeoJN-BL7oMHwqU
                r7.<init>()
                r6.setOnLongClickListener(r7)
                r7 = 2131623951(0x7f0e000f, float:1.8875068E38)
                java.lang.String r8 = "AccDescrBackspace"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
                r6.setContentDescription(r7)
                r7 = 2131230857(0x7var_, float:1.8077779E38)
                r0.setNextFocus(r6, r7)
                goto L_0x0344
            L_0x0311:
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r3)
                java.lang.String r8 = ""
                r7.append(r8)
                java.lang.String r7 = r7.toString()
                r6.setContentDescription(r7)
                if (r3 != 0) goto L_0x032e
                r7 = 2131230868(0x7var_, float:1.80778E38)
                r0.setNextFocus(r6, r7)
                goto L_0x0344
            L_0x032e:
                r7 = 9
                if (r3 != r7) goto L_0x0339
                r7 = 2131230867(0x7var_, float:1.8077799E38)
                r0.setNextFocus(r6, r7)
                goto L_0x0344
            L_0x0339:
                int[] r7 = org.telegram.ui.Wallet.WalletPasscodeActivity.ids
                int r8 = r3 + 1
                r7 = r7[r8]
                r0.setNextFocus(r6, r7)
            L_0x0344:
                int[] r7 = org.telegram.ui.Wallet.WalletPasscodeActivity.ids
                r7 = r7[r3]
                r6.setId(r7)
                org.telegram.ui.Wallet.-$$Lambda$WalletPasscodeActivity$PasscodeView$nbbLCMOC0l-8cjXePX2oSC6G1qk r7 = new org.telegram.ui.Wallet.-$$Lambda$WalletPasscodeActivity$PasscodeView$nbbLCMOC0l-8cjXePX2oSC6G1qk
                r7.<init>()
                r6.setOnClickListener(r7)
                java.util.ArrayList<android.widget.FrameLayout> r7 = r0.numberFrameLayouts
                r7.add(r6)
                int r3 = r3 + 1
                goto L_0x02c7
            L_0x035e:
                r1 = 11
            L_0x0360:
                if (r1 < 0) goto L_0x037a
                java.util.ArrayList<android.widget.FrameLayout> r2 = r0.numberFrameLayouts
                java.lang.Object r2 = r2.get(r1)
                android.widget.FrameLayout r2 = (android.widget.FrameLayout) r2
                android.widget.FrameLayout r3 = r0.numbersFrameLayout
                r4 = 64
                r6 = 64
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r5)
                r3.addView(r2, r4)
                int r1 = r1 + -1
                goto L_0x0360
            L_0x037a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletPasscodeActivity.PasscodeView.<init>(org.telegram.ui.Wallet.WalletPasscodeActivity, android.content.Context, int):void");
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
            if (Build.VERSION.SDK_INT >= 22) {
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
                boolean unused = this.this$0.swipeBackEnabled = false;
                this.this$0.actionBar.setBackButtonDrawable((Drawable) null);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(200);
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PasscodeView.this.setVisibility(8);
                    }
                });
                animatorSet.start();
                setOnTouchListener((View.OnTouchListener) null);
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

        /* access modifiers changed from: private */
        public void shakeTextView(final float f, final int i) {
            if (i != 6) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.passcodeTextView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(f)})});
                animatorSet.setDuration(50);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PasscodeView.this.shakeTextView(i == 5 ? 0.0f : -f, i + 1);
                    }
                });
                animatorSet.start();
            }
        }

        /* access modifiers changed from: private */
        public void checkRetryTextView() {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (elapsedRealtime > this.this$0.userConfig.tonLastUptimeMillis) {
                this.this$0.userConfig.tonPasscodeRetryInMs -= elapsedRealtime - this.this$0.userConfig.tonLastUptimeMillis;
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

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            FrameLayout.LayoutParams layoutParams;
            int i3;
            FrameLayout.LayoutParams layoutParams2;
            int i4;
            int i5;
            int i6;
            int size = View.MeasureSpec.getSize(i);
            int i7 = 0;
            int i8 = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            int i9 = AndroidUtilities.displaySize.y - i8;
            if (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) {
                if (AndroidUtilities.isTablet()) {
                    if (size > AndroidUtilities.dp(498.0f)) {
                        int dp = AndroidUtilities.dp(498.0f);
                        i5 = (size - AndroidUtilities.dp(498.0f)) / 2;
                        size = dp;
                    } else {
                        i5 = 0;
                    }
                    if (i9 > AndroidUtilities.dp(528.0f)) {
                        i6 = (i9 - AndroidUtilities.dp(528.0f)) / 2;
                        i4 = AndroidUtilities.dp(528.0f);
                    } else {
                        i4 = i9;
                        i6 = 0;
                    }
                } else {
                    i4 = i9;
                    i6 = ActionBar.getCurrentActionBarHeight() + i8;
                    i5 = 0;
                }
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
                layoutParams3.width = size;
                layoutParams3.topMargin = i6;
                layoutParams3.leftMargin = i5;
                int i10 = layoutParams3.height + layoutParams3.topMargin;
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.numbersFrameLayout.getLayoutParams();
                layoutParams4.height = i4 - i10;
                layoutParams4.leftMargin = i5;
                layoutParams4.topMargin = (i4 - layoutParams4.height) + i8;
                layoutParams4.width = size;
                layoutParams = layoutParams4;
            } else {
                FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
                int i11 = size / 2;
                layoutParams5.width = i11;
                layoutParams5.topMargin = ((i9 - layoutParams5.height) / 2) + i8;
                layoutParams = (FrameLayout.LayoutParams) this.numbersFrameLayout.getLayoutParams();
                layoutParams.height = i9;
                layoutParams.leftMargin = i11;
                layoutParams.topMargin = i8;
                layoutParams.width = i11;
            }
            int dp2 = (layoutParams.width - (AndroidUtilities.dp(50.0f) * 3)) / 4;
            int dp3 = (layoutParams.height - (AndroidUtilities.dp(50.0f) * 4)) / 5;
            while (i7 < 12) {
                int i12 = 11;
                if (i7 == 0) {
                    i12 = 10;
                } else if (i7 == 10) {
                    i12 = 9;
                } else if (i7 != 11) {
                    i12 = i7 - 1;
                }
                int i13 = i12 / 3;
                int i14 = i12 % 3;
                if (i7 < 10) {
                    layoutParams2 = (FrameLayout.LayoutParams) this.numberTextViews.get(i7).getLayoutParams();
                    FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) this.lettersTextViews.get(i7).getLayoutParams();
                    i3 = ((AndroidUtilities.dp(50.0f) + dp3) * i13) + dp3;
                    layoutParams2.topMargin = i3;
                    layoutParams6.topMargin = i3;
                    int dp4 = ((AndroidUtilities.dp(50.0f) + dp2) * i14) + dp2;
                    layoutParams2.leftMargin = dp4;
                    layoutParams6.leftMargin = dp4;
                    layoutParams6.topMargin += AndroidUtilities.dp(40.0f);
                } else {
                    layoutParams2 = (FrameLayout.LayoutParams) (i7 == 10 ? this.eraseView : this.checkImage).getLayoutParams();
                    i3 = dp3 + ((AndroidUtilities.dp(50.0f) + dp3) * i13);
                    layoutParams2.topMargin = i3;
                    layoutParams2.leftMargin = ((AndroidUtilities.dp(50.0f) + dp2) * i14) + dp2;
                }
                FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) this.numberFrameLayouts.get(i7).getLayoutParams();
                layoutParams7.topMargin = i3 + AndroidUtilities.dp(1.0f);
                layoutParams7.leftMargin = layoutParams2.leftMargin - AndroidUtilities.dp(7.0f);
                i7++;
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
            this.this$0.userConfig.tonBadPasscodeTries++;
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

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public WalletPasscodeActivity(boolean z, Cipher cipher, String str, String str2, long j, String str3, boolean z2) {
        this(z ? 0 : 3);
        this.fromWallet = str;
        this.toWallet = str2;
        this.sendingAmount = j;
        this.sendingMessage = str3;
        this.hasWalletInBack = z2;
        this.sendingCipher = cipher;
        if (!z) {
            checkPasscode((String) null);
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass1 r0 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (WalletPasscodeActivity.this.continueButton != null && Build.VERSION.SDK_INT >= 21) {
                    ((FrameLayout.LayoutParams) WalletPasscodeActivity.this.continueButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(i, i2);
            }
        };
        r0.setBackButtonImage(NUM);
        r0.setBackgroundDrawable((Drawable) null);
        r0.setTitleColor(Theme.getColor("wallet_whiteText"));
        r0.setItemsColor(Theme.getColor("wallet_whiteText"), false);
        r0.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        r0.setAddToContainer(false);
        r0.setOnTouchListener((View.OnTouchListener) null);
        r0.setOnClickListener((View.OnClickListener) null);
        r0.setClickable(false);
        r0.setFocusable(false);
        r0.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1 && WalletPasscodeActivity.this.swipeBackEnabled) {
                    WalletPasscodeActivity.this.finishFragment();
                }
            }
        });
        return r0;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture()) {
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
        this.continueButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletPasscodeActivity.this.lambda$createView$0$WalletPasscodeActivity(view);
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, -2, 17));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
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
            this.passcodeView = new PasscodeView(this, context, i == 0 ? 4 : i == 1 ? 6 : 32);
            frameLayout.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0f));
            this.passcodeView.onShow();
        }
        frameLayout.addView(this.actionBar);
        if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture()) {
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
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 != null) {
            passcodeView2.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 != null) {
            passcodeView2.onPause();
        }
    }

    public void checkPasscode(String str) {
        this.allowEditing = false;
        int i = this.currentType;
        if (i == 1) {
            if (getParentActivity() != null) {
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
                getTonController().prepareForPasscodeChange(str, new Runnable(alertDialog) {
                    private final /* synthetic */ AlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WalletPasscodeActivity.this.lambda$checkPasscode$1$WalletPasscodeActivity(this.f$1);
                    }
                }, new TonController.ErrorCallback(alertDialog) {
                    private final /* synthetic */ AlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(String str, TonApi.Error error) {
                        WalletPasscodeActivity.this.lambda$checkPasscode$2$WalletPasscodeActivity(this.f$1, str, error);
                    }
                });
            }
        } else if (i == 2) {
            if (getParentActivity() != null) {
                AlertDialog alertDialog2 = new AlertDialog(getParentActivity(), 3);
                alertDialog2.setCanCacnel(false);
                alertDialog2.show();
                getTonController().getSecretWords(str, (Cipher) null, new TonController.WordsCallback(alertDialog2) {
                    private final /* synthetic */ AlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(String[] strArr) {
                        WalletPasscodeActivity.this.lambda$checkPasscode$3$WalletPasscodeActivity(this.f$1, strArr);
                    }
                }, new TonController.ErrorCallback(alertDialog2) {
                    private final /* synthetic */ AlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(String str, TonApi.Error error) {
                        WalletPasscodeActivity.this.lambda$checkPasscode$4$WalletPasscodeActivity(this.f$1, str, error);
                    }
                });
            }
        } else if (i == 0 || i == 3) {
            trySendGrams(str, (TonApi.InputKey) null);
        }
    }

    public /* synthetic */ void lambda$checkPasscode$1$WalletPasscodeActivity(AlertDialog alertDialog) {
        alertDialog.dismiss();
        this.passcodeView.onGoodPasscode(false);
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(8);
        walletCreateActivity.setChangingPasscode();
        presentFragment(walletCreateActivity, true);
    }

    public /* synthetic */ void lambda$checkPasscode$2$WalletPasscodeActivity(AlertDialog alertDialog, String str, TonApi.Error error) {
        this.allowEditing = true;
        alertDialog.dismiss();
        if ("PASSCODE_INVALID".equals(str)) {
            this.passcodeView.onWrongPasscode();
            return;
        }
        String string = LocaleController.getString("Wallet", NUM);
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleController.getString("ErrorOccurred", NUM));
        sb.append("\n");
        if (error != null) {
            str = error.message;
        }
        sb.append(str);
        AlertsCreator.showSimpleAlert(this, string, sb.toString());
    }

    public /* synthetic */ void lambda$checkPasscode$3$WalletPasscodeActivity(AlertDialog alertDialog, String[] strArr) {
        this.passcodeView.onGoodPasscode(false);
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.setSecretWords(strArr);
        presentFragment(walletCreateActivity, true);
    }

    public /* synthetic */ void lambda$checkPasscode$4$WalletPasscodeActivity(AlertDialog alertDialog, String str, TonApi.Error error) {
        this.allowEditing = true;
        alertDialog.dismiss();
        if ("PASSCODE_INVALID".equals(str)) {
            this.passcodeView.onWrongPasscode();
            return;
        }
        String string = LocaleController.getString("Wallet", NUM);
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleController.getString("ErrorOccurred", NUM));
        sb.append("\n");
        if (error != null) {
            str = error.message;
        }
        sb.append(str);
        AlertsCreator.showSimpleAlert(this, string, sb.toString());
    }

    private void trySendGrams(String str, TonApi.InputKey inputKey) {
        AlertDialog alertDialog;
        if (this.currentType == 0 && inputKey == null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        String str2 = str;
        getTonController().sendGrams(str2, this.sendingCipher, inputKey, this.fromWallet, this.toWallet, this.sendingAmount, this.sendingMessage, new Runnable(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WalletPasscodeActivity.this.lambda$trySendGrams$5$WalletPasscodeActivity(this.f$1);
            }
        }, new Runnable(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WalletPasscodeActivity.this.lambda$trySendGrams$6$WalletPasscodeActivity(this.f$1);
            }
        }, new Runnable() {
            public final void run() {
                WalletPasscodeActivity.this.lambda$trySendGrams$7$WalletPasscodeActivity();
            }
        }, new TonController.DangerousCallback(str2) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TonApi.InputKey inputKey) {
                WalletPasscodeActivity.this.lambda$trySendGrams$10$WalletPasscodeActivity(this.f$1, inputKey);
            }
        }, new TonController.ErrorCallback(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(String str, TonApi.Error error) {
                WalletPasscodeActivity.this.lambda$trySendGrams$13$WalletPasscodeActivity(this.f$1, str, error);
            }
        });
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

    public /* synthetic */ void lambda$trySendGrams$10$WalletPasscodeActivity(String str, TonApi.InputKey inputKey) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("WalletSendWarningTitle", NUM));
            builder.setMessage(LocaleController.getString("WalletSendWarningText", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    WalletPasscodeActivity.this.lambda$null$8$WalletPasscodeActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("WalletSendWarningSendAnyway", NUM), new DialogInterface.OnClickListener(str, inputKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ TonApi.InputKey f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    WalletPasscodeActivity.this.lambda$null$9$WalletPasscodeActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$8$WalletPasscodeActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public /* synthetic */ void lambda$null$9$WalletPasscodeActivity(String str, TonApi.InputKey inputKey, DialogInterface dialogInterface, int i) {
        trySendGrams(str, inputKey);
    }

    public /* synthetic */ void lambda$trySendGrams$13$WalletPasscodeActivity(AlertDialog alertDialog, String str, TonApi.Error error) {
        this.allowEditing = true;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if ("PASSCODE_INVALID".equals(str)) {
            this.passcodeView.onWrongPasscode();
        } else if (error == null || !error.message.startsWith("NOT_ENOUGH_FUNDS")) {
            Activity parentActivity = getParentActivity();
            String string = LocaleController.getString("Wallet", NUM);
            StringBuilder sb = new StringBuilder();
            sb.append(LocaleController.getString("ErrorOccurred", NUM));
            sb.append("\n");
            if (error != null) {
                str = error.message;
            }
            sb.append(str);
            showDialog(AlertsCreator.createSimpleAlert(parentActivity, string, sb.toString()).create(), new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    WalletPasscodeActivity.this.lambda$null$12$WalletPasscodeActivity(dialogInterface);
                }
            });
        } else {
            showDialog(AlertsCreator.createSimpleAlert(getParentActivity(), LocaleController.getString("WalletInsufficientGramsTitle", NUM), LocaleController.getString("WalletInsufficientGramsText", NUM)).create(), new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    WalletPasscodeActivity.this.lambda$null$11$WalletPasscodeActivity(dialogInterface);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$11$WalletPasscodeActivity(DialogInterface dialogInterface) {
        finishFragment();
    }

    public /* synthetic */ void lambda$null$12$WalletPasscodeActivity(DialogInterface dialogInterface) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void openFinishedFragment() {
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(9);
        String formatString = LocaleController.formatString("WalletSendDoneText", NUM, TonController.formatCurrency(this.sendingAmount));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
        spannableStringBuilder.append("\n\n");
        int length = formatString.length();
        String str = this.toWallet;
        SpannableStringBuilder append = spannableStringBuilder.append(str.substring(0, str.length() / 2)).append(10);
        String str2 = this.toWallet;
        append.append(str2.substring(str2.length() / 2));
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmono.ttf")), length, spannableStringBuilder.length(), 33);
        walletCreateActivity.setSendText(spannableStringBuilder, this.hasWalletInBack);
        if (!presentFragment(walletCreateActivity, true)) {
            this.failedToOpenFinished = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (this.failedToOpenFinished && z && !z2) {
            int i = this.currentType;
            if ((i == 0 || i == 3) && this.sendingFinished) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletPasscodeActivity.this.openFinishedFragment();
                    }
                });
            }
        }
    }

    public boolean onBackPressed() {
        return this.swipeBackEnabled;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarWhiteSelector"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6")};
    }
}
