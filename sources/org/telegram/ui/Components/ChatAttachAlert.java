package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIconColor;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messages_toggleBotInAttachMenu;
import org.telegram.tgnet.TLRPC$TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerSearchActivity;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    public final Property<AttachAlertLayout, Float> ATTACH_ALERT_LAYOUT_TRANSLATION;
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    protected ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    protected boolean allowOrder;
    protected boolean allowPassConfirmationAlert;
    private SpringAnimation appearSpringAnimation;
    private final Paint attachButtonPaint;
    private int attachItemSize;
    private ChatAttachAlertAudioLayout audioLayout;
    protected int avatarPicker;
    protected boolean avatarSearch;
    protected BaseFragment baseFragment;
    private float baseSelectedTextViewTranslationY;
    private LongSparseArray<ChatAttachAlertBotWebViewLayout> botAttachLayouts;
    private boolean botButtonProgressWasVisible;
    private boolean botButtonWasVisible;
    private float botMainButtonOffsetY;
    private TextView botMainButtonTextView;
    private RadialProgressView botProgressView;
    private float bottomPannelTranslation;
    private boolean buttonPressed;
    private ButtonsAdapter buttonsAdapter;
    private AnimatorSet buttonsAnimation;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    public boolean canOpenPreview;
    private float captionEditTextTopOffset;
    protected boolean captionLimitBulletinShown;
    private final NumberTextView captionLimitView;
    private float chatActivityEnterViewAnimateFromTop;
    private int codepointCount;
    protected EditTextEmoji commentTextView;
    private AnimatorSet commentsAnimator;
    private boolean confirmationAlertShown;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius;
    protected int currentAccount;
    private AttachAlertLayout currentAttachLayout;
    private int currentLimit;
    float currentPanTranslationY;
    protected ChatAttachViewDelegate delegate;
    private ChatAttachAlertDocumentLayout documentLayout;
    protected ActionBarMenuItem doneItem;
    protected MessageObject editingMessageObject;
    private boolean enterCommentEventSent;
    private ArrayList<android.graphics.Rect> exclusionRects;
    private android.graphics.Rect exclustionRect;
    private final boolean forceDarkTheme;
    private FrameLayout frameLayout2;
    private float fromScrollY;
    protected FrameLayout headerView;
    protected boolean inBubbleMode;
    private boolean isSoundPicker;
    private ActionBarMenuSubItem[] itemCells;
    private AttachAlertLayout[] layouts;
    private ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos;
    private boolean mediaEnabled;
    protected TextView mediaPreviewTextView;
    protected LinearLayout mediaPreviewView;
    private AnimatorSet menuAnimator;
    private boolean menuShowed;
    private AttachAlertLayout nextAttachLayout;
    private boolean openTransitionFinished;
    protected boolean openWithFrontFaceCamera;
    private Paint paint;
    public ChatActivity.ThemeDelegate parentThemeDelegate;
    protected boolean paused;
    private ChatAttachAlertPhotoLayout photoLayout;
    private ChatAttachAlertPhotoLayoutPreview photoPreviewLayout;
    private ChatAttachAlertPollLayout pollLayout;
    private boolean pollsEnabled;
    private int previousScrollOffsetY;
    private RectF rect;
    protected int[] scrollOffsetY;
    protected ActionBarMenuItem searchItem;
    protected ImageView selectedArrowImageView;
    private View selectedCountView;
    private long selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    protected LinearLayout selectedView;
    private ValueAnimator sendButtonColorAnimator;
    boolean sendButtonEnabled;
    private float sendButtonEnabledProgress;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private View shadow;
    protected SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private TextPaint textPaint;
    private float toScrollY;
    private ValueAnimator topBackgroundAnimator;
    public float translationProgress;
    protected boolean typeButtonsAvailable;
    private Object viewChangeAnimator;
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public static /* synthetic */ boolean lambda$new$10(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
    public boolean canDismiss() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void setCanOpenPreview(boolean z) {
        this.canOpenPreview = z;
        this.selectedArrowImageView.setVisibility((!z || this.avatarPicker == 2) ? 8 : 0);
    }

    public float getClipLayoutBottom() {
        return this.frameLayout2.getMeasuredHeight() - ((this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f)) * (1.0f - this.frameLayout2.getAlpha()));
    }

    public void showBotLayout(long j) {
        showBotLayout(j, null);
    }

    public void showBotLayout(long j, String str) {
        if ((this.botAttachLayouts.get(j) == null || !ObjectsCompat$$ExternalSyntheticBackport0.m(str, this.botAttachLayouts.get(j).getStartCommand()) || this.botAttachLayouts.get(j).needReload()) && (this.baseFragment instanceof ChatActivity)) {
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = new ChatAttachAlertBotWebViewLayout(this, getContext(), this.resourcesProvider);
            this.botAttachLayouts.put(j, chatAttachAlertBotWebViewLayout);
            this.botAttachLayouts.get(j).setDelegate(new AnonymousClass1(chatAttachAlertBotWebViewLayout));
            MessageObject replyingMessageObject = ((ChatActivity) this.baseFragment).getChatActivityEnterView().getReplyingMessageObject();
            this.botAttachLayouts.get(j).requestWebView(this.currentAccount, ((ChatActivity) this.baseFragment).getDialogId(), j, false, replyingMessageObject != null ? replyingMessageObject.messageOwner.id : 0, str);
        }
        if (this.botAttachLayouts.get(j) != null) {
            this.botAttachLayouts.get(j).disallowSwipeOffsetAnimation();
            showLayout(this.botAttachLayouts.get(j), -j);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$1 */
    /* loaded from: classes3.dex */
    public class AnonymousClass1 implements BotWebViewContainer.Delegate {
        private ValueAnimator botButtonAnimator;
        final /* synthetic */ ChatAttachAlertBotWebViewLayout val$webViewLayout;

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onSendWebViewData(String str) {
            BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass1(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout) {
            ChatAttachAlert.this = r1;
            this.val$webViewLayout = chatAttachAlertBotWebViewLayout;
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetupClosingBehavior(boolean z) {
            this.val$webViewLayout.setNeedCloseConfirmation(z);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onCloseRequested(final Runnable runnable) {
            if (ChatAttachAlert.this.currentAttachLayout != this.val$webViewLayout) {
                return;
            }
            ChatAttachAlert.this.setFocusable(false);
            ChatAttachAlert.this.getWindow().setSoftInputMode(48);
            ChatAttachAlert.this.dismiss();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.AnonymousClass1.lambda$onCloseRequested$0(runnable);
                }
            }, 150L);
        }

        public static /* synthetic */ void lambda$onCloseRequested$0(Runnable runnable) {
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetActionBarColor(String str) {
            final int color = ((ColorDrawable) ChatAttachAlert.this.actionBar.getBackground()).getColor();
            final int themedColor = ChatAttachAlert.this.getThemedColor(str);
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlert.AnonymousClass1.this.lambda$onWebAppSetActionBarColor$1(color, themedColor, valueAnimator);
                }
            });
            duration.start();
        }

        public /* synthetic */ void lambda$onWebAppSetActionBarColor$1(int i, int i2, ValueAnimator valueAnimator) {
            ChatAttachAlert.this.actionBar.setBackgroundColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetBackgroundColor(int i) {
            this.val$webViewLayout.setCustomBackground(i);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppOpenInvoice(final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            BaseFragment baseFragment = chatAttachAlert.baseFragment;
            if (tLObject instanceof TLRPC$TL_payments_paymentForm) {
                TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = (TLRPC$TL_payments_paymentForm) tLObject;
                MessagesController.getInstance(chatAttachAlert.currentAccount).putUsers(tLRPC$TL_payments_paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm, str, baseFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC$TL_payments_paymentReceipt ? new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                this.val$webViewLayout.scrollToTop();
                AndroidUtilities.hideKeyboard(this.val$webViewLayout);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(baseFragment.getParentActivity(), ((BottomSheet) ChatAttachAlert.this).resourcesProvider);
                overlayActionBarLayoutDialog.show();
                final ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda3
                    @Override // org.telegram.ui.PaymentFormActivity.PaymentFormCallback
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        ChatAttachAlert.AnonymousClass1.lambda$onWebAppOpenInvoice$2(OverlayActionBarLayoutDialog.this, chatAttachAlertBotWebViewLayout, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(((BottomSheet) ChatAttachAlert.this).resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        public static /* synthetic */ void lambda$onWebAppOpenInvoice$2(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            chatAttachAlertBotWebViewLayout.getWebViewContainer().onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppExpand() {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.canExpandByRequest()) {
                this.val$webViewLayout.scrollToTop();
            }
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetupMainButton(final boolean z, boolean z2, String str, int i, int i2, final boolean z3) {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout != chatAttachAlertBotWebViewLayout || !chatAttachAlertBotWebViewLayout.isBotButtonAvailable()) {
                return;
            }
            ChatAttachAlert.this.botMainButtonTextView.setClickable(z2);
            ChatAttachAlert.this.botMainButtonTextView.setText(str);
            ChatAttachAlert.this.botMainButtonTextView.setTextColor(i2);
            ChatAttachAlert.this.botMainButtonTextView.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
            float f = 0.0f;
            float f2 = 1.0f;
            if (ChatAttachAlert.this.botButtonWasVisible != z) {
                ChatAttachAlert.this.botButtonWasVisible = z;
                ValueAnimator valueAnimator = this.botButtonAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = z ? 0.0f : 1.0f;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(250L);
                this.botButtonAnimator = duration;
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatAttachAlert.AnonymousClass1.this.lambda$onSetupMainButton$3(valueAnimator2);
                    }
                });
                this.botButtonAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.1.1
                    {
                        AnonymousClass1.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        if (z) {
                            ChatAttachAlert.this.botMainButtonTextView.setAlpha(0.0f);
                            ChatAttachAlert.this.botMainButtonTextView.setVisibility(0);
                            int dp = AndroidUtilities.dp(36.0f);
                            for (int i3 = 0; i3 < ChatAttachAlert.this.botAttachLayouts.size(); i3++) {
                                ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i3)).setMeasureOffsetY(dp);
                            }
                            return;
                        }
                        ChatAttachAlert.this.buttonsRecyclerView.setAlpha(0.0f);
                        ChatAttachAlert.this.buttonsRecyclerView.setVisibility(0);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            ChatAttachAlert.this.botMainButtonTextView.setVisibility(8);
                        } else {
                            ChatAttachAlert.this.buttonsRecyclerView.setVisibility(8);
                        }
                        int dp = z ? AndroidUtilities.dp(36.0f) : 0;
                        for (int i3 = 0; i3 < ChatAttachAlert.this.botAttachLayouts.size(); i3++) {
                            ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i3)).setMeasureOffsetY(dp);
                        }
                        if (AnonymousClass1.this.botButtonAnimator == animator) {
                            AnonymousClass1.this.botButtonAnimator = null;
                        }
                    }
                });
                this.botButtonAnimator.start();
            }
            ChatAttachAlert.this.botProgressView.setProgressColor(i2);
            if (ChatAttachAlert.this.botButtonProgressWasVisible == z3) {
                return;
            }
            ChatAttachAlert.this.botProgressView.animate().cancel();
            if (z3) {
                ChatAttachAlert.this.botProgressView.setAlpha(0.0f);
                ChatAttachAlert.this.botProgressView.setVisibility(0);
            }
            ViewPropertyAnimator animate = ChatAttachAlert.this.botProgressView.animate();
            if (z3) {
                f = 1.0f;
            }
            ViewPropertyAnimator scaleX = animate.alpha(f).scaleX(z3 ? 1.0f : 0.1f);
            if (!z3) {
                f2 = 0.1f;
            }
            scaleX.scaleY(f2).setDuration(250L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.1.2
                {
                    AnonymousClass1.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.botButtonProgressWasVisible = z3;
                    if (!z3) {
                        ChatAttachAlert.this.botProgressView.setVisibility(8);
                    }
                }
            }).start();
        }

        public /* synthetic */ void lambda$onSetupMainButton$3(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.buttonsRecyclerView.setAlpha(1.0f - floatValue);
            ChatAttachAlert.this.botMainButtonTextView.setAlpha(floatValue);
            ChatAttachAlert.this.botMainButtonOffsetY = floatValue * AndroidUtilities.dp(36.0f);
            ChatAttachAlert.this.shadow.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.buttonsRecyclerView.setTranslationY(chatAttachAlert.botMainButtonOffsetY);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetBackButtonVisible(boolean z) {
            AndroidUtilities.updateImageViewImageAnimated(ChatAttachAlert.this.actionBar.getBackButton(), z ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
        }
    }

    public boolean checkCaption(CharSequence charSequence) {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            return ChatActivityEnterView.checkPremiumAnimatedEmoji(this.currentAccount, ((ChatActivity) baseFragment).getDialogId(), this.baseFragment, this.sizeNotifierFrameLayout, charSequence);
        }
        return false;
    }

    /* loaded from: classes3.dex */
    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3);

        void didSelectBot(TLRPC$User tLRPC$User);

        void doOnIdle(Runnable runnable);

        boolean needEnterComment();

        void onCameraOpened();

        void openAvatarsSearch();

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ChatAttachViewDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$didSelectBot(ChatAttachViewDelegate chatAttachViewDelegate, TLRPC$User tLRPC$User) {
            }

            public static boolean $default$needEnterComment(ChatAttachViewDelegate chatAttachViewDelegate) {
                return false;
            }

            public static void $default$openAvatarsSearch(ChatAttachViewDelegate chatAttachViewDelegate) {
            }

            public static void $default$doOnIdle(ChatAttachViewDelegate _this, Runnable runnable) {
                runnable.run();
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class AttachAlertLayout extends FrameLayout {
        protected ChatAttachAlert parentAlert;
        protected final Theme.ResourcesProvider resourcesProvider;

        void applyCaption(CharSequence charSequence) {
        }

        boolean canDismissWithTouchOutside() {
            return true;
        }

        boolean canScheduleMessages() {
            return true;
        }

        void checkColors() {
        }

        public int getCurrentItemTop() {
            return 0;
        }

        int getCustomBackground() {
            return 0;
        }

        int getFirstOffset() {
            return 0;
        }

        public int getListTopPadding() {
            return 0;
        }

        int getSelectedItemsCount() {
            return 0;
        }

        ArrayList<ThemeDescription> getThemeDescriptions() {
            return null;
        }

        boolean hasCustomBackground() {
            return false;
        }

        int needsActionBar() {
            return 0;
        }

        public boolean onBackPressed() {
            return false;
        }

        void onButtonsTranslationYUpdated() {
        }

        public void onContainerTranslationUpdated(float f) {
        }

        boolean onContainerViewTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        void onDestroy() {
        }

        public boolean onDismiss() {
            return false;
        }

        void onDismissWithButtonClick(int i) {
        }

        boolean onDismissWithTouchOutside() {
            return true;
        }

        public void onHidden() {
        }

        public void onHide() {
        }

        void onHideShowProgress(float f) {
        }

        void onMenuItemClick(int i) {
        }

        void onOpenAnimationEnd() {
        }

        public void onPanTransitionEnd() {
        }

        public void onPanTransitionStart(boolean z, int i) {
        }

        void onPause() {
        }

        void onPreMeasure(int i, int i2) {
        }

        public void onResume() {
        }

        void onSelectedItemsCountChanged(int i) {
        }

        boolean onSheetKeyDown(int i, KeyEvent keyEvent) {
            return false;
        }

        void onShow(AttachAlertLayout attachAlertLayout) {
        }

        void onShown() {
        }

        void scrollToTop() {
        }

        void sendSelectedItems(boolean z, int i) {
        }

        boolean shouldHideBottomButtons() {
            return true;
        }

        public AttachAlertLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            this.parentAlert = chatAttachAlert;
        }

        int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12.0f : 17.0f);
        }

        public int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    /* loaded from: classes3.dex */
    public class AttachButton extends FrameLayout {
        private String backgroundKey;
        private Animator checkAnimator;
        private boolean checked;
        private float checkedState;
        private int currentId;
        private RLottieImageView imageView;
        private String textKey;
        private TextView textView;

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AttachButton(Context context) {
            super(context);
            ChatAttachAlert.this = r10;
            setWillNotDraw(false);
            setFocusable(true);
            RLottieImageView rLottieImageView = new RLottieImageView(context, r10) { // from class: org.telegram.ui.Components.ChatAttachAlert.AttachButton.1
                {
                    AttachButton.this = this;
                }

                @Override // android.view.View
                public void setScaleX(float f) {
                    super.setScaleX(f);
                    AttachButton.this.invalidate();
                }
            };
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(32, 32.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(r10.getThemedColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing(-AndroidUtilities.dp(2.0f), 1.0f);
            this.textView.setImportantForAccessibility(2);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(this.textView.getText());
            accessibilityNodeInfo.setEnabled(true);
            accessibilityNodeInfo.setSelected(this.checked);
        }

        void updateCheckedState(boolean z) {
            if (this.checked == (((long) this.currentId) == ChatAttachAlert.this.selectedId)) {
                return;
            }
            this.checked = ((long) this.currentId) == ChatAttachAlert.this.selectedId;
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
                ofFloat.setDuration(200L);
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

        @Keep
        public void setCheckedState(float f) {
            this.checkedState = f;
            float f2 = 1.0f - (f * 0.06f);
            this.imageView.setScaleX(f2);
            this.imageView.setScaleY(f2);
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
            invalidate();
        }

        @Keep
        public float getCheckedState() {
            return this.checkedState;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), NUM));
        }

        public void setTextAndIcon(int i, CharSequence charSequence, RLottieDrawable rLottieDrawable, String str, String str2) {
            this.currentId = i;
            this.textView.setText(charSequence);
            this.imageView.setAnimation(rLottieDrawable);
            this.backgroundKey = str;
            this.textKey = str2;
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
            float dp = AndroidUtilities.dp(23.0f) * scaleX;
            float left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f);
            float top = this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2.0f);
            ChatAttachAlert.this.attachButtonPaint.setColor(ChatAttachAlert.this.getThemedColor(this.backgroundKey));
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
            ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3.0f) * scaleX);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(left, top, dp - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, top, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
        }
    }

    /* loaded from: classes3.dex */
    public class AttachBotButton extends FrameLayout {
        private TLRPC$TL_attachMenuBot attachMenuBot;
        private AvatarDrawable avatarDrawable;
        private ValueAnimator checkAnimator;
        private Boolean checked;
        private float checkedState;
        private TLRPC$User currentUser;
        private int iconBackgroundColor;
        private BackupImageView imageView;
        private TextView nameTextView;
        private View selector;
        private int textColor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AttachBotButton(Context context) {
            super(context);
            ChatAttachAlert.this = r10;
            this.avatarDrawable = new AvatarDrawable();
            setWillNotDraw(false);
            setFocusable(true);
            setFocusableInTouchMode(true);
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(context, r10);
            this.imageView = anonymousClass1;
            anonymousClass1.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View view = new View(context);
                this.selector = view;
                view.setBackground(Theme.createSelectorDrawable(r10.getThemedColor("dialogButtonSelector"), 1, AndroidUtilities.dp(23.0f)));
                addView(this.selector, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
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

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$1 */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 extends BackupImageView {
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(Context context, ChatAttachAlert chatAttachAlert) {
                super(context);
                AttachBotButton.this = r1;
                this.imageReceiver.setDelegate(ChatAttachAlert$AttachBotButton$1$$ExternalSyntheticLambda0.INSTANCE);
            }

            public static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                Drawable drawable = imageReceiver.getDrawable();
                if (drawable instanceof RLottieDrawable) {
                    RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                    rLottieDrawable.setCustomEndFrame(0);
                    rLottieDrawable.stop();
                    rLottieDrawable.setProgress(0.0f, false);
                }
            }

            @Override // android.view.View
            public void setScaleX(float f) {
                super.setScaleX(f);
                AttachBotButton.this.invalidate();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.selector == null || !this.checked.booleanValue()) {
                return;
            }
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setCheckedState(float f) {
            this.checkedState = f;
            float f2 = 1.0f - (f * 0.06f);
            this.imageView.setScaleX(f2);
            this.imageView.setScaleY(f2);
            this.nameTextView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), this.textColor, this.checkedState));
            invalidate();
        }

        private void updateMargins() {
            ((ViewGroup.MarginLayoutParams) this.nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 62.0f : 60.0f);
            ((ViewGroup.MarginLayoutParams) this.imageView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 11.0f : 9.0f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.attachMenuBot != null) {
                float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
                float dp = AndroidUtilities.dp(23.0f) * scaleX;
                float left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f);
                float top = this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2.0f);
                ChatAttachAlert.this.attachButtonPaint.setColor(this.iconBackgroundColor);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
                ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3.0f) * scaleX);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(left, top, dp - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(left, top, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        void updateCheckedState(boolean z) {
            boolean z2 = this.attachMenuBot != null && (-this.currentUser.id) == ChatAttachAlert.this.selectedId;
            Boolean bool = this.checked;
            if (bool == null || bool.booleanValue() != z2 || !z) {
                this.checked = Boolean.valueOf(z2);
                ValueAnimator valueAnimator = this.checkAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                RLottieDrawable lottieAnimation = this.imageView.getImageReceiver().getLottieAnimation();
                float f = 1.0f;
                if (z) {
                    if (this.checked.booleanValue() && lottieAnimation != null) {
                        lottieAnimation.setAutoRepeat(0);
                        lottieAnimation.setCustomEndFrame(-1);
                        lottieAnimation.setProgress(0.0f, false);
                        lottieAnimation.start();
                    }
                    float[] fArr = new float[2];
                    fArr[0] = this.checked.booleanValue() ? 0.0f : 1.0f;
                    if (!this.checked.booleanValue()) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.checkAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            ChatAttachAlert.AttachBotButton.this.lambda$updateCheckedState$0(valueAnimator2);
                        }
                    });
                    this.checkAnimator.setDuration(200L);
                    this.checkAnimator.start();
                    return;
                }
                if (lottieAnimation != null) {
                    lottieAnimation.stop();
                    lottieAnimation.setProgress(0.0f, false);
                }
                if (!this.checked.booleanValue()) {
                    f = 0.0f;
                }
                setCheckedState(f);
            }
        }

        public /* synthetic */ void lambda$updateCheckedState$0(ValueAnimator valueAnimator) {
            setCheckedState(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        public void setUser(TLRPC$User tLRPC$User) {
            if (tLRPC$User == null) {
                return;
            }
            this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
            this.currentUser = tLRPC$User;
            this.nameTextView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
            this.avatarDrawable.setInfo(tLRPC$User);
            this.imageView.setForUserOrChat(tLRPC$User, this.avatarDrawable);
            this.imageView.setSize(-1, -1);
            this.imageView.setColorFilter(null);
            this.attachMenuBot = null;
            this.selector.setVisibility(0);
            updateMargins();
            setCheckedState(0.0f);
            invalidate();
        }

        public void setAttachBot(TLRPC$User tLRPC$User, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
            boolean z;
            if (tLRPC$User == null || tLRPC$TL_attachMenuBot == null) {
                return;
            }
            this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
            this.currentUser = tLRPC$User;
            this.nameTextView.setText(tLRPC$TL_attachMenuBot.short_name);
            this.avatarDrawable.setInfo(tLRPC$User);
            TLRPC$TL_attachMenuBotIcon animatedAttachMenuBotIcon = MediaDataController.getAnimatedAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
            if (animatedAttachMenuBotIcon == null) {
                animatedAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
                z = false;
            } else {
                z = true;
            }
            if (animatedAttachMenuBotIcon != null) {
                this.textColor = ChatAttachAlert.this.getThemedColor("chat_attachContactText");
                this.iconBackgroundColor = ChatAttachAlert.this.getThemedColor("chat_attachContactBackground");
                Iterator<TLRPC$TL_attachMenuBotIconColor> it = animatedAttachMenuBotIcon.colors.iterator();
                while (it.hasNext()) {
                    TLRPC$TL_attachMenuBotIconColor next = it.next();
                    String str = next.name;
                    str.hashCode();
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -1852424286:
                            if (str.equals("dark_icon")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -1852094378:
                            if (str.equals("dark_text")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -208896510:
                            if (str.equals("light_icon")) {
                                c = 2;
                                break;
                            }
                            break;
                        case -208566602:
                            if (str.equals("light_text")) {
                                c = 3;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (!Theme.getCurrentTheme().isDark()) {
                                break;
                            } else {
                                this.iconBackgroundColor = next.color;
                                break;
                            }
                        case 1:
                            if (!Theme.getCurrentTheme().isDark()) {
                                break;
                            } else {
                                this.textColor = next.color;
                                break;
                            }
                        case 2:
                            if (Theme.getCurrentTheme().isDark()) {
                                break;
                            } else {
                                this.iconBackgroundColor = next.color;
                                break;
                            }
                        case 3:
                            if (Theme.getCurrentTheme().isDark()) {
                                break;
                            } else {
                                this.textColor = next.color;
                                break;
                            }
                    }
                }
                this.textColor = ColorUtils.setAlphaComponent(this.textColor, 255);
                this.iconBackgroundColor = ColorUtils.setAlphaComponent(this.iconBackgroundColor, 255);
                TLRPC$Document tLRPC$Document = animatedAttachMenuBotIcon.icon;
                this.imageView.getImageReceiver().setAllowStartLottieAnimation(false);
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), String.valueOf(tLRPC$TL_attachMenuBot.bot_id), z ? "tgs" : "svg", DocumentObject.getSvgThumb(tLRPC$Document, "windowBackgroundGray", 1.0f), tLRPC$TL_attachMenuBot);
            }
            this.imageView.setSize(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f));
            this.imageView.setColorFilter(new PorterDuffColorFilter(ChatAttachAlert.this.getThemedColor("chat_attachContactIcon"), PorterDuff.Mode.SRC_IN));
            this.attachMenuBot = tLRPC$TL_attachMenuBot;
            this.selector.setVisibility(8);
            updateMargins();
            setCheckedState(0.0f);
            invalidate();
        }
    }

    public ChatAttachAlert(Context context, BaseFragment baseFragment, boolean z, boolean z2) {
        this(context, baseFragment, z, z2, null);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public ChatAttachAlert(Context context, final BaseFragment baseFragment, boolean z, final boolean z2, final Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.canOpenPreview = false;
        this.isSoundPicker = false;
        this.translationProgress = 0.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION = new AnimationProperties.FloatProperty<AttachAlertLayout>("translation") { // from class: org.telegram.ui.Components.ChatAttachAlert.2
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(AttachAlertLayout attachAlertLayout, float f) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.translationProgress = f;
                if ((chatAttachAlert.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                    int max = Math.max(ChatAttachAlert.this.nextAttachLayout.getWidth(), ChatAttachAlert.this.currentAttachLayout.getWidth());
                    if (ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) {
                        ChatAttachAlert.this.currentAttachLayout.setTranslationX((-max) * f);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((1.0f - f) * max);
                    } else {
                        ChatAttachAlert.this.currentAttachLayout.setTranslationX(max * f);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((-max) * (1.0f - f));
                    }
                } else {
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
                        ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                        chatAttachAlert2.updateSelectedPosition(chatAttachAlert2.nextAttachLayout == ChatAttachAlert.this.pollLayout ? 1 : 0);
                    }
                    ChatAttachAlert.this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f) * f);
                    ChatAttachAlert.this.currentAttachLayout.onHideShowProgress(1.0f - Math.min(1.0f, f / 0.7f));
                    ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                }
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }

            @Override // android.util.Property
            public Float get(AttachAlertLayout attachAlertLayout) {
                return Float.valueOf(ChatAttachAlert.this.translationProgress);
            }
        };
        this.layouts = new AttachAlertLayout[7];
        this.botAttachLayouts = new LongSparseArray<>();
        this.textPaint = new TextPaint(1);
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.sendButtonEnabled = true;
        this.sendButtonEnabledProgress = 1.0f;
        this.cornerRadius = 1.0f;
        this.botButtonProgressWasVisible = false;
        this.botButtonWasVisible = false;
        this.currentAccount = UserConfig.selectedAccount;
        this.mediaEnabled = true;
        this.pollsEnabled = true;
        this.maxSelectedPhotos = -1;
        this.allowOrder = true;
        this.attachItemSize = AndroidUtilities.dp(85.0f);
        new DecelerateInterpolator();
        this.scrollOffsetY = new int[2];
        this.attachButtonPaint = new Paint(1);
        this.captionLimitBulletinShown = false;
        this.exclusionRects = new ArrayList<>();
        this.exclustionRect = new android.graphics.Rect();
        this.ATTACH_ALERT_PROGRESS = new AnimationProperties.FloatProperty<ChatAttachAlert>("openProgress") { // from class: org.telegram.ui.Components.ChatAttachAlert.20
            private float openProgress;

            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(ChatAttachAlert chatAttachAlert, float f) {
                float f2;
                int childCount = ChatAttachAlert.this.buttonsRecyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    float f3 = (3 - i) * 32.0f;
                    View childAt = ChatAttachAlert.this.buttonsRecyclerView.getChildAt(i);
                    if (f > f3) {
                        float f4 = f - f3;
                        f2 = 1.0f;
                        if (f4 <= 200.0f) {
                            float f5 = f4 / 200.0f;
                            f2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(f5) * 1.1f;
                            childAt.setAlpha(CubicBezierInterpolator.EASE_BOTH.getInterpolation(f5));
                        } else {
                            childAt.setAlpha(1.0f);
                            float f6 = f4 - 200.0f;
                            if (f6 <= 100.0f) {
                                f2 = 1.1f - (CubicBezierInterpolator.EASE_IN.getInterpolation(f6 / 100.0f) * 0.1f);
                            }
                        }
                    } else {
                        f2 = 0.0f;
                    }
                    if (childAt instanceof AttachButton) {
                        AttachButton attachButton = (AttachButton) childAt;
                        attachButton.textView.setScaleX(f2);
                        attachButton.textView.setScaleY(f2);
                        attachButton.imageView.setScaleX(f2);
                        attachButton.imageView.setScaleY(f2);
                    } else if (childAt instanceof AttachBotButton) {
                        AttachBotButton attachBotButton = (AttachBotButton) childAt;
                        attachBotButton.nameTextView.setScaleX(f2);
                        attachBotButton.nameTextView.setScaleY(f2);
                        attachBotButton.imageView.setScaleX(f2);
                        attachBotButton.imageView.setScaleY(f2);
                    }
                }
            }

            @Override // android.util.Property
            public Float get(ChatAttachAlert chatAttachAlert) {
                return Float.valueOf(this.openProgress);
            }
        };
        this.confirmationAlertShown = false;
        this.allowPassConfirmationAlert = false;
        this.forceDarkTheme = z;
        this.drawNavigationBar = true;
        this.inBubbleMode = (baseFragment instanceof ChatActivity) && baseFragment.isInBubbleMode();
        this.openInterpolator = new OvershootInterpolator(0.7f);
        this.baseFragment = baseFragment;
        this.useSmoothKeyboard = true;
        setDelegate(this);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        this.exclusionRects.add(this.exclustionRect);
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(context, z);
        this.sizeNotifierFrameLayout = anonymousClass3;
        anonymousClass3.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.4
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
            public void onSizeChanged(int i, boolean z3) {
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout) {
                    ChatAttachAlert.this.currentAttachLayout.invalidate();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierFrameLayout;
        this.containerView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        ActionBar actionBar = new ActionBar(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlert.5
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                float alpha = getAlpha();
                super.setAlpha(f);
                if (alpha != f) {
                    ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
                    if (ChatAttachAlert.this.frameLayout2 == null) {
                        return;
                    }
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (chatAttachAlert.buttonsRecyclerView == null) {
                        return;
                    }
                    float f2 = 1.0f;
                    if (chatAttachAlert.frameLayout2.getTag() == null) {
                        if (ChatAttachAlert.this.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                            float f3 = 1.0f - f;
                            ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f3);
                            ChatAttachAlert.this.shadow.setAlpha(f3);
                            ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(AndroidUtilities.dp(44.0f) * f);
                        }
                        ChatAttachAlert.this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f) * f);
                        ChatAttachAlert.this.shadow.setTranslationY((AndroidUtilities.dp(84.0f) * f) + ChatAttachAlert.this.botMainButtonOffsetY);
                    } else if (ChatAttachAlert.this.currentAttachLayout != null) {
                    } else {
                        if (f != 0.0f) {
                            f2 = 0.0f;
                        }
                        if (ChatAttachAlert.this.buttonsRecyclerView.getAlpha() == f2) {
                            return;
                        }
                        ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f2);
                    }
                }
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(getThemedColor("dialogBackground"));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(getThemedColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(getThemedColor("dialogTextBlack"));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.ChatAttachAlert.6
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    if (ChatAttachAlert.this.currentAttachLayout.onBackPressed()) {
                        return;
                    }
                    ChatAttachAlert.this.dismiss();
                    return;
                }
                ChatAttachAlert.this.currentAttachLayout.onMenuItemClick(i2);
            }
        });
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, getThemedColor("dialogTextBlack"), false, resourcesProvider);
        this.selectedMenuItem = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.selectedMenuItem.setIcon(R.drawable.ic_ab_other);
        this.selectedMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.selectedMenuItem.setVisibility(4);
        this.selectedMenuItem.setAlpha(0.0f);
        this.selectedMenuItem.setSubMenuOpenSide(2);
        this.selectedMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda28
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i2) {
                ChatAttachAlert.this.lambda$new$0(i2);
            }
        });
        this.selectedMenuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
        this.selectedMenuItem.setTranslationX(AndroidUtilities.dp(6.0f));
        this.selectedMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("dialogButtonSelector"), 6));
        this.selectedMenuItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$1(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, null, 0, getThemedColor("windowBackgroundWhiteBlueHeader"), true, resourcesProvider);
        this.doneItem = actionBarMenuItem2;
        actionBarMenuItem2.setLongClickEnabled(false);
        this.doneItem.setText(LocaleController.getString("Create", R.string.Create).toUpperCase());
        this.doneItem.setVisibility(4);
        this.doneItem.setAlpha(0.0f);
        this.doneItem.setTranslationX(-AndroidUtilities.dp(12.0f));
        this.doneItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("dialogButtonSelector"), 3));
        this.doneItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$2(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, null, 0, getThemedColor("dialogTextBlack"), false, resourcesProvider);
        this.searchItem = actionBarMenuItem3;
        actionBarMenuItem3.setLongClickEnabled(false);
        this.searchItem.setIcon(R.drawable.ic_ab_search);
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setVisibility(4);
        this.searchItem.setAlpha(0.0f);
        this.searchItem.setTranslationX(-AndroidUtilities.dp(42.0f));
        this.searchItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("dialogButtonSelector"), 6));
        this.searchItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$3(z2, view);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.8
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                ChatAttachAlert.this.updateSelectedPosition(0);
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.headerView = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$4(view);
            }
        });
        this.headerView.setAlpha(0.0f);
        this.headerView.setVisibility(4);
        LinearLayout linearLayout = new LinearLayout(context);
        this.selectedView = linearLayout;
        linearLayout.setOrientation(0);
        this.selectedView.setGravity(16);
        TextView textView = new TextView(context);
        this.selectedTextView = textView;
        textView.setTextColor(getThemedColor("dialogTextBlack"));
        this.selectedTextView.setTextSize(1, 16.0f);
        this.selectedTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedTextView.setGravity(19);
        this.selectedView.addView(this.selectedTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.selectedArrowImageView = new ImageView(context);
        Drawable mutate = getContext().getResources().getDrawable(R.drawable.attach_arrow_right).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
        this.selectedArrowImageView.setImageDrawable(mutate);
        this.selectedArrowImageView.setVisibility(8);
        this.selectedView.addView(this.selectedArrowImageView, LayoutHelper.createLinear(-2, -2, 16, 4, 1, 0, 0));
        this.selectedView.setAlpha(1.0f);
        this.headerView.addView(this.selectedView, LayoutHelper.createFrame(-2, -1.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.mediaPreviewView = linearLayout2;
        linearLayout2.setOrientation(0);
        this.mediaPreviewView.setGravity(16);
        ImageView imageView = new ImageView(context);
        Drawable mutate2 = getContext().getResources().getDrawable(R.drawable.attach_arrow_left).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
        imageView.setImageDrawable(mutate2);
        this.mediaPreviewView.addView(imageView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 4, 0));
        TextView textView2 = new TextView(context);
        this.mediaPreviewTextView = textView2;
        textView2.setTextColor(getThemedColor("dialogTextBlack"));
        this.mediaPreviewTextView.setTextSize(1, 16.0f);
        this.mediaPreviewTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.mediaPreviewTextView.setGravity(19);
        this.mediaPreviewTextView.setText(LocaleController.getString("AttachMediaPreview", R.string.AttachMediaPreview));
        this.mediaPreviewView.setAlpha(0.0f);
        this.mediaPreviewView.addView(this.mediaPreviewTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.headerView.addView(this.mediaPreviewView, LayoutHelper.createFrame(-2, -1.0f));
        AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = new ChatAttachAlertPhotoLayout(this, context, z, resourcesProvider);
        this.photoLayout = chatAttachAlertPhotoLayout;
        attachAlertLayoutArr[0] = chatAttachAlertPhotoLayout;
        chatAttachAlertPhotoLayout.setTranslationX(0.0f);
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = this.photoLayout;
        this.currentAttachLayout = chatAttachAlertPhotoLayout2;
        this.selectedId = 1L;
        this.containerView.addView(chatAttachAlertPhotoLayout2, LayoutHelper.createFrame(-1, -1.0f));
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, -2.0f, 51, 23.0f, 0.0f, 48.0f, 0.0f));
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.containerView.addView(this.selectedMenuItem, LayoutHelper.createFrame(48, 48, 53));
        this.containerView.addView(this.searchItem, LayoutHelper.createFrame(48, 48, 53));
        this.containerView.addView(this.doneItem, LayoutHelper.createFrame(-2, 48, 53));
        View view = new View(context);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        View view2 = new View(context);
        this.shadow = view2;
        view2.setBackgroundResource(R.drawable.attach_shadow);
        this.shadow.getBackground().setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 84.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.9
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                ChatAttachAlert.this.currentAttachLayout.onButtonsTranslationYUpdated();
            }
        };
        this.buttonsRecyclerView = recyclerListView;
        ButtonsAdapter buttonsAdapter = new ButtonsAdapter(context);
        this.buttonsAdapter = buttonsAdapter;
        recyclerListView.setAdapter(buttonsAdapter);
        RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false);
        this.buttonsLayoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.buttonsRecyclerView.setVerticalScrollBarEnabled(false);
        this.buttonsRecyclerView.setHorizontalScrollBarEnabled(false);
        this.buttonsRecyclerView.setItemAnimator(null);
        this.buttonsRecyclerView.setLayoutAnimation(null);
        this.buttonsRecyclerView.setGlowColor(getThemedColor("dialogScrollGlow"));
        this.buttonsRecyclerView.setBackgroundColor(getThemedColor("dialogBackground"));
        this.buttonsRecyclerView.setImportantForAccessibility(1);
        this.containerView.addView(this.buttonsRecyclerView, LayoutHelper.createFrame(-1, 84, 83));
        this.buttonsRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view3, int i2) {
                ChatAttachAlert.this.lambda$new$7(resourcesProvider, view3, i2);
            }
        });
        this.buttonsRecyclerView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda37
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view3, int i2) {
                boolean lambda$new$8;
                lambda$new$8 = ChatAttachAlert.this.lambda$new$8(view3, i2);
                return lambda$new$8;
            }
        });
        TextView textView3 = new TextView(context);
        this.botMainButtonTextView = textView3;
        textView3.setVisibility(8);
        this.botMainButtonTextView.setAlpha(0.0f);
        this.botMainButtonTextView.setSingleLine();
        this.botMainButtonTextView.setGravity(17);
        this.botMainButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int dp = AndroidUtilities.dp(16.0f);
        this.botMainButtonTextView.setPadding(dp, 0, dp, 0);
        this.botMainButtonTextView.setTextSize(1, 14.0f);
        this.botMainButtonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatAttachAlert.this.lambda$new$9(view3);
            }
        });
        this.containerView.addView(this.botMainButtonTextView, LayoutHelper.createFrame(-1, 48, 83));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.botProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.botProgressView.setAlpha(0.0f);
        this.botProgressView.setScaleX(0.1f);
        this.botProgressView.setScaleY(0.1f);
        this.botProgressView.setVisibility(8);
        this.containerView.addView(this.botProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        AnonymousClass10 anonymousClass10 = new AnonymousClass10(context, z);
        this.frameLayout2 = anonymousClass10;
        anonymousClass10.setWillNotDraw(false);
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setAlpha(0.0f);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2, 83));
        this.frameLayout2.setOnTouchListener(ChatAttachAlert$$ExternalSyntheticLambda14.INSTANCE);
        NumberTextView numberTextView = new NumberTextView(context);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        numberTextView.setTextSize(15);
        numberTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText"));
        numberTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        numberTextView.setCenterAlign(true);
        this.frameLayout2.addView(numberTextView, LayoutHelper.createFrame(56, 20.0f, 85, 3.0f, 0.0f, 14.0f, 78.0f));
        this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        AnonymousClass11 anonymousClass11 = new AnonymousClass11(context, this.sizeNotifierFrameLayout, null, 1, true, resourcesProvider);
        this.commentTextView = anonymousClass11;
        anonymousClass11.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.commentTextView.onResume();
        this.commentTextView.getEditText().addTextChangedListener(new AnonymousClass12(baseFragment));
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 84.0f, 0.0f));
        this.frameLayout2.setClipChildren(false);
        this.commentTextView.setClipChildren(false);
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.13
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout) {
                    accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendPhotos", ChatAttachAlert.this.photoLayout.getSelectedItemsCount(), new Object[0]));
                } else if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.documentLayout) {
                    accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendFiles", ChatAttachAlert.this.documentLayout.getSelectedItemsCount(), new Object[0]));
                } else if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.audioLayout) {
                    accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendAudio", ChatAttachAlert.this.audioLayout.getSelectedItemsCount(), new Object[0]));
                }
                accessibilityNodeInfo.setClassName(Button.class.getName());
                accessibilityNodeInfo.setLongClickable(true);
                accessibilityNodeInfo.setClickable(true);
            }
        };
        this.writeButtonContainer = frameLayout2;
        frameLayout2.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        this.writeButton = new ImageView(context);
        int dp2 = AndroidUtilities.dp(56.0f);
        int themedColor = getThemedColor("dialogFloatingButton");
        int i2 = Build.VERSION.SDK_INT;
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(dp2, themedColor, getThemedColor(i2 >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
        if (i2 < 21) {
            Drawable mutate3 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate3, this.writeButtonDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButtonDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
        this.writeButton.setImageResource(R.drawable.attach_send);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setImportantForAccessibility(2);
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (i2 >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.ChatAttachAlert.14
                @Override // android.view.ViewOutlineProvider
                @SuppressLint({"NewApi"})
                public void getOutline(View view3, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(i2 >= 21 ? 56 : 60, i2 >= 21 ? 56.0f : 60.0f, 51, i2 >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatAttachAlert.this.lambda$new$12(baseFragment, resourcesProvider, view3);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view3) {
                boolean lambda$new$16;
                lambda$new$16 = ChatAttachAlert.this.lambda$new$16(resourcesProvider, view3);
                return lambda$new$16;
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        View view3 = new View(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.16
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int ceil;
                int i3;
                int i4;
                String format = String.format("%d", Integer.valueOf(Math.max(1, ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount())));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(ChatAttachAlert.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int themedColor2 = ChatAttachAlert.this.getThemedColor("dialogRoundCheckBoxCheck");
                TextPaint textPaint = ChatAttachAlert.this.textPaint;
                double alpha = Color.alpha(themedColor2);
                double d = ChatAttachAlert.this.sendButtonEnabledProgress;
                Double.isNaN(d);
                Double.isNaN(alpha);
                textPaint.setColor(ColorUtils.setAlphaComponent(themedColor2, (int) (alpha * ((d * 0.42d) + 0.58d))));
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor("dialogBackground"));
                int i5 = max / 2;
                ChatAttachAlert.this.rect.set(measuredWidth - i5, 0.0f, i5 + measuredWidth, getMeasuredHeight());
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ChatAttachAlert.this.paint);
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor("dialogRoundCheckBox"));
                ChatAttachAlert.this.rect.set(i3 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), i4 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ChatAttachAlert.this.paint);
                canvas.drawText(format, measuredWidth - (ceil / 2), AndroidUtilities.dp(16.2f), ChatAttachAlert.this.textPaint);
            }
        };
        this.selectedCountView = view3;
        view3.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.containerView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        if (z) {
            checkColors();
            this.navBarColorKey = null;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$3 */
    /* loaded from: classes3.dex */
    public class AnonymousClass3 extends SizeNotifierFrameLayout {
        AdjustPanLayoutHelper adjustPanLayoutHelper;
        private Bulletin.Delegate bulletinDelegate;
        private boolean ignoreLayout;
        private float initialTranslationY;
        private int lastNotifyWidth;
        private RectF rect;
        final /* synthetic */ boolean val$forceDarkTheme;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(Context context, boolean z) {
            super(context);
            ChatAttachAlert.this = r1;
            this.val$forceDarkTheme = z;
            this.bulletinDelegate = new Bulletin.Delegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.3.1
                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ int getTopOffset(int i) {
                    return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onBottomOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }

                {
                    AnonymousClass3.this = this;
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public int getBottomOffset(int i) {
                    return AnonymousClass3.this.getHeight() - ChatAttachAlert.this.frameLayout2.getTop();
                }
            };
            this.rect = new RectF();
            this.adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) { // from class: org.telegram.ui.Components.ChatAttachAlert.3.2
                {
                    AnonymousClass3.this = this;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                /* JADX WARN: Removed duplicated region for block: B:32:0x005d  */
                /* JADX WARN: Removed duplicated region for block: B:33:0x0074  */
                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public void onTransitionStart(boolean r5, int r6) {
                    /*
                        r4 = this;
                        super.onTransitionStart(r5, r6)
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        int r0 = org.telegram.ui.Components.ChatAttachAlert.access$3400(r0)
                        r1 = 0
                        if (r0 <= 0) goto L37
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        int r0 = org.telegram.ui.Components.ChatAttachAlert.access$3400(r0)
                        org.telegram.ui.Components.ChatAttachAlert$3 r2 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r2 = org.telegram.ui.Components.ChatAttachAlert.this
                        int[] r3 = r2.scrollOffsetY
                        r3 = r3[r1]
                        if (r0 == r3) goto L37
                        if (r5 == 0) goto L37
                        int r0 = org.telegram.ui.Components.ChatAttachAlert.access$3400(r2)
                        float r0 = (float) r0
                        org.telegram.ui.Components.ChatAttachAlert.access$3502(r2, r0)
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        int[] r2 = r0.scrollOffsetY
                        r2 = r2[r1]
                        float r2 = (float) r2
                        org.telegram.ui.Components.ChatAttachAlert.access$3602(r0, r2)
                        goto L40
                    L37:
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        r2 = -1082130432(0xffffffffbvar_, float:-1.0)
                        org.telegram.ui.Components.ChatAttachAlert.access$3502(r0, r2)
                    L40:
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        r0.invalidate()
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r0 = org.telegram.ui.Components.ChatAttachAlert.access$000(r0)
                        boolean r0 = r0 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout
                        if (r0 == 0) goto L88
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        boolean r0 = org.telegram.ui.Components.ChatAttachAlert.access$500(r0)
                        if (r0 != 0) goto L88
                        if (r5 == 0) goto L74
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        android.view.View r0 = org.telegram.ui.Components.ChatAttachAlert.access$1100(r0)
                        r1 = 8
                        r0.setVisibility(r1)
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        org.telegram.ui.Components.RecyclerListView r0 = r0.buttonsRecyclerView
                        r0.setVisibility(r1)
                        goto L88
                    L74:
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        android.view.View r0 = org.telegram.ui.Components.ChatAttachAlert.access$1100(r0)
                        r0.setVisibility(r1)
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        org.telegram.ui.Components.RecyclerListView r0 = r0.buttonsRecyclerView
                        r0.setVisibility(r1)
                    L88:
                        org.telegram.ui.Components.ChatAttachAlert$3 r0 = org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.this
                        org.telegram.ui.Components.ChatAttachAlert r0 = org.telegram.ui.Components.ChatAttachAlert.this
                        org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r0 = org.telegram.ui.Components.ChatAttachAlert.access$000(r0)
                        r0.onPanTransitionStart(r5, r6)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.AnonymousClass2.onTransitionStart(boolean, int):void");
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                public void onTransitionEnd() {
                    super.onTransitionEnd();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, false, 0);
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    chatAttachAlert2.previousScrollOffsetY = chatAttachAlert2.scrollOffsetY[0];
                    ChatAttachAlert.this.currentAttachLayout.onPanTransitionEnd();
                    if (!(ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertBotWebViewLayout) || ChatAttachAlert.this.botButtonWasVisible) {
                        return;
                    }
                    int dp = ((BottomSheet) ChatAttachAlert.this).keyboardVisible ? AndroidUtilities.dp(84.0f) : 0;
                    for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                        ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                    }
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                public void onPanTranslationUpdate(float f, float f2, boolean z2) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.currentPanTranslationY = f;
                    if (chatAttachAlert.fromScrollY > 0.0f) {
                        ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                        chatAttachAlert2.currentPanTranslationY += (chatAttachAlert2.fromScrollY - ChatAttachAlert.this.toScrollY) * (1.0f - f2);
                    }
                    ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
                    chatAttachAlert3.actionBar.setTranslationY(chatAttachAlert3.currentPanTranslationY);
                    ChatAttachAlert chatAttachAlert4 = ChatAttachAlert.this;
                    chatAttachAlert4.selectedMenuItem.setTranslationY(chatAttachAlert4.currentPanTranslationY);
                    ChatAttachAlert chatAttachAlert5 = ChatAttachAlert.this;
                    chatAttachAlert5.searchItem.setTranslationY(chatAttachAlert5.currentPanTranslationY);
                    ChatAttachAlert chatAttachAlert6 = ChatAttachAlert.this;
                    chatAttachAlert6.doneItem.setTranslationY(chatAttachAlert6.currentPanTranslationY);
                    ChatAttachAlert.this.actionBarShadow.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                    ChatAttachAlert.this.updateSelectedPosition(0);
                    ChatAttachAlert chatAttachAlert7 = ChatAttachAlert.this;
                    chatAttachAlert7.setCurrentPanTranslationY(chatAttachAlert7.currentPanTranslationY);
                    AnonymousClass3.this.invalidate();
                    ChatAttachAlert.this.frameLayout2.invalidate();
                    if (ChatAttachAlert.this.currentAttachLayout != null) {
                        ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                    }
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                protected boolean heightAnimationEnabled() {
                    if (ChatAttachAlert.this.isDismissed() || !ChatAttachAlert.this.openTransitionFinished) {
                        return false;
                    }
                    return !ChatAttachAlert.this.commentTextView.isPopupVisible();
                }
            };
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.currentAttachLayout.onContainerViewTouchEvent(motionEvent)) {
                return true;
            }
            if (motionEvent.getAction() == 0 && ChatAttachAlert.this.scrollOffsetY[0] != 0 && motionEvent.getY() < getCurrentTop() && ChatAttachAlert.this.actionBar.getAlpha() == 0.0f) {
                ChatAttachAlert.this.onDismissWithTouchOutside();
                return true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.currentAttachLayout.onContainerViewTouchEvent(motionEvent)) {
                return true;
            }
            return !ChatAttachAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int size;
            if (getLayoutParams().height > 0) {
                size = getLayoutParams().height;
            } else {
                size = View.MeasureSpec.getSize(i2);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (!chatAttachAlert.inBubbleMode) {
                    this.ignoreLayout = true;
                    setPadding(((BottomSheet) chatAttachAlert).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
            }
            getPaddingTop();
            int size2 = View.MeasureSpec.getSize(i) - (((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft * 2);
            if (AndroidUtilities.isTablet()) {
                ChatAttachAlert.this.selectedMenuItem.setAdditionalYOffset(-AndroidUtilities.dp(3.0f));
            } else {
                android.graphics.Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    ChatAttachAlert.this.selectedMenuItem.setAdditionalYOffset(0);
                } else {
                    ChatAttachAlert.this.selectedMenuItem.setAdditionalYOffset(-AndroidUtilities.dp(3.0f));
                }
            }
            ((FrameLayout.LayoutParams) ChatAttachAlert.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
            ((FrameLayout.LayoutParams) ChatAttachAlert.this.doneItem.getLayoutParams()).height = ActionBar.getCurrentActionBarHeight();
            this.ignoreLayout = true;
            int min = (int) (size2 / Math.min(4.5f, ChatAttachAlert.this.buttonsAdapter.getItemCount()));
            if (ChatAttachAlert.this.attachItemSize != min) {
                ChatAttachAlert.this.attachItemSize = min;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlert.AnonymousClass3.this.lambda$onMeasure$0();
                    }
                });
            }
            this.ignoreLayout = false;
            onMeasureInternal(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
        }

        public /* synthetic */ void lambda$onMeasure$0() {
            ChatAttachAlert.this.buttonsAdapter.notifyDataSetChanged();
        }

        private void onMeasureInternal(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int i3 = size - (((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft * 2);
            int measureKeyboardHeight = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
            if (!ChatAttachAlert.this.commentTextView.isWaitingForKeyboardOpen() && measureKeyboardHeight <= AndroidUtilities.dp(20.0f) && !ChatAttachAlert.this.commentTextView.isPopupShowing() && !ChatAttachAlert.this.commentTextView.isAnimatePopupClosing()) {
                this.ignoreLayout = true;
                ChatAttachAlert.this.commentTextView.hideEmojiView();
                this.ignoreLayout = false;
            }
            if (measureKeyboardHeight <= AndroidUtilities.dp(20.0f)) {
                int emojiPadding = (!SharedConfig.smoothKeyboard || !((BottomSheet) ChatAttachAlert.this).keyboardVisible) ? ChatAttachAlert.this.commentTextView.getEmojiPadding() : 0;
                if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= emojiPadding;
                    i2 = View.MeasureSpec.makeMeasureSpec(size2, NUM);
                }
                this.ignoreLayout = true;
                ChatAttachAlert.this.currentAttachLayout.onPreMeasure(i3, size2);
                if (ChatAttachAlert.this.nextAttachLayout != null) {
                    ChatAttachAlert.this.nextAttachLayout.onPreMeasure(i3, size2);
                }
                this.ignoreLayout = false;
            }
            int childCount = getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                if (childAt != null && childAt.getVisibility() != 8) {
                    EditTextEmoji editTextEmoji = ChatAttachAlert.this.commentTextView;
                    if (editTextEmoji != null && editTextEmoji.isPopupView(childAt)) {
                        if (ChatAttachAlert.this.inBubbleMode) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(getPaddingTop() + size2, NUM));
                        } else if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                            if (AndroidUtilities.isTablet()) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                            } else {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                            }
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        }
                    } else {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:113:0x00d1  */
        /* JADX WARN: Removed duplicated region for block: B:120:0x00eb  */
        /* JADX WARN: Removed duplicated region for block: B:124:0x00fe  */
        /* JADX WARN: Removed duplicated region for block: B:128:0x010a  */
        /* JADX WARN: Removed duplicated region for block: B:129:0x0113  */
        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
            /*
                Method dump skipped, instructions count: 316
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.onLayout(boolean, int, int, int, int):void");
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        private void drawChildBackground(Canvas canvas, View view) {
            int i;
            int dp;
            float dp2;
            float f;
            int themedColor;
            int themedColor2;
            float alpha;
            if (view instanceof AttachAlertLayout) {
                canvas.save();
                canvas.translate(0.0f, ChatAttachAlert.this.currentPanTranslationY);
                int alpha2 = (int) (view.getAlpha() * 255.0f);
                AttachAlertLayout attachAlertLayout = (AttachAlertLayout) view;
                int needsActionBar = attachAlertLayout.needsActionBar();
                int dp3 = AndroidUtilities.dp(13.0f);
                FrameLayout frameLayout = ChatAttachAlert.this.headerView;
                int dp4 = dp3 + (frameLayout != null ? AndroidUtilities.dp(frameLayout.getAlpha() * 26.0f) : 0);
                int scrollOffsetY = (ChatAttachAlert.this.getScrollOffsetY(0) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) - dp4;
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType == 1 || ChatAttachAlert.this.viewChangeAnimator != null) {
                    scrollOffsetY = (int) (scrollOffsetY + view.getTranslationY());
                }
                int dp5 = AndroidUtilities.dp(20.0f) + scrollOffsetY;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(45.0f) + ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
                if (needsActionBar == 0) {
                    i = ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
                } else {
                    i = ActionBar.getCurrentActionBarHeight();
                }
                if (needsActionBar == 2) {
                    if (scrollOffsetY < i) {
                        f = Math.max(0.0f, 1.0f - ((i - scrollOffsetY) / ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop));
                    }
                    f = 1.0f;
                } else {
                    if (((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY < i) {
                        float f2 = dp4;
                        if (attachAlertLayout != ChatAttachAlert.this.locationLayout) {
                            if (attachAlertLayout == ChatAttachAlert.this.pollLayout) {
                                dp2 = f2 - AndroidUtilities.dp(3.0f);
                                float min = Math.min(1.0f, ((i - scrollOffsetY) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) / dp2);
                                int i2 = (int) ((i - dp2) * min);
                                scrollOffsetY -= i2;
                                dp5 -= i2;
                                measuredHeight += i2;
                                f = 1.0f - min;
                            } else {
                                dp = AndroidUtilities.dp(4.0f);
                            }
                        } else {
                            dp = AndroidUtilities.dp(11.0f);
                        }
                        dp2 = f2 + dp;
                        float min2 = Math.min(1.0f, ((i - scrollOffsetY) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) / dp2);
                        int i22 = (int) ((i - dp2) * min2);
                        scrollOffsetY -= i22;
                        dp5 -= i22;
                        measuredHeight += i22;
                        f = 1.0f - min2;
                    }
                    f = 1.0f;
                }
                if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                    int i3 = AndroidUtilities.statusBarHeight;
                    scrollOffsetY += i3;
                    dp5 += i3;
                    measuredHeight -= i3;
                }
                if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                    themedColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
                } else {
                    themedColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground");
                }
                ((BottomSheet) ChatAttachAlert.this).shadowDrawable.setAlpha(alpha2);
                ((BottomSheet) ChatAttachAlert.this).shadowDrawable.setBounds(0, scrollOffsetY, getMeasuredWidth(), measuredHeight);
                ((BottomSheet) ChatAttachAlert.this).shadowDrawable.draw(canvas);
                if (needsActionBar == 2) {
                    Theme.dialogs_onlineCirclePaint.setColor(themedColor);
                    Theme.dialogs_onlineCirclePaint.setAlpha(alpha2);
                    this.rect.set(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY + AndroidUtilities.dp(24.0f));
                    canvas.save();
                    RectF rectF = this.rect;
                    float f3 = rectF.left;
                    float f4 = rectF.top;
                    canvas.clipRect(f3, f4, rectF.right, (rectF.height() / 2.0f) + f4);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                    canvas.restore();
                }
                if (f != 1.0f && needsActionBar != 2) {
                    Theme.dialogs_onlineCirclePaint.setColor(themedColor);
                    Theme.dialogs_onlineCirclePaint.setAlpha(alpha2);
                    this.rect.set(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY + AndroidUtilities.dp(24.0f));
                    canvas.save();
                    RectF rectF2 = this.rect;
                    float f5 = rectF2.left;
                    float f6 = rectF2.top;
                    canvas.clipRect(f5, f6, rectF2.right, (rectF2.height() / 2.0f) + f6);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                    canvas.restore();
                }
                FrameLayout frameLayout2 = ChatAttachAlert.this.headerView;
                if ((frameLayout2 == null || frameLayout2.getAlpha() != 1.0f) && f != 0.0f) {
                    int dp6 = AndroidUtilities.dp(36.0f);
                    this.rect.set((getMeasuredWidth() - dp6) / 2, dp5, (getMeasuredWidth() + dp6) / 2, dp5 + AndroidUtilities.dp(4.0f));
                    if (needsActionBar == 2) {
                        themedColor2 = NUM;
                        alpha = f;
                    } else {
                        themedColor2 = ChatAttachAlert.this.getThemedColor("key_sheet_scrollUp");
                        FrameLayout frameLayout3 = ChatAttachAlert.this.headerView;
                        alpha = frameLayout3 == null ? 1.0f : 1.0f - frameLayout3.getAlpha();
                    }
                    int alpha3 = Color.alpha(themedColor2);
                    Theme.dialogs_onlineCirclePaint.setColor(themedColor2);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha3 * alpha * f * view.getAlpha()));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                canvas.restore();
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View view, long j) {
            int i;
            int dp;
            float dp2;
            float f;
            int themedColor;
            int themedColor2;
            float alpha;
            int i2;
            FrameLayout frameLayout;
            if ((view instanceof AttachAlertLayout) && view.getAlpha() > 0.0f) {
                canvas.save();
                canvas.translate(0.0f, ChatAttachAlert.this.currentPanTranslationY);
                int alpha2 = (int) (view.getAlpha() * 255.0f);
                AttachAlertLayout attachAlertLayout = (AttachAlertLayout) view;
                int needsActionBar = attachAlertLayout.needsActionBar();
                int dp3 = AndroidUtilities.dp(13.0f);
                FrameLayout frameLayout2 = ChatAttachAlert.this.headerView;
                int dp4 = dp3 + (frameLayout2 != null ? AndroidUtilities.dp(frameLayout2.getAlpha() * 26.0f) : 0);
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                int scrollOffsetY = (chatAttachAlert.getScrollOffsetY(attachAlertLayout == chatAttachAlert.currentAttachLayout ? 0 : 1) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) - dp4;
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType == 1 || ChatAttachAlert.this.viewChangeAnimator != null) {
                    scrollOffsetY = (int) (scrollOffsetY + view.getTranslationY());
                }
                int dp5 = AndroidUtilities.dp(20.0f) + scrollOffsetY;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(45.0f) + ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
                if (needsActionBar == 0) {
                    i = ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
                } else {
                    i = ActionBar.getCurrentActionBarHeight();
                }
                if (needsActionBar == 2) {
                    if (scrollOffsetY < i) {
                        f = Math.max(0.0f, 1.0f - ((i - scrollOffsetY) / ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop));
                    }
                    f = 1.0f;
                } else {
                    if (((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY < i) {
                        float f2 = dp4;
                        if (attachAlertLayout != ChatAttachAlert.this.locationLayout) {
                            if (attachAlertLayout == ChatAttachAlert.this.pollLayout) {
                                dp2 = f2 - AndroidUtilities.dp(3.0f);
                                float min = Math.min(1.0f, ((i - scrollOffsetY) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) / dp2);
                                int i3 = (int) ((i - dp2) * min);
                                scrollOffsetY -= i3;
                                dp5 -= i3;
                                measuredHeight += i3;
                                f = 1.0f - min;
                            } else {
                                dp = AndroidUtilities.dp(4.0f);
                            }
                        } else {
                            dp = AndroidUtilities.dp(11.0f);
                        }
                        dp2 = f2 + dp;
                        float min2 = Math.min(1.0f, ((i - scrollOffsetY) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) / dp2);
                        int i32 = (int) ((i - dp2) * min2);
                        scrollOffsetY -= i32;
                        dp5 -= i32;
                        measuredHeight += i32;
                        f = 1.0f - min2;
                    }
                    f = 1.0f;
                }
                if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                    int i4 = AndroidUtilities.statusBarHeight;
                    scrollOffsetY += i4;
                    dp5 += i4;
                    measuredHeight -= i4;
                }
                if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                    themedColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
                } else {
                    themedColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground");
                }
                boolean z = (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout || ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.photoPreviewLayout || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout && ChatAttachAlert.this.nextAttachLayout == null)) ? false : true;
                if (z) {
                    ((BottomSheet) ChatAttachAlert.this).shadowDrawable.setAlpha(alpha2);
                    ((BottomSheet) ChatAttachAlert.this).shadowDrawable.setBounds(0, scrollOffsetY, getMeasuredWidth(), measuredHeight);
                    ((BottomSheet) ChatAttachAlert.this).shadowDrawable.draw(canvas);
                    if (needsActionBar == 2) {
                        Theme.dialogs_onlineCirclePaint.setColor(themedColor);
                        Theme.dialogs_onlineCirclePaint.setAlpha(alpha2);
                        this.rect.set(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY + AndroidUtilities.dp(24.0f));
                        canvas.save();
                        RectF rectF = this.rect;
                        float f3 = rectF.left;
                        float f4 = rectF.top;
                        canvas.clipRect(f3, f4, rectF.right, (rectF.height() / 2.0f) + f4);
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                        canvas.restore();
                    }
                }
                boolean z2 = !z && (frameLayout = ChatAttachAlert.this.headerView) != null && frameLayout.getAlpha() > 0.9f && ((ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) && (ChatAttachAlert.this.viewChangeAnimator instanceof SpringAnimation) && ((SpringAnimation) ChatAttachAlert.this.viewChangeAnimator).isRunning();
                if (z2) {
                    canvas.save();
                    if (AndroidUtilities.isTablet()) {
                        i2 = 16;
                    } else {
                        android.graphics.Point point = AndroidUtilities.displaySize;
                        i2 = point.x > point.y ? 6 : 12;
                    }
                    canvas.clipRect(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, (int) (ChatAttachAlert.this.baseSelectedTextViewTranslationY + AndroidUtilities.statusBarHeight + ChatAttachAlert.this.headerView.getHeight() + AndroidUtilities.dp(i2 * ChatAttachAlert.this.headerView.getAlpha())), getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, getMeasuredHeight());
                }
                boolean drawChild = super.drawChild(canvas, view, j);
                if (z2) {
                    canvas.restore();
                }
                if (z) {
                    if (f != 1.0f && needsActionBar != 2) {
                        Theme.dialogs_onlineCirclePaint.setColor(themedColor);
                        Theme.dialogs_onlineCirclePaint.setAlpha(alpha2);
                        this.rect.set(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY + AndroidUtilities.dp(24.0f));
                        canvas.save();
                        RectF rectF2 = this.rect;
                        float f5 = rectF2.left;
                        float f6 = rectF2.top;
                        canvas.clipRect(f5, f6, rectF2.right, (rectF2.height() / 2.0f) + f6);
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                        canvas.restore();
                    }
                    FrameLayout frameLayout3 = ChatAttachAlert.this.headerView;
                    if ((frameLayout3 == null || frameLayout3.getAlpha() != 1.0f) && f != 0.0f) {
                        int dp6 = AndroidUtilities.dp(36.0f);
                        this.rect.set((getMeasuredWidth() - dp6) / 2, dp5, (getMeasuredWidth() + dp6) / 2, dp5 + AndroidUtilities.dp(4.0f));
                        if (needsActionBar == 2) {
                            themedColor2 = NUM;
                            alpha = f;
                        } else {
                            themedColor2 = ChatAttachAlert.this.getThemedColor("key_sheet_scrollUp");
                            FrameLayout frameLayout4 = ChatAttachAlert.this.headerView;
                            alpha = frameLayout4 == null ? 1.0f : 1.0f - frameLayout4.getAlpha();
                        }
                        int alpha3 = Color.alpha(themedColor2);
                        Theme.dialogs_onlineCirclePaint.setColor(themedColor2);
                        Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha3 * alpha * f * view.getAlpha()));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    }
                }
                canvas.restore();
                return drawChild;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int themedColor;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (chatAttachAlert.inBubbleMode) {
                return;
            }
            if (chatAttachAlert.currentAttachLayout.hasCustomBackground()) {
                themedColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
            } else {
                themedColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground");
            }
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (ChatAttachAlert.this.actionBar.getAlpha() * 255.0f), Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor)));
            canvas.drawRect(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ChatAttachAlert.this.currentPanTranslationY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight + ChatAttachAlert.this.currentPanTranslationY, Theme.dialogs_onlineCirclePaint);
        }

        private int getCurrentTop() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            int i = 0;
            int i2 = chatAttachAlert.scrollOffsetY[0] - (((BottomSheet) chatAttachAlert).backgroundPaddingTop * 2);
            int dp = AndroidUtilities.dp(13.0f);
            FrameLayout frameLayout = ChatAttachAlert.this.headerView;
            if (frameLayout != null) {
                i = AndroidUtilities.dp(frameLayout.getAlpha() * 26.0f);
            }
            int dp2 = (i2 - (dp + i)) + AndroidUtilities.dp(20.0f);
            return (Build.VERSION.SDK_INT < 21 || ChatAttachAlert.this.inBubbleMode) ? dp2 : dp2 + AndroidUtilities.statusBarHeight;
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, getPaddingTop() + ChatAttachAlert.this.currentPanTranslationY, getMeasuredWidth(), (getMeasuredHeight() + ChatAttachAlert.this.currentPanTranslationY) - getPaddingBottom());
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout || ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.photoPreviewLayout || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout && ChatAttachAlert.this.nextAttachLayout == null)) {
                drawChildBackground(canvas, ChatAttachAlert.this.currentAttachLayout);
            }
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            float f2 = f + chatAttachAlert.currentPanTranslationY;
            if (((BottomSheet) chatAttachAlert).currentSheetAnimationType == 0) {
                this.initialTranslationY = f2;
            }
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType == 1) {
                if (f2 < 0.0f) {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationY(f2);
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    if (chatAttachAlert2.avatarPicker != 0) {
                        chatAttachAlert2.headerView.setTranslationY((chatAttachAlert2.baseSelectedTextViewTranslationY + f2) - ChatAttachAlert.this.currentPanTranslationY);
                    }
                    ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(0.0f);
                    f2 = 0.0f;
                } else {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationY(0.0f);
                    RecyclerListView recyclerListView = ChatAttachAlert.this.buttonsRecyclerView;
                    recyclerListView.setTranslationY((-f2) + (recyclerListView.getMeasuredHeight() * (f2 / this.initialTranslationY)));
                }
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }
            super.setTranslationY(f2 - ChatAttachAlert.this.currentPanTranslationY);
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType != 1) {
                ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.adjustPanLayoutHelper.setResizableView(this);
            this.adjustPanLayoutHelper.onAttach();
            ChatAttachAlert.this.commentTextView.setAdjustPanLayoutHelper(this.adjustPanLayoutHelper);
            Bulletin.addDelegate(this, this.bulletinDelegate);
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.adjustPanLayoutHelper.onDetach();
            Bulletin.removeDelegate(this);
        }
    }

    public /* synthetic */ void lambda$new$0(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    public /* synthetic */ void lambda$new$1(View view) {
        this.selectedMenuItem.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2(View view) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    public /* synthetic */ void lambda$new$3(boolean z, View view) {
        if (this.avatarPicker != 0) {
            this.delegate.openAvatarsSearch();
            dismiss();
            return;
        }
        final HashMap hashMap = new HashMap();
        final ArrayList arrayList = new ArrayList();
        PhotoPickerSearchActivity photoPickerSearchActivity = new PhotoPickerSearchActivity(hashMap, arrayList, 0, true, (ChatActivity) this.baseFragment);
        photoPickerSearchActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.7
            private boolean sendPressed;

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void onCaptionChanged(CharSequence charSequence) {
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void selectedPhotosChanged() {
            }

            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void actionButtonPressed(boolean z2, boolean z3, int i) {
                if (!z2 && !hashMap.isEmpty() && !this.sendPressed) {
                    this.sendPressed = true;
                    ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList2 = new ArrayList<>();
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        Object obj = hashMap.get(arrayList.get(i2));
                        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                        arrayList2.add(sendingMediaInfo);
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
                    ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectSearchPhotos(arrayList2, z3, i);
                }
            }
        });
        photoPickerSearchActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        if (z) {
            this.baseFragment.showAsSheet(photoPickerSearchActivity);
        } else {
            this.baseFragment.presentFragment(photoPickerSearchActivity);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$new$4(View view) {
        updatePhotoPreview(this.currentAttachLayout != this.photoPreviewLayout);
    }

    public /* synthetic */ void lambda$new$7(Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (this.baseFragment.getParentActivity() == null) {
            return;
        }
        if (view instanceof AttachButton) {
            int intValue = ((Integer) view.getTag()).intValue();
            if (intValue == 1) {
                showLayout(this.photoLayout);
            } else if (intValue == 3) {
                if (Build.VERSION.SDK_INT >= 23 && this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                    return;
                }
                openAudioLayout(true);
            } else if (intValue == 4) {
                if (Build.VERSION.SDK_INT >= 23 && this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                    return;
                }
                openDocumentsLayout(true);
            } else if (intValue == 5) {
                if (Build.VERSION.SDK_INT >= 23 && this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
                    return;
                }
                openContactsLayout();
            } else if (intValue == 6) {
                if (!AndroidUtilities.isMapsInstalled(this.baseFragment)) {
                    return;
                }
                if (this.locationLayout == null) {
                    AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                    ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = new ChatAttachAlertLocationLayout(this, getContext(), resourcesProvider);
                    this.locationLayout = chatAttachAlertLocationLayout;
                    attachAlertLayoutArr[5] = chatAttachAlertLocationLayout;
                    chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlertLocationLayout.LocationActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda34
                        @Override // org.telegram.ui.Components.ChatAttachAlertLocationLayout.LocationActivityDelegate
                        public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
                            ChatAttachAlert.this.lambda$new$5(tLRPC$MessageMedia, i2, z, i3);
                        }
                    });
                }
                showLayout(this.locationLayout);
            } else if (intValue == 9) {
                if (this.pollLayout == null) {
                    AttachAlertLayout[] attachAlertLayoutArr2 = this.layouts;
                    ChatAttachAlertPollLayout chatAttachAlertPollLayout = new ChatAttachAlertPollLayout(this, getContext(), resourcesProvider);
                    this.pollLayout = chatAttachAlertPollLayout;
                    attachAlertLayoutArr2[1] = chatAttachAlertPollLayout;
                    chatAttachAlertPollLayout.setDelegate(new ChatAttachAlertPollLayout.PollCreateActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35
                        @Override // org.telegram.ui.Components.ChatAttachAlertPollLayout.PollCreateActivityDelegate
                        public final void sendPoll(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i2) {
                            ChatAttachAlert.this.lambda$new$6(tLRPC$TL_messageMediaPoll, hashMap, z, i2);
                        }
                    });
                }
                showLayout(this.pollLayout);
            } else {
                this.delegate.didPressedButton(((Integer) view.getTag()).intValue(), true, true, 0, false);
            }
            int left = view.getLeft();
            int right = view.getRight();
            int dp = AndroidUtilities.dp(10.0f);
            int i2 = left - dp;
            if (i2 < 0) {
                this.buttonsRecyclerView.smoothScrollBy(i2, 0);
            } else {
                int i3 = right + dp;
                if (i3 > this.buttonsRecyclerView.getMeasuredWidth()) {
                    RecyclerListView recyclerListView = this.buttonsRecyclerView;
                    recyclerListView.smoothScrollBy(i3 - recyclerListView.getMeasuredWidth(), 0);
                }
            }
        } else if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (attachBotButton.attachMenuBot != null) {
                showBotLayout(attachBotButton.attachMenuBot.bot_id);
            } else {
                this.delegate.didSelectBot(attachBotButton.currentUser);
                dismiss();
            }
        }
        if (view.getX() + view.getWidth() < this.buttonsRecyclerView.getMeasuredWidth() - AndroidUtilities.dp(32.0f)) {
            return;
        }
        this.buttonsRecyclerView.smoothScrollBy((int) (view.getWidth() * 1.5f), 0);
    }

    public /* synthetic */ void lambda$new$5(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        ((ChatActivity) this.baseFragment).didSelectLocation(tLRPC$MessageMedia, i, z, i2);
    }

    public /* synthetic */ void lambda$new$6(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendPoll(tLRPC$TL_messageMediaPoll, hashMap, z, i);
    }

    public /* synthetic */ boolean lambda$new$8(View view, int i) {
        if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (this.baseFragment != null && attachBotButton.currentUser != null) {
                onLongClickBotButton(attachBotButton.attachMenuBot, attachBotButton.currentUser);
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$new$9(View view) {
        ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout;
        long j = this.selectedId;
        if (j >= 0 || (chatAttachAlertBotWebViewLayout = this.botAttachLayouts.get(-j)) == null) {
            return;
        }
        chatAttachAlertBotWebViewLayout.getWebViewContainer().onMainButtonPressed();
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$10 */
    /* loaded from: classes3.dex */
    public class AnonymousClass10 extends FrameLayout {
        private int color;
        private final Paint p;
        final /* synthetic */ boolean val$forceDarkTheme;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass10(Context context, boolean z) {
            super(context);
            ChatAttachAlert.this = r1;
            this.val$forceDarkTheme = z;
            this.p = new Paint();
        }

        @Override // android.view.View
        public void setAlpha(float f) {
            super.setAlpha(f);
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int themedColor;
            if (ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop != 0.0f && ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop != ChatAttachAlert.this.frameLayout2.getTop() + ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop) {
                if (ChatAttachAlert.this.topBackgroundAnimator != null) {
                    ChatAttachAlert.this.topBackgroundAnimator.cancel();
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.captionEditTextTopOffset = chatAttachAlert.chatActivityEnterViewAnimateFromTop - (ChatAttachAlert.this.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset);
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                chatAttachAlert2.topBackgroundAnimator = ValueAnimator.ofFloat(chatAttachAlert2.captionEditTextTopOffset, 0.0f);
                ChatAttachAlert.this.topBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$10$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatAttachAlert.AnonymousClass10.this.lambda$onDraw$0(valueAnimator);
                    }
                });
                ChatAttachAlert.this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ChatAttachAlert.this.topBackgroundAnimator.setDuration(200L);
                ChatAttachAlert.this.topBackgroundAnimator.start();
                ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop = 0.0f;
            }
            float measuredHeight = (ChatAttachAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f)) * (1.0f - getAlpha());
            View view = ChatAttachAlert.this.shadow;
            float f = (-(ChatAttachAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f))) + ChatAttachAlert.this.captionEditTextTopOffset;
            ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
            view.setTranslationY(f + chatAttachAlert3.currentPanTranslationY + chatAttachAlert3.bottomPannelTranslation + measuredHeight + ChatAttachAlert.this.botMainButtonOffsetY);
            if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                themedColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
            } else {
                themedColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground");
            }
            if (this.color != themedColor) {
                this.color = themedColor;
                this.p.setColor(themedColor);
            }
            canvas.drawRect(0.0f, ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight(), this.p);
        }

        public /* synthetic */ void lambda$onDraw$0(ValueAnimator valueAnimator) {
            ChatAttachAlert.this.captionEditTextTopOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.frameLayout2.invalidate();
            invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$11 */
    /* loaded from: classes3.dex */
    public class AnonymousClass11 extends EditTextEmoji {
        private ValueAnimator messageEditTextAnimator;
        private int messageEditTextPredrawHeigth;
        private int messageEditTextPredrawScrollY;
        private boolean shouldAnimateEditTextWithBounds;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass11(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, int i, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context, sizeNotifierFrameLayout, baseFragment, i, z, resourcesProvider);
            ChatAttachAlert.this = r8;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!ChatAttachAlert.this.enterCommentEventSent) {
                if (motionEvent.getX() > ChatAttachAlert.this.commentTextView.getEditText().getLeft() && motionEvent.getX() < ChatAttachAlert.this.commentTextView.getEditText().getRight() && motionEvent.getY() > ChatAttachAlert.this.commentTextView.getEditText().getTop() && motionEvent.getY() < ChatAttachAlert.this.commentTextView.getEditText().getBottom()) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.makeFocusable(chatAttachAlert.commentTextView.getEditText(), true);
                } else {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    chatAttachAlert2.makeFocusable(chatAttachAlert2.commentTextView.getEditText(), false);
                }
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.shouldAnimateEditTextWithBounds) {
                final EditTextCaption editText = ChatAttachAlert.this.commentTextView.getEditText();
                editText.setOffsetY(editText.getOffsetY() - ((this.messageEditTextPredrawHeigth - editText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - editText.getScrollY())));
                ValueAnimator ofFloat = ValueAnimator.ofFloat(editText.getOffsetY(), 0.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$11$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatAttachAlert.AnonymousClass11.lambda$dispatchDraw$0(EditTextCaption.this, valueAnimator);
                    }
                });
                ValueAnimator valueAnimator = this.messageEditTextAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.messageEditTextAnimator = ofFloat;
                ofFloat.setDuration(200L);
                ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ofFloat.start();
                this.shouldAnimateEditTextWithBounds = false;
            }
            super.dispatchDraw(canvas);
        }

        public static /* synthetic */ void lambda$dispatchDraw$0(EditTextCaption editTextCaption, ValueAnimator valueAnimator) {
            editTextCaption.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        protected void onLineCountChanged(int i, int i2) {
            if (!TextUtils.isEmpty(getEditText().getText())) {
                this.shouldAnimateEditTextWithBounds = true;
                this.messageEditTextPredrawHeigth = getEditText().getMeasuredHeight();
                this.messageEditTextPredrawScrollY = getEditText().getScrollY();
                invalidate();
            } else {
                getEditText().animate().cancel();
                getEditText().setOffsetY(0.0f);
                this.shouldAnimateEditTextWithBounds = false;
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.chatActivityEnterViewAnimateFromTop = chatAttachAlert.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset;
            ChatAttachAlert.this.frameLayout2.invalidate();
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        protected void bottomPanelTranslationY(float f) {
            ChatAttachAlert.this.bottomPannelTranslation = f;
            ChatAttachAlert.this.frameLayout2.setTranslationY(f);
            ChatAttachAlert.this.writeButtonContainer.setTranslationY(f);
            ChatAttachAlert.this.selectedCountView.setTranslationY(f);
            ChatAttachAlert.this.frameLayout2.invalidate();
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, true, 0);
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        protected void closeParent() {
            ChatAttachAlert.super.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$12 */
    /* loaded from: classes3.dex */
    public class AnonymousClass12 implements TextWatcher {
        private boolean processChange;
        final /* synthetic */ BaseFragment val$parentFragment;

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        AnonymousClass12(BaseFragment baseFragment) {
            ChatAttachAlert.this = r1;
            this.val$parentFragment = baseFragment;
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (i3 - i2 >= 1) {
                this.processChange = true;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:91:0x0133  */
        @Override // android.text.TextWatcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void afterTextChanged(android.text.Editable r10) {
            /*
                Method dump skipped, instructions count: 457
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass12.afterTextChanged(android.text.Editable):void");
        }

        public /* synthetic */ void lambda$afterTextChanged$0(ValueAnimator valueAnimator) {
            ChatAttachAlert.this.sendButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            int themedColor = ChatAttachAlert.this.getThemedColor("dialogFloatingIcon");
            ChatAttachAlert.this.writeButton.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(themedColor, (int) (Color.alpha(themedColor) * ((ChatAttachAlert.this.sendButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
            ChatAttachAlert.this.selectedCountView.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$12(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view) {
        if (this.currentLimit - this.codepointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView);
            try {
                this.captionLimitView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (MessagesController.getInstance(this.currentAccount).premiumLocked || MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium <= this.codepointCount) {
                return;
            }
            showCaptionLimitBulletin(baseFragment);
            return;
        }
        if (this.editingMessageObject == null) {
            BaseFragment baseFragment2 = this.baseFragment;
            if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda30
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlert.this.lambda$new$11(z, i);
                    }
                }, resourcesProvider);
                return;
            }
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(true, 0);
            return;
        }
        attachAlertLayout.sendSelectedItems(true, 0);
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    public /* synthetic */ void lambda$new$11(boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i);
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i);
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* JADX WARN: Removed duplicated region for block: B:76:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0089  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x00b3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ boolean lambda$new$16(final org.telegram.ui.ActionBar.Theme.ResourcesProvider r12, android.view.View r13) {
        /*
            Method dump skipped, instructions count: 392
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.lambda$new$16(org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.view.View):boolean");
    }

    public /* synthetic */ void lambda$new$13(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() != 4 || keyEvent.getRepeatCount() != 0 || (actionBarPopupWindow = this.sendPopupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.sendPopupWindow.dismiss();
    }

    public /* synthetic */ void lambda$new$15(int i, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda31
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i2) {
                    ChatAttachAlert.this.lambda$new$14(z, i2);
                }
            }, resourcesProvider);
        } else if (i != 1) {
        } else {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
                sendPressed(false, 0);
                return;
            }
            attachAlertLayout.sendSelectedItems(false, 0);
            dismiss();
        }
    }

    public /* synthetic */ void lambda$new$14(boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i);
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i);
        dismiss();
    }

    public void showCaptionLimitBulletin(final BaseFragment baseFragment) {
        if (!(baseFragment instanceof ChatActivity) || !ChatObject.isChannelAndNotMegaGroup(((ChatActivity) baseFragment).getCurrentChat())) {
            return;
        }
        BulletinFactory.of(this.sizeNotifierFrameLayout, this.resourcesProvider).createCaptionLimitBulletin(MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium, new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlert.this.lambda$showCaptionLimitBulletin$17(baseFragment);
            }
        }).show();
    }

    public /* synthetic */ void lambda$showCaptionLimitBulletin$17(BaseFragment baseFragment) {
        dismiss(true);
        if (baseFragment != null) {
            baseFragment.presentFragment(new PremiumPreviewFragment("caption_limit"));
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(getThemedColor(this.forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground")) > 0.699999988079071d;
    }

    public void onLongClickBotButton(final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, final TLRPC$User tLRPC$User) {
        String userName = tLRPC$TL_attachMenuBot != null ? tLRPC$TL_attachMenuBot.short_name : UserObject.getUserName(tLRPC$User);
        new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.BotRemoveFromMenuTitle)).setMessage(AndroidUtilities.replaceTags(tLRPC$TL_attachMenuBot != null ? LocaleController.formatString("BotRemoveFromMenu", R.string.BotRemoveFromMenu, userName) : LocaleController.formatString("BotRemoveInlineFromMenu", R.string.BotRemoveInlineFromMenu, userName))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatAttachAlert.this.lambda$onLongClickBotButton$20(tLRPC$TL_attachMenuBot, tLRPC$User, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
    }

    public /* synthetic */ void lambda$onLongClickBotButton$20(final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        if (tLRPC$TL_attachMenuBot != null) {
            TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
            tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(tLRPC$User);
            tLRPC$TL_messages_toggleBotInAttachMenu.enabled = false;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new RequestDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda27
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatAttachAlert.this.lambda$onLongClickBotButton$19(tLRPC$TL_attachMenuBot, tLObject, tLRPC$TL_error);
                }
            }, 66);
            return;
        }
        MediaDataController.getInstance(this.currentAccount).removeInline(tLRPC$User.id);
    }

    public /* synthetic */ void lambda$onLongClickBotButton$19(final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlert.this.lambda$onLongClickBotButton$18(tLRPC$TL_attachMenuBot);
            }
        });
    }

    public /* synthetic */ void lambda$onLongClickBotButton$18(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        MediaDataController.getInstance(this.currentAccount).loadAttachMenuBots(false, true);
        if (this.currentAttachLayout == this.botAttachLayouts.get(tLRPC$TL_attachMenuBot.bot_id)) {
            showLayout(this.photoLayout);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean shouldOverlayCameraViewOverNavBar() {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        return attachAlertLayout == chatAttachAlertPhotoLayout && chatAttachAlertPhotoLayout.cameraExpanded;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        boolean z = false;
        this.buttonPressed = false;
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            this.calcMandatoryInsets = ((ChatActivity) baseFragment).isKeyboardVisible();
        }
        this.openTransitionFinished = false;
        if (Build.VERSION.SDK_INT >= 30) {
            this.navBarColorKey = null;
            this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), 0);
            AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
            Window window = getWindow();
            if (AndroidUtilities.computePerceivedBrightness(this.navBarColor) > 0.721d) {
                z = true;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
    }

    public void setEditingMessageObject(MessageObject messageObject) {
        if (this.editingMessageObject == messageObject) {
            return;
        }
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

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public void applyCaption() {
        if (this.commentTextView.length() <= 0) {
            return;
        }
        this.currentAttachLayout.applyCaption(this.commentTextView.getText());
    }

    private void sendPressed(boolean z, int i) {
        if (this.buttonPressed) {
            return;
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            TLRPC$Chat currentChat = chatActivity.getCurrentChat();
            if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit.putBoolean("silent_" + chatActivity.getDialogId(), !z).commit();
            }
        }
        if (checkCaption(this.commentTextView.getText())) {
            return;
        }
        applyCaption();
        this.buttonPressed = true;
        this.delegate.didPressedButton(7, true, z, i, false);
    }

    private void showLayout(AttachAlertLayout attachAlertLayout) {
        long j = this.selectedId;
        if (attachAlertLayout == this.photoLayout) {
            j = 1;
        } else if (attachAlertLayout == this.audioLayout) {
            j = 3;
        } else if (attachAlertLayout == this.documentLayout) {
            j = 4;
        } else if (attachAlertLayout == this.contactsLayout) {
            j = 5;
        } else if (attachAlertLayout == this.locationLayout) {
            j = 6;
        } else if (attachAlertLayout == this.pollLayout) {
            j = 9;
        }
        showLayout(attachAlertLayout, j);
    }

    private void showLayout(final AttachAlertLayout attachAlertLayout, long j) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout;
        CameraView cameraView;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2;
        CameraView cameraView2;
        if (this.viewChangeAnimator == null && this.commentsAnimator == null) {
            AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
            if (attachAlertLayout2 == attachAlertLayout) {
                attachAlertLayout2.scrollToTop();
                return;
            }
            this.botButtonWasVisible = false;
            this.botButtonProgressWasVisible = false;
            this.botMainButtonOffsetY = 0.0f;
            this.botMainButtonTextView.setVisibility(8);
            this.botProgressView.setAlpha(0.0f);
            this.botProgressView.setScaleX(0.1f);
            this.botProgressView.setScaleY(0.1f);
            this.botProgressView.setVisibility(8);
            this.buttonsRecyclerView.setAlpha(1.0f);
            this.buttonsRecyclerView.setTranslationY(this.botMainButtonOffsetY);
            for (int i = 0; i < this.botAttachLayouts.size(); i++) {
                this.botAttachLayouts.valueAt(i).setMeasureOffsetY(0);
            }
            this.selectedId = j;
            int childCount = this.buttonsRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.buttonsRecyclerView.getChildAt(i2);
                if (childAt instanceof AttachButton) {
                    ((AttachButton) childAt).updateCheckedState(true);
                } else if (childAt instanceof AttachBotButton) {
                    ((AttachBotButton) childAt).updateCheckedState(true);
                }
            }
            int firstOffset = (this.currentAttachLayout.getFirstOffset() - AndroidUtilities.dp(11.0f)) - this.scrollOffsetY[0];
            this.nextAttachLayout = attachAlertLayout;
            if (Build.VERSION.SDK_INT >= 20) {
                this.container.setLayerType(2, null);
            }
            this.actionBar.setVisibility(this.nextAttachLayout.needsActionBar() != 0 ? 0 : 4);
            this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
            if (this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
            }
            this.currentAttachLayout.onHide();
            AttachAlertLayout attachAlertLayout3 = this.nextAttachLayout;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout3 = this.photoLayout;
            if (attachAlertLayout3 == chatAttachAlertPhotoLayout3) {
                chatAttachAlertPhotoLayout3.setCheckCameraWhenShown(true);
            }
            this.nextAttachLayout.onShow(this.currentAttachLayout);
            this.nextAttachLayout.setVisibility(0);
            if (attachAlertLayout.getParent() != null) {
                this.containerView.removeView(this.nextAttachLayout);
            }
            int indexOfChild = this.containerView.indexOfChild(this.currentAttachLayout);
            ViewParent parent = this.nextAttachLayout.getParent();
            ViewGroup viewGroup = this.containerView;
            if (parent != viewGroup) {
                AttachAlertLayout attachAlertLayout4 = this.nextAttachLayout;
                if (attachAlertLayout4 != this.locationLayout) {
                    indexOfChild++;
                }
                viewGroup.addView(attachAlertLayout4, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
            }
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.this.lambda$showLayout$21();
                }
            };
            if (!(this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) && !(this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                AnimatorSet animatorSet = new AnimatorSet();
                this.nextAttachLayout.setAlpha(0.0f);
                this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f));
                AttachAlertLayout attachAlertLayout5 = this.currentAttachLayout;
                Property property = View.TRANSLATION_Y;
                float[] fArr = {AndroidUtilities.dp(78.0f) + firstOffset};
                ActionBar actionBar = this.actionBar;
                animatorSet.playTogether(ObjectAnimator.ofFloat(attachAlertLayout5, property, fArr), ObjectAnimator.ofFloat(this.currentAttachLayout, this.ATTACH_ALERT_LAYOUT_TRANSLATION, 0.0f, 1.0f), ObjectAnimator.ofFloat(actionBar, View.ALPHA, actionBar.getAlpha(), 0.0f));
                animatorSet.setDuration(180L);
                animatorSet.setStartDelay(20L);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnonymousClass17(runnable));
                this.viewChangeAnimator = animatorSet;
                animatorSet.start();
                return;
            }
            int max = Math.max(this.nextAttachLayout.getWidth(), this.currentAttachLayout.getWidth());
            AttachAlertLayout attachAlertLayout6 = this.nextAttachLayout;
            if (attachAlertLayout6 instanceof ChatAttachAlertPhotoLayoutPreview) {
                attachAlertLayout6.setTranslationX(max);
                AttachAlertLayout attachAlertLayout7 = this.currentAttachLayout;
                if ((attachAlertLayout7 instanceof ChatAttachAlertPhotoLayout) && (cameraView2 = (chatAttachAlertPhotoLayout2 = (ChatAttachAlertPhotoLayout) attachAlertLayout7).cameraView) != null) {
                    cameraView2.setVisibility(4);
                    chatAttachAlertPhotoLayout2.cameraIcon.setVisibility(4);
                    chatAttachAlertPhotoLayout2.cameraCell.setVisibility(0);
                }
            } else {
                this.currentAttachLayout.setTranslationX(-max);
                AttachAlertLayout attachAlertLayout8 = this.nextAttachLayout;
                if (attachAlertLayout8 == this.photoLayout && (cameraView = (chatAttachAlertPhotoLayout = (ChatAttachAlertPhotoLayout) attachAlertLayout8).cameraView) != null) {
                    cameraView.setVisibility(0);
                    chatAttachAlertPhotoLayout.cameraIcon.setVisibility(0);
                }
            }
            this.nextAttachLayout.setAlpha(1.0f);
            this.currentAttachLayout.setAlpha(1.0f);
            this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(0.0f));
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.this.lambda$showLayout$24(attachAlertLayout, runnable);
                }
            });
        }
    }

    public /* synthetic */ void lambda$showLayout$21() {
        AttachAlertLayout attachAlertLayout;
        ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview;
        if (Build.VERSION.SDK_INT >= 20) {
            this.container.setLayerType(0, null);
        }
        this.viewChangeAnimator = null;
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        if (attachAlertLayout2 != this.photoLayout && (attachAlertLayout = this.nextAttachLayout) != (chatAttachAlertPhotoLayoutPreview = this.photoPreviewLayout) && attachAlertLayout2 != attachAlertLayout && attachAlertLayout2 != chatAttachAlertPhotoLayoutPreview) {
            this.containerView.removeView(attachAlertLayout2);
        }
        this.currentAttachLayout.setVisibility(8);
        this.currentAttachLayout.onHidden();
        this.nextAttachLayout.onShown();
        this.currentAttachLayout = this.nextAttachLayout;
        this.nextAttachLayout = null;
        int[] iArr = this.scrollOffsetY;
        iArr[0] = iArr[1];
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$17 */
    /* loaded from: classes3.dex */
    public class AnonymousClass17 extends AnimatorListenerAdapter {
        final /* synthetic */ Runnable val$onEnd;

        AnonymousClass17(Runnable runnable) {
            ChatAttachAlert.this = r1;
            this.val$onEnd = runnable;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
            SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
            springAnimation.getSpring().setDampingRatio(0.75f);
            springAnimation.getSpring().setStiffness(500.0f);
            springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$17$$ExternalSyntheticLambda1
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    ChatAttachAlert.AnonymousClass17.this.lambda$onAnimationEnd$0(dynamicAnimation, f, f2);
                }
            });
            final Runnable runnable = this.val$onEnd;
            springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$17$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    runnable.run();
                }
            });
            ChatAttachAlert.this.viewChangeAnimator = springAnimation;
            springAnimation.start();
        }

        public /* synthetic */ void lambda$onAnimationEnd$0(DynamicAnimation dynamicAnimation, float f, float f2) {
            if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout) {
                ChatAttachAlert.this.updateSelectedPosition(1);
            }
            ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
        }
    }

    public /* synthetic */ void lambda$showLayout$24(AttachAlertLayout attachAlertLayout, final Runnable runnable) {
        final float alpha = this.actionBar.getAlpha();
        final boolean z = this.nextAttachLayout.getCurrentItemTop() <= attachAlertLayout.getButtonsHideOffset();
        final float f = z ? 1.0f : 0.0f;
        SpringAnimation springAnimation = new SpringAnimation(new FloatValueHolder(0.0f));
        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda17
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f2, float f3) {
                ChatAttachAlert.this.lambda$showLayout$22(alpha, f, z, dynamicAnimation, f2, f3);
            }
        });
        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda16
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f2, float f3) {
                ChatAttachAlert.this.lambda$showLayout$23(z, runnable, dynamicAnimation, z2, f2, f3);
            }
        });
        springAnimation.setSpring(new SpringForce(500.0f));
        springAnimation.getSpring().setDampingRatio(1.0f);
        springAnimation.getSpring().setStiffness(1000.0f);
        springAnimation.start();
        this.viewChangeAnimator = springAnimation;
    }

    public /* synthetic */ void lambda$showLayout$22(float f, float f2, boolean z, DynamicAnimation dynamicAnimation, float f3, float f4) {
        float f5 = f3 / 500.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(f5));
        this.actionBar.setAlpha(AndroidUtilities.lerp(f, f2, f5));
        updateLayout(this.currentAttachLayout, false, 0);
        updateLayout(this.nextAttachLayout, false, 0);
        if (!(this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || z) {
            f5 = 1.0f - f5;
        }
        this.mediaPreviewView.setAlpha(f5);
        float f6 = 1.0f - f5;
        this.selectedView.setAlpha(f6);
        this.selectedView.setTranslationX(f5 * (-AndroidUtilities.dp(16.0f)));
        this.mediaPreviewView.setTranslationX(f6 * AndroidUtilities.dp(16.0f));
    }

    public /* synthetic */ void lambda$showLayout$23(boolean z, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        this.currentAttachLayout.onHideShowProgress(1.0f);
        this.nextAttachLayout.onHideShowProgress(1.0f);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.nextAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.containerView.invalidate();
        this.actionBar.setTag(z ? 1 : null);
        runnable.run();
    }

    public void updatePhotoPreview(boolean z) {
        if (z) {
            if (!this.canOpenPreview) {
                return;
            }
            if (this.photoPreviewLayout == null) {
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = new ChatAttachAlertPhotoLayoutPreview(this, getContext(), this.parentThemeDelegate);
                this.photoPreviewLayout = chatAttachAlertPhotoLayoutPreview;
                chatAttachAlertPhotoLayoutPreview.bringToFront();
            }
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            AttachAlertLayout attachAlertLayout2 = this.photoPreviewLayout;
            if (attachAlertLayout == attachAlertLayout2) {
                attachAlertLayout2 = this.photoLayout;
            }
            showLayout(attachAlertLayout2);
            return;
        }
        showLayout(this.photoLayout);
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout;
        if (i == 5 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
            openContactsLayout();
        } else if (i != 30 || (chatAttachAlertLocationLayout = this.locationLayout) == null || this.currentAttachLayout != chatAttachAlertLocationLayout || !isShowing()) {
        } else {
            this.locationLayout.openShareLiveLocation();
        }
    }

    private void openContactsLayout() {
        if (this.contactsLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = new ChatAttachAlertContactsLayout(this, getContext(), this.resourcesProvider);
            this.contactsLayout = chatAttachAlertContactsLayout;
            attachAlertLayoutArr[2] = chatAttachAlertContactsLayout;
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda33
                @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate
                public final void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i) {
                    ChatAttachAlert.this.lambda$openContactsLayout$25(tLRPC$User, z, i);
                }
            });
        }
        showLayout(this.contactsLayout);
    }

    public /* synthetic */ void lambda$openContactsLayout$25(TLRPC$User tLRPC$User, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendContact(tLRPC$User, z, i);
    }

    public void openAudioLayout(boolean z) {
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext(), this.resourcesProvider);
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlertAudioLayout.AudioSelectDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda32
                @Override // org.telegram.ui.Components.ChatAttachAlertAudioLayout.AudioSelectDelegate
                public final void didSelectAudio(ArrayList arrayList, CharSequence charSequence, boolean z2, int i) {
                    ChatAttachAlert.this.lambda$openAudioLayout$26(arrayList, charSequence, z2, i);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
            this.audioLayout.setMaxSelectedFiles(((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) ? -1 : 1);
        }
        if (z) {
            showLayout(this.audioLayout);
        }
    }

    public /* synthetic */ void lambda$openAudioLayout$26(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendAudio(arrayList, charSequence, z, i);
    }

    private void openDocumentsLayout(boolean z) {
        if (this.documentLayout == null) {
            int i = this.isSoundPicker ? 2 : 0;
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), i, this.resourcesProvider);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.18
                {
                    ChatAttachAlert.this = this;
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void didSelectFiles(ArrayList<String> arrayList, String str, ArrayList<MessageObject> arrayList2, boolean z2, int i2) {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) baseFragment).didSelectFiles(arrayList, str, arrayList2, z2, i2);
                    } else if (!(baseFragment instanceof PassportActivity)) {
                    } else {
                        ((PassportActivity) baseFragment).didSelectFiles(arrayList, str, z2, i2);
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z2, int i2) {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).didSelectPhotos(arrayList, z2, i2);
                    } else if (!(baseFragment instanceof PassportActivity)) {
                    } else {
                        ((PassportActivity) baseFragment).didSelectPhotos(arrayList, z2, i2);
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void startDocumentSelectActivity() {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) baseFragment).startDocumentSelectActivity();
                    } else if (!(baseFragment instanceof PassportActivity)) {
                    } else {
                        ((PassportActivity) baseFragment).startDocumentSelectActivity();
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void startMusicSelectActivity() {
                    ChatAttachAlert.this.openAudioLayout(true);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        int i2 = 1;
        if (baseFragment instanceof ChatActivity) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout2 = this.documentLayout;
            if ((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) {
                i2 = -1;
            }
            chatAttachAlertDocumentLayout2.setMaxSelectedFiles(i2);
        } else {
            this.documentLayout.setMaxSelectedFiles(this.maxSelectedPhotos);
            this.documentLayout.setCanSelectOnlyImageFiles(!this.isSoundPicker);
        }
        ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout3 = this.documentLayout;
        chatAttachAlertDocumentLayout3.isSoundPicker = this.isSoundPicker;
        if (z) {
            showLayout(chatAttachAlertDocumentLayout3);
        }
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        int i = 0;
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
            if (!this.isSoundPicker) {
                this.frameLayout2.setVisibility(0);
            }
            this.writeButtonContainer.setVisibility(0);
            if (!this.typeButtonsAvailable && !this.isSoundPicker) {
                this.shadow.setVisibility(0);
            }
        } else if (this.typeButtonsAvailable) {
            this.buttonsRecyclerView.setVisibility(0);
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
            FrameLayout frameLayout2 = this.writeButtonContainer;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property3, fArr3));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property4, fArr4));
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
                FrameLayout frameLayout5 = this.frameLayout2;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = z ? 0.0f : AndroidUtilities.dp(48.0f);
                arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property8, fArr8));
                View view4 = this.shadow;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = z ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f);
                arrayList.add(ObjectAnimator.ofFloat(view4, property9, fArr9));
                View view5 = this.shadow;
                Property property10 = View.ALPHA;
                float[] fArr10 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view5, property10, fArr10));
            } else if (this.typeButtonsAvailable) {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                Property property11 = View.TRANSLATION_Y;
                float[] fArr11 = new float[1];
                fArr11[0] = z ? AndroidUtilities.dp(36.0f) : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, property11, fArr11));
                View view6 = this.shadow;
                Property property12 = View.TRANSLATION_Y;
                float[] fArr12 = new float[1];
                if (z) {
                    f2 = AndroidUtilities.dp(36.0f);
                }
                fArr12[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view6, property12, fArr12));
            } else if (!this.isSoundPicker) {
                this.shadow.setTranslationY(AndroidUtilities.dp(36.0f) + this.botMainButtonOffsetY);
                View view7 = this.shadow;
                Property property13 = View.ALPHA;
                float[] fArr13 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr13[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view7, property13, fArr13));
            }
            this.commentsAnimator.playTogether(arrayList);
            this.commentsAnimator.setInterpolator(new DecelerateInterpolator());
            this.commentsAnimator.setDuration(180L);
            this.commentsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.19
                {
                    ChatAttachAlert.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.commentsAnimator)) {
                        if (!z) {
                            if (!ChatAttachAlert.this.isSoundPicker) {
                                ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            }
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                            if (!chatAttachAlert.typeButtonsAvailable && !chatAttachAlert.isSoundPicker) {
                                ChatAttachAlert.this.shadow.setVisibility(4);
                            }
                        } else {
                            ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                            if (chatAttachAlert2.typeButtonsAvailable && (chatAttachAlert2.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons())) {
                                ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                            }
                        }
                        ChatAttachAlert.this.commentsAnimator = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.commentsAnimator)) {
                        ChatAttachAlert.this.commentsAnimator = null;
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
            View view8 = this.selectedCountView;
            if (z) {
                f = 1.0f;
            }
            view8.setScaleY(f);
            this.selectedCountView.setAlpha(z ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(z ? 0.0f : AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((z ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f)) + this.botMainButtonOffsetY);
                View view9 = this.shadow;
                if (z) {
                    f2 = 1.0f;
                }
                view9.setAlpha(f2);
            } else if (this.typeButtonsAvailable) {
                AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
                if (attachAlertLayout == null || attachAlertLayout.shouldHideBottomButtons()) {
                    RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
                    if (z) {
                        f2 = AndroidUtilities.dp(36.0f);
                    }
                    recyclerListView2.setTranslationY(f2);
                }
                View view10 = this.shadow;
                if (z) {
                    i = AndroidUtilities.dp(36.0f);
                }
                view10.setTranslationY(i + this.botMainButtonOffsetY);
            } else {
                this.shadow.setTranslationY(AndroidUtilities.dp(36.0f) + this.botMainButtonOffsetY);
                View view11 = this.shadow;
                if (z) {
                    f2 = 1.0f;
                }
                view11.setAlpha(f2);
            }
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
                if (!this.typeButtonsAvailable) {
                    this.shadow.setVisibility(4);
                }
            }
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            SpringAnimation springAnimation = this.appearSpringAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            AnimatorSet animatorSet2 = this.buttonsAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        this.photoLayout.setTranslationX(0.0f);
        this.mediaPreviewView.setAlpha(0.0f);
        this.selectedView.setAlpha(1.0f);
        this.containerView.setTranslationY(this.containerView.getMeasuredHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        this.buttonsAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, 0.0f, 400.0f));
        this.buttonsAnimation.setDuration(400L);
        this.buttonsAnimation.setStartDelay(20L);
        this.ATTACH_ALERT_PROGRESS.set(this, Float.valueOf(0.0f));
        this.buttonsAnimation.start();
        ValueAnimator valueAnimator = this.navigationBarAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.navigationBarAlpha, 1.0f);
        this.navigationBarAnimation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$27(valueAnimator2);
            }
        });
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(0.75f);
        this.appearSpringAnimation.getSpring().setStiffness(350.0f);
        this.appearSpringAnimation.start();
        if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
            this.container.setLayerType(2, null);
        }
        this.currentSheetAnimationType = 1;
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.currentSheetAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[1];
        ColorDrawable colorDrawable = this.backDrawable;
        Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
        int[] iArr = new int[1];
        iArr[0] = this.dimBehind ? this.dimBehindAlpha : 0;
        animatorArr[0] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
        animatorSet2.playTogether(animatorArr);
        this.currentSheetAnimation.setDuration(400L);
        this.currentSheetAnimation.setStartDelay(20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        final BottomSheet.BottomSheetDelegateInterface bottomSheetDelegateInterface = super.delegate;
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$28(bottomSheetDelegateInterface);
            }
        };
        this.appearSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda15
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$29(runnable, dynamicAnimation, z, f, f2);
            }
        });
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.21
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator) || ChatAttachAlert.this.appearSpringAnimation == null || ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                    return;
                }
                runnable.run();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator)) {
                    return;
                }
                ((BottomSheet) ChatAttachAlert.this).currentSheetAnimation = null;
                ((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType = 0;
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        setNavBarAlpha(0.0f);
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$30(valueAnimator2);
            }
        });
        ofFloat2.setStartDelay(25L);
        ofFloat2.setDuration(200L);
        ofFloat2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat2.start();
        return true;
    }

    public /* synthetic */ void lambda$onCustomOpenAnimation$27(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BottomSheet.ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    public /* synthetic */ void lambda$onCustomOpenAnimation$28(BottomSheet.BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.currentSheetAnimation = null;
        this.appearSpringAnimation = null;
        this.currentSheetAnimationType = 0;
        if (bottomSheetDelegateInterface != null) {
            bottomSheetDelegateInterface.onOpenAnimationEnd();
        }
        if (this.useHardwareLayer) {
            this.container.setLayerType(0, null);
        }
        if (this.isFullscreen) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags &= -1025;
            getWindow().setAttributes(attributes);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
    }

    public /* synthetic */ void lambda$onCustomOpenAnimation$29(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet == null || animatorSet.isRunning()) {
            return;
        }
        runnable.run();
    }

    public /* synthetic */ void lambda$onCustomOpenAnimation$30(ValueAnimator valueAnimator) {
        setNavBarAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setNavBarAlpha(float f) {
        boolean z = false;
        this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), Math.min(255, Math.max(0, (int) (f * 255.0f))));
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
        Window window = getWindow();
        if (AndroidUtilities.computePerceivedBrightness(this.navBarColor) > 0.721d) {
            z = true;
        }
        AndroidUtilities.setLightNavigationBar(window, z);
        getContainer().invalidate();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.currentAttachLayout.onContainerViewTouchEvent(motionEvent);
    }

    public void makeFocusable(final EditTextBoldCursor editTextBoldCursor, final boolean z) {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null && !this.enterCommentEventSent) {
            boolean needEnterComment = chatAttachViewDelegate.needEnterComment();
            this.enterCommentEventSent = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.this.lambda$makeFocusable$32(editTextBoldCursor, z);
                }
            }, needEnterComment ? 200L : 0L);
        }
    }

    public /* synthetic */ void lambda$makeFocusable$32(final EditTextBoldCursor editTextBoldCursor, boolean z) {
        setFocusable(true);
        editTextBoldCursor.requestFocus();
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                }
            });
        }
    }

    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            AttachButton attachButton = (AttachButton) view;
            attachButton.textView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), getThemedColor(attachButton.textKey), attachButton.checkedState));
        } else if (!(view instanceof AttachBotButton)) {
        } else {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            attachBotButton.nameTextView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), attachBotButton.textColor, attachBotButton.checkedState));
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions;
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[i] != null && (themeDescriptions = attachAlertLayoutArr[i].getThemeDescriptions()) != null) {
                    arrayList.addAll(themeDescriptions);
                }
                i++;
            } else {
                arrayList.add(new ThemeDescription(this.container, 0, null, null, null, null, "dialogBackgroundGray"));
                return arrayList;
            }
        }
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(i2));
        }
        this.selectedTextView.setTextColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
        this.mediaPreviewTextView.setTextColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
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
            int i3 = 0;
            while (true) {
                ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.itemCells;
                if (i3 >= actionBarMenuSubItemArr.length) {
                    break;
                }
                if (actionBarMenuSubItemArr[i3] != null) {
                    actionBarMenuSubItemArr[i3].setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
                    this.itemCells[i3].setSelectorColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItemsSelector") : getThemedColor("dialogButtonSelector"));
                }
                i3++;
            }
            this.sendPopupLayout.setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupLayout.invalidate();
            }
        }
        String str = "dialogFloatingButton";
        Theme.setSelectorDrawableColor(this.writeButtonDrawable, getThemedColor(str), false);
        Drawable drawable = this.writeButtonDrawable;
        if (Build.VERSION.SDK_INT >= 21) {
            str = "dialogFloatingButtonPressed";
        }
        Theme.setSelectorDrawableColor(drawable, getThemedColor(str), true);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        this.actionBarShadow.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.buttonsRecyclerView.setGlowColor(getThemedColor("dialogScrollGlow"));
        String str2 = "voipgroup_listViewBackground";
        this.buttonsRecyclerView.setBackgroundColor(getThemedColor(this.forceDarkTheme ? str2 : "dialogBackground"));
        this.frameLayout2.setBackgroundColor(getThemedColor(this.forceDarkTheme ? str2 : "dialogBackground"));
        this.selectedCountView.invalidate();
        this.actionBar.setBackgroundColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBar") : getThemedColor("dialogBackground"));
        this.actionBar.setItemsColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItemsSelector") : getThemedColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(this.forceDarkTheme ? getThemedColor("voipgroup_actionBarItems") : getThemedColor("dialogTextBlack"));
        Drawable drawable2 = this.shadowDrawable;
        if (!this.forceDarkTheme) {
            str2 = "dialogBackground";
        }
        Theme.setDrawableColor(drawable2, getThemedColor(str2));
        this.containerView.invalidate();
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                return;
            }
            if (attachAlertLayoutArr[i] != null) {
                attachAlertLayoutArr[i].checkColors();
            }
            i++;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomMeasure(View view, int i, int i2) {
        return this.photoLayout.onCustomMeasure(view, i, i2);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
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
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[i] != null) {
                attachAlertLayoutArr[i].onResume();
            }
            i++;
        }
        if (isShowing()) {
            this.delegate.needEnterComment();
        }
    }

    public void onActivityResultFragment(int i, Intent intent, String str) {
        this.photoLayout.onActivityResultFragment(i, intent, str);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.reloadInlineHints || i == NotificationCenter.attachMenuBotsDidLoad) {
            ButtonsAdapter buttonsAdapter = this.buttonsAdapter;
            if (buttonsAdapter == null) {
                return;
            }
            buttonsAdapter.notifyDataSetChanged();
        } else if (i != NotificationCenter.currentUserPremiumStatusChanged) {
        } else {
            this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        }
    }

    public int getScrollOffsetY(int i) {
        AttachAlertLayout attachAlertLayout = this.nextAttachLayout;
        if (attachAlertLayout != null && ((this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (attachAlertLayout instanceof ChatAttachAlertPhotoLayoutPreview))) {
            int[] iArr = this.scrollOffsetY;
            return AndroidUtilities.lerp(iArr[0], iArr[1], this.translationProgress);
        }
        return this.scrollOffsetY[i];
    }

    public void updateSelectedPosition(int i) {
        int dp;
        int dp2;
        float f;
        int i2;
        int i3;
        AttachAlertLayout attachAlertLayout = i == 0 ? this.currentAttachLayout : this.nextAttachLayout;
        int scrollOffsetY = getScrollOffsetY(i);
        int i4 = scrollOffsetY - this.backgroundPaddingTop;
        if (attachAlertLayout == this.pollLayout) {
            dp = i4 - AndroidUtilities.dp(13.0f);
            dp2 = AndroidUtilities.dp(11.0f);
        } else {
            dp = i4 - AndroidUtilities.dp(39.0f);
            dp2 = AndroidUtilities.dp(43.0f);
        }
        float f2 = dp2;
        if (this.backgroundPaddingTop + dp < ActionBar.getCurrentActionBarHeight()) {
            f = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - dp) - this.backgroundPaddingTop) / f2);
            this.cornerRadius = 1.0f - f;
        } else {
            this.cornerRadius = 1.0f;
            f = 0.0f;
        }
        if (AndroidUtilities.isTablet()) {
            i2 = 16;
        } else {
            android.graphics.Point point = AndroidUtilities.displaySize;
            i2 = point.x > point.y ? 6 : 12;
        }
        float dp3 = this.actionBar.getAlpha() != 0.0f ? 0.0f : AndroidUtilities.dp((1.0f - this.headerView.getAlpha()) * 26.0f);
        if (this.menuShowed && this.avatarPicker == 0) {
            this.selectedMenuItem.setTranslationY((scrollOffsetY - AndroidUtilities.dp((i2 * f) + 37.0f)) + dp3 + this.currentPanTranslationY);
        } else {
            this.selectedMenuItem.setTranslationY(((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp(i2 + 37)) + this.currentPanTranslationY);
        }
        this.searchItem.setTranslationY(((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp(i2 + 37)) + this.currentPanTranslationY);
        FrameLayout frameLayout = this.headerView;
        float dp4 = (scrollOffsetY - AndroidUtilities.dp((i2 * f) + 25.0f)) + dp3 + this.currentPanTranslationY;
        this.baseSelectedTextViewTranslationY = dp4;
        frameLayout.setTranslationY(dp4);
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout == null || attachAlertLayout != chatAttachAlertPollLayout) {
            return;
        }
        if (AndroidUtilities.isTablet()) {
            i3 = 63;
        } else {
            android.graphics.Point point2 = AndroidUtilities.displaySize;
            i3 = point2.x > point2.y ? 53 : 59;
        }
        this.doneItem.setTranslationY(Math.max(0.0f, (this.pollLayout.getTranslationY() + scrollOffsetY) - AndroidUtilities.dp((i3 * f) + 7.0f)) + this.currentPanTranslationY);
    }

    /* JADX WARN: Code restructure failed: missing block: B:150:0x0047, code lost:
        if (((org.telegram.ui.ChatActivity) r0).allowSendGifs() != false) goto L115;
     */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:210:0x013f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateActionBarVisibility(final boolean r11, boolean r12) {
        /*
            Method dump skipped, instructions count: 408
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateActionBarVisibility(boolean, boolean):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:89:0x0063, code lost:
        if (((androidx.dynamicanimation.animation.SpringAnimation) r8).isRunning() != false) goto L33;
     */
    @android.annotation.SuppressLint({"NewApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateLayout(org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r7, boolean r8, int r9) {
        /*
            r6 = this;
            if (r7 != 0) goto L3
            return
        L3:
            int r0 = r7.getCurrentItemTop()
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto Ld
            return
        Ld:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r1 = r6.currentAttachLayout
            r2 = 1
            r3 = 0
            if (r7 != r1) goto L1b
            int r1 = r7.getButtonsHideOffset()
            if (r0 > r1) goto L1b
            r1 = 1
            goto L1c
        L1b:
            r1 = 0
        L1c:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r6.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = r6.photoPreviewLayout
            if (r4 == r5) goto L2d
            boolean r5 = r6.keyboardVisible
            if (r5 == 0) goto L2d
            if (r8 == 0) goto L2d
            boolean r5 = r4 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout
            if (r5 != 0) goto L2d
            r8 = 0
        L2d:
            if (r7 != r4) goto L32
            r6.updateActionBarVisibility(r1, r8)
        L32:
            android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            if (r8 != 0) goto L3c
            r8 = 0
            goto L3e
        L3c:
            int r8 = r8.topMargin
        L3e:
            r1 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r8 = r8 - r1
            int r0 = r0 + r8
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r8 = r6.currentAttachLayout
            if (r8 != r7) goto L4c
            r7 = 0
            goto L4d
        L4c:
            r7 = 1
        L4d:
            boolean r8 = r8 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r8 != 0) goto L57
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r8 = r6.nextAttachLayout
            boolean r8 = r8 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r8 == 0) goto L66
        L57:
            java.lang.Object r8 = r6.viewChangeAnimator
            boolean r1 = r8 instanceof androidx.dynamicanimation.animation.SpringAnimation
            if (r1 == 0) goto L66
            androidx.dynamicanimation.animation.SpringAnimation r8 = (androidx.dynamicanimation.animation.SpringAnimation) r8
            boolean r8 = r8.isRunning()
            if (r8 == 0) goto L66
            goto L67
        L66:
            r2 = 0
        L67:
            int[] r8 = r6.scrollOffsetY
            r1 = r8[r7]
            if (r1 != r0) goto L77
            if (r2 == 0) goto L70
            goto L77
        L70:
            if (r9 == 0) goto L85
            r7 = r8[r7]
            r6.previousScrollOffsetY = r7
            goto L85
        L77:
            r9 = r8[r7]
            r6.previousScrollOffsetY = r9
            r8[r7] = r0
            r6.updateSelectedPosition(r7)
            android.view.ViewGroup r7 = r6.containerView
            r7.invalidate()
        L85:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateLayout(org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout, boolean, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:181:0x0107  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x0116  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0125  */
    /* JADX WARN: Removed duplicated region for block: B:211:0x0163  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateCountButton(int r19) {
        /*
            Method dump skipped, instructions count: 473
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateCountButton(int):void");
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void init() {
        AttachAlertLayout attachAlertLayout;
        if (this.baseFragment == null) {
            return;
        }
        this.botButtonWasVisible = false;
        this.botButtonProgressWasVisible = false;
        this.botMainButtonOffsetY = 0.0f;
        this.botMainButtonTextView.setVisibility(8);
        this.botProgressView.setAlpha(0.0f);
        this.botProgressView.setScaleX(0.1f);
        this.botProgressView.setScaleY(0.1f);
        this.botProgressView.setVisibility(8);
        this.buttonsRecyclerView.setAlpha(1.0f);
        this.buttonsRecyclerView.setTranslationY(0.0f);
        for (int i = 0; i < this.botAttachLayouts.size(); i++) {
            this.botAttachLayouts.valueAt(i).setMeasureOffsetY(0);
        }
        this.shadow.setAlpha(1.0f);
        this.shadow.setTranslationY(0.0f);
        BaseFragment baseFragment = this.baseFragment;
        int i2 = 4;
        if ((baseFragment instanceof ChatActivity) && this.avatarPicker != 2) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
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
        if (this.isSoundPicker) {
            openDocumentsLayout(false);
            attachAlertLayout = this.documentLayout;
            this.selectedId = 4L;
        } else {
            MessageObject messageObject = this.editingMessageObject;
            if (messageObject != null && (messageObject.isMusic() || (this.editingMessageObject.isDocument() && !this.editingMessageObject.isGif()))) {
                if (this.editingMessageObject.isMusic()) {
                    openAudioLayout(false);
                    attachAlertLayout = this.audioLayout;
                    this.selectedId = 3L;
                } else {
                    openDocumentsLayout(false);
                    attachAlertLayout = this.documentLayout;
                    this.selectedId = 4L;
                }
                this.typeButtonsAvailable = !this.editingMessageObject.hasValidGroupId();
            } else {
                attachAlertLayout = this.photoLayout;
                this.typeButtonsAvailable = this.avatarPicker == 0;
                this.selectedId = 1L;
            }
        }
        this.buttonsRecyclerView.setVisibility(this.typeButtonsAvailable ? 0 : 8);
        this.shadow.setVisibility(this.typeButtonsAvailable ? 0 : 4);
        if (this.currentAttachLayout != attachAlertLayout) {
            if (this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
            }
            this.containerView.removeView(this.currentAttachLayout);
            this.currentAttachLayout.onHide();
            this.currentAttachLayout.setVisibility(8);
            this.currentAttachLayout.onHidden();
            this.currentAttachLayout = attachAlertLayout;
            setAllowNestedScroll(true);
            if (this.currentAttachLayout.getParent() == null) {
                this.containerView.addView(this.currentAttachLayout, 0, LayoutHelper.createFrame(-1, -1.0f));
            }
            attachAlertLayout.setAlpha(1.0f);
            attachAlertLayout.setVisibility(0);
            attachAlertLayout.onShow(null);
            attachAlertLayout.onShown();
            ActionBar actionBar = this.actionBar;
            if (attachAlertLayout.needsActionBar() != 0) {
                i2 = 0;
            }
            actionBar.setVisibility(i2);
            this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
        }
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (attachAlertLayout2 != chatAttachAlertPhotoLayout) {
            chatAttachAlertPhotoLayout.setCheckCameraWhenShown(true);
        }
        updateCountButton(0);
        this.buttonsAdapter.notifyDataSetChanged();
        this.commentTextView.setText("");
        this.buttonsLayoutManager.scrollToPositionWithOffset(0, 1000000);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        this.baseFragment = null;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
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
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
        this.openTransitionFinished = true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void setAllowDrawContent(boolean z) {
        super.setAllowDrawContent(z);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
    }

    public void setAvatarPicker(int i, boolean z) {
        this.avatarPicker = i;
        this.avatarSearch = z;
        if (i != 0) {
            this.typeButtonsAvailable = false;
            if (this.currentAttachLayout == null) {
                this.buttonsRecyclerView.setVisibility(8);
                this.shadow.setVisibility(8);
            }
            if (this.avatarPicker == 2) {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", R.string.ChoosePhotoOrVideo));
                return;
            } else {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
                return;
            }
        }
        this.typeButtonsAvailable = true;
    }

    public void setSoundPicker() {
        this.isSoundPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
        this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", R.string.ChoosePhotoOrVideo));
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        if (this.editingMessageObject != null) {
            return;
        }
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public void setOpenWithFrontFaceCamera(boolean z) {
        this.openWithFrontFaceCamera = z;
    }

    public ChatAttachAlertPhotoLayout getPhotoLayout() {
        return this.photoLayout;
    }

    /* loaded from: classes3.dex */
    public class ButtonsAdapter extends RecyclerListView.SelectionAdapter {
        private int attachBotsEndRow;
        private int attachBotsStartRow;
        private List<TLRPC$TL_attachMenuBot> attachMenuBots = new ArrayList();
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ButtonsAdapter(Context context) {
            ChatAttachAlert.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1817onCreateViewHolder(ViewGroup viewGroup, int i) {
            View attachButton;
            if (i == 0) {
                attachButton = new AttachButton(this.mContext);
            } else {
                attachButton = new AttachBotButton(this.mContext);
            }
            attachButton.setImportantForAccessibility(1);
            attachButton.setFocusable(true);
            return new RecyclerListView.Holder(attachButton);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType != 1) {
                    return;
                }
                AttachBotButton attachBotButton = (AttachBotButton) viewHolder.itemView;
                int i2 = this.attachBotsStartRow;
                if (i >= i2 && i < this.attachBotsEndRow) {
                    int i3 = i - i2;
                    attachBotButton.setTag(Integer.valueOf(i3));
                    TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = this.attachMenuBots.get(i3);
                    attachBotButton.setAttachBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(tLRPC$TL_attachMenuBot.bot_id)), tLRPC$TL_attachMenuBot);
                    return;
                }
                int i4 = i - this.buttonsCount;
                attachBotButton.setTag(Integer.valueOf(i4));
                attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i4).peer.user_id)));
                return;
            }
            AttachButton attachButton = (AttachButton) viewHolder.itemView;
            if (i == this.galleryButton) {
                attachButton.setTextAndIcon(1, LocaleController.getString("ChatGallery", R.string.ChatGallery), Theme.chat_attachButtonDrawables[0], "chat_attachGalleryBackground", "chat_attachGalleryText");
                attachButton.setTag(1);
            } else if (i == this.documentButton) {
                attachButton.setTextAndIcon(4, LocaleController.getString("ChatDocument", R.string.ChatDocument), Theme.chat_attachButtonDrawables[2], "chat_attachFileBackground", "chat_attachFileText");
                attachButton.setTag(4);
            } else if (i == this.locationButton) {
                attachButton.setTextAndIcon(6, LocaleController.getString("ChatLocation", R.string.ChatLocation), Theme.chat_attachButtonDrawables[4], "chat_attachLocationBackground", "chat_attachLocationText");
                attachButton.setTag(6);
            } else if (i == this.musicButton) {
                attachButton.setTextAndIcon(3, LocaleController.getString("AttachMusic", R.string.AttachMusic), Theme.chat_attachButtonDrawables[1], "chat_attachAudioBackground", "chat_attachAudioText");
                attachButton.setTag(3);
            } else if (i == this.pollButton) {
                attachButton.setTextAndIcon(9, LocaleController.getString("Poll", R.string.Poll), Theme.chat_attachButtonDrawables[5], "chat_attachPollBackground", "chat_attachPollText");
                attachButton.setTag(9);
            } else if (i != this.contactButton) {
            } else {
                attachButton.setTextAndIcon(5, LocaleController.getString("AttachContact", R.string.AttachContact), Theme.chat_attachButtonDrawables[3], "chat_attachContactBackground", "chat_attachContactText");
                attachButton.setTag(5);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            ChatAttachAlert.this.applyAttachButtonColors(viewHolder.itemView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.buttonsCount;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            return (chatAttachAlert.editingMessageObject != null || !(chatAttachAlert.baseFragment instanceof ChatActivity)) ? i : i + MediaDataController.getInstance(chatAttachAlert.currentAccount).inlineBots.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            this.attachBotsStartRow = -1;
            this.attachBotsEndRow = -1;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (!(chatAttachAlert.baseFragment instanceof ChatActivity)) {
                int i = 0 + 1;
                this.buttonsCount = i;
                this.galleryButton = 0;
                this.buttonsCount = i + 1;
                this.documentButton = i;
            } else {
                MessageObject messageObject = chatAttachAlert.editingMessageObject;
                if (messageObject == null) {
                    if (chatAttachAlert.mediaEnabled) {
                        int i2 = this.buttonsCount;
                        this.buttonsCount = i2 + 1;
                        this.galleryButton = i2;
                        BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                        if ((baseFragment instanceof ChatActivity) && !((ChatActivity) baseFragment).isInScheduleMode() && !((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat()) {
                            ChatActivity chatActivity = (ChatActivity) ChatAttachAlert.this.baseFragment;
                            this.attachBotsStartRow = this.buttonsCount;
                            this.attachMenuBots.clear();
                            Iterator<TLRPC$TL_attachMenuBot> it = MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).getAttachMenuBots().bots.iterator();
                            while (it.hasNext()) {
                                TLRPC$TL_attachMenuBot next = it.next();
                                if (MediaDataController.canShowAttachMenuBot(next, chatActivity.getCurrentChat() != null ? chatActivity.getCurrentChat() : chatActivity.getCurrentUser())) {
                                    this.attachMenuBots.add(next);
                                }
                            }
                            int size = this.buttonsCount + this.attachMenuBots.size();
                            this.buttonsCount = size;
                            this.attachBotsEndRow = size;
                        }
                        int i3 = this.buttonsCount;
                        this.buttonsCount = i3 + 1;
                        this.documentButton = i3;
                    }
                    int i4 = this.buttonsCount;
                    this.buttonsCount = i4 + 1;
                    this.locationButton = i4;
                    if (ChatAttachAlert.this.pollsEnabled) {
                        int i5 = this.buttonsCount;
                        this.buttonsCount = i5 + 1;
                        this.pollButton = i5;
                    } else {
                        int i6 = this.buttonsCount;
                        this.buttonsCount = i6 + 1;
                        this.contactButton = i6;
                    }
                    if (ChatAttachAlert.this.mediaEnabled) {
                        int i7 = this.buttonsCount;
                        this.buttonsCount = i7 + 1;
                        this.musicButton = i7;
                    }
                    BaseFragment baseFragment2 = ChatAttachAlert.this.baseFragment;
                    TLRPC$User currentUser = baseFragment2 instanceof ChatActivity ? ((ChatActivity) baseFragment2).getCurrentUser() : null;
                    if (currentUser != null && currentUser.bot) {
                        int i8 = this.buttonsCount;
                        this.buttonsCount = i8 + 1;
                        this.contactButton = i8;
                    }
                } else if ((messageObject.isMusic() || ChatAttachAlert.this.editingMessageObject.isDocument()) && ChatAttachAlert.this.editingMessageObject.hasValidGroupId()) {
                    if (ChatAttachAlert.this.editingMessageObject.isMusic()) {
                        int i9 = this.buttonsCount;
                        this.buttonsCount = i9 + 1;
                        this.musicButton = i9;
                    } else {
                        int i10 = this.buttonsCount;
                        this.buttonsCount = i10 + 1;
                        this.documentButton = i10;
                    }
                } else {
                    int i11 = this.buttonsCount;
                    int i12 = i11 + 1;
                    this.buttonsCount = i12;
                    this.galleryButton = i11;
                    int i13 = i12 + 1;
                    this.buttonsCount = i13;
                    this.documentButton = i12;
                    this.buttonsCount = i13 + 1;
                    this.musicButton = i13;
                }
            }
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i < this.buttonsCount) {
                return (i < this.attachBotsStartRow || i >= this.attachBotsEndRow) ? 0 : 1;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.doOnIdle(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.this.removeFromRoot();
                }
            });
        } else {
            removeFromRoot();
        }
    }

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
                updateActionBarVisibility(false, false);
                super.dismissInternal();
                return;
            }
        }
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        } else if (this.currentAttachLayout.onBackPressed()) {
        } else {
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
                this.commentTextView.hidePopup(true);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        this.currentAttachLayout.onDismissWithButtonClick(i);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithTouchOutside() {
        return this.currentAttachLayout.canDismissWithTouchOutside();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected void onDismissWithTouchOutside() {
        if (this.currentAttachLayout.onDismissWithTouchOutside()) {
            dismiss();
        }
    }

    public void dismiss(boolean z) {
        if (z) {
            this.allowPassConfirmationAlert = z;
        }
        dismiss();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (this.currentAttachLayout.onDismiss() || isDismissed()) {
            return;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        this.botAttachLayouts.clear();
        if (!this.allowPassConfirmationAlert && this.baseFragment != null && this.currentAttachLayout.getSelectedItemsCount() > 0) {
            if (this.confirmationAlertShown) {
                return;
            }
            this.confirmationAlertShown = true;
            AlertDialog create = new AlertDialog.Builder(this.baseFragment.getParentActivity(), this.parentThemeDelegate).setTitle(LocaleController.getString("DiscardSelectionAlertTitle", R.string.DiscardSelectionAlertTitle)).setMessage(LocaleController.getString("DiscardSelectionAlertMessage", R.string.DiscardSelectionAlertMessage)).setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda3
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatAttachAlert.this.lambda$dismiss$33(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatAttachAlert.this.lambda$dismiss$34(dialogInterface);
                }
            }).setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ChatAttachAlert.this.lambda$dismiss$35(dialogInterface);
                }
            }).create();
            create.show();
            TextView textView = (TextView) create.getButton(-1);
            if (textView == null) {
                return;
            }
            textView.setTextColor(getThemedColor("dialogTextRed2"));
            return;
        }
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[i] != null && this.currentAttachLayout != attachAlertLayoutArr[i]) {
                attachAlertLayoutArr[i].onDismiss();
            }
            i++;
        }
        AndroidUtilities.setNavigationBarColor(getWindow(), ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), 0), true, new AndroidUtilities.IntColorCallback() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda26
            @Override // org.telegram.messenger.AndroidUtilities.IntColorCallback
            public final void run(int i2) {
                ChatAttachAlert.this.lambda$dismiss$36(i2);
            }
        });
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
        this.captionLimitBulletinShown = false;
        super.dismiss();
        this.allowPassConfirmationAlert = false;
    }

    public /* synthetic */ void lambda$dismiss$33(DialogInterface dialogInterface, int i) {
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    public /* synthetic */ void lambda$dismiss$34(DialogInterface dialogInterface) {
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(1.5f);
        this.appearSpringAnimation.getSpring().setStiffness(1500.0f);
        this.appearSpringAnimation.start();
    }

    public /* synthetic */ void lambda$dismiss$35(DialogInterface dialogInterface) {
        this.confirmationAlertShown = false;
    }

    public /* synthetic */ void lambda$dismiss$36(int i) {
        this.navBarColorKey = null;
        this.navBarColor = i;
        this.containerView.invalidate();
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.currentAttachLayout.onSheetKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    public EditTextEmoji getCommentTextView() {
        return this.commentTextView;
    }

    public ChatAttachAlertDocumentLayout getDocumentLayout() {
        return this.documentLayout;
    }
}
