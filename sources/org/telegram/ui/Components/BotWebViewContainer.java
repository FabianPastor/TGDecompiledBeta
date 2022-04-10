package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
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
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
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
    private CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();
    /* access modifiers changed from: private */
    public BackupImageView flickerView;
    /* access modifiers changed from: private */
    public boolean hasUserPermissions;
    private boolean isFlickeringCenter;
    /* access modifiers changed from: private */
    public boolean isPageLoaded;
    private int lastButtonColor = Theme.getColor("featuredStickers_addButton");
    private String lastButtonText = "";
    private int lastButtonTextColor = Theme.getColor("featuredStickers_buttonText");
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mFilePathCallback;
    /* access modifiers changed from: private */
    public String mUrl;
    private Runnable onPermissionsRequestResultCallback;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public WebView webView;
    /* access modifiers changed from: private */
    public Consumer<Float> webViewProgressListener;
    /* access modifiers changed from: private */
    public WebViewScrollListener webViewScrollListener;

    public interface Delegate {
        void onCloseRequested();

        void onSendWebViewData(String str);

        void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3);

        void onWebAppExpand();
    }

    public interface WebViewScrollListener {
        void onWebViewScrolled(WebView webView, int i, int i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$evaluateJs$3(String str) {
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public BotWebViewContainer(final Context context, final Theme.ResourcesProvider resourcesProvider2, int i) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        CellFlickerDrawable cellFlickerDrawable = this.flickerDrawable;
        cellFlickerDrawable.drawFrame = false;
        cellFlickerDrawable.setColors(i, 153, 204);
        BackupImageView backupImageView = new BackupImageView(context);
        this.flickerView = backupImageView;
        backupImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackgroundGray"), PorterDuff.Mode.SRC_IN));
        this.flickerView.getImageReceiver().setAspectFit(true);
        addView(this.flickerView, LayoutHelper.createFrame(-1, -2, 48));
        AnonymousClass1 r7 = new WebView(context) {
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
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
            }
        };
        this.webView = r7;
        r7.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        GeolocationPermissions.getInstance().clearAll();
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                Uri parse = Uri.parse(BotWebViewContainer.this.mUrl);
                Uri parse2 = Uri.parse(str);
                if (ObjectsCompat$$ExternalSyntheticBackport0.m(parse.getHost(), parse2.getHost()) && ObjectsCompat$$ExternalSyntheticBackport0.m(parse.getPath(), parse2.getPath())) {
                    return false;
                }
                if (BotWebViewContainer.WHITELISTED_SCHEMES.contains(parse2.getScheme())) {
                    new AlertDialog.Builder(context, resourcesProvider2).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.formatString(NUM, parse2.toString())).setPositiveButton(LocaleController.getString(NUM), new BotWebViewContainer$2$$ExternalSyntheticLambda0(this, parse2)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).show();
                }
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$shouldOverrideUrlLoading$0(Uri uri, DialogInterface dialogInterface, int i) {
                boolean isInternalUri = Browser.isInternalUri(uri, new boolean[]{false});
                Browser.openUrl(BotWebViewContainer.this.getContext(), uri, true, false);
                if (isInternalUri && BotWebViewContainer.this.delegate != null) {
                    BotWebViewContainer.this.delegate.onCloseRequested();
                }
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
                if (BotWebViewContainer.this.parentActivity == null) {
                    callback.invoke(str, false, false);
                    return;
                }
                Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, resourcesProvider2, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$3$$ExternalSyntheticLambda0(this, callback, str));
                this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                createWebViewPermissionsRequestDialog.show();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$1(GeolocationPermissions.Callback callback, String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new BotWebViewContainer$3$$ExternalSyntheticLambda1(this, callback, str));
                        return;
                    }
                    callback.invoke(str, false, false);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$0(GeolocationPermissions.Callback callback, String str, Boolean bool) {
                callback.invoke(str, bool.booleanValue(), false);
                if (bool.booleanValue()) {
                    boolean unused = BotWebViewContainer.this.hasUserPermissions = true;
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
                    if (BotWebViewContainer.this.parentActivity == null) {
                        permissionRequest.deny();
                        return;
                    }
                    str.hashCode();
                    if (str.equals("android.webkit.resource.VIDEO_CAPTURE")) {
                        Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, resourcesProvider2, new String[]{"android.permission.CAMERA"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$3$$ExternalSyntheticLambda3(this, permissionRequest, str));
                        this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                        createWebViewPermissionsRequestDialog.show();
                    } else if (str.equals("android.webkit.resource.AUDIO_CAPTURE")) {
                        Dialog createWebViewPermissionsRequestDialog2 = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, resourcesProvider2, new String[]{"android.permission.RECORD_AUDIO"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$3$$ExternalSyntheticLambda5(this, permissionRequest, str));
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
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.RECORD_AUDIO"}, new BotWebViewContainer$3$$ExternalSyntheticLambda2(this, permissionRequest, str));
                        return;
                    }
                    permissionRequest.deny();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPermissionRequest$2(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{str});
                    boolean unused = BotWebViewContainer.this.hasUserPermissions = true;
                    return;
                }
                permissionRequest.deny();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPermissionRequest$5(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.CAMERA"}, new BotWebViewContainer$3$$ExternalSyntheticLambda4(this, permissionRequest, str));
                        return;
                    }
                    permissionRequest.deny();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPermissionRequest$4(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{str});
                    boolean unused = BotWebViewContainer.this.hasUserPermissions = true;
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
    }

    public boolean hasUserPermissions() {
        return this.hasUserPermissions;
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
            this.onPermissionsRequestResultCallback = new BotWebViewContainer$$ExternalSyntheticLambda1(this, consumer, strArr);
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.requestPermissions(strArr, 4000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runWithPermissions$0(Consumer consumer, String[] strArr) {
        consumer.accept(Boolean.valueOf(checkPermissions(strArr)));
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
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
        invalidateViewPortHeight();
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
        if (this.isFlickeringCenter) {
            canvas.save();
            canvas.translate(0.0f, (((float) ActionBar.getCurrentActionBarHeight()) - ((View) getParent()).getTranslationY()) / 2.0f);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.isFlickeringCenter) {
            canvas.restore();
        }
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
        boolean z;
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
            TLRPC$TL_attachMenuBotIcon placeholderStaticAttachMenuBotIcon = MediaDataController.getPlaceholderStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
            if (placeholderStaticAttachMenuBotIcon == null) {
                placeholderStaticAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
                z = true;
            } else {
                z = false;
            }
            if (placeholderStaticAttachMenuBotIcon != null) {
                this.flickerView.setVisibility(0);
                this.flickerView.setAlpha(1.0f);
                this.flickerView.setImage(ImageLocation.getForDocument(placeholderStaticAttachMenuBotIcon.icon), (String) null, (Drawable) null, (Object) tLRPC$TL_attachMenuBot);
                setupFlickerParams(z);
                return;
            }
            return;
        }
        TLRPC$TL_messages_getAttachMenuBot tLRPC$TL_messages_getAttachMenuBot = new TLRPC$TL_messages_getAttachMenuBot();
        tLRPC$TL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i).getInputUser(j);
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getAttachMenuBot, new BotWebViewContainer$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFlicker$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda2(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFlicker$1(TLObject tLObject) {
        boolean z;
        if (tLObject instanceof TLRPC$TL_attachMenuBotsBot) {
            TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = ((TLRPC$TL_attachMenuBotsBot) tLObject).bot;
            TLRPC$TL_attachMenuBotIcon placeholderStaticAttachMenuBotIcon = MediaDataController.getPlaceholderStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
            if (placeholderStaticAttachMenuBotIcon == null) {
                placeholderStaticAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
                z = true;
            } else {
                z = false;
            }
            if (placeholderStaticAttachMenuBotIcon != null) {
                this.flickerView.setVisibility(0);
                this.flickerView.setAlpha(1.0f);
                this.flickerView.setImage(ImageLocation.getForDocument(placeholderStaticAttachMenuBotIcon.icon), (String) null, (Drawable) null, (Object) tLRPC$TL_attachMenuBot);
                setupFlickerParams(z);
            }
        }
    }

    private void setupFlickerParams(boolean z) {
        this.isFlickeringCenter = z;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.flickerView.getLayoutParams();
        layoutParams.gravity = z ? 17 : 48;
        if (z) {
            int dp = AndroidUtilities.dp(64.0f);
            layoutParams.height = dp;
            layoutParams.width = dp;
        } else {
            layoutParams.width = -1;
            layoutParams.height = -2;
        }
        this.flickerView.requestLayout();
    }

    public void loadUrl(String str) {
        this.isPageLoaded = false;
        this.hasUserPermissions = false;
        this.mUrl = str;
        this.webView.loadUrl(str);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.onActivityResultReceived);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.onRequestPermissionResultReceived);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onActivityResultReceived);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onRequestPermissionResultReceived);
    }

    public void destroyWebView() {
        this.webView.destroy();
    }

    public void evaluateJs(String str) {
        if (Build.VERSION.SDK_INT >= 19) {
            this.webView.evaluateJavascript(str, BotWebViewContainer$$ExternalSyntheticLambda0.INSTANCE);
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
                    String trim = jSONObject.optString("text", this.lastButtonText).trim();
                    boolean z = jSONObject.optBoolean("is_visible", false) && !TextUtils.isEmpty(trim);
                    int parseColor = jSONObject.has("color") ? Color.parseColor(jSONObject.optString("color")) : this.lastButtonColor;
                    int parseColor2 = jSONObject.has("text_color") ? Color.parseColor(jSONObject.optString("text_color")) : this.lastButtonTextColor;
                    boolean z2 = jSONObject.optBoolean("is_progress_visible", false) && z;
                    this.lastButtonColor = parseColor;
                    this.lastButtonTextColor = parseColor2;
                    this.lastButtonText = trim;
                    this.buttonData = str2;
                    this.delegate.onSetupMainButton(z, optBoolean, trim, parseColor, parseColor2, z2);
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
