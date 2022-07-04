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
        this.currentAction = -1;
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
    public /* synthetic */ void m1536lambda$new$0$orgtelegramuiComponentsUndoView(View v) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v193, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r12v130, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0986  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x09c5  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x09d0  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0a14  */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x18ea  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x1911  */
    /* JADX WARNING: Removed duplicated region for block: B:628:0x1957  */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x1a00  */
    /* JADX WARNING: Removed duplicated region for block: B:679:? A[RETURN, SYNTHETIC] */
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
            r1 = 33
            if (r0 != 0) goto L_0x0037
            int r0 = r7.currentAction
            r2 = 52
            if (r0 == r2) goto L_0x0036
            r2 = 56
            if (r0 == r2) goto L_0x0036
            r2 = 57
            if (r0 == r2) goto L_0x0036
            r2 = 58
            if (r0 == r2) goto L_0x0036
            r2 = 59
            if (r0 == r2) goto L_0x0036
            r2 = 60
            if (r0 == r2) goto L_0x0036
            r2 = 80
            if (r0 == r2) goto L_0x0036
            if (r0 != r1) goto L_0x0037
        L_0x0036:
            return
        L_0x0037:
            java.lang.Runnable r0 = r7.currentActionRunnable
            if (r0 == 0) goto L_0x003e
            r0.run()
        L_0x003e:
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
            r2 = 2131628728(0x7f0e12b8, float:1.8884757E38)
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
            if (r12 != 0) goto L_0x00e1
            if (r13 == 0) goto L_0x00e5
        L_0x00e1:
            int r1 = ACTION_RINGTONE_ADDED
            if (r9 != r1) goto L_0x00f1
        L_0x00e5:
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda1
            r1.<init>(r7)
            r7.setOnClickListener(r1)
            r7.setOnTouchListener(r0)
            goto L_0x00f9
        L_0x00f1:
            r7.setOnClickListener(r0)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4 r1 = org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda4.INSTANCE
            r7.setOnTouchListener(r1)
        L_0x00f9:
            android.widget.TextView r1 = r7.infoTextView
            r1.setMovementMethod(r0)
            boolean r1 = r34.isTooltipAction()
            java.lang.String r14 = ""
            r26 = 1090519040(0x41000000, float:8.0)
            r27 = 1114112000(0x42680000, float:58.0)
            r28 = r2
            r29 = r3
            if (r1 == 0) goto L_0x0a4c
            r1 = 36
            r31 = 0
            int r15 = ACTION_RINGTONE_ADDED
            if (r9 != r15) goto L_0x0143
            android.widget.TextView r2 = r7.subinfoTextView
            r3 = 0
            r2.setSingleLine(r3)
            r2 = 2131628385(0x7f0e1161, float:1.8884061E38)
            java.lang.String r3 = "SoundAdded"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131628386(0x7f0e1162, float:1.8884063E38)
            java.lang.String r15 = "SoundAddedSubtitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceSingleTag(r3, r12)
            r7.currentActionRunnable = r0
            r0 = 2131558531(0x7f0d0083, float:1.874238E38)
            r20 = r0
            r15 = r1
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x0143:
            r15 = r1
            r1 = 74
            if (r9 != r1) goto L_0x0170
            android.widget.TextView r0 = r7.subinfoTextView
            r1 = 0
            r0.setSingleLine(r1)
            r0 = 2131627938(0x7f0e0fa2, float:1.8883155E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 2131627947(0x7f0e0fab, float:1.8883173E38)
            java.lang.Object[] r3 = new java.lang.Object[r1]
            java.lang.String r1 = "ReportSentInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            r0 = 2131558460(0x7f0d003c, float:1.8742236E38)
            r20 = r0
            r0 = 4000(0xfa0, double:1.9763E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x0170:
            r1 = 34
            if (r9 != r1) goto L_0x01e8
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = r11
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            boolean r20 = org.telegram.messenger.ChatObject.isChannelOrGiga(r1)
            if (r20 == 0) goto L_0x019c
            r3 = 1
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r21 = org.telegram.messenger.UserObject.getFirstName(r0)
            r22 = 0
            r2[r22] = r21
            java.lang.String r3 = "VoipChannelInvitedUser"
            r24 = r1
            r1 = 2131628972(0x7f0e13ac, float:1.8885252E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x01b7
        L_0x019c:
            r24 = r1
            r22 = 0
            r1 = 2131629061(0x7f0e1405, float:1.8885432E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r22] = r2
            java.lang.String r2 = "VoipGroupInvitedUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x01b7:
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
            goto L_0x097f
        L_0x01e8:
            r1 = 44
            if (r9 != r1) goto L_0x0295
            r0 = r11
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x022e
            r1 = r10
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r2 == 0) goto L_0x0215
            r3 = 1
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r21 = org.telegram.messenger.UserObject.getFirstName(r1)
            r22 = 0
            r2[r22] = r21
            java.lang.String r3 = "VoipChannelUserJoined"
            r12 = 2131628997(0x7f0e13c5, float:1.8885302E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r12, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x022d
        L_0x0215:
            r22 = 0
            r2 = 2131629012(0x7f0e13d4, float:1.8885333E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r1)
            r12[r22] = r3
            java.lang.String r3 = "VoipChatUserJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r12)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x022d:
            goto L_0x0264
        L_0x022e:
            r1 = r10
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            boolean r2 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r2 == 0) goto L_0x024e
            r2 = 2131628966(0x7f0e13a6, float:1.888524E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r3 = r1.title
            r20 = 0
            r12[r20] = r3
            java.lang.String r3 = "VoipChannelChatJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r12)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            goto L_0x0264
        L_0x024e:
            r20 = 0
            r2 = 2131629002(0x7f0e13ca, float:1.8885313E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r3 = r1.title
            r12[r20] = r3
            java.lang.String r3 = "VoipChatChatJoined"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r12)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
        L_0x0264:
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
            goto L_0x097f
        L_0x0295:
            r1 = 37
            if (r9 != r1) goto L_0x0317
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setTextSize(r1)
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x02bf
            r1 = r10
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            r0.setInfo((org.telegram.tgnet.TLRPC.User) r1)
            org.telegram.ui.Components.BackupImageView r2 = r7.avatarImageView
            r2.setForUserOrChat(r1, r0)
            java.lang.String r2 = r1.first_name
            java.lang.String r3 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            goto L_0x02cd
        L_0x02bf:
            r1 = r10
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r1)
            org.telegram.ui.Components.BackupImageView r2 = r7.avatarImageView
            r2.setForUserOrChat(r1, r0)
            java.lang.String r2 = r1.title
            r1 = r2
        L_0x02cd:
            r2 = r11
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            boolean r3 = org.telegram.messenger.ChatObject.isChannelOrGiga(r2)
            if (r3 == 0) goto L_0x02ec
            r12 = 1
            java.lang.Object[] r3 = new java.lang.Object[r12]
            r12 = 0
            r3[r12] = r1
            java.lang.String r12 = "VoipChannelUserChanged"
            r22 = r0
            r0 = 2131628996(0x7f0e13c4, float:1.88853E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            goto L_0x0301
        L_0x02ec:
            r22 = r0
            r0 = 2131629125(0x7f0e1445, float:1.8885562E38)
            r3 = 1
            java.lang.Object[] r12 = new java.lang.Object[r3]
            r3 = 0
            r12[r3] = r1
            java.lang.String r3 = "VoipGroupUserChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r12)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
        L_0x0301:
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
            goto L_0x097f
        L_0x0317:
            r1 = 33
            if (r9 != r1) goto L_0x0331
            r0 = 2131629043(0x7f0e13f3, float:1.8885396E38)
            java.lang.String r1 = "VoipGroupCopyInviteLinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = 0
            r0 = 2131558590(0x7f0d00be, float:1.87425E38)
            r12 = r0
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            goto L_0x097f
        L_0x0331:
            r1 = 77
            if (r9 != r1) goto L_0x0363
            r2 = r10
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            r3 = 0
            r1 = 2131558487(0x7f0d0057, float:1.8742291E38)
            r20 = r1
            r0 = 5000(0x1388, double:2.4703E-320)
            r7.timeLeft = r0
            org.telegram.ui.ActionBar.BaseFragment r0 = r7.parentFragment
            if (r0 == 0) goto L_0x035e
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.Message
            if (r0 == 0) goto L_0x035e
            r0 = r11
            org.telegram.tgnet.TLRPC$Message r0 = (org.telegram.tgnet.TLRPC.Message) r0
            r1 = 0
            r7.setOnTouchListener(r1)
            android.widget.TextView r12 = r7.infoTextView
            r12.setMovementMethod(r1)
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda2
            r1.<init>(r7, r0)
            r7.setOnClickListener(r1)
        L_0x035e:
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x0363:
            r0 = 30
            if (r9 != r0) goto L_0x039c
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x0373
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x0379
        L_0x0373:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x0379:
            r1 = 2131629123(0x7f0e1443, float:1.8885558E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCantNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558591(0x7f0d00bf, float:1.8742502E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x039c:
            r0 = 35
            if (r9 != r0) goto L_0x03db
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x03ac
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03b8
        L_0x03ac:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x03b6
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r0 = r0.title
            goto L_0x03b8
        L_0x03b6:
            java.lang.String r0 = ""
        L_0x03b8:
            r1 = 2131629124(0x7f0e1444, float:1.888556E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCantNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558591(0x7f0d00bf, float:1.8742502E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x03db:
            r0 = 31
            if (r9 != r0) goto L_0x0414
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x03eb
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x03f1
        L_0x03eb:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x03f1:
            r1 = 2131629121(0x7f0e1441, float:1.8885554E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCanNowSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558597(0x7f0d00c5, float:1.8742514E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x0414:
            r0 = 38
            if (r9 != r0) goto L_0x0451
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r0 == 0) goto L_0x0436
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            r1 = 2131629133(0x7f0e144d, float:1.8885578E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = r0.title
            r12 = 0
            r3[r12] = r2
            java.lang.String r2 = "VoipGroupYouCanNowSpeakIn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r0
            goto L_0x0444
        L_0x0436:
            r0 = 2131629132(0x7f0e144c, float:1.8885576E38)
            java.lang.String r1 = "VoipGroupYouCanNowSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = r0
        L_0x0444:
            r3 = 0
            r0 = 2131558583(0x7f0d00b7, float:1.8742486E38)
            r12 = r0
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r0 = r12
            r1 = r15
            goto L_0x097f
        L_0x0451:
            r0 = 42
            if (r9 != r0) goto L_0x048b
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r1 == 0) goto L_0x046d
            r1 = 2131628987(0x7f0e13bb, float:1.8885282E38)
            java.lang.String r2 = "VoipChannelSoundMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x047b
        L_0x046d:
            r1 = 2131629101(0x7f0e142d, float:1.8885513E38)
            java.lang.String r2 = "VoipGroupSoundMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x047b:
            r3 = 0
            r1 = 2131558464(0x7f0d0040, float:1.8742245E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x048b:
            r0 = 43
            if (r9 != r0) goto L_0x04c5
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            if (r1 == 0) goto L_0x04a7
            r1 = 2131628988(0x7f0e13bc, float:1.8885284E38)
            java.lang.String r2 = "VoipChannelSoundUnmuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x04b5
        L_0x04a7:
            r1 = 2131629102(0x7f0e142e, float:1.8885515E38)
            java.lang.String r2 = "VoipGroupSoundUnmuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x04b5:
            r3 = 0
            r1 = 2131558470(0x7f0d0046, float:1.8742257E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x04c5:
            int r0 = r7.currentAction
            r1 = 39
            if (r0 == r1) goto L_0x095c
            r1 = 100
            if (r0 != r1) goto L_0x04d3
            r30 = r15
            goto L_0x095e
        L_0x04d3:
            r1 = 40
            if (r0 == r1) goto L_0x08bf
            r1 = 101(0x65, float:1.42E-43)
            if (r0 != r1) goto L_0x04df
            r30 = r15
            goto L_0x08c1
        L_0x04df:
            r1 = 36
            if (r9 != r1) goto L_0x0518
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x04ef
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x04f5
        L_0x04ef:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x04f5:
            r1 = 2131629122(0x7f0e1442, float:1.8885556E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupUserCanNowSpeakForYou"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558597(0x7f0d00c5, float:1.8742514E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x0518:
            r1 = 32
            if (r9 != r1) goto L_0x0551
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x0528
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            goto L_0x052e
        L_0x0528:
            r0 = r10
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            java.lang.String r1 = r0.title
            r0 = r1
        L_0x052e:
            r1 = 2131629092(0x7f0e1424, float:1.8885495E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            java.lang.String r2 = "VoipGroupRemovedFromGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r3 = 0
            r1 = 2131558589(0x7f0d00bd, float:1.8742498E38)
            r12 = r0
            r20 = r1
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            r1 = r15
            r0 = r20
            goto L_0x097f
        L_0x0551:
            r1 = 9
            if (r9 == r1) goto L_0x087d
            r1 = 10
            if (r9 != r1) goto L_0x055d
            r30 = r15
            goto L_0x087f
        L_0x055d:
            r1 = 8
            if (r9 != r1) goto L_0x057e
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = 2131627073(0x7f0e0CLASSNAME, float:1.88814E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r12 = 0
            r3[r12] = r2
            java.lang.String r2 = "NowInContacts"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r3 = 0
            r0 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r1 = r15
            goto L_0x097f
        L_0x057e:
            r1 = 22
            if (r9 != r1) goto L_0x05ef
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r5)
            if (r0 == 0) goto L_0x05a0
            if (r10 != 0) goto L_0x0595
            r0 = 2131626518(0x7f0e0a16, float:1.8880274E38)
            java.lang.String r1 = "MainProfilePhotoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
            goto L_0x05e8
        L_0x0595:
            r0 = 2131626519(0x7f0e0a17, float:1.8880276E38)
            java.lang.String r1 = "MainProfileVideoSetHint"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
            goto L_0x05e8
        L_0x05a0:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x05d1
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x05d1
            if (r10 != 0) goto L_0x05c6
            r1 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.String r2 = "MainChannelProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            goto L_0x05e8
        L_0x05c6:
            r1 = 2131626515(0x7f0e0a13, float:1.8880268E38)
            java.lang.String r2 = "MainChannelProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            goto L_0x05e8
        L_0x05d1:
            if (r10 != 0) goto L_0x05de
            r1 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.String r2 = "MainGroupProfilePhotoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            goto L_0x05e8
        L_0x05de:
            r1 = 2131626517(0x7f0e0a15, float:1.8880272E38)
            java.lang.String r2 = "MainGroupProfileVideoSetHint"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
        L_0x05e8:
            r3 = 0
            r0 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r1 = r15
            goto L_0x097f
        L_0x05ef:
            r1 = 23
            if (r9 != r1) goto L_0x0603
            r0 = 2131625043(0x7f0e0453, float:1.8877283E38)
            java.lang.String r1 = "ChatWasMovedToMainList"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = 0
            r0 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r1 = r15
            goto L_0x097f
        L_0x0603:
            r1 = 6
            if (r9 != r1) goto L_0x061f
            r0 = 2131624395(0x7f0e01cb, float:1.8875969E38)
            java.lang.String r1 = "ArchiveHidden"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131624396(0x7f0e01cc, float:1.887597E38)
            java.lang.String r1 = "ArchiveHiddenInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131558425(0x7f0d0019, float:1.8742165E38)
            r1 = 48
            goto L_0x097f
        L_0x061f:
            r1 = 13
            if (r0 != r1) goto L_0x063c
            r0 = 2131627824(0x7f0e0var_, float:1.8882923E38)
            java.lang.String r1 = "QuizWellDone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131627825(0x7f0e0var_, float:1.8882925E38)
            java.lang.String r1 = "QuizWellDoneInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131558599(0x7f0d00c7, float:1.8742518E38)
            r1 = 44
            goto L_0x097f
        L_0x063c:
            r1 = 14
            if (r0 != r1) goto L_0x0659
            r0 = 2131627826(0x7f0e0var_, float:1.8882927E38)
            java.lang.String r1 = "QuizWrongAnswer"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131627827(0x7f0e0var_, float:1.888293E38)
            java.lang.String r1 = "QuizWrongAnswerInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r0 = 2131558601(0x7f0d00c9, float:1.8742522E38)
            r1 = 44
            goto L_0x097f
        L_0x0659:
            r0 = 7
            if (r9 != r0) goto L_0x0686
            r0 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r1 = "ArchivePinned"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r0)
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r0 = r0.dialogFilters
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x067e
            r0 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r1 = "ArchivePinnedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = r0
            goto L_0x0680
        L_0x067e:
            r0 = 0
            r3 = r0
        L_0x0680:
            r0 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r1 = r15
            goto L_0x097f
        L_0x0686:
            r0 = 20
            if (r9 == r0) goto L_0x0748
            r1 = 21
            if (r9 != r1) goto L_0x0690
            goto L_0x0748
        L_0x0690:
            r0 = 19
            if (r9 != r0) goto L_0x069d
            java.lang.CharSequence r2 = r7.infoText
            r3 = 0
            r0 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r1 = r15
            goto L_0x097f
        L_0x069d:
            r0 = 82
            if (r9 != r0) goto L_0x06bd
            r0 = r10
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            boolean r1 = r0.isVideo
            if (r1 == 0) goto L_0x06ae
            r1 = 2131624499(0x7f0e0233, float:1.887618E38)
            java.lang.String r2 = "AttachMediaVideoDeselected"
            goto L_0x06b3
        L_0x06ae:
            r1 = 2131624494(0x7f0e022e, float:1.887617E38)
            java.lang.String r2 = "AttachMediaPhotoDeselected"
        L_0x06b3:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = r1
            r3 = 0
            r0 = 0
            r1 = r15
            goto L_0x097f
        L_0x06bd:
            r0 = 78
            if (r9 == r0) goto L_0x06ff
            r0 = 79
            if (r9 != r0) goto L_0x06c6
            goto L_0x06ff
        L_0x06c6:
            r0 = 3
            if (r9 != r0) goto L_0x06d4
            r0 = 2131625000(0x7f0e0428, float:1.8877196E38)
            java.lang.String r1 = "ChatArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
            goto L_0x06de
        L_0x06d4:
            r0 = 2131625056(0x7f0e0460, float:1.887731E38)
            java.lang.String r1 = "ChatsArchived"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r2 = r0
        L_0x06de:
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r0 = r0.dialogFilters
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x06f7
            r0 = 2131625001(0x7f0e0429, float:1.8877198E38)
            java.lang.String r1 = "ChatArchivedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3 = r0
            goto L_0x06f9
        L_0x06f7:
            r0 = 0
            r3 = r0
        L_0x06f9:
            r0 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r1 = r15
            goto L_0x097f
        L_0x06ff:
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 78
            if (r9 != r1) goto L_0x0714
            r1 = 0
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = "PinnedDialogsCount"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r2)
            goto L_0x071e
        L_0x0714:
            r1 = 0
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r1 = "UnpinnedDialogsCount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0, r2)
            r2 = r1
        L_0x071e:
            r3 = 0
            int r1 = r7.currentAction
            r12 = 78
            if (r1 != r12) goto L_0x0729
            r1 = 2131558465(0x7f0d0041, float:1.8742247E38)
            goto L_0x072c
        L_0x0729:
            r1 = 2131558471(0x7f0d0047, float:1.8742259E38)
        L_0x072c:
            boolean r12 = r11 instanceof java.lang.Integer
            if (r12 == 0) goto L_0x073f
            r12 = r11
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            r20 = r0
            r22 = r1
            long r0 = (long) r12
            r7.timeLeft = r0
            goto L_0x0743
        L_0x073f:
            r20 = r0
            r22 = r1
        L_0x0743:
            r1 = r15
            r0 = r22
            goto L_0x097f
        L_0x0748:
            r1 = r11
            org.telegram.messenger.MessagesController$DialogFilter r1 = (org.telegram.messenger.MessagesController.DialogFilter) r1
            r2 = 0
            int r12 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r12 == 0) goto L_0x0816
            r2 = r5
            boolean r12 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            if (r12 == 0) goto L_0x076c
            int r12 = r7.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            int r20 = org.telegram.messenger.DialogObject.getEncryptedChatId(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r20)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r12.getEncryptedChat(r0)
            long r2 = r0.user_id
        L_0x076c:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r2)
            if (r0 == 0) goto L_0x07c6
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r12 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r12)
            r12 = 20
            if (r9 != r12) goto L_0x07a5
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
            r15 = 2131625897(0x7f0e07a9, float:1.8879015E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r15, r13)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            goto L_0x07c5
        L_0x07a5:
            r30 = r15
            r21 = 1
            r23 = 0
            r12 = 2131625898(0x7f0e07aa, float:1.8879017E38)
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = org.telegram.messenger.UserObject.getFirstName(r0)
            r15[r23] = r13
            java.lang.String r13 = r1.name
            r15[r21] = r13
            java.lang.String r13 = "FilterUserRemovedFrom"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r13, r12, r15)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
        L_0x07c5:
            goto L_0x0814
        L_0x07c6:
            r30 = r15
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r12 = -r2
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            r12 = 20
            if (r9 != r12) goto L_0x07f8
            r12 = 2131625837(0x7f0e076d, float:1.8878893E38)
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
            goto L_0x0814
        L_0x07f8:
            r20 = 0
            r21 = 1
            r12 = 2131625838(0x7f0e076e, float:1.8878895E38)
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.String r13 = r0.title
            r15[r20] = r13
            java.lang.String r13 = r1.name
            r15[r21] = r13
            java.lang.String r13 = "FilterChatRemovedFrom"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r13, r12, r15)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
        L_0x0814:
            r2 = r12
            goto L_0x086d
        L_0x0816:
            r30 = r15
            r0 = 20
            if (r9 != r0) goto L_0x0845
            r0 = 2131625841(0x7f0e0771, float:1.8878901E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r10
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r12 = 0
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r15 = "ChatsSelected"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2, r13)
            r3[r12] = r2
            java.lang.String r2 = r1.name
            r12 = 1
            r3[r12] = r2
            java.lang.String r2 = "FilterChatsAddedToExisting"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = r0
            goto L_0x086d
        L_0x0845:
            r0 = 2131625842(0x7f0e0772, float:1.8878903E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r10
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r12 = 0
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r15 = "ChatsSelected"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2, r13)
            r3[r12] = r2
            java.lang.String r2 = r1.name
            r12 = 1
            r3[r12] = r2
            java.lang.String r2 = "FilterChatsRemovedFrom"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2 = r0
        L_0x086d:
            r3 = 0
            r0 = 20
            if (r9 != r0) goto L_0x0876
            r0 = 2131558452(0x7f0d0034, float:1.874222E38)
            goto L_0x0879
        L_0x0876:
            r0 = 2131558453(0x7f0d0035, float:1.8742222E38)
        L_0x0879:
            r1 = r30
            goto L_0x097f
        L_0x087d:
            r30 = r15
        L_0x087f:
            r0 = r10
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            r1 = 9
            if (r9 != r1) goto L_0x089f
            r1 = 2131625565(0x7f0e065d, float:1.8878342E38)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r0)
            r13 = 0
            r3[r13] = r12
            java.lang.String r12 = "EditAdminTransferChannelToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
            goto L_0x08b7
        L_0x089f:
            r2 = 1
            r13 = 0
            r1 = 2131625566(0x7f0e065e, float:1.8878344E38)
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r0)
            r3[r13] = r2
            java.lang.String r2 = "EditAdminTransferGroupToast"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r2 = r1
        L_0x08b7:
            r3 = 0
            r0 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r1 = r30
            goto L_0x097f
        L_0x08bf:
            r30 = r15
        L_0x08c1:
            r1 = 40
            if (r0 != r1) goto L_0x08cb
            r0 = 2131629033(0x7f0e13e9, float:1.8885375E38)
            java.lang.String r1 = "VoipGroupAudioRecordSaved"
            goto L_0x08d0
        L_0x08cb:
            r0 = 2131629127(0x7f0e1447, float:1.8885566E38)
            java.lang.String r1 = "VoipGroupVideoRecordSaved"
        L_0x08d0:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = r0
            r3 = 0
            r2 = 2131558593(0x7f0d00c1, float:1.8742506E38)
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
            if (r13 < 0) goto L_0x094e
            if (r15 < 0) goto L_0x094e
            if (r13 == r15) goto L_0x094e
            int r0 = r15 + 2
            r12.replace(r15, r0, r14)
            int r0 = r13 + 2
            r12.replace(r13, r0, r14)
            org.telegram.ui.Components.URLSpanNoUnderline r0 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0943 }
            r22 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x093d }
            r1.<init>()     // Catch:{ Exception -> 0x093d }
            r24 = r2
            java.lang.String r2 = "tg://openmessage?user_id="
            r1.append(r2)     // Catch:{ Exception -> 0x0939 }
            int r2 = r7.currentAccount     // Catch:{ Exception -> 0x0939 }
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x0939 }
            r25 = r3
            long r2 = r2.getClientUserId()     // Catch:{ Exception -> 0x0937 }
            r1.append(r2)     // Catch:{ Exception -> 0x0937 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0937 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0937 }
            int r1 = r15 + -2
            r2 = 33
            r12.setSpan(r0, r13, r1, r2)     // Catch:{ Exception -> 0x0937 }
            goto L_0x0954
        L_0x0937:
            r0 = move-exception
            goto L_0x094a
        L_0x0939:
            r0 = move-exception
            r25 = r3
            goto L_0x094a
        L_0x093d:
            r0 = move-exception
            r24 = r2
            r25 = r3
            goto L_0x094a
        L_0x0943:
            r0 = move-exception
            r22 = r1
            r24 = r2
            r25 = r3
        L_0x094a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0954
        L_0x094e:
            r22 = r1
            r24 = r2
            r25 = r3
        L_0x0954:
            r2 = r12
            r0 = r24
            r3 = r25
            r1 = r30
            goto L_0x097f
        L_0x095c:
            r30 = r15
        L_0x095e:
            r1 = 39
            if (r0 != r1) goto L_0x0968
            r0 = 2131629034(0x7f0e13ea, float:1.8885378E38)
            java.lang.String r1 = "VoipGroupAudioRecordStarted"
            goto L_0x096d
        L_0x0968:
            r0 = 2131629128(0x7f0e1448, float:1.8885568E38)
            java.lang.String r1 = "VoipGroupVideoRecordStarted"
        L_0x096d:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r3 = 0
            r0 = 2131558594(0x7f0d00c2, float:1.8742508E38)
            r12 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r12
            r1 = r30
        L_0x097f:
            android.widget.TextView r12 = r7.infoTextView
            r12.setText(r2)
            if (r0 == 0) goto L_0x09c5
            if (r31 == 0) goto L_0x0990
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.setImageResource(r0)
            r13 = r29
            goto L_0x09ac
        L_0x0990:
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.setAnimation(r0, r1, r1)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            org.telegram.ui.Components.RLottieDrawable r12 = r12.getAnimatedDrawable()
            r13 = r29
            r12.setPlayInDirectionOfCustomEndFrame(r13)
            if (r13 == 0) goto L_0x09a5
            r15 = r19
            goto L_0x09a9
        L_0x09a5:
            int r15 = r12.getFramesCount()
        L_0x09a9:
            r12.setCustomEndFrame(r15)
        L_0x09ac:
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r15 = 0
            r12.setVisibility(r15)
            if (r31 != 0) goto L_0x09ce
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            if (r13 == 0) goto L_0x09bb
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x09bc
        L_0x09bb:
            r15 = 0
        L_0x09bc:
            r12.setProgress(r15)
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r12.playAnimation()
            goto L_0x09ce
        L_0x09c5:
            r13 = r29
            org.telegram.ui.Components.RLottieImageView r12 = r7.leftImageView
            r15 = 8
            r12.setVisibility(r15)
        L_0x09ce:
            if (r3 == 0) goto L_0x0a14
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
            goto L_0x0a40
        L_0x0a14:
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
        L_0x0a40:
            android.widget.LinearLayout r1 = r7.undoButton
            r12 = 8
            r1.setVisibility(r12)
            r0 = r4
            r29 = r13
            goto L_0x18d4
        L_0x0a4c:
            r13 = r29
            int r0 = r7.currentAction
            r1 = 45
            if (r0 == r1) goto L_0x1290
            r1 = 46
            if (r0 == r1) goto L_0x1290
            r1 = 47
            if (r0 == r1) goto L_0x1290
            r1 = 51
            if (r0 == r1) goto L_0x1290
            r1 = 50
            if (r0 == r1) goto L_0x1290
            r1 = 52
            if (r0 == r1) goto L_0x1290
            r1 = 53
            if (r0 == r1) goto L_0x1290
            r1 = 54
            if (r0 == r1) goto L_0x1290
            r1 = 55
            if (r0 == r1) goto L_0x1290
            r1 = 56
            if (r0 == r1) goto L_0x1290
            r1 = 57
            if (r0 == r1) goto L_0x1290
            r1 = 58
            if (r0 == r1) goto L_0x1290
            r1 = 59
            if (r0 == r1) goto L_0x1290
            r1 = 60
            if (r0 == r1) goto L_0x1290
            r1 = 71
            if (r0 == r1) goto L_0x1290
            r1 = 70
            if (r0 == r1) goto L_0x1290
            r1 = 75
            if (r0 == r1) goto L_0x1290
            r1 = 76
            if (r0 == r1) goto L_0x1290
            r1 = 41
            if (r0 == r1) goto L_0x1290
            r1 = 78
            if (r0 == r1) goto L_0x1290
            r1 = 79
            if (r0 == r1) goto L_0x1290
            r1 = 61
            if (r0 == r1) goto L_0x1290
            r1 = 80
            if (r0 != r1) goto L_0x0ab2
            r29 = r13
            r2 = r28
            goto L_0x1294
        L_0x0ab2:
            r1 = 24
            if (r0 == r1) goto L_0x1117
            r1 = 25
            if (r0 != r1) goto L_0x0ac0
            r29 = r13
            r2 = r28
            goto L_0x111b
        L_0x0ac0:
            r1 = 11
            if (r0 != r1) goto L_0x0b3b
            r0 = r10
            org.telegram.tgnet.TLRPC$TL_authorization r0 = (org.telegram.tgnet.TLRPC.TL_authorization) r0
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131624532(0x7f0e0254, float:1.8876246E38)
            java.lang.String r3 = "AuthAnotherClientOk"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558430(0x7f0d001e, float:1.8742176E38)
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
            goto L_0x128b
        L_0x0b3b:
            r1 = 15
            if (r0 != r1) goto L_0x0c1f
            r0 = 10000(0x2710, double:4.9407E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.undoTextView
            r1 = 2131627090(0x7f0e0CLASSNAME, float:1.8881435E38)
            java.lang.String r2 = "Open"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625834(0x7f0e076a, float:1.8878887E38)
            java.lang.String r2 = "FilterAvailableTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558446(0x7f0d002e, float:1.8742208E38)
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
            r1 = 2131625833(0x7f0e0769, float:1.8878885E38)
            java.lang.String r2 = "FilterAvailableText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r1)
            r3 = 42
            int r12 = r1.indexOf(r3)
            int r3 = r1.lastIndexOf(r3)
            if (r12 < 0) goto L_0x0be4
            if (r3 < 0) goto L_0x0be4
            if (r12 == r3) goto L_0x0be4
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
            goto L_0x0be8
        L_0x0be4:
            r16 = r0
            r22 = r1
        L_0x0be8:
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
            goto L_0x18d4
        L_0x0c1f:
            r1 = 16
            if (r0 == r1) goto L_0x0var_
            r1 = 17
            if (r0 != r1) goto L_0x0c2d
            r29 = r13
            r2 = r28
            goto L_0x0var_
        L_0x0c2d:
            r1 = 18
            if (r0 != r1) goto L_0x0cc1
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
            r3 = 2131558424(0x7f0d0018, float:1.8742163E38)
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
            goto L_0x128b
        L_0x0cc1:
            r2 = r28
            r1 = 12
            if (r0 != r1) goto L_0x0d71
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625186(0x7f0e04e2, float:1.8877573E38)
            java.lang.String r3 = "ColorThemeChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131166183(0x7var_e7, float:1.7946604E38)
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
            r0 = 2131625187(0x7f0e04e3, float:1.8877575E38)
            java.lang.String r1 = "ColorThemeChangedInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r0)
            r3 = 42
            int r12 = r0.indexOf(r3)
            int r3 = r0.lastIndexOf(r3)
            if (r12 < 0) goto L_0x0d3f
            if (r3 < 0) goto L_0x0d3f
            if (r12 == r3) goto L_0x0d3f
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
            goto L_0x0d43
        L_0x0d3f:
            r16 = r0
            r22 = r3
        L_0x0d43:
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
            goto L_0x18d4
        L_0x0d71:
            r1 = 2
            if (r0 == r1) goto L_0x0var_
            r1 = 4
            if (r0 != r1) goto L_0x0d7b
            r29 = r13
            goto L_0x0f2b
        L_0x0d7b:
            r0 = 82
            if (r9 != r0) goto L_0x0e43
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r0
            r0 = r10
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            android.widget.TextView r1 = r7.infoTextView
            boolean r3 = r0.isVideo
            if (r3 == 0) goto L_0x0d94
            r3 = 2131624499(0x7f0e0233, float:1.887618E38)
            java.lang.String r15 = "AttachMediaVideoDeselected"
            goto L_0x0d99
        L_0x0d94:
            r3 = 2131624494(0x7f0e022e, float:1.887617E38)
            java.lang.String r15 = "AttachMediaPhotoDeselected"
        L_0x0d99:
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
            if (r1 == 0) goto L_0x0ddc
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            java.lang.String r3 = r0.thumbPath
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r12 = 0
            r1.setImage(r3, r12, r15)
            goto L_0x0e3f
        L_0x0ddc:
            java.lang.String r1 = r0.path
            if (r1 == 0) goto L_0x0e38
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            int r3 = r0.orientation
            r15 = 1
            r1.setOrientation(r3, r15)
            boolean r1 = r0.isVideo
            if (r1 == 0) goto L_0x0e12
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
            goto L_0x0e3f
        L_0x0e12:
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
            goto L_0x0e3f
        L_0x0e38:
            org.telegram.ui.Components.BackupImageView r1 = r7.avatarImageView
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.chat_attachEmptyDrawable
            r1.setImageDrawable(r3)
        L_0x0e3f:
            r29 = r13
            goto L_0x128b
        L_0x0e43:
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
            if (r0 == r1) goto L_0x0eea
            if (r0 == 0) goto L_0x0eea
            r1 = 26
            if (r0 != r1) goto L_0x0e85
            r29 = r13
            goto L_0x0eec
        L_0x0e85:
            r1 = 27
            if (r0 != r1) goto L_0x0e9a
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625057(0x7f0e0461, float:1.8877311E38)
            java.lang.String r3 = "ChatsDeletedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            r29 = r13
            goto L_0x0efa
        L_0x0e9a:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r0 == 0) goto L_0x0ed9
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            r29 = r13
            long r12 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0eca
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x0eca
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131624900(0x7f0e03c4, float:1.8876993E38)
            java.lang.String r12 = "ChannelDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r1.setText(r3)
            goto L_0x0ed8
        L_0x0eca:
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131626090(0x7f0e086a, float:1.8879406E38)
            java.lang.String r12 = "GroupDeletedUndo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r1.setText(r3)
        L_0x0ed8:
            goto L_0x0efa
        L_0x0ed9:
            r29 = r13
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625004(0x7f0e042c, float:1.8877204E38)
            java.lang.String r3 = "ChatDeletedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            goto L_0x0efa
        L_0x0eea:
            r29 = r13
        L_0x0eec:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626146(0x7f0e08a2, float:1.887952E38)
            java.lang.String r3 = "HistoryClearedUndo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
        L_0x0efa:
            int r0 = r7.currentAction
            r1 = 81
            if (r0 == r1) goto L_0x128b
            r0 = 0
        L_0x0var_:
            int r1 = r35.size()
            if (r0 >= r1) goto L_0x128b
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
            goto L_0x0var_
        L_0x0var_:
            r29 = r13
        L_0x0f2b:
            r1 = 2
            if (r9 != r1) goto L_0x0f3d
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625000(0x7f0e0428, float:1.8877196E38)
            java.lang.String r3 = "ChatArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            goto L_0x0f4b
        L_0x0f3d:
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131625056(0x7f0e0460, float:1.887731E38)
            java.lang.String r3 = "ChatsArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
        L_0x0f4b:
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
            r1 = 2131558422(0x7f0d0016, float:1.874216E38)
            r3 = 36
            r0.setAnimation(r1, r3, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 0
            r0.setProgress(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r0.playAnimation()
            goto L_0x128b
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
            if (r1 == 0) goto L_0x0fdc
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r12 = "DiceInfo2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r1.setText(r3)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r3 = 2131165386(0x7var_ca, float:1.7944988E38)
            r1.setImageResource(r3)
            goto L_0x108a
        L_0x0fdc:
            java.lang.String r1 = "🎯"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0ff7
            android.widget.TextView r1 = r7.infoTextView
            r3 = 2131625307(0x7f0e055b, float:1.8877818E38)
            java.lang.String r12 = "DartInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r1.setText(r3)
            goto L_0x1056
        L_0x0ff7:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "DiceEmojiInfo_"
            r1.append(r3)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = org.telegram.messenger.LocaleController.getServerString(r1)
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L_0x102b
            android.widget.TextView r3 = r7.infoTextView
            android.text.TextPaint r12 = r3.getPaint()
            android.graphics.Paint$FontMetricsInt r12 = r12.getFontMetricsInt()
            r13 = 1096810496(0x41600000, float:14.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r13 = 0
            java.lang.CharSequence r12 = org.telegram.messenger.Emoji.replaceEmoji(r1, r12, r15, r13)
            r3.setText(r12)
            goto L_0x1056
        L_0x102b:
            r13 = 0
            android.widget.TextView r3 = r7.infoTextView
            r15 = 1
            java.lang.Object[] r12 = new java.lang.Object[r15]
            r12[r13] = r0
            java.lang.String r15 = "DiceEmojiInfo"
            r13 = 2131625468(0x7f0e05fc, float:1.8878145E38)
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
        L_0x1056:
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
        L_0x108a:
            android.widget.TextView r1 = r7.undoTextView
            r3 = 2131628185(0x7f0e1099, float:1.8883656E38)
            java.lang.String r12 = "SendDice"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r1.setText(r3)
            int r1 = r7.currentAction
            r3 = 16
            if (r1 != r3) goto L_0x10dd
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
            goto L_0x10ed
        L_0x10dd:
            r13 = 8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r26)
            android.widget.TextView r3 = r7.undoTextView
            r3.setVisibility(r13)
            android.widget.LinearLayout r3 = r7.undoButton
            r3.setVisibility(r13)
        L_0x10ed:
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
            goto L_0x128b
        L_0x1117:
            r29 = r13
            r2 = r28
        L_0x111b:
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
            if (r0 == 0) goto L_0x11f4
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
            r12 = 2131627744(0x7f0e0ee0, float:1.8882761E38)
            java.lang.String r13 = "ProximityAlertSet"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558470(0x7f0d0046, float:1.8742257E38)
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
            if (r1 == 0) goto L_0x11c9
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
            r13 = 2131627746(0x7f0e0ee2, float:1.8882765E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            r3.setText(r12)
            goto L_0x11e3
        L_0x11c9:
            android.widget.TextView r3 = r7.subinfoTextView
            r13 = 1
            java.lang.Object[] r15 = new java.lang.Object[r13]
            float r13 = (float) r0
            r12 = 2
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatDistance(r13, r12)
            r12 = 0
            r15[r12] = r13
            java.lang.String r12 = "ProximityAlertSetInfoGroup2"
            r13 = 2131627745(0x7f0e0ee1, float:1.8882763E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r15)
            r3.setText(r12)
        L_0x11e3:
            android.widget.LinearLayout r3 = r7.undoButton
            r12 = 8
            r3.setVisibility(r12)
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4.topMargin = r3
            goto L_0x1279
        L_0x11f4:
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
            r12 = 2131627743(0x7f0e0edf, float:1.888276E38)
            java.lang.String r13 = "ProximityAlertCancelled"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558464(0x7f0d0040, float:1.8742245E38)
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
        L_0x1279:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r4.leftMargin = r3
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 0
            r3.setProgress(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r3.playAnimation()
        L_0x128b:
            r28 = r2
            r0 = r4
            goto L_0x18d4
        L_0x1290:
            r29 = r13
            r2 = r28
        L_0x1294:
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
            if (r3 != r12) goto L_0x12de
            android.widget.TextView r3 = r7.infoTextView
            r12 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.String r15 = "BroadcastGroupConvertSuccess"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558456(0x7f0d0038, float:1.8742228E38)
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
            goto L_0x1895
        L_0x12de:
            r12 = 75
            if (r3 != r12) goto L_0x130e
            android.widget.TextView r3 = r7.infoTextView
            r12 = 2131626069(0x7f0e0855, float:1.8879364E38)
            java.lang.String r15 = "GigagroupConvertCancelHint"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r3.setText(r12)
            org.telegram.ui.Components.RLottieImageView r3 = r7.leftImageView
            r12 = 2131558424(0x7f0d0018, float:1.8742163E38)
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
            goto L_0x1895
        L_0x130e:
            r12 = 70
            if (r9 != r12) goto L_0x1366
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
            r2 = 2131624553(0x7f0e0269, float:1.8876289E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r2, r0)
            r13.setText(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558450(0x7f0d0032, float:1.8742216E38)
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
            goto L_0x1895
        L_0x1366:
            r32 = r0
            r28 = r2
            r0 = 71
            if (r3 != r0) goto L_0x13a0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624552(0x7f0e0268, float:1.8876287E38)
            java.lang.String r2 = "AutoDeleteHintOffText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558449(0x7f0d0031, float:1.8742214E38)
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
            goto L_0x1893
        L_0x13a0:
            r0 = 45
            if (r3 != r0) goto L_0x13d2
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626196(0x7f0e08d4, float:1.8879621E38)
            java.lang.String r2 = "ImportMutualError"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558445(0x7f0d002d, float:1.8742206E38)
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
            goto L_0x1895
        L_0x13d2:
            r0 = 46
            if (r3 != r0) goto L_0x1404
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626197(0x7f0e08d5, float:1.8879623E38)
            java.lang.String r2 = "ImportNotAdmin"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558445(0x7f0d002d, float:1.8742206E38)
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
            goto L_0x1895
        L_0x1404:
            r0 = 47
            if (r3 != r0) goto L_0x1442
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626219(0x7f0e08eb, float:1.8879668E38)
            java.lang.String r2 = "ImportedInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558476(0x7f0d004c, float:1.8742269E38)
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
            goto L_0x1895
        L_0x1442:
            r12 = 1096810496(0x41600000, float:14.0)
            r0 = 51
            if (r3 != r0) goto L_0x146e
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624513(0x7f0e0241, float:1.8876208E38)
            java.lang.String r2 = "AudioSpeedNormal"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558409(0x7f0d0009, float:1.8742133E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            goto L_0x1893
        L_0x146e:
            r0 = 50
            if (r3 != r0) goto L_0x1498
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624512(0x7f0e0240, float:1.8876206E38)
            java.lang.String r2 = "AudioSpeedFast"
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
            goto L_0x1893
        L_0x1498:
            r0 = 52
            if (r3 == r0) goto L_0x17ef
            r0 = 56
            if (r3 == r0) goto L_0x17ef
            r0 = 57
            if (r3 == r0) goto L_0x17ef
            r0 = 58
            if (r3 == r0) goto L_0x17ef
            r0 = 59
            if (r3 == r0) goto L_0x17ef
            r0 = 60
            if (r3 == r0) goto L_0x17ef
            r0 = 80
            if (r3 != r0) goto L_0x14b6
            goto L_0x17ef
        L_0x14b6:
            r0 = 54
            if (r3 != r0) goto L_0x14e0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624938(0x7f0e03ea, float:1.887707E38)
            java.lang.String r2 = "ChannelNotifyMembersInfoOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558529(0x7f0d0081, float:1.8742376E38)
            r2 = 30
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            goto L_0x1893
        L_0x14e0:
            r0 = 55
            if (r3 != r0) goto L_0x150a
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131624937(0x7f0e03e9, float:1.8877068E38)
            java.lang.String r2 = "ChannelNotifyMembersInfoOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558528(0x7f0d0080, float:1.8742374E38)
            r2 = 30
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            android.widget.TextView r0 = r7.infoTextView
            r1 = 1097859072(0x41700000, float:15.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            goto L_0x1893
        L_0x150a:
            r0 = 41
            if (r3 != r0) goto L_0x15c2
            if (r11 != 0) goto L_0x158b
            int r0 = r7.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.clientUserId
            int r2 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x1530
            android.widget.TextView r0 = r7.infoTextView
            r1 = 2131626244(0x7f0e0904, float:1.8879719E38)
            java.lang.String r2 = "InvLinkToSavedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setText(r1)
            goto L_0x15b2
        L_0x1530:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r0 == 0) goto L_0x1560
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626243(0x7f0e0903, float:1.8879717E38)
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = r0.title
            r15 = 0
            r13[r15] = r3
            java.lang.String r3 = "InvLinkToGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r13)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x15b2
        L_0x1560:
            int r0 = r7.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626245(0x7f0e0905, float:1.887972E38)
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r0)
            r15 = 0
            r13[r15] = r3
            java.lang.String r3 = "InvLinkToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r13)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x15b2
        L_0x158b:
            r0 = r11
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626242(0x7f0e0902, float:1.8879715E38)
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            r3 = 0
            java.lang.Object[] r15 = new java.lang.Object[r3]
            java.lang.String r12 = "Chats"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r0, r15)
            r13[r3] = r12
            java.lang.String r3 = "InvLinkToChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r13)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x15b2:
            org.telegram.ui.Components.RLottieImageView r0 = r7.leftImageView
            r1 = 2131558430(0x7f0d001e, float:1.8742176E38)
            r2 = 36
            r0.setAnimation(r1, r2, r2)
            r0 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r0
            goto L_0x1893
        L_0x15c2:
            r0 = 53
            if (r3 != r0) goto L_0x1720
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r11 != 0) goto L_0x16be
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x1611
            int r1 = r0.intValue()
            r2 = 1
            if (r1 != r2) goto L_0x15f1
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626039(0x7f0e0837, float:1.8879303E38)
            java.lang.String r3 = "FwdMessageToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            goto L_0x1603
        L_0x15f1:
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626043(0x7f0e083b, float:1.8879311E38)
            java.lang.String r3 = "FwdMessagesToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
        L_0x1603:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = r32
            goto L_0x1719
        L_0x1611:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r1 == 0) goto L_0x1663
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r2 = -r5
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r0.intValue()
            r3 = 1
            if (r2 != r3) goto L_0x1648
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r15 = r1.title
            r16 = 0
            r13[r16] = r15
            java.lang.String r15 = "FwdMessageToGroup"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r15, r12, r13)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            r2.setText(r12)
            goto L_0x1662
        L_0x1648:
            r16 = 0
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = r1.title
            r13[r16] = r3
            java.lang.String r3 = "FwdMessagesToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x1662:
            goto L_0x16b1
        L_0x1663:
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            int r2 = r0.intValue()
            r3 = 1
            if (r2 != r3) goto L_0x1695
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131626040(0x7f0e0838, float:1.8879305E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r15 = org.telegram.messenger.UserObject.getFirstName(r1)
            r16 = 0
            r13[r16] = r15
            java.lang.String r15 = "FwdMessageToUser"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r15, r12, r13)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            r2.setText(r12)
            goto L_0x16b1
        L_0x1695:
            r16 = 0
            android.widget.TextView r2 = r7.infoTextView
            r12 = 2131626044(0x7f0e083c, float:1.8879313E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r1)
            r13[r16] = r3
            java.lang.String r3 = "FwdMessagesToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x16b1:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            r1 = 300(0x12c, double:1.48E-321)
            goto L_0x1719
        L_0x16be:
            r1 = r11
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r0.intValue()
            r3 = 1
            if (r2 != r3) goto L_0x16ec
            android.widget.TextView r2 = r7.infoTextView
            java.lang.Object[] r13 = new java.lang.Object[r3]
            r3 = 0
            java.lang.Object[] r15 = new java.lang.Object[r3]
            java.lang.String r12 = "Chats"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1, r15)
            r13[r3] = r12
            java.lang.String r12 = "FwdMessageToChats"
            r15 = 2131626037(0x7f0e0835, float:1.8879299E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r15, r13)
            android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
            r2.setText(r12)
            goto L_0x170c
        L_0x16ec:
            r3 = 0
            android.widget.TextView r2 = r7.infoTextView
            r13 = 1
            java.lang.Object[] r15 = new java.lang.Object[r13]
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r12 = "Chats"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1, r13)
            r15[r3] = r12
            java.lang.String r3 = "FwdMessagesToChats"
            r12 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r15)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x170c:
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r12 = 30
            r2.setAnimation(r3, r12, r12)
            r2 = 300(0x12c, double:1.48E-321)
            r1 = r2
        L_0x1719:
            r12 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r12
            r0 = r1
            goto L_0x1895
        L_0x1720:
            r0 = 61
            if (r3 != r0) goto L_0x1893
            r0 = r10
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r11 != 0) goto L_0x17b8
            int r1 = r7.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r1 = r1.clientUserId
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x1753
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.String r3 = "BackgroundToSavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r1.setText(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558526(0x7f0d007e, float:1.874237E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x17e9
        L_0x1753:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r1 == 0) goto L_0x1783
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r2 = -r5
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            android.widget.TextView r2 = r7.infoTextView
            r3 = 2131624657(0x7f0e02d1, float:1.88765E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = r1.title
            r15 = 0
            r13[r15] = r12
            java.lang.String r12 = "BackgroundToGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            goto L_0x17ad
        L_0x1783:
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            android.widget.TextView r2 = r7.infoTextView
            r3 = 2131624659(0x7f0e02d3, float:1.8876504E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r1)
            r15 = 0
            r13[r15] = r12
            java.lang.String r12 = "BackgroundToUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
        L_0x17ad:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r3 = 30
            r1.setAnimation(r2, r3, r3)
            goto L_0x17e9
        L_0x17b8:
            r1 = r11
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.widget.TextView r2 = r7.infoTextView
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            r12 = 0
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r3 = "Chats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r1, r15)
            r13[r12] = r3
            java.lang.String r3 = "BackgroundToChats"
            r12 = 2131624656(0x7f0e02d0, float:1.8876498E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r12, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r2.setText(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            r3 = 2131558454(0x7f0d0036, float:1.8742224E38)
            r12 = 30
            r2.setAnimation(r3, r12, r12)
        L_0x17e9:
            r1 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r1
            goto L_0x1893
        L_0x17ef:
            boolean r0 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r0 != 0) goto L_0x17f6
            return
        L_0x17f6:
            r0 = 2131558433(0x7f0d0021, float:1.8742182E38)
            int r1 = r7.currentAction
            r2 = 80
            if (r1 != r2) goto L_0x180f
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131625597(0x7f0e067d, float:1.8878406E38)
            java.lang.String r3 = "EmailCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x187f
        L_0x180f:
            r2 = 60
            if (r1 != r2) goto L_0x1822
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131627488(0x7f0e0de0, float:1.8882242E38)
            java.lang.String r3 = "PhoneCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x187f
        L_0x1822:
            r2 = 56
            if (r1 != r2) goto L_0x1835
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131628846(0x7f0e132e, float:1.8884996E38)
            java.lang.String r3 = "UsernameCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x187f
        L_0x1835:
            r2 = 57
            if (r1 != r2) goto L_0x1848
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626128(0x7f0e0890, float:1.8879483E38)
            java.lang.String r3 = "HashtagCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x187f
        L_0x1848:
            r2 = 52
            if (r1 != r2) goto L_0x185b
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            java.lang.String r3 = "MessageCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x187f
        L_0x185b:
            r2 = 59
            if (r1 != r2) goto L_0x1871
            r0 = 2131558590(0x7f0d00be, float:1.87425E38)
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            java.lang.String r3 = "LinkCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x187f
        L_0x1871:
            android.widget.TextView r1 = r7.infoTextView
            r2 = 2131628595(0x7f0e1233, float:1.8884487E38)
            java.lang.String r3 = "TextCopied"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x187f:
            org.telegram.ui.Components.RLottieImageView r1 = r7.leftImageView
            r2 = 30
            r1.setAnimation(r0, r2, r2)
            r1 = 3000(0xbb8, double:1.482E-320)
            r7.timeLeft = r1
            android.widget.TextView r1 = r7.infoTextView
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1
            r1.setTextSize(r3, r2)
        L_0x1893:
            r0 = r32
        L_0x1895:
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
            if (r12 <= 0) goto L_0x18d3
            org.telegram.ui.Components.RLottieImageView r2 = r7.leftImageView
            org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda5
            r3.<init>(r7)
            r2.postDelayed(r3, r0)
        L_0x18d3:
            r0 = r4
        L_0x18d4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.widget.TextView r2 = r7.infoTextView
            java.lang.CharSequence r2 = r2.getText()
            r1.append(r2)
            android.widget.TextView r2 = r7.subinfoTextView
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x1901
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = ". "
            r2.append(r3)
            android.widget.TextView r3 = r7.subinfoTextView
            java.lang.CharSequence r3 = r3.getText()
            r2.append(r3)
            java.lang.String r14 = r2.toString()
        L_0x1901:
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.AndroidUtilities.makeAccessibilityAnnouncement(r1)
            boolean r1 = r34.isMultilineSubInfo()
            if (r1 == 0) goto L_0x1957
            android.view.ViewParent r1 = r34.getParent()
            r12 = r1
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r1 = r12.getMeasuredWidth()
            if (r1 != 0) goto L_0x1922
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r2.x
        L_0x1922:
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
            goto L_0x19fa
        L_0x1957:
            r24 = r5
            r16 = r28
            r18 = r29
            boolean r1 = r34.hasSubInfo()
            if (r1 == 0) goto L_0x196d
            r1 = 1112539136(0x42500000, float:52.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.undoViewHeight = r1
            goto L_0x19fa
        L_0x196d:
            android.view.ViewParent r1 = r34.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x19fa
            android.view.ViewParent r1 = r34.getParent()
            r12 = r1
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r1 = r12.getMeasuredWidth()
            int r2 = r12.getPaddingLeft()
            int r1 = r1 - r2
            int r2 = r12.getPaddingRight()
            int r1 = r1 - r2
            if (r1 > 0) goto L_0x1990
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r2.x
        L_0x1990:
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
            if (r2 == r3) goto L_0x19c4
            r3 = 17
            if (r2 == r3) goto L_0x19c4
            r3 = 18
            if (r2 != r3) goto L_0x19c1
            goto L_0x19c4
        L_0x19c1:
            r15 = 1105199104(0x41e00000, float:28.0)
            goto L_0x19c6
        L_0x19c4:
            r15 = 1096810496(0x41600000, float:14.0)
        L_0x19c6:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r1 = r1 + r2
            r7.undoViewHeight = r1
            int r2 = r7.currentAction
            r3 = 18
            if (r2 != r3) goto L_0x19e0
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r7.undoViewHeight = r1
            goto L_0x19fa
        L_0x19e0:
            r3 = 25
            if (r2 != r3) goto L_0x19f1
            r2 = 1112014848(0x42480000, float:50.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = java.lang.Math.max(r1, r2)
            r7.undoViewHeight = r1
            goto L_0x19fa
        L_0x19f1:
            if (r17 == 0) goto L_0x19fa
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r1 = r1 - r2
            r7.undoViewHeight = r1
        L_0x19fa:
            int r1 = r34.getVisibility()
            if (r1 == 0) goto L_0x1a5d
            r1 = 0
            r7.setVisibility(r1)
            boolean r1 = r7.fromTop
            if (r1 == 0) goto L_0x1a0b
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1a0d
        L_0x1a0b:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x1a0d:
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
            if (r4 == 0) goto L_0x1a2a
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x1a2c
        L_0x1a2a:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x1a2c:
            int r6 = r7.enterOffsetMargin
            int r12 = r7.undoViewHeight
            int r6 = r6 + r12
            float r6 = (float) r6
            float r5 = r5 * r6
            r6 = 0
            r2[r6] = r5
            if (r4 == 0) goto L_0x1a3c
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x1a3e
        L_0x1a3c:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x1a3e:
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
        L_0x1a5d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(java.util.ArrayList, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* renamed from: lambda$showWithAction$2$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m1537lambda$showWithAction$2$orgtelegramuiComponentsUndoView(View view) {
        hide(false, 1);
    }

    static /* synthetic */ boolean lambda$showWithAction$3(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$showWithAction$6$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m1540lambda$showWithAction$6$orgtelegramuiComponentsUndoView(TLRPC.Message message, View v) {
        hide(true, 1);
        TLRPC.TL_payments_getPaymentReceipt req = new TLRPC.TL_payments_getPaymentReceipt();
        req.msg_id = message.id;
        req.peer = this.parentFragment.getMessagesController().getInputPeer(message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(req, new UndoView$$ExternalSyntheticLambda7(this), 2);
    }

    /* renamed from: lambda$showWithAction$5$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m1539lambda$showWithAction$5$orgtelegramuiComponentsUndoView(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new UndoView$$ExternalSyntheticLambda6(this, response));
    }

    /* renamed from: lambda$showWithAction$4$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m1538lambda$showWithAction$4$orgtelegramuiComponentsUndoView(TLObject response) {
        if (response instanceof TLRPC.TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response));
        }
    }

    /* renamed from: lambda$showWithAction$7$org-telegram-ui-Components-UndoView  reason: not valid java name */
    public /* synthetic */ void m1541lambda$showWithAction$7$orgtelegramuiComponentsUndoView() {
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
