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
    public static final int ACTION_PREVIEW_MEDIA_DESELECTED = 82;
    public static final int ACTION_PROFILE_PHOTO_CHANGED = 22;
    public static final int ACTION_PROXIMITY_REMOVED = 25;
    public static final int ACTION_PROXIMITY_SET = 24;
    public static final int ACTION_QR_SESSION_ACCEPTED = 11;
    public static final int ACTION_QUIZ_CORRECT = 13;
    public static final int ACTION_QUIZ_INCORRECT = 14;
    public static final int ACTION_REMOVED_FROM_FOLDER = 21;
    public static final int ACTION_REPORT_SENT = 74;
    public static int ACTION_RINGTONE_ADDED = 83;
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
        this.enterOffsetMargin = AndroidUtilities.dp(8.0f);
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m4504lambda$new$0$orgtelegramuiComponentsUndoView(View v) {
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
            int a2 = -NUM;
            if (animated != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (animated == 1) {
                    Animator[] animatorArr = new Animator[1];
                    float[] fArr = new float[1];
                    if (!this.fromTop) {
                        a2 = NUM;
                    }
                    fArr[0] = a2 * ((float) (this.enterOffsetMargin + this.undoViewHeight));
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
                a2 = NUM;
            }
            setEnterOffset(a2 * ((float) (this.enterOffsetMargin + this.undoViewHeight)));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v188, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r12v127, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0968  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x09a7  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x09b2  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x09f6  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0ee2  */
    /* JADX WARNING: Removed duplicated region for block: B:618:0x18c4  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x18eb  */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x1931  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x19da  */
    /* JADX WARNING: Removed duplicated region for block: B:668:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r35, int r36, java.lang.Object r37, java.lang.Object r38, java.lang.Runnable r39, java.lang.Runnable r40) {
        /*
            r34 = this;
            r7 = r34
            r8 = r35
            r9 = r36
            r10 = r37
            r11 = r38
            r12 = r39
            r13 = r40
            boolean r0 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r0 != 0) goto L_0x001a
            int r0 = r7.currentAction
            r1 = 52
            if (r0 == r1) goto L_0x1a38
        L_0x001a:
            int r0 = r7.currentAction
            r1 = 56
            if (r0 == r1) goto L_0x1a38
            r1 = 57
            if (r0 == r1) goto L_0x1a38
            r1 = 58
            if (r0 == r1) goto L_0x1a38
            r1 = 59
            if (r0 == r1) goto L_0x1a38
            r1 = 60
            if (r0 == r1) goto L_0x1a38
            r1 = 80
            if (r0 == r1) goto L_0x1a38
            r1 = 33
            if (r0 != r1) goto L_0x003a
            goto L_0x1a38
        L_0x003a:
            java.lang.Runnable r0 = r7.currentActionRunnable
            if (r0 == 0) goto L_0x0041
            r0.run()
        L_0x0041:
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
            r2 = 5000(0x1388, double:2.4703E-320)
            r7.timeLeft = r2
            r7.currentInfoObject = r10
            long r2 = android.os.SystemClock.elapsedRealtime()
            r7.lastUpdateTime = r2
            android.widget.TextView r0 = r7.undoTextView
            r2 = 2131628500(0x7f0e11d4, float:1.8884294E38)
            java.lang.String r3 = "Undo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.ImageView r0 = r7.undoImageView
            r0.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setPadding(r15, r15, r15, r15)
            android.widget.TextView r0 = r7.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r14, r2)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r3 = 8
            r0.setVisibility(r3)
            android.widget.TextView r0 = r7.infoTextView
            r4 = 51
            r0.setGravity(r4)
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
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r2 = r0
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r0 = 19
            r2.gravity = r0
            r2.bottomMargin = r15
            r2.topMargin = r15
            r0 = 1077936128(0x40400000, float:3.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r2.leftMargin = r0
            r0 = 1113063424(0x42580000, float:54.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r2.width = r0
            r0 = -2
            r2.height = r0
            android.widget.TextView r0 = r7.infoTextView
            r0.setMinHeight(r15)
            r17 = 0
            r3 = 0
            r19 = 0
            r0 = 0
            if (r12 != 0) goto L_0x00e4
            if (r13 == 0) goto L_0x00e8
        L_0x00e4:
            int r1 = ACTION_RINGTONE_ADDED
            if (r9 != r1) goto L_0x00f4
        L_0x00e8:
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r1.<init>(r7)
            r7.setOnClickListener(r1)
            r7.setOnTouchListener(r0)
            goto L_0x00fc
        L_0x00f4:
            r7.setOnClickListener(r0)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r1 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r7.setOnTouchListener(r1)
        L_0x00fc:
            android.widget.TextView r1 = r7.infoTextView
            r1.setMovementMethod(r0)
            boolean r1 = r34.isTooltipAction()
            java.lang.String r14 = ""
            r26 = 1090519040(0x41000000, float:8.0)
            r27 = 1114112000(0x42680000, float:58.0)
            r28 = r2
            r29 = r3
            if (r1 == 0) goto L_0x0a2e
            r1 = 36
            r31 = 0
            int r15 = ACTION_RINGTONE_ADDED
            if (r9 != r15) goto L_0x0146
            android.widget.TextView r2 = r7.subinfoTextView
            r3 = 0
            r2.setSingleLine(r3)
            r2 = 2131628171(0x7f0e108b, float:1.8883627E38)
            java.lang.String r3 = "SoundAdded"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131628172(0x7f0e108c, float:1.888363E38)
            java.lang.String r15 = "SoundAddedSubtitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r3, r12)
            r7.currentActionRunnable = r0
            r0 = 2131558517(0x7f0d0075, float:1.8742352E38)
            r20 = r0
            r15 = r1
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x0146:
            r15 = r1
            r1 = 74
            if (r9 != r1) goto L_0x0173
            android.widget.TextView r0 = r7.subinfoTextView
            r1 = 0
            r0.setSingleLine(r1)
            r0 = 2131627743(0x7f0e0edf, float:1.888276E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 2131627752(0x7f0e0ee8, float:1.8882777E38)
            java.lang.Object[] r3 = new java.lang.Object[r1]
            java.lang.String r1 = "ReportSentInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            r0 = 2131558457(0x7f0d0039, float:1.874223E38)
            r20 = r0
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x0173:
            r1 = 34
            if (r9 != r1) goto L_0x01eb
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = r11
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            boolean r20 = org.telegram.messenger.ChatObject.isChannelOrGiga(r1)
            if (r20 == 0) goto L_0x019f
            r3 = 1
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r21 = org.telegram.messenger.UserObject.getFirstName(r0)
            r22 = 0
            r2[r22] = r21
            java.lang.String r3 = "VoipChannelInvitedUser"
            r24 = r1
            r1 = 2131628738(0x7f0e12c2, float:1.8884777E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x01ba
        L_0x019f:
            r24 = r1
            r22 = 0
            r1 = 2131628827(0x7f0e131b, float:1.8884958E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r22] = r2
            java.lang.String r2 = "VoipGroupInvitedUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x01ba:
            r3 = 0
            r1 = 0
            org.telegram.ui.Components.AvatarDrawable r20 = new org.telegram.ui.Components.AvatarDrawable
            r20.<init>()
            r22 = r20
            r20 = 1094713344(0x41400000, float:12.0)
            r25 = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r20 = r2
            r2 = r22
            r2.setTextSize(r1)
            r2.setInfo((org.telegram.tgnet.TLRPC.User) r0)
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            r1.setForUserOrChat(r0, r2)
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            r2 = 0
            r1.setVisibility(r2)
            r1 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r1
            r1 = r15
            r2 = r20
            r0 = r25
            goto L_0x0961
        L_0x01eb:
            r1 = 44
            if (r9 != r1) goto L_0x0298
            r0 = r11
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x0231
            r1 = r10
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r2 == 0) goto L_0x0218
            r3 = 1
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r21 = org.telegram.messenger.UserObject.getFirstName(r1)
            r22 = 0
            r2[r22] = r21
            java.lang.String r3 = "VoipChannelUserJoined"
            r12 = 2131628763(0x7f0e12db, float:1.8884828E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0230
        L_0x0218:
            r22 = 0
            r2 = 2131628778(0x7f0e12ea, float:1.8884858E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r1)
            r12[r22] = r3
            java.lang.String r3 = "VoipChatUserJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r12)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x0230:
            goto L_0x0267
        L_0x0231:
            r1 = r10
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r2 == 0) goto L_0x0251
            r2 = 2131628732(0x7f0e12bc, float:1.8884765E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r3 = r1.title
            r20 = 0
            r12[r20] = r3
            java.lang.String r3 = "VoipChannelChatJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r12)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0267
        L_0x0251:
            r20 = 0
            r2 = 2131628768(0x7f0e12e0, float:1.8884838E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r3 = r1.title
            r12[r20] = r3
            java.lang.String r3 = "VoipChatChatJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r12)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x0267:
            r3 = 0
            r1 = 0
            org.telegram.ui.Components.AvatarDrawable r12 = new org.telegram.ui.Components.AvatarDrawable
            r12.<init>()
            r20 = 1094713344(0x41400000, float:12.0)
            r22 = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r12.setTextSize(r0)
            r0 = r10
            org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
            r12.setInfo((org.telegram.tgnet.TLObject) r0)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r20 = r1
            r1 = r10
            org.telegram.tgnet.TLObject r1 = (org.telegram.tgnet.TLObject) r1
            r0.setForUserOrChat(r1, r12)
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r1 = 0
            r0.setVisibility(r1)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x0298:
            r1 = 37
            if (r9 != r1) goto L_0x031a
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setTextSize(r1)
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x02c2
            r1 = r10
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            r0.setInfo((org.telegram.tgnet.TLRPC.User) r1)
            org.telegram.ui.Components.BackupImageView r2 = r7.avatarImageView
            r2.setForUserOrChat(r1, r0)
            java.lang.String r2 = r1.first_name
            java.lang.String r3 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            goto L_0x02d0
        L_0x02c2:
            r1 = r10
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r1)
            org.telegram.ui.Components.BackupImageView r2 = r7.avatarImageView
            r2.setForUserOrChat(r1, r0)
            java.lang.String r2 = r1.title
            r1 = r2
        L_0x02d0:
            r2 = r11
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            boolean r3 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r3 == 0) goto L_0x02ef
            r12 = 1
            java.lang.Object[] r3 = new java.lang.Object[r12]
            r12 = 0
            r3[r12] = r1
            java.lang.String r12 = "VoipChannelUserChanged"
            r22 = r0
            r0 = 2131628762(0x7f0e12da, float:1.8884826E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0304
        L_0x02ef:
            r22 = r0
            r0 = 2131628891(0x7f0e135b, float:1.8885087E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            r3 = 0
            r12[r3] = r1
            java.lang.String r3 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r12)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0304:
            r3 = 0
            r12 = 0
            r20 = r0
            org.telegram.ui.Components.BackupImageView r0 = r7.avatarImageView
            r24 = r1
            r1 = 0
            r0.setVisibility(r1)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            r2 = r20
            goto L_0x0961
        L_0x031a:
            r1 = 33
            if (r9 != r1) goto L_0x0334
            r0 = 2131628809(0x7f0e1309, float:1.8884921E38)
            java.lang.String r1 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = 0
            r0 = 2131558570(0x7f0d00aa, float:1.874246E38)
            r12 = r0
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            goto L_0x0961
        L_0x0334:
            r1 = 77
            if (r9 != r1) goto L_0x0366
            r2 = r10
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            r3 = 0
            r1 = 2131558484(0x7f0d0054, float:1.8742285E38)
            r20 = r1
            r0 = 5000(0x1388, double:2.4703E-320)
            r7.timeLeft = r0
            org.telegram.ui.ActionBar.BaseFragment r0 = r7.parentFragment
            if (r0 == 0) goto L_0x0361
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.Message
            if (r0 == 0) goto L_0x0361
            r0 = r11
            org.telegram.tgnet.TLRPC$Message r0 = (org.telegram.tgnet.TLRPC.Message) r0
            r1 = 0
            r7.setOnTouchListener(r1)
            android.widget.TextView r12 = r7.infoTextView
            r12.setMovementMethod(r1)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r1.<init>(r7, r0)
            r7.setOnClickListener(r1)
        L_0x0361:
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x0366:
            r0 = 30
            if (r9 != r0) goto L_0x039f
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x0376
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x037c
        L_0x0376:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x037c:
            r1 = 2131628889(0x7f0e1359, float:1.8885083E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558571(0x7f0d00ab, float:1.8742462E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x039f:
            r0 = 35
            if (r9 != r0) goto L_0x03de
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x03af
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03bb
        L_0x03af:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x03b9
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x03bb
        L_0x03b9:
            java.lang.String r0 = ""
        L_0x03bb:
            r1 = 2131628890(0x7f0e135a, float:1.8885085E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558571(0x7f0d00ab, float:1.8742462E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x03de:
            r0 = 31
            if (r9 != r0) goto L_0x0417
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x03ee
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03f4
        L_0x03ee:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x03f4:
            r1 = 2131628887(0x7f0e1357, float:1.888508E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558577(0x7f0d00b1, float:1.8742474E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x0417:
            r0 = 38
            if (r9 != r0) goto L_0x0454
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x0439
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            r1 = 2131628899(0x7f0e1363, float:1.8885104E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = r0.title
            r12 = 0
            r3[r12] = r2
            java.lang.String r2 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r0
            goto L_0x0447
        L_0x0439:
            r0 = 2131628898(0x7f0e1362, float:1.8885102E38)
            java.lang.String r1 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = r0
        L_0x0447:
            r3 = 0
            r0 = 2131558563(0x7f0d00a3, float:1.8742445E38)
            r12 = r0
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            goto L_0x0961
        L_0x0454:
            r0 = 42
            if (r9 != r0) goto L_0x048e
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r1 == 0) goto L_0x0470
            r1 = 2131628753(0x7f0e12d1, float:1.8884808E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x047e
        L_0x0470:
            r1 = 2131628867(0x7f0e1343, float:1.8885039E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x047e:
            r3 = 0
            r1 = 2131558461(0x7f0d003d, float:1.8742238E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x048e:
            r0 = 43
            if (r9 != r0) goto L_0x04c8
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r1 == 0) goto L_0x04aa
            r1 = 2131628754(0x7f0e12d2, float:1.888481E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x04b8
        L_0x04aa:
            r1 = 2131628868(0x7f0e1344, float:1.888504E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x04b8:
            r3 = 0
            r1 = 2131558467(0x7f0d0043, float:1.874225E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x04c8:
            int r0 = r7.currentAction
            r1 = 39
            if (r0 == r1) goto L_0x093e
            r1 = 100
            if (r0 != r1) goto L_0x04d6
            r30 = r15
            goto L_0x0940
        L_0x04d6:
            r1 = 40
            if (r0 == r1) goto L_0x08a1
            r1 = 101(0x65, float:1.42E-43)
            if (r0 != r1) goto L_0x04e2
            r30 = r15
            goto L_0x08a3
        L_0x04e2:
            r1 = 36
            if (r9 != r1) goto L_0x051b
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x04f2
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x04f8
        L_0x04f2:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x04f8:
            r1 = 2131628888(0x7f0e1358, float:1.8885081E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558577(0x7f0d00b1, float:1.8742474E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x051b:
            r1 = 32
            if (r9 != r1) goto L_0x0554
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x052b
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0531
        L_0x052b:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x0531:
            r1 = 2131628858(0x7f0e133a, float:1.888502E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558569(0x7f0d00a9, float:1.8742458E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x0961
        L_0x0554:
            r1 = 9
            if (r9 == r1) goto L_0x085f
            r1 = 10
            if (r9 != r1) goto L_0x0560
            r30 = r15
            goto L_0x0861
        L_0x0560:
            r1 = 8
            if (r9 != r1) goto L_0x0581
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = 2131626916(0x7f0e0ba4, float:1.8881082E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r12 = 0
            r3[r12] = r2
            java.lang.String r2 = "NowInContacts"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r3 = 0
            r0 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r1 = r15
            goto L_0x0961
        L_0x0581:
            r1 = 22
            if (r9 != r1) goto L_0x05f2
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r5)
            if (r0 == 0) goto L_0x05a3
            if (r10 != 0) goto L_0x0598
            r0 = 2131626365(0x7f0e097d, float:1.8879964E38)
            java.lang.String r1 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
            goto L_0x05eb
        L_0x0598:
            r0 = 2131626366(0x7f0e097e, float:1.8879966E38)
            java.lang.String r1 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
            goto L_0x05eb
        L_0x05a3:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x05d4
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x05d4
            if (r10 != 0) goto L_0x05c9
            r1 = 2131626361(0x7f0e0979, float:1.8879956E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            goto L_0x05eb
        L_0x05c9:
            r1 = 2131626362(0x7f0e097a, float:1.8879958E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            goto L_0x05eb
        L_0x05d4:
            if (r10 != 0) goto L_0x05e1
            r1 = 2131626363(0x7f0e097b, float:1.887996E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            goto L_0x05eb
        L_0x05e1:
            r1 = 2131626364(0x7f0e097c, float:1.8879962E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
        L_0x05eb:
            r3 = 0
            r0 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r1 = r15
            goto L_0x0961
        L_0x05f2:
            r1 = 23
            if (r9 != r1) goto L_0x0606
            r0 = 2131624964(0x7f0e0404, float:1.8877123E38)
            java.lang.String r1 = "ChatWasMovedToMainList"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = 0
            r0 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r1 = r15
            goto L_0x0961
        L_0x0606:
            r1 = 6
            if (r9 != r1) goto L_0x0622
            r0 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r1 = "ArchiveHidden"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131624337(0x7f0e0191, float:1.887585E38)
            java.lang.String r1 = "ArchiveHiddenInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r1 = 48
            goto L_0x0961
        L_0x0622:
            r1 = 13
            if (r0 != r1) goto L_0x063f
            r0 = 2131627631(0x7f0e0e6f, float:1.8882532E38)
            java.lang.String r1 = "QuizWellDone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131627632(0x7f0e0e70, float:1.8882534E38)
            java.lang.String r1 = "QuizWellDoneInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131558579(0x7f0d00b3, float:1.8742478E38)
            r1 = 44
            goto L_0x0961
        L_0x063f:
            r1 = 14
            if (r0 != r1) goto L_0x065c
            r0 = 2131627633(0x7f0e0e71, float:1.8882536E38)
            java.lang.String r1 = "QuizWrongAnswer"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131627634(0x7f0e0e72, float:1.8882538E38)
            java.lang.String r1 = "QuizWrongAnswerInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131558581(0x7f0d00b5, float:1.8742482E38)
            r1 = 44
            goto L_0x0961
        L_0x065c:
            r0 = 7
            if (r9 != r0) goto L_0x0689
            r0 = 2131624344(0x7f0e0198, float:1.8875865E38)
            java.lang.String r1 = "ArchivePinned"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r0 = r0.dialogFilters
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0681
            r0 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r1 = "ArchivePinnedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = r0
            goto L_0x0683
        L_0x0681:
            r0 = 0
            r3 = r0
        L_0x0683:
            r0 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r1 = r15
            goto L_0x0961
        L_0x0689:
            r0 = 20
            if (r9 == r0) goto L_0x072e
            r1 = 21
            if (r9 != r1) goto L_0x0693
            goto L_0x072e
        L_0x0693:
            r0 = 19
            if (r9 != r0) goto L_0x06a0
            java.lang.CharSequence r2 = r7.infoText
            r3 = 0
            r0 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r1 = r15
            goto L_0x0961
        L_0x06a0:
            r0 = 82
            if (r9 != r0) goto L_0x06c0
            r0 = r10
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            boolean r1 = r0.isVideo
            if (r1 == 0) goto L_0x06b1
            r1 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.String r2 = "AttachMediaVideoDeselected"
            goto L_0x06b6
        L_0x06b1:
            r1 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r2 = "AttachMediaPhotoDeselected"
        L_0x06b6:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            r3 = 0
            r0 = 0
            r1 = r15
            goto L_0x0961
        L_0x06c0:
            r0 = 78
            if (r9 == r0) goto L_0x0702
            r0 = 79
            if (r9 != r0) goto L_0x06c9
            goto L_0x0702
        L_0x06c9:
            r0 = 3
            if (r9 != r0) goto L_0x06d7
            r0 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r1 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
            goto L_0x06e1
        L_0x06d7:
            r0 = 2131624977(0x7f0e0411, float:1.8877149E38)
            java.lang.String r1 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
        L_0x06e1:
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r0 = r0.dialogFilters
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x06fa
            r0 = 2131624924(0x7f0e03dc, float:1.8877041E38)
            java.lang.String r1 = "ChatArchivedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = r0
            goto L_0x06fc
        L_0x06fa:
            r0 = 0
            r3 = r0
        L_0x06fc:
            r0 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r1 = r15
            goto L_0x0961
        L_0x0702:
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 78
            if (r9 != r1) goto L_0x0715
            java.lang.String r1 = "PinnedDialogsCount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
            r2 = r1
            goto L_0x071c
        L_0x0715:
            java.lang.String r1 = "UnpinnedDialogsCount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
            r2 = r1
        L_0x071c:
            r3 = 0
            int r1 = r7.currentAction
            r12 = 78
            if (r1 != r12) goto L_0x0727
            r1 = 2131558462(0x7f0d003e, float:1.874224E38)
            goto L_0x072a
        L_0x0727:
            r1 = 2131558468(0x7f0d0044, float:1.8742253E38)
        L_0x072a:
            r0 = r1
            r1 = r15
            goto L_0x0961
        L_0x072e:
            r1 = r11
            org.telegram.messenger.MessagesController$DialogFilter r1 = (org.telegram.messenger.MessagesController.DialogFilter) r1
            r2 = 0
            int r12 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r12 == 0) goto L_0x07fc
            r2 = r5
            boolean r12 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            if (r12 == 0) goto L_0x0752
            int r12 = r7.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            int r20 = org.telegram.messenger.DialogObject.getEncryptedChatId(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r20)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r12.getEncryptedChat(r0)
            long r2 = r0.user_id
        L_0x0752:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r2)
            if (r0 == 0) goto L_0x07ac
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r12 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r12)
            r12 = 20
            if (r9 != r12) goto L_0x078b
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r24 = org.telegram.messenger.UserObject.getFirstName(r0)
            r23 = 0
            r13[r23] = r24
            java.lang.String r12 = r1.name
            r21 = 1
            r13[r21] = r12
            java.lang.String r12 = "FilterUserAddedToExisting"
            r30 = r15
            r15 = 2131625798(0x7f0e0746, float:1.8878814E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r15, r13)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            goto L_0x07ab
        L_0x078b:
            r30 = r15
            r21 = 1
            r23 = 0
            r12 = 2131625799(0x7f0e0747, float:1.8878816E38)
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = org.telegram.messenger.UserObject.getFirstName(r0)
            r15[r23] = r13
            java.lang.String r13 = r1.name
            r15[r21] = r13
            java.lang.String r13 = "FilterUserRemovedFrom"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r13, r12, r15)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
        L_0x07ab:
            goto L_0x07fa
        L_0x07ac:
            r30 = r15
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r12 = -r2
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            r12 = 20
            if (r9 != r12) goto L_0x07de
            r12 = 2131625737(0x7f0e0709, float:1.887869E38)
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = r0.title
            r20 = 0
            r15[r20] = r13
            java.lang.String r13 = r1.name
            r21 = 1
            r15[r21] = r13
            java.lang.String r13 = "FilterChatAddedToExisting"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r13, r12, r15)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            goto L_0x07fa
        L_0x07de:
            r20 = 0
            r21 = 1
            r12 = 2131625738(0x7f0e070a, float:1.8878692E38)
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = r0.title
            r15[r20] = r13
            java.lang.String r13 = r1.name
            r15[r21] = r13
            java.lang.String r13 = "FilterChatRemovedFrom"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r13, r12, r15)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
        L_0x07fa:
            r2 = r12
            goto L_0x084f
        L_0x07fc:
            r30 = r15
            r0 = 20
            if (r9 != r0) goto L_0x0829
            r0 = 2131625741(0x7f0e070d, float:1.8878699E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r10
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r12 = "ChatsSelected"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r12, r2)
            r12 = 0
            r3[r12] = r2
            java.lang.String r2 = r1.name
            r12 = 1
            r3[r12] = r2
            java.lang.String r2 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = r0
            goto L_0x084f
        L_0x0829:
            r0 = 2131625742(0x7f0e070e, float:1.88787E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r10
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.String r12 = "ChatsSelected"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r12, r2)
            r12 = 0
            r3[r12] = r2
            java.lang.String r2 = r1.name
            r12 = 1
            r3[r12] = r2
            java.lang.String r2 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = r0
        L_0x084f:
            r3 = 0
            r0 = 20
            if (r9 != r0) goto L_0x0858
            r0 = 2131558449(0x7f0d0031, float:1.8742214E38)
            goto L_0x085b
        L_0x0858:
            r0 = 2131558450(0x7f0d0032, float:1.8742216E38)
        L_0x085b:
            r1 = r30
            goto L_0x0961
        L_0x085f:
            r30 = r15
        L_0x0861:
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = 9
            if (r9 != r1) goto L_0x0881
            r1 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r0)
            r13 = 0
            r3[r13] = r12
            java.lang.String r12 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x0899
        L_0x0881:
            r2 = 1
            r13 = 0
            r1 = 2131625470(0x7f0e05fe, float:1.8878149E38)
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r13] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x0899:
            r3 = 0
            r0 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r1 = r30
            goto L_0x0961
        L_0x08a1:
            r30 = r15
        L_0x08a3:
            r1 = 40
            if (r0 != r1) goto L_0x08ad
            r0 = 2131628799(0x7f0e12ff, float:1.88849E38)
            java.lang.String r1 = "VoipGroupAudioRecordSaved"
            goto L_0x08b2
        L_0x08ad:
            r0 = 2131628893(0x7f0e135d, float:1.8885092E38)
            java.lang.String r1 = "VoipGroupVideoRecordSaved"
        L_0x08b2:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = r0
            r3 = 0
            r2 = 2131558573(0x7f0d00ad, float:1.8742466E38)
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
            if (r13 < 0) goto L_0x0930
            if (r15 < 0) goto L_0x0930
            if (r13 == r15) goto L_0x0930
            int r0 = r15 + 2
            r12.replace(r15, r0, r14)
            int r0 = r13 + 2
            r12.replace(r13, r0, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r0 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0925 }
            r22 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x091f }
            r1.<init>()     // Catch:{ Exception -> 0x091f }
            r24 = r2
            java.lang.String r2 = "tg://openmessage?user_id="
            r1.append(r2)     // Catch:{ Exception -> 0x091b }
            int r2 = r7.currentAccount     // Catch:{ Exception -> 0x091b }
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x091b }
            r25 = r3
            long r2 = r2.getClientUserId()     // Catch:{ Exception -> 0x0919 }
            r1.append(r2)     // Catch:{ Exception -> 0x0919 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0919 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0919 }
            int r1 = r15 + -2
            r2 = 33
            r12.setSpan(r0, r13, r1, r2)     // Catch:{ Exception -> 0x0919 }
            goto L_0x0936
        L_0x0919:
            r0 = move-exception
            goto L_0x092c
        L_0x091b:
            r0 = move-exception
            r25 = r3
            goto L_0x092c
        L_0x091f:
            r0 = move-exception
            r24 = r2
            r25 = r3
            goto L_0x092c
        L_0x0925:
            r0 = move-exception
            r22 = r1
            r24 = r2
            r25 = r3
        L_0x092c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0936
        L_0x0930:
            r22 = r1
            r24 = r2
            r25 = r3
        L_0x0936:
            r2 = r12
            r0 = r24
            r3 = r25
            r1 = r30
            goto L_0x0961
        L_0x093e:
            r30 = r15
        L_0x0940:
            r1 = 39
            if (r0 != r1) goto L_0x094a
            r0 = 2131628800(0x7f0e1300, float:1.8884903E38)
            java.lang.String r1 = "VoipGroupAudioRecordStarted"
            goto L_0x094f
        L_0x094a:
            r0 = 2131628894(0x7f0e135e, float:1.8885094E38)
            java.lang.String r1 = "VoipGroupVideoRecordStarted"
        L_0x094f:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3 = 0
            r0 = 2131558574(0x7f0d00ae, float:1.8742468E38)
            r12 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r12
            r1 = r30
        L_0x0961:
            android.widget.TextView r12 = r7.infoTextView
            r12.setText(r2)
            if (r0 == 0) goto L_0x09a7
            if (r31 == 0) goto L_0x0972
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.setImageResource(r0)
            r13 = r29
            goto L_0x098e
        L_0x0972:
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.setAnimation(r0, r1, r1)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            org.telegram.ui.Components.RLottieDrawable r12 = r12.getAnimatedDrawable()
            r13 = r29
            r12.setPlayInDirectionOfCustomEndFrame(r13)
            if (r13 == 0) goto L_0x0987
            r15 = r19
            goto L_0x098b
        L_0x0987:
            int r15 = r12.getFramesCount()
        L_0x098b:
            r12.setCustomEndFrame(r15)
        L_0x098e:
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r15 = 0
            r12.setVisibility(r15)
            if (r31 != 0) goto L_0x09b0
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            if (r13 == 0) goto L_0x099d
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x099e
        L_0x099d:
            r15 = 0
        L_0x099e:
            r12.setProgress(r15)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.playAnimation()
            goto L_0x09b0
        L_0x09a7:
            r13 = r29
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r15 = 8
            r12.setVisibility(r15)
        L_0x09b0:
            if (r3 == 0) goto L_0x09f6
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r12
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r4.topMargin = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4.rightMargin = r12
            android.widget.TextView r12 = r7.subinfoTextView
            android.view.ViewGroup$LayoutParams r12 = r12.getLayoutParams()
            r4 = r12
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4.rightMargin = r12
            android.widget.TextView r12 = r7.subinfoTextView
            r12.setText(r3)
            android.widget.TextView r12 = r7.subinfoTextView
            r15 = 0
            r12.setVisibility(r15)
            android.widget.TextView r12 = r7.infoTextView
            r20 = r1
            r1 = 1
            r15 = 1096810496(0x41600000, float:14.0)
            r12.setTextSize(r1, r15)
            android.widget.TextView r1 = r7.infoTextView
            java.lang.String r12 = "fonts/rmedium.ttf"
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r1.setTypeface(r12)
            goto L_0x0a22
        L_0x09f6:
            r20 = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r1
            r1 = 1095761920(0x41500000, float:13.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4.rightMargin = r1
            android.widget.TextView r1 = r7.subinfoTextView
            r12 = 8
            r1.setVisibility(r12)
            android.widget.TextView r1 = r7.infoTextView
            r12 = 1
            r15 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r12, r15)
            android.widget.TextView r1 = r7.infoTextView
            android.graphics.Typeface r12 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r12)
        L_0x0a22:
            android.widget.LinearLayout r1 = r7.undoButton
            r12 = 8
            r1.setVisibility(r12)
            r0 = r4
            r29 = r13
            goto L_0x18ae
        L_0x0a2e:
            r13 = r29
            int r0 = r7.currentAction
            r1 = 45
            if (r0 == r1) goto L_0x1272
            r1 = 46
            if (r0 == r1) goto L_0x1272
            r1 = 47
            if (r0 == r1) goto L_0x1272
            r1 = 51
            if (r0 == r1) goto L_0x1272
            r1 = 50
            if (r0 == r1) goto L_0x1272
            r1 = 52
            if (r0 == r1) goto L_0x1272
            r1 = 53
            if (r0 == r1) goto L_0x1272
            r1 = 54
            if (r0 == r1) goto L_0x1272
            r1 = 55
            if (r0 == r1) goto L_0x1272
            r1 = 56
            if (r0 == r1) goto L_0x1272
            r1 = 57
            if (r0 == r1) goto L_0x1272
            r1 = 58
            if (r0 == r1) goto L_0x1272
            r1 = 59
            if (r0 == r1) goto L_0x1272
            r1 = 60
            if (r0 == r1) goto L_0x1272
            r1 = 71
            if (r0 == r1) goto L_0x1272
            r1 = 70
            if (r0 == r1) goto L_0x1272
            r1 = 75
            if (r0 == r1) goto L_0x1272
            r1 = 76
            if (r0 == r1) goto L_0x1272
            r1 = 41
            if (r0 == r1) goto L_0x1272
            r1 = 78
            if (r0 == r1) goto L_0x1272
            r1 = 79
            if (r0 == r1) goto L_0x1272
            r1 = 61
            if (r0 == r1) goto L_0x1272
            r1 = 80
            if (r0 != r1) goto L_0x0a94
            r29 = r13
            r2 = r28
            goto L_0x1276
        L_0x0a94:
            r1 = 24
            if (r0 == r1) goto L_0x10f9
            r1 = 25
            if (r0 != r1) goto L_0x0aa2
            r29 = r13
            r2 = r28
            goto L_0x10fd
        L_0x0aa2:
            r1 = 11
            if (r0 != r1) goto L_0x0b1d
            r0 = r10
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC.TL_authorization) r0
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131624473(0x7f0e0219, float:1.8876127E38)
            java.lang.String r3 = "AuthAnotherClientOk"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r3 = 36
            r1.setAnimation(r2, r3, r3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r1
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r1
            android.widget.TextView r1 = r7.subinfoTextView
            java.lang.String r2 = r0.app_name
            r1.setText(r2)
            android.widget.TextView r1 = r7.subinfoTextView
            r2 = 0
            r1.setVisibility(r2)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r1.setTextSize(r3, r2)
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
            r29 = r13
            r2 = r28
            goto L_0x126d
        L_0x0b1d:
            r1 = 15
            if (r0 != r1) goto L_0x0CLASSNAME
            r0 = 10000(0x2710, double:4.9407E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.undoTextView
            r1 = 2131626933(0x7f0e0bb5, float:1.8881116E38)
            java.lang.String r2 = "Open"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625734(0x7f0e0706, float:1.8878684E38)
            java.lang.String r2 = "FilterAvailableTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558443(0x7f0d002b, float:1.8742202E38)
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
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r1
            r4.rightMargin = r0
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r1
            android.widget.TextView r1 = r7.subinfoTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            r4 = r1
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r4.rightMargin = r0
            r1 = 2131625733(0x7f0e0705, float:1.8878682E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r12 = r1.indexOf(r3)
            int r3 = r1.lastIndexOf(r3)
            if (r12 < 0) goto L_0x0bc6
            if (r3 < 0) goto L_0x0bc6
            if (r12 == r3) goto L_0x0bc6
            int r15 = r3 + 1
            r2.replace(r3, r15, r14)
            int r15 = r12 + 1
            r2.replace(r12, r15, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r15 = new org.telegram.ui.Components.URLSpanNoUnderline
            r16 = r0
            java.lang.String r0 = "tg://settings/folders"
            r15.<init>(r0)
            int r0 = r3 + -1
            r22 = r1
            r1 = 33
            r2.setSpan(r15, r12, r0, r1)
            goto L_0x0bca
        L_0x0bc6:
            r16 = r0
            r22 = r1
        L_0x0bca:
            android.widget.TextView r0 = r7.subinfoTextView
            r0.setText(r2)
            android.widget.TextView r0 = r7.subinfoTextView
            r1 = 0
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.subinfoTextView
            r0.setSingleLine(r1)
            android.widget.TextView r0 = r7.subinfoTextView
            r15 = 2
            r0.setMaxLines(r15)
            android.widget.LinearLayout r0 = r7.undoButton
            r0.setVisibility(r1)
            android.widget.ImageView r0 = r7.undoImageView
            r15 = 8
            r0.setVisibility(r15)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setVisibility(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 0
            r0.setProgress(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.playAnimation()
            r0 = r4
            r29 = r13
            goto L_0x18ae
        L_0x0CLASSNAME:
            r1 = 16
            if (r0 == r1) goto L_0x0var_
            r1 = 17
            if (r0 != r1) goto L_0x0c0f
            r29 = r13
            r2 = r28
            goto L_0x0var_
        L_0x0c0f:
            r1 = 18
            if (r0 != r1) goto L_0x0ca3
            r0 = r10
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r1 = 4000(0xfa0, float:5.605E-42)
            int r2 = r0.length()
            int r2 = r2 / 50
            int r2 = r2 * 1600
            r3 = 10000(0x2710, float:1.4013E-41)
            int r2 = java.lang.Math.min(r2, r3)
            int r1 = java.lang.Math.max(r1, r2)
            long r1 = (long) r1
            r7.timeLeft = r1
            android.widget.TextView r1 = r7.infoTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r1.setTextSize(r3, r2)
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
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4.rightMargin = r1
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r1
            r1 = 1088421888(0x40e00000, float:7.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.bottomMargin = r1
            r1 = -1
            r4.height = r1
            r1 = 51
            r2 = r28
            r2.gravity = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r2.bottomMargin = r1
            r2.topMargin = r1
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r3 = 0
            r1.setVisibility(r3)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r3 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r12 = 36
            r1.setAnimation(r3, r12, r12)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r3 = 0
            r1.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r1.playAnimation()
            android.widget.TextView r1 = r7.infoTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r3 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r3.<init>()
            r1.setMovementMethod(r3)
            r29 = r13
            goto L_0x126d
        L_0x0ca3:
            r2 = r28
            r1 = 12
            if (r0 != r1) goto L_0x0d53
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625095(0x7f0e0487, float:1.8877388E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131166199(0x7var_f7, float:1.7946637E38)
            r0.setImageResource(r1)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r0
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.rightMargin = r0
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r0
            android.widget.TextView r0 = r7.subinfoTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r4 = r0
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.rightMargin = r0
            r0 = 2131625096(0x7f0e0488, float:1.887739E38)
            java.lang.String r1 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r0)
            r3 = 42
            int r12 = r0.indexOf(r3)
            int r3 = r0.lastIndexOf(r3)
            if (r12 < 0) goto L_0x0d21
            if (r3 < 0) goto L_0x0d21
            if (r12 == r3) goto L_0x0d21
            int r15 = r3 + 1
            r1.replace(r3, r15, r14)
            int r15 = r12 + 1
            r1.replace(r12, r15, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r15 = new org.telegram.ui.Components.URLSpanNoUnderline
            r16 = r0
            java.lang.String r0 = "tg://settings/themes"
            r15.<init>(r0)
            int r0 = r3 + -1
            r22 = r3
            r3 = 33
            r1.setSpan(r15, r12, r0, r3)
            goto L_0x0d25
        L_0x0d21:
            r16 = r0
            r22 = r3
        L_0x0d25:
            android.widget.TextView r0 = r7.subinfoTextView
            r0.setText(r1)
            android.widget.TextView r0 = r7.subinfoTextView
            r3 = 0
            r0.setVisibility(r3)
            android.widget.TextView r0 = r7.subinfoTextView
            r0.setSingleLine(r3)
            android.widget.TextView r0 = r7.subinfoTextView
            r15 = 2
            r0.setMaxLines(r15)
            android.widget.TextView r0 = r7.undoTextView
            r15 = 8
            r0.setVisibility(r15)
            android.widget.LinearLayout r0 = r7.undoButton
            r0.setVisibility(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setVisibility(r3)
            r28 = r2
            r0 = r4
            r29 = r13
            goto L_0x18ae
        L_0x0d53:
            r1 = 2
            if (r0 == r1) goto L_0x0f0b
            r1 = 4
            if (r0 != r1) goto L_0x0d5d
            r29 = r13
            goto L_0x0f0d
        L_0x0d5d:
            r0 = 82
            if (r9 != r0) goto L_0x0e25
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r0
            r0 = r10
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            android.widget.TextView r1 = r7.infoTextView
            boolean r3 = r0.isVideo
            if (r3 == 0) goto L_0x0d76
            r3 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.String r15 = "AttachMediaVideoDeselected"
            goto L_0x0d7b
        L_0x0d76:
            r3 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r15 = "AttachMediaPhotoDeselected"
        L_0x0d7b:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.undoButton
            r3 = 0
            r1.setVisibility(r3)
            android.widget.TextView r1 = r7.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r15 = 1
            r1.setTextSize(r15, r3)
            android.widget.TextView r1 = r7.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r1.setTypeface(r3)
            android.widget.TextView r1 = r7.subinfoTextView
            r3 = 8
            r1.setVisibility(r3)
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            r3 = 0
            r1.setVisibility(r3)
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setRoundRadius(r3)
            java.lang.String r1 = r0.thumbPath
            if (r1 == 0) goto L_0x0dbe
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            java.lang.String r3 = r0.thumbPath
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r12 = 0
            r1.setImage(r3, r12, r15)
            goto L_0x0e21
        L_0x0dbe:
            java.lang.String r1 = r0.path
            if (r1 == 0) goto L_0x0e1a
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            int r3 = r0.orientation
            r15 = 1
            r1.setOrientation(r3, r15)
            boolean r1 = r0.isVideo
            if (r1 == 0) goto L_0x0df4
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r15 = "vthumb://"
            r3.append(r15)
            int r15 = r0.imageId
            r3.append(r15)
            java.lang.String r15 = ":"
            r3.append(r15)
            java.lang.String r15 = r0.path
            r3.append(r15)
            java.lang.String r3 = r3.toString()
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r12 = 0
            r1.setImage(r3, r12, r15)
            goto L_0x0e21
        L_0x0df4:
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r15 = "thumb://"
            r3.append(r15)
            int r15 = r0.imageId
            r3.append(r15)
            java.lang.String r15 = ":"
            r3.append(r15)
            java.lang.String r15 = r0.path
            r3.append(r15)
            java.lang.String r3 = r3.toString()
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r12 = 0
            r1.setImage(r3, r12, r15)
            goto L_0x0e21
        L_0x0e1a:
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r1.setImageDrawable(r3)
        L_0x0e21:
            r29 = r13
            goto L_0x126d
        L_0x0e25:
            r0 = 1110704128(0x42340000, float:45.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            r1 = 0
            r4.rightMargin = r1
            android.widget.TextView r0 = r7.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r12 = 1
            r0.setTextSize(r12, r3)
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
            if (r0 == r1) goto L_0x0ecc
            if (r0 == 0) goto L_0x0ecc
            r1 = 26
            if (r0 != r1) goto L_0x0e67
            r29 = r13
            goto L_0x0ece
        L_0x0e67:
            r1 = 27
            if (r0 != r1) goto L_0x0e7c
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624978(0x7f0e0412, float:1.887715E38)
            java.lang.String r3 = "ChatsDeletedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            r29 = r13
            goto L_0x0edc
        L_0x0e7c:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r0 == 0) goto L_0x0ebb
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            r29 = r13
            long r12 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0eac
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x0eac
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131624830(0x7f0e037e, float:1.887685E38)
            java.lang.String r12 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r1.setText(r3)
            goto L_0x0eba
        L_0x0eac:
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131625986(0x7f0e0802, float:1.8879195E38)
            java.lang.String r12 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r1.setText(r3)
        L_0x0eba:
            goto L_0x0edc
        L_0x0ebb:
            r29 = r13
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624927(0x7f0e03df, float:1.8877048E38)
            java.lang.String r3 = "ChatDeletedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            goto L_0x0edc
        L_0x0ecc:
            r29 = r13
        L_0x0ece:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626039(0x7f0e0837, float:1.8879303E38)
            java.lang.String r3 = "HistoryClearedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
        L_0x0edc:
            int r0 = r7.currentAction
            r1 = 81
            if (r0 == r1) goto L_0x126d
            r0 = 0
        L_0x0ee3:
            int r1 = r35.size()
            if (r0 >= r1) goto L_0x126d
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Object r3 = r8.get(r0)
            java.lang.Long r3 = (java.lang.Long) r3
            long r12 = r3.longValue()
            int r3 = r7.currentAction
            if (r3 == 0) goto L_0x0var_
            r15 = 26
            if (r3 != r15) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r3 = 0
            goto L_0x0var_
        L_0x0var_:
            r3 = 1
        L_0x0var_:
            r1.addDialogAction(r12, r3)
            int r0 = r0 + 1
            goto L_0x0ee3
        L_0x0f0b:
            r29 = r13
        L_0x0f0d:
            r1 = 2
            if (r9 != r1) goto L_0x0f1f
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            goto L_0x0f2d
        L_0x0f1f:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624977(0x7f0e0411, float:1.8877149E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
        L_0x0f2d:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r0
            r0 = 1095761920(0x41500000, float:13.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4.topMargin = r0
            r1 = 0
            r4.rightMargin = r1
            android.widget.TextView r0 = r7.infoTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r12 = 1
            r0.setTextSize(r12, r3)
            android.widget.LinearLayout r0 = r7.undoButton
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.infoTextView
            android.graphics.Typeface r3 = android.graphics.Typeface.DEFAULT
            r0.setTypeface(r3)
            android.widget.TextView r0 = r7.subinfoTextView
            r3 = 8
            r0.setVisibility(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.setVisibility(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558421(0x7f0d0015, float:1.8742157E38)
            r3 = 36
            r0.setAnimation(r1, r3, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 0
            r0.setProgress(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.playAnimation()
            goto L_0x126d
        L_0x0var_:
            r29 = r13
            r2 = r28
        L_0x0var_:
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r1)
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
            if (r1 == 0) goto L_0x0fbe
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131625374(0x7f0e059e, float:1.8877954E38)
            java.lang.String r12 = "DiceInfo2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r1.setText(r3)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r3 = 2131165420(0x7var_ec, float:1.7945057E38)
            r1.setImageResource(r3)
            goto L_0x106c
        L_0x0fbe:
            java.lang.String r1 = "🎯"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0fd9
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131625214(0x7f0e04fe, float:1.887763E38)
            java.lang.String r12 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r1.setText(r3)
            goto L_0x1038
        L_0x0fd9:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r1.append(r3)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = org.telegram.messenger.LocaleController.getServerString(r1)
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L_0x100d
            android.widget.TextView r3 = r7.infoTextView
            android.text.TextPaint r12 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r12 = r12.getFontMetricsInt()
            r13 = 1096810496(0x41600000, float:14.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r13 = 0
            java.lang.CharSequence r12 = org.telegram.messenger.Emoji.replaceEmoji(r1, r12, r15, r13)
            r3.setText(r12)
            goto L_0x1038
        L_0x100d:
            r13 = 0
            android.widget.TextView r3 = r7.infoTextView
            r15 = 1
            java.lang.Object[] r12 = new java.lang.Object[r15]
            r12[r13] = r0
            java.lang.String r15 = "DiceEmojiInfo"
            r13 = 2131625373(0x7f0e059d, float:1.8877952E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r15, r13, r12)
            android.widget.TextView r13 = r7.infoTextView
            android.text.TextPaint r13 = r13.getPaint()
            android.graphics.Paint$FontMetricsInt r13 = r13.getFontMetricsInt()
            r16 = r1
            r15 = 1096810496(0x41600000, float:14.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r15 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r12, r13, r1, r15)
            r3.setText(r1)
        L_0x1038:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            org.telegram.messenger.Emoji$EmojiDrawable r3 = org.telegram.messenger.Emoji.getEmojiDrawable(r0)
            r1.setImageDrawable(r3)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.FIT_XY
            r1.setScaleType(r3)
            r1 = 1096810496(0x41600000, float:14.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.topMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.bottomMargin = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.leftMargin = r3
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.width = r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.height = r1
        L_0x106c:
            android.widget.TextView r1 = r7.undoTextView
            r3 = 2131627973(0x7f0e0fc5, float:1.8883226E38)
            java.lang.String r12 = "SendDice"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r1.setText(r3)
            int r1 = r7.currentAction
            r3 = 16
            if (r1 != r3) goto L_0x10bf
            android.widget.TextView r1 = r7.undoTextView
            android.text.TextPaint r1 = r1.getPaint()
            android.widget.TextView r3 = r7.undoTextView
            java.lang.CharSequence r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            float r1 = r1.measureText(r3)
            double r12 = (double) r1
            double r12 = java.lang.Math.ceil(r12)
            int r1 = (int) r12
            r3 = 1104150528(0x41d00000, float:26.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r3
            android.widget.TextView r3 = r7.undoTextView
            r12 = 0
            r3.setVisibility(r12)
            android.widget.TextView r3 = r7.undoTextView
            java.lang.String r13 = "undo_cancelColor"
            int r13 = r7.getThemedColor(r13)
            r3.setTextColor(r13)
            android.widget.ImageView r3 = r7.undoImageView
            r13 = 8
            r3.setVisibility(r13)
            android.widget.LinearLayout r3 = r7.undoButton
            r3.setVisibility(r12)
            goto L_0x10cf
        L_0x10bf:
            r13 = 8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r26)
            android.widget.TextView r3 = r7.undoTextView
            r3.setVisibility(r13)
            android.widget.LinearLayout r3 = r7.undoButton
            r3.setVisibility(r13)
        L_0x10cf:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r3
            r4.rightMargin = r1
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.topMargin = r3
            r3 = 1088421888(0x40e00000, float:7.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.bottomMargin = r3
            r3 = -1
            r4.height = r3
            android.widget.TextView r3 = r7.subinfoTextView
            r12 = 8
            r3.setVisibility(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 0
            r3.setVisibility(r12)
            goto L_0x126d
        L_0x10f9:
            r29 = r13
            r2 = r28
        L_0x10fd:
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = r11
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            android.widget.ImageView r3 = r7.undoImageView
            r12 = 8
            r3.setVisibility(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 0
            r3.setVisibility(r12)
            java.lang.String r3 = "undo_infoColor"
            if (r0 == 0) goto L_0x11d6
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
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "BODY.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "Wibe Big.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "Wibe Big 3.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r3 = r7.getThemedColor(r3)
            java.lang.String r13 = "Wibe Small.**"
            r12.setLayerColor(r13, r3)
            android.widget.TextView r3 = r7.infoTextView
            r12 = 2131627553(0x7f0e0e21, float:1.8882374E38)
            java.lang.String r13 = "ProximityAlertSet"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558467(0x7f0d0043, float:1.874225E38)
            r13 = 28
            r15 = 28
            r3.setAnimation(r12, r13, r15)
            android.widget.TextView r3 = r7.subinfoTextView
            r12 = 0
            r3.setVisibility(r12)
            android.widget.TextView r3 = r7.subinfoTextView
            r3.setSingleLine(r12)
            android.widget.TextView r3 = r7.subinfoTextView
            r12 = 3
            r3.setMaxLines(r12)
            if (r1 == 0) goto L_0x11ab
            android.widget.TextView r3 = r7.subinfoTextView
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r16 = org.telegram.messenger.UserObject.getFirstName(r1)
            r20 = 0
            r15[r20] = r16
            float r12 = (float) r0
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatDistance(r12, r13)
            r13 = 1
            r15[r13] = r12
            java.lang.String r12 = "ProximityAlertSetInfoUser"
            r13 = 2131627555(0x7f0e0e23, float:1.8882378E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            r3.setText(r12)
            goto L_0x11c5
        L_0x11ab:
            android.widget.TextView r3 = r7.subinfoTextView
            r13 = 1
            java.lang.Object[] r15 = new java.lang.Object[r13]
            float r13 = (float) r0
            r12 = 2
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatDistance(r13, r12)
            r12 = 0
            r15[r12] = r13
            java.lang.String r12 = "ProximityAlertSetInfoGroup2"
            r13 = 2131627554(0x7f0e0e22, float:1.8882376E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            r3.setText(r12)
        L_0x11c5:
            android.widget.LinearLayout r3 = r7.undoButton
            r12 = 8
            r3.setVisibility(r12)
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.topMargin = r3
            goto L_0x125b
        L_0x11d6:
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
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "Body Main.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "Body Top.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "Line.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r13 = r7.getThemedColor(r3)
            java.lang.String r15 = "Curve Big.**"
            r12.setLayerColor(r15, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            int r3 = r7.getThemedColor(r3)
            java.lang.String r13 = "Curve Small.**"
            r12.setLayerColor(r13, r3)
            r3 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.topMargin = r12
            android.widget.TextView r3 = r7.infoTextView
            r12 = 2131627552(0x7f0e0e20, float:1.8882372E38)
            java.lang.String r13 = "ProximityAlertCancelled"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558461(0x7f0d003d, float:1.8742238E38)
            r13 = 28
            r15 = 28
            r3.setAnimation(r12, r13, r15)
            android.widget.TextView r3 = r7.subinfoTextView
            r12 = 8
            r3.setVisibility(r12)
            android.widget.TextView r3 = r7.undoTextView
            java.lang.String r12 = "undo_cancelColor"
            int r12 = r7.getThemedColor(r12)
            r3.setTextColor(r12)
            android.widget.LinearLayout r3 = r7.undoButton
            r12 = 0
            r3.setVisibility(r12)
        L_0x125b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 0
            r3.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r3.playAnimation()
        L_0x126d:
            r28 = r2
            r0 = r4
            goto L_0x18ae
        L_0x1272:
            r29 = r13
            r2 = r28
        L_0x1276:
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
            int r3 = r7.currentAction
            r12 = 76
            r13 = 1091567616(0x41100000, float:9.0)
            if (r3 != r12) goto L_0x12c0
            android.widget.TextView r3 = r7.infoTextView
            r12 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r15 = "BroadcastGroupConvertSuccess"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558453(0x7f0d0035, float:1.8742222E38)
            r15 = 36
            r3.setAnimation(r12, r15, r15)
            r17 = 1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r4.topMargin = r3
            android.widget.TextView r3 = r7.infoTextView
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r3.setTextSize(r13, r12)
            r28 = r2
            goto L_0x186f
        L_0x12c0:
            r12 = 75
            if (r3 != r12) goto L_0x12f0
            android.widget.TextView r3 = r7.infoTextView
            r12 = 2131625965(0x7f0e07ed, float:1.8879153E38)
            java.lang.String r15 = "GigagroupConvertCancelHint"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558423(0x7f0d0017, float:1.8742161E38)
            r15 = 36
            r3.setAnimation(r12, r15, r15)
            r17 = 1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r4.topMargin = r3
            android.widget.TextView r3 = r7.infoTextView
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r3.setTextSize(r13, r12)
            r28 = r2
            goto L_0x186f
        L_0x12f0:
            r12 = 70
            if (r9 != r12) goto L_0x1348
            r3 = r10
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            r12 = r11
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            android.widget.TextView r15 = r7.subinfoTextView
            r13 = 0
            r15.setSingleLine(r13)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatTTLString(r12)
            android.widget.TextView r13 = r7.infoTextView
            r32 = r0
            r1 = 1
            java.lang.Object[] r0 = new java.lang.Object[r1]
            r1 = 0
            r0[r1] = r15
            java.lang.String r1 = "AutoDeleteHintOnText"
            r28 = r2
            r2 = 2131624494(0x7f0e022e, float:1.887617E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r2, r0)
            r13.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558447(0x7f0d002f, float:1.874221E38)
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
            r0 = r32
            goto L_0x186f
        L_0x1348:
            r32 = r0
            r28 = r2
            r0 = 71
            if (r3 != r0) goto L_0x1382
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624493(0x7f0e022d, float:1.8876167E38)
            java.lang.String r2 = "AutoDeleteHintOffText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558446(0x7f0d002e, float:1.8742208E38)
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
            goto L_0x186d
        L_0x1382:
            r0 = 45
            if (r3 != r0) goto L_0x13b4
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626089(0x7f0e0869, float:1.8879404E38)
            java.lang.String r2 = "ImportMutualError"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558442(0x7f0d002a, float:1.87422E38)
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
            r0 = r32
            goto L_0x186f
        L_0x13b4:
            r0 = 46
            if (r3 != r0) goto L_0x13e6
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626090(0x7f0e086a, float:1.8879406E38)
            java.lang.String r2 = "ImportNotAdmin"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558442(0x7f0d002a, float:1.87422E38)
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
            r0 = r32
            goto L_0x186f
        L_0x13e6:
            r0 = 47
            if (r3 != r0) goto L_0x1424
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626112(0x7f0e0880, float:1.887945E38)
            java.lang.String r2 = "ImportedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558473(0x7f0d0049, float:1.8742263E38)
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
            r12 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r1, r12)
            r0 = r32
            goto L_0x186f
        L_0x1424:
            r12 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r3 != r0) goto L_0x1450
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624454(0x7f0e0206, float:1.8876088E38)
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
            goto L_0x186d
        L_0x1450:
            r0 = 50
            if (r3 != r0) goto L_0x147a
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624453(0x7f0e0205, float:1.8876086E38)
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
            goto L_0x186d
        L_0x147a:
            r0 = 52
            if (r3 == r0) goto L_0x17c9
            r0 = 56
            if (r3 == r0) goto L_0x17c9
            r0 = 57
            if (r3 == r0) goto L_0x17c9
            r0 = 58
            if (r3 == r0) goto L_0x17c9
            r0 = 59
            if (r3 == r0) goto L_0x17c9
            r0 = 60
            if (r3 == r0) goto L_0x17c9
            r0 = 80
            if (r3 != r0) goto L_0x1498
            goto L_0x17c9
        L_0x1498:
            r0 = 54
            if (r3 != r0) goto L_0x14c2
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624866(0x7f0e03a2, float:1.8876924E38)
            java.lang.String r2 = "ChannelNotifyMembersInfoOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558515(0x7f0d0073, float:1.8742348E38)
            r2 = 30
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            goto L_0x186d
        L_0x14c2:
            r0 = 55
            if (r3 != r0) goto L_0x14ec
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624865(0x7f0e03a1, float:1.8876922E38)
            java.lang.String r2 = "ChannelNotifyMembersInfoOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r2 = 30
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            goto L_0x186d
        L_0x14ec:
            r0 = 41
            if (r3 != r0) goto L_0x15a2
            if (r11 != 0) goto L_0x156d
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.clientUserId
            int r2 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x1512
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626135(0x7f0e0897, float:1.8879498E38)
            java.lang.String r2 = "InvLinkToSavedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setText(r1)
            goto L_0x1592
        L_0x1512:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r0 == 0) goto L_0x1542
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626134(0x7f0e0896, float:1.8879496E38)
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = r0.title
            r15 = 0
            r13[r15] = r3
            java.lang.String r3 = "InvLinkToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r13)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x1592
        L_0x1542:
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626136(0x7f0e0898, float:1.88795E38)
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r0)
            r15 = 0
            r13[r15] = r3
            java.lang.String r3 = "InvLinkToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r13)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x1592
        L_0x156d:
            r0 = r11
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626133(0x7f0e0895, float:1.8879494E38)
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0)
            r15 = 0
            r13[r15] = r3
            java.lang.String r3 = "InvLinkToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r13)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x1592:
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558429(0x7f0d001d, float:1.8742174E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            goto L_0x186d
        L_0x15a2:
            r0 = 53
            if (r3 != r0) goto L_0x16fc
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r11 != 0) goto L_0x169e
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x15f1
            int r1 = r0.intValue()
            r2 = 1
            if (r1 != r2) goto L_0x15d1
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625935(0x7f0e07cf, float:1.8879092E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x15e3
        L_0x15d1:
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625939(0x7f0e07d3, float:1.88791E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x15e3:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = r32
            goto L_0x16f5
        L_0x15f1:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r1 == 0) goto L_0x1643
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r2 = -r5
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r0.intValue()
            r3 = 1
            if (r2 != r3) goto L_0x1628
            android.widget.TextView r2 = r7.infoTextView
            r13 = 2131625934(0x7f0e07ce, float:1.887909E38)
            java.lang.Object[] r15 = new java.lang.Object[r3]
            java.lang.String r12 = r1.title
            r16 = 0
            r15[r16] = r12
            java.lang.String r12 = "FwdMessageToGroup"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            r2.setText(r12)
            goto L_0x1642
        L_0x1628:
            r16 = 0
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131625938(0x7f0e07d2, float:1.8879098E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = r1.title
            r13[r16] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x1642:
            goto L_0x1691
        L_0x1643:
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r0.intValue()
            r3 = 1
            if (r2 != r3) goto L_0x1675
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131625936(0x7f0e07d0, float:1.8879094E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r15 = org.telegram.messenger.UserObject.getFirstName(r1)
            r16 = 0
            r13[r16] = r15
            java.lang.String r15 = "FwdMessageToUser"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r15, r12, r13)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            r2.setText(r12)
            goto L_0x1691
        L_0x1675:
            r16 = 0
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131625940(0x7f0e07d4, float:1.8879102E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r1)
            r13[r16] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x1691:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = 300(0x12c, double:1.48E-321)
            goto L_0x16f5
        L_0x169e:
            r1 = r11
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r0.intValue()
            r3 = 1
            if (r2 != r3) goto L_0x16ca
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131625933(0x7f0e07cd, float:1.8879088E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r1)
            r15 = 0
            r13[r15] = r3
            java.lang.String r3 = "FwdMessageToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            goto L_0x16e8
        L_0x16ca:
            android.widget.TextView r2 = r7.infoTextView
            r3 = 2131625937(0x7f0e07d1, float:1.8879096E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = "Chats"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
            r15 = 0
            r13[r15] = r12
            java.lang.String r12 = "FwdMessagesToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x16e8:
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r3 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r12 = 30
            r2.setAnimation(r3, r12, r12)
            r2 = 300(0x12c, double:1.48E-321)
            r1 = r2
        L_0x16f5:
            r12 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r12
            r0 = r1
            goto L_0x186f
        L_0x16fc:
            r0 = 61
            if (r3 != r0) goto L_0x186d
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r11 != 0) goto L_0x1794
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x172f
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131624599(0x7f0e0297, float:1.8876382E38)
            java.lang.String r3 = "BackgroundToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558512(0x7f0d0070, float:1.8742342E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x17c3
        L_0x172f:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r1 == 0) goto L_0x175f
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r2 = -r5
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            android.widget.TextView r2 = r7.infoTextView
            r3 = 2131624598(0x7f0e0296, float:1.887638E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = r1.title
            r15 = 0
            r13[r15] = r12
            java.lang.String r12 = "BackgroundToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            goto L_0x1789
        L_0x175f:
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            android.widget.TextView r2 = r7.infoTextView
            r3 = 2131624600(0x7f0e0298, float:1.8876384E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r1)
            r15 = 0
            r13[r15] = r12
            java.lang.String r12 = "BackgroundToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x1789:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x17c3
        L_0x1794:
            r1 = r11
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.widget.TextView r2 = r7.infoTextView
            r3 = 2131624597(0x7f0e0295, float:1.8876378E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = "Chats"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
            r15 = 0
            r13[r15] = r12
            java.lang.String r12 = "BackgroundToChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r3 = 2131558451(0x7f0d0033, float:1.8742218E38)
            r12 = 30
            r2.setAnimation(r3, r12, r12)
        L_0x17c3:
            r1 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r1
            goto L_0x186d
        L_0x17c9:
            boolean r0 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r0 != 0) goto L_0x17d0
            return
        L_0x17d0:
            r0 = 2131558432(0x7f0d0020, float:1.874218E38)
            int r1 = r7.currentAction
            r2 = 80
            if (r1 != r2) goto L_0x17e9
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625501(0x7f0e061d, float:1.8878212E38)
            java.lang.String r3 = "EmailCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x1859
        L_0x17e9:
            r2 = 60
            if (r1 != r2) goto L_0x17fc
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131627327(0x7f0e0d3f, float:1.8881915E38)
            java.lang.String r3 = "PhoneCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x1859
        L_0x17fc:
            r2 = 56
            if (r1 != r2) goto L_0x180f
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131628613(0x7f0e1245, float:1.8884524E38)
            java.lang.String r3 = "UsernameCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x1859
        L_0x180f:
            r2 = 57
            if (r1 != r2) goto L_0x1822
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626022(0x7f0e0826, float:1.8879268E38)
            java.lang.String r3 = "HashtagCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x1859
        L_0x1822:
            r2 = 52
            if (r1 != r2) goto L_0x1835
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626470(0x7f0e09e6, float:1.8880177E38)
            java.lang.String r3 = "MessageCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x1859
        L_0x1835:
            r2 = 59
            if (r1 != r2) goto L_0x184b
            r0 = 2131558570(0x7f0d00aa, float:1.874246E38)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626280(0x7f0e0928, float:1.8879792E38)
            java.lang.String r3 = "LinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x1859
        L_0x184b:
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131628370(0x7f0e1152, float:1.888403E38)
            java.lang.String r3 = "TextCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x1859:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 30
            r1.setAnimation(r0, r2, r2)
            r1 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r1
            android.widget.TextView r1 = r7.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1
            r1.setTextSize(r3, r2)
        L_0x186d:
            r0 = r32
        L_0x186f:
            android.widget.TextView r2 = r7.subinfoTextView
            r3 = 8
            r2.setVisibility(r3)
            android.widget.TextView r2 = r7.undoTextView
            java.lang.String r12 = "undo_cancelColor"
            int r12 = r7.getThemedColor(r12)
            r2.setTextColor(r12)
            android.widget.LinearLayout r2 = r7.undoButton
            r2.setVisibility(r3)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4.rightMargin = r2
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r3 = 0
            r2.setProgress(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r2.playAnimation()
            r2 = 0
            int r12 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r12 <= 0) goto L_0x18ad
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r3.<init>(r7)
            r2.postDelayed(r3, r0)
        L_0x18ad:
            r0 = r4
        L_0x18ae:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r2 = r7.infoTextView
            java.lang.CharSequence r2 = r2.getText()
            r1.append(r2)
            android.widget.TextView r2 = r7.subinfoTextView
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x18db
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = ". "
            r2.append(r3)
            android.widget.TextView r3 = r7.subinfoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            java.lang.String r14 = r2.toString()
        L_0x18db:
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r34.isMultilineSubInfo()
            if (r1 == 0) goto L_0x1931
            android.view.ViewParent r1 = r34.getParent()
            r12 = r1
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r1 = r12.getMeasuredWidth()
            if (r1 != 0) goto L_0x18fc
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r2.x
        L_0x18fc:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r13 = r1 - r2
            android.widget.TextView r2 = r7.subinfoTextView
            r1 = 1073741824(0x40000000, float:2.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r1)
            r4 = 0
            r1 = 0
            int r14 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r1)
            r15 = 0
            r1 = r34
            r16 = r28
            r18 = r29
            r24 = r5
            r5 = r14
            r6 = r15
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            android.widget.TextView r1 = r7.subinfoTextView
            int r1 = r1.getMeasuredHeight()
            r2 = 1108606976(0x42140000, float:37.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            r7.undoViewHeight = r1
            goto L_0x19d4
        L_0x1931:
            r24 = r5
            r16 = r28
            r18 = r29
            boolean r1 = r34.hasSubInfo()
            if (r1 == 0) goto L_0x1947
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.undoViewHeight = r1
            goto L_0x19d4
        L_0x1947:
            android.view.ViewParent r1 = r34.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x19d4
            android.view.ViewParent r1 = r34.getParent()
            r12 = r1
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r1 = r12.getMeasuredWidth()
            int r2 = r12.getPaddingLeft()
            int r1 = r1 - r2
            int r2 = r12.getPaddingRight()
            int r1 = r1 - r2
            if (r1 > 0) goto L_0x196a
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r2.x
        L_0x196a:
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
            r1 = r34
            r1.measureChildWithMargins(r2, r3, r4, r5, r6)
            android.widget.TextView r1 = r7.infoTextView
            int r1 = r1.getMeasuredHeight()
            int r2 = r7.currentAction
            r3 = 16
            if (r2 == r3) goto L_0x199e
            r3 = 17
            if (r2 == r3) goto L_0x199e
            r3 = 18
            if (r2 != r3) goto L_0x199b
            goto L_0x199e
        L_0x199b:
            r15 = 1105199104(0x41e00000, float:28.0)
            goto L_0x19a0
        L_0x199e:
            r15 = 1096810496(0x41600000, float:14.0)
        L_0x19a0:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r1 = r1 + r2
            r7.undoViewHeight = r1
            int r2 = r7.currentAction
            r3 = 18
            if (r2 != r3) goto L_0x19ba
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r7.undoViewHeight = r1
            goto L_0x19d4
        L_0x19ba:
            r3 = 25
            if (r2 != r3) goto L_0x19cb
            r2 = 1112014848(0x42480000, float:50.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r7.undoViewHeight = r1
            goto L_0x19d4
        L_0x19cb:
            if (r17 == 0) goto L_0x19d4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r1 = r1 - r2
            r7.undoViewHeight = r1
        L_0x19d4:
            int r1 = r34.getVisibility()
            if (r1 == 0) goto L_0x1a37
            r1 = 0
            r7.setVisibility(r1)
            boolean r1 = r7.fromTop
            if (r1 == 0) goto L_0x19e5
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x19e7
        L_0x19e5:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x19e7:
            int r2 = r7.enterOffsetMargin
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
            if (r4 == 0) goto L_0x1a04
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1a06
        L_0x1a04:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x1a06:
            int r6 = r7.enterOffsetMargin
            int r12 = r7.undoViewHeight
            int r6 = r6 + r12
            float r6 = (float) r6
            float r5 = r5 * r6
            r6 = 0
            r2[r6] = r5
            if (r4 == 0) goto L_0x1a16
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x1a18
        L_0x1a16:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x1a18:
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
        L_0x1a37:
            return
        L_0x1a38:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(java.util.ArrayList, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* renamed from: lambda$showWithAction$2$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m4505lambda$showWithAction$2$orgtelegramuiComponentsUndoView(View view) {
        hide(false, 1);
    }

    static /* synthetic */ boolean lambda$showWithAction$3(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$showWithAction$6$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m4508lambda$showWithAction$6$orgtelegramuiComponentsUndoView(TLRPC.Message message, View v) {
        hide(true, 1);
        TLRPC.TL_payments_getPaymentReceipt req = new TLRPC.TL_payments_getPaymentReceipt();
        req.msg_id = message.id;
        req.peer = this.parentFragment.getMessagesController().getInputPeer(message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(req, new UndoView$$ExternalSyntheticLambda7(this), 2);
    }

    /* renamed from: lambda$showWithAction$5$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m4507lambda$showWithAction$5$orgtelegramuiComponentsUndoView(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new UndoView$$ExternalSyntheticLambda6(this, response));
    }

    /* renamed from: lambda$showWithAction$4$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m4506lambda$showWithAction$4$orgtelegramuiComponentsUndoView(TLObject response) {
        if (response instanceof TLRPC.TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response));
        }
    }

    /* renamed from: lambda$showWithAction$7$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m4509lambda$showWithAction$7$orgtelegramuiComponentsUndoView() {
        this.leftImageView.performHapticFeedback(3, 2);
    }

    public void setEnterOffsetMargin(int enterOffsetMargin2) {
        this.enterOffsetMargin = enterOffsetMargin2;
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
        Canvas canvas2 = canvas;
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float bottom = (((float) getMeasuredHeight()) - this.enterOffset) + ((float) this.enterOffsetMargin) + ((float) AndroidUtilities.dp(1.0f));
            if (bottom > 0.0f) {
                canvas2.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), bottom);
                super.dispatchDraw(canvas);
            }
            this.backgroundDrawable.draw(canvas2);
            canvas.restore();
        } else {
            this.backgroundDrawable.draw(canvas2);
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
                    canvas2.translate(this.rect.centerX() - ((float) (this.textWidth / 2)), ((float) AndroidUtilities.dp(17.2f)) + (((float) AndroidUtilities.dp(10.0f)) * this.timeReplaceProgress));
                    this.timeLayoutOut.draw(canvas2);
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
                canvas2.translate(this.rect.centerX() - ((float) (this.textWidth / 2)), ((float) AndroidUtilities.dp(17.2f)) - (((float) AndroidUtilities.dp(10.0f)) * (1.0f - this.timeReplaceProgress)));
                this.timeLayout.draw(canvas2);
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
        if (this.currentAction != 82) {
            invalidate();
        }
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
        setTranslationY(((this.enterOffset - ((float) this.enterOffsetMargin)) + ((float) AndroidUtilities.dp(8.0f))) - this.additionalTranslationY);
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
