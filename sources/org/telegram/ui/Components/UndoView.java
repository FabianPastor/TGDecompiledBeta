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
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43;
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

    /* JADX WARNING: Removed duplicated region for block: B:410:0x128a  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x12b1  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x12f6  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x13a0  */
    /* JADX WARNING: Removed duplicated region for block: B:457:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(long r21, int r23, java.lang.Object r24, java.lang.Object r25, java.lang.Runnable r26, java.lang.Runnable r27) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r0 = r23
            r4 = r24
            java.lang.Runnable r5 = r1.currentActionRunnable
            if (r5 == 0) goto L_0x000f
            r5.run()
        L_0x000f:
            r5 = 1
            r1.isShown = r5
            r6 = r26
            r1.currentActionRunnable = r6
            r6 = r27
            r1.currentCancelRunnable = r6
            r1.currentDialogId = r2
            r1.currentAction = r0
            r6 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r6
            r1.currentInfoObject = r4
            long r6 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r6
            android.widget.TextView r6 = r1.undoTextView
            r7 = 2131627750(0x7f0e0ee6, float:1.8882773E38)
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
            boolean r11 = r20.isTooltipAction()
            r13 = 42
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r12 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r15 = 30
            r8 = 2
            r19 = r10
            r9 = 3000(0xbb8, double:1.482E-320)
            r14 = 36
            if (r11 == 0) goto L_0x06df
            r11 = 74
            r19 = 0
            if (r0 != r11) goto L_0x00e8
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r7)
            r0 = 2131627125(0x7f0e0CLASSNAME, float:1.8881506E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627134(0x7f0e0c7e, float:1.8881524E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r2 = 2131558437(0x7f0d0025, float:1.874219E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            r3 = r19
            r2 = 36
            r14 = 2131558437(0x7f0d0025, float:1.874219E38)
            goto L_0x063a
        L_0x00e8:
            r11 = 34
            if (r0 != r11) goto L_0x012f
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131627987(0x7f0e0fd3, float:1.8883254E38)
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
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForUser(r0, r7)
            java.lang.String r13 = "50_50"
            r4.setImage((org.telegram.messenger.ImageLocation) r11, (java.lang.String) r13, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r7)
            r1.timeLeft = r9
            r0 = r2
        L_0x0128:
            r3 = r19
            r2 = 36
            r14 = 0
            goto L_0x063a
        L_0x012f:
            r11 = 37
            if (r0 != r11) goto L_0x018b
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x015f
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUser(r2, r7)
            java.lang.String r11 = "50_50"
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r11, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r2)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x0172
        L_0x015f:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForChat(r2, r7)
            java.lang.String r11 = "50_50"
            r3.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r11, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r2)
            java.lang.String r0 = r2.title
        L_0x0172:
            r2 = 2131628040(0x7f0e1008, float:1.8883361E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r7)
            r1.timeLeft = r9
            goto L_0x0128
        L_0x018b:
            r11 = 33
            if (r0 != r11) goto L_0x01a6
            r0 = 2131627970(0x7f0e0fc2, float:1.888322E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558513(0x7f0d0071, float:1.8742344E38)
            r1.timeLeft = r9
            r3 = r19
            r2 = 36
            r14 = 2131558513(0x7f0d0071, float:1.8742344E38)
            goto L_0x063a
        L_0x01a6:
            if (r0 != r15) goto L_0x01d8
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x01b4
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x01b9
        L_0x01b4:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x01b9:
            r2 = 2131628038(0x7f0e1006, float:1.8883357E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r1.timeLeft = r9
        L_0x01cf:
            r3 = r19
            r2 = 36
            r14 = 2131558514(0x7f0d0072, float:1.8742346E38)
            goto L_0x063a
        L_0x01d8:
            r11 = 35
            if (r0 != r11) goto L_0x020a
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x01e8
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x01f3
        L_0x01e8:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x01f2
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x01f3
        L_0x01f2:
            r0 = r12
        L_0x01f3:
            r2 = 2131628039(0x7f0e1007, float:1.888336E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r1.timeLeft = r9
            goto L_0x01cf
        L_0x020a:
            r11 = 31
            if (r0 != r11) goto L_0x023e
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x021a
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x021f
        L_0x021a:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x021f:
            r2 = 2131628036(0x7f0e1004, float:1.8883353E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r1.timeLeft = r9
        L_0x0235:
            r3 = r19
            r2 = 36
            r14 = 2131558520(0x7f0d0078, float:1.8742358E38)
            goto L_0x063a
        L_0x023e:
            r11 = 38
            if (r0 != r11) goto L_0x0278
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x025d
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628046(0x7f0e100e, float:1.8883374E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r0 = r0.title
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x026a
        L_0x025d:
            r0 = 2131628045(0x7f0e100d, float:1.8883372E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x026a:
            r2 = 2131558507(0x7f0d006b, float:1.8742332E38)
            r1.timeLeft = r9
            r3 = r19
            r2 = 36
            r14 = 2131558507(0x7f0d006b, float:1.8742332E38)
            goto L_0x063a
        L_0x0278:
            if (r0 != r13) goto L_0x0295
            r0 = 2131628022(0x7f0e0ff6, float:1.8883325E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558441(0x7f0d0029, float:1.8742198E38)
            r1.timeLeft = r9
            r3 = r19
            r2 = 36
            r14 = 2131558441(0x7f0d0029, float:1.8742198E38)
            goto L_0x063a
        L_0x0295:
            r11 = 43
            if (r0 != r11) goto L_0x02b4
            r0 = 2131628023(0x7f0e0ff7, float:1.8883327E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r1.timeLeft = r9
            r3 = r19
            r2 = 36
            r14 = 2131558446(0x7f0d002e, float:1.8742208E38)
            goto L_0x063a
        L_0x02b4:
            int r11 = r1.currentAction
            r13 = 39
            if (r11 != r13) goto L_0x02d5
            r0 = 2131627965(0x7f0e0fbd, float:1.888321E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558517(0x7f0d0075, float:1.8742352E38)
            r1.timeLeft = r9
            r3 = r19
            r2 = 36
            r14 = 2131558517(0x7f0d0075, float:1.8742352E38)
            goto L_0x063a
        L_0x02d5:
            r13 = 40
            if (r11 != r13) goto L_0x0349
            r0 = 2131627964(0x7f0e0fbc, float:1.8883207E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558516(0x7f0d0074, float:1.874235E38)
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
            java.lang.String r9 = "**"
            int r0 = r0.lastIndexOf(r9)
            if (r4 < 0) goto L_0x033f
            if (r0 < 0) goto L_0x033f
            if (r4 == r0) goto L_0x033f
            int r9 = r0 + 2
            r3.replace(r0, r9, r12)
            int r9 = r4 + 2
            r3.replace(r4, r9, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x033b }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x033b }
            r10.<init>()     // Catch:{ Exception -> 0x033b }
            java.lang.String r11 = "tg://openmessage?user_id="
            r10.append(r11)     // Catch:{ Exception -> 0x033b }
            int r11 = r1.currentAccount     // Catch:{ Exception -> 0x033b }
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)     // Catch:{ Exception -> 0x033b }
            int r11 = r11.getClientUserId()     // Catch:{ Exception -> 0x033b }
            r10.append(r11)     // Catch:{ Exception -> 0x033b }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x033b }
            r9.<init>(r10)     // Catch:{ Exception -> 0x033b }
            int r0 = r0 - r8
            r10 = 33
            r3.setSpan(r9, r4, r0, r10)     // Catch:{ Exception -> 0x033b }
            goto L_0x033f
        L_0x033b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x033f:
            r0 = r3
            r3 = r19
            r2 = 36
            r14 = 2131558516(0x7f0d0074, float:1.874235E38)
            goto L_0x063a
        L_0x0349:
            if (r0 != r14) goto L_0x0374
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0357
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x035c
        L_0x0357:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x035c:
            r2 = 2131628037(0x7f0e1005, float:1.8883355E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r1.timeLeft = r9
            goto L_0x0235
        L_0x0374:
            r13 = 32
            if (r0 != r13) goto L_0x03a8
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0384
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0389
        L_0x0384:
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0389:
            r2 = 2131628018(0x7f0e0ff2, float:1.8883317E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r7] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r1.timeLeft = r9
            r3 = r19
            r2 = 36
            r14 = 2131558512(0x7f0d0070, float:1.8742342E38)
            goto L_0x063a
        L_0x03a8:
            r9 = 9
            if (r0 == r9) goto L_0x0601
            r9 = 10
            if (r0 != r9) goto L_0x03b2
            goto L_0x0601
        L_0x03b2:
            r9 = 8
            if (r0 != r9) goto L_0x03cc
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626481(0x7f0e09f1, float:1.88802E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r7] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0633
        L_0x03cc:
            r9 = 22
            if (r0 != r9) goto L_0x0438
            r9 = 0
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x03ee
            if (r4 != 0) goto L_0x03e3
            r0 = 2131626008(0x7f0e0818, float:1.887924E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x03e3:
            r0 = 2131626009(0x7f0e0819, float:1.8879242E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x03ee:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r2 = -r2
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x0420
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0420
            if (r4 != 0) goto L_0x0415
            r0 = 2131626004(0x7f0e0814, float:1.8879232E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x0415:
            r0 = 2131626005(0x7f0e0815, float:1.8879234E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x0420:
            if (r4 != 0) goto L_0x042d
            r0 = 2131626006(0x7f0e0816, float:1.8879236E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x042d:
            r0 = 2131626007(0x7f0e0817, float:1.8879238E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x0438:
            r9 = 23
            if (r0 != r9) goto L_0x0447
            r0 = 2131624824(0x7f0e0378, float:1.8876839E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0633
        L_0x0447:
            r9 = 6
            if (r0 != r9) goto L_0x0465
            r0 = 2131624291(0x7f0e0163, float:1.8875758E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r2 = 48
        L_0x0461:
            r3 = r19
            goto L_0x063a
        L_0x0465:
            r9 = 13
            if (r11 != r9) goto L_0x0481
            r0 = 2131627053(0x7f0e0c2d, float:1.888136E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627054(0x7f0e0c2e, float:1.8881362E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14 = 2131558522(0x7f0d007a, float:1.8742362E38)
        L_0x047e:
            r2 = 44
            goto L_0x0461
        L_0x0481:
            r9 = 14
            if (r11 != r9) goto L_0x049b
            r0 = 2131627055(0x7f0e0c2f, float:1.8881364E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627056(0x7f0e0CLASSNAME, float:1.8881366E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r19 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14 = 2131558523(0x7f0d007b, float:1.8742364E38)
            goto L_0x047e
        L_0x049b:
            r9 = 7
            if (r0 != r9) goto L_0x04c9
            r0 = 2131624299(0x7f0e016b, float:1.8875774E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04c0
            r2 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x04be:
            r19 = r2
        L_0x04c0:
            r3 = r19
            r2 = 36
            r14 = 2131558417(0x7f0d0011, float:1.874215E38)
            goto L_0x063a
        L_0x04c9:
            r9 = 20
            if (r0 == r9) goto L_0x0507
            r9 = 21
            if (r0 != r9) goto L_0x04d2
            goto L_0x0507
        L_0x04d2:
            r2 = 19
            if (r0 != r2) goto L_0x04d9
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x04c0
        L_0x04d9:
            r2 = 3
            if (r0 != r2) goto L_0x04e6
            r0 = 2131624794(0x7f0e035a, float:1.8876778E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x04ef
        L_0x04e6:
            r0 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x04ef:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04c0
            r2 = 2131624795(0x7f0e035b, float:1.887678E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x04be
        L_0x0507:
            r9 = r25
            org.telegram.messenger.MessagesController$DialogFilter r9 = (org.telegram.messenger.MessagesController.DialogFilter) r9
            r10 = 0
            int r13 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r13 == 0) goto L_0x05b7
            int r4 = (int) r2
            if (r4 != 0) goto L_0x0528
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r10 = 32
            long r2 = r2 >> r10
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            int r4 = r2.user_id
        L_0x0528:
            if (r4 <= 0) goto L_0x0572
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            r3 = 20
            if (r0 != r3) goto L_0x0557
            r0 = 2131625539(0x7f0e0643, float:1.8878289E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r7] = r2
            java.lang.String r2 = r9.name
            r3[r5] = r2
            java.lang.String r2 = "FilterUserAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x0557:
            r0 = 2131625540(0x7f0e0644, float:1.887829E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r7] = r2
            java.lang.String r2 = r9.name
            r3[r5] = r2
            java.lang.String r2 = "FilterUserRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x0572:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            r3 = 20
            if (r0 != r3) goto L_0x059e
            r0 = 2131625478(0x7f0e0606, float:1.8878165E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r2 = r2.title
            r3[r7] = r2
            java.lang.String r2 = r9.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x059e:
            r0 = 2131625479(0x7f0e0607, float:1.8878167E38)
            java.lang.Object[] r3 = new java.lang.Object[r8]
            java.lang.String r2 = r2.title
            r3[r7] = r2
            java.lang.String r2 = r9.name
            r3[r5] = r2
            java.lang.String r2 = "FilterChatRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x05b7:
            r2 = 20
            if (r0 != r2) goto L_0x05de
            r0 = 2131625482(0x7f0e060a, float:1.8878173E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r2[r7] = r3
            java.lang.String r3 = r9.name
            r2[r5] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x05de:
            r0 = 2131625483(0x7f0e060b, float:1.8878175E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r2[r7] = r3
            java.lang.String r3 = r9.name
            r2[r5] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x0601:
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r3 = 9
            if (r0 != r3) goto L_0x061e
            r0 = 2131625233(0x7f0e0511, float:1.8877668E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r7] = r2
            java.lang.String r2 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0633
        L_0x061e:
            r0 = 2131625234(0x7f0e0512, float:1.887767E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3[r7] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0633:
            r3 = r19
            r2 = 36
            r14 = 2131558421(0x7f0d0015, float:1.8742157E38)
        L_0x063a:
            android.widget.TextView r4 = r1.infoTextView
            r4.setText(r0)
            if (r14 == 0) goto L_0x0667
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r14, r2, r2)
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
            goto L_0x066e
        L_0x0667:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x066e:
            if (r3 == 0) goto L_0x06af
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
            r0.setText(r3)
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
            goto L_0x06d8
        L_0x06af:
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
        L_0x06d8:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x0d7c
        L_0x06df:
            int r11 = r1.currentAction
            r15 = 45
            r9 = 60
            if (r11 == r15) goto L_0x0d81
            r10 = 46
            if (r11 == r10) goto L_0x0d81
            r10 = 47
            if (r11 == r10) goto L_0x0d81
            r10 = 51
            if (r11 == r10) goto L_0x0d81
            r10 = 50
            if (r11 == r10) goto L_0x0d81
            r10 = 52
            if (r11 == r10) goto L_0x0d81
            r10 = 53
            if (r11 == r10) goto L_0x0d81
            r10 = 54
            if (r11 == r10) goto L_0x0d81
            r10 = 55
            if (r11 == r10) goto L_0x0d81
            r10 = 56
            if (r11 == r10) goto L_0x0d81
            r10 = 57
            if (r11 == r10) goto L_0x0d81
            r10 = 58
            if (r11 == r10) goto L_0x0d81
            r10 = 59
            if (r11 == r10) goto L_0x0d81
            if (r11 == r9) goto L_0x0d81
            r10 = 71
            if (r11 == r10) goto L_0x0d81
            r10 = 70
            if (r11 == r10) goto L_0x0d81
            r10 = 75
            if (r11 == r10) goto L_0x0d81
            r10 = 76
            if (r11 == r10) goto L_0x0d81
            r10 = 41
            if (r11 != r10) goto L_0x072f
            goto L_0x0d81
        L_0x072f:
            r9 = 24
            if (r11 == r9) goto L_0x0c1a
            r9 = 25
            if (r11 != r9) goto L_0x0739
            goto L_0x0c1a
        L_0x0739:
            r9 = 11
            if (r11 != r9) goto L_0x07a9
            r0 = r4
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624419(0x7f0e01e3, float:1.8876017E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r2.setAnimation(r3, r14, r14)
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
            goto L_0x0d7c
        L_0x07a9:
            r9 = 15
            if (r11 != r9) goto L_0x0877
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625475(0x7f0e0603, float:1.887816E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558427(0x7f0d001b, float:1.874217E38)
            r0.setAnimation(r2, r14, r14)
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
            r0 = 2131625474(0x7f0e0602, float:1.8878157E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            int r3 = r0.indexOf(r13)
            int r0 = r0.lastIndexOf(r13)
            if (r3 < 0) goto L_0x0845
            if (r0 < 0) goto L_0x0845
            if (r3 == r0) goto L_0x0845
            int r4 = r0 + 1
            r2.replace(r0, r4, r12)
            int r4 = r3 + 1
            r2.replace(r3, r4, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/folders"
            r4.<init>(r6)
            int r0 = r0 - r5
            r6 = 33
            r2.setSpan(r4, r3, r0, r6)
        L_0x0845:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setMaxLines(r8)
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
            goto L_0x0d7c
        L_0x0877:
            r9 = 16
            if (r11 == r9) goto L_0x0aa3
            r9 = 17
            if (r11 != r9) goto L_0x0881
            goto L_0x0aa3
        L_0x0881:
            r9 = 18
            if (r11 != r9) goto L_0x0903
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
            r10 = r19
            r10.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r10.bottomMargin = r0
            r10.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r0.setAnimation(r2, r14, r14)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d7c
        L_0x0903:
            r4 = 12
            if (r11 != r4) goto L_0x099b
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624927(0x7f0e03df, float:1.8877048E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166073(0x7var_, float:1.7946381E38)
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
            r0 = 2131624928(0x7f0e03e0, float:1.887705E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            int r3 = r0.indexOf(r13)
            int r0 = r0.lastIndexOf(r13)
            if (r3 < 0) goto L_0x0974
            if (r0 < 0) goto L_0x0974
            if (r3 == r0) goto L_0x0974
            int r4 = r0 + 1
            r2.replace(r0, r4, r12)
            int r4 = r3 + 1
            r2.replace(r3, r4, r12)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r6 = "tg://settings/themes"
            r4.<init>(r6)
            int r0 = r0 - r5
            r6 = 33
            r2.setSpan(r4, r3, r0, r6)
        L_0x0974:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r7)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setMaxLines(r8)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r7)
            goto L_0x0d7c
        L_0x099b:
            if (r11 == r8) goto L_0x0a40
            r4 = 4
            if (r11 != r4) goto L_0x09a2
            goto L_0x0a40
        L_0x09a2:
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
            if (r0 != 0) goto L_0x09e6
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625742(0x7f0e070e, float:1.88787E38)
            java.lang.String r6 = "HistoryClearedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x0a2e
        L_0x09e6:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x0a20
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r4.getChat(r0)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r4 == 0) goto L_0x0a11
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0a11
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624704(0x7f0e0300, float:1.8876595E38)
            java.lang.String r6 = "ChannelDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x0a2e
        L_0x0a11:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131625695(0x7f0e06df, float:1.8878605E38)
            java.lang.String r6 = "GroupDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
            goto L_0x0a2e
        L_0x0a20:
            android.widget.TextView r0 = r1.infoTextView
            r4 = 2131624798(0x7f0e035e, float:1.8876786E38)
            java.lang.String r6 = "ChatDeletedUndo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setText(r4)
        L_0x0a2e:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r4 = r1.currentAction
            if (r4 != 0) goto L_0x0a3a
            r4 = 1
            goto L_0x0a3b
        L_0x0a3a:
            r4 = 0
        L_0x0a3b:
            r0.addDialogAction(r2, r4)
            goto L_0x0d7c
        L_0x0a40:
            if (r0 != r8) goto L_0x0a51
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624794(0x7f0e035a, float:1.8876778E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0a5f
        L_0x0a51:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0a5f:
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
            r0.setAnimation(r2, r14, r14)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0d7c
        L_0x0aa3:
            r10 = r19
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
            if (r2 == 0) goto L_0x0ae9
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625160(0x7f0e04c8, float:1.887752E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165395(0x7var_d3, float:1.7945006E38)
            r0.setImageResource(r2)
            goto L_0x0b91
        L_0x0ae9:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0b06
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625035(0x7f0e044b, float:1.8877267E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0b03:
            r9 = 1096810496(0x41600000, float:14.0)
            goto L_0x0b5f
        L_0x0b06:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0b39
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r9 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r11, r7)
            r3.setText(r2)
            goto L_0x0b03
        L_0x0b39:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625159(0x7f0e04c7, float:1.8877518E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r0
            java.lang.String r9 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r3, r4)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r9 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r11, r7)
            r2.setText(r3)
        L_0x0b5f:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r10.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r10.height = r0
        L_0x0b91:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627309(0x7f0e0d2d, float:1.8881879E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0be3
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
            goto L_0x0bf3
        L_0x0be3:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0bf3:
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
            goto L_0x0d7c
        L_0x0c1a:
            r2 = 8
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r25
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r7)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x0ce8
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r4.setTypeface(r9)
            android.widget.TextView r4 = r1.infoTextView
            r9 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r5, r9)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "BODY.**"
            r4.setLayerColor(r10, r9)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "Wibe Big.**"
            r4.setLayerColor(r10, r9)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r10 = "Wibe Big 3.**"
            r4.setLayerColor(r10, r9)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r9 = "Wibe Small.**"
            r4.setLayerColor(r9, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627009(0x7f0e0CLASSNAME, float:1.888127E38)
            java.lang.String r9 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r9 = 28
            r10 = 28
            r2.setAnimation(r4, r9, r10)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r7)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r7)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0cc2
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627011(0x7f0e0CLASSNAME, float:1.8881274E38)
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r9[r7] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r8)
            r9[r5] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r9)
            r2.setText(r0)
            goto L_0x0cd9
        L_0x0cc2:
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 2131627010(0x7f0e0CLASSNAME, float:1.8881272E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r8)
            r4[r7] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
        L_0x0cd9:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r6.topMargin = r0
            goto L_0x0d6b
        L_0x0ce8:
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
            r2 = 2131627008(0x7f0e0CLASSNAME, float:1.8881268E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558441(0x7f0d0029, float:1.8742198E38)
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
        L_0x0d6b:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r6.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x0d7c:
            r0 = 0
            r11 = 1096810496(0x41600000, float:14.0)
            goto L_0x1274
        L_0x0d81:
            android.widget.ImageView r10 = r1.undoImageView
            r11 = 8
            r10.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r10 = r1.leftImageView
            r10.setVisibility(r7)
            android.widget.TextView r10 = r1.infoTextView
            android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
            r10.setTypeface(r11)
            int r10 = r1.currentAction
            r11 = 76
            r13 = 1091567616(0x41100000, float:9.0)
            if (r10 != r11) goto L_0x0dc4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624591(0x7f0e028f, float:1.8876366E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558434(0x7f0d0022, float:1.8742184E38)
            r0.setAnimation(r2, r14, r14)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
        L_0x0dbf:
            r0 = 1
            r11 = 1096810496(0x41600000, float:14.0)
            goto L_0x1246
        L_0x0dc4:
            r11 = 75
            if (r10 != r11) goto L_0x0dec
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625674(0x7f0e06ca, float:1.8878563E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558417(0x7f0d0011, float:1.874215E38)
            r0.setAnimation(r2, r14, r14)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            goto L_0x0dbf
        L_0x0dec:
            r11 = 70
            if (r0 != r11) goto L_0x0e5e
            r0 = r4
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r25
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r7)
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r0 <= r2) goto L_0x0e10
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e2d
        L_0x0e10:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r0 < r2) goto L_0x0e1d
            int r0 = r0 / 3600
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e2d
        L_0x0e1d:
            if (r0 < r9) goto L_0x0e27
            int r0 = r0 / r9
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0e2d
        L_0x0e27:
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0e2d:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r4[r7] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558431(0x7f0d001f, float:1.8742178E38)
            r0.setAnimation(r2, r14, r14)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r7, r7, r7, r2)
            goto L_0x0dbf
        L_0x0e5e:
            r0 = 71
            if (r10 != r0) goto L_0x0e92
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624431(0x7f0e01ef, float:1.8876042E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r0.setAnimation(r2, r14, r14)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r7, r7, r7, r2)
            r11 = 1096810496(0x41600000, float:14.0)
            goto L_0x1245
        L_0x0e92:
            r0 = 45
            if (r10 != r0) goto L_0x0ebb
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625783(0x7f0e0737, float:1.8878784E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r0.setAnimation(r2, r14, r14)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            goto L_0x0dbf
        L_0x0ebb:
            r0 = 46
            if (r10 != r0) goto L_0x0ee4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625784(0x7f0e0738, float:1.8878786E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558426(0x7f0d001a, float:1.8742167E38)
            r0.setAnimation(r2, r14, r14)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r2)
            goto L_0x0dbf
        L_0x0ee4:
            r0 = 47
            if (r10 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625790(0x7f0e073e, float:1.8878798E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558452(0x7f0d0034, float:1.874222E38)
            r0.setAnimation(r2, r14, r14)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r7, r7, r7, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r11 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r11)
            r0 = 1
            goto L_0x1246
        L_0x0var_:
            r11 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r10 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r3 = "AudioSpeedNormal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r2, r14, r14)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x1245
        L_0x0var_:
            r0 = 50
            if (r10 != r0) goto L_0x0var_
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r3 = "AudioSpeedFast"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558406(0x7f0d0006, float:1.8742127E38)
            r0.setAnimation(r2, r14, r14)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x1245
        L_0x0var_:
            r0 = 52
            if (r10 == r0) goto L_0x11c2
            r0 = 56
            if (r10 == r0) goto L_0x11c2
            r0 = 57
            if (r10 == r0) goto L_0x11c2
            r0 = 58
            if (r10 == r0) goto L_0x11c2
            r0 = 59
            if (r10 == r0) goto L_0x11c2
            if (r10 != r9) goto L_0x0var_
            goto L_0x11c2
        L_0x0var_:
            r0 = 54
            if (r10 != r0) goto L_0x0faa
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624739(0x7f0e0323, float:1.8876666E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558471(0x7f0d0047, float:1.8742259E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x1245
        L_0x0faa:
            r0 = 55
            if (r10 != r0) goto L_0x0fd3
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624738(0x7f0e0322, float:1.8876664E38)
            java.lang.String r3 = "ChannelNotifyMembersInfoOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558470(0x7f0d0046, float:1.8742257E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
            goto L_0x1245
        L_0x0fd3:
            r0 = 41
            if (r10 != r0) goto L_0x1080
            if (r25 != 0) goto L_0x104e
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            long r9 = (long) r0
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0ffa
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625813(0x7f0e0755, float:1.8878845E38)
            java.lang.String r3 = "InvLinkToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1072
        L_0x0ffa:
            int r0 = (int) r2
            if (r0 >= 0) goto L_0x1025
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625812(0x7f0e0754, float:1.8878843E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = r0.title
            r4[r7] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x1072
        L_0x1025:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625814(0x7f0e0756, float:1.8878847E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r7] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
            goto L_0x1072
        L_0x104e:
            r0 = r25
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r9 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r9, r0)
            r4[r7] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setText(r0)
        L_0x1072:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r0.setAnimation(r2, r14, r14)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x1245
        L_0x1080:
            r0 = 53
            if (r10 != r0) goto L_0x1245
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r25 != 0) goto L_0x116b
            int r4 = r1.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.clientUserId
            long r9 = (long) r4
            int r4 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r4 != 0) goto L_0x10cd
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x10af
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625646(0x7f0e06ae, float:1.8878506E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x10c1
        L_0x10af:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625650(0x7f0e06b2, float:1.8878514E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x10c1:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558468(0x7f0d0044, float:1.8742253E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x11bc
        L_0x10cd:
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x1117
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x10fe
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625645(0x7f0e06ad, float:1.8878504E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = r2.title
            r4[r7] = r2
            java.lang.String r2 = "FwdMessageToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1160
        L_0x10fe:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625649(0x7f0e06b1, float:1.8878512E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = r2.title
            r4[r7] = r2
            java.lang.String r2 = "FwdMessagesToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1160
        L_0x1117:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x1146
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625647(0x7f0e06af, float:1.8878508E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessageToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x1160
        L_0x1146:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625651(0x7f0e06b3, float:1.8878516E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessagesToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x1160:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
            goto L_0x11bc
        L_0x116b:
            r2 = r25
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r0 = r0.intValue()
            if (r0 != r5) goto L_0x1196
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625644(0x7f0e06ac, float:1.8878502E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r9 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessageToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            goto L_0x11b2
        L_0x1196:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625648(0x7f0e06b0, float:1.887851E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r9 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            r4[r7] = r2
            java.lang.String r2 = "FwdMessagesToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x11b2:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558432(0x7f0d0020, float:1.874218E38)
            r3 = 30
            r0.setAnimation(r2, r3, r3)
        L_0x11bc:
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x1245
        L_0x11c2:
            r0 = 2131558422(0x7f0d0016, float:1.874216E38)
            if (r10 != r9) goto L_0x11d6
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626864(0x7f0e0b70, float:1.8880976E38)
            java.lang.String r4 = "PhoneCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1233
        L_0x11d6:
            r2 = 56
            if (r10 != r2) goto L_0x11e9
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627847(0x7f0e0var_, float:1.888297E38)
            java.lang.String r4 = "UsernameCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1233
        L_0x11e9:
            r2 = 57
            if (r10 != r2) goto L_0x11fc
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625729(0x7f0e0701, float:1.8878674E38)
            java.lang.String r4 = "HashtagCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1233
        L_0x11fc:
            r2 = 52
            if (r10 != r2) goto L_0x120f
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r4 = "MessageCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1233
        L_0x120f:
            r2 = 59
            if (r10 != r2) goto L_0x1225
            r0 = 2131558513(0x7f0d0071, float:1.8742344E38)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625929(0x7f0e07c9, float:1.887908E38)
            java.lang.String r4 = "LinkCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x1233
        L_0x1225:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131627632(0x7f0e0e70, float:1.8882534E38)
            java.lang.String r4 = "TextCopied"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x1233:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 30
            r2.setAnimation(r0, r3, r3)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r2)
        L_0x1245:
            r0 = 0
        L_0x1246:
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
        L_0x1274:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.widget.TextView r3 = r1.infoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            android.widget.TextView r3 = r1.subinfoTextView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x12a1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = ". "
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            java.lang.String r12 = r3.toString()
        L_0x12a1:
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r2)
            boolean r2 = r20.isMultilineSubInfo()
            if (r2 == 0) goto L_0x12f6
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x12c1
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x12c1:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r6 = 0
            r21 = r20
            r22 = r2
            r23 = r0
            r24 = r3
            r25 = r4
            r26 = r6
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r0 = r1.subinfoTextView
            int r0 = r0.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.undoViewHeight = r0
            goto L_0x139a
        L_0x12f6:
            boolean r2 = r20.hasSubInfo()
            if (r2 == 0) goto L_0x1306
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x139a
        L_0x1306:
            android.view.ViewParent r2 = r20.getParent()
            boolean r2 = r2 instanceof android.view.ViewGroup
            if (r2 == 0) goto L_0x139a
            android.view.ViewParent r2 = r20.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            int r3 = r2.getMeasuredWidth()
            int r4 = r2.getPaddingLeft()
            int r3 = r3 - r4
            int r2 = r2.getPaddingRight()
            int r3 = r3 - r2
            if (r3 > 0) goto L_0x1328
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
        L_0x1328:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.widget.TextView r2 = r1.infoTextView
            r4 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            r4 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r7)
            r9 = 0
            r21 = r20
            r22 = r2
            r23 = r3
            r24 = r4
            r25 = r6
            r26 = r9
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r2 = r1.infoTextView
            int r2 = r2.getMeasuredHeight()
            int r3 = r1.currentAction
            r4 = 16
            if (r3 == r4) goto L_0x1364
            r4 = 17
            if (r3 == r4) goto L_0x1364
            r4 = 18
            if (r3 != r4) goto L_0x1361
            goto L_0x1364
        L_0x1361:
            r14 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1366
        L_0x1364:
            r14 = 1096810496(0x41600000, float:14.0)
        L_0x1366:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r2 = r2 + r3
            r1.undoViewHeight = r2
            int r3 = r1.currentAction
            r4 = 18
            if (r3 != r4) goto L_0x1380
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x139a
        L_0x1380:
            r4 = 25
            if (r3 != r4) goto L_0x1391
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.undoViewHeight = r0
            goto L_0x139a
        L_0x1391:
            if (r0 == 0) goto L_0x139a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 - r0
            r1.undoViewHeight = r2
        L_0x139a:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x1401
            r1.setVisibility(r7)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x13aa
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x13ac
        L_0x13aa:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x13ac:
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
            float[] r4 = new float[r8]
            boolean r6 = r1.fromTop
            if (r6 == 0) goto L_0x13cb
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x13cd
        L_0x13cb:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x13cd:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r1.undoViewHeight
            int r8 = r8 + r9
            float r8 = (float) r8
            float r6 = r6 * r8
            r4[r7] = r6
            boolean r6 = r1.fromTop
            if (r6 == 0) goto L_0x13e0
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x13e2
        L_0x13e0:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x13e2:
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
        L_0x1401:
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
