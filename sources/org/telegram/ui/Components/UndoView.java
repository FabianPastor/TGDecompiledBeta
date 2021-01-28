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

    /* JADX WARNING: Removed duplicated region for block: B:262:0x0da4  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0dcb  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0e10  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0eba  */
    /* JADX WARNING: Removed duplicated region for block: B:309:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r20, int r22, java.lang.Object r23, java.lang.Object r24, java.lang.Runnable r25, java.lang.Runnable r26) {
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
            r6 = r25
            r0.currentActionRunnable = r6
            r6 = r26
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0.lastUpdateTime = r6
            android.widget.TextView r6 = r0.undoTextView
            r7 = 2131627601(0x7f0e0e51, float:1.8882471E38)
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
            boolean r12 = r19.isTooltipAction()
            java.lang.String r15 = ""
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r10 = 30
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r13 = 3000(0xbb8, double:1.482E-320)
            r8 = 36
            if (r12 == 0) goto L_0x04d9
            r11 = 34
            r12 = 0
            if (r3 != r11) goto L_0x00ef
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627826(0x7f0e0var_, float:1.8882927E38)
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
            r0.timeLeft = r13
            r14 = 0
            goto L_0x0448
        L_0x00ef:
            r11 = 33
            if (r3 != r11) goto L_0x0106
            r1 = 2131627816(0x7f0e0var_, float:1.8882907E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131558490(0x7f0d005a, float:1.8742297E38)
            r0.timeLeft = r13
            r14 = 2131558490(0x7f0d005a, float:1.8742297E38)
            goto L_0x0448
        L_0x0106:
            if (r3 != r10) goto L_0x012a
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627854(0x7f0e0f4e, float:1.8882984E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558491(0x7f0d005b, float:1.87423E38)
            r0.timeLeft = r13
        L_0x0125:
            r14 = 2131558491(0x7f0d005b, float:1.87423E38)
            goto L_0x0448
        L_0x012a:
            r10 = 35
            if (r3 != r10) goto L_0x014c
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627855(0x7f0e0f4f, float:1.8882986E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558491(0x7f0d005b, float:1.87423E38)
            r0.timeLeft = r13
            goto L_0x0125
        L_0x014c:
            r10 = 31
            if (r3 != r10) goto L_0x0172
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627852(0x7f0e0f4c, float:1.888298E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558493(0x7f0d005d, float:1.8742303E38)
            r0.timeLeft = r13
        L_0x016d:
            r14 = 2131558493(0x7f0d005d, float:1.8742303E38)
            goto L_0x0448
        L_0x0172:
            if (r3 != r8) goto L_0x0192
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627853(0x7f0e0f4d, float:1.8882982E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558493(0x7f0d005d, float:1.8742303E38)
            r0.timeLeft = r13
            goto L_0x016d
        L_0x0192:
            r10 = 32
            if (r3 != r10) goto L_0x01b8
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627846(0x7f0e0var_, float:1.8882968E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558489(0x7f0d0059, float:1.8742295E38)
            r0.timeLeft = r13
            r14 = 2131558489(0x7f0d0059, float:1.8742295E38)
            goto L_0x0448
        L_0x01b8:
            r10 = 9
            if (r3 == r10) goto L_0x0410
            r10 = 10
            if (r3 != r10) goto L_0x01c2
            goto L_0x0410
        L_0x01c2:
            if (r3 != r9) goto L_0x01da
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131626381(0x7f0e098d, float:1.8879997E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x0445
        L_0x01da:
            r10 = 22
            if (r3 != r10) goto L_0x0242
            r10 = 0
            int r3 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x01fc
            if (r4 != 0) goto L_0x01f0
            r1 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01f9
        L_0x01f0:
            r1 = 2131625909(0x7f0e07b5, float:1.887904E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x01f9:
            r2 = r1
            goto L_0x0445
        L_0x01fc:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x022c
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x022c
            if (r4 != 0) goto L_0x0222
            r1 = 2131625904(0x7f0e07b0, float:1.887903E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01f9
        L_0x0222:
            r1 = 2131625905(0x7f0e07b1, float:1.8879031E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01f9
        L_0x022c:
            if (r4 != 0) goto L_0x0238
            r1 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01f9
        L_0x0238:
            r1 = 2131625907(0x7f0e07b3, float:1.8879035E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01f9
        L_0x0242:
            r10 = 23
            if (r3 != r10) goto L_0x0251
            r1 = 2131624776(0x7f0e0348, float:1.8876741E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0445
        L_0x0251:
            r10 = 6
            if (r3 != r10) goto L_0x026d
            r1 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131624277(0x7f0e0155, float:1.887573E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14 = 2131558416(0x7f0d0010, float:1.8742147E38)
            r8 = 48
            goto L_0x0448
        L_0x026d:
            int r10 = r0.currentAction
            r11 = 13
            if (r10 != r11) goto L_0x028c
            r1 = 2131626935(0x7f0e0bb7, float:1.888112E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131626936(0x7f0e0bb8, float:1.8881122E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14 = 2131558495(0x7f0d005f, float:1.8742307E38)
        L_0x0288:
            r8 = 44
            goto L_0x0448
        L_0x028c:
            r11 = 14
            if (r10 != r11) goto L_0x02a6
            r1 = 2131626937(0x7f0e0bb9, float:1.8881124E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14 = 2131558496(0x7f0d0060, float:1.874231E38)
            goto L_0x0288
        L_0x02a6:
            r10 = 7
            if (r3 != r10) goto L_0x02cf
            r1 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x02ca
            r1 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
        L_0x02c9:
            r12 = r1
        L_0x02ca:
            r14 = 2131558415(0x7f0d000f, float:1.8742145E38)
            goto L_0x0448
        L_0x02cf:
            r10 = 20
            if (r3 == r10) goto L_0x030e
            r10 = 21
            if (r3 != r10) goto L_0x02d8
            goto L_0x030e
        L_0x02d8:
            r1 = 19
            if (r3 != r1) goto L_0x02df
            java.lang.CharSequence r2 = r0.infoText
            goto L_0x02ca
        L_0x02df:
            r1 = 3
            if (r3 != r1) goto L_0x02ec
            r1 = 2131624747(0x7f0e032b, float:1.8876682E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x02f5
        L_0x02ec:
            r1 = 2131624784(0x7f0e0350, float:1.8876757E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x02f5:
            r2 = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x02ca
            r1 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            goto L_0x02c9
        L_0x030e:
            r10 = r24
            org.telegram.messenger.MessagesController$DialogFilter r10 = (org.telegram.messenger.MessagesController.DialogFilter) r10
            r13 = 0
            int r11 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x03c2
            int r4 = (int) r1
            if (r4 != 0) goto L_0x032f
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r11 = 32
            long r1 = r1 >> r11
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            int r4 = r1.user_id
        L_0x032f:
            if (r4 <= 0) goto L_0x037b
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r2 = 20
            if (r3 != r2) goto L_0x035f
            r2 = 2131625476(0x7f0e0604, float:1.8878161E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterUserAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x035f:
            r3 = 2
            r2 = 2131625477(0x7f0e0605, float:1.8878163E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterUserRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x037b:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            r2 = 20
            if (r3 != r2) goto L_0x03a8
            r2 = 2131625415(0x7f0e05c7, float:1.8878037E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterChatAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x03a8:
            r3 = 2
            r2 = 2131625416(0x7f0e05c8, float:1.887804E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = r10.name
            r4[r5] = r1
            java.lang.String r1 = "FilterChatRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x03c2:
            r1 = 20
            if (r3 != r1) goto L_0x03eb
            r1 = 2131625419(0x7f0e05cb, float:1.8878045E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2)
            r3[r7] = r2
            java.lang.String r2 = r10.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatsAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x03eb:
            r1 = 2131625420(0x7f0e05cc, float:1.8878047E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2)
            r3[r7] = r2
            java.lang.String r2 = r10.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatsRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x0410:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 9
            if (r3 != r2) goto L_0x042e
            r2 = 2131625177(0x7f0e04d9, float:1.8877555E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x042e:
            r2 = 2131625178(0x7f0e04da, float:1.8877557E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01f9
        L_0x0445:
            r14 = 2131558419(0x7f0d0013, float:1.8742153E38)
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
            r10 = 1096810496(0x41600000, float:14.0)
            goto L_0x0b2c
        L_0x04d9:
            int r12 = r0.currentAction
            r10 = 40
            r13 = 42
            if (r12 == r10) goto L_0x0b2f
            r10 = 41
            if (r12 == r10) goto L_0x0b2f
            if (r12 == r13) goto L_0x0b2f
            r10 = 51
            if (r12 == r10) goto L_0x0b2f
            r10 = 50
            if (r12 == r10) goto L_0x0b2f
            r10 = 52
            if (r12 == r10) goto L_0x0b2f
            r10 = 53
            if (r12 != r10) goto L_0x04f9
            goto L_0x0b2f
        L_0x04f9:
            r10 = 24
            if (r12 == r10) goto L_0x09d2
            r10 = 25
            if (r12 != r10) goto L_0x0503
            goto L_0x09d2
        L_0x0503:
            r10 = 11
            if (r12 != r10) goto L_0x0571
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 2131558419(0x7f0d0013, float:1.8742153E38)
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
        L_0x0571:
            r10 = 15
            if (r12 != r10) goto L_0x063e
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626397(0x7f0e099d, float:1.888003E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625412(0x7f0e05c4, float:1.8878031E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
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
            r1 = 2131625411(0x7f0e05c3, float:1.887803E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r13)
            int r1 = r1.lastIndexOf(r13)
            if (r3 < 0) goto L_0x060d
            if (r1 < 0) goto L_0x060d
            if (r3 == r1) goto L_0x060d
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
        L_0x060d:
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
            goto L_0x04d5
        L_0x063e:
            r10 = 16
            if (r12 == r10) goto L_0x0863
            r10 = 17
            if (r12 != r10) goto L_0x0648
            goto L_0x0863
        L_0x0648:
            r10 = 18
            if (r12 != r10) goto L_0x06c6
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
            r2 = 2131558415(0x7f0d000f, float:1.8742145E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x04d5
        L_0x06c6:
            r4 = 12
            if (r12 != r4) goto L_0x075d
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624879(0x7f0e03af, float:1.887695E38)
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
            r1 = 2131624880(0x7f0e03b0, float:1.8876952E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r13)
            int r1 = r1.lastIndexOf(r13)
            if (r3 < 0) goto L_0x0737
            if (r1 < 0) goto L_0x0737
            if (r3 == r1) goto L_0x0737
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
        L_0x0737:
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
            goto L_0x04d5
        L_0x075d:
            r4 = 2
            if (r12 == r4) goto L_0x0801
            r4 = 4
            if (r12 != r4) goto L_0x0765
            goto L_0x0801
        L_0x0765:
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
            if (r3 != 0) goto L_0x07a7
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625665(0x7f0e06c1, float:1.8878544E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x07ef
        L_0x07a7:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x07e1
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x07d2
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x07d2
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624657(0x7f0e02d1, float:1.88765E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x07ef
        L_0x07d2:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625619(0x7f0e0693, float:1.8878451E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x07ef
        L_0x07e1:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624751(0x7f0e032f, float:1.887669E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x07ef:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x07fb
            r4 = 1
            goto L_0x07fc
        L_0x07fb:
            r4 = 0
        L_0x07fc:
            r3.addDialogAction(r1, r4)
            goto L_0x04d5
        L_0x0801:
            r1 = 2
            if (r3 != r1) goto L_0x0813
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624747(0x7f0e032b, float:1.8876682E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0821
        L_0x0813:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624784(0x7f0e0350, float:1.8876757E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0821:
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
            r2 = 2131558413(0x7f0d000d, float:1.8742141E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x04d5
        L_0x0863:
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
            if (r2 == 0) goto L_0x08a7
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625104(0x7f0e0490, float:1.8877407E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165394(0x7var_d2, float:1.7945004E38)
            r1.setImageResource(r2)
            goto L_0x094f
        L_0x08a7:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x08c4
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624987(0x7f0e041b, float:1.887717E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x08c1:
            r8 = 1096810496(0x41600000, float:14.0)
            goto L_0x091d
        L_0x08c4:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x08f7
            android.widget.TextView r3 = r0.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r10, r7)
            r3.setText(r2)
            goto L_0x08c1
        L_0x08f7:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625103(0x7f0e048f, float:1.8877404E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r1
            java.lang.String r8 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4)
            android.widget.TextView r4 = r0.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r10, r7)
            r2.setText(r3)
        L_0x091d:
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
        L_0x094f:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131627177(0x7f0e0ca9, float:1.8881611E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x099f
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
            goto L_0x09ad
        L_0x099f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r0.undoTextView
            r2.setVisibility(r9)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r9)
        L_0x09ad:
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
            goto L_0x04d5
        L_0x09d2:
            r1 = r4
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r24
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            android.widget.ImageView r3 = r0.undoImageView
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r7)
            r3 = 28
            java.lang.String r4 = "undo_infoColor"
            if (r1 == 0) goto L_0x0a9e
            android.widget.TextView r8 = r0.infoTextView
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r8.setTypeface(r10)
            android.widget.TextView r8 = r0.infoTextView
            r10 = 1096810496(0x41600000, float:14.0)
            r8.setTextSize(r5, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            r8.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r11 = "BODY.**"
            r8.setLayerColor(r11, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r11 = "Wibe Big.**"
            r8.setLayerColor(r11, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r11 = "Wibe Big 3.**"
            r8.setLayerColor(r11, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r10 = "Wibe Small.**"
            r8.setLayerColor(r10, r4)
            android.widget.TextView r4 = r0.infoTextView
            r8 = 2131626893(0x7f0e0b8d, float:1.8881035E38)
            java.lang.String r10 = "ProximityAlertSet"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r4.setText(r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            r8 = 2131558439(0x7f0d0027, float:1.8742194E38)
            r4.setAnimation(r8, r3, r3)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setSingleLine(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 3
            r3.setMaxLines(r4)
            if (r2 == 0) goto L_0x0a77
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 2131626895(0x7f0e0b8f, float:1.888104E38)
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
            goto L_0x0a8f
        L_0x0a77:
            r8 = 2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 2131626894(0x7f0e0b8e, float:1.8881037E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r8)
            r4[r7] = r1
            java.lang.String r1 = "ProximityAlertSetInfoGroup2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r2.setText(r1)
        L_0x0a8f:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            r10 = 1096810496(0x41600000, float:14.0)
            goto L_0x0b1b
        L_0x0a9e:
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
            r10 = 1096810496(0x41600000, float:14.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r6.topMargin = r1
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131626892(0x7f0e0b8c, float:1.8881033E38)
            java.lang.String r4 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r1.setAnimation(r2, r3, r3)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r9)
            android.widget.TextView r1 = r0.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
        L_0x0b1b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r1
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
        L_0x0b2c:
            r1 = 0
            goto L_0x0d8e
        L_0x0b2f:
            r10 = 1096810496(0x41600000, float:14.0)
            android.widget.ImageView r3 = r0.undoImageView
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.infoTextView
            android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r11)
            android.widget.TextView r3 = r0.infoTextView
            r11 = 1095761920(0x41500000, float:13.0)
            r3.setTextSize(r5, r11)
            int r3 = r0.currentAction
            r11 = 40
            if (r3 != r11) goto L_0x0b70
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625703(0x7f0e06e7, float:1.8878621E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
        L_0x0b6d:
            r1 = 1
            goto L_0x0d62
        L_0x0b70:
            r11 = 41
            if (r3 != r11) goto L_0x0b93
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625704(0x7f0e06e8, float:1.8878623E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            goto L_0x0b6d
        L_0x0b93:
            if (r3 != r13) goto L_0x0bbf
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625710(0x7f0e06ee, float:1.8878636E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558445(0x7f0d002d, float:1.8742206E38)
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r7, r7, r7, r2)
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            goto L_0x0b6d
        L_0x0bbf:
            r11 = 51
            if (r3 != r11) goto L_0x0bdf
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624386(0x7f0e01c2, float:1.887595E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            goto L_0x0d61
        L_0x0bdf:
            r11 = 50
            if (r3 != r11) goto L_0x0bff
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r1.setAnimation(r2, r8, r8)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            goto L_0x0d61
        L_0x0bff:
            r8 = 52
            if (r3 != r8) goto L_0x0CLASSNAME
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625999(0x7f0e080f, float:1.8879222E38)
            java.lang.String r3 = "MessageCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
            goto L_0x0d61
        L_0x0CLASSNAME:
            r8 = 53
            if (r3 != r8) goto L_0x0d61
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r24 != 0) goto L_0x0d0c
            int r4 = r0.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r11 = (long) r4
            int r4 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r4 != 0) goto L_0x0c6e
            int r1 = r3.intValue()
            if (r1 != r5) goto L_0x0CLASSNAME
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625583(0x7f0e066f, float:1.8878378E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625587(0x7f0e0673, float:1.8878386E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x0CLASSNAME:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558458(0x7f0d003a, float:1.8742232E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x0d5d
        L_0x0c6e:
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x0cb8
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r3.intValue()
            if (r2 != r5) goto L_0x0c9f
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625582(0x7f0e066e, float:1.8878376E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = "FwdMessageToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d01
        L_0x0c9f:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625586(0x7f0e0672, float:1.8878384E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = r1.title
            r4[r7] = r1
            java.lang.String r1 = "FwdMessagesToGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d01
        L_0x0cb8:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r3.intValue()
            if (r2 != r5) goto L_0x0ce7
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625584(0x7f0e0670, float:1.887838E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessageToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d01
        L_0x0ce7:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625588(0x7f0e0674, float:1.8878388E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessagesToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x0d01:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x0d5d
        L_0x0d0c:
            r1 = r24
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r3.intValue()
            if (r2 != r5) goto L_0x0d37
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625581(0x7f0e066d, float:1.8878374E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r8 = "Chats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r8, r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessageToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
            goto L_0x0d53
        L_0x0d37:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625585(0x7f0e0671, float:1.8878382E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r8 = "Chats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r8, r1)
            r4[r7] = r1
            java.lang.String r1 = "FwdMessagesToChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2.setText(r1)
        L_0x0d53:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
        L_0x0d5d:
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.timeLeft = r1
        L_0x0d61:
            r1 = 0
        L_0x0d62:
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
        L_0x0d8e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r0.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0dbb
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r0.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r15 = r3.toString()
        L_0x0dbb:
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r19.isMultilineSubInfo()
            if (r2 == 0) goto L_0x0e10
            android.view.ViewParent r1 = r19.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0ddb
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0ddb:
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
            goto L_0x0eb4
        L_0x0e10:
            boolean r2 = r19.hasSubInfo()
            if (r2 == 0) goto L_0x0e20
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x0eb4
        L_0x0e20:
            android.view.ViewParent r2 = r19.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x0eb4
            android.view.ViewParent r2 = r19.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x0e42
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x0e42:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r0.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r8 = 0
            r20 = r19
            r21 = r2
            r22 = r3
            r23 = r4
            r24 = r6
            r25 = r8
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r2 = r0.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r0.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x0e7e
            r4 = 17
            if (r3 == r4) goto L_0x0e7e
            r4 = 18
            if (r3 != r4) goto L_0x0e7b
            goto L_0x0e7e
        L_0x0e7b:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x0e80
        L_0x0e7e:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x0e80:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r2 = r2 + r3
            r0.undoViewHeight = r2
            int r3 = r0.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x0e9a
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x0eb4
        L_0x0e9a:
            r4 = 25
            if (r3 != r4) goto L_0x0eab
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x0eb4
        L_0x0eab:
            if (r1 == 0) goto L_0x0eb4
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r1
            r0.undoViewHeight = r2
        L_0x0eb4:
            int r1 = r19.getVisibility()
            if (r1 == 0) goto L_0x0f1c
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            if (r1 == 0) goto L_0x0ec4
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0ec6
        L_0x0ec4:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0ec6:
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
            if (r6 == 0) goto L_0x0ee6
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0ee8
        L_0x0ee6:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0ee8:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r0.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x0efb
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0efd
        L_0x0efb:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0efd:
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
        L_0x0f1c:
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
