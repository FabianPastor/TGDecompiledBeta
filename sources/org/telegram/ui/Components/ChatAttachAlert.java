package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerSearchActivity;

public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    public final Property<AttachAlertLayout, Float> ATTACH_ALERT_LAYOUT_TRANSLATION;
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    protected ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    protected boolean allowOrder;
    /* access modifiers changed from: private */
    public final Paint attachButtonPaint;
    /* access modifiers changed from: private */
    public int attachItemSize;
    /* access modifiers changed from: private */
    public ChatAttachAlertAudioLayout audioLayout;
    protected int avatarPicker;
    protected boolean avatarSearch;
    protected BaseFragment baseFragment;
    /* access modifiers changed from: private */
    public float baseSelectedTextViewTranslationY;
    /* access modifiers changed from: private */
    public float bottomPannelTranslation;
    private boolean buttonPressed;
    /* access modifiers changed from: private */
    public ButtonsAdapter buttonsAdapter;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    /* access modifiers changed from: private */
    public float captionEditTextTopOffset;
    /* access modifiers changed from: private */
    public final NumberTextView captionLimitView;
    /* access modifiers changed from: private */
    public float chatActivityEnterViewAnimateFromTop;
    /* access modifiers changed from: private */
    public int codepointCount;
    protected EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public AnimatorSet commentsAnimator;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius;
    protected int currentAccount;
    /* access modifiers changed from: private */
    public AttachAlertLayout currentAttachLayout;
    /* access modifiers changed from: private */
    public final int currentLimit;
    float currentPanTranslationY;
    private DecelerateInterpolator decelerateInterpolator;
    protected ChatAttachViewDelegate delegate;
    /* access modifiers changed from: private */
    public ChatAttachAlertDocumentLayout documentLayout;
    protected ActionBarMenuItem doneItem;
    protected MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public boolean enterCommentEventSent;
    /* access modifiers changed from: private */
    public ArrayList<Rect> exclusionRects;
    /* access modifiers changed from: private */
    public Rect exclustionRect;
    private final boolean forceDarkTheme;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public float fromScrollY;
    protected boolean inBubbleMode;
    private ActionBarMenuSubItem[] itemCells;
    private AttachAlertLayout[] layouts;
    /* access modifiers changed from: private */
    public ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos;
    /* access modifiers changed from: private */
    public boolean mediaEnabled;
    /* access modifiers changed from: private */
    public AnimatorSet menuAnimator;
    /* access modifiers changed from: private */
    public boolean menuShowed;
    /* access modifiers changed from: private */
    public AttachAlertLayout nextAttachLayout;
    /* access modifiers changed from: private */
    public boolean openTransitionFinished;
    protected boolean openWithFrontFaceCamera;
    /* access modifiers changed from: private */
    public Paint paint;
    protected boolean paused;
    /* access modifiers changed from: private */
    public ChatAttachAlertPhotoLayout photoLayout;
    /* access modifiers changed from: private */
    public ChatAttachAlertPollLayout pollLayout;
    /* access modifiers changed from: private */
    public boolean pollsEnabled;
    /* access modifiers changed from: private */
    public int previousScrollOffsetY;
    /* access modifiers changed from: private */
    public RectF rect;
    protected int[] scrollOffsetY;
    protected ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public View selectedCountView;
    /* access modifiers changed from: private */
    public int selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    /* access modifiers changed from: private */
    public ValueAnimator sendButtonColorAnimator;
    boolean sendButtonEnabled;
    /* access modifiers changed from: private */
    public float sendButtonEnabledProgress;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    /* access modifiers changed from: private */
    public View shadow;
    private final boolean showingFromDialog;
    protected SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint;
    /* access modifiers changed from: private */
    public float toScrollY;
    /* access modifiers changed from: private */
    public ValueAnimator topBackgroundAnimator;
    public float translationProgress;
    protected boolean typeButtonsAvailable;
    /* access modifiers changed from: private */
    public Object viewChangeAnimator;
    /* access modifiers changed from: private */
    public ImageView writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public float getClipLayoutBottom() {
        return ((float) this.frameLayout2.getMeasuredHeight()) - (((float) (this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f))) * (1.0f - this.frameLayout2.getAlpha()));
    }

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3);

        void didSelectBot(TLRPC.User user);

        void doOnIdle(Runnable runnable);

        View getRevealView();

        boolean needEnterComment();

        void onCameraOpened();

        void openAvatarsSearch();

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ChatAttachViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$openAvatarsSearch(ChatAttachViewDelegate _this) {
            }
        }
    }

    public static class AttachAlertLayout extends FrameLayout {
        protected ChatAttachAlert parentAlert;
        protected final Theme.ResourcesProvider resourcesProvider;

        public AttachAlertLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            this.parentAlert = alert;
        }

        /* access modifiers changed from: package-private */
        public boolean onSheetKeyDown(int keyCode, KeyEvent event) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean onDismiss() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean onCustomMeasure(View view, int width, int height) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean onContainerViewTouchEvent(MotionEvent event) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void onPreMeasure(int availableWidth, int availableHeight) {
        }

        /* access modifiers changed from: package-private */
        public void onMenuItemClick(int id) {
        }

        /* access modifiers changed from: package-private */
        public void onButtonsTranslationYUpdated() {
        }

        /* access modifiers changed from: package-private */
        public boolean canScheduleMessages() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public void checkColors() {
        }

        /* access modifiers changed from: package-private */
        public ArrayList<ThemeDescription> getThemeDescriptions() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public void onPause() {
        }

        /* access modifiers changed from: package-private */
        public void onResume() {
        }

        /* access modifiers changed from: package-private */
        public boolean canDismissWithTouchOutside() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public void onDismissWithButtonClick(int item) {
        }

        /* access modifiers changed from: package-private */
        public void onContainerTranslationUpdated(float currentPanTranslationY) {
        }

        /* access modifiers changed from: package-private */
        public void onHideShowProgress(float progress) {
        }

        /* access modifiers changed from: package-private */
        public void onOpenAnimationEnd() {
        }

        /* access modifiers changed from: package-private */
        public void onInit(boolean mediaEnabled) {
        }

        /* access modifiers changed from: package-private */
        public int getSelectedItemsCount() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public void onSelectedItemsCountChanged(int count) {
        }

        /* access modifiers changed from: package-private */
        public void applyCaption(String text) {
        }

        /* access modifiers changed from: package-private */
        public void onDestroy() {
        }

        /* access modifiers changed from: package-private */
        public void onHide() {
        }

        /* access modifiers changed from: package-private */
        public void onHidden() {
        }

        /* access modifiers changed from: package-private */
        public int getCurrentItemTop() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int getFirstOffset() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12.0f : 17.0f);
        }

        /* access modifiers changed from: package-private */
        public int getListTopPadding() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int needsActionBar() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public void sendSelectedItems(boolean notify, int scheduleDate) {
        }

        /* access modifiers changed from: package-private */
        public void onShow() {
        }

        /* access modifiers changed from: package-private */
        public void onShown() {
        }

        /* access modifiers changed from: package-private */
        public void scrollToTop() {
        }

        /* access modifiers changed from: package-private */
        public boolean onBackPressed() {
            return false;
        }

        /* access modifiers changed from: protected */
        public int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    private class AttachButton extends FrameLayout {
        private String backgroundKey;
        private Animator checkAnimator;
        private boolean checked;
        /* access modifiers changed from: private */
        public float checkedState;
        private int currentId;
        /* access modifiers changed from: private */
        public RLottieImageView imageView;
        /* access modifiers changed from: private */
        public String textKey;
        /* access modifiers changed from: private */
        public TextView textView;

        public AttachButton(Context context) {
            super(context);
            setWillNotDraw(false);
            AnonymousClass1 r0 = new RLottieImageView(context, ChatAttachAlert.this) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    AttachButton.this.invalidate();
                }
            };
            this.imageView = r0;
            r0.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(32, 32.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean animate) {
            if (this.checked != (this.currentId == ChatAttachAlert.this.selectedId)) {
                this.checked = this.currentId == ChatAttachAlert.this.selectedId;
                Animator animator = this.checkAnimator;
                if (animator != null) {
                    animator.cancel();
                }
                float f = 1.0f;
                if (animate) {
                    if (this.checked) {
                        this.imageView.setProgress(0.0f);
                        this.imageView.playAnimation();
                    }
                    float[] fArr = new float[1];
                    if (!this.checked) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                    this.checkAnimator = ofFloat;
                    ofFloat.setDuration(200);
                    this.checkAnimator.start();
                    return;
                }
                this.imageView.stopAnimation();
                this.imageView.setProgress(0.0f);
                if (!this.checked) {
                    f = 0.0f;
                }
                setCheckedState(f);
            }
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            this.imageView.setScaleX(1.0f - (state * 0.06f));
            this.imageView.setScaleY(1.0f - (0.06f * state));
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
            invalidate();
        }

        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), NUM));
        }

        public void setTextAndIcon(int id, CharSequence text, RLottieDrawable drawable, String background, String textColor) {
            this.currentId = id;
            this.textView.setText(text);
            this.imageView.setAnimation(drawable);
            this.backgroundKey = background;
            this.textKey = textColor;
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float scale = this.imageView.getScaleX() + (this.checkedState * 0.06f);
            float radius = ((float) AndroidUtilities.dp(23.0f)) * scale;
            float cx = (float) (this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2));
            float cy = (float) (this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2));
            ChatAttachAlert.this.attachButtonPaint.setColor(ChatAttachAlert.this.getThemedColor(this.backgroundKey));
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
            ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(((float) AndroidUtilities.dp(3.0f)) * scale);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(cx, cy, radius - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        /* access modifiers changed from: private */
        public TLRPC.User currentUser;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        /* access modifiers changed from: private */
        public TextView nameTextView;

        public AttachBotButton(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View selector = new View(context);
                selector.setBackgroundDrawable(Theme.createSelectorDrawable(ChatAttachAlert.this.getThemedColor("dialogButtonSelector"), 1, AndroidUtilities.dp(23.0f)));
                addView(selector, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            }
            TextView textView = new TextView(context);
            this.nameTextView = textView;
            textView.setTextSize(1, 12.0f);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 60.0f, 6.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setUser(TLRPC.User user) {
            if (user != null) {
                this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
                this.currentUser = user;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
                requestLayout();
            }
        }
    }

    public ChatAttachAlert(Context context, BaseFragment parentFragment, boolean forceDarkTheme2, boolean showingFromDialog2) {
        this(context, parentFragment, forceDarkTheme2, showingFromDialog2, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlert(android.content.Context r35, org.telegram.ui.ActionBar.BaseFragment r36, boolean r37, boolean r38, org.telegram.ui.ActionBar.Theme.ResourcesProvider r39) {
        /*
            r34 = this;
            r7 = r34
            r8 = r35
            r9 = r36
            r10 = r37
            r11 = r38
            r12 = r39
            r13 = 0
            r7.<init>(r8, r13, r12)
            org.telegram.ui.Components.ChatAttachAlert$1 r0 = new org.telegram.ui.Components.ChatAttachAlert$1
            java.lang.String r1 = "translation"
            r0.<init>(r1)
            r7.ATTACH_ALERT_LAYOUT_TRANSLATION = r0
            r14 = 6
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = new org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout[r14]
            r7.layouts = r0
            android.text.TextPaint r0 = new android.text.TextPaint
            r15 = 1
            r0.<init>(r15)
            r7.textPaint = r0
            android.graphics.RectF r0 = new android.graphics.RectF
            r0.<init>()
            r7.rect = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r15)
            r7.paint = r0
            r7.sendButtonEnabled = r15
            r6 = 1065353216(0x3var_, float:1.0)
            r7.sendButtonEnabledProgress = r6
            r7.cornerRadius = r6
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r7.currentAccount = r0
            r7.mediaEnabled = r15
            r7.pollsEnabled = r15
            r5 = -1
            r7.maxSelectedPhotos = r5
            r7.allowOrder = r15
            r0 = 1118437376(0x42aa0000, float:85.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.attachItemSize = r0
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r0.<init>()
            r7.decelerateInterpolator = r0
            r4 = 2
            int[] r0 = new int[r4]
            r7.scrollOffsetY = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r15)
            r7.attachButtonPaint = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.exclusionRects = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.exclustionRect = r0
            org.telegram.ui.Components.ChatAttachAlert$18 r0 = new org.telegram.ui.Components.ChatAttachAlert$18
            java.lang.String r1 = "openProgress"
            r0.<init>(r1)
            r7.ATTACH_ALERT_PROGRESS = r0
            r7.forceDarkTheme = r10
            r7.showingFromDialog = r11
            r7.drawNavigationBar = r15
            boolean r0 = r9 instanceof org.telegram.ui.ChatActivity
            if (r0 == 0) goto L_0x008d
            boolean r0 = r36.isInBubbleMode()
            if (r0 == 0) goto L_0x008d
            r0 = 1
            goto L_0x008e
        L_0x008d:
            r0 = 0
        L_0x008e:
            r7.inBubbleMode = r0
            android.view.animation.OvershootInterpolator r0 = new android.view.animation.OvershootInterpolator
            r1 = 1060320051(0x3var_, float:0.7)
            r0.<init>(r1)
            r7.openInterpolator = r0
            r7.baseFragment = r9
            r7.useSmoothKeyboard = r15
            r7.setDelegate(r7)
            int r0 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.reloadInlineHints
            r0.addObserver(r7, r1)
            java.util.ArrayList<android.graphics.Rect> r0 = r7.exclusionRects
            android.graphics.Rect r1 = r7.exclustionRect
            r0.add(r1)
            org.telegram.ui.Components.ChatAttachAlert$2 r0 = new org.telegram.ui.Components.ChatAttachAlert$2
            r0.<init>(r8, r10)
            r7.sizeNotifierFrameLayout = r0
            r7.containerView = r0
            android.view.ViewGroup r0 = r7.containerView
            r0.setWillNotDraw(r13)
            android.view.ViewGroup r0 = r7.containerView
            r0.setClipChildren(r13)
            android.view.ViewGroup r0 = r7.containerView
            int r1 = r7.backgroundPaddingLeft
            int r2 = r7.backgroundPaddingLeft
            r0.setPadding(r1, r13, r2, r13)
            org.telegram.ui.Components.ChatAttachAlert$3 r0 = new org.telegram.ui.Components.ChatAttachAlert$3
            r0.<init>(r8, r12)
            r7.actionBar = r0
            java.lang.String r3 = "dialogBackground"
            int r1 = r7.getThemedColor(r3)
            r0.setBackgroundColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131165485(0x7var_d, float:1.7945188E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.String r2 = "dialogTextBlack"
            int r1 = r7.getThemedColor(r2)
            r0.setItemsColor(r1, r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.String r1 = "dialogButtonSelector"
            int r4 = r7.getThemedColor(r1)
            r0.setItemsBackgroundColor(r4, r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            int r4 = r7.getThemedColor(r2)
            r0.setTitleColor(r4)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r0.setOccupyStatusBar(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r4 = 0
            r0.setAlpha(r4)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.Components.ChatAttachAlert$4 r4 = new org.telegram.ui.Components.ChatAttachAlert$4
            r4.<init>()
            r0.setActionBarMenuOnItemClick(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r17 = 0
            r18 = 0
            int r19 = r7.getThemedColor(r2)
            r20 = 0
            r0 = r4
            r15 = r1
            r1 = r35
            r22 = r2
            r2 = r17
            r23 = r3
            r3 = r18
            r14 = r4
            r4 = r19
            r5 = r20
            r6 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.selectedMenuItem = r14
            r14.setLongClickEnabled(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 2131165492(0x7var_, float:1.7945203E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 2131623987(0x7f0e0033, float:1.887514E38)
            java.lang.String r2 = "AccDescrMoreOptions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r14 = 4
            r0.setVisibility(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r6 = 2
            r0.setSubMenuOpenSide(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda2
            r1.<init>(r7)
            r0.setDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setAdditionalYOffset(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            int r1 = r7.getThemedColor(r15)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r3 = 0
            java.lang.String r0 = "windowBackgroundWhiteBlueHeader"
            int r4 = r7.getThemedColor(r0)
            r16 = 1
            r0 = r5
            r1 = r35
            r14 = r5
            r5 = r16
            r6 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.doneItem = r14
            r14.setLongClickEnabled(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r1 = 2131625100(0x7f0e048c, float:1.8877398E38)
            java.lang.String r2 = "Create"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r1 = 4
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r14 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            int r1 = r7.getThemedColor(r15)
            r2 = 3
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r5 = r22
            int r4 = r7.getThemedColor(r5)
            r16 = 0
            r0 = r6
            r1 = r35
            r14 = r5
            r5 = r16
            r13 = r6
            r6 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.searchItem = r13
            r0 = 0
            r13.setLongClickEnabled(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 2131165495(0x7var_, float:1.7945209E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 2131627616(0x7f0e0e60, float:1.8882501E38)
            java.lang.String r2 = "Search"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 4
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 1109917696(0x42280000, float:42.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            int r1 = r7.getThemedColor(r15)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda16 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda16
            r1.<init>(r7, r11)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$6 r0 = new org.telegram.ui.Components.ChatAttachAlert$6
            r0.<init>(r8)
            r7.selectedTextView = r0
            int r1 = r7.getThemedColor(r14)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r7.selectedTextView
            r1 = 1098907648(0x41800000, float:16.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r7.selectedTextView
            java.lang.String r13 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r7.selectedTextView
            r1 = 51
            r0.setGravity(r1)
            android.widget.TextView r0 = r7.selectedTextView
            r1 = 4
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.selectedTextView
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = r7.layouts
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout
            r1.<init>(r7, r8, r10, r12)
            r7.photoLayout = r1
            r2 = 0
            r0[r2] = r1
            r7.currentAttachLayout = r1
            r0 = 1
            r7.selectedId = r0
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = r7.photoLayout
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.TextView r1 = r7.selectedTextView
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1102577664(0x41b80000, float:23.0)
            r31 = 0
            r32 = 1111490560(0x42400000, float:48.0)
            r33 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBar r1 = r7.actionBar
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.selectedMenuItem
            r2 = 48
            r4 = 53
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r4)
            r0.addView(r1, r5)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.searchItem
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r4)
            r0.addView(r1, r5)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.doneItem
            r5 = -2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r2, (int) r4)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.actionBarShadow = r0
            r1 = 0
            r0.setAlpha(r1)
            android.view.View r0 = r7.actionBarShadow
            java.lang.String r1 = "dialogShadowLine"
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.view.View r1 = r7.actionBarShadow
            r2 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.shadow = r0
            r1 = 2131165271(0x7var_, float:1.7944754E38)
            r0.setBackgroundResource(r1)
            android.view.View r0 = r7.shadow
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1.<init>(r14, r2)
            r0.setColorFilter(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.view.View r1 = r7.shadow
            r28 = 1073741824(0x40000000, float:2.0)
            r29 = 83
            r30 = 0
            r32 = 0
            r33 = 1118306304(0x42a80000, float:84.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$7 r0 = new org.telegram.ui.Components.ChatAttachAlert$7
            r0.<init>(r8)
            r7.buttonsRecyclerView = r0
            org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter
            r1.<init>(r8)
            r7.buttonsAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r2 = 0
            r1.<init>(r8, r2, r2)
            r7.buttonsLayoutManager = r1
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r0.setVerticalScrollBarEnabled(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r0.setHorizontalScrollBarEnabled(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r15 = 0
            r0.setItemAnimator(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r0.setLayoutAnimation(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = r7.getThemedColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r1 = r23
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r7.buttonsRecyclerView
            r2 = 84
            r4 = 83
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r2, (int) r4)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda10
            r1.<init>(r7, r12)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12
            r1.<init>(r7)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            org.telegram.ui.Components.ChatAttachAlert$8 r0 = new org.telegram.ui.Components.ChatAttachAlert$8
            r0.<init>(r8, r10)
            r7.frameLayout2 = r0
            r1 = 0
            r0.setWillNotDraw(r1)
            android.widget.FrameLayout r0 = r7.frameLayout2
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r7.frameLayout2
            r1 = 0
            r0.setAlpha(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.FrameLayout r1 = r7.frameLayout2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r5, (int) r4)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r7.frameLayout2
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda18 r1 = org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda18.INSTANCE
            r0.setOnTouchListener(r1)
            org.telegram.ui.Components.NumberTextView r0 = new org.telegram.ui.Components.NumberTextView
            r0.<init>(r8)
            r7.captionLimitView = r0
            r1 = 8
            r0.setVisibility(r1)
            r1 = 15
            r0.setTextSize(r1)
            java.lang.String r1 = "windowBackgroundWhiteGrayText"
            int r1 = r7.getThemedColor(r1)
            r0.setTextColor(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r1)
            r1 = 1
            r0.setCenterAlign(r1)
            android.widget.FrameLayout r1 = r7.frameLayout2
            r27 = 56
            r28 = 1101004800(0x41a00000, float:20.0)
            r29 = 85
            r30 = 1077936128(0x40400000, float:3.0)
            r32 = 1096810496(0x41600000, float:14.0)
            r33 = 1117519872(0x429CLASSNAME, float:78.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r1.addView(r0, r2)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.maxCaptionLength
            r7.currentLimit = r0
            org.telegram.ui.Components.ChatAttachAlert$9 r6 = new org.telegram.ui.Components.ChatAttachAlert$9
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r7.sizeNotifierFrameLayout
            r4 = 0
            r5 = 1
            r0 = r6
            r1 = r34
            r2 = r35
            r15 = r6
            r6 = r39
            r0.<init>(r2, r3, r4, r5, r6)
            r7.commentTextView = r15
            r0 = 2131624206(0x7f0e010e, float:1.8875585E38)
            java.lang.String r1 = "AddCaption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r15.setHint(r0)
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            r0.onResume()
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            org.telegram.ui.Components.EditTextCaption r0 = r0.getEditText()
            org.telegram.ui.Components.ChatAttachAlert$10 r1 = new org.telegram.ui.Components.ChatAttachAlert$10
            r1.<init>()
            r0.addTextChangedListener(r1)
            android.widget.FrameLayout r0 = r7.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r1 = r7.commentTextView
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 83
            r30 = 0
            r32 = 1118306304(0x42a80000, float:84.0)
            r33 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r7.frameLayout2
            r1 = 0
            r0.setClipChildren(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            r0.setClipChildren(r1)
            org.telegram.ui.Components.ChatAttachAlert$11 r0 = new org.telegram.ui.Components.ChatAttachAlert$11
            r0.<init>(r8)
            r7.writeButtonContainer = r0
            r1 = 1
            r0.setFocusable(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r0.setFocusableInTouchMode(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r0.setScaleY(r1)
            android.widget.FrameLayout r0 = r7.writeButtonContainer
            r2 = 0
            r0.setAlpha(r2)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.FrameLayout r2 = r7.writeButtonContainer
            r20 = 60
            r21 = 1114636288(0x42700000, float:60.0)
            r22 = 85
            r23 = 0
            r24 = 0
            r25 = 1086324736(0x40CLASSNAME, float:6.0)
            r26 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.writeButton = r0
            r0 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r3 = "dialogFloatingButton"
            int r3 = r7.getThemedColor(r3)
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r4 < r5) goto L_0x04e7
            java.lang.String r4 = "dialogFloatingButtonPressed"
            goto L_0x04e9
        L_0x04e7:
            java.lang.String r4 = "dialogFloatingButton"
        L_0x04e9:
            int r4 = r7.getThemedColor(r4)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r3, r4)
            r7.writeButtonDrawable = r2
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 >= r5) goto L_0x0525
            android.content.res.Resources r2 = r35.getResources()
            r3 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r14, r4)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.Drawable r4 = r7.writeButtonDrawable
            r6 = 0
            r3.<init>(r2, r4, r6, r6)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3.setIconSize(r4, r6)
            r7.writeButtonDrawable = r3
        L_0x0525:
            android.widget.ImageView r2 = r7.writeButton
            android.graphics.drawable.Drawable r3 = r7.writeButtonDrawable
            r2.setBackgroundDrawable(r3)
            android.widget.ImageView r2 = r7.writeButton
            r3 = 2131165270(0x7var_, float:1.7944752E38)
            r2.setImageResource(r3)
            android.widget.ImageView r2 = r7.writeButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "dialogFloatingIcon"
            int r4 = r7.getThemedColor(r4)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r7.writeButton
            r3 = 2
            r2.setImportantForAccessibility(r3)
            android.widget.ImageView r2 = r7.writeButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x0561
            android.widget.ImageView r2 = r7.writeButton
            org.telegram.ui.Components.ChatAttachAlert$12 r3 = new org.telegram.ui.Components.ChatAttachAlert$12
            r3.<init>()
            r2.setOutlineProvider(r3)
        L_0x0561:
            android.widget.FrameLayout r2 = r7.writeButtonContainer
            android.widget.ImageView r3 = r7.writeButton
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r5) goto L_0x056e
            r4 = 56
            r20 = 56
            goto L_0x0572
        L_0x056e:
            r4 = 60
            r20 = 60
        L_0x0572:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r5) goto L_0x0579
            r21 = 1113587712(0x42600000, float:56.0)
            goto L_0x057d
        L_0x0579:
            r0 = 1114636288(0x42700000, float:60.0)
            r21 = 1114636288(0x42700000, float:60.0)
        L_0x057d:
            r22 = 51
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r5) goto L_0x0588
            r4 = 1073741824(0x40000000, float:2.0)
            r23 = 1073741824(0x40000000, float:2.0)
            goto L_0x058a
        L_0x0588:
            r23 = 0
        L_0x058a:
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r3, r0)
            android.widget.ImageView r0 = r7.writeButton
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda15 r2 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda15
            r2.<init>(r7, r12)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r7.writeButton
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda17 r2 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda17
            r2.<init>(r7, r12)
            r0.setOnLongClickListener(r2)
            android.text.TextPaint r0 = r7.textPaint
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = r7.textPaint
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r2)
            org.telegram.ui.Components.ChatAttachAlert$14 r0 = new org.telegram.ui.Components.ChatAttachAlert$14
            r0.<init>(r8)
            r7.selectedCountView = r0
            r2 = 0
            r0.setAlpha(r2)
            android.view.View r0 = r7.selectedCountView
            r0.setScaleX(r1)
            android.view.View r0 = r7.selectedCountView
            r0.setScaleY(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.view.View r1 = r7.selectedCountView
            r18 = 42
            r19 = 1103101952(0x41CLASSNAME, float:24.0)
            r20 = 85
            r21 = 0
            r22 = 0
            r23 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r24 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            if (r10 == 0) goto L_0x05f6
            r34.checkColors()
            r0 = 0
            r7.navBarColorKey = r0
        L_0x05f6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2093lambda$new$0$orgtelegramuiComponentsChatAttachAlert(int id) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(id);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2094lambda$new$1$orgtelegramuiComponentsChatAttachAlert(View v) {
        this.selectedMenuItem.toggleSubMenu();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2101lambda$new$2$orgtelegramuiComponentsChatAttachAlert(View v) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2102lambda$new$3$orgtelegramuiComponentsChatAttachAlert(boolean showingFromDialog2, View v) {
        if (this.avatarPicker != 0) {
            this.delegate.openAvatarsSearch();
            dismiss();
            return;
        }
        final HashMap<Object, Object> photos = new HashMap<>();
        final ArrayList<Object> order = new ArrayList<>();
        PhotoPickerSearchActivity fragment = new PhotoPickerSearchActivity(photos, order, 0, true, (ChatActivity) this.baseFragment);
        fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            private boolean sendPressed;

            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            public void selectedPhotosChanged() {
            }

            public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                if (!canceled && !photos.isEmpty() && !this.sendPressed) {
                    this.sendPressed = true;
                    ArrayList media = new ArrayList();
                    for (int a = 0; a < order.size(); a++) {
                        Object object = photos.get(order.get(a));
                        SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                        media.add(info);
                        MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                        if (searchImage.imagePath != null) {
                            info.path = searchImage.imagePath;
                        } else {
                            info.searchImage = searchImage;
                        }
                        info.thumbPath = searchImage.thumbPath;
                        info.videoEditedInfo = searchImage.editedInfo;
                        info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                        info.entities = searchImage.entities;
                        info.masks = searchImage.stickers;
                        info.ttl = searchImage.ttl;
                        if (searchImage.inlineResult != null && searchImage.type == 1) {
                            info.inlineResult = searchImage.inlineResult;
                            info.params = searchImage.params;
                        }
                        searchImage.date = (int) (System.currentTimeMillis() / 1000);
                    }
                    ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectSearchPhotos(media, notify, scheduleDate);
                }
            }

            public void onCaptionChanged(CharSequence text) {
            }
        });
        fragment.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        if (showingFromDialog2) {
            this.baseFragment.showAsSheet(fragment);
        } else {
            this.baseFragment.presentFragment(fragment);
        }
        dismiss();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2105lambda$new$6$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        if (this.baseFragment.getParentActivity() != null) {
            if (view instanceof AttachButton) {
                int num = ((Integer) view.getTag()).intValue();
                if (num == 1) {
                    showLayout(this.photoLayout);
                } else if (num == 3) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                        openAudioLayout(true);
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    }
                } else if (num == 4) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                        openDocumentsLayout(true);
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    }
                } else if (num == 5) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                        openContactsLayout();
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
                        return;
                    }
                } else if (num == 6) {
                    if (AndroidUtilities.isGoogleMapsInstalled(this.baseFragment)) {
                        if (this.locationLayout == null) {
                            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                            ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = new ChatAttachAlertLocationLayout(this, getContext(), resourcesProvider);
                            this.locationLayout = chatAttachAlertLocationLayout;
                            attachAlertLayoutArr[5] = chatAttachAlertLocationLayout;
                            chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda8(this));
                        }
                        showLayout(this.locationLayout);
                    } else {
                        return;
                    }
                } else if (num == 9) {
                    if (this.pollLayout == null) {
                        AttachAlertLayout[] attachAlertLayoutArr2 = this.layouts;
                        ChatAttachAlertPollLayout chatAttachAlertPollLayout = new ChatAttachAlertPollLayout(this, getContext(), resourcesProvider);
                        this.pollLayout = chatAttachAlertPollLayout;
                        attachAlertLayoutArr2[1] = chatAttachAlertPollLayout;
                        chatAttachAlertPollLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda9(this));
                    }
                    showLayout(this.pollLayout);
                } else {
                    this.delegate.didPressedButton(((Integer) view.getTag()).intValue(), true, true, 0, false);
                }
                int left = view.getLeft();
                int right = view.getRight();
                int extra = AndroidUtilities.dp(10.0f);
                if (left - extra < 0) {
                    this.buttonsRecyclerView.smoothScrollBy(left - extra, 0);
                } else if (right + extra > this.buttonsRecyclerView.getMeasuredWidth()) {
                    RecyclerListView recyclerListView = this.buttonsRecyclerView;
                    recyclerListView.smoothScrollBy((right + extra) - recyclerListView.getMeasuredWidth(), 0);
                }
            } else if ((view instanceof AttachBotButton) != 0) {
                this.delegate.didSelectBot(((AttachBotButton) view).currentUser);
                dismiss();
            }
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2103lambda$new$4$orgtelegramuiComponentsChatAttachAlert(TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).didSelectLocation(location, live, notify, scheduleDate);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2104lambda$new$5$orgtelegramuiComponentsChatAttachAlert(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendPoll(poll, params, notify, scheduleDate);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ boolean m2107lambda$new$8$orgtelegramuiComponentsChatAttachAlert(View view, int position) {
        if (!(view instanceof AttachBotButton)) {
            return false;
        }
        AttachBotButton button = (AttachBotButton) view;
        if (this.baseFragment == null || button.currentUser == null) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.formatString("ChatHintsDelete", NUM, ContactsController.formatName(button.currentUser.first_name, button.currentUser.last_name)));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new ChatAttachAlert$$ExternalSyntheticLambda0(this, button));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
        return true;
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2106lambda$new$7$orgtelegramuiComponentsChatAttachAlert(AttachBotButton button, DialogInterface dialogInterface, int i) {
        MediaDataController.getInstance(this.currentAccount).removeInline(button.currentUser.id);
    }

    static /* synthetic */ boolean lambda$new$9(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2096lambda$new$11$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View v) {
        if (this.currentLimit - this.codepointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator vibrator = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
                return;
            }
            return;
        }
        if (this.editingMessageObject == null) {
            BaseFragment baseFragment2 = this.baseFragment;
            if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlert$$ExternalSyntheticLambda4(this), resourcesProvider);
                return;
            }
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout) {
            sendPressed(true, 0);
            return;
        }
        attachAlertLayout.sendSelectedItems(true, 0);
        dismiss();
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2095lambda$new$10$orgtelegramuiComponentsChatAttachAlert(boolean notify, int scheduleDate) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout) {
            sendPressed(notify, scheduleDate);
            return;
        }
        attachAlertLayout.sendSelectedItems(notify, scheduleDate);
        dismiss();
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ boolean m2100lambda$new$15$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View view) {
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        View view2 = view;
        BaseFragment baseFragment2 = this.baseFragment;
        if (!(baseFragment2 instanceof ChatActivity) || this.editingMessageObject != null || this.currentLimit - this.codepointCount < 0) {
            return false;
        }
        ChatActivity chatActivity = (ChatActivity) baseFragment2;
        TLRPC.Chat currentChat = chatActivity.getCurrentChat();
        TLRPC.User user = chatActivity.getCurrentUser();
        if (chatActivity.isInScheduleMode()) {
            return false;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), resourcesProvider2);
        this.sendPopupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setAnimationEnabled(false);
        this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() != 0 || ChatAttachAlert.this.sendPopupWindow == null || !ChatAttachAlert.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                v.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                ChatAttachAlert.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        this.sendPopupLayout.setDispatchKeyEventListener(new ChatAttachAlert$$ExternalSyntheticLambda3(this));
        this.sendPopupLayout.setShownFromBotton(false);
        this.itemCells = new ActionBarMenuSubItem[2];
        int i = 0;
        int a = 0;
        for (int i2 = 2; a < i2; i2 = 2) {
            if (a == 0) {
                if (chatActivity.canScheduleMessage()) {
                    if (!this.currentAttachLayout.canScheduleMessages()) {
                    }
                }
                a++;
            } else if (a == 1 && UserObject.isUserSelf(user)) {
                a++;
            }
            int num = a;
            this.itemCells[a] = new ActionBarMenuSubItem(getContext(), a == 0, a == 1, resourcesProvider2);
            if (num == 0) {
                if (UserObject.isUserSelf(user)) {
                    this.itemCells[a].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                } else {
                    this.itemCells[a].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                }
            } else if (num == 1) {
                this.itemCells[a].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
            }
            this.itemCells[a].setMinimumWidth(AndroidUtilities.dp(196.0f));
            this.sendPopupLayout.addView(this.itemCells[a], LayoutHelper.createLinear(-1, 48));
            this.itemCells[a].setOnClickListener(new ChatAttachAlert$$ExternalSyntheticLambda14(this, num, chatActivity, resourcesProvider2));
            i++;
            a++;
        }
        this.sendPopupLayout.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(NUM);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view2.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view2, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2097lambda$new$12$orgtelegramuiComponentsChatAttachAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2099lambda$new$14$orgtelegramuiComponentsChatAttachAlert(int num, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlert$$ExternalSyntheticLambda5(this), resourcesProvider);
        } else if (num == 1) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == this.photoLayout) {
                sendPressed(false, 0);
                return;
            }
            attachAlertLayout.sendSelectedItems(false, 0);
            dismiss();
        }
    }

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2098lambda$new$13$orgtelegramuiComponentsChatAttachAlert(boolean notify, int scheduleDate) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout) {
            sendPressed(notify, scheduleDate);
            return;
        }
        attachAlertLayout.sendSelectedItems(notify, scheduleDate);
        dismiss();
    }

    public void show() {
        super.show();
        this.buttonPressed = false;
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            this.calcMandatoryInsets = ((ChatActivity) baseFragment2).isKeyboardVisible();
        }
        this.openTransitionFinished = false;
        if (Build.VERSION.SDK_INT >= 30) {
            int color = getThemedColor("windowBackgroundGray");
            if (((double) AndroidUtilities.computePerceivedBrightness(color)) < 0.721d) {
                getWindow().setNavigationBarColor(color);
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject) {
        if (this.editingMessageObject != messageObject) {
            this.editingMessageObject = messageObject;
            if (messageObject != null) {
                this.maxSelectedPhotos = 1;
                this.allowOrder = false;
            } else {
                this.maxSelectedPhotos = -1;
                this.allowOrder = true;
            }
            this.buttonsAdapter.notifyDataSetChanged();
        }
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    /* access modifiers changed from: protected */
    public void applyCaption() {
        if (this.commentTextView.length() > 0) {
            this.currentAttachLayout.applyCaption(this.commentTextView.getText().toString());
        }
    }

    private void sendPressed(boolean notify, int scheduleDate) {
        if (!this.buttonPressed) {
            BaseFragment baseFragment2 = this.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                TLRPC.Chat chat = chatActivity.getCurrentChat();
                if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(chat) && chat.megagroup) || !ChatObject.isChannel(chat))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + chatActivity.getDialogId(), !notify).commit();
                }
            }
            applyCaption();
            this.buttonPressed = true;
            this.delegate.didPressedButton(7, true, notify, scheduleDate, false);
        }
    }

    private void showLayout(AttachAlertLayout layout) {
        if (this.viewChangeAnimator == null && this.commentsAnimator == null) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == layout) {
                attachAlertLayout.scrollToTop();
                return;
            }
            int i = 4;
            if (layout == this.photoLayout) {
                this.selectedId = 1;
            } else if (layout == this.audioLayout) {
                this.selectedId = 3;
            } else if (layout == this.documentLayout) {
                this.selectedId = 4;
            } else if (layout == this.contactsLayout) {
                this.selectedId = 5;
            } else if (layout == this.locationLayout) {
                this.selectedId = 6;
            } else if (layout == this.pollLayout) {
                this.selectedId = 9;
            }
            int count = this.buttonsRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.buttonsRecyclerView.getChildAt(a);
                if (child instanceof AttachButton) {
                    ((AttachButton) child).updateCheckedState(true);
                }
            }
            int t = (this.currentAttachLayout.getFirstOffset() - AndroidUtilities.dp(11.0f)) - this.scrollOffsetY[0];
            this.nextAttachLayout = layout;
            if (Build.VERSION.SDK_INT >= 20) {
                this.container.setLayerType(2, (Paint) null);
            }
            ActionBar actionBar2 = this.actionBar;
            if (this.nextAttachLayout.needsActionBar() != 0) {
                i = 0;
            }
            actionBar2.setVisibility(i);
            this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
            if (this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
            }
            this.currentAttachLayout.onHide();
            this.nextAttachLayout.onShow();
            this.nextAttachLayout.setVisibility(0);
            this.nextAttachLayout.setAlpha(0.0f);
            if (layout.getParent() != null) {
                this.containerView.removeView(this.nextAttachLayout);
            }
            int index = this.containerView.indexOfChild(this.currentAttachLayout);
            ViewGroup viewGroup = this.containerView;
            AttachAlertLayout attachAlertLayout2 = this.nextAttachLayout;
            viewGroup.addView(attachAlertLayout2, attachAlertLayout2 == this.locationLayout ? index : index + 1, LayoutHelper.createFrame(-1, -1.0f));
            this.nextAttachLayout.setTranslationY((float) AndroidUtilities.dp(78.0f));
            AnimatorSet animator = new AnimatorSet();
            animator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.currentAttachLayout, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(78.0f) + t)}), ObjectAnimator.ofFloat(this.currentAttachLayout, this.ATTACH_ALERT_LAYOUT_TRANSLATION, new float[]{0.0f, 1.0f})});
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.setDuration(180);
            animator.setStartDelay(20);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
                    SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
                    springAnimation.getSpring().setDampingRatio(0.7f);
                    springAnimation.getSpring().setStiffness(400.0f);
                    springAnimation.addUpdateListener(new ChatAttachAlert$15$$ExternalSyntheticLambda1(this));
                    springAnimation.addEndListener(new ChatAttachAlert$15$$ExternalSyntheticLambda0(this));
                    Object unused = ChatAttachAlert.this.viewChangeAnimator = springAnimation;
                    springAnimation.start();
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-ChatAttachAlert$15  reason: not valid java name */
                public /* synthetic */ void m2111xe4ba0CLASSNAME(DynamicAnimation animation12, float value, float velocity) {
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert.this.updateSelectedPosition(1);
                    }
                    ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                    ChatAttachAlert.this.containerView.invalidate();
                }

                /* renamed from: lambda$onAnimationEnd$1$org-telegram-ui-Components-ChatAttachAlert$15  reason: not valid java name */
                public /* synthetic */ void m2112x5a343266(DynamicAnimation animation1, boolean canceled, float value, float velocity) {
                    if (Build.VERSION.SDK_INT >= 20) {
                        ChatAttachAlert.this.container.setLayerType(0, (Paint) null);
                    }
                    Object unused = ChatAttachAlert.this.viewChangeAnimator = null;
                    ChatAttachAlert.this.containerView.removeView(ChatAttachAlert.this.currentAttachLayout);
                    ChatAttachAlert.this.currentAttachLayout.setVisibility(8);
                    ChatAttachAlert.this.currentAttachLayout.onHidden();
                    ChatAttachAlert.this.nextAttachLayout.onShown();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    AttachAlertLayout unused2 = chatAttachAlert.currentAttachLayout = chatAttachAlert.nextAttachLayout;
                    AttachAlertLayout unused3 = ChatAttachAlert.this.nextAttachLayout = null;
                    ChatAttachAlert.this.scrollOffsetY[0] = ChatAttachAlert.this.scrollOffsetY[1];
                }
            });
            this.viewChangeAnimator = animator;
            animator.start();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout;
        if (requestCode == 5 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
            openContactsLayout();
        } else if (requestCode == 30 && (chatAttachAlertLocationLayout = this.locationLayout) != null && this.currentAttachLayout == chatAttachAlertLocationLayout && isShowing()) {
            this.locationLayout.openShareLiveLocation();
        }
    }

    private void openContactsLayout() {
        if (this.contactsLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = new ChatAttachAlertContactsLayout(this, getContext(), this.resourcesProvider);
            this.contactsLayout = chatAttachAlertContactsLayout;
            attachAlertLayoutArr[2] = chatAttachAlertContactsLayout;
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda7(this));
        }
        showLayout(this.contactsLayout);
    }

    /* renamed from: lambda$openContactsLayout$16$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2109xa144a7d(TLRPC.User user, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendContact(user, notify, scheduleDate);
    }

    /* access modifiers changed from: private */
    public void openAudioLayout(boolean show) {
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext(), this.resourcesProvider);
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda6(this));
        }
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            TLRPC.Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
            this.audioLayout.setMaxSelectedFiles(((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) ? -1 : 1);
        }
        if (show) {
            showLayout(this.audioLayout);
        }
    }

    /* renamed from: lambda$openAudioLayout$17$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2108x3d0723f1(ArrayList audios, CharSequence caption, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendAudio(audios, caption, notify, scheduleDate);
    }

    private void openDocumentsLayout(boolean show) {
        if (this.documentLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), false, this.resourcesProvider);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() {
                public void didSelectFiles(ArrayList<String> files, String caption, ArrayList<MessageObject> fmessages, boolean notify, int scheduleDate) {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                        ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectFiles(files, caption, fmessages, notify, scheduleDate);
                    } else if (ChatAttachAlert.this.baseFragment instanceof PassportActivity) {
                        ((PassportActivity) ChatAttachAlert.this.baseFragment).didSelectFiles(files, caption, notify, scheduleDate);
                    }
                }

                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                        ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectPhotos(photos, notify, scheduleDate);
                    } else if (ChatAttachAlert.this.baseFragment instanceof PassportActivity) {
                        ((PassportActivity) ChatAttachAlert.this.baseFragment).didSelectPhotos(photos, notify, scheduleDate);
                    }
                }

                public void startDocumentSelectActivity() {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                        ((ChatActivity) ChatAttachAlert.this.baseFragment).startDocumentSelectActivity();
                    } else if (ChatAttachAlert.this.baseFragment instanceof PassportActivity) {
                        ((PassportActivity) ChatAttachAlert.this.baseFragment).startDocumentSelectActivity();
                    }
                }

                public void startMusicSelectActivity() {
                    ChatAttachAlert.this.openAudioLayout(true);
                }
            });
        }
        BaseFragment baseFragment2 = this.baseFragment;
        int i = 1;
        if (baseFragment2 instanceof ChatActivity) {
            TLRPC.Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout2 = this.documentLayout;
            if ((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) {
                i = -1;
            }
            chatAttachAlertDocumentLayout2.setMaxSelectedFiles(i);
        } else {
            this.documentLayout.setMaxSelectedFiles(this.maxSelectedPhotos);
            this.documentLayout.setCanSelectOnlyImageFiles(true);
        }
        if (show) {
            showLayout(this.documentLayout);
        }
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.commentsAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
            if (!this.typeButtonsAvailable) {
                this.shadow.setVisibility(0);
            }
        } else if (this.typeButtonsAvailable) {
            this.buttonsRecyclerView.setVisibility(0);
        }
        float f = 0.2f;
        float f2 = 0.0f;
        if (animated) {
            this.commentsAnimator = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            FrameLayout frameLayout = this.frameLayout2;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            FrameLayout frameLayout5 = this.writeButtonContainer;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout5, property4, fArr4));
            View view = this.selectedCountView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(view, property5, fArr5));
            View view2 = this.selectedCountView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr6[0] = f;
            animators.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
            View view3 = this.selectedCountView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
            if (this.actionBar.getTag() != null) {
                FrameLayout frameLayout6 = this.frameLayout2;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = show ? 0.0f : (float) AndroidUtilities.dp(48.0f);
                animators.add(ObjectAnimator.ofFloat(frameLayout6, property8, fArr8));
                View view4 = this.shadow;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = (float) (show ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f));
                animators.add(ObjectAnimator.ofFloat(view4, property9, fArr9));
                View view5 = this.shadow;
                Property property10 = View.ALPHA;
                float[] fArr10 = new float[1];
                if (show) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view5, property10, fArr10));
            } else if (this.typeButtonsAvailable) {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                Property property11 = View.TRANSLATION_Y;
                float[] fArr11 = new float[1];
                fArr11[0] = show ? (float) AndroidUtilities.dp(36.0f) : 0.0f;
                animators.add(ObjectAnimator.ofFloat(recyclerListView, property11, fArr11));
                View view6 = this.shadow;
                Property property12 = View.TRANSLATION_Y;
                float[] fArr12 = new float[1];
                if (show) {
                    f2 = (float) AndroidUtilities.dp(36.0f);
                }
                fArr12[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view6, property12, fArr12));
            } else {
                this.shadow.setTranslationY((float) AndroidUtilities.dp(36.0f));
                View view7 = this.shadow;
                Property property13 = View.ALPHA;
                float[] fArr13 = new float[1];
                if (show) {
                    f2 = 1.0f;
                }
                fArr13[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view7, property13, fArr13));
            }
            this.commentsAnimator.playTogether(animators);
            this.commentsAnimator.setInterpolator(new DecelerateInterpolator());
            this.commentsAnimator.setDuration(180);
            this.commentsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ChatAttachAlert.this.commentsAnimator)) {
                        if (!show) {
                            ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                            if (!ChatAttachAlert.this.typeButtonsAvailable) {
                                ChatAttachAlert.this.shadow.setVisibility(4);
                            }
                        } else if (ChatAttachAlert.this.typeButtonsAvailable) {
                            ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlert.this.commentsAnimator = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(ChatAttachAlert.this.commentsAnimator)) {
                        AnimatorSet unused = ChatAttachAlert.this.commentsAnimator = null;
                    }
                }
            });
            this.commentsAnimator.start();
        } else {
            this.frameLayout2.setAlpha(show ? 1.0f : 0.0f);
            this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            View view8 = this.selectedCountView;
            if (show) {
                f = 1.0f;
            }
            view8.setScaleY(f);
            this.selectedCountView.setAlpha(show ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((float) (show ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f)));
                View view9 = this.shadow;
                if (show) {
                    f2 = 1.0f;
                }
                view9.setAlpha(f2);
            } else if (this.typeButtonsAvailable) {
                this.buttonsRecyclerView.setTranslationY(show ? (float) AndroidUtilities.dp(36.0f) : 0.0f);
                View view10 = this.shadow;
                if (show) {
                    f2 = (float) AndroidUtilities.dp(36.0f);
                }
                view10.setTranslationY(f2);
            } else {
                this.shadow.setTranslationY((float) AndroidUtilities.dp(36.0f));
                View view11 = this.shadow;
                if (show) {
                    f2 = 1.0f;
                }
                view11.setAlpha(f2);
            }
            if (!show) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
                if (!this.typeButtonsAvailable) {
                    this.shadow.setVisibility(4);
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, new float[]{0.0f, 400.0f})});
        animatorSet.setDuration(400);
        animatorSet.setStartDelay(20);
        animatorSet.start();
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent event) {
        return this.currentAttachLayout.onContainerViewTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(EditTextBoldCursor editText, boolean showKeyboard) {
        if (!this.enterCommentEventSent) {
            boolean keyboardVisible = this.delegate.needEnterComment();
            this.enterCommentEventSent = true;
            AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda20(this, editText, showKeyboard), keyboardVisible ? 200 : 0);
        }
    }

    /* renamed from: lambda$makeFocusable$19$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m2092x6717acdd(EditTextBoldCursor editText, boolean showKeyboard) {
        setFocusable(true);
        editText.requestFocus();
        if (showKeyboard) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda1(editText));
        }
    }

    /* access modifiers changed from: private */
    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            AttachButton button = (AttachButton) view;
            button.textView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), getThemedColor(button.textKey), button.checkedState));
        } else if (view instanceof AttachBotButton) {
            ((AttachBotButton) view).nameTextView.setTextColor(getThemedColor("dialogTextGray2"));
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList;
        ArrayList<ThemeDescription> descriptions = new ArrayList<>();
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a < attachAlertLayoutArr.length) {
                if (!(attachAlertLayoutArr[a] == null || (arrayList = attachAlertLayoutArr[a].getThemeDescriptions()) == null)) {
                    descriptions.addAll(arrayList);
                }
                a++;
            } else {
                descriptions.add(new ThemeDescription(this.container, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
                return descriptions;
            }
        }
    }

    public void checkColors() {
        String str;
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(a));
            }
            this.selectedTextView.setTextColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
            this.doneItem.getTextView().setTextColor(getThemedColor("windowBackgroundWhiteBlueHeader"));
            this.selectedMenuItem.setIconColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
            Theme.setDrawableColor(this.selectedMenuItem.getBackground(), this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItemsSelector") : getThemedColor("dialogButtonSelector"));
            this.selectedMenuItem.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), false);
            this.selectedMenuItem.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), true);
            this.selectedMenuItem.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
            this.searchItem.setIconColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
            Theme.setDrawableColor(this.searchItem.getBackground(), this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItemsSelector") : getThemedColor("dialogButtonSelector"));
            this.commentTextView.updateColors();
            if (this.sendPopupLayout != null) {
                int a2 = 0;
                while (true) {
                    ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.itemCells;
                    if (a2 >= actionBarMenuSubItemArr.length) {
                        break;
                    }
                    if (actionBarMenuSubItemArr[a2] != null) {
                        actionBarMenuSubItemArr[a2].setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
                        this.itemCells[a2].setSelectorColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItemsSelector") : getThemedColor("dialogButtonSelector"));
                    }
                    a2++;
                }
                this.sendPopupLayout.setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
                ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
                if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                    this.sendPopupLayout.invalidate();
                }
            }
            String str2 = "dialogFloatingButton";
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, getThemedColor(str2), false);
            Drawable drawable = this.writeButtonDrawable;
            if (Build.VERSION.SDK_INT >= 21) {
                str2 = "dialogFloatingButtonPressed";
            }
            Theme.setSelectorDrawableColor(drawable, getThemedColor(str2), true);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.actionBarShadow.setBackgroundColor(getThemedColor("dialogShadowLine"));
            this.buttonsRecyclerView.setGlowColor(getThemedColor("dialogScrollGlow"));
            String str3 = "voipgroup_listViewBackground";
            this.buttonsRecyclerView.setBackgroundColor(getThemedColor(this.forceDarkTheme ? str3 : "dialogBackground"));
            FrameLayout frameLayout = this.frameLayout2;
            if (this.forceDarkTheme) {
                str = str3;
            } else {
                str = "dialogBackground";
            }
            frameLayout.setBackgroundColor(getThemedColor(str));
            this.selectedCountView.invalidate();
            this.actionBar.setBackgroundColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBar") : getThemedColor("dialogBackground"));
            this.actionBar.setItemsColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"), false);
            this.actionBar.setItemsBackgroundColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItemsSelector") : getThemedColor("dialogButtonSelector"), false);
            this.actionBar.setTitleColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
            Drawable drawable2 = this.shadowDrawable;
            if (!this.forceDarkTheme) {
                str3 = "dialogBackground";
            }
            Theme.setDrawableColor(drawable2, getThemedColor(str3));
            this.containerView.invalidate();
            int a3 = 0;
            while (true) {
                AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                if (a3 < attachAlertLayoutArr.length) {
                    if (attachAlertLayoutArr[a3] != null) {
                        attachAlertLayoutArr[a3].checkColors();
                    }
                    a3++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int width, int height) {
        if (this.photoLayout.onCustomMeasure(view, width, height)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        if (this.photoLayout.onCustomLayout(view, left, top, right, bottom)) {
            return true;
        }
        return false;
    }

    public void onPause() {
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[a] != null) {
                    attachAlertLayoutArr[a].onPause();
                }
                a++;
            } else {
                this.paused = true;
                return;
            }
        }
    }

    public void onResume() {
        this.paused = false;
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[a] != null) {
                attachAlertLayoutArr[a].onResume();
            }
            a++;
        }
        if (isShowing() != 0) {
            this.delegate.needEnterComment();
        }
    }

    public void onActivityResultFragment(int requestCode, Intent data, String currentPicturePath) {
        this.photoLayout.onActivityResultFragment(requestCode, data, currentPicturePath);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ButtonsAdapter buttonsAdapter2;
        if (id == NotificationCenter.reloadInlineHints && (buttonsAdapter2 = this.buttonsAdapter) != null) {
            buttonsAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition(int idx) {
        float toMove;
        int t;
        float moveProgress;
        int finalMove;
        int finalMove2;
        AttachAlertLayout layout = idx == 0 ? this.currentAttachLayout : this.nextAttachLayout;
        int t2 = this.scrollOffsetY[idx] - this.backgroundPaddingTop;
        if (layout == this.pollLayout) {
            t = t2 - AndroidUtilities.dp(13.0f);
            toMove = (float) AndroidUtilities.dp(11.0f);
        } else {
            t = t2 - AndroidUtilities.dp(39.0f);
            toMove = (float) AndroidUtilities.dp(43.0f);
        }
        if (this.backgroundPaddingTop + t < ActionBar.getCurrentActionBarHeight()) {
            moveProgress = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - t) - this.backgroundPaddingTop)) / toMove);
            this.cornerRadius = 1.0f - moveProgress;
        } else {
            moveProgress = 0.0f;
            this.cornerRadius = 1.0f;
        }
        if (AndroidUtilities.isTablet()) {
            finalMove = 16;
        } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            finalMove = 6;
        } else {
            finalMove = 12;
        }
        float offset = this.actionBar.getAlpha() != 0.0f ? 0.0f : (float) AndroidUtilities.dp((1.0f - this.selectedTextView.getAlpha()) * 26.0f);
        if (!this.menuShowed || this.avatarPicker != 0) {
            this.selectedMenuItem.setTranslationY(((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (finalMove + 37)))) + this.currentPanTranslationY);
        } else {
            this.selectedMenuItem.setTranslationY(((float) (this.scrollOffsetY[idx] - AndroidUtilities.dp((((float) finalMove) * moveProgress) + 37.0f))) + offset + this.currentPanTranslationY);
        }
        this.searchItem.setTranslationY(((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (finalMove + 37)))) + this.currentPanTranslationY);
        TextView textView = this.selectedTextView;
        float dp = ((float) (this.scrollOffsetY[idx] - AndroidUtilities.dp((((float) finalMove) * moveProgress) + 25.0f))) + offset + this.currentPanTranslationY;
        this.baseSelectedTextViewTranslationY = dp;
        textView.setTranslationY(dp);
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout != null && layout == chatAttachAlertPollLayout) {
            if (AndroidUtilities.isTablet()) {
                finalMove2 = 63;
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                finalMove2 = 53;
            } else {
                finalMove2 = 59;
            }
            this.doneItem.setTranslationY(Math.max(0.0f, (this.pollLayout.getTranslationY() + ((float) this.scrollOffsetY[idx])) - ((float) AndroidUtilities.dp((((float) finalMove2) * moveProgress) + 7.0f))) + this.currentPanTranslationY);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x006f, code lost:
        if (((org.telegram.ui.ChatActivity) r7).allowSendGifs() != false) goto L_0x0074;
     */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0126  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateLayout(org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r17, boolean r18, int r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            int r2 = r17.getCurrentItemTop()
            r3 = 2147483647(0x7fffffff, float:NaN)
            if (r2 != r3) goto L_0x0011
            return
        L_0x0011:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r3 = r0.currentAttachLayout
            r4 = 1
            r5 = 0
            if (r1 != r3) goto L_0x001f
            int r3 = r17.getButtonsHideOffset()
            if (r2 > r3) goto L_0x001f
            r3 = 1
            goto L_0x0020
        L_0x001f:
            r3 = 0
        L_0x0020:
            boolean r6 = r0.keyboardVisible
            if (r6 == 0) goto L_0x0028
            if (r18 == 0) goto L_0x0028
            r6 = 0
            goto L_0x002a
        L_0x0028:
            r6 = r18
        L_0x002a:
            if (r3 == 0) goto L_0x0034
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            java.lang.Object r7 = r7.getTag()
            if (r7 == 0) goto L_0x003e
        L_0x0034:
            if (r3 != 0) goto L_0x0174
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            java.lang.Object r7 = r7.getTag()
            if (r7 == 0) goto L_0x0174
        L_0x003e:
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r8 = 0
            if (r3 == 0) goto L_0x0048
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            goto L_0x0049
        L_0x0048:
            r9 = r8
        L_0x0049:
            r7.setTag(r9)
            android.animation.AnimatorSet r7 = r0.actionBarAnimation
            if (r7 == 0) goto L_0x0055
            r7.cancel()
            r0.actionBarAnimation = r8
        L_0x0055:
            boolean r7 = r0.avatarSearch
            if (r7 != 0) goto L_0x0074
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r7 = r0.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r8 = r0.photoLayout
            if (r7 != r8) goto L_0x0072
            boolean r7 = r0.menuShowed
            if (r7 != 0) goto L_0x0072
            org.telegram.ui.ActionBar.BaseFragment r7 = r0.baseFragment
            boolean r8 = r7 instanceof org.telegram.ui.ChatActivity
            if (r8 == 0) goto L_0x0072
            org.telegram.ui.ChatActivity r7 = (org.telegram.ui.ChatActivity) r7
            boolean r7 = r7.allowSendGifs()
            if (r7 == 0) goto L_0x0072
            goto L_0x0074
        L_0x0072:
            r7 = 0
            goto L_0x0075
        L_0x0074:
            r7 = 1
        L_0x0075:
            int r8 = r0.avatarPicker
            if (r8 != 0) goto L_0x008a
            boolean r8 = r0.menuShowed
            if (r8 != 0) goto L_0x0088
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r8 = r0.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r9 = r0.photoLayout
            if (r8 != r9) goto L_0x0088
            boolean r8 = r0.mediaEnabled
            if (r8 == 0) goto L_0x0088
            goto L_0x008a
        L_0x0088:
            r8 = 0
            goto L_0x008b
        L_0x008a:
            r8 = 1
        L_0x008b:
            if (r3 == 0) goto L_0x009c
            if (r7 == 0) goto L_0x0094
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.searchItem
            r9.setVisibility(r5)
        L_0x0094:
            if (r8 == 0) goto L_0x00a5
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.selectedMenuItem
            r9.setVisibility(r5)
            goto L_0x00a5
        L_0x009c:
            boolean r9 = r0.typeButtonsAvailable
            if (r9 == 0) goto L_0x00a5
            org.telegram.ui.Components.RecyclerListView r9 = r0.buttonsRecyclerView
            r9.setVisibility(r5)
        L_0x00a5:
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = 0
            if (r6 == 0) goto L_0x0126
            android.animation.AnimatorSet r11 = new android.animation.AnimatorSet
            r11.<init>()
            r0.actionBarAnimation = r11
            r12 = 180(0xb4, double:8.9E-322)
            r11.setDuration(r12)
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            org.telegram.ui.ActionBar.ActionBar r12 = r0.actionBar
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r4]
            if (r3 == 0) goto L_0x00c6
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x00c7
        L_0x00c6:
            r15 = 0
        L_0x00c7:
            r14[r5] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11.add(r12)
            android.view.View r12 = r0.actionBarShadow
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r4]
            if (r3 == 0) goto L_0x00db
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x00dc
        L_0x00db:
            r15 = 0
        L_0x00dc:
            r14[r5] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11.add(r12)
            if (r7 == 0) goto L_0x00fc
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r0.searchItem
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r4]
            if (r3 == 0) goto L_0x00f2
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x00f3
        L_0x00f2:
            r15 = 0
        L_0x00f3:
            r14[r5] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11.add(r12)
        L_0x00fc:
            if (r8 == 0) goto L_0x0111
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r0.selectedMenuItem
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r4]
            if (r3 == 0) goto L_0x0107
            goto L_0x0108
        L_0x0107:
            r9 = 0
        L_0x0108:
            r14[r5] = r9
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11.add(r9)
        L_0x0111:
            android.animation.AnimatorSet r9 = r0.actionBarAnimation
            r9.playTogether(r11)
            android.animation.AnimatorSet r9 = r0.actionBarAnimation
            org.telegram.ui.Components.ChatAttachAlert$19 r10 = new org.telegram.ui.Components.ChatAttachAlert$19
            r10.<init>(r3)
            r9.addListener(r10)
            android.animation.AnimatorSet r9 = r0.actionBarAnimation
            r9.start()
            goto L_0x0174
        L_0x0126:
            r11 = 4
            if (r3 == 0) goto L_0x0132
            boolean r12 = r0.typeButtonsAvailable
            if (r12 == 0) goto L_0x0132
            org.telegram.ui.Components.RecyclerListView r12 = r0.buttonsRecyclerView
            r12.setVisibility(r11)
        L_0x0132:
            org.telegram.ui.ActionBar.ActionBar r12 = r0.actionBar
            if (r3 == 0) goto L_0x0139
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x013a
        L_0x0139:
            r13 = 0
        L_0x013a:
            r12.setAlpha(r13)
            android.view.View r12 = r0.actionBarShadow
            if (r3 == 0) goto L_0x0144
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0145
        L_0x0144:
            r13 = 0
        L_0x0145:
            r12.setAlpha(r13)
            if (r7 == 0) goto L_0x0155
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r0.searchItem
            if (r3 == 0) goto L_0x0151
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0152
        L_0x0151:
            r13 = 0
        L_0x0152:
            r12.setAlpha(r13)
        L_0x0155:
            if (r8 == 0) goto L_0x0160
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r0.selectedMenuItem
            if (r3 == 0) goto L_0x015c
            goto L_0x015d
        L_0x015c:
            r9 = 0
        L_0x015d:
            r12.setAlpha(r9)
        L_0x0160:
            if (r3 != 0) goto L_0x0174
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.searchItem
            r9.setVisibility(r11)
            int r9 = r0.avatarPicker
            if (r9 != 0) goto L_0x016f
            boolean r9 = r0.menuShowed
            if (r9 != 0) goto L_0x0174
        L_0x016f:
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.selectedMenuItem
            r9.setVisibility(r11)
        L_0x0174:
            android.view.ViewGroup$LayoutParams r7 = r17.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r7 = (android.widget.FrameLayout.LayoutParams) r7
            int r8 = r7.topMargin
            r9 = 1093664768(0x41300000, float:11.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            int r2 = r2 + r8
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r8 = r0.currentAttachLayout
            if (r8 != r1) goto L_0x0189
            r4 = 0
        L_0x0189:
            int[] r5 = r0.scrollOffsetY
            r8 = r5[r4]
            if (r8 == r2) goto L_0x019e
            r8 = r5[r4]
            r0.previousScrollOffsetY = r8
            r5[r4] = r2
            r0.updateSelectedPosition(r4)
            android.view.ViewGroup r5 = r0.containerView
            r5.invalidate()
            goto L_0x01a4
        L_0x019e:
            if (r19 == 0) goto L_0x01a4
            r5 = r5[r4]
            r0.previousScrollOffsetY = r5
        L_0x01a4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateLayout(org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout, boolean, int):void");
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0125  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0163  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateCountButton(int r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            java.lang.Object r2 = r0.viewChangeAnimator
            if (r2 == 0) goto L_0x0009
            return
        L_0x0009:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r2 = r0.currentAttachLayout
            int r2 = r2.getSelectedItemsCount()
            r3 = 180(0xb4, double:8.9E-322)
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = 1
            r8 = 0
            if (r2 != 0) goto L_0x002c
            android.view.View r9 = r0.selectedCountView
            r9.setPivotX(r6)
            android.view.View r9 = r0.selectedCountView
            r9.setPivotY(r6)
            if (r1 == 0) goto L_0x0026
            r9 = 1
            goto L_0x0027
        L_0x0026:
            r9 = 0
        L_0x0027:
            r0.showCommentTextView(r8, r9)
            goto L_0x00af
        L_0x002c:
            android.view.View r9 = r0.selectedCountView
            r9.invalidate()
            if (r1 == 0) goto L_0x0035
            r9 = 1
            goto L_0x0036
        L_0x0035:
            r9 = 0
        L_0x0036:
            boolean r9 = r0.showCommentTextView(r7, r9)
            if (r9 != 0) goto L_0x00a5
            if (r1 == 0) goto L_0x00a5
            android.view.View r9 = r0.selectedCountView
            r10 = 1101529088(0x41a80000, float:21.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r9.setPivotX(r10)
            android.view.View r9 = r0.selectedCountView
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r9.setPivotY(r10)
            android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
            r9.<init>()
            r10 = 2
            android.animation.Animator[] r11 = new android.animation.Animator[r10]
            android.view.View r12 = r0.selectedCountView
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r14 = new float[r10]
            r15 = 1066192077(0x3f8ccccd, float:1.1)
            r16 = 1063675494(0x3var_, float:0.9)
            if (r1 != r7) goto L_0x0070
            r17 = 1066192077(0x3f8ccccd, float:1.1)
            goto L_0x0073
        L_0x0070:
            r17 = 1063675494(0x3var_, float:0.9)
        L_0x0073:
            r14[r8] = r17
            r14[r7] = r5
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11[r8] = r12
            android.view.View r12 = r0.selectedCountView
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r10 = new float[r10]
            if (r1 != r7) goto L_0x0086
            goto L_0x0089
        L_0x0086:
            r15 = 1063675494(0x3var_, float:0.9)
        L_0x0089:
            r10[r8] = r15
            r10[r7] = r5
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r12, r13, r10)
            r11[r7] = r10
            r9.playTogether(r11)
            android.view.animation.OvershootInterpolator r10 = new android.view.animation.OvershootInterpolator
            r10.<init>()
            r9.setInterpolator(r10)
            r9.setDuration(r3)
            r9.start()
            goto L_0x00af
        L_0x00a5:
            android.view.View r9 = r0.selectedCountView
            r9.setPivotX(r6)
            android.view.View r9 = r0.selectedCountView
            r9.setPivotY(r6)
        L_0x00af:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r9 = r0.currentAttachLayout
            r9.onSelectedItemsCountChanged(r2)
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r9 = r0.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r10 = r0.photoLayout
            if (r9 != r10) goto L_0x01d8
            org.telegram.ui.ActionBar.BaseFragment r9 = r0.baseFragment
            boolean r9 = r9 instanceof org.telegram.ui.ChatActivity
            if (r9 != 0) goto L_0x00c4
            int r9 = r0.avatarPicker
            if (r9 == 0) goto L_0x01d8
        L_0x00c4:
            if (r2 != 0) goto L_0x00ca
            boolean r9 = r0.menuShowed
            if (r9 != 0) goto L_0x00d4
        L_0x00ca:
            if (r2 != 0) goto L_0x00d0
            int r9 = r0.avatarPicker
            if (r9 == 0) goto L_0x01d8
        L_0x00d0:
            boolean r9 = r0.menuShowed
            if (r9 != 0) goto L_0x01d8
        L_0x00d4:
            if (r2 != 0) goto L_0x00dd
            int r9 = r0.avatarPicker
            if (r9 == 0) goto L_0x00db
            goto L_0x00dd
        L_0x00db:
            r9 = 0
            goto L_0x00de
        L_0x00dd:
            r9 = 1
        L_0x00de:
            r0.menuShowed = r9
            android.animation.AnimatorSet r9 = r0.menuAnimator
            if (r9 == 0) goto L_0x00ea
            r9.cancel()
            r9 = 0
            r0.menuAnimator = r9
        L_0x00ea:
            org.telegram.ui.ActionBar.ActionBar r9 = r0.actionBar
            java.lang.Object r9 = r9.getTag()
            if (r9 == 0) goto L_0x0102
            org.telegram.ui.ActionBar.BaseFragment r9 = r0.baseFragment
            boolean r10 = r9 instanceof org.telegram.ui.ChatActivity
            if (r10 == 0) goto L_0x0102
            org.telegram.ui.ChatActivity r9 = (org.telegram.ui.ChatActivity) r9
            boolean r9 = r9.allowSendGifs()
            if (r9 == 0) goto L_0x0102
            r9 = 1
            goto L_0x0103
        L_0x0102:
            r9 = 0
        L_0x0103:
            boolean r10 = r0.menuShowed
            if (r10 == 0) goto L_0x0116
            int r10 = r0.avatarPicker
            if (r10 != 0) goto L_0x0110
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.selectedMenuItem
            r10.setVisibility(r8)
        L_0x0110:
            android.widget.TextView r10 = r0.selectedTextView
            r10.setVisibility(r8)
            goto L_0x0123
        L_0x0116:
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            java.lang.Object r10 = r10.getTag()
            if (r10 == 0) goto L_0x0123
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.searchItem
            r10.setVisibility(r8)
        L_0x0123:
            if (r1 != 0) goto L_0x0163
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.Object r3 = r3.getTag()
            if (r3 != 0) goto L_0x013e
            int r3 = r0.avatarPicker
            if (r3 != 0) goto L_0x013e
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.selectedMenuItem
            boolean r4 = r0.menuShowed
            if (r4 == 0) goto L_0x013a
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x013b
        L_0x013a:
            r4 = 0
        L_0x013b:
            r3.setAlpha(r4)
        L_0x013e:
            android.widget.TextView r3 = r0.selectedTextView
            boolean r4 = r0.menuShowed
            if (r4 == 0) goto L_0x0147
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0148
        L_0x0147:
            r4 = 0
        L_0x0148:
            r3.setAlpha(r4)
            if (r9 == 0) goto L_0x0157
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.searchItem
            boolean r4 = r0.menuShowed
            if (r4 == 0) goto L_0x0154
            r5 = 0
        L_0x0154:
            r3.setAlpha(r5)
        L_0x0157:
            boolean r3 = r0.menuShowed
            if (r3 == 0) goto L_0x01d8
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.searchItem
            r4 = 4
            r3.setVisibility(r4)
            goto L_0x01d8
        L_0x0163:
            android.animation.AnimatorSet r10 = new android.animation.AnimatorSet
            r10.<init>()
            r0.menuAnimator = r10
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            org.telegram.ui.ActionBar.ActionBar r11 = r0.actionBar
            java.lang.Object r11 = r11.getTag()
            if (r11 != 0) goto L_0x0192
            int r11 = r0.avatarPicker
            if (r11 != 0) goto L_0x0192
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r0.selectedMenuItem
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r7]
            boolean r14 = r0.menuShowed
            if (r14 == 0) goto L_0x0188
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x0189
        L_0x0188:
            r14 = 0
        L_0x0189:
            r13[r8] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r10.add(r11)
        L_0x0192:
            android.widget.TextView r11 = r0.selectedTextView
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r7]
            boolean r14 = r0.menuShowed
            if (r14 == 0) goto L_0x019f
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x01a0
        L_0x019f:
            r14 = 0
        L_0x01a0:
            r13[r8] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r10.add(r11)
            if (r9 == 0) goto L_0x01bf
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r0.searchItem
            android.util.Property r12 = android.view.View.ALPHA
            float[] r7 = new float[r7]
            boolean r13 = r0.menuShowed
            if (r13 == 0) goto L_0x01b6
            r5 = 0
        L_0x01b6:
            r7[r8] = r5
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r11, r12, r7)
            r10.add(r5)
        L_0x01bf:
            android.animation.AnimatorSet r5 = r0.menuAnimator
            r5.playTogether(r10)
            android.animation.AnimatorSet r5 = r0.menuAnimator
            org.telegram.ui.Components.ChatAttachAlert$20 r6 = new org.telegram.ui.Components.ChatAttachAlert$20
            r6.<init>()
            r5.addListener(r6)
            android.animation.AnimatorSet r5 = r0.menuAnimator
            r5.setDuration(r3)
            android.animation.AnimatorSet r3 = r0.menuAnimator
            r3.start()
        L_0x01d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateCountButton(int):void");
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void init() {
        AttachAlertLayout layoutToSet;
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 != null) {
            int i = 4;
            if (!(baseFragment2 instanceof ChatActivity) || this.avatarPicker == 2) {
                this.commentTextView.setVisibility(4);
            } else {
                TLRPC.Chat chat = ((ChatActivity) baseFragment2).getCurrentChat();
                TLRPC.User user = ((ChatActivity) this.baseFragment).getCurrentUser();
                if (chat != null) {
                    this.mediaEnabled = ChatObject.canSendMedia(chat);
                    this.pollsEnabled = ChatObject.canSendPolls(chat);
                } else {
                    this.pollsEnabled = user != null && user.bot;
                }
            }
            this.photoLayout.onInit(this.mediaEnabled);
            this.commentTextView.hidePopup(true);
            this.enterCommentEventSent = false;
            setFocusable(false);
            MessageObject messageObject = this.editingMessageObject;
            if (messageObject == null || (!messageObject.isMusic() && (!this.editingMessageObject.isDocument() || this.editingMessageObject.isGif()))) {
                layoutToSet = this.photoLayout;
                this.typeButtonsAvailable = this.avatarPicker == 0;
                this.selectedId = 1;
            } else {
                if (this.editingMessageObject.isMusic()) {
                    openAudioLayout(false);
                    layoutToSet = this.audioLayout;
                    this.selectedId = 3;
                } else {
                    openDocumentsLayout(false);
                    layoutToSet = this.documentLayout;
                    this.selectedId = 4;
                }
                this.typeButtonsAvailable = !this.editingMessageObject.hasValidGroupId();
            }
            this.buttonsRecyclerView.setVisibility(this.typeButtonsAvailable ? 0 : 8);
            this.shadow.setVisibility(this.typeButtonsAvailable ? 0 : 4);
            if (this.currentAttachLayout != layoutToSet) {
                if (this.actionBar.isSearchFieldVisible()) {
                    this.actionBar.closeSearchField();
                }
                this.containerView.removeView(this.currentAttachLayout);
                this.currentAttachLayout.onHide();
                this.currentAttachLayout.setVisibility(8);
                this.currentAttachLayout.onHidden();
                this.currentAttachLayout = layoutToSet;
                setAllowNestedScroll(true);
                if (this.currentAttachLayout.getParent() == null) {
                    this.containerView.addView(this.currentAttachLayout, 0, LayoutHelper.createFrame(-1, -1.0f));
                }
                layoutToSet.setAlpha(1.0f);
                layoutToSet.setVisibility(0);
                layoutToSet.onShow();
                layoutToSet.onShown();
                ActionBar actionBar2 = this.actionBar;
                if (layoutToSet.needsActionBar() != 0) {
                    i = 0;
                }
                actionBar2.setVisibility(i);
                this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
            }
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
            if (attachAlertLayout != chatAttachAlertPhotoLayout) {
                chatAttachAlertPhotoLayout.setCheckCameraWhenShown(true);
            }
            updateCountButton(0);
            this.buttonsAdapter.notifyDataSetChanged();
            this.commentTextView.setText("");
            this.buttonsLayoutManager.scrollToPositionWithOffset(0, 1000000);
        }
    }

    public void onDestroy() {
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[a] != null) {
                attachAlertLayoutArr[a].onDestroy();
            }
            a++;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadInlineHints);
        this.baseFragment = null;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onOpenAnimationEnd() {
        MediaController.AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        this.currentAttachLayout.onOpenAnimationEnd();
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", NUM));
        this.openTransitionFinished = true;
    }

    public void onOpenAnimationStart() {
    }

    public boolean canDismiss() {
        return true;
    }

    public void setAllowDrawContent(boolean value) {
        super.setAllowDrawContent(value);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
    }

    public void setAvatarPicker(int type, boolean search) {
        this.avatarPicker = type;
        this.avatarSearch = search;
        if (type != 0) {
            this.typeButtonsAvailable = false;
            this.buttonsRecyclerView.setVisibility(8);
            this.shadow.setVisibility(8);
            if (this.avatarPicker == 2) {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", NUM));
            } else {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhoto", NUM));
            }
        } else {
            this.typeButtonsAvailable = true;
        }
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        if (this.editingMessageObject == null) {
            this.maxSelectedPhotos = value;
            this.allowOrder = order;
        }
    }

    public void setOpenWithFrontFaceCamera(boolean value) {
        this.openWithFrontFaceCamera = value;
    }

    public ChatAttachAlertPhotoLayout getPhotoLayout() {
        return this.photoLayout;
    }

    private class ButtonsAdapter extends RecyclerListView.SelectionAdapter {
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;

        public ButtonsAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new AttachButton(this.mContext);
                    break;
                default:
                    view = new AttachBotButton(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    AttachButton attachButton = (AttachButton) holder.itemView;
                    if (position == this.galleryButton) {
                        attachButton.setTextAndIcon(1, LocaleController.getString("ChatGallery", NUM), Theme.chat_attachButtonDrawables[0], "chat_attachGalleryBackground", "chat_attachGalleryText");
                        attachButton.setTag(1);
                        return;
                    } else if (position == this.documentButton) {
                        attachButton.setTextAndIcon(4, LocaleController.getString("ChatDocument", NUM), Theme.chat_attachButtonDrawables[2], "chat_attachFileBackground", "chat_attachFileText");
                        attachButton.setTag(4);
                        return;
                    } else if (position == this.locationButton) {
                        attachButton.setTextAndIcon(6, LocaleController.getString("ChatLocation", NUM), Theme.chat_attachButtonDrawables[4], "chat_attachLocationBackground", "chat_attachLocationText");
                        attachButton.setTag(6);
                        return;
                    } else if (position == this.musicButton) {
                        attachButton.setTextAndIcon(3, LocaleController.getString("AttachMusic", NUM), Theme.chat_attachButtonDrawables[1], "chat_attachAudioBackground", "chat_attachAudioText");
                        attachButton.setTag(3);
                        return;
                    } else if (position == this.pollButton) {
                        attachButton.setTextAndIcon(9, LocaleController.getString("Poll", NUM), Theme.chat_attachButtonDrawables[5], "chat_attachPollBackground", "chat_attachPollText");
                        attachButton.setTag(9);
                        return;
                    } else if (position == this.contactButton) {
                        attachButton.setTextAndIcon(5, LocaleController.getString("AttachContact", NUM), Theme.chat_attachButtonDrawables[3], "chat_attachContactBackground", "chat_attachContactText");
                        attachButton.setTag(5);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    int position2 = position - this.buttonsCount;
                    AttachBotButton child = (AttachBotButton) holder.itemView;
                    child.setTag(Integer.valueOf(position2));
                    child.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(position2).peer.user_id)));
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ChatAttachAlert.this.applyAttachButtonColors(holder.itemView);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            int count = this.buttonsCount;
            if (ChatAttachAlert.this.editingMessageObject != null || !(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                return count;
            }
            return count + MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size();
        }

        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                int i = this.buttonsCount;
                int i2 = i + 1;
                this.buttonsCount = i2;
                this.galleryButton = i;
                this.buttonsCount = i2 + 1;
                this.documentButton = i2;
            } else if (ChatAttachAlert.this.editingMessageObject == null) {
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i3 = this.buttonsCount;
                    int i4 = i3 + 1;
                    this.buttonsCount = i4;
                    this.galleryButton = i3;
                    this.buttonsCount = i4 + 1;
                    this.documentButton = i4;
                }
                int i5 = this.buttonsCount;
                this.buttonsCount = i5 + 1;
                this.locationButton = i5;
                if (ChatAttachAlert.this.pollsEnabled) {
                    int i6 = this.buttonsCount;
                    this.buttonsCount = i6 + 1;
                    this.pollButton = i6;
                } else {
                    int i7 = this.buttonsCount;
                    this.buttonsCount = i7 + 1;
                    this.contactButton = i7;
                }
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i8 = this.buttonsCount;
                    this.buttonsCount = i8 + 1;
                    this.musicButton = i8;
                }
                TLRPC.User user = ChatAttachAlert.this.baseFragment instanceof ChatActivity ? ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentUser() : null;
                if (user != null && user.bot) {
                    int i9 = this.buttonsCount;
                    this.buttonsCount = i9 + 1;
                    this.contactButton = i9;
                }
            } else if ((!ChatAttachAlert.this.editingMessageObject.isMusic() && !ChatAttachAlert.this.editingMessageObject.isDocument()) || !ChatAttachAlert.this.editingMessageObject.hasValidGroupId()) {
                int i10 = this.buttonsCount;
                int i11 = i10 + 1;
                this.buttonsCount = i11;
                this.galleryButton = i10;
                int i12 = i11 + 1;
                this.buttonsCount = i12;
                this.documentButton = i11;
                this.buttonsCount = i12 + 1;
                this.musicButton = i12;
            } else if (ChatAttachAlert.this.editingMessageObject.isMusic()) {
                int i13 = this.buttonsCount;
                this.buttonsCount = i13 + 1;
                this.musicButton = i13;
            } else {
                int i14 = this.buttonsCount;
                this.buttonsCount = i14 + 1;
                this.documentButton = i14;
            }
            super.notifyDataSetChanged();
        }

        public int getButtonsCount() {
            return this.buttonsCount;
        }

        public int getItemViewType(int position) {
            if (position < this.buttonsCount) {
                return 0;
            }
            return 1;
        }
    }

    public void dismissInternal() {
        this.delegate.doOnIdle(new ChatAttachAlert$$ExternalSyntheticLambda19(this));
    }

    /* access modifiers changed from: private */
    public void removeFromRoot() {
        if (this.containerView != null) {
            this.containerView.setVisibility(4);
        }
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        }
        this.contactsLayout = null;
        this.audioLayout = null;
        this.pollLayout = null;
        this.locationLayout = null;
        this.documentLayout = null;
        int a = 1;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[a] != null) {
                    attachAlertLayoutArr[a].onDestroy();
                    this.containerView.removeView(this.layouts[a]);
                    this.layouts[a] = null;
                }
                a++;
            } else {
                super.dismissInternal();
                return;
            }
        }
    }

    public void onBackPressed() {
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        } else if (!this.currentAttachLayout.onBackPressed()) {
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
                super.onBackPressed();
            } else {
                this.commentTextView.hidePopup(true);
            }
        }
    }

    public void dismissWithButtonClick(int item) {
        super.dismissWithButtonClick(item);
        this.currentAttachLayout.onDismissWithButtonClick(item);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return this.currentAttachLayout.canDismissWithTouchOutside();
    }

    public void dismiss() {
        if (!this.currentAttachLayout.onDismiss()) {
            int a = 0;
            while (true) {
                AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                if (a >= attachAlertLayoutArr.length) {
                    break;
                }
                if (!(attachAlertLayoutArr[a] == null || this.currentAttachLayout == attachAlertLayoutArr[a])) {
                    attachAlertLayoutArr[a].onDismiss();
                }
                a++;
            }
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
            }
            super.dismiss();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.currentAttachLayout.onSheetKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setAllowNestedScroll(boolean allowNestedScroll) {
        this.allowNestedScroll = allowNestedScroll;
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    public EditTextEmoji getCommentTextView() {
        return this.commentTextView;
    }
}
