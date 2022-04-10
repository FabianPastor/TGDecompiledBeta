package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
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
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlert;

public class ChatAttachAlertBotWebViewLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
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
    /* access modifiers changed from: private */
    public long peerId;
    private Runnable pollRunnable = new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda5(this);
    /* access modifiers changed from: private */
    public WebProgressView progressView;
    private long queryId;
    /* access modifiers changed from: private */
    public int replyToMsgId;
    /* access modifiers changed from: private */
    public boolean silent;
    /* access modifiers changed from: private */
    public String startCommand;
    private WebViewSwipeContainer swipeContainer;
    /* access modifiers changed from: private */
    public BotWebViewContainer webViewContainer;
    /* access modifiers changed from: private */
    public ValueAnimator webViewScrollAnimator;

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHideBottomButtons() {
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        if (!this.destroyed) {
            TLRPC$TL_messages_prolongWebView tLRPC$TL_messages_prolongWebView = new TLRPC$TL_messages_prolongWebView();
            tLRPC$TL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            tLRPC$TL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
            tLRPC$TL_messages_prolongWebView.query_id = this.queryId;
            tLRPC$TL_messages_prolongWebView.silent = this.silent;
            int i = this.replyToMsgId;
            if (i != 0) {
                tLRPC$TL_messages_prolongWebView.reply_to_msg_id = i;
                tLRPC$TL_messages_prolongWebView.flags |= 1;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda9(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda8(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TLRPC$TL_error tLRPC$TL_error) {
        if (!this.destroyed) {
            if (tLRPC$TL_error != null) {
                this.parentAlert.dismiss();
            } else {
                AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
            }
        }
    }

    public ChatAttachAlertBotWebViewLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        ActionBarMenuItem addItem = this.parentAlert.actionBar.createMenu().addItem(0, NUM);
        this.otherItem = addItem;
        addItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        this.otherItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        this.otherItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        this.parentAlert.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.dismiss();
                } else if (i == NUM) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", ChatAttachAlertBotWebViewLayout.this.botId);
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.baseFragment.presentFragment(new ChatActivity(bundle));
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.dismiss();
                } else if (i == NUM) {
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().animate().cancel();
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = ChatAttachAlertBotWebViewLayout.this;
                    chatAttachAlertBotWebViewLayout.requestWebView(chatAttachAlertBotWebViewLayout.currentAccount, ChatAttachAlertBotWebViewLayout.this.peerId, ChatAttachAlertBotWebViewLayout.this.botId, ChatAttachAlertBotWebViewLayout.this.silent, ChatAttachAlertBotWebViewLayout.this.replyToMsgId, ChatAttachAlertBotWebViewLayout.this.startCommand);
                } else if (i == NUM) {
                    Iterator<TLRPC$TL_attachMenuBot> it = MediaDataController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getAttachMenuBots().bots.iterator();
                    while (it.hasNext()) {
                        TLRPC$TL_attachMenuBot next = it.next();
                        if (next.bot_id == ChatAttachAlertBotWebViewLayout.this.botId) {
                            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout2 = ChatAttachAlertBotWebViewLayout.this;
                            chatAttachAlertBotWebViewLayout2.parentAlert.onLongClickBotButton(next, MessagesController.getInstance(chatAttachAlertBotWebViewLayout2.currentAccount).getUser(Long.valueOf(ChatAttachAlertBotWebViewLayout.this.botId)));
                            return;
                        }
                    }
                }
            }
        });
        AnonymousClass2 r8 = new BotWebViewContainer(context, resourcesProvider, getThemedColor("dialogBackground")) {
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && !ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable) {
                    boolean unused = ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable = true;
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.restoreButtonData();
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.webViewContainer = r8;
        r8.getWebView().setVerticalScrollBarEnabled(false);
        AnonymousClass3 r82 = new WebViewSwipeContainer(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(((View.MeasureSpec.getSize(i2) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(84.0f)) + ChatAttachAlertBotWebViewLayout.this.measureOffsetY, NUM));
            }
        };
        this.swipeContainer = r82;
        r82.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        this.swipeContainer.setScrollListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda6(this));
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda11(this));
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f));
        WebProgressView webProgressView = new WebProgressView(this, context);
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 84.0f));
        this.webViewContainer.setWebViewProgressListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3() {
        this.parentAlert.updateLayout(this, true, 0);
        this.webViewContainer.invalidateTranslation();
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda0(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlertBotWebViewLayout.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public boolean canExpandByRequest() {
        return System.currentTimeMillis() - this.lastSwipeTime > 1000 && !this.swipeContainer.isSwipeInProgress();
    }

    public void setMeasureOffsetY(int i) {
        this.measureOffsetY = i;
        this.swipeContainer.requestLayout();
    }

    public void disallowSwipeOffsetAnimation() {
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.ignoreMeasure) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(i, i2);
        }
    }

    public void onPanTransitionStart(boolean z, int i) {
        if (z) {
            int measureKeyboardHeight = this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() + i;
            setMeasuredDimension(getMeasuredWidth(), i);
            this.ignoreMeasure = true;
            ValueAnimator valueAnimator = this.webViewScrollAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.webViewScrollAnimator = null;
            }
            int scrollY = this.webViewContainer.getWebView().getScrollY();
            final int i2 = (measureKeyboardHeight - i) + scrollY;
            ValueAnimator duration = ValueAnimator.ofInt(new int[]{scrollY, i2}).setDuration(250);
            this.webViewScrollAnimator = duration;
            duration.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
            this.webViewScrollAnimator.addUpdateListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda1(this));
            this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().setScrollY(i2);
                    if (animator == ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator) {
                        ValueAnimator unused = ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator = null;
                    }
                }
            });
            this.webViewScrollAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPanTransitionStart$7(ValueAnimator valueAnimator) {
        this.webViewContainer.getWebView().setScrollY(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        requestLayout();
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.parentAlert.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId))));
        this.swipeContainer.setSwipeOffsetY(0.0f);
        this.webViewContainer.getWebView().scrollTo(0, 0);
        if (this.parentAlert.getBaseFragment() != null) {
            this.webViewContainer.setParentActivity(this.parentAlert.getBaseFragment().getParentActivity());
        }
        this.otherItem.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void onShown() {
        requestEnableKeyboard();
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShown$8() {
        this.webViewContainer.restoreButtonData();
    }

    /* access modifiers changed from: private */
    public void requestEnableKeyboard() {
        BaseFragment baseFragment = this.parentAlert.getBaseFragment();
        if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).contentView.measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
            setFocusable(true);
            this.parentAlert.setFocusable(true);
            this.parentAlert.getWindow().setSoftInputMode(21);
            return;
        }
        AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView());
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda3(this), 150);
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

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    public String getStartCommand() {
        return this.startCommand;
    }

    public void requestWebView(int i, long j, long j2, boolean z, int i2, String str) {
        this.currentAccount = i;
        this.peerId = j;
        this.botId = j2;
        this.silent = z;
        this.replyToMsgId = i2;
        this.startCommand = str;
        this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
        this.webViewContainer.loadFlicker(i, j2);
        TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
        tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
        tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
        tLRPC$TL_messages_requestWebView.silent = z;
        if (str != null) {
            tLRPC$TL_messages_requestWebView.start_param = str;
            tLRPC$TL_messages_requestWebView.flags |= 8;
        }
        if (i2 != 0) {
            tLRPC$TL_messages_requestWebView.reply_to_msg_id = i2;
            tLRPC$TL_messages_requestWebView.flags |= 1;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", getThemedColor("dialogBackground"));
            jSONObject.put("text_color", getThemedColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", getThemedColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", getThemedColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", getThemedColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", getThemedColor("featuredStickers_buttonText"));
            TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
            tLRPC$TL_messages_requestWebView.theme_params = tLRPC$TL_dataJSON;
            tLRPC$TL_dataJSON.data = jSONObject.toString();
            tLRPC$TL_messages_requestWebView.flags |= 4;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda10(this));
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda7(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$9(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(tLRPC$TL_webViewResultUrl.url);
            AndroidUtilities.runOnUIThread(this.pollRunnable);
        }
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        this.otherItem.removeAllSubItems();
        createMenu.removeView(this.otherItem);
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
    /* JADX WARNING: Removed duplicated region for block: B:11:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0021  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r3, int r4) {
        /*
            r2 = this;
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 != 0) goto L_0x0014
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r3.x
            int r3 = r3.y
            if (r0 <= r3) goto L_0x0014
            float r3 = (float) r4
            r4 = 1080033280(0x40600000, float:3.5)
            float r3 = r3 / r4
            int r3 = (int) r3
            goto L_0x0018
        L_0x0014:
            int r4 = r4 / 5
            int r3 = r4 * 2
        L_0x0018:
            org.telegram.ui.Components.ChatAttachAlert r4 = r2.parentAlert
            r0 = 1
            r4.setAllowNestedScroll(r0)
            r4 = 0
            if (r3 >= 0) goto L_0x0022
            r3 = 0
        L_0x0022:
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r1 = r2.swipeContainer
            float r1 = r1.getOffsetY()
            float r3 = (float) r3
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0036
            r2.ignoreLayout = r0
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r0 = r2.swipeContainer
            r0.setOffsetY(r3)
            r2.ignoreLayout = r4
        L_0x0036:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.onPreMeasure(int, int):void");
    }

    /* access modifiers changed from: package-private */
    public int getButtonsHideOffset() {
        return AndroidUtilities.dp(56.0f);
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

    public BotWebViewContainer getWebViewContainer() {
        return this.webViewContainer;
    }

    public void setDelegate(BotWebViewContainer.Delegate delegate) {
        this.webViewContainer.setDelegate(delegate);
    }

    public boolean isBotButtonAvailable() {
        return this.isBotButtonAvailable;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == objArr[0].longValue()) {
                this.webViewContainer.destroyWebView();
                this.needReload = true;
                this.parentAlert.dismiss();
            }
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
        private Runnable scrollListener;
        /* access modifiers changed from: private */
        public float swipeOffsetY;
        /* access modifiers changed from: private */
        public float topActionBarOffsetY = ((float) ActionBar.getCurrentActionBarHeight());
        /* access modifiers changed from: private */
        public WebView webView;

        public interface Delegate {
            void onDismiss();
        }

        static /* synthetic */ float access$1324(WebViewSwipeContainer webViewSwipeContainer, float f) {
            float f2 = webViewSwipeContainer.swipeOffsetY - f;
            webViewSwipeContainer.swipeOffsetY = f2;
            return f2;
        }

        public WebViewSwipeContainer(Context context) {
            super(context);
            final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (WebViewSwipeContainer.this.isSwipeDisallowed) {
                        return false;
                    }
                    if (f2 < 700.0f || WebViewSwipeContainer.this.webView.getScrollY() != 0) {
                        if (f2 <= -700.0f && WebViewSwipeContainer.this.swipeOffsetY > (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                            boolean unused = WebViewSwipeContainer.this.flingInProgress = true;
                            WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                            webViewSwipeContainer.stickTo((-webViewSwipeContainer.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY);
                        }
                        return true;
                    }
                    boolean unused2 = WebViewSwipeContainer.this.flingInProgress = true;
                    if (WebViewSwipeContainer.this.swipeOffsetY < ((float) AndroidUtilities.dp(64.0f))) {
                        WebViewSwipeContainer.this.stickTo(0.0f);
                    } else if (WebViewSwipeContainer.this.delegate != null) {
                        WebViewSwipeContainer.this.delegate.onDismiss();
                    }
                    return true;
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (!WebViewSwipeContainer.this.isScrolling && !WebViewSwipeContainer.this.isSwipeDisallowed) {
                        if (Math.abs(f2) < ((float) scaledTouchSlop) || Math.abs(f2) * 1.5f < Math.abs(f) || (WebViewSwipeContainer.this.swipeOffsetY == (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY && (f2 >= 0.0f || WebViewSwipeContainer.this.webView.getScrollY() != 0))) {
                            if (WebViewSwipeContainer.this.webView.canScrollHorizontally(f >= 0.0f ? 1 : -1)) {
                                boolean unused = WebViewSwipeContainer.this.isSwipeDisallowed = true;
                            }
                        } else {
                            boolean unused2 = WebViewSwipeContainer.this.isScrolling = true;
                            MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                            for (int i = 0; i < WebViewSwipeContainer.this.getChildCount(); i++) {
                                WebViewSwipeContainer.this.getChildAt(i).dispatchTouchEvent(obtain);
                            }
                            obtain.recycle();
                            return true;
                        }
                    }
                    if (WebViewSwipeContainer.this.isScrolling) {
                        if (f2 >= 0.0f) {
                            WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                            float unused3 = webViewSwipeContainer.swipeOffsetY = webViewSwipeContainer.swipeOffsetY - f2;
                            if (WebViewSwipeContainer.this.swipeOffsetY < (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                                WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(((float) WebViewSwipeContainer.this.webView.getScrollY()) - ((WebViewSwipeContainer.this.swipeOffsetY + WebViewSwipeContainer.this.offsetY) - WebViewSwipeContainer.this.topActionBarOffsetY), 0.0f, ((float) Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight())) - WebViewSwipeContainer.this.topActionBarOffsetY));
                            }
                        } else if (WebViewSwipeContainer.this.swipeOffsetY > (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                            WebViewSwipeContainer.access$1324(WebViewSwipeContainer.this, f2);
                        } else {
                            float scrollY = ((float) WebViewSwipeContainer.this.webView.getScrollY()) + f2;
                            WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(scrollY, 0.0f, ((float) Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight())) - WebViewSwipeContainer.this.topActionBarOffsetY));
                            if (scrollY < 0.0f) {
                                WebViewSwipeContainer.access$1324(WebViewSwipeContainer.this, scrollY);
                            }
                        }
                        WebViewSwipeContainer webViewSwipeContainer2 = WebViewSwipeContainer.this;
                        float unused4 = webViewSwipeContainer2.swipeOffsetY = MathUtils.clamp(webViewSwipeContainer2.swipeOffsetY, (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY, ((float) WebViewSwipeContainer.this.getHeight()) - WebViewSwipeContainer.this.offsetY);
                        WebViewSwipeContainer.this.invalidateTranslation();
                    }
                    return true;
                }
            });
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            if (z) {
                this.isSwipeDisallowed = true;
                this.isScrolling = false;
            }
        }

        public void setSwipeOffsetAnimationDisallowed(boolean z) {
            this.isSwipeOffsetAnimationDisallowed = z;
        }

        public void setScrollListener(Runnable runnable) {
            this.scrollListener = runnable;
        }

        public void setWebView(WebView webView2) {
            this.webView = webView2;
        }

        public void setTopActionBarOffsetY(float f) {
            this.topActionBarOffsetY = f;
            invalidateTranslation();
        }

        public void setSwipeOffsetY(float f) {
            this.swipeOffsetY = f;
            invalidateTranslation();
        }

        public void setOffsetY(float f) {
            float f2 = this.offsetY;
            float f3 = this.swipeOffsetY;
            float f4 = this.topActionBarOffsetY;
            boolean z = f3 == (-f2) + f4;
            if (f2 == -1.0f || this.isSwipeOffsetAnimationDisallowed) {
                this.offsetY = f;
                if (z) {
                    this.swipeOffsetY = (-f) + f4;
                }
                invalidateTranslation();
                return;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f2, f}).setDuration(200);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0(this, z));
            duration.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setOffsetY$0(boolean z, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.offsetY = floatValue;
            if (z) {
                this.swipeOffsetY = (-floatValue) + this.topActionBarOffsetY;
            } else {
                this.swipeOffsetY = MathUtils.clamp(this.swipeOffsetY, (-floatValue) + this.topActionBarOffsetY, ((float) getHeight()) - this.offsetY);
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

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (this.isScrolling && motionEvent.getActionIndex() != 0) {
                return false;
            }
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            int actionIndex = motionEvent.getActionIndex();
            if (Build.VERSION.SDK_INT >= 29) {
                obtain.setLocation(motionEvent.getRawX(actionIndex), motionEvent.getRawY(actionIndex));
            } else {
                obtain.setLocation(motionEvent.getX(actionIndex) + (motionEvent.getRawX() - motionEvent.getX()), motionEvent.getY(actionIndex) + (motionEvent.getRawY() - motionEvent.getY()));
            }
            boolean onTouchEvent = this.gestureDetector.onTouchEvent(obtain);
            obtain.recycle();
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.isSwipeDisallowed = false;
                this.isScrolling = false;
                if (this.flingInProgress) {
                    this.flingInProgress = false;
                } else if (this.swipeOffsetY <= ((float) (-AndroidUtilities.dp(64.0f)))) {
                    stickTo((-this.offsetY) + this.topActionBarOffsetY);
                } else if (this.swipeOffsetY <= ((float) (-AndroidUtilities.dp(64.0f))) || this.swipeOffsetY > ((float) AndroidUtilities.dp(64.0f))) {
                    Delegate delegate2 = this.delegate;
                    if (delegate2 != null) {
                        delegate2.onDismiss();
                    }
                } else {
                    stickTo(0.0f);
                }
            }
            boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
            if ((dispatchTouchEvent || onTouchEvent || motionEvent.getAction() != 0) && !dispatchTouchEvent && !onTouchEvent) {
                return false;
            }
            return true;
        }

        public void stickTo(float f) {
            stickTo(f, (Runnable) null);
        }

        public void stickTo(float f, Runnable runnable) {
            if (this.swipeOffsetY != f) {
                SpringAnimation springAnimation = this.scrollAnimator;
                if (springAnimation != null) {
                    springAnimation.cancel();
                }
                SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this, SWIPE_OFFSET_Y, f).setSpring(new SpringForce(f).setStiffness(1400.0f).setDampingRatio(1.0f)).addEndListener(new ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda1(this, runnable));
                this.scrollAnimator = springAnimation2;
                springAnimation2.start();
            } else if (runnable != null) {
                runnable.run();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$stickTo$1(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (dynamicAnimation == this.scrollAnimator) {
                this.scrollAnimator = null;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        public boolean isSwipeInProgress() {
            return this.isScrolling;
        }
    }

    private class WebProgressView extends View {
        private final SimpleFloatPropertyCompat<WebProgressView> LOAD_PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("loadProgress", ChatAttachAlertBotWebViewLayout$WebProgressView$$ExternalSyntheticLambda0.INSTANCE, ChatAttachAlertBotWebViewLayout$WebProgressView$$ExternalSyntheticLambda1.INSTANCE).setMultiplier(100.0f);
        private Paint bluePaint;
        /* access modifiers changed from: private */
        public float loadProgress;
        private SpringAnimation springAnimation;

        public WebProgressView(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.bluePaint = paint;
            paint.setColor(chatAttachAlertBotWebViewLayout.getThemedColor("featuredStickers_addButton"));
            this.bluePaint.setStyle(Paint.Style.STROKE);
            this.bluePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.bluePaint.setStrokeCap(Paint.Cap.ROUND);
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

        public void setLoadProgressAnimated(float f) {
            SpringAnimation springAnimation2 = this.springAnimation;
            if (springAnimation2 == null) {
                setLoadProgress(f);
                return;
            }
            springAnimation2.getSpring().setFinalPosition(f * 100.0f);
            this.springAnimation.start();
        }

        /* access modifiers changed from: private */
        public void setLoadProgress(float f) {
            this.loadProgress = f;
            invalidate();
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            float height = ((float) getHeight()) - (this.bluePaint.getStrokeWidth() / 2.0f);
            canvas.drawLine(0.0f, height, ((float) getWidth()) * this.loadProgress, height, this.bluePaint);
        }
    }
}
