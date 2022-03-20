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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77 || i == 44 || i == 78 || i == 79 || i == 100 || i == 101;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || i == 74 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty());
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24 || i == 74;
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

    /* JADX WARNING: Removed duplicated region for block: B:571:0x167d  */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x169d  */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x16c4  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x1709  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x17b3  */
    /* JADX WARNING: Removed duplicated region for block: B:631:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r21, int r22, java.lang.Object r23, java.lang.Object r24, java.lang.Runnable r25, java.lang.Runnable r26) {
        /*
            r20 = this;
            r1 = r20
            r0 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r25
            r6 = r26
            int r7 = android.os.Build.VERSION.SDK_INT
            r8 = 31
            if (r7 < r8) goto L_0x001a
            int r8 = r1.currentAction
            r9 = 52
            if (r8 == r9) goto L_0x180d
        L_0x001a:
            int r8 = r1.currentAction
            r9 = 56
            if (r8 == r9) goto L_0x180d
            r9 = 57
            if (r8 == r9) goto L_0x180d
            r9 = 58
            if (r8 == r9) goto L_0x180d
            r9 = 59
            if (r8 == r9) goto L_0x180d
            r9 = 60
            if (r8 == r9) goto L_0x180d
            r10 = 80
            if (r8 == r10) goto L_0x180d
            r10 = 33
            if (r8 != r10) goto L_0x003a
            goto L_0x180d
        L_0x003a:
            java.lang.Runnable r8 = r1.currentActionRunnable
            if (r8 == 0) goto L_0x0041
            r8.run()
        L_0x0041:
            r8 = 1
            r1.isShown = r8
            r1.currentActionRunnable = r5
            r1.currentCancelRunnable = r6
            r1.currentDialogIds = r0
            r11 = 0
            java.lang.Object r12 = r0.get(r11)
            java.lang.Long r12 = (java.lang.Long) r12
            long r12 = r12.longValue()
            r1.currentAction = r2
            r14 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r14
            r1.currentInfoObject = r3
            long r14 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r14
            android.widget.TextView r14 = r1.undoTextView
            r15 = 2131628401(0x7f0e1171, float:1.8884094E38)
            java.lang.String r9 = "Undo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r15)
            java.lang.String r9 = r9.toUpperCase()
            r14.setText(r9)
            android.widget.ImageView r9 = r1.undoImageView
            r9.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r9 = r1.leftImageView
            r9.setPadding(r11, r11, r11, r11)
            android.widget.TextView r9 = r1.infoTextView
            r14 = 1097859072(0x41700000, float:15.0)
            r9.setTextSize(r8, r14)
            org.telegram.ui.Components.BackupImageView r9 = r1.avatarImageView
            r15 = 8
            r9.setVisibility(r15)
            android.widget.TextView r9 = r1.infoTextView
            r14 = 51
            r9.setGravity(r14)
            android.widget.TextView r9 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            r14 = -2
            r9.height = r14
            r14 = 1095761920(0x41500000, float:13.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r9.topMargin = r14
            r9.bottomMargin = r11
            org.telegram.ui.Components.RLottieImageView r14 = r1.leftImageView
            android.widget.ImageView$ScaleType r15 = android.widget.ImageView.ScaleType.CENTER
            r14.setScaleType(r15)
            org.telegram.ui.Components.RLottieImageView r14 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r15 = 19
            r14.gravity = r15
            r14.bottomMargin = r11
            r14.topMargin = r11
            r15 = 1077936128(0x40400000, float:3.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r14.leftMargin = r15
            r15 = 1113063424(0x42580000, float:54.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r14.width = r15
            r15 = -2
            r14.height = r15
            android.widget.TextView r15 = r1.infoTextView
            r15.setMinHeight(r11)
            r15 = 0
            if (r5 != 0) goto L_0x00e9
            if (r6 != 0) goto L_0x00e9
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r5.<init>(r1)
            r1.setOnClickListener(r5)
            r1.setOnTouchListener(r15)
            goto L_0x00f1
        L_0x00e9:
            r1.setOnClickListener(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r5 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r1.setOnTouchListener(r5)
        L_0x00f1:
            android.widget.TextView r5 = r1.infoTextView
            r5.setMovementMethod(r15)
            boolean r5 = r20.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r6 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r19 = r9
            r8 = 3000(0xbb8, double:1.482E-320)
            r15 = 36
            if (r5 == 0) goto L_0x08a2
            r0 = 74
            if (r2 != r0) goto L_0x0134
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r11)
            r0 = 2131627656(0x7f0e0e88, float:1.8882583E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627665(0x7f0e0e91, float:1.88826E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            r3 = r2
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
            goto L_0x07f7
        L_0x0134:
            r0 = 34
            if (r2 != r0) goto L_0x0193
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x015b
            r2 = 2131628633(0x7f0e1259, float:1.8884564E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r11] = r5
            java.lang.String r5 = "VoipChannelInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0171
        L_0x015b:
            r3 = 1
            r2 = 2131628722(0x7f0e12b2, float:1.8884745E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r11] = r3
            java.lang.String r3 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x0171:
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r0, r3)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r11)
            r1.timeLeft = r8
            r0 = r2
        L_0x018f:
            r2 = 0
        L_0x0190:
            r3 = 0
            goto L_0x07f7
        L_0x0193:
            r0 = 44
            if (r2 != r0) goto L_0x0228
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01d5
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01be
            r0 = 2131628658(0x7f0e1272, float:1.8884615E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r11] = r2
            java.lang.String r2 = "VoipChannelUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0207
        L_0x01be:
            r4 = 1
            r0 = 2131628673(0x7f0e1281, float:1.8884645E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r11] = r2
            java.lang.String r2 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0207
        L_0x01d5:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01f3
            r0 = 2131628627(0x7f0e1253, float:1.8884552E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r11] = r2
            java.lang.String r2 = "VoipChannelChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0207
        L_0x01f3:
            r4 = 1
            r0 = 2131628663(0x7f0e1277, float:1.8884625E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r11] = r2
            java.lang.String r2 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0207:
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
            r2.setVisibility(r11)
            r1.timeLeft = r8
            goto L_0x018f
        L_0x0228:
            r0 = 37
            if (r2 != r0) goto L_0x0296
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x0252
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x025f
        L_0x0252:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x025f:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x027b
            r2 = 2131628657(0x7f0e1271, float:1.8884613E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipChannelUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x028d
        L_0x027b:
            r3 = 1
            r2 = 2131628786(0x7f0e12f2, float:1.8884875E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x028d:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r11)
            r1.timeLeft = r8
            goto L_0x018f
        L_0x0296:
            if (r2 != r10) goto L_0x02a8
            r0 = 2131628704(0x7f0e12a0, float:1.8884708E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558562(0x7f0d00a2, float:1.8742443E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x02a8:
            r0 = 77
            if (r2 != r0) goto L_0x02d4
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2 = 2131558480(0x7f0d0050, float:1.8742277E38)
            r7 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r7
            org.telegram.ui.ActionBar.BaseFragment r3 = r1.parentFragment
            if (r3 == 0) goto L_0x0190
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$Message
            if (r3 == 0) goto L_0x0190
            r3 = r4
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            r4 = 0
            r1.setOnTouchListener(r4)
            android.widget.TextView r5 = r1.infoTextView
            r5.setMovementMethod(r4)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r4.<init>(r1, r3)
            r1.setOnClickListener(r4)
            goto L_0x0190
        L_0x02d4:
            r0 = 30
            if (r2 != r0) goto L_0x0302
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x02e4
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x02e9
        L_0x02e4:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x02e9:
            r2 = 2131628784(0x7f0e12f0, float:1.888487E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558563(0x7f0d00a3, float:1.8742445E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x0302:
            r0 = 35
            if (r2 != r0) goto L_0x0336
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0312
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x031d
        L_0x0312:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x031c
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x031d
        L_0x031c:
            r0 = r6
        L_0x031d:
            r2 = 2131628785(0x7f0e12f1, float:1.8884872E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558563(0x7f0d00a3, float:1.8742445E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x0336:
            r0 = 31
            if (r2 != r0) goto L_0x0364
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0346
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x034b
        L_0x0346:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x034b:
            r2 = 2131628782(0x7f0e12ee, float:1.8884866E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558569(0x7f0d00a9, float:1.8742458E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x0364:
            r0 = 38
            if (r2 != r0) goto L_0x0398
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0384
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628794(0x7f0e12fa, float:1.888489E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = r0.title
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0391
        L_0x0384:
            r0 = 2131628793(0x7f0e12f9, float:1.8884889E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0391:
            r2 = 2131558555(0x7f0d009b, float:1.874243E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x0398:
            r0 = 42
            if (r2 != r0) goto L_0x03c7
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03b3
            r0 = 2131628648(0x7f0e1268, float:1.8884595E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03c0
        L_0x03b3:
            r0 = 2131628762(0x7f0e12da, float:1.8884826E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03c0:
            r2 = 2131558458(0x7f0d003a, float:1.8742232E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x03c7:
            r0 = 43
            if (r2 != r0) goto L_0x03f6
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03e2
            r0 = 2131628649(0x7f0e1269, float:1.8884597E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03ef
        L_0x03e2:
            r0 = 2131628763(0x7f0e12db, float:1.8884828E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03ef:
            r2 = 2131558464(0x7f0d0040, float:1.8742245E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x03f6:
            int r0 = r1.currentAction
            r5 = 39
            if (r0 == r5) goto L_0x07d9
            r5 = 100
            if (r0 != r5) goto L_0x0402
            goto L_0x07d9
        L_0x0402:
            r5 = 40
            if (r0 == r5) goto L_0x0767
            r5 = 101(0x65, float:1.42E-43)
            if (r0 != r5) goto L_0x040c
            goto L_0x0767
        L_0x040c:
            if (r2 != r15) goto L_0x0438
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x041a
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x041f
        L_0x041a:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x041f:
            r2 = 2131628783(0x7f0e12ef, float:1.8884868E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558569(0x7f0d00a9, float:1.8742458E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x0438:
            r5 = 32
            if (r2 != r5) goto L_0x0466
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0448
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x044d
        L_0x0448:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x044d:
            r2 = 2131628753(0x7f0e12d1, float:1.8884808E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r11] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558561(0x7f0d00a1, float:1.8742441E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x0466:
            r5 = 9
            if (r2 == r5) goto L_0x072e
            r5 = 10
            if (r2 != r5) goto L_0x0470
            goto L_0x072e
        L_0x0470:
            r5 = 8
            if (r2 != r5) goto L_0x048b
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626830(0x7f0e0b4e, float:1.8880907E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r11] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            goto L_0x0762
        L_0x048b:
            r5 = 22
            if (r2 != r5) goto L_0x04f6
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r12)
            if (r0 == 0) goto L_0x04ad
            if (r3 != 0) goto L_0x04a2
            r0 = 2131626293(0x7f0e0935, float:1.8879818E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x04a2:
            r0 = 2131626294(0x7f0e0936, float:1.887982E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x04ad:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r4 = -r12
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x04de
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x04de
            if (r3 != 0) goto L_0x04d3
            r0 = 2131626289(0x7f0e0931, float:1.887981E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x04d3:
            r0 = 2131626290(0x7f0e0932, float:1.8879812E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x04de:
            if (r3 != 0) goto L_0x04eb
            r0 = 2131626291(0x7f0e0933, float:1.8879814E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x04eb:
            r0 = 2131626292(0x7f0e0934, float:1.8879816E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x04f6:
            r5 = 23
            if (r2 != r5) goto L_0x0505
            r0 = 2131624918(0x7f0e03d6, float:1.887703E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0762
        L_0x0505:
            r5 = 6
            if (r2 != r5) goto L_0x0524
            r0 = 2131624326(0x7f0e0186, float:1.8875829E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624327(0x7f0e0187, float:1.887583E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558422(0x7f0d0016, float:1.874216E38)
            r3 = 48
            r3 = r15
            r15 = 48
            goto L_0x07f7
        L_0x0524:
            r5 = 13
            if (r0 != r5) goto L_0x0544
            r0 = 2131627544(0x7f0e0e18, float:1.8882355E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627545(0x7f0e0e19, float:1.8882357E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558571(0x7f0d00ab, float:1.8742462E38)
        L_0x053d:
            r3 = 44
            r3 = r15
            r15 = 44
            goto L_0x07f7
        L_0x0544:
            r5 = 14
            if (r0 != r5) goto L_0x055e
            r0 = 2131627546(0x7f0e0e1a, float:1.888236E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627547(0x7f0e0e1b, float:1.8882362E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558573(0x7f0d00ad, float:1.8742466E38)
            goto L_0x053d
        L_0x055e:
            r0 = 7
            if (r2 != r0) goto L_0x0589
            r0 = 2131624334(0x7f0e018e, float:1.8875845E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0582
            r2 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0583
        L_0x0582:
            r2 = 0
        L_0x0583:
            r3 = r2
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            goto L_0x07f7
        L_0x0589:
            r0 = 20
            if (r2 == r0) goto L_0x0619
            r0 = 21
            if (r2 != r0) goto L_0x0593
            goto L_0x0619
        L_0x0593:
            r0 = 19
            if (r2 != r0) goto L_0x059e
            java.lang.CharSequence r0 = r1.infoText
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            goto L_0x0190
        L_0x059e:
            r0 = 82
            if (r2 != r0) goto L_0x05ba
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            boolean r0 = r0.isVideo
            if (r0 == 0) goto L_0x05af
            r0 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r2 = "AttachMediaVideoDeselected"
            goto L_0x05b4
        L_0x05af:
            r0 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = "AttachMediaPhotoDeselected"
        L_0x05b4:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x018f
        L_0x05ba:
            r0 = 78
            if (r2 == r0) goto L_0x05f1
            r0 = 79
            if (r2 != r0) goto L_0x05c3
            goto L_0x05f1
        L_0x05c3:
            r0 = 3
            if (r2 != r0) goto L_0x05d0
            r0 = 2131624877(0x7f0e03ad, float:1.8876946E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x05d9
        L_0x05d0:
            r0 = 2131624931(0x7f0e03e3, float:1.8877056E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x05d9:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0582
            r2 = 2131624878(0x7f0e03ae, float:1.8876948E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0583
        L_0x05f1:
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = 78
            if (r2 != r3) goto L_0x0603
            java.lang.String r2 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0609
        L_0x0603:
            java.lang.String r2 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0609:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x0614
            r2 = 2131558459(0x7f0d003b, float:1.8742234E38)
            goto L_0x0190
        L_0x0614:
            r2 = 2131558465(0x7f0d0041, float:1.8742247E38)
            goto L_0x0190
        L_0x0619:
            r0 = r4
            org.telegram.messenger.MessagesController$DialogFilter r0 = (org.telegram.messenger.MessagesController.DialogFilter) r0
            r4 = 0
            int r7 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x06d5
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r12)
            if (r3 == 0) goto L_0x063c
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r12)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            long r12 = r3.user_id
        L_0x063c:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r12)
            if (r3 == 0) goto L_0x068e
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            r4 = 20
            if (r2 != r4) goto L_0x0671
            r4 = 2131625727(0x7f0e06ff, float:1.887867E38)
            r5 = 2
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r7[r11] = r3
            java.lang.String r0 = r0.name
            r8 = 1
            r7[r8] = r0
            java.lang.String r0 = "FilterUserAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r7)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0720
        L_0x0671:
            r5 = 2
            r8 = 1
            r4 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r7[r11] = r3
            java.lang.String r0 = r0.name
            r7[r8] = r0
            java.lang.String r0 = "FilterUserRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r7)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0720
        L_0x068e:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r12
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            r4 = 20
            if (r2 != r4) goto L_0x06bb
            r4 = 2131625666(0x7f0e06c2, float:1.8878546E38)
            r5 = 2
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r7[r11] = r3
            java.lang.String r0 = r0.name
            r8 = 1
            r7[r8] = r0
            java.lang.String r0 = "FilterChatAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r7)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0720
        L_0x06bb:
            r5 = 2
            r8 = 1
            r4 = 2131625667(0x7f0e06c3, float:1.8878548E38)
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r7[r11] = r3
            java.lang.String r0 = r0.name
            r7[r8] = r0
            java.lang.String r0 = "FilterChatRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r7)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0720
        L_0x06d5:
            r4 = 20
            if (r2 != r4) goto L_0x06fd
            r4 = 2131625670(0x7f0e06c6, float:1.8878555E38)
            r5 = 2
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)
            r7[r11] = r3
            java.lang.String r0 = r0.name
            r3 = 1
            r7[r3] = r0
            java.lang.String r0 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r7)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0720
        L_0x06fd:
            r4 = 2131625671(0x7f0e06c7, float:1.8878557E38)
            r5 = 2
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)
            r7[r11] = r3
            java.lang.String r0 = r0.name
            r3 = 1
            r7[r3] = r0
            java.lang.String r0 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r7)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0720:
            r3 = 20
            if (r2 != r3) goto L_0x0729
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
            goto L_0x0190
        L_0x0729:
            r2 = 2131558447(0x7f0d002f, float:1.874221E38)
            goto L_0x0190
        L_0x072e:
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r3 = 9
            if (r2 != r3) goto L_0x074c
            r2 = 2131625401(0x7f0e05b9, float:1.8878009E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r11] = r0
            java.lang.String r0 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0762
        L_0x074c:
            r3 = 1
            r2 = 2131625402(0x7f0e05ba, float:1.887801E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r11] = r0
            java.lang.String r0 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0762:
            r2 = 2131558427(0x7f0d001b, float:1.874217E38)
            goto L_0x0190
        L_0x0767:
            r2 = 40
            if (r0 != r2) goto L_0x0771
            r0 = 2131628694(0x7f0e1296, float:1.8884688E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            goto L_0x0776
        L_0x0771:
            r0 = 2131628788(0x7f0e12f4, float:1.8884879E38)
            java.lang.String r2 = "VoipGroupVideoRecordSaved"
        L_0x0776:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558565(0x7f0d00a5, float:1.874245E38)
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
            if (r4 < 0) goto L_0x07d6
            if (r0 < 0) goto L_0x07d6
            if (r4 == r0) goto L_0x07d6
            int r5 = r0 + 2
            r3.replace(r0, r5, r6)
            int r5 = r4 + 2
            r3.replace(r4, r5, r6)
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x07d2 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07d2 }
            r7.<init>()     // Catch:{ Exception -> 0x07d2 }
            java.lang.String r8 = "tg://openmessage?user_id="
            r7.append(r8)     // Catch:{ Exception -> 0x07d2 }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x07d2 }
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ Exception -> 0x07d2 }
            long r8 = r8.getClientUserId()     // Catch:{ Exception -> 0x07d2 }
            r7.append(r8)     // Catch:{ Exception -> 0x07d2 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x07d2 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x07d2 }
            r7 = 2
            int r0 = r0 - r7
            r3.setSpan(r5, r4, r0, r10)     // Catch:{ Exception -> 0x07d2 }
            goto L_0x07d6
        L_0x07d2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07d6:
            r0 = r3
            goto L_0x0190
        L_0x07d9:
            r2 = 39
            if (r0 != r2) goto L_0x07e3
            r0 = 2131628695(0x7f0e1297, float:1.888469E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            goto L_0x07e8
        L_0x07e3:
            r0 = 2131628789(0x7f0e12f5, float:1.888488E38)
            java.lang.String r2 = "VoipGroupVideoRecordStarted"
        L_0x07e8:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558566(0x7f0d00a6, float:1.8742451E38)
            r1.timeLeft = r8
            goto L_0x0190
        L_0x07f7:
            android.widget.TextView r4 = r1.infoTextView
            r4.setText(r0)
            if (r2 == 0) goto L_0x0824
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r0.setPlayInDirectionOfCustomEndFrame(r11)
            int r2 = r0.getFramesCount()
            r0.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x082b
        L_0x0824:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x082b:
            if (r3 == 0) goto L_0x086f
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5 = r19
            r5.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r5.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r3)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r11)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x089b
        L_0x086f:
            r5 = r19
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r5.rightMargin = r0
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
        L_0x089b:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x1069
        L_0x08a2:
            r5 = r19
            int r8 = r1.currentAction
            r9 = 45
            if (r8 == r9) goto L_0x106e
            r9 = 46
            if (r8 == r9) goto L_0x106e
            r9 = 47
            if (r8 == r9) goto L_0x106e
            r9 = 51
            if (r8 == r9) goto L_0x106e
            r9 = 50
            if (r8 == r9) goto L_0x106e
            r9 = 52
            if (r8 == r9) goto L_0x106e
            r9 = 53
            if (r8 == r9) goto L_0x106e
            r9 = 54
            if (r8 == r9) goto L_0x106e
            r9 = 55
            if (r8 == r9) goto L_0x106e
            r9 = 56
            if (r8 == r9) goto L_0x106e
            r9 = 57
            if (r8 == r9) goto L_0x106e
            r9 = 58
            if (r8 == r9) goto L_0x106e
            r9 = 59
            if (r8 == r9) goto L_0x106e
            r9 = 60
            if (r8 == r9) goto L_0x106e
            r9 = 71
            if (r8 == r9) goto L_0x106e
            r9 = 70
            if (r8 == r9) goto L_0x106e
            r9 = 75
            if (r8 == r9) goto L_0x106e
            r9 = 76
            if (r8 == r9) goto L_0x106e
            r9 = 41
            if (r8 == r9) goto L_0x106e
            r9 = 78
            if (r8 == r9) goto L_0x106e
            r9 = 79
            if (r8 == r9) goto L_0x106e
            r9 = 61
            if (r8 == r9) goto L_0x106e
            r9 = 80
            if (r8 != r9) goto L_0x0904
            goto L_0x106e
        L_0x0904:
            r7 = 24
            if (r8 == r7) goto L_0x0var_
            r7 = 25
            if (r8 != r7) goto L_0x090e
            goto L_0x0var_
        L_0x090e:
            r4 = 11
            if (r8 != r4) goto L_0x097f
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624462(0x7f0e020e, float:1.8876104E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558427(0x7f0d001b, float:1.874217E38)
            r2.setAnimation(r3, r15, r15)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r11)
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
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x1069
        L_0x097f:
            r4 = 15
            if (r8 != r4) goto L_0x0a4f
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625663(0x7f0e06bf, float:1.887854E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r0.setAnimation(r2, r15, r15)
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
            r5.leftMargin = r2
            r5.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625662(0x7f0e06be, float:1.8878538E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0a1c
            if (r0 < 0) goto L_0x0a1c
            if (r4 == r0) goto L_0x0a1c
            int r3 = r0 + 1
            r2.replace(r0, r3, r6)
            int r3 = r4 + 1
            r2.replace(r4, r3, r6)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r3.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r2.setSpan(r3, r4, r0, r10)
        L_0x0a1c:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r11)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r11)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 2
            r0.setMaxLines(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r11)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x1069
        L_0x0a4f:
            r4 = 16
            if (r8 == r4) goto L_0x0d8b
            r4 = 17
            if (r8 != r4) goto L_0x0a59
            goto L_0x0d8b
        L_0x0a59:
            r4 = 18
            if (r8 != r4) goto L_0x0ae4
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
            r5.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r5.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.bottomMargin = r0
            r0 = -1
            r5.height = r0
            r0 = 51
            r14.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r14.bottomMargin = r0
            r14.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            android.widget.TextView r0 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r2.<init>()
            r0.setMovementMethod(r2)
            goto L_0x1069
        L_0x0ae4:
            r4 = 12
            if (r8 != r4) goto L_0x0b7e
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625045(0x7f0e0455, float:1.8877287E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166165(0x7var_d5, float:1.7946568E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131625046(0x7f0e0456, float:1.8877289E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0b56
            if (r0 < 0) goto L_0x0b56
            if (r4 == r0) goto L_0x0b56
            int r3 = r0 + 1
            r2.replace(r0, r3, r6)
            int r3 = r4 + 1
            r2.replace(r4, r3, r6)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r3.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r2.setSpan(r3, r4, r0, r10)
        L_0x0b56:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r11)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r11)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 2
            r0.setMaxLines(r4)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            goto L_0x1069
        L_0x0b7e:
            r4 = 2
            if (r8 == r4) goto L_0x0d26
            r4 = 4
            if (r8 != r4) goto L_0x0b86
            goto L_0x0d26
        L_0x0b86:
            r4 = 82
            if (r2 != r4) goto L_0x0c4b
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r0
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            android.widget.TextView r2 = r1.infoTextView
            boolean r3 = r0.isVideo
            if (r3 == 0) goto L_0x0b9f
            r3 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r4 = "AttachMediaVideoDeselected"
            goto L_0x0ba4
        L_0x0b9f:
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r4 = "AttachMediaPhotoDeselected"
        L_0x0ba4:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r11)
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
            r2.setVisibility(r11)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setRoundRadius(r3)
            java.lang.String r2 = r0.thumbPath
            if (r2 == 0) goto L_0x0be4
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r4 = 0
            r0.setImage(r2, r4, r3)
            goto L_0x1069
        L_0x0be4:
            java.lang.String r2 = r0.path
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            int r3 = r0.orientation
            r4 = 1
            r2.setOrientation(r3, r4)
            boolean r2 = r0.isVideo
            if (r2 == 0) goto L_0x0c1b
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
            r4 = 0
            r2.setImage(r0, r4, r3)
            goto L_0x1069
        L_0x0c1b:
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
            r4 = 0
            r2.setImage(r0, r4, r3)
            goto L_0x1069
        L_0x0CLASSNAME:
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImageDrawable(r2)
            goto L_0x1069
        L_0x0c4b:
            r2 = 1110704128(0x42340000, float:45.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r5.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r5.topMargin = r2
            r5.rightMargin = r11
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r11)
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
            if (r2 == r3) goto L_0x0ce9
            if (r2 == 0) goto L_0x0ce9
            r3 = 26
            if (r2 != r3) goto L_0x0c8a
            goto L_0x0ce9
        L_0x0c8a:
            r3 = 27
            if (r2 != r3) goto L_0x0c9d
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624932(0x7f0e03e4, float:1.8877058E38)
            java.lang.String r4 = "ChatsDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cf7
        L_0x0c9d:
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r12)
            if (r2 == 0) goto L_0x0cda
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = -r12
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x0ccb
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0ccb
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624784(0x7f0e0350, float:1.8876757E38)
            java.lang.String r4 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cf7
        L_0x0ccb:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625915(0x7f0e07bb, float:1.8879051E38)
            java.lang.String r4 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cf7
        L_0x0cda:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624881(0x7f0e03b1, float:1.8876954E38)
            java.lang.String r4 = "ChatDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cf7
        L_0x0ce9:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625968(0x7f0e07f0, float:1.8879159E38)
            java.lang.String r4 = "HistoryClearedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x0cf7:
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x1069
            r2 = 0
        L_0x0cfe:
            int r3 = r21.size()
            if (r2 >= r3) goto L_0x1069
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Object r4 = r0.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            long r4 = r4.longValue()
            int r7 = r1.currentAction
            if (r7 == 0) goto L_0x0d1f
            r8 = 26
            if (r7 != r8) goto L_0x0d1d
            goto L_0x0d1f
        L_0x0d1d:
            r7 = 0
            goto L_0x0d20
        L_0x0d1f:
            r7 = 1
        L_0x0d20:
            r3.addDialogAction(r4, r7)
            int r2 = r2 + 1
            goto L_0x0cfe
        L_0x0d26:
            r3 = 2
            if (r2 != r3) goto L_0x0d38
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624877(0x7f0e03ad, float:1.8876946E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0d46
        L_0x0d38:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624931(0x7f0e03e3, float:1.8877056E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0d46:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.topMargin = r0
            r5.rightMargin = r11
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r11)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558419(0x7f0d0013, float:1.8742153E38)
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x1069
        L_0x0d8b:
            r7 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r7
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
            if (r2 == 0) goto L_0x0dd0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625306(0x7f0e055a, float:1.8877816E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165408(0x7var_e0, float:1.7945032E38)
            r0.setImageResource(r2)
            goto L_0x0e79
        L_0x0dd0:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0ded
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625163(0x7f0e04cb, float:1.8877526E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0dea:
            r7 = 1096810496(0x41600000, float:14.0)
            goto L_0x0e47
        L_0x0ded:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0e20
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r7 = 1096810496(0x41600000, float:14.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r8, r11)
            r3.setText(r2)
            goto L_0x0dea
        L_0x0e20:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625305(0x7f0e0559, float:1.8877814E38)
            r4 = 1
            java.lang.Object[] r7 = new java.lang.Object[r4]
            r7[r11] = r0
            java.lang.String r4 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r7)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r7 = 1096810496(0x41600000, float:14.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r8, r11)
            r2.setText(r3)
        L_0x0e47:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.height = r0
        L_0x0e79:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627885(0x7f0e0f6d, float:1.8883047E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0ecb
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
            r2.setVisibility(r11)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = r1.getThemedColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r11)
            goto L_0x0edb
        L_0x0ecb:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0edb:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r2
            r5.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.bottomMargin = r0
            r0 = -1
            r5.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            goto L_0x1069
        L_0x0var_:
            r2 = 8
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r4
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r11)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0fd4
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r4.setTypeface(r7)
            android.widget.TextView r4 = r1.infoTextView
            r7 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r4.setTextSize(r8, r7)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r7 = r1.getThemedColor(r2)
            java.lang.String r8 = "BODY.**"
            r4.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r7 = r1.getThemedColor(r2)
            java.lang.String r8 = "Wibe Big.**"
            r4.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r7 = r1.getThemedColor(r2)
            java.lang.String r8 = "Wibe Big 3.**"
            r4.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = r1.getThemedColor(r2)
            java.lang.String r7 = "Wibe Small.**"
            r4.setLayerColor(r7, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627466(0x7f0e0dca, float:1.8882197E38)
            java.lang.String r7 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558464(0x7f0d0040, float:1.8742245E38)
            r7 = 28
            r8 = 28
            r2.setAnimation(r4, r7, r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r11)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r11)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0fac
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627468(0x7f0e0dcc, float:1.8882201E38)
            r7 = 2
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r8[r11] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r7)
            r3 = 1
            r8[r3] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            r2.setText(r0)
            goto L_0x0fc5
        L_0x0fac:
            r3 = 1
            r7 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627467(0x7f0e0dcb, float:1.88822E38)
            java.lang.Object[] r8 = new java.lang.Object[r3]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r7)
            r8[r11] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            r2.setText(r0)
        L_0x0fc5:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r5.topMargin = r0
            goto L_0x1058
        L_0x0fd4:
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
            r5.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627465(0x7f0e0dc9, float:1.8882195E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558458(0x7f0d003a, float:1.8742232E38)
            r3 = 28
            r4 = 28
            r0.setAnimation(r2, r3, r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = r1.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r11)
        L_0x1058:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x1069:
            r0 = 0
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1687
        L_0x106e:
            android.widget.ImageView r0 = r1.undoImageView
            r8 = 8
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r11)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r8 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r8)
            r8 = -1
            int r0 = r1.currentAction
            r10 = 76
            r14 = 1091567616(0x41100000, float:9.0)
            if (r0 != r10) goto L_0x10b2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624646(0x7f0e02c6, float:1.8876478E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558450(0x7f0d0032, float:1.8742216E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
        L_0x10af:
            r0 = 1
            goto L_0x1649
        L_0x10b2:
            r10 = 75
            if (r0 != r10) goto L_0x10db
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625894(0x7f0e07a6, float:1.8879009E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10af
        L_0x10db:
            r10 = 70
            if (r2 != r10) goto L_0x1162
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r11)
            r2 = 2592000(0x278d00, float:3.632166E-39)
            if (r0 < r2) goto L_0x10fe
            r2 = 2592000(0x278d00, float:3.632166E-39)
            int r0 = r0 / r2
            java.lang.String r2 = "Months"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x112d
        L_0x10fe:
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x110e
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x112d
        L_0x110e:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x111b
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x112d
        L_0x111b:
            r2 = 60
            if (r0 < r2) goto L_0x1127
            int r0 = r0 / r2
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x112d
        L_0x1127:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x112d:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624477(0x7f0e021d, float:1.8876135E38)
            r4 = 1
            java.lang.Object[] r7 = new java.lang.Object[r4]
            r7[r11] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r7)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r11, r11, r11, r2)
            r0 = 1
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1649
        L_0x1162:
            r2 = 71
            if (r0 != r2) goto L_0x1197
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558443(0x7f0d002b, float:1.8742202E38)
            r0.setAnimation(r2, r15, r15)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r11, r11, r11, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1648
        L_0x1197:
            r2 = 45
            if (r0 != r2) goto L_0x11c1
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626018(0x7f0e0822, float:1.887926E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558439(0x7f0d0027, float:1.8742194E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10af
        L_0x11c1:
            r2 = 46
            if (r0 != r2) goto L_0x11eb
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626019(0x7f0e0823, float:1.8879262E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558439(0x7f0d0027, float:1.8742194E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10af
        L_0x11eb:
            r2 = 47
            if (r0 != r2) goto L_0x1220
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558470(0x7f0d0046, float:1.8742257E38)
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r11, r11, r11, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10af
        L_0x1220:
            r2 = 1096810496(0x41600000, float:14.0)
            r10 = 51
            if (r0 != r10) goto L_0x124a
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r4 = "AudioSpeedNormal"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558408(0x7f0d0008, float:1.874213E38)
            r0.setAnimation(r3, r15, r15)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1648
        L_0x124a:
            r10 = 50
            if (r0 != r10) goto L_0x1272
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624442(0x7f0e01fa, float:1.8876064E38)
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r3, r15, r15)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1648
        L_0x1272:
            r10 = 52
            if (r0 == r10) goto L_0x15a9
            r10 = 56
            if (r0 == r10) goto L_0x15a9
            r10 = 57
            if (r0 == r10) goto L_0x15a9
            r10 = 58
            if (r0 == r10) goto L_0x15a9
            r10 = 59
            if (r0 == r10) goto L_0x15a9
            r10 = 60
            if (r0 == r10) goto L_0x15a9
            r10 = 80
            if (r0 != r10) goto L_0x1290
            goto L_0x15a9
        L_0x1290:
            r7 = 54
            if (r0 != r7) goto L_0x12ba
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624820(0x7f0e0374, float:1.887683E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558511(0x7f0d006f, float:1.874234E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1648
        L_0x12ba:
            r7 = 55
            if (r0 != r7) goto L_0x12e4
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558510(0x7f0d006e, float:1.8742338E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1648
        L_0x12e4:
            r7 = 41
            if (r0 != r7) goto L_0x1395
            if (r4 != 0) goto L_0x1363
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x130a
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131626064(0x7f0e0850, float:1.8879354E38)
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1387
        L_0x130a:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r12)
            if (r0 == 0) goto L_0x1339
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r12
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626063(0x7f0e084f, float:1.8879352E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r10[r11] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1387
        L_0x1339:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626065(0x7f0e0851, float:1.8879356E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r10[r11] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1387
        L_0x1363:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626062(0x7f0e084e, float:1.887935E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r7 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r7, r0)
            r10[r11] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x1387:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558427(0x7f0d001b, float:1.874217E38)
            r0.setAnimation(r3, r15, r15)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1648
        L_0x1395:
            r7 = 53
            if (r0 != r7) goto L_0x14df
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1485
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.clientUserId
            int r7 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x13e2
            int r0 = r0.intValue()
            r3 = 1
            if (r0 != r3) goto L_0x13c4
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625864(0x7f0e0788, float:1.8878948E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13d6
        L_0x13c4:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625868(0x7f0e078c, float:1.8878956E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x13d6:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558508(0x7f0d006c, float:1.8742334E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x14d9
        L_0x13e2:
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r12)
            if (r3 == 0) goto L_0x1430
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r7 = -r12
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x1417
            android.widget.TextView r0 = r1.infoTextView
            r7 = 2131625863(0x7f0e0787, float:1.8878946E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r8[r11] = r3
            java.lang.String r3 = "FwdMessageToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r7, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x147a
        L_0x1417:
            android.widget.TextView r0 = r1.infoTextView
            r7 = 2131625867(0x7f0e078b, float:1.8878954E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r8[r11] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r7, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x147a
        L_0x1430:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x1460
            android.widget.TextView r0 = r1.infoTextView
            r7 = 2131625865(0x7f0e0789, float:1.887895E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r8[r11] = r3
            java.lang.String r3 = "FwdMessageToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r7, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x147a
        L_0x1460:
            android.widget.TextView r0 = r1.infoTextView
            r7 = 2131625869(0x7f0e078d, float:1.8878958E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r8[r11] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r7, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x147a:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x14d6
        L_0x1485:
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x14b0
            android.widget.TextView r0 = r1.infoTextView
            r7 = 2131625862(0x7f0e0786, float:1.8878944E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r9 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3)
            r8[r11] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r7, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x14cc
        L_0x14b0:
            android.widget.TextView r0 = r1.infoTextView
            r7 = 2131625866(0x7f0e078a, float:1.8878952E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r8[r11] = r3
            java.lang.String r3 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r7, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x14cc:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x14d6:
            r3 = 300(0x12c, double:1.48E-321)
            r8 = r3
        L_0x14d9:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1648
        L_0x14df:
            r7 = 61
            if (r0 != r7) goto L_0x1648
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1575
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1512
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624580(0x7f0e0284, float:1.8876344E38)
            java.lang.String r4 = "BackgroundToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558508(0x7f0d006c, float:1.8742334E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x15a3
        L_0x1512:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r12)
            if (r0 == 0) goto L_0x1541
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r12
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624579(0x7f0e0283, float:1.8876342E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r10[r11] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x156a
        L_0x1541:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624581(0x7f0e0285, float:1.8876346E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r10[r11] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x156a:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x15a3
        L_0x1575:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624578(0x7f0e0282, float:1.887634E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.String r7 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r7, r0)
            r10[r11] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x15a3:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1648
        L_0x15a9:
            r3 = 31
            if (r7 < r3) goto L_0x15ae
            return
        L_0x15ae:
            r3 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r4 = 80
            if (r0 != r4) goto L_0x15c5
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625433(0x7f0e05d9, float:1.8878074E38)
            java.lang.String r7 = "EmailCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
            goto L_0x1635
        L_0x15c5:
            r4 = 60
            if (r0 != r4) goto L_0x15d8
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131627241(0x7f0e0ce9, float:1.888174E38)
            java.lang.String r7 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
            goto L_0x1635
        L_0x15d8:
            r4 = 56
            if (r0 != r4) goto L_0x15eb
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131628508(0x7f0e11dc, float:1.888431E38)
            java.lang.String r7 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
            goto L_0x1635
        L_0x15eb:
            r4 = 57
            if (r0 != r4) goto L_0x15fe
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.String r7 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
            goto L_0x1635
        L_0x15fe:
            r4 = 52
            if (r0 != r4) goto L_0x1611
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626398(0x7f0e099e, float:1.8880031E38)
            java.lang.String r7 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
            goto L_0x1635
        L_0x1611:
            r4 = 59
            if (r0 != r4) goto L_0x1627
            r3 = 2131558562(0x7f0d00a2, float:1.8742443E38)
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626208(0x7f0e08e0, float:1.8879646E38)
            java.lang.String r7 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
            goto L_0x1635
        L_0x1627:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131628273(0x7f0e10f1, float:1.8883834E38)
            java.lang.String r7 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.setText(r4)
        L_0x1635:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
        L_0x1648:
            r0 = 0
        L_0x1649:
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
            r5.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r5.rightMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 0
            r3.setProgress(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r3.playAnimation()
            r3 = 0
            int r5 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x1687
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r4.<init>(r1)
            r3.postDelayed(r4, r8)
        L_0x1687:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.widget.TextView r4 = r1.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x16b4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ". "
            r4.append(r5)
            android.widget.TextView r5 = r1.subinfoTextView
            java.lang.CharSequence r5 = r5.getText()
            r4.append(r5)
            java.lang.String r6 = r4.toString()
        L_0x16b4:
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r3)
            boolean r3 = r20.isMultilineSubInfo()
            if (r3 == 0) goto L_0x1709
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x16d4
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x16d4:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r11, r11)
            r5 = 0
            r21 = r20
            r22 = r2
            r23 = r0
            r24 = r3
            r25 = r4
            r26 = r5
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r0 = r1.subinfoTextView
            int r0 = r0.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.undoViewHeight = r0
            goto L_0x17ad
        L_0x1709:
            boolean r3 = r20.hasSubInfo()
            if (r3 == 0) goto L_0x1719
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x17ad
        L_0x1719:
            android.view.ViewParent r3 = r20.getParent()
            boolean r3 = r3 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x17ad
            android.view.ViewParent r3 = r20.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = r3.getMeasuredWidth()
            int r5 = r3.getPaddingLeft()
            int r4 = r4 - r5
            int r3 = r3.getPaddingRight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x173b
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
        L_0x173b:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r3
            android.widget.TextView r3 = r1.infoTextView
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            r5 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r11, r11)
            r7 = 0
            r21 = r20
            r22 = r3
            r23 = r4
            r24 = r5
            r25 = r6
            r26 = r7
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r3 = r1.infoTextView
            int r3 = r3.getMeasuredHeight()
            int r4 = r1.currentAction
            r5 = 16
            if (r4 == r5) goto L_0x1777
            r5 = 17
            if (r4 == r5) goto L_0x1777
            r5 = 18
            if (r4 != r5) goto L_0x1774
            goto L_0x1777
        L_0x1774:
            r15 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1779
        L_0x1777:
            r15 = 1096810496(0x41600000, float:14.0)
        L_0x1779:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r3 = r3 + r2
            r1.undoViewHeight = r3
            int r2 = r1.currentAction
            r4 = 18
            if (r2 != r4) goto L_0x1793
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x17ad
        L_0x1793:
            r4 = 25
            if (r2 != r4) goto L_0x17a4
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x17ad
        L_0x17a4:
            if (r0 == 0) goto L_0x17ad
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r0
            r1.undoViewHeight = r3
        L_0x17ad:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x180d
            r1.setVisibility(r11)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x17bd
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x17bf
        L_0x17bd:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x17bf:
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
            if (r4 == 0) goto L_0x17dc
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x17de
        L_0x17dc:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x17de:
            int r6 = r1.enterOffsetMargin
            int r7 = r1.undoViewHeight
            int r6 = r6 + r7
            float r6 = (float) r6
            float r5 = r5 * r6
            r2[r11] = r5
            if (r4 == 0) goto L_0x17ed
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x17ef
        L_0x17ed:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x17ef:
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "enterOffset"
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r1, r4, r2)
            r3[r11] = r2
            r0.playTogether(r3)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x180d:
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
