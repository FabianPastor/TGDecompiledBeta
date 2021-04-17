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
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.StaticLayout;
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

    static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$showWithAction$2(View view, MotionEvent motionEvent) {
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
        this(context, (BaseFragment) null, false);
    }

    public UndoView(Context context, BaseFragment baseFragment) {
        this(context, baseFragment, false);
    }

    public UndoView(Context context, BaseFragment baseFragment, boolean z) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77;
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
                onRemoveDialogAction(this.currentDialogId, this.currentAction);
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

    /* JADX WARNING: Removed duplicated region for block: B:421:0x126d  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x1294  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x12d9  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x1383  */
    /* JADX WARNING: Removed duplicated region for block: B:468:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r21, int r23, java.lang.Object r24, java.lang.Object r25, java.lang.Runnable r26, java.lang.Runnable r27) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r0 = r23
            r4 = r24
            r5 = r25
            java.lang.Runnable r6 = r1.currentActionRunnable
            if (r6 == 0) goto L_0x0011
            r6.run()
        L_0x0011:
            r6 = 1
            r1.isShown = r6
            r7 = r26
            r1.currentActionRunnable = r7
            r7 = r27
            r1.currentCancelRunnable = r7
            r1.currentDialogId = r2
            r1.currentAction = r0
            r7 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r7
            r1.currentInfoObject = r4
            long r7 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r7
            android.widget.TextView r7 = r1.undoTextView
            r8 = 2131627805(0x7f0e0f1d, float:1.8882885E38)
            java.lang.String r9 = "Undo"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            java.lang.String r8 = r8.toUpperCase()
            r7.setText(r8)
            android.widget.ImageView r7 = r1.undoImageView
            r8 = 0
            r7.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r7 = r1.leftImageView
            r7.setPadding(r8, r8, r8, r8)
            android.widget.TextView r7 = r1.infoTextView
            r9 = 1097859072(0x41700000, float:15.0)
            r7.setTextSize(r6, r9)
            org.telegram.ui.Components.BackupImageView r7 = r1.avatarImageView
            r10 = 8
            r7.setVisibility(r10)
            android.widget.TextView r7 = r1.infoTextView
            r11 = 51
            r7.setGravity(r11)
            android.widget.TextView r7 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r7 = (android.widget.FrameLayout.LayoutParams) r7
            r11 = -2
            r7.height = r11
            r11 = 1095761920(0x41500000, float:13.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r7.topMargin = r11
            r7.bottomMargin = r8
            org.telegram.ui.Components.RLottieImageView r11 = r1.leftImageView
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r11.setScaleType(r12)
            org.telegram.ui.Components.RLottieImageView r11 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r11 = r11.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r11 = (android.widget.FrameLayout.LayoutParams) r11
            r12 = 19
            r11.gravity = r12
            r11.bottomMargin = r8
            r11.topMargin = r8
            r12 = 1077936128(0x40400000, float:3.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.leftMargin = r12
            r12 = 1113063424(0x42580000, float:54.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.width = r12
            r12 = -2
            r11.height = r12
            android.widget.TextView r12 = r1.infoTextView
            r12.setMinHeight(r8)
            r12 = 0
            r1.setOnClickListener(r12)
            org.telegram.ui.Components.-$$Lambda$UndoView$ZAs14JcizD3kU0BwPeZw5NJPYZU r13 = org.telegram.ui.Components.$$Lambda$UndoView$ZAs14JcizD3kU0BwPeZw5NJPYZU.INSTANCE
            r1.setOnTouchListener(r13)
            android.widget.TextView r13 = r1.infoTextView
            org.telegram.ui.Components.UndoView$LinkMovementMethodMy r14 = new org.telegram.ui.Components.UndoView$LinkMovementMethodMy
            r14.<init>()
            r13.setMovementMethod(r14)
            boolean r13 = r20.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r9 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r14 = 30
            r19 = r11
            r10 = 3000(0xbb8, double:1.482E-320)
            r15 = 36
            if (r13 == 0) goto L_0x06cd
            r13 = 74
            if (r0 != r13) goto L_0x00f1
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            r0 = 2131627157(0x7f0e0CLASSNAME, float:1.888157E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627166(0x7f0e0c9e, float:1.8881589E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r14 = 2131558442(0x7f0d002a, float:1.87422E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            goto L_0x0628
        L_0x00f1:
            r13 = 34
            if (r0 != r13) goto L_0x012e
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628061(0x7f0e101d, float:1.8883404E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
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
            r1.timeLeft = r10
            r0 = r2
        L_0x012b:
            r14 = 0
            goto L_0x0628
        L_0x012e:
            r13 = 37
            if (r0 != r13) goto L_0x017e
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x0158
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x0165
        L_0x0158:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x0165:
            r2 = 2131628122(0x7f0e105a, float:1.8883528E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r8)
            r1.timeLeft = r10
            goto L_0x012b
        L_0x017e:
            r13 = 33
            if (r0 != r13) goto L_0x0192
            r0 = 2131628044(0x7f0e100c, float:1.888337E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r14 = 2131558521(0x7f0d0079, float:1.874236E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x0192:
            r13 = 77
            if (r0 != r13) goto L_0x01bd
            r0 = r4
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r14 = 2131558462(0x7f0d003e, float:1.874224E38)
            r2 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r2
            org.telegram.ui.ActionBar.BaseFragment r2 = r1.parentFragment
            if (r2 == 0) goto L_0x0628
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$Message
            if (r2 == 0) goto L_0x0628
            r2 = r5
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            r1.setOnTouchListener(r12)
            android.widget.TextView r3 = r1.infoTextView
            r3.setMovementMethod(r12)
            org.telegram.ui.Components.-$$Lambda$UndoView$xeJ8z9FlextsmLZ8kkzmlexL7K4 r3 = new org.telegram.ui.Components.-$$Lambda$UndoView$xeJ8z9FlextsmLZ8kkzmlexL7K4
            r3.<init>(r2)
            r1.setOnClickListener(r3)
            goto L_0x0628
        L_0x01bd:
            if (r0 != r14) goto L_0x01e8
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x01cb
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x01d0
        L_0x01cb:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x01d0:
            r2 = 2131628120(0x7f0e1058, float:1.8883524E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558522(0x7f0d007a, float:1.8742362E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x01e8:
            r13 = 35
            if (r0 != r13) goto L_0x021b
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x01f8
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0203
        L_0x01f8:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0202
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0203
        L_0x0202:
            r0 = r9
        L_0x0203:
            r2 = 2131628121(0x7f0e1059, float:1.8883526E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558522(0x7f0d007a, float:1.8742362E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x021b:
            r13 = 31
            if (r0 != r13) goto L_0x0248
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x022b
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0230
        L_0x022b:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0230:
            r2 = 2131628118(0x7f0e1056, float:1.888352E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x0248:
            r13 = 38
            if (r0 != r13) goto L_0x027b
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0267
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628128(0x7f0e1060, float:1.888354E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r0 = r0.title
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0274
        L_0x0267:
            r0 = 2131628127(0x7f0e105f, float:1.8883538E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0274:
            r14 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x027b:
            r13 = 42
            if (r0 != r13) goto L_0x0293
            r0 = 2131628101(0x7f0e1045, float:1.8883485E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x0293:
            r13 = 43
            if (r0 != r13) goto L_0x02ab
            r0 = 2131628102(0x7f0e1046, float:1.8883487E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x02ab:
            int r13 = r1.currentAction
            r14 = 39
            if (r13 != r14) goto L_0x02c5
            r0 = 2131628035(0x7f0e1003, float:1.8883351E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558525(0x7f0d007d, float:1.8742368E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x02c5:
            r14 = 40
            if (r13 != r14) goto L_0x0333
            r0 = 2131628034(0x7f0e1002, float:1.888335E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r14 = 2131558524(0x7f0d007c, float:1.8742366E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            android.widget.TextView r2 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r3 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r3.<init>()
            r2.setMovementMethod(r3)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            java.lang.String r3 = "**"
            int r3 = r0.indexOf(r3)
            java.lang.String r4 = "**"
            int r0 = r0.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0330
            if (r0 < 0) goto L_0x0330
            if (r3 == r0) goto L_0x0330
            int r4 = r0 + 2
            r2.replace(r0, r4, r9)
            int r4 = r3 + 2
            r2.replace(r3, r4, r9)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x032c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x032c }
            r5.<init>()     // Catch:{ Exception -> 0x032c }
            java.lang.String r10 = "tg://openmessage?user_id="
            r5.append(r10)     // Catch:{ Exception -> 0x032c }
            int r10 = r1.currentAccount     // Catch:{ Exception -> 0x032c }
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)     // Catch:{ Exception -> 0x032c }
            int r10 = r10.getClientUserId()     // Catch:{ Exception -> 0x032c }
            r5.append(r10)     // Catch:{ Exception -> 0x032c }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x032c }
            r4.<init>(r5)     // Catch:{ Exception -> 0x032c }
            r5 = 2
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)     // Catch:{ Exception -> 0x032c }
            goto L_0x0330
        L_0x032c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0330:
            r0 = r2
            goto L_0x0628
        L_0x0333:
            if (r0 != r15) goto L_0x035e
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0341
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0346
        L_0x0341:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0346:
            r2 = 2131628119(0x7f0e1057, float:1.8883522E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x035e:
            r14 = 32
            if (r0 != r14) goto L_0x038b
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x036e
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0373
        L_0x036e:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0373:
            r2 = 2131628092(0x7f0e103c, float:1.8883467E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r8] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r14 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r1.timeLeft = r10
            goto L_0x0628
        L_0x038b:
            r10 = 9
            if (r0 == r10) goto L_0x05f3
            r10 = 10
            if (r0 != r10) goto L_0x0395
            goto L_0x05f3
        L_0x0395:
            r10 = 8
            if (r0 != r10) goto L_0x03af
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r8] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0625
        L_0x03af:
            r10 = 22
            if (r0 != r10) goto L_0x041b
            r10 = 0
            int r0 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x03d1
            if (r4 != 0) goto L_0x03c6
            r0 = 2131626026(0x7f0e082a, float:1.8879277E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x03c6:
            r0 = 2131626027(0x7f0e082b, float:1.8879279E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x03d1:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r2 = -r2
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x0403
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0403
            if (r4 != 0) goto L_0x03f8
            r0 = 2131626022(0x7f0e0826, float:1.8879268E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x03f8:
            r0 = 2131626023(0x7f0e0827, float:1.887927E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x0403:
            if (r4 != 0) goto L_0x0410
            r0 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x0410:
            r0 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x041b:
            r10 = 23
            if (r0 != r10) goto L_0x042a
            r0 = 2131624827(0x7f0e037b, float:1.8876845E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x042a:
            r10 = 6
            if (r0 != r10) goto L_0x0446
            r0 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624293(0x7f0e0165, float:1.8875762E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r15 = 48
            goto L_0x0628
        L_0x0446:
            r10 = 13
            if (r13 != r10) goto L_0x0463
            r0 = 2131627085(0x7f0e0c4d, float:1.8881424E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627086(0x7f0e0c4e, float:1.8881426E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14 = 2131558530(0x7f0d0082, float:1.8742378E38)
        L_0x045f:
            r15 = 44
            goto L_0x0628
        L_0x0463:
            r10 = 14
            if (r13 != r10) goto L_0x047d
            r0 = 2131627087(0x7f0e0c4f, float:1.8881429E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627088(0x7f0e0CLASSNAME, float:1.888143E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14 = 2131558531(0x7f0d0083, float:1.874238E38)
            goto L_0x045f
        L_0x047d:
            r10 = 7
            if (r0 != r10) goto L_0x04a6
            r0 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04a1
            r2 = 2131624301(0x7f0e016d, float:1.8875778E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x04a0:
            r12 = r2
        L_0x04a1:
            r14 = 2131558417(0x7f0d0011, float:1.874215E38)
            goto L_0x0628
        L_0x04a6:
            r10 = 20
            if (r0 == r10) goto L_0x04e4
            r10 = 21
            if (r0 != r10) goto L_0x04af
            goto L_0x04e4
        L_0x04af:
            r2 = 19
            if (r0 != r2) goto L_0x04b6
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x04a1
        L_0x04b6:
            r2 = 3
            if (r0 != r2) goto L_0x04c3
            r0 = 2131624797(0x7f0e035d, float:1.8876784E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x04cc
        L_0x04c3:
            r0 = 2131624835(0x7f0e0383, float:1.887686E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x04cc:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04a1
            r2 = 2131624798(0x7f0e035e, float:1.8876786E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x04a0
        L_0x04e4:
            org.telegram.messenger.MessagesController$DialogFilter r5 = (org.telegram.messenger.MessagesController.DialogFilter) r5
            r10 = 0
            int r13 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r13 == 0) goto L_0x0594
            int r4 = (int) r2
            if (r4 != 0) goto L_0x0503
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r10 = 32
            long r2 = r2 >> r10
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            int r4 = r2.user_id
        L_0x0503:
            if (r4 <= 0) goto L_0x054f
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            r3 = 20
            if (r0 != r3) goto L_0x0533
            r3 = 2131625548(0x7f0e064c, float:1.8878307E38)
            r4 = 2
            java.lang.Object[] r10 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r10[r8] = r2
            java.lang.String r2 = r5.name
            r10[r6] = r2
            java.lang.String r2 = "FilterUserAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05df
        L_0x0533:
            r4 = 2
            r3 = 2131625549(0x7f0e064d, float:1.887831E38)
            java.lang.Object[] r10 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r10[r8] = r2
            java.lang.String r2 = r5.name
            r10[r6] = r2
            java.lang.String r2 = "FilterUserRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05df
        L_0x054f:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            r3 = 20
            if (r0 != r3) goto L_0x057b
            r3 = 2131625487(0x7f0e060f, float:1.8878183E38)
            r4 = 2
            java.lang.Object[] r10 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r10[r8] = r2
            java.lang.String r2 = r5.name
            r10[r6] = r2
            java.lang.String r2 = "FilterChatAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05df
        L_0x057b:
            r4 = 2
            r3 = 2131625488(0x7f0e0610, float:1.8878185E38)
            java.lang.Object[] r10 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r10[r8] = r2
            java.lang.String r2 = r5.name
            r10[r6] = r2
            java.lang.String r2 = "FilterChatRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r10)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05df
        L_0x0594:
            r2 = 20
            if (r0 != r2) goto L_0x05bc
            r2 = 2131625491(0x7f0e0613, float:1.8878191E38)
            r3 = 2
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r10[r8] = r3
            java.lang.String r3 = r5.name
            r10[r6] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r10)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05df
        L_0x05bc:
            r2 = 2131625492(0x7f0e0614, float:1.8878193E38)
            r3 = 2
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r10[r8] = r3
            java.lang.String r3 = r5.name
            r10[r6] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r10)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x05df:
            r3 = 20
            if (r0 != r3) goto L_0x05eb
            r0 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r14 = 2131558434(0x7f0d0022, float:1.8742184E38)
            goto L_0x0330
        L_0x05eb:
            r0 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r14 = 2131558435(0x7f0d0023, float:1.8742186E38)
            goto L_0x0330
        L_0x05f3:
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r3 = 9
            if (r0 != r3) goto L_0x0610
            r0 = 2131625242(0x7f0e051a, float:1.8877686E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r8] = r2
            java.lang.String r2 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0625
        L_0x0610:
            r0 = 2131625243(0x7f0e051b, float:1.8877688E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r8] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0625:
            r14 = 2131558421(0x7f0d0015, float:1.8742157E38)
        L_0x0628:
            android.widget.TextView r2 = r1.infoTextView
            r2.setText(r0)
            if (r14 == 0) goto L_0x0655
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r14, r15, r15)
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
            goto L_0x065c
        L_0x0655:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x065c:
            if (r12 == 0) goto L_0x069d
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r12)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x06c6
        L_0x069d:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x06c6:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x0d73
        L_0x06cd:
            int r12 = r1.currentAction
            r13 = 45
            r14 = 60
            if (r12 == r13) goto L_0x0d78
            r13 = 46
            if (r12 == r13) goto L_0x0d78
            r13 = 47
            if (r12 == r13) goto L_0x0d78
            r13 = 51
            if (r12 == r13) goto L_0x0d78
            r13 = 50
            if (r12 == r13) goto L_0x0d78
            r13 = 52
            if (r12 == r13) goto L_0x0d78
            r13 = 53
            if (r12 == r13) goto L_0x0d78
            r13 = 54
            if (r12 == r13) goto L_0x0d78
            r13 = 55
            if (r12 == r13) goto L_0x0d78
            r13 = 56
            if (r12 == r13) goto L_0x0d78
            r13 = 57
            if (r12 == r13) goto L_0x0d78
            r13 = 58
            if (r12 == r13) goto L_0x0d78
            r13 = 59
            if (r12 == r13) goto L_0x0d78
            if (r12 == r14) goto L_0x0d78
            r13 = 71
            if (r12 == r13) goto L_0x0d78
            r13 = 70
            if (r12 == r13) goto L_0x0d78
            r13 = 75
            if (r12 == r13) goto L_0x0d78
            r13 = 76
            if (r12 == r13) goto L_0x0d78
            r13 = 41
            if (r12 != r13) goto L_0x071d
            goto L_0x0d78
        L_0x071d:
            r10 = 24
            if (r12 == r10) goto L_0x0CLASSNAME
            r10 = 25
            if (r12 != r10) goto L_0x0727
            goto L_0x0CLASSNAME
        L_0x0727:
            r5 = 11
            if (r12 != r5) goto L_0x0797
            r0 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624420(0x7f0e01e4, float:1.887602E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r2.setAnimation(r3, r15, r15)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
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
            goto L_0x0d73
        L_0x0797:
            r5 = 15
            if (r12 != r5) goto L_0x0868
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626522(0x7f0e0a1a, float:1.8880283E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625484(0x7f0e060c, float:1.8878177E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r0.setAnimation(r2, r15, r15)
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
            r7.leftMargin = r2
            r7.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625483(0x7f0e060b, float:1.8878175E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0835
            if (r0 < 0) goto L_0x0835
            if (r4 == r0) goto L_0x0835
            int r3 = r0 + 1
            r2.replace(r0, r3, r9)
            int r3 = r4 + 1
            r2.replace(r4, r3, r9)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r3.<init>(r5)
            int r0 = r0 - r6
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x0835:
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
            goto L_0x0d73
        L_0x0868:
            r5 = 16
            if (r12 == r5) goto L_0x0a99
            r5 = 17
            if (r12 != r5) goto L_0x0872
            goto L_0x0a99
        L_0x0872:
            r5 = 18
            if (r12 != r5) goto L_0x08f4
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
            r2.setTextSize(r6, r3)
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
            r7.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.bottomMargin = r0
            r0 = -1
            r7.height = r0
            r0 = 51
            r11 = r19
            r11.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r11.bottomMargin = r0
            r11.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d73
        L_0x08f4:
            r4 = 12
            if (r12 != r4) goto L_0x098f
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624930(0x7f0e03e2, float:1.8877054E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166085(0x7var_, float:1.7946405E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131624931(0x7f0e03e3, float:1.8877056E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0967
            if (r0 < 0) goto L_0x0967
            if (r4 == r0) goto L_0x0967
            int r3 = r0 + 1
            r2.replace(r0, r3, r9)
            int r3 = r4 + 1
            r2.replace(r4, r3, r9)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r3.<init>(r5)
            int r0 = r0 - r6
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x0967:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r8)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 2
            r0.setMaxLines(r4)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            goto L_0x0d73
        L_0x098f:
            r4 = 2
            if (r12 == r4) goto L_0x0a35
            r4 = 4
            if (r12 != r4) goto L_0x0997
            goto L_0x0a35
        L_0x0997:
            r0 = 1110704128(0x42340000, float:45.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.topMargin = r0
            r7.rightMargin = r8
            android.widget.TextView r0 = r1.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r4)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r8)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 8
            r0.setVisibility(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r4)
            int r0 = r1.currentAction
            if (r0 != 0) goto L_0x09db
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625752(0x7f0e0718, float:1.887872E38)
            java.lang.String r5 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0a23
        L_0x09db:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x0a15
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r4.getChat(r0)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r4 == 0) goto L_0x0a06
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0a06
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624705(0x7f0e0301, float:1.8876597E38)
            java.lang.String r5 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0a23
        L_0x0a06:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625705(0x7f0e06e9, float:1.8878625E38)
            java.lang.String r5 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0a23
        L_0x0a15:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624801(0x7f0e0361, float:1.8876792E38)
            java.lang.String r5 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
        L_0x0a23:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r4 = r1.currentAction
            if (r4 != 0) goto L_0x0a2f
            r4 = 1
            goto L_0x0a30
        L_0x0a2f:
            r4 = 0
        L_0x0a30:
            r0.addDialogAction(r2, r4)
            goto L_0x0d73
        L_0x0a35:
            r2 = 2
            if (r0 != r2) goto L_0x0a47
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624797(0x7f0e035d, float:1.8876784E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0a55
        L_0x0a47:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624835(0x7f0e0383, float:1.887686E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0a55:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.topMargin = r0
            r7.rightMargin = r8
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
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
            r2 = 2131558415(0x7f0d000f, float:1.8742145E38)
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d73
        L_0x0a99:
            r11 = r19
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
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
            if (r2 == 0) goto L_0x0adf
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625169(0x7f0e04d1, float:1.8877538E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165396(0x7var_d4, float:1.7945008E38)
            r0.setImageResource(r2)
            goto L_0x0b87
        L_0x0adf:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0afc
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625038(0x7f0e044e, float:1.8877273E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0af9:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0b55
        L_0x0afc:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0b2f
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r10, r8)
            r3.setText(r2)
            goto L_0x0af9
        L_0x0b2f:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625168(0x7f0e04d0, float:1.8877536E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r8] = r0
            java.lang.String r5 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r10, r8)
            r2.setText(r3)
        L_0x0b55:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r11.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.height = r0
        L_0x0b87:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627347(0x7f0e0d53, float:1.8881956E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0bd9
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
            goto L_0x0be9
        L_0x0bd9:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0be9:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r2
            r7.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.bottomMargin = r0
            r0 = -1
            r7.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r8)
            goto L_0x0d73
        L_0x0CLASSNAME:
            r2 = 8
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r5
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r8)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0cdf
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r1.infoTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "BODY.**"
            r4.setLayerColor(r10, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "Wibe Big.**"
            r4.setLayerColor(r10, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "Wibe Big 3.**"
            r4.setLayerColor(r10, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r5 = "Wibe Small.**"
            r4.setLayerColor(r5, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627041(0x7f0e0CLASSNAME, float:1.8881335E38)
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r5 = 28
            r10 = 28
            r2.setAnimation(r4, r5, r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r8)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0cb8
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627043(0x7f0e0CLASSNAME, float:1.888134E38)
            r5 = 2
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r10[r8] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r10[r6] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            r2.setText(r0)
            goto L_0x0cd0
        L_0x0cb8:
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627042(0x7f0e0CLASSNAME, float:1.8881337E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r4[r8] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0cd0:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            goto L_0x0d62
        L_0x0cdf:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r3)
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
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627040(0x7f0e0CLASSNAME, float:1.8881333E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
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
        L_0x0d62:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r7.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0d73:
            r0 = 0
            r13 = 1096810496(0x41600000, float:14.0)
            goto L_0x1257
        L_0x0d78:
            android.widget.ImageView r12 = r1.undoImageView
            r13 = 8
            r12.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r12 = r1.leftImageView
            r12.setVisibility(r8)
            android.widget.TextView r12 = r1.infoTextView
            android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
            r12.setTypeface(r13)
            int r12 = r1.currentAction
            r13 = 76
            r16 = 1091567616(0x41100000, float:9.0)
            if (r12 != r13) goto L_0x0dbb
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624592(0x7f0e0290, float:1.8876368E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
        L_0x0db6:
            r0 = 1
            r13 = 1096810496(0x41600000, float:14.0)
            goto L_0x1229
        L_0x0dbb:
            r13 = 75
            if (r12 != r13) goto L_0x0de3
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625684(0x7f0e06d4, float:1.8878583E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            goto L_0x0db6
        L_0x0de3:
            r13 = 70
            if (r0 != r13) goto L_0x0e54
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r8)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x0e06
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e23
        L_0x0e06:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x0e13
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e23
        L_0x0e13:
            if (r0 < r14) goto L_0x0e1d
            int r0 = r0 / r14
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e23
        L_0x0e1d:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0e23:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r8] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558433(0x7f0d0021, float:1.8742182E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            goto L_0x0db6
        L_0x0e54:
            r0 = 71
            if (r12 != r0) goto L_0x0e86
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r0.setAnimation(r2, r15, r15)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            r1.timeLeft = r10
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            r13 = 1096810496(0x41600000, float:14.0)
            goto L_0x1228
        L_0x0e86:
            r0 = 45
            if (r12 != r0) goto L_0x0eaf
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625799(0x7f0e0747, float:1.8878816E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            goto L_0x0db6
        L_0x0eaf:
            r0 = 46
            if (r12 != r0) goto L_0x0ed8
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625800(0x7f0e0748, float:1.8878818E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r0.setAnimation(r2, r15, r15)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r2)
            goto L_0x0db6
        L_0x0ed8:
            r0 = 47
            if (r12 != r0) goto L_0x0f0d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625806(0x7f0e074e, float:1.887883E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558457(0x7f0d0039, float:1.874223E38)
            r0.setAnimation(r2, r15, r15)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r8, r8, r8, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r13 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r13)
            r0 = 1
            goto L_0x1229
        L_0x0f0d:
            r13 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r12 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r2, r15, r15)
            r1.timeLeft = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x1228
        L_0x0var_:
            r0 = 50
            if (r12 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r0.setAnimation(r2, r15, r15)
            r1.timeLeft = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x1228
        L_0x0var_:
            r0 = 52
            if (r12 == r0) goto L_0x11a7
            r0 = 56
            if (r12 == r0) goto L_0x11a7
            r0 = 57
            if (r12 == r0) goto L_0x11a7
            r0 = 58
            if (r12 == r0) goto L_0x11a7
            r0 = 59
            if (r12 == r0) goto L_0x11a7
            if (r12 != r14) goto L_0x0var_
            goto L_0x11a7
        L_0x0var_:
            r0 = 54
            if (r12 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624741(0x7f0e0325, float:1.887667E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558477(0x7f0d004d, float:1.874227E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r1.timeLeft = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x1228
        L_0x0var_:
            r0 = 55
            if (r12 != r0) goto L_0x0fbf
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624740(0x7f0e0324, float:1.8876668E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558476(0x7f0d004c, float:1.8742269E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r1.timeLeft = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
            goto L_0x1228
        L_0x0fbf:
            r0 = 41
            if (r12 != r0) goto L_0x1068
            if (r5 != 0) goto L_0x1039
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r4 = (long) r0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x0fe5
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625829(0x7f0e0765, float:1.8878877E38)
            java.lang.String r3 = "InvLinkToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x105c
        L_0x0fe5:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x1010
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625828(0x7f0e0764, float:1.8878875E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r0 = r0.title
            r4[r8] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x105c
        L_0x1010:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625830(0x7f0e0766, float:1.887888E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r8] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x105c
        L_0x1039:
            r0 = r5
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625827(0x7f0e0763, float:1.8878873E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r5, r0)
            r4[r8] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x105c:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r0.setAnimation(r2, r15, r15)
            r1.timeLeft = r10
            goto L_0x1228
        L_0x1068:
            r0 = 53
            if (r12 != r0) goto L_0x1228
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r5 != 0) goto L_0x1153
            int r4 = r1.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r4 = (long) r4
            int r12 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r12 != 0) goto L_0x10b5
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x1097
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625655(0x7f0e06b7, float:1.8878524E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x10a9
        L_0x1097:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625659(0x7f0e06bb, float:1.8878532E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x10a9:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558474(0x7f0d004a, float:1.8742265E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x11a3
        L_0x10b5:
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x10ff
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x10e6
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625654(0x7f0e06b6, float:1.8878522E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = r2.title
            r4[r8] = r2
            java.lang.String r2 = "FwdMessageToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1148
        L_0x10e6:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625658(0x7f0e06ba, float:1.887853E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = r2.title
            r4[r8] = r2
            java.lang.String r2 = "FwdMessagesToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1148
        L_0x10ff:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x112e
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessageToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1148
        L_0x112e:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625660(0x7f0e06bc, float:1.8878534E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessagesToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x1148:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558436(0x7f0d0024, float:1.8742188E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x11a3
        L_0x1153:
            r2 = r5
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x117d
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625653(0x7f0e06b5, float:1.887852E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessageToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1199
        L_0x117d:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625657(0x7f0e06b9, float:1.8878528E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r5 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r4[r8] = r2
            java.lang.String r2 = "FwdMessagesToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x1199:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558436(0x7f0d0024, float:1.8742188E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x11a3:
            r1.timeLeft = r10
            goto L_0x1228
        L_0x11a7:
            r0 = 2131558424(0x7f0d0018, float:1.8742163E38)
            if (r12 != r14) goto L_0x11bb
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626893(0x7f0e0b8d, float:1.8881035E38)
            java.lang.String r4 = "PhoneCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1218
        L_0x11bb:
            r2 = 56
            if (r12 != r2) goto L_0x11ce
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627902(0x7f0e0f7e, float:1.8883082E38)
            java.lang.String r4 = "UsernameCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1218
        L_0x11ce:
            r2 = 57
            if (r12 != r2) goto L_0x11e1
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625739(0x7f0e070b, float:1.8878694E38)
            java.lang.String r4 = "HashtagCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1218
        L_0x11e1:
            r2 = 52
            if (r12 != r2) goto L_0x11f4
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626117(0x7f0e0885, float:1.8879461E38)
            java.lang.String r4 = "MessageCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1218
        L_0x11f4:
            r2 = 59
            if (r12 != r2) goto L_0x120a
            r0 = 2131558521(0x7f0d0079, float:1.874236E38)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625947(0x7f0e07db, float:1.8879116E38)
            java.lang.String r4 = "LinkCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1218
        L_0x120a:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627687(0x7f0e0ea7, float:1.8882645E38)
            java.lang.String r4 = "TextCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x1218:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 30
            r2.setAnimation(r0, r3, r3)
            r1.timeLeft = r10
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r6, r2)
        L_0x1228:
            r0 = 0
        L_0x1229:
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
            r7.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r7.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.playAnimation()
        L_0x1257:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r1.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r1.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x1284
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r9 = r3.toString()
        L_0x1284:
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r20.isMultilineSubInfo()
            if (r2 == 0) goto L_0x12d9
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x12a4
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x12a4:
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
            goto L_0x137d
        L_0x12d9:
            boolean r2 = r20.hasSubInfo()
            if (r2 == 0) goto L_0x12e9
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x137d
        L_0x12e9:
            android.view.ViewParent r2 = r20.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x137d
            android.view.ViewParent r2 = r20.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x130b
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x130b:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r1.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r8)
            r7 = 0
            r21 = r20
            r22 = r2
            r23 = r3
            r24 = r4
            r25 = r5
            r26 = r7
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r2 = r1.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r1.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x1347
            r4 = 17
            if (r3 == r4) goto L_0x1347
            r4 = 18
            if (r3 != r4) goto L_0x1344
            goto L_0x1347
        L_0x1344:
            r10 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1349
        L_0x1347:
            r10 = 1096810496(0x41600000, float:14.0)
        L_0x1349:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = r2 + r3
            r1.undoViewHeight = r2
            int r3 = r1.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x1363
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x137d
        L_0x1363:
            r4 = 25
            if (r3 != r4) goto L_0x1374
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x137d
        L_0x1374:
            if (r0 == 0) goto L_0x137d
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r0
            r1.undoViewHeight = r2
        L_0x137d:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x13e5
            r1.setVisibility(r8)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x138d
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x138f
        L_0x138d:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x138f:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setTranslationY(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r6]
            android.util.Property r3 = android.view.View.TRANSLATION_Y
            r4 = 2
            float[] r4 = new float[r4]
            boolean r5 = r1.fromTop
            if (r5 == 0) goto L_0x13af
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x13b1
        L_0x13af:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x13b1:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r1.undoViewHeight
            int r7 = r7 + r9
            float r7 = (float) r7
            float r5 = r5 * r7
            r4[r8] = r5
            boolean r5 = r1.fromTop
            if (r5 == 0) goto L_0x13c4
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x13c6
        L_0x13c4:
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x13c6:
            float r7 = r1.additionalTranslationY
            float r5 = r5 * r7
            r4[r6] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r1, r3, r4)
            r2[r8] = r3
            r0.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x13e5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(long, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWithAction$5 */
    public /* synthetic */ void lambda$showWithAction$5$UndoView(TLRPC$Message tLRPC$Message, View view) {
        hide(true, 1);
        TLRPC$TL_payments_getPaymentReceipt tLRPC$TL_payments_getPaymentReceipt = new TLRPC$TL_payments_getPaymentReceipt();
        tLRPC$TL_payments_getPaymentReceipt.msg_id = tLRPC$Message.id;
        tLRPC$TL_payments_getPaymentReceipt.peer = this.parentFragment.getMessagesController().getInputPeer(tLRPC$Message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_getPaymentReceipt, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                UndoView.this.lambda$null$4$UndoView(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$UndoView(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UndoView.this.lambda$null$3$UndoView(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$UndoView(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject));
        }
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
