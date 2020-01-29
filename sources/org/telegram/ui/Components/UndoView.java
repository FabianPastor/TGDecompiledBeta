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

    static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canUndo() {
        return true;
    }

    public UndoView(Context context) {
        super(context);
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        this.subinfoTextView = new TextView(context);
        this.subinfoTextView.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        this.subinfoTextView.setLinkTextColor(Theme.getColor("undo_cancelColor"));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        this.leftImageView = new RLottieImageView(context);
        this.leftImageView.setScaleType(ImageView.ScaleType.CENTER);
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
        this.undoButton = new LinearLayout(context);
        this.undoButton.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                UndoView.this.lambda$new$0$UndoView(view);
            }
        });
        this.undoImageView = new ImageView(context);
        this.undoImageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        this.undoTextView = new TextView(context);
        this.undoTextView.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(Theme.getColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor("undo_infoColor"));
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 6 || i == 3 || i == 5 || i == 7;
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0126  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r18, int r20, java.lang.Object r21, java.lang.Runnable r22, java.lang.Runnable r23) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r3 = r20
            r4 = r21
            java.lang.Runnable r5 = r0.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r0.isShown = r5
            r6 = r22
            r0.currentActionRunnable = r6
            r6 = r23
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.uptimeMillis()
            r0.lastUpdateTime = r6
            boolean r6 = r17.isTooltipAction()
            java.lang.String r7 = "fonts/rmedium.ttf"
            r8 = 1096810496(0x41600000, float:14.0)
            r9 = 1097859072(0x41700000, float:15.0)
            r10 = 1095761920(0x41500000, float:13.0)
            java.lang.String r11 = ""
            r12 = 0
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            r16 = 1114112000(0x42680000, float:58.0)
            r14 = 8
            r15 = 0
            if (r6 == 0) goto L_0x0161
            r1 = 9
            if (r3 == r1) goto L_0x00b7
            r2 = 10
            if (r3 != r2) goto L_0x004b
            goto L_0x00b7
        L_0x004b:
            if (r3 != r14) goto L_0x0063
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            r2 = 2131625826(0x7f0e0762, float:1.887887E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r15] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x00e7
        L_0x0063:
            r1 = 6
            if (r3 != r1) goto L_0x007e
            r1 = 2131624192(0x7f0e0100, float:1.8875557E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624193(0x7f0e0101, float:1.8875559E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558405(0x7f0d0005, float:1.8742125E38)
            r4 = 48
            goto L_0x00ed
        L_0x007e:
            r1 = 7
            if (r3 != r1) goto L_0x0097
            r1 = 2131624200(0x7f0e0108, float:1.8875573E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624201(0x7f0e0109, float:1.8875575E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x0093:
            r3 = 2131558404(0x7f0d0004, float:1.8742123E38)
            goto L_0x00eb
        L_0x0097:
            r1 = 3
            if (r3 != r1) goto L_0x00a4
            r1 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x00ad
        L_0x00a4:
            r1 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x00ad:
            r2 = 2131624607(0x7f0e029f, float:1.8876398E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0093
        L_0x00b7:
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            if (r3 != r1) goto L_0x00d2
            r1 = 2131624958(0x7f0e03fe, float:1.887711E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r15] = r2
            java.lang.String r2 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x00e7
        L_0x00d2:
            r1 = 2131624959(0x7f0e03ff, float:1.8877112E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r15] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x00e7:
            r2 = 0
            r3 = 2131558408(0x7f0d0008, float:1.874213E38)
        L_0x00eb:
            r4 = 36
        L_0x00ed:
            android.widget.TextView r6 = r0.infoTextView
            r6.setText(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r3, r4, r4)
            if (r2 == 0) goto L_0x0126
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.topMargin = r3
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r15)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r1.setTypeface(r2)
            goto L_0x014b
        L_0x0126:
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r2
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r14)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r9)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
        L_0x014b:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x038a
        L_0x0161:
            int r6 = r0.currentAction
            r9 = 11
            if (r6 != r9) goto L_0x01d7
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC.TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624301(0x7f0e016d, float:1.8875778E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 2131558408(0x7f0d0008, float:1.874213E38)
            r4 = 36
            r2.setAnimation(r3, r4, r4)
            android.widget.TextView r2 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r2.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r2.topMargin = r3
            android.widget.TextView r2 = r0.subinfoTextView
            java.lang.String r1 = r1.app_name
            r2.setText(r1)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r15)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r14)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x038a
        L_0x01d7:
            r7 = 12
            if (r6 != r7) goto L_0x027e
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC.TL_authorization) r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624724(0x7f0e0314, float:1.8876636E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165908(0x7var_d4, float:1.7946046E38)
            r1.setImageResource(r2)
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.leftMargin = r2
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.rightMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.topMargin = r2
            android.widget.TextView r1 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.rightMargin = r2
            r1 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r3 = r1.indexOf(r3)
            r4 = 42
            int r1 = r1.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0258
            if (r1 < 0) goto L_0x0258
            if (r3 == r1) goto L_0x0258
            int r4 = r1 + 1
            r2.replace(r1, r4, r11)
            int r4 = r3 + 1
            r2.replace(r3, r4, r11)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
        L_0x0258:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r15)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r15)
            android.widget.TextView r1 = r0.subinfoTextView
            r4 = 2
            r1.setMaxLines(r4)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r14)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r15)
            goto L_0x038a
        L_0x027e:
            r4 = 2
            if (r6 == r4) goto L_0x0325
            r4 = 4
            if (r6 != r4) goto L_0x0286
            goto L_0x0325
        L_0x0286:
            android.widget.TextView r3 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r4 = 1110704128(0x42340000, float:45.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.leftMargin = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r3.topMargin = r4
            android.widget.TextView r3 = r0.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r3.setTextSize(r5, r4)
            android.widget.LinearLayout r3 = r0.undoButton
            r3.setVisibility(r15)
            android.widget.TextView r3 = r0.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r14)
            int r3 = r0.currentAction
            if (r3 != 0) goto L_0x02cc
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625305(0x7f0e0559, float:1.8877814E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0314
        L_0x02cc:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x0306
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x02f7
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x02f7
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624528(0x7f0e0250, float:1.8876238E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0314
        L_0x02f7:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625266(0x7f0e0532, float:1.8877735E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0314
        L_0x0306:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624610(0x7f0e02a2, float:1.8876405E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x0314:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x0320
            r4 = 1
            goto L_0x0321
        L_0x0320:
            r4 = 0
        L_0x0321:
            r3.addDialogAction(r1, r4)
            goto L_0x038a
        L_0x0325:
            r1 = 2
            if (r3 != r1) goto L_0x0337
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0345
        L_0x0337:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0345:
            android.widget.TextView r1 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r2
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r15)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558402(0x7f0d0002, float:1.8742119E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
        L_0x038a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r2 = r0.infoTextView
            java.lang.CharSequence r2 = r2.getText()
            r1.append(r2)
            android.widget.TextView r2 = r0.subinfoTextView
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x03b7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = ". "
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            java.lang.String r11 = r2.toString()
        L_0x03b7:
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r17.isMultilineSubInfo()
            if (r1 == 0) goto L_0x040b
            android.view.ViewParent r1 = r17.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x03d7
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x03d7:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r15)
            r6 = 0
            r18 = r17
            r19 = r2
            r20 = r1
            r21 = r3
            r22 = r4
            r23 = r6
            r18.measureChildWithMargins(r19, r20, r21, r22, r23)
            android.widget.TextView r1 = r0.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            goto L_0x045e
        L_0x040b:
            boolean r1 = r17.hasSubInfo()
            if (r1 == 0) goto L_0x041a
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x045e
        L_0x041a:
            android.view.ViewParent r1 = r17.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x045e
            android.view.ViewParent r1 = r17.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0432
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0432:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r15)
            r6 = 0
            r18 = r17
            r19 = r2
            r20 = r1
            r21 = r3
            r22 = r4
            r23 = r6
            r18.measureChildWithMargins(r19, r20, r21, r22, r23)
            android.widget.TextView r1 = r0.infoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1105199104(0x41e00000, float:28.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
        L_0x045e:
            int r1 = r17.getVisibility()
            if (r1 == 0) goto L_0x04aa
            r0.setVisibility(r15)
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
            int r7 = r0.undoViewHeight
            int r6 = r6 + r7
            float r6 = (float) r6
            r4[r15] = r6
            float r6 = r0.additionalTranslationY
            float r6 = -r6
            r4[r5] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r3, r4)
            r2[r15] = r3
            r1.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r1.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r1.setDuration(r2)
            r1.start()
        L_0x04aa:
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
                this.timeLeftString = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ceil))});
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(this.timeLeftString));
            }
            canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        this.timeLeft -= uptimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = uptimeMillis;
        if (this.timeLeft <= 0) {
            hide(true, 1);
        }
        invalidate();
    }
}
