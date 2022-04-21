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
        public /* synthetic */ void m634x99a68a63(long uploadedSize, long totalSize) {
            ImageLoader.this.fileProgresses.put(this.url, new long[]{uploadedSize, totalSize});
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpFileTask$$ExternalSyntheticLambda0(this, uploadedSize, totalSize));
        }

        /* renamed from: lambda$reportProgress$0$org-telegram-messenger-ImageLoader$HttpFileTask  reason: not valid java name */
        public /* synthetic */ void m633x9a1cvar_(long uploadedSize, long totalSize) {
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
                            this.fileSize = Utilities.parseInt(length).intValue();
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
        public /* synthetic */ void m626x647e920b(String result) {
            this.cacheImage.httpTask = new HttpImageTask(this.cacheImage, 0, result);
            ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
            ImageLoader.this.runHttpTasks(false);
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-messenger-ImageLoader$ArtworkLoadTask  reason: not valid java name */
        public /* synthetic */ void m627x92572c6a() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* renamed from: lambda$onCancelled$2$org-telegram-messenger-ImageLoader$ArtworkLoadTask  reason: not valid java name */
        public /* synthetic */ void m625x1e3958ad() {
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
        public int imageSize;
        private long lastProgressTime;
        private String overrideUrl;

        public HttpImageTask(CacheImage cacheImage2, int size) {
            this.cacheImage = cacheImage2;
            this.imageSize = size;
        }

        public HttpImageTask(CacheImage cacheImage2, int size, String url) {
            this.cacheImage = cacheImage2;
            this.imageSize = size;
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
        public /* synthetic */ void m642xb5eb4c7e(long uploadedSize, long totalSize) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, new long[]{uploadedSize, totalSize});
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda4(this, uploadedSize, totalSize));
        }

        /* renamed from: lambda$reportProgress$0$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m641xCLASSNAMEa65f(long uploadedSize, long totalSize) {
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
                            this.imageSize = Utilities.parseInt(length).intValue();
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
                                    int i = this.imageSize;
                                    if (i != 0) {
                                        reportProgress((long) totalLoaded, (long) i);
                                    }
                                } else if (read == -1) {
                                    done = true;
                                    int i2 = this.imageSize;
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
        public /* synthetic */ void m639x6321fc0(Boolean result) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda6(this, result));
        }

        /* renamed from: lambda$onPostExecute$3$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m638x148879a1(Boolean result) {
            if (result.booleanValue()) {
                NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoaded, this.cacheImage.url, this.cacheImage.finalFilePath);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 2);
        }

        /* renamed from: lambda$onPostExecute$5$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m640xf7dbc5df() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* renamed from: lambda$onCancelled$6$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m635xa7d226e2() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda0(this));
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda2(this));
        }

        /* renamed from: lambda$onCancelled$8$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m637x8b257320() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$onCancelled$7$org-telegram-messenger-ImageLoader$HttpImageTask  reason: not valid java name */
        public /* synthetic */ void m636x997bcd01() {
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
        public /* synthetic */ void m643x21var_f(String name) {
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
        public /* synthetic */ void m644x4b36var_(String key, ArrayList finalImageReceiverArray, BitmapDrawable bitmapDrawable, ArrayList finalImageReceiverGuidsArray) {
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v6, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v7, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v8, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v10, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v73, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v29, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v31, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v11, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: org.telegram.messenger.secretmedia.EncryptedFileInputStream} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v54, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v27, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v56, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v57, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v58, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v60, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v61, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: float} */
        /* JADX WARNING: type inference failed for: r7v80 */
        /* JADX WARNING: type inference failed for: r7v81 */
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
        /* JADX WARNING: Failed to insert additional move for type inference */
        /* JADX WARNING: Multi-variable type inference failed */
        public void run() {
            /*
                r42 = this;
                r1 = r42
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0d71 }
                r1.runningThread = r0     // Catch:{ all -> 0x0d71 }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0d71 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0d71 }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0d71 }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0d71 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r0 = r0.photoSize
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
                if (r0 == 0) goto L_0x003f
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r0 = r0.photoSize
                org.telegram.tgnet.TLRPC$TL_photoStrippedSize r0 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r0
                byte[] r3 = r0.bytes
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage
                java.lang.String r4 = r4.filter
                android.graphics.Bitmap r3 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r3, r4)
                if (r3 == 0) goto L_0x0039
                android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
                r2.<init>(r3)
                goto L_0x003a
            L_0x0039:
                r2 = 0
            L_0x003a:
                r1.onPostExecute(r2)
                goto L_0x0d70
            L_0x003f:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r3 = 5
                if (r0 != r3) goto L_0x0063
                r2 = 0
                org.telegram.ui.Components.ThemePreviewDrawable r0 = new org.telegram.ui.Components.ThemePreviewDrawable     // Catch:{ all -> 0x005a }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x005a }
                java.io.File r3 = r3.finalFilePath     // Catch:{ all -> 0x005a }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x005a }
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x005a }
                org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ all -> 0x005a }
                org.telegram.messenger.DocumentObject$ThemeDocument r4 = (org.telegram.messenger.DocumentObject.ThemeDocument) r4     // Catch:{ all -> 0x005a }
                r0.<init>(r3, r4)     // Catch:{ all -> 0x005a }
                r2 = r0
                goto L_0x005e
            L_0x005a:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x005e:
                r1.onPostExecute(r2)
                goto L_0x0d70
            L_0x0063:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r4 = 3
                r5 = 4
                r6 = 2
                r7 = 0
                r8 = 1
                if (r0 == r4) goto L_0x0d08
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                if (r0 != r5) goto L_0x0076
                goto L_0x0d08
            L_0x0076:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                if (r0 != r8) goto L_0x025c
                r0 = 1126865306(0x432a999a, float:170.6)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r9 = 512(0x200, float:7.175E-43)
                int r2 = java.lang.Math.min(r9, r2)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r9, r0)
                r10 = 0
                r11 = 0
                r12 = 0
                r13 = 1
                r14 = 0
                r15 = 0
                r16 = 0
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                if (r5 == 0) goto L_0x0199
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                java.lang.String r3 = "_"
                java.lang.String[] r3 = r5.split(r3)
                int r5 = r3.length
                if (r5 < r6) goto L_0x011e
                r5 = r3[r7]
                float r5 = java.lang.Float.parseFloat(r5)
                r19 = r3[r8]
                float r19 = java.lang.Float.parseFloat(r19)
                float r20 = org.telegram.messenger.AndroidUtilities.density
                float r7 = r5 * r20
                int r7 = (int) r7
                int r2 = java.lang.Math.min(r9, r7)
                float r7 = org.telegram.messenger.AndroidUtilities.density
                float r7 = r7 * r19
                int r7 = (int) r7
                int r0 = java.lang.Math.min(r9, r7)
                r7 = 1119092736(0x42b40000, float:90.0)
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 > 0) goto L_0x00eb
                int r7 = (r19 > r7 ? 1 : (r19 == r7 ? 0 : -1))
                if (r7 > 0) goto L_0x00eb
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                java.lang.String r9 = "nolimit"
                boolean r7 = r7.contains(r9)
                if (r7 != 0) goto L_0x00eb
                r7 = 160(0xa0, float:2.24E-43)
                int r2 = java.lang.Math.min(r2, r7)
                int r0 = java.lang.Math.min(r0, r7)
                r11 = 1
            L_0x00eb:
                int r7 = r3.length
                if (r7 < r4) goto L_0x00fb
                java.lang.String r7 = "pcache"
                r9 = r3[r6]
                boolean r7 = r7.equals(r9)
                if (r7 == 0) goto L_0x00fb
                r7 = 1
                r10 = r7
                goto L_0x0111
            L_0x00fb:
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                java.lang.String r9 = "nolimit"
                boolean r7 = r7.contains(r9)
                if (r7 != 0) goto L_0x010f
                int r7 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                if (r7 == r6) goto L_0x010f
                r7 = 1
                goto L_0x0110
            L_0x010f:
                r7 = 0
            L_0x0110:
                r10 = r7
            L_0x0111:
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.lang.String r7 = r7.filter
                java.lang.String r9 = "lastframe"
                boolean r7 = r7.contains(r9)
                if (r7 == 0) goto L_0x011e
                r12 = 1
            L_0x011e:
                int r5 = r3.length
                if (r5 < r4) goto L_0x0146
                java.lang.String r5 = "nr"
                r7 = r3[r6]
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x012d
                r13 = 2
                goto L_0x0146
            L_0x012d:
                java.lang.String r5 = "nrs"
                r7 = r3[r6]
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x0139
                r13 = 3
                goto L_0x0146
            L_0x0139:
                java.lang.String r5 = "dice"
                r7 = r3[r6]
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x0146
                r15 = r3[r4]
                r13 = 2
            L_0x0146:
                int r4 = r3.length
                r5 = 5
                if (r4 < r5) goto L_0x0196
                java.lang.String r4 = "c1"
                r5 = 4
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x015a
                r16 = 12
                r3 = r2
                r2 = r0
                goto L_0x019b
            L_0x015a:
                java.lang.String r4 = "c2"
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0169
                r16 = 3
                r3 = r2
                r2 = r0
                goto L_0x019b
            L_0x0169:
                java.lang.String r4 = "c3"
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0178
                r16 = 4
                r3 = r2
                r2 = r0
                goto L_0x019b
            L_0x0178:
                java.lang.String r4 = "c4"
                r7 = r3[r5]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0187
                r16 = 5
                r3 = r2
                r2 = r0
                goto L_0x019b
            L_0x0187:
                java.lang.String r4 = "c5"
                r5 = r3[r5]
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0196
                r16 = 6
                r3 = r2
                r2 = r0
                goto L_0x019b
            L_0x0196:
                r3 = r2
                r2 = r0
                goto L_0x019b
            L_0x0199:
                r3 = r2
                r2 = r0
            L_0x019b:
                if (r15 == 0) goto L_0x01b3
                java.lang.String r0 = "🎰"
                boolean r0 = r0.equals(r15)
                if (r0 == 0) goto L_0x01ac
                org.telegram.ui.Components.SlotsDrawable r0 = new org.telegram.ui.Components.SlotsDrawable
                r0.<init>(r15, r3, r2)
                goto L_0x0241
            L_0x01ac:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                r0.<init>(r15, r3, r2)
                goto L_0x0241
            L_0x01b3:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r4 = r0.finalFilePath
                r5 = 0
                r7 = 0
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x01f5 }
                org.telegram.messenger.ImageLoader$CacheImage r9 = r1.cacheImage     // Catch:{ Exception -> 0x01f5 }
                java.io.File r9 = r9.finalFilePath     // Catch:{ Exception -> 0x01f5 }
                java.lang.String r6 = "r"
                r0.<init>(r9, r6)     // Catch:{ Exception -> 0x01f5 }
                r5 = r0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x01f5 }
                int r0 = r0.type     // Catch:{ Exception -> 0x01f5 }
                if (r0 != r8) goto L_0x01d0
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x01f5 }
                goto L_0x01d4
            L_0x01d0:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x01f5 }
            L_0x01d4:
                r6 = 2
                r9 = 0
                r5.readFully(r0, r9, r6)     // Catch:{ Exception -> 0x01f5 }
                byte r6 = r0[r9]     // Catch:{ Exception -> 0x01f5 }
                r9 = 31
                if (r6 != r9) goto L_0x01e6
                byte r6 = r0[r8]     // Catch:{ Exception -> 0x01f5 }
                r8 = -117(0xffffffffffffff8b, float:NaN)
                if (r6 != r8) goto L_0x01e6
                r7 = 1
            L_0x01e6:
                r5.close()     // Catch:{ Exception -> 0x01eb }
            L_0x01ea:
                goto L_0x0200
            L_0x01eb:
                r0 = move-exception
                r6 = r0
                r0 = r6
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x01ea
            L_0x01f2:
                r0 = move-exception
                r6 = r0
                goto L_0x024f
            L_0x01f5:
                r0 = move-exception
                r6 = 0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r6)     // Catch:{ all -> 0x01f2 }
                if (r5 == 0) goto L_0x0200
                r5.close()     // Catch:{ Exception -> 0x01eb }
                goto L_0x01ea
            L_0x0200:
                if (r12 == 0) goto L_0x0204
                r0 = 0
                r10 = r0
            L_0x0204:
                if (r7 == 0) goto L_0x0228
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                org.telegram.messenger.ImageLoader$CacheImage r8 = r1.cacheImage
                java.io.File r8 = r8.finalFilePath
                java.lang.String r24 = org.telegram.messenger.ImageLoader.decompressGzip(r8)
                r29 = 0
                r22 = r0
                r23 = r6
                r25 = r3
                r26 = r2
                r27 = r10
                r28 = r11
                r30 = r16
                r22.<init>(r23, r24, r25, r26, r27, r28, r29, r30)
                goto L_0x0241
            L_0x0228:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                r28 = 0
                r22 = r0
                r23 = r6
                r24 = r3
                r25 = r2
                r26 = r10
                r27 = r11
                r29 = r16
                r22.<init>(r23, r24, r25, r26, r27, r28, r29)
            L_0x0241:
                if (r12 == 0) goto L_0x0247
                r1.loadLastFrame(r0, r2, r3)
                goto L_0x024d
            L_0x0247:
                r0.setAutoRepeat(r13)
                r1.onPostExecute(r0)
            L_0x024d:
                goto L_0x0d70
            L_0x024f:
                if (r5 == 0) goto L_0x025b
                r5.close()     // Catch:{ Exception -> 0x0255 }
                goto L_0x025b
            L_0x0255:
                r0 = move-exception
                r8 = r0
                r0 = r8
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x025b:
                throw r6
            L_0x025c:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r3 = 2
                if (r0 != r3) goto L_0x033f
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                if (r0 == 0) goto L_0x0270
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                long r3 = r0.videoSeekTo
                goto L_0x0272
            L_0x0270:
                r3 = 0
            L_0x0272:
                java.lang.String r0 = "g"
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                boolean r0 = r0.equals(r5)
                if (r0 == 0) goto L_0x02d6
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r0 = r0.document
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted
                if (r0 != 0) goto L_0x02d6
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r0 = r0.document
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document
                if (r0 == 0) goto L_0x0299
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r0 = r0.document
                goto L_0x029a
            L_0x0299:
                r0 = 0
            L_0x029a:
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                if (r0 == 0) goto L_0x02a1
                int r5 = r5.size
                goto L_0x02a5
            L_0x02a1:
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation
                int r5 = r5.currentSize
            L_0x02a5:
                org.telegram.ui.Components.AnimatedFileDrawable r6 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                java.io.File r7 = r7.finalFilePath
                r24 = 0
                long r8 = (long) r5
                if (r0 != 0) goto L_0x02b7
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation
                r28 = r2
                goto L_0x02b9
            L_0x02b7:
                r28 = 0
            L_0x02b9:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.lang.Object r2 = r2.parentObject
                org.telegram.messenger.ImageLoader$CacheImage r10 = r1.cacheImage
                int r10 = r10.currentAccount
                r33 = 0
                r22 = r6
                r23 = r7
                r25 = r8
                r27 = r0
                r29 = r2
                r30 = r3
                r32 = r10
                r22.<init>(r23, r24, r25, r27, r28, r29, r30, r32, r33)
                r0 = r6
                goto L_0x0337
            L_0x02d6:
                r0 = 0
                r2 = 0
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                if (r5 == 0) goto L_0x0303
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.lang.String r5 = r5.filter
                java.lang.String r6 = "_"
                java.lang.String[] r5 = r5.split(r6)
                int r6 = r5.length
                r7 = 2
                if (r6 < r7) goto L_0x0303
                r6 = 0
                r6 = r5[r6]
                float r6 = java.lang.Float.parseFloat(r6)
                r7 = r5[r8]
                float r7 = java.lang.Float.parseFloat(r7)
                float r8 = org.telegram.messenger.AndroidUtilities.density
                float r8 = r8 * r6
                int r0 = (int) r8
                float r8 = org.telegram.messenger.AndroidUtilities.density
                float r8 = r8 * r7
                int r2 = (int) r8
            L_0x0303:
                org.telegram.ui.Components.AnimatedFileDrawable r5 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                java.lang.String r7 = "d"
                org.telegram.messenger.ImageLoader$CacheImage r8 = r1.cacheImage
                java.lang.String r8 = r8.filter
                boolean r24 = r7.equals(r8)
                r25 = 0
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage
                org.telegram.messenger.ImageLocation r7 = r7.imageLocation
                org.telegram.tgnet.TLRPC$Document r7 = r7.document
                r28 = 0
                r29 = 0
                org.telegram.messenger.ImageLoader$CacheImage r8 = r1.cacheImage
                int r8 = r8.currentAccount
                r33 = 0
                r22 = r5
                r23 = r6
                r27 = r7
                r30 = r3
                r32 = r8
                r34 = r0
                r35 = r2
                r22.<init>(r23, r24, r25, r27, r28, r29, r30, r32, r33, r34, r35)
                r0 = r5
            L_0x0337:
                java.lang.Thread.interrupted()
                r1.onPostExecute(r0)
                goto L_0x0d70
            L_0x033f:
                r3 = 0
                r5 = 0
                r6 = 0
                r7 = 0
                r9 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r10 = r0.finalFilePath
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 != 0) goto L_0x0365
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x0363
                if (r10 == 0) goto L_0x0363
                java.lang.String r0 = r10.getAbsolutePath()
                java.lang.String r11 = ".enc"
                boolean r0 = r0.endsWith(r11)
                if (r0 == 0) goto L_0x0363
                goto L_0x0365
            L_0x0363:
                r0 = 0
                goto L_0x0366
            L_0x0365:
                r0 = 1
            L_0x0366:
                r11 = r0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x0399
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                org.telegram.messenger.SecureDocumentKey r0 = r0.secureDocumentKey
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                org.telegram.tgnet.TLRPC$TL_secureFile r12 = r12.secureFile
                if (r12 == 0) goto L_0x0390
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                org.telegram.tgnet.TLRPC$TL_secureFile r12 = r12.secureFile
                byte[] r12 = r12.file_hash
                if (r12 == 0) goto L_0x0390
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                org.telegram.tgnet.TLRPC$TL_secureFile r12 = r12.secureFile
                byte[] r12 = r12.file_hash
                r13 = r12
                r12 = r0
                goto L_0x039d
            L_0x0390:
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                org.telegram.messenger.SecureDocument r12 = r12.secureDocument
                byte[] r12 = r12.fileHash
                r13 = r12
                r12 = r0
                goto L_0x039d
            L_0x0399:
                r0 = 0
                r12 = 0
                r13 = r12
                r12 = r0
            L_0x039d:
                r14 = 1
                r15 = 0
                int r0 = android.os.Build.VERSION.SDK_INT
                r4 = 19
                if (r0 >= r4) goto L_0x0407
                r4 = 0
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x03f0 }
                java.lang.String r2 = "r"
                r0.<init>(r10, r2)     // Catch:{ Exception -> 0x03f0 }
                r4 = r0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x03f0 }
                int r0 = r0.type     // Catch:{ Exception -> 0x03f0 }
                if (r0 != r8) goto L_0x03b9
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x03f0 }
                goto L_0x03bd
            L_0x03b9:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x03f0 }
            L_0x03bd:
                int r2 = r0.length     // Catch:{ Exception -> 0x03f0 }
                r8 = 0
                r4.readFully(r0, r8, r2)     // Catch:{ Exception -> 0x03f0 }
                java.lang.String r2 = new java.lang.String     // Catch:{ Exception -> 0x03f0 }
                r2.<init>(r0)     // Catch:{ Exception -> 0x03f0 }
                java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x03f0 }
                java.lang.String r8 = r2.toLowerCase()     // Catch:{ Exception -> 0x03f0 }
                r2 = r8
                java.lang.String r8 = "riff"
                boolean r8 = r2.startsWith(r8)     // Catch:{ Exception -> 0x03f0 }
                if (r8 == 0) goto L_0x03e1
                java.lang.String r8 = "webp"
                boolean r8 = r2.endsWith(r8)     // Catch:{ Exception -> 0x03f0 }
                if (r8 == 0) goto L_0x03e1
                r15 = 1
            L_0x03e1:
                r4.close()     // Catch:{ Exception -> 0x03e6 }
            L_0x03e5:
                goto L_0x0407
            L_0x03e6:
                r0 = move-exception
                r2 = r0
                r0 = r2
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x03e5
            L_0x03ed:
                r0 = move-exception
                r2 = r0
                goto L_0x03fa
            L_0x03f0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x03ed }
                if (r4 == 0) goto L_0x0407
                r4.close()     // Catch:{ Exception -> 0x03e6 }
                goto L_0x03e5
            L_0x03fa:
                if (r4 == 0) goto L_0x0406
                r4.close()     // Catch:{ Exception -> 0x0400 }
                goto L_0x0406
            L_0x0400:
                r0 = move-exception
                r8 = r0
                r0 = r8
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0406:
                throw r2
            L_0x0407:
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation
                java.lang.String r2 = r2.path
                if (r2 == 0) goto L_0x046b
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation
                java.lang.String r2 = r2.path
                java.lang.String r4 = "thumb://"
                boolean r4 = r2.startsWith(r4)
                if (r4 == 0) goto L_0x043e
                java.lang.String r4 = ":"
                r8 = 8
                int r4 = r2.indexOf(r4, r8)
                if (r4 < 0) goto L_0x043b
                java.lang.String r8 = r2.substring(r8, r4)
                long r22 = java.lang.Long.parseLong(r8)
                java.lang.Long r3 = java.lang.Long.valueOf(r22)
                r5 = 0
                int r8 = r4 + 1
                java.lang.String r0 = r2.substring(r8)
            L_0x043b:
                r14 = 0
                r2 = r0
                goto L_0x046c
            L_0x043e:
                java.lang.String r4 = "vthumb://"
                boolean r4 = r2.startsWith(r4)
                if (r4 == 0) goto L_0x0460
                java.lang.String r4 = ":"
                r8 = 9
                int r4 = r2.indexOf(r4, r8)
                if (r4 < 0) goto L_0x045d
                java.lang.String r8 = r2.substring(r8, r4)
                long r22 = java.lang.Long.parseLong(r8)
                java.lang.Long r3 = java.lang.Long.valueOf(r22)
                r5 = 1
            L_0x045d:
                r14 = 0
                r2 = r0
                goto L_0x046c
            L_0x0460:
                java.lang.String r4 = "http"
                boolean r4 = r2.startsWith(r4)
                if (r4 != 0) goto L_0x046b
                r14 = 0
                r2 = r0
                goto L_0x046c
            L_0x046b:
                r2 = r0
            L_0x046c:
                android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
                r0.<init>()
                r4 = r0
                r8 = 1
                r4.inSampleSize = r8
                int r0 = android.os.Build.VERSION.SDK_INT
                r8 = 21
                if (r0 >= r8) goto L_0x047e
                r8 = 1
                r4.inPurgeable = r8
            L_0x047e:
                r8 = 0
                r22 = 0
                r23 = 0
                r24 = 0
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r25 = r0.canForce8888
                r26 = 0
                r27 = 1065353216(0x3var_, float:1.0)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x06cc }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x06cc }
                if (r0 == 0) goto L_0x0646
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x06cc }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x06cc }
                r28 = r6
                java.lang.String r6 = "_"
                java.lang.String[] r0 = r0.split(r6)     // Catch:{ all -> 0x063a }
                int r6 = r0.length     // Catch:{ all -> 0x063a }
                r29 = r7
                r7 = 2
                if (r6 < r7) goto L_0x04ca
                r6 = 0
                r7 = r0[r6]     // Catch:{ all -> 0x04c0 }
                float r6 = java.lang.Float.parseFloat(r7)     // Catch:{ all -> 0x04c0 }
                float r7 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x04c0 }
                float r8 = r6 * r7
                r6 = 1
                r7 = r0[r6]     // Catch:{ all -> 0x04c0 }
                float r6 = java.lang.Float.parseFloat(r7)     // Catch:{ all -> 0x04c0 }
                float r7 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x04c0 }
                float r6 = r6 * r7
                r22 = r6
                goto L_0x04ca
            L_0x04c0:
                r0 = move-exception
                r31 = r9
                r30 = r14
                r7 = r15
                r6 = r28
                goto L_0x06d6
            L_0x04ca:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x0630 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x0630 }
                java.lang.String r7 = "b2"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x0630 }
                if (r6 == 0) goto L_0x04da
                r6 = 3
                r23 = r6
                goto L_0x04f9
            L_0x04da:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x0630 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x0630 }
                java.lang.String r7 = "b1"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x0630 }
                if (r6 == 0) goto L_0x04ea
                r6 = 2
                r23 = r6
                goto L_0x04f9
            L_0x04ea:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x0630 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x0630 }
                java.lang.String r7 = "b"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x0630 }
                if (r6 == 0) goto L_0x04f9
                r6 = 1
                r23 = r6
            L_0x04f9:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x0630 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x0630 }
                java.lang.String r7 = "i"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x0630 }
                if (r6 == 0) goto L_0x0508
                r6 = 1
                r24 = r6
            L_0x0508:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x0630 }
                java.lang.String r6 = r6.filter     // Catch:{ all -> 0x0630 }
                java.lang.String r7 = "f"
                boolean r6 = r6.contains(r7)     // Catch:{ all -> 0x0630 }
                if (r6 == 0) goto L_0x0517
                r6 = 1
                r25 = r6
            L_0x0517:
                if (r15 != 0) goto L_0x0627
                int r6 = (r8 > r26 ? 1 : (r8 == r26 ? 0 : -1))
                if (r6 == 0) goto L_0x0627
                int r6 = (r22 > r26 ? 1 : (r22 == r26 ? 0 : -1))
                if (r6 == 0) goto L_0x0627
                r6 = 1
                r4.inJustDecodeBounds = r6     // Catch:{ all -> 0x0630 }
                if (r3 == 0) goto L_0x055f
                if (r2 != 0) goto L_0x055f
                if (r5 == 0) goto L_0x0548
                android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0630 }
                android.content.ContentResolver r6 = r6.getContentResolver()     // Catch:{ all -> 0x0630 }
                r30 = r14
                r7 = r15
                long r14 = r3.longValue()     // Catch:{ all -> 0x0541 }
                r31 = r9
                r9 = 1
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r6, r14, r9, r4)     // Catch:{ all -> 0x06c1 }
                r32 = r0
                goto L_0x05dc
            L_0x0541:
                r0 = move-exception
                r31 = r9
                r6 = r28
                goto L_0x06d6
            L_0x0548:
                r31 = r9
                r30 = r14
                r7 = r15
                android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x06c1 }
                android.content.ContentResolver r6 = r6.getContentResolver()     // Catch:{ all -> 0x06c1 }
                long r14 = r3.longValue()     // Catch:{ all -> 0x06c1 }
                r9 = 1
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r6, r14, r9, r4)     // Catch:{ all -> 0x06c1 }
                r32 = r0
                goto L_0x05dc
            L_0x055f:
                r31 = r9
                r30 = r14
                r7 = r15
                if (r12 == 0) goto L_0x05c2
                java.io.RandomAccessFile r6 = new java.io.RandomAccessFile     // Catch:{ all -> 0x06c1 }
                java.lang.String r9 = "r"
                r6.<init>(r10, r9)     // Catch:{ all -> 0x06c1 }
                long r14 = r6.length()     // Catch:{ all -> 0x06c1 }
                int r9 = (int) r14     // Catch:{ all -> 0x06c1 }
                java.lang.ThreadLocal r14 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x06c1 }
                java.lang.Object r14 = r14.get()     // Catch:{ all -> 0x06c1 }
                byte[] r14 = (byte[]) r14     // Catch:{ all -> 0x06c1 }
                if (r14 == 0) goto L_0x0583
                int r15 = r14.length     // Catch:{ all -> 0x06c1 }
                if (r15 < r9) goto L_0x0583
                r15 = r14
                goto L_0x0584
            L_0x0583:
                r15 = 0
            L_0x0584:
                if (r15 != 0) goto L_0x0594
                r32 = r0
                byte[] r0 = new byte[r9]     // Catch:{ all -> 0x06c1 }
                r15 = r0
                r14 = r0
                java.lang.ThreadLocal r0 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x06c1 }
                r0.set(r14)     // Catch:{ all -> 0x06c1 }
                goto L_0x0596
            L_0x0594:
                r32 = r0
            L_0x0596:
                r0 = r14
                r14 = 0
                r6.readFully(r15, r14, r9)     // Catch:{ all -> 0x06c1 }
                r6.close()     // Catch:{ all -> 0x06c1 }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r15, (int) r14, (int) r9, (org.telegram.messenger.SecureDocumentKey) r12)     // Catch:{ all -> 0x06c1 }
                byte[] r33 = org.telegram.messenger.Utilities.computeSHA256(r15, r14, r9)     // Catch:{ all -> 0x06c1 }
                r14 = r33
                r33 = 0
                if (r13 == 0) goto L_0x05b1
                boolean r34 = java.util.Arrays.equals(r14, r13)     // Catch:{ all -> 0x06c1 }
                if (r34 != 0) goto L_0x05b3
            L_0x05b1:
                r33 = 1
            L_0x05b3:
                r34 = r0
                r21 = 0
                byte r0 = r15[r21]     // Catch:{ all -> 0x06c1 }
                r0 = r0 & 255(0xff, float:3.57E-43)
                int r9 = r9 - r0
                if (r33 != 0) goto L_0x05c1
                android.graphics.BitmapFactory.decodeByteArray(r15, r0, r9, r4)     // Catch:{ all -> 0x06c1 }
            L_0x05c1:
                goto L_0x05dc
            L_0x05c2:
                r32 = r0
                if (r11 == 0) goto L_0x05d0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x06c1 }
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x06c1 }
                java.io.File r6 = r6.encryptionKeyPath     // Catch:{ all -> 0x06c1 }
                r0.<init>((java.io.File) r10, (java.io.File) r6)     // Catch:{ all -> 0x06c1 }
                goto L_0x05d5
            L_0x05d0:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x06c1 }
                r0.<init>(r10)     // Catch:{ all -> 0x06c1 }
            L_0x05d5:
                r6 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r6, r4)     // Catch:{ all -> 0x06c1 }
                r0.close()     // Catch:{ all -> 0x06c1 }
            L_0x05dc:
                int r0 = r4.outWidth     // Catch:{ all -> 0x06c1 }
                float r0 = (float) r0     // Catch:{ all -> 0x06c1 }
                int r6 = r4.outHeight     // Catch:{ all -> 0x06c1 }
                float r6 = (float) r6     // Catch:{ all -> 0x06c1 }
                int r9 = (r8 > r22 ? 1 : (r8 == r22 ? 0 : -1))
                if (r9 < 0) goto L_0x05f3
                int r9 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r9 <= 0) goto L_0x05f3
                float r9 = r0 / r8
                float r14 = r6 / r22
                float r9 = java.lang.Math.max(r9, r14)     // Catch:{ all -> 0x06c1 }
                goto L_0x05fb
            L_0x05f3:
                float r9 = r0 / r8
                float r14 = r6 / r22
                float r9 = java.lang.Math.min(r9, r14)     // Catch:{ all -> 0x06c1 }
            L_0x05fb:
                r14 = 1067030938(0x3var_a, float:1.2)
                int r14 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r14 >= 0) goto L_0x0604
                r9 = 1065353216(0x3var_, float:1.0)
            L_0x0604:
                r14 = 0
                r4.inJustDecodeBounds = r14     // Catch:{ all -> 0x06c1 }
                int r14 = (r9 > r27 ? 1 : (r9 == r27 ? 0 : -1))
                if (r14 <= 0) goto L_0x0622
                int r14 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                if (r14 > 0) goto L_0x0613
                int r14 = (r6 > r22 ? 1 : (r6 == r22 ? 0 : -1))
                if (r14 <= 0) goto L_0x0622
            L_0x0613:
                r14 = 1
            L_0x0614:
                r15 = 2
                int r14 = r14 * 2
                int r15 = r14 * 2
                float r15 = (float) r15     // Catch:{ all -> 0x06c1 }
                int r15 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1))
                if (r15 < 0) goto L_0x0614
                r4.inSampleSize = r14     // Catch:{ all -> 0x06c1 }
                goto L_0x06c5
            L_0x0622:
                int r14 = (int) r9     // Catch:{ all -> 0x06c1 }
                r4.inSampleSize = r14     // Catch:{ all -> 0x06c1 }
                goto L_0x06c5
            L_0x0627:
                r32 = r0
                r31 = r9
                r30 = r14
                r7 = r15
                goto L_0x06c5
            L_0x0630:
                r0 = move-exception
                r31 = r9
                r30 = r14
                r7 = r15
                r6 = r28
                goto L_0x06d6
            L_0x063a:
                r0 = move-exception
                r29 = r7
                r31 = r9
                r30 = r14
                r7 = r15
                r6 = r28
                goto L_0x06d6
            L_0x0646:
                r28 = r6
                r29 = r7
                r31 = r9
                r30 = r14
                r7 = r15
                if (r2 == 0) goto L_0x06c5
                r6 = 1
                r4.inJustDecodeBounds = r6     // Catch:{ all -> 0x06c1 }
                if (r25 == 0) goto L_0x0659
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x06c1 }
                goto L_0x065b
            L_0x0659:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x06c1 }
            L_0x065b:
                r4.inPreferredConfig = r0     // Catch:{ all -> 0x06c1 }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x06c1 }
                r0.<init>(r10)     // Catch:{ all -> 0x06c1 }
                r6 = 0
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r0, r6, r4)     // Catch:{ all -> 0x06c1 }
                r6 = r9
                r0.close()     // Catch:{ all -> 0x06bd }
                int r9 = r4.outWidth     // Catch:{ all -> 0x06bd }
                int r14 = r4.outHeight     // Catch:{ all -> 0x06bd }
                r15 = 0
                r4.inJustDecodeBounds = r15     // Catch:{ all -> 0x06bd }
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x06bd }
                int r15 = r15.x     // Catch:{ all -> 0x06bd }
                r32 = r0
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x06bd }
                int r0 = r0.y     // Catch:{ all -> 0x06bd }
                int r0 = java.lang.Math.min(r15, r0)     // Catch:{ all -> 0x06bd }
                r15 = 66
                int r0 = java.lang.Math.max(r15, r0)     // Catch:{ all -> 0x06bd }
                int r15 = java.lang.Math.min(r14, r9)     // Catch:{ all -> 0x06bd }
                float r15 = (float) r15
                r28 = r6
                float r6 = (float) r0
                float r15 = r15 / r6
                r6 = 1086324736(0x40CLASSNAME, float:6.0)
                float r15 = r15 * r6
                int r6 = (r15 > r27 ? 1 : (r15 == r27 ? 0 : -1))
                if (r6 >= 0) goto L_0x069d
                r15 = 1065353216(0x3var_, float:1.0)
            L_0x069d:
                int r6 = (r15 > r27 ? 1 : (r15 == r27 ? 0 : -1))
                if (r6 <= 0) goto L_0x06b5
                r6 = 1
            L_0x06a2:
                r18 = 2
                int r6 = r6 * 2
                r33 = r0
                int r0 = r6 * 2
                float r0 = (float) r0
                int r0 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
                if (r0 <= 0) goto L_0x06b2
                r4.inSampleSize = r6     // Catch:{ all -> 0x06c1 }
                goto L_0x06ba
            L_0x06b2:
                r0 = r33
                goto L_0x06a2
            L_0x06b5:
                r33 = r0
                int r0 = (int) r15     // Catch:{ all -> 0x06c1 }
                r4.inSampleSize = r0     // Catch:{ all -> 0x06c1 }
            L_0x06ba:
                r6 = r28
                goto L_0x06c7
            L_0x06bd:
                r0 = move-exception
                r28 = r6
                goto L_0x06d6
            L_0x06c1:
                r0 = move-exception
                r6 = r28
                goto L_0x06d6
            L_0x06c5:
                r6 = r28
            L_0x06c7:
                r9 = r22
                r14 = r23
                goto L_0x06e3
            L_0x06cc:
                r0 = move-exception
                r28 = r6
                r29 = r7
                r31 = r9
                r30 = r14
                r7 = r15
            L_0x06d6:
                r9 = 1
                boolean r14 = r0 instanceof java.io.FileNotFoundException
                if (r14 == 0) goto L_0x06dc
                r9 = 0
            L_0x06dc:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r9)
                r9 = r22
                r14 = r23
            L_0x06e3:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.type
                r22 = r5
                r23 = r6
                r15 = 1
                if (r0 != r15) goto L_0x094c
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0937 }
                long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0937 }
                long unused = r0.lastCacheOutTime = r5     // Catch:{ all -> 0x0937 }
                java.lang.Object r5 = r1.sync     // Catch:{ all -> 0x0937 }
                monitor-enter(r5)     // Catch:{ all -> 0x0937 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0928 }
                if (r0 == 0) goto L_0x0707
                monitor-exit(r5)     // Catch:{ all -> 0x0700 }
                return
            L_0x0700:
                r0 = move-exception
                r35 = r7
                r34 = r9
                goto L_0x092d
            L_0x0707:
                monitor-exit(r5)     // Catch:{ all -> 0x0928 }
                if (r7 == 0) goto L_0x075d
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0937 }
                java.lang.String r5 = "r"
                r0.<init>(r10, r5)     // Catch:{ all -> 0x0937 }
                java.nio.channels.FileChannel r34 = r0.getChannel()     // Catch:{ all -> 0x0937 }
                java.nio.channels.FileChannel$MapMode r35 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0937 }
                r36 = 0
                long r38 = r10.length()     // Catch:{ all -> 0x0937 }
                java.nio.MappedByteBuffer r5 = r34.map(r35, r36, r38)     // Catch:{ all -> 0x0937 }
                android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0937 }
                r6.<init>()     // Catch:{ all -> 0x0937 }
                r15 = 1
                r6.inJustDecodeBounds = r15     // Catch:{ all -> 0x0937 }
                int r15 = r5.limit()     // Catch:{ all -> 0x0937 }
                r35 = r7
                r34 = r9
                r7 = 1
                r9 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r5, r15, r6, r7)     // Catch:{ all -> 0x092f }
                int r7 = r6.outWidth     // Catch:{ all -> 0x092f }
                int r9 = r6.outHeight     // Catch:{ all -> 0x092f }
                android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x092f }
                android.graphics.Bitmap r7 = org.telegram.messenger.Bitmaps.createBitmap(r7, r9, r15)     // Catch:{ all -> 0x092f }
                int r9 = r5.limit()     // Catch:{ all -> 0x0757 }
                boolean r15 = r4.inPurgeable     // Catch:{ all -> 0x0757 }
                if (r15 != 0) goto L_0x074a
                r15 = 1
                goto L_0x074b
            L_0x074a:
                r15 = 0
            L_0x074b:
                r17 = r6
                r6 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r7, r5, r9, r6, r15)     // Catch:{ all -> 0x0757 }
                r0.close()     // Catch:{ all -> 0x0757 }
                r6 = r7
                goto L_0x07f3
            L_0x0757:
                r0 = move-exception
                r6 = r7
                r7 = r29
                goto L_0x0940
            L_0x075d:
                r35 = r7
                r34 = r9
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x092f }
                if (r0 != 0) goto L_0x0788
                if (r12 == 0) goto L_0x0768
                goto L_0x0788
            L_0x0768:
                if (r11 == 0) goto L_0x0774
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x092f }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x092f }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x092f }
                r0.<init>((java.io.File) r10, (java.io.File) r5)     // Catch:{ all -> 0x092f }
                goto L_0x0779
            L_0x0774:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x092f }
                r0.<init>(r10)     // Catch:{ all -> 0x092f }
            L_0x0779:
                r5 = 0
                android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeStream(r0, r5, r4)     // Catch:{ all -> 0x092f }
                r0.close()     // Catch:{ all -> 0x0783 }
                goto L_0x07f3
            L_0x0783:
                r0 = move-exception
                r7 = r29
                goto L_0x0940
            L_0x0788:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x092f }
                java.lang.String r5 = "r"
                r0.<init>(r10, r5)     // Catch:{ all -> 0x092f }
                long r5 = r0.length()     // Catch:{ all -> 0x092f }
                int r6 = (int) r5     // Catch:{ all -> 0x092f }
                r5 = 0
                java.lang.ThreadLocal r7 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x092f }
                java.lang.Object r7 = r7.get()     // Catch:{ all -> 0x092f }
                byte[] r7 = (byte[]) r7     // Catch:{ all -> 0x092f }
                if (r7 == 0) goto L_0x07a6
                int r9 = r7.length     // Catch:{ all -> 0x092f }
                if (r9 < r6) goto L_0x07a6
                r9 = r7
                goto L_0x07a7
            L_0x07a6:
                r9 = 0
            L_0x07a7:
                if (r9 != 0) goto L_0x07b4
                byte[] r15 = new byte[r6]     // Catch:{ all -> 0x092f }
                r9 = r15
                r7 = r15
                java.lang.ThreadLocal r15 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x092f }
                r15.set(r7)     // Catch:{ all -> 0x092f }
            L_0x07b4:
                r15 = 0
                r0.readFully(r9, r15, r6)     // Catch:{ all -> 0x092f }
                r0.close()     // Catch:{ all -> 0x092f }
                r17 = 0
                if (r12 == 0) goto L_0x07dc
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r9, (int) r15, (int) r6, (org.telegram.messenger.SecureDocumentKey) r12)     // Catch:{ all -> 0x092f }
                byte[] r27 = org.telegram.messenger.Utilities.computeSHA256(r9, r15, r6)     // Catch:{ all -> 0x092f }
                r15 = r27
                if (r13 == 0) goto L_0x07d0
                boolean r27 = java.util.Arrays.equals(r15, r13)     // Catch:{ all -> 0x092f }
                if (r27 != 0) goto L_0x07d2
            L_0x07d0:
                r17 = 1
            L_0x07d2:
                r27 = r0
                r21 = 0
                byte r0 = r9[r21]     // Catch:{ all -> 0x092f }
                r5 = r0 & 255(0xff, float:3.57E-43)
                int r6 = r6 - r5
            L_0x07db:
                goto L_0x07e8
            L_0x07dc:
                r27 = r0
                if (r11 == 0) goto L_0x07db
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x092f }
                java.io.File r0 = r0.encryptionKeyPath     // Catch:{ all -> 0x092f }
                r15 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r9, (int) r15, (int) r6, (java.io.File) r0)     // Catch:{ all -> 0x092f }
            L_0x07e8:
                if (r17 != 0) goto L_0x07f0
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeByteArray(r9, r5, r6, r4)     // Catch:{ all -> 0x092f }
                r6 = r0
                goto L_0x07f2
            L_0x07f0:
                r6 = r23
            L_0x07f2:
            L_0x07f3:
                if (r6 != 0) goto L_0x080c
                long r15 = r10.length()     // Catch:{ all -> 0x0783 }
                r17 = 0
                int r0 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1))
                if (r0 == 0) goto L_0x0805
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0783 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0783 }
                if (r0 != 0) goto L_0x0808
            L_0x0805:
                r10.delete()     // Catch:{ all -> 0x0783 }
            L_0x0808:
                r7 = r29
                goto L_0x0943
            L_0x080c:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0783 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0783 }
                if (r0 == 0) goto L_0x0847
                int r0 = r6.getWidth()     // Catch:{ all -> 0x0783 }
                float r0 = (float) r0     // Catch:{ all -> 0x0783 }
                int r5 = r6.getHeight()     // Catch:{ all -> 0x0783 }
                float r5 = (float) r5     // Catch:{ all -> 0x0783 }
                boolean r7 = r4.inPurgeable     // Catch:{ all -> 0x0783 }
                if (r7 != 0) goto L_0x0845
                int r7 = (r8 > r26 ? 1 : (r8 == r26 ? 0 : -1))
                if (r7 == 0) goto L_0x0845
                int r7 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                if (r7 == 0) goto L_0x0845
                r7 = 1101004800(0x41a00000, float:20.0)
                float r15 = r8 + r7
                int r7 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
                if (r7 <= 0) goto L_0x0845
                float r7 = r0 / r8
                int r9 = (int) r8     // Catch:{ all -> 0x0783 }
                float r15 = r5 / r7
                int r15 = (int) r15     // Catch:{ all -> 0x0783 }
                r17 = r5
                r5 = 1
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r9, r15, r5)     // Catch:{ all -> 0x0783 }
                r5 = r9
                if (r6 == r5) goto L_0x0847
                r6.recycle()     // Catch:{ all -> 0x0783 }
                r6 = r5
                goto L_0x0847
            L_0x0845:
                r17 = r5
            L_0x0847:
                if (r24 == 0) goto L_0x0867
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0783 }
                if (r0 == 0) goto L_0x084f
                r0 = 0
                goto L_0x0850
            L_0x084f:
                r0 = 1
            L_0x0850:
                int r5 = r6.getWidth()     // Catch:{ all -> 0x0783 }
                int r7 = r6.getHeight()     // Catch:{ all -> 0x0783 }
                int r9 = r6.getRowBytes()     // Catch:{ all -> 0x0783 }
                int r0 = org.telegram.messenger.Utilities.needInvert(r6, r0, r5, r7, r9)     // Catch:{ all -> 0x0783 }
                if (r0 == 0) goto L_0x0864
                r0 = 1
                goto L_0x0865
            L_0x0864:
                r0 = 0
            L_0x0865:
                r7 = r0
                goto L_0x0869
            L_0x0867:
                r7 = r29
            L_0x0869:
                r5 = 1
                if (r14 != r5) goto L_0x0895
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0892 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0892 }
                if (r0 != r5) goto L_0x0943
                r37 = 3
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0892 }
                if (r0 == 0) goto L_0x087d
                r38 = 0
                goto L_0x087f
            L_0x087d:
                r38 = 1
            L_0x087f:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0892 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0892 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0892 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0892 }
                goto L_0x0943
            L_0x0892:
                r0 = move-exception
                goto L_0x0940
            L_0x0895:
                r5 = 2
                if (r14 != r5) goto L_0x08be
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0892 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0892 }
                if (r0 != r5) goto L_0x0943
                r37 = 1
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0892 }
                if (r0 == 0) goto L_0x08a9
                r38 = 0
                goto L_0x08ab
            L_0x08a9:
                r38 = 1
            L_0x08ab:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0892 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0892 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0892 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0892 }
                goto L_0x0943
            L_0x08be:
                r5 = 3
                if (r14 != r5) goto L_0x091e
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0892 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0892 }
                if (r0 != r5) goto L_0x0943
                r37 = 7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0892 }
                if (r0 == 0) goto L_0x08d2
                r38 = 0
                goto L_0x08d4
            L_0x08d2:
                r38 = 1
            L_0x08d4:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0892 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0892 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0892 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0892 }
                r37 = 7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0892 }
                if (r0 == 0) goto L_0x08ee
                r38 = 0
                goto L_0x08f0
            L_0x08ee:
                r38 = 1
            L_0x08f0:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0892 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0892 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0892 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0892 }
                r37 = 7
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0892 }
                if (r0 == 0) goto L_0x090a
                r38 = 0
                goto L_0x090c
            L_0x090a:
                r38 = 1
            L_0x090c:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0892 }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0892 }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0892 }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0892 }
                goto L_0x0943
            L_0x091e:
                if (r14 != 0) goto L_0x0943
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0892 }
                if (r0 == 0) goto L_0x0943
                org.telegram.messenger.Utilities.pinBitmap(r6)     // Catch:{ all -> 0x0892 }
                goto L_0x0943
            L_0x0928:
                r0 = move-exception
                r35 = r7
                r34 = r9
            L_0x092d:
                monitor-exit(r5)     // Catch:{ all -> 0x0935 }
                throw r0     // Catch:{ all -> 0x092f }
            L_0x092f:
                r0 = move-exception
                r6 = r23
                r7 = r29
                goto L_0x0940
            L_0x0935:
                r0 = move-exception
                goto L_0x092d
            L_0x0937:
                r0 = move-exception
                r35 = r7
                r34 = r9
                r6 = r23
                r7 = r29
            L_0x0940:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0943:
                r16 = r2
                r18 = r3
                r15 = r8
                r9 = r31
                goto L_0x0ce4
            L_0x094c:
                r35 = r7
                r34 = r9
                r0 = 20
                if (r3 == 0) goto L_0x0957
                r0 = 0
                r5 = r0
                goto L_0x0958
            L_0x0957:
                r5 = r0
            L_0x0958:
                if (r5 == 0) goto L_0x09a0
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0992 }
                long r6 = r0.lastCacheOutTime     // Catch:{ all -> 0x0992 }
                r15 = 0
                int r0 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1))
                if (r0 == 0) goto L_0x09a0
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0992 }
                long r6 = r0.lastCacheOutTime     // Catch:{ all -> 0x0992 }
                long r15 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0992 }
                r18 = r8
                long r8 = (long) r5
                long r15 = r15 - r8
                int r0 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1))
                if (r0 <= 0) goto L_0x09a2
                int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0983 }
                r6 = 21
                if (r0 >= r6) goto L_0x09a2
                long r6 = (long) r5     // Catch:{ all -> 0x0983 }
                java.lang.Thread.sleep(r6)     // Catch:{ all -> 0x0983 }
                goto L_0x09a2
            L_0x0983:
                r0 = move-exception
                r16 = r2
                r15 = r18
                r6 = r23
                r7 = r29
                r9 = r31
                r18 = r3
                goto L_0x0ce4
            L_0x0992:
                r0 = move-exception
                r16 = r2
                r18 = r3
                r15 = r8
                r6 = r23
                r7 = r29
                r9 = r31
                goto L_0x0ce4
            L_0x09a0:
                r18 = r8
            L_0x09a2:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0cd7 }
                long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0cd7 }
                long unused = r0.lastCacheOutTime = r6     // Catch:{ all -> 0x0cd7 }
                java.lang.Object r6 = r1.sync     // Catch:{ all -> 0x0cd7 }
                monitor-enter(r6)     // Catch:{ all -> 0x0cd7 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0cc2 }
                if (r0 == 0) goto L_0x09bf
                monitor-exit(r6)     // Catch:{ all -> 0x09b4 }
                return
            L_0x09b4:
                r0 = move-exception
                r16 = r2
                r26 = r5
                r15 = r18
                r18 = r3
                goto L_0x0ccb
            L_0x09bf:
                monitor-exit(r6)     // Catch:{ all -> 0x0cc2 }
                if (r25 != 0) goto L_0x09d8
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0983 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0983 }
                if (r0 == 0) goto L_0x09d8
                if (r14 != 0) goto L_0x09d8
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0983 }
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation     // Catch:{ all -> 0x0983 }
                java.lang.String r0 = r0.path     // Catch:{ all -> 0x0983 }
                if (r0 == 0) goto L_0x09d3
                goto L_0x09d8
            L_0x09d3:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0983 }
                r4.inPreferredConfig = r0     // Catch:{ all -> 0x0983 }
                goto L_0x09dc
            L_0x09d8:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0cd7 }
                r4.inPreferredConfig = r0     // Catch:{ all -> 0x0cd7 }
            L_0x09dc:
                r6 = 0
                r4.inDither = r6     // Catch:{ all -> 0x0cd7 }
                if (r3 == 0) goto L_0x0a07
                if (r2 != 0) goto L_0x0a07
                if (r22 == 0) goto L_0x09f6
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0983 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0983 }
                long r6 = r3.longValue()     // Catch:{ all -> 0x0983 }
                r8 = 1
                android.graphics.Bitmap r0 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r6, r8, r4)     // Catch:{ all -> 0x0983 }
                r6 = r0
                goto L_0x0a09
            L_0x09f6:
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0983 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0983 }
                long r6 = r3.longValue()     // Catch:{ all -> 0x0983 }
                r8 = 1
                android.graphics.Bitmap r0 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r6, r8, r4)     // Catch:{ all -> 0x0983 }
                r6 = r0
                goto L_0x0a09
            L_0x0a07:
                r6 = r23
            L_0x0a09:
                if (r6 != 0) goto L_0x0b37
                if (r35 == 0) goto L_0x0a64
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0a57 }
                java.lang.String r7 = "r"
                r0.<init>(r10, r7)     // Catch:{ all -> 0x0a57 }
                java.nio.channels.FileChannel r36 = r0.getChannel()     // Catch:{ all -> 0x0a57 }
                java.nio.channels.FileChannel$MapMode r37 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0a57 }
                r38 = 0
                long r40 = r10.length()     // Catch:{ all -> 0x0a57 }
                java.nio.MappedByteBuffer r7 = r36.map(r37, r38, r40)     // Catch:{ all -> 0x0a57 }
                android.graphics.BitmapFactory$Options r8 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0a57 }
                r8.<init>()     // Catch:{ all -> 0x0a57 }
                r9 = 1
                r8.inJustDecodeBounds = r9     // Catch:{ all -> 0x0a57 }
                int r15 = r7.limit()     // Catch:{ all -> 0x0a57 }
                r16 = r2
                r2 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r2, r7, r15, r8, r9)     // Catch:{ all -> 0x0b2c }
                int r2 = r8.outWidth     // Catch:{ all -> 0x0b2c }
                int r9 = r8.outHeight     // Catch:{ all -> 0x0b2c }
                android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0b2c }
                android.graphics.Bitmap r2 = org.telegram.messenger.Bitmaps.createBitmap(r2, r9, r15)     // Catch:{ all -> 0x0b2c }
                r6 = r2
                int r2 = r7.limit()     // Catch:{ all -> 0x0b2c }
                boolean r9 = r4.inPurgeable     // Catch:{ all -> 0x0b2c }
                if (r9 != 0) goto L_0x0a4b
                r9 = 1
                goto L_0x0a4c
            L_0x0a4b:
                r9 = 0
            L_0x0a4c:
                r15 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r6, r7, r2, r15, r9)     // Catch:{ all -> 0x0b2c }
                r0.close()     // Catch:{ all -> 0x0b2c }
                r9 = r31
                goto L_0x0b3b
            L_0x0a57:
                r0 = move-exception
                r16 = r2
                r15 = r18
                r7 = r29
                r9 = r31
                r18 = r3
                goto L_0x0ce4
            L_0x0a64:
                r16 = r2
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0b2c }
                if (r0 != 0) goto L_0x0ac1
                if (r12 == 0) goto L_0x0a6e
                r7 = 0
                goto L_0x0ac2
            L_0x0a6e:
                if (r11 == 0) goto L_0x0a7b
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0b2c }
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0b2c }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x0b2c }
                r0.<init>((java.io.File) r10, (java.io.File) r2)     // Catch:{ all -> 0x0b2c }
                r2 = r0
                goto L_0x0a81
            L_0x0a7b:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0b2c }
                r0.<init>(r10)     // Catch:{ all -> 0x0b2c }
                r2 = r0
            L_0x0a81:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0b2c }
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation     // Catch:{ all -> 0x0b2c }
                org.telegram.tgnet.TLRPC$Document r0 = r0.document     // Catch:{ all -> 0x0b2c }
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document     // Catch:{ all -> 0x0b2c }
                if (r0 == 0) goto L_0x0ab4
                androidx.exifinterface.media.ExifInterface r0 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0aa7 }
                r0.<init>((java.io.InputStream) r2)     // Catch:{ all -> 0x0aa7 }
                java.lang.String r7 = "Orientation"
                r8 = 1
                int r7 = r0.getAttributeInt(r7, r8)     // Catch:{ all -> 0x0aa7 }
                switch(r7) {
                    case 3: goto L_0x0aa3;
                    case 6: goto L_0x0aa0;
                    case 8: goto L_0x0a9d;
                    default: goto L_0x0a9a;
                }
            L_0x0a9a:
                r9 = r31
                goto L_0x0aa6
            L_0x0a9d:
                r9 = 270(0x10e, float:3.78E-43)
                goto L_0x0aa6
            L_0x0aa0:
                r9 = 90
                goto L_0x0aa6
            L_0x0aa3:
                r9 = 180(0xb4, float:2.52E-43)
            L_0x0aa6:
                goto L_0x0aaa
            L_0x0aa7:
                r0 = move-exception
                r9 = r31
            L_0x0aaa:
                java.nio.channels.FileChannel r0 = r2.getChannel()     // Catch:{ all -> 0x0b53 }
                r7 = 0
                r0.position(r7)     // Catch:{ all -> 0x0b53 }
                goto L_0x0ab6
            L_0x0ab4:
                r9 = r31
            L_0x0ab6:
                r7 = 0
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r2, r7, r4)     // Catch:{ all -> 0x0b53 }
                r6 = r0
                r2.close()     // Catch:{ all -> 0x0b53 }
                goto L_0x0b3b
            L_0x0ac1:
                r7 = 0
            L_0x0ac2:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0b2c }
                java.lang.String r2 = "r"
                r0.<init>(r10, r2)     // Catch:{ all -> 0x0b2c }
                long r8 = r0.length()     // Catch:{ all -> 0x0b2c }
                int r2 = (int) r8     // Catch:{ all -> 0x0b2c }
                r8 = 0
                java.lang.ThreadLocal r9 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0b2c }
                java.lang.Object r9 = r9.get()     // Catch:{ all -> 0x0b2c }
                byte[] r9 = (byte[]) r9     // Catch:{ all -> 0x0b2c }
                if (r9 == 0) goto L_0x0ae0
                int r15 = r9.length     // Catch:{ all -> 0x0b2c }
                if (r15 < r2) goto L_0x0ae0
                r15 = r9
                goto L_0x0ae1
            L_0x0ae0:
                r15 = r7
            L_0x0ae1:
                if (r15 != 0) goto L_0x0aee
                byte[] r7 = new byte[r2]     // Catch:{ all -> 0x0b2c }
                r15 = r7
                r9 = r7
                java.lang.ThreadLocal r7 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0b2c }
                r7.set(r9)     // Catch:{ all -> 0x0b2c }
            L_0x0aee:
                r7 = 0
                r0.readFully(r15, r7, r2)     // Catch:{ all -> 0x0b2c }
                r0.close()     // Catch:{ all -> 0x0b2c }
                r17 = 0
                if (r12 == 0) goto L_0x0b16
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r15, (int) r7, (int) r2, (org.telegram.messenger.SecureDocumentKey) r12)     // Catch:{ all -> 0x0b2c }
                byte[] r23 = org.telegram.messenger.Utilities.computeSHA256(r15, r7, r2)     // Catch:{ all -> 0x0b2c }
                r7 = r23
                if (r13 == 0) goto L_0x0b0a
                boolean r23 = java.util.Arrays.equals(r7, r13)     // Catch:{ all -> 0x0b2c }
                if (r23 != 0) goto L_0x0b0c
            L_0x0b0a:
                r17 = 1
            L_0x0b0c:
                r23 = r0
                r21 = 0
                byte r0 = r15[r21]     // Catch:{ all -> 0x0b2c }
                r8 = r0 & 255(0xff, float:3.57E-43)
                int r2 = r2 - r8
            L_0x0b15:
                goto L_0x0b22
            L_0x0b16:
                r23 = r0
                if (r11 == 0) goto L_0x0b15
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0b2c }
                java.io.File r0 = r0.encryptionKeyPath     // Catch:{ all -> 0x0b2c }
                r7 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r15, (int) r7, (int) r2, (java.io.File) r0)     // Catch:{ all -> 0x0b2c }
            L_0x0b22:
                if (r17 != 0) goto L_0x0b29
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeByteArray(r15, r8, r2, r4)     // Catch:{ all -> 0x0b2c }
                r6 = r0
            L_0x0b29:
                r9 = r31
                goto L_0x0b3b
            L_0x0b2c:
                r0 = move-exception
                r15 = r18
                r7 = r29
                r9 = r31
                r18 = r3
                goto L_0x0ce4
            L_0x0b37:
                r16 = r2
                r9 = r31
            L_0x0b3b:
                if (r6 != 0) goto L_0x0b66
                if (r30 == 0) goto L_0x0b5c
                long r7 = r10.length()     // Catch:{ all -> 0x0b53 }
                r20 = 0
                int r0 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
                if (r0 == 0) goto L_0x0b4f
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0b53 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0b53 }
                if (r0 != 0) goto L_0x0b5c
            L_0x0b4f:
                r10.delete()     // Catch:{ all -> 0x0b53 }
                goto L_0x0b5c
            L_0x0b53:
                r0 = move-exception
                r15 = r18
                r7 = r29
                r18 = r3
                goto L_0x0ce4
            L_0x0b5c:
                r28 = r9
                r15 = r18
                r7 = r29
                r18 = r3
                goto L_0x0cb5
            L_0x0b66:
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0cb8 }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x0cb8 }
                if (r2 == 0) goto L_0x0c9b
                int r2 = r6.getWidth()     // Catch:{ all -> 0x0cb8 }
                float r2 = (float) r2     // Catch:{ all -> 0x0cb8 }
                int r7 = r6.getHeight()     // Catch:{ all -> 0x0cb8 }
                float r7 = (float) r7     // Catch:{ all -> 0x0cb8 }
                boolean r8 = r4.inPurgeable     // Catch:{ all -> 0x0cb8 }
                if (r8 != 0) goto L_0x0bda
                int r8 = (r18 > r26 ? 1 : (r18 == r26 ? 0 : -1))
                if (r8 == 0) goto L_0x0bda
                int r8 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
                if (r8 == 0) goto L_0x0bda
                r8 = 1101004800(0x41a00000, float:20.0)
                float r8 = r18 + r8
                int r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r8 <= 0) goto L_0x0bda
                int r8 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r8 <= 0) goto L_0x0bb5
                int r8 = (r18 > r34 ? 1 : (r18 == r34 ? 0 : -1))
                if (r8 <= 0) goto L_0x0bb5
                float r8 = r2 / r18
                int r15 = (r8 > r27 ? 1 : (r8 == r27 ? 0 : -1))
                if (r15 <= 0) goto L_0x0bab
                r17 = r0
                r15 = r18
                int r0 = (int) r15
                r18 = r3
                float r3 = r7 / r8
                int r3 = (int) r3
                r26 = r5
                r5 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r0, r3, r5)     // Catch:{ all -> 0x0bff }
                goto L_0x0bb4
            L_0x0bab:
                r17 = r0
                r26 = r5
                r15 = r18
                r18 = r3
                r0 = r6
            L_0x0bb4:
                goto L_0x0bd2
            L_0x0bb5:
                r17 = r0
                r26 = r5
                r15 = r18
                r18 = r3
                float r0 = r7 / r34
                int r3 = (r0 > r27 ? 1 : (r0 == r27 ? 0 : -1))
                if (r3 <= 0) goto L_0x0bd0
                float r3 = r2 / r0
                int r3 = (int) r3     // Catch:{ all -> 0x0bff }
                r5 = r34
                int r8 = (int) r5     // Catch:{ all -> 0x0bff }
                r5 = 1
                android.graphics.Bitmap r3 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r3, r8, r5)     // Catch:{ all -> 0x0bff }
                r0 = r3
                goto L_0x0bd2
            L_0x0bd0:
                r3 = r6
                r0 = r3
            L_0x0bd2:
                if (r6 == r0) goto L_0x0be2
                r6.recycle()     // Catch:{ all -> 0x0bff }
                r3 = r0
                r6 = r3
                goto L_0x0be2
            L_0x0bda:
                r17 = r0
                r26 = r5
                r15 = r18
                r18 = r3
            L_0x0be2:
                if (r6 == 0) goto L_0x0CLASSNAME
                if (r24 == 0) goto L_0x0c3c
                r0 = r6
                int r3 = r6.getWidth()     // Catch:{ all -> 0x0CLASSNAME }
                int r5 = r6.getHeight()     // Catch:{ all -> 0x0CLASSNAME }
                int r8 = r3 * r5
                r23 = r0
                r0 = 22500(0x57e4, float:3.1529E-41)
                if (r8 <= r0) goto L_0x0CLASSNAME
                r0 = 100
                r8 = 0
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r0, r0, r8)     // Catch:{ all -> 0x0bff }
                goto L_0x0CLASSNAME
            L_0x0bff:
                r0 = move-exception
                r7 = r29
                goto L_0x0ce4
            L_0x0CLASSNAME:
                r0 = r23
            L_0x0CLASSNAME:
                boolean r8 = r4.inPurgeable     // Catch:{ all -> 0x0CLASSNAME }
                if (r8 == 0) goto L_0x0c0c
                r8 = 0
                goto L_0x0c0d
            L_0x0c0c:
                r8 = 1
            L_0x0c0d:
                r23 = r3
                int r3 = r0.getWidth()     // Catch:{ all -> 0x0CLASSNAME }
                r27 = r5
                int r5 = r0.getHeight()     // Catch:{ all -> 0x0CLASSNAME }
                r28 = r9
                int r9 = r0.getRowBytes()     // Catch:{ all -> 0x0c2e }
                int r3 = org.telegram.messenger.Utilities.needInvert(r0, r8, r3, r5, r9)     // Catch:{ all -> 0x0c2e }
                if (r3 == 0) goto L_0x0CLASSNAME
                r3 = 1
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r3 = 0
            L_0x0CLASSNAME:
                if (r0 == r6) goto L_0x0CLASSNAME
                r0.recycle()     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0CLASSNAME
            L_0x0c2e:
                r0 = move-exception
                r9 = r28
                r7 = r29
                goto L_0x0ce4
            L_0x0CLASSNAME:
                r0 = move-exception
                r28 = r9
                r7 = r29
                goto L_0x0ce4
            L_0x0c3c:
                r28 = r9
                r3 = r29
            L_0x0CLASSNAME:
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                if (r14 == 0) goto L_0x0c5f
                int r5 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                if (r5 > 0) goto L_0x0c4c
                int r5 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r5 <= 0) goto L_0x0c5f
            L_0x0c4c:
                r5 = 80
                r8 = 0
                android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r5, r5, r8)     // Catch:{ all -> 0x0CLASSNAME }
                r7 = 1117782016(0x42a00000, float:80.0)
                r2 = 1117782016(0x42a00000, float:80.0)
                r6 = r5
                goto L_0x0c5f
            L_0x0CLASSNAME:
                r0 = move-exception
                r7 = r3
                r9 = r28
                goto L_0x0ce4
            L_0x0c5f:
                if (r14 == 0) goto L_0x0CLASSNAME
                int r5 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                if (r5 >= 0) goto L_0x0CLASSNAME
                int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x0CLASSNAME
                android.graphics.Bitmap$Config r0 = r6.getConfig()     // Catch:{ all -> 0x0CLASSNAME }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0CLASSNAME }
                if (r0 != r5) goto L_0x0c8d
                r37 = 3
                boolean r0 = r4.inPurgeable     // Catch:{ all -> 0x0CLASSNAME }
                if (r0 == 0) goto L_0x0c7a
                r38 = 0
                goto L_0x0c7c
            L_0x0c7a:
                r38 = 1
            L_0x0c7c:
                int r39 = r6.getWidth()     // Catch:{ all -> 0x0CLASSNAME }
                int r40 = r6.getHeight()     // Catch:{ all -> 0x0CLASSNAME }
                int r41 = r6.getRowBytes()     // Catch:{ all -> 0x0CLASSNAME }
                r36 = r6
                org.telegram.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0CLASSNAME }
            L_0x0c8d:
                r0 = 1
                r7 = r3
                goto L_0x0ca7
            L_0x0CLASSNAME:
                r7 = r3
                r0 = r17
                goto L_0x0ca7
            L_0x0CLASSNAME:
                r28 = r9
                r0 = r17
                r7 = r29
                goto L_0x0ca7
            L_0x0c9b:
                r17 = r0
                r26 = r5
                r28 = r9
                r15 = r18
                r18 = r3
                r7 = r29
            L_0x0ca7:
                if (r0 != 0) goto L_0x0cb5
                boolean r2 = r4.inPurgeable     // Catch:{ all -> 0x0cb1 }
                if (r2 == 0) goto L_0x0cb5
                org.telegram.messenger.Utilities.pinBitmap(r6)     // Catch:{ all -> 0x0cb1 }
                goto L_0x0cb5
            L_0x0cb1:
                r0 = move-exception
                r9 = r28
                goto L_0x0ce4
            L_0x0cb5:
                r9 = r28
                goto L_0x0ce4
            L_0x0cb8:
                r0 = move-exception
                r28 = r9
                r15 = r18
                r18 = r3
                r7 = r29
                goto L_0x0ce4
            L_0x0cc2:
                r0 = move-exception
                r16 = r2
                r26 = r5
                r15 = r18
                r18 = r3
            L_0x0ccb:
                monitor-exit(r6)     // Catch:{ all -> 0x0cd5 }
                throw r0     // Catch:{ all -> 0x0ccd }
            L_0x0ccd:
                r0 = move-exception
                r6 = r23
                r7 = r29
                r9 = r31
                goto L_0x0ce4
            L_0x0cd5:
                r0 = move-exception
                goto L_0x0ccb
            L_0x0cd7:
                r0 = move-exception
                r16 = r2
                r15 = r18
                r18 = r3
                r6 = r23
                r7 = r29
                r9 = r31
            L_0x0ce4:
                java.lang.Thread.interrupted()
                if (r7 != 0) goto L_0x0cfa
                if (r9 == 0) goto L_0x0cec
                goto L_0x0cfa
            L_0x0cec:
                if (r6 == 0) goto L_0x0cf4
                android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
                r2.<init>(r6)
                goto L_0x0cf5
            L_0x0cf4:
                r2 = 0
            L_0x0cf5:
                r1.onPostExecute(r2)
                goto L_0x0d70
            L_0x0cfa:
                if (r6 == 0) goto L_0x0d02
                org.telegram.messenger.ExtendedBitmapDrawable r2 = new org.telegram.messenger.ExtendedBitmapDrawable
                r2.<init>(r6, r7, r9)
                goto L_0x0d03
            L_0x0d02:
                r2 = 0
            L_0x0d03:
                r1.onPostExecute(r2)
                goto L_0x0d70
            L_0x0d08:
                r0 = 1135869952(0x43b40000, float:360.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r2 = 1142947840(0x44200000, float:640.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0d46
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                java.lang.String r4 = "_"
                java.lang.String[] r3 = r3.split(r4)
                int r4 = r3.length
                r5 = 2
                if (r4 < r5) goto L_0x0d43
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
                goto L_0x0d4a
            L_0x0d43:
                r4 = 0
                r6 = 1
                goto L_0x0d48
            L_0x0d46:
                r4 = 0
                r6 = 1
            L_0x0d48:
                r3 = r2
                r2 = r0
            L_0x0d4a:
                r5 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0d5f }
                java.io.File r0 = r0.finalFilePath     // Catch:{ all -> 0x0d5f }
                org.telegram.messenger.ImageLoader$CacheImage r7 = r1.cacheImage     // Catch:{ all -> 0x0d5f }
                int r7 = r7.imageType     // Catch:{ all -> 0x0d5f }
                r8 = 4
                if (r7 != r8) goto L_0x0d58
                r7 = 1
                goto L_0x0d59
            L_0x0d58:
                r7 = 0
            L_0x0d59:
                android.graphics.Bitmap r0 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r2, (int) r3, (boolean) r7)     // Catch:{ all -> 0x0d5f }
                r5 = r0
                goto L_0x0d63
            L_0x0d5f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0d63:
                if (r5 == 0) goto L_0x0d6b
                android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable
                r0.<init>(r5)
                goto L_0x0d6c
            L_0x0d6b:
                r0 = 0
            L_0x0d6c:
                r1.onPostExecute(r0)
            L_0x0d70:
                return
            L_0x0d71:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0d71 }
                goto L_0x0d75
            L_0x0d74:
                throw r0
            L_0x0d75:
                goto L_0x0d74
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
        public /* synthetic */ void m630x8CLASSNAMEa141(RLottieDrawable lottieDrawable, Canvas canvas, Bitmap bitmap) {
            lottieDrawable.setOnFrameReadyRunnable(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda2(this, lottieDrawable, canvas, bitmap));
            lottieDrawable.setCurrentFrame(lottieDrawable.getFramesCount() - 1, true, true);
        }

        /* renamed from: lambda$loadLastFrame$0$org-telegram-messenger-ImageLoader$CacheOutTask  reason: not valid java name */
        public /* synthetic */ void m629x8c7CLASSNAME(RLottieDrawable lottieDrawable, Canvas canvas, Bitmap bitmap) {
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
        public /* synthetic */ void m632xba899b28(android.graphics.drawable.Drawable r8) {
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
                android.graphics.drawable.BitmapDrawable r0 = r3.getFromLottieCahce(r4)
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
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.m632xba899b28(android.graphics.drawable.Drawable):void");
        }

        /* renamed from: lambda$onPostExecute$2$org-telegram-messenger-ImageLoader$CacheOutTask  reason: not valid java name */
        public /* synthetic */ void m631xbb000127(Drawable toSetFinal, String decrementKetFinal) {
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
    public BitmapDrawable getFromMemCache(String key) {
        BitmapDrawable drawable = this.memCache.get(key);
        if (drawable == null) {
            drawable = this.smallImagesMemCache.get(key);
        }
        if (drawable == null) {
            drawable = this.wallpaperMemCache.get(key);
        }
        if (drawable == null) {
            return getFromLottieCahce(key);
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
        protected int size;
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
        public /* synthetic */ void m628x9483CLASSNAME(Drawable image, ArrayList finalImageReceiverArray, ArrayList finalImageReceiverGuidsArray, String decrementKey) {
            if (image instanceof AnimatedFileDrawable) {
                boolean imageSet = false;
                AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) image;
                int a = 0;
                while (a < finalImageReceiverArray.size()) {
                    ImageReceiver imgView = (ImageReceiver) finalImageReceiverArray.get(a);
                    AnimatedFileDrawable toSet = a == 0 ? fileDrawable : fileDrawable.makeCopy();
                    if (imgView.setImageBitmapByKey(toSet, this.key, this.type, false, ((Integer) finalImageReceiverGuidsArray.get(a)).intValue())) {
                        if (toSet == fileDrawable) {
                            imageSet = true;
                        }
                    } else if (toSet != fileDrawable) {
                        toSet.recycle();
                    }
                    a++;
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
                            b.recycle();
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
                            b.recycle();
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

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
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
        for (int a = 0; a < 3; a++) {
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
                public /* synthetic */ void m623lambda$fileDidUploaded$2$orgtelegrammessengerImageLoader$5(int currentAccount, String location, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda2(currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize));
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidFailedUpload(String location, boolean isEncrypted) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$5$$ExternalSyntheticLambda5(this, currentAccount, location, isEncrypted));
                }

                /* renamed from: lambda$fileDidFailedUpload$4$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m621xaCLASSNAMEfd2(int currentAccount, String location, boolean isEncrypted) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda3(currentAccount, location, isEncrypted));
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidLoaded(String location, File finalFile, Object parentObject, int type) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda6(this, finalFile, location, parentObject, currentAccount, type));
                }

                /* renamed from: lambda$fileDidLoaded$5$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m622lambda$fileDidLoaded$5$orgtelegrammessengerImageLoader$5(File finalFile, String location, Object parentObject, int currentAccount, int type) {
                    if (SharedConfig.saveToGallery && finalFile != null && ((location.endsWith(".mp4") || location.endsWith(".jpg")) && (parentObject instanceof MessageObject) && ((MessageObject) parentObject).getDialogId() >= 0)) {
                        AndroidUtilities.addMediaToGallery(finalFile.toString());
                    }
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileLoaded, location, finalFile);
                    ImageLoader.this.fileDidLoaded(location, finalFile, type);
                }

                public void fileDidFailedLoad(String location, int canceled) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda7(this, location, canceled, currentAccount));
                }

                /* renamed from: lambda$fileDidFailedLoad$6$org-telegram-messenger-ImageLoader$5  reason: not valid java name */
                public /* synthetic */ void m620lambda$fileDidFailedLoad$6$orgtelegrammessengerImageLoader$5(String location, int canceled, int currentAccount) {
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
            public /* synthetic */ void m624lambda$onReceive$0$orgtelegrammessengerImageLoader$6() {
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
    public /* synthetic */ void m611lambda$checkMediaPaths$1$orgtelegrammessengerImageLoader() {
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
                    } catch (Exception e7) {
                        FileLog.e((Throwable) e7);
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
                    } catch (Exception e8) {
                        FileLog.e((Throwable) e8);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e9) {
            FileLog.e((Throwable) e9);
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
        } else if (type == 3) {
            srcFile = new File(from, "000000000_999999_temp.f");
            dstFile = new File(to, "000000000_999999.f");
        } else if (type == 1) {
            srcFile = new File(from, "000000000_999999_temp.f");
            dstFile = new File(to, "000000000_999999.f");
        } else if (type == 2) {
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
            if (getFromLottieCahce(key) != null) {
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
    public /* synthetic */ void m610xd4a49e20(boolean cancelAll, ImageReceiver imageReceiver) {
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
    public void m617lambda$replaceImageInCache$4$orgtelegrammessengerImageLoader(String oldKey, String newKey, ImageLocation newLocation) {
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
            m617lambda$replaceImageInCache$4$orgtelegrammessengerImageLoader(oldKey, newKey, newLocation);
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
    public /* synthetic */ void m609xa371a69b(String key) {
        this.forceLoadingImages.remove(key);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String key, String url, String ext, ImageLocation imageLocation, String filter, int size, int cacheType, int type, int thumb, int guid) {
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
            Runnable loadOperationRunnable = new ImageLoader$$ExternalSyntheticLambda6(this, thumb, url, key, finalTag, imageReceiver, guid, filter, type, imageLocation, i == 0 && imageReceiver.isCurrentKeyQuality(), imageReceiver.getParentObject(), imageReceiver.getQulityThumbDocument(), finalIsNeedsQualityThumb, imageReceiver.isShouldGenerateQualityThumb(), ext, cacheType, size, imageReceiver.getCurrentAccount());
            this.imageLoadQueue.postRunnable(loadOperationRunnable);
            imageReceiver.addLoadingImageRunnable(loadOperationRunnable);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:239:0x0572  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x057d  */
    /* renamed from: lambda$createLoadOperationForImageReceiver$6$org-telegram-messenger-ImageLoader  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m612xaef5b73a(int r31, java.lang.String r32, java.lang.String r33, int r34, org.telegram.messenger.ImageReceiver r35, int r36, java.lang.String r37, int r38, org.telegram.messenger.ImageLocation r39, boolean r40, java.lang.Object r41, org.telegram.tgnet.TLRPC.Document r42, boolean r43, boolean r44, java.lang.String r45, int r46, int r47, int r48) {
        /*
            r30 = this;
            r0 = r30
            r1 = r31
            r2 = r32
            r9 = r33
            r10 = r34
            r11 = r35
            r12 = r37
            r13 = r39
            r14 = r41
            r15 = r45
            r8 = r46
            r7 = r47
            r16 = 0
            r6 = 2
            if (r1 == r6) goto L_0x00b4
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            java.lang.Object r3 = r3.get(r2)
            r5 = r3
            org.telegram.messenger.ImageLoader$CacheImage r5 = (org.telegram.messenger.ImageLoader.CacheImage) r5
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByKeys
            java.lang.Object r3 = r3.get(r9)
            r4 = r3
            org.telegram.messenger.ImageLoader$CacheImage r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4
            android.util.SparseArray<org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByTag
            java.lang.Object r3 = r3.get(r10)
            org.telegram.messenger.ImageLoader$CacheImage r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3
            if (r3 == 0) goto L_0x007d
            if (r3 != r4) goto L_0x0049
            r9 = r36
            r3.setImageReceiverGuid(r11, r9)
            r16 = 1
            r8 = r3
            r18 = r4
            r19 = r5
            r9 = 2
            goto L_0x0083
        L_0x0049:
            r9 = r36
            if (r3 != r5) goto L_0x0070
            if (r4 != 0) goto L_0x0064
            r17 = r3
            r18 = r4
            r4 = r35
            r19 = r5
            r5 = r33
            r9 = 2
            r6 = r37
            r7 = r38
            r8 = r36
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x006b
        L_0x0064:
            r17 = r3
            r18 = r4
            r19 = r5
            r9 = 2
        L_0x006b:
            r16 = 1
            r8 = r17
            goto L_0x0083
        L_0x0070:
            r17 = r3
            r18 = r4
            r19 = r5
            r9 = 2
            r8 = r17
            r8.removeImageReceiver(r11)
            goto L_0x0083
        L_0x007d:
            r8 = r3
            r18 = r4
            r19 = r5
            r9 = 2
        L_0x0083:
            if (r16 != 0) goto L_0x009c
            if (r18 == 0) goto L_0x009c
            r3 = r18
            r4 = r35
            r5 = r33
            r6 = r37
            r7 = r38
            r17 = r8
            r8 = r36
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r3 = 1
            r16 = r3
            goto L_0x009e
        L_0x009c:
            r17 = r8
        L_0x009e:
            if (r16 != 0) goto L_0x00b5
            if (r19 == 0) goto L_0x00b5
            r3 = r19
            r4 = r35
            r5 = r33
            r6 = r37
            r7 = r38
            r8 = r36
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r16 = 1
            goto L_0x00b5
        L_0x00b4:
            r9 = 2
        L_0x00b5:
            if (r16 != 0) goto L_0x070c
            r3 = 0
            r17 = 0
            r4 = 0
            r5 = 0
            java.lang.String r6 = r13.path
            java.lang.String r8 = "athumb"
            java.lang.String r9 = "_"
            r19 = 4
            if (r6 == 0) goto L_0x0134
            java.lang.String r6 = r13.path
            java.lang.String r7 = "http"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x012b
            boolean r7 = r6.startsWith(r8)
            if (r7 != 0) goto L_0x012b
            r3 = 1
            java.lang.String r7 = "thumb://"
            boolean r7 = r6.startsWith(r7)
            r21 = r3
            java.lang.String r3 = ":"
            if (r7 == 0) goto L_0x00ff
            r7 = 8
            int r3 = r6.indexOf(r3, r7)
            if (r3 < 0) goto L_0x00fa
            java.io.File r7 = new java.io.File
            r22 = r4
            int r4 = r3 + 1
            java.lang.String r4 = r6.substring(r4)
            r7.<init>(r4)
            r4 = r7
            goto L_0x00fc
        L_0x00fa:
            r22 = r4
        L_0x00fc:
            r3 = r21
            goto L_0x012f
        L_0x00ff:
            r22 = r4
            java.lang.String r4 = "vthumb://"
            boolean r4 = r6.startsWith(r4)
            if (r4 == 0) goto L_0x0122
            r4 = 9
            int r3 = r6.indexOf(r3, r4)
            if (r3 < 0) goto L_0x011d
            java.io.File r4 = new java.io.File
            int r7 = r3 + 1
            java.lang.String r7 = r6.substring(r7)
            r4.<init>(r7)
            goto L_0x011f
        L_0x011d:
            r4 = r22
        L_0x011f:
            r3 = r21
            goto L_0x012f
        L_0x0122:
            java.io.File r3 = new java.io.File
            r3.<init>(r6)
            r4 = r3
            r3 = r21
            goto L_0x012f
        L_0x012b:
            r22 = r4
            r4 = r22
        L_0x012f:
            r24 = r8
            r15 = r9
            goto L_0x0256
        L_0x0134:
            r22 = r4
            if (r1 != 0) goto L_0x024d
            if (r40 == 0) goto L_0x024d
            r3 = 1
            boolean r4 = r14 instanceof org.telegram.messenger.MessageObject
            if (r4 == 0) goto L_0x015e
            r4 = r14
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Document r6 = r4.getDocument()
            org.telegram.tgnet.TLRPC$Message r7 = r4.messageOwner
            java.lang.String r7 = r7.attachPath
            r21 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3)
            int r23 = r4.getMediaType()
            r4 = 0
            r28 = r23
            r23 = r5
            r5 = r28
            goto L_0x0190
        L_0x015e:
            r21 = r3
            if (r42 == 0) goto L_0x0184
            r6 = r42
            r3 = 1
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r3)
            boolean r3 = org.telegram.messenger.MessageObject.isVideoDocument(r6)
            if (r3 == 0) goto L_0x0173
            r3 = 2
            r23 = r3
            goto L_0x0176
        L_0x0173:
            r3 = 3
            r23 = r3
        L_0x0176:
            r7 = 0
            r3 = 1
            r28 = r4
            r4 = r3
            r3 = r28
            r29 = r23
            r23 = r5
            r5 = r29
            goto L_0x0190
        L_0x0184:
            r6 = 0
            r7 = 0
            r3 = 0
            r23 = 0
            r4 = 0
            r28 = r23
            r23 = r5
            r5 = r28
        L_0x0190:
            if (r6 == 0) goto L_0x0243
            if (r43 == 0) goto L_0x01cd
            r24 = r8
            java.io.File r8 = new java.io.File
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r19)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r15 = "q_"
            r2.append(r15)
            int r15 = r6.dc_id
            r2.append(r15)
            r2.append(r9)
            r15 = r9
            long r9 = r6.id
            r2.append(r9)
            java.lang.String r9 = ".jpg"
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r8.<init>(r14, r2)
            r2 = r8
            boolean r8 = r2.exists()
            if (r8 != 0) goto L_0x01c9
            r2 = 0
            goto L_0x01d2
        L_0x01c9:
            r8 = 1
            r23 = r8
            goto L_0x01d2
        L_0x01cd:
            r24 = r8
            r15 = r9
            r2 = r22
        L_0x01d2:
            r8 = 0
            boolean r9 = android.text.TextUtils.isEmpty(r7)
            if (r9 != 0) goto L_0x01e6
            java.io.File r9 = new java.io.File
            r9.<init>(r7)
            r8 = r9
            boolean r9 = r8.exists()
            if (r9 != 0) goto L_0x01e6
            r8 = 0
        L_0x01e6:
            if (r8 != 0) goto L_0x01e9
            r8 = r3
        L_0x01e9:
            if (r2 != 0) goto L_0x023d
            java.lang.String r9 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r10 = r0.waitingForQualityThumb
            java.lang.Object r10 = r10.get(r9)
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r10 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r10
            if (r10 != 0) goto L_0x020e
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r14 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo
            r15 = 0
            r14.<init>()
            r10 = r14
            org.telegram.tgnet.TLRPC.Document unused = r10.parentDocument = r6
            java.lang.String unused = r10.filter = r12
            boolean unused = r10.big = r4
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r14 = r0.waitingForQualityThumb
            r14.put(r9, r10)
        L_0x020e:
            java.util.ArrayList r14 = r10.imageReceiverArray
            boolean r14 = r14.contains(r11)
            if (r14 != 0) goto L_0x022a
            java.util.ArrayList r14 = r10.imageReceiverArray
            r14.add(r11)
            java.util.ArrayList r14 = r10.imageReceiverGuidsArray
            java.lang.Integer r15 = java.lang.Integer.valueOf(r36)
            r14.add(r15)
        L_0x022a:
            android.util.SparseArray<java.lang.String> r14 = r0.waitingForQualityThumbByTag
            r15 = r34
            r14.put(r15, r9)
            boolean r14 = r8.exists()
            if (r14 == 0) goto L_0x023c
            if (r44 == 0) goto L_0x023c
            r0.generateThumb(r5, r8, r10)
        L_0x023c:
            return
        L_0x023d:
            r4 = r2
            r3 = r21
            r5 = r23
            goto L_0x0256
        L_0x0243:
            r24 = r8
            r15 = r9
            r3 = r21
            r4 = r22
            r5 = r23
            goto L_0x0256
        L_0x024d:
            r23 = r5
            r24 = r8
            r15 = r9
            r4 = r22
            r5 = r23
        L_0x0256:
            r2 = 2
            if (r1 == r2) goto L_0x06fb
            boolean r9 = r39.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r6 = new org.telegram.messenger.ImageLoader$CacheImage
            r7 = 0
            r6.<init>()
            r10 = r6
            if (r40 != 0) goto L_0x02e4
            int r6 = r13.imageType
            if (r6 == r2) goto L_0x02de
            org.telegram.messenger.WebFile r2 = r13.webFile
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r2)
            if (r2 != 0) goto L_0x02de
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r2 != 0) goto L_0x02de
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r2)
            if (r2 != 0) goto L_0x02de
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            boolean r2 = org.telegram.messenger.MessageObject.isVideoSticker(r2)
            if (r2 == 0) goto L_0x028d
            r14 = r45
            goto L_0x02e0
        L_0x028d:
            java.lang.String r2 = r13.path
            if (r2 == 0) goto L_0x02db
            java.lang.String r2 = r13.path
            java.lang.String r6 = "vthumb"
            boolean r6 = r2.startsWith(r6)
            if (r6 != 0) goto L_0x02d8
            java.lang.String r6 = "thumb"
            boolean r6 = r2.startsWith(r6)
            if (r6 != 0) goto L_0x02d8
            java.lang.String r6 = "jpg"
            java.lang.String r6 = getHttpUrlExtension(r2, r6)
            java.lang.String r7 = "webm"
            boolean r7 = r6.equals(r7)
            if (r7 != 0) goto L_0x02d2
            java.lang.String r7 = "mp4"
            boolean r7 = r6.equals(r7)
            if (r7 != 0) goto L_0x02d2
            java.lang.String r7 = "gif"
            boolean r7 = r6.equals(r7)
            if (r7 == 0) goto L_0x02c4
            r14 = r45
            goto L_0x02d4
        L_0x02c4:
            java.lang.String r7 = "tgs"
            r14 = r45
            boolean r7 = r7.equals(r14)
            if (r7 == 0) goto L_0x02e6
            r7 = 1
            r10.imageType = r7
            goto L_0x02e6
        L_0x02d2:
            r14 = r45
        L_0x02d4:
            r7 = 2
            r10.imageType = r7
            goto L_0x02e6
        L_0x02d8:
            r14 = r45
            goto L_0x02e6
        L_0x02db:
            r14 = r45
            goto L_0x02e6
        L_0x02de:
            r14 = r45
        L_0x02e0:
            r2 = 2
            r10.imageType = r2
            goto L_0x02e6
        L_0x02e4:
            r14 = r45
        L_0x02e6:
            if (r4 != 0) goto L_0x0582
            r6 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r13.photoSize
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            java.lang.String r8 = "g"
            if (r7 != 0) goto L_0x0557
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r13.photoSize
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoPathSize
            if (r7 == 0) goto L_0x0306
            r0 = r46
            r21 = r3
            r22 = r4
            r23 = r5
            r25 = r6
            r4 = r8
            r27 = r9
            goto L_0x0564
        L_0x0306:
            org.telegram.messenger.SecureDocument r7 = r13.secureDocument
            if (r7 == 0) goto L_0x0330
            org.telegram.messenger.SecureDocument r7 = r13.secureDocument
            r10.secureDocument = r7
            org.telegram.messenger.SecureDocument r7 = r10.secureDocument
            org.telegram.tgnet.TLRPC$TL_secureFile r7 = r7.secureFile
            int r7 = r7.dc_id
            r15 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r7 != r15) goto L_0x031a
            r7 = 1
            goto L_0x031b
        L_0x031a:
            r7 = 0
        L_0x031b:
            r3 = r7
            java.io.File r7 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r2 = r32
            r7.<init>(r15, r2)
            r4 = r7
            r0 = r46
            r27 = r9
            r9 = r4
            r4 = r8
            goto L_0x056c
        L_0x0330:
            r2 = r32
            boolean r7 = r8.equals(r12)
            r21 = r3
            java.lang.String r3 = ".svg"
            r22 = r4
            java.lang.String r4 = "application/x-tgwallpattern"
            r23 = r5
            java.lang.String r5 = "application/x-tgsticker"
            r25 = r6
            java.lang.String r6 = "application/x-tgsdice"
            if (r7 != 0) goto L_0x0408
            r7 = r46
            if (r7 != 0) goto L_0x035d
            r11 = r47
            if (r11 <= 0) goto L_0x035f
            r26 = r15
            java.lang.String r15 = r13.path
            if (r15 != 0) goto L_0x035f
            if (r9 == 0) goto L_0x0359
            goto L_0x035f
        L_0x0359:
            r27 = r9
            goto L_0x040e
        L_0x035d:
            r11 = r47
        L_0x035f:
            java.io.File r15 = new java.io.File
            r27 = r9
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r15.<init>(r9, r2)
            r9 = r15
            boolean r15 = r9.exists()
            if (r15 == 0) goto L_0x0375
            r15 = 1
            r23 = r15
            goto L_0x0398
        L_0x0375:
            r15 = 2
            if (r7 != r15) goto L_0x0396
            java.io.File r15 = new java.io.File
            r22 = r9
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r19)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            java.lang.String r11 = ".enc"
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r15.<init>(r9, r1)
            r9 = r15
            goto L_0x0398
        L_0x0396:
            r22 = r9
        L_0x0398:
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            if (r1 == 0) goto L_0x03fe
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            boolean r1 = r1 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r1 == 0) goto L_0x03b9
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            org.telegram.messenger.DocumentObject$ThemeDocument r1 = (org.telegram.messenger.DocumentObject.ThemeDocument) r1
            org.telegram.tgnet.TLRPC$Document r3 = r1.wallpaper
            if (r3 != 0) goto L_0x03ac
            r3 = 1
            goto L_0x03ae
        L_0x03ac:
            r3 = r21
        L_0x03ae:
            r4 = 5
            r10.imageType = r4
            r0 = r7
            r4 = r8
            r5 = r23
            r6 = r25
            goto L_0x056c
        L_0x03b9:
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            java.lang.String r1 = r1.mime_type
            boolean r1 = r6.equals(r1)
            if (r1 == 0) goto L_0x03cf
            r1 = 1
            r10.imageType = r1
            r3 = 1
            r0 = r7
            r4 = r8
            r5 = r23
            r6 = r25
            goto L_0x056c
        L_0x03cf:
            r1 = 1
            org.telegram.tgnet.TLRPC$Document r6 = r13.document
            java.lang.String r6 = r6.mime_type
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x03dd
            r10.imageType = r1
            goto L_0x03fe
        L_0x03dd:
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            java.lang.String r1 = r1.mime_type
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x03eb
            r1 = 3
            r10.imageType = r1
            goto L_0x03fe
        L_0x03eb:
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r1 == 0) goto L_0x03fe
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            java.lang.String r1 = org.telegram.messenger.FileLoader.getDocumentFileName(r1)
            boolean r3 = r1.endsWith(r3)
            if (r3 == 0) goto L_0x03fe
            r3 = 3
            r10.imageType = r3
        L_0x03fe:
            r0 = r7
            r4 = r8
            r3 = r21
            r5 = r23
            r6 = r25
            goto L_0x056c
        L_0x0408:
            r7 = r46
            r27 = r9
            r26 = r15
        L_0x040e:
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            java.lang.String r9 = ".temp"
            if (r1 == 0) goto L_0x04d6
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            boolean r11 = r1 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted
            if (r11 == 0) goto L_0x0424
            java.io.File r11 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r11.<init>(r15, r2)
            goto L_0x0440
        L_0x0424:
            boolean r11 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r11 == 0) goto L_0x0435
            java.io.File r11 = new java.io.File
            r15 = 2
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r15)
            r11.<init>(r0, r2)
            goto L_0x0440
        L_0x0435:
            java.io.File r0 = new java.io.File
            r11 = 3
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r11)
            r0.<init>(r15, r2)
            r11 = r0
        L_0x0440:
            boolean r0 = r8.equals(r12)
            if (r0 == 0) goto L_0x0476
            boolean r0 = r11.exists()
            if (r0 != 0) goto L_0x0476
            java.io.File r0 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r22 = r11
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r14 = r1.dc_id
            r11.append(r14)
            r14 = r26
            r11.append(r14)
            r26 = r8
            long r7 = r1.id
            r11.append(r7)
            r11.append(r9)
            java.lang.String r7 = r11.toString()
            r0.<init>(r15, r7)
            r11 = r0
            goto L_0x047c
        L_0x0476:
            r26 = r8
            r22 = r11
            r11 = r22
        L_0x047c:
            boolean r0 = r1 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r0 == 0) goto L_0x048f
            r0 = r1
            org.telegram.messenger.DocumentObject$ThemeDocument r0 = (org.telegram.messenger.DocumentObject.ThemeDocument) r0
            org.telegram.tgnet.TLRPC$Document r3 = r0.wallpaper
            if (r3 != 0) goto L_0x0489
            r3 = 1
            goto L_0x048b
        L_0x0489:
            r3 = r21
        L_0x048b:
            r4 = 5
            r10.imageType = r4
            goto L_0x04cb
        L_0x048f:
            org.telegram.tgnet.TLRPC$Document r0 = r13.document
            java.lang.String r0 = r0.mime_type
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x049e
            r0 = 1
            r10.imageType = r0
            r3 = 1
            goto L_0x04cb
        L_0x049e:
            r0 = 1
            java.lang.String r6 = r1.mime_type
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x04aa
            r10.imageType = r0
            goto L_0x04c9
        L_0x04aa:
            java.lang.String r0 = r1.mime_type
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x04b6
            r0 = 3
            r10.imageType = r0
            goto L_0x04c9
        L_0x04b6:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r0 == 0) goto L_0x04c9
            org.telegram.tgnet.TLRPC$Document r0 = r13.document
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            boolean r3 = r0.endsWith(r3)
            if (r3 == 0) goto L_0x04c9
            r3 = 3
            r10.imageType = r3
        L_0x04c9:
            r3 = r21
        L_0x04cb:
            int r6 = r1.size
            r0 = r46
            r9 = r11
            r5 = r23
            r4 = r26
            goto L_0x056c
        L_0x04d6:
            r14 = r26
            r26 = r8
            org.telegram.messenger.WebFile r0 = r13.webFile
            if (r0 == 0) goto L_0x04f5
            java.io.File r0 = new java.io.File
            r1 = 3
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r0.<init>(r1, r2)
            r9 = r0
            r3 = r21
            r5 = r23
            r6 = r25
            r4 = r26
            r0 = r46
            goto L_0x056c
        L_0x04f5:
            r0 = r46
            r1 = 1
            if (r0 != r1) goto L_0x0504
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r3.<init>(r4, r2)
            goto L_0x050e
        L_0x0504:
            java.io.File r3 = new java.io.File
            r4 = 0
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r5, r2)
        L_0x050e:
            r4 = r26
            boolean r5 = r4.equals(r12)
            if (r5 == 0) goto L_0x054f
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r5 = r13.location
            if (r5 == 0) goto L_0x054f
            boolean r5 = r3.exists()
            if (r5 != 0) goto L_0x054f
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r19)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = r13.location
            long r1 = r8.volume_id
            r7.append(r1)
            r7.append(r14)
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r13.location
            int r1 = r1.local_id
            r7.append(r1)
            r7.append(r9)
            java.lang.String r1 = r7.toString()
            r5.<init>(r6, r1)
            r1 = r5
            r9 = r1
            r3 = r21
            r5 = r23
            r6 = r25
            goto L_0x056c
        L_0x054f:
            r9 = r3
            r3 = r21
            r5 = r23
            r6 = r25
            goto L_0x056c
        L_0x0557:
            r0 = r46
            r21 = r3
            r22 = r4
            r23 = r5
            r25 = r6
            r4 = r8
            r27 = r9
        L_0x0564:
            r1 = 1
            r3 = r1
            r9 = r22
            r5 = r23
            r6 = r25
        L_0x056c:
            boolean r1 = r4.equals(r12)
            if (r1 == 0) goto L_0x057d
            r1 = 2
            r10.imageType = r1
            r10.size = r6
            r3 = 1
            r21 = r3
            r23 = r5
            goto L_0x058e
        L_0x057d:
            r21 = r3
            r23 = r5
            goto L_0x058e
        L_0x0582:
            r0 = r46
            r21 = r3
            r22 = r4
            r23 = r5
            r27 = r9
            r9 = r22
        L_0x058e:
            r1 = r38
            r10.type = r1
            r2 = r33
            r10.key = r2
            r10.filter = r12
            r10.imageLocation = r13
            r11 = r45
            r10.ext = r11
            r14 = r48
            r10.currentAccount = r14
            r15 = r41
            r10.parentObject = r15
            int r3 = r13.imageType
            if (r3 == 0) goto L_0x05ae
            int r3 = r13.imageType
            r10.imageType = r3
        L_0x05ae:
            r8 = 2
            if (r0 != r8) goto L_0x05d0
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r7 = r32
            r5.append(r7)
            java.lang.String r6 = ".enc.key"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r3.<init>(r4, r5)
            r10.encryptionKeyPath = r3
            goto L_0x05d2
        L_0x05d0:
            r7 = r32
        L_0x05d2:
            r3 = r10
            r4 = r35
            r5 = r33
            r6 = r37
            r1 = r0
            r0 = r7
            r18 = 1
            r7 = r38
            r11 = r24
            r20 = 2
            r8 = r36
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r21 != 0) goto L_0x06d5
            if (r23 != 0) goto L_0x06d5
            boolean r3 = r9.exists()
            if (r3 == 0) goto L_0x05f8
            r0 = r30
            r11 = r47
            goto L_0x06d9
        L_0x05f8:
            r10.url = r0
            r8 = r30
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r8.imageLoadingByUrl
            r3.put(r0, r10)
            java.lang.String r3 = r13.path
            if (r3 == 0) goto L_0x065d
            java.lang.String r3 = r13.path
            java.lang.String r3 = org.telegram.messenger.Utilities.MD5(r3)
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r19)
            java.io.File r5 = new java.io.File
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            java.lang.String r7 = "_temp.jpg"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r4, r6)
            r10.tempFilePath = r5
            r10.finalFilePath = r9
            java.lang.String r5 = r13.path
            boolean r5 = r5.startsWith(r11)
            if (r5 == 0) goto L_0x0646
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r5 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r5.<init>(r10)
            r10.artworkTask = r5
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r5 = r8.artworkTasks
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r6 = r10.artworkTask
            r5.add(r6)
            r5 = 0
            r8.runArtworkTasks(r5)
            r11 = r47
            goto L_0x065a
        L_0x0646:
            r5 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r6 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r11 = r47
            r6.<init>(r10, r11)
            r10.httpTask = r6
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r6 = r8.httpTasks
            org.telegram.messenger.ImageLoader$HttpImageTask r7 = r10.httpTask
            r6.add(r7)
            r8.runHttpTasks(r5)
        L_0x065a:
            r0 = r8
            goto L_0x0715
        L_0x065d:
            r11 = r47
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r13.location
            if (r3 == 0) goto L_0x0689
            r3 = r46
            if (r3 != 0) goto L_0x0671
            if (r11 <= 0) goto L_0x066d
            byte[] r4 = r13.key
            if (r4 == 0) goto L_0x0671
        L_0x066d:
            r3 = 1
            r19 = r3
            goto L_0x0673
        L_0x0671:
            r19 = r3
        L_0x0673:
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r48)
            if (r31 == 0) goto L_0x067b
            r7 = 2
            goto L_0x067c
        L_0x067b:
            r7 = 1
        L_0x067c:
            r4 = r39
            r5 = r41
            r6 = r45
            r0 = r8
            r8 = r19
            r3.loadFile(r4, r5, r6, r7, r8)
            goto L_0x06c2
        L_0x0689:
            r0 = r8
            org.telegram.tgnet.TLRPC$Document r3 = r13.document
            if (r3 == 0) goto L_0x069d
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r48)
            org.telegram.tgnet.TLRPC$Document r4 = r13.document
            if (r31 == 0) goto L_0x0698
            r6 = 2
            goto L_0x0699
        L_0x0698:
            r6 = 1
        L_0x0699:
            r3.loadFile(r4, r15, r6, r1)
            goto L_0x06c2
        L_0x069d:
            org.telegram.messenger.SecureDocument r3 = r13.secureDocument
            if (r3 == 0) goto L_0x06b0
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r48)
            org.telegram.messenger.SecureDocument r4 = r13.secureDocument
            if (r31 == 0) goto L_0x06ab
            r6 = 2
            goto L_0x06ac
        L_0x06ab:
            r6 = 1
        L_0x06ac:
            r3.loadFile(r4, r6)
            goto L_0x06c2
        L_0x06b0:
            org.telegram.messenger.WebFile r3 = r13.webFile
            if (r3 == 0) goto L_0x06c2
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r48)
            org.telegram.messenger.WebFile r4 = r13.webFile
            if (r31 == 0) goto L_0x06be
            r6 = 2
            goto L_0x06bf
        L_0x06be:
            r6 = 1
        L_0x06bf:
            r3.loadFile(r4, r6, r1)
        L_0x06c2:
            boolean r3 = r35.isForceLoding()
            if (r3 == 0) goto L_0x0715
            java.util.HashMap<java.lang.String, java.lang.Integer> r3 = r0.forceLoadingImages
            java.lang.String r4 = r10.key
            r5 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3.put(r4, r5)
            goto L_0x0715
        L_0x06d5:
            r0 = r30
            r11 = r47
        L_0x06d9:
            r10.finalFilePath = r9
            r10.imageLocation = r13
            org.telegram.messenger.ImageLoader$CacheOutTask r3 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r3.<init>(r10)
            r10.cacheTask = r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByKeys
            r3.put(r2, r10)
            if (r31 == 0) goto L_0x06f3
            org.telegram.messenger.DispatchQueue r3 = r0.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r4 = r10.cacheTask
            r3.postRunnable(r4)
            goto L_0x0715
        L_0x06f3:
            org.telegram.messenger.DispatchQueue r3 = r0.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r4 = r10.cacheTask
            r3.postRunnable(r4)
            goto L_0x0715
        L_0x06fb:
            r2 = r33
            r15 = r41
            r1 = r46
            r11 = r47
            r14 = r48
            r21 = r3
            r22 = r4
            r23 = r5
            goto L_0x0715
        L_0x070c:
            r2 = r33
            r1 = r46
            r11 = r47
            r15 = r14
            r14 = r48
        L_0x0715:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.m612xaef5b73a(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, java.lang.String, int, int, int):void");
    }

    public void preloadArtwork(String athumbUrl) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda11(this, athumbUrl));
    }

    /* renamed from: lambda$preloadArtwork$7$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m616lambda$preloadArtwork$7$orgtelegrammessengerImageLoader(String athumbUrl) {
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
    /* JADX WARNING: Removed duplicated region for block: B:237:0x04c2  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0181  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0190  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01b2  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01bb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r47) {
        /*
            r46 = this;
            r15 = r46
            r14 = r47
            if (r14 != 0) goto L_0x0007
            return
        L_0x0007:
            r6 = 0
            java.lang.String r7 = r47.getMediaKey()
            int r21 = r47.getNewGuid()
            r9 = 1
            if (r7 == 0) goto L_0x0071
            org.telegram.messenger.ImageLocation r8 = r47.getMediaLocation()
            boolean r0 = r15.useLottieMemChache(r8)
            if (r0 == 0) goto L_0x0023
            android.graphics.drawable.BitmapDrawable r0 = r15.getFromLottieCahce(r7)
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
            r15.cancelLoadingForImageReceiver(r14, r9)
            r3 = 3
            r4 = 1
            r0 = r47
            r1 = r10
            r2 = r7
            r5 = r21
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r6 = 1
            boolean r0 = r47.isForcePreview()
            if (r0 != 0) goto L_0x0071
            return
        L_0x0071:
            java.lang.String r8 = r47.getImageKey()
            if (r6 != 0) goto L_0x00dd
            if (r8 == 0) goto L_0x00dd
            org.telegram.messenger.ImageLocation r10 = r47.getImageLocation()
            r0 = 0
            boolean r1 = r15.useLottieMemChache(r10)
            if (r1 == 0) goto L_0x0088
            android.graphics.drawable.BitmapDrawable r0 = r15.getFromLottieCahce(r8)
        L_0x0088:
            if (r0 != 0) goto L_0x00c2
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            java.lang.Object r1 = r1.get(r8)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x009a
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r8)
        L_0x009a:
            if (r0 != 0) goto L_0x00ac
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            java.lang.Object r1 = r1.get(r8)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x00ac
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.smallImagesMemCache
            r1.moveToFront(r8)
        L_0x00ac:
            if (r0 != 0) goto L_0x00c0
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            java.lang.Object r1 = r1.get(r8)
            r0 = r1
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x00be
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.wallpaperMemCache
            r1.moveToFront(r8)
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
            r15.cancelLoadingForImageReceiver(r14, r9)
            r3 = 0
            r4 = 1
            r0 = r47
            r1 = r11
            r2 = r8
            r5 = r21
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r6 = 1
            boolean r0 = r47.isForcePreview()
            if (r0 != 0) goto L_0x00dd
            if (r7 != 0) goto L_0x00dd
            return
        L_0x00dd:
            r22 = r6
            r6 = 0
            java.lang.String r10 = r47.getThumbKey()
            r11 = 0
            if (r10 == 0) goto L_0x014a
            org.telegram.messenger.ImageLocation r12 = r47.getThumbLocation()
            boolean r0 = r15.useLottieMemChache(r12)
            if (r0 == 0) goto L_0x00f7
            android.graphics.drawable.BitmapDrawable r0 = r15.getFromLottieCahce(r10)
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
            r0 = r47
            r1 = r13
            r2 = r10
            r5 = r21
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r15.cancelLoadingForImageReceiver(r14, r11)
            if (r22 == 0) goto L_0x0146
            boolean r0 = r47.isForcePreview()
            if (r0 == 0) goto L_0x0146
            return
        L_0x0146:
            r6 = 1
            r23 = r6
            goto L_0x014c
        L_0x014a:
            r23 = r6
        L_0x014c:
            r0 = 0
            java.lang.Object r13 = r47.getParentObject()
            org.telegram.tgnet.TLRPC$Document r24 = r47.getQulityThumbDocument()
            org.telegram.messenger.ImageLocation r12 = r47.getThumbLocation()
            java.lang.String r6 = r47.getThumbFilter()
            org.telegram.messenger.ImageLocation r1 = r47.getMediaLocation()
            java.lang.String r5 = r47.getMediaFilter()
            org.telegram.messenger.ImageLocation r25 = r47.getImageLocation()
            java.lang.String r4 = r47.getImageFilter()
            r2 = r25
            if (r2 != 0) goto L_0x019a
            boolean r3 = r47.isNeedsQualityThumb()
            if (r3 == 0) goto L_0x019a
            boolean r3 = r47.isCurrentKeyQuality()
            if (r3 == 0) goto L_0x019a
            boolean r3 = r13 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x0190
            r3 = r13
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r16 = r3.getDocument()
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r16)
            r0 = 1
            r26 = r0
            goto L_0x019c
        L_0x0190:
            if (r24 == 0) goto L_0x019a
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r24)
            r0 = 1
            r26 = r0
            goto L_0x019c
        L_0x019a:
            r26 = r0
        L_0x019c:
            r0 = 0
            r3 = 0
            r16 = 0
            r17 = 0
            r8 = 0
            r10 = 0
            r7 = 0
            r9 = 2
            if (r2 == 0) goto L_0x01af
            int r11 = r2.imageType
            if (r11 != r9) goto L_0x01af
            java.lang.String r11 = "mp4"
            goto L_0x01b0
        L_0x01af:
            r11 = 0
        L_0x01b0:
            if (r1 == 0) goto L_0x01bb
            r20 = r0
            int r0 = r1.imageType
            if (r0 != r9) goto L_0x01bd
            java.lang.String r0 = "mp4"
            goto L_0x01be
        L_0x01bb:
            r20 = r0
        L_0x01bd:
            r0 = 0
        L_0x01be:
            java.lang.String r27 = r47.getExt()
            if (r27 != 0) goto L_0x01c9
            java.lang.String r27 = "jpg"
            r28 = r27
            goto L_0x01cb
        L_0x01c9:
            r28 = r27
        L_0x01cb:
            if (r11 != 0) goto L_0x01d2
            r11 = r28
            r27 = r11
            goto L_0x01d4
        L_0x01d2:
            r27 = r11
        L_0x01d4:
            if (r0 != 0) goto L_0x01db
            r0 = r28
            r29 = r0
            goto L_0x01dd
        L_0x01db:
            r29 = r0
        L_0x01dd:
            r0 = 0
            r31 = r1
            r11 = r2
            r32 = r3
            r33 = r17
            r30 = r20
        L_0x01e7:
            java.lang.String r1 = "jpg"
            java.lang.String r2 = "."
            if (r0 >= r9) goto L_0x038b
            if (r0 != 0) goto L_0x01f5
            r3 = r11
            r17 = r27
            r9 = r17
            goto L_0x01fb
        L_0x01f5:
            r3 = r31
            r17 = r29
            r9 = r17
        L_0x01fb:
            if (r3 != 0) goto L_0x0201
            r20 = r10
            goto L_0x0380
        L_0x0201:
            r20 = r10
            if (r31 == 0) goto L_0x0208
            r10 = r31
            goto L_0x0209
        L_0x0208:
            r10 = r11
        L_0x0209:
            r14 = 0
            java.lang.String r10 = r3.getKey(r13, r10, r14)
            if (r10 != 0) goto L_0x0212
            goto L_0x0380
        L_0x0212:
            if (r31 == 0) goto L_0x0217
            r14 = r31
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
            r1.append(r9)
            java.lang.String r14 = r1.toString()
            java.lang.String r1 = r47.getExt()
            if (r1 != 0) goto L_0x027c
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            byte[] r1 = r1.key
            if (r1 != 0) goto L_0x027c
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            long r1 = r1.volume_id
            r34 = -2147483648(0xfffffffvar_, double:NaN)
            int r15 = (r1 > r34 ? 1 : (r1 == r34 ? 0 : -1))
            if (r15 != 0) goto L_0x0363
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r3.location
            int r1 = r1.local_id
            if (r1 >= 0) goto L_0x0363
        L_0x027c:
            r30 = 1
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
            r1.append(r9)
            java.lang.String r14 = r1.toString()
            goto L_0x0363
        L_0x02c0:
            org.telegram.tgnet.TLRPC$Document r1 = r3.document
            if (r1 == 0) goto L_0x0363
            if (r0 != 0) goto L_0x02da
            if (r26 == 0) goto L_0x02da
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
            r34 = r10
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
            r30 = r2
            r10 = r34
            goto L_0x0363
        L_0x0351:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r2)
            r1.append(r9)
            java.lang.String r14 = r1.toString()
        L_0x0363:
            if (r0 != 0) goto L_0x0369
            r8 = r10
            r32 = r14
            goto L_0x036c
        L_0x0369:
            r7 = r10
            r33 = r14
        L_0x036c:
            if (r3 != r12) goto L_0x0380
            if (r0 != 0) goto L_0x0378
            r1 = 0
            r2 = 0
            r8 = 0
            r11 = r1
            r32 = r8
            r8 = r2
            goto L_0x0380
        L_0x0378:
            r1 = 0
            r2 = 0
            r7 = 0
            r31 = r1
            r33 = r7
            r7 = r2
        L_0x0380:
            int r0 = r0 + 1
            r9 = 2
            r15 = r46
            r14 = r47
            r10 = r20
            goto L_0x01e7
        L_0x038b:
            r20 = r10
            if (r12 == 0) goto L_0x040e
            org.telegram.messenger.ImageLocation r0 = r47.getStrippedLocation()
            if (r0 != 0) goto L_0x039d
            if (r31 == 0) goto L_0x039a
            r3 = r31
            goto L_0x039c
        L_0x039a:
            r3 = r25
        L_0x039c:
            r0 = r3
        L_0x039d:
            r3 = 0
            java.lang.String r10 = r12.getKey(r13, r0, r3)
            r3 = 1
            java.lang.String r9 = r12.getKey(r13, r0, r3)
            java.lang.String r14 = r12.path
            if (r14 == 0) goto L_0x03c8
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r9)
            r14.append(r2)
            java.lang.String r2 = r12.path
            java.lang.String r1 = getHttpUrlExtension(r2, r1)
            r14.append(r1)
            java.lang.String r16 = r14.toString()
            r15 = r28
            r28 = r16
            goto L_0x0415
        L_0x03c8:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r12.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r1 != 0) goto L_0x03f7
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r12.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoPathSize
            if (r1 == 0) goto L_0x03d7
            r15 = r28
            goto L_0x03f9
        L_0x03d7:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r12.location
            if (r1 == 0) goto L_0x03f2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r2)
            r15 = r28
            r1.append(r15)
            java.lang.String r16 = r1.toString()
            r28 = r16
            goto L_0x0415
        L_0x03f2:
            r15 = r28
            r28 = r9
            goto L_0x0415
        L_0x03f7:
            r15 = r28
        L_0x03f9:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r2)
            r1.append(r15)
            java.lang.String r16 = r1.toString()
            r28 = r16
            goto L_0x0415
        L_0x040e:
            r15 = r28
            r3 = 1
            r28 = r16
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
            r34 = r7
            goto L_0x0432
        L_0x0430:
            r34 = r7
        L_0x0432:
            if (r8 == 0) goto L_0x0448
            if (r4 == 0) goto L_0x0448
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            r1.append(r0)
            r1.append(r4)
            java.lang.String r8 = r1.toString()
        L_0x0448:
            if (r10 == 0) goto L_0x0461
            if (r6 == 0) goto L_0x0461
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            r1.append(r0)
            r1.append(r6)
            java.lang.String r10 = r1.toString()
            r35 = r10
            goto L_0x0463
        L_0x0461:
            r35 = r10
        L_0x0463:
            java.lang.String r0 = r47.getUniqKeyPrefix()
            if (r0 == 0) goto L_0x047f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r47.getUniqKeyPrefix()
            r0.append(r1)
            r0.append(r8)
            java.lang.String r8 = r0.toString()
            r36 = r8
            goto L_0x0481
        L_0x047f:
            r36 = r8
        L_0x0481:
            if (r11 == 0) goto L_0x04c2
            java.lang.String r0 = r11.path
            if (r0 == 0) goto L_0x04c2
            r7 = 0
            r8 = 1
            r9 = 1
            if (r23 == 0) goto L_0x048e
            r10 = 2
            goto L_0x048f
        L_0x048e:
            r10 = 1
        L_0x048f:
            r0 = r46
            r1 = r47
            r2 = r35
            r3 = r28
            r37 = r4
            r4 = r15
            r38 = r5
            r5 = r12
            r39 = r6
            r40 = r11
            r11 = r21
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r7 = r47.getSize()
            r8 = 1
            r9 = 0
            r10 = 0
            r2 = r36
            r3 = r32
            r4 = r27
            r5 = r40
            r6 = r37
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r43 = r12
            r44 = r13
            r45 = r15
            goto L_0x0591
        L_0x04c2:
            r37 = r4
            r38 = r5
            r39 = r6
            r40 = r11
            if (r31 == 0) goto L_0x0540
            int r0 = r47.getCacheType()
            r41 = 1
            if (r0 != 0) goto L_0x04da
            if (r30 == 0) goto L_0x04da
            r0 = 1
            r42 = r0
            goto L_0x04dc
        L_0x04da:
            r42 = r0
        L_0x04dc:
            if (r42 != 0) goto L_0x04e0
            r8 = 1
            goto L_0x04e2
        L_0x04e0:
            r8 = r42
        L_0x04e2:
            if (r23 != 0) goto L_0x04f8
            r7 = 0
            r9 = 1
            r10 = 1
            r0 = r46
            r1 = r47
            r2 = r35
            r3 = r28
            r4 = r15
            r5 = r12
            r6 = r39
            r11 = r21
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x04f8:
            if (r22 != 0) goto L_0x051c
            r16 = 0
            r18 = 0
            r19 = 0
            r9 = r46
            r10 = r47
            r11 = r36
            r43 = r12
            r12 = r32
            r44 = r13
            r13 = r27
            r14 = r40
            r45 = r15
            r15 = r37
            r17 = r41
            r20 = r21
            r9.createLoadOperationForImageReceiver(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0522
        L_0x051c:
            r43 = r12
            r44 = r13
            r45 = r15
        L_0x0522:
            int r16 = r47.getSize()
            r18 = 3
            r19 = 0
            r9 = r46
            r10 = r47
            r11 = r34
            r12 = r33
            r13 = r29
            r14 = r31
            r15 = r38
            r17 = r42
            r20 = r21
            r9.createLoadOperationForImageReceiver(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0591
        L_0x0540:
            r43 = r12
            r44 = r13
            r45 = r15
            int r0 = r47.getCacheType()
            if (r0 != 0) goto L_0x0552
            if (r30 == 0) goto L_0x0552
            r0 = 1
            r41 = r0
            goto L_0x0554
        L_0x0552:
            r41 = r0
        L_0x0554:
            if (r41 != 0) goto L_0x0558
            r8 = 1
            goto L_0x055a
        L_0x0558:
            r8 = r41
        L_0x055a:
            r7 = 0
            r9 = 1
            if (r23 == 0) goto L_0x0560
            r10 = 2
            goto L_0x0561
        L_0x0560:
            r10 = 1
        L_0x0561:
            r0 = r46
            r1 = r47
            r2 = r35
            r3 = r28
            r4 = r45
            r5 = r43
            r6 = r39
            r11 = r21
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r16 = r47.getSize()
            r18 = 0
            r19 = 0
            r9 = r46
            r10 = r47
            r11 = r36
            r12 = r32
            r13 = r27
            r14 = r40
            r15 = r37
            r17 = r41
            r20 = r21
            r9.createLoadOperationForImageReceiver(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
        L_0x0591:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public BitmapDrawable getFromLottieCahce(String imageKey) {
        BitmapDrawable drawable = this.lottieMemCache.get(imageKey);
        if (!(drawable instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) drawable).isRecycled()) {
            return drawable;
        }
        this.lottieMemCache.remove(imageKey);
        return null;
    }

    private boolean useLottieMemChache(ImageLocation imageLocation) {
        return imageLocation != null && (MessageObject.isAnimatedStickerDocument(imageLocation.document, true) || imageLocation.imageType == 1 || MessageObject.isVideoSticker(imageLocation.document));
    }

    /* access modifiers changed from: private */
    public void httpFileLoadError(String location) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda10(this, location));
    }

    /* renamed from: lambda$httpFileLoadError$8$org-telegram-messenger-ImageLoader  reason: not valid java name */
    public /* synthetic */ void m615lambda$httpFileLoadError$8$orgtelegrammessengerImageLoader(String location) {
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
    public /* synthetic */ void m608lambda$artworkLoadError$9$orgtelegrammessengerImageLoader(String location) {
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
    public /* synthetic */ void m614lambda$fileDidLoaded$10$orgtelegrammessengerImageLoader(String location, int mediaType, File finalFile) {
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
    public /* synthetic */ void m613lambda$fileDidFailedLoad$11$orgtelegrammessengerImageLoader(String location) {
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
    public /* synthetic */ void m619xCLASSNAMEfbb45(HttpFileTask oldTask, int reason) {
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
    public /* synthetic */ void m618x531082e6(HttpFileTask newTask) {
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
                RandomAccessFile f = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
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
        boolean z = true;
        if (scaleFactor > 1.0f || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap2, w, h, true);
        } else {
            int i2 = w;
            int i3 = h;
            scaledBitmap = bitmap;
        }
        if (photoSize2 == null) {
            z = false;
        }
        boolean z2 = z;
        if (photoSize2 == null || !(photoSize2.location instanceof TLRPC.TL_fileLocationToBeDeprecated)) {
            TLRPC.TL_fileLocationToBeDeprecated location2 = new TLRPC.TL_fileLocationToBeDeprecated();
            location2.volume_id = -2147483648L;
            location2.dc_id = Integer.MIN_VALUE;
            location2.local_id = SharedConfig.getLastLocalId();
            location2.file_reference = new byte[0];
            photoSize2 = new TLRPC.TL_photoSize_layer127();
            photoSize2.location = location2;
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
            location = location2;
        } else {
            location = (TLRPC.TL_fileLocationToBeDeprecated) photoSize2.location;
        }
        String fileName = location.volume_id + "_" + location.local_id + ".jpg";
        if (forceCacheDir) {
            fileDir = FileLoader.getDirectory(4);
        } else {
            fileDir = location.volume_id != -2147483648L ? FileLoader.getDirectory(0) : FileLoader.getDirectory(4);
        }
        File cacheFile = new File(fileDir, fileName);
        if (compressFormat2 != Bitmap.CompressFormat.JPEG || !progressive || !BuildVars.DEBUG_VERSION) {
            File file = fileDir;
            FileOutputStream stream = new FileOutputStream(cacheFile);
            scaledBitmap.compress(compressFormat2, i, stream);
            if (!cache) {
                photoSize2.size = (int) stream.getChannel().size();
            }
            stream.close();
        } else {
            File file2 = cacheFile;
            File file3 = fileDir;
            photoSize2.size = Utilities.saveProgressiveJpeg(scaledBitmap, scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap.getRowBytes(), quality, cacheFile.getAbsolutePath());
        }
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
            File file2 = FileLoader.getPathToAttach(photoSize, true);
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
                        Utilities.aesCtrDecryptionByteArray(photoSize.bytes, encryptKey, encryptIv, 0, photoSize.bytes.length, 0);
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
            File file = FileLoader.getPathToAttach(photoSize, true);
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
                    objArr[0] = ImageLocation.getStippedKey(message2, message2, size);
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
