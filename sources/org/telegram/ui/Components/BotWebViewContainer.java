package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.core.util.Consumer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class BotWebViewContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String DURGER_KING_USERNAME = "DurgerKingBot";
    private static final int REQUEST_CODE_WEB_PERMISSION = 4000;
    private static final int REQUEST_CODE_WEB_VIEW_FILE = 3000;
    /* access modifiers changed from: private */
    public static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[]{"http", "https"});
    /* access modifiers changed from: private */
    public TLRPC.User botUser;
    private String buttonData;
    private int currentAccount;
    private String currentPaymentSlug;
    private Delegate delegate;
    private CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();
    /* access modifiers changed from: private */
    public BackupImageView flickerView;
    /* access modifiers changed from: private */
    public boolean hasUserPermissions;
    private boolean isBackButtonVisible;
    /* access modifiers changed from: private */
    public boolean isFlickeringCenter;
    /* access modifiers changed from: private */
    public boolean isPageLoaded;
    private boolean isRequestingPageOpen;
    private boolean isViewPortByMeasureSuppressed;
    private int lastButtonColor = getColor("featuredStickers_addButton");
    private String lastButtonText = "";
    private int lastButtonTextColor = getColor("featuredStickers_buttonText");
    /* access modifiers changed from: private */
    public long lastClickMs;
    private boolean lastExpanded;
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mFilePathCallback;
    /* access modifiers changed from: private */
    public String mUrl;
    private Runnable onPermissionsRequestResultCallback;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    private WebView webView;
    private boolean webViewNotAvailable;
    private TextView webViewNotAvailableText;
    /* access modifiers changed from: private */
    public Consumer<Float> webViewProgressListener;
    /* access modifiers changed from: private */
    public WebViewScrollListener webViewScrollListener;

    public interface WebViewScrollListener {
        void onWebViewScrolled(WebView webView, int i, int i2);
    }

    public BotWebViewContainer(Context context, Theme.ResourcesProvider resourcesProvider2, int backgroundColor) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.flickerDrawable.drawFrame = false;
        this.flickerDrawable.setColors(backgroundColor, 153, 204);
        AnonymousClass1 r0 = new BackupImageView(context) {
            {
                this.imageReceiver = new ImageReceiver(this) {
                    /* access modifiers changed from: protected */
                    public boolean setImageBitmapByKey(Drawable drawable, String key, int type, boolean memCache, int guid) {
                        boolean set = super.setImageBitmapByKey(drawable, key, type, memCache, guid);
                        ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                        anim.addUpdateListener(new BotWebViewContainer$1$1$$ExternalSyntheticLambda0(this));
                        anim.start();
                        return set;
                    }

                    /* renamed from: lambda$setImageBitmapByKey$0$org-telegram-ui-Components-BotWebViewContainer$1$1  reason: not valid java name */
                    public /* synthetic */ void m585x2638d59d(ValueAnimator animation) {
                        AnonymousClass1.this.imageReceiver.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
                        invalidate();
                    }
                };
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (BotWebViewContainer.this.isFlickeringCenter) {
                    super.onDraw(canvas);
                    return;
                }
                Drawable drawable = this.imageReceiver.getDrawable();
                if (drawable != null) {
                    this.imageReceiver.setImageCoords(0.0f, 0.0f, (float) getWidth(), ((float) drawable.getIntrinsicHeight()) * (((float) getWidth()) / ((float) drawable.getIntrinsicWidth())));
                    this.imageReceiver.draw(canvas);
                }
            }
        };
        this.flickerView = r0;
        r0.setColorFilter(new PorterDuffColorFilter(getColor("dialogSearchHint"), PorterDuff.Mode.SRC_IN));
        this.flickerView.getImageReceiver().setAspectFit(true);
        addView(this.flickerView, LayoutHelper.createFrame(-1, -2, 48));
        TextView textView = new TextView(context);
        this.webViewNotAvailableText = textView;
        textView.setText(LocaleController.getString(NUM));
        this.webViewNotAvailableText.setTextColor(getColor("windowBackgroundWhiteGrayText"));
        this.webViewNotAvailableText.setTextSize(1, 15.0f);
        this.webViewNotAvailableText.setGravity(17);
        this.webViewNotAvailableText.setVisibility(8);
        int padding = AndroidUtilities.dp(16.0f);
        this.webViewNotAvailableText.setPadding(padding, padding, padding, padding);
        addView(this.webViewNotAvailableText, LayoutHelper.createFrame(-1, -2, 17));
        setFocusable(false);
    }

    public void setViewPortByMeasureSuppressed(boolean viewPortByMeasureSuppressed) {
        this.isViewPortByMeasureSuppressed = viewPortByMeasureSuppressed;
    }

    private void checkCreateWebView() {
        if (this.webView == null && !this.webViewNotAvailable) {
            try {
                setupWebView();
            } catch (Throwable t) {
                FileLog.e(t);
                this.flickerView.setVisibility(8);
                this.webViewNotAvailable = true;
                this.webViewNotAvailableText.setVisibility(0);
                if (this.webView != null) {
                    removeView(this.webView);
                }
            }
        }
    }

    private void setupWebView() {
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.destroy();
            removeView(this.webView);
        }
        AnonymousClass2 r0 = new WebView(getContext()) {
            private int prevScrollX;
            private int prevScrollY;

            /* access modifiers changed from: protected */
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                if (BotWebViewContainer.this.webViewScrollListener != null) {
                    BotWebViewContainer.this.webViewScrollListener.onWebViewScrolled(this, getScrollX() - this.prevScrollX, getScrollY() - this.prevScrollY);
                }
                this.prevScrollX = getScrollX();
                this.prevScrollY = getScrollY();
            }

            public void setScrollX(int value) {
                super.setScrollX(value);
                this.prevScrollX = value;
            }

            public void setScrollY(int value) {
                super.setScrollY(value);
                this.prevScrollY = value;
            }

            public boolean onCheckIsTextEditor() {
                return BotWebViewContainer.this.isFocusable();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), NUM));
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    long unused = BotWebViewContainer.this.lastClickMs = System.currentTimeMillis();
                }
                return super.onTouchEvent(event);
            }
        };
        this.webView = r0;
        r0.setBackgroundColor(getColor("windowBackgroundWhite"));
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        File databaseStorage = new File(ApplicationLoader.getFilesDirFixed(), "webview_database");
        if ((databaseStorage.exists() && databaseStorage.isDirectory()) || databaseStorage.mkdirs()) {
            settings.setDatabasePath(databaseStorage.getAbsolutePath());
        }
        GeolocationPermissions.getInstance().clearAll();
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uriOrig = Uri.parse(BotWebViewContainer.this.mUrl);
                Uri uriNew = Uri.parse(url);
                if (!BotWebViewContainer.this.isPageLoaded || (ColorUtils$$ExternalSyntheticBackport0.m(uriOrig.getHost(), uriNew.getHost()) && ColorUtils$$ExternalSyntheticBackport0.m(uriOrig.getPath(), uriNew.getPath()))) {
                    return false;
                }
                if (!BotWebViewContainer.WHITELISTED_SCHEMES.contains(uriNew.getScheme())) {
                    return true;
                }
                BotWebViewContainer.this.onOpenUri(uriNew);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                BotWebViewContainer.this.setPageLoaded(url);
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {
            private Dialog lastPermissionsDialog;

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                Context ctx = BotWebViewContainer.this.getContext();
                if (!(ctx instanceof Activity)) {
                    return false;
                }
                Activity activity = (Activity) ctx;
                if (BotWebViewContainer.this.mFilePathCallback != null) {
                    BotWebViewContainer.this.mFilePathCallback.onReceiveValue((Object) null);
                }
                ValueCallback unused = BotWebViewContainer.this.mFilePathCallback = filePathCallback;
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

            public void onProgressChanged(WebView view, int newProgress) {
                if (BotWebViewContainer.this.webViewProgressListener != null) {
                    BotWebViewContainer.this.webViewProgressListener.accept(Float.valueOf(((float) newProgress) / 100.0f));
                }
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (BotWebViewContainer.this.parentActivity == null) {
                    callback.invoke(origin, false, false);
                    return;
                }
                Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$4$$ExternalSyntheticLambda1(this, callback, origin));
                this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                createWebViewPermissionsRequestDialog.show();
            }

            /* renamed from: lambda$onGeolocationPermissionsShowPrompt$1$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m587x53986a9a(GeolocationPermissions.Callback callback, String origin, Boolean allow) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (allow.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new BotWebViewContainer$4$$ExternalSyntheticLambda0(this, callback, origin));
                        return;
                    }
                    callback.invoke(origin, false, false);
                }
            }

            /* renamed from: lambda$onGeolocationPermissionsShowPrompt$0$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m586x61eeCLASSNAMEb(GeolocationPermissions.Callback callback, String origin, Boolean allowSystem) {
                callback.invoke(origin, allowSystem.booleanValue(), false);
                if (allowSystem.booleanValue()) {
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

            public void onPermissionRequest(PermissionRequest request) {
                Dialog dialog = this.lastPermissionsDialog;
                if (dialog != null) {
                    dialog.dismiss();
                    this.lastPermissionsDialog = null;
                }
                String[] resources = request.getResources();
                if (resources.length == 1) {
                    String resource = resources[0];
                    if (BotWebViewContainer.this.parentActivity == null) {
                        request.deny();
                        return;
                    }
                    char c = 65535;
                    switch (resource.hashCode()) {
                        case -1660821873:
                            if (resource.equals("android.webkit.resource.VIDEO_CAPTURE")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 968612586:
                            if (resource.equals("android.webkit.resource.AUDIO_CAPTURE")) {
                                c = 0;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.RECORD_AUDIO"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$4$$ExternalSyntheticLambda3(this, request, resource));
                            this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                            createWebViewPermissionsRequestDialog.show();
                            return;
                        case 1:
                            Dialog createWebViewPermissionsRequestDialog2 = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.CAMERA"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$4$$ExternalSyntheticLambda5(this, request, resource));
                            this.lastPermissionsDialog = createWebViewPermissionsRequestDialog2;
                            createWebViewPermissionsRequestDialog2.show();
                            return;
                        default:
                            return;
                    }
                }
            }

            /* renamed from: lambda$onPermissionRequest$3$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m589x14aacaef(PermissionRequest request, String resource, Boolean allow) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (allow.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.RECORD_AUDIO"}, new BotWebViewContainer$4$$ExternalSyntheticLambda2(this, request, resource));
                        return;
                    }
                    request.deny();
                }
            }

            /* renamed from: lambda$onPermissionRequest$2$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m588x230124d0(PermissionRequest request, String resource, Boolean allowSystem) {
                if (allowSystem.booleanValue()) {
                    request.grant(new String[]{resource});
                    boolean unused = BotWebViewContainer.this.hasUserPermissions = true;
                    return;
                }
                request.deny();
            }

            /* renamed from: lambda$onPermissionRequest$5$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m591xf7fe172d(PermissionRequest request, String resource, Boolean allow) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (allow.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.CAMERA"}, new BotWebViewContainer$4$$ExternalSyntheticLambda4(this, request, resource));
                        return;
                    }
                    request.deny();
                }
            }

            /* renamed from: lambda$onPermissionRequest$4$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m590x654710e(PermissionRequest request, String resource, Boolean allowSystem) {
                if (allowSystem.booleanValue()) {
                    request.grant(new String[]{resource});
                    boolean unused = BotWebViewContainer.this.hasUserPermissions = true;
                    return;
                }
                request.deny();
            }

            public void onPermissionRequestCanceled(PermissionRequest request) {
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

    /* access modifiers changed from: private */
    public void onOpenUri(Uri uri) {
        onOpenUri(uri, false);
    }

    private void onOpenUri(Uri uri, boolean suppressPopup) {
        if (this.isRequestingPageOpen) {
            return;
        }
        if (System.currentTimeMillis() - this.lastClickMs <= 10000 || !suppressPopup) {
            this.lastClickMs = 0;
            boolean[] forceBrowser = {false};
            if (!Browser.isInternalUri(uri, forceBrowser) || forceBrowser[0]) {
                if (suppressPopup) {
                    Browser.openUrl(getContext(), uri, true, false);
                    return;
                }
                this.isRequestingPageOpen = true;
                new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.formatString(NUM, uri.toString())).setPositiveButton(LocaleController.getString(NUM), new BotWebViewContainer$$ExternalSyntheticLambda0(this, uri)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new BotWebViewContainer$$ExternalSyntheticLambda1(this)).show();
            } else if (this.delegate != null) {
                setDescendantFocusability(393216);
                setFocusable(false);
                this.webView.setFocusable(false);
                this.webView.setDescendantFocusability(393216);
                this.webView.clearFocus();
                ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 2);
                this.delegate.onCloseRequested(new BotWebViewContainer$$ExternalSyntheticLambda3(this, uri));
            } else {
                Browser.openUrl(getContext(), uri, true, false);
            }
        }
    }

    /* renamed from: lambda$onOpenUri$0$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m581xb32d06c0(Uri uri) {
        Browser.openUrl(getContext(), uri, true, false);
    }

    /* renamed from: lambda$onOpenUri$1$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m582xecf7a89f(Uri uri, DialogInterface dialog, int which) {
        Browser.openUrl(getContext(), uri, true, false);
    }

    /* renamed from: lambda$onOpenUri$2$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m583x26CLASSNAMEa7e(DialogInterface dialog) {
        this.isRequestingPageOpen = false;
    }

    public static int getMainButtonRippleColor(int buttonColor) {
        return ColorUtils.calculateLuminance(buttonColor) >= 0.30000001192092896d ? NUM : NUM;
    }

    public static Drawable getMainButtonRippleDrawable(int buttonColor) {
        return Theme.createSelectorWithBackgroundDrawable(buttonColor, getMainButtonRippleColor(buttonColor));
    }

    public void updateFlickerBackgroundColor(int backgroundColor) {
        this.flickerDrawable.setColors(backgroundColor, 153, 204);
    }

    public boolean onBackPressed() {
        if (this.webView == null || !this.isBackButtonVisible) {
            return false;
        }
        notifyEvent("back_button_pressed", (JSONObject) null);
        return true;
    }

    /* access modifiers changed from: private */
    public void setPageLoaded(String url) {
        if (!this.isPageLoaded) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.webView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flickerView, View.ALPHA, new float[]{0.0f})});
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    BotWebViewContainer.this.flickerView.setVisibility(8);
                }
            });
            set.start();
            this.mUrl = url;
            this.isPageLoaded = true;
            setFocusable(true);
            this.delegate.onWebAppReady();
        }
    }

    public boolean hasUserPermissions() {
        return this.hasUserPermissions;
    }

    public void setBotUser(TLRPC.User botUser2) {
        this.botUser = botUser2;
    }

    /* access modifiers changed from: private */
    public void runWithPermissions(String[] permissions, Consumer<Boolean> callback) {
        if (Build.VERSION.SDK_INT < 23) {
            callback.accept(true);
        } else if (checkPermissions(permissions)) {
            callback.accept(true);
        } else {
            this.onPermissionsRequestResultCallback = new BotWebViewContainer$$ExternalSyntheticLambda4(this, callback, permissions);
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.requestPermissions(permissions, 4000);
            }
        }
    }

    /* renamed from: lambda$runWithPermissions$3$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m584x4d467b13(Consumer callback, String[] permissions) {
        callback.accept(Boolean.valueOf(checkPermissions(permissions)));
    }

    public boolean isPageLoaded() {
        return this.isPageLoaded;
    }

    public void setParentActivity(Activity parentActivity2) {
        this.parentActivity = parentActivity2;
    }

    private boolean checkPermissions(String[] permissions) {
        for (String perm : permissions) {
            if (getContext().checkSelfPermission(perm) != 0) {
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

    public void onInvoiceStatusUpdate(String slug, String status) {
        onInvoiceStatusUpdate(slug, status, false);
    }

    public void onInvoiceStatusUpdate(String slug, String status, boolean ignoreCurrentCheck) {
        try {
            JSONObject data = new JSONObject();
            data.put("slug", slug);
            data.put("status", status);
            notifyEvent("invoice_closed", data);
            if (!ignoreCurrentCheck && ColorUtils$$ExternalSyntheticBackport0.m(this.currentPaymentSlug, slug)) {
                this.currentPaymentSlug = null;
            }
        } catch (JSONException e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onSettingsButtonPressed() {
        this.lastClickMs = System.currentTimeMillis();
        notifyEvent("settings_button_pressed", (JSONObject) null);
    }

    public void onMainButtonPressed() {
        this.lastClickMs = System.currentTimeMillis();
        notifyEvent("main_button_pressed", (JSONObject) null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Runnable runnable;
        if (requestCode == 4000 && (runnable = this.onPermissionsRequestResultCallback) != null) {
            runnable.run();
            this.onPermissionsRequestResultCallback = null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3000 && this.mFilePathCallback != null) {
            Uri[] results = null;
            if (!(resultCode != -1 || data == null || data.getDataString() == null)) {
                results = new Uri[]{Uri.parse(data.getDataString())};
            }
            this.mFilePathCallback.onReceiveValue(results);
            this.mFilePathCallback = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!this.isViewPortByMeasureSuppressed) {
            invalidateViewPortHeight(true);
        }
    }

    public void invalidateViewPortHeight() {
        invalidateViewPortHeight(false);
    }

    public void invalidateViewPortHeight(boolean isStable) {
        invalidateViewPortHeight(isStable, false);
    }

    public void invalidateViewPortHeight(boolean isStable, boolean force) {
        invalidate();
        if ((this.isPageLoaded || force) && (getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer = (ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent();
            if (isStable) {
                this.lastExpanded = swipeContainer.getSwipeOffsetY() == (-swipeContainer.getOffsetY()) + swipeContainer.getTopActionBarOffsetY();
            }
            int viewPortHeight = (int) (((((float) swipeContainer.getMeasuredHeight()) - swipeContainer.getOffsetY()) - swipeContainer.getSwipeOffsetY()) + swipeContainer.getTopActionBarOffsetY());
            try {
                JSONObject data = new JSONObject();
                data.put("height", (double) (((float) viewPortHeight) / AndroidUtilities.density));
                data.put("is_state_stable", isStable);
                data.put("is_expanded", this.lastExpanded);
                notifyEvent("viewport_changed", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.flickerView) {
            if (this.isFlickeringCenter) {
                canvas.save();
                canvas.translate(0.0f, (((float) ActionBar.getCurrentActionBarHeight()) - ((View) getParent()).getTranslationY()) / 2.0f);
            }
            boolean draw = super.drawChild(canvas, child, drawingTime);
            if (this.isFlickeringCenter) {
                canvas.restore();
            }
            AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            this.flickerDrawable.draw(canvas, AndroidUtilities.rectTmp, 0.0f, this);
            invalidate();
            return draw;
        } else if (child != this.webViewNotAvailableText) {
            return super.drawChild(canvas, child, drawingTime);
        } else {
            canvas.save();
            canvas.translate(0.0f, (((float) ActionBar.getCurrentActionBarHeight()) - ((View) getParent()).getTranslationY()) / 2.0f);
            boolean draw2 = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return draw2;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.flickerDrawable.setParentWidth(getMeasuredWidth());
    }

    public void setWebViewProgressListener(Consumer<Float> webViewProgressListener2) {
        this.webViewProgressListener = webViewProgressListener2;
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void loadFlickerAndSettingsItem(int currentAccount2, long botId, ActionBarMenuSubItem settingsItem) {
        TLRPC.User user = MessagesController.getInstance(currentAccount2).getUser(Long.valueOf(botId));
        int i = 0;
        if (user.username == null || !ColorUtils$$ExternalSyntheticBackport0.m(user.username, "DurgerKingBot")) {
            TLRPC.TL_attachMenuBot cachedBot = null;
            Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(currentAccount2).getAttachMenuBots().bots.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TLRPC.TL_attachMenuBot bot = it.next();
                if (bot.bot_id == botId) {
                    cachedBot = bot;
                    break;
                }
            }
            if (cachedBot != null) {
                boolean center = false;
                TLRPC.TL_attachMenuBotIcon botIcon = MediaDataController.getPlaceholderStaticAttachMenuBotIcon(cachedBot);
                if (botIcon == null) {
                    botIcon = MediaDataController.getStaticAttachMenuBotIcon(cachedBot);
                    center = true;
                }
                if (botIcon != null) {
                    this.flickerView.setVisibility(0);
                    this.flickerView.setAlpha(1.0f);
                    this.flickerView.setImage(ImageLocation.getForDocument(botIcon.icon), (String) null, (Drawable) null, (Object) cachedBot);
                    setupFlickerParams(center);
                }
                if (settingsItem != null) {
                    if (!cachedBot.has_settings) {
                        i = 8;
                    }
                    settingsItem.setVisibility(i);
                    return;
                }
                return;
            }
            TLRPC.TL_messages_getAttachMenuBot req = new TLRPC.TL_messages_getAttachMenuBot();
            req.bot = MessagesController.getInstance(currentAccount2).getInputUser(botId);
            ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new BotWebViewContainer$$ExternalSyntheticLambda8(this, settingsItem));
            return;
        }
        this.flickerView.setVisibility(0);
        this.flickerView.setAlpha(1.0f);
        this.flickerView.setImageDrawable(SvgHelper.getDrawable(NUM, getColor("windowBackgroundGray")));
        setupFlickerParams(false);
    }

    /* renamed from: lambda$loadFlickerAndSettingsItem$5$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m578xcCLASSNAMEe9(ActionBarMenuSubItem settingsItem, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda5(this, response, settingsItem));
    }

    /* renamed from: lambda$loadFlickerAndSettingsItem$4$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m577x927e940a(TLObject response, ActionBarMenuSubItem settingsItem) {
        int i = 8;
        if (response instanceof TLRPC.TL_attachMenuBotsBot) {
            TLRPC.TL_attachMenuBot bot = ((TLRPC.TL_attachMenuBotsBot) response).bot;
            boolean center = false;
            TLRPC.TL_attachMenuBotIcon botIcon = MediaDataController.getPlaceholderStaticAttachMenuBotIcon(bot);
            if (botIcon == null) {
                botIcon = MediaDataController.getStaticAttachMenuBotIcon(bot);
                center = true;
            }
            if (botIcon != null) {
                this.flickerView.setVisibility(0);
                this.flickerView.setAlpha(1.0f);
                this.flickerView.setImage(ImageLocation.getForDocument(botIcon.icon), (String) null, (Drawable) null, (Object) bot);
                setupFlickerParams(center);
            }
            if (settingsItem != null) {
                if (bot.has_settings) {
                    i = 0;
                }
                settingsItem.setVisibility(i);
            }
        } else if (settingsItem != null) {
            settingsItem.setVisibility(8);
        }
    }

    private void setupFlickerParams(boolean center) {
        this.isFlickeringCenter = center;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.flickerView.getLayoutParams();
        params.gravity = center ? 17 : 48;
        if (center) {
            int dp = AndroidUtilities.dp(64.0f);
            params.height = dp;
            params.width = dp;
        } else {
            params.width = -1;
            params.height = -2;
        }
        this.flickerView.requestLayout();
    }

    public void reload() {
        checkCreateWebView();
        this.isPageLoaded = false;
        this.lastClickMs = 0;
        this.hasUserPermissions = false;
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.reload();
        }
    }

    public void loadUrl(int currentAccount2, String url) {
        checkCreateWebView();
        this.currentAccount = currentAccount2;
        this.isPageLoaded = false;
        this.lastClickMs = 0;
        this.hasUserPermissions = false;
        this.mUrl = url;
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.loadUrl(url);
        }
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
        WebView webView2 = this.webView;
        if (webView2 != null) {
            if (webView2.getParent() != null) {
                removeView(this.webView);
            }
            this.webView.destroy();
        }
    }

    public boolean isBackButtonVisible() {
        return this.isBackButtonVisible;
    }

    public void evaluateJs(String script) {
        checkCreateWebView();
        if (this.webView != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                this.webView.evaluateJavascript(script, BotWebViewContainer$$ExternalSyntheticLambda2.INSTANCE);
                return;
            }
            try {
                WebView webView2 = this.webView;
                webView2.loadUrl("javascript:" + URLEncoder.encode(script, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                WebView webView3 = this.webView;
                webView3.loadUrl("javascript:" + URLEncoder.encode(script));
            }
        }
    }

    static /* synthetic */ void lambda$evaluateJs$6(String value) {
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetNewTheme) {
            WebView webView2 = this.webView;
            if (webView2 != null) {
                webView2.setBackgroundColor(getColor("windowBackgroundWhite"));
            }
            this.flickerView.setColorFilter(new PorterDuffColorFilter(getColor("dialogSearchHint"), PorterDuff.Mode.SRC_IN));
            notifyThemeChanged();
        } else if (id == NotificationCenter.onActivityResultReceived) {
            onActivityResult(args[0].intValue(), args[1].intValue(), args[2]);
        } else if (id == NotificationCenter.onRequestPermissionResultReceived) {
            onRequestPermissionsResult(args[0].intValue(), args[1], args[2]);
        }
    }

    private void notifyThemeChanged() {
        notifyEvent("theme_changed", buildThemeParams());
    }

    private void notifyEvent(String event, JSONObject eventData) {
        evaluateJs("window.Telegram.WebView.receiveEvent('" + event + "', " + eventData + ");");
    }

    public void setWebViewScrollListener(WebViewScrollListener webViewScrollListener2) {
        this.webViewScrollListener = webViewScrollListener2;
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onEventReceived(java.lang.String r21, java.lang.String r22) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            java.lang.String r0 = "text_color"
            android.webkit.WebView r4 = r1.webView
            if (r4 == 0) goto L_0x0394
            org.telegram.ui.Components.BotWebViewContainer$Delegate r4 = r1.delegate
            if (r4 != 0) goto L_0x0012
            goto L_0x0394
        L_0x0012:
            int r4 = r21.hashCode()
            r5 = 4
            r6 = 3
            r7 = 2
            r8 = -1
            r9 = 0
            r10 = 1
            switch(r4) {
                case -1717314938: goto L_0x00ad;
                case -1390641887: goto L_0x00a2;
                case -1259935152: goto L_0x0097;
                case -921083201: goto L_0x008c;
                case -439770054: goto L_0x0082;
                case -71726289: goto L_0x0078;
                case -58095910: goto L_0x006d;
                case 668142772: goto L_0x0063;
                case 1011447167: goto L_0x0059;
                case 1273834781: goto L_0x004f;
                case 1398490221: goto L_0x0043;
                case 1917103703: goto L_0x0038;
                case 2001330488: goto L_0x002d;
                case 2139805763: goto L_0x0021;
                default: goto L_0x001f;
            }
        L_0x001f:
            goto L_0x00b7
        L_0x0021:
            java.lang.String r4 = "web_app_expand"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 9
            goto L_0x00b8
        L_0x002d:
            java.lang.String r4 = "web_app_set_background_color"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 1
            goto L_0x00b8
        L_0x0038:
            java.lang.String r4 = "web_app_set_header_color"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 2
            goto L_0x00b8
        L_0x0043:
            java.lang.String r4 = "web_app_setup_main_button"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 13
            goto L_0x00b8
        L_0x004f:
            java.lang.String r4 = "web_app_trigger_haptic_feedback"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 4
            goto L_0x00b8
        L_0x0059:
            java.lang.String r4 = "web_app_setup_back_button"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 7
            goto L_0x00b8
        L_0x0063:
            java.lang.String r4 = "web_app_data_send"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 3
            goto L_0x00b8
        L_0x006d:
            java.lang.String r4 = "web_app_ready"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 12
            goto L_0x00b8
        L_0x0078:
            java.lang.String r4 = "web_app_close"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 0
            goto L_0x00b8
        L_0x0082:
            java.lang.String r4 = "web_app_open_tg_link"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 6
            goto L_0x00b8
        L_0x008c:
            java.lang.String r4 = "web_app_request_viewport"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 10
            goto L_0x00b8
        L_0x0097:
            java.lang.String r4 = "web_app_request_theme"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 11
            goto L_0x00b8
        L_0x00a2:
            java.lang.String r4 = "web_app_open_invoice"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 8
            goto L_0x00b8
        L_0x00ad:
            java.lang.String r4 = "web_app_open_link"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x001f
            r4 = 5
            goto L_0x00b8
        L_0x00b7:
            r4 = -1
        L_0x00b8:
            java.lang.String r11 = "is_visible"
            java.lang.String r12 = "color"
            switch(r4) {
                case 0: goto L_0x038c;
                case 1: goto L_0x0371;
                case 2: goto L_0x0331;
                case 3: goto L_0x031a;
                case 4: goto L_0x0223;
                case 5: goto L_0x01fd;
                case 6: goto L_0x01c5;
                case 7: goto L_0x01a9;
                case 8: goto L_0x016c;
                case 9: goto L_0x0165;
                case 10: goto L_0x0146;
                case 11: goto L_0x0141;
                case 12: goto L_0x0136;
                case 13: goto L_0x00c1;
                default: goto L_0x00bf;
            }
        L_0x00bf:
            goto L_0x0393
        L_0x00c1:
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            r4.<init>(r3)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            java.lang.String r5 = "is_active"
            boolean r15 = r4.optBoolean(r5, r9)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            java.lang.String r5 = "text"
            java.lang.String r6 = r1.lastButtonText     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            java.lang.String r5 = r4.optString(r5, r6)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            java.lang.String r5 = r5.trim()     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            boolean r6 = r4.optBoolean(r11, r9)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            if (r6 == 0) goto L_0x00e6
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            if (r6 != 0) goto L_0x00e6
            r6 = 1
            goto L_0x00e7
        L_0x00e6:
            r6 = 0
        L_0x00e7:
            boolean r7 = r4.has(r12)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            if (r7 == 0) goto L_0x00f6
            java.lang.String r7 = r4.optString(r12)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            goto L_0x00f8
        L_0x00f6:
            int r7 = r1.lastButtonColor     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
        L_0x00f8:
            boolean r8 = r4.has(r0)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            if (r8 == 0) goto L_0x0107
            java.lang.String r0 = r4.optString(r0)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            int r0 = android.graphics.Color.parseColor(r0)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            goto L_0x0109
        L_0x0107:
            int r0 = r1.lastButtonTextColor     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
        L_0x0109:
            java.lang.String r8 = "is_progress_visible"
            boolean r8 = r4.optBoolean(r8, r9)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            if (r8 == 0) goto L_0x0116
            if (r6 == 0) goto L_0x0116
            r19 = 1
            goto L_0x0118
        L_0x0116:
            r19 = 0
        L_0x0118:
            r1.lastButtonColor = r7     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            r1.lastButtonTextColor = r0     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            r1.lastButtonText = r5     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            r1.buttonData = r3     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r13 = r1.delegate     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            r14 = r6
            r16 = r5
            r17 = r7
            r18 = r0
            r13.onSetupMainButton(r14, r15, r16, r17, r18, r19)     // Catch:{ JSONException -> 0x0130, IllegalArgumentException -> 0x012e }
            goto L_0x0393
        L_0x012e:
            r0 = move-exception
            goto L_0x0131
        L_0x0130:
            r0 = move-exception
        L_0x0131:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x0136:
            android.webkit.WebView r0 = r1.webView
            java.lang.String r0 = r0.getUrl()
            r1.setPageLoaded(r0)
            goto L_0x0393
        L_0x0141:
            r20.notifyThemeChanged()
            goto L_0x0393
        L_0x0146:
            android.view.ViewParent r0 = r20.getParent()
            boolean r0 = r0 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer
            if (r0 == 0) goto L_0x015c
            android.view.ViewParent r0 = r20.getParent()
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r0 = (org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) r0
            boolean r0 = r0.isSwipeInProgress()
            if (r0 == 0) goto L_0x015c
            r0 = 1
            goto L_0x015d
        L_0x015c:
            r0 = 0
        L_0x015d:
            if (r0 != 0) goto L_0x0160
            r9 = 1
        L_0x0160:
            r1.invalidateViewPortHeight(r9, r10)
            goto L_0x0393
        L_0x0165:
            org.telegram.ui.Components.BotWebViewContainer$Delegate r0 = r1.delegate
            r0.onWebAppExpand()
            goto L_0x0393
        L_0x016c:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x01a3 }
            r0.<init>(r3)     // Catch:{ JSONException -> 0x01a3 }
            java.lang.String r4 = "slug"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ JSONException -> 0x01a3 }
            java.lang.String r5 = r1.currentPaymentSlug     // Catch:{ JSONException -> 0x01a3 }
            if (r5 == 0) goto L_0x0182
            java.lang.String r5 = "cancelled"
            r1.onInvoiceStatusUpdate(r4, r5, r10)     // Catch:{ JSONException -> 0x01a3 }
            goto L_0x0393
        L_0x0182:
            r1.currentPaymentSlug = r4     // Catch:{ JSONException -> 0x01a3 }
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r5 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm     // Catch:{ JSONException -> 0x01a3 }
            r5.<init>()     // Catch:{ JSONException -> 0x01a3 }
            org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug r6 = new org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug     // Catch:{ JSONException -> 0x01a3 }
            r6.<init>()     // Catch:{ JSONException -> 0x01a3 }
            r6.slug = r4     // Catch:{ JSONException -> 0x01a3 }
            r5.invoice = r6     // Catch:{ JSONException -> 0x01a3 }
            int r7 = r1.currentAccount     // Catch:{ JSONException -> 0x01a3 }
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)     // Catch:{ JSONException -> 0x01a3 }
            org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda7 r8 = new org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda7     // Catch:{ JSONException -> 0x01a3 }
            r8.<init>(r1, r4)     // Catch:{ JSONException -> 0x01a3 }
            r7.sendRequest(r5, r8)     // Catch:{ JSONException -> 0x01a3 }
            goto L_0x0393
        L_0x01a3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x01a9:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x01bf }
            r0.<init>(r3)     // Catch:{ JSONException -> 0x01bf }
            boolean r4 = r0.optBoolean(r11)     // Catch:{ JSONException -> 0x01bf }
            boolean r5 = r1.isBackButtonVisible     // Catch:{ JSONException -> 0x01bf }
            if (r4 == r5) goto L_0x01bd
            r1.isBackButtonVisible = r4     // Catch:{ JSONException -> 0x01bf }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r5 = r1.delegate     // Catch:{ JSONException -> 0x01bf }
            r5.onSetBackButtonVisible(r4)     // Catch:{ JSONException -> 0x01bf }
        L_0x01bd:
            goto L_0x0393
        L_0x01bf:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x01c5:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x01f7 }
            r0.<init>(r3)     // Catch:{ JSONException -> 0x01f7 }
            java.lang.String r4 = "path_full"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ JSONException -> 0x01f7 }
            java.lang.String r5 = "/"
            boolean r5 = r4.startsWith(r5)     // Catch:{ JSONException -> 0x01f7 }
            if (r5 == 0) goto L_0x01dd
            java.lang.String r5 = r4.substring(r10)     // Catch:{ JSONException -> 0x01f7 }
            r4 = r5
        L_0x01dd:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x01f7 }
            r5.<init>()     // Catch:{ JSONException -> 0x01f7 }
            java.lang.String r6 = "https://t.me/"
            r5.append(r6)     // Catch:{ JSONException -> 0x01f7 }
            r5.append(r4)     // Catch:{ JSONException -> 0x01f7 }
            java.lang.String r5 = r5.toString()     // Catch:{ JSONException -> 0x01f7 }
            android.net.Uri r5 = android.net.Uri.parse(r5)     // Catch:{ JSONException -> 0x01f7 }
            r1.onOpenUri(r5)     // Catch:{ JSONException -> 0x01f7 }
            goto L_0x0393
        L_0x01f7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x01fd:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x021d }
            r0.<init>(r3)     // Catch:{ Exception -> 0x021d }
            java.lang.String r4 = "url"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ Exception -> 0x021d }
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x021d }
            java.util.List<java.lang.String> r5 = WHITELISTED_SCHEMES     // Catch:{ Exception -> 0x021d }
            java.lang.String r6 = r4.getScheme()     // Catch:{ Exception -> 0x021d }
            boolean r5 = r5.contains(r6)     // Catch:{ Exception -> 0x021d }
            if (r5 == 0) goto L_0x021b
            r1.onOpenUri(r4, r10)     // Catch:{ Exception -> 0x021d }
        L_0x021b:
            goto L_0x0393
        L_0x021d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x0223:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0314 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x0314 }
            java.lang.String r4 = "type"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ Exception -> 0x0314 }
            r11 = 0
            int r12 = r4.hashCode()     // Catch:{ Exception -> 0x0314 }
            switch(r12) {
                case -1184809658: goto L_0x024b;
                case 193071555: goto L_0x0241;
                case 595233003: goto L_0x0237;
                default: goto L_0x0236;
            }     // Catch:{ Exception -> 0x0314 }
        L_0x0236:
            goto L_0x0255
        L_0x0237:
            java.lang.String r12 = "notification"
            boolean r12 = r4.equals(r12)     // Catch:{ Exception -> 0x0314 }
            if (r12 == 0) goto L_0x0236
            r12 = 1
            goto L_0x0256
        L_0x0241:
            java.lang.String r12 = "selection_change"
            boolean r12 = r4.equals(r12)     // Catch:{ Exception -> 0x0314 }
            if (r12 == 0) goto L_0x0236
            r12 = 2
            goto L_0x0256
        L_0x024b:
            java.lang.String r12 = "impact"
            boolean r12 = r4.equals(r12)     // Catch:{ Exception -> 0x0314 }
            if (r12 == 0) goto L_0x0236
            r12 = 0
            goto L_0x0256
        L_0x0255:
            r12 = -1
        L_0x0256:
            switch(r12) {
                case 0: goto L_0x029d;
                case 1: goto L_0x0260;
                case 2: goto L_0x025b;
                default: goto L_0x0259;
            }     // Catch:{ Exception -> 0x0314 }
        L_0x0259:
            goto L_0x02f5
        L_0x025b:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.SELECTION_CHANGE     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x02f5
        L_0x0260:
            java.lang.String r5 = "notification_type"
            java.lang.String r5 = r0.optString(r5)     // Catch:{ Exception -> 0x0314 }
            int r6 = r5.hashCode()     // Catch:{ Exception -> 0x0314 }
            switch(r6) {
                case -1867169789: goto L_0x0281;
                case 96784904: goto L_0x0277;
                case 1124446108: goto L_0x026e;
                default: goto L_0x026d;
            }     // Catch:{ Exception -> 0x0314 }
        L_0x026d:
            goto L_0x028b
        L_0x026e:
            java.lang.String r6 = "warning"
            boolean r5 = r5.equals(r6)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x026d
            goto L_0x028c
        L_0x0277:
            java.lang.String r6 = "error"
            boolean r5 = r5.equals(r6)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x026d
            r7 = 0
            goto L_0x028c
        L_0x0281:
            java.lang.String r6 = "success"
            boolean r5 = r5.equals(r6)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x026d
            r7 = 1
            goto L_0x028c
        L_0x028b:
            r7 = -1
        L_0x028c:
            switch(r7) {
                case 0: goto L_0x0298;
                case 1: goto L_0x0294;
                case 2: goto L_0x0290;
                default: goto L_0x028f;
            }     // Catch:{ Exception -> 0x0314 }
        L_0x028f:
            goto L_0x029b
        L_0x0290:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.NOTIFICATION_WARNING     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x029b
        L_0x0294:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.NOTIFICATION_SUCCESS     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x029b
        L_0x0298:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.NOTIFICATION_ERROR     // Catch:{ Exception -> 0x0314 }
            r11 = r5
        L_0x029b:
            goto L_0x02f5
        L_0x029d:
            java.lang.String r12 = "impact_style"
            java.lang.String r12 = r0.optString(r12)     // Catch:{ Exception -> 0x0314 }
            int r13 = r12.hashCode()     // Catch:{ Exception -> 0x0314 }
            switch(r13) {
                case -1078030475: goto L_0x02d2;
                case 3535914: goto L_0x02c9;
                case 99152071: goto L_0x02bf;
                case 102970646: goto L_0x02b5;
                case 108511787: goto L_0x02ab;
                default: goto L_0x02aa;
            }     // Catch:{ Exception -> 0x0314 }
        L_0x02aa:
            goto L_0x02dc
        L_0x02ab:
            java.lang.String r5 = "rigid"
            boolean r5 = r12.equals(r5)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x02aa
            r5 = 3
            goto L_0x02dd
        L_0x02b5:
            java.lang.String r5 = "light"
            boolean r5 = r12.equals(r5)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x02aa
            r5 = 0
            goto L_0x02dd
        L_0x02bf:
            java.lang.String r5 = "heavy"
            boolean r5 = r12.equals(r5)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x02aa
            r5 = 2
            goto L_0x02dd
        L_0x02c9:
            java.lang.String r6 = "soft"
            boolean r6 = r12.equals(r6)     // Catch:{ Exception -> 0x0314 }
            if (r6 == 0) goto L_0x02aa
            goto L_0x02dd
        L_0x02d2:
            java.lang.String r5 = "medium"
            boolean r5 = r12.equals(r5)     // Catch:{ Exception -> 0x0314 }
            if (r5 == 0) goto L_0x02aa
            r5 = 1
            goto L_0x02dd
        L_0x02dc:
            r5 = -1
        L_0x02dd:
            switch(r5) {
                case 0: goto L_0x02f1;
                case 1: goto L_0x02ed;
                case 2: goto L_0x02e9;
                case 3: goto L_0x02e5;
                case 4: goto L_0x02e1;
                default: goto L_0x02e0;
            }     // Catch:{ Exception -> 0x0314 }
        L_0x02e0:
            goto L_0x02f4
        L_0x02e1:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_SOFT     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x02f4
        L_0x02e5:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_RIGID     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x02f4
        L_0x02e9:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_HEAVY     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x02f4
        L_0x02ed:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_MEDIUM     // Catch:{ Exception -> 0x0314 }
            r11 = r5
            goto L_0x02f4
        L_0x02f1:
            org.telegram.messenger.BotWebViewVibrationEffect r5 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_LIGHT     // Catch:{ Exception -> 0x0314 }
            r11 = r5
        L_0x02f4:
        L_0x02f5:
            if (r11 == 0) goto L_0x0312
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0314 }
            r6 = 26
            if (r5 < r6) goto L_0x0309
            android.os.Vibrator r5 = org.telegram.messenger.AndroidUtilities.getVibrator()     // Catch:{ Exception -> 0x0314 }
            android.os.VibrationEffect r6 = r11.getVibrationEffectForOreo()     // Catch:{ Exception -> 0x0314 }
            r5.vibrate(r6)     // Catch:{ Exception -> 0x0314 }
            goto L_0x0312
        L_0x0309:
            android.os.Vibrator r5 = org.telegram.messenger.AndroidUtilities.getVibrator()     // Catch:{ Exception -> 0x0314 }
            long[] r6 = r11.fallbackTimings     // Catch:{ Exception -> 0x0314 }
            r5.vibrate(r6, r8)     // Catch:{ Exception -> 0x0314 }
        L_0x0312:
            goto L_0x0393
        L_0x0314:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x031a:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x032c }
            r0.<init>(r3)     // Catch:{ JSONException -> 0x032c }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r4 = r1.delegate     // Catch:{ JSONException -> 0x032c }
            java.lang.String r5 = "data"
            java.lang.String r5 = r0.optString(r5)     // Catch:{ JSONException -> 0x032c }
            r4.onSendWebViewData(r5)     // Catch:{ JSONException -> 0x032c }
            goto L_0x0393
        L_0x032c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x0331:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x036c }
            r0.<init>(r3)     // Catch:{ JSONException -> 0x036c }
            java.lang.String r4 = "color_key"
            java.lang.String r4 = r0.getString(r4)     // Catch:{ JSONException -> 0x036c }
            r5 = 0
            int r6 = r4.hashCode()     // Catch:{ JSONException -> 0x036c }
            switch(r6) {
                case -1265068311: goto L_0x034f;
                case -210781868: goto L_0x0345;
                default: goto L_0x0344;
            }     // Catch:{ JSONException -> 0x036c }
        L_0x0344:
            goto L_0x0358
        L_0x0345:
            java.lang.String r6 = "secondary_bg_color"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x036c }
            if (r6 == 0) goto L_0x0344
            r8 = 1
            goto L_0x0358
        L_0x034f:
            java.lang.String r6 = "bg_color"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x036c }
            if (r6 == 0) goto L_0x0344
            r8 = 0
        L_0x0358:
            switch(r8) {
                case 0: goto L_0x0360;
                case 1: goto L_0x035c;
                default: goto L_0x035b;
            }     // Catch:{ JSONException -> 0x036c }
        L_0x035b:
            goto L_0x0364
        L_0x035c:
            java.lang.String r6 = "windowBackgroundGray"
            r5 = r6
            goto L_0x0364
        L_0x0360:
            java.lang.String r6 = "windowBackgroundWhite"
            r5 = r6
        L_0x0364:
            if (r5 == 0) goto L_0x036b
            org.telegram.ui.Components.BotWebViewContainer$Delegate r6 = r1.delegate     // Catch:{ JSONException -> 0x036c }
            r6.onWebAppSetActionBarColor(r5)     // Catch:{ JSONException -> 0x036c }
        L_0x036b:
            goto L_0x0393
        L_0x036c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x0371:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0387 }
            r0.<init>(r3)     // Catch:{ JSONException -> 0x0387 }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r4 = r1.delegate     // Catch:{ JSONException -> 0x0387 }
            java.lang.String r5 = r0.optString(r12)     // Catch:{ JSONException -> 0x0387 }
            int r5 = android.graphics.Color.parseColor(r5)     // Catch:{ JSONException -> 0x0387 }
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r5 = r5 | r6
            r4.onWebAppSetBackgroundColor(r5)     // Catch:{ JSONException -> 0x0387 }
            goto L_0x0393
        L_0x0387:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0393
        L_0x038c:
            org.telegram.ui.Components.BotWebViewContainer$Delegate r0 = r1.delegate
            r4 = 0
            r0.onCloseRequested(r4)
        L_0x0393:
            return
        L_0x0394:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewContainer.onEventReceived(java.lang.String, java.lang.String):void");
    }

    /* renamed from: lambda$onEventReceived$8$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m580x7d6dddff(String slug, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda6(this, error, slug, response));
    }

    /* renamed from: lambda$onEventReceived$7$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m579x43a33CLASSNAME(TLRPC.TL_error error, String slug, TLObject response) {
        if (error != null) {
            onInvoiceStatusUpdate(slug, "failed");
        } else {
            this.delegate.onWebAppOpenInvoice(slug, response);
        }
    }

    private JSONObject buildThemeParams() {
        try {
            JSONObject object = new JSONObject();
            object.put("bg_color", formatColor("windowBackgroundWhite"));
            object.put("secondary_bg_color", formatColor("windowBackgroundGray"));
            object.put("text_color", formatColor("windowBackgroundWhiteBlackText"));
            object.put("hint_color", formatColor("windowBackgroundWhiteHintText"));
            object.put("link_color", formatColor("windowBackgroundWhiteLinkText"));
            object.put("button_color", formatColor("featuredStickers_addButton"));
            object.put("button_text_color", formatColor("featuredStickers_buttonText"));
            return new JSONObject().put("theme_params", object);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new JSONObject();
        }
    }

    private int getColor(String colorKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = Integer.valueOf(resourcesProvider2 != null ? resourcesProvider2.getColor(colorKey).intValue() : Theme.getColor(colorKey));
        if (color == null) {
            color = Integer.valueOf(Theme.getColor(colorKey));
        }
        return color.intValue();
    }

    private String formatColor(String colorKey) {
        int color = getColor(colorKey);
        return "#" + hexFixed(Color.red(color)) + hexFixed(Color.green(color)) + hexFixed(Color.blue(color));
    }

    private String hexFixed(int h) {
        String hex = Integer.toHexString(h);
        if (hex.length() >= 2) {
            return hex;
        }
        return "0" + hex;
    }

    private class WebViewProxy {
        private WebViewProxy() {
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-Components-BotWebViewContainer$WebViewProxy  reason: not valid java name */
        public /* synthetic */ void m592x6a2a9d10(String eventType, String eventData) {
            BotWebViewContainer.this.onEventReceived(eventType, eventData);
        }

        @JavascriptInterface
        public void postEvent(String eventType, String eventData) {
            AndroidUtilities.runOnUIThread(new BotWebViewContainer$WebViewProxy$$ExternalSyntheticLambda0(this, eventType, eventData));
        }
    }

    public interface Delegate {
        void onCloseRequested(Runnable runnable);

        void onSendWebViewData(String str);

        void onSetBackButtonVisible(boolean z);

        void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3);

        void onWebAppExpand();

        void onWebAppOpenInvoice(String str, TLObject tLObject);

        void onWebAppReady();

        void onWebAppSetActionBarColor(String str);

        void onWebAppSetBackgroundColor(int i);

        /* renamed from: org.telegram.ui.Components.BotWebViewContainer$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSendWebViewData(Delegate _this, String data) {
            }

            public static void $default$onWebAppReady(Delegate _this) {
            }
        }
    }
}
