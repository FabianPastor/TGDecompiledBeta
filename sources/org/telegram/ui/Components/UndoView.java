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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34;
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

    /* JADX WARNING: Removed duplicated region for block: B:192:0x0adb  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0b02  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0b47  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0be7  */
    /* JADX WARNING: Removed duplicated region for block: B:237:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r22, int r24, java.lang.Object r25, java.lang.Object r26, java.lang.Runnable r27, java.lang.Runnable r28) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r3 = r24
            r4 = r25
            java.lang.Runnable r5 = r0.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r0.isShown = r5
            r6 = r27
            r0.currentActionRunnable = r6
            r6 = r28
            r0.currentCancelRunnable = r6
            r0.currentDialogId = r1
            r0.currentAction = r3
            r6 = 5000(0x1388, double:2.4703E-320)
            r0.timeLeft = r6
            r0.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0.lastUpdateTime = r6
            android.widget.TextView r6 = r0.undoTextView
            r7 = 2131627500(0x7f0e0dec, float:1.8882266E38)
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
            boolean r11 = r21.isTooltipAction()
            java.lang.String r14 = ""
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1114112000(0x42680000, float:58.0)
            r15 = 2
            if (r11 == 0) goto L_0x0492
            r10 = 34
            r12 = 3000(0xbb8, double:1.482E-320)
            r19 = 0
            if (r3 != r10) goto L_0x00ea
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627723(0x7f0e0ecb, float:1.8882718E38)
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
            r0.timeLeft = r12
            r3 = r19
            r1 = 36
            r12 = 0
            goto L_0x0411
        L_0x00ea:
            r10 = 33
            if (r3 != r10) goto L_0x0105
            r1 = 2131627713(0x7f0e0ec1, float:1.8882698E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131558468(0x7f0d0044, float:1.8742253E38)
            r0.timeLeft = r12
            r3 = r19
            r1 = 36
            r12 = 2131558468(0x7f0d0044, float:1.8742253E38)
            goto L_0x0411
        L_0x0105:
            r10 = 30
            if (r3 != r10) goto L_0x012f
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627744(0x7f0e0ee0, float:1.8882761E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558469(0x7f0d0045, float:1.8742255E38)
            r0.timeLeft = r12
            r3 = r19
            r1 = 36
            r12 = 2131558469(0x7f0d0045, float:1.8742255E38)
            goto L_0x0411
        L_0x012f:
            r10 = 31
            if (r3 != r10) goto L_0x0159
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627743(0x7f0e0edf, float:1.888276E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558471(0x7f0d0047, float:1.8742259E38)
            r0.timeLeft = r12
            r3 = r19
            r1 = 36
            r12 = 2131558471(0x7f0d0047, float:1.8742259E38)
            goto L_0x0411
        L_0x0159:
            r10 = 32
            if (r3 != r10) goto L_0x0183
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131627739(0x7f0e0edb, float:1.888275E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r1 = 2131558467(0x7f0d0043, float:1.874225E38)
            r0.timeLeft = r12
            r3 = r19
            r1 = 36
            r12 = 2131558467(0x7f0d0043, float:1.874225E38)
            goto L_0x0411
        L_0x0183:
            r10 = 9
            if (r3 == r10) goto L_0x03d5
            r10 = 10
            if (r3 != r10) goto L_0x018d
            goto L_0x03d5
        L_0x018d:
            if (r3 != r9) goto L_0x01a5
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 2131626300(0x7f0e093c, float:1.8879832E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "NowInContacts"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            goto L_0x040a
        L_0x01a5:
            r10 = 22
            if (r3 != r10) goto L_0x020d
            r10 = 0
            int r3 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x01c7
            if (r4 != 0) goto L_0x01bb
            r1 = 2131625835(0x7f0e076b, float:1.887889E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01c4
        L_0x01bb:
            r1 = 2131625836(0x7f0e076c, float:1.8878891E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x01c4:
            r2 = r1
            goto L_0x040a
        L_0x01c7:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x01f7
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x01f7
            if (r4 != 0) goto L_0x01ed
            r1 = 2131625831(0x7f0e0767, float:1.8878881E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01c4
        L_0x01ed:
            r1 = 2131625832(0x7f0e0768, float:1.8878883E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01c4
        L_0x01f7:
            if (r4 != 0) goto L_0x0203
            r1 = 2131625833(0x7f0e0769, float:1.8878885E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01c4
        L_0x0203:
            r1 = 2131625834(0x7f0e076a, float:1.8878887E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01c4
        L_0x020d:
            r10 = 23
            if (r3 != r10) goto L_0x021c
            r1 = 2131624768(0x7f0e0340, float:1.8876725E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x040a
        L_0x021c:
            r10 = 6
            if (r3 != r10) goto L_0x023a
            r1 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r12 = 2131558413(0x7f0d000d, float:1.8742141E38)
            r1 = 48
        L_0x0236:
            r3 = r19
            goto L_0x0411
        L_0x023a:
            int r10 = r0.currentAction
            r11 = 13
            if (r10 != r11) goto L_0x0258
            r1 = 2131626846(0x7f0e0b5e, float:1.888094E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r12 = 2131558473(0x7f0d0049, float:1.8742263E38)
        L_0x0255:
            r1 = 44
            goto L_0x0236
        L_0x0258:
            r11 = 14
            if (r10 != r11) goto L_0x0272
            r1 = 2131626848(0x7f0e0b60, float:1.8880944E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r1 = 2131626849(0x7f0e0b61, float:1.8880946E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r12 = 2131558474(0x7f0d004a, float:1.8742265E38)
            goto L_0x0255
        L_0x0272:
            r10 = 7
            if (r3 != r10) goto L_0x02a0
            r1 = 2131624278(0x7f0e0156, float:1.8875731E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0297
            r1 = 2131624279(0x7f0e0157, float:1.8875733E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
        L_0x0295:
            r19 = r1
        L_0x0297:
            r3 = r19
            r1 = 36
            r12 = 2131558412(0x7f0d000c, float:1.874214E38)
            goto L_0x0411
        L_0x02a0:
            r10 = 20
            if (r3 == r10) goto L_0x02df
            r11 = 21
            if (r3 != r11) goto L_0x02a9
            goto L_0x02df
        L_0x02a9:
            r1 = 19
            if (r3 != r1) goto L_0x02b0
            java.lang.CharSequence r2 = r0.infoText
            goto L_0x0297
        L_0x02b0:
            r1 = 3
            if (r3 != r1) goto L_0x02bd
            r1 = 2131624739(0x7f0e0323, float:1.8876666E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x02c6
        L_0x02bd:
            r1 = 2131624776(0x7f0e0348, float:1.8876741E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x02c6:
            r2 = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0297
            r1 = 2131624740(0x7f0e0324, float:1.8876668E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            goto L_0x0295
        L_0x02df:
            r11 = r26
            org.telegram.messenger.MessagesController$DialogFilter r11 = (org.telegram.messenger.MessagesController.DialogFilter) r11
            r12 = 0
            int r20 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r20 == 0) goto L_0x038b
            int r4 = (int) r1
            if (r4 != 0) goto L_0x0300
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r12 = 32
            long r1 = r1 >> r12
            int r2 = (int) r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            int r4 = r1.user_id
        L_0x0300:
            if (r4 <= 0) goto L_0x0348
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r3 != r10) goto L_0x032d
            r2 = 2131625451(0x7f0e05eb, float:1.887811E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterUserAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01c4
        L_0x032d:
            r2 = 2131625452(0x7f0e05ec, float:1.8878112E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterUserRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01c4
        L_0x0348:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r3 != r10) goto L_0x0372
            r2 = 2131625390(0x7f0e05ae, float:1.8877987E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterChatAddedToExisting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01c4
        L_0x0372:
            r2 = 2131625391(0x7f0e05af, float:1.8877989E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r1 = r1.title
            r3[r7] = r1
            java.lang.String r1 = r11.name
            r3[r5] = r1
            java.lang.String r1 = "FilterChatRemovedFrom"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01c4
        L_0x038b:
            if (r3 != r10) goto L_0x03b1
            r1 = 2131625394(0x7f0e05b2, float:1.8877995E38)
            java.lang.Object[] r2 = new java.lang.Object[r15]
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
            goto L_0x01c4
        L_0x03b1:
            r1 = 2131625395(0x7f0e05b3, float:1.8877997E38)
            java.lang.Object[] r2 = new java.lang.Object[r15]
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
            goto L_0x01c4
        L_0x03d5:
            r1 = r4
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            r2 = 9
            if (r3 != r2) goto L_0x03f3
            r2 = 2131625156(0x7f0e04c4, float:1.8877512E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01c4
        L_0x03f3:
            r2 = 2131625157(0x7f0e04c5, float:1.8877514E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            r3[r7] = r1
            java.lang.String r1 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x01c4
        L_0x040a:
            r3 = r19
            r1 = 36
            r12 = 2131558416(0x7f0d0010, float:1.8742147E38)
        L_0x0411:
            android.widget.TextView r4 = r0.infoTextView
            r4.setText(r2)
            if (r12 == 0) goto L_0x042e
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r2.setAnimation(r12, r1, r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x0433
        L_0x042e:
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r9)
        L_0x0433:
            if (r3 == 0) goto L_0x046a
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
            r1.setText(r3)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r0.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            goto L_0x048b
        L_0x046a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            r1 = 1095761920(0x41500000, float:13.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            r6.rightMargin = r7
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r9)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
        L_0x048b:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            goto L_0x050c
        L_0x0492:
            int r12 = r0.currentAction
            r13 = 24
            if (r12 == r13) goto L_0x0970
            r13 = 25
            if (r12 != r13) goto L_0x049e
            goto L_0x0970
        L_0x049e:
            r13 = 11
            if (r12 != r13) goto L_0x0510
            r1 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r1 = (org.telegram.tgnet.TLRPC$TL_authorization) r1
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624395(0x7f0e01cb, float:1.8875969E38)
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
        L_0x050c:
            r1 = 1096810496(0x41600000, float:14.0)
            goto L_0x0ac5
        L_0x0510:
            r13 = 15
            r11 = 42
            r18 = 1104150528(0x41d00000, float:26.0)
            if (r12 != r13) goto L_0x05e0
            r1 = 10000(0x2710, double:4.9407E-320)
            r0.timeLeft = r1
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131626316(0x7f0e094c, float:1.8879865E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625387(0x7f0e05ab, float:1.887798E38)
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
            r1 = 2131625386(0x7f0e05aa, float:1.8877978E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r11)
            int r1 = r1.lastIndexOf(r11)
            if (r3 < 0) goto L_0x05b0
            if (r1 < 0) goto L_0x05b0
            if (r3 == r1) goto L_0x05b0
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
        L_0x05b0:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setMaxLines(r15)
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
            goto L_0x050c
        L_0x05e0:
            r13 = 16
            if (r12 == r13) goto L_0x0804
            r13 = 17
            if (r12 != r13) goto L_0x05ea
            goto L_0x0804
        L_0x05ea:
            r13 = 18
            if (r12 != r13) goto L_0x066c
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
            r10.gravity = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r10.bottomMargin = r2
            r10.topMargin = r2
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
            goto L_0x050c
        L_0x066c:
            r10 = 12
            if (r12 != r10) goto L_0x0702
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624871(0x7f0e03a7, float:1.8876934E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131166048(0x7var_, float:1.794633E38)
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
            r1 = 2131624872(0x7f0e03a8, float:1.8876936E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            int r3 = r1.indexOf(r11)
            int r1 = r1.lastIndexOf(r11)
            if (r3 < 0) goto L_0x06dd
            if (r1 < 0) goto L_0x06dd
            if (r3 == r1) goto L_0x06dd
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
        L_0x06dd:
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setText(r2)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setVisibility(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.subinfoTextView
            r1.setMaxLines(r15)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r9)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x050c
        L_0x0702:
            if (r12 == r15) goto L_0x07a3
            r10 = 4
            if (r12 != r10) goto L_0x0709
            goto L_0x07a3
        L_0x0709:
            r3 = 1110704128(0x42340000, float:45.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6.leftMargin = r3
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6.topMargin = r3
            r6.rightMargin = r7
            android.widget.TextView r3 = r0.infoTextView
            r3.setTextSize(r5, r8)
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
            if (r3 != 0) goto L_0x0749
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625629(0x7f0e069d, float:1.8878471E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0791
        L_0x0749:
            int r3 = (int) r1
            if (r3 >= 0) goto L_0x0783
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x0774
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0774
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624649(0x7f0e02c9, float:1.8876484E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0791
        L_0x0774:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131625585(0x7f0e0671, float:1.8878382E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            goto L_0x0791
        L_0x0783:
            android.widget.TextView r3 = r0.infoTextView
            r4 = 2131624743(0x7f0e0327, float:1.8876674E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
        L_0x0791:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r0.currentAction
            if (r4 != 0) goto L_0x079d
            r4 = 1
            goto L_0x079e
        L_0x079d:
            r4 = 0
        L_0x079e:
            r3.addDialogAction(r1, r4)
            goto L_0x050c
        L_0x07a3:
            if (r3 != r15) goto L_0x07b4
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624739(0x7f0e0323, float:1.8876666E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x07c2
        L_0x07b4:
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131624776(0x7f0e0348, float:1.8876741E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x07c2:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r1
            r1 = 1095761920(0x41500000, float:13.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.topMargin = r1
            r6.rightMargin = r7
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
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
            r2 = 2131558410(0x7f0d000a, float:1.8742135E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.playAnimation()
            goto L_0x050c
        L_0x0804:
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
            if (r2 == 0) goto L_0x0848
            android.widget.TextView r1 = r0.infoTextView
            r2 = 2131625083(0x7f0e047b, float:1.8877364E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r2 = 2131165392(0x7var_d0, float:1.7945E38)
            r1.setImageResource(r2)
            goto L_0x08ec
        L_0x0848:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0865
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131624976(0x7f0e0410, float:1.8877147E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0862:
            r8 = 1096810496(0x41600000, float:14.0)
            goto L_0x08be
        L_0x0865:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0898
            android.widget.TextView r3 = r0.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r11, r7)
            r3.setText(r2)
            goto L_0x0862
        L_0x0898:
            android.widget.TextView r2 = r0.infoTextView
            r3 = 2131625082(0x7f0e047a, float:1.8877362E38)
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
        L_0x08be:
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
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r10.width = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r10.height = r1
        L_0x08ec:
            android.widget.TextView r1 = r0.undoTextView
            r2 = 2131627082(0x7f0e0c4a, float:1.8881418E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x093a
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
            r2.setVisibility(r9)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r7)
            goto L_0x094b
        L_0x093a:
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.widget.TextView r1 = r0.undoTextView
            r1.setVisibility(r9)
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            r1 = r2
        L_0x094b:
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
            r1.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r1 = r0.leftImageView
            r1.setVisibility(r7)
            goto L_0x050c
        L_0x0970:
            r1 = r4
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r26
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            android.widget.ImageView r3 = r0.undoImageView
            r3.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.leftImageView
            r3.setVisibility(r7)
            r3 = 28
            java.lang.String r4 = "undo_infoColor"
            if (r1 == 0) goto L_0x0a39
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
            java.lang.String r12 = "BODY.**"
            r8.setLayerColor(r12, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r12 = "Wibe Big.**"
            r8.setLayerColor(r12, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r12 = "Wibe Big 3.**"
            r8.setLayerColor(r12, r10)
            org.telegram.ui.Components.RLottieImageView r8 = r0.leftImageView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r10 = "Wibe Small.**"
            r8.setLayerColor(r10, r4)
            android.widget.TextView r4 = r0.infoTextView
            r8 = 2131626805(0x7f0e0b35, float:1.8880857E38)
            java.lang.String r10 = "ProximityAlertSet"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r4.setText(r8)
            org.telegram.ui.Components.RLottieImageView r4 = r0.leftImageView
            r8 = 2131558432(0x7f0d0020, float:1.874218E38)
            r4.setAnimation(r8, r3, r3)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setVisibility(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r3.setSingleLine(r7)
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 3
            r3.setMaxLines(r4)
            if (r2 == 0) goto L_0x0a14
            android.widget.TextView r3 = r0.subinfoTextView
            r4 = 2131626807(0x7f0e0b37, float:1.888086E38)
            java.lang.Object[] r8 = new java.lang.Object[r15]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r8[r7] = r2
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r15)
            r8[r5] = r1
            java.lang.String r1 = "ProximityAlertSetInfoUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r8)
            r3.setText(r1)
            goto L_0x0a2b
        L_0x0a14:
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r1 = (float) r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDistance(r1, r15)
            r4[r7] = r1
            java.lang.String r1 = "ProximityAlertSetInfoGroup2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r2.setText(r1)
        L_0x0a2b:
            android.widget.LinearLayout r1 = r0.undoButton
            r1.setVisibility(r9)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r1
            r1 = 1096810496(0x41600000, float:14.0)
            goto L_0x0ab4
        L_0x0a39:
            android.widget.TextView r1 = r0.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.infoTextView
            r1.setTextSize(r5, r8)
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
            android.widget.TextView r2 = r0.infoTextView
            r4 = 2131626804(0x7f0e0b34, float:1.8880855E38)
            java.lang.String r8 = "ProximityAlertCancelled"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r4 = 2131558427(0x7f0d001b, float:1.874217E38)
            r2.setAnimation(r4, r3, r3)
            android.widget.TextView r2 = r0.subinfoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r0.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.LinearLayout r2 = r0.undoButton
            r2.setVisibility(r7)
        L_0x0ab4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.leftMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.leftImageView
            r2.playAnimation()
        L_0x0ac5:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r0.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r0.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0af2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r0.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r14 = r3.toString()
        L_0x0af2:
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r21.isMultilineSubInfo()
            if (r2 == 0) goto L_0x0b47
            android.view.ViewParent r1 = r21.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L_0x0b12
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.x
        L_0x0b12:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            android.widget.TextView r2 = r0.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r22 = r21
            r23 = r2
            r24 = r1
            r25 = r3
            r26 = r4
            r27 = r6
            r22.measureChildWithMargins(r23, r24, r25, r26, r27)
            android.widget.TextView r1 = r0.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r0.undoViewHeight = r1
            goto L_0x0be1
        L_0x0b47:
            boolean r2 = r21.hasSubInfo()
            if (r2 == 0) goto L_0x0b57
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.undoViewHeight = r1
            goto L_0x0be1
        L_0x0b57:
            android.view.ViewParent r2 = r21.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x0be1
            android.view.ViewParent r2 = r21.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x0b79
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x0b79:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r0.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r8 = 0
            r22 = r21
            r23 = r2
            r24 = r3
            r25 = r4
            r26 = r6
            r27 = r8
            r22.measureChildWithMargins(r23, r24, r25, r26, r27)
            android.widget.TextView r2 = r0.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r0.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x0bb5
            r4 = 17
            if (r3 == r4) goto L_0x0bb5
            r4 = 18
            if (r3 != r4) goto L_0x0bb2
            goto L_0x0bb5
        L_0x0bb2:
            r12 = 1105199104(0x41e00000, float:28.0)
            goto L_0x0bb7
        L_0x0bb5:
            r12 = 1096810496(0x41600000, float:14.0)
        L_0x0bb7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 + r1
            r0.undoViewHeight = r2
            int r1 = r0.currentAction
            r3 = 18
            if (r1 != r3) goto L_0x0bd1
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
            goto L_0x0be1
        L_0x0bd1:
            r3 = 25
            if (r1 != r3) goto L_0x0be1
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = java.lang.Math.max(r2, r1)
            r0.undoViewHeight = r1
        L_0x0be1:
            int r1 = r21.getVisibility()
            if (r1 == 0) goto L_0x0c4c
            r0.setVisibility(r7)
            boolean r1 = r0.fromTop
            if (r1 == 0) goto L_0x0bf1
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0bf3
        L_0x0bf1:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0bf3:
            r2 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0.undoViewHeight
            int r3 = r3 + r2
            float r2 = (float) r3
            float r1 = r1 * r2
            r0.setTranslationY(r1)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r5]
            android.util.Property r3 = android.view.View.TRANSLATION_Y
            float[] r4 = new float[r15]
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x0CLASSNAME
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0CLASSNAME:
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r9 = r0.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r0.fromTop
            if (r6 == 0) goto L_0x0c2b
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0c2d
        L_0x0c2b:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0c2d:
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
        L_0x0c4c:
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
