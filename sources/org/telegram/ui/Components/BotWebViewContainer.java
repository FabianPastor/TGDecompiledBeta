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
import android.text.TextUtils;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class BotWebViewContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String DURGER_KING_USERNAME = "DurgerKingBot";
    private static final int REQUEST_CODE_WEB_PERMISSION = 4000;
    private static final int REQUEST_CODE_WEB_VIEW_FILE = 3000;
    private static final boolean WEB_VIEW_CAN_GO_BACK = false;
    /* access modifiers changed from: private */
    public static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[]{"http", "https"});
    /* access modifiers changed from: private */
    public TLRPC.User botUser;
    private String buttonData;
    /* access modifiers changed from: private */
    public Delegate delegate;
    private CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();
    /* access modifiers changed from: private */
    public BackupImageView flickerView;
    /* access modifiers changed from: private */
    public boolean hasUserPermissions;
    /* access modifiers changed from: private */
    public boolean isFlickeringCenter;
    /* access modifiers changed from: private */
    public boolean isPageLoaded;
    private int lastButtonColor = getColor("featuredStickers_addButton");
    private String lastButtonText = "";
    private int lastButtonTextColor = getColor("featuredStickers_buttonText");
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
    /* access modifiers changed from: private */
    public WebView webView;
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
                    public /* synthetic */ void m3638x2638d59d(ValueAnimator animation) {
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
                if (!Browser.isInternalUri(uriNew, new boolean[]{false})) {
                    new AlertDialog.Builder(BotWebViewContainer.this.getContext(), BotWebViewContainer.this.resourcesProvider).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.formatString(NUM, uriNew.toString())).setPositiveButton(LocaleController.getString(NUM), new BotWebViewContainer$3$$ExternalSyntheticLambda0(this, uriNew)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).show();
                    return true;
                } else if (BotWebViewContainer.this.delegate != null) {
                    BotWebViewContainer.this.setDescendantFocusability(393216);
                    BotWebViewContainer.this.setFocusable(false);
                    BotWebViewContainer.this.webView.setFocusable(false);
                    BotWebViewContainer.this.webView.setDescendantFocusability(393216);
                    BotWebViewContainer.this.webView.clearFocus();
                    ((InputMethodManager) BotWebViewContainer.this.getContext().getSystemService("input_method")).hideSoftInputFromWindow(BotWebViewContainer.this.getWindowToken(), 2);
                    BotWebViewContainer.this.delegate.onCloseRequested(new BotWebViewContainer$3$$ExternalSyntheticLambda1(this, uriNew));
                    return true;
                } else {
                    Browser.openUrl(BotWebViewContainer.this.getContext(), uriNew, true, false);
                    return true;
                }
            }

            /* renamed from: lambda$shouldOverrideUrlLoading$0$org-telegram-ui-Components-BotWebViewContainer$3  reason: not valid java name */
            public /* synthetic */ void m3639x6avar_ac(Uri uriNew) {
                Browser.openUrl(BotWebViewContainer.this.getContext(), uriNew, true, false);
            }

            /* renamed from: lambda$shouldOverrideUrlLoading$1$org-telegram-ui-Components-BotWebViewContainer$3  reason: not valid java name */
            public /* synthetic */ void m3640x5c9CLASSNAMEcb(Uri uriNew, DialogInterface dialog, int which) {
                Browser.openUrl(BotWebViewContainer.this.getContext(), uriNew, true, false);
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
            public /* synthetic */ void m3642x53986a9a(GeolocationPermissions.Callback callback, String origin, Boolean allow) {
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
            public /* synthetic */ void m3641x61eeCLASSNAMEb(GeolocationPermissions.Callback callback, String origin, Boolean allowSystem) {
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
            public /* synthetic */ void m3644x14aacaef(PermissionRequest request, String resource, Boolean allow) {
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
            public /* synthetic */ void m3643x230124d0(PermissionRequest request, String resource, Boolean allowSystem) {
                if (allowSystem.booleanValue()) {
                    request.grant(new String[]{resource});
                    boolean unused = BotWebViewContainer.this.hasUserPermissions = true;
                    return;
                }
                request.deny();
            }

            /* renamed from: lambda$onPermissionRequest$5$org-telegram-ui-Components-BotWebViewContainer$4  reason: not valid java name */
            public /* synthetic */ void m3646xf7fe172d(PermissionRequest request, String resource, Boolean allow) {
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
            public /* synthetic */ void m3645x654710e(PermissionRequest request, String resource, Boolean allowSystem) {
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
        return false;
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
            this.onPermissionsRequestResultCallback = new BotWebViewContainer$$ExternalSyntheticLambda1(this, callback, permissions);
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.requestPermissions(permissions, 4000);
            }
        }
    }

    /* renamed from: lambda$runWithPermissions$0$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m3637x9fe69576(Consumer callback, String[] permissions) {
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

    public void onMainButtonPressed() {
        evaluateJs("window.Telegram.WebView.receiveEvent('main_button_pressed', null);");
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
        invalidateViewPortHeight(true);
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
                evaluateJs("window.Telegram.WebView.receiveEvent('viewport_changed', " + data + ");");
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
            this.flickerDrawable.draw(canvas, AndroidUtilities.rectTmp, 0.0f);
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

    public void loadFlicker(int currentAccount, long botId) {
        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(botId));
        if (user.username == null || !ColorUtils$$ExternalSyntheticBackport0.m(user.username, "DurgerKingBot")) {
            TLRPC.TL_attachMenuBot cachedBot = null;
            Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(currentAccount).getAttachMenuBots().bots.iterator();
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
                    return;
                }
                return;
            }
            TLRPC.TL_messages_getAttachMenuBot req = new TLRPC.TL_messages_getAttachMenuBot();
            req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new BotWebViewContainer$$ExternalSyntheticLambda3(this));
            return;
        }
        this.flickerView.setVisibility(0);
        this.flickerView.setAlpha(1.0f);
        this.flickerView.setImageDrawable(SvgHelper.getDrawable(NUM, getColor("windowBackgroundGray")));
        setupFlickerParams(false);
    }

    /* renamed from: lambda$loadFlicker$2$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m3636xc5b228cf(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda2(this, response));
    }

    /* renamed from: lambda$loadFlicker$1$org-telegram-ui-Components-BotWebViewContainer  reason: not valid java name */
    public /* synthetic */ void m3635x8be786f0(TLObject response) {
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
        this.hasUserPermissions = false;
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.reload();
        }
    }

    public void loadUrl(String url) {
        checkCreateWebView();
        this.isPageLoaded = false;
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
            webView2.destroy();
        }
    }

    public void evaluateJs(String script) {
        checkCreateWebView();
        if (this.webView != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                this.webView.evaluateJavascript(script, BotWebViewContainer$$ExternalSyntheticLambda0.INSTANCE);
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

    static /* synthetic */ void lambda$evaluateJs$3(String value) {
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
        evaluateJs("window.Telegram.WebView.receiveEvent('theme_changed', {theme_params: " + buildThemeParams() + "});");
    }

    public void setWebViewScrollListener(WebViewScrollListener webViewScrollListener2) {
        this.webViewScrollListener = webViewScrollListener2;
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void onEventReceived(String eventType, String eventData) {
        String str = eventType;
        String str2 = eventData;
        if (this.webView != null && this.delegate != null) {
            char c = 65535;
            boolean z = false;
            switch (eventType.hashCode()) {
                case -1259935152:
                    if (str.equals("web_app_request_theme")) {
                        c = 4;
                        break;
                    }
                    break;
                case -921083201:
                    if (str.equals("web_app_request_viewport")) {
                        c = 3;
                        break;
                    }
                    break;
                case -71726289:
                    if (str.equals("web_app_close")) {
                        c = 0;
                        break;
                    }
                    break;
                case -58095910:
                    if (str.equals("web_app_ready")) {
                        c = 5;
                        break;
                    }
                    break;
                case 668142772:
                    if (str.equals("web_app_data_send")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1398490221:
                    if (str.equals("web_app_setup_main_button")) {
                        c = 6;
                        break;
                    }
                    break;
                case 2139805763:
                    if (str.equals("web_app_expand")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.delegate.onCloseRequested((Runnable) null);
                    return;
                case 1:
                    try {
                        this.delegate.onSendWebViewData(new JSONObject(str2).optString("data"));
                        return;
                    } catch (JSONException e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                case 2:
                    this.delegate.onWebAppExpand();
                    return;
                case 3:
                    if (!((getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) && ((ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent()).isSwipeInProgress())) {
                        z = true;
                    }
                    invalidateViewPortHeight(z, true);
                    return;
                case 4:
                    notifyThemeChanged();
                    return;
                case 5:
                    setPageLoaded(this.webView.getUrl());
                    return;
                case 6:
                    try {
                        JSONObject info = new JSONObject(str2);
                        boolean isActive = info.optBoolean("is_active", false);
                        String text = info.optString("text", this.lastButtonText).trim();
                        boolean isVisible = info.optBoolean("is_visible", false) && !TextUtils.isEmpty(text);
                        int color = info.has("color") ? Color.parseColor(info.optString("color")) : this.lastButtonColor;
                        int textColor = info.has("text_color") ? Color.parseColor(info.optString("text_color")) : this.lastButtonTextColor;
                        boolean isProgressVisible = info.optBoolean("is_progress_visible", false) && isVisible;
                        this.lastButtonColor = color;
                        this.lastButtonTextColor = textColor;
                        this.lastButtonText = text;
                        this.buttonData = str2;
                        this.delegate.onSetupMainButton(isVisible, isActive, text, color, textColor, isProgressVisible);
                        return;
                    } catch (IllegalArgumentException | JSONException e2) {
                        FileLog.e((Throwable) e2);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    private String buildThemeParams() {
        try {
            JSONObject object = new JSONObject();
            object.put("bg_color", formatColor("windowBackgroundWhite"));
            object.put("text_color", formatColor("windowBackgroundWhiteBlackText"));
            object.put("hint_color", formatColor("windowBackgroundWhiteHintText"));
            object.put("link_color", formatColor("windowBackgroundWhiteLinkText"));
            object.put("button_color", formatColor("featuredStickers_addButton"));
            object.put("button_text_color", formatColor("featuredStickers_buttonText"));
            return object.toString();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "{}";
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
        public /* synthetic */ void m3647x6a2a9d10(String eventType, String eventData) {
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

        void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3);

        void onWebAppExpand();

        void onWebAppReady();

        /* renamed from: org.telegram.ui.Components.BotWebViewContainer$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSendWebViewData(Delegate _this, String data) {
            }

            public static void $default$onWebAppReady(Delegate _this) {
            }
        }
    }
}
