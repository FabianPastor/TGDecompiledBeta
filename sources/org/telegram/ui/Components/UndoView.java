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
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.PaymentFormActivity;

public class UndoView extends FrameLayout {
    public static int ACTION_RINGTONE_ADDED = 83;
    private float additionalTranslationY;
    private BackupImageView avatarImageView;
    Drawable backgroundDrawable;
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private ArrayList<Long> currentDialogIds;
    private Object currentInfoObject;
    float enterOffset;
    private int enterOffsetMargin;
    private boolean fromTop;
    private int hideAnimationType;
    private CharSequence infoText;
    private TextView infoTextView;
    private boolean isShown;
    private long lastUpdateTime;
    private RLottieImageView leftImageView;
    private BaseFragment parentFragment;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showWithAction$3(View view, MotionEvent motionEvent) {
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
            CharacterStyle[] characterStyleArr;
            try {
                if (motionEvent.getAction() == 0 && ((characterStyleArr = (CharacterStyle[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), CharacterStyle.class)) == null || characterStyleArr.length == 0)) {
                    return false;
                }
                if (motionEvent.getAction() != 1) {
                    return super.onTouchEvent(textView, spannable, motionEvent);
                }
                CharacterStyle[] characterStyleArr2 = (CharacterStyle[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), CharacterStyle.class);
                if (characterStyleArr2 != null && characterStyleArr2.length > 0) {
                    UndoView.this.didPressUrl(characterStyleArr2[0]);
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
        this(context, (BaseFragment) null, false, (Theme.ResourcesProvider) null);
    }

    public UndoView(Context context, BaseFragment baseFragment) {
        this(context, baseFragment, false, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UndoView(Context context, BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.currentAccount = UserConfig.selectedAccount;
        this.hideAnimationType = 1;
        this.enterOffsetMargin = AndroidUtilities.dp(8.0f);
        this.timeReplaceProgress = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = baseFragment;
        this.fromTop = z;
        TextView textView = new TextView(context2);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(getThemedColor("undo_infoColor"));
        this.infoTextView.setLinkTextColor(getThemedColor("undo_cancelColor"));
        this.infoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(getThemedColor("undo_infoColor"));
        this.subinfoTextView.setLinkTextColor(getThemedColor("undo_cancelColor"));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.leftImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.leftImageView.setLayerColor("info1.**", getThemedColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("info2.**", getThemedColor("undo_background") | -16777216);
        this.leftImageView.setLayerColor("luCLASSNAME.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luCLASSNAME.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc9.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc8.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc7.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc6.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc5.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc4.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc3.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc2.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("luc1.**", getThemedColor("undo_infoColor"));
        this.leftImageView.setLayerColor("Oval.**", getThemedColor("undo_infoColor"));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(30, 30.0f, 19, 15.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        this.undoButton.setBackground(Theme.createRadSelectorDrawable(getThemedColor("undo_cancelColor") & NUM, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        addView(this.undoButton, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 11.0f, 0.0f));
        this.undoButton.setOnClickListener(new UndoView$$ExternalSyntheticLambda0(this));
        ImageView imageView = new ImageView(context2);
        this.undoImageView = imageView;
        imageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19, 4, 4, 0, 4));
        TextView textView3 = new TextView(context2);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(getThemedColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 4, 8, 4));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(getThemedColor("undo_infoColor"));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setColor(getThemedColor("undo_infoColor"));
        setWillNotDraw(false);
        this.backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("undo_background"));
        setOnTouchListener(UndoView$$ExternalSyntheticLambda3.INSTANCE);
        setVisibility(4);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    public void setColors(int i, int i2) {
        Theme.setDrawableColor(this.backgroundDrawable, i);
        this.infoTextView.setTextColor(i2);
        this.subinfoTextView.setTextColor(i2);
        int i3 = i | -16777216;
        this.leftImageView.setLayerColor("info1.**", i3);
        this.leftImageView.setLayerColor("info2.**", i3);
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77 || i == 44 || i == 78 || i == 79 || i == 100 || i == 101 || i == ACTION_RINGTONE_ADDED;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || i == 74 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty()) || this.currentAction == ACTION_RINGTONE_ADDED;
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24 || i == 74 || i == ACTION_RINGTONE_ADDED;
    }

    public void setAdditionalTranslationY(float f) {
        if (this.additionalTranslationY != f) {
            this.additionalTranslationY = f;
            updatePosition();
        }
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
            if (i2 == 0 || i2 == 1 || i2 == 26 || i2 == 27) {
                for (int i3 = 0; i3 < this.currentDialogIds.size(); i3++) {
                    long longValue = this.currentDialogIds.get(i3).longValue();
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    int i4 = this.currentAction;
                    instance.removeDialogAction(longValue, i4 == 0 || i4 == 26, z);
                    onRemoveDialogAction(longValue, this.currentAction);
                }
            }
            float f = -1.0f;
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    Animator[] animatorArr = new Animator[1];
                    float[] fArr = new float[1];
                    if (!this.fromTop) {
                        f = 1.0f;
                    }
                    fArr[0] = f * ((float) (this.enterOffsetMargin + this.undoViewHeight));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "enterOffset", fArr);
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
            setEnterOffset(f * ((float) (this.enterOffsetMargin + this.undoViewHeight)));
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

    public void showWithAction(long j, int i, Object obj, Object obj2, Runnable runnable, Runnable runnable2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Long.valueOf(j));
        showWithAction((ArrayList<Long>) arrayList, i, obj, obj2, runnable, runnable2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:563:0x167c  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x169c  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x16c3  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x1708  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x17b2  */
    /* JADX WARNING: Removed duplicated region for block: B:623:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r20, int r21, java.lang.Object r22, java.lang.Object r23, java.lang.Runnable r24, java.lang.Runnable r25) {
        /*
            r19 = this;
            r1 = r19
            r0 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            boolean r7 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r7 != 0) goto L_0x001a
            int r7 = r1.currentAction
            r8 = 52
            if (r7 == r8) goto L_0x180c
        L_0x001a:
            int r7 = r1.currentAction
            r8 = 56
            if (r7 == r8) goto L_0x180c
            r8 = 57
            if (r7 == r8) goto L_0x180c
            r8 = 58
            if (r7 == r8) goto L_0x180c
            r8 = 59
            if (r7 == r8) goto L_0x180c
            r8 = 60
            if (r7 == r8) goto L_0x180c
            r8 = 80
            if (r7 == r8) goto L_0x180c
            r8 = 33
            if (r7 != r8) goto L_0x003a
            goto L_0x180c
        L_0x003a:
            java.lang.Runnable r7 = r1.currentActionRunnable
            if (r7 == 0) goto L_0x0041
            r7.run()
        L_0x0041:
            r7 = 1
            r1.isShown = r7
            r1.currentActionRunnable = r5
            r1.currentCancelRunnable = r6
            r1.currentDialogIds = r0
            r9 = 0
            java.lang.Object r10 = r0.get(r9)
            java.lang.Long r10 = (java.lang.Long) r10
            long r10 = r10.longValue()
            r1.currentAction = r2
            r12 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r12
            r1.currentInfoObject = r3
            long r12 = android.os.SystemClock.elapsedRealtime()
            r1.lastUpdateTime = r12
            android.widget.TextView r12 = r1.undoTextView
            r13 = 2131628499(0x7f0e11d3, float:1.8884292E38)
            java.lang.String r14 = "Undo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            java.lang.String r13 = r13.toUpperCase()
            r12.setText(r13)
            android.widget.ImageView r12 = r1.undoImageView
            r12.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r12 = r1.leftImageView
            r12.setPadding(r9, r9, r9, r9)
            android.widget.TextView r12 = r1.infoTextView
            r13 = 1097859072(0x41700000, float:15.0)
            r12.setTextSize(r7, r13)
            org.telegram.ui.Components.BackupImageView r12 = r1.avatarImageView
            r14 = 8
            r12.setVisibility(r14)
            android.widget.TextView r12 = r1.infoTextView
            r15 = 51
            r12.setGravity(r15)
            android.widget.TextView r12 = r1.infoTextView
            android.view.ViewGroup$LayoutParams r12 = r12.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r12 = (android.widget.FrameLayout.LayoutParams) r12
            r15 = -2
            r12.height = r15
            r15 = 1095761920(0x41500000, float:13.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r12.topMargin = r15
            r12.bottomMargin = r9
            org.telegram.ui.Components.RLottieImageView r15 = r1.leftImageView
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r15.setScaleType(r13)
            org.telegram.ui.Components.RLottieImageView r13 = r1.leftImageView
            android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            r15 = 19
            r13.gravity = r15
            r13.bottomMargin = r9
            r13.topMargin = r9
            r15 = 1077936128(0x40400000, float:3.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r13.leftMargin = r15
            r15 = 1113063424(0x42580000, float:54.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r13.width = r15
            r15 = -2
            r13.height = r15
            android.widget.TextView r15 = r1.infoTextView
            r15.setMinHeight(r9)
            r15 = 0
            if (r5 != 0) goto L_0x00dd
            if (r6 == 0) goto L_0x00e1
        L_0x00dd:
            int r6 = ACTION_RINGTONE_ADDED
            if (r2 != r6) goto L_0x00ed
        L_0x00e1:
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r6.<init>(r1)
            r1.setOnClickListener(r6)
            r1.setOnTouchListener(r15)
            goto L_0x00f5
        L_0x00ed:
            r1.setOnClickListener(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r6 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r1.setOnTouchListener(r6)
        L_0x00f5:
            android.widget.TextView r6 = r1.infoTextView
            r6.setMovementMethod(r15)
            boolean r6 = r19.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r14 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r7 = 36
            if (r6 == 0) goto L_0x08d5
            int r0 = ACTION_RINGTONE_ADDED
            if (r2 != r0) goto L_0x0138
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            r0 = 2131628170(0x7f0e108a, float:1.8883625E38)
            java.lang.String r2 = "SoundAdded"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131628171(0x7f0e108b, float:1.8883627E38)
            java.lang.String r3 = "SoundAddedSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r5)
            r1.currentActionRunnable = r15
            r3 = 2131558517(0x7f0d0075, float:1.8742352E38)
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            r15 = r2
            r2 = 2131558517(0x7f0d0075, float:1.8742352E38)
            goto L_0x082e
        L_0x0138:
            r0 = 74
            if (r2 != r0) goto L_0x015e
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            r0 = 2131627742(0x7f0e0ede, float:1.8882757E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627751(0x7f0e0ee7, float:1.8882775E38)
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            r2 = 2131558457(0x7f0d0039, float:1.874223E38)
            r3 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x015e:
            r0 = 34
            if (r2 != r0) goto L_0x01be
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x0185
            r2 = 2131628737(0x7f0e12c1, float:1.8884775E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r5
            java.lang.String r5 = "VoipChannelInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x019b
        L_0x0185:
            r3 = 1
            r2 = 2131628826(0x7f0e131a, float:1.8884956E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r3
            java.lang.String r3 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x019b:
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setTextSize(r4)
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r0, r3)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            r0.setVisibility(r9)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            r0 = r2
        L_0x01bb:
            r2 = 0
            goto L_0x082e
        L_0x01be:
            r0 = 44
            if (r2 != r0) goto L_0x0255
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x0200
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01e9
            r0 = 2131628762(0x7f0e12da, float:1.8884826E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r9] = r2
            java.lang.String r2 = "VoipChannelUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0232
        L_0x01e9:
            r4 = 1
            r0 = 2131628777(0x7f0e12e9, float:1.8884856E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r9] = r2
            java.lang.String r2 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0232
        L_0x0200:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x021e
            r0 = 2131628731(0x7f0e12bb, float:1.8884763E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r9] = r2
            java.lang.String r2 = "VoipChannelChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0232
        L_0x021e:
            r4 = 1
            r0 = 2131628767(0x7f0e12df, float:1.8884836E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r9] = r2
            java.lang.String r2 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0232:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r2.<init>()
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setTextSize(r4)
            org.telegram.tgnet.TLObject r3 = (org.telegram.tgnet.TLObject) r3
            r2.setInfo((org.telegram.tgnet.TLObject) r3)
            org.telegram.ui.Components.BackupImageView r4 = r1.avatarImageView
            r4.setForUserOrChat(r3, r2)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            goto L_0x01bb
        L_0x0255:
            r0 = 37
            if (r2 != r0) goto L_0x02c5
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x027f
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x028c
        L_0x027f:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x028c:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x02a8
            r2 = 2131628761(0x7f0e12d9, float:1.8884824E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipChannelUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02ba
        L_0x02a8:
            r3 = 1
            r2 = 2131628890(0x7f0e135a, float:1.8885085E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02ba:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            r5 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r5
            goto L_0x01bb
        L_0x02c5:
            r0 = 33
            r5 = 3000(0xbb8, double:1.482E-320)
            if (r2 != r0) goto L_0x02db
            r0 = 2131628808(0x7f0e1308, float:1.888492E38)
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558570(0x7f0d00aa, float:1.874246E38)
            r1.timeLeft = r5
            goto L_0x082e
        L_0x02db:
            r0 = 77
            if (r2 != r0) goto L_0x0306
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2 = 2131558484(0x7f0d0054, float:1.8742285E38)
            r5 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r5
            org.telegram.ui.ActionBar.BaseFragment r3 = r1.parentFragment
            if (r3 == 0) goto L_0x082e
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$Message
            if (r3 == 0) goto L_0x082e
            r3 = r4
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            r1.setOnTouchListener(r15)
            android.widget.TextView r4 = r1.infoTextView
            r4.setMovementMethod(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r4.<init>(r1, r3)
            r1.setOnClickListener(r4)
            goto L_0x082e
        L_0x0306:
            r0 = 30
            if (r2 != r0) goto L_0x0336
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0316
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x031b
        L_0x0316:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x031b:
            r2 = 2131628888(0x7f0e1358, float:1.8885081E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558571(0x7f0d00ab, float:1.8742462E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x0336:
            r0 = 35
            if (r2 != r0) goto L_0x036c
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0346
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0351
        L_0x0346:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0350
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0351
        L_0x0350:
            r0 = r14
        L_0x0351:
            r2 = 2131628889(0x7f0e1359, float:1.8885083E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558571(0x7f0d00ab, float:1.8742462E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x036c:
            r0 = 31
            if (r2 != r0) goto L_0x039c
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x037c
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0381
        L_0x037c:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0381:
            r2 = 2131628886(0x7f0e1356, float:1.8885077E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558577(0x7f0d00b1, float:1.8742474E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x039c:
            r0 = 38
            if (r2 != r0) goto L_0x03d2
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x03bc
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            r2 = 2131628898(0x7f0e1362, float:1.8885102E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = r0.title
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03c9
        L_0x03bc:
            r0 = 2131628897(0x7f0e1361, float:1.88851E38)
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03c9:
            r2 = 2131558563(0x7f0d00a3, float:1.8742445E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x03d2:
            r0 = 42
            if (r2 != r0) goto L_0x0403
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03ed
            r0 = 2131628752(0x7f0e12d0, float:1.8884806E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03fa
        L_0x03ed:
            r0 = 2131628866(0x7f0e1342, float:1.8885037E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03fa:
            r2 = 2131558461(0x7f0d003d, float:1.8742238E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x0403:
            r0 = 43
            if (r2 != r0) goto L_0x0434
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x041e
            r0 = 2131628753(0x7f0e12d1, float:1.8884808E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x042b
        L_0x041e:
            r0 = 2131628867(0x7f0e1343, float:1.8885039E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x042b:
            r2 = 2131558467(0x7f0d0043, float:1.874225E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x0434:
            int r0 = r1.currentAction
            r5 = 39
            if (r0 == r5) goto L_0x0810
            r5 = 100
            if (r0 != r5) goto L_0x0440
            goto L_0x0810
        L_0x0440:
            r5 = 40
            if (r0 == r5) goto L_0x079d
            r5 = 101(0x65, float:1.42E-43)
            if (r0 != r5) goto L_0x044a
            goto L_0x079d
        L_0x044a:
            if (r2 != r7) goto L_0x0478
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0458
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x045d
        L_0x0458:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x045d:
            r2 = 2131628887(0x7f0e1357, float:1.888508E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558577(0x7f0d00b1, float:1.8742474E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x0478:
            r5 = 32
            if (r2 != r5) goto L_0x04a8
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0488
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x048d
        L_0x0488:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x048d:
            r2 = 2131628857(0x7f0e1339, float:1.8885019E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558569(0x7f0d00a9, float:1.8742458E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x082e
        L_0x04a8:
            r5 = 9
            if (r2 == r5) goto L_0x0764
            r5 = 10
            if (r2 != r5) goto L_0x04b2
            goto L_0x0764
        L_0x04b2:
            r5 = 8
            if (r2 != r5) goto L_0x04cd
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = 2131626915(0x7f0e0ba3, float:1.888108E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            goto L_0x0798
        L_0x04cd:
            r5 = 22
            if (r2 != r5) goto L_0x0538
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r0 == 0) goto L_0x04ef
            if (r3 != 0) goto L_0x04e4
            r0 = 2131626364(0x7f0e097c, float:1.8879962E38)
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x04e4:
            r0 = 2131626365(0x7f0e097d, float:1.8879964E38)
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x04ef:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r4 = -r10
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x0520
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0520
            if (r3 != 0) goto L_0x0515
            r0 = 2131626360(0x7f0e0978, float:1.8879954E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x0515:
            r0 = 2131626361(0x7f0e0979, float:1.8879956E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x0520:
            if (r3 != 0) goto L_0x052d
            r0 = 2131626362(0x7f0e097a, float:1.8879958E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x052d:
            r0 = 2131626363(0x7f0e097b, float:1.887996E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x0538:
            r5 = 23
            if (r2 != r5) goto L_0x0547
            r0 = 2131624963(0x7f0e0403, float:1.887712E38)
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0798
        L_0x0547:
            r5 = 6
            if (r2 != r5) goto L_0x0563
            r0 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131624337(0x7f0e0191, float:1.887585E38)
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r7 = 48
            goto L_0x082e
        L_0x0563:
            r5 = 13
            if (r0 != r5) goto L_0x0580
            r0 = 2131627630(0x7f0e0e6e, float:1.888253E38)
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627631(0x7f0e0e6f, float:1.8882532E38)
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558579(0x7f0d00b3, float:1.8742478E38)
        L_0x057c:
            r7 = 44
            goto L_0x082e
        L_0x0580:
            r5 = 14
            if (r0 != r5) goto L_0x059a
            r0 = 2131627632(0x7f0e0e70, float:1.8882534E38)
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131627633(0x7f0e0e71, float:1.8882536E38)
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 2131558581(0x7f0d00b5, float:1.8742482E38)
            goto L_0x057c
        L_0x059a:
            r0 = 7
            if (r2 != r0) goto L_0x05c3
            r0 = 2131624344(0x7f0e0198, float:1.8875865E38)
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05be
            r2 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x05bd:
            r15 = r2
        L_0x05be:
            r2 = 2131558423(0x7f0d0017, float:1.8742161E38)
            goto L_0x082e
        L_0x05c3:
            r0 = 20
            if (r2 == r0) goto L_0x064f
            r0 = 21
            if (r2 != r0) goto L_0x05cd
            goto L_0x064f
        L_0x05cd:
            r0 = 19
            if (r2 != r0) goto L_0x05d4
            java.lang.CharSequence r0 = r1.infoText
            goto L_0x05be
        L_0x05d4:
            r0 = 82
            if (r2 != r0) goto L_0x05f0
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            boolean r0 = r0.isVideo
            if (r0 == 0) goto L_0x05e5
            r0 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.String r2 = "AttachMediaVideoDeselected"
            goto L_0x05ea
        L_0x05e5:
            r0 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r2 = "AttachMediaPhotoDeselected"
        L_0x05ea:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x01bb
        L_0x05f0:
            r0 = 78
            if (r2 == r0) goto L_0x0627
            r0 = 79
            if (r2 != r0) goto L_0x05f9
            goto L_0x0627
        L_0x05f9:
            r0 = 3
            if (r2 != r0) goto L_0x0606
            r0 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x060f
        L_0x0606:
            r0 = 2131624976(0x7f0e0410, float:1.8877147E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x060f:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05be
            r2 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x05bd
        L_0x0627:
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = 78
            if (r2 != r3) goto L_0x0639
            java.lang.String r2 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x063f
        L_0x0639:
            java.lang.String r2 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x063f:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x064a
            r2 = 2131558462(0x7f0d003e, float:1.874224E38)
            goto L_0x082e
        L_0x064a:
            r2 = 2131558468(0x7f0d0044, float:1.8742253E38)
            goto L_0x082e
        L_0x064f:
            r0 = r4
            org.telegram.messenger.MessagesController$DialogFilter r0 = (org.telegram.messenger.MessagesController.DialogFilter) r0
            r4 = 0
            int r6 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x070b
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)
            if (r3 == 0) goto L_0x0672
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r10)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            long r10 = r3.user_id
        L_0x0672:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r3 == 0) goto L_0x06c4
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            r4 = 20
            if (r2 != r4) goto L_0x06a7
            r4 = 2131625797(0x7f0e0745, float:1.8878812E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r8 = 1
            r6[r8] = r0
            java.lang.String r0 = "FilterUserAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0756
        L_0x06a7:
            r5 = 2
            r8 = 1
            r4 = 2131625798(0x7f0e0746, float:1.8878814E38)
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r6[r8] = r0
            java.lang.String r0 = "FilterUserRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0756
        L_0x06c4:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r10
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            r4 = 20
            if (r2 != r4) goto L_0x06f1
            r4 = 2131625736(0x7f0e0708, float:1.8878688E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r8 = 1
            r6[r8] = r0
            java.lang.String r0 = "FilterChatAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0756
        L_0x06f1:
            r5 = 2
            r8 = 1
            r4 = 2131625737(0x7f0e0709, float:1.887869E38)
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r6[r8] = r0
            java.lang.String r0 = "FilterChatRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0756
        L_0x070b:
            r4 = 20
            if (r2 != r4) goto L_0x0733
            r4 = 2131625740(0x7f0e070c, float:1.8878696E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r3 = 1
            r6[r3] = r0
            java.lang.String r0 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0756
        L_0x0733:
            r4 = 2131625741(0x7f0e070d, float:1.8878699E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r3)
            r6[r9] = r3
            java.lang.String r0 = r0.name
            r3 = 1
            r6[r3] = r0
            java.lang.String r0 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0756:
            r3 = 20
            if (r2 != r3) goto L_0x075f
            r2 = 2131558449(0x7f0d0031, float:1.8742214E38)
            goto L_0x082e
        L_0x075f:
            r2 = 2131558450(0x7f0d0032, float:1.8742216E38)
            goto L_0x082e
        L_0x0764:
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r3 = 9
            if (r2 != r3) goto L_0x0782
            r2 = 2131625468(0x7f0e05fc, float:1.8878145E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0798
        L_0x0782:
            r3 = 1
            r2 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0798:
            r2 = 2131558429(0x7f0d001d, float:1.8742174E38)
            goto L_0x082e
        L_0x079d:
            r2 = 40
            if (r0 != r2) goto L_0x07a7
            r0 = 2131628798(0x7f0e12fe, float:1.8884899E38)
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            goto L_0x07ac
        L_0x07a7:
            r0 = 2131628892(0x7f0e135c, float:1.888509E38)
            java.lang.String r2 = "VoipGroupVideoRecordSaved"
        L_0x07ac:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131558573(0x7f0d00ad, float:1.8742466E38)
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
            java.lang.String r5 = "**"
            int r0 = r0.lastIndexOf(r5)
            if (r4 < 0) goto L_0x080e
            if (r0 < 0) goto L_0x080e
            if (r4 == r0) goto L_0x080e
            int r5 = r0 + 2
            r3.replace(r0, r5, r14)
            int r5 = r4 + 2
            r3.replace(r4, r5, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x080a }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x080a }
            r6.<init>()     // Catch:{ Exception -> 0x080a }
            java.lang.String r8 = "tg://openmessage?user_id="
            r6.append(r8)     // Catch:{ Exception -> 0x080a }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x080a }
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ Exception -> 0x080a }
            long r10 = r8.getClientUserId()     // Catch:{ Exception -> 0x080a }
            r6.append(r10)     // Catch:{ Exception -> 0x080a }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x080a }
            r5.<init>(r6)     // Catch:{ Exception -> 0x080a }
            r6 = 2
            int r0 = r0 - r6
            r6 = 33
            r3.setSpan(r5, r4, r0, r6)     // Catch:{ Exception -> 0x080a }
            goto L_0x080e
        L_0x080a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x080e:
            r0 = r3
            goto L_0x082e
        L_0x0810:
            r2 = 39
            if (r0 != r2) goto L_0x081a
            r0 = 2131628799(0x7f0e12ff, float:1.88849E38)
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            goto L_0x081f
        L_0x081a:
            r0 = 2131628893(0x7f0e135d, float:1.8885092E38)
            java.lang.String r2 = "VoipGroupVideoRecordStarted"
        L_0x081f:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = 2131558574(0x7f0d00ae, float:1.8742468E38)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
        L_0x082e:
            android.widget.TextView r3 = r1.infoTextView
            r3.setText(r0)
            if (r2 == 0) goto L_0x085b
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r0.setPlayInDirectionOfCustomEndFrame(r9)
            int r2 = r0.getFramesCount()
            r0.setCustomEndFrame(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x0862
        L_0x085b:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0862:
            if (r15 == 0) goto L_0x08a4
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r0.rightMargin = r2
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r15)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            r2 = 8
            goto L_0x08ce
        L_0x08a4:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
        L_0x08ce:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x109f
        L_0x08d5:
            int r5 = r1.currentAction
            r6 = 45
            if (r5 == r6) goto L_0x10a4
            r6 = 46
            if (r5 == r6) goto L_0x10a4
            r6 = 47
            if (r5 == r6) goto L_0x10a4
            r6 = 51
            if (r5 == r6) goto L_0x10a4
            r6 = 50
            if (r5 == r6) goto L_0x10a4
            r6 = 52
            if (r5 == r6) goto L_0x10a4
            r6 = 53
            if (r5 == r6) goto L_0x10a4
            r6 = 54
            if (r5 == r6) goto L_0x10a4
            r6 = 55
            if (r5 == r6) goto L_0x10a4
            r6 = 56
            if (r5 == r6) goto L_0x10a4
            r6 = 57
            if (r5 == r6) goto L_0x10a4
            r6 = 58
            if (r5 == r6) goto L_0x10a4
            r6 = 59
            if (r5 == r6) goto L_0x10a4
            r6 = 60
            if (r5 == r6) goto L_0x10a4
            r6 = 71
            if (r5 == r6) goto L_0x10a4
            r6 = 70
            if (r5 == r6) goto L_0x10a4
            r6 = 75
            if (r5 == r6) goto L_0x10a4
            r6 = 76
            if (r5 == r6) goto L_0x10a4
            r6 = 41
            if (r5 == r6) goto L_0x10a4
            r6 = 78
            if (r5 == r6) goto L_0x10a4
            r6 = 79
            if (r5 == r6) goto L_0x10a4
            r6 = 61
            if (r5 == r6) goto L_0x10a4
            r6 = 80
            if (r5 != r6) goto L_0x0935
            goto L_0x10a4
        L_0x0935:
            r6 = 24
            if (r5 == r6) goto L_0x0var_
            r6 = 25
            if (r5 != r6) goto L_0x093f
            goto L_0x0var_
        L_0x093f:
            r4 = 11
            if (r5 != r4) goto L_0x09b0
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624473(0x7f0e0219, float:1.8876127E38)
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r3 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r2.setAnimation(r3, r7, r7)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            java.lang.String r0 = r0.app_name
            r2.setText(r0)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r1.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = r1.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x109f
        L_0x09b0:
            r4 = 15
            if (r5 != r4) goto L_0x0a84
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131626932(0x7f0e0bb4, float:1.8881114E38)
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625733(0x7f0e0705, float:1.8878682E38)
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558443(0x7f0d002b, float:1.8742202E38)
            r0.setAnimation(r2, r7, r7)
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
            r12.leftMargin = r2
            r12.rightMargin = r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r2
            android.widget.TextView r2 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.rightMargin = r0
            r0 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r3 = r0.indexOf(r3)
            r4 = 42
            int r0 = r0.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0a51
            if (r0 < 0) goto L_0x0a51
            if (r3 == r0) goto L_0x0a51
            int r4 = r0 + 1
            r2.replace(r0, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r4.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)
        L_0x0a51:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 2
            r0.setMaxLines(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            android.widget.ImageView r0 = r1.undoImageView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x109f
        L_0x0a84:
            r4 = 16
            if (r5 == r4) goto L_0x0dc1
            r4 = 17
            if (r5 != r4) goto L_0x0a8e
            goto L_0x0dc1
        L_0x0a8e:
            r4 = 18
            if (r5 != r4) goto L_0x0b19
            r0 = r3
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
            r4 = 1
            r2.setTextSize(r4, r3)
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
            r12.leftMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.bottomMargin = r0
            r0 = -1
            r12.height = r0
            r0 = 51
            r13.gravity = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r13.bottomMargin = r0
            r13.topMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            android.widget.TextView r0 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r2.<init>()
            r0.setMovementMethod(r2)
            goto L_0x109f
        L_0x0b19:
            r4 = 12
            if (r5 != r4) goto L_0x0bb7
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625094(0x7f0e0486, float:1.8877386E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131166199(0x7var_f7, float:1.7946637E38)
            r0.setImageResource(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0 = 2131625095(0x7f0e0487, float:1.8877388E38)
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r3 = r0.indexOf(r3)
            r4 = 42
            int r0 = r0.lastIndexOf(r4)
            if (r3 < 0) goto L_0x0b8f
            if (r0 < 0) goto L_0x0b8f
            if (r3 == r0) goto L_0x0b8f
            int r4 = r0 + 1
            r2.replace(r0, r4, r14)
            int r4 = r3 + 1
            r2.replace(r3, r4, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r4.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r4, r3, r0, r5)
        L_0x0b8f:
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            android.widget.TextView r0 = r1.subinfoTextView
            r4 = 2
            r0.setMaxLines(r4)
            android.widget.TextView r0 = r1.undoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            goto L_0x109f
        L_0x0bb7:
            r4 = 2
            if (r5 == r4) goto L_0x0d5c
            r4 = 4
            if (r5 != r4) goto L_0x0bbf
            goto L_0x0d5c
        L_0x0bbf:
            r4 = 82
            if (r2 != r4) goto L_0x0CLASSNAME
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            android.widget.TextView r2 = r1.infoTextView
            boolean r3 = r0.isVideo
            if (r3 == 0) goto L_0x0bd8
            r3 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.String r4 = "AttachMediaVideoDeselected"
            goto L_0x0bdd
        L_0x0bd8:
            r3 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r4 = "AttachMediaPhotoDeselected"
        L_0x0bdd:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.TextView r2 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r2.setTypeface(r3)
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setRoundRadius(r3)
            java.lang.String r2 = r0.thumbPath
            if (r2 == 0) goto L_0x0c1c
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImage(r2, r15, r3)
            goto L_0x109f
        L_0x0c1c:
            java.lang.String r2 = r0.path
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            int r3 = r0.orientation
            r4 = 1
            r2.setOrientation(r3, r4)
            boolean r2 = r0.isVideo
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "vthumb://"
            r3.append(r4)
            int r4 = r0.imageId
            r3.append(r4)
            java.lang.String r4 = ":"
            r3.append(r4)
            java.lang.String r0 = r0.path
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r2.setImage(r0, r15, r3)
            goto L_0x109f
        L_0x0CLASSNAME:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "thumb://"
            r3.append(r4)
            int r4 = r0.imageId
            r3.append(r4)
            java.lang.String r4 = ":"
            r3.append(r4)
            java.lang.String r0 = r0.path
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r2.setImage(r0, r15, r3)
            goto L_0x109f
        L_0x0CLASSNAME:
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImageDrawable(r2)
            goto L_0x109f
        L_0x0CLASSNAME:
            r2 = 1110704128(0x42340000, float:45.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.leftMargin = r2
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.topMargin = r2
            r12.rightMargin = r9
            android.widget.TextView r2 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r2.setTypeface(r3)
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r3)
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x0d1f
            if (r2 == 0) goto L_0x0d1f
            r3 = 26
            if (r2 != r3) goto L_0x0cc0
            goto L_0x0d1f
        L_0x0cc0:
            r3 = 27
            if (r2 != r3) goto L_0x0cd3
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624977(0x7f0e0411, float:1.8877149E38)
            java.lang.String r4 = "ChatsDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d2d
        L_0x0cd3:
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r2 == 0) goto L_0x0d10
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x0d01
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0d01
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624829(0x7f0e037d, float:1.8876849E38)
            java.lang.String r4 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d2d
        L_0x0d01:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625985(0x7f0e0801, float:1.8879193E38)
            java.lang.String r4 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d2d
        L_0x0d10:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624926(0x7f0e03de, float:1.8877045E38)
            java.lang.String r4 = "ChatDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0d2d
        L_0x0d1f:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.String r4 = "HistoryClearedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x0d2d:
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x109f
            r2 = 0
        L_0x0d34:
            int r3 = r20.size()
            if (r2 >= r3) goto L_0x109f
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Object r4 = r0.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            long r4 = r4.longValue()
            int r6 = r1.currentAction
            if (r6 == 0) goto L_0x0d55
            r7 = 26
            if (r6 != r7) goto L_0x0d53
            goto L_0x0d55
        L_0x0d53:
            r6 = 0
            goto L_0x0d56
        L_0x0d55:
            r6 = 1
        L_0x0d56:
            r3.addDialogAction(r4, r6)
            int r2 = r2 + 1
            goto L_0x0d34
        L_0x0d5c:
            r3 = 2
            if (r2 != r3) goto L_0x0d6e
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0d7c
        L_0x0d6e:
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624976(0x7f0e0410, float:1.8877147E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0d7c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.topMargin = r0
            r12.rightMargin = r9
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x109f
        L_0x0dc1:
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r0.setTextSize(r4, r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 16
            r0.setGravity(r2)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1106247680(0x41var_, float:30.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setMinHeight(r2)
            r0 = r3
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "🎲"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0e06
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625373(0x7f0e059d, float:1.8877952E38)
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131165420(0x7var_ec, float:1.7945057E38)
            r0.setImageResource(r2)
            goto L_0x0eaf
        L_0x0e06:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0e23
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625213(0x7f0e04fd, float:1.8877628E38)
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0e20:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0e7d
        L_0x0e23:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0e56
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r9)
            r3.setText(r2)
            goto L_0x0e20
        L_0x0e56:
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131625372(0x7f0e059c, float:1.887795E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r9] = r0
            java.lang.String r4 = "DiceEmojiInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            android.widget.TextView r4 = r1.infoTextView
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r6, r9)
            r2.setText(r3)
        L_0x0e7d:
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r0 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r0.setScaleType(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r12.topMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r12.bottomMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r13.leftMargin = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.width = r0
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r13.height = r0
        L_0x0eaf:
            android.widget.TextView r0 = r1.undoTextView
            r2 = 2131627972(0x7f0e0fc4, float:1.8883224E38)
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0var_
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
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.undoTextView
            java.lang.String r3 = "undo_cancelColor"
            int r3 = r1.getThemedColor(r3)
            r2.setTextColor(r3)
            android.widget.ImageView r2 = r1.undoImageView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r9)
            goto L_0x0var_
        L_0x0var_:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0var_:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r2
            r12.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            r0 = 1088421888(0x40e00000, float:7.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.bottomMargin = r0
            r0 = -1
            r12.height = r0
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            goto L_0x109f
        L_0x0var_:
            r2 = 8
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = r4
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            android.widget.ImageView r4 = r1.undoImageView
            r4.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r2.setVisibility(r9)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x100a
            android.widget.TextView r4 = r1.infoTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r1.infoTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r6 = 1
            r4.setTextSize(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            r4.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = r1.getThemedColor(r2)
            java.lang.String r6 = "BODY.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = r1.getThemedColor(r2)
            java.lang.String r6 = "Wibe Big.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r5 = r1.getThemedColor(r2)
            java.lang.String r6 = "Wibe Big 3.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieImageView r4 = r1.leftImageView
            int r2 = r1.getThemedColor(r2)
            java.lang.String r5 = "Wibe Small.**"
            r4.setLayerColor(r5, r2)
            android.widget.TextView r2 = r1.infoTextView
            r4 = 2131627552(0x7f0e0e20, float:1.8882372E38)
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            r4 = 2131558467(0x7f0d0043, float:1.874225E38)
            r5 = 28
            r6 = 28
            r2.setAnimation(r4, r5, r6)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0fe2
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627554(0x7f0e0e22, float:1.8882376E38)
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r3 = 1
            r6[r3] = r0
            java.lang.String r0 = "ProximityAlertSetInfoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
            goto L_0x0ffb
        L_0x0fe2:
            r3 = 1
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 2131627553(0x7f0e0e21, float:1.8882374E38)
            java.lang.Object[] r6 = new java.lang.Object[r3]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r6[r9] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
        L_0x0ffb:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            goto L_0x108e
        L_0x100a:
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Body Main.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Body Top.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Line.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = r1.getThemedColor(r2)
            java.lang.String r4 = "Curve Big.**"
            r0.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = r1.getThemedColor(r2)
            java.lang.String r3 = "Curve Small.**"
            r0.setLayerColor(r3, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131627551(0x7f0e0e1f, float:1.888237E38)
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558461(0x7f0d003d, float:1.8742238E38)
            r3 = 28
            r4 = 28
            r0.setAnimation(r2, r3, r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = r1.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
        L_0x108e:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x109f:
            r0 = 0
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1686
        L_0x10a4:
            android.widget.ImageView r0 = r1.undoImageView
            r5 = 8
            r0.setVisibility(r5)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setVisibility(r9)
            android.widget.TextView r0 = r1.infoTextView
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r5)
            r5 = -1
            int r0 = r1.currentAction
            r8 = 76
            r13 = 1091567616(0x41100000, float:9.0)
            if (r0 != r8) goto L_0x10e8
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624691(0x7f0e02f3, float:1.8876569E38)
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
        L_0x10e5:
            r0 = 1
            goto L_0x1648
        L_0x10e8:
            r8 = 75
            if (r0 != r8) goto L_0x1111
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131625964(0x7f0e07ec, float:1.887915E38)
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10e5
        L_0x1111:
            r8 = 70
            if (r2 != r8) goto L_0x115d
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r9)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            android.widget.TextView r2 = r1.infoTextView
            r3 = 2131624494(0x7f0e022e, float:1.887617E38)
            r4 = 1
            java.lang.Object[] r8 = new java.lang.Object[r4]
            r8[r9] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r8)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558447(0x7f0d002f, float:1.874221E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            r2 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r9, r9, r2)
            r0 = 1
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1648
        L_0x115d:
            r2 = 71
            if (r0 != r2) goto L_0x1192
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131624493(0x7f0e022d, float:1.8876167E38)
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r0.setAnimation(r2, r7, r7)
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            r2 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r2
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r9, r9, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x1647
        L_0x1192:
            r2 = 45
            if (r0 != r2) goto L_0x11bc
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558442(0x7f0d002a, float:1.87422E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10e5
        L_0x11bc:
            r2 = 46
            if (r0 != r2) goto L_0x11e6
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626089(0x7f0e0869, float:1.8879404E38)
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558442(0x7f0d002a, float:1.87422E38)
            r0.setAnimation(r2, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10e5
        L_0x11e6:
            r2 = 47
            if (r0 != r2) goto L_0x121b
            android.widget.TextView r0 = r1.infoTextView
            r2 = 2131626111(0x7f0e087f, float:1.8879449E38)
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 2131558473(0x7f0d0049, float:1.8742263E38)
            r0.setAnimation(r2, r7, r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r9, r9, r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x10e5
        L_0x121b:
            r2 = 1096810496(0x41600000, float:14.0)
            r8 = 51
            if (r0 != r8) goto L_0x1245
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624454(0x7f0e0206, float:1.8876088E38)
            java.lang.String r4 = "AudioSpeedNormal"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558408(0x7f0d0008, float:1.874213E38)
            r0.setAnimation(r3, r7, r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1647
        L_0x1245:
            r8 = 50
            if (r0 != r8) goto L_0x126d
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624453(0x7f0e0205, float:1.8876086E38)
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r0.setAnimation(r3, r7, r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1647
        L_0x126d:
            r8 = 52
            if (r0 == r8) goto L_0x15a4
            r8 = 56
            if (r0 == r8) goto L_0x15a4
            r8 = 57
            if (r0 == r8) goto L_0x15a4
            r8 = 58
            if (r0 == r8) goto L_0x15a4
            r8 = 59
            if (r0 == r8) goto L_0x15a4
            r8 = 60
            if (r0 == r8) goto L_0x15a4
            r8 = 80
            if (r0 != r8) goto L_0x128b
            goto L_0x15a4
        L_0x128b:
            r8 = 54
            if (r0 != r8) goto L_0x12b5
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624865(0x7f0e03a1, float:1.8876922E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558515(0x7f0d0073, float:1.8742348E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1647
        L_0x12b5:
            r8 = 55
            if (r0 != r8) goto L_0x12df
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624864(0x7f0e03a0, float:1.887692E38)
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x1647
        L_0x12df:
            r8 = 41
            if (r0 != r8) goto L_0x1390
            if (r4 != 0) goto L_0x135e
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1305
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131626134(0x7f0e0896, float:1.8879496E38)
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1382
        L_0x1305:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r0 == 0) goto L_0x1334
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626133(0x7f0e0895, float:1.8879494E38)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1382
        L_0x1334:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626135(0x7f0e0897, float:1.8879498E38)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1382
        L_0x135e:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626132(0x7f0e0894, float:1.8879492E38)
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r8 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r8, r0)
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x1382:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r0.setAnimation(r3, r7, r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1647
        L_0x1390:
            r7 = 53
            if (r0 != r7) goto L_0x14da
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1480
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.clientUserId
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x13dd
            int r0 = r0.intValue()
            r3 = 1
            if (r0 != r3) goto L_0x13bf
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625934(0x7f0e07ce, float:1.887909E38)
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13d1
        L_0x13bf:
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131625938(0x7f0e07d2, float:1.8879098E38)
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x13d1:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x14d4
        L_0x13dd:
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r3 == 0) goto L_0x142b
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r10
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x1412
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131625933(0x7f0e07cd, float:1.8879088E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1475
        L_0x1412:
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131625937(0x7f0e07d1, float:1.8879096E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1475
        L_0x142b:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x145b
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131625935(0x7f0e07cf, float:1.8879092E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1475
        L_0x145b:
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131625939(0x7f0e07d3, float:1.88791E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x1475:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x14d1
        L_0x1480:
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x14ab
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r7 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x14c7
        L_0x14ab:
            android.widget.TextView r0 = r1.infoTextView
            r5 = 2131625936(0x7f0e07d0, float:1.8879094E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r4 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r4, r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x14c7:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x14d1:
            r3 = 300(0x12c, double:1.48E-321)
            r5 = r3
        L_0x14d4:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1647
        L_0x14da:
            r7 = 61
            if (r0 != r7) goto L_0x1647
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x1570
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x150d
            android.widget.TextView r0 = r1.infoTextView
            r3 = 2131624599(0x7f0e0297, float:1.8876382E38)
            java.lang.String r4 = "BackgroundToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x159e
        L_0x150d:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r0 == 0) goto L_0x153c
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624598(0x7f0e0296, float:1.887638E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1565
        L_0x153c:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624600(0x7f0e0298, float:1.8876384E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x1565:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
            goto L_0x159e
        L_0x1570:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131624597(0x7f0e0295, float:1.8876378E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r7 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r7, r0)
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r3 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r4 = 30
            r0.setAnimation(r3, r4, r4)
        L_0x159e:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x1647
        L_0x15a4:
            boolean r0 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r0 != 0) goto L_0x15ab
            return
        L_0x15ab:
            r0 = 2131558432(0x7f0d0020, float:1.874218E38)
            int r3 = r1.currentAction
            r4 = 80
            if (r3 != r4) goto L_0x15c4
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131625500(0x7f0e061c, float:1.887821E38)
            java.lang.String r7 = "EmailCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x1634
        L_0x15c4:
            r4 = 60
            if (r3 != r4) goto L_0x15d7
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131627326(0x7f0e0d3e, float:1.8881913E38)
            java.lang.String r7 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x1634
        L_0x15d7:
            r4 = 56
            if (r3 != r4) goto L_0x15ea
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131628612(0x7f0e1244, float:1.8884522E38)
            java.lang.String r7 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x1634
        L_0x15ea:
            r4 = 57
            if (r3 != r4) goto L_0x15fd
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626021(0x7f0e0825, float:1.8879266E38)
            java.lang.String r7 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x1634
        L_0x15fd:
            r4 = 52
            if (r3 != r4) goto L_0x1610
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r7 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x1634
        L_0x1610:
            r4 = 59
            if (r3 != r4) goto L_0x1626
            r0 = 2131558570(0x7f0d00aa, float:1.874246E38)
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131626279(0x7f0e0927, float:1.887979E38)
            java.lang.String r7 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x1634
        L_0x1626:
            android.widget.TextView r3 = r1.infoTextView
            r4 = 2131628369(0x7f0e1151, float:1.8884029E38)
            java.lang.String r7 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
        L_0x1634:
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 30
            r3.setAnimation(r0, r4, r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
        L_0x1647:
            r0 = 0
        L_0x1648:
            android.widget.TextView r3 = r1.subinfoTextView
            r4 = 8
            r3.setVisibility(r4)
            android.widget.TextView r3 = r1.undoTextView
            java.lang.String r7 = "undo_cancelColor"
            int r7 = r1.getThemedColor(r7)
            r3.setTextColor(r7)
            android.widget.LinearLayout r3 = r1.undoButton
            r3.setVisibility(r4)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r12.rightMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 0
            r3.setProgress(r4)
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r3.playAnimation()
            r3 = 0
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x1686
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r4.<init>(r1)
            r3.postDelayed(r4, r5)
        L_0x1686:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.widget.TextView r4 = r1.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x16b3
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ". "
            r4.append(r5)
            android.widget.TextView r5 = r1.subinfoTextView
            java.lang.CharSequence r5 = r5.getText()
            r4.append(r5)
            java.lang.String r14 = r4.toString()
        L_0x16b3:
            r3.append(r14)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r3)
            boolean r3 = r19.isMultilineSubInfo()
            if (r3 == 0) goto L_0x1708
            android.view.ViewParent r0 = r19.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x16d3
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x16d3:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r9)
            r5 = 0
            r20 = r19
            r21 = r2
            r22 = r0
            r23 = r3
            r24 = r4
            r25 = r5
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r0 = r1.subinfoTextView
            int r0 = r0.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.undoViewHeight = r0
            goto L_0x17ac
        L_0x1708:
            boolean r3 = r19.hasSubInfo()
            if (r3 == 0) goto L_0x1718
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x17ac
        L_0x1718:
            android.view.ViewParent r3 = r19.getParent()
            boolean r3 = r3 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x17ac
            android.view.ViewParent r3 = r19.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = r3.getMeasuredWidth()
            int r5 = r3.getPaddingLeft()
            int r4 = r4 - r5
            int r3 = r3.getPaddingRight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x173a
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
        L_0x173a:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r3
            android.widget.TextView r3 = r1.infoTextView
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            r5 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r9)
            r7 = 0
            r20 = r19
            r21 = r3
            r22 = r4
            r23 = r5
            r24 = r6
            r25 = r7
            r20.measureChildWithMargins(r21, r22, r23, r24, r25)
            android.widget.TextView r3 = r1.infoTextView
            int r3 = r3.getMeasuredHeight()
            int r4 = r1.currentAction
            r5 = 16
            if (r4 == r5) goto L_0x1776
            r5 = 17
            if (r4 == r5) goto L_0x1776
            r5 = 18
            if (r4 != r5) goto L_0x1773
            goto L_0x1776
        L_0x1773:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1778
        L_0x1776:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x1778:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 + r2
            r1.undoViewHeight = r3
            int r2 = r1.currentAction
            r4 = 18
            if (r2 != r4) goto L_0x1792
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x17ac
        L_0x1792:
            r4 = 25
            if (r2 != r4) goto L_0x17a3
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x17ac
        L_0x17a3:
            if (r0 == 0) goto L_0x17ac
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r0
            r1.undoViewHeight = r3
        L_0x17ac:
            int r0 = r19.getVisibility()
            if (r0 == 0) goto L_0x180c
            r1.setVisibility(r9)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x17bc
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x17be
        L_0x17bc:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x17be:
            int r2 = r1.enterOffsetMargin
            int r3 = r1.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r0 = r0 * r2
            r1.setEnterOffset(r0)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r2 = 1
            android.animation.Animator[] r3 = new android.animation.Animator[r2]
            r2 = 2
            float[] r2 = new float[r2]
            boolean r4 = r1.fromTop
            if (r4 == 0) goto L_0x17db
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x17dd
        L_0x17db:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x17dd:
            int r6 = r1.enterOffsetMargin
            int r7 = r1.undoViewHeight
            int r6 = r6 + r7
            float r6 = (float) r6
            float r5 = r5 * r6
            r2[r9] = r5
            if (r4 == 0) goto L_0x17ec
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x17ee
        L_0x17ec:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x17ee:
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "enterOffset"
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r1, r4, r2)
            r3[r9] = r2
            r0.playTogether(r3)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r2)
            r0.start()
        L_0x180c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(java.util.ArrayList, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$2(View view) {
        hide(false, 1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$6(TLRPC$Message tLRPC$Message, View view) {
        hide(true, 1);
        TLRPC$TL_payments_getPaymentReceipt tLRPC$TL_payments_getPaymentReceipt = new TLRPC$TL_payments_getPaymentReceipt();
        tLRPC$TL_payments_getPaymentReceipt.msg_id = tLRPC$Message.id;
        tLRPC$TL_payments_getPaymentReceipt.peer = this.parentFragment.getMessagesController().getInputPeer(tLRPC$Message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_getPaymentReceipt, new UndoView$$ExternalSyntheticLambda7(this), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new UndoView$$ExternalSyntheticLambda6(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$4(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showWithAction$7() {
        this.leftImageView.performHapticFeedback(3, 2);
    }

    public void setEnterOffsetMargin(int i) {
        this.enterOffsetMargin = i;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, NUM));
        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float measuredHeight = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) AndroidUtilities.dp(9.0f));
            if (measuredHeight > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), measuredHeight);
                super.dispatchDraw(canvas);
            }
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float measuredHeight = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) this.enterOffsetMargin) + ((float) AndroidUtilities.dp(1.0f));
            if (measuredHeight > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), measuredHeight);
                super.dispatchDraw(canvas);
            }
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        } else {
            this.backgroundDrawable.draw(canvas);
        }
        int i = this.currentAction;
        if (i == 1 || i == 0 || i == 27 || i == 26 || i == 81) {
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
            hide(true, this.hideAnimationType);
        }
        if (this.currentAction != 82) {
            invalidate();
        }
    }

    public void invalidate() {
        super.invalidate();
        this.infoTextView.invalidate();
        this.leftImageView.invalidate();
    }

    public void setInfoText(CharSequence charSequence) {
        this.infoText = charSequence;
    }

    public void setHideAnimationType(int i) {
        this.hideAnimationType = i;
    }

    @Keep
    public float getEnterOffset() {
        return this.enterOffset;
    }

    @Keep
    public void setEnterOffset(float f) {
        if (this.enterOffset != f) {
            this.enterOffset = f;
            updatePosition();
        }
    }

    private void updatePosition() {
        setTranslationY(((this.enterOffset - ((float) this.enterOffsetMargin)) + ((float) AndroidUtilities.dp(8.0f))) - this.additionalTranslationY);
        invalidate();
    }

    public Drawable getBackground() {
        return this.backgroundDrawable;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
