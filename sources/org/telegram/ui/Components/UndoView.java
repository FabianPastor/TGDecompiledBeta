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

    /* JADX WARNING: Removed duplicated region for block: B:416:0x1284  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x12ab  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x12f0  */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x139a  */
    /* JADX WARNING: Removed duplicated region for block: B:463:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r28, int r30, java.lang.Object r31, java.lang.Object r32, java.lang.Runnable r33, java.lang.Runnable r34) {
        /*
            r27 = this;
            r1 = r27
            r2 = r28
            r0 = r30
            r4 = r31
            java.lang.Runnable r5 = r1.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r1.isShown = r5
            r6 = r33
            r1.currentActionRunnable = r6
            r6 = r34
            r1.currentCancelRunnable = r6
            r1.currentDialogId = r2
            r1.currentAction = r0
            r6 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r6
            r1.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r6
            android.widget.TextView r6 = r1.undoTextView
            r7 = 2131627797(0x7f0e0var_, float:1.8882869E38)
            java.lang.String r8 = "Undo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r7.toUpperCase()
            r6.setText(r7)
            android.widget.ImageView r6 = r1.undoImageView
            r7 = 0
            r6.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r6 = r1.leftImageView
            r6.setPadding(r7, r7, r7, r7)
            android.widget.TextView r6 = r1.infoTextView
            r8 = 1097859072(0x41700000, float:15.0)
            r6.setTextSize(r5, r8)
            org.telegram.ui.Components.BackupImageView r6 = r1.avatarImageView
            r9 = 8
            r6.setVisibility(r9)
            android.widget.TextView r6 = r1.infoTextView
            r10 = 51
            r6.setGravity(r10)
            android.widget.TextView r6 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r10 = -2
            r6.height = r10
            r10 = 1095761920(0x41500000, float:13.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r6.topMargin = r10
            r6.bottomMargin = r7
            org.telegram.ui.Components.RLottieImageView r10 = r1.leftImageView
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r10.setScaleType(r11)
            org.telegram.ui.Components.RLottieImageView r10 = r1.leftImageView
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
            android.widget.TextView r11 = r1.infoTextView
            r11.setMinHeight(r7)
            boolean r11 = r27.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r12 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r15 = 30
            r8 = 3000(0xbb8, double:1.482E-320)
            r14 = 2
            r13 = 36
            if (r11 == 0) goto L_0x06d9
            r10 = 74
            r11 = 0
            if (r0 != r10) goto L_0x00dc
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r7)
            r0 = 2131627150(0x7f0e0c8e, float:1.8881556E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627159(0x7f0e0CLASSNAME, float:1.8881575E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r2 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            goto L_0x0634
        L_0x00dc:
            r10 = 34
            if (r0 != r10) goto L_0x012b
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131628053(0x7f0e1015, float:1.8883388E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r7] = r4
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
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r5)
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r14)
            java.lang.String r21 = "50_50"
            java.lang.String r23 = "50_50"
            r19 = r4
            r24 = r3
            r25 = r0
            r19.setImage((org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (org.telegram.messenger.ImageLocation) r22, (java.lang.String) r23, (android.graphics.drawable.Drawable) r24, (java.lang.Object) r25)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r7)
            r1.timeLeft = r8
            r0 = r2
        L_0x0128:
            r2 = 0
            goto L_0x0634
        L_0x012b:
            r10 = 37
            if (r0 != r10) goto L_0x019f
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x0167
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r5)
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r14)
            java.lang.String r21 = "50_50"
            java.lang.String r23 = "50_50"
            r19 = r3
            r24 = r0
            r25 = r2
            r19.setImage((org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (org.telegram.messenger.ImageLocation) r22, (java.lang.String) r23, (android.graphics.drawable.Drawable) r24, (java.lang.Object) r25)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x0186
        L_0x0167:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r5)
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r14)
            java.lang.String r21 = "50_50"
            java.lang.String r23 = "50_50"
            r19 = r3
            r24 = r0
            r25 = r2
            r19.setImage((org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (org.telegram.messenger.ImageLocation) r22, (java.lang.String) r23, (android.graphics.drawable.Drawable) r24, (java.lang.Object) r25)
            java.lang.String r0 = r2.title
        L_0x0186:
            r2 = 2131628114(0x7f0e1052, float:1.8883512E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r7)
            r1.timeLeft = r8
            goto L_0x0128
        L_0x019f:
            r10 = 33
            if (r0 != r10) goto L_0x01b3
            r0 = 2131628036(0x7f0e1004, float:1.8883353E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558519(0x7f0d0077, float:1.8742356E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x01b3:
            r10 = 77
            if (r0 != r10) goto L_0x01d3
            r0 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r7] = r4
            r2[r5] = r32
            java.lang.String r3 = "PaymentInfoHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558460(0x7f0d003c, float:1.8742236E38)
            r3 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r3
            goto L_0x0634
        L_0x01d3:
            if (r0 != r15) goto L_0x01fe
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x01e1
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x01e6
        L_0x01e1:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x01e6:
            r2 = 2131628112(0x7f0e1050, float:1.8883507E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x01fe:
            r10 = 35
            if (r0 != r10) goto L_0x0231
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x020e
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0219
        L_0x020e:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0218
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0219
        L_0x0218:
            r0 = r12
        L_0x0219:
            r2 = 2131628113(0x7f0e1051, float:1.888351E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x0231:
            r10 = 31
            if (r0 != r10) goto L_0x025e
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0241
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0246
        L_0x0241:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0246:
            r2 = 2131628110(0x7f0e104e, float:1.8883503E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x025e:
            r10 = 38
            if (r0 != r10) goto L_0x0291
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x027d
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628120(0x7f0e1058, float:1.8883524E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r0 = r0.title
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x028a
        L_0x027d:
            r0 = 2131628119(0x7f0e1057, float:1.8883522E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x028a:
            r2 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x0291:
            r10 = 42
            if (r0 != r10) goto L_0x02a9
            r0 = 2131628093(0x7f0e103d, float:1.8883469E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x02a9:
            r10 = 43
            if (r0 != r10) goto L_0x02c1
            r0 = 2131628094(0x7f0e103e, float:1.888347E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x02c1:
            int r10 = r1.currentAction
            r15 = 39
            if (r10 != r15) goto L_0x02db
            r0 = 2131628027(0x7f0e0ffb, float:1.8883335E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558523(0x7f0d007b, float:1.8742364E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x02db:
            r15 = 40
            if (r10 != r15) goto L_0x0348
            r0 = 2131628026(0x7f0e0ffa, float:1.8883333E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558522(0x7f0d007a, float:1.8742362E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            android.widget.TextView r3 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r4 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r4.<init>()
            r3.setMovementMethod(r4)
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            java.lang.String r4 = "**"
            int r4 = r0.indexOf(r4)
            java.lang.String r8 = "**"
            int r0 = r0.lastIndexOf(r8)
            if (r4 < 0) goto L_0x0345
            if (r0 < 0) goto L_0x0345
            if (r4 == r0) goto L_0x0345
            int r8 = r0 + 2
            r3.replace(r0, r8, r12)
            int r8 = r4 + 2
            r3.replace(r4, r8, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r8 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0341 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0341 }
            r9.<init>()     // Catch:{ Exception -> 0x0341 }
            java.lang.String r10 = "tg://openmessage?user_id="
            r9.append(r10)     // Catch:{ Exception -> 0x0341 }
            int r10 = r1.currentAccount     // Catch:{ Exception -> 0x0341 }
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)     // Catch:{ Exception -> 0x0341 }
            int r10 = r10.getClientUserId()     // Catch:{ Exception -> 0x0341 }
            r9.append(r10)     // Catch:{ Exception -> 0x0341 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0341 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0341 }
            int r0 = r0 - r14
            r9 = 33
            r3.setSpan(r8, r4, r0, r9)     // Catch:{ Exception -> 0x0341 }
            goto L_0x0345
        L_0x0341:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0345:
            r0 = r3
            goto L_0x0634
        L_0x0348:
            if (r0 != r13) goto L_0x0373
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0356
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x035b
        L_0x0356:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x035b:
            r2 = 2131628111(0x7f0e104f, float:1.8883505E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x0373:
            r15 = 32
            if (r0 != r15) goto L_0x03a0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0383
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0388
        L_0x0383:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0388:
            r2 = 2131628084(0x7f0e1034, float:1.888345E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558518(0x7f0d0076, float:1.8742354E38)
            r1.timeLeft = r8
            goto L_0x0634
        L_0x03a0:
            r8 = 9
            if (r0 == r8) goto L_0x05ff
            r8 = 10
            if (r0 != r8) goto L_0x03aa
            goto L_0x05ff
        L_0x03aa:
            r8 = 8
            if (r0 != r8) goto L_0x03c4
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r7] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0631
        L_0x03c4:
            r8 = 22
            if (r0 != r8) goto L_0x0430
            r8 = 0
            int r0 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x03e6
            if (r4 != 0) goto L_0x03db
            r0 = 2131626023(0x7f0e0827, float:1.887927E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x03db:
            r0 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x03e6:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r2 = -r2
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x0418
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0418
            if (r4 != 0) goto L_0x040d
            r0 = 2131626019(0x7f0e0823, float:1.8879262E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x040d:
            r0 = 2131626020(0x7f0e0824, float:1.8879264E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x0418:
            if (r4 != 0) goto L_0x0425
            r0 = 2131626021(0x7f0e0825, float:1.8879266E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x0425:
            r0 = 2131626022(0x7f0e0826, float:1.8879268E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x0430:
            r8 = 23
            if (r0 != r8) goto L_0x043f
            r0 = 2131624827(0x7f0e037b, float:1.8876845E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0631
        L_0x043f:
            r8 = 6
            if (r0 != r8) goto L_0x045b
            r0 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624293(0x7f0e0165, float:1.8875762E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r13 = 48
            goto L_0x0634
        L_0x045b:
            r8 = 13
            if (r10 != r8) goto L_0x0478
            r0 = 2131627078(0x7f0e0CLASSNAME, float:1.888141E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627079(0x7f0e0CLASSNAME, float:1.8881412E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558528(0x7f0d0080, float:1.8742374E38)
        L_0x0474:
            r13 = 44
            goto L_0x0634
        L_0x0478:
            r8 = 14
            if (r10 != r8) goto L_0x0492
            r0 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627081(0x7f0e0CLASSNAME, float:1.8881416E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558529(0x7f0d0081, float:1.8742376E38)
            goto L_0x0474
        L_0x0492:
            r8 = 7
            if (r0 != r8) goto L_0x04bb
            r0 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04b6
            r2 = 2131624301(0x7f0e016d, float:1.8875778E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x04b5:
            r11 = r2
        L_0x04b6:
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            goto L_0x0634
        L_0x04bb:
            r8 = 20
            if (r0 == r8) goto L_0x04f9
            r8 = 21
            if (r0 != r8) goto L_0x04c4
            goto L_0x04f9
        L_0x04c4:
            r2 = 19
            if (r0 != r2) goto L_0x04cb
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x04b6
        L_0x04cb:
            r2 = 3
            if (r0 != r2) goto L_0x04d8
            r0 = 2131624797(0x7f0e035d, float:1.8876784E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x04e1
        L_0x04d8:
            r0 = 2131624835(0x7f0e0383, float:1.887686E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x04e1:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04b6
            r2 = 2131624798(0x7f0e035e, float:1.8876786E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x04b5
        L_0x04f9:
            r8 = r32
            org.telegram.messenger.MessagesController$DialogFilter r8 = (org.telegram.messenger.MessagesController.DialogFilter) r8
            r9 = 0
            int r15 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r15 == 0) goto L_0x05a7
            int r4 = (int) r2
            if (r4 != 0) goto L_0x051a
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r9 = 32
            long r2 = r2 >> r9
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            int r4 = r2.user_id
        L_0x051a:
            if (r4 <= 0) goto L_0x0564
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            r3 = 20
            if (r0 != r3) goto L_0x0549
            r3 = 2131625548(0x7f0e064c, float:1.8878307E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r7] = r2
            java.lang.String r2 = r8.name
            r4[r5] = r2
            java.lang.String r2 = "FilterUserAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05ee
        L_0x0549:
            r3 = 2131625549(0x7f0e064d, float:1.887831E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r7] = r2
            java.lang.String r2 = r8.name
            r4[r5] = r2
            java.lang.String r2 = "FilterUserRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05ee
        L_0x0564:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            r3 = 20
            if (r0 != r3) goto L_0x058f
            r3 = 2131625487(0x7f0e060f, float:1.8878183E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r2 = r2.title
            r4[r7] = r2
            java.lang.String r2 = r8.name
            r4[r5] = r2
            java.lang.String r2 = "FilterChatAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05ee
        L_0x058f:
            r3 = 2131625488(0x7f0e0610, float:1.8878185E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String r2 = r2.title
            r4[r7] = r2
            java.lang.String r2 = r8.name
            r4[r5] = r2
            java.lang.String r2 = "FilterChatRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05ee
        L_0x05a7:
            r2 = 20
            if (r0 != r2) goto L_0x05cd
            r2 = 2131625491(0x7f0e0613, float:1.8878191E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            java.lang.String r9 = "Chats"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4)
            r3[r7] = r4
            java.lang.String r4 = r8.name
            r3[r5] = r4
            java.lang.String r4 = "FilterChatsAddedToExisting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x05ee
        L_0x05cd:
            r2 = 2131625492(0x7f0e0614, float:1.8878193E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            java.lang.String r9 = "Chats"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r9, r4)
            r3[r7] = r4
            java.lang.String r4 = r8.name
            r3[r5] = r4
            java.lang.String r4 = "FilterChatsRemovedFrom"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x05ee:
            r3 = 20
            if (r0 != r3) goto L_0x05f6
            r0 = 2131558432(0x7f0d0020, float:1.874218E38)
            goto L_0x05f9
        L_0x05f6:
            r0 = 2131558433(0x7f0d0021, float:1.8742182E38)
        L_0x05f9:
            r26 = r2
            r2 = r0
            r0 = r26
            goto L_0x0634
        L_0x05ff:
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r3 = 9
            if (r0 != r3) goto L_0x061c
            r0 = 2131625242(0x7f0e051a, float:1.8877686E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r7] = r2
            java.lang.String r2 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0631
        L_0x061c:
            r0 = 2131625243(0x7f0e051b, float:1.8877688E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r7] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0631:
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
        L_0x0634:
            android.widget.TextView r3 = r1.infoTextView
            r3.setText(r0)
            if (r2 == 0) goto L_0x0661
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r2, r13, r13)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r0.setPlayInDirectionOfCustomEndFrame(r7)
            int r2 = r0.getFramesCount()
            r0.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0668
        L_0x0661:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0668:
            if (r11 == 0) goto L_0x06a9
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r11)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x06d2
        L_0x06a9:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x06d2:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x0d76
        L_0x06d9:
            int r11 = r1.currentAction
            r15 = 45
            r8 = 60
            if (r11 == r15) goto L_0x0d7b
            r9 = 46
            if (r11 == r9) goto L_0x0d7b
            r9 = 47
            if (r11 == r9) goto L_0x0d7b
            r9 = 51
            if (r11 == r9) goto L_0x0d7b
            r9 = 50
            if (r11 == r9) goto L_0x0d7b
            r9 = 52
            if (r11 == r9) goto L_0x0d7b
            r9 = 53
            if (r11 == r9) goto L_0x0d7b
            r9 = 54
            if (r11 == r9) goto L_0x0d7b
            r9 = 55
            if (r11 == r9) goto L_0x0d7b
            r9 = 56
            if (r11 == r9) goto L_0x0d7b
            r9 = 57
            if (r11 == r9) goto L_0x0d7b
            r9 = 58
            if (r11 == r9) goto L_0x0d7b
            r9 = 59
            if (r11 == r9) goto L_0x0d7b
            if (r11 == r8) goto L_0x0d7b
            r9 = 71
            if (r11 == r9) goto L_0x0d7b
            r9 = 70
            if (r11 == r9) goto L_0x0d7b
            r9 = 75
            if (r11 == r9) goto L_0x0d7b
            r9 = 76
            if (r11 == r9) goto L_0x0d7b
            r9 = 41
            if (r11 != r9) goto L_0x0729
            goto L_0x0d7b
        L_0x0729:
            r8 = 24
            if (r11 == r8) goto L_0x0CLASSNAME
            r8 = 25
            if (r11 != r8) goto L_0x0733
            goto L_0x0CLASSNAME
        L_0x0733:
            r8 = 11
            if (r11 != r8) goto L_0x07a3
            r0 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624420(0x7f0e01e4, float:1.887602E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r2.setAnimation(r3, r13, r13)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
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
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d76
        L_0x07a3:
            r8 = 15
            if (r11 != r8) goto L_0x0873
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626519(0x7f0e0a17, float:1.8880276E38)
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
            r2 = 2131558427(0x7f0d001b, float:1.874217E38)
            r0.setAnimation(r2, r13, r13)
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
            r6.leftMargin = r2
            r6.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r2
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
            if (r4 < 0) goto L_0x0841
            if (r0 < 0) goto L_0x0841
            if (r4 == r0) goto L_0x0841
            int r3 = r0 + 1
            r2.replace(r0, r3, r12)
            int r3 = r4 + 1
            r2.replace(r4, r3, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r3.<init>(r6)
            int r0 = r0 - r5
            r6 = 33
            r2.setSpan(r3, r4, r0, r6)
        L_0x0841:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setMaxLines(r14)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r7)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d76
        L_0x0873:
            r8 = 16
            if (r11 == r8) goto L_0x0a9f
            r8 = 17
            if (r11 != r8) goto L_0x087d
            goto L_0x0a9f
        L_0x087d:
            r8 = 18
            if (r11 != r8) goto L_0x08fd
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
            r2.setTextSize(r5, r3)
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
            r6.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.bottomMargin = r0
            r0 = -1
            r6.height = r0
            r0 = 51
            r10.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r10.bottomMargin = r0
            r10.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r0.setAnimation(r2, r13, r13)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d76
        L_0x08fd:
            r4 = 12
            if (r11 != r4) goto L_0x0997
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624930(0x7f0e03e2, float:1.8877054E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166085(0x7var_, float:1.7946405E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r0
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
            if (r4 < 0) goto L_0x0970
            if (r0 < 0) goto L_0x0970
            if (r4 == r0) goto L_0x0970
            int r3 = r0 + 1
            r2.replace(r0, r3, r12)
            int r3 = r4 + 1
            r2.replace(r4, r3, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r3.<init>(r6)
            int r0 = r0 - r5
            r6 = 33
            r2.setSpan(r3, r4, r0, r6)
        L_0x0970:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setMaxLines(r14)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            goto L_0x0d76
        L_0x0997:
            if (r11 == r14) goto L_0x0a3c
            r4 = 4
            if (r11 != r4) goto L_0x099e
            goto L_0x0a3c
        L_0x099e:
            r0 = 1110704128(0x42340000, float:45.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.topMargin = r0
            r6.rightMargin = r7
            android.widget.TextView r0 = r1.infoTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r4)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 8
            r0.setVisibility(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r4)
            int r0 = r1.currentAction
            if (r0 != 0) goto L_0x09e2
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625751(0x7f0e0717, float:1.8878719E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x0a2a
        L_0x09e2:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x0a1c
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r4.getChat(r0)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r4 == 0) goto L_0x0a0d
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0a0d
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624705(0x7f0e0301, float:1.8876597E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x0a2a
        L_0x0a0d:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625704(0x7f0e06e8, float:1.8878623E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x0a2a
        L_0x0a1c:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624801(0x7f0e0361, float:1.8876792E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
        L_0x0a2a:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r4 = r1.currentAction
            if (r4 != 0) goto L_0x0a36
            r4 = 1
            goto L_0x0a37
        L_0x0a36:
            r4 = 0
        L_0x0a37:
            r0.addDialogAction(r2, r4)
            goto L_0x0d76
        L_0x0a3c:
            if (r0 != r14) goto L_0x0a4d
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624797(0x7f0e035d, float:1.8876784E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0a5b
        L_0x0a4d:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624835(0x7f0e0383, float:1.887686E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0a5b:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.topMargin = r0
            r6.rightMargin = r7
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558415(0x7f0d000f, float:1.8742145E38)
            r0.setAnimation(r2, r13, r13)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d76
        L_0x0a9f:
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
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
            if (r2 == 0) goto L_0x0ae3
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625169(0x7f0e04d1, float:1.8877538E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165396(0x7var_d4, float:1.7945008E38)
            r0.setImageResource(r2)
            goto L_0x0b8b
        L_0x0ae3:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0b00
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625038(0x7f0e044e, float:1.8877273E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0afd:
            r8 = 1096810496(0x41600000, float:14.0)
            goto L_0x0b59
        L_0x0b00:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0b33
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r9, r7)
            r3.setText(r2)
            goto L_0x0afd
        L_0x0b33:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625168(0x7f0e04d0, float:1.8877536E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r0
            java.lang.String r8 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r9, r7)
            r2.setText(r3)
        L_0x0b59:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r10.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r10.height = r0
        L_0x0b8b:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627340(0x7f0e0d4c, float:1.8881942E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0bdd
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
            r2.setVisibility(r7)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r7)
            goto L_0x0bed
        L_0x0bdd:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0bed:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r2
            r6.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.bottomMargin = r0
            r0 = -1
            r6.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            goto L_0x0d76
        L_0x0CLASSNAME:
            r2 = 8
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r32
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r7)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0ce2
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r4.setTypeface(r8)
            android.widget.TextView r4 = r1.infoTextView
            r8 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r5, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r9 = "BODY.**"
            r4.setLayerColor(r9, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r9 = "Wibe Big.**"
            r4.setLayerColor(r9, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r9 = "Wibe Big 3.**"
            r4.setLayerColor(r9, r8)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r8 = "Wibe Small.**"
            r4.setLayerColor(r8, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627034(0x7f0e0c1a, float:1.8881321E38)
            java.lang.String r8 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558449(0x7f0d0031, float:1.8742214E38)
            r8 = 28
            r9 = 28
            r2.setAnimation(r4, r8, r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r7)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r7)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0cbc
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627036(0x7f0e0c1c, float:1.8881325E38)
            java.lang.Object[] r8 = new java.lang.Object[r14]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r8[r7] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r14)
            r8[r5] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            r2.setText(r0)
            goto L_0x0cd3
        L_0x0cbc:
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627035(0x7f0e0c1b, float:1.8881323E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r14)
            r4[r7] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0cd3:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r0
            goto L_0x0d65
        L_0x0ce2:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r3)
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
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627033(0x7f0e0CLASSNAME, float:1.888132E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558444(0x7f0d002c, float:1.8742204E38)
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
            r0.setVisibility(r7)
        L_0x0d65:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0d76:
            r0 = 0
            r10 = 1096810496(0x41600000, float:14.0)
            goto L_0x126e
        L_0x0d7b:
            android.widget.ImageView r9 = r1.undoImageView
            r10 = 8
            r9.setVisibility(r10)
            org.telegram.ui.Components.RLottieImageView r9 = r1.leftImageView
            r9.setVisibility(r7)
            android.widget.TextView r9 = r1.infoTextView
            android.graphics.Typeface r10 = android.graphics.Typeface.DEFAULT
            r9.setTypeface(r10)
            int r9 = r1.currentAction
            r10 = 76
            r11 = 1091567616(0x41100000, float:9.0)
            if (r9 != r10) goto L_0x0dbe
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624592(0x7f0e0290, float:1.8876368E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558436(0x7f0d0024, float:1.8742188E38)
            r0.setAnimation(r2, r13, r13)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
        L_0x0db9:
            r0 = 1
            r10 = 1096810496(0x41600000, float:14.0)
            goto L_0x1240
        L_0x0dbe:
            r10 = 75
            if (r9 != r10) goto L_0x0de6
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625683(0x7f0e06d3, float:1.887858E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r0.setAnimation(r2, r13, r13)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            goto L_0x0db9
        L_0x0de6:
            r10 = 70
            if (r0 != r10) goto L_0x0e58
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r32
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r7)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x0e0a
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e27
        L_0x0e0a:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x0e17
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e27
        L_0x0e17:
            if (r0 < r8) goto L_0x0e21
            int r0 = r0 / r8
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e27
        L_0x0e21:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0e27:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558431(0x7f0d001f, float:1.8742178E38)
            r0.setAnimation(r2, r13, r13)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r7, r7, r7, r2)
            goto L_0x0db9
        L_0x0e58:
            r0 = 71
            if (r9 != r0) goto L_0x0e8c
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r13, r13)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r7, r7, r7, r2)
            r10 = 1096810496(0x41600000, float:14.0)
            goto L_0x123f
        L_0x0e8c:
            r0 = 45
            if (r9 != r0) goto L_0x0eb5
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625798(0x7f0e0746, float:1.8878814E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r0.setAnimation(r2, r13, r13)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            goto L_0x0db9
        L_0x0eb5:
            r0 = 46
            if (r9 != r0) goto L_0x0ede
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625799(0x7f0e0747, float:1.8878816E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r0.setAnimation(r2, r13, r13)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            goto L_0x0db9
        L_0x0ede:
            r0 = 47
            if (r9 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625805(0x7f0e074d, float:1.8878828E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558455(0x7f0d0037, float:1.8742226E38)
            r0.setAnimation(r2, r13, r13)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r7, r7, r7, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r10 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r10)
            r0 = 1
            goto L_0x1240
        L_0x0var_:
            r10 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r9 != r0) goto L_0x0f3c
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r2, r13, r13)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x123f
        L_0x0f3c:
            r0 = 50
            if (r9 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r0.setAnimation(r2, r13, r13)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x123f
        L_0x0var_:
            r0 = 52
            if (r9 == r0) goto L_0x11bc
            r0 = 56
            if (r9 == r0) goto L_0x11bc
            r0 = 57
            if (r9 == r0) goto L_0x11bc
            r0 = 58
            if (r9 == r0) goto L_0x11bc
            r0 = 59
            if (r9 == r0) goto L_0x11bc
            if (r9 != r8) goto L_0x0f7b
            goto L_0x11bc
        L_0x0f7b:
            r0 = 54
            if (r9 != r0) goto L_0x0fa4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624741(0x7f0e0325, float:1.887667E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558475(0x7f0d004b, float:1.8742267E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x123f
        L_0x0fa4:
            r0 = 55
            if (r9 != r0) goto L_0x0fcd
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624740(0x7f0e0324, float:1.8876668E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558474(0x7f0d004a, float:1.8742265E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x123f
        L_0x0fcd:
            r0 = 41
            if (r9 != r0) goto L_0x107a
            if (r32 != 0) goto L_0x1048
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r8 = (long) r0
            int r0 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r0 != 0) goto L_0x0ff4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625828(0x7f0e0764, float:1.8878875E38)
            java.lang.String r3 = "InvLinkToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x106c
        L_0x0ff4:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x101f
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625827(0x7f0e0763, float:1.8878873E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = r0.title
            r4[r7] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x106c
        L_0x101f:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625829(0x7f0e0765, float:1.8878877E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r7] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x106c
        L_0x1048:
            r0 = r32
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625826(0x7f0e0762, float:1.887887E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r8 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r8, r0)
            r4[r7] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x106c:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r0.setAnimation(r2, r13, r13)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x123f
        L_0x107a:
            r0 = 53
            if (r9 != r0) goto L_0x123f
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r32 != 0) goto L_0x1165
            int r4 = r1.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r8 = (long) r4
            int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r4 != 0) goto L_0x10c7
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x10a9
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625655(0x7f0e06b7, float:1.8878524E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x10bb
        L_0x10a9:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625659(0x7f0e06bb, float:1.8878532E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x10bb:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558472(0x7f0d0048, float:1.874226E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x11b6
        L_0x10c7:
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x1111
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x10f8
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625654(0x7f0e06b6, float:1.8878522E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = r2.title
            r4[r7] = r2
            java.lang.String r2 = "FwdMessageToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x115a
        L_0x10f8:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625658(0x7f0e06ba, float:1.887853E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = r2.title
            r4[r7] = r2
            java.lang.String r2 = "FwdMessagesToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x115a
        L_0x1111:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x1140
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessageToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x115a
        L_0x1140:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625660(0x7f0e06bc, float:1.8878534E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessagesToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x115a:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x11b6
        L_0x1165:
            r2 = r32
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x1190
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625653(0x7f0e06b5, float:1.887852E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r8 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessageToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x11ac
        L_0x1190:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625657(0x7f0e06b9, float:1.8878528E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r8 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessagesToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x11ac:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x11b6:
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x123f
        L_0x11bc:
            r0 = 2131558422(0x7f0d0016, float:1.874216E38)
            if (r9 != r8) goto L_0x11d0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626889(0x7f0e0b89, float:1.8881027E38)
            java.lang.String r4 = "PhoneCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x122d
        L_0x11d0:
            r2 = 56
            if (r9 != r2) goto L_0x11e3
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627894(0x7f0e0var_, float:1.8883065E38)
            java.lang.String r4 = "UsernameCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x122d
        L_0x11e3:
            r2 = 57
            if (r9 != r2) goto L_0x11f6
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.String r4 = "HashtagCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x122d
        L_0x11f6:
            r2 = 52
            if (r9 != r2) goto L_0x1209
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626114(0x7f0e0882, float:1.8879455E38)
            java.lang.String r4 = "MessageCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x122d
        L_0x1209:
            r2 = 59
            if (r9 != r2) goto L_0x121f
            r0 = 2131558519(0x7f0d0077, float:1.8742356E38)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625944(0x7f0e07d8, float:1.887911E38)
            java.lang.String r4 = "LinkCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x122d
        L_0x121f:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627679(0x7f0e0e9f, float:1.888263E38)
            java.lang.String r4 = "TextCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x122d:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 30
            r2.setAnimation(r0, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
        L_0x123f:
            r0 = 0
        L_0x1240:
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
            r6.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r6.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.playAnimation()
        L_0x126e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r1.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r1.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x129b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r12 = r3.toString()
        L_0x129b:
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r27.isMultilineSubInfo()
            if (r2 == 0) goto L_0x12f0
            android.view.ViewParent r0 = r27.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x12bb
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x12bb:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r28 = r27
            r29 = r2
            r30 = r0
            r31 = r3
            r32 = r4
            r33 = r6
            r28.measureChildWithMargins(r29, r30, r31, r32, r33)
            android.widget.TextView r0 = r1.subinfoTextView
            int r0 = r0.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.undoViewHeight = r0
            goto L_0x1394
        L_0x12f0:
            boolean r2 = r27.hasSubInfo()
            if (r2 == 0) goto L_0x1300
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x1394
        L_0x1300:
            android.view.ViewParent r2 = r27.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x1394
            android.view.ViewParent r2 = r27.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x1322
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x1322:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r1.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r8 = 0
            r28 = r27
            r29 = r2
            r30 = r3
            r31 = r4
            r32 = r6
            r33 = r8
            r28.measureChildWithMargins(r29, r30, r31, r32, r33)
            android.widget.TextView r2 = r1.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r1.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x135e
            r4 = 17
            if (r3 == r4) goto L_0x135e
            r4 = 18
            if (r3 != r4) goto L_0x135b
            goto L_0x135e
        L_0x135b:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1360
        L_0x135e:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x1360:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r2 = r2 + r3
            r1.undoViewHeight = r2
            int r3 = r1.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x137a
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x1394
        L_0x137a:
            r4 = 25
            if (r3 != r4) goto L_0x138b
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x1394
        L_0x138b:
            if (r0 == 0) goto L_0x1394
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r0
            r1.undoViewHeight = r2
        L_0x1394:
            int r0 = r27.getVisibility()
            if (r0 == 0) goto L_0x13fb
            r1.setVisibility(r7)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x13a4
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x13a6
        L_0x13a4:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x13a6:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setTranslationY(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r5]
            android.util.Property r3 = android.view.View.TRANSLATION_Y
            float[] r4 = new float[r14]
            boolean r6 = r1.fromTop
            if (r6 == 0) goto L_0x13c5
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x13c7
        L_0x13c5:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x13c7:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r1.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r1.fromTop
            if (r6 == 0) goto L_0x13da
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x13dc
        L_0x13da:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x13dc:
            float r8 = r1.additionalTranslationY
            float r6 = r6 * r8
            r4[r5] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r1, r3, r4)
            r2[r7] = r3
            r0.playTogether(r2)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x13fb:
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
