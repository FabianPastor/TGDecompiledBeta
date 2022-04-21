package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public class BotWebViewMenuContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewMenuContainer> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewMenuContainer$$ExternalSyntheticLambda11.INSTANCE, BotWebViewMenuContainer$$ExternalSyntheticLambda12.INSTANCE).setMultiplier(100.0f);
    private static final int POLL_PERIOD = 60000;
    private ActionBar.ActionBarMenuOnItemClick actionBarOnItemClick;
    /* access modifiers changed from: private */
    public float actionBarTransitionProgress;
    private Paint backgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public long botId;
    private ActionBarMenuItem botMenuItem;
    private String botUrl;
    private SpringAnimation botWebViewButtonAnimator;
    /* access modifiers changed from: private */
    public boolean botWebViewButtonWasVisible;
    /* access modifiers changed from: private */
    public int currentAccount;
    private Paint dimPaint = new Paint();
    private boolean dismissed;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    private boolean ignoreMeasure;
    /* access modifiers changed from: private */
    public boolean isLoaded;
    private long lastSwipeTime;
    private Paint linePaint = new Paint();
    private ChatActivityEnterView parentEnterView;
    private Runnable pollRunnable = new BotWebViewMenuContainer$$ExternalSyntheticLambda20(this);
    /* access modifiers changed from: private */
    public ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private MessageObject savedEditMessageObject;
    private Editable savedEditText;
    private MessageObject savedReplyMessageObject;
    private SpringAnimation springAnimation;
    /* access modifiers changed from: private */
    public ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    /* access modifiers changed from: private */
    public BotWebViewContainer webViewContainer;
    private BotWebViewContainer.Delegate webViewDelegate;
    /* access modifiers changed from: private */
    public ValueAnimator webViewScrollAnimator;

    static /* synthetic */ void lambda$static$1(BotWebViewMenuContainer obj, float value) {
        obj.actionBarTransitionProgress = value;
        obj.invalidate();
        obj.invalidateActionBar();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3655lambda$new$4$orgtelegramuiComponentsBotWebViewMenuContainer() {
        if (!this.dismissed) {
            TLRPC.TL_messages_prolongWebView prolongWebView = new TLRPC.TL_messages_prolongWebView();
            prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
            prolongWebView.query_id = this.queryId;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(prolongWebView, new BotWebViewMenuContainer$$ExternalSyntheticLambda8(this));
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3654lambda$new$3$orgtelegramuiComponentsBotWebViewMenuContainer(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda6(this, error));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3653lambda$new$2$orgtelegramuiComponentsBotWebViewMenuContainer(TLRPC.TL_error error) {
        if (!this.dismissed) {
            if (error != null) {
                dismiss();
            } else {
                AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
            }
        }
    }

    public BotWebViewMenuContainer(Context context, final ChatActivityEnterView parentEnterView2) {
        super(context);
        this.parentEnterView = parentEnterView2;
        ActionBar actionBar = parentEnterView2.getParentFragment().getActionBar();
        ActionBarMenuItem addItem = actionBar.createMenu().addItem(1000, NUM);
        this.botMenuItem = addItem;
        addItem.setVisibility(8);
        this.botMenuItem.addSubItem(NUM, NUM, (CharSequence) LocaleController.getString(NUM));
        this.actionBarOnItemClick = actionBar.getActionBarMenuOnItemClick();
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, parentEnterView2.getParentFragment().getResourceProvider(), getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        AnonymousClass1 r5 = new BotWebViewContainer.Delegate() {
            public /* synthetic */ void onSendWebViewData(String str) {
                BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
            }

            public /* synthetic */ void onWebAppReady() {
                BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
            }

            public void onCloseRequested(Runnable callback) {
                BotWebViewMenuContainer.this.dismiss(callback);
            }

            public void onWebAppExpand() {
                if (!BotWebViewMenuContainer.this.swipeContainer.isSwipeInProgress()) {
                    BotWebViewMenuContainer.this.swipeContainer.stickTo((-BotWebViewMenuContainer.this.swipeContainer.getOffsetY()) + BotWebViewMenuContainer.this.swipeContainer.getTopActionBarOffsetY());
                }
            }

            public void onSetupMainButton(boolean isVisible, boolean isActive, String text, int color, int textColor, boolean isProgressVisible) {
                ChatActivityBotWebViewButton botWebViewButton = parentEnterView2.getBotWebViewButton();
                botWebViewButton.setupButtonParams(isActive, text, color, textColor, isProgressVisible);
                botWebViewButton.setOnClickListener(new BotWebViewMenuContainer$1$$ExternalSyntheticLambda0(this));
                if (isVisible != BotWebViewMenuContainer.this.botWebViewButtonWasVisible) {
                    BotWebViewMenuContainer.this.animateBotButton(isVisible);
                }
            }

            /* renamed from: lambda$onSetupMainButton$0$org-telegram-ui-Components-BotWebViewMenuContainer$1  reason: not valid java name */
            public /* synthetic */ void m3665xa42evar_(View v) {
                BotWebViewMenuContainer.this.webViewContainer.onMainButtonPressed();
            }
        };
        this.webViewDelegate = r5;
        botWebViewContainer.setDelegate(r5);
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(NUM);
        AnonymousClass2 r4 = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int availableHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                if (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                    padding = (availableHeight / 5) * 2;
                } else {
                    padding = (int) (((float) availableHeight) / 3.5f);
                }
                if (padding < 0) {
                    padding = 0;
                }
                if (getOffsetY() != ((float) padding)) {
                    boolean unused = BotWebViewMenuContainer.this.ignoreLayout = true;
                    setOffsetY((float) padding);
                    boolean unused2 = BotWebViewMenuContainer.this.ignoreLayout = false;
                }
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.8f), NUM);
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((((View.MeasureSpec.getSize(heightMeasureSpec) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(24.0f)) - AndroidUtilities.dp(5.0f), NUM));
            }

            public void requestLayout() {
                if (!BotWebViewMenuContainer.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.swipeContainer = r4;
        r4.setScrollListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda0(this));
        this.swipeContainer.setScrollEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda1(this));
        this.swipeContainer.addView(this.webViewContainer);
        this.swipeContainer.setDelegate(new BotWebViewMenuContainer$$ExternalSyntheticLambda9(this));
        this.swipeContainer.setTopActionBarOffsetY((float) ((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f)));
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 24.0f, 0.0f, 0.0f));
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(context, parentEnterView2.getParentFragment().getResourceProvider());
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 5.0f));
        this.webViewContainer.setWebViewProgressListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda15(this));
        setWillNotDraw(false);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3656lambda$new$5$orgtelegramuiComponentsBotWebViewMenuContainer() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - (Math.min(this.swipeContainer.getSwipeOffsetY(), (float) this.swipeContainer.getHeight()) / ((float) this.swipeContainer.getHeight()))) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float newPos = ((float) (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0)) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != newPos) {
                this.springAnimation.getSpring().setFinalPosition(newPos);
                this.springAnimation.start();
            }
        }
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3657lambda$new$6$orgtelegramuiComponentsBotWebViewMenuContainer() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3659lambda$new$8$orgtelegramuiComponentsBotWebViewMenuContainer(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda10(this));
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    BotWebViewMenuContainer.this.progressView.setVisibility(8);
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3658lambda$new$7$orgtelegramuiComponentsBotWebViewMenuContainer(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public void invalidateActionBar() {
        int subtitleColor = ColorUtils.blendARGB(getColor("actionBarDefaultSubtitle"), getColor("windowBackgroundWhiteGrayText"), this.actionBarTransitionProgress);
        ChatActivity chatActivity = this.parentEnterView.getParentFragment();
        ActionBar actionBar = chatActivity.getActionBar();
        actionBar.setBackgroundColor(ColorUtils.blendARGB(getColor("actionBarDefault"), getColor("windowBackgroundWhite"), this.actionBarTransitionProgress));
        actionBar.setItemsColor(ColorUtils.blendARGB(getColor("actionBarDefaultIcon"), getColor("windowBackgroundWhiteBlackText"), this.actionBarTransitionProgress), false);
        actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(getColor("actionBarDefaultSelector"), getColor("actionBarWhiteSelector"), this.actionBarTransitionProgress), false);
        actionBar.setSubtitleColor(subtitleColor);
        ChatAvatarContainer chatAvatarContainer = chatActivity.getAvatarContainer();
        chatAvatarContainer.getTitleTextView().setTextColor(ColorUtils.blendARGB(getColor("actionBarDefaultTitle"), getColor("windowBackgroundWhiteBlackText"), this.actionBarTransitionProgress));
        chatAvatarContainer.getSubtitleTextView().setTextColor(subtitleColor);
        chatAvatarContainer.setOverrideSubtitleColor(this.actionBarTransitionProgress == 0.0f ? null : Integer.valueOf(subtitleColor));
        updateLightStatusBar();
    }

    public boolean onBackPressed() {
        return this.webViewContainer.onBackPressed();
    }

    /* access modifiers changed from: private */
    public void animateBotButton(boolean isVisible) {
        ChatActivityBotWebViewButton botWebViewButton = this.parentEnterView.getBotWebViewButton();
        SpringAnimation springAnimation2 = this.botWebViewButtonAnimator;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
            this.botWebViewButtonAnimator = null;
        }
        float f = 0.0f;
        botWebViewButton.setProgress(isVisible ? 0.0f : 1.0f);
        if (isVisible) {
            botWebViewButton.setVisibility(0);
        }
        SpringAnimation springAnimation3 = new SpringAnimation(botWebViewButton, ChatActivityBotWebViewButton.PROGRESS_PROPERTY);
        if (isVisible) {
            f = 1.0f;
        }
        SpringAnimation springAnimation4 = (SpringAnimation) ((SpringAnimation) springAnimation3.setSpring(new SpringForce(f * ChatActivityBotWebViewButton.PROGRESS_PROPERTY.getMultiplier()).setStiffness(isVisible ? 600.0f : 750.0f).setDampingRatio(1.0f)).addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda19(this))).addEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda18(this, isVisible, botWebViewButton));
        this.botWebViewButtonAnimator = springAnimation4;
        springAnimation4.start();
        this.botWebViewButtonWasVisible = isVisible;
    }

    /* renamed from: lambda$animateBotButton$9$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3649xbdfcavar_(DynamicAnimation animation, float value, float velocity) {
        float v = value / ChatActivityBotWebViewButton.PROGRESS_PROPERTY.getMultiplier();
        this.parentEnterView.setBotWebViewButtonOffsetX(((float) AndroidUtilities.dp(64.0f)) * v);
        this.parentEnterView.setComposeShadowAlpha(1.0f - v);
    }

    /* renamed from: lambda$animateBotButton$10$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3648xd53aad85(boolean isVisible, ChatActivityBotWebViewButton botWebViewButton, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (!isVisible) {
            botWebViewButton.setVisibility(8);
        }
        if (this.botWebViewButtonAnimator == animation) {
            this.botWebViewButtonAnimator = null;
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = (SpringAnimation) new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f)).addEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda17(this));
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    /* renamed from: lambda$onAttachedToWindow$11$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3660x9cvar_a(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        ChatActivity chatActivity = this.parentEnterView.getParentFragment();
        ChatAvatarContainer chatAvatarContainer = chatActivity.getAvatarContainer();
        chatAvatarContainer.setClickable(value == 0.0f);
        chatAvatarContainer.getAvatarImageView().setClickable(value == 0.0f);
        ActionBar actionBar = chatActivity.getActionBar();
        if (value != 100.0f || !this.parentEnterView.hasBotWebView()) {
            chatActivity.showHeaderItem(true);
            this.botMenuItem.setVisibility(8);
            actionBar.setActionBarMenuOnItemClick(this.actionBarOnItemClick);
            return;
        }
        chatActivity.showHeaderItem(false);
        this.botMenuItem.setVisibility(0);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    BotWebViewMenuContainer.this.dismiss();
                } else if (id == NUM) {
                    if (BotWebViewMenuContainer.this.webViewContainer.getWebView() != null) {
                        BotWebViewMenuContainer.this.webViewContainer.getWebView().animate().cancel();
                        BotWebViewMenuContainer.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    }
                    boolean unused = BotWebViewMenuContainer.this.isLoaded = false;
                    BotWebViewMenuContainer.this.progressView.setLoadProgress(0.0f);
                    BotWebViewMenuContainer.this.progressView.setAlpha(1.0f);
                    BotWebViewMenuContainer.this.progressView.setVisibility(0);
                    BotWebViewMenuContainer.this.webViewContainer.setBotUser(MessagesController.getInstance(BotWebViewMenuContainer.this.currentAccount).getUser(Long.valueOf(BotWebViewMenuContainer.this.botId)));
                    BotWebViewMenuContainer.this.webViewContainer.loadFlicker(BotWebViewMenuContainer.this.currentAccount, BotWebViewMenuContainer.this.botId);
                    BotWebViewMenuContainer.this.webViewContainer.reload();
                }
            }
        });
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation2 = this.springAnimation;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
            this.springAnimation = null;
        }
        this.actionBarTransitionProgress = 0.0f;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
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
            int oldh = this.parentEnterView.getSizeNotifierLayout().measureKeyboardHeight() + contentHeight;
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
                this.webViewScrollAnimator.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda14(this));
                this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (BotWebViewMenuContainer.this.webViewContainer.getWebView() != null) {
                            BotWebViewMenuContainer.this.webViewContainer.getWebView().setScrollY(toY);
                        }
                        if (animation == BotWebViewMenuContainer.this.webViewScrollAnimator) {
                            ValueAnimator unused = BotWebViewMenuContainer.this.webViewScrollAnimator = null;
                        }
                    }
                });
                this.webViewScrollAnimator.start();
            }
        }
    }

    /* renamed from: lambda$onPanTransitionStart$12$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3664x8CLASSNAMEa07a(ValueAnimator animation) {
        int val = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().setScrollY(val);
        }
    }

    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        requestLayout();
    }

    private void updateLightStatusBar() {
        int flags;
        boolean z = true;
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
            z = false;
        }
        boolean lightStatusBar = z;
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != lightStatusBar) {
            this.wasLightStatusBar = Boolean.valueOf(lightStatusBar);
            if (Build.VERSION.SDK_INT >= 23) {
                int flags2 = getSystemUiVisibility();
                if (lightStatusBar) {
                    flags = flags2 | 8192;
                } else {
                    flags = flags2 & -8193;
                }
                setSystemUiVisibility(flags);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.backgroundPaint.setColor(getColor("windowBackgroundWhite"));
        AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        canvas.drawRect(AndroidUtilities.rectTmp, this.dimPaint);
        float radius = ((float) AndroidUtilities.dp(16.0f)) * (1.0f - this.actionBarTransitionProgress);
        AndroidUtilities.rectTmp.set(0.0f, AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), 0.0f, this.actionBarTransitionProgress), (float) getWidth(), ((float) getHeight()) + radius);
        canvas.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, this.backgroundPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != 0 || event.getY() > AndroidUtilities.lerp(this.swipeContainer.getTranslationY() + ((float) AndroidUtilities.dp(24.0f)), 0.0f, this.actionBarTransitionProgress)) {
            return super.onTouchEvent(event);
        }
        dismiss();
        return true;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.linePaint.setColor(getColor("dialogGrayLine"));
        Paint paint = this.linePaint;
        paint.setAlpha((int) (((float) paint.getAlpha()) * (1.0f - (Math.min(0.5f, this.actionBarTransitionProgress) / 0.5f))));
        canvas.save();
        float scale = 1.0f - this.actionBarTransitionProgress;
        float y = AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), ((float) AndroidUtilities.statusBarHeight) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f), this.actionBarTransitionProgress) + ((float) AndroidUtilities.dp(12.0f));
        canvas.scale(scale, scale, ((float) getWidth()) / 2.0f, y);
        canvas.drawLine((((float) getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(16.0f)), y, (((float) getWidth()) / 2.0f) + ((float) AndroidUtilities.dp(16.0f)), y, this.linePaint);
        canvas.restore();
    }

    public void show(int currentAccount2, long botId2, String botUrl2) {
        this.dismissed = false;
        if (!(this.currentAccount == currentAccount2 && this.botId == botId2 && ColorUtils$$ExternalSyntheticBackport0.m(this.botUrl, botUrl2))) {
            this.isLoaded = false;
        }
        this.currentAccount = currentAccount2;
        this.botId = botId2;
        this.botUrl = botUrl2;
        this.savedEditText = this.parentEnterView.getEditField().getText();
        this.parentEnterView.getEditField().setText((CharSequence) null);
        this.savedReplyMessageObject = this.parentEnterView.getReplyingMessageObject();
        this.savedEditMessageObject = this.parentEnterView.getEditingMessageObject();
        ChatActivity chatActivity = this.parentEnterView.getParentFragment();
        if (chatActivity != null) {
            chatActivity.hideFieldPanel(true);
        }
        if (!this.isLoaded) {
            loadWebView();
        }
        setVisibility(0);
        setAlpha(0.0f);
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                BotWebViewMenuContainer.this.swipeContainer.setSwipeOffsetY((float) BotWebViewMenuContainer.this.swipeContainer.getHeight());
                BotWebViewMenuContainer.this.setAlpha(1.0f);
                ((SpringAnimation) new SpringAnimation(BotWebViewMenuContainer.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).addEndListener(new BotWebViewMenuContainer$6$$ExternalSyntheticLambda0(this))).start();
            }

            /* renamed from: lambda$onLayoutChange$0$org-telegram-ui-Components-BotWebViewMenuContainer$6  reason: not valid java name */
            public /* synthetic */ void m3666xd1dae4c3(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                BotWebViewMenuContainer.this.webViewContainer.restoreButtonData();
                BotWebViewMenuContainer.this.webViewContainer.invalidateViewPortHeight(true);
            }
        });
    }

    private void loadWebView() {
        this.progressView.setLoadProgress(0.0f);
        this.progressView.setAlpha(1.0f);
        this.progressView.setVisibility(0);
        this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
        this.webViewContainer.loadFlicker(this.currentAccount, this.botId);
        TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
        req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
        req.url = this.botUrl;
        req.flags |= 2;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getColor("windowBackgroundWhite"));
            jsonObject.put("text_color", getColor("windowBackgroundWhiteBlackText"));
            jsonObject.put("hint_color", getColor("windowBackgroundWhiteHintText"));
            jsonObject.put("link_color", getColor("windowBackgroundWhiteLinkText"));
            jsonObject.put("button_color", getColor("featuredStickers_addButton"));
            jsonObject.put("button_text_color", getColor("featuredStickers_buttonText"));
            req.theme_params = new TLRPC.TL_dataJSON();
            req.theme_params.data = jsonObject.toString();
            req.flags |= 4;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        req.from_bot_menu = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new BotWebViewMenuContainer$$ExternalSyntheticLambda7(this));
    }

    /* renamed from: lambda$loadWebView$14$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3652x14568e02(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda5(this, response));
    }

    /* renamed from: lambda$loadWebView$13$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3651xe67df3a3(TLObject response) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            this.isLoaded = true;
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    private int getColor(String key) {
        Integer color;
        Theme.ResourcesProvider resourcesProvider = this.parentEnterView.getParentFragment().getResourceProvider();
        if (resourcesProvider != null) {
            color = resourcesProvider.getColor(key);
        } else {
            color = Integer.valueOf(Theme.getColor(key));
        }
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void dismiss() {
        dismiss((Runnable) null);
    }

    public void dismiss(Runnable callback) {
        if (!this.dismissed) {
            this.dismissed = true;
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((float) (webViewSwipeContainer.getHeight() + this.parentEnterView.getSizeNotifierLayout().measureKeyboardHeight()), new BotWebViewMenuContainer$$ExternalSyntheticLambda4(this, callback));
        }
    }

    /* renamed from: lambda$dismiss$15$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3650x32500d8(Runnable callback) {
        onDismiss();
        if (callback != null) {
            callback.run();
        }
    }

    public void onDismiss() {
        setVisibility(8);
        this.webViewContainer.destroyWebView();
        this.swipeContainer.removeView(this.webViewContainer);
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(getContext(), this.parentEnterView.getParentFragment().getResourceProvider(), getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(this.webViewDelegate);
        this.webViewContainer.setWebViewProgressListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda16(this));
        this.swipeContainer.addView(this.webViewContainer);
        this.isLoaded = false;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        boolean delayRestoreText = this.botWebViewButtonWasVisible;
        if (this.botWebViewButtonWasVisible) {
            this.botWebViewButtonWasVisible = false;
            animateBotButton(false);
        }
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda2(this), delayRestoreText ? 200 : 0);
    }

    /* renamed from: lambda$onDismiss$17$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3662x811b0737(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda13(this));
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    BotWebViewMenuContainer.this.progressView.setVisibility(8);
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$onDismiss$16$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3661x53426cd8(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* renamed from: lambda$onDismiss$18$org-telegram-ui-Components-BotWebViewMenuContainer  reason: not valid java name */
    public /* synthetic */ void m3663xaef3a196() {
        if (this.savedEditText != null) {
            this.parentEnterView.getEditField().setText(this.savedEditText);
            this.savedEditText = null;
        }
        if (this.savedReplyMessageObject != null) {
            ChatActivity chatActivity = this.parentEnterView.getParentFragment();
            if (chatActivity != null) {
                chatActivity.showFieldPanelForReply(this.savedReplyMessageObject);
            }
            this.savedReplyMessageObject = null;
        }
        if (this.savedEditMessageObject != null) {
            ChatActivity chatActivity2 = this.parentEnterView.getParentFragment();
            if (chatActivity2 != null) {
                chatActivity2.showFieldPanelForEdit(true, this.savedEditMessageObject);
            }
            this.savedEditMessageObject = null;
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            if (this.queryId == args[0].longValue()) {
                dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            this.webViewContainer.updateFlickerBackgroundColor(getColor("windowBackgroundWhite"));
            invalidate();
            invalidateActionBar();
            AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda3(this), 300);
        }
    }
}
