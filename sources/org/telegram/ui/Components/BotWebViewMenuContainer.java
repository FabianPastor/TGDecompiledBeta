package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
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
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public class BotWebViewMenuContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewMenuContainer> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewMenuContainer$$ExternalSyntheticLambda20.INSTANCE, BotWebViewMenuContainer$$ExternalSyntheticLambda21.INSTANCE).setMultiplier(100.0f);
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
    private Paint linePaint = new Paint();
    private ChatActivityEnterView parentEnterView;
    private Runnable pollRunnable = new BotWebViewMenuContainer$$ExternalSyntheticLambda9(this);
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(BotWebViewMenuContainer botWebViewMenuContainer, float f) {
        botWebViewMenuContainer.actionBarTransitionProgress = f;
        botWebViewMenuContainer.invalidate();
        botWebViewMenuContainer.invalidateActionBar();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        if (!this.dismissed) {
            TLRPC$TL_messages_prolongWebView tLRPC$TL_messages_prolongWebView = new TLRPC$TL_messages_prolongWebView();
            tLRPC$TL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            tLRPC$TL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
            tLRPC$TL_messages_prolongWebView.query_id = this.queryId;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new BotWebViewMenuContainer$$ExternalSyntheticLambda17(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda15(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(TLRPC$TL_error tLRPC$TL_error) {
        if (!this.dismissed) {
            if (tLRPC$TL_error != null) {
                dismiss();
            } else {
                AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
            }
        }
    }

    public BotWebViewMenuContainer(Context context, final ChatActivityEnterView chatActivityEnterView) {
        super(context);
        this.parentEnterView = chatActivityEnterView;
        ActionBar actionBar = chatActivityEnterView.getParentFragment().getActionBar();
        ActionBarMenuItem addItem = actionBar.createMenu().addItem(1000, NUM);
        this.botMenuItem = addItem;
        addItem.setVisibility(8);
        this.botMenuItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        this.actionBarOnItemClick = actionBar.getActionBarMenuOnItemClick();
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, chatActivityEnterView.getParentFragment().getResourceProvider(), getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        AnonymousClass1 r2 = new BotWebViewContainer.Delegate() {
            public /* synthetic */ void onSendWebViewData(String str) {
                BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
            }

            public /* synthetic */ void onWebAppReady() {
                BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
            }

            public void onCloseRequested(Runnable runnable) {
                BotWebViewMenuContainer.this.dismiss(runnable);
            }

            public void onWebAppExpand() {
                if (!BotWebViewMenuContainer.this.swipeContainer.isSwipeInProgress()) {
                    BotWebViewMenuContainer.this.swipeContainer.stickTo((-BotWebViewMenuContainer.this.swipeContainer.getOffsetY()) + BotWebViewMenuContainer.this.swipeContainer.getTopActionBarOffsetY());
                }
            }

            public void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3) {
                ChatActivityBotWebViewButton botWebViewButton = chatActivityEnterView.getBotWebViewButton();
                botWebViewButton.setupButtonParams(z2, str, i, i2, z3);
                botWebViewButton.setOnClickListener(new BotWebViewMenuContainer$1$$ExternalSyntheticLambda0(this));
                if (z != BotWebViewMenuContainer.this.botWebViewButtonWasVisible) {
                    BotWebViewMenuContainer.this.animateBotButton(z);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSetupMainButton$0(View view) {
                BotWebViewMenuContainer.this.webViewContainer.onMainButtonPressed();
            }
        };
        this.webViewDelegate = r2;
        botWebViewContainer.setDelegate(r2);
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(NUM);
        AnonymousClass2 r0 = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:11:0x0029  */
            /* JADX WARNING: Removed duplicated region for block: B:8:0x001f  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onMeasure(int r5, int r6) {
                /*
                    r4 = this;
                    int r0 = android.view.View.MeasureSpec.getSize(r6)
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r1 != 0) goto L_0x0018
                    android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r2 = r1.x
                    int r1 = r1.y
                    if (r2 <= r1) goto L_0x0018
                    float r0 = (float) r0
                    r1 = 1080033280(0x40600000, float:3.5)
                    float r0 = r0 / r1
                    int r0 = (int) r0
                    goto L_0x001c
                L_0x0018:
                    int r0 = r0 / 5
                    int r0 = r0 * 2
                L_0x001c:
                    r1 = 0
                    if (r0 >= 0) goto L_0x0020
                    r0 = 0
                L_0x0020:
                    float r2 = r4.getOffsetY()
                    float r0 = (float) r0
                    int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                    if (r2 == 0) goto L_0x0037
                    org.telegram.ui.Components.BotWebViewMenuContainer r2 = org.telegram.ui.Components.BotWebViewMenuContainer.this
                    r3 = 1
                    boolean unused = r2.ignoreLayout = r3
                    r4.setOffsetY(r0)
                    org.telegram.ui.Components.BotWebViewMenuContainer r0 = org.telegram.ui.Components.BotWebViewMenuContainer.this
                    boolean unused = r0.ignoreLayout = r1
                L_0x0037:
                    int r6 = android.view.View.MeasureSpec.getSize(r6)
                    int r0 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
                    int r6 = r6 - r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r6 = r6 - r0
                    r0 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r6 = r6 + r0
                    r0 = 1084227584(0x40a00000, float:5.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r6 = r6 - r0
                    r0 = 1073741824(0x40000000, float:2.0)
                    int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r0)
                    super.onMeasure(r5, r6)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewMenuContainer.AnonymousClass2.onMeasure(int, int):void");
            }

            public void requestLayout() {
                if (!BotWebViewMenuContainer.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.swipeContainer = r0;
        r0.setScrollListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda11(this));
        this.swipeContainer.setScrollEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda12(this));
        this.swipeContainer.addView(this.webViewContainer);
        this.swipeContainer.setDelegate(new BotWebViewMenuContainer$$ExternalSyntheticLambda19(this));
        this.swipeContainer.setTopActionBarOffsetY((float) ((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f)));
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
        this.swipeContainer.setIsKeyboardVisible(new BotWebViewMenuContainer$$ExternalSyntheticLambda16(chatActivityEnterView));
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 24.0f, 0.0f, 0.0f));
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(context, chatActivityEnterView.getParentFragment().getResourceProvider());
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 5.0f));
        this.webViewContainer.setWebViewProgressListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda3(this));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - (Math.min(this.swipeContainer.getSwipeOffsetY(), (float) this.swipeContainer.getHeight()) / ((float) this.swipeContainer.getHeight()))) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float f = ((float) (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0)) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ Boolean lambda$new$7(ChatActivityEnterView chatActivityEnterView, Void voidR) {
        return Boolean.valueOf(chatActivityEnterView.getSizeNotifierLayout().getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda1(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    BotWebViewMenuContainer.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public void invalidateActionBar() {
        int blendARGB = ColorUtils.blendARGB(getColor("actionBarDefaultSubtitle"), getColor("windowBackgroundWhiteGrayText"), this.actionBarTransitionProgress);
        ChatActivity parentFragment = this.parentEnterView.getParentFragment();
        ActionBar actionBar = parentFragment.getActionBar();
        actionBar.setBackgroundColor(ColorUtils.blendARGB(getColor("actionBarDefault"), getColor("windowBackgroundWhite"), this.actionBarTransitionProgress));
        actionBar.setItemsColor(ColorUtils.blendARGB(getColor("actionBarDefaultIcon"), getColor("windowBackgroundWhiteBlackText"), this.actionBarTransitionProgress), false);
        actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(getColor("actionBarDefaultSelector"), getColor("actionBarWhiteSelector"), this.actionBarTransitionProgress), false);
        actionBar.setSubtitleColor(blendARGB);
        ChatAvatarContainer avatarContainer = parentFragment.getAvatarContainer();
        avatarContainer.getTitleTextView().setTextColor(ColorUtils.blendARGB(getColor("actionBarDefaultTitle"), getColor("windowBackgroundWhiteBlackText"), this.actionBarTransitionProgress));
        avatarContainer.getSubtitleTextView().setTextColor(blendARGB);
        avatarContainer.setOverrideSubtitleColor(this.actionBarTransitionProgress == 0.0f ? null : Integer.valueOf(blendARGB));
        updateLightStatusBar();
    }

    public boolean onBackPressed() {
        return this.webViewContainer.onBackPressed();
    }

    /* access modifiers changed from: private */
    public void animateBotButton(boolean z) {
        ChatActivityBotWebViewButton botWebViewButton = this.parentEnterView.getBotWebViewButton();
        SpringAnimation springAnimation2 = this.botWebViewButtonAnimator;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
            this.botWebViewButtonAnimator = null;
        }
        float f = 0.0f;
        botWebViewButton.setProgress(z ? 0.0f : 1.0f);
        if (z) {
            botWebViewButton.setVisibility(0);
        }
        SimpleFloatPropertyCompat<ChatActivityBotWebViewButton> simpleFloatPropertyCompat = ChatActivityBotWebViewButton.PROGRESS_PROPERTY;
        SpringAnimation springAnimation3 = new SpringAnimation(botWebViewButton, simpleFloatPropertyCompat);
        if (z) {
            f = 1.0f;
        }
        SpringAnimation springAnimation4 = (SpringAnimation) ((SpringAnimation) springAnimation3.setSpring(new SpringForce(f * simpleFloatPropertyCompat.getMultiplier()).setStiffness(z ? 600.0f : 750.0f).setDampingRatio(1.0f)).addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda7(this))).addEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda6(this, z, botWebViewButton));
        this.botWebViewButtonAnimator = springAnimation4;
        springAnimation4.start();
        this.botWebViewButtonWasVisible = z;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateBotButton$10(DynamicAnimation dynamicAnimation, float f, float f2) {
        float multiplier = f / ChatActivityBotWebViewButton.PROGRESS_PROPERTY.getMultiplier();
        this.parentEnterView.setBotWebViewButtonOffsetX(((float) AndroidUtilities.dp(64.0f)) * multiplier);
        this.parentEnterView.setComposeShadowAlpha(1.0f - multiplier);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateBotButton$11(boolean z, ChatActivityBotWebViewButton chatActivityBotWebViewButton, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        if (!z) {
            chatActivityBotWebViewButton.setVisibility(8);
        }
        if (this.botWebViewButtonAnimator == dynamicAnimation) {
            this.botWebViewButtonAnimator = null;
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = (SpringAnimation) new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f)).addEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda5(this));
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$12(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        ChatActivity parentFragment = this.parentEnterView.getParentFragment();
        ChatAvatarContainer avatarContainer = parentFragment.getAvatarContainer();
        avatarContainer.setClickable(f == 0.0f);
        avatarContainer.getAvatarImageView().setClickable(f == 0.0f);
        ActionBar actionBar = parentFragment.getActionBar();
        if (f != 100.0f || !this.parentEnterView.hasBotWebView()) {
            parentFragment.showHeaderItem(true);
            this.botMenuItem.setVisibility(8);
            actionBar.setActionBarMenuOnItemClick(this.actionBarOnItemClick);
            return;
        }
        parentFragment.showHeaderItem(false);
        this.botMenuItem.setVisibility(0);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    BotWebViewMenuContainer.this.dismiss();
                } else if (i == NUM) {
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
    public void onMeasure(int i, int i2) {
        if (this.ignoreMeasure) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(i, i2);
        }
    }

    public void onPanTransitionStart(boolean z, int i) {
        boolean z2;
        if (z) {
            float topActionBarOffsetY = (-this.swipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY();
            if (this.swipeContainer.getSwipeOffsetY() != topActionBarOffsetY) {
                this.swipeContainer.stickTo(topActionBarOffsetY);
                z2 = true;
            } else {
                z2 = false;
            }
            int measureKeyboardHeight = this.parentEnterView.getSizeNotifierLayout().measureKeyboardHeight() + i;
            setMeasuredDimension(getMeasuredWidth(), i);
            this.ignoreMeasure = true;
            if (!z2) {
                ValueAnimator valueAnimator = this.webViewScrollAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.webViewScrollAnimator = null;
                }
                if (this.webViewContainer.getWebView() != null) {
                    int scrollY = this.webViewContainer.getWebView().getScrollY();
                    final int i2 = (measureKeyboardHeight - i) + scrollY;
                    ValueAnimator duration = ValueAnimator.ofInt(new int[]{scrollY, i2}).setDuration(250);
                    this.webViewScrollAnimator = duration;
                    duration.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                    this.webViewScrollAnimator.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda2(this));
                    this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (BotWebViewMenuContainer.this.webViewContainer.getWebView() != null) {
                                BotWebViewMenuContainer.this.webViewContainer.getWebView().setScrollY(i2);
                            }
                            if (animator == BotWebViewMenuContainer.this.webViewScrollAnimator) {
                                ValueAnimator unused = BotWebViewMenuContainer.this.webViewScrollAnimator = null;
                            }
                        }
                    });
                    this.webViewScrollAnimator.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPanTransitionStart$13(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().setScrollY(intValue);
        }
    }

    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        requestLayout();
    }

    private void updateLightStatusBar() {
        boolean z = true;
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
            z = false;
        }
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != z) {
            this.wasLightStatusBar = Boolean.valueOf(z);
            if (Build.VERSION.SDK_INT >= 23) {
                int systemUiVisibility = getSystemUiVisibility();
                setSystemUiVisibility(z ? systemUiVisibility | 8192 : systemUiVisibility & -8193);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.backgroundPaint.setColor(getColor("windowBackgroundWhite"));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        canvas.drawRect(rectF, this.dimPaint);
        float dp = ((float) AndroidUtilities.dp(16.0f)) * (1.0f - this.actionBarTransitionProgress);
        rectF.set(0.0f, AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), 0.0f, this.actionBarTransitionProgress), (float) getWidth(), ((float) getHeight()) + dp);
        canvas.drawRoundRect(rectF, dp, dp, this.backgroundPaint);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || motionEvent.getY() > AndroidUtilities.lerp(this.swipeContainer.getTranslationY() + ((float) AndroidUtilities.dp(24.0f)), 0.0f, this.actionBarTransitionProgress)) {
            return super.onTouchEvent(motionEvent);
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
        float f = 1.0f - this.actionBarTransitionProgress;
        float lerp = AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), ((float) AndroidUtilities.statusBarHeight) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f), this.actionBarTransitionProgress) + ((float) AndroidUtilities.dp(12.0f));
        canvas.scale(f, f, ((float) getWidth()) / 2.0f, lerp);
        canvas.drawLine((((float) getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(16.0f)), lerp, (((float) getWidth()) / 2.0f) + ((float) AndroidUtilities.dp(16.0f)), lerp, this.linePaint);
        canvas.restore();
    }

    public void show(int i, long j, String str) {
        this.dismissed = false;
        if (!(this.currentAccount == i && this.botId == j && ObjectsCompat$$ExternalSyntheticBackport0.m(this.botUrl, str))) {
            this.isLoaded = false;
        }
        this.currentAccount = i;
        this.botId = j;
        this.botUrl = str;
        this.savedEditText = this.parentEnterView.getEditField().getText();
        this.parentEnterView.getEditField().setText((CharSequence) null);
        this.savedReplyMessageObject = this.parentEnterView.getReplyingMessageObject();
        this.savedEditMessageObject = this.parentEnterView.getEditingMessageObject();
        ChatActivity parentFragment = this.parentEnterView.getParentFragment();
        if (parentFragment != null) {
            parentFragment.hideFieldPanel(true);
        }
        if (!this.isLoaded) {
            loadWebView();
        }
        setVisibility(0);
        setAlpha(0.0f);
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                BotWebViewMenuContainer.this.swipeContainer.setSwipeOffsetY((float) BotWebViewMenuContainer.this.swipeContainer.getHeight());
                BotWebViewMenuContainer.this.setAlpha(1.0f);
                ((SpringAnimation) new SpringAnimation(BotWebViewMenuContainer.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).addEndListener(new BotWebViewMenuContainer$6$$ExternalSyntheticLambda0(this))).start();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLayoutChange$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
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
        TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
        tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
        tLRPC$TL_messages_requestWebView.url = this.botUrl;
        tLRPC$TL_messages_requestWebView.flags |= 2;
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", getColor("windowBackgroundWhite"));
            jSONObject.put("text_color", getColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", getColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", getColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", getColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", getColor("featuredStickers_buttonText"));
            TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
            tLRPC$TL_messages_requestWebView.theme_params = tLRPC$TL_dataJSON;
            tLRPC$TL_dataJSON.data = jSONObject.toString();
            tLRPC$TL_messages_requestWebView.flags |= 4;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        tLRPC$TL_messages_requestWebView.from_bot_menu = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_requestWebView, new BotWebViewMenuContainer$$ExternalSyntheticLambda18(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadWebView$15(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda14(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadWebView$14(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            this.isLoaded = true;
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    private int getColor(String str) {
        Integer num;
        Theme.ResourcesProvider resourceProvider = this.parentEnterView.getParentFragment().getResourceProvider();
        if (resourceProvider != null) {
            num = resourceProvider.getColor(str);
        } else {
            num = Integer.valueOf(Theme.getColor(str));
        }
        return num != null ? num.intValue() : Theme.getColor(str);
    }

    public void dismiss() {
        dismiss((Runnable) null);
    }

    public void dismiss(Runnable runnable) {
        if (!this.dismissed) {
            this.dismissed = true;
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((float) (webViewSwipeContainer.getHeight() + this.parentEnterView.getSizeNotifierLayout().measureKeyboardHeight()), new BotWebViewMenuContainer$$ExternalSyntheticLambda13(this, runnable));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$16(Runnable runnable) {
        onDismiss();
        if (runnable != null) {
            runnable.run();
        }
    }

    public void onDismiss() {
        setVisibility(8);
        this.webViewContainer.destroyWebView();
        this.swipeContainer.removeView(this.webViewContainer);
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(getContext(), this.parentEnterView.getParentFragment().getResourceProvider(), getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(this.webViewDelegate);
        this.webViewContainer.setWebViewProgressListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda4(this));
        this.swipeContainer.addView(this.webViewContainer);
        this.isLoaded = false;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        boolean z = this.botWebViewButtonWasVisible;
        if (z) {
            this.botWebViewButtonWasVisible = false;
            animateBotButton(false);
        }
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda8(this), z ? 200 : 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismiss$18(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda0(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    BotWebViewMenuContainer.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismiss$17(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismiss$19() {
        if (this.savedEditText != null) {
            this.parentEnterView.getEditField().setText(this.savedEditText);
            this.savedEditText = null;
        }
        if (this.savedReplyMessageObject != null) {
            ChatActivity parentFragment = this.parentEnterView.getParentFragment();
            if (parentFragment != null) {
                parentFragment.showFieldPanelForReply(this.savedReplyMessageObject);
            }
            this.savedReplyMessageObject = null;
        }
        if (this.savedEditMessageObject != null) {
            ChatActivity parentFragment2 = this.parentEnterView.getParentFragment();
            if (parentFragment2 != null) {
                parentFragment2.showFieldPanelForEdit(true, this.savedEditMessageObject);
            }
            this.savedEditMessageObject = null;
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == objArr[0].longValue()) {
                dismiss();
            }
        } else if (i == NotificationCenter.didSetNewTheme) {
            this.webViewContainer.updateFlickerBackgroundColor(getColor("windowBackgroundWhite"));
            invalidate();
            invalidateActionBar();
            AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda10(this), 300);
        }
    }
}
