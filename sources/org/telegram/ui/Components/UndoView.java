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
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
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
    private LottieAnimationView leftImageView;
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

    /* Access modifiers changed, original: protected */
    public boolean canUndo() {
        return true;
    }

    public UndoView(Context context) {
        Context context2 = context;
        super(context);
        this.infoTextView = new TextView(context2);
        this.infoTextView.setTextSize(1, 15.0f);
        String str = "undo_infoColor";
        this.infoTextView.setTextColor(Theme.getColor(str));
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        this.subinfoTextView = new TextView(context2);
        this.subinfoTextView.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor(str));
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TruncateAt.END);
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        this.leftImageView = new LottieAnimationView(context2);
        this.leftImageView.setScaleType(ScaleType.CENTER);
        LottieAnimationView lottieAnimationView = this.leftImageView;
        String[] strArr = new String[2];
        strArr[0] = "info1";
        strArr[1] = "**";
        String str2 = "undo_background";
        lottieAnimationView.addValueCallback(new KeyPath(strArr), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str2) | -16777216)));
        this.leftImageView.addValueCallback(new KeyPath("info2", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str2) | -16777216)));
        this.leftImageView.addValueCallback(new KeyPath("luCLASSNAME", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luCLASSNAME", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luCLASSNAME", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc9", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc8", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc7", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc6", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc5", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc4", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc3", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc2", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc1", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("Oval", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        this.undoButton = new LinearLayout(context2);
        this.undoButton.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new -$$Lambda$UndoView$ut_O3jMsR3UxcWCuvOqjPzYJ4Go(this));
        this.undoImageView = new ImageView(context2);
        this.undoImageView.setImageResource(NUM);
        String str3 = "undo_cancelColor";
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        this.undoTextView = new TextView(context2);
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
            int i3 = 52;
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    Animator[] animatorArr = new Animator[1];
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (!hasSubInfo()) {
                        i3 = 48;
                    }
                    fArr[0] = (float) AndroidUtilities.dp((float) (i3 + 8));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
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
            if (!hasSubInfo()) {
                i3 = 48;
            }
            setTranslationY((float) AndroidUtilities.dp((float) (i3 + 8)));
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00eb  */
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
        r7 = NUM; // 0x7f0d027c float:1.8743405E38 double:1.053130092E-314;
        r8 = "ChatArchived";
        r9 = NUM; // 0x7f0d029d float:1.8743472E38 double:1.053130108E-314;
        r10 = "ChatsArchived";
        r11 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        r12 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r13 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r15 = 8;
        r14 = 0;
        if (r5 == 0) goto L_0x0159;
    L_0x0040:
        r2 = 0;
        r5 = 9;
        if (r3 == r5) goto L_0x00ab;
    L_0x0045:
        r1 = 10;
        if (r3 != r1) goto L_0x004a;
    L_0x0049:
        goto L_0x00ab;
    L_0x004a:
        if (r3 != r15) goto L_0x0063;
    L_0x004c:
        r1 = r20;
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r3 = NUM; // 0x7f0d06d0 float:1.8745652E38 double:1.053130639E-314;
        r5 = new java.lang.Object[r4];
        r1 = org.telegram.messenger.UserObject.getFirstName(r1);
        r5[r14] = r1;
        r1 = "NowInContacts";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r5);
        goto L_0x00dc;
    L_0x0063:
        r1 = 6;
        if (r3 != r1) goto L_0x007c;
    L_0x0066:
        r1 = NUM; // 0x7f0d00f7 float:1.8742616E38 double:1.0531298996E-314;
        r2 = "ArchiveHidden";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = NUM; // 0x7f0d00f8 float:1.8742618E38 double:1.0531299E-314;
        r3 = "ArchiveHiddenInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = NUM; // 0x7f0CLASSNAME float:1.8609202E38 double:1.053097401E-314;
        goto L_0x00df;
    L_0x007c:
        r1 = 7;
        if (r3 != r1) goto L_0x0095;
    L_0x007f:
        r1 = NUM; // 0x7f0d00ff float:1.8742632E38 double:1.0531299035E-314;
        r2 = "ArchivePinned";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = NUM; // 0x7f0d0100 float:1.8742634E38 double:1.053129904E-314;
        r3 = "ArchivePinnedInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
    L_0x0091:
        r3 = NUM; // 0x7f0CLASSNAME float:1.86092E38 double:1.0530974004E-314;
        goto L_0x00df;
    L_0x0095:
        r1 = 3;
        if (r3 != r1) goto L_0x009d;
    L_0x0098:
        r1 = org.telegram.messenger.LocaleController.getString(r8, r7);
        goto L_0x00a1;
    L_0x009d:
        r1 = org.telegram.messenger.LocaleController.getString(r10, r9);
    L_0x00a1:
        r2 = NUM; // 0x7f0d027d float:1.8743407E38 double:1.0531300923E-314;
        r3 = "ChatArchivedInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        goto L_0x0091;
    L_0x00ab:
        r1 = r20;
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        if (r3 != r5) goto L_0x00c7;
    L_0x00b1:
        r3 = NUM; // 0x7f0d03b0 float:1.874403E38 double:1.053130244E-314;
        r5 = new java.lang.Object[r4];
        r1 = org.telegram.messenger.UserObject.getFirstName(r1);
        r5[r14] = r1;
        r1 = "EditAdminTransferChannelToast";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r5);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        goto L_0x00dc;
    L_0x00c7:
        r3 = NUM; // 0x7f0d03b1 float:1.8744031E38 double:1.0531302444E-314;
        r5 = new java.lang.Object[r4];
        r1 = org.telegram.messenger.UserObject.getFirstName(r1);
        r5[r14] = r1;
        r1 = "EditAdminTransferGroupToast";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r5);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
    L_0x00dc:
        r3 = NUM; // 0x7f0CLASSNAME float:1.8609208E38 double:1.0530974024E-314;
    L_0x00df:
        r5 = r0.infoTextView;
        r5.setText(r1);
        r1 = r0.leftImageView;
        r1.setAnimation(r3);
        if (r2 == 0) goto L_0x011e;
    L_0x00eb:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.leftMargin = r3;
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.topMargin = r3;
        r1 = r0.subinfoTextView;
        r1.setText(r2);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r14);
        r1 = r0.infoTextView;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r1.setTextSize(r4, r2);
        r1 = r0.infoTextView;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        goto L_0x0143;
    L_0x011e:
        r1 = r0.infoTextView;
        r1 = r1.getLayoutParams();
        r1 = (android.widget.FrameLayout.LayoutParams) r1;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.leftMargin = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.topMargin = r2;
        r1 = r0.subinfoTextView;
        r1.setVisibility(r15);
        r1 = r0.infoTextView;
        r1.setTextSize(r4, r12);
        r1 = r0.infoTextView;
        r2 = android.graphics.Typeface.DEFAULT;
        r1.setTypeface(r2);
    L_0x0143:
        r1 = r0.undoButton;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setVisibility(r14);
        r1 = r0.leftImageView;
        r1.setProgress(r6);
        r1 = r0.leftImageView;
        r1.playAnimation();
        goto L_0x0258;
    L_0x0159:
        r5 = r0.currentAction;
        r6 = 2;
        if (r5 == r6) goto L_0x0200;
    L_0x015e:
        r6 = 4;
        if (r5 != r6) goto L_0x0163;
    L_0x0161:
        goto L_0x0200;
    L_0x0163:
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
        r3.setVisibility(r14);
        r3 = r0.infoTextView;
        r5 = android.graphics.Typeface.DEFAULT;
        r3.setTypeface(r5);
        r3 = r0.subinfoTextView;
        r3.setVisibility(r15);
        r3 = r0.leftImageView;
        r3.setVisibility(r15);
        r3 = r0.currentAction;
        if (r3 != 0) goto L_0x01a7;
    L_0x0198:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0d04fb float:1.87447E38 double:1.0531304075E-314;
        r6 = "HistoryClearedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
        goto L_0x01ef;
    L_0x01a7:
        r3 = (int) r1;
        if (r3 >= 0) goto L_0x01e1;
    L_0x01aa:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r5.getChat(r3);
        r5 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r5 == 0) goto L_0x01d2;
    L_0x01bf:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x01d2;
    L_0x01c3:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0d0231 float:1.8743253E38 double:1.0531300547E-314;
        r6 = "ChannelDeletedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
        goto L_0x01ef;
    L_0x01d2:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0d04d5 float:1.8744624E38 double:1.0531303887E-314;
        r6 = "GroupDeletedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
        goto L_0x01ef;
    L_0x01e1:
        r3 = r0.infoTextView;
        r5 = NUM; // 0x7f0d0280 float:1.8743413E38 double:1.0531300937E-314;
        r6 = "ChatDeletedUndo";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setText(r5);
    L_0x01ef:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r5 = r0.currentAction;
        if (r5 != 0) goto L_0x01fb;
    L_0x01f9:
        r5 = 1;
        goto L_0x01fc;
    L_0x01fb:
        r5 = 0;
    L_0x01fc:
        r3.addDialogAction(r1, r5);
        goto L_0x0258;
    L_0x0200:
        r1 = 2;
        if (r3 != r1) goto L_0x020d;
    L_0x0203:
        r1 = r0.infoTextView;
        r2 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r1.setText(r2);
        goto L_0x0216;
    L_0x020d:
        r1 = r0.infoTextView;
        r2 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r1.setText(r2);
    L_0x0216:
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
        r1.setVisibility(r14);
        r1 = r0.infoTextView;
        r2 = android.graphics.Typeface.DEFAULT;
        r1.setTypeface(r2);
        r1 = r0.subinfoTextView;
        r1.setVisibility(r15);
        r1 = r0.leftImageView;
        r1.setVisibility(r14);
        r1 = r0.leftImageView;
        r2 = NUM; // 0x7f0CLASSNAME float:1.8609196E38 double:1.0530973994E-314;
        r1.setAnimation(r2);
        r1 = r0.leftImageView;
        r2 = 0;
        r1.setProgress(r2);
        r1 = r0.leftImageView;
        r1.playAnimation();
    L_0x0258:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r0.infoTextView;
        r2 = r2.getText();
        r1.append(r2);
        r2 = r0.subinfoTextView;
        r2 = r2.getVisibility();
        if (r2 != 0) goto L_0x0286;
    L_0x026e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = ". ";
        r2.append(r3);
        r3 = r0.subinfoTextView;
        r3 = r3.getText();
        r2.append(r3);
        r2 = r2.toString();
        goto L_0x0288;
    L_0x0286:
        r2 = "";
    L_0x0288:
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1);
        r1 = r16.getVisibility();
        if (r1 == 0) goto L_0x02ee;
    L_0x0298:
        r0.setVisibility(r14);
        r1 = r16.hasSubInfo();
        if (r1 == 0) goto L_0x02a4;
    L_0x02a1:
        r1 = 52;
        goto L_0x02a6;
    L_0x02a4:
        r1 = 48;
    L_0x02a6:
        r1 = r1 + r15;
        r1 = (float) r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r0.setTranslationY(r1);
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r2 = new android.animation.Animator[r4];
        r3 = android.view.View.TRANSLATION_Y;
        r5 = 2;
        r5 = new float[r5];
        r6 = r16.hasSubInfo();
        if (r6 == 0) goto L_0x02c5;
    L_0x02c2:
        r6 = 52;
        goto L_0x02c7;
    L_0x02c5:
        r6 = 48;
    L_0x02c7:
        r6 = r6 + r15;
        r6 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r5[r14] = r6;
        r6 = r0.additionalTranslationY;
        r6 = -r6;
        r5[r4] = r6;
        r3 = android.animation.ObjectAnimator.ofFloat(r0, r3, r5);
        r2[r14] = r3;
        r1.playTogether(r2);
        r2 = new android.view.animation.DecelerateInterpolator;
        r2.<init>();
        r1.setInterpolator(r2);
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r1.setDuration(r2);
        r1.start();
    L_0x02ee:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(hasSubInfo() ? 52.0f : 48.0f), NUM));
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
