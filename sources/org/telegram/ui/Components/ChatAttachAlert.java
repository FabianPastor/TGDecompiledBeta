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
import android.graphics.Point;
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
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.ConnectionsManager;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$10(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean canDismiss() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void setCanOpenPreview(boolean z) {
        this.canOpenPreview = z;
        this.selectedArrowImageView.setVisibility((!z || this.avatarPicker == 2) ? 8 : 0);
    }

    public float getClipLayoutBottom() {
        return ((float) this.frameLayout2.getMeasuredHeight()) - (((float) (this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f))) * (1.0f - this.frameLayout2.getAlpha()));
    }

    public void showBotLayout(long j) {
        showBotLayout(j, (String) null);
    }

    public void showBotLayout(long j, String str) {
        if ((this.botAttachLayouts.get(j) == null || !ObjectsCompat$$ExternalSyntheticBackport0.m(str, this.botAttachLayouts.get(j).getStartCommand()) || this.botAttachLayouts.get(j).needReload()) && (this.baseFragment instanceof ChatActivity)) {
            final ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = new ChatAttachAlertBotWebViewLayout(this, getContext(), this.resourcesProvider);
            this.botAttachLayouts.put(j, chatAttachAlertBotWebViewLayout);
            this.botAttachLayouts.get(j).setDelegate(new BotWebViewContainer.Delegate() {
                /* access modifiers changed from: private */
                public ValueAnimator botButtonAnimator;

                public /* synthetic */ void onSendWebViewData(String str) {
                    BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
                }

                public /* synthetic */ void onWebAppReady() {
                    BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
                }

                public void onWebAppSetupClosingBehavior(boolean z) {
                    chatAttachAlertBotWebViewLayout.setNeedCloseConfirmation(z);
                }

                public void onCloseRequested(Runnable runnable) {
                    if (ChatAttachAlert.this.currentAttachLayout == chatAttachAlertBotWebViewLayout) {
                        ChatAttachAlert.this.setFocusable(false);
                        ChatAttachAlert.this.getWindow().setSoftInputMode(48);
                        ChatAttachAlert.this.dismiss();
                        AndroidUtilities.runOnUIThread(new ChatAttachAlert$1$$ExternalSyntheticLambda2(runnable), 150);
                    }
                }

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onCloseRequested$0(Runnable runnable) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }

                public void onWebAppSetActionBarColor(String str) {
                    int color = ((ColorDrawable) ChatAttachAlert.this.actionBar.getBackground()).getColor();
                    int access$100 = ChatAttachAlert.this.getThemedColor(str);
                    ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(200);
                    duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    duration.addUpdateListener(new ChatAttachAlert$1$$ExternalSyntheticLambda1(this, color, access$100));
                    duration.start();
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onWebAppSetActionBarColor$1(int i, int i2, ValueAnimator valueAnimator) {
                    ChatAttachAlert.this.actionBar.setBackgroundColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
                }

                public void onWebAppSetBackgroundColor(int i) {
                    chatAttachAlertBotWebViewLayout.setCustomBackground(i);
                }

                public void onWebAppOpenInvoice(String str, TLObject tLObject) {
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
                        chatAttachAlertBotWebViewLayout.scrollToTop();
                        AndroidUtilities.hideKeyboard(chatAttachAlertBotWebViewLayout);
                        OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(baseFragment.getParentActivity(), ChatAttachAlert.this.resourcesProvider);
                        overlayActionBarLayoutDialog.show();
                        paymentFormActivity.setPaymentFormCallback(new ChatAttachAlert$1$$ExternalSyntheticLambda3(overlayActionBarLayoutDialog, chatAttachAlertBotWebViewLayout, str));
                        paymentFormActivity.setResourcesProvider(ChatAttachAlert.this.resourcesProvider);
                        overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
                    }
                }

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onWebAppOpenInvoice$2(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
                    if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                        overlayActionBarLayoutDialog.dismiss();
                    }
                    chatAttachAlertBotWebViewLayout.getWebViewContainer().onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
                }

                public void onWebAppExpand() {
                    AttachAlertLayout access$000 = ChatAttachAlert.this.currentAttachLayout;
                    ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = chatAttachAlertBotWebViewLayout;
                    if (access$000 == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.canExpandByRequest()) {
                        chatAttachAlertBotWebViewLayout.scrollToTop();
                    }
                }

                public void onSetupMainButton(final boolean z, boolean z2, String str, int i, int i2, final boolean z3) {
                    AttachAlertLayout access$000 = ChatAttachAlert.this.currentAttachLayout;
                    ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = chatAttachAlertBotWebViewLayout;
                    if (access$000 == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.isBotButtonAvailable()) {
                        ChatAttachAlert.this.botMainButtonTextView.setClickable(z2);
                        ChatAttachAlert.this.botMainButtonTextView.setText(str);
                        ChatAttachAlert.this.botMainButtonTextView.setTextColor(i2);
                        ChatAttachAlert.this.botMainButtonTextView.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
                        float f = 0.0f;
                        float f2 = 1.0f;
                        if (ChatAttachAlert.this.botButtonWasVisible != z) {
                            boolean unused = ChatAttachAlert.this.botButtonWasVisible = z;
                            ValueAnimator valueAnimator = this.botButtonAnimator;
                            if (valueAnimator != null) {
                                valueAnimator.cancel();
                            }
                            float[] fArr = new float[2];
                            fArr[0] = z ? 0.0f : 1.0f;
                            fArr[1] = z ? 1.0f : 0.0f;
                            ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(250);
                            this.botButtonAnimator = duration;
                            duration.addUpdateListener(new ChatAttachAlert$1$$ExternalSyntheticLambda0(this));
                            this.botButtonAnimator.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationStart(Animator animator) {
                                    if (z) {
                                        ChatAttachAlert.this.botMainButtonTextView.setAlpha(0.0f);
                                        ChatAttachAlert.this.botMainButtonTextView.setVisibility(0);
                                        int dp = AndroidUtilities.dp(36.0f);
                                        for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                                            ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                                        }
                                        return;
                                    }
                                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(0.0f);
                                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(0);
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (!z) {
                                        ChatAttachAlert.this.botMainButtonTextView.setVisibility(8);
                                    } else {
                                        ChatAttachAlert.this.buttonsRecyclerView.setVisibility(8);
                                    }
                                    int dp = z ? AndroidUtilities.dp(36.0f) : 0;
                                    for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                                        ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                                    }
                                    if (AnonymousClass1.this.botButtonAnimator == animator) {
                                        ValueAnimator unused = AnonymousClass1.this.botButtonAnimator = null;
                                    }
                                }
                            });
                            this.botButtonAnimator.start();
                        }
                        ChatAttachAlert.this.botProgressView.setProgressColor(i2);
                        if (ChatAttachAlert.this.botButtonProgressWasVisible != z3) {
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
                            scaleX.scaleY(f2).setDuration(250).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    boolean unused = ChatAttachAlert.this.botButtonProgressWasVisible = z3;
                                    if (!z3) {
                                        ChatAttachAlert.this.botProgressView.setVisibility(8);
                                    }
                                }
                            }).start();
                        }
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onSetupMainButton$3(ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(1.0f - floatValue);
                    ChatAttachAlert.this.botMainButtonTextView.setAlpha(floatValue);
                    float unused = ChatAttachAlert.this.botMainButtonOffsetY = floatValue * ((float) AndroidUtilities.dp(36.0f));
                    ChatAttachAlert.this.shadow.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.buttonsRecyclerView.setTranslationY(chatAttachAlert.botMainButtonOffsetY);
                }

                public void onSetBackButtonVisible(boolean z) {
                    AndroidUtilities.updateImageViewImageAnimated(ChatAttachAlert.this.actionBar.getBackButton(), z ? NUM : NUM);
                }
            });
            MessageObject replyingMessageObject = ((ChatActivity) this.baseFragment).getChatActivityEnterView().getReplyingMessageObject();
            this.botAttachLayouts.get(j).requestWebView(this.currentAccount, ((ChatActivity) this.baseFragment).getDialogId(), j, false, replyingMessageObject != null ? replyingMessageObject.messageOwner.id : 0, str);
        }
        if (this.botAttachLayouts.get(j) != null) {
            this.botAttachLayouts.get(j).disallowSwipeOffsetAnimation();
            showLayout(this.botAttachLayouts.get(j), -j);
        }
    }

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3);

        void didSelectBot(TLRPC$User tLRPC$User);

        void doOnIdle(Runnable runnable);

        boolean needEnterComment();

        void onCameraOpened();

        void openAvatarsSearch();

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ChatAttachViewDelegate$-CC  reason: invalid class name */
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

    public static class AttachAlertLayout extends FrameLayout {
        protected ChatAttachAlert parentAlert;
        protected final Theme.ResourcesProvider resourcesProvider;

        /* access modifiers changed from: package-private */
        public void applyCaption(CharSequence charSequence) {
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
        public int getCustomBackground() {
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
        public boolean hasCustomBackground() {
            return false;
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
        public void onContainerTranslationUpdated(float f) {
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
        public boolean onDismissWithTouchOutside() {
            return true;
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

        public void onPanTransitionEnd() {
        }

        public void onPanTransitionStart(boolean z, int i) {
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
        public void onShow(AttachAlertLayout attachAlertLayout) {
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

        /* access modifiers changed from: package-private */
        public boolean shouldHideBottomButtons() {
            return true;
        }

        public AttachAlertLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            this.parentAlert = chatAttachAlert;
        }

        /* access modifiers changed from: package-private */
        public int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12.0f : 17.0f);
        }

        /* access modifiers changed from: protected */
        public int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
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
            setFocusable(true);
            AnonymousClass1 r1 = new RLottieImageView(context, ChatAttachAlert.this) {
                public void setScaleX(float f) {
                    super.setScaleX(f);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(this.textView.getText());
            accessibilityNodeInfo.setEnabled(true);
            accessibilityNodeInfo.setSelected(this.checked);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean z) {
            if (this.checked != (((long) this.currentId) == ChatAttachAlert.this.selectedId)) {
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
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
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
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor("dialogTextGray2"), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
            float dp = ((float) AndroidUtilities.dp(23.0f)) * scaleX;
            float left = ((float) this.imageView.getLeft()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
            float top = ((float) this.imageView.getTop()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
            ChatAttachAlert.this.attachButtonPaint.setColor(ChatAttachAlert.this.getThemedColor(this.backgroundKey));
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
        /* access modifiers changed from: private */
        public TLRPC$TL_attachMenuBot attachMenuBot;
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private ValueAnimator checkAnimator;
        private Boolean checked;
        /* access modifiers changed from: private */
        public float checkedState;
        /* access modifiers changed from: private */
        public TLRPC$User currentUser;
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

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                    Drawable drawable = imageReceiver.getDrawable();
                    if (drawable instanceof RLottieDrawable) {
                        RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                        rLottieDrawable.setCustomEndFrame(0);
                        rLottieDrawable.stop();
                        rLottieDrawable.setProgress(0.0f, false);
                    }
                }

                public void setScaleX(float f) {
                    super.setScaleX(f);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.selector != null && this.checked.booleanValue()) {
                accessibilityNodeInfo.setCheckable(true);
                accessibilityNodeInfo.setChecked(true);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
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

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.attachMenuBot != null) {
                float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
                float dp = ((float) AndroidUtilities.dp(23.0f)) * scaleX;
                float left = ((float) this.imageView.getLeft()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
                float top = ((float) this.imageView.getTop()) + (((float) this.imageView.getMeasuredWidth()) / 2.0f);
                ChatAttachAlert.this.attachButtonPaint.setColor(this.iconBackgroundColor);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
                ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(((float) AndroidUtilities.dp(3.0f)) * scaleX);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(left, top, dp - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(left, top, dp - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean z) {
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
                    ofFloat.addUpdateListener(new ChatAttachAlert$AttachBotButton$$ExternalSyntheticLambda0(this));
                    this.checkAnimator.setDuration(200);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateCheckedState$0(ValueAnimator valueAnimator) {
            setCheckedState(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        public void setUser(TLRPC$User tLRPC$User) {
            if (tLRPC$User != null) {
                this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor("dialogTextGray2"));
                this.currentUser = tLRPC$User;
                this.nameTextView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
                this.avatarDrawable.setInfo(tLRPC$User);
                this.imageView.setForUserOrChat(tLRPC$User, this.avatarDrawable);
                this.imageView.setSize(-1, -1);
                this.imageView.setColorFilter((ColorFilter) null);
                this.attachMenuBot = null;
                this.selector.setVisibility(0);
                updateMargins();
                setCheckedState(0.0f);
                invalidate();
            }
        }

        public void setAttachBot(TLRPC$User tLRPC$User, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
            boolean z;
            if (tLRPC$User != null && tLRPC$TL_attachMenuBot != null) {
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
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), String.valueOf(tLRPC$TL_attachMenuBot.bot_id), z ? "tgs" : "svg", (Drawable) DocumentObject.getSvgThumb(tLRPC$Document, "windowBackgroundGray", 1.0f), (Object) tLRPC$TL_attachMenuBot);
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
    }

    public ChatAttachAlert(Context context, BaseFragment baseFragment2, boolean z, boolean z2) {
        this(context, baseFragment2, z, z2, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlert(android.content.Context r30, org.telegram.ui.ActionBar.BaseFragment r31, boolean r32, boolean r33, org.telegram.ui.ActionBar.Theme.ResourcesProvider r34) {
        /*
            r29 = this;
            r8 = r29
            r9 = r30
            r0 = r31
            r10 = r32
            r11 = r34
            r12 = 0
            r8.<init>(r9, r12, r11)
            r8.canOpenPreview = r12
            r8.isSoundPicker = r12
            r13 = 0
            r8.translationProgress = r13
            org.telegram.ui.Components.ChatAttachAlert$2 r1 = new org.telegram.ui.Components.ChatAttachAlert$2
            java.lang.String r2 = "translation"
            r1.<init>(r2)
            r8.ATTACH_ALERT_LAYOUT_TRANSLATION = r1
            r1 = 7
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r1 = new org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout[r1]
            r8.layouts = r1
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            r8.botAttachLayouts = r1
            android.text.TextPaint r1 = new android.text.TextPaint
            r14 = 1
            r1.<init>(r14)
            r8.textPaint = r1
            android.graphics.RectF r1 = new android.graphics.RectF
            r1.<init>()
            r8.rect = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r14)
            r8.paint = r1
            r8.sendButtonEnabled = r14
            r7 = 1065353216(0x3var_, float:1.0)
            r8.sendButtonEnabledProgress = r7
            r8.cornerRadius = r7
            r8.botButtonProgressWasVisible = r12
            r8.botButtonWasVisible = r12
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r8.currentAccount = r1
            r8.mediaEnabled = r14
            r8.pollsEnabled = r14
            r15 = -1
            r8.maxSelectedPhotos = r15
            r8.allowOrder = r14
            r1 = 1118437376(0x42aa0000, float:85.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r8.attachItemSize = r1
            android.view.animation.DecelerateInterpolator r1 = new android.view.animation.DecelerateInterpolator
            r1.<init>()
            r6 = 2
            int[] r1 = new int[r6]
            r8.scrollOffsetY = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r14)
            r8.attachButtonPaint = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.exclusionRects = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r8.exclustionRect = r1
            org.telegram.ui.Components.ChatAttachAlert$20 r1 = new org.telegram.ui.Components.ChatAttachAlert$20
            java.lang.String r2 = "openProgress"
            r1.<init>(r2)
            r8.ATTACH_ALERT_PROGRESS = r1
            r8.confirmationAlertShown = r12
            r8.allowPassConfirmationAlert = r12
            r8.forceDarkTheme = r10
            r8.drawNavigationBar = r14
            boolean r1 = r0 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x009d
            boolean r1 = r31.isInBubbleMode()
            if (r1 == 0) goto L_0x009d
            r1 = 1
            goto L_0x009e
        L_0x009d:
            r1 = 0
        L_0x009e:
            r8.inBubbleMode = r1
            android.view.animation.OvershootInterpolator r1 = new android.view.animation.OvershootInterpolator
            r2 = 1060320051(0x3var_, float:0.7)
            r1.<init>(r2)
            r8.openInterpolator = r1
            r8.baseFragment = r0
            r8.useSmoothKeyboard = r14
            r8.setDelegate(r8)
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.reloadInlineHints
            r0.addObserver(r8, r1)
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.attachMenuBotsDidLoad
            r0.addObserver(r8, r1)
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.currentUserPremiumStatusChanged
            r0.addObserver(r8, r1)
            java.util.ArrayList<android.graphics.Rect> r0 = r8.exclusionRects
            android.graphics.Rect r1 = r8.exclustionRect
            r0.add(r1)
            org.telegram.ui.Components.ChatAttachAlert$3 r0 = new org.telegram.ui.Components.ChatAttachAlert$3
            r0.<init>(r9, r10)
            r8.sizeNotifierFrameLayout = r0
            org.telegram.ui.Components.ChatAttachAlert$4 r1 = new org.telegram.ui.Components.ChatAttachAlert$4
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r8.sizeNotifierFrameLayout
            r8.containerView = r0
            r0.setWillNotDraw(r12)
            android.view.ViewGroup r0 = r8.containerView
            r0.setClipChildren(r12)
            android.view.ViewGroup r0 = r8.containerView
            r0.setClipToPadding(r12)
            android.view.ViewGroup r0 = r8.containerView
            int r1 = r8.backgroundPaddingLeft
            r0.setPadding(r1, r12, r1, r12)
            org.telegram.ui.Components.ChatAttachAlert$5 r0 = new org.telegram.ui.Components.ChatAttachAlert$5
            r0.<init>(r9, r11)
            r8.actionBar = r0
            java.lang.String r1 = "dialogBackground"
            int r1 = r8.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131165449(0x7var_, float:1.7945115E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            java.lang.String r5 = "dialogTextBlack"
            int r1 = r8.getThemedColor(r5)
            r0.setItemsColor(r1, r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            java.lang.String r4 = "dialogButtonSelector"
            int r1 = r8.getThemedColor(r4)
            r0.setItemsBackgroundColor(r1, r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            int r1 = r8.getThemedColor(r5)
            r0.setTitleColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r0.setOccupyStatusBar(r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r0.setAlpha(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.Components.ChatAttachAlert$6 r1 = new org.telegram.ui.Components.ChatAttachAlert$6
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r16 = 0
            int r17 = r8.getThemedColor(r5)
            r18 = 0
            r0 = r3
            r1 = r30
            r15 = r3
            r3 = r16
            r7 = r4
            r4 = r17
            r14 = r5
            r5 = r18
            r6 = r34
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r8.selectedMenuItem = r15
            r15.setLongClickEnabled(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r1 = 2131165453(0x7var_d, float:1.7945124E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r1 = 2131624003(0x7f0e0043, float:1.8875173E38)
            java.lang.String r2 = "AccDescrMoreOptions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r15 = 4
            r0.setVisibility(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r0.setAlpha(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r6 = 2
            r0.setSubMenuOpenSide(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda27 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda27
            r1.<init>(r8)
            r0.setDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r1 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setAdditionalYOffset(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            int r1 = r8.getThemedColor(r7)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.selectedMenuItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda8
            r1.<init>(r8)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r3 = 0
            java.lang.String r0 = "windowBackgroundWhiteBlueHeader"
            int r4 = r8.getThemedColor(r0)
            r18 = 1
            r0 = r5
            r1 = r30
            r13 = r5
            r5 = r18
            r6 = r34
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r8.doneItem = r13
            r13.setLongClickEnabled(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.doneItem
            r1 = 2131625288(0x7f0e0548, float:1.887778E38)
            java.lang.String r2 = "Create"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.doneItem
            r0.setVisibility(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.doneItem
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.doneItem
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.doneItem
            int r1 = r8.getThemedColor(r7)
            r2 = 3
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.doneItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda6
            r1.<init>(r8)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r13 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            int r4 = r8.getThemedColor(r14)
            r5 = 0
            r0 = r13
            r1 = r30
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r8.searchItem = r13
            r13.setLongClickEnabled(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            r1 = 2131165456(0x7var_, float:1.794513E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            r1 = 2131628163(0x7f0e1083, float:1.888361E38)
            java.lang.String r2 = "Search"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            r0.setVisibility(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            r1 = 1109917696(0x42280000, float:42.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            int r1 = r8.getThemedColor(r7)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.searchItem
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12
            r2 = r33
            r1.<init>(r8, r2)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$8 r0 = new org.telegram.ui.Components.ChatAttachAlert$8
            r0.<init>(r9)
            r8.headerView = r0
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda9
            r1.<init>(r8)
            r0.setOnClickListener(r1)
            android.widget.FrameLayout r0 = r8.headerView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.FrameLayout r0 = r8.headerView
            r0.setVisibility(r15)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r9)
            r8.selectedView = r0
            r0.setOrientation(r12)
            android.widget.LinearLayout r0 = r8.selectedView
            r1 = 16
            r0.setGravity(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.selectedTextView = r0
            int r2 = r8.getThemedColor(r14)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r8.selectedTextView
            r2 = 1098907648(0x41800000, float:16.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r8.selectedTextView
            java.lang.String r13 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r3)
            android.widget.TextView r0 = r8.selectedTextView
            r3 = 19
            r0.setGravity(r3)
            android.widget.LinearLayout r0 = r8.selectedView
            android.widget.TextView r3 = r8.selectedTextView
            r4 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r4, (int) r1)
            r0.addView(r3, r5)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.selectedArrowImageView = r0
            android.content.Context r0 = r29.getContext()
            android.content.res.Resources r0 = r0.getResources()
            r3 = 2131165262(0x7var_e, float:1.7944736E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r3)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r5 = r8.getThemedColor(r14)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r5, r6)
            r0.setColorFilter(r3)
            android.widget.ImageView r3 = r8.selectedArrowImageView
            r3.setImageDrawable(r0)
            android.widget.ImageView r0 = r8.selectedArrowImageView
            r3 = 8
            r0.setVisibility(r3)
            android.widget.LinearLayout r0 = r8.selectedView
            android.widget.ImageView r5 = r8.selectedArrowImageView
            r22 = -2
            r23 = -2
            r24 = 16
            r25 = 4
            r26 = 1
            r27 = 0
            r28 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
            r0.addView(r5, r6)
            android.widget.LinearLayout r0 = r8.selectedView
            r5 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r5)
            android.widget.FrameLayout r0 = r8.headerView
            android.widget.LinearLayout r5 = r8.selectedView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r6)
            r0.addView(r5, r7)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r9)
            r8.mediaPreviewView = r0
            r0.setOrientation(r12)
            android.widget.LinearLayout r0 = r8.mediaPreviewView
            r0.setGravity(r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            android.content.Context r5 = r29.getContext()
            android.content.res.Resources r5 = r5.getResources()
            r7 = 2131165261(0x7var_d, float:1.7944734E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r7)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r15 = r8.getThemedColor(r14)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r15, r3)
            r5.setColorFilter(r7)
            r0.setImageDrawable(r5)
            android.widget.LinearLayout r3 = r8.mediaPreviewView
            r25 = 0
            r27 = 4
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
            r3.addView(r0, r5)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.mediaPreviewTextView = r0
            int r3 = r8.getThemedColor(r14)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r8.mediaPreviewTextView
            r3 = 1
            r0.setTextSize(r3, r2)
            android.widget.TextView r0 = r8.mediaPreviewTextView
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r3)
            android.widget.TextView r0 = r8.mediaPreviewTextView
            r3 = 19
            r0.setGravity(r3)
            android.widget.TextView r0 = r8.mediaPreviewTextView
            r3 = 2131624510(0x7f0e023e, float:1.8876202E38)
            java.lang.String r5 = "AttachMediaPreview"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r0.setText(r3)
            android.widget.LinearLayout r0 = r8.mediaPreviewView
            r3 = 0
            r0.setAlpha(r3)
            android.widget.LinearLayout r0 = r8.mediaPreviewView
            android.widget.TextView r3 = r8.mediaPreviewTextView
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r4, (int) r1)
            r0.addView(r3, r1)
            android.widget.FrameLayout r0 = r8.headerView
            android.widget.LinearLayout r1 = r8.mediaPreviewView
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r6)
            r0.addView(r1, r3)
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout[] r0 = r8.layouts
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout
            r1.<init>(r8, r9, r10, r11)
            r8.photoLayout = r1
            r0[r12] = r1
            r0 = 0
            r1.setTranslationX(r0)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r0 = r8.photoLayout
            r8.currentAttachLayout = r0
            r14 = 1
            r8.selectedId = r14
            android.view.ViewGroup r1 = r8.containerView
            r3 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r6)
            r1.addView(r0, r5)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.FrameLayout r1 = r8.headerView
            r22 = -1
            r23 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r24 = 51
            r25 = 1102577664(0x41b80000, float:23.0)
            r26 = 0
            r27 = 1111490560(0x42400000, float:48.0)
            r28 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r0.addView(r1, r3)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3)
            r0.addView(r1, r3)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r8.selectedMenuItem
            r3 = 48
            r5 = 53
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r5)
            r0.addView(r1, r6)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r8.searchItem
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r5)
            r0.addView(r1, r6)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r8.doneItem
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r3, (int) r5)
            r0.addView(r1, r5)
            android.view.View r0 = new android.view.View
            r0.<init>(r9)
            r8.actionBarShadow = r0
            r1 = 0
            r0.setAlpha(r1)
            android.view.View r0 = r8.actionBarShadow
            java.lang.String r1 = "dialogShadowLine"
            int r1 = r8.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r1 = r8.actionBarShadow
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r0.addView(r1, r5)
            android.view.View r0 = new android.view.View
            r0.<init>(r9)
            r8.shadow = r0
            r1 = 2131165265(0x7var_, float:1.7944742E38)
            r0.setBackgroundResource(r1)
            android.view.View r0 = r8.shadow
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r5, r6)
            r0.setColorFilter(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r1 = r8.shadow
            r23 = 1073741824(0x40000000, float:2.0)
            r24 = 83
            r25 = 0
            r27 = 0
            r28 = 1118306304(0x42a80000, float:84.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r0.addView(r1, r5)
            org.telegram.ui.Components.ChatAttachAlert$9 r0 = new org.telegram.ui.Components.ChatAttachAlert$9
            r0.<init>(r9)
            r8.buttonsRecyclerView = r0
            org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter
            r1.<init>(r9)
            r8.buttonsAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r9, r12, r12)
            r8.buttonsLayoutManager = r1
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            r0.setVerticalScrollBarEnabled(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            r0.setHorizontalScrollBarEnabled(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            r14 = 0
            r0.setItemAnimator(r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            r0.setLayoutAnimation(r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = r8.getThemedColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            java.lang.String r1 = "dialogBackground"
            int r1 = r8.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            r1 = 1
            r0.setImportantForAccessibility(r1)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r8.buttonsRecyclerView
            r5 = 84
            r6 = 83
            r7 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r5, (int) r6)
            r0.addView(r1, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35
            r1.<init>(r8, r11)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.buttonsRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36
            r1.<init>(r8)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.botMainButtonTextView = r0
            r1 = 8
            r0.setVisibility(r1)
            android.widget.TextView r0 = r8.botMainButtonTextView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.TextView r0 = r8.botMainButtonTextView
            r0.setSingleLine()
            android.widget.TextView r0 = r8.botMainButtonTextView
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r8.botMainButtonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r1)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.widget.TextView r1 = r8.botMainButtonTextView
            r1.setPadding(r0, r12, r0, r12)
            android.widget.TextView r0 = r8.botMainButtonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r8.botMainButtonTextView
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda7
            r1.<init>(r8)
            r0.setOnClickListener(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r1 = r8.botMainButtonTextView
            r2 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r3, (int) r6)
            r0.addView(r1, r3)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r9)
            r8.botProgressView = r0
            r1 = 1099956224(0x41900000, float:18.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setSize(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r8.botProgressView
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r8.botProgressView
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r8.botProgressView
            r0.setScaleY(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r8.botProgressView
            r1 = 8
            r0.setVisibility(r1)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.RadialProgressView r1 = r8.botProgressView
            r22 = 28
            r23 = 1105199104(0x41e00000, float:28.0)
            r24 = 85
            r27 = 1092616192(0x41200000, float:10.0)
            r28 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$10 r0 = new org.telegram.ui.Components.ChatAttachAlert$10
            r0.<init>(r9, r10)
            r8.frameLayout2 = r0
            r0.setWillNotDraw(r12)
            android.widget.FrameLayout r0 = r8.frameLayout2
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r8.frameLayout2
            r1 = 0
            r0.setAlpha(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.FrameLayout r1 = r8.frameLayout2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r4, (int) r6)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r8.frameLayout2
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda14 r1 = org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda14.INSTANCE
            r0.setOnTouchListener(r1)
            org.telegram.ui.Components.NumberTextView r0 = new org.telegram.ui.Components.NumberTextView
            r0.<init>(r9)
            r8.captionLimitView = r0
            r1 = 8
            r0.setVisibility(r1)
            r1 = 15
            r0.setTextSize(r1)
            java.lang.String r1 = "windowBackgroundWhiteGrayText"
            int r1 = r8.getThemedColor(r1)
            r0.setTextColor(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r1)
            r1 = 1
            r0.setCenterAlign(r1)
            android.widget.FrameLayout r1 = r8.frameLayout2
            r22 = 56
            r23 = 1101004800(0x41a00000, float:20.0)
            r25 = 1077936128(0x40400000, float:3.0)
            r27 = 1096810496(0x41600000, float:14.0)
            r28 = 1117519872(0x429CLASSNAME, float:78.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r1.addView(r0, r2)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.getCaptionMaxLengthLimit()
            r8.currentLimit = r0
            org.telegram.ui.Components.ChatAttachAlert$11 r15 = new org.telegram.ui.Components.ChatAttachAlert$11
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r8.sizeNotifierFrameLayout
            r4 = 0
            r5 = 1
            r6 = 1
            r0 = r15
            r1 = r29
            r2 = r30
            r7 = r34
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.commentTextView = r15
            r0 = 2131624263(0x7f0e0147, float:1.88757E38)
            java.lang.String r1 = "AddCaption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r15.setHint(r0)
            org.telegram.ui.Components.EditTextEmoji r0 = r8.commentTextView
            r0.onResume()
            org.telegram.ui.Components.EditTextEmoji r0 = r8.commentTextView
            org.telegram.ui.Components.EditTextCaption r0 = r0.getEditText()
            org.telegram.ui.Components.ChatAttachAlert$12 r1 = new org.telegram.ui.Components.ChatAttachAlert$12
            r1.<init>()
            r0.addTextChangedListener(r1)
            android.widget.FrameLayout r0 = r8.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r1 = r8.commentTextView
            r22 = -1
            r23 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r24 = 83
            r25 = 0
            r27 = 1118306304(0x42a80000, float:84.0)
            r28 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r8.frameLayout2
            r0.setClipChildren(r12)
            org.telegram.ui.Components.EditTextEmoji r0 = r8.commentTextView
            r0.setClipChildren(r12)
            org.telegram.ui.Components.ChatAttachAlert$13 r0 = new org.telegram.ui.Components.ChatAttachAlert$13
            r0.<init>(r9)
            r8.writeButtonContainer = r0
            r1 = 1
            r0.setFocusable(r1)
            android.widget.FrameLayout r0 = r8.writeButtonContainer
            r0.setFocusableInTouchMode(r1)
            android.widget.FrameLayout r0 = r8.writeButtonContainer
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r8.writeButtonContainer
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r1)
            android.widget.FrameLayout r0 = r8.writeButtonContainer
            r0.setScaleY(r1)
            android.widget.FrameLayout r0 = r8.writeButtonContainer
            r2 = 0
            r0.setAlpha(r2)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.FrameLayout r2 = r8.writeButtonContainer
            r22 = 60
            r23 = 1114636288(0x42700000, float:60.0)
            r24 = 85
            r27 = 1086324736(0x40CLASSNAME, float:6.0)
            r28 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.writeButton = r0
            r0 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r3 = "dialogFloatingButton"
            int r3 = r8.getThemedColor(r3)
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r4 < r5) goto L_0x06b6
            java.lang.String r6 = "dialogFloatingButtonPressed"
            goto L_0x06b8
        L_0x06b6:
            java.lang.String r6 = "dialogFloatingButton"
        L_0x06b8:
            int r6 = r8.getThemedColor(r6)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r3, r6)
            r8.writeButtonDrawable = r2
            if (r4 >= r5) goto L_0x06f3
            android.content.res.Resources r2 = r30.getResources()
            r3 = 2131165415(0x7var_e7, float:1.7945046E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r6, r7)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.Drawable r6 = r8.writeButtonDrawable
            r3.<init>(r2, r6, r12, r12)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3.setIconSize(r2, r6)
            r8.writeButtonDrawable = r3
        L_0x06f3:
            android.widget.ImageView r2 = r8.writeButton
            android.graphics.drawable.Drawable r3 = r8.writeButtonDrawable
            r2.setBackgroundDrawable(r3)
            android.widget.ImageView r2 = r8.writeButton
            r3 = 2131165264(0x7var_, float:1.794474E38)
            r2.setImageResource(r3)
            android.widget.ImageView r2 = r8.writeButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "dialogFloatingIcon"
            int r6 = r8.getThemedColor(r6)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r6, r7)
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r8.writeButton
            r3 = 2
            r2.setImportantForAccessibility(r3)
            android.widget.ImageView r2 = r8.writeButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            if (r4 < r5) goto L_0x072d
            android.widget.ImageView r2 = r8.writeButton
            org.telegram.ui.Components.ChatAttachAlert$14 r3 = new org.telegram.ui.Components.ChatAttachAlert$14
            r3.<init>(r8)
            r2.setOutlineProvider(r3)
        L_0x072d:
            android.widget.FrameLayout r2 = r8.writeButtonContainer
            android.widget.ImageView r3 = r8.writeButton
            if (r4 < r5) goto L_0x0738
            r6 = 56
            r21 = 56
            goto L_0x073c
        L_0x0738:
            r6 = 60
            r21 = 60
        L_0x073c:
            if (r4 < r5) goto L_0x0741
            r22 = 1113587712(0x42600000, float:56.0)
            goto L_0x0745
        L_0x0741:
            r0 = 1114636288(0x42700000, float:60.0)
            r22 = 1114636288(0x42700000, float:60.0)
        L_0x0745:
            r23 = 51
            if (r4 < r5) goto L_0x074e
            r0 = 1073741824(0x40000000, float:2.0)
            r24 = 1073741824(0x40000000, float:2.0)
            goto L_0x0750
        L_0x074e:
            r24 = 0
        L_0x0750:
            r25 = 0
            r26 = 0
            r27 = 0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r2.addView(r3, r0)
            android.widget.ImageView r0 = r8.writeButton
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11
            r2.<init>(r8, r11)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r8.writeButton
            org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13
            r2.<init>(r8, r11)
            r0.setOnLongClickListener(r2)
            android.text.TextPaint r0 = r8.textPaint
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = r8.textPaint
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r0.setTypeface(r2)
            org.telegram.ui.Components.ChatAttachAlert$16 r0 = new org.telegram.ui.Components.ChatAttachAlert$16
            r0.<init>(r9)
            r8.selectedCountView = r0
            r2 = 0
            r0.setAlpha(r2)
            android.view.View r0 = r8.selectedCountView
            r0.setScaleX(r1)
            android.view.View r0 = r8.selectedCountView
            r0.setScaleY(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r1 = r8.selectedCountView
            r15 = 42
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
            r17 = 85
            r18 = 0
            r19 = 0
            r20 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r21 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r0.addView(r1, r2)
            if (r10 == 0) goto L_0x07bb
            r29.checkColors()
            r8.navBarColorKey = r14
        L_0x07bb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        this.selectedMenuItem.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(boolean z, View view) {
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
        if (z) {
            this.baseFragment.showAsSheet(photoPickerSearchActivity);
        } else {
            this.baseFragment.presentFragment(photoPickerSearchActivity);
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        updatePhotoPreview(this.currentAttachLayout != this.photoPreviewLayout);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (this.baseFragment.getParentActivity() != null) {
            if (view instanceof AttachButton) {
                int intValue = ((Integer) view.getTag()).intValue();
                if (intValue == 1) {
                    showLayout(this.photoLayout);
                } else if (intValue == 3) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                        openAudioLayout(true);
                    } else {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    }
                } else if (intValue == 4) {
                    if (Build.VERSION.SDK_INT < 23 || this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                        openDocumentsLayout(true);
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
                            ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = new ChatAttachAlertLocationLayout(this, getContext(), resourcesProvider);
                            this.locationLayout = chatAttachAlertLocationLayout;
                            attachAlertLayoutArr[5] = chatAttachAlertLocationLayout;
                            chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda33(this));
                        }
                        showLayout(this.locationLayout);
                    } else {
                        return;
                    }
                } else if (intValue == 9) {
                    if (this.pollLayout == null) {
                        AttachAlertLayout[] attachAlertLayoutArr2 = this.layouts;
                        ChatAttachAlertPollLayout chatAttachAlertPollLayout = new ChatAttachAlertPollLayout(this, getContext(), resourcesProvider);
                        this.pollLayout = chatAttachAlertPollLayout;
                        attachAlertLayoutArr2[1] = chatAttachAlertPollLayout;
                        chatAttachAlertPollLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda34(this));
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
            if (view.getX() + ((float) view.getWidth()) >= ((float) (this.buttonsRecyclerView.getMeasuredWidth() - AndroidUtilities.dp(32.0f)))) {
                this.buttonsRecyclerView.smoothScrollBy((int) (((float) view.getWidth()) * 1.5f), 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        ((ChatActivity) this.baseFragment).didSelectLocation(tLRPC$MessageMedia, i, z, i2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendPoll(tLRPC$TL_messageMediaPoll, hashMap, z, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$8(View view, int i) {
        if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (!(this.baseFragment == null || attachBotButton.currentUser == null)) {
                onLongClickBotButton(attachBotButton.attachMenuBot, attachBotButton.currentUser);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(View view) {
        ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout;
        long j = this.selectedId;
        if (j < 0 && (chatAttachAlertBotWebViewLayout = this.botAttachLayouts.get(-j)) != null) {
            chatAttachAlertBotWebViewLayout.getWebViewContainer().onMainButtonPressed();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$12(Theme.ResourcesProvider resourcesProvider, View view) {
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
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlert$$ExternalSyntheticLambda29(this), resourcesProvider);
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

    /* access modifiers changed from: private */
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$16(Theme.ResourcesProvider resourcesProvider, View view) {
        BaseFragment baseFragment2 = this.baseFragment;
        if ((baseFragment2 instanceof ChatActivity) && this.editingMessageObject == null && this.currentLimit - this.codepointCount >= 0) {
            ChatActivity chatActivity = (ChatActivity) baseFragment2;
            chatActivity.getCurrentChat();
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            if (chatActivity.isInScheduleMode()) {
                return false;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), resourcesProvider);
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
            this.sendPopupLayout.setDispatchKeyEventListener(new ChatAttachAlert$$ExternalSyntheticLambda28(this));
            this.sendPopupLayout.setShownFromBottom(false);
            this.itemCells = new ActionBarMenuSubItem[2];
            int i = 0;
            while (i < 2) {
                if (i == 0) {
                    if (chatActivity.canScheduleMessage()) {
                        if (!this.currentAttachLayout.canScheduleMessages()) {
                        }
                    }
                    i++;
                } else if (i == 1 && UserObject.isUserSelf(currentUser)) {
                    i++;
                }
                this.itemCells[i] = new ActionBarMenuSubItem(getContext(), i == 0, i == 1, resourcesProvider);
                if (i == 0) {
                    if (UserObject.isUserSelf(currentUser)) {
                        this.itemCells[i].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                    } else {
                        this.itemCells[i].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                    }
                } else if (i == 1) {
                    this.itemCells[i].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                }
                this.itemCells[i].setMinimumWidth(AndroidUtilities.dp(196.0f));
                this.sendPopupLayout.addView(this.itemCells[i], LayoutHelper.createLinear(-1, 48));
                this.itemCells[i].setOnClickListener(new ChatAttachAlert$$ExternalSyntheticLambda10(this, i, chatActivity, resourcesProvider));
                i++;
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
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view.performHapticFeedback(3, 2);
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$13(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$15(int i, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlert$$ExternalSyntheticLambda30(this), resourcesProvider);
        } else if (i == 1) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
                sendPressed(false, 0);
                return;
            }
            attachAlertLayout.sendSelectedItems(false, 0);
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$14(boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i);
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i);
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(getThemedColor(this.forceDarkTheme ? "voipgroup_listViewBackground" : "dialogBackground")) > 0.699999988079071d;
    }

    public void onLongClickBotButton(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLRPC$User tLRPC$User) {
        String userName = tLRPC$TL_attachMenuBot != null ? tLRPC$TL_attachMenuBot.short_name : UserObject.getUserName(tLRPC$User);
        new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(tLRPC$TL_attachMenuBot != null ? LocaleController.formatString("BotRemoveFromMenu", NUM, userName) : LocaleController.formatString("BotRemoveInlineFromMenu", NUM, userName))).setPositiveButton(LocaleController.getString("OK", NUM), new ChatAttachAlert$$ExternalSyntheticLambda4(this, tLRPC$TL_attachMenuBot, tLRPC$User)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClickBotButton$19(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        if (tLRPC$TL_attachMenuBot != null) {
            TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
            tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(tLRPC$User);
            tLRPC$TL_messages_toggleBotInAttachMenu.enabled = false;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new ChatAttachAlert$$ExternalSyntheticLambda26(this, tLRPC$TL_attachMenuBot), 66);
            return;
        }
        MediaDataController.getInstance(this.currentAccount).removeInline(tLRPC$User.id);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClickBotButton$18(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda20(this, tLRPC$TL_attachMenuBot));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClickBotButton$17(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        MediaDataController.getInstance(this.currentAccount).loadAttachMenuBots(false, true);
        if (this.currentAttachLayout == this.botAttachLayouts.get(tLRPC$TL_attachMenuBot.bot_id)) {
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
            this.delegate.didPressedButton(7, true, z, i, false);
        }
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

    private void showLayout(AttachAlertLayout attachAlertLayout, long j) {
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
                this.container.setLayerType(2, (Paint) null);
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
            final ChatAttachAlert$$ExternalSyntheticLambda19 chatAttachAlert$$ExternalSyntheticLambda19 = new ChatAttachAlert$$ExternalSyntheticLambda19(this);
            if ((this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                int max = Math.max(this.nextAttachLayout.getWidth(), this.currentAttachLayout.getWidth());
                AttachAlertLayout attachAlertLayout5 = this.nextAttachLayout;
                if (attachAlertLayout5 instanceof ChatAttachAlertPhotoLayoutPreview) {
                    attachAlertLayout5.setTranslationX((float) max);
                    AttachAlertLayout attachAlertLayout6 = this.currentAttachLayout;
                    if ((attachAlertLayout6 instanceof ChatAttachAlertPhotoLayout) && (cameraView2 = chatAttachAlertPhotoLayout2.cameraView) != null) {
                        cameraView2.setVisibility(4);
                        (chatAttachAlertPhotoLayout2 = (ChatAttachAlertPhotoLayout) attachAlertLayout6).cameraIcon.setVisibility(4);
                        chatAttachAlertPhotoLayout2.cameraCell.setVisibility(0);
                    }
                } else {
                    this.currentAttachLayout.setTranslationX((float) (-max));
                    AttachAlertLayout attachAlertLayout7 = this.nextAttachLayout;
                    if (attachAlertLayout7 == this.photoLayout && (cameraView = chatAttachAlertPhotoLayout.cameraView) != null) {
                        cameraView.setVisibility(0);
                        (chatAttachAlertPhotoLayout = (ChatAttachAlertPhotoLayout) attachAlertLayout7).cameraIcon.setVisibility(0);
                    }
                }
                this.nextAttachLayout.setAlpha(1.0f);
                this.currentAttachLayout.setAlpha(1.0f);
                this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(0.0f));
                AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda22(this, attachAlertLayout, chatAttachAlert$$ExternalSyntheticLambda19));
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.nextAttachLayout.setAlpha(0.0f);
            this.nextAttachLayout.setTranslationY((float) AndroidUtilities.dp(78.0f));
            AttachAlertLayout attachAlertLayout8 = this.currentAttachLayout;
            Property property = View.TRANSLATION_Y;
            float[] fArr = {(float) (AndroidUtilities.dp(78.0f) + firstOffset)};
            ActionBar actionBar2 = this.actionBar;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(attachAlertLayout8, property, fArr), ObjectAnimator.ofFloat(this.currentAttachLayout, this.ATTACH_ALERT_LAYOUT_TRANSLATION, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(actionBar2, View.ALPHA, new float[]{actionBar2.getAlpha(), 0.0f})});
            animatorSet.setDuration(180);
            animatorSet.setStartDelay(20);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
                    SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
                    springAnimation.getSpring().setDampingRatio(0.75f);
                    springAnimation.getSpring().setStiffness(500.0f);
                    springAnimation.addUpdateListener(new ChatAttachAlert$17$$ExternalSyntheticLambda1(this));
                    springAnimation.addEndListener(new ChatAttachAlert$17$$ExternalSyntheticLambda0(chatAttachAlert$$ExternalSyntheticLambda19));
                    Object unused = ChatAttachAlert.this.viewChangeAnimator = springAnimation;
                    springAnimation.start();
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0(DynamicAnimation dynamicAnimation, float f, float f2) {
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert.this.updateSelectedPosition(1);
                    }
                    ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                    ChatAttachAlert.this.containerView.invalidate();
                }
            });
            this.viewChangeAnimator = animatorSet;
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLayout$20() {
        AttachAlertLayout attachAlertLayout;
        ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview;
        if (Build.VERSION.SDK_INT >= 20) {
            this.container.setLayerType(0, (Paint) null);
        }
        this.viewChangeAnimator = null;
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        if (!(attachAlertLayout2 == this.photoLayout || (attachAlertLayout = this.nextAttachLayout) == (chatAttachAlertPhotoLayoutPreview = this.photoPreviewLayout) || attachAlertLayout2 == attachAlertLayout || attachAlertLayout2 == chatAttachAlertPhotoLayoutPreview)) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLayout$23(AttachAlertLayout attachAlertLayout, Runnable runnable) {
        float alpha = this.actionBar.getAlpha();
        boolean z = this.nextAttachLayout.getCurrentItemTop() <= attachAlertLayout.getButtonsHideOffset();
        float f = z ? 1.0f : 0.0f;
        SpringAnimation springAnimation = new SpringAnimation(new FloatValueHolder(0.0f));
        springAnimation.addUpdateListener(new ChatAttachAlert$$ExternalSyntheticLambda17(this, alpha, f, z));
        springAnimation.addEndListener(new ChatAttachAlert$$ExternalSyntheticLambda16(this, z, runnable));
        springAnimation.setSpring(new SpringForce(500.0f));
        springAnimation.getSpring().setDampingRatio(1.0f);
        springAnimation.getSpring().setStiffness(1000.0f);
        springAnimation.start();
        this.viewChangeAnimator = springAnimation;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLayout$21(float f, float f2, boolean z, DynamicAnimation dynamicAnimation, float f3, float f4) {
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
        this.selectedView.setTranslationX(f5 * ((float) (-AndroidUtilities.dp(16.0f))));
        this.mediaPreviewView.setTranslationX(f6 * ((float) AndroidUtilities.dp(16.0f)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showLayout$22(boolean z, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        this.currentAttachLayout.onHideShowProgress(1.0f);
        this.nextAttachLayout.onHideShowProgress(1.0f);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.nextAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.containerView.invalidate();
        this.actionBar.setTag(z ? 1 : null);
        runnable.run();
    }

    public void updatePhotoPreview(boolean z) {
        if (!z) {
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

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout;
        if (i == 5 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
            openContactsLayout();
        } else if (i == 30 && (chatAttachAlertLocationLayout = this.locationLayout) != null && this.currentAttachLayout == chatAttachAlertLocationLayout && isShowing()) {
            this.locationLayout.openShareLiveLocation();
        }
    }

    private void openContactsLayout() {
        if (this.contactsLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = new ChatAttachAlertContactsLayout(this, getContext(), this.resourcesProvider);
            this.contactsLayout = chatAttachAlertContactsLayout;
            attachAlertLayoutArr[2] = chatAttachAlertContactsLayout;
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda32(this));
        }
        showLayout(this.contactsLayout);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openContactsLayout$24(TLRPC$User tLRPC$User, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendContact(tLRPC$User, z, i);
    }

    /* access modifiers changed from: private */
    public void openAudioLayout(boolean z) {
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext(), this.resourcesProvider);
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlert$$ExternalSyntheticLambda31(this));
        }
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
            this.audioLayout.setMaxSelectedFiles(((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) ? -1 : 1);
        }
        if (z) {
            showLayout(this.audioLayout);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openAudioLayout$25(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendAudio(arrayList, charSequence, z, i);
    }

    private void openDocumentsLayout(boolean z) {
        if (this.documentLayout == null) {
            int i = this.isSoundPicker ? 2 : 0;
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), i, this.resourcesProvider);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() {
                public void didSelectFiles(ArrayList<String> arrayList, String str, ArrayList<MessageObject> arrayList2, boolean z, int i) {
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) baseFragment).didSelectFiles(arrayList, str, arrayList2, z, i);
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
                    if (baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) baseFragment).startDocumentSelectActivity();
                    } else if (baseFragment instanceof PassportActivity) {
                        ((PassportActivity) baseFragment).startDocumentSelectActivity();
                    }
                }

                public void startMusicSelectActivity() {
                    ChatAttachAlert.this.openAudioLayout(true);
                }
            });
        }
        BaseFragment baseFragment2 = this.baseFragment;
        int i2 = 1;
        if (baseFragment2 instanceof ChatActivity) {
            TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
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
            } else if (this.typeButtonsAvailable) {
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
            } else if (!this.isSoundPicker) {
                this.shadow.setTranslationY(((float) AndroidUtilities.dp(36.0f)) + this.botMainButtonOffsetY);
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
            this.commentsAnimator.setDuration(180);
            this.commentsAnimator.addListener(new AnimatorListenerAdapter() {
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
            View view8 = this.selectedCountView;
            if (z) {
                f = 1.0f;
            }
            view8.setScaleY(f);
            this.selectedCountView.setAlpha(z ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY(((float) (z ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f))) + this.botMainButtonOffsetY);
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
                        f2 = (float) AndroidUtilities.dp(36.0f);
                    }
                    recyclerListView2.setTranslationY(f2);
                }
                View view10 = this.shadow;
                if (z) {
                    i = AndroidUtilities.dp(36.0f);
                }
                view10.setTranslationY(((float) i) + this.botMainButtonOffsetY);
            } else {
                this.shadow.setTranslationY(((float) AndroidUtilities.dp(36.0f)) + this.botMainButtonOffsetY);
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

    /* access modifiers changed from: protected */
    public void cancelSheetAnimation() {
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
        ValueAnimator valueAnimator = this.navigationBarAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.navigationBarAlpha, 1.0f});
        this.navigationBarAnimation = ofFloat;
        ofFloat.addUpdateListener(new ChatAttachAlert$$ExternalSyntheticLambda0(this));
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
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.currentSheetAnimation = animatorSet2;
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
        final ChatAttachAlert$$ExternalSyntheticLambda21 chatAttachAlert$$ExternalSyntheticLambda21 = new ChatAttachAlert$$ExternalSyntheticLambda21(this, this.delegate);
        this.appearSpringAnimation.addEndListener(new ChatAttachAlert$$ExternalSyntheticLambda15(this, chatAttachAlert$$ExternalSyntheticLambda21));
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animator) && ChatAttachAlert.this.appearSpringAnimation != null && !ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                    chatAttachAlert$$ExternalSyntheticLambda21.run();
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animator)) {
                    AnimatorSet unused = ChatAttachAlert.this.currentSheetAnimation = null;
                    int unused2 = ChatAttachAlert.this.currentSheetAnimationType = 0;
                }
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        setNavBarAlpha(0.0f);
        ofFloat2.addUpdateListener(new ChatAttachAlert$$ExternalSyntheticLambda1(this));
        ofFloat2.setStartDelay(25);
        ofFloat2.setDuration(200);
        ofFloat2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat2.start();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomOpenAnimation$26(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BottomSheet.ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomOpenAnimation$27(BottomSheet.BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.currentSheetAnimation = null;
        this.appearSpringAnimation = null;
        this.currentSheetAnimationType = 0;
        if (bottomSheetDelegateInterface != null) {
            bottomSheetDelegateInterface.onOpenAnimationEnd();
        }
        if (this.useHardwareLayer) {
            this.container.setLayerType(0, (Paint) null);
        }
        if (this.isFullscreen) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags &= -1025;
            getWindow().setAttributes(attributes);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomOpenAnimation$28(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null && !animatorSet.isRunning()) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomOpenAnimation$29(ValueAnimator valueAnimator) {
        setNavBarAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setNavBarAlpha(float f) {
        boolean z = false;
        this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), Math.min(255, Math.max(0, (int) (f * 255.0f))));
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
        Window window = getWindow();
        if (((double) AndroidUtilities.computePerceivedBrightness(this.navBarColor)) > 0.721d) {
            z = true;
        }
        AndroidUtilities.setLightNavigationBar(window, z);
        getContainer().invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.currentAttachLayout.onContainerViewTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(EditTextBoldCursor editTextBoldCursor, boolean z) {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null && !this.enterCommentEventSent) {
            boolean needEnterComment = chatAttachViewDelegate.needEnterComment();
            this.enterCommentEventSent = true;
            AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda23(this, editTextBoldCursor, z), needEnterComment ? 200 : 0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeFocusable$31(EditTextBoldCursor editTextBoldCursor, boolean z) {
        setFocusable(true);
        editTextBoldCursor.requestFocus();
        if (z) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlert$$ExternalSyntheticLambda24(editTextBoldCursor));
        }
    }

    /* access modifiers changed from: private */
    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            AttachButton attachButton = (AttachButton) view;
            attachButton.textView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), getThemedColor(attachButton.textKey), attachButton.checkedState));
        } else if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            attachBotButton.nameTextView.setTextColor(ColorUtils.blendARGB(getThemedColor("dialogTextGray2"), attachBotButton.textColor, attachBotButton.checkedState));
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
        String str;
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView != null) {
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.reloadInlineHints || i == NotificationCenter.attachMenuBotsDidLoad) {
            ButtonsAdapter buttonsAdapter2 = this.buttonsAdapter;
            if (buttonsAdapter2 != null) {
                buttonsAdapter2.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        }
    }

    /* access modifiers changed from: private */
    public int getScrollOffsetY(int i) {
        AttachAlertLayout attachAlertLayout = this.nextAttachLayout;
        if (attachAlertLayout == null || (!(this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) && !(attachAlertLayout instanceof ChatAttachAlertPhotoLayoutPreview))) {
            return this.scrollOffsetY[i];
        }
        int[] iArr = this.scrollOffsetY;
        return AndroidUtilities.lerp(iArr[0], iArr[1], this.translationProgress);
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition(int i) {
        int i2;
        int i3;
        float f;
        int i4;
        int i5;
        AttachAlertLayout attachAlertLayout = i == 0 ? this.currentAttachLayout : this.nextAttachLayout;
        int scrollOffsetY2 = getScrollOffsetY(i);
        int i6 = scrollOffsetY2 - this.backgroundPaddingTop;
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
        float dp = this.actionBar.getAlpha() != 0.0f ? 0.0f : (float) AndroidUtilities.dp((1.0f - this.headerView.getAlpha()) * 26.0f);
        if (!this.menuShowed || this.avatarPicker != 0) {
            this.selectedMenuItem.setTranslationY(((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (i4 + 37)))) + this.currentPanTranslationY);
        } else {
            this.selectedMenuItem.setTranslationY(((float) (scrollOffsetY2 - AndroidUtilities.dp((((float) i4) * f) + 37.0f))) + dp + this.currentPanTranslationY);
        }
        this.searchItem.setTranslationY(((float) ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp((float) (i4 + 37)))) + this.currentPanTranslationY);
        FrameLayout frameLayout = this.headerView;
        float dp2 = ((float) (scrollOffsetY2 - AndroidUtilities.dp((((float) i4) * f) + 25.0f))) + dp + this.currentPanTranslationY;
        this.baseSelectedTextViewTranslationY = dp2;
        frameLayout.setTranslationY(dp2);
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout != null && attachAlertLayout == chatAttachAlertPollLayout) {
            if (AndroidUtilities.isTablet()) {
                i5 = 63;
            } else {
                Point point2 = AndroidUtilities.displaySize;
                i5 = point2.x > point2.y ? 53 : 59;
            }
            this.doneItem.setTranslationY(Math.max(0.0f, (this.pollLayout.getTranslationY() + ((float) scrollOffsetY2)) - ((float) AndroidUtilities.dp((((float) i5) * f) + 7.0f))) + this.currentPanTranslationY);
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
    private void updateActionBarVisibility(final boolean r11, boolean r12) {
        /*
            r10 = this;
            if (r11 == 0) goto L_0x000a
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0014
        L_0x000a:
            if (r11 != 0) goto L_0x0197
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0197
        L_0x0014:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 0
            r2 = 1
            if (r11 == 0) goto L_0x001f
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            goto L_0x0020
        L_0x001f:
            r3 = r1
        L_0x0020:
            r0.setTag(r3)
            android.animation.AnimatorSet r0 = r10.actionBarAnimation
            if (r0 == 0) goto L_0x002c
            r0.cancel()
            r10.actionBarAnimation = r1
        L_0x002c:
            boolean r0 = r10.avatarSearch
            r1 = 0
            if (r0 != 0) goto L_0x004c
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r0 = r10.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r3 = r10.photoLayout
            if (r0 != r3) goto L_0x004a
            boolean r0 = r10.menuShowed
            if (r0 != 0) goto L_0x004a
            org.telegram.ui.ActionBar.BaseFragment r0 = r10.baseFragment
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
            int r3 = r10.avatarPicker
            if (r3 != 0) goto L_0x0062
            boolean r3 = r10.menuShowed
            if (r3 != 0) goto L_0x0060
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r3 = r10.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout r4 = r10.photoLayout
            if (r3 != r4) goto L_0x0060
            boolean r3 = r10.mediaEnabled
            if (r3 == 0) goto L_0x0060
            goto L_0x0062
        L_0x0060:
            r3 = 0
            goto L_0x0063
        L_0x0062:
            r3 = 1
        L_0x0063:
            if (r11 == 0) goto L_0x0074
            if (r0 == 0) goto L_0x006c
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r10.searchItem
            r4.setVisibility(r1)
        L_0x006c:
            if (r3 == 0) goto L_0x0085
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r10.selectedMenuItem
            r4.setVisibility(r1)
            goto L_0x0085
        L_0x0074:
            boolean r4 = r10.typeButtonsAvailable
            if (r4 == 0) goto L_0x0085
            android.widget.FrameLayout r4 = r10.frameLayout2
            java.lang.Object r4 = r4.getTag()
            if (r4 != 0) goto L_0x0085
            org.telegram.ui.Components.RecyclerListView r4 = r10.buttonsRecyclerView
            r4.setVisibility(r1)
        L_0x0085:
            android.view.Window r4 = r10.getWindow()
            if (r4 == 0) goto L_0x00aa
            org.telegram.ui.ActionBar.BaseFragment r4 = r10.baseFragment
            if (r4 == 0) goto L_0x00aa
            if (r11 == 0) goto L_0x009d
            android.view.Window r4 = r10.getWindow()
            boolean r5 = r10.isLightStatusBar()
            org.telegram.messenger.AndroidUtilities.setLightStatusBar(r4, r5)
            goto L_0x00aa
        L_0x009d:
            android.view.Window r4 = r10.getWindow()
            org.telegram.ui.ActionBar.BaseFragment r5 = r10.baseFragment
            boolean r5 = r5.isLightStatusBar()
            org.telegram.messenger.AndroidUtilities.setLightStatusBar(r4, r5)
        L_0x00aa:
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 0
            if (r12 == 0) goto L_0x013f
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r10.actionBarAnimation = r12
            r6 = 1127481344(0x43340000, float:180.0)
            if (r11 == 0) goto L_0x00bd
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x00be
        L_0x00bd:
            r7 = 0
        L_0x00be:
            org.telegram.ui.ActionBar.ActionBar r8 = r10.actionBar
            float r8 = r8.getAlpha()
            float r7 = r7 - r8
            float r7 = java.lang.Math.abs(r7)
            float r7 = r7 * r6
            long r6 = (long) r7
            r12.setDuration(r6)
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            org.telegram.ui.ActionBar.ActionBar r6 = r10.actionBar
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            if (r11 == 0) goto L_0x00df
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x00e0
        L_0x00df:
            r9 = 0
        L_0x00e0:
            r8[r1] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r12.add(r6)
            android.view.View r6 = r10.actionBarShadow
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            if (r11 == 0) goto L_0x00f4
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x00f5
        L_0x00f4:
            r9 = 0
        L_0x00f5:
            r8[r1] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r12.add(r6)
            if (r0 == 0) goto L_0x0115
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r2]
            if (r11 == 0) goto L_0x010b
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x010c
        L_0x010b:
            r8 = 0
        L_0x010c:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r12.add(r0)
        L_0x0115:
            if (r3 == 0) goto L_0x012a
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.selectedMenuItem
            android.util.Property r3 = android.view.View.ALPHA
            float[] r2 = new float[r2]
            if (r11 == 0) goto L_0x0120
            goto L_0x0121
        L_0x0120:
            r4 = 0
        L_0x0121:
            r2[r1] = r4
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r3, r2)
            r12.add(r0)
        L_0x012a:
            android.animation.AnimatorSet r0 = r10.actionBarAnimation
            r0.playTogether(r12)
            android.animation.AnimatorSet r12 = r10.actionBarAnimation
            org.telegram.ui.Components.ChatAttachAlert$22 r0 = new org.telegram.ui.Components.ChatAttachAlert$22
            r0.<init>(r11)
            r12.addListener(r0)
            android.animation.AnimatorSet r11 = r10.actionBarAnimation
            r11.start()
            goto L_0x0197
        L_0x013f:
            r12 = 4
            if (r11 == 0) goto L_0x0155
            boolean r1 = r10.typeButtonsAvailable
            if (r1 == 0) goto L_0x0155
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r1 = r10.currentAttachLayout
            if (r1 == 0) goto L_0x0150
            boolean r1 = r1.shouldHideBottomButtons()
            if (r1 == 0) goto L_0x0155
        L_0x0150:
            org.telegram.ui.Components.RecyclerListView r1 = r10.buttonsRecyclerView
            r1.setVisibility(r12)
        L_0x0155:
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            if (r11 == 0) goto L_0x015c
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x015d
        L_0x015c:
            r2 = 0
        L_0x015d:
            r1.setAlpha(r2)
            android.view.View r1 = r10.actionBarShadow
            if (r11 == 0) goto L_0x0167
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0168
        L_0x0167:
            r2 = 0
        L_0x0168:
            r1.setAlpha(r2)
            if (r0 == 0) goto L_0x0178
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            if (r11 == 0) goto L_0x0174
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0175
        L_0x0174:
            r1 = 0
        L_0x0175:
            r0.setAlpha(r1)
        L_0x0178:
            if (r3 == 0) goto L_0x0183
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.selectedMenuItem
            if (r11 == 0) goto L_0x017f
            goto L_0x0180
        L_0x017f:
            r4 = 0
        L_0x0180:
            r0.setAlpha(r4)
        L_0x0183:
            if (r11 != 0) goto L_0x0197
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r10.searchItem
            r11.setVisibility(r12)
            int r11 = r10.avatarPicker
            if (r11 != 0) goto L_0x0192
            boolean r11 = r10.menuShowed
            if (r11 != 0) goto L_0x0197
        L_0x0192:
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r10.selectedMenuItem
            r11.setVisibility(r12)
        L_0x0197:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateActionBarVisibility(boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0063, code lost:
        if (((androidx.dynamicanimation.animation.SpringAnimation) r8).isRunning() != false) goto L_0x0067;
     */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateLayout(org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r7, boolean r8, int r9) {
        /*
            r6 = this;
            if (r7 != 0) goto L_0x0003
            return
        L_0x0003:
            int r0 = r7.getCurrentItemTop()
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto L_0x000d
            return
        L_0x000d:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r1 = r6.currentAttachLayout
            r2 = 1
            r3 = 0
            if (r7 != r1) goto L_0x001b
            int r1 = r7.getButtonsHideOffset()
            if (r0 > r1) goto L_0x001b
            r1 = 1
            goto L_0x001c
        L_0x001b:
            r1 = 0
        L_0x001c:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r6.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = r6.photoPreviewLayout
            if (r4 == r5) goto L_0x002d
            boolean r5 = r6.keyboardVisible
            if (r5 == 0) goto L_0x002d
            if (r8 == 0) goto L_0x002d
            boolean r5 = r4 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout
            if (r5 != 0) goto L_0x002d
            r8 = 0
        L_0x002d:
            if (r7 != r4) goto L_0x0032
            r6.updateActionBarVisibility(r1, r8)
        L_0x0032:
            android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            if (r8 != 0) goto L_0x003c
            r8 = 0
            goto L_0x003e
        L_0x003c:
            int r8 = r8.topMargin
        L_0x003e:
            r1 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r8 = r8 - r1
            int r0 = r0 + r8
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r8 = r6.currentAttachLayout
            if (r8 != r7) goto L_0x004c
            r7 = 0
            goto L_0x004d
        L_0x004c:
            r7 = 1
        L_0x004d:
            boolean r8 = r8 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r8 != 0) goto L_0x0057
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r8 = r6.nextAttachLayout
            boolean r8 = r8 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r8 == 0) goto L_0x0066
        L_0x0057:
            java.lang.Object r8 = r6.viewChangeAnimator
            boolean r1 = r8 instanceof androidx.dynamicanimation.animation.SpringAnimation
            if (r1 == 0) goto L_0x0066
            androidx.dynamicanimation.animation.SpringAnimation r8 = (androidx.dynamicanimation.animation.SpringAnimation) r8
            boolean r8 = r8.isRunning()
            if (r8 == 0) goto L_0x0066
            goto L_0x0067
        L_0x0066:
            r2 = 0
        L_0x0067:
            int[] r8 = r6.scrollOffsetY
            r1 = r8[r7]
            if (r1 != r0) goto L_0x0077
            if (r2 == 0) goto L_0x0070
            goto L_0x0077
        L_0x0070:
            if (r9 == 0) goto L_0x0085
            r7 = r8[r7]
            r6.previousScrollOffsetY = r7
            goto L_0x0085
        L_0x0077:
            r9 = r8[r7]
            r6.previousScrollOffsetY = r9
            r8[r7] = r0
            r6.updateSelectedPosition(r7)
            android.view.ViewGroup r7 = r6.containerView
            r7.invalidate()
        L_0x0085:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateLayout(org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout, boolean, int):void");
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
            android.widget.FrameLayout r9 = r0.headerView
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
            android.widget.FrameLayout r1 = r0.headerView
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
            android.widget.FrameLayout r9 = r0.headerView
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
            org.telegram.ui.Components.ChatAttachAlert$23 r2 = new org.telegram.ui.Components.ChatAttachAlert$23
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
        AttachAlertLayout attachAlertLayout;
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
                TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
                TLRPC$User currentUser = ((ChatActivity) this.baseFragment).getCurrentUser();
                if (currentChat != null) {
                    this.mediaEnabled = ChatObject.canSendMedia(currentChat);
                    this.pollsEnabled = ChatObject.canSendPolls(currentChat);
                } else {
                    this.pollsEnabled = currentUser != null && currentUser.bot;
                }
            }
            this.photoLayout.onInit(this.mediaEnabled);
            this.commentTextView.hidePopup(true);
            this.enterCommentEventSent = false;
            setFocusable(false);
            if (this.isSoundPicker) {
                openDocumentsLayout(false);
                attachAlertLayout = this.documentLayout;
                this.selectedId = 4;
            } else {
                MessageObject messageObject = this.editingMessageObject;
                if (messageObject == null || (!messageObject.isMusic() && (!this.editingMessageObject.isDocument() || this.editingMessageObject.isGif()))) {
                    attachAlertLayout = this.photoLayout;
                    this.typeButtonsAvailable = this.avatarPicker == 0;
                    this.selectedId = 1;
                } else {
                    if (this.editingMessageObject.isMusic()) {
                        openAudioLayout(false);
                        attachAlertLayout = this.audioLayout;
                        this.selectedId = 3;
                    } else {
                        openDocumentsLayout(false);
                        attachAlertLayout = this.documentLayout;
                        this.selectedId = 4;
                    }
                    this.typeButtonsAvailable = !this.editingMessageObject.hasValidGroupId();
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
                attachAlertLayout.onShow((AttachAlertLayout) null);
                attachAlertLayout.onShown();
                ActionBar actionBar2 = this.actionBar;
                if (attachAlertLayout.needsActionBar() != 0) {
                    i2 = 0;
                }
                actionBar2.setVisibility(i2);
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
            view.setImportantForAccessibility(1);
            view.setFocusable(true);
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
                AttachBotButton attachBotButton = (AttachBotButton) viewHolder.itemView;
                int i2 = this.attachBotsStartRow;
                if (i < i2 || i >= this.attachBotsEndRow) {
                    int i3 = i - this.buttonsCount;
                    attachBotButton.setTag(Integer.valueOf(i3));
                    attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i3).peer.user_id)));
                    return;
                }
                int i4 = i - i2;
                attachBotButton.setTag(Integer.valueOf(i4));
                TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = this.attachMenuBots.get(i4);
                attachBotButton.setAttachBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(tLRPC$TL_attachMenuBot.bot_id)), tLRPC$TL_attachMenuBot);
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
                } else if ((!messageObject.isMusic() && !ChatAttachAlert.this.editingMessageObject.isDocument()) || !ChatAttachAlert.this.editingMessageObject.hasValidGroupId()) {
                    int i9 = this.buttonsCount;
                    int i10 = i9 + 1;
                    this.buttonsCount = i10;
                    this.galleryButton = i9;
                    int i11 = i10 + 1;
                    this.buttonsCount = i11;
                    this.documentButton = i10;
                    this.buttonsCount = i11 + 1;
                    this.musicButton = i11;
                } else if (ChatAttachAlert.this.editingMessageObject.isMusic()) {
                    int i12 = this.buttonsCount;
                    this.buttonsCount = i12 + 1;
                    this.musicButton = i12;
                } else {
                    int i13 = this.buttonsCount;
                    this.buttonsCount = i13 + 1;
                    this.documentButton = i13;
                }
            }
            super.notifyDataSetChanged();
        }

        public int getItemViewType(int i) {
            if (i >= this.buttonsCount) {
                return 1;
            }
            if (i < this.attachBotsStartRow || i >= this.attachBotsEndRow) {
                return 0;
            }
            return 1;
        }
    }

    public void dismissInternal() {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.doOnIdle(new ChatAttachAlert$$ExternalSyntheticLambda18(this));
        } else {
            removeFromRoot();
        }
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

    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        this.currentAttachLayout.onDismissWithButtonClick(i);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return this.currentAttachLayout.canDismissWithTouchOutside();
    }

    /* access modifiers changed from: protected */
    public void onDismissWithTouchOutside() {
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

    public void dismiss() {
        if (!this.currentAttachLayout.onDismiss() && !isDismissed()) {
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
            }
            this.botAttachLayouts.clear();
            if (this.allowPassConfirmationAlert || this.baseFragment == null || this.currentAttachLayout.getSelectedItemsCount() <= 0) {
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
                AndroidUtilities.setNavigationBarColor(getWindow(), ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundGray"), 0), true, new ChatAttachAlert$$ExternalSyntheticLambda25(this));
                if (this.baseFragment != null) {
                    AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
                }
                super.dismiss();
                this.allowPassConfirmationAlert = false;
            } else if (!this.confirmationAlertShown) {
                this.confirmationAlertShown = true;
                AlertDialog create = new AlertDialog.Builder(this.baseFragment.getParentActivity(), this.parentThemeDelegate).setTitle(LocaleController.getString("DiscardSelectionAlertTitle", NUM)).setMessage(LocaleController.getString("DiscardSelectionAlertMessage", NUM)).setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new ChatAttachAlert$$ExternalSyntheticLambda3(this)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).setOnCancelListener(new ChatAttachAlert$$ExternalSyntheticLambda2(this)).setOnPreDismissListener(new ChatAttachAlert$$ExternalSyntheticLambda5(this)).create();
                create.show();
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(getThemedColor("dialogTextRed2"));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$32(DialogInterface dialogInterface, int i) {
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$33(DialogInterface dialogInterface) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$34(DialogInterface dialogInterface) {
        this.confirmationAlertShown = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$35(int i) {
        this.navBarColorKey = null;
        this.navBarColor = i;
        this.containerView.invalidate();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.currentAttachLayout.onSheetKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

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
