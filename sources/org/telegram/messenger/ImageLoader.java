package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
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
    public ConcurrentHashMap<String, Float> fileProgresses = new ConcurrentHashMap<>();
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
    public long lastProgressUpdateTime;
    /* access modifiers changed from: private */
    public LruCache<RLottieDrawable> lottieMemCache;
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> memCache;
    private HashMap<String, String> replacedBitmaps = new HashMap<>();
    private HashMap<String, Runnable> retryHttpsTasks;
    /* access modifiers changed from: private */
    public File telegramPath;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, WebFile> testWebFile;
    /* access modifiers changed from: private */
    public HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap<>();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap<>();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray<>();

    public void putThumbsToCache(ArrayList<MessageThumb> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            putImageToCache(arrayList.get(i).drawable, arrayList.get(i).key);
        }
    }

    private class ThumbGenerateInfo {
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

        public HttpFileTask(String str, File file, String str2, int i) {
            this.url = str;
            this.tempFile = file;
            this.ext = str2;
            this.currentAccount = i;
        }

        private void reportProgress(float f) {
            long currentTimeMillis = System.currentTimeMillis();
            if (f != 1.0f) {
                long j = this.lastProgressTime;
                if (j != 0 && j >= currentTimeMillis - 500) {
                    return;
                }
            }
            this.lastProgressTime = currentTimeMillis;
            Utilities.stageQueue.postRunnable(new Runnable(f) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$reportProgress$1$ImageLoader$HttpFileTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpFileTask(float f) {
            ImageLoader.this.fileProgresses.put(this.url, Float.valueOf(f));
            AndroidUtilities.runOnUIThread(new Runnable(f) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$null$0$ImageLoader$HttpFileTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpFileTask(float f) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.url, Float.valueOf(f));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0123, code lost:
            if (r5 != -1) goto L_0x0134;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:0x0127, code lost:
            if (r9.fileSize == 0) goto L_0x013b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x0129, code lost:
            reportProgress(1.0f);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x012f, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:0x0130, code lost:
            r3 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:0x0132, code lost:
            r0 = e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:89:0x0134, code lost:
            r1 = false;
         */
        /* JADX WARNING: Removed duplicated region for block: B:102:0x0148 A[Catch:{ all -> 0x0150 }] */
        /* JADX WARNING: Removed duplicated region for block: B:106:0x0156 A[SYNTHETIC, Splitter:B:106:0x0156] */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0076  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A[SYNTHETIC, Splitter:B:43:0x00ad] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r10) {
            /*
                r9 = this;
                java.lang.String r10 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                java.lang.String r0 = "User-Agent"
                r1 = 1
                r2 = 0
                r3 = 0
                java.net.URL r4 = new java.net.URL     // Catch:{ all -> 0x006f }
                java.lang.String r5 = r9.url     // Catch:{ all -> 0x006f }
                r4.<init>(r5)     // Catch:{ all -> 0x006f }
                java.net.URLConnection r4 = r4.openConnection()     // Catch:{ all -> 0x006f }
                r4.addRequestProperty(r0, r10)     // Catch:{ all -> 0x006c }
                r5 = 5000(0x1388, float:7.006E-42)
                r4.setConnectTimeout(r5)     // Catch:{ all -> 0x006c }
                r4.setReadTimeout(r5)     // Catch:{ all -> 0x006c }
                boolean r5 = r4 instanceof java.net.HttpURLConnection     // Catch:{ all -> 0x006c }
                if (r5 == 0) goto L_0x0054
                r5 = r4
                java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5     // Catch:{ all -> 0x006c }
                r5.setInstanceFollowRedirects(r1)     // Catch:{ all -> 0x006c }
                int r6 = r5.getResponseCode()     // Catch:{ all -> 0x006c }
                r7 = 302(0x12e, float:4.23E-43)
                if (r6 == r7) goto L_0x0037
                r7 = 301(0x12d, float:4.22E-43)
                if (r6 == r7) goto L_0x0037
                r7 = 303(0x12f, float:4.25E-43)
                if (r6 != r7) goto L_0x0054
            L_0x0037:
                java.lang.String r6 = "Location"
                java.lang.String r6 = r5.getHeaderField(r6)     // Catch:{ all -> 0x006c }
                java.lang.String r7 = "Set-Cookie"
                java.lang.String r5 = r5.getHeaderField(r7)     // Catch:{ all -> 0x006c }
                java.net.URL r7 = new java.net.URL     // Catch:{ all -> 0x006c }
                r7.<init>(r6)     // Catch:{ all -> 0x006c }
                java.net.URLConnection r4 = r7.openConnection()     // Catch:{ all -> 0x006c }
                java.lang.String r6 = "Cookie"
                r4.setRequestProperty(r6, r5)     // Catch:{ all -> 0x006c }
                r4.addRequestProperty(r0, r10)     // Catch:{ all -> 0x006c }
            L_0x0054:
                r4.connect()     // Catch:{ all -> 0x006c }
                java.io.InputStream r10 = r4.getInputStream()     // Catch:{ all -> 0x006c }
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0067 }
                java.io.File r5 = r9.tempFile     // Catch:{ all -> 0x0067 }
                java.lang.String r6 = "rws"
                r0.<init>(r5, r6)     // Catch:{ all -> 0x0067 }
                r9.fileOutputStream = r0     // Catch:{ all -> 0x0067 }
                goto L_0x00a9
            L_0x0067:
                r0 = move-exception
                r8 = r0
                r0 = r10
                r10 = r8
                goto L_0x0072
            L_0x006c:
                r10 = move-exception
                r0 = r2
                goto L_0x0072
            L_0x006f:
                r10 = move-exception
                r0 = r2
                r4 = r0
            L_0x0072:
                boolean r5 = r10 instanceof java.net.SocketTimeoutException
                if (r5 == 0) goto L_0x007f
                boolean r5 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r5 == 0) goto L_0x00a5
                r9.canRetry = r3
                goto L_0x00a5
            L_0x007f:
                boolean r5 = r10 instanceof java.net.UnknownHostException
                if (r5 == 0) goto L_0x0086
                r9.canRetry = r3
                goto L_0x00a5
            L_0x0086:
                boolean r5 = r10 instanceof java.net.SocketException
                if (r5 == 0) goto L_0x009f
                java.lang.String r5 = r10.getMessage()
                if (r5 == 0) goto L_0x00a5
                java.lang.String r5 = r10.getMessage()
                java.lang.String r6 = "ECONNRESET"
                boolean r5 = r5.contains(r6)
                if (r5 == 0) goto L_0x00a5
                r9.canRetry = r3
                goto L_0x00a5
            L_0x009f:
                boolean r5 = r10 instanceof java.io.FileNotFoundException
                if (r5 == 0) goto L_0x00a5
                r9.canRetry = r3
            L_0x00a5:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
                r10 = r0
            L_0x00a9:
                boolean r0 = r9.canRetry
                if (r0 == 0) goto L_0x015e
                boolean r0 = r4 instanceof java.net.HttpURLConnection     // Catch:{ Exception -> 0x00c7 }
                if (r0 == 0) goto L_0x00cb
                r0 = r4
                java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x00c7 }
                int r0 = r0.getResponseCode()     // Catch:{ Exception -> 0x00c7 }
                r5 = 200(0xc8, float:2.8E-43)
                if (r0 == r5) goto L_0x00cb
                r5 = 202(0xca, float:2.83E-43)
                if (r0 == r5) goto L_0x00cb
                r5 = 304(0x130, float:4.26E-43)
                if (r0 == r5) goto L_0x00cb
                r9.canRetry = r3     // Catch:{ Exception -> 0x00c7 }
                goto L_0x00cb
            L_0x00c7:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00cb:
                if (r4 == 0) goto L_0x00fa
                java.util.Map r0 = r4.getHeaderFields()     // Catch:{ Exception -> 0x00f6 }
                if (r0 == 0) goto L_0x00fa
                java.lang.String r4 = "content-Length"
                java.lang.Object r0 = r0.get(r4)     // Catch:{ Exception -> 0x00f6 }
                java.util.List r0 = (java.util.List) r0     // Catch:{ Exception -> 0x00f6 }
                if (r0 == 0) goto L_0x00fa
                boolean r4 = r0.isEmpty()     // Catch:{ Exception -> 0x00f6 }
                if (r4 != 0) goto L_0x00fa
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x00f6 }
                java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x00f6 }
                if (r0 == 0) goto L_0x00fa
                java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x00f6 }
                int r0 = r0.intValue()     // Catch:{ Exception -> 0x00f6 }
                r9.fileSize = r0     // Catch:{ Exception -> 0x00f6 }
                goto L_0x00fa
            L_0x00f6:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00fa:
                if (r10 == 0) goto L_0x0144
                r0 = 32768(0x8000, float:4.5918E-41)
                byte[] r0 = new byte[r0]     // Catch:{ all -> 0x0140 }
                r4 = 0
            L_0x0102:
                boolean r5 = r9.isCancelled()     // Catch:{ all -> 0x0140 }
                if (r5 == 0) goto L_0x0109
                goto L_0x0134
            L_0x0109:
                int r5 = r10.read(r0)     // Catch:{ Exception -> 0x0136 }
                if (r5 <= 0) goto L_0x0122
                java.io.RandomAccessFile r6 = r9.fileOutputStream     // Catch:{ Exception -> 0x0136 }
                r6.write(r0, r3, r5)     // Catch:{ Exception -> 0x0136 }
                int r4 = r4 + r5
                int r5 = r9.fileSize     // Catch:{ Exception -> 0x0136 }
                if (r5 <= 0) goto L_0x0102
                float r5 = (float) r4     // Catch:{ Exception -> 0x0136 }
                int r6 = r9.fileSize     // Catch:{ Exception -> 0x0136 }
                float r6 = (float) r6     // Catch:{ Exception -> 0x0136 }
                float r5 = r5 / r6
                r9.reportProgress(r5)     // Catch:{ Exception -> 0x0136 }
                goto L_0x0102
            L_0x0122:
                r0 = -1
                if (r5 != r0) goto L_0x0134
                int r0 = r9.fileSize     // Catch:{ Exception -> 0x0132, all -> 0x012f }
                if (r0 == 0) goto L_0x013b
                r0 = 1065353216(0x3var_, float:1.0)
                r9.reportProgress(r0)     // Catch:{ Exception -> 0x0132, all -> 0x012f }
                goto L_0x013b
            L_0x012f:
                r0 = move-exception
                r3 = 1
                goto L_0x0141
            L_0x0132:
                r0 = move-exception
                goto L_0x0138
            L_0x0134:
                r1 = 0
                goto L_0x013b
            L_0x0136:
                r0 = move-exception
                r1 = 0
            L_0x0138:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x013d }
            L_0x013b:
                r3 = r1
                goto L_0x0144
            L_0x013d:
                r0 = move-exception
                r3 = r1
                goto L_0x0141
            L_0x0140:
                r0 = move-exception
            L_0x0141:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0144:
                java.io.RandomAccessFile r0 = r9.fileOutputStream     // Catch:{ all -> 0x0150 }
                if (r0 == 0) goto L_0x0154
                java.io.RandomAccessFile r0 = r9.fileOutputStream     // Catch:{ all -> 0x0150 }
                r0.close()     // Catch:{ all -> 0x0150 }
                r9.fileOutputStream = r2     // Catch:{ all -> 0x0150 }
                goto L_0x0154
            L_0x0150:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0154:
                if (r10 == 0) goto L_0x015e
                r10.close()     // Catch:{ all -> 0x015a }
                goto L_0x015e
            L_0x015a:
                r10 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
            L_0x015e:
                java.lang.Boolean r10 = java.lang.Boolean.valueOf(r3)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.HttpFileTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            ImageLoader.this.runHttpFileLoadTasks(this, bool.booleanValue() ? 2 : 1);
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
        /* JADX WARNING: Code restructure failed: missing block: B:112:0x014c, code lost:
            if (r2 == null) goto L_0x014f;
         */
        /* JADX WARNING: Removed duplicated region for block: B:105:0x013b A[Catch:{ all -> 0x0141 }] */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0144 A[SYNTHETIC, Splitter:B:108:0x0144] */
        /* JADX WARNING: Removed duplicated region for block: B:127:0x0168 A[SYNTHETIC, Splitter:B:127:0x0168] */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x0105 A[Catch:{ all -> 0x0150, all -> 0x015b, all -> 0x0162 }] */
        /* JADX WARNING: Removed duplicated region for block: B:88:0x010e A[Catch:{ all -> 0x0150, all -> 0x015b, all -> 0x0162 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String doInBackground(java.lang.Void... r8) {
            /*
                r7 = this;
                r8 = 0
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r1 = r7.cacheImage     // Catch:{ all -> 0x00fe }
                org.telegram.messenger.ImageLocation r1 = r1.imageLocation     // Catch:{ all -> 0x00fe }
                java.lang.String r1 = r1.path     // Catch:{ all -> 0x00fe }
                java.net.URL r2 = new java.net.URL     // Catch:{ all -> 0x00fe }
                java.lang.String r3 = "athumb://"
                java.lang.String r4 = "https://"
                java.lang.String r1 = r1.replace(r3, r4)     // Catch:{ all -> 0x00fe }
                r2.<init>(r1)     // Catch:{ all -> 0x00fe }
                java.net.URLConnection r1 = r2.openConnection()     // Catch:{ all -> 0x00fe }
                java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch:{ all -> 0x00fe }
                r7.httpConnection = r1     // Catch:{ all -> 0x00fe }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00fe }
                java.lang.String r2 = "User-Agent"
                java.lang.String r3 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r1.addRequestProperty(r2, r3)     // Catch:{ all -> 0x00fe }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00fe }
                r2 = 5000(0x1388, float:7.006E-42)
                r1.setConnectTimeout(r2)     // Catch:{ all -> 0x00fe }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00fe }
                r1.setReadTimeout(r2)     // Catch:{ all -> 0x00fe }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00fe }
                r1.connect()     // Catch:{ all -> 0x00fe }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ Exception -> 0x0050 }
                if (r1 == 0) goto L_0x0054
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ Exception -> 0x0050 }
                int r1 = r1.getResponseCode()     // Catch:{ Exception -> 0x0050 }
                r2 = 200(0xc8, float:2.8E-43)
                if (r1 == r2) goto L_0x0054
                r2 = 202(0xca, float:2.83E-43)
                if (r1 == r2) goto L_0x0054
                r2 = 304(0x130, float:4.26E-43)
                if (r1 == r2) goto L_0x0054
                r7.canRetry = r0     // Catch:{ Exception -> 0x0050 }
                goto L_0x0054
            L_0x0050:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x00fe }
            L_0x0054:
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00fe }
                java.io.InputStream r1 = r1.getInputStream()     // Catch:{ all -> 0x00fe }
                java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00f9 }
                r2.<init>()     // Catch:{ all -> 0x00f9 }
                r3 = 32768(0x8000, float:4.5918E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x00f4 }
            L_0x0064:
                boolean r4 = r7.isCancelled()     // Catch:{ all -> 0x00f4 }
                if (r4 == 0) goto L_0x006b
                goto L_0x0076
            L_0x006b:
                int r4 = r1.read(r3)     // Catch:{ all -> 0x00f4 }
                if (r4 <= 0) goto L_0x0075
                r2.write(r3, r0, r4)     // Catch:{ all -> 0x00f4 }
                goto L_0x0064
            L_0x0075:
                r3 = -1
            L_0x0076:
                r7.canRetry = r0     // Catch:{ all -> 0x00f4 }
                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ all -> 0x00f4 }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x00f4 }
                byte[] r5 = r2.toByteArray()     // Catch:{ all -> 0x00f4 }
                r4.<init>(r5)     // Catch:{ all -> 0x00f4 }
                r3.<init>(r4)     // Catch:{ all -> 0x00f4 }
                java.lang.String r4 = "results"
                org.json.JSONArray r3 = r3.getJSONArray(r4)     // Catch:{ all -> 0x00f4 }
                int r4 = r3.length()     // Catch:{ all -> 0x00f4 }
                if (r4 <= 0) goto L_0x00da
                org.json.JSONObject r3 = r3.getJSONObject(r0)     // Catch:{ all -> 0x00f4 }
                java.lang.String r4 = "artworkUrl100"
                java.lang.String r3 = r3.getString(r4)     // Catch:{ all -> 0x00f4 }
                boolean r4 = r7.small     // Catch:{ all -> 0x00f4 }
                if (r4 == 0) goto L_0x00b9
                java.net.HttpURLConnection r8 = r7.httpConnection     // Catch:{ all -> 0x00aa }
                if (r8 == 0) goto L_0x00ab
                java.net.HttpURLConnection r8 = r7.httpConnection     // Catch:{ all -> 0x00aa }
                r8.disconnect()     // Catch:{ all -> 0x00aa }
                goto L_0x00ab
            L_0x00aa:
            L_0x00ab:
                if (r1 == 0) goto L_0x00b5
                r1.close()     // Catch:{ all -> 0x00b1 }
                goto L_0x00b5
            L_0x00b1:
                r8 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            L_0x00b5:
                r2.close()     // Catch:{ Exception -> 0x00b8 }
            L_0x00b8:
                return r3
            L_0x00b9:
                java.lang.String r4 = "100x100"
                java.lang.String r5 = "600x600"
                java.lang.String r8 = r3.replace(r4, r5)     // Catch:{ all -> 0x00f4 }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00cb }
                if (r0 == 0) goto L_0x00cc
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00cb }
                r0.disconnect()     // Catch:{ all -> 0x00cb }
                goto L_0x00cc
            L_0x00cb:
            L_0x00cc:
                if (r1 == 0) goto L_0x00d6
                r1.close()     // Catch:{ all -> 0x00d2 }
                goto L_0x00d6
            L_0x00d2:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00d6:
                r2.close()     // Catch:{ Exception -> 0x00d9 }
            L_0x00d9:
                return r8
            L_0x00da:
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00e4 }
                if (r0 == 0) goto L_0x00e5
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00e4 }
                r0.disconnect()     // Catch:{ all -> 0x00e4 }
                goto L_0x00e5
            L_0x00e4:
            L_0x00e5:
                if (r1 == 0) goto L_0x00ef
                r1.close()     // Catch:{ all -> 0x00eb }
                goto L_0x00ef
            L_0x00eb:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00ef:
                r2.close()     // Catch:{ Exception -> 0x014f }
                goto L_0x014f
            L_0x00f4:
                r3 = move-exception
                r6 = r3
                r3 = r1
                r1 = r6
                goto L_0x0101
            L_0x00f9:
                r2 = move-exception
                r3 = r1
                r1 = r2
                r2 = r8
                goto L_0x0101
            L_0x00fe:
                r1 = move-exception
                r2 = r8
                r3 = r2
            L_0x0101:
                boolean r4 = r1 instanceof java.net.SocketTimeoutException     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x010e
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x0134
                r7.canRetry = r0     // Catch:{ all -> 0x0150 }
                goto L_0x0134
            L_0x010e:
                boolean r4 = r1 instanceof java.net.UnknownHostException     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x0115
                r7.canRetry = r0     // Catch:{ all -> 0x0150 }
                goto L_0x0134
            L_0x0115:
                boolean r4 = r1 instanceof java.net.SocketException     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x012e
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x0134
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0150 }
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x0134
                r7.canRetry = r0     // Catch:{ all -> 0x0150 }
                goto L_0x0134
            L_0x012e:
                boolean r4 = r1 instanceof java.io.FileNotFoundException     // Catch:{ all -> 0x0150 }
                if (r4 == 0) goto L_0x0134
                r7.canRetry = r0     // Catch:{ all -> 0x0150 }
            L_0x0134:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0150 }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x0141 }
                if (r0 == 0) goto L_0x0142
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x0141 }
                r0.disconnect()     // Catch:{ all -> 0x0141 }
                goto L_0x0142
            L_0x0141:
            L_0x0142:
                if (r3 == 0) goto L_0x014c
                r3.close()     // Catch:{ all -> 0x0148 }
                goto L_0x014c
            L_0x0148:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x014c:
                if (r2 == 0) goto L_0x014f
                goto L_0x00ef
            L_0x014f:
                return r8
            L_0x0150:
                r8 = move-exception
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x015b }
                if (r0 == 0) goto L_0x015c
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x015b }
                r0.disconnect()     // Catch:{ all -> 0x015b }
                goto L_0x015c
            L_0x015b:
            L_0x015c:
                if (r3 == 0) goto L_0x0166
                r3.close()     // Catch:{ all -> 0x0162 }
                goto L_0x0166
            L_0x0162:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0166:
                if (r2 == 0) goto L_0x016b
                r2.close()     // Catch:{ Exception -> 0x016b }
            L_0x016b:
                goto L_0x016d
            L_0x016c:
                throw r8
            L_0x016d:
                goto L_0x016c
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.ArtworkLoadTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                CacheImage cacheImage2 = this.cacheImage;
                cacheImage2.httpTask = new HttpImageTask(cacheImage2, 0, str);
                ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
                ImageLoader.this.runHttpTasks(false);
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask();
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$1$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onCancelled$1$ImageLoader$ArtworkLoadTask();
                }
            });
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

        static /* synthetic */ void lambda$doInBackground$2(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        public HttpImageTask(CacheImage cacheImage2, int i) {
            this.cacheImage = cacheImage2;
            this.imageSize = i;
        }

        public HttpImageTask(CacheImage cacheImage2, int i, String str) {
            this.cacheImage = cacheImage2;
            this.imageSize = i;
            this.overrideUrl = str;
        }

        private void reportProgress(float f) {
            long currentTimeMillis = System.currentTimeMillis();
            if (f != 1.0f) {
                long j = this.lastProgressTime;
                if (j != 0 && j >= currentTimeMillis - 500) {
                    return;
                }
            }
            this.lastProgressTime = currentTimeMillis;
            Utilities.stageQueue.postRunnable(new Runnable(f) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$reportProgress$1$ImageLoader$HttpImageTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpImageTask(float f) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, Float.valueOf(f));
            AndroidUtilities.runOnUIThread(new Runnable(f) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$0$ImageLoader$HttpImageTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpImageTask(float f) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.cacheImage.url, Float.valueOf(f));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:103:0x017e, code lost:
            org.telegram.messenger.FileLog.e(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:89:0x0163, code lost:
            if (r5 != -1) goto L_0x0181;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:92:0x0167, code lost:
            if (r8.imageSize == 0) goto L_0x016e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x0169, code lost:
            reportProgress(1.0f);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:0x016e, code lost:
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x0170, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:0x0171, code lost:
            r0 = r2;
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x0174, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x0175, code lost:
            r0 = r2;
            r2 = true;
         */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0185 A[Catch:{ all -> 0x018d }] */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x0195 A[Catch:{ all -> 0x019b }] */
        /* JADX WARNING: Removed duplicated region for block: B:116:0x019e A[SYNTHETIC, Splitter:B:116:0x019e] */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9 A[SYNTHETIC, Splitter:B:48:0x00e9] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r9) {
            /*
                r8 = this;
                boolean r9 = r8.isCancelled()
                r0 = 1
                r1 = 0
                r2 = 0
                if (r9 != 0) goto L_0x00e2
                org.telegram.messenger.ImageLoader$CacheImage r9 = r8.cacheImage     // Catch:{ all -> 0x00a8 }
                org.telegram.messenger.ImageLocation r9 = r9.imageLocation     // Catch:{ all -> 0x00a8 }
                java.lang.String r9 = r9.path     // Catch:{ all -> 0x00a8 }
                java.lang.String r3 = "https://static-maps"
                boolean r3 = r9.startsWith(r3)     // Catch:{ all -> 0x00a8 }
                if (r3 != 0) goto L_0x001f
                java.lang.String r3 = "https://maps.googleapis"
                boolean r3 = r9.startsWith(r3)     // Catch:{ all -> 0x00a8 }
                if (r3 == 0) goto L_0x0057
            L_0x001f:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r8.cacheImage     // Catch:{ all -> 0x00a8 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a8 }
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)     // Catch:{ all -> 0x00a8 }
                int r3 = r3.mapProvider     // Catch:{ all -> 0x00a8 }
                r4 = 3
                if (r3 == r4) goto L_0x002f
                r4 = 4
                if (r3 != r4) goto L_0x0057
            L_0x002f:
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x00a8 }
                java.util.concurrent.ConcurrentHashMap r3 = r3.testWebFile     // Catch:{ all -> 0x00a8 }
                java.lang.Object r3 = r3.get(r9)     // Catch:{ all -> 0x00a8 }
                org.telegram.messenger.WebFile r3 = (org.telegram.messenger.WebFile) r3     // Catch:{ all -> 0x00a8 }
                if (r3 == 0) goto L_0x0057
                org.telegram.tgnet.TLRPC$TL_upload_getWebFile r4 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile     // Catch:{ all -> 0x00a8 }
                r4.<init>()     // Catch:{ all -> 0x00a8 }
                org.telegram.tgnet.TLRPC$InputWebFileLocation r3 = r3.location     // Catch:{ all -> 0x00a8 }
                r4.location = r3     // Catch:{ all -> 0x00a8 }
                r4.offset = r2     // Catch:{ all -> 0x00a8 }
                r4.limit = r2     // Catch:{ all -> 0x00a8 }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r8.cacheImage     // Catch:{ all -> 0x00a8 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a8 }
                org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)     // Catch:{ all -> 0x00a8 }
                org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8 r5 = org.telegram.messenger.$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8.INSTANCE     // Catch:{ all -> 0x00a8 }
                r3.sendRequest(r4, r5)     // Catch:{ all -> 0x00a8 }
            L_0x0057:
                java.net.URL r3 = new java.net.URL     // Catch:{ all -> 0x00a8 }
                java.lang.String r4 = r8.overrideUrl     // Catch:{ all -> 0x00a8 }
                if (r4 == 0) goto L_0x005f
                java.lang.String r9 = r8.overrideUrl     // Catch:{ all -> 0x00a8 }
            L_0x005f:
                r3.<init>(r9)     // Catch:{ all -> 0x00a8 }
                java.net.URLConnection r9 = r3.openConnection()     // Catch:{ all -> 0x00a8 }
                java.net.HttpURLConnection r9 = (java.net.HttpURLConnection) r9     // Catch:{ all -> 0x00a8 }
                r8.httpConnection = r9     // Catch:{ all -> 0x00a8 }
                java.net.HttpURLConnection r9 = r8.httpConnection     // Catch:{ all -> 0x00a8 }
                java.lang.String r3 = "User-Agent"
                java.lang.String r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r9.addRequestProperty(r3, r4)     // Catch:{ all -> 0x00a8 }
                java.net.HttpURLConnection r9 = r8.httpConnection     // Catch:{ all -> 0x00a8 }
                r3 = 5000(0x1388, float:7.006E-42)
                r9.setConnectTimeout(r3)     // Catch:{ all -> 0x00a8 }
                java.net.HttpURLConnection r9 = r8.httpConnection     // Catch:{ all -> 0x00a8 }
                r9.setReadTimeout(r3)     // Catch:{ all -> 0x00a8 }
                java.net.HttpURLConnection r9 = r8.httpConnection     // Catch:{ all -> 0x00a8 }
                r9.setInstanceFollowRedirects(r0)     // Catch:{ all -> 0x00a8 }
                boolean r9 = r8.isCancelled()     // Catch:{ all -> 0x00a8 }
                if (r9 != 0) goto L_0x00e2
                java.net.HttpURLConnection r9 = r8.httpConnection     // Catch:{ all -> 0x00a8 }
                r9.connect()     // Catch:{ all -> 0x00a8 }
                java.net.HttpURLConnection r9 = r8.httpConnection     // Catch:{ all -> 0x00a8 }
                java.io.InputStream r9 = r9.getInputStream()     // Catch:{ all -> 0x00a8 }
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x00a3 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r8.cacheImage     // Catch:{ all -> 0x00a3 }
                java.io.File r4 = r4.tempFilePath     // Catch:{ all -> 0x00a3 }
                java.lang.String r5 = "rws"
                r3.<init>(r4, r5)     // Catch:{ all -> 0x00a3 }
                r8.fileOutputStream = r3     // Catch:{ all -> 0x00a3 }
                goto L_0x00e3
            L_0x00a3:
                r3 = move-exception
                r7 = r3
                r3 = r9
                r9 = r7
                goto L_0x00aa
            L_0x00a8:
                r9 = move-exception
                r3 = r1
            L_0x00aa:
                boolean r4 = r9 instanceof java.net.SocketTimeoutException
                if (r4 == 0) goto L_0x00b7
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r4 == 0) goto L_0x00dd
                r8.canRetry = r2
                goto L_0x00dd
            L_0x00b7:
                boolean r4 = r9 instanceof java.net.UnknownHostException
                if (r4 == 0) goto L_0x00be
                r8.canRetry = r2
                goto L_0x00dd
            L_0x00be:
                boolean r4 = r9 instanceof java.net.SocketException
                if (r4 == 0) goto L_0x00d7
                java.lang.String r4 = r9.getMessage()
                if (r4 == 0) goto L_0x00dd
                java.lang.String r4 = r9.getMessage()
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)
                if (r4 == 0) goto L_0x00dd
                r8.canRetry = r2
                goto L_0x00dd
            L_0x00d7:
                boolean r4 = r9 instanceof java.io.FileNotFoundException
                if (r4 == 0) goto L_0x00dd
                r8.canRetry = r2
            L_0x00dd:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
                r9 = r3
                goto L_0x00e3
            L_0x00e2:
                r9 = r1
            L_0x00e3:
                boolean r3 = r8.isCancelled()
                if (r3 != 0) goto L_0x0181
                java.net.HttpURLConnection r3 = r8.httpConnection     // Catch:{ Exception -> 0x0102 }
                if (r3 == 0) goto L_0x0106
                java.net.HttpURLConnection r3 = r8.httpConnection     // Catch:{ Exception -> 0x0102 }
                int r3 = r3.getResponseCode()     // Catch:{ Exception -> 0x0102 }
                r4 = 200(0xc8, float:2.8E-43)
                if (r3 == r4) goto L_0x0106
                r4 = 202(0xca, float:2.83E-43)
                if (r3 == r4) goto L_0x0106
                r4 = 304(0x130, float:4.26E-43)
                if (r3 == r4) goto L_0x0106
                r8.canRetry = r2     // Catch:{ Exception -> 0x0102 }
                goto L_0x0106
            L_0x0102:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0106:
                int r3 = r8.imageSize
                if (r3 != 0) goto L_0x013b
                java.net.HttpURLConnection r3 = r8.httpConnection
                if (r3 == 0) goto L_0x013b
                java.util.Map r3 = r3.getHeaderFields()     // Catch:{ Exception -> 0x0137 }
                if (r3 == 0) goto L_0x013b
                java.lang.String r4 = "content-Length"
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0137 }
                java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x0137 }
                if (r3 == 0) goto L_0x013b
                boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x0137 }
                if (r4 != 0) goto L_0x013b
                java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0137 }
                java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0137 }
                if (r3 == 0) goto L_0x013b
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x0137 }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x0137 }
                r8.imageSize = r3     // Catch:{ Exception -> 0x0137 }
                goto L_0x013b
            L_0x0137:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x013b:
                if (r9 == 0) goto L_0x0181
                r3 = 8192(0x2000, float:1.14794E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x017d }
                r4 = 0
            L_0x0142:
                boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x017d }
                if (r5 == 0) goto L_0x0149
                goto L_0x0181
            L_0x0149:
                int r5 = r9.read(r3)     // Catch:{ Exception -> 0x0178 }
                if (r5 <= 0) goto L_0x0162
                int r4 = r4 + r5
                java.io.RandomAccessFile r6 = r8.fileOutputStream     // Catch:{ Exception -> 0x0178 }
                r6.write(r3, r2, r5)     // Catch:{ Exception -> 0x0178 }
                int r5 = r8.imageSize     // Catch:{ Exception -> 0x0178 }
                if (r5 == 0) goto L_0x0142
                float r5 = (float) r4     // Catch:{ Exception -> 0x0178 }
                int r6 = r8.imageSize     // Catch:{ Exception -> 0x0178 }
                float r6 = (float) r6     // Catch:{ Exception -> 0x0178 }
                float r5 = r5 / r6
                r8.reportProgress(r5)     // Catch:{ Exception -> 0x0178 }
                goto L_0x0142
            L_0x0162:
                r3 = -1
                if (r5 != r3) goto L_0x0181
                int r2 = r8.imageSize     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                if (r2 == 0) goto L_0x016e
                r2 = 1065353216(0x3var_, float:1.0)
                r8.reportProgress(r2)     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
            L_0x016e:
                r2 = 1
                goto L_0x0181
            L_0x0170:
                r2 = move-exception
                r0 = r2
                r2 = 1
                goto L_0x017e
            L_0x0174:
                r2 = move-exception
                r0 = r2
                r2 = 1
                goto L_0x0179
            L_0x0178:
                r0 = move-exception
            L_0x0179:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x017d }
                goto L_0x0181
            L_0x017d:
                r0 = move-exception
            L_0x017e:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0181:
                java.io.RandomAccessFile r0 = r8.fileOutputStream     // Catch:{ all -> 0x018d }
                if (r0 == 0) goto L_0x0191
                java.io.RandomAccessFile r0 = r8.fileOutputStream     // Catch:{ all -> 0x018d }
                r0.close()     // Catch:{ all -> 0x018d }
                r8.fileOutputStream = r1     // Catch:{ all -> 0x018d }
                goto L_0x0191
            L_0x018d:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0191:
                java.net.HttpURLConnection r0 = r8.httpConnection     // Catch:{ all -> 0x019b }
                if (r0 == 0) goto L_0x019c
                java.net.HttpURLConnection r0 = r8.httpConnection     // Catch:{ all -> 0x019b }
                r0.disconnect()     // Catch:{ all -> 0x019b }
                goto L_0x019c
            L_0x019b:
            L_0x019c:
                if (r9 == 0) goto L_0x01a6
                r9.close()     // Catch:{ all -> 0x01a2 }
                goto L_0x01a6
            L_0x01a2:
                r9 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
            L_0x01a6:
                if (r2 == 0) goto L_0x01bc
                org.telegram.messenger.ImageLoader$CacheImage r9 = r8.cacheImage
                java.io.File r0 = r9.tempFilePath
                if (r0 == 0) goto L_0x01bc
                java.io.File r9 = r9.finalFilePath
                boolean r9 = r0.renameTo(r9)
                if (r9 != 0) goto L_0x01bc
                org.telegram.messenger.ImageLoader$CacheImage r9 = r8.cacheImage
                java.io.File r0 = r9.tempFilePath
                r9.finalFilePath = r0
            L_0x01bc:
                java.lang.Boolean r9 = java.lang.Boolean.valueOf(r2)
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.HttpImageTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            if (bool.booleanValue() || !this.canRetry) {
                ImageLoader imageLoader = ImageLoader.this;
                CacheImage cacheImage2 = this.cacheImage;
                imageLoader.fileDidLoaded(cacheImage2.url, cacheImage2.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new Runnable(bool) {
                private final /* synthetic */ Boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$4$ImageLoader$HttpImageTask(this.f$1);
                }
            });
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$5$ImageLoader$HttpImageTask();
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$4$ImageLoader$HttpImageTask(Boolean bool) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable(bool) {
                private final /* synthetic */ Boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$3$ImageLoader$HttpImageTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$3$ImageLoader$HttpImageTask(Boolean bool) {
            if (bool.booleanValue()) {
                NotificationCenter instance = NotificationCenter.getInstance(this.cacheImage.currentAccount);
                int i = NotificationCenter.fileDidLoad;
                CacheImage cacheImage2 = this.cacheImage;
                instance.postNotificationName(i, cacheImage2.url, cacheImage2.finalFilePath);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, this.cacheImage.url, 2);
        }

        public /* synthetic */ void lambda$onPostExecute$5$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$6$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$6$ImageLoader$HttpImageTask();
                }
            });
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$8$ImageLoader$HttpImageTask();
                }
            });
        }

        public /* synthetic */ void lambda$onCancelled$8$ImageLoader$HttpImageTask() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$7$ImageLoader$HttpImageTask();
                }
            });
        }

        public /* synthetic */ void lambda$null$7$ImageLoader$HttpImageTask() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, this.cacheImage.url, 1);
        }
    }

    private class ThumbGenerateTask implements Runnable {
        private ThumbGenerateInfo info;
        private int mediaType;
        private File originalPath;

        public ThumbGenerateTask(int i, File file, ThumbGenerateInfo thumbGenerateInfo) {
            this.mediaType = i;
            this.originalPath = file;
            this.info = thumbGenerateInfo;
        }

        private void removeTask() {
            ThumbGenerateInfo thumbGenerateInfo = this.info;
            if (thumbGenerateInfo != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable(FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ImageLoader.ThumbGenerateTask.this.lambda$removeTask$0$ImageLoader$ThumbGenerateTask(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$removeTask$0$ImageLoader$ThumbGenerateTask(String str) {
            ThumbGenerateTask thumbGenerateTask = (ThumbGenerateTask) ImageLoader.this.thumbGenerateTasks.remove(str);
        }

        public void run() {
            Bitmap bitmap;
            try {
                if (this.info == null) {
                    removeTask();
                    return;
                }
                String str = "q_" + this.info.parentDocument.dc_id + "_" + this.info.parentDocument.id;
                File file = new File(FileLoader.getDirectory(4), str + ".jpg");
                if (!file.exists()) {
                    if (this.originalPath.exists()) {
                        int max = this.info.big ? Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) : Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                        Bitmap bitmap2 = null;
                        if (this.mediaType == 0) {
                            float f = (float) max;
                            bitmap2 = ImageLoader.loadBitmap(this.originalPath.toString(), (Uri) null, f, f, false);
                        } else {
                            int i = 2;
                            if (this.mediaType == 2) {
                                String file2 = this.originalPath.toString();
                                if (!this.info.big) {
                                    i = 1;
                                }
                                bitmap2 = ThumbnailUtils.createVideoThumbnail(file2, i);
                            } else if (this.mediaType == 3) {
                                String lowerCase = this.originalPath.toString().toLowerCase();
                                if (lowerCase.endsWith("mp4")) {
                                    String file3 = this.originalPath.toString();
                                    if (!this.info.big) {
                                        i = 1;
                                    }
                                    bitmap2 = ThumbnailUtils.createVideoThumbnail(file3, i);
                                } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif")) {
                                    float f2 = (float) max;
                                    bitmap2 = ImageLoader.loadBitmap(lowerCase, (Uri) null, f2, f2, false);
                                }
                            }
                        }
                        if (bitmap2 == null) {
                            removeTask();
                            return;
                        }
                        int width = bitmap2.getWidth();
                        int height = bitmap2.getHeight();
                        if (width != 0) {
                            if (height != 0) {
                                float f3 = (float) width;
                                float f4 = (float) max;
                                float f5 = (float) height;
                                float min = Math.min(f3 / f4, f5 / f4);
                                if (min <= 1.0f || (bitmap = Bitmaps.createScaledBitmap(bitmap2, (int) (f3 / min), (int) (f5 / min), true)) == bitmap2) {
                                    bitmap = bitmap2;
                                } else {
                                    bitmap2.recycle();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, this.info.big ? 83 : 60, fileOutputStream);
                                fileOutputStream.close();
                                AndroidUtilities.runOnUIThread(new Runnable(str, new ArrayList(this.info.imageReceiverArray), new BitmapDrawable(bitmap), new ArrayList(this.info.imageReceiverGuidsArray)) {
                                    private final /* synthetic */ String f$1;
                                    private final /* synthetic */ ArrayList f$2;
                                    private final /* synthetic */ BitmapDrawable f$3;
                                    private final /* synthetic */ ArrayList f$4;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                        this.f$4 = r5;
                                    }

                                    public final void run() {
                                        ImageLoader.ThumbGenerateTask.this.lambda$run$1$ImageLoader$ThumbGenerateTask(this.f$1, this.f$2, this.f$3, this.f$4);
                                    }
                                });
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
            } catch (Throwable th) {
                FileLog.e(th);
                removeTask();
            }
        }

        public /* synthetic */ void lambda$run$1$ImageLoader$ThumbGenerateTask(String str, ArrayList arrayList, BitmapDrawable bitmapDrawable, ArrayList arrayList2) {
            removeTask();
            if (this.info.filter != null) {
                str = str + "@" + this.info.filter;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(bitmapDrawable, str, 0, false, ((Integer) arrayList2.get(i)).intValue());
            }
            ImageLoader.this.memCache.put(str, bitmapDrawable);
        }
    }

    private class CacheOutTask implements Runnable {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage cacheImage2) {
            this.cacheImage = cacheImage2;
        }

        /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
            java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
            	at java.util.ArrayList.get(ArrayList.java:433)
            	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
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
        public void run() {
            /*
                r37 = this;
                r1 = r37
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0a2e }
                r1.runningThread = r0     // Catch:{ all -> 0x0a2e }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0a2e }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0a2e }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0a2e }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0a2e }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r3 = r2.photoSize
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
                if (r4 == 0) goto L_0x0037
                org.telegram.tgnet.TLRPC$TL_photoStrippedSize r3 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r3
                byte[] r2 = r3.bytes
                java.lang.String r0 = r0.filter
                android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r2, r0)
                if (r0 == 0) goto L_0x0031
                android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
                r5.<init>(r0)
                goto L_0x0032
            L_0x0031:
                r5 = 0
            L_0x0032:
                r1.onPostExecute(r5)
                goto L_0x0a2d
            L_0x0037:
                int r3 = r0.imageType
                r4 = 5
                if (r3 != r4) goto L_0x0052
                org.telegram.ui.Components.ThemePreviewDrawable r3 = new org.telegram.ui.Components.ThemePreviewDrawable     // Catch:{ all -> 0x0048 }
                java.io.File r0 = r0.finalFilePath     // Catch:{ all -> 0x0048 }
                org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ all -> 0x0048 }
                org.telegram.messenger.DocumentObject$ThemeDocument r2 = (org.telegram.messenger.DocumentObject.ThemeDocument) r2     // Catch:{ all -> 0x0048 }
                r3.<init>(r0, r2)     // Catch:{ all -> 0x0048 }
                goto L_0x004d
            L_0x0048:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r3 = 0
            L_0x004d:
                r1.onPostExecute(r3)
                goto L_0x0a2d
            L_0x0052:
                r2 = 3
                r6 = 4
                r7 = 2
                r8 = 1
                r9 = 0
                if (r3 == r2) goto L_0x09d6
                if (r3 != r6) goto L_0x005d
                goto L_0x09d6
            L_0x005d:
                r10 = 8
                if (r3 != r8) goto L_0x015f
                r0 = 1126865306(0x432a999a, float:170.6)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r11 = 512(0x200, float:7.175E-43)
                int r3 = java.lang.Math.min(r11, r3)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r11, r0)
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                java.lang.String r12 = r12.filter
                if (r12 == 0) goto L_0x0145
                java.lang.String r13 = "_"
                java.lang.String[] r12 = r12.split(r13)
                int r13 = r12.length
                if (r13 < r7) goto L_0x00c7
                r0 = r12[r9]
                float r0 = java.lang.Float.parseFloat(r0)
                r3 = r12[r8]
                float r3 = java.lang.Float.parseFloat(r3)
                float r13 = org.telegram.messenger.AndroidUtilities.density
                float r13 = r13 * r0
                int r13 = (int) r13
                int r13 = java.lang.Math.min(r11, r13)
                float r14 = org.telegram.messenger.AndroidUtilities.density
                float r14 = r14 * r3
                int r14 = (int) r14
                int r11 = java.lang.Math.min(r11, r14)
                r14 = 1119092736(0x42b40000, float:90.0)
                int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
                if (r0 > 0) goto L_0x00bd
                int r0 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
                if (r0 > 0) goto L_0x00bd
                r0 = 160(0xa0, float:2.24E-43)
                int r0 = java.lang.Math.min(r13, r0)
                r3 = 160(0xa0, float:2.24E-43)
                int r3 = java.lang.Math.min(r11, r3)
                r11 = r3
                r3 = r0
                r0 = 1
                goto L_0x00bf
            L_0x00bd:
                r3 = r13
                r0 = 0
            L_0x00bf:
                int r13 = org.telegram.messenger.SharedConfig.getDevicePerfomanceClass()
                if (r13 == r7) goto L_0x00c9
                r9 = 1
                goto L_0x00c9
            L_0x00c7:
                r11 = r0
                r0 = 0
            L_0x00c9:
                int r13 = r12.length
                if (r13 < r2) goto L_0x00e3
                r13 = r12[r7]
                java.lang.String r14 = "nr"
                boolean r13 = r14.equals(r13)
                if (r13 == 0) goto L_0x00d8
                r8 = 2
                goto L_0x00e3
            L_0x00d8:
                r7 = r12[r7]
                java.lang.String r13 = "nrs"
                boolean r7 = r13.equals(r7)
                if (r7 == 0) goto L_0x00e3
                r8 = 3
            L_0x00e3:
                int r2 = r12.length
                if (r2 < r4) goto L_0x013e
                r2 = r12[r6]
                java.lang.String r4 = "c1"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x00fe
                int[] r5 = new int[r10]
                r5 = {16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934} // fill-array
            L_0x00f5:
                r17 = r0
                r14 = r3
                r18 = r5
                r16 = r9
                r15 = r11
                goto L_0x014d
            L_0x00fe:
                r2 = r12[r6]
                java.lang.String r4 = "c2"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x010e
                int[] r5 = new int[r10]
                r5 = {16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242} // fill-array
                goto L_0x00f5
            L_0x010e:
                r2 = r12[r6]
                java.lang.String r4 = "c3"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x011e
                int[] r5 = new int[r10]
                r5 = {16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080} // fill-array
                goto L_0x00f5
            L_0x011e:
                r2 = r12[r6]
                java.lang.String r4 = "c4"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x012e
                int[] r5 = new int[r10]
                r5 = {16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506} // fill-array
                goto L_0x00f5
            L_0x012e:
                r2 = r12[r6]
                java.lang.String r4 = "c5"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x013e
                int[] r5 = new int[r10]
                r5 = {16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492} // fill-array
                goto L_0x00f5
            L_0x013e:
                r17 = r0
                r14 = r3
                r16 = r9
                r15 = r11
                goto L_0x014b
            L_0x0145:
                r15 = r0
                r14 = r3
                r16 = 0
                r17 = 0
            L_0x014b:
                r18 = 0
            L_0x014d:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.io.File r13 = r2.finalFilePath
                r12 = r0
                r12.<init>((java.io.File) r13, (int) r14, (int) r15, (boolean) r16, (boolean) r17, (int[]) r18)
                r0.setAutoRepeat(r8)
                r1.onPostExecute(r0)
                goto L_0x0a2d
            L_0x015f:
                if (r3 != r7) goto L_0x01bb
                java.lang.String r0 = r0.filter
                java.lang.String r2 = "g"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0191
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r2 = r2.document
                boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted
                if (r3 != 0) goto L_0x0191
                org.telegram.ui.Components.AnimatedFileDrawable r3 = new org.telegram.ui.Components.AnimatedFileDrawable
                java.io.File r7 = r0.finalFilePath
                r8 = 0
                int r0 = r0.size
                long r9 = (long) r0
                boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC.Document
                if (r0 == 0) goto L_0x0183
                r11 = r2
                goto L_0x0184
            L_0x0183:
                r11 = 0
            L_0x0184:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.Object r12 = r0.parentObject
                int r13 = r0.currentAccount
                r14 = 0
                r6 = r3
                r6.<init>(r7, r8, r9, r11, r12, r13, r14)
                r0 = r3
                goto L_0x01b3
            L_0x0191:
                org.telegram.ui.Components.AnimatedFileDrawable r0 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.io.File r3 = r2.finalFilePath
                java.lang.String r2 = r2.filter
                java.lang.String r4 = "d"
                boolean r17 = r4.equals(r2)
                r18 = 0
                r20 = 0
                r21 = 0
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                int r2 = r2.currentAccount
                r23 = 0
                r15 = r0
                r16 = r3
                r22 = r2
                r15.<init>(r16, r17, r18, r20, r21, r22, r23)
            L_0x01b3:
                java.lang.Thread.interrupted()
                r1.onPostExecute(r0)
                goto L_0x0a2d
            L_0x01bb:
                java.io.File r3 = r0.finalFilePath
                org.telegram.messenger.SecureDocument r4 = r0.secureDocument
                if (r4 != 0) goto L_0x01d6
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x01d4
                if (r3 == 0) goto L_0x01d4
                java.lang.String r0 = r3.getAbsolutePath()
                java.lang.String r4 = ".enc"
                boolean r0 = r0.endsWith(r4)
                if (r0 == 0) goto L_0x01d4
                goto L_0x01d6
            L_0x01d4:
                r4 = 0
                goto L_0x01d7
            L_0x01d6:
                r4 = 1
            L_0x01d7:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x01f0
                org.telegram.messenger.SecureDocumentKey r6 = r0.secureDocumentKey
                org.telegram.tgnet.TLRPC$TL_secureFile r0 = r0.secureFile
                if (r0 == 0) goto L_0x01e8
                byte[] r0 = r0.file_hash
                if (r0 == 0) goto L_0x01e8
                goto L_0x01ee
            L_0x01e8:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                byte[] r0 = r0.fileHash
            L_0x01ee:
                r11 = r0
                goto L_0x01f2
            L_0x01f0:
                r6 = 0
                r11 = 0
            L_0x01f2:
                int r0 = android.os.Build.VERSION.SDK_INT
                r12 = 19
                if (r0 >= r12) goto L_0x0262
                java.io.RandomAccessFile r12 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0248, all -> 0x0244 }
                java.lang.String r0 = "r"
                r12.<init>(r3, r0)     // Catch:{ Exception -> 0x0248, all -> 0x0244 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x0242 }
                int r0 = r0.type     // Catch:{ Exception -> 0x0242 }
                if (r0 != r8) goto L_0x020a
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x0242 }
                goto L_0x020e
            L_0x020a:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x0242 }
            L_0x020e:
                int r13 = r0.length     // Catch:{ Exception -> 0x0242 }
                r12.readFully(r0, r9, r13)     // Catch:{ Exception -> 0x0242 }
                java.lang.String r13 = new java.lang.String     // Catch:{ Exception -> 0x0242 }
                r13.<init>(r0)     // Catch:{ Exception -> 0x0242 }
                java.lang.String r0 = r13.toLowerCase()     // Catch:{ Exception -> 0x0242 }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0242 }
                java.lang.String r13 = "riff"
                boolean r13 = r0.startsWith(r13)     // Catch:{ Exception -> 0x0242 }
                if (r13 == 0) goto L_0x0232
                java.lang.String r13 = "webp"
                boolean r0 = r0.endsWith(r13)     // Catch:{ Exception -> 0x0242 }
                if (r0 == 0) goto L_0x0232
                r13 = 1
                goto L_0x0233
            L_0x0232:
                r13 = 0
            L_0x0233:
                r12.close()     // Catch:{ Exception -> 0x0240 }
                r12.close()     // Catch:{ Exception -> 0x023a }
                goto L_0x0263
            L_0x023a:
                r0 = move-exception
                r12 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
                goto L_0x0263
            L_0x0240:
                r0 = move-exception
                goto L_0x024b
            L_0x0242:
                r0 = move-exception
                goto L_0x024a
            L_0x0244:
                r0 = move-exception
                r2 = r0
                r12 = 0
                goto L_0x0256
            L_0x0248:
                r0 = move-exception
                r12 = 0
            L_0x024a:
                r13 = 0
            L_0x024b:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0254 }
                if (r12 == 0) goto L_0x0263
                r12.close()     // Catch:{ Exception -> 0x023a }
                goto L_0x0263
            L_0x0254:
                r0 = move-exception
                r2 = r0
            L_0x0256:
                if (r12 == 0) goto L_0x0261
                r12.close()     // Catch:{ Exception -> 0x025c }
                goto L_0x0261
            L_0x025c:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0261:
                throw r2
            L_0x0262:
                r13 = 0
            L_0x0263:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                java.lang.String r0 = r0.path
                if (r0 == 0) goto L_0x02c9
                java.lang.String r12 = "thumb://"
                boolean r12 = r0.startsWith(r12)
                if (r12 == 0) goto L_0x0295
                java.lang.String r12 = ":"
                int r12 = r0.indexOf(r12, r10)
                if (r12 < 0) goto L_0x028e
                java.lang.String r14 = r0.substring(r10, r12)
                long r14 = java.lang.Long.parseLong(r14)
                java.lang.Long r14 = java.lang.Long.valueOf(r14)
                int r12 = r12 + r8
                java.lang.String r0 = r0.substring(r12)
                goto L_0x0290
            L_0x028e:
                r0 = 0
                r14 = 0
            L_0x0290:
                r12 = r0
            L_0x0291:
                r15 = 0
            L_0x0292:
                r16 = 0
                goto L_0x02ce
            L_0x0295:
                java.lang.String r12 = "vthumb://"
                boolean r12 = r0.startsWith(r12)
                if (r12 == 0) goto L_0x02be
                r12 = 9
                java.lang.String r14 = ":"
                int r12 = r0.indexOf(r14, r12)
                if (r12 < 0) goto L_0x02b8
                r14 = 9
                java.lang.String r0 = r0.substring(r14, r12)
                long r14 = java.lang.Long.parseLong(r0)
                java.lang.Long r0 = java.lang.Long.valueOf(r14)
                r12 = 1
                goto L_0x02ba
            L_0x02b8:
                r0 = 0
                r12 = 0
            L_0x02ba:
                r14 = r0
                r15 = r12
                r12 = 0
                goto L_0x0292
            L_0x02be:
                java.lang.String r12 = "http"
                boolean r0 = r0.startsWith(r12)
                if (r0 != 0) goto L_0x02c9
                r12 = 0
                r14 = 0
                goto L_0x0291
            L_0x02c9:
                r12 = 0
                r14 = 0
                r15 = 0
                r16 = 1
            L_0x02ce:
                android.graphics.BitmapFactory$Options r10 = new android.graphics.BitmapFactory$Options
                r10.<init>()
                r10.inSampleSize = r8
                int r0 = android.os.Build.VERSION.SDK_INT
                r2 = 21
                if (r0 >= r2) goto L_0x02dd
                r10.inPurgeable = r8
            L_0x02dd:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r2 = r0.canForce8888
                r19 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04f2 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04f2 }
                r20 = 1065353216(0x3var_, float:1.0)
                if (r0 == 0) goto L_0x0498
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04f2 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04f2 }
                java.lang.String r5 = "_"
                java.lang.String[] r0 = r0.split(r5)     // Catch:{ all -> 0x04f2 }
                int r5 = r0.length     // Catch:{ all -> 0x04f2 }
                if (r5 < r7) goto L_0x0325
                r5 = r0[r9]     // Catch:{ all -> 0x031b }
                float r5 = java.lang.Float.parseFloat(r5)     // Catch:{ all -> 0x031b }
                float r22 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x031b }
                float r5 = r5 * r22
                r0 = r0[r8]     // Catch:{ all -> 0x0311 }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ all -> 0x0311 }
                float r22 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0311 }
                float r0 = r0 * r22
                r22 = r0
                goto L_0x0328
            L_0x0311:
                r0 = move-exception
                r8 = r5
                r26 = r12
                r25 = r13
                r5 = 0
                r7 = 0
                goto L_0x04fb
            L_0x031b:
                r0 = move-exception
                r26 = r12
                r25 = r13
                r5 = 0
                r7 = 0
                r8 = 0
                goto L_0x04fb
            L_0x0325:
                r5 = 0
                r22 = 0
            L_0x0328:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x048d }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x048d }
                java.lang.String r7 = "b2"
                boolean r0 = r0.contains(r7)     // Catch:{ all -> 0x048d }
                if (r0 == 0) goto L_0x0336
                r7 = 3
                goto L_0x0353
            L_0x0336:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x048d }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x048d }
                java.lang.String r7 = "b1"
                boolean r0 = r0.contains(r7)     // Catch:{ all -> 0x048d }
                if (r0 == 0) goto L_0x0344
                r7 = 2
                goto L_0x0353
            L_0x0344:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x048d }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x048d }
                java.lang.String r7 = "b"
                boolean r0 = r0.contains(r7)     // Catch:{ all -> 0x048d }
                if (r0 == 0) goto L_0x0352
                r7 = 1
                goto L_0x0353
            L_0x0352:
                r7 = 0
            L_0x0353:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0485 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0485 }
                java.lang.String r9 = "i"
                boolean r9 = r0.contains(r9)     // Catch:{ all -> 0x0485 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x047c }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x047c }
                java.lang.String r8 = "f"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x047c }
                if (r0 == 0) goto L_0x036a
                r2 = 1
            L_0x036a:
                if (r13 != 0) goto L_0x0471
                int r0 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
                if (r0 == 0) goto L_0x0471
                int r0 = (r22 > r19 ? 1 : (r22 == r19 ? 0 : -1))
                if (r0 == 0) goto L_0x0471
                r8 = 1
                r10.inJustDecodeBounds = r8     // Catch:{ all -> 0x046d }
                if (r14 == 0) goto L_0x03a8
                if (r12 != 0) goto L_0x03a8
                if (r15 == 0) goto L_0x038f
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x047c }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x047c }
                r26 = r12
                r25 = r13
                long r12 = r14.longValue()     // Catch:{ all -> 0x03a5 }
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r12, r8, r10)     // Catch:{ all -> 0x03a5 }
                goto L_0x03a1
            L_0x038f:
                r26 = r12
                r25 = r13
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03a5 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x03a5 }
                long r12 = r14.longValue()     // Catch:{ all -> 0x03a5 }
                r8 = 1
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r12, r8, r10)     // Catch:{ all -> 0x03a5 }
            L_0x03a1:
                r27 = r2
                goto L_0x041f
            L_0x03a5:
                r0 = move-exception
                goto L_0x0481
            L_0x03a8:
                r26 = r12
                r25 = r13
                if (r6 == 0) goto L_0x0405
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0400 }
                java.lang.String r8 = "r"
                r0.<init>(r3, r8)     // Catch:{ all -> 0x0400 }
                long r12 = r0.length()     // Catch:{ all -> 0x0400 }
                int r8 = (int) r12     // Catch:{ all -> 0x0400 }
                java.lang.ThreadLocal r12 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0400 }
                java.lang.Object r12 = r12.get()     // Catch:{ all -> 0x0400 }
                byte[] r12 = (byte[]) r12     // Catch:{ all -> 0x0400 }
                if (r12 == 0) goto L_0x03ca
                int r13 = r12.length     // Catch:{ all -> 0x03a5 }
                if (r13 < r8) goto L_0x03ca
                goto L_0x03cb
            L_0x03ca:
                r12 = 0
            L_0x03cb:
                if (r12 != 0) goto L_0x03d6
                byte[] r12 = new byte[r8]     // Catch:{ all -> 0x03a5 }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x03a5 }
                r13.set(r12)     // Catch:{ all -> 0x03a5 }
            L_0x03d6:
                r13 = 0
                r0.readFully(r12, r13, r8)     // Catch:{ all -> 0x0400 }
                r0.close()     // Catch:{ all -> 0x0400 }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r12, (int) r13, (int) r8, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x0400 }
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r12, r13, r8)     // Catch:{ all -> 0x0400 }
                if (r11 == 0) goto L_0x03f1
                boolean r0 = java.util.Arrays.equals(r0, r11)     // Catch:{ all -> 0x03a5 }
                if (r0 != 0) goto L_0x03ed
                goto L_0x03f1
            L_0x03ed:
                r27 = r2
                r0 = 0
                goto L_0x03f4
            L_0x03f1:
                r27 = r2
                r0 = 1
            L_0x03f4:
                r13 = 0
                byte r2 = r12[r13]     // Catch:{ all -> 0x0468 }
                r2 = r2 & 255(0xff, float:3.57E-43)
                int r8 = r8 - r2
                if (r0 != 0) goto L_0x041f
                android.graphics.BitmapFactory.decodeByteArray(r12, r2, r8, r10)     // Catch:{ all -> 0x0468 }
                goto L_0x041f
            L_0x0400:
                r0 = move-exception
                r27 = r2
                goto L_0x0481
            L_0x0405:
                r27 = r2
                if (r4 == 0) goto L_0x0413
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0468 }
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0468 }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x0468 }
                r0.<init>((java.io.File) r3, (java.io.File) r2)     // Catch:{ all -> 0x0468 }
                goto L_0x0418
            L_0x0413:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0468 }
                r0.<init>(r3)     // Catch:{ all -> 0x0468 }
            L_0x0418:
                r2 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r2, r10)     // Catch:{ all -> 0x0468 }
                r0.close()     // Catch:{ all -> 0x0468 }
            L_0x041f:
                int r0 = r10.outWidth     // Catch:{ all -> 0x0468 }
                float r0 = (float) r0     // Catch:{ all -> 0x0468 }
                int r2 = r10.outHeight     // Catch:{ all -> 0x0468 }
                float r2 = (float) r2     // Catch:{ all -> 0x0468 }
                int r8 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1))
                if (r8 < 0) goto L_0x0436
                int r8 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r8 <= 0) goto L_0x0436
                float r8 = r0 / r5
                float r12 = r2 / r22
                float r8 = java.lang.Math.max(r8, r12)     // Catch:{ all -> 0x0468 }
                goto L_0x043e
            L_0x0436:
                float r8 = r0 / r5
                float r12 = r2 / r22
                float r8 = java.lang.Math.min(r8, r12)     // Catch:{ all -> 0x0468 }
            L_0x043e:
                r12 = 1067030938(0x3var_a, float:1.2)
                int r12 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r12 >= 0) goto L_0x0447
                r8 = 1065353216(0x3var_, float:1.0)
            L_0x0447:
                r12 = 0
                r10.inJustDecodeBounds = r12     // Catch:{ all -> 0x0468 }
                int r12 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1))
                if (r12 <= 0) goto L_0x0464
                int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r0 > 0) goto L_0x0456
                int r0 = (r2 > r22 ? 1 : (r2 == r22 ? 0 : -1))
                if (r0 <= 0) goto L_0x0464
            L_0x0456:
                r0 = 1
            L_0x0457:
                r2 = 2
                int r0 = r0 * 2
                int r2 = r0 * 2
                float r2 = (float) r2     // Catch:{ all -> 0x0468 }
                int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r2 < 0) goto L_0x0457
                r10.inSampleSize = r0     // Catch:{ all -> 0x0468 }
                goto L_0x0477
            L_0x0464:
                int r0 = (int) r8     // Catch:{ all -> 0x0468 }
                r10.inSampleSize = r0     // Catch:{ all -> 0x0468 }
                goto L_0x0477
            L_0x0468:
                r0 = move-exception
                r8 = r5
                r2 = r27
                goto L_0x0482
            L_0x046d:
                r0 = move-exception
                r27 = r2
                goto L_0x047d
            L_0x0471:
                r27 = r2
                r26 = r12
                r25 = r13
            L_0x0477:
                r2 = r27
                r0 = 0
                goto L_0x04ef
            L_0x047c:
                r0 = move-exception
            L_0x047d:
                r26 = r12
                r25 = r13
            L_0x0481:
                r8 = r5
            L_0x0482:
                r5 = 0
                goto L_0x04fd
            L_0x0485:
                r0 = move-exception
                r26 = r12
                r25 = r13
                r8 = r5
                r5 = 0
                goto L_0x0495
            L_0x048d:
                r0 = move-exception
                r26 = r12
                r25 = r13
                r8 = r5
                r5 = 0
                r7 = 0
            L_0x0495:
                r9 = 0
                goto L_0x04fd
            L_0x0498:
                r26 = r12
                r25 = r13
                if (r26 == 0) goto L_0x04e9
                r5 = 1
                r10.inJustDecodeBounds = r5     // Catch:{ all -> 0x04e7 }
                if (r2 == 0) goto L_0x04a6
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x04e7 }
                goto L_0x04a8
            L_0x04a6:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x04e7 }
            L_0x04a8:
                r10.inPreferredConfig = r0     // Catch:{ all -> 0x04e7 }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x04e7 }
                r0.<init>(r3)     // Catch:{ all -> 0x04e7 }
                r5 = 0
                android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeStream(r0, r5, r10)     // Catch:{ all -> 0x04e7 }
                r0.close()     // Catch:{ all -> 0x04e4 }
                int r0 = r10.outWidth     // Catch:{ all -> 0x04e4 }
                int r5 = r10.outHeight     // Catch:{ all -> 0x04e4 }
                r8 = 0
                r10.inJustDecodeBounds = r8     // Catch:{ all -> 0x04e4 }
                int r0 = r0 / 200
                int r5 = r5 / 200
                int r0 = java.lang.Math.max(r0, r5)     // Catch:{ all -> 0x04e4 }
                float r0 = (float) r0     // Catch:{ all -> 0x04e4 }
                int r5 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1))
                if (r5 >= 0) goto L_0x04cd
                r0 = 1065353216(0x3var_, float:1.0)
            L_0x04cd:
                int r5 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1))
                if (r5 <= 0) goto L_0x04df
                r5 = 1
            L_0x04d2:
                r8 = 2
                int r5 = r5 * 2
                int r8 = r5 * 2
                float r8 = (float) r8     // Catch:{ all -> 0x04e4 }
                int r8 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r8 < 0) goto L_0x04d2
                r10.inSampleSize = r5     // Catch:{ all -> 0x04e4 }
                goto L_0x04e2
            L_0x04df:
                int r0 = (int) r0     // Catch:{ all -> 0x04e4 }
                r10.inSampleSize = r0     // Catch:{ all -> 0x04e4 }
            L_0x04e2:
                r0 = r7
                goto L_0x04ea
            L_0x04e4:
                r0 = move-exception
                r5 = r7
                goto L_0x04f8
            L_0x04e7:
                r0 = move-exception
                goto L_0x04f7
            L_0x04e9:
                r0 = 0
            L_0x04ea:
                r5 = 0
                r7 = 0
                r9 = 0
                r22 = 0
            L_0x04ef:
                r8 = r5
                r5 = r0
                goto L_0x0500
            L_0x04f2:
                r0 = move-exception
                r26 = r12
                r25 = r13
            L_0x04f7:
                r5 = 0
            L_0x04f8:
                r7 = 0
                r8 = 0
                r9 = 0
            L_0x04fb:
                r22 = 0
            L_0x04fd:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0500:
                r0 = r22
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                int r12 = r12.type
                r20 = r14
                r13 = 1
                if (r12 != r13) goto L_0x0705
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x06fc }
                long r12 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x06fc }
                long unused = r0.lastCacheOutTime = r12     // Catch:{ all -> 0x06fc }
                java.lang.Object r2 = r1.sync     // Catch:{ all -> 0x06fc }
                monitor-enter(r2)     // Catch:{ all -> 0x06fc }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x06f9 }
                if (r0 == 0) goto L_0x051d
                monitor-exit(r2)     // Catch:{ all -> 0x06f9 }
                return
            L_0x051d:
                monitor-exit(r2)     // Catch:{ all -> 0x06f9 }
                if (r25 == 0) goto L_0x0565
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x06fc }
                java.lang.String r2 = "r"
                r0.<init>(r3, r2)     // Catch:{ all -> 0x06fc }
                java.nio.channels.FileChannel r11 = r0.getChannel()     // Catch:{ all -> 0x06fc }
                java.nio.channels.FileChannel$MapMode r12 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x06fc }
                r13 = 0
                long r15 = r3.length()     // Catch:{ all -> 0x06fc }
                java.nio.MappedByteBuffer r2 = r11.map(r12, r13, r15)     // Catch:{ all -> 0x06fc }
                android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x06fc }
                r4.<init>()     // Catch:{ all -> 0x06fc }
                r6 = 1
                r4.inJustDecodeBounds = r6     // Catch:{ all -> 0x06fc }
                int r11 = r2.limit()     // Catch:{ all -> 0x06fc }
                r12 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r12, r2, r11, r4, r6)     // Catch:{ all -> 0x06fc }
                int r6 = r4.outWidth     // Catch:{ all -> 0x06fc }
                int r4 = r4.outHeight     // Catch:{ all -> 0x06fc }
                android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x06fc }
                android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createBitmap(r6, r4, r11)     // Catch:{ all -> 0x06fc }
                int r4 = r2.limit()     // Catch:{ all -> 0x06fc }
                boolean r6 = r10.inPurgeable     // Catch:{ all -> 0x06fc }
                if (r6 != 0) goto L_0x055b
                r6 = 1
                goto L_0x055c
            L_0x055b:
                r6 = 0
            L_0x055c:
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r5, r2, r4, r11, r6)     // Catch:{ all -> 0x06fc }
                r0.close()     // Catch:{ all -> 0x06fc }
                goto L_0x05e3
            L_0x0565:
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x06fc }
                if (r0 != 0) goto L_0x0586
                if (r6 == 0) goto L_0x056c
                goto L_0x0586
            L_0x056c:
                if (r4 == 0) goto L_0x0578
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x06fc }
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x06fc }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x06fc }
                r0.<init>((java.io.File) r3, (java.io.File) r2)     // Catch:{ all -> 0x06fc }
                goto L_0x057d
            L_0x0578:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x06fc }
                r0.<init>(r3)     // Catch:{ all -> 0x06fc }
            L_0x057d:
                r2 = 0
                android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeStream(r0, r2, r10)     // Catch:{ all -> 0x06fc }
                r0.close()     // Catch:{ all -> 0x06fc }
                goto L_0x05e3
            L_0x0586:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x06fc }
                java.lang.String r2 = "r"
                r0.<init>(r3, r2)     // Catch:{ all -> 0x06fc }
                long r12 = r0.length()     // Catch:{ all -> 0x06fc }
                int r2 = (int) r12     // Catch:{ all -> 0x06fc }
                java.lang.ThreadLocal r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x06fc }
                java.lang.Object r12 = r12.get()     // Catch:{ all -> 0x06fc }
                byte[] r12 = (byte[]) r12     // Catch:{ all -> 0x06fc }
                if (r12 == 0) goto L_0x05a2
                int r13 = r12.length     // Catch:{ all -> 0x06fc }
                if (r13 < r2) goto L_0x05a2
                goto L_0x05a3
            L_0x05a2:
                r12 = 0
            L_0x05a3:
                if (r12 != 0) goto L_0x05ae
                byte[] r12 = new byte[r2]     // Catch:{ all -> 0x06fc }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x06fc }
                r13.set(r12)     // Catch:{ all -> 0x06fc }
            L_0x05ae:
                r13 = 0
                r0.readFully(r12, r13, r2)     // Catch:{ all -> 0x06fc }
                r0.close()     // Catch:{ all -> 0x06fc }
                if (r6 == 0) goto L_0x05d1
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r12, (int) r13, (int) r2, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x06fc }
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r12, r13, r2)     // Catch:{ all -> 0x06fc }
                if (r11 == 0) goto L_0x05c9
                boolean r0 = java.util.Arrays.equals(r0, r11)     // Catch:{ all -> 0x06fc }
                if (r0 != 0) goto L_0x05c7
                goto L_0x05c9
            L_0x05c7:
                r0 = 0
                goto L_0x05ca
            L_0x05c9:
                r0 = 1
            L_0x05ca:
                r4 = 0
                byte r6 = r12[r4]     // Catch:{ all -> 0x06fc }
                r4 = r6 & 255(0xff, float:3.57E-43)
                int r2 = r2 - r4
                goto L_0x05dd
            L_0x05d1:
                if (r4 == 0) goto L_0x05db
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x06fc }
                java.io.File r0 = r0.encryptionKeyPath     // Catch:{ all -> 0x06fc }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r12, (int) r4, (int) r2, (java.io.File) r0)     // Catch:{ all -> 0x06fc }
            L_0x05db:
                r0 = 0
                r4 = 0
            L_0x05dd:
                if (r0 != 0) goto L_0x05e3
                android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeByteArray(r12, r4, r2, r10)     // Catch:{ all -> 0x06fc }
            L_0x05e3:
                if (r5 != 0) goto L_0x05fb
                long r6 = r3.length()     // Catch:{ all -> 0x06fc }
                r8 = 0
                int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r0 == 0) goto L_0x05f5
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x06fc }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x06fc }
                if (r0 != 0) goto L_0x05f8
            L_0x05f5:
                r3.delete()     // Catch:{ all -> 0x06fc }
            L_0x05f8:
                r9 = 0
                goto L_0x0701
            L_0x05fb:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x06fc }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x06fc }
                if (r0 == 0) goto L_0x062d
                int r0 = r5.getWidth()     // Catch:{ all -> 0x06fc }
                float r0 = (float) r0     // Catch:{ all -> 0x06fc }
                int r2 = r5.getHeight()     // Catch:{ all -> 0x06fc }
                float r2 = (float) r2     // Catch:{ all -> 0x06fc }
                boolean r3 = r10.inPurgeable     // Catch:{ all -> 0x06fc }
                if (r3 != 0) goto L_0x062d
                int r3 = (r8 > r19 ? 1 : (r8 == r19 ? 0 : -1))
                if (r3 == 0) goto L_0x062d
                int r3 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                if (r3 == 0) goto L_0x062d
                r3 = 1101004800(0x41a00000, float:20.0)
                float r3 = r3 + r8
                int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x062d
                float r0 = r0 / r8
                int r3 = (int) r8     // Catch:{ all -> 0x06fc }
                float r2 = r2 / r0
                int r0 = (int) r2     // Catch:{ all -> 0x06fc }
                r2 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r3, r0, r2)     // Catch:{ all -> 0x06fc }
                if (r5 == r0) goto L_0x062d
                r5.recycle()     // Catch:{ all -> 0x06fc }
                r5 = r0
            L_0x062d:
                if (r9 == 0) goto L_0x064d
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x06fc }
                if (r0 == 0) goto L_0x0635
                r0 = 0
                goto L_0x0636
            L_0x0635:
                r0 = 1
            L_0x0636:
                int r2 = r5.getWidth()     // Catch:{ all -> 0x06fc }
                int r3 = r5.getHeight()     // Catch:{ all -> 0x06fc }
                int r4 = r5.getRowBytes()     // Catch:{ all -> 0x06fc }
                int r0 = org.telegram.messenger.Utilities.needInvert(r5, r0, r2, r3, r4)     // Catch:{ all -> 0x06fc }
                if (r0 == 0) goto L_0x064a
                r9 = 1
                goto L_0x064b
            L_0x064a:
                r9 = 0
            L_0x064b:
                r2 = 1
                goto L_0x064f
            L_0x064d:
                r2 = 1
                r9 = 0
            L_0x064f:
                if (r7 != r2) goto L_0x0676
                android.graphics.Bitmap$Config r0 = r5.getConfig()     // Catch:{ all -> 0x0673 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0673 }
                if (r0 != r2) goto L_0x0701
                r12 = 3
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x0673 }
                if (r0 == 0) goto L_0x0660
                r13 = 0
                goto L_0x0661
            L_0x0660:
                r13 = 1
            L_0x0661:
                int r14 = r5.getWidth()     // Catch:{ all -> 0x0673 }
                int r15 = r5.getHeight()     // Catch:{ all -> 0x0673 }
                int r16 = r5.getRowBytes()     // Catch:{ all -> 0x0673 }
                r11 = r5
                org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0673 }
                goto L_0x0701
            L_0x0673:
                r0 = move-exception
                goto L_0x06fe
            L_0x0676:
                r2 = 2
                if (r7 != r2) goto L_0x069b
                android.graphics.Bitmap$Config r0 = r5.getConfig()     // Catch:{ all -> 0x0673 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0673 }
                if (r0 != r2) goto L_0x0701
                r12 = 1
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x0673 }
                if (r0 == 0) goto L_0x0688
                r13 = 0
                goto L_0x0689
            L_0x0688:
                r13 = 1
            L_0x0689:
                int r14 = r5.getWidth()     // Catch:{ all -> 0x0673 }
                int r15 = r5.getHeight()     // Catch:{ all -> 0x0673 }
                int r16 = r5.getRowBytes()     // Catch:{ all -> 0x0673 }
                r11 = r5
                org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0673 }
                goto L_0x0701
            L_0x069b:
                r2 = 3
                if (r7 != r2) goto L_0x06ef
                android.graphics.Bitmap$Config r0 = r5.getConfig()     // Catch:{ all -> 0x0673 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0673 }
                if (r0 != r2) goto L_0x0701
                r12 = 7
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x0673 }
                if (r0 == 0) goto L_0x06ad
                r13 = 0
                goto L_0x06ae
            L_0x06ad:
                r13 = 1
            L_0x06ae:
                int r14 = r5.getWidth()     // Catch:{ all -> 0x0673 }
                int r15 = r5.getHeight()     // Catch:{ all -> 0x0673 }
                int r16 = r5.getRowBytes()     // Catch:{ all -> 0x0673 }
                r11 = r5
                org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0673 }
                r12 = 7
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x0673 }
                if (r0 == 0) goto L_0x06c5
                r13 = 0
                goto L_0x06c6
            L_0x06c5:
                r13 = 1
            L_0x06c6:
                int r14 = r5.getWidth()     // Catch:{ all -> 0x0673 }
                int r15 = r5.getHeight()     // Catch:{ all -> 0x0673 }
                int r16 = r5.getRowBytes()     // Catch:{ all -> 0x0673 }
                r11 = r5
                org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0673 }
                r12 = 7
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x0673 }
                if (r0 == 0) goto L_0x06dd
                r13 = 0
                goto L_0x06de
            L_0x06dd:
                r13 = 1
            L_0x06de:
                int r14 = r5.getWidth()     // Catch:{ all -> 0x0673 }
                int r15 = r5.getHeight()     // Catch:{ all -> 0x0673 }
                int r16 = r5.getRowBytes()     // Catch:{ all -> 0x0673 }
                r11 = r5
                org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0673 }
                goto L_0x0701
            L_0x06ef:
                if (r7 != 0) goto L_0x0701
                boolean r0 = r10.inPurgeable     // Catch:{ all -> 0x0673 }
                if (r0 == 0) goto L_0x0701
                org.telegram.messenger.Utilities.pinBitmap(r5)     // Catch:{ all -> 0x0673 }
                goto L_0x0701
            L_0x06f9:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x06f9 }
                throw r0     // Catch:{ all -> 0x06fc }
            L_0x06fc:
                r0 = move-exception
                r9 = 0
            L_0x06fe:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0701:
                r0 = 0
                r12 = 0
                goto L_0x09b4
            L_0x0705:
                r12 = 20
                if (r20 == 0) goto L_0x070a
                r12 = 0
            L_0x070a:
                if (r12 == 0) goto L_0x073b
                org.telegram.messenger.ImageLoader r13 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0737 }
                long r13 = r13.lastCacheOutTime     // Catch:{ all -> 0x0737 }
                r22 = 0
                int r28 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1))
                if (r28 == 0) goto L_0x073b
                org.telegram.messenger.ImageLoader r13 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0737 }
                long r13 = r13.lastCacheOutTime     // Catch:{ all -> 0x0737 }
                long r22 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0737 }
                r28 = r8
                r29 = r9
                long r8 = (long) r12     // Catch:{ all -> 0x0737 }
                long r22 = r22 - r8
                int r12 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1))
                if (r12 <= 0) goto L_0x073f
                int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0737 }
                r13 = 21
                if (r12 >= r13) goto L_0x073f
                java.lang.Thread.sleep(r8)     // Catch:{ all -> 0x0737 }
                goto L_0x073f
            L_0x0737:
                r9 = 0
                r12 = 0
                goto L_0x09b0
            L_0x073b:
                r28 = r8
                r29 = r9
            L_0x073f:
                org.telegram.messenger.ImageLoader r8 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x09ae }
                long r12 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x09ae }
                long unused = r8.lastCacheOutTime = r12     // Catch:{ all -> 0x09ae }
                java.lang.Object r8 = r1.sync     // Catch:{ all -> 0x09ae }
                monitor-enter(r8)     // Catch:{ all -> 0x09ae }
                boolean r9 = r1.isCancelled     // Catch:{ all -> 0x09a8 }
                if (r9 == 0) goto L_0x0751
                monitor-exit(r8)     // Catch:{ all -> 0x09a8 }
                return
            L_0x0751:
                monitor-exit(r8)     // Catch:{ all -> 0x09a8 }
                if (r2 != 0) goto L_0x076a
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0737 }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x0737 }
                if (r2 == 0) goto L_0x076a
                if (r7 != 0) goto L_0x076a
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0737 }
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation     // Catch:{ all -> 0x0737 }
                java.lang.String r2 = r2.path     // Catch:{ all -> 0x0737 }
                if (r2 == 0) goto L_0x0765
                goto L_0x076a
            L_0x0765:
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0737 }
                r10.inPreferredConfig = r2     // Catch:{ all -> 0x0737 }
                goto L_0x076e
            L_0x076a:
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x09ae }
                r10.inPreferredConfig = r2     // Catch:{ all -> 0x09ae }
            L_0x076e:
                r2 = 0
                r10.inDither = r2     // Catch:{ all -> 0x09ae }
                if (r20 == 0) goto L_0x0796
                if (r26 != 0) goto L_0x0796
                if (r15 == 0) goto L_0x0787
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0737 }
                android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ all -> 0x0737 }
                long r8 = r20.longValue()     // Catch:{ all -> 0x0737 }
                r12 = 1
                android.graphics.Bitmap r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r2, r8, r12, r10)     // Catch:{ all -> 0x0737 }
                goto L_0x0796
            L_0x0787:
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0737 }
                android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ all -> 0x0737 }
                long r8 = r20.longValue()     // Catch:{ all -> 0x0737 }
                r12 = 1
                android.graphics.Bitmap r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r2, r8, r12, r10)     // Catch:{ all -> 0x0737 }
            L_0x0796:
                if (r5 != 0) goto L_0x08a3
                if (r25 == 0) goto L_0x07e7
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0737 }
                java.lang.String r4 = "r"
                r2.<init>(r3, r4)     // Catch:{ all -> 0x0737 }
                java.nio.channels.FileChannel r30 = r2.getChannel()     // Catch:{ all -> 0x0737 }
                java.nio.channels.FileChannel$MapMode r31 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0737 }
                r32 = 0
                long r34 = r3.length()     // Catch:{ all -> 0x0737 }
                java.nio.MappedByteBuffer r4 = r30.map(r31, r32, r34)     // Catch:{ all -> 0x0737 }
                android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0737 }
                r6.<init>()     // Catch:{ all -> 0x0737 }
                r8 = 1
                r6.inJustDecodeBounds = r8     // Catch:{ all -> 0x0737 }
                int r9 = r4.limit()     // Catch:{ all -> 0x0737 }
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r11, r4, r9, r6, r8)     // Catch:{ all -> 0x07e4 }
                int r8 = r6.outWidth     // Catch:{ all -> 0x0737 }
                int r6 = r6.outHeight     // Catch:{ all -> 0x0737 }
                android.graphics.Bitmap$Config r9 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0737 }
                android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createBitmap(r8, r6, r9)     // Catch:{ all -> 0x0737 }
                int r6 = r4.limit()     // Catch:{ all -> 0x0737 }
                boolean r8 = r10.inPurgeable     // Catch:{ all -> 0x0737 }
                if (r8 != 0) goto L_0x07d5
                r8 = 1
                goto L_0x07d6
            L_0x07d5:
                r8 = 0
            L_0x07d6:
                r9 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r5, r4, r6, r9, r8)     // Catch:{ all -> 0x07e1 }
                r2.close()     // Catch:{ all -> 0x0737 }
                r9 = 0
                r12 = 0
                goto L_0x08a5
            L_0x07e1:
                r12 = r9
                goto L_0x09af
            L_0x07e4:
                r12 = r11
                goto L_0x09af
            L_0x07e7:
                boolean r2 = r10.inPurgeable     // Catch:{ all -> 0x09ae }
                if (r2 != 0) goto L_0x0844
                if (r6 == 0) goto L_0x07ee
                goto L_0x0844
            L_0x07ee:
                if (r4 == 0) goto L_0x07fa
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r2 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0737 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0737 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x0737 }
                r2.<init>((java.io.File) r3, (java.io.File) r4)     // Catch:{ all -> 0x0737 }
                goto L_0x07ff
            L_0x07fa:
                java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x09ae }
                r2.<init>(r3)     // Catch:{ all -> 0x09ae }
            L_0x07ff:
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x09ae }
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x09ae }
                org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ all -> 0x09ae }
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_document     // Catch:{ all -> 0x09ae }
                if (r4 == 0) goto L_0x083a
                androidx.exifinterface.media.ExifInterface r4 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0829 }
                r4.<init>((java.io.InputStream) r2)     // Catch:{ all -> 0x0829 }
                java.lang.String r6 = "Orientation"
                r8 = 1
                int r4 = r4.getAttributeInt(r6, r8)     // Catch:{ all -> 0x0829 }
                r6 = 3
                if (r4 == r6) goto L_0x0826
                r6 = 6
                if (r4 == r6) goto L_0x0823
                r6 = 8
                if (r4 == r6) goto L_0x0820
                goto L_0x0829
            L_0x0820:
                r9 = 270(0x10e, float:3.78E-43)
                goto L_0x082a
            L_0x0823:
                r9 = 90
                goto L_0x082a
            L_0x0826:
                r9 = 180(0xb4, float:2.52E-43)
                goto L_0x082a
            L_0x0829:
                r9 = 0
            L_0x082a:
                java.nio.channels.FileChannel r4 = r2.getChannel()     // Catch:{ all -> 0x0834 }
                r11 = 0
                r4.position(r11)     // Catch:{ all -> 0x0834 }
                goto L_0x083b
            L_0x0834:
                r24 = r9
                r9 = 0
                r12 = 0
                goto L_0x09b2
            L_0x083a:
                r9 = 0
            L_0x083b:
                r12 = 0
                android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeStream(r2, r12, r10)     // Catch:{ all -> 0x09a4 }
                r2.close()     // Catch:{ all -> 0x09a4 }
                goto L_0x08a5
            L_0x0844:
                r12 = 0
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x09af }
                java.lang.String r8 = "r"
                r2.<init>(r3, r8)     // Catch:{ all -> 0x09af }
                long r8 = r2.length()     // Catch:{ all -> 0x09af }
                int r9 = (int) r8     // Catch:{ all -> 0x09af }
                java.lang.ThreadLocal r8 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x09af }
                java.lang.Object r8 = r8.get()     // Catch:{ all -> 0x09af }
                byte[] r8 = (byte[]) r8     // Catch:{ all -> 0x09af }
                if (r8 == 0) goto L_0x0861
                int r13 = r8.length     // Catch:{ all -> 0x09af }
                if (r13 < r9) goto L_0x0861
                goto L_0x0862
            L_0x0861:
                r8 = r12
            L_0x0862:
                if (r8 != 0) goto L_0x086d
                byte[] r8 = new byte[r9]     // Catch:{ all -> 0x09af }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x09af }
                r13.set(r8)     // Catch:{ all -> 0x09af }
            L_0x086d:
                r13 = 0
                r2.readFully(r8, r13, r9)     // Catch:{ all -> 0x09af }
                r2.close()     // Catch:{ all -> 0x09af }
                if (r6 == 0) goto L_0x0890
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r8, (int) r13, (int) r9, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x09af }
                byte[] r2 = org.telegram.messenger.Utilities.computeSHA256(r8, r13, r9)     // Catch:{ all -> 0x09af }
                if (r11 == 0) goto L_0x0888
                boolean r2 = java.util.Arrays.equals(r2, r11)     // Catch:{ all -> 0x09af }
                if (r2 != 0) goto L_0x0886
                goto L_0x0888
            L_0x0886:
                r2 = 0
                goto L_0x0889
            L_0x0888:
                r2 = 1
            L_0x0889:
                r4 = 0
                byte r6 = r8[r4]     // Catch:{ all -> 0x09af }
                r4 = r6 & 255(0xff, float:3.57E-43)
                int r9 = r9 - r4
                goto L_0x089c
            L_0x0890:
                if (r4 == 0) goto L_0x089a
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x09af }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x09af }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r8, (int) r4, (int) r9, (java.io.File) r2)     // Catch:{ all -> 0x09af }
            L_0x089a:
                r2 = 0
                r4 = 0
            L_0x089c:
                if (r2 != 0) goto L_0x08a4
                android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeByteArray(r8, r4, r9, r10)     // Catch:{ all -> 0x09af }
                goto L_0x08a4
            L_0x08a3:
                r12 = 0
            L_0x08a4:
                r9 = 0
            L_0x08a5:
                if (r5 != 0) goto L_0x08bf
                if (r16 == 0) goto L_0x08bc
                long r6 = r3.length()     // Catch:{ all -> 0x09a4 }
                r10 = 0
                int r0 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
                if (r0 == 0) goto L_0x08b9
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x09a4 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x09a4 }
                if (r0 != 0) goto L_0x08bc
            L_0x08b9:
                r3.delete()     // Catch:{ all -> 0x09a4 }
            L_0x08bc:
                r0 = 0
                goto L_0x099e
            L_0x08bf:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x09a4 }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x09a4 }
                if (r2 == 0) goto L_0x098d
                int r2 = r5.getWidth()     // Catch:{ all -> 0x09a4 }
                float r2 = (float) r2     // Catch:{ all -> 0x09a4 }
                int r3 = r5.getHeight()     // Catch:{ all -> 0x09a4 }
                float r3 = (float) r3     // Catch:{ all -> 0x09a4 }
                boolean r4 = r10.inPurgeable     // Catch:{ all -> 0x09a4 }
                if (r4 != 0) goto L_0x090a
                int r4 = (r28 > r19 ? 1 : (r28 == r19 ? 0 : -1))
                if (r4 == 0) goto L_0x090a
                int r4 = (r2 > r28 ? 1 : (r2 == r28 ? 0 : -1))
                if (r4 == 0) goto L_0x090a
                r4 = 1101004800(0x41a00000, float:20.0)
                float r8 = r28 + r4
                int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r4 <= 0) goto L_0x090a
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 <= 0) goto L_0x08f9
                int r4 = (r28 > r0 ? 1 : (r28 == r0 ? 0 : -1))
                if (r4 <= 0) goto L_0x08f9
                float r0 = r2 / r28
                r8 = r28
                int r4 = (int) r8     // Catch:{ all -> 0x09a4 }
                float r0 = r3 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x09a4 }
                r6 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r0, r6)     // Catch:{ all -> 0x09a4 }
                goto L_0x0904
            L_0x08f9:
                r6 = 1
                float r4 = r3 / r0
                float r4 = r2 / r4
                int r4 = (int) r4     // Catch:{ all -> 0x09a4 }
                int r0 = (int) r0     // Catch:{ all -> 0x09a4 }
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r0, r6)     // Catch:{ all -> 0x09a4 }
            L_0x0904:
                if (r5 == r0) goto L_0x090a
                r5.recycle()     // Catch:{ all -> 0x09a4 }
                goto L_0x090b
            L_0x090a:
                r0 = r5
            L_0x090b:
                if (r0 == 0) goto L_0x098c
                if (r29 == 0) goto L_0x094d
                int r4 = r0.getWidth()     // Catch:{ all -> 0x094a }
                int r5 = r0.getHeight()     // Catch:{ all -> 0x094a }
                int r4 = r4 * r5
                r5 = 22500(0x57e4, float:3.1529E-41)
                if (r4 <= r5) goto L_0x0927
                r4 = 100
                r5 = 100
                r6 = 0
                android.graphics.Bitmap r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r0, r4, r5, r6)     // Catch:{ all -> 0x094a }
                goto L_0x0928
            L_0x0927:
                r4 = r0
            L_0x0928:
                boolean r5 = r10.inPurgeable     // Catch:{ all -> 0x094a }
                if (r5 == 0) goto L_0x092e
                r5 = 0
                goto L_0x092f
            L_0x092e:
                r5 = 1
            L_0x092f:
                int r6 = r4.getWidth()     // Catch:{ all -> 0x094a }
                int r8 = r4.getHeight()     // Catch:{ all -> 0x094a }
                int r11 = r4.getRowBytes()     // Catch:{ all -> 0x094a }
                int r5 = org.telegram.messenger.Utilities.needInvert(r4, r5, r6, r8, r11)     // Catch:{ all -> 0x094a }
                if (r5 == 0) goto L_0x0943
                r5 = 1
                goto L_0x0944
            L_0x0943:
                r5 = 0
            L_0x0944:
                if (r4 == r0) goto L_0x094e
                r4.recycle()     // Catch:{ all -> 0x097f }
                goto L_0x094e
            L_0x094a:
                r5 = r0
                goto L_0x09a4
            L_0x094d:
                r5 = 0
            L_0x094e:
                if (r7 == 0) goto L_0x0984
                r4 = 1120403456(0x42CLASSNAME, float:100.0)
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 >= 0) goto L_0x0984
                r3 = 1120403456(0x42CLASSNAME, float:100.0)
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 >= 0) goto L_0x0984
                android.graphics.Bitmap$Config r2 = r0.getConfig()     // Catch:{ all -> 0x097f }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x097f }
                if (r2 != r3) goto L_0x097c
                r14 = 3
                boolean r2 = r10.inPurgeable     // Catch:{ all -> 0x097f }
                if (r2 == 0) goto L_0x096b
                r15 = 0
                goto L_0x096c
            L_0x096b:
                r15 = 1
            L_0x096c:
                int r16 = r0.getWidth()     // Catch:{ all -> 0x097f }
                int r17 = r0.getHeight()     // Catch:{ all -> 0x097f }
                int r18 = r0.getRowBytes()     // Catch:{ all -> 0x097f }
                r13 = r0
                org.telegram.messenger.Utilities.blurBitmap(r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x097f }
            L_0x097c:
                r24 = 1
                goto L_0x0986
            L_0x097f:
                r24 = r9
                r9 = r5
                r5 = r0
                goto L_0x09b2
            L_0x0984:
                r24 = 0
            L_0x0986:
                r36 = r5
                r5 = r0
                r0 = r36
                goto L_0x0990
            L_0x098c:
                r5 = r0
            L_0x098d:
                r0 = 0
                r24 = 0
            L_0x0990:
                if (r24 != 0) goto L_0x099e
                boolean r2 = r10.inPurgeable     // Catch:{ all -> 0x099a }
                if (r2 == 0) goto L_0x099e
                org.telegram.messenger.Utilities.pinBitmap(r5)     // Catch:{ all -> 0x099a }
                goto L_0x099e
            L_0x099a:
                r24 = r9
                r9 = r0
                goto L_0x09b2
            L_0x099e:
                r36 = r9
                r9 = r0
                r0 = r36
                goto L_0x09b4
            L_0x09a4:
                r24 = r9
                r9 = 0
                goto L_0x09b2
            L_0x09a8:
                r0 = move-exception
                r12 = 0
            L_0x09aa:
                monitor-exit(r8)     // Catch:{ all -> 0x09ac }
                throw r0     // Catch:{ all -> 0x09af }
            L_0x09ac:
                r0 = move-exception
                goto L_0x09aa
            L_0x09ae:
                r12 = 0
            L_0x09af:
                r9 = 0
            L_0x09b0:
                r24 = 0
            L_0x09b2:
                r0 = r24
            L_0x09b4:
                java.lang.Thread.interrupted()
                if (r9 != 0) goto L_0x09c9
                if (r0 == 0) goto L_0x09bc
                goto L_0x09c9
            L_0x09bc:
                if (r5 == 0) goto L_0x09c4
                android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable
                r0.<init>(r5)
                goto L_0x09c5
            L_0x09c4:
                r0 = r12
            L_0x09c5:
                r1.onPostExecute(r0)
                goto L_0x0a2d
            L_0x09c9:
                if (r5 == 0) goto L_0x09d1
                org.telegram.messenger.ExtendedBitmapDrawable r2 = new org.telegram.messenger.ExtendedBitmapDrawable
                r2.<init>(r5, r9, r0)
                goto L_0x09d2
            L_0x09d1:
                r2 = r12
            L_0x09d2:
                r1.onPostExecute(r2)
                goto L_0x0a2d
            L_0x09d6:
                r12 = 0
                r0 = 1135869952(0x43b40000, float:360.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r2 = 1142947840(0x44200000, float:640.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0a0a
                java.lang.String r4 = "_"
                java.lang.String[] r3 = r3.split(r4)
                int r4 = r3.length
                r5 = 2
                if (r4 < r5) goto L_0x0a0a
                r4 = 0
                r0 = r3[r4]
                float r0 = java.lang.Float.parseFloat(r0)
                r8 = 1
                r2 = r3[r8]
                float r2 = java.lang.Float.parseFloat(r2)
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r0 = r0 * r3
                int r0 = (int) r0
                float r2 = r2 * r3
                int r2 = (int) r2
                goto L_0x0a0c
            L_0x0a0a:
                r4 = 0
                r8 = 1
            L_0x0a0c:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0a1c }
                java.io.File r3 = r3.finalFilePath     // Catch:{ all -> 0x0a1c }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0a1c }
                int r5 = r5.imageType     // Catch:{ all -> 0x0a1c }
                if (r5 != r6) goto L_0x0a17
                r4 = 1
            L_0x0a17:
                android.graphics.Bitmap r5 = org.telegram.ui.Components.SvgHelper.getBitmap(r3, r0, r2, r4)     // Catch:{ all -> 0x0a1c }
                goto L_0x0a21
            L_0x0a1c:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r5 = r12
            L_0x0a21:
                if (r5 == 0) goto L_0x0a29
                android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable
                r0.<init>(r5)
                goto L_0x0a2a
            L_0x0a29:
                r0 = r12
            L_0x0a2a:
                r1.onPostExecute(r0)
            L_0x0a2d:
                return
            L_0x0a2e:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0a2e }
                goto L_0x0a32
            L_0x0a31:
                throw r0
            L_0x0a32:
                goto L_0x0a31
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new Runnable(drawable) {
                private final /* synthetic */ Drawable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.CacheOutTask.this.lambda$onPostExecute$1$ImageLoader$CacheOutTask(this.f$1);
                }
            });
        }

        /* JADX WARNING: Failed to insert additional move for type inference */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onPostExecute$1$ImageLoader$CacheOutTask(android.graphics.drawable.Drawable r4) {
            /*
                r3 = this;
                boolean r0 = r4 instanceof org.telegram.ui.Components.RLottieDrawable
                r1 = 0
                if (r0 == 0) goto L_0x003b
                org.telegram.ui.Components.RLottieDrawable r4 = (org.telegram.ui.Components.RLottieDrawable) r4
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r3.cacheImage
                java.lang.String r2 = r2.key
                java.lang.Object r0 = r0.get(r2)
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x0027
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r3.cacheImage
                java.lang.String r2 = r2.key
                r0.put(r2, r4)
                goto L_0x002b
            L_0x0027:
                r4.recycle()
                r4 = r0
            L_0x002b:
                if (r4 == 0) goto L_0x007f
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r3.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r3.cacheImage
                java.lang.String r1 = r0.key
                goto L_0x007f
            L_0x003b:
                boolean r0 = r4 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                if (r0 == 0) goto L_0x0040
                goto L_0x007f
            L_0x0040:
                boolean r0 = r4 instanceof android.graphics.drawable.BitmapDrawable
                if (r0 == 0) goto L_0x007e
                android.graphics.drawable.BitmapDrawable r4 = (android.graphics.drawable.BitmapDrawable) r4
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.memCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r3.cacheImage
                java.lang.String r2 = r2.key
                java.lang.Object r0 = r0.get(r2)
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x0066
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.memCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r3.cacheImage
                java.lang.String r2 = r2.key
                r0.put(r2, r4)
                goto L_0x006e
            L_0x0066:
                android.graphics.Bitmap r4 = r4.getBitmap()
                r4.recycle()
                r4 = r0
            L_0x006e:
                if (r4 == 0) goto L_0x007f
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r3.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r3.cacheImage
                java.lang.String r1 = r0.key
                goto L_0x007f
            L_0x007e:
                r4 = r1
            L_0x007f:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.DispatchQueue r0 = r0.imageLoadQueue
                org.telegram.messenger.-$$Lambda$ImageLoader$CacheOutTask$L2FS7HdPO2NRh4SX6OgH248fhO4 r2 = new org.telegram.messenger.-$$Lambda$ImageLoader$CacheOutTask$L2FS7HdPO2NRh4SX6OgH248fhO4
                r2.<init>(r4, r1)
                r0.postRunnable(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.lambda$onPostExecute$1$ImageLoader$CacheOutTask(android.graphics.drawable.Drawable):void");
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$CacheOutTask(Drawable drawable, String str) {
            this.cacheImage.setImageAndClear(drawable, str);
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x0012 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void cancel() {
            /*
                r2 = this;
                java.lang.Object r0 = r2.sync
                monitor-enter(r0)
                r1 = 1
                r2.isCancelled = r1     // Catch:{ Exception -> 0x0012 }
                java.lang.Thread r1 = r2.runningThread     // Catch:{ Exception -> 0x0012 }
                if (r1 == 0) goto L_0x0012
                java.lang.Thread r1 = r2.runningThread     // Catch:{ Exception -> 0x0012 }
                r1.interrupt()     // Catch:{ Exception -> 0x0012 }
                goto L_0x0012
            L_0x0010:
                r1 = move-exception
                goto L_0x0014
            L_0x0012:
                monitor-exit(r0)     // Catch:{ all -> 0x0010 }
                return
            L_0x0014:
                monitor-exit(r0)     // Catch:{ all -> 0x0010 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.cancel():void");
        }
    }

    /* access modifiers changed from: private */
    public static Bitmap getStrippedPhotoBitmap(byte[] bArr, String str) {
        int length = (bArr.length - 3) + Bitmaps.header.length + Bitmaps.footer.length;
        byte[] bArr2 = bytesLocal.get();
        if (bArr2 == null || bArr2.length < length) {
            bArr2 = null;
        }
        if (bArr2 == null) {
            bArr2 = new byte[length];
            bytesLocal.set(bArr2);
        }
        byte[] bArr3 = Bitmaps.header;
        System.arraycopy(bArr3, 0, bArr2, 0, bArr3.length);
        System.arraycopy(bArr, 3, bArr2, Bitmaps.header.length, bArr.length - 3);
        System.arraycopy(Bitmaps.footer, 0, bArr2, (Bitmaps.header.length + bArr.length) - 3, Bitmaps.footer.length);
        bArr2[164] = bArr[1];
        bArr2[166] = bArr[2];
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr2, 0, length);
        if (decodeByteArray != null && !TextUtils.isEmpty(str) && str.contains("b")) {
            Utilities.blurBitmap(decodeByteArray, 3, 1, decodeByteArray.getWidth(), decodeByteArray.getHeight(), decodeByteArray.getRowBytes());
        }
        return decodeByteArray;
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

        public void addImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf >= 0) {
                this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i2));
                return;
            }
            this.imageReceiverArray.add(imageReceiver);
            this.imageReceiverGuidsArray.add(Integer.valueOf(i2));
            this.keys.add(str);
            this.filters.add(str2);
            this.types.add(Integer.valueOf(i));
            ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(i), this);
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf != -1) {
                if (this.types.get(indexOf).intValue() != i) {
                    ArrayList<ImageReceiver> arrayList = this.imageReceiverArray;
                    indexOf = arrayList.subList(indexOf + 1, arrayList.size()).indexOf(imageReceiver);
                    if (indexOf == -1) {
                        return;
                    }
                }
                this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i2));
                this.keys.set(indexOf, str);
                this.filters.set(indexOf, str2);
            }
        }

        public void setImageReceiverGuid(ImageReceiver imageReceiver, int i) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf != -1) {
                this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i));
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int i = this.type;
            int i2 = 0;
            while (i2 < this.imageReceiverArray.size()) {
                ImageReceiver imageReceiver2 = this.imageReceiverArray.get(i2);
                if (imageReceiver2 == null || imageReceiver2 == imageReceiver) {
                    this.imageReceiverArray.remove(i2);
                    this.imageReceiverGuidsArray.remove(i2);
                    this.keys.remove(i2);
                    this.filters.remove(i2);
                    i = this.types.remove(i2).intValue();
                    if (imageReceiver2 != null) {
                        ImageLoader.this.imageLoadingByTag.remove(imageReceiver2.getTag(i));
                    }
                    i2--;
                }
                i2++;
            }
            if (this.imageReceiverArray.isEmpty()) {
                if (this.imageLocation != null && !ImageLoader.this.forceLoadingImages.containsKey(this.key)) {
                    ImageLocation imageLocation2 = this.imageLocation;
                    if (imageLocation2.location != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.location, this.ext);
                    } else if (imageLocation2.document != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.document);
                    } else if (imageLocation2.secureDocument != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.secureDocument);
                    } else if (imageLocation2.webFile != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.webFile);
                    }
                }
                if (this.cacheTask != null) {
                    if (i == 1) {
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

        public void setImageAndClear(Drawable drawable, String str) {
            if (drawable != null) {
                AndroidUtilities.runOnUIThread(new Runnable(drawable, new ArrayList(this.imageReceiverArray), new ArrayList(this.imageReceiverGuidsArray), str) {
                    private final /* synthetic */ Drawable f$1;
                    private final /* synthetic */ ArrayList f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ String f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        ImageLoader.CacheImage.this.lambda$setImageAndClear$0$ImageLoader$CacheImage(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
            for (int i = 0; i < this.imageReceiverArray.size(); i++) {
                ImageLoader.this.imageLoadingByTag.remove(this.imageReceiverArray.get(i).getTag(this.type));
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

        public /* synthetic */ void lambda$setImageAndClear$0$ImageLoader$CacheImage(Drawable drawable, ArrayList arrayList, ArrayList arrayList2, String str) {
            AnimatedFileDrawable animatedFileDrawable;
            int i = 0;
            if (drawable instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable animatedFileDrawable2 = (AnimatedFileDrawable) drawable;
                boolean z = false;
                while (i < arrayList.size()) {
                    ImageReceiver imageReceiver = (ImageReceiver) arrayList.get(i);
                    if (i == 0) {
                        animatedFileDrawable = animatedFileDrawable2;
                    } else {
                        animatedFileDrawable = animatedFileDrawable2.makeCopy();
                    }
                    if (imageReceiver.setImageBitmapByKey(animatedFileDrawable, this.key, this.type, false, ((Integer) arrayList2.get(i)).intValue())) {
                        if (animatedFileDrawable == animatedFileDrawable2) {
                            z = true;
                        }
                    } else if (animatedFileDrawable != animatedFileDrawable2) {
                        animatedFileDrawable.recycle();
                    }
                    i++;
                }
                if (!z) {
                    animatedFileDrawable2.recycle();
                }
            } else {
                while (i < arrayList.size()) {
                    ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(drawable, this.key, this.types.get(i).intValue(), false, ((Integer) arrayList2.get(i)).intValue());
                    i++;
                }
            }
            if (str != null) {
                ImageLoader.this.decrementUseCount(str);
            }
        }
    }

    public static ImageLoader getInstance() {
        ImageLoader imageLoader = Instance;
        if (imageLoader == null) {
            synchronized (ImageLoader.class) {
                imageLoader = Instance;
                if (imageLoader == null) {
                    imageLoader = new ImageLoader();
                    Instance = imageLoader;
                }
            }
        }
        return imageLoader;
    }

    public ImageLoader() {
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
        this.lastProgressUpdateTime = 0;
        this.telegramPath = null;
        boolean z = true;
        this.thumbGeneratingQueue.setPriority(1);
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
        z = memoryClass < 192 ? false : z;
        this.canForce8888 = z;
        this.memCache = new LruCache<BitmapDrawable>(Math.min(z ? 30 : 15, memoryClass / 7) * 1024 * 1024) {
            /* access modifiers changed from: protected */
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean z, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
                if (ImageLoader.this.ignoreRemoval == null || !ImageLoader.this.ignoreRemoval.equals(str)) {
                    Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                    if (num == null || num.intValue() == 0) {
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    }
                }
            }
        };
        this.lottieMemCache = new LruCache<RLottieDrawable>(10485760) {
            /* access modifiers changed from: protected */
            public int sizeOf(String str, RLottieDrawable rLottieDrawable) {
                return rLottieDrawable.getIntrinsicWidth() * rLottieDrawable.getIntrinsicHeight() * 4 * 2;
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean z, String str, RLottieDrawable rLottieDrawable, RLottieDrawable rLottieDrawable2) {
                Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                if (num == null || num.intValue() == 0) {
                    rLottieDrawable.recycle();
                }
            }
        };
        SparseArray sparseArray = new SparseArray();
        File cacheDir = AndroidUtilities.getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            new File(cacheDir, ".nomedia").createNewFile();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        sparseArray.put(4, cacheDir);
        for (final int i = 0; i < 3; i++) {
            FileLoader.getInstance(i).setDelegate(new FileLoader.FileLoaderDelegate() {
                public void fileUploadProgressChanged(String str, float f, boolean z) {
                    ImageLoader.this.fileProgresses.put(str, Float.valueOf(f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                        long unused = ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                        AndroidUtilities.runOnUIThread(new Runnable(i, str, f, z) {
                            private final /* synthetic */ int f$0;
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ float f$2;
                            private final /* synthetic */ boolean f$3;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileUploadProgressChanged, this.f$1, Float.valueOf(this.f$2), Boolean.valueOf(this.f$3));
                            }
                        });
                    }
                }

                public void fileDidUploaded(String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    Utilities.stageQueue.postRunnable(new Runnable(i, str, inputFile, inputEncryptedFile, bArr, bArr2, j) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ TLRPC.InputFile f$3;
                        private final /* synthetic */ TLRPC.InputEncryptedFile f$4;
                        private final /* synthetic */ byte[] f$5;
                        private final /* synthetic */ byte[] f$6;
                        private final /* synthetic */ long f$7;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidUploaded$2$ImageLoader$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidUploaded$2$ImageLoader$3(int i, String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    AndroidUtilities.runOnUIThread(new Runnable(i, str, inputFile, inputEncryptedFile, bArr, bArr2, j) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ TLRPC.InputFile f$2;
                        private final /* synthetic */ TLRPC.InputEncryptedFile f$3;
                        private final /* synthetic */ byte[] f$4;
                        private final /* synthetic */ byte[] f$5;
                        private final /* synthetic */ long f$6;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void run() {
                            NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileDidUpload, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, Long.valueOf(this.f$6));
                        }
                    });
                    ImageLoader.this.fileProgresses.remove(str);
                }

                public void fileDidFailedUpload(String str, boolean z) {
                    Utilities.stageQueue.postRunnable(new Runnable(i, str, z) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ boolean f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidFailedUpload$4$ImageLoader$3(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidFailedUpload$4$ImageLoader$3(int i, String str, boolean z) {
                    AndroidUtilities.runOnUIThread(new Runnable(i, str, z) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ boolean f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileDidFailUpload, this.f$1, Boolean.valueOf(this.f$2));
                        }
                    });
                    ImageLoader.this.fileProgresses.remove(str);
                }

                public void fileDidLoaded(String str, File file, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new Runnable(file, str, i, i) {
                        private final /* synthetic */ File f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ int f$3;
                        private final /* synthetic */ int f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidLoaded$5$ImageLoader$3(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidLoaded$5$ImageLoader$3(File file, String str, int i, int i2) {
                    if (SharedConfig.saveToGallery && ImageLoader.this.telegramPath != null && file != null && ((str.endsWith(".mp4") || str.endsWith(".jpg")) && file.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                        AndroidUtilities.addMediaToGallery(file.toString());
                    }
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileDidLoad, str, file);
                    ImageLoader.this.fileDidLoaded(str, file, i2);
                }

                public void fileDidFailedLoad(String str, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new Runnable(str, i, i) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ int f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidFailedLoad$6$ImageLoader$3(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$3(String str, int i, int i2) {
                    ImageLoader.this.fileDidFailedLoad(str, i);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.fileDidFailToLoad, str, Integer.valueOf(i));
                }

                public void fileLoadProgressChanged(String str, float f) {
                    ImageLoader.this.fileProgresses.put(str, Float.valueOf(f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                        long unused = ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                        AndroidUtilities.runOnUIThread(new Runnable(i, str, f) {
                            private final /* synthetic */ int f$0;
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ float f$2;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.f$1, Float.valueOf(this.f$2));
                            }
                        });
                    }
                }
            });
        }
        FileLoader.setMediaDirs(sparseArray);
        AnonymousClass4 r0 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("file system changed");
                }
                $$Lambda$ImageLoader$4$C7Gf_cAEPSagerixfg5JW73rtw r3 = new Runnable() {
                    public final void run() {
                        ImageLoader.AnonymousClass4.this.lambda$onReceive$0$ImageLoader$4();
                    }
                };
                if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                    AndroidUtilities.runOnUIThread(r3, 1000);
                } else {
                    r3.run();
                }
            }

            public /* synthetic */ void lambda$onReceive$0$ImageLoader$4() {
                ImageLoader.this.checkMediaPaths();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_NOFS");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addDataScheme("file");
        try {
            ApplicationLoader.applicationContext.registerReceiver(r0, intentFilter);
        } catch (Throwable unused) {
        }
        checkMediaPaths();
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new Runnable() {
            public final void run() {
                ImageLoader.this.lambda$checkMediaPaths$1$ImageLoader();
            }
        });
    }

    public /* synthetic */ void lambda$checkMediaPaths$1$ImageLoader() {
        AndroidUtilities.runOnUIThread(new Runnable(createMediaPaths()) {
            private final /* synthetic */ SparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                FileLoader.setMediaDirs(this.f$0);
            }
        });
    }

    public void addTestWebFile(String str, WebFile webFile) {
        if (str != null && webFile != null) {
            this.testWebFile.put(str, webFile);
        }
    }

    public void removeTestWebFile(String str) {
        if (str != null) {
            this.testWebFile.remove(str);
        }
    }

    public SparseArray<File> createMediaPaths() {
        SparseArray<File> sparseArray = new SparseArray<>();
        File cacheDir = AndroidUtilities.getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            new File(cacheDir, ".nomedia").createNewFile();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        sparseArray.put(4, cacheDir);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cache path = " + cacheDir);
        }
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                this.telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
                this.telegramPath.mkdirs();
                if (this.telegramPath.isDirectory()) {
                    try {
                        File file = new File(this.telegramPath, "Telegram Images");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 0)) {
                            sparseArray.put(0, file);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + file);
                            }
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                    try {
                        File file2 = new File(this.telegramPath, "Telegram Video");
                        file2.mkdir();
                        if (file2.isDirectory() && canMoveFiles(cacheDir, file2, 2)) {
                            sparseArray.put(2, file2);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("video path = " + file2);
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                    try {
                        File file3 = new File(this.telegramPath, "Telegram Audio");
                        file3.mkdir();
                        if (file3.isDirectory() && canMoveFiles(cacheDir, file3, 1)) {
                            new File(file3, ".nomedia").createNewFile();
                            sparseArray.put(1, file3);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("audio path = " + file3);
                            }
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                    try {
                        File file4 = new File(this.telegramPath, "Telegram Documents");
                        file4.mkdir();
                        if (file4.isDirectory() && canMoveFiles(cacheDir, file4, 3)) {
                            new File(file4, ".nomedia").createNewFile();
                            sparseArray.put(3, file4);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("documents path = " + file4);
                            }
                        }
                    } catch (Exception e6) {
                        FileLog.e((Throwable) e6);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e7) {
            FileLog.e((Throwable) e7);
        }
        return sparseArray;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x007a A[SYNTHETIC, Splitter:B:31:0x007a] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0086 A[SYNTHETIC, Splitter:B:36:0x0086] */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canMoveFiles(java.io.File r5, java.io.File r6, int r7) {
        /*
            r4 = this;
            r0 = 1
            r1 = 0
            if (r7 != 0) goto L_0x0018
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999_temp.jpg"
            r7.<init>(r5, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999.jpg"
            r5.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            goto L_0x004f
        L_0x0013:
            r5 = move-exception
            goto L_0x0084
        L_0x0016:
            r5 = move-exception
            goto L_0x0075
        L_0x0018:
            r2 = 3
            if (r7 != r2) goto L_0x002a
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999_temp.doc"
            r7.<init>(r5, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999.doc"
            r5.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            goto L_0x004f
        L_0x002a:
            if (r7 != r0) goto L_0x003b
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999_temp.ogg"
            r7.<init>(r5, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999.ogg"
            r5.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            goto L_0x004f
        L_0x003b:
            r2 = 2
            if (r7 != r2) goto L_0x004d
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999_temp.mp4"
            r7.<init>(r5, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "000000000_999999.mp4"
            r5.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            goto L_0x004f
        L_0x004d:
            r5 = r1
            r7 = r5
        L_0x004f:
            r6 = 1024(0x400, float:1.435E-42)
            byte[] r6 = new byte[r6]     // Catch:{ Exception -> 0x0016 }
            r7.createNewFile()     // Catch:{ Exception -> 0x0016 }
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0016 }
            java.lang.String r3 = "rws"
            r2.<init>(r7, r3)     // Catch:{ Exception -> 0x0016 }
            r2.write(r6)     // Catch:{ Exception -> 0x0073, all -> 0x0070 }
            r2.close()     // Catch:{ Exception -> 0x0073, all -> 0x0070 }
            boolean r6 = r7.renameTo(r5)     // Catch:{ Exception -> 0x0016 }
            r7.delete()     // Catch:{ Exception -> 0x0016 }
            r5.delete()     // Catch:{ Exception -> 0x0016 }
            if (r6 == 0) goto L_0x0082
            return r0
        L_0x0070:
            r5 = move-exception
            r1 = r2
            goto L_0x0084
        L_0x0073:
            r5 = move-exception
            r1 = r2
        L_0x0075:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x0013 }
            if (r1 == 0) goto L_0x0082
            r1.close()     // Catch:{ Exception -> 0x007e }
            goto L_0x0082
        L_0x007e:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x0082:
            r5 = 0
            return r5
        L_0x0084:
            if (r1 == 0) goto L_0x008e
            r1.close()     // Catch:{ Exception -> 0x008a }
            goto L_0x008e
        L_0x008a:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x008e:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.canMoveFiles(java.io.File, java.io.File, int):boolean");
    }

    public Float getFileProgress(String str) {
        if (str == null) {
            return null;
        }
        return this.fileProgresses.get(str);
    }

    public String getReplacedKey(String str) {
        if (str == null) {
            return null;
        }
        return this.replacedBitmaps.get(str);
    }

    private void performReplace(String str, String str2) {
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        this.replacedBitmaps.put(str, str2);
        if (bitmapDrawable != null) {
            BitmapDrawable bitmapDrawable2 = this.memCache.get(str2);
            boolean z = false;
            if (!(bitmapDrawable2 == null || bitmapDrawable2.getBitmap() == null || bitmapDrawable.getBitmap() == null)) {
                Bitmap bitmap = bitmapDrawable2.getBitmap();
                Bitmap bitmap2 = bitmapDrawable.getBitmap();
                if (bitmap.getWidth() > bitmap2.getWidth() || bitmap.getHeight() > bitmap2.getHeight()) {
                    z = true;
                }
            }
            if (!z) {
                this.ignoreRemoval = str;
                this.memCache.remove(str);
                this.memCache.put(str2, bitmapDrawable);
                this.ignoreRemoval = null;
            } else {
                this.memCache.remove(str);
            }
        }
        Integer num = this.bitmapUseCounts.get(str);
        if (num != null) {
            this.bitmapUseCounts.put(str2, num);
            this.bitmapUseCounts.remove(str);
        }
    }

    public void incrementUseCount(String str) {
        Integer num = this.bitmapUseCounts.get(str);
        if (num == null) {
            this.bitmapUseCounts.put(str, 1);
        } else {
            this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String str) {
        Integer num = this.bitmapUseCounts.get(str);
        if (num == null) {
            return true;
        }
        if (num.intValue() == 1) {
            this.bitmapUseCounts.remove(str);
            return true;
        }
        this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() - 1));
        return false;
    }

    public void removeImage(String str) {
        this.bitmapUseCounts.remove(str);
        this.memCache.remove(str);
    }

    public boolean isInMemCache(String str, boolean z) {
        if (z) {
            return this.lottieMemCache.get(str) != null;
        }
        if (this.memCache.get(str) != null) {
            return true;
        }
        return false;
    }

    public void clearMemory() {
        this.memCache.evictAll();
        this.lottieMemCache.evictAll();
    }

    private void removeFromWaitingForThumb(int i, ImageReceiver imageReceiver) {
        String str = this.waitingForQualityThumbByTag.get(i);
        if (str != null) {
            ThumbGenerateInfo thumbGenerateInfo = this.waitingForQualityThumb.get(str);
            if (thumbGenerateInfo != null) {
                int indexOf = thumbGenerateInfo.imageReceiverArray.indexOf(imageReceiver);
                if (indexOf >= 0) {
                    thumbGenerateInfo.imageReceiverArray.remove(indexOf);
                    thumbGenerateInfo.imageReceiverGuidsArray.remove(indexOf);
                }
                if (thumbGenerateInfo.imageReceiverArray.isEmpty()) {
                    this.waitingForQualityThumb.remove(str);
                }
            }
            this.waitingForQualityThumbByTag.remove(i);
        }
    }

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, boolean z) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new Runnable(z, imageReceiver) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ ImageReceiver f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ImageLoader.this.lambda$cancelLoadingForImageReceiver$2$ImageLoader(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$cancelLoadingForImageReceiver$2$ImageLoader(boolean z, ImageReceiver imageReceiver) {
        int i = 0;
        while (true) {
            int i2 = 3;
            if (i >= 3) {
                return;
            }
            if (i <= 0 || z) {
                if (i == 0) {
                    i2 = 1;
                } else if (i == 1) {
                    i2 = 0;
                }
                int tag = imageReceiver.getTag(i2);
                if (tag != 0) {
                    if (i == 0) {
                        removeFromWaitingForThumb(tag, imageReceiver);
                    }
                    CacheImage cacheImage = this.imageLoadingByTag.get(tag);
                    if (cacheImage != null) {
                        cacheImage.removeImageReceiver(imageReceiver);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public BitmapDrawable getAnyImageFromMemory(String str) {
        ArrayList<String> filterKeys;
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        if (bitmapDrawable != null || (filterKeys = this.memCache.getFilterKeys(str)) == null || filterKeys.isEmpty()) {
            return bitmapDrawable;
        }
        LruCache<BitmapDrawable> lruCache = this.memCache;
        return lruCache.get(str + "@" + filterKeys.get(0));
    }

    public BitmapDrawable getImageFromMemory(TLObject tLObject, String str, String str2) {
        String str3 = null;
        if (tLObject == null && str == null) {
            return null;
        }
        if (str != null) {
            str3 = Utilities.MD5(str);
        } else if (tLObject instanceof TLRPC.FileLocation) {
            TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) tLObject;
            str3 = fileLocation.volume_id + "_" + fileLocation.local_id;
        } else if (tLObject instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) tLObject;
            str3 = document.dc_id + "_" + document.id;
        } else if (tLObject instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) tLObject;
            str3 = secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id;
        } else if (tLObject instanceof WebFile) {
            str3 = Utilities.MD5(((WebFile) tLObject).url);
        }
        if (str2 != null) {
            str3 = str3 + "@" + str2;
        }
        return this.memCache.get(str3);
    }

    /* access modifiers changed from: private */
    /* renamed from: replaceImageInCacheInternal */
    public void lambda$replaceImageInCache$3$ImageLoader(String str, String str2, ImageLocation imageLocation) {
        ArrayList<String> filterKeys = this.memCache.getFilterKeys(str);
        if (filterKeys != null) {
            for (int i = 0; i < filterKeys.size(); i++) {
                String str3 = filterKeys.get(i);
                String str4 = str + "@" + str3;
                String str5 = str2 + "@" + str3;
                performReplace(str4, str5);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str4, str5, imageLocation);
            }
            return;
        }
        performReplace(str, str2);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, imageLocation);
    }

    public void replaceImageInCache(String str, String str2, ImageLocation imageLocation, boolean z) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable(str, str2, imageLocation) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ ImageLocation f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ImageLoader.this.lambda$replaceImageInCache$3$ImageLoader(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            lambda$replaceImageInCache$3$ImageLoader(str, str2, imageLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmapDrawable, String str) {
        this.memCache.put(str, bitmapDrawable);
    }

    private void generateThumb(int i, File file, ThumbGenerateInfo thumbGenerateInfo) {
        if ((i == 0 || i == 2 || i == 3) && file != null && thumbGenerateInfo != null) {
            if (this.thumbGenerateTasks.get(FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(i, file, thumbGenerateInfo));
            }
        }
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        String imageKey;
        if (imageReceiver != null && (imageKey = imageReceiver.getImageKey()) != null) {
            this.imageLoadQueue.postRunnable(new Runnable(imageKey) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.this.lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(String str) {
        Integer remove = this.forceLoadingImages.remove(str);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String str, String str2, String str3, ImageLocation imageLocation, String str4, int i, int i2, int i3, int i4, int i5) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i6 = i3;
        if (imageReceiver2 != null && str2 != null && str != null && imageLocation != null) {
            int tag = imageReceiver2.getTag(i6);
            if (tag == 0) {
                tag = this.lastImageNum;
                imageReceiver2.setTag(tag, i6);
                this.lastImageNum++;
                if (this.lastImageNum == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
            }
            int i7 = tag;
            boolean isNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            Object parentObject = imageReceiver.getParentObject();
            TLRPC.Document qulityThumbDocument = imageReceiver.getQulityThumbDocument();
            boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            int currentAccount = imageReceiver.getCurrentAccount();
            boolean z = i6 == 0 && imageReceiver.isCurrentKeyQuality();
            $$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js r20 = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            $$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js r0 = new Runnable(this, i4, str2, str, i7, imageReceiver, i5, str4, i3, imageLocation, z, parentObject, qulityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, i2, i, str3, currentAccount) {
                private final /* synthetic */ ImageLoader f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$10;
                private final /* synthetic */ Object f$11;
                private final /* synthetic */ TLRPC.Document f$12;
                private final /* synthetic */ boolean f$13;
                private final /* synthetic */ boolean f$14;
                private final /* synthetic */ int f$15;
                private final /* synthetic */ int f$16;
                private final /* synthetic */ String f$17;
                private final /* synthetic */ int f$18;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ ImageReceiver f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ String f$7;
                private final /* synthetic */ int f$8;
                private final /* synthetic */ ImageLocation f$9;

                {
                    this.f$0 = r3;
                    this.f$1 = r4;
                    this.f$2 = r5;
                    this.f$3 = r6;
                    this.f$4 = r7;
                    this.f$5 = r8;
                    this.f$6 = r9;
                    this.f$7 = r10;
                    this.f$8 = r11;
                    this.f$9 = r12;
                    this.f$10 = r13;
                    this.f$11 = r14;
                    this.f$12 = r15;
                    this.f$13 = r16;
                    this.f$14 = r17;
                    this.f$15 = r18;
                    this.f$16 = r19;
                    this.f$17 = r20;
                    this.f$18 = r21;
                }

                public final void run() {
                    ImageLoader imageLoader = this.f$0;
                    ImageLoader imageLoader2 = imageLoader;
                    imageLoader2.lambda$createLoadOperationForImageReceiver$5$ImageLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18);
                }
            };
            dispatchQueue.postRunnable(r20);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0195, code lost:
        if (r8.exists() == false) goto L_0x0197;
     */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0420  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01ed  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r22, java.lang.String r23, java.lang.String r24, int r25, org.telegram.messenger.ImageReceiver r26, int r27, java.lang.String r28, int r29, org.telegram.messenger.ImageLocation r30, boolean r31, java.lang.Object r32, org.telegram.tgnet.TLRPC.Document r33, boolean r34, boolean r35, int r36, int r37, java.lang.String r38, int r39) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r23
            r9 = r24
            r10 = r25
            r11 = r26
            r12 = r28
            r13 = r30
            r14 = r32
            r15 = r33
            r8 = r36
            r7 = r37
            r6 = 0
            r5 = 2
            if (r1 == r5) goto L_0x00a8
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            java.lang.Object r3 = r3.get(r2)
            org.telegram.messenger.ImageLoader$CacheImage r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r4 = r0.imageLoadingByKeys
            java.lang.Object r4 = r4.get(r9)
            org.telegram.messenger.ImageLoader$CacheImage r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4
            android.util.SparseArray<org.telegram.messenger.ImageLoader$CacheImage> r5 = r0.imageLoadingByTag
            java.lang.Object r5 = r5.get(r10)
            org.telegram.messenger.ImageLoader$CacheImage r5 = (org.telegram.messenger.ImageLoader.CacheImage) r5
            if (r5 == 0) goto L_0x0075
            if (r5 != r4) goto L_0x0046
            r9 = r27
            r5.setImageReceiverGuid(r11, r9)
            r17 = r3
            r16 = r4
            r3 = 1
            r9 = 1
            r18 = 0
            goto L_0x007d
        L_0x0046:
            r9 = r27
            if (r5 != r3) goto L_0x006a
            r17 = r3
            if (r4 != 0) goto L_0x0063
            r3 = r5
            r16 = r4
            r5 = 1
            r4 = r26
            r9 = 1
            r5 = r24
            r18 = 0
            r6 = r28
            r7 = r29
            r8 = r27
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x0068
        L_0x0063:
            r16 = r4
            r9 = 1
            r18 = 0
        L_0x0068:
            r3 = 1
            goto L_0x007d
        L_0x006a:
            r17 = r3
            r16 = r4
            r9 = 1
            r18 = 0
            r5.removeImageReceiver(r11)
            goto L_0x007c
        L_0x0075:
            r17 = r3
            r16 = r4
            r9 = 1
            r18 = 0
        L_0x007c:
            r3 = 0
        L_0x007d:
            if (r3 != 0) goto L_0x0092
            if (r16 == 0) goto L_0x0092
            r3 = r16
            r4 = r26
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r6 = 1
            goto L_0x0093
        L_0x0092:
            r6 = r3
        L_0x0093:
            if (r6 != 0) goto L_0x00ac
            if (r17 == 0) goto L_0x00ac
            r3 = r17
            r4 = r26
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r6 = 1
            goto L_0x00ac
        L_0x00a8:
            r9 = 1
            r18 = 0
            r6 = 0
        L_0x00ac:
            if (r6 != 0) goto L_0x0584
            java.lang.String r3 = r13.path
            java.lang.String r4 = "_"
            java.lang.String r8 = "athumb"
            r7 = 4
            if (r3 == 0) goto L_0x0111
            java.lang.String r10 = "http"
            boolean r10 = r3.startsWith(r10)
            if (r10 != 0) goto L_0x0109
            boolean r10 = r3.startsWith(r8)
            if (r10 != 0) goto L_0x0109
            java.lang.String r10 = "thumb://"
            boolean r10 = r3.startsWith(r10)
            java.lang.String r15 = ":"
            if (r10 == 0) goto L_0x00e5
            r10 = 8
            int r10 = r3.indexOf(r15, r10)
            if (r10 < 0) goto L_0x00e3
            java.io.File r15 = new java.io.File
            int r10 = r10 + r9
            java.lang.String r3 = r3.substring(r10)
            r15.<init>(r3)
            goto L_0x0107
        L_0x00e3:
            r15 = 0
            goto L_0x0107
        L_0x00e5:
            java.lang.String r10 = "vthumb://"
            boolean r10 = r3.startsWith(r10)
            if (r10 == 0) goto L_0x0101
            r10 = 9
            int r10 = r3.indexOf(r15, r10)
            if (r10 < 0) goto L_0x00e3
            java.io.File r15 = new java.io.File
            int r10 = r10 + r9
            java.lang.String r3 = r3.substring(r10)
            r15.<init>(r3)
            goto L_0x0107
        L_0x0101:
            java.io.File r10 = new java.io.File
            r10.<init>(r3)
            r15 = r10
        L_0x0107:
            r3 = 1
            goto L_0x010b
        L_0x0109:
            r3 = 0
            r15 = 0
        L_0x010b:
            r19 = r8
            r5 = 2
            r7 = 0
            goto L_0x01fb
        L_0x0111:
            if (r1 != 0) goto L_0x01f5
            if (r31 == 0) goto L_0x01f5
            boolean r3 = r14 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x012f
            r3 = r14
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r15 = r3.getDocument()
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            java.lang.String r6 = r5.attachPath
            java.io.File r5 = org.telegram.messenger.FileLoader.getPathToMessage(r5)
            int r3 = r3.getMediaType()
            r9 = r3
            r3 = 0
            goto L_0x0148
        L_0x012f:
            if (r15 == 0) goto L_0x0143
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r9)
            boolean r3 = org.telegram.messenger.MessageObject.isVideoDocument(r33)
            if (r3 == 0) goto L_0x013d
            r3 = 2
            goto L_0x013e
        L_0x013d:
            r3 = 3
        L_0x013e:
            r9 = r3
            r5 = r6
            r3 = 1
            r6 = 0
            goto L_0x0148
        L_0x0143:
            r3 = 0
            r5 = 0
            r6 = 0
            r9 = 0
            r15 = 0
        L_0x0148:
            if (r15 == 0) goto L_0x01f1
            r33 = r5
            if (r34 == 0) goto L_0x0182
            java.io.File r5 = new java.io.File
            r19 = r8
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r14 = "q_"
            r7.append(r14)
            int r14 = r15.dc_id
            r7.append(r14)
            r7.append(r4)
            long r13 = r15.id
            r7.append(r13)
            java.lang.String r13 = ".jpg"
            r7.append(r13)
            java.lang.String r7 = r7.toString()
            r5.<init>(r8, r7)
            boolean r7 = r5.exists()
            if (r7 != 0) goto L_0x0180
            goto L_0x0184
        L_0x0180:
            r7 = 1
            goto L_0x0186
        L_0x0182:
            r19 = r8
        L_0x0184:
            r5 = 0
            r7 = 0
        L_0x0186:
            boolean r8 = android.text.TextUtils.isEmpty(r6)
            if (r8 != 0) goto L_0x0197
            java.io.File r8 = new java.io.File
            r8.<init>(r6)
            boolean r6 = r8.exists()
            if (r6 != 0) goto L_0x0198
        L_0x0197:
            r8 = 0
        L_0x0198:
            if (r8 != 0) goto L_0x019c
            r8 = r33
        L_0x019c:
            if (r5 != 0) goto L_0x01ed
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15)
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r2 = r0.waitingForQualityThumb
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2
            if (r2 != 0) goto L_0x01c0
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo
            r4 = 0
            r2.<init>()
            org.telegram.tgnet.TLRPC.Document unused = r2.parentDocument = r15
            java.lang.String unused = r2.filter = r12
            boolean unused = r2.big = r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r3 = r0.waitingForQualityThumb
            r3.put(r1, r2)
        L_0x01c0:
            java.util.ArrayList r3 = r2.imageReceiverArray
            boolean r3 = r3.contains(r11)
            if (r3 != 0) goto L_0x01dc
            java.util.ArrayList r3 = r2.imageReceiverArray
            r3.add(r11)
            java.util.ArrayList r3 = r2.imageReceiverGuidsArray
            java.lang.Integer r4 = java.lang.Integer.valueOf(r27)
            r3.add(r4)
        L_0x01dc:
            android.util.SparseArray<java.lang.String> r3 = r0.waitingForQualityThumbByTag
            r3.put(r10, r1)
            boolean r1 = r8.exists()
            if (r1 == 0) goto L_0x01ec
            if (r35 == 0) goto L_0x01ec
            r0.generateThumb(r9, r8, r2)
        L_0x01ec:
            return
        L_0x01ed:
            r15 = r5
            r3 = 1
            r5 = 2
            goto L_0x01fb
        L_0x01f1:
            r19 = r8
            r3 = 1
            goto L_0x01f8
        L_0x01f5:
            r19 = r8
            r3 = 0
        L_0x01f8:
            r5 = 2
            r7 = 0
            r15 = 0
        L_0x01fb:
            if (r1 == r5) goto L_0x0584
            boolean r5 = r30.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r9 = new org.telegram.messenger.ImageLoader$CacheImage
            r6 = 0
            r9.<init>()
            r10 = r30
            if (r31 != 0) goto L_0x0257
            org.telegram.messenger.WebFile r6 = r10.webFile
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r6)
            if (r6 != 0) goto L_0x0254
            org.telegram.tgnet.TLRPC$Document r6 = r10.document
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r6)
            if (r6 != 0) goto L_0x0254
            org.telegram.tgnet.TLRPC$Document r6 = r10.document
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6)
            if (r6 == 0) goto L_0x0224
            goto L_0x0254
        L_0x0224:
            java.lang.String r6 = r10.path
            if (r6 == 0) goto L_0x0257
            java.lang.String r8 = "vthumb"
            boolean r8 = r6.startsWith(r8)
            if (r8 != 0) goto L_0x0257
            java.lang.String r8 = "thumb"
            boolean r8 = r6.startsWith(r8)
            if (r8 != 0) goto L_0x0257
            java.lang.String r8 = "jpg"
            java.lang.String r6 = getHttpUrlExtension(r6, r8)
            java.lang.String r8 = "mp4"
            boolean r8 = r6.equals(r8)
            if (r8 != 0) goto L_0x0250
            java.lang.String r8 = "gif"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x0257
        L_0x0250:
            r6 = 2
            r9.imageType = r6
            goto L_0x0257
        L_0x0254:
            r6 = 2
            r9.imageType = r6
        L_0x0257:
            if (r15 != 0) goto L_0x0425
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r10.photoSize
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            java.lang.String r8 = "g"
            if (r6 == 0) goto L_0x026b
            r6 = r36
            r5 = r7
            r11 = r12
            r1 = 1
            r3 = 0
            r4 = 1
        L_0x0268:
            r7 = 4
            goto L_0x0410
        L_0x026b:
            org.telegram.messenger.SecureDocument r6 = r10.secureDocument
            if (r6 == 0) goto L_0x0290
            r9.secureDocument = r6
            org.telegram.messenger.SecureDocument r3 = r9.secureDocument
            org.telegram.tgnet.TLRPC$TL_secureFile r3 = r3.secureFile
            int r3 = r3.dc_id
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r3 != r4) goto L_0x027d
            r4 = 1
            goto L_0x027e
        L_0x027d:
            r4 = 0
        L_0x027e:
            java.io.File r3 = new java.io.File
            r5 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r3.<init>(r6, r2)
            r6 = r36
            r15 = r3
            r5 = r7
            r11 = r12
            r1 = 1
            r3 = 0
            goto L_0x0268
        L_0x0290:
            boolean r6 = r8.equals(r12)
            java.lang.String r13 = ".svg"
            java.lang.String r15 = "application/x-tgwallpattern"
            java.lang.String r14 = "application/x-tgsticker"
            if (r6 != 0) goto L_0x033a
            r6 = r36
            r11 = r37
            r33 = r3
            if (r6 != 0) goto L_0x02ac
            if (r11 <= 0) goto L_0x02ac
            java.lang.String r3 = r10.path
            if (r3 != 0) goto L_0x02ac
            if (r5 == 0) goto L_0x0340
        L_0x02ac:
            java.io.File r3 = new java.io.File
            r4 = 4
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r5, r2)
            boolean r5 = r3.exists()
            if (r5 == 0) goto L_0x02be
            r4 = 1
            goto L_0x02e2
        L_0x02be:
            r5 = 2
            if (r6 != r5) goto L_0x02de
            java.io.File r3 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r4)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r34 = r7
            java.lang.String r7 = ".enc"
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            r3.<init>(r5, r4)
            goto L_0x02e0
        L_0x02de:
            r34 = r7
        L_0x02e0:
            r4 = r34
        L_0x02e2:
            org.telegram.tgnet.TLRPC$Document r5 = r10.document
            if (r5 == 0) goto L_0x0330
            boolean r7 = r5 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r7 == 0) goto L_0x0303
            org.telegram.messenger.DocumentObject$ThemeDocument r5 = (org.telegram.messenger.DocumentObject.ThemeDocument) r5
            org.telegram.tgnet.TLRPC$Document r5 = r5.wallpaper
            if (r5 != 0) goto L_0x02f2
            r5 = 1
            goto L_0x02f4
        L_0x02f2:
            r5 = r33
        L_0x02f4:
            r7 = 5
            r9.imageType = r7
            r15 = r3
            r11 = r12
            r1 = 1
            r3 = 0
            r7 = 4
            r20 = r5
            r5 = r4
            r4 = r20
            goto L_0x0410
        L_0x0303:
            java.lang.String r5 = r5.mime_type
            boolean r5 = r14.equals(r5)
            if (r5 == 0) goto L_0x030f
            r5 = 1
            r9.imageType = r5
            goto L_0x0330
        L_0x030f:
            org.telegram.tgnet.TLRPC$Document r5 = r10.document
            java.lang.String r5 = r5.mime_type
            boolean r5 = r15.equals(r5)
            if (r5 == 0) goto L_0x031d
            r5 = 3
            r9.imageType = r5
            goto L_0x0330
        L_0x031d:
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r5 == 0) goto L_0x0330
            org.telegram.tgnet.TLRPC$Document r5 = r10.document
            java.lang.String r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r5)
            boolean r5 = r5.endsWith(r13)
            if (r5 == 0) goto L_0x0330
            r5 = 4
            r9.imageType = r5
        L_0x0330:
            r15 = r3
            r5 = r4
            r11 = r12
            r1 = 1
            r3 = 0
            r7 = 4
            r4 = r33
            goto L_0x0410
        L_0x033a:
            r6 = r36
            r11 = r37
            r33 = r3
        L_0x0340:
            r34 = r7
            org.telegram.tgnet.TLRPC$Document r3 = r10.document
            if (r3 == 0) goto L_0x03ee
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted
            if (r5 == 0) goto L_0x0355
            java.io.File r5 = new java.io.File
            r7 = 4
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r7)
            r5.<init>(r1, r2)
            goto L_0x0370
        L_0x0355:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r3)
            if (r1 == 0) goto L_0x0366
            java.io.File r5 = new java.io.File
            r1 = 2
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r5.<init>(r7, r2)
            goto L_0x0370
        L_0x0366:
            java.io.File r5 = new java.io.File
            r1 = 3
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r5.<init>(r7, r2)
        L_0x0370:
            boolean r1 = r8.equals(r12)
            if (r1 == 0) goto L_0x03a1
            boolean r1 = r5.exists()
            if (r1 != 0) goto L_0x03a1
            java.io.File r5 = new java.io.File
            r1 = 4
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r11 = r3.dc_id
            r1.append(r11)
            r1.append(r4)
            long r11 = r3.id
            r1.append(r11)
            java.lang.String r4 = ".temp"
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            r5.<init>(r7, r1)
        L_0x03a1:
            boolean r1 = r3 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r1 == 0) goto L_0x03b7
            r1 = r3
            org.telegram.messenger.DocumentObject$ThemeDocument r1 = (org.telegram.messenger.DocumentObject.ThemeDocument) r1
            org.telegram.tgnet.TLRPC$Document r1 = r1.wallpaper
            if (r1 != 0) goto L_0x03ae
            r1 = 1
            goto L_0x03b0
        L_0x03ae:
            r1 = r33
        L_0x03b0:
            r4 = 5
            r9.imageType = r4
            r4 = r1
            r1 = 1
            r7 = 4
            goto L_0x03e6
        L_0x03b7:
            java.lang.String r1 = r3.mime_type
            boolean r1 = r14.equals(r1)
            if (r1 == 0) goto L_0x03c4
            r1 = 1
            r9.imageType = r1
        L_0x03c2:
            r7 = 4
            goto L_0x03e4
        L_0x03c4:
            r1 = 1
            java.lang.String r4 = r3.mime_type
            boolean r4 = r15.equals(r4)
            if (r4 == 0) goto L_0x03d1
            r4 = 3
            r9.imageType = r4
            goto L_0x03c2
        L_0x03d1:
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r4 == 0) goto L_0x03c2
            org.telegram.tgnet.TLRPC$Document r4 = r10.document
            java.lang.String r4 = org.telegram.messenger.FileLoader.getDocumentFileName(r4)
            boolean r4 = r4.endsWith(r13)
            if (r4 == 0) goto L_0x03c2
            r7 = 4
            r9.imageType = r7
        L_0x03e4:
            r4 = r33
        L_0x03e6:
            int r3 = r3.size
            r11 = r28
            r15 = r5
            r5 = r34
            goto L_0x0410
        L_0x03ee:
            r1 = 1
            r7 = 4
            org.telegram.messenger.WebFile r3 = r10.webFile
            if (r3 == 0) goto L_0x03ff
            java.io.File r3 = new java.io.File
            r4 = 3
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r4, r2)
            goto L_0x0408
        L_0x03ff:
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r3.<init>(r4, r2)
        L_0x0408:
            r11 = r28
            r4 = r33
            r5 = r34
            r15 = r3
            r3 = 0
        L_0x0410:
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x0420
            r8 = 2
            r9.imageType = r8
            r9.size = r3
            r8 = r29
            r13 = r5
            r12 = 1
            goto L_0x0434
        L_0x0420:
            r8 = r29
            r12 = r4
            r13 = r5
            goto L_0x0434
        L_0x0425:
            r6 = r36
            r33 = r3
            r34 = r7
            r11 = r12
            r1 = 1
            r7 = 4
            r8 = r29
            r12 = r33
            r13 = r34
        L_0x0434:
            r9.type = r8
            r14 = r24
            r9.key = r14
            r9.filter = r11
            r9.imageLocation = r10
            r5 = r38
            r9.ext = r5
            r4 = r39
            r9.currentAccount = r4
            r3 = r32
            r9.parentObject = r3
            int r1 = r10.imageType
            if (r1 == 0) goto L_0x0450
            r9.imageType = r1
        L_0x0450:
            r1 = 2
            if (r6 != r1) goto L_0x046f
            java.io.File r1 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            java.lang.String r4 = ".enc.key"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.<init>(r7, r3)
            r9.encryptionKeyPath = r1
        L_0x046f:
            r1 = r32
            r3 = r9
            r4 = r26
            r5 = r24
            r7 = r6
            r6 = r28
            r11 = r7
            r17 = 4
            r7 = r29
            r1 = r19
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r12 != 0) goto L_0x0563
            if (r13 != 0) goto L_0x0563
            boolean r3 = r15.exists()
            if (r3 == 0) goto L_0x0491
            goto L_0x0563
        L_0x0491:
            r9.url = r2
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            r3.put(r2, r9)
            java.lang.String r2 = r10.path
            if (r2 == 0) goto L_0x04f0
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r17)
            java.io.File r4 = new java.io.File
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r2 = "_temp.jpg"
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            r4.<init>(r3, r2)
            r9.tempFilePath = r4
            r9.finalFilePath = r15
            java.lang.String r2 = r10.path
            boolean r1 = r2.startsWith(r1)
            if (r1 == 0) goto L_0x04da
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r1 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r1.<init>(r9)
            r9.artworkTask = r1
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r1 = r0.artworkTasks
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r2 = r9.artworkTask
            r1.add(r2)
            r7 = 0
            r0.runArtworkTasks(r7)
            goto L_0x0584
        L_0x04da:
            r7 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r1 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r2 = r37
            r1.<init>(r9, r2)
            r9.httpTask = r1
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r1 = r0.httpTasks
            org.telegram.messenger.ImageLoader$HttpImageTask r2 = r9.httpTask
            r1.add(r2)
            r0.runHttpTasks(r7)
            goto L_0x0584
        L_0x04f0:
            r2 = r37
            r7 = 0
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r10.location
            if (r1 == 0) goto L_0x0517
            if (r11 != 0) goto L_0x0501
            if (r2 <= 0) goto L_0x04ff
            byte[] r1 = r10.key
            if (r1 == 0) goto L_0x0501
        L_0x04ff:
            r6 = 1
            goto L_0x0502
        L_0x0501:
            r6 = r11
        L_0x0502:
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            r3 = r32
            if (r22 == 0) goto L_0x050c
            r5 = 2
            goto L_0x050d
        L_0x050c:
            r5 = 1
        L_0x050d:
            r2 = r30
            r3 = r32
            r4 = r38
            r1.loadFile(r2, r3, r4, r5, r6)
            goto L_0x0551
        L_0x0517:
            r3 = r32
            org.telegram.tgnet.TLRPC$Document r1 = r10.document
            if (r1 == 0) goto L_0x052c
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.tgnet.TLRPC$Document r2 = r10.document
            if (r22 == 0) goto L_0x0527
            r4 = 2
            goto L_0x0528
        L_0x0527:
            r4 = 1
        L_0x0528:
            r1.loadFile(r2, r3, r4, r11)
            goto L_0x0551
        L_0x052c:
            org.telegram.messenger.SecureDocument r1 = r10.secureDocument
            if (r1 == 0) goto L_0x053f
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.messenger.SecureDocument r2 = r10.secureDocument
            if (r22 == 0) goto L_0x053a
            r3 = 2
            goto L_0x053b
        L_0x053a:
            r3 = 1
        L_0x053b:
            r1.loadFile(r2, r3)
            goto L_0x0551
        L_0x053f:
            org.telegram.messenger.WebFile r1 = r10.webFile
            if (r1 == 0) goto L_0x0551
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.messenger.WebFile r2 = r10.webFile
            if (r22 == 0) goto L_0x054d
            r3 = 2
            goto L_0x054e
        L_0x054d:
            r3 = 1
        L_0x054e:
            r1.loadFile(r2, r3, r11)
        L_0x0551:
            boolean r1 = r26.isForceLoding()
            if (r1 == 0) goto L_0x0584
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = r0.forceLoadingImages
            java.lang.String r2 = r9.key
            java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
            r1.put(r2, r3)
            goto L_0x0584
        L_0x0563:
            r9.finalFilePath = r15
            r9.imageLocation = r10
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r1.<init>(r9)
            r9.cacheTask = r1
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r1 = r0.imageLoadingByKeys
            r1.put(r14, r9)
            if (r22 == 0) goto L_0x057d
            org.telegram.messenger.DispatchQueue r1 = r0.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r2 = r9.cacheTask
            r1.postRunnable(r2)
            goto L_0x0584
        L_0x057d:
            org.telegram.messenger.DispatchQueue r1 = r0.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r2 = r9.cacheTask
            r1.postRunnable(r2)
        L_0x0584:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:138:0x0280  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x02a0  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02d0  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02d2  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x02d7  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02da  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02f4  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x03f9  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r31) {
        /*
            r30 = this;
            r12 = r30
            r13 = r31
            if (r13 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.String r6 = r31.getMediaKey()
            int r14 = r31.getNewGuid()
            r7 = 0
            r8 = 1
            if (r6 == 0) goto L_0x0055
            org.telegram.messenger.ImageLocation r0 = r31.getMediaLocation()
            if (r0 == 0) goto L_0x002e
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r8)
            if (r1 != 0) goto L_0x0025
            int r0 = r0.imageType
            if (r0 != r8) goto L_0x002e
        L_0x0025:
            org.telegram.messenger.LruCache<org.telegram.ui.Components.RLottieDrawable> r0 = r12.lottieMemCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            goto L_0x003d
        L_0x002e:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.memCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x003d
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.memCache
            r1.moveToFront(r6)
        L_0x003d:
            r1 = r0
            if (r1 == 0) goto L_0x0055
            r12.cancelLoadingForImageReceiver(r13, r8)
            r3 = 3
            r4 = 1
            r0 = r31
            r2 = r6
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r31.isForcePreview()
            if (r0 != 0) goto L_0x0053
            return
        L_0x0053:
            r0 = 1
            goto L_0x0056
        L_0x0055:
            r0 = 0
        L_0x0056:
            java.lang.String r2 = r31.getImageKey()
            if (r0 != 0) goto L_0x00a0
            if (r2 == 0) goto L_0x00a0
            org.telegram.messenger.ImageLocation r1 = r31.getImageLocation()
            if (r1 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            boolean r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r8)
            if (r3 != 0) goto L_0x0070
            int r1 = r1.imageType
            if (r1 != r8) goto L_0x0079
        L_0x0070:
            org.telegram.messenger.LruCache<org.telegram.ui.Components.RLottieDrawable> r1 = r12.lottieMemCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            goto L_0x0088
        L_0x0079:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.memCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x0088
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r12.memCache
            r3.moveToFront(r2)
        L_0x0088:
            if (r1 == 0) goto L_0x00a0
            r12.cancelLoadingForImageReceiver(r13, r8)
            r3 = 0
            r4 = 1
            r0 = r31
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r31.isForcePreview()
            if (r0 != 0) goto L_0x009e
            if (r6 != 0) goto L_0x009e
            return
        L_0x009e:
            r15 = 1
            goto L_0x00a1
        L_0x00a0:
            r15 = r0
        L_0x00a1:
            java.lang.String r2 = r31.getThumbKey()
            if (r2 == 0) goto L_0x00ea
            org.telegram.messenger.ImageLocation r0 = r31.getThumbLocation()
            if (r0 == 0) goto L_0x00c2
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r8)
            if (r1 != 0) goto L_0x00b9
            int r0 = r0.imageType
            if (r0 != r8) goto L_0x00c2
        L_0x00b9:
            org.telegram.messenger.LruCache<org.telegram.ui.Components.RLottieDrawable> r0 = r12.lottieMemCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            goto L_0x00d1
        L_0x00c2:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.memCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x00d1
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.memCache
            r1.moveToFront(r2)
        L_0x00d1:
            r1 = r0
            if (r1 == 0) goto L_0x00ea
            r3 = 1
            r4 = 1
            r0 = r31
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r12.cancelLoadingForImageReceiver(r13, r7)
            if (r15 == 0) goto L_0x00e8
            boolean r0 = r31.isForcePreview()
            if (r0 == 0) goto L_0x00e8
            return
        L_0x00e8:
            r0 = 1
            goto L_0x00eb
        L_0x00ea:
            r0 = 0
        L_0x00eb:
            java.lang.Object r1 = r31.getParentObject()
            org.telegram.tgnet.TLRPC$Document r2 = r31.getQulityThumbDocument()
            org.telegram.messenger.ImageLocation r5 = r31.getThumbLocation()
            java.lang.String r6 = r31.getThumbFilter()
            org.telegram.messenger.ImageLocation r3 = r31.getMediaLocation()
            java.lang.String r11 = r31.getMediaFilter()
            org.telegram.messenger.ImageLocation r4 = r31.getImageLocation()
            java.lang.String r10 = r31.getImageFilter()
            if (r4 != 0) goto L_0x0131
            boolean r9 = r31.isNeedsQualityThumb()
            if (r9 == 0) goto L_0x0131
            boolean r9 = r31.isCurrentKeyQuality()
            if (r9 == 0) goto L_0x0131
            boolean r9 = r1 instanceof org.telegram.messenger.MessageObject
            if (r9 == 0) goto L_0x012a
            r2 = r1
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Document r2 = r2.getDocument()
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r2)
        L_0x0128:
            r2 = 1
            goto L_0x0132
        L_0x012a:
            if (r2 == 0) goto L_0x0131
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            goto L_0x0128
        L_0x0131:
            r2 = 0
        L_0x0132:
            java.lang.String r9 = r31.getExt()
            java.lang.String r8 = "jpg"
            if (r9 != 0) goto L_0x013b
            r9 = r8
        L_0x013b:
            r17 = 0
            r18 = r3
            r19 = r17
            r20 = r19
            r22 = r20
            r23 = r22
            r3 = 0
            r21 = 0
        L_0x014a:
            r7 = 2
            java.lang.String r12 = "."
            if (r3 >= r7) goto L_0x0306
            if (r3 != 0) goto L_0x0153
            r7 = r4
            goto L_0x0155
        L_0x0153:
            r7 = r18
        L_0x0155:
            if (r7 != 0) goto L_0x015a
            r24 = r15
            goto L_0x016b
        L_0x015a:
            if (r18 == 0) goto L_0x0161
            r24 = r15
            r13 = r18
            goto L_0x0164
        L_0x0161:
            r13 = r4
            r24 = r15
        L_0x0164:
            r15 = 0
            java.lang.String r13 = r7.getKey(r1, r13, r15)
            if (r13 != 0) goto L_0x0173
        L_0x016b:
            r26 = r0
            r27 = r2
            r25 = r14
            goto L_0x02f6
        L_0x0173:
            if (r18 == 0) goto L_0x017a
            r25 = r14
            r15 = r18
            goto L_0x017d
        L_0x017a:
            r15 = r4
            r25 = r14
        L_0x017d:
            r14 = 1
            java.lang.String r15 = r7.getKey(r1, r15, r14)
            java.lang.String r14 = r7.path
            if (r14 == 0) goto L_0x01a4
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r15)
            r14.append(r12)
            java.lang.String r12 = r7.path
            java.lang.String r12 = getHttpUrlExtension(r12, r8)
            r14.append(r12)
            java.lang.String r15 = r14.toString()
        L_0x019e:
            r26 = r0
        L_0x01a0:
            r27 = r2
            goto L_0x02d5
        L_0x01a4:
            org.telegram.tgnet.TLRPC$PhotoSize r14 = r7.photoSize
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r14 == 0) goto L_0x01bd
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r15)
            r14.append(r12)
            r14.append(r9)
            java.lang.String r15 = r14.toString()
            goto L_0x019e
        L_0x01bd:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r14 = r7.location
            if (r14 == 0) goto L_0x0200
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r15)
            r14.append(r12)
            r14.append(r9)
            java.lang.String r15 = r14.toString()
            java.lang.String r12 = r31.getExt()
            if (r12 != 0) goto L_0x01f4
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r12 = r7.location
            byte[] r14 = r12.key
            if (r14 != 0) goto L_0x01f4
            r26 = r15
            long r14 = r12.volume_id
            r27 = -2147483648(0xfffffffvar_, double:NaN)
            int r29 = (r14 > r27 ? 1 : (r14 == r27 ? 0 : -1))
            if (r29 != 0) goto L_0x01ef
            int r12 = r12.local_id
            if (r12 >= 0) goto L_0x01ef
            goto L_0x01f6
        L_0x01ef:
            r27 = r2
            r15 = r26
            goto L_0x01fc
        L_0x01f4:
            r26 = r15
        L_0x01f6:
            r27 = r2
            r15 = r26
            r21 = 1
        L_0x01fc:
            r26 = r0
            goto L_0x02d5
        L_0x0200:
            org.telegram.messenger.WebFile r14 = r7.webFile
            if (r14 == 0) goto L_0x0228
            java.lang.String r14 = r14.mime_type
            java.lang.String r14 = org.telegram.messenger.FileLoader.getMimeTypePart(r14)
            r26 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            r0.append(r12)
            org.telegram.messenger.WebFile r12 = r7.webFile
            java.lang.String r12 = r12.url
            java.lang.String r12 = getHttpUrlExtension(r12, r14)
            r0.append(r12)
            java.lang.String r15 = r0.toString()
            goto L_0x01a0
        L_0x0228:
            r26 = r0
            org.telegram.messenger.SecureDocument r0 = r7.secureDocument
            if (r0 == 0) goto L_0x0242
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            r0.append(r12)
            r0.append(r9)
            java.lang.String r15 = r0.toString()
            goto L_0x01a0
        L_0x0242:
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            if (r0 == 0) goto L_0x01a0
            if (r3 != 0) goto L_0x025b
            if (r2 == 0) goto L_0x025b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = "q_"
            r0.append(r12)
            r0.append(r13)
            java.lang.String r13 = r0.toString()
        L_0x025b:
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            java.lang.String r12 = ""
            if (r0 == 0) goto L_0x0276
            r14 = 46
            int r14 = r0.lastIndexOf(r14)
            r27 = r2
            r2 = -1
            if (r14 != r2) goto L_0x0271
            goto L_0x0278
        L_0x0271:
            java.lang.String r0 = r0.substring(r14)
            goto L_0x0279
        L_0x0276:
            r27 = r2
        L_0x0278:
            r0 = r12
        L_0x0279:
            int r2 = r0.length()
            r14 = 1
            if (r2 > r14) goto L_0x02a0
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            java.lang.String r0 = r0.mime_type
            java.lang.String r2 = "video/mp4"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0290
            java.lang.String r12 = ".mp4"
            goto L_0x02a1
        L_0x0290:
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            java.lang.String r0 = r0.mime_type
            java.lang.String r2 = "video/x-matroska"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x02a1
            java.lang.String r12 = ".mkv"
            goto L_0x02a1
        L_0x02a0:
            r12 = r0
        L_0x02a1:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            r0.append(r12)
            java.lang.String r15 = r0.toString()
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r0)
            if (r0 != 0) goto L_0x02d2
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            boolean r0 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r0)
            if (r0 != 0) goto L_0x02d2
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r0)
            if (r0 != 0) goto L_0x02d2
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            boolean r0 = org.telegram.messenger.MessageObject.canPreviewDocument(r0)
            if (r0 != 0) goto L_0x02d2
            r0 = 1
            goto L_0x02d3
        L_0x02d2:
            r0 = 0
        L_0x02d3:
            r21 = r0
        L_0x02d5:
            if (r3 != 0) goto L_0x02da
            r20 = r13
            goto L_0x02e0
        L_0x02da:
            r19 = r13
            r23 = r15
            r15 = r22
        L_0x02e0:
            if (r7 != r5) goto L_0x02f4
            if (r3 != 0) goto L_0x02eb
            r4 = r17
            r20 = r4
            r22 = r20
            goto L_0x02f6
        L_0x02eb:
            r22 = r15
            r18 = r17
            r19 = r18
            r23 = r19
            goto L_0x02f6
        L_0x02f4:
            r22 = r15
        L_0x02f6:
            int r3 = r3 + 1
            r12 = r30
            r13 = r31
            r15 = r24
            r14 = r25
            r0 = r26
            r2 = r27
            goto L_0x014a
        L_0x0306:
            r26 = r0
            r25 = r14
            r24 = r15
            if (r5 == 0) goto L_0x0374
            org.telegram.messenger.ImageLocation r0 = r31.getStrippedLocation()
            if (r0 != 0) goto L_0x031a
            if (r18 == 0) goto L_0x0319
            r0 = r18
            goto L_0x031a
        L_0x0319:
            r0 = r4
        L_0x031a:
            r2 = 0
            java.lang.String r17 = r5.getKey(r1, r0, r2)
            r2 = 1
            java.lang.String r0 = r5.getKey(r1, r0, r2)
            java.lang.String r1 = r5.path
            if (r1 == 0) goto L_0x0344
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r12)
            java.lang.String r0 = r5.path
            java.lang.String r0 = getHttpUrlExtension(r0, r8)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x0340:
            r3 = r0
            r0 = r17
            goto L_0x0378
        L_0x0344:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r5.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r1 == 0) goto L_0x035d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r12)
            r1.append(r9)
            java.lang.String r0 = r1.toString()
            goto L_0x0340
        L_0x035d:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r5.location
            if (r1 == 0) goto L_0x0340
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r12)
            r1.append(r9)
            java.lang.String r0 = r1.toString()
            goto L_0x0340
        L_0x0374:
            r2 = 1
            r0 = r17
            r3 = r0
        L_0x0378:
            java.lang.String r1 = "@"
            r8 = r19
            if (r8 == 0) goto L_0x0392
            if (r11 == 0) goto L_0x0392
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r8)
            r12.append(r1)
            r12.append(r11)
            java.lang.String r8 = r12.toString()
        L_0x0392:
            r12 = r8
            r8 = r20
            if (r8 == 0) goto L_0x03ab
            if (r10 == 0) goto L_0x03ab
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r8)
            r13.append(r1)
            r13.append(r10)
            java.lang.String r8 = r13.toString()
        L_0x03ab:
            r13 = r8
            if (r0 == 0) goto L_0x03c2
            if (r6 == 0) goto L_0x03c2
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r0)
            r8.append(r1)
            r8.append(r6)
            java.lang.String r0 = r8.toString()
        L_0x03c2:
            r8 = r0
            if (r4 == 0) goto L_0x03f9
            java.lang.String r0 = r4.path
            if (r0 == 0) goto L_0x03f9
            r11 = 0
            r12 = 1
            r14 = 1
            if (r26 == 0) goto L_0x03d0
            r15 = 2
            goto L_0x03d1
        L_0x03d0:
            r15 = 1
        L_0x03d1:
            r0 = r30
            r1 = r31
            r2 = r8
            r16 = r4
            r4 = r9
            r7 = r11
            r8 = r12
            r17 = r9
            r9 = r14
            r14 = r10
            r10 = r15
            r11 = r25
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r7 = r31.getSize()
            r8 = 1
            r9 = 0
            r10 = 0
            r2 = r13
            r3 = r22
            r4 = r17
            r5 = r16
            r6 = r14
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x04a0
        L_0x03f9:
            r16 = r4
            r17 = r9
            r14 = r10
            if (r18 == 0) goto L_0x0469
            int r0 = r31.getCacheType()
            r15 = 1
            if (r0 != 0) goto L_0x040c
            if (r21 == 0) goto L_0x040c
            r19 = 1
            goto L_0x040e
        L_0x040c:
            r19 = r0
        L_0x040e:
            if (r19 != 0) goto L_0x0412
            r9 = 1
            goto L_0x0414
        L_0x0412:
            r9 = r19
        L_0x0414:
            if (r26 != 0) goto L_0x0435
            r10 = 0
            r20 = 1
            if (r26 == 0) goto L_0x041e
            r21 = 2
            goto L_0x0420
        L_0x041e:
            r21 = 1
        L_0x0420:
            r0 = r30
            r1 = r31
            r2 = r8
            r4 = r17
            r7 = r10
            r8 = r9
            r9 = r20
            r10 = r21
            r20 = r11
            r11 = r25
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x0437
        L_0x0435:
            r20 = r11
        L_0x0437:
            if (r24 != 0) goto L_0x044e
            r7 = 0
            r9 = 0
            r10 = 0
            r0 = r30
            r1 = r31
            r2 = r13
            r3 = r22
            r4 = r17
            r5 = r16
            r6 = r14
            r8 = r15
            r11 = r25
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x044e:
            int r7 = r31.getSize()
            r9 = 3
            r10 = 0
            r0 = r30
            r1 = r31
            r2 = r12
            r3 = r23
            r4 = r17
            r5 = r18
            r6 = r20
            r8 = r19
            r11 = r25
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x04a0
        L_0x0469:
            int r0 = r31.getCacheType()
            if (r0 != 0) goto L_0x0473
            if (r21 == 0) goto L_0x0473
            r12 = 1
            goto L_0x0474
        L_0x0473:
            r12 = r0
        L_0x0474:
            if (r12 != 0) goto L_0x0478
            r9 = 1
            goto L_0x0479
        L_0x0478:
            r9 = r12
        L_0x0479:
            r10 = 0
            r11 = 1
            if (r26 == 0) goto L_0x047f
            r15 = 2
            goto L_0x0480
        L_0x047f:
            r15 = 1
        L_0x0480:
            r0 = r30
            r1 = r31
            r2 = r8
            r4 = r17
            r7 = r10
            r8 = r9
            r9 = r11
            r10 = r15
            r11 = r25
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r7 = r31.getSize()
            r9 = 0
            r10 = 0
            r2 = r13
            r3 = r22
            r5 = r16
            r6 = r14
            r8 = r12
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x04a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public void httpFileLoadError(String str) {
        this.imageLoadQueue.postRunnable(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ImageLoader.this.lambda$httpFileLoadError$6$ImageLoader(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$httpFileLoadError$6$ImageLoader(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            HttpImageTask httpImageTask = cacheImage.httpTask;
            cacheImage.httpTask = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
            this.httpTasks.add(cacheImage.httpTask);
            runHttpTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void artworkLoadError(String str) {
        this.imageLoadQueue.postRunnable(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ImageLoader.this.lambda$artworkLoadError$7$ImageLoader(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$artworkLoadError$7$ImageLoader(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            cacheImage.artworkTask = new ArtworkLoadTask(cacheImage.artworkTask.cacheImage);
            this.artworkTasks.add(cacheImage.artworkTask);
            runArtworkTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void fileDidLoaded(String str, File file, int i) {
        this.imageLoadQueue.postRunnable(new Runnable(str, i, file) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ File f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ImageLoader.this.lambda$fileDidLoaded$8$ImageLoader(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$fileDidLoaded$8$ImageLoader(String str, int i, File file) {
        ThumbGenerateInfo thumbGenerateInfo = this.waitingForQualityThumb.get(str);
        if (!(thumbGenerateInfo == null || thumbGenerateInfo.parentDocument == null)) {
            generateThumb(i, file, thumbGenerateInfo);
            this.waitingForQualityThumb.remove(str);
        }
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            this.imageLoadingByUrl.remove(str);
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < cacheImage.imageReceiverArray.size(); i2++) {
                String str2 = cacheImage.keys.get(i2);
                String str3 = cacheImage.filters.get(i2);
                int intValue = cacheImage.types.get(i2).intValue();
                ImageReceiver imageReceiver = cacheImage.imageReceiverArray.get(i2);
                int intValue2 = cacheImage.imageReceiverGuidsArray.get(i2).intValue();
                CacheImage cacheImage2 = this.imageLoadingByKeys.get(str2);
                if (cacheImage2 == null) {
                    cacheImage2 = new CacheImage();
                    cacheImage2.secureDocument = cacheImage.secureDocument;
                    cacheImage2.currentAccount = cacheImage.currentAccount;
                    cacheImage2.finalFilePath = file;
                    cacheImage2.key = str2;
                    cacheImage2.imageLocation = cacheImage.imageLocation;
                    cacheImage2.type = intValue;
                    cacheImage2.ext = cacheImage.ext;
                    cacheImage2.encryptionKeyPath = cacheImage.encryptionKeyPath;
                    cacheImage2.cacheTask = new CacheOutTask(cacheImage2);
                    cacheImage2.filter = str3;
                    cacheImage2.imageType = cacheImage.imageType;
                    this.imageLoadingByKeys.put(str2, cacheImage2);
                    arrayList.add(cacheImage2.cacheTask);
                }
                cacheImage2.addImageReceiver(imageReceiver, str2, str3, intValue, intValue2);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                CacheOutTask cacheOutTask = (CacheOutTask) arrayList.get(i3);
                if (cacheOutTask.cacheImage.type == 1) {
                    this.cacheThumbOutQueue.postRunnable(cacheOutTask);
                } else {
                    this.cacheOutQueue.postRunnable(cacheOutTask);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fileDidFailedLoad(String str, int i) {
        if (i != 1) {
            this.imageLoadQueue.postRunnable(new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.this.lambda$fileDidFailedLoad$9$ImageLoader(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$fileDidFailedLoad$9$ImageLoader(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            cacheImage.setImageAndClear((Drawable) null, (String) null);
        }
    }

    /* access modifiers changed from: private */
    public void runHttpTasks(boolean z) {
        if (z) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            HttpImageTask poll = this.httpTasks.poll();
            if (poll != null) {
                poll.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentHttpTasksCount++;
            }
        }
    }

    /* access modifiers changed from: private */
    public void runArtworkTasks(boolean z) {
        if (z) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            try {
                this.artworkTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentArtworkTasksCount++;
            } catch (Throwable unused) {
                runArtworkTasks(false);
            }
        }
    }

    public boolean isLoadingHttpFile(String str) {
        return this.httpFileLoadTasksByKeys.containsKey(str);
    }

    public static String getHttpFileName(String str) {
        return Utilities.MD5(str);
    }

    public static File getHttpFilePath(String str, String str2) {
        String httpUrlExtension = getHttpUrlExtension(str, str2);
        File directory = FileLoader.getDirectory(4);
        return new File(directory, Utilities.MD5(str) + "." + httpUrlExtension);
    }

    public void loadHttpFile(String str, String str2, int i) {
        if (str != null && str.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(str)) {
            String httpUrlExtension = getHttpUrlExtension(str, str2);
            File directory = FileLoader.getDirectory(4);
            File file = new File(directory, Utilities.MD5(str) + "_temp." + httpUrlExtension);
            file.delete();
            HttpFileTask httpFileTask = new HttpFileTask(str, file, httpUrlExtension, i);
            this.httpFileLoadTasks.add(httpFileTask);
            this.httpFileLoadTasksByKeys.put(str, httpFileTask);
            runHttpFileLoadTasks((HttpFileTask) null, 0);
        }
    }

    public void cancelLoadHttpFile(String str) {
        HttpFileTask httpFileTask = this.httpFileLoadTasksByKeys.get(str);
        if (httpFileTask != null) {
            httpFileTask.cancel(true);
            this.httpFileLoadTasksByKeys.remove(str);
            this.httpFileLoadTasks.remove(httpFileTask);
        }
        Runnable runnable = this.retryHttpsTasks.get(str);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks((HttpFileTask) null, 0);
    }

    /* access modifiers changed from: private */
    public void runHttpFileLoadTasks(HttpFileTask httpFileTask, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(httpFileTask, i) {
            private final /* synthetic */ ImageLoader.HttpFileTask f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ImageLoader.this.lambda$runHttpFileLoadTasks$11$ImageLoader(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$runHttpFileLoadTasks$11$ImageLoader(HttpFileTask httpFileTask, int i) {
        if (httpFileTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (httpFileTask != null) {
            if (i == 1) {
                if (httpFileTask.canRetry) {
                    $$Lambda$ImageLoader$NIWBUvKKa0U_8_0TQJUIDKfrzig r3 = new Runnable(new HttpFileTask(httpFileTask.url, httpFileTask.tempFile, httpFileTask.ext, httpFileTask.currentAccount)) {
                        private final /* synthetic */ ImageLoader.HttpFileTask f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ImageLoader.this.lambda$null$10$ImageLoader(this.f$1);
                        }
                    };
                    this.retryHttpsTasks.put(httpFileTask.url, r3);
                    AndroidUtilities.runOnUIThread(r3, 1000);
                } else {
                    this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                    NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, httpFileTask.url, 0);
                }
            } else if (i == 2) {
                this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(httpFileTask.url) + "." + httpFileTask.ext);
                if (!httpFileTask.tempFile.renameTo(file)) {
                    file = httpFileTask.tempFile;
                }
                NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoad, httpFileTask.url, file.toString());
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            this.httpFileLoadTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpFileLoadTasksCount++;
        }
    }

    public /* synthetic */ void lambda$null$10$ImageLoader(HttpFileTask httpFileTask) {
        this.httpFileLoadTasks.add(httpFileTask);
        runHttpFileLoadTasks((HttpFileTask) null, 0);
    }

    public static boolean shouldSendImageAsDocument(String str, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (!(str != null || uri == null || uri.getScheme() == null)) {
            if (uri.getScheme().contains("file")) {
                str = uri.getPath();
            } else {
                try {
                    str = AndroidUtilities.getPath(uri);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
        if (str != null) {
            BitmapFactory.decodeFile(str, options);
        } else if (uri != null) {
            try {
                InputStream openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(openInputStream, (Rect) null, options);
                openInputStream.close();
            } catch (Throwable th2) {
                FileLog.e(th2);
                return false;
            }
        }
        float f = (float) options.outWidth;
        float f2 = (float) options.outHeight;
        if (f / f2 > 10.0f || f2 / f > 10.0f) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x009f A[SYNTHETIC, Splitter:B:46:0x009f] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d1 A[SYNTHETIC, Splitter:B:63:0x00d1] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x010a A[SYNTHETIC, Splitter:B:80:0x010a] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x012f A[Catch:{ all -> 0x0118 }] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x013a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r11, android.net.Uri r12, float r13, float r14, boolean r15) {
        /*
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
            r0.<init>()
            r1 = 1
            r0.inJustDecodeBounds = r1
            if (r11 != 0) goto L_0x002c
            if (r12 == 0) goto L_0x002c
            java.lang.String r2 = r12.getScheme()
            if (r2 == 0) goto L_0x002c
            java.lang.String r2 = r12.getScheme()
            java.lang.String r3 = "file"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x0023
            java.lang.String r11 = r12.getPath()
            goto L_0x002c
        L_0x0023:
            java.lang.String r11 = org.telegram.messenger.AndroidUtilities.getPath(r12)     // Catch:{ all -> 0x0028 }
            goto L_0x002c
        L_0x0028:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x002c:
            r2 = 0
            if (r11 == 0) goto L_0x0033
            android.graphics.BitmapFactory.decodeFile(r11, r0)
            goto L_0x0055
        L_0x0033:
            if (r12 == 0) goto L_0x0055
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0050 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0050 }
            java.io.InputStream r3 = r3.openInputStream(r12)     // Catch:{ all -> 0x0050 }
            android.graphics.BitmapFactory.decodeStream(r3, r2, r0)     // Catch:{ all -> 0x0050 }
            r3.close()     // Catch:{ all -> 0x0050 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0050 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0050 }
            java.io.InputStream r3 = r3.openInputStream(r12)     // Catch:{ all -> 0x0050 }
            goto L_0x0056
        L_0x0050:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            return r2
        L_0x0055:
            r3 = r2
        L_0x0056:
            int r4 = r0.outWidth
            float r4 = (float) r4
            int r5 = r0.outHeight
            float r5 = (float) r5
            float r4 = r4 / r13
            float r5 = r5 / r14
            if (r15 == 0) goto L_0x0065
            float r13 = java.lang.Math.max(r4, r5)
            goto L_0x0069
        L_0x0065:
            float r13 = java.lang.Math.min(r4, r5)
        L_0x0069:
            r14 = 1065353216(0x3var_, float:1.0)
            int r15 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r15 >= 0) goto L_0x0071
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0071:
            r14 = 0
            r0.inJustDecodeBounds = r14
            int r13 = (int) r13
            r0.inSampleSize = r13
            int r13 = r0.inSampleSize
            int r13 = r13 % 2
            if (r13 == 0) goto L_0x0088
            r13 = 1
        L_0x007e:
            int r15 = r13 * 2
            int r4 = r0.inSampleSize
            if (r15 >= r4) goto L_0x0086
            r13 = r15
            goto L_0x007e
        L_0x0086:
            r0.inSampleSize = r13
        L_0x0088:
            int r13 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r13 >= r15) goto L_0x008f
            r14 = 1
        L_0x008f:
            r0.inPurgeable = r14
            if (r11 == 0) goto L_0x0095
            r13 = r11
            goto L_0x009d
        L_0x0095:
            if (r12 == 0) goto L_0x009c
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.getPath(r12)
            goto L_0x009d
        L_0x009c:
            r13 = r2
        L_0x009d:
            if (r13 == 0) goto L_0x00ce
            androidx.exifinterface.media.ExifInterface r14 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00ce }
            r14.<init>((java.lang.String) r13)     // Catch:{ all -> 0x00ce }
            java.lang.String r13 = "Orientation"
            int r13 = r14.getAttributeInt(r13, r1)     // Catch:{ all -> 0x00ce }
            android.graphics.Matrix r14 = new android.graphics.Matrix     // Catch:{ all -> 0x00ce }
            r14.<init>()     // Catch:{ all -> 0x00ce }
            r15 = 3
            if (r13 == r15) goto L_0x00c6
            r15 = 6
            if (r13 == r15) goto L_0x00c0
            r15 = 8
            if (r13 == r15) goto L_0x00ba
            goto L_0x00cf
        L_0x00ba:
            r13 = 1132920832(0x43870000, float:270.0)
            r14.postRotate(r13)     // Catch:{ all -> 0x00cc }
            goto L_0x00cf
        L_0x00c0:
            r13 = 1119092736(0x42b40000, float:90.0)
            r14.postRotate(r13)     // Catch:{ all -> 0x00cc }
            goto L_0x00cf
        L_0x00c6:
            r13 = 1127481344(0x43340000, float:180.0)
            r14.postRotate(r13)     // Catch:{ all -> 0x00cc }
            goto L_0x00cf
        L_0x00cc:
            goto L_0x00cf
        L_0x00ce:
            r14 = r2
        L_0x00cf:
            if (r11 == 0) goto L_0x013a
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x00fc }
            if (r12 == 0) goto L_0x00f9
            boolean r13 = r0.inPurgeable     // Catch:{ all -> 0x00f7 }
            if (r13 == 0) goto L_0x00de
            org.telegram.messenger.Utilities.pinBitmap(r12)     // Catch:{ all -> 0x00f7 }
        L_0x00de:
            r5 = 0
            r6 = 0
            int r7 = r12.getWidth()     // Catch:{ all -> 0x00f7 }
            int r8 = r12.getHeight()     // Catch:{ all -> 0x00f7 }
            r10 = 1
            r4 = r12
            r9 = r14
            android.graphics.Bitmap r13 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x00f7 }
            if (r13 == r12) goto L_0x00f9
            r12.recycle()     // Catch:{ all -> 0x00f7 }
            r2 = r13
            goto L_0x0180
        L_0x00f7:
            r13 = move-exception
            goto L_0x00fe
        L_0x00f9:
            r2 = r12
            goto L_0x0180
        L_0x00fc:
            r13 = move-exception
            r12 = r2
        L_0x00fe:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            org.telegram.messenger.ImageLoader r13 = getInstance()
            r13.clearMemory()
            if (r12 != 0) goto L_0x011a
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x0118 }
            if (r12 == 0) goto L_0x011a
            boolean r11 = r0.inPurgeable     // Catch:{ all -> 0x0118 }
            if (r11 == 0) goto L_0x011a
            org.telegram.messenger.Utilities.pinBitmap(r12)     // Catch:{ all -> 0x0118 }
            goto L_0x011a
        L_0x0118:
            r11 = move-exception
            goto L_0x0133
        L_0x011a:
            if (r12 == 0) goto L_0x0137
            r5 = 0
            r6 = 0
            int r7 = r12.getWidth()     // Catch:{ all -> 0x0118 }
            int r8 = r12.getHeight()     // Catch:{ all -> 0x0118 }
            r10 = 1
            r4 = r12
            r9 = r14
            android.graphics.Bitmap r11 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0118 }
            if (r11 == r12) goto L_0x0137
            r12.recycle()     // Catch:{ all -> 0x0118 }
            goto L_0x0138
        L_0x0133:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x00f9
        L_0x0137:
            r11 = r12
        L_0x0138:
            r2 = r11
            goto L_0x0180
        L_0x013a:
            if (r12 == 0) goto L_0x0180
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeStream(r3, r2, r0)     // Catch:{ all -> 0x016e }
            if (r11 == 0) goto L_0x0164
            boolean r12 = r0.inPurgeable     // Catch:{ all -> 0x0161 }
            if (r12 == 0) goto L_0x0149
            org.telegram.messenger.Utilities.pinBitmap(r11)     // Catch:{ all -> 0x0161 }
        L_0x0149:
            r5 = 0
            r6 = 0
            int r7 = r11.getWidth()     // Catch:{ all -> 0x0161 }
            int r8 = r11.getHeight()     // Catch:{ all -> 0x0161 }
            r10 = 1
            r4 = r11
            r9 = r14
            android.graphics.Bitmap r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0161 }
            if (r12 == r11) goto L_0x0164
            r11.recycle()     // Catch:{ all -> 0x0161 }
            r2 = r12
            goto L_0x0165
        L_0x0161:
            r12 = move-exception
            r2 = r11
            goto L_0x016f
        L_0x0164:
            r2 = r11
        L_0x0165:
            r3.close()     // Catch:{ all -> 0x0169 }
            goto L_0x0180
        L_0x0169:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x0180
        L_0x016e:
            r12 = move-exception
        L_0x016f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ all -> 0x0176 }
            r3.close()     // Catch:{ all -> 0x0169 }
            goto L_0x0180
        L_0x0176:
            r11 = move-exception
            r3.close()     // Catch:{ all -> 0x017b }
            goto L_0x017f
        L_0x017b:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x017f:
            throw r11
        L_0x0180:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(TLRPC.PhotoSize photoSize) {
        if (photoSize != null) {
            byte[] bArr = photoSize.bytes;
            if (bArr == null || bArr.length == 0) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
                    if (((int) randomAccessFile.length()) < 20000) {
                        photoSize.bytes = new byte[((int) randomAccessFile.length())];
                        randomAccessFile.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.tgnet.TLRPC.PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC.PhotoSize r3, android.graphics.Bitmap r4, int r5, int r6, float r7, float r8, float r9, int r10, boolean r11, boolean r12, boolean r13) throws java.lang.Exception {
        /*
            r7 = 1065353216(0x3var_, float:1.0)
            int r7 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r7 > 0) goto L_0x000b
            if (r12 == 0) goto L_0x0009
            goto L_0x000b
        L_0x0009:
            r5 = r4
            goto L_0x0010
        L_0x000b:
            r7 = 1
            android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r5, r6, r7)
        L_0x0010:
            r6 = 0
            r7 = -2147483648(0xfffffffvar_, double:NaN)
            if (r3 == 0) goto L_0x0020
            org.telegram.tgnet.TLRPC$FileLocation r9 = r3.location
            boolean r12 = r9 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated
            if (r12 != 0) goto L_0x001d
            goto L_0x0020
        L_0x001d:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r9 = (org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated) r9
            goto L_0x008b
        L_0x0020:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r9 = new org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            r9.<init>()
            r9.volume_id = r7
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
            r9.dc_id = r3
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r9.local_id = r3
            byte[] r3 = new byte[r6]
            r9.file_reference = r3
            org.telegram.tgnet.TLRPC$TL_photoSize r3 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r3.<init>()
            r3.location = r9
            int r12 = r5.getWidth()
            r3.w = r12
            int r12 = r5.getHeight()
            r3.h = r12
            int r12 = r3.w
            r0 = 100
            if (r12 > r0) goto L_0x0057
            int r12 = r3.h
            if (r12 > r0) goto L_0x0057
            java.lang.String r12 = "s"
            r3.type = r12
            goto L_0x008b
        L_0x0057:
            int r12 = r3.w
            r0 = 320(0x140, float:4.48E-43)
            if (r12 > r0) goto L_0x0066
            int r12 = r3.h
            if (r12 > r0) goto L_0x0066
            java.lang.String r12 = "m"
            r3.type = r12
            goto L_0x008b
        L_0x0066:
            int r12 = r3.w
            r0 = 800(0x320, float:1.121E-42)
            if (r12 > r0) goto L_0x0076
            int r12 = r3.h
            if (r12 > r0) goto L_0x0076
            java.lang.String r12 = "x"
            r3.type = r12
            goto L_0x008b
        L_0x0076:
            int r12 = r3.w
            r0 = 1280(0x500, float:1.794E-42)
            if (r12 > r0) goto L_0x0086
            int r12 = r3.h
            if (r12 > r0) goto L_0x0086
            java.lang.String r12 = "y"
            r3.type = r12
            goto L_0x008b
        L_0x0086:
            java.lang.String r12 = "w"
            r3.type = r12
        L_0x008b:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            long r0 = r9.volume_id
            r12.append(r0)
            java.lang.String r0 = "_"
            r12.append(r0)
            int r0 = r9.local_id
            r12.append(r0)
            java.lang.String r0 = ".jpg"
            r12.append(r0)
            java.lang.String r12 = r12.toString()
            r0 = 4
            if (r13 == 0) goto L_0x00b0
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r0)
            goto L_0x00bf
        L_0x00b0:
            long r1 = r9.volume_id
            int r9 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x00bb
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)
            goto L_0x00bf
        L_0x00bb:
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r0)
        L_0x00bf:
            java.io.File r7 = new java.io.File
            r7.<init>(r6, r12)
            java.io.FileOutputStream r6 = new java.io.FileOutputStream
            r6.<init>(r7)
            android.graphics.Bitmap$CompressFormat r7 = android.graphics.Bitmap.CompressFormat.JPEG
            r5.compress(r7, r10, r6)
            if (r11 == 0) goto L_0x00e9
            java.io.ByteArrayOutputStream r7 = new java.io.ByteArrayOutputStream
            r7.<init>()
            android.graphics.Bitmap$CompressFormat r8 = android.graphics.Bitmap.CompressFormat.JPEG
            r5.compress(r8, r10, r7)
            byte[] r8 = r7.toByteArray()
            r3.bytes = r8
            byte[] r8 = r3.bytes
            int r8 = r8.length
            r3.size = r8
            r7.close()
            goto L_0x00f4
        L_0x00e9:
            java.nio.channels.FileChannel r7 = r6.getChannel()
            long r7 = r7.size()
            int r8 = (int) r7
            r3.size = r8
        L_0x00f4:
            r6.close()
            if (r5 == r4) goto L_0x00fc
            r5.recycle()
        L_0x00fc:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap, int, int, float, float, float, int, boolean, boolean, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, f, f2, i, z, 0, 0, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(TLRPC.PhotoSize photoSize, Bitmap bitmap, float f, float f2, int i, boolean z, boolean z2) {
        return scaleAndSaveImage(photoSize, bitmap, f, f2, i, z, 0, 0, z2);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(TLRPC.PhotoSize photoSize, Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3, boolean z2) {
        boolean z3;
        float f3;
        int i4;
        int i5;
        float f4;
        int i6 = i2;
        int i7 = i3;
        if (bitmap == null) {
            return null;
        }
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        if (!(width == 0.0f || height == 0.0f)) {
            float max = Math.max(width / f, height / f2);
            if (!(i6 == 0 || i7 == 0)) {
                float f5 = (float) i6;
                if (width < f5 || height < ((float) i7)) {
                    if (width >= f5 || height <= ((float) i7)) {
                        if (width > f5) {
                            float f6 = (float) i7;
                            if (height < f6) {
                                f4 = height / f6;
                            }
                        }
                        f4 = Math.max(width / f5, height / ((float) i7));
                    } else {
                        f4 = width / f5;
                    }
                    f3 = f4;
                    z3 = true;
                    i4 = (int) (width / f3);
                    i5 = (int) (height / f3);
                    if (!(i5 == 0 || i4 == 0)) {
                        int i8 = i5;
                        int i9 = i4;
                        return scaleAndSaveImageInternal(photoSize, bitmap, i4, i5, width, height, f3, i, z, z3, z2);
                    }
                }
            }
            f3 = max;
            z3 = false;
            i4 = (int) (width / f3);
            i5 = (int) (height / f3);
            int i82 = i5;
            int i92 = i4;
            try {
                return scaleAndSaveImageInternal(photoSize, bitmap, i4, i5, width, height, f3, i, z, z3, z2);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return null;
    }

    public static String getHttpUrlExtension(String str, String str2) {
        String lastPathSegment = Uri.parse(str).getLastPathSegment();
        if (!TextUtils.isEmpty(lastPathSegment) && lastPathSegment.length() > 1) {
            str = lastPathSegment;
        }
        int lastIndexOf = str.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : null;
        return (substring == null || substring.length() == 0 || substring.length() > 4) ? str2 : substring;
    }

    public static void saveMessageThumbs(TLRPC.Message message) {
        TLRPC.PhotoSize findPhotoCachedSize;
        byte[] bArr;
        if (message.media != null && (findPhotoCachedSize = findPhotoCachedSize(message)) != null && (bArr = findPhotoCachedSize.bytes) != null && bArr.length != 0) {
            TLRPC.FileLocation fileLocation = findPhotoCachedSize.location;
            if (fileLocation == null || (fileLocation instanceof TLRPC.TL_fileLocationUnavailable)) {
                findPhotoCachedSize.location = new TLRPC.TL_fileLocationToBeDeprecated();
                TLRPC.FileLocation fileLocation2 = findPhotoCachedSize.location;
                fileLocation2.volume_id = -2147483648L;
                fileLocation2.local_id = SharedConfig.getLastLocalId();
            }
            boolean z = true;
            File pathToAttach = FileLoader.getPathToAttach(findPhotoCachedSize, true);
            int i = 0;
            if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                pathToAttach = new File(pathToAttach.getAbsolutePath() + ".enc");
            } else {
                z = false;
            }
            if (!pathToAttach.exists()) {
                if (z) {
                    try {
                        File internalCacheDir = FileLoader.getInternalCacheDir();
                        RandomAccessFile randomAccessFile = new RandomAccessFile(new File(internalCacheDir, pathToAttach.getName() + ".key"), "rws");
                        long length = randomAccessFile.length();
                        byte[] bArr2 = new byte[32];
                        byte[] bArr3 = new byte[16];
                        if (length <= 0 || length % 48 != 0) {
                            Utilities.random.nextBytes(bArr2);
                            Utilities.random.nextBytes(bArr3);
                            randomAccessFile.write(bArr2);
                            randomAccessFile.write(bArr3);
                        } else {
                            randomAccessFile.read(bArr2, 0, 32);
                            randomAccessFile.read(bArr3, 0, 16);
                        }
                        randomAccessFile.close();
                        Utilities.aesCtrDecryptionByteArray(findPhotoCachedSize.bytes, bArr2, bArr3, 0, findPhotoCachedSize.bytes.length, 0);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                RandomAccessFile randomAccessFile2 = new RandomAccessFile(pathToAttach, "rws");
                randomAccessFile2.write(findPhotoCachedSize.bytes);
                randomAccessFile2.close();
            }
            TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
            tL_photoSize.w = findPhotoCachedSize.w;
            tL_photoSize.h = findPhotoCachedSize.h;
            tL_photoSize.location = findPhotoCachedSize.location;
            tL_photoSize.size = findPhotoCachedSize.size;
            tL_photoSize.type = findPhotoCachedSize.type;
            TLRPC.MessageMedia messageMedia = message.media;
            if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                int size = messageMedia.photo.sizes.size();
                while (i < size) {
                    if (message.media.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize) {
                        message.media.photo.sizes.set(i, tL_photoSize);
                        return;
                    }
                    i++;
                }
            } else if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
                int size2 = messageMedia.document.thumbs.size();
                while (i < size2) {
                    if (message.media.document.thumbs.get(i) instanceof TLRPC.TL_photoCachedSize) {
                        message.media.document.thumbs.set(i, tL_photoSize);
                        return;
                    }
                    i++;
                }
            } else if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
                int size3 = messageMedia.webpage.photo.sizes.size();
                while (i < size3) {
                    if (message.media.webpage.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize) {
                        message.media.webpage.photo.sizes.set(i, tL_photoSize);
                        return;
                    }
                    i++;
                }
            }
        }
    }

    private static TLRPC.PhotoSize findPhotoCachedSize(TLRPC.Message message) {
        TLRPC.PhotoSize photoSize;
        TLRPC.Photo photo;
        TLRPC.MessageMedia messageMedia = message.media;
        int i = 0;
        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            int size = messageMedia.photo.sizes.size();
            while (i < size) {
                photoSize = message.media.photo.sizes.get(i);
                if (!(photoSize instanceof TLRPC.TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            int size2 = messageMedia.document.thumbs.size();
            while (i < size2) {
                photoSize = message.media.document.thumbs.get(i);
                if (!(photoSize instanceof TLRPC.TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (!(messageMedia instanceof TLRPC.TL_messageMediaWebPage) || (photo = messageMedia.webpage.photo) == null) {
            return null;
        } else {
            int size3 = photo.sizes.size();
            while (i < size3) {
                photoSize = message.media.webpage.photo.sizes.get(i);
                if (!(photoSize instanceof TLRPC.TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        }
        return photoSize;
    }

    public static void saveMessagesThumbs(ArrayList<TLRPC.Message> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                saveMessageThumbs(arrayList.get(i));
            }
        }
    }

    public static MessageThumb generateMessageThumb(TLRPC.Message message) {
        int i;
        int i2;
        Bitmap strippedPhotoBitmap;
        byte[] bArr;
        TLRPC.Message message2 = message;
        TLRPC.PhotoSize findPhotoCachedSize = findPhotoCachedSize(message);
        if (findPhotoCachedSize == null || (bArr = findPhotoCachedSize.bytes) == null || bArr.length == 0) {
            TLRPC.MessageMedia messageMedia = message2.media;
            if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
                int size = messageMedia.document.thumbs.size();
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC.PhotoSize photoSize = message2.media.document.thumbs.get(i3);
                    if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(message2.media.document.thumbs, 320);
                        if (closestPhotoSizeWithSize == null) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= message2.media.document.attributes.size()) {
                                    i2 = 0;
                                    i = 0;
                                    break;
                                } else if (message2.media.document.attributes.get(i4) instanceof TLRPC.TL_documentAttributeVideo) {
                                    TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo = (TLRPC.TL_documentAttributeVideo) message2.media.document.attributes.get(i4);
                                    i = tL_documentAttributeVideo.h;
                                    i2 = tL_documentAttributeVideo.w;
                                    break;
                                } else {
                                    i4++;
                                }
                            }
                        } else {
                            i = closestPhotoSizeWithSize.h;
                            i2 = closestPhotoSizeWithSize.w;
                        }
                        Point messageSize = ChatMessageCell.getMessageSize(i2, i);
                        String format = String.format(Locale.US, "%s_false@%d_%d_b", new Object[]{ImageLocation.getStippedKey(message2, message2, photoSize), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
                        if (!getInstance().memCache.contains(format) && (strippedPhotoBitmap = getStrippedPhotoBitmap(photoSize.bytes, (String) null)) != null) {
                            Utilities.blurBitmap(strippedPhotoBitmap, 3, 1, strippedPhotoBitmap.getWidth(), strippedPhotoBitmap.getHeight(), strippedPhotoBitmap.getRowBytes());
                            float f = messageSize.x;
                            float f2 = AndroidUtilities.density;
                            Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(strippedPhotoBitmap, (int) (f / f2), (int) (messageSize.y / f2), true);
                            if (createScaledBitmap != strippedPhotoBitmap) {
                                strippedPhotoBitmap.recycle();
                            } else {
                                createScaledBitmap = strippedPhotoBitmap;
                            }
                            return new MessageThumb(format, new BitmapDrawable(createScaledBitmap));
                        }
                    }
                }
            }
        } else {
            File pathToAttach = FileLoader.getPathToAttach(findPhotoCachedSize, true);
            TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
            tL_photoSize.w = findPhotoCachedSize.w;
            tL_photoSize.h = findPhotoCachedSize.h;
            tL_photoSize.location = findPhotoCachedSize.location;
            tL_photoSize.size = findPhotoCachedSize.size;
            tL_photoSize.type = findPhotoCachedSize.type;
            if (pathToAttach.exists() && message2.grouped_id == 0) {
                Point messageSize2 = ChatMessageCell.getMessageSize(findPhotoCachedSize.w, findPhotoCachedSize.h);
                String format2 = String.format(Locale.US, "%d_%d@%d_%d_b", new Object[]{Long.valueOf(findPhotoCachedSize.location.volume_id), Integer.valueOf(findPhotoCachedSize.location.local_id), Integer.valueOf((int) (messageSize2.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize2.y / AndroidUtilities.density))});
                if (!getInstance().isInMemCache(format2, false)) {
                    String path = pathToAttach.getPath();
                    float f3 = messageSize2.x;
                    float f4 = AndroidUtilities.density;
                    Bitmap loadBitmap = loadBitmap(path, (Uri) null, (float) ((int) (f3 / f4)), (float) ((int) (messageSize2.y / f4)), false);
                    if (loadBitmap != null) {
                        Utilities.blurBitmap(loadBitmap, 3, 1, loadBitmap.getWidth(), loadBitmap.getHeight(), loadBitmap.getRowBytes());
                        float f5 = messageSize2.x;
                        float f6 = AndroidUtilities.density;
                        Bitmap createScaledBitmap2 = Bitmaps.createScaledBitmap(loadBitmap, (int) (f5 / f6), (int) (messageSize2.y / f6), true);
                        if (createScaledBitmap2 != loadBitmap) {
                            loadBitmap.recycle();
                        } else {
                            createScaledBitmap2 = loadBitmap;
                        }
                        return new MessageThumb(format2, new BitmapDrawable(createScaledBitmap2));
                    }
                }
            }
        }
        return null;
    }

    public static class MessageThumb {
        BitmapDrawable drawable;
        String key;

        public MessageThumb(String str, BitmapDrawable bitmapDrawable) {
            this.key = str;
            this.drawable = bitmapDrawable;
        }
    }
}
