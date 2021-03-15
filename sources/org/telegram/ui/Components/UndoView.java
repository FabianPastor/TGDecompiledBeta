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

    /* JADX WARNING: Removed duplicated region for block: B:393:0x1199  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x11c0  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x1205  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x12af  */
    /* JADX WARNING: Removed duplicated region for block: B:440:? A[RETURN, SYNTHETIC] */
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
            r7 = 2131627748(0x7f0e0ee4, float:1.888277E38)
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
            r10 = -2
            r6.height = r10
            r10 = 1095761920(0x41500000, float:13.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r6.topMargin = r10
            r6.bottomMargin = r7
            org.telegram.ui.Components.RLottieImageView r10 = r0.leftImageView
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r10.setScaleType(r11)
            org.telegram.ui.Components.RLottieImageView r10 = r0.leftImageView
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r10 = (android.widget.FrameLayout.LayoutParams) r10
            r11 = 19
            r10.gravity = r11
            r10.bottomMargin = r7
            r10.topMargin = r7
            r11 = 1077936128(0x40400000, float:3.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.leftMargin = r11
            r11 = 1113063424(0x42580000, float:54.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.width = r11
            r11 = -2
            r10.height = r11
            android.widget.TextView r11 = r0.infoTextView
            r11.setMinHeight(r7)
            boolean r11 = r20.isTooltipAction()
            java.lang.String r12 = "Chats"
            java.lang.String r14 = ""
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r15 = 30
            r19 = r14
            r13 = 3000(0xbb8, double:1.482E-320)
            r8 = 36
            if (r11 == 0) goto L_0x05fd
            r10 = 74
            r11 = 0
            if (r3 != r10) goto L_0x00df
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            r1 = 2131627124(0x7f0e0CLASSNAME, float:1.8881504E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131627133(0x7f0e0c7d, float:1.8881522E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r15 = 2131558437(0x7f0d0025, float:1.874219E38)
            r2 = 4000(0xfa0, double:1.9763E-320)
            r0.timeLeft = r2
            goto L_0x055c
        L_0x00df:
            r10 = 34
            if (r3 != r10) goto L_0x0122
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627985(0x7f0e0fd1, float:1.888325E38)
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
            java.lang.String r12 = "50_50"
            r4.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r12, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r1)
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            r1.setVisibility(r7)
            r0.timeLeft = r13
            r1 = r2
        L_0x011f:
            r15 = 0
            goto L_0x055c
        L_0x0122:
            r10 = 37
            if (r3 != r10) goto L_0x017e
            org.telegram.ui.Components.AvatarDrawable r1 = new org.telegram.ui.Components.AvatarDrawable
            r1.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x0152
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r1.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUser(r2, r7)
            java.lang.String r10 = "50_50"
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r10, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r2)
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            goto L_0x0165
        L_0x0152:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForChat(r2, r7)
            java.lang.String r10 = "50_50"
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r10, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r2)
            java.lang.String r1 = r2.title
        L_0x0165:
            r2 = 2131628031(0x7f0e0fff, float:1.8883343E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            org.telegram.ui.Components.BackupImageView r2 = r0.avatarImageView
            r2.setVisibility(r7)
            r0.timeLeft = r13
            goto L_0x011f
        L_0x017e:
            r10 = 33
            if (r3 != r10) goto L_0x0192
            r1 = 2131627968(0x7f0e0fc0, float:1.8883215E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x0192:
            if (r3 != r15) goto L_0x01bd
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x01a0
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x01a5
        L_0x01a0:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x01a5:
            r2 = 2131628029(0x7f0e0ffd, float:1.888334E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558513(0x7f0d0071, float:1.8742344E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x01bd:
            r10 = 35
            if (r3 != r10) goto L_0x01ea
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x01cd
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x01d2
        L_0x01cd:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x01d2:
            r2 = 2131628030(0x7f0e0ffe, float:1.8883341E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558513(0x7f0d0071, float:1.8742344E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x01ea:
            r10 = 31
            if (r3 != r10) goto L_0x0217
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x01fa
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x01ff
        L_0x01fa:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x01ff:
            r2 = 2131628027(0x7f0e0ffb, float:1.8883335E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558516(0x7f0d0074, float:1.874235E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x0217:
            r10 = 38
            if (r3 != r10) goto L_0x024a
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r1 == 0) goto L_0x0236
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            r2 = 2131628037(0x7f0e1005, float:1.8883355E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0243
        L_0x0236:
            r1 = 2131628036(0x7f0e1004, float:1.8883353E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0243:
            r15 = 2131558506(0x7f0d006a, float:1.874233E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x024a:
            int r10 = r0.currentAction
            r15 = 39
            if (r10 != r15) goto L_0x0264
            r1 = 2131627963(0x7f0e0fbb, float:1.8883205E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x0264:
            r15 = 40
            if (r10 != r15) goto L_0x027c
            r1 = 2131627962(0x7f0e0fba, float:1.8883203E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558468(0x7f0d0044, float:1.8742253E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x027c:
            if (r3 != r8) goto L_0x02a7
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x028a
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x028f
        L_0x028a:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x028f:
            r2 = 2131628028(0x7f0e0ffc, float:1.8883337E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558516(0x7f0d0074, float:1.874235E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x02a7:
            r15 = 32
            if (r3 != r15) goto L_0x02d4
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r1 == 0) goto L_0x02b7
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x02bc
        L_0x02b7:
            r1 = r4
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            java.lang.String r1 = r1.title
        L_0x02bc:
            r2 = 2131628012(0x7f0e0fec, float:1.8883305E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r15 = 2131558511(0x7f0d006f, float:1.874234E38)
            r0.timeLeft = r13
            goto L_0x055c
        L_0x02d4:
            r13 = 9
            if (r3 == r13) goto L_0x0527
            r13 = 10
            if (r3 != r13) goto L_0x02de
            goto L_0x0527
        L_0x02de:
            if (r3 != r9) goto L_0x02f6
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131626481(0x7f0e09f1, float:1.88802E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x0559
        L_0x02f6:
            r13 = 22
            if (r3 != r13) goto L_0x0362
            r12 = 0
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 <= 0) goto L_0x0318
            if (r4 != 0) goto L_0x030d
            r1 = 2131626008(0x7f0e0818, float:1.887924E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x030d:
            r1 = 2131626009(0x7f0e0819, float:1.8879242E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x0318:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x034a
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x034a
            if (r4 != 0) goto L_0x033f
            r1 = 2131626004(0x7f0e0814, float:1.8879232E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x033f:
            r1 = 2131626005(0x7f0e0815, float:1.8879234E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x034a:
            if (r4 != 0) goto L_0x0357
            r1 = 2131626006(0x7f0e0816, float:1.8879236E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x0357:
            r1 = 2131626007(0x7f0e0817, float:1.8879238E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x0362:
            r13 = 23
            if (r3 != r13) goto L_0x0371
            r1 = 2131624824(0x7f0e0378, float:1.8876839E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0559
        L_0x0371:
            r13 = 6
            if (r3 != r13) goto L_0x038d
            r1 = 2131624291(0x7f0e0163, float:1.8875758E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r8 = 48
            goto L_0x055c
        L_0x038d:
            r13 = 13
            if (r10 != r13) goto L_0x03aa
            r1 = 2131627052(0x7f0e0c2c, float:1.8881358E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131627053(0x7f0e0c2d, float:1.888136E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558518(0x7f0d0076, float:1.8742354E38)
        L_0x03a6:
            r8 = 44
            goto L_0x055c
        L_0x03aa:
            r13 = 14
            if (r10 != r13) goto L_0x03c4
            r1 = 2131627054(0x7f0e0c2e, float:1.8881362E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131627055(0x7f0e0c2f, float:1.8881364E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = 2131558519(0x7f0d0077, float:1.8742356E38)
            goto L_0x03a6
        L_0x03c4:
            r10 = 7
            if (r3 != r10) goto L_0x03ed
            r1 = 2131624299(0x7f0e016b, float:1.8875774E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x03e8
            r2 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x03e7:
            r11 = r2
        L_0x03e8:
            r15 = 2131558417(0x7f0d0011, float:1.874215E38)
            goto L_0x055c
        L_0x03ed:
            r10 = 20
            if (r3 == r10) goto L_0x042b
            r10 = 21
            if (r3 != r10) goto L_0x03f6
            goto L_0x042b
        L_0x03f6:
            r1 = 19
            if (r3 != r1) goto L_0x03fd
            java.lang.CharSequence r1 = r0.infoText
            goto L_0x03e8
        L_0x03fd:
            r1 = 3
            if (r3 != r1) goto L_0x040a
            r1 = 2131624794(0x7f0e035a, float:1.8876778E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0413
        L_0x040a:
            r1 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x0413:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x03e8
            r2 = 2131624795(0x7f0e035b, float:1.887678E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x03e7
        L_0x042b:
            r10 = r25
            org.telegram.messenger.MessagesController$DialogFilter r10 = (org.telegram.messenger.MessagesController.DialogFilter) r10
            r13 = 0
            int r15 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r15 == 0) goto L_0x04df
            int r4 = (int) r1
            if (r4 != 0) goto L_0x044c
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r12 = 32
            long r1 = r1 >> r12
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            int r4 = r1.user_id
        L_0x044c:
            if (r4 <= 0) goto L_0x0498
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r2 = 20
            if (r3 != r2) goto L_0x047c
            r2 = 2131625539(0x7f0e0643, float:1.8878289E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterUserAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x047c:
            r3 = 2
            r2 = 2131625540(0x7f0e0644, float:1.887829E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterUserRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x0498:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            r2 = 20
            if (r3 != r2) goto L_0x04c5
            r2 = 2131625478(0x7f0e0606, float:1.8878165E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterChatAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x04c5:
            r3 = 2
            r2 = 2131625479(0x7f0e0607, float:1.8878167E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterChatRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x04df:
            r1 = 20
            if (r3 != r1) goto L_0x0505
            r1 = 2131625482(0x7f0e060a, float:1.8878173E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r12, r2)
            r3[r7] = r2
            java.lang.String r2 = r10.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatsAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x0505:
            r1 = 2131625483(0x7f0e060b, float:1.8878175E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r12, r2)
            r3[r7] = r2
            java.lang.String r2 = r10.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatsRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x0527:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 9
            if (r3 != r2) goto L_0x0544
            r2 = 2131625233(0x7f0e0511, float:1.8877668E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0559
        L_0x0544:
            r2 = 2131625234(0x7f0e0512, float:1.887767E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0559:
            r15 = 2131558421(0x7f0d0015, float:1.8742157E38)
        L_0x055c:
            android.widget.TextView r2 = r0.infoTextView
            r2.setText(r1)
            if (r15 == 0) goto L_0x0589
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setAnimation(r15, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getAnimatedDrawable()
            r1.setPlayInDirectionOfCustomEndFrame(r7)
            int r2 = r1.getFramesCount()
            r1.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x058e
        L_0x0589:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r9)
        L_0x058e:
            if (r11 == 0) goto L_0x05cd
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
            r1.setText(r11)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            goto L_0x05f4
        L_0x05cd:
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
        L_0x05f4:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
        L_0x05f9:
            r14 = r19
            goto L_0x0CLASSNAME
        L_0x05fd:
            int r11 = r0.currentAction
            r15 = 45
            r13 = 60
            if (r11 == r15) goto L_0x0c9a
            r14 = 46
            if (r11 == r14) goto L_0x0c9a
            r14 = 47
            if (r11 == r14) goto L_0x0c9a
            r14 = 51
            if (r11 == r14) goto L_0x0c9a
            r14 = 50
            if (r11 == r14) goto L_0x0c9a
            r14 = 52
            if (r11 == r14) goto L_0x0c9a
            r14 = 53
            if (r11 == r14) goto L_0x0c9a
            r14 = 54
            if (r11 == r14) goto L_0x0c9a
            r14 = 55
            if (r11 == r14) goto L_0x0c9a
            r14 = 56
            if (r11 == r14) goto L_0x0c9a
            r14 = 57
            if (r11 == r14) goto L_0x0c9a
            r14 = 58
            if (r11 == r14) goto L_0x0c9a
            r14 = 59
            if (r11 == r14) goto L_0x0c9a
            if (r11 == r13) goto L_0x0c9a
            r14 = 71
            if (r11 == r14) goto L_0x0c9a
            r14 = 70
            if (r11 == r14) goto L_0x0c9a
            r14 = 75
            if (r11 == r14) goto L_0x0c9a
            r14 = 76
            if (r11 == r14) goto L_0x0c9a
            r14 = 41
            if (r11 != r14) goto L_0x064d
            goto L_0x0c9a
        L_0x064d:
            r12 = 24
            if (r11 == r12) goto L_0x0b35
            r12 = 25
            if (r11 != r12) goto L_0x0657
            goto L_0x0b35
        L_0x0657:
            r12 = 11
            if (r11 != r12) goto L_0x06c5
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624419(0x7f0e01e3, float:1.8876017E38)
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
            goto L_0x05f9
        L_0x06c5:
            r12 = 15
            if (r11 != r12) goto L_0x079b
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625475(0x7f0e0603, float:1.887816E38)
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
            r1 = 2131625474(0x7f0e0602, float:1.8878157E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r3 = r1.indexOf(r3)
            r4 = 42
            int r1 = r1.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0768
            if (r1 < 0) goto L_0x0768
            if (r3 == r1) goto L_0x0768
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
            goto L_0x076a
        L_0x0768:
            r14 = r19
        L_0x076a:
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
            goto L_0x0CLASSNAME
        L_0x079b:
            r14 = r19
            r12 = 16
            if (r11 == r12) goto L_0x09c6
            r12 = 17
            if (r11 != r12) goto L_0x07a7
            goto L_0x09c6
        L_0x07a7:
            r12 = 18
            if (r11 != r12) goto L_0x0825
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
            r10.gravity = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r10.bottomMargin = r1
            r10.topMargin = r1
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
        L_0x0825:
            r4 = 12
            if (r11 != r4) goto L_0x08c0
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624927(0x7f0e03df, float:1.8877048E38)
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
            r1 = 2131624928(0x7f0e03e0, float:1.887705E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r3 = r1.indexOf(r3)
            r4 = 42
            int r1 = r1.lastIndexOf(r4)
            if (r3 < 0) goto L_0x089a
            if (r1 < 0) goto L_0x089a
            if (r3 == r1) goto L_0x089a
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
        L_0x089a:
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
            goto L_0x0CLASSNAME
        L_0x08c0:
            r4 = 2
            if (r11 == r4) goto L_0x0964
            r4 = 4
            if (r11 != r4) goto L_0x08c8
            goto L_0x0964
        L_0x08c8:
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
            if (r3 != 0) goto L_0x090a
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625742(0x7f0e070e, float:1.88787E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0952
        L_0x090a:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x0944
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x0935
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0935
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624704(0x7f0e0300, float:1.8876595E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0952
        L_0x0935:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625695(0x7f0e06df, float:1.8878605E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0952
        L_0x0944:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624798(0x7f0e035e, float:1.8876786E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x0952:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x095e
            r4 = 1
            goto L_0x095f
        L_0x095e:
            r4 = 0
        L_0x095f:
            r3.addDialogAction(r1, r4)
            goto L_0x0CLASSNAME
        L_0x0964:
            r1 = 2
            if (r3 != r1) goto L_0x0976
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624794(0x7f0e035a, float:1.8876778E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0984
        L_0x0976:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0984:
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
        L_0x09c6:
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
            if (r2 == 0) goto L_0x0a0a
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625160(0x7f0e04c8, float:1.887752E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165395(0x7var_d3, float:1.7945006E38)
            r1.setImageResource(r2)
            goto L_0x0ab2
        L_0x0a0a:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0a27
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625035(0x7f0e044b, float:1.8877267E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0a24:
            r8 = 1096810496(0x41600000, float:14.0)
            goto L_0x0a80
        L_0x0a27:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0a5a
            android.widget.TextView r3 = r0.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r11, r7)
            r3.setText(r2)
            goto L_0x0a24
        L_0x0a5a:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625159(0x7f0e04c7, float:1.8877518E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r1
            java.lang.String r8 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4)
            android.widget.TextView r4 = r0.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r11, r7)
            r2.setText(r3)
        L_0x0a80:
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
            r10.leftMargin = r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r10.width = r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r10.height = r1
        L_0x0ab2:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131627308(0x7f0e0d2c, float:1.8881877E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x0b02
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
            goto L_0x0b10
        L_0x0b02:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r9)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r9)
        L_0x0b10:
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
        L_0x0b35:
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
            if (r1 == 0) goto L_0x0CLASSNAME
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
            java.lang.String r10 = "BODY.**"
            r4.setLayerColor(r10, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r10 = "Wibe Big.**"
            r4.setLayerColor(r10, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r10 = "Wibe Big 3.**"
            r4.setLayerColor(r10, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r8 = "Wibe Small.**"
            r4.setLayerColor(r8, r3)
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131627008(0x7f0e0CLASSNAME, float:1.8881268E38)
            java.lang.String r8 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r3.setText(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r4 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r8 = 28
            r10 = 28
            r3.setAnimation(r4, r8, r10)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setSingleLine(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 3
            r3.setMaxLines(r4)
            if (r2 == 0) goto L_0x0bde
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 2131627010(0x7f0e0CLASSNAME, float:1.8881272E38)
            r8 = 2
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r10[r7] = r2
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r8)
            r10[r5] = r1
            java.lang.String r1 = "ProximityAlertSetInfoUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r10)
            r3.setText(r1)
            goto L_0x0bf6
        L_0x0bde:
            r8 = 2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 2131627009(0x7f0e0CLASSNAME, float:1.888127E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r8)
            r4[r7] = r1
            java.lang.String r1 = "ProximityAlertSetInfoGroup2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r2.setText(r1)
        L_0x0bf6:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
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
            r2 = 2131627007(0x7f0e0bff, float:1.8881266E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558441(0x7f0d0029, float:1.8742198E38)
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
            goto L_0x1183
        L_0x0c9a:
            r14 = r19
            android.widget.ImageView r10 = r0.undoImageView
            r10.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r10 = r0.leftImageView
            r10.setVisibility(r7)
            android.widget.TextView r10 = r0.infoTextView
            android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
            r10.setTypeface(r11)
            int r10 = r0.currentAction
            r11 = 76
            r15 = 1091567616(0x41100000, float:9.0)
            if (r10 != r11) goto L_0x0cdd
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624591(0x7f0e028f, float:1.8876366E38)
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
        L_0x0cd8:
            r1 = 1
            r3 = 1096810496(0x41600000, float:14.0)
            goto L_0x1157
        L_0x0cdd:
            r11 = 75
            if (r10 != r11) goto L_0x0d05
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625674(0x7f0e06ca, float:1.8878563E38)
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
            goto L_0x0cd8
        L_0x0d05:
            r11 = 70
            if (r3 != r11) goto L_0x0d77
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r1 = r25
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.widget.TextView r2 = r0.subinfoTextView
            r2.setSingleLine(r7)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r1 <= r2) goto L_0x0d29
            r2 = 86400(0x15180, float:1.21072E-40)
            int r1 = r1 / r2
            java.lang.String r2 = "Days"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d46
        L_0x0d29:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r1 < r2) goto L_0x0d36
            int r1 = r1 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d46
        L_0x0d36:
            if (r1 < r13) goto L_0x0d40
            int r1 = r1 / r13
            java.lang.String r2 = "Minutes"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d46
        L_0x0d40:
            java.lang.String r2 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
        L_0x0d46:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624432(0x7f0e01f0, float:1.8876044E38)
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
            goto L_0x0cd8
        L_0x0d77:
            r3 = 71
            if (r10 != r3) goto L_0x0dab
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624431(0x7f0e01ef, float:1.8876042E38)
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
            goto L_0x1156
        L_0x0dab:
            r3 = 45
            if (r10 != r3) goto L_0x0dd4
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625783(0x7f0e0737, float:1.8878784E38)
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
            goto L_0x0cd8
        L_0x0dd4:
            r3 = 46
            if (r10 != r3) goto L_0x0dfd
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625784(0x7f0e0738, float:1.8878786E38)
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
            goto L_0x0cd8
        L_0x0dfd:
            r3 = 47
            if (r10 != r3) goto L_0x0e32
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625790(0x7f0e073e, float:1.8878798E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558452(0x7f0d0034, float:1.874222E38)
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
            goto L_0x1157
        L_0x0e32:
            r3 = 1096810496(0x41600000, float:14.0)
            r11 = 51
            if (r10 != r11) goto L_0x0e5b
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
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
            goto L_0x1156
        L_0x0e5b:
            r11 = 50
            if (r10 != r11) goto L_0x0e82
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624401(0x7f0e01d1, float:1.887598E38)
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
            goto L_0x1156
        L_0x0e82:
            r11 = 52
            if (r10 == r11) goto L_0x10d3
            r11 = 56
            if (r10 == r11) goto L_0x10d3
            r11 = 57
            if (r10 == r11) goto L_0x10d3
            r11 = 58
            if (r10 == r11) goto L_0x10d3
            r11 = 59
            if (r10 == r11) goto L_0x10d3
            if (r10 != r13) goto L_0x0e9a
            goto L_0x10d3
        L_0x0e9a:
            r11 = 54
            if (r10 != r11) goto L_0x0ec3
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624739(0x7f0e0323, float:1.8876666E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
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
            goto L_0x1156
        L_0x0ec3:
            r11 = 55
            if (r10 != r11) goto L_0x0eec
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624738(0x7f0e0322, float:1.8876664E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558470(0x7f0d0046, float:1.8742257E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
            goto L_0x1156
        L_0x0eec:
            r11 = 41
            if (r10 != r11) goto L_0x0var_
            if (r25 != 0) goto L_0x0var_
            int r4 = r0.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r10 = (long) r4
            int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r4 != 0) goto L_0x0var_
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625813(0x7f0e0755, float:1.8878845E38)
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x0var_
        L_0x0var_:
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x0f3d
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625812(0x7f0e0754, float:1.8878843E38)
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r10[r7] = r1
            java.lang.String r1 = "InvLinkToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r10)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0var_
        L_0x0f3d:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r10[r7] = r1
            java.lang.String r1 = "InvLinkToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r10)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0var_
        L_0x0var_:
            r1 = r25
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
            r10[r7] = r1
            java.lang.String r1 = "InvLinkToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r10)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x0var_:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            goto L_0x1156
        L_0x0var_:
            r8 = 53
            if (r10 != r8) goto L_0x1156
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r25 != 0) goto L_0x1080
            int r8 = r0.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            int r8 = r8.clientUserId
            long r10 = (long) r8
            int r8 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r8 != 0) goto L_0x0fe2
            int r1 = r4.intValue()
            if (r1 != r5) goto L_0x0fc4
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625646(0x7f0e06ae, float:1.8878506E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x0fd6
        L_0x0fc4:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625650(0x7f0e06b2, float:1.8878514E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x0fd6:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558468(0x7f0d0044, float:1.8742253E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            goto L_0x10cd
        L_0x0fe2:
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x102c
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r4.intValue()
            if (r2 != r5) goto L_0x1013
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625645(0x7f0e06ad, float:1.8878504E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r8[r7] = r1
            java.lang.String r1 = "FwdMessageToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x1075
        L_0x1013:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625649(0x7f0e06b1, float:1.8878512E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r8[r7] = r1
            java.lang.String r1 = "FwdMessagesToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x1075
        L_0x102c:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r4.intValue()
            if (r2 != r5) goto L_0x105b
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625647(0x7f0e06af, float:1.8878508E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessageToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x1075
        L_0x105b:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625651(0x7f0e06b3, float:1.8878516E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessagesToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x1075:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
            goto L_0x10cd
        L_0x1080:
            r1 = r25
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r4.intValue()
            if (r2 != r5) goto L_0x10a9
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625644(0x7f0e06ac, float:1.8878502E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessageToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x10c3
        L_0x10a9:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625648(0x7f0e06b0, float:1.887851E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
            r8[r7] = r1
            java.lang.String r1 = "FwdMessagesToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x10c3:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r4 = 30
            r1.setAnimation(r2, r4, r4)
        L_0x10cd:
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            goto L_0x1156
        L_0x10d3:
            r1 = 2131558422(0x7f0d0016, float:1.874216E38)
            if (r10 != r13) goto L_0x10e7
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131626863(0x7f0e0b6f, float:1.8880974E38)
            java.lang.String r8 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1144
        L_0x10e7:
            r2 = 56
            if (r10 != r2) goto L_0x10fa
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131627845(0x7f0e0var_, float:1.8882966E38)
            java.lang.String r8 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1144
        L_0x10fa:
            r2 = 57
            if (r10 != r2) goto L_0x110d
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625729(0x7f0e0701, float:1.8878674E38)
            java.lang.String r8 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1144
        L_0x110d:
            r2 = 52
            if (r10 != r2) goto L_0x1120
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r8 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1144
        L_0x1120:
            r2 = 59
            if (r10 != r2) goto L_0x1136
            r1 = 2131558512(0x7f0d0070, float:1.8742342E38)
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131625929(0x7f0e07c9, float:1.887908E38)
            java.lang.String r8 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            goto L_0x1144
        L_0x1136:
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131627631(0x7f0e0e6f, float:1.8882532E38)
            java.lang.String r8 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
        L_0x1144:
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r4 = 30
            r2.setAnimation(r1, r4, r4)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r5, r2)
        L_0x1156:
            r1 = 0
        L_0x1157:
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
        L_0x1183:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r4 = r0.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r2.append(r4)
            android.widget.TextView r4 = r0.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x11b0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = ". "
            r4.append(r6)
            android.widget.TextView r6 = r0.subinfoTextView
            java.lang.CharSequence r6 = r6.getText()
            r4.append(r6)
            java.lang.String r14 = r4.toString()
        L_0x11b0:
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r20.isMultilineSubInfo()
            if (r2 == 0) goto L_0x1205
            android.view.ViewParent r1 = r20.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x11d0
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x11d0:
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
            goto L_0x12a9
        L_0x1205:
            boolean r2 = r20.hasSubInfo()
            if (r2 == 0) goto L_0x1215
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x12a9
        L_0x1215:
            android.view.ViewParent r2 = r20.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x12a9
            android.view.ViewParent r2 = r20.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r4 = r2.getMeasuredWidth()
            int r6 = r2.getPaddingLeft()
            int r4 = r4 - r6
            int r2 = r2.getPaddingRight()
            int r4 = r4 - r2
            if (r4 > 0) goto L_0x1237
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r2.x
        L_0x1237:
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
            if (r4 == r6) goto L_0x1273
            r6 = 17
            if (r4 == r6) goto L_0x1273
            r6 = 18
            if (r4 != r6) goto L_0x1270
            goto L_0x1273
        L_0x1270:
            r13 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1275
        L_0x1273:
            r13 = 1096810496(0x41600000, float:14.0)
        L_0x1275:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r2 = r2 + r3
            r0.undoViewHeight = r2
            int r3 = r0.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x128f
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x12a9
        L_0x128f:
            r4 = 25
            if (r3 != r4) goto L_0x12a0
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x12a9
        L_0x12a0:
            if (r1 == 0) goto L_0x12a9
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r1
            r0.undoViewHeight = r2
        L_0x12a9:
            int r1 = r20.getVisibility()
            if (r1 == 0) goto L_0x1311
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            if (r1 == 0) goto L_0x12b9
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x12bb
        L_0x12b9:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x12bb:
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
            if (r6 == 0) goto L_0x12db
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x12dd
        L_0x12db:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x12dd:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r0.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x12f0
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x12f2
        L_0x12f0:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x12f2:
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
        L_0x1311:
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
