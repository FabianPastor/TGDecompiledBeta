package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class BotWebViewContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[]{"http", "https"});
    /* access modifiers changed from: private */
    public TLRPC$User botUser;
    private String buttonData;
    /* access modifiers changed from: private */
    public Delegate delegate;
    private CellFlickerDrawable flickerDrawable;
    /* access modifiers changed from: private */
    public BackupImageView flickerView;
    /* access modifiers changed from: private */
    public boolean isPageLoaded;
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mFilePathCallback;
    /* access modifiers changed from: private */
    public String mUrl;
    private VerticalPositionAutoAnimator mainAutoAnimator;
    /* access modifiers changed from: private */
    public TextView mainButton;
    /* access modifiers changed from: private */
    public boolean mainButtonProgressWasVisible = false;
    /* access modifiers changed from: private */
    public boolean mainButtonWasVisible = false;
    private Runnable onPermissionsRequestResultCallback;
    private VerticalPositionAutoAnimator progressAutoAnimator;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    private Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public WebView webView;
    /* access modifiers changed from: private */
    public Consumer<Float> webViewProgressListener;
    /* access modifiers changed from: private */
    public WebViewScrollListener webViewScrollListener;

    public interface Delegate {

        /* renamed from: org.telegram.ui.Components.BotWebViewContainer$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$hasIntegratedMainButton(Delegate delegate) {
                return false;
            }

            public static void $default$onSetupMainButton(Delegate delegate, boolean z, boolean z2, String str, int i, int i2, boolean z3) {
            }
        }

        boolean hasIntegratedMainButton();

        void onCloseRequested();

        void onSendWebViewData(String str);

        void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3);

        void onWebAppExpand();
    }

    public interface WebViewScrollListener {
        void onWebViewScrolled(WebView webView, int i, int i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$evaluateJs$4(String str) {
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public BotWebViewContainer(Context context, final Theme.ResourcesProvider resourcesProvider2, int i) {
        super(context);
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.flickerDrawable = cellFlickerDrawable;
        this.resourcesProvider = resourcesProvider2;
        cellFlickerDrawable.drawFrame = false;
        cellFlickerDrawable.setColors(i, 153, 204);
        BackupImageView backupImageView = new BackupImageView(context);
        this.flickerView = backupImageView;
        backupImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackgroundGray"), PorterDuff.Mode.SRC_IN));
        addView(this.flickerView, LayoutHelper.createFrame(64, 64, 17));
        AnonymousClass1 r10 = new WebView(context) {
            private int prevScrollX;
            private int prevScrollY;

            public boolean onCheckIsTextEditor() {
                return true;
            }

            /* access modifiers changed from: protected */
            public void onScrollChanged(int i, int i2, int i3, int i4) {
                super.onScrollChanged(i, i2, i3, i4);
                if (BotWebViewContainer.this.webViewScrollListener != null) {
                    BotWebViewContainer.this.webViewScrollListener.onWebViewScrolled(this, getScrollX() - this.prevScrollX, getScrollY() - this.prevScrollY);
                }
                this.prevScrollX = getScrollX();
                this.prevScrollY = getScrollY();
            }

            public void setScrollX(int i) {
                super.setScrollX(i);
                this.prevScrollX = i;
            }

            public void setScrollY(int i) {
                super.setScrollY(i);
                this.prevScrollY = i;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2) - (BotWebViewContainer.this.mainButtonWasVisible ? BotWebViewContainer.this.mainButton.getLayoutParams().height : 0), NUM));
            }
        };
        this.webView = r10;
        r10.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                Uri parse = Uri.parse(BotWebViewContainer.this.mUrl);
                Uri parse2 = Uri.parse(str);
                if (ObjectsCompat$$ExternalSyntheticBackport0.m(parse.getHost(), parse2.getHost()) && ObjectsCompat$$ExternalSyntheticBackport0.m(parse.getPath(), parse2.getPath())) {
                    return false;
                }
                if (!BotWebViewContainer.WHITELISTED_SCHEMES.contains(parse2.getScheme())) {
                    return true;
                }
                boolean isInternalUri = Browser.isInternalUri(parse2, new boolean[]{false});
                Browser.openUrl(BotWebViewContainer.this.getContext(), parse2, true, false);
                if (!isInternalUri || BotWebViewContainer.this.delegate == null) {
                    return true;
                }
                BotWebViewContainer.this.delegate.onCloseRequested();
                return true;
            }

            public void onPageFinished(WebView webView, String str) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(BotWebViewContainer.this.webView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(BotWebViewContainer.this.flickerView, View.ALPHA, new float[]{0.0f})});
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        BotWebViewContainer.this.flickerView.setVisibility(8);
                    }
                });
                animatorSet.start();
                boolean unused = BotWebViewContainer.this.isPageLoaded = true;
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {
            private Dialog lastPermissionsDialog;

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                Context context = BotWebViewContainer.this.getContext();
                if (!(context instanceof Activity)) {
                    return false;
                }
                Activity activity = (Activity) context;
                if (BotWebViewContainer.this.mFilePathCallback != null) {
                    BotWebViewContainer.this.mFilePathCallback.onReceiveValue((Object) null);
                }
                ValueCallback unused = BotWebViewContainer.this.mFilePathCallback = valueCallback;
                if (Build.VERSION.SDK_INT >= 21) {
                    activity.startActivityForResult(fileChooserParams.createIntent(), 3000);
                    return true;
                }
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("*/*");
                activity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString(NUM)), 3000);
                return true;
            }

            public void onProgressChanged(WebView webView, int i) {
                if (BotWebViewContainer.this.webViewProgressListener != null) {
                    BotWebViewContainer.this.webViewProgressListener.accept(Float.valueOf(((float) i) / 100.0f));
                }
            }

            public void onGeolocationPermissionsShowPrompt(String str, GeolocationPermissions.Callback callback) {
                Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.getContext(), resourcesProvider2, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$3$$ExternalSyntheticLambda3(this, callback, str));
                this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                createWebViewPermissionsRequestDialog.show();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$1(GeolocationPermissions.Callback callback, String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new BotWebViewContainer$3$$ExternalSyntheticLambda0(callback, str));
                        return;
                    }
                    callback.invoke(str, false, false);
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$0(GeolocationPermissions.Callback callback, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    callback.invoke(str, true, true);
                } else {
                    callback.invoke(str, false, false);
                }
            }

            public void onGeolocationPermissionsHidePrompt() {
                Dialog dialog = this.lastPermissionsDialog;
                if (dialog != null) {
                    dialog.dismiss();
                    this.lastPermissionsDialog = null;
                }
            }

            public void onPermissionRequest(PermissionRequest permissionRequest) {
                Dialog dialog = this.lastPermissionsDialog;
                if (dialog != null) {
                    dialog.dismiss();
                    this.lastPermissionsDialog = null;
                }
                String[] resources = permissionRequest.getResources();
                if (resources.length == 1) {
                    String str = resources[0];
                    str.hashCode();
                    if (str.equals("android.webkit.resource.VIDEO_CAPTURE")) {
                        Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.getContext(), resourcesProvider2, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$3$$ExternalSyntheticLambda4(this, permissionRequest, str));
                        this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                        createWebViewPermissionsRequestDialog.show();
                    } else if (str.equals("android.webkit.resource.AUDIO_CAPTURE")) {
                        Dialog createWebViewPermissionsRequestDialog2 = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.getContext(), resourcesProvider2, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$3$$ExternalSyntheticLambda5(this, permissionRequest, str));
                        this.lastPermissionsDialog = createWebViewPermissionsRequestDialog2;
                        createWebViewPermissionsRequestDialog2.show();
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPermissionRequest$3(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.RECORD_AUDIO"}, new BotWebViewContainer$3$$ExternalSyntheticLambda2(permissionRequest, str));
                        return;
                    }
                    permissionRequest.deny();
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$onPermissionRequest$2(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{str});
                    return;
                }
                permissionRequest.deny();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPermissionRequest$5(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO"}, new BotWebViewContainer$3$$ExternalSyntheticLambda1(permissionRequest, str));
                        return;
                    }
                    permissionRequest.deny();
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$onPermissionRequest$4(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{str});
                    return;
                }
                permissionRequest.deny();
            }

            public void onPermissionRequestCanceled(PermissionRequest permissionRequest) {
                Dialog dialog = this.lastPermissionsDialog;
                if (dialog != null) {
                    dialog.dismiss();
                    this.lastPermissionsDialog = null;
                }
            }
        });
        this.webView.setAlpha(0.0f);
        addView(this.webView);
        if (Build.VERSION.SDK_INT >= 17) {
            this.webView.addJavascriptInterface(new WebViewProxy(), "TelegramWebviewProxy");
        }
        TextView textView = new TextView(context);
        this.mainButton = textView;
        textView.setVisibility(8);
        this.mainButton.setAlpha(0.0f);
        this.mainButton.setSingleLine();
        this.mainButton.setGravity(17);
        int dp = AndroidUtilities.dp(16.0f);
        this.mainButton.setPadding(dp, 0, dp, 0);
        this.mainButton.setTextSize(1, 14.0f);
        this.mainButton.setOnClickListener(new BotWebViewContainer$$ExternalSyntheticLambda0(this));
        addView(this.mainButton, LayoutHelper.createFrame(-1, 48, 80));
        this.mainAutoAnimator = VerticalPositionAutoAnimator.attach(this.mainButton);
        RadialProgressView radialProgressView2 = new RadialProgressView(context);
        this.radialProgressView = radialProgressView2;
        radialProgressView2.setSize(AndroidUtilities.dp(18.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(8);
        addView(this.radialProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        this.progressAutoAnimator = VerticalPositionAutoAnimator.attach(this.radialProgressView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onMainButtonPressed();
    }

    public void setBotUser(TLRPC$User tLRPC$User) {
        this.botUser = tLRPC$User;
    }

    /* access modifiers changed from: private */
    public void runWithPermissions(String[] strArr, Consumer<Boolean> consumer) {
        if (Build.VERSION.SDK_INT < 23) {
            consumer.accept(Boolean.TRUE);
        } else if (checkPermissions(strArr)) {
            consumer.accept(Boolean.TRUE);
        } else {
            this.onPermissionsRequestResultCallback = new BotWebViewContainer$$ExternalSyntheticLambda2(this, consumer, strArr);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).requestPermissions(strArr, 4000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runWithPermissions$1(Consumer consumer, String[] strArr) {
        consumer.accept(Boolean.valueOf(checkPermissions(strArr)));
    }

    private boolean checkPermissions(String[] strArr) {
        for (String checkSelfPermission : strArr) {
            if (getContext().checkSelfPermission(checkSelfPermission) != 0) {
                return false;
            }
        }
        return true;
    }

    public void restoreButtonData() {
        String str = this.buttonData;
        if (str != null) {
            onEventReceived("web_app_setup_main_button", str);
        }
    }

    public void onMainButtonPressed() {
        evaluateJs("window.Telegram.WebView.receiveEvent('main_button_pressed', null);");
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        Runnable runnable;
        if (i == 4000 && (runnable = this.onPermissionsRequestResultCallback) != null) {
            runnable.run();
            this.onPermissionsRequestResultCallback = null;
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 3000 && this.mFilePathCallback != null) {
            this.mFilePathCallback.onReceiveValue((i2 != -1 || intent == null || intent.getDataString() == null) ? null : new Uri[]{Uri.parse(intent.getDataString())});
            this.mFilePathCallback = null;
        }
    }

    public void invalidateTranslation() {
        if (getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = (ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent();
            float swipeOffsetY = ((-webViewSwipeContainer.getOffsetY()) - webViewSwipeContainer.getSwipeOffsetY()) + webViewSwipeContainer.getTopActionBarOffsetY();
            this.mainAutoAnimator.setOffsetY(swipeOffsetY);
            this.progressAutoAnimator.setOffsetY(swipeOffsetY);
            invalidateViewPortHeight();
        }
    }

    private void invalidateViewPortHeight() {
        if (this.isPageLoaded && (getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = (ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent();
            int height = (int) (((((float) getHeight()) - webViewSwipeContainer.getOffsetY()) - webViewSwipeContainer.getSwipeOffsetY()) + webViewSwipeContainer.getTopActionBarOffsetY());
            evaluateJs("window.Telegram.WebView.receiveEvent('viewport_changed', {\"height\": " + height + "});");
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (view != this.flickerView) {
            return super.drawChild(canvas, view, j);
        }
        canvas.save();
        canvas.translate(0.0f, (((float) ActionBar.getCurrentActionBarHeight()) - ((View) getParent()).getTranslationY()) / 2.0f);
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        this.flickerDrawable.draw(canvas, rectF, 0.0f);
        invalidate();
        return drawChild;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.flickerDrawable.setParentWidth(getMeasuredWidth());
    }

    public void setWebViewProgressListener(Consumer<Float> consumer) {
        this.webViewProgressListener = consumer;
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void loadFlicker(int i, long j) {
        TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot;
        Iterator<TLRPC$TL_attachMenuBot> it = MediaDataController.getInstance(i).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                tLRPC$TL_attachMenuBot = null;
                break;
            }
            tLRPC$TL_attachMenuBot = it.next();
            if (tLRPC$TL_attachMenuBot.bot_id == j) {
                break;
            }
        }
        if (tLRPC$TL_attachMenuBot != null) {
            TLRPC$TL_attachMenuBotIcon staticAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
            if (staticAttachMenuBotIcon != null) {
                this.flickerView.setVisibility(0);
                this.flickerView.setAlpha(1.0f);
                this.flickerView.setImage(ImageLocation.getForDocument(staticAttachMenuBotIcon.icon), "64_64", (Drawable) null, (Object) tLRPC$TL_attachMenuBot);
                return;
            }
            return;
        }
        TLRPC$TL_messages_getAttachMenuBot tLRPC$TL_messages_getAttachMenuBot = new TLRPC$TL_messages_getAttachMenuBot();
        tLRPC$TL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i).getInputUser(j);
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getAttachMenuBot, new BotWebViewContainer$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFlicker$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda3(this, tLObject));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r5 = ((org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot) r5).bot;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadFlicker$2(org.telegram.tgnet.TLObject r5) {
        /*
            r4 = this;
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot
            if (r0 == 0) goto L_0x0029
            org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot r5 = (org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot) r5
            org.telegram.tgnet.TLRPC$TL_attachMenuBot r5 = r5.bot
            org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon r0 = org.telegram.messenger.MediaDataController.getStaticAttachMenuBotIcon(r5)
            if (r0 == 0) goto L_0x0029
            org.telegram.ui.Components.BackupImageView r1 = r4.flickerView
            r2 = 0
            r1.setVisibility(r2)
            org.telegram.ui.Components.BackupImageView r1 = r4.flickerView
            r2 = 1065353216(0x3var_, float:1.0)
            r1.setAlpha(r2)
            org.telegram.ui.Components.BackupImageView r1 = r4.flickerView
            org.telegram.tgnet.TLRPC$Document r0 = r0.icon
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForDocument(r0)
            r2 = 0
            java.lang.String r3 = "64_64"
            r1.setImage((org.telegram.messenger.ImageLocation) r0, (java.lang.String) r3, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r5)
        L_0x0029:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewContainer.lambda$loadFlicker$2(org.telegram.tgnet.TLObject):void");
    }

    public void loadUrl(String str) {
        this.isPageLoaded = false;
        this.mUrl = str;
        this.webView.loadUrl(str);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.onActivityResultReceived);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onActivityResultReceived);
    }

    public void evaluateJs(String str) {
        if (Build.VERSION.SDK_INT >= 19) {
            this.webView.evaluateJavascript(str, BotWebViewContainer$$ExternalSyntheticLambda1.INSTANCE);
            return;
        }
        try {
            WebView webView2 = this.webView;
            webView2.loadUrl("javascript:" + URLEncoder.encode(str, "UTF-8"));
        } catch (UnsupportedEncodingException unused) {
            WebView webView3 = this.webView;
            webView3.loadUrl("javascript:" + URLEncoder.encode(str));
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetNewTheme) {
            evaluateJs("window.Telegram.WebView.receiveEvent('theme_changed', {theme_params: " + buildThemeParams() + "});");
        } else if (i == NotificationCenter.onActivityResultReceived) {
            onActivityResult(objArr[0].intValue(), objArr[1].intValue(), objArr[2]);
        } else if (i == NotificationCenter.onRequestPermissionResultReceived) {
            onRequestPermissionsResult(objArr[0].intValue(), objArr[1], objArr[2]);
        }
    }

    public void setWebViewScrollListener(WebViewScrollListener webViewScrollListener2) {
        this.webViewScrollListener = webViewScrollListener2;
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void onEventReceived(String str, String str2) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -921083201:
                if (str.equals("web_app_request_viewport")) {
                    c = 0;
                    break;
                }
                break;
            case -71726289:
                if (str.equals("web_app_close")) {
                    c = 1;
                    break;
                }
                break;
            case 668142772:
                if (str.equals("web_app_data_send")) {
                    c = 2;
                    break;
                }
                break;
            case 1398490221:
                if (str.equals("web_app_setup_main_button")) {
                    c = 3;
                    break;
                }
                break;
            case 2139805763:
                if (str.equals("web_app_expand")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                invalidateViewPortHeight();
                return;
            case 1:
                this.delegate.onCloseRequested();
                return;
            case 2:
                this.delegate.onSendWebViewData(str2);
                return;
            case 3:
                try {
                    JSONObject jSONObject = new JSONObject(str2);
                    boolean optBoolean = jSONObject.optBoolean("is_active", false);
                    String trim = jSONObject.optString("text", "").trim();
                    final boolean z = jSONObject.optBoolean("is_visible", false) && !TextUtils.isEmpty(trim);
                    int parseColor = jSONObject.has("color") ? Color.parseColor(jSONObject.optString("color")) : Theme.getColor("featuredStickers_addButton");
                    int parseColor2 = jSONObject.has("text_color") ? Color.parseColor(jSONObject.optString("text_color")) : Theme.getColor("featuredStickers_buttonText");
                    final boolean z2 = jSONObject.optBoolean("is_progress_visible", false) && z;
                    this.buttonData = str2;
                    if (this.delegate.hasIntegratedMainButton()) {
                        this.delegate.onSetupMainButton(z, optBoolean, trim, parseColor, parseColor2, z2);
                        return;
                    }
                    this.mainButton.setClickable(optBoolean);
                    this.mainButton.setText(trim);
                    this.mainButton.setTextColor(parseColor2);
                    this.mainButton.setBackground(Theme.createSelectorWithBackgroundDrawable(parseColor, Theme.getColor("listSelectorSDK21")));
                    float f = 1.0f;
                    float f2 = 0.0f;
                    if (this.mainButtonWasVisible != z) {
                        this.mainButton.animate().cancel();
                        if (z) {
                            this.mainButton.setAlpha(0.0f);
                            this.mainButton.setVisibility(0);
                        }
                        this.mainButton.animate().alpha(z ? 1.0f : 0.0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                boolean unused = BotWebViewContainer.this.mainButtonWasVisible = z;
                                if (!z) {
                                    BotWebViewContainer.this.mainButton.setVisibility(8);
                                }
                                BotWebViewContainer.this.webView.requestLayout();
                            }
                        }).start();
                    }
                    this.radialProgressView.setProgressColor(parseColor2);
                    if (this.mainButtonProgressWasVisible != z2) {
                        this.radialProgressView.animate().cancel();
                        if (z2) {
                            this.radialProgressView.setAlpha(0.0f);
                            this.radialProgressView.setVisibility(0);
                        }
                        ViewPropertyAnimator animate = this.radialProgressView.animate();
                        if (z2) {
                            f2 = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f2).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f = 0.1f;
                        }
                        scaleX.scaleY(f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                boolean unused = BotWebViewContainer.this.mainButtonProgressWasVisible = z2;
                                if (!z2) {
                                    BotWebViewContainer.this.radialProgressView.setVisibility(8);
                                }
                            }
                        }).start();
                        return;
                    }
                    return;
                } catch (IllegalArgumentException | JSONException e) {
                    FileLog.e(e);
                    return;
                }
            case 4:
                this.delegate.onWebAppExpand();
                return;
            default:
                return;
        }
    }

    private String buildThemeParams() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", formatColor("windowBackgroundWhite"));
            jSONObject.put("text_color", formatColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", formatColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", formatColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", formatColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", formatColor("featuredStickers_buttonText"));
            return jSONObject.toString();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "{}";
        }
    }

    private String formatColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer valueOf = Integer.valueOf(resourcesProvider2 != null ? resourcesProvider2.getColor(str).intValue() : Theme.getColor(str));
        if (valueOf == null) {
            valueOf = Integer.valueOf(Theme.getColor(str));
        }
        return "#" + hexFixed(Color.red(valueOf.intValue())) + hexFixed(Color.green(valueOf.intValue())) + hexFixed(Color.blue(valueOf.intValue()));
    }

    private String hexFixed(int i) {
        String hexString = Integer.toHexString(i);
        if (hexString.length() >= 2) {
            return hexString;
        }
        return "0" + hexString;
    }

    private class WebViewProxy {
        private WebViewProxy() {
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$postEvent$0(String str, String str2) {
            BotWebViewContainer.this.onEventReceived(str, str2);
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new BotWebViewContainer$WebViewProxy$$ExternalSyntheticLambda0(this, str, str2));
        }
    }
}
