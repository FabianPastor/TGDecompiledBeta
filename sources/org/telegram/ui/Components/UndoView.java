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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23;
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

    /* JADX WARNING: Removed duplicated region for block: B:88:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x036c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r23, int r25, java.lang.Object r26, java.lang.Object r27, java.lang.Runnable r28, java.lang.Runnable r29) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r3 = r25
            r4 = r26
            java.lang.Runnable r5 = r0.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r0.isShown = r5
            r6 = r28
            r0.currentActionRunnable = r6
            r6 = r29
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0.lastUpdateTime = r6
            android.widget.TextView r6 = r0.undoTextView
            r7 = 2131627321(0x7f0e0d39, float:1.8881903E38)
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
            boolean r10 = r22.isTooltipAction()
            r11 = 1095761920(0x41500000, float:13.0)
            java.lang.String r15 = ""
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1114112000(0x42680000, float:58.0)
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 2
            r14 = 8
            if (r10 == 0) goto L_0x03a2
            r9 = 9
            if (r3 == r9) goto L_0x02f5
            r9 = 10
            if (r3 != r9) goto L_0x00a9
            goto L_0x02f5
        L_0x00a9:
            if (r3 != r14) goto L_0x00c1
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131626176(0x7f0e08c0, float:1.887958E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x0327
        L_0x00c1:
            r9 = 22
            if (r3 != r9) goto L_0x012d
            r18 = 0
            int r3 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r3 <= 0) goto L_0x00e3
            if (r4 != 0) goto L_0x00d8
            r1 = 2131625765(0x7f0e0725, float:1.8878747E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x00d8:
            r1 = 2131625766(0x7f0e0726, float:1.887875E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x00e3:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0115
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0115
            if (r4 != 0) goto L_0x010a
            r1 = 2131625761(0x7f0e0721, float:1.887874E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x010a:
            r1 = 2131625762(0x7f0e0722, float:1.8878741E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x0115:
            if (r4 != 0) goto L_0x0122
            r1 = 2131625763(0x7f0e0723, float:1.8878743E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x0122:
            r1 = 2131625764(0x7f0e0724, float:1.8878745E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x012d:
            r9 = 23
            if (r3 != r9) goto L_0x013c
            r1 = 2131624742(0x7f0e0326, float:1.8876672E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0327
        L_0x013c:
            r9 = 6
            if (r3 != r9) goto L_0x0158
            r1 = 2131624255(0x7f0e013f, float:1.8875685E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558413(0x7f0d000d, float:1.8742141E38)
            r3 = 48
            goto L_0x032d
        L_0x0158:
            int r9 = r0.currentAction
            r10 = 13
            if (r9 != r10) goto L_0x0177
            r1 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131626680(0x7f0e0ab8, float:1.8880603E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
        L_0x0173:
            r3 = 44
            goto L_0x032d
        L_0x0177:
            r10 = 14
            if (r9 != r10) goto L_0x0191
            r1 = 2131626681(0x7f0e0ab9, float:1.8880605E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558450(0x7f0d0032, float:1.8742216E38)
            goto L_0x0173
        L_0x0191:
            r9 = 7
            if (r3 != r9) goto L_0x01be
            r1 = 2131624263(0x7f0e0147, float:1.88757E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x01b6
            r2 = 2131624264(0x7f0e0148, float:1.8875703E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x01b4:
            r10 = r2
            goto L_0x01b7
        L_0x01b6:
            r10 = 0
        L_0x01b7:
            r2 = 2131558412(0x7f0d000c, float:1.874214E38)
            r3 = 36
            goto L_0x032d
        L_0x01be:
            r9 = 20
            if (r3 == r9) goto L_0x0200
            r10 = 21
            if (r3 != r10) goto L_0x01c7
            goto L_0x0200
        L_0x01c7:
            r1 = 19
            if (r3 != r1) goto L_0x01d2
            java.lang.CharSequence r1 = r0.infoText
            r2 = 2131558412(0x7f0d000c, float:1.874214E38)
            goto L_0x032a
        L_0x01d2:
            r1 = 3
            if (r3 != r1) goto L_0x01df
            r1 = 2131624713(0x7f0e0309, float:1.8876613E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01e8
        L_0x01df:
            r1 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x01e8:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x01b6
            r2 = 2131624714(0x7f0e030a, float:1.8876616E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x01b4
        L_0x0200:
            r10 = r27
            org.telegram.messenger.MessagesController$DialogFilter r10 = (org.telegram.messenger.MessagesController.DialogFilter) r10
            r19 = 0
            int r21 = (r1 > r19 ? 1 : (r1 == r19 ? 0 : -1))
            if (r21 == 0) goto L_0x02ad
            int r4 = (int) r1
            if (r4 != 0) goto L_0x0222
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r19 = 32
            long r1 = r1 >> r19
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            int r4 = r1.user_id
        L_0x0222:
            if (r4 <= 0) goto L_0x026a
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r3 != r9) goto L_0x024f
            r2 = 2131625392(0x7f0e05b0, float:1.887799E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = r10.name
            r3[r5] = r1
            java.lang.String r1 = "FilterUserAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x024f:
            r2 = 2131625393(0x7f0e05b1, float:1.8877993E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = r10.name
            r3[r5] = r1
            java.lang.String r1 = "FilterUserRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x026a:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r3 != r9) goto L_0x0294
            r2 = 2131625331(0x7f0e0573, float:1.8877867E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = r10.name
            r3[r5] = r1
            java.lang.String r1 = "FilterChatAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x0294:
            r2 = 2131625332(0x7f0e0574, float:1.8877869E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = r10.name
            r3[r5] = r1
            java.lang.String r1 = "FilterChatRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x02ad:
            if (r3 != r9) goto L_0x02d2
            r1 = 2131625335(0x7f0e0577, float:1.8877875E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r2[r7] = r3
            java.lang.String r3 = r10.name
            r2[r5] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x02d2:
            r1 = 2131625336(0x7f0e0578, float:1.8877877E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r2[r7] = r3
            java.lang.String r3 = r10.name
            r2[r5] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x02f5:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 9
            if (r3 != r2) goto L_0x0312
            r2 = 2131625120(0x7f0e04a0, float:1.8877439E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0327
        L_0x0312:
            r2 = 2131625121(0x7f0e04a1, float:1.887744E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0327:
            r2 = 2131558416(0x7f0d0010, float:1.8742147E38)
        L_0x032a:
            r3 = 36
            r10 = 0
        L_0x032d:
            android.widget.TextView r4 = r0.infoTextView
            r4.setText(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r2, r3, r3)
            if (r10 == 0) goto L_0x036c
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
            r1.setText(r10)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r12)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            goto L_0x038b
        L_0x036c:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r1
            r6.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r14)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
        L_0x038b:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x086a
        L_0x03a2:
            int r10 = r0.currentAction
            r8 = 11
            if (r10 != r8) goto L_0x0412
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624370(0x7f0e01b2, float:1.8875918E38)
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
            r1.setTextSize(r5, r12)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r14)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x086a
        L_0x0412:
            r8 = 15
            r11 = 42
            r19 = 1104150528(0x41d00000, float:26.0)
            if (r10 != r8) goto L_0x04e2
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626192(0x7f0e08d0, float:1.8879613E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625328(0x7f0e0570, float:1.887786E38)
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
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
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
            r1 = 2131625327(0x7f0e056f, float:1.8877859E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r11)
            int r1 = r1.lastIndexOf(r11)
            if (r3 < 0) goto L_0x04b2
            if (r1 < 0) goto L_0x04b2
            if (r3 == r1) goto L_0x04b2
            int r4 = r1 + 1
            r2.replace(r1, r4, r15)
            int r4 = r3 + 1
            r2.replace(r3, r4, r15)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
        L_0x04b2:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setMaxLines(r13)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x086a
        L_0x04e2:
            r8 = 16
            if (r10 == r8) goto L_0x0708
            r8 = 17
            if (r10 != r8) goto L_0x04ec
            goto L_0x0708
        L_0x04ec:
            r8 = 18
            if (r10 != r8) goto L_0x056c
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
            r2.setTextSize(r5, r12)
            android.widget.TextView r2 = r0.infoTextView
            r3 = 16
            r2.setGravity(r3)
            android.widget.TextView r2 = r0.infoTextView
            r2.setText(r1)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r14)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r14)
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
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x086a
        L_0x056c:
            r4 = 12
            if (r10 != r4) goto L_0x0602
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624845(0x7f0e038d, float:1.8876881E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131166012(0x7var_c, float:1.7946257E38)
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
            r1 = 2131624846(0x7f0e038e, float:1.8876883E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r11)
            int r1 = r1.lastIndexOf(r11)
            if (r3 < 0) goto L_0x05dd
            if (r1 < 0) goto L_0x05dd
            if (r3 == r1) goto L_0x05dd
            int r4 = r1 + 1
            r2.replace(r1, r4, r15)
            int r4 = r3 + 1
            r2.replace(r3, r4, r15)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
        L_0x05dd:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setMaxLines(r13)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r14)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x086a
        L_0x0602:
            if (r10 == r13) goto L_0x06a5
            r4 = 4
            if (r10 != r4) goto L_0x0609
            goto L_0x06a5
        L_0x0609:
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
            r3.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r14)
            int r3 = r0.currentAction
            if (r3 != 0) goto L_0x064b
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625567(0x7f0e065f, float:1.8878346E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0693
        L_0x064b:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x0685
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x0676
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0676
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624624(0x7f0e02b0, float:1.8876433E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0693
        L_0x0676:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625524(0x7f0e0634, float:1.8878258E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0693
        L_0x0685:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624717(0x7f0e030d, float:1.8876622E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x0693:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x069f
            r4 = 1
            goto L_0x06a0
        L_0x069f:
            r4 = 0
        L_0x06a0:
            r3.addDialogAction(r1, r4)
            goto L_0x086a
        L_0x06a5:
            if (r3 != r13) goto L_0x06b6
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624713(0x7f0e0309, float:1.8876613E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x06c4
        L_0x06b6:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x06c4:
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
            r1.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558410(0x7f0d000a, float:1.8742135E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x086a
        L_0x0708:
            r1 = 4000(0xfa0, double:1.9763E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r12)
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
            if (r2 == 0) goto L_0x074a
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625045(0x7f0e0455, float:1.8877287E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165390(0x7var_ce, float:1.7944996E38)
            r1.setImageResource(r2)
            goto L_0x07e8
        L_0x074a:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0765
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624938(0x7f0e03ea, float:1.887707E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            goto L_0x07ba
        L_0x0765:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0796
            android.widget.TextView r3 = r0.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r8, r7)
            r3.setText(r2)
            goto L_0x07ba
        L_0x0796:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625044(0x7f0e0454, float:1.8877285E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r1
            java.lang.String r8 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4)
            android.widget.TextView r4 = r0.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r8, r7)
            r2.setText(r3)
        L_0x07ba:
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r1 = org.telegram.messenger.Emoji.getEmojiDrawable(r1)
            r2.setImageDrawable(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r1.setScaleType(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r6.topMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r6.bottomMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r9.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r9.width = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r9.height = r1
        L_0x07e8:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626912(0x7f0e0ba0, float:1.8881074E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x0836
            android.widget.TextView r1 = r0.undoTextView
            android.text.TextPaint r1 = r1.getPaint()
            android.widget.TextView r2 = r0.undoTextView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r1 = r1.measureText(r2)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r1 + r2
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r7)
            android.widget.TextView r2 = r0.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r0.undoImageView
            r2.setVisibility(r14)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r7)
            goto L_0x0847
        L_0x0836:
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r14)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r14)
            r1 = r2
        L_0x0847:
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
            r1.setVisibility(r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
        L_0x086a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r2 = r0.infoTextView
            java.lang.CharSequence r2 = r2.getText()
            r1.append(r2)
            android.widget.TextView r2 = r0.subinfoTextView
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x0897
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = ". "
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            java.lang.String r15 = r2.toString()
        L_0x0897:
            r1.append(r15)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r22.isMultilineSubInfo()
            if (r1 == 0) goto L_0x08ec
            android.view.ViewParent r1 = r22.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x08b7
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x08b7:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r23 = r22
            r24 = r2
            r25 = r1
            r26 = r3
            r27 = r4
            r28 = r6
            r23.measureChildWithMargins(r24, r25, r26, r27, r28)
            android.widget.TextView r1 = r0.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            goto L_0x0969
        L_0x08ec:
            boolean r1 = r22.hasSubInfo()
            if (r1 == 0) goto L_0x08fc
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x0969
        L_0x08fc:
            android.view.ViewParent r1 = r22.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x0969
            android.view.ViewParent r1 = r22.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0914
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0914:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.infoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r23 = r22
            r24 = r2
            r25 = r1
            r26 = r3
            r27 = r4
            r28 = r6
            r23.measureChildWithMargins(r24, r25, r26, r27, r28)
            android.widget.TextView r1 = r0.infoTextView
            int r1 = r1.getMeasuredHeight()
            int r2 = r0.currentAction
            r3 = 16
            if (r2 == r3) goto L_0x0950
            r3 = 17
            if (r2 == r3) goto L_0x0950
            r3 = 18
            if (r2 != r3) goto L_0x094d
            goto L_0x0952
        L_0x094d:
            r12 = 1105199104(0x41e00000, float:28.0)
            goto L_0x0952
        L_0x0950:
            r3 = 18
        L_0x0952:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            int r2 = r0.currentAction
            if (r2 != r3) goto L_0x0969
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r0.undoViewHeight = r1
        L_0x0969:
            int r1 = r22.getVisibility()
            if (r1 == 0) goto L_0x09d4
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            r2 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x097b
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x097d
        L_0x097b:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x097d:
            r3 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r0.undoViewHeight
            int r4 = r4 + r3
            float r3 = (float) r4
            float r1 = r1 * r3
            r0.setTranslationY(r1)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            android.animation.Animator[] r3 = new android.animation.Animator[r5]
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r13]
            boolean r8 = r0.fromTop
            if (r8 == 0) goto L_0x099e
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x09a0
        L_0x099e:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x09a0:
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r10 = r0.undoViewHeight
            int r9 = r9 + r10
            float r9 = (float) r9
            float r8 = r8 * r9
            r6[r7] = r8
            boolean r8 = r0.fromTop
            if (r8 == 0) goto L_0x09b3
            goto L_0x09b5
        L_0x09b3:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x09b5:
            float r8 = r0.additionalTranslationY
            float r2 = r2 * r8
            r6[r5] = r2
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r4, r6)
            r3[r7] = r2
            r1.playTogether(r3)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r1.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r1.setDuration(r2)
            r1.start()
        L_0x09d4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
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
