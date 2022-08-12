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
import org.telegram.messenger.R;
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
        this.currentAction = -1;
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
        imageView.setImageResource(R.drawable.chats_undo);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19, 4, 4, 0, 4));
        TextView textView3 = new TextView(context2);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(getThemedColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", R.string.Undo));
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

    /* JADX WARNING: Removed duplicated region for block: B:565:0x15ed  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x160d  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x1634  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x1679  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x1723  */
    /* JADX WARNING: Removed duplicated region for block: B:626:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r21, int r22, java.lang.Object r23, java.lang.Object r24, java.lang.Runnable r25, java.lang.Runnable r26) {
        /*
            r20 = this;
            r1 = r20
            r0 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r25
            r6 = r26
            boolean r7 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            r8 = 33
            if (r7 != 0) goto L_0x0037
            int r7 = r1.currentAction
            r9 = 52
            if (r7 == r9) goto L_0x0036
            r9 = 56
            if (r7 == r9) goto L_0x0036
            r9 = 57
            if (r7 == r9) goto L_0x0036
            r9 = 58
            if (r7 == r9) goto L_0x0036
            r9 = 59
            if (r7 == r9) goto L_0x0036
            r9 = 60
            if (r7 == r9) goto L_0x0036
            r9 = 80
            if (r7 == r9) goto L_0x0036
            if (r7 != r8) goto L_0x0037
        L_0x0036:
            return
        L_0x0037:
            java.lang.Runnable r7 = r1.currentActionRunnable
            if (r7 == 0) goto L_0x003e
            r7.run()
        L_0x003e:
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
            int r13 = org.telegram.messenger.R.string.Undo
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
            if (r5 != 0) goto L_0x00d9
            if (r6 == 0) goto L_0x00dd
        L_0x00d9:
            int r6 = ACTION_RINGTONE_ADDED
            if (r2 != r6) goto L_0x00e9
        L_0x00dd:
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r6.<init>(r1)
            r1.setOnClickListener(r6)
            r1.setOnTouchListener(r15)
            goto L_0x00f1
        L_0x00e9:
            r1.setOnClickListener(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r6 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r1.setOnTouchListener(r6)
        L_0x00f1:
            android.widget.TextView r6 = r1.infoTextView
            r6.setMovementMethod(r15)
            boolean r6 = r20.isTooltipAction()
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            java.lang.String r14 = ""
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r7 = 36
            if (r6 == 0) goto L_0x0897
            int r0 = ACTION_RINGTONE_ADDED
            if (r2 != r0) goto L_0x012e
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            int r0 = org.telegram.messenger.R.string.SoundAdded
            java.lang.String r2 = "SoundAdded"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = org.telegram.messenger.R.string.SoundAddedSubtitle
            java.lang.String r3 = "SoundAddedSubtitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r2, r5)
            r1.currentActionRunnable = r15
            int r3 = org.telegram.messenger.R.raw.sound_download
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            r15 = r2
            goto L_0x07f0
        L_0x012e:
            r0 = 74
            if (r2 != r0) goto L_0x0151
            android.widget.TextView r0 = r1.subinfoTextView
            r0.setSingleLine(r9)
            int r0 = org.telegram.messenger.R.string.ReportChatSent
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = org.telegram.messenger.R.string.ReportSentInfo
            java.lang.Object[] r3 = new java.lang.Object[r9]
            java.lang.String r4 = "ReportSentInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            int r3 = org.telegram.messenger.R.raw.ic_admin
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x0151:
            r0 = 34
            if (r2 != r0) goto L_0x01af
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x0177
            int r2 = org.telegram.messenger.R.string.VoipChannelInvitedUser
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r5
            java.lang.String r5 = "VoipChannelInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x018c
        L_0x0177:
            r3 = 1
            int r2 = org.telegram.messenger.R.string.VoipGroupInvitedUser
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r3
            java.lang.String r3 = "VoipGroupInvitedUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x018c:
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
        L_0x01ac:
            r3 = 0
            goto L_0x07f0
        L_0x01af:
            r0 = 44
            if (r2 != r0) goto L_0x0242
            r0 = r4
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x01ef
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x01d9
            int r0 = org.telegram.messenger.R.string.VoipChannelUserJoined
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r9] = r2
            java.lang.String r2 = "VoipChannelUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x021f
        L_0x01d9:
            r4 = 1
            int r0 = org.telegram.messenger.R.string.VoipChatUserJoined
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r5[r9] = r2
            java.lang.String r2 = "VoipChatUserJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x021f
        L_0x01ef:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x020c
            int r0 = org.telegram.messenger.R.string.VoipChannelChatJoined
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r9] = r2
            java.lang.String r2 = "VoipChannelChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x021f
        L_0x020c:
            r4 = 1
            int r0 = org.telegram.messenger.R.string.VoipChatChatJoined
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r5[r9] = r2
            java.lang.String r2 = "VoipChatChatJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x021f:
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
            goto L_0x01ac
        L_0x0242:
            r0 = 37
            if (r2 != r0) goto L_0x02b0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x026c
            r2 = r3
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r2)
            goto L_0x0279
        L_0x026c:
            r2 = r3
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.ui.Components.BackupImageView r3 = r1.avatarImageView
            r3.setForUserOrChat(r2, r0)
            java.lang.String r0 = r2.title
        L_0x0279:
            r2 = r4
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r2 == 0) goto L_0x0294
            int r2 = org.telegram.messenger.R.string.VoipChannelUserChanged
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipChannelUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02a5
        L_0x0294:
            r3 = 1
            int r2 = org.telegram.messenger.R.string.VoipGroupUserChanged
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02a5:
            org.telegram.ui.Components.BackupImageView r2 = r1.avatarImageView
            r2.setVisibility(r9)
            r5 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r5
            goto L_0x01ac
        L_0x02b0:
            r0 = 33
            r5 = 3000(0xbb8, double:1.482E-320)
            if (r2 != r0) goto L_0x02c4
            int r0 = org.telegram.messenger.R.string.VoipGroupCopyInviteLinkCopied
            java.lang.String r2 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r3 = org.telegram.messenger.R.raw.voip_invite
            r1.timeLeft = r5
            goto L_0x07f0
        L_0x02c4:
            r0 = 77
            if (r2 != r0) goto L_0x02ee
            r0 = r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            int r3 = org.telegram.messenger.R.raw.payment_success
            r5 = 5000(0x1388, double:2.4703E-320)
            r1.timeLeft = r5
            org.telegram.ui.ActionBar.BaseFragment r2 = r1.parentFragment
            if (r2 == 0) goto L_0x07f0
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$Message
            if (r2 == 0) goto L_0x07f0
            r2 = r4
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            r1.setOnTouchListener(r15)
            android.widget.TextView r4 = r1.infoTextView
            r4.setMovementMethod(r15)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r4.<init>(r1, r2)
            r1.setOnClickListener(r4)
            goto L_0x07f0
        L_0x02ee:
            r0 = 30
            if (r2 != r0) goto L_0x031c
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x02fe
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0303
        L_0x02fe:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0303:
            int r2 = org.telegram.messenger.R.string.VoipGroupUserCantNowSpeak
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            int r3 = org.telegram.messenger.R.raw.voip_muted
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x031c:
            r0 = 35
            if (r2 != r0) goto L_0x0350
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x032c
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0337
        L_0x032c:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x0336
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0337
        L_0x0336:
            r0 = r14
        L_0x0337:
            int r2 = org.telegram.messenger.R.string.VoipGroupUserCantNowSpeakForYou
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            int r3 = org.telegram.messenger.R.raw.voip_muted
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x0350:
            r0 = 31
            if (r2 != r0) goto L_0x037e
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0360
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0365
        L_0x0360:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0365:
            int r2 = org.telegram.messenger.R.string.VoipGroupUserCanNowSpeak
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            int r3 = org.telegram.messenger.R.raw.voip_unmuted
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x037e:
            r0 = 38
            if (r2 != r0) goto L_0x03b1
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x039d
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            int r2 = org.telegram.messenger.R.string.VoipGroupYouCanNowSpeakIn
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = r0.title
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03a9
        L_0x039d:
            int r0 = org.telegram.messenger.R.string.VoipGroupYouCanNowSpeak
            java.lang.String r2 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03a9:
            int r3 = org.telegram.messenger.R.raw.voip_allow_talk
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x03b1:
            r0 = 42
            if (r2 != r0) goto L_0x03df
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03cb
            int r0 = org.telegram.messenger.R.string.VoipChannelSoundMuted
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x03d7
        L_0x03cb:
            int r0 = org.telegram.messenger.R.string.VoipGroupSoundMuted
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x03d7:
            int r3 = org.telegram.messenger.R.raw.ic_mute
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x03df:
            r0 = 43
            if (r2 != r0) goto L_0x040d
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r0 == 0) goto L_0x03f9
            int r0 = org.telegram.messenger.R.string.VoipChannelSoundUnmuted
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0405
        L_0x03f9:
            int r0 = org.telegram.messenger.R.string.VoipGroupSoundUnmuted
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0405:
            int r3 = org.telegram.messenger.R.raw.ic_unmute
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x040d:
            int r0 = r1.currentAction
            r5 = 39
            if (r0 == r5) goto L_0x07d5
            r5 = 100
            if (r0 != r5) goto L_0x0419
            goto L_0x07d5
        L_0x0419:
            r5 = 40
            if (r0 == r5) goto L_0x0765
            r5 = 101(0x65, float:1.42E-43)
            if (r0 != r5) goto L_0x0423
            goto L_0x0765
        L_0x0423:
            if (r2 != r7) goto L_0x044f
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0431
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0436
        L_0x0431:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0436:
            int r2 = org.telegram.messenger.R.string.VoipGroupUserCanNowSpeakForYou
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            int r3 = org.telegram.messenger.R.raw.voip_unmuted
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x044f:
            r5 = 32
            if (r2 != r5) goto L_0x047d
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x045f
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0464
        L_0x045f:
            r0 = r3
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            java.lang.String r0 = r0.title
        L_0x0464:
            int r2 = org.telegram.messenger.R.string.VoipGroupRemovedFromGroup
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r4[r9] = r0
            java.lang.String r0 = "VoipGroupRemovedFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            int r3 = org.telegram.messenger.R.raw.voip_group_removed
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x047d:
            r5 = 9
            if (r2 == r5) goto L_0x072f
            r5 = 10
            if (r2 != r5) goto L_0x0487
            goto L_0x072f
        L_0x0487:
            r5 = 8
            if (r2 != r5) goto L_0x04a3
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            int r2 = org.telegram.messenger.R.string.NowInContacts
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "NowInContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            int r3 = org.telegram.messenger.R.raw.contact_check
            goto L_0x07f0
        L_0x04a3:
            r5 = 22
            if (r2 != r5) goto L_0x0505
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r0 == 0) goto L_0x04c1
            if (r3 != 0) goto L_0x04b8
            int r0 = org.telegram.messenger.R.string.MainProfilePhotoSetHint
            java.lang.String r2 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0501
        L_0x04b8:
            int r0 = org.telegram.messenger.R.string.MainProfileVideoSetHint
            java.lang.String r2 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0501
        L_0x04c1:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r4 = -r10
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x04ee
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x04ee
            if (r3 != 0) goto L_0x04e5
            int r0 = org.telegram.messenger.R.string.MainChannelProfilePhotoSetHint
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0501
        L_0x04e5:
            int r0 = org.telegram.messenger.R.string.MainChannelProfileVideoSetHint
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0501
        L_0x04ee:
            if (r3 != 0) goto L_0x04f9
            int r0 = org.telegram.messenger.R.string.MainGroupProfilePhotoSetHint
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0501
        L_0x04f9:
            int r0 = org.telegram.messenger.R.string.MainGroupProfileVideoSetHint
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0501:
            int r3 = org.telegram.messenger.R.raw.contact_check
            goto L_0x07f0
        L_0x0505:
            r5 = 23
            if (r2 != r5) goto L_0x0515
            int r0 = org.telegram.messenger.R.string.ChatWasMovedToMainList
            java.lang.String r2 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r3 = org.telegram.messenger.R.raw.contact_check
            goto L_0x07f0
        L_0x0515:
            r5 = 6
            if (r2 != r5) goto L_0x052e
            int r0 = org.telegram.messenger.R.string.ArchiveHidden
            java.lang.String r2 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = org.telegram.messenger.R.string.ArchiveHiddenInfo
            java.lang.String r3 = "ArchiveHiddenInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            int r3 = org.telegram.messenger.R.raw.chats_swipearchive
            r7 = 48
            goto L_0x07f0
        L_0x052e:
            r5 = 13
            if (r0 != r5) goto L_0x0548
            int r0 = org.telegram.messenger.R.string.QuizWellDone
            java.lang.String r2 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = org.telegram.messenger.R.string.QuizWellDoneInfo
            java.lang.String r3 = "QuizWellDoneInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            int r3 = org.telegram.messenger.R.raw.wallet_congrats
        L_0x0544:
            r7 = 44
            goto L_0x07f0
        L_0x0548:
            r5 = 14
            if (r0 != r5) goto L_0x055f
            int r0 = org.telegram.messenger.R.string.QuizWrongAnswer
            java.lang.String r2 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = org.telegram.messenger.R.string.QuizWrongAnswerInfo
            java.lang.String r3 = "QuizWrongAnswerInfo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r3, r2)
            int r3 = org.telegram.messenger.R.raw.wallet_science
            goto L_0x0544
        L_0x055f:
            r0 = 7
            if (r2 != r0) goto L_0x0585
            int r0 = org.telegram.messenger.R.string.ArchivePinned
            java.lang.String r2 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0581
            int r2 = org.telegram.messenger.R.string.ArchivePinnedInfo
            java.lang.String r3 = "ArchivePinnedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = r2
        L_0x0581:
            int r3 = org.telegram.messenger.R.raw.chats_infotip
            goto L_0x07f0
        L_0x0585:
            r0 = 20
            if (r2 == r0) goto L_0x0623
            r5 = 21
            if (r2 != r5) goto L_0x058f
            goto L_0x0623
        L_0x058f:
            r0 = 19
            if (r2 != r0) goto L_0x0599
            java.lang.CharSequence r0 = r1.infoText
            int r3 = org.telegram.messenger.R.raw.chats_infotip
            goto L_0x07f0
        L_0x0599:
            r0 = 82
            if (r2 != r0) goto L_0x05b3
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            boolean r0 = r0.isVideo
            if (r0 == 0) goto L_0x05a9
            int r0 = org.telegram.messenger.R.string.AttachMediaVideoDeselected
            java.lang.String r2 = "AttachMediaVideoDeselected"
            goto L_0x05ad
        L_0x05a9:
            int r0 = org.telegram.messenger.R.string.AttachMediaPhotoDeselected
            java.lang.String r2 = "AttachMediaPhotoDeselected"
        L_0x05ad:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x01ac
        L_0x05b3:
            r0 = 78
            if (r2 == r0) goto L_0x05eb
            r0 = 79
            if (r2 != r0) goto L_0x05bc
            goto L_0x05eb
        L_0x05bc:
            r0 = 3
            if (r2 != r0) goto L_0x05c8
            int r0 = org.telegram.messenger.R.string.ChatArchived
            java.lang.String r2 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x05d0
        L_0x05c8:
            int r0 = org.telegram.messenger.R.string.ChatsArchived
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x05d0:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05e7
            int r2 = org.telegram.messenger.R.string.ChatArchivedInfo
            java.lang.String r3 = "ChatArchivedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r15 = r2
        L_0x05e7:
            int r3 = org.telegram.messenger.R.raw.chats_infotip
            goto L_0x07f0
        L_0x05eb:
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r3 = 78
            if (r2 != r3) goto L_0x05ff
            java.lang.Object[] r2 = new java.lang.Object[r9]
            java.lang.String r3 = "PinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r2)
            goto L_0x0607
        L_0x05ff:
            java.lang.Object[] r2 = new java.lang.Object[r9]
            java.lang.String r3 = "UnpinnedDialogsCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r2)
        L_0x0607:
            int r2 = r1.currentAction
            r3 = 78
            if (r2 != r3) goto L_0x0610
            int r2 = org.telegram.messenger.R.raw.ic_pin
            goto L_0x0612
        L_0x0610:
            int r2 = org.telegram.messenger.R.raw.ic_unpin
        L_0x0612:
            r3 = r2
            boolean r2 = r4 instanceof java.lang.Integer
            if (r2 == 0) goto L_0x07f0
            r2 = r4
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            long r4 = (long) r2
            r1.timeLeft = r4
            goto L_0x07f0
        L_0x0623:
            org.telegram.messenger.MessagesController$DialogFilter r4 = (org.telegram.messenger.MessagesController.DialogFilter) r4
            r5 = 0
            int r8 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r8 == 0) goto L_0x06d6
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)
            if (r3 == 0) goto L_0x0645
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r10)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r5)
            long r10 = r3.user_id
        L_0x0645:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r3 == 0) goto L_0x0693
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r5 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
            if (r2 != r0) goto L_0x0677
            int r5 = org.telegram.messenger.R.string.FilterUserAddedToExisting
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r8[r9] = r3
            java.lang.String r3 = r4.name
            r10 = 1
            r8[r10] = r3
            java.lang.String r3 = "FilterUserAddedToExisting"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            goto L_0x0721
        L_0x0677:
            r6 = 2
            r10 = 1
            int r5 = org.telegram.messenger.R.string.FilterUserRemovedFrom
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r8[r9] = r3
            java.lang.String r3 = r4.name
            r8[r10] = r3
            java.lang.String r3 = "FilterUserRemovedFrom"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            goto L_0x0721
        L_0x0693:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r5 = -r10
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r5)
            if (r2 != r0) goto L_0x06bd
            int r5 = org.telegram.messenger.R.string.FilterChatAddedToExisting
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r3 = r3.title
            r8[r9] = r3
            java.lang.String r3 = r4.name
            r10 = 1
            r8[r10] = r3
            java.lang.String r3 = "FilterChatAddedToExisting"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            goto L_0x0721
        L_0x06bd:
            r6 = 2
            r10 = 1
            int r5 = org.telegram.messenger.R.string.FilterChatRemovedFrom
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r3 = r3.title
            r8[r9] = r3
            java.lang.String r3 = r4.name
            r8[r10] = r3
            java.lang.String r3 = "FilterChatRemovedFrom"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            goto L_0x0721
        L_0x06d6:
            if (r2 != r0) goto L_0x06fd
            int r5 = org.telegram.messenger.R.string.FilterChatsAddedToExisting
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r10 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3, r6)
            r8[r9] = r3
            java.lang.String r3 = r4.name
            r4 = 1
            r8[r4] = r3
            java.lang.String r3 = "FilterChatsAddedToExisting"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            goto L_0x0721
        L_0x06fd:
            int r5 = org.telegram.messenger.R.string.FilterChatsRemovedFrom
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r10 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3, r6)
            r8[r9] = r3
            java.lang.String r3 = r4.name
            r4 = 1
            r8[r4] = r3
            java.lang.String r3 = "FilterChatsRemovedFrom"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r8)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
        L_0x0721:
            if (r2 != r0) goto L_0x0726
            int r0 = org.telegram.messenger.R.raw.folder_in
            goto L_0x0728
        L_0x0726:
            int r0 = org.telegram.messenger.R.raw.folder_out
        L_0x0728:
            r19 = r3
            r3 = r0
            r0 = r19
            goto L_0x07f0
        L_0x072f:
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r3 = 9
            if (r2 != r3) goto L_0x074c
            int r2 = org.telegram.messenger.R.string.EditAdminTransferChannelToast
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "EditAdminTransferChannelToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0761
        L_0x074c:
            r3 = 1
            int r2 = org.telegram.messenger.R.string.EditAdminTransferGroupToast
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r4[r9] = r0
            java.lang.String r0 = "EditAdminTransferGroupToast"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0761:
            int r3 = org.telegram.messenger.R.raw.contact_check
            goto L_0x07f0
        L_0x0765:
            r2 = 40
            if (r0 != r2) goto L_0x076e
            int r0 = org.telegram.messenger.R.string.VoipGroupAudioRecordSaved
            java.lang.String r2 = "VoipGroupAudioRecordSaved"
            goto L_0x0772
        L_0x076e:
            int r0 = org.telegram.messenger.R.string.VoipGroupVideoRecordSaved
            java.lang.String r2 = "VoipGroupVideoRecordSaved"
        L_0x0772:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            int r3 = org.telegram.messenger.R.raw.voip_record_saved
            r4 = 4000(0xfa0, double:1.9763E-320)
            r1.timeLeft = r4
            android.widget.TextView r2 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r4 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r4.<init>()
            r2.setMovementMethod(r4)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            java.lang.String r4 = "**"
            int r4 = r0.indexOf(r4)
            java.lang.String r5 = "**"
            int r0 = r0.lastIndexOf(r5)
            if (r4 < 0) goto L_0x07d3
            if (r0 < 0) goto L_0x07d3
            if (r4 == r0) goto L_0x07d3
            int r5 = r0 + 2
            r2.replace(r0, r5, r14)
            int r5 = r4 + 2
            r2.replace(r4, r5, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x07cf }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07cf }
            r6.<init>()     // Catch:{ Exception -> 0x07cf }
            java.lang.String r8 = "tg://openmessage?user_id="
            r6.append(r8)     // Catch:{ Exception -> 0x07cf }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x07cf }
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ Exception -> 0x07cf }
            long r10 = r8.getClientUserId()     // Catch:{ Exception -> 0x07cf }
            r6.append(r10)     // Catch:{ Exception -> 0x07cf }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x07cf }
            r5.<init>(r6)     // Catch:{ Exception -> 0x07cf }
            r6 = 2
            int r0 = r0 - r6
            r6 = 33
            r2.setSpan(r5, r4, r0, r6)     // Catch:{ Exception -> 0x07cf }
            goto L_0x07d3
        L_0x07cf:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07d3:
            r0 = r2
            goto L_0x07f0
        L_0x07d5:
            r2 = 39
            if (r0 != r2) goto L_0x07de
            int r0 = org.telegram.messenger.R.string.VoipGroupAudioRecordStarted
            java.lang.String r2 = "VoipGroupAudioRecordStarted"
            goto L_0x07e2
        L_0x07de:
            int r0 = org.telegram.messenger.R.string.VoipGroupVideoRecordStarted
            java.lang.String r2 = "VoipGroupVideoRecordStarted"
        L_0x07e2:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            int r3 = org.telegram.messenger.R.raw.voip_record_start
            r4 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r4
        L_0x07f0:
            android.widget.TextView r2 = r1.infoTextView
            r2.setText(r0)
            if (r3 == 0) goto L_0x081d
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.setAnimation((int) r3, (int) r7, (int) r7)
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
            goto L_0x0824
        L_0x081d:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 8
            r0.setVisibility(r2)
        L_0x0824:
            if (r15 == 0) goto L_0x0866
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
            goto L_0x0890
        L_0x0866:
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
        L_0x0890:
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r2)
            goto L_0x103e
        L_0x0897:
            int r5 = r1.currentAction
            r6 = 45
            if (r5 == r6) goto L_0x1043
            r6 = 46
            if (r5 == r6) goto L_0x1043
            r6 = 47
            if (r5 == r6) goto L_0x1043
            r6 = 51
            if (r5 == r6) goto L_0x1043
            r6 = 50
            if (r5 == r6) goto L_0x1043
            r6 = 52
            if (r5 == r6) goto L_0x1043
            r6 = 53
            if (r5 == r6) goto L_0x1043
            r6 = 54
            if (r5 == r6) goto L_0x1043
            r6 = 55
            if (r5 == r6) goto L_0x1043
            r6 = 56
            if (r5 == r6) goto L_0x1043
            r6 = 57
            if (r5 == r6) goto L_0x1043
            r6 = 58
            if (r5 == r6) goto L_0x1043
            r6 = 59
            if (r5 == r6) goto L_0x1043
            r6 = 60
            if (r5 == r6) goto L_0x1043
            r6 = 71
            if (r5 == r6) goto L_0x1043
            r6 = 70
            if (r5 == r6) goto L_0x1043
            r6 = 75
            if (r5 == r6) goto L_0x1043
            r6 = 76
            if (r5 == r6) goto L_0x1043
            r6 = 41
            if (r5 == r6) goto L_0x1043
            r6 = 78
            if (r5 == r6) goto L_0x1043
            r6 = 79
            if (r5 == r6) goto L_0x1043
            r6 = 61
            if (r5 == r6) goto L_0x1043
            r6 = 80
            if (r5 != r6) goto L_0x08f7
            goto L_0x1043
        L_0x08f7:
            r6 = 24
            if (r5 == r6) goto L_0x0edd
            r6 = 25
            if (r5 != r6) goto L_0x0901
            goto L_0x0edd
        L_0x0901:
            r4 = 11
            if (r5 != r4) goto L_0x0970
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC$TL_authorization) r0
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.AuthAnotherClientOk
            java.lang.String r4 = "AuthAnotherClientOk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.contact_check
            r2.setAnimation((int) r3, (int) r7, (int) r7)
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
            goto L_0x103e
        L_0x0970:
            r4 = 15
            if (r5 != r4) goto L_0x0a3e
            r2 = 10000(0x2710, double:4.9407E-320)
            r1.timeLeft = r2
            android.widget.TextView r0 = r1.undoTextView
            int r2 = org.telegram.messenger.R.string.Open
            java.lang.String r3 = "Open"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.FilterAvailableTitle
            java.lang.String r3 = "FilterAvailableTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.filter_new
            r0.setAnimation((int) r2, (int) r7, (int) r7)
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
            int r0 = org.telegram.messenger.R.string.FilterAvailableText
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0a0b
            if (r0 < 0) goto L_0x0a0b
            if (r4 == r0) goto L_0x0a0b
            int r3 = r0 + 1
            r2.replace(r0, r3, r14)
            int r3 = r4 + 1
            r2.replace(r4, r3, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/folders"
            r3.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x0a0b:
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
            goto L_0x103e
        L_0x0a3e:
            r4 = 16
            if (r5 == r4) goto L_0x0d6b
            r4 = 17
            if (r5 != r4) goto L_0x0a48
            goto L_0x0d6b
        L_0x0a48:
            r4 = 18
            if (r5 != r4) goto L_0x0ad2
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
            int r2 = org.telegram.messenger.R.raw.chats_infotip
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            android.widget.TextView r0 = r1.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r2.<init>()
            r0.setMovementMethod(r2)
            goto L_0x103e
        L_0x0ad2:
            r4 = 12
            if (r5 != r4) goto L_0x0b6b
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.ColorThemeChanged
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.drawable.toast_pallete
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
            int r0 = org.telegram.messenger.R.string.ColorThemeChangedInfo
            java.lang.String r2 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            if (r4 < 0) goto L_0x0b43
            if (r0 < 0) goto L_0x0b43
            if (r4 == r0) goto L_0x0b43
            int r3 = r0 + 1
            r2.replace(r0, r3, r14)
            int r3 = r4 + 1
            r2.replace(r4, r3, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r3 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r5 = "tg://settings/themes"
            r3.<init>(r5)
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x0b43:
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
            goto L_0x103e
        L_0x0b6b:
            r4 = 2
            if (r5 == r4) goto L_0x0d09
            r4 = 4
            if (r5 != r4) goto L_0x0b73
            goto L_0x0d09
        L_0x0b73:
            r4 = 82
            if (r2 != r4) goto L_0x0CLASSNAME
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            r0 = r3
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            android.widget.TextView r2 = r1.infoTextView
            boolean r3 = r0.isVideo
            if (r3 == 0) goto L_0x0b8b
            int r3 = org.telegram.messenger.R.string.AttachMediaVideoDeselected
            java.lang.String r4 = "AttachMediaVideoDeselected"
            goto L_0x0b8f
        L_0x0b8b:
            int r3 = org.telegram.messenger.R.string.AttachMediaPhotoDeselected
            java.lang.String r4 = "AttachMediaPhotoDeselected"
        L_0x0b8f:
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
            if (r2 == 0) goto L_0x0bce
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImage(r2, r15, r3)
            goto L_0x103e
        L_0x0bce:
            java.lang.String r2 = r0.path
            if (r2 == 0) goto L_0x0c2a
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
            goto L_0x103e
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
            goto L_0x103e
        L_0x0c2a:
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r0.setImageDrawable(r2)
            goto L_0x103e
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
            if (r2 == r3) goto L_0x0ccd
            if (r2 == 0) goto L_0x0ccd
            r3 = 26
            if (r2 != r3) goto L_0x0CLASSNAME
            goto L_0x0ccd
        L_0x0CLASSNAME:
            r3 = 27
            if (r2 != r3) goto L_0x0CLASSNAME
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.ChatsDeletedUndo
            java.lang.String r4 = "ChatsDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cda
        L_0x0CLASSNAME:
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r2 == 0) goto L_0x0cbf
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x0cb1
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0cb1
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.ChannelDeletedUndo
            java.lang.String r4 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cda
        L_0x0cb1:
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.GroupDeletedUndo
            java.lang.String r4 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cda
        L_0x0cbf:
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.ChatDeletedUndo
            java.lang.String r4 = "ChatDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x0cda
        L_0x0ccd:
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.HistoryClearedUndo
            java.lang.String r4 = "HistoryClearedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
        L_0x0cda:
            int r2 = r1.currentAction
            r3 = 81
            if (r2 == r3) goto L_0x103e
            r2 = 0
        L_0x0ce1:
            int r3 = r21.size()
            if (r2 >= r3) goto L_0x103e
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Object r4 = r0.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            long r4 = r4.longValue()
            int r6 = r1.currentAction
            if (r6 == 0) goto L_0x0d02
            r7 = 26
            if (r6 != r7) goto L_0x0d00
            goto L_0x0d02
        L_0x0d00:
            r6 = 0
            goto L_0x0d03
        L_0x0d02:
            r6 = 1
        L_0x0d03:
            r3.addDialogAction(r4, r6)
            int r2 = r2 + 1
            goto L_0x0ce1
        L_0x0d09:
            r3 = 2
            if (r2 != r3) goto L_0x0d1a
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.ChatArchived
            java.lang.String r3 = "ChatArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0d27
        L_0x0d1a:
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.ChatsArchived
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x0d27:
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
            int r2 = org.telegram.messenger.R.raw.chats_archived
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
            goto L_0x103e
        L_0x0d6b:
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
            if (r2 == 0) goto L_0x0dae
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.DiceInfo2
            java.lang.String r3 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.drawable.dice
            r0.setImageResource(r2)
            goto L_0x0e55
        L_0x0dae:
            java.lang.String r2 = "🎯"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0dca
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.DartInfo
            java.lang.String r4 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x0dc7:
            r5 = 1096810496(0x41600000, float:14.0)
            goto L_0x0e23
        L_0x0dca:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getServerString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0dfd
            android.widget.TextView r3 = r1.infoTextView
            android.text.TextPaint r4 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r9)
            r3.setText(r2)
            goto L_0x0dc7
        L_0x0dfd:
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.DiceEmojiInfo
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
        L_0x0e23:
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
        L_0x0e55:
            android.widget.TextView r0 = r1.undoTextView
            int r2 = org.telegram.messenger.R.string.SendDice
            java.lang.String r3 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            int r0 = r1.currentAction
            r2 = 16
            if (r0 != r2) goto L_0x0ea6
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
            goto L_0x0eb6
        L_0x0ea6:
            r3 = 8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.widget.TextView r2 = r1.undoTextView
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r1.undoButton
            r2.setVisibility(r3)
        L_0x0eb6:
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
            goto L_0x103e
        L_0x0edd:
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
            if (r0 == 0) goto L_0x0fab
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
            int r4 = org.telegram.messenger.R.string.ProximityAlertSet
            java.lang.String r5 = "ProximityAlertSet"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.RLottieImageView r2 = r1.leftImageView
            int r4 = org.telegram.messenger.R.raw.ic_unmute
            r5 = 28
            r6 = 28
            r2.setAnimation((int) r4, (int) r5, (int) r6)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setVisibility(r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r9)
            android.widget.TextView r2 = r1.subinfoTextView
            r4 = 3
            r2.setMaxLines(r4)
            if (r3 == 0) goto L_0x0var_
            android.widget.TextView r2 = r1.subinfoTextView
            int r4 = org.telegram.messenger.R.string.ProximityAlertSetInfoUser
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
            goto L_0x0f9c
        L_0x0var_:
            r3 = 1
            r5 = 2
            android.widget.TextView r2 = r1.subinfoTextView
            int r4 = org.telegram.messenger.R.string.ProximityAlertSetInfoGroup2
            java.lang.Object[] r6 = new java.lang.Object[r3]
            float r0 = (float) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatDistance(r0, r5)
            r6[r9] = r0
            java.lang.String r0 = "ProximityAlertSetInfoGroup2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r6)
            r2.setText(r0)
        L_0x0f9c:
            android.widget.LinearLayout r0 = r1.undoButton
            r2 = 8
            r0.setVisibility(r2)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r12.topMargin = r0
            goto L_0x102d
        L_0x0fab:
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
            int r2 = org.telegram.messenger.R.string.ProximityAlertCancelled
            java.lang.String r3 = "ProximityAlertCancelled"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.ic_mute
            r3 = 28
            r4 = 28
            r0.setAnimation((int) r2, (int) r3, (int) r4)
            android.widget.TextView r0 = r1.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r1.undoTextView
            java.lang.String r2 = "undo_cancelColor"
            int r2 = r1.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.LinearLayout r0 = r1.undoButton
            r0.setVisibility(r9)
        L_0x102d:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r12.leftMargin = r0
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r2 = 0
            r0.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            r0.playAnimation()
        L_0x103e:
            r0 = 0
            r2 = 1096810496(0x41600000, float:14.0)
            goto L_0x15f7
        L_0x1043:
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
            if (r0 != r8) goto L_0x1085
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.BroadcastGroupConvertSuccess
            java.lang.String r3 = "BroadcastGroupConvertSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.gigagroup_convert
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
        L_0x1082:
            r0 = 1
            goto L_0x15b9
        L_0x1085:
            r8 = 75
            if (r0 != r8) goto L_0x10ac
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.GigagroupConvertCancelHint
            java.lang.String r3 = "GigagroupConvertCancelHint"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.chats_infotip
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x1082
        L_0x10ac:
            r8 = 70
            if (r2 != r8) goto L_0x10f6
            r0 = r3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r2 = r1.subinfoTextView
            r2.setSingleLine(r9)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            android.widget.TextView r2 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.AutoDeleteHintOnText
            r4 = 1
            java.lang.Object[] r8 = new java.lang.Object[r4]
            r8[r9] = r0
            java.lang.String r0 = "AutoDeleteHintOnText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r8)
            r2.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.fire_on
            r0.setAnimation((int) r2, (int) r7, (int) r7)
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
            goto L_0x15b9
        L_0x10f6:
            r2 = 71
            if (r0 != r2) goto L_0x1129
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.AutoDeleteHintOffText
            java.lang.String r3 = "AutoDeleteHintOffText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.fire_off
            r0.setAnimation((int) r2, (int) r7, (int) r7)
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
            goto L_0x15b8
        L_0x1129:
            r2 = 45
            if (r0 != r2) goto L_0x1151
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.ImportMutualError
            java.lang.String r3 = "ImportMutualError"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.error
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x1082
        L_0x1151:
            r2 = 46
            if (r0 != r2) goto L_0x1179
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.ImportNotAdmin
            java.lang.String r3 = "ImportNotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.error
            r0.setAnimation((int) r2, (int) r7, (int) r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r0
            android.widget.TextView r0 = r1.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            goto L_0x1082
        L_0x1179:
            r2 = 47
            if (r0 != r2) goto L_0x11ac
            android.widget.TextView r0 = r1.infoTextView
            int r2 = org.telegram.messenger.R.string.ImportedInfo
            java.lang.String r3 = "ImportedInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r2 = org.telegram.messenger.R.raw.imported
            r0.setAnimation((int) r2, (int) r7, (int) r7)
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
            goto L_0x1082
        L_0x11ac:
            r2 = 1096810496(0x41600000, float:14.0)
            r8 = 51
            if (r0 != r8) goto L_0x11d4
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.AudioSpeedNormal
            java.lang.String r4 = "AudioSpeedNormal"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.audio_stop_speed
            r0.setAnimation((int) r3, (int) r7, (int) r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x15b8
        L_0x11d4:
            r8 = 50
            if (r0 != r8) goto L_0x11fa
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.AudioSpeedFast
            java.lang.String r4 = "AudioSpeedFast"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.audio_speed
            r0.setAnimation((int) r3, (int) r7, (int) r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x15b8
        L_0x11fa:
            r8 = 52
            if (r0 == r8) goto L_0x151f
            r8 = 56
            if (r0 == r8) goto L_0x151f
            r8 = 57
            if (r0 == r8) goto L_0x151f
            r8 = 58
            if (r0 == r8) goto L_0x151f
            r8 = 59
            if (r0 == r8) goto L_0x151f
            r8 = 60
            if (r0 == r8) goto L_0x151f
            r8 = 80
            if (r0 != r8) goto L_0x1218
            goto L_0x151f
        L_0x1218:
            r8 = 54
            if (r0 != r8) goto L_0x1240
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.ChannelNotifyMembersInfoOn
            java.lang.String r4 = "ChannelNotifyMembersInfoOn"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.silent_unmute
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x15b8
        L_0x1240:
            r8 = 55
            if (r0 != r8) goto L_0x1268
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.ChannelNotifyMembersInfoOff
            java.lang.String r4 = "ChannelNotifyMembersInfoOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.silent_mute
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            goto L_0x15b8
        L_0x1268:
            r8 = 41
            if (r0 != r8) goto L_0x1316
            if (r4 != 0) goto L_0x12e4
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x128d
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.InvLinkToSavedMessages
            java.lang.String r4 = "InvLinkToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1309
        L_0x128d:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r0 == 0) goto L_0x12bb
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.InvLinkToGroup
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r0 = r0.title
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1309
        L_0x12bb:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.InvLinkToUser
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x1309
        L_0x12e4:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.InvLinkToChats
            r8 = 1
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.Object[] r8 = new java.lang.Object[r9]
            java.lang.String r11 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r11, r0, r8)
            r10[r9] = r0
            java.lang.String r0 = "InvLinkToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r10)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x1309:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.contact_check
            r0.setAnimation((int) r3, (int) r7, (int) r7)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x15b8
        L_0x1316:
            r7 = 53
            if (r0 != r7) goto L_0x145a
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x13fe
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.clientUserId
            int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x1360
            int r0 = r0.intValue()
            r3 = 1
            if (r0 != r3) goto L_0x1344
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.FwdMessageToSavedMessages
            java.lang.String r4 = "FwdMessageToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1355
        L_0x1344:
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.FwdMessagesToSavedMessages
            java.lang.String r4 = "FwdMessagesToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x1355:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.saved_messages
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x1454
        L_0x1360:
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r3 == 0) goto L_0x13ac
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r10
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x1394
            android.widget.TextView r0 = r1.infoTextView
            int r5 = org.telegram.messenger.R.string.FwdMessageToGroup
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13f4
        L_0x1394:
            android.widget.TextView r0 = r1.infoTextView
            int r5 = org.telegram.messenger.R.string.FwdMessagesToGroup
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = r3.title
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13f4
        L_0x13ac:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x13db
            android.widget.TextView r0 = r1.infoTextView
            int r5 = org.telegram.messenger.R.string.FwdMessageToUser
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x13f4
        L_0x13db:
            android.widget.TextView r0 = r1.infoTextView
            int r5 = org.telegram.messenger.R.string.FwdMessagesToUser
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x13f4:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.forward
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x1451
        L_0x13fe:
            r3 = r4
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            int r0 = r0.intValue()
            r4 = 1
            if (r0 != r4) goto L_0x142a
            android.widget.TextView r0 = r1.infoTextView
            int r5 = org.telegram.messenger.R.string.FwdMessageToChats
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.Object[] r4 = new java.lang.Object[r9]
            java.lang.String r7 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3, r4)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            goto L_0x1448
        L_0x142a:
            android.widget.TextView r0 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.FwdMessagesToChats
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Object[] r5 = new java.lang.Object[r9]
            java.lang.String r7 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3, r5)
            r6[r9] = r3
            java.lang.String r3 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r6)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
        L_0x1448:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.forward
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
        L_0x1451:
            r3 = 300(0x12c, double:1.48E-321)
            r5 = r3
        L_0x1454:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x15b8
        L_0x145a:
            r7 = 61
            if (r0 != r7) goto L_0x15b8
            r0 = r3
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r4 != 0) goto L_0x14eb
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x148b
            android.widget.TextView r0 = r1.infoTextView
            int r3 = org.telegram.messenger.R.string.BackgroundToSavedMessages
            java.lang.String r4 = "BackgroundToSavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r0.setText(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.saved_messages
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x1519
        L_0x148b:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r10)
            if (r0 == 0) goto L_0x14b9
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.BackgroundToGroup
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r0 = r0.title
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            goto L_0x14e1
        L_0x14b9:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.BackgroundToUser
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
        L_0x14e1:
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.forward
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
            goto L_0x1519
        L_0x14eb:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.BackgroundToChats
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r10 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r10, r0, r7)
            r8[r9] = r0
            java.lang.String r0 = "BackgroundToChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r4, r8)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r1.leftImageView
            int r3 = org.telegram.messenger.R.raw.forward
            r4 = 30
            r0.setAnimation((int) r3, (int) r4, (int) r4)
        L_0x1519:
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            goto L_0x15b8
        L_0x151f:
            boolean r0 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r0 != 0) goto L_0x1526
            return
        L_0x1526:
            int r0 = org.telegram.messenger.R.raw.copy
            int r3 = r1.currentAction
            r4 = 80
            if (r3 != r4) goto L_0x153c
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.EmailCopied
            java.lang.String r7 = "EmailCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x15a5
        L_0x153c:
            r4 = 60
            if (r3 != r4) goto L_0x154e
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.PhoneCopied
            java.lang.String r7 = "PhoneCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x15a5
        L_0x154e:
            r4 = 56
            if (r3 != r4) goto L_0x1560
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.UsernameCopied
            java.lang.String r7 = "UsernameCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x15a5
        L_0x1560:
            r4 = 57
            if (r3 != r4) goto L_0x1572
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.HashtagCopied
            java.lang.String r7 = "HashtagCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x15a5
        L_0x1572:
            r4 = 52
            if (r3 != r4) goto L_0x1584
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.MessageCopied
            java.lang.String r7 = "MessageCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x15a5
        L_0x1584:
            r4 = 59
            if (r3 != r4) goto L_0x1598
            int r0 = org.telegram.messenger.R.raw.voip_invite
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.LinkCopied
            java.lang.String r7 = "LinkCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            goto L_0x15a5
        L_0x1598:
            android.widget.TextView r3 = r1.infoTextView
            int r4 = org.telegram.messenger.R.string.TextCopied
            java.lang.String r7 = "TextCopied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
        L_0x15a5:
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            r4 = 30
            r3.setAnimation((int) r0, (int) r4, (int) r4)
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.timeLeft = r3
            android.widget.TextView r0 = r1.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1
            r0.setTextSize(r4, r3)
        L_0x15b8:
            r0 = 0
        L_0x15b9:
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
            if (r7 <= 0) goto L_0x15f7
            org.telegram.ui.Components.RLottieImageView r3 = r1.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r4.<init>(r1)
            r3.postDelayed(r4, r5)
        L_0x15f7:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.widget.TextView r4 = r1.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r3.append(r4)
            android.widget.TextView r4 = r1.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x1624
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ". "
            r4.append(r5)
            android.widget.TextView r5 = r1.subinfoTextView
            java.lang.CharSequence r5 = r5.getText()
            r4.append(r5)
            java.lang.String r14 = r4.toString()
        L_0x1624:
            r3.append(r14)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r3)
            boolean r3 = r20.isMultilineSubInfo()
            if (r3 == 0) goto L_0x1679
            android.view.ViewParent r0 = r20.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x1644
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x1644:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.widget.TextView r2 = r1.subinfoTextView
            r3 = 1073741824(0x40000000, float:2.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            r3 = 0
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r9)
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
            goto L_0x171d
        L_0x1679:
            boolean r3 = r20.hasSubInfo()
            if (r3 == 0) goto L_0x1689
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.undoViewHeight = r0
            goto L_0x171d
        L_0x1689:
            android.view.ViewParent r3 = r20.getParent()
            boolean r3 = r3 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x171d
            android.view.ViewParent r3 = r20.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = r3.getMeasuredWidth()
            int r5 = r3.getPaddingLeft()
            int r4 = r4 - r5
            int r3 = r3.getPaddingRight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x16ab
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
        L_0x16ab:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r3
            android.widget.TextView r3 = r1.infoTextView
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            r5 = 0
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r9)
            r7 = 0
            r21 = r20
            r22 = r3
            r23 = r4
            r24 = r5
            r25 = r6
            r26 = r7
            r21.measureChildWithMargins(r22, r23, r24, r25, r26)
            android.widget.TextView r3 = r1.infoTextView
            int r3 = r3.getMeasuredHeight()
            int r4 = r1.currentAction
            r5 = 16
            if (r4 == r5) goto L_0x16e7
            r5 = 17
            if (r4 == r5) goto L_0x16e7
            r5 = 18
            if (r4 != r5) goto L_0x16e4
            goto L_0x16e7
        L_0x16e4:
            r8 = 1105199104(0x41e00000, float:28.0)
            goto L_0x16e9
        L_0x16e7:
            r8 = 1096810496(0x41600000, float:14.0)
        L_0x16e9:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 + r2
            r1.undoViewHeight = r3
            int r2 = r1.currentAction
            r4 = 18
            if (r2 != r4) goto L_0x1703
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x171d
        L_0x1703:
            r4 = 25
            if (r2 != r4) goto L_0x1714
            r0 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = java.lang.Math.max(r3, r0)
            r1.undoViewHeight = r0
            goto L_0x171d
        L_0x1714:
            if (r0 == 0) goto L_0x171d
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r0
            r1.undoViewHeight = r3
        L_0x171d:
            int r0 = r20.getVisibility()
            if (r0 == 0) goto L_0x177d
            r1.setVisibility(r9)
            boolean r0 = r1.fromTop
            if (r0 == 0) goto L_0x172d
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x172f
        L_0x172d:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x172f:
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
            if (r4 == 0) goto L_0x174c
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x174e
        L_0x174c:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x174e:
            int r6 = r1.enterOffsetMargin
            int r7 = r1.undoViewHeight
            int r6 = r6 + r7
            float r6 = (float) r6
            float r5 = r5 * r6
            r2[r9] = r5
            if (r4 == 0) goto L_0x175d
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x175f
        L_0x175d:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x175f:
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
        L_0x177d:
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
