package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import androidx.core.util.Consumer;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
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
    private int currentAccount;
    private AlertDialog currentDialog;
    private String currentPaymentSlug;
    private Delegate delegate;
    private int dialogSequentialOpenTimes;
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
    private long lastDialogClosed;
    private long lastDialogCooldownTime;
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

    public interface Delegate {

        /* renamed from: org.telegram.ui.Components.BotWebViewContainer$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSendWebViewData(Delegate delegate, String str) {
            }

            public static void $default$onWebAppReady(Delegate delegate) {
            }
        }

        void onCloseRequested(Runnable runnable);

        void onSendWebViewData(String str);

        void onSetBackButtonVisible(boolean z);

        void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3);

        void onWebAppExpand();

        void onWebAppOpenInvoice(String str, TLObject tLObject);

        void onWebAppReady();

        void onWebAppSetActionBarColor(String str);

        void onWebAppSetBackgroundColor(int i);

        void onWebAppSetupClosingBehavior(boolean z);
    }

    public interface WebViewScrollListener {
        void onWebViewScrolled(WebView webView, int i, int i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$evaluateJs$6(String str) {
    }

    public BotWebViewContainer(Context context, Theme.ResourcesProvider resourcesProvider2, int i) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        CellFlickerDrawable cellFlickerDrawable = this.flickerDrawable;
        cellFlickerDrawable.drawFrame = false;
        cellFlickerDrawable.setColors(i, 153, 204);
        AnonymousClass1 r6 = new BackupImageView(context) {
            {
                this.imageReceiver = new ImageReceiver(this) {
                    /* access modifiers changed from: protected */
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                        boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z, i2);
                        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                        duration.addUpdateListener(new BotWebViewContainer$1$1$$ExternalSyntheticLambda0(this));
                        duration.start();
                        return imageBitmapByKey;
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$setImageBitmapByKey$0(ValueAnimator valueAnimator) {
                        AnonymousClass1.this.imageReceiver.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
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
        this.flickerView = r6;
        r6.setColorFilter(new PorterDuffColorFilter(getColor("dialogSearchHint"), PorterDuff.Mode.SRC_IN));
        this.flickerView.getImageReceiver().setAspectFit(true);
        addView(this.flickerView, LayoutHelper.createFrame(-1, -2, 48));
        TextView textView = new TextView(context);
        this.webViewNotAvailableText = textView;
        textView.setText(LocaleController.getString(NUM));
        this.webViewNotAvailableText.setTextColor(getColor("windowBackgroundWhiteGrayText"));
        this.webViewNotAvailableText.setTextSize(1, 15.0f);
        this.webViewNotAvailableText.setGravity(17);
        this.webViewNotAvailableText.setVisibility(8);
        int dp = AndroidUtilities.dp(16.0f);
        this.webViewNotAvailableText.setPadding(dp, dp, dp, dp);
        addView(this.webViewNotAvailableText, LayoutHelper.createFrame(-1, -2, 17));
        setFocusable(false);
    }

    public void setViewPortByMeasureSuppressed(boolean z) {
        this.isViewPortByMeasureSuppressed = z;
    }

    private void checkCreateWebView() {
        if (this.webView == null && !this.webViewNotAvailable) {
            try {
                setupWebView();
            } catch (Throwable th) {
                FileLog.e(th);
                this.flickerView.setVisibility(8);
                this.webViewNotAvailable = true;
                this.webViewNotAvailableText.setVisibility(0);
                if (this.webView != null) {
                    removeView(this.webView);
                }
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
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

            public boolean onCheckIsTextEditor() {
                return BotWebViewContainer.this.isFocusable();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
            }

            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    long unused = BotWebViewContainer.this.lastClickMs = System.currentTimeMillis();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.webView = r0;
        r0.setBackgroundColor(getColor("windowBackgroundWhite"));
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        File file = new File(ApplicationLoader.getFilesDirFixed(), "webview_database");
        if ((file.exists() && file.isDirectory()) || file.mkdirs()) {
            settings.setDatabasePath(file.getAbsolutePath());
        }
        GeolocationPermissions.getInstance().clearAll();
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                Uri parse = Uri.parse(BotWebViewContainer.this.mUrl);
                Uri parse2 = Uri.parse(str);
                if (!BotWebViewContainer.this.isPageLoaded || (ObjectsCompat$$ExternalSyntheticBackport0.m(parse.getHost(), parse2.getHost()) && ObjectsCompat$$ExternalSyntheticBackport0.m(parse.getPath(), parse2.getPath()))) {
                    return false;
                }
                if (!BotWebViewContainer.WHITELISTED_SCHEMES.contains(parse2.getScheme())) {
                    return true;
                }
                BotWebViewContainer.this.onOpenUri(parse2);
                return true;
            }

            public void onPageFinished(WebView webView, String str) {
                BotWebViewContainer.this.setPageLoaded(str);
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
                Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$4$$ExternalSyntheticLambda1(this, callback, str));
                this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                createWebViewPermissionsRequestDialog.show();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$1(GeolocationPermissions.Callback callback, String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new BotWebViewContainer$4$$ExternalSyntheticLambda0(this, callback, str));
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
                        Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.CAMERA"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$4$$ExternalSyntheticLambda2(this, permissionRequest, str));
                        this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                        createWebViewPermissionsRequestDialog.show();
                    } else if (str.equals("android.webkit.resource.AUDIO_CAPTURE")) {
                        Dialog createWebViewPermissionsRequestDialog2 = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.RECORD_AUDIO"}, NUM, LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(NUM, UserObject.getUserName(BotWebViewContainer.this.botUser)), new BotWebViewContainer$4$$ExternalSyntheticLambda4(this, permissionRequest, str));
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
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.RECORD_AUDIO"}, new BotWebViewContainer$4$$ExternalSyntheticLambda5(this, permissionRequest, str));
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
                        BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.CAMERA"}, new BotWebViewContainer$4$$ExternalSyntheticLambda3(this, permissionRequest, str));
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

    /* access modifiers changed from: private */
    public void onOpenUri(Uri uri) {
        onOpenUri(uri, false);
    }

    private void onOpenUri(Uri uri, boolean z) {
        if (this.isRequestingPageOpen) {
            return;
        }
        if (System.currentTimeMillis() - this.lastClickMs <= 10000 || !z) {
            this.lastClickMs = 0;
            boolean[] zArr = {false};
            if (!Browser.isInternalUri(uri, zArr) || zArr[0]) {
                if (z) {
                    Browser.openUrl(getContext(), uri, true, false);
                    return;
                }
                this.isRequestingPageOpen = true;
                new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.formatString(NUM, uri.toString())).setPositiveButton(LocaleController.getString(NUM), new BotWebViewContainer$$ExternalSyntheticLambda0(this, uri)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new BotWebViewContainer$$ExternalSyntheticLambda4(this)).show();
            } else if (this.delegate != null) {
                setDescendantFocusability(393216);
                setFocusable(false);
                this.webView.setFocusable(false);
                this.webView.setDescendantFocusability(393216);
                this.webView.clearFocus();
                ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 2);
                this.delegate.onCloseRequested(new BotWebViewContainer$$ExternalSyntheticLambda7(this, uri));
            } else {
                Browser.openUrl(getContext(), uri, true, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onOpenUri$0(Uri uri) {
        Browser.openUrl(getContext(), uri, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onOpenUri$1(Uri uri, DialogInterface dialogInterface, int i) {
        Browser.openUrl(getContext(), uri, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onOpenUri$2(DialogInterface dialogInterface) {
        this.isRequestingPageOpen = false;
    }

    public static int getMainButtonRippleColor(int i) {
        return ColorUtils.calculateLuminance(i) >= 0.30000001192092896d ? NUM : NUM;
    }

    public static Drawable getMainButtonRippleDrawable(int i) {
        return Theme.createSelectorWithBackgroundDrawable(i, getMainButtonRippleColor(i));
    }

    public void updateFlickerBackgroundColor(int i) {
        this.flickerDrawable.setColors(i, 153, 204);
    }

    public boolean onBackPressed() {
        if (this.webView == null || !this.isBackButtonVisible) {
            return false;
        }
        notifyEvent("back_button_pressed", (JSONObject) null);
        return true;
    }

    /* access modifiers changed from: private */
    public void setPageLoaded(String str) {
        if (!this.isPageLoaded) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.webView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flickerView, View.ALPHA, new float[]{0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    BotWebViewContainer.this.flickerView.setVisibility(8);
                }
            });
            animatorSet.start();
            this.mUrl = str;
            this.isPageLoaded = true;
            setFocusable(true);
            this.delegate.onWebAppReady();
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
            this.onPermissionsRequestResultCallback = new BotWebViewContainer$$ExternalSyntheticLambda8(this, consumer, strArr);
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.requestPermissions(strArr, 4000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runWithPermissions$3(Consumer consumer, String[] strArr) {
        consumer.accept(Boolean.valueOf(checkPermissions(strArr)));
    }

    public boolean isPageLoaded() {
        return this.isPageLoaded;
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

    public void onInvoiceStatusUpdate(String str, String str2) {
        onInvoiceStatusUpdate(str, str2, false);
    }

    public void onInvoiceStatusUpdate(String str, String str2, boolean z) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("slug", str);
            jSONObject.put("status", str2);
            notifyEvent("invoice_closed", jSONObject);
            if (!z && ObjectsCompat$$ExternalSyntheticBackport0.m(this.currentPaymentSlug, str)) {
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

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (!this.isViewPortByMeasureSuppressed) {
            invalidateViewPortHeight(true);
        }
    }

    public void invalidateViewPortHeight() {
        invalidateViewPortHeight(false);
    }

    public void invalidateViewPortHeight(boolean z) {
        invalidateViewPortHeight(z, false);
    }

    public void invalidateViewPortHeight(boolean z, boolean z2) {
        invalidate();
        if ((this.isPageLoaded || z2) && (getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = (ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent();
            if (z) {
                this.lastExpanded = webViewSwipeContainer.getSwipeOffsetY() == (-webViewSwipeContainer.getOffsetY()) + webViewSwipeContainer.getTopActionBarOffsetY();
            }
            int measuredHeight = (int) (((((float) webViewSwipeContainer.getMeasuredHeight()) - webViewSwipeContainer.getOffsetY()) - webViewSwipeContainer.getSwipeOffsetY()) + webViewSwipeContainer.getTopActionBarOffsetY());
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("height", (double) (((float) measuredHeight) / AndroidUtilities.density));
                jSONObject.put("is_state_stable", z);
                jSONObject.put("is_expanded", this.lastExpanded);
                notifyEvent("viewport_changed", jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (view == this.flickerView) {
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
            this.flickerDrawable.draw(canvas, rectF, 0.0f, this);
            invalidate();
            return drawChild;
        } else if (view != this.webViewNotAvailableText) {
            return super.drawChild(canvas, view, j);
        } else {
            canvas.save();
            canvas.translate(0.0f, (((float) ActionBar.getCurrentActionBarHeight()) - ((View) getParent()).getTranslationY()) / 2.0f);
            boolean drawChild2 = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild2;
        }
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

    public void loadFlickerAndSettingsItem(int i, long j, ActionBarMenuSubItem actionBarMenuSubItem) {
        TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot;
        boolean z;
        String str = MessagesController.getInstance(i).getUser(Long.valueOf(j)).username;
        int i2 = 0;
        if (str == null || !ObjectsCompat$$ExternalSyntheticBackport0.m(str, "DurgerKingBot")) {
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
                }
                if (actionBarMenuSubItem != null) {
                    if (!tLRPC$TL_attachMenuBot.has_settings) {
                        i2 = 8;
                    }
                    actionBarMenuSubItem.setVisibility(i2);
                    return;
                }
                return;
            }
            TLRPC$TL_messages_getAttachMenuBot tLRPC$TL_messages_getAttachMenuBot = new TLRPC$TL_messages_getAttachMenuBot();
            tLRPC$TL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i).getInputUser(j);
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getAttachMenuBot, new BotWebViewContainer$$ExternalSyntheticLambda12(this, actionBarMenuSubItem));
            return;
        }
        this.flickerView.setVisibility(0);
        this.flickerView.setAlpha(1.0f);
        this.flickerView.setImageDrawable(SvgHelper.getDrawable(NUM, getColor("windowBackgroundGray")));
        setupFlickerParams(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFlickerAndSettingsItem$5(ActionBarMenuSubItem actionBarMenuSubItem, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda9(this, tLObject, actionBarMenuSubItem));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFlickerAndSettingsItem$4(TLObject tLObject, ActionBarMenuSubItem actionBarMenuSubItem) {
        boolean z;
        int i = 8;
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
            if (actionBarMenuSubItem != null) {
                if (tLRPC$TL_attachMenuBot.has_settings) {
                    i = 0;
                }
                actionBarMenuSubItem.setVisibility(i);
            }
        } else if (actionBarMenuSubItem != null) {
            actionBarMenuSubItem.setVisibility(8);
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

    public void loadUrl(int i, String str) {
        checkCreateWebView();
        this.currentAccount = i;
        this.isPageLoaded = false;
        this.lastClickMs = 0;
        this.hasUserPermissions = false;
        this.mUrl = str;
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.loadUrl(str);
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
            this.isPageLoaded = false;
        }
    }

    public boolean isBackButtonVisible() {
        return this.isBackButtonVisible;
    }

    public void evaluateJs(String str) {
        checkCreateWebView();
        WebView webView2 = this.webView;
        if (webView2 != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                webView2.evaluateJavascript(str, BotWebViewContainer$$ExternalSyntheticLambda6.INSTANCE);
                return;
            }
            try {
                webView2.loadUrl("javascript:" + URLEncoder.encode(str, "UTF-8"));
            } catch (UnsupportedEncodingException unused) {
                WebView webView3 = this.webView;
                webView3.loadUrl("javascript:" + URLEncoder.encode(str));
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetNewTheme) {
            WebView webView2 = this.webView;
            if (webView2 != null) {
                webView2.setBackgroundColor(getColor("windowBackgroundWhite"));
            }
            this.flickerView.setColorFilter(new PorterDuffColorFilter(getColor("dialogSearchHint"), PorterDuff.Mode.SRC_IN));
            notifyThemeChanged();
        } else if (i == NotificationCenter.onActivityResultReceived) {
            onActivityResult(objArr[0].intValue(), objArr[1].intValue(), objArr[2]);
        } else if (i == NotificationCenter.onRequestPermissionResultReceived) {
            onRequestPermissionsResult(objArr[0].intValue(), objArr[1], objArr[2]);
        }
    }

    private void notifyThemeChanged() {
        notifyEvent("theme_changed", buildThemeParams());
    }

    private void notifyEvent(String str, JSONObject jSONObject) {
        evaluateJs("window.Telegram.WebView.receiveEvent('" + str + "', " + jSONObject + ");");
    }

    public void setWebViewScrollListener(WebViewScrollListener webViewScrollListener2) {
        this.webViewScrollListener = webViewScrollListener2;
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: org.telegram.messenger.BotWebViewVibrationEffect} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r12v0 */
    /* JADX WARNING: type inference failed for: r12v2 */
    /* JADX WARNING: type inference failed for: r12v8 */
    /* JADX WARNING: type inference failed for: r12v9 */
    /* JADX WARNING: type inference failed for: r12v10 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x02b6, code lost:
        r9 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02b7, code lost:
        if (r9 == 0) goto L_0x02ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02b9, code lost:
        if (r9 == 1) goto L_0x02cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02bb, code lost:
        if (r9 == 2) goto L_0x02c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02bd, code lost:
        if (r9 == 3) goto L_0x02c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02bf, code lost:
        if (r9 == 4) goto L_0x02c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02c2, code lost:
        r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_SOFT;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x02c5, code lost:
        r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_RIGID;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x02c8, code lost:
        r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_HEAVY;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x02cb, code lost:
        r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_MEDIUM;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x02ce, code lost:
        r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_LIGHT;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0223 A[Catch:{ Exception -> 0x02f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0267 A[Catch:{ Exception -> 0x02f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0273 A[Catch:{ Exception -> 0x02f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0277 A[Catch:{ Exception -> 0x02f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x02d3 A[Catch:{ Exception -> 0x02f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:291:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onEventReceived(java.lang.String r20, java.lang.String r21) {
        /*
            r19 = this;
            r1 = r19
            r0 = r20
            r2 = r21
            java.lang.String r3 = "text_color"
            android.webkit.WebView r4 = r1.webView
            if (r4 == 0) goto L_0x054d
            org.telegram.ui.Components.BotWebViewContainer$Delegate r4 = r1.delegate
            if (r4 != 0) goto L_0x0012
            goto L_0x054d
        L_0x0012:
            r20.hashCode()
            int r4 = r20.hashCode()
            r5 = 4
            r6 = 3
            r7 = -1
            r8 = 2
            r9 = 0
            r10 = 1
            switch(r4) {
                case -1717314938: goto L_0x00ee;
                case -1693280352: goto L_0x00e2;
                case -1390641887: goto L_0x00d6;
                case -1341039673: goto L_0x00ca;
                case -1263619595: goto L_0x00be;
                case -1259935152: goto L_0x00b2;
                case -921083201: goto L_0x00a6;
                case -439770054: goto L_0x009a;
                case -71726289: goto L_0x008d;
                case -58095910: goto L_0x0080;
                case 668142772: goto L_0x0073;
                case 1011447167: goto L_0x0066;
                case 1273834781: goto L_0x0059;
                case 1398490221: goto L_0x004c;
                case 1917103703: goto L_0x003f;
                case 2001330488: goto L_0x0032;
                case 2139805763: goto L_0x0025;
                default: goto L_0x0022;
            }
        L_0x0022:
            r0 = -1
            goto L_0x00f9
        L_0x0025:
            java.lang.String r4 = "web_app_expand"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x002e
            goto L_0x0022
        L_0x002e:
            r0 = 16
            goto L_0x00f9
        L_0x0032:
            java.lang.String r4 = "web_app_set_background_color"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x003b
            goto L_0x0022
        L_0x003b:
            r0 = 15
            goto L_0x00f9
        L_0x003f:
            java.lang.String r4 = "web_app_set_header_color"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x0048
            goto L_0x0022
        L_0x0048:
            r0 = 14
            goto L_0x00f9
        L_0x004c:
            java.lang.String r4 = "web_app_setup_main_button"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x0055
            goto L_0x0022
        L_0x0055:
            r0 = 13
            goto L_0x00f9
        L_0x0059:
            java.lang.String r4 = "web_app_trigger_haptic_feedback"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x0062
            goto L_0x0022
        L_0x0062:
            r0 = 12
            goto L_0x00f9
        L_0x0066:
            java.lang.String r4 = "web_app_setup_back_button"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x006f
            goto L_0x0022
        L_0x006f:
            r0 = 11
            goto L_0x00f9
        L_0x0073:
            java.lang.String r4 = "web_app_data_send"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x007c
            goto L_0x0022
        L_0x007c:
            r0 = 10
            goto L_0x00f9
        L_0x0080:
            java.lang.String r4 = "web_app_ready"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x0089
            goto L_0x0022
        L_0x0089:
            r0 = 9
            goto L_0x00f9
        L_0x008d:
            java.lang.String r4 = "web_app_close"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x0096
            goto L_0x0022
        L_0x0096:
            r0 = 8
            goto L_0x00f9
        L_0x009a:
            java.lang.String r4 = "web_app_open_tg_link"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00a4
            goto L_0x0022
        L_0x00a4:
            r0 = 7
            goto L_0x00f9
        L_0x00a6:
            java.lang.String r4 = "web_app_request_viewport"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00b0
            goto L_0x0022
        L_0x00b0:
            r0 = 6
            goto L_0x00f9
        L_0x00b2:
            java.lang.String r4 = "web_app_request_theme"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00bc
            goto L_0x0022
        L_0x00bc:
            r0 = 5
            goto L_0x00f9
        L_0x00be:
            java.lang.String r4 = "web_app_request_phone"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00c8
            goto L_0x0022
        L_0x00c8:
            r0 = 4
            goto L_0x00f9
        L_0x00ca:
            java.lang.String r4 = "web_app_setup_closing_behavior"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00d4
            goto L_0x0022
        L_0x00d4:
            r0 = 3
            goto L_0x00f9
        L_0x00d6:
            java.lang.String r4 = "web_app_open_invoice"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00e0
            goto L_0x0022
        L_0x00e0:
            r0 = 2
            goto L_0x00f9
        L_0x00e2:
            java.lang.String r4 = "web_app_open_popup"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00ec
            goto L_0x0022
        L_0x00ec:
            r0 = 1
            goto L_0x00f9
        L_0x00ee:
            java.lang.String r4 = "web_app_open_link"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00f8
            goto L_0x0022
        L_0x00f8:
            r0 = 0
        L_0x00f9:
            java.lang.String r4 = "is_visible"
            java.lang.String r11 = "color"
            r12 = 0
            switch(r0) {
                case 0: goto L_0x052a;
                case 1: goto L_0x03e9;
                case 2: goto L_0x03ad;
                case 3: goto L_0x0395;
                case 4: goto L_0x054d;
                case 5: goto L_0x0390;
                case 6: goto L_0x0374;
                case 7: goto L_0x033d;
                case 8: goto L_0x0336;
                case 9: goto L_0x032b;
                case 10: goto L_0x0313;
                case 11: goto L_0x02f7;
                case 12: goto L_0x01e3;
                case 13: goto L_0x0171;
                case 14: goto L_0x012b;
                case 15: goto L_0x010a;
                case 16: goto L_0x0103;
                default: goto L_0x0101;
            }
        L_0x0101:
            goto L_0x054d
        L_0x0103:
            org.telegram.ui.Components.BotWebViewContainer$Delegate r0 = r1.delegate
            r0.onWebAppExpand()
            goto L_0x054d
        L_0x010a:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0125, IllegalArgumentException -> 0x0123 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x0125, IllegalArgumentException -> 0x0123 }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r2 = r1.delegate     // Catch:{ JSONException -> 0x0125, IllegalArgumentException -> 0x0123 }
            java.lang.String r3 = "#ffffff"
            java.lang.String r0 = r0.optString(r11, r3)     // Catch:{ JSONException -> 0x0125, IllegalArgumentException -> 0x0123 }
            int r0 = android.graphics.Color.parseColor(r0)     // Catch:{ JSONException -> 0x0125, IllegalArgumentException -> 0x0123 }
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = r0 | r3
            r2.onWebAppSetBackgroundColor(r0)     // Catch:{ JSONException -> 0x0125, IllegalArgumentException -> 0x0123 }
            goto L_0x054d
        L_0x0123:
            r0 = move-exception
            goto L_0x0126
        L_0x0125:
            r0 = move-exception
        L_0x0126:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x012b:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x016b }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x016b }
            java.lang.String r2 = "color_key"
            java.lang.String r0 = r0.getString(r2)     // Catch:{ JSONException -> 0x016b }
            int r2 = r0.hashCode()     // Catch:{ JSONException -> 0x016b }
            r3 = -1265068311(0xffffffffb49896e9, float:-2.8422008E-7)
            if (r2 == r3) goto L_0x014f
            r3 = -210781868(0xffffffffvar_fb954, float:-1.8992887E31)
            if (r2 == r3) goto L_0x0145
            goto L_0x0158
        L_0x0145:
            java.lang.String r2 = "secondary_bg_color"
            boolean r0 = r0.equals(r2)     // Catch:{ JSONException -> 0x016b }
            if (r0 == 0) goto L_0x0158
            r7 = 1
            goto L_0x0158
        L_0x014f:
            java.lang.String r2 = "bg_color"
            boolean r0 = r0.equals(r2)     // Catch:{ JSONException -> 0x016b }
            if (r0 == 0) goto L_0x0158
            r7 = 0
        L_0x0158:
            if (r7 == 0) goto L_0x0160
            if (r7 == r10) goto L_0x015d
            goto L_0x0162
        L_0x015d:
            java.lang.String r12 = "windowBackgroundGray"
            goto L_0x0162
        L_0x0160:
            java.lang.String r12 = "windowBackgroundWhite"
        L_0x0162:
            if (r12 == 0) goto L_0x054d
            org.telegram.ui.Components.BotWebViewContainer$Delegate r0 = r1.delegate     // Catch:{ JSONException -> 0x016b }
            r0.onWebAppSetActionBarColor(r12)     // Catch:{ JSONException -> 0x016b }
            goto L_0x054d
        L_0x016b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x0171:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            java.lang.String r5 = "is_active"
            boolean r14 = r0.optBoolean(r5, r9)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            java.lang.String r5 = "text"
            java.lang.String r6 = r1.lastButtonText     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            java.lang.String r5 = r0.optString(r5, r6)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            java.lang.String r15 = r5.trim()     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            boolean r4 = r0.optBoolean(r4, r9)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            if (r4 == 0) goto L_0x0196
            boolean r4 = android.text.TextUtils.isEmpty(r15)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            if (r4 != 0) goto L_0x0196
            r13 = 1
            goto L_0x0197
        L_0x0196:
            r13 = 0
        L_0x0197:
            boolean r4 = r0.has(r11)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            if (r4 == 0) goto L_0x01a6
            java.lang.String r4 = r0.optString(r11)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            int r4 = android.graphics.Color.parseColor(r4)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            goto L_0x01a8
        L_0x01a6:
            int r4 = r1.lastButtonColor     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
        L_0x01a8:
            boolean r5 = r0.has(r3)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            if (r5 == 0) goto L_0x01b7
            java.lang.String r3 = r0.optString(r3)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            int r3 = android.graphics.Color.parseColor(r3)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            goto L_0x01b9
        L_0x01b7:
            int r3 = r1.lastButtonTextColor     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
        L_0x01b9:
            java.lang.String r5 = "is_progress_visible"
            boolean r0 = r0.optBoolean(r5, r9)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            if (r0 == 0) goto L_0x01c6
            if (r13 == 0) goto L_0x01c6
            r18 = 1
            goto L_0x01c8
        L_0x01c6:
            r18 = 0
        L_0x01c8:
            r1.lastButtonColor = r4     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            r1.lastButtonTextColor = r3     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            r1.lastButtonText = r15     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            r1.buttonData = r2     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r12 = r1.delegate     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            r16 = r4
            r17 = r3
            r12.onSetupMainButton(r13, r14, r15, r16, r17, r18)     // Catch:{ JSONException -> 0x01dd, IllegalArgumentException -> 0x01db }
            goto L_0x054d
        L_0x01db:
            r0 = move-exception
            goto L_0x01de
        L_0x01dd:
            r0 = move-exception
        L_0x01de:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x01e3:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x02f1 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x02f1 }
            java.lang.String r2 = "type"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ Exception -> 0x02f1 }
            int r3 = r2.hashCode()     // Catch:{ Exception -> 0x02f1 }
            r4 = -1184809658(0xffffffffb9613d46, float:-2.1480498E-4)
            if (r3 == r4) goto L_0x0216
            r4 = 193071555(0xb8209c3, float:5.0088866E-32)
            if (r3 == r4) goto L_0x020c
            r4 = 595233003(0x237a88eb, float:1.3581521E-17)
            if (r3 == r4) goto L_0x0202
            goto L_0x0220
        L_0x0202:
            java.lang.String r3 = "notification"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x02f1 }
            if (r2 == 0) goto L_0x0220
            r2 = 1
            goto L_0x0221
        L_0x020c:
            java.lang.String r3 = "selection_change"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x02f1 }
            if (r2 == 0) goto L_0x0220
            r2 = 2
            goto L_0x0221
        L_0x0216:
            java.lang.String r3 = "impact"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x02f1 }
            if (r2 == 0) goto L_0x0220
            r2 = 0
            goto L_0x0221
        L_0x0220:
            r2 = -1
        L_0x0221:
            if (r2 == 0) goto L_0x0277
            if (r2 == r10) goto L_0x022d
            if (r2 == r8) goto L_0x0229
            goto L_0x02d1
        L_0x0229:
            org.telegram.messenger.BotWebViewVibrationEffect r12 = org.telegram.messenger.BotWebViewVibrationEffect.SELECTION_CHANGE     // Catch:{ Exception -> 0x02f1 }
            goto L_0x02d1
        L_0x022d:
            java.lang.String r2 = "notification_type"
            java.lang.String r0 = r0.optString(r2)     // Catch:{ Exception -> 0x02f1 }
            int r2 = r0.hashCode()     // Catch:{ Exception -> 0x02f1 }
            r3 = -1867169789(0xfffffffvar_b54003, float:-7.149054E-29)
            if (r2 == r3) goto L_0x025a
            r3 = 96784904(0x5c4d208, float:1.8508905E-35)
            if (r2 == r3) goto L_0x0251
            r3 = 1124446108(0x4305af9c, float:133.68597)
            if (r2 == r3) goto L_0x0247
            goto L_0x0264
        L_0x0247:
            java.lang.String r2 = "warning"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x0264
            r9 = 2
            goto L_0x0265
        L_0x0251:
            java.lang.String r2 = "error"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x0264
            goto L_0x0265
        L_0x025a:
            java.lang.String r2 = "success"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x0264
            r9 = 1
            goto L_0x0265
        L_0x0264:
            r9 = -1
        L_0x0265:
            if (r9 == 0) goto L_0x0273
            if (r9 == r10) goto L_0x0270
            if (r9 == r8) goto L_0x026d
            goto L_0x02d1
        L_0x026d:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.NOTIFICATION_WARNING     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x0270:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.NOTIFICATION_SUCCESS     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x0273:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.NOTIFICATION_ERROR     // Catch:{ Exception -> 0x02f1 }
        L_0x0275:
            r12 = r0
            goto L_0x02d1
        L_0x0277:
            java.lang.String r2 = "impact_style"
            java.lang.String r0 = r0.optString(r2)     // Catch:{ Exception -> 0x02f1 }
            int r2 = r0.hashCode()     // Catch:{ Exception -> 0x02f1 }
            switch(r2) {
                case -1078030475: goto L_0x02ac;
                case 3535914: goto L_0x02a2;
                case 99152071: goto L_0x0298;
                case 102970646: goto L_0x028f;
                case 108511787: goto L_0x0285;
                default: goto L_0x0284;
            }     // Catch:{ Exception -> 0x02f1 }
        L_0x0284:
            goto L_0x02b6
        L_0x0285:
            java.lang.String r2 = "rigid"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x02b6
            r9 = 3
            goto L_0x02b7
        L_0x028f:
            java.lang.String r2 = "light"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x02b6
            goto L_0x02b7
        L_0x0298:
            java.lang.String r2 = "heavy"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x02b6
            r9 = 2
            goto L_0x02b7
        L_0x02a2:
            java.lang.String r2 = "soft"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x02b6
            r9 = 4
            goto L_0x02b7
        L_0x02ac:
            java.lang.String r2 = "medium"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x02f1 }
            if (r0 == 0) goto L_0x02b6
            r9 = 1
            goto L_0x02b7
        L_0x02b6:
            r9 = -1
        L_0x02b7:
            if (r9 == 0) goto L_0x02ce
            if (r9 == r10) goto L_0x02cb
            if (r9 == r8) goto L_0x02c8
            if (r9 == r6) goto L_0x02c5
            if (r9 == r5) goto L_0x02c2
            goto L_0x02d1
        L_0x02c2:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_SOFT     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x02c5:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_RIGID     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x02c8:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_HEAVY     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x02cb:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_MEDIUM     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x02ce:
            org.telegram.messenger.BotWebViewVibrationEffect r0 = org.telegram.messenger.BotWebViewVibrationEffect.IMPACT_LIGHT     // Catch:{ Exception -> 0x02f1 }
            goto L_0x0275
        L_0x02d1:
            if (r12 == 0) goto L_0x054d
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02f1 }
            r2 = 26
            if (r0 < r2) goto L_0x02e6
            android.os.Vibrator r0 = org.telegram.messenger.AndroidUtilities.getVibrator()     // Catch:{ Exception -> 0x02f1 }
            android.os.VibrationEffect r2 = r12.getVibrationEffectForOreo()     // Catch:{ Exception -> 0x02f1 }
            r0.vibrate(r2)     // Catch:{ Exception -> 0x02f1 }
            goto L_0x054d
        L_0x02e6:
            android.os.Vibrator r0 = org.telegram.messenger.AndroidUtilities.getVibrator()     // Catch:{ Exception -> 0x02f1 }
            long[] r2 = r12.fallbackTimings     // Catch:{ Exception -> 0x02f1 }
            r0.vibrate(r2, r7)     // Catch:{ Exception -> 0x02f1 }
            goto L_0x054d
        L_0x02f1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x02f7:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x030d }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x030d }
            boolean r0 = r0.optBoolean(r4)     // Catch:{ JSONException -> 0x030d }
            boolean r2 = r1.isBackButtonVisible     // Catch:{ JSONException -> 0x030d }
            if (r0 == r2) goto L_0x054d
            r1.isBackButtonVisible = r0     // Catch:{ JSONException -> 0x030d }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r2 = r1.delegate     // Catch:{ JSONException -> 0x030d }
            r2.onSetBackButtonVisible(r0)     // Catch:{ JSONException -> 0x030d }
            goto L_0x054d
        L_0x030d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x0313:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0325 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x0325 }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r2 = r1.delegate     // Catch:{ JSONException -> 0x0325 }
            java.lang.String r3 = "data"
            java.lang.String r0 = r0.optString(r3)     // Catch:{ JSONException -> 0x0325 }
            r2.onSendWebViewData(r0)     // Catch:{ JSONException -> 0x0325 }
            goto L_0x054d
        L_0x0325:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x032b:
            android.webkit.WebView r0 = r1.webView
            java.lang.String r0 = r0.getUrl()
            r1.setPageLoaded(r0)
            goto L_0x054d
        L_0x0336:
            org.telegram.ui.Components.BotWebViewContainer$Delegate r0 = r1.delegate
            r0.onCloseRequested(r12)
            goto L_0x054d
        L_0x033d:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x036e }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x036e }
            java.lang.String r2 = "path_full"
            java.lang.String r0 = r0.optString(r2)     // Catch:{ JSONException -> 0x036e }
            java.lang.String r2 = "/"
            boolean r2 = r0.startsWith(r2)     // Catch:{ JSONException -> 0x036e }
            if (r2 == 0) goto L_0x0354
            java.lang.String r0 = r0.substring(r10)     // Catch:{ JSONException -> 0x036e }
        L_0x0354:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x036e }
            r2.<init>()     // Catch:{ JSONException -> 0x036e }
            java.lang.String r3 = "https://t.me/"
            r2.append(r3)     // Catch:{ JSONException -> 0x036e }
            r2.append(r0)     // Catch:{ JSONException -> 0x036e }
            java.lang.String r0 = r2.toString()     // Catch:{ JSONException -> 0x036e }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ JSONException -> 0x036e }
            r1.onOpenUri(r0)     // Catch:{ JSONException -> 0x036e }
            goto L_0x054d
        L_0x036e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x0374:
            android.view.ViewParent r0 = r19.getParent()
            boolean r0 = r0 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer
            if (r0 == 0) goto L_0x0389
            android.view.ViewParent r0 = r19.getParent()
            org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer r0 = (org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) r0
            boolean r0 = r0.isSwipeInProgress()
            if (r0 == 0) goto L_0x0389
            r9 = 1
        L_0x0389:
            r0 = r9 ^ 1
            r1.invalidateViewPortHeight(r0, r10)
            goto L_0x054d
        L_0x0390:
            r19.notifyThemeChanged()
            goto L_0x054d
        L_0x0395:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x03a7 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x03a7 }
            org.telegram.ui.Components.BotWebViewContainer$Delegate r2 = r1.delegate     // Catch:{ JSONException -> 0x03a7 }
            java.lang.String r3 = "need_confirmation"
            boolean r0 = r0.optBoolean(r3)     // Catch:{ JSONException -> 0x03a7 }
            r2.onWebAppSetupClosingBehavior(r0)     // Catch:{ JSONException -> 0x03a7 }
            goto L_0x054d
        L_0x03a7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x03ad:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x03e3 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x03e3 }
            java.lang.String r2 = "slug"
            java.lang.String r0 = r0.optString(r2)     // Catch:{ JSONException -> 0x03e3 }
            java.lang.String r2 = r1.currentPaymentSlug     // Catch:{ JSONException -> 0x03e3 }
            if (r2 == 0) goto L_0x03c3
            java.lang.String r2 = "cancelled"
            r1.onInvoiceStatusUpdate(r0, r2, r10)     // Catch:{ JSONException -> 0x03e3 }
            goto L_0x054d
        L_0x03c3:
            r1.currentPaymentSlug = r0     // Catch:{ JSONException -> 0x03e3 }
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r2 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm     // Catch:{ JSONException -> 0x03e3 }
            r2.<init>()     // Catch:{ JSONException -> 0x03e3 }
            org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug     // Catch:{ JSONException -> 0x03e3 }
            r3.<init>()     // Catch:{ JSONException -> 0x03e3 }
            r3.slug = r0     // Catch:{ JSONException -> 0x03e3 }
            r2.invoice = r3     // Catch:{ JSONException -> 0x03e3 }
            int r3 = r1.currentAccount     // Catch:{ JSONException -> 0x03e3 }
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)     // Catch:{ JSONException -> 0x03e3 }
            org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda11 r4 = new org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda11     // Catch:{ JSONException -> 0x03e3 }
            r4.<init>(r1, r0)     // Catch:{ JSONException -> 0x03e3 }
            r3.sendRequest(r2, r4)     // Catch:{ JSONException -> 0x03e3 }
            goto L_0x054d
        L_0x03e3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x03e9:
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.currentDialog     // Catch:{ JSONException -> 0x0525 }
            if (r0 == 0) goto L_0x03ef
            goto L_0x054d
        L_0x03ef:
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x0525 }
            long r13 = r1.lastDialogClosed     // Catch:{ JSONException -> 0x0525 }
            long r3 = r3 - r13
            r13 = 150(0x96, double:7.4E-322)
            int r0 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r0 > 0) goto L_0x040d
            int r0 = r1.dialogSequentialOpenTimes     // Catch:{ JSONException -> 0x0525 }
            int r0 = r0 + r10
            r1.dialogSequentialOpenTimes = r0     // Catch:{ JSONException -> 0x0525 }
            if (r0 < r6) goto L_0x040d
            r1.dialogSequentialOpenTimes = r9     // Catch:{ JSONException -> 0x0525 }
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x0525 }
            r1.lastDialogCooldownTime = r2     // Catch:{ JSONException -> 0x0525 }
            goto L_0x054d
        L_0x040d:
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x0525 }
            long r13 = r1.lastDialogCooldownTime     // Catch:{ JSONException -> 0x0525 }
            long r3 = r3 - r13
            r13 = 3000(0xbb8, double:1.482E-320)
            int r0 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r0 > 0) goto L_0x041c
            goto L_0x054d
        L_0x041c:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0525 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r2 = "title"
            java.lang.String r2 = r0.optString(r2, r12)     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r3 = "message"
            java.lang.String r3 = r0.getString(r3)     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r4 = "buttons"
            org.json.JSONArray r0 = r0.getJSONArray(r4)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.ActionBar.AlertDialog$Builder r4 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ JSONException -> 0x0525 }
            android.content.Context r5 = r19.getContext()     // Catch:{ JSONException -> 0x0525 }
            r4.<init>((android.content.Context) r5)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r4.setTitle(r2)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r2.setMessage(r3)     // Catch:{ JSONException -> 0x0525 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ JSONException -> 0x0525 }
            r3.<init>()     // Catch:{ JSONException -> 0x0525 }
            r4 = 0
        L_0x044a:
            int r5 = r0.length()     // Catch:{ JSONException -> 0x0525 }
            if (r4 >= r5) goto L_0x045f
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r5 = new org.telegram.ui.Components.BotWebViewContainer$PopupButton     // Catch:{ JSONException -> 0x0525 }
            org.json.JSONObject r11 = r0.getJSONObject(r4)     // Catch:{ JSONException -> 0x0525 }
            r5.<init>(r11)     // Catch:{ JSONException -> 0x0525 }
            r3.add(r5)     // Catch:{ JSONException -> 0x0525 }
            int r4 = r4 + 1
            goto L_0x044a
        L_0x045f:
            int r0 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r0 <= r6) goto L_0x0467
            goto L_0x054d
        L_0x0467:
            java.util.Collections.reverse(r3)     // Catch:{ JSONException -> 0x0525 }
            java.util.concurrent.atomic.AtomicBoolean r0 = new java.util.concurrent.atomic.AtomicBoolean     // Catch:{ JSONException -> 0x0525 }
            r0.<init>()     // Catch:{ JSONException -> 0x0525 }
            int r4 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r4 < r10) goto L_0x0485
            java.lang.Object r4 = r3.get(r9)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r4 = (org.telegram.ui.Components.BotWebViewContainer.PopupButton) r4     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r5 = r4.text     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda1 r11 = new org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda1     // Catch:{ JSONException -> 0x0525 }
            r11.<init>(r1, r4, r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setPositiveButton(r5, r11)     // Catch:{ JSONException -> 0x0525 }
        L_0x0485:
            int r4 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r4 < r8) goto L_0x049b
            java.lang.Object r4 = r3.get(r10)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r4 = (org.telegram.ui.Components.BotWebViewContainer.PopupButton) r4     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r5 = r4.text     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda3 r11 = new org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda3     // Catch:{ JSONException -> 0x0525 }
            r11.<init>(r1, r4, r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setNegativeButton(r5, r11)     // Catch:{ JSONException -> 0x0525 }
        L_0x049b:
            int r4 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r4 != r6) goto L_0x04b1
            java.lang.Object r4 = r3.get(r8)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r4 = (org.telegram.ui.Components.BotWebViewContainer.PopupButton) r4     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r5 = r4.text     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda2 r11 = new org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda2     // Catch:{ JSONException -> 0x0525 }
            r11.<init>(r1, r4, r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setNeutralButton(r5, r11)     // Catch:{ JSONException -> 0x0525 }
        L_0x04b1:
            org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda5     // Catch:{ JSONException -> 0x0525 }
            r4.<init>(r1, r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setOnDismissListener(r4)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.ActionBar.AlertDialog r0 = r2.show()     // Catch:{ JSONException -> 0x0525 }
            r1.currentDialog = r0     // Catch:{ JSONException -> 0x0525 }
            int r0 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r0 < r10) goto L_0x04e0
            java.lang.Object r0 = r3.get(r9)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r0 = (org.telegram.ui.Components.BotWebViewContainer.PopupButton) r0     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r2 = r0.textColorKey     // Catch:{ JSONException -> 0x0525 }
            if (r2 == 0) goto L_0x04e0
            org.telegram.ui.ActionBar.AlertDialog r2 = r1.currentDialog     // Catch:{ JSONException -> 0x0525 }
            android.view.View r2 = r2.getButton(r7)     // Catch:{ JSONException -> 0x0525 }
            android.widget.TextView r2 = (android.widget.TextView) r2     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r0 = r0.textColorKey     // Catch:{ JSONException -> 0x0525 }
            int r0 = r1.getColor(r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setTextColor(r0)     // Catch:{ JSONException -> 0x0525 }
        L_0x04e0:
            int r0 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r0 < r8) goto L_0x0502
            java.lang.Object r0 = r3.get(r10)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r0 = (org.telegram.ui.Components.BotWebViewContainer.PopupButton) r0     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r2 = r0.textColorKey     // Catch:{ JSONException -> 0x0525 }
            if (r2 == 0) goto L_0x0502
            org.telegram.ui.ActionBar.AlertDialog r2 = r1.currentDialog     // Catch:{ JSONException -> 0x0525 }
            r4 = -2
            android.view.View r2 = r2.getButton(r4)     // Catch:{ JSONException -> 0x0525 }
            android.widget.TextView r2 = (android.widget.TextView) r2     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r0 = r0.textColorKey     // Catch:{ JSONException -> 0x0525 }
            int r0 = r1.getColor(r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setTextColor(r0)     // Catch:{ JSONException -> 0x0525 }
        L_0x0502:
            int r0 = r3.size()     // Catch:{ JSONException -> 0x0525 }
            if (r0 != r6) goto L_0x054d
            java.lang.Object r0 = r3.get(r8)     // Catch:{ JSONException -> 0x0525 }
            org.telegram.ui.Components.BotWebViewContainer$PopupButton r0 = (org.telegram.ui.Components.BotWebViewContainer.PopupButton) r0     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r2 = r0.textColorKey     // Catch:{ JSONException -> 0x0525 }
            if (r2 == 0) goto L_0x054d
            org.telegram.ui.ActionBar.AlertDialog r2 = r1.currentDialog     // Catch:{ JSONException -> 0x0525 }
            r3 = -3
            android.view.View r2 = r2.getButton(r3)     // Catch:{ JSONException -> 0x0525 }
            android.widget.TextView r2 = (android.widget.TextView) r2     // Catch:{ JSONException -> 0x0525 }
            java.lang.String r0 = r0.textColorKey     // Catch:{ JSONException -> 0x0525 }
            int r0 = r1.getColor(r0)     // Catch:{ JSONException -> 0x0525 }
            r2.setTextColor(r0)     // Catch:{ JSONException -> 0x0525 }
            goto L_0x054d
        L_0x0525:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054d
        L_0x052a:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0549 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0549 }
            java.lang.String r2 = "url"
            java.lang.String r0 = r0.optString(r2)     // Catch:{ Exception -> 0x0549 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x0549 }
            java.util.List<java.lang.String> r2 = WHITELISTED_SCHEMES     // Catch:{ Exception -> 0x0549 }
            java.lang.String r3 = r0.getScheme()     // Catch:{ Exception -> 0x0549 }
            boolean r2 = r2.contains(r3)     // Catch:{ Exception -> 0x0549 }
            if (r2 == 0) goto L_0x054d
            r1.onOpenUri(r0, r10)     // Catch:{ Exception -> 0x0549 }
            goto L_0x054d
        L_0x0549:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x054d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewContainer.onEventReceived(java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEventReceived$9(PopupButton popupButton, AtomicBoolean atomicBoolean, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            notifyEvent("popup_closed", new JSONObject().put("button_id", popupButton.id));
            atomicBoolean.set(true);
        } catch (JSONException e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEventReceived$10(PopupButton popupButton, AtomicBoolean atomicBoolean, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            notifyEvent("popup_closed", new JSONObject().put("button_id", popupButton.id));
            atomicBoolean.set(true);
        } catch (JSONException e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEventReceived$11(PopupButton popupButton, AtomicBoolean atomicBoolean, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            notifyEvent("popup_closed", new JSONObject().put("button_id", popupButton.id));
            atomicBoolean.set(true);
        } catch (JSONException e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEventReceived$12(AtomicBoolean atomicBoolean, DialogInterface dialogInterface) {
        if (!atomicBoolean.get()) {
            notifyEvent("popup_closed", new JSONObject());
        }
        this.currentDialog = null;
        this.lastDialogClosed = System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEventReceived$14(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new BotWebViewContainer$$ExternalSyntheticLambda10(this, tLRPC$TL_error, str, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEventReceived$13(TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            onInvoiceStatusUpdate(str, "failed");
        } else {
            this.delegate.onWebAppOpenInvoice(str, tLObject);
        }
    }

    private JSONObject buildThemeParams() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", formatColor("windowBackgroundWhite"));
            jSONObject.put("secondary_bg_color", formatColor("windowBackgroundGray"));
            jSONObject.put("text_color", formatColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", formatColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", formatColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", formatColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", formatColor("featuredStickers_buttonText"));
            return new JSONObject().put("theme_params", jSONObject);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new JSONObject();
        }
    }

    private int getColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer valueOf = Integer.valueOf(resourcesProvider2 != null ? resourcesProvider2.getColor(str).intValue() : Theme.getColor(str));
        if (valueOf == null) {
            valueOf = Integer.valueOf(Theme.getColor(str));
        }
        return valueOf.intValue();
    }

    private String formatColor(String str) {
        int color = getColor(str);
        return "#" + hexFixed(Color.red(color)) + hexFixed(Color.green(color)) + hexFixed(Color.blue(color));
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

    public static final class PopupButton {
        public String id;
        public String text;
        public String textColorKey;

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PopupButton(org.json.JSONObject r8) throws org.json.JSONException {
            /*
                r7 = this;
                r7.<init>()
                java.lang.String r0 = "id"
                java.lang.String r0 = r8.getString(r0)
                r7.id = r0
                java.lang.String r0 = "type"
                java.lang.String r0 = r8.getString(r0)
                int r1 = r0.hashCode()
                r2 = 5
                r3 = 4
                r4 = 3
                r5 = 2
                r6 = 1
                switch(r1) {
                    case -1829997182: goto L_0x0046;
                    case -1367724422: goto L_0x003c;
                    case 3548: goto L_0x0032;
                    case 94756344: goto L_0x0028;
                    case 1544803905: goto L_0x001e;
                    default: goto L_0x001d;
                }
            L_0x001d:
                goto L_0x0050
            L_0x001e:
                java.lang.String r1 = "default"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0050
                r0 = 1
                goto L_0x0051
            L_0x0028:
                java.lang.String r1 = "close"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0050
                r0 = 3
                goto L_0x0051
            L_0x0032:
                java.lang.String r1 = "ok"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0050
                r0 = 2
                goto L_0x0051
            L_0x003c:
                java.lang.String r1 = "cancel"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0050
                r0 = 4
                goto L_0x0051
            L_0x0046:
                java.lang.String r1 = "destructive"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0050
                r0 = 5
                goto L_0x0051
            L_0x0050:
                r0 = -1
            L_0x0051:
                if (r0 == r5) goto L_0x0073
                if (r0 == r4) goto L_0x0069
                if (r0 == r3) goto L_0x005f
                if (r0 == r2) goto L_0x005a
                goto L_0x007d
            L_0x005a:
                java.lang.String r0 = "dialogTextRed"
                r7.textColorKey = r0
                goto L_0x007d
            L_0x005f:
                r0 = 2131624832(0x7f0e0380, float:1.8876855E38)
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
                r7.text = r0
                goto L_0x007c
            L_0x0069:
                r0 = 2131625183(0x7f0e04df, float:1.8877567E38)
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
                r7.text = r0
                goto L_0x007c
            L_0x0073:
                r0 = 2131627127(0x7f0e0CLASSNAME, float:1.888151E38)
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
                r7.text = r0
            L_0x007c:
                r6 = 0
            L_0x007d:
                if (r6 == 0) goto L_0x0087
                java.lang.String r0 = "text"
                java.lang.String r8 = r8.getString(r0)
                r7.text = r8
            L_0x0087:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewContainer.PopupButton.<init>(org.json.JSONObject):void");
        }
    }
}
