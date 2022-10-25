package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_messages_sendWebViewData;
import org.telegram.tgnet.TLRPC$TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC$TL_simpleWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.BotWebViewSheet;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
/* loaded from: classes3.dex */
public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda19.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda20.INSTANCE).setMultiplier(100.0f);
    private ActionBar actionBar;
    private int actionBarColor;
    private Paint actionBarPaint;
    private Drawable actionBarShadow;
    private float actionBarTransitionProgress;
    private Paint backgroundPaint;
    private long botId;
    private String buttonText;
    private int currentAccount;
    private Paint dimPaint;
    private boolean dismissed;
    private SizeNotifierFrameLayout frameLayout;
    private boolean ignoreLayout;
    private Paint linePaint;
    private TextView mainButton;
    private VerticalPositionAutoAnimator mainButtonAutoAnimator;
    private boolean mainButtonProgressWasVisible;
    private boolean mainButtonWasVisible;
    private boolean needCloseConfirmation;
    private boolean overrideBackgroundColor;
    private Activity parentActivity;
    private long peerId;
    private Runnable pollRunnable;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private VerticalPositionAutoAnimator radialProgressAutoAnimator;
    private RadialProgressView radialProgressView;
    private int replyToMsgId;
    private Theme.ResourcesProvider resourcesProvider;
    private ActionBarMenuSubItem settingsItem;
    private boolean silent;
    private SpringAnimation springAnimation;
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(BotWebViewSheet botWebViewSheet, float f) {
        botWebViewSheet.actionBarTransitionProgress = f;
        botWebViewSheet.frameLayout.invalidate();
        botWebViewSheet.actionBar.setAlpha(f);
        botWebViewSheet.updateLightStatusBar();
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda14
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$new$3(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$new$2(tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(TLRPC$TL_error tLRPC$TL_error) {
        if (this.dismissed) {
            return;
        }
        if (tLRPC$TL_error != null) {
            dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public BotWebViewSheet(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.actionBarTransitionProgress = 0.0f;
        this.linePaint = new Paint(1);
        this.dimPaint = new Paint();
        this.backgroundPaint = new Paint(1);
        this.actionBarPaint = new Paint(1);
        this.pollRunnable = new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$new$4();
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) { // from class: org.telegram.ui.Components.BotWebViewSheet.1
            /* JADX WARN: Removed duplicated region for block: B:10:0x001f  */
            /* JADX WARN: Removed duplicated region for block: B:25:0x0081  */
            @Override // android.widget.FrameLayout, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected void onMeasure(int r5, int r6) {
                /*
                    r4 = this;
                    int r0 = android.view.View.MeasureSpec.getSize(r6)
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r1 != 0) goto L18
                    android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r2 = r1.x
                    int r1 = r1.y
                    if (r2 <= r1) goto L18
                    float r0 = (float) r0
                    r1 = 1080033280(0x40600000, float:3.5)
                    float r0 = r0 / r1
                    int r0 = (int) r0
                    goto L1c
                L18:
                    int r0 = r0 / 5
                    int r0 = r0 * 2
                L1c:
                    r1 = 0
                    if (r0 >= 0) goto L20
                    r0 = 0
                L20:
                    float r2 = r4.getOffsetY()
                    float r0 = (float) r0
                    int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                    if (r2 == 0) goto L3f
                    org.telegram.ui.Components.BotWebViewSheet r2 = org.telegram.ui.Components.BotWebViewSheet.this
                    boolean r2 = org.telegram.ui.Components.BotWebViewSheet.access$000(r2)
                    if (r2 != 0) goto L3f
                    org.telegram.ui.Components.BotWebViewSheet r2 = org.telegram.ui.Components.BotWebViewSheet.this
                    r3 = 1
                    org.telegram.ui.Components.BotWebViewSheet.access$102(r2, r3)
                    r4.setOffsetY(r0)
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
                    org.telegram.ui.Components.BotWebViewSheet.access$102(r0, r1)
                L3f:
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    r2 = 1073741824(0x40000000, float:2.0)
                    if (r0 == 0) goto L66
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L66
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isSmallTablet()
                    if (r0 != 0) goto L66
                    android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r0 = r5.x
                    int r5 = r5.y
                    int r5 = java.lang.Math.min(r0, r5)
                    float r5 = (float) r5
                    r0 = 1061997773(0x3f4ccccd, float:0.8)
                    float r5 = r5 * r0
                    int r5 = (int) r5
                    int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r2)
                L66:
                    int r6 = android.view.View.MeasureSpec.getSize(r6)
                    int r0 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
                    int r6 = r6 - r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r6 = r6 - r0
                    r0 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r6 = r6 + r0
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
                    boolean r0 = org.telegram.ui.Components.BotWebViewSheet.access$200(r0)
                    if (r0 == 0) goto L8d
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
                    android.widget.TextView r0 = org.telegram.ui.Components.BotWebViewSheet.access$300(r0)
                    android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
                    int r1 = r0.height
                L8d:
                    int r6 = r6 - r1
                    int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r2)
                    super.onMeasure(r5, r6)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewSheet.AnonymousClass1.onMeasure(int, int):void");
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (BotWebViewSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(new AnonymousClass2(context, resourcesProvider));
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(NUM);
        this.actionBarColor = getColor("windowBackgroundWhite");
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.Components.BotWebViewSheet.3
            {
                setWillNotDraw(false);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (!BotWebViewSheet.this.overrideBackgroundColor) {
                    BotWebViewSheet.this.backgroundPaint.setColor(BotWebViewSheet.this.getColor("windowBackgroundWhite"));
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                canvas.drawRect(rectF, BotWebViewSheet.this.dimPaint);
                BotWebViewSheet.this.actionBarPaint.setColor(ColorUtils.blendARGB(BotWebViewSheet.this.actionBarColor, BotWebViewSheet.this.getColor("windowBackgroundWhite"), BotWebViewSheet.this.actionBarTransitionProgress));
                float dp = AndroidUtilities.dp(16.0f);
                float f = 1.0f;
                if (!AndroidUtilities.isTablet()) {
                    f = 1.0f - BotWebViewSheet.this.actionBarTransitionProgress;
                }
                float f2 = dp * f;
                rectF.set(BotWebViewSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress), BotWebViewSheet.this.swipeContainer.getRight(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + f2);
                canvas.drawRoundRect(rectF, f2, f2, BotWebViewSheet.this.actionBarPaint);
                rectF.set(BotWebViewSheet.this.swipeContainer.getLeft(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewSheet.this.swipeContainer.getRight(), getHeight());
                canvas.drawRect(rectF, BotWebViewSheet.this.backgroundPaint);
            }

            @Override // android.view.View
            public void draw(Canvas canvas) {
                super.draw(canvas);
                float f = AndroidUtilities.isTablet() ? 0.0f : BotWebViewSheet.this.actionBarTransitionProgress;
                BotWebViewSheet.this.linePaint.setColor(Theme.getColor("key_sheet_scrollUp"));
                BotWebViewSheet.this.linePaint.setAlpha((int) (BotWebViewSheet.this.linePaint.getAlpha() * (1.0f - (Math.min(0.5f, f) / 0.5f))));
                canvas.save();
                float f2 = 1.0f - f;
                float lerp = AndroidUtilities.isTablet() ? AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(12.0f), AndroidUtilities.statusBarHeight / 2.0f, BotWebViewSheet.this.actionBarTransitionProgress) : AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), f) + AndroidUtilities.dp(12.0f);
                canvas.scale(f2, f2, getWidth() / 2.0f, lerp);
                canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), lerp, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), lerp, BotWebViewSheet.this.linePaint);
                canvas.restore();
                BotWebViewSheet.this.actionBarShadow.setAlpha((int) (BotWebViewSheet.this.actionBar.getAlpha() * 255.0f));
                float y = BotWebViewSheet.this.actionBar.getY() + BotWebViewSheet.this.actionBar.getTranslationY() + BotWebViewSheet.this.actionBar.getHeight();
                BotWebViewSheet.this.actionBarShadow.setBounds(0, (int) y, getWidth(), (int) (y + BotWebViewSheet.this.actionBarShadow.getIntrinsicHeight()));
                BotWebViewSheet.this.actionBarShadow.draw(canvas);
            }

            @Override // android.view.View
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && (motionEvent.getY() <= AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress) || motionEvent.getX() > BotWebViewSheet.this.swipeContainer.getRight() || motionEvent.getX() < BotWebViewSheet.this.swipeContainer.getLeft())) {
                    BotWebViewSheet.this.onCheckDismissByUser();
                    return true;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.frameLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda21
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
            public final void onSizeChanged(int i, boolean z) {
                BotWebViewSheet.this.lambda$new$5(i, z);
            }
        });
        this.frameLayout.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 24.0f, 0.0f, 0.0f));
        TextView textView = new TextView(this, context) { // from class: org.telegram.ui.Components.BotWebViewSheet.4
            @Override // android.widget.TextView, android.view.View
            protected void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), NUM);
                }
                super.onMeasure(i, i2);
            }
        };
        this.mainButton = textView;
        textView.setVisibility(8);
        this.mainButton.setAlpha(0.0f);
        this.mainButton.setSingleLine();
        this.mainButton.setGravity(17);
        this.mainButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int dp = AndroidUtilities.dp(16.0f);
        this.mainButton.setPadding(dp, 0, dp, 0);
        this.mainButton.setTextSize(1, 14.0f);
        this.mainButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BotWebViewSheet.this.lambda$new$6(view);
            }
        });
        this.frameLayout.addView(this.mainButton, LayoutHelper.createFrame(-1, 48, 81));
        this.mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(this.mainButton);
        RadialProgressView radialProgressView = new RadialProgressView(this, context) { // from class: org.telegram.ui.Components.BotWebViewSheet.5
            @Override // android.view.View
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    marginLayoutParams.rightMargin = (int) (AndroidUtilities.dp(10.0f) + (Math.min(point.x, point.y) * 0.1f));
                    return;
                }
                marginLayoutParams.rightMargin = AndroidUtilities.dp(10.0f);
            }
        };
        this.radialProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(8);
        this.frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        this.radialProgressAutoAnimator = VerticalPositionAutoAnimator.attach(this.radialProgressView);
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();
        ActionBar actionBar = new ActionBar(this, context, resourcesProvider) { // from class: org.telegram.ui.Components.BotWebViewSheet.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.ActionBar.ActionBar, android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), NUM);
                }
                super.onMeasure(i, i2);
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(0);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.BotWebViewSheet.7
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    BotWebViewSheet.this.onCheckDismissByUser();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        this.frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 49));
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.frameLayout;
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(this, context, resourcesProvider) { // from class: org.telegram.ui.Components.BotWebViewSheet.8
            @Override // android.view.View
            protected void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), NUM);
                }
                super.onMeasure(i, i2);
            }
        };
        this.progressView = webProgressView;
        sizeNotifierFrameLayout2.addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda4
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                BotWebViewSheet.this.lambda$new$8((Float) obj);
            }
        });
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$new$9();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$new$10();
            }
        });
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda18
            @Override // org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate
            public final void onDismiss() {
                BotWebViewSheet.this.lambda$new$11();
            }
        });
        this.swipeContainer.setTopActionBarOffsetY((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f));
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda13
            @Override // org.telegram.messenger.GenericProvider
            public final Object provide(Object obj) {
                Boolean lambda$new$12;
                lambda$new$12 = BotWebViewSheet.this.lambda$new$12((Void) obj);
                return lambda$new$12;
            }
        });
        setContentView(this.frameLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.BotWebViewSheet$2  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 implements BotWebViewContainer.Delegate {
        private boolean sentWebViewData;
        final /* synthetic */ Context val$context;
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass2(Context context, Theme.ResourcesProvider resourcesProvider) {
            this.val$context = context;
            this.val$resourcesProvider = resourcesProvider;
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onCloseRequested(Runnable runnable) {
            BotWebViewSheet.this.dismiss(runnable);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetupClosingBehavior(boolean z) {
            BotWebViewSheet.this.needCloseConfirmation = z;
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSendWebViewData(String str) {
            if (BotWebViewSheet.this.queryId != 0 || this.sentWebViewData) {
                return;
            }
            this.sentWebViewData = true;
            TLRPC$TL_messages_sendWebViewData tLRPC$TL_messages_sendWebViewData = new TLRPC$TL_messages_sendWebViewData();
            tLRPC$TL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
            tLRPC$TL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
            tLRPC$TL_messages_sendWebViewData.button_text = BotWebViewSheet.this.buttonText;
            tLRPC$TL_messages_sendWebViewData.data = str;
            ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(tLRPC$TL_messages_sendWebViewData, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.AnonymousClass2.this.lambda$onSendWebViewData$0(tLObject, tLRPC$TL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSendWebViewData$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$TL_updates) {
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
            }
            final BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BotWebViewSheet.this.dismiss();
                }
            });
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetActionBarColor(String str) {
            final int i = BotWebViewSheet.this.actionBarColor;
            final int color = BotWebViewSheet.this.getColor(str);
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppSetActionBarColor$1(i, color, valueAnimator);
                }
            });
            duration.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWebAppSetActionBarColor$1(int i, int i2, ValueAnimator valueAnimator) {
            BotWebViewSheet.this.actionBarColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            BotWebViewSheet.this.frameLayout.invalidate();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetBackgroundColor(final int i) {
            BotWebViewSheet.this.overrideBackgroundColor = true;
            final int color = BotWebViewSheet.this.backgroundPaint.getColor();
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppSetBackgroundColor$2(color, i, valueAnimator);
                }
            });
            duration.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWebAppSetBackgroundColor$2(int i, int i2, ValueAnimator valueAnimator) {
            BotWebViewSheet.this.backgroundPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
            BotWebViewSheet.this.frameLayout.invalidate();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetBackButtonVisible(boolean z) {
            AndroidUtilities.updateImageViewImageAnimated(BotWebViewSheet.this.actionBar.getBackButton(), z ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppOpenInvoice(final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
            if (tLObject instanceof TLRPC$TL_payments_paymentForm) {
                TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = (TLRPC$TL_payments_paymentForm) tLObject;
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).putUsers(tLRPC$TL_payments_paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm, str, lastFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC$TL_payments_paymentReceipt ? new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
                AndroidUtilities.hideKeyboard(BotWebViewSheet.this.frameLayout);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(this.val$context, this.val$resourcesProvider);
                overlayActionBarLayoutDialog.show();
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda4
                    @Override // org.telegram.ui.PaymentFormActivity.PaymentFormCallback
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppOpenInvoice$3(overlayActionBarLayoutDialog, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(this.val$resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWebAppOpenInvoice$3(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            BotWebViewSheet.this.webViewContainer.onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppExpand() {
            if (BotWebViewSheet.this.swipeContainer.isSwipeInProgress()) {
                return;
            }
            BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetupMainButton(final boolean z, boolean z2, String str, int i, int i2, final boolean z3) {
            BotWebViewSheet.this.mainButton.setClickable(z2);
            BotWebViewSheet.this.mainButton.setText(str);
            BotWebViewSheet.this.mainButton.setTextColor(i2);
            BotWebViewSheet.this.mainButton.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
            float f = 1.0f;
            float f2 = 0.0f;
            if (z != BotWebViewSheet.this.mainButtonWasVisible) {
                BotWebViewSheet.this.mainButtonWasVisible = z;
                BotWebViewSheet.this.mainButton.animate().cancel();
                if (z) {
                    BotWebViewSheet.this.mainButton.setAlpha(0.0f);
                    BotWebViewSheet.this.mainButton.setVisibility(0);
                }
                BotWebViewSheet.this.mainButton.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewSheet.2.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            BotWebViewSheet.this.mainButton.setVisibility(8);
                        }
                        BotWebViewSheet.this.swipeContainer.requestLayout();
                    }
                }).start();
            }
            BotWebViewSheet.this.radialProgressView.setProgressColor(i2);
            if (z3 != BotWebViewSheet.this.mainButtonProgressWasVisible) {
                BotWebViewSheet.this.mainButtonProgressWasVisible = z3;
                BotWebViewSheet.this.radialProgressView.animate().cancel();
                if (z3) {
                    BotWebViewSheet.this.radialProgressView.setAlpha(0.0f);
                    BotWebViewSheet.this.radialProgressView.setVisibility(0);
                }
                ViewPropertyAnimator animate = BotWebViewSheet.this.radialProgressView.animate();
                if (z3) {
                    f2 = 1.0f;
                }
                ViewPropertyAnimator scaleX = animate.alpha(f2).scaleX(z3 ? 1.0f : 0.1f);
                if (!z3) {
                    f = 0.1f;
                }
                scaleX.scaleY(f).setDuration(250L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewSheet.2.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (!z3) {
                            BotWebViewSheet.this.radialProgressView.setVisibility(8);
                        }
                    }
                }).start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(int i, boolean z) {
        if (i > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view) {
        this.webViewContainer.onMainButtonPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.this.lambda$new$7(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewSheet.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / this.swipeContainer.getHeight(), 0.0f, 1.0f)) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.frameLayout.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float f = (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        float max = Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        this.mainButtonAutoAnimator.setOffsetY(max);
        this.radialProgressAutoAnimator.setOffsetY(max);
        System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11() {
        if (!onCheckDismissByUser()) {
            this.swipeContainer.stickTo(0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$new$12(Void r2) {
        return Boolean.valueOf(this.frameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
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
        boolean z = true;
        int color = Theme.getColor("windowBackgroundWhite", null, true);
        if (AndroidUtilities.isTablet() || ColorUtils.calculateLuminance(color) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
            z = false;
        }
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != z) {
            this.wasLightStatusBar = Boolean.valueOf(z);
            if (Build.VERSION.SDK_INT < 23) {
                return;
            }
            int systemUiVisibility = this.frameLayout.getSystemUiVisibility();
            this.frameLayout.setSystemUiVisibility(z ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
        }
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            window.addFlags(-NUM);
        } else if (i >= 21) {
            window.addFlags(-NUM);
        }
        window.setWindowAnimations(R.style.DialogNoAnimation);
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
            this.frameLayout.setOnApplyWindowInsetsListener(BotWebViewSheet$$ExternalSyntheticLambda2.INSTANCE);
        }
        if (i >= 26) {
            if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", null, true)) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ WindowInsets lambda$onCreate$13(View view, WindowInsets windowInsets) {
        view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
        return windowInsets;
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation = this.springAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.springAnimation = null;
        }
    }

    public void requestWebView(final int i, long j, final long j2, String str, String str2, int i2, int i3, boolean z) {
        String str3;
        this.currentAccount = i;
        this.peerId = j;
        this.botId = j2;
        this.replyToMsgId = i3;
        this.silent = z;
        this.buttonText = str;
        this.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(i).getUser(Long.valueOf(j2))));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.removeAllViews();
        boolean z2 = false;
        ActionBarMenuItem addItem = createMenu.addItem(0, R.drawable.ic_ab_other);
        addItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString(R.string.BotWebViewOpenBot));
        addItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString(R.string.BotWebViewReloadPage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.BotWebViewSheet.10
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i4) {
                if (i4 == -1) {
                    if (BotWebViewSheet.this.webViewContainer.onBackPressed()) {
                        return;
                    }
                    BotWebViewSheet.this.onCheckDismissByUser();
                } else if (i4 == R.id.menu_open_bot) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j2);
                    if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) BotWebViewSheet.this.parentActivity).lambda$runLinkRequest$66(new ChatActivity(bundle));
                    }
                    BotWebViewSheet.this.dismiss();
                } else if (i4 == R.id.menu_reload_page) {
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
                } else if (i4 != R.id.menu_settings) {
                } else {
                    BotWebViewSheet.this.webViewContainer.onSettingsButtonPressed();
                }
            }
        });
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", getColor("windowBackgroundWhite"));
            jSONObject.put("secondary_bg_color", getColor("windowBackgroundGray"));
            jSONObject.put("text_color", getColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", getColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", getColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", getColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", getColor("featuredStickers_buttonText"));
            str3 = jSONObject.toString();
            z2 = true;
        } catch (Exception e) {
            FileLog.e(e);
            str3 = null;
        }
        this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
        this.webViewContainer.loadFlickerAndSettingsItem(i, j2, this.settingsItem);
        if (i2 == 0) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
            tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestWebView.platform = "android";
            if (str2 != null) {
                tLRPC$TL_messages_requestWebView.url = str2;
                tLRPC$TL_messages_requestWebView.flags |= 2;
            }
            if (i3 != 0) {
                tLRPC$TL_messages_requestWebView.reply_to_msg_id = i3;
                tLRPC$TL_messages_requestWebView.flags |= 1;
            }
            if (z2) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView.theme_params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = str3;
                tLRPC$TL_messages_requestWebView.flags |= 4;
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda15
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$19(i, tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
        } else if (i2 == 1) {
            TLRPC$TL_messages_requestSimpleWebView tLRPC$TL_messages_requestSimpleWebView = new TLRPC$TL_messages_requestSimpleWebView();
            tLRPC$TL_messages_requestSimpleWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestSimpleWebView.platform = "android";
            if (z2) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON2 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestSimpleWebView.theme_params = tLRPC$TL_dataJSON2;
                tLRPC$TL_dataJSON2.data = str3;
                tLRPC$TL_messages_requestSimpleWebView.flags |= 1;
            }
            tLRPC$TL_messages_requestSimpleWebView.url = str2;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestSimpleWebView, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda17
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$17(i, tLObject, tLRPC$TL_error);
                }
            });
        } else if (i2 != 2) {
        } else {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView2 = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView2.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestWebView2.peer = MessagesController.getInstance(i).getInputPeer(j2);
            tLRPC$TL_messages_requestWebView2.platform = "android";
            tLRPC$TL_messages_requestWebView2.url = str2;
            tLRPC$TL_messages_requestWebView2.flags |= 2;
            if (z2) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON3 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView2.theme_params = tLRPC$TL_dataJSON3;
                tLRPC$TL_dataJSON3.data = str3;
                tLRPC$TL_messages_requestWebView2.flags |= 4;
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView2, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda16
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$15(i, tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$15(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$14(tLObject, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$14(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$17(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$16(tLObject, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$16(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_simpleWebViewResultUrl) {
            this.queryId = 0L;
            this.webViewContainer.loadUrl(i, ((TLRPC$TL_simpleWebViewResultUrl) tLObject).url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$19(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$18(tLObject, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$18(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getColor(String str) {
        Integer valueOf;
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            valueOf = resourcesProvider.getColor(str);
        } else {
            valueOf = Integer.valueOf(Theme.getColor(str));
        }
        return valueOf != null ? valueOf.intValue() : Theme.getColor(str);
    }

    @Override // android.app.Dialog
    public void show() {
        this.frameLayout.setAlpha(0.0f);
        this.frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: org.telegram.ui.Components.BotWebViewSheet.11
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetY(BotWebViewSheet.this.swipeContainer.getHeight());
                BotWebViewSheet.this.frameLayout.setAlpha(1.0f);
                new SpringAnimation(BotWebViewSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).start();
            }
        });
        super.show();
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        if (this.webViewContainer.onBackPressed()) {
            return;
        }
        onCheckDismissByUser();
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        dismiss(null);
    }

    public boolean onCheckDismissByUser() {
        if (this.needCloseConfirmation) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
            AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved)).setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    BotWebViewSheet.this.lambda$onCheckDismissByUser$20(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
            create.show();
            ((TextView) create.getButton(-1)).setTextColor(getColor("dialogTextRed"));
            return false;
        }
        dismiss();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckDismissByUser$20(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    public void dismiss(final Runnable runnable) {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        this.webViewContainer.destroyWebView();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo(webViewSwipeContainer.getHeight() + this.frameLayout.measureKeyboardHeight(), new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.lambda$dismiss$21(runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$21(Runnable runnable) {
        super.dismiss();
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId != ((Long) objArr[0]).longValue()) {
                return;
            }
            dismiss();
        } else if (i != NotificationCenter.didSetNewTheme) {
        } else {
            this.frameLayout.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor("windowBackgroundWhite"));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }
}
