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
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class UndoView extends FrameLayout {
    private float additionalTranslationY;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private Object currentInfoObject;
    private TextView infoTextView;
    private boolean isShown;
    private long lastUpdateTime;
    private RLottieImageView leftImageView;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    private long timeLeft;
    private String timeLeftString;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canUndo() {
        return true;
    }

    public UndoView(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(Theme.getColor("undo_infoColor"));
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
        setOnTouchListener($$Lambda$UndoView$cqEu5tq8BrTwZKHKJIY3aTuNIjk.INSTANCE);
        setVisibility(4);
    }

    public /* synthetic */ void lambda$new$0$UndoView(View view) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty());
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15;
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
            }
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)})});
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
            setTranslationY((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
            setVisibility(4);
        }
    }

    public void showWithAction(long j, int i, Runnable runnable) {
        showWithAction(j, i, (Object) null, runnable, (Runnable) null);
    }

    public void showWithAction(long j, int i, Object obj) {
        showWithAction(j, i, obj, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long j, int i, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, (Object) null, runnable, runnable2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x019a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r19, int r21, java.lang.Object r22, java.lang.Runnable r23, java.lang.Runnable r24) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r3 = r21
            r4 = r22
            java.lang.Runnable r5 = r0.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r0.isShown = r5
            r6 = r23
            r0.currentActionRunnable = r6
            r6 = r24
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0.lastUpdateTime = r6
            android.widget.TextView r6 = r0.undoTextView
            r7 = 2131627034(0x7f0e0c1a, float:1.8881321E38)
            java.lang.String r8 = "Undo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r7.toUpperCase()
            r6.setText(r7)
            android.widget.ImageView r6 = r0.undoImageView
            r7 = 0
            r6.setVisibility(r7)
            boolean r6 = r18.isTooltipAction()
            r8 = 2131624626(0x7f0e02b2, float:1.8876437E38)
            java.lang.String r9 = "ChatArchived"
            r10 = 2131624659(0x7f0e02d3, float:1.8876504E38)
            java.lang.String r11 = "ChatsArchived"
            java.lang.String r12 = "fonts/rmedium.ttf"
            r13 = 1096810496(0x41600000, float:14.0)
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r14 = ""
            r17 = 1114112000(0x42680000, float:58.0)
            r15 = 8
            if (r6 == 0) goto L_0x01dc
            r1 = 9
            r2 = 0
            if (r3 == r1) goto L_0x0121
            r6 = 10
            if (r3 != r6) goto L_0x0069
            goto L_0x0121
        L_0x0069:
            if (r3 != r15) goto L_0x0081
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r3 = 2131625986(0x7f0e0802, float:1.8879195E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            goto L_0x0150
        L_0x0081:
            r1 = 6
            if (r3 != r1) goto L_0x009d
            r1 = 2131624203(0x7f0e010b, float:1.887558E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624204(0x7f0e010c, float:1.8875581E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r4 = 48
            goto L_0x0155
        L_0x009d:
            int r1 = r0.currentAction
            r4 = 13
            if (r1 != r4) goto L_0x00bc
            r1 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558432(0x7f0d0020, float:1.874218E38)
        L_0x00b8:
            r4 = 44
            goto L_0x0155
        L_0x00bc:
            r4 = 14
            if (r1 != r4) goto L_0x00d6
            r1 = 2131626466(0x7f0e09e2, float:1.888017E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558440(0x7f0d0028, float:1.8742196E38)
            goto L_0x00b8
        L_0x00d6:
            r1 = 7
            if (r3 != r1) goto L_0x00fd
            r1 = 2131624211(0x7f0e0113, float:1.8875595E38)
            java.lang.String r3 = "ArchivePinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x00f9
            r2 = 2131624212(0x7f0e0114, float:1.8875597E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x00f9:
            r3 = 2131558406(0x7f0d0006, float:1.8742127E38)
            goto L_0x0153
        L_0x00fd:
            r1 = 3
            if (r3 != r1) goto L_0x0105
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x0109
        L_0x0105:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r10)
        L_0x0109:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x00f9
            r2 = 2131624627(0x7f0e02b3, float:1.887644E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x00f9
        L_0x0121:
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            if (r3 != r1) goto L_0x013b
            r1 = 2131625002(0x7f0e042a, float:1.88772E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r3[r7] = r4
            java.lang.String r4 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0150
        L_0x013b:
            r1 = 2131625003(0x7f0e042b, float:1.8877202E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r3[r7] = r4
            java.lang.String r4 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0150:
            r3 = 2131558410(0x7f0d000a, float:1.8742135E38)
        L_0x0153:
            r4 = 36
        L_0x0155:
            android.widget.TextView r6 = r0.infoTextView
            r6.setText(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r3, r4, r4)
            if (r2 == 0) goto L_0x019a
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.topMargin = r3
            r1.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r1.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r13)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r1.setTypeface(r2)
            goto L_0x01c5
        L_0x019a:
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.topMargin = r2
            r1.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r15)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
        L_0x01c5:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x04da
        L_0x01dc:
            int r6 = r0.currentAction
            r10 = 11
            if (r6 != r10) goto L_0x0253
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624314(0x7f0e017a, float:1.8875804E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 2131558410(0x7f0d000a, float:1.8742135E38)
            r4 = 36
            r2.setAnimation(r3, r4, r4)
            android.widget.TextView r2 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r2.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r2.topMargin = r3
            android.widget.TextView r2 = r0.subinfoTextView
            java.lang.String r1 = r1.app_name
            r2.setText(r1)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r13)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r15)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x04da
        L_0x0253:
            r10 = 15
            r12 = 42
            if (r6 != r10) goto L_0x032c
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626002(0x7f0e0812, float:1.8879228E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625200(0x7f0e04f0, float:1.8877601E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            android.widget.TextView r1 = r0.undoTextView
            android.text.TextPaint r1 = r1.getPaint()
            android.widget.TextView r2 = r0.undoTextView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r1 = r1.measureText(r2)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            android.widget.TextView r2 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r2.leftMargin = r3
            r2.rightMargin = r1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r2.topMargin = r3
            android.widget.TextView r2 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r1
            r1 = 2131625199(0x7f0e04ef, float:1.88776E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r12)
            int r1 = r1.lastIndexOf(r12)
            if (r3 < 0) goto L_0x02fb
            if (r1 < 0) goto L_0x02fb
            if (r3 == r1) goto L_0x02fb
            int r4 = r1 + 1
            r2.replace(r1, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
        L_0x02fb:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r2 = 2
            r1.setMaxLines(r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x04da
        L_0x032c:
            r10 = 12
            if (r6 != r10) goto L_0x03cc
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165939(0x7var_f3, float:1.794611E38)
            r1.setImageResource(r2)
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.leftMargin = r2
            r2 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.rightMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.topMargin = r3
            android.widget.TextView r1 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.rightMargin = r2
            r1 = 2131624749(0x7f0e032d, float:1.8876687E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r12)
            int r1 = r1.lastIndexOf(r12)
            if (r3 < 0) goto L_0x03a6
            if (r1 < 0) goto L_0x03a6
            if (r3 == r1) goto L_0x03a6
            int r4 = r1 + 1
            r2.replace(r1, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
        L_0x03a6:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r4 = 2
            r1.setMaxLines(r4)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r15)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x04da
        L_0x03cc:
            r4 = 2
            if (r6 == r4) goto L_0x0477
            r4 = 4
            if (r6 != r4) goto L_0x03d4
            goto L_0x0477
        L_0x03d4:
            android.widget.TextView r3 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r4 = 1110704128(0x42340000, float:45.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.leftMargin = r4
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.topMargin = r4
            r3.rightMargin = r7
            android.widget.TextView r3 = r0.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r3.setTextSize(r5, r4)
            android.widget.LinearLayout r3 = r0.undoButton
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r15)
            int r3 = r0.currentAction
            if (r3 != 0) goto L_0x041e
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625425(0x7f0e05d1, float:1.8878058E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0466
        L_0x041e:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x0458
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x0449
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0449
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624546(0x7f0e0262, float:1.8876275E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0466
        L_0x0449:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625385(0x7f0e05a9, float:1.8877976E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0466
        L_0x0458:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624630(0x7f0e02b6, float:1.8876445E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x0466:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x0472
            r4 = 1
            goto L_0x0473
        L_0x0472:
            r4 = 0
        L_0x0473:
            r3.addDialogAction(r1, r4)
            goto L_0x04da
        L_0x0477:
            r1 = 2
            if (r3 != r1) goto L_0x0484
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r1.setText(r2)
            goto L_0x0490
        L_0x0484:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624659(0x7f0e02d3, float:1.8876504E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)
            r1.setText(r2)
        L_0x0490:
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.topMargin = r2
            r1.rightMargin = r7
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558404(0x7f0d0004, float:1.8742123E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
        L_0x04da:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r2 = r0.infoTextView
            java.lang.CharSequence r2 = r2.getText()
            r1.append(r2)
            android.widget.TextView r2 = r0.subinfoTextView
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x0507
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = ". "
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            java.lang.String r14 = r2.toString()
        L_0x0507:
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r18.isMultilineSubInfo()
            if (r1 == 0) goto L_0x055b
            android.view.ViewParent r1 = r18.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0527
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0527:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r19 = r18
            r20 = r2
            r21 = r1
            r22 = r3
            r23 = r4
            r24 = r6
            r19.measureChildWithMargins(r20, r21, r22, r23, r24)
            android.widget.TextView r1 = r0.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            goto L_0x05ae
        L_0x055b:
            boolean r1 = r18.hasSubInfo()
            if (r1 == 0) goto L_0x056a
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x05ae
        L_0x056a:
            android.view.ViewParent r1 = r18.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x05ae
            android.view.ViewParent r1 = r18.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0582
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0582:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r19 = r18
            r20 = r2
            r21 = r1
            r22 = r3
            r23 = r4
            r24 = r6
            r19.measureChildWithMargins(r20, r21, r22, r23, r24)
            android.widget.TextView r1 = r0.infoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1105199104(0x41e00000, float:28.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
        L_0x05ae:
            int r1 = r18.getVisibility()
            if (r1 == 0) goto L_0x05fa
            r0.setVisibility(r7)
            r1 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r0.undoViewHeight
            int r1 = r1 + r2
            float r1 = (float) r1
            r0.setTranslationY(r1)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r5]
            android.util.Property r3 = android.view.View.TRANSLATION_Y
            r4 = 2
            float[] r4 = new float[r4]
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r0.undoViewHeight
            int r6 = r6 + r8
            float r6 = (float) r6
            r4[r7] = r6
            float r6 = r0.additionalTranslationY
            float r6 = -r6
            r4[r5] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r3, r4)
            r2[r7] = r3
            r1.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r1.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r1.setDuration(r2)
            r1.start()
        L_0x05fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
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
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(format));
            }
            canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = this.timeLeft - (elapsedRealtime - this.lastUpdateTime);
        this.timeLeft = j2;
        this.lastUpdateTime = elapsedRealtime;
        if (j2 <= 0) {
            hide(true, 1);
        }
        invalidate();
    }
}
