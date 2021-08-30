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
        this.undoButton.setOnClickListener(new UndoView$$ExternalSyntheticLambda0(this));
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

    /* JADX WARNING: Removed duplicated region for block: B:497:0x1455  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x1475  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x149c  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x14e1  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x158b  */
    /* JADX WARNING: Removed duplicated region for block: B:550:? A[RETURN, SYNTHETIC] */
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
            java.lang.Runnable r7 = r1.currentActionRunnable
            if (r7 == 0) goto L_0x0015
            r7.run()
        L_0x0015:
            r7 = 1
            r1.isShown = r7
            r1.currentActionRunnable = r5
            r1.currentCancelRunnable = r6
            r1.currentDialogIds = r0
            r8 = 0
            java.lang.Object r9 = r0.get(r8)
            java.lang.Long r9 = (java.lang.Long) r9
            long r9 = r9.longValue()
            r1.currentAction = r2
            r11 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r11
            r1.currentInfoObject = r3
            long r11 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r11
            android.widget.TextView r11 = r1.undoTextView
            r12 = 2131628012(0x7f0e0fec, float:1.8883305E38)
            java.lang.String r13 = "Undo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.String r12 = r12.toUpperCase()
            r11.setText(r12)
            android.widget.ImageView r11 = r1.undoImageView
            r11.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r11 = r1.leftImageView
            r11.setPadding(r8, r8, r8, r8)
            android.widget.TextView r11 = r1.infoTextView
            r12 = 1097859072(0x41700000, float:15.0)
            r11.setTextSize(r7, r12)
            org.telegram.ui.Components.BackupImageView r11 = r1.avatarImageView
            r13 = 8
            r11.setVisibility(r13)
            android.widget.TextView r11 = r1.infoTextView
            r14 = 51
            r11.setGravity(r14)
            android.widget.TextView r11 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r11 = r11.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r11 = (android.widget.FrameLayout.LayoutParams) r11
            r14 = -2
            r11.height = r14
            r14 = 1095761920(0x41500000, float:13.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r11.topMargin = r14
            r11.bottomMargin = r8
            org.telegram.ui.Components.RLottieImageView r14 = r1.leftImageView
            android.widget.ImageView$ScaleType r15 = android.widget.ImageView.ScaleType.CENTER
            r14.setScaleType(r15)
            org.telegram.ui.Components.RLottieImageView r14 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r15 = 19
            r14.gravity = r15
            r14.bottomMargin = r8
            r14.topMargin = r8
            r15 = 1077936128(0x40400000, float:3.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r14.leftMargin = r15
            r15 = 1113063424(0x42580000, float:54.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r14.width = r15
            r15 = -2
            r14.height = r15
            android.widget.TextView r15 = r1.infoTextView
            r15.setMinHeight(r8)
            r15 = 0
            if (r5 != 0) goto L_0x00bd
            if (r6 != 0) goto L_0x00bd
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r5.<init>(r1)
            r1.setOnClickListener(r5)
            r1.setOnTouchListener(r15)
            goto L_0x00c5
        L_0x00bd:
            r1.setOnClickListener(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r5 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r1.setOnTouchListener(r5)
        L_0x00c5:
            android.widget.TextView r5 = r1.infoTextView
            r5.setMovementMethod(r15)
            boolean r5 = r20.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r12 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r19 = r14
            r13 = 3000(0xbb8, double:1.482E-320)
            r6 = 36
            if (r5 == 0) goto L_0x0784
            r0 = 74
            if (r2 != r0) goto L_0x0104
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            r0 = 2131627335(0x7f0e0d47, float:1.8881932E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627344(0x7f0e0d50, float:1.888195E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r2 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            goto L_0x06df
        L_0x0104:
            r0 = 34
            if (r2 != r0) goto L_0x0141
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628287(0x7f0e10ff, float:1.8883862E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
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
            r1.timeLeft = r13
            r0 = r2
        L_0x013e:
            r2 = 0
            goto L_0x06df
        L_0x0141:
            r0 = 44
            if (r2 != r0) goto L_0x0198
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0162
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628239(0x7f0e10cf, float:1.8883765E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r8] = r0
            java.lang.String r0 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0178
        L_0x0162:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628229(0x7f0e10c5, float:1.8883745E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r4[r8] = r0
            java.lang.String r0 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0178:
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
            r2.setVisibility(r8)
            r1.timeLeft = r13
            goto L_0x013e
        L_0x0198:
            r0 = 37
            if (r2 != r0) goto L_0x01e9
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01c2
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x01cf
        L_0x01c2:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x01cf:
            r2 = 2131628348(0x7f0e113c, float:1.8883986E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r8)
            r1.timeLeft = r13
            goto L_0x013e
        L_0x01e9:
            r0 = 33
            if (r2 != r0) goto L_0x01fd
            r0 = 2131628270(0x7f0e10ee, float:1.8883828E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558527(0x7f0d007f, float:1.8742372E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x01fd:
            r0 = 77
            if (r2 != r0) goto L_0x0228
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2 = 2131558463(0x7f0d003f, float:1.8742243E38)
            r9 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r9
            org.telegram.ui.ActionBar.BaseFragment r3 = r1.parentFragment
            if (r3 == 0) goto L_0x06df
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$Message
            if (r3 == 0) goto L_0x06df
            r3 = r4
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            r1.setOnTouchListener(r15)
            android.widget.TextView r4 = r1.infoTextView
            r4.setMovementMethod(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r4.<init>(r1, r3)
            r1.setOnClickListener(r4)
            goto L_0x06df
        L_0x0228:
            r0 = 30
            if (r2 != r0) goto L_0x0255
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0238
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x023d
        L_0x0238:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x023d:
            r2 = 2131628346(0x7f0e113a, float:1.8883982E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x0255:
            r0 = 35
            if (r2 != r0) goto L_0x0288
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0265
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0270
        L_0x0265:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x026f
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0270
        L_0x026f:
            r0 = r12
        L_0x0270:
            r2 = 2131628347(0x7f0e113b, float:1.8883984E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x0288:
            r0 = 31
            if (r2 != r0) goto L_0x02b5
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0298
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x029d
        L_0x0298:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x029d:
            r2 = 2131628344(0x7f0e1138, float:1.8883978E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558534(0x7f0d0086, float:1.8742387E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x02b5:
            r0 = 38
            if (r2 != r0) goto L_0x02e8
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x02d4
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628356(0x7f0e1144, float:1.8884002E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02e1
        L_0x02d4:
            r0 = 2131628355(0x7f0e1143, float:1.8884E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02e1:
            r2 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x02e8:
            r0 = 42
            if (r2 != r0) goto L_0x0300
            r0 = 2131628326(0x7f0e1126, float:1.8883942E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x0300:
            r0 = 43
            if (r2 != r0) goto L_0x0318
            r0 = 2131628327(0x7f0e1127, float:1.8883944E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x0318:
            int r0 = r1.currentAction
            r5 = 39
            if (r0 == r5) goto L_0x06c3
            r5 = 100
            if (r0 != r5) goto L_0x0324
            goto L_0x06c3
        L_0x0324:
            r5 = 40
            if (r0 == r5) goto L_0x0650
            r5 = 101(0x65, float:1.42E-43)
            if (r0 != r5) goto L_0x032e
            goto L_0x0650
        L_0x032e:
            if (r2 != r6) goto L_0x0359
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x033c
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0341
        L_0x033c:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0341:
            r2 = 2131628345(0x7f0e1139, float:1.888398E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558534(0x7f0d0086, float:1.8742387E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x0359:
            r5 = 32
            if (r2 != r5) goto L_0x0386
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0369
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x036e
        L_0x0369:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x036e:
            r2 = 2131628317(0x7f0e111d, float:1.8883923E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r1.timeLeft = r13
            goto L_0x06df
        L_0x0386:
            r5 = 9
            if (r2 == r5) goto L_0x0619
            r5 = 10
            if (r2 != r5) goto L_0x0390
            goto L_0x0619
        L_0x0390:
            r5 = 8
            if (r2 != r5) goto L_0x03aa
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x064b
        L_0x03aa:
            r5 = 22
            if (r2 != r5) goto L_0x0416
            r4 = 0
            int r0 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x03cc
            if (r3 != 0) goto L_0x03c1
            r0 = 2131626109(0x7f0e087d, float:1.8879445E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x03c1:
            r0 = 2131626110(0x7f0e087e, float:1.8879447E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x03cc:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r4 = -r9
            int r2 = (int) r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x03fe
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x03fe
            if (r3 != 0) goto L_0x03f3
            r0 = 2131626105(0x7f0e0879, float:1.8879437E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x03f3:
            r0 = 2131626106(0x7f0e087a, float:1.8879439E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x03fe:
            if (r3 != 0) goto L_0x040b
            r0 = 2131626107(0x7f0e087b, float:1.887944E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x040b:
            r0 = 2131626108(0x7f0e087c, float:1.8879443E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x0416:
            r5 = 23
            if (r2 != r5) goto L_0x0425
            r0 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x064b
        L_0x0425:
            r5 = 6
            if (r2 != r5) goto L_0x0441
            r0 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624303(0x7f0e016f, float:1.8875782E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558419(0x7f0d0013, float:1.8742153E38)
            r6 = 48
            goto L_0x06df
        L_0x0441:
            r5 = 13
            if (r0 != r5) goto L_0x045e
            r0 = 2131627258(0x7f0e0cfa, float:1.8881775E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627259(0x7f0e0cfb, float:1.8881777E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558536(0x7f0d0088, float:1.874239E38)
        L_0x045a:
            r6 = 44
            goto L_0x06df
        L_0x045e:
            r5 = 14
            if (r0 != r5) goto L_0x0478
            r0 = 2131627260(0x7f0e0cfc, float:1.888178E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627261(0x7f0e0cfd, float:1.8881781E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558538(0x7f0d008a, float:1.8742395E38)
            goto L_0x045a
        L_0x0478:
            r0 = 7
            if (r2 != r0) goto L_0x04a1
            r0 = 2131624310(0x7f0e0176, float:1.8875796E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x049c
            r2 = 2131624311(0x7f0e0177, float:1.8875798E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x049b:
            r15 = r2
        L_0x049c:
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            goto L_0x06df
        L_0x04a1:
            r0 = 20
            if (r2 == r0) goto L_0x0510
            r0 = 21
            if (r2 != r0) goto L_0x04aa
            goto L_0x0510
        L_0x04aa:
            r0 = 19
            if (r2 != r0) goto L_0x04b1
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x049c
        L_0x04b1:
            r0 = 78
            if (r2 == r0) goto L_0x04e8
            r0 = 79
            if (r2 != r0) goto L_0x04ba
            goto L_0x04e8
        L_0x04ba:
            r0 = 3
            if (r2 != r0) goto L_0x04c7
            r0 = 2131624826(0x7f0e037a, float:1.8876843E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x04d0
        L_0x04c7:
            r0 = 2131624864(0x7f0e03a0, float:1.887692E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x04d0:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x049c
            r2 = 2131624827(0x7f0e037b, float:1.8876845E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x049b
        L_0x04e8:
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = 78
            if (r2 != r3) goto L_0x04fa
            java.lang.String r2 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0500
        L_0x04fa:
            java.lang.String r2 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0500:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x050b
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
            goto L_0x06df
        L_0x050b:
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
            goto L_0x06df
        L_0x0510:
            r0 = r4
            org.telegram.messenger.MessagesController$DialogFilter r0 = (org.telegram.messenger.MessagesController.DialogFilter) r0
            r4 = 0
            int r13 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r13 == 0) goto L_0x05c2
            int r3 = (int) r9
            if (r3 != 0) goto L_0x0531
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            r4 = 32
            long r4 = r9 >> r4
            int r5 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            int r3 = r3.user_id
        L_0x0531:
            if (r3 <= 0) goto L_0x057d
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r4.getUser(r3)
            r4 = 20
            if (r2 != r4) goto L_0x0561
            r4 = 2131625598(0x7f0e067e, float:1.8878408E38)
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r9[r8] = r3
            java.lang.String r0 = r0.name
            r9[r7] = r0
            java.lang.String r0 = "FilterUserAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x060b
        L_0x0561:
            r5 = 2
            r4 = 2131625599(0x7f0e067f, float:1.887841E38)
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r9[r8] = r3
            java.lang.String r0 = r0.name
            r9[r7] = r0
            java.lang.String r0 = "FilterUserRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x060b
        L_0x057d:
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            r4 = 20
            if (r2 != r4) goto L_0x05a9
            r4 = 2131625537(0x7f0e0641, float:1.8878285E38)
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r9[r8] = r3
            java.lang.String r0 = r0.name
            r9[r7] = r0
            java.lang.String r0 = "FilterChatAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x060b
        L_0x05a9:
            r5 = 2
            r4 = 2131625538(0x7f0e0642, float:1.8878287E38)
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r9[r8] = r3
            java.lang.String r0 = r0.name
            r9[r7] = r0
            java.lang.String r0 = "FilterChatRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x060b
        L_0x05c2:
            r4 = 20
            if (r2 != r4) goto L_0x05e9
            r4 = 2131625541(0x7f0e0645, float:1.8878293E38)
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)
            r9[r8] = r3
            java.lang.String r0 = r0.name
            r9[r7] = r0
            java.lang.String r0 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x060b
        L_0x05e9:
            r4 = 2131625542(0x7f0e0646, float:1.8878295E38)
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)
            r9[r8] = r3
            java.lang.String r0 = r0.name
            r9[r7] = r0
            java.lang.String r0 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x060b:
            r3 = 20
            if (r2 != r3) goto L_0x0614
            r2 = 2131558436(0x7f0d0024, float:1.8742188E38)
            goto L_0x06df
        L_0x0614:
            r2 = 2131558437(0x7f0d0025, float:1.874219E38)
            goto L_0x06df
        L_0x0619:
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r3 = 9
            if (r2 != r3) goto L_0x0636
            r2 = 2131625286(0x7f0e0546, float:1.8877776E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r0
            java.lang.String r0 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x064b
        L_0x0636:
            r2 = 2131625287(0x7f0e0547, float:1.8877778E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r0
            java.lang.String r0 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x064b:
            r2 = 2131558422(0x7f0d0016, float:1.874216E38)
            goto L_0x06df
        L_0x0650:
            r2 = 40
            if (r0 != r2) goto L_0x065a
            r0 = 2131628260(0x7f0e10e4, float:1.8883808E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            goto L_0x065f
        L_0x065a:
            r0 = 2131628350(0x7f0e113e, float:1.888399E38)
            java.lang.String r2 = "VoipGroupVideoRecordSaved"
        L_0x065f:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558530(0x7f0d0082, float:1.8742378E38)
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
            if (r4 < 0) goto L_0x06c1
            if (r0 < 0) goto L_0x06c1
            if (r4 == r0) goto L_0x06c1
            int r5 = r0 + 2
            r3.replace(r0, r5, r12)
            int r5 = r4 + 2
            r3.replace(r4, r5, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x06bd }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06bd }
            r9.<init>()     // Catch:{ Exception -> 0x06bd }
            java.lang.String r10 = "tg://openmessage?user_id="
            r9.append(r10)     // Catch:{ Exception -> 0x06bd }
            int r10 = r1.currentAccount     // Catch:{ Exception -> 0x06bd }
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)     // Catch:{ Exception -> 0x06bd }
            int r10 = r10.getClientUserId()     // Catch:{ Exception -> 0x06bd }
            r9.append(r10)     // Catch:{ Exception -> 0x06bd }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x06bd }
            r5.<init>(r9)     // Catch:{ Exception -> 0x06bd }
            r9 = 2
            int r0 = r0 - r9
            r9 = 33
            r3.setSpan(r5, r4, r0, r9)     // Catch:{ Exception -> 0x06bd }
            goto L_0x06c1
        L_0x06bd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x06c1:
            r0 = r3
            goto L_0x06df
        L_0x06c3:
            r2 = 39
            if (r0 != r2) goto L_0x06cd
            r0 = 2131628261(0x7f0e10e5, float:1.888381E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            goto L_0x06d2
        L_0x06cd:
            r0 = 2131628351(0x7f0e113f, float:1.8883992E38)
            java.lang.String r2 = "VoipGroupVideoRecordStarted"
        L_0x06d2:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558531(0x7f0d0083, float:1.874238E38)
            r1.timeLeft = r13
        L_0x06df:
            android.widget.TextView r3 = r1.infoTextView
            r3.setText(r0)
            if (r2 == 0) goto L_0x070c
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r2, r6, r6)
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
            goto L_0x0713
        L_0x070c:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0713:
            if (r15 == 0) goto L_0x0754
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r15)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x077d
        L_0x0754:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x077d:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x0e69
        L_0x0784:
            int r5 = r1.currentAction
            r15 = 45
            r13 = 60
            if (r5 == r15) goto L_0x0e6e
            r14 = 46
            if (r5 == r14) goto L_0x0e6e
            r14 = 47
            if (r5 == r14) goto L_0x0e6e
            r14 = 51
            if (r5 == r14) goto L_0x0e6e
            r14 = 50
            if (r5 == r14) goto L_0x0e6e
            r14 = 52
            if (r5 == r14) goto L_0x0e6e
            r14 = 53
            if (r5 == r14) goto L_0x0e6e
            r14 = 54
            if (r5 == r14) goto L_0x0e6e
            r14 = 55
            if (r5 == r14) goto L_0x0e6e
            r14 = 56
            if (r5 == r14) goto L_0x0e6e
            r14 = 57
            if (r5 == r14) goto L_0x0e6e
            r14 = 58
            if (r5 == r14) goto L_0x0e6e
            r14 = 59
            if (r5 == r14) goto L_0x0e6e
            if (r5 == r13) goto L_0x0e6e
            r14 = 71
            if (r5 == r14) goto L_0x0e6e
            r14 = 70
            if (r5 == r14) goto L_0x0e6e
            r14 = 75
            if (r5 == r14) goto L_0x0e6e
            r14 = 76
            if (r5 == r14) goto L_0x0e6e
            r14 = 41
            if (r5 == r14) goto L_0x0e6e
            r14 = 78
            if (r5 == r14) goto L_0x0e6e
            r14 = 79
            if (r5 == r14) goto L_0x0e6e
            r14 = 61
            if (r5 == r14) goto L_0x0e6e
            r14 = 80
            if (r5 != r14) goto L_0x07e4
            goto L_0x0e6e
        L_0x07e4:
            r13 = 24
            if (r5 == r13) goto L_0x0d06
            r13 = 25
            if (r5 != r13) goto L_0x07ee
            goto L_0x0d06
        L_0x07ee:
            r4 = 11
            if (r5 != r4) goto L_0x085e
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624430(0x7f0e01ee, float:1.887604E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558422(0x7f0d0016, float:1.874216E38)
            r2.setAnimation(r3, r6, r6)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
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
            goto L_0x0e69
        L_0x085e:
            r4 = 15
            if (r5 != r4) goto L_0x092f
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625534(0x7f0e063e, float:1.8878279E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558431(0x7f0d001f, float:1.8742178E38)
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
            r11.leftMargin = r2
            r11.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625533(0x7f0e063d, float:1.8878277E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x08fc
            if (r0 < 0) goto L_0x08fc
            if (r4 == r0) goto L_0x08fc
            int r3 = r0 + 1
            r2.replace(r0, r3, r12)
            int r3 = r4 + 1
            r2.replace(r4, r3, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r3.<init>(r5)
            int r0 = r0 - r7
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x08fc:
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
            goto L_0x0e69
        L_0x092f:
            r4 = 16
            if (r5 == r4) goto L_0x0b8f
            r4 = 17
            if (r5 != r4) goto L_0x0939
            goto L_0x0b8f
        L_0x0939:
            r4 = 18
            if (r5 != r4) goto L_0x09bb
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
            r11.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.bottomMargin = r0
            r0 = -1
            r11.height = r0
            r0 = 51
            r14 = r19
            r14.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r14.bottomMargin = r0
            r14.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e69
        L_0x09bb:
            r3 = 12
            if (r5 != r3) goto L_0x0a56
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624972(0x7f0e040c, float:1.8877139E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166113(0x7var_a1, float:1.7946462E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131624973(0x7f0e040d, float:1.887714E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0a2e
            if (r0 < 0) goto L_0x0a2e
            if (r4 == r0) goto L_0x0a2e
            int r3 = r0 + 1
            r2.replace(r0, r3, r12)
            int r3 = r4 + 1
            r2.replace(r4, r3, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r3.<init>(r5)
            int r0 = r0 - r7
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x0a2e:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r3 = 2
            r0.setMaxLines(r3)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            goto L_0x0e69
        L_0x0a56:
            r3 = 2
            if (r5 == r3) goto L_0x0b2c
            r3 = 4
            if (r5 != r3) goto L_0x0a5f
            r3 = 2
            goto L_0x0b2c
        L_0x0a5f:
            r2 = 1110704128(0x42340000, float:45.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r11.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r11.topMargin = r2
            r11.rightMargin = r8
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r7, r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r8)
            android.widget.TextView r2 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r2.setTypeface(r3)
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r3)
            int r2 = r1.currentAction
            if (r2 == 0) goto L_0x0af5
            r3 = 26
            if (r2 != r3) goto L_0x0a99
            goto L_0x0af5
        L_0x0a99:
            r3 = 27
            if (r2 != r3) goto L_0x0aac
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624865(0x7f0e03a1, float:1.8876922E38)
            java.lang.String r4 = "ChatsDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0b03
        L_0x0aac:
            int r2 = (int) r9
            if (r2 >= 0) goto L_0x0ae6
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r3.getChat(r2)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x0ad7
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0ad7
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624734(0x7f0e031e, float:1.8876656E38)
            java.lang.String r4 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0b03
        L_0x0ad7:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625763(0x7f0e0723, float:1.8878743E38)
            java.lang.String r4 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0b03
        L_0x0ae6:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624830(0x7f0e037e, float:1.887685E38)
            java.lang.String r4 = "ChatDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0b03
        L_0x0af5:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.String r4 = "HistoryClearedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x0b03:
            r2 = 0
        L_0x0b04:
            int r3 = r21.size()
            if (r2 >= r3) goto L_0x0e69
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Object r4 = r0.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            long r4 = r4.longValue()
            int r6 = r1.currentAction
            if (r6 == 0) goto L_0x0b25
            r9 = 26
            if (r6 != r9) goto L_0x0b23
            goto L_0x0b25
        L_0x0b23:
            r6 = 0
            goto L_0x0b26
        L_0x0b25:
            r6 = 1
        L_0x0b26:
            r3.addDialogAction(r4, r6)
            int r2 = r2 + 1
            goto L_0x0b04
        L_0x0b2c:
            if (r2 != r3) goto L_0x0b3d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624826(0x7f0e037a, float:1.8876843E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0b4b
        L_0x0b3d:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624864(0x7f0e03a0, float:1.887692E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0b4b:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.topMargin = r0
            r11.rightMargin = r8
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r2)
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
            r0.setAnimation(r2, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0e69
        L_0x0b8f:
            r14 = r19
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
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
            if (r2 == 0) goto L_0x0bd5
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625213(0x7f0e04fd, float:1.8877628E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165389(0x7var_cd, float:1.7944994E38)
            r0.setImageResource(r2)
            goto L_0x0c7d
        L_0x0bd5:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0bf2
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625080(0x7f0e0478, float:1.8877358E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0bef:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0c4b
        L_0x0bf2:
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
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r8)
            r3.setText(r2)
            goto L_0x0bef
        L_0x0CLASSNAME:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625212(0x7f0e04fc, float:1.8877626E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r8] = r0
            java.lang.String r5 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r6, r8)
            r2.setText(r3)
        L_0x0c4b:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r11.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r11.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r14.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.height = r0
        L_0x0c7d:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627536(0x7f0e0e10, float:1.888234E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0ccf
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
            goto L_0x0cdf
        L_0x0ccf:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0cdf:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r2
            r11.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.bottomMargin = r0
            r0 = -1
            r11.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            goto L_0x0e69
        L_0x0d06:
            r2 = 8
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r4
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r8)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0dd5
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r1.infoTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r7, r5)
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
            r4 = 2131627214(0x7f0e0cce, float:1.8881686E38)
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r5 = 28
            r6 = 28
            r2.setAnimation(r4, r5, r6)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0dae
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627216(0x7f0e0cd0, float:1.888169E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r8] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r6[r7] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
            goto L_0x0dc6
        L_0x0dae:
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627215(0x7f0e0ccf, float:1.8881688E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r4[r8] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0dc6:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            goto L_0x0e58
        L_0x0dd5:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
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
            r11.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627213(0x7f0e0ccd, float:1.8881684E38)
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
        L_0x0e58:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0e69:
            r0 = 0
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x145f
        L_0x0e6e:
            android.widget.ImageView r0 = r1.undoImageView
            r5 = 8
            r0.setVisibility(r5)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r5)
            r14 = -1
            int r0 = r1.currentAction
            r5 = 76
            r16 = 1091567616(0x41100000, float:9.0)
            if (r0 != r5) goto L_0x0eb1
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624610(0x7f0e02a2, float:1.8876405E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
        L_0x0eae:
            r0 = 1
            goto L_0x1421
        L_0x0eb1:
            r5 = 75
            if (r0 != r5) goto L_0x0ed9
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625742(0x7f0e070e, float:1.88787E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0eae
        L_0x0ed9:
            r5 = 70
            if (r2 != r5) goto L_0x0f5d
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r8)
            r2 = 2592000(0x278d00, float:3.632166E-39)
            if (r0 < r2) goto L_0x0efc
            r2 = 2592000(0x278d00, float:3.632166E-39)
            int r0 = r0 / r2
            java.lang.String r2 = "Months"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0var_
        L_0x0efc:
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x0f0c
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0var_
        L_0x0f0c:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x0var_
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0var_
        L_0x0var_:
            if (r0 < r13) goto L_0x0var_
            int r0 = r0 / r13
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0var_
        L_0x0var_:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0var_:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r8] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            r0 = 1
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1421
        L_0x0f5d:
            r2 = 71
            if (r0 != r2) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r0.setAnimation(r2, r6, r6)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1420
        L_0x0var_:
            r2 = 45
            if (r0 != r2) goto L_0x0fba
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625862(0x7f0e0786, float:1.8878944E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0eae
        L_0x0fba:
            r2 = 46
            if (r0 != r2) goto L_0x0fe3
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625863(0x7f0e0787, float:1.8878946E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0eae
        L_0x0fe3:
            r2 = 47
            if (r0 != r2) goto L_0x1017
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625885(0x7f0e079d, float:1.887899E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r0.setAnimation(r2, r6, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r11.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r7, r2)
            goto L_0x0eae
        L_0x1017:
            r2 = 1096810496(0x41600000, float:14.0)
            r5 = 51
            if (r0 != r5) goto L_0x1040
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624413(0x7f0e01dd, float:1.8876005E38)
            java.lang.String r4 = "AudioSpeedNormal"
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
            goto L_0x1420
        L_0x1040:
            r5 = 50
            if (r0 != r5) goto L_0x1067
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r0.setAnimation(r3, r6, r6)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1420
        L_0x1067:
            r5 = 52
            if (r0 == r5) goto L_0x1389
            r5 = 56
            if (r0 == r5) goto L_0x1389
            r5 = 57
            if (r0 == r5) goto L_0x1389
            r5 = 58
            if (r0 == r5) goto L_0x1389
            r5 = 59
            if (r0 == r5) goto L_0x1389
            if (r0 == r13) goto L_0x1389
            r5 = 80
            if (r0 != r5) goto L_0x1083
            goto L_0x1389
        L_0x1083:
            r5 = 54
            if (r0 != r5) goto L_0x10ac
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624770(0x7f0e0342, float:1.887673E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558481(0x7f0d0051, float:1.874228E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1420
        L_0x10ac:
            r5 = 55
            if (r0 != r5) goto L_0x10d5
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624769(0x7f0e0341, float:1.8876727E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558480(0x7f0d0050, float:1.8742277E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
            goto L_0x1420
        L_0x10d5:
            r5 = 41
            if (r0 != r5) goto L_0x1180
            if (r4 != 0) goto L_0x114f
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r3 = (long) r0
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x10fb
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1172
        L_0x10fb:
            int r0 = (int) r9
            if (r0 >= 0) goto L_0x1126
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r3.getChat(r0)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131625907(0x7f0e07b3, float:1.8879035E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r5[r8] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1172
        L_0x1126:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r3.getUser(r0)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131625909(0x7f0e07b5, float:1.887904E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r5[r8] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1172
        L_0x114f:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r9 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r9, r0)
            r5[r8] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x1172:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558422(0x7f0d0016, float:1.874216E38)
            r0.setAnimation(r3, r6, r6)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1420
        L_0x1180:
            r5 = 53
            if (r0 != r5) goto L_0x12c4
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x126b
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            int r3 = r3.clientUserId
            long r3 = (long) r3
            int r5 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x11cd
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x11af
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625713(0x7f0e06f1, float:1.8878642E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x11c1
        L_0x11af:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x11c1:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558478(0x7f0d004e, float:1.8742273E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x12be
        L_0x11cd:
            int r3 = (int) r9
            if (r3 >= 0) goto L_0x1217
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x11fe
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625712(0x7f0e06f0, float:1.887864E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r3 = r3.title
            r5[r8] = r3
            java.lang.String r3 = "FwdMessageToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1260
        L_0x11fe:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625716(0x7f0e06f4, float:1.8878648E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r3 = r3.title
            r5[r8] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1260
        L_0x1217:
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r4.getUser(r3)
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x1246
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r5[r8] = r3
            java.lang.String r3 = "FwdMessageToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1260
        L_0x1246:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625718(0x7f0e06f6, float:1.8878652E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r5[r8] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x1260:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x12bb
        L_0x126b:
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r0 = r0.intValue()
            if (r0 != r7) goto L_0x1295
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625711(0x7f0e06ef, float:1.8878638E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r6 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r3)
            r5[r8] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x12b1
        L_0x1295:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r6 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r3)
            r5[r8] = r3
            java.lang.String r3 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x12b1:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x12bb:
            r3 = 300(0x12c, double:1.48E-321)
            r14 = r3
        L_0x12be:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1420
        L_0x12c4:
            r5 = 61
            if (r0 != r5) goto L_0x1420
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1356
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r3 = (long) r0
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x12f8
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624545(0x7f0e0261, float:1.8876273E38)
            java.lang.String r4 = "BackgroundToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558478(0x7f0d004e, float:1.8742273E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x1383
        L_0x12f8:
            int r0 = (int) r9
            if (r0 >= 0) goto L_0x1323
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r3.getChat(r0)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624544(0x7f0e0260, float:1.887627E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r5[r8] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x134b
        L_0x1323:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r3.getUser(r0)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624546(0x7f0e0262, float:1.8876275E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r5[r8] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x134b:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x1383
        L_0x1356:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624543(0x7f0e025f, float:1.8876269E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r6 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r6, r0)
            r5[r8] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x1383:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1420
        L_0x1389:
            r3 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r4 = 80
            if (r0 != r4) goto L_0x13a0
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625315(0x7f0e0563, float:1.8877834E38)
            java.lang.String r5 = "EmailCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x140e
        L_0x13a0:
            if (r0 != r13) goto L_0x13b1
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626991(0x7f0e0bef, float:1.8881234E38)
            java.lang.String r5 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x140e
        L_0x13b1:
            r4 = 56
            if (r0 != r4) goto L_0x13c4
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131628115(0x7f0e1053, float:1.8883514E38)
            java.lang.String r5 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x140e
        L_0x13c4:
            r4 = 57
            if (r0 != r4) goto L_0x13d7
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625797(0x7f0e0745, float:1.8878812E38)
            java.lang.String r5 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x140e
        L_0x13d7:
            r4 = 52
            if (r0 != r4) goto L_0x13ea
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626206(0x7f0e08de, float:1.8879642E38)
            java.lang.String r5 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x140e
        L_0x13ea:
            r4 = 59
            if (r0 != r4) goto L_0x1400
            r3 = 2131558527(0x7f0d007f, float:1.8742372E38)
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.String r5 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x140e
        L_0x1400:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131627894(0x7f0e0var_, float:1.8883065E38)
            java.lang.String r5 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
        L_0x140e:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r7, r3)
        L_0x1420:
            r0 = 0
        L_0x1421:
            android.widget.TextView r3 = r1.subinfoTextView
            r4 = 8
            r3.setVisibility(r4)
            android.widget.TextView r3 = r1.undoTextView
            java.lang.String r5 = "undo_cancelColor"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setTextColor(r5)
            android.widget.LinearLayout r3 = r1.undoButton
            r3.setVisibility(r4)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.rightMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 0
            r3.setProgress(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r3.playAnimation()
            r3 = 0
            int r5 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x145f
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r4.<init>(r1)
            r3.postDelayed(r4, r14)
        L_0x145f:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.widget.TextView r4 = r1.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x148c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ". "
            r4.append(r5)
            android.widget.TextView r5 = r1.subinfoTextView
            java.lang.CharSequence r5 = r5.getText()
            r4.append(r5)
            java.lang.String r12 = r4.toString()
        L_0x148c:
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r3)
            boolean r3 = r20.isMultilineSubInfo()
            if (r3 == 0) goto L_0x14e1
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x14ac
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x14ac:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r8)
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
            goto L_0x1585
        L_0x14e1:
            boolean r3 = r20.hasSubInfo()
            if (r3 == 0) goto L_0x14f1
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x1585
        L_0x14f1:
            android.view.ViewParent r3 = r20.getParent()
            boolean r3 = r3 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x1585
            android.view.ViewParent r3 = r20.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = r3.getMeasuredWidth()
            int r5 = r3.getPaddingLeft()
            int r4 = r4 - r5
            int r3 = r3.getPaddingRight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x1513
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
        L_0x1513:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r3
            android.widget.TextView r3 = r1.infoTextView
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            r5 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r8)
            r9 = 0
            r21 = r20
            r22 = r3
            r23 = r4
            r24 = r5
            r25 = r6
            r26 = r9
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r3 = r1.infoTextView
            int r3 = r3.getMeasuredHeight()
            int r4 = r1.currentAction
            r5 = 16
            if (r4 == r5) goto L_0x154f
            r5 = 17
            if (r4 == r5) goto L_0x154f
            r5 = 18
            if (r4 != r5) goto L_0x154c
            goto L_0x154f
        L_0x154c:
            r13 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1551
        L_0x154f:
            r13 = 1096810496(0x41600000, float:14.0)
        L_0x1551:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r3 = r3 + r2
            r1.undoViewHeight = r3
            int r2 = r1.currentAction
            r4 = 18
            if (r2 != r4) goto L_0x156b
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x1585
        L_0x156b:
            r4 = 25
            if (r2 != r4) goto L_0x157c
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x1585
        L_0x157c:
            if (r0 == 0) goto L_0x1585
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r0
            r1.undoViewHeight = r3
        L_0x1585:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x15e9
            r1.setVisibility(r8)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x1595
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1597
        L_0x1595:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x1597:
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
            if (r4 == 0) goto L_0x15b5
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x15b7
        L_0x15b5:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x15b7:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r1.undoViewHeight
            int r5 = r5 + r6
            float r5 = (float) r5
            float r4 = r4 * r5
            r3[r8] = r4
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x15ca
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x15cc
        L_0x15ca:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x15cc:
            r3[r7] = r4
            java.lang.String r4 = "enterOffset"
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r1, r4, r3)
            r2[r8] = r3
            r0.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x15e9:
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
        if (i == 1 || i == 0 || i == 27 || i == 26) {
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
}
