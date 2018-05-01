package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.AlertDialog;
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
    class C12241 implements OnEditorActionListener {
        C12241() {
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
    class C12252 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C12252() {
        }

        public void afterTextChanged(Editable editable) {
            if (PasscodeView.this.passwordEditText.length() == 4 && SharedConfig.passcodeType == null) {
                PasscodeView.this.processDone(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$3 */
    class C12263 implements Callback {
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

        C12263() {
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$4 */
    class C12274 implements OnClickListener {
        C12274() {
        }

        public void onClick(View view) {
            PasscodeView.this.processDone(false);
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$5 */
    class C12285 implements OnLongClickListener {
        C12285() {
        }

        public boolean onLongClick(View view) {
            PasscodeView.this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            PasscodeView.this.passwordEditText2.eraseAllCharacters(true);
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$6 */
    class C12296 implements OnClickListener {
        C12296() {
        }

        public void onClick(View view) {
            switch (((Integer) view.getTag()).intValue()) {
                case null:
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
    class C12307 extends AnimatorListenerAdapter {
        C12307() {
        }

        public void onAnimationEnd(Animator animator) {
            PasscodeView.this.setVisibility(8);
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$9 */
    class C12329 implements Runnable {
        C12329() {
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
        class C12352 extends AnimatorListenerAdapter {
            C12352() {
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator) != null) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$3 */
        class C12363 extends AnimatorListenerAdapter {
            C12363() {
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator) != null) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$4 */
        class C12374 extends AnimatorListenerAdapter {
            C12374() {
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator) != null) {
                    AnimatingTextView.this.currentAnimation = null;
                }
            }
        }

        public AnimatingTextView(Context context) {
            super(context);
            for (PasscodeView passcodeView = null; passcodeView < 4; passcodeView++) {
                View textView = new TextView(context);
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
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (i * AndroidUtilities.dp(30.0f))) - AndroidUtilities.dp(NUM);
        }

        public void appendCharacter(String str) {
            if (this.stringBuilder.length() != 4) {
                try {
                    performHapticFeedback(3);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                Collection arrayList = new ArrayList();
                final int length = this.stringBuilder.length();
                this.stringBuilder.append(str);
                TextView textView = (TextView) this.characterTextViews.get(length);
                textView.setText(str);
                textView.setTranslationX((float) getXForTextView(length));
                arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                TextView textView2 = (TextView) this.dotTextViews.get(length);
                textView2.setTranslationX((float) getXForTextView(length));
                textView2.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(textView2, "scaleX", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView2, "scaleY", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(textView2, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (str = length + 1; str < 4; str++) {
                    textView = (TextView) this.characterTextViews.get(str);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    }
                    textView = (TextView) this.dotTextViews.get(str);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                    }
                }
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                }
                this.dotRunnable = new Runnable() {

                    /* renamed from: org.telegram.ui.Components.PasscodeView$AnimatingTextView$1$1 */
                    class C12331 extends AnimatorListenerAdapter {
                        C12331() {
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animator) != null) {
                                AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    }

                    public void run() {
                        if (AnimatingTextView.this.dotRunnable == this) {
                            Collection arrayList = new ArrayList();
                            TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(length);
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                            textView = (TextView) AnimatingTextView.this.dotTextViews.get(length);
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{1.0f}));
                            AnimatingTextView.this.currentAnimation = new AnimatorSet();
                            AnimatingTextView.this.currentAnimation.setDuration(150);
                            AnimatingTextView.this.currentAnimation.playTogether(arrayList);
                            AnimatingTextView.this.currentAnimation.addListener(new C12331());
                            AnimatingTextView.this.currentAnimation.start();
                        }
                    }
                };
                AndroidUtilities.runOnUIThread(this.dotRunnable, 1500);
                for (str = null; str < length; str++) {
                    TextView textView3 = (TextView) this.characterTextViews.get(str);
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "translationX", new float[]{(float) getXForTextView(str)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "scaleX", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "scaleY", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "alpha", new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "translationY", new float[]{0.0f}));
                    textView3 = (TextView) this.dotTextViews.get(str);
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "translationX", new float[]{(float) getXForTextView(str)}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "scaleX", new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "scaleY", new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "alpha", new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(textView3, "translationY", new float[]{0.0f}));
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(150);
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new C12352());
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
                int i;
                try {
                    performHapticFeedback(3);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                Collection arrayList = new ArrayList();
                int length = this.stringBuilder.length() - 1;
                if (length != 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (i = length; i < 4; i++) {
                    TextView textView = (TextView) this.characterTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(i)}));
                    }
                    textView = (TextView) this.dotTextViews.get(i);
                    if (textView.getAlpha() != 0.0f) {
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(i)}));
                    }
                }
                if (length == 0) {
                    this.stringBuilder.deleteCharAt(length);
                }
                for (i = 0; i < length; i++) {
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.characterTextViews.get(i), "translationX", new float[]{(float) getXForTextView(i)}));
                    arrayList.add(ObjectAnimator.ofFloat((TextView) this.dotTextViews.get(i), "translationX", new float[]{(float) getXForTextView(i)}));
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
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.addListener(new C12363());
                this.currentAnimation.start();
            }
        }

        private void eraseAllCharacters(boolean z) {
            if (this.stringBuilder.length() != 0) {
                if (this.dotRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                    this.dotRunnable = null;
                }
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                    this.currentAnimation = null;
                }
                int i = 0;
                this.stringBuilder.delete(0, this.stringBuilder.length());
                if (z) {
                    z = new ArrayList();
                    for (int i2 = 0; i2 < 4; i2++) {
                        TextView textView = (TextView) this.characterTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            z.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            z.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            z.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                        textView = (TextView) this.dotTextViews.get(i2);
                        if (textView.getAlpha() != 0.0f) {
                            z.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            z.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            z.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                    }
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.playTogether(z);
                    this.currentAnimation.addListener(new C12374());
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

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (this.dotRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
                this.dotRunnable = null;
            }
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
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
        View imageView = new ImageView(context2);
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setImageResource(C0446R.drawable.passcode_logo);
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
        r0.passwordEditText.setOnEditorActionListener(new C12241());
        r0.passwordEditText.addTextChangedListener(new C12252());
        r0.passwordEditText.setCustomSelectionActionModeCallback(new C12263());
        r0.checkImage = new ImageView(context2);
        r0.checkImage.setImageResource(C0446R.drawable.passcode_check);
        r0.checkImage.setScaleType(ScaleType.CENTER);
        r0.checkImage.setBackgroundResource(C0446R.drawable.bar_selector_lock);
        r0.passwordFrameLayout.addView(r0.checkImage);
        layoutParams = (LayoutParams) r0.checkImage.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(60.0f);
        layoutParams.height = AndroidUtilities.dp(60.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
        layoutParams.gravity = 85;
        r0.checkImage.setLayoutParams(layoutParams);
        r0.checkImage.setOnClickListener(new C12274());
        imageView = new FrameLayout(context2);
        imageView.setBackgroundColor(654311423);
        r0.passwordFrameLayout.addView(imageView);
        layoutParams2 = (LayoutParams) imageView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(1.0f);
        layoutParams2.gravity = 83;
        layoutParams2.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(20.0f);
        imageView.setLayoutParams(layoutParams2);
        r0.numbersFrameLayout = new FrameLayout(context2);
        addView(r0.numbersFrameLayout);
        layoutParams = (LayoutParams) r0.numbersFrameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        r0.numbersFrameLayout.setLayoutParams(layoutParams);
        int i2 = 10;
        r0.lettersTextViews = new ArrayList(10);
        r0.numberTextViews = new ArrayList(10);
        r0.numberFrameLayouts = new ArrayList(10);
        for (int i3 = 0; i3 < 10; i3++) {
            View textView = new TextView(context2);
            textView.setTextColor(-1);
            textView.setTextSize(1, 36.0f);
            textView.setGravity(17);
            textView.setText(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i3)}));
            r0.numbersFrameLayout.addView(textView);
            LayoutParams layoutParams3 = (LayoutParams) textView.getLayoutParams();
            layoutParams3.width = AndroidUtilities.dp(50.0f);
            layoutParams3.height = AndroidUtilities.dp(50.0f);
            layoutParams3.gravity = 51;
            textView.setLayoutParams(layoutParams3);
            r0.numberTextViews.add(textView);
            textView = new TextView(context2);
            textView.setTextSize(1, 12.0f);
            textView.setTextColor(ConnectionsManager.DEFAULT_DATACENTER_ID);
            textView.setGravity(17);
            r0.numbersFrameLayout.addView(textView);
            layoutParams3 = (LayoutParams) textView.getLayoutParams();
            layoutParams3.width = AndroidUtilities.dp(50.0f);
            layoutParams3.height = AndroidUtilities.dp(20.0f);
            layoutParams3.gravity = 51;
            textView.setLayoutParams(layoutParams3);
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
            r0.lettersTextViews.add(textView);
        }
        r0.eraseView = new ImageView(context2);
        r0.eraseView.setScaleType(ScaleType.CENTER);
        r0.eraseView.setImageResource(C0446R.drawable.passcode_delete);
        r0.numbersFrameLayout.addView(r0.eraseView);
        layoutParams = (LayoutParams) r0.eraseView.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(50.0f);
        layoutParams.height = AndroidUtilities.dp(50.0f);
        layoutParams.gravity = 51;
        r0.eraseView.setLayoutParams(layoutParams);
        while (i < 11) {
            FrameLayout frameLayout = new FrameLayout(context2);
            frameLayout.setBackgroundResource(C0446R.drawable.bar_selector_lock);
            frameLayout.setTag(Integer.valueOf(i));
            if (i == 10) {
                frameLayout.setOnLongClickListener(new C12285());
            }
            frameLayout.setOnClickListener(new C12296());
            r0.numberFrameLayouts.add(frameLayout);
            i++;
        }
        while (i2 >= 0) {
            FrameLayout frameLayout2 = (FrameLayout) r0.numberFrameLayouts.get(i2);
            r0.numbersFrameLayout.addView(frameLayout2);
            LayoutParams layoutParams4 = (LayoutParams) frameLayout2.getLayoutParams();
            layoutParams4.width = AndroidUtilities.dp(100.0f);
            layoutParams4.height = AndroidUtilities.dp(100.0f);
            layoutParams4.gravity = 51;
            frameLayout2.setLayoutParams(layoutParams4);
            i2--;
        }
    }

    public void setDelegate(PasscodeViewDelegate passcodeViewDelegate) {
        this.delegate = passcodeViewDelegate;
    }

    private void processDone(boolean z) {
        if (!z) {
            z = TtmlNode.ANONYMOUS_REGION_ID;
            if (SharedConfig.passcodeType == 0) {
                z = this.passwordEditText2.getString();
            } else if (SharedConfig.passcodeType == 1) {
                z = this.passwordEditText.getText().toString();
            }
            if (z.length() == 0) {
                onPasscodeError();
                return;
            } else if (!SharedConfig.checkPasscode(z)) {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.passwordEditText2.eraseAllCharacters(true);
                onPasscodeError();
                return;
            }
        }
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        z = new AnimatorSet();
        z.setDuration(200);
        r1 = new Animator[2];
        r1[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)});
        r1[1] = ObjectAnimator.ofFloat(this, "alpha", new float[]{(float) AndroidUtilities.dp(0.0f)});
        z.playTogether(r1);
        z.addListener(new C12307());
        z.start();
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener(false);
        if (this.delegate) {
            this.delegate.didAcceptedPassword();
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

    private void onPasscodeError() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        shakeTextView(2.0f, 0);
    }

    public void onResume() {
        if (SharedConfig.passcodeType == 1) {
            if (this.passwordEditText != null) {
                this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
            AndroidUtilities.runOnUIThread(new C12329(), 200);
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
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r15 = this;
        r0 = r15.getContext();
        r0 = (android.app.Activity) r0;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 23;
        if (r1 < r2) goto L_0x017a;
    L_0x000c:
        if (r0 == 0) goto L_0x017a;
    L_0x000e:
        r0 = org.telegram.messenger.SharedConfig.useFingerprint;
        if (r0 == 0) goto L_0x017a;
    L_0x0012:
        r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;
        if (r0 != 0) goto L_0x017a;
    L_0x0016:
        r0 = r15.fingerprintDialog;	 Catch:{ Exception -> 0x0023 }
        if (r0 == 0) goto L_0x0027;	 Catch:{ Exception -> 0x0023 }
    L_0x001a:
        r0 = r15.fingerprintDialog;	 Catch:{ Exception -> 0x0023 }
        r0 = r0.isShowing();	 Catch:{ Exception -> 0x0023 }
        if (r0 == 0) goto L_0x0027;
    L_0x0022:
        return;
    L_0x0023:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x0027:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x017a }
        r1 = org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.from(r0);	 Catch:{ Throwable -> 0x017a }
        r0 = r1.isHardwareDetected();	 Catch:{ Throwable -> 0x017a }
        if (r0 == 0) goto L_0x017a;	 Catch:{ Throwable -> 0x017a }
    L_0x0033:
        r0 = r1.hasEnrolledFingerprints();	 Catch:{ Throwable -> 0x017a }
        if (r0 == 0) goto L_0x017a;	 Catch:{ Throwable -> 0x017a }
    L_0x0039:
        r0 = new android.widget.RelativeLayout;	 Catch:{ Throwable -> 0x017a }
        r2 = r15.getContext();	 Catch:{ Throwable -> 0x017a }
        r0.<init>(r2);	 Catch:{ Throwable -> 0x017a }
        r2 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;	 Catch:{ Throwable -> 0x017a }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Throwable -> 0x017a }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Throwable -> 0x017a }
        r4 = 0;	 Catch:{ Throwable -> 0x017a }
        r0.setPadding(r3, r4, r2, r4);	 Catch:{ Throwable -> 0x017a }
        r2 = new android.widget.TextView;	 Catch:{ Throwable -> 0x017a }
        r3 = r15.getContext();	 Catch:{ Throwable -> 0x017a }
        r2.<init>(r3);	 Catch:{ Throwable -> 0x017a }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Throwable -> 0x017a }
        r2.setId(r3);	 Catch:{ Throwable -> 0x017a }
        r3 = 16974344; // 0x1030208 float:2.4062357E-38 double:8.38644E-317;	 Catch:{ Throwable -> 0x017a }
        r2.setTextAppearance(r3);	 Catch:{ Throwable -> 0x017a }
        r3 = "dialogTextBlack";	 Catch:{ Throwable -> 0x017a }
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);	 Catch:{ Throwable -> 0x017a }
        r2.setTextColor(r3);	 Catch:{ Throwable -> 0x017a }
        r3 = "FingerprintInfo";	 Catch:{ Throwable -> 0x017a }
        r5 = NUM; // 0x7f0c02a4 float:1.8610563E38 double:1.0530977324E-314;	 Catch:{ Throwable -> 0x017a }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x017a }
        r2.setText(r3);	 Catch:{ Throwable -> 0x017a }
        r0.addView(r2);	 Catch:{ Throwable -> 0x017a }
        r3 = -2;	 Catch:{ Throwable -> 0x017a }
        r5 = org.telegram.ui.Components.LayoutHelper.createRelative(r3, r3);	 Catch:{ Throwable -> 0x017a }
        r6 = 10;	 Catch:{ Throwable -> 0x017a }
        r5.addRule(r6);	 Catch:{ Throwable -> 0x017a }
        r6 = 20;	 Catch:{ Throwable -> 0x017a }
        r5.addRule(r6);	 Catch:{ Throwable -> 0x017a }
        r2.setLayoutParams(r5);	 Catch:{ Throwable -> 0x017a }
        r2 = new android.widget.ImageView;	 Catch:{ Throwable -> 0x017a }
        r5 = r15.getContext();	 Catch:{ Throwable -> 0x017a }
        r2.<init>(r5);	 Catch:{ Throwable -> 0x017a }
        r15.fingerprintImageView = r2;	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintImageView;	 Catch:{ Throwable -> 0x017a }
        r5 = NUM; // 0x7f0700c3 float:1.7944973E38 double:1.0529355994E-314;	 Catch:{ Throwable -> 0x017a }
        r2.setImageResource(r5);	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintImageView;	 Catch:{ Throwable -> 0x017a }
        r5 = 1001; // 0x3e9 float:1.403E-42 double:4.946E-321;	 Catch:{ Throwable -> 0x017a }
        r2.setId(r5);	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintImageView;	 Catch:{ Throwable -> 0x017a }
        r6 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;	 Catch:{ Throwable -> 0x017a }
        r7 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;	 Catch:{ Throwable -> 0x017a }
        r8 = 0;	 Catch:{ Throwable -> 0x017a }
        r9 = 20;	 Catch:{ Throwable -> 0x017a }
        r10 = 0;	 Catch:{ Throwable -> 0x017a }
        r11 = 0;	 Catch:{ Throwable -> 0x017a }
        r12 = 20;	 Catch:{ Throwable -> 0x017a }
        r13 = 3;	 Catch:{ Throwable -> 0x017a }
        r14 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Throwable -> 0x017a }
        r6 = org.telegram.ui.Components.LayoutHelper.createRelative(r6, r7, r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Throwable -> 0x017a }
        r0.addView(r2, r6);	 Catch:{ Throwable -> 0x017a }
        r2 = new android.widget.TextView;	 Catch:{ Throwable -> 0x017a }
        r6 = r15.getContext();	 Catch:{ Throwable -> 0x017a }
        r2.<init>(r6);	 Catch:{ Throwable -> 0x017a }
        r15.fingerprintStatusTextView = r2;	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintStatusTextView;	 Catch:{ Throwable -> 0x017a }
        r6 = 16;	 Catch:{ Throwable -> 0x017a }
        r2.setGravity(r6);	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintStatusTextView;	 Catch:{ Throwable -> 0x017a }
        r6 = "FingerprintHelp";	 Catch:{ Throwable -> 0x017a }
        r7 = NUM; // 0x7f0c02a3 float:1.861056E38 double:1.053097732E-314;	 Catch:{ Throwable -> 0x017a }
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);	 Catch:{ Throwable -> 0x017a }
        r2.setText(r6);	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintStatusTextView;	 Catch:{ Throwable -> 0x017a }
        r6 = 16974320; // 0x10301f0 float:2.406229E-38 double:8.3864284E-317;	 Catch:{ Throwable -> 0x017a }
        r2.setTextAppearance(r6);	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintStatusTextView;	 Catch:{ Throwable -> 0x017a }
        r6 = "dialogTextBlack";	 Catch:{ Throwable -> 0x017a }
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);	 Catch:{ Throwable -> 0x017a }
        r7 = NUM; // 0x42ffffff float:127.99999 double:5.553660854E-315;	 Catch:{ Throwable -> 0x017a }
        r6 = r6 & r7;	 Catch:{ Throwable -> 0x017a }
        r2.setTextColor(r6);	 Catch:{ Throwable -> 0x017a }
        r2 = r15.fingerprintStatusTextView;	 Catch:{ Throwable -> 0x017a }
        r0.addView(r2);	 Catch:{ Throwable -> 0x017a }
        r2 = org.telegram.ui.Components.LayoutHelper.createRelative(r3, r3);	 Catch:{ Throwable -> 0x017a }
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;	 Catch:{ Throwable -> 0x017a }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x017a }
        r2.setMarginStart(r3);	 Catch:{ Throwable -> 0x017a }
        r3 = 8;	 Catch:{ Throwable -> 0x017a }
        r2.addRule(r3, r5);	 Catch:{ Throwable -> 0x017a }
        r3 = 6;	 Catch:{ Throwable -> 0x017a }
        r2.addRule(r3, r5);	 Catch:{ Throwable -> 0x017a }
        r3 = 17;	 Catch:{ Throwable -> 0x017a }
        r2.addRule(r3, r5);	 Catch:{ Throwable -> 0x017a }
        r3 = r15.fingerprintStatusTextView;	 Catch:{ Throwable -> 0x017a }
        r3.setLayoutParams(r2);	 Catch:{ Throwable -> 0x017a }
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;	 Catch:{ Throwable -> 0x017a }
        r3 = r15.getContext();	 Catch:{ Throwable -> 0x017a }
        r2.<init>(r3);	 Catch:{ Throwable -> 0x017a }
        r3 = "AppName";	 Catch:{ Throwable -> 0x017a }
        r5 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;	 Catch:{ Throwable -> 0x017a }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Throwable -> 0x017a }
        r2.setTitle(r3);	 Catch:{ Throwable -> 0x017a }
        r2.setView(r0);	 Catch:{ Throwable -> 0x017a }
        r0 = "Cancel";	 Catch:{ Throwable -> 0x017a }
        r3 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;	 Catch:{ Throwable -> 0x017a }
        r0 = org.telegram.messenger.LocaleController.getString(r0, r3);	 Catch:{ Throwable -> 0x017a }
        r3 = 0;	 Catch:{ Throwable -> 0x017a }
        r2.setNegativeButton(r0, r3);	 Catch:{ Throwable -> 0x017a }
        r0 = new org.telegram.ui.Components.PasscodeView$10;	 Catch:{ Throwable -> 0x017a }
        r0.<init>();	 Catch:{ Throwable -> 0x017a }
        r2.setOnDismissListener(r0);	 Catch:{ Throwable -> 0x017a }
        r0 = r15.fingerprintDialog;	 Catch:{ Throwable -> 0x017a }
        if (r0 == 0) goto L_0x015e;
    L_0x014c:
        r0 = r15.fingerprintDialog;	 Catch:{ Exception -> 0x015a }
        r0 = r0.isShowing();	 Catch:{ Exception -> 0x015a }
        if (r0 == 0) goto L_0x015e;	 Catch:{ Exception -> 0x015a }
    L_0x0154:
        r0 = r15.fingerprintDialog;	 Catch:{ Exception -> 0x015a }
        r0.dismiss();	 Catch:{ Exception -> 0x015a }
        goto L_0x015e;
    L_0x015a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);	 Catch:{ Throwable -> 0x017a }
    L_0x015e:
        r0 = r2.show();	 Catch:{ Throwable -> 0x017a }
        r15.fingerprintDialog = r0;	 Catch:{ Throwable -> 0x017a }
        r0 = new android.support.v4.os.CancellationSignal;	 Catch:{ Throwable -> 0x017a }
        r0.<init>();	 Catch:{ Throwable -> 0x017a }
        r15.cancellationSignal = r0;	 Catch:{ Throwable -> 0x017a }
        r15.selfCancelled = r4;	 Catch:{ Throwable -> 0x017a }
        r2 = 0;	 Catch:{ Throwable -> 0x017a }
        r3 = 0;	 Catch:{ Throwable -> 0x017a }
        r4 = r15.cancellationSignal;	 Catch:{ Throwable -> 0x017a }
        r5 = new org.telegram.ui.Components.PasscodeView$11;	 Catch:{ Throwable -> 0x017a }
        r5.<init>();	 Catch:{ Throwable -> 0x017a }
        r6 = 0;	 Catch:{ Throwable -> 0x017a }
        r1.authenticate(r2, r3, r4, r5, r6);	 Catch:{ Throwable -> 0x017a }
    L_0x017a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PasscodeView.checkFingerprint():void");
    }

    public void onShow() {
        Activity activity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (this.passwordEditText != null) {
                this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (activity != null) {
            View currentFocus = activity.getCurrentFocus();
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
            this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", C0446R.string.EnterYourPasscode));
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
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
    }

    private void showFingerprintError(CharSequence charSequence) {
        this.fingerprintImageView.setImageResource(C0446R.drawable.ic_fingerprint_error);
        this.fingerprintStatusTextView.setText(charSequence);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    protected void onMeasure(int i, int i2) {
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
                i3 = dp;
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

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View rootView = getRootView();
        int height = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        this.keyboardHeight = height - (this.rect.bottom - this.rect.top);
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

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            if (this.backgroundDrawable == null) {
                super.onDraw(canvas);
            } else if (this.backgroundDrawable instanceof ColorDrawable) {
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
