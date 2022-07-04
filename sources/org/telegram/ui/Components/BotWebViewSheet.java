package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.LaunchActivity;

public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda9.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda10.INSTANCE).setMultiplier(100.0f);
    private static final int POLL_PERIOD = 60000;
    public static final int TYPE_BOT_MENU_BUTTON = 2;
    public static final int TYPE_SIMPLE_WEB_VIEW_BUTTON = 1;
    public static final int TYPE_WEB_VIEW_BUTTON = 0;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public int actionBarColor;
    /* access modifiers changed from: private */
    public Paint actionBarPaint = new Paint(1);
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
    private long lastSwipeTime;
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
    public boolean overrideBackgroundColor;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private long peerId;
    private Runnable pollRunnable = new BotWebViewSheet$$ExternalSyntheticLambda16(this);
    /* access modifiers changed from: private */
    public ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    /* access modifiers changed from: private */
    public long queryId;
    private VerticalPositionAutoAnimator radialProgressAutoAnimator;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    private int replyToMsgId;
    private Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem settingsItem;
    private boolean silent;
    private SpringAnimation springAnimation;
    /* access modifiers changed from: private */
    public ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    /* access modifiers changed from: private */
    public BotWebViewContainer webViewContainer;

    @Retention(RetentionPolicy.SOURCE)
    public @interface WebViewType {
    }

    static /* synthetic */ void lambda$static$1(BotWebViewSheet obj, float value) {
        obj.actionBarTransitionProgress = value;
        obj.frameLayout.invalidate();
        obj.actionBar.setAlpha(value);
        obj.updateLightStatusBar();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m620lambda$new$4$orgtelegramuiComponentsBotWebViewSheet() {
        if (!this.dismissed) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(prolongWebView, new BotWebViewSheet$$ExternalSyntheticLambda4(this));
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m619lambda$new$3$orgtelegramuiComponentsBotWebViewSheet(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda2(this, error));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m618lambda$new$2$orgtelegramuiComponentsBotWebViewSheet(TLRPC.TL_error error) {
        if (!this.dismissed) {
            if (error != null) {
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
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r5)
            r0.actionBarPaint = r4
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda16 r4 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda16
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
            org.telegram.ui.Components.BotWebViewSheet$2 r7 = new org.telegram.ui.Components.BotWebViewSheet$2
            r7.<init>(r1, r2)
            r4.setDelegate(r7)
            android.graphics.Paint r4 = r0.linePaint
            android.graphics.Paint$Style r7 = android.graphics.Paint.Style.FILL_AND_STROKE
            r4.setStyle(r7)
            android.graphics.Paint r4 = r0.linePaint
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r4.setStrokeWidth(r7)
            android.graphics.Paint r4 = r0.linePaint
            android.graphics.Paint$Cap r7 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r7)
            android.graphics.Paint r4 = r0.dimPaint
            r7 = 1073741824(0x40000000, float:2.0)
            r4.setColor(r7)
            int r4 = r0.getColor(r6)
            r0.actionBarColor = r4
            org.telegram.ui.Components.BotWebViewSheet$3 r4 = new org.telegram.ui.Components.BotWebViewSheet$3
            r4.<init>(r1)
            r0.frameLayout = r4
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda12 r6 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda12
            r6.<init>(r0)
            r4.setDelegate(r6)
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r0.frameLayout
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r6 = r0.swipeContainer
            r7 = -1
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9 = 49
            r10 = 0
            r11 = 1103101952(0x41CLASSNAME, float:24.0)
            r12 = 0
            r13 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r4.addView(r6, r7)
            org.telegram.ui.Components.BotWebViewSheet$4 r4 = new org.telegram.ui.Components.BotWebViewSheet$4
            r4.<init>(r1)
            r0.mainButton = r4
            r6 = 8
            r4.setVisibility(r6)
            android.widget.TextView r4 = r0.mainButton
            r4.setAlpha(r3)
            android.widget.TextView r4 = r0.mainButton
            r4.setSingleLine()
            android.widget.TextView r4 = r0.mainButton
            r7 = 17
            r4.setGravity(r7)
            android.widget.TextView r4 = r0.mainButton
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r4.setTypeface(r7)
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            android.widget.TextView r7 = r0.mainButton
            r8 = 0
            r7.setPadding(r4, r8, r4, r8)
            android.widget.TextView r7 = r0.mainButton
            r9 = 1096810496(0x41600000, float:14.0)
            r7.setTextSize(r5, r9)
            android.widget.TextView r5 = r0.mainButton
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda13 r7 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda13
            r7.<init>(r0)
            r5.setOnClickListener(r7)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r0.frameLayout
            android.widget.TextView r7 = r0.mainButton
            r9 = -1
            r10 = 48
            r11 = 81
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r10, (int) r11)
            r5.addView(r7, r10)
            android.widget.TextView r5 = r0.mainButton
            org.telegram.ui.Components.VerticalPositionAutoAnimator r5 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r5)
            r0.mainButtonAutoAnimator = r5
            org.telegram.ui.Components.BotWebViewSheet$5 r5 = new org.telegram.ui.Components.BotWebViewSheet$5
            r5.<init>(r1)
            r0.radialProgressView = r5
            r7 = 1099956224(0x41900000, float:18.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.setSize(r7)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r5.setAlpha(r3)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r7 = 1036831949(0x3dcccccd, float:0.1)
            r5.setScaleX(r7)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r5.setScaleY(r7)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r5.setVisibility(r6)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r0.frameLayout
            org.telegram.ui.Components.RadialProgressView r6 = r0.radialProgressView
            r10 = 28
            r11 = 1105199104(0x41e00000, float:28.0)
            r12 = 85
            r14 = 0
            r15 = 1092616192(0x41200000, float:10.0)
            r16 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r7)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            org.telegram.ui.Components.VerticalPositionAutoAnimator r5 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r5)
            r0.radialProgressAutoAnimator = r5
            android.content.Context r5 = r17.getContext()
            r6 = 2131165446(0x7var_, float:1.794511E38)
            android.graphics.drawable.Drawable r5 = androidx.core.content.ContextCompat.getDrawable(r5, r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.actionBarShadow = r5
            org.telegram.ui.Components.BotWebViewSheet$6 r5 = new org.telegram.ui.Components.BotWebViewSheet$6
            r5.<init>(r1, r2)
            r0.actionBar = r5
            r5.setBackgroundColor(r8)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r6 = 2131165473(0x7var_, float:1.7945164E38)
            r5.setBackButtonImage(r6)
            r17.updateActionBarColors()
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.Components.BotWebViewSheet$7 r6 = new org.telegram.ui.Components.BotWebViewSheet$7
            r6.<init>()
            r5.setActionBarMenuOnItemClick(r6)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r5.setAlpha(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r0.frameLayout
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r6 = -2
            r7 = 49
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r6, (int) r7)
            r3.addView(r5, r6)
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r0.frameLayout
            org.telegram.ui.Components.BotWebViewSheet$8 r5 = new org.telegram.ui.Components.BotWebViewSheet$8
            r5.<init>(r1, r2)
            r0.progressView = r5
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 81
            r15 = 0
            r16 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r3.addView(r5, r6)
            org.telegram.ui.Components.BotWebViewContainer r3 = r0.webViewContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda14 r5 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda14
            r5.<init>(r0)
            r3.setWebViewProgressListener(r5)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r3 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewContainer r5 = r0.webViewContainer
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r3.addView(r5, r6)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r3 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda17 r5 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda17
            r5.<init>(r0)
            r3.setScrollListener(r5)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r3 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda15 r5 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda15
            r5.<init>(r0)
            r3.setScrollEndListener(r5)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r3 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda8 r5 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda8
            r5.<init>(r0)
            r3.setDelegate(r5)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r3 = r0.swipeContainer
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r5 = r5 + r6
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            float r5 = (float) r5
            r3.setTopActionBarOffsetY(r5)
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r3 = r0.swipeContainer
            org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda3
            r5.<init>(r0)
            r3.setIsKeyboardVisible(r5)
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r0.frameLayout
            android.view.ViewGroup$LayoutParams r5 = new android.view.ViewGroup$LayoutParams
            r5.<init>(r9, r9)
            r0.setContentView(r3, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewSheet.<init>(android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m621lambda$new$5$orgtelegramuiComponentsBotWebViewSheet(int keyboardHeight, boolean isWidthGreater) {
        if (keyboardHeight > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m622lambda$new$6$orgtelegramuiComponentsBotWebViewSheet(View v) {
        this.webViewContainer.onMainButtonPressed();
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m624lambda$new$8$orgtelegramuiComponentsBotWebViewSheet(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new BotWebViewSheet$$ExternalSyntheticLambda0(this));
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    BotWebViewSheet.this.progressView.setVisibility(8);
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m623lambda$new$7$orgtelegramuiComponentsBotWebViewSheet(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m625lambda$new$9$orgtelegramuiComponentsBotWebViewSheet() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / ((float) this.swipeContainer.getHeight()), 0.0f, 1.0f)) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.frameLayout.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float newPos = ((float) (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0)) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != newPos) {
                this.springAnimation.getSpring().setFinalPosition(newPos);
                this.springAnimation.start();
            }
        }
        float offsetY = Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        this.mainButtonAutoAnimator.setOffsetY(offsetY);
        this.radialProgressAutoAnimator.setOffsetY(offsetY);
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m616lambda$new$10$orgtelegramuiComponentsBotWebViewSheet() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ Boolean m617lambda$new$11$orgtelegramuiComponentsBotWebViewSheet(Void obj) {
        return Boolean.valueOf(this.frameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public void setParentActivity(Activity parentActivity2) {
        this.parentActivity = parentActivity2;
    }

    private void updateActionBarColors() {
        this.actionBar.setTitleColor(getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(getColor("actionBarWhiteSelector"), false);
        this.actionBar.setPopupBackgroundColor(getColor("actionBarDefaultSubmenuBackground"), false);
        this.actionBar.setPopupItemsColor(getColor("actionBarDefaultSubmenuItem"), false, false);
        this.actionBar.setPopupItemsColor(getColor("actionBarDefaultSubmenuItemIcon"), true, false);
        this.actionBar.setPopupItemsSelectorColor(getColor("dialogButtonSelector"), false);
    }

    private void updateLightStatusBar() {
        int flags;
        boolean z = true;
        int color = Theme.getColor("windowBackgroundWhite", (boolean[]) null, true);
        if (AndroidUtilities.isTablet() || ColorUtils.calculateLuminance(color) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
            z = false;
        }
        boolean lightStatusBar = z;
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != lightStatusBar) {
            this.wasLightStatusBar = Boolean.valueOf(lightStatusBar);
            if (Build.VERSION.SDK_INT >= 23) {
                int flags2 = this.frameLayout.getSystemUiVisibility();
                if (lightStatusBar) {
                    flags = flags2 | 8192;
                } else {
                    flags = flags2 & -8193;
                }
                this.frameLayout.setSystemUiVisibility(flags);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            window.addFlags(-NUM);
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(-NUM);
        }
        window.setWindowAnimations(NUM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        params.softInputMode = 16;
        params.height = -1;
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(params);
        if (Build.VERSION.SDK_INT >= 23) {
            window.setStatusBarColor(0);
        }
        this.frameLayout.setSystemUiVisibility(1280);
        if (Build.VERSION.SDK_INT >= 21) {
            this.frameLayout.setOnApplyWindowInsetsListener(BotWebViewSheet$$ExternalSyntheticLambda11.INSTANCE);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
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

    public void requestWebView(int currentAccount2, long peerId2, long botId2, String buttonText2, String buttonUrl, int type, int replyToMsgId2, boolean silent2) {
        final int i = currentAccount2;
        long j = peerId2;
        final long j2 = botId2;
        String str = buttonUrl;
        int i2 = replyToMsgId2;
        this.currentAccount = i;
        this.peerId = j;
        this.botId = j2;
        this.replyToMsgId = i2;
        this.silent = silent2;
        this.buttonText = buttonText2;
        this.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(currentAccount2).getUser(Long.valueOf(botId2))));
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.removeAllViews();
        ActionBarMenuItem otherItem = menu.addItem(0, NUM);
        otherItem.addSubItem(NUM, NUM, (CharSequence) LocaleController.getString(NUM));
        otherItem.addSubItem(NUM, NUM, (CharSequence) LocaleController.getString(NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!BotWebViewSheet.this.webViewContainer.onBackPressed()) {
                        BotWebViewSheet.this.dismiss();
                    }
                } else if (id == NUM) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j2);
                    if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) BotWebViewSheet.this.parentActivity).m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(bundle));
                    }
                    BotWebViewSheet.this.dismiss();
                } else if (id == NUM) {
                    if (BotWebViewSheet.this.webViewContainer.getWebView() != null) {
                        BotWebViewSheet.this.webViewContainer.getWebView().animate().cancel();
                        BotWebViewSheet.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    }
                    BotWebViewSheet.this.progressView.setLoadProgress(0.0f);
                    BotWebViewSheet.this.progressView.setAlpha(1.0f);
                    BotWebViewSheet.this.progressView.setVisibility(0);
                    BotWebViewSheet.this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
                    BotWebViewSheet.this.webViewContainer.loadFlickerAndSettingsItem(i, j2, BotWebViewSheet.this.settingsItem);
                    BotWebViewSheet.this.webViewContainer.reload();
                } else if (id == NUM) {
                    BotWebViewSheet.this.webViewContainer.onSettingsButtonPressed();
                }
            }
        });
        boolean hasThemeParams = true;
        String themeParams = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getColor("windowBackgroundWhite"));
            jsonObject.put("secondary_bg_color", getColor("windowBackgroundGray"));
            jsonObject.put("text_color", getColor("windowBackgroundWhiteBlackText"));
            jsonObject.put("hint_color", getColor("windowBackgroundWhiteHintText"));
            jsonObject.put("link_color", getColor("windowBackgroundWhiteLinkText"));
            jsonObject.put("button_color", getColor("featuredStickers_addButton"));
            jsonObject.put("button_text_color", getColor("featuredStickers_buttonText"));
            themeParams = jsonObject.toString();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            hasThemeParams = false;
        }
        this.webViewContainer.setBotUser(MessagesController.getInstance(currentAccount2).getUser(Long.valueOf(botId2)));
        this.webViewContainer.loadFlickerAndSettingsItem(i, j2, this.settingsItem);
        switch (type) {
            case 0:
                TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
                req.peer = MessagesController.getInstance(currentAccount2).getInputPeer(j);
                req.bot = MessagesController.getInstance(currentAccount2).getInputUser(j2);
                if (str != null) {
                    req.url = str;
                    req.flags |= 2;
                }
                if (i2 != 0) {
                    req.reply_to_msg_id = i2;
                    req.flags |= 1;
                }
                if (hasThemeParams) {
                    req.theme_params = new TLRPC.TL_dataJSON();
                    req.theme_params.data = themeParams;
                    req.flags |= 4;
                }
                ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new BotWebViewSheet$$ExternalSyntheticLambda7(this, i));
                NotificationCenter.getInstance(currentAccount2).addObserver(this, NotificationCenter.webViewResultSent);
                return;
            case 1:
                TLRPC.TL_messages_requestSimpleWebView req2 = new TLRPC.TL_messages_requestSimpleWebView();
                req2.bot = MessagesController.getInstance(currentAccount2).getInputUser(j2);
                if (hasThemeParams) {
                    req2.theme_params = new TLRPC.TL_dataJSON();
                    req2.theme_params.data = themeParams;
                    req2.flags |= 1;
                }
                req2.url = str;
                ConnectionsManager.getInstance(currentAccount2).sendRequest(req2, new BotWebViewSheet$$ExternalSyntheticLambda6(this, i));
                return;
            case 2:
                TLRPC.TL_messages_requestWebView req3 = new TLRPC.TL_messages_requestWebView();
                req3.bot = MessagesController.getInstance(currentAccount2).getInputUser(j2);
                req3.peer = MessagesController.getInstance(currentAccount2).getInputPeer(j2);
                req3.url = str;
                req3.flags |= 2;
                if (hasThemeParams) {
                    req3.theme_params = new TLRPC.TL_dataJSON();
                    req3.theme_params.data = themeParams;
                    req3.flags |= 4;
                }
                ConnectionsManager.getInstance(currentAccount2).sendRequest(req3, new BotWebViewSheet$$ExternalSyntheticLambda5(this, i));
                NotificationCenter.getInstance(currentAccount2).addObserver(this, NotificationCenter.webViewResultSent);
                return;
            default:
                return;
        }
    }

    /* renamed from: lambda$requestWebView$14$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m627x2bdb89d0(int currentAccount2, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda19(this, response, currentAccount2));
    }

    /* renamed from: lambda$requestWebView$13$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m626x68evar_(TLObject response, int currentAccount2) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(currentAccount2, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    /* renamed from: lambda$requestWebView$16$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m629xb1b45c8e(int currentAccount2, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda20(this, response, currentAccount2));
    }

    /* renamed from: lambda$requestWebView$15$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m628xeec7var_f(TLObject response, int currentAccount2) {
        if (response instanceof TLRPC.TL_simpleWebViewResultUrl) {
            this.queryId = 0;
            this.webViewContainer.loadUrl(currentAccount2, ((TLRPC.TL_simpleWebViewResultUrl) response).url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        }
    }

    /* renamed from: lambda$requestWebView$18$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m631x378d2f4c(int currentAccount2, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda1(this, response, currentAccount2));
    }

    /* renamed from: lambda$requestWebView$17$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m630x74a0c5ed(TLObject response, int currentAccount2) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(currentAccount2, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    /* access modifiers changed from: private */
    public int getColor(String key) {
        Integer color;
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        if (resourcesProvider2 != null) {
            color = resourcesProvider2.getColor(key);
        } else {
            color = Integer.valueOf(Theme.getColor(key));
        }
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void show() {
        this.frameLayout.setAlpha(0.0f);
        this.frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetY((float) BotWebViewSheet.this.swipeContainer.getHeight());
                BotWebViewSheet.this.frameLayout.setAlpha(1.0f);
                new SpringAnimation(BotWebViewSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).start();
            }
        });
        super.show();
    }

    public void onBackPressed() {
        if (!this.webViewContainer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void dismiss() {
        dismiss((Runnable) null);
    }

    public void dismiss(Runnable callback) {
        if (!this.dismissed) {
            this.dismissed = true;
            AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
            this.webViewContainer.destroyWebView();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((float) (webViewSwipeContainer.getHeight() + this.frameLayout.measureKeyboardHeight()), new BotWebViewSheet$$ExternalSyntheticLambda18(this, callback));
        }
    }

    /* renamed from: lambda$dismiss$19$org-telegram-ui-Components-BotWebViewSheet  reason: not valid java name */
    public /* synthetic */ void m615lambda$dismiss$19$orgtelegramuiComponentsBotWebViewSheet(Runnable callback) {
        super.dismiss();
        if (callback != null) {
            callback.run();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            if (this.queryId == args[0].longValue()) {
                dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            this.frameLayout.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor("windowBackgroundWhite"));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }
}
