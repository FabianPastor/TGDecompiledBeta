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

    public UndoView(Context context, BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.hideAnimationType = 1;
        this.timeReplaceProgress = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = baseFragment;
        this.fromTop = z;
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(getThemedColor("undo_infoColor"));
        this.infoTextView.setLinkTextColor(getThemedColor("undo_cancelColor"));
        this.infoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(getThemedColor("undo_infoColor"));
        this.subinfoTextView.setLinkTextColor(getThemedColor("undo_cancelColor"));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
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
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(30, 30.0f, 19, 15.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new UndoView$$ExternalSyntheticLambda0(this));
        ImageView imageView = new ImageView(context);
        this.undoImageView = imageView;
        imageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        TextView textView3 = new TextView(context);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(getThemedColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
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
                    fArr[0] = f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
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
            setEnterOffset(f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)));
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

    /* JADX WARNING: Removed duplicated region for block: B:264:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0837  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0876  */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x1596  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x15b6  */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x15ce  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x15df  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x1624  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x16ce  */
    /* JADX WARNING: Removed duplicated region for block: B:612:? A[RETURN, SYNTHETIC] */
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
            int r7 = r1.currentAction
            r8 = 52
            if (r7 == r8) goto L_0x172c
        L_0x001a:
            int r7 = r1.currentAction
            r8 = 56
            if (r7 == r8) goto L_0x172c
            r8 = 57
            if (r7 == r8) goto L_0x172c
            r8 = 58
            if (r7 == r8) goto L_0x172c
            r8 = 59
            if (r7 == r8) goto L_0x172c
            r8 = 60
            if (r7 == r8) goto L_0x172c
            r9 = 80
            if (r7 == r9) goto L_0x172c
            r9 = 33
            if (r7 != r9) goto L_0x003a
            goto L_0x172c
        L_0x003a:
            java.lang.Runnable r7 = r1.currentActionRunnable
            if (r7 == 0) goto L_0x0041
            r7.run()
        L_0x0041:
            r7 = 1
            r1.isShown = r7
            r1.currentActionRunnable = r5
            r1.currentCancelRunnable = r6
            r1.currentDialogIds = r0
            r10 = 0
            java.lang.Object r11 = r0.get(r10)
            java.lang.Long r11 = (java.lang.Long) r11
            long r11 = r11.longValue()
            r1.currentAction = r2
            r13 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r13
            r1.currentInfoObject = r3
            long r13 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r13
            android.widget.TextView r13 = r1.undoTextView
            r14 = 2131628283(0x7f0e10fb, float:1.8883854E38)
            java.lang.String r15 = "Undo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            java.lang.String r14 = r14.toUpperCase()
            r13.setText(r14)
            android.widget.ImageView r13 = r1.undoImageView
            r13.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r13 = r1.leftImageView
            r13.setPadding(r10, r10, r10, r10)
            android.widget.TextView r13 = r1.infoTextView
            r14 = 1097859072(0x41700000, float:15.0)
            r13.setTextSize(r7, r14)
            org.telegram.ui.Components.BackupImageView r13 = r1.avatarImageView
            r15 = 8
            r13.setVisibility(r15)
            android.widget.TextView r13 = r1.infoTextView
            r8 = 51
            r13.setGravity(r8)
            android.widget.TextView r8 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            r13 = -2
            r8.height = r13
            r13 = 1095761920(0x41500000, float:13.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r8.topMargin = r13
            r8.bottomMargin = r10
            org.telegram.ui.Components.RLottieImageView r13 = r1.leftImageView
            android.widget.ImageView$ScaleType r14 = android.widget.ImageView.ScaleType.CENTER
            r13.setScaleType(r14)
            org.telegram.ui.Components.RLottieImageView r13 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            r14 = 19
            r13.gravity = r14
            r13.bottomMargin = r10
            r13.topMargin = r10
            r14 = 1077936128(0x40400000, float:3.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13.leftMargin = r14
            r14 = 1113063424(0x42580000, float:54.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13.width = r14
            r14 = -2
            r13.height = r14
            android.widget.TextView r14 = r1.infoTextView
            r14.setMinHeight(r10)
            r14 = 0
            if (r5 != 0) goto L_0x00e9
            if (r6 != 0) goto L_0x00e9
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r5.<init>(r1)
            r1.setOnClickListener(r5)
            r1.setOnTouchListener(r14)
            goto L_0x00f1
        L_0x00e9:
            r1.setOnClickListener(r14)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r5 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r1.setOnTouchListener(r5)
        L_0x00f1:
            android.widget.TextView r5 = r1.infoTextView
            r5.setMovementMethod(r14)
            boolean r5 = r20.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r15 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r19 = r15
            r14 = 3000(0xbb8, double:1.482E-320)
            r6 = 36
            if (r5 == 0) goto L_0x08a7
            r0 = 74
            if (r2 != r0) goto L_0x0132
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r10)
            r0 = 2131627554(0x7f0e0e22, float:1.8882376E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627563(0x7f0e0e2b, float:1.8882394E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r15 = 2131558450(0x7f0d0032, float:1.8742216E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
        L_0x012e:
            r11 = r19
            goto L_0x0801
        L_0x0132:
            r0 = 34
            if (r2 != r0) goto L_0x0191
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x0158
            r2 = 2131628508(0x7f0e11dc, float:1.888431E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r4
            java.lang.String r4 = "VoipChannelInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x016d
        L_0x0158:
            r2 = 2131628595(0x7f0e1233, float:1.8884487E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r4
            java.lang.String r4 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x016d:
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r0, r3)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r10)
            r1.timeLeft = r14
            r0 = r2
        L_0x018b:
            r11 = r19
            r14 = 0
            r15 = 0
            goto L_0x0801
        L_0x0191:
            r0 = 44
            if (r2 != r0) goto L_0x0222
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01d1
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01bb
            r0 = 2131628532(0x7f0e11f4, float:1.888436E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r10] = r2
            java.lang.String r2 = "VoipChannelUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0201
        L_0x01bb:
            r0 = 2131628547(0x7f0e1203, float:1.888439E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r10] = r2
            java.lang.String r2 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0201
        L_0x01d1:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01ee
            r0 = 2131628502(0x7f0e11d6, float:1.8884298E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r2 = r2.title
            r4[r10] = r2
            java.lang.String r2 = "VoipChannelChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0201
        L_0x01ee:
            r0 = 2131628537(0x7f0e11f9, float:1.888437E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r2 = r2.title
            r4[r10] = r2
            java.lang.String r2 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0201:
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
            r2.setVisibility(r10)
            r1.timeLeft = r14
            goto L_0x018b
        L_0x0222:
            r0 = 37
            if (r2 != r0) goto L_0x028e
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x024c
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x0259
        L_0x024c:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x0259:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x0274
            r2 = 2131628531(0x7f0e11f3, float:1.8884357E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipChannelUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0285
        L_0x0274:
            r2 = 2131628656(0x7f0e1270, float:1.888461E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0285:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r10)
            r1.timeLeft = r14
            goto L_0x018b
        L_0x028e:
            if (r2 != r9) goto L_0x02a6
            r0 = 2131628578(0x7f0e1222, float:1.8884453E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558543(0x7f0d008f, float:1.8742405E38)
            r1.timeLeft = r14
            r11 = r19
            r14 = 0
            r15 = 2131558543(0x7f0d008f, float:1.8742405E38)
            goto L_0x0801
        L_0x02a6:
            r0 = 77
            if (r2 != r0) goto L_0x02d5
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r15 = 2131558474(0x7f0d004a, float:1.8742265E38)
            r2 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r2
            org.telegram.ui.ActionBar.BaseFragment r2 = r1.parentFragment
            if (r2 == 0) goto L_0x02d1
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$Message
            if (r2 == 0) goto L_0x02d1
            r2 = r4
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            r5 = 0
            r1.setOnTouchListener(r5)
            android.widget.TextView r3 = r1.infoTextView
            r3.setMovementMethod(r5)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r3.<init>(r1, r2)
            r1.setOnClickListener(r3)
            goto L_0x02d2
        L_0x02d1:
            r5 = 0
        L_0x02d2:
            r14 = r5
            goto L_0x012e
        L_0x02d5:
            r0 = 30
            r5 = 0
            if (r2 != r0) goto L_0x0309
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x02e6
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x02eb
        L_0x02e6:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x02eb:
            r2 = 2131628654(0x7f0e126e, float:1.8884607E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558544(0x7f0d0090, float:1.8742407E38)
            r1.timeLeft = r14
        L_0x0301:
            r14 = r5
            r11 = r19
            r15 = 2131558544(0x7f0d0090, float:1.8742407E38)
            goto L_0x0801
        L_0x0309:
            r0 = 35
            if (r2 != r0) goto L_0x033c
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0319
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0325
        L_0x0319:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0323
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0325
        L_0x0323:
            r0 = r19
        L_0x0325:
            r2 = 2131628655(0x7f0e126f, float:1.8884609E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558544(0x7f0d0090, float:1.8742407E38)
            r1.timeLeft = r14
            goto L_0x0301
        L_0x033c:
            r0 = 31
            if (r2 != r0) goto L_0x036f
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x034c
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0351
        L_0x034c:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0351:
            r2 = 2131628652(0x7f0e126c, float:1.8884603E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558550(0x7f0d0096, float:1.8742419E38)
            r1.timeLeft = r14
        L_0x0367:
            r14 = r5
            r11 = r19
            r15 = 2131558550(0x7f0d0096, float:1.8742419E38)
            goto L_0x0801
        L_0x036f:
            r0 = 38
            if (r2 != r0) goto L_0x03a8
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x038e
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628664(0x7f0e1278, float:1.8884627E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x039b
        L_0x038e:
            r0 = 2131628663(0x7f0e1277, float:1.8884625E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x039b:
            r2 = 2131558536(0x7f0d0088, float:1.874239E38)
            r1.timeLeft = r14
            r14 = r5
            r11 = r19
            r15 = 2131558536(0x7f0d0088, float:1.874239E38)
            goto L_0x0801
        L_0x03a8:
            r0 = 42
            if (r2 != r0) goto L_0x03dd
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03c3
            r0 = 2131628523(0x7f0e11eb, float:1.8884341E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03d0
        L_0x03c3:
            r0 = 2131628634(0x7f0e125a, float:1.8884566E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03d0:
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r1.timeLeft = r14
            r14 = r5
            r11 = r19
            r15 = 2131558454(0x7f0d0036, float:1.8742224E38)
            goto L_0x0801
        L_0x03dd:
            r0 = 43
            if (r2 != r0) goto L_0x0412
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03f8
            r0 = 2131628524(0x7f0e11ec, float:1.8884343E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0405
        L_0x03f8:
            r0 = 2131628635(0x7f0e125b, float:1.8884568E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0405:
            r2 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r1.timeLeft = r14
            r14 = r5
            r11 = r19
            r15 = 2131558459(0x7f0d003b, float:1.8742234E38)
            goto L_0x0801
        L_0x0412:
            int r0 = r1.currentAction
            r13 = 39
            if (r0 == r13) goto L_0x07df
            r13 = 100
            if (r0 != r13) goto L_0x041e
            goto L_0x07df
        L_0x041e:
            r13 = 40
            if (r0 == r13) goto L_0x0768
            r13 = 101(0x65, float:1.42E-43)
            if (r0 != r13) goto L_0x0428
            goto L_0x0768
        L_0x0428:
            if (r2 != r6) goto L_0x0453
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0436
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x043b
        L_0x0436:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x043b:
            r2 = 2131628653(0x7f0e126d, float:1.8884605E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558550(0x7f0d0096, float:1.8742419E38)
            r1.timeLeft = r14
            goto L_0x0367
        L_0x0453:
            r9 = 32
            if (r2 != r9) goto L_0x0486
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0463
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0468
        L_0x0463:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0468:
            r2 = 2131628625(0x7f0e1251, float:1.8884548E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558542(0x7f0d008e, float:1.8742403E38)
            r1.timeLeft = r14
            r14 = r5
            r11 = r19
            r15 = 2131558542(0x7f0d008e, float:1.8742403E38)
            goto L_0x0801
        L_0x0486:
            r9 = 9
            if (r2 == r9) goto L_0x072e
            r9 = 10
            if (r2 != r9) goto L_0x0490
            goto L_0x072e
        L_0x0490:
            r9 = 8
            if (r2 != r9) goto L_0x04aa
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626769(0x7f0e0b11, float:1.8880784E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0760
        L_0x04aa:
            r9 = 22
            if (r2 != r9) goto L_0x0515
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r11)
            if (r0 == 0) goto L_0x04cc
            if (r3 != 0) goto L_0x04c1
            r0 = 2131626235(0x7f0e08fb, float:1.88797E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x04c1:
            r0 = 2131626236(0x7f0e08fc, float:1.8879702E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x04cc:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r11 = -r11
            java.lang.Long r2 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x04fd
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x04fd
            if (r3 != 0) goto L_0x04f2
            r0 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x04f2:
            r0 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x04fd:
            if (r3 != 0) goto L_0x050a
            r0 = 2131626233(0x7f0e08f9, float:1.8879696E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x050a:
            r0 = 2131626234(0x7f0e08fa, float:1.8879698E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x0515:
            r9 = 23
            if (r2 != r9) goto L_0x0524
            r0 = 2131624904(0x7f0e03c8, float:1.8877E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0760
        L_0x0524:
            r9 = 6
            if (r2 != r9) goto L_0x0540
            r0 = 2131624321(0x7f0e0181, float:1.8875818E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624322(0x7f0e0182, float:1.887582E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r6 = 48
            goto L_0x012e
        L_0x0540:
            r9 = 13
            if (r0 != r9) goto L_0x055d
            r0 = 2131627462(0x7f0e0dc6, float:1.888219E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627463(0x7f0e0dc7, float:1.8882191E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558552(0x7f0d0098, float:1.8742423E38)
        L_0x0559:
            r6 = 44
            goto L_0x012e
        L_0x055d:
            r9 = 14
            if (r0 != r9) goto L_0x0577
            r0 = 2131627464(0x7f0e0dc8, float:1.8882193E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627465(0x7f0e0dc9, float:1.8882195E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558554(0x7f0d009a, float:1.8742427E38)
            goto L_0x0559
        L_0x0577:
            r0 = 7
            if (r2 != r0) goto L_0x05a2
            r0 = 2131624329(0x7f0e0189, float:1.8875835E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05b2
            r2 = 2131624330(0x7f0e018a, float:1.8875837E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x059a:
            r14 = r2
        L_0x059b:
            r11 = r19
            r15 = 2131558420(0x7f0d0014, float:1.8742155E38)
            goto L_0x0801
        L_0x05a2:
            r0 = 20
            if (r2 == r0) goto L_0x0619
            r0 = 21
            if (r2 != r0) goto L_0x05ac
            goto L_0x0619
        L_0x05ac:
            r0 = 19
            if (r2 != r0) goto L_0x05b4
            java.lang.CharSequence r0 = r1.infoText
        L_0x05b2:
            r14 = r5
            goto L_0x059b
        L_0x05b4:
            r0 = 78
            if (r2 == r0) goto L_0x05eb
            r0 = 79
            if (r2 != r0) goto L_0x05bd
            goto L_0x05eb
        L_0x05bd:
            r0 = 3
            if (r2 != r0) goto L_0x05ca
            r0 = 2131624863(0x7f0e039f, float:1.8876918E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x05d3
        L_0x05ca:
            r0 = 2131624917(0x7f0e03d5, float:1.8877027E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x05d3:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05b2
            r2 = 2131624864(0x7f0e03a0, float:1.887692E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x059a
        L_0x05eb:
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = 78
            if (r2 != r3) goto L_0x05fd
            java.lang.String r2 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0603
        L_0x05fd:
            java.lang.String r2 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0603:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x0611
            r2 = 2131558455(0x7f0d0037, float:1.8742226E38)
            r15 = 2131558455(0x7f0d0037, float:1.8742226E38)
            goto L_0x02d2
        L_0x0611:
            r2 = 2131558460(0x7f0d003c, float:1.8742236E38)
            r15 = 2131558460(0x7f0d003c, float:1.8742236E38)
            goto L_0x02d2
        L_0x0619:
            r0 = r4
            org.telegram.messenger.MessagesController$DialogFilter r0 = (org.telegram.messenger.MessagesController.DialogFilter) r0
            r13 = 0
            int r4 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r4 == 0) goto L_0x06d1
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r11)
            if (r3 == 0) goto L_0x063c
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r11)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            long r11 = r3.user_id
        L_0x063c:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r11)
            if (r3 == 0) goto L_0x068c
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            r4 = 20
            if (r2 != r4) goto L_0x0670
            r4 = 2131625683(0x7f0e06d3, float:1.887858E38)
            r9 = 2
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r11[r10] = r3
            java.lang.String r0 = r0.name
            r11[r7] = r0
            java.lang.String r0 = "FilterUserAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r11)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x071a
        L_0x0670:
            r9 = 2
            r4 = 2131625684(0x7f0e06d4, float:1.8878583E38)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r11[r10] = r3
            java.lang.String r0 = r0.name
            r11[r7] = r0
            java.lang.String r0 = "FilterUserRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r11)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x071a
        L_0x068c:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r11 = -r11
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            r4 = 20
            if (r2 != r4) goto L_0x06b8
            r4 = 2131625622(0x7f0e0696, float:1.8878457E38)
            r9 = 2
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r3 = r3.title
            r11[r10] = r3
            java.lang.String r0 = r0.name
            r11[r7] = r0
            java.lang.String r0 = "FilterChatAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r11)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x071a
        L_0x06b8:
            r9 = 2
            r4 = 2131625623(0x7f0e0697, float:1.887846E38)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r3 = r3.title
            r11[r10] = r3
            java.lang.String r0 = r0.name
            r11[r7] = r0
            java.lang.String r0 = "FilterChatRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r11)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x071a
        L_0x06d1:
            r4 = 20
            if (r2 != r4) goto L_0x06f8
            r4 = 2131625626(0x7f0e069a, float:1.8878465E38)
            r9 = 2
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r9 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3)
            r11[r10] = r3
            java.lang.String r0 = r0.name
            r11[r7] = r0
            java.lang.String r0 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r11)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x071a
        L_0x06f8:
            r4 = 2131625627(0x7f0e069b, float:1.8878467E38)
            r9 = 2
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r9 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3)
            r11[r10] = r3
            java.lang.String r0 = r0.name
            r11[r7] = r0
            java.lang.String r0 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r11)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x071a:
            r3 = 20
            if (r2 != r3) goto L_0x0726
            r2 = 2131558442(0x7f0d002a, float:1.87422E38)
            r15 = 2131558442(0x7f0d002a, float:1.87422E38)
            goto L_0x02d2
        L_0x0726:
            r2 = 2131558443(0x7f0d002b, float:1.8742202E38)
            r15 = 2131558443(0x7f0d002b, float:1.8742202E38)
            goto L_0x02d2
        L_0x072e:
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r3 = 9
            if (r2 != r3) goto L_0x074b
            r2 = 2131625362(0x7f0e0592, float:1.887793E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r0
            java.lang.String r0 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0760
        L_0x074b:
            r2 = 2131625363(0x7f0e0593, float:1.8877932E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r0
            java.lang.String r0 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0760:
            r14 = r5
            r11 = r19
            r15 = 2131558425(0x7f0d0019, float:1.8742165E38)
            goto L_0x0801
        L_0x0768:
            r2 = 40
            if (r0 != r2) goto L_0x0772
            r0 = 2131628568(0x7f0e1218, float:1.8884432E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            goto L_0x0777
        L_0x0772:
            r0 = 2131628658(0x7f0e1272, float:1.8884615E38)
            java.lang.String r2 = "VoipGroupVideoRecordSaved"
        L_0x0777:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r15 = 2131558546(0x7f0d0092, float:1.874241E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            android.widget.TextView r2 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r3 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r3.<init>()
            r2.setMovementMethod(r3)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            java.lang.String r3 = "**"
            int r3 = r0.indexOf(r3)
            java.lang.String r4 = "**"
            int r0 = r0.lastIndexOf(r4)
            if (r3 < 0) goto L_0x07da
            if (r0 < 0) goto L_0x07da
            if (r3 == r0) goto L_0x07da
            int r4 = r0 + 2
            r11 = r19
            r2.replace(r0, r4, r11)
            int r4 = r3 + 2
            r2.replace(r3, r4, r11)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x07d5 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07d5 }
            r12.<init>()     // Catch:{ Exception -> 0x07d5 }
            java.lang.String r13 = "tg://openmessage?user_id="
            r12.append(r13)     // Catch:{ Exception -> 0x07d5 }
            int r13 = r1.currentAccount     // Catch:{ Exception -> 0x07d5 }
            org.telegram.messenger.UserConfig r13 = org.telegram.messenger.UserConfig.getInstance(r13)     // Catch:{ Exception -> 0x07d5 }
            long r13 = r13.getClientUserId()     // Catch:{ Exception -> 0x07d5 }
            r12.append(r13)     // Catch:{ Exception -> 0x07d5 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x07d5 }
            r4.<init>(r12)     // Catch:{ Exception -> 0x07d5 }
            r12 = 2
            int r0 = r0 - r12
            r2.setSpan(r4, r3, r0, r9)     // Catch:{ Exception -> 0x07d5 }
            goto L_0x07dc
        L_0x07d5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07dc
        L_0x07da:
            r11 = r19
        L_0x07dc:
            r0 = r2
            r14 = r5
            goto L_0x0801
        L_0x07df:
            r11 = r19
            r2 = 39
            if (r0 != r2) goto L_0x07eb
            r0 = 2131628569(0x7f0e1219, float:1.8884434E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            goto L_0x07f0
        L_0x07eb:
            r0 = 2131628659(0x7f0e1273, float:1.8884617E38)
            java.lang.String r2 = "VoipGroupVideoRecordStarted"
        L_0x07f0:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558547(0x7f0d0093, float:1.8742413E38)
            r1.timeLeft = r14
            r14 = r5
            r15 = 2131558547(0x7f0d0093, float:1.8742413E38)
        L_0x0801:
            android.widget.TextView r2 = r1.infoTextView
            r2.setText(r0)
            if (r15 == 0) goto L_0x082e
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r15, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r0.setPlayInDirectionOfCustomEndFrame(r10)
            int r2 = r0.getFramesCount()
            r0.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0835
        L_0x082e:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0835:
            if (r14 == 0) goto L_0x0876
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r8.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r14)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x089f
        L_0x0876:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r8.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x089f:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            r5 = r11
            goto L_0x0f9d
        L_0x08a7:
            r5 = r19
            int r14 = r1.currentAction
            r15 = 45
            if (r14 == r15) goto L_0x0fa2
            r15 = 46
            if (r14 == r15) goto L_0x0fa2
            r15 = 47
            if (r14 == r15) goto L_0x0fa2
            r15 = 51
            if (r14 == r15) goto L_0x0fa2
            r15 = 50
            if (r14 == r15) goto L_0x0fa2
            r15 = 52
            if (r14 == r15) goto L_0x0fa2
            r15 = 53
            if (r14 == r15) goto L_0x0fa2
            r15 = 54
            if (r14 == r15) goto L_0x0fa2
            r15 = 55
            if (r14 == r15) goto L_0x0fa2
            r15 = 56
            if (r14 == r15) goto L_0x0fa2
            r15 = 57
            if (r14 == r15) goto L_0x0fa2
            r15 = 58
            if (r14 == r15) goto L_0x0fa2
            r15 = 59
            if (r14 == r15) goto L_0x0fa2
            r15 = 60
            if (r14 == r15) goto L_0x0fa2
            r15 = 71
            if (r14 == r15) goto L_0x0fa2
            r15 = 70
            if (r14 == r15) goto L_0x0fa2
            r15 = 75
            if (r14 == r15) goto L_0x0fa2
            r15 = 76
            if (r14 == r15) goto L_0x0fa2
            r15 = 41
            if (r14 == r15) goto L_0x0fa2
            r15 = 78
            if (r14 == r15) goto L_0x0fa2
            r15 = 79
            if (r14 == r15) goto L_0x0fa2
            r15 = 61
            if (r14 == r15) goto L_0x0fa2
            r15 = 80
            if (r14 != r15) goto L_0x0909
            goto L_0x0fa2
        L_0x0909:
            r15 = 24
            if (r14 == r15) goto L_0x0e3a
            r15 = 25
            if (r14 != r15) goto L_0x0913
            goto L_0x0e3a
        L_0x0913:
            r4 = 11
            if (r14 != r4) goto L_0x0983
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624452(0x7f0e0204, float:1.8876084E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r2.setAnimation(r3, r6, r6)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
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
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0f9d
        L_0x0983:
            r4 = 15
            if (r14 != r4) goto L_0x0a52
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626786(0x7f0e0b22, float:1.8880818E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625619(0x7f0e0693, float:1.8878451E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558436(0x7f0d0024, float:1.8742188E38)
            r0.setAnimation(r2, r6, r6)
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
            r8.leftMargin = r2
            r8.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625618(0x7f0e0692, float:1.887845E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0a1f
            if (r0 < 0) goto L_0x0a1f
            if (r4 == r0) goto L_0x0a1f
            int r3 = r0 + 1
            r2.replace(r0, r3, r5)
            int r3 = r4 + 1
            r2.replace(r4, r3, r5)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r3.<init>(r6)
            int r0 = r0 - r7
            r2.setSpan(r3, r4, r0, r9)
        L_0x0a1f:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r10)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 2
            r0.setMaxLines(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r10)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0f9d
        L_0x0a52:
            r4 = 16
            if (r14 == r4) goto L_0x0cc5
            r4 = 17
            if (r14 != r4) goto L_0x0a5c
            goto L_0x0cc5
        L_0x0a5c:
            r4 = 18
            if (r14 != r4) goto L_0x0ae6
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
            r2.setTextSize(r7, r3)
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
            r8.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r8.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.bottomMargin = r0
            r0 = -1
            r8.height = r0
            r0 = 51
            r13.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r13.bottomMargin = r0
            r13.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r0.setAnimation(r2, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            android.widget.TextView r0 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r2.<init>()
            r0.setMovementMethod(r2)
            goto L_0x0f9d
        L_0x0ae6:
            r3 = 12
            if (r14 != r3) goto L_0x0b7f
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625029(0x7f0e0445, float:1.8877254E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166152(0x7var_c8, float:1.7946541E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131625030(0x7f0e0446, float:1.8877256E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0b57
            if (r0 < 0) goto L_0x0b57
            if (r4 == r0) goto L_0x0b57
            int r3 = r0 + 1
            r2.replace(r0, r3, r5)
            int r3 = r4 + 1
            r2.replace(r4, r3, r5)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r3.<init>(r6)
            int r0 = r0 - r7
            r2.setSpan(r3, r4, r0, r9)
        L_0x0b57:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r10)
            android.widget.TextView r0 = r1.subinfoTextView
            r3 = 2
            r0.setMaxLines(r3)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            goto L_0x0f9d
        L_0x0b7f:
            r3 = 2
            if (r14 == r3) goto L_0x0CLASSNAME
            r3 = 4
            if (r14 != r3) goto L_0x0b88
            r3 = 2
            goto L_0x0CLASSNAME
        L_0x0b88:
            r2 = 1110704128(0x42340000, float:45.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r8.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r8.topMargin = r2
            r8.rightMargin = r10
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r7, r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r10)
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
            if (r2 == r3) goto L_0x0CLASSNAME
            if (r2 == 0) goto L_0x0CLASSNAME
            r3 = 26
            if (r2 != r3) goto L_0x0bc6
            goto L_0x0CLASSNAME
        L_0x0bc6:
            r3 = 27
            if (r2 != r3) goto L_0x0bd9
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624918(0x7f0e03d6, float:1.887703E38)
            java.lang.String r4 = "ChatsDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0CLASSNAME
        L_0x0bd9:
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r11)
            if (r2 == 0) goto L_0x0CLASSNAME
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = -r11
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x0CLASSNAME
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0CLASSNAME
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624770(0x7f0e0342, float:1.887673E38)
            java.lang.String r4 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625860(0x7f0e0784, float:1.887894E38)
            java.lang.String r4 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624867(0x7f0e03a3, float:1.8876926E38)
            java.lang.String r4 = "ChatDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625913(0x7f0e07b9, float:1.8879047E38)
            java.lang.String r4 = "HistoryClearedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x0CLASSNAME:
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x0f9d
            r2 = 0
        L_0x0c3a:
            int r3 = r21.size()
            if (r2 >= r3) goto L_0x0f9d
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Object r4 = r0.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            long r8 = r4.longValue()
            int r4 = r1.currentAction
            if (r4 == 0) goto L_0x0c5b
            r6 = 26
            if (r4 != r6) goto L_0x0CLASSNAME
            goto L_0x0c5b
        L_0x0CLASSNAME:
            r4 = 0
            goto L_0x0c5c
        L_0x0c5b:
            r4 = 1
        L_0x0c5c:
            r3.addDialogAction(r8, r4)
            int r2 = r2 + 1
            goto L_0x0c3a
        L_0x0CLASSNAME:
            if (r2 != r3) goto L_0x0CLASSNAME
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624863(0x7f0e039f, float:1.8876918E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624917(0x7f0e03d5, float:1.8877027E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0CLASSNAME:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.topMargin = r0
            r8.rightMargin = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0f9d
        L_0x0cc5:
            r11 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r11
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
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
            if (r2 == 0) goto L_0x0d09
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625279(0x7f0e053f, float:1.8877761E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165406(0x7var_de, float:1.7945028E38)
            r0.setImageResource(r2)
            goto L_0x0db1
        L_0x0d09:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0d26
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625139(0x7f0e04b3, float:1.8877478E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0d23:
            r6 = 1096810496(0x41600000, float:14.0)
            goto L_0x0d7f
        L_0x0d26:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0d59
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r6 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r9, r10)
            r3.setText(r2)
            goto L_0x0d23
        L_0x0d59:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625278(0x7f0e053e, float:1.887776E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r10] = r0
            java.lang.String r6 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r6 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r9, r10)
            r2.setText(r3)
        L_0x0d7f:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r8.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r8.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r13.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.height = r0
        L_0x0db1:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627775(0x7f0e0eff, float:1.8882824E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0e03
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
            r2.setVisibility(r10)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = r1.getThemedColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r10)
            goto L_0x0e13
        L_0x0e03:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0e13:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r2
            r8.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.bottomMargin = r0
            r0 = -1
            r8.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            goto L_0x0f9d
        L_0x0e3a:
            r2 = 8
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r4
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r10)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0var_
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r6 = "fonts/rmedium.ttf"
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r4.setTypeface(r6)
            android.widget.TextView r4 = r1.infoTextView
            r6 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r7, r6)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r6 = r1.getThemedColor(r2)
            java.lang.String r9 = "BODY.**"
            r4.setLayerColor(r9, r6)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r6 = r1.getThemedColor(r2)
            java.lang.String r9 = "Wibe Big.**"
            r4.setLayerColor(r9, r6)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r6 = r1.getThemedColor(r2)
            java.lang.String r9 = "Wibe Big 3.**"
            r4.setLayerColor(r9, r6)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = r1.getThemedColor(r2)
            java.lang.String r6 = "Wibe Small.**"
            r4.setLayerColor(r6, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627385(0x7f0e0d79, float:1.8882033E38)
            java.lang.String r6 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r6 = 28
            r9 = 28
            r2.setAnimation(r4, r6, r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0ee2
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627387(0x7f0e0d7b, float:1.8882037E38)
            r6 = 2
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r9[r10] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r6)
            r9[r7] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            r2.setText(r0)
            goto L_0x0efa
        L_0x0ee2:
            r6 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627386(0x7f0e0d7a, float:1.8882035E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r6)
            r4[r10] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0efa:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r8.topMargin = r0
            goto L_0x0f8c
        L_0x0var_:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
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
            r8.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627384(0x7f0e0d78, float:1.888203E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
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
            r0.setVisibility(r10)
        L_0x0f8c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0f9d:
            r0 = 0
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x15a0
        L_0x0fa2:
            android.widget.ImageView r0 = r1.undoImageView
            r9 = 8
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r9)
            r13 = -1
            int r0 = r1.currentAction
            r9 = 76
            r15 = 1091567616(0x41100000, float:9.0)
            if (r0 != r9) goto L_0x0fe5
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624635(0x7f0e02bb, float:1.8876455E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
        L_0x0fe2:
            r0 = 1
            goto L_0x1562
        L_0x0fe5:
            r9 = 75
            if (r0 != r9) goto L_0x100d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625839(0x7f0e076f, float:1.8878897E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0fe2
        L_0x100d:
            r9 = 70
            if (r2 != r9) goto L_0x1093
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r10)
            r2 = 2592000(0x278d00, float:3.632166E-39)
            if (r0 < r2) goto L_0x1030
            r2 = 2592000(0x278d00, float:3.632166E-39)
            int r0 = r0 / r2
            java.lang.String r2 = "Months"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x105f
        L_0x1030:
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x1040
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x105f
        L_0x1040:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x104d
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x105f
        L_0x104d:
            r2 = 60
            if (r0 < r2) goto L_0x1059
            int r0 = r0 / r2
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x105f
        L_0x1059:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x105f:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624467(0x7f0e0213, float:1.8876115E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r10] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            r0 = 1
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1562
        L_0x1093:
            r2 = 71
            if (r0 != r2) goto L_0x10c7
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624466(0x7f0e0212, float:1.8876113E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558439(0x7f0d0027, float:1.8742194E38)
            r0.setAnimation(r2, r6, r6)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1561
        L_0x10c7:
            r2 = 45
            if (r0 != r2) goto L_0x10f0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625963(0x7f0e07eb, float:1.8879149E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0fe2
        L_0x10f0:
            r2 = 46
            if (r0 != r2) goto L_0x1119
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625964(0x7f0e07ec, float:1.887915E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0fe2
        L_0x1119:
            r2 = 47
            if (r0 != r2) goto L_0x114d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625986(0x7f0e0802, float:1.8879195E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558465(0x7f0d0041, float:1.8742247E38)
            r0.setAnimation(r2, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0fe2
        L_0x114d:
            r2 = 1096810496(0x41600000, float:14.0)
            r9 = 51
            if (r0 != r9) goto L_0x1176
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r4 = "AudioSpeedNormal"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558408(0x7f0d0008, float:1.874213E38)
            r0.setAnimation(r3, r6, r6)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1561
        L_0x1176:
            r9 = 50
            if (r0 != r9) goto L_0x119d
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r3, r6, r6)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1561
        L_0x119d:
            r9 = 52
            if (r0 == r9) goto L_0x14c8
            r9 = 56
            if (r0 == r9) goto L_0x14c8
            r9 = 57
            if (r0 == r9) goto L_0x14c8
            r9 = 58
            if (r0 == r9) goto L_0x14c8
            r9 = 59
            if (r0 == r9) goto L_0x14c8
            r9 = 60
            if (r0 == r9) goto L_0x14c8
            r9 = 80
            if (r0 != r9) goto L_0x11bb
            goto L_0x14c8
        L_0x11bb:
            r9 = 54
            if (r0 != r9) goto L_0x11e4
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624806(0x7f0e0366, float:1.8876802E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558494(0x7f0d005e, float:1.8742305E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1561
        L_0x11e4:
            r9 = 55
            if (r0 != r9) goto L_0x120d
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624805(0x7f0e0365, float:1.88768E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558493(0x7f0d005d, float:1.8742303E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1561
        L_0x120d:
            r9 = 41
            if (r0 != r9) goto L_0x12bb
            if (r4 != 0) goto L_0x128a
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1233
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131626009(0x7f0e0819, float:1.8879242E38)
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x12ad
        L_0x1233:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r11)
            if (r0 == 0) goto L_0x1261
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r11
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626008(0x7f0e0818, float:1.887924E38)
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r9[r10] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x12ad
        L_0x1261:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626010(0x7f0e081a, float:1.8879244E38)
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r9[r10] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x12ad
        L_0x128a:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626007(0x7f0e0817, float:1.8879238E38)
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.lang.String r11 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r11, r0)
            r9[r10] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x12ad:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r0.setAnimation(r3, r6, r6)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1561
        L_0x12bb:
            r6 = 53
            if (r0 != r6) goto L_0x1401
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x13a8
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.clientUserId
            int r6 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r6 != 0) goto L_0x1307
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x12e9
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625810(0x7f0e0752, float:1.8878838E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x12fb
        L_0x12e9:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x12fb:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558491(0x7f0d005b, float:1.87423E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x13fb
        L_0x1307:
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r11)
            if (r3 == 0) goto L_0x1354
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r11 = -r11
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x133b
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625809(0x7f0e0751, float:1.8878836E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r3 = r3.title
            r6[r10] = r3
            java.lang.String r3 = "FwdMessageToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x139d
        L_0x133b:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625813(0x7f0e0755, float:1.8878845E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r3 = r3.title
            r6[r10] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x139d
        L_0x1354:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x1383
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r10] = r3
            java.lang.String r3 = "FwdMessageToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x139d
        L_0x1383:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625815(0x7f0e0757, float:1.8878849E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r10] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x139d:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x13f8
        L_0x13a8:
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x13d2
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625808(0x7f0e0750, float:1.8878834E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r9 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3)
            r6[r10] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13ee
        L_0x13d2:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625812(0x7f0e0754, float:1.8878843E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r9 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3)
            r6[r10] = r3
            java.lang.String r3 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x13ee:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x13f8:
            r3 = 300(0x12c, double:1.48E-321)
            r13 = r3
        L_0x13fb:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1561
        L_0x1401:
            r6 = 61
            if (r0 != r6) goto L_0x1561
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1495
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1434
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624570(0x7f0e027a, float:1.8876323E38)
            java.lang.String r4 = "BackgroundToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558491(0x7f0d005b, float:1.87423E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x14c2
        L_0x1434:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r11)
            if (r0 == 0) goto L_0x1462
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r11
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624569(0x7f0e0279, float:1.8876321E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r6[r10] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x148a
        L_0x1462:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624571(0x7f0e027b, float:1.8876325E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r6[r10] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x148a:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x14c2
        L_0x1495:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624568(0x7f0e0278, float:1.887632E38)
            java.lang.Object[] r6 = new java.lang.Object[r7]
            java.lang.String r9 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r9, r0)
            r6[r10] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x14c2:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1561
        L_0x14c8:
            r3 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r4 = 80
            if (r0 != r4) goto L_0x14df
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625392(0x7f0e05b0, float:1.887799E38)
            java.lang.String r6 = "EmailCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x154f
        L_0x14df:
            r4 = 60
            if (r0 != r4) goto L_0x14f2
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131627162(0x7f0e0c9a, float:1.888158E38)
            java.lang.String r6 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x154f
        L_0x14f2:
            r4 = 56
            if (r0 != r4) goto L_0x1505
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131628390(0x7f0e1166, float:1.8884071E38)
            java.lang.String r6 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x154f
        L_0x1505:
            r4 = 57
            if (r0 != r4) goto L_0x1518
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625896(0x7f0e07a8, float:1.8879013E38)
            java.lang.String r6 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x154f
        L_0x1518:
            r4 = 52
            if (r0 != r4) goto L_0x152b
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626339(0x7f0e0963, float:1.8879911E38)
            java.lang.String r6 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x154f
        L_0x152b:
            r4 = 59
            if (r0 != r4) goto L_0x1541
            r3 = 2131558543(0x7f0d008f, float:1.8742405E38)
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            java.lang.String r6 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x154f
        L_0x1541:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131628155(0x7f0e107b, float:1.8883595E38)
            java.lang.String r6 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
        L_0x154f:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
        L_0x1561:
            r0 = 0
        L_0x1562:
            android.widget.TextView r3 = r1.subinfoTextView
            r4 = 8
            r3.setVisibility(r4)
            android.widget.TextView r3 = r1.undoTextView
            java.lang.String r6 = "undo_cancelColor"
            int r6 = r1.getThemedColor(r6)
            r3.setTextColor(r6)
            android.widget.LinearLayout r3 = r1.undoButton
            r3.setVisibility(r4)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r8.rightMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 0
            r3.setProgress(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r3.playAnimation()
            r3 = 0
            int r6 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x15a0
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r4.<init>(r1)
            r3.postDelayed(r4, r13)
        L_0x15a0:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.widget.TextView r4 = r1.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x15ce
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ". "
            r4.append(r5)
            android.widget.TextView r5 = r1.subinfoTextView
            java.lang.CharSequence r5 = r5.getText()
            r4.append(r5)
            java.lang.String r15 = r4.toString()
            goto L_0x15cf
        L_0x15ce:
            r15 = r5
        L_0x15cf:
            r3.append(r15)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r3)
            boolean r3 = r20.isMultilineSubInfo()
            if (r3 == 0) goto L_0x1624
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x15ef
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x15ef:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r10)
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
            goto L_0x16c8
        L_0x1624:
            boolean r3 = r20.hasSubInfo()
            if (r3 == 0) goto L_0x1634
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x16c8
        L_0x1634:
            android.view.ViewParent r3 = r20.getParent()
            boolean r3 = r3 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x16c8
            android.view.ViewParent r3 = r20.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = r3.getMeasuredWidth()
            int r5 = r3.getPaddingLeft()
            int r4 = r4 - r5
            int r3 = r3.getPaddingRight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x1656
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
        L_0x1656:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r3
            android.widget.TextView r3 = r1.infoTextView
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            r5 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r10)
            r8 = 0
            r21 = r20
            r22 = r3
            r23 = r4
            r24 = r5
            r25 = r6
            r26 = r8
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r3 = r1.infoTextView
            int r3 = r3.getMeasuredHeight()
            int r4 = r1.currentAction
            r5 = 16
            if (r4 == r5) goto L_0x1692
            r5 = 17
            if (r4 == r5) goto L_0x1692
            r5 = 18
            if (r4 != r5) goto L_0x168f
            goto L_0x1692
        L_0x168f:
            r6 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1694
        L_0x1692:
            r6 = 1096810496(0x41600000, float:14.0)
        L_0x1694:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 + r2
            r1.undoViewHeight = r3
            int r2 = r1.currentAction
            r4 = 18
            if (r2 != r4) goto L_0x16ae
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x16c8
        L_0x16ae:
            r4 = 25
            if (r2 != r4) goto L_0x16bf
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x16c8
        L_0x16bf:
            if (r0 == 0) goto L_0x16c8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r0
            r1.undoViewHeight = r3
        L_0x16c8:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x172c
            r1.setVisibility(r10)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x16d8
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x16da
        L_0x16d8:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x16da:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setEnterOffset(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r7]
            r3 = 2
            float[] r3 = new float[r3]
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x16f8
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x16fa
        L_0x16f8:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x16fa:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r1.undoViewHeight
            int r5 = r5 + r6
            float r5 = (float) r5
            float r4 = r4 * r5
            r3[r10] = r4
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x170d
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x170f
        L_0x170d:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x170f:
            r3[r7] = r4
            java.lang.String r4 = "enterOffset"
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r1, r4, r3)
            r2[r10] = r3
            r0.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x172c:
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
            float measuredHeight = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) AndroidUtilities.dp(9.0f));
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
        invalidate();
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
        setTranslationY(this.enterOffset - this.additionalTranslationY);
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
