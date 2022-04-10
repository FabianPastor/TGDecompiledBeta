package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_simpleWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.LaunchActivity;

public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda12.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda13.INSTANCE).setMultiplier(100.0f);
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
    public Paint dimPaint = new Paint();
    /* access modifiers changed from: private */
    public boolean dismissed;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout frameLayout;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public long lastSwipeTime;
    /* access modifiers changed from: private */
    public Paint linePaint = new Paint(1);
    /* access modifiers changed from: private */
    public TextView mainButton;
    private VerticalPositionAutoAnimator mainButtonAutoAnimator;
    /* access modifiers changed from: private */
    public boolean mainButtonProgressWasVisible;
    /* access modifiers changed from: private */
    public boolean mainButtonWasVisible;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private long peerId;
    private Runnable pollRunnable = new BotWebViewSheet$$ExternalSyntheticLambda3(this);
    private long queryId;
    private VerticalPositionAutoAnimator radialProgressAutoAnimator;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new BotWebViewSheet$$ExternalSyntheticLambda10(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda7(this, tLRPC$TL_error));
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

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public BotWebViewSheet(android.content.Context r18, org.telegram.ui.ActionBar.Theme.ResourcesProvider r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = 2131689509(0x7f0var_, float:1.9008035E38)
            r0.<init>(r1, r3)
            r3 = 0
            r0.actionBarTransitionProgress = r3
            android.graphics.Paint r4 = new android.graphics.Paint
            r5 = 1
            r4.<init>(r5)
            r0.linePaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>()
            r0.dimPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r5)
            r0.backgroundPaint = r4
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda3
            r4.<init>(r0)
            r0.pollRunnable = r4
            r0.resourcesProvider = r2
            org.telegram.ui.Components.BotWebViewSheet$1 r4 = new org.telegram.ui.Components.BotWebViewSheet$1
            r4.<init>(r1)
            r0.swipeContainer = r4
            org.telegram.ui.Components.BotWebViewContainer r4 = new org.telegram.ui.Components.BotWebViewContainer
            java.lang.String r6 = "windowBackgroundWhite"
            int r7 = r0.getColor(r6)
            r4.<init>(r1, r2, r7)
            r0.webViewContainer = r4
            android.webkit.WebView r4 = r4.getWebView()
            r7 = 0
            r4.setVerticalScrollBarEnabled(r7)
            org.telegram.ui.Components.BotWebViewContainer r4 = r0.webViewContainer
            org.telegram.ui.Components.BotWebViewSheet$2 r8 = new org.telegram.ui.Components.BotWebViewSheet$2
            r8.<init>()
            r4.setDelegate(r8)
            android.graphics.Paint r4 = r0.linePaint
            android.graphics.Paint$Style r8 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r8)
            android.graphics.Paint r4 = r0.linePaint
            r8 = 1082130432(0x40800000, float:4.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r4.setStrokeWidth(r8)
            android.graphics.Paint r4 = r0.linePaint
            android.graphics.Paint$Cap r8 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r8)
            android.graphics.Paint r4 = r0.backgroundPaint
            int r6 = r0.getColor(r6)
            r4.setColor(r6)
            android.graphics.Paint r4 = r0.dimPaint
            r6 = 1073741824(0x40000000, float:2.0)
            r4.setColor(r6)
            org.telegram.ui.Components.BotWebViewSheet$3 r4 = new org.telegram.ui.Components.BotWebViewSheet$3
            r4.<init>(r1)
            r0.frameLayout = r4
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda14 r6 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda14
            r6.<init>(r0)
            r4.setDelegate(r6)
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r0.frameLayout
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r6 = r0.swipeContainer
            r8 = -1
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = 48
            r11 = 0
            r12 = 1103101952(0x41CLASSNAME, float:24.0)
            r13 = 0
            r14 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r4.addView(r6, r8)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.mainButton = r4
            r6 = 8
            r4.setVisibility(r6)
            android.widget.TextView r4 = r0.mainButton
            r4.setAlpha(r3)
            android.widget.TextView r4 = r0.mainButton
            r4.setSingleLine()
            android.widget.TextView r4 = r0.mainButton
            r8 = 17
            r4.setGravity(r8)
            android.widget.TextView r4 = r0.mainButton
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r4.setTypeface(r8)
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            android.widget.TextView r8 = r0.mainButton
            r8.setPadding(r4, r7, r4, r7)
            android.widget.TextView r4 = r0.mainButton
            r8 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r5, r8)
            android.widget.TextView r4 = r0.mainButton
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda1
            r5.<init>(r0)
            r4.setOnClickListener(r5)
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r0.frameLayout
            android.widget.TextView r5 = r0.mainButton
            r8 = -1
            r9 = 48
            r10 = 80
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r9, (int) r10)
            r4.addView(r5, r10)
            android.widget.TextView r4 = r0.mainButton
            org.telegram.ui.Components.VerticalPositionAutoAnimator r4 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            r0.mainButtonAutoAnimator = r4
            org.telegram.ui.Components.RadialProgressView r4 = new org.telegram.ui.Components.RadialProgressView
            r4.<init>(r1)
            r0.radialProgressView = r4
            r5 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setSize(r5)
            org.telegram.ui.Components.RadialProgressView r4 = r0.radialProgressView
            r4.setAlpha(r3)
            org.telegram.ui.Components.RadialProgressView r4 = r0.radialProgressView
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r4.setScaleX(r5)
            org.telegram.ui.Components.RadialProgressView r4 = r0.radialProgressView
            r4.setScaleY(r5)
            org.telegram.ui.Components.RadialProgressView r4 = r0.radialProgressView
            r4.setVisibility(r6)
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r0.frameLayout
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r10 = 28
            r11 = 1105199104(0x41e00000, float:28.0)
            r12 = 85
            r15 = 1092616192(0x41200000, float:10.0)
            r16 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r4.addView(r5, r6)
            org.telegram.ui.Components.RadialProgressView r4 = r0.radialProgressView
            org.telegram.ui.Components.VerticalPositionAutoAnimator r4 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            r0.radialProgressAutoAnimator = r4
            android.content.Context r4 = r17.getContext()
            r5 = 2131165500(0x7var_c, float:1.7945219E38)
            android.graphics.drawable.Drawable r4 = androidx.core.content.ContextCompat.getDrawable(r4, r5)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            r0.actionBarShadow = r4
            org.telegram.ui.ActionBar.ActionBar r4 = new org.telegram.ui.ActionBar.ActionBar
            r4.<init>(r1, r2)
            r0.actionBar = r4
            r4.setBackgroundColor(r7)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r2 = "windowBackgroundWhiteBlackText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTitleColor(r4)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setItemsColor(r2, r7)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r2 = "actionBarWhiteSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setItemsBackgroundColor(r2, r7)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131165503(0x7var_f, float:1.7945225E38)
            r1.setBackButtonImage(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.Components.BotWebViewSheet$4 r2 = new org.telegram.ui.Components.BotWebViewSheet$4
            r2.<init>()
            r1.setActionBarMenuOnItemClick(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r1.setAlpha(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r1 = r0.frameLayout
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = -2
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r3, (int) r9)
            r1.addView(r2, r3)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r1 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewContainer r2 = r0.webViewContainer
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r3)
            r1.addView(r2, r3)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r1 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewContainer r2 = r0.webViewContainer
            android.webkit.WebView r2 = r2.getWebView()
            r1.setWebView(r2)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r1 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda4
            r2.<init>(r0)
            r1.setScrollListener(r2)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r1 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda11
            r2.<init>(r0)
            r1.setDelegate(r2)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r1 = r0.swipeContainer
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r2 = r2 + r3
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            r1.setTopActionBarOffsetY(r2)
            org.telegram.ui.Components.SizeNotifierFrameLayout r1 = r0.frameLayout
            android.view.ViewGroup$LayoutParams r2 = new android.view.ViewGroup$LayoutParams
            r2.<init>(r8, r8)
            r0.setContentView(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewSheet.<init>(android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(int i, boolean z) {
        if (i > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view) {
        this.webViewContainer.onMainButtonPressed();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7() {
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
        float max = Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        this.mainButtonAutoAnimator.setOffsetY(max);
        this.radialProgressAutoAnimator.setOffsetY(max);
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
        int i3 = i;
        String str4 = str2;
        this.currentAccount = i3;
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
        final int i4 = i;
        boolean z3 = false;
        final long j4 = j;
        final String str5 = str;
        final String str6 = str2;
        final boolean z4 = z;
        final int i5 = i2;
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
                    BotWebViewSheet.this.requestWebView(i4, j4, j3, str5, str6, z4, i5, z5);
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
        this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
        long j5 = j2;
        this.webViewContainer.loadFlicker(i3, j5);
        if (z) {
            TLRPC$TL_messages_requestSimpleWebView tLRPC$TL_messages_requestSimpleWebView = new TLRPC$TL_messages_requestSimpleWebView();
            tLRPC$TL_messages_requestSimpleWebView.bot = MessagesController.getInstance(i).getInputUser(j5);
            if (z3) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestSimpleWebView.theme_params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = str3;
                tLRPC$TL_messages_requestSimpleWebView.flags |= 1;
            }
            tLRPC$TL_messages_requestSimpleWebView.url = str2;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestSimpleWebView, new BotWebViewSheet$$ExternalSyntheticLambda9(this));
            return;
        }
        String str7 = str2;
        TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
        tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
        tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j5);
        if (str7 != null) {
            tLRPC$TL_messages_requestWebView.url = str7;
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
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new BotWebViewSheet$$ExternalSyntheticLambda8(this));
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda6(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$9(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_simpleWebViewResultUrl) {
            this.webViewContainer.loadUrl(((TLRPC$TL_simpleWebViewResultUrl) tLObject).url);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda5(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$11(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
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
            this.webViewContainer.destroyWebView();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((float) (webViewSwipeContainer.getHeight() + this.frameLayout.measureKeyboardHeight()), new BotWebViewSheet$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$13() {
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
