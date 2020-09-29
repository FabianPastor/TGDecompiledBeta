package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.WebPlayerView;

public class WebPlayerView extends ViewGroup implements VideoPlayer.VideoPlayerDelegate, AudioManager.OnAudioFocusChangeListener {
    /* access modifiers changed from: private */
    public static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    /* access modifiers changed from: private */
    public static final Pattern exprParensPattern = Pattern.compile("[()]");
    /* access modifiers changed from: private */
    public static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static int lastContainerId = 4001;
    /* access modifiers changed from: private */
    public static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|(?:/[a-z]{2}_[A-Z]{2})?/base)?\\.([a-z]+)$");
    /* access modifiers changed from: private */
    public static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    /* access modifiers changed from: private */
    public static final Pattern sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    /* access modifiers changed from: private */
    public static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    /* access modifiers changed from: private */
    public static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    /* access modifiers changed from: private */
    public static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    /* access modifiers changed from: private */
    public static final Pattern twitchClipFilePattern = Pattern.compile("clipInfo\\s*=\\s*(\\{[^']+\\});");
    private static final Pattern twitchClipIdRegex = Pattern.compile("https?://clips\\.twitch\\.tv/(?:[^/]+/)*([^/?#&]+)");
    private static final Pattern twitchStreamIdRegex = Pattern.compile("https?://(?:(?:www\\.)?twitch\\.tv/|player\\.twitch\\.tv/\\?.*?\\bchannel=)([^/#?]+)");
    private static final Pattern vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    /* access modifiers changed from: private */
    public boolean allowInlineAnimation;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    private Paint backgroundPaint;
    /* access modifiers changed from: private */
    public TextureView changedTextureView;
    /* access modifiers changed from: private */
    public boolean changingTextureView;
    /* access modifiers changed from: private */
    public ControlsView controlsView;
    /* access modifiers changed from: private */
    public float currentAlpha;
    /* access modifiers changed from: private */
    public Bitmap currentBitmap;
    private AsyncTask currentTask;
    private String currentYoutubeId;
    /* access modifiers changed from: private */
    public WebPlayerViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean drawImage;
    /* access modifiers changed from: private */
    public boolean firstFrameRendered;
    private ImageView fullscreenButton;
    private boolean hasAudioFocus;
    /* access modifiers changed from: private */
    public boolean inFullscreen;
    /* access modifiers changed from: private */
    public boolean initied;
    private ImageView inlineButton;
    /* access modifiers changed from: private */
    public String interfaceName;
    /* access modifiers changed from: private */
    public boolean isAutoplay;
    private boolean isCompleted;
    /* access modifiers changed from: private */
    public boolean isInline;
    /* access modifiers changed from: private */
    public boolean isStream;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public String playAudioType;
    /* access modifiers changed from: private */
    public String playAudioUrl;
    private ImageView playButton;
    /* access modifiers changed from: private */
    public String playVideoType;
    /* access modifiers changed from: private */
    public String playVideoUrl;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public Runnable progressRunnable;
    private RadialProgressView progressView;
    private boolean resumeAudioOnFocusGain;
    private int seekToTime;
    private ImageView shareButton;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Runnable switchToInlineRunnable;
    /* access modifiers changed from: private */
    public boolean switchingInlineMode;
    /* access modifiers changed from: private */
    public ImageView textureImageView;
    /* access modifiers changed from: private */
    public TextureView textureView;
    /* access modifiers changed from: private */
    public ViewGroup textureViewContainer;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    /* access modifiers changed from: private */
    public int waitingForFirstTextureUpload;
    /* access modifiers changed from: private */
    public WebView webView;

    public interface CallJavaResultInterface {
        void jsCallFinished(String str);
    }

    public interface WebPlayerViewDelegate {
        boolean checkInlinePermissions();

        ViewGroup getTextureViewContainer();

        void onInitFailed();

        void onInlineSurfaceTextureReady();

        void onPlayStateChanged(WebPlayerView webPlayerView, boolean z);

        void onSharePressed();

        TextureView onSwitchInlineMode(View view, boolean z, float f, int i, boolean z2);

        TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2);

        void onVideoSizeChanged(float f, int i);

        void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2);
    }

    public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
    }

    public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
    }

    public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
    }

    private static class JSExtractor {
        private String[] assign_operators = {"|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "="};
        ArrayList<String> codeLines = new ArrayList<>();
        private String jsCode;
        private String[] operators = {"|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*"};

        public JSExtractor(String str) {
            this.jsCode = str;
        }

        private void interpretExpression(String str, HashMap<String, String> hashMap, int i) throws Exception {
            String trim = str.trim();
            if (!TextUtils.isEmpty(trim)) {
                if (trim.charAt(0) == '(') {
                    Matcher matcher = WebPlayerView.exprParensPattern.matcher(trim);
                    int i2 = 0;
                    while (true) {
                        if (!matcher.find()) {
                            break;
                        } else if (matcher.group(0).indexOf(48) == 40) {
                            i2++;
                        } else {
                            i2--;
                            if (i2 == 0) {
                                interpretExpression(trim.substring(1, matcher.start()), hashMap, i);
                                trim = trim.substring(matcher.end()).trim();
                                if (TextUtils.isEmpty(trim)) {
                                    return;
                                }
                            }
                        }
                    }
                    if (i2 != 0) {
                        throw new Exception(String.format("Premature end of parens in %s", new Object[]{trim}));
                    }
                }
                int i3 = 0;
                while (true) {
                    String[] strArr = this.assign_operators;
                    if (i3 < strArr.length) {
                        Matcher matcher2 = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", new Object[]{"[a-zA-Z_$][a-zA-Z_$0-9]*", Pattern.quote(strArr[i3])})).matcher(trim);
                        if (!matcher2.find()) {
                            i3++;
                        } else {
                            interpretExpression(matcher2.group(3), hashMap, i - 1);
                            String group = matcher2.group(2);
                            if (!TextUtils.isEmpty(group)) {
                                interpretExpression(group, hashMap, i);
                                return;
                            } else {
                                hashMap.put(matcher2.group(1), "");
                                return;
                            }
                        }
                    } else {
                        try {
                            Integer.parseInt(trim);
                            return;
                        } catch (Exception unused) {
                            if (!Pattern.compile(String.format(Locale.US, "(?!if|return|true|false)(%s)$", new Object[]{"[a-zA-Z_$][a-zA-Z_$0-9]*"})).matcher(trim).find()) {
                                if (trim.charAt(0) != '\"' || trim.charAt(trim.length() - 1) != '\"') {
                                    try {
                                        new JSONObject(trim).toString();
                                        return;
                                    } catch (Exception unused2) {
                                        Matcher matcher3 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{"[a-zA-Z_$][a-zA-Z_$0-9]*"})).matcher(trim);
                                        if (matcher3.find()) {
                                            matcher3.group(1);
                                            interpretExpression(matcher3.group(2), hashMap, i - 1);
                                            return;
                                        }
                                        Matcher matcher4 = Pattern.compile(String.format(Locale.US, "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$", new Object[]{"[a-zA-Z_$][a-zA-Z_$0-9]*"})).matcher(trim);
                                        if (matcher4.find()) {
                                            String group2 = matcher4.group(1);
                                            String group3 = matcher4.group(2);
                                            String group4 = matcher4.group(3);
                                            if (TextUtils.isEmpty(group3)) {
                                                group3 = group4;
                                            }
                                            group3.replace("\"", "");
                                            String group5 = matcher4.group(4);
                                            if (hashMap.get(group2) == null) {
                                                extractObject(group2);
                                            }
                                            if (group5 != null) {
                                                if (trim.charAt(trim.length() - 1) != ')') {
                                                    throw new Exception("last char not ')'");
                                                } else if (group5.length() != 0) {
                                                    String[] split = group5.split(",");
                                                    for (String interpretExpression : split) {
                                                        interpretExpression(interpretExpression, hashMap, i);
                                                    }
                                                    return;
                                                } else {
                                                    return;
                                                }
                                            } else {
                                                return;
                                            }
                                        } else {
                                            Matcher matcher5 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{"[a-zA-Z_$][a-zA-Z_$0-9]*"})).matcher(trim);
                                            if (matcher5.find()) {
                                                hashMap.get(matcher5.group(1));
                                                interpretExpression(matcher5.group(2), hashMap, i - 1);
                                                return;
                                            }
                                            int i4 = 0;
                                            while (true) {
                                                String[] strArr2 = this.operators;
                                                if (i4 < strArr2.length) {
                                                    String str2 = strArr2[i4];
                                                    Matcher matcher6 = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", new Object[]{Pattern.quote(str2)})).matcher(trim);
                                                    if (matcher6.find()) {
                                                        boolean[] zArr = new boolean[1];
                                                        int i5 = i - 1;
                                                        interpretStatement(matcher6.group(1), hashMap, zArr, i5);
                                                        if (!zArr[0]) {
                                                            interpretStatement(matcher6.group(2), hashMap, zArr, i5);
                                                            if (zArr[0]) {
                                                                throw new Exception(String.format("Premature right-side return of %s in %s", new Object[]{str2, trim}));
                                                            }
                                                        } else {
                                                            throw new Exception(String.format("Premature left-side return of %s in %s", new Object[]{str2, trim}));
                                                        }
                                                    }
                                                    i4++;
                                                } else {
                                                    Matcher matcher7 = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", new Object[]{"[a-zA-Z_$][a-zA-Z_$0-9]*"})).matcher(trim);
                                                    if (matcher7.find()) {
                                                        extractFunction(matcher7.group(1));
                                                    }
                                                    throw new Exception(String.format("Unsupported JS expression %s", new Object[]{trim}));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }

        private void interpretStatement(String str, HashMap<String, String> hashMap, boolean[] zArr, int i) throws Exception {
            if (i >= 0) {
                zArr[0] = false;
                String trim = str.trim();
                Matcher matcher = WebPlayerView.stmtVarPattern.matcher(trim);
                if (matcher.find()) {
                    trim = trim.substring(matcher.group(0).length());
                } else {
                    Matcher matcher2 = WebPlayerView.stmtReturnPattern.matcher(trim);
                    if (matcher2.find()) {
                        trim = trim.substring(matcher2.group(0).length());
                        zArr[0] = true;
                    }
                }
                interpretExpression(trim, hashMap, i);
                return;
            }
            throw new Exception("recursion limit reached");
        }

        private HashMap<String, Object> extractObject(String str) throws Exception {
            HashMap<String, Object> hashMap = new HashMap<>();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[]{Pattern.quote(str), "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')"})).matcher(this.jsCode);
            String str2 = null;
            while (true) {
                if (!matcher.find()) {
                    break;
                }
                String group = matcher.group();
                String group2 = matcher.group(2);
                if (TextUtils.isEmpty(group2)) {
                    str2 = group2;
                } else {
                    if (!this.codeLines.contains(group)) {
                        this.codeLines.add(matcher.group());
                    }
                    str2 = group2;
                }
            }
            Matcher matcher2 = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[]{"(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')"})).matcher(str2);
            while (matcher2.find()) {
                buildFunction(matcher2.group(2).split(","), matcher2.group(3));
            }
            return hashMap;
        }

        private void buildFunction(String[] strArr, String str) throws Exception {
            HashMap hashMap = new HashMap();
            for (String put : strArr) {
                hashMap.put(put, "");
            }
            String[] split = str.split(";");
            boolean[] zArr = new boolean[1];
            int i = 0;
            while (i < split.length) {
                interpretStatement(split[i], hashMap, zArr, 100);
                if (!zArr[0]) {
                    i++;
                } else {
                    return;
                }
            }
        }

        /* access modifiers changed from: private */
        public String extractFunction(String str) {
            try {
                String quote = Pattern.quote(str);
                Matcher matcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[]{quote, quote, quote})).matcher(this.jsCode);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (!this.codeLines.contains(group)) {
                        ArrayList<String> arrayList = this.codeLines;
                        arrayList.add(group + ";");
                    }
                    buildFunction(matcher.group(1).split(","), matcher.group(2));
                }
            } catch (Exception e) {
                this.codeLines.clear();
                FileLog.e((Throwable) e);
            }
            return TextUtils.join("", this.codeLines);
        }
    }

    public static class JavaScriptInterface {
        private final CallJavaResultInterface callJavaResultInterface;

        public JavaScriptInterface(CallJavaResultInterface callJavaResultInterface2) {
            this.callJavaResultInterface = callJavaResultInterface2;
        }

        @JavascriptInterface
        public void returnResultToJava(String str) {
            this.callJavaResultInterface.jsCallFinished(str);
        }
    }

    /* access modifiers changed from: protected */
    public String downloadUrlContent(AsyncTask asyncTask, String str) {
        return downloadUrlContent(asyncTask, str, (HashMap<String, String>) null, true);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:34|35|36|37) */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x007b, code lost:
        if (r10 == 303) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x016c, code lost:
        if (r3 == -1) goto L_0x0177;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:36:0x00dd */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x018a A[SYNTHETIC, Splitter:B:106:0x018a] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0199  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x019e A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0129 A[SYNTHETIC, Splitter:B:61:0x0129] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String downloadUrlContent(android.os.AsyncTask r19, java.lang.String r20, java.util.HashMap<java.lang.String, java.lang.String> r21, boolean r22) {
        /*
            r18 = this;
            java.lang.String r0 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7"
            java.lang.String r1 = "Accept-Charset"
            java.lang.String r2 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
            java.lang.String r3 = "Accept"
            java.lang.String r4 = "en-us,en;q=0.5"
            java.lang.String r5 = "Accept-Language"
            java.lang.String r6 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)"
            java.lang.String r7 = "User-Agent"
            r8 = 1
            java.net.URL r11 = new java.net.URL     // Catch:{ all -> 0x00f2 }
            r12 = r20
            r11.<init>(r12)     // Catch:{ all -> 0x00f2 }
            java.net.URLConnection r12 = r11.openConnection()     // Catch:{ all -> 0x00f2 }
            r12.addRequestProperty(r7, r6)     // Catch:{ all -> 0x00f0 }
            java.lang.String r13 = "gzip, deflate"
            java.lang.String r14 = "Accept-Encoding"
            if (r22 == 0) goto L_0x0028
            r12.addRequestProperty(r14, r13)     // Catch:{ all -> 0x00f0 }
        L_0x0028:
            r12.addRequestProperty(r5, r4)     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r3, r2)     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r1, r0)     // Catch:{ all -> 0x00f0 }
            if (r21 == 0) goto L_0x005b
            java.util.Set r15 = r21.entrySet()     // Catch:{ all -> 0x00f0 }
            java.util.Iterator r15 = r15.iterator()     // Catch:{ all -> 0x00f0 }
        L_0x003b:
            boolean r16 = r15.hasNext()     // Catch:{ all -> 0x00f0 }
            if (r16 == 0) goto L_0x005b
            java.lang.Object r16 = r15.next()     // Catch:{ all -> 0x00f0 }
            java.util.Map$Entry r16 = (java.util.Map.Entry) r16     // Catch:{ all -> 0x00f0 }
            java.lang.Object r17 = r16.getKey()     // Catch:{ all -> 0x00f0 }
            r9 = r17
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x00f0 }
            java.lang.Object r16 = r16.getValue()     // Catch:{ all -> 0x00f0 }
            r10 = r16
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r9, r10)     // Catch:{ all -> 0x00f0 }
            goto L_0x003b
        L_0x005b:
            r9 = 5000(0x1388, float:7.006E-42)
            r12.setConnectTimeout(r9)     // Catch:{ all -> 0x00f0 }
            r12.setReadTimeout(r9)     // Catch:{ all -> 0x00f0 }
            boolean r9 = r12 instanceof java.net.HttpURLConnection     // Catch:{ all -> 0x00f0 }
            if (r9 == 0) goto L_0x00ce
            r9 = r12
            java.net.HttpURLConnection r9 = (java.net.HttpURLConnection) r9     // Catch:{ all -> 0x00f0 }
            r9.setInstanceFollowRedirects(r8)     // Catch:{ all -> 0x00f0 }
            int r10 = r9.getResponseCode()     // Catch:{ all -> 0x00f0 }
            r15 = 302(0x12e, float:4.23E-43)
            if (r10 == r15) goto L_0x007d
            r15 = 301(0x12d, float:4.22E-43)
            if (r10 == r15) goto L_0x007d
            r15 = 303(0x12f, float:4.25E-43)
            if (r10 != r15) goto L_0x00ce
        L_0x007d:
            java.lang.String r10 = "Location"
            java.lang.String r10 = r9.getHeaderField(r10)     // Catch:{ all -> 0x00f0 }
            java.lang.String r11 = "Set-Cookie"
            java.lang.String r9 = r9.getHeaderField(r11)     // Catch:{ all -> 0x00f0 }
            java.net.URL r11 = new java.net.URL     // Catch:{ all -> 0x00f0 }
            r11.<init>(r10)     // Catch:{ all -> 0x00f0 }
            java.net.URLConnection r12 = r11.openConnection()     // Catch:{ all -> 0x00f0 }
            java.lang.String r10 = "Cookie"
            r12.setRequestProperty(r10, r9)     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r7, r6)     // Catch:{ all -> 0x00f0 }
            if (r22 == 0) goto L_0x009f
            r12.addRequestProperty(r14, r13)     // Catch:{ all -> 0x00f0 }
        L_0x009f:
            r12.addRequestProperty(r5, r4)     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r3, r2)     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r1, r0)     // Catch:{ all -> 0x00f0 }
            if (r21 == 0) goto L_0x00ce
            java.util.Set r0 = r21.entrySet()     // Catch:{ all -> 0x00f0 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x00f0 }
        L_0x00b2:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x00f0 }
            if (r1 == 0) goto L_0x00ce
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x00f0 }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x00f0 }
            java.lang.Object r2 = r1.getKey()     // Catch:{ all -> 0x00f0 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x00f0 }
            java.lang.Object r1 = r1.getValue()     // Catch:{ all -> 0x00f0 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ all -> 0x00f0 }
            r12.addRequestProperty(r2, r1)     // Catch:{ all -> 0x00f0 }
            goto L_0x00b2
        L_0x00ce:
            r12.connect()     // Catch:{ all -> 0x00f0 }
            if (r22 == 0) goto L_0x00e9
            java.util.zip.GZIPInputStream r0 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x00dd }
            java.io.InputStream r1 = r12.getInputStream()     // Catch:{ Exception -> 0x00dd }
            r0.<init>(r1)     // Catch:{ Exception -> 0x00dd }
            goto L_0x00ed
        L_0x00dd:
            java.net.URLConnection r12 = r11.openConnection()     // Catch:{ all -> 0x00f0 }
            r12.connect()     // Catch:{ all -> 0x00f0 }
            java.io.InputStream r0 = r12.getInputStream()     // Catch:{ all -> 0x00f0 }
            goto L_0x00ed
        L_0x00e9:
            java.io.InputStream r0 = r12.getInputStream()     // Catch:{ all -> 0x00f0 }
        L_0x00ed:
            r1 = r0
            r0 = 1
            goto L_0x0127
        L_0x00f0:
            r0 = move-exception
            goto L_0x00f4
        L_0x00f2:
            r0 = move-exception
            r12 = 0
        L_0x00f4:
            boolean r1 = r0 instanceof java.net.SocketTimeoutException
            if (r1 == 0) goto L_0x00ff
            boolean r1 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
            if (r1 == 0) goto L_0x0121
            goto L_0x0103
        L_0x00ff:
            boolean r1 = r0 instanceof java.net.UnknownHostException
            if (r1 == 0) goto L_0x0105
        L_0x0103:
            r1 = 0
            goto L_0x0122
        L_0x0105:
            boolean r1 = r0 instanceof java.net.SocketException
            if (r1 == 0) goto L_0x011c
            java.lang.String r1 = r0.getMessage()
            if (r1 == 0) goto L_0x0121
            java.lang.String r1 = r0.getMessage()
            java.lang.String r2 = "ECONNRESET"
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x0121
            goto L_0x0103
        L_0x011c:
            boolean r1 = r0 instanceof java.io.FileNotFoundException
            if (r1 == 0) goto L_0x0121
            goto L_0x0103
        L_0x0121:
            r1 = 1
        L_0x0122:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r1
            r1 = 0
        L_0x0127:
            if (r0 == 0) goto L_0x0194
            boolean r0 = r12 instanceof java.net.HttpURLConnection     // Catch:{ Exception -> 0x013a }
            if (r0 == 0) goto L_0x013e
            java.net.HttpURLConnection r12 = (java.net.HttpURLConnection) r12     // Catch:{ Exception -> 0x013a }
            int r0 = r12.getResponseCode()     // Catch:{ Exception -> 0x013a }
            r2 = 200(0xc8, float:2.8E-43)
            if (r0 == r2) goto L_0x013e
            r2 = 202(0xca, float:2.83E-43)
            goto L_0x013e
        L_0x013a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x013e:
            if (r1 == 0) goto L_0x0185
            r0 = 32768(0x8000, float:4.5918E-41)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x017e }
            r2 = 0
        L_0x0146:
            boolean r3 = r19.isCancelled()     // Catch:{ all -> 0x017b }
            if (r3 == 0) goto L_0x014e
            r6 = 0
            goto L_0x016f
        L_0x014e:
            int r3 = r1.read(r0)     // Catch:{ Exception -> 0x0171 }
            if (r3 <= 0) goto L_0x016a
            if (r2 != 0) goto L_0x015c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0171 }
            r4.<init>()     // Catch:{ Exception -> 0x0171 }
            r2 = r4
        L_0x015c:
            java.lang.String r4 = new java.lang.String     // Catch:{ Exception -> 0x0171 }
            java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ Exception -> 0x0171 }
            r6 = 0
            r4.<init>(r0, r6, r3, r5)     // Catch:{ Exception -> 0x0168 }
            r2.append(r4)     // Catch:{ Exception -> 0x0168 }
            goto L_0x0146
        L_0x0168:
            r0 = move-exception
            goto L_0x0173
        L_0x016a:
            r6 = 0
            r0 = -1
            if (r3 != r0) goto L_0x016f
            goto L_0x0177
        L_0x016f:
            r8 = 0
            goto L_0x0177
        L_0x0171:
            r0 = move-exception
            r6 = 0
        L_0x0173:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0179 }
            goto L_0x016f
        L_0x0177:
            r10 = r8
            goto L_0x0188
        L_0x0179:
            r0 = move-exception
            goto L_0x0181
        L_0x017b:
            r0 = move-exception
            r6 = 0
            goto L_0x0181
        L_0x017e:
            r0 = move-exception
            r6 = 0
            r2 = 0
        L_0x0181:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0187
        L_0x0185:
            r6 = 0
            r2 = 0
        L_0x0187:
            r10 = 0
        L_0x0188:
            if (r1 == 0) goto L_0x0197
            r1.close()     // Catch:{ all -> 0x018e }
            goto L_0x0197
        L_0x018e:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0197
        L_0x0194:
            r6 = 0
            r2 = 0
            r10 = 0
        L_0x0197:
            if (r10 == 0) goto L_0x019e
            java.lang.String r9 = r2.toString()
            goto L_0x019f
        L_0x019e:
            r9 = 0
        L_0x019f:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String str) {
            this.videoId = str;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.lang.String[]} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v15, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v2, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v3, resolved type: java.lang.Object} */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:124:0x02ed  */
        /* JADX WARNING: Removed duplicated region for block: B:162:0x03ea  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String[] doInBackground(java.lang.Void... r24) {
            /*
                r23 = this;
                r1 = r23
                java.lang.String r2 = "UTF-8"
                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "https://www.youtube.com/embed/"
                r3.append(r4)
                java.lang.String r4 = r1.videoId
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                java.lang.String r3 = r0.downloadUrlContent(r1, r3)
                boolean r0 = r23.isCancelled()
                r4 = 0
                if (r0 == 0) goto L_0x0025
                return r4
            L_0x0025:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r5 = "video_id="
                r0.append(r5)
                java.lang.String r5 = r1.videoId
                r0.append(r5)
                java.lang.String r5 = "&ps=default&gl=US&hl=en"
                r0.append(r5)
                java.lang.String r5 = r0.toString()
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0069 }
                r0.<init>()     // Catch:{ Exception -> 0x0069 }
                r0.append(r5)     // Catch:{ Exception -> 0x0069 }
                java.lang.String r6 = "&eurl="
                r0.append(r6)     // Catch:{ Exception -> 0x0069 }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0069 }
                r6.<init>()     // Catch:{ Exception -> 0x0069 }
                java.lang.String r7 = "https://youtube.googleapis.com/v/"
                r6.append(r7)     // Catch:{ Exception -> 0x0069 }
                java.lang.String r7 = r1.videoId     // Catch:{ Exception -> 0x0069 }
                r6.append(r7)     // Catch:{ Exception -> 0x0069 }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0069 }
                java.lang.String r6 = java.net.URLEncoder.encode(r6, r2)     // Catch:{ Exception -> 0x0069 }
                r0.append(r6)     // Catch:{ Exception -> 0x0069 }
                java.lang.String r5 = r0.toString()     // Catch:{ Exception -> 0x0069 }
                goto L_0x006d
            L_0x0069:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x006d:
                if (r3 == 0) goto L_0x00af
                java.util.regex.Pattern r0 = org.telegram.ui.Components.WebPlayerView.stsPattern
                java.util.regex.Matcher r0 = r0.matcher(r3)
                boolean r6 = r0.find()
                java.lang.String r7 = "&sts="
                if (r6 == 0) goto L_0x00a0
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r5)
                r6.append(r7)
                int r5 = r0.start()
                int r5 = r5 + 6
                int r0 = r0.end()
                java.lang.String r0 = r3.substring(r5, r0)
                r6.append(r0)
                java.lang.String r5 = r6.toString()
                goto L_0x00af
            L_0x00a0:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r5)
                r0.append(r7)
                java.lang.String r5 = r0.toString()
            L_0x00af:
                java.lang.String[] r0 = r1.result
                java.lang.String r6 = "dash"
                r7 = 1
                r0[r7] = r6
                r6 = 5
                java.lang.String[] r8 = new java.lang.String[r6]
                java.lang.String r0 = ""
                r9 = 0
                r8[r9] = r0
                java.lang.String r0 = "&el=leanback"
                r8[r7] = r0
                java.lang.String r0 = "&el=embedded"
                r10 = 2
                r8[r10] = r0
                java.lang.String r0 = "&el=detailpage"
                r11 = 3
                r8[r11] = r0
                r0 = 4
                java.lang.String r12 = "&el=vevo"
                r8[r0] = r12
                r13 = r4
                r0 = 0
                r12 = 0
            L_0x00d4:
                java.lang.String r14 = "/s/"
                if (r12 >= r6) goto L_0x0273
                org.telegram.ui.Components.WebPlayerView r15 = org.telegram.ui.Components.WebPlayerView.this
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r11 = "https://www.youtube.com/get_video_info?"
                r6.append(r11)
                r6.append(r5)
                r11 = r8[r12]
                r6.append(r11)
                java.lang.String r6 = r6.toString()
                java.lang.String r6 = r15.downloadUrlContent(r1, r6)
                boolean r11 = r23.isCancelled()
                if (r11 == 0) goto L_0x00fb
                return r4
            L_0x00fb:
                if (r6 == 0) goto L_0x0243
                java.lang.String r11 = "&"
                java.lang.String[] r6 = r6.split(r11)
                r11 = r0
                r16 = r4
                r15 = 0
                r17 = 0
                r18 = 0
            L_0x010b:
                int r0 = r6.length
                if (r15 >= r0) goto L_0x023d
                r0 = r6[r15]
                java.lang.String r4 = "dashmpd"
                boolean r0 = r0.startsWith(r4)
                java.lang.String r4 = "="
                if (r0 == 0) goto L_0x0138
                r0 = r6[r15]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                if (r4 != r10) goto L_0x0132
                java.lang.String[] r4 = r1.result     // Catch:{ Exception -> 0x012e }
                r0 = r0[r7]     // Catch:{ Exception -> 0x012e }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x012e }
                r4[r9] = r0     // Catch:{ Exception -> 0x012e }
                goto L_0x0132
            L_0x012e:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0132:
                r22 = r5
                r18 = 1
                goto L_0x0233
            L_0x0138:
                r0 = r6[r15]
                java.lang.String r9 = "url_encoded_fmt_stream_map"
                boolean r0 = r0.startsWith(r9)
                if (r0 == 0) goto L_0x01c4
                r0 = r6[r15]
                java.lang.String[] r0 = r0.split(r4)
                int r9 = r0.length
                if (r9 != r10) goto L_0x01b8
                r0 = r0[r7]     // Catch:{ Exception -> 0x01bc }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01bc }
                java.lang.String r9 = "[&,]"
                java.lang.String[] r0 = r0.split(r9)     // Catch:{ Exception -> 0x01bc }
                r9 = 0
                r10 = 0
                r20 = 0
            L_0x015b:
                int r7 = r0.length     // Catch:{ Exception -> 0x01bc }
                if (r10 >= r7) goto L_0x01b8
                r7 = r0[r10]     // Catch:{ Exception -> 0x01bc }
                java.lang.String[] r7 = r7.split(r4)     // Catch:{ Exception -> 0x01bc }
                r21 = r0
                r19 = 0
                r0 = r7[r19]     // Catch:{ Exception -> 0x01bc }
                r22 = r5
                java.lang.String r5 = "type"
                boolean r0 = r0.startsWith(r5)     // Catch:{ Exception -> 0x01b6 }
                if (r0 == 0) goto L_0x0186
                r5 = 1
                r0 = r7[r5]     // Catch:{ Exception -> 0x01b6 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01b6 }
                java.lang.String r5 = "video/mp4"
                boolean r0 = r0.contains(r5)     // Catch:{ Exception -> 0x01b6 }
                if (r0 == 0) goto L_0x01a8
                r20 = 1
                goto L_0x01a8
            L_0x0186:
                r5 = 0
                r0 = r7[r5]     // Catch:{ Exception -> 0x01b6 }
                java.lang.String r5 = "url"
                boolean r0 = r0.startsWith(r5)     // Catch:{ Exception -> 0x01b6 }
                if (r0 == 0) goto L_0x019a
                r5 = 1
                r0 = r7[r5]     // Catch:{ Exception -> 0x01b6 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01b6 }
                r9 = r0
                goto L_0x01a8
            L_0x019a:
                r5 = 0
                r0 = r7[r5]     // Catch:{ Exception -> 0x01b6 }
                java.lang.String r5 = "itag"
                boolean r0 = r0.startsWith(r5)     // Catch:{ Exception -> 0x01b6 }
                if (r0 == 0) goto L_0x01a8
                r9 = 0
                r20 = 0
            L_0x01a8:
                if (r20 == 0) goto L_0x01af
                if (r9 == 0) goto L_0x01af
                r13 = r9
                goto L_0x0233
            L_0x01af:
                int r10 = r10 + 1
                r0 = r21
                r5 = r22
                goto L_0x015b
            L_0x01b6:
                r0 = move-exception
                goto L_0x01bf
            L_0x01b8:
                r22 = r5
                goto L_0x0233
            L_0x01bc:
                r0 = move-exception
                r22 = r5
            L_0x01bf:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0233
            L_0x01c4:
                r22 = r5
                r0 = r6[r15]
                java.lang.String r5 = "use_cipher_signature"
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x01eb
                r0 = r6[r15]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r5 = 2
                if (r4 != r5) goto L_0x0233
                r4 = 1
                r0 = r0[r4]
                java.lang.String r0 = r0.toLowerCase()
                java.lang.String r4 = "true"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x0233
                r11 = 1
                goto L_0x0233
            L_0x01eb:
                r0 = r6[r15]
                java.lang.String r5 = "hlsvp"
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x020e
                r0 = r6[r15]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r5 = 2
                if (r4 != r5) goto L_0x0233
                r4 = 1
                r0 = r0[r4]     // Catch:{ Exception -> 0x0209 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x0209 }
                r16 = r0
                goto L_0x0233
            L_0x0209:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0233
            L_0x020e:
                r0 = r6[r15]
                java.lang.String r5 = "livestream"
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x0233
                r0 = r6[r15]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r5 = 2
                if (r4 != r5) goto L_0x0233
                r4 = 1
                r0 = r0[r4]
                java.lang.String r0 = r0.toLowerCase()
                java.lang.String r4 = "1"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x0233
                r17 = 1
            L_0x0233:
                int r15 = r15 + 1
                r5 = r22
                r4 = 0
                r7 = 1
                r9 = 0
                r10 = 2
                goto L_0x010b
            L_0x023d:
                r22 = r5
                r0 = r11
                r4 = r16
                goto L_0x024a
            L_0x0243:
                r22 = r5
                r4 = 0
                r17 = 0
                r18 = 0
            L_0x024a:
                if (r17 == 0) goto L_0x0264
                if (r4 == 0) goto L_0x0262
                if (r0 != 0) goto L_0x0262
                boolean r5 = r4.contains(r14)
                if (r5 == 0) goto L_0x0257
                goto L_0x0262
            L_0x0257:
                java.lang.String[] r5 = r1.result
                r6 = 0
                r5[r6] = r4
                java.lang.String r4 = "hls"
                r6 = 1
                r5[r6] = r4
                goto L_0x0264
            L_0x0262:
                r2 = 0
                return r2
            L_0x0264:
                if (r18 == 0) goto L_0x0267
                goto L_0x0273
            L_0x0267:
                int r12 = r12 + 1
                r5 = r22
                r4 = 0
                r6 = 5
                r7 = 1
                r9 = 0
                r10 = 2
                r11 = 3
                goto L_0x00d4
            L_0x0273:
                java.lang.String[] r2 = r1.result
                r4 = 0
                r5 = r2[r4]
                if (r5 != 0) goto L_0x0283
                if (r13 == 0) goto L_0x0283
                r2[r4] = r13
                java.lang.String r5 = "other"
                r6 = 1
                r2[r6] = r5
            L_0x0283:
                java.lang.String[] r2 = r1.result
                r5 = r2[r4]
                if (r5 == 0) goto L_0x0460
                if (r0 != 0) goto L_0x0293
                r2 = r2[r4]
                boolean r2 = r2.contains(r14)
                if (r2 == 0) goto L_0x0460
            L_0x0293:
                if (r3 == 0) goto L_0x0460
                java.lang.String[] r0 = r1.result
                r0 = r0[r4]
                int r0 = r0.indexOf(r14)
                java.lang.String[] r2 = r1.result
                r2 = r2[r4]
                r5 = 47
                int r6 = r0 + 10
                int r2 = r2.indexOf(r5, r6)
                r5 = -1
                if (r0 == r5) goto L_0x045c
                if (r2 != r5) goto L_0x02b6
                java.lang.String[] r2 = r1.result
                r2 = r2[r4]
                int r2 = r2.length()
            L_0x02b6:
                java.lang.String[] r5 = r1.result
                r5 = r5[r4]
                java.lang.String r0 = r5.substring(r0, r2)
                r1.sig = r0
                java.util.regex.Pattern r0 = org.telegram.ui.Components.WebPlayerView.jsPattern
                java.util.regex.Matcher r0 = r0.matcher(r3)
                boolean r2 = r0.find()
                if (r2 == 0) goto L_0x02ea
                org.json.JSONTokener r2 = new org.json.JSONTokener     // Catch:{ Exception -> 0x02e6 }
                r3 = 1
                java.lang.String r0 = r0.group(r3)     // Catch:{ Exception -> 0x02e6 }
                r2.<init>(r0)     // Catch:{ Exception -> 0x02e6 }
                java.lang.Object r0 = r2.nextValue()     // Catch:{ Exception -> 0x02e6 }
                boolean r2 = r0 instanceof java.lang.String     // Catch:{ Exception -> 0x02e6 }
                if (r2 == 0) goto L_0x02e3
                java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x02e6 }
                goto L_0x02e4
            L_0x02e3:
                r0 = 0
            L_0x02e4:
                r2 = r0
                goto L_0x02eb
            L_0x02e6:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x02ea:
                r2 = 0
            L_0x02eb:
                if (r2 == 0) goto L_0x045c
                java.util.regex.Pattern r0 = org.telegram.ui.Components.WebPlayerView.playerIdPattern
                java.util.regex.Matcher r0 = r0.matcher(r2)
                boolean r3 = r0.find()
                if (r3 == 0) goto L_0x0315
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r4 = 1
                java.lang.String r5 = r0.group(r4)
                r3.append(r5)
                r4 = 2
                java.lang.String r0 = r0.group(r4)
                r3.append(r0)
                java.lang.String r0 = r3.toString()
                goto L_0x0316
            L_0x0315:
                r0 = 0
            L_0x0316:
                android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
                java.lang.String r4 = "youtubecode"
                r5 = 0
                android.content.SharedPreferences r3 = r3.getSharedPreferences(r4, r5)
                java.lang.String r4 = "n"
                if (r0 == 0) goto L_0x033c
                r6 = 0
                java.lang.String r7 = r3.getString(r0, r6)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r0)
                r8.append(r4)
                java.lang.String r8 = r8.toString()
                java.lang.String r8 = r3.getString(r8, r6)
                goto L_0x033e
            L_0x033c:
                r7 = 0
                r8 = 0
            L_0x033e:
                if (r7 != 0) goto L_0x03e2
                java.lang.String r6 = "//"
                boolean r6 = r2.startsWith(r6)
                if (r6 == 0) goto L_0x035a
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r9 = "https:"
                r6.append(r9)
                r6.append(r2)
                java.lang.String r2 = r6.toString()
                goto L_0x0373
            L_0x035a:
                java.lang.String r6 = "/"
                boolean r6 = r2.startsWith(r6)
                if (r6 == 0) goto L_0x0373
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r9 = "https://www.youtube.com"
                r6.append(r9)
                r6.append(r2)
                java.lang.String r2 = r6.toString()
            L_0x0373:
                org.telegram.ui.Components.WebPlayerView r6 = org.telegram.ui.Components.WebPlayerView.this
                java.lang.String r2 = r6.downloadUrlContent(r1, r2)
                boolean r6 = r23.isCancelled()
                if (r6 == 0) goto L_0x0381
                r6 = 0
                return r6
            L_0x0381:
                r6 = 0
                if (r2 == 0) goto L_0x03e3
                java.util.regex.Pattern r9 = org.telegram.ui.Components.WebPlayerView.sigPattern
                java.util.regex.Matcher r9 = r9.matcher(r2)
                boolean r10 = r9.find()
                if (r10 == 0) goto L_0x0398
                r10 = 1
                java.lang.String r8 = r9.group(r10)
                goto L_0x03ab
            L_0x0398:
                r10 = 1
                java.util.regex.Pattern r9 = org.telegram.ui.Components.WebPlayerView.sigPattern2
                java.util.regex.Matcher r9 = r9.matcher(r2)
                boolean r11 = r9.find()
                if (r11 == 0) goto L_0x03ab
                java.lang.String r8 = r9.group(r10)
            L_0x03ab:
                if (r8 == 0) goto L_0x03e4
                org.telegram.ui.Components.WebPlayerView$JSExtractor r9 = new org.telegram.ui.Components.WebPlayerView$JSExtractor     // Catch:{ Exception -> 0x03dd }
                r9.<init>(r2)     // Catch:{ Exception -> 0x03dd }
                java.lang.String r7 = r9.extractFunction(r8)     // Catch:{ Exception -> 0x03dd }
                boolean r2 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x03dd }
                if (r2 != 0) goto L_0x03e4
                if (r0 == 0) goto L_0x03e4
                android.content.SharedPreferences$Editor r2 = r3.edit()     // Catch:{ Exception -> 0x03dd }
                android.content.SharedPreferences$Editor r2 = r2.putString(r0, r7)     // Catch:{ Exception -> 0x03dd }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03dd }
                r3.<init>()     // Catch:{ Exception -> 0x03dd }
                r3.append(r0)     // Catch:{ Exception -> 0x03dd }
                r3.append(r4)     // Catch:{ Exception -> 0x03dd }
                java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x03dd }
                android.content.SharedPreferences$Editor r0 = r2.putString(r0, r8)     // Catch:{ Exception -> 0x03dd }
                r0.commit()     // Catch:{ Exception -> 0x03dd }
                goto L_0x03e4
            L_0x03dd:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x03e4
            L_0x03e2:
                r6 = 0
            L_0x03e3:
                r10 = 1
            L_0x03e4:
                boolean r0 = android.text.TextUtils.isEmpty(r7)
                if (r0 != 0) goto L_0x045e
                int r0 = android.os.Build.VERSION.SDK_INT
                r2 = 21
                java.lang.String r3 = "('"
                if (r0 < r2) goto L_0x0414
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r7)
                r0.append(r8)
                r0.append(r3)
                java.lang.String r2 = r1.sig
                r3 = 3
                java.lang.String r2 = r2.substring(r3)
                r0.append(r2)
                java.lang.String r2 = "');"
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                goto L_0x0448
            L_0x0414:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r7)
                java.lang.String r2 = "window."
                r0.append(r2)
                org.telegram.ui.Components.WebPlayerView r2 = org.telegram.ui.Components.WebPlayerView.this
                java.lang.String r2 = r2.interfaceName
                r0.append(r2)
                java.lang.String r2 = ".returnResultToJava("
                r0.append(r2)
                r0.append(r8)
                r0.append(r3)
                java.lang.String r2 = r1.sig
                r3 = 3
                java.lang.String r2 = r2.substring(r3)
                r0.append(r2)
                java.lang.String r2 = "'));"
                r0.append(r2)
                java.lang.String r0 = r0.toString()
            L_0x0448:
                org.telegram.ui.Components.-$$Lambda$WebPlayerView$YoutubeVideoTask$GMLQkdVjUFyM84BTj7n250BCLASSNAMEg r2 = new org.telegram.ui.Components.-$$Lambda$WebPlayerView$YoutubeVideoTask$GMLQkdVjUFyM84BTj7n250BCLASSNAMEg     // Catch:{ Exception -> 0x0457 }
                r2.<init>(r0)     // Catch:{ Exception -> 0x0457 }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x0457 }
                java.util.concurrent.CountDownLatch r0 = r1.countDownLatch     // Catch:{ Exception -> 0x0457 }
                r0.await()     // Catch:{ Exception -> 0x0457 }
                r7 = 0
                goto L_0x0462
            L_0x0457:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x045e
            L_0x045c:
                r6 = 0
                r10 = 1
            L_0x045e:
                r7 = 1
                goto L_0x0462
            L_0x0460:
                r6 = 0
                r7 = r0
            L_0x0462:
                boolean r0 = r23.isCancelled()
                if (r0 != 0) goto L_0x046e
                if (r7 == 0) goto L_0x046b
                goto L_0x046e
            L_0x046b:
                java.lang.String[] r4 = r1.result
                goto L_0x046f
            L_0x046e:
                r4 = r6
            L_0x046f:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.YoutubeVideoTask.doInBackground(java.lang.Void[]):java.lang.String[]");
        }

        public /* synthetic */ void lambda$doInBackground$1$WebPlayerView$YoutubeVideoTask(String str) {
            if (Build.VERSION.SDK_INT >= 21) {
                WebPlayerView.this.webView.evaluateJavascript(str, new ValueCallback() {
                    public final void onReceiveValue(Object obj) {
                        WebPlayerView.YoutubeVideoTask.this.lambda$null$0$WebPlayerView$YoutubeVideoTask((String) obj);
                    }
                });
                return;
            }
            try {
                String encodeToString = Base64.encodeToString(("<script>" + str + "</script>").getBytes(StandardCharsets.UTF_8), 0);
                WebView access$2100 = WebPlayerView.this.webView;
                access$2100.loadUrl("data:text/html;charset=utf-8;base64," + encodeToString);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public /* synthetic */ void lambda$null$0$WebPlayerView$YoutubeVideoTask(String str) {
            String[] strArr = this.result;
            String str2 = strArr[0];
            String str3 = this.sig;
            strArr[0] = str2.replace(str3, "/signature/" + str.substring(1, str.length() - 1));
            this.countDownLatch.countDown();
        }

        /* access modifiers changed from: private */
        public void onInterfaceResult(String str) {
            String[] strArr = this.result;
            String str2 = strArr[0];
            String str3 = this.sig;
            strArr[0] = str2.replace(str3, "/signature/" + str);
            this.countDownLatch.countDown();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String[] strArr) {
            if (strArr[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start play youtube video " + strArr[1] + " " + strArr[0]);
                }
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = strArr[0];
                String unused3 = WebPlayerView.this.playVideoType = strArr[1];
                if (WebPlayerView.this.playVideoType.equals("hls")) {
                    boolean unused4 = WebPlayerView.this.isStream = true;
                }
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String str) {
            this.videoId = str;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(downloadUrlContent).getJSONObject("request").getJSONObject("files");
                if (jSONObject.has("hls")) {
                    JSONObject jSONObject2 = jSONObject.getJSONObject("hls");
                    try {
                        this.results[0] = jSONObject2.getString("url");
                    } catch (Exception unused) {
                        this.results[0] = jSONObject2.getJSONObject("cdns").getJSONObject(jSONObject2.getString("default_cdn")).getString("url");
                    }
                    this.results[1] = "hls";
                } else if (jSONObject.has("progressive")) {
                    this.results[1] = "other";
                    this.results[0] = jSONObject.getJSONArray("progressive").getJSONObject(0).getString("url");
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = str;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String str) {
            this.videoId = str;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher matcher = WebPlayerView.aparatFileListPattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    JSONArray jSONArray = new JSONArray(matcher.group(1));
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                        if (jSONArray2.length() != 0) {
                            JSONObject jSONObject = jSONArray2.getJSONObject(0);
                            if (jSONObject.has("file")) {
                                this.results[0] = jSONObject.getString("file");
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = str;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class TwitchClipVideoTask extends AsyncTask<Void, Void, String> {
        private String currentUrl;
        private String[] results = new String[2];

        public TwitchClipVideoTask(String str, String str2) {
            this.currentUrl = str;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, (HashMap<String, String>) null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher matcher = WebPlayerView.twitchClipFilePattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    this.results[0] = new JSONObject(matcher.group(1)).getJSONArray("quality_options").getJSONObject(0).getString("source");
                    this.results[1] = "other";
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = str;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class TwitchStreamVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[2];
        private String videoId;

        public TwitchStreamVideoTask(String str, String str2) {
            this.videoId = str2;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voidArr) {
            HashMap hashMap = new HashMap();
            hashMap.put("Client-ID", "jzkbprfvar_iqj646a697cyrvl0zt2m6");
            int indexOf = this.videoId.indexOf(38);
            if (indexOf > 0) {
                this.videoId = this.videoId.substring(0, indexOf);
            }
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[]{this.videoId}), hashMap, false);
            if (isCancelled()) {
                return null;
            }
            try {
                new JSONObject(downloadUrlContent).getJSONObject("stream");
                JSONObject jSONObject = new JSONObject(WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[]{this.videoId}), hashMap, false));
                String encode = URLEncoder.encode(jSONObject.getString("sig"), "UTF-8");
                String encode2 = URLEncoder.encode(jSONObject.getString("token"), "UTF-8");
                URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
                this.results[0] = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[]{this.videoId, "allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=" + ((int) (Math.random() * 1.0E7d)) + "&sig=" + encode + "&token=" + encode2});
                this.results[1] = "hls";
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = str;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String str) {
            this.videoId = str;
        }

        private String decodeUrl(String str) {
            StringBuilder sb = new StringBuilder(str);
            for (int i = 0; i < sb.length(); i++) {
                char charAt = sb.charAt(i);
                char lowerCase = Character.toLowerCase(charAt);
                if (charAt == lowerCase) {
                    lowerCase = Character.toUpperCase(charAt);
                }
                sb.setCharAt(i, lowerCase);
            }
            try {
                return new String(Base64.decode(sb.toString(), 0), StandardCharsets.UTF_8);
            } catch (Exception unused) {
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(downloadUrlContent).getJSONObject("file_versions").getJSONObject("mobile");
                String decodeUrl = decodeUrl(jSONObject.getString("gifv"));
                String string = jSONObject.getJSONArray("audio").getString(0);
                if (!(decodeUrl == null || string == null)) {
                    this.results[0] = decodeUrl;
                    this.results[1] = "other";
                    this.results[2] = string;
                    this.results[3] = "other";
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = str;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                String unused4 = WebPlayerView.this.playAudioUrl = this.results[2];
                String unused5 = WebPlayerView.this.playAudioType = this.results[3];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class ControlsView extends FrameLayout {
        private int bufferedPosition;
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private Runnable hideRunnable = new Runnable() {
            public final void run() {
                WebPlayerView.ControlsView.this.lambda$new$0$WebPlayerView$ControlsView();
            }
        };
        /* access modifiers changed from: private */
        public ImageReceiver imageReceiver;
        private boolean isVisible = true;
        private int lastProgressX;
        private int progress;
        private Paint progressBufferedPaint;
        private Paint progressInnerPaint;
        private StaticLayout progressLayout;
        private Paint progressPaint;
        private boolean progressPressed;
        private TextPaint textPaint;

        public /* synthetic */ void lambda$new$0$WebPlayerView$ControlsView() {
            show(false, true);
        }

        public ControlsView(Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setColor(-1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            Paint paint = new Paint(1);
            this.progressPaint = paint;
            paint.setColor(-15095832);
            Paint paint2 = new Paint();
            this.progressInnerPaint = paint2;
            paint2.setColor(-6975081);
            Paint paint3 = new Paint(1);
            this.progressBufferedPaint = paint3;
            paint3.setColor(-1);
            this.imageReceiver = new ImageReceiver(this);
        }

        public void setDuration(int i) {
            if (this.duration != i && i >= 0 && !WebPlayerView.this.isStream) {
                this.duration = i;
                StaticLayout staticLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.duration), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.durationLayout = staticLayout;
                if (staticLayout.getLineCount() > 0) {
                    this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                }
                invalidate();
            }
        }

        public void setBufferedProgress(int i) {
            this.bufferedPosition = i;
            invalidate();
        }

        public void setProgress(int i) {
            if (!this.progressPressed && i >= 0 && !WebPlayerView.this.isStream) {
                this.progress = i;
                this.progressLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.progress), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                invalidate();
            }
        }

        public void show(boolean z, boolean z2) {
            if (this.isVisible != z) {
                this.isVisible = z;
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                if (this.isVisible) {
                    if (z2) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.currentAnimation = animatorSet2;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{1.0f})});
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = ControlsView.this.currentAnimation = null;
                            }
                        });
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (z2) {
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.currentAnimation = animatorSet3;
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = ControlsView.this.currentAnimation = null;
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    setAlpha(0.0f);
                }
                checkNeedHide();
            }
        }

        /* access modifiers changed from: private */
        public void checkNeedHide() {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            if (this.isVisible && WebPlayerView.this.videoPlayer.isPlaying()) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            if (!this.isVisible) {
                show(true, true);
                return true;
            }
            onTouchEvent(motionEvent);
            return this.progressPressed;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            checkNeedHide();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int i;
            int i2;
            int i3;
            if (WebPlayerView.this.inFullscreen) {
                i3 = AndroidUtilities.dp(36.0f) + this.durationWidth;
                i2 = (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                i = getMeasuredHeight() - AndroidUtilities.dp(28.0f);
            } else {
                i2 = getMeasuredWidth();
                i = getMeasuredHeight() - AndroidUtilities.dp(12.0f);
                i3 = 0;
            }
            int i4 = this.duration;
            int i5 = (i4 != 0 ? (int) (((float) (i2 - i3)) * (((float) this.progress) / ((float) i4))) : 0) + i3;
            if (motionEvent.getAction() == 0) {
                if (!this.isVisible || WebPlayerView.this.isInline || WebPlayerView.this.isStream) {
                    show(true, true);
                } else if (this.duration != 0) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    if (x >= i5 - AndroidUtilities.dp(10.0f) && x <= AndroidUtilities.dp(10.0f) + i5 && y >= i - AndroidUtilities.dp(10.0f) && y <= i + AndroidUtilities.dp(10.0f)) {
                        this.progressPressed = true;
                        this.lastProgressX = x;
                        this.currentProgressX = i5;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (WebPlayerView.this.initied && WebPlayerView.this.videoPlayer.isPlaying()) {
                    AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
                }
                if (this.progressPressed) {
                    this.progressPressed = false;
                    if (WebPlayerView.this.initied) {
                        this.progress = (int) (((float) this.duration) * (((float) (this.currentProgressX - i3)) / ((float) (i2 - i3))));
                        WebPlayerView.this.videoPlayer.seekTo(((long) this.progress) * 1000);
                    }
                }
            } else if (motionEvent.getAction() == 2 && this.progressPressed) {
                int x2 = (int) motionEvent.getX();
                int i6 = this.currentProgressX - (this.lastProgressX - x2);
                this.currentProgressX = i6;
                this.lastProgressX = x2;
                if (i6 < i3) {
                    this.currentProgressX = i3;
                } else if (i6 > i2) {
                    this.currentProgressX = i2;
                }
                setProgress((int) (((float) (this.duration * 1000)) * (((float) (this.currentProgressX - i3)) / ((float) (i2 - i3)))));
                invalidate();
            }
            super.onTouchEvent(motionEvent);
            return true;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x014f  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x0163  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0166  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0195  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0198  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x01be  */
        /* JADX WARNING: Removed duplicated region for block: B:66:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r16) {
            /*
                r15 = this;
                r0 = r15
                r7 = r16
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.drawImage
                if (r1 == 0) goto L_0x005f
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.firstFrameRendered
                if (r1 == 0) goto L_0x004f
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                float r1 = r1.currentAlpha
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x004f
                long r3 = java.lang.System.currentTimeMillis()
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                long r5 = r1.lastUpdateTime
                long r5 = r3 - r5
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                long unused = r1.lastUpdateTime = r3
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                float r3 = r1.currentAlpha
                float r4 = (float) r5
                r5 = 1125515264(0x43160000, float:150.0)
                float r4 = r4 / r5
                float r3 = r3 - r4
                float unused = r1.currentAlpha = r3
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                float r1 = r1.currentAlpha
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 >= 0) goto L_0x004c
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                float unused = r1.currentAlpha = r2
            L_0x004c:
                r15.invalidate()
            L_0x004f:
                org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
                org.telegram.ui.Components.WebPlayerView r2 = org.telegram.ui.Components.WebPlayerView.this
                float r2 = r2.currentAlpha
                r1.setAlpha(r2)
                org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
                r1.draw(r7)
            L_0x005f:
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                org.telegram.ui.Components.VideoPlayer r1 = r1.videoPlayer
                boolean r1 = r1.isPlayerPrepared()
                if (r1 == 0) goto L_0x01d0
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.isStream
                if (r1 != 0) goto L_0x01d0
                int r1 = r15.getMeasuredWidth()
                int r2 = r15.getMeasuredHeight()
                org.telegram.ui.Components.WebPlayerView r3 = org.telegram.ui.Components.WebPlayerView.this
                boolean r3 = r3.isInline
                if (r3 != 0) goto L_0x00e8
                android.text.StaticLayout r3 = r0.durationLayout
                r4 = 6
                r5 = 10
                if (r3 == 0) goto L_0x00ba
                r16.save()
                r3 = 1114112000(0x42680000, float:58.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = r1 - r3
                int r6 = r0.durationWidth
                int r3 = r3 - r6
                float r3 = (float) r3
                org.telegram.ui.Components.WebPlayerView r6 = org.telegram.ui.Components.WebPlayerView.this
                boolean r6 = r6.inFullscreen
                if (r6 == 0) goto L_0x00a3
                r6 = 6
                goto L_0x00a5
            L_0x00a3:
                r6 = 10
            L_0x00a5:
                int r6 = r6 + 29
                float r6 = (float) r6
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = r2 - r6
                float r6 = (float) r6
                r7.translate(r3, r6)
                android.text.StaticLayout r3 = r0.durationLayout
                r3.draw(r7)
                r16.restore()
            L_0x00ba:
                android.text.StaticLayout r3 = r0.progressLayout
                if (r3 == 0) goto L_0x00e8
                r16.save()
                r3 = 1099956224(0x41900000, float:18.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                org.telegram.ui.Components.WebPlayerView r6 = org.telegram.ui.Components.WebPlayerView.this
                boolean r6 = r6.inFullscreen
                if (r6 == 0) goto L_0x00d1
                goto L_0x00d3
            L_0x00d1:
                r4 = 10
            L_0x00d3:
                int r4 = r4 + 29
                float r4 = (float) r4
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = r2 - r4
                float r4 = (float) r4
                r7.translate(r3, r4)
                android.text.StaticLayout r3 = r0.progressLayout
                r3.draw(r7)
                r16.restore()
            L_0x00e8:
                int r3 = r0.duration
                if (r3 == 0) goto L_0x01d0
                org.telegram.ui.Components.WebPlayerView r3 = org.telegram.ui.Components.WebPlayerView.this
                boolean r3 = r3.isInline
                r8 = 1088421888(0x40e00000, float:7.0)
                r4 = 0
                r9 = 1077936128(0x40400000, float:3.0)
                if (r3 == 0) goto L_0x0109
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r3 = r2 - r3
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            L_0x0103:
                int r2 = r2 - r5
                r10 = r1
                r11 = r2
                r12 = r3
                r13 = 0
                goto L_0x0147
            L_0x0109:
                org.telegram.ui.Components.WebPlayerView r3 = org.telegram.ui.Components.WebPlayerView.this
                boolean r3 = r3.inFullscreen
                if (r3 == 0) goto L_0x0138
                r3 = 1105723392(0x41e80000, float:29.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = r2 - r3
                r4 = 1108344832(0x42100000, float:36.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r5 = r0.durationWidth
                int r4 = r4 + r5
                r5 = 1117257728(0x42980000, float:76.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r1 = r1 - r5
                int r5 = r0.durationWidth
                int r1 = r1 - r5
                r5 = 1105199104(0x41e00000, float:28.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 - r5
                r10 = r1
                r11 = r2
                r12 = r3
                r13 = r4
                goto L_0x0147
            L_0x0138:
                r3 = 1095761920(0x41500000, float:13.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = r2 - r3
                r5 = 1094713344(0x41400000, float:12.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                goto L_0x0103
            L_0x0147:
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.inFullscreen
                if (r1 == 0) goto L_0x015f
                float r2 = (float) r13
                float r3 = (float) r12
                float r4 = (float) r10
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r1 = r1 + r12
                float r5 = (float) r1
                android.graphics.Paint r6 = r0.progressInnerPaint
                r1 = r16
                r1.drawRect(r2, r3, r4, r5, r6)
            L_0x015f:
                boolean r1 = r0.progressPressed
                if (r1 == 0) goto L_0x0166
                int r1 = r0.currentProgressX
                goto L_0x0174
            L_0x0166:
                int r1 = r10 - r13
                float r1 = (float) r1
                int r2 = r0.progress
                float r2 = (float) r2
                int r3 = r0.duration
                float r3 = (float) r3
                float r2 = r2 / r3
                float r1 = r1 * r2
                int r1 = (int) r1
                int r1 = r1 + r13
            L_0x0174:
                r14 = r1
                int r1 = r0.bufferedPosition
                if (r1 == 0) goto L_0x01a5
                int r2 = r0.duration
                if (r2 == 0) goto L_0x01a5
                float r3 = (float) r13
                float r4 = (float) r12
                int r10 = r10 - r13
                float r5 = (float) r10
                float r1 = (float) r1
                float r2 = (float) r2
                float r1 = r1 / r2
                float r5 = r5 * r1
                float r5 = r5 + r3
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r1 = r1 + r12
                float r6 = (float) r1
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.inFullscreen
                if (r1 == 0) goto L_0x0198
                android.graphics.Paint r1 = r0.progressBufferedPaint
                goto L_0x019a
            L_0x0198:
                android.graphics.Paint r1 = r0.progressInnerPaint
            L_0x019a:
                r10 = r1
                r1 = r16
                r2 = r3
                r3 = r4
                r4 = r5
                r5 = r6
                r6 = r10
                r1.drawRect(r2, r3, r4, r5, r6)
            L_0x01a5:
                float r2 = (float) r13
                float r3 = (float) r12
                float r10 = (float) r14
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r12 = r12 + r1
                float r5 = (float) r12
                android.graphics.Paint r6 = r0.progressPaint
                r1 = r16
                r4 = r10
                r1.drawRect(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.isInline
                if (r1 != 0) goto L_0x01d0
                float r1 = (float) r11
                boolean r2 = r0.progressPressed
                if (r2 == 0) goto L_0x01c4
                goto L_0x01c6
            L_0x01c4:
                r8 = 1084227584(0x40a00000, float:5.0)
            L_0x01c6:
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r2 = (float) r2
                android.graphics.Paint r3 = r0.progressPaint
                r7.drawCircle(r10, r1, r2, r3)
            L_0x01d0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.ControlsView.onDraw(android.graphics.Canvas):void");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean z, boolean z2, WebPlayerViewDelegate webPlayerViewDelegate) {
        super(context);
        lastContainerId++;
        this.allowInlineAnimation = Build.VERSION.SDK_INT >= 21;
        this.backgroundPaint = new Paint();
        this.progressRunnable = new Runnable() {
            public void run() {
                if (WebPlayerView.this.videoPlayer != null && WebPlayerView.this.videoPlayer.isPlaying()) {
                    WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                    WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000));
                    AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000);
                }
            }
        };
        this.surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (!WebPlayerView.this.changingTextureView) {
                    return true;
                }
                if (WebPlayerView.this.switchingInlineMode) {
                    int unused = WebPlayerView.this.waitingForFirstTextureUpload = 2;
                }
                WebPlayerView.this.textureView.setSurfaceTexture(surfaceTexture);
                WebPlayerView.this.textureView.setVisibility(0);
                boolean unused2 = WebPlayerView.this.changingTextureView = false;
                return false;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                if (WebPlayerView.this.waitingForFirstTextureUpload == 1) {
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (WebPlayerView.this.textureImageView != null) {
                                WebPlayerView.this.textureImageView.setVisibility(4);
                                WebPlayerView.this.textureImageView.setImageDrawable((Drawable) null);
                                if (WebPlayerView.this.currentBitmap != null) {
                                    WebPlayerView.this.currentBitmap.recycle();
                                    Bitmap unused = WebPlayerView.this.currentBitmap = null;
                                }
                            }
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0052: INVOKE  
                                  (wrap: org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ : 0x004f: CONSTRUCTOR  (r0v7 org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ) = 
                                  (r2v0 'this' org.telegram.ui.Components.WebPlayerView$2$1 A[THIS])
                                 call: org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ.<init>(org.telegram.ui.Components.WebPlayerView$2$1):void type: CONSTRUCTOR)
                                 org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.WebPlayerView.2.1.onPreDraw():boolean, dex: classes3.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004f: CONSTRUCTOR  (r0v7 org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ) = 
                                  (r2v0 'this' org.telegram.ui.Components.WebPlayerView$2$1 A[THIS])
                                 call: org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ.<init>(org.telegram.ui.Components.WebPlayerView$2$1):void type: CONSTRUCTOR in method: org.telegram.ui.Components.WebPlayerView.2.1.onPreDraw():boolean, dex: classes3.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 98 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 104 more
                                */
                            /*
                                this = this;
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.view.TextureView r0 = r0.changedTextureView
                                android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
                                r0.removeOnPreDrawListener(r2)
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.widget.ImageView r0 = r0.textureImageView
                                if (r0 == 0) goto L_0x004d
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.widget.ImageView r0 = r0.textureImageView
                                r1 = 4
                                r0.setVisibility(r1)
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.widget.ImageView r0 = r0.textureImageView
                                r1 = 0
                                r0.setImageDrawable(r1)
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.graphics.Bitmap r0 = r0.currentBitmap
                                if (r0 == 0) goto L_0x004d
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.graphics.Bitmap r0 = r0.currentBitmap
                                r0.recycle()
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                android.graphics.Bitmap unused = r0.currentBitmap = r1
                            L_0x004d:
                                org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ r0 = new org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ
                                r0.<init>(r2)
                                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                                org.telegram.ui.Components.WebPlayerView$2 r0 = org.telegram.ui.Components.WebPlayerView.AnonymousClass2.this
                                org.telegram.ui.Components.WebPlayerView r0 = org.telegram.ui.Components.WebPlayerView.this
                                r1 = 0
                                int unused = r0.waitingForFirstTextureUpload = r1
                                r0 = 1
                                return r0
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.AnonymousClass2.AnonymousClass1.onPreDraw():boolean");
                        }

                        public /* synthetic */ void lambda$onPreDraw$0$WebPlayerView$2$1() {
                            WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                        }
                    });
                    WebPlayerView.this.changedTextureView.invalidate();
                }
            }
        };
        this.switchToInlineRunnable = new Runnable() {
            public void run() {
                boolean unused = WebPlayerView.this.switchingInlineMode = false;
                if (WebPlayerView.this.currentBitmap != null) {
                    WebPlayerView.this.currentBitmap.recycle();
                    Bitmap unused2 = WebPlayerView.this.currentBitmap = null;
                }
                boolean unused3 = WebPlayerView.this.changingTextureView = true;
                if (WebPlayerView.this.textureImageView != null) {
                    try {
                        Bitmap unused4 = WebPlayerView.this.currentBitmap = Bitmaps.createBitmap(WebPlayerView.this.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                        WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
                    } catch (Throwable th) {
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            Bitmap unused5 = WebPlayerView.this.currentBitmap = null;
                        }
                        FileLog.e(th);
                    }
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.textureImageView.setVisibility(0);
                        WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
                    } else {
                        WebPlayerView.this.textureImageView.setImageDrawable((Drawable) null);
                    }
                }
                boolean unused6 = WebPlayerView.this.isInline = true;
                WebPlayerView.this.updatePlayButton();
                WebPlayerView.this.updateShareButton();
                WebPlayerView.this.updateFullscreenButton();
                WebPlayerView.this.updateInlineButton();
                ViewGroup viewGroup = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(WebPlayerView.this.controlsView);
                }
                WebPlayerView webPlayerView = WebPlayerView.this;
                TextureView unused7 = webPlayerView.changedTextureView = webPlayerView.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation);
                WebPlayerView.this.changedTextureView.setVisibility(4);
                ViewGroup viewGroup2 = (ViewGroup) WebPlayerView.this.textureView.getParent();
                if (viewGroup2 != null) {
                    viewGroup2.removeView(WebPlayerView.this.textureView);
                }
                WebPlayerView.this.controlsView.show(false, false);
            }
        };
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(-16777216);
        AnonymousClass4 r14 = new AspectRatioFrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (WebPlayerView.this.textureViewContainer != null) {
                    ViewGroup.LayoutParams layoutParams = WebPlayerView.this.textureView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                    if (WebPlayerView.this.textureImageView != null) {
                        ViewGroup.LayoutParams layoutParams2 = WebPlayerView.this.textureImageView.getLayoutParams();
                        layoutParams2.width = getMeasuredWidth();
                        layoutParams2.height = getMeasuredHeight();
                    }
                }
            }
        };
        this.aspectRatioFrameLayout = r14;
        addView(r14, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        WebView webView2 = new WebView(context);
        this.webView = webView2;
        webView2.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() {
            public final void jsCallFinished(String str) {
                WebPlayerView.this.lambda$new$0$WebPlayerView(str);
            }
        }), this.interfaceName);
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        TextureView textureView2 = new TextureView(context);
        this.textureView = textureView2;
        textureView2.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (this.allowInlineAnimation && this.textureViewContainer != null) {
            ImageView imageView = new ImageView(context);
            this.textureImageView = imageView;
            imageView.setBackgroundColor(-65536);
            this.textureImageView.setPivotX(0.0f);
            this.textureImageView.setPivotY(0.0f);
            this.textureImageView.setVisibility(4);
            this.textureViewContainer.addView(this.textureImageView);
        }
        VideoPlayer videoPlayer2 = new VideoPlayer();
        this.videoPlayer = videoPlayer2;
        videoPlayer2.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        ControlsView controlsView2 = new ControlsView(context);
        this.controlsView = controlsView2;
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 != null) {
            viewGroup2.addView(controlsView2);
        } else {
            addView(controlsView2, LayoutHelper.createFrame(-1, -1.0f));
        }
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        ImageView imageView2 = new ImageView(context);
        this.fullscreenButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$1$WebPlayerView(view);
            }
        });
        ImageView imageView3 = new ImageView(context);
        this.playButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$2$WebPlayerView(view);
            }
        });
        if (z) {
            ImageView imageView4 = new ImageView(context);
            this.inlineButton = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$3$WebPlayerView(view);
                }
            });
        }
        if (z2) {
            ImageView imageView5 = new ImageView(context);
            this.shareButton = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setImageResource(NUM);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$4$WebPlayerView(view);
                }
            });
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    public /* synthetic */ void lambda$new$0$WebPlayerView(String str) {
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null && !asyncTask.isCancelled()) {
            AsyncTask asyncTask2 = this.currentTask;
            if (asyncTask2 instanceof YoutubeVideoTask) {
                ((YoutubeVideoTask) asyncTask2).onInterfaceResult(str);
            }
        }
    }

    public /* synthetic */ void lambda$new$1$WebPlayerView(View view) {
        if (this.initied && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.inFullscreen = !this.inFullscreen;
            updateFullscreenState(true);
        }
    }

    public /* synthetic */ void lambda$new$2$WebPlayerView(View view) {
        if (this.initied && this.playVideoUrl != null) {
            if (!this.videoPlayer.isPlayerPrepared()) {
                preparePlayer();
            }
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
            } else {
                this.isCompleted = false;
                this.videoPlayer.play();
            }
            updatePlayButton();
        }
    }

    public /* synthetic */ void lambda$new$3$WebPlayerView(View view) {
        if (this.textureView != null && this.delegate.checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.switchingInlineMode = true;
            if (!this.isInline) {
                this.inFullscreen = false;
                this.delegate.prepareToSwitchInlineMode(true, this.switchToInlineRunnable, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
                return;
            }
            ViewGroup viewGroup = (ViewGroup) this.aspectRatioFrameLayout.getParent();
            if (viewGroup != this) {
                if (viewGroup != null) {
                    viewGroup.removeView(this.aspectRatioFrameLayout);
                }
                addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - AndroidUtilities.dp(10.0f), NUM));
            }
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
            this.isInline = false;
            updatePlayButton();
            updateShareButton();
            updateFullscreenButton();
            updateInlineButton();
            this.textureView.setVisibility(4);
            ViewGroup viewGroup2 = this.textureViewContainer;
            if (viewGroup2 != null) {
                viewGroup2.addView(this.textureView);
            } else {
                this.aspectRatioFrameLayout.addView(this.textureView);
            }
            ViewGroup viewGroup3 = (ViewGroup) this.controlsView.getParent();
            if (viewGroup3 != this) {
                if (viewGroup3 != null) {
                    viewGroup3.removeView(this.controlsView);
                }
                ViewGroup viewGroup4 = this.textureViewContainer;
                if (viewGroup4 != null) {
                    viewGroup4.addView(this.controlsView);
                } else {
                    addView(this.controlsView, 1);
                }
            }
            this.controlsView.show(false, false);
            this.delegate.prepareToSwitchInlineMode(false, (Runnable) null, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
        }
    }

    public /* synthetic */ void lambda$new$4$WebPlayerView(View view) {
        WebPlayerViewDelegate webPlayerViewDelegate = this.delegate;
        if (webPlayerViewDelegate != null) {
            webPlayerViewDelegate.onSharePressed();
        }
    }

    /* access modifiers changed from: private */
    public void onInitFailed() {
        if (this.controlsView.getParent() != this) {
            this.controlsView.setVisibility(8);
        }
        this.delegate.onInitFailed();
    }

    public void updateTextureImageView() {
        if (this.textureImageView != null) {
            try {
                Bitmap createBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                this.currentBitmap = createBitmap;
                this.changedTextureView.getBitmap(createBitmap);
            } catch (Throwable th) {
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
                FileLog.e(th);
            }
            if (this.currentBitmap != null) {
                this.textureImageView.setVisibility(0);
                this.textureImageView.setImageBitmap(this.currentBitmap);
                return;
            }
            this.textureImageView.setImageDrawable((Drawable) null);
        }
    }

    public String getYoutubeId() {
        return this.currentYoutubeId;
    }

    public void onStateChanged(boolean z, int i) {
        if (i != 2) {
            if (this.videoPlayer.getDuration() != -9223372036854775807L) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
        }
        if (i == 4 || i == 1 || !this.videoPlayer.isPlaying()) {
            this.delegate.onPlayStateChanged(this, false);
        } else {
            this.delegate.onPlayStateChanged(this, true);
        }
        if (this.videoPlayer.isPlaying() && i != 4) {
            updatePlayButton();
        } else if (i == 4) {
            this.isCompleted = true;
            this.videoPlayer.pause();
            this.videoPlayer.seekTo(0);
            updatePlayButton();
            this.controlsView.show(true, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(10.0f)), this.backgroundPaint);
    }

    public void onError(VideoPlayer videoPlayer2, Exception exc) {
        FileLog.e((Throwable) exc);
        onInitFailed();
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        if (this.aspectRatioFrameLayout != null) {
            if (!(i3 == 90 || i3 == 270)) {
                int i4 = i2;
                i2 = i;
                i = i4;
            }
            float f2 = i == 0 ? 1.0f : (((float) i2) * f) / ((float) i);
            this.aspectRatioFrameLayout.setAspectRatio(f2, i3);
            if (this.inFullscreen) {
                this.delegate.onVideoSizeChanged(f2, i3);
            }
        }
    }

    public void onRenderedFirstFrame() {
        this.firstFrameRendered = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.controlsView.invalidate();
    }

    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        if (this.changingTextureView) {
            this.changingTextureView = false;
            if (this.inFullscreen || this.isInline) {
                if (this.isInline) {
                    this.waitingForFirstTextureUpload = 1;
                }
                this.changedTextureView.setSurfaceTexture(surfaceTexture);
                this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
                this.changedTextureView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (this.waitingForFirstTextureUpload == 2) {
            ImageView imageView = this.textureImageView;
            if (imageView != null) {
                imageView.setVisibility(4);
                this.textureImageView.setImageDrawable((Drawable) null);
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
            }
            this.switchingInlineMode = false;
            this.delegate.onSwitchInlineMode(this.controlsView, false, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
            this.waitingForFirstTextureUpload = 0;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int measuredWidth = (i5 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int i6 = i4 - i2;
        int dp = ((i6 - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        aspectRatioFrameLayout2.layout(measuredWidth, dp, aspectRatioFrameLayout2.getMeasuredWidth() + measuredWidth, this.aspectRatioFrameLayout.getMeasuredHeight() + dp);
        if (this.controlsView.getParent() == this) {
            ControlsView controlsView2 = this.controlsView;
            controlsView2.layout(0, 0, controlsView2.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        int measuredWidth2 = (i5 - this.progressView.getMeasuredWidth()) / 2;
        int measuredHeight = (i6 - this.progressView.getMeasuredHeight()) / 2;
        RadialProgressView radialProgressView = this.progressView;
        radialProgressView.layout(measuredWidth2, measuredHeight, radialProgressView.getMeasuredWidth() + measuredWidth2, this.progressView.getMeasuredHeight() + measuredHeight);
        this.controlsView.imageReceiver.setImageCoords(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(10.0f)));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2 - AndroidUtilities.dp(10.0f), NUM));
        if (this.controlsView.getParent() == this) {
            this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
        }
        this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        setMeasuredDimension(size, size2);
    }

    /* access modifiers changed from: private */
    public void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (this.videoPlayer.isPlaying()) {
            this.playButton.setImageResource(this.isInline ? NUM : NUM);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            checkAudioFocus();
        } else if (this.isCompleted) {
            this.playButton.setImageResource(this.isInline ? NUM : NUM);
        } else {
            this.playButton.setImageResource(this.isInline ? NUM : NUM);
        }
    }

    private void checkAudioFocus() {
        if (!this.hasAudioFocus) {
            this.hasAudioFocus = true;
            int requestAudioFocus = ((AudioManager) ApplicationLoader.applicationContext.getSystemService("audio")).requestAudioFocus(this, 3, 1);
        }
    }

    public void onAudioFocusChange(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WebPlayerView.this.lambda$onAudioFocusChange$5$WebPlayerView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onAudioFocusChange$5$WebPlayerView(int i) {
        if (i == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
        } else if (i == 1) {
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (i != -3 && i == -2 && this.videoPlayer.isPlaying()) {
            this.resumeAudioOnFocusGain = true;
            this.videoPlayer.pause();
            updatePlayButton();
        }
    }

    /* access modifiers changed from: private */
    public void updateFullscreenButton() {
        if (!this.videoPlayer.isPlayerPrepared() || this.isInline) {
            this.fullscreenButton.setVisibility(8);
            return;
        }
        this.fullscreenButton.setVisibility(0);
        if (!this.inFullscreen) {
            this.fullscreenButton.setImageResource(NUM);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
            return;
        }
        this.fullscreenButton.setImageResource(NUM);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
    }

    /* access modifiers changed from: private */
    public void updateShareButton() {
        ImageView imageView = this.shareButton;
        if (imageView != null) {
            imageView.setVisibility((this.isInline || !this.videoPlayer.isPlayerPrepared()) ? 8 : 0);
        }
    }

    private View getControlView() {
        return this.controlsView;
    }

    private View getProgressView() {
        return this.progressView;
    }

    /* access modifiers changed from: private */
    public void updateInlineButton() {
        ImageView imageView = this.inlineButton;
        if (imageView != null) {
            imageView.setImageResource(this.isInline ? NUM : NUM);
            this.inlineButton.setVisibility(this.videoPlayer.isPlayerPrepared() ? 0 : 8);
            if (this.isInline) {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
            } else {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
            }
        }
    }

    /* access modifiers changed from: private */
    public void preparePlayer() {
        String str = this.playVideoUrl;
        if (str != null) {
            if (str == null || this.playAudioUrl == null) {
                this.videoPlayer.preparePlayer(Uri.parse(this.playVideoUrl), this.playVideoType);
            } else {
                this.videoPlayer.preparePlayerLoop(Uri.parse(str), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
            }
            this.videoPlayer.setPlayWhenReady(this.isAutoplay);
            if (this.videoPlayer.getDuration() != -9223372036854775807L) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
            updateFullscreenButton();
            updateShareButton();
            updateInlineButton();
            this.controlsView.invalidate();
            int i = this.seekToTime;
            if (i != -1) {
                this.videoPlayer.seekTo((long) (i * 1000));
            }
        }
    }

    public void pause() {
        this.videoPlayer.pause();
        updatePlayButton();
        this.controlsView.show(true, true);
    }

    private void updateFullscreenState(boolean z) {
        ViewGroup viewGroup;
        if (this.textureView != null) {
            updateFullscreenButton();
            ViewGroup viewGroup2 = this.textureViewContainer;
            if (viewGroup2 == null) {
                this.changingTextureView = true;
                if (!this.inFullscreen) {
                    if (viewGroup2 != null) {
                        viewGroup2.addView(this.textureView);
                    } else {
                        this.aspectRatioFrameLayout.addView(this.textureView);
                    }
                }
                if (this.inFullscreen) {
                    ViewGroup viewGroup3 = (ViewGroup) this.controlsView.getParent();
                    if (viewGroup3 != null) {
                        viewGroup3.removeView(this.controlsView);
                    }
                } else {
                    ViewGroup viewGroup4 = (ViewGroup) this.controlsView.getParent();
                    if (viewGroup4 != this) {
                        if (viewGroup4 != null) {
                            viewGroup4.removeView(this.controlsView);
                        }
                        ViewGroup viewGroup5 = this.textureViewContainer;
                        if (viewGroup5 != null) {
                            viewGroup5.addView(this.controlsView);
                        } else {
                            addView(this.controlsView, 1);
                        }
                    }
                }
                TextureView onSwitchToFullscreen = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
                this.changedTextureView = onSwitchToFullscreen;
                onSwitchToFullscreen.setVisibility(4);
                if (!(!this.inFullscreen || this.changedTextureView == null || (viewGroup = (ViewGroup) this.textureView.getParent()) == null)) {
                    viewGroup.removeView(this.textureView);
                }
                this.controlsView.checkNeedHide();
                return;
            }
            if (this.inFullscreen) {
                ViewGroup viewGroup6 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (viewGroup6 != null) {
                    viewGroup6.removeView(this.aspectRatioFrameLayout);
                }
            } else {
                ViewGroup viewGroup7 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (viewGroup7 != this) {
                    if (viewGroup7 != null) {
                        viewGroup7.removeView(this.aspectRatioFrameLayout);
                    }
                    addView(this.aspectRatioFrameLayout, 0);
                }
            }
            this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
        }
    }

    public void exitFullscreen() {
        if (this.inFullscreen) {
            this.inFullscreen = false;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInitied() {
        return this.initied;
    }

    public boolean isInline() {
        return this.isInline || this.switchingInlineMode;
    }

    public void enterFullscreen() {
        if (!this.inFullscreen) {
            this.inFullscreen = true;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInFullscreen() {
        return this.inFullscreen;
    }

    public String getYouTubeVideoId(String str) {
        Matcher matcher = youtubeIdRegex.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean canHandleUrl(String str) {
        if (str == null) {
            return false;
        }
        if (str.endsWith(".mp4")) {
            return true;
        }
        String str2 = null;
        try {
            Matcher matcher = youtubeIdRegex.matcher(str);
            if ((matcher.find() ? matcher.group(1) : null) != null) {
                return true;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            Matcher matcher2 = vimeoIdRegex.matcher(str);
            if ((matcher2.find() ? matcher2.group(3) : null) != null) {
                return true;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            Matcher matcher3 = aparatIdRegex.matcher(str);
            if ((matcher3.find() ? matcher3.group(1) : null) != null) {
                return true;
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        try {
            Matcher matcher4 = twitchClipIdRegex.matcher(str);
            if ((matcher4.find() ? matcher4.group(1) : null) != null) {
                return true;
            }
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
        try {
            Matcher matcher5 = twitchStreamIdRegex.matcher(str);
            if ((matcher5.find() ? matcher5.group(1) : null) != null) {
                return true;
            }
        } catch (Exception e5) {
            FileLog.e((Throwable) e5);
        }
        try {
            Matcher matcher6 = coubIdRegex.matcher(str);
            if (matcher6.find()) {
                str2 = matcher6.group(1);
            }
            if (str2 != null) {
                return true;
            }
            return false;
        } catch (Exception e6) {
            FileLog.e((Throwable) e6);
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a9 A[SYNTHETIC, Splitter:B:40:0x00a9] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c8 A[SYNTHETIC, Splitter:B:52:0x00c8] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e7 A[SYNTHETIC, Splitter:B:64:0x00e7] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0106 A[SYNTHETIC, Splitter:B:76:0x0106] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadVideo(java.lang.String r26, org.telegram.tgnet.TLRPC$Photo r27, java.lang.Object r28, java.lang.String r29, boolean r30) {
        /*
            r25 = this;
            r1 = r25
            r2 = r26
            r3 = r27
            java.lang.String r0 = "m"
            r4 = -1
            r1.seekToTime = r4
            r4 = 3
            r5 = 0
            r6 = 1
            r7 = 0
            if (r2 == 0) goto L_0x0125
            java.lang.String r8 = ".mp4"
            boolean r8 = r2.endsWith(r8)
            if (r8 == 0) goto L_0x0022
            r13 = r2
            r0 = r7
            r8 = r0
            r9 = r8
            r10 = r9
            r11 = r10
            r12 = r11
            goto L_0x012c
        L_0x0022:
            if (r29 == 0) goto L_0x006b
            android.net.Uri r8 = android.net.Uri.parse(r29)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r9 = "t"
            java.lang.String r9 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0067 }
            if (r9 != 0) goto L_0x0036
            java.lang.String r9 = "time_continue"
            java.lang.String r9 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0067 }
        L_0x0036:
            if (r9 == 0) goto L_0x006b
            boolean r8 = r9.contains(r0)     // Catch:{ Exception -> 0x0067 }
            if (r8 == 0) goto L_0x005c
            java.lang.String[] r0 = r9.split(r0)     // Catch:{ Exception -> 0x0067 }
            r8 = r0[r5]     // Catch:{ Exception -> 0x0067 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ Exception -> 0x0067 }
            int r8 = r8.intValue()     // Catch:{ Exception -> 0x0067 }
            int r8 = r8 * 60
            r0 = r0[r6]     // Catch:{ Exception -> 0x0067 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0067 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0067 }
            int r8 = r8 + r0
            r1.seekToTime = r8     // Catch:{ Exception -> 0x0067 }
            goto L_0x006b
        L_0x005c:
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ Exception -> 0x0067 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0067 }
            r1.seekToTime = r0     // Catch:{ Exception -> 0x0067 }
            goto L_0x006b
        L_0x0067:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0083 }
        L_0x006b:
            java.util.regex.Pattern r0 = youtubeIdRegex     // Catch:{ Exception -> 0x0083 }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x0083 }
            boolean r8 = r0.find()     // Catch:{ Exception -> 0x0083 }
            if (r8 == 0) goto L_0x007c
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x0083 }
            goto L_0x007d
        L_0x007c:
            r0 = r7
        L_0x007d:
            if (r0 == 0) goto L_0x0080
            goto L_0x0081
        L_0x0080:
            r0 = r7
        L_0x0081:
            r8 = r0
            goto L_0x0088
        L_0x0083:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r8 = r7
        L_0x0088:
            if (r8 != 0) goto L_0x00a6
            java.util.regex.Pattern r0 = vimeoIdRegex     // Catch:{ Exception -> 0x00a2 }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00a2 }
            boolean r9 = r0.find()     // Catch:{ Exception -> 0x00a2 }
            if (r9 == 0) goto L_0x009b
            java.lang.String r0 = r0.group(r4)     // Catch:{ Exception -> 0x00a2 }
            goto L_0x009c
        L_0x009b:
            r0 = r7
        L_0x009c:
            if (r0 == 0) goto L_0x009f
            goto L_0x00a0
        L_0x009f:
            r0 = r7
        L_0x00a0:
            r9 = r0
            goto L_0x00a7
        L_0x00a2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00a6:
            r9 = r7
        L_0x00a7:
            if (r9 != 0) goto L_0x00c5
            java.util.regex.Pattern r0 = aparatIdRegex     // Catch:{ Exception -> 0x00c1 }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00c1 }
            boolean r10 = r0.find()     // Catch:{ Exception -> 0x00c1 }
            if (r10 == 0) goto L_0x00ba
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00bb
        L_0x00ba:
            r0 = r7
        L_0x00bb:
            if (r0 == 0) goto L_0x00be
            goto L_0x00bf
        L_0x00be:
            r0 = r7
        L_0x00bf:
            r10 = r0
            goto L_0x00c6
        L_0x00c1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c5:
            r10 = r7
        L_0x00c6:
            if (r10 != 0) goto L_0x00e4
            java.util.regex.Pattern r0 = twitchClipIdRegex     // Catch:{ Exception -> 0x00e0 }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00e0 }
            boolean r11 = r0.find()     // Catch:{ Exception -> 0x00e0 }
            if (r11 == 0) goto L_0x00d9
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x00e0 }
            goto L_0x00da
        L_0x00d9:
            r0 = r7
        L_0x00da:
            if (r0 == 0) goto L_0x00dd
            goto L_0x00de
        L_0x00dd:
            r0 = r7
        L_0x00de:
            r11 = r0
            goto L_0x00e5
        L_0x00e0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00e4:
            r11 = r7
        L_0x00e5:
            if (r11 != 0) goto L_0x0103
            java.util.regex.Pattern r0 = twitchStreamIdRegex     // Catch:{ Exception -> 0x00ff }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00ff }
            boolean r12 = r0.find()     // Catch:{ Exception -> 0x00ff }
            if (r12 == 0) goto L_0x00f8
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x00ff }
            goto L_0x00f9
        L_0x00f8:
            r0 = r7
        L_0x00f9:
            if (r0 == 0) goto L_0x00fc
            goto L_0x00fd
        L_0x00fc:
            r0 = r7
        L_0x00fd:
            r12 = r0
            goto L_0x0104
        L_0x00ff:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0103:
            r12 = r7
        L_0x0104:
            if (r12 != 0) goto L_0x0122
            java.util.regex.Pattern r0 = coubIdRegex     // Catch:{ Exception -> 0x011e }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x011e }
            boolean r13 = r0.find()     // Catch:{ Exception -> 0x011e }
            if (r13 == 0) goto L_0x0117
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x011e }
            goto L_0x0118
        L_0x0117:
            r0 = r7
        L_0x0118:
            if (r0 == 0) goto L_0x011b
            goto L_0x011c
        L_0x011b:
            r0 = r7
        L_0x011c:
            r13 = r7
            goto L_0x012c
        L_0x011e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0122:
            r0 = r7
            r13 = r0
            goto L_0x012c
        L_0x0125:
            r0 = r7
            r8 = r0
            r9 = r8
            r10 = r9
            r11 = r10
            r12 = r11
            r13 = r12
        L_0x012c:
            r1.initied = r5
            r1.isCompleted = r5
            r14 = r30
            r1.isAutoplay = r14
            r1.playVideoUrl = r7
            r1.playAudioUrl = r7
            r25.destroy()
            r1.firstFrameRendered = r5
            r14 = 1065353216(0x3var_, float:1.0)
            r1.currentAlpha = r14
            android.os.AsyncTask r14 = r1.currentTask
            if (r14 == 0) goto L_0x014a
            r14.cancel(r6)
            r1.currentTask = r7
        L_0x014a:
            r25.updateFullscreenButton()
            r25.updateShareButton()
            r25.updateInlineButton()
            r25.updatePlayButton()
            if (r3 == 0) goto L_0x0180
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r3.sizes
            r15 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15, r6)
            if (r14 == 0) goto L_0x0182
            org.telegram.ui.Components.WebPlayerView$ControlsView r15 = r1.controlsView
            org.telegram.messenger.ImageReceiver r16 = r15.imageReceiver
            r17 = 0
            r18 = 0
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r14, (org.telegram.tgnet.TLRPC$Photo) r3)
            r21 = 0
            r22 = 0
            r24 = 1
            java.lang.String r20 = "80_80_b"
            r23 = r28
            r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24)
            r1.drawImage = r6
            goto L_0x0182
        L_0x0180:
            r1.drawImage = r5
        L_0x0182:
            android.animation.AnimatorSet r3 = r1.progressAnimation
            if (r3 == 0) goto L_0x018b
            r3.cancel()
            r1.progressAnimation = r7
        L_0x018b:
            org.telegram.ui.Components.WebPlayerView$ControlsView r3 = r1.controlsView
            r3.setProgress(r5)
            if (r8 == 0) goto L_0x0195
            r1.currentYoutubeId = r8
            r8 = r7
        L_0x0195:
            if (r13 == 0) goto L_0x01b0
            r1.initied = r6
            r1.playVideoUrl = r13
            java.lang.String r2 = "other"
            r1.playVideoType = r2
            boolean r2 = r1.isAutoplay
            if (r2 == 0) goto L_0x01a6
            r25.preparePlayer()
        L_0x01a6:
            r1.showProgress(r5, r5)
            org.telegram.ui.Components.WebPlayerView$ControlsView r2 = r1.controlsView
            r2.show(r6, r6)
            goto L_0x0247
        L_0x01b0:
            r3 = 2
            if (r8 == 0) goto L_0x01c9
            org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask
            r2.<init>(r8)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            goto L_0x023f
        L_0x01c9:
            if (r9 == 0) goto L_0x01e0
            org.telegram.ui.Components.WebPlayerView$VimeoVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$VimeoVideoTask
            r2.<init>(r9)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            goto L_0x023f
        L_0x01e0:
            if (r0 == 0) goto L_0x01f9
            org.telegram.ui.Components.WebPlayerView$CoubVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$CoubVideoTask
            r2.<init>(r0)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            r1.isStream = r6
            goto L_0x023f
        L_0x01f9:
            if (r10 == 0) goto L_0x0210
            org.telegram.ui.Components.WebPlayerView$AparatVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$AparatVideoTask
            r2.<init>(r10)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            goto L_0x023f
        L_0x0210:
            if (r11 == 0) goto L_0x0227
            org.telegram.ui.Components.WebPlayerView$TwitchClipVideoTask r14 = new org.telegram.ui.Components.WebPlayerView$TwitchClipVideoTask
            r14.<init>(r2, r11)
            java.util.concurrent.Executor r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r14.executeOnExecutor(r2, r4)
            r1.currentTask = r14
            goto L_0x023f
        L_0x0227:
            if (r12 == 0) goto L_0x023f
            org.telegram.ui.Components.WebPlayerView$TwitchStreamVideoTask r14 = new org.telegram.ui.Components.WebPlayerView$TwitchStreamVideoTask
            r14.<init>(r2, r12)
            java.util.concurrent.Executor r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r14.executeOnExecutor(r2, r4)
            r1.currentTask = r14
            r1.isStream = r6
        L_0x023f:
            org.telegram.ui.Components.WebPlayerView$ControlsView r2 = r1.controlsView
            r2.show(r5, r5)
            r1.showProgress(r6, r5)
        L_0x0247:
            if (r8 != 0) goto L_0x025e
            if (r9 != 0) goto L_0x025e
            if (r0 != 0) goto L_0x025e
            if (r10 != 0) goto L_0x025e
            if (r13 != 0) goto L_0x025e
            if (r11 != 0) goto L_0x025e
            if (r12 == 0) goto L_0x0256
            goto L_0x025e
        L_0x0256:
            org.telegram.ui.Components.WebPlayerView$ControlsView r0 = r1.controlsView
            r2 = 8
            r0.setVisibility(r2)
            return r5
        L_0x025e:
            org.telegram.ui.Components.WebPlayerView$ControlsView r0 = r1.controlsView
            r0.setVisibility(r5)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.loadVideo(java.lang.String, org.telegram.tgnet.TLRPC$Photo, java.lang.Object, java.lang.String, boolean):boolean");
    }

    public View getAspectRatioView() {
        return this.aspectRatioFrameLayout;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public ImageView getTextureImageView() {
        return this.textureImageView;
    }

    public View getControlsView() {
        return this.controlsView;
    }

    public void destroy() {
        this.videoPlayer.releasePlayer(false);
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    /* access modifiers changed from: private */
    public void showProgress(boolean z, boolean z2) {
        float f = 1.0f;
        if (z2) {
            AnimatorSet animatorSet = this.progressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.progressAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, "alpha", fArr);
            animatorSet2.playTogether(animatorArr);
            this.progressAnimation.setDuration(150);
            this.progressAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = WebPlayerView.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        RadialProgressView radialProgressView2 = this.progressView;
        if (!z) {
            f = 0.0f;
        }
        radialProgressView2.setAlpha(f);
    }
}
