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
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    /* access modifiers changed from: private */
    public static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
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
    private int audioFocus;
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
    private int fragment_container_id;
    private ImageView fullscreenButton;
    private boolean hasAudioFocus;
    /* access modifiers changed from: private */
    public boolean inFullscreen;
    private boolean initFailed;
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
    private boolean isLoading;
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

    private abstract class function {
        public abstract Object run(Object[] objArr);

        private function() {
        }
    }

    private class JSExtractor {
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

    public class JavaScriptInterface {
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
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:44:0x00e3 */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0195 A[SYNTHETIC, Splitter:B:116:0x0195] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01ab A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d9 A[SYNTHETIC, Splitter:B:42:0x00d9] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ef A[Catch:{ all -> 0x00fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A[SYNTHETIC, Splitter:B:71:0x0136] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String downloadUrlContent(android.os.AsyncTask r21, java.lang.String r22, java.util.HashMap<java.lang.String, java.lang.String> r23, boolean r24) {
        /*
            r20 = this;
            java.lang.String r0 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7"
            java.lang.String r1 = "Accept-Charset"
            java.lang.String r2 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
            java.lang.String r3 = "Accept"
            java.lang.String r4 = "en-us,en;q=0.5"
            java.lang.String r5 = "Accept-Language"
            java.lang.String r6 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)"
            java.lang.String r7 = "User-Agent"
            r8 = 1
            java.net.URL r11 = new java.net.URL     // Catch:{ all -> 0x0100 }
            r12 = r22
            r11.<init>(r12)     // Catch:{ all -> 0x0100 }
            java.net.URLConnection r12 = r11.openConnection()     // Catch:{ all -> 0x0100 }
            r12.addRequestProperty(r7, r6)     // Catch:{ all -> 0x00fe }
            java.lang.String r13 = "gzip, deflate"
            java.lang.String r14 = "Accept-Encoding"
            if (r24 == 0) goto L_0x0028
            r12.addRequestProperty(r14, r13)     // Catch:{ all -> 0x00fe }
        L_0x0028:
            r12.addRequestProperty(r5, r4)     // Catch:{ all -> 0x00fe }
            r12.addRequestProperty(r3, r2)     // Catch:{ all -> 0x00fe }
            r12.addRequestProperty(r1, r0)     // Catch:{ all -> 0x00fe }
            if (r23 == 0) goto L_0x005b
            java.util.Set r15 = r23.entrySet()     // Catch:{ all -> 0x00fe }
            java.util.Iterator r15 = r15.iterator()     // Catch:{ all -> 0x00fe }
        L_0x003b:
            boolean r16 = r15.hasNext()     // Catch:{ all -> 0x00fe }
            if (r16 == 0) goto L_0x005b
            java.lang.Object r16 = r15.next()     // Catch:{ all -> 0x00fe }
            java.util.Map$Entry r16 = (java.util.Map.Entry) r16     // Catch:{ all -> 0x00fe }
            java.lang.Object r17 = r16.getKey()     // Catch:{ all -> 0x00fe }
            r9 = r17
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x00fe }
            java.lang.Object r16 = r16.getValue()     // Catch:{ all -> 0x00fe }
            r10 = r16
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x00fe }
            r12.addRequestProperty(r9, r10)     // Catch:{ all -> 0x00fe }
            goto L_0x003b
        L_0x005b:
            r9 = 5000(0x1388, float:7.006E-42)
            r12.setConnectTimeout(r9)     // Catch:{ all -> 0x00fe }
            r12.setReadTimeout(r9)     // Catch:{ all -> 0x00fe }
            boolean r9 = r12 instanceof java.net.HttpURLConnection     // Catch:{ all -> 0x00fe }
            if (r9 == 0) goto L_0x00d3
            r9 = r12
            java.net.HttpURLConnection r9 = (java.net.HttpURLConnection) r9     // Catch:{ all -> 0x00fe }
            r9.setInstanceFollowRedirects(r8)     // Catch:{ all -> 0x00fe }
            int r10 = r9.getResponseCode()     // Catch:{ all -> 0x00fe }
            r15 = 302(0x12e, float:4.23E-43)
            if (r10 == r15) goto L_0x007d
            r15 = 301(0x12d, float:4.22E-43)
            if (r10 == r15) goto L_0x007d
            r15 = 303(0x12f, float:4.25E-43)
            if (r10 != r15) goto L_0x00d3
        L_0x007d:
            java.lang.String r10 = "Location"
            java.lang.String r10 = r9.getHeaderField(r10)     // Catch:{ all -> 0x00fe }
            java.lang.String r11 = "Set-Cookie"
            java.lang.String r9 = r9.getHeaderField(r11)     // Catch:{ all -> 0x00fe }
            java.net.URL r11 = new java.net.URL     // Catch:{ all -> 0x00fe }
            r11.<init>(r10)     // Catch:{ all -> 0x00fe }
            java.net.URLConnection r10 = r11.openConnection()     // Catch:{ all -> 0x00fe }
            java.lang.String r12 = "Cookie"
            r10.setRequestProperty(r12, r9)     // Catch:{ all -> 0x00d0 }
            r10.addRequestProperty(r7, r6)     // Catch:{ all -> 0x00d0 }
            if (r24 == 0) goto L_0x009f
            r10.addRequestProperty(r14, r13)     // Catch:{ all -> 0x00d0 }
        L_0x009f:
            r10.addRequestProperty(r5, r4)     // Catch:{ all -> 0x00d0 }
            r10.addRequestProperty(r3, r2)     // Catch:{ all -> 0x00d0 }
            r10.addRequestProperty(r1, r0)     // Catch:{ all -> 0x00d0 }
            if (r23 == 0) goto L_0x00ce
            java.util.Set r0 = r23.entrySet()     // Catch:{ all -> 0x00d0 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x00d0 }
        L_0x00b2:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x00d0 }
            if (r1 == 0) goto L_0x00ce
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x00d0 }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x00d0 }
            java.lang.Object r2 = r1.getKey()     // Catch:{ all -> 0x00d0 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x00d0 }
            java.lang.Object r1 = r1.getValue()     // Catch:{ all -> 0x00d0 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ all -> 0x00d0 }
            r10.addRequestProperty(r2, r1)     // Catch:{ all -> 0x00d0 }
            goto L_0x00b2
        L_0x00ce:
            r9 = r10
            goto L_0x00d4
        L_0x00d0:
            r0 = move-exception
            r12 = r10
            goto L_0x0102
        L_0x00d3:
            r9 = r12
        L_0x00d4:
            r9.connect()     // Catch:{ all -> 0x00fb }
            if (r24 == 0) goto L_0x00ef
            java.util.zip.GZIPInputStream r0 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x00e3 }
            java.io.InputStream r1 = r9.getInputStream()     // Catch:{ Exception -> 0x00e3 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x00e3 }
            goto L_0x00f3
        L_0x00e3:
            java.net.URLConnection r9 = r11.openConnection()     // Catch:{ all -> 0x00fb }
            r9.connect()     // Catch:{ all -> 0x00fb }
            java.io.InputStream r0 = r9.getInputStream()     // Catch:{ all -> 0x00fb }
            goto L_0x00f3
        L_0x00ef:
            java.io.InputStream r0 = r9.getInputStream()     // Catch:{ all -> 0x00fb }
        L_0x00f3:
            r19 = r9
            r9 = r0
            r0 = r19
            r12 = r0
            r1 = 1
            goto L_0x0134
        L_0x00fb:
            r0 = move-exception
            r12 = r9
            goto L_0x0102
        L_0x00fe:
            r0 = move-exception
            goto L_0x0102
        L_0x0100:
            r0 = move-exception
            r12 = 0
        L_0x0102:
            boolean r1 = r0 instanceof java.net.SocketTimeoutException
            if (r1 == 0) goto L_0x010d
            boolean r1 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
            if (r1 == 0) goto L_0x012f
            goto L_0x0111
        L_0x010d:
            boolean r1 = r0 instanceof java.net.UnknownHostException
            if (r1 == 0) goto L_0x0113
        L_0x0111:
            r1 = 0
            goto L_0x0130
        L_0x0113:
            boolean r1 = r0 instanceof java.net.SocketException
            if (r1 == 0) goto L_0x012a
            java.lang.String r1 = r0.getMessage()
            if (r1 == 0) goto L_0x012f
            java.lang.String r1 = r0.getMessage()
            java.lang.String r2 = "ECONNRESET"
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x012f
            goto L_0x0111
        L_0x012a:
            boolean r1 = r0 instanceof java.io.FileNotFoundException
            if (r1 == 0) goto L_0x012f
            goto L_0x0111
        L_0x012f:
            r1 = 1
        L_0x0130:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r9 = 0
        L_0x0134:
            if (r1 == 0) goto L_0x01a0
            boolean r0 = r12 instanceof java.net.HttpURLConnection     // Catch:{ Exception -> 0x0147 }
            if (r0 == 0) goto L_0x014b
            java.net.HttpURLConnection r12 = (java.net.HttpURLConnection) r12     // Catch:{ Exception -> 0x0147 }
            int r0 = r12.getResponseCode()     // Catch:{ Exception -> 0x0147 }
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 == r1) goto L_0x014b
            r1 = 202(0xca, float:2.83E-43)
            goto L_0x014b
        L_0x0147:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x014b:
            if (r9 == 0) goto L_0x0190
            r0 = 32768(0x8000, float:4.5918E-41)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x0189 }
            r1 = 0
        L_0x0153:
            boolean r2 = r21.isCancelled()     // Catch:{ all -> 0x0186 }
            if (r2 == 0) goto L_0x015b
            r5 = 0
            goto L_0x0182
        L_0x015b:
            int r2 = r9.read(r0)     // Catch:{ Exception -> 0x017d }
            if (r2 <= 0) goto L_0x0177
            if (r1 != 0) goto L_0x0169
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017d }
            r3.<init>()     // Catch:{ Exception -> 0x017d }
            r1 = r3
        L_0x0169:
            java.lang.String r3 = new java.lang.String     // Catch:{ Exception -> 0x017d }
            java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ Exception -> 0x017d }
            r5 = 0
            r3.<init>(r0, r5, r2, r4)     // Catch:{ Exception -> 0x0175 }
            r1.append(r3)     // Catch:{ Exception -> 0x0175 }
            goto L_0x0153
        L_0x0175:
            r0 = move-exception
            goto L_0x017f
        L_0x0177:
            r5 = 0
            r0 = -1
            if (r2 != r0) goto L_0x0182
            r5 = 1
            goto L_0x0182
        L_0x017d:
            r0 = move-exception
            r5 = 0
        L_0x017f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0184 }
        L_0x0182:
            r10 = r5
            goto L_0x0193
        L_0x0184:
            r0 = move-exception
            goto L_0x018c
        L_0x0186:
            r0 = move-exception
            r5 = 0
            goto L_0x018c
        L_0x0189:
            r0 = move-exception
            r5 = 0
            r1 = 0
        L_0x018c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0192
        L_0x0190:
            r5 = 0
            r1 = 0
        L_0x0192:
            r10 = 0
        L_0x0193:
            if (r9 == 0) goto L_0x019e
            r9.close()     // Catch:{ all -> 0x0199 }
            goto L_0x019e
        L_0x0199:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x019e:
            r5 = r10
            goto L_0x01a2
        L_0x01a0:
            r5 = 0
            r1 = 0
        L_0x01a2:
            if (r5 == 0) goto L_0x01ab
            java.lang.String r9 = r1.toString()
            r18 = r9
            goto L_0x01ad
        L_0x01ab:
            r18 = 0
        L_0x01ad:
            return r18
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private boolean canRetry = true;
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String str) {
            this.videoId = str;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: java.lang.String[]} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: java.lang.Object} */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:185:0x022d, code lost:
            r15 = r15;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:192:0x022d, code lost:
            r15 = r15;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x02dd  */
        /* JADX WARNING: Removed duplicated region for block: B:155:0x03dd  */
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
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006a }
                r0.<init>()     // Catch:{ Exception -> 0x006a }
                r0.append(r5)     // Catch:{ Exception -> 0x006a }
                java.lang.String r6 = "&eurl="
                r0.append(r6)     // Catch:{ Exception -> 0x006a }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006a }
                r6.<init>()     // Catch:{ Exception -> 0x006a }
                java.lang.String r7 = "https://youtube.googleapis.com/v/"
                r6.append(r7)     // Catch:{ Exception -> 0x006a }
                java.lang.String r7 = r1.videoId     // Catch:{ Exception -> 0x006a }
                r6.append(r7)     // Catch:{ Exception -> 0x006a }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x006a }
                java.lang.String r6 = java.net.URLEncoder.encode(r6, r2)     // Catch:{ Exception -> 0x006a }
                r0.append(r6)     // Catch:{ Exception -> 0x006a }
                java.lang.String r5 = r0.toString()     // Catch:{ Exception -> 0x006a }
                goto L_0x006e
            L_0x006a:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x006e:
                if (r3 == 0) goto L_0x00b0
                java.util.regex.Pattern r0 = org.telegram.ui.Components.WebPlayerView.stsPattern
                java.util.regex.Matcher r0 = r0.matcher(r3)
                boolean r6 = r0.find()
                java.lang.String r7 = "&sts="
                if (r6 == 0) goto L_0x00a1
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
                goto L_0x00b0
            L_0x00a1:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r5)
                r0.append(r7)
                java.lang.String r5 = r0.toString()
            L_0x00b0:
                java.lang.String[] r0 = r1.result
                r6 = 1
                java.lang.String r7 = "dash"
                r0[r6] = r7
                r0 = 5
                java.lang.String[] r7 = new java.lang.String[r0]
                r8 = 0
                java.lang.String r0 = ""
                r7[r8] = r0
                java.lang.String r0 = "&el=leanback"
                r7[r6] = r0
                r9 = 2
                java.lang.String r0 = "&el=embedded"
                r7[r9] = r0
                r10 = 3
                java.lang.String r0 = "&el=detailpage"
                r7[r10] = r0
                r0 = 4
                java.lang.String r11 = "&el=vevo"
                r7[r0] = r11
                r0 = r4
                r11 = 0
                r12 = 0
            L_0x00d5:
                int r13 = r7.length
                java.lang.String r14 = "/s/"
                if (r11 >= r13) goto L_0x0264
                org.telegram.ui.Components.WebPlayerView r13 = org.telegram.ui.Components.WebPlayerView.this
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                java.lang.String r10 = "https://www.youtube.com/get_video_info?"
                r15.append(r10)
                r15.append(r5)
                r10 = r7[r11]
                r15.append(r10)
                java.lang.String r10 = r15.toString()
                java.lang.String r10 = r13.downloadUrlContent(r1, r10)
                boolean r13 = r23.isCancelled()
                if (r13 == 0) goto L_0x00fd
                return r4
            L_0x00fd:
                if (r10 == 0) goto L_0x023a
                java.lang.String r13 = "&"
                java.lang.String[] r10 = r10.split(r13)
                r18 = r0
                r15 = r4
                r16 = r12
                r12 = 0
                r13 = 0
                r17 = 0
            L_0x010e:
                int r0 = r10.length
                if (r12 >= r0) goto L_0x0235
                r0 = r10[r12]
                java.lang.String r4 = "dashmpd"
                boolean r0 = r0.startsWith(r4)
                java.lang.String r4 = "="
                if (r0 == 0) goto L_0x0139
                r0 = r10[r12]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                if (r4 != r9) goto L_0x0135
                java.lang.String[] r4 = r1.result     // Catch:{ Exception -> 0x0131 }
                r0 = r0[r6]     // Catch:{ Exception -> 0x0131 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x0131 }
                r4[r8] = r0     // Catch:{ Exception -> 0x0131 }
                goto L_0x0135
            L_0x0131:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0135:
                r17 = 1
                goto L_0x022d
            L_0x0139:
                r0 = r10[r12]
                java.lang.String r8 = "url_encoded_fmt_stream_map"
                boolean r0 = r0.startsWith(r8)
                if (r0 == 0) goto L_0x01c0
                r0 = r10[r12]
                java.lang.String[] r0 = r0.split(r4)
                int r8 = r0.length
                if (r8 != r9) goto L_0x022d
                r0 = r0[r6]     // Catch:{ Exception -> 0x01bb }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01bb }
                java.lang.String r8 = "[&,]"
                java.lang.String[] r0 = r0.split(r8)     // Catch:{ Exception -> 0x01bb }
                r8 = 0
                r20 = 0
                r21 = 0
            L_0x015e:
                int r9 = r0.length     // Catch:{ Exception -> 0x01bb }
                if (r8 >= r9) goto L_0x022d
                r9 = r0[r8]     // Catch:{ Exception -> 0x01bb }
                java.lang.String[] r9 = r9.split(r4)     // Catch:{ Exception -> 0x01bb }
                r19 = 0
                r6 = r9[r19]     // Catch:{ Exception -> 0x01bb }
                r22 = r0
                java.lang.String r0 = "type"
                boolean r0 = r6.startsWith(r0)     // Catch:{ Exception -> 0x01bb }
                if (r0 == 0) goto L_0x0188
                r6 = 1
                r0 = r9[r6]     // Catch:{ Exception -> 0x01bb }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01bb }
                java.lang.String r6 = "video/mp4"
                boolean r0 = r0.contains(r6)     // Catch:{ Exception -> 0x01bb }
                if (r0 == 0) goto L_0x01ad
                r20 = 1
                goto L_0x01ad
            L_0x0188:
                r6 = 0
                r0 = r9[r6]     // Catch:{ Exception -> 0x01bb }
                java.lang.String r6 = "url"
                boolean r0 = r0.startsWith(r6)     // Catch:{ Exception -> 0x01bb }
                if (r0 == 0) goto L_0x019e
                r6 = 1
                r0 = r9[r6]     // Catch:{ Exception -> 0x01bb }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01bb }
                r21 = r0
                goto L_0x01ad
            L_0x019e:
                r6 = 0
                r0 = r9[r6]     // Catch:{ Exception -> 0x01bb }
                java.lang.String r6 = "itag"
                boolean r0 = r0.startsWith(r6)     // Catch:{ Exception -> 0x01bb }
                if (r0 == 0) goto L_0x01ad
                r20 = 0
                r21 = 0
            L_0x01ad:
                if (r20 == 0) goto L_0x01b5
                if (r21 == 0) goto L_0x01b5
                r18 = r21
                goto L_0x022d
            L_0x01b5:
                int r8 = r8 + 1
                r0 = r22
                r6 = 1
                goto L_0x015e
            L_0x01bb:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x022d
            L_0x01c0:
                r0 = r10[r12]
                java.lang.String r6 = "use_cipher_signature"
                boolean r0 = r0.startsWith(r6)
                if (r0 == 0) goto L_0x01e7
                r0 = r10[r12]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r6 = 2
                if (r4 != r6) goto L_0x022d
                r4 = 1
                r0 = r0[r4]
                java.lang.String r0 = r0.toLowerCase()
                java.lang.String r4 = "true"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x022d
                r16 = 1
                goto L_0x022d
            L_0x01e7:
                r0 = r10[r12]
                java.lang.String r6 = "hlsvp"
                boolean r0 = r0.startsWith(r6)
                if (r0 == 0) goto L_0x0209
                r0 = r10[r12]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r6 = 2
                if (r4 != r6) goto L_0x022d
                r4 = 1
                r0 = r0[r4]     // Catch:{ Exception -> 0x0204 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x0204 }
                r15 = r0
                goto L_0x022d
            L_0x0204:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x022d
            L_0x0209:
                r0 = r10[r12]
                java.lang.String r6 = "livestream"
                boolean r0 = r0.startsWith(r6)
                if (r0 == 0) goto L_0x022d
                r0 = r10[r12]
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r6 = 2
                if (r4 != r6) goto L_0x022d
                r4 = 1
                r0 = r0[r4]
                java.lang.String r0 = r0.toLowerCase()
                java.lang.String r4 = "1"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x022d
                r13 = 1
            L_0x022d:
                int r12 = r12 + 1
                r4 = 0
                r6 = 1
                r8 = 0
                r9 = 2
                goto L_0x010e
            L_0x0235:
                r12 = r16
                r0 = r18
                goto L_0x023e
            L_0x023a:
                r13 = 0
                r15 = 0
                r17 = 0
            L_0x023e:
                if (r13 == 0) goto L_0x0258
                if (r15 == 0) goto L_0x0256
                if (r12 != 0) goto L_0x0256
                boolean r4 = r15.contains(r14)
                if (r4 == 0) goto L_0x024b
                goto L_0x0256
            L_0x024b:
                java.lang.String[] r4 = r1.result
                r6 = 0
                r4[r6] = r15
                java.lang.String r6 = "hls"
                r8 = 1
                r4[r8] = r6
                goto L_0x0258
            L_0x0256:
                r2 = 0
                return r2
            L_0x0258:
                if (r17 == 0) goto L_0x025b
                goto L_0x0264
            L_0x025b:
                int r11 = r11 + 1
                r4 = 0
                r6 = 1
                r8 = 0
                r9 = 2
                r10 = 3
                goto L_0x00d5
            L_0x0264:
                r6 = r12
                java.lang.String[] r2 = r1.result
                r4 = 0
                r5 = r2[r4]
                if (r5 != 0) goto L_0x0275
                if (r0 == 0) goto L_0x0275
                r2[r4] = r0
                java.lang.String r0 = "other"
                r5 = 1
                r2[r5] = r0
            L_0x0275:
                java.lang.String[] r0 = r1.result
                r2 = r0[r4]
                if (r2 == 0) goto L_0x0453
                if (r6 != 0) goto L_0x0285
                r0 = r0[r4]
                boolean r0 = r0.contains(r14)
                if (r0 == 0) goto L_0x0453
            L_0x0285:
                if (r3 == 0) goto L_0x0453
                java.lang.String[] r0 = r1.result
                r0 = r0[r4]
                int r0 = r0.indexOf(r14)
                java.lang.String[] r2 = r1.result
                r2 = r2[r4]
                r5 = 47
                int r6 = r0 + 10
                int r2 = r2.indexOf(r5, r6)
                r5 = -1
                if (r0 == r5) goto L_0x044f
                if (r2 != r5) goto L_0x02a8
                java.lang.String[] r2 = r1.result
                r2 = r2[r4]
                int r2 = r2.length()
            L_0x02a8:
                java.lang.String[] r5 = r1.result
                r5 = r5[r4]
                java.lang.String r0 = r5.substring(r0, r2)
                r1.sig = r0
                java.util.regex.Pattern r0 = org.telegram.ui.Components.WebPlayerView.jsPattern
                java.util.regex.Matcher r0 = r0.matcher(r3)
                boolean r2 = r0.find()
                if (r2 == 0) goto L_0x02da
                org.json.JSONTokener r2 = new org.json.JSONTokener     // Catch:{ Exception -> 0x02d6 }
                r3 = 1
                java.lang.String r0 = r0.group(r3)     // Catch:{ Exception -> 0x02d6 }
                r2.<init>(r0)     // Catch:{ Exception -> 0x02d6 }
                java.lang.Object r0 = r2.nextValue()     // Catch:{ Exception -> 0x02d6 }
                boolean r2 = r0 instanceof java.lang.String     // Catch:{ Exception -> 0x02d6 }
                if (r2 == 0) goto L_0x02da
                java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x02d6 }
                r4 = r0
                goto L_0x02db
            L_0x02d6:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x02da:
                r4 = 0
            L_0x02db:
                if (r4 == 0) goto L_0x044f
                java.util.regex.Pattern r0 = org.telegram.ui.Components.WebPlayerView.playerIdPattern
                java.util.regex.Matcher r0 = r0.matcher(r4)
                boolean r2 = r0.find()
                if (r2 == 0) goto L_0x0305
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r3 = 1
                java.lang.String r5 = r0.group(r3)
                r2.append(r5)
                r3 = 2
                java.lang.String r0 = r0.group(r3)
                r2.append(r0)
                java.lang.String r0 = r2.toString()
                goto L_0x0306
            L_0x0305:
                r0 = 0
            L_0x0306:
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
                java.lang.String r3 = "youtubecode"
                r5 = 0
                android.content.SharedPreferences r2 = r2.getSharedPreferences(r3, r5)
                java.lang.String r3 = "n"
                if (r0 == 0) goto L_0x032d
                r6 = 0
                java.lang.String r7 = r2.getString(r0, r6)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r0)
                r8.append(r3)
                java.lang.String r8 = r8.toString()
                java.lang.String r8 = r2.getString(r8, r6)
                goto L_0x032f
            L_0x032d:
                r7 = 0
                r8 = 0
            L_0x032f:
                if (r7 != 0) goto L_0x03d5
                java.lang.String r6 = "//"
                boolean r6 = r4.startsWith(r6)
                if (r6 == 0) goto L_0x034b
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r9 = "https:"
                r6.append(r9)
                r6.append(r4)
                java.lang.String r4 = r6.toString()
                goto L_0x0364
            L_0x034b:
                java.lang.String r6 = "/"
                boolean r6 = r4.startsWith(r6)
                if (r6 == 0) goto L_0x0364
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r9 = "https://www.youtube.com"
                r6.append(r9)
                r6.append(r4)
                java.lang.String r4 = r6.toString()
            L_0x0364:
                org.telegram.ui.Components.WebPlayerView r6 = org.telegram.ui.Components.WebPlayerView.this
                java.lang.String r4 = r6.downloadUrlContent(r1, r4)
                boolean r6 = r23.isCancelled()
                if (r6 == 0) goto L_0x0372
                r9 = 0
                return r9
            L_0x0372:
                r9 = 0
                if (r4 == 0) goto L_0x03d6
                java.util.regex.Pattern r6 = org.telegram.ui.Components.WebPlayerView.sigPattern
                java.util.regex.Matcher r6 = r6.matcher(r4)
                boolean r10 = r6.find()
                if (r10 == 0) goto L_0x0389
                r10 = 1
                java.lang.String r8 = r6.group(r10)
                goto L_0x039c
            L_0x0389:
                r10 = 1
                java.util.regex.Pattern r6 = org.telegram.ui.Components.WebPlayerView.sigPattern2
                java.util.regex.Matcher r6 = r6.matcher(r4)
                boolean r11 = r6.find()
                if (r11 == 0) goto L_0x039c
                java.lang.String r8 = r6.group(r10)
            L_0x039c:
                if (r8 == 0) goto L_0x03d7
                org.telegram.ui.Components.WebPlayerView$JSExtractor r6 = new org.telegram.ui.Components.WebPlayerView$JSExtractor     // Catch:{ Exception -> 0x03d0 }
                org.telegram.ui.Components.WebPlayerView r11 = org.telegram.ui.Components.WebPlayerView.this     // Catch:{ Exception -> 0x03d0 }
                r6.<init>(r4)     // Catch:{ Exception -> 0x03d0 }
                java.lang.String r7 = r6.extractFunction(r8)     // Catch:{ Exception -> 0x03d0 }
                boolean r4 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x03d0 }
                if (r4 != 0) goto L_0x03d7
                if (r0 == 0) goto L_0x03d7
                android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ Exception -> 0x03d0 }
                android.content.SharedPreferences$Editor r2 = r2.putString(r0, r7)     // Catch:{ Exception -> 0x03d0 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d0 }
                r4.<init>()     // Catch:{ Exception -> 0x03d0 }
                r4.append(r0)     // Catch:{ Exception -> 0x03d0 }
                r4.append(r3)     // Catch:{ Exception -> 0x03d0 }
                java.lang.String r0 = r4.toString()     // Catch:{ Exception -> 0x03d0 }
                android.content.SharedPreferences$Editor r0 = r2.putString(r0, r8)     // Catch:{ Exception -> 0x03d0 }
                r0.commit()     // Catch:{ Exception -> 0x03d0 }
                goto L_0x03d7
            L_0x03d0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x03d7
            L_0x03d5:
                r9 = 0
            L_0x03d6:
                r10 = 1
            L_0x03d7:
                boolean r0 = android.text.TextUtils.isEmpty(r7)
                if (r0 != 0) goto L_0x0451
                int r0 = android.os.Build.VERSION.SDK_INT
                r2 = 21
                java.lang.String r3 = "('"
                if (r0 < r2) goto L_0x0407
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
                goto L_0x043c
            L_0x0407:
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
            L_0x043c:
                org.telegram.ui.Components.-$$Lambda$WebPlayerView$YoutubeVideoTask$GMLQkdVjUFyM84BTj7n250BCLASSNAMEg r2 = new org.telegram.ui.Components.-$$Lambda$WebPlayerView$YoutubeVideoTask$GMLQkdVjUFyM84BTj7n250BCLASSNAMEg     // Catch:{ Exception -> 0x044a }
                r2.<init>(r0)     // Catch:{ Exception -> 0x044a }
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x044a }
                java.util.concurrent.CountDownLatch r0 = r1.countDownLatch     // Catch:{ Exception -> 0x044a }
                r0.await()     // Catch:{ Exception -> 0x044a }
                goto L_0x0455
            L_0x044a:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0451
            L_0x044f:
                r9 = 0
                r10 = 1
            L_0x0451:
                r5 = 1
                goto L_0x0455
            L_0x0453:
                r9 = 0
                r5 = r6
            L_0x0455:
                boolean r0 = r23.isCancelled()
                if (r0 != 0) goto L_0x0461
                if (r5 == 0) goto L_0x045e
                goto L_0x0461
            L_0x045e:
                java.lang.String[] r4 = r1.result
                goto L_0x0462
            L_0x0461:
                r4 = r9
            L_0x0462:
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
        private boolean canRetry = true;
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
        private boolean canRetry = true;
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
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchClipVideoTask(String str, String str2) {
            this.videoId = str2;
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
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchStreamVideoTask(String str, String str2) {
            this.videoId = str2;
            this.currentUrl = str;
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
        private boolean canRetry = true;
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
            this.textPaint = new TextPaint(1);
            this.textPaint.setColor(-1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.progressPaint = new Paint(1);
            this.progressPaint.setColor(-15095832);
            this.progressInnerPaint = new Paint();
            this.progressInnerPaint.setColor(-6975081);
            this.progressBufferedPaint = new Paint(1);
            this.progressBufferedPaint.setColor(-1);
            this.imageReceiver = new ImageReceiver(this);
        }

        public void setDuration(int i) {
            if (this.duration != i && i >= 0 && !WebPlayerView.this.isStream) {
                this.duration = i;
                this.durationLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.duration), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.durationLayout.getLineCount() > 0) {
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
                        this.currentAnimation = new AnimatorSet();
                        this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f})});
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
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f})});
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
                this.currentProgressX -= this.lastProgressX - x2;
                this.lastProgressX = x2;
                int i6 = this.currentProgressX;
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
                r12 = r1
                r13 = r2
                r11 = r3
                r10 = 0
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
                r12 = r1
                r13 = r2
                r11 = r3
                r10 = r4
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
                float r2 = (float) r10
                float r3 = (float) r11
                float r4 = (float) r12
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r1 = r1 + r11
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
                int r1 = r12 - r10
                float r1 = (float) r1
                int r2 = r0.progress
                float r2 = (float) r2
                int r3 = r0.duration
                float r3 = (float) r3
                float r2 = r2 / r3
                float r1 = r1 * r2
                int r1 = (int) r1
                int r1 = r1 + r10
            L_0x0174:
                r14 = r1
                int r1 = r0.bufferedPosition
                if (r1 == 0) goto L_0x01a5
                int r2 = r0.duration
                if (r2 == 0) goto L_0x01a5
                float r3 = (float) r10
                float r4 = (float) r11
                int r12 = r12 - r10
                float r5 = (float) r12
                float r1 = (float) r1
                float r2 = (float) r2
                float r1 = r1 / r2
                float r5 = r5 * r1
                float r5 = r5 + r3
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r1 = r1 + r11
                float r6 = (float) r1
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.inFullscreen
                if (r1 == 0) goto L_0x0198
                android.graphics.Paint r1 = r0.progressBufferedPaint
                goto L_0x019a
            L_0x0198:
                android.graphics.Paint r1 = r0.progressInnerPaint
            L_0x019a:
                r12 = r1
                r1 = r16
                r2 = r3
                r3 = r4
                r4 = r5
                r5 = r6
                r6 = r12
                r1.drawRect(r2, r3, r4, r5, r6)
            L_0x01a5:
                float r2 = (float) r10
                float r3 = (float) r11
                float r10 = (float) r14
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r11 = r11 + r1
                float r5 = (float) r11
                android.graphics.Paint r6 = r0.progressPaint
                r1 = r16
                r4 = r10
                r1.drawRect(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.WebPlayerView r1 = org.telegram.ui.Components.WebPlayerView.this
                boolean r1 = r1.isInline
                if (r1 != 0) goto L_0x01d0
                float r1 = (float) r13
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
        int i = lastContainerId;
        lastContainerId = i + 1;
        this.fragment_container_id = i;
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
                                 org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.WebPlayerView.2.1.onPreDraw():boolean, dex: classes.dex
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
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
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
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
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
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
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
                                 call: org.telegram.ui.Components.-$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ.<init>(org.telegram.ui.Components.WebPlayerView$2$1):void type: CONSTRUCTOR in method: org.telegram.ui.Components.WebPlayerView.2.1.onPreDraw():boolean, dex: classes.dex
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
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(context) {
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
        addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        this.webView = new WebView(context);
        this.webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() {
            public final void jsCallFinished(String str) {
                WebPlayerView.this.lambda$new$0$WebPlayerView(str);
            }
        }), this.interfaceName);
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        this.textureView = new TextureView(context);
        this.textureView.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (this.allowInlineAnimation && this.textureViewContainer != null) {
            this.textureImageView = new ImageView(context);
            this.textureImageView.setBackgroundColor(-65536);
            this.textureImageView.setPivotX(0.0f);
            this.textureImageView.setPivotY(0.0f);
            this.textureImageView.setVisibility(4);
            this.textureViewContainer.addView(this.textureImageView);
        }
        this.videoPlayer = new VideoPlayer();
        this.videoPlayer.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        this.controlsView = new ControlsView(context);
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 != null) {
            viewGroup2.addView(this.controlsView);
        } else {
            addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.progressView = new RadialProgressView(context);
        this.progressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        this.fullscreenButton = new ImageView(context);
        this.fullscreenButton.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$1$WebPlayerView(view);
            }
        });
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$2$WebPlayerView(view);
            }
        });
        if (z) {
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ImageView.ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$3$WebPlayerView(view);
                }
            });
        }
        if (z2) {
            this.shareButton = new ImageView(context);
            this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
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
                this.currentBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                this.changedTextureView.getBitmap(this.currentBitmap);
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

    public void onError(Exception exc) {
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
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
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
            if (((AudioManager) ApplicationLoader.applicationContext.getSystemService("audio")).requestAudioFocus(this, 3, 1) == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void onAudioFocusChange(int i) {
        if (i == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
            this.audioFocus = 0;
        } else if (i == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (i == -3) {
            this.audioFocus = 1;
        } else if (i == -2) {
            this.audioFocus = 0;
            if (this.videoPlayer.isPlaying()) {
                this.resumeAudioOnFocusGain = true;
                this.videoPlayer.pause();
                updatePlayButton();
            }
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
            this.isLoading = false;
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
                this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
                this.changedTextureView.setVisibility(4);
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

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x024d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a4 A[SYNTHETIC, Splitter:B:40:0x00a4] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c3 A[SYNTHETIC, Splitter:B:52:0x00c3] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e2 A[SYNTHETIC, Splitter:B:64:0x00e2] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0101 A[SYNTHETIC, Splitter:B:76:0x0101] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadVideo(java.lang.String r26, org.telegram.tgnet.TLRPC.Photo r27, java.lang.Object r28, java.lang.String r29, boolean r30) {
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
            if (r2 == 0) goto L_0x0127
            java.lang.String r8 = ".mp4"
            boolean r8 = r2.endsWith(r8)
            if (r8 == 0) goto L_0x001d
            r0 = r2
            r8 = r7
            goto L_0x0129
        L_0x001d:
            if (r29 == 0) goto L_0x0066
            android.net.Uri r8 = android.net.Uri.parse(r29)     // Catch:{ Exception -> 0x0062 }
            java.lang.String r9 = "t"
            java.lang.String r9 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0062 }
            if (r9 != 0) goto L_0x0031
            java.lang.String r9 = "time_continue"
            java.lang.String r9 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0062 }
        L_0x0031:
            if (r9 == 0) goto L_0x0066
            boolean r8 = r9.contains(r0)     // Catch:{ Exception -> 0x0062 }
            if (r8 == 0) goto L_0x0057
            java.lang.String[] r0 = r9.split(r0)     // Catch:{ Exception -> 0x0062 }
            r8 = r0[r5]     // Catch:{ Exception -> 0x0062 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ Exception -> 0x0062 }
            int r8 = r8.intValue()     // Catch:{ Exception -> 0x0062 }
            int r8 = r8 * 60
            r0 = r0[r6]     // Catch:{ Exception -> 0x0062 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0062 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0062 }
            int r8 = r8 + r0
            r1.seekToTime = r8     // Catch:{ Exception -> 0x0062 }
            goto L_0x0066
        L_0x0057:
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ Exception -> 0x0062 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0062 }
            r1.seekToTime = r0     // Catch:{ Exception -> 0x0062 }
            goto L_0x0066
        L_0x0062:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x007e }
        L_0x0066:
            java.util.regex.Pattern r0 = youtubeIdRegex     // Catch:{ Exception -> 0x007e }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x007e }
            boolean r8 = r0.find()     // Catch:{ Exception -> 0x007e }
            if (r8 == 0) goto L_0x0077
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x007e }
            goto L_0x0078
        L_0x0077:
            r0 = r7
        L_0x0078:
            if (r0 == 0) goto L_0x007b
            goto L_0x007c
        L_0x007b:
            r0 = r7
        L_0x007c:
            r8 = r0
            goto L_0x0083
        L_0x007e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r8 = r7
        L_0x0083:
            if (r8 != 0) goto L_0x00a1
            java.util.regex.Pattern r0 = vimeoIdRegex     // Catch:{ Exception -> 0x009d }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x009d }
            boolean r9 = r0.find()     // Catch:{ Exception -> 0x009d }
            if (r9 == 0) goto L_0x0096
            java.lang.String r0 = r0.group(r4)     // Catch:{ Exception -> 0x009d }
            goto L_0x0097
        L_0x0096:
            r0 = r7
        L_0x0097:
            if (r0 == 0) goto L_0x009a
            goto L_0x009b
        L_0x009a:
            r0 = r7
        L_0x009b:
            r9 = r0
            goto L_0x00a2
        L_0x009d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00a1:
            r9 = r7
        L_0x00a2:
            if (r9 != 0) goto L_0x00c0
            java.util.regex.Pattern r0 = aparatIdRegex     // Catch:{ Exception -> 0x00bc }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00bc }
            boolean r10 = r0.find()     // Catch:{ Exception -> 0x00bc }
            if (r10 == 0) goto L_0x00b5
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x00bc }
            goto L_0x00b6
        L_0x00b5:
            r0 = r7
        L_0x00b6:
            if (r0 == 0) goto L_0x00b9
            goto L_0x00ba
        L_0x00b9:
            r0 = r7
        L_0x00ba:
            r10 = r0
            goto L_0x00c1
        L_0x00bc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c0:
            r10 = r7
        L_0x00c1:
            if (r10 != 0) goto L_0x00df
            java.util.regex.Pattern r0 = twitchClipIdRegex     // Catch:{ Exception -> 0x00db }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00db }
            boolean r11 = r0.find()     // Catch:{ Exception -> 0x00db }
            if (r11 == 0) goto L_0x00d4
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x00db }
            goto L_0x00d5
        L_0x00d4:
            r0 = r7
        L_0x00d5:
            if (r0 == 0) goto L_0x00d8
            goto L_0x00d9
        L_0x00d8:
            r0 = r7
        L_0x00d9:
            r11 = r0
            goto L_0x00e0
        L_0x00db:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00df:
            r11 = r7
        L_0x00e0:
            if (r11 != 0) goto L_0x00fe
            java.util.regex.Pattern r0 = twitchStreamIdRegex     // Catch:{ Exception -> 0x00fa }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x00fa }
            boolean r12 = r0.find()     // Catch:{ Exception -> 0x00fa }
            if (r12 == 0) goto L_0x00f3
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x00fa }
            goto L_0x00f4
        L_0x00f3:
            r0 = r7
        L_0x00f4:
            if (r0 == 0) goto L_0x00f7
            goto L_0x00f8
        L_0x00f7:
            r0 = r7
        L_0x00f8:
            r12 = r0
            goto L_0x00ff
        L_0x00fa:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00fe:
            r12 = r7
        L_0x00ff:
            if (r12 != 0) goto L_0x0121
            java.util.regex.Pattern r0 = coubIdRegex     // Catch:{ Exception -> 0x011d }
            java.util.regex.Matcher r0 = r0.matcher(r2)     // Catch:{ Exception -> 0x011d }
            boolean r13 = r0.find()     // Catch:{ Exception -> 0x011d }
            if (r13 == 0) goto L_0x0112
            java.lang.String r0 = r0.group(r6)     // Catch:{ Exception -> 0x011d }
            goto L_0x0113
        L_0x0112:
            r0 = r7
        L_0x0113:
            if (r0 == 0) goto L_0x0116
            goto L_0x0117
        L_0x0116:
            r0 = r7
        L_0x0117:
            r13 = r12
            r12 = r11
            r11 = r10
            r10 = r0
            r0 = r7
            goto L_0x012e
        L_0x011d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0121:
            r0 = r7
            r13 = r12
            r12 = r11
            r11 = r10
            r10 = r0
            goto L_0x012e
        L_0x0127:
            r0 = r7
            r8 = r0
        L_0x0129:
            r9 = r8
            r10 = r9
            r11 = r10
            r12 = r11
            r13 = r12
        L_0x012e:
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
            if (r14 == 0) goto L_0x014c
            r14.cancel(r6)
            r1.currentTask = r7
        L_0x014c:
            r25.updateFullscreenButton()
            r25.updateShareButton()
            r25.updateInlineButton()
            r25.updatePlayButton()
            if (r3 == 0) goto L_0x0182
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r3.sizes
            r15 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15, r6)
            if (r14 == 0) goto L_0x0184
            org.telegram.ui.Components.WebPlayerView$ControlsView r15 = r1.controlsView
            org.telegram.messenger.ImageReceiver r16 = r15.imageReceiver
            r17 = 0
            r18 = 0
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForPhoto(r14, r3)
            r21 = 0
            r22 = 0
            r24 = 1
            java.lang.String r20 = "80_80_b"
            r23 = r28
            r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24)
            r1.drawImage = r6
            goto L_0x0184
        L_0x0182:
            r1.drawImage = r5
        L_0x0184:
            android.animation.AnimatorSet r3 = r1.progressAnimation
            if (r3 == 0) goto L_0x018d
            r3.cancel()
            r1.progressAnimation = r7
        L_0x018d:
            r1.isLoading = r6
            org.telegram.ui.Components.WebPlayerView$ControlsView r3 = r1.controlsView
            r3.setProgress(r5)
            if (r8 == 0) goto L_0x0199
            r1.currentYoutubeId = r8
            r8 = r7
        L_0x0199:
            if (r0 == 0) goto L_0x01b4
            r1.initied = r6
            r1.playVideoUrl = r0
            java.lang.String r2 = "other"
            r1.playVideoType = r2
            boolean r2 = r1.isAutoplay
            if (r2 == 0) goto L_0x01aa
            r25.preparePlayer()
        L_0x01aa:
            r1.showProgress(r5, r5)
            org.telegram.ui.Components.WebPlayerView$ControlsView r2 = r1.controlsView
            r2.show(r6, r6)
            goto L_0x024b
        L_0x01b4:
            r3 = 2
            if (r8 == 0) goto L_0x01cd
            org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask
            r2.<init>(r8)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            goto L_0x0243
        L_0x01cd:
            if (r9 == 0) goto L_0x01e4
            org.telegram.ui.Components.WebPlayerView$VimeoVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$VimeoVideoTask
            r2.<init>(r9)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            goto L_0x0243
        L_0x01e4:
            if (r10 == 0) goto L_0x01fd
            org.telegram.ui.Components.WebPlayerView$CoubVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$CoubVideoTask
            r2.<init>(r10)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            r1.isStream = r6
            goto L_0x0243
        L_0x01fd:
            if (r11 == 0) goto L_0x0214
            org.telegram.ui.Components.WebPlayerView$AparatVideoTask r2 = new org.telegram.ui.Components.WebPlayerView$AparatVideoTask
            r2.<init>(r11)
            java.util.concurrent.Executor r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r2.executeOnExecutor(r14, r4)
            r1.currentTask = r2
            goto L_0x0243
        L_0x0214:
            if (r12 == 0) goto L_0x022b
            org.telegram.ui.Components.WebPlayerView$TwitchClipVideoTask r14 = new org.telegram.ui.Components.WebPlayerView$TwitchClipVideoTask
            r14.<init>(r2, r12)
            java.util.concurrent.Executor r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r14.executeOnExecutor(r2, r4)
            r1.currentTask = r14
            goto L_0x0243
        L_0x022b:
            if (r13 == 0) goto L_0x0243
            org.telegram.ui.Components.WebPlayerView$TwitchStreamVideoTask r14 = new org.telegram.ui.Components.WebPlayerView$TwitchStreamVideoTask
            r14.<init>(r2, r13)
            java.util.concurrent.Executor r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            java.lang.Void[] r4 = new java.lang.Void[r4]
            r4[r5] = r7
            r4[r6] = r7
            r4[r3] = r7
            r14.executeOnExecutor(r2, r4)
            r1.currentTask = r14
            r1.isStream = r6
        L_0x0243:
            org.telegram.ui.Components.WebPlayerView$ControlsView r2 = r1.controlsView
            r2.show(r5, r5)
            r1.showProgress(r6, r5)
        L_0x024b:
            if (r8 != 0) goto L_0x0262
            if (r9 != 0) goto L_0x0262
            if (r10 != 0) goto L_0x0262
            if (r11 != 0) goto L_0x0262
            if (r0 != 0) goto L_0x0262
            if (r12 != 0) goto L_0x0262
            if (r13 == 0) goto L_0x025a
            goto L_0x0262
        L_0x025a:
            org.telegram.ui.Components.WebPlayerView$ControlsView r0 = r1.controlsView
            r2 = 8
            r0.setVisibility(r2)
            return r5
        L_0x0262:
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
            this.progressAnimation = new AnimatorSet();
            AnimatorSet animatorSet2 = this.progressAnimation;
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
