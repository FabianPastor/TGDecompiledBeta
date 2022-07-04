package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.stream.Stream;
import j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;

public class ImageLoader {
    public static final String AUTOPLAY_FILTER = "g";
    private static volatile ImageLoader Instance = null;
    /* access modifiers changed from: private */
    public static ThreadLocal<byte[]> bytesLocal = new ThreadLocal<>();
    /* access modifiers changed from: private */
    public static ThreadLocal<byte[]> bytesThumbLocal = new ThreadLocal<>();
    /* access modifiers changed from: private */
    public static byte[] header = new byte[12];
    /* access modifiers changed from: private */
    public static byte[] headerThumb = new byte[12];
    /* access modifiers changed from: private */
    public LinkedList<ArtworkLoadTask> artworkTasks = new LinkedList<>();
    /* access modifiers changed from: private */
    public HashMap<String, Integer> bitmapUseCounts = new HashMap<>();
    /* access modifiers changed from: private */
    public DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    /* access modifiers changed from: private */
    public DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
    ArrayList<AnimatedFileDrawable> cachedAnimatedFileDrawables = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean canForce8888;
    private int currentArtworkTasksCount;
    private int currentHttpFileLoadTasksCount;
    private int currentHttpTasksCount;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, long[]> fileProgresses = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, Integer> forceLoadingImages = new HashMap<>();
    private LinkedList<HttpFileTask> httpFileLoadTasks;
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys;
    /* access modifiers changed from: private */
    public LinkedList<HttpImageTask> httpTasks = new LinkedList<>();
    /* access modifiers changed from: private */
    public String ignoreRemoval;
    /* access modifiers changed from: private */
    public DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
    /* access modifiers changed from: private */
    public HashMap<String, CacheImage> imageLoadingByKeys = new HashMap<>();
    /* access modifiers changed from: private */
    public SparseArray<CacheImage> imageLoadingByTag = new SparseArray<>();
    /* access modifiers changed from: private */
    public HashMap<String, CacheImage> imageLoadingByUrl = new HashMap<>();
    /* access modifiers changed from: private */
    public volatile long lastCacheOutTime;
    private int lastImageNum;
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> lottieMemCache;
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> memCache;
    private HashMap<String, String> replacedBitmaps = new HashMap<>();
    private HashMap<String, Runnable> retryHttpsTasks;
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> smallImagesMemCache;
    private File telegramPath;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, WebFile> testWebFile;
    /* access modifiers changed from: private */
    public HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap<>();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap<>();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray<>();
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> wallpaperMemCache;

    public void moveToFront(String key) {
        if (key != null) {
            if (this.memCache.get(key) != null) {
                this.memCache.moveToFront(key);
            }
            if (this.smallImagesMemCache.get(key) != null) {
                this.smallImagesMemCache.moveToFront(key);
            }
        }
    }

    public void putThumbsToCache(ArrayList<MessageThumb> updateMessageThumbs) {
        for (int i = 0; i < updateMessageThumbs.size(); i++) {
            putImageToCache(updateMessageThumbs.get(i).drawable, updateMessageThumbs.get(i).key, true);
        }
    }

    private static class ThumbGenerateInfo {
        /* access modifiers changed from: private */
        public boolean big;
        /* access modifiers changed from: private */
        public String filter;
        /* access modifiers changed from: private */
        public ArrayList<ImageReceiver> imageReceiverArray;
        /* access modifiers changed from: private */
        public ArrayList<Integer> imageReceiverGuidsArray;
        /* access modifiers changed from: private */
        public TLRPC.Document parentDocument;

        private ThumbGenerateInfo() {
            this.imageReceiverArray = new ArrayList<>();
            this.imageReceiverGuidsArray = new ArrayList<>();
        }
    }

    private class HttpFileTask extends AsyncTask<Void, Void, Boolean> {
        /* access modifiers changed from: private */
        public boolean canRetry = true;
        /* access modifiers changed from: private */
        public int currentAccount;
        /* access modifiers changed from: private */
        public String ext;
        private RandomAccessFile fileOutputStream = null;
        private int fileSize;
        private long lastProgressTime;
        /* access modifiers changed from: private */
        public File tempFile;
        /* access modifiers changed from: private */
        public String url;

        public HttpFileTask(String url2, File tempFile2, String ext2, int currentAccount2) {
            this.url = url2;
            this.tempFile = tempFile2;
            this.ext = ext2;
            this.currentAccount = currentAccount2;
        }

        private void reportProgress(long uploadedSize, long totalSize) {
            long currentTime = SystemClock.elapsedRealtime();
            if (uploadedSize != totalSize) {
                long j = this.lastProgressTime;
                if (j != 0 && j >= currentTime - 100) {
                    return;
                }
            }
            this.lastProgressTime = currentTime;
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpFileTask$$ExternalSyntheticLambda1(this, uploadedSize, totalSize));
        }

