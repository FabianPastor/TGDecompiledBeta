package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.FrameLayout;
import androidx.core.math.MathUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlert;

public class ChatAttachAlertBotWebViewLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int POLL_PERIOD = 60000;
    /* access modifiers changed from: private */
    public long botId;
    /* access modifiers changed from: private */
    public int currentAccount;
    private boolean destroyed;
    private boolean ignoreLayout;
    private boolean ignoreMeasure;
    /* access modifiers changed from: private */
    public boolean isBotButtonAvailable;
    private long lastSwipeTime;
    /* access modifiers changed from: private */
    public int measureOffsetY;
    private boolean needReload;
    private ActionBarMenuItem otherItem;
    private long peerId;
    private Runnable pollRunnable = new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda6(this);
    /* access modifiers changed from: private */
    public WebProgressView progressView;
    private long queryId;
    private int replyToMsgId;
    private boolean silent;
    private String startCommand;
    private WebViewSwipeContainer swipeContainer;
    /* access modifiers changed from: private */
    public BotWebViewContainer webViewContainer;
    /* access modifiers changed from: private */
    public ValueAnimator webViewScrollAnimator;

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3804xcCLASSNAMEdcad() {
        if (!this.destroyed) {
            TLRPC.TL_messages_prolongWebView prolongWebView = new TLRPC.TL_messages_prolongWebView();
            prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
            prolongWebView.query_id = this.queryId;
            prolongWebView.silent = this.silent;
            int i = this.replyToMsgId;
            if (i != 0) {
                prolongWebView.reply_to_msg_id = i;
                prolongWebView.flags |= 1;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(prolongWebView, new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3803xCLASSNAMEd114e(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda12(this, error));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3802xCLASSNAMEef(TLRPC.TL_error error) {
        if (!this.destroyed) {
            if (error != null) {
                this.parentAlert.dismiss();
            } else {
                AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
            }
        }
    }

    public ChatAttachAlertBotWebViewLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        ActionBarMenuItem addItem = this.parentAlert.actionBar.createMenu().addItem(0, NUM);
        this.otherItem = addItem;
        addItem.addSubItem(NUM, NUM, (CharSequence) LocaleController.getString(NUM));
        this.otherItem.addSubItem(NUM, NUM, (CharSequence) LocaleController.getString(NUM));
        this.otherItem.addSubItem(NUM, NUM, (CharSequence) LocaleController.getString(NUM));
        this.parentAlert.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.dismiss();
                } else if (id == NUM) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", ChatAttachAlertBotWebViewLayout.this.botId);
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.baseFragment.presentFragment(new ChatActivity(bundle));
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.dismiss();
                } else if (id == NUM) {
                    if (ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView() != null) {
                        ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().animate().cancel();
                        ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    }
                    ChatAttachAlertBotWebViewLayout.this.progressView.setLoadProgress(0.0f);
                    ChatAttachAlertBotWebViewLayout.this.progressView.setAlpha(1.0f);
                    ChatAttachAlertBotWebViewLayout.this.progressView.setVisibility(0);
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.setBotUser(MessagesController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getUser(Long.valueOf(ChatAttachAlertBotWebViewLayout.this.botId)));
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.loadFlicker(ChatAttachAlertBotWebViewLayout.this.currentAccount, ChatAttachAlertBotWebViewLayout.this.botId);
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.reload();
                } else if (id == NUM) {
                    Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getAttachMenuBots().bots.iterator();
                    while (it.hasNext()) {
                        TLRPC.TL_attachMenuBot bot = it.next();
                        if (bot.bot_id == ChatAttachAlertBotWebViewLayout.this.botId) {
                            ChatAttachAlertBotWebViewLayout.this.parentAlert.onLongClickBotButton(bot, MessagesController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getUser(Long.valueOf(ChatAttachAlertBotWebViewLayout.this.botId)));
                            return;
                        }
                    }
                }
            }
        });
        this.webViewContainer = new BotWebViewContainer(context, resourcesProvider, getThemedColor("dialogBackground")) {
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && !ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable) {
                    boolean unused = ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable = true;
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.restoreButtonData();
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        AnonymousClass3 r1 = new WebViewSwipeContainer(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(((View.MeasureSpec.getSize(heightMeasureSpec) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(84.0f)) + ChatAttachAlertBotWebViewLayout.this.measureOffsetY, NUM));
            }
        };
        this.swipeContainer = r1;
        r1.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda7(this));
        this.swipeContainer.setScrollEndListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda8(this));
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda3(this));
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f));
        WebProgressView webProgressView = new WebProgressView(context, resourcesProvider);
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 84.0f));
        this.webViewContainer.setWebViewProgressListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda5(this));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3805xd214a80c() {
        this.parentAlert.updateLayout(this, true, 0);
        this.webViewContainer.invalidateViewPortHeight();
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3806xd818736b() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3807xde1c3eca() {
        this.parentAlert.dismiss();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3809xea23d588(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda0(this));
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ChatAttachAlertBotWebViewLayout.this.progressView.setVisibility(8);
                }
            });
            animator.start();
            requestEnableKeyboard();
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3808xe4200a29(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    public boolean canExpandByRequest() {
        return !this.swipeContainer.isSwipeInProgress();
    }

    public void setMeasureOffsetY(int measureOffsetY2) {
        this.measureOffsetY = measureOffsetY2;
        this.swipeContainer.requestLayout();
    }

    public void disallowSwipeOffsetAnimation() {
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.ignoreMeasure) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void onPanTransitionStart(boolean keyboardVisible, int contentHeight) {
        if (keyboardVisible) {
            int oldh = this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() + contentHeight;
            setMeasuredDimension(getMeasuredWidth(), contentHeight);
            this.ignoreMeasure = true;
            ValueAnimator valueAnimator = this.webViewScrollAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.webViewScrollAnimator = null;
            }
            if (this.webViewContainer.getWebView() != null) {
                int fromY = this.webViewContainer.getWebView().getScrollY();
                final int toY = (oldh - contentHeight) + fromY;
                ValueAnimator duration = ValueAnimator.ofInt(new int[]{fromY, toY}).setDuration(250);
                this.webViewScrollAnimator = duration;
                duration.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                this.webViewScrollAnimator.addUpdateListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda4(this));
                this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView() != null) {
                            ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().setScrollY(toY);
                        }
                        if (animation == ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator) {
                            ValueAnimator unused = ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator = null;
                        }
                    }
                });
                this.webViewScrollAnimator.start();
            }
        }
    }

    /* renamed from: lambda$onPanTransitionStart$8$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3810x1247061e(ValueAnimator animation) {
        int val = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().setScrollY(val);
        }
    }

    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        requestLayout();
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.parentAlert.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId))));
        this.swipeContainer.setSwipeOffsetY(0.0f);
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().scrollTo(0, 0);
        }
        if (this.parentAlert.getBaseFragment() != null) {
            this.webViewContainer.setParentActivity(this.parentAlert.getBaseFragment().getParentActivity());
        }
        this.otherItem.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void onShown() {
        if (this.webViewContainer.isPageLoaded()) {
            requestEnableKeyboard();
        }
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda9(this));
    }

    /* renamed from: lambda$onShown$9$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3811x472CLASSNAME() {
        this.webViewContainer.restoreButtonData();
    }

    /* access modifiers changed from: private */
    public void requestEnableKeyboard() {
        BaseFragment fragment = this.parentAlert.getBaseFragment();
        if (!(fragment instanceof ChatActivity) || ((ChatActivity) fragment).contentView.measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
            this.parentAlert.getWindow().setSoftInputMode(20);
            setFocusable(true);
            this.parentAlert.setFocusable(true);
            return;
        }
        AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView());
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda10(this), 250);
    }

    /* access modifiers changed from: package-private */
    public void onHidden() {
        super.onHidden();
        this.parentAlert.setFocusable(false);
        this.parentAlert.getWindow().setSoftInputMode(48);
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        return (int) (this.swipeContainer.getSwipeOffsetY() + this.swipeContainer.getOffsetY());
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    public String getStartCommand() {
        return this.startCommand;
    }

    public void requestWebView(int currentAccount2, long peerId2, long botId2, boolean silent2, int replyToMsgId2) {
        requestWebView(currentAccount2, peerId2, botId2, silent2, replyToMsgId2, (String) null);
    }

    public void requestWebView(int currentAccount2, long peerId2, long botId2, boolean silent2, int replyToMsgId2, String startCommand2) {
        this.currentAccount = currentAccount2;
        this.peerId = peerId2;
        this.botId = botId2;
        this.silent = silent2;
        this.replyToMsgId = replyToMsgId2;
        this.startCommand = startCommand2;
        this.webViewContainer.setBotUser(MessagesController.getInstance(currentAccount2).getUser(Long.valueOf(botId2)));
        this.webViewContainer.loadFlicker(currentAccount2, botId2);
        TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
        req.peer = MessagesController.getInstance(currentAccount2).getInputPeer(peerId2);
        req.bot = MessagesController.getInstance(currentAccount2).getInputUser(botId2);
        req.silent = silent2;
        if (startCommand2 != null) {
            req.start_param = startCommand2;
            req.flags |= 8;
        }
        if (replyToMsgId2 != 0) {
            req.reply_to_msg_id = replyToMsgId2;
            req.flags |= 1;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getThemedColor("dialogBackground"));
            jsonObject.put("text_color", getThemedColor("windowBackgroundWhiteBlackText"));
            jsonObject.put("hint_color", getThemedColor("windowBackgroundWhiteHintText"));
            jsonObject.put("link_color", getThemedColor("windowBackgroundWhiteLinkText"));
            jsonObject.put("button_color", getThemedColor("featuredStickers_addButton"));
            jsonObject.put("button_text_color", getThemedColor("featuredStickers_buttonText"));
            req.theme_params = new TLRPC.TL_dataJSON();
            req.theme_params.data = jsonObject.toString();
            req.flags |= 4;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda2(this));
        NotificationCenter.getInstance(currentAccount2).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* renamed from: lambda$requestWebView$11$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3813x35472d61(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda11(this, response));
    }

    /* renamed from: lambda$requestWebView$10$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout  reason: not valid java name */
    public /* synthetic */ void m3812x2var_(TLObject response) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable);
        }
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        this.otherItem.removeAllSubItems();
        menu.removeView(this.otherItem);
        this.webViewContainer.destroyWebView();
        this.destroyed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
    }

    /* access modifiers changed from: package-private */
    public void onHide() {
        super.onHide();
        this.otherItem.setVisibility(8);
        this.isBotButtonAvailable = false;
        if (this.webViewContainer.hasUserPermissions()) {
            this.webViewContainer.destroyWebView();
            this.needReload = true;
        }
    }

    public boolean needReload() {
        if (!this.needReload) {
            return false;
        }
        this.needReload = false;
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return (int) this.swipeContainer.getOffsetY();
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    /* access modifiers changed from: package-private */
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        if (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
            padding = (availableHeight / 5) * 2;
        } else {
            padding = (int) (((float) availableHeight) / 3.5f);
        }
        this.parentAlert.setAllowNestedScroll(true);
        if (padding < 0) {
            padding = 0;
        }
        if (this.swipeContainer.getOffsetY() != ((float) padding)) {
            this.ignoreLayout = true;
            this.swipeContainer.setOffsetY((float) padding);
            this.ignoreLayout = false;
        }
    }

    /* access modifiers changed from: package-private */
    public int getButtonsHideOffset() {
        return AndroidUtilities.dp(56.0f);
    }

    /* access modifiers changed from: package-private */
    public boolean onBackPressed() {
        return this.webViewContainer.onBackPressed();
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHideBottomButtons() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    public BotWebViewContainer getWebViewContainer() {
        return this.webViewContainer;
    }

    public void setDelegate(BotWebViewContainer.Delegate delegate) {
        this.webViewContainer.setDelegate(delegate);
    }

    public boolean isBotButtonAvailable() {
        return this.isBotButtonAvailable;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            if (this.queryId == args[0].longValue()) {
                this.webViewContainer.destroyWebView();
                this.needReload = true;
                this.parentAlert.dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            this.webViewContainer.updateFlickerBackgroundColor(getThemedColor("dialogBackground"));
        }
    }

    public static class WebViewSwipeContainer extends FrameLayout {
        public static final SimpleFloatPropertyCompat<WebViewSwipeContainer> SWIPE_OFFSET_Y = new SimpleFloatPropertyCompat<>("swipeOffsetY", ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda2.INSTANCE, ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda3.INSTANCE);
        /* access modifiers changed from: private */
        public Delegate delegate;
        /* access modifiers changed from: private */
        public boolean flingInProgress;
        private GestureDetectorCompat gestureDetector;
        /* access modifiers changed from: private */
        public boolean isScrolling;
        /* access modifiers changed from: private */
        public boolean isSwipeDisallowed;
        private boolean isSwipeOffsetAnimationDisallowed;
        /* access modifiers changed from: private */
        public float offsetY = -1.0f;
        private SpringAnimation scrollAnimator;
        private Runnable scrollEndListener;
        private Runnable scrollListener;
        /* access modifiers changed from: private */
        public float swipeOffsetY;
        /* access modifiers changed from: private */
        public int swipeStickyRange;
        /* access modifiers changed from: private */
        public float topActionBarOffsetY = ((float) ActionBar.getCurrentActionBarHeight());
        /* access modifiers changed from: private */
        public WebView webView;

        public interface Delegate {
            void onDismiss();
        }

        static /* synthetic */ float access$1024(WebViewSwipeContainer x0, float x1) {
            float f = x0.swipeOffsetY - x1;
            x0.swipeOffsetY = f;
            return f;
        }

        public WebViewSwipeContainer(Context context) {
            super(context);
            final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (WebViewSwipeContainer.this.isSwipeDisallowed) {
                        return false;
                    }
                    if (velocityY >= 700.0f && (WebViewSwipeContainer.this.webView == null || WebViewSwipeContainer.this.webView.getScrollY() == 0)) {
                        boolean unused = WebViewSwipeContainer.this.flingInProgress = true;
                        if (WebViewSwipeContainer.this.swipeOffsetY < ((float) WebViewSwipeContainer.this.swipeStickyRange)) {
                            WebViewSwipeContainer.this.stickTo(0.0f);
                        } else if (WebViewSwipeContainer.this.delegate != null) {
                            WebViewSwipeContainer.this.delegate.onDismiss();
                        }
                        return true;
                    } else if (velocityY > -700.0f || WebViewSwipeContainer.this.swipeOffsetY <= (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                        return true;
                    } else {
                        boolean unused2 = WebViewSwipeContainer.this.flingInProgress = true;
                        WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                        webViewSwipeContainer.stickTo((-webViewSwipeContainer.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY);
                        return true;
                    }
                }

                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (!WebViewSwipeContainer.this.isScrolling && !WebViewSwipeContainer.this.isSwipeDisallowed) {
                        if (Math.abs(distanceY) >= ((float) touchSlop) && Math.abs(distanceY) * 1.5f >= Math.abs(distanceX) && (WebViewSwipeContainer.this.swipeOffsetY != (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY || WebViewSwipeContainer.this.webView == null || (distanceY < 0.0f && WebViewSwipeContainer.this.webView.getScrollY() == 0))) {
                            boolean unused = WebViewSwipeContainer.this.isScrolling = true;
                            MotionEvent ev = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                            for (int i = 0; i < WebViewSwipeContainer.this.getChildCount(); i++) {
                                WebViewSwipeContainer.this.getChildAt(i).dispatchTouchEvent(ev);
                            }
                            ev.recycle();
                            return true;
                        } else if (WebViewSwipeContainer.this.webView != null) {
                            if (WebViewSwipeContainer.this.webView.canScrollHorizontally(distanceX >= 0.0f ? 1 : -1)) {
                                boolean unused2 = WebViewSwipeContainer.this.isSwipeDisallowed = true;
                            }
                        }
                    }
                    if (!WebViewSwipeContainer.this.isScrolling) {
                        return true;
                    }
                    if (distanceY >= 0.0f) {
                        WebViewSwipeContainer.access$1024(WebViewSwipeContainer.this, distanceY);
                        if (WebViewSwipeContainer.this.webView != null && WebViewSwipeContainer.this.swipeOffsetY < (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                            WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(((float) WebViewSwipeContainer.this.webView.getScrollY()) - ((WebViewSwipeContainer.this.swipeOffsetY + WebViewSwipeContainer.this.offsetY) - WebViewSwipeContainer.this.topActionBarOffsetY), 0.0f, ((float) Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight())) - WebViewSwipeContainer.this.topActionBarOffsetY));
                        }
                    } else if (WebViewSwipeContainer.this.swipeOffsetY > (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                        WebViewSwipeContainer.access$1024(WebViewSwipeContainer.this, distanceY);
                    } else if (WebViewSwipeContainer.this.webView != null) {
                        float newWebScrollY = ((float) WebViewSwipeContainer.this.webView.getScrollY()) + distanceY;
                        WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(newWebScrollY, 0.0f, ((float) Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight())) - WebViewSwipeContainer.this.topActionBarOffsetY));
                        if (newWebScrollY < 0.0f) {
                            WebViewSwipeContainer.access$1024(WebViewSwipeContainer.this, newWebScrollY);
                        }
                    } else {
                        WebViewSwipeContainer.access$1024(WebViewSwipeContainer.this, distanceY);
                    }
                    WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                    float unused3 = webViewSwipeContainer.swipeOffsetY = MathUtils.clamp(webViewSwipeContainer.swipeOffsetY, (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY, (((float) WebViewSwipeContainer.this.getHeight()) - WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY);
                    WebViewSwipeContainer.this.invalidateTranslation();
                    return true;
                }
            });
            updateStickyRange();
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateStickyRange();
        }

        private void updateStickyRange() {
            this.swipeStickyRange = AndroidUtilities.dp(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 8.0f : 64.0f);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            if (disallowIntercept) {
                this.isSwipeDisallowed = true;
                this.isScrolling = false;
            }
        }

        public void setSwipeOffsetAnimationDisallowed(boolean swipeOffsetAnimationDisallowed) {
            this.isSwipeOffsetAnimationDisallowed = swipeOffsetAnimationDisallowed;
        }

        public void setScrollListener(Runnable scrollListener2) {
            this.scrollListener = scrollListener2;
        }

        public void setScrollEndListener(Runnable scrollEndListener2) {
            this.scrollEndListener = scrollEndListener2;
        }

        public void setWebView(WebView webView2) {
            this.webView = webView2;
        }

        public void setTopActionBarOffsetY(float topActionBarOffsetY2) {
            this.topActionBarOffsetY = topActionBarOffsetY2;
            invalidateTranslation();
        }

        public void setSwipeOffsetY(float swipeOffsetY2) {
            this.swipeOffsetY = swipeOffsetY2;
            invalidateTranslation();
        }

        public void setOffsetY(float offsetY2) {
            float wasOffsetY = this.offsetY;
            float f = this.swipeOffsetY;
            float f2 = this.topActionBarOffsetY;
            boolean wasOnTop = f == (-wasOffsetY) + f2;
            if (wasOffsetY == -1.0f || this.isSwipeOffsetAnimationDisallowed) {
                this.offsetY = offsetY2;
                if (wasOnTop) {
                    this.swipeOffsetY = (-offsetY2) + f2;
                }
                invalidateTranslation();
                return;
            }
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{wasOffsetY, offsetY2}).setDuration(200);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0(this, wasOnTop));
            animator.start();
        }

        /* renamed from: lambda$setOffsetY$0$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer  reason: not valid java name */
        public /* synthetic */ void m3814x248b8bed(boolean wasOnTop, ValueAnimator animation) {
            float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
            this.offsetY = floatValue;
            if (wasOnTop) {
                this.swipeOffsetY = (-floatValue) + this.topActionBarOffsetY;
            } else {
                this.swipeOffsetY = MathUtils.clamp(this.swipeOffsetY, (-floatValue) + this.topActionBarOffsetY, (((float) getHeight()) - this.offsetY) + this.topActionBarOffsetY);
            }
            invalidateTranslation();
        }

        /* access modifiers changed from: private */
        public void invalidateTranslation() {
            setTranslationY(Math.max(this.topActionBarOffsetY, this.offsetY + this.swipeOffsetY));
            Runnable runnable = this.scrollListener;
            if (runnable != null) {
                runnable.run();
            }
        }

        public float getTopActionBarOffsetY() {
            return this.topActionBarOffsetY;
        }

        public float getOffsetY() {
            return this.offsetY;
        }

        public float getSwipeOffsetY() {
            return this.swipeOffsetY;
        }

        public void setDelegate(Delegate delegate2) {
            this.delegate = delegate2;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (this.isScrolling && ev.getActionIndex() != 0) {
                return false;
            }
            MotionEvent rawEvent = MotionEvent.obtain(ev);
            int index = ev.getActionIndex();
            if (Build.VERSION.SDK_INT >= 29) {
                rawEvent.setLocation(ev.getRawX(index), ev.getRawY(index));
            } else {
                rawEvent.setLocation(ev.getX(index) + (ev.getRawX() - ev.getX()), ev.getY(index) + (ev.getRawY() - ev.getY()));
            }
            boolean detector = this.gestureDetector.onTouchEvent(rawEvent);
            rawEvent.recycle();
            if (ev.getAction() == 1 || ev.getAction() == 3) {
                this.isSwipeDisallowed = false;
                this.isScrolling = false;
                if (this.flingInProgress) {
                    this.flingInProgress = false;
                } else {
                    float f = this.swipeOffsetY;
                    int i = this.swipeStickyRange;
                    if (f <= ((float) (-i))) {
                        stickTo((-this.offsetY) + this.topActionBarOffsetY);
                    } else if (f <= ((float) (-i)) || f > ((float) i)) {
                        Delegate delegate2 = this.delegate;
                        if (delegate2 != null) {
                            delegate2.onDismiss();
                        }
                    } else {
                        stickTo(0.0f);
                    }
                }
            }
            boolean superTouch = super.dispatchTouchEvent(ev);
            if ((superTouch || detector || ev.getAction() != 0) && !superTouch && !detector) {
                return false;
            }
            return true;
        }

        public void stickTo(float offset) {
            stickTo(offset, (Runnable) null);
        }

        public void stickTo(float offset, Runnable callback) {
            if (this.swipeOffsetY == offset) {
                if (callback != null) {
                    callback.run();
                }
                Runnable runnable = this.scrollEndListener;
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
            SpringAnimation springAnimation = this.scrollAnimator;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this, SWIPE_OFFSET_Y, offset).setSpring(new SpringForce(offset).setStiffness(1400.0f).setDampingRatio(1.0f)).addEndListener(new ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda1(this, callback));
            this.scrollAnimator = springAnimation2;
            springAnimation2.start();
        }

        /* renamed from: lambda$stickTo$1$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer  reason: not valid java name */
        public /* synthetic */ void m3815xc3cbd5ff(Runnable callback, DynamicAnimation animation, boolean canceled, float value, float velocity) {
            if (animation == this.scrollAnimator) {
                this.scrollAnimator = null;
                if (callback != null) {
                    callback.run();
                }
                Runnable runnable = this.scrollEndListener;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        public boolean isSwipeInProgress() {
            return this.isScrolling;
        }
    }

    public static final class WebProgressView extends View {
        private final SimpleFloatPropertyCompat<WebProgressView> LOAD_PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("loadProgress", ChatAttachAlertBotWebViewLayout$WebProgressView$$ExternalSyntheticLambda0.INSTANCE, ChatAttachAlertBotWebViewLayout$WebProgressView$$ExternalSyntheticLambda1.INSTANCE).setMultiplier(100.0f);
        private Paint bluePaint;
        /* access modifiers changed from: private */
        public float loadProgress;
        private Theme.ResourcesProvider resourcesProvider;
        private SpringAnimation springAnimation;

        public WebProgressView(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            Paint paint = new Paint(1);
            this.bluePaint = paint;
            this.resourcesProvider = resourcesProvider2;
            paint.setColor(getThemedColor("featuredStickers_addButton"));
            this.bluePaint.setStyle(Paint.Style.STROKE);
            this.bluePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.bluePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        /* access modifiers changed from: protected */
        public int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.springAnimation = new SpringAnimation(this, this.LOAD_PROGRESS_PROPERTY).setSpring(new SpringForce().setStiffness(400.0f).setDampingRatio(1.0f));
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.springAnimation.cancel();
            this.springAnimation = null;
        }

        public void setLoadProgressAnimated(float loadProgress2) {
            SpringAnimation springAnimation2 = this.springAnimation;
            if (springAnimation2 == null) {
                setLoadProgress(loadProgress2);
                return;
            }
            springAnimation2.getSpring().setFinalPosition(100.0f * loadProgress2);
            this.springAnimation.start();
        }

        public void setLoadProgress(float loadProgress2) {
            this.loadProgress = loadProgress2;
            invalidate();
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            float y = ((float) getHeight()) - (this.bluePaint.getStrokeWidth() / 2.0f);
            canvas.drawLine(0.0f, y, ((float) getWidth()) * this.loadProgress, y, this.bluePaint);
        }
    }
}
