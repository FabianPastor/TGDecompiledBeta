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
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
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
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
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
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PaymentFormActivity;
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
    protected boolean allowPassConfirmationAlert;
    /* access modifiers changed from: private */
    public SpringAnimation appearSpringAnimation;
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
    public LongSparseArray<ChatAttachAlertBotWebViewLayout> botAttachLayouts;
    /* access modifiers changed from: private */
    public boolean botButtonProgressWasVisible;
    /* access modifiers changed from: private */
    public boolean botButtonWasVisible;
    /* access modifiers changed from: private */
    public float botMainButtonOffsetY;
    /* access modifiers changed from: private */
    public TextView botMainButtonTextView;
    /* access modifiers changed from: private */
    public RadialProgressView botProgressView;
    /* access modifiers changed from: private */
    public float bottomPannelTranslation;
    private boolean buttonPressed;
    /* access modifiers changed from: private */
    public ButtonsAdapter buttonsAdapter;
    private AnimatorSet buttonsAnimation;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    public boolean canOpenPreview;
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
    private boolean confirmationAlertShown;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius;
    protected int currentAccount;
    /* access modifiers changed from: private */
    public AttachAlertLayout currentAttachLayout;
    /* access modifiers changed from: private */
    public int currentLimit;
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
    protected FrameLayout headerView;
    protected boolean inBubbleMode;
    /* access modifiers changed from: private */
    public boolean isSoundPicker;
    private ActionBarMenuSubItem[] itemCells;
    private AttachAlertLayout[] layouts;
    /* access modifiers changed from: private */
    public ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos;
    /* access modifiers changed from: private */
    public boolean mediaEnabled;
    protected TextView mediaPreviewTextView;
    protected LinearLayout mediaPreviewView;
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
    public ChatActivity.ThemeDelegate parentThemeDelegate;
    protected boolean paused;
    /* access modifiers changed from: private */
    public ChatAttachAlertPhotoLayout photoLayout;
    /* access modifiers changed from: private */
    public ChatAttachAlertPhotoLayoutPreview photoPreviewLayout;
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
    protected ImageView selectedArrowImageView;
    /* access modifiers changed from: private */
    public View selectedCountView;
    /* access modifiers changed from: private */
    public long selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    protected LinearLayout selectedView;
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

    public void setCanOpenPreview(boolean canOpenPreview2) {
        this.canOpenPreview = canOpenPreview2;
        this.selectedArrowImageView.setVisibility((!canOpenPreview2 || this.avatarPicker == 2) ? 8 : 0);
    }

    public float getClipLayoutBottom() {
        return ((float) this.frameLayout2.getMeasuredHeight()) - (((float) (this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f))) * (1.0f - this.frameLayout2.getAlpha()));
    }

    public void showBotLayout(long id) {
        showBotLayout(id, (String) null);
    }

    public void showBotLayout(long id, String startCommand) {
        if ((this.botAttachLayouts.get(id) == null || !ColorUtils$$ExternalSyntheticBackport0.m(startCommand, this.botAttachLayouts.get(id).getStartCommand()) || this.botAttachLayouts.get(id).needReload()) && (this.baseFragment instanceof ChatActivity)) {
            final ChatAttachAlertBotWebViewLayout webViewLayout = new ChatAttachAlertBotWebViewLayout(this, getContext(), this.resourcesProvider);
            this.botAttachLayouts.put(id, webViewLayout);
            this.botAttachLayouts.get(id).setDelegate(new BotWebViewContainer.Delegate() {
                /* access modifiers changed from: private */
                public ValueAnimator botButtonAnimator;

                public /* synthetic */ void onSendWebViewData(String str) {
                    BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
                }

                public /* synthetic */ void onWebAppReady() {
                    BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
                }

                public void onCloseRequested(Runnable callback) {
                    if (ChatAttachAlert.this.currentAttachLayout == webViewLayout) {
                        ChatAttachAlert.this.setFocusable(false);
                        ChatAttachAlert.this.getWindow().setSoftInputMode(48);
                        ChatAttachAlert.this.dismiss();
                        AndroidUtilities.runOnUIThread(new ChatAttachAlert$1$$ExternalSyntheticLambda2(callback), 150);
                    }
                }

                static /* synthetic */ void lambda$onCloseRequested$0(Runnable callback) {
                    if (callback != null) {
                        callback.run();
                    }
                }

                public void onWebAppSetActionBarColor(String colorKey) {
                    int from = ((ColorDrawable) ChatAttachAlert.this.actionBar.getBackground()).getColor();
                    int to = ChatAttachAlert.this.getThemedColor(colorKey);
                    ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(200);
                    animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animator.addUpdateListener(new ChatAttachAlert$1$$ExternalSyntheticLambda1(this, from, to));
                    animator.start();
                }

                /* renamed from: lambda$onWebAppSetActionBarColor$1$org-telegram-ui-Components-ChatAttachAlert$1  reason: not valid java name */
                public /* synthetic */ void m746x676708a3(int from, int to, ValueAnimator animation) {
                    ChatAttachAlert.this.actionBar.setBackgroundColor(ColorUtils.blendARGB(from, to, ((Float) animation.getAnimatedValue()).floatValue()));
                }

                public void onWebAppSetBackgroundColor(int color) {
                    webViewLayout.setCustomBackground(color);
                }

                public void onWebAppOpenInvoice(String slug, TLObject response) {
                    BaseFragment parentFragment = ChatAttachAlert.this.baseFragment;
                    PaymentFormActivity paymentFormActivity = null;
                    if (response instanceof TLRPC.TL_payments_paymentForm) {
                        TLRPC.TL_payments_paymentForm form = (TLRPC.TL_payments_paymentForm) response;
                        MessagesController.getInstance(ChatAttachAlert.this.currentAccount).putUsers(form.users, false);
                        paymentFormActivity = new PaymentFormActivity(form, slug, parentFragment);
                    } else if (response instanceof TLRPC.TL_payments_paymentReceipt) {
                        paymentFormActivity = new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response);
                    }
                    if (paymentFormActivity != null) {
                        webViewLayout.scrollToTop();
                        AndroidUtilities.hideKeyboard(webViewLayout);
                        OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(parentFragment.getParentActivity(), ChatAttachAlert.this.resourcesProvider);
                        overlayActionBarLayoutDialog.show();
                        paymentFormActivity.setPaymentFormCallback(new ChatAttachAlert$1$$ExternalSyntheticLambda3(overlayActionBarLayoutDialog, webViewLayout, slug));
                        paymentFormActivity.setResourcesProvider(ChatAttachAlert.this.resourcesProvider);
                        overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
                    }
                }

                static /* synthetic */ void lambda$onWebAppOpenInvoice$2(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout webViewLayout, String slug, PaymentFormActivity.InvoiceStatus status) {
                    overlayActionBarLayoutDialog.dismiss();
                    webViewLayout.getWebViewContainer().onInvoiceStatusUpdate(slug, status.name().toLowerCase(Locale.ROOT));
                }

                public void onWebAppExpand() {
                    AttachAlertLayout access$000 = ChatAttachAlert.this.currentAttachLayout;
                    ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = webViewLayout;
                    if (access$000 == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.canExpandByRequest()) {
                        webViewLayout.scrollToTop();
                    }
                }

                public void onSetupMainButton(final boolean isVisible, boolean isActive, String text, int color, int textColor, final boolean isProgressVisible) {
                    AttachAlertLayout access$000 = ChatAttachAlert.this.currentAttachLayout;
                    ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = webViewLayout;
                    if (access$000 == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.isBotButtonAvailable()) {
                        ChatAttachAlert.this.botMainButtonTextView.setClickable(isActive);
                        ChatAttachAlert.this.botMainButtonTextView.setText(text);
                        ChatAttachAlert.this.botMainButtonTextView.setTextColor(textColor);
                        ChatAttachAlert.this.botMainButtonTextView.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(color));
                        float f = 0.0f;
                        float f2 = 1.0f;
                        if (ChatAttachAlert.this.botButtonWasVisible != isVisible) {
                            boolean unused = ChatAttachAlert.this.botButtonWasVisible = isVisible;
                            ValueAnimator valueAnimator = this.botButtonAnimator;
                            if (valueAnimator != null) {
                                valueAnimator.cancel();
                            }
                            float[] fArr = new float[2];
                            fArr[0] = isVisible ? 0.0f : 1.0f;
                            fArr[1] = isVisible ? 1.0f : 0.0f;
                            ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(250);
                            this.botButtonAnimator = duration;
                            duration.addUpdateListener(new ChatAttachAlert$1$$ExternalSyntheticLambda0(this));
                            this.botButtonAnimator.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationStart(Animator animation) {
                                    if (isVisible) {
                                        ChatAttachAlert.this.botMainButtonTextView.setAlpha(0.0f);
                                        ChatAttachAlert.this.botMainButtonTextView.setVisibility(0);
                                        int offsetY = AndroidUtilities.dp(36.0f);
                                        for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                                            ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(offsetY);
                                        }
                                        return;
                                    }
                                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(0.0f);
                                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(0);
                                }

                                public void onAnimationEnd(Animator animation) {
                                    if (!isVisible) {
                                        ChatAttachAlert.this.botMainButtonTextView.setVisibility(8);
                                    } else {
                                        ChatAttachAlert.this.buttonsRecyclerView.setVisibility(8);
                                    }
                                    int offsetY = isVisible ? AndroidUtilities.dp(36.0f) : 0;
                                    for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                                        ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(offsetY);
                                    }
                                    if (AnonymousClass1.this.botButtonAnimator == animation) {
                                        ValueAnimator unused = AnonymousClass1.this.botButtonAnimator = null;
                                    }
                                }
                            });
                            this.botButtonAnimator.start();
                        }
                        ChatAttachAlert.this.botProgressView.setProgressColor(textColor);
                        if (ChatAttachAlert.this.botButtonProgressWasVisible != isProgressVisible) {
                            ChatAttachAlert.this.botProgressView.animate().cancel();
                            if (isProgressVisible) {
                                ChatAttachAlert.this.botProgressView.setAlpha(0.0f);
                                ChatAttachAlert.this.botProgressView.setVisibility(0);
                            }
                            ViewPropertyAnimator animate = ChatAttachAlert.this.botProgressView.animate();
                            if (isProgressVisible) {
                                f = 1.0f;
                            }
                            ViewPropertyAnimator scaleX = animate.alpha(f).scaleX(isProgressVisible ? 1.0f : 0.1f);
                            if (!isProgressVisible) {
                                f2 = 0.1f;
                            }
                            scaleX.scaleY(f2).setDuration(250).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    boolean unused = ChatAttachAlert.this.botButtonProgressWasVisible = isProgressVisible;
                                    if (!isProgressVisible) {
                                        ChatAttachAlert.this.botProgressView.setVisibility(8);
                                    }
                                }
                            }).start();
                        }
                    }
                }

                /* renamed from: lambda$onSetupMainButton$3$org-telegram-ui-Components-ChatAttachAlert$1  reason: not valid java name */
                public /* synthetic */ void m745x94420554(ValueAnimator animation) {
                    float value = ((Float) animation.getAnimatedValue()).floatValue();
                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(1.0f - value);
                    ChatAttachAlert.this.botMainButtonTextView.setAlpha(value);
                    float unused = ChatAttachAlert.this.botMainButtonOffsetY = ((float) AndroidUtilities.dp(36.0f)) * value;
                    ChatAttachAlert.this.shadow.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
                    ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
                }

                public void onSetBackButtonVisible(boolean visible) {
                    AndroidUtilities.updateImageViewImageAnimated(ChatAttachAlert.this.actionBar.getBackButton(), visible ? NUM : NUM);
                }
            });
            MessageObject replyingObject = ((ChatActivity) this.baseFragment).getChatActivityEnterView().getReplyingMessageObject();
            this.botAttachLayouts.get(id).requestWebView(this.currentAccount, ((ChatActivity) this.baseFragment).getDialogId(), id, false, replyingObject != null ? replyingObject.messageOwner.id : 0, startCommand);
        }
        if (this.botAttachLayouts.get(id) != null) {
            this.botAttachLayouts.get(id).disallowSwipeOffsetAnimation();
            showLayout(this.botAttachLayouts.get(id), -id);
        }
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
            public static View $default$getRevealView(ChatAttachViewDelegate _this) {
                return null;
            }

            public static void $default$didSelectBot(ChatAttachViewDelegate _this, TLRPC.User user) {
            }

            public static boolean $default$needEnterComment(ChatAttachViewDelegate _this) {
                return false;
            }

            public static void $default$doOnIdle(ChatAttachViewDelegate _this, Runnable runnable) {
                runnable.run();
            }

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
        public boolean hasCustomBackground() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public int getCustomBackground() {
            return 0;
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
        public void applyCaption(CharSequence text) {
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
        public void onShow(AttachAlertLayout previousLayout) {
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

        /* access modifiers changed from: package-private */
        public boolean shouldHideBottomButtons() {
            return true;
        }

        public void onPanTransitionStart(boolean keyboardVisible, int contentHeight) {
        }

        public void onPanTransitionEnd() {
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
            setFocusable(true);
            AnonymousClass1 r1 = new RLottieImageView(context, ChatAttachAlert.this) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    AttachButton.this.invalidate();
                }
            };
            this.imageView = r1;
            r1.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(32, 32.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            this.textView.setImportantForAccessibility(2);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(this.textView.getText());
            info.setEnabled(true);
            info.setSelected(this.checked);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean animate) {
            if (this.checked != (((long) this.currentId) == ChatAttachAlert.this.selectedId)) {
                this.checked = ((long) this.currentId) == ChatAttachAlert.this.selectedId;
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
            float cx = ((float) this.imageView.getLeft()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
            float cy = ((float) this.imageView.getTop()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
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
        /* access modifiers changed from: private */
        public TLRPC.TL_attachMenuBot attachMenuBot;
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private ValueAnimator checkAnimator;
        private Boolean checked;
        /* access modifiers changed from: private */
        public float checkedState;
        /* access modifiers changed from: private */
        public TLRPC.User currentUser;
        private int iconBackgroundColor;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        /* access modifiers changed from: private */
        public TextView nameTextView;
        private View selector;
        /* access modifiers changed from: private */
        public int textColor;

        public AttachBotButton(Context context) {
            super(context);
            setWillNotDraw(false);
            setFocusable(true);
            setFocusableInTouchMode(true);
            AnonymousClass1 r1 = new BackupImageView(context, ChatAttachAlert.this) {
                {
                    this.imageReceiver.setDelegate(ChatAttachAlert$AttachBotButton$1$$ExternalSyntheticLambda0.INSTANCE);
                }

                static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver1, boolean set, boolean thumb, boolean memCache) {
                    Drawable drawable = imageReceiver1.getDrawable();
                    if (drawable instanceof RLottieDrawable) {
                        ((RLottieDrawable) drawable).setCustomEndFrame(0);
                        ((RLottieDrawable) drawable).stop();
                        ((RLottieDrawable) drawable).setProgress(0.0f, false);
                    }
                }

                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    AttachBotButton.this.invalidate();
                }
            };
            this.imageView = r1;
            r1.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View view = new View(context);
                this.selector = view;
                view.setBackground(Theme.createSelectorDrawable(ChatAttachAlert.this.getThemedColor("dialogButtonSelector"), 1, AndroidUtilities.dp(23.0f)));
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.selector != null && this.checked.booleanValue()) {
                info.setCheckable(true);
                info.setChecked(true);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            this.imageView.setScaleX(1.0f - (state * 0.06f));
            this.imageView.setScaleY(1.0f - (0.06f * state));
            this.nameTextView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), this.textColor, this.checkedState));
            invalidate();
        }

        private void updateMargins() {
            ((ViewGroup.MarginLayoutParams) this.nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 62.0f : 60.0f);
            ((ViewGroup.MarginLayoutParams) this.imageView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 11.0f : 9.0f);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.attachMenuBot != null) {
                float scale = (this.checkedState * 0.06f) + this.imageView.getScaleX();
                float radius = ((float) AndroidUtilities.dp(23.0f)) * scale;
                float cx = ((float) this.imageView.getLeft()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
                float cy = ((float) this.imageView.getTop()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
                ChatAttachAlert.this.attachButtonPaint.setColor(this.iconBackgroundColor);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
                ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(((float) AndroidUtilities.dp(3.0f)) * scale);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(cx, cy, radius - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, radius - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean animate) {
            boolean newChecked = this.attachMenuBot != null && (-this.currentUser.id) == ChatAttachAlert.this.selectedId;
            Boolean bool = this.checked;
            if (bool == null || bool.booleanValue() != newChecked || !animate) {
                this.checked = Boolean.valueOf(newChecked);
                ValueAnimator valueAnimator = this.checkAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                RLottieDrawable drawable = this.imageView.getImageReceiver().getLottieAnimation();
                float f = 1.0f;
                if (animate) {
                    if (this.checked.booleanValue() && drawable != null) {
                        drawable.setAutoRepeat(0);
                        drawable.setCustomEndFrame(-1);
                        drawable.setProgress(0.0f, false);
                        drawable.start();
                    }
                    float[] fArr = new float[2];
                    fArr[0] = this.checked.booleanValue() ? 0.0f : 1.0f;
                    if (!this.checked.booleanValue()) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.checkAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ChatAttachAlert$AttachBotButton$$ExternalSyntheticLambda0(this));
                    this.checkAnimator.setDuration(200);
                    this.checkAnimator.start();
                    return;
                }
                if (drawable != null) {
                    drawable.stop();
                    drawable.setProgress(0.0f, false);
                }
                if (!this.checked.booleanValue()) {
                    f = 0.0f;
                }
                setCheckedState(f);
            }
        }

        /* renamed from: lambda$updateCheckedState$0$org-telegram-ui-Components-ChatAttachAlert$AttachBotButton  reason: not valid java name */
        public /* synthetic */ void m751x7fcd93d6(ValueAnimator animation) {
            setCheckedState(((Float) animation.getAnimatedValue()).floatValue());
        }

        public void setUser(TLRPC.User user) {
            if (user != null) {
                this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
                this.currentUser = user;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
                this.imageView.setSize(-1, -1);
                this.imageView.setColorFilter((ColorFilter) null);
                this.attachMenuBot = null;
                this.selector.setVisibility(0);
                updateMargins();
                setCheckedState(0.0f);
                invalidate();
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0071, code lost:
            if (r5.equals("light_icon") != false) goto L_0x0089;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setAttachBot(org.telegram.tgnet.TLRPC.User r12, org.telegram.tgnet.TLRPC.TL_attachMenuBot r13) {
            /*
                r11 = this;
                if (r12 == 0) goto L_0x013d
                if (r13 != 0) goto L_0x0006
                goto L_0x013d
            L_0x0006:
                android.widget.TextView r0 = r11.nameTextView
                org.telegram.ui.Components.ChatAttachAlert r1 = org.telegram.ui.Components.ChatAttachAlert.this
                java.lang.String r2 = "dialogTextGray2"
                int r1 = r1.getThemedColor(r2)
                r0.setTextColor(r1)
                r11.currentUser = r12
                android.widget.TextView r0 = r11.nameTextView
                java.lang.String r1 = r13.short_name
                r0.setText(r1)
                org.telegram.ui.Components.AvatarDrawable r0 = r11.avatarDrawable
                r0.setInfo((org.telegram.tgnet.TLRPC.User) r12)
                r0 = 1
                org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon r1 = org.telegram.messenger.MediaDataController.getAnimatedAttachMenuBotIcon(r13)
                if (r1 != 0) goto L_0x002d
                org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon r1 = org.telegram.messenger.MediaDataController.getStaticAttachMenuBotIcon(r13)
                r0 = 0
            L_0x002d:
                if (r1 == 0) goto L_0x0106
                org.telegram.ui.Components.ChatAttachAlert r2 = org.telegram.ui.Components.ChatAttachAlert.this
                java.lang.String r3 = "chat_attachContactText"
                int r2 = r2.getThemedColor(r3)
                r11.textColor = r2
                org.telegram.ui.Components.ChatAttachAlert r2 = org.telegram.ui.Components.ChatAttachAlert.this
                java.lang.String r3 = "chat_attachContactBackground"
                int r2 = r2.getThemedColor(r3)
                r11.iconBackgroundColor = r2
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_attachMenuBotIconColor> r2 = r1.colors
                java.util.Iterator r2 = r2.iterator()
            L_0x0049:
                boolean r3 = r2.hasNext()
                r4 = 0
                if (r3 == 0) goto L_0x00c9
                java.lang.Object r3 = r2.next()
                org.telegram.tgnet.TLRPC$TL_attachMenuBotIconColor r3 = (org.telegram.tgnet.TLRPC.TL_attachMenuBotIconColor) r3
                java.lang.String r5 = r3.name
                r6 = -1
                int r7 = r5.hashCode()
                switch(r7) {
                    case -1852424286: goto L_0x007e;
                    case -1852094378: goto L_0x0074;
                    case -208896510: goto L_0x006b;
                    case -208566602: goto L_0x0061;
                    default: goto L_0x0060;
                }
            L_0x0060:
                goto L_0x0088
            L_0x0061:
                java.lang.String r4 = "light_text"
                boolean r4 = r5.equals(r4)
                if (r4 == 0) goto L_0x0060
                r4 = 1
                goto L_0x0089
            L_0x006b:
                java.lang.String r7 = "light_icon"
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x0060
                goto L_0x0089
            L_0x0074:
                java.lang.String r4 = "dark_text"
                boolean r4 = r5.equals(r4)
                if (r4 == 0) goto L_0x0060
                r4 = 3
                goto L_0x0089
            L_0x007e:
                java.lang.String r4 = "dark_icon"
                boolean r4 = r5.equals(r4)
                if (r4 == 0) goto L_0x0060
                r4 = 2
                goto L_0x0089
            L_0x0088:
                r4 = -1
            L_0x0089:
                switch(r4) {
                    case 0: goto L_0x00ba;
                    case 1: goto L_0x00ab;
                    case 2: goto L_0x009c;
                    case 3: goto L_0x008d;
                    default: goto L_0x008c;
                }
            L_0x008c:
                goto L_0x00c8
            L_0x008d:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
                boolean r4 = r4.isDark()
                if (r4 == 0) goto L_0x00c8
                int r4 = r3.color
                r11.textColor = r4
                goto L_0x00c8
            L_0x009c:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
                boolean r4 = r4.isDark()
                if (r4 == 0) goto L_0x00c8
                int r4 = r3.color
                r11.iconBackgroundColor = r4
                goto L_0x00c8
            L_0x00ab:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
                boolean r4 = r4.isDark()
                if (r4 != 0) goto L_0x00c8
                int r4 = r3.color
                r11.textColor = r4
                goto L_0x00c8
            L_0x00ba:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
                boolean r4 = r4.isDark()
                if (r4 != 0) goto L_0x00c8
                int r4 = r3.color
                r11.iconBackgroundColor = r4
            L_0x00c8:
                goto L_0x0049
            L_0x00c9:
                int r2 = r11.textColor
                r3 = 255(0xff, float:3.57E-43)
                int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r3)
                r11.textColor = r2
                int r2 = r11.iconBackgroundColor
                int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r3)
                r11.iconBackgroundColor = r2
                org.telegram.tgnet.TLRPC$Document r2 = r1.icon
                org.telegram.ui.Components.BackupImageView r3 = r11.imageView
                org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
                r3.setAllowStartLottieAnimation(r4)
                org.telegram.ui.Components.BackupImageView r5 = r11.imageView
                org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r2)
                long r3 = r13.bot_id
                java.lang.String r7 = java.lang.String.valueOf(r3)
                if (r0 == 0) goto L_0x00f7
                java.lang.String r3 = "tgs"
                goto L_0x00f9
            L_0x00f7:
                java.lang.String r3 = "svg"
            L_0x00f9:
                r8 = r3
                r3 = 1065353216(0x3var_, float:1.0)
                java.lang.String r4 = "windowBackgroundGray"
                org.telegram.messenger.SvgHelper$SvgDrawable r9 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC.Document) r2, (java.lang.String) r4, (float) r3)
                r10 = r13
                r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10)
            L_0x0106:
                org.telegram.ui.Components.BackupImageView r2 = r11.imageView
                r3 = 1105199104(0x41e00000, float:28.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.setSize(r4, r3)
                org.telegram.ui.Components.BackupImageView r2 = r11.imageView
                android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
                org.telegram.ui.Components.ChatAttachAlert r4 = org.telegram.ui.Components.ChatAttachAlert.this
                java.lang.String r5 = "chat_attachContactIcon"
                int r4 = r4.getThemedColor(r5)
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.SRC_IN
                r3.<init>(r4, r5)
                r2.setColorFilter(r3)
                r11.attachMenuBot = r13
                android.view.View r2 = r11.selector
                r3 = 8
                r2.setVisibility(r3)
                r11.updateMargins()
                r2 = 0
                r11.setCheckedState(r2)
                r11.invalidate()
                return
            L_0x013d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AttachBotButton.setAttachBot(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_attachMenuBot):void");
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
            r7.canOpenPreview = r13
            r7.isSoundPicker = r13
            r14 = 0
            r7.translationProgress = r14
            org.telegram.ui.Components.ChatAttachAlert$2 r0 = new org.telegram.ui.Components.ChatAttachAlert$2
            java.lang.String r1 = "translation"
            r0.<init>(r1)
            r7.ATTACH_ALERT_LAYOUT_TRANSLATION = r0
            r0 = 7
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = new org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout[r0]
            r7.layouts = r0
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r7.botAttachLayouts = r0
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
            r7.botButtonProgressWasVisible = r13
            r7.botButtonWasVisible = r13
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
            org.telegram.ui.Components.ChatAttachAlert$20 r0 = new org.telegram.ui.Components.ChatAttachAlert$20
            java.lang.String r1 = "openProgress"
            r0.<init>(r1)
            r7.ATTACH_ALERT_PROGRESS = r0
            r7.confirmationAlertShown = r13
            r7.allowPassConfirmationAlert = r13
            r7.forceDarkTheme = r10
            r7.showingFromDialog = r11
            r7.drawNavigationBar = r15
            boolean r0 = r9 instanceof org.telegram.ui.ChatActivity
            if (r0 == 0) goto L_0x00a3
            boolean r0 = r36.isInBubbleMode()
            if (r0 == 0) goto L_0x00a3
            r0 = 1
            goto L_0x00a4
        L_0x00a3:
            r0 = 0
        L_0x00a4:
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
            int r0 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.attachMenuBotsDidLoad
            r0.addObserver(r7, r1)
            int r0 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.currentUserPremiumStatusChanged
            r0.addObserver(r7, r1)
            java.util.ArrayList<android.graphics.Rect> r0 = r7.exclusionRects
            android.graphics.Rect r1 = r7.exclustionRect
            r0.add(r1)
            org.telegram.ui.Components.ChatAttachAlert$3 r0 = new org.telegram.ui.Components.ChatAttachAlert$3
            r0.<init>(r8, r10)
            r7.sizeNotifierFrameLayout = r0
            org.telegram.ui.Components.ChatAttachAlert$4 r1 = new org.telegram.ui.Components.ChatAttachAlert$4
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.sizeNotifierFrameLayout
            r7.containerView = r0
            android.view.ViewGroup r0 = r7.containerView
            r0.setWillNotDraw(r13)
            android.view.ViewGroup r0 = r7.containerView
            r0.setClipChildren(r13)
            android.view.ViewGroup r0 = r7.containerView
            r0.setClipToPadding(r13)
            android.view.ViewGroup r0 = r7.containerView
            int r1 = r7.backgroundPaddingLeft
            int r2 = r7.backgroundPaddingLeft
            r0.setPadding(r1, r13, r2, r13)
            org.telegram.ui.Components.ChatAttachAlert$5 r0 = new org.telegram.ui.Components.ChatAttachAlert$5
            r0.<init>(r8, r12)
            r7.actionBar = r0
            java.lang.String r1 = "dialogBackground"
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131165449(0x7var_, float:1.7945115E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.String r3 = "dialogTextBlack"
            int r1 = r7.getThemedColor(r3)
            r0.setItemsColor(r1, r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.String r2 = "dialogButtonSelector"
            int r1 = r7.getThemedColor(r2)
            r0.setItemsBackgroundColor(r1, r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            int r1 = r7.getThemedColor(r3)
            r0.setTitleColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r0.setOccupyStatusBar(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r0.setAlpha(r14)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.Components.ChatAttachAlert$6 r1 = new org.telegram.ui.Components.ChatAttachAlert$6
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r16 = 0
            r17 = 0
            int r18 = r7.getThemedColor(r3)
            r19 = 0
            r0 = r1
            r15 = r1
            r1 = r35
            r20 = r2
            r2 = r16
            r21 = r3
            r3 = r17
            r4 = r18
            r5 = r19
            r6 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.selectedMenuItem = r15
            r15.setLongClickEnabled(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 2131165453(0x7var_d, float:1.7945124E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r1 = 2131624003(0x7f0e0043, float:1.8875173E38)
            java.lang.String r2 = "AccDescrMoreOptions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r15 = 4
            r0.setVisibility(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r0.setAlpha(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            r6 = 2
            r0.setSubMenuOpenSide(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda19 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda19
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
            r5 = r20
            int r1 = r7.getThemedColor(r5)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.selectedMenuItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda34 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda34
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r3 = 0
            java.lang.String r0 = "windowBackgroundWhiteBlueHeader"
            int r16 = r7.getThemedColor(r0)
            r17 = 1
            r0 = r4
            r1 = r35
            r14 = r4
            r4 = r16
            r25 = r5
            r5 = r17
            r6 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.doneItem = r14
            r14.setLongClickEnabled(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r1 = 2131625268(0x7f0e0534, float:1.887774E38)
            java.lang.String r2 = "Create"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r0.setVisibility(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r14 = r25
            int r1 = r7.getThemedColor(r14)
            r2 = 3
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r5 = r21
            int r4 = r7.getThemedColor(r5)
            r16 = 0
            r0 = r6
            r1 = r35
            r27 = r5
            r5 = r16
            r15 = r6
            r6 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.searchItem = r15
            r15.setLongClickEnabled(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 2131165456(0x7var_, float:1.794513E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            r1 = 2131628092(0x7f0e103c, float:1.8883467E38)
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
            int r1 = r7.getThemedColor(r14)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda3 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda3
            r1.<init>(r7, r11)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$8 r0 = new org.telegram.ui.Components.ChatAttachAlert$8
            r0.<init>(r8)
            r7.headerView = r0
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            android.widget.FrameLayout r0 = r7.headerView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.FrameLayout r0 = r7.headerView
            r1 = 4
            r0.setVisibility(r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.selectedView = r0
            r0.setOrientation(r13)
            android.widget.LinearLayout r0 = r7.selectedView
            r1 = 16
            r0.setGravity(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.selectedTextView = r0
            r2 = r27
            int r3 = r7.getThemedColor(r2)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r7.selectedTextView
            r3 = 1098907648(0x41800000, float:16.0)
            r4 = 1
            r0.setTextSize(r4, r3)
            android.widget.TextView r0 = r7.selectedTextView
            java.lang.String r14 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r4)
            android.widget.TextView r0 = r7.selectedTextView
            r4 = 19
            r0.setGravity(r4)
            android.widget.LinearLayout r0 = r7.selectedView
            android.widget.TextView r4 = r7.selectedTextView
            r5 = -2
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r5, (int) r1)
            r0.addView(r4, r6)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.selectedArrowImageView = r0
            android.content.Context r0 = r34.getContext()
            android.content.res.Resources r0 = r0.getResources()
            r4 = 2131165262(0x7var_e, float:1.7944736E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r15 = r0.mutate()
            android.graphics.PorterDuffColorFilter r0 = new android.graphics.PorterDuffColorFilter
            int r4 = r7.getThemedColor(r2)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r0.<init>(r4, r6)
            r15.setColorFilter(r0)
            android.widget.ImageView r0 = r7.selectedArrowImageView
            r0.setImageDrawable(r15)
            android.widget.ImageView r0 = r7.selectedArrowImageView
            r4 = 8
            r0.setVisibility(r4)
            android.widget.LinearLayout r0 = r7.selectedView
            android.widget.ImageView r6 = r7.selectedArrowImageView
            r27 = -2
            r28 = -2
            r29 = 16
            r30 = 4
            r31 = 1
            r32 = 0
            r33 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r6, r4)
            android.widget.LinearLayout r0 = r7.selectedView
            r4 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r4)
            android.widget.FrameLayout r0 = r7.headerView
            android.widget.LinearLayout r6 = r7.selectedView
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r0.addView(r6, r3)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.mediaPreviewView = r0
            r0.setOrientation(r13)
            android.widget.LinearLayout r0 = r7.mediaPreviewView
            r0.setGravity(r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r6 = r0
            android.content.Context r0 = r34.getContext()
            android.content.res.Resources r0 = r0.getResources()
            r3 = 2131165261(0x7var_d, float:1.7944734E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r3)
            android.graphics.drawable.Drawable r3 = r0.mutate()
            android.graphics.PorterDuffColorFilter r0 = new android.graphics.PorterDuffColorFilter
            int r13 = r7.getThemedColor(r2)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r0.<init>(r13, r4)
            r3.setColorFilter(r0)
            r6.setImageDrawable(r3)
            android.widget.LinearLayout r0 = r7.mediaPreviewView
            r30 = 0
            r32 = 4
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r6, r4)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.mediaPreviewTextView = r0
            int r2 = r7.getThemedColor(r2)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r7.mediaPreviewTextView
            r2 = 1098907648(0x41800000, float:16.0)
            r4 = 1
            r0.setTextSize(r4, r2)
            android.widget.TextView r0 = r7.mediaPreviewTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r7.mediaPreviewTextView
            r2 = 19
            r0.setGravity(r2)
            android.widget.TextView r0 = r7.mediaPreviewTextView
            r2 = 2131624495(0x7f0e022f, float:1.8876171E38)
            java.lang.String r4 = "AttachMediaPreview"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setText(r2)
            android.widget.LinearLayout r0 = r7.mediaPreviewView
            r2 = 0
            r0.setAlpha(r2)
            android.widget.LinearLayout r0 = r7.mediaPreviewView
            android.widget.TextView r2 = r7.mediaPreviewTextView
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r5, (int) r1)
            r0.addView(r2, r1)
            android.widget.FrameLayout r0 = r7.headerView
            android.widget.LinearLayout r1 = r7.mediaPreviewView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r2)
            r0.addView(r1, r4)
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = r7.layouts
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout
            r1.<init>(r7, r8, r10, r12)
            r7.photoLayout = r1
            r2 = 0
            r0[r2] = r1
            r0 = 0
            r1.setTranslationX(r0)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r0 = r7.photoLayout
            r7.currentAttachLayout = r0
            r0 = 1
            r7.selectedId = r0
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = r7.photoLayout
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r2)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.FrameLayout r1 = r7.headerView
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
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r2)
            r0.addView(r1, r2)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.selectedMenuItem
            r2 = 48
            r13 = 53
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r13)
            r0.addView(r1, r4)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.searchItem
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r13)
            r0.addView(r1, r4)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.doneItem
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r2, (int) r13)
            r0.addView(r1, r4)
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
            r4 = 1065353216(0x3var_, float:1.0)
            r13 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r4)
            r0.addView(r1, r4)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.shadow = r0
            r1 = 2131165265(0x7var_, float:1.7944742E38)
            r0.setBackgroundResource(r1)
            android.view.View r0 = r7.shadow
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r4, r13)
            r0.setColorFilter(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.view.View r1 = r7.shadow
            r28 = 1073741824(0x40000000, float:2.0)
            r29 = 83
            r30 = 0
            r32 = 0
            r33 = 1118306304(0x42a80000, float:84.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r4)
            org.telegram.ui.Components.ChatAttachAlert$9 r0 = new org.telegram.ui.Components.ChatAttachAlert$9
            r0.<init>(r8)
            r7.buttonsRecyclerView = r0
            org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter
            r1.<init>(r8)
            r7.buttonsAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r4 = 0
            r1.<init>(r8, r4, r4)
            r7.buttonsLayoutManager = r1
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r0.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r0.setHorizontalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r13 = 0
            r0.setItemAnimator(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r0.setLayoutAnimation(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = r7.getThemedColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            java.lang.String r1 = "dialogBackground"
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            r1 = 1
            r0.setImportantForAccessibility(r1)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r7.buttonsRecyclerView
            r4 = 84
            r13 = 83
            r5 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r4, (int) r13)
            r0.addView(r1, r4)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda28 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda28
            r1.<init>(r7, r12)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.buttonsRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda29 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda29
            r1.<init>(r7)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.botMainButtonTextView = r0
            r1 = 8
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.botMainButtonTextView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.TextView r0 = r7.botMainButtonTextView
            r0.setSingleLine()
            android.widget.TextView r0 = r7.botMainButtonTextView
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r7.botMainButtonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r1)
            r0 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.widget.TextView r0 = r7.botMainButtonTextView
            r1 = 0
            r0.setPadding(r5, r1, r5, r1)
            android.widget.TextView r0 = r7.botMainButtonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r0.setTextSize(r4, r1)
            android.widget.TextView r0 = r7.botMainButtonTextView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda0
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            android.view.ViewGroup r0 = r7.containerView
            android.widget.TextView r1 = r7.botMainButtonTextView
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r2, (int) r13)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r8)
            r7.botProgressView = r0
            r1 = 1099956224(0x41900000, float:18.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setSize(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r7.botProgressView
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r7.botProgressView
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r7.botProgressView
            r0.setScaleY(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r7.botProgressView
            r1 = 8
            r0.setVisibility(r1)
            android.view.ViewGroup r0 = r7.containerView
            org.telegram.ui.Components.RadialProgressView r1 = r7.botProgressView
            r27 = 28
            r28 = 1105199104(0x41e00000, float:28.0)
            r29 = 85
            r32 = 1092616192(0x41200000, float:10.0)
            r33 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$10 r0 = new org.telegram.ui.Components.ChatAttachAlert$10
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
            r2 = -1
            r4 = -2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r4, (int) r13)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r7.frameLayout2
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda5 r1 = org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda5.INSTANCE
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
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r1)
            r1 = 1
            r0.setCenterAlign(r1)
            android.widget.FrameLayout r1 = r7.frameLayout2
            r27 = 56
            r28 = 1101004800(0x41a00000, float:20.0)
            r30 = 1077936128(0x40400000, float:3.0)
            r32 = 1096810496(0x41600000, float:14.0)
            r33 = 1117519872(0x429CLASSNAME, float:78.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r1.addView(r0, r2)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.getCaptionMaxLengthLimit()
            r7.currentLimit = r0
            org.telegram.ui.Components.ChatAttachAlert$11 r13 = new org.telegram.ui.Components.ChatAttachAlert$11
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r7.sizeNotifierFrameLayout
            r17 = 0
            r19 = 1
            r0 = r13
            r1 = r34
            r2 = r35
            r22 = r3
            r3 = r4
            r4 = r17
            r17 = r5
            r5 = r19
            r19 = r6
            r6 = r39
            r0.<init>(r2, r3, r4, r5, r6)
            r7.commentTextView = r13
            r0 = 2131624258(0x7f0e0142, float:1.887569E38)
            java.lang.String r1 = "AddCaption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r13.setHint(r0)
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            r0.onResume()
            org.telegram.ui.Components.EditTextEmoji r0 = r7.commentTextView
            org.telegram.ui.Components.EditTextCaption r0 = r0.getEditText()
            org.telegram.ui.Components.ChatAttachAlert$12 r1 = new org.telegram.ui.Components.ChatAttachAlert$12
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
            org.telegram.ui.Components.ChatAttachAlert$13 r0 = new org.telegram.ui.Components.ChatAttachAlert$13
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
            r27 = 60
            r28 = 1114636288(0x42700000, float:60.0)
            r29 = 85
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            r33 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
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
            if (r4 < r5) goto L_0x06f5
            java.lang.String r4 = "dialogFloatingButtonPressed"
            goto L_0x06f7
        L_0x06f5:
            java.lang.String r4 = "dialogFloatingButton"
        L_0x06f7:
            int r4 = r7.getThemedColor(r4)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r3, r4)
            r7.writeButtonDrawable = r2
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 >= r5) goto L_0x0735
            android.content.res.Resources r2 = r35.getResources()
            r3 = 2131165415(0x7var_e7, float:1.7945046E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.Drawable r4 = r7.writeButtonDrawable
            r6 = 0
            r3.<init>(r2, r4, r6, r6)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3.setIconSize(r4, r6)
            r7.writeButtonDrawable = r3
        L_0x0735:
            android.widget.ImageView r2 = r7.writeButton
            android.graphics.drawable.Drawable r3 = r7.writeButtonDrawable
            r2.setBackgroundDrawable(r3)
            android.widget.ImageView r2 = r7.writeButton
            r3 = 2131165264(0x7var_, float:1.794474E38)
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
            if (r2 < r5) goto L_0x0771
            android.widget.ImageView r2 = r7.writeButton
            org.telegram.ui.Components.ChatAttachAlert$14 r3 = new org.telegram.ui.Components.ChatAttachAlert$14
            r3.<init>()
            r2.setOutlineProvider(r3)
        L_0x0771:
            android.widget.FrameLayout r2 = r7.writeButtonContainer
            android.widget.ImageView r3 = r7.writeButton
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r5) goto L_0x077e
            r4 = 56
            r23 = 56
            goto L_0x0782
        L_0x077e:
            r4 = 60
            r23 = 60
        L_0x0782:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r5) goto L_0x0789
            r24 = 1113587712(0x42600000, float:56.0)
            goto L_0x078d
        L_0x0789:
            r0 = 1114636288(0x42700000, float:60.0)
            r24 = 1114636288(0x42700000, float:60.0)
        L_0x078d:
            r25 = 51
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r5) goto L_0x0798
            r0 = 1073741824(0x40000000, float:2.0)
            r26 = 1073741824(0x40000000, float:2.0)
            goto L_0x079a
        L_0x0798:
            r26 = 0
        L_0x079a:
            r27 = 0
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r2.addView(r3, r0)
            android.widget.ImageView r0 = r7.writeButton
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda2
            r2.<init>(r7, r12)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r7.writeButton
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda4
            r2.<init>(r7, r12)
            r0.setOnLongClickListener(r2)
            android.text.TextPaint r0 = r7.textPaint
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = r7.textPaint
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r2)
            org.telegram.ui.Components.ChatAttachAlert$16 r0 = new org.telegram.ui.Components.ChatAttachAlert$16
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
            r23 = 42
            r24 = 1103101952(0x41CLASSNAME, float:24.0)
            r25 = 85
            r26 = 0
            r28 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r29 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r1, r2)
            if (r10 == 0) goto L_0x0804
            r34.checkColors()
            r0 = 0
            r7.navBarColorKey = r0
        L_0x0804:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m716lambda$new$0$orgtelegramuiComponentsChatAttachAlert(int id) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(id);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m717lambda$new$1$orgtelegramuiComponentsChatAttachAlert(View v) {
        this.selectedMenuItem.toggleSubMenu();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m724lambda$new$2$orgtelegramuiComponentsChatAttachAlert(View v) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m725lambda$new$3$orgtelegramuiComponentsChatAttachAlert(boolean showingFromDialog2, View v) {
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

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m726lambda$new$4$orgtelegramuiComponentsChatAttachAlert(View e) {
        updatePhotoPreview(this.currentAttachLayout != this.photoPreviewLayout);
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m729lambda$new$7$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View view, int position) {
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
                            chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda26(this));
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
                        chatAttachAlertPollLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda27(this));
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
                AttachBotButton button = (AttachBotButton) view;
                if (button.attachMenuBot != null) {
                    showBotLayout(button.attachMenuBot.bot_id);
                } else {
                    this.delegate.didSelectBot(button.currentUser);
                    dismiss();
                }
            }
            if (view.getX() + ((float) view.getWidth()) >= ((float) (this.buttonsRecyclerView.getMeasuredWidth() - AndroidUtilities.dp(32.0f)))) {
                this.buttonsRecyclerView.smoothScrollBy((int) (((float) view.getWidth()) * 1.5f), 0);
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m727lambda$new$5$orgtelegramuiComponentsChatAttachAlert(TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).didSelectLocation(location, live, notify, scheduleDate);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m728lambda$new$6$orgtelegramuiComponentsChatAttachAlert(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendPoll(poll, params, notify, scheduleDate);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ boolean m730lambda$new$8$orgtelegramuiComponentsChatAttachAlert(View view, int position) {
        if (!(view instanceof AttachBotButton)) {
            return false;
        }
        AttachBotButton button = (AttachBotButton) view;
        if (this.baseFragment == null || button.currentUser == null) {
            return false;
        }
        onLongClickBotButton(button.attachMenuBot, button.currentUser);
        return true;
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m731lambda$new$9$orgtelegramuiComponentsChatAttachAlert(View v) {
        ChatAttachAlertBotWebViewLayout webViewLayout;
        long j = this.selectedId;
        if (j < 0 && (webViewLayout = this.botAttachLayouts.get(-j)) != null) {
            webViewLayout.getWebViewContainer().onMainButtonPressed();
        }
    }

    static /* synthetic */ boolean lambda$new$10(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m719lambda$new$12$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View v) {
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
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlert$$ExternalSyntheticLambda22(this), resourcesProvider);
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

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m718lambda$new$11$orgtelegramuiComponentsChatAttachAlert(boolean notify, int scheduleDate) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(notify, scheduleDate);
            return;
        }
        attachAlertLayout.sendSelectedItems(notify, scheduleDate);
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* renamed from: lambda$new$16$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ boolean m723lambda$new$16$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View view) {
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
        this.sendPopupLayout.setDispatchKeyEventListener(new ChatAttachAlert$$ExternalSyntheticLambda20(this));
        this.sendPopupLayout.setShownFromBottom(false);
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
            this.itemCells[a].setOnClickListener(new ChatAttachAlert$$ExternalSyntheticLambda1(this, num, chatActivity, resourcesProvider2));
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

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m720lambda$new$13$orgtelegramuiComponentsChatAttachAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m722lambda$new$15$orgtelegramuiComponentsChatAttachAlert(int num, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlert$$ExternalSyntheticLambda23(this), resourcesProvider);
        } else if (num == 1) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
                sendPressed(false, 0);
                return;
            }
            attachAlertLayout.sendSelectedItems(false, 0);
            dismiss();
        }
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m721lambda$new$14$orgtelegramuiComponentsChatAttachAlert(boolean notify, int scheduleDate) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(notify, scheduleDate);
            return;
        }
        attachAlertLayout.sendSelectedItems(notify, scheduleDate);
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(getThemedColor(this.forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground")) > 0.699999988079071d;
    }

    public void onLongClickBotButton(TLRPC.TL_attachMenuBot attachMenuBot, TLRPC.User currentUser) {
        String botName = attachMenuBot != null ? attachMenuBot.short_name : UserObject.getUserName(currentUser);
        new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(attachMenuBot != null ? LocaleController.formatString("BotRemoveFromMenu", NUM, botName) : LocaleController.formatString("BotRemoveInlineFromMenu", NUM, botName))).setPositiveButton(LocaleController.getString("OK", NUM), new ChatAttachAlert$$ExternalSyntheticLambda32(this, attachMenuBot, currentUser)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).show();
    }

    /* renamed from: lambda$onLongClickBotButton$19$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m738xdf2da15f(TLRPC.TL_attachMenuBot attachMenuBot, TLRPC.User currentUser, DialogInterface dialogInterface, int i) {
        if (attachMenuBot != null) {
            TLRPC.TL_messages_toggleBotInAttachMenu req = new TLRPC.TL_messages_toggleBotInAttachMenu();
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(currentUser);
            req.enabled = false;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatAttachAlert$$ExternalSyntheticLambda18(this, attachMenuBot), 66);
            return;
        }
        MediaDataController.getInstance(this.currentAccount).removeInline(currentUser.id);
    }

    /* renamed from: lambda$onLongClickBotButton$18$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m737x1CLASSNAME(TLRPC.TL_attachMenuBot attachMenuBot, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda12(this, attachMenuBot));
    }

    /* renamed from: lambda$onLongClickBotButton$17$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m736x5954cea1(TLRPC.TL_attachMenuBot attachMenuBot) {
        MediaDataController.getInstance(this.currentAccount).loadAttachMenuBots(false, true);
        if (this.currentAttachLayout == this.botAttachLayouts.get(attachMenuBot.bot_id)) {
            showLayout(this.photoLayout);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldOverlayCameraViewOverNavBar() {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        return attachAlertLayout == chatAttachAlertPhotoLayout && chatAttachAlertPhotoLayout.cameraExpanded;
    }

    public void show() {
        super.show();
        boolean z = false;
        this.buttonPressed = false;
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            this.calcMandatoryInsets = ((ChatActivity) baseFragment2).isKeyboardVisible();
        }
        this.openTransitionFinished = false;
        if (Build.VERSION.SDK_INT >= 30) {
            this.navBarColorKey = null;
            this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), 0);
            AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
            Window window = getWindow();
            if (((double) AndroidUtilities.computePerceivedBrightness(this.navBarColor)) > 0.721d) {
                z = true;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
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
            this.currentAttachLayout.applyCaption(this.commentTextView.getText());
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
        long newId = this.selectedId;
        if (layout == this.photoLayout) {
            newId = 1;
        } else if (layout == this.audioLayout) {
            newId = 3;
        } else if (layout == this.documentLayout) {
            newId = 4;
        } else if (layout == this.contactsLayout) {
            newId = 5;
        } else if (layout == this.locationLayout) {
            newId = 6;
        } else if (layout == this.pollLayout) {
            newId = 9;
        }
        showLayout(layout, newId);
    }

    private void showLayout(AttachAlertLayout layout, long newId) {
        AttachAlertLayout attachAlertLayout = layout;
        if (this.viewChangeAnimator != null) {
            long j = newId;
        } else if (this.commentsAnimator != null) {
            long j2 = newId;
        } else {
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
            this.selectedId = newId;
            int count = this.buttonsRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.buttonsRecyclerView.getChildAt(a);
                if (child instanceof AttachButton) {
                    ((AttachButton) child).updateCheckedState(true);
                } else if (child instanceof AttachBotButton) {
                    ((AttachBotButton) child).updateCheckedState(true);
                }
            }
            int t = (this.currentAttachLayout.getFirstOffset() - AndroidUtilities.dp(11.0f)) - this.scrollOffsetY[0];
            this.nextAttachLayout = attachAlertLayout;
            if (Build.VERSION.SDK_INT >= 20) {
                this.container.setLayerType(2, (Paint) null);
            }
            this.actionBar.setVisibility(this.nextAttachLayout.needsActionBar() != 0 ? 0 : 4);
            this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
            if (this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
            }
            this.currentAttachLayout.onHide();
            AttachAlertLayout attachAlertLayout3 = this.nextAttachLayout;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
            if (attachAlertLayout3 == chatAttachAlertPhotoLayout) {
                chatAttachAlertPhotoLayout.setCheckCameraWhenShown(true);
            }
            this.nextAttachLayout.onShow(this.currentAttachLayout);
            this.nextAttachLayout.setVisibility(0);
            if (layout.getParent() != null) {
                this.containerView.removeView(this.nextAttachLayout);
            }
            int index = this.containerView.indexOfChild(this.currentAttachLayout);
            if (this.nextAttachLayout.getParent() != this.containerView) {
                ViewGroup viewGroup = this.containerView;
                AttachAlertLayout attachAlertLayout4 = this.nextAttachLayout;
                viewGroup.addView(attachAlertLayout4, attachAlertLayout4 == this.locationLayout ? index : index + 1, LayoutHelper.createFrame(-1, -1.0f));
            }
            final Runnable onEnd = new ChatAttachAlert$$ExternalSyntheticLambda9(this);
            if ((this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                int width = Math.max(this.nextAttachLayout.getWidth(), this.currentAttachLayout.getWidth());
                AttachAlertLayout attachAlertLayout5 = this.nextAttachLayout;
                if (attachAlertLayout5 instanceof ChatAttachAlertPhotoLayoutPreview) {
                    attachAlertLayout5.setTranslationX((float) width);
                    AttachAlertLayout attachAlertLayout6 = this.currentAttachLayout;
                    if (attachAlertLayout6 instanceof ChatAttachAlertPhotoLayout) {
                        ChatAttachAlertPhotoLayout photoLayout2 = (ChatAttachAlertPhotoLayout) attachAlertLayout6;
                        if (photoLayout2.cameraView != null) {
                            photoLayout2.cameraView.setVisibility(4);
                            photoLayout2.cameraIcon.setVisibility(4);
                            photoLayout2.cameraCell.setVisibility(0);
                        }
                    }
                } else {
                    this.currentAttachLayout.setTranslationX((float) (-width));
                    AttachAlertLayout attachAlertLayout7 = this.nextAttachLayout;
                    if (attachAlertLayout7 == this.photoLayout) {
                        ChatAttachAlertPhotoLayout photoLayout3 = (ChatAttachAlertPhotoLayout) attachAlertLayout7;
                        if (photoLayout3.cameraView != null) {
                            photoLayout3.cameraView.setVisibility(0);
                            photoLayout3.cameraIcon.setVisibility(0);
                        }
                    }
                }
                this.nextAttachLayout.setAlpha(1.0f);
                this.currentAttachLayout.setAlpha(1.0f);
                this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(0.0f));
                AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda14(this, attachAlertLayout, onEnd));
                return;
            }
            AnimatorSet animator = new AnimatorSet();
            this.nextAttachLayout.setAlpha(0.0f);
            this.nextAttachLayout.setTranslationY((float) AndroidUtilities.dp(78.0f));
            animator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.currentAttachLayout, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(78.0f) + t)}), ObjectAnimator.ofFloat(this.currentAttachLayout, this.ATTACH_ALERT_LAYOUT_TRANSLATION, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{this.actionBar.getAlpha(), 0.0f})});
            animator.setDuration(180);
            animator.setStartDelay(20);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
                    SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
                    springAnimation.getSpring().setDampingRatio(0.75f);
                    springAnimation.getSpring().setStiffness(500.0f);
                    springAnimation.addUpdateListener(new ChatAttachAlert$17$$ExternalSyntheticLambda1(this));
                    springAnimation.addEndListener(new ChatAttachAlert$17$$ExternalSyntheticLambda0(onEnd));
                    Object unused = ChatAttachAlert.this.viewChangeAnimator = springAnimation;
                    springAnimation.start();
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-ChatAttachAlert$17  reason: not valid java name */
                public /* synthetic */ void m749xe4ba0CLASSNAME(DynamicAnimation animation12, float value, float velocity) {
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert.this.updateSelectedPosition(1);
                    }
                    ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                    ChatAttachAlert.this.containerView.invalidate();
                }
            });
            this.viewChangeAnimator = animator;
            animator.start();
        }
    }

    /* renamed from: lambda$showLayout$20$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m741lambda$showLayout$20$orgtelegramuiComponentsChatAttachAlert() {
        AttachAlertLayout attachAlertLayout;
        ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview;
        if (Build.VERSION.SDK_INT >= 20) {
            this.container.setLayerType(0, (Paint) null);
        }
        this.viewChangeAnimator = null;
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        if (!(attachAlertLayout2 == this.photoLayout || (attachAlertLayout = this.nextAttachLayout) == (chatAttachAlertPhotoLayoutPreview = this.photoPreviewLayout) || attachAlertLayout2 == attachAlertLayout || attachAlertLayout2 == chatAttachAlertPhotoLayoutPreview)) {
            this.containerView.removeView(this.currentAttachLayout);
        }
        this.currentAttachLayout.setVisibility(8);
        this.currentAttachLayout.onHidden();
        this.nextAttachLayout.onShown();
        this.currentAttachLayout = this.nextAttachLayout;
        this.nextAttachLayout = null;
        int[] iArr = this.scrollOffsetY;
        iArr[0] = iArr[1];
    }

    /* renamed from: lambda$showLayout$23$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m744lambda$showLayout$23$orgtelegramuiComponentsChatAttachAlert(AttachAlertLayout layout, Runnable onEnd) {
        float fromActionBarAlpha = this.actionBar.getAlpha();
        boolean showActionBar = this.nextAttachLayout.getCurrentItemTop() <= layout.getButtonsHideOffset();
        float toActionBarAlpha = showActionBar ? 1.0f : 0.0f;
        SpringAnimation springAnimation = new SpringAnimation(new FloatValueHolder(0.0f));
        springAnimation.addUpdateListener(new ChatAttachAlert$$ExternalSyntheticLambda8(this, fromActionBarAlpha, toActionBarAlpha, showActionBar));
        springAnimation.addEndListener(new ChatAttachAlert$$ExternalSyntheticLambda7(this, showActionBar, onEnd));
        springAnimation.setSpring(new SpringForce(500.0f));
        springAnimation.getSpring().setDampingRatio(1.0f);
        springAnimation.getSpring().setStiffness(1000.0f);
        springAnimation.start();
        this.viewChangeAnimator = springAnimation;
    }

    /* renamed from: lambda$showLayout$21$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m742lambda$showLayout$21$orgtelegramuiComponentsChatAttachAlert(float fromActionBarAlpha, float toActionBarAlpha, boolean showActionBar, DynamicAnimation animation, float value, float velocity) {
        float f = value / 500.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(f));
        this.actionBar.setAlpha(AndroidUtilities.lerp(fromActionBarAlpha, toActionBarAlpha, f));
        updateLayout(this.currentAttachLayout, false, 0);
        updateLayout(this.nextAttachLayout, false, 0);
        float mediaPreviewAlpha = (!(this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || showActionBar) ? 1.0f - f : f;
        this.mediaPreviewView.setAlpha(mediaPreviewAlpha);
        this.selectedView.setAlpha(1.0f - mediaPreviewAlpha);
        this.selectedView.setTranslationX(((float) (-AndroidUtilities.dp(16.0f))) * mediaPreviewAlpha);
        this.mediaPreviewView.setTranslationX((1.0f - mediaPreviewAlpha) * ((float) AndroidUtilities.dp(16.0f)));
    }

    /* renamed from: lambda$showLayout$22$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m743lambda$showLayout$22$orgtelegramuiComponentsChatAttachAlert(boolean showActionBar, Runnable onEnd, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        this.currentAttachLayout.onHideShowProgress(1.0f);
        this.nextAttachLayout.onHideShowProgress(1.0f);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.nextAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.containerView.invalidate();
        this.actionBar.setTag(showActionBar ? 1 : null);
        onEnd.run();
    }

    public void updatePhotoPreview(boolean show) {
        if (!show) {
            showLayout(this.photoLayout);
        } else if (this.canOpenPreview) {
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
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda25(this));
        }
        showLayout(this.contactsLayout);
    }

    /* renamed from: lambda$openContactsLayout$24$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m740x1edc3a40(TLRPC.User user, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendContact(user, notify, scheduleDate);
    }

    /* access modifiers changed from: private */
    public void openAudioLayout(boolean show) {
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext(), this.resourcesProvider);
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda24(this));
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

    /* renamed from: lambda$openAudioLayout$25$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m739x51cvar_b4(ArrayList audios, CharSequence caption, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendAudio(audios, caption, notify, scheduleDate);
    }

    private void openDocumentsLayout(boolean show) {
        if (this.documentLayout == null) {
            int type = this.isSoundPicker ? 2 : 0;
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), type, this.resourcesProvider);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() {
                public void didSelectFiles(ArrayList<String> files, String caption, ArrayList<MessageObject> fmessages, boolean notify, int scheduleDate) {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) ChatAttachAlert.this.baseFragment).didSelectFiles(files, caption, fmessages, notify, scheduleDate);
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
                    if (ChatAttachAlert.this.baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) ChatAttachAlert.this.baseFragment).startDocumentSelectActivity();
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
            this.documentLayout.setCanSelectOnlyImageFiles(!this.isSoundPicker);
        }
        this.documentLayout.isSoundPicker = this.isSoundPicker;
        if (show) {
            showLayout(this.documentLayout);
        }
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        int i = 0;
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
            } else if (!this.isSoundPicker) {
                this.shadow.setTranslationY(((float) AndroidUtilities.dp(36.0f)) + this.botMainButtonOffsetY);
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
                            if (!ChatAttachAlert.this.isSoundPicker) {
                                ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            }
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                            if (!ChatAttachAlert.this.typeButtonsAvailable && !ChatAttachAlert.this.isSoundPicker) {
                                ChatAttachAlert.this.shadow.setVisibility(4);
                            }
                        } else if (ChatAttachAlert.this.typeButtonsAvailable && (ChatAttachAlert.this.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons())) {
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
                this.shadow.setTranslationY(((float) (show ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f))) + this.botMainButtonOffsetY);
                View view9 = this.shadow;
                if (show) {
                    f2 = 1.0f;
                }
                view9.setAlpha(f2);
            } else if (this.typeButtonsAvailable) {
                AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
                if (attachAlertLayout == null || attachAlertLayout.shouldHideBottomButtons()) {
                    RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
                    if (show) {
                        f2 = (float) AndroidUtilities.dp(36.0f);
                    }
                    recyclerListView2.setTranslationY(f2);
                }
                View view10 = this.shadow;
                if (show) {
                    i = AndroidUtilities.dp(36.0f);
                }
                view10.setTranslationY(((float) i) + this.botMainButtonOffsetY);
            } else {
                this.shadow.setTranslationY(((float) AndroidUtilities.dp(36.0f)) + this.botMainButtonOffsetY);
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
    public void cancelSheetAnimation() {
        if (this.currentSheetAnimation != null) {
            this.currentSheetAnimation.cancel();
            SpringAnimation springAnimation = this.appearSpringAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            AnimatorSet animatorSet = this.buttonsAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        this.photoLayout.setTranslationX(0.0f);
        this.mediaPreviewView.setAlpha(0.0f);
        this.selectedView.setAlpha(1.0f);
        this.containerView.setTranslationY((float) this.containerView.getMeasuredHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        this.buttonsAnimation = animatorSet;
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, new float[]{0.0f, 400.0f})});
        this.buttonsAnimation.setDuration(400);
        this.buttonsAnimation.setStartDelay(20);
        this.ATTACH_ALERT_PROGRESS.set(this, Float.valueOf(0.0f));
        this.buttonsAnimation.start();
        if (this.navigationBarAnimation != null) {
            this.navigationBarAnimation.cancel();
        }
        this.navigationBarAnimation = ValueAnimator.ofFloat(new float[]{this.navigationBarAlpha, 1.0f});
        this.navigationBarAnimation.addUpdateListener(new ChatAttachAlert$$ExternalSyntheticLambda10(this));
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
            this.container.setLayerType(2, (Paint) null);
        }
        this.currentSheetAnimationType = 1;
        this.currentSheetAnimation = new AnimatorSet();
        AnimatorSet animatorSet2 = this.currentSheetAnimation;
        Animator[] animatorArr = new Animator[1];
        ColorDrawable colorDrawable = this.backDrawable;
        Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
        int[] iArr = new int[1];
        iArr[0] = this.dimBehind ? this.dimBehindAlpha : 0;
        animatorArr[0] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
        animatorSet2.playTogether(animatorArr);
        this.currentSheetAnimation.setDuration(400);
        this.currentSheetAnimation.setStartDelay(20);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        final Runnable onAnimationEnd = new ChatAttachAlert$$ExternalSyntheticLambda13(this, this.delegate);
        this.appearSpringAnimation.addEndListener(new ChatAttachAlert$$ExternalSyntheticLambda6(this, onAnimationEnd));
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animation) && ChatAttachAlert.this.appearSpringAnimation != null && !ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                    onAnimationEnd.run();
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animation)) {
                    AnimatorSet unused = ChatAttachAlert.this.currentSheetAnimation = null;
                    int unused2 = ChatAttachAlert.this.currentSheetAnimationType = 0;
                }
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        ValueAnimator navigationBarAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        setNavBarAlpha(0.0f);
        navigationBarAnimator.addUpdateListener(new ChatAttachAlert$$ExternalSyntheticLambda21(this));
        navigationBarAnimator.setStartDelay(25);
        navigationBarAnimator.setDuration(200);
        navigationBarAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        navigationBarAnimator.start();
        return true;
    }

    /* renamed from: lambda$onCustomOpenAnimation$26$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m732x1a9a4de7(ValueAnimator a) {
        this.navigationBarAlpha = ((Float) a.getAnimatedValue()).floatValue();
        if (this.container != null) {
            this.container.invalidate();
        }
    }

    /* renamed from: lambda$onCustomOpenAnimation$27$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m733xdd86b746(BottomSheet.BottomSheetDelegateInterface delegate2) {
        this.currentSheetAnimation = null;
        this.appearSpringAnimation = null;
        this.currentSheetAnimationType = 0;
        if (delegate2 != null) {
            delegate2.onOpenAnimationEnd();
        }
        if (this.useHardwareLayer) {
            this.container.setLayerType(0, (Paint) null);
        }
        if (this.isFullscreen) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= -1025;
            getWindow().setAttributes(params);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
    }

    /* renamed from: lambda$onCustomOpenAnimation$28$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m734xa07320a5(Runnable onAnimationEnd, DynamicAnimation animation, boolean cancelled, float value, float velocity) {
        if (this.currentSheetAnimation != null && !this.currentSheetAnimation.isRunning()) {
            onAnimationEnd.run();
        }
    }

    /* renamed from: lambda$onCustomOpenAnimation$29$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m735x635f8a04(ValueAnimator a) {
        setNavBarAlpha(((Float) a.getAnimatedValue()).floatValue());
    }

    private void setNavBarAlpha(float alpha) {
        boolean z = false;
        this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), Math.min(255, Math.max(0, (int) (255.0f * alpha))));
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
        Window window = getWindow();
        if (((double) AndroidUtilities.computePerceivedBrightness(this.navBarColor)) > 0.721d) {
            z = true;
        }
        AndroidUtilities.setLightNavigationBar(window, z);
        getContainer().invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent event) {
        return this.currentAttachLayout.onContainerViewTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(EditTextBoldCursor editText, boolean showKeyboard) {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null && !this.enterCommentEventSent) {
            boolean keyboardVisible = chatAttachViewDelegate.needEnterComment();
            this.enterCommentEventSent = true;
            AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda15(this, editText, showKeyboard), keyboardVisible ? 200 : 0);
        }
    }

    /* renamed from: lambda$makeFocusable$31$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m715x84f5e6e7(EditTextBoldCursor editText, boolean showKeyboard) {
        setFocusable(true);
        editText.requestFocus();
        if (showKeyboard) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda16(editText));
        }
    }

    /* access modifiers changed from: private */
    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            AttachButton button = (AttachButton) view;
            button.textView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), getThemedColor(button.textKey), button.checkedState));
        } else if (view instanceof AttachBotButton) {
            AttachBotButton button2 = (AttachBotButton) view;
            button2.nameTextView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), button2.textColor, button2.checkedState));
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
        if (id == NotificationCenter.reloadInlineHints || id == NotificationCenter.attachMenuBotsDidLoad) {
            ButtonsAdapter buttonsAdapter2 = this.buttonsAdapter;
            if (buttonsAdapter2 != null) {
                buttonsAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        }
    }

    /* access modifiers changed from: private */
    public int getScrollOffsetY(int idx) {
        AttachAlertLayout attachAlertLayout = this.nextAttachLayout;
        if (attachAlertLayout == null || (!(this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) && !(attachAlertLayout instanceof ChatAttachAlertPhotoLayoutPreview))) {
            return this.scrollOffsetY[idx];
        }
        int[] iArr = this.scrollOffsetY;
        return AndroidUtilities.lerp(iArr[0], iArr[1], this.translationProgress);
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition(int idx) {
        float toMove;
        int t;
        float moveProgress;
        int finalMove;
        int finalMove2;
        AttachAlertLayout layout = idx == 0 ? this.currentAttachLayout : this.nextAttachLayout;
        int scrollOffset = getScrollOffsetY(idx);
        int t2 = scrollOffset - this.backgroundPaddingTop;
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
        float offset = this.actionBar.getAlpha() != 0.0f ? 0.0f : (float) AndroidUtilities.dp((1.0f - this.headerView.getAlpha()) * 26.0f);
        if (!this.menuShowed || this.avatarPicker != 0) {
            this.selectedMenuItem.setTranslationY(((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (finalMove + 37)))) + this.currentPanTranslationY);
        } else {
            this.selectedMenuItem.setTranslationY(((float) (scrollOffset - AndroidUtilities.dp((((float) finalMove) * moveProgress) + 37.0f))) + offset + this.currentPanTranslationY);
        }
        this.searchItem.setTranslationY(((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (finalMove + 37)))) + this.currentPanTranslationY);
        FrameLayout frameLayout = this.headerView;
        float dp = ((float) (scrollOffset - AndroidUtilities.dp((((float) finalMove) * moveProgress) + 25.0f))) + offset + this.currentPanTranslationY;
        this.baseSelectedTextViewTranslationY = dp;
        frameLayout.setTranslationY(dp);
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout != null && layout == chatAttachAlertPollLayout) {
            if (AndroidUtilities.isTablet()) {
                finalMove2 = 63;
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                finalMove2 = 53;
            } else {
                finalMove2 = 59;
            }
            this.doneItem.setTranslationY(Math.max(0.0f, (this.pollLayout.getTranslationY() + ((float) scrollOffset)) - ((float) AndroidUtilities.dp((((float) finalMove2) * moveProgress) + 7.0f))) + this.currentPanTranslationY);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
        if (((org.telegram.ui.ChatActivity) r0).allowSendGifs() != false) goto L_0x004c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x009d  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x013f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateActionBarVisibility(final boolean r12, boolean r13) {
        /*
            r11 = this;
            if (r12 == 0) goto L_0x000a
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0014
        L_0x000a:
            if (r12 != 0) goto L_0x0197
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0197
        L_0x0014:
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = 0
            r2 = 1
            if (r12 == 0) goto L_0x001f
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            goto L_0x0020
        L_0x001f:
            r3 = r1
        L_0x0020:
            r0.setTag(r3)
            android.animation.AnimatorSet r0 = r11.actionBarAnimation
            if (r0 == 0) goto L_0x002c
            r0.cancel()
            r11.actionBarAnimation = r1
        L_0x002c:
            boolean r0 = r11.avatarSearch
            r1 = 0
            if (r0 != 0) goto L_0x004c
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r0 = r11.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r3 = r11.photoLayout
            if (r0 != r3) goto L_0x004a
            boolean r0 = r11.menuShowed
            if (r0 != 0) goto L_0x004a
            org.telegram.ui.ActionBar.BaseFragment r0 = r11.baseFragment
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x004a
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            boolean r0 = r0.allowSendGifs()
            if (r0 == 0) goto L_0x004a
            goto L_0x004c
        L_0x004a:
            r0 = 0
            goto L_0x004d
        L_0x004c:
            r0 = 1
        L_0x004d:
            int r3 = r11.avatarPicker
            if (r3 != 0) goto L_0x0062
            boolean r3 = r11.menuShowed
            if (r3 != 0) goto L_0x0060
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r3 = r11.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r4 = r11.photoLayout
            if (r3 != r4) goto L_0x0060
            boolean r3 = r11.mediaEnabled
            if (r3 == 0) goto L_0x0060
            goto L_0x0062
        L_0x0060:
            r3 = 0
            goto L_0x0063
        L_0x0062:
            r3 = 1
        L_0x0063:
            if (r12 == 0) goto L_0x0074
            if (r0 == 0) goto L_0x006c
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r11.searchItem
            r4.setVisibility(r1)
        L_0x006c:
            if (r3 == 0) goto L_0x0085
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r11.selectedMenuItem
            r4.setVisibility(r1)
            goto L_0x0085
        L_0x0074:
            boolean r4 = r11.typeButtonsAvailable
            if (r4 == 0) goto L_0x0085
            android.widget.FrameLayout r4 = r11.frameLayout2
            java.lang.Object r4 = r4.getTag()
            if (r4 != 0) goto L_0x0085
            org.telegram.ui.Components.RecyclerListView r4 = r11.buttonsRecyclerView
            r4.setVisibility(r1)
        L_0x0085:
            android.view.Window r4 = r11.getWindow()
            if (r4 == 0) goto L_0x00aa
            org.telegram.ui.ActionBar.BaseFragment r4 = r11.baseFragment
            if (r4 == 0) goto L_0x00aa
            if (r12 == 0) goto L_0x009d
            android.view.Window r4 = r11.getWindow()
            boolean r5 = r11.isLightStatusBar()
            org.telegram.messenger.AndroidUtilities.setLightStatusBar(r4, r5)
            goto L_0x00aa
        L_0x009d:
            android.view.Window r4 = r11.getWindow()
            org.telegram.ui.ActionBar.BaseFragment r5 = r11.baseFragment
            boolean r5 = r5.isLightStatusBar()
            org.telegram.messenger.AndroidUtilities.setLightStatusBar(r4, r5)
        L_0x00aa:
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 0
            if (r13 == 0) goto L_0x013f
            android.animation.AnimatorSet r6 = new android.animation.AnimatorSet
            r6.<init>()
            r11.actionBarAnimation = r6
            r7 = 1127481344(0x43340000, float:180.0)
            if (r12 == 0) goto L_0x00bd
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x00be
        L_0x00bd:
            r8 = 0
        L_0x00be:
            org.telegram.ui.ActionBar.ActionBar r9 = r11.actionBar
            float r9 = r9.getAlpha()
            float r8 = r8 - r9
            float r8 = java.lang.Math.abs(r8)
            float r8 = r8 * r7
            long r7 = (long) r8
            r6.setDuration(r7)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            org.telegram.ui.ActionBar.ActionBar r7 = r11.actionBar
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00df
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x00e0
        L_0x00df:
            r10 = 0
        L_0x00e0:
            r9[r1] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6.add(r7)
            android.view.View r7 = r11.actionBarShadow
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00f4
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x00f5
        L_0x00f4:
            r10 = 0
        L_0x00f5:
            r9[r1] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6.add(r7)
            if (r0 == 0) goto L_0x0115
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r11.searchItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x010b
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x010c
        L_0x010b:
            r10 = 0
        L_0x010c:
            r9[r1] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6.add(r7)
        L_0x0115:
            if (r3 == 0) goto L_0x012a
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r11.selectedMenuItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r2 = new float[r2]
            if (r12 == 0) goto L_0x0120
            goto L_0x0121
        L_0x0120:
            r4 = 0
        L_0x0121:
            r2[r1] = r4
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r7, r8, r2)
            r6.add(r1)
        L_0x012a:
            android.animation.AnimatorSet r1 = r11.actionBarAnimation
            r1.playTogether(r6)
            android.animation.AnimatorSet r1 = r11.actionBarAnimation
            org.telegram.ui.Components.ChatAttachAlert$22 r2 = new org.telegram.ui.Components.ChatAttachAlert$22
            r2.<init>(r12)
            r1.addListener(r2)
            android.animation.AnimatorSet r1 = r11.actionBarAnimation
            r1.start()
            goto L_0x0197
        L_0x013f:
            r1 = 4
            if (r12 == 0) goto L_0x0155
            boolean r2 = r11.typeButtonsAvailable
            if (r2 == 0) goto L_0x0155
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r2 = r11.currentAttachLayout
            if (r2 == 0) goto L_0x0150
            boolean r2 = r2.shouldHideBottomButtons()
            if (r2 == 0) goto L_0x0155
        L_0x0150:
            org.telegram.ui.Components.RecyclerListView r2 = r11.buttonsRecyclerView
            r2.setVisibility(r1)
        L_0x0155:
            org.telegram.ui.ActionBar.ActionBar r2 = r11.actionBar
            if (r12 == 0) goto L_0x015c
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x015d
        L_0x015c:
            r6 = 0
        L_0x015d:
            r2.setAlpha(r6)
            android.view.View r2 = r11.actionBarShadow
            if (r12 == 0) goto L_0x0167
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0168
        L_0x0167:
            r6 = 0
        L_0x0168:
            r2.setAlpha(r6)
            if (r0 == 0) goto L_0x0178
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r11.searchItem
            if (r12 == 0) goto L_0x0174
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0175
        L_0x0174:
            r6 = 0
        L_0x0175:
            r2.setAlpha(r6)
        L_0x0178:
            if (r3 == 0) goto L_0x0183
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r11.selectedMenuItem
            if (r12 == 0) goto L_0x017f
            goto L_0x0180
        L_0x017f:
            r4 = 0
        L_0x0180:
            r2.setAlpha(r4)
        L_0x0183:
            if (r12 != 0) goto L_0x0197
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r11.searchItem
            r2.setVisibility(r1)
            int r2 = r11.avatarPicker
            if (r2 != 0) goto L_0x0192
            boolean r2 = r11.menuShowed
            if (r2 != 0) goto L_0x0197
        L_0x0192:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r11.selectedMenuItem
            r2.setVisibility(r1)
        L_0x0197:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateActionBarVisibility(boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0067, code lost:
        if (((androidx.dynamicanimation.animation.SpringAnimation) r5).isRunning() != false) goto L_0x006b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateLayout(org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r9, boolean r10, int r11) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0003
            return
        L_0x0003:
            int r0 = r9.getCurrentItemTop()
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto L_0x000d
            return
        L_0x000d:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r1 = r8.currentAttachLayout
            r2 = 1
            r3 = 0
            if (r9 != r1) goto L_0x001b
            int r1 = r9.getButtonsHideOffset()
            if (r0 > r1) goto L_0x001b
            r1 = 1
            goto L_0x001c
        L_0x001b:
            r1 = 0
        L_0x001c:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r8.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = r8.photoPreviewLayout
            if (r4 == r5) goto L_0x002f
            boolean r4 = r8.keyboardVisible
            if (r4 == 0) goto L_0x002f
            if (r10 == 0) goto L_0x002f
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r8.currentAttachLayout
            boolean r4 = r4 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout
            if (r4 != 0) goto L_0x002f
            r10 = 0
        L_0x002f:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r8.currentAttachLayout
            if (r9 != r4) goto L_0x0036
            r8.updateActionBarVisibility(r1, r10)
        L_0x0036:
            android.view.ViewGroup$LayoutParams r4 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            if (r4 != 0) goto L_0x0040
            r5 = 0
            goto L_0x0042
        L_0x0040:
            int r5 = r4.topMargin
        L_0x0042:
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r0 = r0 + r5
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r5 = r8.currentAttachLayout
            if (r5 != r9) goto L_0x0050
            r6 = 0
            goto L_0x0051
        L_0x0050:
            r6 = 1
        L_0x0051:
            boolean r5 = r5 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r5 != 0) goto L_0x005b
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r5 = r8.nextAttachLayout
            boolean r5 = r5 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r5 == 0) goto L_0x006a
        L_0x005b:
            java.lang.Object r5 = r8.viewChangeAnimator
            boolean r7 = r5 instanceof androidx.dynamicanimation.animation.SpringAnimation
            if (r7 == 0) goto L_0x006a
            androidx.dynamicanimation.animation.SpringAnimation r5 = (androidx.dynamicanimation.animation.SpringAnimation) r5
            boolean r5 = r5.isRunning()
            if (r5 == 0) goto L_0x006a
            goto L_0x006b
        L_0x006a:
            r2 = 0
        L_0x006b:
            int[] r3 = r8.scrollOffsetY
            r5 = r3[r6]
            if (r5 != r0) goto L_0x007b
            if (r2 == 0) goto L_0x0074
            goto L_0x007b
        L_0x0074:
            if (r11 == 0) goto L_0x0089
            r3 = r3[r6]
            r8.previousScrollOffsetY = r3
            goto L_0x0089
        L_0x007b:
            r5 = r3[r6]
            r8.previousScrollOffsetY = r5
            r3[r6] = r0
            r8.updateSelectedPosition(r6)
            android.view.ViewGroup r3 = r8.containerView
            r3.invalidate()
        L_0x0089:
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
            android.widget.FrameLayout r10 = r0.headerView
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
            android.widget.FrameLayout r3 = r0.headerView
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
            android.widget.FrameLayout r11 = r0.headerView
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
            org.telegram.ui.Components.ChatAttachAlert$23 r6 = new org.telegram.ui.Components.ChatAttachAlert$23
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
        if (this.baseFragment != null) {
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
            BaseFragment baseFragment2 = this.baseFragment;
            int i2 = 4;
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
            if (this.isSoundPicker) {
                openDocumentsLayout(false);
                layoutToSet = this.documentLayout;
                this.selectedId = 4;
            } else {
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
                AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
                this.currentAttachLayout = layoutToSet;
                setAllowNestedScroll(true);
                if (this.currentAttachLayout.getParent() == null) {
                    this.containerView.addView(this.currentAttachLayout, 0, LayoutHelper.createFrame(-1, -1.0f));
                }
                layoutToSet.setAlpha(1.0f);
                layoutToSet.setVisibility(0);
                layoutToSet.onShow((AttachAlertLayout) null);
                layoutToSet.onShown();
                ActionBar actionBar2 = this.actionBar;
                if (layoutToSet.needsActionBar() != 0) {
                    i2 = 0;
                }
                actionBar2.setVisibility(i2);
                this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
            }
            AttachAlertLayout previousLayout = this.currentAttachLayout;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
            if (previousLayout != chatAttachAlertPhotoLayout) {
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
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
            if (this.currentAttachLayout == null) {
                this.buttonsRecyclerView.setVisibility(8);
                this.shadow.setVisibility(8);
            }
            if (this.avatarPicker == 2) {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", NUM));
            } else {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhoto", NUM));
            }
        } else {
            this.typeButtonsAvailable = true;
        }
    }

    public void setSoundPicker() {
        this.isSoundPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
        this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", NUM));
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
        private static final int VIEW_TYPE_BOT_BUTTON = 1;
        private static final int VIEW_TYPE_BUTTON = 0;
        private int attachBotsEndRow;
        private int attachBotsStartRow;
        private List<TLRPC.TL_attachMenuBot> attachMenuBots = new ArrayList();
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
            view.setImportantForAccessibility(1);
            view.setFocusable(true);
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
                    AttachBotButton child = (AttachBotButton) holder.itemView;
                    int i = this.attachBotsStartRow;
                    if (position < i || position >= this.attachBotsEndRow) {
                        int position2 = position - this.buttonsCount;
                        child.setTag(Integer.valueOf(position2));
                        child.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(position2).peer.user_id)));
                        return;
                    }
                    int position3 = position - i;
                    child.setTag(Integer.valueOf(position3));
                    TLRPC.TL_attachMenuBot bot = this.attachMenuBots.get(position3);
                    child.setAttachBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(bot.bot_id)), bot);
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
            this.attachBotsStartRow = -1;
            this.attachBotsEndRow = -1;
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
                    this.buttonsCount = i3 + 1;
                    this.galleryButton = i3;
                    if ((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && !((ChatActivity) ChatAttachAlert.this.baseFragment).isInScheduleMode() && !((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat()) {
                        ChatActivity chatActivity = (ChatActivity) ChatAttachAlert.this.baseFragment;
                        this.attachBotsStartRow = this.buttonsCount;
                        this.attachMenuBots.clear();
                        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).getAttachMenuBots().bots.iterator();
                        while (it.hasNext()) {
                            TLRPC.TL_attachMenuBot bot = it.next();
                            if (MediaDataController.canShowAttachMenuBot(bot, chatActivity.getCurrentChat() != null ? chatActivity.getCurrentChat() : chatActivity.getCurrentUser())) {
                                this.attachMenuBots.add(bot);
                            }
                        }
                        int size = this.buttonsCount + this.attachMenuBots.size();
                        this.buttonsCount = size;
                        this.attachBotsEndRow = size;
                    }
                    int i4 = this.buttonsCount;
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
            if (position >= this.buttonsCount) {
                return 1;
            }
            if (position < this.attachBotsStartRow || position >= this.attachBotsEndRow) {
                return 0;
            }
            return 1;
        }
    }

    public void dismissInternal() {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.doOnIdle(new ChatAttachAlert$$ExternalSyntheticLambda11(this));
        } else {
            removeFromRoot();
        }
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
                updateActionBarVisibility(false, false);
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

    public void dismiss(boolean passConfirmationAlert) {
        if (passConfirmationAlert) {
            this.allowPassConfirmationAlert = passConfirmationAlert;
        }
        dismiss();
    }

    public void dismiss() {
        if (!this.currentAttachLayout.onDismiss() && !isDismissed()) {
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
            }
            this.botAttachLayouts.clear();
            if (this.allowPassConfirmationAlert || this.baseFragment == null || this.currentAttachLayout.getSelectedItemsCount() <= 0) {
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
                AndroidUtilities.setNavigationBarColor(getWindow(), ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), 0), true, new ChatAttachAlert$$ExternalSyntheticLambda17(this));
                if (this.baseFragment != null) {
                    AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
                }
                super.dismiss();
                this.allowPassConfirmationAlert = false;
            } else if (!this.confirmationAlertShown) {
                this.confirmationAlertShown = true;
                AlertDialog dialog = new AlertDialog.Builder(this.baseFragment.getParentActivity(), this.parentThemeDelegate).setTitle(LocaleController.getString("DiscardSelectionAlertTitle", NUM)).setMessage(LocaleController.getString("DiscardSelectionAlertMessage", NUM)).setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new ChatAttachAlert$$ExternalSyntheticLambda31(this)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).setOnCancelListener(new ChatAttachAlert$$ExternalSyntheticLambda30(this)).setOnPreDismissListener(new ChatAttachAlert$$ExternalSyntheticLambda33(this)).create();
                dialog.show();
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(getThemedColor("dialogTextRed2"));
                }
            }
        }
    }

    /* renamed from: lambda$dismiss$32$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m711lambda$dismiss$32$orgtelegramuiComponentsChatAttachAlert(DialogInterface dialogInterface, int i) {
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* renamed from: lambda$dismiss$33$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m712lambda$dismiss$33$orgtelegramuiComponentsChatAttachAlert(DialogInterface di) {
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

    /* renamed from: lambda$dismiss$34$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m713lambda$dismiss$34$orgtelegramuiComponentsChatAttachAlert(DialogInterface di) {
        this.confirmationAlertShown = false;
    }

    /* renamed from: lambda$dismiss$35$org-telegram-ui-Components-ChatAttachAlert  reason: not valid java name */
    public /* synthetic */ void m714lambda$dismiss$35$orgtelegramuiComponentsChatAttachAlert(int tcolor) {
        this.navBarColorKey = null;
        this.navBarColor = tcolor;
        this.containerView.invalidate();
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

    public ChatAttachAlertDocumentLayout getDocumentLayout() {
        return this.documentLayout;
    }
}