        /* renamed from: lambda$reportProgress$1$org-telegram-messenger-ImageLoader$HttpFileTask  reason: not valid java name */
        public /* synthetic */ void m1904x99a68a63(long uploadedSize, long totalSize) {
            ImageLoader.this.fileProgresses.put(this.url, new long[]{uploadedSize, totalSize});
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpFileTask$$ExternalSyntheticLambda0(this, uploadedSize, totalSize));
        }

        /* renamed from: lambda$reportProgress$0$org-telegram-messenger-ImageLoader$HttpFileTask  reason: not valid java name */
        public /* synthetic */ void m1903x9a1cvar_(long uploadedSize, long totalSize) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.url, Long.valueOf(uploadedSize), Long.valueOf(totalSize));
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voids) {
            List values;
            String length;
            int code;
            InputStream httpConnectionStream = null;
            boolean done = false;
            URLConnection httpConnection = null;
            try {
                httpConnection = new URL(this.url).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                if (httpConnection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                    httpURLConnection.setInstanceFollowRedirects(true);
                    int status = httpURLConnection.getResponseCode();
                    if (status == 302 || status == 301 || status == 303) {
                        String newUrl = httpURLConnection.getHeaderField("Location");
                        String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                        httpConnection = new URL(newUrl).openConnection();
                        httpConnection.setRequestProperty("Cookie", cookies);
                        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    }
                }
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                this.fileOutputStream = new RandomAccessFile(this.tempFile, "rws");
            } catch (Throwable e) {
                if (e instanceof SocketTimeoutException) {
                    if (ApplicationLoader.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                } else if (e instanceof UnknownHostException) {
                    this.canRetry = false;
                } else if (e instanceof SocketException) {
                    if (e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                        this.canRetry = false;
                    }
                } else if (e instanceof FileNotFoundException) {
                    this.canRetry = false;
                }
                FileLog.e(e);
            }
            if (this.canRetry) {
                try {
                    if (!(!(httpConnection instanceof HttpURLConnection) || (code = ((HttpURLConnection) httpConnection).getResponseCode()) == 200 || code == 202 || code == 304)) {
                        this.canRetry = false;
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (httpConnection != null) {
                    try {
                        Map<String, List<String>> headerFields = httpConnection.getHeaderFields();
                        if (!(headerFields == null || (values = headerFields.get("content-Length")) == null || values.isEmpty() || (length = (String) values.get(0)) == null)) {
                            this.fileSize = Utilities.parseInt((CharSequence) length).intValue();
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[32768];
                        int totalLoaded = 0;
                        while (true) {
                            if (!isCancelled()) {
                                int read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    this.fileOutputStream.write(data, 0, read);
                                    totalLoaded += read;
                                    int i = this.fileSize;
                                    if (i > 0) {
                                        reportProgress((long) totalLoaded, (long) i);
                                    }
                                } else if (read == -1) {
                                    done = true;
                                    int i2 = this.fileSize;
                                    if (i2 != 0) {
                                        reportProgress((long) i2, (long) i2);
                                    }
                                }
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    } catch (Throwable e5) {
                        FileLog.e(e5);
                    }
                }
                try {
                    RandomAccessFile randomAccessFile = this.fileOutputStream;
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e6) {
                    FileLog.e(e6);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e7) {
                        FileLog.e(e7);
                    }
                }
            }
            return Boolean.valueOf(done);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean result) {
            ImageLoader.this.runHttpFileLoadTasks(this, result.booleanValue() ? 2 : 1);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.runHttpFileLoadTasks(this, 2);
        }
    }

    private class ArtworkLoadTask extends AsyncTask<Void, Void, String> {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean canRetry = true;
        private HttpURLConnection httpConnection;
        private boolean small;

        public ArtworkLoadTask(CacheImage cacheImage2) {
            boolean z = true;
            this.cacheImage = cacheImage2;
            this.small = Uri.parse(cacheImage2.imageLocation.path).getQueryParameter("s") == null ? false : z;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            int code;
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.cacheImage.imageLocation.path.replace("athumb://", "https://")).openConnection();
                this.httpConnection = httpURLConnection;
                httpURLConnection.setConnectTimeout(5000);
                this.httpConnection.setReadTimeout(5000);
                this.httpConnection.connect();
                HttpURLConnection httpURLConnection2 = this.httpConnection;
                if (!(httpURLConnection2 == null || (code = httpURLConnection2.getResponseCode()) == 200 || code == 202 || code == 304)) {
                    this.canRetry = false;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e, false);
            } catch (Throwable e2) {
                try {
                    if (e2 instanceof SocketTimeoutException) {
                        if (ApplicationLoader.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (e2 instanceof UnknownHostException) {
                        this.canRetry = false;
                    } else if (e2 instanceof SocketException) {
                        if (e2.getMessage() != null && e2.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                    } else if (e2 instanceof FileNotFoundException) {
                        this.canRetry = false;
                    }
                    FileLog.e(e2, false);
                    HttpURLConnection httpURLConnection3 = this.httpConnection;
                    if (httpURLConnection3 != null) {
                        httpURLConnection3.disconnect();
                    }
                } catch (Throwable e3) {
                    FileLog.e(e3);
                }
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
                if (outbuf == null) {
                    return null;
                }
                outbuf.close();
                return null;
            }
            httpConnectionStream = this.httpConnection.getInputStream();
            outbuf = new ByteArrayOutputStream();
            byte[] data = new byte[32768];
            while (true) {
                if (!isCancelled()) {
                    int read = httpConnectionStream.read(data);
                    if (read <= 0) {
                        break;
                    }
                    outbuf.write(data, 0, read);
                } else {
                    break;
                }
            }
            this.canRetry = false;
            JSONArray array = new JSONObject(new String(outbuf.toByteArray())).getJSONArray("results");
            if (array.length() > 0) {
                String artworkUrl100 = array.getJSONObject(0).getString("artworkUrl100");
                if (this.small) {
                    try {
                        HttpURLConnection httpURLConnection4 = this.httpConnection;
                        if (httpURLConnection4 != null) {
                            httpURLConnection4.disconnect();
                        }
                    } catch (Throwable th) {
                    }
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e4) {
                            FileLog.e(e4);
                        }
                    }
                    try {
                        outbuf.close();
                    } catch (Exception e5) {
                    }
                    return artworkUrl100;
                }
                String replace = artworkUrl100.replace("100x100", "600x600");
                try {
                    HttpURLConnection httpURLConnection5 = this.httpConnection;
                    if (httpURLConnection5 != null) {
                        httpURLConnection5.disconnect();
                    }
                } catch (Throwable th2) {
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e6) {
                        FileLog.e(e6);
                    }
                }
                try {
                    outbuf.close();
                } catch (Exception e7) {
                }
                return replace;
            }
            try {
                HttpURLConnection httpURLConnection6 = this.httpConnection;
                if (httpURLConnection6 != null) {
                    httpURLConnection6.disconnect();
                }
            } catch (Throwable th3) {
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e8) {
                    FileLog.e(e8);
                }
            }
            try {
                outbuf.close();
                return null;
            } catch (Exception e9) {
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda2(this, result));
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$onPostExecute$0$org-telegram-messenger-ImageLoader$ArtworkLoadTask  reason: not valid java name */
        public /* synthetic */ void m1896x647e920b(String result) {
            this.cacheImage.httpTask = new HttpImageTask(this.cacheImage, 0, result);
            ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
            ImageLoader.this.runHttpTasks(false);
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-messenger-ImageLoader$ArtworkLoadTask  reason: not valid java name */
        public /* synthetic */ void m1897x92572c6a() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* renamed from: lambda$onCancelled$2$org-telegram-messenger-ImageLoader$ArtworkLoadTask  reason: not valid java name */
        public /* synthetic */ void m1895x1e3958ad() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda0(this));
        }
    }

    private class HttpImageTask extends AsyncTask<Void, Void, Boolean> {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean canRetry = true;
        private RandomAccessFile fileOutputStream;
        private HttpURLConnection httpConnection;
        /* access modifiers changed from: private */
        public long imageSize;
        private long lastProgressTime;
        private String overrideUrl;

        public HttpImageTask(CacheImage cacheImage2, long size) {
            this.cacheImage = cacheImage2;
            this.imageSize = size;
        }

        public HttpImageTask(CacheImage cacheImage2, int size, String url) {
            this.cacheImage = cacheImage2;
            this.imageSize = (long) size;
            this.overrideUrl = url;
        }

        private void reportProgress(long uploadedSize, long totalSize) {
            long currentTime = SystemClock.elapsedRealtime();
            if (uploadedSize != totalSize) {
                long j = this.lastProgressTime;
                if (j != 0 && j >= currentTime - 100) {
                    return;
                }
            }
            this.lastProgressTime = currentTime;
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda5(this, uploadedSize, totalSize));
        }

        /* renamed from: lambda$reportProgress$1$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1912xb5eb4c7e(long uploadedSize, long totalSize) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, new long[]{uploadedSize, totalSize});
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda4(this, uploadedSize, totalSize));
        }

        /* renamed from: lambda$reportProgress$0$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1911xCLASSNAMEa65f(long uploadedSize, long totalSize) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.cacheImage.url, Long.valueOf(uploadedSize), Long.valueOf(totalSize));
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection;
            List values;
            String length;
            int code;
            int provider;
            WebFile webFile;
            InputStream httpConnectionStream = null;
            boolean done = false;
            if (!isCancelled()) {
                try {
                    String location = this.cacheImage.imageLocation.path;
                    if ((location.startsWith("https://static-maps") || location.startsWith("https://maps.googleapis")) && (((provider = MessagesController.getInstance(this.cacheImage.currentAccount).mapProvider) == 3 || provider == 4) && (webFile = (WebFile) ImageLoader.this.testWebFile.get(location)) != null)) {
                        TLRPC.TL_upload_getWebFile req = new TLRPC.TL_upload_getWebFile();
                        req.location = webFile.location;
                        req.offset = 0;
                        req.limit = 0;
                        ConnectionsManager.getInstance(this.cacheImage.currentAccount).sendRequest(req, ImageLoader$HttpImageTask$$ExternalSyntheticLambda8.INSTANCE);
                    }
                    String str = this.overrideUrl;
                    if (str == null) {
                        str = location;
                    }
                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str).openConnection();
                    this.httpConnection = httpURLConnection2;
                    httpURLConnection2.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    this.httpConnection.setConnectTimeout(5000);
                    this.httpConnection.setReadTimeout(5000);
                    this.httpConnection.setInstanceFollowRedirects(true);
                    if (!isCancelled()) {
                        this.httpConnection.connect();
                        httpConnectionStream = this.httpConnection.getInputStream();
                        this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
                    }
                } catch (Throwable e) {
                    boolean sentLogs = true;
                    if (e instanceof SocketTimeoutException) {
                        if (ApplicationLoader.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                        sentLogs = false;
                    } else if (e instanceof UnknownHostException) {
                        this.canRetry = false;
                        sentLogs = false;
                    } else if (e instanceof SocketException) {
                        if (e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                        sentLogs = false;
                    } else if (e instanceof FileNotFoundException) {
                        this.canRetry = false;
                        sentLogs = false;
                    } else if (e instanceof InterruptedIOException) {
                        sentLogs = false;
                    }
                    FileLog.e(e, sentLogs);
                }
            }
            if (!isCancelled()) {
                try {
                    HttpURLConnection httpURLConnection3 = this.httpConnection;
                    if (!(httpURLConnection3 == null || (code = httpURLConnection3.getResponseCode()) == 200 || code == 202 || code == 304)) {
                        this.canRetry = false;
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (this.imageSize == 0 && (httpURLConnection = this.httpConnection) != null) {
                    try {
                        Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
                        if (!(headerFields == null || (values = headerFields.get("content-Length")) == null || values.isEmpty() || (length = (String) values.get(0)) == null)) {
                            this.imageSize = (long) Utilities.parseInt((CharSequence) length).intValue();
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[8192];
                        int totalLoaded = 0;
                        while (true) {
                            if (!isCancelled()) {
                                int read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    totalLoaded += read;
                                    this.fileOutputStream.write(data, 0, read);
                                    long j = this.imageSize;
                                    if (j != 0) {
                                        reportProgress((long) totalLoaded, j);
                                    }
                                } else if (read == -1) {
                                    done = true;
                                    long j2 = this.imageSize;
                                    if (j2 != 0) {
                                        reportProgress(j2, j2);
                                    }
                                }
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    } catch (Throwable e5) {
                        FileLog.e(e5);
                    }
                }
            }
            try {
                RandomAccessFile randomAccessFile = this.fileOutputStream;
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e6) {
                FileLog.e(e6);
            }
            try {
                HttpURLConnection httpURLConnection4 = this.httpConnection;
                if (httpURLConnection4 != null) {
                    httpURLConnection4.disconnect();
                }
            } catch (Throwable th) {
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e7) {
                    FileLog.e(e7);
                }
            }
            if (done && this.cacheImage.tempFilePath != null && !this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath)) {
                CacheImage cacheImage2 = this.cacheImage;
                cacheImage2.finalFilePath = cacheImage2.tempFilePath;
            }
            return Boolean.valueOf(done);
        }

        static /* synthetic */ void lambda$doInBackground$2(TLObject response, TLRPC.TL_error error) {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean result) {
            if (result.booleanValue() || !this.canRetry) {
                ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda7(this, result));
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda3(this));
        }

        /* renamed from: lambda$onPostExecute$4$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1909x6321fc0(Boolean result) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda6(this, result));
        }

        /* renamed from: lambda$onPostExecute$3$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1908x148879a1(Boolean result) {
            if (result.booleanValue()) {
                NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoaded, this.cacheImage.url, this.cacheImage.finalFilePath);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 2);
        }

        /* renamed from: lambda$onPostExecute$5$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1910xf7dbc5df() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* renamed from: lambda$onCancelled$6$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1905xa7d226e2() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda0(this));
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda2(this));
        }

        /* renamed from: lambda$onCancelled$8$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1907x8b257320() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$onCancelled$7$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m1906x997bcd01() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 1);
        }
    }

    private class ThumbGenerateTask implements Runnable {
        private ThumbGenerateInfo info;
        private int mediaType;
        private File originalPath;

        public ThumbGenerateTask(int type, File path, ThumbGenerateInfo i) {
            this.mediaType = type;
            this.originalPath = path;
            this.info = i;
        }

        private void removeTask() {
            ThumbGenerateInfo thumbGenerateInfo = this.info;
            if (thumbGenerateInfo != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda0(this, FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)));
            }
        }

        /* renamed from: lambda$removeTask$0$org-telegram-messenger-ImageLoader$ThumbGenerateTask  reason: not valid java name */
        public /* synthetic */ void m1913x21var_f(String name) {
            ImageLoader.this.thumbGenerateTasks.remove(name);
        }

        public void run() {
            Bitmap originalBitmap;
            Bitmap scaledBitmap;
            try {
                if (this.info == null) {
                    removeTask();
                    return;
                }
                String key = "q_" + this.info.parentDocument.dc_id + "_" + this.info.parentDocument.id;
                File thumbFile = new File(FileLoader.getDirectory(4), key + ".jpg");
                if (!thumbFile.exists()) {
                    if (this.originalPath.exists()) {
                        int size = this.info.big ? Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) : Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                        Bitmap originalBitmap2 = null;
                        int i = this.mediaType;
                        if (i == 0) {
                            originalBitmap2 = ImageLoader.loadBitmap(this.originalPath.toString(), (Uri) null, (float) size, (float) size, false);
                        } else {
                            int i2 = 2;
                            if (i == 2) {
                                String file = this.originalPath.toString();
                                if (!this.info.big) {
                                    i2 = 1;
                                }
                                originalBitmap2 = SendMessagesHelper.createVideoThumbnail(file, i2);
                            } else if (i == 3) {
                                String path = this.originalPath.toString().toLowerCase();
                                if (path.endsWith("mp4")) {
                                    String file2 = this.originalPath.toString();
                                    if (!this.info.big) {
                                        i2 = 1;
                                    }
                                    originalBitmap2 = SendMessagesHelper.createVideoThumbnail(file2, i2);
                                } else if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                                    originalBitmap2 = ImageLoader.loadBitmap(path, (Uri) null, (float) size, (float) size, false);
                                }
                            }
                        }
                        if (originalBitmap2 == null) {
                            removeTask();
                            return;
                        }
                        int w = originalBitmap2.getWidth();
                        int h = originalBitmap2.getHeight();
                        if (w != 0) {
                            if (h != 0) {
                                float scaleFactor = Math.min(((float) w) / ((float) size), ((float) h) / ((float) size));
                                if (scaleFactor <= 1.0f || (scaledBitmap = Bitmaps.createScaledBitmap(originalBitmap2, (int) (((float) w) / scaleFactor), (int) (((float) h) / scaleFactor), true)) == originalBitmap2) {
                                    originalBitmap = originalBitmap2;
                                } else {
                                    originalBitmap2.recycle();
                                    originalBitmap = scaledBitmap;
                                }
                                FileOutputStream stream = new FileOutputStream(thumbFile);
                                originalBitmap.compress(Bitmap.CompressFormat.JPEG, this.info.big ? 83 : 60, stream);
                                stream.close();
                                AndroidUtilities.runOnUIThread(new ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda1(this, key, new ArrayList<>(this.info.imageReceiverArray), new BitmapDrawable(originalBitmap), new ArrayList<>(this.info.imageReceiverGuidsArray)));
                                return;
                            }
                        }
                        removeTask();
                        return;
                    }
                }
                removeTask();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable e2) {
                FileLog.e(e2);
                removeTask();
            }
        }

        /* renamed from: lambda$run$1$org-telegram-messenger-ImageLoader$ThumbGenerateTask  reason: not valid java name */
        public /* synthetic */ void m1914x4b36var_(String key, ArrayList finalImageReceiverArray, BitmapDrawable bitmapDrawable, ArrayList finalImageReceiverGuidsArray) {
            removeTask();
            String kf = key;
            if (this.info.filter != null) {
                kf = kf + "@" + this.info.filter;
            }
            for (int a = 0; a < finalImageReceiverArray.size(); a++) {
                ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(bitmapDrawable, kf, 0, false, ((Integer) finalImageReceiverGuidsArray.get(a)).intValue());
            }
            ImageLoader.this.memCache.put(kf, bitmapDrawable);
        }
    }

    public static String decompressGzip(File file) {
        BufferedReader bufferedReader;
        StringBuilder outStr = new StringBuilder();
        if (file == null) {
            return "";
        }
        try {
            GZIPInputStream gis = new GZIPInputStream(new FileInputStream(file));
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    String line = readLine;
                    if (readLine != null) {
                        outStr.append(line);
                    } else {
                        String sb = outStr.toString();
                        bufferedReader.close();
                        gis.close();
                        return sb;
                    }
                }
            } catch (Throwable th) {
                gis.close();
                throw th;
            }
        } catch (Exception e) {
            return "";
        } catch (Throwable th2) {
        }
        throw th;
    }

    private class CacheOutTask implements Runnable {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage image) {
            this.cacheImage = image;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v12, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: org.telegram.messenger.secretmedia.EncryptedFileInputStream} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v54, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v57, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: float} */
        /* JADX WARNING: type inference failed for: r7v88 */
        /* JADX WARNING: type inference failed for: r7v89 */
        /* JADX WARNING: type inference failed for: r7v93, types: [java.lang.CharSequence, java.lang.String] */
        /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
            java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
            	at java.util.ArrayList.get(ArrayList.java:435)
            	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
            */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:837:0x0e15 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:843:0x0e28  */
        /* JADX WARNING: Removed duplicated region for block: B:844:0x0e2e  */
        public void run() {
            /*
                r42 = this;
                r1 = r42
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0e98 }
                r1.runningThread = r0     // Catch:{ all -> 0x0e98 }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0e98 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0e98 }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0e98 }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0e98 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r0 = r0.photoSize
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
                if (r0 == 0) goto L_0x003d
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r0 = r0.photoSize
                org.telegram.tgnet.TLRPC$TL_photoStrippedSize r0 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r0
                byte[] r3 = r0.bytes
                java.lang.String r4 = "b"
                android.graphics.Bitmap r3 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r3, r4)
                if (r3 == 0) goto L_0x0037
                android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
                r2.<init>(r3)
                goto L_0x0038
            L_0x0037:
                r2 = 0
            L_0x0038:
                r1.onPostExecute(r2)
                goto L_0x0e97
            L_0x003d:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r3 = 5
                if (r0 != r3) goto L_0x0061
                r2 = 0
                org.telegram.ui.Components.ThemePreviewDrawable r0 = new org.telegram.ui.Components.ThemePreviewDrawable     // Catch:{ all -> 0x0058 }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0058 }
                java.io.File r3 = r3.finalFilePath     // Catch:{ all -> 0x0058 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0058 }
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x0058 }
                org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ all -> 0x0058 }
                org.telegram.messenger.DocumentObject$ThemeDocument r4 = (org.telegram.messenger.DocumentObject.ThemeDocument) r4     // Catch:{ all -> 0x0058 }
                r0.<init>(r3, r4)     // Catch:{ all -> 0x0058 }
                r2 = r0
                goto L_0x005c
            L_0x0058:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x005c:
                r1.onPostExecute(r2)
                goto L_0x0e97
            L_0x0061:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r4 = 3
                r5 = 4
                r6 = 2
                r7 = 0
                r8 = 1
                if (r0 == r4) goto L_0x0e33
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                if (r0 != r5) goto L_0x0074
                goto L_0x0e33
            L_0x0074:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r9 = 1119092736(0x42b40000, float:90.0)
                if (r0 != r8) goto L_0x0269
                r0 = 1126865306(0x432a999a, float:170.6)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r10 = 512(0x200, float:7.175E-43)
                int r2 = java.lang.Math.min(r10, r2)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r10, r0)
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 1
                r15 = 0
                r16 = 0
                r17 = 0
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                if (r5 == 0) goto L_0x01a4
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                java.lang.String r3 = "_"
                java.lang.String[] r3 = r5.split(r3)
                int r5 = r3.length
                if (r5 < r6) goto L_0x011d
                r5 = r3[r7]
                float r5 = java.lang.Float.parseFloat(r5)
                r19 = r3[r8]
                float r19 = java.lang.Float.parseFloat(r19)
                float r20 = org.telegram.messenger.AndroidUtilities.density
                float r7 = r5 * r20
                int r7 = (int) r7
                int r2 = java.lang.Math.min(r10, r7)
                float r7 = org.telegram.messenger.AndroidUtilities.density
                float r7 = r7 * r19
                int r7 = (int) r7
                int r0 = java.lang.Math.min(r10, r7)
                int r7 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
                if (r7 > 0) goto L_0x00ea
                int r7 = (r19 > r9 ? 1 : (r19 == r9 ? 0 : -1))
                if (r7 > 0) goto L_0x00ea
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                java.lang.String r9 = "nolimit"
                boolean r7 = r7.contains(r9)
                if (r7 != 0) goto L_0x00ea
                r7 = 160(0xa0, float:2.24E-43)
                int r2 = java.lang.Math.min(r2, r7)
                int r0 = java.lang.Math.min(r0, r7)
                r12 = 1
            L_0x00ea:
                int r7 = r3.length
                if (r7 < r4) goto L_0x00fa
                java.lang.String r7 = "pcache"
                r9 = r3[r6]
                boolean r7 = r7.equals(r9)
                if (r7 == 0) goto L_0x00fa
                r7 = 1
                r11 = r7
                goto L_0x0110
            L_0x00fa:
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                java.lang.String r9 = "nolimit"
                boolean r7 = r7.contains(r9)
                if (r7 != 0) goto L_0x010e
                int r7 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                if (r7 == r6) goto L_0x010e
                r7 = 1
                goto L_0x010f
            L_0x010e:
                r7 = 0
            L_0x010f:
                r11 = r7
            L_0x0110:
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                java.lang.String r9 = "lastframe"
                boolean r7 = r7.contains(r9)
                if (r7 == 0) goto L_0x011d
                r13 = 1
            L_0x011d:
                int r5 = r3.length
                if (r5 < r4) goto L_0x0145
                java.lang.String r5 = "nr"
                r7 = r3[r6]
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x012c
                r14 = 2
                goto L_0x0145
            L_0x012c:
                java.lang.String r5 = "nrs"
                r7 = r3[r6]
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x0138
                r14 = 3
                goto L_0x0145
            L_0x0138:
                java.lang.String r5 = "dice"
                r7 = r3[r6]
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x0145
                r16 = r3[r4]
                r14 = 2
            L_0x0145:
                int r4 = r3.length
                r5 = 5
                if (r4 < r5) goto L_0x019f
                java.lang.String r4 = "c1"
                r5 = 4
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x015b
                r17 = 12
                r3 = r2
                r4 = r16
                r2 = r0
                goto L_0x01a8
            L_0x015b:
                java.lang.String r4 = "c2"
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x016c
                r17 = 3
                r3 = r2
                r4 = r16
                r2 = r0
                goto L_0x01a8
            L_0x016c:
                java.lang.String r4 = "c3"
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x017d
                r17 = 4
                r3 = r2
                r4 = r16
                r2 = r0
                goto L_0x01a8
            L_0x017d:
                java.lang.String r4 = "c4"
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x018e
                r17 = 5
                r3 = r2
                r4 = r16
                r2 = r0
                goto L_0x01a8
            L_0x018e:
                java.lang.String r4 = "c5"
                r5 = r3[r5]
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x019f
                r17 = 6
                r3 = r2
                r4 = r16
                r2 = r0
                goto L_0x01a8
            L_0x019f:
                r3 = r2
                r4 = r16
                r2 = r0
                goto L_0x01a8
            L_0x01a4:
                r3 = r2
                r4 = r16
                r2 = r0
            L_0x01a8:
                if (r4 == 0) goto L_0x01c0
                java.lang.String r0 = "🎰"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x01b9
                org.telegram.ui.Components.SlotsDrawable r0 = new org.telegram.ui.Components.SlotsDrawable
                r0.<init>(r4, r3, r2)
                goto L_0x024e
            L_0x01b9:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                r0.<init>(r4, r3, r2)
                goto L_0x024e
            L_0x01c0:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r5 = r0.finalFilePath
                r7 = 0
                r9 = 0
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0202 }
                org.telegram.messenger.ImageLoader$CacheImage r10 = r1.cacheImage     // Catch:{ Exception -> 0x0202 }
                java.io.File r10 = r10.finalFilePath     // Catch:{ Exception -> 0x0202 }
                java.lang.String r6 = "r"
                r0.<init>(r10, r6)     // Catch:{ Exception -> 0x0202 }
                r7 = r0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x0202 }
                int r0 = r0.type     // Catch:{ Exception -> 0x0202 }
                if (r0 != r8) goto L_0x01dd
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x0202 }
                goto L_0x01e1
            L_0x01dd:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x0202 }
            L_0x01e1:
                r6 = 2
                r10 = 0
                r7.readFully(r0, r10, r6)     // Catch:{ Exception -> 0x0202 }
                byte r6 = r0[r10]     // Catch:{ Exception -> 0x0202 }
                r10 = 31
                if (r6 != r10) goto L_0x01f3
                byte r6 = r0[r8]     // Catch:{ Exception -> 0x0202 }
                r8 = -117(0xffffffffffffff8b, float:NaN)
                if (r6 != r8) goto L_0x01f3
                r9 = 1
            L_0x01f3:
                r7.close()     // Catch:{ Exception -> 0x01f8 }
            L_0x01f7:
                goto L_0x020d
            L_0x01f8:
                r0 = move-exception
                r6 = r0
                r0 = r6
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x01f7
            L_0x01ff:
                r0 = move-exception
                r6 = r0
                goto L_0x025c
            L_0x0202:
                r0 = move-exception
                r6 = 0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r6)     // Catch:{ all -> 0x01ff }
                if (r7 == 0) goto L_0x020d
                r7.close()     // Catch:{ Exception -> 0x01f8 }
                goto L_0x01f7
            L_0x020d:
                if (r13 == 0) goto L_0x0211
                r0 = 0
                r11 = r0
            L_0x0211:
                if (r9 == 0) goto L_0x0235
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                org.telegram.messenger.ImageLoader$CacheImage r8 = r1.cacheImage
                java.io.File r8 = r8.finalFilePath
                java.lang.String r23 = org.telegram.messenger.ImageLoader.decompressGzip(r8)
                r28 = 0
                r21 = r0
                r22 = r6
                r24 = r3
                r25 = r2
                r26 = r11
                r27 = r12
                r29 = r17
                r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
                goto L_0x024e
            L_0x0235:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                r27 = 0
                r21 = r0
                r22 = r6
                r23 = r3
                r24 = r2
                r25 = r11
                r26 = r12
                r28 = r17
                r21.<init>(r22, r23, r24, r25, r26, r27, r28)
            L_0x024e:
                if (r13 == 0) goto L_0x0254
                r1.loadLastFrame(r0, r2, r3)
                goto L_0x025a
            L_0x0254:
                r0.setAutoRepeat(r14)
                r1.onPostExecute(r0)
            L_0x025a:
                goto L_0x0e97
            L_0x025c:
                if (r7 == 0) goto L_0x0268
                r7.close()     // Catch:{ Exception -> 0x0262 }
                goto L_0x0268
            L_0x0262:
                r0 = move-exception
                r8 = r0
                r0 = r8
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0268:
                throw r6
            L_0x0269:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r3 = 2
                if (r0 != r3) goto L_0x03dc
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                if (r0 == 0) goto L_0x027d
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                long r3 = r0.videoSeekTo
                goto L_0x027f
            L_0x027d:
                r3 = 0
            L_0x027f:
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                if (r5 == 0) goto L_0x02b6
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                java.lang.String r6 = "_"
                java.lang.String[] r5 = r5.split(r6)
                int r6 = r5.length
                r7 = 2
                if (r6 < r7) goto L_0x02b6
                r6 = 0
                r7 = r5[r6]
                float r6 = java.lang.Float.parseFloat(r7)
                r7 = r5[r8]
                float r7 = java.lang.Float.parseFloat(r7)
                int r10 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r10 > 0) goto L_0x02b6
                int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r9 > 0) goto L_0x02b6
                org.telegram.messenger.ImageLoader$CacheImage r9 = r1.cacheImage
                java.lang.String r9 = r9.filter
                java.lang.String r10 = "nolimit"
                boolean r9 = r9.contains(r10)
                if (r9 != 0) goto L_0x02b6
                r0 = 1
            L_0x02b6:
                org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.lang.String r6 = r6.filter
                boolean r5 = r5.isAnimatedAvatar(r6)
                if (r5 != 0) goto L_0x02ce
                java.lang.String r5 = "g"
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.lang.String r6 = r6.filter
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x0345
            L_0x02ce:
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation
                org.telegram.tgnet.TLRPC$Document r5 = r5.document
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted
                if (r5 != 0) goto L_0x0345
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation
                org.telegram.tgnet.TLRPC$Document r5 = r5.document
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.Document
                if (r5 == 0) goto L_0x02e9
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation
                org.telegram.tgnet.TLRPC$Document r5 = r5.document
                goto L_0x02ea
            L_0x02e9:
                r5 = 0
            L_0x02ea:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                if (r5 == 0) goto L_0x02f1
                long r6 = r6.size
                goto L_0x02f5
            L_0x02f1:
                org.telegram.messenger.ImageLocation r6 = r6.imageLocation
                long r6 = r6.currentSize
            L_0x02f5:
                r24 = r6
                org.telegram.ui.Components.AnimatedFileDrawable r6 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.io.File r7 = r7.finalFilePath
                r23 = 0
                if (r5 != 0) goto L_0x0308
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation
                r27 = r2
                goto L_0x030a
            L_0x0308:
                r27 = 0
            L_0x030a:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.lang.Object r2 = r2.parentObject
                org.telegram.messenger.ImageLoader$CacheImage r9 = r1.cacheImage
                int r9 = r9.currentAccount
                r32 = 0
                r21 = r6
                r22 = r7
                r26 = r5
                r28 = r2
                r29 = r3
                r31 = r9
                r21.<init>(r22, r23, r24, r26, r27, r28, r29, r31, r32)
                r2 = r6
                boolean r6 = org.telegram.messenger.MessageObject.isWebM(r5)
                if (r6 != 0) goto L_0x033f
                boolean r6 = org.telegram.messenger.MessageObject.isVideoSticker(r5)
                if (r6 != 0) goto L_0x033f
                org.telegram.messenger.ImageLoader r6 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                boolean r6 = r6.isAnimatedAvatar(r7)
                if (r6 == 0) goto L_0x033d
                goto L_0x033f
            L_0x033d:
                r7 = 0
                goto L_0x0340
            L_0x033f:
                r7 = 1
            L_0x0340:
                r2.setIsWebmSticker(r7)
                goto L_0x03d1
            L_0x0345:
                r2 = 0
                r5 = 0
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.lang.String r6 = r6.filter
                if (r6 == 0) goto L_0x0372
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.lang.String r6 = r6.filter
                java.lang.String r7 = "_"
                java.lang.String[] r6 = r6.split(r7)
                int r7 = r6.length
                r9 = 2
                if (r7 < r9) goto L_0x0372
                r7 = 0
                r9 = r6[r7]
                float r7 = java.lang.Float.parseFloat(r9)
                r9 = r6[r8]
                float r9 = java.lang.Float.parseFloat(r9)
                float r10 = org.telegram.messenger.AndroidUtilities.density
                float r10 = r10 * r7
                int r2 = (int) r10
                float r10 = org.telegram.messenger.AndroidUtilities.density
                float r10 = r10 * r9
                int r5 = (int) r10
            L_0x0372:
                org.telegram.ui.Components.AnimatedFileDrawable r6 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.io.File r7 = r7.finalFilePath
                java.lang.String r9 = "d"
                org.telegram.messenger.ImageLoader$CacheImage r10 = r1.cacheImage
                java.lang.String r10 = r10.filter
                boolean r23 = r9.equals(r10)
                r24 = 0
                org.telegram.messenger.ImageLoader$CacheImage r9 = r1.cacheImage
                org.telegram.messenger.ImageLocation r9 = r9.imageLocation
                org.telegram.tgnet.TLRPC$Document r9 = r9.document
                r27 = 0
                r28 = 0
                org.telegram.messenger.ImageLoader$CacheImage r10 = r1.cacheImage
                int r10 = r10.currentAccount
                r32 = 0
                r21 = r6
                r22 = r7
                r26 = r9
                r29 = r3
                r31 = r10
                r33 = r2
                r34 = r5
                r21.<init>(r22, r23, r24, r26, r27, r28, r29, r31, r32, r33, r34)
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                org.telegram.messenger.ImageLocation r7 = r7.imageLocation
                org.telegram.tgnet.TLRPC$Document r7 = r7.document
                boolean r7 = org.telegram.messenger.MessageObject.isWebM(r7)
                if (r7 != 0) goto L_0x03cc
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                org.telegram.messenger.ImageLocation r7 = r7.imageLocation
                org.telegram.tgnet.TLRPC$Document r7 = r7.document
                boolean r7 = org.telegram.messenger.MessageObject.isVideoSticker(r7)
                if (r7 != 0) goto L_0x03cc
                org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r9 = r1.cacheImage
                java.lang.String r9 = r9.filter
                boolean r7 = r7.isAnimatedAvatar(r9)
                if (r7 == 0) goto L_0x03ca
                goto L_0x03cc
            L_0x03ca:
                r7 = 0
                goto L_0x03cd
            L_0x03cc:
                r7 = 1
            L_0x03cd:
                r6.setIsWebmSticker(r7)
                r2 = r6
            L_0x03d1:
                r2.setLimitFps(r0)
                java.lang.Thread.interrupted()
                r1.onPostExecute(r2)
                goto L_0x0e97
            L_0x03dc:
                r3 = 0
                r5 = 0
                r6 = 0
                r7 = 0
                r9 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r10 = r0.finalFilePath
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 != 0) goto L_0x0402
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x0400
                if (r10 == 0) goto L_0x0400
                java.lang.String r0 = r10.getAbsolutePath()
                java.lang.String r11 = ".enc"
                boolean r0 = r0.endsWith(r11)
                if (r0 == 0) goto L_0x0400
                goto L_0x0402
            L_0x0400:
                r0 = 0
                goto L_0x0403
            L_0x0402:
                r0 = 1
            L_0x0403:
                r11 = r0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x0436
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                org.telegram.messenger.SecureDocumentKey r0 = r0.secureDocumentKey
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                org.telegram.tgnet.TLRPC$TL_secureFile r12 = r12.secureFile
                if (r12 == 0) goto L_0x042d
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                org.telegram.tgnet.TLRPC$TL_secureFile r12 = r12.secureFile
                byte[] r12 = r12.file_hash
                if (r12 == 0) goto L_0x042d
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                org.telegram.tgnet.TLRPC$TL_secureFile r12 = r12.secureFile
                byte[] r12 = r12.file_hash
                r13 = r12
                r12 = r0
                goto L_0x043a
            L_0x042d:
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                byte[] r12 = r12.fileHash
                r13 = r12
                r12 = r0
                goto L_0x043a
            L_0x0436:
                r0 = 0
                r12 = 0
                r13 = r12
                r12 = r0
            L_0x043a:
                r14 = 1
                r15 = 0
                int r0 = android.os.Build.VERSION.SDK_INT
                r4 = 19
                if (r0 >= r4) goto L_0x04a4
                r4 = 0
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x048d }
                java.lang.String r2 = "r"
                r0.<init>(r10, r2)     // Catch:{ Exception -> 0x048d }
                r4 = r0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x048d }
                int r0 = r0.type     // Catch:{ Exception -> 0x048d }
                if (r0 != r8) goto L_0x0456
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x048d }
                goto L_0x045a
            L_0x0456:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x048d }
            L_0x045a:
                int r2 = r0.length     // Catch:{ Exception -> 0x048d }
                r8 = 0
                r4.readFully(r0, r8, r2)     // Catch:{ Exception -> 0x048d }
                java.lang.String r2 = new java.lang.String     // Catch:{ Exception -> 0x048d }
                r2.<init>(r0)     // Catch:{ Exception -> 0x048d }
                java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x048d }
                java.lang.String r8 = r2.toLowerCase()     // Catch:{ Exception -> 0x048d }
                r2 = r8
                java.lang.String r8 = "riff"
                boolean r8 = r2.startsWith(r8)     // Catch:{ Exception -> 0x048d }
                if (r8 == 0) goto L_0x047e
                java.lang.String r8 = "webp"
                boolean r8 = r2.endsWith(r8)     // Catch:{ Exception -> 0x048d }
                if (r8 == 0) goto L_0x047e
                r15 = 1
            L_0x047e:
                r4.close()     // Catch:{ Exception -> 0x0483 }
            L_0x0482:
                goto L_0x04a4
            L_0x0483:
                r0 = move-exception
                r2 = r0
                r0 = r2
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0482
            L_0x048a:
                r0 = move-exception
                r2 = r0
                goto L_0x0497
            L_0x048d:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x048a }
                if (r4 == 0) goto L_0x04a4
                r4.close()     // Catch:{ Exception -> 0x0483 }
                goto L_0x0482
            L_0x0497:
                if (r4 == 0) goto L_0x04a3
                r4.close()     // Catch:{ Exception -> 0x049d }
                goto L_0x04a3
            L_0x049d:
                r0 = move-exception
                r8 = r0
                r0 = r8
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x04a3:
                throw r2
            L_0x04a4:
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation
                java.lang.String r2 = r2.path
                if (r2 == 0) goto L_0x0508
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation
                java.lang.String r2 = r2.path
                java.lang.String r4 = "thumb://"
                boolean r4 = r2.startsWith(r4)
                if (r4 == 0) goto L_0x04db
                java.lang.String r4 = ":"
                r8 = 8
                int r4 = r2.indexOf(r4, r8)
                if (r4 < 0) goto L_0x04d8
                java.lang.String r8 = r2.substring(r8, r4)
                long r21 = java.lang.Long.parseLong(r8)
                java.lang.Long r3 = java.lang.Long.valueOf(r21)
                r5 = 0
                int r8 = r4 + 1
                java.lang.String r0 = r2.substring(r8)
            L_0x04d8:
                r14 = 0
                r2 = r0
                goto L_0x0509
            L_0x04db:
                java.lang.String r4 = "vthumb://"
                boolean r4 = r2.startsWith(r4)
                if (r4 == 0) goto L_0x04fd
                java.lang.String r4 = ":"
                r8 = 9
                int r4 = r2.indexOf(r4, r8)
                if (r4 < 0) goto L_0x04fa
                java.lang.String r8 = r2.substring(r8, r4)
                long r21 = java.lang.Long.parseLong(r8)
                java.lang.Long r3 = java.lang.Long.valueOf(r21)
                r5 = 1
            L_0x04fa:
                r14 = 0
                r2 = r0
                goto L_0x0509
            L_0x04fd:
                java.lang.String r4 = "http"
                boolean r4 = r2.startsWith(r4)
                if (r4 != 0) goto L_0x0508
                r14 = 0
                r2 = r0
                goto L_0x0509
            L_0x0508:
                r2 = r0
            L_0x0509:
                android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
                r0.<init>()
                r4 = r0
                r8 = 1
                r4.inSampleSize = r8
                int r0 = android.os.Build.VERSION.SDK_INT
                r8 = 21
                if (r0 >= r8) goto L_0x051b
                r8 = 1
                r4.inPurgeable = r8
            L_0x051b:
                r8 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r23 = r0.canForce8888
                r24 = 0
                r25 = 1065353216(0x3var_, float:1.0)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x078e }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x078e }
                if (r0 == 0) goto L_0x070d
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x078e }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x078e }
                r26 = r6
                java.lang.String r6 = "_"
                java.lang.String[] r0 = r0.split(r6)     // Catch:{ all -> 0x06ff }
                int r6 = r0.length     // Catch:{ all -> 0x06ff }
                r27 = r7
                r7 = 2
                if (r6 < r7) goto L_0x0569
                r6 = 0
                r7 = r0[r6]     // Catch:{ all -> 0x055d }
                float r6 = java.lang.Float.parseFloat(r7)     // Catch:{ all -> 0x055d }
                float r7 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x055d }
                float r8 = r6 * r7
                r6 = 1
                r7 = r0[r6]     // Catch:{ all -> 0x055d }
                float r6 = java.lang.Float.parseFloat(r7)     // Catch:{ all -> 0x055d }
                float r7 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x055d }
                float r6 = r6 * r7
                r20 = r6
                goto L_0x0569
            L_0x055d:
                r0 = move-exception
                r31 = r5
                r29 = r9
                r28 = r14
                r7 = r15
                r6 = r26
                goto L_0x079a
            L_0x0569:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x06f3 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x06f3 }
                java.lang.String r7 = "b2"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x06f3 }
                if (r6 == 0) goto L_0x0579
                r6 = 3
                r21 = r6
                goto L_0x0598
            L_0x0579:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x06f3 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x06f3 }
                java.lang.String r7 = "b1"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x06f3 }
                if (r6 == 0) goto L_0x0589
                r6 = 2
                r21 = r6
                goto L_0x0598
            L_0x0589:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x06f3 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x06f3 }
                java.lang.String r7 = "b"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x06f3 }
                if (r6 == 0) goto L_0x0598
                r6 = 1
                r21 = r6
            L_0x0598:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x06f3 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x06f3 }
                java.lang.String r7 = "i"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x06f3 }
                if (r6 == 0) goto L_0x05a7
                r6 = 1
                r22 = r6
            L_0x05a7:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x06f3 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x06f3 }
                java.lang.String r7 = "f"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x06f3 }
                if (r6 == 0) goto L_0x05b6
                r6 = 1
                r23 = r6
            L_0x05b6:
                if (r15 != 0) goto L_0x06e8
                int r6 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
                if (r6 == 0) goto L_0x06e8
                int r6 = (r20 > r24 ? 1 : (r20 == r24 ? 0 : -1))
                if (r6 == 0) goto L_0x06e8
                r6 = 1
                r4.inJustDecodeBounds = r6     // Catch:{ all -> 0x06f3 }
                if (r3 == 0) goto L_0x0617
                if (r2 != 0) goto L_0x0617
                if (r5 == 0) goto L_0x05f7
                android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05eb }
                android.content.ContentResolver r6 = r6.getContentResolver()     // Catch:{ all -> 0x05eb }
                r28 = r14
                r7 = r15
                long r14 = r3.longValue()     // Catch:{ all -> 0x05e2 }
                r29 = r9
                r9 = 1
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r6, r14, r9, r4)     // Catch:{ all -> 0x0610 }
                r30 = r0
                r31 = r5
                goto L_0x069d
            L_0x05e2:
                r0 = move-exception
                r29 = r9
                r31 = r5
                r6 = r26
                goto L_0x079a
            L_0x05eb:
                r0 = move-exception
                r29 = r9
                r28 = r14
                r7 = r15
                r31 = r5
                r6 = r26
                goto L_0x079a
            L_0x05f7:
                r29 = r9
                r28 = r14
                r7 = r15
                android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0610 }
                android.content.ContentResolver r6 = r6.getContentResolver()     // Catch:{ all -> 0x0610 }
                long r14 = r3.longValue()     // Catch:{ all -> 0x0610 }
                r9 = 1
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r6, r14, r9, r4)     // Catch:{ all -> 0x0610 }
                r30 = r0
                r31 = r5
                goto L_0x069d
            L_0x0610:
                r0 = move-exception
                r31 = r5
                r6 = r26
                goto L_0x079a
            L_0x0617:
                r29 = r9
                r28 = r14
                r7 = r15
                if (r12 == 0) goto L_0x0681
                java.io.RandomAccessFile r6 = new java.io.RandomAccessFile     // Catch:{ all -> 0x067a }
                java.lang.String r9 = "r"
                r6.<init>(r10, r9)     // Catch:{ all -> 0x067a }
                long r14 = r6.length()     // Catch:{ all -> 0x067a }
                int r9 = (int) r14     // Catch:{ all -> 0x067a }
                java.lang.ThreadLocal r14 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x067a }
                java.lang.Object r14 = r14.get()     // Catch:{ all -> 0x067a }
                byte[] r14 = (byte[]) r14     // Catch:{ all -> 0x067a }
                if (r14 == 0) goto L_0x063b
                int r15 = r14.length     // Catch:{ all -> 0x0610 }
                if (r15 < r9) goto L_0x063b
                r15 = r14
                goto L_0x063c
            L_0x063b:
                r15 = 0
            L_0x063c:
                if (r15 != 0) goto L_0x064c
                r30 = r0
                byte[] r0 = new byte[r9]     // Catch:{ all -> 0x0610 }
                r15 = r0
                r14 = r0
                java.lang.ThreadLocal r0 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0610 }
                r0.set(r14)     // Catch:{ all -> 0x0610 }
                goto L_0x064e
            L_0x064c:
                r30 = r0
            L_0x064e:
                r0 = r14
                r14 = 0
                r6.readFully(r15, r14, r9)     // Catch:{ all -> 0x067a }
                r6.close()     // Catch:{ all -> 0x067a }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r15, (int) r14, (int) r9, (org.telegram.messenger.SecureDocumentKey) r12)     // Catch:{ all -> 0x067a }
                r31 = r5
                r32 = r6
                long r5 = (long) r9
                byte[] r5 = org.telegram.messenger.Utilities.computeSHA256(r15, r14, r5)     // Catch:{ all -> 0x0783 }
                r6 = 0
                if (r13 == 0) goto L_0x066b
                boolean r14 = java.util.Arrays.equals(r5, r13)     // Catch:{ all -> 0x0783 }
                if (r14 != 0) goto L_0x066c
            L_0x066b:
                r6 = 1
            L_0x066c:
                r33 = r0
                r14 = 0
                byte r0 = r15[r14]     // Catch:{ all -> 0x0783 }
                r0 = r0 & 255(0xff, float:3.57E-43)
                int r9 = r9 - r0
                if (r6 != 0) goto L_0x0679
                android.graphics.BitmapFactory.decodeByteArray(r15, r0, r9, r4)     // Catch:{ all -> 0x0783 }
            L_0x0679:
                goto L_0x069d
            L_0x067a:
                r0 = move-exception
                r31 = r5
                r6 = r26
                goto L_0x079a
            L_0x0681:
                r30 = r0
                r31 = r5
                if (r11 == 0) goto L_0x0691
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0783 }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0783 }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x0783 }
                r0.<init>((java.io.File) r10, (java.io.File) r5)     // Catch:{ all -> 0x0783 }
                goto L_0x0696
            L_0x0691:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0783 }
                r0.<init>(r10)     // Catch:{ all -> 0x0783 }
            L_0x0696:
                r5 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r5, r4)     // Catch:{ all -> 0x0783 }
                r0.close()     // Catch:{ all -> 0x0783 }
            L_0x069d:
                int r0 = r4.outWidth     // Catch:{ all -> 0x0783 }
                float r0 = (float) r0     // Catch:{ all -> 0x0783 }
                int r5 = r4.outHeight     // Catch:{ all -> 0x0783 }
                float r5 = (float) r5     // Catch:{ all -> 0x0783 }
                int r6 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1))
                if (r6 < 0) goto L_0x06b4
                int r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r6 <= 0) goto L_0x06b4
                float r6 = r0 / r8
                float r9 = r5 / r20
                float r6 = java.lang.Math.max(r6, r9)     // Catch:{ all -> 0x0783 }
                goto L_0x06bc
            L_0x06b4:
                float r6 = r0 / r8
                float r9 = r5 / r20
                float r6 = java.lang.Math.min(r6, r9)     // Catch:{ all -> 0x0783 }
            L_0x06bc:
                r9 = 1067030938(0x3var_a, float:1.2)
                int r9 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x06c5
                r6 = 1065353216(0x3var_, float:1.0)
            L_0x06c5:
                r9 = 0
                r4.inJustDecodeBounds = r9     // Catch:{ all -> 0x0783 }
                int r9 = (r6 > r25 ? 1 : (r6 == r25 ? 0 : -1))
                if (r9 <= 0) goto L_0x06e3
                int r9 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                if (r9 > 0) goto L_0x06d4
                int r9 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
                if (r9 <= 0) goto L_0x06e3
            L_0x06d4:
                r9 = 1
            L_0x06d5:
                r14 = 2
                int r9 = r9 * 2
                int r14 = r9 * 2
                float r14 = (float) r14     // Catch:{ all -> 0x0783 }
                int r14 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
                if (r14 < 0) goto L_0x06d5
                r4.inSampleSize = r9     // Catch:{ all -> 0x0783 }
                goto L_0x0787
            L_0x06e3:
                int r9 = (int) r6     // Catch:{ all -> 0x0783 }
                r4.inSampleSize = r9     // Catch:{ all -> 0x0783 }
                goto L_0x0787
            L_0x06e8:
                r30 = r0
                r31 = r5
                r29 = r9
                r28 = r14
                r7 = r15
                goto L_0x0787
            L_0x06f3:
                r0 = move-exception
                r31 = r5
                r29 = r9
                r28 = r14
                r7 = r15
                r6 = r26
                goto L_0x079a
            L_0x06ff:
                r0 = move-exception
                r31 = r5
                r27 = r7
                r29 = r9
                r28 = r14
                r7 = r15
                r6 = r26
                goto L_0x079a
            L_0x070d:
                r31 = r5
                r26 = r6
                r27 = r7
                r29 = r9
                r28 = r14
                r7 = r15
                if (r2 == 0) goto L_0x0787
                r5 = 1
                r4.inJustDecodeBounds = r5     // Catch:{ all -> 0x0783 }
                if (r23 == 0) goto L_0x0722
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0783 }
                goto L_0x0724
            L_0x0722:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0783 }
            L_0x0724:
                r4.inPreferredConfig = r0     // Catch:{ all -> 0x0783 }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0783 }
                r0.<init>(r10)     // Catch:{ all -> 0x0783 }
                r5 = 0
                android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeStream(r0, r5, r4)     // Catch:{ all -> 0x0783 }
                r0.close()     // Catch:{ all -> 0x0781 }
                int r5 = r4.outWidth     // Catch:{ all -> 0x0781 }
                int r9 = r4.outHeight     // Catch:{ all -> 0x0781 }
                r14 = 0
                r4.inJustDecodeBounds = r14     // Catch:{ all -> 0x0781 }
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x0781 }
                int r15 = r15.x     // Catch:{ all -> 0x0781 }
                android.graphics.Point r14 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x0781 }
                int r14 = r14.y     // Catch:{ all -> 0x0781 }
                int r14 = java.lang.Math.min(r15, r14)     // Catch:{ all -> 0x0781 }
                r15 = 66
                int r14 = java.lang.Math.max(r15, r14)     // Catch:{ all -> 0x0781 }
                int r15 = java.lang.Math.min(r9, r5)     // Catch:{ all -> 0x0781 }
                float r15 = (float) r15     // Catch:{ all -> 0x0781 }
                r30 = r0
                float r0 = (float) r14     // Catch:{ all -> 0x0781 }
                float r15 = r15 / r0
                r0 = 1086324736(0x40CLASSNAME, float:6.0)
                float r15 = r15 * r0
                int r0 = (r15 > r25 ? 1 : (r15 == r25 ? 0 : -1))
                if (r0 >= 0) goto L_0x0763
                r15 = 1065353216(0x3var_, float:1.0)
            L_0x0763:
                int r0 = (r15 > r25 ? 1 : (r15 == r25 ? 0 : -1))
                if (r0 <= 0) goto L_0x077b
                r0 = 1
            L_0x0768:
                r16 = 2
                int r0 = r0 * 2
                r26 = r5
                int r5 = r0 * 2
                float r5 = (float) r5     // Catch:{ all -> 0x0781 }
                int r5 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
                if (r5 <= 0) goto L_0x0778
                r4.inSampleSize = r0     // Catch:{ all -> 0x0781 }
                goto L_0x0789
            L_0x0778:
                r5 = r26
                goto L_0x0768
            L_0x077b:
                r26 = r5
                int r0 = (int) r15     // Catch:{ all -> 0x0781 }
                r4.inSampleSize = r0     // Catch:{ all -> 0x0781 }
                goto L_0x0789
            L_0x0781:
                r0 = move-exception
                goto L_0x079a
            L_0x0783:
                r0 = move-exception
                r6 = r26
                goto L_0x079a
            L_0x0787:
                r6 = r26
            L_0x0789:
                r5 = r20
                r9 = r21
                goto L_0x07a7
            L_0x078e:
                r0 = move-exception
                r31 = r5
                r26 = r6
                r27 = r7
                r29 = r9
                r28 = r14
                r7 = r15
            L_0x079a:
                r5 = 1
                boolean r9 = r0 instanceof java.io.FileNotFoundException
                if (r9 == 0) goto L_0x07a0
                r5 = 0
            L_0x07a0:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r5)
                r5 = r20
                r9 = r21
            L_0x07a7:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.type
                r14 = 1
                if (r0 != r14) goto L_0x0a5b
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0a41 }
                long r14 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0a41 }
                long unused = r0.lastCacheOutTime = r14     // Catch:{ all -> 0x0a41 }
                java.lang.Object r14 = r1.sync     // Catch:{ all -> 0x0a41 }
                monitor-enter(r14)     // Catch:{ all -> 0x0a41 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0a2c }
                if (r0 == 0) goto L_0x07cd
                monitor-exit(r14)     // Catch:{ all -> 0x07c0 }
                return
            L_0x07c0:
                r0 = move-exception
                r34 = r2
                r35 = r3
                r26 = r5
                r21 = r6
                r30 = r7
                goto L_0x0a37
            L_0x07cd:
                monitor-exit(r14)     // Catch:{ all -> 0x0a2c }
                if (r7 == 0) goto L_0x0841
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0832 }
                java.lang.String r14 = "r"
                r0.<init>(r10, r14)     // Catch:{ all -> 0x0832 }
                java.nio.channels.FileChannel r34 = r0.getChannel()     // Catch:{ all -> 0x0832 }
                java.nio.channels.FileChannel$MapMode r35 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0832 }
                r36 = 0
                long r38 = r10.length()     // Catch:{ all -> 0x0832 }
                java.nio.MappedByteBuffer r14 = r34.map(r35, r36, r38)     // Catch:{ all -> 0x0832 }
                android.graphics.BitmapFactory$Options r15 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0832 }
                r15.<init>()     // Catch:{ all -> 0x0832 }
                r21 = r6
                r6 = 1
                r15.inJustDecodeBounds = r6     // Catch:{ all -> 0x0823 }
                int r6 = r14.limit()     // Catch:{ all -> 0x0823 }
                r26 = r5
                r30 = r7
                r5 = 0
                r7 = 1
                org.telegram.messenger.Utilities.loadWebpImage(r5, r14, r6, r15, r7)     // Catch:{ all -> 0x0876 }
                int r5 = r15.outWidth     // Catch:{ all -> 0x0876 }
                int r6 = r15.outHeight     // Catch:{ all -> 0x0876 }
                android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0876 }
                android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createBitmap(r5, r6, r7)     // Catch:{ all -> 0x0876 }
                r6 = r5
                int r5 = r14.limit()     // Catch:{ all -> 0x086d }
                boolean r7 = r4.inPurgeable     // Catch:{ all -> 0x086d }
                if (r7 != 0) goto L_0x0813
                r7 = 1
                goto L_0x0814
            L_0x0813:
                r7 = 0
            L_0x0814:
                r18 = r15
                r15 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r6, r14, r5, r15, r7)     // Catch:{ all -> 0x086d }
                r0.close()     // Catch:{ all -> 0x086d }
                r34 = r2
                r35 = r3
                goto L_0x08ef
            L_0x0823:
                r0 = move-exception
                r26 = r5
                r30 = r7
                r34 = r2
                r35 = r3
                r6 = r21
                r7 = r27
                goto L_0x0a4e
            L_0x0832:
                r0 = move-exception
                r26 = r5
                r21 = r6
                r30 = r7
                r34 = r2
                r35 = r3
                r7 = r27
                goto L_0x0a4e
            L_0x0841:
                r26 = r5
                r21 = r6
                r30 = r7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0a22 }
                if (r0 != 0) goto L_0x0881
                if (r12 == 0) goto L_0x084e
                goto L_0x0881
            L_0x084e:
                if (r11 == 0) goto L_0x085a
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0876 }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0876 }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x0876 }
                r0.<init>((java.io.File) r10, (java.io.File) r5)     // Catch:{ all -> 0x0876 }
                goto L_0x085f
            L_0x085a:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0876 }
                r0.<init>(r10)     // Catch:{ all -> 0x0876 }
            L_0x085f:
                r5 = 0
                android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeStream(r0, r5, r4)     // Catch:{ all -> 0x0876 }
                r0.close()     // Catch:{ all -> 0x086d }
                r34 = r2
                r35 = r3
                goto L_0x08ef
            L_0x086d:
                r0 = move-exception
                r34 = r2
                r35 = r3
                r7 = r27
                goto L_0x0a4e
            L_0x0876:
                r0 = move-exception
                r34 = r2
                r35 = r3
                r6 = r21
                r7 = r27
                goto L_0x0a4e
            L_0x0881:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0a22 }
                java.lang.String r5 = "r"
                r0.<init>(r10, r5)     // Catch:{ all -> 0x0a22 }
                long r5 = r0.length()     // Catch:{ all -> 0x0a22 }
                int r6 = (int) r5     // Catch:{ all -> 0x0a22 }
                r5 = 0
                java.lang.ThreadLocal r7 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0a22 }
                java.lang.Object r7 = r7.get()     // Catch:{ all -> 0x0a22 }
                byte[] r7 = (byte[]) r7     // Catch:{ all -> 0x0a22 }
                if (r7 == 0) goto L_0x089f
                int r14 = r7.length     // Catch:{ all -> 0x0876 }
                if (r14 < r6) goto L_0x089f
                r14 = r7
                goto L_0x08a0
            L_0x089f:
                r14 = 0
            L_0x08a0:
                if (r14 != 0) goto L_0x08ad
                byte[] r15 = new byte[r6]     // Catch:{ all -> 0x0876 }
                r14 = r15
                r7 = r15
                java.lang.ThreadLocal r15 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0876 }
                r15.set(r7)     // Catch:{ all -> 0x0876 }
            L_0x08ad:
                r15 = 0
                r0.readFully(r14, r15, r6)     // Catch:{ all -> 0x0a22 }
                r0.close()     // Catch:{ all -> 0x0a22 }
                r18 = 0
                if (r12 == 0) goto L_0x08d6
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r14, (int) r15, (int) r6, (org.telegram.messenger.SecureDocumentKey) r12)     // Catch:{ all -> 0x0a22 }
                r34 = r2
                r35 = r3
                long r2 = (long) r6
                byte[] r2 = org.telegram.messenger.Utilities.computeSHA256(r14, r15, r2)     // Catch:{ all -> 0x0a39 }
                if (r13 == 0) goto L_0x08cc
                boolean r3 = java.util.Arrays.equals(r2, r13)     // Catch:{ all -> 0x0a39 }
                if (r3 != 0) goto L_0x08cf
            L_0x08cc:
                r3 = 1
                r18 = r3
            L_0x08cf:
                r3 = 0
                byte r15 = r14[r3]     // Catch:{ all -> 0x0a39 }
                r5 = r15 & 255(0xff, float:3.57E-43)
                int r6 = r6 - r5
            L_0x08d5:
                goto L_0x08e4
            L_0x08d6:
                r34 = r2
                r35 = r3
                if (r11 == 0) goto L_0x08d5
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0a39 }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x0a39 }
                r3 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r14, (int) r3, (int) r6, (java.io.File) r2)     // Catch:{ all -> 0x0a39 }
            L_0x08e4:
                if (r18 != 0) goto L_0x08ec
                android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeByteArray(r14, r5, r6, r4)     // Catch:{ all -> 0x0a39 }
                r6 = r2
                goto L_0x08ee
            L_0x08ec:
                r6 = r21
            L_0x08ee:
            L_0x08ef:
                if (r6 != 0) goto L_0x0908
                long r2 = r10.length()     // Catch:{ all -> 0x0a1e }
                r14 = 0
                int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
                if (r0 == 0) goto L_0x0901
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a1e }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0a1e }
                if (r0 != 0) goto L_0x0904
            L_0x0901:
                r10.delete()     // Catch:{ all -> 0x0a1e }
            L_0x0904:
                r7 = r27
                goto L_0x0a51
            L_0x0908:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a1e }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0a1e }
                if (r0 == 0) goto L_0x093d
                int r0 = r6.getWidth()     // Catch:{ all -> 0x0a1e }
                float r0 = (float) r0     // Catch:{ all -> 0x0a1e }
                int r2 = r6.getHeight()     // Catch:{ all -> 0x0a1e }
                float r2 = (float) r2     // Catch:{ all -> 0x0a1e }
                boolean r3 = r4.inPurgeable     // Catch:{ all -> 0x0a1e }
                if (r3 != 0) goto L_0x093d
                int r3 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
                if (r3 == 0) goto L_0x093d
                int r3 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                if (r3 == 0) goto L_0x093d
                r3 = 1101004800(0x41a00000, float:20.0)
                float r14 = r8 + r3
                int r3 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
                if (r3 <= 0) goto L_0x093d
                float r3 = r0 / r8
                int r5 = (int) r8     // Catch:{ all -> 0x0a1e }
                float r7 = r2 / r3
                int r7 = (int) r7     // Catch:{ all -> 0x0a1e }
                r14 = 1
                android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r5, r7, r14)     // Catch:{ all -> 0x0a1e }
                if (r6 == r5) goto L_0x093d
                r6.recycle()     // Catch:{ all -> 0x0a1e }
                r6 = r5
            L_0x093d:
                if (r22 == 0) goto L_0x095d
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0a1e }
                if (r0 == 0) goto L_0x0945
                r0 = 0
                goto L_0x0946
            L_0x0945:
                r0 = 1
            L_0x0946:
                int r2 = r6.getWidth()     // Catch:{ all -> 0x0a1e }
                int r3 = r6.getHeight()     // Catch:{ all -> 0x0a1e }
                int r5 = r6.getRowBytes()     // Catch:{ all -> 0x0a1e }
                int r0 = org.telegram.messenger.Utilities.needInvert(r6, r0, r2, r3, r5)     // Catch:{ all -> 0x0a1e }
                if (r0 == 0) goto L_0x095a
                r0 = 1
                goto L_0x095b
            L_0x095a:
                r0 = 0
            L_0x095b:
                r7 = r0
                goto L_0x095f
            L_0x095d:
                r7 = r27
            L_0x095f:
                r2 = 1
                if (r9 != r2) goto L_0x098b
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0988 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0988 }
                if (r0 != r2) goto L_0x0a51
                r37 = 3
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0988 }
                if (r0 == 0) goto L_0x0973
                r38 = 0
                goto L_0x0975
            L_0x0973:
                r38 = 1
            L_0x0975:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0988 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0988 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0988 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0988 }
                goto L_0x0a51
            L_0x0988:
                r0 = move-exception
                goto L_0x0a4e
            L_0x098b:
                r2 = 2
                if (r9 != r2) goto L_0x09b4
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0988 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0988 }
                if (r0 != r2) goto L_0x0a51
                r37 = 1
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0988 }
                if (r0 == 0) goto L_0x099f
                r38 = 0
                goto L_0x09a1
            L_0x099f:
                r38 = 1
            L_0x09a1:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0988 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0988 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0988 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0988 }
                goto L_0x0a51
            L_0x09b4:
                r2 = 3
                if (r9 != r2) goto L_0x0a14
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0988 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0988 }
                if (r0 != r2) goto L_0x0a51
                r37 = 7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0988 }
                if (r0 == 0) goto L_0x09c8
                r38 = 0
                goto L_0x09ca
            L_0x09c8:
                r38 = 1
            L_0x09ca:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0988 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0988 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0988 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0988 }
                r37 = 7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0988 }
                if (r0 == 0) goto L_0x09e4
                r38 = 0
                goto L_0x09e6
            L_0x09e4:
                r38 = 1
            L_0x09e6:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0988 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0988 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0988 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0988 }
                r37 = 7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0988 }
                if (r0 == 0) goto L_0x0a00
                r38 = 0
                goto L_0x0a02
            L_0x0a00:
                r38 = 1
            L_0x0a02:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0988 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0988 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0988 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0988 }
                goto L_0x0a51
            L_0x0a14:
                if (r9 != 0) goto L_0x0a51
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0988 }
                if (r0 == 0) goto L_0x0a51
                org.telegram.messenger.Utilities.pinBitmap(r6)     // Catch:{ all -> 0x0988 }
                goto L_0x0a51
            L_0x0a1e:
                r0 = move-exception
                r7 = r27
                goto L_0x0a4e
            L_0x0a22:
                r0 = move-exception
                r34 = r2
                r35 = r3
                r6 = r21
                r7 = r27
                goto L_0x0a4e
            L_0x0a2c:
                r0 = move-exception
                r34 = r2
                r35 = r3
                r26 = r5
                r21 = r6
                r30 = r7
            L_0x0a37:
                monitor-exit(r14)     // Catch:{ all -> 0x0a3f }
                throw r0     // Catch:{ all -> 0x0a39 }
            L_0x0a39:
                r0 = move-exception
                r6 = r21
                r7 = r27
                goto L_0x0a4e
            L_0x0a3f:
                r0 = move-exception
                goto L_0x0a37
            L_0x0a41:
                r0 = move-exception
                r34 = r2
                r35 = r3
                r26 = r5
                r21 = r6
                r30 = r7
                r7 = r27
            L_0x0a4e:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0a51:
                r20 = r8
                r17 = r9
                r14 = r26
                r9 = r29
                goto L_0x0e10
            L_0x0a5b:
                r34 = r2
                r35 = r3
                r26 = r5
                r21 = r6
                r30 = r7
                r0 = 20
                if (r35 == 0) goto L_0x0a6c
                r0 = 0
                r2 = r0
                goto L_0x0a6d
            L_0x0a6c:
                r2 = r0
            L_0x0a6d:
                if (r2 == 0) goto L_0x0ab5
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0aa6 }
                long r5 = r0.lastCacheOutTime     // Catch:{ all -> 0x0aa6 }
                r14 = 0
                int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
                if (r0 == 0) goto L_0x0ab5
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0aa6 }
                long r5 = r0.lastCacheOutTime     // Catch:{ all -> 0x0aa6 }
                long r14 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0aa6 }
                r3 = r8
                long r7 = (long) r2
                long r14 = r14 - r7
                int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
                if (r0 <= 0) goto L_0x0ab6
                int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0a97 }
                r5 = 21
                if (r0 >= r5) goto L_0x0ab6
                long r5 = (long) r2     // Catch:{ all -> 0x0a97 }
                java.lang.Thread.sleep(r5)     // Catch:{ all -> 0x0a97 }
                goto L_0x0ab6
            L_0x0a97:
                r0 = move-exception
                r20 = r3
                r17 = r9
                r6 = r21
                r14 = r26
                r7 = r27
                r9 = r29
                goto L_0x0e10
            L_0x0aa6:
                r0 = move-exception
                r20 = r8
                r17 = r9
                r6 = r21
                r14 = r26
                r7 = r27
                r9 = r29
                goto L_0x0e10
            L_0x0ab5:
                r3 = r8
            L_0x0ab6:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0e03 }
                long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0e03 }
                long unused = r0.lastCacheOutTime = r5     // Catch:{ all -> 0x0e03 }
                java.lang.Object r5 = r1.sync     // Catch:{ all -> 0x0e03 }
                monitor-enter(r5)     // Catch:{ all -> 0x0e03 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0dee }
                if (r0 == 0) goto L_0x0ad3
                monitor-exit(r5)     // Catch:{ all -> 0x0ac8 }
                return
            L_0x0ac8:
                r0 = move-exception
                r16 = r2
                r20 = r3
                r17 = r9
                r14 = r26
                goto L_0x0df7
            L_0x0ad3:
                monitor-exit(r5)     // Catch:{ all -> 0x0dee }
                if (r23 != 0) goto L_0x0aec
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a97 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0a97 }
                if (r0 == 0) goto L_0x0aec
                if (r9 != 0) goto L_0x0aec
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a97 }
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation     // Catch:{ all -> 0x0a97 }
                java.lang.String r0 = r0.path     // Catch:{ all -> 0x0a97 }
                if (r0 == 0) goto L_0x0ae7
                goto L_0x0aec
            L_0x0ae7:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0a97 }
                r4.inPreferredConfig = r0     // Catch:{ all -> 0x0a97 }
                goto L_0x0af0
            L_0x0aec:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0e03 }
                r4.inPreferredConfig = r0     // Catch:{ all -> 0x0e03 }
            L_0x0af0:
                r5 = 0
                r4.inDither = r5     // Catch:{ all -> 0x0e03 }
                if (r35 == 0) goto L_0x0b1b
                if (r34 != 0) goto L_0x0b1b
                if (r31 == 0) goto L_0x0b0a
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0a97 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0a97 }
                long r5 = r35.longValue()     // Catch:{ all -> 0x0a97 }
                r7 = 1
                android.graphics.Bitmap r0 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r5, r7, r4)     // Catch:{ all -> 0x0a97 }
                r6 = r0
                goto L_0x0b1d
            L_0x0b0a:
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0a97 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0a97 }
                long r5 = r35.longValue()     // Catch:{ all -> 0x0a97 }
                r7 = 1
                android.graphics.Bitmap r0 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r5, r7, r4)     // Catch:{ all -> 0x0a97 }
                r6 = r0
                goto L_0x0b1d
            L_0x0b1b:
                r6 = r21
            L_0x0b1d:
                if (r6 != 0) goto L_0x0c5e
                if (r30 == 0) goto L_0x0b78
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0b6b }
                java.lang.String r5 = "r"
                r0.<init>(r10, r5)     // Catch:{ all -> 0x0b6b }
                java.nio.channels.FileChannel r36 = r0.getChannel()     // Catch:{ all -> 0x0b6b }
                java.nio.channels.FileChannel$MapMode r37 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0b6b }
                r38 = 0
                long r40 = r10.length()     // Catch:{ all -> 0x0b6b }
                java.nio.MappedByteBuffer r5 = r36.map(r37, r38, r40)     // Catch:{ all -> 0x0b6b }
                android.graphics.BitmapFactory$Options r7 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0b6b }
                r7.<init>()     // Catch:{ all -> 0x0b6b }
                r8 = 1
                r7.inJustDecodeBounds = r8     // Catch:{ all -> 0x0b6b }
                int r14 = r5.limit()     // Catch:{ all -> 0x0b6b }
                r15 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r15, r5, r14, r7, r8)     // Catch:{ all -> 0x0b6b }
                int r8 = r7.outWidth     // Catch:{ all -> 0x0b6b }
                int r14 = r7.outHeight     // Catch:{ all -> 0x0b6b }
                android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0b6b }
                android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createBitmap(r8, r14, r15)     // Catch:{ all -> 0x0b6b }
                r6 = r8
                int r8 = r5.limit()     // Catch:{ all -> 0x0b6b }
                boolean r14 = r4.inPurgeable     // Catch:{ all -> 0x0b6b }
                if (r14 != 0) goto L_0x0b5d
                r14 = 1
                goto L_0x0b5e
            L_0x0b5d:
                r14 = 0
            L_0x0b5e:
                r15 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r6, r5, r8, r15, r14)     // Catch:{ all -> 0x0b6b }
                r0.close()     // Catch:{ all -> 0x0b6b }
                r17 = r9
                r9 = r29
                goto L_0x0CLASSNAME
            L_0x0b6b:
                r0 = move-exception
                r20 = r3
                r17 = r9
                r14 = r26
                r7 = r27
                r9 = r29
                goto L_0x0e10
            L_0x0b78:
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0CLASSNAME }
                if (r0 != 0) goto L_0x0bd4
                if (r12 == 0) goto L_0x0b80
                r7 = 0
                goto L_0x0bd5
            L_0x0b80:
                if (r11 == 0) goto L_0x0b8d
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0b6b }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0b6b }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x0b6b }
                r0.<init>((java.io.File) r10, (java.io.File) r5)     // Catch:{ all -> 0x0b6b }
                r5 = r0
                goto L_0x0b93
            L_0x0b8d:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0b6b }
                r0.<init>(r10)     // Catch:{ all -> 0x0b6b }
                r5 = r0
            L_0x0b93:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0b6b }
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation     // Catch:{ all -> 0x0b6b }
                org.telegram.tgnet.TLRPC$Document r0 = r0.document     // Catch:{ all -> 0x0b6b }
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document     // Catch:{ all -> 0x0b6b }
                if (r0 == 0) goto L_0x0bc5
                androidx.exifinterface.media.ExifInterface r0 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0bbb }
                r0.<init>((java.io.InputStream) r5)     // Catch:{ all -> 0x0bbb }
                java.lang.String r7 = "Orientation"
                r8 = 1
                int r7 = r0.getAttributeInt(r7, r8)     // Catch:{ all -> 0x0bbb }
                switch(r7) {
                    case 3: goto L_0x0bb5;
                    case 6: goto L_0x0bb2;
                    case 8: goto L_0x0baf;
                    default: goto L_0x0bac;
                }
            L_0x0bac:
                r8 = r29
                goto L_0x0bb8
            L_0x0baf:
                r8 = 270(0x10e, float:3.78E-43)
                goto L_0x0bb8
            L_0x0bb2:
                r8 = 90
                goto L_0x0bb8
            L_0x0bb5:
                r8 = 180(0xb4, float:2.52E-43)
            L_0x0bb8:
                r29 = r8
                goto L_0x0bbc
            L_0x0bbb:
                r0 = move-exception
            L_0x0bbc:
                java.nio.channels.FileChannel r0 = r5.getChannel()     // Catch:{ all -> 0x0b6b }
                r7 = 0
                r0.position(r7)     // Catch:{ all -> 0x0b6b }
            L_0x0bc5:
                r7 = 0
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r5, r7, r4)     // Catch:{ all -> 0x0b6b }
                r6 = r0
                r5.close()     // Catch:{ all -> 0x0b6b }
                r17 = r9
                r9 = r29
                goto L_0x0CLASSNAME
            L_0x0bd4:
                r7 = 0
            L_0x0bd5:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0CLASSNAME }
                java.lang.String r5 = "r"
                r0.<init>(r10, r5)     // Catch:{ all -> 0x0CLASSNAME }
                long r14 = r0.length()     // Catch:{ all -> 0x0CLASSNAME }
                int r5 = (int) r14     // Catch:{ all -> 0x0CLASSNAME }
                r8 = 0
                java.lang.ThreadLocal r14 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0CLASSNAME }
                java.lang.Object r14 = r14.get()     // Catch:{ all -> 0x0CLASSNAME }
                byte[] r14 = (byte[]) r14     // Catch:{ all -> 0x0CLASSNAME }
                if (r14 == 0) goto L_0x0bf3
                int r15 = r14.length     // Catch:{ all -> 0x0b6b }
                if (r15 < r5) goto L_0x0bf3
                r15 = r14
                goto L_0x0bf4
            L_0x0bf3:
                r15 = r7
            L_0x0bf4:
                if (r15 != 0) goto L_0x0CLASSNAME
                byte[] r7 = new byte[r5]     // Catch:{ all -> 0x0b6b }
                r15 = r7
                r14 = r7
                java.lang.ThreadLocal r7 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0b6b }
                r7.set(r14)     // Catch:{ all -> 0x0b6b }
            L_0x0CLASSNAME:
                r7 = 0
                r0.readFully(r15, r7, r5)     // Catch:{ all -> 0x0CLASSNAME }
                r0.close()     // Catch:{ all -> 0x0CLASSNAME }
                r16 = 0
                if (r12 == 0) goto L_0x0c2b
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r15, (int) r7, (int) r5, (org.telegram.messenger.SecureDocumentKey) r12)     // Catch:{ all -> 0x0CLASSNAME }
                r18 = r8
                r17 = r9
                long r8 = (long) r5
                byte[] r8 = org.telegram.messenger.Utilities.computeSHA256(r15, r7, r8)     // Catch:{ all -> 0x0c3a }
                r7 = r8
                if (r13 == 0) goto L_0x0CLASSNAME
                boolean r8 = java.util.Arrays.equals(r7, r13)     // Catch:{ all -> 0x0c3a }
                if (r8 != 0) goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r8 = 1
                r16 = r8
            L_0x0CLASSNAME:
                r8 = 0
                byte r9 = r15[r8]     // Catch:{ all -> 0x0c3a }
                r8 = r9 & 255(0xff, float:3.57E-43)
                int r5 = r5 - r8
                goto L_0x0CLASSNAME
            L_0x0c2b:
                r18 = r8
                r17 = r9
                if (r11 == 0) goto L_0x0CLASSNAME
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage     // Catch:{ all -> 0x0c3a }
                java.io.File r7 = r7.encryptionKeyPath     // Catch:{ all -> 0x0c3a }
                r8 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r15, (int) r8, (int) r5, (java.io.File) r7)     // Catch:{ all -> 0x0c3a }
                goto L_0x0CLASSNAME
            L_0x0c3a:
                r0 = move-exception
                r20 = r3
                r14 = r26
                r7 = r27
                r9 = r29
                goto L_0x0e10
            L_0x0CLASSNAME:
                r8 = r18
            L_0x0CLASSNAME:
                if (r16 != 0) goto L_0x0c4e
                android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeByteArray(r15, r8, r5, r4)     // Catch:{ all -> 0x0c3a }
                r6 = r7
            L_0x0c4e:
                r9 = r29
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r0 = move-exception
                r17 = r9
                r20 = r3
                r14 = r26
                r7 = r27
                r9 = r29
                goto L_0x0e10
            L_0x0c5e:
                r17 = r9
                r9 = r29
            L_0x0CLASSNAME:
                if (r6 != 0) goto L_0x0c8d
                if (r28 == 0) goto L_0x0CLASSNAME
                long r7 = r10.length()     // Catch:{ all -> 0x0c7a }
                r14 = 0
                int r0 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
                if (r0 == 0) goto L_0x0CLASSNAME
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0c7a }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0c7a }
                if (r0 != 0) goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r10.delete()     // Catch:{ all -> 0x0c7a }
                goto L_0x0CLASSNAME
            L_0x0c7a:
                r0 = move-exception
                r20 = r3
                r14 = r26
                r7 = r27
                goto L_0x0e10
            L_0x0CLASSNAME:
                r20 = r3
                r24 = r9
                r14 = r26
                r7 = r27
                goto L_0x0de1
            L_0x0c8d:
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0de4 }
                java.lang.String r5 = r5.filter     // Catch:{ all -> 0x0de4 }
                if (r5 == 0) goto L_0x0dc7
                int r5 = r6.getWidth()     // Catch:{ all -> 0x0de4 }
                float r5 = (float) r5     // Catch:{ all -> 0x0de4 }
                int r7 = r6.getHeight()     // Catch:{ all -> 0x0de4 }
                float r7 = (float) r7     // Catch:{ all -> 0x0de4 }
                boolean r8 = r4.inPurgeable     // Catch:{ all -> 0x0de4 }
                if (r8 != 0) goto L_0x0cf5
                int r8 = (r3 > r24 ? 1 : (r3 == r24 ? 0 : -1))
                if (r8 == 0) goto L_0x0cf5
                int r8 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
                if (r8 == 0) goto L_0x0cf5
                r8 = 1101004800(0x41a00000, float:20.0)
                float r8 = r8 + r3
                int r8 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r8 <= 0) goto L_0x0cf5
                int r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r8 <= 0) goto L_0x0cd2
                int r8 = (r3 > r26 ? 1 : (r3 == r26 ? 0 : -1))
                if (r8 <= 0) goto L_0x0cd2
                float r8 = r5 / r3
                int r14 = (r8 > r25 ? 1 : (r8 == r25 ? 0 : -1))
                if (r14 <= 0) goto L_0x0ccc
                int r14 = (int) r3
                float r15 = r7 / r8
                int r15 = (int) r15
                r16 = r2
                r2 = 1
                android.graphics.Bitmap r14 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r14, r15, r2)     // Catch:{ all -> 0x0c7a }
                r2 = r14
                goto L_0x0ccf
            L_0x0ccc:
                r16 = r2
                r2 = r6
            L_0x0ccf:
                r14 = r26
                goto L_0x0cee
            L_0x0cd2:
                r16 = r2
                float r2 = r7 / r26
                int r8 = (r2 > r25 ? 1 : (r2 == r25 ? 0 : -1))
                if (r8 <= 0) goto L_0x0ce9
                float r8 = r5 / r2
                int r8 = (int) r8
                r14 = r26
                int r15 = (int) r14
                r18 = r2
                r2 = 1
                android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r8, r15, r2)     // Catch:{ all -> 0x0d19 }
                r2 = r8
                goto L_0x0cee
            L_0x0ce9:
                r18 = r2
                r14 = r26
                r2 = r6
            L_0x0cee:
                if (r6 == r2) goto L_0x0cf9
                r6.recycle()     // Catch:{ all -> 0x0d19 }
                r6 = r2
                goto L_0x0cf9
            L_0x0cf5:
                r16 = r2
                r14 = r26
            L_0x0cf9:
                if (r6 == 0) goto L_0x0dbe
                if (r22 == 0) goto L_0x0d62
                r2 = r6
                int r8 = r6.getWidth()     // Catch:{ all -> 0x0d59 }
                int r15 = r6.getHeight()     // Catch:{ all -> 0x0d59 }
                r18 = r0
                int r0 = r8 * r15
                r20 = r2
                r2 = 22500(0x57e4, float:3.1529E-41)
                if (r0 <= r2) goto L_0x0d20
                r0 = 100
                r2 = 0
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r0, r0, r2)     // Catch:{ all -> 0x0d19 }
                r2 = r0
                goto L_0x0d22
            L_0x0d19:
                r0 = move-exception
                r20 = r3
                r7 = r27
                goto L_0x0e10
            L_0x0d20:
                r2 = r20
            L_0x0d22:
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0d59 }
                if (r0 == 0) goto L_0x0d28
                r0 = 0
                goto L_0x0d29
            L_0x0d28:
                r0 = 1
            L_0x0d29:
                r20 = r3
                int r3 = r2.getWidth()     // Catch:{ all -> 0x0d52 }
                r21 = r8
                int r8 = r2.getHeight()     // Catch:{ all -> 0x0d52 }
                r24 = r9
                int r9 = r2.getRowBytes()     // Catch:{ all -> 0x0d4b }
                int r0 = org.telegram.messenger.Utilities.needInvert(r2, r0, r3, r8, r9)     // Catch:{ all -> 0x0d4b }
                if (r0 == 0) goto L_0x0d43
                r0 = 1
                goto L_0x0d44
            L_0x0d43:
                r0 = 0
            L_0x0d44:
                r3 = r0
                if (r2 == r6) goto L_0x0d6a
                r2.recycle()     // Catch:{ all -> 0x0d83 }
                goto L_0x0d6a
            L_0x0d4b:
                r0 = move-exception
                r9 = r24
                r7 = r27
                goto L_0x0e10
            L_0x0d52:
                r0 = move-exception
                r24 = r9
                r7 = r27
                goto L_0x0e10
            L_0x0d59:
                r0 = move-exception
                r20 = r3
                r24 = r9
                r7 = r27
                goto L_0x0e10
            L_0x0d62:
                r18 = r0
                r20 = r3
                r24 = r9
                r3 = r27
            L_0x0d6a:
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                if (r17 == 0) goto L_0x0d89
                int r2 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                if (r2 > 0) goto L_0x0d76
                int r2 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
                if (r2 <= 0) goto L_0x0d89
            L_0x0d76:
                r2 = 80
                r8 = 0
                android.graphics.Bitmap r2 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r2, r2, r8)     // Catch:{ all -> 0x0d83 }
                r7 = 1117782016(0x42a00000, float:80.0)
                r5 = 1117782016(0x42a00000, float:80.0)
                r6 = r2
                goto L_0x0d89
            L_0x0d83:
                r0 = move-exception
                r7 = r3
                r9 = r24
                goto L_0x0e10
            L_0x0d89:
                if (r17 == 0) goto L_0x0dba
                int r2 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                if (r2 >= 0) goto L_0x0dba
                int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x0dba
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0d83 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0d83 }
                if (r0 != r2) goto L_0x0db7
                r37 = 3
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0d83 }
                if (r0 == 0) goto L_0x0da4
                r38 = 0
                goto L_0x0da6
            L_0x0da4:
                r38 = 1
            L_0x0da6:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0d83 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0d83 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0d83 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0d83 }
            L_0x0db7:
                r0 = 1
                r7 = r3
                goto L_0x0dd3
            L_0x0dba:
                r7 = r3
                r0 = r18
                goto L_0x0dd3
            L_0x0dbe:
                r18 = r0
                r20 = r3
                r24 = r9
                r7 = r27
                goto L_0x0dd3
            L_0x0dc7:
                r18 = r0
                r16 = r2
                r20 = r3
                r24 = r9
                r14 = r26
                r7 = r27
            L_0x0dd3:
                if (r0 != 0) goto L_0x0de1
                boolean r2 = r4.inPurgeable     // Catch:{ all -> 0x0ddd }
                if (r2 == 0) goto L_0x0de1
                org.telegram.messenger.Utilities.pinBitmap(r6)     // Catch:{ all -> 0x0ddd }
                goto L_0x0de1
            L_0x0ddd:
                r0 = move-exception
                r9 = r24
                goto L_0x0e10
            L_0x0de1:
                r9 = r24
                goto L_0x0e10
            L_0x0de4:
                r0 = move-exception
                r20 = r3
                r24 = r9
                r14 = r26
                r7 = r27
                goto L_0x0e10
            L_0x0dee:
                r0 = move-exception
                r16 = r2
                r20 = r3
                r17 = r9
                r14 = r26
            L_0x0df7:
                monitor-exit(r5)     // Catch:{ all -> 0x0e01 }
                throw r0     // Catch:{ all -> 0x0df9 }
            L_0x0df9:
                r0 = move-exception
                r6 = r21
                r7 = r27
                r9 = r29
                goto L_0x0e10
            L_0x0e01:
                r0 = move-exception
                goto L_0x0df7
            L_0x0e03:
                r0 = move-exception
                r20 = r3
                r17 = r9
                r14 = r26
                r6 = r21
                r7 = r27
                r9 = r29
            L_0x0e10:
                java.lang.Thread.interrupted()
                if (r7 != 0) goto L_0x0e26
                if (r9 == 0) goto L_0x0e18
                goto L_0x0e26
            L_0x0e18:
                if (r6 == 0) goto L_0x0e20
                android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
                r2.<init>(r6)
                goto L_0x0e21
            L_0x0e20:
                r2 = 0
            L_0x0e21:
                r1.onPostExecute(r2)
                goto L_0x0e97
            L_0x0e26:
                if (r6 == 0) goto L_0x0e2e
                org.telegram.messenger.ExtendedBitmapDrawable r2 = new org.telegram.messenger.ExtendedBitmapDrawable
                r2.<init>(r6, r7, r9)
                goto L_0x0e2f
            L_0x0e2e:
                r2 = 0
            L_0x0e2f:
                r1.onPostExecute(r2)
                goto L_0x0e97
            L_0x0e33:
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r0 = r0.x
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r2 = r2.y
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0e6d
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                java.lang.String r4 = "_"
                java.lang.String[] r3 = r3.split(r4)
                int r4 = r3.length
                r5 = 2
                if (r4 < r5) goto L_0x0e6a
                r4 = 0
                r5 = r3[r4]
                float r5 = java.lang.Float.parseFloat(r5)
                r6 = 1
                r7 = r3[r6]
                float r7 = java.lang.Float.parseFloat(r7)
                float r8 = org.telegram.messenger.AndroidUtilities.density
                float r8 = r8 * r5
                int r0 = (int) r8
                float r8 = org.telegram.messenger.AndroidUtilities.density
                float r8 = r8 * r7
                int r2 = (int) r8
                r3 = r2
                r2 = r0
                goto L_0x0e71
            L_0x0e6a:
                r4 = 0
                r6 = 1
                goto L_0x0e6f
            L_0x0e6d:
                r4 = 0
                r6 = 1
            L_0x0e6f:
                r3 = r2
                r2 = r0
            L_0x0e71:
                r5 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0e86 }
                java.io.File r0 = r0.finalFilePath     // Catch:{ all -> 0x0e86 }
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage     // Catch:{ all -> 0x0e86 }
                int r7 = r7.imageType     // Catch:{ all -> 0x0e86 }
                r8 = 4
                if (r7 != r8) goto L_0x0e7f
                r7 = 1
                goto L_0x0e80
            L_0x0e7f:
                r7 = 0
            L_0x0e80:
                android.graphics.Bitmap r0 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r2, (int) r3, (boolean) r7)     // Catch:{ all -> 0x0e86 }
                r5 = r0
                goto L_0x0e8a
            L_0x0e86:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0e8a:
                if (r5 == 0) goto L_0x0e92
                android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable
                r0.<init>(r5)
                goto L_0x0e93
            L_0x0e92:
                r0 = 0
            L_0x0e93:
                r1.onPostExecute(r0)
            L_0x0e97:
                return
            L_0x0e98:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0e98 }
                goto L_0x0e9c
            L_0x0e9b:
                throw r0
            L_0x0e9c:
                goto L_0x0e9b
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void loadLastFrame(RLottieDrawable lottieDrawable, int w, int h) {
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(2.0f, 2.0f, ((float) w) / 2.0f, ((float) h) / 2.0f);
            AndroidUtilities.runOnUIThread(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda3(this, lottieDrawable, canvas, bitmap));
        }

        /* renamed from: lambda$loadLastFrame$1$org-telegram-messenger-ImageLoader$CacheOutTask  reason: not valid java name */
        public /* synthetic */ void m1900x8CLASSNAMEa141(RLottieDrawable lottieDrawable, Canvas canvas, Bitmap bitmap) {
            lottieDrawable.setOnFrameReadyRunnable(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda2(this, lottieDrawable, canvas, bitmap));
            lottieDrawable.setCurrentFrame(lottieDrawable.getFramesCount() - 1, true, true);
        }

        /* renamed from: lambda$loadLastFrame$0$org-telegram-messenger-ImageLoader$CacheOutTask  reason: not valid java name */
        public /* synthetic */ void m1899x8c7CLASSNAME(RLottieDrawable lottieDrawable, Canvas canvas, Bitmap bitmap) {
            lottieDrawable.setOnFrameReadyRunnable((Runnable) null);
            BitmapDrawable bitmapDrawable = null;
            if (!(lottieDrawable.getBackgroundBitmap() == null && lottieDrawable.getRenderingBitmap() == null)) {
                canvas.drawBitmap(lottieDrawable.getBackgroundBitmap() != null ? lottieDrawable.getBackgroundBitmap() : lottieDrawable.getRenderingBitmap(), 0.0f, 0.0f, (Paint) null);
                bitmapDrawable = new BitmapDrawable(bitmap);
            }
            onPostExecute(bitmapDrawable);
            lottieDrawable.recycle();
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda0(this, drawable));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: android.graphics.drawable.Drawable} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* renamed from: lambda$onPostExecute$3$org-telegram-messenger-ImageLoader$CacheOutTask  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m1902xba899b28(android.graphics.drawable.Drawable r8) {
            /*
                r7 = this;
                r0 = 0
                r1 = 0
                boolean r2 = r8 instanceof org.telegram.ui.Components.RLottieDrawable
                if (r2 == 0) goto L_0x003f
                r2 = r8
                org.telegram.ui.Components.RLottieDrawable r2 = (org.telegram.ui.Components.RLottieDrawable) r2
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r3 = r3.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                java.lang.Object r3 = r3.get(r4)
                r0 = r3
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x002b
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r3 = r3.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                r3.put(r4, r2)
                r0 = r2
                goto L_0x002e
            L_0x002b:
                r2.recycle()
            L_0x002e:
                if (r0 == 0) goto L_0x003d
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                r3.incrementUseCount(r4)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r7.cacheImage
                java.lang.String r1 = r3.key
            L_0x003d:
                goto L_0x0110
            L_0x003f:
                boolean r2 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                if (r2 == 0) goto L_0x007a
                r2 = r8
                org.telegram.ui.Components.AnimatedFileDrawable r2 = (org.telegram.ui.Components.AnimatedFileDrawable) r2
                boolean r3 = r2.isWebmSticker
                if (r3 == 0) goto L_0x0077
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                android.graphics.drawable.BitmapDrawable r0 = r3.getFromLottieCache(r4)
                if (r0 != 0) goto L_0x0065
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r3 = r3.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                r3.put(r4, r2)
                r0 = r2
                goto L_0x0068
            L_0x0065:
                r2.recycle()
            L_0x0068:
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                r3.incrementUseCount(r4)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r7.cacheImage
                java.lang.String r1 = r3.key
                goto L_0x010f
            L_0x0077:
                r0 = r8
                goto L_0x010f
            L_0x007a:
                boolean r2 = r8 instanceof android.graphics.drawable.BitmapDrawable
                if (r2 == 0) goto L_0x010f
                r2 = r8
                android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                android.graphics.drawable.BitmapDrawable r0 = r3.getFromMemCache(r4)
                r3 = 1
                if (r0 != 0) goto L_0x00f6
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                java.lang.String r5 = "_f"
                boolean r4 = r4.endsWith(r5)
                if (r4 == 0) goto L_0x00a9
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r4 = r4.wallpaperMemCache
                org.telegram.messenger.ImageLoader$CacheImage r5 = r7.cacheImage
                java.lang.String r5 = r5.key
                r4.put(r5, r2)
                r3 = 0
                goto L_0x00f4
            L_0x00a9:
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r4 = r4.key
                java.lang.String r5 = "_isc"
                boolean r4 = r4.endsWith(r5)
                if (r4 != 0) goto L_0x00e7
                android.graphics.Bitmap r4 = r2.getBitmap()
                int r4 = r4.getWidth()
                float r4 = (float) r4
                float r5 = org.telegram.messenger.AndroidUtilities.density
                r6 = 1117782016(0x42a00000, float:80.0)
                float r5 = r5 * r6
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 > 0) goto L_0x00e7
                android.graphics.Bitmap r4 = r2.getBitmap()
                int r4 = r4.getHeight()
                float r4 = (float) r4
                float r5 = org.telegram.messenger.AndroidUtilities.density
                float r5 = r5 * r6
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 > 0) goto L_0x00e7
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r4 = r4.smallImagesMemCache
                org.telegram.messenger.ImageLoader$CacheImage r5 = r7.cacheImage
                java.lang.String r5 = r5.key
                r4.put(r5, r2)
                goto L_0x00f4
            L_0x00e7:
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r4 = r4.memCache
                org.telegram.messenger.ImageLoader$CacheImage r5 = r7.cacheImage
                java.lang.String r5 = r5.key
                r4.put(r5, r2)
            L_0x00f4:
                r0 = r2
                goto L_0x00fd
            L_0x00f6:
                android.graphics.Bitmap r4 = r2.getBitmap()
                r4.recycle()
            L_0x00fd:
                if (r0 == 0) goto L_0x0110
                if (r3 == 0) goto L_0x0110
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r5 = r7.cacheImage
                java.lang.String r5 = r5.key
                r4.incrementUseCount(r5)
                org.telegram.messenger.ImageLoader$CacheImage r4 = r7.cacheImage
                java.lang.String r1 = r4.key
                goto L_0x0110
            L_0x010f:
            L_0x0110:
                r2 = r0
                r3 = r1
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.DispatchQueue r4 = r4.imageLoadQueue
                org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1 r5 = new org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1
                r5.<init>(r7, r2, r3)
                r4.postRunnable(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.m1902xba899b28(android.graphics.drawable.Drawable):void");
        }

        /* renamed from: lambda$onPostExecute$2$org-telegram-messenger-ImageLoader$CacheOutTask  reason: not valid java name */
        public /* synthetic */ void m1901xbb000127(Drawable toSetFinal, String decrementKetFinal) {
            this.cacheImage.setImageAndClear(toSetFinal, decrementKetFinal);
        }

        public void cancel() {
            synchronized (this.sync) {
                try {
                    this.isCancelled = true;
                    Thread thread = this.runningThread;
                    if (thread != null) {
                        thread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isAnimatedAvatar(String filter) {
        return filter != null && filter.endsWith("avatar");
    }

    /* access modifiers changed from: private */
    public BitmapDrawable getFromMemCache(String key) {
        BitmapDrawable drawable = this.memCache.get(key);
        if (drawable == null) {
            drawable = this.smallImagesMemCache.get(key);
        }
        if (drawable == null) {
            drawable = this.wallpaperMemCache.get(key);
        }
        if (drawable == null) {
            return getFromLottieCache(key);
        }
        return drawable;
    }

    public static Bitmap getStrippedPhotoBitmap(byte[] photoBytes, String filter) {
        int len = (photoBytes.length - 3) + Bitmaps.header.length + Bitmaps.footer.length;
        byte[] bytes = bytesLocal.get();
        byte[] data = (bytes == null || bytes.length < len) ? null : bytes;
        if (data == null) {
            byte[] bytes2 = new byte[len];
            data = bytes2;
            bytesLocal.set(bytes2);
        }
        System.arraycopy(Bitmaps.header, 0, data, 0, Bitmaps.header.length);
        System.arraycopy(photoBytes, 3, data, Bitmaps.header.length, photoBytes.length - 3);
        System.arraycopy(Bitmaps.footer, 0, data, (Bitmaps.header.length + photoBytes.length) - 3, Bitmaps.footer.length);
        data[164] = photoBytes[1];
        data[166] = photoBytes[2];
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, len);
        if (bitmap != null && !TextUtils.isEmpty(filter) && filter.contains("b")) {
            Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
        }
        return bitmap;
    }

    private class CacheImage {
        protected ArtworkLoadTask artworkTask;
        protected CacheOutTask cacheTask;
        protected int currentAccount;
        protected File encryptionKeyPath;
        protected String ext;
        protected String filter;
        protected ArrayList<String> filters;
        protected File finalFilePath;
        protected HttpImageTask httpTask;
        protected ImageLocation imageLocation;
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected ArrayList<Integer> imageReceiverGuidsArray;
        protected int imageType;
        protected String key;
        protected ArrayList<String> keys;
        protected Object parentObject;
        protected SecureDocument secureDocument;
        protected long size;
        protected File tempFilePath;
        protected int type;
        protected ArrayList<Integer> types;
        protected String url;

        private CacheImage() {
            this.imageReceiverArray = new ArrayList<>();
            this.imageReceiverGuidsArray = new ArrayList<>();
            this.keys = new ArrayList<>();
            this.filters = new ArrayList<>();
            this.types = new ArrayList<>();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String key2, String filter2, int type2, int guid) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index >= 0) {
                this.imageReceiverGuidsArray.set(index, Integer.valueOf(guid));
                return;
            }
            this.imageReceiverArray.add(imageReceiver);
            this.imageReceiverGuidsArray.add(Integer.valueOf(guid));
            this.keys.add(key2);
            this.filters.add(filter2);
            this.types.add(Integer.valueOf(type2));
            ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(type2), this);
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String key2, String filter2, int type2, int guid) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index != -1) {
                if (this.types.get(index).intValue() != type2) {
                    ArrayList<ImageReceiver> arrayList = this.imageReceiverArray;
                    index = arrayList.subList(index + 1, arrayList.size()).indexOf(imageReceiver);
                    if (index == -1) {
                        return;
                    }
                }
                this.imageReceiverGuidsArray.set(index, Integer.valueOf(guid));
                this.keys.set(index, key2);
                this.filters.set(index, filter2);
            }
        }

        public void setImageReceiverGuid(ImageReceiver imageReceiver, int guid) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index != -1) {
                this.imageReceiverGuidsArray.set(index, Integer.valueOf(guid));
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int currentMediaType = this.type;
            int a = 0;
            while (a < this.imageReceiverArray.size()) {
                ImageReceiver obj = this.imageReceiverArray.get(a);
                if (obj == null || obj == imageReceiver) {
                    this.imageReceiverArray.remove(a);
                    this.imageReceiverGuidsArray.remove(a);
                    this.keys.remove(a);
                    this.filters.remove(a);
                    currentMediaType = this.types.remove(a).intValue();
                    if (obj != null) {
                        ImageLoader.this.imageLoadingByTag.remove(obj.getTag(currentMediaType));
                    }
                    a--;
                }
                a++;
            }
            if (this.imageReceiverArray.isEmpty()) {
                if (this.imageLocation != null && !ImageLoader.this.forceLoadingImages.containsKey(this.key)) {
                    if (this.imageLocation.location != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.FileLocation) this.imageLocation.location, this.ext);
                    } else if (this.imageLocation.document != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.document);
                    } else if (this.imageLocation.secureDocument != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.secureDocument);
                    } else if (this.imageLocation.webFile != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.webFile);
                    }
                }
                if (this.cacheTask != null) {
                    if (currentMediaType == 1) {
                        ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
                    } else {
                        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
                    }
                    this.cacheTask.cancel();
                    this.cacheTask = null;
                }
                if (this.httpTask != null) {
                    ImageLoader.this.httpTasks.remove(this.httpTask);
                    this.httpTask.cancel(true);
                    this.httpTask = null;
                }
                if (this.artworkTask != null) {
                    ImageLoader.this.artworkTasks.remove(this.artworkTask);
                    this.artworkTask.cancel(true);
                    this.artworkTask = null;
                }
                if (this.url != null) {
                    ImageLoader.this.imageLoadingByUrl.remove(this.url);
                }
                if (this.key != null) {
                    ImageLoader.this.imageLoadingByKeys.remove(this.key);
                }
            }
        }

        public void setImageAndClear(Drawable image, String decrementKey) {
            if (image != null) {
                AndroidUtilities.runOnUIThread(new ImageLoader$CacheImage$$ExternalSyntheticLambda0(this, image, new ArrayList<>(this.imageReceiverArray), new ArrayList<>(this.imageReceiverGuidsArray), decrementKey));
            }
            for (int a = 0; a < this.imageReceiverArray.size(); a++) {
                ImageLoader.this.imageLoadingByTag.remove(this.imageReceiverArray.get(a).getTag(this.type));
            }
            this.imageReceiverArray.clear();
            this.imageReceiverGuidsArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }

        /* renamed from: lambda$setImageAndClear$0$org-telegram-messenger-ImageLoader$CacheImage  reason: not valid java name */
        public /* synthetic */ void m1898x9483CLASSNAME(Drawable image, ArrayList finalImageReceiverArray, ArrayList finalImageReceiverGuidsArray, String decrementKey) {
            if (image instanceof AnimatedFileDrawable) {
                boolean imageSet = false;
                AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) image;
                for (int a = 0; a < finalImageReceiverArray.size(); a++) {
                    AnimatedFileDrawable toSet = fileDrawable;
                    if (((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(toSet, this.key, this.type, false, ((Integer) finalImageReceiverGuidsArray.get(a)).intValue())) {
                        if (toSet == fileDrawable) {
                            imageSet = true;
                        }
                    } else if (toSet != fileDrawable) {
                        toSet.recycle();
                    }
                }
                if (!imageSet) {
                    fileDrawable.recycle();
                }
            } else {
                for (int a2 = 0; a2 < finalImageReceiverArray.size(); a2++) {
                    ((ImageReceiver) finalImageReceiverArray.get(a2)).setImageBitmapByKey(image, this.key, this.types.get(a2).intValue(), false, ((Integer) finalImageReceiverGuidsArray.get(a2)).intValue());
                }
            }
            if (decrementKey != null) {
                ImageLoader.this.decrementUseCount(decrementKey);
            }
        }
    }

    public static ImageLoader getInstance() {
        ImageLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (ImageLoader.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ImageLoader imageLoader = new ImageLoader();
                    localInstance = imageLoader;
                    Instance = imageLoader;
                }
            }
        }
        return localInstance;
    }

    public ImageLoader() {
        int maxSize;
        boolean z = false;
        this.currentHttpTasksCount = 0;
        this.currentArtworkTasksCount = 0;
        this.testWebFile = new ConcurrentHashMap<>();
        this.httpFileLoadTasks = new LinkedList<>();
        this.httpFileLoadTasksByKeys = new HashMap<>();
        this.retryHttpsTasks = new HashMap<>();
        this.currentHttpFileLoadTasksCount = 0;
        this.ignoreRemoval = null;
        this.lastCacheOutTime = 0;
        this.lastImageNum = 0;
        this.telegramPath = null;
        this.thumbGeneratingQueue.setPriority(1);
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
        z = memoryClass >= 192 ? true : z;
        this.canForce8888 = z;
        if (z) {
            maxSize = 30;
        } else {
            maxSize = 15;
        }
        int cacheSize = Math.min(maxSize, memoryClass / 7) * 1024 * 1024;
        this.memCache = new LruCache<BitmapDrawable>((int) (((float) cacheSize) * 0.8f)) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                if (ImageLoader.this.ignoreRemoval == null || !ImageLoader.this.ignoreRemoval.equals(key)) {
                    Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
                    if (count == null || count.intValue() == 0) {
                        Bitmap b = oldValue.getBitmap();
                        if (!b.isRecycled()) {
                            ArrayList<Bitmap> bitmapToRecycle = new ArrayList<>();
                            bitmapToRecycle.add(b);
                            AndroidUtilities.recycleBitmaps(bitmapToRecycle);
                        }
                    }
                }
            }
        };
        this.smallImagesMemCache = new LruCache<BitmapDrawable>((int) (((float) cacheSize) * 0.2f)) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                if (ImageLoader.this.ignoreRemoval == null || !ImageLoader.this.ignoreRemoval.equals(key)) {
                    Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
                    if (count == null || count.intValue() == 0) {
                        Bitmap b = oldValue.getBitmap();
                        if (!b.isRecycled()) {
                            ArrayList<Bitmap> bitmapToRecycle = new ArrayList<>();
                            bitmapToRecycle.add(b);
                            AndroidUtilities.recycleBitmaps(bitmapToRecycle);
                        }
                    }
                }
            }
        };
        this.wallpaperMemCache = new LruCache<BitmapDrawable>(cacheSize / 4) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }
        };
        this.lottieMemCache = new LruCache<BitmapDrawable>(10485760) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapDrawable value) {
                return value.getIntrinsicWidth() * value.getIntrinsicHeight() * 4 * 2;
            }

            public BitmapDrawable put(String key, BitmapDrawable value) {
                if (value instanceof AnimatedFileDrawable) {
                    ImageLoader.this.cachedAnimatedFileDrawables.add((AnimatedFileDrawable) value);
                }
                return (BitmapDrawable) super.put(key, value);
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
                if (oldValue instanceof AnimatedFileDrawable) {
                    ImageLoader.this.cachedAnimatedFileDrawables.remove((AnimatedFileDrawable) oldValue);
                }
                if (count == null || count.intValue() == 0) {
                    if (oldValue instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) oldValue).recycle();
                    }
                    if (oldValue instanceof RLottieDrawable) {
                        ((RLottieDrawable) oldValue).recycle();
                    }
                }
            }
        };
        SparseArray<File> mediaDirs = new SparseArray<>();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        AndroidUtilities.createEmptyFile(new File(cachePath, ".nomedia"));
        mediaDirs.put(4, cachePath);
        for (int a = 0; a < 4; a++) {
            final int currentAccount = a;
            FileLoader.getInstance(a).setDelegate(new FileLoader.FileLoaderDelegate() {
                public void fileUploadProgressChanged(FileUploadOperation operation, String location, long uploadedSize, long totalSize, boolean isEncrypted) {
                    FileUploadOperation fileUploadOperation = operation;
                    ImageLoader.this.fileProgresses.put(location, new long[]{uploadedSize, totalSize});
                    long currentTime = SystemClock.elapsedRealtime();
                    if (fileUploadOperation.lastProgressUpdateTime == 0 || fileUploadOperation.lastProgressUpdateTime < currentTime - 100 || uploadedSize == totalSize) {
                        fileUploadOperation.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda1(currentAccount, location, uploadedSize, totalSize, isEncrypted));
                    }
                }

                public void fileDidUploaded(String location, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$5$$ExternalSyntheticLambda4(this, currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize));
                }

                /* renamed from: lambda$fileDidUploaded$2$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m1893lambda$fileDidUploaded$2$orgtelegrammessengerImageLoader$5(int currentAccount, String location, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda2(currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize));
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidFailedUpload(String location, boolean isEncrypted) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$5$$ExternalSyntheticLambda5(this, currentAccount, location, isEncrypted));
                }

                /* renamed from: lambda$fileDidFailedUpload$4$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m1891xaCLASSNAMEfd2(int currentAccount, String location, boolean isEncrypted) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda3(currentAccount, location, isEncrypted));
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidLoaded(String location, File finalFile, Object parentObject, int type) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda6(this, finalFile, location, parentObject, currentAccount, type));
                }

                /* renamed from: lambda$fileDidLoaded$5$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m1892lambda$fileDidLoaded$5$orgtelegrammessengerImageLoader$5(File finalFile, String location, Object parentObject, int currentAccount, int type) {
                    int flag;
                    if (!(SharedConfig.saveToGalleryFlags == 0 || finalFile == null || ((!location.endsWith(".mp4") && !location.endsWith(".jpg")) || !(parentObject instanceof MessageObject)))) {
                        long dialogId = ((MessageObject) parentObject).getDialogId();
                        if (dialogId >= 0) {
                            flag = 1;
                        } else if (ChatObject.isChannelAndNotMegaGroup(MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-dialogId)))) {
                            flag = 4;
                        } else {
                            flag = 2;
                        }
                        if ((SharedConfig.saveToGalleryFlags & flag) != 0) {
                            AndroidUtilities.addMediaToGallery(finalFile.toString());
                        }
                    }
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileLoaded, location, finalFile);
                    ImageLoader.this.fileDidLoaded(location, finalFile, type);
                }

                public void fileDidFailedLoad(String location, int canceled) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda7(this, location, canceled, currentAccount));
                }

                /* renamed from: lambda$fileDidFailedLoad$6$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m1890lambda$fileDidFailedLoad$6$orgtelegrammessengerImageLoader$5(String location, int canceled, int currentAccount) {
                    ImageLoader.this.fileDidFailedLoad(location, canceled);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, location, Integer.valueOf(canceled));
                }

                public void fileLoadProgressChanged(FileLoadOperation operation, String location, long uploadedSize, long totalSize) {
                    FileLoadOperation fileLoadOperation = operation;
                    String str = location;
                    ImageLoader.this.fileProgresses.put(location, new long[]{uploadedSize, totalSize});
                    long currentTime = SystemClock.elapsedRealtime();
                    if (fileLoadOperation.lastProgressUpdateTime == 0 || fileLoadOperation.lastProgressUpdateTime < currentTime - 500 || uploadedSize == 0) {
                        fileLoadOperation.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda0(currentAccount, location, uploadedSize, totalSize));
                    }
                }
            });
        }
        FileLoader.setMediaDirs(mediaDirs);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent intent) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("file system changed");
                }
                Runnable r = new ImageLoader$6$$ExternalSyntheticLambda0(this);
                if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                    AndroidUtilities.runOnUIThread(r, 1000);
                } else {
                    r.run();
                }
            }

            /* renamed from: lambda$onReceive$0$org-telegram-messenger-ImageLoader$6  reason: not valid java name */
            public /* synthetic */ void m1894lambda$onReceive$0$orgtelegrammessengerImageLoader$6() {
                ImageLoader.this.checkMediaPaths();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        filter.addAction("android.intent.action.MEDIA_CHECKING");
        filter.addAction("android.intent.action.MEDIA_EJECT");
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_NOFS");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        filter.addAction("android.intent.action.MEDIA_SHARED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addDataScheme("file");
        try {
            ApplicationLoader.applicationContext.registerReceiver(receiver, filter);
        } catch (Throwable th) {
        }
        checkMediaPaths();
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda5(this));
    }

    /* renamed from: lambda$checkMediaPaths$1$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1881lambda$checkMediaPaths$1$orgtelegrammessengerImageLoader() {
        AndroidUtilities.runOnUIThread(new ImageLoader$$ExternalSyntheticLambda0(createMediaPaths()));
    }

    public void addTestWebFile(String url, WebFile webFile) {
        if (url != null && webFile != null) {
            this.testWebFile.put(url, webFile);
        }
    }

    public void removeTestWebFile(String url) {
        if (url != null) {
            this.testWebFile.remove(url);
        }
    }

    private static void moveDirectory(File source, File target) {
        Stream convert;
        if (!source.exists()) {
            return;
        }
        if (target.exists() || target.mkdir()) {
            try {
                convert = C$r8$wrapper$java$util$stream$Stream$VWRP.convert(Files.list(source.toPath()));
                convert.forEach(new ImageLoader$$ExternalSyntheticLambda4(target));
                if (convert != null) {
                    convert.close();
                    return;
                }
                return;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return;
            } catch (Throwable th) {
            }
        } else {
            return;
        }
        throw th;
    }

    static /* synthetic */ void lambda$moveDirectory$2(File target, Path path) {
        File dest = new File(target, path.getFileName().toString());
        if (Files.isDirectory(path, new LinkOption[0])) {
            moveDirectory(path.toFile(), dest);
            return;
        }
        try {
            Files.move(path, dest.toPath(), new CopyOption[0]);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public SparseArray<File> createMediaPaths() {
        ArrayList<File> dirs;
        SparseArray<File> mediaDirs = new SparseArray<>();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        AndroidUtilities.createEmptyFile(new File(cachePath, ".nomedia"));
        mediaDirs.put(4, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cache path = " + cachePath);
        }
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = Environment.getExternalStorageDirectory();
                if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(SharedConfig.storageCacheDir) && (dirs = AndroidUtilities.getRootDirs()) != null) {
                    int a = 0;
                    int N = dirs.size();
                    while (true) {
                        if (a >= N) {
                            break;
                        }
                        File dir = dirs.get(a);
                        if (dir.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            path = dir;
                            break;
                        }
                        a++;
                    }
                }
                File publicMediaDir = null;
                if (Build.VERSION.SDK_INT >= 30) {
                    try {
                        if (ApplicationLoader.applicationContext.getExternalMediaDirs().length > 0) {
                            publicMediaDir = new File(ApplicationLoader.applicationContext.getExternalMediaDirs()[0], "Telegram");
                            publicMediaDir.mkdirs();
                        }
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                    this.telegramPath = new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), "Telegram");
                } else {
                    this.telegramPath = new File(path, "Telegram");
                }
                this.telegramPath.mkdirs();
                if (Build.VERSION.SDK_INT >= 19 && !this.telegramPath.isDirectory()) {
                    ArrayList<File> dirs2 = AndroidUtilities.getDataDirs();
                    int a2 = 0;
                    int N2 = dirs2.size();
                    while (true) {
                        if (a2 >= N2) {
                            break;
                        }
                        File dir2 = dirs2.get(a2);
                        if (dir2.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            File file = new File(dir2, "Telegram");
                            this.telegramPath = file;
                            file.mkdirs();
                            break;
                        }
                        a2++;
                    }
                }
                if (this.telegramPath.isDirectory()) {
                    try {
                        File imagePath = new File(this.telegramPath, "Telegram Images");
                        imagePath.mkdir();
                        if (imagePath.isDirectory() && canMoveFiles(cachePath, imagePath, 0)) {
                            mediaDirs.put(0, imagePath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + imagePath);
                            }
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                    try {
                        File videoPath = new File(this.telegramPath, "Telegram Video");
                        videoPath.mkdir();
                        if (videoPath.isDirectory() && canMoveFiles(cachePath, videoPath, 2)) {
                            mediaDirs.put(2, videoPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("video path = " + videoPath);
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                    try {
                        File audioPath = new File(this.telegramPath, "Telegram Audio");
                        audioPath.mkdir();
                        if (audioPath.isDirectory() && canMoveFiles(cachePath, audioPath, 1)) {
                            AndroidUtilities.createEmptyFile(new File(audioPath, ".nomedia"));
                            mediaDirs.put(1, audioPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("audio path = " + audioPath);
                            }
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                    try {
                        File documentPath = new File(this.telegramPath, "Telegram Documents");
                        documentPath.mkdir();
                        if (documentPath.isDirectory() && canMoveFiles(cachePath, documentPath, 3)) {
                            AndroidUtilities.createEmptyFile(new File(documentPath, ".nomedia"));
                            mediaDirs.put(3, documentPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("documents path = " + documentPath);
                            }
                        }
                    } catch (Exception e6) {
                        FileLog.e((Throwable) e6);
                    }
                    try {
                        File normalNamesPath = new File(this.telegramPath, "Telegram Files");
                        normalNamesPath.mkdir();
                        if (normalNamesPath.isDirectory() && canMoveFiles(cachePath, normalNamesPath, 5)) {
                            AndroidUtilities.createEmptyFile(new File(normalNamesPath, ".nomedia"));
                            mediaDirs.put(5, normalNamesPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("files path = " + normalNamesPath);
                            }
                        }
                    } catch (Exception e7) {
                        FileLog.e((Throwable) e7);
                    }
                }
                if (publicMediaDir != null && publicMediaDir.isDirectory()) {
                    try {
                        File imagePath2 = new File(publicMediaDir, "Telegram Images");
                        imagePath2.mkdir();
                        if (imagePath2.isDirectory() && canMoveFiles(cachePath, imagePath2, 0)) {
                            mediaDirs.put(100, imagePath2);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + imagePath2);
                            }
                        }
                    } catch (Exception e8) {
                        FileLog.e((Throwable) e8);
                    }
                    try {
                        File videoPath2 = new File(publicMediaDir, "Telegram Video");
                        videoPath2.mkdir();
                        if (videoPath2.isDirectory() && canMoveFiles(cachePath, videoPath2, 2)) {
                            mediaDirs.put(101, videoPath2);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("video path = " + videoPath2);
                            }
                        }
                    } catch (Exception e9) {
                        FileLog.e((Throwable) e9);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e10) {
            FileLog.e((Throwable) e10);
        }
        return mediaDirs;
    }

    private boolean canMoveFiles(File from, File to, int type) {
        RandomAccessFile file = null;
        File srcFile = null;
        File dstFile = null;
        if (type == 0) {
            try {
                srcFile = new File(from, "000000000_999999_temp.f");
                dstFile = new File(to, "000000000_999999.f");
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                if (file == null) {
                    return false;
                }
                file.close();
                return false;
            } catch (Throwable th) {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                throw th;
            }
        } else {
            if (type != 3) {
                if (type != 5) {
                    if (type == 1) {
                        srcFile = new File(from, "000000000_999999_temp.f");
                        dstFile = new File(to, "000000000_999999.f");
                    } else if (type == 2) {
                        srcFile = new File(from, "000000000_999999_temp.f");
                        dstFile = new File(to, "000000000_999999.f");
                    }
                }
            }
            srcFile = new File(from, "000000000_999999_temp.f");
            dstFile = new File(to, "000000000_999999.f");
        }
        srcFile.createNewFile();
        RandomAccessFile file2 = new RandomAccessFile(srcFile, "rws");
        file2.write(new byte[1024]);
        file2.close();
        RandomAccessFile file3 = null;
        boolean canRename = srcFile.renameTo(dstFile);
        srcFile.delete();
        dstFile.delete();
        if (canRename) {
            if (file3 != null) {
                try {
                    file3.close();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            return true;
        } else if (file3 == null) {
            return false;
        } else {
            try {
                file3.close();
                return false;
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
                return false;
            }
        }
    }

    public Float getFileProgress(String location) {
        long[] progress;
        if (location == null || (progress = this.fileProgresses.get(location)) == null) {
            return null;
        }
        if (progress[1] == 0) {
            return Float.valueOf(0.0f);
        }
        return Float.valueOf(Math.min(1.0f, ((float) progress[0]) / ((float) progress[1])));
    }

    public long[] getFileProgressSizes(String location) {
        if (location == null) {
            return null;
        }
        return this.fileProgresses.get(location);
    }

    public String getReplacedKey(String oldKey) {
        if (oldKey == null) {
            return null;
        }
        return this.replacedBitmaps.get(oldKey);
    }

    private void performReplace(String oldKey, String newKey) {
        LruCache<BitmapDrawable> currentCache = this.memCache;
        BitmapDrawable b = currentCache.get(oldKey);
        if (b == null) {
            currentCache = this.smallImagesMemCache;
            b = currentCache.get(oldKey);
        }
        this.replacedBitmaps.put(oldKey, newKey);
        if (b != null) {
            BitmapDrawable oldBitmap = currentCache.get(newKey);
            boolean dontChange = false;
            if (!(oldBitmap == null || oldBitmap.getBitmap() == null || b.getBitmap() == null)) {
                Bitmap oldBitmapObject = oldBitmap.getBitmap();
                Bitmap newBitmapObject = b.getBitmap();
                if (oldBitmapObject.getWidth() > newBitmapObject.getWidth() || oldBitmapObject.getHeight() > newBitmapObject.getHeight()) {
                    dontChange = true;
                }
            }
            if (!dontChange) {
                this.ignoreRemoval = oldKey;
                currentCache.remove(oldKey);
                currentCache.put(newKey, b);
                this.ignoreRemoval = null;
            } else {
                currentCache.remove(oldKey);
            }
        }
        Integer val = this.bitmapUseCounts.get(oldKey);
        if (val != null) {
            this.bitmapUseCounts.put(newKey, val);
            this.bitmapUseCounts.remove(oldKey);
        }
    }

    public void incrementUseCount(String key) {
        Integer count = this.bitmapUseCounts.get(key);
        if (count == null) {
            this.bitmapUseCounts.put(key, 1);
        } else {
            this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String key) {
        Integer count = this.bitmapUseCounts.get(key);
        if (count == null) {
            return true;
        }
        if (count.intValue() == 1) {
            this.bitmapUseCounts.remove(key);
            return true;
        }
        this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() - 1));
        return false;
    }

    public void removeImage(String key) {
        this.bitmapUseCounts.remove(key);
        this.memCache.remove(key);
        this.smallImagesMemCache.remove(key);
    }

    public boolean isInMemCache(String key, boolean animated) {
        if (animated) {
            if (getFromLottieCache(key) != null) {
                return true;
            }
            return false;
        } else if (getFromMemCache(key) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void clearMemory() {
        this.smallImagesMemCache.evictAll();
        this.memCache.evictAll();
        this.lottieMemCache.evictAll();
    }

    private void removeFromWaitingForThumb(int TAG, ImageReceiver imageReceiver) {
        String location = this.waitingForQualityThumbByTag.get(TAG);
        if (location != null) {
            ThumbGenerateInfo info = this.waitingForQualityThumb.get(location);
            if (info != null) {
                int index = info.imageReceiverArray.indexOf(imageReceiver);
                if (index >= 0) {
                    info.imageReceiverArray.remove(index);
                    info.imageReceiverGuidsArray.remove(index);
                }
                if (info.imageReceiverArray.isEmpty()) {
                    this.waitingForQualityThumb.remove(location);
                }
            }
            this.waitingForQualityThumbByTag.remove(TAG);
        }
    }

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, boolean cancelAll) {
        if (imageReceiver != null) {
            ArrayList<Runnable> runnables = imageReceiver.getLoadingOperations();
            if (!runnables.isEmpty()) {
                for (int i = 0; i < runnables.size(); i++) {
                    this.imageLoadQueue.cancelRunnable(runnables.get(i));
                }
                runnables.clear();
            }
            imageReceiver.addLoadingImageRunnable((Runnable) null);
            this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda3(this, cancelAll, imageReceiver));
        }
    }

    /* renamed from: lambda$cancelLoadingForImageReceiver$3$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1880xd4a49e20(boolean cancelAll, ImageReceiver imageReceiver) {
        int type;
        int a = 0;
        while (a < 3) {
            if (a <= 0 || cancelAll) {
                if (a == 0) {
                    type = 1;
                } else if (a == 1) {
                    type = 0;
                } else {
                    type = 3;
                }
                int TAG = imageReceiver.getTag(type);
                if (TAG != 0) {
                    if (a == 0) {
                        removeFromWaitingForThumb(TAG, imageReceiver);
                    }
                    CacheImage ei = this.imageLoadingByTag.get(TAG);
                    if (ei != null) {
                        ei.removeImageReceiver(imageReceiver);
                    }
                }
                a++;
            } else {
                return;
            }
        }
    }

    public BitmapDrawable getImageFromMemory(TLObject fileLocation, String httpUrl, String filter) {
        if (fileLocation == null && httpUrl == null) {
            return null;
        }
        String key = null;
        if (httpUrl != null) {
            key = Utilities.MD5(httpUrl);
        } else if (fileLocation instanceof TLRPC.FileLocation) {
            TLRPC.FileLocation location = (TLRPC.FileLocation) fileLocation;
            key = location.volume_id + "_" + location.local_id;
        } else if (fileLocation instanceof TLRPC.Document) {
            TLRPC.Document location2 = (TLRPC.Document) fileLocation;
            key = location2.dc_id + "_" + location2.id;
        } else if (fileLocation instanceof SecureDocument) {
            SecureDocument location3 = (SecureDocument) fileLocation;
            key = location3.secureFile.dc_id + "_" + location3.secureFile.id;
        } else if (fileLocation instanceof WebFile) {
            key = Utilities.MD5(((WebFile) fileLocation).url);
        }
        if (filter != null) {
            key = key + "@" + filter;
        }
        return getFromMemCache(key);
    }

    /* access modifiers changed from: private */
    /* renamed from: replaceImageInCacheInternal */
    public void m1887lambda$replaceImageInCache$4$orgtelegrammessengerImageLoader(String oldKey, String newKey, ImageLocation newLocation) {
        ArrayList<String> arr;
        String str = oldKey;
        String str2 = newKey;
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                arr = this.memCache.getFilterKeys(str);
            } else {
                arr = this.smallImagesMemCache.getFilterKeys(str);
            }
            if (arr != null) {
                for (int a = 0; a < arr.size(); a++) {
                    String filter = arr.get(a);
                    String oldK = str + "@" + filter;
                    String newK = str2 + "@" + filter;
                    performReplace(oldK, newK);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldK, newK, newLocation);
                }
            } else {
                performReplace(oldKey, newKey);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, newLocation);
            }
        }
    }

    public void replaceImageInCache(String oldKey, String newKey, ImageLocation newLocation, boolean post) {
        if (post) {
            AndroidUtilities.runOnUIThread(new ImageLoader$$ExternalSyntheticLambda13(this, oldKey, newKey, newLocation));
        } else {
            m1887lambda$replaceImageInCache$4$orgtelegrammessengerImageLoader(oldKey, newKey, newLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmap, String key, boolean smallImage) {
        if (smallImage) {
            this.smallImagesMemCache.put(key, bitmap);
        } else {
            this.memCache.put(key, bitmap);
        }
    }

    private void generateThumb(int mediaType, File originalPath, ThumbGenerateInfo info) {
        if ((mediaType == 0 || mediaType == 2 || mediaType == 3) && originalPath != null && info != null) {
            if (this.thumbGenerateTasks.get(FileLoader.getAttachFileName(info.parentDocument)) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(mediaType, originalPath, info));
            }
        }
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        String key;
        if (imageReceiver != null && (key = imageReceiver.getImageKey()) != null) {
            this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda8(this, key));
        }
    }

    /* renamed from: lambda$cancelForceLoadingForImageReceiver$5$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1879xa371a69b(String key) {
        this.forceLoadingImages.remove(key);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String key, String url, String ext, ImageLocation imageLocation, String filter, long size, int cacheType, int type, int thumb, int guid) {
        int TAG;
        ImageReceiver imageReceiver2 = imageReceiver;
        int i = type;
        if (imageReceiver2 == null || url == null || key == null) {
            ImageReceiver imageReceiver3 = imageReceiver2;
        } else if (imageLocation == null) {
            ImageReceiver imageReceiver4 = imageReceiver2;
        } else {
            int TAG2 = imageReceiver2.getTag(i);
            if (TAG2 == 0) {
                int i2 = this.lastImageNum;
                int TAG3 = i2;
                imageReceiver2.setTag(i2, i);
                int i3 = this.lastImageNum + 1;
                this.lastImageNum = i3;
                if (i3 == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
                TAG = TAG3;
            } else {
                TAG = TAG2;
            }
            int finalTag = TAG;
            boolean finalIsNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            Object parentObject = imageReceiver.getParentObject();
            TLRPC.Document qualityDocument = imageReceiver.getQualityThumbDocument();
            boolean shouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            Runnable loadOperationRunnable = new ImageLoader$$ExternalSyntheticLambda6(this, thumb, url, key, finalTag, imageReceiver, guid, filter, type, imageLocation, i == 0 && imageReceiver.isCurrentKeyQuality(), parentObject, imageReceiver.getCurrentAccount(), qualityDocument, finalIsNeedsQualityThumb, shouldGenerateQualityThumb, ext, cacheType, size);
            this.imageLoadQueue.postRunnable(loadOperationRunnable);
            imageReceiver.addLoadingImageRunnable(loadOperationRunnable);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:203:0x0493  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x04a2  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0594  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x059a  */
    /* renamed from: lambda$createLoadOperationForImageReceiver$6$org-telegram-messenger-ImageLoader  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1882xaef5b73a(int r33, java.lang.String r34, java.lang.String r35, int r36, org.telegram.messenger.ImageReceiver r37, int r38, java.lang.String r39, int r40, org.telegram.messenger.ImageLocation r41, boolean r42, java.lang.Object r43, int r44, org.telegram.tgnet.TLRPC.Document r45, boolean r46, boolean r47, java.lang.String r48, int r49, long r50) {
        /*
            r32 = this;
            r0 = r32
            r1 = r33
            r2 = r34
            r9 = r35
            r10 = r36
            r11 = r37
            r12 = r39
            r13 = r41
            r14 = r43
            r15 = r48
            r8 = r49
            r6 = r50
            r16 = 0
            r5 = 2
            if (r1 == r5) goto L_0x00a2
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            java.lang.Object r3 = r3.get(r2)
            r4 = r3
            org.telegram.messenger.ImageLoader$CacheImage r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByKeys
            java.lang.Object r3 = r3.get(r9)
            org.telegram.messenger.ImageLoader$CacheImage r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3
            android.util.SparseArray<org.telegram.messenger.ImageLoader$CacheImage> r5 = r0.imageLoadingByTag
            java.lang.Object r5 = r5.get(r10)
            org.telegram.messenger.ImageLoader$CacheImage r5 = (org.telegram.messenger.ImageLoader.CacheImage) r5
            if (r5 == 0) goto L_0x0072
            if (r5 != r3) goto L_0x0047
            r9 = r38
            r5.setImageReceiverGuid(r11, r9)
            r16 = 1
            r17 = r3
            r18 = r4
            r9 = r5
            goto L_0x0077
        L_0x0047:
            r9 = r38
            if (r5 != r4) goto L_0x0069
            if (r3 != 0) goto L_0x0061
            r17 = r3
            r3 = r5
            r18 = r4
            r4 = r37
            r9 = r5
            r5 = r35
            r6 = r39
            r7 = r40
            r8 = r38
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x0066
        L_0x0061:
            r17 = r3
            r18 = r4
            r9 = r5
        L_0x0066:
            r16 = 1
            goto L_0x0077
        L_0x0069:
            r17 = r3
            r18 = r4
            r9 = r5
            r9.removeImageReceiver(r11)
            goto L_0x0077
        L_0x0072:
            r17 = r3
            r18 = r4
            r9 = r5
        L_0x0077:
            if (r16 != 0) goto L_0x008d
            if (r17 == 0) goto L_0x008d
            r3 = r17
            r4 = r37
            r5 = r35
            r6 = r39
            r7 = r40
            r8 = r38
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r3 = 1
            r16 = r3
        L_0x008d:
            if (r16 != 0) goto L_0x00a2
            if (r18 == 0) goto L_0x00a2
            r3 = r18
            r4 = r37
            r5 = r35
            r6 = r39
            r7 = r40
            r8 = r38
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r16 = 1
        L_0x00a2:
            if (r16 != 0) goto L_0x0722
            r3 = 0
            r9 = 0
            r4 = 0
            r5 = 0
            java.lang.String r6 = r13.path
            java.lang.String r8 = "athumb"
            java.lang.String r7 = "_"
            r18 = 4
            r19 = r9
            if (r6 == 0) goto L_0x0123
            java.lang.String r6 = r13.path
            java.lang.String r9 = "http"
            boolean r9 = r6.startsWith(r9)
            if (r9 != 0) goto L_0x0119
            boolean r9 = r6.startsWith(r8)
            if (r9 != 0) goto L_0x0119
            r3 = 1
            java.lang.String r9 = "thumb://"
            boolean r9 = r6.startsWith(r9)
            r20 = r3
            java.lang.String r3 = ":"
            if (r9 == 0) goto L_0x00ed
            r9 = 8
            int r3 = r6.indexOf(r3, r9)
            if (r3 < 0) goto L_0x00e8
            java.io.File r9 = new java.io.File
            r21 = r4
            int r4 = r3 + 1
            java.lang.String r4 = r6.substring(r4)
            r9.<init>(r4)
            r4 = r9
            goto L_0x00ea
        L_0x00e8:
            r21 = r4
        L_0x00ea:
            r3 = r20
            goto L_0x011d
        L_0x00ed:
            r21 = r4
            java.lang.String r4 = "vthumb://"
            boolean r4 = r6.startsWith(r4)
            if (r4 == 0) goto L_0x0110
            r4 = 9
            int r3 = r6.indexOf(r3, r4)
            if (r3 < 0) goto L_0x010b
            java.io.File r4 = new java.io.File
            int r9 = r3 + 1
            java.lang.String r9 = r6.substring(r9)
            r4.<init>(r9)
            goto L_0x010d
        L_0x010b:
            r4 = r21
        L_0x010d:
            r3 = r20
            goto L_0x011d
        L_0x0110:
            java.io.File r3 = new java.io.File
            r3.<init>(r6)
            r4 = r3
            r3 = r20
            goto L_0x011d
        L_0x0119:
            r21 = r4
            r4 = r21
        L_0x011d:
            r23 = r8
            r15 = r10
            r14 = r11
            goto L_0x023d
        L_0x0123:
            r21 = r4
            if (r1 != 0) goto L_0x0233
            if (r42 == 0) goto L_0x0233
            r3 = 1
            boolean r4 = r14 instanceof org.telegram.messenger.MessageObject
            if (r4 == 0) goto L_0x014d
            r4 = r14
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Document r6 = r4.getDocument()
            org.telegram.tgnet.TLRPC$Message r9 = r4.messageOwner
            java.lang.String r9 = r9.attachPath
            r20 = r3
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r44)
            r22 = r5
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.io.File r3 = r3.getPathToMessage(r5)
            int r5 = r4.getMediaType()
            r4 = 0
            goto L_0x0171
        L_0x014d:
            r20 = r3
            r22 = r5
            if (r45 == 0) goto L_0x016c
            r6 = r45
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r44)
            r4 = 1
            java.io.File r3 = r3.getPathToAttach(r6, r4)
            boolean r4 = org.telegram.messenger.MessageObject.isVideoDocument(r6)
            if (r4 == 0) goto L_0x0167
            r4 = 2
            r5 = r4
            goto L_0x0169
        L_0x0167:
            r4 = 3
            r5 = r4
        L_0x0169:
            r9 = 0
            r4 = 1
            goto L_0x0171
        L_0x016c:
            r6 = 0
            r9 = 0
            r3 = 0
            r5 = 0
            r4 = 0
        L_0x0171:
            if (r6 == 0) goto L_0x0228
            if (r46 == 0) goto L_0x01ad
            r23 = r8
            java.io.File r8 = new java.io.File
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r18)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r15 = "q_"
            r2.append(r15)
            int r15 = r6.dc_id
            r2.append(r15)
            r2.append(r7)
            long r10 = r6.id
            r2.append(r10)
            java.lang.String r10 = ".jpg"
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r8.<init>(r14, r2)
            r2 = r8
            boolean r8 = r2.exists()
            if (r8 != 0) goto L_0x01a9
            r2 = 0
            goto L_0x01b1
        L_0x01a9:
            r8 = 1
            r22 = r8
            goto L_0x01b1
        L_0x01ad:
            r23 = r8
            r2 = r21
        L_0x01b1:
            r8 = 0
            boolean r10 = android.text.TextUtils.isEmpty(r9)
            if (r10 != 0) goto L_0x01c5
            java.io.File r10 = new java.io.File
            r10.<init>(r9)
            r8 = r10
            boolean r10 = r8.exists()
            if (r10 != 0) goto L_0x01c5
            r8 = 0
        L_0x01c5:
            if (r8 != 0) goto L_0x01c8
            r8 = r3
        L_0x01c8:
            if (r2 != 0) goto L_0x021e
            java.lang.String r7 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r10 = r0.waitingForQualityThumb
            java.lang.Object r10 = r10.get(r7)
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r10 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r10
            if (r10 != 0) goto L_0x01ed
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r11 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo
            r14 = 0
            r11.<init>()
            r10 = r11
            org.telegram.tgnet.TLRPC.Document unused = r10.parentDocument = r6
            java.lang.String unused = r10.filter = r12
            boolean unused = r10.big = r4
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r11 = r0.waitingForQualityThumb
            r11.put(r7, r10)
        L_0x01ed:
            java.util.ArrayList r11 = r10.imageReceiverArray
            r14 = r37
            boolean r11 = r11.contains(r14)
            if (r11 != 0) goto L_0x020b
            java.util.ArrayList r11 = r10.imageReceiverArray
            r11.add(r14)
            java.util.ArrayList r11 = r10.imageReceiverGuidsArray
            java.lang.Integer r15 = java.lang.Integer.valueOf(r38)
            r11.add(r15)
        L_0x020b:
            android.util.SparseArray<java.lang.String> r11 = r0.waitingForQualityThumbByTag
            r15 = r36
            r11.put(r15, r7)
            boolean r11 = r8.exists()
            if (r11 == 0) goto L_0x021d
            if (r47 == 0) goto L_0x021d
            r0.generateThumb(r5, r8, r10)
        L_0x021d:
            return
        L_0x021e:
            r15 = r36
            r14 = r37
            r4 = r2
            r3 = r20
            r5 = r22
            goto L_0x023d
        L_0x0228:
            r23 = r8
            r15 = r10
            r14 = r11
            r3 = r20
            r4 = r21
            r5 = r22
            goto L_0x023d
        L_0x0233:
            r22 = r5
            r23 = r8
            r15 = r10
            r14 = r11
            r4 = r21
            r5 = r22
        L_0x023d:
            r2 = 2
            if (r1 == r2) goto L_0x0712
            boolean r9 = r41.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r6 = new org.telegram.messenger.ImageLoader$CacheImage
            r8 = 0
            r6.<init>()
            r10 = r6
            if (r42 != 0) goto L_0x02cb
            int r6 = r13.imageType
            if (r6 == r2) goto L_0x02c5
            org.telegram.messenger.WebFile r2 = r13.webFile
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r2)
            if (r2 != 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r2 != 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r2)
            if (r2 != 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            boolean r2 = org.telegram.messenger.MessageObject.isVideoSticker(r2)
            if (r2 == 0) goto L_0x0274
            r11 = r48
            goto L_0x02c7
        L_0x0274:
            java.lang.String r2 = r13.path
            if (r2 == 0) goto L_0x02c2
            java.lang.String r2 = r13.path
            java.lang.String r6 = "vthumb"
            boolean r6 = r2.startsWith(r6)
            if (r6 != 0) goto L_0x02bf
            java.lang.String r6 = "thumb"
            boolean r6 = r2.startsWith(r6)
            if (r6 != 0) goto L_0x02bf
            java.lang.String r6 = "jpg"
            java.lang.String r6 = getHttpUrlExtension(r2, r6)
            java.lang.String r8 = "webm"
            boolean r8 = r6.equals(r8)
            if (r8 != 0) goto L_0x02b9
            java.lang.String r8 = "mp4"
            boolean r8 = r6.equals(r8)
            if (r8 != 0) goto L_0x02b9
            java.lang.String r8 = "gif"
            boolean r8 = r6.equals(r8)
            if (r8 == 0) goto L_0x02ab
            r11 = r48
            goto L_0x02bb
        L_0x02ab:
            java.lang.String r8 = "tgs"
            r11 = r48
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x02cd
            r8 = 1
            r10.imageType = r8
            goto L_0x02cd
        L_0x02b9:
            r11 = r48
        L_0x02bb:
            r8 = 2
            r10.imageType = r8
            goto L_0x02cd
        L_0x02bf:
            r11 = r48
            goto L_0x02cd
        L_0x02c2:
            r11 = r48
            goto L_0x02cd
        L_0x02c5:
            r11 = r48
        L_0x02c7:
            r2 = 2
            r10.imageType = r2
            goto L_0x02cd
        L_0x02cb:
            r11 = r48
        L_0x02cd:
            r20 = 0
            if (r4 != 0) goto L_0x05a6
            r24 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r13.photoSize
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            java.lang.String r8 = "g"
            if (r6 != 0) goto L_0x0570
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r13.photoSize
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoPathSize
            if (r6 == 0) goto L_0x02f2
            r2 = r34
            r6 = r49
            r22 = r3
            r26 = r4
            r27 = r5
            r4 = r8
            r31 = r9
            r9 = r0
            r0 = 1
            goto L_0x057f
        L_0x02f2:
            org.telegram.messenger.SecureDocument r6 = r13.secureDocument
            if (r6 == 0) goto L_0x0320
            org.telegram.messenger.SecureDocument r6 = r13.secureDocument
            r10.secureDocument = r6
            org.telegram.messenger.SecureDocument r6 = r10.secureDocument
            org.telegram.tgnet.TLRPC$TL_secureFile r6 = r6.secureFile
            int r6 = r6.dc_id
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r6 != r7) goto L_0x0306
            r6 = 1
            goto L_0x0307
        L_0x0306:
            r6 = 0
        L_0x0307:
            r3 = r6
            java.io.File r6 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r2 = r34
            r6.<init>(r7, r2)
            r4 = r6
            r6 = r49
            r7 = r4
            r4 = r8
            r31 = r9
            r14 = r24
            r9 = r0
            r0 = 1
            goto L_0x0587
        L_0x0320:
            r2 = r34
            boolean r6 = r8.equals(r12)
            r22 = r3
            java.lang.String r3 = ".svg"
            r26 = r4
            java.lang.String r4 = "application/x-tgwallpattern"
            r27 = r5
            java.lang.String r5 = "application/x-tgsticker"
            java.lang.String r14 = "application/x-tgsdice"
            if (r6 != 0) goto L_0x0409
            boolean r6 = r0.isAnimatedAvatar(r12)
            if (r6 != 0) goto L_0x0409
            r6 = r49
            if (r6 != 0) goto L_0x0355
            r29 = r7
            r28 = r8
            r7 = r50
            int r30 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r30 <= 0) goto L_0x0359
            java.lang.String r15 = r13.path
            if (r15 != 0) goto L_0x0359
            if (r9 == 0) goto L_0x0351
            goto L_0x0359
        L_0x0351:
            r31 = r9
            goto L_0x0411
        L_0x0355:
            r28 = r8
            r7 = r50
        L_0x0359:
            java.io.File r15 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r15.<init>(r7, r2)
            r7 = r15
            boolean r8 = r7.exists()
            if (r8 == 0) goto L_0x036f
            r8 = 1
            r27 = r8
            r31 = r9
            goto L_0x0396
        L_0x036f:
            r8 = 2
            if (r6 != r8) goto L_0x0392
            java.io.File r8 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r26 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            r31 = r9
            java.lang.String r9 = ".enc"
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r8.<init>(r15, r7)
            r7 = r8
            goto L_0x0396
        L_0x0392:
            r26 = r7
            r31 = r9
        L_0x0396:
            org.telegram.tgnet.TLRPC$Document r8 = r13.document
            if (r8 == 0) goto L_0x03fd
            org.telegram.tgnet.TLRPC$Document r8 = r13.document
            boolean r8 = r8 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r8 == 0) goto L_0x03ba
            org.telegram.tgnet.TLRPC$Document r3 = r13.document
            org.telegram.messenger.DocumentObject$ThemeDocument r3 = (org.telegram.messenger.DocumentObject.ThemeDocument) r3
            org.telegram.tgnet.TLRPC$Document r4 = r3.wallpaper
            if (r4 != 0) goto L_0x03aa
            r4 = 1
            goto L_0x03ac
        L_0x03aa:
            r4 = r22
        L_0x03ac:
            r5 = 5
            r10.imageType = r5
            r9 = r0
            r3 = r4
            r14 = r24
            r5 = r27
            r4 = r28
            r0 = 1
            goto L_0x0587
        L_0x03ba:
            org.telegram.tgnet.TLRPC$Document r8 = r13.document
            java.lang.String r8 = r8.mime_type
            boolean r8 = r14.equals(r8)
            if (r8 == 0) goto L_0x03d2
            r8 = 1
            r10.imageType = r8
            r3 = 1
            r9 = r0
            r14 = r24
            r5 = r27
            r4 = r28
            r0 = 1
            goto L_0x0587
        L_0x03d2:
            r8 = 1
            org.telegram.tgnet.TLRPC$Document r9 = r13.document
            java.lang.String r9 = r9.mime_type
            boolean r5 = r5.equals(r9)
            if (r5 == 0) goto L_0x03e0
            r10.imageType = r8
            goto L_0x03fd
        L_0x03e0:
            org.telegram.tgnet.TLRPC$Document r5 = r13.document
            java.lang.String r5 = r5.mime_type
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x03ee
            r4 = 3
            r10.imageType = r4
            goto L_0x03fd
        L_0x03ee:
            r4 = 3
            org.telegram.tgnet.TLRPC$Document r5 = r13.document
            java.lang.String r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r5)
            boolean r3 = r5.endsWith(r3)
            if (r3 == 0) goto L_0x03fd
            r10.imageType = r4
        L_0x03fd:
            r9 = r0
            r3 = r22
            r14 = r24
            r5 = r27
            r4 = r28
            r0 = 1
            goto L_0x0587
        L_0x0409:
            r6 = r49
            r29 = r7
            r28 = r8
            r31 = r9
        L_0x0411:
            org.telegram.tgnet.TLRPC$Document r7 = r13.document
            java.lang.String r8 = ".temp"
            if (r7 == 0) goto L_0x04e7
            org.telegram.tgnet.TLRPC$Document r7 = r13.document
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted
            if (r9 == 0) goto L_0x0427
            java.io.File r9 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r9.<init>(r15, r2)
            goto L_0x0443
        L_0x0427:
            boolean r9 = org.telegram.messenger.MessageObject.isVideoDocument(r7)
            if (r9 == 0) goto L_0x0438
            java.io.File r9 = new java.io.File
            r15 = 2
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r15)
            r9.<init>(r1, r2)
            goto L_0x0443
        L_0x0438:
            java.io.File r1 = new java.io.File
            r9 = 3
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r9)
            r1.<init>(r15, r2)
            r9 = r1
        L_0x0443:
            boolean r1 = r0.isAnimatedAvatar(r12)
            if (r1 != 0) goto L_0x0457
            r1 = r28
            boolean r15 = r1.equals(r12)
            if (r15 == 0) goto L_0x0452
            goto L_0x0459
        L_0x0452:
            r28 = r1
            r26 = r9
            goto L_0x048d
        L_0x0457:
            r1 = r28
        L_0x0459:
            boolean r15 = r9.exists()
            if (r15 != 0) goto L_0x0489
            java.io.File r15 = new java.io.File
            r26 = r9
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r18)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r28 = r1
            int r1 = r7.dc_id
            r11.append(r1)
            r1 = r29
            r11.append(r1)
            long r0 = r7.id
            r11.append(r0)
            r11.append(r8)
            java.lang.String r0 = r11.toString()
            r15.<init>(r9, r0)
            r9 = r15
            goto L_0x048f
        L_0x0489:
            r28 = r1
            r26 = r9
        L_0x048d:
            r9 = r26
        L_0x048f:
            boolean r0 = r7 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r0 == 0) goto L_0x04a2
            r0 = r7
            org.telegram.messenger.DocumentObject$ThemeDocument r0 = (org.telegram.messenger.DocumentObject.ThemeDocument) r0
            org.telegram.tgnet.TLRPC$Document r1 = r0.wallpaper
            if (r1 != 0) goto L_0x049c
            r3 = 1
            goto L_0x049e
        L_0x049c:
            r3 = r22
        L_0x049e:
            r1 = 5
            r10.imageType = r1
            goto L_0x04da
        L_0x04a2:
            org.telegram.tgnet.TLRPC$Document r0 = r13.document
            java.lang.String r0 = r0.mime_type
            boolean r0 = r14.equals(r0)
            if (r0 == 0) goto L_0x04b1
            r0 = 1
            r10.imageType = r0
            r3 = 1
            goto L_0x04da
        L_0x04b1:
            r0 = 1
            java.lang.String r1 = r7.mime_type
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x04bd
            r10.imageType = r0
            goto L_0x04d8
        L_0x04bd:
            java.lang.String r0 = r7.mime_type
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x04c9
            r0 = 3
            r10.imageType = r0
            goto L_0x04d8
        L_0x04c9:
            r0 = 3
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getDocumentFileName(r1)
            boolean r3 = r1.endsWith(r3)
            if (r3 == 0) goto L_0x04d8
            r10.imageType = r0
        L_0x04d8:
            r3 = r22
        L_0x04da:
            long r0 = r7.size
            r14 = r0
            r7 = r9
            r5 = r27
            r4 = r28
            r0 = 1
            r9 = r32
            goto L_0x0587
        L_0x04e7:
            r1 = r29
            org.telegram.messenger.WebFile r0 = r13.webFile
            if (r0 == 0) goto L_0x0505
            java.io.File r0 = new java.io.File
            r1 = 3
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r0.<init>(r1, r2)
            r9 = r32
            r7 = r0
            r3 = r22
            r14 = r24
            r5 = r27
            r4 = r28
            r0 = 1
            goto L_0x0587
        L_0x0505:
            r0 = 1
            if (r6 != r0) goto L_0x0512
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r3.<init>(r4, r2)
            goto L_0x051c
        L_0x0512:
            java.io.File r3 = new java.io.File
            r4 = 0
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r5, r2)
        L_0x051c:
            r9 = r32
            boolean r4 = r9.isAnimatedAvatar(r12)
            if (r4 != 0) goto L_0x053f
            r4 = r28
            boolean r5 = r4.equals(r12)
            if (r5 == 0) goto L_0x0537
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r5 = r13.location
            if (r5 == 0) goto L_0x0537
            boolean r5 = r3.exists()
            if (r5 != 0) goto L_0x0537
            goto L_0x0541
        L_0x0537:
            r7 = r3
            r3 = r22
            r14 = r24
            r5 = r27
            goto L_0x0587
        L_0x053f:
            r4 = r28
        L_0x0541:
            java.io.File r5 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r18)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r14 = r13.location
            long r14 = r14.volume_id
            r11.append(r14)
            r11.append(r1)
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r13.location
            int r1 = r1.local_id
            r11.append(r1)
            r11.append(r8)
            java.lang.String r1 = r11.toString()
            r5.<init>(r7, r1)
            r1 = r5
            r7 = r1
            r3 = r22
            r14 = r24
            r5 = r27
            goto L_0x0587
        L_0x0570:
            r2 = r34
            r6 = r49
            r22 = r3
            r26 = r4
            r27 = r5
            r4 = r8
            r31 = r9
            r9 = r0
            r0 = 1
        L_0x057f:
            r1 = 1
            r3 = r1
            r14 = r24
            r7 = r26
            r5 = r27
        L_0x0587:
            boolean r1 = r4.equals(r12)
            if (r1 != 0) goto L_0x059a
            boolean r1 = r9.isAnimatedAvatar(r12)
            if (r1 == 0) goto L_0x0594
            goto L_0x059a
        L_0x0594:
            r22 = r3
            r27 = r5
            r1 = r7
            goto L_0x05b6
        L_0x059a:
            r1 = 2
            r10.imageType = r1
            r10.size = r14
            r3 = 1
            r22 = r3
            r27 = r5
            r1 = r7
            goto L_0x05b6
        L_0x05a6:
            r2 = r34
            r6 = r49
            r22 = r3
            r26 = r4
            r27 = r5
            r31 = r9
            r9 = r0
            r0 = 1
            r1 = r26
        L_0x05b6:
            r11 = r40
            r10.type = r11
            r14 = r35
            r10.key = r14
            r10.filter = r12
            r10.imageLocation = r13
            r15 = r48
            r10.ext = r15
            r8 = r44
            r10.currentAccount = r8
            r7 = r43
            r10.parentObject = r7
            int r3 = r13.imageType
            if (r3 == 0) goto L_0x05d6
            int r3 = r13.imageType
            r10.imageType = r3
        L_0x05d6:
            r5 = 2
            if (r6 != r5) goto L_0x05f5
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            java.lang.String r5 = ".enc.key"
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            r3.<init>(r4, r0)
            r10.encryptionKeyPath = r3
        L_0x05f5:
            r3 = r10
            r4 = r37
            r0 = 2
            r5 = r35
            r0 = r6
            r6 = r39
            r14 = r50
            r11 = r7
            r7 = r40
            r12 = r23
            r8 = r38
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r22 != 0) goto L_0x06ee
            if (r27 != 0) goto L_0x06ee
            boolean r3 = r1.exists()
            if (r3 == 0) goto L_0x0616
            goto L_0x06ee
        L_0x0616:
            r10.url = r2
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r9.imageLoadingByUrl
            r3.put(r2, r10)
            java.lang.String r3 = r13.path
            if (r3 == 0) goto L_0x0676
            java.lang.String r3 = r13.path
            java.lang.String r3 = org.telegram.messenger.Utilities.MD5(r3)
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r18)
            java.io.File r5 = new java.io.File
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            java.lang.String r7 = "_temp.jpg"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r4, r6)
            r10.tempFilePath = r5
            r10.finalFilePath = r1
            java.lang.String r5 = r13.path
            boolean r5 = r5.startsWith(r12)
            if (r5 == 0) goto L_0x0660
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r5 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r5.<init>(r10)
            r10.artworkTask = r5
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r5 = r9.artworkTasks
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r6 = r10.artworkTask
            r5.add(r6)
            r5 = 0
            r9.runArtworkTasks(r5)
            goto L_0x0672
        L_0x0660:
            r5 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r6 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r6.<init>(r10, r14)
            r10.httpTask = r6
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r6 = r9.httpTasks
            org.telegram.messenger.ImageLoader$HttpImageTask r7 = r10.httpTask
            r6.add(r7)
            r9.runHttpTasks(r5)
        L_0x0672:
            r4 = r35
            goto L_0x0728
        L_0x0676:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r13.location
            if (r3 == 0) goto L_0x069e
            r3 = r49
            if (r3 != 0) goto L_0x0689
            int r4 = (r14 > r20 ? 1 : (r14 == r20 ? 0 : -1))
            if (r4 <= 0) goto L_0x0686
            byte[] r4 = r13.key
            if (r4 == 0) goto L_0x0689
        L_0x0686:
            r3 = 1
            r12 = r3
            goto L_0x068a
        L_0x0689:
            r12 = r3
        L_0x068a:
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r44)
            if (r33 == 0) goto L_0x0692
            r7 = 2
            goto L_0x0693
        L_0x0692:
            r7 = 1
        L_0x0693:
            r4 = r41
            r5 = r43
            r6 = r48
            r8 = r12
            r3.loadFile(r4, r5, r6, r7, r8)
            goto L_0x06d6
        L_0x069e:
            org.telegram.tgnet.TLRPC$Document r3 = r13.document
            if (r3 == 0) goto L_0x06b1
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r44)
            org.telegram.tgnet.TLRPC$Document r4 = r13.document
            if (r33 == 0) goto L_0x06ac
            r5 = 2
            goto L_0x06ad
        L_0x06ac:
            r5 = 1
        L_0x06ad:
            r3.loadFile(r4, r11, r5, r0)
            goto L_0x06d6
        L_0x06b1:
            org.telegram.messenger.SecureDocument r3 = r13.secureDocument
            if (r3 == 0) goto L_0x06c4
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r44)
            org.telegram.messenger.SecureDocument r4 = r13.secureDocument
            if (r33 == 0) goto L_0x06bf
            r5 = 2
            goto L_0x06c0
        L_0x06bf:
            r5 = 1
        L_0x06c0:
            r3.loadFile(r4, r5)
            goto L_0x06d6
        L_0x06c4:
            org.telegram.messenger.WebFile r3 = r13.webFile
            if (r3 == 0) goto L_0x06d6
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r44)
            org.telegram.messenger.WebFile r4 = r13.webFile
            if (r33 == 0) goto L_0x06d2
            r5 = 2
            goto L_0x06d3
        L_0x06d2:
            r5 = 1
        L_0x06d3:
            r3.loadFile(r4, r5, r0)
        L_0x06d6:
            boolean r3 = r37.isForceLoding()
            if (r3 == 0) goto L_0x06eb
            java.util.HashMap<java.lang.String, java.lang.Integer> r3 = r9.forceLoadingImages
            java.lang.String r4 = r10.key
            r5 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3.put(r4, r5)
            r4 = r35
            goto L_0x0728
        L_0x06eb:
            r4 = r35
            goto L_0x0728
        L_0x06ee:
            r10.finalFilePath = r1
            r10.imageLocation = r13
            org.telegram.messenger.ImageLoader$CacheOutTask r3 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r3.<init>(r10)
            r10.cacheTask = r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r9.imageLoadingByKeys
            r4 = r35
            r3.put(r4, r10)
            if (r33 == 0) goto L_0x070a
            org.telegram.messenger.DispatchQueue r3 = r9.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r5 = r10.cacheTask
            r3.postRunnable(r5)
            goto L_0x0728
        L_0x070a:
            org.telegram.messenger.DispatchQueue r3 = r9.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r5 = r10.cacheTask
            r3.postRunnable(r5)
            goto L_0x0728
        L_0x0712:
            r2 = r34
            r11 = r43
            r9 = r0
            r22 = r3
            r26 = r4
            r27 = r5
            r4 = r35
            r0 = r49
            goto L_0x0728
        L_0x0722:
            r4 = r35
            r9 = r0
            r11 = r14
            r0 = r49
        L_0x0728:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.m1882xaef5b73a(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, int, org.telegram.tgnet.TLRPC$Document, boolean, boolean, java.lang.String, int, long):void");
    }

    public void preloadArtwork(String athumbUrl) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda11(this, athumbUrl));
    }

    /* renamed from: lambda$preloadArtwork$7$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1886lambda$preloadArtwork$7$orgtelegrammessengerImageLoader(String athumbUrl) {
        String ext = getHttpUrlExtension(athumbUrl, "jpg");
        String url = Utilities.MD5(athumbUrl) + "." + ext;
        File cacheFile = new File(FileLoader.getDirectory(4), url);
        if (!cacheFile.exists()) {
            ImageLocation imageLocation = ImageLocation.getForPath(athumbUrl);
            CacheImage img = new CacheImage();
            img.type = 1;
            img.key = Utilities.MD5(athumbUrl);
            img.filter = null;
            img.imageLocation = imageLocation;
            img.ext = ext;
            img.parentObject = null;
            if (imageLocation.imageType != 0) {
                img.imageType = imageLocation.imageType;
            }
            img.url = url;
            this.imageLoadingByUrl.put(url, img);
            String file = Utilities.MD5(imageLocation.path);
            img.tempFilePath = new File(FileLoader.getDirectory(4), file + "_temp.jpg");
            img.finalFilePath = cacheFile;
            img.artworkTask = new ArtworkLoadTask(img);
            this.artworkTasks.add(img.artworkTask);
            runArtworkTasks(false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01c9  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01cd  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01d2  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01d6  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x038f  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0419 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x044a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x047f  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0487  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x04c7  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0181  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0190  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01b2  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01bb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r49) {
        /*
            r48 = this;
            r15 = r48
            r14 = r49
            if (r14 != 0) goto L_0x0007
            return
        L_0x0007:
            r6 = 0
            java.lang.String r7 = r49.getMediaKey()
            int r23 = r49.getNewGuid()
            r8 = 1
            if (r7 == 0) goto L_0x0071
            org.telegram.messenger.ImageLocation r9 = r49.getMediaLocation()
            boolean r0 = r15.useLottieMemCache(r9, r7)
            if (r0 == 0) goto L_0x0023
            android.graphics.drawable.BitmapDrawable r0 = r15.getFromLottieCache(r7)
            r10 = r0
            goto L_0x0059
        L_0x0023:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r15.memCache
            java.lang.Object r0 = r0.get(r7)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0032
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r7)
        L_0x0032:
            if (r0 != 0) goto L_0x0044
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            java.lang.Object r1 = r1.get(r7)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0044
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            r1.moveToFront(r7)
        L_0x0044:
            if (r0 != 0) goto L_0x0058
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            java.lang.Object r1 = r1.get(r7)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0056
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            r1.moveToFront(r7)
        L_0x0056:
            r10 = r0
            goto L_0x0059
        L_0x0058:
            r10 = r0
        L_0x0059:
            if (r10 == 0) goto L_0x0071
            r15.cancelLoadingForImageReceiver(r14, r8)
            r3 = 3
            r4 = 1
            r0 = r49
            r1 = r10
            r2 = r7
            r5 = r23
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r6 = 1
            boolean r0 = r49.isForcePreview()
            if (r0 != 0) goto L_0x0071
            return
        L_0x0071:
            java.lang.String r9 = r49.getImageKey()
            if (r6 != 0) goto L_0x00dd
            if (r9 == 0) goto L_0x00dd
            org.telegram.messenger.ImageLocation r10 = r49.getImageLocation()
            r0 = 0
            boolean r1 = r15.useLottieMemCache(r10, r9)
            if (r1 == 0) goto L_0x0088
            android.graphics.drawable.BitmapDrawable r0 = r15.getFromLottieCache(r9)
        L_0x0088:
            if (r0 != 0) goto L_0x00c2
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            java.lang.Object r1 = r1.get(r9)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x009a
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r9)
        L_0x009a:
            if (r0 != 0) goto L_0x00ac
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            java.lang.Object r1 = r1.get(r9)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x00ac
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            r1.moveToFront(r9)
        L_0x00ac:
            if (r0 != 0) goto L_0x00c0
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            java.lang.Object r1 = r1.get(r9)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x00be
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            r1.moveToFront(r9)
        L_0x00be:
            r11 = r0
            goto L_0x00c3
        L_0x00c0:
            r11 = r0
            goto L_0x00c3
        L_0x00c2:
            r11 = r0
        L_0x00c3:
            if (r11 == 0) goto L_0x00dd
            r15.cancelLoadingForImageReceiver(r14, r8)
            r3 = 0
            r4 = 1
            r0 = r49
            r1 = r11
            r2 = r9
            r5 = r23
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r6 = 1
            boolean r0 = r49.isForcePreview()
            if (r0 != 0) goto L_0x00dd
            if (r7 != 0) goto L_0x00dd
            return
        L_0x00dd:
            r24 = r6
            r6 = 0
            java.lang.String r10 = r49.getThumbKey()
            r11 = 0
            if (r10 == 0) goto L_0x014a
            org.telegram.messenger.ImageLocation r12 = r49.getThumbLocation()
            boolean r0 = r15.useLottieMemCache(r12, r10)
            if (r0 == 0) goto L_0x00f7
            android.graphics.drawable.BitmapDrawable r0 = r15.getFromLottieCache(r10)
            r13 = r0
            goto L_0x012d
        L_0x00f7:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r15.memCache
            java.lang.Object r0 = r0.get(r10)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0106
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r10)
        L_0x0106:
            if (r0 != 0) goto L_0x0118
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            java.lang.Object r1 = r1.get(r10)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0118
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            r1.moveToFront(r10)
        L_0x0118:
            if (r0 != 0) goto L_0x012c
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            java.lang.Object r1 = r1.get(r10)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x012a
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            r1.moveToFront(r10)
        L_0x012a:
            r13 = r0
            goto L_0x012d
        L_0x012c:
            r13 = r0
        L_0x012d:
            if (r13 == 0) goto L_0x014a
            r3 = 1
            r4 = 1
            r0 = r49
            r1 = r13
            r2 = r10
            r5 = r23
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r15.cancelLoadingForImageReceiver(r14, r11)
            if (r24 == 0) goto L_0x0146
            boolean r0 = r49.isForcePreview()
            if (r0 == 0) goto L_0x0146
            return
        L_0x0146:
            r6 = 1
            r25 = r6
            goto L_0x014c
        L_0x014a:
            r25 = r6
        L_0x014c:
            r0 = 0
            java.lang.Object r13 = r49.getParentObject()
            org.telegram.tgnet.TLRPC$Document r26 = r49.getQualityThumbDocument()
            org.telegram.messenger.ImageLocation r12 = r49.getThumbLocation()
            java.lang.String r6 = r49.getThumbFilter()
            org.telegram.messenger.ImageLocation r1 = r49.getMediaLocation()
            java.lang.String r5 = r49.getMediaFilter()
            org.telegram.messenger.ImageLocation r27 = r49.getImageLocation()
            java.lang.String r4 = r49.getImageFilter()
            r2 = r27
            if (r2 != 0) goto L_0x019a
            boolean r3 = r49.isNeedsQualityThumb()
            if (r3 == 0) goto L_0x019a
            boolean r3 = r49.isCurrentKeyQuality()
            if (r3 == 0) goto L_0x019a
            boolean r3 = r13 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x0190
            r3 = r13
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r16 = r3.getDocument()
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r16)
            r0 = 1
            r28 = r0
            goto L_0x019c
        L_0x0190:
            if (r26 == 0) goto L_0x019a
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r26)
            r0 = 1
            r28 = r0
            goto L_0x019c
        L_0x019a:
            r28 = r0
        L_0x019c:
            r0 = 0
            r3 = 0
            r16 = 0
            r17 = 0
            r9 = 0
            r10 = 0
            r7 = 0
            r8 = 2
            if (r2 == 0) goto L_0x01af
            int r11 = r2.imageType
            if (r11 != r8) goto L_0x01af
            java.lang.String r11 = "mp4"
            goto L_0x01b0
        L_0x01af:
            r11 = 0
        L_0x01b0:
            if (r1 == 0) goto L_0x01bb
            r20 = r0
            int r0 = r1.imageType
            if (r0 != r8) goto L_0x01bd
            java.lang.String r0 = "mp4"
            goto L_0x01be
        L_0x01bb:
            r20 = r0
        L_0x01bd:
            r0 = 0
        L_0x01be:
            java.lang.String r21 = r49.getExt()
            if (r21 != 0) goto L_0x01c9
            java.lang.String r21 = "jpg"
            r29 = r21
            goto L_0x01cb
        L_0x01c9:
            r29 = r21
        L_0x01cb:
            if (r11 != 0) goto L_0x01d2
            r11 = r29
            r30 = r11
            goto L_0x01d4
        L_0x01d2:
            r30 = r11
        L_0x01d4:
            if (r0 != 0) goto L_0x01db
            r0 = r29
            r31 = r0
            goto L_0x01dd
        L_0x01db:
            r31 = r0
        L_0x01dd:
            r0 = 0
            r33 = r1
            r11 = r2
            r34 = r3
            r35 = r17
            r32 = r20
        L_0x01e7:
            java.lang.String r1 = "jpg"
            java.lang.String r2 = "."
            if (r0 >= r8) goto L_0x038b
            if (r0 != 0) goto L_0x01f5
            r3 = r11
            r17 = r30
            r8 = r17
            goto L_0x01fb
        L_0x01f5:
            r3 = r33
            r17 = r31
            r8 = r17
        L_0x01fb:
            if (r3 != 0) goto L_0x0201
            r20 = r10
            goto L_0x0380
        L_0x0201:
            r20 = r10
            if (r33 == 0) goto L_0x0208
            r10 = r33
            goto L_0x0209
        L_0x0208:
            r10 = r11
        L_0x0209:
            r14 = 0
            java.lang.String r10 = r3.getKey(r13, r10, r14)
            if (r10 != 0) goto L_0x0212
            goto L_0x0380
        L_0x0212:
            if (r33 == 0) goto L_0x0217
            r14 = r33
            goto L_0x0218
        L_0x0217:
            r14 = r11
        L_0x0218:
            r15 = 1
            java.lang.String r14 = r3.getKey(r13, r14, r15)
            java.lang.String r15 = r3.path
            if (r15 == 0) goto L_0x023b
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r14)
            r15.append(r2)
            java.lang.String r2 = r3.path
            java.lang.String r1 = getHttpUrlExtension(r2, r1)
            r15.append(r1)
            java.lang.String r14 = r15.toString()
            goto L_0x0363
        L_0x023b:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r3.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r1 != 0) goto L_0x0351
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r3.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoPathSize
            if (r1 == 0) goto L_0x0249
            goto L_0x0351
        L_0x0249:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            if (r1 == 0) goto L_0x0280
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r2)
            r1.append(r8)
            java.lang.String r14 = r1.toString()
            java.lang.String r1 = r49.getExt()
            if (r1 != 0) goto L_0x027c
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            byte[] r1 = r1.key
            if (r1 != 0) goto L_0x027c
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            long r1 = r1.volume_id
            r21 = -2147483648(0xfffffffvar_, double:NaN)
            int r15 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r15 != 0) goto L_0x0363
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            int r1 = r1.local_id
            if (r1 >= 0) goto L_0x0363
        L_0x027c:
            r32 = 1
            goto L_0x0363
        L_0x0280:
            org.telegram.messenger.WebFile r1 = r3.webFile
            if (r1 == 0) goto L_0x02a8
            org.telegram.messenger.WebFile r1 = r3.webFile
            java.lang.String r1 = r1.mime_type
            java.lang.String r1 = org.telegram.messenger.FileLoader.getMimeTypePart(r1)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r14)
            r15.append(r2)
            org.telegram.messenger.WebFile r2 = r3.webFile
            java.lang.String r2 = r2.url
            java.lang.String r2 = getHttpUrlExtension(r2, r1)
            r15.append(r2)
            java.lang.String r14 = r15.toString()
            goto L_0x0363
        L_0x02a8:
            org.telegram.messenger.SecureDocument r1 = r3.secureDocument
            if (r1 == 0) goto L_0x02c0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r2)
            r1.append(r8)
            java.lang.String r14 = r1.toString()
            goto L_0x0363
        L_0x02c0:
            org.telegram.tgnet.TLRPC$Document r1 = r3.document
            if (r1 == 0) goto L_0x0363
            if (r0 != 0) goto L_0x02da
            if (r28 == 0) goto L_0x02da
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "q_"
            r1.append(r2)
            r1.append(r10)
            java.lang.String r1 = r1.toString()
            r10 = r1
        L_0x02da:
            org.telegram.tgnet.TLRPC$Document r1 = r3.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getDocumentFileName(r1)
            r2 = 46
            int r2 = r1.lastIndexOf(r2)
            r15 = r2
            r21 = r10
            r10 = -1
            if (r2 != r10) goto L_0x02ef
            java.lang.String r1 = ""
            goto L_0x02f3
        L_0x02ef:
            java.lang.String r1 = r1.substring(r15)
        L_0x02f3:
            int r2 = r1.length()
            r10 = 1
            if (r2 > r10) goto L_0x031a
            org.telegram.tgnet.TLRPC$Document r2 = r3.document
            java.lang.String r2 = r2.mime_type
            java.lang.String r10 = "video/mp4"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0309
            java.lang.String r1 = ".mp4"
            goto L_0x031a
        L_0x0309:
            org.telegram.tgnet.TLRPC$Document r2 = r3.document
            java.lang.String r2 = r2.mime_type
            java.lang.String r10 = "video/x-matroska"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0318
            java.lang.String r1 = ".mkv"
            goto L_0x031a
        L_0x0318:
            java.lang.String r1 = ""
        L_0x031a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            r2.append(r1)
            java.lang.String r14 = r2.toString()
            org.telegram.tgnet.TLRPC$Document r2 = r3.document
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r2)
            if (r2 != 0) goto L_0x034b
            org.telegram.tgnet.TLRPC$Document r2 = r3.document
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r2 != 0) goto L_0x034b
            org.telegram.tgnet.TLRPC$Document r2 = r3.document
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r2)
            if (r2 != 0) goto L_0x034b
            org.telegram.tgnet.TLRPC$Document r2 = r3.document
            boolean r2 = org.telegram.messenger.MessageObject.canPreviewDocument(r2)
            if (r2 != 0) goto L_0x034b
            r2 = 1
            goto L_0x034c
        L_0x034b:
            r2 = 0
        L_0x034c:
            r32 = r2
            r10 = r21
            goto L_0x0363
        L_0x0351:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r2)
            r1.append(r8)
            java.lang.String r14 = r1.toString()
        L_0x0363:
            if (r0 != 0) goto L_0x0369
            r9 = r10
            r34 = r14
            goto L_0x036c
        L_0x0369:
            r7 = r10
            r35 = r14
        L_0x036c:
            if (r3 != r12) goto L_0x0380
            if (r0 != 0) goto L_0x0378
            r1 = 0
            r2 = 0
            r9 = 0
            r11 = r1
            r34 = r9
            r9 = r2
            goto L_0x0380
        L_0x0378:
            r1 = 0
            r2 = 0
            r7 = 0
            r33 = r1
            r35 = r7
            r7 = r2
        L_0x0380:
            int r0 = r0 + 1
            r8 = 2
            r15 = r48
            r14 = r49
            r10 = r20
            goto L_0x01e7
        L_0x038b:
            r20 = r10
            if (r12 == 0) goto L_0x040e
            org.telegram.messenger.ImageLocation r0 = r49.getStrippedLocation()
            if (r0 != 0) goto L_0x039d
            if (r33 == 0) goto L_0x039a
            r3 = r33
            goto L_0x039c
        L_0x039a:
            r3 = r27
        L_0x039c:
            r0 = r3
        L_0x039d:
            r3 = 0
            java.lang.String r10 = r12.getKey(r13, r0, r3)
            r3 = 1
            java.lang.String r8 = r12.getKey(r13, r0, r3)
            java.lang.String r14 = r12.path
            if (r14 == 0) goto L_0x03c8
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r8)
            r14.append(r2)
            java.lang.String r2 = r12.path
            java.lang.String r1 = getHttpUrlExtension(r2, r1)
            r14.append(r1)
            java.lang.String r16 = r14.toString()
            r15 = r29
            r29 = r16
            goto L_0x0415
        L_0x03c8:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r12.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r1 != 0) goto L_0x03f7
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r12.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoPathSize
            if (r1 == 0) goto L_0x03d7
            r15 = r29
            goto L_0x03f9
        L_0x03d7:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r12.location
            if (r1 == 0) goto L_0x03f2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            r1.append(r2)
            r15 = r29
            r1.append(r15)
            java.lang.String r16 = r1.toString()
            r29 = r16
            goto L_0x0415
        L_0x03f2:
            r15 = r29
            r29 = r8
            goto L_0x0415
        L_0x03f7:
            r15 = r29
        L_0x03f9:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            r1.append(r2)
            r1.append(r15)
            java.lang.String r16 = r1.toString()
            r29 = r16
            goto L_0x0415
        L_0x040e:
            r15 = r29
            r3 = 1
            r29 = r16
            r10 = r20
        L_0x0415:
            java.lang.String r0 = "@"
            if (r7 == 0) goto L_0x0430
            if (r5 == 0) goto L_0x0430
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            r1.append(r0)
            r1.append(r5)
            java.lang.String r7 = r1.toString()
            r36 = r7
            goto L_0x0432
        L_0x0430:
            r36 = r7
        L_0x0432:
            if (r9 == 0) goto L_0x0448
            if (r4 == 0) goto L_0x0448
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r0)
            r1.append(r4)
            java.lang.String r9 = r1.toString()
        L_0x0448:
            if (r10 == 0) goto L_0x0461
            if (r6 == 0) goto L_0x0461
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            r1.append(r0)
            r1.append(r6)
            java.lang.String r10 = r1.toString()
            r37 = r10
            goto L_0x0463
        L_0x0461:
            r37 = r10
        L_0x0463:
            java.lang.String r0 = r49.getUniqKeyPrefix()
            if (r0 == 0) goto L_0x047f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r49.getUniqKeyPrefix()
            r0.append(r1)
            r0.append(r9)
            java.lang.String r9 = r0.toString()
            r38 = r9
            goto L_0x0481
        L_0x047f:
            r38 = r9
        L_0x0481:
            if (r11 == 0) goto L_0x04c7
            java.lang.String r0 = r11.path
            if (r0 == 0) goto L_0x04c7
            r7 = 0
            r9 = 1
            r10 = 1
            if (r25 == 0) goto L_0x0490
            r17 = 2
            goto L_0x0492
        L_0x0490:
            r17 = 1
        L_0x0492:
            r0 = r48
            r1 = r49
            r2 = r37
            r3 = r29
            r39 = r4
            r4 = r15
            r40 = r5
            r5 = r12
            r41 = r6
            r42 = r11
            r11 = r17
            r43 = r12
            r12 = r23
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            long r7 = r49.getSize()
            r9 = 1
            r10 = 0
            r11 = 0
            r2 = r38
            r3 = r34
            r4 = r30
            r5 = r42
            r6 = r39
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            r46 = r13
            r47 = r15
            goto L_0x0595
        L_0x04c7:
            r39 = r4
            r40 = r5
            r41 = r6
            r42 = r11
            r43 = r12
            if (r33 == 0) goto L_0x0545
            int r0 = r49.getCacheType()
            r44 = 1
            if (r0 != 0) goto L_0x04e1
            if (r32 == 0) goto L_0x04e1
            r0 = 1
            r45 = r0
            goto L_0x04e3
        L_0x04e1:
            r45 = r0
        L_0x04e3:
            if (r45 != 0) goto L_0x04e7
            r9 = 1
            goto L_0x04e9
        L_0x04e7:
            r9 = r45
        L_0x04e9:
            if (r25 != 0) goto L_0x0501
            r7 = 0
            r10 = 1
            r11 = 1
            r0 = r48
            r1 = r49
            r2 = r37
            r3 = r29
            r4 = r15
            r5 = r43
            r6 = r41
            r12 = r23
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
        L_0x0501:
            if (r24 != 0) goto L_0x0523
            r17 = 0
            r20 = 0
            r21 = 0
            r10 = r48
            r11 = r49
            r12 = r38
            r46 = r13
            r13 = r34
            r14 = r30
            r47 = r15
            r15 = r42
            r16 = r39
            r19 = r44
            r22 = r23
            r10.createLoadOperationForImageReceiver(r11, r12, r13, r14, r15, r16, r17, r19, r20, r21, r22)
            goto L_0x0527
        L_0x0523:
            r46 = r13
            r47 = r15
        L_0x0527:
            long r17 = r49.getSize()
            r20 = 3
            r21 = 0
            r10 = r48
            r11 = r49
            r12 = r36
            r13 = r35
            r14 = r31
            r15 = r33
            r16 = r40
            r19 = r45
            r22 = r23
            r10.createLoadOperationForImageReceiver(r11, r12, r13, r14, r15, r16, r17, r19, r20, r21, r22)
            goto L_0x0595
        L_0x0545:
            r46 = r13
            r47 = r15
            int r0 = r49.getCacheType()
            if (r0 != 0) goto L_0x0555
            if (r32 == 0) goto L_0x0555
            r0 = 1
            r44 = r0
            goto L_0x0557
        L_0x0555:
            r44 = r0
        L_0x0557:
            if (r44 != 0) goto L_0x055b
            r9 = 1
            goto L_0x055d
        L_0x055b:
            r9 = r44
        L_0x055d:
            r7 = 0
            r10 = 1
            if (r25 == 0) goto L_0x0564
            r11 = 2
            goto L_0x0565
        L_0x0564:
            r11 = 1
        L_0x0565:
            r0 = r48
            r1 = r49
            r2 = r37
            r3 = r29
            r4 = r47
            r5 = r43
            r6 = r41
            r12 = r23
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            long r17 = r49.getSize()
            r20 = 0
            r21 = 0
            r10 = r48
            r11 = r49
            r12 = r38
            r13 = r34
            r14 = r30
            r15 = r42
            r16 = r39
            r19 = r44
            r22 = r23
            r10.createLoadOperationForImageReceiver(r11, r12, r13, r14, r15, r16, r17, r19, r20, r21, r22)
        L_0x0595:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public BitmapDrawable getFromLottieCache(String imageKey) {
        BitmapDrawable drawable = this.lottieMemCache.get(imageKey);
        if (!(drawable instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) drawable).isRecycled()) {
            return drawable;
        }
        this.lottieMemCache.remove(imageKey);
        return null;
    }

    private boolean useLottieMemCache(ImageLocation imageLocation, String key) {
        return (imageLocation != null && (MessageObject.isAnimatedStickerDocument(imageLocation.document, true) || imageLocation.imageType == 1 || MessageObject.isVideoSticker(imageLocation.document))) || isAnimatedAvatar(key);
    }

    /* access modifiers changed from: private */
    public void httpFileLoadError(String location) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda10(this, location));
    }

    /* renamed from: lambda$httpFileLoadError$8$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1885lambda$httpFileLoadError$8$orgtelegrammessengerImageLoader(String location) {
        CacheImage img = this.imageLoadingByUrl.get(location);
        if (img != null) {
            HttpImageTask oldTask = img.httpTask;
            if (oldTask != null) {
                img.httpTask = new HttpImageTask(oldTask.cacheImage, oldTask.imageSize);
                this.httpTasks.add(img.httpTask);
            }
            runHttpTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void artworkLoadError(String location) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda7(this, location));
    }

    /* renamed from: lambda$artworkLoadError$9$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1878lambda$artworkLoadError$9$orgtelegrammessengerImageLoader(String location) {
        CacheImage img = this.imageLoadingByUrl.get(location);
        if (img != null) {
            ArtworkLoadTask oldTask = img.artworkTask;
            if (oldTask != null) {
                img.artworkTask = new ArtworkLoadTask(oldTask.cacheImage);
                this.artworkTasks.add(img.artworkTask);
            }
            runArtworkTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void fileDidLoaded(String location, File finalFile, int mediaType) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda12(this, location, mediaType, finalFile));
    }

    /* renamed from: lambda$fileDidLoaded$10$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1884lambda$fileDidLoaded$10$orgtelegrammessengerImageLoader(String location, int mediaType, File finalFile) {
        String str = location;
        File file = finalFile;
        ThumbGenerateInfo info = this.waitingForQualityThumb.get(str);
        if (info == null || info.parentDocument == null) {
            int i = mediaType;
        } else {
            generateThumb(mediaType, file, info);
            this.waitingForQualityThumb.remove(str);
        }
        CacheImage img = this.imageLoadingByUrl.get(str);
        if (img != null) {
            this.imageLoadingByUrl.remove(str);
            ArrayList<CacheOutTask> tasks = new ArrayList<>();
            for (int a = 0; a < img.imageReceiverArray.size(); a++) {
                String key = img.keys.get(a);
                String filter = img.filters.get(a);
                int type = img.types.get(a).intValue();
                ImageReceiver imageReceiver = img.imageReceiverArray.get(a);
                int guid = img.imageReceiverGuidsArray.get(a).intValue();
                CacheImage cacheImage = this.imageLoadingByKeys.get(key);
                if (cacheImage == null) {
                    cacheImage = new CacheImage();
                    cacheImage.secureDocument = img.secureDocument;
                    cacheImage.currentAccount = img.currentAccount;
                    cacheImage.finalFilePath = file;
                    cacheImage.parentObject = img.parentObject;
                    cacheImage.key = key;
                    cacheImage.imageLocation = img.imageLocation;
                    cacheImage.type = type;
                    cacheImage.ext = img.ext;
                    cacheImage.encryptionKeyPath = img.encryptionKeyPath;
                    cacheImage.cacheTask = new CacheOutTask(cacheImage);
                    cacheImage.filter = filter;
                    cacheImage.imageType = img.imageType;
                    this.imageLoadingByKeys.put(key, cacheImage);
                    tasks.add(cacheImage.cacheTask);
                }
                int i2 = type;
                cacheImage.addImageReceiver(imageReceiver, key, filter, type, guid);
            }
            for (int a2 = 0; a2 < tasks.size(); a2++) {
                CacheOutTask task = tasks.get(a2);
                if (task.cacheImage.type == 1) {
                    this.cacheThumbOutQueue.postRunnable(task);
                } else {
                    this.cacheOutQueue.postRunnable(task);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fileDidFailedLoad(String location, int canceled) {
        if (canceled != 1) {
            this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda9(this, location));
        }
    }

    /* renamed from: lambda$fileDidFailedLoad$11$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1883lambda$fileDidFailedLoad$11$orgtelegrammessengerImageLoader(String location) {
        CacheImage img = this.imageLoadingByUrl.get(location);
        if (img != null) {
            img.setImageAndClear((Drawable) null, (String) null);
        }
    }

    /* access modifiers changed from: private */
    public void runHttpTasks(boolean complete) {
        if (complete) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            HttpImageTask task = this.httpTasks.poll();
            if (task != null) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentHttpTasksCount++;
            }
        }
    }

    /* access modifiers changed from: private */
    public void runArtworkTasks(boolean complete) {
        if (complete) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            try {
                this.artworkTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentArtworkTasksCount++;
            } catch (Throwable th) {
                runArtworkTasks(false);
            }
        }
    }

    public boolean isLoadingHttpFile(String url) {
        return this.httpFileLoadTasksByKeys.containsKey(url);
    }

    public static String getHttpFileName(String url) {
        return Utilities.MD5(url);
    }

    public static File getHttpFilePath(String url, String defaultExt) {
        String ext = getHttpUrlExtension(url, defaultExt);
        File directory = FileLoader.getDirectory(4);
        return new File(directory, Utilities.MD5(url) + "." + ext);
    }

    public void loadHttpFile(String url, String defaultExt, int currentAccount) {
        if (url != null && url.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(url)) {
            String ext = getHttpUrlExtension(url, defaultExt);
            File directory = FileLoader.getDirectory(4);
            File file = new File(directory, Utilities.MD5(url) + "_temp." + ext);
            file.delete();
            HttpFileTask task = new HttpFileTask(url, file, ext, currentAccount);
            this.httpFileLoadTasks.add(task);
            this.httpFileLoadTasksByKeys.put(url, task);
            runHttpFileLoadTasks((HttpFileTask) null, 0);
        }
    }

    public void cancelLoadHttpFile(String url) {
        HttpFileTask task = this.httpFileLoadTasksByKeys.get(url);
        if (task != null) {
            task.cancel(true);
            this.httpFileLoadTasksByKeys.remove(url);
            this.httpFileLoadTasks.remove(task);
        }
        Runnable runnable = this.retryHttpsTasks.get(url);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks((HttpFileTask) null, 0);
    }

    /* access modifiers changed from: private */
    public void runHttpFileLoadTasks(HttpFileTask oldTask, int reason) {
        AndroidUtilities.runOnUIThread(new ImageLoader$$ExternalSyntheticLambda2(this, oldTask, reason));
    }

    /* renamed from: lambda$runHttpFileLoadTasks$13$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1889xCLASSNAMEfbb45(HttpFileTask oldTask, int reason) {
        if (oldTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (oldTask != null) {
            if (reason == 1) {
                if (oldTask.canRetry) {
                    Runnable runnable = new ImageLoader$$ExternalSyntheticLambda1(this, new HttpFileTask(oldTask.url, oldTask.tempFile, oldTask.ext, oldTask.currentAccount));
                    this.retryHttpsTasks.put(oldTask.url, runnable);
                    AndroidUtilities.runOnUIThread(runnable, 1000);
                } else {
                    this.httpFileLoadTasksByKeys.remove(oldTask.url);
                    NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, oldTask.url, 0);
                }
            } else if (reason == 2) {
                this.httpFileLoadTasksByKeys.remove(oldTask.url);
                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(oldTask.url) + "." + oldTask.ext);
                NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoad, oldTask.url, oldTask.tempFile.renameTo(file) ? file.toString() : oldTask.tempFile.toString());
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            this.httpFileLoadTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpFileLoadTasksCount++;
        }
    }

    /* renamed from: lambda$runHttpFileLoadTasks$12$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m1888x531082e6(HttpFileTask newTask) {
        this.httpFileLoadTasks.add(newTask);
        runHttpFileLoadTasks((HttpFileTask) null, 0);
    }

    public static boolean shouldSendImageAsDocument(String path, Uri uri) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (!(path != null || uri == null || uri.getScheme() == null)) {
            if (uri.getScheme().contains("file")) {
                path = uri.getPath();
            } else {
                try {
                    path = AndroidUtilities.getPath(uri);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
        if (path != null) {
            BitmapFactory.decodeFile(path, bmOptions);
        } else if (uri != null) {
            try {
                InputStream inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, (Rect) null, bmOptions);
                inputStream.close();
            } catch (Throwable e2) {
                FileLog.e(e2);
                return false;
            }
        }
        float photoW = (float) bmOptions.outWidth;
        float photoH = (float) bmOptions.outHeight;
        if (photoW / photoH > 10.0f || photoH / photoW > 10.0f) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00bc A[SYNTHETIC, Splitter:B:49:0x00bc] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00f6 A[SYNTHETIC, Splitter:B:72:0x00f6] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0102 A[Catch:{ all -> 0x00c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x010e A[Catch:{ all -> 0x00c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0122  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0133 A[SYNTHETIC, Splitter:B:83:0x0133] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r19, android.net.Uri r20, float r21, float r22, boolean r23) {
        /*
            r1 = r20
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
            r0.<init>()
            r2 = r0
            r3 = 1
            r2.inJustDecodeBounds = r3
            r4 = 0
            if (r19 != 0) goto L_0x0047
            if (r1 == 0) goto L_0x0047
            java.lang.String r0 = r20.getScheme()
            if (r0 == 0) goto L_0x0047
            r5 = 0
            java.lang.String r0 = r20.getScheme()
            java.lang.String r6 = "file"
            boolean r0 = r0.contains(r6)
            if (r0 == 0) goto L_0x0029
            java.lang.String r0 = r20.getPath()
            r5 = r0
            goto L_0x0049
        L_0x0029:
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 30
            if (r0 < r6) goto L_0x003b
            java.lang.String r0 = r20.getScheme()
            java.lang.String r6 = "content"
            boolean r0 = r6.equals(r0)
            if (r0 != 0) goto L_0x0047
        L_0x003b:
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r20)     // Catch:{ all -> 0x0041 }
            r5 = r0
            goto L_0x0049
        L_0x0041:
            r0 = move-exception
            r6 = r0
            r0 = r6
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0047:
            r5 = r19
        L_0x0049:
            r6 = 0
            if (r5 == 0) goto L_0x0050
            android.graphics.BitmapFactory.decodeFile(r5, r2)
            goto L_0x0075
        L_0x0050:
            if (r1 == 0) goto L_0x0075
            r7 = 0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0070 }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0070 }
            java.io.InputStream r0 = r0.openInputStream(r1)     // Catch:{ all -> 0x0070 }
            r4 = r0
            android.graphics.BitmapFactory.decodeStream(r4, r6, r2)     // Catch:{ all -> 0x0070 }
            r4.close()     // Catch:{ all -> 0x0070 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0070 }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0070 }
            java.io.InputStream r0 = r0.openInputStream(r1)     // Catch:{ all -> 0x0070 }
            r4 = r0
            goto L_0x0075
        L_0x0070:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            return r6
        L_0x0075:
            int r0 = r2.outWidth
            float r7 = (float) r0
            int r0 = r2.outHeight
            float r8 = (float) r0
            float r0 = r7 / r21
            float r9 = r8 / r22
            if (r23 == 0) goto L_0x0086
            float r0 = java.lang.Math.max(r0, r9)
            goto L_0x008a
        L_0x0086:
            float r0 = java.lang.Math.min(r0, r9)
        L_0x008a:
            r9 = 1065353216(0x3var_, float:1.0)
            int r10 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r10 >= 0) goto L_0x0094
            r0 = 1065353216(0x3var_, float:1.0)
            r10 = r0
            goto L_0x0095
        L_0x0094:
            r10 = r0
        L_0x0095:
            r0 = 0
            r2.inJustDecodeBounds = r0
            int r11 = (int) r10
            r2.inSampleSize = r11
            int r11 = r2.inSampleSize
            int r11 = r11 % 2
            if (r11 == 0) goto L_0x00ad
            r11 = 1
        L_0x00a2:
            int r12 = r11 * 2
            int r13 = r2.inSampleSize
            if (r12 >= r13) goto L_0x00ab
            int r11 = r11 * 2
            goto L_0x00a2
        L_0x00ab:
            r2.inSampleSize = r11
        L_0x00ad:
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r11 >= r12) goto L_0x00b4
            r0 = 1
        L_0x00b4:
            r2.inPurgeable = r0
            r11 = 0
            r12 = 0
            java.lang.String r0 = "Orientation"
            if (r5 == 0) goto L_0x00c9
            androidx.exifinterface.media.ExifInterface r13 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00c7 }
            r13.<init>((java.lang.String) r5)     // Catch:{ all -> 0x00c7 }
            int r0 = r13.getAttributeInt(r0, r3)     // Catch:{ all -> 0x00c7 }
            r12 = r0
            goto L_0x00f1
        L_0x00c7:
            r0 = move-exception
            goto L_0x011a
        L_0x00c9:
            if (r1 == 0) goto L_0x00f1
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00ef }
            android.content.ContentResolver r13 = r13.getContentResolver()     // Catch:{ all -> 0x00ef }
            java.io.InputStream r13 = r13.openInputStream(r1)     // Catch:{ all -> 0x00ef }
            androidx.exifinterface.media.ExifInterface r14 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00e5 }
            r14.<init>((java.io.InputStream) r13)     // Catch:{ all -> 0x00e5 }
            int r0 = r14.getAttributeInt(r0, r3)     // Catch:{ all -> 0x00e5 }
            r12 = r0
            if (r13 == 0) goto L_0x00e4
            r13.close()     // Catch:{ all -> 0x00ef }
        L_0x00e4:
            goto L_0x00f2
        L_0x00e5:
            r0 = move-exception
            r3 = r0
            if (r13 == 0) goto L_0x00ee
            r13.close()     // Catch:{ all -> 0x00ed }
            goto L_0x00ee
        L_0x00ed:
            r0 = move-exception
        L_0x00ee:
            throw r3     // Catch:{ all -> 0x00ef }
        L_0x00ef:
            r0 = move-exception
            goto L_0x00f2
        L_0x00f1:
        L_0x00f2:
            switch(r12) {
                case 3: goto L_0x010e;
                case 6: goto L_0x0102;
                case 8: goto L_0x00f6;
                default: goto L_0x00f5;
            }
        L_0x00f5:
            goto L_0x0119
        L_0x00f6:
            android.graphics.Matrix r0 = new android.graphics.Matrix     // Catch:{ all -> 0x00c7 }
            r0.<init>()     // Catch:{ all -> 0x00c7 }
            r11 = r0
            r0 = 1132920832(0x43870000, float:270.0)
            r11.postRotate(r0)     // Catch:{ all -> 0x00c7 }
            goto L_0x0119
        L_0x0102:
            android.graphics.Matrix r0 = new android.graphics.Matrix     // Catch:{ all -> 0x00c7 }
            r0.<init>()     // Catch:{ all -> 0x00c7 }
            r11 = r0
            r0 = 1119092736(0x42b40000, float:90.0)
            r11.postRotate(r0)     // Catch:{ all -> 0x00c7 }
            goto L_0x0119
        L_0x010e:
            android.graphics.Matrix r0 = new android.graphics.Matrix     // Catch:{ all -> 0x00c7 }
            r0.<init>()     // Catch:{ all -> 0x00c7 }
            r11 = r0
            r0 = 1127481344(0x43340000, float:180.0)
            r11.postRotate(r0)     // Catch:{ all -> 0x00c7 }
        L_0x0119:
        L_0x011a:
            int r0 = r2.inSampleSize
            float r0 = (float) r0
            float r10 = r10 / r0
            int r0 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x0130
            if (r11 != 0) goto L_0x012a
            android.graphics.Matrix r0 = new android.graphics.Matrix
            r0.<init>()
            r11 = r0
        L_0x012a:
            float r0 = r9 / r10
            float r9 = r9 / r10
            r11.postScale(r0, r9)
        L_0x0130:
            r3 = 0
            if (r5 == 0) goto L_0x019c
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r5, r2)     // Catch:{ all -> 0x015c }
            r3 = r0
            if (r3 == 0) goto L_0x015a
            boolean r0 = r2.inPurgeable     // Catch:{ all -> 0x015c }
            if (r0 == 0) goto L_0x0141
            org.telegram.messenger.Utilities.pinBitmap(r3)     // Catch:{ all -> 0x015c }
        L_0x0141:
            r13 = 0
            r14 = 0
            int r15 = r3.getWidth()     // Catch:{ all -> 0x015c }
            int r16 = r3.getHeight()     // Catch:{ all -> 0x015c }
            r18 = 1
            r12 = r3
            r17 = r11
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x015c }
            if (r0 == r3) goto L_0x015a
            r3.recycle()     // Catch:{ all -> 0x015c }
            r3 = r0
        L_0x015a:
            goto L_0x01e5
        L_0x015c:
            r0 = move-exception
            r6 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            org.telegram.messenger.ImageLoader r0 = getInstance()
            r0.clearMemory()
            if (r3 != 0) goto L_0x017b
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r5, r2)     // Catch:{ all -> 0x0179 }
            r3 = r0
            if (r3 == 0) goto L_0x017b
            boolean r0 = r2.inPurgeable     // Catch:{ all -> 0x0179 }
            if (r0 == 0) goto L_0x017b
            org.telegram.messenger.Utilities.pinBitmap(r3)     // Catch:{ all -> 0x0179 }
            goto L_0x017b
        L_0x0179:
            r0 = move-exception
            goto L_0x0197
        L_0x017b:
            if (r3 == 0) goto L_0x019b
            r13 = 0
            r14 = 0
            int r15 = r3.getWidth()     // Catch:{ all -> 0x0179 }
            int r16 = r3.getHeight()     // Catch:{ all -> 0x0179 }
            r18 = 1
            r12 = r3
            r17 = r11
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x0179 }
            if (r0 == r3) goto L_0x019b
            r3.recycle()     // Catch:{ all -> 0x0179 }
            r3 = r0
            goto L_0x019b
        L_0x0197:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x015a
        L_0x019b:
            goto L_0x015a
        L_0x019c:
            if (r1 == 0) goto L_0x01e5
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r6, r2)     // Catch:{ all -> 0x01d0 }
            r3 = r0
            if (r3 == 0) goto L_0x01c5
            boolean r0 = r2.inPurgeable     // Catch:{ all -> 0x01d0 }
            if (r0 == 0) goto L_0x01ac
            org.telegram.messenger.Utilities.pinBitmap(r3)     // Catch:{ all -> 0x01d0 }
        L_0x01ac:
            r13 = 0
            r14 = 0
            int r15 = r3.getWidth()     // Catch:{ all -> 0x01d0 }
            int r16 = r3.getHeight()     // Catch:{ all -> 0x01d0 }
            r18 = 1
            r12 = r3
            r17 = r11
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x01d0 }
            if (r0 == r3) goto L_0x01c5
            r3.recycle()     // Catch:{ all -> 0x01d0 }
            r3 = r0
        L_0x01c5:
            r4.close()     // Catch:{ all -> 0x01c9 }
        L_0x01c8:
            goto L_0x01e5
        L_0x01c9:
            r0 = move-exception
            r6 = r0
            r0 = r6
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01e5
        L_0x01d0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x01d8 }
            r4.close()     // Catch:{ all -> 0x01c9 }
            goto L_0x01c8
        L_0x01d8:
            r0 = move-exception
            r6 = r0
            r4.close()     // Catch:{ all -> 0x01de }
            goto L_0x01e4
        L_0x01de:
            r0 = move-exception
            r9 = r0
            r0 = r9
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01e4:
            throw r6
        L_0x01e5:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(TLRPC.PhotoSize photoSize) {
        if (photoSize == null) {
            return;
        }
        if (photoSize.bytes == null || photoSize.bytes.length == 0) {
            try {
                RandomAccessFile f = new RandomAccessFile(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(photoSize, true), "r");
                if (((int) f.length()) < 20000) {
                    photoSize.bytes = new byte[((int) f.length())];
                    f.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private static TLRPC.PhotoSize scaleAndSaveImageInternal(TLRPC.PhotoSize photoSize, Bitmap bitmap, Bitmap.CompressFormat compressFormat, boolean progressive, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway, boolean forceCacheDir) throws Exception {
        Bitmap scaledBitmap;
        TLRPC.TL_fileLocationToBeDeprecated location;
        File fileDir;
        TLRPC.PhotoSize photoSize2 = photoSize;
        Bitmap bitmap2 = bitmap;
        Bitmap.CompressFormat compressFormat2 = compressFormat;
        int i = quality;
        if (scaleFactor > 1.0f || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap2, w, h, true);
        } else {
            int i2 = h;
            scaledBitmap = bitmap;
            int i3 = w;
        }
        if (photoSize2 == null) {
        }
        if (photoSize2 == null || !(photoSize2.location instanceof TLRPC.TL_fileLocationToBeDeprecated)) {
            location = new TLRPC.TL_fileLocationToBeDeprecated();
            location.volume_id = -2147483648L;
            location.dc_id = Integer.MIN_VALUE;
            location.local_id = SharedConfig.getLastLocalId();
            location.file_reference = new byte[0];
            photoSize2 = new TLRPC.TL_photoSize_layer127();
            photoSize2.location = location;
            photoSize2.w = scaledBitmap.getWidth();
            photoSize2.h = scaledBitmap.getHeight();
            if (photoSize2.w <= 100 && photoSize2.h <= 100) {
                photoSize2.type = "s";
            } else if (photoSize2.w <= 320 && photoSize2.h <= 320) {
                photoSize2.type = "m";
            } else if (photoSize2.w <= 800 && photoSize2.h <= 800) {
                photoSize2.type = "x";
            } else if (photoSize2.w > 1280 || photoSize2.h > 1280) {
                photoSize2.type = "w";
            } else {
                photoSize2.type = "y";
            }
        } else {
            location = (TLRPC.TL_fileLocationToBeDeprecated) photoSize2.location;
        }
        String fileName = location.volume_id + "_" + location.local_id + ".jpg";
        if (forceCacheDir) {
            fileDir = FileLoader.getDirectory(4);
        } else {
            fileDir = location.volume_id != -2147483648L ? FileLoader.getDirectory(0) : FileLoader.getDirectory(4);
        }
        FileOutputStream stream = new FileOutputStream(new File(fileDir, fileName));
        scaledBitmap.compress(compressFormat2, i, stream);
        if (!cache) {
            photoSize2.size = (int) stream.getChannel().size();
        }
        stream.close();
        if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            scaledBitmap.compress(compressFormat2, i, stream2);
            photoSize2.bytes = stream2.toByteArray();
            photoSize2.size = photoSize2.bytes.length;
            stream2.close();
        }
        if (scaledBitmap != bitmap2) {
            scaledBitmap.recycle();
        }
        return photoSize2;
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, false, maxWidth, maxHeight, quality, cache, 0, 0, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(TLRPC.PhotoSize photoSize, Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, boolean forceCacheDir) {
        return scaleAndSaveImage(photoSize, bitmap, Bitmap.CompressFormat.JPEG, false, maxWidth, maxHeight, quality, cache, 0, 0, forceCacheDir);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, false, maxWidth, maxHeight, quality, cache, minWidth, minHeight, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, boolean progressive, int quality, boolean cache, int minWidth, int minHeight) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, progressive, maxWidth, maxHeight, quality, cache, minWidth, minHeight, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, Bitmap.CompressFormat compressFormat, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, compressFormat, false, maxWidth, maxHeight, quality, cache, minWidth, minHeight, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(TLRPC.PhotoSize photoSize, Bitmap bitmap, Bitmap.CompressFormat compressFormat, boolean progressive, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight, boolean forceCacheDir) {
        float scaleFactor;
        boolean scaleAnyway;
        float scaleFactor2;
        int i = minWidth;
        int i2 = minHeight;
        if (bitmap == null) {
            return null;
        }
        float photoW = (float) bitmap.getWidth();
        float photoH = (float) bitmap.getHeight();
        if (photoW == 0.0f) {
            float f = photoW;
        } else if (photoH == 0.0f) {
            float f2 = photoH;
            float f3 = photoW;
        } else {
            float scaleFactor3 = Math.max(photoW / maxWidth, photoH / maxHeight);
            if (i == 0 || i2 == 0 || (photoW >= ((float) i) && photoH >= ((float) i2))) {
                scaleAnyway = false;
                scaleFactor = scaleFactor3;
            } else {
                if (photoW < ((float) i) && photoH > ((float) i2)) {
                    scaleFactor2 = photoW / ((float) i);
                } else if (photoW <= ((float) i) || photoH >= ((float) i2)) {
                    scaleFactor2 = Math.max(photoW / ((float) i), photoH / ((float) i2));
                } else {
                    scaleFactor2 = photoH / ((float) i2);
                }
                scaleAnyway = true;
                scaleFactor = scaleFactor2;
            }
            int w = (int) (photoW / scaleFactor);
            int h = (int) (photoH / scaleFactor);
            if (h == 0) {
                int i3 = w;
                float f4 = photoH;
                float f5 = photoW;
            } else if (w == 0) {
                int i4 = h;
                int i5 = w;
                float f6 = photoH;
                float f7 = photoW;
            } else {
                int h2 = h;
                int w2 = w;
                float photoH2 = photoH;
                float photoW2 = photoW;
                try {
                    return scaleAndSaveImageInternal(photoSize, bitmap, compressFormat, progressive, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway, forceCacheDir);
                } catch (Throwable th) {
                    FileLog.e(th);
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    public static String getHttpUrlExtension(String url, String defaultExt) {
        String ext = null;
        String last = Uri.parse(url).getLastPathSegment();
        if (!TextUtils.isEmpty(last) && last.length() > 1) {
            url = last;
        }
        int idx = url.lastIndexOf(46);
        if (idx != -1) {
            ext = url.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0 || ext.length() > 4) {
            return defaultExt;
        }
        return ext;
    }

    public static void saveMessageThumbs(TLRPC.Message message) {
        TLRPC.PhotoSize photoSize;
        boolean isEncrypted;
        File file;
        TLRPC.Message message2 = message;
        if (message2.media != null && (photoSize = findPhotoCachedSize(message)) != null && photoSize.bytes != null && photoSize.bytes.length != 0) {
            if (photoSize.location == null || (photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                photoSize.location = new TLRPC.TL_fileLocationToBeDeprecated();
                photoSize.location.volume_id = -2147483648L;
                photoSize.location.local_id = SharedConfig.getLastLocalId();
            }
            File file2 = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(photoSize, true);
            if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                isEncrypted = true;
                file = new File(file2.getAbsolutePath() + ".enc");
            } else {
                isEncrypted = false;
                file = file2;
            }
            if (!file.exists()) {
                if (isEncrypted) {
                    try {
                        RandomAccessFile keyFile = new RandomAccessFile(new File(FileLoader.getInternalCacheDir(), file.getName() + ".key"), "rws");
                        long len = keyFile.length();
                        byte[] encryptKey = new byte[32];
                        byte[] encryptIv = new byte[16];
                        if (len <= 0 || len % 48 != 0) {
                            Utilities.random.nextBytes(encryptKey);
                            Utilities.random.nextBytes(encryptIv);
                            keyFile.write(encryptKey);
                            keyFile.write(encryptIv);
                        } else {
                            keyFile.read(encryptKey, 0, 32);
                            keyFile.read(encryptIv, 0, 16);
                        }
                        keyFile.close();
                        byte[] bArr = encryptIv;
                        Utilities.aesCtrDecryptionByteArray(photoSize.bytes, encryptKey, encryptIv, 0, (long) photoSize.bytes.length, 0);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                RandomAccessFile writeFile = new RandomAccessFile(file, "rws");
                writeFile.write(photoSize.bytes);
                writeFile.close();
            }
            TLRPC.TL_photoSize newPhotoSize = new TLRPC.TL_photoSize_layer127();
            newPhotoSize.w = photoSize.w;
            newPhotoSize.h = photoSize.h;
            newPhotoSize.location = photoSize.location;
            newPhotoSize.size = photoSize.size;
            newPhotoSize.type = photoSize.type;
            if (message2.media instanceof TLRPC.TL_messageMediaPhoto) {
                int count = message2.media.photo.sizes.size();
                for (int a = 0; a < count; a++) {
                    if (message2.media.photo.sizes.get(a) instanceof TLRPC.TL_photoCachedSize) {
                        message2.media.photo.sizes.set(a, newPhotoSize);
                        return;
                    }
                }
            } else if (message2.media instanceof TLRPC.TL_messageMediaDocument) {
                int count2 = message2.media.document.thumbs.size();
                for (int a2 = 0; a2 < count2; a2++) {
                    if (message2.media.document.thumbs.get(a2) instanceof TLRPC.TL_photoCachedSize) {
                        message2.media.document.thumbs.set(a2, newPhotoSize);
                        return;
                    }
                }
            } else if (message2.media instanceof TLRPC.TL_messageMediaWebPage) {
                int count3 = message2.media.webpage.photo.sizes.size();
                for (int a3 = 0; a3 < count3; a3++) {
                    if (message2.media.webpage.photo.sizes.get(a3) instanceof TLRPC.TL_photoCachedSize) {
                        message2.media.webpage.photo.sizes.set(a3, newPhotoSize);
                        return;
                    }
                }
            }
        }
    }

    private static TLRPC.PhotoSize findPhotoCachedSize(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
            int count = message.media.photo.sizes.size();
            for (int a = 0; a < count; a++) {
                TLRPC.PhotoSize size = message.media.photo.sizes.get(a);
                if (size instanceof TLRPC.TL_photoCachedSize) {
                    return size;
                }
            }
            return null;
        } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            int count2 = message.media.document.thumbs.size();
            for (int a2 = 0; a2 < count2; a2++) {
                TLRPC.PhotoSize size2 = message.media.document.thumbs.get(a2);
                if (size2 instanceof TLRPC.TL_photoCachedSize) {
                    return size2;
                }
            }
            return null;
        } else if (!(message.media instanceof TLRPC.TL_messageMediaWebPage) || message.media.webpage.photo == null) {
            return null;
        } else {
            int count3 = message.media.webpage.photo.sizes.size();
            for (int a3 = 0; a3 < count3; a3++) {
                TLRPC.PhotoSize size3 = message.media.webpage.photo.sizes.get(a3);
                if (size3 instanceof TLRPC.TL_photoCachedSize) {
                    return size3;
                }
            }
            return null;
        }
    }

    public static void saveMessagesThumbs(ArrayList<TLRPC.Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (int a = 0; a < messages.size(); a++) {
                saveMessageThumbs(messages.get(a));
            }
        }
    }

    public static MessageThumb generateMessageThumb(TLRPC.Message message) {
        Bitmap b;
        Bitmap bitmap;
        TLRPC.Message message2 = message;
        TLRPC.PhotoSize photoSize = findPhotoCachedSize(message);
        int i = 3;
        if (photoSize != null && photoSize.bytes != null && photoSize.bytes.length != 0) {
            File file = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(photoSize, true);
            TLRPC.TL_photoSize newPhotoSize = new TLRPC.TL_photoSize_layer127();
            newPhotoSize.w = photoSize.w;
            newPhotoSize.h = photoSize.h;
            newPhotoSize.location = photoSize.location;
            newPhotoSize.size = photoSize.size;
            newPhotoSize.type = photoSize.type;
            if (file.exists() && message2.grouped_id == 0) {
                Point point = ChatMessageCell.getMessageSize(photoSize.w, photoSize.h);
                String key = String.format(Locale.US, "%d_%d@%d_%d_b", new Object[]{Long.valueOf(photoSize.location.volume_id), Integer.valueOf(photoSize.location.local_id), Integer.valueOf((int) (point.x / AndroidUtilities.density)), Integer.valueOf((int) (point.y / AndroidUtilities.density))});
                if (!getInstance().isInMemCache(key, false) && (bitmap = loadBitmap(file.getPath(), (Uri) null, (float) ((int) (point.x / AndroidUtilities.density)), (float) ((int) (point.y / AndroidUtilities.density)), false)) != null) {
                    Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                    Bitmap scaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) (point.x / AndroidUtilities.density), (int) (point.y / AndroidUtilities.density), true);
                    if (scaledBitmap != bitmap) {
                        bitmap.recycle();
                        bitmap = scaledBitmap;
                    }
                    return new MessageThumb(key, new BitmapDrawable(bitmap));
                }
            }
        } else if (message2.media instanceof TLRPC.TL_messageMediaDocument) {
            int a = 0;
            int count = message2.media.document.thumbs.size();
            while (a < count) {
                TLRPC.PhotoSize size = message2.media.document.thumbs.get(a);
                if (size instanceof TLRPC.TL_photoStrippedSize) {
                    TLRPC.PhotoSize thumbSize = FileLoader.getClosestPhotoSizeWithSize(message2.media.document.thumbs, 320);
                    int h = 0;
                    int w = 0;
                    if (thumbSize != null) {
                        h = thumbSize.h;
                        w = thumbSize.w;
                    } else {
                        int k = 0;
                        while (true) {
                            if (k >= message2.media.document.attributes.size()) {
                                break;
                            } else if (message2.media.document.attributes.get(k) instanceof TLRPC.TL_documentAttributeVideo) {
                                TLRPC.TL_documentAttributeVideo videoAttribute = (TLRPC.TL_documentAttributeVideo) message2.media.document.attributes.get(k);
                                h = videoAttribute.h;
                                w = videoAttribute.w;
                                break;
                            } else {
                                k++;
                            }
                        }
                    }
                    Point point2 = ChatMessageCell.getMessageSize(w, h);
                    Locale locale = Locale.US;
                    Object[] objArr = new Object[i];
                    objArr[0] = ImageLocation.getStrippedKey(message2, message2, size);
                    objArr[1] = Integer.valueOf((int) (point2.x / AndroidUtilities.density));
                    objArr[2] = Integer.valueOf((int) (point2.y / AndroidUtilities.density));
                    String key2 = String.format(locale, "%s_false@%d_%d_b", objArr);
                    if (!getInstance().isInMemCache(key2, false) && (b = getStrippedPhotoBitmap(size.bytes, (String) null)) != null) {
                        Utilities.blurBitmap(b, 3, 1, b.getWidth(), b.getHeight(), b.getRowBytes());
                        Bitmap scaledBitmap2 = Bitmaps.createScaledBitmap(b, (int) (point2.x / AndroidUtilities.density), (int) (point2.y / AndroidUtilities.density), true);
                        if (scaledBitmap2 != b) {
                            b.recycle();
                            b = scaledBitmap2;
                        }
                        return new MessageThumb(key2, new BitmapDrawable(b));
                    }
                }
                a++;
                i = 3;
            }
        }
        return null;
    }

    public void onFragmentStackChanged() {
        for (int i = 0; i < this.cachedAnimatedFileDrawables.size(); i++) {
            this.cachedAnimatedFileDrawables.get(i).repeatCount = 0;
        }
    }

    public DispatchQueue getCacheOutQueue() {
        return this.cacheOutQueue;
    }

    public static class MessageThumb {
        BitmapDrawable drawable;
        String key;

        public MessageThumb(String key2, BitmapDrawable bitmapDrawable) {
            this.key = key2;
            this.drawable = bitmapDrawable;
        }
    }
}
