package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_messages_sendWebViewData;
import org.telegram.tgnet.TLRPC$TL_simpleWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.LaunchActivity;

public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda11.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda12.INSTANCE).setMultiplier(100.0f);
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public Drawable actionBarShadow;
    /* access modifiers changed from: private */
    public float actionBarTransitionProgress = 0.0f;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public long botId;
    /* access modifiers changed from: private */
    public String buttonText;
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public Paint dimPaint = new Paint(1);
    private boolean dismissed;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public long lastSwipeTime;
    /* access modifiers changed from: private */
    public Paint linePaint = new Paint(1);
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private long peerId;
    private Runnable pollRunnable = new BotWebViewSheet$$ExternalSyntheticLambda2(this);
    private long queryId;
    private int replyToMsgId;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean silent;
    private SpringAnimation springAnimation;
    /* access modifiers changed from: private */
    public ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    /* access modifiers changed from: private */
    public BotWebViewContainer webViewContainer;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(BotWebViewSheet botWebViewSheet, float f) {
        botWebViewSheet.actionBarTransitionProgress = f;
        botWebViewSheet.frameLayout.invalidate();
        botWebViewSheet.actionBar.setAlpha(f);
        botWebViewSheet.updateLightStatusBar();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        if (!this.dismissed) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new BotWebViewSheet$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda6(this, tLRPC$TL_error));
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

    public BotWebViewSheet(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context, NUM);
        this.resourcesProvider = resourcesProvider2;
        this.swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
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
                    org.telegram.ui.Components.BotWebViewSheet r2 = org.telegram.ui.Components.BotWebViewSheet.this
                    r3 = 1
                    boolean unused = r2.ignoreLayout = r3
                    r4.setOffsetY(r0)
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
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
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewSheet.AnonymousClass1.onMeasure(int, int):void");
            }

            public void requestLayout() {
                if (!BotWebViewSheet.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider2, getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.getWebView().setVerticalScrollBarEnabled(false);
        this.webViewContainer.setDelegate(new BotWebViewContainer.Delegate() {
            private boolean sentWebViewData;

            public /* synthetic */ boolean hasIntegratedMainButton() {
                return BotWebViewContainer.Delegate.CC.$default$hasIntegratedMainButton(this);
            }

            public /* synthetic */ void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3) {
                BotWebViewContainer.Delegate.CC.$default$onSetupMainButton(this, z, z2, str, i, i2, z3);
            }

            public void onCloseRequested() {
                BotWebViewSheet.this.dismiss();
            }

            public void onSendWebViewData(String str) {
                if (!this.sentWebViewData) {
                    this.sentWebViewData = true;
                    TLRPC$TL_messages_sendWebViewData tLRPC$TL_messages_sendWebViewData = new TLRPC$TL_messages_sendWebViewData();
                    tLRPC$TL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
                    tLRPC$TL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
                    tLRPC$TL_messages_sendWebViewData.button_text = BotWebViewSheet.this.buttonText;
                    tLRPC$TL_messages_sendWebViewData.data = str;
                    ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(tLRPC$TL_messages_sendWebViewData, new BotWebViewSheet$2$$ExternalSyntheticLambda1(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSendWebViewData$0() {
                BotWebViewSheet.this.dismiss();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSendWebViewData$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new BotWebViewSheet$2$$ExternalSyntheticLambda0(this));
            }

            public void onWebAppExpand() {
                if (System.currentTimeMillis() - BotWebViewSheet.this.lastSwipeTime > 1000 && !BotWebViewSheet.this.swipeContainer.isSwipeInProgress()) {
                    BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
                }
            }
        });
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        this.dimPaint.setColor(NUM);
        AnonymousClass3 r1 = new FrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                canvas.drawRect(rectF, BotWebViewSheet.this.dimPaint);
                float dp = ((float) AndroidUtilities.dp(16.0f)) * (1.0f - BotWebViewSheet.this.actionBarTransitionProgress);
                rectF.set(0.0f, AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress), (float) getWidth(), ((float) getHeight()) + dp);
                canvas.drawRoundRect(rectF, dp, dp, BotWebViewSheet.this.backgroundPaint);
            }

            public void draw(Canvas canvas) {
                super.draw(canvas);
                BotWebViewSheet.this.linePaint.setColor(Theme.getColor("dialogGrayLine"));
                BotWebViewSheet.this.linePaint.setAlpha((int) (((float) BotWebViewSheet.this.linePaint.getAlpha()) * (1.0f - (Math.min(0.5f, BotWebViewSheet.this.actionBarTransitionProgress) / 0.5f))));
                canvas.save();
                float access$700 = 1.0f - BotWebViewSheet.this.actionBarTransitionProgress;
                float lerp = AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), ((float) AndroidUtilities.statusBarHeight) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f), BotWebViewSheet.this.actionBarTransitionProgress) + ((float) AndroidUtilities.dp(12.0f));
                canvas.scale(access$700, access$700, ((float) getWidth()) / 2.0f, lerp);
                canvas.drawLine((((float) getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(16.0f)), lerp, (((float) getWidth()) / 2.0f) + ((float) AndroidUtilities.dp(16.0f)), lerp, BotWebViewSheet.this.linePaint);
                canvas.restore();
                BotWebViewSheet.this.actionBarShadow.setAlpha((int) (BotWebViewSheet.this.actionBar.getAlpha() * 255.0f));
                float y = BotWebViewSheet.this.actionBar.getY() + BotWebViewSheet.this.actionBar.getTranslationY() + ((float) BotWebViewSheet.this.actionBar.getHeight());
                BotWebViewSheet.this.actionBarShadow.setBounds(0, (int) y, getWidth(), (int) (y + ((float) BotWebViewSheet.this.actionBarShadow.getIntrinsicHeight())));
                BotWebViewSheet.this.actionBarShadow.draw(canvas);
            }

            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress)) {
                    return super.onTouchEvent(motionEvent);
                }
                BotWebViewSheet.this.dismiss();
                return true;
            }
        };
        this.frameLayout = r1;
        r1.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 24.0f, 0.0f, 0.0f));
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), NUM).mutate();
        ActionBar actionBar2 = new ActionBar(context, resourcesProvider2);
        this.actionBar = actionBar2;
        actionBar2.setBackgroundColor(0);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    BotWebViewSheet.this.dismiss();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        this.frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 48));
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        this.swipeContainer.setScrollListener(new BotWebViewSheet$$ExternalSyntheticLambda3(this));
        this.swipeContainer.setDelegate(new BotWebViewSheet$$ExternalSyntheticLambda10(this));
        this.swipeContainer.setTopActionBarOffsetY((float) ((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f)));
        setContentView(this.frameLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - (this.swipeContainer.getSwipeOffsetY() / ((float) this.swipeContainer.getHeight()))) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.frameLayout.invalidate();
        this.webViewContainer.invalidateTranslation();
        if (this.springAnimation != null) {
            float f = ((float) (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0)) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        this.lastSwipeTime = System.currentTimeMillis();
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
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
                int systemUiVisibility = this.frameLayout.getSystemUiVisibility();
                this.frameLayout.setSystemUiVisibility(z ? systemUiVisibility | 8192 : systemUiVisibility & -8193);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            window.addFlags(-NUM);
        } else if (i >= 21) {
            window.addFlags(-NUM);
        }
        window.setWindowAnimations(NUM);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        attributes.flags &= -3;
        attributes.softInputMode = 16;
        attributes.height = -1;
        boolean z = true;
        if (i >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
        if (i >= 23) {
            window.setStatusBarColor(0);
        }
        this.frameLayout.setSystemUiVisibility(1280);
        if (i >= 21) {
            this.frameLayout.setOnApplyWindowInsetsListener(BotWebViewSheet$$ExternalSyntheticLambda0.INSTANCE);
        }
        if (i >= 26) {
            if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation2 = this.springAnimation;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
            this.springAnimation = null;
        }
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, boolean z, int i2, boolean z2) {
        String str3;
        String str4 = str2;
        this.currentAccount = i;
        this.peerId = j;
        this.botId = j2;
        this.replyToMsgId = i2;
        this.silent = z2;
        this.buttonText = str;
        this.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(i).getUser(Long.valueOf(j2))));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.removeAllViews();
        ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
        addItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        addItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        AnonymousClass5 r0 = r1;
        final long j3 = j2;
        ActionBar actionBar2 = this.actionBar;
        final int i3 = i;
        boolean z3 = false;
        final long j4 = j;
        final String str5 = str;
        final String str6 = str2;
        final boolean z4 = z;
        final int i4 = i2;
        final boolean z5 = z2;
        AnonymousClass5 r1 = new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    BotWebViewSheet.this.dismiss();
                } else if (i == NUM) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j3);
                    if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) BotWebViewSheet.this.parentActivity).lambda$runLinkRequest$54(new ChatActivity(bundle));
                    }
                    BotWebViewSheet.this.dismiss();
                } else if (i == NUM) {
                    BotWebViewSheet.this.webViewContainer.getWebView().animate().cancel();
                    BotWebViewSheet.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    BotWebViewSheet.this.requestWebView(i3, j4, j3, str5, str6, z4, i4, z5);
                }
            }
        };
        actionBar2.setActionBarMenuOnItemClick(r0);
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", getColor("windowBackgroundWhite"));
            jSONObject.put("text_color", getColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", getColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", getColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", getColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", getColor("featuredStickers_buttonText"));
            str3 = jSONObject.toString();
            z3 = true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            str3 = null;
        }
        if (z) {
            TLRPC$TL_messages_requestSimpleWebView tLRPC$TL_messages_requestSimpleWebView = new TLRPC$TL_messages_requestSimpleWebView();
            long j5 = j2;
            tLRPC$TL_messages_requestSimpleWebView.bot = MessagesController.getInstance(i).getInputUser(j5);
            if (z3) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestSimpleWebView.theme_params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = str3;
                tLRPC$TL_messages_requestSimpleWebView.flags |= 1;
            }
            tLRPC$TL_messages_requestSimpleWebView.url = str4;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestSimpleWebView, new BotWebViewSheet$$ExternalSyntheticLambda8(this, i, j5));
            return;
        }
        int i5 = i;
        long j6 = j2;
        TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
        tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
        tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j6);
        if (str4 != null) {
            tLRPC$TL_messages_requestWebView.url = str4;
            tLRPC$TL_messages_requestWebView.flags |= 2;
        }
        int i6 = i2;
        if (i6 != 0) {
            tLRPC$TL_messages_requestWebView.reply_to_msg_id = i6;
            tLRPC$TL_messages_requestWebView.flags |= 1;
        }
        if (z3) {
            TLRPC$TL_dataJSON tLRPC$TL_dataJSON2 = new TLRPC$TL_dataJSON();
            tLRPC$TL_messages_requestWebView.theme_params = tLRPC$TL_dataJSON2;
            tLRPC$TL_dataJSON2.data = str3;
            tLRPC$TL_messages_requestWebView.flags |= 4;
        }
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new BotWebViewSheet$$ExternalSyntheticLambda9(this, i5, j6));
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$8(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda4(this, tLObject, i, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$7(TLObject tLObject, int i, long j) {
        if (tLObject instanceof TLRPC$TL_simpleWebViewResultUrl) {
            this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j)));
            this.webViewContainer.loadFlicker(i, j);
            this.webViewContainer.loadUrl(((TLRPC$TL_simpleWebViewResultUrl) tLObject).url);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$10(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda5(this, tLObject, i, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$9(TLObject tLObject, int i, long j) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j)));
            this.webViewContainer.loadFlicker(i, j);
            this.webViewContainer.loadUrl(tLRPC$TL_webViewResultUrl.url);
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    private int getColor(String str) {
        Integer num;
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        if (resourcesProvider2 != null) {
            num = resourcesProvider2.getColor(str);
        } else {
            num = Integer.valueOf(Theme.getColor(str));
        }
        return num != null ? num.intValue() : Theme.getColor(str);
    }

    public void show() {
        this.frameLayout.setAlpha(0.0f);
        this.frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetY((float) BotWebViewSheet.this.swipeContainer.getHeight());
                BotWebViewSheet.this.frameLayout.setAlpha(1.0f);
                new SpringAnimation(BotWebViewSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).start();
            }
        });
        super.show();
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((float) webViewSwipeContainer.getHeight(), new BotWebViewSheet$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$11() {
        super.dismiss();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == objArr[0].longValue()) {
                dismiss();
            }
        }
    }
}
