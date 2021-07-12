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
    Drawable backgroundDrawable;
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
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

    static /* synthetic */ boolean lambda$showWithAction$3(View view, MotionEvent motionEvent) {
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
        setWillNotDraw(false);
        this.backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("undo_background"));
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
        Theme.setDrawableColor(this.backgroundDrawable, i);
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
            if (i2 == 0 || i2 == 1) {
                MessagesController.getInstance(this.currentAccount).removeDialogAction(this.currentDialogId, this.currentAction == 0, z);
                onRemoveDialogAction(this.currentDialogId, this.currentAction);
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

    /* JADX WARNING: Removed duplicated region for block: B:471:0x140c  */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x142c  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x1453  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x1498  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x1542  */
    /* JADX WARNING: Removed duplicated region for block: B:521:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r21, int r23, java.lang.Object r24, java.lang.Object r25, java.lang.Runnable r26, java.lang.Runnable r27) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r0 = r23
            r4 = r24
            r5 = r25
            r6 = r26
            r7 = r27
            java.lang.Runnable r8 = r1.currentActionRunnable
            if (r8 == 0) goto L_0x0015
            r8.run()
        L_0x0015:
            r8 = 1
            r1.isShown = r8
            r1.currentActionRunnable = r6
            r1.currentCancelRunnable = r7
            r1.currentDialogId = r2
            r1.currentAction = r0
            r9 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r9
            r1.currentInfoObject = r4
            long r9 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r9
            android.widget.TextView r9 = r1.undoTextView
            r10 = 2131627885(0x7f0e0f6d, float:1.8883047E38)
            java.lang.String r11 = "Undo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.String r10 = r10.toUpperCase()
            r9.setText(r10)
            android.widget.ImageView r9 = r1.undoImageView
            r10 = 0
            r9.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r9 = r1.leftImageView
            r9.setPadding(r10, r10, r10, r10)
            android.widget.TextView r9 = r1.infoTextView
            r11 = 1097859072(0x41700000, float:15.0)
            r9.setTextSize(r8, r11)
            org.telegram.ui.Components.BackupImageView r9 = r1.avatarImageView
            r12 = 8
            r9.setVisibility(r12)
            android.widget.TextView r9 = r1.infoTextView
            r13 = 51
            r9.setGravity(r13)
            android.widget.TextView r9 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            r13 = -2
            r9.height = r13
            r13 = 1095761920(0x41500000, float:13.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r9.topMargin = r13
            r9.bottomMargin = r10
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
            if (r6 != 0) goto L_0x00b3
            if (r7 != 0) goto L_0x00b3
            org.telegram.ui.Components.-$$Lambda$UndoView$aFqdairJ69QWpMVu0ABM8OC6FQI r6 = new org.telegram.ui.Components.-$$Lambda$UndoView$aFqdairJ69QWpMVu0ABM8OC6FQI
            r6.<init>()
            r1.setOnClickListener(r6)
            r1.setOnTouchListener(r14)
            goto L_0x00bb
        L_0x00b3:
            r1.setOnClickListener(r14)
            org.telegram.ui.Components.-$$Lambda$UndoView$UGXa_l46bEAu82M3R2HvE5MrPto r6 = org.telegram.ui.Components.$$Lambda$UndoView$UGXa_l46bEAu82M3R2HvE5MrPto.INSTANCE
            r1.setOnTouchListener(r6)
        L_0x00bb:
            android.widget.TextView r6 = r1.infoTextView
            r6.setMovementMethod(r14)
            boolean r6 = r20.isTooltipAction()
            r15 = 42
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r11 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r19 = r13
            r12 = 3000(0xbb8, double:1.482E-320)
            r7 = 36
            if (r6 == 0) goto L_0x079a
            r6 = 74
            if (r0 != r6) goto L_0x0101
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r10)
            r0 = 2131627228(0x7f0e0cdc, float:1.8881715E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627237(0x7f0e0ce5, float:1.8881733E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r2 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            r2 = 36
            r7 = 2131558444(0x7f0d002c, float:1.8742204E38)
            goto L_0x06f5
        L_0x0101:
            r6 = 34
            if (r0 != r6) goto L_0x0140
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628158(0x7f0e107e, float:1.88836E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r4
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
            r0.setVisibility(r10)
            r1.timeLeft = r12
            r0 = r2
        L_0x013b:
            r2 = 36
            r7 = 0
            goto L_0x06f5
        L_0x0140:
            r6 = 44
            if (r0 != r6) goto L_0x0198
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0161
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628111(0x7f0e104f, float:1.8883505E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r0
            java.lang.String r0 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0177
        L_0x0161:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628101(0x7f0e1045, float:1.8883485E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r3[r10] = r0
            java.lang.String r0 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0177:
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
            r2.setVisibility(r10)
            r1.timeLeft = r12
            goto L_0x013b
        L_0x0198:
            r6 = 37
            if (r0 != r6) goto L_0x01e9
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01c2
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x01cf
        L_0x01c2:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x01cf:
            r2 = 2131628218(0x7f0e10ba, float:1.8883722E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r10)
            r1.timeLeft = r12
            goto L_0x013b
        L_0x01e9:
            r6 = 33
            if (r0 != r6) goto L_0x0202
            r0 = 2131628141(0x7f0e106d, float:1.8883566E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558524(0x7f0d007c, float:1.8742366E38)
            r1.timeLeft = r12
            r2 = 36
            r7 = 2131558524(0x7f0d007c, float:1.8742366E38)
            goto L_0x06f5
        L_0x0202:
            r6 = 77
            if (r0 != r6) goto L_0x0232
            r0 = r4
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2 = 2131558463(0x7f0d003f, float:1.8742243E38)
            r3 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r3
            org.telegram.ui.ActionBar.BaseFragment r3 = r1.parentFragment
            if (r3 == 0) goto L_0x022b
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$Message
            if (r3 == 0) goto L_0x022b
            r3 = r5
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            r1.setOnTouchListener(r14)
            android.widget.TextView r4 = r1.infoTextView
            r4.setMovementMethod(r14)
            org.telegram.ui.Components.-$$Lambda$UndoView$8eWxdkpzy3GrADkiuxUowHwapNE r4 = new org.telegram.ui.Components.-$$Lambda$UndoView$8eWxdkpzy3GrADkiuxUowHwapNE
            r4.<init>(r3)
            r1.setOnClickListener(r4)
        L_0x022b:
            r2 = 36
            r7 = 2131558463(0x7f0d003f, float:1.8742243E38)
            goto L_0x06f5
        L_0x0232:
            r6 = 30
            if (r0 != r6) goto L_0x0264
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0242
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0247
        L_0x0242:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0247:
            r2 = 2131628216(0x7f0e10b8, float:1.8883718E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558525(0x7f0d007d, float:1.8742368E38)
            r1.timeLeft = r12
        L_0x025d:
            r2 = 36
            r7 = 2131558525(0x7f0d007d, float:1.8742368E38)
            goto L_0x06f5
        L_0x0264:
            r6 = 35
            if (r0 != r6) goto L_0x0296
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0274
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x027f
        L_0x0274:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x027e
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x027f
        L_0x027e:
            r0 = r11
        L_0x027f:
            r2 = 2131628217(0x7f0e10b9, float:1.888372E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558525(0x7f0d007d, float:1.8742368E38)
            r1.timeLeft = r12
            goto L_0x025d
        L_0x0296:
            r6 = 31
            if (r0 != r6) goto L_0x02c8
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x02a6
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x02ab
        L_0x02a6:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x02ab:
            r2 = 2131628214(0x7f0e10b6, float:1.8883714E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558531(0x7f0d0083, float:1.874238E38)
            r1.timeLeft = r12
        L_0x02c1:
            r2 = 36
            r7 = 2131558531(0x7f0d0083, float:1.874238E38)
            goto L_0x06f5
        L_0x02c8:
            r6 = 38
            if (r0 != r6) goto L_0x0300
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x02e7
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628224(0x7f0e10c0, float:1.8883735E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02f4
        L_0x02e7:
            r0 = 2131628223(0x7f0e10bf, float:1.8883733E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02f4:
            r2 = 2131558517(0x7f0d0075, float:1.8742352E38)
            r1.timeLeft = r12
            r2 = 36
            r7 = 2131558517(0x7f0d0075, float:1.8742352E38)
            goto L_0x06f5
        L_0x0300:
            if (r0 != r15) goto L_0x031b
            r0 = 2131628197(0x7f0e10a5, float:1.888368E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r1.timeLeft = r12
            r2 = 36
            r7 = 2131558448(0x7f0d0030, float:1.8742212E38)
            goto L_0x06f5
        L_0x031b:
            r6 = 43
            if (r0 != r6) goto L_0x0338
            r0 = 2131628198(0x7f0e10a6, float:1.8883682E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r1.timeLeft = r12
            r2 = 36
            r7 = 2131558453(0x7f0d0035, float:1.8742222E38)
            goto L_0x06f5
        L_0x0338:
            int r6 = r1.currentAction
            r15 = 39
            if (r6 != r15) goto L_0x0357
            r0 = 2131628132(0x7f0e1064, float:1.8883548E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r1.timeLeft = r12
            r2 = 36
            r7 = 2131558528(0x7f0d0080, float:1.8742374E38)
            goto L_0x06f5
        L_0x0357:
            r15 = 40
            if (r6 != r15) goto L_0x03ca
            r0 = 2131628131(0x7f0e1063, float:1.8883546E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558527(0x7f0d007f, float:1.8742372E38)
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
            if (r4 < 0) goto L_0x03c2
            if (r0 < 0) goto L_0x03c2
            if (r4 == r0) goto L_0x03c2
            int r5 = r0 + 2
            r3.replace(r0, r5, r11)
            int r5 = r4 + 2
            r3.replace(r4, r5, r11)
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x03be }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03be }
            r6.<init>()     // Catch:{ Exception -> 0x03be }
            java.lang.String r12 = "tg://openmessage?user_id="
            r6.append(r12)     // Catch:{ Exception -> 0x03be }
            int r12 = r1.currentAccount     // Catch:{ Exception -> 0x03be }
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)     // Catch:{ Exception -> 0x03be }
            int r12 = r12.getClientUserId()     // Catch:{ Exception -> 0x03be }
            r6.append(r12)     // Catch:{ Exception -> 0x03be }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x03be }
            r5.<init>(r6)     // Catch:{ Exception -> 0x03be }
            r6 = 2
            int r0 = r0 - r6
            r6 = 33
            r3.setSpan(r5, r4, r0, r6)     // Catch:{ Exception -> 0x03be }
            goto L_0x03c2
        L_0x03be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c2:
            r0 = r3
            r2 = 36
            r7 = 2131558527(0x7f0d007f, float:1.8742372E38)
            goto L_0x06f5
        L_0x03ca:
            if (r0 != r7) goto L_0x03f5
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x03d8
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03dd
        L_0x03d8:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x03dd:
            r2 = 2131628215(0x7f0e10b7, float:1.8883716E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558531(0x7f0d0083, float:1.874238E38)
            r1.timeLeft = r12
            goto L_0x02c1
        L_0x03f5:
            r15 = 32
            if (r0 != r15) goto L_0x0427
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0405
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x040a
        L_0x0405:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x040a:
            r2 = 2131628188(0x7f0e109c, float:1.8883662E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r3[r10] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558523(0x7f0d007b, float:1.8742364E38)
            r1.timeLeft = r12
            r2 = 36
            r7 = 2131558523(0x7f0d007b, float:1.8742364E38)
            goto L_0x06f5
        L_0x0427:
            r12 = 9
            if (r0 == r12) goto L_0x06be
            r12 = 10
            if (r0 != r12) goto L_0x0431
            goto L_0x06be
        L_0x0431:
            r12 = 8
            if (r0 != r12) goto L_0x044b
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r10] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x06f0
        L_0x044b:
            r12 = 22
            if (r0 != r12) goto L_0x04b7
            r5 = 0
            int r0 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x046d
            if (r4 != 0) goto L_0x0462
            r0 = 2131626080(0x7f0e0860, float:1.8879386E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x0462:
            r0 = 2131626081(0x7f0e0861, float:1.8879388E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x046d:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r2 = -r2
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x049f
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x049f
            if (r4 != 0) goto L_0x0494
            r0 = 2131626076(0x7f0e085c, float:1.8879378E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x0494:
            r0 = 2131626077(0x7f0e085d, float:1.887938E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x049f:
            if (r4 != 0) goto L_0x04ac
            r0 = 2131626078(0x7f0e085e, float:1.8879382E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x04ac:
            r0 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x04b7:
            r12 = 23
            if (r0 != r12) goto L_0x04c6
            r0 = 2131624850(0x7f0e0392, float:1.8876891E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06f0
        L_0x04c6:
            r12 = 6
            if (r0 != r12) goto L_0x04e2
            r0 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624303(0x7f0e016f, float:1.8875782E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r7 = 2131558419(0x7f0d0013, float:1.8742153E38)
            r2 = 48
            goto L_0x06f5
        L_0x04e2:
            r12 = 13
            if (r6 != r12) goto L_0x04ff
            r0 = 2131627156(0x7f0e0CLASSNAME, float:1.8881568E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627157(0x7f0e0CLASSNAME, float:1.888157E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r7 = 2131558533(0x7f0d0085, float:1.8742384E38)
        L_0x04fb:
            r2 = 44
            goto L_0x06f5
        L_0x04ff:
            r12 = 14
            if (r6 != r12) goto L_0x0519
            r0 = 2131627158(0x7f0e0CLASSNAME, float:1.8881573E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627159(0x7f0e0CLASSNAME, float:1.8881575E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r7 = 2131558535(0x7f0d0087, float:1.8742389E38)
            goto L_0x04fb
        L_0x0519:
            r6 = 7
            if (r0 != r6) goto L_0x0544
            r0 = 2131624310(0x7f0e0176, float:1.8875796E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x053d
            r2 = 2131624311(0x7f0e0177, float:1.8875798E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x053c:
            r14 = r2
        L_0x053d:
            r2 = 36
            r7 = 2131558418(0x7f0d0012, float:1.8742151E38)
            goto L_0x06f5
        L_0x0544:
            r6 = 20
            if (r0 == r6) goto L_0x05b3
            r6 = 21
            if (r0 != r6) goto L_0x054d
            goto L_0x05b3
        L_0x054d:
            r2 = 19
            if (r0 != r2) goto L_0x0554
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x053d
        L_0x0554:
            r2 = 78
            if (r0 == r2) goto L_0x058b
            r2 = 79
            if (r0 != r2) goto L_0x055d
            goto L_0x058b
        L_0x055d:
            r2 = 3
            if (r0 != r2) goto L_0x056a
            r0 = 2131624820(0x7f0e0374, float:1.887683E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0573
        L_0x056a:
            r0 = 2131624858(0x7f0e039a, float:1.8876908E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0573:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x053d
            r2 = 2131624821(0x7f0e0375, float:1.8876833E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x053c
        L_0x058b:
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r3 = 78
            if (r0 != r3) goto L_0x059d
            java.lang.String r0 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r2)
            goto L_0x05a3
        L_0x059d:
            java.lang.String r0 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r2)
        L_0x05a3:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x05ad
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
            goto L_0x05b0
        L_0x05ad:
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
        L_0x05b0:
            r7 = r2
            goto L_0x06bb
        L_0x05b3:
            org.telegram.messenger.MessagesController$DialogFilter r5 = (org.telegram.messenger.MessagesController.DialogFilter) r5
            r12 = 0
            int r6 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r6 == 0) goto L_0x0663
            int r4 = (int) r2
            if (r4 != 0) goto L_0x05d2
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r6 = 32
            long r2 = r2 >> r6
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            int r4 = r2.user_id
        L_0x05d2:
            if (r4 <= 0) goto L_0x061e
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            r3 = 20
            if (r0 != r3) goto L_0x0602
            r3 = 2131625585(0x7f0e0671, float:1.8878382E38)
            r4 = 2
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r6[r10] = r2
            java.lang.String r2 = r5.name
            r6[r8] = r2
            java.lang.String r2 = "FilterUserAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x06ae
        L_0x0602:
            r4 = 2
            r3 = 2131625586(0x7f0e0672, float:1.8878384E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r6[r10] = r2
            java.lang.String r2 = r5.name
            r6[r8] = r2
            java.lang.String r2 = "FilterUserRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x06ae
        L_0x061e:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            r3 = 20
            if (r0 != r3) goto L_0x064a
            r3 = 2131625524(0x7f0e0634, float:1.8878258E38)
            r4 = 2
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r6[r10] = r2
            java.lang.String r2 = r5.name
            r6[r8] = r2
            java.lang.String r2 = "FilterChatAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x06ae
        L_0x064a:
            r4 = 2
            r3 = 2131625525(0x7f0e0635, float:1.887826E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r6[r10] = r2
            java.lang.String r2 = r5.name
            r6[r8] = r2
            java.lang.String r2 = "FilterChatRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x06ae
        L_0x0663:
            r2 = 20
            if (r0 != r2) goto L_0x068b
            r2 = 2131625528(0x7f0e0638, float:1.8878266E38)
            r3 = 2
            java.lang.Object[] r6 = new java.lang.Object[r3]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r6[r10] = r3
            java.lang.String r3 = r5.name
            r6[r8] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r6)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x06ae
        L_0x068b:
            r2 = 2131625529(0x7f0e0639, float:1.8878269E38)
            r3 = 2
            java.lang.Object[] r6 = new java.lang.Object[r3]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r6[r10] = r3
            java.lang.String r3 = r5.name
            r6[r8] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r6)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x06ae:
            r3 = 20
            if (r0 != r3) goto L_0x06b6
            r0 = 2131558436(0x7f0d0024, float:1.8742188E38)
            goto L_0x06b9
        L_0x06b6:
            r0 = 2131558437(0x7f0d0025, float:1.874219E38)
        L_0x06b9:
            r7 = r0
            r0 = r2
        L_0x06bb:
            r2 = 36
            goto L_0x06f5
        L_0x06be:
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r3 = 9
            if (r0 != r3) goto L_0x06db
            r0 = 2131625278(0x7f0e053e, float:1.887776E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r10] = r2
            java.lang.String r2 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x06f0
        L_0x06db:
            r0 = 2131625279(0x7f0e053f, float:1.8877761E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r10] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x06f0:
            r2 = 36
            r7 = 2131558422(0x7f0d0016, float:1.874216E38)
        L_0x06f5:
            android.widget.TextView r3 = r1.infoTextView
            r3.setText(r0)
            if (r7 == 0) goto L_0x0722
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r7, r2, r2)
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
            goto L_0x0729
        L_0x0722:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0729:
            if (r14 == 0) goto L_0x076a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r9.rightMargin = r0
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
            r0.setTextSize(r8, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x0793
        L_0x076a:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r9.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x0793:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x0e48
        L_0x079a:
            int r6 = r1.currentAction
            r14 = 45
            r12 = 60
            if (r6 == r14) goto L_0x0e4d
            r13 = 46
            if (r6 == r13) goto L_0x0e4d
            r13 = 47
            if (r6 == r13) goto L_0x0e4d
            r13 = 51
            if (r6 == r13) goto L_0x0e4d
            r13 = 50
            if (r6 == r13) goto L_0x0e4d
            r13 = 52
            if (r6 == r13) goto L_0x0e4d
            r13 = 53
            if (r6 == r13) goto L_0x0e4d
            r13 = 54
            if (r6 == r13) goto L_0x0e4d
            r13 = 55
            if (r6 == r13) goto L_0x0e4d
            r13 = 56
            if (r6 == r13) goto L_0x0e4d
            r13 = 57
            if (r6 == r13) goto L_0x0e4d
            r13 = 58
            if (r6 == r13) goto L_0x0e4d
            r13 = 59
            if (r6 == r13) goto L_0x0e4d
            if (r6 == r12) goto L_0x0e4d
            r13 = 71
            if (r6 == r13) goto L_0x0e4d
            r13 = 70
            if (r6 == r13) goto L_0x0e4d
            r13 = 75
            if (r6 == r13) goto L_0x0e4d
            r13 = 76
            if (r6 == r13) goto L_0x0e4d
            r13 = 41
            if (r6 == r13) goto L_0x0e4d
            r13 = 78
            if (r6 == r13) goto L_0x0e4d
            r13 = 79
            if (r6 == r13) goto L_0x0e4d
            r13 = 61
            if (r6 != r13) goto L_0x07f6
            goto L_0x0e4d
        L_0x07f6:
            r12 = 24
            if (r6 == r12) goto L_0x0ce5
            r12 = 25
            if (r6 != r12) goto L_0x0800
            goto L_0x0ce5
        L_0x0800:
            r5 = 11
            if (r6 != r5) goto L_0x0870
            r0 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624430(0x7f0e01ee, float:1.887604E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558422(0x7f0d0016, float:1.874216E38)
            r2.setAnimation(r3, r7, r7)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
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
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e48
        L_0x0870:
            r5 = 15
            if (r6 != r5) goto L_0x093f
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625521(0x7f0e0631, float:1.8878252E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558431(0x7f0d001f, float:1.8742178E38)
            r0.setAnimation(r2, r7, r7)
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
            r9.leftMargin = r2
            r9.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625520(0x7f0e0630, float:1.887825E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            int r3 = r0.indexOf(r15)
            int r0 = r0.lastIndexOf(r15)
            if (r3 < 0) goto L_0x090c
            if (r0 < 0) goto L_0x090c
            if (r3 == r0) goto L_0x090c
            int r4 = r0 + 1
            r2.replace(r0, r4, r11)
            int r4 = r3 + 1
            r2.replace(r3, r4, r11)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r4.<init>(r5)
            int r0 = r0 - r8
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)
        L_0x090c:
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
            goto L_0x0e48
        L_0x093f:
            r5 = 16
            if (r6 == r5) goto L_0x0b6e
            r5 = 17
            if (r6 != r5) goto L_0x0949
            goto L_0x0b6e
        L_0x0949:
            r5 = 18
            if (r6 != r5) goto L_0x09cb
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
            r2.setTextSize(r8, r3)
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
            r9.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r9.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.bottomMargin = r0
            r0 = -1
            r9.height = r0
            r0 = 51
            r13 = r19
            r13.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r13.bottomMargin = r0
            r13.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e48
        L_0x09cb:
            r4 = 12
            if (r6 != r4) goto L_0x0a64
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624964(0x7f0e0404, float:1.8877123E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166093(0x7var_d, float:1.7946422E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131624965(0x7f0e0405, float:1.8877125E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            int r3 = r0.indexOf(r15)
            int r0 = r0.lastIndexOf(r15)
            if (r3 < 0) goto L_0x0a3c
            if (r0 < 0) goto L_0x0a3c
            if (r3 == r0) goto L_0x0a3c
            int r4 = r0 + 1
            r2.replace(r0, r4, r11)
            int r4 = r3 + 1
            r2.replace(r3, r4, r11)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r4.<init>(r5)
            int r0 = r0 - r8
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)
        L_0x0a3c:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r10)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 2
            r0.setMaxLines(r4)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            goto L_0x0e48
        L_0x0a64:
            r4 = 2
            if (r6 == r4) goto L_0x0b0a
            r4 = 4
            if (r6 != r4) goto L_0x0a6c
            goto L_0x0b0a
        L_0x0a6c:
            r0 = 1110704128(0x42340000, float:45.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.topMargin = r0
            r9.rightMargin = r10
            android.widget.TextView r0 = r1.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r4)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r10)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 8
            r0.setVisibility(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r4)
            int r0 = r1.currentAction
            if (r0 != 0) goto L_0x0ab0
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625789(0x7f0e073d, float:1.8878796E38)
            java.lang.String r5 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0af8
        L_0x0ab0:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x0aea
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r4.getChat(r0)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r4 == 0) goto L_0x0adb
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0adb
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624728(0x7f0e0318, float:1.8876644E38)
            java.lang.String r5 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0af8
        L_0x0adb:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625742(0x7f0e070e, float:1.88787E38)
            java.lang.String r5 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0af8
        L_0x0aea:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624824(0x7f0e0378, float:1.8876839E38)
            java.lang.String r5 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
        L_0x0af8:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r4 = r1.currentAction
            if (r4 != 0) goto L_0x0b04
            r4 = 1
            goto L_0x0b05
        L_0x0b04:
            r4 = 0
        L_0x0b05:
            r0.addDialogAction(r2, r4)
            goto L_0x0e48
        L_0x0b0a:
            r2 = 2
            if (r0 != r2) goto L_0x0b1c
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624820(0x7f0e0374, float:1.887683E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0b2a
        L_0x0b1c:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624858(0x7f0e039a, float:1.8876908E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0b2a:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.topMargin = r0
            r9.rightMargin = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r2)
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
            r2 = 2131558416(0x7f0d0010, float:1.8742147E38)
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e48
        L_0x0b6e:
            r13 = r19
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
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
            if (r2 == 0) goto L_0x0bb4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625205(0x7f0e04f5, float:1.8877611E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165388(0x7var_cc, float:1.7944992E38)
            r0.setImageResource(r2)
            goto L_0x0c5c
        L_0x0bb4:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0bd1
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625072(0x7f0e0470, float:1.8877342E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0bce:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0c2a
        L_0x0bd1:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0CLASSNAME
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r10)
            r3.setText(r2)
            goto L_0x0bce
        L_0x0CLASSNAME:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625204(0x7f0e04f4, float:1.887761E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            r4[r10] = r0
            java.lang.String r5 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r6, r10)
            r2.setText(r3)
        L_0x0c2a:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r9.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r9.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r13.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.height = r0
        L_0x0c5c:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627426(0x7f0e0da2, float:1.8882116E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0cae
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
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r10)
            goto L_0x0cbe
        L_0x0cae:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0cbe:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r2
            r9.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9.bottomMargin = r0
            r0 = -1
            r9.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r10)
            goto L_0x0e48
        L_0x0ce5:
            r2 = 8
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r5
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r10)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0db4
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r1.infoTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r8, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r6 = "BODY.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r6 = "Wibe Big.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r6 = "Wibe Big 3.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r5 = "Wibe Small.**"
            r4.setLayerColor(r5, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627112(0x7f0e0CLASSNAME, float:1.888148E38)
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r5 = 28
            r6 = 28
            r2.setAnimation(r4, r5, r6)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0d8d
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627114(0x7f0e0c6a, float:1.8881483E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r10] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r6[r8] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
            goto L_0x0da5
        L_0x0d8d:
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627113(0x7f0e0CLASSNAME, float:1.8881481E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r4[r10] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0da5:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            goto L_0x0e37
        L_0x0db4:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r3)
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
            r9.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627111(0x7f0e0CLASSNAME, float:1.8881477E38)
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
            r0.setVisibility(r10)
        L_0x0e37:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0e48:
            r0 = 0
            r15 = 1096810496(0x41600000, float:14.0)
            goto L_0x1416
        L_0x0e4d:
            android.widget.ImageView r6 = r1.undoImageView
            r13 = 8
            r6.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r6 = r1.leftImageView
            r6.setVisibility(r10)
            android.widget.TextView r6 = r1.infoTextView
            android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
            r6.setTypeface(r13)
            r13 = -1
            int r6 = r1.currentAction
            r15 = 76
            r16 = 1091567616(0x41100000, float:9.0)
            if (r6 != r15) goto L_0x0e92
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624607(0x7f0e029f, float:1.8876398E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
        L_0x0e8d:
            r0 = 1
            r15 = 1096810496(0x41600000, float:14.0)
            goto L_0x13d8
        L_0x0e92:
            r15 = 75
            if (r6 != r15) goto L_0x0eba
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
            goto L_0x0e8d
        L_0x0eba:
            r15 = 70
            if (r0 != r15) goto L_0x0f2b
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r10)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x0edd
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0efa
        L_0x0edd:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x0eea
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0efa
        L_0x0eea:
            if (r0 < r12) goto L_0x0ef4
            int r0 = r0 / r12
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0efa
        L_0x0ef4:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0efa:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            r4[r10] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            goto L_0x0e8d
        L_0x0f2b:
            r0 = 71
            if (r6 != r0) goto L_0x0f5f
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624442(0x7f0e01fa, float:1.8876064E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r0.setAnimation(r2, r7, r7)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            r15 = 1096810496(0x41600000, float:14.0)
            goto L_0x13d7
        L_0x0f5f:
            r0 = 45
            if (r6 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625837(0x7f0e076d, float:1.8878893E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
            goto L_0x0e8d
        L_0x0var_:
            r0 = 46
            if (r6 != r0) goto L_0x0fb1
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625838(0x7f0e076e, float:1.8878895E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
            goto L_0x0e8d
        L_0x0fb1:
            r0 = 47
            if (r6 != r0) goto L_0x0fe6
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625860(0x7f0e0784, float:1.887894E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r15 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r15)
            r0 = 1
            goto L_0x13d8
        L_0x0fe6:
            r15 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r6 != r0) goto L_0x100f
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r2, r7, r7)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r2)
            goto L_0x13d7
        L_0x100f:
            r0 = 50
            if (r6 != r0) goto L_0x1036
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r0.setAnimation(r2, r7, r7)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r2)
            goto L_0x13d7
        L_0x1036:
            r0 = 52
            if (r6 == r0) goto L_0x1354
            r0 = 56
            if (r6 == r0) goto L_0x1354
            r0 = 57
            if (r6 == r0) goto L_0x1354
            r0 = 58
            if (r6 == r0) goto L_0x1354
            r0 = 59
            if (r6 == r0) goto L_0x1354
            if (r6 != r12) goto L_0x104e
            goto L_0x1354
        L_0x104e:
            r0 = 54
            if (r6 != r0) goto L_0x1077
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624764(0x7f0e033c, float:1.8876717E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOn"
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
            r0.setTextSize(r8, r2)
            goto L_0x13d7
        L_0x1077:
            r0 = 55
            if (r6 != r0) goto L_0x10a0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558477(0x7f0d004d, float:1.874227E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r2)
            goto L_0x13d7
        L_0x10a0:
            r0 = 41
            if (r6 != r0) goto L_0x114b
            if (r5 != 0) goto L_0x111a
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r4 = (long) r0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x10c6
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625883(0x7f0e079b, float:1.8878987E38)
            java.lang.String r3 = "InvLinkToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x113d
        L_0x10c6:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x10f1
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625882(0x7f0e079a, float:1.8878984E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r4[r10] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x113d
        L_0x10f1:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625884(0x7f0e079c, float:1.8878989E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r10] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x113d
        L_0x111a:
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625881(0x7f0e0799, float:1.8878982E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r5 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r5, r0)
            r4[r10] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x113d:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558422(0x7f0d0016, float:1.874216E38)
            r0.setAnimation(r2, r7, r7)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x13d7
        L_0x114b:
            r0 = 53
            if (r6 != r0) goto L_0x128f
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r5 != 0) goto L_0x1236
            int r4 = r1.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r4 = (long) r4
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x1198
            int r0 = r0.intValue()
            if (r0 != r8) goto L_0x117a
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625692(0x7f0e06dc, float:1.88786E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x118c
        L_0x117a:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x118c:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558475(0x7f0d004b, float:1.8742267E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x1289
        L_0x1198:
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x11e2
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            int r0 = r0.intValue()
            if (r0 != r8) goto L_0x11c9
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625691(0x7f0e06db, float:1.8878597E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r2 = r2.title
            r4[r10] = r2
            java.lang.String r2 = "FwdMessageToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x122b
        L_0x11c9:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625695(0x7f0e06df, float:1.8878605E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r2 = r2.title
            r4[r10] = r2
            java.lang.String r2 = "FwdMessagesToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x122b
        L_0x11e2:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            int r0 = r0.intValue()
            if (r0 != r8) goto L_0x1211
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625693(0x7f0e06dd, float:1.8878601E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r10] = r2
            java.lang.String r2 = "FwdMessageToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x122b
        L_0x1211:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625697(0x7f0e06e1, float:1.887861E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r10] = r2
            java.lang.String r2 = "FwdMessagesToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x122b:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x1286
        L_0x1236:
            r2 = r5
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r0 = r0.intValue()
            if (r0 != r8) goto L_0x1260
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r5 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r4[r10] = r2
            java.lang.String r2 = "FwdMessageToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x127c
        L_0x1260:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625694(0x7f0e06de, float:1.8878603E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r5 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r4[r10] = r2
            java.lang.String r2 = "FwdMessagesToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x127c:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x1286:
            r2 = 300(0x12c, double:1.48E-321)
            r13 = r2
        L_0x1289:
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x13d7
        L_0x128f:
            r0 = 61
            if (r6 != r0) goto L_0x13d7
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r5 != 0) goto L_0x1321
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r4 = (long) r0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x12c3
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624542(0x7f0e025e, float:1.8876267E38)
            java.lang.String r3 = "BackgroundToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558475(0x7f0d004b, float:1.8742267E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x134e
        L_0x12c3:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x12ee
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624541(0x7f0e025d, float:1.8876265E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r4[r10] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x1316
        L_0x12ee:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624543(0x7f0e025f, float:1.8876269E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r10] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x1316:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x134e
        L_0x1321:
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624540(0x7f0e025c, float:1.8876263E38)
            java.lang.Object[] r4 = new java.lang.Object[r8]
            java.lang.String r5 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r5, r0)
            r4[r10] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x134e:
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x13d7
        L_0x1354:
            r0 = 2131558425(0x7f0d0019, float:1.8742165E38)
            if (r6 != r12) goto L_0x1368
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            java.lang.String r4 = "PhoneCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x13c5
        L_0x1368:
            r2 = 56
            if (r6 != r2) goto L_0x137b
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627988(0x7f0e0fd4, float:1.8883256E38)
            java.lang.String r4 = "UsernameCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x13c5
        L_0x137b:
            r2 = 57
            if (r6 != r2) goto L_0x138e
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625776(0x7f0e0730, float:1.887877E38)
            java.lang.String r4 = "HashtagCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x13c5
        L_0x138e:
            r2 = 52
            if (r6 != r2) goto L_0x13a1
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            java.lang.String r4 = "MessageCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x13c5
        L_0x13a1:
            r2 = 59
            if (r6 != r2) goto L_0x13b7
            r0 = 2131558524(0x7f0d007c, float:1.8742366E38)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626001(0x7f0e0811, float:1.8879226E38)
            java.lang.String r4 = "LinkCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x13c5
        L_0x13b7:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627767(0x7f0e0ef7, float:1.8882808E38)
            java.lang.String r4 = "TextCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x13c5:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 30
            r2.setAnimation(r0, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r2)
        L_0x13d7:
            r0 = 0
        L_0x13d8:
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
            r9.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r9.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.playAnimation()
            r2 = 0
            int r4 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x1416
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.ui.Components.-$$Lambda$UndoView$DCwv0P0ONjLA2IL1-euLZvuAWO4 r3 = new org.telegram.ui.Components.-$$Lambda$UndoView$DCwv0P0ONjLA2IL1-euLZvuAWO4
            r3.<init>()
            r2.postDelayed(r3, r13)
        L_0x1416:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r1.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r1.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x1443
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r11 = r3.toString()
        L_0x1443:
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r20.isMultilineSubInfo()
            if (r2 == 0) goto L_0x1498
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x1463
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x1463:
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
            goto L_0x153c
        L_0x1498:
            boolean r2 = r20.hasSubInfo()
            if (r2 == 0) goto L_0x14a8
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x153c
        L_0x14a8:
            android.view.ViewParent r2 = r20.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x153c
            android.view.ViewParent r2 = r20.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x14ca
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x14ca:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r1.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r10)
            r6 = 0
            r21 = r20
            r22 = r2
            r23 = r3
            r24 = r4
            r25 = r5
            r26 = r6
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r2 = r1.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r1.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x1506
            r4 = 17
            if (r3 == r4) goto L_0x1506
            r4 = 18
            if (r3 != r4) goto L_0x1503
            goto L_0x1506
        L_0x1503:
            r12 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1508
        L_0x1506:
            r12 = 1096810496(0x41600000, float:14.0)
        L_0x1508:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 + r3
            r1.undoViewHeight = r2
            int r3 = r1.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x1522
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x153c
        L_0x1522:
            r4 = 25
            if (r3 != r4) goto L_0x1533
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x153c
        L_0x1533:
            if (r0 == 0) goto L_0x153c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r0
            r1.undoViewHeight = r2
        L_0x153c:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x15a0
            r1.setVisibility(r10)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x154c
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x154e
        L_0x154c:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x154e:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setEnterOffset(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r8]
            r3 = 2
            float[] r3 = new float[r3]
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x156c
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x156e
        L_0x156c:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x156e:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r1.undoViewHeight
            int r5 = r5 + r6
            float r5 = (float) r5
            float r4 = r4 * r5
            r3[r10] = r4
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x1581
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x1583
        L_0x1581:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x1583:
            r3[r8] = r4
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
        L_0x15a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$2 */
    public /* synthetic */ void lambda$showWithAction$2$UndoView(View view) {
        hide(false, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$6 */
    public /* synthetic */ void lambda$showWithAction$6$UndoView(TLRPC$Message tLRPC$Message, View view) {
        hide(true, 1);
        TLRPC$TL_payments_getPaymentReceipt tLRPC$TL_payments_getPaymentReceipt = new TLRPC$TL_payments_getPaymentReceipt();
        tLRPC$TL_payments_getPaymentReceipt.msg_id = tLRPC$Message.id;
        tLRPC$TL_payments_getPaymentReceipt.peer = this.parentFragment.getMessagesController().getInputPeer(tLRPC$Message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_getPaymentReceipt, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                UndoView.this.lambda$showWithAction$5$UndoView(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$5 */
    public /* synthetic */ void lambda$showWithAction$5$UndoView(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UndoView.this.lambda$showWithAction$4$UndoView(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$4 */
    public /* synthetic */ void lambda$showWithAction$4$UndoView(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$7 */
    public /* synthetic */ void lambda$showWithAction$7$UndoView() {
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
}
