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
import android.text.Selection;
import android.text.Spannable;
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
import org.telegram.ui.ActionBar.Theme;

public class UndoView extends FrameLayout {
    private float additionalTranslationY;
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private Object currentInfoObject;
    private boolean fromTop;
    private CharSequence infoText;
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

    public void didPressUrl(CharacterStyle characterStyle) {
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
        this(context, false);
    }

    public UndoView(Context context, boolean z) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19;
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
        showWithAction(j, i, (Object) null, runnable, (Runnable) null);
    }

    public void showWithAction(long j, int i, Object obj) {
        showWithAction(j, i, obj, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long j, int i, Runnable runnable, Runnable runnable2) {
        showWithAction(j, i, (Object) null, runnable, runnable2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x01b6  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01e9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r20, int r22, java.lang.Object r23, java.lang.Runnable r24, java.lang.Runnable r25) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r3 = r22
            r4 = r23
            java.lang.Runnable r5 = r0.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r0.isShown = r5
            r6 = r24
            r0.currentActionRunnable = r6
            r6 = r25
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0.lastUpdateTime = r6
            android.widget.TextView r6 = r0.undoTextView
            r7 = 2131627069(0x7f0e0c3d, float:1.8881392E38)
            java.lang.String r8 = "Undo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r7.toUpperCase()
            r6.setText(r7)
            android.widget.ImageView r6 = r0.undoImageView
            r7 = 0
            r6.setVisibility(r7)
            android.widget.TextView r6 = r0.infoTextView
            r8 = 1097859072(0x41700000, float:15.0)
            r6.setTextSize(r5, r8)
            android.widget.TextView r6 = r0.infoTextView
            r9 = 51
            r6.setGravity(r9)
            android.widget.TextView r6 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r9 = -2
            r6.height = r9
            r6.bottomMargin = r7
            org.telegram.ui.Components.RLottieImageView r9 = r0.leftImageView
            android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER
            r9.setScaleType(r10)
            org.telegram.ui.Components.RLottieImageView r9 = r0.leftImageView
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
            r10 = 19
            r9.gravity = r10
            r9.bottomMargin = r7
            r9.topMargin = r7
            r10 = 1077936128(0x40400000, float:3.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r9.leftMargin = r10
            r10 = 1113063424(0x42580000, float:54.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r9.width = r10
            r10 = -2
            r9.height = r10
            android.widget.TextView r10 = r0.infoTextView
            r10.setMinHeight(r7)
            boolean r10 = r19.isTooltipAction()
            r11 = 1095761920(0x41500000, float:13.0)
            java.lang.String r14 = ""
            r12 = 0
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1114112000(0x42680000, float:58.0)
            r15 = 1096810496(0x41600000, float:14.0)
            r13 = 8
            if (r10 == 0) goto L_0x021e
            r1 = 9
            r2 = 0
            if (r3 == r1) goto L_0x0173
            r1 = 10
            if (r3 != r1) goto L_0x00aa
            goto L_0x0173
        L_0x00aa:
            if (r3 != r13) goto L_0x00c2
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r3 = 2131626001(0x7f0e0811, float:1.8879226E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            goto L_0x01a5
        L_0x00c2:
            r1 = 6
            if (r3 != r1) goto L_0x00de
            r1 = 2131624206(0x7f0e010e, float:1.8875585E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624207(0x7f0e010f, float:1.8875587E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558413(0x7f0d000d, float:1.8742141E38)
            r4 = 48
            goto L_0x01aa
        L_0x00de:
            int r1 = r0.currentAction
            r4 = 13
            if (r1 != r4) goto L_0x00fd
            r1 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131626491(0x7f0e09fb, float:1.888022E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558433(0x7f0d0021, float:1.8742182E38)
        L_0x00f9:
            r4 = 44
            goto L_0x01aa
        L_0x00fd:
            r4 = 14
            if (r1 != r4) goto L_0x0117
            r1 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131558441(0x7f0d0029, float:1.8742198E38)
            goto L_0x00f9
        L_0x0117:
            r1 = 7
            if (r3 != r1) goto L_0x013b
            r1 = 2131624214(0x7f0e0116, float:1.8875601E38)
            java.lang.String r3 = "ArchivePinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0141
            r2 = 2131624215(0x7f0e0117, float:1.8875603E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0141
        L_0x013b:
            r1 = 19
            if (r3 != r1) goto L_0x0145
            java.lang.CharSequence r1 = r0.infoText
        L_0x0141:
            r3 = 2131558412(0x7f0d000c, float:1.874214E38)
            goto L_0x01a8
        L_0x0145:
            r1 = 3
            if (r3 != r1) goto L_0x0152
            r1 = 2131624631(0x7f0e02b7, float:1.8876447E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            goto L_0x015b
        L_0x0152:
            r1 = 2131624664(0x7f0e02d8, float:1.8876514E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
        L_0x015b:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0141
            r2 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0141
        L_0x0173:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r4 = 9
            if (r3 != r4) goto L_0x0190
            r3 = 2131625013(0x7f0e0435, float:1.8877222E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01a5
        L_0x0190:
            r3 = 2131625014(0x7f0e0436, float:1.8877224E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x01a5:
            r3 = 2131558416(0x7f0d0010, float:1.8742147E38)
        L_0x01a8:
            r4 = 36
        L_0x01aa:
            android.widget.TextView r9 = r0.infoTextView
            r9.setText(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r3, r4, r4)
            if (r2 == 0) goto L_0x01e9
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            r6.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r1.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r15)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            goto L_0x0208
        L_0x01e9:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r1
            r6.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r13)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
        L_0x0208:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x06b4
        L_0x021e:
            int r10 = r0.currentAction
            r8 = 11
            if (r10 != r8) goto L_0x028d
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624318(0x7f0e017e, float:1.8875812E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 2131558416(0x7f0d0010, float:1.8742147E38)
            r4 = 36
            r2.setAnimation(r3, r4, r4)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
            android.widget.TextView r2 = r0.subinfoTextView
            java.lang.String r1 = r1.app_name
            r2.setText(r1)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r15)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r13)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x06b4
        L_0x028d:
            r8 = 15
            r11 = 42
            r18 = 1104150528(0x41d00000, float:26.0)
            if (r10 != r8) goto L_0x035d
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626017(0x7f0e0821, float:1.8879258E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625212(0x7f0e04fc, float:1.8877626E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558419(0x7f0d0013, float:1.8742153E38)
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
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r1 = r1 + r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r2
            r6.rightMargin = r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
            android.widget.TextView r2 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r1
            r1 = 2131625211(0x7f0e04fb, float:1.8877624E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r11)
            int r1 = r1.lastIndexOf(r11)
            if (r3 < 0) goto L_0x032d
            if (r1 < 0) goto L_0x032d
            if (r3 == r1) goto L_0x032d
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
        L_0x032d:
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
            r1.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x06b4
        L_0x035d:
            r8 = 16
            if (r10 == r8) goto L_0x0584
            r8 = 17
            if (r10 != r8) goto L_0x0367
            goto L_0x0584
        L_0x0367:
            r8 = 18
            if (r10 != r8) goto L_0x03e6
            r1 = r4
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            r2 = 4000(0xfa0, float:5.605E-42)
            int r3 = r1.length()
            int r3 = r3 / 50
            int r3 = r3 * 1600
            r4 = 10000(0x2710, float:1.4013E-41)
            int r3 = java.lang.Math.min(r3, r4)
            int r2 = java.lang.Math.max(r2, r3)
            long r2 = (long) r2
            r0.timeLeft = r2
            android.widget.TextView r2 = r0.infoTextView
            r2.setTextSize(r5, r15)
            android.widget.TextView r2 = r0.infoTextView
            r3 = 16
            r2.setGravity(r3)
            android.widget.TextView r2 = r0.infoTextView
            r2.setText(r1)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r13)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r13)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.rightMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
            r2 = 1088421888(0x40e00000, float:7.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r6.bottomMargin = r2
            r2 = -1
            r6.height = r2
            r2 = 51
            r9.gravity = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r9.bottomMargin = r2
            r9.topMargin = r2
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558412(0x7f0d000c, float:1.874214E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x06b4
        L_0x03e6:
            r4 = 12
            if (r10 != r4) goto L_0x047d
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624755(0x7f0e0333, float:1.8876699E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165939(0x7var_f3, float:1.794611E38)
            r1.setImageResource(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            r1 = 1111490560(0x42400000, float:48.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.rightMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.rightMargin = r2
            r1 = 2131624756(0x7f0e0334, float:1.88767E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r11)
            int r1 = r1.lastIndexOf(r11)
            if (r3 < 0) goto L_0x0457
            if (r1 < 0) goto L_0x0457
            if (r3 == r1) goto L_0x0457
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
        L_0x0457:
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
            r1.setVisibility(r13)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x06b4
        L_0x047d:
            r4 = 2
            if (r10 == r4) goto L_0x0521
            r4 = 4
            if (r10 != r4) goto L_0x0485
            goto L_0x0521
        L_0x0485:
            r3 = 1110704128(0x42340000, float:45.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6.leftMargin = r3
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6.topMargin = r3
            r6.rightMargin = r7
            android.widget.TextView r3 = r0.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r3.setTextSize(r5, r4)
            android.widget.LinearLayout r3 = r0.undoButton
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r13)
            int r3 = r0.currentAction
            if (r3 != 0) goto L_0x04c7
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625439(0x7f0e05df, float:1.8878086E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x050f
        L_0x04c7:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x0501
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x04f2
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x04f2
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624551(0x7f0e0267, float:1.8876285E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x050f
        L_0x04f2:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625399(0x7f0e05b7, float:1.8878005E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x050f
        L_0x0501:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624635(0x7f0e02bb, float:1.8876455E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x050f:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x051b
            r4 = 1
            goto L_0x051c
        L_0x051b:
            r4 = 0
        L_0x051c:
            r3.addDialogAction(r1, r4)
            goto L_0x06b4
        L_0x0521:
            r1 = 2
            if (r3 != r1) goto L_0x0533
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624631(0x7f0e02b7, float:1.8876447E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0541
        L_0x0533:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624664(0x7f0e02d8, float:1.8876514E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0541:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            r1 = 1095761920(0x41500000, float:13.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            r6.rightMargin = r7
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558410(0x7f0d000a, float:1.8742135E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x06b4
        L_0x0584:
            r1 = 4000(0xfa0, double:1.9763E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r15)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 16
            r1.setGravity(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1106247680(0x41var_, float:30.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setMinHeight(r2)
            r1 = r4
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r2 = "🎲"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x05c5
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624936(0x7f0e03e8, float:1.8877066E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165364(0x7var_b4, float:1.7944943E38)
            r1.setImageResource(r2)
            goto L_0x0632
        L_0x05c5:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x05e0
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624839(0x7f0e0387, float:1.887687E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            goto L_0x0604
        L_0x05e0:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624935(0x7f0e03e7, float:1.8877064E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r1
            java.lang.String r8 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4)
            android.widget.TextView r4 = r0.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r8, r7)
            r2.setText(r3)
        L_0x0604:
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r1 = org.telegram.messenger.Emoji.getEmojiDrawable(r1)
            r2.setImageDrawable(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r1.setScaleType(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.bottomMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r9.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.width = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.height = r1
        L_0x0632:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626686(0x7f0e0abe, float:1.8880615E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x0680
            android.widget.TextView r1 = r0.undoTextView
            android.text.TextPaint r1 = r1.getPaint()
            android.widget.TextView r2 = r0.undoTextView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r1 = r1.measureText(r2)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r1 = r1 + r2
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r7)
            android.widget.TextView r2 = r0.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r0.undoImageView
            r2.setVisibility(r13)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r7)
            goto L_0x0691
        L_0x0680:
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r13)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r13)
            r1 = r2
        L_0x0691:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r2
            r6.rightMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            r1 = 1088421888(0x40e00000, float:7.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.bottomMargin = r1
            r1 = -1
            r6.height = r1
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
        L_0x06b4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r2 = r0.infoTextView
            java.lang.CharSequence r2 = r2.getText()
            r1.append(r2)
            android.widget.TextView r2 = r0.subinfoTextView
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x06e1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = ". "
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            java.lang.String r14 = r2.toString()
        L_0x06e1:
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r19.isMultilineSubInfo()
            if (r1 == 0) goto L_0x0736
            android.view.ViewParent r1 = r19.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0701
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0701:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r20 = r19
            r21 = r2
            r22 = r1
            r23 = r3
            r24 = r4
            r25 = r6
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r1 = r0.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            goto L_0x07b3
        L_0x0736:
            boolean r1 = r19.hasSubInfo()
            if (r1 == 0) goto L_0x0746
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x07b3
        L_0x0746:
            android.view.ViewParent r1 = r19.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x07b3
            android.view.ViewParent r1 = r19.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x075e
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x075e:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.infoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r20 = r19
            r21 = r2
            r22 = r1
            r23 = r3
            r24 = r4
            r25 = r6
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r1 = r0.infoTextView
            int r1 = r1.getMeasuredHeight()
            int r2 = r0.currentAction
            r3 = 16
            if (r2 == r3) goto L_0x079a
            r3 = 17
            if (r2 == r3) goto L_0x079a
            r3 = 18
            if (r2 != r3) goto L_0x0797
            goto L_0x079c
        L_0x0797:
            r15 = 1105199104(0x41e00000, float:28.0)
            goto L_0x079c
        L_0x079a:
            r3 = 18
        L_0x079c:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            int r2 = r0.currentAction
            if (r2 != r3) goto L_0x07b3
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r0.undoViewHeight = r1
        L_0x07b3:
            int r1 = r19.getVisibility()
            if (r1 == 0) goto L_0x0820
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x07c7
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x07c9
        L_0x07c7:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x07c9:
            r4 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r0.undoViewHeight
            int r6 = r6 + r4
            float r4 = (float) r6
            float r1 = r1 * r4
            r0.setTranslationY(r1)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            android.animation.Animator[] r4 = new android.animation.Animator[r5]
            android.util.Property r6 = android.view.View.TRANSLATION_Y
            r8 = 2
            float[] r8 = new float[r8]
            boolean r9 = r0.fromTop
            if (r9 == 0) goto L_0x07eb
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x07ed
        L_0x07eb:
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x07ed:
            r10 = 1090519040(0x41000000, float:8.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = r0.undoViewHeight
            int r10 = r10 + r11
            float r10 = (float) r10
            float r9 = r9 * r10
            r8[r7] = r9
            boolean r9 = r0.fromTop
            if (r9 == 0) goto L_0x0801
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x0801:
            float r3 = r0.additionalTranslationY
            float r2 = r2 * r3
            r8[r5] = r2
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r6, r8)
            r4[r7] = r2
            r1.playTogether(r4)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r1.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r1.setDuration(r2)
            r1.start()
        L_0x0820:
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

    public void invalidate() {
        super.invalidate();
        this.infoTextView.invalidate();
        this.leftImageView.invalidate();
    }

    public void setInfoText(CharSequence charSequence) {
        this.infoText = charSequence;
    }
}
