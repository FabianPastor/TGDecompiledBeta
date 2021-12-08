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
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.PaymentFormActivity;

public class UndoView extends FrameLayout {
    public static final int ACTION_ADDED_TO_FOLDER = 20;
    public static final int ACTION_ARCHIVE = 2;
    public static final int ACTION_ARCHIVE_FEW = 4;
    public static final int ACTION_ARCHIVE_FEW_HINT = 5;
    public static final int ACTION_ARCHIVE_HIDDEN = 6;
    public static final int ACTION_ARCHIVE_HINT = 3;
    public static final int ACTION_ARCHIVE_PINNED = 7;
    public static final int ACTION_AUTO_DELETE_OFF = 71;
    public static final int ACTION_AUTO_DELETE_ON = 70;
    public static final int ACTION_CACHE_WAS_CLEARED = 19;
    public static final int ACTION_CHAT_UNARCHIVED = 23;
    public static final int ACTION_CLEAR = 0;
    public static final int ACTION_CLEAR_DATES = 81;
    public static final int ACTION_CLEAR_FEW = 26;
    public static final int ACTION_CONTACT_ADDED = 8;
    public static final int ACTION_DELETE = 1;
    public static final int ACTION_DELETE_FEW = 27;
    public static final int ACTION_DICE_INFO = 16;
    public static final int ACTION_DICE_NO_SEND_INFO = 17;
    public static final int ACTION_EMAIL_COPIED = 80;
    public static final int ACTION_FILTERS_AVAILABLE = 15;
    public static final int ACTION_FWD_MESSAGES = 53;
    public static final int ACTION_GIGAGROUP_CANCEL = 75;
    public static final int ACTION_GIGAGROUP_SUCCESS = 76;
    public static final int ACTION_HASHTAG_COPIED = 57;
    public static final int ACTION_IMPORT_GROUP_NOT_ADMIN = 46;
    public static final int ACTION_IMPORT_INFO = 47;
    public static final int ACTION_IMPORT_NOT_MUTUAL = 45;
    public static final int ACTION_LINK_COPIED = 59;
    public static final int ACTION_MESSAGE_COPIED = 52;
    public static final int ACTION_NOTIFY_OFF = 55;
    public static final int ACTION_NOTIFY_ON = 54;
    public static final int ACTION_OWNER_TRANSFERED_CHANNEL = 9;
    public static final int ACTION_OWNER_TRANSFERED_GROUP = 10;
    public static final int ACTION_PAYMENT_SUCCESS = 77;
    public static final int ACTION_PHONE_COPIED = 60;
    public static final int ACTION_PIN_DIALOGS = 78;
    public static final int ACTION_PLAYBACK_SPEED_DISABLED = 51;
    public static final int ACTION_PLAYBACK_SPEED_ENABLED = 50;
    public static final int ACTION_PROFILE_PHOTO_CHANGED = 22;
    public static final int ACTION_PROXIMITY_REMOVED = 25;
    public static final int ACTION_PROXIMITY_SET = 24;
    public static final int ACTION_QR_SESSION_ACCEPTED = 11;
    public static final int ACTION_QUIZ_CORRECT = 13;
    public static final int ACTION_QUIZ_INCORRECT = 14;
    public static final int ACTION_REMOVED_FROM_FOLDER = 21;
    public static final int ACTION_REPORT_SENT = 74;
    public static final int ACTION_SHARE_BACKGROUND = 61;
    public static final int ACTION_TEXT_COPIED = 58;
    public static final int ACTION_TEXT_INFO = 18;
    public static final int ACTION_THEME_CHANGED = 12;
    public static final int ACTION_UNPIN_DIALOGS = 79;
    public static final int ACTION_USERNAME_COPIED = 56;
    public static final int ACTION_VOIP_CAN_NOW_SPEAK = 38;
    public static final int ACTION_VOIP_INVITED = 34;
    public static final int ACTION_VOIP_INVITE_LINK_SENT = 41;
    public static final int ACTION_VOIP_LINK_COPIED = 33;
    public static final int ACTION_VOIP_MUTED = 30;
    public static final int ACTION_VOIP_MUTED_FOR_YOU = 35;
    public static final int ACTION_VOIP_RECORDING_FINISHED = 40;
    public static final int ACTION_VOIP_RECORDING_STARTED = 39;
    public static final int ACTION_VOIP_REMOVED = 32;
    public static final int ACTION_VOIP_SOUND_MUTED = 42;
    public static final int ACTION_VOIP_SOUND_UNMUTED = 43;
    public static final int ACTION_VOIP_UNMUTED = 31;
    public static final int ACTION_VOIP_UNMUTED_FOR_YOU = 36;
    public static final int ACTION_VOIP_USER_CHANGED = 37;
    public static final int ACTION_VOIP_USER_JOINED = 44;
    public static final int ACTION_VOIP_VIDEO_RECORDING_FINISHED = 101;
    public static final int ACTION_VOIP_VIDEO_RECORDING_STARTED = 100;
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
    int textWidthOut;
    StaticLayout timeLayout;
    StaticLayout timeLayoutOut;
    private long timeLeft;
    private String timeLeftString;
    float timeReplaceProgress;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    public class LinkMovementMethodMy extends LinkMovementMethod {
        public LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            CharacterStyle[] links;
            try {
                if (event.getAction() == 0 && ((links = (CharacterStyle[]) buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), CharacterStyle.class)) == null || links.length == 0)) {
                    return false;
                }
                if (event.getAction() != 1) {
                    return super.onTouchEvent(widget, buffer, event);
                }
                CharacterStyle[] links2 = (CharacterStyle[]) buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), CharacterStyle.class);
                if (links2 != null && links2.length > 0) {
                    UndoView.this.didPressUrl(links2[0]);
                }
                Selection.removeSelection(buffer);
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

    public UndoView(Context context, BaseFragment parent) {
        this(context, parent, false, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UndoView(Context context, BaseFragment parent, boolean top, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.currentAccount = UserConfig.selectedAccount;
        this.hideAnimationType = 1;
        this.timeReplaceProgress = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = parent;
        this.fromTop = top;
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
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new UndoView$$ExternalSyntheticLambda0(this));
        ImageView imageView = new ImageView(context2);
        this.undoImageView = imageView;
        imageView.setImageResource(NUM);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("undo_cancelColor"), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        TextView textView3 = new TextView(context2);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(getThemedColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m2703lambda$new$0$orgtelegramuiComponentsUndoView(View v) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
        return true;
    }

    public void setColors(int background, int text) {
        Theme.setDrawableColor(this.backgroundDrawable, background);
        this.infoTextView.setTextColor(text);
        this.subinfoTextView.setTextColor(text);
        this.leftImageView.setLayerColor("info1.**", background | -16777216);
        this.leftImageView.setLayerColor("info2.**", -16777216 | background);
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77 || i == 44 || i == 78 || i == 79 || i == 100 || i == 101;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || i == 74 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty());
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24 || i == 74;
    }

    public void setAdditionalTranslationY(float value) {
        if (this.additionalTranslationY != value) {
            this.additionalTranslationY = value;
            updatePosition();
        }
    }

    public Object getCurrentInfoObject() {
        return this.currentInfoObject;
    }

    public void hide(boolean apply, int animated) {
        if (getVisibility() == 0 && this.isShown) {
            this.currentInfoObject = null;
            this.isShown = false;
            Runnable runnable = this.currentActionRunnable;
            if (runnable != null) {
                if (apply) {
                    runnable.run();
                }
                this.currentActionRunnable = null;
            }
            Runnable runnable2 = this.currentCancelRunnable;
            if (runnable2 != null) {
                if (!apply) {
                    runnable2.run();
                }
                this.currentCancelRunnable = null;
            }
            int i = this.currentAction;
            if (i == 0 || i == 1 || i == 26 || i == 27) {
                for (int a = 0; a < this.currentDialogIds.size(); a++) {
                    long did = this.currentDialogIds.get(a).longValue();
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    int i2 = this.currentAction;
                    instance.removeDialogAction(did, i2 == 0 || i2 == 26, apply);
                    onRemoveDialogAction(did, this.currentAction);
                }
            }
            float f = -1.0f;
            if (animated != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (animated == 1) {
                    Animator[] animatorArr = new Animator[1];
                    float[] fArr = new float[1];
                    if (!this.fromTop) {
                        f = 1.0f;
                    }
                    fArr[0] = f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "enterOffset", fArr);
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(250);
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
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
            setEnterOffset(f * ((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)));
            setVisibility(4);
        }
    }

    /* access modifiers changed from: protected */
    public void onRemoveDialogAction(long currentDialogId, int action) {
    }

    public void didPressUrl(CharacterStyle span) {
    }

    public void showWithAction(long did, int action, Runnable actionRunnable) {
        showWithAction(did, action, (Object) null, (Object) null, actionRunnable, (Runnable) null);
    }

    public void showWithAction(long did, int action, Object infoObject) {
        showWithAction(did, action, infoObject, (Object) null, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long did, int action, Runnable actionRunnable, Runnable cancelRunnable) {
        showWithAction(did, action, (Object) null, (Object) null, actionRunnable, cancelRunnable);
    }

    public void showWithAction(long did, int action, Object infoObject, Runnable actionRunnable, Runnable cancelRunnable) {
        showWithAction(did, action, infoObject, (Object) null, actionRunnable, cancelRunnable);
    }

    public void showWithAction(long did, int action, Object infoObject, Object infoObject2, Runnable actionRunnable, Runnable cancelRunnable) {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(Long.valueOf(did));
        showWithAction(ids, action, infoObject, infoObject2, actionRunnable, cancelRunnable);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v64, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v66, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r8v42, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0901  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0940  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x094b  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x098d  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x17cb  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x17e3  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x17f4  */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x183c  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x18e3  */
    /* JADX WARNING: Removed duplicated region for block: B:622:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r34, int r35, java.lang.Object r36, java.lang.Object r37, java.lang.Runnable r38, java.lang.Runnable r39) {
        /*
            r33 = this;
            r7 = r33
            r8 = r34
            r9 = r35
            r10 = r36
            r11 = r37
            r12 = r38
            r13 = r39
            java.lang.Runnable r0 = r7.currentActionRunnable
            if (r0 == 0) goto L_0x0015
            r0.run()
        L_0x0015:
            r14 = 1
            r7.isShown = r14
            r7.currentActionRunnable = r12
            r7.currentCancelRunnable = r13
            r7.currentDialogIds = r8
            r15 = 0
            java.lang.Object r0 = r8.get(r15)
            java.lang.Long r0 = (java.lang.Long) r0
            long r5 = r0.longValue()
            r7.currentAction = r9
            r0 = 5000(0x1388, double:2.4703E-320)
            r7.timeLeft = r0
            r7.currentInfoObject = r10
            long r0 = android.os.SystemClock.elapsedRealtime()
            r7.lastUpdateTime = r0
            android.widget.TextView r0 = r7.undoTextView
            r1 = 2131628207(0x7f0e10af, float:1.88837E38)
            java.lang.String r2 = "Undo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.ImageView r0 = r7.undoImageView
            r0.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setPadding(r15, r15, r15, r15)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r14, r1)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r2 = 8
            r0.setVisibility(r2)
            android.widget.TextView r0 = r7.infoTextView
            r3 = 51
            r0.setGravity(r3)
            android.widget.TextView r0 = r7.infoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r4 = r0
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r0 = -2
            r4.height = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            r4.bottomMargin = r15
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r3 = r0
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r0 = 19
            r3.gravity = r0
            r3.bottomMargin = r15
            r3.topMargin = r15
            r0 = 1077936128(0x40400000, float:3.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3.leftMargin = r0
            r0 = 1113063424(0x42580000, float:54.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3.width = r0
            r0 = -2
            r3.height = r0
            android.widget.TextView r0 = r7.infoTextView
            r0.setMinHeight(r15)
            r17 = 0
            r1 = 0
            r19 = 0
            r0 = 0
            if (r12 != 0) goto L_0x00c4
            if (r13 != 0) goto L_0x00c4
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r2.<init>(r7)
            r7.setOnClickListener(r2)
            r7.setOnTouchListener(r0)
            goto L_0x00cc
        L_0x00c4:
            r7.setOnClickListener(r0)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r2 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r7.setOnTouchListener(r2)
        L_0x00cc:
            android.widget.TextView r2 = r7.infoTextView
            r2.setMovementMethod(r0)
            boolean r2 = r33.isTooltipAction()
            java.lang.String r14 = ""
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
            r24 = 1090519040(0x41000000, float:8.0)
            r25 = 1114112000(0x42680000, float:58.0)
            r27 = r1
            if (r2 == 0) goto L_0x09c4
            r2 = 36
            r16 = 0
            r15 = 74
            if (r9 != r15) goto L_0x0117
            android.widget.TextView r0 = r7.subinfoTextView
            r1 = 0
            r0.setSingleLine(r1)
            r0 = 2131627486(0x7f0e0dde, float:1.8882238E38)
            java.lang.String r15 = "ReportChatSent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            r15 = 2131627495(0x7f0e0de7, float:1.8882256E38)
            r21 = r0
            java.lang.Object[] r0 = new java.lang.Object[r1]
            java.lang.String r1 = "ReportSentInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r15, r0)
            r1 = 2131558450(0x7f0d0032, float:1.8742216E38)
            r15 = r0
            r28 = r1
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            r12 = r2
            r1 = r15
            r0 = r21
            r2 = r28
            goto L_0x08fa
        L_0x0117:
            r15 = 34
            if (r9 != r15) goto L_0x018f
            r15 = r10
            org.telegram.tgnet.TLRPC$User r15 = (org.telegram.tgnet.TLRPC.User) r15
            r21 = r11
            org.telegram.tgnet.TLRPC$Chat r21 = (org.telegram.tgnet.TLRPC.Chat) r21
            boolean r28 = org.telegram.messenger.ChatObject.isChannelOrGiga(r21)
            if (r28 == 0) goto L_0x0143
            r1 = 1
            java.lang.Object[] r0 = new java.lang.Object[r1]
            java.lang.String r28 = org.telegram.messenger.UserObject.getFirstName(r15)
            r23 = 0
            r0[r23] = r28
            java.lang.String r1 = "VoipChannelInvitedUser"
            r31 = r2
            r2 = 2131628432(0x7f0e1190, float:1.8884157E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r2, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x015d
        L_0x0143:
            r31 = r2
            r23 = 0
            r0 = 2131628519(0x7f0e11e7, float:1.8884333E38)
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r15)
            r2[r23] = r1
            java.lang.String r1 = "VoipGroupInvitedUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x015d:
            r1 = 0
            r2 = 0
            org.telegram.ui.Components.AvatarDrawable r28 = new org.telegram.ui.Components.AvatarDrawable
            r28.<init>()
            r29 = r28
            r28 = 1094713344(0x41400000, float:12.0)
            r30 = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r28)
            r28 = r1
            r1 = r29
            r1.setTextSize(r0)
            r1.setInfo((org.telegram.tgnet.TLRPC.User) r15)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r0.setForUserOrChat(r15, r1)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r1 = 0
            r0.setVisibility(r1)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r28
            r0 = r30
            r12 = r31
            goto L_0x08fa
        L_0x018f:
            r31 = r2
            r0 = 44
            if (r9 != r0) goto L_0x0241
            r0 = r11
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x01d7
            r1 = r10
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r2 == 0) goto L_0x01be
            r15 = 1
            java.lang.Object[] r2 = new java.lang.Object[r15]
            java.lang.String r21 = org.telegram.messenger.UserObject.getFirstName(r1)
            r23 = 0
            r2[r23] = r21
            java.lang.String r15 = "VoipChannelUserJoined"
            r12 = 2131628456(0x7f0e11a8, float:1.8884205E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r15, r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x01d6
        L_0x01be:
            r23 = 0
            r2 = 2131628471(0x7f0e11b7, float:1.8884236E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r1)
            r15[r23] = r12
            java.lang.String r12 = "VoipChatUserJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r15)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x01d6:
            goto L_0x020d
        L_0x01d7:
            r1 = r10
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r2 == 0) goto L_0x01f7
            r2 = 2131628426(0x7f0e118a, float:1.8884144E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = r1.title
            r21 = 0
            r15[r21] = r12
            java.lang.String r12 = "VoipChannelChatJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r15)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x020d
        L_0x01f7:
            r21 = 0
            r2 = 2131628461(0x7f0e11ad, float:1.8884215E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = r1.title
            r15[r21] = r12
            java.lang.String r12 = "VoipChatChatJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r15)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x020d:
            r1 = 0
            r12 = 0
            org.telegram.ui.Components.AvatarDrawable r15 = new org.telegram.ui.Components.AvatarDrawable
            r15.<init>()
            r21 = 1094713344(0x41400000, float:12.0)
            r28 = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r15.setTextSize(r0)
            r0 = r10
            org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
            r15.setInfo((org.telegram.tgnet.TLObject) r0)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r21 = r1
            r1 = r10
            org.telegram.tgnet.TLObject r1 = (org.telegram.tgnet.TLObject) r1
            r0.setForUserOrChat(r1, r15)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r1 = 0
            r0.setVisibility(r1)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r2
            r2 = r12
            r1 = r21
            r12 = r31
            goto L_0x08fa
        L_0x0241:
            r0 = 37
            if (r9 != r0) goto L_0x02c5
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setTextSize(r1)
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x026b
            r1 = r10
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            r0.setInfo((org.telegram.tgnet.TLRPC.User) r1)
            org.telegram.ui.Components.BackupImageView r2 = r7.avatarImageView
            r2.setForUserOrChat(r1, r0)
            java.lang.String r2 = r1.first_name
            java.lang.String r12 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r2, r12)
            goto L_0x0279
        L_0x026b:
            r1 = r10
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r1)
            org.telegram.ui.Components.BackupImageView r2 = r7.avatarImageView
            r2.setForUserOrChat(r1, r0)
            java.lang.String r2 = r1.title
            r1 = r2
        L_0x0279:
            r2 = r11
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            boolean r12 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r12 == 0) goto L_0x0298
            r15 = 1
            java.lang.Object[] r12 = new java.lang.Object[r15]
            r15 = 0
            r12[r15] = r1
            java.lang.String r15 = "VoipChannelUserChanged"
            r21 = r0
            r0 = 2131628455(0x7f0e11a7, float:1.8884203E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r15, r0, r12)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x02ad
        L_0x0298:
            r21 = r0
            r0 = 2131628580(0x7f0e1224, float:1.8884457E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            r12 = 0
            r15[r12] = r1
            java.lang.String r12 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r0, r15)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x02ad:
            r12 = 0
            r15 = 0
            r28 = r0
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r29 = r1
            r1 = 0
            r0.setVisibility(r1)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r12
            r2 = r15
            r0 = r28
            r12 = r31
            goto L_0x08fa
        L_0x02c5:
            r0 = 33
            if (r9 != r0) goto L_0x02e2
            r0 = 2131628502(0x7f0e11d6, float:1.8884298E38)
            java.lang.String r1 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = 0
            r2 = 2131558542(0x7f0d008e, float:1.8742403E38)
            r12 = r0
            r15 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            r12 = r31
            goto L_0x08fa
        L_0x02e2:
            r0 = 77
            if (r9 != r0) goto L_0x031c
            r0 = r10
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r1 = 0
            r2 = 2131558474(0x7f0d004a, float:1.8742265E38)
            r12 = r0
            r15 = r1
            r0 = 5000(0x1388, double:2.4703E-320)
            r7.timeLeft = r0
            org.telegram.ui.ActionBar.BaseFragment r0 = r7.parentFragment
            if (r0 == 0) goto L_0x0312
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.Message
            if (r0 == 0) goto L_0x0312
            r0 = r11
            org.telegram.tgnet.TLRPC$Message r0 = (org.telegram.tgnet.TLRPC.Message) r0
            r1 = 0
            r7.setOnTouchListener(r1)
            r21 = r2
            android.widget.TextView r2 = r7.infoTextView
            r2.setMovementMethod(r1)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r1.<init>(r7, r0)
            r7.setOnClickListener(r1)
            goto L_0x0314
        L_0x0312:
            r21 = r2
        L_0x0314:
            r0 = r12
            r1 = r15
            r2 = r21
            r12 = r31
            goto L_0x08fa
        L_0x031c:
            r0 = 30
            if (r9 != r0) goto L_0x0358
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x032c
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0332
        L_0x032c:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x0332:
            r1 = 2131628578(0x7f0e1222, float:1.8884453E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            r2 = 0
            r12[r2] = r0
            java.lang.String r2 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = 0
            r12 = 2131558543(0x7f0d008f, float:1.8742405E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x0358:
            r0 = 35
            if (r9 != r0) goto L_0x039a
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x0368
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0374
        L_0x0368:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x0372
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x0374
        L_0x0372:
            java.lang.String r0 = ""
        L_0x0374:
            r1 = 2131628579(0x7f0e1223, float:1.8884455E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            r2 = 0
            r12[r2] = r0
            java.lang.String r2 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = 0
            r12 = 2131558543(0x7f0d008f, float:1.8742405E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x039a:
            r0 = 31
            if (r9 != r0) goto L_0x03d6
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x03aa
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03b0
        L_0x03aa:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x03b0:
            r1 = 2131628576(0x7f0e1220, float:1.8884449E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            r2 = 0
            r12[r2] = r0
            java.lang.String r2 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = 0
            r12 = 2131558549(0x7f0d0095, float:1.8742417E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x03d6:
            r0 = 38
            if (r9 != r0) goto L_0x0414
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x03f7
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            r1 = 2131628588(0x7f0e122c, float:1.8884473E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            java.lang.String r2 = r0.title
            r15 = 0
            r12[r15] = r2
            java.lang.String r2 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0404
        L_0x03f7:
            r0 = 2131628587(0x7f0e122b, float:1.888447E38)
            java.lang.String r1 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0404:
            r1 = 0
            r2 = 2131558535(0x7f0d0087, float:1.8742389E38)
            r12 = r0
            r15 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            r12 = r31
            goto L_0x08fa
        L_0x0414:
            r0 = 42
            if (r9 != r0) goto L_0x044f
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r1 == 0) goto L_0x042f
            r1 = 2131628447(0x7f0e119f, float:1.8884187E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x043c
        L_0x042f:
            r1 = 2131628558(0x7f0e120e, float:1.8884412E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x043c:
            r2 = 0
            r12 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x044f:
            r0 = 43
            if (r9 != r0) goto L_0x048a
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r1 == 0) goto L_0x046a
            r1 = 2131628448(0x7f0e11a0, float:1.8884189E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x0477
        L_0x046a:
            r1 = 2131628559(0x7f0e120f, float:1.8884414E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x0477:
            r2 = 0
            r12 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x048a:
            int r0 = r7.currentAction
            r1 = 39
            if (r0 == r1) goto L_0x08d9
            r1 = 100
            if (r0 != r1) goto L_0x0496
            goto L_0x08d9
        L_0x0496:
            r1 = 40
            if (r0 == r1) goto L_0x084b
            r1 = 101(0x65, float:1.42E-43)
            if (r0 != r1) goto L_0x04a0
            goto L_0x084b
        L_0x04a0:
            r1 = 36
            if (r9 != r1) goto L_0x04dc
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x04b0
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x04b6
        L_0x04b0:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x04b6:
            r1 = 2131628577(0x7f0e1221, float:1.888445E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            r2 = 0
            r12[r2] = r0
            java.lang.String r2 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = 0
            r12 = 2131558549(0x7f0d0095, float:1.8742417E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x04dc:
            r1 = 32
            if (r9 != r1) goto L_0x0518
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x04ec
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x04f2
        L_0x04ec:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x04f2:
            r1 = 2131628549(0x7f0e1205, float:1.8884394E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            r2 = 0
            r12[r2] = r0
            java.lang.String r2 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = 0
            r12 = 2131558541(0x7f0d008d, float:1.87424E38)
            r15 = r0
            r21 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r2
            r2 = r12
            r0 = r21
            r12 = r31
            goto L_0x08fa
        L_0x0518:
            r1 = 9
            if (r9 == r1) goto L_0x0807
            r1 = 10
            if (r9 != r1) goto L_0x0522
            goto L_0x0807
        L_0x0522:
            r1 = 8
            if (r9 != r1) goto L_0x054a
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = 2131626749(0x7f0e0afd, float:1.8880743E38)
            r2 = 1
            java.lang.Object[] r12 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r15 = 0
            r12[r15] = r2
            java.lang.String r2 = "NowInContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r12)
            r2 = 0
            r0 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r12 = r31
            r32 = r2
            r2 = r0
            r0 = r1
            r1 = r32
            goto L_0x08fa
        L_0x054a:
            r1 = 22
            if (r9 != r1) goto L_0x05ba
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r5)
            if (r0 == 0) goto L_0x056a
            if (r10 != 0) goto L_0x0560
            r0 = 2131626216(0x7f0e08e8, float:1.8879662E38)
            java.lang.String r1 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x05b2
        L_0x0560:
            r0 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            java.lang.String r1 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x05b2
        L_0x056a:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x059b
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x059b
            if (r10 != 0) goto L_0x0590
            r1 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0 = r1
            goto L_0x05b2
        L_0x0590:
            r1 = 2131626213(0x7f0e08e5, float:1.8879656E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0 = r1
            goto L_0x05b2
        L_0x059b:
            if (r10 != 0) goto L_0x05a8
            r1 = 2131626214(0x7f0e08e6, float:1.8879658E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0 = r1
            goto L_0x05b2
        L_0x05a8:
            r1 = 2131626215(0x7f0e08e7, float:1.887966E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0 = r1
        L_0x05b2:
            r1 = 0
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r12 = r31
            goto L_0x08fa
        L_0x05ba:
            r1 = 23
            if (r9 != r1) goto L_0x05cf
            r0 = 2131624901(0x7f0e03c5, float:1.8876995E38)
            java.lang.String r1 = "ChatWasMovedToMainList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = 0
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r12 = r31
            goto L_0x08fa
        L_0x05cf:
            r1 = 6
            if (r9 != r1) goto L_0x05eb
            r0 = 2131624320(0x7f0e0180, float:1.8875816E38)
            java.lang.String r1 = "ArchiveHidden"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = 2131624321(0x7f0e0181, float:1.8875818E38)
            java.lang.String r2 = "ArchiveHiddenInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r12 = 48
            goto L_0x08fa
        L_0x05eb:
            r1 = 13
            if (r0 != r1) goto L_0x0608
            r0 = 2131627409(0x7f0e0d91, float:1.8882082E38)
            java.lang.String r1 = "QuizWellDone"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = 2131627410(0x7f0e0d92, float:1.8882084E38)
            java.lang.String r2 = "QuizWellDoneInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131558551(0x7f0d0097, float:1.874242E38)
            r12 = 44
            goto L_0x08fa
        L_0x0608:
            r1 = 14
            if (r0 != r1) goto L_0x0625
            r0 = 2131627411(0x7f0e0d93, float:1.8882086E38)
            java.lang.String r1 = "QuizWrongAnswer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = 2131627412(0x7f0e0d94, float:1.8882088E38)
            java.lang.String r2 = "QuizWrongAnswerInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131558553(0x7f0d0099, float:1.8742425E38)
            r12 = 44
            goto L_0x08fa
        L_0x0625:
            r0 = 7
            if (r9 != r0) goto L_0x0651
            r0 = 2131624328(0x7f0e0188, float:1.8875833E38)
            java.lang.String r1 = "ArchivePinned"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0649
            r1 = 2131624329(0x7f0e0189, float:1.8875835E38)
            java.lang.String r2 = "ArchivePinnedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x064a
        L_0x0649:
            r1 = 0
        L_0x064a:
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r12 = r31
            goto L_0x08fa
        L_0x0651:
            r0 = 20
            if (r9 == r0) goto L_0x06d9
            r1 = 21
            if (r9 != r1) goto L_0x065b
            goto L_0x06d9
        L_0x065b:
            r0 = 19
            if (r9 != r0) goto L_0x0669
            java.lang.CharSequence r0 = r7.infoText
            r1 = 0
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r12 = r31
            goto L_0x08fa
        L_0x0669:
            r0 = 78
            if (r9 == r0) goto L_0x06a8
            r0 = 79
            if (r9 != r0) goto L_0x0672
            goto L_0x06a8
        L_0x0672:
            r0 = 3
            if (r9 != r0) goto L_0x067f
            r0 = 2131624860(0x7f0e039c, float:1.8876912E38)
            java.lang.String r1 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0688
        L_0x067f:
            r0 = 2131624914(0x7f0e03d2, float:1.8877021E38)
            java.lang.String r1 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x0688:
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x06a0
            r1 = 2131624861(0x7f0e039d, float:1.8876914E38)
            java.lang.String r2 = "ChatArchivedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x06a1
        L_0x06a0:
            r1 = 0
        L_0x06a1:
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r12 = r31
            goto L_0x08fa
        L_0x06a8:
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 78
            if (r9 != r1) goto L_0x06ba
            java.lang.String r1 = "PinnedDialogsCount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
            goto L_0x06c0
        L_0x06ba:
            java.lang.String r1 = "UnpinnedDialogsCount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
        L_0x06c0:
            r2 = 0
            int r12 = r7.currentAction
            r15 = 78
            if (r12 != r15) goto L_0x06cb
            r12 = 2131558455(0x7f0d0037, float:1.8742226E38)
            goto L_0x06ce
        L_0x06cb:
            r12 = 2131558460(0x7f0d003c, float:1.8742236E38)
        L_0x06ce:
            r0 = r12
            r12 = r31
            r32 = r2
            r2 = r0
            r0 = r1
            r1 = r32
            goto L_0x08fa
        L_0x06d9:
            r1 = r11
            org.telegram.messenger.MessagesController$DialogFilter r1 = (org.telegram.messenger.MessagesController.DialogFilter) r1
            r28 = 0
            int r2 = (r5 > r28 ? 1 : (r5 == r28 ? 0 : -1))
            if (r2 == 0) goto L_0x07a7
            r28 = r5
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            if (r2 == 0) goto L_0x0700
            int r2 = r7.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r12 = org.telegram.messenger.DialogObject.getEncryptedChatId(r28)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r12)
            r15 = r1
            long r0 = r2.user_id
            goto L_0x0703
        L_0x0700:
            r15 = r1
            r0 = r28
        L_0x0703:
            boolean r2 = org.telegram.messenger.DialogObject.isUserDialog(r0)
            if (r2 == 0) goto L_0x0759
            int r2 = r7.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Long r12 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r12)
            r12 = 20
            if (r9 != r12) goto L_0x073a
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r26 = org.telegram.messenger.UserObject.getFirstName(r2)
            r23 = 0
            r13[r23] = r26
            java.lang.String r12 = r15.name
            r20 = 1
            r13[r20] = r12
            java.lang.String r12 = "FilterUserAddedToExisting"
            r11 = 2131625670(0x7f0e06c6, float:1.8878555E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r12, r11, r13)
            android.text.SpannableStringBuilder r11 = org.telegram.messenger.AndroidUtilities.replaceTags(r11)
            goto L_0x0758
        L_0x073a:
            r20 = 1
            r23 = 0
            r11 = 2131625671(0x7f0e06c7, float:1.8878557E38)
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r2)
            r13[r23] = r12
            java.lang.String r12 = r15.name
            r13[r20] = r12
            java.lang.String r12 = "FilterUserRemovedFrom"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r12, r11, r13)
            android.text.SpannableStringBuilder r11 = org.telegram.messenger.AndroidUtilities.replaceTags(r11)
        L_0x0758:
            goto L_0x07a5
        L_0x0759:
            int r2 = r7.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r11 = -r0
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r11)
            r11 = 20
            if (r9 != r11) goto L_0x0789
            r11 = 2131625609(0x7f0e0689, float:1.887843E38)
            r13 = 2
            java.lang.Object[] r12 = new java.lang.Object[r13]
            java.lang.String r13 = r2.title
            r23 = 0
            r12[r23] = r13
            java.lang.String r13 = r15.name
            r20 = 1
            r12[r20] = r13
            java.lang.String r13 = "FilterChatAddedToExisting"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r13, r11, r12)
            android.text.SpannableStringBuilder r11 = org.telegram.messenger.AndroidUtilities.replaceTags(r11)
            goto L_0x07a5
        L_0x0789:
            r20 = 1
            r23 = 0
            r11 = 2131625610(0x7f0e068a, float:1.8878433E38)
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = r2.title
            r13[r23] = r12
            java.lang.String r12 = r15.name
            r13[r20] = r12
            java.lang.String r12 = "FilterChatRemovedFrom"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r12, r11, r13)
            android.text.SpannableStringBuilder r11 = org.telegram.messenger.AndroidUtilities.replaceTags(r11)
        L_0x07a5:
            r0 = r11
            goto L_0x07f7
        L_0x07a7:
            r15 = r1
            r0 = 20
            if (r9 != r0) goto L_0x07d2
            r0 = 2131625613(0x7f0e068d, float:1.8878439E38)
            r1 = 2
            java.lang.Object[] r2 = new java.lang.Object[r1]
            r1 = r10
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            java.lang.String r11 = "ChatsSelected"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1)
            r11 = 0
            r2[r11] = r1
            java.lang.String r1 = r15.name
            r11 = 1
            r2[r11] = r1
            java.lang.String r1 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x07f7
        L_0x07d2:
            r0 = 2131625614(0x7f0e068e, float:1.887844E38)
            r1 = 2
            java.lang.Object[] r2 = new java.lang.Object[r1]
            r1 = r10
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            java.lang.String r11 = "ChatsSelected"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1)
            r11 = 0
            r2[r11] = r1
            java.lang.String r1 = r15.name
            r11 = 1
            r2[r11] = r1
            java.lang.String r1 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x07f7:
            r1 = 0
            r2 = 20
            if (r9 != r2) goto L_0x0800
            r2 = 2131558442(0x7f0d002a, float:1.87422E38)
            goto L_0x0803
        L_0x0800:
            r2 = 2131558443(0x7f0d002b, float:1.8742202E38)
        L_0x0803:
            r12 = r31
            goto L_0x08fa
        L_0x0807:
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = 9
            if (r9 != r1) goto L_0x0826
            r1 = 2131625352(0x7f0e0588, float:1.887791E38)
            r2 = 1
            java.lang.Object[] r11 = new java.lang.Object[r2]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r0)
            r13 = 0
            r11[r13] = r12
            java.lang.String r12 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r1, r11)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            goto L_0x083d
        L_0x0826:
            r2 = 1
            r13 = 0
            r1 = 2131625353(0x7f0e0589, float:1.8877912E38)
            java.lang.Object[] r11 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r11[r13] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r11)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
        L_0x083d:
            r2 = 0
            r0 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r12 = r31
            r32 = r2
            r2 = r0
            r0 = r1
            r1 = r32
            goto L_0x08fa
        L_0x084b:
            r1 = 40
            if (r0 != r1) goto L_0x0855
            r0 = 2131628492(0x7f0e11cc, float:1.8884278E38)
            java.lang.String r1 = "VoipGroupAudioRecordSaved"
            goto L_0x085a
        L_0x0855:
            r0 = 2131628582(0x7f0e1226, float:1.888446E38)
            java.lang.String r1 = "VoipGroupVideoRecordSaved"
        L_0x085a:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = r0
            r2 = 0
            r11 = 2131558545(0x7f0d0091, float:1.8742409E38)
            r12 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r12
            android.widget.TextView r0 = r7.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r12 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r12.<init>()
            r0.setMovementMethod(r12)
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r1)
            r12 = r0
            java.lang.String r0 = "**"
            int r13 = r1.indexOf(r0)
            java.lang.String r0 = "**"
            int r15 = r1.lastIndexOf(r0)
            if (r13 < 0) goto L_0x08ce
            if (r15 < 0) goto L_0x08ce
            if (r13 == r15) goto L_0x08ce
            int r0 = r15 + 2
            r12.replace(r15, r0, r14)
            int r0 = r13 + 2
            r12.replace(r13, r0, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r0 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x08c5 }
            r21 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x08c1 }
            r1.<init>()     // Catch:{ Exception -> 0x08c1 }
            r28 = r2
            java.lang.String r2 = "tg://openmessage?user_id="
            r1.append(r2)     // Catch:{ Exception -> 0x08bf }
            int r2 = r7.currentAccount     // Catch:{ Exception -> 0x08bf }
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x08bf }
            long r8 = r2.getClientUserId()     // Catch:{ Exception -> 0x08bf }
            r1.append(r8)     // Catch:{ Exception -> 0x08bf }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x08bf }
            r0.<init>(r1)     // Catch:{ Exception -> 0x08bf }
            int r1 = r15 + -2
            r2 = 33
            r12.setSpan(r0, r13, r1, r2)     // Catch:{ Exception -> 0x08bf }
            goto L_0x08d2
        L_0x08bf:
            r0 = move-exception
            goto L_0x08ca
        L_0x08c1:
            r0 = move-exception
            r28 = r2
            goto L_0x08ca
        L_0x08c5:
            r0 = move-exception
            r21 = r1
            r28 = r2
        L_0x08ca:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x08d2
        L_0x08ce:
            r21 = r1
            r28 = r2
        L_0x08d2:
            r0 = r12
            r2 = r11
            r1 = r28
            r12 = r31
            goto L_0x08fa
        L_0x08d9:
            r1 = 39
            if (r0 != r1) goto L_0x08e3
            r0 = 2131628493(0x7f0e11cd, float:1.888428E38)
            java.lang.String r1 = "VoipGroupAudioRecordStarted"
            goto L_0x08e8
        L_0x08e3:
            r0 = 2131628583(0x7f0e1227, float:1.8884463E38)
            java.lang.String r1 = "VoipGroupVideoRecordStarted"
        L_0x08e8:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r1 = 0
            r2 = 2131558546(0x7f0d0092, float:1.874241E38)
            r8 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r8
            r12 = r31
        L_0x08fa:
            android.widget.TextView r8 = r7.infoTextView
            r8.setText(r0)
            if (r2 == 0) goto L_0x0940
            if (r16 == 0) goto L_0x090b
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            r8.setImageResource(r2)
            r9 = r27
            goto L_0x0927
        L_0x090b:
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            r8.setAnimation(r2, r12, r12)
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            org.telegram.ui.Components.RLottieDrawable r8 = r8.getAnimatedDrawable()
            r9 = r27
            r8.setPlayInDirectionOfCustomEndFrame(r9)
            if (r9 == 0) goto L_0x0920
            r11 = r19
            goto L_0x0924
        L_0x0920:
            int r11 = r8.getFramesCount()
        L_0x0924:
            r8.setCustomEndFrame(r11)
        L_0x0927:
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            r11 = 0
            r8.setVisibility(r11)
            if (r16 != 0) goto L_0x0949
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            if (r9 == 0) goto L_0x0936
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0937
        L_0x0936:
            r15 = 0
        L_0x0937:
            r8.setProgress(r15)
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            r8.playAnimation()
            goto L_0x0949
        L_0x0940:
            r9 = r27
            org.telegram.ui.Components.RLottieImageView r8 = r7.leftImageView
            r11 = 8
            r8.setVisibility(r11)
        L_0x0949:
            if (r1 == 0) goto L_0x098d
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r4.rightMargin = r8
            android.widget.TextView r8 = r7.subinfoTextView
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            r4 = r8
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r4.rightMargin = r8
            android.widget.TextView r8 = r7.subinfoTextView
            r8.setText(r1)
            android.widget.TextView r8 = r7.subinfoTextView
            r11 = 0
            r8.setVisibility(r11)
            android.widget.TextView r8 = r7.infoTextView
            r11 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r8.setTextSize(r13, r11)
            android.widget.TextView r8 = r7.infoTextView
            java.lang.String r11 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r8.setTypeface(r11)
            r11 = 8
            goto L_0x09b7
        L_0x098d:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r8
            r8 = 1095761920(0x41500000, float:13.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r4.topMargin = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r4.rightMargin = r8
            android.widget.TextView r8 = r7.subinfoTextView
            r11 = 8
            r8.setVisibility(r11)
            android.widget.TextView r8 = r7.infoTextView
            r13 = 1097859072(0x41700000, float:15.0)
            r15 = 1
            r8.setTextSize(r15, r13)
            android.widget.TextView r8 = r7.infoTextView
            android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
            r8.setTypeface(r13)
        L_0x09b7:
            android.widget.LinearLayout r8 = r7.undoButton
            r8.setVisibility(r11)
            r8 = r34
            r11 = r35
            r0 = r4
            r2 = r14
            goto L_0x17b5
        L_0x09c4:
            r9 = r27
            int r0 = r7.currentAction
            r1 = 45
            r2 = 60
            if (r0 == r1) goto L_0x112a
            r1 = 46
            if (r0 == r1) goto L_0x112a
            r1 = 47
            if (r0 == r1) goto L_0x112a
            r1 = 51
            if (r0 == r1) goto L_0x112a
            r1 = 50
            if (r0 == r1) goto L_0x112a
            r1 = 52
            if (r0 == r1) goto L_0x112a
            r1 = 53
            if (r0 == r1) goto L_0x112a
            r1 = 54
            if (r0 == r1) goto L_0x112a
            r1 = 55
            if (r0 == r1) goto L_0x112a
            r1 = 56
            if (r0 == r1) goto L_0x112a
            r1 = 57
            if (r0 == r1) goto L_0x112a
            r1 = 58
            if (r0 == r1) goto L_0x112a
            r1 = 59
            if (r0 == r1) goto L_0x112a
            if (r0 == r2) goto L_0x112a
            r1 = 71
            if (r0 == r1) goto L_0x112a
            r1 = 70
            if (r0 == r1) goto L_0x112a
            r1 = 75
            if (r0 == r1) goto L_0x112a
            r1 = 76
            if (r0 == r1) goto L_0x112a
            r1 = 41
            if (r0 == r1) goto L_0x112a
            r1 = 78
            if (r0 == r1) goto L_0x112a
            r1 = 79
            if (r0 == r1) goto L_0x112a
            r1 = 61
            if (r0 == r1) goto L_0x112a
            r1 = 80
            if (r0 != r1) goto L_0x0a2a
            r8 = r34
            r11 = r35
            goto L_0x112e
        L_0x0a2a:
            r1 = 24
            if (r0 == r1) goto L_0x0fb3
            r1 = 25
            if (r0 != r1) goto L_0x0a38
            r8 = r34
            r11 = r35
            goto L_0x0fb7
        L_0x0a38:
            r1 = 11
            if (r0 != r1) goto L_0x0ab1
            r0 = r10
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC.TL_authorization) r0
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131624451(0x7f0e0203, float:1.8876082E38)
            java.lang.String r8 = "AuthAnotherClientOk"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r8 = 36
            r1.setAnimation(r2, r8, r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r1
            android.widget.TextView r1 = r7.subinfoTextView
            java.lang.String r2 = r0.app_name
            r1.setText(r2)
            android.widget.TextView r1 = r7.subinfoTextView
            r2 = 0
            r1.setVisibility(r2)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r1.setTextSize(r8, r2)
            android.widget.TextView r1 = r7.infoTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r7.undoTextView
            java.lang.String r2 = "windowBackgroundWhiteRedText2"
            int r2 = r7.getThemedColor(r2)
            r1.setTextColor(r2)
            android.widget.ImageView r1 = r7.undoImageView
            r2 = 8
            r1.setVisibility(r2)
            android.widget.LinearLayout r1 = r7.undoButton
            r2 = 0
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r1.playAnimation()
            r8 = r34
            r11 = r35
            goto L_0x1126
        L_0x0ab1:
            r1 = 15
            if (r0 != r1) goto L_0x0b8d
            r0 = 10000(0x2710, double:4.9407E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.undoTextView
            r1 = 2131626766(0x7f0e0b0e, float:1.8880777E38)
            java.lang.String r2 = "Open"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625606(0x7f0e0686, float:1.8878425E38)
            java.lang.String r2 = "FilterAvailableTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558436(0x7f0d0024, float:1.8742188E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            android.widget.TextView r0 = r7.undoTextView
            android.text.TextPaint r0 = r0.getPaint()
            android.widget.TextView r1 = r7.undoTextView
            java.lang.CharSequence r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            float r0 = r0.measureText(r1)
            double r0 = (double) r0
            double r0 = java.lang.Math.ceil(r0)
            int r0 = (int) r0
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r1
            r4.rightMargin = r0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r1
            android.widget.TextView r1 = r7.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            r4 = r1
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r4.rightMargin = r0
            r1 = 2131625605(0x7f0e0685, float:1.8878423E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r8 = 42
            int r11 = r1.indexOf(r8)
            int r8 = r1.lastIndexOf(r8)
            if (r11 < 0) goto L_0x0b53
            if (r8 < 0) goto L_0x0b53
            if (r11 == r8) goto L_0x0b53
            int r12 = r8 + 1
            r2.replace(r8, r12, r14)
            int r12 = r11 + 1
            r2.replace(r11, r12, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r12 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r13 = "tg://settings/folders"
            r12.<init>(r13)
            int r13 = r8 + -1
            r15 = 33
            r2.setSpan(r12, r11, r13, r15)
        L_0x0b53:
            android.widget.TextView r12 = r7.subinfoTextView
            r12.setText(r2)
            android.widget.TextView r12 = r7.subinfoTextView
            r13 = 0
            r12.setVisibility(r13)
            android.widget.TextView r12 = r7.subinfoTextView
            r12.setSingleLine(r13)
            android.widget.TextView r12 = r7.subinfoTextView
            r15 = 2
            r12.setMaxLines(r15)
            android.widget.LinearLayout r12 = r7.undoButton
            r12.setVisibility(r13)
            android.widget.ImageView r12 = r7.undoImageView
            r15 = 8
            r12.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.setVisibility(r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r13 = 0
            r12.setProgress(r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.playAnimation()
            r8 = r34
            r11 = r35
            r0 = r4
            r2 = r14
            goto L_0x17b5
        L_0x0b8d:
            r1 = 16
            if (r0 == r1) goto L_0x0e31
            r1 = 17
            if (r0 != r1) goto L_0x0b9b
            r8 = r34
            r11 = r35
            goto L_0x0e35
        L_0x0b9b:
            r1 = 18
            if (r0 != r1) goto L_0x0c2d
            r0 = r10
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r1 = 4000(0xfa0, float:5.605E-42)
            int r2 = r0.length()
            int r2 = r2 / 50
            int r2 = r2 * 1600
            r8 = 10000(0x2710, float:1.4013E-41)
            int r2 = java.lang.Math.min(r2, r8)
            int r1 = java.lang.Math.max(r1, r2)
            long r1 = (long) r1
            r7.timeLeft = r1
            android.widget.TextView r1 = r7.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r1.setTextSize(r8, r2)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 16
            r1.setGravity(r2)
            android.widget.TextView r1 = r7.infoTextView
            r1.setText(r0)
            android.widget.TextView r1 = r7.undoTextView
            r2 = 8
            r1.setVisibility(r2)
            android.widget.LinearLayout r1 = r7.undoButton
            r1.setVisibility(r2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r4.rightMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r1
            r1 = 1088421888(0x40e00000, float:7.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.bottomMargin = r1
            r1 = -1
            r4.height = r1
            r1 = 51
            r3.gravity = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r3.bottomMargin = r1
            r3.topMargin = r1
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 0
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r8 = 36
            r1.setAnimation(r2, r8, r8)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 0
            r1.setProgress(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r1.playAnimation()
            android.widget.TextView r1 = r7.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r2.<init>()
            r1.setMovementMethod(r2)
            r8 = r34
            r11 = r35
            goto L_0x1126
        L_0x0c2d:
            r1 = 12
            if (r0 != r1) goto L_0x0cd1
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625024(0x7f0e0440, float:1.8877244E38)
            java.lang.String r2 = "ColorThemeChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131166142(0x7var_be, float:1.794652E38)
            r0.setImageResource(r1)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.rightMargin = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r0
            android.widget.TextView r0 = r7.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r4 = r0
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.rightMargin = r0
            r0 = 2131625025(0x7f0e0441, float:1.8877246E38)
            java.lang.String r1 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r0)
            r2 = 42
            int r8 = r0.indexOf(r2)
            int r2 = r0.lastIndexOf(r2)
            if (r8 < 0) goto L_0x0ca2
            if (r2 < 0) goto L_0x0ca2
            if (r8 == r2) goto L_0x0ca2
            int r11 = r2 + 1
            r1.replace(r2, r11, r14)
            int r11 = r8 + 1
            r1.replace(r8, r11, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.String r12 = "tg://settings/themes"
            r11.<init>(r12)
            int r12 = r2 + -1
            r13 = 33
            r1.setSpan(r11, r8, r12, r13)
        L_0x0ca2:
            android.widget.TextView r11 = r7.subinfoTextView
            r11.setText(r1)
            android.widget.TextView r11 = r7.subinfoTextView
            r12 = 0
            r11.setVisibility(r12)
            android.widget.TextView r11 = r7.subinfoTextView
            r11.setSingleLine(r12)
            android.widget.TextView r11 = r7.subinfoTextView
            r13 = 2
            r11.setMaxLines(r13)
            android.widget.TextView r11 = r7.undoTextView
            r13 = 8
            r11.setVisibility(r13)
            android.widget.LinearLayout r11 = r7.undoButton
            r11.setVisibility(r12)
            org.telegram.ui.Components.RLottieImageView r11 = r7.leftImageView
            r11.setVisibility(r12)
            r8 = r34
            r11 = r35
            r0 = r4
            r2 = r14
            goto L_0x17b5
        L_0x0cd1:
            r1 = 2
            if (r0 == r1) goto L_0x0dc5
            r1 = 4
            if (r0 != r1) goto L_0x0cdb
            r8 = r34
            goto L_0x0dc7
        L_0x0cdb:
            r0 = 1110704128(0x42340000, float:45.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            r1 = 0
            r4.rightMargin = r1
            android.widget.TextView r0 = r7.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r8 = 1
            r0.setTextSize(r8, r2)
            android.widget.LinearLayout r0 = r7.undoButton
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.infoTextView
            android.graphics.Typeface r1 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r1)
            android.widget.TextView r0 = r7.subinfoTextView
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setVisibility(r1)
            int r0 = r7.currentAction
            r1 = 81
            if (r0 == r1) goto L_0x0d7a
            if (r0 == 0) goto L_0x0d7a
            r1 = 26
            if (r0 != r1) goto L_0x0d1b
            goto L_0x0d7a
        L_0x0d1b:
            r1 = 27
            if (r0 != r1) goto L_0x0d2e
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624915(0x7f0e03d3, float:1.8877023E38)
            java.lang.String r2 = "ChatsDeletedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0d88
        L_0x0d2e:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r0 == 0) goto L_0x0d6b
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0d5c
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x0d5c
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131624767(0x7f0e033f, float:1.8876723E38)
            java.lang.String r8 = "ChannelDeletedUndo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r1.setText(r2)
            goto L_0x0d6a
        L_0x0d5c:
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625847(0x7f0e0777, float:1.8878914E38)
            java.lang.String r8 = "GroupDeletedUndo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r1.setText(r2)
        L_0x0d6a:
            goto L_0x0d88
        L_0x0d6b:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624864(0x7f0e03a0, float:1.887692E38)
            java.lang.String r2 = "ChatDeletedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0d88
        L_0x0d7a:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625900(0x7f0e07ac, float:1.887902E38)
            java.lang.String r2 = "HistoryClearedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
        L_0x0d88:
            int r0 = r7.currentAction
            r1 = 81
            if (r0 == r1) goto L_0x0dbf
            r0 = 0
        L_0x0d8f:
            int r1 = r34.size()
            if (r0 >= r1) goto L_0x0db9
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            r8 = r34
            java.lang.Object r2 = r8.get(r0)
            java.lang.Long r2 = (java.lang.Long) r2
            long r11 = r2.longValue()
            int r2 = r7.currentAction
            if (r2 == 0) goto L_0x0db2
            r13 = 26
            if (r2 != r13) goto L_0x0db0
            goto L_0x0db2
        L_0x0db0:
            r2 = 0
            goto L_0x0db3
        L_0x0db2:
            r2 = 1
        L_0x0db3:
            r1.addDialogAction(r11, r2)
            int r0 = r0 + 1
            goto L_0x0d8f
        L_0x0db9:
            r8 = r34
            r11 = r35
            goto L_0x1126
        L_0x0dbf:
            r8 = r34
            r11 = r35
            goto L_0x1126
        L_0x0dc5:
            r8 = r34
        L_0x0dc7:
            r11 = r35
            r1 = 2
            if (r11 != r1) goto L_0x0ddb
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624860(0x7f0e039c, float:1.8876912E38)
            java.lang.String r2 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0de9
        L_0x0ddb:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624914(0x7f0e03d2, float:1.8877021E38)
            java.lang.String r2 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
        L_0x0de9:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            r1 = 0
            r4.rightMargin = r1
            android.widget.TextView r0 = r7.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r12 = 1
            r0.setTextSize(r12, r2)
            android.widget.LinearLayout r0 = r7.undoButton
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.infoTextView
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r2)
            android.widget.TextView r0 = r7.subinfoTextView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setVisibility(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558418(0x7f0d0012, float:1.8742151E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 0
            r0.setProgress(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.playAnimation()
            goto L_0x1126
        L_0x0e31:
            r8 = r34
            r11 = r35
        L_0x0e35:
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 16
            r0.setGravity(r1)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1106247680(0x41var_, float:30.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setMinHeight(r1)
            r0 = r10
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = "🎲"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0e7a
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625274(0x7f0e053a, float:1.8877751E38)
            java.lang.String r12 = "DiceInfo2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131165405(0x7var_dd, float:1.7945026E38)
            r1.setImageResource(r2)
            goto L_0x0var_
        L_0x0e7a:
            java.lang.String r1 = "🎯"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0e95
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            java.lang.String r12 = "DartInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x0ef4
        L_0x0e95:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "DiceEmojiInfo_"
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = org.telegram.messenger.LocaleController.getServerString(r1)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L_0x0ec9
            android.widget.TextView r2 = r7.infoTextView
            android.text.TextPaint r12 = r2.getPaint()
            android.graphics.Paint$FontMetricsInt r12 = r12.getFontMetricsInt()
            r13 = 1096810496(0x41600000, float:14.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r13 = 0
            java.lang.CharSequence r12 = org.telegram.messenger.Emoji.replaceEmoji(r1, r12, r15, r13)
            r2.setText(r12)
            goto L_0x0ef4
        L_0x0ec9:
            r13 = 0
            android.widget.TextView r2 = r7.infoTextView
            r15 = 1
            java.lang.Object[] r12 = new java.lang.Object[r15]
            r12[r13] = r0
            java.lang.String r15 = "DiceEmojiInfo"
            r13 = 2131625273(0x7f0e0539, float:1.887775E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r15, r13, r12)
            android.widget.TextView r13 = r7.infoTextView
            android.text.TextPaint r13 = r13.getPaint()
            android.graphics.Paint$FontMetricsInt r13 = r13.getFontMetricsInt()
            r16 = r1
            r15 = 1096810496(0x41600000, float:14.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r15 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r12, r13, r1, r15)
            r2.setText(r1)
        L_0x0ef4:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r2 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r1.setImageDrawable(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_XY
            r1.setScaleType(r2)
            r1 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.bottomMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3.leftMargin = r2
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3.width = r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3.height = r1
        L_0x0var_:
            android.widget.TextView r1 = r7.undoTextView
            r2 = 2131627707(0x7f0e0ebb, float:1.8882686E38)
            java.lang.String r12 = "SendDice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r2)
            r1.setText(r2)
            int r1 = r7.currentAction
            r2 = 16
            if (r1 != r2) goto L_0x0f7b
            android.widget.TextView r1 = r7.undoTextView
            android.text.TextPaint r1 = r1.getPaint()
            android.widget.TextView r2 = r7.undoTextView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r1 = r1.measureText(r2)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            android.widget.TextView r2 = r7.undoTextView
            r12 = 0
            r2.setVisibility(r12)
            android.widget.TextView r2 = r7.undoTextView
            java.lang.String r13 = "undo_cancelColor"
            int r13 = r7.getThemedColor(r13)
            r2.setTextColor(r13)
            android.widget.ImageView r2 = r7.undoImageView
            r13 = 8
            r2.setVisibility(r13)
            android.widget.LinearLayout r2 = r7.undoButton
            r2.setVisibility(r12)
            goto L_0x0f8b
        L_0x0f7b:
            r13 = 8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            android.widget.TextView r2 = r7.undoTextView
            r2.setVisibility(r13)
            android.widget.LinearLayout r2 = r7.undoButton
            r2.setVisibility(r13)
        L_0x0f8b:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r2
            r4.rightMargin = r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r2
            r2 = 1088421888(0x40e00000, float:7.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4.bottomMargin = r2
            r2 = -1
            r4.height = r2
            android.widget.TextView r2 = r7.subinfoTextView
            r12 = 8
            r2.setVisibility(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 0
            r2.setVisibility(r12)
            goto L_0x1126
        L_0x0fb3:
            r8 = r34
            r11 = r35
        L_0x0fb7:
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = r37
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            android.widget.ImageView r2 = r7.undoImageView
            r12 = 8
            r2.setVisibility(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 0
            r2.setVisibility(r12)
            java.lang.String r2 = "undo_infoColor"
            if (r0 == 0) goto L_0x108f
            android.widget.TextView r12 = r7.infoTextView
            java.lang.String r13 = "fonts/rmedium.ttf"
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r12.setTypeface(r13)
            android.widget.TextView r12 = r7.infoTextView
            r13 = 1096810496(0x41600000, float:14.0)
            r15 = 1
            r12.setTextSize(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "BODY.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "Wibe Big.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "Wibe Big 3.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r2 = r7.getThemedColor(r2)
            java.lang.String r13 = "Wibe Small.**"
            r12.setLayerColor(r13, r2)
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131627365(0x7f0e0d65, float:1.8881992E38)
            java.lang.String r13 = "ProximityAlertSet"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setText(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 2131558459(0x7f0d003b, float:1.8742234E38)
            r13 = 28
            r15 = 28
            r2.setAnimation(r12, r13, r15)
            android.widget.TextView r2 = r7.subinfoTextView
            r12 = 0
            r2.setVisibility(r12)
            android.widget.TextView r2 = r7.subinfoTextView
            r2.setSingleLine(r12)
            android.widget.TextView r2 = r7.subinfoTextView
            r12 = 3
            r2.setMaxLines(r12)
            if (r1 == 0) goto L_0x1066
            android.widget.TextView r2 = r7.subinfoTextView
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r16 = org.telegram.messenger.UserObject.getFirstName(r1)
            r18 = 0
            r15[r18] = r16
            float r12 = (float) r0
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatDistance(r12, r13)
            r13 = 1
            r15[r13] = r12
            java.lang.String r12 = "ProximityAlertSetInfoUser"
            r13 = 2131627367(0x7f0e0d67, float:1.8881996E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            r2.setText(r12)
            goto L_0x1080
        L_0x1066:
            android.widget.TextView r2 = r7.subinfoTextView
            r13 = 1
            java.lang.Object[] r15 = new java.lang.Object[r13]
            float r13 = (float) r0
            r12 = 2
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatDistance(r13, r12)
            r12 = 0
            r15[r12] = r13
            java.lang.String r12 = "ProximityAlertSetInfoGroup2"
            r13 = 2131627366(0x7f0e0d66, float:1.8881994E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            r2.setText(r12)
        L_0x1080:
            android.widget.LinearLayout r2 = r7.undoButton
            r12 = 8
            r2.setVisibility(r12)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r4.topMargin = r2
            goto L_0x1114
        L_0x108f:
            android.widget.TextView r12 = r7.infoTextView
            android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
            r12.setTypeface(r13)
            android.widget.TextView r12 = r7.infoTextView
            r13 = 1097859072(0x41700000, float:15.0)
            r15 = 1
            r12.setTextSize(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.clearLayerColors()
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "Body Main.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "Body Top.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "Line.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r2)
            java.lang.String r15 = "Curve Big.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r2 = r7.getThemedColor(r2)
            java.lang.String r13 = "Curve Small.**"
            r12.setLayerColor(r13, r2)
            r2 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4.topMargin = r12
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131627364(0x7f0e0d64, float:1.888199E38)
            java.lang.String r13 = "ProximityAlertCancelled"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setText(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r13 = 28
            r15 = 28
            r2.setAnimation(r12, r13, r15)
            android.widget.TextView r2 = r7.subinfoTextView
            r12 = 8
            r2.setVisibility(r12)
            android.widget.TextView r2 = r7.undoTextView
            java.lang.String r12 = "undo_cancelColor"
            int r12 = r7.getThemedColor(r12)
            r2.setTextColor(r12)
            android.widget.LinearLayout r2 = r7.undoButton
            r12 = 0
            r2.setVisibility(r12)
        L_0x1114:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 0
            r2.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r2.playAnimation()
        L_0x1126:
            r0 = r4
            r2 = r14
            goto L_0x17b5
        L_0x112a:
            r8 = r34
            r11 = r35
        L_0x112e:
            android.widget.ImageView r0 = r7.undoImageView
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 0
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.infoTextView
            android.graphics.Typeface r1 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r1)
            r0 = -1
            int r12 = r7.currentAction
            r13 = 76
            r15 = 1091567616(0x41100000, float:9.0)
            if (r12 != r13) goto L_0x1177
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r13 = "BroadcastGroupConvertSuccess"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setText(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 2131558446(0x7f0d002e, float:1.8742208E38)
            r13 = 36
            r2.setAnimation(r12, r13, r13)
            r17 = 1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r4.topMargin = r2
            android.widget.TextView r2 = r7.infoTextView
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r2.setTextSize(r13, r12)
            r2 = r14
            goto L_0x1776
        L_0x1177:
            r13 = 75
            if (r12 != r13) goto L_0x11a6
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131625826(0x7f0e0762, float:1.887887E38)
            java.lang.String r13 = "GigagroupConvertCancelHint"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setText(r12)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r12 = 2131558420(0x7f0d0014, float:1.8742155E38)
            r13 = 36
            r2.setAnimation(r12, r13, r13)
            r17 = 1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r4.topMargin = r2
            android.widget.TextView r2 = r7.infoTextView
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r2.setTextSize(r13, r12)
            r2 = r14
            goto L_0x1776
        L_0x11a6:
            r13 = 70
            if (r11 != r13) goto L_0x123e
            r12 = r10
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC.User) r12
            r13 = r37
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r13 = r13.intValue()
            android.widget.TextView r15 = r7.subinfoTextView
            r2 = 0
            r15.setSingleLine(r2)
            r2 = 2592000(0x278d00, float:3.632166E-39)
            if (r13 < r2) goto L_0x11cc
            r2 = 2592000(0x278d00, float:3.632166E-39)
            int r2 = r13 / r2
            java.lang.String r15 = "Months"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2)
            goto L_0x11fd
        L_0x11cc:
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r13 <= r2) goto L_0x11dd
            r2 = 86400(0x15180, float:1.21072E-40)
            int r2 = r13 / r2
            java.lang.String r15 = "Days"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2)
            goto L_0x11fd
        L_0x11dd:
            r2 = 3600(0xe10, float:5.045E-42)
            if (r13 < r2) goto L_0x11ea
            int r2 = r13 / 3600
            java.lang.String r15 = "Hours"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2)
            goto L_0x11fd
        L_0x11ea:
            r2 = 60
            if (r13 < r2) goto L_0x11f7
            int r2 = r13 / 60
            java.lang.String r15 = "Minutes"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2)
            goto L_0x11fd
        L_0x11f7:
            java.lang.String r2 = "Seconds"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r13)
        L_0x11fd:
            android.widget.TextView r15 = r7.infoTextView
            r27 = r0
            r1 = 1
            java.lang.Object[] r0 = new java.lang.Object[r1]
            r1 = 0
            r0[r1] = r2
            java.lang.String r1 = "AutoDeleteHintOnText"
            r18 = r2
            r2 = 2131624466(0x7f0e0212, float:1.8876113E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r2, r0)
            r15.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558440(0x7f0d0028, float:1.8742196E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            r17 = 1
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 0
            r0.setPadding(r2, r2, r2, r1)
            r2 = r14
            r0 = r27
            goto L_0x1776
        L_0x123e:
            r27 = r0
            r0 = 71
            if (r12 != r0) goto L_0x1277
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624465(0x7f0e0211, float:1.887611E38)
            java.lang.String r2 = "AutoDeleteHintOffText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558439(0x7f0d0027, float:1.8742194E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 0
            r0.setPadding(r2, r2, r2, r1)
            r2 = r14
            goto L_0x1774
        L_0x1277:
            r0 = 45
            if (r12 != r0) goto L_0x12aa
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.String r2 = "ImportMutualError"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r17 = 1
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r2 = r14
            r0 = r27
            goto L_0x1776
        L_0x12aa:
            r0 = 46
            if (r12 != r0) goto L_0x12dd
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.String r2 = "ImportNotAdmin"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558435(0x7f0d0023, float:1.8742186E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r17 = 1
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r2 = r14
            r0 = r27
            goto L_0x1776
        L_0x12dd:
            r0 = 47
            if (r12 != r0) goto L_0x131c
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625973(0x7f0e07f5, float:1.887917E38)
            java.lang.String r2 = "ImportedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558465(0x7f0d0041, float:1.8742247E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 1084227584(0x40a00000, float:5.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 0
            r0.setPadding(r2, r2, r2, r1)
            r17 = 1
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1
            r13 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r1, r13)
            r2 = r14
            r0 = r27
            goto L_0x1776
        L_0x131c:
            r13 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r12 != r0) goto L_0x1349
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624432(0x7f0e01f0, float:1.8876044E38)
            java.lang.String r2 = "AudioSpeedNormal"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558408(0x7f0d0008, float:1.874213E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r2 = r14
            goto L_0x1774
        L_0x1349:
            r0 = 50
            if (r12 != r0) goto L_0x1374
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624431(0x7f0e01ef, float:1.8876042E38)
            java.lang.String r2 = "AudioSpeedFast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558407(0x7f0d0007, float:1.8742129E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r2 = r14
            goto L_0x1774
        L_0x1374:
            r0 = 52
            if (r12 == r0) goto L_0x16d8
            r0 = 56
            if (r12 == r0) goto L_0x16d8
            r0 = 57
            if (r12 == r0) goto L_0x16d8
            r0 = 58
            if (r12 == r0) goto L_0x16d8
            r0 = 59
            if (r12 == r0) goto L_0x16d8
            r0 = 60
            if (r12 == r0) goto L_0x16d8
            r0 = 80
            if (r12 != r0) goto L_0x1393
            r2 = r14
            goto L_0x16d9
        L_0x1393:
            r0 = 54
            if (r12 != r0) goto L_0x13be
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624803(0x7f0e0363, float:1.8876796E38)
            java.lang.String r2 = "ChannelNotifyMembersInfoOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558493(0x7f0d005d, float:1.8742303E38)
            r2 = 30
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r2 = r14
            goto L_0x1774
        L_0x13be:
            r0 = 55
            if (r12 != r0) goto L_0x13e9
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624802(0x7f0e0362, float:1.8876794E38)
            java.lang.String r2 = "ChannelNotifyMembersInfoOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558492(0x7f0d005c, float:1.8742301E38)
            r2 = 30
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            r2 = r14
            goto L_0x1774
        L_0x13e9:
            r0 = 41
            if (r12 != r0) goto L_0x14a4
            if (r37 != 0) goto L_0x146c
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.clientUserId
            int r2 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x140f
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625996(0x7f0e080c, float:1.8879216E38)
            java.lang.String r2 = "InvLinkToSavedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setText(r1)
            goto L_0x1493
        L_0x140f:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r0 == 0) goto L_0x1440
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625995(0x7f0e080b, float:1.8879214E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = r0.title
            r16 = 0
            r15[r16] = r12
            java.lang.String r12 = "InvLinkToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r15)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x1493
        L_0x1440:
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625997(0x7f0e080d, float:1.8879218E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r0)
            r16 = 0
            r15[r16] = r12
            java.lang.String r12 = "InvLinkToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r15)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x1493
        L_0x146c:
            r0 = r37
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625994(0x7f0e080a, float:1.8879212E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = "Chats"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r0)
            r16 = 0
            r15[r16] = r12
            java.lang.String r12 = "InvLinkToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r15)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x1493:
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r2 = r14
            goto L_0x1774
        L_0x14a4:
            r0 = 53
            if (r12 != r0) goto L_0x1606
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r37 != 0) goto L_0x15a5
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            int r12 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r12 != 0) goto L_0x14f4
            int r1 = r0.intValue()
            r2 = 1
            if (r1 != r2) goto L_0x14d3
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625797(0x7f0e0745, float:1.8878812E38)
            java.lang.String r12 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x14e5
        L_0x14d3:
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625801(0x7f0e0749, float:1.887882E38)
            java.lang.String r12 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x14e5:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558490(0x7f0d005a, float:1.8742297E38)
            r12 = 30
            r1.setAnimation(r2, r12, r12)
            r2 = r14
            r12 = r27
            goto L_0x15ff
        L_0x14f4:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r1 == 0) goto L_0x1548
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            r2 = r14
            long r13 = -r5
            java.lang.Long r12 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r12)
            int r12 = r0.intValue()
            r13 = 1
            if (r12 != r13) goto L_0x152c
            android.widget.TextView r12 = r7.infoTextView
            r14 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = r1.title
            r16 = 0
            r15[r16] = r13
            java.lang.String r13 = "FwdMessageToGroup"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r13, r14, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
            goto L_0x1547
        L_0x152c:
            r16 = 0
            android.widget.TextView r12 = r7.infoTextView
            r13 = 2131625800(0x7f0e0748, float:1.8878818E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = r1.title
            r15[r16] = r14
            java.lang.String r14 = "FwdMessagesToGroup"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r14, r13, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
        L_0x1547:
            goto L_0x1598
        L_0x1548:
            r2 = r14
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r12 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r12)
            int r12 = r0.intValue()
            r13 = 1
            if (r12 != r13) goto L_0x157b
            android.widget.TextView r12 = r7.infoTextView
            r14 = 2131625798(0x7f0e0746, float:1.8878814E38)
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r16 = org.telegram.messenger.UserObject.getFirstName(r1)
            r18 = 0
            r15[r18] = r16
            java.lang.String r13 = "FwdMessageToUser"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r13, r14, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
            goto L_0x1598
        L_0x157b:
            r18 = 0
            android.widget.TextView r12 = r7.infoTextView
            r13 = 2131625802(0x7f0e074a, float:1.8878822E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r1)
            r15[r18] = r14
            java.lang.String r14 = "FwdMessagesToUser"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r14, r13, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
        L_0x1598:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r12 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r13 = 30
            r1.setAnimation(r12, r13, r13)
            r12 = 300(0x12c, double:1.48E-321)
            goto L_0x15ff
        L_0x15a5:
            r2 = r14
            r1 = r37
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r12 = r0.intValue()
            r13 = 1
            if (r12 != r13) goto L_0x15d4
            android.widget.TextView r12 = r7.infoTextView
            r14 = 2131625795(0x7f0e0743, float:1.8878808E38)
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = "Chats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r13, r1)
            r16 = 0
            r15[r16] = r13
            java.lang.String r13 = "FwdMessageToChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r13, r14, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
            goto L_0x15f3
        L_0x15d4:
            android.widget.TextView r12 = r7.infoTextView
            r13 = 2131625799(0x7f0e0747, float:1.8878816E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = "Chats"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r1)
            r16 = 0
            r15[r16] = r14
            java.lang.String r14 = "FwdMessagesToChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r14, r13, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
        L_0x15f3:
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r13 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r14 = 30
            r12.setAnimation(r13, r14, r14)
            r12 = 300(0x12c, double:1.48E-321)
        L_0x15ff:
            r14 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r14
            r0 = r12
            goto L_0x1776
        L_0x1606:
            r2 = r14
            r0 = 61
            if (r12 != r0) goto L_0x1774
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r37 != 0) goto L_0x16a1
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r12 = r1.clientUserId
            int r1 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x163a
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131624567(0x7f0e0277, float:1.8876317E38)
            java.lang.String r13 = "BackgroundToSavedMessages"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            r1.setText(r12)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r12 = 2131558490(0x7f0d005a, float:1.8742297E38)
            r13 = 30
            r1.setAnimation(r12, r13, r13)
            goto L_0x16d2
        L_0x163a:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r1 == 0) goto L_0x166b
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r12 = -r5
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r12)
            android.widget.TextView r12 = r7.infoTextView
            r13 = 2131624566(0x7f0e0276, float:1.8876315E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = r1.title
            r16 = 0
            r15[r16] = r14
            java.lang.String r14 = "BackgroundToGroup"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r14, r13, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
            goto L_0x1696
        L_0x166b:
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r12 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r12)
            android.widget.TextView r12 = r7.infoTextView
            r13 = 2131624568(0x7f0e0278, float:1.887632E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r1)
            r16 = 0
            r15[r16] = r14
            java.lang.String r14 = "BackgroundToUser"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r14, r13, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
        L_0x1696:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r12 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r13 = 30
            r1.setAnimation(r12, r13, r13)
            goto L_0x16d2
        L_0x16a1:
            r1 = r37
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.widget.TextView r12 = r7.infoTextView
            r13 = 2131624565(0x7f0e0275, float:1.8876313E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            java.lang.String r14 = "Chats"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r1)
            r16 = 0
            r15[r16] = r14
            java.lang.String r14 = "BackgroundToChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r14, r13, r15)
            android.text.SpannableStringBuilder r13 = org.telegram.messenger.AndroidUtilities.replaceTags(r13)
            r12.setText(r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r13 = 2131558444(0x7f0d002c, float:1.8742204E38)
            r14 = 30
            r12.setAnimation(r13, r14, r14)
        L_0x16d2:
            r12 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r12
            goto L_0x1774
        L_0x16d8:
            r2 = r14
        L_0x16d9:
            r0 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r1 = 80
            if (r12 != r1) goto L_0x16f0
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131625382(0x7f0e05a6, float:1.887797E38)
            java.lang.String r13 = "EmailCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
            goto L_0x1760
        L_0x16f0:
            r1 = 60
            if (r12 != r1) goto L_0x1703
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131627142(0x7f0e0CLASSNAME, float:1.888154E38)
            java.lang.String r13 = "PhoneCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
            goto L_0x1760
        L_0x1703:
            r1 = 56
            if (r12 != r1) goto L_0x1716
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131628314(0x7f0e111a, float:1.8883917E38)
            java.lang.String r13 = "UsernameCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
            goto L_0x1760
        L_0x1716:
            r1 = 57
            if (r12 != r1) goto L_0x1729
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131625883(0x7f0e079b, float:1.8878987E38)
            java.lang.String r13 = "HashtagCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
            goto L_0x1760
        L_0x1729:
            r1 = 52
            if (r12 != r1) goto L_0x173c
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131626320(0x7f0e0950, float:1.8879873E38)
            java.lang.String r13 = "MessageCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
            goto L_0x1760
        L_0x173c:
            r1 = 59
            if (r12 != r1) goto L_0x1752
            r0 = 2131558542(0x7f0d008e, float:1.8742403E38)
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131626133(0x7f0e0895, float:1.8879494E38)
            java.lang.String r13 = "LinkCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
            goto L_0x1760
        L_0x1752:
            android.widget.TextView r1 = r7.infoTextView
            r12 = 2131628085(0x7f0e1035, float:1.8883453E38)
            java.lang.String r13 = "TextCopied"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setText(r12)
        L_0x1760:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r12 = 30
            r1.setAnimation(r0, r12, r12)
            r12 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r12
            android.widget.TextView r1 = r7.infoTextView
            r12 = 1097859072(0x41700000, float:15.0)
            r13 = 1
            r1.setTextSize(r13, r12)
        L_0x1774:
            r0 = r27
        L_0x1776:
            android.widget.TextView r12 = r7.subinfoTextView
            r13 = 8
            r12.setVisibility(r13)
            android.widget.TextView r12 = r7.undoTextView
            java.lang.String r14 = "undo_cancelColor"
            int r14 = r7.getThemedColor(r14)
            r12.setTextColor(r14)
            android.widget.LinearLayout r12 = r7.undoButton
            r12.setVisibility(r13)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r4.leftMargin = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r4.rightMargin = r12
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r13 = 0
            r12.setProgress(r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.playAnimation()
            r12 = 0
            int r14 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r14 <= 0) goto L_0x17b4
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r13 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r13.<init>(r7)
            r12.postDelayed(r13, r0)
        L_0x17b4:
            r0 = r4
        L_0x17b5:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r4 = r7.infoTextView
            java.lang.CharSequence r4 = r4.getText()
            r1.append(r4)
            android.widget.TextView r4 = r7.subinfoTextView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x17e3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = ". "
            r2.append(r4)
            android.widget.TextView r4 = r7.subinfoTextView
            java.lang.CharSequence r4 = r4.getText()
            r2.append(r4)
            java.lang.String r14 = r2.toString()
            goto L_0x17e4
        L_0x17e3:
            r14 = r2
        L_0x17e4:
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r33.isMultilineSubInfo()
            if (r1 == 0) goto L_0x183c
            android.view.ViewParent r1 = r33.getParent()
            r12 = r1
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r1 = r12.getMeasuredWidth()
            if (r1 != 0) goto L_0x1805
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r2.x
        L_0x1805:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r13 = r1 - r2
            android.widget.TextView r2 = r7.subinfoTextView
            r1 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r1)
            r14 = 0
            r1 = 0
            int r15 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r1)
            r16 = 0
            r1 = r33
            r18 = r3
            r3 = r4
            r4 = r14
            r21 = r5
            r5 = r15
            r6 = r16
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            android.widget.TextView r1 = r7.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r7.undoViewHeight = r1
            goto L_0x18dd
        L_0x183c:
            r18 = r3
            r21 = r5
            boolean r1 = r33.hasSubInfo()
            if (r1 == 0) goto L_0x1850
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.undoViewHeight = r1
            goto L_0x18dd
        L_0x1850:
            android.view.ViewParent r1 = r33.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x18dd
            android.view.ViewParent r1 = r33.getParent()
            r12 = r1
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r1 = r12.getMeasuredWidth()
            int r2 = r12.getPaddingLeft()
            int r1 = r1 - r2
            int r2 = r12.getPaddingRight()
            int r1 = r1 - r2
            if (r1 > 0) goto L_0x1873
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r2.x
        L_0x1873:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r13 = r1 - r2
            android.widget.TextView r2 = r7.infoTextView
            r1 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r1)
            r4 = 0
            r1 = 0
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r1)
            r6 = 0
            r1 = r33
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            android.widget.TextView r1 = r7.infoTextView
            int r1 = r1.getMeasuredHeight()
            int r2 = r7.currentAction
            r3 = 16
            if (r2 == r3) goto L_0x18a7
            r3 = 17
            if (r2 == r3) goto L_0x18a7
            r3 = 18
            if (r2 != r3) goto L_0x18a4
            goto L_0x18a7
        L_0x18a4:
            r15 = 1105199104(0x41e00000, float:28.0)
            goto L_0x18a9
        L_0x18a7:
            r15 = 1096810496(0x41600000, float:14.0)
        L_0x18a9:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r1 = r1 + r2
            r7.undoViewHeight = r1
            int r2 = r7.currentAction
            r3 = 18
            if (r2 != r3) goto L_0x18c3
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r7.undoViewHeight = r1
            goto L_0x18dd
        L_0x18c3:
            r3 = 25
            if (r2 != r3) goto L_0x18d4
            r2 = 1112014848(0x42480000, float:50.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r7.undoViewHeight = r1
            goto L_0x18dd
        L_0x18d4:
            if (r17 == 0) goto L_0x18dd
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = r1 - r2
            r7.undoViewHeight = r1
        L_0x18dd:
            int r1 = r33.getVisibility()
            if (r1 == 0) goto L_0x1946
            r1 = 0
            r7.setVisibility(r1)
            boolean r1 = r7.fromTop
            if (r1 == 0) goto L_0x18ee
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x18f0
        L_0x18ee:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x18f0:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r3 = r7.undoViewHeight
            int r2 = r2 + r3
            float r2 = (float) r2
            float r1 = r1 * r2
            r7.setEnterOffset(r1)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r2 = 1
            android.animation.Animator[] r3 = new android.animation.Animator[r2]
            r2 = 2
            float[] r2 = new float[r2]
            boolean r4 = r7.fromTop
            if (r4 == 0) goto L_0x190f
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1911
        L_0x190f:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x1911:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r6 = r7.undoViewHeight
            int r5 = r5 + r6
            float r5 = (float) r5
            float r4 = r4 * r5
            r5 = 0
            r2[r5] = r4
            boolean r4 = r7.fromTop
            if (r4 == 0) goto L_0x1925
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x1927
        L_0x1925:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x1927:
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "enterOffset"
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r7, r4, r2)
            r4 = 0
            r3[r4] = r2
            r1.playTogether(r3)
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r1.setInterpolator(r2)
            r2 = 180(0xb4, double:8.9E-322)
            r1.setDuration(r2)
            r1.start()
        L_0x1946:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(java.util.ArrayList, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* renamed from: lambda$showWithAction$2$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m2704lambda$showWithAction$2$orgtelegramuiComponentsUndoView(View view) {
        hide(false, 1);
    }

    static /* synthetic */ boolean lambda$showWithAction$3(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$showWithAction$6$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m2707lambda$showWithAction$6$orgtelegramuiComponentsUndoView(TLRPC.Message message, View v) {
        hide(true, 1);
        TLRPC.TL_payments_getPaymentReceipt req = new TLRPC.TL_payments_getPaymentReceipt();
        req.msg_id = message.id;
        req.peer = this.parentFragment.getMessagesController().getInputPeer(message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(req, new UndoView$$ExternalSyntheticLambda7(this), 2);
    }

    /* renamed from: lambda$showWithAction$5$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m2706lambda$showWithAction$5$orgtelegramuiComponentsUndoView(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new UndoView$$ExternalSyntheticLambda6(this, response));
    }

    /* renamed from: lambda$showWithAction$4$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m2705lambda$showWithAction$4$orgtelegramuiComponentsUndoView(TLObject response) {
        if (response instanceof TLRPC.TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response));
        }
    }

    /* renamed from: lambda$showWithAction$7$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m2708lambda$showWithAction$7$orgtelegramuiComponentsUndoView() {
        this.leftImageView.performHapticFeedback(3, 2);
    }

    /* access modifiers changed from: protected */
    public boolean canUndo() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, NUM));
        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float bottom = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) AndroidUtilities.dp(9.0f));
            if (bottom > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), bottom);
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
            float bottom = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) AndroidUtilities.dp(9.0f));
            if (bottom > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), bottom);
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
            int newSeconds = j > 0 ? (int) Math.ceil((double) (((float) j) / 1000.0f)) : 0;
            if (this.prevSeconds != newSeconds) {
                this.prevSeconds = newSeconds;
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, newSeconds))});
                this.timeLeftString = format;
                StaticLayout staticLayout = this.timeLayout;
                if (staticLayout != null) {
                    this.timeLayoutOut = staticLayout;
                    this.timeReplaceProgress = 0.0f;
                    this.textWidthOut = this.textWidth;
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
        long newTime = SystemClock.elapsedRealtime();
        long j2 = this.timeLeft - (newTime - this.lastUpdateTime);
        this.timeLeft = j2;
        this.lastUpdateTime = newTime;
        if (j2 <= 0) {
            hide(true, this.hideAnimationType);
        }
        invalidate();
    }

    public void invalidate() {
        super.invalidate();
        this.infoTextView.invalidate();
        this.leftImageView.invalidate();
    }

    public void setInfoText(CharSequence text) {
        this.infoText = text;
    }

    public void setHideAnimationType(int hideAnimationType2) {
        this.hideAnimationType = hideAnimationType2;
    }

    public float getEnterOffset() {
        return this.enterOffset;
    }

    public void setEnterOffset(float enterOffset2) {
        if (this.enterOffset != enterOffset2) {
            this.enterOffset = enterOffset2;
            updatePosition();
        }
    }

    private void updatePosition() {
        setTranslationY(this.enterOffset - this.additionalTranslationY);
        invalidate();
    }

    public Drawable getBackground() {
        return this.backgroundDrawable;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
