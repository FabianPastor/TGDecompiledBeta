package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import androidx.annotation.Keep;
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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerSearchActivity;

public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    public final Property<AttachAlertLayout, Float> ATTACH_ALERT_LAYOUT_TRANSLATION = new AnimationProperties.FloatProperty<AttachAlertLayout>("translation") {
        public void setValue(AttachAlertLayout attachAlertLayout, float f) {
            if (f > 0.7f) {
                float f2 = 1.0f - ((1.0f - f) / 0.3f);
                if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.locationLayout) {
                    ChatAttachAlert.this.currentAttachLayout.setAlpha(1.0f - f2);
                    ChatAttachAlert.this.nextAttachLayout.setAlpha(1.0f);
                } else {
                    ChatAttachAlert.this.nextAttachLayout.setAlpha(f2);
                    ChatAttachAlert.this.nextAttachLayout.onHideShowProgress(f2);
                }
            } else if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.locationLayout) {
                ChatAttachAlert.this.nextAttachLayout.setAlpha(0.0f);
            }
            if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.updateSelectedPosition(chatAttachAlert.nextAttachLayout == ChatAttachAlert.this.pollLayout ? 1 : 0);
            }
            ChatAttachAlert.this.nextAttachLayout.setTranslationY(((float) AndroidUtilities.dp(78.0f)) * f);
            ChatAttachAlert.this.currentAttachLayout.onHideShowProgress(1.0f - Math.min(1.0f, f / 0.7f));
            ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated();
            ChatAttachAlert.this.containerView.invalidate();
        }

        public Float get(AttachAlertLayout attachAlertLayout) {
            return Float.valueOf(ChatAttachAlert.this.translationProgress);
        }
    };
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    protected ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    protected boolean allowOrder = true;
    /* access modifiers changed from: private */
    public Paint attachButtonPaint;
    /* access modifiers changed from: private */
    public int attachItemSize = AndroidUtilities.dp(85.0f);
    private ChatAttachAlertAudioLayout audioLayout;
    protected int avatarPicker;
    protected boolean avatarSearch;
    protected BaseFragment baseFragment;
    /* access modifiers changed from: private */
    public float baseSelectedTextViewTranslationY;
    private boolean buttonPressed;
    /* access modifiers changed from: private */
    public ButtonsAdapter buttonsAdapter;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    protected EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public AnimatorSet commentsAnimator;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius = 1.0f;
    protected int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public AttachAlertLayout currentAttachLayout;
    protected ChatAttachViewDelegate delegate;
    private ChatAttachAlertDocumentLayout documentLayout;
    protected ActionBarMenuItem doneItem;
    protected MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public boolean enterCommentEventSent;
    /* access modifiers changed from: private */
    public ArrayList<Rect> exclusionRects;
    /* access modifiers changed from: private */
    public Rect exclustionRect;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    private ActionBarMenuSubItem[] itemCells;
    private AttachAlertLayout[] layouts = new AttachAlertLayout[6];
    /* access modifiers changed from: private */
    public ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos = -1;
    /* access modifiers changed from: private */
    public boolean mediaEnabled = true;
    /* access modifiers changed from: private */
    public AnimatorSet menuAnimator;
    /* access modifiers changed from: private */
    public boolean menuShowed;
    /* access modifiers changed from: private */
    public AttachAlertLayout nextAttachLayout;
    protected boolean openWithFrontFaceCamera;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    protected boolean paused;
    private ChatAttachAlertPhotoLayout photoLayout;
    /* access modifiers changed from: private */
    public ChatAttachAlertPollLayout pollLayout;
    /* access modifiers changed from: private */
    public boolean pollsEnabled = true;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    protected int[] scrollOffsetY;
    protected ActionBarMenuItem searchItem;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public int selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    /* access modifiers changed from: private */
    public View shadow;
    protected SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    public float translationProgress;
    /* access modifiers changed from: private */
    public Object viewChangeAnimator;
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface ChatAttachViewDelegate {

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ChatAttachViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$openAvatarsSearch(ChatAttachViewDelegate chatAttachViewDelegate) {
            }
        }

        void didPressedButton(int i, boolean z, boolean z2, int i2);

        void didSelectBot(TLRPC$User tLRPC$User);

        void doOnIdle(Runnable runnable);

        void needEnterComment();

        void onCameraOpened();

        void openAvatarsSearch();
    }

    static /* synthetic */ boolean lambda$new$9(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean canDismiss() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public static class AttachAlertLayout extends FrameLayout {
        protected ChatAttachAlert parentAlert;

        /* access modifiers changed from: package-private */
        public void applyCaption(String str) {
        }

        /* access modifiers changed from: package-private */
        public boolean canDismissWithTouchOutside() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean canScheduleMessages() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public void checkColors() {
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
        public int getListTopPadding() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int getSelectedItemsCount() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public ArrayList<ThemeDescription> getThemeDescriptions() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public int needsActionBar() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public boolean onBackPressed() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void onButtonsTranslationYUpdated() {
        }

        /* access modifiers changed from: package-private */
        public void onContainerTranslationUpdated() {
        }

        /* access modifiers changed from: package-private */
        public boolean onContainerViewTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void onDestroy() {
        }

        /* access modifiers changed from: package-private */
        public boolean onDismiss() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void onDismissWithButtonClick(int i) {
        }

        /* access modifiers changed from: package-private */
        public void onHidden() {
        }

        /* access modifiers changed from: package-private */
        public void onHide() {
        }

        /* access modifiers changed from: package-private */
        public void onHideShowProgress(float f) {
        }

        /* access modifiers changed from: package-private */
        public void onMenuItemClick(int i) {
        }

        /* access modifiers changed from: package-private */
        public void onOpenAnimationEnd() {
        }

        /* access modifiers changed from: package-private */
        public void onPause() {
        }

        /* access modifiers changed from: package-private */
        public void onPreMeasure(int i, int i2) {
        }

        /* access modifiers changed from: package-private */
        public void onResume() {
        }

        /* access modifiers changed from: package-private */
        public void onSelectedItemsCountChanged(int i) {
        }

        /* access modifiers changed from: package-private */
        public boolean onSheetKeyDown(int i, KeyEvent keyEvent) {
            return false;
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
        public void sendSelectedItems(boolean z, int i) {
        }

        public AttachAlertLayout(ChatAttachAlert chatAttachAlert, Context context) {
            super(context);
            this.parentAlert = chatAttachAlert;
        }

        /* access modifiers changed from: package-private */
        public int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12.0f : 17.0f);
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

        public boolean hasOverlappingRendering() {
            return false;
        }

        public AttachButton(Context context) {
            super(context);
            setWillNotDraw(false);
            AnonymousClass1 r0 = new RLottieImageView(context, ChatAttachAlert.this) {
                public void setScaleX(float f) {
                    super.setScaleX(f);
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
            this.textView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean z) {
            if (this.checked != (this.currentId == ChatAttachAlert.this.selectedId)) {
                this.checked = this.currentId == ChatAttachAlert.this.selectedId;
                Animator animator = this.checkAnimator;
                if (animator != null) {
                    animator.cancel();
                }
                float f = 1.0f;
                if (z) {
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

        @Keep
        public void setCheckedState(float f) {
            this.checkedState = f;
            float f2 = 1.0f - (f * 0.06f);
            this.imageView.setScaleX(f2);
            this.imageView.setScaleY(f2);
            this.textView.setTextColor(ColorUtils.blendARGB(Theme.getColor("dialogTextGray2"), Theme.getColor(this.textKey), this.checkedState));
            invalidate();
        }

        @Keep
        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), NUM));
        }

        public void setTextAndIcon(int i, CharSequence charSequence, RLottieDrawable rLottieDrawable, String str, String str2) {
            this.currentId = i;
            this.textView.setText(charSequence);
            this.imageView.setAnimation(rLottieDrawable);
            this.backgroundKey = str;
            this.textKey = str2;
            this.textView.setTextColor(ColorUtils.blendARGB(Theme.getColor("dialogTextGray2"), Theme.getColor(this.textKey), this.checkedState));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
            float dp = ((float) AndroidUtilities.dp(23.0f)) * scaleX;
            float left = (float) (this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2));
            float top = (float) (this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2));
            ChatAttachAlert.this.attachButtonPaint.setColor(Theme.getColor(this.backgroundKey));
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
            ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(((float) AndroidUtilities.dp(3.0f)) * scaleX);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(left, top, dp - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, top, dp - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
        }
    }

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        /* access modifiers changed from: private */
        public TLRPC$User currentUser;
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
                View view = new View(context);
                view.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1, AndroidUtilities.dp(23.0f)));
                addView(view, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
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
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setUser(TLRPC$User tLRPC$User) {
            if (tLRPC$User != null) {
                this.nameTextView.setTextColor(Theme.getColor("dialogTextGray2"));
                this.currentUser = tLRPC$User;
                this.nameTextView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
                this.avatarDrawable.setInfo(tLRPC$User);
                this.imageView.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
                requestLayout();
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlert(android.content.Context r29, org.telegram.ui.ActionBar.BaseFragment r30) {
        /*
            r28 = this;
            r6 = r28
            r7 = r29
            r8 = 0
            r6.<init>(r7, r8)
            org.telegram.ui.Components.ChatAttachAlert$1 r0 = new org.telegram.ui.Components.ChatAttachAlert$1
            java.lang.String r1 = "translation"
            r0.<init>(r1)
            r6.ATTACH_ALERT_LAYOUT_TRANSLATION = r0
            r9 = 6
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = new org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout[r9]
            r6.layouts = r0
            android.text.TextPaint r0 = new android.text.TextPaint
            r10 = 1
            r0.<init>(r10)
            r6.textPaint = r0
            android.graphics.RectF r0 = new android.graphics.RectF
            r0.<init>()
            r6.rect = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r10)
            r6.paint = r0
            r11 = 1065353216(0x3var_, float:1.0)
            r6.cornerRadius = r11
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r6.currentAccount = r0
            r6.mediaEnabled = r10
            r6.pollsEnabled = r10
            r12 = -1
            r6.maxSelectedPhotos = r12
            r6.allowOrder = r10
            r0 = 1118437376(0x42aa0000, float:85.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.attachItemSize = r0
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r0.<init>()
            r0 = 2
            int[] r1 = new int[r0]
            r6.scrollOffsetY = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r10)
            r6.attachButtonPaint = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r6.exclusionRects = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r6.exclustionRect = r1
            org.telegram.ui.Components.ChatAttachAlert$15 r1 = new org.telegram.ui.Components.ChatAttachAlert$15
            java.lang.String r2 = "openProgress"
            r1.<init>(r2)
            r6.ATTACH_ALERT_PROGRESS = r1
            r6.drawNavigationBar = r10
            android.view.animation.OvershootInterpolator r1 = new android.view.animation.OvershootInterpolator
            r2 = 1060320051(0x3var_, float:0.7)
            r1.<init>(r2)
            r6.openInterpolator = r1
            r1 = r30
            r6.baseFragment = r1
            r6.setDelegate(r6)
            int r1 = r6.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.reloadInlineHints
            r1.addObserver(r6, r2)
            java.util.ArrayList<android.graphics.Rect> r1 = r6.exclusionRects
            android.graphics.Rect r2 = r6.exclustionRect
            r1.add(r2)
            org.telegram.ui.Components.ChatAttachAlert$2 r1 = new org.telegram.ui.Components.ChatAttachAlert$2
            r1.<init>(r7, r8)
            r6.sizeNotifierFrameLayout = r1
            r6.containerView = r1
            r1.setWillNotDraw(r8)
            android.view.ViewGroup r1 = r6.containerView
            int r2 = r6.backgroundPaddingLeft
            r1.setPadding(r2, r8, r2, r8)
            org.telegram.ui.Components.ChatAttachAlert$3 r1 = new org.telegram.ui.Components.ChatAttachAlert$3
            r1.<init>(r7)
            r6.actionBar = r1
            java.lang.String r13 = "dialogBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r1.setBackgroundColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r2 = 2131165432(0x7var_f8, float:1.794508E38)
            r1.setBackButtonImage(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r14 = "dialogTextBlack"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r1.setItemsColor(r2, r8)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r15 = "dialogButtonSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r1.setItemsBackgroundColor(r2, r8)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r1.setTitleColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r1.setOccupyStatusBar(r8)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r5 = 0
            r1.setAlpha(r5)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            org.telegram.ui.Components.ChatAttachAlert$4 r2 = new org.telegram.ui.Components.ChatAttachAlert$4
            r2.<init>()
            r1.setActionBarMenuOnItemClick(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r4 = 0
            r1.<init>(r7, r4, r8, r2)
            r6.selectedMenuItem = r1
            r1.setLongClickEnabled(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r2 = 2131165439(0x7var_ff, float:1.7945095E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            java.lang.String r2 = "AccDescrMoreOptions"
            r3 = 2131623983(0x7f0e002f, float:1.8875133E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r3 = 4
            r1.setVisibility(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r1.setAlpha(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r1.setSubMenuOpenSide(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Nt32ReYKj24zlB7vHT-vQyIS3fQ r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Nt32ReYKj24zlB7vHT-vQyIS3fQ
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r1 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setAdditionalYOffset(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r9)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$ub7wHJjWI52pBpaKjvNcIp8HBK8 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$ub7wHJjWI52pBpaKjvNcIp8HBK8
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            java.lang.String r0 = "windowBackgroundWhiteBlueHeader"
            int r16 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r17 = 0
            r18 = 0
            r19 = 1
            r0 = r2
            r1 = r29
            r11 = r2
            r2 = r17
            r12 = 4
            r3 = r18
            r10 = r4
            r4 = r16
            r9 = 0
            r5 = r19
            r0.<init>(r1, r2, r3, r4, r5)
            r6.doneItem = r11
            r11.setLongClickEnabled(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.doneItem
            java.lang.String r1 = "Create"
            r2 = 2131624853(0x7f0e0395, float:1.8876897E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.doneItem
            r0.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.doneItem
            r0.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.doneItem
            r11 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.doneItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r2 = 3
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.doneItem
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$70irb8b3j6E6JPtp7E_VZic7gT0 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$70irb8b3j6E6JPtp7E_VZic7gT0
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r0.<init>(r7, r10, r8, r1)
            r6.searchItem = r0
            r0.setLongClickEnabled(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            r1 = 2131165442(0x7var_, float:1.7945101E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            java.lang.String r1 = "Search"
            r2 = 2131626748(0x7f0e0afc, float:1.888074E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            r0.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            r0.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            r1 = 1109917696(0x42280000, float:42.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$ZqUzbu7OWYZNWU7ujZFdDlGIODQ r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$ZqUzbu7OWYZNWU7ujZFdDlGIODQ
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$6 r0 = new org.telegram.ui.Components.ChatAttachAlert$6
            r0.<init>(r7)
            r6.selectedTextView = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r1 = 1098907648(0x41800000, float:16.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r6.selectedTextView
            java.lang.String r14 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r1 = 51
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r0.setVisibility(r12)
            android.widget.TextView r0 = r6.selectedTextView
            r0.setAlpha(r9)
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = r6.layouts
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout
            r1.<init>(r6, r7)
            r6.photoLayout = r1
            r0[r8] = r1
            r6.currentAttachLayout = r1
            r0 = 1
            r6.selectedId = r0
            android.view.ViewGroup r0 = r6.containerView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.TextView r1 = r6.selectedTextView
            r21 = -1
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 51
            r24 = 1102577664(0x41b80000, float:23.0)
            r25 = 0
            r26 = 1111490560(0x42400000, float:48.0)
            r27 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r2 = 48
            r3 = 53
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r3)
            r0.addView(r1, r4)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.searchItem
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r3)
            r0.addView(r1, r4)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.doneItem
            r4 = -2
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r2, r3)
            r0.addView(r1, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.actionBarShadow = r0
            r0.setAlpha(r9)
            android.view.View r0 = r6.actionBarShadow
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.actionBarShadow
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r0.addView(r1, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.shadow = r0
            r1 = 2131165268(0x7var_, float:1.7944748E38)
            r0.setBackgroundResource(r1)
            android.view.View r0 = r6.shadow
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r15 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1.<init>(r15, r3)
            r0.setColorFilter(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.shadow
            r19 = -1
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 83
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 1118306304(0x42a80000, float:84.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r3)
            org.telegram.ui.Components.ChatAttachAlert$7 r0 = new org.telegram.ui.Components.ChatAttachAlert$7
            r0.<init>(r7)
            r6.buttonsRecyclerView = r0
            org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter
            r1.<init>(r7)
            r6.buttonsAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r8, r8)
            r6.buttonsLayoutManager = r1
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setHorizontalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setItemAnimator(r10)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setLayoutAnimation(r10)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r6.buttonsRecyclerView
            r3 = 84
            r4 = 83
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r4)
            r0.addView(r1, r3)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$CyyHzKiqxFSiqyZ6mhfsqDdYtuM r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$CyyHzKiqxFSiqyZ6mhfsqDdYtuM
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$GOHvhL36PEGcbYhvoPR_0J6GKIQ r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$GOHvhL36PEGcbYhvoPR_0J6GKIQ
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.frameLayout2 = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.frameLayout2
            r0.setVisibility(r12)
            android.widget.FrameLayout r0 = r6.frameLayout2
            r0.setAlpha(r9)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.FrameLayout r1 = r6.frameLayout2
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2, r4)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r6.frameLayout2
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$p7kWdqALll_0oVPRRUX-vl_j2IM r1 = org.telegram.ui.Components.$$Lambda$ChatAttachAlert$p7kWdqALll_0oVPRRUXvl_j2IM.INSTANCE
            r0.setOnTouchListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$8 r10 = new org.telegram.ui.Components.ChatAttachAlert$8
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r6.sizeNotifierFrameLayout
            r4 = 0
            r5 = 1
            r0 = r10
            r1 = r28
            r2 = r29
            r0.<init>(r2, r3, r4, r5)
            r6.commentTextView = r10
            r0 = 1
            android.text.InputFilter[] r1 = new android.text.InputFilter[r0]
            android.text.InputFilter$LengthFilter r0 = new android.text.InputFilter$LengthFilter
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r2 = r2.maxCaptionLength
            r0.<init>(r2)
            r1[r8] = r0
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            r0.setFilters(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            java.lang.String r1 = "AddCaption"
            r2 = 2131624127(0x7f0e00bf, float:1.8875425E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setHint(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            r0.onResume()
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getEditText()
            r1 = 1
            r0.setMaxLines(r1)
            r0.setSingleLine(r1)
            android.widget.FrameLayout r0 = r6.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r1 = r6.commentTextView
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 1118306304(0x42a80000, float:84.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.writeButtonContainer = r0
            r0.setVisibility(r12)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r1)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r0.setScaleY(r1)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r0.setAlpha(r9)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            java.lang.String r2 = "Send"
            r3 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r0.setContentDescription(r2)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r16 = 60
            r17 = 1114636288(0x42700000, float:60.0)
            r18 = 85
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
            r22 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.writeButton = r0
            r0 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r3 = "dialogFloatingButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r5 = android.os.Build.VERSION.SDK_INT
            r10 = 21
            if (r5 < r10) goto L_0x0459
            java.lang.String r3 = "dialogFloatingButtonPressed"
        L_0x0459:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r4, r3)
            r6.writeButtonDrawable = r2
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 >= r10) goto L_0x0494
            android.content.res.Resources r2 = r29.getResources()
            r3 = 2131165388(0x7var_cc, float:1.7944992E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r15, r4)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.Drawable r4 = r6.writeButtonDrawable
            r3.<init>(r2, r4, r8, r8)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3.setIconSize(r2, r4)
            r6.writeButtonDrawable = r3
        L_0x0494:
            android.widget.ImageView r2 = r6.writeButton
            android.graphics.drawable.Drawable r3 = r6.writeButtonDrawable
            r2.setBackgroundDrawable(r3)
            android.widget.ImageView r2 = r6.writeButton
            r3 = 2131165267(0x7var_, float:1.7944746E38)
            r2.setImageResource(r3)
            android.widget.ImageView r2 = r6.writeButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "dialogFloatingIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r6.writeButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r10) goto L_0x04ca
            android.widget.ImageView r2 = r6.writeButton
            org.telegram.ui.Components.ChatAttachAlert$9 r3 = new org.telegram.ui.Components.ChatAttachAlert$9
            r3.<init>(r6)
            r2.setOutlineProvider(r3)
        L_0x04ca:
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            android.widget.ImageView r3 = r6.writeButton
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r10) goto L_0x04d7
            r4 = 56
            r15 = 56
            goto L_0x04db
        L_0x04d7:
            r4 = 60
            r15 = 60
        L_0x04db:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r10) goto L_0x04e2
            r16 = 1113587712(0x42600000, float:56.0)
            goto L_0x04e6
        L_0x04e2:
            r0 = 1114636288(0x42700000, float:60.0)
            r16 = 1114636288(0x42700000, float:60.0)
        L_0x04e6:
            r17 = 51
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r10) goto L_0x04f1
            r5 = 1073741824(0x40000000, float:2.0)
            r18 = 1073741824(0x40000000, float:2.0)
            goto L_0x04f3
        L_0x04f1:
            r18 = 0
        L_0x04f3:
            r19 = 0
            r20 = 0
            r21 = 0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r2.addView(r3, r0)
            android.widget.ImageView r0 = r6.writeButton
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Y4aVgruEnWhn1_LmIK2_xtaQBWc r2 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Y4aVgruEnWhn1_LmIK2_xtaQBWc
            r2.<init>()
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r6.writeButton
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$t3JD0Zfxfc_ljs90IMa-y95Ib1A r2 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$t3JD0Zfxfc_ljs90IMa-y95Ib1A
            r2.<init>()
            r0.setOnLongClickListener(r2)
            android.text.TextPaint r0 = r6.textPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = r6.textPaint
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r2)
            org.telegram.ui.Components.ChatAttachAlert$11 r0 = new org.telegram.ui.Components.ChatAttachAlert$11
            r0.<init>(r7)
            r6.selectedCountView = r0
            r0.setAlpha(r9)
            android.view.View r0 = r6.selectedCountView
            r0.setScaleX(r1)
            android.view.View r0 = r6.selectedCountView
            r0.setScaleY(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.selectedCountView
            r7 = 42
            r8 = 1103101952(0x41CLASSNAME, float:24.0)
            r9 = 85
            r10 = 0
            r11 = 0
            r12 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r13 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r0.addView(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlert(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    public /* synthetic */ void lambda$new$1$ChatAttachAlert(View view) {
        this.selectedMenuItem.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$ChatAttachAlert(View view) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    public /* synthetic */ void lambda$new$3$ChatAttachAlert(View view) {
        if (this.avatarPicker != 0) {
            this.delegate.openAvatarsSearch();
            dismiss();
            return;
        }
        final HashMap hashMap = new HashMap();
        final ArrayList arrayList = new ArrayList();
        PhotoPickerSearchActivity photoPickerSearchActivity = new PhotoPickerSearchActivity(hashMap, arrayList, 0, true, (ChatActivity) this.baseFragment);
        photoPickerSearchActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            private boolean sendPressed;

            public void onCaptionChanged(CharSequence charSequence) {
            }

            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            public void selectedPhotosChanged() {
            }

            public void actionButtonPressed(boolean z, boolean z2, int i) {
                if (!z && !hashMap.isEmpty() && !this.sendPressed) {
                    this.sendPressed = true;
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        Object obj = hashMap.get(arrayList.get(i2));
                        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                        arrayList.add(sendingMediaInfo);
                        MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                        String str = searchImage.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            sendingMediaInfo.searchImage = searchImage;
                        }
                        sendingMediaInfo.thumbPath = searchImage.thumbPath;
                        sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                        CharSequence charSequence = searchImage.caption;
                        sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                        sendingMediaInfo.entities = searchImage.entities;
                        sendingMediaInfo.masks = searchImage.stickers;
                        sendingMediaInfo.ttl = searchImage.ttl;
                        TLRPC$BotInlineResult tLRPC$BotInlineResult = searchImage.inlineResult;
                        if (tLRPC$BotInlineResult != null && searchImage.type == 1) {
                            sendingMediaInfo.inlineResult = tLRPC$BotInlineResult;
                            sendingMediaInfo.params = searchImage.params;
                        }
                        searchImage.date = (int) (System.currentTimeMillis() / 1000);
                    }
                    ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectSearchPhotos(arrayList, z2, i);
                }
            }
        });
        photoPickerSearchActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        this.baseFragment.presentFragment(photoPickerSearchActivity);
        dismiss();
    }

    public /* synthetic */ void lambda$new$6$ChatAttachAlert(View view, int i) {
        if (this.baseFragment.getParentActivity() != null) {
            if (view instanceof AttachButton) {
                int intValue = ((Integer) view.getTag()).intValue();
                if (intValue == 1) {
                    showLayout(this.photoLayout);
                } else if (intValue == 3) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                        openAudioLayout();
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    }
                } else if (intValue == 4) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                        openDocumentsLayout();
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    }
                } else if (intValue == 5) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                        openContactsLayout();
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
                        return;
                    }
                } else if (intValue == 6) {
                    if (AndroidUtilities.isGoogleMapsInstalled(this.baseFragment)) {
                        if (this.locationLayout == null) {
                            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                            ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = new ChatAttachAlertLocationLayout(this, getContext());
                            this.locationLayout = chatAttachAlertLocationLayout;
                            attachAlertLayoutArr[5] = chatAttachAlertLocationLayout;
                            chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlertLocationLayout.LocationActivityDelegate() {
                                public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                                    ChatAttachAlert.this.lambda$null$4$ChatAttachAlert(tLRPC$MessageMedia, i, z, i2);
                                }
                            });
                        }
                        showLayout(this.locationLayout);
                    } else {
                        return;
                    }
                } else if (intValue == 9) {
                    if (this.pollLayout == null) {
                        AttachAlertLayout[] attachAlertLayoutArr2 = this.layouts;
                        ChatAttachAlertPollLayout chatAttachAlertPollLayout = new ChatAttachAlertPollLayout(this, getContext());
                        this.pollLayout = chatAttachAlertPollLayout;
                        attachAlertLayoutArr2[1] = chatAttachAlertPollLayout;
                        chatAttachAlertPollLayout.setDelegate(new ChatAttachAlertPollLayout.PollCreateActivityDelegate() {
                            public final void sendPoll(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
                                ChatAttachAlert.this.lambda$null$5$ChatAttachAlert(tLRPC$TL_messageMediaPoll, hashMap, z, i);
                            }
                        });
                    }
                    showLayout(this.pollLayout);
                } else {
                    this.delegate.didPressedButton(((Integer) view.getTag()).intValue(), true, true, 0);
                }
                int left = view.getLeft();
                int right = view.getRight();
                int dp = AndroidUtilities.dp(10.0f);
                int i2 = left - dp;
                if (i2 < 0) {
                    this.buttonsRecyclerView.smoothScrollBy(i2, 0);
                    return;
                }
                int i3 = right + dp;
                if (i3 > this.buttonsRecyclerView.getMeasuredWidth()) {
                    RecyclerListView recyclerListView = this.buttonsRecyclerView;
                    recyclerListView.smoothScrollBy(i3 - recyclerListView.getMeasuredWidth(), 0);
                }
            } else if (view instanceof AttachBotButton) {
                this.delegate.didSelectBot(((AttachBotButton) view).currentUser);
                dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$4$ChatAttachAlert(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        ((ChatActivity) this.baseFragment).didSelectLocation(tLRPC$MessageMedia, i, z, i2);
    }

    public /* synthetic */ void lambda$null$5$ChatAttachAlert(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendPoll(tLRPC$TL_messageMediaPoll, hashMap, z, i);
    }

    public /* synthetic */ boolean lambda$new$8$ChatAttachAlert(View view, int i) {
        if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (!(this.baseFragment == null || attachBotButton.currentUser == null)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", NUM, ContactsController.formatName(attachBotButton.currentUser.first_name, attachBotButton.currentUser.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(attachBotButton) {
                    public final /* synthetic */ ChatAttachAlert.AttachBotButton f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatAttachAlert.this.lambda$null$7$ChatAttachAlert(this.f$1, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.show();
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$7$ChatAttachAlert(AttachBotButton attachBotButton, DialogInterface dialogInterface, int i) {
        MediaDataController.getInstance(this.currentAccount).removeInline(attachBotButton.currentUser.id);
    }

    public /* synthetic */ void lambda$new$11$ChatAttachAlert(View view) {
        if (this.editingMessageObject == null) {
            BaseFragment baseFragment2 = this.baseFragment;
            if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlert.this.lambda$null$10$ChatAttachAlert(z, i);
                    }
                });
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

    public /* synthetic */ void lambda$null$10$ChatAttachAlert(boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout) {
            sendPressed(z, i);
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i);
        dismiss();
    }

    public /* synthetic */ boolean lambda$new$15$ChatAttachAlert(View view) {
        View view2 = view;
        BaseFragment baseFragment2 = this.baseFragment;
        if ((baseFragment2 instanceof ChatActivity) && this.editingMessageObject == null) {
            ChatActivity chatActivity = (ChatActivity) baseFragment2;
            chatActivity.getCurrentChat();
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            if (chatActivity.getCurrentEncryptedChat() == null && !chatActivity.isInScheduleMode()) {
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                this.sendPopupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || ChatAttachAlert.this.sendPopupWindow == null || !ChatAttachAlert.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        ChatAttachAlert.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        ChatAttachAlert.this.lambda$null$12$ChatAttachAlert(keyEvent);
                    }
                });
                this.sendPopupLayout.setShowedFromBotton(false);
                this.itemCells = new ActionBarMenuSubItem[2];
                int i = 0;
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        if (!this.currentAttachLayout.canScheduleMessages()) {
                        }
                    } else if (i2 == 1 && UserObject.isUserSelf(currentUser)) {
                    }
                    this.itemCells[i2] = new ActionBarMenuSubItem(getContext());
                    if (i2 == 0) {
                        if (UserObject.isUserSelf(currentUser)) {
                            this.itemCells[i2].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                        } else {
                            this.itemCells[i2].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                        }
                    } else if (i2 == 1) {
                        this.itemCells[i2].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                    }
                    this.itemCells[i2].setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView(this.itemCells[i2], LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                    this.itemCells[i2].setOnClickListener(new View.OnClickListener(i2, chatActivity) {
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ ChatActivity f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(View view) {
                            ChatAttachAlert.this.lambda$null$14$ChatAttachAlert(this.f$1, this.f$2, view);
                        }
                    });
                    i++;
                }
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
                int[] iArr = new int[2];
                view2.getLocationInWindow(iArr);
                this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
                this.sendPopupWindow.dimBehind();
                view2.performHapticFeedback(3, 2);
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$12$ChatAttachAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$14$ChatAttachAlert(int i, ChatActivity chatActivity, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatAttachAlert.this.lambda$null$13$ChatAttachAlert(z, i);
                }
            });
        } else if (i == 1) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == this.photoLayout) {
                sendPressed(false, 0);
                return;
            }
            attachAlertLayout.sendSelectedItems(false, 0);
            dismiss();
        }
    }

    public /* synthetic */ void lambda$null$13$ChatAttachAlert(boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout) {
            sendPressed(z, i);
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i);
        dismiss();
    }

    public void show() {
        super.show();
        this.buttonPressed = false;
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            this.calcMandatoryInsets = ((ChatActivity) baseFragment2).isKeyboardVisible();
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

    private void sendPressed(boolean z, int i) {
        if (!this.buttonPressed) {
            BaseFragment baseFragment2 = this.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + chatActivity.getDialogId(), !z).commit();
                }
            }
            applyCaption();
            this.buttonPressed = true;
            this.delegate.didPressedButton(7, true, z, i);
        }
    }

    private void showLayout(AttachAlertLayout attachAlertLayout) {
        if (this.viewChangeAnimator == null && this.commentsAnimator == null) {
            AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
            if (attachAlertLayout2 == attachAlertLayout) {
                attachAlertLayout2.scrollToTop();
                return;
            }
            int i = 4;
            if (attachAlertLayout == this.photoLayout) {
                this.selectedId = 1;
            } else if (attachAlertLayout == this.audioLayout) {
                this.selectedId = 3;
            } else if (attachAlertLayout == this.documentLayout) {
                this.selectedId = 4;
            } else if (attachAlertLayout == this.contactsLayout) {
                this.selectedId = 5;
            } else if (attachAlertLayout == this.locationLayout) {
                this.selectedId = 6;
            } else if (attachAlertLayout == this.pollLayout) {
                this.selectedId = 9;
            }
            int childCount = this.buttonsRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.buttonsRecyclerView.getChildAt(i2);
                if (childAt instanceof AttachButton) {
                    ((AttachButton) childAt).updateCheckedState(true);
                }
            }
            int firstOffset = (this.currentAttachLayout.getFirstOffset() - AndroidUtilities.dp(11.0f)) - this.scrollOffsetY[0];
            this.nextAttachLayout = attachAlertLayout;
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
            if (attachAlertLayout.getParent() != null) {
                this.containerView.removeView(this.nextAttachLayout);
            }
            int indexOfChild = this.containerView.indexOfChild(this.currentAttachLayout);
            ViewGroup viewGroup = this.containerView;
            AttachAlertLayout attachAlertLayout3 = this.nextAttachLayout;
            if (attachAlertLayout3 != this.locationLayout) {
                indexOfChild++;
            }
            viewGroup.addView(attachAlertLayout3, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
            this.nextAttachLayout.setTranslationY((float) AndroidUtilities.dp(78.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.currentAttachLayout, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(78.0f) + firstOffset)}), ObjectAnimator.ofFloat(this.currentAttachLayout, this.ATTACH_ALERT_LAYOUT_TRANSLATION, new float[]{0.0f, 1.0f})});
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.setDuration(180);
            animatorSet.setStartDelay(20);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
                    SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
                    springAnimation.getSpring().setDampingRatio(0.7f);
                    springAnimation.getSpring().setStiffness(400.0f);
                    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                            ChatAttachAlert.AnonymousClass12.this.lambda$onAnimationEnd$0$ChatAttachAlert$12(dynamicAnimation, f, f2);
                        }
                    });
                    springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                            ChatAttachAlert.AnonymousClass12.this.lambda$onAnimationEnd$1$ChatAttachAlert$12(dynamicAnimation, z, f, f2);
                        }
                    });
                    Object unused = ChatAttachAlert.this.viewChangeAnimator = springAnimation;
                    springAnimation.start();
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$ChatAttachAlert$12(DynamicAnimation dynamicAnimation, float f, float f2) {
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert.this.updateSelectedPosition(1);
                    }
                    ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated();
                    ChatAttachAlert.this.containerView.invalidate();
                }

                public /* synthetic */ void lambda$onAnimationEnd$1$ChatAttachAlert$12(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
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
                    int[] iArr = ChatAttachAlert.this.scrollOffsetY;
                    iArr[0] = iArr[1];
                }
            });
            this.viewChangeAnimator = animatorSet;
            animatorSet.start();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 5 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
            openContactsLayout();
        }
    }

    private void openContactsLayout() {
        if (this.contactsLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = new ChatAttachAlertContactsLayout(this, getContext());
            this.contactsLayout = chatAttachAlertContactsLayout;
            attachAlertLayoutArr[2] = chatAttachAlertContactsLayout;
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate() {
                public final void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i) {
                    ChatAttachAlert.this.lambda$openContactsLayout$16$ChatAttachAlert(tLRPC$User, z, i);
                }
            });
        }
        showLayout(this.contactsLayout);
    }

    public /* synthetic */ void lambda$openContactsLayout$16$ChatAttachAlert(TLRPC$User tLRPC$User, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendContact(tLRPC$User, z, i);
    }

    /* access modifiers changed from: private */
    public void openAudioLayout() {
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext());
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlertAudioLayout.AudioSelectDelegate() {
                public final void didSelectAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
                    ChatAttachAlert.this.lambda$openAudioLayout$17$ChatAttachAlert(arrayList, charSequence, z, i);
                }
            });
        }
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
            this.audioLayout.setMaxSelectedFiles(((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) ? -1 : 1);
        }
        showLayout(this.audioLayout);
    }

    public /* synthetic */ void lambda$openAudioLayout$17$ChatAttachAlert(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendAudio(arrayList, charSequence, z, i);
    }

    private void openDocumentsLayout() {
        if (this.documentLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), false);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() {
                public void didSelectFiles(ArrayList<String> arrayList, String str, boolean z, int i) {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).didSelectFiles(arrayList, str, z, i);
                    } else if (baseFragment instanceof PassportActivity) {
                        ((PassportActivity) baseFragment).didSelectFiles(arrayList, str, z, i);
                    }
                }

                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).didSelectPhotos(arrayList, z, i);
                    } else if (baseFragment instanceof PassportActivity) {
                        ((PassportActivity) baseFragment).didSelectPhotos(arrayList, z, i);
                    }
                }

                public void startDocumentSelectActivity() {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).startDocumentSelectActivity();
                    } else if (baseFragment instanceof PassportActivity) {
                        ((PassportActivity) baseFragment).startDocumentSelectActivity();
                    }
                }

                public void startMusicSelectActivity() {
                    ChatAttachAlert.this.openAudioLayout();
                }
            });
        }
        BaseFragment baseFragment2 = this.baseFragment;
        int i = 1;
        if (baseFragment2 instanceof ChatActivity) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout2 = this.documentLayout;
            if ((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) {
                i = -1;
            }
            chatAttachAlertDocumentLayout2.setMaxSelectedFiles(i);
        } else {
            this.documentLayout.setMaxSelectedFiles(this.maxSelectedPhotos);
            this.documentLayout.setCanSelectOnlyImageFiles(true);
        }
        showLayout(this.documentLayout);
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.commentsAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.2f;
        float f2 = 0.0f;
        if (z2) {
            this.commentsAnimator = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.frameLayout2;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            FrameLayout frameLayout5 = this.writeButtonContainer;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property4, fArr4));
            View view = this.selectedCountView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property5, fArr5));
            View view2 = this.selectedCountView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr6[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
            View view3 = this.selectedCountView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
            if (this.actionBar.getTag() != null) {
                FrameLayout frameLayout6 = this.frameLayout2;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
                arrayList.add(ObjectAnimator.ofFloat(frameLayout6, property8, fArr8));
                View view4 = this.shadow;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = (float) (z ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f));
                arrayList.add(ObjectAnimator.ofFloat(view4, property9, fArr9));
                View view5 = this.shadow;
                Property property10 = View.ALPHA;
                float[] fArr10 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view5, property10, fArr10));
            } else {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                Property property11 = View.TRANSLATION_Y;
                float[] fArr11 = new float[1];
                fArr11[0] = z ? (float) AndroidUtilities.dp(36.0f) : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, property11, fArr11));
                View view6 = this.shadow;
                Property property12 = View.TRANSLATION_Y;
                float[] fArr12 = new float[1];
                if (z) {
                    f2 = (float) AndroidUtilities.dp(36.0f);
                }
                fArr12[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view6, property12, fArr12));
            }
            this.commentsAnimator.playTogether(arrayList);
            this.commentsAnimator.setInterpolator(new DecelerateInterpolator());
            this.commentsAnimator.setDuration(180);
            this.commentsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.commentsAnimator)) {
                        if (!z) {
                            ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlert.this.commentsAnimator = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.commentsAnimator)) {
                        AnimatorSet unused = ChatAttachAlert.this.commentsAnimator = null;
                    }
                }
            });
            this.commentsAnimator.start();
        } else {
            this.frameLayout2.setAlpha(z ? 1.0f : 0.0f);
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view7 = this.selectedCountView;
            if (z) {
                f = 1.0f;
            }
            view7.setScaleY(f);
            this.selectedCountView.setAlpha(z ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((float) (z ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f)));
                View view8 = this.shadow;
                if (z) {
                    f2 = 1.0f;
                }
                view8.setAlpha(f2);
            } else {
                this.buttonsRecyclerView.setTranslationY(z ? (float) AndroidUtilities.dp(36.0f) : 0.0f);
                View view9 = this.shadow;
                if (z) {
                    f2 = (float) AndroidUtilities.dp(36.0f);
                }
                view9.setTranslationY(f2);
            }
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
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
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.currentAttachLayout.onContainerViewTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(EditTextBoldCursor editTextBoldCursor) {
        if (!this.enterCommentEventSent) {
            this.delegate.needEnterComment();
            setFocusable(true);
            this.enterCommentEventSent = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            AttachButton attachButton = (AttachButton) view;
            attachButton.textView.setTextColor(ColorUtils.blendARGB(Theme.getColor("dialogTextGray2"), Theme.getColor(attachButton.textKey), attachButton.checkedState));
        } else if (view instanceof AttachBotButton) {
            ((AttachBotButton) view).nameTextView.setTextColor(Theme.getColor("dialogTextGray2"));
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions;
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i < attachAlertLayoutArr.length) {
                if (!(attachAlertLayoutArr[i] == null || (themeDescriptions = attachAlertLayoutArr[i].getThemeDescriptions()) == null)) {
                    arrayList.addAll(themeDescriptions);
                }
                i++;
            } else {
                arrayList.add(new ThemeDescription(this.container, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
                return arrayList;
            }
        }
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            int i = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(i2));
            }
            this.selectedTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.doneItem.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.selectedMenuItem.setIconColor(Theme.getColor("dialogTextBlack"));
            Theme.setDrawableColor(this.selectedMenuItem.getBackground(), Theme.getColor("dialogButtonSelector"));
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), true);
            this.selectedMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.searchItem.setIconColor(Theme.getColor("dialogTextBlack"));
            Theme.setDrawableColor(this.searchItem.getBackground(), Theme.getColor("dialogButtonSelector"));
            this.commentTextView.updateColors();
            if (this.sendPopupLayout != null) {
                int i3 = 0;
                while (true) {
                    ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.itemCells;
                    if (i3 >= actionBarMenuSubItemArr.length) {
                        break;
                    }
                    if (actionBarMenuSubItemArr[i3] != null) {
                        actionBarMenuSubItemArr[i3].setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                        this.itemCells[i3].setSelectorColor(Theme.getColor("dialogButtonSelector"));
                    }
                    i3++;
                }
                this.sendPopupLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
                ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
                if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                    this.sendPopupLayout.invalidate();
                }
            }
            String str = "dialogFloatingButton";
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor(str), false);
            Drawable drawable = this.writeButtonDrawable;
            if (Build.VERSION.SDK_INT >= 21) {
                str = "dialogFloatingButtonPressed";
            }
            Theme.setSelectorDrawableColor(drawable, Theme.getColor(str), true);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
            this.buttonsRecyclerView.setGlowColor(Theme.getColor("dialogScrollGlow"));
            this.buttonsRecyclerView.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.selectedCountView.invalidate();
            this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
            this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
            Theme.setDrawableColor(this.shadowDrawable, Theme.getColor("dialogBackground"));
            this.containerView.invalidate();
            while (true) {
                AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                if (i < attachAlertLayoutArr.length) {
                    if (attachAlertLayoutArr[i] != null) {
                        attachAlertLayoutArr[i].checkColors();
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        return this.photoLayout.onCustomMeasure(view, i, i2);
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return this.photoLayout.onCustomLayout(view, i, i2, i3, i4);
    }

    public void onPause() {
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[i] != null) {
                    attachAlertLayoutArr[i].onPause();
                }
                i++;
            } else {
                this.paused = true;
                return;
            }
        }
    }

    public void onResume() {
        int i = 0;
        this.paused = false;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[i] != null) {
                    attachAlertLayoutArr[i].onResume();
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void onActivityResultFragment(int i, Intent intent, String str) {
        this.photoLayout.onActivityResultFragment(i, intent, str);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ButtonsAdapter buttonsAdapter2;
        if (i == NotificationCenter.reloadInlineHints && (buttonsAdapter2 = this.buttonsAdapter) != null) {
            buttonsAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition(int i) {
        int i2;
        int i3;
        float f;
        int i4;
        int i5;
        AttachAlertLayout attachAlertLayout = i == 0 ? this.currentAttachLayout : this.nextAttachLayout;
        int i6 = this.scrollOffsetY[i] - this.backgroundPaddingTop;
        if (attachAlertLayout == this.pollLayout) {
            i3 = i6 - AndroidUtilities.dp(13.0f);
            i2 = AndroidUtilities.dp(11.0f);
        } else {
            i3 = i6 - AndroidUtilities.dp(39.0f);
            i2 = AndroidUtilities.dp(43.0f);
        }
        float f2 = (float) i2;
        if (this.backgroundPaddingTop + i3 < ActionBar.getCurrentActionBarHeight()) {
            f = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - i3) - this.backgroundPaddingTop)) / f2);
            this.cornerRadius = 1.0f - f;
        } else {
            this.cornerRadius = 1.0f;
            f = 0.0f;
        }
        if (AndroidUtilities.isTablet()) {
            i4 = 16;
        } else {
            Point point = AndroidUtilities.displaySize;
            i4 = point.x > point.y ? 6 : 12;
        }
        float dp = this.actionBar.getAlpha() != 0.0f ? 0.0f : (float) AndroidUtilities.dp((1.0f - this.selectedTextView.getAlpha()) * 26.0f);
        if (!this.menuShowed || this.avatarPicker != 0) {
            this.selectedMenuItem.setTranslationY((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (i4 + 37))));
        } else {
            this.selectedMenuItem.setTranslationY(((float) (this.scrollOffsetY[i] - AndroidUtilities.dp((((float) i4) * f) + 37.0f))) + dp);
        }
        this.searchItem.setTranslationY((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (i4 + 37))));
        TextView textView = this.selectedTextView;
        float dp2 = ((float) (this.scrollOffsetY[i] - AndroidUtilities.dp((((float) i4) * f) + 25.0f))) + dp;
        this.baseSelectedTextViewTranslationY = dp2;
        textView.setTranslationY(dp2);
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout != null && attachAlertLayout == chatAttachAlertPollLayout) {
            if (AndroidUtilities.isTablet()) {
                i5 = 63;
            } else {
                Point point2 = AndroidUtilities.displaySize;
                i5 = point2.x > point2.y ? 53 : 59;
            }
            this.doneItem.setTranslationY(Math.max(0.0f, (this.pollLayout.getTranslationY() + ((float) this.scrollOffsetY[i])) - ((float) AndroidUtilities.dp((((float) i5) * f) + 7.0f))));
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0068, code lost:
        if (((org.telegram.ui.ChatActivity) r4).allowSendGifs() != false) goto L_0x006d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0121  */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateLayout(org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r12, boolean r13) {
        /*
            r11 = this;
            if (r12 != 0) goto L_0x0003
            return
        L_0x0003:
            int r0 = r12.getCurrentItemTop()
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto L_0x000d
            return
        L_0x000d:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r1 = r11.currentAttachLayout
            r2 = 1
            r3 = 0
            if (r12 != r1) goto L_0x001b
            int r1 = r12.getButtonsHideOffset()
            if (r0 > r1) goto L_0x001b
            r1 = 1
            goto L_0x001c
        L_0x001b:
            r1 = 0
        L_0x001c:
            boolean r4 = r11.keyboardVisible
            if (r4 == 0) goto L_0x0023
            if (r13 == 0) goto L_0x0023
            r13 = 0
        L_0x0023:
            if (r1 == 0) goto L_0x002d
            org.telegram.ui.ActionBar.ActionBar r4 = r11.actionBar
            java.lang.Object r4 = r4.getTag()
            if (r4 == 0) goto L_0x0037
        L_0x002d:
            if (r1 != 0) goto L_0x017b
            org.telegram.ui.ActionBar.ActionBar r4 = r11.actionBar
            java.lang.Object r4 = r4.getTag()
            if (r4 == 0) goto L_0x017b
        L_0x0037:
            org.telegram.ui.ActionBar.ActionBar r4 = r11.actionBar
            r5 = 0
            if (r1 == 0) goto L_0x0041
            java.lang.Integer r6 = java.lang.Integer.valueOf(r2)
            goto L_0x0042
        L_0x0041:
            r6 = r5
        L_0x0042:
            r4.setTag(r6)
            android.animation.AnimatorSet r4 = r11.actionBarAnimation
            if (r4 == 0) goto L_0x004e
            r4.cancel()
            r11.actionBarAnimation = r5
        L_0x004e:
            boolean r4 = r11.avatarSearch
            if (r4 != 0) goto L_0x006d
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r11.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r5 = r11.photoLayout
            if (r4 != r5) goto L_0x006b
            boolean r4 = r11.menuShowed
            if (r4 != 0) goto L_0x006b
            org.telegram.ui.ActionBar.BaseFragment r4 = r11.baseFragment
            boolean r5 = r4 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x006b
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            boolean r4 = r4.allowSendGifs()
            if (r4 == 0) goto L_0x006b
            goto L_0x006d
        L_0x006b:
            r4 = 0
            goto L_0x006e
        L_0x006d:
            r4 = 1
        L_0x006e:
            if (r1 == 0) goto L_0x008b
            if (r4 == 0) goto L_0x0077
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r11.searchItem
            r5.setVisibility(r3)
        L_0x0077:
            int r5 = r11.avatarPicker
            if (r5 != 0) goto L_0x0085
            boolean r5 = r11.menuShowed
            if (r5 != 0) goto L_0x0094
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r5 = r11.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r6 = r11.photoLayout
            if (r5 != r6) goto L_0x0094
        L_0x0085:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r11.selectedMenuItem
            r5.setVisibility(r3)
            goto L_0x0094
        L_0x008b:
            int r5 = r11.avatarPicker
            if (r5 != 0) goto L_0x0094
            org.telegram.ui.Components.RecyclerListView r5 = r11.buttonsRecyclerView
            r5.setVisibility(r3)
        L_0x0094:
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            if (r13 == 0) goto L_0x0121
            android.animation.AnimatorSet r13 = new android.animation.AnimatorSet
            r13.<init>()
            r11.actionBarAnimation = r13
            r7 = 180(0xb4, double:8.9E-322)
            r13.setDuration(r7)
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            org.telegram.ui.ActionBar.ActionBar r7 = r11.actionBar
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r1 == 0) goto L_0x00b5
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x00b6
        L_0x00b5:
            r10 = 0
        L_0x00b6:
            r9[r3] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r13.add(r7)
            android.view.View r7 = r11.actionBarShadow
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r1 == 0) goto L_0x00ca
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x00cb
        L_0x00ca:
            r10 = 0
        L_0x00cb:
            r9[r3] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r13.add(r7)
            if (r4 == 0) goto L_0x00eb
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r11.searchItem
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            if (r1 == 0) goto L_0x00e1
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x00e2
        L_0x00e1:
            r9 = 0
        L_0x00e2:
            r8[r3] = r9
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r7, r8)
            r13.add(r4)
        L_0x00eb:
            int r4 = r11.avatarPicker
            if (r4 != 0) goto L_0x00f9
            boolean r4 = r11.menuShowed
            if (r4 != 0) goto L_0x010c
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r11.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r7 = r11.photoLayout
            if (r4 != r7) goto L_0x010c
        L_0x00f9:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r11.selectedMenuItem
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            if (r1 == 0) goto L_0x0102
            goto L_0x0103
        L_0x0102:
            r5 = 0
        L_0x0103:
            r8[r3] = r5
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r7, r8)
            r13.add(r4)
        L_0x010c:
            android.animation.AnimatorSet r4 = r11.actionBarAnimation
            r4.playTogether(r13)
            android.animation.AnimatorSet r13 = r11.actionBarAnimation
            org.telegram.ui.Components.ChatAttachAlert$16 r4 = new org.telegram.ui.Components.ChatAttachAlert$16
            r4.<init>(r1)
            r13.addListener(r4)
            android.animation.AnimatorSet r13 = r11.actionBarAnimation
            r13.start()
            goto L_0x017b
        L_0x0121:
            r13 = 4
            if (r1 == 0) goto L_0x012d
            int r7 = r11.avatarPicker
            if (r7 != 0) goto L_0x012d
            org.telegram.ui.Components.RecyclerListView r7 = r11.buttonsRecyclerView
            r7.setVisibility(r13)
        L_0x012d:
            org.telegram.ui.ActionBar.ActionBar r7 = r11.actionBar
            if (r1 == 0) goto L_0x0134
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x0135
        L_0x0134:
            r8 = 0
        L_0x0135:
            r7.setAlpha(r8)
            android.view.View r7 = r11.actionBarShadow
            if (r1 == 0) goto L_0x013f
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x0140
        L_0x013f:
            r8 = 0
        L_0x0140:
            r7.setAlpha(r8)
            if (r4 == 0) goto L_0x0150
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r11.searchItem
            if (r1 == 0) goto L_0x014c
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x014d
        L_0x014c:
            r7 = 0
        L_0x014d:
            r4.setAlpha(r7)
        L_0x0150:
            int r4 = r11.avatarPicker
            if (r4 != 0) goto L_0x015e
            boolean r4 = r11.menuShowed
            if (r4 != 0) goto L_0x0167
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r11.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r7 = r11.photoLayout
            if (r4 != r7) goto L_0x0167
        L_0x015e:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r11.selectedMenuItem
            if (r1 == 0) goto L_0x0163
            goto L_0x0164
        L_0x0163:
            r5 = 0
        L_0x0164:
            r4.setAlpha(r5)
        L_0x0167:
            if (r1 != 0) goto L_0x017b
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r11.searchItem
            r1.setVisibility(r13)
            int r1 = r11.avatarPicker
            if (r1 != 0) goto L_0x0176
            boolean r1 = r11.menuShowed
            if (r1 != 0) goto L_0x017b
        L_0x0176:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r11.selectedMenuItem
            r1.setVisibility(r13)
        L_0x017b:
            android.view.ViewGroup$LayoutParams r13 = r12.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            int r13 = r13.topMargin
            r1 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r13 = r13 - r1
            int r0 = r0 + r13
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r13 = r11.currentAttachLayout
            if (r13 != r12) goto L_0x0190
            r2 = 0
        L_0x0190:
            int[] r12 = r11.scrollOffsetY
            r13 = r12[r2]
            if (r13 == r0) goto L_0x01a0
            r12[r2] = r0
            r11.updateSelectedPosition(r2)
            android.view.ViewGroup r12 = r11.containerView
            r12.invalidate()
        L_0x01a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateLayout(org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout, boolean):void");
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
            int r2 = r0.avatarPicker
            if (r2 == 0) goto L_0x00db
            goto L_0x00dd
        L_0x00db:
            r2 = 0
            goto L_0x00de
        L_0x00dd:
            r2 = 1
        L_0x00de:
            r0.menuShowed = r2
            android.animation.AnimatorSet r2 = r0.menuAnimator
            if (r2 == 0) goto L_0x00ea
            r2.cancel()
            r2 = 0
            r0.menuAnimator = r2
        L_0x00ea:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.Object r2 = r2.getTag()
            if (r2 == 0) goto L_0x0102
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.baseFragment
            boolean r9 = r2 instanceof org.telegram.ui.ChatActivity
            if (r9 == 0) goto L_0x0102
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            boolean r2 = r2.allowSendGifs()
            if (r2 == 0) goto L_0x0102
            r2 = 1
            goto L_0x0103
        L_0x0102:
            r2 = 0
        L_0x0103:
            boolean r9 = r0.menuShowed
            if (r9 == 0) goto L_0x0116
            int r9 = r0.avatarPicker
            if (r9 != 0) goto L_0x0110
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.selectedMenuItem
            r9.setVisibility(r8)
        L_0x0110:
            android.widget.TextView r9 = r0.selectedTextView
            r9.setVisibility(r8)
            goto L_0x0123
        L_0x0116:
            org.telegram.ui.ActionBar.ActionBar r9 = r0.actionBar
            java.lang.Object r9 = r9.getTag()
            if (r9 == 0) goto L_0x0123
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.searchItem
            r9.setVisibility(r8)
        L_0x0123:
            if (r1 != 0) goto L_0x0163
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.Object r1 = r1.getTag()
            if (r1 != 0) goto L_0x013e
            int r1 = r0.avatarPicker
            if (r1 != 0) goto L_0x013e
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.selectedMenuItem
            boolean r3 = r0.menuShowed
            if (r3 == 0) goto L_0x013a
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x013b
        L_0x013a:
            r3 = 0
        L_0x013b:
            r1.setAlpha(r3)
        L_0x013e:
            android.widget.TextView r1 = r0.selectedTextView
            boolean r3 = r0.menuShowed
            if (r3 == 0) goto L_0x0147
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x0148
        L_0x0147:
            r3 = 0
        L_0x0148:
            r1.setAlpha(r3)
            if (r2 == 0) goto L_0x0157
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            boolean r2 = r0.menuShowed
            if (r2 == 0) goto L_0x0154
            r5 = 0
        L_0x0154:
            r1.setAlpha(r5)
        L_0x0157:
            boolean r1 = r0.menuShowed
            if (r1 == 0) goto L_0x01d8
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r2 = 4
            r1.setVisibility(r2)
            goto L_0x01d8
        L_0x0163:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.menuAnimator = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBar r9 = r0.actionBar
            java.lang.Object r9 = r9.getTag()
            if (r9 != 0) goto L_0x0192
            int r9 = r0.avatarPicker
            if (r9 != 0) goto L_0x0192
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.selectedMenuItem
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r7]
            boolean r12 = r0.menuShowed
            if (r12 == 0) goto L_0x0188
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0189
        L_0x0188:
            r12 = 0
        L_0x0189:
            r11[r8] = r12
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r1.add(r9)
        L_0x0192:
            android.widget.TextView r9 = r0.selectedTextView
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r7]
            boolean r12 = r0.menuShowed
            if (r12 == 0) goto L_0x019f
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x01a0
        L_0x019f:
            r12 = 0
        L_0x01a0:
            r11[r8] = r12
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r1.add(r9)
            if (r2 == 0) goto L_0x01bf
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            android.util.Property r9 = android.view.View.ALPHA
            float[] r7 = new float[r7]
            boolean r10 = r0.menuShowed
            if (r10 == 0) goto L_0x01b6
            r5 = 0
        L_0x01b6:
            r7[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r9, r7)
            r1.add(r2)
        L_0x01bf:
            android.animation.AnimatorSet r2 = r0.menuAnimator
            r2.playTogether(r1)
            android.animation.AnimatorSet r1 = r0.menuAnimator
            org.telegram.ui.Components.ChatAttachAlert$17 r2 = new org.telegram.ui.Components.ChatAttachAlert$17
            r2.<init>()
            r1.addListener(r2)
            android.animation.AnimatorSet r1 = r0.menuAnimator
            r1.setDuration(r3)
            android.animation.AnimatorSet r1 = r0.menuAnimator
            r1.start()
        L_0x01d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateCountButton(int):void");
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void init() {
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 != null) {
            if (baseFragment2 instanceof ChatActivity) {
                TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
                TLRPC$User currentUser = ((ChatActivity) this.baseFragment).getCurrentUser();
                if (currentChat != null) {
                    this.mediaEnabled = ChatObject.canSendMedia(currentChat);
                    this.pollsEnabled = ChatObject.canSendPolls(currentChat);
                } else {
                    this.pollsEnabled = currentUser != null && currentUser.bot;
                }
            } else {
                this.commentTextView.setVisibility(4);
            }
            this.photoLayout.onInit(this.mediaEnabled);
            this.commentTextView.hidePopup(true);
            this.enterCommentEventSent = false;
            setFocusable(false);
            if (this.currentAttachLayout != this.photoLayout) {
                if (this.actionBar.isSearchFieldVisible()) {
                    this.actionBar.closeSearchField();
                }
                this.containerView.removeView(this.currentAttachLayout);
                this.currentAttachLayout.onHide();
                this.currentAttachLayout.setVisibility(8);
                this.currentAttachLayout.onHidden();
                this.currentAttachLayout = this.photoLayout;
                setAllowNestedScroll(true);
                if (this.currentAttachLayout.getParent() == null) {
                    this.containerView.addView(this.currentAttachLayout, 0, LayoutHelper.createFrame(-1, -1.0f));
                }
                this.selectedId = 1;
                this.photoLayout.setAlpha(1.0f);
                this.photoLayout.setVisibility(0);
                this.photoLayout.onShow();
                this.photoLayout.onShown();
                this.actionBar.setVisibility(0);
                this.actionBarShadow.setVisibility(0);
            }
            updateCountButton(0);
            this.buttonsAdapter.notifyDataSetChanged();
            this.commentTextView.setText("");
            this.buttonsLayoutManager.scrollToPositionWithOffset(0, 1000000);
        }
    }

    public void onDestroy() {
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[i] != null) {
                attachAlertLayoutArr[i].onDestroy();
            }
            i++;
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
    }

    public void setAllowDrawContent(boolean z) {
        super.setAllowDrawContent(z);
        this.currentAttachLayout.onContainerTranslationUpdated();
    }

    public void setAvatarPicker(int i, boolean z) {
        this.avatarPicker = i;
        this.avatarSearch = z;
        if (i != 0) {
            this.buttonsRecyclerView.setVisibility(8);
            this.shadow.setVisibility(8);
            if (this.avatarPicker == 2) {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", NUM));
            } else {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhoto", NUM));
            }
        }
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        if (this.editingMessageObject == null) {
            this.maxSelectedPhotos = i;
            this.allowOrder = z;
        }
    }

    public void setOpenWithFrontFaceCamera(boolean z) {
        this.openWithFrontFaceCamera = z;
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ButtonsAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new AttachBotButton(this.mContext);
            } else {
                view = new AttachButton(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                AttachButton attachButton = (AttachButton) viewHolder.itemView;
                if (i == this.galleryButton) {
                    attachButton.setTextAndIcon(1, LocaleController.getString("ChatGallery", NUM), Theme.chat_attachButtonDrawables[0], "chat_attachGalleryBackground", "chat_attachGalleryText");
                    attachButton.setTag(1);
                } else if (i == this.documentButton) {
                    attachButton.setTextAndIcon(4, LocaleController.getString("ChatDocument", NUM), Theme.chat_attachButtonDrawables[2], "chat_attachFileBackground", "chat_attachFileText");
                    attachButton.setTag(4);
                } else if (i == this.locationButton) {
                    attachButton.setTextAndIcon(6, LocaleController.getString("ChatLocation", NUM), Theme.chat_attachButtonDrawables[4], "chat_attachLocationBackground", "chat_attachLocationText");
                    attachButton.setTag(6);
                } else if (i == this.musicButton) {
                    attachButton.setTextAndIcon(3, LocaleController.getString("AttachMusic", NUM), Theme.chat_attachButtonDrawables[1], "chat_attachAudioBackground", "chat_attachAudioText");
                    attachButton.setTag(3);
                } else if (i == this.pollButton) {
                    attachButton.setTextAndIcon(9, LocaleController.getString("Poll", NUM), Theme.chat_attachButtonDrawables[5], "chat_attachPollBackground", "chat_attachPollText");
                    attachButton.setTag(9);
                } else if (i == this.contactButton) {
                    attachButton.setTextAndIcon(5, LocaleController.getString("AttachContact", NUM), Theme.chat_attachButtonDrawables[3], "chat_attachContactBackground", "chat_attachContactText");
                    attachButton.setTag(5);
                }
            } else if (itemViewType == 1) {
                int i2 = i - this.buttonsCount;
                AttachBotButton attachBotButton = (AttachBotButton) viewHolder.itemView;
                attachBotButton.setTag(Integer.valueOf(i2));
                attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i2).peer.user_id)));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            ChatAttachAlert.this.applyAttachButtonColors(viewHolder.itemView);
        }

        public int getItemCount() {
            int i = this.buttonsCount;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            return (chatAttachAlert.editingMessageObject != null || !(chatAttachAlert.baseFragment instanceof ChatActivity)) ? i : i + MediaDataController.getInstance(chatAttachAlert.currentAccount).inlineBots.size();
        }

        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (!(chatAttachAlert.baseFragment instanceof ChatActivity)) {
                int i = 0 + 1;
                this.buttonsCount = i;
                this.galleryButton = 0;
                this.buttonsCount = i + 1;
                this.documentButton = i;
            } else if (chatAttachAlert.editingMessageObject != null) {
                int i2 = 0 + 1;
                this.buttonsCount = i2;
                this.galleryButton = 0;
                int i3 = i2 + 1;
                this.buttonsCount = i3;
                this.documentButton = i2;
                this.buttonsCount = i3 + 1;
                this.musicButton = i3;
            } else {
                if (chatAttachAlert.mediaEnabled) {
                    int i4 = this.buttonsCount;
                    int i5 = i4 + 1;
                    this.buttonsCount = i5;
                    this.galleryButton = i4;
                    this.buttonsCount = i5 + 1;
                    this.documentButton = i5;
                }
                int i6 = this.buttonsCount;
                this.buttonsCount = i6 + 1;
                this.locationButton = i6;
                if (ChatAttachAlert.this.pollsEnabled) {
                    int i7 = this.buttonsCount;
                    this.buttonsCount = i7 + 1;
                    this.pollButton = i7;
                } else {
                    int i8 = this.buttonsCount;
                    this.buttonsCount = i8 + 1;
                    this.contactButton = i8;
                }
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i9 = this.buttonsCount;
                    this.buttonsCount = i9 + 1;
                    this.musicButton = i9;
                }
                BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                TLRPC$User currentUser = baseFragment instanceof ChatActivity ? ((ChatActivity) baseFragment).getCurrentUser() : null;
                if (currentUser != null && currentUser.bot) {
                    int i10 = this.buttonsCount;
                    this.buttonsCount = i10 + 1;
                    this.contactButton = i10;
                }
            }
            super.notifyDataSetChanged();
        }

        public int getItemViewType(int i) {
            return i < this.buttonsCount ? 0 : 1;
        }
    }

    public void dismissInternal() {
        this.delegate.doOnIdle(new Runnable() {
            public final void run() {
                ChatAttachAlert.this.removeFromRoot();
            }
        });
    }

    /* access modifiers changed from: private */
    public void removeFromRoot() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
        }
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        }
        this.contactsLayout = null;
        this.audioLayout = null;
        this.pollLayout = null;
        this.locationLayout = null;
        this.documentLayout = null;
        int i = 1;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[i] != null) {
                    attachAlertLayoutArr[i].onDestroy();
                    this.containerView.removeView(this.layouts[i]);
                    this.layouts[i] = null;
                }
                i++;
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

    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        this.currentAttachLayout.onDismissWithButtonClick(i);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return this.currentAttachLayout.canDismissWithTouchOutside();
    }

    public void dismiss() {
        if (!this.currentAttachLayout.onDismiss()) {
            int i = 0;
            while (true) {
                AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                if (i >= attachAlertLayoutArr.length) {
                    break;
                }
                if (!(attachAlertLayoutArr[i] == null || this.currentAttachLayout == attachAlertLayoutArr[i])) {
                    attachAlertLayoutArr[i].onDismiss();
                }
                i++;
            }
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
            }
            super.dismiss();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.currentAttachLayout.onSheetKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }
}
