package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
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
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlert;

public class ChatAttachAlertBotWebViewLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public long botId;
    /* access modifiers changed from: private */
    public int currentAccount;
    private boolean ignoreLayout;
    private long lastSwipeTime;
    /* access modifiers changed from: private */
    public int measureOffsetY;
    private ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    /* access modifiers changed from: private */
    public long peerId;
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

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHideBottomButtons() {
        return false;
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
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getThemedColor("dialogBackground"));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.getWebView().setVerticalScrollBarEnabled(false);
        AnonymousClass2 r8 = new WebViewSwipeContainer(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(((View.MeasureSpec.getSize(i2) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(84.0f)) + ChatAttachAlertBotWebViewLayout.this.measureOffsetY, NUM));
            }
        };
        this.swipeContainer = r8;
        r8.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        this.swipeContainer.setScrollListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda2(this));
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda6(this));
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f));
        WebProgressView webProgressView = new WebProgressView(this, context);
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 84.0f));
        this.webViewContainer.setWebViewProgressListener(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.parentAlert.updateLayout(this, true, 0);
        this.webViewContainer.invalidateTranslation();
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(Float f) {
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
    public /* synthetic */ void lambda$new$2(ValueAnimator valueAnimator) {
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

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.parentAlert.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId))));
        this.swipeContainer.setSwipeOffsetY(0.0f);
        this.webViewContainer.getWebView().scrollTo(0, 0);
        this.otherItem.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void onShown() {
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShown$4() {
        this.webViewContainer.restoreButtonData();
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
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda5(this, i, j2));
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$6(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda4(this, tLObject, i, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$5(TLObject tLObject, int i, long j) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j)));
            this.webViewContainer.loadFlicker(i, j);
            this.webViewContainer.loadUrl(tLRPC$TL_webViewResultUrl.url);
        }
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        this.otherItem.removeAllSubItems();
        createMenu.removeView(this.otherItem);
    }

    /* access modifiers changed from: package-private */
    public void onHide() {
        super.onHide();
        this.otherItem.setVisibility(8);
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
    public void onPreMeasure(int i, int i2) {
        int i3;
        if (this.parentAlert.actionBar.isSearchFieldVisible() || this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            i3 = this.webViewContainer.getWebView().getContentHeight() - this.overScrollHeight;
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    i3 = (int) (((float) i2) / 3.5f);
                    this.parentAlert.setAllowNestedScroll(true);
                }
            }
            i3 = (i2 / 5) * 2;
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (i3 < 0) {
            i3 = 0;
        }
        float f = (float) i3;
        if (this.swipeContainer.getOffsetY() != f) {
            this.ignoreLayout = true;
            this.swipeContainer.setOffsetY(f);
            this.ignoreLayout = false;
        }
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == objArr[0].longValue()) {
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
        /* access modifiers changed from: private */
        public SpringAnimation webViewFlingAnimator;

        public interface Delegate {
            void onDismiss();
        }

        static /* synthetic */ float access$1024(WebViewSwipeContainer webViewSwipeContainer, float f) {
            float f2 = webViewSwipeContainer.swipeOffsetY - f;
            webViewSwipeContainer.swipeOffsetY = f2;
            return f2;
        }

        public WebViewSwipeContainer(Context context) {
            super(context);
            final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (f2 >= 700.0f && WebViewSwipeContainer.this.webView.getScrollY() == 0) {
                        boolean unused = WebViewSwipeContainer.this.flingInProgress = true;
                        if (WebViewSwipeContainer.this.swipeOffsetY < ((float) AndroidUtilities.dp(64.0f))) {
                            WebViewSwipeContainer.this.stickTo(0.0f);
                        } else if (WebViewSwipeContainer.this.delegate != null) {
                            WebViewSwipeContainer.this.delegate.onDismiss();
                        }
                        return true;
                    } else if (f2 > -700.0f || WebViewSwipeContainer.this.swipeOffsetY <= (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                        if (WebViewSwipeContainer.this.swipeOffsetY == (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                            boolean unused2 = WebViewSwipeContainer.this.flingInProgress = true;
                            WebViewSwipeContainer.this.stopWebViewScroll();
                            float clamp = MathUtils.clamp(((float) WebViewSwipeContainer.this.webView.getScrollY()) - (f2 / 2.5f), 0.0f, ((float) Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight())) - WebViewSwipeContainer.this.topActionBarOffsetY);
                            SpringAnimation unused3 = WebViewSwipeContainer.this.webViewFlingAnimator = (SpringAnimation) new SpringAnimation(WebViewSwipeContainer.this.webView, DynamicAnimation.SCROLL_Y, clamp).setSpring(new SpringForce(clamp).setStiffness(500.0f).setDampingRatio(1.0f)).addEndListener(new ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$1$$ExternalSyntheticLambda0(this));
                            WebViewSwipeContainer.this.webViewFlingAnimator.start();
                        }
                        return true;
                    } else {
                        boolean unused4 = WebViewSwipeContainer.this.flingInProgress = true;
                        WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                        webViewSwipeContainer.stickTo((-webViewSwipeContainer.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY);
                        return true;
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onFling$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    if (dynamicAnimation == WebViewSwipeContainer.this.webViewFlingAnimator) {
                        SpringAnimation unused = WebViewSwipeContainer.this.webViewFlingAnimator = null;
                    }
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (!WebViewSwipeContainer.this.isScrolling && !WebViewSwipeContainer.this.isSwipeDisallowed) {
                        if (Math.abs(f2) < ((float) scaledTouchSlop) || Math.abs(f2) * 1.5f < Math.abs(f)) {
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
                            WebViewSwipeContainer.access$1024(WebViewSwipeContainer.this, f2);
                        } else {
                            float scrollY = ((float) WebViewSwipeContainer.this.webView.getScrollY()) + f2;
                            WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(scrollY, 0.0f, ((float) Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight())) - WebViewSwipeContainer.this.topActionBarOffsetY));
                            if (scrollY < 0.0f) {
                                WebViewSwipeContainer.access$1024(WebViewSwipeContainer.this, scrollY);
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
            if (f2 == -1.0f || this.isSwipeOffsetAnimationDisallowed) {
                this.offsetY = f;
                invalidateTranslation();
                return;
            }
            boolean z = this.swipeOffsetY == (-f2) + this.topActionBarOffsetY;
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

        /* access modifiers changed from: private */
        public void stopWebViewScroll() {
            SpringAnimation springAnimation = this.webViewFlingAnimator;
            if (springAnimation != null) {
                springAnimation.cancel();
                this.webViewFlingAnimator = null;
            }
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (this.isScrolling && motionEvent.getActionIndex() != 0) {
                return false;
            }
            MotionEvent obtain = MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getEventTime(), motionEvent.getAction(), motionEvent.getRawX(), motionEvent.getRawY(), motionEvent.getMetaState());
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
            if (motionEvent.getAction() == 0) {
                stopWebViewScroll();
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
                SpringAnimation springAnimation2 = (SpringAnimation) new SpringAnimation(this, SWIPE_OFFSET_Y, f).setSpring(new SpringForce(f).setStiffness(1250.0f).setDampingRatio(1.0f)).addEndListener(new ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda1(this, runnable));
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
