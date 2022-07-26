package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.PaymentFormActivity;

public class UndoView extends FrameLayout {
    public static int ACTION_RINGTONE_ADDED = 83;
    private float additionalTranslationY;
    private BackupImageView avatarImageView;
    Drawable backgroundDrawable;
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private ArrayList<Long> currentDialogIds;
    private Object currentInfoObject;
    float enterOffset;
    private int enterOffsetMargin;
    private boolean fromTop;
    private int hideAnimationType;
    private CharSequence infoText;
    private TextView infoTextView;
    private boolean isShown;
    private long lastUpdateTime;
    private RLottieImageView leftImageView;
    private BaseFragment parentFragment;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    StaticLayout timeLayout;
    StaticLayout timeLayoutOut;
    private long timeLeft;
    private String timeLeftString;
    float timeReplaceProgress;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showWithAction$3(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canUndo() {
        return true;
    }

    public void didPressUrl(CharacterStyle characterStyle) {
    }

    /* access modifiers changed from: protected */
    public void onRemoveDialogAction(long j, int i) {
    }

    public class LinkMovementMethodMy extends LinkMovementMethod {
        public LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            CharacterStyle[] characterStyleArr;
            try {
                if (motionEvent.getAction() == 0 && ((characterStyleArr = (CharacterStyle[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), CharacterStyle.class)) == null || characterStyleArr.length == 0)) {
                    return false;
                }
                if (motionEvent.getAction() != 1) {
                    return super.onTouchEvent(textView, spannable, motionEvent);
                }
                CharacterStyle[] characterStyleArr2 = (CharacterStyle[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), CharacterStyle.class);
                if (characterStyleArr2 != null && characterStyleArr2.length > 0) {
                    UndoView.this.didPressUrl(characterStyleArr2[0]);
                }
                Selection.removeSelection(spannable);
                return true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public UndoView(Context context) {
        this(context, (BaseFragment) null, false, (Theme.ResourcesProvider) null);
    }

    public UndoView(Context context, BaseFragment baseFragment) {
        this(context, baseFragment, false, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UndoView(Context context, BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.currentAccount = UserConfig.selectedAccount;
        this.currentAction = -1;
        this.hideAnimationType = 1;
        this.enterOffsetMargin = AndroidUtilities.dp(8.0f);
        this.timeReplaceProgress = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = baseFragment;
        this.fromTop = z;
        TextView textView = new TextView(context2);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(getThemedColor("undo_infoColor"));
        this.infoTextView.setLinkTextColor(getThemedColor("undo_cancelColor"));
        this.infoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(getThemedColor("undo_infoColor"));
        this.subinfoTextView.setLinkTextColor(getThemedColor("undo_cancelColor"));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.leftImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.leftImageView.setLayerColor("info1.**", getThemedColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("info2.**", getThemedColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("luCLASSNAME.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc9.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc8.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc7.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc6.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc5.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc4.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc3.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc2.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc1.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("Oval.**", getThemedColor("undo_infoColor"));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(30, 30.0f, 19, 15.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        this.undoButton.setBackground(Theme.createRadSelectorDrawable(getThemedColor("undo_cancelColor") & NUM, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        addView(this.undoButton, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 11.0f, 0.0f));
        this.undoButton.setOnClickListener(new UndoView$$ExternalSyntheticLambda0(this));
        ImageView imageView = new ImageView(context2);
        this.undoImageView = imageView;
        imageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19, 4, 4, 0, 4));
        TextView textView3 = new TextView(context2);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(getThemedColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 4, 8, 4));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(getThemedColor("undo_infoColor"));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setColor(getThemedColor("undo_infoColor"));
        setWillNotDraw(false);
        this.backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("undo_background"));
        setOnTouchListener(UndoView$$ExternalSyntheticLambda3.INSTANCE);
        setVisibility(4);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    public void setColors(int i, int i2) {
        Theme.setDrawableColor(this.backgroundDrawable, i);
        this.infoTextView.setTextColor(i2);
        this.subinfoTextView.setTextColor(i2);
        int i3 = i | -16777216;
        this.leftImageView.setLayerColor("info1.**", i3);
        this.leftImageView.setLayerColor("info2.**", i3);
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77 || i == 44 || i == 78 || i == 79 || i == 100 || i == 101 || i == ACTION_RINGTONE_ADDED;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || i == 74 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty()) || this.currentAction == ACTION_RINGTONE_ADDED;
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24 || i == 74 || i == ACTION_RINGTONE_ADDED;
    }

    public void setAdditionalTranslationY(float f) {
        if (this.additionalTranslationY != f) {
            this.additionalTranslationY = f;
            updatePosition();
        }
    }

    public Object getCurrentInfoObject() {
        return this.currentInfoObject;
    }

    public void hide(boolean z, int i) {
        if (getVisibility() == 0 && this.isShown) {
            this.currentInfoObject = null;
            this.isShown = false;
            Runnable runnable = this.currentActionRunnable;
            if (runnable != null) {
                if (z) {
                    runnable.run();
                }
                this.currentActionRunnable = null;
            }
            Runnable runnable2 = this.currentCancelRunnable;
            if (runnable2 != null) {
                if (!z) {
                    runnable2.run();
                }
                this.currentCancelRunnable = null;
            }
            int i2 = this.currentAction;
            if (i2 == 0 || i2 == 1 || i2 == 26 || i2 == 27) {
                for (int i3 = 0; i3 < this.currentDialogIds.size(); i3++) {
                    long longValue = this.currentDialogIds.get(i3).longValue();
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    int i4 = this.currentAction;
                    instance.removeDialogAction(longValue, i4 == 0 || i4 == 26, z);
                    onRemoveDialogAction(longValue, this.currentAction);
                }
            }
            float f = -1.0f;
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    Animator[] animatorArr = new Animator[1];
                    float[] fArr = new float[1];
                    if (!this.fromTop) {
                        f = 1.0f;
                    }
                    fArr[0] = f * ((float) (this.enterOffsetMargin + this.undoViewHeight));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "enterOffset", fArr);
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(250);
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        UndoView.this.setVisibility(4);
                        UndoView.this.setScaleX(1.0f);
                        UndoView.this.setScaleY(1.0f);
                        UndoView.this.setAlpha(1.0f);
                    }
                });
                animatorSet.start();
                return;
            }
            if (!this.fromTop) {
                f = 1.0f;
            }
            setEnterOffset(f * ((float) (this.enterOffsetMargin + this.undoViewHeight)));
            setVisibility(4);
        }
    }

    public void showWithAction(long j, int i, Runnable runnable) {
        showWithAction(j, i, (Object) null, (Object) null, runnable, (Runnable) null);
    }

    public void showWithAction(long j, int i, Object obj) {
        showWithAction(j, i, obj, (Object) null, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long j, int i, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, (Object) null, (Object) null, runnable, runnable2);
    }

    public void showWithAction(long j, int i, Object obj, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, obj, (Object) null, runnable, runnable2);
    }

    public void showWithAction(long j, int i, Object obj, Object obj2, Runnable runnable, Runnable runnable2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Long.valueOf(j));
        showWithAction((ArrayList<Long>) arrayList, i, obj, obj2, runnable, runnable2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:565:0x1697  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x16b7  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x16de  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x1723  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x17cd  */
    /* JADX WARNING: Removed duplicated region for block: B:626:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r20, int r21, java.lang.Object r22, java.lang.Object r23, java.lang.Runnable r24, java.lang.Runnable r25) {
        /*
            r19 = this;
            r1 = r19
            r0 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            boolean r7 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            r8 = 33
            if (r7 != 0) goto L_0x0037
            int r7 = r1.currentAction
            r9 = 52
            if (r7 == r9) goto L_0x0036
            r9 = 56
            if (r7 == r9) goto L_0x0036
            r9 = 57
            if (r7 == r9) goto L_0x0036
            r9 = 58
            if (r7 == r9) goto L_0x0036
            r9 = 59
            if (r7 == r9) goto L_0x0036
            r9 = 60
            if (r7 == r9) goto L_0x0036
            r9 = 80
            if (r7 == r9) goto L_0x0036
            if (r7 != r8) goto L_0x0037
        L_0x0036:
            return
        L_0x0037:
            java.lang.Runnable r7 = r1.currentActionRunnable
            if (r7 == 0) goto L_0x003e
            r7.run()
        L_0x003e:
            r7 = 1
            r1.isShown = r7
            r1.currentActionRunnable = r5
            r1.currentCancelRunnable = r6
            r1.currentDialogIds = r0
            r9 = 0
            java.lang.Object r10 = r0.get(r9)
            java.lang.Long r10 = (java.lang.Long) r10
            long r10 = r10.longValue()
            r1.currentAction = r2
            r12 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r12
            r1.currentInfoObject = r3
            long r12 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r12
            android.widget.TextView r12 = r1.undoTextView
            r13 = 2131628805(0x7f0e1305, float:1.8884913E38)
            java.lang.String r14 = "Undo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            java.lang.String r13 = r13.toUpperCase()
            r12.setText(r13)
            android.widget.ImageView r12 = r1.undoImageView
            r12.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r12 = r1.leftImageView
            r12.setPadding(r9, r9, r9, r9)
            android.widget.TextView r12 = r1.infoTextView
            r13 = 1097859072(0x41700000, float:15.0)
            r12.setTextSize(r7, r13)
            org.telegram.ui.Components.BackupImageView r12 = r1.avatarImageView
            r14 = 8
            r12.setVisibility(r14)
            android.widget.TextView r12 = r1.infoTextView
            r15 = 51
            r12.setGravity(r15)
            android.widget.TextView r12 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r12 = r12.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r12 = (android.widget.FrameLayout.LayoutParams) r12
            r15 = -2
            r12.height = r15
            r15 = 1095761920(0x41500000, float:13.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r12.topMargin = r15
            r12.bottomMargin = r9
            org.telegram.ui.Components.RLottieImageView r15 = r1.leftImageView
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r15.setScaleType(r13)
            org.telegram.ui.Components.RLottieImageView r13 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            r15 = 19
            r13.gravity = r15
            r13.bottomMargin = r9
            r13.topMargin = r9
            r15 = 1077936128(0x40400000, float:3.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r13.leftMargin = r15
            r15 = 1113063424(0x42580000, float:54.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r13.width = r15
            r15 = -2
            r13.height = r15
            android.widget.TextView r15 = r1.infoTextView
            r15.setMinHeight(r9)
            r15 = 0
            if (r5 != 0) goto L_0x00da
            if (r6 == 0) goto L_0x00de
        L_0x00da:
            int r6 = ACTION_RINGTONE_ADDED
            if (r2 != r6) goto L_0x00ea
        L_0x00de:
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r6.<init>(r1)
            r1.setOnClickListener(r6)
            r1.setOnTouchListener(r15)
            goto L_0x00f2
        L_0x00ea:
            r1.setOnClickListener(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r6 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r1.setOnTouchListener(r6)
        L_0x00f2:
            android.widget.TextView r6 = r1.infoTextView
            r6.setMovementMethod(r15)
            boolean r6 = r19.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r14 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r7 = 36
            if (r6 == 0) goto L_0x08e7
            int r0 = ACTION_RINGTONE_ADDED
            if (r2 != r0) goto L_0x0135
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            r0 = 2131628457(0x7f0e11a9, float:1.8884207E38)
            java.lang.String r2 = "SoundAdded"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131628458(0x7f0e11aa, float:1.888421E38)
            java.lang.String r3 = "SoundAddedSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r5)
            r1.currentActionRunnable = r15
            r3 = 2131558550(0x7f0d0096, float:1.8742419E38)
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            r15 = r2
            r2 = 2131558550(0x7f0d0096, float:1.8742419E38)
            goto L_0x0840
        L_0x0135:
            r0 = 74
            if (r2 != r0) goto L_0x015b
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            r0 = 2131628008(0x7f0e0fe8, float:1.8883297E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131628017(0x7f0e0ff1, float:1.8883315E38)
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r2 = 2131558462(0x7f0d003e, float:1.874224E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x015b:
            r0 = 34
            if (r2 != r0) goto L_0x01bb
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x0182
            r2 = 2131629055(0x7f0e13ff, float:1.888542E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r5
            java.lang.String r5 = "VoipChannelInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0198
        L_0x0182:
            r3 = 1
            r2 = 2131629144(0x7f0e1458, float:1.88856E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r3
            java.lang.String r3 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x0198:
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r0, r3)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r9)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            r0 = r2
        L_0x01b8:
            r2 = 0
            goto L_0x0840
        L_0x01bb:
            r0 = 44
            if (r2 != r0) goto L_0x0252
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01fd
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01e6
            r0 = 2131629080(0x7f0e1418, float:1.888547E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r9] = r2
            java.lang.String r2 = "VoipChannelUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x022f
        L_0x01e6:
            r4 = 1
            r0 = 2131629095(0x7f0e1427, float:1.8885501E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r9] = r2
            java.lang.String r2 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x022f
        L_0x01fd:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x021b
            r0 = 2131629049(0x7f0e13f9, float:1.8885408E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r9] = r2
            java.lang.String r2 = "VoipChannelChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x022f
        L_0x021b:
            r4 = 1
            r0 = 2131629085(0x7f0e141d, float:1.888548E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r9] = r2
            java.lang.String r2 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x022f:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r2.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setTextSize(r4)
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r2.setInfo((org.telegram.tgnet.TLObject) r3)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r3, r2)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x01b8
        L_0x0252:
            r0 = 37
            if (r2 != r0) goto L_0x02c2
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x027c
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x0289
        L_0x027c:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x0289:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x02a5
            r2 = 2131629079(0x7f0e1417, float:1.8885469E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipChannelUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02b7
        L_0x02a5:
            r3 = 1
            r2 = 2131629208(0x7f0e1498, float:1.888573E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02b7:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            r5 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r5
            goto L_0x01b8
        L_0x02c2:
            r0 = 33
            r5 = 3000(0xbb8, double:1.482E-320)
            if (r2 != r0) goto L_0x02d8
            r0 = 2131629126(0x7f0e1446, float:1.8885564E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558613(0x7f0d00d5, float:1.8742547E38)
            r1.timeLeft = r5
            goto L_0x0840
        L_0x02d8:
            r0 = 77
            if (r2 != r0) goto L_0x0303
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2 = 2131558500(0x7f0d0064, float:1.8742318E38)
            r5 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r5
            org.telegram.ui.ActionBar.BaseFragment r3 = r1.parentFragment
            if (r3 == 0) goto L_0x0840
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$Message
            if (r3 == 0) goto L_0x0840
            r3 = r4
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            r1.setOnTouchListener(r15)
            android.widget.TextView r4 = r1.infoTextView
            r4.setMovementMethod(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r4.<init>(r1, r3)
            r1.setOnClickListener(r4)
            goto L_0x0840
        L_0x0303:
            r0 = 30
            if (r2 != r0) goto L_0x0333
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0313
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0318
        L_0x0313:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0318:
            r2 = 2131629206(0x7f0e1496, float:1.8885726E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558614(0x7f0d00d6, float:1.8742549E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x0333:
            r0 = 35
            if (r2 != r0) goto L_0x0369
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0343
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x034e
        L_0x0343:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x034d
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x034e
        L_0x034d:
            r0 = r14
        L_0x034e:
            r2 = 2131629207(0x7f0e1497, float:1.8885728E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558614(0x7f0d00d6, float:1.8742549E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x0369:
            r0 = 31
            if (r2 != r0) goto L_0x0399
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0379
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x037e
        L_0x0379:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x037e:
            r2 = 2131629204(0x7f0e1494, float:1.8885722E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558620(0x7f0d00dc, float:1.874256E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x0399:
            r0 = 38
            if (r2 != r0) goto L_0x03cf
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x03b9
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131629216(0x7f0e14a0, float:1.8885747E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = r0.title
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03c6
        L_0x03b9:
            r0 = 2131629215(0x7f0e149f, float:1.8885745E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03c6:
            r2 = 2131558606(0x7f0d00ce, float:1.8742533E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x03cf:
            r0 = 42
            if (r2 != r0) goto L_0x0400
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03ea
            r0 = 2131629070(0x7f0e140e, float:1.888545E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03f7
        L_0x03ea:
            r0 = 2131629184(0x7f0e1480, float:1.8885682E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03f7:
            r2 = 2131558466(0x7f0d0042, float:1.8742249E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x0400:
            r0 = 43
            if (r2 != r0) goto L_0x0431
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x041b
            r0 = 2131629071(0x7f0e140f, float:1.8885453E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0428
        L_0x041b:
            r0 = 2131629185(0x7f0e1481, float:1.8885684E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0428:
            r2 = 2131558472(0x7f0d0048, float:1.874226E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x0431:
            int r0 = r1.currentAction
            r5 = 39
            if (r0 == r5) goto L_0x0822
            r5 = 100
            if (r0 != r5) goto L_0x043d
            goto L_0x0822
        L_0x043d:
            r5 = 40
            if (r0 == r5) goto L_0x07af
            r5 = 101(0x65, float:1.42E-43)
            if (r0 != r5) goto L_0x0447
            goto L_0x07af
        L_0x0447:
            if (r2 != r7) goto L_0x0475
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0455
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x045a
        L_0x0455:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x045a:
            r2 = 2131629205(0x7f0e1495, float:1.8885724E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558620(0x7f0d00dc, float:1.874256E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x0475:
            r5 = 32
            if (r2 != r5) goto L_0x04a5
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0485
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x048a
        L_0x0485:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x048a:
            r2 = 2131629175(0x7f0e1477, float:1.8885663E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558612(0x7f0d00d4, float:1.8742545E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x0840
        L_0x04a5:
            r5 = 9
            if (r2 == r5) goto L_0x0776
            r5 = 10
            if (r2 != r5) goto L_0x04af
            goto L_0x0776
        L_0x04af:
            r5 = 8
            if (r2 != r5) goto L_0x04ca
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131627133(0x7f0e0c7d, float:1.8881522E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            goto L_0x07aa
        L_0x04ca:
            r5 = 22
            if (r2 != r5) goto L_0x0535
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r0 == 0) goto L_0x04ec
            if (r3 != 0) goto L_0x04e1
            r0 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x04e1:
            r0 = 2131626574(0x7f0e0a4e, float:1.8880388E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x04ec:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r4 = -r10
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x051d
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x051d
            if (r3 != 0) goto L_0x0512
            r0 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x0512:
            r0 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x051d:
            if (r3 != 0) goto L_0x052a
            r0 = 2131626571(0x7f0e0a4b, float:1.8880382E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x052a:
            r0 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x0535:
            r5 = 23
            if (r2 != r5) goto L_0x0544
            r0 = 2131625060(0x7f0e0464, float:1.8877317E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07aa
        L_0x0544:
            r5 = 6
            if (r2 != r5) goto L_0x0560
            r0 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624409(0x7f0e01d9, float:1.8875997E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r7 = 48
            goto L_0x0840
        L_0x0560:
            r5 = 13
            if (r0 != r5) goto L_0x057d
            r0 = 2131627894(0x7f0e0var_, float:1.8883065E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627895(0x7f0e0var_, float:1.8883067E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558622(0x7f0d00de, float:1.8742565E38)
        L_0x0579:
            r7 = 44
            goto L_0x0840
        L_0x057d:
            r5 = 14
            if (r0 != r5) goto L_0x0597
            r0 = 2131627896(0x7f0e0var_, float:1.888307E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627897(0x7f0e0var_, float:1.8883071E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558624(0x7f0d00e0, float:1.874257E38)
            goto L_0x0579
        L_0x0597:
            r0 = 7
            if (r2 != r0) goto L_0x05c0
            r0 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05bb
            r2 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x05ba:
            r15 = r2
        L_0x05bb:
            r2 = 2131558424(0x7f0d0018, float:1.8742163E38)
            goto L_0x0840
        L_0x05c0:
            r0 = 20
            if (r2 == r0) goto L_0x065d
            r0 = 21
            if (r2 != r0) goto L_0x05ca
            goto L_0x065d
        L_0x05ca:
            r0 = 19
            if (r2 != r0) goto L_0x05d1
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x05bb
        L_0x05d1:
            r0 = 82
            if (r2 != r0) goto L_0x05ed
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            boolean r0 = r0.isVideo
            if (r0 == 0) goto L_0x05e2
            r0 = 2131624514(0x7f0e0242, float:1.887621E38)
            java.lang.String r2 = "AttachMediaVideoDeselected"
            goto L_0x05e7
        L_0x05e2:
            r0 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r2 = "AttachMediaPhotoDeselected"
        L_0x05e7:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x01b8
        L_0x05ed:
            r0 = 78
            if (r2 == r0) goto L_0x0624
            r0 = 79
            if (r2 != r0) goto L_0x05f6
            goto L_0x0624
        L_0x05f6:
            r0 = 3
            if (r2 != r0) goto L_0x0603
            r0 = 2131625017(0x7f0e0439, float:1.887723E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x060c
        L_0x0603:
            r0 = 2131625073(0x7f0e0471, float:1.8877344E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x060c:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05bb
            r2 = 2131625018(0x7f0e043a, float:1.8877232E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x05ba
        L_0x0624:
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = 78
            if (r2 != r3) goto L_0x0638
            java.lang.Object[] r2 = new java.lang.Object[r9]
            java.lang.String r3 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r2)
            goto L_0x0640
        L_0x0638:
            java.lang.Object[] r2 = new java.lang.Object[r9]
            java.lang.String r3 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r2)
        L_0x0640:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x064a
            r2 = 2131558467(0x7f0d0043, float:1.874225E38)
            goto L_0x064d
        L_0x064a:
            r2 = 2131558473(0x7f0d0049, float:1.8742263E38)
        L_0x064d:
            boolean r3 = r4 instanceof java.lang.Integer
            if (r3 == 0) goto L_0x0840
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            long r3 = (long) r3
            r1.timeLeft = r3
            goto L_0x0840
        L_0x065d:
            r0 = r4
            org.telegram.messenger.MessagesController$DialogFilter r0 = (org.telegram.messenger.MessagesController.DialogFilter) r0
            r4 = 0
            int r6 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0719
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)
            if (r3 == 0) goto L_0x0680
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r10)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            long r10 = r3.user_id
        L_0x0680:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r3 == 0) goto L_0x06d2
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            r4 = 20
            if (r2 != r4) goto L_0x06b5
            r4 = 2131625946(0x7f0e07da, float:1.8879114E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r8 = 1
            r6[r8] = r0
            java.lang.String r0 = "FilterUserAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0768
        L_0x06b5:
            r5 = 2
            r8 = 1
            r4 = 2131625947(0x7f0e07db, float:1.8879116E38)
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r6[r8] = r0
            java.lang.String r0 = "FilterUserRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0768
        L_0x06d2:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r10
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            r4 = 20
            if (r2 != r4) goto L_0x06ff
            r4 = 2131625886(0x7f0e079e, float:1.8878993E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r8 = 1
            r6[r8] = r0
            java.lang.String r0 = "FilterChatAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0768
        L_0x06ff:
            r5 = 2
            r8 = 1
            r4 = 2131625887(0x7f0e079f, float:1.8878995E38)
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r6[r8] = r0
            java.lang.String r0 = "FilterChatRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0768
        L_0x0719:
            r4 = 20
            if (r2 != r4) goto L_0x0743
            r4 = 2131625890(0x7f0e07a2, float:1.8879E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.Object[] r5 = new java.lang.Object[r9]
            java.lang.String r8 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r8, r3, r5)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r3 = 1
            r6[r3] = r0
            java.lang.String r0 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0768
        L_0x0743:
            r4 = 2131625891(0x7f0e07a3, float:1.8879003E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.Object[] r5 = new java.lang.Object[r9]
            java.lang.String r8 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r8, r3, r5)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r3 = 1
            r6[r3] = r0
            java.lang.String r0 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0768:
            r3 = 20
            if (r2 != r3) goto L_0x0771
            r2 = 2131558452(0x7f0d0034, float:1.874222E38)
            goto L_0x0840
        L_0x0771:
            r2 = 2131558453(0x7f0d0035, float:1.8742222E38)
            goto L_0x0840
        L_0x0776:
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r3 = 9
            if (r2 != r3) goto L_0x0794
            r2 = 2131625585(0x7f0e0671, float:1.8878382E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x07aa
        L_0x0794:
            r3 = 1
            r2 = 2131625586(0x7f0e0672, float:1.8878384E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x07aa:
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            goto L_0x0840
        L_0x07af:
            r2 = 40
            if (r0 != r2) goto L_0x07b9
            r0 = 2131629116(0x7f0e143c, float:1.8885544E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            goto L_0x07be
        L_0x07b9:
            r0 = 2131629210(0x7f0e149a, float:1.8885734E38)
            java.lang.String r2 = "VoipGroupVideoRecordSaved"
        L_0x07be:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558616(0x7f0d00d8, float:1.8742553E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            android.widget.TextView r3 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r4 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r4.<init>()
            r3.setMovementMethod(r4)
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            java.lang.String r4 = "**"
            int r4 = r0.indexOf(r4)
            java.lang.String r5 = "**"
            int r0 = r0.lastIndexOf(r5)
            if (r4 < 0) goto L_0x0820
            if (r0 < 0) goto L_0x0820
            if (r4 == r0) goto L_0x0820
            int r5 = r0 + 2
            r3.replace(r0, r5, r14)
            int r5 = r4 + 2
            r3.replace(r4, r5, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x081c }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x081c }
            r6.<init>()     // Catch:{ Exception -> 0x081c }
            java.lang.String r8 = "tg://openmessage?user_id="
            r6.append(r8)     // Catch:{ Exception -> 0x081c }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x081c }
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ Exception -> 0x081c }
            long r10 = r8.getClientUserId()     // Catch:{ Exception -> 0x081c }
            r6.append(r10)     // Catch:{ Exception -> 0x081c }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x081c }
            r5.<init>(r6)     // Catch:{ Exception -> 0x081c }
            r6 = 2
            int r0 = r0 - r6
            r6 = 33
            r3.setSpan(r5, r4, r0, r6)     // Catch:{ Exception -> 0x081c }
            goto L_0x0820
        L_0x081c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0820:
            r0 = r3
            goto L_0x0840
        L_0x0822:
            r2 = 39
            if (r0 != r2) goto L_0x082c
            r0 = 2131629117(0x7f0e143d, float:1.8885546E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            goto L_0x0831
        L_0x082c:
            r0 = 2131629211(0x7f0e149b, float:1.8885737E38)
            java.lang.String r2 = "VoipGroupVideoRecordStarted"
        L_0x0831:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558617(0x7f0d00d9, float:1.8742555E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
        L_0x0840:
            android.widget.TextView r3 = r1.infoTextView
            r3.setText(r0)
            if (r2 == 0) goto L_0x086d
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r0.setPlayInDirectionOfCustomEndFrame(r9)
            int r2 = r0.getFramesCount()
            r0.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0874
        L_0x086d:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0874:
            if (r15 == 0) goto L_0x08b6
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r15)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x08e0
        L_0x08b6:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x08e0:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x10b1
        L_0x08e7:
            int r5 = r1.currentAction
            r6 = 45
            if (r5 == r6) goto L_0x10b6
            r6 = 46
            if (r5 == r6) goto L_0x10b6
            r6 = 47
            if (r5 == r6) goto L_0x10b6
            r6 = 51
            if (r5 == r6) goto L_0x10b6
            r6 = 50
            if (r5 == r6) goto L_0x10b6
            r6 = 52
            if (r5 == r6) goto L_0x10b6
            r6 = 53
            if (r5 == r6) goto L_0x10b6
            r6 = 54
            if (r5 == r6) goto L_0x10b6
            r6 = 55
            if (r5 == r6) goto L_0x10b6
            r6 = 56
            if (r5 == r6) goto L_0x10b6
            r6 = 57
            if (r5 == r6) goto L_0x10b6
            r6 = 58
            if (r5 == r6) goto L_0x10b6
            r6 = 59
            if (r5 == r6) goto L_0x10b6
            r6 = 60
            if (r5 == r6) goto L_0x10b6
            r6 = 71
            if (r5 == r6) goto L_0x10b6
            r6 = 70
            if (r5 == r6) goto L_0x10b6
            r6 = 75
            if (r5 == r6) goto L_0x10b6
            r6 = 76
            if (r5 == r6) goto L_0x10b6
            r6 = 41
            if (r5 == r6) goto L_0x10b6
            r6 = 78
            if (r5 == r6) goto L_0x10b6
            r6 = 79
            if (r5 == r6) goto L_0x10b6
            r6 = 61
            if (r5 == r6) goto L_0x10b6
            r6 = 80
            if (r5 != r6) goto L_0x0947
            goto L_0x10b6
        L_0x0947:
            r6 = 24
            if (r5 == r6) goto L_0x0f4a
            r6 = 25
            if (r5 != r6) goto L_0x0951
            goto L_0x0f4a
        L_0x0951:
            r4 = 11
            if (r5 != r4) goto L_0x09c2
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624547(0x7f0e0263, float:1.8876277E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r2.setAnimation((int) r3, (int) r7, (int) r7)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = r1.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x10b1
        L_0x09c2:
            r4 = 15
            if (r5 != r4) goto L_0x0a96
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627150(0x7f0e0c8e, float:1.8881556E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625883(0x7f0e079b, float:1.8878987E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            android.widget.TextView r0 = r1.undoTextView
            android.text.TextPaint r0 = r0.getPaint()
            android.widget.TextView r2 = r1.undoTextView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r0 = r0.measureText(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            int r0 = (int) r2
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r2
            r12.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625882(0x7f0e079a, float:1.8878984E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r3 = r0.indexOf(r3)
            r4 = 42
            int r0 = r0.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0a63
            if (r0 < 0) goto L_0x0a63
            if (r3 == r0) goto L_0x0a63
            int r4 = r0 + 1
            r2.replace(r0, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r4.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)
        L_0x0a63:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 2
            r0.setMaxLines(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x10b1
        L_0x0a96:
            r4 = 16
            if (r5 == r4) goto L_0x0dd3
            r4 = 17
            if (r5 != r4) goto L_0x0aa0
            goto L_0x0dd3
        L_0x0aa0:
            r4 = 18
            if (r5 != r4) goto L_0x0b2b
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2 = 4000(0xfa0, float:5.605E-42)
            int r3 = r0.length()
            int r3 = r3 / 50
            int r3 = r3 * 1600
            r4 = 10000(0x2710, float:1.4013E-41)
            int r3 = java.lang.Math.min(r3, r4)
            int r2 = java.lang.Math.max(r2, r3)
            long r2 = (long) r2
            r1.timeLeft = r2
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 16
            r2.setGravity(r3)
            android.widget.TextView r2 = r1.infoTextView
            r2.setText(r0)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.bottomMargin = r0
            r0 = -1
            r12.height = r0
            r0 = 51
            r13.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r13.bottomMargin = r0
            r13.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            android.widget.TextView r0 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r2.<init>()
            r0.setMovementMethod(r2)
            goto L_0x10b1
        L_0x0b2b:
            r4 = 12
            if (r5 != r4) goto L_0x0bc9
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625206(0x7f0e04f6, float:1.8877613E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166188(0x7var_ec, float:1.7946614E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131625207(0x7f0e04f7, float:1.8877615E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r3 = r0.indexOf(r3)
            r4 = 42
            int r0 = r0.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0ba1
            if (r0 < 0) goto L_0x0ba1
            if (r3 == r0) goto L_0x0ba1
            int r4 = r0 + 1
            r2.replace(r0, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r4.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)
        L_0x0ba1:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 2
            r0.setMaxLines(r4)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            goto L_0x10b1
        L_0x0bc9:
            r4 = 2
            if (r5 == r4) goto L_0x0d6e
            r4 = 4
            if (r5 != r4) goto L_0x0bd1
            goto L_0x0d6e
        L_0x0bd1:
            r4 = 82
            if (r2 != r4) goto L_0x0CLASSNAME
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            android.widget.TextView r2 = r1.infoTextView
            boolean r3 = r0.isVideo
            if (r3 == 0) goto L_0x0bea
            r3 = 2131624514(0x7f0e0242, float:1.887621E38)
            java.lang.String r4 = "AttachMediaVideoDeselected"
            goto L_0x0bef
        L_0x0bea:
            r3 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r4 = "AttachMediaPhotoDeselected"
        L_0x0bef:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.TextView r2 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r2.setTypeface(r3)
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setRoundRadius(r3)
            java.lang.String r2 = r0.thumbPath
            if (r2 == 0) goto L_0x0c2e
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImage(r2, r15, r3)
            goto L_0x10b1
        L_0x0c2e:
            java.lang.String r2 = r0.path
            if (r2 == 0) goto L_0x0c8a
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            int r3 = r0.orientation
            r4 = 1
            r2.setOrientation(r3, r4)
            boolean r2 = r0.isVideo
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "vthumb://"
            r3.append(r4)
            int r4 = r0.imageId
            r3.append(r4)
            java.lang.String r4 = ":"
            r3.append(r4)
            java.lang.String r0 = r0.path
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r2.setImage(r0, r15, r3)
            goto L_0x10b1
        L_0x0CLASSNAME:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "thumb://"
            r3.append(r4)
            int r4 = r0.imageId
            r3.append(r4)
            java.lang.String r4 = ":"
            r3.append(r4)
            java.lang.String r0 = r0.path
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r2.setImage(r0, r15, r3)
            goto L_0x10b1
        L_0x0c8a:
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImageDrawable(r2)
            goto L_0x10b1
        L_0x0CLASSNAME:
            r2 = 1110704128(0x42340000, float:45.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.topMargin = r2
            r12.rightMargin = r9
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r2.setTypeface(r3)
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r3)
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x0d31
            if (r2 == 0) goto L_0x0d31
            r3 = 26
            if (r2 != r3) goto L_0x0cd2
            goto L_0x0d31
        L_0x0cd2:
            r3 = 27
            if (r2 != r3) goto L_0x0ce5
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625074(0x7f0e0472, float:1.8877346E38)
            java.lang.String r4 = "ChatsDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d3f
        L_0x0ce5:
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r2 == 0) goto L_0x0d22
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x0d13
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0d13
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624917(0x7f0e03d5, float:1.8877027E38)
            java.lang.String r4 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d3f
        L_0x0d13:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626145(0x7f0e08a1, float:1.8879518E38)
            java.lang.String r4 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d3f
        L_0x0d22:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625021(0x7f0e043d, float:1.8877238E38)
            java.lang.String r4 = "ChatDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d3f
        L_0x0d31:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626201(0x7f0e08d9, float:1.8879632E38)
            java.lang.String r4 = "HistoryClearedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x0d3f:
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x10b1
            r2 = 0
        L_0x0d46:
            int r3 = r20.size()
            if (r2 >= r3) goto L_0x10b1
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Object r4 = r0.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            long r4 = r4.longValue()
            int r6 = r1.currentAction
            if (r6 == 0) goto L_0x0d67
            r7 = 26
            if (r6 != r7) goto L_0x0d65
            goto L_0x0d67
        L_0x0d65:
            r6 = 0
            goto L_0x0d68
        L_0x0d67:
            r6 = 1
        L_0x0d68:
            r3.addDialogAction(r4, r6)
            int r2 = r2 + 1
            goto L_0x0d46
        L_0x0d6e:
            r3 = 2
            if (r2 != r3) goto L_0x0d80
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625017(0x7f0e0439, float:1.887723E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0d8e
        L_0x0d80:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625073(0x7f0e0471, float:1.8877344E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0d8e:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.topMargin = r0
            r12.rightMargin = r9
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558422(0x7f0d0016, float:1.874216E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x10b1
        L_0x0dd3:
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r0.setTextSize(r4, r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 16
            r0.setGravity(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1106247680(0x41var_, float:30.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setMinHeight(r2)
            r0 = r3
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "🎲"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0e18
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625489(0x7f0e0611, float:1.8878187E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165386(0x7var_ca, float:1.7944988E38)
            r0.setImageResource(r2)
            goto L_0x0ec1
        L_0x0e18:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0e35
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625327(0x7f0e056f, float:1.8877859E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0e32:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0e8f
        L_0x0e35:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0e68
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r9)
            r3.setText(r2)
            goto L_0x0e32
        L_0x0e68:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625488(0x7f0e0610, float:1.8878185E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r9] = r0
            java.lang.String r4 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r6, r9)
            r2.setText(r3)
        L_0x0e8f:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r12.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r12.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r13.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.height = r0
        L_0x0ec1:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131628256(0x7f0e10e0, float:1.88838E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0var_
            android.widget.TextView r0 = r1.undoTextView
            android.text.TextPaint r0 = r0.getPaint()
            android.widget.TextView r2 = r1.undoTextView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r0 = r0.measureText(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            int r0 = (int) r2
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = r1.getThemedColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r9)
            goto L_0x0var_
        L_0x0var_:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0var_:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r2
            r12.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.bottomMargin = r0
            r0 = -1
            r12.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            goto L_0x10b1
        L_0x0f4a:
            r2 = 8
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r4
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r9)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x101c
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r1.infoTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r6 = 1
            r4.setTextSize(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = r1.getThemedColor(r2)
            java.lang.String r6 = "BODY.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = r1.getThemedColor(r2)
            java.lang.String r6 = "Wibe Big.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = r1.getThemedColor(r2)
            java.lang.String r6 = "Wibe Big 3.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = r1.getThemedColor(r2)
            java.lang.String r5 = "Wibe Small.**"
            r4.setLayerColor(r5, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627814(0x7f0e0var_, float:1.8882903E38)
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558472(0x7f0d0048, float:1.874226E38)
            r5 = 28
            r6 = 28
            r2.setAnimation((int) r4, (int) r5, (int) r6)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0ff4
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627816(0x7f0e0var_, float:1.8882907E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r3 = 1
            r6[r3] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
            goto L_0x100d
        L_0x0ff4:
            r3 = 1
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627815(0x7f0e0var_, float:1.8882905E38)
            java.lang.Object[] r6 = new java.lang.Object[r3]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r6[r9] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
        L_0x100d:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            goto L_0x10a0
        L_0x101c:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Body Main.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Body Top.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Line.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Curve Big.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = r1.getThemedColor(r2)
            java.lang.String r3 = "Curve Small.**"
            r0.setLayerColor(r3, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627813(0x7f0e0var_, float:1.8882901E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558466(0x7f0d0042, float:1.8742249E38)
            r3 = 28
            r4 = 28
            r0.setAnimation((int) r2, (int) r3, (int) r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = r1.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
        L_0x10a0:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x10b1:
            r0 = 0
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x16a1
        L_0x10b6:
            android.widget.ImageView r0 = r1.undoImageView
            r5 = 8
            r0.setVisibility(r5)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r5)
            r5 = -1
            int r0 = r1.currentAction
            r8 = 76
            r13 = 1091567616(0x41100000, float:9.0)
            if (r0 != r8) goto L_0x10fa
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624775(0x7f0e0347, float:1.887674E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558458(0x7f0d003a, float:1.8742232E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
        L_0x10f7:
            r0 = 1
            goto L_0x1663
        L_0x10fa:
            r8 = 75
            if (r0 != r8) goto L_0x1123
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626124(0x7f0e088c, float:1.8879475E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10f7
        L_0x1123:
            r8 = 70
            if (r2 != r8) goto L_0x116f
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r9)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624568(0x7f0e0278, float:1.887632E38)
            r4 = 1
            java.lang.Object[] r8 = new java.lang.Object[r4]
            r8[r9] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r8)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558450(0x7f0d0032, float:1.8742216E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r9, r9, r2)
            r0 = 1
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1663
        L_0x116f:
            r2 = 71
            if (r0 != r2) goto L_0x11a4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624567(0x7f0e0277, float:1.8876317E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r9, r9, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1662
        L_0x11a4:
            r2 = 45
            if (r0 != r2) goto L_0x11ce
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626251(0x7f0e090b, float:1.8879733E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558445(0x7f0d002d, float:1.8742206E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10f7
        L_0x11ce:
            r2 = 46
            if (r0 != r2) goto L_0x11f8
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626252(0x7f0e090c, float:1.8879735E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558445(0x7f0d002d, float:1.8742206E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10f7
        L_0x11f8:
            r2 = 47
            if (r0 != r2) goto L_0x122d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626274(0x7f0e0922, float:1.887978E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558478(0x7f0d004e, float:1.8742273E38)
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r9, r9, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10f7
        L_0x122d:
            r2 = 1096810496(0x41600000, float:14.0)
            r8 = 51
            if (r0 != r8) goto L_0x1257
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624528(0x7f0e0250, float:1.8876238E38)
            java.lang.String r4 = "AudioSpeedNormal"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558409(0x7f0d0009, float:1.8742133E38)
            r0.setAnimation((int) r3, (int) r7, (int) r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1662
        L_0x1257:
            r8 = 50
            if (r0 != r8) goto L_0x127f
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624527(0x7f0e024f, float:1.8876236E38)
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558408(0x7f0d0008, float:1.874213E38)
            r0.setAnimation((int) r3, (int) r7, (int) r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1662
        L_0x127f:
            r8 = 52
            if (r0 == r8) goto L_0x15bf
            r8 = 56
            if (r0 == r8) goto L_0x15bf
            r8 = 57
            if (r0 == r8) goto L_0x15bf
            r8 = 58
            if (r0 == r8) goto L_0x15bf
            r8 = 59
            if (r0 == r8) goto L_0x15bf
            r8 = 60
            if (r0 == r8) goto L_0x15bf
            r8 = 80
            if (r0 != r8) goto L_0x129d
            goto L_0x15bf
        L_0x129d:
            r8 = 54
            if (r0 != r8) goto L_0x12c7
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558545(0x7f0d0091, float:1.8742409E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1662
        L_0x12c7:
            r8 = 55
            if (r0 != r8) goto L_0x12f1
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558544(0x7f0d0090, float:1.8742407E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1662
        L_0x12f1:
            r8 = 41
            if (r0 != r8) goto L_0x13a4
            if (r4 != 0) goto L_0x1370
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1317
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131626299(0x7f0e093b, float:1.887983E38)
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1396
        L_0x1317:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r0 == 0) goto L_0x1346
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626298(0x7f0e093a, float:1.8879828E38)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1396
        L_0x1346:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626300(0x7f0e093c, float:1.8879832E38)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1396
        L_0x1370:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626297(0x7f0e0939, float:1.8879826E38)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.Object[] r8 = new java.lang.Object[r9]
            java.lang.String r11 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r11, r0, r8)
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x1396:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation((int) r3, (int) r7, (int) r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1662
        L_0x13a4:
            r7 = 53
            if (r0 != r7) goto L_0x14f3
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1494
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.clientUserId
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x13f1
            int r0 = r0.intValue()
            r3 = 1
            if (r0 != r3) goto L_0x13d3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13e5
        L_0x13d3:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131626092(0x7f0e086c, float:1.887941E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x13e5:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558542(0x7f0d008e, float:1.8742403E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x14ed
        L_0x13f1:
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r3 == 0) goto L_0x143f
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r10
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x1426
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131626087(0x7f0e0867, float:1.88794E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1489
        L_0x1426:
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131626091(0x7f0e086b, float:1.8879408E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1489
        L_0x143f:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x146f
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131626089(0x7f0e0869, float:1.8879404E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1489
        L_0x146f:
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131626093(0x7f0e086d, float:1.8879412E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x1489:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x14ea
        L_0x1494:
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x14c1
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131626086(0x7f0e0866, float:1.8879398E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.Object[] r4 = new java.lang.Object[r9]
            java.lang.String r7 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3, r4)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x14e0
        L_0x14c1:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626090(0x7f0e086a, float:1.8879406E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Object[] r5 = new java.lang.Object[r9]
            java.lang.String r7 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3, r5)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x14e0:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
        L_0x14ea:
            r3 = 300(0x12c, double:1.48E-321)
            r5 = r3
        L_0x14ed:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1662
        L_0x14f3:
            r7 = 61
            if (r0 != r7) goto L_0x1662
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1589
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1526
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624673(0x7f0e02e1, float:1.8876532E38)
            java.lang.String r4 = "BackgroundToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558542(0x7f0d008e, float:1.8742403E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x15b9
        L_0x1526:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r0 == 0) goto L_0x1555
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624672(0x7f0e02e0, float:1.887653E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x157e
        L_0x1555:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624674(0x7f0e02e2, float:1.8876534E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x157e:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x15b9
        L_0x1589:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624671(0x7f0e02df, float:1.8876528E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r10 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r10, r0, r7)
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
        L_0x15b9:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1662
        L_0x15bf:
            boolean r0 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r0 != 0) goto L_0x15c6
            return
        L_0x15c6:
            r0 = 2131558433(0x7f0d0021, float:1.8742182E38)
            int r3 = r1.currentAction
            r4 = 80
            if (r3 != r4) goto L_0x15df
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131625617(0x7f0e0691, float:1.8878447E38)
            java.lang.String r7 = "EmailCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x164f
        L_0x15df:
            r4 = 60
            if (r3 != r4) goto L_0x15f2
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131627548(0x7f0e0e1c, float:1.8882364E38)
            java.lang.String r7 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x164f
        L_0x15f2:
            r4 = 56
            if (r3 != r4) goto L_0x1605
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131628927(0x7f0e137f, float:1.888516E38)
            java.lang.String r7 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x164f
        L_0x1605:
            r4 = 57
            if (r3 != r4) goto L_0x1618
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626183(0x7f0e08c7, float:1.8879595E38)
            java.lang.String r7 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x164f
        L_0x1618:
            r4 = 52
            if (r3 != r4) goto L_0x162b
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626683(0x7f0e0abb, float:1.888061E38)
            java.lang.String r7 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x164f
        L_0x162b:
            r4 = 59
            if (r3 != r4) goto L_0x1641
            r0 = 2131558613(0x7f0d00d5, float:1.8742547E38)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.String r7 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x164f
        L_0x1641:
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131628671(0x7f0e127f, float:1.8884641E38)
            java.lang.String r7 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
        L_0x164f:
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 30
            r3.setAnimation((int) r0, (int) r4, (int) r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
        L_0x1662:
            r0 = 0
        L_0x1663:
            android.widget.TextView r3 = r1.subinfoTextView
            r4 = 8
            r3.setVisibility(r4)
            android.widget.TextView r3 = r1.undoTextView
            java.lang.String r7 = "undo_cancelColor"
            int r7 = r1.getThemedColor(r7)
            r3.setTextColor(r7)
            android.widget.LinearLayout r3 = r1.undoButton
            r3.setVisibility(r4)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 0
            r3.setProgress(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r3.playAnimation()
            r3 = 0
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x16a1
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r4.<init>(r1)
            r3.postDelayed(r4, r5)
        L_0x16a1:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.widget.TextView r4 = r1.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x16ce
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ". "
            r4.append(r5)
            android.widget.TextView r5 = r1.subinfoTextView
            java.lang.CharSequence r5 = r5.getText()
            r4.append(r5)
            java.lang.String r14 = r4.toString()
        L_0x16ce:
            r3.append(r14)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r3)
            boolean r3 = r19.isMultilineSubInfo()
            if (r3 == 0) goto L_0x1723
            android.view.ViewParent r0 = r19.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x16ee
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x16ee:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r9)
            r5 = 0
            r20 = r19
            r21 = r2
            r22 = r0
            r23 = r3
            r24 = r4
            r25 = r5
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r0 = r1.subinfoTextView
            int r0 = r0.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.undoViewHeight = r0
            goto L_0x17c7
        L_0x1723:
            boolean r3 = r19.hasSubInfo()
            if (r3 == 0) goto L_0x1733
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x17c7
        L_0x1733:
            android.view.ViewParent r3 = r19.getParent()
            boolean r3 = r3 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x17c7
            android.view.ViewParent r3 = r19.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = r3.getMeasuredWidth()
            int r5 = r3.getPaddingLeft()
            int r4 = r4 - r5
            int r3 = r3.getPaddingRight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x1755
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
        L_0x1755:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r3
            android.widget.TextView r3 = r1.infoTextView
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            r5 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r9)
            r7 = 0
            r20 = r19
            r21 = r3
            r22 = r4
            r23 = r5
            r24 = r6
            r25 = r7
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r3 = r1.infoTextView
            int r3 = r3.getMeasuredHeight()
            int r4 = r1.currentAction
            r5 = 16
            if (r4 == r5) goto L_0x1791
            r5 = 17
            if (r4 == r5) goto L_0x1791
            r5 = 18
            if (r4 != r5) goto L_0x178e
            goto L_0x1791
        L_0x178e:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1793
        L_0x1791:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x1793:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 + r2
            r1.undoViewHeight = r3
            int r2 = r1.currentAction
            r4 = 18
            if (r2 != r4) goto L_0x17ad
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x17c7
        L_0x17ad:
            r4 = 25
            if (r2 != r4) goto L_0x17be
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x17c7
        L_0x17be:
            if (r0 == 0) goto L_0x17c7
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r0
            r1.undoViewHeight = r3
        L_0x17c7:
            int r0 = r19.getVisibility()
            if (r0 == 0) goto L_0x1827
            r1.setVisibility(r9)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x17d7
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x17d9
        L_0x17d7:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x17d9:
            int r2 = r1.enterOffsetMargin
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setEnterOffset(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r2 = 1
            android.animation.Animator[] r3 = new android.animation.Animator[r2]
            r2 = 2
            float[] r2 = new float[r2]
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x17f6
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x17f8
        L_0x17f6:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x17f8:
            int r6 = r1.enterOffsetMargin
            int r7 = r1.undoViewHeight
            int r6 = r6 + r7
            float r6 = (float) r6
            float r5 = r5 * r6
            r2[r9] = r5
            if (r4 == 0) goto L_0x1807
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x1809
        L_0x1807:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x1809:
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "enterOffset"
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r1, r4, r2)
            r3[r9] = r2
            r0.playTogether(r3)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x1827:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(java.util.ArrayList, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$2(View view) {
        hide(false, 1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$6(TLRPC$Message tLRPC$Message, View view) {
        hide(true, 1);
        TLRPC$TL_payments_getPaymentReceipt tLRPC$TL_payments_getPaymentReceipt = new TLRPC$TL_payments_getPaymentReceipt();
        tLRPC$TL_payments_getPaymentReceipt.msg_id = tLRPC$Message.id;
        tLRPC$TL_payments_getPaymentReceipt.peer = this.parentFragment.getMessagesController().getInputPeer(tLRPC$Message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_getPaymentReceipt, new UndoView$$ExternalSyntheticLambda7(this), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new UndoView$$ExternalSyntheticLambda6(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$4(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$7() {
        this.leftImageView.performHapticFeedback(3, 2);
    }

    public void setEnterOffsetMargin(int i) {
        this.enterOffsetMargin = i;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, NUM));
        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float measuredHeight = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) AndroidUtilities.dp(9.0f));
            if (measuredHeight > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), measuredHeight);
                super.dispatchDraw(canvas);
            }
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float measuredHeight = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) this.enterOffsetMargin) + ((float) AndroidUtilities.dp(1.0f));
            if (measuredHeight > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), measuredHeight);
                super.dispatchDraw(canvas);
            }
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        } else {
            this.backgroundDrawable.draw(canvas);
        }
        int i = this.currentAction;
        if (i == 1 || i == 0 || i == 27 || i == 26 || i == 81) {
            long j = this.timeLeft;
            int ceil = j > 0 ? (int) Math.ceil((double) (((float) j) / 1000.0f)) : 0;
            if (this.prevSeconds != ceil) {
                this.prevSeconds = ceil;
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ceil))});
                this.timeLeftString = format;
                StaticLayout staticLayout = this.timeLayout;
                if (staticLayout != null) {
                    this.timeLayoutOut = staticLayout;
                    this.timeReplaceProgress = 0.0f;
                }
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(format));
                this.timeLayout = new StaticLayout(this.timeLeftString, this.textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            float f = this.timeReplaceProgress;
            if (f < 1.0f) {
                float f2 = f + 0.10666667f;
                this.timeReplaceProgress = f2;
                if (f2 > 1.0f) {
                    this.timeReplaceProgress = 1.0f;
                } else {
                    invalidate();
                }
            }
            int alpha = this.textPaint.getAlpha();
            if (this.timeLayoutOut != null) {
                float f3 = this.timeReplaceProgress;
                if (f3 < 1.0f) {
                    this.textPaint.setAlpha((int) (((float) alpha) * (1.0f - f3)));
                    canvas.save();
                    canvas.translate(this.rect.centerX() - ((float) (this.textWidth / 2)), ((float) AndroidUtilities.dp(17.2f)) + (((float) AndroidUtilities.dp(10.0f)) * this.timeReplaceProgress));
                    this.timeLayoutOut.draw(canvas);
                    this.textPaint.setAlpha(alpha);
                    canvas.restore();
                }
            }
            if (this.timeLayout != null) {
                float f4 = this.timeReplaceProgress;
                if (f4 != 1.0f) {
                    this.textPaint.setAlpha((int) (((float) alpha) * f4));
                }
                canvas.save();
                canvas.translate(this.rect.centerX() - ((float) (this.textWidth / 2)), ((float) AndroidUtilities.dp(17.2f)) - (((float) AndroidUtilities.dp(10.0f)) * (1.0f - this.timeReplaceProgress)));
                this.timeLayout.draw(canvas);
                if (this.timeReplaceProgress != 1.0f) {
                    this.textPaint.setAlpha(alpha);
                }
                canvas.restore();
            }
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = this.timeLeft - (elapsedRealtime - this.lastUpdateTime);
        this.timeLeft = j2;
        this.lastUpdateTime = elapsedRealtime;
        if (j2 <= 0) {
            hide(true, this.hideAnimationType);
        }
        if (this.currentAction != 82) {
            invalidate();
        }
    }

    public void invalidate() {
        super.invalidate();
        this.infoTextView.invalidate();
        this.leftImageView.invalidate();
    }

    public void setInfoText(CharSequence charSequence) {
        this.infoText = charSequence;
    }

    public void setHideAnimationType(int i) {
        this.hideAnimationType = i;
    }

    @Keep
    public float getEnterOffset() {
        return this.enterOffset;
    }

    @Keep
    public void setEnterOffset(float f) {
        if (this.enterOffset != f) {
            this.enterOffset = f;
            updatePosition();
        }
    }

    private void updatePosition() {
        setTranslationY(((this.enterOffset - ((float) this.enterOffsetMargin)) + ((float) AndroidUtilities.dp(8.0f))) - this.additionalTranslationY);
        invalidate();
    }

    public Drawable getBackground() {
        return this.backgroundDrawable;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
