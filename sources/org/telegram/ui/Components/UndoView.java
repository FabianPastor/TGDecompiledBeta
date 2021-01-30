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
    private BackupImageView avatarImageView;
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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty());
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24;
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

    /* JADX WARNING: Removed duplicated region for block: B:273:0x0e2c  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0e44  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0e55  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0e9a  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:321:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r21, int r23, java.lang.Object r24, java.lang.Object r25, java.lang.Runnable r26, java.lang.Runnable r27) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r3 = r23
            r4 = r24
            java.lang.Runnable r5 = r0.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r0.isShown = r5
            r6 = r26
            r0.currentActionRunnable = r6
            r6 = r27
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0.lastUpdateTime = r6
            android.widget.TextView r6 = r0.undoTextView
            r7 = 2131627605(0x7f0e0e55, float:1.888248E38)
            java.lang.String r8 = "Undo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r7.toUpperCase()
            r6.setText(r7)
            android.widget.ImageView r6 = r0.undoImageView
            r7 = 0
            r6.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r6 = r0.leftImageView
            r6.setPadding(r7, r7, r7, r7)
            android.widget.TextView r6 = r0.infoTextView
            r8 = 1097859072(0x41700000, float:15.0)
            r6.setTextSize(r5, r8)
            org.telegram.ui.Components.BackupImageView r6 = r0.avatarImageView
            r9 = 8
            r6.setVisibility(r9)
            android.widget.TextView r6 = r0.infoTextView
            r10 = 51
            r6.setGravity(r10)
            android.widget.TextView r6 = r0.infoTextView
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r11 = -2
            r6.height = r11
            r6.bottomMargin = r7
            org.telegram.ui.Components.RLottieImageView r11 = r0.leftImageView
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r11.setScaleType(r12)
            org.telegram.ui.Components.RLottieImageView r11 = r0.leftImageView
            android.view.ViewGroup$LayoutParams r11 = r11.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r11 = (android.widget.FrameLayout.LayoutParams) r11
            r12 = 19
            r11.gravity = r12
            r11.bottomMargin = r7
            r11.topMargin = r7
            r12 = 1077936128(0x40400000, float:3.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.leftMargin = r12
            r12 = 1113063424(0x42580000, float:54.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.width = r12
            r12 = -2
            r11.height = r12
            android.widget.TextView r12 = r0.infoTextView
            r12.setMinHeight(r7)
            boolean r12 = r20.isTooltipAction()
            java.lang.String r13 = "Chats"
            java.lang.String r15 = ""
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r10 = 30
            r19 = r15
            r14 = 3000(0xbb8, double:1.482E-320)
            r8 = 36
            if (r12 == 0) goto L_0x04d9
            r11 = 34
            r12 = 0
            if (r3 != r11) goto L_0x00f3
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627830(0x7f0e0var_, float:1.8882936E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r4
            java.lang.String r4 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.ui.Components.BackupImageView r4 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForUser(r1, r7)
            java.lang.String r11 = "50_50"
            r4.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r1)
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            r1.setVisibility(r7)
            r0.timeLeft = r14
            r14 = 0
            goto L_0x0448
        L_0x00f3:
            r11 = 33
            if (r3 != r11) goto L_0x010a
            r1 = 2131627820(0x7f0e0f2c, float:1.8882915E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131558491(0x7f0d005b, float:1.87423E38)
            r0.timeLeft = r14
            r14 = 2131558491(0x7f0d005b, float:1.87423E38)
            goto L_0x0448
        L_0x010a:
            if (r3 != r10) goto L_0x012e
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627858(0x7f0e0var_, float:1.8882992E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558492(0x7f0d005c, float:1.8742301E38)
            r0.timeLeft = r14
        L_0x0129:
            r14 = 2131558492(0x7f0d005c, float:1.8742301E38)
            goto L_0x0448
        L_0x012e:
            r10 = 35
            if (r3 != r10) goto L_0x0150
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627859(0x7f0e0var_, float:1.8882994E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558492(0x7f0d005c, float:1.8742301E38)
            r0.timeLeft = r14
            goto L_0x0129
        L_0x0150:
            r10 = 31
            if (r3 != r10) goto L_0x0176
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627856(0x7f0e0var_, float:1.8882988E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558494(0x7f0d005e, float:1.8742305E38)
            r0.timeLeft = r14
        L_0x0171:
            r14 = 2131558494(0x7f0d005e, float:1.8742305E38)
            goto L_0x0448
        L_0x0176:
            if (r3 != r8) goto L_0x0196
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627857(0x7f0e0var_, float:1.888299E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558494(0x7f0d005e, float:1.8742305E38)
            r0.timeLeft = r14
            goto L_0x0171
        L_0x0196:
            r10 = 32
            if (r3 != r10) goto L_0x01bc
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627850(0x7f0e0f4a, float:1.8882976E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558490(0x7f0d005a, float:1.8742297E38)
            r0.timeLeft = r14
            r14 = 2131558490(0x7f0d005a, float:1.8742297E38)
            goto L_0x0448
        L_0x01bc:
            r10 = 9
            if (r3 == r10) goto L_0x0410
            r10 = 10
            if (r3 != r10) goto L_0x01c6
            goto L_0x0410
        L_0x01c6:
            if (r3 != r9) goto L_0x01de
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131626385(0x7f0e0991, float:1.8880005E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x0445
        L_0x01de:
            r10 = 22
            if (r3 != r10) goto L_0x0246
            r10 = 0
            int r3 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x0200
            if (r4 != 0) goto L_0x01f4
            r1 = 2131625912(0x7f0e07b8, float:1.8879045E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01fd
        L_0x01f4:
            r1 = 2131625913(0x7f0e07b9, float:1.8879047E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x01fd:
            r2 = r1
            goto L_0x0445
        L_0x0200:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0230
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0230
            if (r4 != 0) goto L_0x0226
            r1 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01fd
        L_0x0226:
            r1 = 2131625909(0x7f0e07b5, float:1.887904E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01fd
        L_0x0230:
            if (r4 != 0) goto L_0x023c
            r1 = 2131625910(0x7f0e07b6, float:1.8879041E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01fd
        L_0x023c:
            r1 = 2131625911(0x7f0e07b7, float:1.8879043E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01fd
        L_0x0246:
            r10 = 23
            if (r3 != r10) goto L_0x0255
            r1 = 2131624777(0x7f0e0349, float:1.8876743E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0445
        L_0x0255:
            r10 = 6
            if (r3 != r10) goto L_0x0271
            r1 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131624277(0x7f0e0155, float:1.887573E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14 = 2131558417(0x7f0d0011, float:1.874215E38)
            r8 = 48
            goto L_0x0448
        L_0x0271:
            int r10 = r0.currentAction
            r11 = 13
            if (r10 != r11) goto L_0x0290
            r1 = 2131626939(0x7f0e0bbb, float:1.8881128E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131626940(0x7f0e0bbc, float:1.888113E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14 = 2131558496(0x7f0d0060, float:1.874231E38)
        L_0x028c:
            r8 = 44
            goto L_0x0448
        L_0x0290:
            r11 = 14
            if (r10 != r11) goto L_0x02aa
            r1 = 2131626941(0x7f0e0bbd, float:1.8881132E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131626942(0x7f0e0bbe, float:1.8881134E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14 = 2131558497(0x7f0d0061, float:1.8742311E38)
            goto L_0x028c
        L_0x02aa:
            r10 = 7
            if (r3 != r10) goto L_0x02d3
            r1 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x02ce
            r1 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
        L_0x02cd:
            r12 = r1
        L_0x02ce:
            r14 = 2131558416(0x7f0d0010, float:1.8742147E38)
            goto L_0x0448
        L_0x02d3:
            r10 = 20
            if (r3 == r10) goto L_0x0312
            r10 = 21
            if (r3 != r10) goto L_0x02dc
            goto L_0x0312
        L_0x02dc:
            r1 = 19
            if (r3 != r1) goto L_0x02e3
            java.lang.CharSequence r2 = r0.infoText
            goto L_0x02ce
        L_0x02e3:
            r1 = 3
            if (r3 != r1) goto L_0x02f0
            r1 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x02f9
        L_0x02f0:
            r1 = 2131624785(0x7f0e0351, float:1.887676E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x02f9:
            r2 = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x02ce
            r1 = 2131624749(0x7f0e032d, float:1.8876687E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            goto L_0x02cd
        L_0x0312:
            r10 = r25
            org.telegram.messenger.MessagesController$DialogFilter r10 = (org.telegram.messenger.MessagesController.DialogFilter) r10
            r14 = 0
            int r11 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r11 == 0) goto L_0x03c6
            int r4 = (int) r1
            if (r4 != 0) goto L_0x0333
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r11 = 32
            long r1 = r1 >> r11
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            int r4 = r1.user_id
        L_0x0333:
            if (r4 <= 0) goto L_0x037f
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r2 = 20
            if (r3 != r2) goto L_0x0363
            r2 = 2131625478(0x7f0e0606, float:1.8878165E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterUserAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x0363:
            r3 = 2
            r2 = 2131625479(0x7f0e0607, float:1.8878167E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterUserRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x037f:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            r2 = 20
            if (r3 != r2) goto L_0x03ac
            r2 = 2131625417(0x7f0e05c9, float:1.8878041E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterChatAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x03ac:
            r3 = 2
            r2 = 2131625418(0x7f0e05ca, float:1.8878043E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterChatRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x03c6:
            r1 = 20
            if (r3 != r1) goto L_0x03ed
            r1 = 2131625421(0x7f0e05cd, float:1.887805E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r13, r2)
            r3[r7] = r2
            java.lang.String r2 = r10.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatsAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x03ed:
            r1 = 2131625422(0x7f0e05ce, float:1.8878052E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r13, r2)
            r3[r7] = r2
            java.lang.String r2 = r10.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatsRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x0410:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 9
            if (r3 != r2) goto L_0x042e
            r2 = 2131625179(0x7f0e04db, float:1.8877559E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x042e:
            r2 = 2131625180(0x7f0e04dc, float:1.887756E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01fd
        L_0x0445:
            r14 = 2131558420(0x7f0d0014, float:1.8742155E38)
        L_0x0448:
            android.widget.TextView r1 = r0.infoTextView
            r1.setText(r2)
            if (r14 == 0) goto L_0x0465
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r14, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x046a
        L_0x0465:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r9)
        L_0x046a:
            if (r12 == 0) goto L_0x04a9
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r1
            android.widget.TextView r1 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.rightMargin = r2
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r12)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            goto L_0x04d0
        L_0x04a9:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r1
            r1 = 1095761920(0x41500000, float:13.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r1
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r9)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
        L_0x04d0:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
        L_0x04d5:
            r10 = r19
            goto L_0x0b3b
        L_0x04d9:
            int r12 = r0.currentAction
            r10 = 40
            r14 = 42
            if (r12 == r10) goto L_0x0b40
            r10 = 41
            if (r12 == r10) goto L_0x0b40
            if (r12 == r14) goto L_0x0b40
            r10 = 51
            if (r12 == r10) goto L_0x0b40
            r10 = 50
            if (r12 == r10) goto L_0x0b40
            r10 = 52
            if (r12 == r10) goto L_0x0b40
            r10 = 53
            if (r12 == r10) goto L_0x0b40
            r10 = 54
            if (r12 == r10) goto L_0x0b40
            r10 = 55
            if (r12 != r10) goto L_0x0501
            goto L_0x0b40
        L_0x0501:
            r10 = 24
            if (r12 == r10) goto L_0x09e1
            r10 = 25
            if (r12 != r10) goto L_0x050b
            goto L_0x09e1
        L_0x050b:
            r10 = 11
            if (r12 != r10) goto L_0x0579
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r2.setAnimation(r3, r8, r8)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
            android.widget.TextView r2 = r0.subinfoTextView
            java.lang.String r1 = r1.app_name
            r2.setText(r1)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.ImageView r1 = r0.undoImageView
            r1.setVisibility(r9)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x04d5
        L_0x0579:
            r10 = 15
            if (r12 != r10) goto L_0x064b
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626401(0x7f0e09a1, float:1.8880037E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625414(0x7f0e05c6, float:1.8878035E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r1.setAnimation(r2, r8, r8)
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
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r2
            r6.rightMargin = r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
            android.widget.TextView r2 = r0.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r1
            r1 = 2131625413(0x7f0e05c5, float:1.8878033E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r14)
            int r1 = r1.lastIndexOf(r14)
            if (r3 < 0) goto L_0x0618
            if (r1 < 0) goto L_0x0618
            if (r3 == r1) goto L_0x0618
            int r4 = r1 + 1
            r10 = r19
            r2.replace(r1, r4, r10)
            int r4 = r3 + 1
            r2.replace(r3, r4, r10)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
            goto L_0x061a
        L_0x0618:
            r10 = r19
        L_0x061a:
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
            r1.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x0b3b
        L_0x064b:
            r10 = r19
            r13 = 16
            if (r12 == r13) goto L_0x0872
            r13 = 17
            if (r12 != r13) goto L_0x0657
            goto L_0x0872
        L_0x0657:
            r13 = 18
            if (r12 != r13) goto L_0x06d5
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
            r3 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r5, r3)
            android.widget.TextView r2 = r0.infoTextView
            r3 = 16
            r2.setGravity(r3)
            android.widget.TextView r2 = r0.infoTextView
            r2.setText(r1)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r9)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            r1 = 1088421888(0x40e00000, float:7.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.bottomMargin = r1
            r1 = -1
            r6.height = r1
            r1 = 51
            r11.gravity = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.bottomMargin = r1
            r11.topMargin = r1
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558416(0x7f0d0010, float:1.8742147E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x0b3b
        L_0x06d5:
            r4 = 12
            if (r12 != r4) goto L_0x076c
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624880(0x7f0e03b0, float:1.8876952E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131166061(0x7var_d, float:1.7946357E38)
            r1.setImageResource(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
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
            r1 = 2131624881(0x7f0e03b1, float:1.8876954E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r14)
            int r1 = r1.lastIndexOf(r14)
            if (r3 < 0) goto L_0x0746
            if (r1 < 0) goto L_0x0746
            if (r3 == r1) goto L_0x0746
            int r4 = r1 + 1
            r2.replace(r1, r4, r10)
            int r4 = r3 + 1
            r2.replace(r3, r4, r10)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
        L_0x0746:
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
            r1.setVisibility(r9)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x0b3b
        L_0x076c:
            r4 = 2
            if (r12 == r4) goto L_0x0810
            r4 = 4
            if (r12 != r4) goto L_0x0774
            goto L_0x0810
        L_0x0774:
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
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r9)
            int r3 = r0.currentAction
            if (r3 != 0) goto L_0x07b6
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625667(0x7f0e06c3, float:1.8878548E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x07fe
        L_0x07b6:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x07f0
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x07e1
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x07e1
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x07fe
        L_0x07e1:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625621(0x7f0e0695, float:1.8878455E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x07fe
        L_0x07f0:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624752(0x7f0e0330, float:1.8876693E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x07fe:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x080a
            r4 = 1
            goto L_0x080b
        L_0x080a:
            r4 = 0
        L_0x080b:
            r3.addDialogAction(r1, r4)
            goto L_0x0b3b
        L_0x0810:
            r1 = 2
            if (r3 != r1) goto L_0x0822
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0830
        L_0x0822:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624785(0x7f0e0351, float:1.887676E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0830:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
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
            r1.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558414(0x7f0d000e, float:1.8742143E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x0b3b
        L_0x0872:
            r1 = 4000(0xfa0, double:1.9763E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
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
            if (r2 == 0) goto L_0x08b6
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625106(0x7f0e0492, float:1.887741E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165394(0x7var_d2, float:1.7945004E38)
            r1.setImageResource(r2)
            goto L_0x095e
        L_0x08b6:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x08d3
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624988(0x7f0e041c, float:1.8877171E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x08d0:
            r8 = 1096810496(0x41600000, float:14.0)
            goto L_0x092c
        L_0x08d3:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0906
            android.widget.TextView r3 = r0.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r12, r7)
            r3.setText(r2)
            goto L_0x08d0
        L_0x0906:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625105(0x7f0e0491, float:1.8877409E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r1
            java.lang.String r8 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4)
            android.widget.TextView r4 = r0.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r12, r7)
            r2.setText(r3)
        L_0x092c:
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r1 = org.telegram.messenger.Emoji.getEmojiDrawable(r1)
            r2.setImageDrawable(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r1.setScaleType(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.topMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.bottomMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r11.leftMargin = r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r11.width = r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r11.height = r1
        L_0x095e:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131627181(0x7f0e0cad, float:1.888162E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x09ae
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
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r7)
            android.widget.TextView r2 = r0.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r0.undoImageView
            r2.setVisibility(r9)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r7)
            goto L_0x09bc
        L_0x09ae:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r9)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r9)
        L_0x09bc:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
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
            r1.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x0b3b
        L_0x09e1:
            r10 = r19
            r1 = r4
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r25
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            android.widget.ImageView r3 = r0.undoImageView
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r7)
            r3 = 28
            java.lang.String r4 = "undo_infoColor"
            if (r1 == 0) goto L_0x0aad
            android.widget.TextView r8 = r0.infoTextView
            java.lang.String r11 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r8.setTypeface(r11)
            android.widget.TextView r8 = r0.infoTextView
            r11 = 1096810496(0x41600000, float:14.0)
            r8.setTextSize(r5, r11)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            r8.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r12 = "BODY.**"
            r8.setLayerColor(r12, r11)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r12 = "Wibe Big.**"
            r8.setLayerColor(r12, r11)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r12 = "Wibe Big 3.**"
            r8.setLayerColor(r12, r11)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r11 = "Wibe Small.**"
            r8.setLayerColor(r11, r4)
            android.widget.TextView r4 = r0.infoTextView
            r8 = 2131626897(0x7f0e0b91, float:1.8881043E38)
            java.lang.String r11 = "ProximityAlertSet"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r4.setText(r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            r8 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r4.setAnimation(r8, r3, r3)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setSingleLine(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 3
            r3.setMaxLines(r4)
            if (r2 == 0) goto L_0x0a88
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 2131626899(0x7f0e0b93, float:1.8881047E38)
            r8 = 2
            java.lang.Object[] r11 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r11[r7] = r2
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r8)
            r11[r5] = r1
            java.lang.String r1 = "ProximityAlertSetInfoUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r11)
            r3.setText(r1)
            goto L_0x0aa0
        L_0x0a88:
            r8 = 2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 2131626898(0x7f0e0b92, float:1.8881045E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r8)
            r4[r7] = r1
            java.lang.String r1 = "ProximityAlertSetInfoGroup2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r2.setText(r1)
        L_0x0aa0:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            goto L_0x0b2a
        L_0x0aad:
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r8 = "Body Main.**"
            r1.setLayerColor(r8, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r8 = "Body Top.**"
            r1.setLayerColor(r8, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r8 = "Line.**"
            r1.setLayerColor(r8, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r8 = "Curve Big.**"
            r1.setLayerColor(r8, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r4 = "Curve Small.**"
            r1.setLayerColor(r4, r2)
            r1 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r2
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131626896(0x7f0e0b90, float:1.8881041E38)
            java.lang.String r4 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r1.setAnimation(r2, r3, r3)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r9)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
        L_0x0b2a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r1
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
        L_0x0b3b:
            r1 = 0
            r11 = 1096810496(0x41600000, float:14.0)
            goto L_0x0e16
        L_0x0b40:
            r10 = r19
            android.widget.ImageView r3 = r0.undoImageView
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.infoTextView
            android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r11)
            int r3 = r0.currentAction
            r11 = 40
            if (r3 != r11) goto L_0x0b83
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625707(0x7f0e06eb, float:1.887863E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
        L_0x0b7e:
            r1 = 1
            r11 = 1096810496(0x41600000, float:14.0)
            goto L_0x0dea
        L_0x0b83:
            r11 = 41
            if (r3 != r11) goto L_0x0bad
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625708(0x7f0e06ec, float:1.8878632E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            goto L_0x0b7e
        L_0x0bad:
            if (r3 != r14) goto L_0x0be2
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r7, r7, r7, r2)
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r11 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r11)
            r1 = 1
            goto L_0x0dea
        L_0x0be2:
            r11 = 1096810496(0x41600000, float:14.0)
            r12 = 51
            if (r3 != r12) goto L_0x0c0b
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x0de9
        L_0x0c0b:
            r12 = 50
            if (r3 != r12) goto L_0x0CLASSNAME
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624386(0x7f0e01c2, float:1.887595E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x0de9
        L_0x0CLASSNAME:
            r8 = 52
            if (r3 != r8) goto L_0x0c5b
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131626003(0x7f0e0813, float:1.887923E38)
            java.lang.String r3 = "MessageCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x0de9
        L_0x0c5b:
            r8 = 54
            if (r3 != r8) goto L_0x0CLASSNAME
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624693(0x7f0e02f5, float:1.8876573E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558462(0x7f0d003e, float:1.874224E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x0de9
        L_0x0CLASSNAME:
            r8 = 55
            if (r3 != r8) goto L_0x0cad
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558461(0x7f0d003d, float:1.8742238E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x0de9
        L_0x0cad:
            r8 = 53
            if (r3 != r8) goto L_0x0de9
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r25 != 0) goto L_0x0d98
            int r4 = r0.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r12 = (long) r4
            int r4 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r4 != 0) goto L_0x0cfa
            int r1 = r3.intValue()
            if (r1 != r5) goto L_0x0cdc
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625585(0x7f0e0671, float:1.8878382E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x0cee
        L_0x0cdc:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625589(0x7f0e0675, float:1.887839E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x0cee:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x0de5
        L_0x0cfa:
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x0d44
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r3.intValue()
            if (r2 != r5) goto L_0x0d2b
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625584(0x7f0e0670, float:1.887838E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = "FwdMessageToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d8d
        L_0x0d2b:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625588(0x7f0e0674, float:1.8878388E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = "FwdMessagesToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d8d
        L_0x0d44:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r3.intValue()
            if (r2 != r5) goto L_0x0d73
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625586(0x7f0e0672, float:1.8878384E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessageToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d8d
        L_0x0d73:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625590(0x7f0e0676, float:1.8878392E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessagesToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x0d8d:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x0de5
        L_0x0d98:
            r1 = r25
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r3.intValue()
            if (r2 != r5) goto L_0x0dc1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625583(0x7f0e066f, float:1.8878378E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r13, r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessageToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0ddb
        L_0x0dc1:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625587(0x7f0e0673, float:1.8878386E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r13, r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessagesToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x0ddb:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
        L_0x0de5:
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
        L_0x0de9:
            r1 = 0
        L_0x0dea:
            android.widget.TextView r2 = r0.subinfoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r0.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r9)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r2.playAnimation()
        L_0x0e16:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r0.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0e44
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r0.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r15 = r3.toString()
            goto L_0x0e45
        L_0x0e44:
            r15 = r10
        L_0x0e45:
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r20.isMultilineSubInfo()
            if (r2 == 0) goto L_0x0e9a
            android.view.ViewParent r1 = r20.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0e65
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0e65:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r21 = r20
            r22 = r2
            r23 = r1
            r24 = r3
            r25 = r4
            r26 = r6
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r1 = r0.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            goto L_0x0f3e
        L_0x0e9a:
            boolean r2 = r20.hasSubInfo()
            if (r2 == 0) goto L_0x0eaa
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x0f3e
        L_0x0eaa:
            android.view.ViewParent r2 = r20.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x0f3e
            android.view.ViewParent r2 = r20.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x0ecc
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x0ecc:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r0.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r8 = 0
            r21 = r20
            r22 = r2
            r23 = r3
            r24 = r4
            r25 = r6
            r26 = r8
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r2 = r0.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r0.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x0var_
            r4 = 17
            if (r3 == r4) goto L_0x0var_
            r4 = 18
            if (r3 != r4) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x0f0a
        L_0x0var_:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x0f0a:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r2 = r2 + r3
            r0.undoViewHeight = r2
            int r3 = r0.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x0var_
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x0f3e
        L_0x0var_:
            r4 = 25
            if (r3 != r4) goto L_0x0var_
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x0f3e
        L_0x0var_:
            if (r1 == 0) goto L_0x0f3e
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r1
            r0.undoViewHeight = r2
        L_0x0f3e:
            int r1 = r20.getVisibility()
            if (r1 == 0) goto L_0x0fa6
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            if (r1 == 0) goto L_0x0f4e
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0var_
        L_0x0f4e:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0var_:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r0.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r1 = r1 * r2
            r0.setTranslationY(r1)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r5]
            android.util.Property r3 = android.view.View.TRANSLATION_Y
            r4 = 2
            float[] r4 = new float[r4]
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x0var_
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0var_
        L_0x0var_:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0var_:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r0.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x0var_
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0var_
        L_0x0var_:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0var_:
            float r8 = r0.additionalTranslationY
            float r6 = r6 * r8
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
        L_0x0fa6:
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
