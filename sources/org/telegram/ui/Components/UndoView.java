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
    private float additionalTranslationY;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private TextView infoTextView;
    private boolean isShowed;
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
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TruncateAt.END);
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        this.leftImageView = new RLottieImageView(context);
        this.leftImageView.setScaleType(ScaleType.CENTER);
        String str2 = "undo_background";
        this.leftImageView.setLayerColor("info1.**", Theme.getColor(str2) | -16777216);
        this.leftImageView.setLayerColor("info2.**", Theme.getColor(str2) | -16777216);
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
        String str3 = "undo_cancelColor";
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        this.undoTextView = new TextView(context);
        this.undoTextView.setTextSize(1, 14.0f);
        String str4 = "fonts/rmedium.ttf";
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface(str4));
        this.undoTextView.setTextColor(Theme.getColor(str3));
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
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(str2)));
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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7;
    }

    public void setAdditionalTranslationY(float f) {
        this.additionalTranslationY = f;
    }

    public void hide(boolean z, int i) {
        if (getVisibility() == 0 && this.isShowed) {
            this.isShowed = false;
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

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0123  */
    public void showWithAction(long r17, int r19, java.lang.Object r20, java.lang.Runnable r21, java.lang.Runnable r22) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r3 = r19;
        r4 = r0.currentActionRunnable;
        if (r4 == 0) goto L_0x000d;
    L_0x000a:
        r4.run();
    L_0x000d:
        r4 = 1;
        r0.isShowed = r4;
        r5 = r21;
        r0.currentActionRunnable = r5;
        r5 = r22;
        r0.currentCancelRunnable = r5;
        r0.currentDialogId = r1;
        r0.currentAction = r3;
        r5 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r0.timeLeft = r5;
        r5 = android.os.SystemClock.uptimeMillis();
        r0.lastUpdateTime = r5;
        r5 = r16.isTooltipAction();
        r6 = 0;
        r7 = NUM; // 0x7f0e0281 float:1.8876338E38 double:1.0531624733E-314;
        r8 = "ChatArchived";
        r9 = NUM; // 0x7f0e02a2 float:1.8876405E38 double:1.0531624896E-314;
        r10 = "ChatsArchived";
        r11 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        r12 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r13 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r14 = 8;
        r15 = 0;
        if (r5 == 0) goto L_0x015e;
    L_0x0040:
        r1 = 0;
        r2 = 9;
        if (r3 == r2) goto L_0x00ae;
    L_0x0045:
        r5 = 10;
        if (r3 != r5) goto L_0x004a;
    L_0x0049:
        goto L_0x00ae;
    L_0x004a:
        if (r3 != r14) goto L_0x0063;
    L_0x004c:
        r2 = r20;
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        r3 = NUM; // 0x7f0e070c float:1.8878696E38 double:1.053163048E-314;
        r5 = new java.lang.Object[r4];
        r2 = org.telegram.messenger.UserObject.getFirstName(r2);
        r5[r15] = r2;
        r2 = "NowInContacts";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r5);
        goto L_0x00df;
    L_0x0063:
        r1 = 6;
        if (r3 != r1) goto L_0x007e;
    L_0x0066:
        r1 = NUM; // 0x7f0e00f9 float:1.8875543E38 double:1.0531622797E-314;
        r2 = "ArchiveHidden";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r1 = NUM; // 0x7f0e00fa float:1.8875545E38 double:1.05316228E-314;
        r3 = "ArchiveHiddenInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = NUM; // 0x7f0d0005 float:1.8742125E38 double:1.05312978E-314;
        r5 = 48;
        goto L_0x00e4;
    L_0x007e:
        r1 = 7;
        if (r3 != r1) goto L_0x0097;
    L_0x0081:
        r1 = NUM; // 0x7f0e0101 float:1.8875559E38 double:1.0531622836E-314;
        r2 = "ArchivePinned";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r1 = NUM; // 0x7f0e0102 float:1.887556E38 double:1.053162284E-314;
        r3 = "ArchivePinnedInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
    L_0x0093:
        r3 = NUM; // 0x7f0d0004 float:1.8742123E38 double:1.0531297795E-314;
        goto L_0x00e2;
    L_0x0097:
        r1 = 3;
        if (r3 != r1) goto L_0x009f;
    L_0x009a:
        r1 = org.telegram.messenger.LocaleController.getString(r8, r7);
        goto L_0x00a3;
    L_0x009f:
        r1 = org.telegram.messenger.LocaleController.getString(r10, r9);
    L_0x00a3:
        r2 = r1;
        r1 = NUM; // 0x7f0e0282 float:1.887634E38 double:1.053162474E-314;
        r3 = "ChatArchivedInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        goto L_0x0093;
    L_0x00ae:
        r5 = r20;
        r5 = (org.telegram.tgnet.TLRPC.User) r5;
        if (r3 != r2) goto L_0x00ca;
    L_0x00b4:
        r2 = NUM; // 0x7f0e03c6 float:1.8876997E38 double:1.053162634E-314;
        r3 = new java.lang.Object[r4];
        r5 = org.telegram.messenger.UserObject.getFirstName(r5);
        r3[r15] = r5;
        r5 = "EditAdminTransferChannelToast";
        r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r3);
        r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2);
        goto L_0x00df;
    L_0x00ca:
        r2 = NUM; // 0x7f0e03c7 float:1.8876999E38 double:1.0531626344E-314;
        r3 = new java.lang.Object[r4];
        r5 = org.telegram.messenger.UserObject.getFirstName(r5);
        r3[r15] = r5;
        r5 = "EditAdminTransferGroupToast";
        r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r3);
        r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2);
    L_0x00df:
        r3 = NUM; // 0x7f0d0008 float:1.874213E38 double:1.0531297815E-314;
    L_0x00e2:
        r5 = 36;
    L_0x00e4:
        r7 = r0.infoTextView;
        r7.setText(r2);
        r2 = r0.leftImageView;
        r2.setAnimation(r3, r5, r5);
        if (r1 == 0) goto L_0x0123;
    L_0x00f0:
        r2 = r0.infoTextView;
        r2 = r2.getLayoutParams();
        r2 = (android.widget.FrameLayout.LayoutParams) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r2.leftMargin = r3;
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2.topMargin = r3;
        r2 = r0.subinfoTextView;
        r2.setText(r1);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r15);
        r1 = r0.infoTextView;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r1.setTextSize(r4, r2);
        r1 = r0.infoTextView;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        goto L_0x0148;
    L_0x0123:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.leftMargin = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.topMargin = r2;
        r1 = r0.subinfoTextView;
        r1.setVisibility(r14);
        r1 = r0.infoTextView;
        r1.setTextSize(r4, r12);
        r1 = r0.infoTextView;
        r2 = android.graphics.Typeface.DEFAULT;
        r1.setTypeface(r2);
    L_0x0148:
        r1 = r0.undoButton;
        r1.setVisibility(r14);
        r1 = r0.leftImageView;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setProgress(r6);
        r1 = r0.leftImageView;
        r1.playAnimation();
        goto L_0x025f;
    L_0x015e:
        r5 = r0.currentAction;
        r6 = 2;
        if (r5 == r6) goto L_0x0205;
    L_0x0163:
        r6 = 4;
        if (r5 != r6) goto L_0x0168;
    L_0x0166:
        goto L_0x0205;
    L_0x0168:
        r3 = r0.infoTextView;
        r3 = r3.getLayoutParams();
        r3 = (android.widget.FrameLayout.LayoutParams) r3;
        r5 = NUM; // 0x42340000 float:45.0 double:5.487607523E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3.leftMargin = r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r3.topMargin = r5;
        r3 = r0.infoTextView;
        r3.setTextSize(r4, r12);
        r3 = r0.undoButton;
        r3.setVisibility(r15);
        r3 = r0.infoTextView;
        r5 = android.graphics.Typeface.DEFAULT;
        r3.setTypeface(r5);
        r3 = r0.subinfoTextView;
        r3.setVisibility(r14);
        r3 = r0.leftImageView;
        r3.setVisibility(r14);
        r3 = r0.currentAction;
        if (r3 != 0) goto L_0x01ac;
    L_0x019d:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0e051b float:1.8877688E38 double:1.0531628024E-314;
        r6 = "HistoryClearedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
        goto L_0x01f4;
    L_0x01ac:
        r3 = (int) r1;
        if (r3 >= 0) goto L_0x01e6;
    L_0x01af:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r5.getChat(r3);
        r5 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r5 == 0) goto L_0x01d7;
    L_0x01c4:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x01d7;
    L_0x01c8:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0e0235 float:1.8876183E38 double:1.053162436E-314;
        r6 = "ChannelDeletedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
        goto L_0x01f4;
    L_0x01d7:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0e04f5 float:1.8877611E38 double:1.0531627836E-314;
        r6 = "GroupDeletedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
        goto L_0x01f4;
    L_0x01e6:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0e0285 float:1.8876346E38 double:1.0531624753E-314;
        r6 = "ChatDeletedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
    L_0x01f4:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r5 = r0.currentAction;
        if (r5 != 0) goto L_0x0200;
    L_0x01fe:
        r5 = 1;
        goto L_0x0201;
    L_0x0200:
        r5 = 0;
    L_0x0201:
        r3.addDialogAction(r1, r5);
        goto L_0x025f;
    L_0x0205:
        r1 = 2;
        if (r3 != r1) goto L_0x0212;
    L_0x0208:
        r1 = r0.infoTextView;
        r2 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r1.setText(r2);
        goto L_0x021b;
    L_0x0212:
        r1 = r0.infoTextView;
        r2 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r1.setText(r2);
    L_0x021b:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.leftMargin = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.topMargin = r2;
        r1 = r0.infoTextView;
        r1.setTextSize(r4, r12);
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
        r2 = 0;
        r1.setProgress(r2);
        r1 = r0.leftImageView;
        r1.playAnimation();
    L_0x025f:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r0.infoTextView;
        r2 = r2.getText();
        r1.append(r2);
        r2 = r0.subinfoTextView;
        r2 = r2.getVisibility();
        if (r2 != 0) goto L_0x028d;
    L_0x0275:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = ". ";
        r2.append(r3);
        r3 = r0.subinfoTextView;
        r3 = r3.getText();
        r2.append(r3);
        r2 = r2.toString();
        goto L_0x028f;
    L_0x028d:
        r2 = "";
    L_0x028f:
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1);
        r1 = r16.hasSubInfo();
        if (r1 == 0) goto L_0x02a8;
    L_0x029f:
        r1 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.undoViewHeight = r1;
        goto L_0x02ec;
    L_0x02a8:
        r1 = r16.getParent();
        r1 = r1 instanceof android.view.ViewGroup;
        if (r1 == 0) goto L_0x02ec;
    L_0x02b0:
        r1 = r16.getParent();
        r1 = (android.view.ViewGroup) r1;
        r1 = r1.getMeasuredWidth();
        if (r1 != 0) goto L_0x02c0;
    L_0x02bc:
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r1 = r1.x;
    L_0x02c0:
        r2 = r0.infoTextView;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3);
        r3 = 0;
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r15);
        r6 = 0;
        r17 = r16;
        r18 = r2;
        r19 = r1;
        r20 = r3;
        r21 = r5;
        r22 = r6;
        r17.measureChildWithMargins(r18, r19, r20, r21, r22);
        r1 = r0.infoTextView;
        r1 = r1.getMeasuredHeight();
        r2 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r0.undoViewHeight = r1;
    L_0x02ec:
        r1 = r16.getVisibility();
        if (r1 == 0) goto L_0x0338;
    L_0x02f2:
        r0.setVisibility(r15);
        r1 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r0.undoViewHeight;
        r1 = r1 + r2;
        r1 = (float) r1;
        r0.setTranslationY(r1);
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r2 = new android.animation.Animator[r4];
        r3 = android.view.View.TRANSLATION_Y;
        r5 = 2;
        r5 = new float[r5];
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = r0.undoViewHeight;
        r6 = r6 + r7;
        r6 = (float) r6;
        r5[r15] = r6;
        r6 = r0.additionalTranslationY;
        r6 = -r6;
        r5[r4] = r6;
        r3 = android.animation.ObjectAnimator.ofFloat(r0, r3, r5);
        r2[r15] = r3;
        r1.playTogether(r2);
        r2 = new android.view.animation.DecelerateInterpolator;
        r2.<init>();
        r1.setInterpolator(r2);
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r1.setDuration(r2);
        r1.start();
    L_0x0338:
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
