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
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
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
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;

public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda19.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda20.INSTANCE).setMultiplier(100.0f);
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
    public boolean needCloseConfirmation;
    /* access modifiers changed from: private */
    public boolean overrideBackgroundColor;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private long peerId;
    private Runnable pollRunnable = new BotWebViewSheet$$ExternalSyntheticLambda5(this);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new BotWebViewSheet$$ExternalSyntheticLambda14(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda12(this, tLRPC$TL_error));
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

    public BotWebViewSheet(final Context context, final Theme.ResourcesProvider resourcesProvider2) {
        super(context, NUM);
        this.resourcesProvider = resourcesProvider2;
        this.swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:23:0x0081  */
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
                    if (r2 == 0) goto L_0x003f
                    org.telegram.ui.Components.BotWebViewSheet r2 = org.telegram.ui.Components.BotWebViewSheet.this
                    boolean r2 = r2.dismissed
                    if (r2 != 0) goto L_0x003f
                    org.telegram.ui.Components.BotWebViewSheet r2 = org.telegram.ui.Components.BotWebViewSheet.this
                    r3 = 1
                    boolean unused = r2.ignoreLayout = r3
                    r4.setOffsetY(r0)
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
                    boolean unused = r0.ignoreLayout = r1
                L_0x003f:
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    r2 = 1073741824(0x40000000, float:2.0)
                    if (r0 == 0) goto L_0x0066
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L_0x0066
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isSmallTablet()
                    if (r0 != 0) goto L_0x0066
                    android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r0 = r5.x
                    int r5 = r5.y
                    int r5 = java.lang.Math.min(r0, r5)
                    float r5 = (float) r5
                    r0 = 1061997773(0x3f4ccccd, float:0.8)
                    float r5 = r5 * r0
                    int r5 = (int) r5
                    int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r2)
                L_0x0066:
                    int r6 = android.view.View.MeasureSpec.getSize(r6)
                    int r0 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
                    int r6 = r6 - r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r6 = r6 - r0
                    r0 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r6 = r6 + r0
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
                    boolean r0 = r0.mainButtonWasVisible
                    if (r0 == 0) goto L_0x008d
                    org.telegram.ui.Components.BotWebViewSheet r0 = org.telegram.ui.Components.BotWebViewSheet.this
                    android.widget.TextView r0 = r0.mainButton
                    android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
                    int r1 = r0.height
                L_0x008d:
                    int r6 = r6 - r1
                    int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r2)
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
        botWebViewContainer.setDelegate(new BotWebViewContainer.Delegate() {
            private boolean sentWebViewData;

            public /* synthetic */ void onWebAppReady() {
                BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
            }

            public void onCloseRequested(Runnable runnable) {
                BotWebViewSheet.this.dismiss(runnable);
            }

            public void onWebAppSetupClosingBehavior(boolean z) {
                boolean unused = BotWebViewSheet.this.needCloseConfirmation = z;
            }

            public void onSendWebViewData(String str) {
                if (BotWebViewSheet.this.queryId == 0 && !this.sentWebViewData) {
                    this.sentWebViewData = true;
                    TLRPC$TL_messages_sendWebViewData tLRPC$TL_messages_sendWebViewData = new TLRPC$TL_messages_sendWebViewData();
                    tLRPC$TL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
                    tLRPC$TL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
                    tLRPC$TL_messages_sendWebViewData.button_text = BotWebViewSheet.this.buttonText;
                    tLRPC$TL_messages_sendWebViewData.data = str;
                    ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(tLRPC$TL_messages_sendWebViewData, new BotWebViewSheet$2$$ExternalSyntheticLambda3(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSendWebViewData$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject instanceof TLRPC$TL_updates) {
                    MessagesController.getInstance(BotWebViewSheet.this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
                }
                AndroidUtilities.runOnUIThread(new BotWebViewSheet$2$$ExternalSyntheticLambda2(BotWebViewSheet.this));
            }

            public void onWebAppSetActionBarColor(String str) {
                int access$900 = BotWebViewSheet.this.actionBarColor;
                int access$1000 = BotWebViewSheet.this.getColor(str);
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(200);
                duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                duration.addUpdateListener(new BotWebViewSheet$2$$ExternalSyntheticLambda0(this, access$900, access$1000));
                duration.start();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onWebAppSetActionBarColor$1(int i, int i2, ValueAnimator valueAnimator) {
                int unused = BotWebViewSheet.this.actionBarColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                BotWebViewSheet.this.frameLayout.invalidate();
            }

            public void onWebAppSetBackgroundColor(int i) {
                boolean unused = BotWebViewSheet.this.overrideBackgroundColor = true;
                int color = BotWebViewSheet.this.backgroundPaint.getColor();
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(200);
                duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                duration.addUpdateListener(new BotWebViewSheet$2$$ExternalSyntheticLambda1(this, color, i));
                duration.start();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onWebAppSetBackgroundColor$2(int i, int i2, ValueAnimator valueAnimator) {
                BotWebViewSheet.this.backgroundPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
                BotWebViewSheet.this.frameLayout.invalidate();
            }

            public void onSetBackButtonVisible(boolean z) {
                AndroidUtilities.updateImageViewImageAnimated(BotWebViewSheet.this.actionBar.getBackButton(), z ? NUM : NUM);
            }

            public void onWebAppOpenInvoice(String str, TLObject tLObject) {
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
                    OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(context, resourcesProvider2);
                    overlayActionBarLayoutDialog.show();
                    paymentFormActivity.setPaymentFormCallback(new BotWebViewSheet$2$$ExternalSyntheticLambda4(this, overlayActionBarLayoutDialog, str));
                    paymentFormActivity.setResourcesProvider(resourcesProvider2);
                    overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onWebAppOpenInvoice$3(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
                if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                    overlayActionBarLayoutDialog.dismiss();
                }
                BotWebViewSheet.this.webViewContainer.onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
            }

            public void onWebAppExpand() {
                if (!BotWebViewSheet.this.swipeContainer.isSwipeInProgress()) {
                    BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
                }
            }

            public void onSetupMainButton(final boolean z, boolean z2, String str, int i, int i2, final boolean z3) {
                BotWebViewSheet.this.mainButton.setClickable(z2);
                BotWebViewSheet.this.mainButton.setText(str);
                BotWebViewSheet.this.mainButton.setTextColor(i2);
                BotWebViewSheet.this.mainButton.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
                float f = 1.0f;
                float f2 = 0.0f;
                if (z != BotWebViewSheet.this.mainButtonWasVisible) {
                    boolean unused = BotWebViewSheet.this.mainButtonWasVisible = z;
                    BotWebViewSheet.this.mainButton.animate().cancel();
                    if (z) {
                        BotWebViewSheet.this.mainButton.setAlpha(0.0f);
                        BotWebViewSheet.this.mainButton.setVisibility(0);
                    }
                    BotWebViewSheet.this.mainButton.animate().alpha(z ? 1.0f : 0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
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
                    boolean unused2 = BotWebViewSheet.this.mainButtonProgressWasVisible = z3;
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
                    scaleX.scaleY(f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (!z3) {
                                BotWebViewSheet.this.radialProgressView.setVisibility(8);
                            }
                        }
                    }).start();
                }
            }
        });
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(NUM);
        this.actionBarColor = getColor("windowBackgroundWhite");
        AnonymousClass3 r1 = new SizeNotifierFrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (!BotWebViewSheet.this.overrideBackgroundColor) {
                    BotWebViewSheet.this.backgroundPaint.setColor(BotWebViewSheet.this.getColor("windowBackgroundWhite"));
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                canvas.drawRect(rectF, BotWebViewSheet.this.dimPaint);
                BotWebViewSheet.this.actionBarPaint.setColor(ColorUtils.blendARGB(BotWebViewSheet.this.actionBarColor, BotWebViewSheet.this.getColor("windowBackgroundWhite"), BotWebViewSheet.this.actionBarTransitionProgress));
                float dp = (float) AndroidUtilities.dp(16.0f);
                float f = 1.0f;
                if (!AndroidUtilities.isTablet()) {
                    f = 1.0f - BotWebViewSheet.this.actionBarTransitionProgress;
                }
                float f2 = dp * f;
                rectF.set((float) BotWebViewSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress), (float) BotWebViewSheet.this.swipeContainer.getRight(), BotWebViewSheet.this.swipeContainer.getTranslationY() + ((float) AndroidUtilities.dp(24.0f)) + f2);
                canvas.drawRoundRect(rectF, f2, f2, BotWebViewSheet.this.actionBarPaint);
                rectF.set((float) BotWebViewSheet.this.swipeContainer.getLeft(), BotWebViewSheet.this.swipeContainer.getTranslationY() + ((float) AndroidUtilities.dp(24.0f)), (float) BotWebViewSheet.this.swipeContainer.getRight(), (float) getHeight());
                canvas.drawRect(rectF, BotWebViewSheet.this.backgroundPaint);
            }

            public void draw(Canvas canvas) {
                float f;
                super.draw(canvas);
                float access$2100 = AndroidUtilities.isTablet() ? 0.0f : BotWebViewSheet.this.actionBarTransitionProgress;
                BotWebViewSheet.this.linePaint.setColor(Theme.getColor("key_sheet_scrollUp"));
                BotWebViewSheet.this.linePaint.setAlpha((int) (((float) BotWebViewSheet.this.linePaint.getAlpha()) * (1.0f - (Math.min(0.5f, access$2100) / 0.5f))));
                canvas.save();
                float f2 = 1.0f - access$2100;
                if (AndroidUtilities.isTablet()) {
                    f = AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + ((float) AndroidUtilities.dp(12.0f)), ((float) AndroidUtilities.statusBarHeight) / 2.0f, BotWebViewSheet.this.actionBarTransitionProgress);
                } else {
                    f = AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), ((float) AndroidUtilities.statusBarHeight) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f), access$2100) + ((float) AndroidUtilities.dp(12.0f));
                }
                float f3 = f;
                canvas.scale(f2, f2, ((float) getWidth()) / 2.0f, f3);
                canvas.drawLine((((float) getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(16.0f)), f3, (((float) getWidth()) / 2.0f) + ((float) AndroidUtilities.dp(16.0f)), f3, BotWebViewSheet.this.linePaint);
                canvas.restore();
                BotWebViewSheet.this.actionBarShadow.setAlpha((int) (BotWebViewSheet.this.actionBar.getAlpha() * 255.0f));
                float y = BotWebViewSheet.this.actionBar.getY() + BotWebViewSheet.this.actionBar.getTranslationY() + ((float) BotWebViewSheet.this.actionBar.getHeight());
                BotWebViewSheet.this.actionBarShadow.setBounds(0, (int) y, getWidth(), (int) (y + ((float) BotWebViewSheet.this.actionBarShadow.getIntrinsicHeight())));
                BotWebViewSheet.this.actionBarShadow.draw(canvas);
            }

            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || (motionEvent.getY() > AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + ((float) AndroidUtilities.dp(24.0f)), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress) && motionEvent.getX() <= ((float) BotWebViewSheet.this.swipeContainer.getRight()) && motionEvent.getX() >= ((float) BotWebViewSheet.this.swipeContainer.getLeft()))) {
                    return super.onTouchEvent(motionEvent);
                }
                BotWebViewSheet.this.onCheckDismissByUser();
                return true;
            }
        };
        this.frameLayout = r1;
        r1.setDelegate(new BotWebViewSheet$$ExternalSyntheticLambda21(this));
        this.frameLayout.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 24.0f, 0.0f, 0.0f));
        AnonymousClass4 r12 = new TextView(this, context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (((float) Math.min(point.x, point.y)) * 0.8f), NUM);
                }
                super.onMeasure(i, i2);
            }
        };
        this.mainButton = r12;
        r12.setVisibility(8);
        this.mainButton.setAlpha(0.0f);
        this.mainButton.setSingleLine();
        this.mainButton.setGravity(17);
        this.mainButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int dp = AndroidUtilities.dp(16.0f);
        this.mainButton.setPadding(dp, 0, dp, 0);
        this.mainButton.setTextSize(1, 14.0f);
        this.mainButton.setOnClickListener(new BotWebViewSheet$$ExternalSyntheticLambda3(this));
        this.frameLayout.addView(this.mainButton, LayoutHelper.createFrame(-1, 48, 81));
        this.mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(this.mainButton);
        AnonymousClass5 r13 = new RadialProgressView(this, context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (!AndroidUtilities.isTablet() || AndroidUtilities.isInMultiwindow || AndroidUtilities.isSmallTablet()) {
                    marginLayoutParams.rightMargin = AndroidUtilities.dp(10.0f);
                    return;
                }
                Point point = AndroidUtilities.displaySize;
                marginLayoutParams.rightMargin = (int) (((float) AndroidUtilities.dp(10.0f)) + (((float) Math.min(point.x, point.y)) * 0.1f));
            }
        };
        this.radialProgressView = r13;
        r13.setSize(AndroidUtilities.dp(18.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(8);
        this.frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        this.radialProgressAutoAnimator = VerticalPositionAutoAnimator.attach(this.radialProgressView);
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), NUM).mutate();
        AnonymousClass6 r14 = new ActionBar(this, context, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (((float) Math.min(point.x, point.y)) * 0.8f), NUM);
                }
                super.onMeasure(i, i2);
            }
        };
        this.actionBar = r14;
        r14.setBackgroundColor(0);
        this.actionBar.setBackButtonImage(NUM);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    BotWebViewSheet.this.onCheckDismissByUser();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        this.frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 49));
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.frameLayout;
        AnonymousClass8 r15 = new ChatAttachAlertBotWebViewLayout.WebProgressView(this, context, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (((float) Math.min(point.x, point.y)) * 0.8f), NUM);
                }
                super.onMeasure(i, i2);
            }
        };
        this.progressView = r15;
        sizeNotifierFrameLayout.addView(r15, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new BotWebViewSheet$$ExternalSyntheticLambda4(this));
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new BotWebViewSheet$$ExternalSyntheticLambda7(this));
        this.swipeContainer.setScrollEndListener(new BotWebViewSheet$$ExternalSyntheticLambda6(this));
        this.swipeContainer.setDelegate(new BotWebViewSheet$$ExternalSyntheticLambda18(this));
        this.swipeContainer.setTopActionBarOffsetY((float) ((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f)));
        this.swipeContainer.setIsKeyboardVisible(new BotWebViewSheet$$ExternalSyntheticLambda13(this));
        setContentView(this.frameLayout, new ViewGroup.LayoutParams(-1, -1));
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
    public /* synthetic */ void lambda$new$8(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(200);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new BotWebViewSheet$$ExternalSyntheticLambda0(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / ((float) this.swipeContainer.getHeight()), 0.0f, 1.0f)) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.frameLayout.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
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
        System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11() {
        if (!onCheckDismissByUser()) {
            this.swipeContainer.stickTo(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$new$12(Void voidR) {
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
        int color = Theme.getColor("windowBackgroundWhite", (boolean[]) null, true);
        if (AndroidUtilities.isTablet() || ColorUtils.calculateLuminance(color) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
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
            this.frameLayout.setOnApplyWindowInsetsListener(BotWebViewSheet$$ExternalSyntheticLambda2.INSTANCE);
        }
        if (i >= 26) {
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
        ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
        addItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        addItem.addSubItem(NUM, NUM, LocaleController.getString(NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (!BotWebViewSheet.this.webViewContainer.onBackPressed()) {
                        BotWebViewSheet.this.onCheckDismissByUser();
                    }
                } else if (i == NUM) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j2);
                    if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) BotWebViewSheet.this.parentActivity).lambda$runLinkRequest$61(new ChatActivity(bundle));
                    }
                    BotWebViewSheet.this.dismiss();
                } else if (i == NUM) {
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
                } else if (i == NUM) {
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
            FileLog.e((Throwable) e);
            str3 = null;
        }
        this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
        this.webViewContainer.loadFlickerAndSettingsItem(i, j2, this.settingsItem);
        if (i2 == 0) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
            tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
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
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new BotWebViewSheet$$ExternalSyntheticLambda15(this, i));
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
        } else if (i2 == 1) {
            TLRPC$TL_messages_requestSimpleWebView tLRPC$TL_messages_requestSimpleWebView = new TLRPC$TL_messages_requestSimpleWebView();
            tLRPC$TL_messages_requestSimpleWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
            if (z2) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON2 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestSimpleWebView.theme_params = tLRPC$TL_dataJSON2;
                tLRPC$TL_dataJSON2.data = str3;
                tLRPC$TL_messages_requestSimpleWebView.flags |= 1;
            }
            tLRPC$TL_messages_requestSimpleWebView.url = str2;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestSimpleWebView, new BotWebViewSheet$$ExternalSyntheticLambda17(this, i));
        } else if (i2 == 2) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView2 = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView2.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestWebView2.peer = MessagesController.getInstance(i).getInputPeer(j2);
            tLRPC$TL_messages_requestWebView2.url = str2;
            tLRPC$TL_messages_requestWebView2.flags |= 2;
            if (z2) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON3 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView2.theme_params = tLRPC$TL_dataJSON3;
                tLRPC$TL_dataJSON3.data = str3;
                tLRPC$TL_messages_requestWebView2.flags |= 4;
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView2, new BotWebViewSheet$$ExternalSyntheticLambda16(this, i));
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$15(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda9(this, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$14(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$17(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda11(this, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$16(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_simpleWebViewResultUrl) {
            this.queryId = 0;
            this.webViewContainer.loadUrl(i, ((TLRPC$TL_simpleWebViewResultUrl) tLObject).url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$19(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewSheet$$ExternalSyntheticLambda10(this, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWebView$18(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000);
        }
    }

    /* access modifiers changed from: private */
    public int getColor(String str) {
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

    public void onBackPressed() {
        if (!this.webViewContainer.onBackPressed()) {
            onCheckDismissByUser();
        }
    }

    public void dismiss() {
        dismiss((Runnable) null);
    }

    public boolean onCheckDismissByUser() {
        if (this.needCloseConfirmation) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
            AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(NUM)).setPositiveButton(LocaleController.getString(NUM), new BotWebViewSheet$$ExternalSyntheticLambda1(this)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).create();
            create.show();
            ((TextView) create.getButton(-1)).setTextColor(getColor("dialogTextRed"));
            return false;
        }
        dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckDismissByUser$20(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    public void dismiss(Runnable runnable) {
        if (!this.dismissed) {
            this.dismissed = true;
            AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
            this.webViewContainer.destroyWebView();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((float) (webViewSwipeContainer.getHeight() + this.frameLayout.measureKeyboardHeight()), new BotWebViewSheet$$ExternalSyntheticLambda8(this, runnable));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$21(Runnable runnable) {
        super.dismiss();
        if (runnable != null) {
            runnable.run();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == objArr[0].longValue()) {
                dismiss();
            }
        } else if (i == NotificationCenter.didSetNewTheme) {
            this.frameLayout.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor("windowBackgroundWhite"));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }
}
