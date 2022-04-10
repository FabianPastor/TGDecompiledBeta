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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_messages_sendWebViewData;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public class BotWebViewMenuContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewMenuContainer> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewMenuContainer$$ExternalSyntheticLambda10.INSTANCE, BotWebViewMenuContainer$$ExternalSyntheticLambda11.INSTANCE).setMultiplier(100.0f);
    private ActionBar.ActionBarMenuOnItemClick actionBarOnItemClick;
    /* access modifiers changed from: private */
    public float actionBarTransitionProgress;
    private Paint backgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public long botId;
    private ActionBarMenuItem botMenuItem;
    private String botUrl;
    private ValueAnimator botWebViewButtonAnimator;
    /* access modifiers changed from: private */
    public boolean botWebViewButtonWasVisible;
    /* access modifiers changed from: private */
    public int currentAccount;
    private Paint dimPaint = new Paint();
    private boolean dismissed;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public boolean isLoaded;
    /* access modifiers changed from: private */
    public long lastSwipeTime;
    private Paint linePaint = new Paint();
    private ChatActivityEnterView parentEnterView;
    private Runnable pollRunnable = new BotWebViewMenuContainer$$ExternalSyntheticLambda2(this);
    private long queryId;
    private SpringAnimation springAnimation;
    /* access modifiers changed from: private */
    public ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    /* access modifiers changed from: private */
    public BotWebViewContainer webViewContainer;
    private BotWebViewContainer.Delegate webViewDelegate;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(BotWebViewMenuContainer botWebViewMenuContainer, float f) {
        botWebViewMenuContainer.actionBarTransitionProgress = f;
        botWebViewMenuContainer.invalidate();
        ChatActivity parentFragment = botWebViewMenuContainer.parentEnterView.getParentFragment();
        ActionBar actionBar = parentFragment.getActionBar();
        actionBar.setBackgroundColor(ColorUtils.blendARGB(botWebViewMenuContainer.getColor("actionBarDefault"), botWebViewMenuContainer.getColor("windowBackgroundWhite"), f));
        actionBar.setItemsColor(ColorUtils.blendARGB(botWebViewMenuContainer.getColor("actionBarDefaultIcon"), botWebViewMenuContainer.getColor("windowBackgroundWhiteBlackText"), f), false);
        actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(botWebViewMenuContainer.getColor("actionBarDefaultSelector"), botWebViewMenuContainer.getColor("actionBarWhiteSelector"), f), false);
        ChatAvatarContainer avatarContainer = parentFragment.getAvatarContainer();
        avatarContainer.getTitleTextView().setTextColor(ColorUtils.blendARGB(botWebViewMenuContainer.getColor("actionBarDefaultTitle"), botWebViewMenuContainer.getColor("windowBackgroundWhiteBlackText"), f));
        avatarContainer.getSubtitleTextView().setTextColor(ColorUtils.blendARGB(botWebViewMenuContainer.getColor("actionBarDefaultSubtitle"), botWebViewMenuContainer.getColor("windowBackgroundWhiteGrayText"), f));
        botWebViewMenuContainer.updateLightStatusBar();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        if (!this.dismissed) {
            TLRPC$TL_messages_prolongWebView tLRPC$TL_messages_prolongWebView = new TLRPC$TL_messages_prolongWebView();
            tLRPC$TL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            tLRPC$TL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
            tLRPC$TL_messages_prolongWebView.query_id = this.queryId;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new BotWebViewMenuContainer$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda6(this, tLRPC$TL_error));
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
        AnonymousClass1 r1 = new BotWebViewContainer.Delegate() {
            private boolean sentWebViewData;

            public void onCloseRequested() {
                BotWebViewMenuContainer.this.dismiss();
            }

            public void onSendWebViewData(String str) {
                if (!this.sentWebViewData) {
                    this.sentWebViewData = true;
                    TLRPC$TL_messages_sendWebViewData tLRPC$TL_messages_sendWebViewData = new TLRPC$TL_messages_sendWebViewData();
                    tLRPC$TL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewMenuContainer.this.currentAccount).getInputUser(BotWebViewMenuContainer.this.botId);
                    tLRPC$TL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
                    tLRPC$TL_messages_sendWebViewData.button_text = "Menu";
                    tLRPC$TL_messages_sendWebViewData.data = str;
                    ConnectionsManager.getInstance(BotWebViewMenuContainer.this.currentAccount).sendRequest(tLRPC$TL_messages_sendWebViewData, new BotWebViewMenuContainer$1$$ExternalSyntheticLambda2(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSendWebViewData$0() {
                BotWebViewMenuContainer.this.dismiss();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSendWebViewData$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$1$$ExternalSyntheticLambda1(this));
            }

            public void onWebAppExpand() {
                if (System.currentTimeMillis() - BotWebViewMenuContainer.this.lastSwipeTime > 1000 && !BotWebViewMenuContainer.this.swipeContainer.isSwipeInProgress()) {
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
            public /* synthetic */ void lambda$onSetupMainButton$2(View view) {
                BotWebViewMenuContainer.this.webViewContainer.onMainButtonPressed();
            }
        };
        this.webViewDelegate = r1;
        botWebViewContainer.setDelegate(r1);
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(NUM);
        this.backgroundPaint.setColor(getColor("windowBackgroundWhite"));
        AnonymousClass2 r9 = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
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
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    r1 = 1073741824(0x40000000, float:2.0)
                    if (r0 == 0) goto L_0x005e
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L_0x005e
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isSmallTablet()
                    if (r0 != 0) goto L_0x005e
                    android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r0 = r5.x
                    int r5 = r5.y
                    int r5 = java.lang.Math.min(r0, r5)
                    float r5 = (float) r5
                    r0 = 1061997773(0x3f4ccccd, float:0.8)
                    float r5 = r5 * r0
                    int r5 = (int) r5
                    int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r1)
                L_0x005e:
                    int r6 = android.view.View.MeasureSpec.getSize(r6)
                    int r0 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
                    int r6 = r6 - r0
                    r0 = 1082130432(0x40800000, float:4.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r6 = r6 - r0
                    int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r1)
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
        this.swipeContainer = r9;
        r9.setWebView(this.webViewContainer.getWebView());
        this.swipeContainer.setScrollListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda3(this));
        this.swipeContainer.addView(this.webViewContainer);
        this.swipeContainer.setDelegate(new BotWebViewMenuContainer$$ExternalSyntheticLambda9(this));
        this.swipeContainer.setTopActionBarOffsetY((float) ((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f)));
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 24.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - (this.swipeContainer.getSwipeOffsetY() / ((float) this.swipeContainer.getHeight()))) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        invalidate();
        if (this.springAnimation != null) {
            float f = ((float) (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0)) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public void animateBotButton(final boolean z) {
        final ChatActivityBotWebViewButton botWebViewButton = this.parentEnterView.getBotWebViewButton();
        ValueAnimator valueAnimator = this.botWebViewButtonAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.botWebViewButtonAnimator = null;
        }
        float[] fArr = new float[2];
        float f = 0.0f;
        fArr[0] = z ? 0.0f : 1.0f;
        if (z) {
            f = 1.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(250);
        this.botWebViewButtonAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.botWebViewButtonAnimator.addUpdateListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda0(this, botWebViewButton));
        this.botWebViewButtonAnimator.addListener(new AnimatorListenerAdapter(this) {
            public void onAnimationStart(Animator animator) {
                if (z) {
                    botWebViewButton.setVisibility(0);
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    botWebViewButton.setVisibility(8);
                }
            }
        });
        this.botWebViewButtonAnimator.start();
        this.botWebViewButtonWasVisible = z;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateBotButton$6(ChatActivityBotWebViewButton chatActivityBotWebViewButton, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatActivityBotWebViewButton.setProgress(floatValue);
        this.parentEnterView.setBotWebViewButtonOffsetX(((float) AndroidUtilities.dp(64.0f)) * floatValue);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = (SpringAnimation) new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f)).addEndListener(new BotWebViewMenuContainer$$ExternalSyntheticLambda1(this));
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$7(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        ChatActivity parentFragment = this.parentEnterView.getParentFragment();
        ChatAvatarContainer avatarContainer = parentFragment.getAvatarContainer();
        boolean z2 = true;
        avatarContainer.setClickable(f == 0.0f);
        BackupImageView avatarImageView = avatarContainer.getAvatarImageView();
        if (f != 0.0f) {
            z2 = false;
        }
        avatarImageView.setClickable(z2);
        ActionBar actionBar = parentFragment.getActionBar();
        if (f == 100.0f) {
            parentFragment.getHeaderItem().setVisibility(8);
            this.botMenuItem.setVisibility(0);
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    if (i == -1) {
                        BotWebViewMenuContainer.this.dismiss();
                    } else if (i == NUM) {
                        BotWebViewMenuContainer.this.webViewContainer.getWebView().animate().cancel();
                        BotWebViewMenuContainer.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                        boolean unused = BotWebViewMenuContainer.this.isLoaded = false;
                        BotWebViewMenuContainer.this.loadWebView();
                    }
                }
            });
            return;
        }
        parentFragment.getHeaderItem().setVisibility(0);
        this.botMenuItem.setVisibility(8);
        actionBar.setActionBarMenuOnItemClick(this.actionBarOnItemClick);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation2 = this.springAnimation;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
            this.springAnimation = null;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
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
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        canvas.drawRect(rectF, this.dimPaint);
        float dp = ((float) AndroidUtilities.dp(16.0f)) * (1.0f - this.actionBarTransitionProgress);
        rectF.set(0.0f, AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), 0.0f, this.actionBarTransitionProgress), (float) getWidth(), ((float) getHeight()) + dp);
        canvas.drawRoundRect(rectF, dp, dp, this.backgroundPaint);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || motionEvent.getY() > AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), 0.0f, this.actionBarTransitionProgress)) {
            return super.onTouchEvent(motionEvent);
        }
        dismiss();
        return true;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.linePaint.setColor(Theme.getColor("dialogGrayLine"));
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
                ((SpringAnimation) new SpringAnimation(BotWebViewMenuContainer.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).addEndListener(new BotWebViewMenuContainer$5$$ExternalSyntheticLambda0(this))).start();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLayoutChange$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                BotWebViewMenuContainer.this.webViewContainer.restoreButtonData();
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadWebView() {
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_requestWebView, new BotWebViewMenuContainer$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadWebView$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewMenuContainer$$ExternalSyntheticLambda5(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadWebView$8(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            this.isLoaded = true;
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
            this.webViewContainer.loadFlicker(this.currentAccount, this.botId);
            this.webViewContainer.loadUrl(tLRPC$TL_webViewResultUrl.url);
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
            webViewSwipeContainer.stickTo((float) webViewSwipeContainer.getHeight(), new BotWebViewMenuContainer$$ExternalSyntheticLambda4(this, runnable));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$10(Runnable runnable) {
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
        this.swipeContainer.addView(this.webViewContainer);
        this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        this.isLoaded = false;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        if (this.botWebViewButtonWasVisible) {
            this.botWebViewButtonWasVisible = false;
            animateBotButton(false);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == objArr[0].longValue()) {
                dismiss();
            }
        }
    }
}
