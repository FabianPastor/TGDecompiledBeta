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
        this.timeReplaceProgress = 1.0f;
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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40;
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

    /* JADX WARNING: Removed duplicated region for block: B:377:0x10d6  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x10fd  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x1142  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x11ec  */
    /* JADX WARNING: Removed duplicated region for block: B:424:? A[RETURN, SYNTHETIC] */
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
            r7 = 2131627724(0x7f0e0ecc, float:1.888272E38)
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
            r11 = 1095761920(0x41500000, float:13.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r11
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
            java.lang.String r14 = ""
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r15 = 30
            r10 = 2
            r19 = r14
            r13 = 3000(0xbb8, double:1.482E-320)
            r8 = 36
            if (r12 == 0) goto L_0x05ea
            r11 = 74
            r12 = 0
            if (r3 != r11) goto L_0x00de
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            r1 = 2131627114(0x7f0e0c6a, float:1.8881483E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131627123(0x7f0e0CLASSNAME, float:1.8881502E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r15 = 2131558438(0x7f0d0026, float:1.8742192E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r0.timeLeft = r2
            goto L_0x0559
        L_0x00de:
            r11 = 34
            if (r3 != r11) goto L_0x0121
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627961(0x7f0e0fb9, float:1.8883201E38)
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
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForUser(r1, r7)
            java.lang.String r15 = "50_50"
            r4.setImage((org.telegram.messenger.ImageLocation) r11, (java.lang.String) r15, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r1)
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            r1.setVisibility(r7)
            r0.timeLeft = r13
            r1 = r2
        L_0x011e:
            r15 = 0
            goto L_0x0559
        L_0x0121:
            r11 = 37
            if (r3 != r11) goto L_0x017d
            org.telegram.ui.Components.AvatarDrawable r1 = new org.telegram.ui.Components.AvatarDrawable
            r1.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x0151
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r1.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUser(r2, r7)
            java.lang.String r11 = "50_50"
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r11, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r2)
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            goto L_0x0164
        L_0x0151:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForChat(r2, r7)
            java.lang.String r11 = "50_50"
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r11, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r2)
            java.lang.String r1 = r2.title
        L_0x0164:
            r2 = 2131628005(0x7f0e0fe5, float:1.888329E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            org.telegram.ui.Components.BackupImageView r2 = r0.avatarImageView
            r2.setVisibility(r7)
            r0.timeLeft = r13
            goto L_0x011e
        L_0x017d:
            r11 = 33
            if (r3 != r11) goto L_0x0191
            r1 = 2131627944(0x7f0e0fa8, float:1.8883167E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15 = 2131558506(0x7f0d006a, float:1.874233E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x0191:
            if (r3 != r15) goto L_0x01bc
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x019f
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x01a4
        L_0x019f:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x01a4:
            r2 = 2131628003(0x7f0e0fe3, float:1.8883286E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558507(0x7f0d006b, float:1.8742332E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x01bc:
            r11 = 35
            if (r3 != r11) goto L_0x01e9
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x01cc
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x01d1
        L_0x01cc:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x01d1:
            r2 = 2131628004(0x7f0e0fe4, float:1.8883288E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558507(0x7f0d006b, float:1.8742332E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x01e9:
            r11 = 31
            if (r3 != r11) goto L_0x0216
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x01f9
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x01fe
        L_0x01f9:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x01fe:
            r2 = 2131628001(0x7f0e0fe1, float:1.8883282E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558510(0x7f0d006e, float:1.8742338E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x0216:
            r11 = 38
            if (r3 != r11) goto L_0x0249
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r1 == 0) goto L_0x0235
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            r2 = 2131628011(0x7f0e0feb, float:1.8883303E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0242
        L_0x0235:
            r1 = 2131628010(0x7f0e0fea, float:1.88833E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0242:
            r15 = 2131558500(0x7f0d0064, float:1.8742318E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x0249:
            int r11 = r0.currentAction
            r15 = 39
            if (r11 != r15) goto L_0x0263
            r1 = 2131627939(0x7f0e0fa3, float:1.8883157E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558508(0x7f0d006c, float:1.8742334E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x0263:
            r15 = 40
            if (r11 != r15) goto L_0x027b
            r1 = 2131627938(0x7f0e0fa2, float:1.8883155E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558508(0x7f0d006c, float:1.8742334E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x027b:
            if (r3 != r8) goto L_0x02a6
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x0289
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x028e
        L_0x0289:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x028e:
            r2 = 2131628002(0x7f0e0fe2, float:1.8883284E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558510(0x7f0d006e, float:1.8742338E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x02a6:
            r15 = 32
            if (r3 != r15) goto L_0x02d3
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x02b6
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x02bb
        L_0x02b6:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x02bb:
            r2 = 2131627986(0x7f0e0fd2, float:1.8883252E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558505(0x7f0d0069, float:1.8742328E38)
            r0.timeLeft = r13
            goto L_0x0559
        L_0x02d3:
            r13 = 9
            if (r3 == r13) goto L_0x0524
            r13 = 10
            if (r3 != r13) goto L_0x02dd
            goto L_0x0524
        L_0x02dd:
            if (r3 != r9) goto L_0x02f5
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131626471(0x7f0e09e7, float:1.888018E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x0556
        L_0x02f5:
            r13 = 22
            if (r3 != r13) goto L_0x0361
            r13 = 0
            int r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r3 <= 0) goto L_0x0317
            if (r4 != 0) goto L_0x030c
            r1 = 2131625998(0x7f0e080e, float:1.887922E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x030c:
            r1 = 2131625999(0x7f0e080f, float:1.8879222E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x0317:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0349
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0349
            if (r4 != 0) goto L_0x033e
            r1 = 2131625994(0x7f0e080a, float:1.8879212E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x033e:
            r1 = 2131625995(0x7f0e080b, float:1.8879214E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x0349:
            if (r4 != 0) goto L_0x0356
            r1 = 2131625996(0x7f0e080c, float:1.8879216E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x0356:
            r1 = 2131625997(0x7f0e080d, float:1.8879218E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x0361:
            r13 = 23
            if (r3 != r13) goto L_0x0370
            r1 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0556
        L_0x0370:
            r13 = 6
            if (r3 != r13) goto L_0x038c
            r1 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624291(0x7f0e0163, float:1.8875758E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r8 = 48
            goto L_0x0559
        L_0x038c:
            r13 = 13
            if (r11 != r13) goto L_0x03a9
            r1 = 2131627042(0x7f0e0CLASSNAME, float:1.8881337E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131627043(0x7f0e0CLASSNAME, float:1.888134E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558512(0x7f0d0070, float:1.8742342E38)
        L_0x03a5:
            r8 = 44
            goto L_0x0559
        L_0x03a9:
            r13 = 14
            if (r11 != r13) goto L_0x03c3
            r1 = 2131627044(0x7f0e0CLASSNAME, float:1.8881341E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558513(0x7f0d0071, float:1.8742344E38)
            goto L_0x03a5
        L_0x03c3:
            r11 = 7
            if (r3 != r11) goto L_0x03ec
            r1 = 2131624298(0x7f0e016a, float:1.8875772E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x03e7
            r2 = 2131624299(0x7f0e016b, float:1.8875774E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x03e6:
            r12 = r2
        L_0x03e7:
            r15 = 2131558417(0x7f0d0011, float:1.874215E38)
            goto L_0x0559
        L_0x03ec:
            r11 = 20
            if (r3 == r11) goto L_0x042a
            r11 = 21
            if (r3 != r11) goto L_0x03f5
            goto L_0x042a
        L_0x03f5:
            r1 = 19
            if (r3 != r1) goto L_0x03fc
            java.lang.CharSequence r1 = r0.infoText
            goto L_0x03e7
        L_0x03fc:
            r1 = 3
            if (r3 != r1) goto L_0x0409
            r1 = 2131624790(0x7f0e0356, float:1.887677E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0412
        L_0x0409:
            r1 = 2131624827(0x7f0e037b, float:1.8876845E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x0412:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x03e7
            r2 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x03e6
        L_0x042a:
            r11 = r25
            org.telegram.messenger.MessagesController$DialogFilter r11 = (org.telegram.messenger.MessagesController.DialogFilter) r11
            r13 = 0
            int r15 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r15 == 0) goto L_0x04da
            int r4 = (int) r1
            if (r4 != 0) goto L_0x044b
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r13 = 32
            long r1 = r1 >> r13
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            int r4 = r1.user_id
        L_0x044b:
            if (r4 <= 0) goto L_0x0495
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r2 = 20
            if (r3 != r2) goto L_0x047a
            r2 = 2131625534(0x7f0e063e, float:1.8878279E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterUserAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x047a:
            r2 = 2131625535(0x7f0e063f, float:1.887828E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterUserRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x0495:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            r2 = 20
            if (r3 != r2) goto L_0x04c1
            r2 = 2131625473(0x7f0e0601, float:1.8878155E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterChatAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x04c1:
            r2 = 2131625474(0x7f0e0602, float:1.8878157E38)
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterChatRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x04da:
            r1 = 20
            if (r3 != r1) goto L_0x0501
            r1 = 2131625477(0x7f0e0605, float:1.8878163E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r2[r7] = r3
            java.lang.String r3 = r11.name
            r2[r5] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x0501:
            r1 = 2131625478(0x7f0e0606, float:1.8878165E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r2[r7] = r3
            java.lang.String r3 = r11.name
            r2[r5] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x0524:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 9
            if (r3 != r2) goto L_0x0541
            r2 = 2131625228(0x7f0e050c, float:1.8877658E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0556
        L_0x0541:
            r2 = 2131625229(0x7f0e050d, float:1.887766E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0556:
            r15 = 2131558421(0x7f0d0015, float:1.8742157E38)
        L_0x0559:
            android.widget.TextView r2 = r0.infoTextView
            r2.setText(r1)
            if (r15 == 0) goto L_0x0576
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r15, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x057b
        L_0x0576:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r9)
        L_0x057b:
            if (r12 == 0) goto L_0x05ba
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
            goto L_0x05e1
        L_0x05ba:
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
        L_0x05e1:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
        L_0x05e6:
            r14 = r19
            goto L_0x0CLASSNAME
        L_0x05ea:
            int r12 = r0.currentAction
            r15 = 45
            r13 = 60
            if (r12 == r15) goto L_0x0c7d
            r14 = 46
            if (r12 == r14) goto L_0x0c7d
            r14 = 47
            if (r12 == r14) goto L_0x0c7d
            r14 = 51
            if (r12 == r14) goto L_0x0c7d
            r14 = 50
            if (r12 == r14) goto L_0x0c7d
            r14 = 52
            if (r12 == r14) goto L_0x0c7d
            r14 = 53
            if (r12 == r14) goto L_0x0c7d
            r14 = 54
            if (r12 == r14) goto L_0x0c7d
            r14 = 55
            if (r12 == r14) goto L_0x0c7d
            r14 = 56
            if (r12 == r14) goto L_0x0c7d
            r14 = 57
            if (r12 == r14) goto L_0x0c7d
            r14 = 58
            if (r12 == r14) goto L_0x0c7d
            r14 = 59
            if (r12 == r14) goto L_0x0c7d
            if (r12 == r13) goto L_0x0c7d
            r14 = 71
            if (r12 == r14) goto L_0x0c7d
            r14 = 70
            if (r12 == r14) goto L_0x0c7d
            r14 = 75
            if (r12 == r14) goto L_0x0c7d
            r14 = 76
            if (r12 != r14) goto L_0x0636
            goto L_0x0c7d
        L_0x0636:
            r13 = 24
            if (r12 == r13) goto L_0x0b1a
            r13 = 25
            if (r12 != r13) goto L_0x0640
            goto L_0x0b1a
        L_0x0640:
            r13 = 11
            if (r12 != r13) goto L_0x06ae
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624418(0x7f0e01e2, float:1.8876015E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 2131558421(0x7f0d0015, float:1.8742157E38)
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
            goto L_0x05e6
        L_0x06ae:
            r13 = 15
            if (r12 != r13) goto L_0x0783
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625470(0x7f0e05fe, float:1.8878149E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558427(0x7f0d001b, float:1.874217E38)
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
            r1 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r3 = r1.indexOf(r3)
            r4 = 42
            int r1 = r1.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0751
            if (r1 < 0) goto L_0x0751
            if (r3 == r1) goto L_0x0751
            int r4 = r1 + 1
            r14 = r19
            r2.replace(r1, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r4.<init>(r6)
            int r1 = r1 - r5
            r6 = 33
            r2.setSpan(r4, r3, r1, r6)
            goto L_0x0753
        L_0x0751:
            r14 = r19
        L_0x0753:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setMaxLines(r10)
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
            goto L_0x0CLASSNAME
        L_0x0783:
            r14 = r19
            r13 = 16
            if (r12 == r13) goto L_0x09ab
            r13 = 17
            if (r12 != r13) goto L_0x078f
            goto L_0x09ab
        L_0x078f:
            r13 = 18
            if (r12 != r13) goto L_0x080d
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
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x0CLASSNAME
        L_0x080d:
            r4 = 12
            if (r12 != r4) goto L_0x08a7
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131166072(0x7var_, float:1.794638E38)
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
            r1 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r3 = r1.indexOf(r3)
            r4 = 42
            int r1 = r1.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0882
            if (r1 < 0) goto L_0x0882
            if (r3 == r1) goto L_0x0882
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
        L_0x0882:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setMaxLines(r10)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r9)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x0CLASSNAME
        L_0x08a7:
            if (r12 == r10) goto L_0x094a
            r4 = 4
            if (r12 != r4) goto L_0x08ae
            goto L_0x094a
        L_0x08ae:
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
            if (r3 != 0) goto L_0x08f0
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625737(0x7f0e0709, float:1.887869E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0938
        L_0x08f0:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x092a
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x091b
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x091b
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624700(0x7f0e02fc, float:1.8876587E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0938
        L_0x091b:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0938
        L_0x092a:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624794(0x7f0e035a, float:1.8876778E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x0938:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x0944
            r4 = 1
            goto L_0x0945
        L_0x0944:
            r4 = 0
        L_0x0945:
            r3.addDialogAction(r1, r4)
            goto L_0x0CLASSNAME
        L_0x094a:
            if (r3 != r10) goto L_0x095b
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624790(0x7f0e0356, float:1.887677E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0969
        L_0x095b:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624827(0x7f0e037b, float:1.8876845E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0969:
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
            r2 = 2131558415(0x7f0d000f, float:1.8742145E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x0CLASSNAME
        L_0x09ab:
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
            if (r2 == 0) goto L_0x09ef
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625155(0x7f0e04c3, float:1.887751E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165395(0x7var_d3, float:1.7945006E38)
            r1.setImageResource(r2)
            goto L_0x0a97
        L_0x09ef:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0a0c
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625030(0x7f0e0446, float:1.8877256E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0a09:
            r8 = 1096810496(0x41600000, float:14.0)
            goto L_0x0a65
        L_0x0a0c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0a3f
            android.widget.TextView r3 = r0.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r12, r7)
            r3.setText(r2)
            goto L_0x0a09
        L_0x0a3f:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625154(0x7f0e04c2, float:1.8877508E38)
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
        L_0x0a65:
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
        L_0x0a97:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131627297(0x7f0e0d21, float:1.8881854E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x0ae7
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
            goto L_0x0af5
        L_0x0ae7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r9)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r9)
        L_0x0af5:
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
            goto L_0x0CLASSNAME
        L_0x0b1a:
            r14 = r19
            r1 = r4
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r25
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            android.widget.ImageView r3 = r0.undoImageView
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r7)
            java.lang.String r3 = "undo_infoColor"
            if (r1 == 0) goto L_0x0be6
            android.widget.TextView r4 = r0.infoTextView
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r4.setTypeface(r8)
            android.widget.TextView r4 = r0.infoTextView
            r8 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r5, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r11 = "BODY.**"
            r4.setLayerColor(r11, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r11 = "Wibe Big.**"
            r4.setLayerColor(r11, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r11 = "Wibe Big 3.**"
            r4.setLayerColor(r11, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r8 = "Wibe Small.**"
            r4.setLayerColor(r8, r3)
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131626998(0x7f0e0bf6, float:1.8881248E38)
            java.lang.String r8 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r3.setText(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r4 = 2131558447(0x7f0d002f, float:1.874221E38)
            r8 = 28
            r11 = 28
            r3.setAnimation(r4, r8, r11)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setSingleLine(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 3
            r3.setMaxLines(r4)
            if (r2 == 0) goto L_0x0bc2
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 2131627000(0x7f0e0bf8, float:1.8881252E38)
            java.lang.Object[] r8 = new java.lang.Object[r10]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r8[r7] = r2
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r10)
            r8[r5] = r1
            java.lang.String r1 = "ProximityAlertSetInfoUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            r3.setText(r1)
            goto L_0x0bd9
        L_0x0bc2:
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 2131626999(0x7f0e0bf7, float:1.888125E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r10)
            r4[r7] = r1
            java.lang.String r1 = "ProximityAlertSetInfoGroup2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r2.setText(r1)
        L_0x0bd9:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            goto L_0x0CLASSNAME
        L_0x0be6:
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "Body Main.**"
            r1.setLayerColor(r4, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "Body Top.**"
            r1.setLayerColor(r4, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "Line.**"
            r1.setLayerColor(r4, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "Curve Big.**"
            r1.setLayerColor(r4, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r3 = "Curve Small.**"
            r1.setLayerColor(r3, r2)
            r1 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r2
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131626997(0x7f0e0bf5, float:1.8881246E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558442(0x7f0d002a, float:1.87422E38)
            r3 = 28
            r4 = 28
            r1.setAnimation(r2, r3, r4)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r9)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r1
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
        L_0x0CLASSNAME:
            r1 = 0
            r3 = 1096810496(0x41600000, float:14.0)
            goto L_0x10c0
        L_0x0c7d:
            r14 = r19
            android.widget.ImageView r11 = r0.undoImageView
            r11.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r11 = r0.leftImageView
            r11.setVisibility(r7)
            android.widget.TextView r11 = r0.infoTextView
            android.graphics.Typeface r12 = android.graphics.Typeface.DEFAULT
            r11.setTypeface(r12)
            int r11 = r0.currentAction
            r12 = 76
            r15 = 1091567616(0x41100000, float:9.0)
            if (r11 != r12) goto L_0x0cc0
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624590(0x7f0e028e, float:1.8876364E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r1.setAnimation(r2, r8, r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
        L_0x0cbb:
            r1 = 1
            r3 = 1096810496(0x41600000, float:14.0)
            goto L_0x1094
        L_0x0cc0:
            r12 = 75
            if (r11 != r12) goto L_0x0ce8
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625669(0x7f0e06c5, float:1.8878552E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r1.setAnimation(r2, r8, r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            goto L_0x0cbb
        L_0x0ce8:
            r12 = 70
            if (r3 != r12) goto L_0x0d5a
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r1 = r25
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.widget.TextView r2 = r0.subinfoTextView
            r2.setSingleLine(r7)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r1 <= r2) goto L_0x0d0c
            r2 = 86400(0x15180, float:1.21072E-40)
            int r1 = r1 / r2
            java.lang.String r2 = "Days"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d29
        L_0x0d0c:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r1 < r2) goto L_0x0d19
            int r1 = r1 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d29
        L_0x0d19:
            if (r1 < r13) goto L_0x0d23
            int r1 = r1 / r13
            java.lang.String r2 = "Minutes"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d29
        L_0x0d23:
            java.lang.String r2 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
        L_0x0d29:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624431(0x7f0e01ef, float:1.8876042E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r1
            java.lang.String r1 = "AutoDeleteHintOnText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r2.setText(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558431(0x7f0d001f, float:1.8742178E38)
            r1.setAnimation(r2, r8, r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            r1 = 4000(0xfa0, double:1.9763E-320)
            r0.timeLeft = r1
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r7, r7, r7, r2)
            goto L_0x0cbb
        L_0x0d5a:
            r3 = 71
            if (r11 != r3) goto L_0x0d8e
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624430(0x7f0e01ee, float:1.887604E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r1.setAnimation(r2, r8, r8)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r7, r7, r7, r2)
            r3 = 1096810496(0x41600000, float:14.0)
            goto L_0x1093
        L_0x0d8e:
            r3 = 45
            if (r11 != r3) goto L_0x0db7
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625778(0x7f0e0732, float:1.8878774E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r1.setAnimation(r2, r8, r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            goto L_0x0cbb
        L_0x0db7:
            r3 = 46
            if (r11 != r3) goto L_0x0de0
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625779(0x7f0e0733, float:1.8878776E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r1.setAnimation(r2, r8, r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            goto L_0x0cbb
        L_0x0de0:
            r3 = 47
            if (r11 != r3) goto L_0x0e15
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625785(0x7f0e0739, float:1.8878788E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r7, r7, r7, r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r3)
            r1 = 1
            goto L_0x1094
        L_0x0e15:
            r3 = 1096810496(0x41600000, float:14.0)
            r12 = 51
            if (r11 != r12) goto L_0x0e3e
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r4 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x1093
        L_0x0e3e:
            r12 = 50
            if (r11 != r12) goto L_0x0e65
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x1093
        L_0x0e65:
            r8 = 52
            if (r11 == r8) goto L_0x1010
            r8 = 56
            if (r11 == r8) goto L_0x1010
            r8 = 57
            if (r11 == r8) goto L_0x1010
            r8 = 58
            if (r11 == r8) goto L_0x1010
            r8 = 59
            if (r11 == r8) goto L_0x1010
            if (r11 != r13) goto L_0x0e7d
            goto L_0x1010
        L_0x0e7d:
            r8 = 54
            if (r11 != r8) goto L_0x0ea6
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624735(0x7f0e031f, float:1.8876658E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558472(0x7f0d0048, float:1.874226E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x1093
        L_0x0ea6:
            r8 = 55
            if (r11 != r8) goto L_0x0ecf
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624734(0x7f0e031e, float:1.8876656E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558471(0x7f0d0047, float:1.8742259E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x1093
        L_0x0ecf:
            r8 = 53
            if (r11 != r8) goto L_0x1093
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r25 != 0) goto L_0x0fb9
            int r8 = r0.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            int r8 = r8.clientUserId
            long r11 = (long) r8
            int r8 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r8 != 0) goto L_0x0f1b
            int r1 = r4.intValue()
            if (r1 != r5) goto L_0x0efd
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625641(0x7f0e06a9, float:1.8878496E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x0f0f
        L_0x0efd:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625645(0x7f0e06ad, float:1.8878504E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x0f0f:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558469(0x7f0d0045, float:1.8742255E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            goto L_0x100a
        L_0x0f1b:
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x0var_
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r4.intValue()
            if (r2 != r5) goto L_0x0f4c
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625640(0x7f0e06a8, float:1.8878494E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r8[r7] = r1
            java.lang.String r1 = "FwdMessageToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0fae
        L_0x0f4c:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625644(0x7f0e06ac, float:1.8878502E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r8[r7] = r1
            java.lang.String r1 = "FwdMessagesToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0fae
        L_0x0var_:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r4.intValue()
            if (r2 != r5) goto L_0x0var_
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625642(0x7f0e06aa, float:1.8878498E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessageToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0fae
        L_0x0var_:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625646(0x7f0e06ae, float:1.8878506E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessagesToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x0fae:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            goto L_0x100a
        L_0x0fb9:
            r1 = r25
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r4.intValue()
            if (r2 != r5) goto L_0x0fe4
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625639(0x7f0e06a7, float:1.8878492E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r11 = "Chats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessageToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x1000
        L_0x0fe4:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625643(0x7f0e06ab, float:1.88785E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r11 = "Chats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessagesToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x1000:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
        L_0x100a:
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            goto L_0x1093
        L_0x1010:
            r1 = 2131558422(0x7f0d0016, float:1.874216E38)
            if (r11 != r13) goto L_0x1024
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131626853(0x7f0e0b65, float:1.8880954E38)
            java.lang.String r8 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1081
        L_0x1024:
            r2 = 56
            if (r11 != r2) goto L_0x1037
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131627821(0x7f0e0f2d, float:1.8882917E38)
            java.lang.String r8 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1081
        L_0x1037:
            r2 = 57
            if (r11 != r2) goto L_0x104a
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625724(0x7f0e06fc, float:1.8878664E38)
            java.lang.String r8 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1081
        L_0x104a:
            r2 = 52
            if (r11 != r2) goto L_0x105d
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131626089(0x7f0e0869, float:1.8879404E38)
            java.lang.String r8 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1081
        L_0x105d:
            r2 = 59
            if (r11 != r2) goto L_0x1073
            r1 = 2131558506(0x7f0d006a, float:1.874233E38)
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625920(0x7f0e07c0, float:1.8879062E38)
            java.lang.String r8 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1081
        L_0x1073:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131627607(0x7f0e0e57, float:1.8882483E38)
            java.lang.String r8 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
        L_0x1081:
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r4 = 30
            r2.setAnimation(r1, r4, r4)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
        L_0x1093:
            r1 = 0
        L_0x1094:
            android.widget.TextView r2 = r0.subinfoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r0.undoTextView
            java.lang.String r4 = "undo_cancelColor"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r9)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r4 = 0
            r2.setProgress(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r2.playAnimation()
        L_0x10c0:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r4 = r0.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r2.append(r4)
            android.widget.TextView r4 = r0.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x10ed
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = ". "
            r4.append(r6)
            android.widget.TextView r6 = r0.subinfoTextView
            java.lang.CharSequence r6 = r6.getText()
            r4.append(r6)
            java.lang.String r14 = r4.toString()
        L_0x10ed:
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r20.isMultilineSubInfo()
            if (r2 == 0) goto L_0x1142
            android.view.ViewParent r1 = r20.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x110d
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x110d:
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
            goto L_0x11e6
        L_0x1142:
            boolean r2 = r20.hasSubInfo()
            if (r2 == 0) goto L_0x1152
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x11e6
        L_0x1152:
            android.view.ViewParent r2 = r20.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x11e6
            android.view.ViewParent r2 = r20.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r4 = r2.getMeasuredWidth()
            int r6 = r2.getPaddingLeft()
            int r4 = r4 - r6
            int r2 = r2.getPaddingRight()
            int r4 = r4 - r2
            if (r4 > 0) goto L_0x1174
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r2.x
        L_0x1174:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r4 = r4 - r2
            android.widget.TextView r2 = r0.infoTextView
            r6 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r6)
            r6 = 0
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r9 = 0
            r21 = r20
            r22 = r2
            r23 = r4
            r24 = r6
            r25 = r8
            r26 = r9
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r2 = r0.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r4 = r0.currentAction
            r6 = 16
            if (r4 == r6) goto L_0x11b0
            r6 = 17
            if (r4 == r6) goto L_0x11b0
            r6 = 18
            if (r4 != r6) goto L_0x11ad
            goto L_0x11b0
        L_0x11ad:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x11b2
        L_0x11b0:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x11b2:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r2 = r2 + r3
            r0.undoViewHeight = r2
            int r3 = r0.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x11cc
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x11e6
        L_0x11cc:
            r4 = 25
            if (r3 != r4) goto L_0x11dd
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x11e6
        L_0x11dd:
            if (r1 == 0) goto L_0x11e6
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r1
            r0.undoViewHeight = r2
        L_0x11e6:
            int r1 = r20.getVisibility()
            if (r1 == 0) goto L_0x124d
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            if (r1 == 0) goto L_0x11f6
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x11f8
        L_0x11f6:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x11f8:
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
            float[] r4 = new float[r10]
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x1217
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1219
        L_0x1217:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x1219:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r0.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x122c
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x122e
        L_0x122c:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x122e:
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
        L_0x124d:
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
