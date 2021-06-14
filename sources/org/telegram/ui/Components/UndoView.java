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
import android.os.SystemClock;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.RequestDelegate;
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
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private Object currentInfoObject;
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
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    int textWidthOut;
    StaticLayout timeLayout;
    StaticLayout timeLayoutOut;
    private long timeLeft;
    private String timeLeftString;
    float timeReplaceProgress;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$showWithAction$2(View view, MotionEvent motionEvent) {
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
            try {
                if (motionEvent.getAction() != 1) {
                    return super.onTouchEvent(textView, spannable, motionEvent);
                }
                CharacterStyle[] characterStyleArr = (CharacterStyle[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), CharacterStyle.class);
                if (characterStyleArr != null && characterStyleArr.length > 0) {
                    UndoView.this.didPressUrl(characterStyleArr[0]);
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
        this(context, (BaseFragment) null, false);
    }

    public UndoView(Context context, BaseFragment baseFragment) {
        this(context, baseFragment, false);
    }

    public UndoView(Context context, BaseFragment baseFragment, boolean z) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.hideAnimationType = 1;
        this.timeReplaceProgress = 1.0f;
        this.parentFragment = baseFragment;
        this.fromTop = z;
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        this.infoTextView.setLinkTextColor(Theme.getColor("undo_cancelColor"));
        this.infoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        this.subinfoTextView.setLinkTextColor(Theme.getColor("undo_cancelColor"));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.leftImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.leftImageView.setLayerColor("info1.**", Theme.getColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("info2.**", Theme.getColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc9.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc8.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc7.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc6.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc5.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc4.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc3.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc2.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc1.**", Theme.getColor("undo_infoColor"));
        this.leftImageView.setLayerColor("Oval.**", Theme.getColor("undo_infoColor"));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(30, 30.0f, 19, 15.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                UndoView.this.lambda$new$0$UndoView(view);
            }
        });
        ImageView imageView = new ImageView(context);
        this.undoImageView = imageView;
        imageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        TextView textView3 = new TextView(context);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(Theme.getColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor("undo_infoColor"));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setColor(Theme.getColor("undo_infoColor"));
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("undo_background")));
        setOnTouchListener($$Lambda$UndoView$5pDYUsngdsAjUAfTf6WlgMO5mBI.INSTANCE);
        setVisibility(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$UndoView(View view) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    public void setColors(int i, int i2) {
        Theme.setDrawableColor(getBackground(), i);
        this.infoTextView.setTextColor(i2);
        this.subinfoTextView.setTextColor(i2);
        int i3 = i | -16777216;
        this.leftImageView.setLayerColor("info1.**", i3);
        this.leftImageView.setLayerColor("info2.**", i3);
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77 || i == 44 || i == 78 || i == 79;
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
        this.additionalTranslationY = f;
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
            if (i2 == 0 || i2 == 1) {
                MessagesController.getInstance(this.currentAccount).removeDialogAction(this.currentDialogId, this.currentAction == 0, z);
                onRemoveDialogAction(this.currentDialogId, this.currentAction);
            }
            float f = -1.0f;
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    Animator[] animatorArr = new Animator[1];
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (!this.fromTop) {
                        f = 1.0f;
                    }
                    fArr[0] = f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
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
            setTranslationY(f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)));
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

    /* JADX WARNING: Removed duplicated region for block: B:463:0x13f0  */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x1417  */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x145c  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x1506  */
    /* JADX WARNING: Removed duplicated region for block: B:510:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r20, int r22, java.lang.Object r23, java.lang.Object r24, java.lang.Runnable r25, java.lang.Runnable r26) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            r0 = r22
            r4 = r23
            r5 = r24
            java.lang.Runnable r6 = r1.currentActionRunnable
            if (r6 == 0) goto L_0x0011
            r6.run()
        L_0x0011:
            r6 = 1
            r1.isShown = r6
            r7 = r25
            r1.currentActionRunnable = r7
            r7 = r26
            r1.currentCancelRunnable = r7
            r1.currentDialogId = r2
            r1.currentAction = r0
            r7 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r7
            r1.currentInfoObject = r4
            long r7 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r7
            android.widget.TextView r7 = r1.undoTextView
            r8 = 2131627836(0x7f0e0f3c, float:1.8882948E38)
            java.lang.String r9 = "Undo"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            java.lang.String r8 = r8.toUpperCase()
            r7.setText(r8)
            android.widget.ImageView r7 = r1.undoImageView
            r8 = 0
            r7.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r7 = r1.leftImageView
            r7.setPadding(r8, r8, r8, r8)
            android.widget.TextView r7 = r1.infoTextView
            r9 = 1097859072(0x41700000, float:15.0)
            r7.setTextSize(r6, r9)
            org.telegram.ui.Components.BackupImageView r7 = r1.avatarImageView
            r10 = 8
            r7.setVisibility(r10)
            android.widget.TextView r7 = r1.infoTextView
            r11 = 51
            r7.setGravity(r11)
            android.widget.TextView r7 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r7 = (android.widget.FrameLayout.LayoutParams) r7
            r11 = -2
            r7.height = r11
            r11 = 1095761920(0x41500000, float:13.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r7.topMargin = r11
            r7.bottomMargin = r8
            org.telegram.ui.Components.RLottieImageView r11 = r1.leftImageView
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r11.setScaleType(r12)
            org.telegram.ui.Components.RLottieImageView r11 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r11 = r11.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r11 = (android.widget.FrameLayout.LayoutParams) r11
            r12 = 19
            r11.gravity = r12
            r11.bottomMargin = r8
            r11.topMargin = r8
            r12 = 1077936128(0x40400000, float:3.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.leftMargin = r12
            r12 = 1113063424(0x42580000, float:54.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.width = r12
            r12 = -2
            r11.height = r12
            android.widget.TextView r12 = r1.infoTextView
            r12.setMinHeight(r8)
            r12 = 0
            r1.setOnClickListener(r12)
            org.telegram.ui.Components.-$$Lambda$UndoView$ZAs14JcizD3kU0BwPeZw5NJPYZU r13 = org.telegram.ui.Components.$$Lambda$UndoView$ZAs14JcizD3kU0BwPeZw5NJPYZU.INSTANCE
            r1.setOnTouchListener(r13)
            android.widget.TextView r13 = r1.infoTextView
            org.telegram.ui.Components.UndoView$LinkMovementMethodMy r14 = new org.telegram.ui.Components.UndoView$LinkMovementMethodMy
            r14.<init>()
            r13.setMovementMethod(r14)
            boolean r13 = r19.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r9 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r14 = 3000(0xbb8, double:1.482E-320)
            r10 = 36
            if (r13 == 0) goto L_0x0773
            r11 = 74
            if (r0 != r11) goto L_0x00ed
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            r0 = 2131627187(0x7f0e0cb3, float:1.8881631E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627196(0x7f0e0cbc, float:1.888165E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r15 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            goto L_0x06ce
        L_0x00ed:
            r11 = 34
            if (r0 != r11) goto L_0x012a
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628109(0x7f0e104d, float:1.8883501E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r4
            java.lang.String r4 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r0, r3)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r8)
            r1.timeLeft = r14
            r0 = r2
        L_0x0127:
            r15 = 0
            goto L_0x06ce
        L_0x012a:
            r11 = 44
            if (r0 != r11) goto L_0x0182
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x014b
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628062(0x7f0e101e, float:1.8883406E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r0
            java.lang.String r0 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0161
        L_0x014b:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628052(0x7f0e1014, float:1.8883386E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r0 = r0.title
            r3[r8] = r0
            java.lang.String r0 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0161:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r2.<init>()
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setTextSize(r3)
            r3 = r4
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r2.setInfo((org.telegram.tgnet.TLObject) r3)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r3, r2)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r8)
            r1.timeLeft = r14
            goto L_0x0127
        L_0x0182:
            r11 = 37
            if (r0 != r11) goto L_0x01d3
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01ac
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x01b9
        L_0x01ac:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x01b9:
            r2 = 2131628170(0x7f0e108a, float:1.8883625E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r8)
            r1.timeLeft = r14
            goto L_0x0127
        L_0x01d3:
            r11 = 33
            if (r0 != r11) goto L_0x01ea
            r0 = 2131628092(0x7f0e103c, float:1.8883467E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558525(0x7f0d007d, float:1.8742368E38)
            r1.timeLeft = r14
            r15 = 2131558525(0x7f0d007d, float:1.8742368E38)
            goto L_0x06ce
        L_0x01ea:
            r11 = 77
            if (r0 != r11) goto L_0x0215
            r0 = r4
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r15 = 2131558464(0x7f0d0040, float:1.8742245E38)
            r2 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r2
            org.telegram.ui.ActionBar.BaseFragment r2 = r1.parentFragment
            if (r2 == 0) goto L_0x06ce
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$Message
            if (r2 == 0) goto L_0x06ce
            r2 = r5
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            r1.setOnTouchListener(r12)
            android.widget.TextView r3 = r1.infoTextView
            r3.setMovementMethod(r12)
            org.telegram.ui.Components.-$$Lambda$UndoView$xeJ8z9FlextsmLZ8kkzmlexL7K4 r3 = new org.telegram.ui.Components.-$$Lambda$UndoView$xeJ8z9FlextsmLZ8kkzmlexL7K4
            r3.<init>(r2)
            r1.setOnClickListener(r3)
            goto L_0x06ce
        L_0x0215:
            r11 = 30
            if (r0 != r11) goto L_0x0245
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0225
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x022a
        L_0x0225:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x022a:
            r2 = 2131628168(0x7f0e1088, float:1.8883621E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r1.timeLeft = r14
        L_0x0240:
            r15 = 2131558526(0x7f0d007e, float:1.874237E38)
            goto L_0x06ce
        L_0x0245:
            r11 = 35
            if (r0 != r11) goto L_0x0277
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0255
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0260
        L_0x0255:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x025f
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0260
        L_0x025f:
            r0 = r9
        L_0x0260:
            r2 = 2131628169(0x7f0e1089, float:1.8883623E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r1.timeLeft = r14
            goto L_0x0240
        L_0x0277:
            r11 = 31
            if (r0 != r11) goto L_0x02a7
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0287
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x028c
        L_0x0287:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x028c:
            r2 = 2131628166(0x7f0e1086, float:1.8883617E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558532(0x7f0d0084, float:1.8742382E38)
            r1.timeLeft = r14
        L_0x02a2:
            r15 = 2131558532(0x7f0d0084, float:1.8742382E38)
            goto L_0x06ce
        L_0x02a7:
            r11 = 38
            if (r0 != r11) goto L_0x02dd
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x02c6
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628176(0x7f0e1090, float:1.8883637E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r0 = r0.title
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02d3
        L_0x02c6:
            r0 = 2131628175(0x7f0e108f, float:1.8883635E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02d3:
            r2 = 2131558518(0x7f0d0076, float:1.8742354E38)
            r1.timeLeft = r14
            r15 = 2131558518(0x7f0d0076, float:1.8742354E38)
            goto L_0x06ce
        L_0x02dd:
            r11 = 42
            if (r0 != r11) goto L_0x02f8
            r0 = 2131628149(0x7f0e1075, float:1.8883583E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r1.timeLeft = r14
            r15 = 2131558448(0x7f0d0030, float:1.8742212E38)
            goto L_0x06ce
        L_0x02f8:
            r11 = 43
            if (r0 != r11) goto L_0x0313
            r0 = 2131628150(0x7f0e1076, float:1.8883585E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r1.timeLeft = r14
            r15 = 2131558453(0x7f0d0035, float:1.8742222E38)
            goto L_0x06ce
        L_0x0313:
            int r11 = r1.currentAction
            r13 = 39
            if (r11 != r13) goto L_0x0330
            r0 = 2131628083(0x7f0e1033, float:1.8883449E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558529(0x7f0d0081, float:1.8742376E38)
            r1.timeLeft = r14
            r15 = 2131558529(0x7f0d0081, float:1.8742376E38)
            goto L_0x06ce
        L_0x0330:
            r13 = 40
            if (r11 != r13) goto L_0x039e
            r0 = 2131628082(0x7f0e1032, float:1.8883447E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r15 = 2131558528(0x7f0d0080, float:1.8742374E38)
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
            if (r3 < 0) goto L_0x039b
            if (r0 < 0) goto L_0x039b
            if (r3 == r0) goto L_0x039b
            int r4 = r0 + 2
            r2.replace(r0, r4, r9)
            int r4 = r3 + 2
            r2.replace(r3, r4, r9)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0397 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0397 }
            r5.<init>()     // Catch:{ Exception -> 0x0397 }
            java.lang.String r11 = "tg://openmessage?user_id="
            r5.append(r11)     // Catch:{ Exception -> 0x0397 }
            int r11 = r1.currentAccount     // Catch:{ Exception -> 0x0397 }
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)     // Catch:{ Exception -> 0x0397 }
            int r11 = r11.getClientUserId()     // Catch:{ Exception -> 0x0397 }
            r5.append(r11)     // Catch:{ Exception -> 0x0397 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0397 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0397 }
            r5 = 2
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)     // Catch:{ Exception -> 0x0397 }
            goto L_0x039b
        L_0x0397:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x039b:
            r0 = r2
            goto L_0x06ce
        L_0x039e:
            if (r0 != r10) goto L_0x03c9
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x03ac
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03b1
        L_0x03ac:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x03b1:
            r2 = 2131628167(0x7f0e1087, float:1.888362E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558532(0x7f0d0084, float:1.8742382E38)
            r1.timeLeft = r14
            goto L_0x02a2
        L_0x03c9:
            r13 = 32
            if (r0 != r13) goto L_0x03f9
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x03d9
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03de
        L_0x03d9:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x03de:
            r2 = 2131628140(0x7f0e106c, float:1.8883564E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558524(0x7f0d007c, float:1.8742366E38)
            r1.timeLeft = r14
            r15 = 2131558524(0x7f0d007c, float:1.8742366E38)
            goto L_0x06ce
        L_0x03f9:
            r13 = 9
            if (r0 == r13) goto L_0x0699
            r13 = 10
            if (r0 != r13) goto L_0x0403
            goto L_0x0699
        L_0x0403:
            r13 = 8
            if (r0 != r13) goto L_0x041d
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x06cb
        L_0x041d:
            r13 = 22
            if (r0 != r13) goto L_0x0489
            r13 = 0
            int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x043f
            if (r4 != 0) goto L_0x0434
            r0 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x0434:
            r0 = 2131626043(0x7f0e083b, float:1.8879311E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x043f:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r2 = -r2
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x0471
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0471
            if (r4 != 0) goto L_0x0466
            r0 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x0466:
            r0 = 2131626039(0x7f0e0837, float:1.8879303E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x0471:
            if (r4 != 0) goto L_0x047e
            r0 = 2131626040(0x7f0e0838, float:1.8879305E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x047e:
            r0 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x0489:
            r13 = 23
            if (r0 != r13) goto L_0x0498
            r0 = 2131624843(0x7f0e038b, float:1.8876877E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06cb
        L_0x0498:
            r13 = 6
            if (r0 != r13) goto L_0x04b4
            r0 = 2131624299(0x7f0e016b, float:1.8875774E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558419(0x7f0d0013, float:1.8742153E38)
            r10 = 48
            goto L_0x06ce
        L_0x04b4:
            r13 = 13
            if (r11 != r13) goto L_0x04d1
            r0 = 2131627115(0x7f0e0c6b, float:1.8881485E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627116(0x7f0e0c6c, float:1.8881487E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558534(0x7f0d0086, float:1.8742387E38)
        L_0x04cd:
            r10 = 44
            goto L_0x06ce
        L_0x04d1:
            r13 = 14
            if (r11 != r13) goto L_0x04eb
            r0 = 2131627117(0x7f0e0c6d, float:1.888149E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627118(0x7f0e0c6e, float:1.8881491E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558535(0x7f0d0087, float:1.8742389E38)
            goto L_0x04cd
        L_0x04eb:
            r11 = 7
            if (r0 != r11) goto L_0x0514
            r0 = 2131624307(0x7f0e0173, float:1.887579E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x050f
            r2 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x050e:
            r12 = r2
        L_0x050f:
            r15 = 2131558418(0x7f0d0012, float:1.8742151E38)
            goto L_0x06ce
        L_0x0514:
            r11 = 20
            if (r0 == r11) goto L_0x058a
            r11 = 21
            if (r0 != r11) goto L_0x051e
            goto L_0x058a
        L_0x051e:
            r2 = 19
            if (r0 != r2) goto L_0x0525
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x050f
        L_0x0525:
            r2 = 78
            if (r0 == r2) goto L_0x055c
            r2 = 79
            if (r0 != r2) goto L_0x052e
            goto L_0x055c
        L_0x052e:
            r2 = 3
            if (r0 != r2) goto L_0x053b
            r0 = 2131624813(0x7f0e036d, float:1.8876816E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0544
        L_0x053b:
            r0 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0544:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x050f
            r2 = 2131624814(0x7f0e036e, float:1.8876818E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x050e
        L_0x055c:
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r3 = 78
            if (r0 != r3) goto L_0x056e
            java.lang.String r0 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r2)
            goto L_0x0574
        L_0x056e:
            java.lang.String r0 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r2)
        L_0x0574:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x0582
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
            r15 = 2131558449(0x7f0d0031, float:1.8742214E38)
            goto L_0x06ce
        L_0x0582:
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r15 = 2131558454(0x7f0d0036, float:1.8742224E38)
            goto L_0x06ce
        L_0x058a:
            org.telegram.messenger.MessagesController$DialogFilter r5 = (org.telegram.messenger.MessagesController.DialogFilter) r5
            r13 = 0
            int r11 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x063a
            int r4 = (int) r2
            if (r4 != 0) goto L_0x05a9
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r11 = 32
            long r2 = r2 >> r11
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            int r4 = r2.user_id
        L_0x05a9:
            if (r4 <= 0) goto L_0x05f5
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            r3 = 20
            if (r0 != r3) goto L_0x05d9
            r3 = 2131625564(0x7f0e065c, float:1.887834E38)
            r4 = 2
            java.lang.Object[] r11 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r11[r8] = r2
            java.lang.String r2 = r5.name
            r11[r6] = r2
            java.lang.String r2 = "FilterUserAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0685
        L_0x05d9:
            r4 = 2
            r3 = 2131625565(0x7f0e065d, float:1.8878342E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r11[r8] = r2
            java.lang.String r2 = r5.name
            r11[r6] = r2
            java.lang.String r2 = "FilterUserRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0685
        L_0x05f5:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            r3 = 20
            if (r0 != r3) goto L_0x0621
            r3 = 2131625503(0x7f0e061f, float:1.8878216E38)
            r4 = 2
            java.lang.Object[] r11 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r11[r8] = r2
            java.lang.String r2 = r5.name
            r11[r6] = r2
            java.lang.String r2 = "FilterChatAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0685
        L_0x0621:
            r4 = 2
            r3 = 2131625504(0x7f0e0620, float:1.8878218E38)
            java.lang.Object[] r11 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r11[r8] = r2
            java.lang.String r2 = r5.name
            r11[r6] = r2
            java.lang.String r2 = "FilterChatRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r11)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0685
        L_0x063a:
            r2 = 20
            if (r0 != r2) goto L_0x0662
            r2 = 2131625507(0x7f0e0623, float:1.8878224E38)
            r3 = 2
            java.lang.Object[] r11 = new java.lang.Object[r3]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r11[r8] = r3
            java.lang.String r3 = r5.name
            r11[r6] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0685
        L_0x0662:
            r2 = 2131625508(0x7f0e0624, float:1.8878226E38)
            r3 = 2
            java.lang.Object[] r11 = new java.lang.Object[r3]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r11[r8] = r3
            java.lang.String r3 = r5.name
            r11[r6] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r11)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x0685:
            r3 = 20
            if (r0 != r3) goto L_0x0691
            r0 = 2131558436(0x7f0d0024, float:1.8742188E38)
            r15 = 2131558436(0x7f0d0024, float:1.8742188E38)
            goto L_0x039b
        L_0x0691:
            r0 = 2131558437(0x7f0d0025, float:1.874219E38)
            r15 = 2131558437(0x7f0d0025, float:1.874219E38)
            goto L_0x039b
        L_0x0699:
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r3 = 9
            if (r0 != r3) goto L_0x06b6
            r0 = 2131625258(0x7f0e052a, float:1.8877719E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r8] = r2
            java.lang.String r2 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x06cb
        L_0x06b6:
            r0 = 2131625259(0x7f0e052b, float:1.887772E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r8] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x06cb:
            r15 = 2131558422(0x7f0d0016, float:1.874216E38)
        L_0x06ce:
            android.widget.TextView r2 = r1.infoTextView
            r2.setText(r0)
            if (r15 == 0) goto L_0x06fb
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r15, r10, r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r0.setPlayInDirectionOfCustomEndFrame(r8)
            int r2 = r0.getFramesCount()
            r0.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0702
        L_0x06fb:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0702:
            if (r12 == 0) goto L_0x0743
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r12)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x076c
        L_0x0743:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x076c:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x0e21
        L_0x0773:
            int r12 = r1.currentAction
            r13 = 45
            r14 = 60
            if (r12 == r13) goto L_0x0e26
            r13 = 46
            if (r12 == r13) goto L_0x0e26
            r13 = 47
            if (r12 == r13) goto L_0x0e26
            r13 = 51
            if (r12 == r13) goto L_0x0e26
            r13 = 50
            if (r12 == r13) goto L_0x0e26
            r13 = 52
            if (r12 == r13) goto L_0x0e26
            r13 = 53
            if (r12 == r13) goto L_0x0e26
            r13 = 54
            if (r12 == r13) goto L_0x0e26
            r13 = 55
            if (r12 == r13) goto L_0x0e26
            r13 = 56
            if (r12 == r13) goto L_0x0e26
            r13 = 57
            if (r12 == r13) goto L_0x0e26
            r13 = 58
            if (r12 == r13) goto L_0x0e26
            r13 = 59
            if (r12 == r13) goto L_0x0e26
            if (r12 == r14) goto L_0x0e26
            r13 = 71
            if (r12 == r13) goto L_0x0e26
            r13 = 70
            if (r12 == r13) goto L_0x0e26
            r13 = 75
            if (r12 == r13) goto L_0x0e26
            r13 = 76
            if (r12 == r13) goto L_0x0e26
            r13 = 41
            if (r12 == r13) goto L_0x0e26
            r13 = 78
            if (r12 == r13) goto L_0x0e26
            r13 = 79
            if (r12 == r13) goto L_0x0e26
            r13 = 61
            if (r12 != r13) goto L_0x07cf
            goto L_0x0e26
        L_0x07cf:
            r13 = 24
            if (r12 == r13) goto L_0x0cbe
            r13 = 25
            if (r12 != r13) goto L_0x07d9
            goto L_0x0cbe
        L_0x07d9:
            r5 = 11
            if (r12 != r5) goto L_0x0849
            r0 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558422(0x7f0d0016, float:1.874216E38)
            r2.setAnimation(r3, r10, r10)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e21
        L_0x0849:
            r5 = 15
            if (r12 != r5) goto L_0x091a
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625500(0x7f0e061c, float:1.887821E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558431(0x7f0d001f, float:1.8742178E38)
            r0.setAnimation(r2, r10, r10)
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
            r7.leftMargin = r2
            r7.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625499(0x7f0e061b, float:1.8878208E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x08e7
            if (r0 < 0) goto L_0x08e7
            if (r4 == r0) goto L_0x08e7
            int r3 = r0 + 1
            r2.replace(r0, r3, r9)
            int r3 = r4 + 1
            r2.replace(r4, r3, r9)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r3.<init>(r5)
            int r0 = r0 - r6
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x08e7:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 2
            r0.setMaxLines(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e21
        L_0x091a:
            r5 = 16
            if (r12 == r5) goto L_0x0b49
            r5 = 17
            if (r12 != r5) goto L_0x0924
            goto L_0x0b49
        L_0x0924:
            r5 = 18
            if (r12 != r5) goto L_0x09a4
            r0 = r4
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
            r2.setTextSize(r6, r3)
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
            r7.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.bottomMargin = r0
            r0 = -1
            r7.height = r0
            r0 = 51
            r11.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.bottomMargin = r0
            r11.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r10, r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e21
        L_0x09a4:
            r4 = 12
            if (r12 != r4) goto L_0x0a3f
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624946(0x7f0e03f2, float:1.8877086E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166090(0x7var_a, float:1.7946416E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131624947(0x7f0e03f3, float:1.8877088E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0a17
            if (r0 < 0) goto L_0x0a17
            if (r4 == r0) goto L_0x0a17
            int r3 = r0 + 1
            r2.replace(r0, r3, r9)
            int r3 = r4 + 1
            r2.replace(r4, r3, r9)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r3.<init>(r5)
            int r0 = r0 - r6
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x0a17:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 2
            r0.setMaxLines(r4)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            goto L_0x0e21
        L_0x0a3f:
            r4 = 2
            if (r12 == r4) goto L_0x0ae5
            r4 = 4
            if (r12 != r4) goto L_0x0a47
            goto L_0x0ae5
        L_0x0a47:
            r0 = 1110704128(0x42340000, float:45.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.topMargin = r0
            r7.rightMargin = r8
            android.widget.TextView r0 = r1.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r4)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 8
            r0.setVisibility(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r4)
            int r0 = r1.currentAction
            if (r0 != 0) goto L_0x0a8b
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625768(0x7f0e0728, float:1.8878753E38)
            java.lang.String r5 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0ad3
        L_0x0a8b:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x0ac5
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r4.getChat(r0)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r4 == 0) goto L_0x0ab6
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0ab6
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624721(0x7f0e0311, float:1.887663E38)
            java.lang.String r5 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0ad3
        L_0x0ab6:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            java.lang.String r5 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0ad3
        L_0x0ac5:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624817(0x7f0e0371, float:1.8876824E38)
            java.lang.String r5 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
        L_0x0ad3:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r4 = r1.currentAction
            if (r4 != 0) goto L_0x0adf
            r4 = 1
            goto L_0x0ae0
        L_0x0adf:
            r4 = 0
        L_0x0ae0:
            r0.addDialogAction(r2, r4)
            goto L_0x0e21
        L_0x0ae5:
            r2 = 2
            if (r0 != r2) goto L_0x0af7
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624813(0x7f0e036d, float:1.8876816E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0b05
        L_0x0af7:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624851(0x7f0e0393, float:1.8876893E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0b05:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.topMargin = r0
            r7.rightMargin = r8
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558416(0x7f0d0010, float:1.8742147E38)
            r0.setAnimation(r2, r10, r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e21
        L_0x0b49:
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 16
            r0.setGravity(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1106247680(0x41var_, float:30.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setMinHeight(r2)
            r0 = r4
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "🎲"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0b8d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625185(0x7f0e04e1, float:1.887757E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165388(0x7var_cc, float:1.7944992E38)
            r0.setImageResource(r2)
            goto L_0x0CLASSNAME
        L_0x0b8d:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0baa
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625054(0x7f0e045e, float:1.8877305E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0ba7:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0CLASSNAME
        L_0x0baa:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0bdd
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r10, r8)
            r3.setText(r2)
            goto L_0x0ba7
        L_0x0bdd:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625184(0x7f0e04e0, float:1.8877569E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r8] = r0
            java.lang.String r5 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r10, r8)
            r2.setText(r3)
        L_0x0CLASSNAME:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r11.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.height = r0
        L_0x0CLASSNAME:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627377(0x7f0e0d71, float:1.8882017E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0CLASSNAME
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
            r2.setVisibility(r8)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r8)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0CLASSNAME:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r2
            r7.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.bottomMargin = r0
            r0 = -1
            r7.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            goto L_0x0e21
        L_0x0cbe:
            r2 = 8
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r5
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r8)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0d8d
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r1.infoTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "BODY.**"
            r4.setLayerColor(r10, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "Wibe Big.**"
            r4.setLayerColor(r10, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "Wibe Big 3.**"
            r4.setLayerColor(r10, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r5 = "Wibe Small.**"
            r4.setLayerColor(r5, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627071(0x7f0e0c3f, float:1.8881396E38)
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r5 = 28
            r10 = 28
            r2.setAnimation(r4, r5, r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0d66
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627073(0x7f0e0CLASSNAME, float:1.88814E38)
            r5 = 2
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r10[r8] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r10[r6] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            r2.setText(r0)
            goto L_0x0d7e
        L_0x0d66:
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627072(0x7f0e0CLASSNAME, float:1.8881398E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r4[r8] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0d7e:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            goto L_0x0e10
        L_0x0d8d:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r4 = "Body Main.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r4 = "Body Top.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r4 = "Line.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r4 = "Curve Big.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r3 = "Curve Small.**"
            r0.setLayerColor(r3, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627070(0x7f0e0c3e, float:1.8881394E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r3 = 28
            r4 = 28
            r0.setAnimation(r2, r3, r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
        L_0x0e10:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0e21:
            r0 = 0
            r12 = 1096810496(0x41600000, float:14.0)
            goto L_0x13da
        L_0x0e26:
            android.widget.ImageView r11 = r1.undoImageView
            r12 = 8
            r11.setVisibility(r12)
            org.telegram.ui.Components.RLottieImageView r11 = r1.leftImageView
            r11.setVisibility(r8)
            android.widget.TextView r11 = r1.infoTextView
            android.graphics.Typeface r12 = android.graphics.Typeface.DEFAULT
            r11.setTypeface(r12)
            int r11 = r1.currentAction
            r12 = 76
            r13 = 1091567616(0x41100000, float:9.0)
            if (r11 != r12) goto L_0x0e69
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624604(0x7f0e029c, float:1.8876392E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r0.setAnimation(r2, r10, r10)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
        L_0x0e64:
            r0 = 1
            r12 = 1096810496(0x41600000, float:14.0)
            goto L_0x13ac
        L_0x0e69:
            r12 = 75
            if (r11 != r12) goto L_0x0e91
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625700(0x7f0e06e4, float:1.8878615E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r10, r10)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            goto L_0x0e64
        L_0x0e91:
            r12 = 70
            if (r0 != r12) goto L_0x0var_
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r8)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x0eb4
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0ed1
        L_0x0eb4:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x0ec1
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0ed1
        L_0x0ec1:
            if (r0 < r14) goto L_0x0ecb
            int r0 = r0 / r14
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0ed1
        L_0x0ecb:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0ed1:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r8] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r0.setAnimation(r2, r10, r10)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            goto L_0x0e64
        L_0x0var_:
            r0 = 71
            if (r11 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624439(0x7f0e01f7, float:1.8876058E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r0.setAnimation(r2, r10, r10)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            r12 = 1096810496(0x41600000, float:14.0)
            goto L_0x13ab
        L_0x0var_:
            r0 = 45
            if (r11 != r0) goto L_0x0f5f
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625815(0x7f0e0757, float:1.8878849E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r10, r10)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            goto L_0x0e64
        L_0x0f5f:
            r0 = 46
            if (r11 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625816(0x7f0e0758, float:1.887885E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r10, r10)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            goto L_0x0e64
        L_0x0var_:
            r0 = 47
            if (r11 != r0) goto L_0x0fbd
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625822(0x7f0e075e, float:1.8878863E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r0.setAnimation(r2, r10, r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r12 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r12)
            r0 = 1
            goto L_0x13ac
        L_0x0fbd:
            r12 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r11 != r0) goto L_0x0fe6
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624410(0x7f0e01da, float:1.8875999E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r2, r10, r10)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x13ab
        L_0x0fe6:
            r0 = 50
            if (r11 != r0) goto L_0x100d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624409(0x7f0e01d9, float:1.8875997E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r0.setAnimation(r2, r10, r10)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x13ab
        L_0x100d:
            r0 = 52
            if (r11 == r0) goto L_0x1328
            r0 = 56
            if (r11 == r0) goto L_0x1328
            r0 = 57
            if (r11 == r0) goto L_0x1328
            r0 = 58
            if (r11 == r0) goto L_0x1328
            r0 = 59
            if (r11 == r0) goto L_0x1328
            if (r11 != r14) goto L_0x1025
            goto L_0x1328
        L_0x1025:
            r0 = 54
            if (r11 != r0) goto L_0x104e
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624757(0x7f0e0335, float:1.8876703E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558479(0x7f0d004f, float:1.8742275E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x13ab
        L_0x104e:
            r0 = 55
            if (r11 != r0) goto L_0x1077
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624756(0x7f0e0334, float:1.88767E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558478(0x7f0d004e, float:1.8742273E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x13ab
        L_0x1077:
            r0 = 41
            if (r11 != r0) goto L_0x1122
            if (r5 != 0) goto L_0x10f1
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r4 = (long) r0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x109d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625845(0x7f0e0775, float:1.887891E38)
            java.lang.String r3 = "InvLinkToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1114
        L_0x109d:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x10c8
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625844(0x7f0e0774, float:1.8878907E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r0 = r0.title
            r4[r8] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x1114
        L_0x10c8:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625846(0x7f0e0776, float:1.8878911E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r8] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x1114
        L_0x10f1:
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625843(0x7f0e0773, float:1.8878905E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r5, r0)
            r4[r8] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x1114:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558422(0x7f0d0016, float:1.874216E38)
            r0.setAnimation(r2, r10, r10)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x13ab
        L_0x1122:
            r0 = 53
            if (r11 != r0) goto L_0x1263
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r5 != 0) goto L_0x120d
            int r4 = r1.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r4 = (long) r4
            int r10 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r10 != 0) goto L_0x116f
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x1151
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625671(0x7f0e06c7, float:1.8878557E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1163
        L_0x1151:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625675(0x7f0e06cb, float:1.8878565E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x1163:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558476(0x7f0d004c, float:1.8742269E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x125d
        L_0x116f:
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x11b9
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x11a0
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625670(0x7f0e06c6, float:1.8878555E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = r2.title
            r4[r8] = r2
            java.lang.String r2 = "FwdMessageToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1202
        L_0x11a0:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625674(0x7f0e06ca, float:1.8878563E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = r2.title
            r4[r8] = r2
            java.lang.String r2 = "FwdMessagesToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1202
        L_0x11b9:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x11e8
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625672(0x7f0e06c8, float:1.8878559E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessageToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1202
        L_0x11e8:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625676(0x7f0e06cc, float:1.8878567E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessagesToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x1202:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x125d
        L_0x120d:
            r2 = r5
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x1237
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625669(0x7f0e06c5, float:1.8878552E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessageToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1253
        L_0x1237:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625673(0x7f0e06c9, float:1.887856E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessagesToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x1253:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x125d:
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x13ab
        L_0x1263:
            r0 = 61
            if (r11 != r0) goto L_0x13ab
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r5 != 0) goto L_0x12f5
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r4 = (long) r0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1297
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624540(0x7f0e025c, float:1.8876263E38)
            java.lang.String r3 = "BackgroundToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558476(0x7f0d004c, float:1.8742269E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x1322
        L_0x1297:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x12c2
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r0 = r0.title
            r4[r8] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x12ea
        L_0x12c2:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624541(0x7f0e025d, float:1.8876265E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r8] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x12ea:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x1322
        L_0x12f5:
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624538(0x7f0e025a, float:1.8876259E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r5, r0)
            r4[r8] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x1322:
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x13ab
        L_0x1328:
            r0 = 2131558425(0x7f0d0019, float:1.8742165E38)
            if (r11 != r14) goto L_0x133c
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626917(0x7f0e0ba5, float:1.8881084E38)
            java.lang.String r4 = "PhoneCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1399
        L_0x133c:
            r2 = 56
            if (r11 != r2) goto L_0x134f
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627939(0x7f0e0fa3, float:1.8883157E38)
            java.lang.String r4 = "UsernameCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1399
        L_0x134f:
            r2 = 57
            if (r11 != r2) goto L_0x1362
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625755(0x7f0e071b, float:1.8878727E38)
            java.lang.String r4 = "HashtagCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1399
        L_0x1362:
            r2 = 52
            if (r11 != r2) goto L_0x1375
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626139(0x7f0e089b, float:1.8879506E38)
            java.lang.String r4 = "MessageCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1399
        L_0x1375:
            r2 = 59
            if (r11 != r2) goto L_0x138b
            r0 = 2131558525(0x7f0d007d, float:1.8742368E38)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625963(0x7f0e07eb, float:1.8879149E38)
            java.lang.String r4 = "LinkCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1399
        L_0x138b:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627718(0x7f0e0ec6, float:1.8882708E38)
            java.lang.String r4 = "TextCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x1399:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 30
            r2.setAnimation(r0, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
        L_0x13ab:
            r0 = 0
        L_0x13ac:
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r4 = "undo_cancelColor"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.playAnimation()
        L_0x13da:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r1.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r1.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x1407
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r9 = r3.toString()
        L_0x1407:
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r19.isMultilineSubInfo()
            if (r2 == 0) goto L_0x145c
            android.view.ViewParent r0 = r19.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x1427
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x1427:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r8)
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
            goto L_0x1500
        L_0x145c:
            boolean r2 = r19.hasSubInfo()
            if (r2 == 0) goto L_0x146c
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x1500
        L_0x146c:
            android.view.ViewParent r2 = r19.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x1500
            android.view.ViewParent r2 = r19.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x148e
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x148e:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r1.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r8)
            r7 = 0
            r20 = r19
            r21 = r2
            r22 = r3
            r23 = r4
            r24 = r5
            r25 = r7
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r2 = r1.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r1.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x14ca
            r4 = 17
            if (r3 == r4) goto L_0x14ca
            r4 = 18
            if (r3 != r4) goto L_0x14c7
            goto L_0x14ca
        L_0x14c7:
            r10 = 1105199104(0x41e00000, float:28.0)
            goto L_0x14cc
        L_0x14ca:
            r10 = 1096810496(0x41600000, float:14.0)
        L_0x14cc:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = r2 + r3
            r1.undoViewHeight = r2
            int r3 = r1.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x14e6
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x1500
        L_0x14e6:
            r4 = 25
            if (r3 != r4) goto L_0x14f7
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x1500
        L_0x14f7:
            if (r0 == 0) goto L_0x1500
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r0
            r1.undoViewHeight = r2
        L_0x1500:
            int r0 = r19.getVisibility()
            if (r0 == 0) goto L_0x1568
            r1.setVisibility(r8)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x1510
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1512
        L_0x1510:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x1512:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setTranslationY(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r6]
            android.util.Property r3 = android.view.View.TRANSLATION_Y
            r4 = 2
            float[] r4 = new float[r4]
            boolean r5 = r1.fromTop
            if (r5 == 0) goto L_0x1532
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1534
        L_0x1532:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x1534:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r1.undoViewHeight
            int r7 = r7 + r9
            float r7 = (float) r7
            float r5 = r5 * r7
            r4[r8] = r5
            boolean r5 = r1.fromTop
            if (r5 == 0) goto L_0x1547
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x1549
        L_0x1547:
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x1549:
            float r7 = r1.additionalTranslationY
            float r5 = r5 * r7
            r4[r6] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r1, r3, r4)
            r2[r8] = r3
            r0.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x1568:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$5 */
    public /* synthetic */ void lambda$showWithAction$5$UndoView(TLRPC$Message tLRPC$Message, View view) {
        hide(true, 1);
        TLRPC$TL_payments_getPaymentReceipt tLRPC$TL_payments_getPaymentReceipt = new TLRPC$TL_payments_getPaymentReceipt();
        tLRPC$TL_payments_getPaymentReceipt.msg_id = tLRPC$Message.id;
        tLRPC$TL_payments_getPaymentReceipt.peer = this.parentFragment.getMessagesController().getInputPeer(tLRPC$Message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_getPaymentReceipt, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                UndoView.this.lambda$showWithAction$4$UndoView(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$4 */
    public /* synthetic */ void lambda$showWithAction$4$UndoView(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UndoView.this.lambda$showWithAction$3$UndoView(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$3 */
    public /* synthetic */ void lambda$showWithAction$3$UndoView(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = this.currentAction;
        if (i == 1 || i == 0) {
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
                    this.textWidthOut = this.textWidth;
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
}
