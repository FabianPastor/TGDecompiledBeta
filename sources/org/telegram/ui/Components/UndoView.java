package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class UndoView extends FrameLayout {
    public static final int ACTION_ARCHIVE = 2;
    public static final int ACTION_ARCHIVE_FEW = 4;
    public static final int ACTION_ARCHIVE_FEW_HINT = 5;
    public static final int ACTION_ARCHIVE_HIDDEN = 6;
    public static final int ACTION_ARCHIVE_HINT = 3;
    public static final int ACTION_ARCHIVE_PINNED = 7;
    public static final int ACTION_CLEAR = 0;
    public static final int ACTION_CONTACT_ADDED = 8;
    public static final int ACTION_DELETE = 1;
    public static final int ACTION_OWNER_TRANSFERED_CHANNEL = 9;
    public static final int ACTION_OWNER_TRANSFERED_GROUP = 10;
    public static final int ACTION_QR_SESSION_ACCEPTED = 11;
    public static final int ACTION_QUIZ_CORRECT = 13;
    public static final int ACTION_QUIZ_INCORRECT = 14;
    public static final int ACTION_THEME_CHANGED = 12;
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

    /* Access modifiers changed, original: protected */
    public boolean canUndo() {
        return true;
    }

    public UndoView(Context context) {
        super(context);
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextSize(1, 15.0f);
        String str = "undo_infoColor";
        this.infoTextView.setTextColor(Theme.getColor(str));
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        this.subinfoTextView = new TextView(context);
        this.subinfoTextView.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor(str));
        String str2 = "undo_cancelColor";
        this.subinfoTextView.setLinkTextColor(Theme.getColor(str2));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        this.leftImageView = new RLottieImageView(context);
        this.leftImageView.setScaleType(ScaleType.CENTER);
        String str3 = "undo_background";
        this.leftImageView.setLayerColor("info1.**", Theme.getColor(str3) | -16777216);
        this.leftImageView.setLayerColor("info2.**", Theme.getColor(str3) | -16777216);
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luCLASSNAME.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc9.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc8.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc7.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc6.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc5.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc4.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc3.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc2.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("luc1.**", Theme.getColor(str));
        this.leftImageView.setLayerColor("Oval.**", Theme.getColor(str));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        this.undoButton = new LinearLayout(context);
        this.undoButton.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new -$$Lambda$UndoView$ut_O3jMsR3UxcWCuvOqjPzYJ4Go(this));
        this.undoImageView = new ImageView(context);
        this.undoImageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        this.undoTextView = new TextView(context);
        this.undoTextView.setTextSize(1, 14.0f);
        String str4 = "fonts/rmedium.ttf";
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface(str4));
        this.undoTextView.setTextColor(Theme.getColor(str2));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor(str));
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(str4));
        this.textPaint.setColor(Theme.getColor(str));
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(str3)));
        setOnTouchListener(-$$Lambda$UndoView$cqEu5tq8BrTwZKHKJIY3aTuNIjk.INSTANCE);
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
        return i == 11 || i == 6 || i == 3 || i == 5 || i == 7 || i == 13 || i == 14;
    }

    public boolean isMultilineSubInfo() {
        return this.currentAction == 12;
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
            runnable = this.currentCancelRunnable;
            if (runnable != null) {
                if (!z) {
                    runnable.run();
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
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(250);
                } else {
                    r7 = new Animator[3];
                    r7[0] = ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.8f});
                    r7[1] = ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.8f});
                    r7[2] = ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(r7);
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
        showWithAction(j, i, null, runnable, null);
    }

    public void showWithAction(long j, int i, Object obj) {
        showWithAction(j, i, obj, null, null);
    }

    public void showWithAction(long j, int i, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, null, runnable, runnable2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0134  */
    public void showWithAction(long r18, int r20, java.lang.Object r21, java.lang.Runnable r22, java.lang.Runnable r23) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r3 = r20;
        r4 = r21;
        r5 = r0.currentActionRunnable;
        if (r5 == 0) goto L_0x000f;
    L_0x000c:
        r5.run();
    L_0x000f:
        r5 = 1;
        r0.isShown = r5;
        r6 = r22;
        r0.currentActionRunnable = r6;
        r6 = r23;
        r0.currentCancelRunnable = r6;
        r0.currentDialogId = r1;
        r0.currentAction = r3;
        r6 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r0.timeLeft = r6;
        r0.currentInfoObject = r4;
        r6 = android.os.SystemClock.uptimeMillis();
        r0.lastUpdateTime = r6;
        r6 = r17.isTooltipAction();
        r7 = "fonts/rmedium.ttf";
        r8 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r9 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r10 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r11 = "";
        r12 = 0;
        r13 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r16 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        r14 = 8;
        r15 = 0;
        if (r6 == 0) goto L_0x019c;
    L_0x0042:
        r1 = 9;
        if (r3 == r1) goto L_0x00f2;
    L_0x0046:
        r2 = 10;
        if (r3 != r2) goto L_0x004c;
    L_0x004a:
        goto L_0x00f2;
    L_0x004c:
        if (r3 != r14) goto L_0x0064;
    L_0x004e:
        r1 = r4;
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r2 = NUM; // 0x7f0e0766 float:1.887888E38 double:1.0531630924E-314;
        r3 = new java.lang.Object[r5];
        r1 = org.telegram.messenger.UserObject.getFirstName(r1);
        r3[r15] = r1;
        r1 = "NowInContacts";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3);
        goto L_0x0122;
    L_0x0064:
        r1 = 6;
        if (r3 != r1) goto L_0x0080;
    L_0x0067:
        r1 = NUM; // 0x7f0e0101 float:1.8875559E38 double:1.0531622836E-314;
        r2 = "ArchiveHidden";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = NUM; // 0x7f0e0102 float:1.887556E38 double:1.053162284E-314;
        r3 = "ArchiveHiddenInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = NUM; // 0x7f0d0005 float:1.8742125E38 double:1.05312978E-314;
        r4 = 48;
        goto L_0x0128;
    L_0x0080:
        r1 = r0.currentAction;
        r2 = 13;
        if (r1 != r2) goto L_0x009f;
    L_0x0086:
        r1 = NUM; // 0x7f0e0941 float:1.8879842E38 double:1.053163327E-314;
        r2 = "QuizWellDone";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = NUM; // 0x7f0e0942 float:1.8879844E38 double:1.0531633276E-314;
        r3 = "QuizWellDoneInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = NUM; // 0x7f0d0013 float:1.8742153E38 double:1.053129787E-314;
    L_0x009b:
        r4 = 44;
        goto L_0x0128;
    L_0x009f:
        r2 = 14;
        if (r1 != r2) goto L_0x00b9;
    L_0x00a3:
        r1 = NUM; // 0x7f0e0943 float:1.8879846E38 double:1.053163328E-314;
        r2 = "QuizWrongAnswer";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = NUM; // 0x7f0e0944 float:1.8879849E38 double:1.0531633286E-314;
        r3 = "QuizWrongAnswerInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = NUM; // 0x7f0d001b float:1.874217E38 double:1.053129791E-314;
        goto L_0x009b;
    L_0x00b9:
        r1 = 7;
        if (r3 != r1) goto L_0x00d2;
    L_0x00bc:
        r1 = NUM; // 0x7f0e0109 float:1.8875575E38 double:1.0531622876E-314;
        r2 = "ArchivePinned";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = NUM; // 0x7f0e010a float:1.8875577E38 double:1.053162288E-314;
        r3 = "ArchivePinnedInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
    L_0x00ce:
        r3 = NUM; // 0x7f0d0004 float:1.8742123E38 double:1.0531297795E-314;
        goto L_0x0126;
    L_0x00d2:
        r1 = 3;
        if (r3 != r1) goto L_0x00df;
    L_0x00d5:
        r1 = NUM; // 0x7f0e02a0 float:1.88764E38 double:1.0531624886E-314;
        r2 = "ChatArchived";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        goto L_0x00e8;
    L_0x00df:
        r1 = NUM; // 0x7f0e02c1 float:1.8876467E38 double:1.053162505E-314;
        r2 = "ChatsArchived";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
    L_0x00e8:
        r2 = NUM; // 0x7f0e02a1 float:1.8876403E38 double:1.053162489E-314;
        r3 = "ChatArchivedInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        goto L_0x00ce;
    L_0x00f2:
        r2 = r4;
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        if (r3 != r1) goto L_0x010d;
    L_0x00f7:
        r1 = NUM; // 0x7f0e0400 float:1.8877114E38 double:1.0531626626E-314;
        r3 = new java.lang.Object[r5];
        r2 = org.telegram.messenger.UserObject.getFirstName(r2);
        r3[r15] = r2;
        r2 = "EditAdminTransferChannelToast";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        goto L_0x0122;
    L_0x010d:
        r1 = NUM; // 0x7f0e0401 float:1.8877116E38 double:1.053162663E-314;
        r3 = new java.lang.Object[r5];
        r2 = org.telegram.messenger.UserObject.getFirstName(r2);
        r3[r15] = r2;
        r2 = "EditAdminTransferGroupToast";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
    L_0x0122:
        r2 = 0;
        r3 = NUM; // 0x7f0d0008 float:1.874213E38 double:1.0531297815E-314;
    L_0x0126:
        r4 = 36;
    L_0x0128:
        r6 = r0.infoTextView;
        r6.setText(r1);
        r1 = r0.leftImageView;
        r1.setAnimation(r3, r4, r4);
        if (r2 == 0) goto L_0x0161;
    L_0x0134:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.leftMargin = r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.topMargin = r3;
        r1 = r0.subinfoTextView;
        r1.setText(r2);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r15);
        r1 = r0.infoTextView;
        r1.setTextSize(r5, r8);
        r1 = r0.infoTextView;
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r7);
        r1.setTypeface(r2);
        goto L_0x0186;
    L_0x0161:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.leftMargin = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.topMargin = r2;
        r1 = r0.subinfoTextView;
        r1.setVisibility(r14);
        r1 = r0.infoTextView;
        r1.setTextSize(r5, r9);
        r1 = r0.infoTextView;
        r2 = android.graphics.Typeface.DEFAULT;
        r1.setTypeface(r2);
    L_0x0186:
        r1 = r0.undoButton;
        r1.setVisibility(r14);
        r1 = r0.leftImageView;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setProgress(r12);
        r1 = r0.leftImageView;
        r1.playAnimation();
        goto L_0x03c4;
    L_0x019c:
        r6 = r0.currentAction;
        r9 = 11;
        if (r6 != r9) goto L_0x0212;
    L_0x01a2:
        r1 = r4;
        r1 = (org.telegram.tgnet.TLRPC.TL_authorization) r1;
        r2 = r0.infoTextView;
        r3 = NUM; // 0x7f0e016e float:1.887578E38 double:1.0531623375E-314;
        r4 = "AuthAnotherClientOk";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2.setText(r3);
        r2 = r0.leftImageView;
        r3 = NUM; // 0x7f0d0008 float:1.874213E38 double:1.0531297815E-314;
        r4 = 36;
        r2.setAnimation(r3, r4, r4);
        r2 = r0.infoTextView;
        r2 = r2.getLayoutParams();
        r2 = (android.widget.FrameLayout.LayoutParams) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2.leftMargin = r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r2.topMargin = r3;
        r2 = r0.subinfoTextView;
        r1 = r1.app_name;
        r2.setText(r1);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r15);
        r1 = r0.infoTextView;
        r1.setTextSize(r5, r8);
        r1 = r0.infoTextView;
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r7);
        r1.setTypeface(r2);
        r1 = r0.undoTextView;
        r2 = "windowBackgroundWhiteRedText2";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r0.undoImageView;
        r1.setVisibility(r14);
        r1 = r0.undoButton;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setProgress(r12);
        r1 = r0.leftImageView;
        r1.playAnimation();
        goto L_0x03c4;
    L_0x0212:
        r7 = 12;
        if (r6 != r7) goto L_0x02b8;
    L_0x0216:
        r1 = r4;
        r1 = (org.telegram.tgnet.TLRPC.TL_authorization) r1;
        r1 = r0.infoTextView;
        r2 = NUM; // 0x7f0e0316 float:1.887664E38 double:1.053162547E-314;
        r3 = "ColorThemeChanged";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
        r1 = r0.leftImageView;
        r2 = NUM; // 0x7var_d8 float:1.7946055E38 double:1.0529358627E-314;
        r1.setImageResource(r2);
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.leftMargin = r2;
        r2 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.rightMargin = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.topMargin = r2;
        r1 = r0.subinfoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.rightMargin = r2;
        r1 = NUM; // 0x7f0e0317 float:1.8876642E38 double:1.0531625474E-314;
        r2 = "ColorThemeChangedInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = new android.text.SpannableStringBuilder;
        r2.<init>(r1);
        r3 = 42;
        r3 = r1.indexOf(r3);
        r4 = 42;
        r1 = r1.lastIndexOf(r4);
        if (r3 < 0) goto L_0x0292;
    L_0x0277:
        if (r1 < 0) goto L_0x0292;
    L_0x0279:
        if (r3 == r1) goto L_0x0292;
    L_0x027b:
        r4 = r1 + 1;
        r2.replace(r1, r4, r11);
        r4 = r3 + 1;
        r2.replace(r3, r4, r11);
        r4 = new org.telegram.ui.Components.URLSpanNoUnderline;
        r6 = "tg://settings/themes";
        r4.<init>(r6);
        r1 = r1 - r5;
        r6 = 33;
        r2.setSpan(r4, r3, r1, r6);
    L_0x0292:
        r1 = r0.subinfoTextView;
        r1.setText(r2);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r15);
        r1 = r0.subinfoTextView;
        r1.setSingleLine(r15);
        r1 = r0.subinfoTextView;
        r4 = 2;
        r1.setMaxLines(r4);
        r1 = r0.undoTextView;
        r1.setVisibility(r14);
        r1 = r0.undoButton;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setVisibility(r15);
        goto L_0x03c4;
    L_0x02b8:
        r4 = 2;
        if (r6 == r4) goto L_0x035f;
    L_0x02bb:
        r4 = 4;
        if (r6 != r4) goto L_0x02c0;
    L_0x02be:
        goto L_0x035f;
    L_0x02c0:
        r3 = r0.infoTextView;
        r3 = r3.getLayoutParams();
        r3 = (android.widget.FrameLayout.LayoutParams) r3;
        r4 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3.leftMargin = r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r3.topMargin = r4;
        r3 = r0.infoTextView;
        r4 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r3.setTextSize(r5, r4);
        r3 = r0.undoButton;
        r3.setVisibility(r15);
        r3 = r0.infoTextView;
        r4 = android.graphics.Typeface.DEFAULT;
        r3.setTypeface(r4);
        r3 = r0.subinfoTextView;
        r3.setVisibility(r14);
        r3 = r0.leftImageView;
        r3.setVisibility(r14);
        r3 = r0.currentAction;
        if (r3 != 0) goto L_0x0306;
    L_0x02f7:
        r3 = r0.infoTextView;
        r4 = NUM; // 0x7f0e055b float:1.8877818E38 double:1.053162834E-314;
        r6 = "HistoryClearedUndo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        goto L_0x034e;
    L_0x0306:
        r3 = (int) r1;
        if (r3 >= 0) goto L_0x0340;
    L_0x0309:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r4.getChat(r3);
        r4 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r4 == 0) goto L_0x0331;
    L_0x031e:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x0331;
    L_0x0322:
        r3 = r0.infoTextView;
        r4 = NUM; // 0x7f0e0252 float:1.8876242E38 double:1.05316245E-314;
        r6 = "ChannelDeletedUndo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        goto L_0x034e;
    L_0x0331:
        r3 = r0.infoTextView;
        r4 = NUM; // 0x7f0e0534 float:1.887774E38 double:1.0531628147E-314;
        r6 = "GroupDeletedUndo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        goto L_0x034e;
    L_0x0340:
        r3 = r0.infoTextView;
        r4 = NUM; // 0x7f0e02a4 float:1.8876409E38 double:1.0531624906E-314;
        r6 = "ChatDeletedUndo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
    L_0x034e:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r4 = r0.currentAction;
        if (r4 != 0) goto L_0x035a;
    L_0x0358:
        r4 = 1;
        goto L_0x035b;
    L_0x035a:
        r4 = 0;
    L_0x035b:
        r3.addDialogAction(r1, r4);
        goto L_0x03c4;
    L_0x035f:
        r1 = 2;
        if (r3 != r1) goto L_0x0371;
    L_0x0362:
        r1 = r0.infoTextView;
        r2 = NUM; // 0x7f0e02a0 float:1.88764E38 double:1.0531624886E-314;
        r3 = "ChatArchived";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
        goto L_0x037f;
    L_0x0371:
        r1 = r0.infoTextView;
        r2 = NUM; // 0x7f0e02c1 float:1.8876467E38 double:1.053162505E-314;
        r3 = "ChatsArchived";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
    L_0x037f:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.leftMargin = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.topMargin = r2;
        r1 = r0.infoTextView;
        r2 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r1.setTextSize(r5, r2);
        r1 = r0.undoButton;
        r1.setVisibility(r15);
        r1 = r0.infoTextView;
        r2 = android.graphics.Typeface.DEFAULT;
        r1.setTypeface(r2);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r14);
        r1 = r0.leftImageView;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r2 = NUM; // 0x7f0d0002 float:1.8742119E38 double:1.0531297785E-314;
        r3 = 36;
        r1.setAnimation(r2, r3, r3);
        r1 = r0.leftImageView;
        r1.setProgress(r12);
        r1 = r0.leftImageView;
        r1.playAnimation();
    L_0x03c4:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r0.infoTextView;
        r2 = r2.getText();
        r1.append(r2);
        r2 = r0.subinfoTextView;
        r2 = r2.getVisibility();
        if (r2 != 0) goto L_0x03f1;
    L_0x03da:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = ". ";
        r2.append(r3);
        r3 = r0.subinfoTextView;
        r3 = r3.getText();
        r2.append(r3);
        r11 = r2.toString();
    L_0x03f1:
        r1.append(r11);
        r1 = r1.toString();
        org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1);
        r1 = r17.isMultilineSubInfo();
        if (r1 == 0) goto L_0x0445;
    L_0x0401:
        r1 = r17.getParent();
        r1 = (android.view.ViewGroup) r1;
        r1 = r1.getMeasuredWidth();
        if (r1 != 0) goto L_0x0411;
    L_0x040d:
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r1 = r1.x;
    L_0x0411:
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 - r2;
        r2 = r0.subinfoTextView;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3);
        r3 = 0;
        r4 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r15);
        r6 = 0;
        r18 = r17;
        r19 = r2;
        r20 = r1;
        r21 = r3;
        r22 = r4;
        r23 = r6;
        r18.measureChildWithMargins(r19, r20, r21, r22, r23);
        r1 = r0.subinfoTextView;
        r1 = r1.getMeasuredHeight();
        r2 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r0.undoViewHeight = r1;
        goto L_0x0498;
    L_0x0445:
        r1 = r17.hasSubInfo();
        if (r1 == 0) goto L_0x0454;
    L_0x044b:
        r1 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.undoViewHeight = r1;
        goto L_0x0498;
    L_0x0454:
        r1 = r17.getParent();
        r1 = r1 instanceof android.view.ViewGroup;
        if (r1 == 0) goto L_0x0498;
    L_0x045c:
        r1 = r17.getParent();
        r1 = (android.view.ViewGroup) r1;
        r1 = r1.getMeasuredWidth();
        if (r1 != 0) goto L_0x046c;
    L_0x0468:
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r1 = r1.x;
    L_0x046c:
        r2 = r0.infoTextView;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3);
        r3 = 0;
        r4 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r15);
        r6 = 0;
        r18 = r17;
        r19 = r2;
        r20 = r1;
        r21 = r3;
        r22 = r4;
        r23 = r6;
        r18.measureChildWithMargins(r19, r20, r21, r22, r23);
        r1 = r0.infoTextView;
        r1 = r1.getMeasuredHeight();
        r2 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r0.undoViewHeight = r1;
    L_0x0498:
        r1 = r17.getVisibility();
        if (r1 == 0) goto L_0x04e4;
    L_0x049e:
        r0.setVisibility(r15);
        r1 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r0.undoViewHeight;
        r1 = r1 + r2;
        r1 = (float) r1;
        r0.setTranslationY(r1);
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r2 = new android.animation.Animator[r5];
        r3 = android.view.View.TRANSLATION_Y;
        r4 = 2;
        r4 = new float[r4];
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = r0.undoViewHeight;
        r6 = r6 + r7;
        r6 = (float) r6;
        r4[r15] = r6;
        r6 = r0.additionalTranslationY;
        r6 = -r6;
        r4[r5] = r6;
        r3 = android.animation.ObjectAnimator.ofFloat(r0, r3, r4);
        r2[r15] = r3;
        r1.playTogether(r2);
        r2 = new android.view.animation.DecelerateInterpolator;
        r2.<init>();
        r1.setInterpolator(r2);
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r1.setDuration(r2);
        r1.start();
    L_0x04e4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(this.undoViewHeight, NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        long j;
        int i = this.currentAction;
        if (i == 1 || i == 0) {
            j = this.timeLeft;
            int ceil = j > 0 ? (int) Math.ceil((double) (((float) j) / 1000.0f)) : 0;
            if (this.prevSeconds != ceil) {
                this.prevSeconds = ceil;
                this.timeLeftString = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ceil))});
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(this.timeLeftString));
            }
            canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        j = SystemClock.uptimeMillis();
        this.timeLeft -= j - this.lastUpdateTime;
        this.lastUpdateTime = j;
        if (this.timeLeft <= 0) {
            hide(true, 1);
        }
        invalidate();
    }
}
